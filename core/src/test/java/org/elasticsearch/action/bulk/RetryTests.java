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
name|ActionFuture
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
name|support
operator|.
name|PlainActionFuture
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
name|update
operator|.
name|UpdateRequest
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
name|EsRejectedExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|NoOpClient
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
name|ESTestCase
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
name|concurrent
operator|.
name|CountDownLatch
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
name|*
import|;
end_import

begin_class
DECL|class|RetryTests
specifier|public
class|class
name|RetryTests
extends|extends
name|ESTestCase
block|{
comment|// no need to wait fof a long time in tests
DECL|field|DELAY
specifier|private
specifier|static
specifier|final
name|TimeValue
name|DELAY
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1L
argument_list|)
decl_stmt|;
DECL|field|CALLS_TO_FAIL
specifier|private
specifier|static
specifier|final
name|int
name|CALLS_TO_FAIL
init|=
literal|5
decl_stmt|;
DECL|field|bulkClient
specifier|private
name|MockBulkClient
name|bulkClient
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|this
operator|.
name|bulkClient
operator|=
operator|new
name|MockBulkClient
argument_list|(
name|getTestName
argument_list|()
argument_list|,
name|CALLS_TO_FAIL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|this
operator|.
name|bulkClient
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|createBulkRequest
specifier|private
name|BulkRequest
name|createBulkRequest
parameter_list|()
block|{
name|BulkRequest
name|request
init|=
operator|new
name|BulkRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
literal|"shop"
argument_list|,
literal|"products"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
literal|"shop"
argument_list|,
literal|"products"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
literal|"shop"
argument_list|,
literal|"products"
argument_list|,
literal|"3"
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
literal|"shop"
argument_list|,
literal|"products"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|add
argument_list|(
operator|new
name|UpdateRequest
argument_list|(
literal|"shop"
argument_list|,
literal|"products"
argument_list|,
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
DECL|method|testSyncRetryBacksOff
specifier|public
name|void
name|testSyncRetryBacksOff
parameter_list|()
throws|throws
name|Exception
block|{
name|BackoffPolicy
name|backoff
init|=
name|BackoffPolicy
operator|.
name|constantBackoff
argument_list|(
name|DELAY
argument_list|,
name|CALLS_TO_FAIL
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
name|createBulkRequest
argument_list|()
decl_stmt|;
name|BulkResponse
name|response
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
name|backoff
argument_list|)
operator|.
name|withSyncBackoff
argument_list|(
name|bulkClient
argument_list|,
name|bulkRequest
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|response
operator|.
name|hasFailures
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getItems
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSyncRetryFailsAfterBackoff
specifier|public
name|void
name|testSyncRetryFailsAfterBackoff
parameter_list|()
throws|throws
name|Exception
block|{
name|BackoffPolicy
name|backoff
init|=
name|BackoffPolicy
operator|.
name|constantBackoff
argument_list|(
name|DELAY
argument_list|,
name|CALLS_TO_FAIL
operator|-
literal|1
argument_list|)
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
name|createBulkRequest
argument_list|()
decl_stmt|;
name|BulkResponse
name|response
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
name|backoff
argument_list|)
operator|.
name|withSyncBackoff
argument_list|(
name|bulkClient
argument_list|,
name|bulkRequest
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|hasFailures
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getItems
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testAsyncRetryBacksOff
specifier|public
name|void
name|testAsyncRetryBacksOff
parameter_list|()
throws|throws
name|Exception
block|{
name|BackoffPolicy
name|backoff
init|=
name|BackoffPolicy
operator|.
name|constantBackoff
argument_list|(
name|DELAY
argument_list|,
name|CALLS_TO_FAIL
argument_list|)
decl_stmt|;
name|AssertingListener
name|listener
init|=
operator|new
name|AssertingListener
argument_list|()
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
name|createBulkRequest
argument_list|()
decl_stmt|;
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
name|backoff
argument_list|)
operator|.
name|withAsyncBackoff
argument_list|(
name|bulkClient
argument_list|,
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|awaitCallbacksCalled
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertOnResponseCalled
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertResponseWithoutFailures
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertResponseWithNumberOfItems
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertOnFailureNeverCalled
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AwaitsFix
argument_list|(
name|bugUrl
operator|=
literal|"spuriously fails on Jenkins. Investigation ongoing."
argument_list|)
DECL|method|testAsyncRetryFailsAfterBacksOff
specifier|public
name|void
name|testAsyncRetryFailsAfterBacksOff
parameter_list|()
throws|throws
name|Exception
block|{
name|BackoffPolicy
name|backoff
init|=
name|BackoffPolicy
operator|.
name|constantBackoff
argument_list|(
name|DELAY
argument_list|,
name|CALLS_TO_FAIL
operator|-
literal|1
argument_list|)
decl_stmt|;
name|AssertingListener
name|listener
init|=
operator|new
name|AssertingListener
argument_list|()
decl_stmt|;
name|BulkRequest
name|bulkRequest
init|=
name|createBulkRequest
argument_list|()
decl_stmt|;
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
name|backoff
argument_list|)
operator|.
name|withAsyncBackoff
argument_list|(
name|bulkClient
argument_list|,
name|bulkRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|awaitCallbacksCalled
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertOnResponseCalled
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertResponseWithFailures
argument_list|()
expr_stmt|;
name|listener
operator|.
name|assertResponseWithNumberOfItems
argument_list|(
name|bulkRequest
operator|.
name|numberOfActions
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertOnFailureNeverCalled
argument_list|()
expr_stmt|;
block|}
DECL|class|AssertingListener
specifier|private
specifier|static
class|class
name|AssertingListener
implements|implements
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
block|{
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|countOnResponseCalled
specifier|private
specifier|volatile
name|int
name|countOnResponseCalled
init|=
literal|0
decl_stmt|;
DECL|field|lastFailure
specifier|private
specifier|volatile
name|Throwable
name|lastFailure
decl_stmt|;
DECL|field|response
specifier|private
specifier|volatile
name|BulkResponse
name|response
decl_stmt|;
DECL|method|AssertingListener
specifier|private
name|AssertingListener
parameter_list|()
block|{
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|awaitCallbacksCalled
specifier|public
name|void
name|awaitCallbacksCalled
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|BulkResponse
name|bulkItemResponses
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|bulkItemResponses
expr_stmt|;
name|countOnResponseCalled
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastFailure
operator|=
name|e
expr_stmt|;
block|}
DECL|method|assertOnResponseCalled
specifier|public
name|void
name|assertOnResponseCalled
parameter_list|()
block|{
name|assertThat
argument_list|(
name|countOnResponseCalled
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResponseWithNumberOfItems
specifier|public
name|void
name|assertResponseWithNumberOfItems
parameter_list|(
name|int
name|numItems
parameter_list|)
block|{
name|assertThat
argument_list|(
name|response
operator|.
name|getItems
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
name|numItems
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResponseWithoutFailures
specifier|public
name|void
name|assertResponseWithoutFailures
parameter_list|()
block|{
name|assertThat
argument_list|(
name|response
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Response should not have failures"
argument_list|,
name|response
operator|.
name|hasFailures
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResponseWithFailures
specifier|public
name|void
name|assertResponseWithFailures
parameter_list|()
block|{
name|assertThat
argument_list|(
name|response
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Response should have failures"
argument_list|,
name|response
operator|.
name|hasFailures
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertOnFailureNeverCalled
specifier|public
name|void
name|assertOnFailureNeverCalled
parameter_list|()
block|{
name|assertThat
argument_list|(
name|lastFailure
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MockBulkClient
specifier|private
specifier|static
class|class
name|MockBulkClient
extends|extends
name|NoOpClient
block|{
DECL|field|numberOfCallsToFail
specifier|private
name|int
name|numberOfCallsToFail
decl_stmt|;
DECL|method|MockBulkClient
specifier|private
name|MockBulkClient
parameter_list|(
name|String
name|testName
parameter_list|,
name|int
name|numberOfCallsToFail
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
name|this
operator|.
name|numberOfCallsToFail
operator|=
name|numberOfCallsToFail
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|bulk
specifier|public
name|ActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|bulk
parameter_list|(
name|BulkRequest
name|request
parameter_list|)
block|{
name|PlainActionFuture
argument_list|<
name|BulkResponse
argument_list|>
name|responseFuture
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|bulk
argument_list|(
name|request
argument_list|,
name|responseFuture
argument_list|)
expr_stmt|;
return|return
name|responseFuture
return|;
block|}
annotation|@
name|Override
DECL|method|bulk
specifier|public
name|void
name|bulk
parameter_list|(
name|BulkRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BulkResponse
argument_list|>
name|listener
parameter_list|)
block|{
comment|// do everything synchronously, that's fine for a test
name|boolean
name|shouldFail
init|=
name|numberOfCallsToFail
operator|>
literal|0
decl_stmt|;
name|numberOfCallsToFail
operator|--
expr_stmt|;
name|BulkItemResponse
index|[]
name|itemResponses
init|=
operator|new
name|BulkItemResponse
index|[
name|request
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
comment|// if we have to fail, we need to fail at least once "reliably", the rest can be random
name|int
name|itemToFail
init|=
name|randomInt
argument_list|(
name|request
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|request
operator|.
name|requests
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|shouldFail
operator|&&
operator|(
name|randomBoolean
argument_list|()
operator|||
name|idx
operator|==
name|itemToFail
operator|)
condition|)
block|{
name|itemResponses
index|[
name|idx
index|]
operator|=
name|failedResponse
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|itemResponses
index|[
name|idx
index|]
operator|=
name|successfulResponse
argument_list|()
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|BulkResponse
argument_list|(
name|itemResponses
argument_list|,
literal|1000L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|successfulResponse
specifier|private
name|BulkItemResponse
name|successfulResponse
parameter_list|()
block|{
return|return
operator|new
name|BulkItemResponse
argument_list|(
literal|1
argument_list|,
literal|"update"
argument_list|,
operator|new
name|DeleteResponse
argument_list|()
argument_list|)
return|;
block|}
DECL|method|failedResponse
specifier|private
name|BulkItemResponse
name|failedResponse
parameter_list|()
block|{
return|return
operator|new
name|BulkItemResponse
argument_list|(
literal|1
argument_list|,
literal|"update"
argument_list|,
operator|new
name|BulkItemResponse
operator|.
name|Failure
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|,
operator|new
name|EsRejectedExecutionException
argument_list|(
literal|"pool full"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

