begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.fieldstats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|fieldstats
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|MultiFields
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Terms
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|ActionFilters
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
name|TransportBroadcastAction
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
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|IndexService
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
name|engine
operator|.
name|Engine
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
name|mapper
operator|.
name|MappedFieldType
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
name|mapper
operator|.
name|MapperService
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
name|IndexShard
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
name|*
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

begin_class
DECL|class|TransportFieldStatsTransportAction
specifier|public
class|class
name|TransportFieldStatsTransportAction
extends|extends
name|TransportBroadcastAction
argument_list|<
name|FieldStatsRequest
argument_list|,
name|FieldStatsResponse
argument_list|,
name|FieldStatsShardRequest
argument_list|,
name|FieldStatsShardResponse
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
DECL|method|TransportFieldStatsTransportAction
specifier|public
name|TransportFieldStatsTransportAction
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
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|FieldStatsAction
operator|.
name|NAME
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|FieldStatsRequest
operator|::
operator|new
argument_list|,
name|FieldStatsShardRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
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
DECL|method|newResponse
specifier|protected
name|FieldStatsResponse
name|newResponse
parameter_list|(
name|FieldStatsRequest
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
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
argument_list|>
name|indicesMergedFieldStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|shardValue
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
name|shardValue
operator|==
literal|null
condition|)
block|{
comment|// simply ignore non active shards
block|}
elseif|else
if|if
condition|(
name|shardValue
operator|instanceof
name|BroadcastShardOperationFailedException
condition|)
block|{
name|failedShards
operator|++
expr_stmt|;
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
name|shardValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|successfulShards
operator|++
expr_stmt|;
name|FieldStatsShardResponse
name|shardResponse
init|=
operator|(
name|FieldStatsShardResponse
operator|)
name|shardValue
decl_stmt|;
specifier|final
name|String
name|indexName
decl_stmt|;
if|if
condition|(
literal|"cluster"
operator|.
name|equals
argument_list|(
name|request
operator|.
name|level
argument_list|()
argument_list|)
condition|)
block|{
name|indexName
operator|=
literal|"_all"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"indices"
operator|.
name|equals
argument_list|(
name|request
operator|.
name|level
argument_list|()
argument_list|)
condition|)
block|{
name|indexName
operator|=
name|shardResponse
operator|.
name|getIndex
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// should already have been caught by the FieldStatsRequest#validate(...)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal level option ["
operator|+
name|request
operator|.
name|level
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
name|indexMergedFieldStats
init|=
name|indicesMergedFieldStats
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMergedFieldStats
operator|==
literal|null
condition|)
block|{
name|indicesMergedFieldStats
operator|.
name|put
argument_list|(
name|indexName
argument_list|,
name|indexMergedFieldStats
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
name|fieldStats
init|=
name|shardResponse
operator|.
name|getFieldStats
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
name|entry
range|:
name|fieldStats
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|FieldStats
name|existing
init|=
name|indexMergedFieldStats
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|existing
operator|.
name|getType
argument_list|()
operator|!=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"trying to merge the field stats of field ["
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"] from index ["
operator|+
name|shardResponse
operator|.
name|getIndex
argument_list|()
operator|+
literal|"] but the field type is incompatible, try to set the 'level' option to 'indices'"
argument_list|)
throw|;
block|}
name|existing
operator|.
name|append
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|indexMergedFieldStats
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|request
operator|.
name|getIndexConstraints
argument_list|()
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|fieldStatFields
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|request
operator|.
name|getFields
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexConstraint
name|indexConstraint
range|:
name|request
operator|.
name|getIndexConstraints
argument_list|()
control|)
block|{
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
argument_list|>
argument_list|>
name|iterator
init|=
name|indicesMergedFieldStats
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|FieldStats
name|indexConstraintFieldStats
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|get
argument_list|(
name|indexConstraint
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexConstraintFieldStats
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|indexConstraintFieldStats
operator|.
name|match
argument_list|(
name|indexConstraint
argument_list|)
condition|)
block|{
comment|// If the field stats didn't occur in the list of fields in the original request we need to remove the
comment|// field stats, because it was never requested and was only needed to validate the index constraint
if|if
condition|(
name|fieldStatFields
operator|.
name|contains
argument_list|(
name|indexConstraint
operator|.
name|getField
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|remove
argument_list|(
name|indexConstraint
operator|.
name|getField
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// The index constraint didn't match, so we remove all the field stats of the index we're checking
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
operator|new
name|FieldStatsResponse
argument_list|(
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
argument_list|,
name|indicesMergedFieldStats
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newShardRequest
specifier|protected
name|FieldStatsShardRequest
name|newShardRequest
parameter_list|(
name|int
name|numShards
parameter_list|,
name|ShardRouting
name|shard
parameter_list|,
name|FieldStatsRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|FieldStatsShardRequest
argument_list|(
name|shard
operator|.
name|shardId
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
name|FieldStatsShardResponse
name|newShardResponse
parameter_list|()
block|{
return|return
operator|new
name|FieldStatsShardResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|FieldStatsShardResponse
name|shardOperation
parameter_list|(
name|FieldStatsShardRequest
name|request
parameter_list|)
block|{
name|ShardId
name|shardId
init|=
name|request
operator|.
name|shardId
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|FieldStats
argument_list|>
name|fieldStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|IndexService
name|indexServices
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|indexServices
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|IndexShard
name|shard
init|=
name|indexServices
operator|.
name|getShard
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|Engine
operator|.
name|Searcher
name|searcher
init|=
name|shard
operator|.
name|acquireSearcher
argument_list|(
literal|"fieldstats"
argument_list|)
init|)
block|{
for|for
control|(
name|String
name|field
range|:
name|request
operator|.
name|getFields
argument_list|()
control|)
block|{
name|MappedFieldType
name|fieldType
init|=
name|mapperService
operator|.
name|fullName
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|IndexReader
name|reader
init|=
name|searcher
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|fieldStats
operator|.
name|put
argument_list|(
name|field
argument_list|,
name|fieldType
operator|.
name|stats
argument_list|(
name|terms
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field ["
operator|+
name|field
operator|+
literal|"] doesn't exist"
argument_list|)
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|FieldStatsShardResponse
argument_list|(
name|shardId
argument_list|,
name|fieldStats
argument_list|)
return|;
block|}
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
name|FieldStatsRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|)
block|{
return|return
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|searchShards
argument_list|(
name|clusterState
argument_list|,
name|concreteIndices
argument_list|,
literal|null
argument_list|,
literal|null
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
name|FieldStatsRequest
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
name|READ
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
name|FieldStatsRequest
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
name|READ
argument_list|,
name|concreteIndices
argument_list|)
return|;
block|}
block|}
end_class

end_unit

