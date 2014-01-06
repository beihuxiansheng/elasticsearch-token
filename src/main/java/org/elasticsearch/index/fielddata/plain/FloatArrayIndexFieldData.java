begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|AtomicReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Terms
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|BigFloatArrayList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|FloatValuesComparatorSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|fieldcomparator
operator|.
name|SortMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|Ordinals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|Ordinals
operator|.
name|Docs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|OrdinalsBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|FieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FloatArrayIndexFieldData
specifier|public
class|class
name|FloatArrayIndexFieldData
extends|extends
name|AbstractIndexFieldData
argument_list|<
name|FloatArrayAtomicFieldData
argument_list|>
implements|implements
name|IndexNumericFieldData
argument_list|<
name|FloatArrayAtomicFieldData
argument_list|>
block|{
DECL|field|breakerService
specifier|private
specifier|final
name|CircuitBreakerService
name|breakerService
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
implements|implements
name|IndexFieldData
operator|.
name|Builder
block|{
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexFieldData
argument_list|<
name|?
argument_list|>
name|build
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
block|{
return|return
operator|new
name|FloatArrayIndexFieldData
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|mapper
operator|.
name|names
argument_list|()
argument_list|,
name|mapper
operator|.
name|fieldDataType
argument_list|()
argument_list|,
name|cache
argument_list|,
name|breakerService
argument_list|)
return|;
block|}
block|}
DECL|method|FloatArrayIndexFieldData
specifier|public
name|FloatArrayIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|cache
argument_list|)
expr_stmt|;
name|this
operator|.
name|breakerService
operator|=
name|breakerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumericType
specifier|public
name|NumericType
name|getNumericType
parameter_list|()
block|{
return|return
name|NumericType
operator|.
name|FLOAT
return|;
block|}
annotation|@
name|Override
DECL|method|valuesOrdered
specifier|public
name|boolean
name|valuesOrdered
parameter_list|()
block|{
comment|// because we might have single values? we can dynamically update a flag to reflect that
comment|// based on the atomic field data loaded
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|FloatArrayAtomicFieldData
name|loadDirect
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
name|FloatArrayAtomicFieldData
name|data
init|=
literal|null
decl_stmt|;
comment|// TODO: Use an actual estimator to estimate before loading.
name|NonEstimatingEstimator
name|estimator
init|=
operator|new
name|NonEstimatingEstimator
argument_list|(
name|breakerService
operator|.
name|getBreaker
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
name|data
operator|=
name|FloatArrayAtomicFieldData
operator|.
name|empty
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|estimator
operator|.
name|afterLoad
argument_list|(
literal|null
argument_list|,
name|data
operator|.
name|getMemorySizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|// TODO: how can we guess the number of terms? numerics end up creating more terms per value...
specifier|final
name|BigFloatArrayList
name|values
init|=
operator|new
name|BigFloatArrayList
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// first "t" indicates null value
specifier|final
name|float
name|acceptableTransientOverheadRatio
init|=
name|fieldDataType
operator|.
name|getSettings
argument_list|()
operator|.
name|getAsFloat
argument_list|(
literal|"acceptable_transient_overhead_ratio"
argument_list|,
name|OrdinalsBuilder
operator|.
name|DEFAULT_ACCEPTABLE_OVERHEAD_RATIO
argument_list|)
decl_stmt|;
name|OrdinalsBuilder
name|builder
init|=
operator|new
name|OrdinalsBuilder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptableTransientOverheadRatio
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|BytesRefIterator
name|iter
init|=
name|builder
operator|.
name|buildFromTerms
argument_list|(
name|getNumericType
argument_list|()
operator|.
name|wrapTermsEnum
argument_list|(
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
while|while
condition|(
operator|(
name|term
operator|=
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|NumericUtils
operator|.
name|sortableIntToFloat
argument_list|(
name|NumericUtils
operator|.
name|prefixCodedToInt
argument_list|(
name|term
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Ordinals
name|build
init|=
name|builder
operator|.
name|build
argument_list|(
name|fieldDataType
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|build
operator|.
name|isMultiValued
argument_list|()
operator|&&
name|CommonSettings
operator|.
name|removeOrdsOnSingleValue
argument_list|(
name|fieldDataType
argument_list|)
condition|)
block|{
name|Docs
name|ordinals
init|=
name|build
operator|.
name|ordinals
argument_list|()
decl_stmt|;
specifier|final
name|FixedBitSet
name|set
init|=
name|builder
operator|.
name|buildDocsWithValuesSet
argument_list|()
decl_stmt|;
comment|// there's sweet spot where due to low unique value count, using ordinals will consume less memory
name|long
name|singleValuesArraySize
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_FLOAT
operator|+
operator|(
name|set
operator|==
literal|null
condition|?
literal|0
else|:
name|RamUsageEstimator
operator|.
name|sizeOf
argument_list|(
name|set
operator|.
name|getBits
argument_list|()
argument_list|)
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
operator|)
decl_stmt|;
name|long
name|uniqueValuesArraySize
init|=
name|values
operator|.
name|sizeInBytes
argument_list|()
decl_stmt|;
name|long
name|ordinalsSize
init|=
name|build
operator|.
name|getMemorySizeInBytes
argument_list|()
decl_stmt|;
if|if
condition|(
name|uniqueValuesArraySize
operator|+
name|ordinalsSize
operator|<
name|singleValuesArraySize
condition|)
block|{
name|data
operator|=
operator|new
name|FloatArrayAtomicFieldData
operator|.
name|WithOrdinals
argument_list|(
name|values
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|build
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|data
return|;
block|}
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|BigFloatArrayList
name|sValues
init|=
operator|new
name|BigFloatArrayList
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDoc
condition|;
name|i
operator|++
control|)
block|{
name|sValues
operator|.
name|add
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|ordinals
operator|.
name|getOrd
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
name|sValues
operator|.
name|size
argument_list|()
operator|==
name|maxDoc
assert|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
name|data
operator|=
operator|new
name|FloatArrayAtomicFieldData
operator|.
name|Single
argument_list|(
name|sValues
argument_list|,
name|maxDoc
argument_list|,
name|ordinals
operator|.
name|getNumOrds
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
operator|new
name|FloatArrayAtomicFieldData
operator|.
name|SingleFixedSet
argument_list|(
name|sValues
argument_list|,
name|maxDoc
argument_list|,
name|set
argument_list|,
name|ordinals
operator|.
name|getNumOrds
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|data
operator|=
operator|new
name|FloatArrayAtomicFieldData
operator|.
name|WithOrdinals
argument_list|(
name|values
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|build
argument_list|)
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|data
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|estimator
operator|.
name|afterLoad
argument_list|(
literal|null
argument_list|,
name|data
operator|.
name|getMemorySizeInBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|comparatorSource
specifier|public
name|XFieldComparatorSource
name|comparatorSource
parameter_list|(
annotation|@
name|Nullable
name|Object
name|missingValue
parameter_list|,
name|SortMode
name|sortMode
parameter_list|)
block|{
return|return
operator|new
name|FloatValuesComparatorSource
argument_list|(
name|this
argument_list|,
name|missingValue
argument_list|,
name|sortMode
argument_list|)
return|;
block|}
block|}
end_class

end_unit

