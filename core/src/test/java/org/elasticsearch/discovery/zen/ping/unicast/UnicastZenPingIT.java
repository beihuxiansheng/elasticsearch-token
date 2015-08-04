begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.ping.unicast
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|unicast
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
name|ClusterName
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNodes
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
name|NetworkService
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
name|InetSocketTransportAddress
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
name|unit
operator|.
name|TimeValue
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
name|util
operator|.
name|BigArrays
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
name|zen
operator|.
name|elect
operator|.
name|ElectMasterService
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
name|zen
operator|.
name|ping
operator|.
name|PingContextProvider
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
name|zen
operator|.
name|ping
operator|.
name|ZenPing
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
name|service
operator|.
name|NodeService
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
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
operator|.
name|NettyTransport
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
DECL|class|UnicastZenPingIT
specifier|public
class|class
name|UnicastZenPingIT
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testSimplePings
specifier|public
name|void
name|testSimplePings
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
name|int
name|startPort
init|=
literal|11000
operator|+
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|1000
argument_list|)
decl_stmt|;
name|int
name|endPort
init|=
name|startPort
operator|+
literal|10
decl_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.tcp.port"
argument_list|,
name|startPort
operator|+
literal|"-"
operator|+
name|endPort
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|ThreadPool
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterName
name|clusterName
init|=
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|ElectMasterService
name|electMasterService
init|=
operator|new
name|ElectMasterService
argument_list|(
name|settings
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|NettyTransport
name|transportA
init|=
operator|new
name|NettyTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|networkService
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
specifier|final
name|TransportService
name|transportServiceA
init|=
operator|new
name|TransportService
argument_list|(
name|transportA
argument_list|,
name|threadPool
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNode
name|nodeA
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"UZP_A"
argument_list|,
name|transportServiceA
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|InetSocketTransportAddress
name|addressA
init|=
operator|(
name|InetSocketTransportAddress
operator|)
name|transportA
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|NettyTransport
name|transportB
init|=
operator|new
name|NettyTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|networkService
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
specifier|final
name|TransportService
name|transportServiceB
init|=
operator|new
name|TransportService
argument_list|(
name|transportB
argument_list|,
name|threadPool
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNode
name|nodeB
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"UZP_B"
argument_list|,
name|transportServiceA
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|InetSocketTransportAddress
name|addressB
init|=
operator|(
name|InetSocketTransportAddress
operator|)
name|transportB
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|Settings
name|hostsSettings
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|putArray
argument_list|(
literal|"discovery.zen.ping.unicast.hosts"
argument_list|,
name|addressA
operator|.
name|address
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|":"
operator|+
name|addressA
operator|.
name|address
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|,
name|addressB
operator|.
name|address
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|+
literal|":"
operator|+
name|addressB
operator|.
name|address
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|UnicastZenPing
name|zenPingA
init|=
operator|new
name|UnicastZenPing
argument_list|(
name|hostsSettings
argument_list|,
name|threadPool
argument_list|,
name|transportServiceA
argument_list|,
name|clusterName
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|electMasterService
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|zenPingA
operator|.
name|setPingContextProvider
argument_list|(
operator|new
name|PingContextProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DiscoveryNodes
name|nodes
parameter_list|()
block|{
return|return
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|nodeA
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"UZP_A"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeService
name|nodeService
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeHasJoinedClusterOnce
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zenPingA
operator|.
name|start
argument_list|()
expr_stmt|;
name|UnicastZenPing
name|zenPingB
init|=
operator|new
name|UnicastZenPing
argument_list|(
name|hostsSettings
argument_list|,
name|threadPool
argument_list|,
name|transportServiceB
argument_list|,
name|clusterName
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|electMasterService
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|zenPingB
operator|.
name|setPingContextProvider
argument_list|(
operator|new
name|PingContextProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DiscoveryNodes
name|nodes
parameter_list|()
block|{
return|return
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|nodeB
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"UZP_B"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeService
name|nodeService
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|nodeHasJoinedClusterOnce
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|zenPingB
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"ping from UZP_A"
argument_list|)
expr_stmt|;
name|ZenPing
operator|.
name|PingResponse
index|[]
name|pingResponses
init|=
name|zenPingA
operator|.
name|pingAndWait
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|pingResponses
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pingResponses
index|[
literal|0
index|]
operator|.
name|node
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"UZP_B"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pingResponses
index|[
literal|0
index|]
operator|.
name|hasJoinedOnce
argument_list|()
argument_list|)
expr_stmt|;
comment|// ping again, this time from B,
name|logger
operator|.
name|info
argument_list|(
literal|"ping from UZP_B"
argument_list|)
expr_stmt|;
name|pingResponses
operator|=
name|zenPingB
operator|.
name|pingAndWait
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pingResponses
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|pingResponses
index|[
literal|0
index|]
operator|.
name|node
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"UZP_A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pingResponses
index|[
literal|0
index|]
operator|.
name|hasJoinedOnce
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|zenPingA
operator|.
name|close
argument_list|()
expr_stmt|;
name|zenPingB
operator|.
name|close
argument_list|()
expr_stmt|;
name|transportServiceA
operator|.
name|close
argument_list|()
expr_stmt|;
name|transportServiceB
operator|.
name|close
argument_list|()
expr_stmt|;
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

