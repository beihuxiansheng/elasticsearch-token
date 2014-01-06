begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|BufferRecycler
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|ChunkDecoder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|ning
operator|.
name|compress
operator|.
name|lzf
operator|.
name|LZFChunk
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
name|compress
operator|.
name|CompressedStreamInput
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
name|IOException
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|LZFCompressedStreamInput
specifier|public
class|class
name|LZFCompressedStreamInput
extends|extends
name|CompressedStreamInput
argument_list|<
name|LZFCompressorContext
argument_list|>
block|{
DECL|field|recycler
specifier|private
specifier|final
name|BufferRecycler
name|recycler
decl_stmt|;
DECL|field|decoder
specifier|private
specifier|final
name|ChunkDecoder
name|decoder
decl_stmt|;
comment|// scratch area buffer
DECL|field|inputBuffer
specifier|private
name|byte
index|[]
name|inputBuffer
decl_stmt|;
DECL|method|LZFCompressedStreamInput
specifier|public
name|LZFCompressedStreamInput
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|ChunkDecoder
name|decoder
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|LZFCompressorContext
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|this
operator|.
name|recycler
operator|=
name|BufferRecycler
operator|.
name|instance
argument_list|()
expr_stmt|;
name|this
operator|.
name|decoder
operator|=
name|decoder
expr_stmt|;
name|this
operator|.
name|uncompressed
operator|=
name|recycler
operator|.
name|allocDecodeBuffer
argument_list|(
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputBuffer
operator|=
name|recycler
operator|.
name|allocInputBuffer
argument_list|(
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readHeader
specifier|public
name|void
name|readHeader
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do here, each chunk has a header
block|}
annotation|@
name|Override
DECL|method|uncompress
specifier|public
name|int
name|uncompress
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|byte
index|[]
name|out
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|decoder
operator|.
name|decodeChunk
argument_list|(
name|in
argument_list|,
name|inputBuffer
argument_list|,
name|out
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|buf
init|=
name|inputBuffer
decl_stmt|;
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
name|inputBuffer
operator|=
literal|null
expr_stmt|;
name|recycler
operator|.
name|releaseInputBuffer
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
name|buf
operator|=
name|uncompressed
expr_stmt|;
if|if
condition|(
name|buf
operator|!=
literal|null
condition|)
block|{
name|uncompressed
operator|=
literal|null
expr_stmt|;
name|recycler
operator|.
name|releaseDecodeBuffer
argument_list|(
name|uncompressed
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

