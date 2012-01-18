begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search.type
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|type
package|;
end_package

begin_import
import|import
name|jsr166y
operator|.
name|LinkedTransferQueue
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
name|search
operator|.
name|ScoreDoc
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
name|search
operator|.
name|*
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
name|node
operator|.
name|DiscoveryNode
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
name|action
operator|.
name|SearchServiceListener
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
name|action
operator|.
name|SearchServiceTransportAction
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
name|controller
operator|.
name|SearchPhaseController
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
name|controller
operator|.
name|ShardDoc
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
name|controller
operator|.
name|ShardScoreDoc
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
name|fetch
operator|.
name|QueryFetchSearchResult
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
name|InternalSearchHits
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
name|InternalSearchResponse
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
name|Map
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|type
operator|.
name|TransportSearchHelper
operator|.
name|internalScrollSearchRequest
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportSearchScrollScanAction
specifier|public
class|class
name|TransportSearchScrollScanAction
extends|extends
name|AbstractComponent
block|{
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
DECL|field|searchService
specifier|private
specifier|final
name|SearchServiceTransportAction
name|searchService
decl_stmt|;
DECL|field|searchPhaseController
specifier|private
specifier|final
name|SearchPhaseController
name|searchPhaseController
decl_stmt|;
DECL|field|searchCache
specifier|private
specifier|final
name|TransportSearchCache
name|searchCache
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportSearchScrollScanAction
specifier|public
name|TransportSearchScrollScanAction
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
name|TransportSearchCache
name|searchCache
parameter_list|,
name|SearchServiceTransportAction
name|searchService
parameter_list|,
name|SearchPhaseController
name|searchPhaseController
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
name|searchCache
operator|=
name|searchCache
expr_stmt|;
name|this
operator|.
name|searchService
operator|=
name|searchService
expr_stmt|;
name|this
operator|.
name|searchPhaseController
operator|=
name|searchPhaseController
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|,
name|ParsedScrollId
name|scrollId
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
operator|new
name|AsyncAction
argument_list|(
name|request
argument_list|,
name|scrollId
argument_list|,
name|listener
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|class|AsyncAction
specifier|private
class|class
name|AsyncAction
block|{
DECL|field|request
specifier|private
specifier|final
name|SearchScrollRequest
name|request
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
decl_stmt|;
DECL|field|scrollId
specifier|private
specifier|final
name|ParsedScrollId
name|scrollId
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|DiscoveryNodes
name|nodes
decl_stmt|;
DECL|field|shardFailures
specifier|protected
specifier|volatile
name|LinkedTransferQueue
argument_list|<
name|ShardSearchFailure
argument_list|>
name|shardFailures
decl_stmt|;
DECL|field|queryFetchResults
specifier|private
specifier|final
name|Map
argument_list|<
name|SearchShardTarget
argument_list|,
name|QueryFetchSearchResult
argument_list|>
name|queryFetchResults
init|=
name|searchCache
operator|.
name|obtainQueryFetchResults
argument_list|()
decl_stmt|;
DECL|field|successfulOps
specifier|private
specifier|final
name|AtomicInteger
name|successfulOps
decl_stmt|;
DECL|field|counter
specifier|private
specifier|final
name|AtomicInteger
name|counter
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|AsyncAction
specifier|private
name|AsyncAction
parameter_list|(
name|SearchScrollRequest
name|request
parameter_list|,
name|ParsedScrollId
name|scrollId
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|scrollId
operator|=
name|scrollId
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
expr_stmt|;
name|this
operator|.
name|successfulOps
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|scrollId
operator|.
name|context
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|counter
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|scrollId
operator|.
name|context
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|buildShardFailures
specifier|protected
specifier|final
name|ShardSearchFailure
index|[]
name|buildShardFailures
parameter_list|()
block|{
name|LinkedTransferQueue
argument_list|<
name|ShardSearchFailure
argument_list|>
name|localFailures
init|=
name|shardFailures
decl_stmt|;
if|if
condition|(
name|localFailures
operator|==
literal|null
condition|)
block|{
return|return
name|ShardSearchFailure
operator|.
name|EMPTY_ARRAY
return|;
block|}
return|return
name|localFailures
operator|.
name|toArray
argument_list|(
name|ShardSearchFailure
operator|.
name|EMPTY_ARRAY
argument_list|)
return|;
block|}
comment|// we do our best to return the shard failures, but its ok if its not fully concurrently safe
comment|// we simply try and return as much as possible
DECL|method|addShardFailure
specifier|protected
specifier|final
name|void
name|addShardFailure
parameter_list|(
name|ShardSearchFailure
name|failure
parameter_list|)
block|{
if|if
condition|(
name|shardFailures
operator|==
literal|null
condition|)
block|{
name|shardFailures
operator|=
operator|new
name|LinkedTransferQueue
argument_list|<
name|ShardSearchFailure
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|shardFailures
operator|.
name|add
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
name|scrollId
operator|.
name|context
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
specifier|final
name|InternalSearchResponse
name|internalResponse
init|=
operator|new
name|InternalSearchResponse
argument_list|(
operator|new
name|InternalSearchHits
argument_list|(
name|InternalSearchHits
operator|.
name|EMPTY
argument_list|,
name|Long
operator|.
name|parseLong
argument_list|(
name|this
operator|.
name|scrollId
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"total_hits"
argument_list|)
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|searchCache
operator|.
name|releaseQueryFetchResults
argument_list|(
name|queryFetchResults
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|SearchResponse
argument_list|(
name|internalResponse
argument_list|,
name|request
operator|.
name|scrollId
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0l
argument_list|,
name|buildShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|localOperations
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|target
range|:
name|scrollId
operator|.
name|context
argument_list|()
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|target
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nodes
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|localOperations
operator|++
expr_stmt|;
block|}
else|else
block|{
name|executePhase
argument_list|(
name|node
argument_list|,
name|target
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Node ["
operator|+
name|target
operator|.
name|v1
argument_list|()
operator|+
literal|"] not available for scroll request ["
operator|+
name|scrollId
operator|.
name|source
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|successfulOps
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
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
block|}
if|if
condition|(
name|localOperations
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|operationThreading
argument_list|()
operator|==
name|SearchOperationThreading
operator|.
name|SINGLE_THREAD
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
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
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|target
range|:
name|scrollId
operator|.
name|context
argument_list|()
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|target
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|nodes
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|executePhase
argument_list|(
name|node
argument_list|,
name|target
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|localAsync
init|=
name|request
operator|.
name|operationThreading
argument_list|()
operator|==
name|SearchOperationThreading
operator|.
name|THREAD_PER_SHARD
decl_stmt|;
for|for
control|(
specifier|final
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|target
range|:
name|scrollId
operator|.
name|context
argument_list|()
control|)
block|{
specifier|final
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|target
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|nodes
operator|.
name|localNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|localAsync
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
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
name|executePhase
argument_list|(
name|node
argument_list|,
name|target
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|executePhase
argument_list|(
name|node
argument_list|,
name|target
operator|.
name|v2
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|target
range|:
name|scrollId
operator|.
name|context
argument_list|()
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|target
operator|.
name|v1
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Node ["
operator|+
name|target
operator|.
name|v1
argument_list|()
operator|+
literal|"] not available for scroll request ["
operator|+
name|scrollId
operator|.
name|source
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|successfulOps
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
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
else|else
block|{                 }
block|}
block|}
DECL|method|executePhase
specifier|private
name|void
name|executePhase
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|long
name|searchId
parameter_list|)
block|{
name|searchService
operator|.
name|sendExecuteScan
argument_list|(
name|node
argument_list|,
name|internalScrollSearchRequest
argument_list|(
name|searchId
argument_list|,
name|request
argument_list|)
argument_list|,
operator|new
name|SearchServiceListener
argument_list|<
name|QueryFetchSearchResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResult
parameter_list|(
name|QueryFetchSearchResult
name|result
parameter_list|)
block|{
name|queryFetchResults
operator|.
name|put
argument_list|(
name|result
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
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
name|t
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] Failed to execute query phase"
argument_list|,
name|t
argument_list|,
name|searchId
argument_list|)
expr_stmt|;
block|}
name|addShardFailure
argument_list|(
operator|new
name|ShardSearchFailure
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
name|successfulOps
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
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
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|finishHim
specifier|private
name|void
name|finishHim
parameter_list|()
block|{
try|try
block|{
name|innerFinishHim
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ReduceSearchPhaseException
name|failure
init|=
operator|new
name|ReduceSearchPhaseException
argument_list|(
literal|"fetch"
argument_list|,
literal|""
argument_list|,
name|e
argument_list|,
name|buildShardFailures
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to reduce search"
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|searchCache
operator|.
name|releaseQueryFetchResults
argument_list|(
name|queryFetchResults
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerFinishHim
specifier|private
name|void
name|innerFinishHim
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|numberOfHits
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QueryFetchSearchResult
name|shardResult
range|:
name|queryFetchResults
operator|.
name|values
argument_list|()
control|)
block|{
name|numberOfHits
operator|+=
name|shardResult
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
operator|.
name|length
expr_stmt|;
block|}
name|ShardDoc
index|[]
name|docs
init|=
operator|new
name|ShardDoc
index|[
name|numberOfHits
index|]
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|QueryFetchSearchResult
name|shardResult
range|:
name|queryFetchResults
operator|.
name|values
argument_list|()
control|)
block|{
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|shardResult
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|scoreDoc
range|:
name|scoreDocs
control|)
block|{
name|docs
index|[
name|counter
operator|++
index|]
operator|=
operator|new
name|ShardScoreDoc
argument_list|(
name|shardResult
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|scoreDoc
operator|.
name|doc
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|InternalSearchResponse
name|internalResponse
init|=
name|searchPhaseController
operator|.
name|merge
argument_list|(
name|docs
argument_list|,
name|queryFetchResults
argument_list|,
name|queryFetchResults
argument_list|)
decl_stmt|;
operator|(
operator|(
name|InternalSearchHits
operator|)
name|internalResponse
operator|.
name|hits
argument_list|()
operator|)
operator|.
name|totalHits
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|this
operator|.
name|scrollId
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
literal|"total_hits"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|QueryFetchSearchResult
name|shardResult
range|:
name|queryFetchResults
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|shardResult
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
operator|.
name|length
operator|<
name|shardResult
operator|.
name|queryResult
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// we found more than we want for this round, remove this from our scrolling
name|queryFetchResults
operator|.
name|remove
argument_list|(
name|shardResult
operator|.
name|shardTarget
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|scrollId
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|scroll
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// we rebuild the scroll id since we remove shards that we finished scrolling on
name|scrollId
operator|=
name|TransportSearchHelper
operator|.
name|buildScrollId
argument_list|(
name|this
operator|.
name|scrollId
operator|.
name|type
argument_list|()
argument_list|,
name|queryFetchResults
operator|.
name|values
argument_list|()
argument_list|,
name|this
operator|.
name|scrollId
operator|.
name|attributes
argument_list|()
argument_list|)
expr_stmt|;
comment|// continue moving the total_hits
block|}
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|SearchResponse
argument_list|(
name|internalResponse
argument_list|,
name|scrollId
argument_list|,
name|this
operator|.
name|scrollId
operator|.
name|context
argument_list|()
operator|.
name|length
argument_list|,
name|successfulOps
operator|.
name|get
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|,
name|buildShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

