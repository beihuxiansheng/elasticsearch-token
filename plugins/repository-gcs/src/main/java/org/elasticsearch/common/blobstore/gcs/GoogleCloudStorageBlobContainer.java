begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.blobstore.gcs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|blobstore
operator|.
name|gcs
package|;
end_package

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
name|BlobStoreException
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
name|AbstractBlobContainer
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_class
DECL|class|GoogleCloudStorageBlobContainer
specifier|public
class|class
name|GoogleCloudStorageBlobContainer
extends|extends
name|AbstractBlobContainer
block|{
DECL|field|blobStore
specifier|private
specifier|final
name|GoogleCloudStorageBlobStore
name|blobStore
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
DECL|method|GoogleCloudStorageBlobContainer
name|GoogleCloudStorageBlobContainer
parameter_list|(
name|BlobPath
name|path
parameter_list|,
name|GoogleCloudStorageBlobStore
name|blobStore
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|blobStore
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
operator|.
name|buildAsString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|blobExists
specifier|public
name|boolean
name|blobExists
parameter_list|(
name|String
name|blobName
parameter_list|)
block|{
try|try
block|{
return|return
name|blobStore
operator|.
name|blobExists
argument_list|(
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BlobStoreException
argument_list|(
literal|"Failed to check if blob ["
operator|+
name|blobName
operator|+
literal|"] exists"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listBlobs
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|listBlobs
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|blobStore
operator|.
name|listBlobs
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|blobStore
operator|.
name|listBlobsByPrefix
argument_list|(
name|path
argument_list|,
name|prefix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readBlob
specifier|public
name|InputStream
name|readBlob
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|blobStore
operator|.
name|readBlob
argument_list|(
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeBlob
specifier|public
name|void
name|writeBlob
parameter_list|(
name|String
name|blobName
parameter_list|,
name|InputStream
name|inputStream
parameter_list|,
name|long
name|blobSize
parameter_list|)
throws|throws
name|IOException
block|{
name|blobStore
operator|.
name|writeBlob
argument_list|(
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|,
name|inputStream
argument_list|,
name|blobSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeBlob
specifier|public
name|void
name|writeBlob
parameter_list|(
name|String
name|blobName
parameter_list|,
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBlob
argument_list|(
name|blobName
argument_list|,
name|bytes
operator|.
name|streamInput
argument_list|()
argument_list|,
name|bytes
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteBlob
specifier|public
name|void
name|deleteBlob
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
name|blobStore
operator|.
name|deleteBlob
argument_list|(
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteBlobsByPrefix
specifier|public
name|void
name|deleteBlobsByPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|IOException
block|{
name|blobStore
operator|.
name|deleteBlobsByPrefix
argument_list|(
name|buildKey
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteBlobs
specifier|public
name|void
name|deleteBlobs
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|blobNames
parameter_list|)
throws|throws
name|IOException
block|{
name|blobStore
operator|.
name|deleteBlobs
argument_list|(
name|buildKeys
argument_list|(
name|blobNames
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|move
specifier|public
name|void
name|move
parameter_list|(
name|String
name|sourceBlobName
parameter_list|,
name|String
name|targetBlobName
parameter_list|)
throws|throws
name|IOException
block|{
name|blobStore
operator|.
name|moveBlob
argument_list|(
name|buildKey
argument_list|(
name|sourceBlobName
argument_list|)
argument_list|,
name|buildKey
argument_list|(
name|targetBlobName
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildKey
specifier|protected
name|String
name|buildKey
parameter_list|(
name|String
name|blobName
parameter_list|)
block|{
assert|assert
name|blobName
operator|!=
literal|null
assert|;
return|return
name|path
operator|+
name|blobName
return|;
block|}
DECL|method|buildKeys
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|buildKeys
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|blobNames
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|keys
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|blobNames
operator|!=
literal|null
condition|)
block|{
name|keys
operator|.
name|addAll
argument_list|(
name|blobNames
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|this
operator|::
name|buildKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
block|}
end_class

end_unit

