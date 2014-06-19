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
name|services
operator|.
name|s3
operator|.
name|model
operator|.
name|AmazonS3Exception
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
name|ObjectListing
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
name|S3Object
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
name|S3ObjectSummary
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
name|util
operator|.
name|IOUtils
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractS3BlobContainer
specifier|public
class|class
name|AbstractS3BlobContainer
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
DECL|method|AbstractS3BlobContainer
specifier|public
name|AbstractS3BlobContainer
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
name|boolean
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
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|readBlob
specifier|public
name|void
name|readBlob
parameter_list|(
specifier|final
name|String
name|blobName
parameter_list|,
specifier|final
name|ReadBlobListener
name|listener
parameter_list|)
block|{
name|blobStore
operator|.
name|executor
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|InputStream
name|is
decl_stmt|;
try|try
block|{
name|S3Object
name|object
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
name|is
operator|=
name|object
operator|.
name|getObjectContent
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|blobStore
operator|.
name|bufferSizeInBytes
argument_list|()
index|]
decl_stmt|;
try|try
block|{
name|int
name|bytesRead
decl_stmt|;
while|while
condition|(
operator|(
name|bytesRead
operator|=
name|is
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|listener
operator|.
name|onPartial
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|bytesRead
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onCompleted
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
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
DECL|method|shouldRetry
specifier|protected
name|boolean
name|shouldRetry
parameter_list|(
name|AmazonS3Exception
name|e
parameter_list|)
block|{
return|return
name|e
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|400
operator|&&
literal|"RequestTimeout"
operator|.
name|equals
argument_list|(
name|e
operator|.
name|getErrorCode
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

