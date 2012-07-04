begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|BytesHolder
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
name|collect
operator|.
name|MapBuilder
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
name|compress
operator|.
name|snappy
operator|.
name|UnavailableSnappyCompressor
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
name|snappy
operator|.
name|xerial
operator|.
name|XerialSnappy
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
name|snappy
operator|.
name|xerial
operator|.
name|XerialSnappyCompressor
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
name|logging
operator|.
name|Loggers
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
DECL|field|LZF
specifier|private
specifier|static
specifier|final
name|LZFCompressor
name|LZF
init|=
operator|new
name|LZFCompressor
argument_list|()
decl_stmt|;
DECL|field|compressors
specifier|private
specifier|static
specifier|final
name|Compressor
index|[]
name|compressors
decl_stmt|;
DECL|field|compressorsByType
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Compressor
argument_list|>
name|compressorsByType
decl_stmt|;
DECL|field|defaultCompressor
specifier|private
specifier|static
name|Compressor
name|defaultCompressor
decl_stmt|;
static|static
block|{
name|List
argument_list|<
name|Compressor
argument_list|>
name|compressorsX
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|compressorsX
operator|.
name|add
argument_list|(
name|LZF
argument_list|)
expr_stmt|;
name|boolean
name|addedSnappy
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|XerialSnappy
operator|.
name|available
condition|)
block|{
name|compressorsX
operator|.
name|add
argument_list|(
operator|new
name|XerialSnappyCompressor
argument_list|()
argument_list|)
expr_stmt|;
name|addedSnappy
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|Loggers
operator|.
name|getLogger
argument_list|(
name|CompressorFactory
operator|.
name|class
argument_list|)
operator|.
name|debug
argument_list|(
literal|"failed to load xerial snappy-java"
argument_list|,
name|XerialSnappy
operator|.
name|failure
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|addedSnappy
condition|)
block|{
name|compressorsX
operator|.
name|add
argument_list|(
operator|new
name|UnavailableSnappyCompressor
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|compressors
operator|=
name|compressorsX
operator|.
name|toArray
argument_list|(
operator|new
name|Compressor
index|[
name|compressorsX
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|Compressor
argument_list|>
name|compressorsByTypeX
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Compressor
name|compressor
range|:
name|compressors
control|)
block|{
name|compressorsByTypeX
operator|.
name|put
argument_list|(
name|compressor
operator|.
name|type
argument_list|()
argument_list|,
name|compressor
argument_list|)
expr_stmt|;
block|}
name|compressorsByType
operator|=
name|compressorsByTypeX
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|defaultCompressor
operator|=
name|LZF
expr_stmt|;
block|}
DECL|method|configure
specifier|public
specifier|static
specifier|synchronized
name|void
name|configure
parameter_list|(
name|Settings
name|settings
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
name|compressor
operator|.
name|configure
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
name|String
name|defaultType
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"compress.default.type"
argument_list|,
literal|"lzf"
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
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
name|defaultType
operator|.
name|equalsIgnoreCase
argument_list|(
name|compressor
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
name|defaultCompressor
operator|=
name|compressor
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|Loggers
operator|.
name|getLogger
argument_list|(
name|CompressorFactory
operator|.
name|class
argument_list|)
operator|.
name|warn
argument_list|(
literal|"failed to find default type [{}]"
argument_list|,
name|defaultType
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setDefaultCompressor
specifier|public
specifier|static
specifier|synchronized
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
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|compressor
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|isCompressed
specifier|public
specifier|static
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
return|return
name|compressor
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
operator|!=
literal|null
return|;
block|}
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
name|BytesHolder
name|bytes
parameter_list|)
block|{
return|return
name|compressor
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|()
argument_list|,
name|bytes
operator|.
name|offset
argument_list|()
argument_list|,
name|bytes
operator|.
name|length
argument_list|()
argument_list|)
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
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|compressor
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
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
name|data
argument_list|,
name|offset
argument_list|,
name|length
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
annotation|@
name|Nullable
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
return|return
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
DECL|method|compressor
specifier|public
specifier|static
name|Compressor
name|compressor
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|compressorsByType
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/**      * Uncompress the provided data, data can be detected as compressed using {@link #isCompressed(byte[], int, int)}.      */
DECL|method|uncompressIfNeeded
specifier|public
specifier|static
name|BytesHolder
name|uncompressIfNeeded
parameter_list|(
name|BytesHolder
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
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|BytesHolder
argument_list|(
name|compressor
operator|.
name|uncompress
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|()
argument_list|,
name|bytes
operator|.
name|offset
argument_list|()
argument_list|,
name|bytes
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|bytes
return|;
block|}
block|}
end_class

end_unit

