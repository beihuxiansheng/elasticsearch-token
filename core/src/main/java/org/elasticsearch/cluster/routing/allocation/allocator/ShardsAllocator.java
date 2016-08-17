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
name|node
operator|.
name|DiscoveryNode
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
name|allocation
operator|.
name|RoutingAllocation
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *<p>  * A {@link ShardsAllocator} is the main entry point for shard allocation on nodes in the cluster.  * The allocator makes basic decision where a shard instance will be allocated, if already allocated instances  * need to relocate to other nodes due to node failures or due to rebalancing decisions.  *</p>  */
end_comment

begin_interface
DECL|interface|ShardsAllocator
specifier|public
interface|interface
name|ShardsAllocator
block|{
comment|/**      * Allocates shards to nodes in the cluster. An implementation of this method should:      * - assign unassigned shards      * - relocate shards that cannot stay on a node anymore      * - relocate shards to find a good shard balance in the cluster      *      * @param allocation current node allocation      */
DECL|method|allocate
name|void
name|allocate
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
function_decl|;
comment|/**      * Returns a map of node to a float "weight" of where the allocator would like to place the shard.      * Higher weights signify greater desire to place the shard on that node.      * Does not modify the allocation at all.      *      * @param allocation current node allocation      * @param shard shard to weigh      * @return map of nodes to float weights      */
DECL|method|weighShard
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|Float
argument_list|>
name|weighShard
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|,
name|ShardRouting
name|shard
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

