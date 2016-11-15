begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|node
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
name|cluster
operator|.
name|ClusterInfoService
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
name|MockInternalClusterInfoService
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
name|ZenPing
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
name|indices
operator|.
name|recovery
operator|.
name|RecoverySettings
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
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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
name|MockSearchService
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
name|search
operator|.
name|fetch
operator|.
name|FetchPhase
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
name|discovery
operator|.
name|MockZenPing
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
name|TransportService
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

begin_comment
comment|/**  * A node for testing which allows:  *<ul>  *<li>Overriding Version.CURRENT</li>  *<li>Adding test plugins that exist on the classpath</li>  *</ul>  */
end_comment

begin_class
DECL|class|MockNode
specifier|public
class|class
name|MockNode
extends|extends
name|Node
block|{
DECL|field|classpathPlugins
specifier|private
specifier|final
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|classpathPlugins
decl_stmt|;
DECL|method|MockNode
specifier|public
name|MockNode
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
name|classpathPlugins
parameter_list|)
block|{
name|super
argument_list|(
name|InternalSettingsPreparer
operator|.
name|prepareEnvironment
argument_list|(
name|settings
argument_list|,
literal|null
argument_list|)
argument_list|,
name|classpathPlugins
argument_list|)
expr_stmt|;
name|this
operator|.
name|classpathPlugins
operator|=
name|classpathPlugins
expr_stmt|;
block|}
comment|/**      * The classpath plugins this node was constructed with.      */
DECL|method|getClasspathPlugins
specifier|public
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getClasspathPlugins
parameter_list|()
block|{
return|return
name|classpathPlugins
return|;
block|}
annotation|@
name|Override
DECL|method|createBigArrays
specifier|protected
name|BigArrays
name|createBigArrays
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
parameter_list|)
block|{
if|if
condition|(
name|getPluginsService
argument_list|()
operator|.
name|filterPlugins
argument_list|(
name|NodeMocksPlugin
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|createBigArrays
argument_list|(
name|settings
argument_list|,
name|circuitBreakerService
argument_list|)
return|;
block|}
return|return
operator|new
name|MockBigArrays
argument_list|(
name|settings
argument_list|,
name|circuitBreakerService
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newSearchService
specifier|protected
name|SearchService
name|newSearchService
parameter_list|(
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|FetchPhase
name|fetchPhase
parameter_list|)
block|{
if|if
condition|(
name|getPluginsService
argument_list|()
operator|.
name|filterPlugins
argument_list|(
name|MockSearchService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|newSearchService
argument_list|(
name|clusterService
argument_list|,
name|indicesService
argument_list|,
name|threadPool
argument_list|,
name|scriptService
argument_list|,
name|bigArrays
argument_list|,
name|fetchPhase
argument_list|)
return|;
block|}
return|return
operator|new
name|MockSearchService
argument_list|(
name|clusterService
argument_list|,
name|indicesService
argument_list|,
name|threadPool
argument_list|,
name|scriptService
argument_list|,
name|bigArrays
argument_list|,
name|fetchPhase
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newTransportService
specifier|protected
name|TransportService
name|newTransportService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Transport
name|transport
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportInterceptor
name|interceptor
parameter_list|,
name|ClusterSettings
name|clusterSettings
parameter_list|)
block|{
comment|// we use the MockTransportService.TestPlugin class as a marker to create a network
comment|// module with this MockNetworkService. NetworkService is such an integral part of the systme
comment|// we don't allow to plug it in from plugins or anything. this is a test-only override and
comment|// can't be done in a production env.
if|if
condition|(
name|getPluginsService
argument_list|()
operator|.
name|filterPlugins
argument_list|(
name|MockTransportService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|newTransportService
argument_list|(
name|settings
argument_list|,
name|transport
argument_list|,
name|threadPool
argument_list|,
name|interceptor
argument_list|,
name|clusterSettings
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MockTransportService
argument_list|(
name|settings
argument_list|,
name|transport
argument_list|,
name|threadPool
argument_list|,
name|interceptor
argument_list|,
name|clusterSettings
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|newTribeClientNode
specifier|protected
name|Node
name|newTribeClientNode
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
name|classpathPlugins
parameter_list|)
block|{
return|return
operator|new
name|MockNode
argument_list|(
name|settings
argument_list|,
name|classpathPlugins
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|processRecoverySettings
specifier|protected
name|void
name|processRecoverySettings
parameter_list|(
name|ClusterSettings
name|clusterSettings
parameter_list|,
name|RecoverySettings
name|recoverySettings
parameter_list|)
block|{
if|if
condition|(
literal|false
operator|==
name|getPluginsService
argument_list|()
operator|.
name|filterPlugins
argument_list|(
name|RecoverySettingsChunkSizePlugin
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|clusterSettings
operator|.
name|addSettingsUpdateConsumer
argument_list|(
name|RecoverySettingsChunkSizePlugin
operator|.
name|CHUNK_SIZE_SETTING
argument_list|,
name|recoverySettings
operator|::
name|setChunkSize
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newClusterInfoService
specifier|protected
name|ClusterInfoService
name|newClusterInfoService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|NodeClient
name|client
parameter_list|)
block|{
if|if
condition|(
name|getPluginsService
argument_list|()
operator|.
name|filterPlugins
argument_list|(
name|MockInternalClusterInfoService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|super
operator|.
name|newClusterInfoService
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|client
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|MockInternalClusterInfoService
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|client
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

