begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
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
name|RoutingTable
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|ElasticsearchTestCase
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
operator|.
name|newClusterStateBuilder
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
name|newIndexMetaDataBuilder
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
name|MetaData
operator|.
name|newMetaDataBuilder
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
name|node
operator|.
name|DiscoveryNodes
operator|.
name|newNodesBuilder
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
name|RoutingBuilders
operator|.
name|routingTable
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
name|allocation
operator|.
name|RoutingAllocationTests
operator|.
name|newNode
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
name|ImmutableSettings
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
name|MatcherAssert
operator|.
name|assertThat
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
comment|/**  *  */
end_comment

begin_class
DECL|class|ThrottlingAllocationTests
specifier|public
class|class
name|ThrottlingAllocationTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|ThrottlingAllocationTests
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testPrimaryRecoveryThrottling
specifier|public
name|void
name|testPrimaryRecoveryThrottling
parameter_list|()
block|{
name|AllocationService
name|strategy
init|=
operator|new
name|AllocationService
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_concurrent_recoveries"
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_initial_primaries_recoveries"
argument_list|,
literal|3
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Building initial routing table"
argument_list|)
expr_stmt|;
name|MetaData
name|metaData
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|10
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|routingTable
argument_list|()
operator|.
name|addAsNew
argument_list|(
name|metaData
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
name|ClusterState
name|clusterState
init|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start one node, do reroute, only 3 should initialize"
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|newNodesBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|17
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing, another 3 should initialize"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|14
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing, another 3 should initialize"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|11
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing, another 1 should initialize"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing, all primaries should be started"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplicaAndPrimaryRecoveryThrottling
specifier|public
name|void
name|testReplicaAndPrimaryRecoveryThrottling
parameter_list|()
block|{
name|AllocationService
name|strategy
init|=
operator|new
name|AllocationService
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.concurrent_recoveries"
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_initial_primaries_recoveries"
argument_list|,
literal|3
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Building initial routing table"
argument_list|)
expr_stmt|;
name|MetaData
name|metaData
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|5
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|routingTable
argument_list|()
operator|.
name|addAsNew
argument_list|(
name|metaData
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
name|ClusterState
name|clusterState
init|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start one node, do reroute, only 3 should initialize"
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|newNodesBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing, another 2 should initialize"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
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
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing, all primaries should be started"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start another node, replicas should start being allocated"
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|newNodesBuilder
argument_list|()
operator|.
name|putAll
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
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
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing replicas"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
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
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start initializing replicas, all should be started"
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingTable
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
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|STARTED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|shardsWithState
argument_list|(
name|UNASSIGNED
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

