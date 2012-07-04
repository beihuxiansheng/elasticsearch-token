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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|CompressedIndexInput
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
name|InputStreamIndexInput
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
DECL|class|LZFCompressedIndexInput
specifier|public
class|class
name|LZFCompressedIndexInput
extends|extends
name|CompressedIndexInput
argument_list|<
name|LZFCompressorContext
argument_list|>
block|{
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
DECL|method|LZFCompressedIndexInput
specifier|public
name|LZFCompressedIndexInput
parameter_list|(
name|IndexInput
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
name|decoder
operator|=
name|decoder
expr_stmt|;
name|this
operator|.
name|uncompressed
operator|=
operator|new
name|byte
index|[
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
index|]
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
name|inputBuffer
operator|=
operator|new
name|byte
index|[
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
index|]
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readHeader
specifier|protected
name|void
name|readHeader
parameter_list|(
name|IndexInput
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
name|LZFCompressor
operator|.
name|LUCENE_HEADER
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
argument_list|,
literal|false
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
name|LZFCompressor
operator|.
name|LUCENE_HEADER
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"wrong lzf compressed header ["
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
block|}
annotation|@
name|Override
DECL|method|uncompress
specifier|protected
name|int
name|uncompress
parameter_list|(
name|IndexInput
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
operator|new
name|InputStreamIndexInput
argument_list|(
name|in
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
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
comment|// nothing to do here...
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|Object
name|clone
parameter_list|()
block|{
name|LZFCompressedIndexInput
name|cloned
init|=
operator|(
name|LZFCompressedIndexInput
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|cloned
operator|.
name|inputBuffer
operator|=
operator|new
name|byte
index|[
name|LZFChunk
operator|.
name|MAX_CHUNK_LEN
index|]
expr_stmt|;
return|return
name|cloned
return|;
block|}
block|}
end_class

end_unit

