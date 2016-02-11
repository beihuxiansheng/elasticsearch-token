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
name|IndexService
import|;
end_import

begin_comment
comment|/**  * An index event listener is the primary extension point for plugins and build-in services  * to react / listen to per-index and per-shard events. These listeners are registered per-index  * via {@link org.elasticsearch.index.IndexModule#addIndexEventListener(IndexEventListener)}. All listeners have the same  * lifecycle as the {@link IndexService} they are created for.  *<p>  * An IndexEventListener can be used across multiple indices and shards since all callback methods receive sufficient  * local state via their arguments. Yet, if an instance is shared across indices they might be called concurrently and should not  * modify local state without sufficient synchronization.  *</p>  */
end_comment

begin_interface
DECL|interface|IndexEventListener
specifier|public
interface|interface
name|IndexEventListener
block|{
comment|/**      * Called when the shard routing has changed state.      *      * @param indexShard The index shard      * @param oldRouting The old routing state (can be null)      * @param newRouting The new routing state      */
DECL|method|shardRoutingChanged
specifier|default
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
block|{}
comment|/**      * Called after the index shard has been created.      */
DECL|method|afterIndexShardCreated
specifier|default
name|void
name|afterIndexShardCreated
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{}
comment|/**      * Called after the index shard has been started.      */
DECL|method|afterIndexShardStarted
specifier|default
name|void
name|afterIndexShardStarted
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{}
comment|/**      * Called before the index shard gets closed.      *      * @param indexShard The index shard      */
DECL|method|beforeIndexShardClosed
specifier|default
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
block|{}
comment|/**      * Called after the index shard has been closed.      *      * @param shardId The shard id      */
DECL|method|afterIndexShardClosed
specifier|default
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
block|{}
comment|/**      * Called after a shard's {@link org.elasticsearch.index.shard.IndexShardState} changes.      * The order of concurrent events is preserved. The execution must be lightweight.      *      * @param indexShard the shard the new state was applied to      * @param previousState the previous index shard state if there was one, null otherwise      * @param currentState the new shard state      * @param reason the reason for the state change if there is one, null otherwise      */
DECL|method|indexShardStateChanged
specifier|default
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
block|{}
comment|/**      * Called when a shard is marked as inactive      *      * @param indexShard The shard that was marked inactive      */
DECL|method|onShardInactive
specifier|default
name|void
name|onShardInactive
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{}
comment|/**      * Called before the index gets created. Note that this is also called      * when the index is created on data nodes      */
DECL|method|beforeIndexCreated
specifier|default
name|void
name|beforeIndexCreated
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{      }
comment|/**      * Called after the index has been created.      */
DECL|method|afterIndexCreated
specifier|default
name|void
name|afterIndexCreated
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{      }
comment|/**      * Called before the index shard gets created.      */
DECL|method|beforeIndexShardCreated
specifier|default
name|void
name|beforeIndexShardCreated
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{     }
comment|/**      * Called before the index get closed.      *      * @param indexService The index service      */
DECL|method|beforeIndexClosed
specifier|default
name|void
name|beforeIndexClosed
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{      }
comment|/**      * Called after the index has been closed.      *      * @param index The index      */
DECL|method|afterIndexClosed
specifier|default
name|void
name|afterIndexClosed
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{      }
comment|/**      * Called before the index shard gets deleted from disk      * Note: this method is only executed on the first attempt of deleting the shard. Retries are will not invoke      * this method.      * @param shardId The shard id      * @param indexSettings the shards index settings      */
DECL|method|beforeIndexShardDeleted
specifier|default
name|void
name|beforeIndexShardDeleted
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{     }
comment|/**      * Called after the index shard has been deleted from disk.      *      * Note: this method is only called if the deletion of the shard did finish without an exception      *      * @param shardId The shard id      * @param indexSettings the shards index settings      */
DECL|method|afterIndexShardDeleted
specifier|default
name|void
name|afterIndexShardDeleted
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{     }
comment|/**      * Called after the index has been deleted.      * This listener method is invoked after {@link #afterIndexClosed(org.elasticsearch.index.Index, org.elasticsearch.common.settings.Settings)}      * when an index is deleted      *      * @param index The index      */
DECL|method|afterIndexDeleted
specifier|default
name|void
name|afterIndexDeleted
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{      }
comment|/**      * Called before the index gets deleted.      * This listener method is invoked after      * {@link #beforeIndexClosed(org.elasticsearch.index.IndexService)} when an index is deleted      *      * @param indexService The index service      */
DECL|method|beforeIndexDeleted
specifier|default
name|void
name|beforeIndexDeleted
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{      }
comment|/**      * Called on the Master node only before the {@link IndexService} instances is created to simulate an index creation.      * This happens right before the index and it's metadata is registered in the cluster state      */
DECL|method|beforeIndexAddedToCluster
specifier|default
name|void
name|beforeIndexAddedToCluster
parameter_list|(
name|Index
name|index
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{     }
comment|/**      * Called when the given shards store is closed. The store is closed once all resource have been released on the store.      * This implies that all index readers are closed and no recoveries are running.      *      * @param shardId the shard ID the store belongs to      */
DECL|method|onStoreClosed
specifier|default
name|void
name|onStoreClosed
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{}
block|}
end_interface

end_unit

