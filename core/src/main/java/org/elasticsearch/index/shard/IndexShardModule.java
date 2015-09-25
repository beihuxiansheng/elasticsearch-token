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
name|metadata
operator|.
name|IndexMetaData
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
name|AbstractModule
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
name|multibindings
operator|.
name|Multibinder
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
name|index
operator|.
name|engine
operator|.
name|IndexSearcherWrapper
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
name|IndexSearcherWrappingService
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
name|engine
operator|.
name|InternalEngineFactory
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
name|stats
operator|.
name|ShardPercolateService
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
name|termvectors
operator|.
name|ShardTermVectorsService
import|;
end_import

begin_comment
comment|/**  * The {@code IndexShardModule} module is responsible for binding the correct  * shard id, index shard, engine factory, and warming service for a newly  * created shard.  */
end_comment

begin_class
DECL|class|IndexShardModule
specifier|public
class|class
name|IndexShardModule
extends|extends
name|AbstractModule
block|{
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|primary
specifier|private
specifier|final
name|boolean
name|primary
decl_stmt|;
comment|// pkg private so tests can mock
DECL|field|engineFactoryImpl
name|Class
argument_list|<
name|?
extends|extends
name|EngineFactory
argument_list|>
name|engineFactoryImpl
init|=
name|InternalEngineFactory
operator|.
name|class
decl_stmt|;
DECL|method|IndexShardModule
specifier|public
name|IndexShardModule
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|boolean
name|primary
parameter_list|,
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
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
literal|"index.translog.type"
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"a custom translog type is no longer supported. got ["
operator|+
name|settings
operator|.
name|get
argument_list|(
literal|"index.translog.type"
argument_list|)
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/** Return true if a shadow engine should be used */
DECL|method|useShadowEngine
specifier|protected
name|boolean
name|useShadowEngine
parameter_list|()
block|{
return|return
name|primary
operator|==
literal|false
operator|&&
name|IndexMetaData
operator|.
name|isIndexUsingShadowReplicas
argument_list|(
name|settings
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|ShardId
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
if|if
condition|(
name|useShadowEngine
argument_list|()
condition|)
block|{
name|bind
argument_list|(
name|IndexShard
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|ShadowIndexShard
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|bind
argument_list|(
name|IndexShard
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
block|}
name|bind
argument_list|(
name|EngineFactory
operator|.
name|class
argument_list|)
operator|.
name|to
argument_list|(
name|engineFactoryImpl
argument_list|)
expr_stmt|;
name|bind
argument_list|(
name|StoreRecoveryService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|ShardPercolateService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|ShardTermVectorsService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
name|bind
argument_list|(
name|IndexSearcherWrappingService
operator|.
name|class
argument_list|)
operator|.
name|asEagerSingleton
argument_list|()
expr_stmt|;
comment|// this injects an empty set in IndexSearcherWrappingService, otherwise guice can't construct IndexSearcherWrappingService
name|Multibinder
argument_list|<
name|IndexSearcherWrapper
argument_list|>
name|multibinder
init|=
name|Multibinder
operator|.
name|newSetBinder
argument_list|(
name|binder
argument_list|()
argument_list|,
name|IndexSearcherWrapper
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

