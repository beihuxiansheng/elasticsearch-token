begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Version
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
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|state
operator|.
name|ClusterStateResponse
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
name|ClusterInfoService
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
name|InternalClusterInfoService
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
name|MockInternalClusterInfoService
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
name|monitor
operator|.
name|fs
operator|.
name|FsInfo
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
name|ArrayList
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|newHashMap
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
name|*
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
DECL|class|MockDiskUsagesIT
specifier|public
class|class
name|MockDiskUsagesIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
comment|// Use the mock internal cluster info service, which has fake-able disk usages
operator|.
name|extendArray
argument_list|(
literal|"plugin.types"
argument_list|,
name|MockInternalClusterInfoService
operator|.
name|TestPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
comment|// Update more frequently
operator|.
name|put
argument_list|(
name|InternalClusterInfoService
operator|.
name|INTERNAL_CLUSTER_INFO_UPDATE_INTERVAL
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
comment|//@TestLogging("org.elasticsearch.cluster:TRACE,org.elasticsearch.cluster.routing.allocation.decider:TRACE")
DECL|method|testRerouteOccursOnDiskPassingHighWatermark
specifier|public
name|void
name|testRerouteOccursOnDiskPassingHighWatermark
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodes
init|=
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
decl_stmt|;
comment|// Wait for all 3 nodes to be up
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
name|NodesStatsResponse
name|resp
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
name|prepareNodesStats
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|resp
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Start with all nodes at 50% usage
specifier|final
name|MockInternalClusterInfoService
name|cis
init|=
operator|(
name|MockInternalClusterInfoService
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterInfoService
operator|.
name|class
argument_list|,
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
argument_list|)
decl_stmt|;
name|cis
operator|.
name|setN1Usage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"n1"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|cis
operator|.
name|setN2Usage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"n2"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|cis
operator|.
name|setN3Usage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"n3"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
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
name|prepareUpdateSettings
argument_list|()
operator|.
name|setTransientSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK
argument_list|,
name|randomFrom
argument_list|(
literal|"20b"
argument_list|,
literal|"80%"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_HIGH_DISK_WATERMARK
argument_list|,
name|randomFrom
argument_list|(
literal|"10b"
argument_list|,
literal|"90%"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_REROUTE_INTERVAL
argument_list|,
literal|"1ms"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Create an index with 10 shards so we can check allocation for it
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|10
argument_list|)
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.routing.allocation.exclude._name"
argument_list|,
literal|""
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
comment|// Block until the "fake" cluster info is retrieved at least once
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
name|ClusterInfo
name|info
init|=
name|cis
operator|.
name|getClusterInfo
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> got: {} nodes"
argument_list|,
name|info
operator|.
name|getNodeDiskUsages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeDiskUsages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|realNodeNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ClusterStateResponse
name|resp
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
decl_stmt|;
name|Iterator
argument_list|<
name|RoutingNode
argument_list|>
name|iter
init|=
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RoutingNode
name|node
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|realNodeNames
operator|.
name|add
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> node {} has {} shards"
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|,
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|numberOfOwningShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Update the disk usages so one node has now passed the high watermark
name|cis
operator|.
name|setN1Usage
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"n1"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|cis
operator|.
name|setN2Usage
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"n2"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|cis
operator|.
name|setN3Usage
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"n3"
argument_list|,
literal|100
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// nothing free on node3
comment|// Retrieve the count of shards on each node
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|nodesToShardCount
init|=
name|newHashMap
argument_list|()
decl_stmt|;
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
name|ClusterStateResponse
name|resp
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
decl_stmt|;
name|Iterator
argument_list|<
name|RoutingNode
argument_list|>
name|iter
init|=
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RoutingNode
name|node
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> node {} has {} shards"
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|,
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|numberOfOwningShards
argument_list|()
argument_list|)
expr_stmt|;
name|nodesToShardCount
operator|.
name|put
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|,
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|numberOfOwningShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
literal|"node1 has 5 shards"
argument_list|,
name|nodesToShardCount
operator|.
name|get
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"node2 has 5 shards"
argument_list|,
name|nodesToShardCount
operator|.
name|get
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"node3 has 0 shards"
argument_list|,
name|nodesToShardCount
operator|.
name|get
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Update the disk usages so one node is now back under the high watermark
name|cis
operator|.
name|setN1Usage
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"n1"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|cis
operator|.
name|setN2Usage
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"n2"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|cis
operator|.
name|setN3Usage
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
operator|new
name|DiskUsage
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|"n3"
argument_list|,
literal|100
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
comment|// node3 has free space now
comment|// Retrieve the count of shards on each node
name|nodesToShardCount
operator|.
name|clear
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
name|ClusterStateResponse
name|resp
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
decl_stmt|;
name|Iterator
argument_list|<
name|RoutingNode
argument_list|>
name|iter
init|=
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RoutingNode
name|node
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> node {} has {} shards"
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|,
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|numberOfOwningShards
argument_list|()
argument_list|)
expr_stmt|;
name|nodesToShardCount
operator|.
name|put
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|,
name|resp
operator|.
name|getState
argument_list|()
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
operator|.
name|numberOfOwningShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
literal|"node1 has at least 3 shards"
argument_list|,
name|nodesToShardCount
operator|.
name|get
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"node2 has at least 3 shards"
argument_list|,
name|nodesToShardCount
operator|.
name|get
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"node3 has at least 3 shards"
argument_list|,
name|nodesToShardCount
operator|.
name|get
argument_list|(
name|realNodeNames
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Create a fake NodeStats for the given node and usage */
DECL|method|makeStats
specifier|public
specifier|static
name|NodeStats
name|makeStats
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|DiskUsage
name|usage
parameter_list|)
block|{
name|FsInfo
operator|.
name|Path
index|[]
name|paths
init|=
operator|new
name|FsInfo
operator|.
name|Path
index|[
literal|1
index|]
decl_stmt|;
name|FsInfo
operator|.
name|Path
name|path
init|=
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/path.data"
argument_list|,
literal|null
argument_list|,
name|usage
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|)
decl_stmt|;
name|paths
index|[
literal|0
index|]
operator|=
name|path
expr_stmt|;
name|FsInfo
name|fsInfo
init|=
operator|new
name|FsInfo
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|paths
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeStats
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
name|nodeName
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|fsInfo
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

