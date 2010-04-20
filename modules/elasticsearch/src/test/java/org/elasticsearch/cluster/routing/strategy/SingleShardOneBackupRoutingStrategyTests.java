begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.strategy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|strategy
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
name|util
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
name|util
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
name|util
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
name|testng
operator|.
name|annotations
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
name|metadata
operator|.
name|IndexMetaData
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
name|metadata
operator|.
name|MetaData
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
name|node
operator|.
name|DiscoveryNodes
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
name|RoutingBuilders
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
name|ShardRoutingState
operator|.
name|*
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
name|*
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SingleShardOneBackupRoutingStrategyTests
specifier|public
class|class
name|SingleShardOneBackupRoutingStrategyTests
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
name|SingleShardOneBackupRoutingStrategyTests
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|testSingleIndexFirstStartPrimaryThenBackups
annotation|@
name|Test
specifier|public
name|void
name|testSingleIndexFirstStartPrimaryThenBackups
parameter_list|()
block|{
name|DefaultShardsRoutingStrategy
name|strategy
init|=
operator|new
name|DefaultShardsRoutingStrategy
argument_list|()
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
name|RoutingTable
name|routingTable
init|=
name|routingTable
argument_list|()
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|(
literal|"test"
argument_list|)
operator|.
name|initializeEmpty
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
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
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
name|equalTo
argument_list|(
name|UNASSIGNED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
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
literal|1
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|UNASSIGNED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
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
name|currentNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
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
literal|1
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Adding one node and performing rerouting"
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
name|prevRoutingTable
operator|!=
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
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
name|equalTo
argument_list|(
name|UNASSIGNED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Add another node and perform rerouting"
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
name|prevRoutingTable
operator|=
name|routingTable
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
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
name|prevRoutingTable
operator|!=
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
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
comment|// backup shards are initializing as well, we make sure that they recover from primary *started* shards in the IndicesClusterStateService
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
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
name|equalTo
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Start the primary shard (on node1)"
argument_list|)
expr_stmt|;
name|RoutingNodes
name|routingNodes
init|=
name|routingTable
operator|.
name|routingNodes
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
name|prevRoutingTable
operator|=
name|routingTable
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingNodes
operator|.
name|node
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
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
name|prevRoutingTable
operator|!=
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
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
comment|// backup shards are initializing as well, we make sure that they recover from primary *started* shards in the IndicesClusterStateService
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
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
name|equalTo
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Reroute, nothing should change"
argument_list|)
expr_stmt|;
name|prevRoutingTable
operator|=
name|routingTable
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|prevRoutingTable
operator|==
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Start the backup shard"
argument_list|)
expr_stmt|;
name|routingNodes
operator|=
name|routingTable
operator|.
name|routingNodes
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|prevRoutingTable
operator|=
name|routingTable
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|routingNodes
operator|.
name|node
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|shardsWithState
argument_list|(
name|INITIALIZING
argument_list|)
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
name|prevRoutingTable
operator|!=
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
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
name|equalTo
argument_list|(
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Kill node1, backup shard should become primary"
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
name|remove
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|prevRoutingTable
operator|=
name|routingTable
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
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
name|prevRoutingTable
operator|!=
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
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
comment|// backup shards are initializing as well, we make sure that they recover from primary *started* shards in the IndicesClusterStateService
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
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
name|equalTo
argument_list|(
name|UNASSIGNED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Start another node, backup shard should start initializing"
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
literal|"node3"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|prevRoutingTable
operator|=
name|routingTable
expr_stmt|;
name|routingTable
operator|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
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
name|prevRoutingTable
operator|!=
name|routingTable
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shards
argument_list|()
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
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
name|index
argument_list|(
literal|"test"
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
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
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
comment|// backup shards are initializing as well, we make sure that they recover from primary *started* shards in the IndicesClusterStateService
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
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
name|equalTo
argument_list|(
name|INITIALIZING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|routingTable
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
literal|0
argument_list|)
operator|.
name|backupsShards
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"node3"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newNode
specifier|private
name|DiscoveryNode
name|newNode
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
return|return
operator|new
name|DiscoveryNode
argument_list|(
name|nodeId
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

