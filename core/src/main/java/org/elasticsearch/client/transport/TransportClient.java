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
name|action
operator|.
name|Action
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionResponse
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
name|support
operator|.
name|AbstractClient
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
name|transport
operator|.
name|support
operator|.
name|TransportProxyClient
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
name|component
operator|.
name|LifecycleComponent
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
name|Injector
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
name|Module
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
name|ModulesBuilder
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
name|Setting
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
name|settings
operator|.
name|SettingsModule
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
name|CircuitBreakerService
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
name|plugins
operator|.
name|ActionPlugin
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
name|plugins
operator|.
name|Plugin
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
name|plugins
operator|.
name|SearchPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchModule
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
name|ExecutorBuilder
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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
operator|.
name|generateCustomNameResolvers
import|;
end_import

begin_comment
comment|/**  * The transport client allows to create a client that is not part of the cluster, but simply connects to one  * or more nodes directly by adding their respective addresses using {@link #addTransportAddress(org.elasticsearch.common.transport.TransportAddress)}.  *<p>  * The transport client important modules used is the {@link org.elasticsearch.common.network.NetworkModule} which is  * started in client mode (only connects, no bind).  */
end_comment

begin_class
DECL|class|TransportClient
specifier|public
specifier|abstract
class|class
name|TransportClient
extends|extends
name|AbstractClient
block|{
DECL|method|newPluginService
specifier|private
specifier|static
name|PluginsService
name|newPluginService
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|plugins
parameter_list|)
block|{
specifier|final
name|Settings
operator|.
name|Builder
name|settingsBuilder
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|TcpTransport
operator|.
name|PING_SCHEDULE
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"5s"
argument_list|)
comment|// enable by default the transport schedule ping interval
operator|.
name|put
argument_list|(
name|InternalSettingsPreparer
operator|.
name|prepareSettings
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|NetworkService
operator|.
name|NETWORK_SERVER
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|CLIENT_TYPE_SETTING_S
operator|.
name|getKey
argument_list|()
argument_list|,
name|CLIENT_TYPE
argument_list|)
decl_stmt|;
return|return
operator|new
name|PluginsService
argument_list|(
name|settingsBuilder
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|plugins
argument_list|)
return|;
block|}
DECL|method|addPlugins
specifier|protected
specifier|static
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|addPlugins
parameter_list|(
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|collection
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
modifier|...
name|plugins
parameter_list|)
block|{
return|return
name|addPlugins
argument_list|(
name|collection
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|plugins
argument_list|)
argument_list|)
return|;
block|}
DECL|method|addPlugins
specifier|protected
specifier|static
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|addPlugins
parameter_list|(
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|collection
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|plugins
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|collection
argument_list|)
decl_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
name|p
range|:
name|plugins
control|)
block|{
if|if
condition|(
name|list
operator|.
name|contains
argument_list|(
name|p
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"plugin already exists: "
operator|+
name|p
argument_list|)
throw|;
block|}
name|list
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
DECL|method|buildTemplate
specifier|private
specifier|static
name|ClientTemplate
name|buildTemplate
parameter_list|(
name|Settings
name|providedSettings
parameter_list|,
name|Settings
name|defaultSettings
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|plugins
parameter_list|)
block|{
if|if
condition|(
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|exists
argument_list|(
name|providedSettings
argument_list|)
operator|==
literal|false
condition|)
block|{
name|providedSettings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|providedSettings
argument_list|)
operator|.
name|put
argument_list|(
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"_client_"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|final
name|PluginsService
name|pluginsService
init|=
name|newPluginService
argument_list|(
name|providedSettings
argument_list|,
name|plugins
argument_list|)
decl_stmt|;
specifier|final
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
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
name|pluginsService
operator|.
name|updatedSettings
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Closeable
argument_list|>
name|resourcesToClose
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|ThreadPool
name|threadPool
init|=
operator|new
name|ThreadPool
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|resourcesToClose
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
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
argument_list|)
expr_stmt|;
specifier|final
name|NetworkService
name|networkService
init|=
operator|new
name|NetworkService
argument_list|(
name|settings
argument_list|,
name|generateCustomNameResolvers
argument_list|(
name|settings
argument_list|,
name|pluginsService
operator|.
name|filterPlugins
argument_list|(
name|DiscoveryPlugin
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|NamedWriteableRegistry
name|namedWriteableRegistry
init|=
operator|new
name|NamedWriteableRegistry
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|List
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|additionalSettings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|additionalSettingsFilter
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|additionalSettings
operator|.
name|addAll
argument_list|(
name|pluginsService
operator|.
name|getPluginSettings
argument_list|()
argument_list|)
expr_stmt|;
name|additionalSettingsFilter
operator|.
name|addAll
argument_list|(
name|pluginsService
operator|.
name|getPluginSettingsFilter
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|ExecutorBuilder
argument_list|<
name|?
argument_list|>
name|builder
range|:
name|threadPool
operator|.
name|builders
argument_list|()
control|)
block|{
name|additionalSettings
operator|.
name|addAll
argument_list|(
name|builder
operator|.
name|getRegisteredSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|SettingsModule
name|settingsModule
init|=
operator|new
name|SettingsModule
argument_list|(
name|settings
argument_list|,
name|additionalSettings
argument_list|,
name|additionalSettingsFilter
argument_list|)
decl_stmt|;
name|ModulesBuilder
name|modules
init|=
operator|new
name|ModulesBuilder
argument_list|()
decl_stmt|;
comment|// plugin modules must be added here, before others or we can get crazy injection errors...
for|for
control|(
name|Module
name|pluginModule
range|:
name|pluginsService
operator|.
name|createGuiceModules
argument_list|()
control|)
block|{
name|modules
operator|.
name|add
argument_list|(
name|pluginModule
argument_list|)
expr_stmt|;
block|}
name|modules
operator|.
name|add
argument_list|(
operator|new
name|NetworkModule
argument_list|(
name|networkService
argument_list|,
name|settings
argument_list|,
literal|true
argument_list|,
name|namedWriteableRegistry
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|b
lambda|->
name|b
operator|.
name|bind
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|threadPool
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|SearchModule
argument_list|(
name|settings
argument_list|,
name|namedWriteableRegistry
argument_list|,
literal|true
argument_list|,
name|pluginsService
operator|.
name|filterPlugins
argument_list|(
name|SearchPlugin
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ActionModule
name|actionModule
init|=
operator|new
name|ActionModule
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
name|settings
argument_list|,
literal|null
argument_list|,
name|settingsModule
operator|.
name|getClusterSettings
argument_list|()
argument_list|,
name|pluginsService
operator|.
name|filterPlugins
argument_list|(
name|ActionPlugin
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|actionModule
argument_list|)
expr_stmt|;
name|pluginsService
operator|.
name|processModules
argument_list|(
name|modules
argument_list|)
expr_stmt|;
name|CircuitBreakerService
name|circuitBreakerService
init|=
name|Node
operator|.
name|createCircuitBreakerService
argument_list|(
name|settingsModule
operator|.
name|getSettings
argument_list|()
argument_list|,
name|settingsModule
operator|.
name|getClusterSettings
argument_list|()
argument_list|)
decl_stmt|;
name|resourcesToClose
operator|.
name|add
argument_list|(
name|circuitBreakerService
argument_list|)
expr_stmt|;
name|BigArrays
name|bigArrays
init|=
operator|new
name|BigArrays
argument_list|(
name|settings
argument_list|,
name|circuitBreakerService
argument_list|)
decl_stmt|;
name|resourcesToClose
operator|.
name|add
argument_list|(
name|bigArrays
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
name|settingsModule
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|(
name|b
lambda|->
block|{
name|b
operator|.
name|bind
argument_list|(
name|BigArrays
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|bigArrays
argument_list|)
expr_stmt|;
name|b
operator|.
name|bind
argument_list|(
name|PluginsService
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|pluginsService
argument_list|)
expr_stmt|;
name|b
operator|.
name|bind
argument_list|(
name|CircuitBreakerService
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|circuitBreakerService
argument_list|)
expr_stmt|;
block|}
operator|)
argument_list|)
expr_stmt|;
name|Injector
name|injector
init|=
name|modules
operator|.
name|createInjector
argument_list|()
decl_stmt|;
specifier|final
name|TransportService
name|transportService
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|TransportClientNodesService
name|nodesService
init|=
operator|new
name|TransportClientNodesService
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|threadPool
argument_list|)
decl_stmt|;
specifier|final
name|TransportProxyClient
name|proxy
init|=
operator|new
name|TransportProxyClient
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|nodesService
argument_list|,
name|actionModule
operator|.
name|getActions
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|x
lambda|->
name|x
operator|.
name|getAction
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LifecycleComponent
argument_list|>
name|pluginLifecycleComponents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|pluginLifecycleComponents
operator|.
name|addAll
argument_list|(
name|pluginsService
operator|.
name|getGuiceServiceClasses
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|injector
operator|::
name|getInstance
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|resourcesToClose
operator|.
name|addAll
argument_list|(
name|pluginLifecycleComponents
argument_list|)
expr_stmt|;
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
name|ClientTemplate
name|transportClient
init|=
operator|new
name|ClientTemplate
argument_list|(
name|injector
argument_list|,
name|pluginLifecycleComponents
argument_list|,
name|nodesService
argument_list|,
name|proxy
argument_list|)
decl_stmt|;
name|resourcesToClose
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|transportClient
return|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|resourcesToClose
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ClientTemplate
specifier|private
specifier|static
specifier|final
class|class
name|ClientTemplate
block|{
DECL|field|injector
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|pluginLifecycleComponents
specifier|private
specifier|final
name|List
argument_list|<
name|LifecycleComponent
argument_list|>
name|pluginLifecycleComponents
decl_stmt|;
DECL|field|nodesService
specifier|private
specifier|final
name|TransportClientNodesService
name|nodesService
decl_stmt|;
DECL|field|proxy
specifier|private
specifier|final
name|TransportProxyClient
name|proxy
decl_stmt|;
DECL|method|ClientTemplate
specifier|private
name|ClientTemplate
parameter_list|(
name|Injector
name|injector
parameter_list|,
name|List
argument_list|<
name|LifecycleComponent
argument_list|>
name|pluginLifecycleComponents
parameter_list|,
name|TransportClientNodesService
name|nodesService
parameter_list|,
name|TransportProxyClient
name|proxy
parameter_list|)
block|{
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|this
operator|.
name|pluginLifecycleComponents
operator|=
name|pluginLifecycleComponents
expr_stmt|;
name|this
operator|.
name|nodesService
operator|=
name|nodesService
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
block|}
DECL|method|getSettings
name|Settings
name|getSettings
parameter_list|()
block|{
return|return
name|injector
operator|.
name|getInstance
argument_list|(
name|Settings
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|getThreadPool
name|ThreadPool
name|getThreadPool
parameter_list|()
block|{
return|return
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
return|;
block|}
block|}
DECL|field|CLIENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CLIENT_TYPE
init|=
literal|"transport"
decl_stmt|;
DECL|field|injector
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|pluginLifecycleComponents
specifier|private
specifier|final
name|List
argument_list|<
name|LifecycleComponent
argument_list|>
name|pluginLifecycleComponents
decl_stmt|;
DECL|field|nodesService
specifier|private
specifier|final
name|TransportClientNodesService
name|nodesService
decl_stmt|;
DECL|field|proxy
specifier|private
specifier|final
name|TransportProxyClient
name|proxy
decl_stmt|;
comment|/**      * Creates a new TransportClient with the given settings and plugins      */
DECL|method|TransportClient
specifier|public
name|TransportClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|plugins
parameter_list|)
block|{
name|this
argument_list|(
name|buildTemplate
argument_list|(
name|settings
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|plugins
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new TransportClient with the given settings, defaults and plugins.      * @param settings the client settings      * @param defaultSettings default settings that are merged after the plugins have added it's additional settings.      * @param plugins the client plugins      */
DECL|method|TransportClient
specifier|protected
name|TransportClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Settings
name|defaultSettings
parameter_list|,
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|plugins
parameter_list|)
block|{
name|this
argument_list|(
name|buildTemplate
argument_list|(
name|settings
argument_list|,
name|defaultSettings
argument_list|,
name|plugins
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|TransportClient
specifier|private
name|TransportClient
parameter_list|(
name|ClientTemplate
name|template
parameter_list|)
block|{
name|super
argument_list|(
name|template
operator|.
name|getSettings
argument_list|()
argument_list|,
name|template
operator|.
name|getThreadPool
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|template
operator|.
name|injector
expr_stmt|;
name|this
operator|.
name|pluginLifecycleComponents
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|template
operator|.
name|pluginLifecycleComponents
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodesService
operator|=
name|template
operator|.
name|nodesService
expr_stmt|;
name|this
operator|.
name|proxy
operator|=
name|template
operator|.
name|proxy
expr_stmt|;
block|}
comment|/**      * Returns the current registered transport addresses to use (added using      * {@link #addTransportAddress(org.elasticsearch.common.transport.TransportAddress)}.      */
DECL|method|transportAddresses
specifier|public
name|List
argument_list|<
name|TransportAddress
argument_list|>
name|transportAddresses
parameter_list|()
block|{
return|return
name|nodesService
operator|.
name|transportAddresses
argument_list|()
return|;
block|}
comment|/**      * Returns the current connected transport nodes that this client will use.      *<p>      * The nodes include all the nodes that are currently alive based on the transport      * addresses provided.      */
DECL|method|connectedNodes
specifier|public
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|connectedNodes
parameter_list|()
block|{
return|return
name|nodesService
operator|.
name|connectedNodes
argument_list|()
return|;
block|}
comment|/**      * The list of filtered nodes that were not connected to, for example, due to      * mismatch in cluster name.      */
DECL|method|filteredNodes
specifier|public
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|filteredNodes
parameter_list|()
block|{
return|return
name|nodesService
operator|.
name|filteredNodes
argument_list|()
return|;
block|}
comment|/**      * Returns the listed nodes in the transport client (ones added to it).      */
DECL|method|listedNodes
specifier|public
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|listedNodes
parameter_list|()
block|{
return|return
name|nodesService
operator|.
name|listedNodes
argument_list|()
return|;
block|}
comment|/**      * Adds a transport address that will be used to connect to.      *<p>      * The Node this transport address represents will be used if its possible to connect to it.      * If it is unavailable, it will be automatically connected to once it is up.      *<p>      * In order to get the list of all the current connected nodes, please see {@link #connectedNodes()}.      */
DECL|method|addTransportAddress
specifier|public
name|TransportClient
name|addTransportAddress
parameter_list|(
name|TransportAddress
name|transportAddress
parameter_list|)
block|{
name|nodesService
operator|.
name|addTransportAddresses
argument_list|(
name|transportAddress
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a list of transport addresses that will be used to connect to.      *<p>      * The Node this transport address represents will be used if its possible to connect to it.      * If it is unavailable, it will be automatically connected to once it is up.      *<p>      * In order to get the list of all the current connected nodes, please see {@link #connectedNodes()}.      */
DECL|method|addTransportAddresses
specifier|public
name|TransportClient
name|addTransportAddresses
parameter_list|(
name|TransportAddress
modifier|...
name|transportAddress
parameter_list|)
block|{
name|nodesService
operator|.
name|addTransportAddresses
argument_list|(
name|transportAddress
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Removes a transport address from the list of transport addresses that are used to connect to.      */
DECL|method|removeTransportAddress
specifier|public
name|TransportClient
name|removeTransportAddress
parameter_list|(
name|TransportAddress
name|transportAddress
parameter_list|)
block|{
name|nodesService
operator|.
name|removeTransportAddress
argument_list|(
name|transportAddress
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Closes the client.      */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|List
argument_list|<
name|Closeable
argument_list|>
name|closeables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|closeables
operator|.
name|add
argument_list|(
name|nodesService
argument_list|)
expr_stmt|;
name|closeables
operator|.
name|add
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|LifecycleComponent
name|plugin
range|:
name|pluginLifecycleComponents
control|)
block|{
name|closeables
operator|.
name|add
argument_list|(
name|plugin
argument_list|)
expr_stmt|;
block|}
name|closeables
operator|.
name|add
argument_list|(
parameter_list|()
lambda|->
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|closeables
operator|.
name|add
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|BigArrays
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|closeables
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
parameter_list|<
name|Request
extends|extends
name|ActionRequest
argument_list|<
name|Request
argument_list|>
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|,
name|RequestBuilder
extends|extends
name|ActionRequestBuilder
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
parameter_list|>
name|void
name|doExecute
parameter_list|(
name|Action
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|,
name|RequestBuilder
argument_list|>
name|action
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|proxy
operator|.
name|execute
argument_list|(
name|action
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

