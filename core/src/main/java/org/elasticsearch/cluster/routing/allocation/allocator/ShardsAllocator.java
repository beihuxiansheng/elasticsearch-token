begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.allocator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|allocator
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
name|cluster
operator|.
name|routing
operator|.
name|ShardRoutingState
import|;
end_import

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
name|allocation
operator|.
name|FailedRerouteAllocation
import|;
end_import

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
name|allocation
operator|.
name|RoutingAllocation
import|;
end_import

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
name|allocation
operator|.
name|StartedRerouteAllocation
import|;
end_import

begin_comment
comment|/**  *<p>  * A {@link ShardsAllocator} is the main entry point for shard allocation on nodes in the cluster.  * The allocator makes basic decision where a shard instance will be allocated, if already allocated instances  * need relocate to other nodes due to node failures or due to rebalancing decisions.  *</p>  */
end_comment

begin_interface
DECL|interface|ShardsAllocator
specifier|public
interface|interface
name|ShardsAllocator
block|{
comment|/**      * Applies changes on started nodes based on the implemented algorithm. For example if a      * shard has changed to {@link ShardRoutingState#STARTED} from {@link ShardRoutingState#RELOCATING}      * this allocator might apply some cleanups on the node that used to hold the shard.      * @param allocation all started {@link ShardRouting shards}      */
DECL|method|applyStartedShards
name|void
name|applyStartedShards
parameter_list|(
name|StartedRerouteAllocation
name|allocation
parameter_list|)
function_decl|;
comment|/**      * Applies changes on failed nodes based on the implemented algorithm.      * @param allocation all failed {@link ShardRouting shards}      */
DECL|method|applyFailedShards
name|void
name|applyFailedShards
parameter_list|(
name|FailedRerouteAllocation
name|allocation
parameter_list|)
function_decl|;
comment|/**      * Assign all unassigned shards to nodes      *      * @param allocation current node allocation      * @return<code>true</code> if the allocation has changed, otherwise<code>false</code>      */
DECL|method|allocateUnassigned
name|boolean
name|allocateUnassigned
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
function_decl|;
comment|/**      * Rebalancing number of shards on all nodes      *      * @param allocation current node allocation      * @return<code>true</code> if the allocation has changed, otherwise<code>false</code>      */
DECL|method|rebalance
name|boolean
name|rebalance
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
function_decl|;
comment|/**      * Move started shards that can not be allocated to a node anymore      *      * @param allocation current node allocation      * @return<code>true</code> if the allocation has changed, otherwise<code>false</code>      */
DECL|method|moveShards
name|boolean
name|moveShards
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

