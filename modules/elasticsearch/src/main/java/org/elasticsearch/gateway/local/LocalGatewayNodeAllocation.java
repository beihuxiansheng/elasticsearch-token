begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
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
name|*
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
name|NodeAllocation
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
name|NodeAllocations
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
name|collect
operator|.
name|Tuple
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
name|shard
operator|.
name|ShardId
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
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|LocalGatewayNodeAllocation
specifier|public
class|class
name|LocalGatewayNodeAllocation
extends|extends
name|NodeAllocation
block|{
DECL|field|listGatewayState
specifier|private
specifier|final
name|TransportNodesListGatewayState
name|listGatewayState
decl_stmt|;
DECL|method|LocalGatewayNodeAllocation
annotation|@
name|Inject
specifier|public
name|LocalGatewayNodeAllocation
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportNodesListGatewayState
name|listGatewayState
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|listGatewayState
operator|=
name|listGatewayState
expr_stmt|;
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
name|RoutingNodes
name|routingNodes
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|failedShards
parameter_list|)
block|{
for|for
control|(
name|ShardRouting
name|failedShard
range|:
name|failedShards
control|)
block|{
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|routingNodes
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|failedShard
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|routingNodes
operator|.
name|blocks
argument_list|()
operator|.
name|hasIndexBlock
argument_list|(
name|indexRoutingTable
operator|.
name|index
argument_list|()
argument_list|,
name|LocalGateway
operator|.
name|INDEX_NOT_RECOVERED_BLOCK
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// we are still in the initial allocation, find another node with existing shards
comment|// all primary are unassigned for the index, see if we can allocate it on existing nodes, if not, don't assign
name|Set
argument_list|<
name|String
argument_list|>
name|nodesIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|nodesIds
operator|.
name|addAll
argument_list|(
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|nodesIds
operator|.
name|addAll
argument_list|(
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|TransportNodesListGatewayState
operator|.
name|NodesLocalGatewayState
name|nodesState
init|=
name|listGatewayState
operator|.
name|list
argument_list|(
name|nodesIds
argument_list|,
literal|null
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
comment|// make a list of ShardId to Node, each one from the latest version
name|Tuple
argument_list|<
name|DiscoveryNode
argument_list|,
name|Long
argument_list|>
name|t
init|=
literal|null
decl_stmt|;
for|for
control|(
name|TransportNodesListGatewayState
operator|.
name|NodeLocalGatewayState
name|nodeState
range|:
name|nodesState
control|)
block|{
comment|// we don't want to reallocate to the node we failed on
if|if
condition|(
name|nodeState
operator|.
name|node
argument_list|()
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
name|failedShard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
comment|// go and find
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|nodeState
operator|.
name|state
argument_list|()
operator|.
name|shards
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|failedShard
operator|.
name|shardId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|t
operator|==
literal|null
operator|||
name|entry
operator|.
name|getValue
argument_list|()
operator|>
name|t
operator|.
name|v2
argument_list|()
operator|.
name|longValue
argument_list|()
condition|)
block|{
name|t
operator|=
operator|new
name|Tuple
argument_list|<
name|DiscoveryNode
argument_list|,
name|Long
argument_list|>
argument_list|(
name|nodeState
operator|.
name|node
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
comment|// we found a node to allocate to, do it
name|RoutingNode
name|currentRoutingNode
init|=
name|routingNodes
operator|.
name|nodesToShards
argument_list|()
operator|.
name|get
argument_list|(
name|failedShard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentRoutingNode
operator|==
literal|null
condition|)
block|{
comment|// already failed (might be called several times for the same shard)
continue|continue;
block|}
comment|// find the shard and cancel relocation
name|Iterator
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
init|=
name|currentRoutingNode
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|shards
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|MutableShardRouting
name|shard
init|=
name|shards
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|.
name|shardId
argument_list|()
operator|.
name|equals
argument_list|(
name|failedShard
operator|.
name|shardId
argument_list|()
argument_list|)
condition|)
block|{
name|shard
operator|.
name|deassignNode
argument_list|()
expr_stmt|;
name|shards
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|RoutingNode
name|targetNode
init|=
name|routingNodes
operator|.
name|nodesToShards
argument_list|()
operator|.
name|get
argument_list|(
name|t
operator|.
name|v1
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|targetNode
operator|.
name|add
argument_list|(
operator|new
name|MutableShardRouting
argument_list|(
name|failedShard
operator|.
name|index
argument_list|()
argument_list|,
name|failedShard
operator|.
name|id
argument_list|()
argument_list|,
name|targetNode
operator|.
name|nodeId
argument_list|()
argument_list|,
name|failedShard
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|failedShard
operator|.
name|primary
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|RoutingNodes
name|routingNodes
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|routingNodes
operator|.
name|routingTable
argument_list|()
control|)
block|{
comment|// only do the allocation if there is a local "INDEX NOT RECOVERED" block
if|if
condition|(
operator|!
name|routingNodes
operator|.
name|blocks
argument_list|()
operator|.
name|hasIndexBlock
argument_list|(
name|indexRoutingTable
operator|.
name|index
argument_list|()
argument_list|,
name|LocalGateway
operator|.
name|INDEX_NOT_RECOVERED_BLOCK
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|indexRoutingTable
operator|.
name|allPrimaryShardsUnassigned
argument_list|()
condition|)
block|{
comment|// all primary are unassigned for the index, see if we can allocate it on existing nodes, if not, don't assign
name|Set
argument_list|<
name|String
argument_list|>
name|nodesIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|nodesIds
operator|.
name|addAll
argument_list|(
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|nodesIds
operator|.
name|addAll
argument_list|(
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
expr_stmt|;
name|TransportNodesListGatewayState
operator|.
name|NodesLocalGatewayState
name|nodesState
init|=
name|listGatewayState
operator|.
name|list
argument_list|(
name|nodesIds
argument_list|,
literal|null
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
comment|// make a list of ShardId to Node, each one from the latest version
name|Map
argument_list|<
name|ShardId
argument_list|,
name|Tuple
argument_list|<
name|DiscoveryNode
argument_list|,
name|Long
argument_list|>
argument_list|>
name|shards
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|TransportNodesListGatewayState
operator|.
name|NodeLocalGatewayState
name|nodeState
range|:
name|nodesState
control|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|nodeState
operator|.
name|state
argument_list|()
operator|.
name|shards
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|indexRoutingTable
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|Tuple
argument_list|<
name|DiscoveryNode
argument_list|,
name|Long
argument_list|>
name|t
init|=
name|shards
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|==
literal|null
operator|||
name|entry
operator|.
name|getValue
argument_list|()
operator|>
name|t
operator|.
name|v2
argument_list|()
operator|.
name|longValue
argument_list|()
condition|)
block|{
name|t
operator|=
operator|new
name|Tuple
argument_list|<
name|DiscoveryNode
argument_list|,
name|Long
argument_list|>
argument_list|(
name|nodeState
operator|.
name|node
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|shards
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// check if we managed to allocate to all of them, if not, move all relevant shards to ignored
if|if
condition|(
name|shards
operator|.
name|size
argument_list|()
operator|<
name|indexRoutingTable
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
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
name|shardRouting
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|index
argument_list|()
operator|.
name|equals
argument_list|(
name|indexRoutingTable
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|it
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
name|shardRouting
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|changed
operator|=
literal|true
expr_stmt|;
comment|// we found all nodes to allocate to, do the allocation
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
name|shardRouting
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
name|DiscoveryNode
name|node
init|=
name|shards
operator|.
name|get
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|)
operator|.
name|v1
argument_list|()
decl_stmt|;
name|RoutingNode
name|routingNode
init|=
name|routingNodes
operator|.
name|node
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|routingNode
operator|.
name|add
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// TODO optimize replica allocation to existing work locations
return|return
name|changed
return|;
block|}
block|}
end_class

end_unit

