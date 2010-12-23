begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ObjectMetadata
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
name|PutObjectResult
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
name|ImmutableBlobContainer
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
name|BlobStores
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|S3ImmutableBlobContainer
specifier|public
class|class
name|S3ImmutableBlobContainer
extends|extends
name|AbstractS3BlobContainer
implements|implements
name|ImmutableBlobContainer
block|{
DECL|method|S3ImmutableBlobContainer
specifier|public
name|S3ImmutableBlobContainer
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
argument_list|,
name|blobStore
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBlob
annotation|@
name|Override
specifier|public
name|void
name|writeBlob
parameter_list|(
specifier|final
name|String
name|blobName
parameter_list|,
specifier|final
name|InputStream
name|is
parameter_list|,
specifier|final
name|long
name|sizeInBytes
parameter_list|,
specifier|final
name|WriterListener
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
try|try
block|{
name|ObjectMetadata
name|md
init|=
operator|new
name|ObjectMetadata
argument_list|()
decl_stmt|;
name|md
operator|.
name|setContentLength
argument_list|(
name|sizeInBytes
argument_list|)
expr_stmt|;
name|PutObjectResult
name|objectResult
init|=
name|blobStore
operator|.
name|client
argument_list|()
operator|.
name|putObject
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
argument_list|,
name|is
argument_list|,
name|md
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onCompleted
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBlob
annotation|@
name|Override
specifier|public
name|void
name|writeBlob
parameter_list|(
name|String
name|blobName
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|long
name|sizeInBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|BlobStores
operator|.
name|syncWriteBlob
argument_list|(
name|this
argument_list|,
name|blobName
argument_list|,
name|is
argument_list|,
name|sizeInBytes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

