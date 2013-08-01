begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchIllegalArgumentException
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
name|node
operator|.
name|DiscoveryNodes
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
name|ShardIterator
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
name|allocation
operator|.
name|decider
operator|.
name|AwarenessAllocationDecider
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
name|cluster
operator|.
name|routing
operator|.
name|operation
operator|.
name|hash
operator|.
name|djb
operator|.
name|DjbHashFunction
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
name|Strings
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
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  *  */
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
DECL|field|hashFunction
specifier|private
specifier|final
name|HashFunction
name|hashFunction
decl_stmt|;
DECL|field|useType
specifier|private
specifier|final
name|boolean
name|useType
decl_stmt|;
DECL|field|awarenessAllocationDecider
specifier|private
specifier|final
name|AwarenessAllocationDecider
name|awarenessAllocationDecider
decl_stmt|;
annotation|@
name|Inject
DECL|method|PlainOperationRouting
specifier|public
name|PlainOperationRouting
parameter_list|(
name|Settings
name|indexSettings
parameter_list|,
name|HashFunction
name|hashFunction
parameter_list|,
name|AwarenessAllocationDecider
name|awarenessAllocationDecider
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
name|this
operator|.
name|useType
operator|=
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"cluster.routing.operation.use_type"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|awarenessAllocationDecider
operator|=
name|awarenessAllocationDecider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indexShards
specifier|public
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
annotation|@
name|Override
DECL|method|deleteShards
specifier|public
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
annotation|@
name|Override
DECL|method|getShards
specifier|public
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
block|{
return|return
name|preferenceActiveShardIterator
argument_list|(
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
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|,
name|preference
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getShards
specifier|public
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
block|{
return|return
name|preferenceActiveShardIterator
argument_list|(
name|shards
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|,
name|preference
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|broadcastDeleteShards
specifier|public
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
annotation|@
name|Override
DECL|method|deleteByQueryShards
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
name|Set
argument_list|<
name|String
argument_list|>
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
operator|||
name|routing
operator|.
name|isEmpty
argument_list|()
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
name|ShardIterator
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|ShardIterator
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
name|routing
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
annotation|@
name|Override
DECL|method|searchShardsCount
specifier|public
name|int
name|searchShardsCount
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
block|{
if|if
condition|(
name|concreteIndices
operator|==
literal|null
operator|||
name|concreteIndices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|concreteIndices
operator|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteAllOpenIndices
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|HashSet
argument_list|<
name|ShardId
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|ShardId
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
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
name|Set
argument_list|<
name|String
argument_list|>
name|effectiveRouting
init|=
name|routing
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|effectiveRouting
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|r
range|:
name|effectiveRouting
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
comment|// we might get duplicates, but that's ok, its an estimated count? (we just want to know if its 1 or not)
name|set
operator|.
name|add
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|set
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
comment|// we use list here since we know we are not going to create duplicates
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
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
name|count
operator|+=
name|indexRouting
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
block|}
DECL|field|EMPTY_ROUTING
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|EMPTY_ROUTING
init|=
name|Collections
operator|.
name|emptyMap
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|searchShards
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
name|String
index|[]
name|concreteIndices
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
block|{
if|if
condition|(
name|concreteIndices
operator|==
literal|null
operator|||
name|concreteIndices
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|concreteIndices
operator|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteAllOpenIndices
argument_list|()
expr_stmt|;
block|}
name|routing
operator|=
name|routing
operator|==
literal|null
condition|?
name|EMPTY_ROUTING
else|:
name|routing
expr_stmt|;
comment|// just use an empty map
specifier|final
name|Set
argument_list|<
name|ShardIterator
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<
name|ShardIterator
argument_list|>
argument_list|()
decl_stmt|;
comment|// we use set here and not list since we might get duplicates
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
specifier|final
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
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|effectiveRouting
init|=
name|routing
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|effectiveRouting
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|r
range|:
name|effectiveRouting
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
name|ShardIterator
name|iterator
init|=
name|preferenceActiveShardIterator
argument_list|(
name|indexShard
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|,
name|preference
argument_list|)
decl_stmt|;
if|if
condition|(
name|iterator
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|IndexShardRoutingTable
name|indexShard
range|:
name|indexRouting
control|)
block|{
name|ShardIterator
name|iterator
init|=
name|preferenceActiveShardIterator
argument_list|(
name|indexShard
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
argument_list|,
name|preference
argument_list|)
decl_stmt|;
if|if
condition|(
name|iterator
operator|!=
literal|null
condition|)
block|{
name|set
operator|.
name|add
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|preferenceActiveShardIterator
specifier|private
name|ShardIterator
name|preferenceActiveShardIterator
parameter_list|(
name|IndexShardRoutingTable
name|indexShard
parameter_list|,
name|String
name|localNodeId
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|,
annotation|@
name|Nullable
name|String
name|preference
parameter_list|)
block|{
if|if
condition|(
name|preference
operator|==
literal|null
condition|)
block|{
name|String
index|[]
name|awarenessAttributes
init|=
name|awarenessAllocationDecider
operator|.
name|awarenessAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|awarenessAttributes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|indexShard
operator|.
name|activeInitializingShardsRandomIt
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|indexShard
operator|.
name|preferAttributesActiveInitializingShardsIt
argument_list|(
name|awarenessAttributes
argument_list|,
name|nodes
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|preference
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'_'
condition|)
block|{
if|if
condition|(
name|preference
operator|.
name|startsWith
argument_list|(
literal|"_shards:"
argument_list|)
condition|)
block|{
comment|// starts with _shards, so execute on specific ones
name|int
name|index
init|=
name|preference
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
name|String
name|shards
decl_stmt|;
if|if
condition|(
name|index
operator|==
operator|-
literal|1
condition|)
block|{
name|shards
operator|=
name|preference
operator|.
name|substring
argument_list|(
literal|"_shards:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|shards
operator|=
name|preference
operator|.
name|substring
argument_list|(
literal|"_shards:"
operator|.
name|length
argument_list|()
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|ids
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|shards
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
if|if
condition|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|id
argument_list|)
operator|==
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// no more preference
if|if
condition|(
name|index
operator|==
operator|-
literal|1
operator|||
name|index
operator|==
name|preference
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|String
index|[]
name|awarenessAttributes
init|=
name|awarenessAllocationDecider
operator|.
name|awarenessAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|awarenessAttributes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|indexShard
operator|.
name|activeInitializingShardsRandomIt
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|indexShard
operator|.
name|preferAttributesActiveInitializingShardsIt
argument_list|(
name|awarenessAttributes
argument_list|,
name|nodes
argument_list|)
return|;
block|}
block|}
else|else
block|{
comment|// update the preference and continue
name|preference
operator|=
name|preference
operator|.
name|substring
argument_list|(
name|index
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|preference
operator|.
name|startsWith
argument_list|(
literal|"_prefer_node:"
argument_list|)
condition|)
block|{
return|return
name|indexShard
operator|.
name|preferNodeActiveInitializingShardsIt
argument_list|(
name|preference
operator|.
name|substring
argument_list|(
literal|"_prefer_node:"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
if|if
condition|(
literal|"_local"
operator|.
name|equals
argument_list|(
name|preference
argument_list|)
condition|)
block|{
return|return
name|indexShard
operator|.
name|preferNodeActiveInitializingShardsIt
argument_list|(
name|localNodeId
argument_list|)
return|;
block|}
if|if
condition|(
literal|"_primary"
operator|.
name|equals
argument_list|(
name|preference
argument_list|)
condition|)
block|{
return|return
name|indexShard
operator|.
name|primaryActiveInitializingShardIt
argument_list|()
return|;
block|}
if|if
condition|(
literal|"_primary_first"
operator|.
name|equals
argument_list|(
name|preference
argument_list|)
operator|||
literal|"_primaryFirst"
operator|.
name|equals
argument_list|(
name|preference
argument_list|)
condition|)
block|{
return|return
name|indexShard
operator|.
name|primaryFirstActiveInitializingShardsIt
argument_list|()
return|;
block|}
if|if
condition|(
literal|"_only_local"
operator|.
name|equals
argument_list|(
name|preference
argument_list|)
operator|||
literal|"_onlyLocal"
operator|.
name|equals
argument_list|(
name|preference
argument_list|)
condition|)
block|{
return|return
name|indexShard
operator|.
name|onlyNodeActiveInitializingShardsIt
argument_list|(
name|localNodeId
argument_list|)
return|;
block|}
if|if
condition|(
name|preference
operator|.
name|startsWith
argument_list|(
literal|"_only_node:"
argument_list|)
condition|)
block|{
return|return
name|indexShard
operator|.
name|onlyNodeActiveInitializingShardsIt
argument_list|(
name|preference
operator|.
name|substring
argument_list|(
literal|"_only_node:"
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// if not, then use it as the index
name|String
index|[]
name|awarenessAttributes
init|=
name|awarenessAllocationDecider
operator|.
name|awarenessAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|awarenessAttributes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|indexShard
operator|.
name|activeInitializingShardsIt
argument_list|(
name|DjbHashFunction
operator|.
name|DJB_HASH
argument_list|(
name|preference
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|indexShard
operator|.
name|preferAttributesActiveInitializingShardsIt
argument_list|(
name|awarenessAttributes
argument_list|,
name|nodes
argument_list|,
name|DjbHashFunction
operator|.
name|DJB_HASH
argument_list|(
name|preference
argument_list|)
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
return|return
name|shards
argument_list|(
name|clusterState
argument_list|,
name|index
argument_list|,
name|shardId
argument_list|)
return|;
block|}
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
name|int
name|shardId
parameter_list|)
block|{
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
if|if
condition|(
operator|!
name|useType
condition|)
block|{
return|return
name|Math
operator|.
name|abs
argument_list|(
name|hash
argument_list|(
name|id
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
argument_list|)
return|;
block|}
else|else
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
argument_list|)
return|;
block|}
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
argument_list|)
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
if|if
condition|(
name|type
operator|==
literal|null
operator|||
literal|"_all"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Can't route an operation with no type and having type part of the routing (for backward comp)"
argument_list|)
throw|;
block|}
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

