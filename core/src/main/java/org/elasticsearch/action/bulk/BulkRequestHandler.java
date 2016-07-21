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
name|util
operator|.
name|concurrent
operator|.
name|EsRejectedExecutionException
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

begin_comment
comment|/**  * Abstracts the low-level details of bulk request handling  */
end_comment

begin_class
DECL|class|BulkRequestHandler
specifier|abstract
class|class
name|BulkRequestHandler
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|client
specifier|protected
specifier|final
name|Client
name|client
decl_stmt|;
DECL|method|BulkRequestHandler
specifier|protected
name|BulkRequestHandler
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
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
argument_list|,
name|client
operator|.
name|settings
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|execute
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|BulkRequest
name|bulkRequest
parameter_list|,
name|long
name|executionId
parameter_list|)
function_decl|;
DECL|method|awaitClose
specifier|public
specifier|abstract
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
function_decl|;
DECL|method|syncHandler
specifier|public
specifier|static
name|BulkRequestHandler
name|syncHandler
parameter_list|(
name|Client
name|client
parameter_list|,
name|BackoffPolicy
name|backoffPolicy
parameter_list|,
name|BulkProcessor
operator|.
name|Listener
name|listener
parameter_list|)
block|{
return|return
operator|new
name|SyncBulkRequestHandler
argument_list|(
name|client
argument_list|,
name|backoffPolicy
argument_list|,
name|listener
argument_list|)
return|;
block|}
DECL|method|asyncHandler
specifier|public
specifier|static
name|BulkRequestHandler
name|asyncHandler
parameter_list|(
name|Client
name|client
parameter_list|,
name|BackoffPolicy
name|backoffPolicy
parameter_list|,
name|BulkProcessor
operator|.
name|Listener
name|listener
parameter_list|,
name|int
name|concurrentRequests
parameter_list|)
block|{
return|return
operator|new
name|AsyncBulkRequestHandler
argument_list|(
name|client
argument_list|,
name|backoffPolicy
argument_list|,
name|listener
argument_list|,
name|concurrentRequests
argument_list|)
return|;
block|}
DECL|class|SyncBulkRequestHandler
specifier|private
specifier|static
class|class
name|SyncBulkRequestHandler
extends|extends
name|BulkRequestHandler
block|{
DECL|field|listener
specifier|private
specifier|final
name|BulkProcessor
operator|.
name|Listener
name|listener
decl_stmt|;
DECL|field|backoffPolicy
specifier|private
specifier|final
name|BackoffPolicy
name|backoffPolicy
decl_stmt|;
DECL|method|SyncBulkRequestHandler
specifier|public
name|SyncBulkRequestHandler
parameter_list|(
name|Client
name|client
parameter_list|,
name|BackoffPolicy
name|backoffPolicy
parameter_list|,
name|BulkProcessor
operator|.
name|Listener
name|listener
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|this
operator|.
name|backoffPolicy
operator|=
name|backoffPolicy
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
annotation|@
name|Override
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
name|boolean
name|afterCalled
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
name|BulkResponse
name|bulkResponse
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
name|backoffPolicy
argument_list|)
operator|.
name|withSyncBackoff
argument_list|(
name|client
argument_list|,
name|bulkRequest
argument_list|)
decl_stmt|;
name|afterCalled
operator|=
literal|true
expr_stmt|;
name|listener
operator|.
name|afterBulk
argument_list|(
name|executionId
argument_list|,
name|bulkRequest
argument_list|,
name|bulkResponse
argument_list|)
expr_stmt|;
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
literal|"Bulk request {} has been cancelled."
argument_list|,
name|e
argument_list|,
name|executionId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|afterCalled
condition|)
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
literal|"Failed to execute bulk request {}."
argument_list|,
name|e
argument_list|,
name|executionId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|afterCalled
condition|)
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
block|}
block|}
annotation|@
name|Override
DECL|method|awaitClose
specifier|public
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
comment|// we are "closed" immediately as there is no request in flight
return|return
literal|true
return|;
block|}
block|}
DECL|class|AsyncBulkRequestHandler
specifier|private
specifier|static
class|class
name|AsyncBulkRequestHandler
extends|extends
name|BulkRequestHandler
block|{
DECL|field|backoffPolicy
specifier|private
specifier|final
name|BackoffPolicy
name|backoffPolicy
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
DECL|field|concurrentRequests
specifier|private
specifier|final
name|int
name|concurrentRequests
decl_stmt|;
DECL|method|AsyncBulkRequestHandler
specifier|private
name|AsyncBulkRequestHandler
parameter_list|(
name|Client
name|client
parameter_list|,
name|BackoffPolicy
name|backoffPolicy
parameter_list|,
name|BulkProcessor
operator|.
name|Listener
name|listener
parameter_list|,
name|int
name|concurrentRequests
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|)
expr_stmt|;
name|this
operator|.
name|backoffPolicy
operator|=
name|backoffPolicy
expr_stmt|;
assert|assert
name|concurrentRequests
operator|>
literal|0
assert|;
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
name|semaphore
operator|=
operator|new
name|Semaphore
argument_list|(
name|concurrentRequests
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|boolean
name|bulkRequestSetupSuccessful
init|=
literal|false
decl_stmt|;
name|boolean
name|acquired
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
name|acquired
operator|=
literal|true
expr_stmt|;
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
name|backoffPolicy
argument_list|)
operator|.
name|withAsyncBackoff
argument_list|(
name|client
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
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|bulkRequestSetupSuccessful
operator|=
literal|true
expr_stmt|;
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
literal|"Bulk request {} has been cancelled."
argument_list|,
name|e
argument_list|,
name|executionId
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
literal|"Failed to execute bulk request {}."
argument_list|,
name|e
argument_list|,
name|executionId
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
operator|!
name|bulkRequestSetupSuccessful
operator|&&
name|acquired
condition|)
block|{
comment|// if we fail on client.bulk() release the semaphore
name|semaphore
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|awaitClose
specifier|public
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
block|}
end_class

end_unit

