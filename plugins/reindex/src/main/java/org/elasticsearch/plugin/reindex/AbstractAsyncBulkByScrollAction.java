begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.plugin.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|reindex
package|;
end_package

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
name|refresh
operator|.
name|RefreshRequest
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
name|refresh
operator|.
name|RefreshResponse
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
name|bulk
operator|.
name|BackoffPolicy
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
name|bulk
operator|.
name|BulkItemResponse
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
name|bulk
operator|.
name|BulkItemResponse
operator|.
name|Failure
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
name|bulk
operator|.
name|BulkRequest
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
name|bulk
operator|.
name|BulkResponse
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
name|bulk
operator|.
name|Retry
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
name|search
operator|.
name|ClearScrollRequest
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
name|ClearScrollResponse
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
name|SearchRequest
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
name|SearchResponse
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
name|SearchScrollRequest
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
name|ShardSearchFailure
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|logging
operator|.
name|ESLogger
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|AbstractRunnable
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
name|EsRejectedExecutionException
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
name|SearchHit
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
name|ArrayList
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
name|Iterator
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
name|ConcurrentHashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|max
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|unmodifiableList
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
name|bulk
operator|.
name|BackoffPolicy
operator|.
name|exponentialBackoff
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueNanos
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|plugin
operator|.
name|reindex
operator|.
name|AbstractBulkByScrollRequest
operator|.
name|SIZE_ALL_MATCHES
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
operator|.
name|CONFLICT
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
operator|.
name|SortBuilders
operator|.
name|fieldSort
import|;
end_import

begin_comment
comment|/**  * Abstract base for scrolling across a search and executing bulk actions on all  * results.  */
end_comment

