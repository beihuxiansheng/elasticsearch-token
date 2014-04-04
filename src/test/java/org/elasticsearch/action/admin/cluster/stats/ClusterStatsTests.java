begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.stats
package|package
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
name|stats
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
name|health
operator|.
name|ClusterHealthResponse
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
name|health
operator|.
name|ClusterHealthStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
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
name|monitor
operator|.
name|sigar
operator|.
name|SigarService
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
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|is
import|;
end_import

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|SUITE
argument_list|,
name|numNodes
operator|=
literal|1
argument_list|)
DECL|class|ClusterStatsTests
specifier|public
class|class
name|ClusterStatsTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|method|assertCounts
specifier|private
name|void
name|assertCounts
parameter_list|(
name|ClusterStatsNodes
operator|.
name|Counts
name|counts
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|masterOnly
parameter_list|,
name|int
name|dataOnly
parameter_list|,
name|int
name|masterData
parameter_list|,
name|int
name|client
parameter_list|)
block|{
name|assertThat
argument_list|(
name|counts
operator|.
name|getTotal
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counts
operator|.
name|getMasterOnly
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|masterOnly
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counts
operator|.
name|getDataOnly
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|dataOnly
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counts
operator|.
name|getMasterData
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|masterData
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counts
operator|.
name|getClient
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|client
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForNodes
specifier|private
name|void
name|waitForNodes
parameter_list|(
name|int
name|numNodes
parameter_list|)
block|{
name|ClusterHealthResponse
name|actionGet
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
name|health
argument_list|(
name|Requests
operator|.
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForNodes
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|numNodes
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actionGet
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeCounts
specifier|public
name|void
name|testNodeCounts
parameter_list|()
block|{
name|ClusterStatsResponse
name|response
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
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertCounts
argument_list|(
name|response
operator|.
name|getNodesStats
argument_list|()
operator|.
name|getCounts
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|waitForNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertCounts
argument_list|(
name|response
operator|.
name|getNodesStats
argument_list|()
operator|.
name|getCounts
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.master"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|waitForNodes
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|assertCounts
argument_list|(
name|response
operator|.
name|getNodesStats
argument_list|()
operator|.
name|getCounts
argument_list|()
argument_list|,
literal|3
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.client"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|waitForNodes
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|assertCounts
argument_list|(
name|response
operator|.
name|getNodesStats
argument_list|()
operator|.
name|getCounts
argument_list|()
argument_list|,
literal|4
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertShardStats
specifier|private
name|void
name|assertShardStats
parameter_list|(
name|ClusterStatsIndices
operator|.
name|ShardStats
name|stats
parameter_list|,
name|int
name|indices
parameter_list|,
name|int
name|total
parameter_list|,
name|int
name|primaries
parameter_list|,
name|double
name|replicationFactor
parameter_list|)
block|{
name|assertThat
argument_list|(
name|stats
operator|.
name|getIndices
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|indices
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getTotal
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getPrimaries
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|primaries
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getReplication
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|replicationFactor
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndicesShardStats
specifier|public
name|void
name|testIndicesShardStats
parameter_list|()
block|{
name|ClusterStatsResponse
name|response
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
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|2
argument_list|,
literal|"number_of_replicas"
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|indicesStats
operator|.
name|getDocs
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|indicesStats
operator|.
name|getIndexCount
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertShardStats
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
comment|// add another node, replicas should get assigned
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|index
argument_list|(
literal|"test1"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
literal|"f"
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
comment|// make the doc visible
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|indicesStats
operator|.
name|getDocs
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertShardStats
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|4
argument_list|,
literal|2
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|setSettings
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|3
argument_list|,
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStatus
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|indicesStats
operator|.
name|getIndexCount
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertShardStats
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|7
argument_list|,
literal|5
argument_list|,
literal|2.0
operator|/
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getAvgIndexPrimaryShards
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|2.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getMinIndexPrimaryShards
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getMaxIndexPrimaryShards
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getAvgIndexShards
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|3.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getMinIndexShards
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getMaxIndexShards
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getAvgIndexReplication
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|0.5
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getMinIndexReplication
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|0.0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getIndicesStats
argument_list|()
operator|.
name|getShards
argument_list|()
operator|.
name|getMaxIndexReplication
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|1.0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testValuesSmokeScreen
specifier|public
name|void
name|testValuesSmokeScreen
parameter_list|()
block|{
name|cluster
argument_list|()
operator|.
name|ensureAtMostNumNodes
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|ensureAtLeastNumNodes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|SigarService
name|sigarService
init|=
name|cluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|SigarService
operator|.
name|class
argument_list|)
decl_stmt|;
name|index
argument_list|(
literal|"test1"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
literal|"f"
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
name|ClusterStatsResponse
name|response
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
name|prepareClusterStats
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|946681200000l
argument_list|)
argument_list|)
expr_stmt|;
comment|// 1 Jan 2000
name|assertThat
argument_list|(
name|response
operator|.
name|indicesStats
operator|.
name|getStore
argument_list|()
operator|.
name|getSizeInBytes
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getFs
argument_list|()
operator|.
name|getTotal
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getJvm
argument_list|()
operator|.
name|getVersions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|sigarService
operator|.
name|sigarAvailable
argument_list|()
condition|)
block|{
comment|// We only get those if we have sigar
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getOs
argument_list|()
operator|.
name|getAvailableProcessors
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getOs
argument_list|()
operator|.
name|getAvailableMemory
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getOs
argument_list|()
operator|.
name|getCpus
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getVersions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getVersions
argument_list|()
operator|.
name|contains
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getPlugins
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThanOrEqualTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getProcess
argument_list|()
operator|.
name|count
argument_list|,
name|Matchers
operator|.
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// 0 happens when not supported on platform
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getProcess
argument_list|()
operator|.
name|getAvgOpenFileDescriptors
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThanOrEqualTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
comment|// these can be -1 if not supported on platform
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getProcess
argument_list|()
operator|.
name|getMinOpenFileDescriptors
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThanOrEqualTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|nodesStats
operator|.
name|getProcess
argument_list|()
operator|.
name|getMaxOpenFileDescriptors
argument_list|()
argument_list|,
name|Matchers
operator|.
name|greaterThanOrEqualTo
argument_list|(
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

