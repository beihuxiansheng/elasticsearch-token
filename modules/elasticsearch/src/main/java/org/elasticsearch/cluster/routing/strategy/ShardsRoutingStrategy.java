begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.strategy
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|strategy
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
name|ClusterState
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
name|RoutingTable
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|ShardsRoutingStrategy
specifier|public
interface|interface
name|ShardsRoutingStrategy
block|{
comment|/**      * Applies the started shards. Note, shards can be called several times within this method.      *      *<p>If the same instance of the routing table is returned, then no change has been made.      */
DECL|method|applyStartedShards
name|RoutingTable
name|applyStartedShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|startedShardEntries
parameter_list|)
function_decl|;
comment|/**      * Applies the failed shards. Note, shards can be called several times within this method.      *      *<p>If the same instance of the routing table is returned, then no change has been made.      */
DECL|method|applyFailedShards
name|RoutingTable
name|applyFailedShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|Iterable
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|failedShardEntries
parameter_list|)
function_decl|;
comment|/**      * Reroutes the routing table based on the live nodes.      *      *<p>If the same instance of the routing table is returned, then no change has been made.      */
DECL|method|reroute
name|RoutingTable
name|reroute
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

