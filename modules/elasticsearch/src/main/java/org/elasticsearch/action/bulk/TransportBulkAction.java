begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
package|;
end_package

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
name|ActionListener
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
name|ActionRequest
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
name|TransportActions
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|TransportCreateIndexAction
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
name|delete
operator|.
name|DeleteRequest
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
name|index
operator|.
name|IndexRequest
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
name|BaseAction
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
name|metadata
operator|.
name|MappingMetaData
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
name|UUID
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|IndexAlreadyExistsException
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
name|BaseTransportRequestHandler
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
name|TransportChannel
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportBulkAction
specifier|public
class|class
name|TransportBulkAction
extends|extends
name|BaseAction
argument_list|<
name|BulkRequest
argument_list|,
name|BulkResponse
argument_list|>
block|{
DECL|field|autoCreateIndex
specifier|private
specifier|final
name|boolean
name|autoCreateIndex
decl_stmt|;
DECL|field|allowIdGeneration
specifier|private
specifier|final
name|boolean
name|allowIdGeneration
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|shardBulkAction
specifier|private
specifier|final
name|TransportShardBulkAction
name|shardBulkAction
decl_stmt|;
DECL|field|createIndexAction
specifier|private
specifier|final
name|TransportCreateIndexAction
name|createIndexAction
decl_stmt|;
DECL|method|TransportBulkAction
annotation|@
name|Inject
specifier|public
name|TransportBulkAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportShardBulkAction
name|shardBulkAction
parameter_list|,
name|TransportCreateIndexAction
name|createIndexAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|shardBulkAction
operator|=
name|shardBulkAction
expr_stmt|;
name|this
operator|.
name|createIndexAction
operator|=
name|createIndexAction
expr_stmt|;
name|this
operator|.
name|autoCreateIndex
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"action.auto_create_index"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowIdGeneration
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"action.allow_id_generation"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|TransportActions
operator|.
name|BULK
argument_list|,
operator|new
name|TransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doExecute
annotation|@
name|Override
specifier|protected
name|void
name|doExecute
parameter_list|(
specifier|final
name|BulkRequest
name|bulkRequest
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
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
name|ActionRequest
name|request
range|:
name|bulkRequest
operator|.
name|requests
control|)
block|{
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
operator|(
name|IndexRequest
operator|)
name|request
decl_stmt|;
if|if
condition|(
operator|!
name|indices
operator|.
name|contains
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|DeleteRequest
name|deleteRequest
init|=
operator|(
name|DeleteRequest
operator|)
name|request
decl_stmt|;
if|if
condition|(
operator|!
name|indices
operator|.
name|contains
argument_list|(
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|autoCreateIndex
condition|)
block|{
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
name|indices
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|failed
init|=
operator|new
name|AtomicBoolean
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
if|if
condition|(
operator|!
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|hasConcreteIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|createIndexAction
operator|.
name|execute
argument_list|(
operator|new
name|CreateIndexRequest
argument_list|(
name|index
argument_list|)
operator|.
name|cause
argument_list|(
literal|"auto(bulk api)"
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|CreateIndexResponse
name|result
parameter_list|)
block|{
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|executeBulk
argument_list|(
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
operator|instanceof
name|IndexAlreadyExistsException
condition|)
block|{
comment|// we have the index, do it
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|executeBulk
argument_list|(
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|failed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|executeBulk
argument_list|(
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|executeBulk
argument_list|(
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|executeBulk
specifier|private
name|void
name|executeBulk
parameter_list|(
specifier|final
name|BulkRequest
name|bulkRequest
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
for|for
control|(
name|ActionRequest
name|request
range|:
name|bulkRequest
operator|.
name|requests
control|)
block|{
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
operator|(
name|IndexRequest
operator|)
name|request
decl_stmt|;
name|indexRequest
operator|.
name|index
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndex
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|allowIdGeneration
condition|)
block|{
if|if
condition|(
name|indexRequest
operator|.
name|id
argument_list|()
operator|==
literal|null
condition|)
block|{
name|indexRequest
operator|.
name|id
argument_list|(
name|UUID
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// since we generate the id, change it to CREATE
name|indexRequest
operator|.
name|opType
argument_list|(
name|IndexRequest
operator|.
name|OpType
operator|.
name|CREATE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|DeleteRequest
name|deleteRequest
init|=
operator|(
name|DeleteRequest
operator|)
name|request
decl_stmt|;
name|deleteRequest
operator|.
name|index
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndex
argument_list|(
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|BulkItemResponse
index|[]
name|responses
init|=
operator|new
name|BulkItemResponse
index|[
name|bulkRequest
operator|.
name|requests
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// first, go over all the requests and create a ShardId -> Operations mapping
name|Map
argument_list|<
name|ShardId
argument_list|,
name|List
argument_list|<
name|BulkItemRequest
argument_list|>
argument_list|>
name|requestsByShard
init|=
name|Maps
operator|.
name|newHashMap
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
name|bulkRequest
operator|.
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ActionRequest
name|request
init|=
name|bulkRequest
operator|.
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|instanceof
name|IndexRequest
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
operator|(
name|IndexRequest
operator|)
name|request
decl_stmt|;
comment|// handle routing
name|MappingMetaData
name|mappingMd
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|mapping
argument_list|(
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappingMd
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|indexRequest
operator|.
name|processRouting
argument_list|(
name|mappingMd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticSearchException
name|e
parameter_list|)
block|{
name|responses
index|[
name|i
index|]
operator|=
operator|new
name|BulkItemResponse
argument_list|(
name|i
argument_list|,
name|indexRequest
operator|.
name|opType
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
operator|new
name|BulkItemResponse
operator|.
name|Failure
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|id
argument_list|()
argument_list|,
name|e
operator|.
name|getDetailedMessage
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|ShardId
name|shardId
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|indexShards
argument_list|(
name|clusterState
argument_list|,
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|id
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|shardId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BulkItemRequest
argument_list|>
name|list
init|=
name|requestsByShard
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|requestsByShard
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|BulkItemRequest
argument_list|(
name|i
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|DeleteRequest
name|deleteRequest
init|=
operator|(
name|DeleteRequest
operator|)
name|request
decl_stmt|;
name|MappingMetaData
name|mappingMd
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|mapping
argument_list|(
name|deleteRequest
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappingMd
operator|!=
literal|null
operator|&&
name|mappingMd
operator|.
name|routing
argument_list|()
operator|.
name|required
argument_list|()
operator|&&
name|deleteRequest
operator|.
name|routing
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// if routing is required, and no routing on the delete request, we need to broadcast it....
name|GroupShardsIterator
name|groupShards
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|broadcastDeleteShards
argument_list|(
name|clusterState
argument_list|,
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardIterator
name|shardIt
range|:
name|groupShards
control|)
block|{
name|List
argument_list|<
name|BulkItemRequest
argument_list|>
name|list
init|=
name|requestsByShard
operator|.
name|get
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|requestsByShard
operator|.
name|put
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|BulkItemRequest
argument_list|(
name|i
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|ShardId
name|shardId
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|deleteShards
argument_list|(
name|clusterState
argument_list|,
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|,
name|deleteRequest
operator|.
name|type
argument_list|()
argument_list|,
name|deleteRequest
operator|.
name|id
argument_list|()
argument_list|,
name|deleteRequest
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|shardId
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BulkItemRequest
argument_list|>
name|list
init|=
name|requestsByShard
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
name|requestsByShard
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
operator|new
name|BulkItemRequest
argument_list|(
name|i
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
name|requestsByShard
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|List
argument_list|<
name|BulkItemRequest
argument_list|>
argument_list|>
name|entry
range|:
name|requestsByShard
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|ShardId
name|shardId
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|BulkItemRequest
argument_list|>
name|requests
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|BulkShardRequest
name|bulkShardRequest
init|=
operator|new
name|BulkShardRequest
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|,
name|bulkRequest
operator|.
name|refresh
argument_list|()
argument_list|,
name|requests
operator|.
name|toArray
argument_list|(
operator|new
name|BulkItemRequest
index|[
name|requests
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|bulkShardRequest
operator|.
name|replicationType
argument_list|(
name|bulkRequest
operator|.
name|replicationType
argument_list|()
argument_list|)
expr_stmt|;
name|bulkShardRequest
operator|.
name|consistencyLevel
argument_list|(
name|bulkRequest
operator|.
name|consistencyLevel
argument_list|()
argument_list|)
expr_stmt|;
name|shardBulkAction
operator|.
name|execute
argument_list|(
name|bulkShardRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|BulkShardResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|BulkShardResponse
name|bulkShardResponse
parameter_list|)
block|{
synchronized|synchronized
init|(
name|responses
init|)
block|{
for|for
control|(
name|BulkItemResponse
name|bulkItemResponse
range|:
name|bulkShardResponse
operator|.
name|responses
argument_list|()
control|)
block|{
name|responses
index|[
name|bulkItemResponse
operator|.
name|itemId
argument_list|()
index|]
operator|=
name|bulkItemResponse
expr_stmt|;
block|}
block|}
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// create failures for all relevant requests
name|String
name|message
init|=
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|responses
init|)
block|{
for|for
control|(
name|BulkItemRequest
name|request
range|:
name|requests
control|)
block|{
if|if
condition|(
name|request
operator|.
name|request
argument_list|()
operator|instanceof
name|IndexRequest
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
operator|(
name|IndexRequest
operator|)
name|request
operator|.
name|request
argument_list|()
decl_stmt|;
name|responses
index|[
name|request
operator|.
name|id
argument_list|()
index|]
operator|=
operator|new
name|BulkItemResponse
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|opType
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
operator|new
name|BulkItemResponse
operator|.
name|Failure
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|,
name|indexRequest
operator|.
name|id
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|request
argument_list|()
operator|instanceof
name|DeleteRequest
condition|)
block|{
name|DeleteRequest
name|deleteRequest
init|=
operator|(
name|DeleteRequest
operator|)
name|request
operator|.
name|request
argument_list|()
decl_stmt|;
name|responses
index|[
name|request
operator|.
name|id
argument_list|()
index|]
operator|=
operator|new
name|BulkItemResponse
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|,
literal|"delete"
argument_list|,
operator|new
name|BulkItemResponse
operator|.
name|Failure
argument_list|(
name|deleteRequest
operator|.
name|index
argument_list|()
argument_list|,
name|deleteRequest
operator|.
name|type
argument_list|()
argument_list|,
name|deleteRequest
operator|.
name|id
argument_list|()
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|finishHim
parameter_list|()
block|{
if|if
condition|(
name|bulkRequest
operator|.
name|listenerThreaded
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|BulkResponse
argument_list|(
name|responses
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|BulkResponse
argument_list|(
name|responses
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TransportHandler
class|class
name|TransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|BulkRequest
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|BulkRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|BulkRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|BulkRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
comment|// no need to use threaded listener, since we just send a response
name|request
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|BulkResponse
name|result
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send error response for action ["
operator|+
name|TransportActions
operator|.
name|BULK
operator|+
literal|"] and request ["
operator|+
name|request
operator|+
literal|"]"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|spawn
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// spawn, we do some work here...
block|}
block|}
block|}
end_class

end_unit

