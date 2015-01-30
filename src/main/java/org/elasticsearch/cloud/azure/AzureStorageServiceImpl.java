begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|BlobConfiguration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|BlobContract
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|BlobService
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|client
operator|.
name|CloudBlobClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|client
operator|.
name|CloudBlobContainer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|client
operator|.
name|CloudBlockBlob
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|client
operator|.
name|ListBlobItem
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|models
operator|.
name|BlobProperties
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|models
operator|.
name|GetBlobResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|models
operator|.
name|ListBlobsOptions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|blob
operator|.
name|models
operator|.
name|ListBlobsResult
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|core
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|core
operator|.
name|ServiceException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|core
operator|.
name|storage
operator|.
name|CloudStorageAccount
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|windowsazure
operator|.
name|services
operator|.
name|core
operator|.
name|storage
operator|.
name|StorageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|BlobMetaData
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
name|support
operator|.
name|PlainBlobMetaData
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
name|settings
operator|.
name|SettingsFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AzureStorageServiceImpl
specifier|public
class|class
name|AzureStorageServiceImpl
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|AzureStorageServiceImpl
argument_list|>
implements|implements
name|AzureStorageService
block|{
DECL|field|account
specifier|private
specifier|final
name|String
name|account
decl_stmt|;
DECL|field|key
specifier|private
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|blob
specifier|private
specifier|final
name|String
name|blob
decl_stmt|;
DECL|field|storage_account
specifier|private
name|CloudStorageAccount
name|storage_account
decl_stmt|;
DECL|field|client
specifier|private
name|CloudBlobClient
name|client
decl_stmt|;
DECL|field|service
specifier|private
name|BlobContract
name|service
decl_stmt|;
annotation|@
name|Inject
DECL|method|AzureStorageServiceImpl
specifier|public
name|AzureStorageServiceImpl
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|SettingsFilter
name|settingsFilter
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|settingsFilter
operator|.
name|addFilter
argument_list|(
operator|new
name|AzureSettingsFilter
argument_list|()
argument_list|)
expr_stmt|;
comment|// We try to load storage API settings from `cloud.azure.`
name|account
operator|=
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.azure."
operator|+
name|Fields
operator|.
name|ACCOUNT
argument_list|)
expr_stmt|;
name|key
operator|=
name|settings
operator|.
name|get
argument_list|(
literal|"cloud.azure."
operator|+
name|Fields
operator|.
name|KEY
argument_list|)
expr_stmt|;
name|blob
operator|=
literal|"http://"
operator|+
name|account
operator|+
literal|".blob.core.windows.net/"
expr_stmt|;
try|try
block|{
if|if
condition|(
name|account
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"creating new Azure storage client using account [{}], key [{}], blob [{}]"
argument_list|,
name|account
argument_list|,
name|key
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|String
name|storageConnectionString
init|=
literal|"DefaultEndpointsProtocol=http;"
operator|+
literal|"AccountName="
operator|+
name|account
operator|+
literal|";"
operator|+
literal|"AccountKey="
operator|+
name|key
decl_stmt|;
name|Configuration
name|configuration
init|=
name|Configuration
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|setProperty
argument_list|(
name|BlobConfiguration
operator|.
name|ACCOUNT_NAME
argument_list|,
name|account
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setProperty
argument_list|(
name|BlobConfiguration
operator|.
name|ACCOUNT_KEY
argument_list|,
name|key
argument_list|)
expr_stmt|;
name|configuration
operator|.
name|setProperty
argument_list|(
name|BlobConfiguration
operator|.
name|URI
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|service
operator|=
name|BlobService
operator|.
name|create
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
name|storage_account
operator|=
name|CloudStorageAccount
operator|.
name|parse
argument_list|(
name|storageConnectionString
argument_list|)
expr_stmt|;
name|client
operator|=
name|storage_account
operator|.
name|createCloudBlobClient
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Can not start Azure Storage Client
name|logger
operator|.
name|error
argument_list|(
literal|"can not start azure storage client: {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doesContainerExist
specifier|public
name|boolean
name|doesContainerExist
parameter_list|(
name|String
name|container
parameter_list|)
block|{
try|try
block|{
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
return|return
name|blob_container
operator|.
name|exists
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"can not access container [{}]"
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|removeContainer
specifier|public
name|void
name|removeContainer
parameter_list|(
name|String
name|container
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
comment|// TODO Should we set some timeout and retry options?
comment|/*         BlobRequestOptions options = new BlobRequestOptions();         options.setTimeoutIntervalInMs(1000);         options.setRetryPolicyFactory(new RetryNoRetry());         blob_container.deleteIfExists(options, null);         */
name|logger
operator|.
name|trace
argument_list|(
literal|"removing container [{}]"
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|blob_container
operator|.
name|deleteIfExists
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createContainer
specifier|public
name|void
name|createContainer
parameter_list|(
name|String
name|container
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
try|try
block|{
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"creating container [{}]"
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|blob_container
operator|.
name|createIfNotExist
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"fails creating container [{}]"
argument_list|,
name|container
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|container
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|deleteFiles
specifier|public
name|void
name|deleteFiles
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
throws|,
name|ServiceException
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"delete files container [{}], path [{}]"
argument_list|,
name|container
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// Container name must be lower case.
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob_container
operator|.
name|exists
argument_list|()
condition|)
block|{
name|ListBlobsOptions
name|options
init|=
operator|new
name|ListBlobsOptions
argument_list|()
decl_stmt|;
name|options
operator|.
name|setPrefix
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ListBlobsResult
operator|.
name|BlobEntry
argument_list|>
name|blobs
init|=
name|service
operator|.
name|listBlobs
argument_list|(
name|container
argument_list|,
name|options
argument_list|)
operator|.
name|getBlobs
argument_list|()
decl_stmt|;
for|for
control|(
name|ListBlobsResult
operator|.
name|BlobEntry
name|blob
range|:
name|blobs
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"removing in container [{}], path [{}], blob [{}]"
argument_list|,
name|container
argument_list|,
name|path
argument_list|,
name|blob
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|deleteBlob
argument_list|(
name|container
argument_list|,
name|blob
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|blobExists
specifier|public
name|boolean
name|blobExists
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|blob
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
comment|// Container name must be lower case.
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob_container
operator|.
name|exists
argument_list|()
condition|)
block|{
name|CloudBlockBlob
name|azureBlob
init|=
name|blob_container
operator|.
name|getBlockBlobReference
argument_list|(
name|blob
argument_list|)
decl_stmt|;
return|return
name|azureBlob
operator|.
name|exists
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|deleteBlob
specifier|public
name|void
name|deleteBlob
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|blob
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"delete blob for container [{}], blob [{}]"
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
expr_stmt|;
comment|// Container name must be lower case.
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob_container
operator|.
name|exists
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"blob found. removing."
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|CloudBlockBlob
name|azureBlob
init|=
name|blob_container
operator|.
name|getBlockBlobReference
argument_list|(
name|blob
argument_list|)
decl_stmt|;
name|azureBlob
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getInputStream
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|blob
parameter_list|)
throws|throws
name|ServiceException
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"reading container [{}], blob [{}]"
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
expr_stmt|;
name|GetBlobResult
name|blobResult
init|=
name|service
operator|.
name|getBlob
argument_list|(
name|container
argument_list|,
name|blob
argument_list|)
decl_stmt|;
return|return
name|blobResult
operator|.
name|getContentStream
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOutputStream
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|blob
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"writing container [{}], blob [{}]"
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
expr_stmt|;
return|return
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
operator|.
name|getBlockBlobReference
argument_list|(
name|blob
argument_list|)
operator|.
name|openOutputStream
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|listBlobsByPrefix
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|listBlobsByPrefix
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|keyPath
parameter_list|,
name|String
name|prefix
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
throws|,
name|ServiceException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"listing container [{}], keyPath [{}], prefix [{}]"
argument_list|,
name|container
argument_list|,
name|keyPath
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|blobsBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob_container
operator|.
name|exists
argument_list|()
condition|)
block|{
name|Iterable
argument_list|<
name|ListBlobItem
argument_list|>
name|blobs
init|=
name|blob_container
operator|.
name|listBlobs
argument_list|(
name|keyPath
operator|+
name|prefix
argument_list|)
decl_stmt|;
for|for
control|(
name|ListBlobItem
name|blob
range|:
name|blobs
control|)
block|{
name|URI
name|uri
init|=
name|blob
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"blob url [{}]"
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|String
name|blobpath
init|=
name|uri
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
name|container
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|BlobProperties
name|properties
init|=
name|service
operator|.
name|getBlobProperties
argument_list|(
name|container
argument_list|,
name|blobpath
argument_list|)
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|blobpath
operator|.
name|substring
argument_list|(
name|keyPath
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"blob url [{}], name [{}], size [{}]"
argument_list|,
name|uri
argument_list|,
name|name
argument_list|,
name|properties
operator|.
name|getContentLength
argument_list|()
argument_list|)
expr_stmt|;
name|blobsBuilder
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|PlainBlobMetaData
argument_list|(
name|name
argument_list|,
name|properties
operator|.
name|getContentLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|blobsBuilder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|moveBlob
specifier|public
name|void
name|moveBlob
parameter_list|(
name|String
name|container
parameter_list|,
name|String
name|sourceBlob
parameter_list|,
name|String
name|targetBlob
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"moveBlob container [{}], sourceBlob [{}], targetBlob [{}]"
argument_list|,
name|container
argument_list|,
name|sourceBlob
argument_list|,
name|targetBlob
argument_list|)
expr_stmt|;
name|CloudBlobContainer
name|blob_container
init|=
name|client
operator|.
name|getContainerReference
argument_list|(
name|container
argument_list|)
decl_stmt|;
name|CloudBlockBlob
name|blobSource
init|=
name|blob_container
operator|.
name|getBlockBlobReference
argument_list|(
name|sourceBlob
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobSource
operator|.
name|exists
argument_list|()
condition|)
block|{
name|CloudBlockBlob
name|blobTarget
init|=
name|blob_container
operator|.
name|getBlockBlobReference
argument_list|(
name|targetBlob
argument_list|)
decl_stmt|;
name|blobTarget
operator|.
name|copyFromBlob
argument_list|(
name|blobSource
argument_list|)
expr_stmt|;
name|blobSource
operator|.
name|delete
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"moveBlob container [{}], sourceBlob [{}], targetBlob [{}] -> done"
argument_list|,
name|container
argument_list|,
name|sourceBlob
argument_list|,
name|targetBlob
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"starting azure storage client instance"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"stopping azure storage client instance"
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
name|ElasticsearchException
block|{     }
block|}
end_class

end_unit

