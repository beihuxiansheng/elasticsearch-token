begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress.snappy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|snappy
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SnappyCompressedStreamInput
specifier|public
specifier|abstract
class|class
name|SnappyCompressedStreamInput
extends|extends
name|CompressedStreamInput
argument_list|<
name|SnappyCompressorContext
argument_list|>
block|{
DECL|field|recycler
specifier|protected
specifier|final
name|BufferRecycler
name|recycler
decl_stmt|;
DECL|field|chunkSize
specifier|protected
name|int
name|chunkSize
decl_stmt|;
DECL|field|maxCompressedChunkLength
specifier|protected
name|int
name|maxCompressedChunkLength
decl_stmt|;
DECL|field|inputBuffer
specifier|protected
name|byte
index|[]
name|inputBuffer
decl_stmt|;
DECL|method|SnappyCompressedStreamInput
specifier|public
name|SnappyCompressedStreamInput
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|SnappyCompressorContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
name|context
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
name|recycler
operator|.
name|allocDecodeBuffer
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|chunkSize
argument_list|,
name|maxCompressedChunkLength
argument_list|)
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
name|Math
operator|.
name|max
argument_list|(
name|chunkSize
argument_list|,
name|maxCompressedChunkLength
argument_list|)
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
name|byte
index|[]
name|header
init|=
operator|new
name|byte
index|[
name|SnappyCompressor
operator|.
name|HEADER
operator|.
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readBytes
argument_list|(
name|header
argument_list|,
literal|0
argument_list|,
name|header
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|header
argument_list|,
name|SnappyCompressor
operator|.
name|HEADER
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"wrong snappy compressed header ["
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|header
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|chunkSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|this
operator|.
name|maxCompressedChunkLength
operator|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|releaseDecodeBuffer
argument_list|(
name|uncompressed
argument_list|)
expr_stmt|;
block|}
name|buf
operator|=
name|inputBuffer
expr_stmt|;
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
name|inputBuffer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

