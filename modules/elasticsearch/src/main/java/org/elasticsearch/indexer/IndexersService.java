begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indexer
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
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
name|ImmutableSet
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
name|MapBuilder
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
name|Maps
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
name|indexer
operator|.
name|cluster
operator|.
name|IndexerClusterChangedEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|cluster
operator|.
name|IndexerClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|cluster
operator|.
name|IndexerClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|cluster
operator|.
name|IndexerClusterStateListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|metadata
operator|.
name|IndexerMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|routing
operator|.
name|IndexerRouting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indexer
operator|.
name|settings
operator|.
name|IndexerSettingsModule
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexersService
specifier|public
class|class
name|IndexersService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|IndexersService
argument_list|>
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|injector
specifier|private
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|indexersInjectors
specifier|private
specifier|final
name|Map
argument_list|<
name|IndexerName
argument_list|,
name|Injector
argument_list|>
name|indexersInjectors
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|indexers
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|IndexerName
argument_list|,
name|Indexer
argument_list|>
name|indexers
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|method|IndexersService
annotation|@
name|Inject
specifier|public
name|IndexersService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndexerClusterService
name|indexerClusterService
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
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|indexerClusterService
operator|.
name|add
argument_list|(
operator|new
name|ApplyIndexers
argument_list|()
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
block|{     }
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
name|ImmutableSet
argument_list|<
name|IndexerName
argument_list|>
name|indices
init|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|this
operator|.
name|indexers
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|indices
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|IndexerName
name|indexerName
range|:
name|indices
control|)
block|{
name|threadPool
operator|.
name|cached
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|deleteIndexer
argument_list|(
name|indexerName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to delete indexer on stop [{}]/[{}]"
argument_list|,
name|e
argument_list|,
name|indexerName
operator|.
name|type
argument_list|()
argument_list|,
name|indexerName
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
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
block|{     }
DECL|method|createIndexer
specifier|public
specifier|synchronized
name|Indexer
name|createIndexer
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|,
name|Settings
name|settings
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|indexersInjectors
operator|.
name|containsKey
argument_list|(
name|indexerName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexerException
argument_list|(
name|indexerName
argument_list|,
literal|"indexer already exists"
argument_list|)
throw|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"creating indexer [{}][{}]"
argument_list|,
name|indexerName
operator|.
name|type
argument_list|()
argument_list|,
name|indexerName
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|Settings
name|indexerSettings
init|=
name|settingsBuilder
argument_list|()
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
name|IndexerNameModule
argument_list|(
name|indexerName
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexerSettingsModule
argument_list|(
name|indexerSettings
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|IndexerModule
argument_list|(
name|indexerName
argument_list|,
name|indexerSettings
argument_list|)
argument_list|)
expr_stmt|;
name|Injector
name|indexInjector
init|=
name|modules
operator|.
name|createChildInjector
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|indexersInjectors
operator|.
name|put
argument_list|(
name|indexerName
argument_list|,
name|indexInjector
argument_list|)
expr_stmt|;
name|Indexer
name|indexer
init|=
name|indexInjector
operator|.
name|getInstance
argument_list|(
name|Indexer
operator|.
name|class
argument_list|)
decl_stmt|;
name|indexers
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|indexers
argument_list|)
operator|.
name|put
argument_list|(
name|indexerName
argument_list|,
name|indexer
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
return|return
name|indexer
return|;
block|}
DECL|method|cleanIndexer
specifier|public
specifier|synchronized
name|void
name|cleanIndexer
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteIndexer
argument_list|(
name|indexerName
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteIndexer
specifier|public
specifier|synchronized
name|void
name|deleteIndexer
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteIndexer
argument_list|(
name|indexerName
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteIndexer
specifier|private
name|void
name|deleteIndexer
parameter_list|(
name|IndexerName
name|indexerName
parameter_list|,
name|boolean
name|delete
parameter_list|)
block|{
name|Injector
name|indexerInjector
decl_stmt|;
name|Indexer
name|indexer
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|indexerInjector
operator|=
name|indexersInjectors
operator|.
name|remove
argument_list|(
name|indexerName
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexerInjector
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
name|IndexerException
argument_list|(
name|indexerName
argument_list|,
literal|"missing"
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
literal|"deleting indexer [{}][{}]"
argument_list|,
name|indexerName
operator|.
name|type
argument_list|()
argument_list|,
name|indexerName
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|IndexerName
argument_list|,
name|Indexer
argument_list|>
name|tmpMap
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|indexers
argument_list|)
decl_stmt|;
name|indexer
operator|=
name|tmpMap
operator|.
name|remove
argument_list|(
name|indexerName
argument_list|)
expr_stmt|;
name|indexers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tmpMap
argument_list|)
expr_stmt|;
block|}
comment|//        for (Class<? extends CloseableIndexerComponent> closeable : pluginsService.indexServices()) {
comment|//            indexerInjector.getInstance(closeable).close(delete);
comment|//        }
name|indexer
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|indexerInjector
operator|.
name|getInstance
argument_list|(
name|Indexer
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
block|}
DECL|class|ApplyIndexers
specifier|private
class|class
name|ApplyIndexers
implements|implements
name|IndexerClusterStateListener
block|{
DECL|method|indexerClusterChanged
annotation|@
name|Override
specifier|public
name|void
name|indexerClusterChanged
parameter_list|(
name|IndexerClusterChangedEvent
name|event
parameter_list|)
block|{
name|DiscoveryNode
name|localNode
init|=
name|clusterService
operator|.
name|localNode
argument_list|()
decl_stmt|;
name|IndexerClusterState
name|state
init|=
name|event
operator|.
name|state
argument_list|()
decl_stmt|;
comment|// first, go over and delete ones that either don't exists or are not allocated
for|for
control|(
name|IndexerName
name|indexerName
range|:
name|indexers
operator|.
name|keySet
argument_list|()
control|)
block|{
comment|// if its not on the metadata, it was deleted, delete it
name|IndexerMetaData
name|indexerMetaData
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indexer
argument_list|(
name|indexerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexerMetaData
operator|==
literal|null
condition|)
block|{
name|deleteIndexer
argument_list|(
name|indexerName
argument_list|)
expr_stmt|;
block|}
name|IndexerRouting
name|routing
init|=
name|state
operator|.
name|routing
argument_list|()
operator|.
name|routing
argument_list|(
name|indexerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|routing
operator|==
literal|null
operator|||
operator|!
name|localNode
operator|.
name|equals
argument_list|(
name|routing
operator|.
name|node
argument_list|()
argument_list|)
condition|)
block|{
comment|// not routed at all, and not allocated here, clean it (we delete the relevant ones before)
name|cleanIndexer
argument_list|(
name|indexerName
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|IndexerRouting
name|routing
range|:
name|state
operator|.
name|routing
argument_list|()
control|)
block|{
comment|// only apply changes to the local node
if|if
condition|(
operator|!
name|routing
operator|.
name|node
argument_list|()
operator|.
name|equals
argument_list|(
name|localNode
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|IndexerMetaData
name|indexerMetaData
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|indexer
argument_list|(
name|routing
operator|.
name|indexerName
argument_list|()
argument_list|)
decl_stmt|;
name|createIndexer
argument_list|(
name|indexerMetaData
operator|.
name|indexerName
argument_list|()
argument_list|,
name|indexerMetaData
operator|.
name|settings
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

