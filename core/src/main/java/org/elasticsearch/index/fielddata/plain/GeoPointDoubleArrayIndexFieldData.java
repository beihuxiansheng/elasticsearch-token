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
name|BitSet
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
name|geo
operator|.
name|GeoPoint
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
name|DoubleArray
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
name|IndexSettings
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
name|MappedFieldType
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
name|indices
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
DECL|class|GeoPointDoubleArrayIndexFieldData
specifier|public
class|class
name|GeoPointDoubleArrayIndexFieldData
extends|extends
name|AbstractIndexGeoPointFieldData
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
name|IndexSettings
name|indexSettings
parameter_list|,
name|MappedFieldType
name|fieldType
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
name|GeoPointDoubleArrayIndexFieldData
argument_list|(
name|indexSettings
argument_list|,
name|fieldType
operator|.
name|names
argument_list|()
argument_list|,
name|fieldType
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
DECL|method|GeoPointDoubleArrayIndexFieldData
specifier|public
name|GeoPointDoubleArrayIndexFieldData
parameter_list|(
name|IndexSettings
name|indexSettings
parameter_list|,
name|MappedFieldType
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
DECL|method|loadDirect
specifier|public
name|AtomicGeoPointFieldData
name|loadDirect
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
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
name|AtomicGeoPointFieldData
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
name|AbstractAtomicGeoPointFieldData
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
name|DoubleArray
name|lat
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newDoubleArray
argument_list|(
literal|128
argument_list|)
decl_stmt|;
name|DoubleArray
name|lon
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newDoubleArray
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
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptableTransientOverheadRatio
argument_list|)
init|)
block|{
specifier|final
name|GeoPointEnum
name|iter
init|=
operator|new
name|GeoPointEnum
argument_list|(
name|builder
operator|.
name|buildFromTerms
argument_list|(
name|terms
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|GeoPoint
name|point
decl_stmt|;
name|long
name|numTerms
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|point
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
name|lat
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|resize
argument_list|(
name|lat
argument_list|,
name|numTerms
operator|+
literal|1
argument_list|)
expr_stmt|;
name|lon
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|resize
argument_list|(
name|lon
argument_list|,
name|numTerms
operator|+
literal|1
argument_list|)
expr_stmt|;
name|lat
operator|.
name|set
argument_list|(
name|numTerms
argument_list|,
name|point
operator|.
name|getLat
argument_list|()
argument_list|)
expr_stmt|;
name|lon
operator|.
name|set
argument_list|(
name|numTerms
argument_list|,
name|point
operator|.
name|getLon
argument_list|()
argument_list|)
expr_stmt|;
operator|++
name|numTerms
expr_stmt|;
block|}
name|lat
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|resize
argument_list|(
name|lat
argument_list|,
name|numTerms
argument_list|)
expr_stmt|;
name|lon
operator|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|resize
argument_list|(
name|lon
argument_list|,
name|numTerms
argument_list|)
expr_stmt|;
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
operator|!
operator|(
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
operator|)
condition|)
block|{
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|DoubleArray
name|sLat
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newDoubleArray
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|DoubleArray
name|sLon
init|=
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
operator|.
name|newDoubleArray
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
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
name|long
name|nativeOrdinal
init|=
name|ordinals
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|nativeOrdinal
operator|!=
name|RandomAccessOrds
operator|.
name|NO_MORE_ORDS
condition|)
block|{
name|sLat
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|lat
operator|.
name|get
argument_list|(
name|nativeOrdinal
argument_list|)
argument_list|)
expr_stmt|;
name|sLon
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|lon
operator|.
name|get
argument_list|(
name|nativeOrdinal
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|BitSet
name|set
init|=
name|builder
operator|.
name|buildDocsWithValuesSet
argument_list|()
decl_stmt|;
name|data
operator|=
operator|new
name|GeoPointDoubleArrayAtomicFieldData
operator|.
name|Single
argument_list|(
name|sLon
argument_list|,
name|sLat
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
operator|new
name|GeoPointDoubleArrayAtomicFieldData
operator|.
name|WithOrdinals
argument_list|(
name|lon
argument_list|,
name|lat
argument_list|,
name|build
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
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
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

