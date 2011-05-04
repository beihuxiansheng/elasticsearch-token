begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.blobstore
package|package
name|org
operator|.
name|elasticsearch
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
name|ExceptionsHelper
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
name|node
operator|.
name|DiscoveryNodes
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
name|cluster
operator|.
name|routing
operator|.
name|ShardRouting
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
name|allocation
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
name|collect
operator|.
name|Maps
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
name|Sets
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|index
operator|.
name|gateway
operator|.
name|CommitPoint
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
name|shard
operator|.
name|ShardId
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
name|store
operator|.
name|TransportNodesListShardStoreMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|internal
operator|.
name|InternalNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|ConnectTransportException
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|BlobReuseExistingNodeAllocation
specifier|public
class|class
name|BlobReuseExistingNodeAllocation
extends|extends
name|NodeAllocation
block|{
DECL|field|node
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
DECL|field|listShardStoreMetaData
specifier|private
specifier|final
name|TransportNodesListShardStoreMetaData
name|listShardStoreMetaData
decl_stmt|;
DECL|field|listTimeout
specifier|private
specifier|final
name|TimeValue
name|listTimeout
decl_stmt|;
DECL|field|cachedCommitPoints
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ShardId
argument_list|,
name|CommitPoint
argument_list|>
name|cachedCommitPoints
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|cachedStores
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ShardId
argument_list|,
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
argument_list|>
argument_list|>
name|cachedStores
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|method|BlobReuseExistingNodeAllocation
annotation|@
name|Inject
specifier|public
name|BlobReuseExistingNodeAllocation
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Node
name|node
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
name|node
operator|=
name|node
expr_stmt|;
comment|// YACK!, we need the Gateway, but it creates crazy circular dependency
name|this
operator|.
name|listShardStoreMetaData
operator|=
name|transportNodesListShardStoreMetaData
expr_stmt|;
name|this
operator|.
name|listTimeout
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"list_timeout"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|applyStartedShards
annotation|@
name|Override
specifier|public
name|void
name|applyStartedShards
parameter_list|(
name|NodeAllocations
name|nodeAllocations
parameter_list|,
name|StartedRerouteAllocation
name|allocation
parameter_list|)
block|{
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|allocation
operator|.
name|startedShards
argument_list|()
control|)
block|{
name|cachedCommitPoints
operator|.
name|remove
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
name|cachedStores
operator|.
name|remove
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|applyFailedShards
annotation|@
name|Override
specifier|public
name|void
name|applyFailedShards
parameter_list|(
name|NodeAllocations
name|nodeAllocations
parameter_list|,
name|FailedRerouteAllocation
name|allocation
parameter_list|)
block|{
name|cachedCommitPoints
operator|.
name|remove
argument_list|(
name|allocation
operator|.
name|failedShard
argument_list|()
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
name|cachedStores
operator|.
name|remove
argument_list|(
name|allocation
operator|.
name|failedShard
argument_list|()
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|allocateUnassigned
annotation|@
name|Override
specifier|public
name|boolean
name|allocateUnassigned
parameter_list|(
name|NodeAllocations
name|nodeAllocations
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|DiscoveryNodes
name|nodes
init|=
name|allocation
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|RoutingNodes
name|routingNodes
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|changed
return|;
block|}
if|if
condition|(
operator|!
name|routingNodes
operator|.
name|hasUnassigned
argument_list|()
condition|)
block|{
return|return
name|changed
return|;
block|}
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
comment|// pre-check if it can be allocated to any node that currently exists, so we won't list the store for it for nothing
name|boolean
name|canBeAllocatedToAtLeastOneNode
init|=
literal|false
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|discoNode
range|:
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
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
comment|// if its THROTTLING, we are not going to allocate it to this node, so ignore it as well
if|if
condition|(
name|nodeAllocations
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
operator|.
name|allocate
argument_list|()
condition|)
block|{
name|canBeAllocatedToAtLeastOneNode
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|canBeAllocatedToAtLeastOneNode
condition|)
block|{
continue|continue;
block|}
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
argument_list|>
name|shardStores
init|=
name|buildShardStores
argument_list|(
name|nodes
argument_list|,
name|shard
argument_list|)
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
name|Map
operator|.
name|Entry
argument_list|<
name|DiscoveryNode
argument_list|,
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
argument_list|>
name|nodeStoreEntry
range|:
name|shardStores
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DiscoveryNode
name|discoNode
init|=
name|nodeStoreEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
name|storeFilesMetaData
init|=
name|nodeStoreEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: checking node [{}]"
argument_list|,
name|shard
argument_list|,
name|discoNode
argument_list|)
expr_stmt|;
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
comment|// we only check for NO, since if this node is THROTTLING and it has enough "same data"
comment|// then we will try and assign it next time
if|if
condition|(
name|nodeAllocations
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
operator|==
name|Decision
operator|.
name|NO
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
condition|)
block|{
try|try
block|{
name|CommitPoint
name|commitPoint
init|=
name|cachedCommitPoints
operator|.
name|get
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|commitPoint
operator|==
literal|null
condition|)
block|{
name|commitPoint
operator|=
operator|(
call|(
name|BlobStoreGateway
call|)
argument_list|(
operator|(
name|InternalNode
operator|)
name|this
operator|.
name|node
argument_list|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Gateway
operator|.
name|class
argument_list|)
operator|)
operator|.
name|findCommitPoint
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|,
name|shard
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitPoint
operator|!=
literal|null
condition|)
block|{
name|cachedCommitPoints
operator|.
name|put
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|commitPoint
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cachedCommitPoints
operator|.
name|put
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|CommitPoint
operator|.
name|NULL
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|commitPoint
operator|==
name|CommitPoint
operator|.
name|NULL
condition|)
block|{
name|commitPoint
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|commitPoint
operator|==
literal|null
condition|)
block|{
break|break;
block|}
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
name|CommitPoint
operator|.
name|FileInfo
name|fileInfo
init|=
name|commitPoint
operator|.
name|findPhysicalIndexFile
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileInfo
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fileInfo
operator|.
name|isSame
argument_list|(
name|storeFileMetaData
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: [{}] reusing file since it exists on remote node and on gateway"
argument_list|,
name|shard
argument_list|,
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|sizeMatched
operator|+=
name|storeFileMetaData
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: [{}] ignore file since it exists on remote node and on gateway but is different"
argument_list|,
name|shard
argument_list|,
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: [{}] exists on remote node, does not exists on gateway"
argument_list|,
name|shard
argument_list|,
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
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
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: node elected for pre_allocation [{}], total_size_matched [{}]"
argument_list|,
name|shard
argument_list|,
name|discoNode
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|sizeMatched
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: node ignored for pre_allocation [{}], total_size_matched [{}] smaller than last_size_matched [{}]"
argument_list|,
name|shard
argument_list|,
name|discoNode
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|sizeMatched
argument_list|)
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|lastSizeMatched
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
else|else
block|{
comment|// if its backup, see if there is a primary that *is* allocated, and try and assign a location that is closest to it
comment|// note, since we replicate operations, this might not be the same (different flush intervals)
name|MutableShardRouting
name|primaryShard
init|=
name|routingNodes
operator|.
name|findPrimaryForReplica
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
name|DiscoveryNode
name|primaryNode
init|=
name|nodes
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
name|primaryNode
operator|!=
literal|null
condition|)
block|{
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
name|primaryNodeStore
init|=
name|shardStores
operator|.
name|get
argument_list|(
name|primaryNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryNodeStore
operator|!=
literal|null
operator|&&
name|primaryNodeStore
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
name|primaryNodeStore
operator|.
name|fileExists
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
name|primaryNodeStore
operator|.
name|file
argument_list|(
name|storeFileMetaData
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|isSame
argument_list|(
name|storeFileMetaData
argument_list|)
condition|)
block|{
name|sizeMatched
operator|+=
name|storeFileMetaData
operator|.
name|length
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
block|}
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
name|nodeAllocations
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|lastNodeMatched
argument_list|,
name|allocation
argument_list|)
operator|==
name|NodeAllocation
operator|.
name|Decision
operator|.
name|THROTTLE
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}]: throttling allocation [{}] to [{}] in order to reuse its unallocated persistent store with total_size [{}]"
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
name|shard
argument_list|,
name|lastDiscoNodeMatched
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|lastSizeMatched
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// we are throttling this, but we have enough to allocate to this node, ignore it for now
name|unassignedIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
name|routingNodes
operator|.
name|ignoredUnassigned
argument_list|()
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"[{}][{}]: allocating [{}] to [{}] in order to reuse its unallocated persistent store with total_size [{}]"
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
name|shard
argument_list|,
name|lastDiscoNodeMatched
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|lastSizeMatched
argument_list|)
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
block|}
return|return
name|changed
return|;
block|}
DECL|method|buildShardStores
specifier|private
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
argument_list|>
name|buildShardStores
parameter_list|(
name|DiscoveryNodes
name|nodes
parameter_list|,
name|MutableShardRouting
name|shard
parameter_list|)
block|{
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
argument_list|>
name|shardStores
init|=
name|cachedStores
operator|.
name|get
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodesIds
decl_stmt|;
if|if
condition|(
name|shardStores
operator|==
literal|null
condition|)
block|{
name|shardStores
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|cachedStores
operator|.
name|put
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardStores
argument_list|)
expr_stmt|;
name|nodesIds
operator|=
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|keySet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|nodesIds
operator|=
name|Sets
operator|.
name|newHashSet
argument_list|()
expr_stmt|;
comment|// clean nodes that have failed
for|for
control|(
name|Iterator
argument_list|<
name|DiscoveryNode
argument_list|>
name|it
init|=
name|shardStores
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nodes
operator|.
name|nodeExists
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|shardStores
operator|.
name|containsKey
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|nodesIds
operator|.
name|add
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|nodesIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|TransportNodesListShardStoreMetaData
operator|.
name|NodesStoreFilesMetaData
name|nodesStoreFilesMetaData
init|=
name|listShardStoreMetaData
operator|.
name|list
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
literal|false
argument_list|,
name|nodesIds
argument_list|,
name|listTimeout
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
if|if
condition|(
name|nodesStoreFilesMetaData
operator|.
name|failures
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|shard
operator|+
literal|": failures when trying to list stores on nodes:"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodesStoreFilesMetaData
operator|.
name|failures
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Throwable
name|cause
init|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|nodesStoreFilesMetaData
operator|.
name|failures
argument_list|()
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|ConnectTransportException
condition|)
block|{
continue|continue;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n    -> "
argument_list|)
operator|.
name|append
argument_list|(
name|nodesStoreFilesMetaData
operator|.
name|failures
argument_list|()
index|[
name|i
index|]
operator|.
name|getDetailedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|nodeStoreFilesMetaData
operator|.
name|storeFilesMetaData
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|shardStores
operator|.
name|put
argument_list|(
name|nodeStoreFilesMetaData
operator|.
name|node
argument_list|()
argument_list|,
name|nodeStoreFilesMetaData
operator|.
name|storeFilesMetaData
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|shardStores
return|;
block|}
block|}
end_class

end_unit

