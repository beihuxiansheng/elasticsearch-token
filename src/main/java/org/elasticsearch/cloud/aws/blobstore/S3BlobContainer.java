begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cloud.aws.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|aws
operator|.
name|blobstore
package|;
end_package

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|AmazonClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|amazonaws
operator|.
name|services
operator|.
name|s3
operator|.
name|model
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|S3BlobContainer
specifier|public
class|class
name|S3BlobContainer
extends|extends
name|AbstractBlobContainer
block|{
DECL|field|blobStore
specifier|protected
specifier|final
name|S3BlobStore
name|blobStore
decl_stmt|;
DECL|field|keyPath
specifier|protected
specifier|final
name|String
name|keyPath
decl_stmt|;
DECL|method|S3BlobContainer
specifier|public
name|S3BlobContainer
parameter_list|(
name|BlobPath
name|path
parameter_list|,
name|S3BlobStore
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
name|String
name|keyPath
init|=
name|path
operator|.
name|buildAsString
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|keyPath
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|keyPath
operator|=
name|keyPath
operator|+
literal|"/"
expr_stmt|;
block|}
name|this
operator|.
name|keyPath
operator|=
name|keyPath
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
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|getObjectMetadata
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|AmazonS3Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BlobStoreException
argument_list|(
literal|"failed to check if blob exists"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|deleteObject
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Exception when deleting blob ["
operator|+
name|blobName
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|InputStream
name|openInput
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|retry
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|retry
operator|<=
name|blobStore
operator|.
name|numberOfRetries
argument_list|()
condition|)
block|{
try|try
block|{
name|S3Object
name|s3Object
init|=
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|getObject
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|s3Object
operator|.
name|getObjectContent
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|AmazonClientException
name|e
parameter_list|)
block|{
if|if
condition|(
name|blobStore
operator|.
name|shouldRetry
argument_list|(
name|e
argument_list|)
operator|&&
operator|(
name|retry
operator|<
name|blobStore
operator|.
name|numberOfRetries
argument_list|()
operator|)
condition|)
block|{
name|retry
operator|++
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|e
operator|instanceof
name|AmazonS3Exception
condition|)
block|{
if|if
condition|(
literal|404
operator|==
operator|(
operator|(
name|AmazonS3Exception
operator|)
name|e
operator|)
operator|.
name|getStatusCode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Blob object ["
operator|+
name|blobName
operator|+
literal|"] not found: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
throw|throw
name|e
throw|;
block|}
block|}
block|}
throw|throw
operator|new
name|BlobStoreException
argument_list|(
literal|"retries exhausted while attempting to access blob object [name:"
operator|+
name|blobName
operator|+
literal|", bucket:"
operator|+
name|blobStore
operator|.
name|bucket
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|OutputStream
name|createOutput
parameter_list|(
specifier|final
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// UploadS3OutputStream does buffering& retry logic internally
return|return
operator|new
name|DefaultS3OutputStream
argument_list|(
name|blobStore
argument_list|,
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|blobName
argument_list|)
argument_list|,
name|blobStore
operator|.
name|bufferSizeInBytes
argument_list|()
argument_list|,
name|blobStore
operator|.
name|numberOfRetries
argument_list|()
argument_list|,
name|blobStore
operator|.
name|serverSideEncryption
argument_list|()
argument_list|)
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
annotation|@
name|Nullable
name|String
name|blobNamePrefix
parameter_list|)
throws|throws
name|IOException
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
name|ObjectListing
name|prevListing
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|ObjectListing
name|list
decl_stmt|;
if|if
condition|(
name|prevListing
operator|!=
literal|null
condition|)
block|{
name|list
operator|=
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|listNextBatchOfObjects
argument_list|(
name|prevListing
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|blobNamePrefix
operator|!=
literal|null
condition|)
block|{
name|list
operator|=
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|listObjects
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|blobNamePrefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|list
operator|=
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|listObjects
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|keyPath
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|S3ObjectSummary
name|summary
range|:
name|list
operator|.
name|getObjectSummaries
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|summary
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
name|keyPath
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
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
name|summary
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|list
operator|.
name|isTruncated
argument_list|()
condition|)
block|{
name|prevListing
operator|=
name|list
expr_stmt|;
block|}
else|else
block|{
break|break;
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
try|try
block|{
name|CopyObjectRequest
name|request
init|=
operator|new
name|CopyObjectRequest
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|sourceBlobName
argument_list|)
argument_list|,
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|targetBlobName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|blobStore
operator|.
name|serverSideEncryption
argument_list|()
condition|)
block|{
name|ObjectMetadata
name|objectMetadata
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
name|objectMetadata
operator|.
name|setSSEAlgorithm
argument_list|(
name|ObjectMetadata
operator|.
name|AES_256_SERVER_SIDE_ENCRYPTION
argument_list|)
expr_stmt|;
name|request
operator|.
name|setNewObjectMetadata
argument_list|(
name|objectMetadata
argument_list|)
expr_stmt|;
block|}
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|copyObject
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|deleteObject
argument_list|(
name|blobStore
operator|.
name|bucket
argument_list|()
argument_list|,
name|buildKey
argument_list|(
name|sourceBlobName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AmazonS3Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|listBlobs
specifier|public
name|ImmutableMap
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
name|listBlobsByPrefix
argument_list|(
literal|null
argument_list|)
return|;
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
return|return
name|keyPath
operator|+
name|blobName
return|;
block|}
block|}
end_class

end_unit

