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
name|OutputStream
import|;
end_import

begin_comment
comment|/**  * Class that handles actual encoding of individual chunks.  * Resulting chunks can be compressed or non-compressed; compression  * is only used if it actually reduces chunk size (including overhead  * of additional header bytes)  *  * @author tatu@ning.com  */
end_comment

begin_class
DECL|class|ChunkEncoder
specifier|public
class|class
name|ChunkEncoder
block|{
comment|// Beyond certain point we won't be able to compress; let's use 16 bytes as cut-off
DECL|field|MIN_BLOCK_TO_COMPRESS
specifier|private
specifier|static
specifier|final
name|int
name|MIN_BLOCK_TO_COMPRESS
init|=
literal|16
decl_stmt|;
DECL|field|MIN_HASH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MIN_HASH_SIZE
init|=
literal|256
decl_stmt|;
comment|// Not much point in bigger tables, with 8k window
DECL|field|MAX_HASH_SIZE
specifier|private
specifier|static
specifier|final
name|int
name|MAX_HASH_SIZE
init|=
literal|16384
decl_stmt|;
DECL|field|MAX_OFF
specifier|private
specifier|static
specifier|final
name|int
name|MAX_OFF
init|=
literal|1
operator|<<
literal|13
decl_stmt|;
comment|// 8k
DECL|field|MAX_REF
specifier|private
specifier|static
specifier|final
name|int
name|MAX_REF
init|=
operator|(
literal|1
operator|<<
literal|8
operator|)
operator|+
operator|(
literal|1
operator|<<
literal|3
operator|)
decl_stmt|;
comment|// 264
comment|// // Encoding tables etc
DECL|field|_recycler
specifier|private
specifier|final
name|BufferRecycler
name|_recycler
decl_stmt|;
DECL|field|_hashTable
specifier|private
name|int
index|[]
name|_hashTable
decl_stmt|;
DECL|field|_hashModulo
specifier|private
specifier|final
name|int
name|_hashModulo
decl_stmt|;
comment|/**      * Buffer in which encoded content is stored during processing      */
DECL|field|_encodeBuffer
specifier|private
name|byte
index|[]
name|_encodeBuffer
decl_stmt|;
comment|/**      * Small buffer passed to LZFChunk, needed for writing chunk header      */
DECL|field|_headerBuffer
specifier|private
name|byte
index|[]
name|_headerBuffer
decl_stmt|;
comment|/**      * @param totalLength Total encoded length; used for calculating size      *                    of hash table to use      */
DECL|method|ChunkEncoder
specifier|public
name|ChunkEncoder
parameter_list|(
name|int
name|totalLength
parameter_list|)
block|{
name|int
name|largestChunkLen
init|=
name|Math
operator|.
name|max
argument_list|(
name|totalLength
argument_list|,
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
argument_list|)
decl_stmt|;
name|int
name|suggestedHashLen
init|=
name|calcHashLen
argument_list|(
name|largestChunkLen
argument_list|)
decl_stmt|;
name|_recycler
operator|=
name|BufferRecycler
operator|.
name|instance
argument_list|()
expr_stmt|;
name|_hashTable
operator|=
name|_recycler
operator|.
name|allocEncodingHash
argument_list|(
name|suggestedHashLen
argument_list|)
expr_stmt|;
name|_hashModulo
operator|=
name|_hashTable
operator|.
name|length
operator|-
literal|1
expr_stmt|;
comment|// Ok, then, what's the worst case output buffer length?
comment|// length indicator for each 32 literals, so:
name|int
name|bufferLen
init|=
name|largestChunkLen
operator|+
operator|(
operator|(
name|largestChunkLen
operator|+
literal|31
operator|)
operator|>>
literal|5
operator|)
decl_stmt|;
name|_encodeBuffer
operator|=
name|_recycler
operator|.
name|allocEncodingBuffer
argument_list|(
name|bufferLen
argument_list|)
expr_stmt|;
block|}
comment|/*     ///////////////////////////////////////////////////////////////////////     // Public API     ///////////////////////////////////////////////////////////////////////      */
comment|/**      * Method to close once encoder is no longer in use. Note: after calling      * this method, further calls to {@link #_encodeChunk} will fail      */
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|byte
index|[]
name|buf
init|=
name|_encodeBuffer
decl_stmt|;
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
name|_encodeBuffer
operator|=
literal|null
expr_stmt|;
name|_recycler
operator|.
name|releaseEncodeBuffer
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
name|int
index|[]
name|ibuf
init|=
name|_hashTable
decl_stmt|;
if|if
condition|(
name|ibuf
operator|!=
literal|null
condition|)
block|{
name|_hashTable
operator|=
literal|null
expr_stmt|;
name|_recycler
operator|.
name|releaseEncodingHash
argument_list|(
name|ibuf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Method for compressing (or not) individual chunks      */
DECL|method|encodeChunk
specifier|public
name|LZFChunk
name|encodeChunk
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
if|if
condition|(
name|len
operator|>=
name|MIN_BLOCK_TO_COMPRESS
condition|)
block|{
comment|/* If we have non-trivial block, and can compress it by at least              * 2 bytes (since header is 2 bytes longer), let's compress:              */
name|int
name|compLen
init|=
name|tryCompress
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|offset
operator|+
name|len
argument_list|,
name|_encodeBuffer
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|compLen
operator|<
operator|(
name|len
operator|-
literal|2
operator|)
condition|)
block|{
comment|// nah; just return uncompressed
return|return
name|LZFChunk
operator|.
name|createCompressed
argument_list|(
name|len
argument_list|,
name|_encodeBuffer
argument_list|,
literal|0
argument_list|,
name|compLen
argument_list|)
return|;
block|}
block|}
comment|// Otherwise leave uncompressed:
return|return
name|LZFChunk
operator|.
name|createNonCompressed
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
return|;
block|}
comment|/**      * Method for encoding individual chunk, writing it to given output stream.      */
DECL|method|encodeAndWriteChunk
specifier|public
name|void
name|encodeAndWriteChunk
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|headerBuf
init|=
name|_headerBuffer
decl_stmt|;
if|if
condition|(
name|headerBuf
operator|==
literal|null
condition|)
block|{
name|_headerBuffer
operator|=
name|headerBuf
operator|=
operator|new
name|byte
index|[
name|LZFChunk
operator|.
name|MAX_HEADER_LEN
index|]
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|>=
name|MIN_BLOCK_TO_COMPRESS
condition|)
block|{
comment|/* If we have non-trivial block, and can compress it by at least              * 2 bytes (since header is 2 bytes longer), let's compress:              */
name|int
name|compLen
init|=
name|tryCompress
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|offset
operator|+
name|len
argument_list|,
name|_encodeBuffer
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|compLen
operator|<
operator|(
name|len
operator|-
literal|2
operator|)
condition|)
block|{
comment|// nah; just return uncompressed
name|LZFChunk
operator|.
name|writeCompressedHeader
argument_list|(
name|len
argument_list|,
name|compLen
argument_list|,
name|out
argument_list|,
name|headerBuf
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|_encodeBuffer
argument_list|,
literal|0
argument_list|,
name|compLen
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|// Otherwise leave uncompressed:
name|LZFChunk
operator|.
name|writeNonCompressedHeader
argument_list|(
name|len
argument_list|,
name|out
argument_list|,
name|headerBuf
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
comment|/*     ///////////////////////////////////////////////////////////////////////     // Internal methods     ///////////////////////////////////////////////////////////////////////      */
DECL|method|calcHashLen
specifier|private
specifier|static
name|int
name|calcHashLen
parameter_list|(
name|int
name|chunkSize
parameter_list|)
block|{
comment|// in general try get hash table size of 2x input size
name|chunkSize
operator|+=
name|chunkSize
expr_stmt|;
comment|// but no larger than max size:
if|if
condition|(
name|chunkSize
operator|>=
name|MAX_HASH_SIZE
condition|)
block|{
return|return
name|MAX_HASH_SIZE
return|;
block|}
comment|// otherwise just need to round up to nearest 2x
name|int
name|hashLen
init|=
name|MIN_HASH_SIZE
decl_stmt|;
while|while
condition|(
name|hashLen
operator|<
name|chunkSize
condition|)
block|{
name|hashLen
operator|+=
name|hashLen
expr_stmt|;
block|}
return|return
name|hashLen
return|;
block|}
DECL|method|first
specifier|private
name|int
name|first
parameter_list|(
name|byte
index|[]
name|in
parameter_list|,
name|int
name|inPos
parameter_list|)
block|{
return|return
operator|(
name|in
index|[
name|inPos
index|]
operator|<<
literal|8
operator|)
operator|+
operator|(
name|in
index|[
name|inPos
operator|+
literal|1
index|]
operator|&
literal|255
operator|)
return|;
block|}
comment|/*     private static int next(int v, byte[] in, int inPos) {         return (v<< 8) + (in[inPos + 2]& 255);     } */
DECL|method|hash
specifier|private
specifier|final
name|int
name|hash
parameter_list|(
name|int
name|h
parameter_list|)
block|{
comment|// or 184117; but this seems to give better hashing?
return|return
operator|(
operator|(
name|h
operator|*
literal|57321
operator|)
operator|>>
literal|9
operator|)
operator|&
name|_hashModulo
return|;
comment|// original lzf-c.c used this:
comment|//return (((h ^ (h<< 5))>> (24 - HLOG) - h*5)& _hashModulo;
comment|// but that didn't seem to provide better matches
block|}
DECL|method|tryCompress
specifier|private
name|int
name|tryCompress
parameter_list|(
name|byte
index|[]
name|in
parameter_list|,
name|int
name|inPos
parameter_list|,
name|int
name|inEnd
parameter_list|,
name|byte
index|[]
name|out
parameter_list|,
name|int
name|outPos
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|hashTable
init|=
name|_hashTable
decl_stmt|;
operator|++
name|outPos
expr_stmt|;
name|int
name|hash
init|=
name|first
argument_list|(
name|in
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|literals
init|=
literal|0
decl_stmt|;
name|inEnd
operator|-=
literal|4
expr_stmt|;
specifier|final
name|int
name|firstPos
init|=
name|inPos
decl_stmt|;
comment|// so that we won't have back references across block boundary
while|while
condition|(
name|inPos
operator|<
name|inEnd
condition|)
block|{
name|byte
name|p2
init|=
name|in
index|[
name|inPos
operator|+
literal|2
index|]
decl_stmt|;
comment|// next
name|hash
operator|=
operator|(
name|hash
operator|<<
literal|8
operator|)
operator|+
operator|(
name|p2
operator|&
literal|255
operator|)
expr_stmt|;
name|int
name|off
init|=
name|hash
argument_list|(
name|hash
argument_list|)
decl_stmt|;
name|int
name|ref
init|=
name|hashTable
index|[
name|off
index|]
decl_stmt|;
name|hashTable
index|[
name|off
index|]
operator|=
name|inPos
expr_stmt|;
comment|// First expected common case: no back-ref (for whatever reason)
if|if
condition|(
name|ref
operator|>=
name|inPos
comment|// can't refer forward (i.e. leftovers)
operator|||
name|ref
operator|<
name|firstPos
comment|// or to previous block
operator|||
operator|(
name|off
operator|=
name|inPos
operator|-
name|ref
operator|-
literal|1
operator|)
operator|>=
name|MAX_OFF
operator|||
name|in
index|[
name|ref
operator|+
literal|2
index|]
operator|!=
name|p2
comment|// must match hash
operator|||
name|in
index|[
name|ref
operator|+
literal|1
index|]
operator|!=
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|8
argument_list|)
operator|||
name|in
index|[
name|ref
index|]
operator|!=
call|(
name|byte
call|)
argument_list|(
name|hash
operator|>>
literal|16
argument_list|)
condition|)
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
operator|++
index|]
expr_stmt|;
name|literals
operator|++
expr_stmt|;
if|if
condition|(
name|literals
operator|==
name|LZFChunk
operator|.
name|MAX_LITERAL
condition|)
block|{
name|out
index|[
name|outPos
operator|-
literal|33
index|]
operator|=
operator|(
name|byte
operator|)
literal|31
expr_stmt|;
comment|//<= out[outPos - literals - 1] = MAX_LITERAL_MINUS_1;
name|literals
operator|=
literal|0
expr_stmt|;
name|outPos
operator|++
expr_stmt|;
block|}
continue|continue;
block|}
comment|// match
name|int
name|maxLen
init|=
name|inEnd
operator|-
name|inPos
operator|+
literal|2
decl_stmt|;
if|if
condition|(
name|maxLen
operator|>
name|MAX_REF
condition|)
block|{
name|maxLen
operator|=
name|MAX_REF
expr_stmt|;
block|}
if|if
condition|(
name|literals
operator|==
literal|0
condition|)
block|{
name|outPos
operator|--
expr_stmt|;
block|}
else|else
block|{
name|out
index|[
name|outPos
operator|-
name|literals
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|literals
operator|-
literal|1
argument_list|)
expr_stmt|;
name|literals
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|len
init|=
literal|3
decl_stmt|;
while|while
condition|(
name|len
operator|<
name|maxLen
operator|&&
name|in
index|[
name|ref
operator|+
name|len
index|]
operator|==
name|in
index|[
name|inPos
operator|+
name|len
index|]
condition|)
block|{
name|len
operator|++
expr_stmt|;
block|}
name|len
operator|-=
literal|2
expr_stmt|;
if|if
condition|(
name|len
operator|<
literal|7
condition|)
block|{
name|out
index|[
name|outPos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|off
operator|>>
literal|8
operator|)
operator|+
operator|(
name|len
operator|<<
literal|5
operator|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
index|[
name|outPos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
operator|(
name|off
operator|>>
literal|8
operator|)
operator|+
operator|(
literal|7
operator|<<
literal|5
operator|)
argument_list|)
expr_stmt|;
name|out
index|[
name|outPos
operator|++
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|len
operator|-
literal|7
argument_list|)
expr_stmt|;
block|}
name|out
index|[
name|outPos
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|off
expr_stmt|;
name|outPos
operator|++
expr_stmt|;
name|inPos
operator|+=
name|len
expr_stmt|;
name|hash
operator|=
name|first
argument_list|(
name|in
argument_list|,
name|inPos
argument_list|)
expr_stmt|;
name|hash
operator|=
operator|(
name|hash
operator|<<
literal|8
operator|)
operator|+
operator|(
name|in
index|[
name|inPos
operator|+
literal|2
index|]
operator|&
literal|255
operator|)
expr_stmt|;
name|hashTable
index|[
name|hash
argument_list|(
name|hash
argument_list|)
index|]
operator|=
name|inPos
operator|++
expr_stmt|;
name|hash
operator|=
operator|(
name|hash
operator|<<
literal|8
operator|)
operator|+
operator|(
name|in
index|[
name|inPos
operator|+
literal|2
index|]
operator|&
literal|255
operator|)
expr_stmt|;
comment|// hash = next(hash, in, inPos);
name|hashTable
index|[
name|hash
argument_list|(
name|hash
argument_list|)
index|]
operator|=
name|inPos
operator|++
expr_stmt|;
block|}
name|inEnd
operator|+=
literal|4
expr_stmt|;
comment|// try offlining the tail
return|return
name|tryCompressTail
argument_list|(
name|in
argument_list|,
name|inPos
argument_list|,
name|inEnd
argument_list|,
name|out
argument_list|,
name|outPos
argument_list|,
name|literals
argument_list|)
return|;
block|}
DECL|method|tryCompressTail
specifier|private
name|int
name|tryCompressTail
parameter_list|(
name|byte
index|[]
name|in
parameter_list|,
name|int
name|inPos
parameter_list|,
name|int
name|inEnd
parameter_list|,
name|byte
index|[]
name|out
parameter_list|,
name|int
name|outPos
parameter_list|,
name|int
name|literals
parameter_list|)
block|{
while|while
condition|(
name|inPos
operator|<
name|inEnd
condition|)
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
operator|++
index|]
expr_stmt|;
name|literals
operator|++
expr_stmt|;
if|if
condition|(
name|literals
operator|==
name|LZFChunk
operator|.
name|MAX_LITERAL
condition|)
block|{
name|out
index|[
name|outPos
operator|-
name|literals
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|literals
operator|-
literal|1
argument_list|)
expr_stmt|;
name|literals
operator|=
literal|0
expr_stmt|;
name|outPos
operator|++
expr_stmt|;
block|}
block|}
name|out
index|[
name|outPos
operator|-
name|literals
operator|-
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|literals
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|literals
operator|==
literal|0
condition|)
block|{
name|outPos
operator|--
expr_stmt|;
block|}
return|return
name|outPos
return|;
block|}
block|}
end_class

end_unit

