begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.ordinals
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
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
name|IndexReader
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
name|TermsEnum
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
name|PriorityQueue
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
name|AppendingPackedLongBuffer
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
name|MonotonicAppendingLongBuffer
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
name|index
operator|.
name|AbstractIndexComponent
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
name|AtomicFieldData
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Comparator
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
DECL|class|InternalGlobalOrdinalsBuilder
specifier|public
class|class
name|InternalGlobalOrdinalsBuilder
extends|extends
name|AbstractIndexComponent
implements|implements
name|GlobalOrdinalsBuilder
block|{
DECL|field|ORDINAL_MAPPING_THRESHOLD_DEFAULT
specifier|public
specifier|final
specifier|static
name|int
name|ORDINAL_MAPPING_THRESHOLD_DEFAULT
init|=
literal|2048
decl_stmt|;
DECL|field|ORDINAL_MAPPING_THRESHOLD_KEY
specifier|public
specifier|final
specifier|static
name|String
name|ORDINAL_MAPPING_THRESHOLD_KEY
init|=
literal|"global_ordinals_compress_threshold"
decl_stmt|;
DECL|field|ORDINAL_MAPPING_THRESHOLD_INDEX_SETTING_KEY
specifier|public
specifier|final
specifier|static
name|String
name|ORDINAL_MAPPING_THRESHOLD_INDEX_SETTING_KEY
init|=
literal|"index."
operator|+
name|ORDINAL_MAPPING_THRESHOLD_KEY
decl_stmt|;
DECL|method|InternalGlobalOrdinalsBuilder
specifier|public
name|InternalGlobalOrdinalsBuilder
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexFieldData
operator|.
name|WithOrdinals
name|build
parameter_list|(
specifier|final
name|IndexReader
name|indexReader
parameter_list|,
name|IndexFieldData
operator|.
name|WithOrdinals
name|indexFieldData
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|1
assert|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// It makes sense to make the overhead ratio configurable for the mapping from segment ords to global ords
comment|// However, other mappings are never the bottleneck and only used to get the original value from an ord, so
comment|// it makes sense to force COMPACT for them
specifier|final
name|float
name|acceptableOverheadRatio
init|=
name|settings
operator|.
name|getAsFloat
argument_list|(
literal|"acceptable_overhead_ratio"
argument_list|,
name|PackedInts
operator|.
name|FAST
argument_list|)
decl_stmt|;
specifier|final
name|AppendingPackedLongBuffer
name|globalOrdToFirstSegment
init|=
operator|new
name|AppendingPackedLongBuffer
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
specifier|final
name|MonotonicAppendingLongBuffer
name|globalOrdToFirstSegmentDelta
init|=
operator|new
name|MonotonicAppendingLongBuffer
argument_list|(
name|PackedInts
operator|.
name|COMPACT
argument_list|)
decl_stmt|;
name|FieldDataType
name|fieldDataType
init|=
name|indexFieldData
operator|.
name|getFieldDataType
argument_list|()
decl_stmt|;
name|int
name|defaultThreshold
init|=
name|settings
operator|.
name|getAsInt
argument_list|(
name|ORDINAL_MAPPING_THRESHOLD_INDEX_SETTING_KEY
argument_list|,
name|ORDINAL_MAPPING_THRESHOLD_DEFAULT
argument_list|)
decl_stmt|;
name|int
name|threshold
init|=
name|fieldDataType
operator|.
name|getSettings
argument_list|()
operator|.
name|getAsInt
argument_list|(
name|ORDINAL_MAPPING_THRESHOLD_KEY
argument_list|,
name|defaultThreshold
argument_list|)
decl_stmt|;
name|OrdinalMappingSourceBuilder
name|ordinalMappingBuilder
init|=
operator|new
name|OrdinalMappingSourceBuilder
argument_list|(
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|acceptableOverheadRatio
argument_list|,
name|threshold
argument_list|)
decl_stmt|;
name|long
name|currentGlobalOrdinal
init|=
literal|0
decl_stmt|;
specifier|final
name|AtomicFieldData
operator|.
name|WithOrdinals
index|[]
name|withOrdinals
init|=
operator|new
name|AtomicFieldData
operator|.
name|WithOrdinals
index|[
name|indexReader
operator|.
name|leaves
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|TermIterator
name|termIterator
init|=
operator|new
name|TermIterator
argument_list|(
name|indexFieldData
argument_list|,
name|indexReader
operator|.
name|leaves
argument_list|()
argument_list|,
name|withOrdinals
argument_list|)
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|termIterator
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|termIterator
operator|.
name|next
argument_list|()
control|)
block|{
name|globalOrdToFirstSegment
operator|.
name|add
argument_list|(
name|termIterator
operator|.
name|firstReaderIndex
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|globalOrdinalDelta
init|=
name|currentGlobalOrdinal
operator|-
name|termIterator
operator|.
name|firstLocalOrdinal
argument_list|()
decl_stmt|;
name|globalOrdToFirstSegmentDelta
operator|.
name|add
argument_list|(
name|globalOrdinalDelta
argument_list|)
expr_stmt|;
for|for
control|(
name|TermIterator
operator|.
name|LeafSource
name|leafSource
range|:
name|termIterator
operator|.
name|competitiveLeafs
argument_list|()
control|)
block|{
name|ordinalMappingBuilder
operator|.
name|onOrdinal
argument_list|(
name|leafSource
operator|.
name|context
operator|.
name|ord
argument_list|,
name|leafSource
operator|.
name|tenum
operator|.
name|ord
argument_list|()
argument_list|,
name|currentGlobalOrdinal
argument_list|)
expr_stmt|;
block|}
name|currentGlobalOrdinal
operator|++
expr_stmt|;
block|}
comment|// ram used for the globalOrd to segmentOrd and segmentOrd to firstReaderIndex lookups
name|long
name|memorySizeInBytesCounter
init|=
literal|0
decl_stmt|;
name|globalOrdToFirstSegment
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|memorySizeInBytesCounter
operator|+=
name|globalOrdToFirstSegment
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
name|globalOrdToFirstSegmentDelta
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|memorySizeInBytesCounter
operator|+=
name|globalOrdToFirstSegmentDelta
operator|.
name|ramBytesUsed
argument_list|()
expr_stmt|;
specifier|final
name|long
name|maxOrd
init|=
name|currentGlobalOrdinal
decl_stmt|;
name|OrdinalMappingSource
index|[]
name|segmentOrdToGlobalOrdLookups
init|=
name|ordinalMappingBuilder
operator|.
name|build
argument_list|(
name|maxOrd
argument_list|)
decl_stmt|;
comment|// add ram used for the main segmentOrd to globalOrd lookups
name|memorySizeInBytesCounter
operator|+=
name|ordinalMappingBuilder
operator|.
name|getMemorySizeInBytes
argument_list|()
expr_stmt|;
specifier|final
name|long
name|memorySizeInBytes
init|=
name|memorySizeInBytesCounter
decl_stmt|;
name|breakerService
operator|.
name|getBreaker
argument_list|()
operator|.
name|addWithoutBreaking
argument_list|(
name|memorySizeInBytes
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
comment|// this does include the [] from the array in the impl name
name|String
name|implName
init|=
name|segmentOrdToGlobalOrdLookups
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Global-ordinals[{}][{}][{}] took {} ms"
argument_list|,
name|implName
argument_list|,
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|maxOrd
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GlobalOrdinalsIndexFieldData
argument_list|(
name|indexFieldData
operator|.
name|index
argument_list|()
argument_list|,
name|settings
argument_list|,
name|indexFieldData
operator|.
name|getFieldNames
argument_list|()
argument_list|,
name|fieldDataType
argument_list|,
name|withOrdinals
argument_list|,
name|globalOrdToFirstSegment
argument_list|,
name|globalOrdToFirstSegmentDelta
argument_list|,
name|segmentOrdToGlobalOrdLookups
argument_list|,
name|memorySizeInBytes
argument_list|)
return|;
block|}
DECL|interface|OrdinalMappingSource
specifier|public
interface|interface
name|OrdinalMappingSource
block|{
DECL|method|globalOrdinals
name|Ordinals
operator|.
name|Docs
name|globalOrdinals
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
parameter_list|)
function_decl|;
block|}
DECL|class|GlobalOrdinalMapping
specifier|public
specifier|static
specifier|abstract
class|class
name|GlobalOrdinalMapping
implements|implements
name|Ordinals
operator|.
name|Docs
block|{
DECL|field|segmentOrdinals
specifier|protected
specifier|final
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
decl_stmt|;
DECL|field|memorySizeInBytes
specifier|private
specifier|final
name|long
name|memorySizeInBytes
decl_stmt|;
DECL|field|maxOrd
specifier|protected
specifier|final
name|long
name|maxOrd
decl_stmt|;
DECL|field|currentGlobalOrd
specifier|protected
name|long
name|currentGlobalOrd
decl_stmt|;
DECL|method|GlobalOrdinalMapping
specifier|private
name|GlobalOrdinalMapping
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|,
name|long
name|maxOrd
parameter_list|)
block|{
name|this
operator|.
name|segmentOrdinals
operator|=
name|segmentOrdinals
expr_stmt|;
name|this
operator|.
name|memorySizeInBytes
operator|=
name|memorySizeInBytes
expr_stmt|;
name|this
operator|.
name|maxOrd
operator|=
name|maxOrd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
specifier|final
name|long
name|getMaxOrd
parameter_list|()
block|{
return|return
name|maxOrd
return|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
specifier|final
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
name|segmentOrdinals
operator|.
name|isMultiValued
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
specifier|final
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|segmentOrdinals
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|currentOrd
specifier|public
specifier|final
name|long
name|currentOrd
parameter_list|()
block|{
return|return
name|currentGlobalOrd
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
specifier|final
name|long
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|long
name|segmentOrd
init|=
name|segmentOrdinals
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|segmentOrd
operator|==
name|Ordinals
operator|.
name|MISSING_ORDINAL
condition|)
block|{
return|return
name|currentGlobalOrd
operator|=
name|Ordinals
operator|.
name|MISSING_ORDINAL
return|;
block|}
else|else
block|{
return|return
name|currentGlobalOrd
operator|=
name|getGlobalOrd
argument_list|(
name|segmentOrd
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
specifier|final
name|long
name|nextOrd
parameter_list|()
block|{
name|long
name|segmentOrd
init|=
name|segmentOrdinals
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
return|return
name|currentGlobalOrd
operator|=
name|getGlobalOrd
argument_list|(
name|segmentOrd
argument_list|)
return|;
block|}
DECL|method|getGlobalOrd
specifier|public
specifier|abstract
name|long
name|getGlobalOrd
parameter_list|(
name|long
name|segmentOrd
parameter_list|)
function_decl|;
block|}
DECL|class|OrdinalMappingSourceBuilder
specifier|private
specifier|final
specifier|static
class|class
name|OrdinalMappingSourceBuilder
block|{
DECL|field|segmentOrdToGlobalOrdDeltas
specifier|final
name|MonotonicAppendingLongBuffer
index|[]
name|segmentOrdToGlobalOrdDeltas
decl_stmt|;
DECL|field|acceptableOverheadRatio
specifier|final
name|float
name|acceptableOverheadRatio
decl_stmt|;
DECL|field|numSegments
specifier|final
name|int
name|numSegments
decl_stmt|;
DECL|field|threshold
specifier|final
name|int
name|threshold
decl_stmt|;
DECL|field|memorySizeInBytesCounter
name|long
name|memorySizeInBytesCounter
decl_stmt|;
DECL|method|OrdinalMappingSourceBuilder
specifier|private
name|OrdinalMappingSourceBuilder
parameter_list|(
name|int
name|numSegments
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|,
name|int
name|threshold
parameter_list|)
block|{
name|segmentOrdToGlobalOrdDeltas
operator|=
operator|new
name|MonotonicAppendingLongBuffer
index|[
name|numSegments
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentOrdToGlobalOrdDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|segmentOrdToGlobalOrdDeltas
index|[
name|i
index|]
operator|=
operator|new
name|MonotonicAppendingLongBuffer
argument_list|(
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|numSegments
operator|=
name|numSegments
expr_stmt|;
name|this
operator|.
name|acceptableOverheadRatio
operator|=
name|acceptableOverheadRatio
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
block|}
DECL|method|onOrdinal
specifier|public
name|void
name|onOrdinal
parameter_list|(
name|int
name|readerIndex
parameter_list|,
name|long
name|segmentOrdinal
parameter_list|,
name|long
name|globalOrdinal
parameter_list|)
block|{
name|long
name|delta
init|=
name|globalOrdinal
operator|-
name|segmentOrdinal
decl_stmt|;
name|segmentOrdToGlobalOrdDeltas
index|[
name|readerIndex
index|]
operator|.
name|add
argument_list|(
name|delta
argument_list|)
expr_stmt|;
block|}
DECL|method|build
specifier|public
name|OrdinalMappingSource
index|[]
name|build
parameter_list|(
name|long
name|maxOrd
parameter_list|)
block|{
comment|// If we find out that there are less then predefined number of ordinals, it is better to put the the
comment|// segment ordinal to global ordinal mapping in a packed ints, since the amount values are small and
comment|// will most likely fit in the CPU caches and MonotonicAppendingLongBuffer's compression will just be
comment|// unnecessary.
if|if
condition|(
name|maxOrd
operator|<=
name|threshold
condition|)
block|{
comment|// Rebuilding from MonotonicAppendingLongBuffer to PackedInts.Mutable is fast
name|PackedInts
operator|.
name|Mutable
index|[]
name|newSegmentOrdToGlobalOrdDeltas
init|=
operator|new
name|PackedInts
operator|.
name|Mutable
index|[
name|numSegments
index|]
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
name|segmentOrdToGlobalOrdDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|newSegmentOrdToGlobalOrdDeltas
index|[
name|i
index|]
operator|=
name|PackedInts
operator|.
name|getMutable
argument_list|(
operator|(
name|int
operator|)
name|segmentOrdToGlobalOrdDeltas
index|[
name|i
index|]
operator|.
name|size
argument_list|()
argument_list|,
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxOrd
argument_list|)
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|readerIndex
init|=
literal|0
init|;
name|readerIndex
operator|<
name|segmentOrdToGlobalOrdDeltas
operator|.
name|length
condition|;
name|readerIndex
operator|++
control|)
block|{
name|MonotonicAppendingLongBuffer
name|segmentOrdToGlobalOrdDelta
init|=
name|segmentOrdToGlobalOrdDeltas
index|[
name|readerIndex
index|]
decl_stmt|;
for|for
control|(
name|long
name|ordIndex
init|=
literal|0
init|;
name|ordIndex
operator|<
name|segmentOrdToGlobalOrdDelta
operator|.
name|size
argument_list|()
condition|;
name|ordIndex
operator|++
control|)
block|{
name|long
name|ordDelta
init|=
name|segmentOrdToGlobalOrdDelta
operator|.
name|get
argument_list|(
name|ordIndex
argument_list|)
decl_stmt|;
name|newSegmentOrdToGlobalOrdDeltas
index|[
name|readerIndex
index|]
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|ordIndex
argument_list|,
name|ordDelta
argument_list|)
expr_stmt|;
block|}
block|}
name|PackedIntOrdinalMappingSource
index|[]
name|sources
init|=
operator|new
name|PackedIntOrdinalMappingSource
index|[
name|numSegments
index|]
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
name|newSegmentOrdToGlobalOrdDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|PackedInts
operator|.
name|Reader
name|segmentOrdToGlobalOrdDelta
init|=
name|newSegmentOrdToGlobalOrdDeltas
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|segmentOrdToGlobalOrdDelta
operator|.
name|size
argument_list|()
operator|==
name|maxOrd
condition|)
block|{
comment|// This means that a segment contains all the value and in that case segment ordinals
comment|// can be used as global ordinals. This will save an extra lookup per hit.
name|sources
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|long
name|ramUsed
init|=
name|segmentOrdToGlobalOrdDelta
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|sources
index|[
name|i
index|]
operator|=
operator|new
name|PackedIntOrdinalMappingSource
argument_list|(
name|segmentOrdToGlobalOrdDelta
argument_list|,
name|ramUsed
argument_list|,
name|maxOrd
argument_list|)
expr_stmt|;
name|memorySizeInBytesCounter
operator|+=
name|ramUsed
expr_stmt|;
block|}
block|}
return|return
name|sources
return|;
block|}
else|else
block|{
name|OrdinalMappingSource
index|[]
name|sources
init|=
operator|new
name|OrdinalMappingSource
index|[
name|segmentOrdToGlobalOrdDeltas
operator|.
name|length
index|]
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
name|segmentOrdToGlobalOrdDeltas
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|MonotonicAppendingLongBuffer
name|segmentOrdToGlobalOrdLookup
init|=
name|segmentOrdToGlobalOrdDeltas
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|segmentOrdToGlobalOrdLookup
operator|.
name|size
argument_list|()
operator|==
name|maxOrd
condition|)
block|{
comment|// idem as above
name|sources
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|segmentOrdToGlobalOrdLookup
operator|.
name|freeze
argument_list|()
expr_stmt|;
name|long
name|ramUsed
init|=
name|segmentOrdToGlobalOrdLookup
operator|.
name|ramBytesUsed
argument_list|()
decl_stmt|;
name|sources
index|[
name|i
index|]
operator|=
operator|new
name|CompressedOrdinalMappingSource
argument_list|(
name|segmentOrdToGlobalOrdLookup
argument_list|,
name|ramUsed
argument_list|,
name|maxOrd
argument_list|)
expr_stmt|;
name|memorySizeInBytesCounter
operator|+=
name|ramUsed
expr_stmt|;
block|}
block|}
return|return
name|sources
return|;
block|}
block|}
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|memorySizeInBytesCounter
return|;
block|}
block|}
DECL|class|CompressedOrdinalMappingSource
specifier|private
specifier|final
specifier|static
class|class
name|CompressedOrdinalMappingSource
implements|implements
name|OrdinalMappingSource
block|{
DECL|field|globalOrdinalMapping
specifier|private
specifier|final
name|MonotonicAppendingLongBuffer
name|globalOrdinalMapping
decl_stmt|;
DECL|field|memorySizeInBytes
specifier|private
specifier|final
name|long
name|memorySizeInBytes
decl_stmt|;
DECL|field|maxOrd
specifier|private
specifier|final
name|long
name|maxOrd
decl_stmt|;
DECL|method|CompressedOrdinalMappingSource
specifier|private
name|CompressedOrdinalMappingSource
parameter_list|(
name|MonotonicAppendingLongBuffer
name|globalOrdinalMapping
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|,
name|long
name|maxOrd
parameter_list|)
block|{
name|this
operator|.
name|globalOrdinalMapping
operator|=
name|globalOrdinalMapping
expr_stmt|;
name|this
operator|.
name|memorySizeInBytes
operator|=
name|memorySizeInBytes
expr_stmt|;
name|this
operator|.
name|maxOrd
operator|=
name|maxOrd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|globalOrdinals
specifier|public
name|Ordinals
operator|.
name|Docs
name|globalOrdinals
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
parameter_list|)
block|{
return|return
operator|new
name|GlobalOrdinalsDocs
argument_list|(
name|segmentOrdinals
argument_list|,
name|globalOrdinalMapping
argument_list|,
name|memorySizeInBytes
argument_list|,
name|maxOrd
argument_list|)
return|;
block|}
DECL|class|GlobalOrdinalsDocs
specifier|private
specifier|final
specifier|static
class|class
name|GlobalOrdinalsDocs
extends|extends
name|GlobalOrdinalMapping
block|{
DECL|field|segmentOrdToGlobalOrdLookup
specifier|private
specifier|final
name|MonotonicAppendingLongBuffer
name|segmentOrdToGlobalOrdLookup
decl_stmt|;
DECL|method|GlobalOrdinalsDocs
specifier|private
name|GlobalOrdinalsDocs
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
parameter_list|,
name|MonotonicAppendingLongBuffer
name|segmentOrdToGlobalOrdLookup
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|,
name|long
name|maxOrd
parameter_list|)
block|{
name|super
argument_list|(
name|segmentOrdinals
argument_list|,
name|memorySizeInBytes
argument_list|,
name|maxOrd
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentOrdToGlobalOrdLookup
operator|=
name|segmentOrdToGlobalOrdLookup
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGlobalOrd
specifier|public
name|long
name|getGlobalOrd
parameter_list|(
name|long
name|segmentOrd
parameter_list|)
block|{
return|return
name|segmentOrd
operator|+
name|segmentOrdToGlobalOrdLookup
operator|.
name|get
argument_list|(
name|segmentOrd
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|PackedIntOrdinalMappingSource
specifier|private
specifier|static
specifier|final
class|class
name|PackedIntOrdinalMappingSource
implements|implements
name|OrdinalMappingSource
block|{
DECL|field|segmentOrdToGlobalOrdLookup
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|segmentOrdToGlobalOrdLookup
decl_stmt|;
DECL|field|memorySizeInBytes
specifier|private
specifier|final
name|long
name|memorySizeInBytes
decl_stmt|;
DECL|field|maxOrd
specifier|private
specifier|final
name|long
name|maxOrd
decl_stmt|;
DECL|method|PackedIntOrdinalMappingSource
specifier|private
name|PackedIntOrdinalMappingSource
parameter_list|(
name|PackedInts
operator|.
name|Reader
name|segmentOrdToGlobalOrdLookup
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|,
name|long
name|maxOrd
parameter_list|)
block|{
name|this
operator|.
name|segmentOrdToGlobalOrdLookup
operator|=
name|segmentOrdToGlobalOrdLookup
expr_stmt|;
name|this
operator|.
name|memorySizeInBytes
operator|=
name|memorySizeInBytes
expr_stmt|;
name|this
operator|.
name|maxOrd
operator|=
name|maxOrd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|globalOrdinals
specifier|public
name|Ordinals
operator|.
name|Docs
name|globalOrdinals
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
parameter_list|)
block|{
return|return
operator|new
name|GlobalOrdinalsDocs
argument_list|(
name|segmentOrdinals
argument_list|,
name|memorySizeInBytes
argument_list|,
name|maxOrd
argument_list|,
name|segmentOrdToGlobalOrdLookup
argument_list|)
return|;
block|}
DECL|class|GlobalOrdinalsDocs
specifier|private
specifier|final
specifier|static
class|class
name|GlobalOrdinalsDocs
extends|extends
name|GlobalOrdinalMapping
block|{
DECL|field|segmentOrdToGlobalOrdLookup
specifier|private
specifier|final
name|PackedInts
operator|.
name|Reader
name|segmentOrdToGlobalOrdLookup
decl_stmt|;
DECL|method|GlobalOrdinalsDocs
specifier|private
name|GlobalOrdinalsDocs
parameter_list|(
name|Ordinals
operator|.
name|Docs
name|segmentOrdinals
parameter_list|,
name|long
name|memorySizeInBytes
parameter_list|,
name|long
name|maxOrd
parameter_list|,
name|PackedInts
operator|.
name|Reader
name|segmentOrdToGlobalOrdLookup
parameter_list|)
block|{
name|super
argument_list|(
name|segmentOrdinals
argument_list|,
name|memorySizeInBytes
argument_list|,
name|maxOrd
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentOrdToGlobalOrdLookup
operator|=
name|segmentOrdToGlobalOrdLookup
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getGlobalOrd
specifier|public
name|long
name|getGlobalOrd
parameter_list|(
name|long
name|segmentOrd
parameter_list|)
block|{
return|return
name|segmentOrd
operator|+
name|segmentOrdToGlobalOrdLookup
operator|.
name|get
argument_list|(
operator|(
name|int
operator|)
name|segmentOrd
argument_list|)
return|;
block|}
block|}
block|}
DECL|class|TermIterator
specifier|private
specifier|final
specifier|static
class|class
name|TermIterator
implements|implements
name|BytesRefIterator
block|{
DECL|field|sources
specifier|private
specifier|final
name|LeafSourceQueue
name|sources
decl_stmt|;
DECL|field|competitiveLeafs
specifier|private
specifier|final
name|List
argument_list|<
name|LeafSource
argument_list|>
name|competitiveLeafs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|TermIterator
specifier|private
name|TermIterator
parameter_list|(
name|IndexFieldData
operator|.
name|WithOrdinals
name|indexFieldData
parameter_list|,
name|List
argument_list|<
name|AtomicReaderContext
argument_list|>
name|leaves
parameter_list|,
name|AtomicFieldData
operator|.
name|WithOrdinals
index|[]
name|withOrdinals
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|sources
operator|=
operator|new
name|LeafSourceQueue
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|leaves
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|AtomicReaderContext
name|atomicReaderContext
init|=
name|leaves
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|AtomicFieldData
operator|.
name|WithOrdinals
name|afd
init|=
name|indexFieldData
operator|.
name|load
argument_list|(
name|atomicReaderContext
argument_list|)
decl_stmt|;
name|withOrdinals
index|[
name|i
index|]
operator|=
name|afd
expr_stmt|;
name|LeafSource
name|leafSource
init|=
operator|new
name|LeafSource
argument_list|(
name|afd
argument_list|,
name|atomicReaderContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|leafSource
operator|.
name|current
operator|!=
literal|null
condition|)
block|{
name|sources
operator|.
name|add
argument_list|(
name|leafSource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|LeafSource
name|top
range|:
name|competitiveLeafs
control|)
block|{
if|if
condition|(
name|top
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sources
operator|.
name|add
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
block|}
name|competitiveLeafs
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
do|do
block|{
name|LeafSource
name|competitiveLeaf
init|=
name|sources
operator|.
name|pop
argument_list|()
decl_stmt|;
name|competitiveLeafs
operator|.
name|add
argument_list|(
name|competitiveLeaf
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|sources
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|competitiveLeafs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|current
operator|.
name|equals
argument_list|(
name|sources
operator|.
name|top
argument_list|()
operator|.
name|current
argument_list|)
condition|)
do|;
return|return
name|competitiveLeafs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|current
return|;
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
return|;
block|}
DECL|method|competitiveLeafs
name|List
argument_list|<
name|LeafSource
argument_list|>
name|competitiveLeafs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|competitiveLeafs
return|;
block|}
DECL|method|firstReaderIndex
name|int
name|firstReaderIndex
parameter_list|()
block|{
return|return
name|competitiveLeafs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|context
operator|.
name|ord
return|;
block|}
DECL|method|firstLocalOrdinal
name|long
name|firstLocalOrdinal
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|competitiveLeafs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|tenum
operator|.
name|ord
argument_list|()
return|;
block|}
DECL|class|LeafSource
specifier|private
specifier|static
class|class
name|LeafSource
block|{
DECL|field|tenum
specifier|final
name|TermsEnum
name|tenum
decl_stmt|;
DECL|field|context
specifier|final
name|AtomicReaderContext
name|context
decl_stmt|;
DECL|field|current
name|BytesRef
name|current
decl_stmt|;
DECL|method|LeafSource
specifier|private
name|LeafSource
parameter_list|(
name|AtomicFieldData
operator|.
name|WithOrdinals
name|afd
parameter_list|,
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|tenum
operator|=
name|afd
operator|.
name|getTermsEnum
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|current
operator|=
name|tenum
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
DECL|method|next
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|current
operator|=
name|tenum
operator|.
name|next
argument_list|()
return|;
block|}
block|}
DECL|class|LeafSourceQueue
specifier|private
specifier|final
specifier|static
class|class
name|LeafSourceQueue
extends|extends
name|PriorityQueue
argument_list|<
name|LeafSource
argument_list|>
block|{
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
init|=
name|BytesRef
operator|.
name|getUTF8SortedAsUnicodeComparator
argument_list|()
decl_stmt|;
DECL|method|LeafSourceQueue
name|LeafSourceQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
name|boolean
name|lessThan
parameter_list|(
name|LeafSource
name|termsA
parameter_list|,
name|LeafSource
name|termsB
parameter_list|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|termsA
operator|.
name|current
argument_list|,
name|termsB
operator|.
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
operator|<
literal|0
return|;
block|}
else|else
block|{
return|return
name|termsA
operator|.
name|context
operator|.
name|ord
operator|<
name|termsB
operator|.
name|context
operator|.
name|ord
return|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

