begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

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
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|recovery
operator|.
name|ShardRecoveryResponse
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
name|count
operator|.
name|CountResponse
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
name|allocation
operator|.
name|decider
operator|.
name|EnableAllocationDecider
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
name|ElasticsearchBackwardsCompatIntegrationTest
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
argument_list|(
name|numDataNodes
operator|=
literal|0
argument_list|,
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|TEST
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|RecoveryBackwardsCompatibilityTests
specifier|public
class|class
name|RecoveryBackwardsCompatibilityTests
extends|extends
name|ElasticsearchBackwardsCompatIntegrationTest
block|{
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
name|ImmutableSettings
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
operator|.
name|put
argument_list|(
literal|"action.admin.cluster.node.shutdown.delay"
argument_list|,
literal|"10ms"
argument_list|)
operator|.
name|put
argument_list|(
literal|"gateway.recover_after_nodes"
argument_list|,
literal|2
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|minExternalNodes
specifier|protected
name|int
name|minExternalNodes
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
DECL|method|maxExternalNodes
specifier|protected
name|int
name|maxExternalNodes
parameter_list|()
block|{
return|return
literal|3
return|;
block|}
annotation|@
name|Test
annotation|@
name|LuceneTestCase
operator|.
name|Slow
DECL|method|testReusePeerRecovery
specifier|public
name|void
name|testReusePeerRecovery
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_REBALANCE_ENABLE
argument_list|,
name|EnableAllocationDecider
operator|.
name|Rebalance
operator|.
name|NONE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing docs"
argument_list|)
expr_stmt|;
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
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> bump number of replicas from 0 to 1"
argument_list|)
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
name|prepareFlush
argument_list|()
operator|.
name|execute
argument_list|()
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
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|"1"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|assertAllShardsOnNodes
argument_list|(
literal|"test"
argument_list|,
name|backwardsCluster
argument_list|()
operator|.
name|backwardsNodePattern
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> upgrade cluster"
argument_list|)
expr_stmt|;
name|logClusterState
argument_list|()
expr_stmt|;
name|CountResponse
name|countResponse
init|=
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|countResponse
argument_list|,
name|numDocs
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
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE
argument_list|,
literal|"none"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|backwardsCluster
argument_list|()
operator|.
name|upgradeAllNodes
argument_list|()
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
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE
argument_list|,
literal|"all"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|countResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|countResponse
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
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
name|setDetailed
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardRecoveryResponse
name|response
range|:
name|recoveryResponse
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
control|)
block|{
name|RecoveryState
name|recoveryState
init|=
name|response
operator|.
name|recoveryState
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|recoveryState
operator|.
name|getPrimary
argument_list|()
condition|)
block|{
name|RecoveryState
operator|.
name|Index
name|index
init|=
name|recoveryState
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|recoveredByteCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|reusedByteCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|reusedByteCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|index
operator|.
name|totalByteCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|recoveredFileCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|index
operator|.
name|totalFileCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|percentBytesRecovered
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0.f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|percentFilesRecovered
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0.f
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|index
operator|.
name|toString
argument_list|()
argument_list|,
name|index
operator|.
name|reusedByteCount
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|index
operator|.
name|numberOfRecoveredBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO upgrade via optimize?
block|}
block|}
block|}
block|}
end_class

end_unit

