begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

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
name|Function
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
name|test
operator|.
name|ESTestCase
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|equalTo
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
name|lessThan
import|;
end_import

begin_comment
comment|/**  * Tests for the automatic queue resizing of the {@code QueueResizingEsThreadPoolExecutorTests}  * based on the time taken for each event.  */
end_comment

begin_class
DECL|class|QueueResizingEsThreadPoolExecutorTests
specifier|public
class|class
name|QueueResizingEsThreadPoolExecutorTests
extends|extends
name|ESTestCase
block|{
DECL|method|testExactWindowSizeAdjustment
specifier|public
name|void
name|testExactWindowSizeAdjustment
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadContext
name|context
init|=
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ResizableBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ResizableBlockingQueue
argument_list|<>
argument_list|(
name|ConcurrentCollections
operator|.
expr|<
name|Runnable
operator|>
name|newBlockingQueue
argument_list|()
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|threads
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|int
name|measureWindow
init|=
literal|3
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> auto-queue with a measurement window of {} tasks"
argument_list|,
name|measureWindow
argument_list|)
expr_stmt|;
name|QueueResizingEsThreadPoolExecutor
name|executor
init|=
operator|new
name|QueueResizingEsThreadPoolExecutor
argument_list|(
literal|"test-threadpool"
argument_list|,
name|threads
argument_list|,
name|threads
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
literal|10
argument_list|,
literal|1000
argument_list|,
name|fastWrapper
argument_list|()
argument_list|,
name|measureWindow
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
literal|"queuetest"
argument_list|)
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|executor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> executor: {}"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
comment|// Execute exactly 3 (measureWindow) times
name|executor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{}
argument_list|)
expr_stmt|;
comment|// The queue capacity should have increased by 50 since they were very fast tasks
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|queue
operator|.
name|capacity
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|150
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAutoQueueSizingUp
specifier|public
name|void
name|testAutoQueueSizingUp
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadContext
name|context
init|=
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ResizableBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ResizableBlockingQueue
argument_list|<>
argument_list|(
name|ConcurrentCollections
operator|.
expr|<
name|Runnable
operator|>
name|newBlockingQueue
argument_list|()
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
name|int
name|threads
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|measureWindow
init|=
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> auto-queue with a measurement window of {} tasks"
argument_list|,
name|measureWindow
argument_list|)
expr_stmt|;
name|QueueResizingEsThreadPoolExecutor
name|executor
init|=
operator|new
name|QueueResizingEsThreadPoolExecutor
argument_list|(
literal|"test-threadpool"
argument_list|,
name|threads
argument_list|,
name|threads
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
literal|10
argument_list|,
literal|3000
argument_list|,
name|fastWrapper
argument_list|()
argument_list|,
name|measureWindow
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
literal|"queuetest"
argument_list|)
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|executor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> executor: {}"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
comment|// Execute a task multiple times that takes 1ms
name|executeTask
argument_list|(
name|executor
argument_list|,
operator|(
name|measureWindow
operator|*
literal|5
operator|)
operator|+
literal|2
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|queue
operator|.
name|capacity
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAutoQueueSizingDown
specifier|public
name|void
name|testAutoQueueSizingDown
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadContext
name|context
init|=
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ResizableBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ResizableBlockingQueue
argument_list|<>
argument_list|(
name|ConcurrentCollections
operator|.
expr|<
name|Runnable
operator|>
name|newBlockingQueue
argument_list|()
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
name|int
name|threads
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|measureWindow
init|=
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> auto-queue with a measurement window of {} tasks"
argument_list|,
name|measureWindow
argument_list|)
expr_stmt|;
name|QueueResizingEsThreadPoolExecutor
name|executor
init|=
operator|new
name|QueueResizingEsThreadPoolExecutor
argument_list|(
literal|"test-threadpool"
argument_list|,
name|threads
argument_list|,
name|threads
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
literal|10
argument_list|,
literal|3000
argument_list|,
name|slowWrapper
argument_list|()
argument_list|,
name|measureWindow
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
literal|"queuetest"
argument_list|)
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|executor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> executor: {}"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
comment|// Execute a task multiple times that takes 1m
name|executeTask
argument_list|(
name|executor
argument_list|,
operator|(
name|measureWindow
operator|*
literal|5
operator|)
operator|+
literal|2
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|queue
operator|.
name|capacity
argument_list|()
argument_list|,
name|lessThan
argument_list|(
literal|2000
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAutoQueueSizingWithMin
specifier|public
name|void
name|testAutoQueueSizingWithMin
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadContext
name|context
init|=
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ResizableBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ResizableBlockingQueue
argument_list|<>
argument_list|(
name|ConcurrentCollections
operator|.
expr|<
name|Runnable
operator|>
name|newBlockingQueue
argument_list|()
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|int
name|threads
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|int
name|measureWindow
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
empty_stmt|;
name|int
name|min
init|=
name|randomIntBetween
argument_list|(
literal|4981
argument_list|,
literal|4999
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> auto-queue with a measurement window of {} tasks"
argument_list|,
name|measureWindow
argument_list|)
expr_stmt|;
name|QueueResizingEsThreadPoolExecutor
name|executor
init|=
operator|new
name|QueueResizingEsThreadPoolExecutor
argument_list|(
literal|"test-threadpool"
argument_list|,
name|threads
argument_list|,
name|threads
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
name|min
argument_list|,
literal|100000
argument_list|,
name|slowWrapper
argument_list|()
argument_list|,
name|measureWindow
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
literal|"queuetest"
argument_list|)
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|executor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> executor: {}"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
comment|// Execute a task multiple times that takes 1m
name|executeTask
argument_list|(
name|executor
argument_list|,
operator|(
name|measureWindow
operator|*
literal|5
operator|)
argument_list|)
expr_stmt|;
comment|// The queue capacity should decrease, but no lower than the minimum
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|queue
operator|.
name|capacity
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testAutoQueueSizingWithMax
specifier|public
name|void
name|testAutoQueueSizingWithMax
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadContext
name|context
init|=
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ResizableBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ResizableBlockingQueue
argument_list|<>
argument_list|(
name|ConcurrentCollections
operator|.
expr|<
name|Runnable
operator|>
name|newBlockingQueue
argument_list|()
argument_list|,
literal|5000
argument_list|)
decl_stmt|;
name|int
name|threads
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|int
name|measureWindow
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|max
init|=
name|randomIntBetween
argument_list|(
literal|5010
argument_list|,
literal|5024
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> auto-queue with a measurement window of {} tasks"
argument_list|,
name|measureWindow
argument_list|)
expr_stmt|;
name|QueueResizingEsThreadPoolExecutor
name|executor
init|=
operator|new
name|QueueResizingEsThreadPoolExecutor
argument_list|(
literal|"test-threadpool"
argument_list|,
name|threads
argument_list|,
name|threads
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
literal|10
argument_list|,
name|max
argument_list|,
name|fastWrapper
argument_list|()
argument_list|,
name|measureWindow
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
literal|"queuetest"
argument_list|)
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|executor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> executor: {}"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
comment|// Execute a task multiple times that takes 1ms
name|executeTask
argument_list|(
name|executor
argument_list|,
name|measureWindow
operator|*
literal|3
argument_list|)
expr_stmt|;
comment|// The queue capacity should increase, but no higher than the maximum
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
name|queue
operator|.
name|capacity
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testExecutionEWMACalculation
specifier|public
name|void
name|testExecutionEWMACalculation
parameter_list|()
throws|throws
name|Exception
block|{
name|ThreadContext
name|context
init|=
operator|new
name|ThreadContext
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ResizableBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ResizableBlockingQueue
argument_list|<>
argument_list|(
name|ConcurrentCollections
operator|.
expr|<
name|Runnable
operator|>
name|newBlockingQueue
argument_list|()
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|QueueResizingEsThreadPoolExecutor
name|executor
init|=
operator|new
name|QueueResizingEsThreadPoolExecutor
argument_list|(
literal|"test-threadpool"
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
literal|10
argument_list|,
literal|200
argument_list|,
name|fastWrapper
argument_list|()
argument_list|,
literal|10
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
literal|"queuetest"
argument_list|)
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|executor
operator|.
name|prestartAllCoreThreads
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> executor: {}"
argument_list|,
name|executor
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|executor
operator|.
name|getTaskExecutionEWMA
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1000000L
argument_list|)
argument_list|)
expr_stmt|;
name|executeTask
argument_list|(
name|executor
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|executor
operator|.
name|getTaskExecutionEWMA
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|700030L
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executeTask
argument_list|(
name|executor
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|executor
operator|.
name|getTaskExecutionEWMA
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|490050L
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executeTask
argument_list|(
name|executor
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|executor
operator|.
name|getTaskExecutionEWMA
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|343065L
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executeTask
argument_list|(
name|executor
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|executor
operator|.
name|getTaskExecutionEWMA
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|240175L
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executeTask
argument_list|(
name|executor
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
block|{
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|executor
operator|.
name|getTaskExecutionEWMA
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|168153L
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|context
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|randomBetweenLimitsWrapper
specifier|private
name|Function
argument_list|<
name|Runnable
argument_list|,
name|Runnable
argument_list|>
name|randomBetweenLimitsWrapper
parameter_list|(
specifier|final
name|int
name|minNs
parameter_list|,
specifier|final
name|int
name|maxNs
parameter_list|)
block|{
return|return
parameter_list|(
name|runnable
parameter_list|)
lambda|->
block|{
return|return
operator|new
name|SettableTimedRunnable
argument_list|(
name|randomIntBetween
argument_list|(
name|minNs
argument_list|,
name|maxNs
argument_list|)
argument_list|)
return|;
block|}
return|;
block|}
DECL|method|fastWrapper
specifier|private
name|Function
argument_list|<
name|Runnable
argument_list|,
name|Runnable
argument_list|>
name|fastWrapper
parameter_list|()
block|{
return|return
parameter_list|(
name|runnable
parameter_list|)
lambda|->
block|{
return|return
operator|new
name|SettableTimedRunnable
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
operator|.
name|toNanos
argument_list|(
literal|100
argument_list|)
argument_list|)
return|;
block|}
return|;
block|}
DECL|method|slowWrapper
specifier|private
name|Function
argument_list|<
name|Runnable
argument_list|,
name|Runnable
argument_list|>
name|slowWrapper
parameter_list|()
block|{
return|return
parameter_list|(
name|runnable
parameter_list|)
lambda|->
block|{
return|return
operator|new
name|SettableTimedRunnable
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toNanos
argument_list|(
literal|2
argument_list|)
argument_list|)
return|;
block|}
return|;
block|}
comment|/** Execute a blank task {@code times} times for the executor */
DECL|method|executeTask
specifier|private
name|void
name|executeTask
parameter_list|(
name|QueueResizingEsThreadPoolExecutor
name|executor
parameter_list|,
name|int
name|times
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> executing a task [{}] times"
argument_list|,
name|times
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|times
condition|;
name|i
operator|++
control|)
block|{
name|executor
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SettableTimedRunnable
specifier|public
class|class
name|SettableTimedRunnable
extends|extends
name|TimedRunnable
block|{
DECL|field|timeTaken
specifier|private
specifier|final
name|long
name|timeTaken
decl_stmt|;
DECL|method|SettableTimedRunnable
specifier|public
name|SettableTimedRunnable
parameter_list|(
name|long
name|timeTaken
parameter_list|)
block|{
name|super
argument_list|(
parameter_list|()
lambda|->
block|{}
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeTaken
operator|=
name|timeTaken
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTotalNanos
specifier|public
name|long
name|getTotalNanos
parameter_list|()
block|{
return|return
name|timeTaken
return|;
block|}
annotation|@
name|Override
DECL|method|getTotalExecutionNanos
specifier|public
name|long
name|getTotalExecutionNanos
parameter_list|()
block|{
return|return
name|timeTaken
return|;
block|}
block|}
block|}
end_class

end_unit

