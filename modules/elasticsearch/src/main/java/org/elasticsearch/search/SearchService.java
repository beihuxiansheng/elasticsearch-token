begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
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
name|inject
operator|.
name|Inject
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
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonToken
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
name|search
operator|.
name|dfs
operator|.
name|CachedDfSource
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
name|DfsPhase
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
name|fetch
operator|.
name|FetchPhase
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
name|FetchSearchRequest
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
name|FetchSearchResult
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
name|InternalScrollSearchRequest
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
name|InternalSearchRequest
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
name|query
operator|.
name|QueryPhase
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
name|QuerySearchResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|timer
operator|.
name|TimerService
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
name|TimeValue
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
name|Unicode
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
name|util
operator|.
name|component
operator|.
name|Lifecycle
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
name|component
operator|.
name|LifecycleComponent
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
name|highscalelib
operator|.
name|NonBlockingHashMapLong
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
name|json
operator|.
name|Jackson
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
name|util
operator|.
name|timer
operator|.
name|Timeout
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
name|timer
operator|.
name|TimerTask
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
name|Arrays
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
name|concurrent
operator|.
name|TimeUnit
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
name|AtomicLong
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|TimeValue
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SearchService
specifier|public
class|class
name|SearchService
extends|extends
name|AbstractComponent
implements|implements
name|LifecycleComponent
argument_list|<
name|SearchService
argument_list|>
block|{
DECL|field|lifecycle
specifier|private
specifier|final
name|Lifecycle
name|lifecycle
init|=
operator|new
name|Lifecycle
argument_list|()
decl_stmt|;
DECL|field|jsonFactory
specifier|private
specifier|final
name|JsonFactory
name|jsonFactory
init|=
name|Jackson
operator|.
name|defaultJsonFactory
argument_list|()
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|timerService
specifier|private
specifier|final
name|TimerService
name|timerService
decl_stmt|;
DECL|field|dfsPhase
specifier|private
specifier|final
name|DfsPhase
name|dfsPhase
decl_stmt|;
DECL|field|queryPhase
specifier|private
specifier|final
name|QueryPhase
name|queryPhase
decl_stmt|;
DECL|field|fetchPhase
specifier|private
specifier|final
name|FetchPhase
name|fetchPhase
decl_stmt|;
DECL|field|defaultKeepAlive
specifier|private
specifier|final
name|TimeValue
name|defaultKeepAlive
decl_stmt|;
DECL|field|idGenerator
specifier|private
specifier|final
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|activeContexts
specifier|private
specifier|final
name|NonBlockingHashMapLong
argument_list|<
name|SearchContext
argument_list|>
name|activeContexts
init|=
operator|new
name|NonBlockingHashMapLong
argument_list|<
name|SearchContext
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|elementParsers
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
name|elementParsers
decl_stmt|;
DECL|method|SearchService
annotation|@
name|Inject
specifier|public
name|SearchService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|TimerService
name|timerService
parameter_list|,
name|DfsPhase
name|dfsPhase
parameter_list|,
name|QueryPhase
name|queryPhase
parameter_list|,
name|FetchPhase
name|fetchPhase
parameter_list|)
block|{
name|super
argument_list|(
name|settings
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
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|timerService
operator|=
name|timerService
expr_stmt|;
name|this
operator|.
name|dfsPhase
operator|=
name|dfsPhase
expr_stmt|;
name|this
operator|.
name|queryPhase
operator|=
name|queryPhase
expr_stmt|;
name|this
operator|.
name|fetchPhase
operator|=
name|fetchPhase
expr_stmt|;
name|this
operator|.
name|defaultKeepAlive
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"defaultKeepAlive"
argument_list|,
name|timeValueMinutes
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
name|elementParsers
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
argument_list|()
decl_stmt|;
name|elementParsers
operator|.
name|putAll
argument_list|(
name|dfsPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
expr_stmt|;
name|elementParsers
operator|.
name|putAll
argument_list|(
name|queryPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
expr_stmt|;
name|elementParsers
operator|.
name|putAll
argument_list|(
name|fetchPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|elementParsers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|elementParsers
argument_list|)
expr_stmt|;
block|}
DECL|method|lifecycleState
annotation|@
name|Override
specifier|public
name|Lifecycle
operator|.
name|State
name|lifecycleState
parameter_list|()
block|{
return|return
name|lifecycle
operator|.
name|state
argument_list|()
return|;
block|}
DECL|method|start
annotation|@
name|Override
specifier|public
name|SearchService
name|start
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|moveToStarted
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
name|this
return|;
block|}
DECL|method|stop
annotation|@
name|Override
specifier|public
name|SearchService
name|stop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|moveToStopped
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
for|for
control|(
name|SearchContext
name|context
range|:
name|activeContexts
operator|.
name|values
argument_list|()
control|)
block|{
name|freeContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
name|activeContexts
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|moveToClosed
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
DECL|method|executeDfsPhase
specifier|public
name|DfsSearchResult
name|executeDfsPhase
parameter_list|(
name|InternalSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|createContext
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|activeContexts
operator|.
name|put
argument_list|(
name|context
operator|.
name|id
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|dfsPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|dfsResult
argument_list|()
return|;
block|}
DECL|method|executeQueryPhase
specifier|public
name|QuerySearchResult
name|executeQueryPhase
parameter_list|(
name|InternalSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|createContext
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|activeContexts
operator|.
name|put
argument_list|(
name|context
operator|.
name|id
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|queryPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|queryResult
argument_list|()
return|;
block|}
DECL|method|executeQueryPhase
specifier|public
name|QuerySearchResult
name|executeQueryPhase
parameter_list|(
name|InternalScrollSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|findContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|processScroll
argument_list|(
name|request
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|queryPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|queryResult
argument_list|()
return|;
block|}
DECL|method|executeQueryPhase
specifier|public
name|QuerySearchResult
name|executeQueryPhase
parameter_list|(
name|QuerySearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|findContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|dfSource
argument_list|(
operator|new
name|CachedDfSource
argument_list|(
name|request
operator|.
name|dfs
argument_list|()
argument_list|,
name|context
operator|.
name|similarityService
argument_list|()
operator|.
name|defaultSearchSimilarity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to set aggregated df"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|queryPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|queryResult
argument_list|()
return|;
block|}
DECL|method|executeFetchPhase
specifier|public
name|QueryFetchSearchResult
name|executeFetchPhase
parameter_list|(
name|InternalSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|createContext
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|queryPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|shortcutDocIdsToLoad
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fetchPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|scroll
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|activeContexts
operator|.
name|put
argument_list|(
name|context
operator|.
name|id
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|QueryFetchSearchResult
argument_list|(
name|context
operator|.
name|queryResult
argument_list|()
argument_list|,
name|context
operator|.
name|fetchResult
argument_list|()
argument_list|)
return|;
block|}
DECL|method|executeFetchPhase
specifier|public
name|QueryFetchSearchResult
name|executeFetchPhase
parameter_list|(
name|QuerySearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|findContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|dfSource
argument_list|(
operator|new
name|CachedDfSource
argument_list|(
name|request
operator|.
name|dfs
argument_list|()
argument_list|,
name|context
operator|.
name|similarityService
argument_list|()
operator|.
name|defaultSearchSimilarity
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|context
argument_list|,
literal|"Failed to set aggregated df"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|queryPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|shortcutDocIdsToLoad
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fetchPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|scroll
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|activeContexts
operator|.
name|put
argument_list|(
name|context
operator|.
name|id
argument_list|()
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|QueryFetchSearchResult
argument_list|(
name|context
operator|.
name|queryResult
argument_list|()
argument_list|,
name|context
operator|.
name|fetchResult
argument_list|()
argument_list|)
return|;
block|}
DECL|method|executeFetchPhase
specifier|public
name|QueryFetchSearchResult
name|executeFetchPhase
parameter_list|(
name|InternalScrollSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|findContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|processScroll
argument_list|(
name|request
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|queryPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|shortcutDocIdsToLoad
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fetchPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|scroll
argument_list|()
operator|==
literal|null
condition|)
block|{
name|freeContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|QueryFetchSearchResult
argument_list|(
name|context
operator|.
name|queryResult
argument_list|()
argument_list|,
name|context
operator|.
name|fetchResult
argument_list|()
argument_list|)
return|;
block|}
DECL|method|executeFetchPhase
specifier|public
name|FetchSearchResult
name|executeFetchPhase
parameter_list|(
name|FetchSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|SearchContext
name|context
init|=
name|findContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|context
operator|.
name|docIdsToLoad
argument_list|(
name|request
operator|.
name|docIds
argument_list|()
argument_list|)
expr_stmt|;
name|fetchPhase
operator|.
name|execute
argument_list|(
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|scroll
argument_list|()
operator|==
literal|null
condition|)
block|{
name|freeContext
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|context
operator|.
name|fetchResult
argument_list|()
return|;
block|}
DECL|method|findContext
specifier|private
name|SearchContext
name|findContext
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|SearchContextMissingException
block|{
name|SearchContext
name|context
init|=
name|activeContexts
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchContextMissingException
argument_list|(
name|id
argument_list|)
throw|;
block|}
comment|// update the last access time of the context
name|context
operator|.
name|accessed
argument_list|(
name|timerService
operator|.
name|estimatedTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
DECL|method|createContext
specifier|private
name|SearchContext
name|createContext
parameter_list|(
name|InternalSearchRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
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
name|index
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
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Searcher
name|engineSearcher
init|=
name|indexShard
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|SearchShardTarget
name|shardTarget
init|=
operator|new
name|SearchShardTarget
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
name|SearchContext
name|context
init|=
operator|new
name|SearchContext
argument_list|(
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
argument_list|,
name|shardTarget
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|,
name|engineSearcher
argument_list|,
name|indexService
argument_list|)
decl_stmt|;
name|context
operator|.
name|scroll
argument_list|(
name|request
operator|.
name|scroll
argument_list|()
argument_list|)
expr_stmt|;
name|parseSource
argument_list|(
name|context
argument_list|,
name|request
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
name|parseSource
argument_list|(
name|context
argument_list|,
name|request
operator|.
name|extraSource
argument_list|()
argument_list|)
expr_stmt|;
comment|// if the from and size are still not set, default them
if|if
condition|(
name|context
operator|.
name|from
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|context
operator|.
name|from
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|size
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|context
operator|.
name|size
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
comment|// pre process
name|dfsPhase
operator|.
name|preProcess
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|queryPhase
operator|.
name|preProcess
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|fetchPhase
operator|.
name|preProcess
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// compute the context keep alive
name|TimeValue
name|keepAlive
init|=
name|defaultKeepAlive
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|scroll
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|scroll
argument_list|()
operator|.
name|keepAlive
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|keepAlive
operator|=
name|request
operator|.
name|scroll
argument_list|()
operator|.
name|keepAlive
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|keepAlive
argument_list|(
name|keepAlive
argument_list|)
expr_stmt|;
name|context
operator|.
name|accessed
argument_list|(
name|timerService
operator|.
name|estimatedTimeInMillis
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|keepAliveTimeout
argument_list|(
name|timerService
operator|.
name|newTimeout
argument_list|(
operator|new
name|KeepAliveTimerTask
argument_list|(
name|context
argument_list|)
argument_list|,
name|keepAlive
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|context
return|;
block|}
DECL|method|freeContext
specifier|private
name|void
name|freeContext
parameter_list|(
name|long
name|id
parameter_list|)
block|{
name|SearchContext
name|context
init|=
name|activeContexts
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|freeContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|freeContext
specifier|private
name|void
name|freeContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|context
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
DECL|method|parseSource
specifier|private
name|void
name|parseSource
parameter_list|(
name|SearchContext
name|context
parameter_list|,
name|byte
index|[]
name|source
parameter_list|)
throws|throws
name|SearchParseException
block|{
comment|// nothing to parse...
if|if
condition|(
name|source
operator|==
literal|null
operator|||
name|source
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return;
block|}
try|try
block|{
name|JsonParser
name|jp
init|=
name|jsonFactory
operator|.
name|createJsonParser
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|JsonToken
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|jp
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|JsonToken
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|FIELD_NAME
condition|)
block|{
name|String
name|fieldName
init|=
name|jp
operator|.
name|getCurrentName
argument_list|()
decl_stmt|;
name|jp
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|SearchParseElement
name|element
init|=
name|elementParsers
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|element
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"No parser for element ["
operator|+
name|fieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|element
operator|.
name|parse
argument_list|(
name|jp
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Failed to parse ["
operator|+
name|Unicode
operator|.
name|fromBytes
argument_list|(
name|source
argument_list|)
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|shortcutDocIdsToLoad
specifier|private
name|void
name|shortcutDocIdsToLoad
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|TopDocs
name|topDocs
init|=
name|context
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
decl_stmt|;
if|if
condition|(
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
operator|<
name|context
operator|.
name|from
argument_list|()
condition|)
block|{
comment|// no more docs...
name|context
operator|.
name|docIdsToLoad
argument_list|(
operator|new
name|int
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|totalSize
init|=
name|context
operator|.
name|from
argument_list|()
operator|+
name|context
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
index|[]
name|docIdsToLoad
init|=
operator|new
name|int
index|[
name|context
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|context
operator|.
name|from
argument_list|()
init|;
name|i
operator|<
name|totalSize
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|<
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
condition|)
block|{
name|docIdsToLoad
index|[
name|counter
index|]
operator|=
name|topDocs
operator|.
name|scoreDocs
index|[
name|i
index|]
operator|.
name|doc
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
name|counter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|<
name|context
operator|.
name|size
argument_list|()
condition|)
block|{
name|docIdsToLoad
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|docIdsToLoad
argument_list|,
literal|0
argument_list|,
name|counter
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|docIdsToLoad
argument_list|(
name|docIdsToLoad
argument_list|)
expr_stmt|;
block|}
DECL|method|processScroll
specifier|private
name|void
name|processScroll
parameter_list|(
name|InternalScrollSearchRequest
name|request
parameter_list|,
name|SearchContext
name|context
parameter_list|)
block|{
comment|// process scroll
name|context
operator|.
name|from
argument_list|(
name|context
operator|.
name|from
argument_list|()
operator|+
name|context
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|scroll
argument_list|(
name|request
operator|.
name|scroll
argument_list|()
argument_list|)
expr_stmt|;
comment|// update the context keep alive based on the new scroll value
if|if
condition|(
name|request
operator|.
name|scroll
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|scroll
argument_list|()
operator|.
name|keepAlive
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|keepAlive
argument_list|(
name|request
operator|.
name|scroll
argument_list|()
operator|.
name|keepAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|KeepAliveTimerTask
specifier|private
class|class
name|KeepAliveTimerTask
implements|implements
name|TimerTask
block|{
DECL|field|context
specifier|private
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|method|KeepAliveTimerTask
specifier|private
name|KeepAliveTimerTask
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|(
name|Timeout
name|timeout
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|timeout
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
return|return;
block|}
name|long
name|currentTime
init|=
name|timerService
operator|.
name|estimatedTimeInMillis
argument_list|()
decl_stmt|;
name|long
name|nextDelay
init|=
name|context
operator|.
name|keepAlive
argument_list|()
operator|.
name|millis
argument_list|()
operator|-
operator|(
name|currentTime
operator|-
name|context
operator|.
name|lastAccessTime
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|nextDelay
operator|<=
literal|0
condition|)
block|{
comment|// Time out, free the context (and remove it from the active context)
name|freeContext
argument_list|(
name|context
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Read occurred before the timeout - set a new timeout with shorter delay.
name|context
operator|.
name|keepAliveTimeout
argument_list|(
name|timerService
operator|.
name|newTimeout
argument_list|(
name|this
argument_list|,
name|nextDelay
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

