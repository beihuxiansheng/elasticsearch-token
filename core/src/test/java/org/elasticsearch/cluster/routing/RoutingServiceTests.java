begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|test
operator|.
name|ESAllocationTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|RoutingServiceTests
specifier|public
class|class
name|RoutingServiceTests
extends|extends
name|ESAllocationTestCase
block|{
DECL|field|routingService
specifier|private
name|TestRoutingService
name|routingService
decl_stmt|;
annotation|@
name|Before
DECL|method|createRoutingService
specifier|public
name|void
name|createRoutingService
parameter_list|()
block|{
name|routingService
operator|=
operator|new
name|TestRoutingService
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|shutdownRoutingService
specifier|public
name|void
name|shutdownRoutingService
parameter_list|()
throws|throws
name|Exception
block|{
name|routingService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReroute
specifier|public
name|void
name|testReroute
parameter_list|()
block|{
name|assertThat
argument_list|(
name|routingService
operator|.
name|hasReroutedAndClear
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|routingService
operator|.
name|reroute
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingService
operator|.
name|hasReroutedAndClear
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoDelayedUnassigned
specifier|public
name|void
name|testNoDelayedUnassigned
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocationService
name|allocation
init|=
name|createAllocationService
argument_list|()
decl_stmt|;
name|MetaData
name|metaData
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
operator|.
name|put
argument_list|(
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
literal|"0"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
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
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
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
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node2"
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
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// starting primaries
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// starting replicas
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|hasUnassigned
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove node2 and reroute
name|ClusterState
name|prevState
init|=
name|clusterState
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterState
name|newState
init|=
name|clusterState
decl_stmt|;
name|assertThat
argument_list|(
name|routingService
operator|.
name|getRegisteredNextDelaySetting
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|routingService
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|newState
argument_list|,
name|prevState
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingService
operator|.
name|getRegisteredNextDelaySetting
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingService
operator|.
name|hasReroutedAndClear
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelayedUnassignedScheduleReroute
specifier|public
name|void
name|testDelayedUnassignedScheduleReroute
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocationService
name|allocation
init|=
name|createAllocationService
argument_list|()
decl_stmt|;
name|MetaData
name|metaData
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
operator|.
name|put
argument_list|(
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
literal|"100ms"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
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
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
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
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node2"
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
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// starting primaries
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// starting replicas
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|hasUnassigned
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove node2 and reroute
name|ClusterState
name|prevState
init|=
name|clusterState
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// We need to update the routing service's last attempted run to
comment|// signal that the GatewayAllocator tried to allocated it but
comment|// it was delayed
name|routingService
operator|.
name|setUnassignedShardsAllocatedTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|newState
init|=
name|clusterState
decl_stmt|;
name|routingService
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|newState
argument_list|,
name|prevState
argument_list|)
argument_list|)
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
name|routingService
operator|.
name|hasReroutedAndClear
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// verify the registration has been reset
name|assertThat
argument_list|(
name|routingService
operator|.
name|getRegisteredNextDelaySetting
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDelayedUnassignedDoesNotRerouteForNegativeDelays
specifier|public
name|void
name|testDelayedUnassignedDoesNotRerouteForNegativeDelays
parameter_list|()
throws|throws
name|Exception
block|{
name|AllocationService
name|allocation
init|=
name|createAllocationService
argument_list|()
decl_stmt|;
name|MetaData
name|metaData
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
operator|.
name|put
argument_list|(
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
literal|"100ms"
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
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
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
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
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
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
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node2"
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
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// starting primaries
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// starting replicas
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|routingNodes
argument_list|()
operator|.
name|hasUnassigned
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove node2 and reroute
name|ClusterState
name|prevState
init|=
name|clusterState
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|remove
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|allocation
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Set it in the future so the delay will be negative
name|routingService
operator|.
name|setUnassignedShardsAllocatedTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|1
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterState
name|newState
init|=
name|clusterState
decl_stmt|;
name|routingService
operator|.
name|clusterChanged
argument_list|(
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|newState
argument_list|,
name|prevState
argument_list|)
argument_list|)
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
name|routingService
operator|.
name|hasReroutedAndClear
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify the registration has been updated
name|assertThat
argument_list|(
name|routingService
operator|.
name|getRegisteredNextDelaySetting
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|TestRoutingService
specifier|private
class|class
name|TestRoutingService
extends|extends
name|RoutingService
block|{
DECL|field|rerouted
specifier|private
name|AtomicBoolean
name|rerouted
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|TestRoutingService
specifier|public
name|TestRoutingService
parameter_list|()
block|{
name|super
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|ThreadPool
argument_list|(
name|getTestName
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|hasReroutedAndClear
specifier|public
name|boolean
name|hasReroutedAndClear
parameter_list|()
block|{
return|return
name|rerouted
operator|.
name|getAndSet
argument_list|(
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|performReroute
specifier|protected
name|void
name|performReroute
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|rerouted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

