begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
name|metadata
operator|.
name|MetaData
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
name|concurrent
operator|.
name|NotThreadSafe
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
DECL|class|RoutingNodes
specifier|public
class|class
name|RoutingNodes
implements|implements
name|Iterable
argument_list|<
name|RoutingNode
argument_list|>
block|{
DECL|field|metaData
specifier|private
specifier|final
name|MetaData
name|metaData
decl_stmt|;
DECL|field|routingTable
specifier|private
specifier|final
name|RoutingTable
name|routingTable
decl_stmt|;
DECL|field|nodesToShards
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingNode
argument_list|>
name|nodesToShards
init|=
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|unassigned
specifier|private
specifier|final
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|unassigned
init|=
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|RoutingNodes
specifier|public
name|RoutingNodes
parameter_list|(
name|MetaData
name|metaData
parameter_list|,
name|RoutingTable
name|routingTable
parameter_list|)
block|{
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
name|this
operator|.
name|routingTable
operator|=
name|routingTable
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
argument_list|>
name|nodesToShards
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|routingTable
operator|.
name|indicesRouting
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|indexRoutingTable
control|)
block|{
for|for
control|(
name|ShardRouting
name|shard
range|:
name|indexShard
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|assignedToNode
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|entries
init|=
name|nodesToShards
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
name|entries
operator|==
literal|null
condition|)
block|{
name|entries
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|nodesToShards
operator|.
name|put
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
name|entries
operator|.
name|add
argument_list|(
operator|new
name|MutableShardRouting
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|shard
operator|.
name|relocating
argument_list|()
condition|)
block|{
name|entries
operator|=
name|nodesToShards
operator|.
name|get
argument_list|(
name|shard
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|entries
operator|==
literal|null
condition|)
block|{
name|entries
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
name|nodesToShards
operator|.
name|put
argument_list|(
name|shard
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|entries
argument_list|)
expr_stmt|;
block|}
comment|// add the counterpart shard with relocatingNodeId reflecting the source from which
comment|// it's relocating from.
name|entries
operator|.
name|add
argument_list|(
operator|new
name|MutableShardRouting
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
argument_list|,
name|shard
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shard
operator|.
name|primary
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|unassigned
operator|.
name|add
argument_list|(
operator|new
name|MutableShardRouting
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
argument_list|>
name|entry
range|:
name|nodesToShards
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|nodeId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|this
operator|.
name|nodesToShards
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
operator|new
name|RoutingNode
argument_list|(
name|nodeId
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|RoutingNode
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|nodesToShards
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|routingTable
specifier|public
name|RoutingTable
name|routingTable
parameter_list|()
block|{
return|return
name|routingTable
return|;
block|}
DECL|method|getRoutingTable
specifier|public
name|RoutingTable
name|getRoutingTable
parameter_list|()
block|{
return|return
name|routingTable
argument_list|()
return|;
block|}
DECL|method|metaData
specifier|public
name|MetaData
name|metaData
parameter_list|()
block|{
return|return
name|this
operator|.
name|metaData
return|;
block|}
DECL|method|getMetaData
specifier|public
name|MetaData
name|getMetaData
parameter_list|()
block|{
return|return
name|metaData
argument_list|()
return|;
block|}
DECL|method|requiredAverageNumberOfShardsPerNode
specifier|public
name|int
name|requiredAverageNumberOfShardsPerNode
parameter_list|()
block|{
return|return
name|metaData
operator|.
name|totalNumberOfShards
argument_list|()
operator|/
name|nodesToShards
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|hasUnassigned
specifier|public
name|boolean
name|hasUnassigned
parameter_list|()
block|{
return|return
operator|!
name|unassigned
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|unassigned
specifier|public
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|unassigned
parameter_list|()
block|{
return|return
name|this
operator|.
name|unassigned
return|;
block|}
DECL|method|getUnassigned
specifier|public
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|getUnassigned
parameter_list|()
block|{
return|return
name|unassigned
argument_list|()
return|;
block|}
DECL|method|nodesToShards
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingNode
argument_list|>
name|nodesToShards
parameter_list|()
block|{
return|return
name|nodesToShards
return|;
block|}
DECL|method|getNodesToShards
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|RoutingNode
argument_list|>
name|getNodesToShards
parameter_list|()
block|{
return|return
name|nodesToShards
argument_list|()
return|;
block|}
DECL|method|node
specifier|public
name|RoutingNode
name|node
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
return|return
name|nodesToShards
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
DECL|method|numberOfShardsOfType
specifier|public
name|int
name|numberOfShardsOfType
parameter_list|(
name|ShardRoutingState
name|state
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RoutingNode
name|routingNode
range|:
name|this
control|)
block|{
name|count
operator|+=
name|routingNode
operator|.
name|numberOfShardsWithState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|shardsOfType
specifier|public
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shardsOfType
parameter_list|(
name|ShardRoutingState
name|state
parameter_list|)
block|{
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RoutingNode
name|routingNode
range|:
name|this
control|)
block|{
name|shards
operator|.
name|addAll
argument_list|(
name|routingNode
operator|.
name|shardsWithState
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|shards
return|;
block|}
DECL|method|sortedNodesLeastToHigh
specifier|public
name|List
argument_list|<
name|RoutingNode
argument_list|>
name|sortedNodesLeastToHigh
parameter_list|()
block|{
return|return
name|nodesToShardsSorted
argument_list|(
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
name|o1
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
operator|-
name|o2
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|nodesToShardsSorted
specifier|public
name|List
argument_list|<
name|RoutingNode
argument_list|>
name|nodesToShardsSorted
parameter_list|(
name|Comparator
argument_list|<
name|RoutingNode
argument_list|>
name|comparator
parameter_list|)
block|{
name|List
argument_list|<
name|RoutingNode
argument_list|>
name|nodes
init|=
operator|new
name|ArrayList
argument_list|<
name|RoutingNode
argument_list|>
argument_list|(
name|nodesToShards
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparator
operator|!=
literal|null
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|nodes
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
block|}
return|return
name|nodes
return|;
block|}
DECL|method|prettyPrint
specifier|public
name|String
name|prettyPrint
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"Routing Nodes:\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|RoutingNode
name|routingNode
range|:
name|this
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|routingNode
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"---- Unassigned\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|MutableShardRouting
name|shardEntry
range|:
name|unassigned
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"--------"
argument_list|)
operator|.
name|append
argument_list|(
name|shardEntry
operator|.
name|shortSummary
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

