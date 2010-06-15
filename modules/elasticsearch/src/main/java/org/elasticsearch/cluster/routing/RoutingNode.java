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
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|RoutingNode
specifier|public
class|class
name|RoutingNode
implements|implements
name|Iterable
argument_list|<
name|MutableShardRouting
argument_list|>
block|{
DECL|field|nodeId
specifier|private
specifier|final
name|String
name|nodeId
decl_stmt|;
DECL|field|shards
specifier|private
specifier|final
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
decl_stmt|;
DECL|method|RoutingNode
specifier|public
name|RoutingNode
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
name|this
argument_list|(
name|nodeId
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|MutableShardRouting
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|RoutingNode
specifier|public
name|RoutingNode
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|shards
expr_stmt|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|MutableShardRouting
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|shards
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|nodeId
specifier|public
name|String
name|nodeId
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeId
return|;
block|}
DECL|method|shards
specifier|public
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
parameter_list|()
block|{
return|return
name|this
operator|.
name|shards
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|MutableShardRouting
name|shard
parameter_list|)
block|{
name|shards
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|shard
operator|.
name|assignToNode
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
DECL|method|removeByShardId
specifier|public
name|void
name|removeByShardId
parameter_list|(
name|int
name|shardId
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|MutableShardRouting
argument_list|>
name|it
init|=
name|shards
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|MutableShardRouting
name|shard
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|.
name|id
argument_list|()
operator|==
name|shardId
condition|)
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|numberOfShardsWithState
specifier|public
name|int
name|numberOfShardsWithState
parameter_list|(
name|ShardRoutingState
modifier|...
name|states
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|shardEntry
range|:
name|this
control|)
block|{
for|for
control|(
name|ShardRoutingState
name|state
range|:
name|states
control|)
block|{
if|if
condition|(
name|shardEntry
operator|.
name|state
argument_list|()
operator|==
name|state
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
DECL|method|shardsWithState
specifier|public
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shardsWithState
parameter_list|(
name|ShardRoutingState
modifier|...
name|states
parameter_list|)
block|{
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|shardEntry
range|:
name|this
control|)
block|{
for|for
control|(
name|ShardRoutingState
name|state
range|:
name|states
control|)
block|{
if|if
condition|(
name|shardEntry
operator|.
name|state
argument_list|()
operator|==
name|state
condition|)
block|{
name|shards
operator|.
name|add
argument_list|(
name|shardEntry
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|shards
return|;
block|}
DECL|method|numberOfShardsNotWithState
specifier|public
name|int
name|numberOfShardsNotWithState
parameter_list|(
name|ShardRoutingState
name|state
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|shardEntry
range|:
name|this
control|)
block|{
if|if
condition|(
name|shardEntry
operator|.
name|state
argument_list|()
operator|!=
name|state
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**      * The number fo shards on this node that will not be eventually relocated.      */
DECL|method|numberOfOwningShards
specifier|public
name|int
name|numberOfOwningShards
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|shardEntry
range|:
name|this
control|)
block|{
if|if
condition|(
name|shardEntry
operator|.
name|state
argument_list|()
operator|!=
name|ShardRoutingState
operator|.
name|RELOCATING
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
DECL|method|canAllocate
specifier|public
name|boolean
name|canAllocate
parameter_list|(
name|MetaData
name|metaData
parameter_list|,
name|RoutingTable
name|routingTable
parameter_list|)
block|{
return|return
name|shards
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|metaData
operator|.
name|maxNumberOfShardsPerNode
argument_list|()
return|;
block|}
DECL|method|canAllocate
specifier|public
name|boolean
name|canAllocate
parameter_list|(
name|ShardRouting
name|requested
parameter_list|)
block|{
for|for
control|(
name|MutableShardRouting
name|current
range|:
name|shards
control|)
block|{
comment|// we do not allow for two shards of the same shard id to exists on the same node
if|if
condition|(
name|current
operator|.
name|shardId
argument_list|()
operator|.
name|equals
argument_list|(
name|requested
operator|.
name|shardId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|canAllocate
specifier|public
name|boolean
name|canAllocate
parameter_list|(
name|MutableShardRouting
name|requested
parameter_list|)
block|{
for|for
control|(
name|MutableShardRouting
name|current
range|:
name|shards
control|)
block|{
comment|// we do not allow for two shards of the same shard id to exists on the same node
if|if
condition|(
name|current
operator|.
name|shardId
argument_list|()
operator|.
name|equals
argument_list|(
name|requested
operator|.
name|shardId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|prettyPrint
specifier|public
name|String
name|prettyPrint
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"-----NodeId["
argument_list|)
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|MutableShardRouting
name|entry
range|:
name|shards
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"--------"
argument_list|)
operator|.
name|append
argument_list|(
name|entry
operator|.
name|shortSummary
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

