begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.compress
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
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
name|Nullable
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
name|bytes
operator|.
name|BytesReference
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
name|deflate
operator|.
name|DeflateCompressor
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
name|lzf
operator|.
name|LZFCompressor
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
name|BytesStreamOutput
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentType
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
DECL|class|CompressorFactory
specifier|public
class|class
name|CompressorFactory
block|{
DECL|field|compressors
specifier|private
specifier|static
specifier|final
name|Compressor
index|[]
name|compressors
decl_stmt|;
DECL|field|defaultCompressor
specifier|private
specifier|static
specifier|volatile
name|Compressor
name|defaultCompressor
decl_stmt|;
static|static
block|{
name|compressors
operator|=
operator|new
name|Compressor
index|[]
block|{
operator|new
name|LZFCompressor
argument_list|()
block|,
operator|new
name|DeflateCompressor
argument_list|()
block|}
expr_stmt|;
name|defaultCompressor
operator|=
operator|new
name|DeflateCompressor
argument_list|()
expr_stmt|;
block|}
DECL|method|setDefaultCompressor
specifier|public
specifier|static
name|void
name|setDefaultCompressor
parameter_list|(
name|Compressor
name|defaultCompressor
parameter_list|)
block|{
name|CompressorFactory
operator|.
name|defaultCompressor
operator|=
name|defaultCompressor
expr_stmt|;
block|}
DECL|method|defaultCompressor
specifier|public
specifier|static
name|Compressor
name|defaultCompressor
parameter_list|()
block|{
return|return
name|defaultCompressor
return|;
block|}
DECL|method|isCompressed
specifier|public
specifier|static
name|boolean
name|isCompressed
parameter_list|(
name|BytesReference
name|bytes
parameter_list|)
block|{
return|return
name|compressor
argument_list|(
name|bytes
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**      * @deprecated we don't compress lucene indexes anymore and rely on lucene codecs      */
annotation|@
name|Deprecated
DECL|method|isCompressed
specifier|public
specifier|static
name|boolean
name|isCompressed
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compressor
argument_list|(
name|in
argument_list|)
operator|!=
literal|null
return|;
block|}
annotation|@
name|Nullable
DECL|method|compressor
specifier|public
specifier|static
name|Compressor
name|compressor
parameter_list|(
name|BytesReference
name|bytes
parameter_list|)
block|{
for|for
control|(
name|Compressor
name|compressor
range|:
name|compressors
control|)
block|{
if|if
condition|(
name|compressor
operator|.
name|isCompressed
argument_list|(
name|bytes
argument_list|)
condition|)
block|{
comment|// bytes should be either detected as compressed or as xcontent,
comment|// if we have bytes that can be either detected as compressed or
comment|// as a xcontent, we have a problem
assert|assert
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
operator|==
literal|null
assert|;
return|return
name|compressor
return|;
block|}
block|}
name|XContentType
name|contentType
init|=
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotXContentException
argument_list|(
literal|"Compressor detection can only be called on some xcontent bytes or compressed xcontent bytes"
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|compressor
specifier|public
specifier|static
name|Compressor
name|compressor
parameter_list|(
name|ChannelBuffer
name|buffer
parameter_list|)
block|{
for|for
control|(
name|Compressor
name|compressor
range|:
name|compressors
control|)
block|{
if|if
condition|(
name|compressor
operator|.
name|isCompressed
argument_list|(
name|buffer
argument_list|)
condition|)
block|{
return|return
name|compressor
return|;
block|}
block|}
throw|throw
operator|new
name|NotCompressedException
argument_list|()
throw|;
block|}
comment|/**      * @deprecated we don't compress lucene indexes anymore and rely on lucene codecs      */
annotation|@
name|Deprecated
annotation|@
name|Nullable
DECL|method|compressor
specifier|public
specifier|static
name|Compressor
name|compressor
parameter_list|(
name|IndexInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Compressor
name|compressor
range|:
name|compressors
control|)
block|{
if|if
condition|(
name|compressor
operator|.
name|isCompressed
argument_list|(
name|in
argument_list|)
condition|)
block|{
return|return
name|compressor
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Uncompress the provided data, data can be detected as compressed using {@link #isCompressed(byte[], int, int)}.      */
DECL|method|uncompressIfNeeded
specifier|public
specifier|static
name|BytesReference
name|uncompressIfNeeded
parameter_list|(
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|compressor
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|BytesReference
name|uncompressed
decl_stmt|;
if|if
condition|(
name|compressor
operator|!=
literal|null
condition|)
block|{
name|uncompressed
operator|=
name|uncompress
argument_list|(
name|bytes
argument_list|,
name|compressor
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|uncompressed
operator|=
name|bytes
expr_stmt|;
block|}
return|return
name|uncompressed
return|;
block|}
comment|/** Decompress the provided {@link BytesReference}. */
DECL|method|uncompress
specifier|public
specifier|static
name|BytesReference
name|uncompress
parameter_list|(
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Compressor
name|compressor
init|=
name|compressor
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|compressor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotCompressedException
argument_list|()
throw|;
block|}
return|return
name|uncompress
argument_list|(
name|bytes
argument_list|,
name|compressor
argument_list|)
return|;
block|}
DECL|method|uncompress
specifier|private
specifier|static
name|BytesReference
name|uncompress
parameter_list|(
name|BytesReference
name|bytes
parameter_list|,
name|Compressor
name|compressor
parameter_list|)
throws|throws
name|IOException
block|{
name|StreamInput
name|compressed
init|=
name|compressor
operator|.
name|streamInput
argument_list|(
name|bytes
operator|.
name|streamInput
argument_list|()
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|bStream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|Streams
operator|.
name|copy
argument_list|(
name|compressed
argument_list|,
name|bStream
argument_list|)
expr_stmt|;
name|compressed
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|bStream
operator|.
name|bytes
argument_list|()
return|;
block|}
block|}
end_class

end_unit
