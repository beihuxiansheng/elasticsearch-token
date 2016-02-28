begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|Setting
operator|.
name|SettingsProperty
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
name|IndexModule
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
name|shard
operator|.
name|IndexEventListener
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
name|IndexShardState
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
name|plugins
operator|.
name|Plugin
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

begin_comment
comment|/**  * This is a testing plugin that registers a generic {@link org.elasticsearch.test.MockIndexEventListener.TestEventListener} as a node level service as well as a listener  * on every index. Tests can access it like this:  *<pre>  *     TestEventListener listener = internalCluster().getInstance(MockIndexEventListener.TestEventListener.class, node1);  *     listener.setNewDelegate(new IndexEventListener() {  *        // do some stuff  *     });  *</pre>  * This allows tests to use the listener without registering their own plugins.  */
end_comment

begin_class
DECL|class|MockIndexEventListener
specifier|public
specifier|final
class|class
name|MockIndexEventListener
block|{
DECL|class|TestPlugin
specifier|public
specifier|static
class|class
name|TestPlugin
extends|extends
name|Plugin
block|{
DECL|field|listener
specifier|private
specifier|final
name|TestEventListener
name|listener
init|=
operator|new
name|TestEventListener
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"mock-index-listener"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"a mock index listener for testing only"
return|;
block|}
comment|/**          * For tests to pass in to fail on listener invocation          */
DECL|field|INDEX_FAIL
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|INDEX_FAIL
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.fail"
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|IndexScope
argument_list|)
decl_stmt|;
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|SettingsModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|registerSetting
argument_list|(
name|INDEX_FAIL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onIndexModule
specifier|public
name|void
name|onIndexModule
parameter_list|(
name|IndexModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|addIndexEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nodeModules
specifier|public
name|Collection
argument_list|<
name|Module
argument_list|>
name|nodeModules
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|binder
lambda|->
name|binder
operator|.
name|bind
argument_list|(
name|TestEventListener
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|listener
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|TestEventListener
specifier|public
specifier|static
class|class
name|TestEventListener
implements|implements
name|IndexEventListener
block|{
DECL|field|delegate
specifier|private
specifier|volatile
name|IndexEventListener
name|delegate
init|=
operator|new
name|IndexEventListener
argument_list|()
block|{}
decl_stmt|;
DECL|method|setNewDelegate
specifier|public
name|void
name|setNewDelegate
parameter_list|(
name|IndexEventListener
name|listener
parameter_list|)
block|{
name|delegate
operator|=
name|listener
operator|==
literal|null
condition|?
operator|new
name|IndexEventListener
argument_list|()
block|{}
else|:
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shardRoutingChanged
specifier|public
name|void
name|shardRoutingChanged
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|,
annotation|@
name|Nullable
name|ShardRouting
name|oldRouting
parameter_list|,
name|ShardRouting
name|newRouting
parameter_list|)
block|{
name|delegate
operator|.
name|shardRoutingChanged
argument_list|(
name|indexShard
argument_list|,
name|oldRouting
argument_list|,
name|newRouting
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexShardCreated
specifier|public
name|void
name|afterIndexShardCreated
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexShardCreated
argument_list|(
name|indexShard
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexShardStarted
specifier|public
name|void
name|afterIndexShardStarted
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexShardStarted
argument_list|(
name|indexShard
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexShardClosed
specifier|public
name|void
name|beforeIndexShardClosed
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|Nullable
name|IndexShard
name|indexShard
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexShardClosed
argument_list|(
name|shardId
argument_list|,
name|indexShard
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexShardClosed
specifier|public
name|void
name|afterIndexShardClosed
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|Nullable
name|IndexShard
name|indexShard
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexShardClosed
argument_list|(
name|shardId
argument_list|,
name|indexShard
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indexShardStateChanged
specifier|public
name|void
name|indexShardStateChanged
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|,
annotation|@
name|Nullable
name|IndexShardState
name|previousState
parameter_list|,
name|IndexShardState
name|currentState
parameter_list|,
annotation|@
name|Nullable
name|String
name|reason
parameter_list|)
block|{
name|delegate
operator|.
name|indexShardStateChanged
argument_list|(
name|indexShard
argument_list|,
name|previousState
argument_list|,
name|currentState
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onShardInactive
specifier|public
name|void
name|onShardInactive
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|delegate
operator|.
name|onShardInactive
argument_list|(
name|indexShard
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexCreated
specifier|public
name|void
name|beforeIndexCreated
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexCreated
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexCreated
specifier|public
name|void
name|afterIndexCreated
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexCreated
argument_list|(
name|indexService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexShardCreated
specifier|public
name|void
name|beforeIndexShardCreated
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexShardCreated
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexClosed
specifier|public
name|void
name|beforeIndexClosed
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexClosed
argument_list|(
name|indexService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexClosed
specifier|public
name|void
name|afterIndexClosed
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexClosed
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexShardDeleted
specifier|public
name|void
name|beforeIndexShardDeleted
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexShardDeleted
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexShardDeleted
specifier|public
name|void
name|afterIndexShardDeleted
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexShardDeleted
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|afterIndexDeleted
specifier|public
name|void
name|afterIndexDeleted
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|afterIndexDeleted
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexDeleted
specifier|public
name|void
name|beforeIndexDeleted
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexDeleted
argument_list|(
name|indexService
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beforeIndexAddedToCluster
specifier|public
name|void
name|beforeIndexAddedToCluster
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|delegate
operator|.
name|beforeIndexAddedToCluster
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

