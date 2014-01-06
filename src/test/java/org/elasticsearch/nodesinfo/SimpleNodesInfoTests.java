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
name|base
operator|.
name|Function
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
import|;
end_import

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
name|FluentIterable
import|;
end_import

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
name|Iterables
import|;
end_import

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
name|PluginsInfo
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
name|ElasticsearchIntegrationTest
operator|.
name|Scope
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
name|File
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|and
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicates
operator|.
name|isNull
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
name|numNodes
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
DECL|field|SITE_PLUGIN_NO_DESCRIPTION
specifier|static
specifier|final
name|String
name|SITE_PLUGIN_NO_DESCRIPTION
init|=
literal|"No description found for dummy."
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodesInfos
specifier|public
name|void
name|testNodesInfos
parameter_list|()
block|{
specifier|final
name|String
name|node_1
init|=
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
decl_stmt|;
specifier|final
name|String
name|node_2
init|=
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
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
name|waitForGreenStatus
argument_list|()
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
name|String
name|server1NodeId
init|=
name|cluster
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
name|cluster
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
name|waitForGreenStatus
argument_list|()
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
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
expr_stmt|;
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
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN
argument_list|)
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN_DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
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
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
expr_stmt|;
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
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Fields
operator|.
name|SITE_PLUGIN_NO_DESCRIPTION
argument_list|,
name|TestNoVersionPlugin
operator|.
name|Fields
operator|.
name|DESCRIPTION
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNodeContainsPlugins
specifier|private
name|void
name|assertNodeContainsPlugins
parameter_list|(
name|NodesInfoResponse
name|response
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedJvmPluginNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedJvmPluginDescriptions
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedSitePluginNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|expectedSitePluginDescriptions
parameter_list|)
block|{
name|assertThat
argument_list|(
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|PluginsInfo
name|plugins
init|=
name|response
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|getPlugins
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|plugins
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|pluginNames
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|plugins
operator|.
name|getInfos
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|jvmPluginPredicate
argument_list|)
operator|.
name|transform
argument_list|(
name|nameFunction
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|expectedJvmPluginName
range|:
name|expectedJvmPluginNames
control|)
block|{
name|assertThat
argument_list|(
name|pluginNames
argument_list|,
name|hasItem
argument_list|(
name|expectedJvmPluginName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|pluginDescriptions
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|plugins
operator|.
name|getInfos
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|jvmPluginPredicate
argument_list|)
operator|.
name|transform
argument_list|(
name|descriptionFunction
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|expectedJvmPluginDescription
range|:
name|expectedJvmPluginDescriptions
control|)
block|{
name|assertThat
argument_list|(
name|pluginDescriptions
argument_list|,
name|hasItem
argument_list|(
name|expectedJvmPluginDescription
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FluentIterable
argument_list|<
name|String
argument_list|>
name|jvmUrls
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|plugins
operator|.
name|getInfos
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|and
argument_list|(
name|jvmPluginPredicate
argument_list|,
name|Predicates
operator|.
name|not
argument_list|(
name|sitePluginPredicate
argument_list|)
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|isNull
argument_list|()
argument_list|)
operator|.
name|transform
argument_list|(
name|urlFunction
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Iterables
operator|.
name|size
argument_list|(
name|jvmUrls
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|sitePluginNames
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|plugins
operator|.
name|getInfos
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|sitePluginPredicate
argument_list|)
operator|.
name|transform
argument_list|(
name|nameFunction
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|expectedSitePluginName
range|:
name|expectedSitePluginNames
control|)
block|{
name|assertThat
argument_list|(
name|sitePluginNames
argument_list|,
name|hasItem
argument_list|(
name|expectedSitePluginName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|sitePluginDescriptions
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|plugins
operator|.
name|getInfos
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|sitePluginPredicate
argument_list|)
operator|.
name|transform
argument_list|(
name|descriptionFunction
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|sitePluginDescription
range|:
name|expectedSitePluginDescriptions
control|)
block|{
name|assertThat
argument_list|(
name|sitePluginDescriptions
argument_list|,
name|hasItem
argument_list|(
name|sitePluginDescription
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|sitePluginUrls
init|=
name|FluentIterable
operator|.
name|from
argument_list|(
name|plugins
operator|.
name|getInfos
argument_list|()
argument_list|)
operator|.
name|filter
argument_list|(
name|sitePluginPredicate
argument_list|)
operator|.
name|transform
argument_list|(
name|urlFunction
argument_list|)
operator|.
name|toList
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|sitePluginUrls
argument_list|,
name|not
argument_list|(
name|contains
argument_list|(
name|nullValue
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|startNodeWithPlugins
specifier|private
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
name|URL
name|resource
init|=
name|SimpleNodesInfoTests
operator|.
name|class
operator|.
name|getResource
argument_list|(
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
operator|new
name|File
argument_list|(
name|resource
operator|.
name|toURI
argument_list|()
argument_list|)
operator|.
name|getAbsolutePath
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
name|cluster
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
name|String
name|serverNodeId
init|=
name|cluster
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
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"--> server {} started"
operator|+
name|serverNodeId
argument_list|)
expr_stmt|;
return|return
name|serverNodeId
return|;
block|}
DECL|field|jvmPluginPredicate
specifier|private
name|Predicate
argument_list|<
name|PluginInfo
argument_list|>
name|jvmPluginPredicate
init|=
operator|new
name|Predicate
argument_list|<
name|PluginInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|PluginInfo
name|pluginInfo
parameter_list|)
block|{
return|return
name|pluginInfo
operator|.
name|isJvm
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|sitePluginPredicate
specifier|private
name|Predicate
argument_list|<
name|PluginInfo
argument_list|>
name|sitePluginPredicate
init|=
operator|new
name|Predicate
argument_list|<
name|PluginInfo
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|PluginInfo
name|pluginInfo
parameter_list|)
block|{
return|return
name|pluginInfo
operator|.
name|isSite
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|nameFunction
specifier|private
name|Function
argument_list|<
name|PluginInfo
argument_list|,
name|String
argument_list|>
name|nameFunction
init|=
operator|new
name|Function
argument_list|<
name|PluginInfo
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|PluginInfo
name|pluginInfo
parameter_list|)
block|{
return|return
name|pluginInfo
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|descriptionFunction
specifier|private
name|Function
argument_list|<
name|PluginInfo
argument_list|,
name|String
argument_list|>
name|descriptionFunction
init|=
operator|new
name|Function
argument_list|<
name|PluginInfo
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|PluginInfo
name|pluginInfo
parameter_list|)
block|{
return|return
name|pluginInfo
operator|.
name|getDescription
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|urlFunction
specifier|private
name|Function
argument_list|<
name|PluginInfo
argument_list|,
name|String
argument_list|>
name|urlFunction
init|=
operator|new
name|Function
argument_list|<
name|PluginInfo
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|PluginInfo
name|pluginInfo
parameter_list|)
block|{
return|return
name|pluginInfo
operator|.
name|getUrl
argument_list|()
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

