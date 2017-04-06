begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories.s3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|s3
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
name|PartETag
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
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
name|Streams
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
name|s3
operator|.
name|DefaultS3OutputStream
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
name|s3
operator|.
name|S3BlobStore
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
name|List
import|;
end_import

begin_class
DECL|class|MockDefaultS3OutputStream
specifier|public
class|class
name|MockDefaultS3OutputStream
extends|extends
name|DefaultS3OutputStream
block|{
DECL|field|out
specifier|private
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|initialized
specifier|private
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
DECL|field|completed
specifier|private
name|boolean
name|completed
init|=
literal|false
decl_stmt|;
DECL|field|aborted
specifier|private
name|boolean
name|aborted
init|=
literal|false
decl_stmt|;
DECL|field|numberOfUploadRequests
specifier|private
name|int
name|numberOfUploadRequests
init|=
literal|0
decl_stmt|;
DECL|method|MockDefaultS3OutputStream
specifier|public
name|MockDefaultS3OutputStream
parameter_list|(
name|int
name|bufferSizeInBytes
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|"test-bucket"
argument_list|,
literal|"test-blobname"
argument_list|,
name|bufferSizeInBytes
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doUpload
specifier|protected
name|void
name|doUpload
parameter_list|(
name|S3BlobStore
name|blobStore
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|blobName
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|serverSideEncryption
parameter_list|)
throws|throws
name|AmazonS3Exception
block|{
try|try
block|{
name|long
name|copied
init|=
name|Streams
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|out
argument_list|)
decl_stmt|;
if|if
condition|(
name|copied
operator|!=
name|length
condition|)
block|{
throw|throw
operator|new
name|AmazonS3Exception
argument_list|(
literal|"Not all the bytes were copied"
argument_list|)
throw|;
block|}
name|numberOfUploadRequests
operator|++
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
name|AmazonS3Exception
argument_list|(
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
DECL|method|doInitialize
specifier|protected
name|String
name|doInitialize
parameter_list|(
name|S3BlobStore
name|blobStore
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|blobName
parameter_list|,
name|boolean
name|serverSideEncryption
parameter_list|)
block|{
name|initialized
operator|=
literal|true
expr_stmt|;
return|return
name|RandomizedTest
operator|.
name|randomAsciiOfLength
argument_list|(
literal|50
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doUploadMultipart
specifier|protected
name|PartETag
name|doUploadMultipart
parameter_list|(
name|S3BlobStore
name|blobStore
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|blobName
parameter_list|,
name|String
name|uploadId
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|lastPart
parameter_list|)
throws|throws
name|AmazonS3Exception
block|{
try|try
block|{
name|long
name|copied
init|=
name|Streams
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|out
argument_list|)
decl_stmt|;
if|if
condition|(
name|copied
operator|!=
name|length
condition|)
block|{
throw|throw
operator|new
name|AmazonS3Exception
argument_list|(
literal|"Not all the bytes were copied"
argument_list|)
throw|;
block|}
return|return
operator|new
name|PartETag
argument_list|(
name|numberOfUploadRequests
operator|++
argument_list|,
name|RandomizedTest
operator|.
name|randomAsciiOfLength
argument_list|(
literal|50
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AmazonS3Exception
argument_list|(
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
DECL|method|doCompleteMultipart
specifier|protected
name|void
name|doCompleteMultipart
parameter_list|(
name|S3BlobStore
name|blobStore
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|blobName
parameter_list|,
name|String
name|uploadId
parameter_list|,
name|List
argument_list|<
name|PartETag
argument_list|>
name|parts
parameter_list|)
throws|throws
name|AmazonS3Exception
block|{
name|completed
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doAbortMultipart
specifier|protected
name|void
name|doAbortMultipart
parameter_list|(
name|S3BlobStore
name|blobStore
parameter_list|,
name|String
name|bucketName
parameter_list|,
name|String
name|blobName
parameter_list|,
name|String
name|uploadId
parameter_list|)
throws|throws
name|AmazonS3Exception
block|{
name|aborted
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getNumberOfUploadRequests
specifier|public
name|int
name|getNumberOfUploadRequests
parameter_list|()
block|{
return|return
name|numberOfUploadRequests
return|;
block|}
DECL|method|isMultipart
specifier|public
name|boolean
name|isMultipart
parameter_list|()
block|{
return|return
operator|(
name|numberOfUploadRequests
operator|>
literal|1
operator|)
operator|&&
name|initialized
operator|&&
name|completed
operator|&&
operator|!
name|aborted
return|;
block|}
DECL|method|toByteArray
specifier|public
name|byte
index|[]
name|toByteArray
parameter_list|()
block|{
return|return
name|out
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
end_class

end_unit

