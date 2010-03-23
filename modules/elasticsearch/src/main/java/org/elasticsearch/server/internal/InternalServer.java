begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.server.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|server
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Guice
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
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
name|ElasticSearchException
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
name|action
operator|.
name|TransportActionModule
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
name|Client
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
name|server
operator|.
name|ServerClientModule
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
name|ClusterModule
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
name|ClusterService
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
name|routing
operator|.
name|RoutingService
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
name|DiscoveryModule
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
name|DiscoveryService
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
name|gateway
operator|.
name|GatewayModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|GatewayService
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
name|HttpServer
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
name|HttpServerModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
operator|.
name|fs
operator|.
name|FsStores
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
name|IndicesModule
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
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|JmxModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|jmx
operator|.
name|JmxService
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
name|MonitorModule
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
name|rest
operator|.
name|RestController
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
name|RestModule
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
name|search
operator|.
name|SearchService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|server
operator|.
name|Server
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
name|timer
operator|.
name|TimerModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|timer
operator|.
name|TimerService
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
name|util
operator|.
name|ThreadLocals
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
operator|.
name|guice
operator|.
name|Injectors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|FileSystemUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|logging
operator|.
name|Loggers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|InternalServer
specifier|public
specifier|final
class|class
name|InternalServer
implements|implements
name|Server
block|{
DECL|field|lifecycle
specifier|private
specifier|final
name|Lifecycle
name|lifecycle
init|=
operator|new
name|Lifecycle
argument_list|()
decl_stmt|;
DECL|field|injector
specifier|private
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|environment
specifier|private
specifier|final
name|Environment
name|environment
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|method|InternalServer
specifier|public
name|InternalServer
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|this
argument_list|(
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|InternalServer
specifier|public
name|InternalServer
parameter_list|(
name|Settings
name|pSettings
parameter_list|,
name|boolean
name|loadConfigSettings
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|Tuple
argument_list|<
name|Settings
argument_list|,
name|Environment
argument_list|>
name|tuple
init|=
name|InternalSettingsPerparer
operator|.
name|prepareSettings
argument_list|(
name|pSettings
argument_list|,
name|loadConfigSettings
argument_list|)
decl_stmt|;
name|this
operator|.
name|settings
operator|=
name|tuple
operator|.
name|v1
argument_list|()
expr_stmt|;
name|this
operator|.
name|environment
operator|=
name|tuple
operator|.
name|v2
argument_list|()
expr_stmt|;
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Server
operator|.
name|class
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Initializing ..."
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Module
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<
name|Module
argument_list|>
argument_list|()
decl_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ServerModule
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|JmxModule
argument_list|(
name|settings
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
name|ClusterNameModule
argument_list|(
name|settings
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
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|TimerModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryModule
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ClusterModule
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|RestModule
argument_list|(
name|settings
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
name|settings
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"http.enabled"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|modules
operator|.
name|add
argument_list|(
operator|new
name|HttpServerModule
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndicesModule
argument_list|(
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
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|TransportActionModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|MonitorModule
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|GatewayModule
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ServerClientModule
argument_list|()
argument_list|)
expr_stmt|;
name|injector
operator|=
name|Guice
operator|.
name|createInjector
argument_list|(
name|modules
argument_list|)
expr_stmt|;
name|client
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|Client
operator|.
name|class
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Initialized"
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|settings
annotation|@
name|Override
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
return|;
block|}
DECL|method|client
annotation|@
name|Override
specifier|public
name|Client
name|client
parameter_list|()
block|{
return|return
name|client
return|;
block|}
DECL|method|start
specifier|public
name|Server
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|moveToStarted
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Server
operator|.
name|class
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Starting ..."
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|GatewayService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|RoutingService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|SearchService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|MonitorService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|RestController
operator|.
name|class
argument_list|)
operator|.
name|start
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
name|start
argument_list|()
expr_stmt|;
name|DiscoveryService
name|discoService
init|=
name|injector
operator|.
name|getInstance
argument_list|(
name|DiscoveryService
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"http.enabled"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|HttpServer
operator|.
name|class
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|injector
operator|.
name|getInstance
argument_list|(
name|JmxService
operator|.
name|class
argument_list|)
operator|.
name|connectAndRegister
argument_list|(
name|discoService
operator|.
name|nodeDescription
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Started"
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|stop
annotation|@
name|Override
specifier|public
name|Server
name|stop
parameter_list|()
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|moveToStopped
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Server
operator|.
name|class
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Stopping ..."
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"http.enabled"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|HttpServer
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|injector
operator|.
name|getInstance
argument_list|(
name|RoutingService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|DiscoveryService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|MonitorService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|GatewayService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|SearchService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|RestController
operator|.
name|class
argument_list|)
operator|.
name|stop
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
name|stop
argument_list|()
expr_stmt|;
name|injector
operator|.
name|getInstance
argument_list|(
name|JmxService
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Not pretty, but here we go
try|try
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|environment
operator|.
name|workWithClusterFile
argument_list|()
argument_list|,
name|FsStores
operator|.
name|DEFAULT_INDICES_LOCATION
argument_list|)
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|)
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
name|Injectors
operator|.
name|close
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Stopped"
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|moveToClosed
argument_list|()
condition|)
block|{
return|return;
block|}
name|Logger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|Server
operator|.
name|class
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Closing ..."
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"http.enabled"
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|HttpServer
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|injector
operator|.
name|getInstance
argument_list|(
name|Client
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
name|RoutingService
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
name|ClusterService
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
name|DiscoveryService
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
name|MonitorService
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
name|GatewayService
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
name|SearchService
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
name|IndicesService
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
name|RestController
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
name|injector
operator|.
name|getInstance
argument_list|(
name|TimerService
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
name|ThreadPool
operator|.
name|class
argument_list|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
operator|.
name|awaitTermination
argument_list|(
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
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
try|try
block|{
name|injector
operator|.
name|getInstance
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
operator|.
name|shutdownNow
argument_list|()
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
name|ThreadLocals
operator|.
name|clearReferencesThreadLocals
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"{{}}: Closed"
argument_list|,
name|Version
operator|.
name|full
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|injector
specifier|public
name|Injector
name|injector
parameter_list|()
block|{
return|return
name|this
operator|.
name|injector
return|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|InternalServer
name|server
init|=
operator|new
name|InternalServer
argument_list|()
decl_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|server
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

