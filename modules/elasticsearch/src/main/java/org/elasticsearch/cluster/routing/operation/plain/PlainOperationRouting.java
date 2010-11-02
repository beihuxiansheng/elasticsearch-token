begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.operation.plain
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
operator|.
name|plain
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
name|IndexRoutingTable
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
name|IndexShardRoutingTable
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
name|ShardsIterator
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
name|operation
operator|.
name|OperationRouting
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
name|operation
operator|.
name|hash
operator|.
name|HashFunction
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
name|component
operator|.
name|AbstractComponent
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
name|Index
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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|PlainOperationRouting
specifier|public
class|class
name|PlainOperationRouting
extends|extends
name|AbstractComponent
implements|implements
name|OperationRouting
block|{
DECL|field|routingPattern
specifier|public
specifier|final
specifier|static
name|Pattern
name|routingPattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|","
argument_list|)
decl_stmt|;
DECL|field|hashFunction
specifier|private
specifier|final
name|HashFunction
name|hashFunction
decl_stmt|;
DECL|method|PlainOperationRouting
annotation|@
name|Inject
specifier|public
name|PlainOperationRouting
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|HashFunction
name|hashFunction
parameter_list|)
block|{
name|super
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|hashFunction
operator|=
name|hashFunction
expr_stmt|;
block|}
DECL|method|indexShards
annotation|@
name|Override
specifier|public
name|ShardsIterator
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
block|{
return|return
name|shards
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|routing
argument_list|)
operator|.
name|shardsIt
argument_list|()
return|;
block|}
DECL|method|deleteShards
annotation|@
name|Override
specifier|public
name|ShardsIterator
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
block|{
return|return
name|shards
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|routing
argument_list|)
operator|.
name|shardsIt
argument_list|()
return|;
block|}
DECL|method|getShards
annotation|@
name|Override
specifier|public
name|ShardsIterator
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
parameter_list|)
throws|throws
name|IndexMissingException
throws|,
name|IndexShardMissingException
block|{
return|return
name|shards
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|routing
argument_list|)
operator|.
name|shardsRandomIt
argument_list|()
return|;
block|}
DECL|method|deleteByQueryShards
annotation|@
name|Override
specifier|public
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
name|String
name|routing
parameter_list|)
throws|throws
name|IndexMissingException
block|{
if|if
condition|(
name|routing
operator|==
literal|null
condition|)
block|{
return|return
name|indexRoutingTable
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
operator|.
name|groupByShardsIt
argument_list|()
return|;
block|}
name|String
index|[]
name|routings
init|=
name|routingPattern
operator|.
name|split
argument_list|(
name|routing
argument_list|)
decl_stmt|;
if|if
condition|(
name|routing
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|indexRoutingTable
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
operator|.
name|groupByShardsIt
argument_list|()
return|;
block|}
comment|// we use set here and not identity set since we might get duplicates
name|HashSet
argument_list|<
name|ShardsIterator
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|ShardsIterator
argument_list|>
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRouting
init|=
name|indexRoutingTable
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|r
range|:
name|routings
control|)
block|{
name|int
name|shardId
init|=
name|shardId
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|IndexShardRoutingTable
name|indexShard
init|=
name|indexRouting
operator|.
name|shard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShard
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|)
throw|;
block|}
name|set
operator|.
name|add
argument_list|(
name|indexShard
operator|.
name|shardsRandomIt
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
DECL|method|searchShards
annotation|@
name|Override
specifier|public
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
annotation|@
name|Nullable
name|String
name|queryHint
parameter_list|,
annotation|@
name|Nullable
name|String
name|routing
parameter_list|)
throws|throws
name|IndexMissingException
block|{
if|if
condition|(
name|indices
operator|==
literal|null
operator|||
name|indices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|indices
operator|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteAllIndices
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|routings
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|routings
operator|=
name|routingPattern
operator|.
name|split
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|routings
operator|!=
literal|null
operator|&&
name|routings
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// we use set here and not list since we might get duplicates
name|HashSet
argument_list|<
name|ShardsIterator
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|ShardsIterator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexRoutingTable
name|indexRouting
init|=
name|indexRoutingTable
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|r
range|:
name|routings
control|)
block|{
name|int
name|shardId
init|=
name|shardId
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|IndexShardRoutingTable
name|indexShard
init|=
name|indexRouting
operator|.
name|shard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShard
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|)
throw|;
block|}
comment|// we might get duplicates, but that's ok, they will override one another
name|set
operator|.
name|add
argument_list|(
name|indexShard
operator|.
name|shardsRandomIt
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
else|else
block|{
comment|// we use list here since we know we are not going to create duplicates
name|ArrayList
argument_list|<
name|ShardsIterator
argument_list|>
name|set
init|=
operator|new
name|ArrayList
argument_list|<
name|ShardsIterator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexRoutingTable
name|indexRouting
init|=
name|indexRoutingTable
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|indexRouting
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|indexShard
operator|.
name|shardsRandomIt
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
block|}
DECL|method|indexMetaData
specifier|public
name|IndexMetaData
name|indexMetaData
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|indexMetaData
return|;
block|}
DECL|method|indexRoutingTable
specifier|protected
name|IndexRoutingTable
name|indexRoutingTable
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|IndexRoutingTable
name|indexRouting
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexRouting
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|indexRouting
return|;
block|}
comment|// either routing is set, or type/id are set
DECL|method|shards
specifier|protected
name|IndexShardRoutingTable
name|shards
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
name|String
name|routing
parameter_list|)
block|{
name|int
name|shardId
init|=
name|shardId
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|routing
argument_list|)
decl_stmt|;
name|IndexShardRoutingTable
name|indexShard
init|=
name|indexRoutingTable
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
operator|.
name|shard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShard
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|indexShard
return|;
block|}
DECL|method|shardId
specifier|private
name|int
name|shardId
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
annotation|@
name|Nullable
name|String
name|id
parameter_list|,
annotation|@
name|Nullable
name|String
name|routing
parameter_list|)
block|{
if|if
condition|(
name|routing
operator|==
literal|null
condition|)
block|{
return|return
name|Math
operator|.
name|abs
argument_list|(
name|hash
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
argument_list|)
operator|%
name|indexMetaData
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
operator|.
name|numberOfShards
argument_list|()
return|;
block|}
return|return
name|Math
operator|.
name|abs
argument_list|(
name|hash
argument_list|(
name|routing
argument_list|)
argument_list|)
operator|%
name|indexMetaData
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|)
operator|.
name|numberOfShards
argument_list|()
return|;
block|}
DECL|method|hash
specifier|protected
name|int
name|hash
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
return|return
name|hashFunction
operator|.
name|hash
argument_list|(
name|routing
argument_list|)
return|;
block|}
DECL|method|hash
specifier|protected
name|int
name|hash
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|hashFunction
operator|.
name|hash
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

