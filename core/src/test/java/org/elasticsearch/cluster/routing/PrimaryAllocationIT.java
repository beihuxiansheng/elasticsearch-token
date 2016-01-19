begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|IntObjectCursor
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
name|admin
operator|.
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteRequestBuilder
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
name|admin
operator|.
name|indices
operator|.
name|shards
operator|.
name|IndicesShardStoresResponse
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
name|routing
operator|.
name|allocation
operator|.
name|command
operator|.
name|AllocateEmptyPrimaryAllocationCommand
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
name|command
operator|.
name|AllocateStalePrimaryAllocationCommand
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
name|ImmutableOpenIntMap
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
name|gateway
operator|.
name|GatewayAllocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|test
operator|.
name|ESIntegTestCase
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
name|InternalTestCluster
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
name|disruption
operator|.
name|NetworkDisconnectPartition
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
name|transport
operator|.
name|MockTransportService
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
name|Collection
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
name|List
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
name|ExecutionException
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
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
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
name|empty
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

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|)
annotation|@
name|ESIntegTestCase
operator|.
name|SuppressLocalMode
DECL|class|PrimaryAllocationIT
specifier|public
class|class
name|PrimaryAllocationIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
comment|// disruption tests need MockTransportService
return|return
name|pluginList
argument_list|(
name|MockTransportService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|createStaleReplicaScenario
specifier|private
name|void
name|createStaleReplicaScenario
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting 3 nodes, 1 master, 2 data"
argument_list|)
expr_stmt|;
name|String
name|master
init|=
name|internalCluster
argument_list|()
operator|.
name|startMasterOnlyNode
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startDataOnlyNodesAsync
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing..."
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|ClusterState
name|state
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|all
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|allShards
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shards
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|primaryNode
decl_stmt|;
specifier|final
name|String
name|replicaNode
decl_stmt|;
if|if
condition|(
name|shards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|primary
argument_list|()
condition|)
block|{
name|primaryNode
operator|=
name|state
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|shards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|node
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|replicaNode
operator|=
name|state
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|shards
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|node
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|primaryNode
operator|=
name|state
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|shards
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|node
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
name|replicaNode
operator|=
name|state
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|shards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|node
argument_list|()
operator|.
name|name
argument_list|()
expr_stmt|;
block|}
name|NetworkDisconnectPartition
name|partition
init|=
operator|new
name|NetworkDisconnectPartition
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|master
argument_list|,
name|replicaNode
argument_list|)
argument_list|)
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|primaryNode
argument_list|)
argument_list|,
name|random
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|partition
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> partitioning node with primary shard from rest of cluster"
argument_list|)
expr_stmt|;
name|partition
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|2
argument_list|,
name|master
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> index a document into previous replica shard (that is now primary)"
argument_list|)
expr_stmt|;
name|client
argument_list|(
name|replicaNode
argument_list|)
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> shut down node that has new acknowledged document"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|(
name|InternalTestCluster
operator|.
name|nameFilter
argument_list|(
name|replicaNode
argument_list|)
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|1
argument_list|,
name|master
argument_list|)
expr_stmt|;
name|partition
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for node with old primary shard to rejoin the cluster"
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|2
argument_list|,
name|master
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> check that old primary shard does not get promoted to primary again"
argument_list|)
expr_stmt|;
comment|// kick reroute and wait for all shard states to be fetched
name|client
argument_list|(
name|master
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareReroute
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertThat
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|GatewayAllocator
operator|.
name|class
argument_list|,
name|master
argument_list|)
operator|.
name|getNumberOfInFlightFetch
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// kick reroute a second time and check that all shards are unassigned
name|assertThat
argument_list|(
name|client
argument_list|(
name|master
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareReroute
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|unassigned
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDoNotAllowStaleReplicasToBePromotedToPrimary
specifier|public
name|void
name|testDoNotAllowStaleReplicasToBePromotedToPrimary
parameter_list|()
throws|throws
name|Exception
block|{
name|createStaleReplicaScenario
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting node that reuses data folder with the up-to-date primary shard"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startDataOnlyNode
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> check that the up-to-date primary shard gets promoted and that documents are available"
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailedAllocationOfStalePrimaryToDataNodeWithNoData
specifier|public
name|void
name|testFailedAllocationOfStalePrimaryToDataNodeWithNoData
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|dataNodeWithShardCopy
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> create single shard index"
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|String
name|dataNodeWithNoShardCopy
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|ensureStableCluster
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|(
name|InternalTestCluster
operator|.
name|nameFilter
argument_list|(
name|dataNodeWithShardCopy
argument_list|)
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|NODE_LEFT
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> force allocation of stale copy to node that does not have shard copy"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareReroute
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|AllocateStalePrimaryAllocationCommand
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|dataNodeWithNoShardCopy
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> wait until shard is failed and becomes unassigned again"
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertTrue
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|allPrimaryShardsUnassigned
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|ALLOCATION_FAILED
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testForceStaleReplicaToBePromotedToPrimary
specifier|public
name|void
name|testForceStaleReplicaToBePromotedToPrimary
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|useStaleReplica
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
comment|// if true, use stale replica, otherwise a completely empty copy
name|createStaleReplicaScenario
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> explicitly promote old primary shard"
argument_list|)
expr_stmt|;
name|ImmutableOpenIntMap
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|storeStatuses
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareShardStores
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|ClusterRerouteRequestBuilder
name|rerouteBuilder
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareReroute
argument_list|()
decl_stmt|;
for|for
control|(
name|IntObjectCursor
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStoreStatuses
range|:
name|storeStatuses
control|)
block|{
name|int
name|shardId
init|=
name|shardStoreStatuses
operator|.
name|key
decl_stmt|;
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|storeStatus
init|=
name|randomFrom
argument_list|(
name|shardStoreStatuses
operator|.
name|value
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> adding allocation command for shard "
operator|+
name|shardId
argument_list|)
expr_stmt|;
comment|// force allocation based on node id
if|if
condition|(
name|useStaleReplica
condition|)
block|{
name|rerouteBuilder
operator|.
name|add
argument_list|(
operator|new
name|AllocateStalePrimaryAllocationCommand
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
name|shardId
argument_list|)
argument_list|,
name|storeStatus
operator|.
name|getNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rerouteBuilder
operator|.
name|add
argument_list|(
operator|new
name|AllocateEmptyPrimaryAllocationCommand
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
name|shardId
argument_list|)
argument_list|,
name|storeStatus
operator|.
name|getNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|rerouteBuilder
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> check that the stale primary shard gets allocated and that documents are available"
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|useStaleReplica
condition|?
literal|1l
else|:
literal|0l
argument_list|)
expr_stmt|;
block|}
DECL|method|testForcePrimaryShardIfAllocationDecidersSayNoAfterIndexCreation
specifier|public
name|void
name|testForcePrimaryShardIfAllocationDecidersSayNoAfterIndexCreation
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|String
name|node
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.exclude._name"
argument_list|,
name|node
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|shardRoutingTable
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|assignedShards
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareReroute
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|AllocateEmptyPrimaryAllocationCommand
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|node
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNotWaitForQuorumCopies
specifier|public
name|void
name|testNotWaitForQuorumCopies
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting 3 nodes"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating index with 1 primary and 2 replicas"
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> removing 2 nodes from cluster"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|fullRestart
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> checking that index still gets allocated with only 1 shard copy being available"
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setSize
argument_list|(
literal|0
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

