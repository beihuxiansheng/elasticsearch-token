begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.allocator
package|package
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
name|allocator
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
name|ObjectIntOpenHashMap
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
name|FailedRerouteAllocation
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
name|StartedRerouteAllocation
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
name|AllocationDecider
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardRoutingState
operator|.
name|INITIALIZING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardRoutingState
operator|.
name|STARTED
import|;
end_import

begin_comment
comment|/**  * A {@link ShardsAllocator} that tries to balance shards across nodes in the  * cluster such that each node holds approximatly the same number of shards. The  * allocations algorithm operates on a cluster ie. is index-agnostic. While the  * number of shards per node might be balanced across the cluster a single node  * can hold mulitple shards from a single index such that the shard of an index  * are not necessarily balanced across nodes. Yet, due to high-level  * {@link AllocationDecider decisions} multiple instances of the same shard  * won't be allocated on the same node.  *<p>  * During {@link #rebalance(RoutingAllocation) re-balancing} the allocator takes  * shards from the<tt>most busy</tt> nodes and tries to relocate the shards to  * the least busy node until the number of shards per node are equal for all  * nodes in the cluster or until no shards can be relocated anymore.  *</p>  */
end_comment

begin_class
DECL|class|EvenShardsCountAllocator
specifier|public
class|class
name|EvenShardsCountAllocator
extends|extends
name|AbstractComponent
implements|implements
name|ShardsAllocator
block|{
annotation|@
name|Inject
DECL|method|EvenShardsCountAllocator
specifier|public
name|EvenShardsCountAllocator
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
annotation|@
name|Override
DECL|method|applyStartedShards
specifier|public
name|void
name|applyStartedShards
parameter_list|(
name|StartedRerouteAllocation
name|allocation
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|applyFailedShards
specifier|public
name|void
name|applyFailedShards
parameter_list|(
name|FailedRerouteAllocation
name|allocation
parameter_list|)
block|{     }
annotation|@
name|Override
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
name|RoutingNodes
name|routingNodes
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
decl_stmt|;
comment|/*           * 1. order nodes by the number of shards allocated on them least one first (this takes relocation into account)          *    ie. if a shard is relocating the target nodes shard count is incremented.          * 2. iterate over the unassigned shards          *    2a. find the least busy node in the cluster that allows allocation for the current unassigned shard          *    2b. if a node is found add the shard to the node and remove it from the unassigned shards          * 3. iterate over the remaining unassigned shards and try to allocate them on next possible node          */
comment|// order nodes by number of shards (asc)
name|RoutingNode
index|[]
name|nodes
init|=
name|sortedNodesLeastToHigh
argument_list|(
name|allocation
argument_list|)
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
name|int
name|lastNode
init|=
literal|0
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
comment|// do the allocation, finding the least "busy" node
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RoutingNode
name|node
init|=
name|nodes
index|[
name|lastNode
index|]
decl_stmt|;
name|lastNode
operator|++
expr_stmt|;
if|if
condition|(
name|lastNode
operator|==
name|nodes
operator|.
name|length
condition|)
block|{
name|lastNode
operator|=
literal|0
expr_stmt|;
block|}
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
name|int
name|numberOfShardsToAllocate
init|=
name|routingNodes
operator|.
name|requiredAverageNumberOfShardsPerNode
argument_list|()
operator|-
name|node
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|numberOfShardsToAllocate
operator|<=
literal|0
condition|)
block|{
continue|continue;
block|}
name|changed
operator|=
literal|true
expr_stmt|;
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|assign
argument_list|(
name|shard
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|unassignedIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// allocate all the unassigned shards above the average per node.
for|for
control|(
name|Iterator
argument_list|<
name|MutableShardRouting
argument_list|>
name|it
init|=
name|routingNodes
operator|.
name|unassigned
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
name|MutableShardRouting
name|shard
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// go over the nodes and try and allocate the remaining ones
for|for
control|(
name|RoutingNode
name|routingNode
range|:
name|sortedNodesLeastToHigh
argument_list|(
name|allocation
argument_list|)
control|)
block|{
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
name|routingNode
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
name|changed
operator|=
literal|true
expr_stmt|;
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|assign
argument_list|(
name|shard
argument_list|,
name|routingNode
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|changed
return|;
block|}
annotation|@
name|Override
DECL|method|rebalance
specifier|public
name|boolean
name|rebalance
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
comment|// take shards form busy nodes and move them to less busy nodes
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|RoutingNode
index|[]
name|sortedNodesLeastToHigh
init|=
name|sortedNodesLeastToHigh
argument_list|(
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortedNodesLeastToHigh
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|lowIndex
init|=
literal|0
decl_stmt|;
name|int
name|highIndex
init|=
name|sortedNodesLeastToHigh
operator|.
name|length
operator|-
literal|1
decl_stmt|;
name|boolean
name|relocationPerformed
decl_stmt|;
do|do
block|{
name|relocationPerformed
operator|=
literal|false
expr_stmt|;
while|while
condition|(
name|lowIndex
operator|!=
name|highIndex
condition|)
block|{
name|RoutingNode
name|lowRoutingNode
init|=
name|sortedNodesLeastToHigh
index|[
name|lowIndex
index|]
decl_stmt|;
name|RoutingNode
name|highRoutingNode
init|=
name|sortedNodesLeastToHigh
index|[
name|highIndex
index|]
decl_stmt|;
name|int
name|averageNumOfShards
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|requiredAverageNumberOfShardsPerNode
argument_list|()
decl_stmt|;
comment|// only active shards can be removed so must count only active ones.
if|if
condition|(
name|highRoutingNode
operator|.
name|numberOfOwningShards
argument_list|()
operator|<=
name|averageNumOfShards
condition|)
block|{
name|highIndex
operator|--
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|lowRoutingNode
operator|.
name|size
argument_list|()
operator|>=
name|averageNumOfShards
condition|)
block|{
name|lowIndex
operator|++
expr_stmt|;
continue|continue;
block|}
comment|// Take a started shard from a "busy" node and move it to less busy node and go on
name|boolean
name|relocated
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|startedShards
init|=
name|highRoutingNode
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|startedShard
range|:
name|startedShards
control|)
block|{
name|Decision
name|rebalanceDecision
init|=
name|allocation
operator|.
name|deciders
argument_list|()
operator|.
name|canRebalance
argument_list|(
name|startedShard
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|rebalanceDecision
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
name|Decision
name|allocateDecision
init|=
name|allocation
operator|.
name|deciders
argument_list|()
operator|.
name|canAllocate
argument_list|(
name|startedShard
argument_list|,
name|lowRoutingNode
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|allocateDecision
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
name|changed
operator|=
literal|true
expr_stmt|;
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|assign
argument_list|(
operator|new
name|MutableShardRouting
argument_list|(
name|startedShard
operator|.
name|index
argument_list|()
argument_list|,
name|startedShard
operator|.
name|id
argument_list|()
argument_list|,
name|lowRoutingNode
operator|.
name|nodeId
argument_list|()
argument_list|,
name|startedShard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|startedShard
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|startedShard
operator|.
name|primary
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|,
name|startedShard
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|,
name|lowRoutingNode
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|relocate
argument_list|(
name|startedShard
argument_list|,
name|lowRoutingNode
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|relocated
operator|=
literal|true
expr_stmt|;
name|relocationPerformed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|relocated
condition|)
block|{
name|highIndex
operator|--
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|relocationPerformed
condition|)
do|;
return|return
name|changed
return|;
block|}
annotation|@
name|Override
DECL|method|move
specifier|public
name|boolean
name|move
parameter_list|(
name|MutableShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
if|if
condition|(
operator|!
name|shardRouting
operator|.
name|started
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
name|RoutingNode
index|[]
name|sortedNodesLeastToHigh
init|=
name|sortedNodesLeastToHigh
argument_list|(
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|sortedNodesLeastToHigh
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|RoutingNode
name|nodeToCheck
range|:
name|sortedNodesLeastToHigh
control|)
block|{
comment|// check if its the node we are moving from, no sense to check on it
if|if
condition|(
name|nodeToCheck
operator|.
name|nodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
name|shardRouting
argument_list|,
name|nodeToCheck
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
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|assign
argument_list|(
operator|new
name|MutableShardRouting
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|,
name|shardRouting
operator|.
name|id
argument_list|()
argument_list|,
name|nodeToCheck
operator|.
name|nodeId
argument_list|()
argument_list|,
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|shardRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|,
name|shardRouting
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|,
name|nodeToCheck
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|relocate
argument_list|(
name|shardRouting
argument_list|,
name|nodeToCheck
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|changed
return|;
block|}
DECL|method|sortedNodesLeastToHigh
specifier|private
name|RoutingNode
index|[]
name|sortedNodesLeastToHigh
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
comment|// create count per node id, taking into account relocations
specifier|final
name|ObjectIntOpenHashMap
argument_list|<
name|String
argument_list|>
name|nodeCounts
init|=
operator|new
name|ObjectIntOpenHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RoutingNode
name|node
range|:
name|allocation
operator|.
name|routingNodes
argument_list|()
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|node
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ShardRouting
name|shardRouting
init|=
name|node
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|nodeId
init|=
name|shardRouting
operator|.
name|relocating
argument_list|()
condition|?
name|shardRouting
operator|.
name|relocatingNodeId
argument_list|()
else|:
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
decl_stmt|;
name|nodeCounts
operator|.
name|addTo
argument_list|(
name|nodeId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|RoutingNode
index|[]
name|nodes
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
operator|new
name|Comparator
argument_list|<
name|RoutingNode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|RoutingNode
name|o1
parameter_list|,
name|RoutingNode
name|o2
parameter_list|)
block|{
return|return
name|nodeCounts
operator|.
name|get
argument_list|(
name|o1
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|-
name|nodeCounts
operator|.
name|get
argument_list|(
name|o2
operator|.
name|nodeId
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|nodes
return|;
block|}
block|}
end_class

end_unit

