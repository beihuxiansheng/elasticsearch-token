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
name|LongsRef
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
name|index
operator|.
name|fielddata
operator|.
name|BytesValues
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
name|BytesValues
operator|.
name|WithOrdinals
import|;
end_import

begin_comment
comment|/**  * {@link Ordinals} implementation which is efficient at storing field data ordinals for multi-valued or sparse fields.  */
end_comment

begin_class
DECL|class|MultiOrdinals
specifier|public
class|class
name|MultiOrdinals
extends|extends
name|Ordinals
block|{
DECL|field|OFFSETS_PAGE_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|OFFSETS_PAGE_SIZE
init|=
literal|1024
decl_stmt|;
DECL|field|OFFSET_INIT_PAGE_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|OFFSET_INIT_PAGE_COUNT
init|=
literal|16
decl_stmt|;
comment|/**      * Return true if this impl is going to be smaller than {@link SinglePackedOrdinals} by at least 20%.      */
DECL|method|significantlySmallerThanSinglePackedOrdinals
specifier|public
specifier|static
name|boolean
name|significantlySmallerThanSinglePackedOrdinals
parameter_list|(
name|int
name|maxDoc
parameter_list|,
name|int
name|numDocsWithValue
parameter_list|,
name|long
name|numOrds
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|int
name|bitsPerOrd
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|numOrds
argument_list|)
decl_stmt|;
name|bitsPerOrd
operator|=
name|PackedInts
operator|.
name|fastestFormatAndBits
argument_list|(
name|numDocsWithValue
argument_list|,
name|bitsPerOrd
argument_list|,
name|acceptableOverheadRatio
argument_list|)
operator|.
name|bitsPerValue
expr_stmt|;
comment|// Compute the worst-case number of bits per value for offsets in the worst case, eg. if no docs have a value at the
comment|// beginning of the block and all docs have one at the end of the block
specifier|final
name|float
name|avgValuesPerDoc
init|=
operator|(
name|float
operator|)
name|numDocsWithValue
operator|/
name|maxDoc
decl_stmt|;
specifier|final
name|int
name|maxDelta
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|OFFSETS_PAGE_SIZE
operator|*
operator|(
literal|1
operator|-
name|avgValuesPerDoc
operator|)
operator|*
name|avgValuesPerDoc
argument_list|)
decl_stmt|;
name|int
name|bitsPerOffset
init|=
name|PackedInts
operator|.
name|bitsRequired
argument_list|(
name|maxDelta
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// +1 because of the sign
name|bitsPerOffset
operator|=
name|PackedInts
operator|.
name|fastestFormatAndBits
argument_list|(
name|maxDoc
argument_list|,
name|bitsPerOffset
argument_list|,
name|acceptableOverheadRatio
argument_list|)
operator|.
name|bitsPerValue
expr_stmt|;
specifier|final
name|long
name|expectedMultiSizeInBytes
init|=
operator|(
name|long
operator|)
name|numDocsWithValue
operator|*
name|bitsPerOrd
operator|+
operator|(
name|long
operator|)
name|maxDoc
operator|*
name|bitsPerOffset
decl_stmt|;
specifier|final
name|long
name|expectedSingleSizeInBytes
init|=
operator|(
name|long
operator|)
name|maxDoc
operator|*
name|bitsPerOrd
decl_stmt|;
return|return
name|expectedMultiSizeInBytes
operator|<
literal|0.8f
operator|*
name|expectedSingleSizeInBytes
return|;
block|}
DECL|field|multiValued
specifier|private
specifier|final
name|boolean
name|multiValued
decl_stmt|;
DECL|field|maxOrd
specifier|private
specifier|final
name|long
name|maxOrd
decl_stmt|;
DECL|field|endOffsets
specifier|private
specifier|final
name|MonotonicAppendingLongBuffer
name|endOffsets
decl_stmt|;
DECL|field|ords
specifier|private
specifier|final
name|AppendingPackedLongBuffer
name|ords
decl_stmt|;
DECL|method|MultiOrdinals
specifier|public
name|MultiOrdinals
parameter_list|(
name|OrdinalsBuilder
name|builder
parameter_list|,
name|float
name|acceptableOverheadRatio
parameter_list|)
block|{
name|multiValued
operator|=
name|builder
operator|.
name|getNumMultiValuesDocs
argument_list|()
operator|>
literal|0
expr_stmt|;
name|maxOrd
operator|=
name|builder
operator|.
name|getMaxOrd
argument_list|()
expr_stmt|;
name|endOffsets
operator|=
operator|new
name|MonotonicAppendingLongBuffer
argument_list|(
name|OFFSET_INIT_PAGE_COUNT
argument_list|,
name|OFFSETS_PAGE_SIZE
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|ords
operator|=
operator|new
name|AppendingPackedLongBuffer
argument_list|(
name|OFFSET_INIT_PAGE_COUNT
argument_list|,
name|OFFSETS_PAGE_SIZE
argument_list|,
name|acceptableOverheadRatio
argument_list|)
expr_stmt|;
name|long
name|lastEndOffset
init|=
literal|0
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
name|builder
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|LongsRef
name|docOrds
init|=
name|builder
operator|.
name|docOrds
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|long
name|endOffset
init|=
name|lastEndOffset
operator|+
name|docOrds
operator|.
name|length
decl_stmt|;
name|endOffsets
operator|.
name|add
argument_list|(
name|endOffset
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|docOrds
operator|.
name|length
condition|;
operator|++
name|j
control|)
block|{
name|ords
operator|.
name|add
argument_list|(
name|docOrds
operator|.
name|longs
index|[
name|docOrds
operator|.
name|offset
operator|+
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|lastEndOffset
operator|=
name|endOffset
expr_stmt|;
block|}
assert|assert
name|endOffsets
operator|.
name|size
argument_list|()
operator|==
name|builder
operator|.
name|maxDoc
argument_list|()
assert|;
assert|assert
name|ords
operator|.
name|size
argument_list|()
operator|==
name|builder
operator|.
name|getTotalNumOrds
argument_list|()
operator|:
name|ords
operator|.
name|size
argument_list|()
operator|+
literal|" != "
operator|+
name|builder
operator|.
name|getTotalNumOrds
argument_list|()
assert|;
block|}
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|endOffsets
operator|.
name|ramBytesUsed
argument_list|()
operator|+
name|ords
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ordinals
specifier|public
name|WithOrdinals
name|ordinals
parameter_list|(
name|ValuesHolder
name|values
parameter_list|)
block|{
return|return
operator|new
name|MultiDocs
argument_list|(
name|this
argument_list|,
name|values
argument_list|)
return|;
block|}
DECL|class|MultiDocs
specifier|public
specifier|static
class|class
name|MultiDocs
extends|extends
name|BytesValues
operator|.
name|WithOrdinals
block|{
DECL|field|maxOrd
specifier|private
specifier|final
name|long
name|maxOrd
decl_stmt|;
DECL|field|endOffsets
specifier|private
specifier|final
name|MonotonicAppendingLongBuffer
name|endOffsets
decl_stmt|;
DECL|field|ords
specifier|private
specifier|final
name|AppendingPackedLongBuffer
name|ords
decl_stmt|;
DECL|field|offset
specifier|private
name|long
name|offset
decl_stmt|;
DECL|field|limit
specifier|private
name|long
name|limit
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|ValuesHolder
name|values
decl_stmt|;
DECL|method|MultiDocs
name|MultiDocs
parameter_list|(
name|MultiOrdinals
name|ordinals
parameter_list|,
name|ValuesHolder
name|values
parameter_list|)
block|{
name|super
argument_list|(
name|ordinals
operator|.
name|multiValued
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxOrd
operator|=
name|ordinals
operator|.
name|maxOrd
expr_stmt|;
name|this
operator|.
name|endOffsets
operator|=
name|ordinals
operator|.
name|endOffsets
expr_stmt|;
name|this
operator|.
name|ords
operator|=
name|ordinals
operator|.
name|ords
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
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
DECL|method|getOrd
specifier|public
name|long
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
specifier|final
name|long
name|startOffset
init|=
name|docId
operator|>
literal|0
condition|?
name|endOffsets
operator|.
name|get
argument_list|(
name|docId
operator|-
literal|1
argument_list|)
else|:
literal|0
decl_stmt|;
specifier|final
name|long
name|endOffset
init|=
name|endOffsets
operator|.
name|get
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|startOffset
operator|==
name|endOffset
condition|)
block|{
return|return
name|MISSING_ORDINAL
return|;
comment|// ord for missing values
block|}
else|else
block|{
return|return
name|ords
operator|.
name|get
argument_list|(
name|startOffset
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
assert|assert
name|offset
operator|<
name|limit
assert|;
return|return
name|ords
operator|.
name|get
argument_list|(
name|offset
operator|++
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
specifier|final
name|long
name|startOffset
init|=
name|docId
operator|>
literal|0
condition|?
name|endOffsets
operator|.
name|get
argument_list|(
name|docId
operator|-
literal|1
argument_list|)
else|:
literal|0
decl_stmt|;
specifier|final
name|long
name|endOffset
init|=
name|endOffsets
operator|.
name|get
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|offset
operator|=
name|startOffset
expr_stmt|;
name|limit
operator|=
name|endOffset
expr_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|endOffset
operator|-
name|startOffset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getValueByOrd
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|values
operator|.
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

