begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|recovery
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
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthRequestBuilder
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
name|indices
operator|.
name|recovery
operator|.
name|RecoveryResponse
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
name|routing
operator|.
name|RecoverySource
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
name|UnassignedInfo
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
name|Priority
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
name|MapBuilder
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
name|discovery
operator|.
name|zen
operator|.
name|ZenDiscovery
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
name|recovery
operator|.
name|RecoveryState
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
name|ESIntegTestCase
operator|.
name|ClusterScope
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
operator|.
name|Scope
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
name|assertHitCount
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|FullRollingRestartIT
specifier|public
class|class
name|FullRollingRestartIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|assertTimeout
specifier|protected
name|void
name|assertTimeout
parameter_list|(
name|ClusterHealthRequestBuilder
name|requestBuilder
parameter_list|)
block|{
name|ClusterHealthResponse
name|clusterHealth
init|=
name|requestBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterHealth
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"cluster health request timed out:\n{}"
argument_list|,
name|clusterHealth
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"cluster health request timed out"
argument_list|)
expr_stmt|;
block|}
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
literal|1
return|;
block|}
DECL|method|testFullRollingRestart
specifier|public
name|void
name|testFullRollingRestart
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ZenDiscovery
operator|.
name|JOIN_TIMEOUT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"30s"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|healthTimeout
init|=
literal|"1m"
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|MapBuilder
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|map
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|flush
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1000
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|MapBuilder
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|map
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> now start adding nodes"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|healthTimeout
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNoRelocatingShards
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> add two more nodes"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
name|settings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// We now have 5 nodes
name|setMinimumMasterNodes
argument_list|(
literal|3
argument_list|)
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|healthTimeout
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNoRelocatingShards
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> refreshing and checking data"
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
literal|2000L
argument_list|)
expr_stmt|;
block|}
comment|// now start shutting nodes down
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|healthTimeout
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNoRelocatingShards
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
comment|// going down to 3 nodes. note that the min_master_node may not be in effect when we shutdown the 4th
comment|// node, but that's OK as it is set to 3 before.
name|setMinimumMasterNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|healthTimeout
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNoRelocatingShards
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stopped two nodes, verifying data"
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
literal|2000L
argument_list|)
expr_stmt|;
block|}
comment|// closing the 3rd node
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is green, and all has been recovered
name|assertTimeout
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|healthTimeout
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNoRelocatingShards
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
comment|// closing the 2nd node
name|setMinimumMasterNodes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
comment|// make sure the cluster state is yellow, and all has been recovered
name|assertTimeout
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
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|healthTimeout
argument_list|)
operator|.
name|setWaitForYellowStatus
argument_list|()
operator|.
name|setWaitForNoRelocatingShards
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForNodes
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> one node left, verifying data"
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
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
literal|2000L
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNoRebalanceOnRollingRestart
specifier|public
name|void
name|testNoRebalanceOnRollingRestart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// see https://github.com/elastic/elasticsearch/issues/14387
name|internalCluster
argument_list|()
operator|.
name|startMasterOnlyNode
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startDataOnlyNodesAsync
argument_list|(
literal|3
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|/**          * We start 3 nodes and a dedicated master. Restart on of the data-nodes and ensure that we got no relocations.          * Yet we have 6 shards 0 replica so that means if the restarting node comes back both other nodes are subject          * to relocating to the restarting node since all had 2 shards and now one node has nothing allocated.          * We have a fix for this to wait until we have allocated unallocated shards now so this shouldn't happen.          */
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
literal|"6"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|"0"
argument_list|)
operator|.
name|put
argument_list|(
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|setSource
argument_list|(
name|MapBuilder
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|newMapBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"test"
argument_list|,
literal|"value"
operator|+
name|i
argument_list|)
operator|.
name|map
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|ensureGreen
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
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|RecoveryResponse
name|recoveryResponse
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
name|prepareRecoveries
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|RecoveryState
name|recoveryState
range|:
name|recoveryResponse
operator|.
name|shardRecoveryStates
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
literal|"relocated from: "
operator|+
name|recoveryState
operator|.
name|getSourceNode
argument_list|()
operator|+
literal|" to: "
operator|+
name|recoveryState
operator|.
name|getTargetNode
argument_list|()
operator|+
literal|"\n"
operator|+
name|state
operator|.
name|prettyPrint
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getRecoverySource
argument_list|()
operator|.
name|getType
argument_list|()
operator|!=
name|RecoverySource
operator|.
name|Type
operator|.
name|PEER
operator|||
name|recoveryState
operator|.
name|getPrimary
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
block|}
name|internalCluster
argument_list|()
operator|.
name|restartRandomDataNode
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|ClusterState
name|afterState
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
name|recoveryResponse
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRecoveries
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
for|for
control|(
name|RecoveryState
name|recoveryState
range|:
name|recoveryResponse
operator|.
name|shardRecoveryStates
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
control|)
block|{
name|assertTrue
argument_list|(
literal|"relocated from: "
operator|+
name|recoveryState
operator|.
name|getSourceNode
argument_list|()
operator|+
literal|" to: "
operator|+
name|recoveryState
operator|.
name|getTargetNode
argument_list|()
operator|+
literal|"-- \nbefore: \n"
operator|+
name|state
operator|.
name|prettyPrint
argument_list|()
operator|+
literal|"\nafter: \n"
operator|+
name|afterState
operator|.
name|prettyPrint
argument_list|()
argument_list|,
name|recoveryState
operator|.
name|getRecoverySource
argument_list|()
operator|.
name|getType
argument_list|()
operator|!=
name|RecoverySource
operator|.
name|Type
operator|.
name|PEER
operator|||
name|recoveryState
operator|.
name|getPrimary
argument_list|()
operator|==
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

