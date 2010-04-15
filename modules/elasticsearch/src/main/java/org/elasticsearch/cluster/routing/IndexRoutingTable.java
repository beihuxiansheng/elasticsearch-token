begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this   * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|UnmodifiableIterator
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
name|metadata
operator|.
name|IndexMetaData
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
name|metadata
operator|.
name|MetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|IdentityHashSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Immutable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|HashMap
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
annotation|@
name|Immutable
DECL|class|IndexRoutingTable
specifier|public
class|class
name|IndexRoutingTable
implements|implements
name|Iterable
argument_list|<
name|IndexShardRoutingTable
argument_list|>
block|{
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
comment|// note, we assume that when the index routing is created, ShardRoutings are created for all possible number of
comment|// shards with state set to UNASSIGNED
DECL|field|shards
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
name|shards
decl_stmt|;
DECL|method|IndexRoutingTable
name|IndexRoutingTable
parameter_list|(
name|String
name|index
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
name|shards
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|shards
argument_list|)
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
argument_list|()
return|;
block|}
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|(
name|RoutingTableValidation
name|validation
parameter_list|,
name|MetaData
name|metaData
parameter_list|)
block|{
if|if
condition|(
operator|!
name|metaData
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|validation
operator|.
name|addIndexFailure
argument_list|(
name|index
argument_list|()
argument_list|,
literal|"Exists in routing does not exists in metadata"
argument_list|)
expr_stmt|;
return|return;
block|}
name|IndexMetaData
name|indexMetaData
init|=
name|metaData
operator|.
name|index
argument_list|(
name|index
argument_list|()
argument_list|)
decl_stmt|;
comment|// check the number of shards
if|if
condition|(
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
operator|!=
name|shards
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|expected
init|=
name|Sets
operator|.
name|newHashSet
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
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|expected
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|IndexShardRoutingTable
name|indexShardRoutingTable
range|:
name|this
control|)
block|{
name|expected
operator|.
name|remove
argument_list|(
name|indexShardRoutingTable
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|validation
operator|.
name|addIndexFailure
argument_list|(
name|index
argument_list|()
argument_list|,
literal|"Wrong number of shards in routing table, missing: "
operator|+
name|expected
argument_list|)
expr_stmt|;
block|}
comment|// check the replicas
for|for
control|(
name|IndexShardRoutingTable
name|indexShardRoutingTable
range|:
name|this
control|)
block|{
name|int
name|routingNumberOfReplicas
init|=
name|indexShardRoutingTable
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|routingNumberOfReplicas
operator|!=
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
condition|)
block|{
name|validation
operator|.
name|addIndexFailure
argument_list|(
name|index
argument_list|()
argument_list|,
literal|"Shard ["
operator|+
name|indexShardRoutingTable
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
operator|+
literal|"] routing table has wrong number of replicas, expected ["
operator|+
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
operator|+
literal|"], got ["
operator|+
name|routingNumberOfReplicas
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|UnmodifiableIterator
argument_list|<
name|IndexShardRoutingTable
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|shards
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|shards
specifier|public
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
name|shards
parameter_list|()
block|{
return|return
name|shards
return|;
block|}
DECL|method|getShards
specifier|public
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
name|getShards
parameter_list|()
block|{
return|return
name|shards
argument_list|()
return|;
block|}
DECL|method|shard
specifier|public
name|IndexShardRoutingTable
name|shard
parameter_list|(
name|int
name|shardId
parameter_list|)
block|{
return|return
name|shards
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
return|;
block|}
comment|/**      * A group shards iterator where each group ({@link ShardsIterator}      * is an iterator across shard replication group.      */
DECL|method|groupByShardsIt
specifier|public
name|GroupShardsIterator
name|groupByShardsIt
parameter_list|()
block|{
name|IdentityHashSet
argument_list|<
name|ShardsIterator
argument_list|>
name|set
init|=
operator|new
name|IdentityHashSet
argument_list|<
name|ShardsIterator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|this
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|indexShard
operator|.
name|shardsIt
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GroupShardsIterator
argument_list|(
name|set
argument_list|)
return|;
block|}
comment|/**      * A groups shards iterator where each groups is a single {@link ShardRouting} and a group      * is created for each shard routing.      *      *<p>This basically means that components that use the {@link GroupShardsIterator} will iterate      * over *all* the shards (all the replicas) within the index.      */
DECL|method|groupByAllIt
specifier|public
name|GroupShardsIterator
name|groupByAllIt
parameter_list|()
block|{
name|IdentityHashSet
argument_list|<
name|ShardsIterator
argument_list|>
name|set
init|=
operator|new
name|IdentityHashSet
argument_list|<
name|ShardsIterator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|this
control|)
block|{
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|indexShard
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|shardRouting
operator|.
name|shardsIt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|GroupShardsIterator
argument_list|(
name|set
argument_list|)
return|;
block|}
DECL|method|validate
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|RoutingValidationException
block|{     }
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|shards
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
name|shards
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|IndexShardRoutingTable
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|IndexRoutingTable
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|index
init|=
name|in
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|addIndexShard
argument_list|(
name|IndexShardRoutingTable
operator|.
name|Builder
operator|.
name|readFromThin
argument_list|(
name|in
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|IndexRoutingTable
name|index
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|index
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|index
operator|.
name|shards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|index
control|)
block|{
name|IndexShardRoutingTable
operator|.
name|Builder
operator|.
name|writeToThin
argument_list|(
name|indexShard
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Initializes a new empry index          */
DECL|method|initializeEmpty
specifier|public
name|Builder
name|initializeEmpty
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
for|for
control|(
name|int
name|shardId
init|=
literal|0
init|;
name|shardId
operator|<
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
condition|;
name|shardId
operator|++
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|addShard
argument_list|(
name|shardId
argument_list|,
literal|null
argument_list|,
name|i
operator|==
literal|0
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|this
return|;
block|}
DECL|method|addIndexShard
specifier|public
name|Builder
name|addIndexShard
parameter_list|(
name|IndexShardRoutingTable
name|indexShard
parameter_list|)
block|{
name|shards
operator|.
name|put
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|indexShard
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|addShard
specifier|public
name|Builder
name|addShard
parameter_list|(
name|ShardRouting
name|shard
parameter_list|)
block|{
return|return
name|internalAddShard
argument_list|(
operator|new
name|ImmutableShardRouting
argument_list|(
name|shard
argument_list|)
argument_list|)
return|;
block|}
DECL|method|addShard
specifier|public
name|Builder
name|addShard
parameter_list|(
name|int
name|shardId
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|boolean
name|primary
parameter_list|,
name|ShardRoutingState
name|state
parameter_list|)
block|{
name|ImmutableShardRouting
name|shard
init|=
operator|new
name|ImmutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|nodeId
argument_list|,
name|primary
argument_list|,
name|state
argument_list|)
decl_stmt|;
return|return
name|internalAddShard
argument_list|(
name|shard
argument_list|)
return|;
block|}
DECL|method|internalAddShard
specifier|private
name|Builder
name|internalAddShard
parameter_list|(
name|ImmutableShardRouting
name|shard
parameter_list|)
block|{
name|IndexShardRoutingTable
name|indexShard
init|=
name|shards
operator|.
name|get
argument_list|(
name|shard
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShard
operator|==
literal|null
condition|)
block|{
name|indexShard
operator|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|)
operator|.
name|addShard
argument_list|(
name|shard
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|indexShard
operator|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
name|indexShard
argument_list|)
operator|.
name|addShard
argument_list|(
name|shard
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|shards
operator|.
name|put
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|indexShard
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|IndexRoutingTable
name|build
parameter_list|()
throws|throws
name|RoutingValidationException
block|{
name|IndexRoutingTable
name|indexRoutingTable
init|=
operator|new
name|IndexRoutingTable
argument_list|(
name|index
argument_list|,
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|shards
argument_list|)
argument_list|)
decl_stmt|;
name|indexRoutingTable
operator|.
name|validate
argument_list|()
expr_stmt|;
return|return
name|indexRoutingTable
return|;
block|}
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
argument_list|(
literal|"-- Index["
operator|+
name|index
operator|+
literal|"]\n"
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|this
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"----ShardId["
argument_list|)
operator|.
name|append
argument_list|(
name|indexShard
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
name|append
argument_list|(
literal|"]["
argument_list|)
operator|.
name|append
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|indexShard
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
name|shard
operator|.
name|shortSummary
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
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

