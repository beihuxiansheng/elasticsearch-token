begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
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
name|discovery
operator|.
name|Discovery
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
name|*
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
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|)
DECL|class|ZenDiscoveryRejoinOnMaster
specifier|public
class|class
name|ZenDiscoveryRejoinOnMaster
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testChangeRejoinOnMasterOptionIsDynamic
specifier|public
name|void
name|testChangeRejoinOnMasterOptionIsDynamic
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|nodeSettings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"zen"
argument_list|)
comment|//<-- To override the local setting if set externally
operator|.
name|build
argument_list|()
decl_stmt|;
name|String
name|nodeName
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|nodeSettings
argument_list|)
decl_stmt|;
name|ZenDiscovery
name|zenDiscovery
init|=
operator|(
name|ZenDiscovery
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Discovery
operator|.
name|class
argument_list|,
name|nodeName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|zenDiscovery
operator|.
name|isRejoinOnMasterGone
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
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
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ZenDiscovery
operator|.
name|SETTING_REJOIN_ON_MASTER_GONE
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|zenDiscovery
operator|.
name|isRejoinOnMasterGone
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
DECL|method|testNoShardRelocationsOccurWhenElectedMasterNodeFails
specifier|public
name|void
name|testNoShardRelocationsOccurWhenElectedMasterNodeFails
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|defaultSettings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.zen.fd.ping_timeout"
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.fd.ping_retries"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"zen"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
name|masterNodeSettings
init|=
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
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
name|masterNodeSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|Settings
name|dateNodeSettings
init|=
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
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
name|dateNodeSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ClusterHealthResponse
name|clusterHealthResponse
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
name|setWaitForNodes
argument_list|(
literal|"4"
argument_list|)
operator|.
name|setWaitForRelocatingShards
argument_list|(
literal|0
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|clusterHealthResponse
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
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|RecoveryResponse
name|r
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
name|int
name|numRecoveriesBeforeNewMaster
init|=
name|r
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|String
name|oldMaster
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopCurrentMasterNode
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
name|String
name|current
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|current
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|current
argument_list|,
name|not
argument_list|(
name|equalTo
argument_list|(
name|oldMaster
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|r
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
name|int
name|numRecoveriesAfterNewMaster
init|=
name|r
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|numRecoveriesAfterNewMaster
argument_list|,
name|equalTo
argument_list|(
name|numRecoveriesBeforeNewMaster
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

