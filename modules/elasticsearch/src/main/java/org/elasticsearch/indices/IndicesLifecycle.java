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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * A global component allowing to register for lifecycle of an index (create/closed) and  * an index shard (created/closed).  *  * @author kimchy (shay.banon)  */
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
comment|/**          * Called before the index get closed.          *          * @param indexService The index service          * @param delete       Does the index gets closed because of a delete command, or because the node is shutting down          */
DECL|method|beforeIndexClosed
specifier|public
name|void
name|beforeIndexClosed
parameter_list|(
name|IndexService
name|indexService
parameter_list|,
name|boolean
name|delete
parameter_list|)
block|{          }
comment|/**          * Called after the index has been closed.          *          * @param index  The index          * @param delete Does the index gets closed because of a delete command, or because the node is shutting down          */
DECL|method|afterIndexClosed
specifier|public
name|void
name|afterIndexClosed
parameter_list|(
name|Index
name|index
parameter_list|,
name|boolean
name|delete
parameter_list|)
block|{          }
comment|/**          * Called before the index shard gets closed.          *          * @param indexShard The index shard          * @param delete     Does the index shard gets closed because of a delete command, or because the node is shutting down          */
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
name|boolean
name|delete
parameter_list|)
block|{          }
comment|/**          * Called after the index shard has been closed.          *          * @param shardId The shard id          * @param delete  Does the index shard gets closed because of a delete command, or because the node is shutting down          */
DECL|method|afterIndexShardClosed
specifier|public
name|void
name|afterIndexShardClosed
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|boolean
name|delete
parameter_list|)
block|{          }
block|}
block|}
end_interface

end_unit

