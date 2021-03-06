begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
operator|.
name|NodeClient
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
name|Table
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|inject
operator|.
name|ModuleTestCase
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
name|BoundTransportAddress
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
name|concurrent
operator|.
name|ThreadContext
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
name|xcontent
operator|.
name|NamedXContentRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpServerTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpStats
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|NullDispatcher
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
name|CircuitBreakerService
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
name|NetworkPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|BaseRestHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
operator|.
name|AbstractCatAction
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
name|Transport
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
name|TransportInterceptor
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
name|TransportRequest
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
name|TransportRequestHandler
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
name|Arrays
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
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_class
DECL|class|NetworkModuleTests
specifier|public
class|class
name|NetworkModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|field|threadPool
specifier|private
name|ThreadPool
name|threadPool
decl_stmt|;
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|threadPool
operator|=
operator|new
name|TestThreadPool
argument_list|(
name|NetworkModuleTests
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|threadPool
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
DECL|class|FakeHttpTransport
specifier|static
class|class
name|FakeHttpTransport
extends|extends
name|AbstractLifecycleComponent
implements|implements
name|HttpServerTransport
block|{
DECL|method|FakeHttpTransport
name|FakeHttpTransport
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{}
annotation|@
name|Override
DECL|method|boundAddress
specifier|public
name|BoundTransportAddress
name|boundAddress
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|info
specifier|public
name|HttpInfo
name|info
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|stats
specifier|public
name|HttpStats
name|stats
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|FakeRestHandler
specifier|static
class|class
name|FakeRestHandler
extends|extends
name|BaseRestHandler
block|{
DECL|method|FakeRestHandler
name|FakeRestHandler
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|prepareRequest
specifier|public
name|RestChannelConsumer
name|prepareRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|channel
lambda|->
block|{}
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"FakeRestHandler"
return|;
block|}
block|}
DECL|class|FakeCatRestHandler
specifier|static
class|class
name|FakeCatRestHandler
extends|extends
name|AbstractCatAction
block|{
DECL|method|FakeCatRestHandler
name|FakeCatRestHandler
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doCatRequest
specifier|protected
name|RestChannelConsumer
name|doCatRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|NodeClient
name|client
parameter_list|)
block|{
return|return
name|channel
lambda|->
block|{}
return|;
block|}
annotation|@
name|Override
DECL|method|documentation
specifier|protected
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|getTableWithHeader
specifier|protected
name|Table
name|getTableWithHeader
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"FakeCatRestHandler"
return|;
block|}
block|}
DECL|method|testRegisterTransport
specifier|public
name|void
name|testRegisterTransport
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
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
literal|"custom"
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
name|build
argument_list|()
decl_stmt|;
name|Supplier
argument_list|<
name|Transport
argument_list|>
name|custom
init|=
parameter_list|()
lambda|->
literal|null
decl_stmt|;
comment|// content doesn't matter we check reference equality
name|NetworkPlugin
name|plugin
init|=
operator|new
name|NetworkPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|Transport
argument_list|>
argument_list|>
name|getTransports
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"custom"
argument_list|,
name|custom
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|NetworkModule
name|module
init|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
name|plugin
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|module
operator|.
name|isTransportClient
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|module
operator|.
name|isHttpEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|custom
argument_list|,
name|module
operator|.
name|getTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
comment|// check it works with transport only as well
name|module
operator|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|true
argument_list|,
name|plugin
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|custom
argument_list|,
name|module
operator|.
name|getTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|module
operator|.
name|isTransportClient
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|module
operator|.
name|isHttpEnabled
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterHttpTransport
specifier|public
name|void
name|testRegisterHttpTransport
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
name|NetworkModule
operator|.
name|HTTP_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
name|custom
init|=
name|FakeHttpTransport
operator|::
operator|new
decl_stmt|;
name|NetworkModule
name|module
init|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
operator|new
name|NetworkPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
argument_list|>
name|getHttpTransports
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|NetworkService
name|networkService
parameter_list|,
name|HttpServerTransport
operator|.
name|Dispatcher
name|requestDispatcher
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"custom"
argument_list|,
name|custom
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|custom
argument_list|,
name|module
operator|.
name|getHttpServerTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|module
operator|.
name|isTransportClient
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|module
operator|.
name|isHttpEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
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
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|NetworkModule
name|newModule
init|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|newModule
operator|.
name|isTransportClient
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|newModule
operator|.
name|isHttpEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|newModule
operator|.
name|getHttpServerTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testOverrideDefault
specifier|public
name|void
name|testOverrideDefault
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
name|NetworkModule
operator|.
name|HTTP_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|HTTP_DEFAULT_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"default_custom"
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|TRANSPORT_DEFAULT_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"local"
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
literal|"default_custom"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Supplier
argument_list|<
name|Transport
argument_list|>
name|customTransport
init|=
parameter_list|()
lambda|->
literal|null
decl_stmt|;
comment|// content doesn't matter we check reference equality
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
name|custom
init|=
name|FakeHttpTransport
operator|::
operator|new
decl_stmt|;
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
name|def
init|=
name|FakeHttpTransport
operator|::
operator|new
decl_stmt|;
name|NetworkModule
name|module
init|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
operator|new
name|NetworkPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|Transport
argument_list|>
argument_list|>
name|getTransports
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"default_custom"
argument_list|,
name|customTransport
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
argument_list|>
name|getHttpTransports
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|NetworkService
name|networkService
parameter_list|,
name|HttpServerTransport
operator|.
name|Dispatcher
name|requestDispatcher
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
argument_list|>
name|supplierMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|supplierMap
operator|.
name|put
argument_list|(
literal|"custom"
argument_list|,
name|custom
argument_list|)
expr_stmt|;
name|supplierMap
operator|.
name|put
argument_list|(
literal|"default_custom"
argument_list|,
name|def
argument_list|)
expr_stmt|;
return|return
name|supplierMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|custom
argument_list|,
name|module
operator|.
name|getHttpServerTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|customTransport
argument_list|,
name|module
operator|.
name|getTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testDefaultKeys
specifier|public
name|void
name|testDefaultKeys
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
name|NetworkModule
operator|.
name|HTTP_DEFAULT_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"default_custom"
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkModule
operator|.
name|TRANSPORT_DEFAULT_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"default_custom"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
name|custom
init|=
name|FakeHttpTransport
operator|::
operator|new
decl_stmt|;
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
name|def
init|=
name|FakeHttpTransport
operator|::
operator|new
decl_stmt|;
name|Supplier
argument_list|<
name|Transport
argument_list|>
name|customTransport
init|=
parameter_list|()
lambda|->
literal|null
decl_stmt|;
name|NetworkModule
name|module
init|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
operator|new
name|NetworkPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|Transport
argument_list|>
argument_list|>
name|getTransports
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"default_custom"
argument_list|,
name|customTransport
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
argument_list|>
name|getHttpTransports
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|NamedXContentRegistry
name|xContentRegistry
parameter_list|,
name|NetworkService
name|networkService
parameter_list|,
name|HttpServerTransport
operator|.
name|Dispatcher
name|requestDispatcher
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|HttpServerTransport
argument_list|>
argument_list|>
name|supplierMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|supplierMap
operator|.
name|put
argument_list|(
literal|"custom"
argument_list|,
name|custom
argument_list|)
expr_stmt|;
name|supplierMap
operator|.
name|put
argument_list|(
literal|"default_custom"
argument_list|,
name|def
argument_list|)
expr_stmt|;
return|return
name|supplierMap
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|def
argument_list|,
name|module
operator|.
name|getHttpServerTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|customTransport
argument_list|,
name|module
operator|.
name|getTransportSupplier
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterInterceptor
specifier|public
name|void
name|testRegisterInterceptor
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
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_KEY
argument_list|,
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AtomicInteger
name|called
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|TransportInterceptor
name|interceptor
init|=
operator|new
name|TransportInterceptor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|TransportRequest
parameter_list|>
name|TransportRequestHandler
argument_list|<
name|T
argument_list|>
name|interceptHandler
parameter_list|(
name|String
name|action
parameter_list|,
name|String
name|executor
parameter_list|,
name|boolean
name|forceExecution
parameter_list|,
name|TransportRequestHandler
argument_list|<
name|T
argument_list|>
name|actualHandler
parameter_list|)
block|{
name|called
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"foo/bar/boom"
operator|.
name|equals
argument_list|(
name|action
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|forceExecution
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|forceExecution
argument_list|)
expr_stmt|;
block|}
return|return
name|actualHandler
return|;
block|}
block|}
decl_stmt|;
name|NetworkModule
name|module
init|=
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
operator|new
name|NetworkPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|TransportInterceptor
argument_list|>
name|getTransportInterceptors
parameter_list|(
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|ThreadContext
name|threadContext
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|threadContext
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|interceptor
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|TransportInterceptor
name|transportInterceptor
init|=
name|module
operator|.
name|getTransportInterceptor
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|transportInterceptor
operator|.
name|interceptHandler
argument_list|(
literal|"foo/bar/boom"
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|transportInterceptor
operator|.
name|interceptHandler
argument_list|(
literal|"foo/baz/boom"
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|transportInterceptor
operator|instanceof
name|NetworkModule
operator|.
name|CompositeTransportInterceptor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|(
operator|(
name|NetworkModule
operator|.
name|CompositeTransportInterceptor
operator|)
name|transportInterceptor
operator|)
operator|.
name|transportInterceptors
operator|.
name|size
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
operator|(
operator|(
name|NetworkModule
operator|.
name|CompositeTransportInterceptor
operator|)
name|transportInterceptor
operator|)
operator|.
name|transportInterceptors
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|interceptor
argument_list|)
expr_stmt|;
name|NullPointerException
name|nullPointerException
init|=
name|expectThrows
argument_list|(
name|NullPointerException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|newNetworkModule
argument_list|(
name|settings
argument_list|,
literal|false
argument_list|,
operator|new
name|NetworkPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|TransportInterceptor
argument_list|>
name|getTransportInterceptors
parameter_list|(
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|ThreadContext
name|threadContext
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|threadContext
argument_list|)
expr_stmt|;
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"interceptor must not be null"
argument_list|,
name|nullPointerException
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|newNetworkModule
specifier|private
name|NetworkModule
name|newNetworkModule
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|boolean
name|transportClient
parameter_list|,
name|NetworkPlugin
modifier|...
name|plugins
parameter_list|)
block|{
return|return
operator|new
name|NetworkModule
argument_list|(
name|settings
argument_list|,
name|transportClient
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|plugins
argument_list|)
argument_list|,
name|threadPool
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|xContentRegistry
argument_list|()
argument_list|,
literal|null
argument_list|,
operator|new
name|NullDispatcher
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

