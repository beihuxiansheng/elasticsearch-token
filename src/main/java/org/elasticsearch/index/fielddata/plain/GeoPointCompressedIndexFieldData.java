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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
operator|.
name|PackedInts
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
name|packed
operator|.
name|PagedMutable
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
name|unit
operator|.
name|DistanceUnit
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
name|unit
operator|.
name|DistanceUnit
operator|.
name|Distance
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
name|mapper
operator|.
name|geo
operator|.
name|GeoPointFieldMapper
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|GeoPointCompressedIndexFieldData
specifier|public
class|class
name|GeoPointCompressedIndexFieldData
extends|extends
name|AbstractIndexGeoPointFieldData
block|{
DECL|field|PRECISION_KEY
specifier|private
specifier|static
specifier|final
name|String
name|PRECISION_KEY
init|=
literal|"precision"
decl_stmt|;
DECL|field|DEFAULT_PRECISION_VALUE
specifier|private
specifier|static
specifier|final
name|Distance
name|DEFAULT_PRECISION_VALUE
init|=
operator|new
name|Distance
argument_list|(
literal|1
argument_list|,
name|DistanceUnit
operator|.
name|CENTIMETERS
argument_list|)
decl_stmt|;
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
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
name|FieldDataType
name|type
init|=
name|mapper
operator|.
name|fieldDataType
argument_list|()
decl_stmt|;
specifier|final
name|String
name|precisionAsString
init|=
name|type
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|PRECISION_KEY
argument_list|)
decl_stmt|;
specifier|final
name|Distance
name|precision
decl_stmt|;
if|if
condition|(
name|precisionAsString
operator|!=
literal|null
condition|)
block|{
name|precision
operator|=
name|Distance
operator|.
name|parseDistance
argument_list|(
name|precisionAsString
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|precision
operator|=
name|DEFAULT_PRECISION_VALUE
expr_stmt|;
block|}
return|return
operator|new
name|GeoPointCompressedIndexFieldData
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
name|precision
argument_list|,
name|breakerService
argument_list|)
return|;
block|}
block|}
DECL|field|encoding
specifier|private
specifier|final
name|GeoPointFieldMapper
operator|.
name|Encoding
name|encoding
decl_stmt|;
DECL|method|GeoPointCompressedIndexFieldData
specifier|public
name|GeoPointCompressedIndexFieldData
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
name|Distance
name|precision
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
name|encoding
operator|=
name|GeoPointFieldMapper
operator|.
name|Encoding
operator|.
name|of
argument_list|(
name|precision
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
specifier|final
name|long
name|initialSize
decl_stmt|;
if|if
condition|(
name|terms
operator|.
name|size
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|initialSize
operator|=
literal|1
operator|+
name|terms
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// codec doesn't expose size
name|initialSize
operator|=
literal|1
operator|+
name|Math
operator|.
name|min
argument_list|(
literal|1
operator|<<
literal|12
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|pageSize
init|=
name|Integer
operator|.
name|highestOneBit
argument_list|(
name|BigArrays
operator|.
name|PAGE_SIZE_IN_BYTES
operator|*
literal|8
operator|/
name|encoding
operator|.
name|numBitsPerCoordinate
argument_list|()
operator|-
literal|1
argument_list|)
operator|<<
literal|1
decl_stmt|;
name|PagedMutable
name|lat
init|=
operator|new
name|PagedMutable
argument_list|(
name|initialSize
argument_list|,
name|pageSize
argument_list|,
name|encoding
operator|.
name|numBitsPerCoordinate
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|PagedMutable
name|lon
init|=
operator|new
name|PagedMutable
argument_list|(
name|initialSize
argument_list|,
name|pageSize
argument_list|,
name|encoding
operator|.
name|numBitsPerCoordinate
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|COMPACT
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
specifier|final
name|long
name|ord
init|=
name|builder
operator|.
name|currentOrdinal
argument_list|()
decl_stmt|;
if|if
condition|(
name|lat
operator|.
name|size
argument_list|()
operator|<=
name|ord
condition|)
block|{
specifier|final
name|long
name|newSize
init|=
name|BigArrays
operator|.
name|overSize
argument_list|(
name|ord
operator|+
literal|1
argument_list|)
decl_stmt|;
name|lat
operator|=
name|lat
operator|.
name|resize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
name|lon
operator|=
name|lon
operator|.
name|resize
argument_list|(
name|newSize
argument_list|)
expr_stmt|;
block|}
name|lat
operator|.
name|set
argument_list|(
name|ord
argument_list|,
name|encoding
operator|.
name|encodeCoordinate
argument_list|(
name|point
operator|.
name|getLat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lon
operator|.
name|set
argument_list|(
name|ord
argument_list|,
name|encoding
operator|.
name|encodeCoordinate
argument_list|(
name|point
operator|.
name|getLon
argument_list|()
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
if|if
condition|(
name|lat
operator|.
name|size
argument_list|()
operator|!=
name|ordinals
operator|.
name|getValueCount
argument_list|()
condition|)
block|{
name|lat
operator|=
name|lat
operator|.
name|resize
argument_list|(
name|ordinals
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
name|lon
operator|=
name|lon
operator|.
name|resize
argument_list|(
name|ordinals
operator|.
name|getValueCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|data
operator|=
operator|new
name|GeoPointCompressedAtomicFieldData
operator|.
name|WithOrdinals
argument_list|(
name|encoding
argument_list|,
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
else|else
block|{
name|int
name|maxDoc
init|=
name|reader
operator|.
name|maxDoc
argument_list|()
decl_stmt|;
name|PagedMutable
name|sLat
init|=
operator|new
name|PagedMutable
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|pageSize
argument_list|,
name|encoding
operator|.
name|numBitsPerCoordinate
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|PagedMutable
name|sLon
init|=
operator|new
name|PagedMutable
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|pageSize
argument_list|,
name|encoding
operator|.
name|numBitsPerCoordinate
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
specifier|final
name|long
name|missing
init|=
name|encoding
operator|.
name|encodeCoordinate
argument_list|(
literal|0
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
operator|>=
literal|0
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
else|else
block|{
name|sLat
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|missing
argument_list|)
expr_stmt|;
name|sLon
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|missing
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
name|GeoPointCompressedAtomicFieldData
operator|.
name|Single
argument_list|(
name|encoding
argument_list|,
name|sLon
argument_list|,
name|sLat
argument_list|,
name|set
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

