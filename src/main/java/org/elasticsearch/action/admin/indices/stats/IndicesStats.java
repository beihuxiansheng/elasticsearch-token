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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndicesStats
specifier|public
class|class
name|IndicesStats
extends|extends
name|BroadcastOperationResponse
implements|implements
name|ToXContent
block|{
DECL|field|shards
specifier|private
name|ShardStats
index|[]
name|shards
decl_stmt|;
DECL|method|IndicesStats
name|IndicesStats
parameter_list|()
block|{      }
DECL|method|IndicesStats
name|IndicesStats
parameter_list|(
name|ShardStats
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
name|ShardStats
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
name|ShardStats
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
name|ShardStats
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
name|IndexStats
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
name|IndexStats
argument_list|>
name|getIndices
parameter_list|()
block|{
return|return
name|indices
argument_list|()
return|;
block|}
DECL|field|indicesStats
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStats
argument_list|>
name|indicesStats
decl_stmt|;
DECL|method|indices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStats
argument_list|>
name|indices
parameter_list|()
block|{
if|if
condition|(
name|indicesStats
operator|!=
literal|null
condition|)
block|{
return|return
name|indicesStats
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|IndexStats
argument_list|>
name|indicesStats
init|=
name|Maps
operator|.
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
name|ShardStats
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
name|ShardStats
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
name|indicesStats
operator|.
name|put
argument_list|(
name|index
argument_list|,
operator|new
name|IndexStats
argument_list|(
name|index
argument_list|,
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
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indicesStats
operator|=
name|indicesStats
expr_stmt|;
return|return
name|indicesStats
return|;
block|}
DECL|field|total
specifier|private
name|CommonStats
name|total
init|=
literal|null
decl_stmt|;
DECL|method|getTotal
specifier|public
name|CommonStats
name|getTotal
parameter_list|()
block|{
return|return
name|total
argument_list|()
return|;
block|}
DECL|method|total
specifier|public
name|CommonStats
name|total
parameter_list|()
block|{
if|if
condition|(
name|total
operator|!=
literal|null
condition|)
block|{
return|return
name|total
return|;
block|}
name|CommonStats
name|stats
init|=
operator|new
name|CommonStats
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardStats
name|shard
range|:
name|shards
control|)
block|{
name|stats
operator|.
name|add
argument_list|(
name|shard
operator|.
name|stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|total
operator|=
name|stats
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|field|primary
specifier|private
name|CommonStats
name|primary
init|=
literal|null
decl_stmt|;
DECL|method|getPrimaries
specifier|public
name|CommonStats
name|getPrimaries
parameter_list|()
block|{
return|return
name|primaries
argument_list|()
return|;
block|}
DECL|method|primaries
specifier|public
name|CommonStats
name|primaries
parameter_list|()
block|{
if|if
condition|(
name|primary
operator|!=
literal|null
condition|)
block|{
return|return
name|primary
return|;
block|}
name|CommonStats
name|stats
init|=
operator|new
name|CommonStats
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardStats
name|shard
range|:
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
name|primary
argument_list|()
condition|)
block|{
name|stats
operator|.
name|add
argument_list|(
name|shard
operator|.
name|stats
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|primary
operator|=
name|stats
expr_stmt|;
return|return
name|stats
return|;
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
name|ShardStats
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
name|ShardStats
operator|.
name|readShardStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardStats
name|shard
range|:
name|shards
control|)
block|{
name|shard
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
name|builder
operator|.
name|startObject
argument_list|(
literal|"_all"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"primaries"
argument_list|)
expr_stmt|;
name|primaries
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
literal|"total"
argument_list|)
expr_stmt|;
name|total
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
name|INDICES
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexStats
name|indexStats
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
name|indexStats
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
literal|"primaries"
argument_list|)
expr_stmt|;
name|indexStats
operator|.
name|primaries
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
literal|"total"
argument_list|)
expr_stmt|;
name|indexStats
operator|.
name|total
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
if|if
condition|(
literal|"shards"
operator|.
name|equalsIgnoreCase
argument_list|(
name|params
operator|.
name|param
argument_list|(
literal|"level"
argument_list|,
literal|null
argument_list|)
argument_list|)
condition|)
block|{
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
name|IndexShardStats
name|indexShardStats
range|:
name|indexStats
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
name|indexShardStats
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
name|ShardStats
name|shardStats
range|:
name|indexShardStats
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
name|shardStats
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
name|shardStats
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
name|shardStats
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
name|shardStats
operator|.
name|shardRouting
argument_list|()
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|shardStats
operator|.
name|stats
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
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
block|}
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
block|}
block|}
end_class

end_unit

