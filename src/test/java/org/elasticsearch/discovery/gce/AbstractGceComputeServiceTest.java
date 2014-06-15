begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|gce
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
name|plugins
operator|.
name|PluginsService
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
name|CoreMatchers
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
name|is
import|;
end_import

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
literal|2
argument_list|,
name|transportClientRatio
operator|=
literal|0.0
argument_list|)
DECL|class|AbstractGceComputeServiceTest
specifier|public
specifier|abstract
class|class
name|AbstractGceComputeServiceTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
comment|/**      * Set the number of expected nodes in the current cluster      */
DECL|method|getExpectedNodes
specifier|protected
specifier|abstract
name|int
name|getExpectedNodes
parameter_list|()
function_decl|;
DECL|method|getPort
specifier|public
specifier|static
name|int
name|getPort
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
try|try
block|{
return|return
name|PropertiesHelper
operator|.
name|getAsInt
argument_list|(
literal|"plugin.port"
argument_list|)
operator|+
name|nodeOrdinal
operator|*
literal|10
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
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
literal|"transport.tcp.port"
argument_list|,
name|getPort
argument_list|(
name|nodeOrdinal
argument_list|)
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
literal|"plugins."
operator|+
name|PluginsService
operator|.
name|LOAD_PLUGIN_FROM_CLASSPATH
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testExpectedNumberOfNodes
specifier|public
name|void
name|testExpectedNumberOfNodes
parameter_list|()
block|{
name|NodesInfoResponse
name|nodeInfos
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
name|nodeInfos
operator|.
name|getNodes
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|nodeInfos
operator|.
name|getNodes
argument_list|()
operator|.
name|length
argument_list|,
name|is
argument_list|(
name|getExpectedNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

