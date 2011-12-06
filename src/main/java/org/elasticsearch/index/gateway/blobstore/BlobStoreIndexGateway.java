begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway.blobstore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|blobstore
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|unit
operator|.
name|ByteSizeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|Gateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|blobstore
operator|.
name|BlobStoreGateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|none
operator|.
name|NoneGateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|AbstractIndexComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|Index
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|IndexGateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BlobStoreIndexGateway
specifier|public
specifier|abstract
class|class
name|BlobStoreIndexGateway
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexGateway
block|{
DECL|field|gateway
specifier|private
specifier|final
name|BlobStoreGateway
name|gateway
decl_stmt|;
DECL|field|blobStore
specifier|private
specifier|final
name|BlobStore
name|blobStore
decl_stmt|;
DECL|field|indexPath
specifier|private
specifier|final
name|BlobPath
name|indexPath
decl_stmt|;
DECL|field|chunkSize
specifier|protected
name|ByteSizeValue
name|chunkSize
decl_stmt|;
DECL|method|BlobStoreIndexGateway
specifier|protected
name|BlobStoreIndexGateway
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|Gateway
name|gateway
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|gateway
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|NoneGateway
operator|.
name|TYPE
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"index gateway is configured, but no cluster level gateway configured, cluster level metadata will be lost on full shutdown"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|gateway
operator|=
operator|(
name|BlobStoreGateway
operator|)
name|gateway
expr_stmt|;
name|this
operator|.
name|blobStore
operator|=
name|this
operator|.
name|gateway
operator|.
name|blobStore
argument_list|()
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"chunk_size"
argument_list|,
name|this
operator|.
name|gateway
operator|.
name|chunkSize
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|this
operator|.
name|gateway
operator|.
name|basePath
argument_list|()
operator|.
name|add
argument_list|(
literal|"indices"
argument_list|)
operator|.
name|add
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
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
name|type
argument_list|()
operator|+
literal|"://"
operator|+
name|blobStore
operator|+
literal|"/"
operator|+
name|indexPath
return|;
block|}
DECL|method|blobStore
specifier|public
name|BlobStore
name|blobStore
parameter_list|()
block|{
return|return
name|blobStore
return|;
block|}
DECL|method|chunkSize
specifier|public
name|ByteSizeValue
name|chunkSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|chunkSize
return|;
block|}
DECL|method|shardPath
specifier|public
name|BlobPath
name|shardPath
parameter_list|(
name|int
name|shardId
parameter_list|)
block|{
return|return
name|indexPath
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|shardId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|shardPath
specifier|public
specifier|static
name|BlobPath
name|shardPath
parameter_list|(
name|BlobPath
name|basePath
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
return|return
name|basePath
operator|.
name|add
argument_list|(
literal|"indices"
argument_list|)
operator|.
name|add
argument_list|(
name|index
argument_list|)
operator|.
name|add
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|shardId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|delete
condition|)
block|{
name|blobStore
operator|.
name|delete
argument_list|(
name|indexPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

