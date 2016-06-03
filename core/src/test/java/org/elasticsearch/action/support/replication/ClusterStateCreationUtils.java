begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|ClusterName
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
name|ClusterState
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
name|IndexRoutingTable
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
name|IndexShardRoutingTable
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
name|RoutingTable
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
name|ShardRoutingState
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
name|TestShardRouting
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
name|transport
operator|.
name|DummyTransportAddress
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
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
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
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
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
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
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
operator|.
name|randomFrom
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
operator|.
name|randomInt
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
operator|.
name|randomIntBetween
import|;
end_import

begin_comment
comment|/**  * Helper methods for generating cluster states  */
end_comment

begin_class
DECL|class|ClusterStateCreationUtils
specifier|public
class|class
name|ClusterStateCreationUtils
block|{
comment|/**      * Creates cluster state with and index that has one shard and #(replicaStates) replicas      *      * @param index              name of the index      * @param activePrimaryLocal if active primary should coincide with the local node in the cluster state      * @param primaryState       state of primary      * @param replicaStates      states of the replicas. length of this array determines also the number of replicas      */
DECL|method|state
specifier|public
specifier|static
name|ClusterState
name|state
parameter_list|(
name|String
name|index
parameter_list|,
name|boolean
name|activePrimaryLocal
parameter_list|,
name|ShardRoutingState
name|primaryState
parameter_list|,
name|ShardRoutingState
modifier|...
name|replicaStates
parameter_list|)
block|{
specifier|final
name|int
name|numberOfReplicas
init|=
name|replicaStates
operator|.
name|length
decl_stmt|;
name|int
name|numberOfNodes
init|=
name|numberOfReplicas
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|primaryState
operator|==
name|ShardRoutingState
operator|.
name|RELOCATING
condition|)
block|{
name|numberOfNodes
operator|++
expr_stmt|;
block|}
for|for
control|(
name|ShardRoutingState
name|state
range|:
name|replicaStates
control|)
block|{
if|if
condition|(
name|state
operator|==
name|ShardRoutingState
operator|.
name|RELOCATING
condition|)
block|{
name|numberOfNodes
operator|++
expr_stmt|;
block|}
block|}
name|numberOfNodes
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|2
argument_list|,
name|numberOfNodes
argument_list|)
expr_stmt|;
comment|// we need a non-local master to test shard failures
specifier|final
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
literal|"_na_"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|discoBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|unassignedNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|numberOfNodes
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|DiscoveryNode
name|node
init|=
name|newNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|discoBuilder
operator|=
name|discoBuilder
operator|.
name|put
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|unassignedNodes
operator|.
name|add
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|discoBuilder
operator|.
name|localNodeId
argument_list|(
name|newNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|masterNodeId
argument_list|(
name|newNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// we need a non-local master to test shard failures
specifier|final
name|int
name|primaryTerm
init|=
literal|1
operator|+
name|randomInt
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|index
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|numberOfReplicas
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_CREATION_DATE
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
operator|.
name|primaryTerm
argument_list|(
literal|0
argument_list|,
name|primaryTerm
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routing
init|=
operator|new
name|RoutingTable
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|routing
operator|.
name|addAsNew
argument_list|(
name|indexMetaData
argument_list|)
expr_stmt|;
name|IndexShardRoutingTable
operator|.
name|Builder
name|indexShardRoutingBuilder
init|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|String
name|primaryNode
init|=
literal|null
decl_stmt|;
name|String
name|relocatingNode
init|=
literal|null
decl_stmt|;
name|UnassignedInfo
name|unassignedInfo
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|primaryState
operator|!=
name|ShardRoutingState
operator|.
name|UNASSIGNED
condition|)
block|{
if|if
condition|(
name|activePrimaryLocal
condition|)
block|{
name|primaryNode
operator|=
name|newNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
expr_stmt|;
name|unassignedNodes
operator|.
name|remove
argument_list|(
name|primaryNode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|unassignedNodesExecludingPrimary
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|unassignedNodes
argument_list|)
decl_stmt|;
name|unassignedNodesExecludingPrimary
operator|.
name|remove
argument_list|(
name|newNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|primaryNode
operator|=
name|selectAndRemove
argument_list|(
name|unassignedNodesExecludingPrimary
argument_list|)
expr_stmt|;
name|unassignedNodes
operator|.
name|remove
argument_list|(
name|primaryNode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|primaryState
operator|==
name|ShardRoutingState
operator|.
name|RELOCATING
condition|)
block|{
name|relocatingNode
operator|=
name|selectAndRemove
argument_list|(
name|unassignedNodes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|unassignedInfo
operator|=
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|indexShardRoutingBuilder
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|index
argument_list|,
literal|0
argument_list|,
name|primaryNode
argument_list|,
name|relocatingNode
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|primaryState
argument_list|,
name|unassignedInfo
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRoutingState
name|replicaState
range|:
name|replicaStates
control|)
block|{
name|String
name|replicaNode
init|=
literal|null
decl_stmt|;
name|relocatingNode
operator|=
literal|null
expr_stmt|;
name|unassignedInfo
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|replicaState
operator|!=
name|ShardRoutingState
operator|.
name|UNASSIGNED
condition|)
block|{
assert|assert
name|primaryNode
operator|!=
literal|null
operator|:
literal|"a replica is assigned but the primary isn't"
assert|;
name|replicaNode
operator|=
name|selectAndRemove
argument_list|(
name|unassignedNodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicaState
operator|==
name|ShardRoutingState
operator|.
name|RELOCATING
condition|)
block|{
name|relocatingNode
operator|=
name|selectAndRemove
argument_list|(
name|unassignedNodes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|unassignedInfo
operator|=
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|indexShardRoutingBuilder
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|,
name|replicaNode
argument_list|,
name|relocatingNode
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|replicaState
argument_list|,
name|unassignedInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ClusterState
operator|.
name|Builder
name|state
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|.
name|nodes
argument_list|(
name|discoBuilder
argument_list|)
expr_stmt|;
name|state
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|false
argument_list|)
operator|.
name|generateClusterUuidIfNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|IndexRoutingTable
operator|.
name|builder
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|addIndexShard
argument_list|(
name|indexShardRoutingBuilder
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|state
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Creates cluster state with several shards and one replica and all shards STARTED.      */
DECL|method|stateWithAssignedPrimariesAndOneReplica
specifier|public
specifier|static
name|ClusterState
name|stateWithAssignedPrimariesAndOneReplica
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|numberOfShards
parameter_list|)
block|{
name|int
name|numberOfNodes
init|=
literal|2
decl_stmt|;
comment|// we need a non-local master to test shard failures
name|DiscoveryNodes
operator|.
name|Builder
name|discoBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
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
name|numberOfNodes
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|DiscoveryNode
name|node
init|=
name|newNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|discoBuilder
operator|=
name|discoBuilder
operator|.
name|put
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|discoBuilder
operator|.
name|localNodeId
argument_list|(
name|newNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|masterNodeId
argument_list|(
name|newNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// we need a non-local master to test shard failures
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|index
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|numberOfShards
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_CREATION_DATE
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterState
operator|.
name|Builder
name|state
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|.
name|nodes
argument_list|(
name|discoBuilder
argument_list|)
expr_stmt|;
name|state
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|false
argument_list|)
operator|.
name|generateClusterUuidIfNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|IndexRoutingTable
operator|.
name|Builder
name|indexRoutingTableBuilder
init|=
name|IndexRoutingTable
operator|.
name|builder
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
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
name|numberOfShards
condition|;
name|i
operator|++
control|)
block|{
name|RoutingTable
operator|.
name|Builder
name|routing
init|=
operator|new
name|RoutingTable
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|routing
operator|.
name|addAsNew
argument_list|(
name|indexMetaData
argument_list|)
expr_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
literal|"_na_"
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|IndexShardRoutingTable
operator|.
name|Builder
name|indexShardRoutingBuilder
init|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|indexShardRoutingBuilder
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|index
argument_list|,
name|i
argument_list|,
name|newNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|indexShardRoutingBuilder
operator|.
name|addShard
argument_list|(
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|index
argument_list|,
name|i
argument_list|,
name|newNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|indexRoutingTableBuilder
operator|.
name|addIndexShard
argument_list|(
name|indexShardRoutingBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|state
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|indexRoutingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|state
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Creates cluster state with and index that has one shard and as many replicas as numberOfReplicas.      * Primary will be STARTED in cluster state but replicas will be one of UNASSIGNED, INITIALIZING, STARTED or RELOCATING.      *      * @param index              name of the index      * @param activePrimaryLocal if active primary should coincide with the local node in the cluster state      * @param numberOfReplicas   number of replicas      */
DECL|method|stateWithActivePrimary
specifier|public
specifier|static
name|ClusterState
name|stateWithActivePrimary
parameter_list|(
name|String
name|index
parameter_list|,
name|boolean
name|activePrimaryLocal
parameter_list|,
name|int
name|numberOfReplicas
parameter_list|)
block|{
name|int
name|assignedReplicas
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|numberOfReplicas
argument_list|)
decl_stmt|;
return|return
name|stateWithActivePrimary
argument_list|(
name|index
argument_list|,
name|activePrimaryLocal
argument_list|,
name|assignedReplicas
argument_list|,
name|numberOfReplicas
operator|-
name|assignedReplicas
argument_list|)
return|;
block|}
comment|/**      * Creates cluster state with and index that has one shard and as many replicas as numberOfReplicas.      * Primary will be STARTED in cluster state. Some (unassignedReplicas) will be UNASSIGNED and      * some (assignedReplicas) will be one of INITIALIZING, STARTED or RELOCATING.      *      * @param index              name of the index      * @param activePrimaryLocal if active primary should coincide with the local node in the cluster state      * @param assignedReplicas   number of replicas that should have INITIALIZING, STARTED or RELOCATING state      * @param unassignedReplicas number of replicas that should be unassigned      */
DECL|method|stateWithActivePrimary
specifier|public
specifier|static
name|ClusterState
name|stateWithActivePrimary
parameter_list|(
name|String
name|index
parameter_list|,
name|boolean
name|activePrimaryLocal
parameter_list|,
name|int
name|assignedReplicas
parameter_list|,
name|int
name|unassignedReplicas
parameter_list|)
block|{
name|ShardRoutingState
index|[]
name|replicaStates
init|=
operator|new
name|ShardRoutingState
index|[
name|assignedReplicas
operator|+
name|unassignedReplicas
index|]
decl_stmt|;
comment|// no point in randomizing - node assignment later on does it too.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|assignedReplicas
condition|;
name|i
operator|++
control|)
block|{
name|replicaStates
index|[
name|i
index|]
operator|=
name|randomFrom
argument_list|(
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
name|ShardRoutingState
operator|.
name|RELOCATING
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
name|assignedReplicas
init|;
name|i
operator|<
name|replicaStates
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|replicaStates
index|[
name|i
index|]
operator|=
name|ShardRoutingState
operator|.
name|UNASSIGNED
expr_stmt|;
block|}
return|return
name|state
argument_list|(
name|index
argument_list|,
name|activePrimaryLocal
argument_list|,
name|randomFrom
argument_list|(
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
name|ShardRoutingState
operator|.
name|RELOCATING
argument_list|)
argument_list|,
name|replicaStates
argument_list|)
return|;
block|}
comment|/**      * Creates a cluster state with no index      */
DECL|method|stateWithNoShard
specifier|public
specifier|static
name|ClusterState
name|stateWithNoShard
parameter_list|()
block|{
name|int
name|numberOfNodes
init|=
literal|2
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|discoBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|discoBuilder
operator|.
name|localNodeId
argument_list|(
name|newNode
argument_list|(
literal|0
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|discoBuilder
operator|.
name|masterNodeId
argument_list|(
name|newNode
argument_list|(
literal|1
argument_list|)
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
name|state
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|.
name|nodes
argument_list|(
name|discoBuilder
argument_list|)
expr_stmt|;
name|state
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|generateClusterUuidIfNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|state
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Creates a cluster state where local node and master node can be specified      *      * @param localNode  node in allNodes that is the local node      * @param masterNode node in allNodes that is the master node. Can be null if no master exists      * @param allNodes   all nodes in the cluster      * @return cluster state      */
DECL|method|state
specifier|public
specifier|static
name|ClusterState
name|state
parameter_list|(
name|DiscoveryNode
name|localNode
parameter_list|,
name|DiscoveryNode
name|masterNode
parameter_list|,
name|DiscoveryNode
modifier|...
name|allNodes
parameter_list|)
block|{
name|DiscoveryNodes
operator|.
name|Builder
name|discoBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|allNodes
control|)
block|{
name|discoBuilder
operator|.
name|put
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|masterNode
operator|!=
literal|null
condition|)
block|{
name|discoBuilder
operator|.
name|masterNodeId
argument_list|(
name|masterNode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|discoBuilder
operator|.
name|localNodeId
argument_list|(
name|localNode
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
name|state
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|state
operator|.
name|nodes
argument_list|(
name|discoBuilder
argument_list|)
expr_stmt|;
name|state
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|generateClusterUuidIfNeeded
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|state
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|newNode
specifier|private
specifier|static
name|DiscoveryNode
name|newNode
parameter_list|(
name|int
name|nodeId
parameter_list|)
block|{
return|return
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_"
operator|+
name|nodeId
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
return|;
block|}
DECL|method|selectAndRemove
specifier|static
specifier|private
name|String
name|selectAndRemove
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|)
block|{
name|String
name|selection
init|=
name|randomFrom
argument_list|(
name|strings
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|strings
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|strings
operator|.
name|remove
argument_list|(
name|selection
argument_list|)
expr_stmt|;
return|return
name|selection
return|;
block|}
block|}
end_class

end_unit

