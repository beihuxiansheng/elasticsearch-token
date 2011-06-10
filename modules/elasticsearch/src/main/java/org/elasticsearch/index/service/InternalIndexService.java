begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.service
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|service
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
name|ElasticSearchIllegalStateException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchInterruptedException
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
name|Nullable
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
name|env
operator|.
name|NodeEnvironment
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
name|none
operator|.
name|NoneGateway
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
name|AbstractIndexComponent
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
name|CloseableIndexComponent
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
name|Index
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
name|IndexShardAlreadyExistsException
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
name|IndexShardMissingException
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
name|aliases
operator|.
name|IndexAliasesService
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
name|IndexCache
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
name|deletionpolicy
operator|.
name|DeletionPolicyModule
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
name|Engine
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
name|EngineModule
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
name|IndexShardGatewayModule
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
name|IndexShardGatewayService
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
name|MapperService
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
name|merge
operator|.
name|policy
operator|.
name|MergePolicyModule
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
name|merge
operator|.
name|policy
operator|.
name|MergePolicyProvider
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
name|merge
operator|.
name|scheduler
operator|.
name|MergeSchedulerModule
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
name|percolator
operator|.
name|PercolatorService
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
name|IndexQueryParserService
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
name|IndexSettings
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
name|shard
operator|.
name|IndexShardManagement
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
name|shard
operator|.
name|IndexShardModule
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
name|shard
operator|.
name|ShardId
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
name|shard
operator|.
name|service
operator|.
name|IndexShard
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
name|shard
operator|.
name|service
operator|.
name|InternalIndexShard
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
name|SimilarityService
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
name|IndexStore
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
name|Store
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
name|StoreModule
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
name|translog
operator|.
name|Translog
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
name|translog
operator|.
name|TranslogModule
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
name|translog
operator|.
name|TranslogService
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
name|IndicesLifecycle
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
name|InternalIndicesLifecycle
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
name|ShardsPluginsModule
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
name|Executor
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
name|MapBuilder
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|InternalIndexService
specifier|public
class|class
name|InternalIndexService
extends|extends
name|AbstractIndexComponent
implements|implements
name|IndexService
block|{
DECL|field|injector
specifier|private
specifier|final
name|Injector
name|injector
decl_stmt|;
DECL|field|indexSettings
specifier|private
specifier|final
name|Settings
name|indexSettings
decl_stmt|;
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|pluginsService
specifier|private
specifier|final
name|PluginsService
name|pluginsService
decl_stmt|;
DECL|field|indicesLifecycle
specifier|private
specifier|final
name|InternalIndicesLifecycle
name|indicesLifecycle
decl_stmt|;
DECL|field|percolatorService
specifier|private
specifier|final
name|PercolatorService
name|percolatorService
decl_stmt|;
DECL|field|analysisService
specifier|private
specifier|final
name|AnalysisService
name|analysisService
decl_stmt|;
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|queryParserService
specifier|private
specifier|final
name|IndexQueryParserService
name|queryParserService
decl_stmt|;
DECL|field|similarityService
specifier|private
specifier|final
name|SimilarityService
name|similarityService
decl_stmt|;
DECL|field|aliasesService
specifier|private
specifier|final
name|IndexAliasesService
name|aliasesService
decl_stmt|;
DECL|field|indexCache
specifier|private
specifier|final
name|IndexCache
name|indexCache
decl_stmt|;
DECL|field|indexEngine
specifier|private
specifier|final
name|IndexEngine
name|indexEngine
decl_stmt|;
DECL|field|indexGateway
specifier|private
specifier|final
name|IndexGateway
name|indexGateway
decl_stmt|;
DECL|field|indexStore
specifier|private
specifier|final
name|IndexStore
name|indexStore
decl_stmt|;
DECL|field|shardsInjectors
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|Injector
argument_list|>
name|shardsInjectors
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|shards
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|IndexShard
argument_list|>
name|shards
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|closed
specifier|private
specifier|volatile
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|method|InternalIndexService
annotation|@
name|Inject
specifier|public
name|InternalIndexService
parameter_list|(
name|Injector
name|injector
parameter_list|,
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|PercolatorService
name|percolatorService
parameter_list|,
name|AnalysisService
name|analysisService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|IndexQueryParserService
name|queryParserService
parameter_list|,
name|SimilarityService
name|similarityService
parameter_list|,
name|IndexAliasesService
name|aliasesService
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|IndexEngine
name|indexEngine
parameter_list|,
name|IndexGateway
name|indexGateway
parameter_list|,
name|IndexStore
name|indexStore
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|injector
operator|=
name|injector
expr_stmt|;
name|this
operator|.
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|indexSettings
operator|=
name|indexSettings
expr_stmt|;
name|this
operator|.
name|percolatorService
operator|=
name|percolatorService
expr_stmt|;
name|this
operator|.
name|analysisService
operator|=
name|analysisService
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|queryParserService
operator|=
name|queryParserService
expr_stmt|;
name|this
operator|.
name|similarityService
operator|=
name|similarityService
expr_stmt|;
name|this
operator|.
name|aliasesService
operator|=
name|aliasesService
expr_stmt|;
name|this
operator|.
name|indexCache
operator|=
name|indexCache
expr_stmt|;
name|this
operator|.
name|indexEngine
operator|=
name|indexEngine
expr_stmt|;
name|this
operator|.
name|indexGateway
operator|=
name|indexGateway
expr_stmt|;
name|this
operator|.
name|indexStore
operator|=
name|indexStore
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
name|this
operator|.
name|indicesLifecycle
operator|=
operator|(
name|InternalIndicesLifecycle
operator|)
name|injector
operator|.
name|getInstance
argument_list|(
name|IndicesLifecycle
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|numberOfShards
annotation|@
name|Override
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|shards
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|UnmodifiableIterator
argument_list|<
name|IndexShard
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|shards
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|hasShard
annotation|@
name|Override
specifier|public
name|boolean
name|hasShard
parameter_list|(
name|int
name|shardId
parameter_list|)
block|{
return|return
name|shards
operator|.
name|containsKey
argument_list|(
name|shardId
argument_list|)
return|;
block|}
DECL|method|shard
annotation|@
name|Override
specifier|public
name|IndexShard
name|shard
parameter_list|(
name|int
name|shardId
parameter_list|)
block|{
return|return
name|shards
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
return|;
block|}
DECL|method|shardSafe
annotation|@
name|Override
specifier|public
name|IndexShard
name|shardSafe
parameter_list|(
name|int
name|shardId
parameter_list|)
throws|throws
name|IndexShardMissingException
block|{
name|IndexShard
name|indexShard
init|=
name|shard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShard
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|indexShard
return|;
block|}
DECL|method|shardIds
annotation|@
name|Override
specifier|public
name|ImmutableSet
argument_list|<
name|Integer
argument_list|>
name|shardIds
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|shards
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
DECL|method|injector
annotation|@
name|Override
specifier|public
name|Injector
name|injector
parameter_list|()
block|{
return|return
name|injector
return|;
block|}
DECL|method|gateway
annotation|@
name|Override
specifier|public
name|IndexGateway
name|gateway
parameter_list|()
block|{
return|return
name|indexGateway
return|;
block|}
DECL|method|store
annotation|@
name|Override
specifier|public
name|IndexStore
name|store
parameter_list|()
block|{
return|return
name|indexStore
return|;
block|}
DECL|method|cache
annotation|@
name|Override
specifier|public
name|IndexCache
name|cache
parameter_list|()
block|{
return|return
name|indexCache
return|;
block|}
DECL|method|percolateService
annotation|@
name|Override
specifier|public
name|PercolatorService
name|percolateService
parameter_list|()
block|{
return|return
name|this
operator|.
name|percolatorService
return|;
block|}
DECL|method|analysisService
annotation|@
name|Override
specifier|public
name|AnalysisService
name|analysisService
parameter_list|()
block|{
return|return
name|this
operator|.
name|analysisService
return|;
block|}
DECL|method|mapperService
annotation|@
name|Override
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|mapperService
return|;
block|}
DECL|method|queryParserService
annotation|@
name|Override
specifier|public
name|IndexQueryParserService
name|queryParserService
parameter_list|()
block|{
return|return
name|queryParserService
return|;
block|}
DECL|method|similarityService
annotation|@
name|Override
specifier|public
name|SimilarityService
name|similarityService
parameter_list|()
block|{
return|return
name|similarityService
return|;
block|}
DECL|method|aliasesService
annotation|@
name|Override
specifier|public
name|IndexAliasesService
name|aliasesService
parameter_list|()
block|{
return|return
name|aliasesService
return|;
block|}
DECL|method|engine
annotation|@
name|Override
specifier|public
name|IndexEngine
name|engine
parameter_list|()
block|{
return|return
name|indexEngine
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
specifier|final
name|boolean
name|delete
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|,
annotation|@
name|Nullable
name|Executor
name|executor
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
block|}
name|Set
argument_list|<
name|Integer
argument_list|>
name|shardIds
init|=
name|shardIds
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|shardIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|int
name|shardId
range|:
name|shardIds
control|)
block|{
name|executor
operator|=
name|executor
operator|==
literal|null
condition|?
name|threadPool
operator|.
name|cached
argument_list|()
else|:
name|executor
expr_stmt|;
name|executor
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
name|deleteShard
argument_list|(
name|shardId
argument_list|,
name|delete
argument_list|,
operator|!
name|delete
argument_list|,
name|delete
argument_list|,
name|reason
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
literal|"failed to close shard, delete [{}]"
argument_list|,
name|e
argument_list|,
name|delete
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
throw|throw
operator|new
name|ElasticSearchInterruptedException
argument_list|(
literal|"interrupted closing index [ "
operator|+
name|index
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|shardInjector
annotation|@
name|Override
specifier|public
name|Injector
name|shardInjector
parameter_list|(
name|int
name|shardId
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|shardsInjectors
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
return|;
block|}
DECL|method|shardInjectorSafe
annotation|@
name|Override
specifier|public
name|Injector
name|shardInjectorSafe
parameter_list|(
name|int
name|shardId
parameter_list|)
throws|throws
name|IndexShardMissingException
block|{
name|Injector
name|shardInjector
init|=
name|shardInjector
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardInjector
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|shardInjector
return|;
block|}
DECL|method|createShard
annotation|@
name|Override
specifier|public
specifier|synchronized
name|IndexShard
name|createShard
parameter_list|(
name|int
name|sShardId
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|closed
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Can't create shard ["
operator|+
name|index
operator|.
name|name
argument_list|()
operator|+
literal|"]["
operator|+
name|sShardId
operator|+
literal|"], closed"
argument_list|)
throw|;
block|}
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|sShardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardsInjectors
operator|.
name|containsKey
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexShardAlreadyExistsException
argument_list|(
name|shardId
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
name|indicesLifecycle
operator|.
name|beforeIndexShardCreated
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"creating shard_id [{}]"
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
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
name|ShardsPluginsModule
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
name|IndexShardModule
argument_list|(
name|shardId
argument_list|)
argument_list|)
expr_stmt|;
name|modules
operator|.
name|add
argument_list|(
operator|new
name|StoreModule
argument_list|(
name|indexSettings
argument_list|,
name|injector
operator|.
name|getInstance
argument_list|(
name|IndexStore
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
name|DeletionPolicyModule
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
name|MergePolicyModule
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
name|MergeSchedulerModule
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
name|TranslogModule
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
name|EngineModule
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
name|IndexShardGatewayModule
argument_list|(
name|injector
operator|.
name|getInstance
argument_list|(
name|IndexGateway
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Injector
name|shardInjector
init|=
name|modules
operator|.
name|createChildInjector
argument_list|(
name|injector
argument_list|)
decl_stmt|;
name|shardsInjectors
operator|=
name|newMapBuilder
argument_list|(
name|shardsInjectors
argument_list|)
operator|.
name|put
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|,
name|shardInjector
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
name|IndexShard
name|indexShard
init|=
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|IndexShard
operator|.
name|class
argument_list|)
decl_stmt|;
name|indicesLifecycle
operator|.
name|afterIndexShardCreated
argument_list|(
name|indexShard
argument_list|)
expr_stmt|;
name|shards
operator|=
name|newMapBuilder
argument_list|(
name|shards
argument_list|)
operator|.
name|put
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|,
name|indexShard
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
return|return
name|indexShard
return|;
block|}
DECL|method|cleanShard
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|cleanShard
parameter_list|(
name|int
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteShard
argument_list|(
name|shardId
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
DECL|method|removeShard
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|removeShard
parameter_list|(
name|int
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|deleteShard
argument_list|(
name|shardId
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteShard
specifier|private
name|void
name|deleteShard
parameter_list|(
name|int
name|shardId
parameter_list|,
name|boolean
name|delete
parameter_list|,
name|boolean
name|snapshotGateway
parameter_list|,
name|boolean
name|deleteGateway
parameter_list|,
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|Injector
name|shardInjector
decl_stmt|;
name|IndexShard
name|indexShard
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|Injector
argument_list|>
name|tmpShardInjectors
init|=
name|newHashMap
argument_list|(
name|shardsInjectors
argument_list|)
decl_stmt|;
name|shardInjector
operator|=
name|tmpShardInjectors
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardInjector
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
name|IndexShardMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|)
throw|;
block|}
name|shardsInjectors
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tmpShardInjectors
argument_list|)
expr_stmt|;
if|if
condition|(
name|delete
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"deleting shard_id [{}]"
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|Integer
argument_list|,
name|IndexShard
argument_list|>
name|tmpShardsMap
init|=
name|newHashMap
argument_list|(
name|shards
argument_list|)
decl_stmt|;
name|indexShard
operator|=
name|tmpShardsMap
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|shards
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tmpShardsMap
argument_list|)
expr_stmt|;
block|}
name|ShardId
name|sId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|indicesLifecycle
operator|.
name|beforeIndexShardClosed
argument_list|(
name|sId
argument_list|,
name|indexShard
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
name|shardServices
argument_list|()
control|)
block|{
try|try
block|{
name|shardInjector
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to clean plugin shard service [{}]"
argument_list|,
name|e
argument_list|,
name|closeable
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
comment|// now we can close the translog service, we need to close it before the we close the shard
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|TranslogService
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
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to close translog service"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
comment|// close shard actions
if|if
condition|(
name|indexShard
operator|!=
literal|null
condition|)
block|{
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|IndexShardManagement
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// this logic is tricky, we want to close the engine so we rollback the changes done to it
comment|// and close the shard so no operations are allowed to it
if|if
condition|(
name|indexShard
operator|!=
literal|null
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|InternalIndexShard
operator|)
name|indexShard
operator|)
operator|.
name|close
argument_list|(
name|reason
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
name|debug
argument_list|(
literal|"failed to close index shard"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
block|}
try|try
block|{
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|Engine
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
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to close engine"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
try|try
block|{
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|MergePolicyProvider
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|(
name|delete
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
name|debug
argument_list|(
literal|"failed to close merge policy provider"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
try|try
block|{
comment|// now, we can snapshot to the gateway, it will be only the translog
if|if
condition|(
name|snapshotGateway
condition|)
block|{
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|IndexShardGatewayService
operator|.
name|class
argument_list|)
operator|.
name|snapshotOnClose
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to snapshot gateway on close"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
try|try
block|{
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|IndexShardGatewayService
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|(
name|deleteGateway
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
name|debug
argument_list|(
literal|"failed to close index shard gateway"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
try|try
block|{
comment|// now we can close the translog
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|Translog
operator|.
name|class
argument_list|)
operator|.
name|close
argument_list|(
name|delete
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
name|debug
argument_list|(
literal|"failed to close translog"
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|// ignore
block|}
comment|// call this before we close the store, so we can release resources for it
name|indicesLifecycle
operator|.
name|afterIndexShardClosed
argument_list|(
name|sId
argument_list|,
name|delete
argument_list|)
expr_stmt|;
comment|// if we delete or have no gateway or the store is not persistent, clean the store...
name|Store
name|store
init|=
name|shardInjector
operator|.
name|getInstance
argument_list|(
name|Store
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|delete
operator|||
name|indexGateway
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|NoneGateway
operator|.
name|TYPE
argument_list|)
operator|||
operator|!
name|indexStore
operator|.
name|persistent
argument_list|()
condition|)
block|{
try|try
block|{
name|store
operator|.
name|fullDelete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to clean store on shard deletion"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// and close it
try|try
block|{
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to close store on shard deletion"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|Injectors
operator|.
name|close
argument_list|(
name|injector
argument_list|)
expr_stmt|;
comment|// delete the shard location if needed
if|if
condition|(
name|delete
operator|||
name|indexGateway
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|NoneGateway
operator|.
name|TYPE
argument_list|)
condition|)
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|nodeEnv
operator|.
name|shardLocation
argument_list|(
name|sId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

