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
name|FailedNodeException
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
name|TaskOperationFailure
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
name|tasks
operator|.
name|TaskId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskInfo
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
name|hamcrest
operator|.
name|Matcher
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
name|org
operator|.
name|mockito
operator|.
name|ArgumentCaptor
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
name|List
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
name|Consumer
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
name|singletonList
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
name|timeValueMillis
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
name|containsString
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
name|hasToString
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
name|theInstance
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|eq
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|atMost
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|verify
import|;
end_import

begin_class
DECL|class|TransportRethrottleActionTests
specifier|public
class|class
name|TransportRethrottleActionTests
extends|extends
name|ESTestCase
block|{
DECL|field|slices
specifier|private
name|int
name|slices
decl_stmt|;
DECL|field|task
specifier|private
name|ParentBulkByScrollTask
name|task
decl_stmt|;
annotation|@
name|Before
DECL|method|createTask
specifier|public
name|void
name|createTask
parameter_list|()
block|{
name|slices
operator|=
name|between
argument_list|(
literal|2
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|task
operator|=
operator|new
name|ParentBulkByScrollTask
argument_list|(
literal|1
argument_list|,
literal|"test_type"
argument_list|,
literal|"test_action"
argument_list|,
literal|"test"
argument_list|,
literal|null
argument_list|,
name|slices
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test rethrottling.      * @param runningSlices the number of slices still running      * @param simulator simulate a response from the sub-request to rethrottle the child requests      * @param verifier verify the resulting response      */
DECL|method|rethrottleTestCase
specifier|private
name|void
name|rethrottleTestCase
parameter_list|(
name|int
name|runningSlices
parameter_list|,
name|Consumer
argument_list|<
name|ActionListener
argument_list|<
name|ListTasksResponse
argument_list|>
argument_list|>
name|simulator
parameter_list|,
name|Consumer
argument_list|<
name|ActionListener
argument_list|<
name|TaskInfo
argument_list|>
argument_list|>
name|verifier
parameter_list|)
block|{
name|Client
name|client
init|=
name|mock
argument_list|(
name|Client
operator|.
name|class
argument_list|)
decl_stmt|;
name|String
name|localNodeId
init|=
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|float
name|newRequestsPerSecond
init|=
name|randomValueOtherThanMany
argument_list|(
name|f
lambda|->
name|f
operator|<=
literal|0
argument_list|,
parameter_list|()
lambda|->
name|randomFloat
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|ActionListener
argument_list|<
name|TaskInfo
argument_list|>
name|listener
init|=
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|TransportRethrottleAction
operator|.
name|rethrottle
argument_list|(
name|logger
argument_list|,
name|localNodeId
argument_list|,
name|client
argument_list|,
name|task
argument_list|,
name|newRequestsPerSecond
argument_list|,
name|listener
argument_list|)
expr_stmt|;
comment|// Capture the sub request and the listener so we can verify they are sane
name|ArgumentCaptor
argument_list|<
name|RethrottleRequest
argument_list|>
name|subRequest
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|RethrottleRequest
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|,
literal|"rawtypes"
block|}
argument_list|)
comment|// Magical generics incantation.....
name|ArgumentCaptor
argument_list|<
name|ActionListener
argument_list|<
name|ListTasksResponse
argument_list|>
argument_list|>
name|subListener
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
operator|(
name|Class
operator|)
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|runningSlices
operator|>
literal|0
condition|)
block|{
name|verify
argument_list|(
name|client
argument_list|)
operator|.
name|execute
argument_list|(
name|eq
argument_list|(
name|RethrottleAction
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|subRequest
operator|.
name|capture
argument_list|()
argument_list|,
name|subListener
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TaskId
argument_list|(
name|localNodeId
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|subRequest
operator|.
name|getValue
argument_list|()
operator|.
name|getParentTaskId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|newRequestsPerSecond
operator|/
name|runningSlices
argument_list|,
name|subRequest
operator|.
name|getValue
argument_list|()
operator|.
name|getRequestsPerSecond
argument_list|()
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
name|simulator
operator|.
name|accept
argument_list|(
name|subListener
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|verifier
operator|.
name|accept
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|expectSuccessfulRethrottleWithStatuses
specifier|private
name|Consumer
argument_list|<
name|ActionListener
argument_list|<
name|TaskInfo
argument_list|>
argument_list|>
name|expectSuccessfulRethrottleWithStatuses
parameter_list|(
name|List
argument_list|<
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|>
name|sliceStatuses
parameter_list|)
block|{
return|return
name|listener
lambda|->
block|{
name|TaskInfo
name|taskInfo
init|=
name|captureResponse
argument_list|(
name|TaskInfo
operator|.
name|class
argument_list|,
name|listener
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|sliceStatuses
argument_list|,
operator|(
operator|(
name|BulkByScrollTask
operator|.
name|Status
operator|)
name|taskInfo
operator|.
name|getStatus
argument_list|()
operator|)
operator|.
name|getSliceStatuses
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|;
block|}
DECL|method|testRethrottleSuccessfulResponse
specifier|public
name|void
name|testRethrottleSuccessfulResponse
parameter_list|()
block|{
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|>
name|sliceStatuses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slices
argument_list|)
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
name|slices
condition|;
name|i
operator|++
control|)
block|{
name|BulkByScrollTask
operator|.
name|Status
name|status
init|=
name|believeableInProgressStatus
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|TaskInfo
argument_list|(
operator|new
name|TaskId
argument_list|(
literal|"test"
argument_list|,
literal|123
argument_list|)
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|status
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
operator|new
name|TaskId
argument_list|(
literal|"test"
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sliceStatuses
operator|.
name|add
argument_list|(
operator|new
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rethrottleTestCase
argument_list|(
name|slices
argument_list|,
name|listener
lambda|->
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ListTasksResponse
argument_list|(
name|tasks
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|expectSuccessfulRethrottleWithStatuses
argument_list|(
name|sliceStatuses
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRethrottleWithSomeSucceeded
specifier|public
name|void
name|testRethrottleWithSomeSucceeded
parameter_list|()
block|{
name|int
name|succeeded
init|=
name|between
argument_list|(
literal|1
argument_list|,
name|slices
operator|-
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|>
name|sliceStatuses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slices
argument_list|)
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
name|succeeded
condition|;
name|i
operator|++
control|)
block|{
name|BulkByScrollTask
operator|.
name|Status
name|status
init|=
name|believeableCompletedStatus
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|task
operator|.
name|onSliceResponse
argument_list|(
name|neverCalled
argument_list|()
argument_list|,
name|i
argument_list|,
operator|new
name|BulkByScrollResponse
argument_list|(
name|timeValueMillis
argument_list|(
literal|10
argument_list|)
argument_list|,
name|status
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|sliceStatuses
operator|.
name|add
argument_list|(
operator|new
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|succeeded
init|;
name|i
operator|<
name|slices
condition|;
name|i
operator|++
control|)
block|{
name|BulkByScrollTask
operator|.
name|Status
name|status
init|=
name|believeableInProgressStatus
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|tasks
operator|.
name|add
argument_list|(
operator|new
name|TaskInfo
argument_list|(
operator|new
name|TaskId
argument_list|(
literal|"test"
argument_list|,
literal|123
argument_list|)
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"test"
argument_list|,
name|status
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
operator|new
name|TaskId
argument_list|(
literal|"test"
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sliceStatuses
operator|.
name|add
argument_list|(
operator|new
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rethrottleTestCase
argument_list|(
name|slices
operator|-
name|succeeded
argument_list|,
name|listener
lambda|->
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ListTasksResponse
argument_list|(
name|tasks
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|expectSuccessfulRethrottleWithStatuses
argument_list|(
name|sliceStatuses
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRethrottleWithAllSucceeded
specifier|public
name|void
name|testRethrottleWithAllSucceeded
parameter_list|()
block|{
name|List
argument_list|<
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|>
name|sliceStatuses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|slices
argument_list|)
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
name|slices
condition|;
name|i
operator|++
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|ActionListener
argument_list|<
name|BulkByScrollResponse
argument_list|>
name|listener
init|=
name|i
operator|<
name|slices
operator|-
literal|1
condition|?
name|neverCalled
argument_list|()
else|:
name|mock
argument_list|(
name|ActionListener
operator|.
name|class
argument_list|)
decl_stmt|;
name|BulkByScrollTask
operator|.
name|Status
name|status
init|=
name|believeableCompletedStatus
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|task
operator|.
name|onSliceResponse
argument_list|(
name|listener
argument_list|,
name|i
argument_list|,
operator|new
name|BulkByScrollResponse
argument_list|(
name|timeValueMillis
argument_list|(
literal|10
argument_list|)
argument_list|,
name|status
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|slices
operator|-
literal|1
condition|)
block|{
comment|// The whole thing succeeded so we should have got the success
name|captureResponse
argument_list|(
name|BulkByScrollResponse
operator|.
name|class
argument_list|,
name|listener
argument_list|)
operator|.
name|getStatus
argument_list|()
expr_stmt|;
block|}
name|sliceStatuses
operator|.
name|add
argument_list|(
operator|new
name|BulkByScrollTask
operator|.
name|StatusOrException
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|rethrottleTestCase
argument_list|(
literal|0
argument_list|,
name|listener
lambda|->
block|{
comment|/* There are no async tasks to simulate because the listener is called for us. */
block|}
argument_list|,
name|expectSuccessfulRethrottleWithStatuses
argument_list|(
name|sliceStatuses
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|expectException
specifier|private
name|Consumer
argument_list|<
name|ActionListener
argument_list|<
name|TaskInfo
argument_list|>
argument_list|>
name|expectException
parameter_list|(
name|Matcher
argument_list|<
name|Exception
argument_list|>
name|exceptionMatcher
parameter_list|)
block|{
return|return
name|listener
lambda|->
block|{
name|ArgumentCaptor
argument_list|<
name|Exception
argument_list|>
name|failure
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|listener
argument_list|)
operator|.
name|onFailure
argument_list|(
name|failure
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|failure
operator|.
name|getValue
argument_list|()
argument_list|,
name|exceptionMatcher
argument_list|)
expr_stmt|;
block|}
return|;
block|}
DECL|method|testRethrottleCatastrophicFailures
specifier|public
name|void
name|testRethrottleCatastrophicFailures
parameter_list|()
block|{
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|()
decl_stmt|;
name|rethrottleTestCase
argument_list|(
name|slices
argument_list|,
name|listener
lambda|->
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
argument_list|,
name|expectException
argument_list|(
name|theInstance
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRethrottleTaskOperationFailure
specifier|public
name|void
name|testRethrottleTaskOperationFailure
parameter_list|()
block|{
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|()
decl_stmt|;
name|TaskOperationFailure
name|failure
init|=
operator|new
name|TaskOperationFailure
argument_list|(
literal|"test"
argument_list|,
literal|123
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|rethrottleTestCase
argument_list|(
name|slices
argument_list|,
name|listener
lambda|->
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ListTasksResponse
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|singletonList
argument_list|(
name|failure
argument_list|)
argument_list|,
name|emptyList
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|expectException
argument_list|(
name|hasToString
argument_list|(
name|containsString
argument_list|(
literal|"Rethrottle of [test:123] failed"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRethrottleNodeFailure
specifier|public
name|void
name|testRethrottleNodeFailure
parameter_list|()
block|{
name|FailedNodeException
name|e
init|=
operator|new
name|FailedNodeException
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
decl_stmt|;
name|rethrottleTestCase
argument_list|(
name|slices
argument_list|,
name|listener
lambda|->
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ListTasksResponse
argument_list|(
name|emptyList
argument_list|()
argument_list|,
name|emptyList
argument_list|()
argument_list|,
name|singletonList
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|expectException
argument_list|(
name|theInstance
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|believeableInProgressStatus
specifier|private
name|BulkByScrollTask
operator|.
name|Status
name|believeableInProgressStatus
parameter_list|(
name|Integer
name|sliceId
parameter_list|)
block|{
return|return
operator|new
name|BulkByScrollTask
operator|.
name|Status
argument_list|(
name|sliceId
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
DECL|method|believeableCompletedStatus
specifier|private
name|BulkByScrollTask
operator|.
name|Status
name|believeableCompletedStatus
parameter_list|(
name|Integer
name|sliceId
parameter_list|)
block|{
return|return
operator|new
name|BulkByScrollTask
operator|.
name|Status
argument_list|(
name|sliceId
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
DECL|method|neverCalled
specifier|private
parameter_list|<
name|T
parameter_list|>
name|ActionListener
argument_list|<
name|T
argument_list|>
name|neverCalled
parameter_list|()
block|{
return|return
operator|new
name|ActionListener
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|T
name|response
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Expected no interactions but got ["
operator|+
name|response
operator|+
literal|"]"
argument_list|)
throw|;
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Expected no interations but was received a failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|;
block|}
DECL|method|captureResponse
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|captureResponse
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|responseClass
parameter_list|,
name|ActionListener
argument_list|<
name|T
argument_list|>
name|listener
parameter_list|)
block|{
name|ArgumentCaptor
argument_list|<
name|Exception
argument_list|>
name|failure
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Rethrow any failures just so we get a nice exception if there were any. We don't expect any though.
name|verify
argument_list|(
name|listener
argument_list|,
name|atMost
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|onFailure
argument_list|(
name|failure
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
literal|false
operator|==
name|failure
operator|.
name|getAllValues
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|failure
operator|.
name|getValue
argument_list|()
argument_list|)
throw|;
block|}
name|ArgumentCaptor
argument_list|<
name|T
argument_list|>
name|response
init|=
name|ArgumentCaptor
operator|.
name|forClass
argument_list|(
name|responseClass
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|listener
argument_list|)
operator|.
name|onResponse
argument_list|(
name|response
operator|.
name|capture
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

