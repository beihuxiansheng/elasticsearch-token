begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|util
operator|.
name|Arrays
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

begin_class
DECL|class|IndexShardStats
specifier|public
class|class
name|IndexShardStats
implements|implements
name|Iterable
argument_list|<
name|ShardStats
argument_list|>
implements|,
name|Streamable
block|{
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
DECL|field|shards
specifier|private
name|ShardStats
index|[]
name|shards
decl_stmt|;
DECL|method|IndexShardStats
specifier|private
name|IndexShardStats
parameter_list|()
block|{}
DECL|method|IndexShardStats
specifier|public
name|IndexShardStats
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|ShardStats
index|[]
name|shards
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|shards
expr_stmt|;
block|}
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
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
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|ShardStats
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|shards
argument_list|)
operator|.
name|iterator
argument_list|()
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
name|getStats
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
DECL|method|getPrimary
specifier|public
name|CommonStats
name|getPrimary
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
name|getShardRouting
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
name|getStats
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
name|shardId
operator|=
name|ShardId
operator|.
name|readShardId
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|shardSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|shards
operator|=
operator|new
name|ShardStats
index|[
name|shardSize
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
name|shardSize
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
name|shardId
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
name|stats
range|:
name|shards
control|)
block|{
name|stats
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readIndexShardStats
specifier|public
specifier|static
name|IndexShardStats
name|readIndexShardStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|IndexShardStats
name|indexShardStats
init|=
operator|new
name|IndexShardStats
argument_list|()
decl_stmt|;
name|indexShardStats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|indexShardStats
return|;
block|}
block|}
end_class

end_unit

