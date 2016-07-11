begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|transport
package|;
end_package

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
name|network
operator|.
name|NetworkModule
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
name|transport
operator|.
name|TransportAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|internal
operator|.
name|InternalSettingsPreparer
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
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
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
name|greaterThanOrEqualTo
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|startsWith
import|;
end_import

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
literal|1.0
argument_list|)
DECL|class|TransportClientIT
specifier|public
class|class
name|TransportClientIT
extends|extends
name|ESIntegTestCase
block|{
DECL|method|testPickingUpChangesInDiscoveryNode
specifier|public
name|void
name|testPickingUpChangesInDiscoveryNode
parameter_list|()
block|{
name|String
name|nodeName
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
name|Node
operator|.
name|NODE_DATA_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|TransportClient
name|client
init|=
operator|(
name|TransportClient
operator|)
name|internalCluster
argument_list|()
operator|.
name|client
argument_list|(
name|nodeName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|connectedNodes
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isDataNode
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNodeVersionIsUpdated
specifier|public
name|void
name|testNodeVersionIsUpdated
parameter_list|()
throws|throws
name|IOException
block|{
name|TransportClient
name|client
init|=
operator|(
name|TransportClient
operator|)
name|internalCluster
argument_list|()
operator|.
name|client
argument_list|()
decl_stmt|;
try|try
init|(
name|Node
name|node
init|=
operator|new
name|Node
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getDefaultSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
literal|"testNodeVersionIsUpdated"
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|HTTP_ENABLED
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|Node
operator|.
name|NODE_DATA_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"foobar"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|start
argument_list|()
init|)
block|{
name|TransportAddress
name|transportAddress
init|=
name|node
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|client
operator|.
name|addTransportAddress
argument_list|(
name|transportAddress
argument_list|)
expr_stmt|;
comment|// since we force transport clients there has to be one node started that we connect to.
name|assertThat
argument_list|(
name|client
operator|.
name|connectedNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// connected nodes have updated version
for|for
control|(
name|DiscoveryNode
name|discoveryNode
range|:
name|client
operator|.
name|connectedNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|discoveryNode
operator|.
name|getVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DiscoveryNode
name|discoveryNode
range|:
name|client
operator|.
name|listedNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|discoveryNode
operator|.
name|getId
argument_list|()
argument_list|,
name|startsWith
argument_list|(
literal|"#transport#-"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|discoveryNode
operator|.
name|getVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|client
operator|.
name|filteredNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|discoveryNode
range|:
name|client
operator|.
name|filteredNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|discoveryNode
operator|.
name|getVersion
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testThatTransportClientSettingIsSet
specifier|public
name|void
name|testThatTransportClientSettingIsSet
parameter_list|()
block|{
name|TransportClient
name|client
init|=
operator|(
name|TransportClient
operator|)
name|internalCluster
argument_list|()
operator|.
name|client
argument_list|()
decl_stmt|;
name|Settings
name|settings
init|=
name|client
operator|.
name|injector
operator|.
name|getInstance
argument_list|(
name|Settings
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Client
operator|.
name|CLIENT_TYPE_SETTING_S
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"transport"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatTransportClientSettingCannotBeChanged
specifier|public
name|void
name|testThatTransportClientSettingCannotBeChanged
parameter_list|()
block|{
name|Settings
name|baseSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Environment
operator|.
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
init|(
name|TransportClient
name|client
init|=
name|TransportClient
operator|.
name|builder
argument_list|()
operator|.
name|settings
argument_list|(
name|baseSettings
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|Settings
name|settings
init|=
name|client
operator|.
name|injector
operator|.
name|getInstance
argument_list|(
name|Settings
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Client
operator|.
name|CLIENT_TYPE_SETTING_S
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"transport"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

