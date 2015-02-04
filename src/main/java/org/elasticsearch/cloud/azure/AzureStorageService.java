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
name|collect
operator|.
name|ImmutableMap
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

begin_comment
comment|/**  * Azure Storage Service interface  * @see org.elasticsearch.cloud.azure.AzureStorageServiceImpl for Azure REST API implementation  */
end_comment

begin_interface
DECL|interface|AzureStorageService
specifier|public
interface|interface
name|AzureStorageService
block|{
DECL|class|Fields
specifier|static
specifier|public
specifier|final
class|class
name|Fields
block|{
DECL|field|ACCOUNT
specifier|public
specifier|static
specifier|final
name|String
name|ACCOUNT
init|=
literal|"storage_account"
decl_stmt|;
DECL|field|KEY
specifier|public
specifier|static
specifier|final
name|String
name|KEY
init|=
literal|"storage_key"
decl_stmt|;
DECL|field|CONTAINER
specifier|public
specifier|static
specifier|final
name|String
name|CONTAINER
init|=
literal|"container"
decl_stmt|;
DECL|field|BASE_PATH
specifier|public
specifier|static
specifier|final
name|String
name|BASE_PATH
init|=
literal|"base_path"
decl_stmt|;
DECL|field|CHUNK_SIZE
specifier|public
specifier|static
specifier|final
name|String
name|CHUNK_SIZE
init|=
literal|"chunk_size"
decl_stmt|;
DECL|field|COMPRESS
specifier|public
specifier|static
specifier|final
name|String
name|COMPRESS
init|=
literal|"compress"
decl_stmt|;
block|}
DECL|method|doesContainerExist
name|boolean
name|doesContainerExist
parameter_list|(
name|String
name|container
parameter_list|)
function_decl|;
DECL|method|removeContainer
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
function_decl|;
DECL|method|createContainer
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
function_decl|;
DECL|method|deleteFiles
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
function_decl|;
DECL|method|blobExists
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
function_decl|;
DECL|method|deleteBlob
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
function_decl|;
DECL|method|getInputStream
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
function_decl|;
DECL|method|getOutputStream
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
function_decl|;
DECL|method|listBlobsByPrefix
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
function_decl|;
DECL|method|moveBlob
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
function_decl|;
block|}
end_interface

end_unit

