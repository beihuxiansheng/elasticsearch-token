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
name|health
operator|.
name|ClusterStateHealth
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
name|node
operator|.
name|DiscoveryNodes
operator|.
name|Builder
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
name|FailedRerouteAllocation
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
name|ESAllocationTestCase
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
name|HashMap
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
DECL|class|PrimaryTermsTests
specifier|public
class|class
name|PrimaryTermsTests
extends|extends
name|ESAllocationTestCase
block|{
DECL|field|TEST_INDEX_1
specifier|private
specifier|static
specifier|final
name|String
name|TEST_INDEX_1
init|=
literal|"test1"
decl_stmt|;
DECL|field|TEST_INDEX_2
specifier|private
specifier|static
specifier|final
name|String
name|TEST_INDEX_2
init|=
literal|"test2"
decl_stmt|;
DECL|field|testRoutingTable
specifier|private
name|RoutingTable
name|testRoutingTable
decl_stmt|;
DECL|field|numberOfShards
specifier|private
name|int
name|numberOfShards
decl_stmt|;
DECL|field|numberOfReplicas
specifier|private
name|int
name|numberOfReplicas
decl_stmt|;
DECL|field|DEFAULT_SETTINGS
specifier|private
specifier|final
specifier|static
name|Settings
name|DEFAULT_SETTINGS
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|allocationService
specifier|private
name|AllocationService
name|allocationService
decl_stmt|;
DECL|field|clusterState
specifier|private
name|ClusterState
name|clusterState
decl_stmt|;
DECL|field|primaryTermsPerIndex
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|long
index|[]
argument_list|>
name|primaryTermsPerIndex
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
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
name|this
operator|.
name|allocationService
operator|=
name|createAllocationService
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_concurrent_recoveries"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
comment|// don't limit recoveries
operator|.
name|put
argument_list|(
literal|"cluster.routing.allocation.node_initial_primaries_recoveries"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfShards
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfReplicas
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Setup test with "
operator|+
name|this
operator|.
name|numberOfShards
operator|+
literal|" shards and "
operator|+
name|this
operator|.
name|numberOfReplicas
operator|+
literal|" replicas."
argument_list|)
expr_stmt|;
name|this
operator|.
name|primaryTermsPerIndex
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|createIndexMetaData
argument_list|(
name|TEST_INDEX_1
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|createIndexMetaData
argument_list|(
name|TEST_INDEX_2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|this
operator|.
name|testRoutingTable
operator|=
operator|new
name|RoutingTable
operator|.
name|Builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|IndexRoutingTable
operator|.
name|Builder
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
name|TEST_INDEX_1
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|initializeAsNew
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
name|TEST_INDEX_1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|IndexRoutingTable
operator|.
name|Builder
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
name|TEST_INDEX_2
argument_list|)
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|initializeAsNew
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
name|TEST_INDEX_2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|clusterState
operator|=
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
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|testRoutingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|/**      * puts primary shard routings into initializing state      */
DECL|method|initPrimaries
specifier|private
name|void
name|initPrimaries
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"adding "
operator|+
operator|(
name|this
operator|.
name|numberOfReplicas
operator|+
literal|1
operator|)
operator|+
literal|" nodes and performing rerouting"
argument_list|)
expr_stmt|;
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
name|this
operator|.
name|numberOfReplicas
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|discoBuilder
operator|=
name|discoBuilder
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
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
name|discoBuilder
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|,
literal|"reroute"
argument_list|)
decl_stmt|;
name|this
operator|.
name|testRoutingTable
operator|=
name|rerouteResult
operator|.
name|routingTable
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|rerouteResult
operator|.
name|changed
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|applyRerouteResult
argument_list|(
name|rerouteResult
argument_list|)
expr_stmt|;
name|primaryTermsPerIndex
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|this
operator|::
name|incrementPrimaryTerm
argument_list|)
expr_stmt|;
block|}
DECL|method|incrementPrimaryTerm
specifier|private
name|void
name|incrementPrimaryTerm
parameter_list|(
name|String
name|index
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|primaryTerms
init|=
name|primaryTermsPerIndex
operator|.
name|get
argument_list|(
name|index
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
name|primaryTerms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|primaryTerms
index|[
name|i
index|]
operator|++
expr_stmt|;
block|}
block|}
DECL|method|incrementPrimaryTerm
specifier|private
name|void
name|incrementPrimaryTerm
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shard
parameter_list|)
block|{
name|primaryTermsPerIndex
operator|.
name|get
argument_list|(
name|index
argument_list|)
index|[
name|shard
index|]
operator|++
expr_stmt|;
block|}
DECL|method|startInitializingShards
specifier|private
name|boolean
name|startInitializingShards
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
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
name|this
operator|.
name|testRoutingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|startedShards
init|=
name|this
operator|.
name|clusterState
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|index
argument_list|,
name|INITIALIZING
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"start primary shards for index [{}]: {} "
argument_list|,
name|index
argument_list|,
name|startedShards
argument_list|)
expr_stmt|;
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
init|=
name|allocationService
operator|.
name|applyStartedShards
argument_list|(
name|this
operator|.
name|clusterState
argument_list|,
name|startedShards
argument_list|)
decl_stmt|;
name|applyRerouteResult
argument_list|(
name|rerouteResult
argument_list|)
expr_stmt|;
return|return
name|rerouteResult
operator|.
name|changed
argument_list|()
return|;
block|}
DECL|method|applyRerouteResult
specifier|private
name|void
name|applyRerouteResult
parameter_list|(
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
parameter_list|)
block|{
name|ClusterState
name|previousClusterState
init|=
name|this
operator|.
name|clusterState
decl_stmt|;
name|ClusterState
name|newClusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|previousClusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|rerouteResult
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterState
operator|.
name|Builder
name|builder
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|newClusterState
argument_list|)
operator|.
name|incrementVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|previousClusterState
operator|.
name|routingTable
argument_list|()
operator|!=
name|newClusterState
operator|.
name|routingTable
argument_list|()
condition|)
block|{
name|builder
operator|.
name|routingTable
argument_list|(
name|RoutingTable
operator|.
name|builder
argument_list|(
name|newClusterState
operator|.
name|routingTable
argument_list|()
argument_list|)
operator|.
name|version
argument_list|(
name|newClusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|previousClusterState
operator|.
name|metaData
argument_list|()
operator|!=
name|newClusterState
operator|.
name|metaData
argument_list|()
condition|)
block|{
name|builder
operator|.
name|metaData
argument_list|(
name|MetaData
operator|.
name|builder
argument_list|(
name|newClusterState
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|version
argument_list|(
name|newClusterState
operator|.
name|metaData
argument_list|()
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|clusterState
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|testRoutingTable
operator|=
name|rerouteResult
operator|.
name|routingTable
argument_list|()
expr_stmt|;
specifier|final
name|ClusterStateHealth
name|clusterHealth
init|=
operator|new
name|ClusterStateHealth
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"applied reroute. active shards: p [{}], t [{}], init shards: [{}], relocating: [{}]"
argument_list|,
name|clusterHealth
operator|.
name|getActivePrimaryShards
argument_list|()
argument_list|,
name|clusterHealth
operator|.
name|getActiveShards
argument_list|()
argument_list|,
name|clusterHealth
operator|.
name|getInitializingShards
argument_list|()
argument_list|,
name|clusterHealth
operator|.
name|getRelocatingShards
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|failSomePrimaries
specifier|private
name|void
name|failSomePrimaries
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
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
name|this
operator|.
name|testRoutingTable
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|IndexRoutingTable
name|indexShardRoutingTable
init|=
name|testRoutingTable
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|shardIdsToFail
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
literal|1
operator|+
name|randomInt
argument_list|(
name|numberOfShards
operator|-
literal|1
argument_list|)
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|shardIdsToFail
operator|.
name|add
argument_list|(
name|randomInt
argument_list|(
name|numberOfShards
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"failing primary shards {} for index [{}]"
argument_list|,
name|shardIdsToFail
argument_list|,
name|index
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|FailedRerouteAllocation
operator|.
name|FailedShard
argument_list|>
name|failedShards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|shard
range|:
name|shardIdsToFail
control|)
block|{
name|failedShards
operator|.
name|add
argument_list|(
operator|new
name|FailedRerouteAllocation
operator|.
name|FailedShard
argument_list|(
name|indexShardRoutingTable
operator|.
name|shard
argument_list|(
name|shard
argument_list|)
operator|.
name|primaryShard
argument_list|()
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|incrementPrimaryTerm
argument_list|(
name|index
argument_list|,
name|shard
argument_list|)
expr_stmt|;
comment|// the primary failure should increment the primary term;
block|}
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
init|=
name|allocationService
operator|.
name|applyFailedShards
argument_list|(
name|this
operator|.
name|clusterState
argument_list|,
name|failedShards
argument_list|)
decl_stmt|;
name|applyRerouteResult
argument_list|(
name|rerouteResult
argument_list|)
expr_stmt|;
block|}
DECL|method|addNodes
specifier|private
name|void
name|addNodes
parameter_list|()
block|{
name|DiscoveryNodes
operator|.
name|Builder
name|nodesBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|newNodes
init|=
name|randomInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"adding [{}] nodes"
argument_list|,
name|newNodes
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|newNodes
condition|;
name|i
operator|++
control|)
block|{
name|nodesBuilder
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"extra_"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
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
name|nodesBuilder
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|this
operator|.
name|clusterState
argument_list|,
literal|"nodes added"
argument_list|)
decl_stmt|;
name|applyRerouteResult
argument_list|(
name|rerouteResult
argument_list|)
expr_stmt|;
block|}
DECL|method|createIndexMetaData
specifier|private
name|IndexMetaData
operator|.
name|Builder
name|createIndexMetaData
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|primaryTermsPerIndex
operator|.
name|put
argument_list|(
name|indexName
argument_list|,
operator|new
name|long
index|[
name|numberOfShards
index|]
argument_list|)
expr_stmt|;
specifier|final
name|IndexMetaData
operator|.
name|Builder
name|builder
init|=
operator|new
name|IndexMetaData
operator|.
name|Builder
argument_list|(
name|indexName
argument_list|)
operator|.
name|settings
argument_list|(
name|DEFAULT_SETTINGS
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
name|this
operator|.
name|numberOfReplicas
argument_list|)
operator|.
name|numberOfShards
argument_list|(
name|this
operator|.
name|numberOfShards
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
name|builder
operator|.
name|primaryTerm
argument_list|(
name|i
argument_list|,
name|randomInt
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|primaryTermsPerIndex
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
index|[
name|i
index|]
operator|=
name|builder
operator|.
name|primaryTerm
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|assertAllPrimaryTerm
specifier|private
name|void
name|assertAllPrimaryTerm
parameter_list|()
block|{
name|primaryTermsPerIndex
operator|.
name|keySet
argument_list|()
operator|.
name|forEach
argument_list|(
name|this
operator|::
name|assertPrimaryTerm
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPrimaryTerm
specifier|private
name|void
name|assertPrimaryTerm
parameter_list|(
name|String
name|index
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|terms
init|=
name|primaryTermsPerIndex
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|IndexMetaData
name|indexMetaData
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|shardRoutingTable
range|:
name|this
operator|.
name|testRoutingTable
operator|.
name|index
argument_list|(
name|index
argument_list|)
control|)
block|{
specifier|final
name|int
name|shard
init|=
name|shardRoutingTable
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"primary term mismatch between indexMetaData of ["
operator|+
name|index
operator|+
literal|"] and shard ["
operator|+
name|shard
operator|+
literal|"]'s routing"
argument_list|,
name|indexMetaData
operator|.
name|primaryTerm
argument_list|(
name|shard
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|terms
index|[
name|shard
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPrimaryTermMetaDataSync
specifier|public
name|void
name|testPrimaryTermMetaDataSync
parameter_list|()
block|{
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
name|initPrimaries
argument_list|()
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
name|startInitializingShards
argument_list|(
name|TEST_INDEX_1
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
name|startInitializingShards
argument_list|(
name|TEST_INDEX_2
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
comment|// now start all replicas too
name|startInitializingShards
argument_list|(
name|TEST_INDEX_1
argument_list|)
expr_stmt|;
name|startInitializingShards
argument_list|(
name|TEST_INDEX_2
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
comment|// relocations shouldn't change much
name|addNodes
argument_list|()
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
name|boolean
name|changed
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|changed
condition|)
block|{
name|changed
operator|=
name|startInitializingShards
argument_list|(
name|TEST_INDEX_1
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
name|changed
operator||=
name|startInitializingShards
argument_list|(
name|TEST_INDEX_2
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
block|}
comment|// primary promotion
name|failSomePrimaries
argument_list|(
name|TEST_INDEX_1
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
comment|// stablize cluster
name|changed
operator|=
literal|true
expr_stmt|;
while|while
condition|(
name|changed
condition|)
block|{
name|changed
operator|=
name|startInitializingShards
argument_list|(
name|TEST_INDEX_1
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
name|changed
operator||=
name|startInitializingShards
argument_list|(
name|TEST_INDEX_2
argument_list|)
expr_stmt|;
name|assertAllPrimaryTerm
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

