begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ESAllocationTestCase
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
name|EmptyClusterInfoService
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
name|allocator
operator|.
name|BalancedShardsAllocator
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
name|AllocationCommands
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
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|decider
operator|.
name|Decision
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
name|MaxRetryAllocationDecider
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
name|test
operator|.
name|gateway
operator|.
name|TestGatewayAllocator
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
name|List
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
name|cluster
operator|.
name|routing
operator|.
name|ShardRoutingState
operator|.
name|STARTED
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
name|UNASSIGNED
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
name|not
import|;
end_import

begin_class
DECL|class|MaxRetryAllocationDeciderTests
specifier|public
class|class
name|MaxRetryAllocationDeciderTests
extends|extends
name|ESAllocationTestCase
block|{
DECL|field|strategy
specifier|private
name|AllocationService
name|strategy
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|strategy
operator|=
operator|new
name|AllocationService
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|AllocationDeciders
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|MaxRetryAllocationDecider
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|new
name|TestGatewayAllocator
argument_list|()
argument_list|,
operator|new
name|BalancedShardsAllocator
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|EmptyClusterInfoService
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
DECL|method|createInitialClusterState
specifier|private
name|ClusterState
name|createInitialClusterState
parameter_list|()
block|{
name|MetaData
operator|.
name|Builder
name|metaBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|metaBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"idx"
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
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|MetaData
name|metaData
init|=
name|metaBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|routingTableBuilder
operator|.
name|addAsNew
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
argument_list|)
expr_stmt|;
name|RoutingTable
name|routingTable
init|=
name|routingTableBuilder
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
name|CLUSTER_NAME_SETTING
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
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
name|add
argument_list|(
name|newNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|add
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
name|RoutingTable
name|prevRoutingTable
init|=
name|routingTable
decl_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|,
literal|"reroute"
argument_list|,
literal|false
argument_list|)
operator|.
name|routingTable
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
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|prevRoutingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|prevRoutingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|UNASSIGNED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|)
expr_stmt|;
return|return
name|clusterState
return|;
block|}
DECL|method|testSingleRetryOnIgnore
specifier|public
name|void
name|testSingleRetryOnIgnore
parameter_list|()
block|{
name|ClusterState
name|clusterState
init|=
name|createInitialClusterState
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
decl_stmt|;
specifier|final
name|int
name|retries
init|=
name|MaxRetryAllocationDecider
operator|.
name|SETTING_ALLOCATION_MAX_RETRY
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
comment|// now fail it N-1 times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|retries
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"boom"
operator|+
name|i
argument_list|,
operator|new
name|UnsupportedOperationException
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|newState
init|=
name|strategy
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
comment|// now we go and check that we are actually stick to unassigned on the next failure
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"boom"
argument_list|,
operator|new
name|UnsupportedOperationException
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|newState
init|=
name|strategy
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|retries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|UNASSIGNED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
argument_list|)
expr_stmt|;
comment|// manual reroute should retry once
name|newState
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|,
operator|new
name|AllocationCommands
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
operator|.
name|getClusterState
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
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
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|retries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
argument_list|)
expr_stmt|;
comment|// now we go and check that we are actually stick to unassigned on the next failure ie. no retry
name|failedShards
operator|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"boom"
argument_list|,
operator|new
name|UnsupportedOperationException
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|newState
operator|=
name|strategy
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|retries
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|UNASSIGNED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailedAllocation
specifier|public
name|void
name|testFailedAllocation
parameter_list|()
block|{
name|ClusterState
name|clusterState
init|=
name|createInitialClusterState
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
decl_stmt|;
specifier|final
name|int
name|retries
init|=
name|MaxRetryAllocationDecider
operator|.
name|SETTING_ALLOCATION_MAX_RETRY
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
comment|// now fail it N-1 times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|retries
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"boom"
operator|+
name|i
argument_list|,
operator|new
name|UnsupportedOperationException
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|newState
init|=
name|strategy
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ShardRouting
name|unassignedPrimary
init|=
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|state
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
operator|+
name|i
argument_list|)
expr_stmt|;
comment|// MaxRetryAllocationDecider#canForceAllocatePrimary should return YES decisions because canAllocate returns YES here
name|assertEquals
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
operator|new
name|MaxRetryAllocationDecider
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|canForceAllocatePrimary
argument_list|(
name|unassignedPrimary
argument_list|,
literal|null
argument_list|,
operator|new
name|RoutingAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|clusterState
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now we go and check that we are actually stick to unassigned on the next failure
block|{
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"boom"
argument_list|,
operator|new
name|UnsupportedOperationException
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|ClusterState
name|newState
init|=
name|strategy
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ShardRouting
name|unassignedPrimary
init|=
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|retries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|state
argument_list|()
argument_list|,
name|UNASSIGNED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
argument_list|)
expr_stmt|;
comment|// MaxRetryAllocationDecider#canForceAllocatePrimary should return a NO decision because canAllocate returns NO here
name|assertEquals
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
operator|new
name|MaxRetryAllocationDecider
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|canForceAllocatePrimary
argument_list|(
name|unassignedPrimary
argument_list|,
literal|null
argument_list|,
operator|new
name|RoutingAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|clusterState
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// change the settings and ensure we can do another round of allocation for that index.
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
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
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|getSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.allocation.max_retries"
argument_list|,
name|retries
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterState
name|newState
init|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|,
literal|"settings changed"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
comment|// good we are initializing and we are maintaining failure information
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|ShardRouting
name|unassignedPrimary
init|=
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
name|retries
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|state
argument_list|()
argument_list|,
name|INITIALIZING
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"boom"
argument_list|)
expr_stmt|;
comment|// bumped up the max retry count, so canForceAllocatePrimary should return a YES decision
name|assertEquals
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
operator|new
name|MaxRetryAllocationDecider
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|canForceAllocatePrimary
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|,
operator|new
name|RoutingAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|clusterState
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// now we start the shard
name|clusterState
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|routingTable
operator|=
name|clusterState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
comment|// all counters have been reset to 0 ie. no unassigned info
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|STARTED
argument_list|)
expr_stmt|;
comment|// now fail again and see if it has a new counter
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedShard
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"ZOOOMG"
argument_list|,
operator|new
name|UnsupportedOperationException
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|newState
operator|=
name|strategy
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newState
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|clusterState
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|clusterState
operator|=
name|newState
expr_stmt|;
name|routingTable
operator|=
name|newState
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|unassignedPrimary
operator|=
name|routingTable
operator|.
name|index
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|shards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|state
argument_list|()
argument_list|,
name|UNASSIGNED
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|unassignedPrimary
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"ZOOOMG"
argument_list|)
expr_stmt|;
comment|// Counter reset, so MaxRetryAllocationDecider#canForceAllocatePrimary should return a YES decision
name|assertEquals
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
operator|new
name|MaxRetryAllocationDecider
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
operator|.
name|canForceAllocatePrimary
argument_list|(
name|unassignedPrimary
argument_list|,
literal|null
argument_list|,
operator|new
name|RoutingAllocation
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|clusterState
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

