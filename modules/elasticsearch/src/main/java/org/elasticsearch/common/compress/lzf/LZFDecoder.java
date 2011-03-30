begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this  * file except in compliance with the License. You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software distributed under  * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS  * OF ANY KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress.lzf
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
package|;
end_package

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Decoder that handles decoding of sequence of encoded LZF chunks,  * combining them into a single contiguous result byte array  *  * @author tatu@ning.com  */
end_comment

begin_class
DECL|class|LZFDecoder
specifier|public
class|class
name|LZFDecoder
block|{
DECL|field|BYTE_NULL
specifier|private
specifier|final
specifier|static
name|byte
name|BYTE_NULL
init|=
literal|0
decl_stmt|;
DECL|field|HEADER_BYTES
specifier|private
specifier|final
specifier|static
name|int
name|HEADER_BYTES
init|=
literal|5
decl_stmt|;
comment|// static methods, no need to instantiate
DECL|method|LZFDecoder
specifier|private
name|LZFDecoder
parameter_list|()
block|{     }
DECL|method|decode
specifier|public
specifier|static
name|byte
index|[]
name|decode
parameter_list|(
specifier|final
name|byte
index|[]
name|sourceBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|calculateUncompressedSize
argument_list|(
name|sourceBuffer
argument_list|)
index|]
decl_stmt|;
name|decode
argument_list|(
name|sourceBuffer
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Method for decompressing whole input data, which encoded in LZF      * block structure (compatible with lzf command line utility),      * and can consist of any number of blocks      */
DECL|method|decode
specifier|public
specifier|static
name|int
name|decode
parameter_list|(
specifier|final
name|byte
index|[]
name|sourceBuffer
parameter_list|,
specifier|final
name|byte
index|[]
name|targetBuffer
parameter_list|)
throws|throws
name|IOException
block|{
comment|/* First: let's calculate actual size, so we can allocate          * exact result size. Also useful for basic sanity checking;          * so that after call we know header structure is not corrupt          * (to the degree that lengths etc seem valid)          */
name|byte
index|[]
name|result
init|=
name|targetBuffer
decl_stmt|;
name|int
name|inPtr
init|=
literal|0
decl_stmt|;
name|int
name|outPtr
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|inPtr
operator|<
operator|(
name|sourceBuffer
operator|.
name|length
operator|-
literal|1
operator|)
condition|)
block|{
comment|// -1 to offset possible end marker
name|inPtr
operator|+=
literal|2
expr_stmt|;
comment|// skip 'ZV' marker
name|int
name|type
init|=
name|sourceBuffer
index|[
name|inPtr
operator|++
index|]
decl_stmt|;
name|int
name|len
init|=
name|uint16
argument_list|(
name|sourceBuffer
argument_list|,
name|inPtr
argument_list|)
decl_stmt|;
name|inPtr
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|LZFChunk
operator|.
name|BLOCK_TYPE_NON_COMPRESSED
condition|)
block|{
comment|// uncompressed
name|System
operator|.
name|arraycopy
argument_list|(
name|sourceBuffer
argument_list|,
name|inPtr
argument_list|,
name|result
argument_list|,
name|outPtr
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|outPtr
operator|+=
name|len
expr_stmt|;
block|}
else|else
block|{
comment|// compressed
name|int
name|uncompLen
init|=
name|uint16
argument_list|(
name|sourceBuffer
argument_list|,
name|inPtr
argument_list|)
decl_stmt|;
name|inPtr
operator|+=
literal|2
expr_stmt|;
name|decompressChunk
argument_list|(
name|sourceBuffer
argument_list|,
name|inPtr
argument_list|,
name|result
argument_list|,
name|outPtr
argument_list|,
name|outPtr
operator|+
name|uncompLen
argument_list|)
expr_stmt|;
name|outPtr
operator|+=
name|uncompLen
expr_stmt|;
block|}
name|inPtr
operator|+=
name|len
expr_stmt|;
block|}
return|return
name|outPtr
return|;
block|}
DECL|method|calculateUncompressedSize
specifier|private
specifier|static
name|int
name|calculateUncompressedSize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|uncompressedSize
init|=
literal|0
decl_stmt|;
name|int
name|ptr
init|=
literal|0
decl_stmt|;
name|int
name|blockNr
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ptr
operator|<
name|data
operator|.
name|length
condition|)
block|{
comment|// can use optional end marker
if|if
condition|(
name|ptr
operator|==
operator|(
name|data
operator|.
name|length
operator|+
literal|1
operator|)
operator|&&
name|data
index|[
name|ptr
index|]
operator|==
name|BYTE_NULL
condition|)
block|{
operator|++
name|ptr
expr_stmt|;
comment|// so that we'll be at end
break|break;
block|}
comment|// simpler to handle bounds checks by catching exception here...
try|try
block|{
if|if
condition|(
name|data
index|[
name|ptr
index|]
operator|!=
name|LZFChunk
operator|.
name|BYTE_Z
operator|||
name|data
index|[
name|ptr
operator|+
literal|1
index|]
operator|!=
name|LZFChunk
operator|.
name|BYTE_V
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupt input data, block #"
operator|+
name|blockNr
operator|+
literal|" (at offset "
operator|+
name|ptr
operator|+
literal|"): did not start with 'ZV' signature bytes"
argument_list|)
throw|;
block|}
name|int
name|type
init|=
operator|(
name|int
operator|)
name|data
index|[
name|ptr
operator|+
literal|2
index|]
decl_stmt|;
name|int
name|blockLen
init|=
name|uint16
argument_list|(
name|data
argument_list|,
name|ptr
operator|+
literal|3
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|LZFChunk
operator|.
name|BLOCK_TYPE_NON_COMPRESSED
condition|)
block|{
comment|// uncompressed
name|ptr
operator|+=
literal|5
expr_stmt|;
name|uncompressedSize
operator|+=
name|blockLen
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|LZFChunk
operator|.
name|BLOCK_TYPE_COMPRESSED
condition|)
block|{
comment|// compressed
name|uncompressedSize
operator|+=
name|uint16
argument_list|(
name|data
argument_list|,
name|ptr
operator|+
literal|5
argument_list|)
expr_stmt|;
name|ptr
operator|+=
literal|7
expr_stmt|;
block|}
else|else
block|{
comment|// unknown... CRC-32 would be 2, but that's not implemented by cli tool
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupt input data, block #"
operator|+
name|blockNr
operator|+
literal|" (at offset "
operator|+
name|ptr
operator|+
literal|"): unrecognized block type "
operator|+
operator|(
name|type
operator|&
literal|0xFF
operator|)
argument_list|)
throw|;
block|}
name|ptr
operator|+=
name|blockLen
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupt input data, block #"
operator|+
name|blockNr
operator|+
literal|" (at offset "
operator|+
name|ptr
operator|+
literal|"): truncated block header"
argument_list|)
throw|;
block|}
operator|++
name|blockNr
expr_stmt|;
block|}
comment|// one more sanity check:
if|if
condition|(
name|ptr
operator|!=
name|data
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupt input data: block #"
operator|+
name|blockNr
operator|+
literal|" extends "
operator|+
operator|(
name|data
operator|.
name|length
operator|-
name|ptr
operator|)
operator|+
literal|" beyond end of input"
argument_list|)
throw|;
block|}
return|return
name|uncompressedSize
return|;
block|}
comment|/**      * Main decode from a stream.  Decompressed bytes are placed in the outputBuffer, inputBuffer is a "scratch-area".      *      * @param is           An input stream of LZF compressed bytes      * @param inputBuffer  A byte array used as a scratch area.      * @param outputBuffer A byte array in which the result is returned      * @return The number of bytes placed in the outputBuffer.      */
DECL|method|decompressChunk
specifier|public
specifier|static
name|int
name|decompressChunk
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|,
specifier|final
name|byte
index|[]
name|inputBuffer
parameter_list|,
specifier|final
name|byte
index|[]
name|outputBuffer
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|bytesInOutput
decl_stmt|;
name|int
name|headerLength
init|=
name|is
operator|.
name|read
argument_list|(
name|inputBuffer
argument_list|,
literal|0
argument_list|,
name|HEADER_BYTES
argument_list|)
decl_stmt|;
if|if
condition|(
name|headerLength
operator|!=
name|HEADER_BYTES
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|inPtr
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|inputBuffer
index|[
name|inPtr
index|]
operator|!=
name|LZFChunk
operator|.
name|BYTE_Z
operator|||
name|inputBuffer
index|[
name|inPtr
operator|+
literal|1
index|]
operator|!=
name|LZFChunk
operator|.
name|BYTE_V
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupt input data, block did not start with 'ZV' signature bytes"
argument_list|)
throw|;
block|}
name|inPtr
operator|+=
literal|2
expr_stmt|;
name|int
name|type
init|=
name|inputBuffer
index|[
name|inPtr
operator|++
index|]
decl_stmt|;
name|int
name|compLen
init|=
name|uint16
argument_list|(
name|inputBuffer
argument_list|,
name|inPtr
argument_list|)
decl_stmt|;
name|inPtr
operator|+=
literal|2
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|LZFChunk
operator|.
name|BLOCK_TYPE_NON_COMPRESSED
condition|)
block|{
comment|// uncompressed
name|readFully
argument_list|(
name|is
argument_list|,
literal|false
argument_list|,
name|outputBuffer
argument_list|,
literal|0
argument_list|,
name|compLen
argument_list|)
expr_stmt|;
name|bytesInOutput
operator|=
name|compLen
expr_stmt|;
block|}
else|else
block|{
comment|// compressed
name|readFully
argument_list|(
name|is
argument_list|,
literal|true
argument_list|,
name|inputBuffer
argument_list|,
literal|0
argument_list|,
literal|2
operator|+
name|compLen
argument_list|)
expr_stmt|;
comment|// first 2 bytes are uncompressed length
name|int
name|uncompLen
init|=
name|uint16
argument_list|(
name|inputBuffer
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|decompressChunk
argument_list|(
name|inputBuffer
argument_list|,
literal|2
argument_list|,
name|outputBuffer
argument_list|,
literal|0
argument_list|,
name|uncompLen
argument_list|)
expr_stmt|;
name|bytesInOutput
operator|=
name|uncompLen
expr_stmt|;
block|}
return|return
name|bytesInOutput
return|;
block|}
comment|/**      * Main decode method for individual chunks.      */
DECL|method|decompressChunk
specifier|public
specifier|static
name|void
name|decompressChunk
parameter_list|(
name|byte
index|[]
name|in
parameter_list|,
name|int
name|inPos
parameter_list|,
name|byte
index|[]
name|out
parameter_list|,
name|int
name|outPos
parameter_list|,
name|int
name|outEnd
parameter_list|)
throws|throws
name|IOException
block|{
do|do
block|{
name|int
name|ctrl
init|=
name|in
index|[
name|inPos
operator|++
index|]
operator|&
literal|255
decl_stmt|;
if|if
condition|(
name|ctrl
operator|<
name|LZFChunk
operator|.
name|MAX_LITERAL
condition|)
block|{
comment|// literal run
name|ctrl
operator|+=
name|inPos
expr_stmt|;
do|do
block|{
name|out
index|[
name|outPos
operator|++
index|]
operator|=
name|in
index|[
name|inPos
index|]
expr_stmt|;
block|}
do|while
condition|(
name|inPos
operator|++
operator|<
name|ctrl
condition|)
do|;
continue|continue;
block|}
comment|// back reference
name|int
name|len
init|=
name|ctrl
operator|>>
literal|5
decl_stmt|;
name|ctrl
operator|=
operator|-
operator|(
operator|(
name|ctrl
operator|&
literal|0x1f
operator|)
operator|<<
literal|8
operator|)
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|len
operator|==
literal|7
condition|)
block|{
name|len
operator|+=
name|in
index|[
name|inPos
operator|++
index|]
operator|&
literal|255
expr_stmt|;
block|}
name|ctrl
operator|-=
name|in
index|[
name|inPos
operator|++
index|]
operator|&
literal|255
expr_stmt|;
name|len
operator|+=
name|outPos
operator|+
literal|2
expr_stmt|;
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
comment|/* Odd: after extensive profiling, looks like magic number              * for unrolling is 4: with 8 performance is worse (even              * bit less than with no unrolling).              */
specifier|final
name|int
name|end
init|=
name|len
operator|-
literal|3
decl_stmt|;
while|while
condition|(
name|outPos
operator|<
name|end
condition|)
block|{
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
block|}
comment|// and, interestingly, unlooping works here too:
if|if
condition|(
name|outPos
operator|<
name|len
condition|)
block|{
comment|// max 3 bytes to copy
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
if|if
condition|(
name|outPos
operator|<
name|len
condition|)
block|{
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
if|if
condition|(
name|outPos
operator|<
name|len
condition|)
block|{
name|out
index|[
name|outPos
index|]
operator|=
name|out
index|[
name|outPos
operator|++
operator|+
name|ctrl
index|]
expr_stmt|;
block|}
block|}
block|}
block|}
do|while
condition|(
name|outPos
operator|<
name|outEnd
condition|)
do|;
comment|// sanity check to guard against corrupt data:
if|if
condition|(
name|outPos
operator|!=
name|outEnd
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Corrupt data: overrun in decompress, input offset "
operator|+
name|inPos
operator|+
literal|", output offset "
operator|+
name|outPos
argument_list|)
throw|;
block|}
DECL|method|uint16
specifier|private
specifier|final
specifier|static
name|int
name|uint16
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|ptr
parameter_list|)
block|{
return|return
operator|(
operator|(
name|data
index|[
name|ptr
index|]
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator|+
operator|(
name|data
index|[
name|ptr
operator|+
literal|1
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
DECL|method|readFully
specifier|private
specifier|final
specifier|static
name|void
name|readFully
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|boolean
name|compressed
parameter_list|,
name|byte
index|[]
name|outputBuffer
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
name|int
name|left
init|=
name|len
decl_stmt|;
while|while
condition|(
name|left
operator|>
literal|0
condition|)
block|{
name|int
name|count
init|=
name|is
operator|.
name|read
argument_list|(
name|outputBuffer
argument_list|,
name|offset
argument_list|,
name|left
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|<
literal|0
condition|)
block|{
comment|// EOF not allowed here
throw|throw
operator|new
name|IOException
argument_list|(
literal|"EOF in "
operator|+
name|len
operator|+
literal|" byte ("
operator|+
operator|(
name|compressed
condition|?
literal|""
else|:
literal|"un"
operator|)
operator|+
literal|"compressed) block: could only read "
operator|+
operator|(
name|len
operator|-
name|left
operator|)
operator|+
literal|" bytes"
argument_list|)
throw|;
block|}
name|offset
operator|+=
name|count
expr_stmt|;
name|left
operator|-=
name|count
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

