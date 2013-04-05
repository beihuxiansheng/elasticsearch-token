begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|stats
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ShardOperationFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|DefaultShardOperationFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastShardOperationFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastShardOperationRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|TransportBroadcastOperationAction
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
name|ClusterService
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
name|block
operator|.
name|ClusterBlockException
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
name|block
operator|.
name|ClusterBlockLevel
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
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
name|InternalIndexService
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
name|InternalIndexShard
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
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReferenceArray
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportIndicesStatsAction
specifier|public
class|class
name|TransportIndicesStatsAction
extends|extends
name|TransportBroadcastOperationAction
argument_list|<
name|IndicesStatsRequest
argument_list|,
name|IndicesStatsResponse
argument_list|,
name|TransportIndicesStatsAction
operator|.
name|IndexShardStatsRequest
argument_list|,
name|ShardStats
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportIndicesStatsAction
specifier|public
name|TransportIndicesStatsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
return|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|IndicesStatsAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|IndicesStatsRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|IndicesStatsRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ignoreNonActiveExceptions
specifier|protected
name|boolean
name|ignoreNonActiveExceptions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * Status goes across *all* shards.      */
annotation|@
name|Override
DECL|method|shards
specifier|protected
name|GroupShardsIterator
name|shards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|IndicesStatsRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|)
block|{
return|return
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|allAssignedShardsGrouped
argument_list|(
name|concreteIndices
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkGlobalBlock
specifier|protected
name|ClusterBlockException
name|checkGlobalBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|IndicesStatsRequest
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkRequestBlock
specifier|protected
name|ClusterBlockException
name|checkRequestBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|IndicesStatsRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indicesBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|,
name|concreteIndices
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|IndicesStatsResponse
name|newResponse
parameter_list|(
name|IndicesStatsRequest
name|request
parameter_list|,
name|AtomicReferenceArray
name|shardsResponses
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|int
name|successfulShards
init|=
literal|0
decl_stmt|;
name|int
name|failedShards
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
init|=
literal|null
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ShardStats
argument_list|>
name|shards
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shardsResponses
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|shardResponse
init|=
name|shardsResponses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardResponse
operator|==
literal|null
condition|)
block|{
comment|// simply ignore non active shards
block|}
elseif|else
if|if
condition|(
name|shardResponse
operator|instanceof
name|BroadcastShardOperationFailedException
condition|)
block|{
name|failedShards
operator|++
expr_stmt|;
if|if
condition|(
name|shardFailures
operator|==
literal|null
condition|)
block|{
name|shardFailures
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
name|shardFailures
operator|.
name|add
argument_list|(
operator|new
name|DefaultShardOperationFailedException
argument_list|(
operator|(
name|BroadcastShardOperationFailedException
operator|)
name|shardResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|shards
operator|.
name|add
argument_list|(
operator|(
name|ShardStats
operator|)
name|shardResponse
argument_list|)
expr_stmt|;
name|successfulShards
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|IndicesStatsResponse
argument_list|(
name|shards
operator|.
name|toArray
argument_list|(
operator|new
name|ShardStats
index|[
name|shards
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|clusterState
argument_list|,
name|shardsResponses
operator|.
name|length
argument_list|()
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|shardFailures
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newShardRequest
specifier|protected
name|IndexShardStatsRequest
name|newShardRequest
parameter_list|()
block|{
return|return
operator|new
name|IndexShardStatsRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newShardRequest
specifier|protected
name|IndexShardStatsRequest
name|newShardRequest
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|IndicesStatsRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|IndexShardStatsRequest
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|,
name|shard
operator|.
name|id
argument_list|()
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newShardResponse
specifier|protected
name|ShardStats
name|newShardResponse
parameter_list|()
block|{
return|return
operator|new
name|ShardStats
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|ShardStats
name|shardOperation
parameter_list|(
name|IndexShardStatsRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|InternalIndexService
name|indexService
init|=
operator|(
name|InternalIndexService
operator|)
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
name|InternalIndexShard
name|indexShard
init|=
operator|(
name|InternalIndexShard
operator|)
name|indexService
operator|.
name|shardSafe
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
name|ShardStats
name|stats
init|=
operator|new
name|ShardStats
argument_list|(
name|indexShard
operator|.
name|routingEntry
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|docs
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|docs
operator|=
name|indexShard
operator|.
name|docStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|store
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|store
operator|=
name|indexShard
operator|.
name|storeStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|indexing
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|indexing
operator|=
name|indexShard
operator|.
name|indexingStats
argument_list|(
name|request
operator|.
name|request
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|get
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|get
operator|=
name|indexShard
operator|.
name|getStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|search
argument_list|()
condition|)
block|{
name|stats
operator|.
name|getStats
argument_list|()
operator|.
name|search
operator|=
name|indexShard
operator|.
name|searchStats
argument_list|(
name|request
operator|.
name|request
operator|.
name|groups
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|merge
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|merge
operator|=
name|indexShard
operator|.
name|mergeStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|refresh
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|refresh
operator|=
name|indexShard
operator|.
name|refreshStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|flush
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|flush
operator|=
name|indexShard
operator|.
name|flushStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|warmer
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|warmer
operator|=
name|indexShard
operator|.
name|warmerStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|filterCache
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|filterCache
operator|=
name|indexShard
operator|.
name|filterCacheStats
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|request
operator|.
name|idCache
argument_list|()
condition|)
block|{
name|stats
operator|.
name|stats
operator|.
name|idCache
operator|=
name|indexShard
operator|.
name|idCacheStats
argument_list|()
expr_stmt|;
block|}
return|return
name|stats
return|;
block|}
DECL|class|IndexShardStatsRequest
specifier|public
specifier|static
class|class
name|IndexShardStatsRequest
extends|extends
name|BroadcastShardOperationRequest
block|{
comment|// TODO if there are many indices, the request might hold a large indices array..., we don't really need to serialize it
DECL|field|request
name|IndicesStatsRequest
name|request
decl_stmt|;
DECL|method|IndexShardStatsRequest
name|IndexShardStatsRequest
parameter_list|()
block|{         }
DECL|method|IndexShardStatsRequest
name|IndexShardStatsRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|IndicesStatsRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|request
operator|=
operator|new
name|IndicesStatsRequest
argument_list|()
expr_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

