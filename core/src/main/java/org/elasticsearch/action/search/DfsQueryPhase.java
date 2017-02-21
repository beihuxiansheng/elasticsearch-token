begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|dfs
operator|.
name|AggregatedDfs
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
name|dfs
operator|.
name|DfsSearchResult
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
name|QuerySearchRequest
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
name|transport
operator|.
name|Transport
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
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * This search phase fans out to every shards to execute a distributed search with a pre-collected distributed frequencies for all  * search terms used in the actual search query. This phase is very similar to a the default query-then-fetch search phase but it doesn't  * retry on another shard if any of the shards are failing. Failures are treated as shard failures and are counted as a non-successful  * operation.  * @see CountedCollector#onFailure(int, SearchShardTarget, Exception)  */
end_comment

begin_class
DECL|class|DfsQueryPhase
specifier|final
class|class
name|DfsQueryPhase
extends|extends
name|SearchPhase
block|{
DECL|field|queryResult
specifier|private
specifier|final
name|InitialSearchPhase
operator|.
name|SearchPhaseResults
argument_list|<
name|QuerySearchResultProvider
argument_list|>
name|queryResult
decl_stmt|;
DECL|field|searchPhaseController
specifier|private
specifier|final
name|SearchPhaseController
name|searchPhaseController
decl_stmt|;
DECL|field|dfsSearchResults
specifier|private
specifier|final
name|AtomicArray
argument_list|<
name|DfsSearchResult
argument_list|>
name|dfsSearchResults
decl_stmt|;
DECL|field|nextPhaseFactory
specifier|private
specifier|final
name|Function
argument_list|<
name|InitialSearchPhase
operator|.
name|SearchPhaseResults
argument_list|<
name|QuerySearchResultProvider
argument_list|>
argument_list|,
name|SearchPhase
argument_list|>
name|nextPhaseFactory
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|SearchPhaseContext
name|context
decl_stmt|;
DECL|field|searchTransportService
specifier|private
specifier|final
name|SearchTransportService
name|searchTransportService
decl_stmt|;
DECL|method|DfsQueryPhase
name|DfsQueryPhase
parameter_list|(
name|AtomicArray
argument_list|<
name|DfsSearchResult
argument_list|>
name|dfsSearchResults
parameter_list|,
name|SearchPhaseController
name|searchPhaseController
parameter_list|,
name|Function
argument_list|<
name|InitialSearchPhase
operator|.
name|SearchPhaseResults
argument_list|<
name|QuerySearchResultProvider
argument_list|>
argument_list|,
name|SearchPhase
argument_list|>
name|nextPhaseFactory
parameter_list|,
name|SearchPhaseContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
literal|"dfs_query"
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryResult
operator|=
name|searchPhaseController
operator|.
name|newSearchPhaseResults
argument_list|(
name|context
operator|.
name|getRequest
argument_list|()
argument_list|,
name|context
operator|.
name|getNumShards
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchPhaseController
operator|=
name|searchPhaseController
expr_stmt|;
name|this
operator|.
name|dfsSearchResults
operator|=
name|dfsSearchResults
expr_stmt|;
name|this
operator|.
name|nextPhaseFactory
operator|=
name|nextPhaseFactory
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|searchTransportService
operator|=
name|context
operator|.
name|getSearchTransport
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
comment|// TODO we can potentially also consume the actual per shard results from the initial phase here in the aggregateDfs
comment|// to free up memory early
specifier|final
name|AggregatedDfs
name|dfs
init|=
name|searchPhaseController
operator|.
name|aggregateDfs
argument_list|(
name|dfsSearchResults
argument_list|)
decl_stmt|;
specifier|final
name|CountedCollector
argument_list|<
name|QuerySearchResultProvider
argument_list|>
name|counter
init|=
operator|new
name|CountedCollector
argument_list|<>
argument_list|(
name|queryResult
operator|::
name|consumeResult
argument_list|,
name|dfsSearchResults
operator|.
name|asList
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
parameter_list|()
lambda|->
block|{
name|context
operator|.
name|executeNextPhase
argument_list|(
name|this
argument_list|,
name|nextPhaseFactory
operator|.
name|apply
argument_list|(
name|queryResult
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|,
name|context
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|AtomicArray
operator|.
name|Entry
argument_list|<
name|DfsSearchResult
argument_list|>
name|entry
range|:
name|dfsSearchResults
operator|.
name|asList
argument_list|()
control|)
block|{
name|DfsSearchResult
name|dfsResult
init|=
name|entry
operator|.
name|value
decl_stmt|;
specifier|final
name|int
name|shardIndex
init|=
name|entry
operator|.
name|index
decl_stmt|;
specifier|final
name|SearchShardTarget
name|searchShardTarget
init|=
name|dfsResult
operator|.
name|shardTarget
argument_list|()
decl_stmt|;
name|Transport
operator|.
name|Connection
name|connection
init|=
name|context
operator|.
name|getConnection
argument_list|(
name|searchShardTarget
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|QuerySearchRequest
name|querySearchRequest
init|=
operator|new
name|QuerySearchRequest
argument_list|(
name|context
operator|.
name|getRequest
argument_list|()
argument_list|,
name|dfsResult
operator|.
name|id
argument_list|()
argument_list|,
name|dfs
argument_list|)
decl_stmt|;
name|searchTransportService
operator|.
name|sendExecuteQuery
argument_list|(
name|connection
argument_list|,
name|querySearchRequest
argument_list|,
name|context
operator|.
name|getTask
argument_list|()
argument_list|,
name|ActionListener
operator|.
name|wrap
argument_list|(
name|result
lambda|->
name|counter
operator|.
name|onResult
argument_list|(
name|shardIndex
argument_list|,
name|result
argument_list|,
name|searchShardTarget
argument_list|)
argument_list|,
name|exception
lambda|->
block|{
lambda|try
block|{
if|if
condition|(
name|context
operator|.
name|getLogger
argument_list|()
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getLogger
argument_list|()
operator|.
name|debug
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"[{}] Failed to execute query phase"
argument_list|,
name|querySearchRequest
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
name|counter
operator|.
name|onFailure
argument_list|(
name|shardIndex
argument_list|,
name|searchShardTarget
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// the query might not have been executed at all (for example because thread pool rejected
comment|// execution) and the search context that was created in dfs phase might not be released.
comment|// release it again to be in the safe side
name|context
operator|.
name|sendReleaseSearchContext
argument_list|(
name|querySearchRequest
operator|.
name|id
argument_list|()
argument_list|,
name|connection
argument_list|)
expr_stmt|;
block|}
block|}
block|)
block|)
class|;
end_class

unit|}     } }
end_unit

