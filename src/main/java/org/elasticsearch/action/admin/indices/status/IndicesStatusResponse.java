begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.status
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
name|status
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
name|Sets
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
name|broadcast
operator|.
name|BroadcastOperationResponse
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
name|SettingsFilter
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentBuilderString
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
name|flush
operator|.
name|FlushStats
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
name|merge
operator|.
name|MergeStats
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
name|refresh
operator|.
name|RefreshStats
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
name|Maps
operator|.
name|newHashMap
import|;
end_import

begin_import
import|import static
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
name|status
operator|.
name|ShardStatus
operator|.
name|readIndexShardStatus
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndicesStatusResponse
specifier|public
class|class
name|IndicesStatusResponse
extends|extends
name|BroadcastOperationResponse
implements|implements
name|ToXContent
block|{
DECL|field|shards
specifier|protected
name|ShardStatus
index|[]
name|shards
decl_stmt|;
DECL|field|indicesStatus
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStatus
argument_list|>
name|indicesStatus
decl_stmt|;
DECL|method|IndicesStatusResponse
name|IndicesStatusResponse
parameter_list|()
block|{     }
DECL|method|IndicesStatusResponse
name|IndicesStatusResponse
parameter_list|(
name|ShardStatus
index|[]
name|shards
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|int
name|failedShards
parameter_list|,
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
parameter_list|)
block|{
name|super
argument_list|(
name|totalShards
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|shards
expr_stmt|;
block|}
DECL|method|shards
specifier|public
name|ShardStatus
index|[]
name|shards
parameter_list|()
block|{
return|return
name|this
operator|.
name|shards
return|;
block|}
DECL|method|getShards
specifier|public
name|ShardStatus
index|[]
name|getShards
parameter_list|()
block|{
return|return
name|this
operator|.
name|shards
return|;
block|}
DECL|method|getAt
specifier|public
name|ShardStatus
name|getAt
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|shards
index|[
name|position
index|]
return|;
block|}
DECL|method|index
specifier|public
name|IndexStatus
name|index
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
name|indices
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStatus
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|indices
argument_list|()
return|;
block|}
DECL|method|indices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStatus
argument_list|>
name|indices
parameter_list|()
block|{
if|if
condition|(
name|indicesStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|indicesStatus
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStatus
argument_list|>
name|indicesStatus
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardStatus
name|shard
range|:
name|shards
control|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|List
argument_list|<
name|ShardStatus
argument_list|>
name|shards
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardStatus
name|shard
range|:
name|this
operator|.
name|shards
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|shardRouting
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|shards
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
name|indicesStatus
operator|.
name|put
argument_list|(
name|index
argument_list|,
operator|new
name|IndexStatus
argument_list|(
name|index
argument_list|,
name|shards
operator|.
name|toArray
argument_list|(
operator|new
name|ShardStatus
index|[
name|shards
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indicesStatus
operator|=
name|indicesStatus
expr_stmt|;
return|return
name|indicesStatus
return|;
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
name|out
operator|.
name|writeVInt
argument_list|(
name|shards
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardStatus
name|status
range|:
name|shards
argument_list|()
control|)
block|{
name|status
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
name|shards
operator|=
operator|new
name|ShardStatus
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shards
index|[
name|i
index|]
operator|=
name|readIndexShardStatus
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|,
annotation|@
name|Nullable
name|SettingsFilter
name|settingsFilter
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDICES
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexStatus
name|indexStatus
range|:
name|indices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexStatus
operator|.
name|index
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexStatus
operator|.
name|storeSize
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARY_SIZE
argument_list|,
name|indexStatus
operator|.
name|primaryStoreSize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARY_SIZE_IN_BYTES
argument_list|,
name|indexStatus
operator|.
name|primaryStoreSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE
argument_list|,
name|indexStatus
operator|.
name|storeSize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|indexStatus
operator|.
name|storeSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|indexStatus
operator|.
name|translogOperations
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|OPERATIONS
argument_list|,
name|indexStatus
operator|.
name|translogOperations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|indexStatus
operator|.
name|docs
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUM_DOCS
argument_list|,
name|indexStatus
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_DOC
argument_list|,
name|indexStatus
operator|.
name|docs
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DELETED_DOCS
argument_list|,
name|indexStatus
operator|.
name|docs
argument_list|()
operator|.
name|deletedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|MergeStats
name|mergeStats
init|=
name|indexStatus
operator|.
name|mergeStats
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergeStats
operator|!=
literal|null
condition|)
block|{
name|mergeStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|RefreshStats
name|refreshStats
init|=
name|indexStatus
operator|.
name|refreshStats
argument_list|()
decl_stmt|;
if|if
condition|(
name|refreshStats
operator|!=
literal|null
condition|)
block|{
name|refreshStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|FlushStats
name|flushStats
init|=
name|indexStatus
operator|.
name|flushStats
argument_list|()
decl_stmt|;
if|if
condition|(
name|flushStats
operator|!=
literal|null
condition|)
block|{
name|flushStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexShardStatus
name|indexShardStatus
range|:
name|indexStatus
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|indexShardStatus
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardStatus
name|shardStatus
range|:
name|indexShardStatus
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|ROUTING
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATE
argument_list|,
name|shardStatus
operator|.
name|shardRouting
argument_list|()
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARY
argument_list|,
name|shardStatus
operator|.
name|shardRouting
argument_list|()
operator|.
name|primary
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NODE
argument_list|,
name|shardStatus
operator|.
name|shardRouting
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RELOCATING_NODE
argument_list|,
name|shardStatus
operator|.
name|shardRouting
argument_list|()
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SHARD
argument_list|,
name|shardStatus
operator|.
name|shardRouting
argument_list|()
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|,
name|shardStatus
operator|.
name|shardRouting
argument_list|()
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATE
argument_list|,
name|shardStatus
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardStatus
operator|.
name|storeSize
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE
argument_list|,
name|shardStatus
operator|.
name|storeSize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|shardStatus
operator|.
name|storeSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shardStatus
operator|.
name|translogId
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|ID
argument_list|,
name|shardStatus
operator|.
name|translogId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|OPERATIONS
argument_list|,
name|shardStatus
operator|.
name|translogOperations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shardStatus
operator|.
name|docs
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|DOCS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NUM_DOCS
argument_list|,
name|shardStatus
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_DOC
argument_list|,
name|shardStatus
operator|.
name|docs
argument_list|()
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DELETED_DOCS
argument_list|,
name|shardStatus
operator|.
name|docs
argument_list|()
operator|.
name|deletedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|mergeStats
operator|=
name|shardStatus
operator|.
name|mergeStats
argument_list|()
expr_stmt|;
if|if
condition|(
name|mergeStats
operator|!=
literal|null
condition|)
block|{
name|mergeStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|refreshStats
operator|=
name|shardStatus
operator|.
name|refreshStats
argument_list|()
expr_stmt|;
if|if
condition|(
name|refreshStats
operator|!=
literal|null
condition|)
block|{
name|refreshStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|flushStats
operator|=
name|shardStatus
operator|.
name|flushStats
argument_list|()
expr_stmt|;
if|if
condition|(
name|flushStats
operator|!=
literal|null
condition|)
block|{
name|flushStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardStatus
operator|.
name|peerRecoveryStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|PeerRecoveryStatus
name|peerRecoveryStatus
init|=
name|shardStatus
operator|.
name|peerRecoveryStatus
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|PEER_RECOVERY
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STAGE
argument_list|,
name|peerRecoveryStatus
operator|.
name|stage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME_IN_MILLIS
argument_list|,
name|peerRecoveryStatus
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME
argument_list|,
name|peerRecoveryStatus
operator|.
name|time
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME_IN_MILLIS
argument_list|,
name|peerRecoveryStatus
operator|.
name|time
argument_list|()
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PROGRESS
argument_list|,
name|peerRecoveryStatus
operator|.
name|indexRecoveryProgress
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE
argument_list|,
name|peerRecoveryStatus
operator|.
name|indexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|peerRecoveryStatus
operator|.
name|indexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REUSED_SIZE
argument_list|,
name|peerRecoveryStatus
operator|.
name|reusedIndexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REUSED_SIZE_IN_BYTES
argument_list|,
name|peerRecoveryStatus
operator|.
name|reusedIndexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|EXPECTED_RECOVERED_SIZE
argument_list|,
name|peerRecoveryStatus
operator|.
name|expectedRecoveredIndexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|EXPECTED_RECOVERED_SIZE_IN_BYTES
argument_list|,
name|peerRecoveryStatus
operator|.
name|expectedRecoveredIndexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RECOVERED_SIZE
argument_list|,
name|peerRecoveryStatus
operator|.
name|recoveredIndexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RECOVERED_SIZE_IN_BYTES
argument_list|,
name|peerRecoveryStatus
operator|.
name|recoveredIndexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RECOVERED
argument_list|,
name|peerRecoveryStatus
operator|.
name|recoveredTranslogOperations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shardStatus
operator|.
name|gatewayRecoveryStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|GatewayRecoveryStatus
name|gatewayRecoveryStatus
init|=
name|shardStatus
operator|.
name|gatewayRecoveryStatus
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|GATEWAY_RECOVERY
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STAGE
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|stage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME_IN_MILLIS
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|time
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME_IN_MILLIS
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|time
argument_list|()
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PROGRESS
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|indexRecoveryProgress
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|indexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|indexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REUSED_SIZE
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|reusedIndexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REUSED_SIZE_IN_BYTES
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|reusedIndexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|EXPECTED_RECOVERED_SIZE
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|expectedRecoveredIndexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|EXPECTED_RECOVERED_SIZE_IN_BYTES
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|expectedRecoveredIndexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RECOVERED_SIZE
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|recoveredIndexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RECOVERED_SIZE_IN_BYTES
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|recoveredIndexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RECOVERED
argument_list|,
name|gatewayRecoveryStatus
operator|.
name|recoveredTranslogOperations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|shardStatus
operator|.
name|gatewaySnapshotStatus
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|GatewaySnapshotStatus
name|gatewaySnapshotStatus
init|=
name|shardStatus
operator|.
name|gatewaySnapshotStatus
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|GATEWAY_SNAPSHOT
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STAGE
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|stage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME_IN_MILLIS
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|time
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIME_IN_MILLIS
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|time
argument_list|()
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|indexSize
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|indexSize
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|EXPECTED_OPERATIONS
argument_list|,
name|gatewaySnapshotStatus
operator|.
name|expectedNumberOfOperations
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|INDICES
specifier|static
specifier|final
name|XContentBuilderString
name|INDICES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"indices"
argument_list|)
decl_stmt|;
DECL|field|INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARY_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primary_size"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARY_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primary_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
DECL|field|SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|TRANSLOG
specifier|static
specifier|final
name|XContentBuilderString
name|TRANSLOG
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"translog"
argument_list|)
decl_stmt|;
DECL|field|OPERATIONS
specifier|static
specifier|final
name|XContentBuilderString
name|OPERATIONS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"operations"
argument_list|)
decl_stmt|;
DECL|field|DOCS
specifier|static
specifier|final
name|XContentBuilderString
name|DOCS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"docs"
argument_list|)
decl_stmt|;
DECL|field|NUM_DOCS
specifier|static
specifier|final
name|XContentBuilderString
name|NUM_DOCS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"num_docs"
argument_list|)
decl_stmt|;
DECL|field|MAX_DOC
specifier|static
specifier|final
name|XContentBuilderString
name|MAX_DOC
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max_doc"
argument_list|)
decl_stmt|;
DECL|field|DELETED_DOCS
specifier|static
specifier|final
name|XContentBuilderString
name|DELETED_DOCS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"deleted_docs"
argument_list|)
decl_stmt|;
DECL|field|SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shards"
argument_list|)
decl_stmt|;
DECL|field|ROUTING
specifier|static
specifier|final
name|XContentBuilderString
name|ROUTING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"routing"
argument_list|)
decl_stmt|;
DECL|field|STATE
specifier|static
specifier|final
name|XContentBuilderString
name|STATE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"state"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primary"
argument_list|)
decl_stmt|;
DECL|field|NODE
specifier|static
specifier|final
name|XContentBuilderString
name|NODE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
DECL|field|RELOCATING_NODE
specifier|static
specifier|final
name|XContentBuilderString
name|RELOCATING_NODE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"relocating_node"
argument_list|)
decl_stmt|;
DECL|field|SHARD
specifier|static
specifier|final
name|XContentBuilderString
name|SHARD
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shard"
argument_list|)
decl_stmt|;
DECL|field|ID
specifier|static
specifier|final
name|XContentBuilderString
name|ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
DECL|field|PEER_RECOVERY
specifier|static
specifier|final
name|XContentBuilderString
name|PEER_RECOVERY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"peer_recovery"
argument_list|)
decl_stmt|;
DECL|field|STAGE
specifier|static
specifier|final
name|XContentBuilderString
name|STAGE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"stage"
argument_list|)
decl_stmt|;
DECL|field|START_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|START_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"start_time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|TIME
specifier|static
specifier|final
name|XContentBuilderString
name|TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"time"
argument_list|)
decl_stmt|;
DECL|field|TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|PROGRESS
specifier|static
specifier|final
name|XContentBuilderString
name|PROGRESS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"progress"
argument_list|)
decl_stmt|;
DECL|field|REUSED_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|REUSED_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"reused_size"
argument_list|)
decl_stmt|;
DECL|field|REUSED_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|REUSED_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"reused_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|EXPECTED_RECOVERED_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|EXPECTED_RECOVERED_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"expected_recovered_size"
argument_list|)
decl_stmt|;
DECL|field|EXPECTED_RECOVERED_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|EXPECTED_RECOVERED_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"expected_recovered_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|RECOVERED_SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|RECOVERED_SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"recovered_size"
argument_list|)
decl_stmt|;
DECL|field|RECOVERED_SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|RECOVERED_SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"recovered_size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|RECOVERED
specifier|static
specifier|final
name|XContentBuilderString
name|RECOVERED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"recovered"
argument_list|)
decl_stmt|;
DECL|field|GATEWAY_RECOVERY
specifier|static
specifier|final
name|XContentBuilderString
name|GATEWAY_RECOVERY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"gateway_recovery"
argument_list|)
decl_stmt|;
DECL|field|GATEWAY_SNAPSHOT
specifier|static
specifier|final
name|XContentBuilderString
name|GATEWAY_SNAPSHOT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"gateway_snapshot"
argument_list|)
decl_stmt|;
DECL|field|EXPECTED_OPERATIONS
specifier|static
specifier|final
name|XContentBuilderString
name|EXPECTED_OPERATIONS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"expected_operations"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

