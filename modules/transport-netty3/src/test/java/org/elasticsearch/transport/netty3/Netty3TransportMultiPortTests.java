begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.netty3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty3
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|Lifecycle
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
name|network
operator|.
name|NetworkUtils
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
name|common
operator|.
name|util
operator|.
name|MockBigArrays
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
name|elasticsearch
operator|.
name|transport
operator|.
name|TcpTransport
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
name|TransportSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
DECL|class|Netty3TransportMultiPortTests
specifier|public
class|class
name|Netty3TransportMultiPortTests
extends|extends
name|ESTestCase
block|{
DECL|field|host
specifier|private
name|String
name|host
decl_stmt|;
annotation|@
name|Before
DECL|method|setup
specifier|public
name|void
name|setup
parameter_list|()
block|{
if|if
condition|(
name|NetworkUtils
operator|.
name|SUPPORTS_V6
operator|&&
name|randomBoolean
argument_list|()
condition|)
block|{
name|host
operator|=
literal|"::1"
expr_stmt|;
block|}
else|else
block|{
name|host
operator|=
literal|"127.0.0.1"
expr_stmt|;
block|}
block|}
DECL|method|testThatNettyCanBindToMultiplePorts
specifier|public
name|void
name|testThatNettyCanBindToMultiplePorts
parameter_list|()
throws|throws
name|Exception
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
literal|"network.host"
argument_list|,
name|host
argument_list|)
operator|.
name|put
argument_list|(
name|TransportSettings
operator|.
name|PORT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|22
argument_list|)
comment|// will not actually bind to this
operator|.
name|put
argument_list|(
literal|"transport.profiles.default.port"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.profiles.client1.port"
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
literal|"tst"
argument_list|)
decl_stmt|;
try|try
init|(
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|transport
init|=
name|startTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|profileBoundAddresses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
operator|.
name|boundAddresses
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatDefaultProfileInheritsFromStandardSettings
specifier|public
name|void
name|testThatDefaultProfileInheritsFromStandardSettings
parameter_list|()
throws|throws
name|Exception
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
literal|"network.host"
argument_list|,
name|host
argument_list|)
operator|.
name|put
argument_list|(
name|TransportSettings
operator|.
name|PORT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.profiles.client1.port"
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
literal|"tst"
argument_list|)
decl_stmt|;
try|try
init|(
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|transport
init|=
name|startTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|profileBoundAddresses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
operator|.
name|boundAddresses
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatProfileWithoutPortSettingsFails
specifier|public
name|void
name|testThatProfileWithoutPortSettingsFails
parameter_list|()
throws|throws
name|Exception
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
literal|"network.host"
argument_list|,
name|host
argument_list|)
operator|.
name|put
argument_list|(
name|TransportSettings
operator|.
name|PORT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.profiles.client1.whatever"
argument_list|,
literal|"foo"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
literal|"tst"
argument_list|)
decl_stmt|;
try|try
init|(
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|transport
init|=
name|startTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|transport
operator|.
name|profileBoundAddresses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
operator|.
name|boundAddresses
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatDefaultProfilePortOverridesGeneralConfiguration
specifier|public
name|void
name|testThatDefaultProfilePortOverridesGeneralConfiguration
parameter_list|()
throws|throws
name|Exception
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
literal|"network.host"
argument_list|,
name|host
argument_list|)
operator|.
name|put
argument_list|(
name|TransportSettings
operator|.
name|PORT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|22
argument_list|)
comment|// will not actually bind to this
operator|.
name|put
argument_list|(
literal|"transport.profiles.default.port"
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
literal|"tst"
argument_list|)
decl_stmt|;
try|try
init|(
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|transport
init|=
name|startTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|transport
operator|.
name|profileBoundAddresses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
operator|.
name|boundAddresses
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testThatProfileWithoutValidNameIsIgnored
specifier|public
name|void
name|testThatProfileWithoutValidNameIsIgnored
parameter_list|()
throws|throws
name|Exception
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
literal|"network.host"
argument_list|,
name|host
argument_list|)
operator|.
name|put
argument_list|(
name|TransportSettings
operator|.
name|PORT
operator|.
name|getKey
argument_list|()
argument_list|,
literal|0
argument_list|)
comment|// mimics someone trying to define a profile for .local which is the profile for a node request to itself
operator|.
name|put
argument_list|(
literal|"transport.profiles."
operator|+
name|TransportService
operator|.
name|DIRECT_RESPONSE_PROFILE
operator|+
literal|".port"
argument_list|,
literal|22
argument_list|)
comment|// will not actually bind to this
operator|.
name|put
argument_list|(
literal|"transport.profiles..port"
argument_list|,
literal|23
argument_list|)
comment|// will not actually bind to this
operator|.
name|build
argument_list|()
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
literal|"tst"
argument_list|)
decl_stmt|;
try|try
init|(
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|transport
init|=
name|startTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
init|)
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|transport
operator|.
name|profileBoundAddresses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|transport
operator|.
name|boundAddress
argument_list|()
operator|.
name|boundAddresses
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|startTransport
specifier|private
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|startTransport
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|BigArrays
name|bigArrays
init|=
operator|new
name|MockBigArrays
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|NoneCircuitBreakerService
argument_list|()
argument_list|)
decl_stmt|;
name|TcpTransport
argument_list|<
name|?
argument_list|>
name|transport
init|=
operator|new
name|Netty3Transport
argument_list|(
name|settings
argument_list|,
name|threadPool
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
argument_list|,
name|bigArrays
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
name|NoneCircuitBreakerService
argument_list|()
argument_list|)
decl_stmt|;
name|transport
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|transport
operator|.
name|lifecycleState
argument_list|()
argument_list|,
name|is
argument_list|(
name|Lifecycle
operator|.
name|State
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|transport
return|;
block|}
block|}
end_class

end_unit

