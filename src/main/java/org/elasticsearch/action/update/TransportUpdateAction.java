begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.update
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|update
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
name|ImmutableList
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
name|ElasticSearchIllegalStateException
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
name|delete
operator|.
name|DeleteResponse
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
name|TransportDeleteAction
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
name|index
operator|.
name|IndexResponse
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
name|TransportIndexAction
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
name|AutoCreateIndex
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
name|single
operator|.
name|instance
operator|.
name|TransportInstanceSingleOperationAction
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
name|MetaData
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
name|PlainShardIterator
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
name|bytes
operator|.
name|BytesReference
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
name|Tuple
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
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
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
name|XContentType
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
name|DocumentAlreadyExistsException
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
name|VersionConflictEngineException
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
name|IllegalIndexShardStateException
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
name|TransportService
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportUpdateAction
specifier|public
class|class
name|TransportUpdateAction
extends|extends
name|TransportInstanceSingleOperationAction
argument_list|<
name|UpdateRequest
argument_list|,
name|UpdateResponse
argument_list|>
block|{
DECL|field|deleteAction
specifier|private
specifier|final
name|TransportDeleteAction
name|deleteAction
decl_stmt|;
DECL|field|indexAction
specifier|private
specifier|final
name|TransportIndexAction
name|indexAction
decl_stmt|;
DECL|field|autoCreateIndex
specifier|private
specifier|final
name|AutoCreateIndex
name|autoCreateIndex
decl_stmt|;
DECL|field|createIndexAction
specifier|private
specifier|final
name|TransportCreateIndexAction
name|createIndexAction
decl_stmt|;
DECL|field|updateHelper
specifier|private
specifier|final
name|UpdateHelper
name|updateHelper
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportUpdateAction
specifier|public
name|TransportUpdateAction
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
name|TransportIndexAction
name|indexAction
parameter_list|,
name|TransportDeleteAction
name|deleteAction
parameter_list|,
name|TransportCreateIndexAction
name|createIndexAction
parameter_list|,
name|UpdateHelper
name|updateHelper
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
name|indexAction
operator|=
name|indexAction
expr_stmt|;
name|this
operator|.
name|deleteAction
operator|=
name|deleteAction
expr_stmt|;
name|this
operator|.
name|createIndexAction
operator|=
name|createIndexAction
expr_stmt|;
name|this
operator|.
name|updateHelper
operator|=
name|updateHelper
expr_stmt|;
name|this
operator|.
name|autoCreateIndex
operator|=
operator|new
name|AutoCreateIndex
argument_list|(
name|settings
argument_list|)
expr_stmt|;
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
name|UpdateAction
operator|.
name|NAME
return|;
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
name|INDEX
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|UpdateRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|UpdateResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|UpdateResponse
argument_list|()
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
name|UpdateRequest
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
name|WRITE
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
name|UpdateRequest
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|retryOnFailure
specifier|protected
name|boolean
name|retryOnFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|IllegalIndexShardStateException
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|resolveRequest
specifier|protected
name|boolean
name|resolveRequest
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|UpdateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|MetaData
name|metaData
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
decl_stmt|;
name|String
name|aliasOrIndex
init|=
name|request
operator|.
name|index
argument_list|()
decl_stmt|;
name|request
operator|.
name|routing
argument_list|(
operator|(
name|metaData
operator|.
name|resolveIndexRouting
argument_list|(
name|request
operator|.
name|routing
argument_list|()
argument_list|,
name|aliasOrIndex
argument_list|)
operator|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|index
argument_list|(
name|metaData
operator|.
name|concreteIndex
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
comment|// if we don't have a master, we don't have metadata, that's fine, let it find a master using create index API
if|if
condition|(
name|autoCreateIndex
operator|.
name|shouldAutoCreate
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
condition|)
block|{
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
comment|// we fork on another thread...
name|createIndexAction
operator|.
name|execute
argument_list|(
operator|new
name|CreateIndexRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|cause
argument_list|(
literal|"auto(update api)"
argument_list|)
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
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
name|innerExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
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
try|try
block|{
name|innerExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
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
name|innerExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerExecute
specifier|private
name|void
name|innerExecute
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|super
operator|.
name|doExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shards
specifier|protected
name|ShardIterator
name|shards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|UpdateRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|request
operator|.
name|shardId
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|shard
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
operator|.
name|primaryShardIt
argument_list|()
return|;
block|}
name|ShardIterator
name|shardIterator
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|indexShards
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|routing
argument_list|()
argument_list|)
decl_stmt|;
name|ShardRouting
name|shard
decl_stmt|;
while|while
condition|(
operator|(
name|shard
operator|=
name|shardIterator
operator|.
name|nextOrNull
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
condition|)
block|{
return|return
operator|new
name|PlainShardIterator
argument_list|(
name|shardIterator
operator|.
name|shardId
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|shard
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|PlainShardIterator
argument_list|(
name|shardIterator
operator|.
name|shardId
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|ShardRouting
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|void
name|shardOperation
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|shardOperation
specifier|protected
name|void
name|shardOperation
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|,
specifier|final
name|int
name|retryCount
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
specifier|final
name|UpdateHelper
operator|.
name|Result
name|result
init|=
name|updateHelper
operator|.
name|prepare
argument_list|(
name|request
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|result
operator|.
name|operation
argument_list|()
condition|)
block|{
case|case
name|UPSERT
case|:
name|IndexRequest
name|upsertRequest
init|=
name|result
operator|.
name|action
argument_list|()
decl_stmt|;
comment|// we fetch it from the index request so we don't generate the bytes twice, its already done in the index request
specifier|final
name|BytesReference
name|upsertSourceBytes
init|=
name|upsertRequest
operator|.
name|source
argument_list|()
decl_stmt|;
name|indexAction
operator|.
name|execute
argument_list|(
name|upsertRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|IndexResponse
name|response
parameter_list|)
block|{
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|response
operator|.
name|getIndex
argument_list|()
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|,
name|response
operator|.
name|getId
argument_list|()
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|response
operator|.
name|isCreated
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|fields
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|sourceAndContent
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|upsertSourceBytes
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|update
operator|.
name|setGetResult
argument_list|(
name|updateHelper
operator|.
name|extractGetResult
argument_list|(
name|request
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|sourceAndContent
operator|.
name|v2
argument_list|()
argument_list|,
name|sourceAndContent
operator|.
name|v1
argument_list|()
argument_list|,
name|upsertSourceBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|update
operator|.
name|setGetResult
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onResponse
argument_list|(
name|update
argument_list|)
expr_stmt|;
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
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|VersionConflictEngineException
operator|||
name|e
operator|instanceof
name|DocumentAlreadyExistsException
condition|)
block|{
if|if
condition|(
name|retryCount
operator|<
name|request
operator|.
name|retryOnConflict
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|()
argument_list|)
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
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
name|retryCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
case|case
name|INDEX
case|:
name|IndexRequest
name|indexRequest
init|=
name|result
operator|.
name|action
argument_list|()
decl_stmt|;
comment|// we fetch it from the index request so we don't generate the bytes twice, its already done in the index request
specifier|final
name|BytesReference
name|indexSourceBytes
init|=
name|indexRequest
operator|.
name|source
argument_list|()
decl_stmt|;
name|indexAction
operator|.
name|execute
argument_list|(
name|indexRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|IndexResponse
name|response
parameter_list|)
block|{
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|response
operator|.
name|getIndex
argument_list|()
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|,
name|response
operator|.
name|getId
argument_list|()
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|response
operator|.
name|isCreated
argument_list|()
argument_list|)
decl_stmt|;
name|update
operator|.
name|setGetResult
argument_list|(
name|updateHelper
operator|.
name|extractGetResult
argument_list|(
name|request
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|result
operator|.
name|updatedSourceAsMap
argument_list|()
argument_list|,
name|result
operator|.
name|updateSourceContentType
argument_list|()
argument_list|,
name|indexSourceBytes
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|update
argument_list|)
expr_stmt|;
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
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|VersionConflictEngineException
condition|)
block|{
if|if
condition|(
name|retryCount
operator|<
name|request
operator|.
name|retryOnConflict
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|()
argument_list|)
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
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
name|retryCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|DeleteRequest
name|deleteRequest
init|=
name|result
operator|.
name|action
argument_list|()
decl_stmt|;
name|deleteAction
operator|.
name|execute
argument_list|(
name|deleteRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|DeleteResponse
name|response
parameter_list|)
block|{
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|response
operator|.
name|getIndex
argument_list|()
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|,
name|response
operator|.
name|getId
argument_list|()
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|update
operator|.
name|setGetResult
argument_list|(
name|updateHelper
operator|.
name|extractGetResult
argument_list|(
name|request
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|result
operator|.
name|updatedSourceAsMap
argument_list|()
argument_list|,
name|result
operator|.
name|updateSourceContentType
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|update
argument_list|)
expr_stmt|;
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
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|VersionConflictEngineException
condition|)
block|{
if|if
condition|(
name|retryCount
operator|<
name|request
operator|.
name|retryOnConflict
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|()
argument_list|)
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
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
name|retryCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
break|break;
case|case
name|NONE
case|:
name|UpdateResponse
name|update
init|=
name|result
operator|.
name|action
argument_list|()
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|update
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Illegal operation "
operator|+
name|result
operator|.
name|operation
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

