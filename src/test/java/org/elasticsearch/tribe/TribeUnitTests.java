begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tribe
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|ElasticsearchTestCase
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|either
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * This test doesn't extend {@link org.elasticsearch.test.ElasticsearchIntegrationTest} as the internal cluster ignores system properties  * all the time, while we need to make the tribe node accept them in this case, so that we can verify that they are not read again as part  * of the tribe client nodes initialization. Note that the started nodes will obey to the 'node.mode' settings as the internal cluster does.  */
end_comment

begin_class
DECL|class|TribeUnitTests
specifier|public
class|class
name|TribeUnitTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|tribe1
specifier|private
specifier|static
name|Node
name|tribe1
decl_stmt|;
DECL|field|tribe2
specifier|private
specifier|static
name|Node
name|tribe2
decl_stmt|;
DECL|field|NODE_MODE
specifier|private
specifier|static
specifier|final
name|String
name|NODE_MODE
init|=
name|InternalTestCluster
operator|.
name|nodeMode
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|createTribes
specifier|public
specifier|static
name|void
name|createTribes
parameter_list|()
block|{
name|tribe1
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"http.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.mode"
argument_list|,
name|NODE_MODE
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"tribe1"
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"tribe1_node"
argument_list|)
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
name|tribe2
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"http.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.mode"
argument_list|,
name|NODE_MODE
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"tribe2"
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"tribe2_node"
argument_list|)
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|closeTribes
specifier|public
specifier|static
name|void
name|closeTribes
parameter_list|()
block|{
name|tribe1
operator|.
name|close
argument_list|()
expr_stmt|;
name|tribe1
operator|=
literal|null
expr_stmt|;
name|tribe2
operator|.
name|close
argument_list|()
expr_stmt|;
name|tribe2
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatTribeClientsIgnoreGlobalSysProps
specifier|public
name|void
name|testThatTribeClientsIgnoreGlobalSysProps
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.cluster.name"
argument_list|,
literal|"tribe_node_cluster"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.tribe.t1.cluster.name"
argument_list|,
literal|"tribe1"
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"es.tribe.t2.cluster.name"
argument_list|,
literal|"tribe2"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertTribeNodeSuccesfullyCreated
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|System
operator|.
name|clearProperty
argument_list|(
literal|"es.cluster.name"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"es.tribe.t1.cluster.name"
argument_list|)
expr_stmt|;
name|System
operator|.
name|clearProperty
argument_list|(
literal|"es.tribe.t2.cluster.name"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testThatTribeClientsIgnoreGlobalConfig
specifier|public
name|void
name|testThatTribeClientsIgnoreGlobalConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|pathConf
init|=
name|Paths
operator|.
name|get
argument_list|(
name|TribeUnitTests
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"elasticsearch.yml"
argument_list|)
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.conf"
argument_list|,
name|pathConf
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTribeNodeSuccesfullyCreated
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|assertTribeNodeSuccesfullyCreated
specifier|private
specifier|static
name|void
name|assertTribeNodeSuccesfullyCreated
parameter_list|(
name|Settings
name|extraSettings
parameter_list|)
throws|throws
name|Exception
block|{
comment|//tribe node doesn't need the node.mode setting, as it's forced local internally anyways. The tribe clients do need it to make sure
comment|//they can find their corresponding tribes using the proper transport
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"http.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"tribe_node"
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.t1.node.mode"
argument_list|,
name|NODE_MODE
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.t2.node.mode"
argument_list|,
name|NODE_MODE
argument_list|)
operator|.
name|put
argument_list|(
name|extraSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
init|(
name|Node
name|node
init|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|node
argument_list|()
init|)
block|{
try|try
init|(
name|Client
name|client
init|=
name|node
operator|.
name|client
argument_list|()
init|)
block|{
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
name|ClusterState
name|state
init|=
name|client
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
name|clear
argument_list|()
operator|.
name|setNodes
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
name|assertThat
argument_list|(
name|state
operator|.
name|getClusterName
argument_list|()
operator|.
name|value
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"tribe_node_cluster"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|getSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|discoveryNode
range|:
name|state
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|discoveryNode
operator|.
name|getName
argument_list|()
argument_list|,
name|either
argument_list|(
name|equalTo
argument_list|(
literal|"tribe1_node"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|equalTo
argument_list|(
literal|"tribe2_node"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|equalTo
argument_list|(
literal|"tribe_node"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|equalTo
argument_list|(
literal|"tribe_node/t1"
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|equalTo
argument_list|(
literal|"tribe_node/t2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
