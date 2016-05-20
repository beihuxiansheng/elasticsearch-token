begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.reindex
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
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
name|util
operator|.
name|LuceneTestCase
operator|.
name|AwaitsFix
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
name|ListenableActionFuture
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
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksResponse
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
name|BulkRequestBuilder
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
name|EsRejectedExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESSingleNodeTestCase
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Collection
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
name|concurrent
operator|.
name|CyclicBarrier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|reindex
operator|.
name|ReindexTestCase
operator|.
name|matcher
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|greaterThan
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|hasSize
import|;
end_import

begin_comment
comment|/**  * Integration test for retry behavior. Useful because retrying relies on the way that the rest of Elasticsearch throws exceptions and unit  * tests won't verify that.  */
end_comment

begin_class
DECL|class|RetryTests
specifier|public
class|class
name|RetryTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|DOC_COUNT
specifier|private
specifier|static
specifier|final
name|int
name|DOC_COUNT
init|=
literal|20
decl_stmt|;
DECL|field|blockedExecutors
specifier|private
name|List
argument_list|<
name|CyclicBarrier
argument_list|>
name|blockedExecutors
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getPlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|ReindexPlugin
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**      * Lower the queue sizes to be small enough that both bulk and searches will time out and have to be retried.      */
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|()
block|{
name|Settings
operator|.
name|Builder
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|nodeSettings
argument_list|()
argument_list|)
decl_stmt|;
comment|// Use pools of size 1 so we can block them
name|settings
operator|.
name|put
argument_list|(
literal|"threadpool.bulk.size"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|settings
operator|.
name|put
argument_list|(
literal|"threadpool.search.size"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Use queues of size 1 because size 0 is broken and because search requests need the queue to function
name|settings
operator|.
name|put
argument_list|(
literal|"threadpool.bulk.queue_size"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|settings
operator|.
name|put
argument_list|(
literal|"threadpool.search.queue_size"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|settings
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|setupSourceIndex
specifier|public
name|void
name|setupSourceIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"source"
argument_list|)
expr_stmt|;
comment|// Build the test data. Don't use indexRandom because that won't work consistently with such small thread pools.
name|BulkRequestBuilder
name|bulk
init|=
name|client
argument_list|()
operator|.
name|prepareBulk
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
name|DOC_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|bulk
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"source"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Retry
name|retry
init|=
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
name|BackoffPolicy
operator|.
name|exponentialBackoff
argument_list|()
argument_list|)
decl_stmt|;
name|BulkResponse
name|response
init|=
name|retry
operator|.
name|withSyncBackoff
argument_list|(
name|client
argument_list|()
argument_list|,
name|bulk
operator|.
name|request
argument_list|()
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|response
operator|.
name|buildFailureMessage
argument_list|()
argument_list|,
name|response
operator|.
name|hasFailures
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
literal|"source"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|forceUnblockAllExecutors
specifier|public
name|void
name|forceUnblockAllExecutors
parameter_list|()
block|{
for|for
control|(
name|CyclicBarrier
name|barrier
range|:
name|blockedExecutors
control|)
block|{
name|barrier
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|testReindex
specifier|public
name|void
name|testReindex
parameter_list|()
throws|throws
name|Exception
block|{
name|testCase
argument_list|(
name|ReindexAction
operator|.
name|NAME
argument_list|,
name|ReindexAction
operator|.
name|INSTANCE
operator|.
name|newRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|)
operator|.
name|source
argument_list|(
literal|"source"
argument_list|)
operator|.
name|destination
argument_list|(
literal|"dest"
argument_list|)
argument_list|,
name|matcher
argument_list|()
operator|.
name|created
argument_list|(
name|DOC_COUNT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpdateByQuery
specifier|public
name|void
name|testUpdateByQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|testCase
argument_list|(
name|UpdateByQueryAction
operator|.
name|NAME
argument_list|,
name|UpdateByQueryAction
operator|.
name|INSTANCE
operator|.
name|newRequestBuilder
argument_list|(
name|client
argument_list|()
argument_list|)
operator|.
name|source
argument_list|(
literal|"source"
argument_list|)
argument_list|,
name|matcher
argument_list|()
operator|.
name|updated
argument_list|(
name|DOC_COUNT
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testCase
specifier|private
name|void
name|testCase
parameter_list|(
name|String
name|action
parameter_list|,
name|AbstractBulkIndexByScrollRequestBuilder
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|request
parameter_list|,
name|BulkIndexByScrollResponseMatcher
name|matcher
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Blocking search"
argument_list|)
expr_stmt|;
name|CyclicBarrier
name|initialSearchBlock
init|=
name|blockExecutor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
comment|// Make sure we use more than one batch so we have to scroll
name|request
operator|.
name|source
argument_list|()
operator|.
name|setSize
argument_list|(
name|DOC_COUNT
operator|/
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Starting request"
argument_list|)
expr_stmt|;
name|ListenableActionFuture
argument_list|<
name|BulkIndexByScrollResponse
argument_list|>
name|responseListener
init|=
name|request
operator|.
name|execute
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for search rejections on the initial search"
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertThat
argument_list|(
name|taskStatus
argument_list|(
name|action
argument_list|)
operator|.
name|getSearchRetries
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Blocking bulk and unblocking search so we start to get bulk rejections"
argument_list|)
expr_stmt|;
name|CyclicBarrier
name|bulkBlock
init|=
name|blockExecutor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|BULK
argument_list|)
decl_stmt|;
name|initialSearchBlock
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for bulk rejections"
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertThat
argument_list|(
name|taskStatus
argument_list|(
name|action
argument_list|)
operator|.
name|getBulkRetries
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Keep a copy of the current number of search rejections so we can assert that we get more when we block the scroll
name|long
name|initialSearchRejections
init|=
name|taskStatus
argument_list|(
name|action
argument_list|)
operator|.
name|getSearchRetries
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Blocking search and unblocking bulk so we should get search rejections for the scroll"
argument_list|)
expr_stmt|;
name|CyclicBarrier
name|scrollBlock
init|=
name|blockExecutor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
argument_list|)
decl_stmt|;
name|bulkBlock
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for search rejections for the scroll"
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertThat
argument_list|(
name|taskStatus
argument_list|(
name|action
argument_list|)
operator|.
name|getSearchRetries
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|initialSearchRejections
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Unblocking the scroll"
argument_list|)
expr_stmt|;
name|scrollBlock
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Waiting for the request to finish"
argument_list|)
expr_stmt|;
name|BulkIndexByScrollResponse
name|response
init|=
name|responseListener
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getBulkRetries
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getSearchRetries
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|initialSearchRejections
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Blocks the named executor by getting its only thread running a task blocked on a CyclicBarrier and fills the queue with a noop task.      * So requests to use this queue should get {@link EsRejectedExecutionException}s.      */
DECL|method|blockExecutor
specifier|private
name|CyclicBarrier
name|blockExecutor
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|Exception
block|{
name|ThreadPool
name|threadPool
init|=
name|getInstanceFromNode
argument_list|(
name|ThreadPool
operator|.
name|class
argument_list|)
decl_stmt|;
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Blocking the [{}] executor"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|executor
argument_list|(
name|name
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|name
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{}
argument_list|)
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Blocked the [{}] executor"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Ublocking the [{}] executor"
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|blockedExecutors
operator|.
name|add
argument_list|(
name|barrier
argument_list|)
expr_stmt|;
return|return
name|barrier
return|;
block|}
comment|/**      * Fetch the status for a task of type "action". Fails if there aren't exactly one of that type of task running.      */
DECL|method|taskStatus
specifier|private
name|BulkByScrollTask
operator|.
name|Status
name|taskStatus
parameter_list|(
name|String
name|action
parameter_list|)
block|{
name|ListTasksResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareListTasks
argument_list|()
operator|.
name|setActions
argument_list|(
name|action
argument_list|)
operator|.
name|setDetailed
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getTasks
argument_list|()
argument_list|,
name|hasSize
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|BulkByScrollTask
operator|.
name|Status
operator|)
name|response
operator|.
name|getTasks
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStatus
argument_list|()
return|;
block|}
block|}
end_class

end_unit

