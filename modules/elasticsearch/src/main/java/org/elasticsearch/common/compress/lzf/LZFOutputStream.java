begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  * @author jon hartlaub  * @author tatu  */
end_comment

begin_class
DECL|class|LZFOutputStream
specifier|public
class|class
name|LZFOutputStream
extends|extends
name|OutputStream
block|{
DECL|field|OUTPUT_BUFFER_SIZE
specifier|private
specifier|static
name|int
name|OUTPUT_BUFFER_SIZE
init|=
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
decl_stmt|;
DECL|field|_encoder
specifier|private
specifier|final
name|ChunkEncoder
name|_encoder
decl_stmt|;
DECL|field|_recycler
specifier|private
specifier|final
name|BufferRecycler
name|_recycler
decl_stmt|;
DECL|field|_outputStream
specifier|protected
specifier|final
name|OutputStream
name|_outputStream
decl_stmt|;
DECL|field|_outputBuffer
specifier|protected
name|byte
index|[]
name|_outputBuffer
decl_stmt|;
DECL|field|_position
specifier|protected
name|int
name|_position
init|=
literal|0
decl_stmt|;
DECL|method|LZFOutputStream
specifier|public
name|LZFOutputStream
parameter_list|(
specifier|final
name|OutputStream
name|outputStream
parameter_list|)
block|{
name|_encoder
operator|=
operator|new
name|ChunkEncoder
argument_list|(
name|OUTPUT_BUFFER_SIZE
argument_list|)
expr_stmt|;
name|_recycler
operator|=
name|BufferRecycler
operator|.
name|instance
argument_list|()
expr_stmt|;
name|_outputStream
operator|=
name|outputStream
expr_stmt|;
name|_outputBuffer
operator|=
name|_recycler
operator|.
name|allocOutputBuffer
argument_list|(
name|OUTPUT_BUFFER_SIZE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|int
name|singleByte
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|_position
operator|>=
name|_outputBuffer
operator|.
name|length
condition|)
block|{
name|writeCompressedBlock
argument_list|()
expr_stmt|;
block|}
name|_outputBuffer
index|[
name|_position
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|singleByte
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|buffer
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|BUFFER_LEN
init|=
name|_outputBuffer
operator|.
name|length
decl_stmt|;
comment|// simple case first: buffering only (for trivially short writes)
name|int
name|free
init|=
name|BUFFER_LEN
operator|-
name|_position
decl_stmt|;
if|if
condition|(
name|free
operator|>=
name|length
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|_outputBuffer
argument_list|,
name|_position
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|_position
operator|+=
name|length
expr_stmt|;
return|return;
block|}
comment|// otherwise, copy whatever we can, flush
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|_outputBuffer
argument_list|,
name|_position
argument_list|,
name|free
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|free
expr_stmt|;
name|length
operator|-=
name|free
expr_stmt|;
name|_position
operator|+=
name|free
expr_stmt|;
name|writeCompressedBlock
argument_list|()
expr_stmt|;
comment|// then write intermediate full block, if any, without copying:
while|while
condition|(
name|length
operator|>=
name|BUFFER_LEN
condition|)
block|{
name|_encoder
operator|.
name|encodeAndWriteChunk
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|BUFFER_LEN
argument_list|,
name|_outputStream
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|BUFFER_LEN
expr_stmt|;
name|length
operator|-=
name|BUFFER_LEN
expr_stmt|;
block|}
comment|// and finally, copy leftovers in buffer, if any
if|if
condition|(
name|length
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
name|offset
argument_list|,
name|_outputBuffer
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
name|_position
operator|=
name|length
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|_position
operator|>
literal|0
condition|)
block|{
name|writeCompressedBlock
argument_list|()
expr_stmt|;
block|}
name|_outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
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
name|flush
argument_list|()
expr_stmt|;
name|_outputStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|_encoder
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|buf
init|=
name|_outputBuffer
decl_stmt|;
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
name|_outputBuffer
operator|=
literal|null
expr_stmt|;
name|_recycler
operator|.
name|releaseOutputBuffer
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Compress and write the current block to the OutputStream      */
DECL|method|writeCompressedBlock
specifier|private
name|void
name|writeCompressedBlock
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|left
init|=
name|_position
decl_stmt|;
name|_position
operator|=
literal|0
expr_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
do|do
block|{
name|int
name|chunkLen
init|=
name|Math
operator|.
name|min
argument_list|(
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
argument_list|,
name|left
argument_list|)
decl_stmt|;
name|_encoder
operator|.
name|encodeAndWriteChunk
argument_list|(
name|_outputBuffer
argument_list|,
literal|0
argument_list|,
name|chunkLen
argument_list|,
name|_outputStream
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|chunkLen
expr_stmt|;
name|left
operator|-=
name|chunkLen
expr_stmt|;
block|}
do|while
condition|(
name|left
operator|>
literal|0
condition|)
do|;
block|}
block|}
end_class

end_unit

