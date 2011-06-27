begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchException
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
name|ActionFuture
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
name|bulk
operator|.
name|BulkRequest
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
name|bulk
operator|.
name|BulkResponse
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
name|count
operator|.
name|CountRequest
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
name|count
operator|.
name|CountResponse
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
name|delete
operator|.
name|DeleteRequest
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
name|delete
operator|.
name|DeleteResponse
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
name|deletebyquery
operator|.
name|DeleteByQueryRequest
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
name|deletebyquery
operator|.
name|DeleteByQueryResponse
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
name|get
operator|.
name|GetRequest
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
name|get
operator|.
name|GetResponse
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
name|get
operator|.
name|MultiGetRequest
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
name|get
operator|.
name|MultiGetResponse
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
name|index
operator|.
name|IndexRequest
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
name|index
operator|.
name|IndexResponse
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
name|mlt
operator|.
name|MoreLikeThisRequest
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
name|percolate
operator|.
name|PercolateRequest
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
name|percolate
operator|.
name|PercolateResponse
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
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchResponse
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
name|search
operator|.
name|SearchScrollRequest
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
name|AdminClient
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
name|action
operator|.
name|ClientTransportActionModule
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
name|InternalTransportClient
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
name|CacheRecycler
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
name|ImmutableList
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
name|CachedStreams
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
name|ImmutableSettings
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
name|thread
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
name|InternalSettingsPerparer
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
name|TransportSearchModule
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
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The transport client allows to create a client that is not part of the cluster, but simply connects to one  * or more nodes directly by adding their respective addresses using {@link #addTransportAddress(org.elasticsearch.common.transport.TransportAddress)}.  *  *<p>The transport client important modules used is the {@link org.elasticsearch.transport.TransportModule} which is  * started in client mode (only connects, no bind).  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportClient
specifier|public
class|class
name|TransportClient
extends|extends
name|AbstractClient
block|{
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
DECL|field|nodesService
specifier|private
specifier|final
name|TransportClientNodesService
name|nodesService
decl_stmt|;
DECL|field|internalClient
specifier|private
specifier|final
name|InternalTransportClient
name|internalClient
decl_stmt|;
comment|/**      * Constructs a new transport client with settings loaded either from the classpath or the file system (the      *<tt>elasticsearch.(yml|json)</tt> files optionally prefixed with<tt>config/</tt>).      */
DECL|method|TransportClient
specifier|public
name|TransportClient
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|this
argument_list|(
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new transport client with explicit settings and settings loaded either from the classpath or the file      * system (the<tt>elasticsearch.(yml|json)</tt> files optionally prefixed with<tt>config/</tt>).      */
DECL|method|TransportClient
specifier|public
name|TransportClient
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
argument_list|(
name|settings
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new transport client with explicit settings and settings loaded either from the classpath or the file      * system (the<tt>elasticsearch.(yml|json)</tt> files optionally prefixed with<tt>config/</tt>).      */
DECL|method|TransportClient
specifier|public
name|TransportClient
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|this
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new transport client with the provided settings and the ability to control if settings will      * be loaded from the classpath / file system (the<tt>elasticsearch.(yml|json)</tt> files optionally prefixed with      *<tt>config/</tt>).      *      * @param settings           The explicit settings.      * @param loadConfigSettings<tt>true</tt> if settings should be loaded from the classpath/file system.      * @throws ElasticSearchException      */
DECL|method|TransportClient
specifier|public
name|TransportClient
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|,
name|boolean
name|loadConfigSettings
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|this
argument_list|(
name|settings
operator|.
name|build
argument_list|()
argument_list|,
name|loadConfigSettings
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new transport client with the provided settings and the ability to control if settings will      * be loaded from the classpath / file system (the<tt>elasticsearch.(yml|json)</tt> files optionally prefixed with      *<tt>config/</tt>).      *      * @param pSettings          The explicit settings.      * @param loadConfigSettings<tt>true</tt> if settings should be loaded from the classpath/file system.      * @throws ElasticSearchException      */
DECL|method|TransportClient
specifier|public
name|TransportClient
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
name|settingsBuilder
argument_list|()
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
name|build
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
name|TransportSearchModule
argument_list|()
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
name|modules
operator|.
name|add
argument_list|(
operator|new
name|ClientTransportActionModule
argument_list|()
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
name|injector
operator|=
name|modules
operator|.
name|createInjector
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
name|internalClient
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|InternalTransportClient
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the current registered transport addresses to use (added using      * {@link #addTransportAddress(org.elasticsearch.common.transport.TransportAddress)}.      */
DECL|method|transportAddresses
specifier|public
name|ImmutableList
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
comment|/**      * Returns the current connected transport nodes that this client will use.      *      *<p>The nodes include all the nodes that are currently alive based on the transport      * addresses provided.      */
DECL|method|connectedNodes
specifier|public
name|ImmutableList
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
comment|/**      * Adds a transport address that will be used to connect to.      *      *<p>The Node this transport address represents will be used if its possible to connect to it.      * If it is unavailable, it will be automatically connected to once it is up.      *      *<p>In order to get the list of all the current connected nodes, please see {@link #connectedNodes()}.      */
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
name|addTransportAddress
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
DECL|method|close
annotation|@
name|Override
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
name|CacheRecycler
operator|.
name|clear
argument_list|()
expr_stmt|;
name|CachedStreams
operator|.
name|clear
argument_list|()
expr_stmt|;
name|ThreadLocals
operator|.
name|clearReferencesThreadLocals
argument_list|()
expr_stmt|;
block|}
DECL|method|threadPool
annotation|@
name|Override
specifier|public
name|ThreadPool
name|threadPool
parameter_list|()
block|{
return|return
name|internalClient
operator|.
name|threadPool
argument_list|()
return|;
block|}
DECL|method|admin
annotation|@
name|Override
specifier|public
name|AdminClient
name|admin
parameter_list|()
block|{
return|return
name|internalClient
operator|.
name|admin
argument_list|()
return|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|index
parameter_list|(
name|IndexRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|index
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|void
name|index
parameter_list|(
name|IndexRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|index
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|delete
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteResponse
argument_list|>
name|delete
parameter_list|(
name|DeleteRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|delete
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|delete
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|(
name|DeleteRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|delete
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|bulk
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|bulk
parameter_list|(
name|BulkRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|bulk
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|bulk
annotation|@
name|Override
specifier|public
name|void
name|bulk
parameter_list|(
name|BulkRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|bulk
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteByQuery
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|deleteByQuery
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|deleteByQuery
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|deleteByQuery
annotation|@
name|Override
specifier|public
name|void
name|deleteByQuery
parameter_list|(
name|DeleteByQueryRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|deleteByQuery
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|get
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|GetResponse
argument_list|>
name|get
parameter_list|(
name|GetRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|get
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|get
annotation|@
name|Override
specifier|public
name|void
name|get
parameter_list|(
name|GetRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|get
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|multiGet
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|MultiGetResponse
argument_list|>
name|multiGet
parameter_list|(
name|MultiGetRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|multiGet
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|multiGet
annotation|@
name|Override
specifier|public
name|void
name|multiGet
parameter_list|(
name|MultiGetRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|MultiGetResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|multiGet
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|count
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|CountResponse
argument_list|>
name|count
parameter_list|(
name|CountRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|count
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|count
annotation|@
name|Override
specifier|public
name|void
name|count
parameter_list|(
name|CountRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|CountResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|count
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|search
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|search
parameter_list|(
name|SearchRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|search
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|search
annotation|@
name|Override
specifier|public
name|void
name|search
parameter_list|(
name|SearchRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|search
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|searchScroll
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|searchScroll
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|searchScroll
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|searchScroll
annotation|@
name|Override
specifier|public
name|void
name|searchScroll
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|searchScroll
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|moreLikeThis
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SearchResponse
argument_list|>
name|moreLikeThis
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|moreLikeThis
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|moreLikeThis
annotation|@
name|Override
specifier|public
name|void
name|moreLikeThis
parameter_list|(
name|MoreLikeThisRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|moreLikeThis
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|percolate
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|PercolateResponse
argument_list|>
name|percolate
parameter_list|(
name|PercolateRequest
name|request
parameter_list|)
block|{
return|return
name|internalClient
operator|.
name|percolate
argument_list|(
name|request
argument_list|)
return|;
block|}
DECL|method|percolate
annotation|@
name|Override
specifier|public
name|void
name|percolate
parameter_list|(
name|PercolateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|internalClient
operator|.
name|percolate
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

