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
name|Compressor
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
name|Streams
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
name|BytesStreamInput
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
name|CachedStreamOutput
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
name|StreamOutput
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|ByteSizeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
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
DECL|class|SnappyCompressor
specifier|public
specifier|abstract
class|class
name|SnappyCompressor
implements|implements
name|Compressor
block|{
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|HEADER
init|=
block|{
literal|'s'
block|,
literal|'n'
block|,
literal|'a'
block|,
literal|'p'
block|,
literal|'p'
block|,
literal|'y'
block|,
literal|0
block|}
decl_stmt|;
DECL|field|compressorContext
specifier|protected
name|SnappyCompressorContext
name|compressorContext
decl_stmt|;
comment|// default block size (32k)
DECL|field|DEFAULT_CHUNK_SIZE
specifier|static
specifier|final
name|int
name|DEFAULT_CHUNK_SIZE
init|=
literal|1
operator|<<
literal|15
decl_stmt|;
DECL|method|SnappyCompressor
specifier|protected
name|SnappyCompressor
parameter_list|()
block|{
name|this
operator|.
name|compressorContext
operator|=
operator|new
name|SnappyCompressorContext
argument_list|(
name|DEFAULT_CHUNK_SIZE
argument_list|,
name|maxCompressedLength
argument_list|(
name|DEFAULT_CHUNK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|public
name|void
name|configure
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|int
name|chunkLength
init|=
operator|(
name|int
operator|)
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"compress.snappy.chunk_size"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|compressorContext
operator|.
name|compressChunkLength
argument_list|()
argument_list|)
argument_list|)
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|int
name|maxCompressedChunkLength
init|=
name|maxCompressedLength
argument_list|(
name|chunkLength
argument_list|)
decl_stmt|;
name|this
operator|.
name|compressorContext
operator|=
operator|new
name|SnappyCompressorContext
argument_list|(
name|chunkLength
argument_list|,
name|maxCompressedChunkLength
argument_list|)
expr_stmt|;
block|}
DECL|method|maxCompressedLength
specifier|protected
specifier|abstract
name|int
name|maxCompressedLength
parameter_list|(
name|int
name|length
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|isCompressed
specifier|public
name|boolean
name|isCompressed
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|<
name|HEADER
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|HEADER
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|data
index|[
name|offset
operator|+
name|i
index|]
operator|!=
name|HEADER
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isCompressed
specifier|public
name|boolean
name|isCompressed
parameter_list|(
name|ChannelBuffer
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|readableBytes
argument_list|()
operator|<
name|HEADER
operator|.
name|length
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|offset
init|=
name|buffer
operator|.
name|readerIndex
argument_list|()
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
name|HEADER
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|buffer
operator|.
name|getByte
argument_list|(
name|offset
operator|+
name|i
argument_list|)
operator|!=
name|HEADER
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|isCompressed
specifier|public
name|boolean
name|isCompressed
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|currentPointer
init|=
name|in
operator|.
name|getFilePointer
argument_list|()
decl_stmt|;
comment|// since we have some metdata before the first compressed header, we check on our specific header
if|if
condition|(
name|in
operator|.
name|length
argument_list|()
operator|-
name|currentPointer
operator|<
operator|(
name|HEADER
operator|.
name|length
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|HEADER
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|in
operator|.
name|readByte
argument_list|()
operator|!=
name|HEADER
index|[
name|i
index|]
condition|)
block|{
name|in
operator|.
name|seek
argument_list|(
name|currentPointer
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
name|in
operator|.
name|seek
argument_list|(
name|currentPointer
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|compress
specifier|public
name|byte
index|[]
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
name|length
parameter_list|)
throws|throws
name|IOException
block|{
comment|// this needs to be the same format as regular streams reading from it!
name|CachedStreamOutput
operator|.
name|Entry
name|entry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
try|try
block|{
name|StreamOutput
name|compressed
init|=
name|entry
operator|.
name|bytes
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|compressed
operator|.
name|writeBytes
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|compressed
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|entry
operator|.
name|bytes
argument_list|()
operator|.
name|copiedByteArray
argument_list|()
return|;
block|}
finally|finally
block|{
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|uncompress
specifier|public
name|byte
index|[]
name|uncompress
parameter_list|(
name|byte
index|[]
name|data
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
name|StreamInput
name|compressed
init|=
name|streamInput
argument_list|(
operator|new
name|BytesStreamInput
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|CachedStreamOutput
operator|.
name|Entry
name|entry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
try|try
block|{
name|Streams
operator|.
name|copy
argument_list|(
name|compressed
argument_list|,
name|entry
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|bytes
argument_list|()
operator|.
name|copiedByteArray
argument_list|()
return|;
block|}
finally|finally
block|{
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

