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
name|action
operator|.
name|index
operator|.
name|IndexRequestBuilder
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
name|InternalTestCluster
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
argument_list|)
DECL|class|DelayedAllocationTests
specifier|public
class|class
name|DelayedAllocationTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|/**      * Verifies that when there is no delay timeout, a 1/1 index shard will immediately      * get allocated to a free node when the node hosting it leaves the cluster.      */
annotation|@
name|Test
DECL|method|testNoDelayedTimeout
specifier|public
name|void
name|testNoDelayedTimeout
parameter_list|()
throws|throws
name|Exception
block|{
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
literal|0
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
name|indexRandomData
argument_list|()
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
name|findNodeWithShard
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
name|prepareHealth
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getDelayedUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
comment|/**      * When we do have delayed allocation set, verifies that even though there is a node      * free to allocate the unassigned shard when the node hosting it leaves, it doesn't      * get allocated. Once we bring the node back, it gets allocated since it existed      * on it before.      */
annotation|@
name|Test
DECL|method|testDelayedAllocationNodeLeavesAndComesBack
specifier|public
name|void
name|testDelayedAllocationNodeLeavesAndComesBack
parameter_list|()
throws|throws
name|Exception
block|{
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|1
argument_list|)
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
name|indexRandomData
argument_list|()
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
name|findNodeWithShard
argument_list|()
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
operator|.
name|routingNodes
argument_list|()
operator|.
name|hasUnassigned
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
name|prepareHealth
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getDelayedUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
comment|// this will use the same data location as the stopped node
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
comment|/**      * With a very small delay timeout, verify that it expires and we get to green even      * though the node hosting the shard is not coming back.      */
annotation|@
name|Test
DECL|method|testDelayedAllocationTimesOut
specifier|public
name|void
name|testDelayedAllocationTimesOut
parameter_list|()
throws|throws
name|Exception
block|{
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
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
name|indexRandomData
argument_list|()
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
name|findNodeWithShard
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
expr_stmt|;
comment|// do a second round with longer delay to make sure it happens
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
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
name|findNodeWithShard
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Verify that when explicitly changing the value of the index setting for the delayed      * allocation to a very small value, it kicks the allocation of the unassigned shard      * even though the node it was hosted on will not come back.      */
annotation|@
name|Test
DECL|method|testDelayedAllocationChangeWithSettingTo100ms
specifier|public
name|void
name|testDelayedAllocationChangeWithSettingTo100ms
parameter_list|()
throws|throws
name|Exception
block|{
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|1
argument_list|)
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
name|indexRandomData
argument_list|()
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
name|findNodeWithShard
argument_list|()
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
operator|.
name|routingNodes
argument_list|()
operator|.
name|hasUnassigned
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
name|prepareHealth
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getDelayedUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
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
name|prepareHealth
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getDelayedUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Verify that when explicitly changing the value of the index setting for the delayed      * allocation to 0, it kicks the allocation of the unassigned shard      * even though the node it was hosted on will not come back.      */
annotation|@
name|Test
DECL|method|testDelayedAllocationChangeWithSettingTo0
specifier|public
name|void
name|testDelayedAllocationChangeWithSettingTo0
parameter_list|()
throws|throws
name|Exception
block|{
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|1
argument_list|)
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
name|indexRandomData
argument_list|()
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
name|findNodeWithShard
argument_list|()
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
operator|.
name|routingNodes
argument_list|()
operator|.
name|hasUnassigned
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
name|prepareHealth
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getDelayedUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
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
name|prepareHealth
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|getDelayedUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|indexRandomData
specifier|private
name|void
name|indexRandomData
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|IndexRequestBuilder
index|[]
name|builders
init|=
operator|new
name|IndexRequestBuilder
index|[
name|numDocs
index|]
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
name|builders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builders
index|[
name|i
index|]
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
comment|// we want to test both full divergent copies of the shard in terms of segments, and
comment|// a case where they are the same (using sync flush), index Random does all this goodness
comment|// already
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
block|}
DECL|method|findNodeWithShard
specifier|private
name|String
name|findNodeWithShard
parameter_list|()
block|{
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
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|startedShards
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|startedShards
argument_list|,
name|getRandom
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|startedShards
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
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

