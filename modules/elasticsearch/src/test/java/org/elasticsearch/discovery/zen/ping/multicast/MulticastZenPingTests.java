begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.ping.multicast
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
name|multicast
package|;
end_package

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
name|discovery
operator|.
name|zen
operator|.
name|DiscoveryNodesProvider
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
name|threadpool
operator|.
name|cached
operator|.
name|CachedThreadPool
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
name|local
operator|.
name|LocalTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
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
name|MatcherAssert
operator|.
name|*
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|MulticastZenPingTests
specifier|public
class|class
name|MulticastZenPingTests
block|{
DECL|method|testSimplePings
annotation|@
name|Test
specifier|public
name|void
name|testSimplePings
parameter_list|()
block|{
name|ThreadPool
name|threadPool
init|=
operator|new
name|CachedThreadPool
argument_list|()
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
specifier|final
name|TransportService
name|transportServiceA
init|=
operator|new
name|TransportService
argument_list|(
operator|new
name|LocalTransport
argument_list|(
name|threadPool
argument_list|)
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
literal|"A"
argument_list|,
name|transportServiceA
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TransportService
name|transportServiceB
init|=
operator|new
name|TransportService
argument_list|(
operator|new
name|LocalTransport
argument_list|(
name|threadPool
argument_list|)
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
literal|"B"
argument_list|,
name|transportServiceA
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|)
decl_stmt|;
name|MulticastZenPing
name|zenPingA
init|=
operator|new
name|MulticastZenPing
argument_list|(
name|threadPool
argument_list|,
name|transportServiceA
argument_list|,
name|clusterName
argument_list|)
decl_stmt|;
name|zenPingA
operator|.
name|setNodesProvider
argument_list|(
operator|new
name|DiscoveryNodesProvider
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
name|newNodesBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|nodeA
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"A"
argument_list|)
operator|.
name|build
argument_list|()
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
name|MulticastZenPing
name|zenPingB
init|=
operator|new
name|MulticastZenPing
argument_list|(
name|threadPool
argument_list|,
name|transportServiceB
argument_list|,
name|clusterName
argument_list|)
decl_stmt|;
name|zenPingB
operator|.
name|setNodesProvider
argument_list|(
operator|new
name|DiscoveryNodesProvider
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
name|newNodesBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|nodeB
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"B"
argument_list|)
operator|.
name|build
argument_list|()
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
literal|1
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
name|target
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"B"
argument_list|)
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
name|threadPool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

