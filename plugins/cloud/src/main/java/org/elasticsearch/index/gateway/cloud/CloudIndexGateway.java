begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway.cloud
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|cloud
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
name|ElasticSearchIllegalArgumentException
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
name|blobstore
operator|.
name|CloudBlobStoreService
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
name|jclouds
operator|.
name|JCloudsUtils
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
name|cloud
operator|.
name|CloudGateway
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
name|gateway
operator|.
name|IndexShardGateway
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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|SizeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|SizeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|guice
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
name|util
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
name|jclouds
operator|.
name|blobstore
operator|.
name|BlobStoreContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jclouds
operator|.
name|domain
operator|.
name|Location
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|CloudIndexGateway
specifier|public
class|class
name|CloudIndexGateway
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexGateway
block|{
DECL|field|gateway
specifier|private
specifier|final
name|Gateway
name|gateway
decl_stmt|;
DECL|field|indexContainer
specifier|private
specifier|final
name|String
name|indexContainer
decl_stmt|;
DECL|field|location
specifier|private
specifier|final
name|Location
name|location
decl_stmt|;
DECL|field|chunkSize
specifier|private
specifier|final
name|SizeValue
name|chunkSize
decl_stmt|;
DECL|field|blobStoreContext
specifier|private
specifier|final
name|BlobStoreContext
name|blobStoreContext
decl_stmt|;
DECL|method|CloudIndexGateway
annotation|@
name|Inject
specifier|public
name|CloudIndexGateway
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|CloudBlobStoreService
name|blobStoreService
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
name|this
operator|.
name|blobStoreContext
operator|=
name|blobStoreService
operator|.
name|context
argument_list|()
expr_stmt|;
name|this
operator|.
name|gateway
operator|=
name|gateway
expr_stmt|;
name|String
name|location
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"location"
argument_list|)
decl_stmt|;
name|String
name|container
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"container"
argument_list|)
decl_stmt|;
name|SizeValue
name|chunkSize
init|=
name|componentSettings
operator|.
name|getAsSize
argument_list|(
literal|"chunk_size"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|gateway
operator|instanceof
name|CloudGateway
condition|)
block|{
name|CloudGateway
name|cloudGateway
init|=
operator|(
name|CloudGateway
operator|)
name|gateway
decl_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
name|container
operator|=
name|cloudGateway
operator|.
name|container
argument_list|()
operator|+
name|JCloudsUtils
operator|.
name|BLOB_CONTAINER_SEP
operator|+
name|index
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|chunkSize
operator|==
literal|null
condition|)
block|{
name|chunkSize
operator|=
name|cloudGateway
operator|.
name|chunkSize
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|chunkSize
operator|==
literal|null
condition|)
block|{
name|chunkSize
operator|=
operator|new
name|SizeValue
argument_list|(
literal|4
argument_list|,
name|SizeUnit
operator|.
name|GB
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|gateway
operator|instanceof
name|CloudGateway
condition|)
block|{
name|CloudGateway
name|cloudGateway
init|=
operator|(
name|CloudGateway
operator|)
name|gateway
decl_stmt|;
name|this
operator|.
name|location
operator|=
name|cloudGateway
operator|.
name|location
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|location
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|Location
name|matchedLocation
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|?
extends|extends
name|Location
argument_list|>
name|assignableLocations
init|=
name|blobStoreContext
operator|.
name|getBlobStore
argument_list|()
operator|.
name|listAssignableLocations
argument_list|()
decl_stmt|;
for|for
control|(
name|Location
name|oLocation
range|:
name|assignableLocations
control|)
block|{
if|if
condition|(
name|oLocation
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|location
argument_list|)
condition|)
block|{
name|matchedLocation
operator|=
name|oLocation
expr_stmt|;
break|break;
block|}
block|}
name|this
operator|.
name|location
operator|=
name|matchedLocation
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|location
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Not a valid location ["
operator|+
name|location
operator|+
literal|"], available locations "
operator|+
name|assignableLocations
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|indexContainer
operator|=
name|container
expr_stmt|;
name|this
operator|.
name|chunkSize
operator|=
name|chunkSize
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using location [{}], container [{}], chunk_size [{}]"
argument_list|,
name|this
operator|.
name|location
argument_list|,
name|this
operator|.
name|indexContainer
argument_list|,
name|this
operator|.
name|chunkSize
argument_list|)
expr_stmt|;
comment|//        blobStoreContext.getBlobStore().createContainerInLocation(this.location, this.indexContainer);
block|}
DECL|method|indexLocation
specifier|public
name|Location
name|indexLocation
parameter_list|()
block|{
return|return
name|this
operator|.
name|location
return|;
block|}
DECL|method|indexContainer
specifier|public
name|String
name|indexContainer
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexContainer
return|;
block|}
DECL|method|chunkSize
specifier|public
name|SizeValue
name|chunkSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|chunkSize
return|;
block|}
DECL|method|shardGatewayClass
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|IndexShardGateway
argument_list|>
name|shardGatewayClass
parameter_list|()
block|{
return|return
name|CloudIndexShardGateway
operator|.
name|class
return|;
block|}
DECL|method|close
annotation|@
name|Override
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
operator|!
name|delete
condition|)
block|{
return|return;
block|}
comment|//        blobStoreContext.getBlobStore().deleteContainer(indexContainer);
block|}
block|}
end_class

end_unit

