begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|blobstore
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|blobstore
operator|.
name|*
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
name|ImmutableMap
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
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
name|XContentParser
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
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|BinaryXContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|Gateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|GatewayException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BlobStoreGateway
specifier|public
specifier|abstract
class|class
name|BlobStoreGateway
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Gateway
argument_list|>
implements|implements
name|Gateway
block|{
DECL|field|blobStore
specifier|private
name|BlobStore
name|blobStore
decl_stmt|;
DECL|field|chunkSize
specifier|private
name|ByteSizeValue
name|chunkSize
decl_stmt|;
DECL|field|basePath
specifier|private
name|BlobPath
name|basePath
decl_stmt|;
DECL|field|metaDataBlobContainer
specifier|private
name|ImmutableBlobContainer
name|metaDataBlobContainer
decl_stmt|;
DECL|field|currentIndex
specifier|private
specifier|volatile
name|int
name|currentIndex
decl_stmt|;
DECL|method|BlobStoreGateway
specifier|protected
name|BlobStoreGateway
parameter_list|(
name|Settings
name|settings
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize
specifier|protected
name|void
name|initialize
parameter_list|(
name|BlobStore
name|blobStore
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
annotation|@
name|Nullable
name|ByteSizeValue
name|defaultChunkSize
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
name|defaultChunkSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|basePath
operator|=
name|BlobPath
operator|.
name|cleanPath
argument_list|()
operator|.
name|add
argument_list|(
name|clusterName
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|metaDataBlobContainer
operator|=
name|blobStore
operator|.
name|immutableBlobContainer
argument_list|(
name|basePath
operator|.
name|add
argument_list|(
literal|"metadata"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentIndex
operator|=
name|findLatestIndex
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Latest metadata found at index ["
operator|+
name|currentIndex
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|toString
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|type
argument_list|()
operator|+
literal|"://"
operator|+
name|blobStore
operator|+
literal|"/"
operator|+
name|basePath
return|;
block|}
DECL|method|blobStore
specifier|public
name|BlobStore
name|blobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
DECL|method|basePath
specifier|public
name|BlobPath
name|basePath
parameter_list|()
block|{
return|return
name|basePath
return|;
block|}
DECL|method|chunkSize
specifier|public
name|ByteSizeValue
name|chunkSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|chunkSize
return|;
block|}
DECL|method|reset
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|Exception
block|{
name|blobStore
operator|.
name|delete
argument_list|(
name|BlobPath
operator|.
name|cleanPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|read
annotation|@
name|Override
specifier|public
name|MetaData
name|read
parameter_list|()
throws|throws
name|GatewayException
block|{
try|try
block|{
name|this
operator|.
name|currentIndex
operator|=
name|findLatestIndex
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GatewayException
argument_list|(
literal|"Failed to find latest metadata to read from"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|currentIndex
operator|==
operator|-
literal|1
condition|)
return|return
literal|null
return|;
name|String
name|metaData
init|=
literal|"metadata-"
operator|+
name|currentIndex
decl_stmt|;
try|try
block|{
return|return
name|readMetaData
argument_list|(
name|metaDataBlobContainer
operator|.
name|readBlobFully
argument_list|(
name|metaData
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|GatewayException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GatewayException
argument_list|(
literal|"Failed to read metadata ["
operator|+
name|metaData
operator|+
literal|"] from gateway"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|write
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|MetaData
name|metaData
parameter_list|)
throws|throws
name|GatewayException
block|{
specifier|final
name|String
name|newMetaData
init|=
literal|"metadata-"
operator|+
operator|(
name|currentIndex
operator|+
literal|1
operator|)
decl_stmt|;
name|BinaryXContentBuilder
name|builder
decl_stmt|;
try|try
block|{
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBinaryBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|builder
operator|.
name|prettyPrint
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|MetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|metaData
argument_list|,
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GatewayException
argument_list|(
literal|"Failed to serialize metadata into gateway"
argument_list|,
name|e
argument_list|)
throw|;
block|}
try|try
block|{
name|metaDataBlobContainer
operator|.
name|writeBlob
argument_list|(
name|newMetaData
argument_list|,
operator|new
name|ByteArrayInputStream
argument_list|(
name|builder
operator|.
name|unsafeBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|builder
operator|.
name|unsafeBytesLength
argument_list|()
argument_list|)
argument_list|,
name|builder
operator|.
name|unsafeBytesLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|GatewayException
argument_list|(
literal|"Failed to write metadata ["
operator|+
name|newMetaData
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|currentIndex
operator|++
expr_stmt|;
try|try
block|{
name|metaDataBlobContainer
operator|.
name|deleteBlobsByFilter
argument_list|(
operator|new
name|BlobContainer
operator|.
name|BlobNameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|String
name|blobName
parameter_list|)
block|{
return|return
name|blobName
operator|.
name|startsWith
argument_list|(
literal|"metadata-"
argument_list|)
operator|&&
operator|!
name|newMetaData
operator|.
name|equals
argument_list|(
name|blobName
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Failed to delete old metadata, will do it next time"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|findLatestIndex
specifier|private
name|int
name|findLatestIndex
parameter_list|()
throws|throws
name|IOException
block|{
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|blobs
init|=
name|metaDataBlobContainer
operator|.
name|listBlobsByPrefix
argument_list|(
literal|"metadata-"
argument_list|)
decl_stmt|;
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|BlobMetaData
name|md
range|:
name|blobs
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[findLatestMetadata]: Processing ["
operator|+
name|md
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|md
operator|.
name|name
argument_list|()
decl_stmt|;
name|int
name|fileIndex
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileIndex
operator|>=
name|index
condition|)
block|{
comment|// try and read the meta data
try|try
block|{
name|readMetaData
argument_list|(
name|metaDataBlobContainer
operator|.
name|readBlobFully
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|fileIndex
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[findLatestMetadata]: Failed to read metadata from ["
operator|+
name|name
operator|+
literal|"], ignoring..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|index
return|;
block|}
DECL|method|readMetaData
specifier|private
name|MetaData
name|readMetaData
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
expr_stmt|;
return|return
name|MetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
name|settings
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

