begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bwcompat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bwcompat
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
name|test
operator|.
name|ElasticsearchBackwardsCompatIntegrationTest
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
name|equalTo
import|;
end_import

begin_class
DECL|class|UnicastBackwardsCompatibilityTest
specifier|public
class|class
name|UnicastBackwardsCompatibilityTest
extends|extends
name|ElasticsearchBackwardsCompatIntegrationTest
block|{
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
literal|9380
operator|+
name|nodeOrdinal
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.ping.multicast.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.ping.unicast.hosts"
argument_list|,
literal|"localhost:9380,localhost:9381,localhost:9390,localhost:9391"
argument_list|)
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
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|externalNodeSettings
specifier|protected
name|Settings
name|externalNodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"transport.tcp.port"
argument_list|,
literal|9390
operator|+
name|nodeOrdinal
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.ping.multicast.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.ping.unicast.hosts"
argument_list|,
literal|"localhost:9380,localhost:9381,localhost:9390,localhost:9391"
argument_list|)
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
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testUnicastDiscovery
specifier|public
name|void
name|testUnicastDiscovery
parameter_list|()
block|{
name|ClusterHealthResponse
name|healthResponse
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
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|healthResponse
operator|.
name|getNumberOfDataNodes
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|cluster
argument_list|()
operator|.
name|numDataNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

