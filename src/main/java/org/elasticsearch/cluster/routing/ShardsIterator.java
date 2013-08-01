begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  * Allows to iterate over unrelated shards.  */
end_comment

begin_interface
DECL|interface|ShardsIterator
specifier|public
interface|interface
name|ShardsIterator
block|{
comment|/**      * Resets the iterator to its initial state.      */
DECL|method|reset
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**      * The number of shard routing instances.      *      * @return number of shard routing instances in this iterator      */
DECL|method|size
name|int
name|size
parameter_list|()
function_decl|;
comment|/**      * The number of active shard routing instances      *      * @return number of active shard routing instances      */
DECL|method|sizeActive
name|int
name|sizeActive
parameter_list|()
function_decl|;
comment|/**      * Returns the number of replicas in this iterator that are not in the      * {@link ShardRoutingState#UNASSIGNED}. The returned double-counts replicas      * that are in the state {@link ShardRoutingState#RELOCATING}      */
DECL|method|assignedReplicasIncludingRelocating
name|int
name|assignedReplicasIncludingRelocating
parameter_list|()
function_decl|;
comment|/**      * Returns the next shard, or<tt>null</tt> if none available.      */
DECL|method|nextOrNull
name|ShardRouting
name|nextOrNull
parameter_list|()
function_decl|;
comment|/**      * Returns the first shard, or<tt>null</tt>, without      * incrementing the iterator.      *      * @see ShardRouting#assignedToNode()      */
DECL|method|firstOrNull
name|ShardRouting
name|firstOrNull
parameter_list|()
function_decl|;
comment|/**      * Return the number of shards remaining in this {@link ShardsIterator}      *      * @return number of shard remaining      */
DECL|method|remaining
name|int
name|remaining
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|hashCode
name|int
name|hashCode
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|equals
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
function_decl|;
DECL|method|asUnordered
name|Iterable
argument_list|<
name|ShardRouting
argument_list|>
name|asUnordered
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

