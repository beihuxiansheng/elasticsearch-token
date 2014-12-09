begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.nodesinfo
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|nodesinfo
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|node
operator|.
name|info
operator|.
name|NodesInfoResponse
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
name|node
operator|.
name|info
operator|.
name|PluginInfo
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
name|ClusterService
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
name|nodesinfo
operator|.
name|plugin
operator|.
name|dummy1
operator|.
name|TestPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|nodesinfo
operator|.
name|plugin
operator|.
name|dummy2
operator|.
name|TestNoVersionPlugin
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
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|client
operator|.
name|Requests
operator|.
name|clusterHealthRequest
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|nodesInfoRequest
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
name|ImmutableSettings
operator|.
name|settingsBuilder
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
name|ElasticsearchIntegrationTest
operator|.
name|Scope
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
argument_list|)
DECL|class|SimpleNodesInfoTests
specifier|public
class|class
name|SimpleNodesInfoTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|SITE_PLUGIN
specifier|static
specifier|final
name|String
name|SITE_PLUGIN
init|=
literal|"dummy"
decl_stmt|;
DECL|field|SITE_PLUGIN_DESCRIPTION
specifier|static
specifier|final
name|String
name|SITE_PLUGIN_DESCRIPTION
init|=
literal|"This is a description for a dummy test site plugin."
decl_stmt|;
DECL|field|SITE_PLUGIN_VERSION
specifier|static
specifier|final
name|String
name|SITE_PLUGIN_VERSION
init|=
literal|"0.0.7-BOND-SITE"
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesInfos
specifier|public
name|void
name|testNodesInfos
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodesIds
init|=
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|String
name|node_1
init|=
name|nodesIds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|String
name|node_2
init|=
name|nodesIds
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ClusterHealthResponse
name|clusterHealth
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
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> done cluster_health, status "
operator|+
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|server1NodeId
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|,
name|node_1
argument_list|)
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
decl_stmt|;
name|String
name|server2NodeId
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|,
name|node_2
argument_list|)
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> started nodes: "
operator|+
name|server1NodeId
operator|+
literal|" and "
operator|+
name|server2NodeId
argument_list|)
expr_stmt|;
name|NodesInfoResponse
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
name|prepareNodesInfo
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server1NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server2NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server1NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server2NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|(
name|server1NodeId
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server1NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|(
name|server1NodeId
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server1NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|(
name|server2NodeId
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server2NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|(
name|server2NodeId
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|server2NodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Use case is to start 4 nodes:      *<ul>      *<li>1 : no plugin</li>      *<li>2 : one site plugin (with a es-plugin.properties file)</li>      *<li>3 : one java plugin</li>      *<li>4 : one site plugin and 2 java plugins (included the previous one)</li>      *</ul>      * We test here that NodeInfo API with plugin option give us the right results.      * @throws URISyntaxException      */
annotation|@
name|Test
DECL|method|testNodeInfoPlugin
specifier|public
name|void
name|testNodeInfoPlugin
parameter_list|()
throws|throws
name|URISyntaxException
block|{
comment|// We start four nodes
comment|// The first has no plugin
name|String
name|server1NodeId
init|=
name|startNodeWithPlugins
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// The second has one site plugin with a es-plugin.properties file (description and version)
name|String
name|server2NodeId
init|=
name|startNodeWithPlugins
argument_list|(
literal|2
argument_list|)
decl_stmt|;
comment|// The third has one java plugin
name|String
name|server3NodeId
init|=
name|startNodeWithPlugins
argument_list|(
literal|3
argument_list|,
name|TestPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|// The fourth has one java plugin and one site plugin
name|String
name|server4NodeId
init|=
name|startNodeWithPlugins
argument_list|(
literal|4
argument_list|,
name|TestNoVersionPlugin
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterHealthResponse
name|clusterHealth
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
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForNodes
argument_list|(
literal|"4"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> done cluster_health, status "
operator|+
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|NodesInfoResponse
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
name|prepareNodesInfo
argument_list|()
operator|.
name|clear
argument_list|()
operator|.
name|setPlugins
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> full json answer, status "
operator|+
name|response
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ElasticsearchAssertions
operator|.
name|assertNodeContainsPlugins
argument_list|(
name|response
argument_list|,
name|server1NodeId
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
comment|// No JVM Plugin
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
expr_stmt|;
comment|// No Site Plugin
name|ElasticsearchAssertions
operator|.
name|assertNodeContainsPlugins
argument_list|(
name|response
argument_list|,
name|server2NodeId
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
comment|// No JVM Plugin
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN
argument_list|)
argument_list|,
comment|// Site Plugin
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN_DESCRIPTION
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN_VERSION
argument_list|)
argument_list|)
expr_stmt|;
name|ElasticsearchAssertions
operator|.
name|assertNodeContainsPlugins
argument_list|(
name|response
argument_list|,
name|server3NodeId
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|TestPlugin
operator|.
name|Fields
operator|.
name|NAME
argument_list|)
argument_list|,
comment|// JVM Plugin
name|Lists
operator|.
name|newArrayList
argument_list|(
name|TestPlugin
operator|.
name|Fields
operator|.
name|DESCRIPTION
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|PluginInfo
operator|.
name|VERSION_NOT_AVAILABLE
argument_list|)
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
expr_stmt|;
comment|// No site Plugin
name|ElasticsearchAssertions
operator|.
name|assertNodeContainsPlugins
argument_list|(
name|response
argument_list|,
name|server4NodeId
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|TestNoVersionPlugin
operator|.
name|Fields
operator|.
name|NAME
argument_list|)
argument_list|,
comment|// JVM Plugin
name|Lists
operator|.
name|newArrayList
argument_list|(
name|TestNoVersionPlugin
operator|.
name|Fields
operator|.
name|DESCRIPTION
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|PluginInfo
operator|.
name|VERSION_NOT_AVAILABLE
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN
argument_list|,
name|TestNoVersionPlugin
operator|.
name|Fields
operator|.
name|NAME
argument_list|)
argument_list|,
comment|// Site Plugin
name|Lists
operator|.
name|newArrayList
argument_list|(
name|PluginInfo
operator|.
name|DESCRIPTION_NOT_AVAILABLE
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|PluginInfo
operator|.
name|VERSION_NOT_AVAILABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|startNodeWithPlugins
specifier|public
specifier|static
name|String
name|startNodeWithPlugins
parameter_list|(
name|int
name|nodeId
parameter_list|,
name|String
modifier|...
name|pluginClassNames
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
name|startNodeWithPlugins
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|,
literal|"/org/elasticsearch/nodesinfo/node"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|nodeId
argument_list|)
operator|+
literal|"/"
argument_list|,
name|pluginClassNames
argument_list|)
return|;
block|}
DECL|method|startNodeWithPlugins
specifier|public
specifier|static
name|String
name|startNodeWithPlugins
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|,
name|String
name|pluginDir
parameter_list|,
name|String
modifier|...
name|pluginClassNames
parameter_list|)
throws|throws
name|URISyntaxException
block|{
name|URL
name|resource
init|=
name|SimpleNodesInfoTests
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|pluginDir
argument_list|)
decl_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|settings
init|=
name|settingsBuilder
argument_list|()
decl_stmt|;
name|settings
operator|.
name|put
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|settings
operator|.
name|put
argument_list|(
literal|"path.plugins"
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|resource
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pluginClassNames
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|settings
operator|.
name|putArray
argument_list|(
literal|"plugin.types"
argument_list|,
name|pluginClassNames
argument_list|)
expr_stmt|;
block|}
name|String
name|nodeName
init|=
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
decl_stmt|;
comment|// We wait for a Green status
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
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
return|return
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|,
name|nodeName
argument_list|)
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
return|;
block|}
block|}
end_class

end_unit

