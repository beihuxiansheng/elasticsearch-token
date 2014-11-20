begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|index
operator|.
name|shard
operator|.
name|service
operator|.
name|IndexShard
import|;
end_import

begin_comment
comment|/**  * A global component allowing to register for lifecycle of an index (create/closed) and  * an index shard (created/closed).  */
end_comment

begin_interface
DECL|interface|IndicesLifecycle
specifier|public
interface|interface
name|IndicesLifecycle
block|{
comment|/**      * Add a listener.      */
DECL|method|addListener
name|void
name|addListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Remove a listener.      */
DECL|method|removeListener
name|void
name|removeListener
parameter_list|(
name|Listener
name|listener
parameter_list|)
function_decl|;
comment|/**      * A listener for index and index shard lifecycle events (create/closed).      */
DECL|class|Listener
specifier|public
specifier|abstract
specifier|static
class|class
name|Listener
block|{
comment|/**          * Called when the shard routing has changed state.          *          * @param indexShard The index shard          * @param oldRouting The old routing state (can be null)          * @param newRouting The new routing state          */
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
block|{          }
comment|/**          * Called before the index gets created.          */
DECL|method|beforeIndexCreated
specifier|public
name|void
name|beforeIndexCreated
parameter_list|(
name|Index
name|index
parameter_list|)
block|{          }
comment|/**          * Called after the index has been created.          */
DECL|method|afterIndexCreated
specifier|public
name|void
name|afterIndexCreated
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{          }
comment|/**          * Called before the index shard gets created.          */
DECL|method|beforeIndexShardCreated
specifier|public
name|void
name|beforeIndexShardCreated
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{          }
comment|/**          * Called after the index shard has been created.          */
DECL|method|afterIndexShardCreated
specifier|public
name|void
name|afterIndexShardCreated
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{          }
DECL|method|afterIndexShardPostRecovery
specifier|public
name|void
name|afterIndexShardPostRecovery
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{          }
comment|/**          * Called after the index shard has been started.          */
DECL|method|afterIndexShardStarted
specifier|public
name|void
name|afterIndexShardStarted
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{          }
comment|/**          * Called before the index get closed.          *          * @param indexService The index service          */
DECL|method|beforeIndexClosed
specifier|public
name|void
name|beforeIndexClosed
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{          }
comment|/**          * Called after the index has been closed.          *          * @param index The index          */
DECL|method|afterIndexClosed
specifier|public
name|void
name|afterIndexClosed
parameter_list|(
name|Index
name|index
parameter_list|)
block|{          }
comment|/**          * Called before the index shard gets closed.          *          * @param indexShard The index shard          */
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
parameter_list|)
block|{          }
comment|/**          * Called after the index shard has been closed.          *          * @param shardId The shard id          */
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
parameter_list|)
block|{          }
comment|/**          * Called after a shard's {@link org.elasticsearch.index.shard.IndexShardState} changes.          * The order of concurrent events is preserved. The execution must be lightweight.          *          * @param indexShard the shard the new state was applied to          * @param previousState the previous index shard state if there was one, null otherwise          * @param currentState the new shard state          * @param reason the reason for the state change if there is one, null otherwise          */
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
block|{          }
comment|/**          * Called after the index has been deleted.          * This listener method is invoked after {@link #afterIndexClosed(org.elasticsearch.index.Index)}          * when an index is deleted          *          * @param index The index          */
DECL|method|afterIndexDeleted
specifier|public
name|void
name|afterIndexDeleted
parameter_list|(
name|Index
name|index
parameter_list|)
block|{          }
comment|/**          * Called before the index gets deleted.          * This listener method is invoked after          * {@link #beforeIndexClosed(org.elasticsearch.index.service.IndexService)} when an index is deleted          *          * @param indexService The index service          */
DECL|method|beforeIndexDeleted
specifier|public
name|void
name|beforeIndexDeleted
parameter_list|(
name|IndexService
name|indexService
parameter_list|)
block|{          }
block|}
block|}
end_interface

end_unit

