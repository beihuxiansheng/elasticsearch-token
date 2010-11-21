begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Allows to iterate over a set of shard instances (routing) within a shard id group.  *  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|ShardIterator
specifier|public
interface|interface
name|ShardIterator
extends|extends
name|Iterable
argument_list|<
name|ShardRouting
argument_list|>
extends|,
name|Iterator
argument_list|<
name|ShardRouting
argument_list|>
block|{
comment|/**      * The shard id this group relates to.      */
DECL|method|shardId
name|ShardId
name|shardId
parameter_list|()
function_decl|;
comment|/**      * Resets the iterator.      */
DECL|method|reset
name|ShardIterator
name|reset
parameter_list|()
function_decl|;
comment|/**      * The number of shard routing instances.      */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * The number of active shard routing instances.      *      * @see ShardRouting#active()      */
DECL|method|sizeActive
name|int
name|sizeActive
parameter_list|()
function_decl|;
comment|/**      * Is there an active shard we can iterate to.      *      * @see ShardRouting#active()      */
DECL|method|hasNextActive
name|boolean
name|hasNextActive
parameter_list|()
function_decl|;
comment|/**      * Returns the next active shard, or throws {@link NoSuchElementException}.      *      * @see ShardRouting#active()      */
DECL|method|nextActive
name|ShardRouting
name|nextActive
parameter_list|()
throws|throws
name|NoSuchElementException
function_decl|;
comment|/**      * Returns the next active shard, or<tt>null</tt>.      *      * @see ShardRouting#active()      */
DECL|method|nextActiveOrNull
name|ShardRouting
name|nextActiveOrNull
parameter_list|()
function_decl|;
comment|/**      * The number of assigned shard routing instances.      *      * @see ShardRouting#assignedToNode()      */
DECL|method|sizeAssigned
name|int
name|sizeAssigned
parameter_list|()
function_decl|;
comment|/**      * Is there an assigned shard we can iterate to.      *      * @see ShardRouting#assignedToNode()      */
DECL|method|hasNextAssigned
name|boolean
name|hasNextAssigned
parameter_list|()
function_decl|;
comment|/**      * Returns the next assigned shard, or throws {@link NoSuchElementException}.      *      * @see ShardRouting#assignedToNode()      */
DECL|method|nextAssigned
name|ShardRouting
name|nextAssigned
parameter_list|()
throws|throws
name|NoSuchElementException
function_decl|;
comment|/**      * Returns the next assigned shard, or<tt>null</tt>.      *      * @see ShardRouting#assignedToNode()      */
DECL|method|nextAssignedOrNull
name|ShardRouting
name|nextAssignedOrNull
parameter_list|()
function_decl|;
DECL|method|hashCode
name|int
name|hashCode
parameter_list|()
function_decl|;
DECL|method|equals
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

