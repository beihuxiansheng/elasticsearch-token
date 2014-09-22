begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.exists
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|exists
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|TransportBroadcastOperationAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
operator|.
name|PageCacheRecycler
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
name|lucene
operator|.
name|Lucene
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
name|util
operator|.
name|BigArrays
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
name|query
operator|.
name|QueryParseContext
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
name|service
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
name|shard
operator|.
name|service
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
name|script
operator|.
name|ScriptService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchShardTarget
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|DefaultSearchContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|ShardSearchRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
operator|.
name|QueryPhaseExecutionException
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
name|AtomicReferenceArray
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
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|exists
operator|.
name|ExistsRequest
operator|.
name|DEFAULT_MIN_SCORE
import|;
end_import

begin_class
DECL|class|TransportExistsAction
specifier|public
class|class
name|TransportExistsAction
extends|extends
name|TransportBroadcastOperationAction
argument_list|<
name|ExistsRequest
argument_list|,
name|ExistsResponse
argument_list|,
name|ShardExistsRequest
argument_list|,
name|ShardExistsResponse
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
DECL|field|pageCacheRecycler
specifier|private
specifier|final
name|PageCacheRecycler
name|pageCacheRecycler
decl_stmt|;
DECL|field|bigArrays
specifier|private
specifier|final
name|BigArrays
name|bigArrays
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportExistsAction
specifier|public
name|TransportExistsAction
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
name|IndicesService
name|indicesService
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|,
name|PageCacheRecycler
name|pageCacheRecycler
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ExistsAction
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|this
operator|.
name|pageCacheRecycler
operator|=
name|pageCacheRecycler
expr_stmt|;
name|this
operator|.
name|bigArrays
operator|=
name|bigArrays
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ExistsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ExistsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|request
operator|.
name|nowInMillis
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
operator|new
name|ExistsAsyncBroadcastAction
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
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
name|SEARCH
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|ExistsRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|ExistsRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newShardRequest
specifier|protected
name|ShardExistsRequest
name|newShardRequest
parameter_list|()
block|{
return|return
operator|new
name|ShardExistsRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newShardRequest
specifier|protected
name|ShardExistsRequest
name|newShardRequest
parameter_list|(
name|int
name|numShards
parameter_list|,
name|ShardRouting
name|shard
parameter_list|,
name|ExistsRequest
name|request
parameter_list|)
block|{
name|String
index|[]
name|filteringAliases
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|filteringAliases
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ShardExistsRequest
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|filteringAliases
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newShardResponse
specifier|protected
name|ShardExistsResponse
name|newShardResponse
parameter_list|()
block|{
return|return
operator|new
name|ShardExistsResponse
argument_list|()
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
name|ExistsRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|routingMap
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|resolveSearchRouting
argument_list|(
name|request
operator|.
name|routing
argument_list|()
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
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
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|concreteIndices
argument_list|,
name|routingMap
argument_list|,
name|request
operator|.
name|preference
argument_list|()
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
name|ExistsRequest
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
name|ExistsRequest
name|countRequest
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
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ExistsResponse
name|newResponse
parameter_list|(
name|ExistsRequest
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
name|boolean
name|exists
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
init|=
literal|null
decl_stmt|;
comment|// if docs do exist, the last response will have exists = true (since we early terminate the shard requests)
for|for
control|(
name|int
name|i
init|=
name|shardsResponses
operator|.
name|length
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Object
name|shardResponse
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
name|shardResponse
operator|==
literal|null
condition|)
block|{
comment|// simply ignore non active shards
block|}
elseif|else
if|if
condition|(
name|shardResponse
operator|instanceof
name|BroadcastShardOperationFailedException
condition|)
block|{
name|failedShards
operator|++
expr_stmt|;
if|if
condition|(
name|shardFailures
operator|==
literal|null
condition|)
block|{
name|shardFailures
operator|=
name|newArrayList
argument_list|()
expr_stmt|;
block|}
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
name|shardResponse
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|successfulShards
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|exists
operator|=
operator|(
operator|(
name|ShardExistsResponse
operator|)
name|shardResponse
operator|)
operator|.
name|exists
argument_list|()
operator|)
condition|)
block|{
name|successfulShards
operator|=
name|shardsResponses
operator|.
name|length
argument_list|()
operator|-
name|failedShards
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
operator|new
name|ExistsResponse
argument_list|(
name|exists
argument_list|,
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
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|ShardExistsResponse
name|shardOperation
parameter_list|(
name|ShardExistsRequest
name|request
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|indexService
operator|.
name|shardSafe
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|SearchShardTarget
name|shardTarget
init|=
operator|new
name|SearchShardTarget
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|SearchContext
name|context
init|=
operator|new
name|DefaultSearchContext
argument_list|(
literal|0
argument_list|,
operator|new
name|ShardSearchRequest
argument_list|(
name|request
argument_list|)
operator|.
name|types
argument_list|(
name|request
operator|.
name|types
argument_list|()
argument_list|)
operator|.
name|filteringAliases
argument_list|(
name|request
operator|.
name|filteringAliases
argument_list|()
argument_list|)
operator|.
name|nowInMillis
argument_list|(
name|request
operator|.
name|nowInMillis
argument_list|()
argument_list|)
argument_list|,
name|shardTarget
argument_list|,
name|indexShard
operator|.
name|acquireSearcher
argument_list|(
literal|"exists"
argument_list|)
argument_list|,
name|indexService
argument_list|,
name|indexShard
argument_list|,
name|scriptService
argument_list|,
name|pageCacheRecycler
argument_list|,
name|bigArrays
argument_list|,
name|threadPool
operator|.
name|estimatedTimeInMillisCounter
argument_list|()
argument_list|)
decl_stmt|;
name|SearchContext
operator|.
name|setCurrent
argument_list|(
name|context
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|request
operator|.
name|minScore
argument_list|()
operator|!=
name|DEFAULT_MIN_SCORE
condition|)
block|{
name|context
operator|.
name|minimumScore
argument_list|(
name|request
operator|.
name|minScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|BytesReference
name|source
init|=
name|request
operator|.
name|querySource
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
operator|&&
name|source
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|request
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|parsedQuery
argument_list|(
name|indexService
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseQuery
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|QueryParseContext
operator|.
name|removeTypes
argument_list|()
expr_stmt|;
block|}
block|}
name|context
operator|.
name|preProcess
argument_list|()
expr_stmt|;
try|try
block|{
name|Lucene
operator|.
name|EarlyTerminatingCollector
name|existsCollector
init|=
name|Lucene
operator|.
name|createExistsCollector
argument_list|()
decl_stmt|;
name|Lucene
operator|.
name|exists
argument_list|(
name|context
operator|.
name|searcher
argument_list|()
argument_list|,
name|context
operator|.
name|query
argument_list|()
argument_list|,
name|existsCollector
argument_list|)
expr_stmt|;
return|return
operator|new
name|ShardExistsResponse
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|,
name|existsCollector
operator|.
name|exists
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"failed to execute exists"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
comment|// this will also release the index searcher
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * An async broadcast action that early terminates shard request      * upon any shard response reporting matched doc existence      */
DECL|class|ExistsAsyncBroadcastAction
specifier|final
specifier|private
class|class
name|ExistsAsyncBroadcastAction
extends|extends
name|AsyncBroadcastAction
block|{
DECL|field|processed
specifier|final
name|AtomicBoolean
name|processed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|ExistsAsyncBroadcastAction
name|ExistsAsyncBroadcastAction
parameter_list|(
name|ExistsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ExistsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onOperation
specifier|protected
name|void
name|onOperation
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|int
name|shardIndex
parameter_list|,
name|ShardExistsResponse
name|response
parameter_list|)
block|{
name|super
operator|.
name|onOperation
argument_list|(
name|shard
argument_list|,
name|shardIndex
argument_list|,
name|response
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|exists
argument_list|()
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|performOperation
specifier|protected
name|void
name|performOperation
parameter_list|(
specifier|final
name|ShardIterator
name|shardIt
parameter_list|,
specifier|final
name|ShardRouting
name|shard
parameter_list|,
specifier|final
name|int
name|shardIndex
parameter_list|)
block|{
if|if
condition|(
name|processed
operator|.
name|get
argument_list|()
condition|)
block|{
return|return;
block|}
name|super
operator|.
name|performOperation
argument_list|(
name|shardIt
argument_list|,
name|shard
argument_list|,
name|shardIndex
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finishHim
specifier|protected
name|void
name|finishHim
parameter_list|()
block|{
if|if
condition|(
name|processed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|super
operator|.
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

