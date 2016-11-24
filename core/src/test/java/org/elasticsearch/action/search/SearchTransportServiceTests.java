begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|SearchTransportServiceTests
specifier|public
class|class
name|SearchTransportServiceTests
extends|extends
name|ESTestCase
block|{
DECL|method|testRemoteClusterSeedSetting
specifier|public
name|void
name|testRemoteClusterSeedSetting
parameter_list|()
block|{
comment|// simple validation
name|SearchTransportService
operator|.
name|REMOTE_CLUSTERS_SEEDS
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"action.search.remote.foo"
argument_list|,
literal|"192.168.0.1:8080"
argument_list|)
operator|.
name|put
argument_list|(
literal|"action.search.remote.bar"
argument_list|,
literal|"[::1]:9090"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|SearchTransportService
operator|.
name|REMOTE_CLUSTERS_SEEDS
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"action.search.remote.foo"
argument_list|,
literal|"192.168.0.1"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuiltRemoteClustersSeeds
specifier|public
name|void
name|testBuiltRemoteClustersSeeds
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
argument_list|>
name|map
init|=
name|SearchTransportService
operator|.
name|buildRemoteClustersSeeds
argument_list|(
name|SearchTransportService
operator|.
name|REMOTE_CLUSTERS_SEEDS
operator|.
name|get
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"action.search.remote.foo"
argument_list|,
literal|"192.168.0.1:8080"
argument_list|)
operator|.
name|put
argument_list|(
literal|"action.search.remote.bar"
argument_list|,
literal|"[::1]:9090"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|foo
init|=
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|foo
operator|.
name|getAddress
argument_list|()
argument_list|,
operator|new
name|TransportAddress
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"192.168.0.1"
argument_list|)
argument_list|,
literal|8080
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|foo
operator|.
name|getId
argument_list|()
argument_list|,
literal|"foo#192.168.0.1:8080"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|foo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|bar
init|=
name|map
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bar
operator|.
name|getAddress
argument_list|()
argument_list|,
operator|new
name|TransportAddress
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"[::1]"
argument_list|)
argument_list|,
literal|9090
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bar
operator|.
name|getId
argument_list|()
argument_list|,
literal|"bar#[::1]:9090"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bar
operator|.
name|getVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

