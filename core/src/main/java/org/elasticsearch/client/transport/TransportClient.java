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
name|cache
operator|.
name|recycler
operator|.
name|PageCacheRecycler
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
name|support
operator|.
name|Headers
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
name|ClusterNameModule
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
name|collect
operator|.
name|Tuple
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
name|env
operator|.
name|EnvironmentModule
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
name|CircuitBreakerModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|MonitorService
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
name|PluginsModule
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
name|ThreadPoolModule
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
name|TransportModule
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
operator|.
name|settingsBuilder
import|;
end_import

begin_comment
comment|/**  * The transport client allows to create a client that is not part of the cluster, but simply connects to one  * or more nodes directly by adding their respective addresses using {@link #addTransportAddress(org.elasticsearch.common.transport.TransportAddress)}.  *<p/>  *<p>The transport client important modules used is the {@link org.elasticsearch.transport.TransportModule} which is  * started in client mode (only connects, no bind).  */
end_comment

begin_class
DECL|class|TransportClient
specifier|public
class|class
name|TransportClient
extends|extends
name|AbstractClient
block|{
comment|/**      * Handy method ot create a {@link org.elasticsearch.client.transport.TransportClient.Builder}.      */
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**      * A builder used to create an instance of the transport client.      */
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|settings
specifier|private
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
DECL|field|loadConfigSettings
specifier|private
name|boolean
name|loadConfigSettings
init|=
literal|true
decl_stmt|;
comment|/**          * The settings to configure the transport client with.          */
DECL|method|settings
specifier|public
name|Builder
name|settings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
return|return
name|settings
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * The settings to configure the transport client with.          */
DECL|method|settings
specifier|public
name|Builder
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Should the transport client load file based configuration automatically or not (and rely          * only on the provided settings), defaults to true.          */
DECL|method|loadConfigSettings
specifier|public
name|Builder
name|loadConfigSettings
parameter_list|(
name|boolean
name|loadConfigSettings
parameter_list|)
block|{
name|this
operator|.
name|loadConfigSettings
operator|=
name|loadConfigSettings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Builds a new instance of the transport client.          */
DECL|method|build
specifier|public
name|TransportClient
name|build
parameter_list|()
block|{
name|Tuple
argument_list|<
name|Settings
argument_list|,
name|Environment
argument_list|>
name|tuple
init|=
name|InternalSettingsPreparer
operator|.
name|prepareSettings
argument_list|(
name|settings
argument_list|,
name|loadConfigSettings
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|NettyTransport
operator|.
name|PING_SCHEDULE
argument_list|,
literal|"5s"
argument_list|)
comment|// enable by default the transport schedule ping interval
operator|.
name|put
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"network.server"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.client"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|CLIENT_TYPE_SETTING
argument_list|,
name|CLIENT_TYPE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Environment
name|environment
init|=
name|tuple
operator|.
name|v2
argument_list|()
decl_stmt|;
name|PluginsService
name|pluginsService
init|=
operator|new
name|PluginsService
argument_list|(
name|settings
argument_list|,
name|tuple
operator|.
name|v2
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|settings
operator|=
name|pluginsService
operator|.
name|updatedSettings
argument_list|()
expr_stmt|;
name|Version
name|version
init|=
name|Version
operator|.
name|CURRENT
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
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|ModulesBuilder
name|modules
init|=
operator|new
name|ModulesBuilder
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|Version
operator|.
name|Module
argument_list|(
name|version
argument_list|)
argument_list|)
expr_stmt|;
comment|// plugin modules must be added here, before others or we can get crazy injection errors...
for|for
control|(
name|Module
name|pluginModule
range|:
name|pluginsService
operator|.
name|nodeModules
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
name|PluginsModule
argument_list|(
name|pluginsService
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|EnvironmentModule
argument_list|(
name|environment
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|SettingsModule
argument_list|(
name|this
operator|.
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|NetworkModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ClusterNameModule
argument_list|(
name|this
operator|.
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ThreadPoolModule
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
name|TransportModule
argument_list|(
name|this
operator|.
name|settings
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
name|this
operator|.
name|settings
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|configure
parameter_list|()
block|{
comment|// noop
block|}
block|}
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ActionModule
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ClientTransportModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|CircuitBreakerModule
argument_list|(
name|this
operator|.
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|pluginsService
operator|.
name|processModules
argument_list|(
name|modules
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
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|TransportClient
name|transportClient
init|=
operator|new
name|TransportClient
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|transportClient
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
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
block|}
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
DECL|method|TransportClient
specifier|private
name|TransportClient
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
name|super
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|Settings
operator|.
name|class
argument_list|)
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|Headers
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|nodesService
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportClientNodesService
operator|.
name|class
argument_list|)
expr_stmt|;
name|proxy
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportProxyClient
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|nodeService
name|TransportClientNodesService
name|nodeService
parameter_list|()
block|{
return|return
name|nodesService
return|;
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
comment|/**      * Returns the current connected transport nodes that this client will use.      *<p/>      *<p>The nodes include all the nodes that are currently alive based on the transport      * addresses provided.      */
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
comment|/**      * Adds a transport address that will be used to connect to.      *<p/>      *<p>The Node this transport address represents will be used if its possible to connect to it.      * If it is unavailable, it will be automatically connected to once it is up.      *<p/>      *<p>In order to get the list of all the current connected nodes, please see {@link #connectedNodes()}.      */
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
comment|/**      * Adds a list of transport addresses that will be used to connect to.      *<p/>      *<p>The Node this transport address represents will be used if its possible to connect to it.      * If it is unavailable, it will be automatically connected to once it is up.      *<p/>      *<p>In order to get the list of all the current connected nodes, please see {@link #connectedNodes()}.      */
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
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportClientNodesService
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|MonitorService
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore, might not be bounded
block|}
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|LifecycleComponent
argument_list|>
name|plugin
range|:
name|injector
operator|.
name|getInstance
argument_list|(
name|PluginsService
operator|.
name|class
argument_list|)
operator|.
name|nodeServices
argument_list|()
control|)
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|plugin
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|injector
operator|.
name|getInstance
argument_list|(
name|PageCacheRecycler
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
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

