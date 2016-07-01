begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.allocation
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
name|allocation
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
name|indices
operator|.
name|shards
operator|.
name|IndicesShardStoresResponse
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
name|UnassignedInfo
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
name|ESIntegTestCase
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
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
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
name|greaterThan
import|;
end_import

begin_comment
comment|/**  * Tests for the cluster allocation explanation  */
end_comment

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
DECL|class|ClusterAllocationExplainIT
specifier|public
specifier|final
class|class
name|ClusterAllocationExplainIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|testDelayShards
specifier|public
name|void
name|testDelayShards
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting 3 nodes"
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for 3 nodes to be up"
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
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
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating 'test' index"
argument_list|)
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
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"1m"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|INDEX_NUMBER_OF_SHARDS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|5
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|INDEX_NUMBER_OF_REPLICAS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|1
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
name|logger
operator|.
name|info
argument_list|(
literal|"--> stopping a random node"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ClusterAllocationExplainResponse
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
name|prepareAllocationExplain
argument_list|()
operator|.
name|useAnyUnassignedShard
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|ClusterAllocationExplanation
name|cae
init|=
name|resp
operator|.
name|getExplanation
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|cae
operator|.
name|getShard
argument_list|()
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cae
operator|.
name|isPrimary
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cae
operator|.
name|isAssigned
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"expecting a remaining delay, got: "
operator|+
name|cae
operator|.
name|getRemainingDelayMillis
argument_list|()
argument_list|,
name|cae
operator|.
name|getRemainingDelayMillis
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnassignedShards
specifier|public
name|void
name|testUnassignedShards
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting 3 nodes"
argument_list|)
expr_stmt|;
name|String
name|noAttrNode
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
name|String
name|barAttrNode
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.attr.bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|fooBarAttrNode
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.attr.foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.attr.bar"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Wait for all 3 nodes to be up
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for 3 nodes to be up"
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
name|health
argument_list|(
name|Requests
operator|.
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForNodes
argument_list|(
literal|"3"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"anywhere"
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
literal|"index.number_of_shards"
argument_list|,
literal|5
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"only-baz"
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
literal|"index.routing.allocation.include.bar"
argument_list|,
literal|"baz"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|5
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"only-foo"
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
literal|"index.routing.allocation.include.foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"anywhere"
argument_list|,
literal|"only-baz"
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
literal|"only-foo"
argument_list|)
expr_stmt|;
name|ClusterAllocationExplainResponse
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
name|prepareAllocationExplain
argument_list|()
operator|.
name|setIndex
argument_list|(
literal|"only-foo"
argument_list|)
operator|.
name|setShard
argument_list|(
literal|0
argument_list|)
operator|.
name|setPrimary
argument_list|(
literal|false
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ClusterAllocationExplanation
name|cae
init|=
name|resp
operator|.
name|getExplanation
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|cae
operator|.
name|getShard
argument_list|()
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"only-foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cae
operator|.
name|isPrimary
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cae
operator|.
name|isAssigned
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|cae
operator|.
name|isStillFetchingShardData
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
name|equalTo
argument_list|(
name|cae
operator|.
name|getUnassignedInfo
argument_list|()
operator|.
name|getReason
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"expecting no remaining delay: "
operator|+
name|cae
operator|.
name|getRemainingDelayMillis
argument_list|()
argument_list|,
name|cae
operator|.
name|getRemainingDelayMillis
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|NodeExplanation
argument_list|>
name|explanations
init|=
name|cae
operator|.
name|getNodeExplanations
argument_list|()
decl_stmt|;
name|Float
name|barAttrWeight
init|=
operator|-
literal|1f
decl_stmt|;
name|Float
name|fooBarAttrWeight
init|=
operator|-
literal|1f
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DiscoveryNode
argument_list|,
name|NodeExplanation
argument_list|>
name|entry
range|:
name|explanations
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
name|node
operator|.
name|getName
argument_list|()
decl_stmt|;
name|NodeExplanation
name|explanation
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
name|finalDecision
init|=
name|explanation
operator|.
name|getFinalDecision
argument_list|()
decl_stmt|;
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
name|storeCopy
init|=
name|explanation
operator|.
name|getStoreCopy
argument_list|()
decl_stmt|;
name|Decision
name|d
init|=
name|explanation
operator|.
name|getDecision
argument_list|()
decl_stmt|;
name|float
name|weight
init|=
name|explanation
operator|.
name|getWeight
argument_list|()
decl_stmt|;
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|storeStatus
init|=
name|explanation
operator|.
name|getStoreStatus
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|d
operator|.
name|type
argument_list|()
argument_list|,
name|Decision
operator|.
name|Type
operator|.
name|NO
argument_list|)
expr_stmt|;
if|if
condition|(
name|noAttrNode
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|d
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"node does not match index include filters [foo:\"bar\"]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|storeStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the shard cannot be assigned because one or more allocation decider returns a 'NO' decision"
argument_list|,
name|explanation
operator|.
name|getFinalExplanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
argument_list|,
name|finalDecision
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|barAttrNode
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|d
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"node does not match index include filters [foo:\"bar\"]"
argument_list|)
argument_list|)
expr_stmt|;
name|barAttrWeight
operator|=
name|weight
expr_stmt|;
name|assertNull
argument_list|(
name|storeStatus
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the shard cannot be assigned because one or more allocation decider returns a 'NO' decision"
argument_list|,
name|explanation
operator|.
name|getFinalExplanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
argument_list|,
name|finalDecision
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fooBarAttrNode
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|d
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"the shard cannot be allocated on the same node id"
argument_list|)
argument_list|)
expr_stmt|;
name|fooBarAttrWeight
operator|=
name|weight
expr_stmt|;
name|assertEquals
argument_list|(
name|storeStatus
operator|.
name|getAllocationStatus
argument_list|()
argument_list|,
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
operator|.
name|AllocationStatus
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
argument_list|,
name|finalDecision
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|AVAILABLE
argument_list|,
name|storeCopy
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the shard cannot be assigned because one or more allocation decider returns a 'NO' decision"
argument_list|,
name|explanation
operator|.
name|getFinalExplanation
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"unexpected node with name: "
operator|+
name|nodeName
operator|+
literal|", I have: "
operator|+
name|noAttrNode
operator|+
literal|", "
operator|+
name|barAttrNode
operator|+
literal|", "
operator|+
name|fooBarAttrNode
argument_list|)
expr_stmt|;
block|}
block|}
name|assertFalse
argument_list|(
name|barAttrWeight
operator|==
name|fooBarAttrWeight
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

