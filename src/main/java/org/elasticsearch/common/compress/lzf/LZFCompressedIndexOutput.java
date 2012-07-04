begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ChunkEncoder
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
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexOutput
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
name|CompressedIndexOutput
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
name|lucene
operator|.
name|store
operator|.
name|OutputStreamIndexOutput
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
DECL|class|LZFCompressedIndexOutput
specifier|public
class|class
name|LZFCompressedIndexOutput
extends|extends
name|CompressedIndexOutput
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
DECL|field|encoder
specifier|private
specifier|final
name|ChunkEncoder
name|encoder
decl_stmt|;
DECL|method|LZFCompressedIndexOutput
specifier|public
name|LZFCompressedIndexOutput
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
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
name|uncompressed
operator|=
name|this
operator|.
name|recycler
operator|.
name|allocOutputBuffer
argument_list|(
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
argument_list|)
expr_stmt|;
name|this
operator|.
name|uncompressedLength
operator|=
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
expr_stmt|;
name|this
operator|.
name|encoder
operator|=
operator|new
name|ChunkEncoder
argument_list|(
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeHeader
specifier|protected
name|void
name|writeHeader
parameter_list|(
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeBytes
argument_list|(
name|LZFCompressor
operator|.
name|LUCENE_HEADER
argument_list|,
name|LZFCompressor
operator|.
name|LUCENE_HEADER
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compress
specifier|protected
name|void
name|compress
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
name|IndexOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|encoder
operator|.
name|encodeAndWriteChunk
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|len
argument_list|,
operator|new
name|OutputStreamIndexOutput
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
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
name|uncompressed
decl_stmt|;
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
name|releaseOutputBuffer
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
name|encoder
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

