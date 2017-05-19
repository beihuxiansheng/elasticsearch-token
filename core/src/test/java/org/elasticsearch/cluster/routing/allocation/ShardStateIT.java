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
name|action
operator|.
name|index
operator|.
name|IndexRequest
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
name|ClusterHealthStatus
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
name|Murmur3HashFunction
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
name|xcontent
operator|.
name|XContentType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|IndexShard
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
name|IndicesService
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

begin_class
DECL|class|ShardStateIT
specifier|public
class|class
name|ShardStateIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|testPrimaryFailureIncreasesTerm
specifier|public
name|void
name|testPrimaryFailureIncreasesTerm
parameter_list|()
throws|throws
name|Exception
block|{
name|internalCluster
argument_list|()
operator|.
name|ensureAtLeastNumDataNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|2
argument_list|,
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|assertPrimaryTerms
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> disabling allocation to capture shard failure"
argument_list|)
expr_stmt|;
name|disableAllocation
argument_list|(
literal|"test"
argument_list|)
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
specifier|final
name|int
name|shard
init|=
name|randomBoolean
argument_list|()
condition|?
literal|0
else|:
literal|1
decl_stmt|;
specifier|final
name|String
name|nodeId
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|shard
argument_list|(
name|shard
argument_list|)
operator|.
name|primaryShard
argument_list|()
operator|.
name|currentNodeId
argument_list|()
decl_stmt|;
specifier|final
name|String
name|node
init|=
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> failing primary of [{}] on node [{}]"
argument_list|,
name|shard
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|getShard
argument_list|(
name|shard
argument_list|)
operator|.
name|failShard
argument_list|(
literal|"simulated test failure"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for a yellow index"
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
comment|// this forces the primary term to propagate to the replicas
name|int
name|id
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// find an ID that routes to the right shard, we will only index to the shard that saw a primary failure
specifier|final
name|String
name|idAsString
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
decl_stmt|;
specifier|final
name|int
name|hash
init|=
name|Math
operator|.
name|floorMod
argument_list|(
name|Murmur3HashFunction
operator|.
name|hash
argument_list|(
name|idAsString
argument_list|)
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|hash
operator|==
name|shard
condition|)
block|{
name|client
argument_list|()
operator|.
name|index
argument_list|(
operator|new
name|IndexRequest
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
name|idAsString
argument_list|)
operator|.
name|source
argument_list|(
literal|"{ \"f\": \""
operator|+
name|idAsString
operator|+
literal|"\"}"
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
break|break;
block|}
else|else
block|{
name|id
operator|++
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|term0
init|=
name|shard
operator|==
literal|0
condition|?
literal|2
else|:
literal|1
decl_stmt|;
specifier|final
name|long
name|term1
init|=
name|shard
operator|==
literal|1
condition|?
literal|2
else|:
literal|1
decl_stmt|;
name|assertPrimaryTerms
argument_list|(
name|term0
argument_list|,
name|term1
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> enabling allocation"
argument_list|)
expr_stmt|;
name|enableAllocation
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|assertPrimaryTerms
argument_list|(
name|term0
argument_list|,
name|term1
argument_list|)
expr_stmt|;
block|}
DECL|method|assertPrimaryTerms
specifier|protected
name|void
name|assertPrimaryTerms
parameter_list|(
name|long
name|shard0Term
parameter_list|,
name|long
name|shard1Term
parameter_list|)
block|{
for|for
control|(
name|String
name|node
range|:
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
control|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"--> asserting primary terms terms on [{}]"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|ClusterState
name|state
init|=
name|client
argument_list|(
name|node
argument_list|)
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
name|setLocal
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|IndexMetaData
name|metaData
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|primaryTerm
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|shard0Term
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metaData
operator|.
name|primaryTerm
argument_list|(
literal|1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|shard1Term
argument_list|)
argument_list|)
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|metaData
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|IndexShard
name|shard
range|:
name|indexService
control|)
block|{
name|assertThat
argument_list|(
literal|"term mismatch for shard "
operator|+
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shard
operator|.
name|getPrimaryTerm
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|metaData
operator|.
name|primaryTerm
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

