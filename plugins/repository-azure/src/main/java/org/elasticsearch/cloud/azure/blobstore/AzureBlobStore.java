begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.azure.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|blobstore
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|storage
operator|.
name|LocationMode
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
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
name|cloud
operator|.
name|azure
operator|.
name|storage
operator|.
name|AzureStorageService
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
name|azure
operator|.
name|storage
operator|.
name|AzureStorageService
operator|.
name|Storage
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
name|Strings
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
name|BlobContainer
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
name|BlobPath
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
name|BlobStore
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
name|AbstractComponent
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
name|repositories
operator|.
name|RepositoryName
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
name|RepositorySettings
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
name|URISyntaxException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|storage
operator|.
name|AzureStorageSettings
operator|.
name|getValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|azure
operator|.
name|AzureRepository
operator|.
name|Repository
import|;
end_import

begin_class
DECL|class|AzureBlobStore
specifier|public
class|class
name|AzureBlobStore
extends|extends
name|AbstractComponent
implements|implements
name|BlobStore
block|{
DECL|field|client
specifier|private
specifier|final
name|AzureStorageService
name|client
decl_stmt|;
DECL|field|accountName
specifier|private
specifier|final
name|String
name|accountName
decl_stmt|;
DECL|field|locMode
specifier|private
specifier|final
name|LocationMode
name|locMode
decl_stmt|;
DECL|field|container
specifier|private
specifier|final
name|String
name|container
decl_stmt|;
DECL|field|repositoryName
specifier|private
specifier|final
name|String
name|repositoryName
decl_stmt|;
annotation|@
name|Inject
DECL|method|AzureBlobStore
specifier|public
name|AzureBlobStore
parameter_list|(
name|RepositoryName
name|name
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|RepositorySettings
name|repositorySettings
parameter_list|,
name|AzureStorageService
name|client
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|StorageException
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|container
operator|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|CONTAINER_SETTING
argument_list|,
name|Storage
operator|.
name|CONTAINER_SETTING
argument_list|)
expr_stmt|;
name|this
operator|.
name|repositoryName
operator|=
name|name
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|accountName
operator|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|ACCOUNT_SETTING
argument_list|,
name|Storage
operator|.
name|ACCOUNT_SETTING
argument_list|)
expr_stmt|;
name|String
name|modeStr
init|=
name|getValue
argument_list|(
name|repositorySettings
argument_list|,
name|Repository
operator|.
name|LOCATION_MODE_SETTING
argument_list|,
name|Storage
operator|.
name|LOCATION_MODE_SETTING
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|modeStr
argument_list|)
condition|)
block|{
name|this
operator|.
name|locMode
operator|=
name|LocationMode
operator|.
name|valueOf
argument_list|(
name|modeStr
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|locMode
operator|=
name|LocationMode
operator|.
name|PRIMARY_ONLY
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|container
return|;
block|}
DECL|method|container
specifier|public
name|String
name|container
parameter_list|()
block|{
return|return
name|container
return|;
block|}
annotation|@
name|Override
DECL|method|blobContainer
specifier|public
name|BlobContainer
name|blobContainer
parameter_list|(
name|BlobPath
name|path
parameter_list|)
block|{
return|return
operator|new
name|AzureBlobContainer
argument_list|(
name|repositoryName
argument_list|,
name|path
argument_list|,
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|BlobPath
name|path
parameter_list|)
block|{
name|String
name|keyPath
init|=
name|path
operator|.
name|buildAsString
argument_list|()
decl_stmt|;
try|try
block|{
name|this
operator|.
name|client
operator|.
name|deleteFiles
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|keyPath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
decl||
name|StorageException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"can not remove [{}] in container {{}}: {}"
argument_list|,
name|keyPath
argument_list|,
name|container
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
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{     }
DECL|method|doesContainerExist
specifier|public
name|boolean
name|doesContainerExist
parameter_list|(
name|String
name|container
parameter_list|)
block|{
return|return
name|this
operator|.
name|client
operator|.
name|doesContainerExist
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|)
return|;
block|}
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
name|this
operator|.
name|client
operator|.
name|removeContainer
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
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
name|this
operator|.
name|client
operator|.
name|createContainer
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|)
expr_stmt|;
block|}
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
block|{
name|this
operator|.
name|client
operator|.
name|deleteFiles
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
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
return|return
name|this
operator|.
name|client
operator|.
name|blobExists
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
return|;
block|}
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
name|this
operator|.
name|client
operator|.
name|deleteBlob
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
expr_stmt|;
block|}
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
name|URISyntaxException
throws|,
name|StorageException
block|{
return|return
name|this
operator|.
name|client
operator|.
name|getInputStream
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
return|;
block|}
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
return|return
name|this
operator|.
name|client
operator|.
name|getOutputStream
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|blob
argument_list|)
return|;
block|}
DECL|method|listBlobsByPrefix
specifier|public
name|Map
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
block|{
return|return
name|this
operator|.
name|client
operator|.
name|listBlobsByPrefix
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
argument_list|,
name|container
argument_list|,
name|keyPath
argument_list|,
name|prefix
argument_list|)
return|;
block|}
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
name|this
operator|.
name|client
operator|.
name|moveBlob
argument_list|(
name|this
operator|.
name|accountName
argument_list|,
name|this
operator|.
name|locMode
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
end_class

end_unit

