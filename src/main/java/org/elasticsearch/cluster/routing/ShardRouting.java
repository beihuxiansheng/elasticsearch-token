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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Serializable
import|;
end_import

begin_comment
comment|/**  * Shard routing represents the state of a shard instance allocated in the cluster.  *  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|ShardRouting
specifier|public
interface|interface
name|ShardRouting
extends|extends
name|Streamable
extends|,
name|Serializable
block|{
comment|/**      * The shard id.      */
DECL|method|shardId
name|ShardId
name|shardId
parameter_list|()
function_decl|;
comment|/**      * The index name.      */
DECL|method|index
name|String
name|index
parameter_list|()
function_decl|;
comment|/**      * The index name.      */
DECL|method|getIndex
name|String
name|getIndex
parameter_list|()
function_decl|;
comment|/**      * The shard id.      */
DECL|method|id
name|int
name|id
parameter_list|()
function_decl|;
comment|/**      * The shard id.      */
DECL|method|getId
name|int
name|getId
parameter_list|()
function_decl|;
comment|/**      * The routing version associated with the shard.      */
DECL|method|version
name|long
name|version
parameter_list|()
function_decl|;
comment|/**      * The shard state.      */
DECL|method|state
name|ShardRoutingState
name|state
parameter_list|()
function_decl|;
comment|/**      * The shard is unassigned (not allocated to any node).      */
DECL|method|unassigned
name|boolean
name|unassigned
parameter_list|()
function_decl|;
comment|/**      * The shard is initializing (usually recovering either from peer shard      * or from gateway).      */
DECL|method|initializing
name|boolean
name|initializing
parameter_list|()
function_decl|;
comment|/**      * The shard is in started mode.      */
DECL|method|started
name|boolean
name|started
parameter_list|()
function_decl|;
comment|/**      * The shard is in relocating mode.      */
DECL|method|relocating
name|boolean
name|relocating
parameter_list|()
function_decl|;
comment|/**      * Relocating or started.      */
DECL|method|active
name|boolean
name|active
parameter_list|()
function_decl|;
comment|/**      * The shard is assigned to a node.      */
DECL|method|assignedToNode
name|boolean
name|assignedToNode
parameter_list|()
function_decl|;
comment|/**      * The current node id the shard is allocated to.      */
DECL|method|currentNodeId
name|String
name|currentNodeId
parameter_list|()
function_decl|;
comment|/**      * The relocating node id the shard is either relocating to or relocating from.      */
DECL|method|relocatingNodeId
name|String
name|relocatingNodeId
parameter_list|()
function_decl|;
comment|/**      * Is this a primary shard.      */
DECL|method|primary
name|boolean
name|primary
parameter_list|()
function_decl|;
comment|/**      * A short description of the shard.      */
DECL|method|shortSummary
name|String
name|shortSummary
parameter_list|()
function_decl|;
comment|/**      * A shard iterator with just this shard in it.      */
DECL|method|shardsIt
name|ShardIterator
name|shardsIt
parameter_list|()
function_decl|;
comment|/**      * Does not write index name and shard id      */
DECL|method|writeToThin
name|void
name|writeToThin
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|readFromThin
name|void
name|readFromThin
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|ClassNotFoundException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

