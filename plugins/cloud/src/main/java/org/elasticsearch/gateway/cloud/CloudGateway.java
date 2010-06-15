begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.cloud
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|cloud
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
name|ElasticSearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|blobstore
operator|.
name|CloudBlobStoreService
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
name|inject
operator|.
name|Inject
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
name|inject
operator|.
name|Module
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
name|FastByteArrayInputStream
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|cloud
operator|.
name|CloudIndexGatewayModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStoreContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|domain
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|domain
operator|.
name|PageSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|domain
operator|.
name|StorageMetadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|domain
operator|.
name|Location
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|jclouds
operator|.
name|blobstore
operator|.
name|options
operator|.
name|ListContainerOptions
operator|.
name|Builder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|CloudGateway
specifier|public
class|class
name|CloudGateway
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Gateway
argument_list|>
implements|implements
name|Gateway
block|{
DECL|field|blobStoreContext
specifier|private
specifier|final
name|BlobStoreContext
name|blobStoreContext
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|String
name|container
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|Location
name|location
decl_stmt|;
DECL|field|metaDataDirectory
specifier|private
specifier|final
name|String
name|metaDataDirectory
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|ByteSizeValue
name|chunkSize
decl_stmt|;
DECL|field|currentIndex
specifier|private
specifier|volatile
name|int
name|currentIndex
decl_stmt|;
DECL|method|CloudGateway
annotation|@
name|Inject
specifier|public
name|CloudGateway
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|CloudBlobStoreService
name|blobStoreService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStoreContext
operator|=
name|blobStoreService
operator|.
name|context
argument_list|()
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
literal|null
argument_list|)
expr_stmt|;
name|String
name|location
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"location"
argument_list|)
decl_stmt|;
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|location
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|Location
name|matchedLocation
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Location
argument_list|>
name|assignableLocations
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|listAssignableLocations
argument_list|()
decl_stmt|;
for|for
control|(
name|Location
name|oLocation
range|:
name|assignableLocations
control|)
block|{
if|if
condition|(
name|oLocation
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|location
argument_list|)
condition|)
block|{
name|matchedLocation
operator|=
name|oLocation
expr_stmt|;
break|break;
block|}
block|}
name|this
operator|.
name|location
operator|=
name|matchedLocation
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|location
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Not a valid location ["
operator|+
name|location
operator|+
literal|"], available locations "
operator|+
name|assignableLocations
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|container
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"container"
argument_list|)
expr_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Cloud gateway requires 'container' setting"
argument_list|)
throw|;
block|}
name|this
operator|.
name|metaDataDirectory
operator|=
name|clusterName
operator|.
name|value
argument_list|()
operator|+
literal|"/metadata"
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using location [{}], container [{}], metadata_directory [{}]"
argument_list|,
name|this
operator|.
name|location
argument_list|,
name|this
operator|.
name|container
argument_list|,
name|metaDataDirectory
argument_list|)
expr_stmt|;
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|createContainerInLocation
argument_list|(
name|this
operator|.
name|location
argument_list|,
name|container
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
DECL|method|container
specifier|public
name|String
name|container
parameter_list|()
block|{
return|return
name|this
operator|.
name|container
return|;
block|}
DECL|method|location
specifier|public
name|Location
name|location
parameter_list|()
block|{
return|return
name|this
operator|.
name|location
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
try|try
block|{
name|String
name|name
init|=
name|metaDataDirectory
operator|+
literal|"/metadata-"
operator|+
operator|(
name|currentIndex
operator|+
literal|1
operator|)
decl_stmt|;
name|BinaryXContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBinaryBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
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
name|Blob
name|blob
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|newBlob
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|blob
operator|.
name|setPayload
argument_list|(
operator|new
name|FastByteArrayInputStream
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
argument_list|)
expr_stmt|;
name|blob
operator|.
name|setContentLength
argument_list|(
name|builder
operator|.
name|unsafeBytesLength
argument_list|()
argument_list|)
expr_stmt|;
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|putBlob
argument_list|(
name|container
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|currentIndex
operator|++
expr_stmt|;
name|PageSet
argument_list|<
name|?
extends|extends
name|StorageMetadata
argument_list|>
name|pageSet
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|list
argument_list|(
name|container
argument_list|,
name|inDirectory
argument_list|(
name|metaDataDirectory
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageMetadata
name|storageMetadata
range|:
name|pageSet
control|)
block|{
if|if
condition|(
name|storageMetadata
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"metadata-"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
name|storageMetadata
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|blobStoreContext
operator|.
name|getAsyncBlobStore
argument_list|()
operator|.
name|removeBlob
argument_list|(
name|container
argument_list|,
name|storageMetadata
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"can't write new metadata file into the gateway"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
return|return
name|readMetaData
argument_list|(
name|metaDataDirectory
operator|+
literal|"/metadata-"
operator|+
name|currentIndex
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
literal|"can't read metadata file from the gateway"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|suggestIndexGateway
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|suggestIndexGateway
parameter_list|()
block|{
return|return
name|CloudIndexGatewayModule
operator|.
name|class
return|;
block|}
DECL|method|reset
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|PageSet
argument_list|<
name|?
extends|extends
name|StorageMetadata
argument_list|>
name|pageSet
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|list
argument_list|(
name|container
argument_list|,
name|inDirectory
argument_list|(
name|metaDataDirectory
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageMetadata
name|storageMetadata
range|:
name|pageSet
control|)
block|{
if|if
condition|(
name|storageMetadata
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"metadata-"
argument_list|)
condition|)
block|{
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|removeBlob
argument_list|(
name|container
argument_list|,
name|storageMetadata
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|currentIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|findLatestIndex
specifier|private
name|int
name|findLatestIndex
parameter_list|()
block|{
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
name|PageSet
argument_list|<
name|?
extends|extends
name|StorageMetadata
argument_list|>
name|pageSet
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|list
argument_list|(
name|container
argument_list|,
name|inDirectory
argument_list|(
name|metaDataDirectory
argument_list|)
operator|.
name|maxResults
argument_list|(
literal|1000
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|StorageMetadata
name|storageMetadata
range|:
name|pageSet
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
literal|"[findLatestMetadata]: Processing blob ["
operator|+
name|storageMetadata
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|storageMetadata
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"metadata-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|int
name|fileIndex
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|storageMetadata
operator|.
name|getName
argument_list|()
operator|.
name|substring
argument_list|(
name|storageMetadata
operator|.
name|getName
argument_list|()
operator|.
name|lastIndexOf
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
name|storageMetadata
operator|.
name|getName
argument_list|()
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
name|storageMetadata
operator|.
name|getName
argument_list|()
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
name|String
name|name
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
name|Blob
name|blob
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|getBlob
argument_list|(
name|container
argument_list|,
name|name
argument_list|)
decl_stmt|;
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
name|blob
operator|.
name|getContent
argument_list|()
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

