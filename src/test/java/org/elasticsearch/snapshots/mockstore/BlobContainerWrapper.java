begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots.mockstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|mockstore
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|BlobContainer
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
DECL|class|BlobContainerWrapper
specifier|public
class|class
name|BlobContainerWrapper
implements|implements
name|BlobContainer
block|{
DECL|field|delegate
specifier|private
name|BlobContainer
name|delegate
decl_stmt|;
DECL|method|BlobContainerWrapper
specifier|public
name|BlobContainerWrapper
parameter_list|(
name|BlobContainer
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|path
specifier|public
name|BlobPath
name|path
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|path
argument_list|()
return|;
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
return|return
name|delegate
operator|.
name|blobExists
argument_list|(
name|blobName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|openInput
specifier|public
name|InputStream
name|openInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|openInput
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createOutput
specifier|public
name|OutputStream
name|createOutput
parameter_list|(
name|String
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|createOutput
argument_list|(
name|blobName
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
name|blobName
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|deleteBlob
argument_list|(
name|blobName
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
name|blobNamePrefix
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|deleteBlobsByPrefix
argument_list|(
name|blobNamePrefix
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deleteBlobsByFilter
specifier|public
name|void
name|deleteBlobsByFilter
parameter_list|(
name|BlobNameFilter
name|filter
parameter_list|)
throws|throws
name|IOException
block|{
name|delegate
operator|.
name|deleteBlobsByFilter
argument_list|(
name|filter
argument_list|)
expr_stmt|;
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
name|delegate
operator|.
name|listBlobs
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
name|blobNamePrefix
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|listBlobsByPrefix
argument_list|(
name|blobNamePrefix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

