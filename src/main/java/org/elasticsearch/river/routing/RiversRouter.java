begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|routing
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
name|Lists
import|;
end_import

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
name|Maps
import|;
end_import

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
name|action
operator|.
name|NoShardAvailableActionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
operator|.
name|GetResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|ClusterService
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
name|ClusterStateListener
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
name|block
operator|.
name|ClusterBlockException
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
name|MappingMetaData
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
name|common
operator|.
name|component
operator|.
name|AbstractLifecycleComponent
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
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
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
name|IndexMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|RiverIndexName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|RiverName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|cluster
operator|.
name|RiverClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|cluster
operator|.
name|RiverClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|cluster
operator|.
name|RiverClusterStateUpdateTask
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|river
operator|.
name|cluster
operator|.
name|RiverNodeHelper
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RiversRouter
specifier|public
class|class
name|RiversRouter
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|RiversRouter
argument_list|>
implements|implements
name|ClusterStateListener
block|{
DECL|field|riverIndexName
specifier|private
specifier|final
name|String
name|riverIndexName
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|riverClusterService
specifier|private
specifier|final
name|RiverClusterService
name|riverClusterService
decl_stmt|;
annotation|@
name|Inject
DECL|method|RiversRouter
specifier|public
name|RiversRouter
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|RiverClusterService
name|riverClusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|riverIndexName
operator|=
name|RiverIndexName
operator|.
name|Conf
operator|.
name|indexName
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|riverClusterService
operator|=
name|riverClusterService
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|clusterService
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
annotation|@
name|Override
DECL|method|clusterChanged
specifier|public
name|void
name|clusterChanged
parameter_list|(
specifier|final
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
operator|!
name|event
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
return|return;
block|}
name|riverClusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"reroute_rivers_node_changed"
argument_list|,
operator|new
name|RiverClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RiverClusterState
name|execute
parameter_list|(
name|RiverClusterState
name|currentState
parameter_list|)
block|{
if|if
condition|(
operator|!
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|riverIndexName
argument_list|)
condition|)
block|{
comment|// if there are routings, publish an empty one (so it will be deleted on nodes), otherwise, return the same state
if|if
condition|(
operator|!
name|currentState
operator|.
name|routing
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|RiverClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routing
argument_list|(
name|RiversRouting
operator|.
name|builder
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|currentState
return|;
block|}
name|RiversRouting
operator|.
name|Builder
name|routingBuilder
init|=
name|RiversRouting
operator|.
name|builder
argument_list|()
operator|.
name|routing
argument_list|(
name|currentState
operator|.
name|routing
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|riverIndexName
argument_list|)
decl_stmt|;
comment|// go over and create new river routing (with no node) for new types (rivers names)
for|for
control|(
name|MappingMetaData
name|mappingMd
range|:
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|String
name|mappingType
init|=
name|mappingMd
operator|.
name|type
argument_list|()
decl_stmt|;
comment|// mapping type is the name of the river
if|if
condition|(
operator|!
name|currentState
operator|.
name|routing
argument_list|()
operator|.
name|hasRiverByName
argument_list|(
name|mappingType
argument_list|)
condition|)
block|{
comment|// no river, we need to add it to the routing with no node allocation
try|try
block|{
name|GetResponse
name|getResponse
init|=
name|client
operator|.
name|prepareGet
argument_list|(
name|riverIndexName
argument_list|,
name|mappingType
argument_list|,
literal|"_meta"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|getResponse
operator|.
name|exists
argument_list|()
condition|)
block|{
name|String
name|riverType
init|=
name|XContentMapValues
operator|.
name|nodeStringValue
argument_list|(
name|getResponse
operator|.
name|sourceAsMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|riverType
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"no river type provided for [{}], ignoring..."
argument_list|,
name|riverIndexName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|routingBuilder
operator|.
name|put
argument_list|(
operator|new
name|RiverRouting
argument_list|(
operator|new
name|RiverName
argument_list|(
name|riverType
argument_list|,
name|mappingType
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|NoShardAvailableActionException
name|e
parameter_list|)
block|{
comment|// ignore, we will get it next time...
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// ignore, we will get it next time
block|}
catch|catch
parameter_list|(
name|IndexMissingException
name|e
parameter_list|)
block|{
comment|// ignore, we will get it next time
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to get/parse _meta for [{}]"
argument_list|,
name|e
argument_list|,
name|mappingType
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// now, remove routings that were deleted
comment|// also, apply nodes that were removed and rivers were running on
for|for
control|(
name|RiverRouting
name|routing
range|:
name|currentState
operator|.
name|routing
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|containsKey
argument_list|(
name|routing
operator|.
name|riverName
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|routingBuilder
operator|.
name|remove
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|routing
operator|.
name|node
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|nodeExists
argument_list|(
name|routing
operator|.
name|node
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|routingBuilder
operator|.
name|remove
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|routingBuilder
operator|.
name|put
argument_list|(
operator|new
name|RiverRouting
argument_list|(
name|routing
operator|.
name|riverName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
block|}
comment|// build a list from nodes to rivers
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|List
argument_list|<
name|RiverRouting
argument_list|>
argument_list|>
name|nodesToRivers
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
control|)
block|{
if|if
condition|(
name|RiverNodeHelper
operator|.
name|isRiverNode
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|nodesToRivers
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|Lists
operator|.
expr|<
name|RiverRouting
operator|>
name|newArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|List
argument_list|<
name|RiverRouting
argument_list|>
name|unassigned
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RiverRouting
name|routing
range|:
name|routingBuilder
operator|.
name|build
argument_list|()
control|)
block|{
if|if
condition|(
name|routing
operator|.
name|node
argument_list|()
operator|==
literal|null
condition|)
block|{
name|unassigned
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|RiverRouting
argument_list|>
name|l
init|=
name|nodesToRivers
operator|.
name|get
argument_list|(
name|routing
operator|.
name|node
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|l
operator|==
literal|null
condition|)
block|{
name|l
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|nodesToRivers
operator|.
name|put
argument_list|(
name|routing
operator|.
name|node
argument_list|()
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|l
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
argument_list|<
name|RiverRouting
argument_list|>
name|it
init|=
name|unassigned
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
name|RiverRouting
name|routing
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|smallest
init|=
literal|null
decl_stmt|;
name|int
name|smallestSize
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DiscoveryNode
argument_list|,
name|List
argument_list|<
name|RiverRouting
argument_list|>
argument_list|>
name|entry
range|:
name|nodesToRivers
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|RiverNodeHelper
operator|.
name|isRiverNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|routing
operator|.
name|riverName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|smallestSize
condition|)
block|{
name|smallestSize
operator|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|smallest
operator|=
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|smallest
operator|!=
literal|null
condition|)
block|{
name|dirty
operator|=
literal|true
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|routing
operator|.
name|node
argument_list|(
name|smallest
argument_list|)
expr_stmt|;
name|nodesToRivers
operator|.
name|get
argument_list|(
name|smallest
argument_list|)
operator|.
name|add
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add relocation logic...
if|if
condition|(
name|dirty
condition|)
block|{
return|return
name|RiverClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routing
argument_list|(
name|routingBuilder
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
return|return
name|currentState
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

