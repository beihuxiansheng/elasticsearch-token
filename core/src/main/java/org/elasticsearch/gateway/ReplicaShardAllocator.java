begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectLongCursor
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
name|ClusterChangedEvent
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
name|UnassignedInfo
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
name|RoutingAllocation
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
name|decider
operator|.
name|Decision
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ReplicaShardAllocator
specifier|public
specifier|abstract
class|class
name|ReplicaShardAllocator
extends|extends
name|AbstractComponent
block|{
DECL|method|ReplicaShardAllocator
specifier|public
name|ReplicaShardAllocator
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Process existing recoveries of replicas and see if we need to cancel them if we find a better      * match. Today, a better match is one that has full sync id match compared to not having one in      * the previous recovery.      */
DECL|method|processExistingRecoveries
specifier|public
name|boolean
name|processExistingRecoveries
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RoutingNodes
operator|.
name|RoutingNodesIterator
name|nodes
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|nodes
argument_list|()
init|;
name|nodes
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|nodes
operator|.
name|next
argument_list|()
expr_stmt|;
for|for
control|(
name|RoutingNodes
operator|.
name|RoutingNodeIterator
name|it
init|=
name|nodes
operator|.
name|nodeShards
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ShardRouting
name|shard
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|==
literal|true
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|shard
operator|.
name|initializing
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|shard
operator|.
name|relocatingNodeId
argument_list|()
operator|!=
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// if we are allocating a replica because of index creation, no need to go and find a copy, there isn't one...
if|if
condition|(
name|shard
operator|.
name|allocatedPostIndexCreate
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|shardStores
init|=
name|fetchData
argument_list|(
name|shard
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardStores
operator|.
name|hasData
argument_list|()
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: fetching new stores for initializing shard"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
continue|continue;
comment|// still fetching
block|}
name|ShardRouting
name|primaryShard
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|activePrimary
argument_list|(
name|shard
argument_list|)
decl_stmt|;
assert|assert
name|primaryShard
operator|!=
literal|null
operator|:
literal|"the replica shard can be allocated on at least one node, so there must be an active primary"
assert|;
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
name|primaryStore
init|=
name|findStore
argument_list|(
name|primaryShard
argument_list|,
name|allocation
argument_list|,
name|shardStores
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryStore
operator|==
literal|null
operator|||
name|primaryStore
operator|.
name|allocated
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// if we can't find the primary data, it is probably because the primary shard is corrupted (and listing failed)
comment|// just let the recovery find it out, no need to do anything about it for the initializing shard
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: no primary shard store found or allocated, letting actual allocation figure it out"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|MatchingNodes
name|matchingNodes
init|=
name|findMatchingNodes
argument_list|(
name|shard
argument_list|,
name|allocation
argument_list|,
name|primaryStore
argument_list|,
name|shardStores
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingNodes
operator|.
name|getNodeWithHighestMatch
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|DiscoveryNode
name|currentNode
init|=
name|allocation
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|nodeWithHighestMatch
init|=
name|matchingNodes
operator|.
name|getNodeWithHighestMatch
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentNode
operator|.
name|equals
argument_list|(
name|nodeWithHighestMatch
argument_list|)
operator|==
literal|false
operator|&&
name|matchingNodes
operator|.
name|isNodeMatchBySyncID
argument_list|(
name|currentNode
argument_list|)
operator|==
literal|false
operator|&&
name|matchingNodes
operator|.
name|isNodeMatchBySyncID
argument_list|(
name|nodeWithHighestMatch
argument_list|)
operator|==
literal|true
condition|)
block|{
comment|// we found a better match that has a full sync id match, the existing allocation is not fully synced
comment|// so we found a better one, cancel this one
name|it
operator|.
name|moveToUnassigned
argument_list|(
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|REALLOCATED_REPLICA
argument_list|,
literal|"existing allocation of replica to ["
operator|+
name|currentNode
operator|+
literal|"] cancelled, sync id match found on node ["
operator|+
name|nodeWithHighestMatch
operator|+
literal|"]"
argument_list|,
literal|null
argument_list|,
name|allocation
operator|.
name|getCurrentNanoTime
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|changed
return|;
block|}
DECL|method|allocateUnassigned
specifier|public
name|boolean
name|allocateUnassigned
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
specifier|final
name|RoutingNodes
name|routingNodes
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
decl_stmt|;
specifier|final
name|RoutingNodes
operator|.
name|UnassignedShards
operator|.
name|UnassignedIterator
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
name|ShardRouting
name|shard
init|=
name|unassignedIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// if we are allocating a replica because of index creation, no need to go and find a copy, there isn't one...
if|if
condition|(
name|shard
operator|.
name|allocatedPostIndexCreate
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
comment|// pre-check if it can be allocated to any node that currently exists, so we won't list the store for it for nothing
if|if
condition|(
name|canBeAllocatedToAtLeastOneNode
argument_list|(
name|shard
argument_list|,
name|allocation
argument_list|)
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: ignoring allocation, can't be allocated on any node"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|unassignedIterator
operator|.
name|removeAndIgnore
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|shardStores
init|=
name|fetchData
argument_list|(
name|shard
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardStores
operator|.
name|hasData
argument_list|()
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: ignoring allocation, still fetching shard stores"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
name|allocation
operator|.
name|setHasPendingAsyncFetch
argument_list|()
expr_stmt|;
name|unassignedIterator
operator|.
name|removeAndIgnore
argument_list|()
expr_stmt|;
continue|continue;
comment|// still fetching
block|}
name|ShardRouting
name|primaryShard
init|=
name|routingNodes
operator|.
name|activePrimary
argument_list|(
name|shard
argument_list|)
decl_stmt|;
assert|assert
name|primaryShard
operator|!=
literal|null
operator|:
literal|"the replica shard can be allocated on at least one node, so there must be an active primary"
assert|;
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
name|primaryStore
init|=
name|findStore
argument_list|(
name|primaryShard
argument_list|,
name|allocation
argument_list|,
name|shardStores
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryStore
operator|==
literal|null
operator|||
name|primaryStore
operator|.
name|allocated
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// if we can't find the primary data, it is probably because the primary shard is corrupted (and listing failed)
comment|// we want to let the replica be allocated in order to expose the actual problem with the primary that the replica
comment|// will try and recover from
comment|// Note, this is the existing behavior, as exposed in running CorruptFileTest#testNoPrimaryData
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: no primary shard store found or allocated, letting actual allocation figure it out"
argument_list|,
name|shard
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|MatchingNodes
name|matchingNodes
init|=
name|findMatchingNodes
argument_list|(
name|shard
argument_list|,
name|allocation
argument_list|,
name|primaryStore
argument_list|,
name|shardStores
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchingNodes
operator|.
name|getNodeWithHighestMatch
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|RoutingNode
name|nodeWithHighestMatch
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|matchingNodes
operator|.
name|getNodeWithHighestMatch
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
comment|// we only check on THROTTLE since we checked before before on NO
name|Decision
name|decision
init|=
name|allocation
operator|.
name|deciders
argument_list|()
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|nodeWithHighestMatch
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|decision
operator|.
name|type
argument_list|()
operator|==
name|Decision
operator|.
name|Type
operator|.
name|THROTTLE
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}]: throttling allocation [{}] to [{}] in order to reuse its unallocated persistent store"
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
name|nodeWithHighestMatch
operator|.
name|node
argument_list|()
argument_list|)
expr_stmt|;
comment|// we are throttling this, but we have enough to allocate to this node, ignore it for now
name|unassignedIterator
operator|.
name|removeAndIgnore
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}]: allocating [{}] to [{}] in order to reuse its unallocated persistent store"
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
name|nodeWithHighestMatch
operator|.
name|node
argument_list|()
argument_list|)
expr_stmt|;
comment|// we found a match
name|changed
operator|=
literal|true
expr_stmt|;
name|unassignedIterator
operator|.
name|initialize
argument_list|(
name|nodeWithHighestMatch
operator|.
name|nodeId
argument_list|()
argument_list|,
name|shard
operator|.
name|version
argument_list|()
argument_list|,
name|allocation
operator|.
name|clusterInfo
argument_list|()
operator|.
name|getShardSize
argument_list|(
name|shard
argument_list|,
name|ShardRouting
operator|.
name|UNAVAILABLE_EXPECTED_SHARD_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|matchingNodes
operator|.
name|hasAnyData
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// if we didn't manage to find *any* data (regardless of matching sizes), check if the allocation of the replica shard needs to be delayed
name|changed
operator||=
name|ignoreUnassignedIfDelayed
argument_list|(
name|unassignedIterator
argument_list|,
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|changed
return|;
block|}
comment|/**      * Check if the allocation of the replica is to be delayed. Compute the delay and if it is delayed, add it to the ignore unassigned list      * Note: we only care about replica in delayed allocation, since if we have an unassigned primary it      *       will anyhow wait to find an existing copy of the shard to be allocated      * Note: the other side of the equation is scheduling a reroute in a timely manner, which happens in the RoutingService      *      * PUBLIC FOR TESTS!      *      * @param unassignedIterator iterator over unassigned shards      * @param shard the shard which might be delayed      * @return true iff allocation is delayed for this shard      */
DECL|method|ignoreUnassignedIfDelayed
specifier|public
name|boolean
name|ignoreUnassignedIfDelayed
parameter_list|(
name|RoutingNodes
operator|.
name|UnassignedShards
operator|.
name|UnassignedIterator
name|unassignedIterator
parameter_list|,
name|ShardRouting
name|shard
parameter_list|)
block|{
comment|// calculate delay and store it in UnassignedInfo to be used by RoutingService
name|long
name|delay
init|=
name|shard
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getLastComputedLeftDelayNanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}]: delaying allocation of [{}] for [{}]"
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
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|delay
argument_list|)
argument_list|)
expr_stmt|;
comment|/**              * mark it as changed, since we want to kick a publishing to schedule future allocation,              * see {@link org.elasticsearch.cluster.routing.RoutingService#clusterChanged(ClusterChangedEvent)}).              */
name|unassignedIterator
operator|.
name|removeAndIgnore
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Can the shard be allocated on at least one node based on the allocation deciders.      */
DECL|method|canBeAllocatedToAtLeastOneNode
specifier|private
name|boolean
name|canBeAllocatedToAtLeastOneNode
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
for|for
control|(
name|ObjectCursor
argument_list|<
name|DiscoveryNode
argument_list|>
name|cursor
range|:
name|allocation
operator|.
name|nodes
argument_list|()
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
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|cursor
operator|.
name|value
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
comment|// if we can't allocate it on a node, ignore it, for example, this handles
comment|// cases for only allocating a replica after a primary
name|Decision
name|decision
init|=
name|allocation
operator|.
name|deciders
argument_list|()
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|decision
operator|.
name|type
argument_list|()
operator|==
name|Decision
operator|.
name|Type
operator|.
name|YES
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Finds the store for the assigned shard in the fetched data, returns null if none is found.      */
DECL|method|findStore
specifier|private
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
name|findStore
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|,
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|data
parameter_list|)
block|{
assert|assert
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|!=
literal|null
assert|;
name|DiscoveryNode
name|primaryNode
init|=
name|allocation
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryNode
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
name|primaryNodeFilesStore
init|=
name|data
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|primaryNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|primaryNodeFilesStore
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|primaryNodeFilesStore
operator|.
name|storeFilesMetaData
argument_list|()
return|;
block|}
DECL|method|findMatchingNodes
specifier|private
name|MatchingNodes
name|findMatchingNodes
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|,
name|TransportNodesListShardStoreMetaData
operator|.
name|StoreFilesMetaData
name|primaryStore
parameter_list|,
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|data
parameter_list|)
block|{
name|ObjectLongMap
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToSize
init|=
operator|new
name|ObjectLongHashMap
argument_list|<>
argument_list|()
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
name|NodeStoreFilesMetaData
argument_list|>
name|nodeStoreEntry
range|:
name|data
operator|.
name|getData
argument_list|()
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
name|allocation
operator|.
name|routingNodes
argument_list|()
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
name|Decision
name|decision
init|=
name|allocation
operator|.
name|deciders
argument_list|()
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|decision
operator|.
name|type
argument_list|()
operator|==
name|Decision
operator|.
name|Type
operator|.
name|NO
condition|)
block|{
continue|continue;
block|}
comment|// if it is already allocated, we can't assign to it... (and it might be primary as well)
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
comment|// we don't have any files at all, it is an empty index
if|if
condition|(
name|storeFilesMetaData
operator|.
name|iterator
argument_list|()
operator|.
name|hasNext
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
name|String
name|primarySyncId
init|=
name|primaryStore
operator|.
name|syncId
argument_list|()
decl_stmt|;
name|String
name|replicaSyncId
init|=
name|storeFilesMetaData
operator|.
name|syncId
argument_list|()
decl_stmt|;
comment|// see if we have a sync id we can make use of
if|if
condition|(
name|replicaSyncId
operator|!=
literal|null
operator|&&
name|replicaSyncId
operator|.
name|equals
argument_list|(
name|primarySyncId
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: node [{}] has same sync id {} as primary"
argument_list|,
name|shard
argument_list|,
name|discoNode
operator|.
name|name
argument_list|()
argument_list|,
name|replicaSyncId
argument_list|)
expr_stmt|;
name|nodesToSize
operator|.
name|put
argument_list|(
name|discoNode
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
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
name|String
name|metaDataFileName
init|=
name|storeFileMetaData
operator|.
name|name
argument_list|()
decl_stmt|;
if|if
condition|(
name|primaryStore
operator|.
name|fileExists
argument_list|(
name|metaDataFileName
argument_list|)
operator|&&
name|primaryStore
operator|.
name|file
argument_list|(
name|metaDataFileName
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
name|logger
operator|.
name|trace
argument_list|(
literal|"{}: node [{}] has [{}/{}] bytes of re-usable data"
argument_list|,
name|shard
argument_list|,
name|discoNode
operator|.
name|name
argument_list|()
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|sizeMatched
argument_list|)
argument_list|,
name|sizeMatched
argument_list|)
expr_stmt|;
name|nodesToSize
operator|.
name|put
argument_list|(
name|discoNode
argument_list|,
name|sizeMatched
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MatchingNodes
argument_list|(
name|nodesToSize
argument_list|)
return|;
block|}
DECL|method|fetchData
specifier|protected
specifier|abstract
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|fetchData
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
function_decl|;
DECL|class|MatchingNodes
specifier|static
class|class
name|MatchingNodes
block|{
DECL|field|nodesToSize
specifier|private
specifier|final
name|ObjectLongMap
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToSize
decl_stmt|;
DECL|field|nodeWithHighestMatch
specifier|private
specifier|final
name|DiscoveryNode
name|nodeWithHighestMatch
decl_stmt|;
DECL|method|MatchingNodes
specifier|public
name|MatchingNodes
parameter_list|(
name|ObjectLongMap
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToSize
parameter_list|)
block|{
name|this
operator|.
name|nodesToSize
operator|=
name|nodesToSize
expr_stmt|;
name|long
name|highestMatchSize
init|=
literal|0
decl_stmt|;
name|DiscoveryNode
name|highestMatchNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ObjectLongCursor
argument_list|<
name|DiscoveryNode
argument_list|>
name|cursor
range|:
name|nodesToSize
control|)
block|{
if|if
condition|(
name|cursor
operator|.
name|value
operator|>
name|highestMatchSize
condition|)
block|{
name|highestMatchSize
operator|=
name|cursor
operator|.
name|value
expr_stmt|;
name|highestMatchNode
operator|=
name|cursor
operator|.
name|key
expr_stmt|;
block|}
block|}
name|this
operator|.
name|nodeWithHighestMatch
operator|=
name|highestMatchNode
expr_stmt|;
block|}
comment|/**          * Returns the node with the highest "non zero byte" match compared to          * the primary.          */
annotation|@
name|Nullable
DECL|method|getNodeWithHighestMatch
specifier|public
name|DiscoveryNode
name|getNodeWithHighestMatch
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeWithHighestMatch
return|;
block|}
DECL|method|isNodeMatchBySyncID
specifier|public
name|boolean
name|isNodeMatchBySyncID
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
return|return
name|nodesToSize
operator|.
name|get
argument_list|(
name|node
argument_list|)
operator|==
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
comment|/**          * Did we manage to find any data, regardless how well they matched or not.          */
DECL|method|hasAnyData
specifier|public
name|boolean
name|hasAnyData
parameter_list|()
block|{
return|return
name|nodesToSize
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

