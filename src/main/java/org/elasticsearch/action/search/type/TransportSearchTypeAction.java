begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntArrayList
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
name|NoShardAvailableActionException
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
name|action
operator|.
name|support
operator|.
name|TransportAction
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
name|TransportActions
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
name|concurrent
operator|.
name|AtomicArray
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
name|SearchPhaseResult
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
name|QuerySearchResultProvider
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
name|internalSearchRequest
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportSearchTypeAction
specifier|public
specifier|abstract
class|class
name|TransportSearchTypeAction
extends|extends
name|TransportAction
argument_list|<
name|SearchRequest
argument_list|,
name|SearchResponse
argument_list|>
block|{
DECL|field|clusterService
specifier|protected
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|searchService
specifier|protected
specifier|final
name|SearchServiceTransportAction
name|searchService
decl_stmt|;
DECL|field|searchPhaseController
specifier|protected
specifier|final
name|SearchPhaseController
name|searchPhaseController
decl_stmt|;
DECL|method|TransportSearchTypeAction
specifier|public
name|TransportSearchTypeAction
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
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
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
DECL|class|BaseAsyncAction
specifier|protected
specifier|abstract
class|class
name|BaseAsyncAction
parameter_list|<
name|FirstResult
extends|extends
name|SearchPhaseResult
parameter_list|>
block|{
DECL|field|listener
specifier|protected
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
decl_stmt|;
DECL|field|shardsIts
specifier|protected
specifier|final
name|GroupShardsIterator
name|shardsIts
decl_stmt|;
DECL|field|request
specifier|protected
specifier|final
name|SearchRequest
name|request
decl_stmt|;
DECL|field|clusterState
specifier|protected
specifier|final
name|ClusterState
name|clusterState
decl_stmt|;
DECL|field|nodes
specifier|protected
specifier|final
name|DiscoveryNodes
name|nodes
decl_stmt|;
DECL|field|expectedSuccessfulOps
specifier|protected
specifier|final
name|int
name|expectedSuccessfulOps
decl_stmt|;
DECL|field|expectedTotalOps
specifier|private
specifier|final
name|int
name|expectedTotalOps
decl_stmt|;
DECL|field|successulOps
specifier|protected
specifier|final
name|AtomicInteger
name|successulOps
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|totalOps
specifier|private
specifier|final
name|AtomicInteger
name|totalOps
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|firstResults
specifier|protected
specifier|final
name|AtomicArray
argument_list|<
name|FirstResult
argument_list|>
name|firstResults
decl_stmt|;
DECL|field|shardFailures
specifier|private
specifier|volatile
name|AtomicArray
argument_list|<
name|ShardSearchFailure
argument_list|>
name|shardFailures
decl_stmt|;
DECL|field|shardFailuresMutex
specifier|private
specifier|final
name|Object
name|shardFailuresMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|sortedShardList
specifier|protected
specifier|volatile
name|ScoreDoc
index|[]
name|sortedShardList
decl_stmt|;
DECL|field|startTime
specifier|protected
specifier|final
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|method|BaseAsyncAction
specifier|protected
name|BaseAsyncAction
parameter_list|(
name|SearchRequest
name|request
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
name|clusterState
operator|=
name|clusterService
operator|.
name|state
argument_list|()
expr_stmt|;
name|nodes
operator|=
name|clusterState
operator|.
name|nodes
argument_list|()
expr_stmt|;
name|clusterState
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedRaiseException
argument_list|(
name|ClusterBlockLevel
operator|.
name|READ
argument_list|)
expr_stmt|;
name|String
index|[]
name|concreteIndices
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|request
operator|.
name|indicesOptions
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
name|clusterState
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedRaiseException
argument_list|(
name|ClusterBlockLevel
operator|.
name|READ
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
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
name|shardsIts
operator|=
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
expr_stmt|;
name|expectedSuccessfulOps
operator|=
name|shardsIts
operator|.
name|size
argument_list|()
expr_stmt|;
comment|// we need to add 1 for non active partition, since we count it in the total!
name|expectedTotalOps
operator|=
name|shardsIts
operator|.
name|totalSizeWith1ForEmpty
argument_list|()
expr_stmt|;
name|firstResults
operator|=
operator|new
name|AtomicArray
argument_list|<
name|FirstResult
argument_list|>
argument_list|(
name|shardsIts
operator|.
name|size
argument_list|()
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
name|expectedSuccessfulOps
operator|==
literal|0
condition|)
block|{
comment|// no search shards to search on, bail with empty response (it happens with search across _all with no indices around and consistent with broadcast operations)
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|SearchResponse
argument_list|(
name|InternalSearchResponse
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|,
name|ShardSearchFailure
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|request
operator|.
name|beforeStart
argument_list|()
expr_stmt|;
comment|// count the local operations, and perform the non local ones
name|int
name|localOperations
init|=
literal|0
decl_stmt|;
name|int
name|shardIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|ShardIterator
name|shardIt
range|:
name|shardsIts
control|)
block|{
name|shardIndex
operator|++
expr_stmt|;
specifier|final
name|ShardRouting
name|shard
init|=
name|shardIt
operator|.
name|firstOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodes
operator|.
name|localNodeId
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
comment|// do the remote operation here, the localAsync flag is not relevant
name|performFirstPhase
argument_list|(
name|shardIndex
argument_list|,
name|shardIt
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// really, no shards active in this group
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|shardIt
argument_list|,
operator|new
name|NoShardAvailableActionException
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// we have local operations, perform them now
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
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
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
name|int
name|shardIndex
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
specifier|final
name|ShardIterator
name|shardIt
range|:
name|shardsIts
control|)
block|{
name|shardIndex
operator|++
expr_stmt|;
specifier|final
name|ShardRouting
name|shard
init|=
name|shardIt
operator|.
name|firstOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodes
operator|.
name|localNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|performFirstPhase
argument_list|(
name|shardIndex
argument_list|,
name|shardIt
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|localAsync
condition|)
block|{
name|request
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
block|}
name|shardIndex
operator|=
operator|-
literal|1
expr_stmt|;
for|for
control|(
specifier|final
name|ShardIterator
name|shardIt
range|:
name|shardsIts
control|)
block|{
name|shardIndex
operator|++
expr_stmt|;
specifier|final
name|int
name|fShardIndex
init|=
name|shardIndex
decl_stmt|;
specifier|final
name|ShardRouting
name|shard
init|=
name|shardIt
operator|.
name|firstOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodes
operator|.
name|localNodeId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|localAsync
condition|)
block|{
try|try
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
name|performFirstPhase
argument_list|(
name|fShardIndex
argument_list|,
name|shardIt
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardIt
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|performFirstPhase
argument_list|(
name|fShardIndex
argument_list|,
name|shardIt
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
block|}
DECL|method|performFirstPhase
name|void
name|performFirstPhase
parameter_list|(
specifier|final
name|int
name|shardIndex
parameter_list|,
specifier|final
name|ShardIterator
name|shardIt
parameter_list|)
block|{
name|performFirstPhase
argument_list|(
name|shardIndex
argument_list|,
name|shardIt
argument_list|,
name|shardIt
operator|.
name|nextOrNull
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|performFirstPhase
name|void
name|performFirstPhase
parameter_list|(
specifier|final
name|int
name|shardIndex
parameter_list|,
specifier|final
name|ShardIterator
name|shardIt
parameter_list|,
specifier|final
name|ShardRouting
name|shard
parameter_list|)
block|{
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
block|{
comment|// no more active shards... (we should not really get here, but just for safety)
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|shardIt
argument_list|,
operator|new
name|NoShardAvailableActionException
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|shard
operator|.
name|currentNodeId
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
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
literal|null
argument_list|,
name|shardIt
argument_list|,
operator|new
name|NoShardAvailableActionException
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|filteringAliases
init|=
name|clusterState
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
name|sendExecuteFirstPhase
argument_list|(
name|node
argument_list|,
name|internalSearchRequest
argument_list|(
name|shard
argument_list|,
name|shardsIts
operator|.
name|size
argument_list|()
argument_list|,
name|request
argument_list|,
name|filteringAliases
argument_list|,
name|startTime
argument_list|)
argument_list|,
operator|new
name|SearchServiceListener
argument_list|<
name|FirstResult
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResult
parameter_list|(
name|FirstResult
name|result
parameter_list|)
block|{
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|result
argument_list|,
name|shardIt
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
name|t
parameter_list|)
block|{
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|node
operator|.
name|id
argument_list|()
argument_list|,
name|shardIt
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onFirstPhaseResult
name|void
name|onFirstPhaseResult
parameter_list|(
name|int
name|shardIndex
parameter_list|,
name|ShardRouting
name|shard
parameter_list|,
name|FirstResult
name|result
parameter_list|,
name|ShardIterator
name|shardIt
parameter_list|)
block|{
name|result
operator|.
name|shardTarget
argument_list|(
operator|new
name|SearchShardTarget
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shard
operator|.
name|index
argument_list|()
argument_list|,
name|shard
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|processFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|result
argument_list|)
expr_stmt|;
comment|// increment all the "future" shards to update the total ops since we some may work and some may not...
comment|// and when that happens, we break on total ops, so we must maintain them
name|int
name|xTotalOps
init|=
name|totalOps
operator|.
name|addAndGet
argument_list|(
name|shardIt
operator|.
name|remaining
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|successulOps
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|xTotalOps
operator|==
name|expectedTotalOps
condition|)
block|{
try|try
block|{
name|innerMoveToSecondPhase
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
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
name|shardIt
operator|.
name|shardId
argument_list|()
operator|+
literal|": Failed to execute ["
operator|+
name|request
operator|+
literal|"] while moving to second phase"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ReduceSearchPhaseException
argument_list|(
name|firstPhaseName
argument_list|()
argument_list|,
literal|""
argument_list|,
name|e
argument_list|,
name|buildShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onFirstPhaseResult
name|void
name|onFirstPhaseResult
parameter_list|(
specifier|final
name|int
name|shardIndex
parameter_list|,
annotation|@
name|Nullable
name|ShardRouting
name|shard
parameter_list|,
annotation|@
name|Nullable
name|String
name|nodeId
parameter_list|,
specifier|final
name|ShardIterator
name|shardIt
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
comment|// we always add the shard failure for a specific shard instance
comment|// we do make sure to clean it on a successful response from a shard
name|SearchShardTarget
name|shardTarget
init|=
operator|new
name|SearchShardTarget
argument_list|(
name|nodeId
argument_list|,
name|shardIt
operator|.
name|shardId
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|,
name|shardIt
operator|.
name|shardId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|addShardFailure
argument_list|(
name|shardIndex
argument_list|,
name|shardTarget
argument_list|,
name|t
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalOps
operator|.
name|incrementAndGet
argument_list|()
operator|==
name|expectedTotalOps
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
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
operator|!
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|t
argument_list|)
condition|)
block|{
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|shard
operator|.
name|shortSummary
argument_list|()
operator|+
literal|": Failed to execute ["
operator|+
name|request
operator|+
literal|"]"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
operator|+
literal|": Failed to execute ["
operator|+
name|request
operator|+
literal|"]"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|successulOps
operator|.
name|get
argument_list|()
operator|==
literal|0
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
literal|"All shards failed for phase: [{}]"
argument_list|,
name|firstPhaseName
argument_list|()
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
comment|// no successful ops, raise an exception
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|SearchPhaseExecutionException
argument_list|(
name|firstPhaseName
argument_list|()
argument_list|,
literal|"all shards failed"
argument_list|,
name|buildShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|innerMoveToSecondPhase
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ReduceSearchPhaseException
argument_list|(
name|firstPhaseName
argument_list|()
argument_list|,
literal|""
argument_list|,
name|e
argument_list|,
name|buildShardFailures
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
specifier|final
name|ShardRouting
name|nextShard
init|=
name|shardIt
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|lastShard
init|=
name|nextShard
operator|==
literal|null
decl_stmt|;
comment|// trace log this exception
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
operator|&&
name|t
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|executionFailureMsg
argument_list|(
name|shard
argument_list|,
name|shardIt
argument_list|,
name|request
argument_list|,
name|lastShard
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|lastShard
condition|)
block|{
try|try
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
name|performFirstPhase
argument_list|(
name|shardIndex
argument_list|,
name|shardIt
argument_list|,
name|nextShard
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t1
parameter_list|)
block|{
name|onFirstPhaseResult
argument_list|(
name|shardIndex
argument_list|,
name|shard
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardIt
argument_list|,
name|t1
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// no more shards active, add a failure
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|!
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
comment|// do not double log this exception
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
operator|!
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|executionFailureMsg
argument_list|(
name|shard
argument_list|,
name|shardIt
argument_list|,
name|request
argument_list|,
name|lastShard
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|executionFailureMsg
specifier|private
name|String
name|executionFailureMsg
parameter_list|(
annotation|@
name|Nullable
name|ShardRouting
name|shard
parameter_list|,
specifier|final
name|ShardIterator
name|shardIt
parameter_list|,
name|SearchRequest
name|request
parameter_list|,
name|boolean
name|lastShard
parameter_list|)
block|{
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
return|return
name|shard
operator|.
name|shortSummary
argument_list|()
operator|+
literal|": Failed to execute ["
operator|+
name|request
operator|+
literal|"] lastShard ["
operator|+
name|lastShard
operator|+
literal|"]"
return|;
block|}
else|else
block|{
return|return
name|shardIt
operator|.
name|shardId
argument_list|()
operator|+
literal|": Failed to execute ["
operator|+
name|request
operator|+
literal|"] lastShard ["
operator|+
name|lastShard
operator|+
literal|"]"
return|;
block|}
block|}
comment|/**          * Builds how long it took to execute the search.          */
DECL|method|buildTookInMillis
specifier|protected
specifier|final
name|long
name|buildTookInMillis
parameter_list|()
block|{
return|return
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
return|;
block|}
DECL|method|buildShardFailures
specifier|protected
specifier|final
name|ShardSearchFailure
index|[]
name|buildShardFailures
parameter_list|()
block|{
name|AtomicArray
argument_list|<
name|ShardSearchFailure
argument_list|>
name|shardFailures
init|=
name|this
operator|.
name|shardFailures
decl_stmt|;
if|if
condition|(
name|shardFailures
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
name|List
argument_list|<
name|AtomicArray
operator|.
name|Entry
argument_list|<
name|ShardSearchFailure
argument_list|>
argument_list|>
name|entries
init|=
name|shardFailures
operator|.
name|asList
argument_list|()
decl_stmt|;
name|ShardSearchFailure
index|[]
name|failures
init|=
operator|new
name|ShardSearchFailure
index|[
name|entries
operator|.
name|size
argument_list|()
index|]
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
name|failures
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|failures
index|[
name|i
index|]
operator|=
name|entries
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|value
expr_stmt|;
block|}
return|return
name|failures
return|;
block|}
DECL|method|addShardFailure
specifier|protected
specifier|final
name|void
name|addShardFailure
parameter_list|(
specifier|final
name|int
name|shardIndex
parameter_list|,
annotation|@
name|Nullable
name|SearchShardTarget
name|shardTarget
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
comment|// we don't aggregate shard failures on non active shards (but do keep the header counts right)
if|if
condition|(
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|t
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// lazily create shard failures, so we can early build the empty shard failure list in most cases (no failures)
if|if
condition|(
name|shardFailures
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|shardFailuresMutex
init|)
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
name|AtomicArray
argument_list|<
name|ShardSearchFailure
argument_list|>
argument_list|(
name|shardsIts
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ShardSearchFailure
name|failure
init|=
name|shardFailures
operator|.
name|get
argument_list|(
name|shardIndex
argument_list|)
decl_stmt|;
if|if
condition|(
name|failure
operator|==
literal|null
condition|)
block|{
name|shardFailures
operator|.
name|set
argument_list|(
name|shardIndex
argument_list|,
operator|new
name|ShardSearchFailure
argument_list|(
name|t
argument_list|,
name|shardTarget
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the failure is already present, try and not override it with an exception that is less meaningless
comment|// for example, getting illegal shard state
if|if
condition|(
name|TransportActions
operator|.
name|isReadOverrideException
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|shardFailures
operator|.
name|set
argument_list|(
name|shardIndex
argument_list|,
operator|new
name|ShardSearchFailure
argument_list|(
name|t
argument_list|,
name|shardTarget
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**          * Releases shard targets that are not used in the docsIdsToLoad.          */
DECL|method|releaseIrrelevantSearchContexts
specifier|protected
name|void
name|releaseIrrelevantSearchContexts
parameter_list|(
name|AtomicArray
argument_list|<
name|?
extends|extends
name|QuerySearchResultProvider
argument_list|>
name|queryResults
parameter_list|,
name|AtomicArray
argument_list|<
name|IntArrayList
argument_list|>
name|docIdsToLoad
parameter_list|)
block|{
if|if
condition|(
name|docIdsToLoad
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// we only release search context that we did not fetch from if we are not scrolling
if|if
condition|(
name|request
operator|.
name|scroll
argument_list|()
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|AtomicArray
operator|.
name|Entry
argument_list|<
name|?
extends|extends
name|QuerySearchResultProvider
argument_list|>
name|entry
range|:
name|queryResults
operator|.
name|asList
argument_list|()
control|)
block|{
if|if
condition|(
name|docIdsToLoad
operator|.
name|get
argument_list|(
name|entry
operator|.
name|index
argument_list|)
operator|==
literal|null
condition|)
block|{
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|entry
operator|.
name|value
operator|.
name|queryResult
argument_list|()
operator|.
name|shardTarget
argument_list|()
operator|.
name|nodeId
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
comment|// should not happen (==null) but safeguard anyhow
name|searchService
operator|.
name|sendFreeContext
argument_list|(
name|node
argument_list|,
name|entry
operator|.
name|value
operator|.
name|queryResult
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|sendExecuteFirstPhase
specifier|protected
specifier|abstract
name|void
name|sendExecuteFirstPhase
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ShardSearchRequest
name|request
parameter_list|,
name|SearchServiceListener
argument_list|<
name|FirstResult
argument_list|>
name|listener
parameter_list|)
function_decl|;
DECL|method|processFirstPhaseResult
specifier|protected
specifier|final
name|void
name|processFirstPhaseResult
parameter_list|(
name|int
name|shardIndex
parameter_list|,
name|ShardRouting
name|shard
parameter_list|,
name|FirstResult
name|result
parameter_list|)
block|{
name|firstResults
operator|.
name|set
argument_list|(
name|shardIndex
argument_list|,
name|result
argument_list|)
expr_stmt|;
comment|// clean a previous error on this shard group (note, this code will be serialized on the same shardIndex value level
comment|// so its ok concurrency wise to miss potentially the shard failures being created because of another failure
comment|// in the #addShardFailure, because by definition, it will happen on *another* shardIndex
name|AtomicArray
argument_list|<
name|ShardSearchFailure
argument_list|>
name|shardFailures
init|=
name|this
operator|.
name|shardFailures
decl_stmt|;
if|if
condition|(
name|shardFailures
operator|!=
literal|null
condition|)
block|{
name|shardFailures
operator|.
name|set
argument_list|(
name|shardIndex
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerMoveToSecondPhase
specifier|final
name|void
name|innerMoveToSecondPhase
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|boolean
name|hadOne
init|=
literal|false
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
name|firstResults
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|FirstResult
name|result
init|=
name|firstResults
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
continue|continue;
comment|// failure
block|}
if|if
condition|(
name|hadOne
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|hadOne
operator|=
literal|true
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|result
operator|.
name|shardTarget
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"Moving to second phase, based on results from: {} (cluster state version: {})"
argument_list|,
name|sb
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|moveToSecondPhase
argument_list|()
expr_stmt|;
block|}
DECL|method|moveToSecondPhase
specifier|protected
specifier|abstract
name|void
name|moveToSecondPhase
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|firstPhaseName
specifier|protected
specifier|abstract
name|String
name|firstPhaseName
parameter_list|()
function_decl|;
block|}
block|}
end_class

end_unit

