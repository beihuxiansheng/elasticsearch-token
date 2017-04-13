begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|logging
operator|.
name|Loggers
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
name|concurrent
operator|.
name|CountDownLatch
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
name|Semaphore
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
name|function
operator|.
name|BiConsumer
import|;
end_import

begin_comment
comment|/**  * Implements the low-level details of bulk request handling  */
end_comment

begin_class
DECL|class|BulkRequestHandler
specifier|public
specifier|final
class|class
name|BulkRequestHandler
block|{
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|consumer
specifier|private
specifier|final
name|BiConsumer
argument_list|<
name|BulkRequest
argument_list|,
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
argument_list|>
name|consumer
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|BulkProcessor
operator|.
name|Listener
name|listener
decl_stmt|;
DECL|field|semaphore
specifier|private
specifier|final
name|Semaphore
name|semaphore
decl_stmt|;
DECL|field|retry
specifier|private
specifier|final
name|Retry
name|retry
decl_stmt|;
DECL|field|concurrentRequests
specifier|private
specifier|final
name|int
name|concurrentRequests
decl_stmt|;
DECL|method|BulkRequestHandler
name|BulkRequestHandler
parameter_list|(
name|BiConsumer
argument_list|<
name|BulkRequest
argument_list|,
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
argument_list|>
name|consumer
parameter_list|,
name|BackoffPolicy
name|backoffPolicy
parameter_list|,
name|BulkProcessor
operator|.
name|Listener
name|listener
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|int
name|concurrentRequests
parameter_list|)
block|{
assert|assert
name|concurrentRequests
operator|>=
literal|0
assert|;
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|concurrentRequests
operator|=
name|concurrentRequests
expr_stmt|;
name|this
operator|.
name|retry
operator|=
operator|new
name|Retry
argument_list|(
name|EsRejectedExecutionException
operator|.
name|class
argument_list|,
name|backoffPolicy
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|semaphore
operator|=
operator|new
name|Semaphore
argument_list|(
name|concurrentRequests
operator|>
literal|0
condition|?
name|concurrentRequests
else|:
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|BulkRequest
name|bulkRequest
parameter_list|,
name|long
name|executionId
parameter_list|)
block|{
name|Runnable
name|toRelease
init|=
parameter_list|()
lambda|->
block|{}
decl_stmt|;
name|boolean
name|bulkRequestSetupSuccessful
init|=
literal|false
decl_stmt|;
try|try
block|{
name|listener
operator|.
name|beforeBulk
argument_list|(
name|executionId
argument_list|,
name|bulkRequest
argument_list|)
expr_stmt|;
name|semaphore
operator|.
name|acquire
argument_list|()
expr_stmt|;
name|toRelease
operator|=
name|semaphore
operator|::
name|release
expr_stmt|;
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|retry
operator|.
name|withBackoff
argument_list|(
name|consumer
argument_list|,
name|bulkRequest
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
try|try
block|{
name|listener
operator|.
name|afterBulk
argument_list|(
name|executionId
argument_list|,
name|bulkRequest
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
name|latch
operator|.
name|countDown
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
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|listener
operator|.
name|afterBulk
argument_list|(
name|executionId
argument_list|,
name|bulkRequest
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|bulkRequestSetupSuccessful
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|concurrentRequests
operator|==
literal|0
condition|)
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
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
literal|"Bulk request {} has been cancelled."
argument_list|,
name|executionId
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|listener
operator|.
name|afterBulk
argument_list|(
name|executionId
argument_list|,
name|bulkRequest
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
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
literal|"Failed to execute bulk request {}."
argument_list|,
name|executionId
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|listener
operator|.
name|afterBulk
argument_list|(
name|executionId
argument_list|,
name|bulkRequest
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|bulkRequestSetupSuccessful
operator|==
literal|false
condition|)
block|{
comment|// if we fail on client.bulk() release the semaphore
name|toRelease
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|awaitClose
name|boolean
name|awaitClose
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|semaphore
operator|.
name|tryAcquire
argument_list|(
name|this
operator|.
name|concurrentRequests
argument_list|,
name|timeout
argument_list|,
name|unit
argument_list|)
condition|)
block|{
name|semaphore
operator|.
name|release
argument_list|(
name|this
operator|.
name|concurrentRequests
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

