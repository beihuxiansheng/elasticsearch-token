begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.store.bytebuffer
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|bytebuffer
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|BufferUnderflowException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ByteBufferIndexInput
specifier|public
class|class
name|ByteBufferIndexInput
extends|extends
name|IndexInput
block|{
DECL|field|EMPTY_BUFFER
specifier|private
specifier|final
specifier|static
name|ByteBuffer
name|EMPTY_BUFFER
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
operator|.
name|asReadOnlyBuffer
argument_list|()
decl_stmt|;
DECL|field|file
specifier|private
specifier|final
name|ByteBufferFile
name|file
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|field|currentBuffer
specifier|private
name|ByteBuffer
name|currentBuffer
decl_stmt|;
DECL|field|currentBufferIndex
specifier|private
name|int
name|currentBufferIndex
decl_stmt|;
DECL|field|bufferStart
specifier|private
name|long
name|bufferStart
decl_stmt|;
DECL|field|BUFFER_SIZE
specifier|private
specifier|final
name|int
name|BUFFER_SIZE
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|ByteBufferIndexInput
specifier|public
name|ByteBufferIndexInput
parameter_list|(
name|String
name|name
parameter_list|,
name|ByteBufferFile
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|"BBIndexInput(name="
operator|+
name|name
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|file
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|file
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|this
operator|.
name|BUFFER_SIZE
operator|=
name|file
operator|.
name|bufferSize
expr_stmt|;
comment|// make sure that we switch to the
comment|// first needed buffer lazily
name|currentBufferIndex
operator|=
operator|-
literal|1
expr_stmt|;
name|currentBuffer
operator|=
name|EMPTY_BUFFER
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// we protected from double closing the index input since
comment|// some tests do that...
if|if
condition|(
name|closed
condition|)
block|{
return|return;
block|}
name|closed
operator|=
literal|true
expr_stmt|;
name|file
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|currentBuffer
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|currentBuffer
operator|.
name|getShort
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
name|currentBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|readShort
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|currentBuffer
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|currentBuffer
operator|.
name|getInt
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
name|currentBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|readInt
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|currentBuffer
operator|.
name|mark
argument_list|()
expr_stmt|;
return|return
name|currentBuffer
operator|.
name|getLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|BufferUnderflowException
name|e
parameter_list|)
block|{
name|currentBuffer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|readLong
argument_list|()
return|;
block|}
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
operator|!
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|currentBufferIndex
operator|++
expr_stmt|;
name|switchCurrentBuffer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|currentBuffer
operator|.
name|get
argument_list|()
return|;
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
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
name|len
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|!
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|currentBufferIndex
operator|++
expr_stmt|;
name|switchCurrentBuffer
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|int
name|remainInBuffer
init|=
name|currentBuffer
operator|.
name|remaining
argument_list|()
decl_stmt|;
name|int
name|bytesToCopy
init|=
name|len
operator|<
name|remainInBuffer
condition|?
name|len
else|:
name|remainInBuffer
decl_stmt|;
name|currentBuffer
operator|.
name|get
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|bytesToCopy
expr_stmt|;
name|len
operator|-=
name|bytesToCopy
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getFilePointer
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|currentBufferIndex
operator|<
literal|0
condition|?
literal|0
else|:
name|bufferStart
operator|+
name|currentBuffer
operator|.
name|position
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentBuffer
operator|==
name|EMPTY_BUFFER
operator|||
name|pos
operator|<
name|bufferStart
operator|||
name|pos
operator|>=
name|bufferStart
operator|+
name|BUFFER_SIZE
condition|)
block|{
name|currentBufferIndex
operator|=
call|(
name|int
call|)
argument_list|(
name|pos
operator|/
name|BUFFER_SIZE
argument_list|)
expr_stmt|;
name|switchCurrentBuffer
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|currentBuffer
operator|.
name|position
argument_list|(
call|(
name|int
call|)
argument_list|(
name|pos
operator|%
name|BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
comment|// Grrr, need to wrap in IllegalArgumentException since tests (if not other places)
comment|// expect an IOException...
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|IOException
name|ioException
init|=
operator|new
name|IOException
argument_list|(
literal|"seeking past position"
argument_list|)
decl_stmt|;
name|ioException
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|ioException
throw|;
block|}
block|}
DECL|method|switchCurrentBuffer
specifier|private
name|void
name|switchCurrentBuffer
parameter_list|(
name|boolean
name|enforceEOF
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|currentBufferIndex
operator|>=
name|file
operator|.
name|numBuffers
argument_list|()
condition|)
block|{
comment|// end of file reached, no more buffers left
if|if
condition|(
name|enforceEOF
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Read past EOF (resource: "
operator|+
name|this
operator|+
literal|")"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Force EOF if a read takes place at this position
name|currentBufferIndex
operator|--
expr_stmt|;
name|currentBuffer
operator|.
name|position
argument_list|(
name|currentBuffer
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ByteBuffer
name|buffer
init|=
name|file
operator|.
name|getBuffer
argument_list|(
name|currentBufferIndex
argument_list|)
decl_stmt|;
comment|// we must duplicate (and make it read only while we are at it) since we need position and such to be independent
name|currentBuffer
operator|=
name|buffer
operator|.
name|asReadOnlyBuffer
argument_list|()
expr_stmt|;
name|currentBuffer
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|bufferStart
operator|=
operator|(
name|long
operator|)
name|BUFFER_SIZE
operator|*
operator|(
name|long
operator|)
name|currentBufferIndex
expr_stmt|;
comment|// if we are at the tip, limit the current buffer to only whats available to read
name|long
name|buflen
init|=
name|length
operator|-
name|bufferStart
decl_stmt|;
if|if
condition|(
name|buflen
operator|<
name|BUFFER_SIZE
condition|)
block|{
name|currentBuffer
operator|.
name|limit
argument_list|(
operator|(
name|int
operator|)
name|buflen
argument_list|)
expr_stmt|;
block|}
comment|// we need to enforce EOF here as well...
if|if
condition|(
operator|!
name|currentBuffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
if|if
condition|(
name|enforceEOF
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|(
literal|"Read past EOF (resource: "
operator|+
name|this
operator|+
literal|")"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Force EOF if a read takes place at this position
name|currentBufferIndex
operator|--
expr_stmt|;
name|currentBuffer
operator|.
name|position
argument_list|(
name|currentBuffer
operator|.
name|limit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|ByteBufferIndexInput
name|cloned
init|=
operator|(
name|ByteBufferIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|cloned
operator|.
name|file
operator|.
name|incRef
argument_list|()
expr_stmt|;
comment|// inc ref on cloned one
if|if
condition|(
name|currentBuffer
operator|!=
name|EMPTY_BUFFER
condition|)
block|{
name|cloned
operator|.
name|currentBuffer
operator|=
name|currentBuffer
operator|.
name|asReadOnlyBuffer
argument_list|()
expr_stmt|;
name|cloned
operator|.
name|currentBuffer
operator|.
name|position
argument_list|(
name|currentBuffer
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|cloned
return|;
block|}
block|}
end_class

end_unit

