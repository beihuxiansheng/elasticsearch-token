begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.strategy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|strategy
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|MutableShardRouting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|RoutingNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|RoutingNodes
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|AbstractComponent
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|blobstore
operator|.
name|BlobStoreIndexGateway
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
name|service
operator|.
name|InternalIndexService
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
name|store
operator|.
name|IndexStore
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
name|store
operator|.
name|StoreFileMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|store
operator|.
name|TransportNodesListShardStoreMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|PreferUnallocatedShardUnassignedStrategy
specifier|public
class|class
name|PreferUnallocatedShardUnassignedStrategy
extends|extends
name|AbstractComponent
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|transportNodesListShardStoreMetaData
specifier|private
specifier|final
name|TransportNodesListShardStoreMetaData
name|transportNodesListShardStoreMetaData
decl_stmt|;
DECL|method|PreferUnallocatedShardUnassignedStrategy
annotation|@
name|Inject
specifier|public
name|PreferUnallocatedShardUnassignedStrategy
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|TransportNodesListShardStoreMetaData
name|transportNodesListShardStoreMetaData
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|transportNodesListShardStoreMetaData
operator|=
name|transportNodesListShardStoreMetaData
expr_stmt|;
block|}
DECL|method|allocateUnassigned
specifier|public
name|boolean
name|allocateUnassigned
parameter_list|(
name|RoutingNodes
name|routingNodes
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|Iterator
argument_list|<
name|MutableShardRouting
argument_list|>
name|unassignedIterator
init|=
name|routingNodes
operator|.
name|unassigned
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|unassignedIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MutableShardRouting
name|shard
init|=
name|unassignedIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|InternalIndexService
name|indexService
init|=
operator|(
name|InternalIndexService
operator|)
name|indicesService
operator|.
name|indexService
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// if the store is not persistent, it makes no sense to test for special allocation
if|if
condition|(
operator|!
name|indexService
operator|.
name|store
argument_list|()
operator|.
name|persistent
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|TransportNodesListShardStoreMetaData
operator|.
name|NodesStoreFilesMetaData
name|nodesStoreFilesMetaData
init|=
name|transportNodesListShardStoreMetaData
operator|.
name|list
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|long
name|lastSizeMatched
init|=
literal|0
decl_stmt|;
name|DiscoveryNode
name|lastDiscoNodeMatched
init|=
literal|null
decl_stmt|;
name|RoutingNode
name|lastNodeMatched
init|=
literal|null
decl_stmt|;
for|for
control|(
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
name|nodeStoreFilesMetaData
range|:
name|nodesStoreFilesMetaData
control|)
block|{
name|DiscoveryNode
name|discoNode
init|=
name|nodeStoreFilesMetaData
operator|.
name|node
argument_list|()
decl_stmt|;
name|IndexStore
operator|.
name|StoreFilesMetaData
name|storeFilesMetaData
init|=
name|nodeStoreFilesMetaData
operator|.
name|storeFilesMetaData
argument_list|()
decl_stmt|;
if|if
condition|(
name|storeFilesMetaData
operator|==
literal|null
condition|)
block|{
comment|// already allocated on that node...
continue|continue;
block|}
name|RoutingNode
name|node
init|=
name|routingNodes
operator|.
name|node
argument_list|(
name|discoNode
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// check if we can allocate on that node...
if|if
condition|(
operator|!
operator|(
name|node
operator|.
name|canAllocate
argument_list|(
name|routingNodes
operator|.
name|metaData
argument_list|()
argument_list|,
name|routingNodes
operator|.
name|routingTable
argument_list|()
argument_list|)
operator|&&
name|node
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
comment|// if it is already allocated, we can't assign to it...
if|if
condition|(
name|storeFilesMetaData
operator|.
name|allocated
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// if its a primary, it will be recovered from the gateway, find one that is closet to it
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|&&
name|indexService
operator|.
name|gateway
argument_list|()
operator|instanceof
name|BlobStoreIndexGateway
condition|)
block|{
name|BlobStoreIndexGateway
name|indexGateway
init|=
operator|(
name|BlobStoreIndexGateway
operator|)
name|indexService
operator|.
name|gateway
argument_list|()
decl_stmt|;
try|try
block|{
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|BlobMetaData
argument_list|>
name|indexBlobsMetaData
init|=
name|indexGateway
operator|.
name|listIndexBlobs
argument_list|(
name|shard
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|sizeMatched
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StoreFileMetaData
name|storeFileMetaData
range|:
name|storeFilesMetaData
control|)
block|{
if|if
condition|(
name|indexBlobsMetaData
operator|.
name|containsKey
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|indexBlobsMetaData
operator|.
name|get
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|md5
argument_list|()
operator|.
name|equals
argument_list|(
name|storeFileMetaData
operator|.
name|md5
argument_list|()
argument_list|)
condition|)
block|{
name|sizeMatched
operator|+=
name|storeFileMetaData
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sizeMatched
operator|>
name|lastSizeMatched
condition|)
block|{
name|lastSizeMatched
operator|=
name|sizeMatched
expr_stmt|;
name|lastDiscoNodeMatched
operator|=
name|discoNode
expr_stmt|;
name|lastNodeMatched
operator|=
name|node
expr_stmt|;
block|}
continue|continue;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// failed, log and try and allocate based on size
name|logger
operator|.
name|debug
argument_list|(
literal|"Failed to guess allocation of primary based on gateway for "
operator|+
name|shard
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if its backup, see if there is a primary that *is* allocated, and try and assign a location that is closest to it
comment|// note, since we replicate operations, this might not be the same (different flush intervals)
if|if
condition|(
operator|!
name|shard
operator|.
name|primary
argument_list|()
condition|)
block|{
name|MutableShardRouting
name|primaryShard
init|=
name|routingNodes
operator|.
name|findPrimaryForBackup
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryShard
operator|!=
literal|null
operator|&&
name|primaryShard
operator|.
name|active
argument_list|()
condition|)
block|{
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
name|primaryNodeStoreFileMetaData
init|=
name|nodesStoreFilesMetaData
operator|.
name|nodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|primaryShard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryNodeStoreFileMetaData
operator|!=
literal|null
operator|&&
name|primaryNodeStoreFileMetaData
operator|.
name|storeFilesMetaData
argument_list|()
operator|!=
literal|null
operator|&&
name|primaryNodeStoreFileMetaData
operator|.
name|storeFilesMetaData
argument_list|()
operator|.
name|allocated
argument_list|()
condition|)
block|{
name|long
name|sizeMatched
init|=
literal|0
decl_stmt|;
name|IndexStore
operator|.
name|StoreFilesMetaData
name|primaryStoreFilesMetaData
init|=
name|primaryNodeStoreFileMetaData
operator|.
name|storeFilesMetaData
argument_list|()
decl_stmt|;
for|for
control|(
name|StoreFileMetaData
name|storeFileMetaData
range|:
name|storeFilesMetaData
control|)
block|{
if|if
condition|(
name|primaryStoreFilesMetaData
operator|.
name|fileExists
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|primaryStoreFilesMetaData
operator|.
name|file
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|sizeInBytes
argument_list|()
operator|==
name|storeFileMetaData
operator|.
name|sizeInBytes
argument_list|()
condition|)
block|{
name|sizeMatched
operator|+=
name|storeFileMetaData
operator|.
name|sizeInBytes
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sizeMatched
operator|>
name|lastSizeMatched
condition|)
block|{
name|lastSizeMatched
operator|=
name|sizeMatched
expr_stmt|;
name|lastDiscoNodeMatched
operator|=
name|discoNode
expr_stmt|;
name|lastNodeMatched
operator|=
name|node
expr_stmt|;
block|}
continue|continue;
block|}
block|}
block|}
block|}
if|if
condition|(
name|lastNodeMatched
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}] allocating to [{}] in order to reuse its unallocated persistent store"
argument_list|,
name|shard
operator|.
name|index
argument_list|()
argument_list|,
name|shard
operator|.
name|id
argument_list|()
argument_list|,
name|lastDiscoNodeMatched
argument_list|)
expr_stmt|;
block|}
comment|// we found a match
name|changed
operator|=
literal|true
expr_stmt|;
name|lastNodeMatched
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|unassignedIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|changed
return|;
block|}
block|}
end_class

end_unit

