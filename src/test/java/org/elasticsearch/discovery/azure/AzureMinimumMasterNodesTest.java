begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|azure
package|;
end_package

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
name|MasterNotDiscoveredException
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
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|notNullValue
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
name|nullValue
import|;
end_import

begin_comment
comment|/**  * Reported issue in #15  * (https://github.com/elasticsearch/elasticsearch-cloud-azure/issues/15)  */
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
name|SUITE
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|,
name|numClientNodes
operator|=
literal|0
argument_list|)
DECL|class|AzureMinimumMasterNodesTest
specifier|public
class|class
name|AzureMinimumMasterNodesTest
extends|extends
name|AbstractAzureComputeServiceTest
block|{
DECL|method|AzureMinimumMasterNodesTest
specifier|public
name|AzureMinimumMasterNodesTest
parameter_list|()
block|{
name|super
argument_list|(
name|AzureComputeServiceTwoNodesMock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|settingsBuilder
specifier|protected
specifier|final
name|Settings
name|settingsBuilder
parameter_list|()
block|{
name|ImmutableSettings
operator|.
name|Builder
name|builder
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.zen.minimum_master_nodes"
argument_list|,
literal|2
argument_list|)
operator|.
name|put
argument_list|(
name|super
operator|.
name|settingsBuilder
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|simpleOnlyMasterNodeElection
specifier|public
name|void
name|simpleOnlyMasterNodeElection
parameter_list|()
throws|throws
name|IOException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> start data node / non master node"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settingsBuilder
argument_list|()
argument_list|)
expr_stmt|;
try|try
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
name|setMasterNodeTimeout
argument_list|(
literal|"100ms"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not be able to find master"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MasterNotDiscoveredException
name|e
parameter_list|)
block|{
comment|// all is well, no master elected
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> start another node"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settingsBuilder
argument_list|()
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
name|prepareState
argument_list|()
operator|.
name|setMasterNodeTimeout
argument_list|(
literal|"1s"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stop master node"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopCurrentMasterNode
argument_list|()
expr_stmt|;
try|try
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
name|setMasterNodeTimeout
argument_list|(
literal|"1s"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should not be able to find master"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MasterNotDiscoveredException
name|e
parameter_list|)
block|{
comment|// all is well, no master elected
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> start another node"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settingsBuilder
argument_list|()
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
name|prepareState
argument_list|()
operator|.
name|setMasterNodeTimeout
argument_list|(
literal|"1s"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

