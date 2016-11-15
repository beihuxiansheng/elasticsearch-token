begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
package|;
end_package

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
name|CountDownLatch
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
name|AtomicBoolean
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
name|Function
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

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
name|service
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
name|ClusterSettings
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
name|discovery
operator|.
name|zen
operator|.
name|UnicastHostsProvider
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
name|ZenDiscovery
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
name|ZenPing
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
name|DiscoveryPlugin
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
name|NoopDiscovery
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
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|DiscoveryModuleTests
specifier|public
class|class
name|DiscoveryModuleTests
extends|extends
name|ESTestCase
block|{
DECL|field|transportService
specifier|private
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|threadPool
specifier|private
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|interface|DummyHostsProviderPlugin
specifier|public
interface|interface
name|DummyHostsProviderPlugin
extends|extends
name|DiscoveryPlugin
block|{
DECL|method|impl
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|UnicastHostsProvider
argument_list|>
argument_list|>
name|impl
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getZenHostsProviders
specifier|default
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|UnicastHostsProvider
argument_list|>
argument_list|>
name|getZenHostsProviders
parameter_list|(
name|TransportService
name|transportService
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
return|return
name|impl
argument_list|()
return|;
block|}
block|}
DECL|interface|DummyDiscoveryPlugin
specifier|public
interface|interface
name|DummyDiscoveryPlugin
extends|extends
name|DiscoveryPlugin
block|{
DECL|method|impl
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|Discovery
argument_list|>
argument_list|>
name|impl
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|getDiscoveryTypes
specifier|default
name|Map
argument_list|<
name|String
argument_list|,
name|Supplier
argument_list|<
name|Discovery
argument_list|>
argument_list|>
name|getDiscoveryTypes
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|UnicastHostsProvider
name|hostsProvider
parameter_list|)
block|{
return|return
name|impl
argument_list|()
return|;
block|}
block|}
annotation|@
name|Before
DECL|method|setupDummyServices
specifier|public
name|void
name|setupDummyServices
parameter_list|()
block|{
name|transportService
operator|=
name|MockTransportService
operator|.
name|createNewService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|clusterService
operator|=
name|mock
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
expr_stmt|;
name|ClusterSettings
name|clusterSettings
init|=
operator|new
name|ClusterSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|clusterService
operator|.
name|getClusterSettings
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|clusterSettings
argument_list|)
expr_stmt|;
name|threadPool
operator|=
name|mock
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|clearDummyServices
specifier|public
name|void
name|clearDummyServices
parameter_list|()
throws|throws
name|IOException
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|transportService
argument_list|)
expr_stmt|;
block|}
DECL|method|newModule
specifier|private
name|DiscoveryModule
name|newModule
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|List
argument_list|<
name|DiscoveryPlugin
argument_list|>
name|plugins
parameter_list|)
block|{
return|return
operator|new
name|DiscoveryModule
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
literal|null
argument_list|,
name|clusterService
argument_list|,
name|plugins
argument_list|)
return|;
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
parameter_list|()
block|{
name|DiscoveryModule
name|module
init|=
name|newModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|module
operator|.
name|getDiscovery
argument_list|()
operator|instanceof
name|ZenDiscovery
argument_list|)
expr_stmt|;
block|}
DECL|method|testLazyConstructionDiscovery
specifier|public
name|void
name|testLazyConstructionDiscovery
parameter_list|()
block|{
name|DummyDiscoveryPlugin
name|plugin
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"custom"
argument_list|,
parameter_list|()
lambda|->
block|{
throw|throw
argument_list|new
name|AssertionError
argument_list|(
literal|"created discovery type which was not selected"
argument_list|)
decl_stmt|;
block|}
block|)
class|;
end_class

begin_expr_stmt
name|newModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|plugin
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      public
DECL|method|testRegisterDiscovery
name|void
name|testRegisterDiscovery
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
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DummyDiscoveryPlugin
name|plugin
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"custom"
argument_list|,
name|NoopDiscovery
operator|::
operator|new
argument_list|)
decl_stmt|;
name|DiscoveryModule
name|module
init|=
name|newModule
argument_list|(
name|settings
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|plugin
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|module
operator|.
name|getDiscovery
argument_list|()
operator|instanceof
name|NoopDiscovery
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testUnknownDiscovery
specifier|public
name|void
name|testUnknownDiscovery
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
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"dne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|newModule
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
name|assertEquals
argument_list|(
literal|"Unknown discovery type [dne]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testDuplicateDiscovery
specifier|public
name|void
name|testDuplicateDiscovery
parameter_list|()
block|{
name|DummyDiscoveryPlugin
name|plugin1
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"dup"
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
decl_stmt|;
name|DummyDiscoveryPlugin
name|plugin2
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"dup"
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|newModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|plugin1
argument_list|,
name|plugin2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Cannot register discovery type [dup] twice"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testHostsProvider
specifier|public
name|void
name|testHostsProvider
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
name|DiscoveryModule
operator|.
name|DISCOVERY_HOSTS_PROVIDER_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|UnicastHostsProvider
name|provider
init|=
name|Collections
operator|::
name|emptyList
decl_stmt|;
name|AtomicBoolean
name|created
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|DummyHostsProviderPlugin
name|plugin
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"custom"
argument_list|,
parameter_list|()
lambda|->
block|{
name|created
operator|.
name|set
argument_list|(
literal|true
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|::
name|emptyList
return|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|newModule
argument_list|(
name|settings
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|plugin
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|assertTrue
argument_list|(
name|created
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      public
DECL|method|testUnknownHostsProvider
name|void
name|testUnknownHostsProvider
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
name|DiscoveryModule
operator|.
name|DISCOVERY_HOSTS_PROVIDER_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"dne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|newModule
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
name|assertEquals
argument_list|(
literal|"Unknown zen hosts provider [dne]"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testDuplicateHostsProvider
specifier|public
name|void
name|testDuplicateHostsProvider
parameter_list|()
block|{
name|DummyHostsProviderPlugin
name|plugin1
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"dup"
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
decl_stmt|;
name|DummyHostsProviderPlugin
name|plugin2
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"dup"
argument_list|,
parameter_list|()
lambda|->
literal|null
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|newModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|plugin1
argument_list|,
name|plugin2
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Cannot register zen hosts provider [dup] twice"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testLazyConstructionHostsProvider
specifier|public
name|void
name|testLazyConstructionHostsProvider
parameter_list|()
block|{
name|DummyHostsProviderPlugin
name|plugin
init|=
parameter_list|()
lambda|->
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"custom"
argument_list|,
parameter_list|()
lambda|->
block|{
throw|throw
argument_list|new
name|AssertionError
argument_list|(
literal|"created hosts provider which was not selected"
argument_list|)
decl_stmt|;
block|}
end_function

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|newModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|plugin
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

unit|} }
end_unit

