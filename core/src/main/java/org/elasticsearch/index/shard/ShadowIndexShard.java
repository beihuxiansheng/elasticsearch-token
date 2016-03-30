begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
package|;
end_package

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
name|ShardRouting
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
name|index
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
name|EngineConfig
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
name|EngineFactory
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|MergeStats
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
name|SearchSlowLog
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
name|translog
operator|.
name|TranslogStats
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

begin_comment
comment|/**  * ShadowIndexShard extends {@link IndexShard} to add file synchronization  * from the primary when a flush happens. It also ensures that a replica being  * promoted to a primary causes the shard to fail, kicking off a re-allocation  * of the primary shard.  */
end_comment

begin_class
DECL|class|ShadowIndexShard
specifier|public
specifier|final
class|class
name|ShadowIndexShard
extends|extends
name|IndexShard
block|{
DECL|method|ShadowIndexShard
specifier|public
name|ShadowIndexShard
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|IndexSettings
name|indexSettings
parameter_list|,
name|ShardPath
name|path
parameter_list|,
name|Store
name|store
parameter_list|,
name|IndexCache
name|indexCache
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|SimilarityService
name|similarityService
parameter_list|,
name|IndexFieldDataService
name|indexFieldDataService
parameter_list|,
annotation|@
name|Nullable
name|EngineFactory
name|engineFactory
parameter_list|,
name|IndexEventListener
name|indexEventListener
parameter_list|,
name|IndexSearcherWrapper
name|wrapper
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|Engine
operator|.
name|Warmer
name|engineWarmer
parameter_list|,
name|List
argument_list|<
name|SearchOperationListener
argument_list|>
name|searchOperationListeners
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|path
argument_list|,
name|store
argument_list|,
name|indexCache
argument_list|,
name|mapperService
argument_list|,
name|similarityService
argument_list|,
name|indexFieldDataService
argument_list|,
name|engineFactory
argument_list|,
name|indexEventListener
argument_list|,
name|wrapper
argument_list|,
name|threadPool
argument_list|,
name|bigArrays
argument_list|,
name|engineWarmer
argument_list|,
name|searchOperationListeners
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * In addition to the regular accounting done in      * {@link IndexShard#updateRoutingEntry(ShardRouting, boolean)},      * if this shadow replica needs to be promoted to a primary, the shard is      * failed in order to allow a new primary to be re-allocated.      */
annotation|@
name|Override
DECL|method|updateRoutingEntry
specifier|public
name|void
name|updateRoutingEntry
parameter_list|(
name|ShardRouting
name|newRouting
parameter_list|,
name|boolean
name|persistState
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|newRouting
operator|.
name|primary
argument_list|()
operator|==
literal|true
condition|)
block|{
comment|// becoming a primary
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't promote shard to primary"
argument_list|)
throw|;
block|}
name|super
operator|.
name|updateRoutingEntry
argument_list|(
name|newRouting
argument_list|,
name|persistState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|mergeStats
specifier|public
name|MergeStats
name|mergeStats
parameter_list|()
block|{
return|return
operator|new
name|MergeStats
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|canIndex
specifier|public
name|boolean
name|canIndex
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|newEngine
specifier|protected
name|Engine
name|newEngine
parameter_list|(
name|EngineConfig
name|config
parameter_list|)
block|{
assert|assert
name|this
operator|.
name|shardRouting
operator|.
name|primary
argument_list|()
operator|==
literal|false
assert|;
name|config
operator|.
name|setCreate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// hardcoded - we always expect an index to be present
return|return
name|engineFactory
operator|.
name|newReadOnlyEngine
argument_list|(
name|config
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shouldFlush
specifier|public
name|boolean
name|shouldFlush
parameter_list|()
block|{
comment|// we don't need to flush since we don't write - all dominated by the primary
return|return
literal|false
return|;
block|}
DECL|method|allowsPrimaryPromotion
specifier|public
name|boolean
name|allowsPrimaryPromotion
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|translogStats
specifier|public
name|TranslogStats
name|translogStats
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// shadow engine has no translog
block|}
block|}
end_class

end_unit

