begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
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
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|indices
operator|.
name|breaker
operator|.
name|NoneCircuitBreakerService
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
name|test
operator|.
name|VersionUtils
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
name|transport
operator|.
name|MockTransportService
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
name|TestThreadPool
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
name|junit
operator|.
name|After
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
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
name|containsString
import|;
end_import

begin_class
DECL|class|TransportServiceHandshakeTests
specifier|public
class|class
name|TransportServiceHandshakeTests
extends|extends
name|ESTestCase
block|{
DECL|field|threadPool
specifier|private
specifier|static
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|timeout
specifier|private
specifier|static
specifier|final
name|long
name|timeout
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startThreadPool
specifier|public
specifier|static
name|void
name|startThreadPool
parameter_list|()
block|{
name|threadPool
operator|=
operator|new
name|TestThreadPool
argument_list|(
name|TransportServiceHandshakeTests
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|transportServices
specifier|private
name|List
argument_list|<
name|TransportService
argument_list|>
name|transportServices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|startServices
specifier|private
name|NetworkHandle
name|startServices
parameter_list|(
name|String
name|nodeNameAndId
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|MockTcpTransport
name|transport
init|=
operator|new
name|MockTcpTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
operator|new
name|NoneCircuitBreakerService
argument_list|()
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|,
operator|new
name|NetworkService
argument_list|(
name|settings
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|TransportService
name|transportService
init|=
operator|new
name|MockTransportService
argument_list|(
name|settings
argument_list|,
name|transport
argument_list|,
name|threadPool
argument_list|,
name|TransportService
operator|.
name|NOOP_TRANSPORT_INTERCEPTOR
argument_list|,
parameter_list|(
name|boundAddress
parameter_list|)
lambda|->
operator|new
name|DiscoveryNode
argument_list|(
name|nodeNameAndId
argument_list|,
name|nodeNameAndId
argument_list|,
name|boundAddress
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|version
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|transportService
operator|.
name|start
argument_list|()
expr_stmt|;
name|transportService
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
name|transportServices
operator|.
name|add
argument_list|(
name|transportService
argument_list|)
expr_stmt|;
return|return
operator|new
name|NetworkHandle
argument_list|(
name|transportService
argument_list|,
name|transportService
operator|.
name|getLocalNode
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|TransportService
name|transportService
range|:
name|transportServices
control|)
block|{
name|transportService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|terminateThreadPool
specifier|public
specifier|static
name|void
name|terminateThreadPool
parameter_list|()
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|threadPool
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
comment|// since static must set to null to be eligible for collection
name|threadPool
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testConnectToNodeLight
specifier|public
name|void
name|testConnectToNodeLight
parameter_list|()
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkHandle
name|handleA
init|=
name|startServices
argument_list|(
literal|"TS_A"
argument_list|,
name|settings
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|NetworkHandle
name|handleB
init|=
name|startServices
argument_list|(
literal|"TS_B"
argument_list|,
name|settings
argument_list|,
name|VersionUtils
operator|.
name|randomVersionBetween
argument_list|(
name|random
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|discoveryNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|""
argument_list|,
name|handleB
operator|.
name|discoveryNode
operator|.
name|getAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Transport
operator|.
name|Connection
name|connection
init|=
name|handleA
operator|.
name|transportService
operator|.
name|openConnection
argument_list|(
name|discoveryNode
argument_list|,
name|MockTcpTransport
operator|.
name|LIGHT_PROFILE
argument_list|)
init|)
block|{
name|DiscoveryNode
name|connectedNode
init|=
name|handleA
operator|.
name|transportService
operator|.
name|handshake
argument_list|(
name|connection
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|connectedNode
argument_list|)
expr_stmt|;
comment|// the name and version should be updated
name|assertEquals
argument_list|(
name|connectedNode
operator|.
name|getName
argument_list|()
argument_list|,
literal|"TS_B"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|connectedNode
operator|.
name|getVersion
argument_list|()
argument_list|,
name|handleB
operator|.
name|discoveryNode
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handleA
operator|.
name|transportService
operator|.
name|nodeConnected
argument_list|(
name|discoveryNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMismatchedClusterName
specifier|public
name|void
name|testMismatchedClusterName
parameter_list|()
block|{
name|NetworkHandle
name|handleA
init|=
name|startServices
argument_list|(
literal|"TS_A"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"a"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|NetworkHandle
name|handleB
init|=
name|startServices
argument_list|(
literal|"TS_B"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|discoveryNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|""
argument_list|,
name|handleB
operator|.
name|discoveryNode
operator|.
name|getAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
decl_stmt|;
name|IllegalStateException
name|ex
init|=
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
init|(
name|Transport
operator|.
name|Connection
name|connection
init|=
name|handleA
operator|.
name|transportService
operator|.
name|openConnection
argument_list|(
name|discoveryNode
argument_list|,
name|MockTcpTransport
operator|.
name|LIGHT_PROFILE
argument_list|)
init|)
block|{
name|handleA
operator|.
name|transportService
operator|.
name|handshake
argument_list|(
name|connection
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"handshake failed, mismatched cluster name [Cluster [b]]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handleA
operator|.
name|transportService
operator|.
name|nodeConnected
argument_list|(
name|discoveryNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncompatibleVersions
specifier|public
name|void
name|testIncompatibleVersions
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|NetworkHandle
name|handleA
init|=
name|startServices
argument_list|(
literal|"TS_A"
argument_list|,
name|settings
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|NetworkHandle
name|handleB
init|=
name|startServices
argument_list|(
literal|"TS_B"
argument_list|,
name|settings
argument_list|,
name|VersionUtils
operator|.
name|getPreviousVersion
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|discoveryNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|""
argument_list|,
name|handleB
operator|.
name|discoveryNode
operator|.
name|getAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
decl_stmt|;
name|IllegalStateException
name|ex
init|=
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
init|(
name|Transport
operator|.
name|Connection
name|connection
init|=
name|handleA
operator|.
name|transportService
operator|.
name|openConnection
argument_list|(
name|discoveryNode
argument_list|,
name|MockTcpTransport
operator|.
name|LIGHT_PROFILE
argument_list|)
init|)
block|{
name|handleA
operator|.
name|transportService
operator|.
name|handshake
argument_list|(
name|connection
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"handshake failed, incompatible version"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|handleA
operator|.
name|transportService
operator|.
name|nodeConnected
argument_list|(
name|discoveryNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|NetworkHandle
specifier|private
specifier|static
class|class
name|NetworkHandle
block|{
DECL|field|transportService
specifier|private
name|TransportService
name|transportService
decl_stmt|;
DECL|field|discoveryNode
specifier|private
name|DiscoveryNode
name|discoveryNode
decl_stmt|;
DECL|method|NetworkHandle
name|NetworkHandle
parameter_list|(
name|TransportService
name|transportService
parameter_list|,
name|DiscoveryNode
name|discoveryNode
parameter_list|)
block|{
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|discoveryNode
operator|=
name|discoveryNode
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

