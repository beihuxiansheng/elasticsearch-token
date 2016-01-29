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
name|allocation
operator|.
name|AllocationService
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
name|ClusterRebalanceAllocationDecider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESAllocationTestCase
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
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
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
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Test IndexMetaState for master and data only nodes return correct list of indices to write  * There are many parameters:  * - meta state is not in memory  * - meta state is in memory with old version/ new version  * - meta state is in memory with new version  * - version changed in cluster state event/ no change  * - node is data only node  * - node is master eligible  * for data only nodes: shard initializing on shard  */
end_comment

begin_class
DECL|class|GatewayMetaStateTests
specifier|public
class|class
name|GatewayMetaStateTests
extends|extends
name|ESAllocationTestCase
block|{
DECL|method|generateEvent
name|ClusterChangedEvent
name|generateEvent
parameter_list|(
name|boolean
name|initializing
parameter_list|,
name|boolean
name|versionChanged
parameter_list|,
name|boolean
name|masterEligible
parameter_list|)
block|{
comment|//ridiculous settings to make sure we don't run into uninitialized because fo default
name|AllocationService
name|strategy
init|=
name|createAllocationService
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_concurrent_recoveries"
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
name|ClusterRebalanceAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ALLOW_REBALANCE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"always"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.cluster_concurrent_rebalance"
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_initial_primaries_recoveries"
argument_list|,
literal|100
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterState
name|newClusterState
decl_stmt|,
name|previousClusterState
decl_stmt|;
name|MetaData
name|metaDataOldClusterState
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|5
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTableOldClusterState
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|addAsNew
argument_list|(
name|metaDataOldClusterState
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// assign all shards
name|ClusterState
name|init
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataOldClusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableOldClusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|generateDiscoveryNodes
argument_list|(
name|masterEligible
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// new cluster state will have initializing shards on node 1
name|RoutingTable
name|routingTableNewClusterState
init|=
name|strategy
operator|.
name|reroute
argument_list|(
name|init
argument_list|,
literal|"reroute"
argument_list|)
operator|.
name|routingTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|initializing
operator|==
literal|false
condition|)
block|{
comment|// pretend all initialized, nothing happened
name|ClusterState
name|temp
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|init
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableNewClusterState
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataOldClusterState
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|routingTableNewClusterState
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|temp
argument_list|,
name|temp
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|routingTableOldClusterState
operator|=
name|routingTableNewClusterState
expr_stmt|;
block|}
else|else
block|{
comment|// nothing to do, we have one routing table with unassigned and one with initializing
block|}
comment|// create new meta data either with version changed or not
name|MetaData
name|metaDataNewClusterState
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|init
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|versionChanged
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// create the cluster states with meta data and routing tables as computed before
name|previousClusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|init
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataOldClusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableOldClusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|generateDiscoveryNodes
argument_list|(
name|masterEligible
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|newClusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|previousClusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableNewClusterState
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataNewClusterState
argument_list|)
operator|.
name|version
argument_list|(
name|previousClusterState
operator|.
name|getVersion
argument_list|()
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterChangedEvent
name|event
init|=
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|newClusterState
argument_list|,
name|previousClusterState
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|event
operator|.
name|previousState
argument_list|()
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|event
return|;
block|}
DECL|method|generateCloseEvent
name|ClusterChangedEvent
name|generateCloseEvent
parameter_list|(
name|boolean
name|masterEligible
parameter_list|)
block|{
comment|//ridiculous settings to make sure we don't run into uninitialized because fo default
name|AllocationService
name|strategy
init|=
name|createAllocationService
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_concurrent_recoveries"
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
name|ClusterRebalanceAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ALLOW_REBALANCE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"always"
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.cluster_concurrent_rebalance"
argument_list|,
literal|100
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_initial_primaries_recoveries"
argument_list|,
literal|100
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterState
name|newClusterState
decl_stmt|,
name|previousClusterState
decl_stmt|;
name|MetaData
name|metaDataIndexCreated
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|5
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTableIndexCreated
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|addAsNew
argument_list|(
name|metaDataIndexCreated
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// assign all shards
name|ClusterState
name|init
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataIndexCreated
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableIndexCreated
argument_list|)
operator|.
name|nodes
argument_list|(
name|generateDiscoveryNodes
argument_list|(
name|masterEligible
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTableInitializing
init|=
name|strategy
operator|.
name|reroute
argument_list|(
name|init
argument_list|,
literal|"reroute"
argument_list|)
operator|.
name|routingTable
argument_list|()
decl_stmt|;
name|ClusterState
name|temp
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|init
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableInitializing
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTableStarted
init|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|temp
argument_list|,
name|temp
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
operator|.
name|routingTable
argument_list|()
decl_stmt|;
comment|// create new meta data either with version changed or not
name|MetaData
name|metaDataStarted
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|init
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// create the cluster states with meta data and routing tables as computed before
name|MetaData
name|metaDataClosed
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|state
argument_list|(
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|5
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
argument_list|)
operator|.
name|version
argument_list|(
name|metaDataStarted
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|previousClusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|init
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataStarted
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableStarted
argument_list|)
operator|.
name|nodes
argument_list|(
name|generateDiscoveryNodes
argument_list|(
name|masterEligible
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|newClusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|previousClusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableIndexCreated
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataClosed
argument_list|)
operator|.
name|version
argument_list|(
name|previousClusterState
operator|.
name|getVersion
argument_list|()
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterChangedEvent
name|event
init|=
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|newClusterState
argument_list|,
name|previousClusterState
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|event
operator|.
name|previousState
argument_list|()
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|event
return|;
block|}
DECL|method|generateDiscoveryNodes
specifier|private
name|DiscoveryNodes
operator|.
name|Builder
name|generateDiscoveryNodes
parameter_list|(
name|boolean
name|masterEligible
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|masterNodeAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|masterNodeAttributes
operator|.
name|put
argument_list|(
literal|"master"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|masterNodeAttributes
operator|.
name|put
argument_list|(
literal|"data"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dataNodeAttributes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|dataNodeAttributes
operator|.
name|put
argument_list|(
literal|"master"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|dataNodeAttributes
operator|.
name|put
argument_list|(
literal|"data"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
return|return
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node1"
argument_list|,
name|masterEligible
condition|?
name|masterNodeAttributes
else|:
name|dataNodeAttributes
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"master_node"
argument_list|,
name|masterNodeAttributes
argument_list|)
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|masterNodeId
argument_list|(
name|masterEligible
condition|?
literal|"node1"
else|:
literal|"master_node"
argument_list|)
return|;
block|}
DECL|method|assertState
specifier|public
name|void
name|assertState
parameter_list|(
name|ClusterChangedEvent
name|event
parameter_list|,
name|boolean
name|stateInMemory
parameter_list|,
name|boolean
name|expectMetaData
parameter_list|)
throws|throws
name|Exception
block|{
name|MetaData
name|inMemoryMetaData
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|oldIndicesList
init|=
name|emptySet
argument_list|()
decl_stmt|;
if|if
condition|(
name|stateInMemory
condition|)
block|{
name|inMemoryMetaData
operator|=
name|event
operator|.
name|previousState
argument_list|()
operator|.
name|metaData
argument_list|()
expr_stmt|;
name|oldIndicesList
operator|=
name|GatewayMetaState
operator|.
name|getRelevantIndices
argument_list|(
name|event
operator|.
name|previousState
argument_list|()
argument_list|,
name|event
operator|.
name|previousState
argument_list|()
argument_list|,
name|oldIndicesList
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|newIndicesList
init|=
name|GatewayMetaState
operator|.
name|getRelevantIndices
argument_list|(
name|event
operator|.
name|state
argument_list|()
argument_list|,
name|event
operator|.
name|previousState
argument_list|()
argument_list|,
name|oldIndicesList
argument_list|)
decl_stmt|;
comment|// third, get the actual write info
name|Iterator
argument_list|<
name|GatewayMetaState
operator|.
name|IndexMetaWriteInfo
argument_list|>
name|indices
init|=
name|GatewayMetaState
operator|.
name|resolveStatesToBeWritten
argument_list|(
name|oldIndicesList
argument_list|,
name|newIndicesList
argument_list|,
name|inMemoryMetaData
argument_list|,
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|expectMetaData
condition|)
block|{
name|assertThat
argument_list|(
name|indices
operator|.
name|hasNext
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indices
operator|.
name|next
argument_list|()
operator|.
name|getNewMetaData
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indices
operator|.
name|hasNext
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|indices
operator|.
name|hasNext
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testVersionChangeIsAlwaysWritten
specifier|public
name|void
name|testVersionChangeIsAlwaysWritten
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test that version changes are always written
name|boolean
name|initializing
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|versionChanged
init|=
literal|true
decl_stmt|;
name|boolean
name|stateInMemory
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|masterEligible
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|expectMetaData
init|=
literal|true
decl_stmt|;
name|ClusterChangedEvent
name|event
init|=
name|generateEvent
argument_list|(
name|initializing
argument_list|,
name|versionChanged
argument_list|,
name|masterEligible
argument_list|)
decl_stmt|;
name|assertState
argument_list|(
name|event
argument_list|,
name|stateInMemory
argument_list|,
name|expectMetaData
argument_list|)
expr_stmt|;
block|}
DECL|method|testNewShardsAlwaysWritten
specifier|public
name|void
name|testNewShardsAlwaysWritten
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure new shards on data only node always written
name|boolean
name|initializing
init|=
literal|true
decl_stmt|;
name|boolean
name|versionChanged
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|stateInMemory
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|masterEligible
init|=
literal|false
decl_stmt|;
name|boolean
name|expectMetaData
init|=
literal|true
decl_stmt|;
name|ClusterChangedEvent
name|event
init|=
name|generateEvent
argument_list|(
name|initializing
argument_list|,
name|versionChanged
argument_list|,
name|masterEligible
argument_list|)
decl_stmt|;
name|assertState
argument_list|(
name|event
argument_list|,
name|stateInMemory
argument_list|,
name|expectMetaData
argument_list|)
expr_stmt|;
block|}
DECL|method|testAllUpToDateNothingWritten
specifier|public
name|void
name|testAllUpToDateNothingWritten
parameter_list|()
throws|throws
name|Exception
block|{
comment|// make sure state is not written again if we wrote already
name|boolean
name|initializing
init|=
literal|false
decl_stmt|;
name|boolean
name|versionChanged
init|=
literal|false
decl_stmt|;
name|boolean
name|stateInMemory
init|=
literal|true
decl_stmt|;
name|boolean
name|masterEligible
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|expectMetaData
init|=
literal|false
decl_stmt|;
name|ClusterChangedEvent
name|event
init|=
name|generateEvent
argument_list|(
name|initializing
argument_list|,
name|versionChanged
argument_list|,
name|masterEligible
argument_list|)
decl_stmt|;
name|assertState
argument_list|(
name|event
argument_list|,
name|stateInMemory
argument_list|,
name|expectMetaData
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoWriteIfNothingChanged
specifier|public
name|void
name|testNoWriteIfNothingChanged
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|initializing
init|=
literal|false
decl_stmt|;
name|boolean
name|versionChanged
init|=
literal|false
decl_stmt|;
name|boolean
name|stateInMemory
init|=
literal|true
decl_stmt|;
name|boolean
name|masterEligible
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|expectMetaData
init|=
literal|false
decl_stmt|;
name|ClusterChangedEvent
name|event
init|=
name|generateEvent
argument_list|(
name|initializing
argument_list|,
name|versionChanged
argument_list|,
name|masterEligible
argument_list|)
decl_stmt|;
name|ClusterChangedEvent
name|newEventWithNothingChanged
init|=
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test cluster state"
argument_list|,
name|event
operator|.
name|state
argument_list|()
argument_list|,
name|event
operator|.
name|state
argument_list|()
argument_list|)
decl_stmt|;
name|assertState
argument_list|(
name|newEventWithNothingChanged
argument_list|,
name|stateInMemory
argument_list|,
name|expectMetaData
argument_list|)
expr_stmt|;
block|}
DECL|method|testWriteClosedIndex
specifier|public
name|void
name|testWriteClosedIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// test that the closing of an index is written also on data only node
name|boolean
name|masterEligible
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|boolean
name|expectMetaData
init|=
literal|true
decl_stmt|;
name|boolean
name|stateInMemory
init|=
literal|true
decl_stmt|;
name|ClusterChangedEvent
name|event
init|=
name|generateCloseEvent
argument_list|(
name|masterEligible
argument_list|)
decl_stmt|;
name|assertState
argument_list|(
name|event
argument_list|,
name|stateInMemory
argument_list|,
name|expectMetaData
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

