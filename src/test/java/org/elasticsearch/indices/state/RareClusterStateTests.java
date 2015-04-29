begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.state
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|state
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
name|ImmutableMap
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
name|ActionListener
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
name|mapping
operator|.
name|put
operator|.
name|PutMappingResponse
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
name|index
operator|.
name|IndexResponse
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
name|ClusterInfo
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
name|DiskUsage
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
name|AllocationDeciders
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
name|ImmutableSettings
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
name|discovery
operator|.
name|DiscoveryModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|DiscoverySettings
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
name|index
operator|.
name|IndexService
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
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|MapperService
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
name|IndicesService
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
name|ElasticsearchIntegrationTest
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
name|BlockClusterStateProcessing
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
name|atomic
operator|.
name|AtomicReference
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
name|equalTo
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
name|hasItem
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
name|hasSize
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
name|instanceOf
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0
argument_list|)
DECL|class|RareClusterStateTests
specifier|public
class|class
name|RareClusterStateTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Override
DECL|method|numberOfShards
specifier|protected
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|numberOfReplicas
specifier|protected
name|int
name|numberOfReplicas
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Test
DECL|method|testUnassignedShardAndEmptyNodesInRoutingTable
specifier|public
name|void
name|testUnassignedShardAndEmptyNodesInRoutingTable
parameter_list|()
throws|throws
name|Exception
block|{
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
name|createIndex
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|ClusterState
name|current
init|=
name|clusterService
argument_list|()
operator|.
name|state
argument_list|()
decl_stmt|;
name|GatewayAllocator
name|allocator
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|GatewayAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|AllocationDeciders
name|allocationDeciders
init|=
operator|new
name|AllocationDeciders
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|,
operator|new
name|AllocationDecider
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|RoutingNodes
name|routingNodes
init|=
operator|new
name|RoutingNodes
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
name|current
argument_list|)
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
argument_list|(
name|current
operator|.
name|routingTable
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
literal|"a"
argument_list|)
operator|.
name|addAsRecovery
argument_list|(
name|current
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"a"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|EMPTY_NODES
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterInfo
name|clusterInfo
init|=
operator|new
name|ClusterInfo
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|DiskUsage
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Long
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|RoutingAllocation
name|routingAllocation
init|=
operator|new
name|RoutingAllocation
argument_list|(
name|allocationDeciders
argument_list|,
name|routingNodes
argument_list|,
name|current
operator|.
name|nodes
argument_list|()
argument_list|,
name|clusterInfo
argument_list|)
decl_stmt|;
name|allocator
operator|.
name|allocateUnassigned
argument_list|(
name|routingAllocation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestLogging
argument_list|(
name|value
operator|=
literal|"cluster.service:TRACE"
argument_list|)
DECL|method|testDeleteCreateInOneBulk
specifier|public
name|void
name|testDeleteCreateInOneBulk
parameter_list|()
throws|throws
name|Exception
block|{
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_KEY
argument_list|,
literal|"zen"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertFalse
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isTimedOut
argument_list|()
argument_list|)
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|,
literal|true
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
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
comment|// now that the cluster is stable, remove publishing timeout
name|assertAcked
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
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|DiscoverySettings
operator|.
name|PUBLISH_TIMEOUT
argument_list|,
literal|"0"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|remove
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
argument_list|)
expr_stmt|;
comment|// block none master node.
name|BlockClusterStateProcessing
name|disruption
init|=
operator|new
name|BlockClusterStateProcessing
argument_list|(
name|nodes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
name|getRandom
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|disruption
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing a doc"
argument_list|)
expr_stmt|;
name|index
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|disruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> delete index and recreate it"
argument_list|)
expr_stmt|;
name|assertFalse
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
name|prepareDelete
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"200ms"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setTimeout
argument_list|(
literal|"200ms"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|,
literal|true
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> letting cluster proceed"
argument_list|)
expr_stmt|;
name|disruption
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|30
argument_list|)
argument_list|,
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
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testDelayedMappingPropagationOnReplica
specifier|public
name|void
name|testDelayedMappingPropagationOnReplica
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Here we want to test that everything goes well if the mappings that
comment|// are needed for a document are not available on the replica at the
comment|// time of indexing it
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nodeNames
init|=
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertFalse
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
name|prepareHealth
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isTimedOut
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|master
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|nodeNames
argument_list|,
name|hasItem
argument_list|(
name|master
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|otherNode
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodeNames
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
name|master
argument_list|)
operator|==
literal|false
condition|)
block|{
name|otherNode
operator|=
name|node
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
name|otherNode
argument_list|)
expr_stmt|;
comment|// Force allocation of the primary on the master node by first only allocating on the master
comment|// and then allowing all nodes so that the replica gets allocated on the other node
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.include._name"
argument_list|,
name|master
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
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
name|prepareUpdateSettings
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.include._name"
argument_list|,
literal|""
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
comment|// Check routing tables
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
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|master
argument_list|,
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"index"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shards
argument_list|,
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|shards
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
condition|)
block|{
comment|// primary must be on the master
name|assertEquals
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|shard
operator|.
name|active
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Block cluster state processing on the replica
name|BlockClusterStateProcessing
name|disruption
init|=
operator|new
name|BlockClusterStateProcessing
argument_list|(
name|otherNode
argument_list|,
name|getRandom
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|disruption
argument_list|)
expr_stmt|;
name|disruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Object
argument_list|>
name|putMappingResponse
init|=
operator|new
name|AtomicReference
argument_list|<>
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
name|preparePutMapping
argument_list|(
literal|"index"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"type=long"
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|ActionListener
argument_list|<
name|PutMappingResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|PutMappingResponse
name|response
parameter_list|)
block|{
name|putMappingResponse
operator|.
name|set
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|putMappingResponse
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Wait for mappings to be available on master
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
specifier|final
name|IndicesService
name|indicesService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|master
argument_list|)
decl_stmt|;
specifier|final
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|indexService
argument_list|)
expr_stmt|;
specifier|final
name|MapperService
name|mapperService
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|mapper
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|mapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Object
argument_list|>
name|docIndexResponse
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|42
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|IndexResponse
name|response
parameter_list|)
block|{
name|docIndexResponse
operator|.
name|set
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|docIndexResponse
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Wait for document to be indexed on primary
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setPreference
argument_list|(
literal|"_primary"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|isExists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// The mappings have not been propagated to the replica yet as a consequence the document count not be indexed
comment|// We wait on purpose to make sure that the document is not indexed because the shard operation is stalled
comment|// and not just because it takes time to replicate the indexing request to the replica
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|putMappingResponse
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docIndexResponse
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now make sure the indexing request finishes successfully
name|disruption
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|assertThat
argument_list|(
name|putMappingResponse
operator|.
name|get
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|PutMappingResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|PutMappingResponse
name|resp
init|=
operator|(
name|PutMappingResponse
operator|)
name|putMappingResponse
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|resp
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docIndexResponse
operator|.
name|get
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|IndexResponse
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IndexResponse
name|docResp
init|=
operator|(
name|IndexResponse
operator|)
name|docIndexResponse
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|docResp
operator|.
name|getShardInfo
argument_list|()
operator|.
name|getFailures
argument_list|()
argument_list|)
argument_list|,
literal|2
argument_list|,
name|docResp
operator|.
name|getShardInfo
argument_list|()
operator|.
name|getTotal
argument_list|()
argument_list|)
expr_stmt|;
comment|// both shards should have succeeded
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

