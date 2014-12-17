begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|store
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|StoreRateLimiting
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
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Index store is an index level information of the {@link Store} each shard will use.  */
end_comment

begin_interface
DECL|interface|IndexStore
specifier|public
interface|interface
name|IndexStore
extends|extends
name|Closeable
block|{
comment|/**      * Returns the rate limiting, either of the index is explicitly configured, or      * the node level one (defaults to the node level one).      */
DECL|method|rateLimiting
name|StoreRateLimiting
name|rateLimiting
parameter_list|()
function_decl|;
comment|/**      * The shard store class that should be used for each shard.      */
DECL|method|shardDirectory
name|Class
argument_list|<
name|?
extends|extends
name|DirectoryService
argument_list|>
name|shardDirectory
parameter_list|()
function_decl|;
comment|/**      * Returns<tt>true</tt> if this shard is allocated on this node. Allocated means      * that it has storage files that can be deleted using {@link #deleteUnallocated(org.elasticsearch.index.shard.ShardId)}.      */
DECL|method|canDeleteUnallocated
name|boolean
name|canDeleteUnallocated
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
function_decl|;
comment|/**      * Deletes this shard store since its no longer allocated.      */
DECL|method|deleteUnallocated
name|void
name|deleteUnallocated
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|shardIndexLocations
name|Path
index|[]
name|shardIndexLocations
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

