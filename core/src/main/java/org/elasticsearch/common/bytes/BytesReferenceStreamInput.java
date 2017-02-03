begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
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
name|BytesRefIterator
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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

begin_comment
comment|/**  * A StreamInput that reads off a {@link BytesRefIterator}. This is used to provide  * generic stream access to {@link BytesReference} instances without materializing the  * underlying bytes reference.  */
end_comment

begin_class
DECL|class|BytesReferenceStreamInput
specifier|final
class|class
name|BytesReferenceStreamInput
extends|extends
name|StreamInput
block|{
DECL|field|iterator
specifier|private
specifier|final
name|BytesRefIterator
name|iterator
decl_stmt|;
DECL|field|sliceOffset
specifier|private
name|int
name|sliceOffset
decl_stmt|;
DECL|field|slice
specifier|private
name|BytesRef
name|slice
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
comment|// the total size of the stream
DECL|field|offset
specifier|private
name|int
name|offset
decl_stmt|;
comment|// the current position of the stream
DECL|method|BytesReferenceStreamInput
name|BytesReferenceStreamInput
parameter_list|(
name|BytesRefIterator
name|iterator
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
name|this
operator|.
name|slice
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|sliceOffset
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|>=
name|length
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|maybeNextSlice
argument_list|()
expr_stmt|;
name|byte
name|b
init|=
name|slice
operator|.
name|bytes
index|[
name|slice
operator|.
name|offset
operator|+
operator|(
name|sliceOffset
operator|++
operator|)
index|]
decl_stmt|;
name|offset
operator|++
expr_stmt|;
return|return
name|b
return|;
block|}
DECL|method|maybeNextSlice
specifier|private
name|void
name|maybeNextSlice
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
name|sliceOffset
operator|==
name|slice
operator|.
name|length
condition|)
block|{
name|slice
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|sliceOffset
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|bOffset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|+
name|len
operator|>
name|length
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Cannot read "
operator|+
name|len
operator|+
literal|" bytes from stream with length "
operator|+
name|length
operator|+
literal|" at offset "
operator|+
name|offset
argument_list|)
throw|;
block|}
name|read
argument_list|(
name|b
argument_list|,
name|bOffset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|>=
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|readByte
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|bOffset
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|offset
operator|>=
name|length
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
specifier|final
name|int
name|numBytesToCopy
init|=
name|Math
operator|.
name|min
argument_list|(
name|len
argument_list|,
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
name|int
name|remaining
init|=
name|numBytesToCopy
decl_stmt|;
comment|// copy the full length or the remaining part
name|int
name|destOffset
init|=
name|bOffset
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|maybeNextSlice
argument_list|()
expr_stmt|;
specifier|final
name|int
name|currentLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|slice
operator|.
name|length
operator|-
name|sliceOffset
argument_list|)
decl_stmt|;
assert|assert
name|currentLen
operator|>
literal|0
operator|:
literal|"length has to be> 0 to make progress but was: "
operator|+
name|currentLen
assert|;
name|System
operator|.
name|arraycopy
argument_list|(
name|slice
operator|.
name|bytes
argument_list|,
name|slice
operator|.
name|offset
operator|+
name|sliceOffset
argument_list|,
name|b
argument_list|,
name|destOffset
argument_list|,
name|currentLen
argument_list|)
expr_stmt|;
name|destOffset
operator|+=
name|currentLen
expr_stmt|;
name|remaining
operator|-=
name|currentLen
expr_stmt|;
name|sliceOffset
operator|+=
name|currentLen
expr_stmt|;
name|offset
operator|+=
name|currentLen
expr_stmt|;
assert|assert
name|remaining
operator|>=
literal|0
operator|:
literal|"remaining: "
operator|+
name|remaining
assert|;
block|}
return|return
name|numBytesToCopy
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
annotation|@
name|Override
DECL|method|available
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|length
operator|-
name|offset
return|;
block|}
annotation|@
name|Override
DECL|method|ensureCanReadBytes
specifier|protected
name|void
name|ensureCanReadBytes
parameter_list|(
name|int
name|bytesToRead
parameter_list|)
throws|throws
name|EOFException
block|{
name|int
name|bytesAvailable
init|=
name|length
operator|-
name|offset
decl_stmt|;
if|if
condition|(
name|bytesAvailable
operator|<
name|bytesToRead
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"tried to read: "
operator|+
name|bytesToRead
operator|+
literal|" bytes but only "
operator|+
name|bytesAvailable
operator|+
literal|" remaining"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|skip
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|skip
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|n
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numBytesSkipped
init|=
name|Math
operator|.
name|min
argument_list|(
name|skip
argument_list|,
name|length
operator|-
name|offset
argument_list|)
decl_stmt|;
name|int
name|remaining
init|=
name|numBytesSkipped
decl_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
name|maybeNextSlice
argument_list|()
expr_stmt|;
name|int
name|currentLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|remaining
argument_list|,
name|slice
operator|.
name|length
operator|-
operator|(
name|slice
operator|.
name|offset
operator|+
name|sliceOffset
operator|)
argument_list|)
decl_stmt|;
name|remaining
operator|-=
name|currentLen
expr_stmt|;
name|sliceOffset
operator|+=
name|currentLen
expr_stmt|;
name|offset
operator|+=
name|currentLen
expr_stmt|;
assert|assert
name|remaining
operator|>=
literal|0
operator|:
literal|"remaining: "
operator|+
name|remaining
assert|;
block|}
return|return
name|numBytesSkipped
return|;
block|}
DECL|method|getOffset
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
block|}
end_class

end_unit

