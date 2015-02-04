begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
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
name|ElasticsearchException
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
name|AzureStorageService
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
name|ByteArrayOutputStream
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * In memory storage for unit tests  */
end_comment

begin_class
DECL|class|AzureStorageServiceMock
specifier|public
class|class
name|AzureStorageServiceMock
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|AzureStorageServiceMock
argument_list|>
implements|implements
name|AzureStorageService
block|{
DECL|field|blobs
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|ByteArrayOutputStream
argument_list|>
name|blobs
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|AzureStorageServiceMock
specifier|protected
name|AzureStorageServiceMock
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
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
return|return
literal|true
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
block|{     }
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
block|{     }
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
block|{     }
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
block|{
return|return
name|blobs
operator|.
name|containsKey
argument_list|(
name|blob
argument_list|)
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
block|{
name|blobs
operator|.
name|remove
argument_list|(
name|blob
argument_list|)
expr_stmt|;
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
block|{
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|blobs
operator|.
name|get
argument_list|(
name|blob
argument_list|)
operator|.
name|toByteArray
argument_list|()
argument_list|)
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
name|ByteArrayOutputStream
name|outputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|blobs
operator|.
name|put
argument_list|(
name|blob
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
return|return
name|outputStream
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
block|{
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
for|for
control|(
name|String
name|blobName
range|:
name|blobs
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|startsWithIgnoreCase
argument_list|(
name|blobName
argument_list|,
name|prefix
argument_list|)
condition|)
block|{
name|blobsBuilder
operator|.
name|put
argument_list|(
name|blobName
argument_list|,
operator|new
name|PlainBlobMetaData
argument_list|(
name|blobName
argument_list|,
name|blobs
operator|.
name|get
argument_list|(
name|blobName
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|map
init|=
name|blobsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|map
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
for|for
control|(
name|String
name|blobName
range|:
name|blobs
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|endsWithIgnoreCase
argument_list|(
name|blobName
argument_list|,
name|sourceBlob
argument_list|)
condition|)
block|{
name|ByteArrayOutputStream
name|outputStream
init|=
name|blobs
operator|.
name|get
argument_list|(
name|blobName
argument_list|)
decl_stmt|;
name|blobs
operator|.
name|put
argument_list|(
name|blobName
operator|.
name|replace
argument_list|(
name|sourceBlob
argument_list|,
name|targetBlob
argument_list|)
argument_list|,
name|outputStream
argument_list|)
expr_stmt|;
name|blobs
operator|.
name|remove
argument_list|(
name|blobName
argument_list|)
expr_stmt|;
block|}
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
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
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
comment|/**      * Test if the given String starts with the specified prefix,      * ignoring upper/lower case.      *      * @param str    the String to check      * @param prefix the prefix to look for      * @see java.lang.String#startsWith      */
DECL|method|startsWithIgnoreCase
specifier|public
specifier|static
name|boolean
name|startsWithIgnoreCase
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
operator|||
name|prefix
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|str
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|<
name|prefix
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|lcStr
init|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|lcPrefix
init|=
name|prefix
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
return|return
name|lcStr
operator|.
name|equals
argument_list|(
name|lcPrefix
argument_list|)
return|;
block|}
comment|/**      * Test if the given String ends with the specified suffix,      * ignoring upper/lower case.      *      * @param str    the String to check      * @param suffix the suffix to look for      * @see java.lang.String#startsWith      */
DECL|method|endsWithIgnoreCase
specifier|public
specifier|static
name|boolean
name|endsWithIgnoreCase
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
operator|||
name|suffix
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|str
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|<
name|suffix
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|lcStr
init|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|suffix
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
name|String
name|lcPrefix
init|=
name|suffix
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
return|return
name|lcStr
operator|.
name|equals
argument_list|(
name|lcPrefix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

