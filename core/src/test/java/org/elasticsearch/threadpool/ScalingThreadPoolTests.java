begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
package|;
end_package

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
name|ClusterSettings
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
name|EsThreadPoolExecutor
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
name|Executor
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
name|hasToString
import|;
end_import

begin_class
DECL|class|ScalingThreadPoolTests
specifier|public
class|class
name|ScalingThreadPoolTests
extends|extends
name|ESThreadPoolTestCase
block|{
DECL|method|testScalingThreadPoolConfiguration
specifier|public
name|void
name|testScalingThreadPoolConfiguration
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|threadPoolName
init|=
name|randomThreadPool
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
decl_stmt|;
specifier|final
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|min
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|min
operator|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".min"
argument_list|,
name|min
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|min
operator|=
literal|"generic"
operator|.
name|equals
argument_list|(
name|threadPoolName
argument_list|)
condition|?
literal|4
else|:
literal|1
expr_stmt|;
comment|// the defaults
block|}
specifier|final
name|int
name|sizeBasedOnNumberOfProcessors
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|processors
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|64
argument_list|)
decl_stmt|;
name|sizeBasedOnNumberOfProcessors
operator|=
name|expectedSize
argument_list|(
name|threadPoolName
argument_list|,
name|processors
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"processors"
argument_list|,
name|processors
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sizeBasedOnNumberOfProcessors
operator|=
name|expectedSize
argument_list|(
name|threadPoolName
argument_list|,
name|Math
operator|.
name|min
argument_list|(
literal|32
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|expectedSize
decl_stmt|;
if|if
condition|(
name|sizeBasedOnNumberOfProcessors
operator|<
name|min
operator|||
name|randomBoolean
argument_list|()
condition|)
block|{
name|expectedSize
operator|=
name|randomIntBetween
argument_list|(
name|min
argument_list|,
literal|16
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".size"
argument_list|,
name|expectedSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expectedSize
operator|=
name|sizeBasedOnNumberOfProcessors
expr_stmt|;
block|}
specifier|final
name|long
name|keepAlive
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|keepAlive
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".keep_alive"
argument_list|,
name|keepAlive
operator|+
literal|"s"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|keepAlive
operator|=
literal|"generic"
operator|.
name|equals
argument_list|(
name|threadPoolName
argument_list|)
condition|?
literal|30
else|:
literal|300
expr_stmt|;
comment|// the defaults
block|}
name|runScalingThreadPoolTest
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
parameter_list|(
name|clusterSettings
parameter_list|,
name|threadPool
parameter_list|)
lambda|->
block|{
specifier|final
name|Executor
name|executor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|executor
argument_list|,
name|instanceOf
argument_list|(
name|EsThreadPoolExecutor
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|EsThreadPoolExecutor
name|esThreadPoolExecutor
init|=
operator|(
name|EsThreadPoolExecutor
operator|)
name|executor
decl_stmt|;
specifier|final
name|ThreadPool
operator|.
name|Info
name|info
init|=
name|info
argument_list|(
name|threadPool
argument_list|,
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|threadPoolName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getThreadPoolType
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getKeepAlive
argument_list|()
operator|.
name|seconds
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|keepAlive
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|esThreadPoolExecutor
operator|.
name|getKeepAliveTime
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|keepAlive
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|info
operator|.
name|getQueueSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|esThreadPoolExecutor
operator|.
name|getQueue
argument_list|()
operator|.
name|remainingCapacity
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getMin
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|esThreadPoolExecutor
operator|.
name|getCorePoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getMax
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedSize
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|esThreadPoolExecutor
operator|.
name|getMaximumPoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|FunctionalInterface
DECL|interface|SizeFunction
specifier|private
interface|interface
name|SizeFunction
block|{
DECL|method|size
name|int
name|size
parameter_list|(
name|int
name|numberOfProcessors
parameter_list|)
function_decl|;
block|}
DECL|method|expectedSize
specifier|private
name|int
name|expectedSize
parameter_list|(
specifier|final
name|String
name|threadPoolName
parameter_list|,
specifier|final
name|int
name|numberOfProcessors
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SizeFunction
argument_list|>
name|sizes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
name|n
lambda|->
name|ThreadPool
operator|.
name|boundedBy
argument_list|(
literal|4
operator|*
name|n
argument_list|,
literal|128
argument_list|,
literal|512
argument_list|)
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
argument_list|,
name|n
lambda|->
literal|5
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|FLUSH
argument_list|,
name|ThreadPool
operator|::
name|halfNumberOfProcessorsMaxFive
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|REFRESH
argument_list|,
name|ThreadPool
operator|::
name|halfNumberOfProcessorsMaxTen
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|WARMER
argument_list|,
name|ThreadPool
operator|::
name|halfNumberOfProcessorsMaxFive
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SNAPSHOT
argument_list|,
name|ThreadPool
operator|::
name|halfNumberOfProcessorsMaxFive
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|FETCH_SHARD_STARTED
argument_list|,
name|ThreadPool
operator|::
name|twiceNumberOfProcessors
argument_list|)
expr_stmt|;
name|sizes
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|FETCH_SHARD_STORE
argument_list|,
name|ThreadPool
operator|::
name|twiceNumberOfProcessors
argument_list|)
expr_stmt|;
return|return
name|sizes
operator|.
name|get
argument_list|(
name|threadPoolName
argument_list|)
operator|.
name|size
argument_list|(
name|numberOfProcessors
argument_list|)
return|;
block|}
DECL|method|testValidDynamicKeepAlive
specifier|public
name|void
name|testValidDynamicKeepAlive
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|threadPoolName
init|=
name|randomThreadPool
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
decl_stmt|;
name|runScalingThreadPoolTest
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
parameter_list|(
name|clusterSettings
parameter_list|,
name|threadPool
parameter_list|)
lambda|->
block|{
specifier|final
name|Executor
name|beforeExecutor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
decl_stmt|;
specifier|final
name|long
name|seconds
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|300
argument_list|)
decl_stmt|;
name|clusterSettings
operator|.
name|applySettings
argument_list|(
name|settings
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".keep_alive"
argument_list|,
name|seconds
operator|+
literal|"s"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Executor
name|afterExecutor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|beforeExecutor
argument_list|,
name|afterExecutor
argument_list|)
expr_stmt|;
specifier|final
name|ThreadPool
operator|.
name|Info
name|info
init|=
name|info
argument_list|(
name|threadPool
argument_list|,
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getKeepAlive
argument_list|()
operator|.
name|seconds
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|seconds
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testScalingThreadPoolIsBounded
specifier|public
name|void
name|testScalingThreadPoolIsBounded
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|threadPoolName
init|=
name|randomThreadPool
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|32
argument_list|,
literal|512
argument_list|)
decl_stmt|;
specifier|final
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".size"
argument_list|,
name|size
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|runScalingThreadPoolTest
argument_list|(
name|settings
argument_list|,
parameter_list|(
name|clusterSettings
parameter_list|,
name|threadPool
parameter_list|)
lambda|->
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numberOfTasks
init|=
literal|2
operator|*
name|size
decl_stmt|;
specifier|final
name|CountDownLatch
name|taskLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numberOfTasks
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
name|numberOfTasks
condition|;
name|i
operator|++
control|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|taskLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
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
block|}
specifier|final
name|ThreadPoolStats
operator|.
name|Stats
name|stats
init|=
name|stats
argument_list|(
name|threadPool
argument_list|,
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getQueue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|numberOfTasks
operator|-
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|stats
operator|.
name|getLargest
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|taskLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
block|}
DECL|method|testScalingThreadPoolThreadsAreTerminatedAfterKeepAlive
specifier|public
name|void
name|testScalingThreadPoolThreadsAreTerminatedAfterKeepAlive
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|threadPoolName
init|=
name|randomThreadPool
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
decl_stmt|;
specifier|final
name|int
name|min
init|=
literal|"generic"
operator|.
name|equals
argument_list|(
name|threadPoolName
argument_list|)
condition|?
literal|4
else|:
literal|1
decl_stmt|;
specifier|final
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".size"
argument_list|,
literal|128
argument_list|)
operator|.
name|put
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".keep_alive"
argument_list|,
literal|"1ms"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|runScalingThreadPoolTest
argument_list|(
name|settings
argument_list|,
operator|(
parameter_list|(
name|clusterSettings
parameter_list|,
name|threadPool
parameter_list|)
lambda|->
block|{
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|taskLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|128
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
literal|128
condition|;
name|i
operator|++
control|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|taskLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
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
block|}
name|assertThat
argument_list|(
name|stats
argument_list|(
name|threadPool
argument_list|,
name|threadPoolName
argument_list|)
operator|.
name|getThreads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|128
argument_list|)
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
comment|// this while loop is the core of this test; if threads
comment|// are correctly idled down by the pool, the number of
comment|// threads in the pool will drop to the min for the pool
comment|// but if threads are not correctly idled down by the pool,
comment|// this test will just timeout waiting for them to idle
comment|// down
do|do
block|{
name|spinForAtLeastOneMillisecond
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|stats
argument_list|(
name|threadPool
argument_list|,
name|threadPoolName
argument_list|)
operator|.
name|getThreads
argument_list|()
operator|>
name|min
condition|)
do|;
try|try
block|{
name|taskLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
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
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDynamicThreadPoolSize
specifier|public
name|void
name|testDynamicThreadPoolSize
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|threadPoolName
init|=
name|randomThreadPool
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
decl_stmt|;
name|runScalingThreadPoolTest
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
parameter_list|(
name|clusterSettings
parameter_list|,
name|threadPool
parameter_list|)
lambda|->
block|{
specifier|final
name|Executor
name|beforeExecutor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
decl_stmt|;
name|int
name|expectedMin
init|=
literal|"generic"
operator|.
name|equals
argument_list|(
name|threadPoolName
argument_list|)
condition|?
literal|4
else|:
literal|1
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
name|expectedMin
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|clusterSettings
operator|.
name|applySettings
argument_list|(
name|settings
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".size"
argument_list|,
name|size
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Executor
name|afterExecutor
init|=
name|threadPool
operator|.
name|executor
argument_list|(
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|beforeExecutor
argument_list|,
name|afterExecutor
argument_list|)
expr_stmt|;
specifier|final
name|ThreadPool
operator|.
name|Info
name|info
init|=
name|info
argument_list|(
name|threadPool
argument_list|,
name|threadPoolName
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getMin
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedMin
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getMax
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|afterExecutor
argument_list|,
name|instanceOf
argument_list|(
name|EsThreadPoolExecutor
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|EsThreadPoolExecutor
name|executor
init|=
operator|(
name|EsThreadPoolExecutor
operator|)
name|afterExecutor
decl_stmt|;
name|assertThat
argument_list|(
name|executor
operator|.
name|getCorePoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedMin
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|executor
operator|.
name|getMaximumPoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|size
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testResizingScalingThreadPoolQueue
specifier|public
name|void
name|testResizingScalingThreadPoolQueue
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|threadPoolName
init|=
name|randomThreadPool
argument_list|(
name|ThreadPool
operator|.
name|ThreadPoolType
operator|.
name|SCALING
argument_list|)
decl_stmt|;
name|runScalingThreadPoolTest
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
parameter_list|(
name|clusterSettings
parameter_list|,
name|threadPool
parameter_list|)
lambda|->
block|{
specifier|final
name|int
name|size
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
specifier|final
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|clusterSettings
operator|.
name|applySettings
argument_list|(
name|settings
argument_list|(
literal|"threadpool."
operator|+
name|threadPoolName
operator|+
literal|".queue_size"
argument_list|,
name|size
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
argument_list|,
name|hasToString
argument_list|(
literal|"java.lang.IllegalArgumentException: thread pool ["
operator|+
name|threadPoolName
operator|+
literal|"] of type scaling can not have its queue re-sized but was ["
operator|+
name|size
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|runScalingThreadPoolTest
specifier|public
name|void
name|runScalingThreadPoolTest
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|,
specifier|final
name|BiConsumer
argument_list|<
name|ClusterSettings
argument_list|,
name|ThreadPool
argument_list|>
name|consumer
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|ThreadPool
name|threadPool
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|String
name|test
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getStackTrace
argument_list|()
index|[
literal|2
index|]
operator|.
name|getMethodName
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|nodeSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
name|test
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|threadPool
operator|=
operator|new
name|ThreadPool
argument_list|(
name|nodeSettings
argument_list|)
expr_stmt|;
specifier|final
name|ClusterSettings
name|clusterSettings
init|=
operator|new
name|ClusterSettings
argument_list|(
name|nodeSettings
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
decl_stmt|;
name|threadPool
operator|.
name|setClusterSettings
argument_list|(
name|clusterSettings
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|accept
argument_list|(
name|clusterSettings
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|terminateThreadPoolIfNeeded
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|settings
specifier|private
specifier|static
name|Settings
name|settings
parameter_list|(
specifier|final
name|String
name|setting
parameter_list|,
specifier|final
name|int
name|value
parameter_list|)
block|{
return|return
name|settings
argument_list|(
name|setting
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|settings
specifier|private
specifier|static
name|Settings
name|settings
parameter_list|(
specifier|final
name|String
name|setting
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|setting
argument_list|,
name|value
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