begin_class
DECL|class|AbstractAsyncBulkByScrollAction
specifier|public
specifier|abstract
class|class
name|AbstractAsyncBulkByScrollAction
parameter_list|<
name|Request
extends|extends
name|AbstractBulkByScrollRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
parameter_list|>
block|{
comment|/**      * The request for this action. Named mainRequest because we create lots of<code>request</code> variables all representing child      * requests of this mainRequest.      */
DECL|field|mainRequest
specifier|protected
specifier|final
name|Request
name|mainRequest
decl_stmt|;
DECL|field|task
specifier|protected
specifier|final
name|BulkByScrollTask
name|task
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|AtomicLong
name|startTime
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|scroll
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|String
argument_list|>
name|scroll
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|destinationIndices
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|destinationIndices
init|=
name|Collections
operator|.
name|newSetFromMap
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|firstSearchRequest
specifier|private
specifier|final
name|SearchRequest
name|firstSearchRequest
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
decl_stmt|;
DECL|field|retry
specifier|private
specifier|final
name|Retry
name|retry
decl_stmt|;
DECL|method|AbstractAsyncBulkByScrollAction
specifier|public
name|AbstractAsyncBulkByScrollAction
parameter_list|(
name|BulkByScrollTask
name|task
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|Client
name|client
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Request
name|mainRequest
parameter_list|,
name|SearchRequest
name|firstSearchRequest
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|mainRequest
operator|=
name|mainRequest
expr_stmt|;
name|this
operator|.
name|firstSearchRequest
operator|=
name|firstSearchRequest
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|retry
operator|=
name|Retry
operator|.
name|on
argument_list|(
name|EsRejectedExecutionException
operator|.
name|class
argument_list|)
operator|.
name|policy
argument_list|(
name|wrapBackoffPolicy
argument_list|(
name|backoffPolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildBulk
specifier|protected
specifier|abstract
name|BulkRequest
name|buildBulk
parameter_list|(
name|Iterable
argument_list|<
name|SearchHit
argument_list|>
name|docs
parameter_list|)
function_decl|;
DECL|method|buildResponse
specifier|protected
specifier|abstract
name|Response
name|buildResponse
parameter_list|(
name|TimeValue
name|took
parameter_list|,
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
parameter_list|,
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
parameter_list|)
function_decl|;
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|initialSearch
argument_list|()
expr_stmt|;
block|}
DECL|method|getTask
specifier|public
name|BulkByScrollTask
name|getTask
parameter_list|()
block|{
return|return
name|task
return|;
block|}
DECL|method|initialSearch
specifier|private
name|void
name|initialSearch
parameter_list|()
block|{
try|try
block|{
comment|// Default to sorting by _doc if it hasn't been changed.
if|if
condition|(
name|firstSearchRequest
operator|.
name|source
argument_list|()
operator|.
name|sorts
argument_list|()
operator|==
literal|null
condition|)
block|{
name|firstSearchRequest
operator|.
name|source
argument_list|()
operator|.
name|sort
argument_list|(
name|fieldSort
argument_list|(
literal|"_doc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|startTime
operator|.
name|set
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"executing initial scroll against {}{}"
argument_list|,
name|firstSearchRequest
operator|.
name|indices
argument_list|()
operator|==
literal|null
operator|||
name|firstSearchRequest
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|?
literal|"all indices"
else|:
name|firstSearchRequest
operator|.
name|indices
argument_list|()
argument_list|,
name|firstSearchRequest
operator|.
name|types
argument_list|()
operator|==
literal|null
operator|||
name|firstSearchRequest
operator|.
name|types
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|?
literal|""
else|:
name|firstSearchRequest
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|search
argument_list|(
name|firstSearchRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|response
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] documents match query"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|)
expr_stmt|;
name|onScrollResponse
argument_list|(
name|response
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
name|finishHim
argument_list|(
name|e
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
name|finishHim
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|onScrollResponse
name|void
name|onScrollResponse
parameter_list|(
name|SearchResponse
name|searchResponse
parameter_list|)
block|{
name|scroll
operator|.
name|set
argument_list|(
name|searchResponse
operator|.
name|getScrollId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
operator|!=
literal|null
operator|&&
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|startNormalTermination
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|long
name|total
init|=
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
decl_stmt|;
if|if
condition|(
name|mainRequest
operator|.
name|getSize
argument_list|()
operator|>
literal|0
condition|)
block|{
name|total
operator|=
name|min
argument_list|(
name|total
argument_list|,
name|mainRequest
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|task
operator|.
name|setTotal
argument_list|(
name|total
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|generic
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchHit
index|[]
name|docs
init|=
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getHits
argument_list|()
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"scroll returned [{}] documents with a scroll id of [{}]"
argument_list|,
name|docs
operator|.
name|length
argument_list|,
name|searchResponse
operator|.
name|getScrollId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|docs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|startNormalTermination
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|task
operator|.
name|countBatch
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|SearchHit
argument_list|>
name|docsIterable
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|docs
argument_list|)
decl_stmt|;
if|if
condition|(
name|mainRequest
operator|.
name|getSize
argument_list|()
operator|!=
name|SIZE_ALL_MATCHES
condition|)
block|{
comment|// Truncate the docs if we have more than the request size
name|long
name|remaining
init|=
name|max
argument_list|(
literal|0
argument_list|,
name|mainRequest
operator|.
name|getSize
argument_list|()
operator|-
name|task
operator|.
name|getSuccessfullyProcessed
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|remaining
operator|<
name|docs
operator|.
name|length
condition|)
block|{
name|docsIterable
operator|=
name|docsIterable
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|remaining
argument_list|)
expr_stmt|;
block|}
block|}
name|BulkRequest
name|request
init|=
name|buildBulk
argument_list|(
name|docsIterable
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|requests
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|/*                      * If we noop-ed the entire batch then just skip to the next batch or the BulkRequest would fail validation.                      */
name|startNextScrollRequest
argument_list|()
expr_stmt|;
return|return;
block|}
name|request
operator|.
name|timeout
argument_list|(
name|mainRequest
operator|.
name|getTimeout
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|consistencyLevel
argument_list|(
name|mainRequest
operator|.
name|getConsistency
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"sending [{}] entry, [{}] bulk request"
argument_list|,
name|request
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
name|request
operator|.
name|estimatedSizeInBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sendBulkRequest
argument_list|(
name|request
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
name|finishHim
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|sendBulkRequest
name|void
name|sendBulkRequest
parameter_list|(
name|BulkRequest
name|request
parameter_list|)
block|{
name|retry
operator|.
name|withAsyncBackoff
argument_list|(
name|client
argument_list|,
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
name|response
parameter_list|)
block|{
name|onBulkResponse
argument_list|(
name|response
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
name|finishHim
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|onBulkResponse
name|void
name|onBulkResponse
parameter_list|(
name|BulkResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|List
argument_list|<
name|Failure
argument_list|>
name|failures
init|=
operator|new
name|ArrayList
argument_list|<
name|Failure
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|destinationIndicesThisBatch
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|BulkItemResponse
name|item
range|:
name|response
control|)
block|{
if|if
condition|(
name|item
operator|.
name|isFailed
argument_list|()
condition|)
block|{
name|recordFailure
argument_list|(
name|item
operator|.
name|getFailure
argument_list|()
argument_list|,
name|failures
argument_list|)
expr_stmt|;
continue|continue;
block|}
switch|switch
condition|(
name|item
operator|.
name|getOpType
argument_list|()
condition|)
block|{
case|case
literal|"index"
case|:
case|case
literal|"create"
case|:
name|IndexResponse
name|ir
init|=
name|item
operator|.
name|getResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|ir
operator|.
name|isCreated
argument_list|()
condition|)
block|{
name|task
operator|.
name|countCreated
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|task
operator|.
name|countUpdated
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
literal|"delete"
case|:
name|task
operator|.
name|countDeleted
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown op type:  "
operator|+
name|item
operator|.
name|getOpType
argument_list|()
argument_list|)
throw|;
block|}
comment|// Track the indexes we've seen so we can refresh them if requested
name|destinationIndices
operator|.
name|add
argument_list|(
name|item
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|destinationIndices
operator|.
name|addAll
argument_list|(
name|destinationIndicesThisBatch
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|failures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|startNormalTermination
argument_list|(
name|unmodifiableList
argument_list|(
name|failures
argument_list|)
argument_list|,
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mainRequest
operator|.
name|getSize
argument_list|()
operator|!=
name|SIZE_ALL_MATCHES
operator|&&
name|task
operator|.
name|getSuccessfullyProcessed
argument_list|()
operator|>=
name|mainRequest
operator|.
name|getSize
argument_list|()
condition|)
block|{
comment|// We've processed all the requested docs.
name|startNormalTermination
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|startNextScrollRequest
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|finishHim
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|startNextScrollRequest
name|void
name|startNextScrollRequest
parameter_list|()
block|{
name|SearchScrollRequest
name|request
init|=
operator|new
name|SearchScrollRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|scrollId
argument_list|(
name|scroll
operator|.
name|get
argument_list|()
argument_list|)
operator|.
name|scroll
argument_list|(
name|firstSearchRequest
operator|.
name|scroll
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|searchScroll
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|response
parameter_list|)
block|{
name|onScrollResponse
argument_list|(
name|response
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
name|finishHim
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|recordFailure
specifier|private
name|void
name|recordFailure
parameter_list|(
name|Failure
name|failure
parameter_list|,
name|List
argument_list|<
name|Failure
argument_list|>
name|failures
parameter_list|)
block|{
if|if
condition|(
name|failure
operator|.
name|getStatus
argument_list|()
operator|==
name|CONFLICT
condition|)
block|{
name|task
operator|.
name|countVersionConflict
argument_list|()
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|mainRequest
operator|.
name|isAbortOnVersionConflict
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
name|failures
operator|.
name|add
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|method|startNormalTermination
name|void
name|startNormalTermination
parameter_list|(
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
parameter_list|,
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
parameter_list|)
block|{
if|if
condition|(
literal|false
operator|==
name|mainRequest
operator|.
name|isRefresh
argument_list|()
condition|)
block|{
name|finishHim
argument_list|(
literal|null
argument_list|,
name|indexingFailures
argument_list|,
name|searchFailures
argument_list|)
expr_stmt|;
return|return;
block|}
name|RefreshRequest
name|refresh
init|=
operator|new
name|RefreshRequest
argument_list|()
decl_stmt|;
name|refresh
operator|.
name|indices
argument_list|(
name|destinationIndices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|destinationIndices
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|refresh
argument_list|(
name|refresh
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|RefreshResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|RefreshResponse
name|response
parameter_list|)
block|{
name|finishHim
argument_list|(
literal|null
argument_list|,
name|indexingFailures
argument_list|,
name|searchFailures
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
name|finishHim
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Finish the request.      *      * @param failure if non null then the request failed catastrophically with this exception      */
DECL|method|finishHim
name|void
name|finishHim
parameter_list|(
name|Throwable
name|failure
parameter_list|)
block|{
name|finishHim
argument_list|(
name|failure
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Finish the request.      *      * @param failure if non null then the request failed catastrophically with this exception      * @param indexingFailures any indexing failures accumulated during the request      * @param searchFailures any search failures accumulated during the request      */
DECL|method|finishHim
name|void
name|finishHim
parameter_list|(
name|Throwable
name|failure
parameter_list|,
name|List
argument_list|<
name|Failure
argument_list|>
name|indexingFailures
parameter_list|,
name|List
argument_list|<
name|ShardSearchFailure
argument_list|>
name|searchFailures
parameter_list|)
block|{
name|String
name|scrollId
init|=
name|scroll
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|scrollId
argument_list|)
condition|)
block|{
comment|/*              * Fire off the clear scroll but don't wait for it it return before              * we send the use their response.              */
name|ClearScrollRequest
name|clearScrollRequest
init|=
operator|new
name|ClearScrollRequest
argument_list|()
decl_stmt|;
name|clearScrollRequest
operator|.
name|addScrollId
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
name|client
operator|.
name|clearScroll
argument_list|(
name|clearScrollRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ClearScrollResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClearScrollResponse
name|response
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Freed [{}] contexts"
argument_list|,
name|response
operator|.
name|getNumFreed
argument_list|()
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
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to clear scroll ["
operator|+
name|scrollId
operator|+
literal|']'
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|failure
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|buildResponse
argument_list|(
name|timeValueNanos
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|indexingFailures
argument_list|,
name|searchFailures
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Build the backoff policy for use with retries. Package private for testing.      */
DECL|method|backoffPolicy
name|BackoffPolicy
name|backoffPolicy
parameter_list|()
block|{
return|return
name|exponentialBackoff
argument_list|(
name|mainRequest
operator|.
name|getRetryBackoffInitialTime
argument_list|()
argument_list|,
name|mainRequest
operator|.
name|getMaxRetries
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Wraps a backoffPolicy in another policy that counts the number of backoffs acquired.      */
DECL|method|wrapBackoffPolicy
specifier|private
name|BackoffPolicy
name|wrapBackoffPolicy
parameter_list|(
name|BackoffPolicy
name|backoffPolicy
parameter_list|)
block|{
return|return
operator|new
name|BackoffPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|TimeValue
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|TimeValue
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|TimeValue
argument_list|>
name|delegate
init|=
name|backoffPolicy
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TimeValue
name|next
parameter_list|()
block|{
if|if
condition|(
literal|false
operator|==
name|delegate
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|task
operator|.
name|countRetry
argument_list|()
expr_stmt|;
return|return
name|delegate
operator|.
name|next
argument_list|()
return|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

