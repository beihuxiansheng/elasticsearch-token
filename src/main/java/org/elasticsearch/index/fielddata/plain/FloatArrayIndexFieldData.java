begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|DocValues
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
name|LeafReader
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
name|LeafReaderContext
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
name|RandomAccessOrds
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|Accountable
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
name|Accountables
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
name|BitSet
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
name|Bits
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
name|BytesRef
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
name|BytesRefIterator
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
name|NumericUtils
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
name|RamUsageEstimator
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
name|breaker
operator|.
name|CircuitBreaker
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
name|BigArrays
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
name|FloatArray
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
name|AtomicNumericFieldData
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
name|FieldData
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
name|FieldDataType
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
name|IndexFieldData
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
name|IndexFieldData
operator|.
name|XFieldComparatorSource
operator|.
name|Nested
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
name|IndexFieldDataCache
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
name|IndexNumericFieldData
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
name|NumericDoubleValues
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
name|SortedNumericDoubleValues
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
name|mapper
operator|.
name|MapperService
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
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|MultiValueMode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|AtomicNumericFieldData
argument_list|>
implements|implements
name|IndexNumericFieldData
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
name|mapper
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|,
name|MapperService
name|mapperService
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
DECL|method|loadDirect
specifier|public
name|AtomicNumericFieldData
name|loadDirect
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|LeafReader
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
name|AtomicNumericFieldData
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
argument_list|(
name|CircuitBreaker
operator|.
name|FIELDDATA
argument_list|)
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
name|AtomicDoubleFieldData
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
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|// TODO: how can we guess the number of terms? numerics end up creating more terms per value...
name|FloatArray
name|values
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newFloatArray
argument_list|(
literal|128
argument_list|)
decl_stmt|;
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
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
init|)
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
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|term
decl_stmt|;
name|long
name|numTerms
init|=
literal|0
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
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|grow
argument_list|(
name|values
argument_list|,
name|numTerms
operator|+
literal|1
argument_list|)
expr_stmt|;
name|values
operator|.
name|set
argument_list|(
name|numTerms
operator|++
argument_list|,
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
name|values
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|resize
argument_list|(
name|values
argument_list|,
name|numTerms
argument_list|)
expr_stmt|;
specifier|final
name|FloatArray
name|finalValues
init|=
name|values
decl_stmt|;
specifier|final
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
name|RandomAccessOrds
name|ordinals
init|=
name|build
operator|.
name|ordinals
argument_list|()
decl_stmt|;
if|if
condition|(
name|FieldData
operator|.
name|isMultiValued
argument_list|(
name|ordinals
argument_list|)
operator|||
name|CommonSettings
operator|.
name|getMemoryStorageHint
argument_list|(
name|fieldDataType
argument_list|)
operator|==
name|CommonSettings
operator|.
name|MemoryStorageFormat
operator|.
name|ORDINALS
condition|)
block|{
specifier|final
name|long
name|ramBytesUsed
init|=
name|build
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|values
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|data
operator|=
operator|new
name|AtomicDoubleFieldData
argument_list|(
name|ramBytesUsed
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SortedNumericDoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
name|withOrdinals
argument_list|(
name|build
argument_list|,
name|finalValues
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"ordinals"
argument_list|,
name|build
argument_list|)
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"values"
argument_list|,
name|finalValues
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BitSet
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
name|set
operator|.
name|ramBytesUsed
argument_list|()
operator|)
decl_stmt|;
name|long
name|uniqueValuesArraySize
init|=
name|values
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|long
name|ordinalsSize
init|=
name|build
operator|.
name|ramBytesUsed
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
specifier|final
name|long
name|ramBytesUsed
init|=
name|build
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|values
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|data
operator|=
operator|new
name|AtomicDoubleFieldData
argument_list|(
name|ramBytesUsed
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SortedNumericDoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
name|withOrdinals
argument_list|(
name|build
argument_list|,
name|finalValues
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"ordinals"
argument_list|,
name|build
argument_list|)
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"values"
argument_list|,
name|finalValues
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
block|}
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
specifier|final
name|FloatArray
name|sValues
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newFloatArray
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
name|ordinals
operator|.
name|setDocument
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|long
name|ordinal
init|=
name|ordinals
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|!=
name|SortedSetDocValues
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|sValues
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|values
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|sValues
operator|.
name|size
argument_list|()
operator|==
name|maxDoc
assert|;
specifier|final
name|long
name|ramBytesUsed
init|=
name|sValues
operator|.
name|ramBytesUsed
argument_list|()
operator|+
operator|(
name|set
operator|==
literal|null
condition|?
literal|0
else|:
name|set
operator|.
name|ramBytesUsed
argument_list|()
operator|)
decl_stmt|;
name|data
operator|=
operator|new
name|AtomicDoubleFieldData
argument_list|(
name|ramBytesUsed
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|SortedNumericDoubleValues
name|getDoubleValues
parameter_list|()
block|{
return|return
name|singles
argument_list|(
name|sValues
argument_list|,
name|set
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
name|List
argument_list|<
name|Accountable
argument_list|>
name|resources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"values"
argument_list|,
name|sValues
argument_list|)
argument_list|)
expr_stmt|;
name|resources
operator|.
name|add
argument_list|(
name|Accountables
operator|.
name|namedAccountable
argument_list|(
literal|"missing bitset"
argument_list|,
name|set
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|resources
argument_list|)
return|;
block|}
block|}
expr_stmt|;
name|success
operator|=
literal|true
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
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|MultiValueMode
name|sortMode
parameter_list|,
name|Nested
name|nested
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
argument_list|,
name|nested
argument_list|)
return|;
block|}
DECL|method|withOrdinals
specifier|private
specifier|static
name|SortedNumericDoubleValues
name|withOrdinals
parameter_list|(
name|Ordinals
name|ordinals
parameter_list|,
specifier|final
name|FloatArray
name|values
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
specifier|final
name|RandomAccessOrds
name|ords
init|=
name|ordinals
operator|.
name|ordinals
argument_list|()
decl_stmt|;
specifier|final
name|SortedDocValues
name|singleOrds
init|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|ords
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleOrds
operator|!=
literal|null
condition|)
block|{
specifier|final
name|NumericDoubleValues
name|singleValues
init|=
operator|new
name|NumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
specifier|final
name|int
name|ord
init|=
name|singleOrds
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|>=
literal|0
condition|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|singleOrds
operator|.
name|getOrd
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
decl_stmt|;
return|return
name|FieldData
operator|.
name|singleton
argument_list|(
name|singleValues
argument_list|,
name|DocValues
operator|.
name|docsWithValue
argument_list|(
name|ords
argument_list|,
name|maxDoc
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|SortedNumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|ords
operator|.
name|ordAt
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|ords
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|ords
operator|.
name|cardinality
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
DECL|method|singles
specifier|private
specifier|static
name|SortedNumericDoubleValues
name|singles
parameter_list|(
specifier|final
name|FloatArray
name|values
parameter_list|,
name|Bits
name|set
parameter_list|)
block|{
specifier|final
name|NumericDoubleValues
name|numValues
init|=
operator|new
name|NumericDoubleValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|double
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|values
operator|.
name|get
argument_list|(
name|docID
argument_list|)
return|;
block|}
block|}
decl_stmt|;
return|return
name|FieldData
operator|.
name|singleton
argument_list|(
name|numValues
argument_list|,
name|set
argument_list|)
return|;
block|}
block|}
end_class

end_unit

