begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.decider
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
name|decider
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
name|IndexMetaData
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
name|RoutingAllocation
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
name|trove
operator|.
name|map
operator|.
name|hash
operator|.
name|TObjectIntHashMap
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
name|settings
operator|.
name|NodeSettingsService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
DECL|class|AwarenessAllocationDecider
specifier|public
class|class
name|AwarenessAllocationDecider
extends|extends
name|AllocationDecider
block|{
static|static
block|{
name|MetaData
operator|.
name|addDynamicSettings
argument_list|(
literal|"cluster.routing.allocation.awareness.attributes"
argument_list|,
literal|"cluster.routing.allocation.awareness.force.*"
argument_list|)
expr_stmt|;
block|}
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|NodeSettingsService
operator|.
name|Listener
block|{
DECL|method|onRefreshSettings
annotation|@
name|Override
specifier|public
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|String
index|[]
name|awarenessAttributes
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"cluster.routing.allocation.awareness.attributes"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|awarenessAttributes
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [cluster.routing.allocation.awareness.attributes] from [{}] to [{}]"
argument_list|,
name|AwarenessAllocationDecider
operator|.
name|this
operator|.
name|awarenessAttributes
argument_list|,
name|awarenessAttributes
argument_list|)
expr_stmt|;
name|AwarenessAllocationDecider
operator|.
name|this
operator|.
name|awarenessAttributes
operator|=
name|awarenessAttributes
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|forcedAwarenessAttributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
argument_list|(
name|AwarenessAllocationDecider
operator|.
name|this
operator|.
name|forcedAwarenessAttributes
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|forceGroups
init|=
name|settings
operator|.
name|getGroups
argument_list|(
literal|"cluster.routing.allocation.awareness.force."
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|forceGroups
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|forceGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
index|[]
name|aValues
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAsArray
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
if|if
condition|(
name|aValues
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|forcedAwarenessAttributes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|aValues
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|AwarenessAllocationDecider
operator|.
name|this
operator|.
name|forcedAwarenessAttributes
operator|=
name|forcedAwarenessAttributes
expr_stmt|;
block|}
block|}
DECL|field|awarenessAttributes
specifier|private
name|String
index|[]
name|awarenessAttributes
decl_stmt|;
DECL|field|forcedAwarenessAttributes
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
index|[]
argument_list|>
name|forcedAwarenessAttributes
decl_stmt|;
DECL|method|AwarenessAllocationDecider
annotation|@
name|Inject
specifier|public
name|AwarenessAllocationDecider
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeSettingsService
name|nodeSettingsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|awarenessAttributes
operator|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"cluster.routing.allocation.awareness.attributes"
argument_list|)
expr_stmt|;
name|forcedAwarenessAttributes
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|forceGroups
init|=
name|settings
operator|.
name|getGroups
argument_list|(
literal|"cluster.routing.allocation.awareness.force."
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|forceGroups
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
index|[]
name|aValues
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getAsArray
argument_list|(
literal|"values"
argument_list|)
decl_stmt|;
if|if
condition|(
name|aValues
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|forcedAwarenessAttributes
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|aValues
argument_list|)
expr_stmt|;
block|}
block|}
name|nodeSettingsService
operator|.
name|addListener
argument_list|(
operator|new
name|ApplySettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|awarenessAttributes
specifier|public
name|String
index|[]
name|awarenessAttributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|awarenessAttributes
return|;
block|}
DECL|method|canAllocate
annotation|@
name|Override
specifier|public
name|Decision
name|canAllocate
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
return|return
name|underCapacity
argument_list|(
name|shardRouting
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|,
literal|true
argument_list|)
condition|?
name|Decision
operator|.
name|YES
else|:
name|Decision
operator|.
name|NO
return|;
block|}
DECL|method|canRemain
annotation|@
name|Override
specifier|public
name|boolean
name|canRemain
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
return|return
name|underCapacity
argument_list|(
name|shardRouting
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|underCapacity
specifier|private
name|boolean
name|underCapacity
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|,
name|boolean
name|moveToNode
parameter_list|)
block|{
if|if
condition|(
name|awarenessAttributes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|IndexMetaData
name|indexMetaData
init|=
name|allocation
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|shardCount
init|=
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
operator|+
literal|1
decl_stmt|;
comment|// 1 for primary
for|for
control|(
name|String
name|awarenessAttribute
range|:
name|awarenessAttributes
control|)
block|{
comment|// the node the shard exists on must be associated with an awareness attribute
if|if
condition|(
operator|!
name|node
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|containsKey
argument_list|(
name|awarenessAttribute
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// build attr_value -> nodes map
name|TObjectIntHashMap
argument_list|<
name|String
argument_list|>
name|nodesPerAttribute
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|nodesPerAttributesCounts
argument_list|(
name|awarenessAttribute
argument_list|)
decl_stmt|;
comment|// build the count of shards per attribute value
name|TObjectIntHashMap
argument_list|<
name|String
argument_list|>
name|shardPerAttribute
init|=
operator|new
name|TObjectIntHashMap
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RoutingNode
name|routingNode
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
name|routingNode
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|MutableShardRouting
name|nodeShardRouting
init|=
name|routingNode
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeShardRouting
operator|.
name|shardId
argument_list|()
operator|.
name|equals
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|)
condition|)
block|{
comment|// if the shard is relocating, then make sure we count it as part of the node it is relocating to
if|if
condition|(
name|nodeShardRouting
operator|.
name|relocating
argument_list|()
condition|)
block|{
name|RoutingNode
name|relocationNode
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|nodeShardRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|shardPerAttribute
operator|.
name|adjustOrPutValue
argument_list|(
name|relocationNode
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeShardRouting
operator|.
name|started
argument_list|()
condition|)
block|{
name|shardPerAttribute
operator|.
name|adjustOrPutValue
argument_list|(
name|routingNode
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|moveToNode
condition|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|assignedToNode
argument_list|()
condition|)
block|{
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
if|if
condition|(
operator|!
name|node
operator|.
name|nodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
comment|// we work on different nodes, move counts around
name|shardPerAttribute
operator|.
name|adjustOrPutValue
argument_list|(
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|nodeId
argument_list|)
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|shardPerAttribute
operator|.
name|adjustOrPutValue
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|shardPerAttribute
operator|.
name|adjustOrPutValue
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|int
name|numberOfAttributes
init|=
name|nodesPerAttribute
operator|.
name|size
argument_list|()
decl_stmt|;
name|String
index|[]
name|fullValues
init|=
name|forcedAwarenessAttributes
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|fullValues
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|fullValue
range|:
name|fullValues
control|)
block|{
if|if
condition|(
operator|!
name|shardPerAttribute
operator|.
name|contains
argument_list|(
name|fullValue
argument_list|)
condition|)
block|{
name|numberOfAttributes
operator|++
expr_stmt|;
block|}
block|}
block|}
comment|// TODO should we remove ones that are not part of full list?
name|int
name|averagePerAttribute
init|=
name|shardCount
operator|/
name|numberOfAttributes
decl_stmt|;
name|int
name|totalLeftover
init|=
name|shardCount
operator|%
name|numberOfAttributes
decl_stmt|;
name|int
name|requiredCountPerAttribute
decl_stmt|;
if|if
condition|(
name|averagePerAttribute
operator|==
literal|0
condition|)
block|{
comment|// if we have more attributes values than shard count, no leftover
name|totalLeftover
operator|=
literal|0
expr_stmt|;
name|requiredCountPerAttribute
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|requiredCountPerAttribute
operator|=
name|averagePerAttribute
expr_stmt|;
block|}
name|int
name|leftoverPerAttribute
init|=
name|totalLeftover
operator|==
literal|0
condition|?
literal|0
else|:
literal|1
decl_stmt|;
name|int
name|currentNodeCount
init|=
name|shardPerAttribute
operator|.
name|get
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|awarenessAttribute
argument_list|)
argument_list|)
decl_stmt|;
comment|// if we are above with leftover, then we know we are not good, even with mod
if|if
condition|(
name|currentNodeCount
operator|>
operator|(
name|requiredCountPerAttribute
operator|+
name|leftoverPerAttribute
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// all is well, we are below or same as average
if|if
condition|(
name|currentNodeCount
operator|<=
name|requiredCountPerAttribute
condition|)
block|{
continue|continue;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

