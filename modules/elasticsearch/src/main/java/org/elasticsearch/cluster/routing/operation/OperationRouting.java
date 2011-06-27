begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.operation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|operation
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
name|GroupShardsIterator
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
name|ShardIterator
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
name|IndexShardMissingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndexMissingException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|OperationRouting
specifier|public
interface|interface
name|OperationRouting
block|{
DECL|method|indexShards
name|ShardIterator
name|indexShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
annotation|@
name|Nullable
name|String
name|routing
parameter_list|)
throws|throws
name|IndexMissingException
throws|,
name|IndexShardMissingException
function_decl|;
DECL|method|deleteShards
name|ShardIterator
name|deleteShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
annotation|@
name|Nullable
name|String
name|routing
parameter_list|)
throws|throws
name|IndexMissingException
throws|,
name|IndexShardMissingException
function_decl|;
DECL|method|broadcastDeleteShards
name|GroupShardsIterator
name|broadcastDeleteShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|)
throws|throws
name|IndexMissingException
throws|,
name|IndexShardMissingException
function_decl|;
DECL|method|getShards
name|ShardIterator
name|getShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
annotation|@
name|Nullable
name|String
name|routing
parameter_list|,
annotation|@
name|Nullable
name|String
name|preference
parameter_list|)
throws|throws
name|IndexMissingException
throws|,
name|IndexShardMissingException
function_decl|;
DECL|method|getShards
name|ShardIterator
name|getShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
annotation|@
name|Nullable
name|String
name|preference
parameter_list|)
throws|throws
name|IndexMissingException
throws|,
name|IndexShardMissingException
function_decl|;
DECL|method|deleteByQueryShards
name|GroupShardsIterator
name|deleteByQueryShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|,
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|routing
parameter_list|)
throws|throws
name|IndexMissingException
function_decl|;
DECL|method|searchShards
name|GroupShardsIterator
name|searchShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
index|[]
name|indices
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|,
annotation|@
name|Nullable
name|String
name|queryHint
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|routing
parameter_list|,
annotation|@
name|Nullable
name|String
name|preference
parameter_list|)
throws|throws
name|IndexMissingException
function_decl|;
block|}
end_interface

end_unit

