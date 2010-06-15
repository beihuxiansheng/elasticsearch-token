begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|cluster
operator|.
name|ClusterState
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
name|GroupShardsIterator
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
name|ImmutableMap
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
name|UnmodifiableIterator
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
name|component
operator|.
name|CloseableIndexComponent
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
name|Inject
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
name|Injectors
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
name|gateway
operator|.
name|Gateway
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
name|*
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
name|analysis
operator|.
name|AnalysisModule
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
name|analysis
operator|.
name|AnalysisService
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
name|cache
operator|.
name|IndexCacheModule
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
name|cache
operator|.
name|filter
operator|.
name|FilterCache
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
name|engine
operator|.
name|IndexEngine
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
name|engine
operator|.
name|IndexEngineModule
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
name|gateway
operator|.
name|IndexGateway
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
name|gateway
operator|.
name|IndexGatewayModule
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
name|mapper
operator|.
name|MapperServiceModule
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
name|query
operator|.
name|IndexQueryParserModule
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
name|routing
operator|.
name|OperationRoutingModule
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
name|service
operator|.
name|IndexService
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
name|settings
operator|.
name|IndexSettingsModule
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
name|similarity
operator|.
name|SimilarityModule
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
name|analysis
operator|.
name|IndicesAnalysisService
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
name|cluster
operator|.
name|IndicesClusterStateService
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
name|IndicesPluginsModule
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
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
name|HashMap
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|*
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
name|collect
operator|.
name|Maps
operator|.
name|*
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
name|collect
operator|.
name|Sets
operator|.
name|*
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|MapBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|ThreadSafe
DECL|class|InternalIndicesService
specifier|public
class|class
name|InternalIndicesService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|IndicesService
argument_list|>
implements|implements
name|IndicesService
block|{
DECL|field|clusterStateService
specifier|private
specifier|final
name|IndicesClusterStateService
name|clusterStateService
decl_stmt|;
DECL|field|indicesLifecycle
specifier|private
specifier|final
name|InternalIndicesLifecycle
name|indicesLifecycle
decl_stmt|;
DECL|field|indicesAnalysisService
specifier|private
specifier|final
name|IndicesAnalysisService
name|indicesAnalysisService
decl_stmt|;
DECL|field|injector
specifier|private
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|pluginsService
specifier|private
specifier|final
name|PluginsService
name|pluginsService
decl_stmt|;
DECL|field|indicesInjectors
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Injector
argument_list|>
name|indicesInjectors
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Injector
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|indices
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|IndexService
argument_list|>
name|indices
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|method|InternalIndicesService
annotation|@
name|Inject
specifier|public
name|InternalIndicesService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndicesClusterStateService
name|clusterStateService
parameter_list|,
name|IndicesLifecycle
name|indicesLifecycle
parameter_list|,
name|IndicesAnalysisService
name|indicesAnalysisService
parameter_list|,
name|Injector
name|injector
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterStateService
operator|=
name|clusterStateService
expr_stmt|;
name|this
operator|.
name|indicesLifecycle
operator|=
operator|(
name|InternalIndicesLifecycle
operator|)
name|indicesLifecycle
expr_stmt|;
name|this
operator|.
name|indicesAnalysisService
operator|=
name|indicesAnalysisService
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|this
operator|.
name|pluginsService
operator|=
name|injector
operator|.
name|getInstance
argument_list|(
name|PluginsService
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clusterStateService
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clusterStateService
operator|.
name|stop
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
operator|.
name|keySet
argument_list|()
control|)
block|{
name|deleteIndex
argument_list|(
name|index
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clusterStateService
operator|.
name|close
argument_list|()
expr_stmt|;
name|indicesAnalysisService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|indicesLifecycle
annotation|@
name|Override
specifier|public
name|IndicesLifecycle
name|indicesLifecycle
parameter_list|()
block|{
return|return
name|this
operator|.
name|indicesLifecycle
return|;
block|}
comment|/**      * Returns<tt>true</tt> if changes (adding / removing) indices, shards and so on are allowed.      */
DECL|method|changesAllowed
specifier|public
name|boolean
name|changesAllowed
parameter_list|()
block|{
comment|// we check on stop here since we defined stop when we delete the indices
return|return
name|lifecycle
operator|.
name|started
argument_list|()
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|UnmodifiableIterator
argument_list|<
name|IndexService
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|indices
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|hasIndex
specifier|public
name|boolean
name|hasIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|indices
operator|.
name|containsKey
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|indices
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|()
block|{
return|return
name|newHashSet
argument_list|(
name|indices
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|indexService
specifier|public
name|IndexService
name|indexService
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|indices
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|indexServiceSafe
annotation|@
name|Override
specifier|public
name|IndexService
name|indexServiceSafe
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|IndexMissingException
block|{
name|IndexService
name|indexService
init|=
name|indexService
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|indexService
return|;
block|}
DECL|method|searchShards
annotation|@
name|Override
specifier|public
name|GroupShardsIterator
name|searchShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
index|[]
name|indexNames
parameter_list|,
name|String
name|queryHint
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|indexNames
operator|==
literal|null
operator|||
name|indexNames
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|IndexService
argument_list|>
name|indices
init|=
name|this
operator|.
name|indices
decl_stmt|;
name|indexNames
operator|=
name|indices
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indices
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
name|GroupShardsIterator
name|its
init|=
operator|new
name|GroupShardsIterator
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indexNames
control|)
block|{
name|its
operator|.
name|add
argument_list|(
name|indexServiceSafe
argument_list|(
name|index
argument_list|)
operator|.
name|operationRouting
argument_list|()
operator|.
name|searchShards
argument_list|(
name|clusterState
argument_list|,
name|queryHint
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|its
return|;
block|}
DECL|method|createIndex
specifier|public
specifier|synchronized
name|IndexService
name|createIndex
parameter_list|(
name|String
name|sIndexName
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|localNodeId
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
name|sIndexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indicesInjectors
operator|.
name|containsKey
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexAlreadyExistsException
argument_list|(
name|index
argument_list|)
throw|;
block|}
name|indicesLifecycle
operator|.
name|beforeIndexCreated
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Creating Index [{}], shards [{}]/[{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|sIndexName
block|,
name|settings
operator|.
name|get
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|)
block|,
name|settings
operator|.
name|get
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|Settings
name|indexSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"settingsType"
argument_list|,
literal|"index"
argument_list|)
operator|.
name|put
argument_list|(
name|this
operator|.
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|classLoader
argument_list|(
name|settings
operator|.
name|getClassLoader
argument_list|()
argument_list|)
operator|.
name|globalSettings
argument_list|(
name|settings
operator|.
name|getGlobalSettings
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|IndexNameModule
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|LocalNodeIdModule
argument_list|(
name|localNodeId
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexSettingsModule
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndicesPluginsModule
argument_list|(
name|indexSettings
argument_list|,
name|pluginsService
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexEngineModule
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|AnalysisModule
argument_list|(
name|indexSettings
argument_list|,
name|indicesAnalysisService
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|SimilarityModule
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexCacheModule
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexQueryParserModule
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|MapperServiceModule
argument_list|()
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexGatewayModule
argument_list|(
name|indexSettings
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|Gateway
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|OperationRoutingModule
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexModule
argument_list|()
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
name|indexInjector
init|=
name|injector
operator|.
name|createChildInjector
argument_list|(
name|modules
argument_list|)
decl_stmt|;
name|indicesInjectors
operator|.
name|put
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|,
name|indexInjector
argument_list|)
expr_stmt|;
name|IndexService
name|indexService
init|=
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|IndexService
operator|.
name|class
argument_list|)
decl_stmt|;
name|indicesLifecycle
operator|.
name|afterIndexCreated
argument_list|(
name|indexService
argument_list|)
expr_stmt|;
name|indices
operator|=
name|newMapBuilder
argument_list|(
name|indices
argument_list|)
operator|.
name|put
argument_list|(
name|index
operator|.
name|name
argument_list|()
argument_list|,
name|indexService
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
return|return
name|indexService
return|;
block|}
DECL|method|cleanIndex
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|cleanIndex
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteIndex
argument_list|(
name|index
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteIndex
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|deleteIndex
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteIndex
argument_list|(
name|index
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteIndex
specifier|private
specifier|synchronized
name|void
name|deleteIndex
parameter_list|(
name|String
name|index
parameter_list|,
name|boolean
name|delete
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|Injector
name|indexInjector
init|=
name|indicesInjectors
operator|.
name|remove
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexInjector
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|delete
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|delete
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Deleting Index [{}]"
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|IndexService
argument_list|>
name|tmpMap
init|=
name|newHashMap
argument_list|(
name|indices
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
name|tmpMap
operator|.
name|remove
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|indices
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tmpMap
argument_list|)
expr_stmt|;
name|indicesLifecycle
operator|.
name|beforeIndexClosed
argument_list|(
name|indexService
argument_list|,
name|delete
argument_list|)
expr_stmt|;
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|CloseableIndexComponent
argument_list|>
name|closeable
range|:
name|pluginsService
operator|.
name|indexServices
argument_list|()
control|)
block|{
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|closeable
argument_list|)
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
name|indexService
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|FilterCache
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|AnalysisService
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|IndexEngine
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|IndexServiceManagement
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|IndexGateway
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|Injectors
operator|.
name|close
argument_list|(
name|injector
argument_list|)
expr_stmt|;
name|indicesLifecycle
operator|.
name|afterIndexClosed
argument_list|(
name|indexService
operator|.
name|index
argument_list|()
argument_list|,
name|delete
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

