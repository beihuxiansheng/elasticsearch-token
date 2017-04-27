begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.disruption
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|disruption
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
name|Nullable
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
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ThreadInfo
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
name|AtomicBoolean
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_class
DECL|class|LongGCDisruptionTests
specifier|public
class|class
name|LongGCDisruptionTests
extends|extends
name|ESTestCase
block|{
DECL|class|LockedExecutor
specifier|static
class|class
name|LockedExecutor
block|{
DECL|field|lock
name|ReentrantLock
name|lock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|method|executeLocked
specifier|public
name|void
name|executeLocked
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|r
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testBlockingTimeout
specifier|public
name|void
name|testBlockingTimeout
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|nodeName
init|=
literal|"test_node"
decl_stmt|;
name|LongGCDisruption
name|disruption
init|=
operator|new
name|LongGCDisruption
argument_list|(
name|random
argument_list|()
argument_list|,
name|nodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Pattern
index|[]
name|getUnsafeClasses
parameter_list|()
block|{
return|return
operator|new
name|Pattern
index|[]
block|{
name|Pattern
operator|.
name|compile
argument_list|(
name|LockedExecutor
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|getSuspendingTimeoutInMillis
parameter_list|()
block|{
return|return
literal|100
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|underLock
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|pauseUnderLock
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|LockedExecutor
name|lockedExecutor
init|=
operator|new
name|LockedExecutor
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|ops
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|10
index|]
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// at least one locked and one none lock thread
specifier|final
name|boolean
name|lockedExec
init|=
operator|(
name|i
operator|<
literal|9
operator|&&
name|randomBoolean
argument_list|()
operator|)
operator|||
name|i
operator|==
literal|0
decl_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|stop
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|lockedExec
condition|)
block|{
name|lockedExecutor
operator|.
name|executeLocked
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|underLock
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|pauseUnderLock
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
block|{                                  }
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"["
operator|+
name|nodeName
operator|+
literal|"]["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// make sure some threads are under lock
name|underLock
operator|.
name|await
argument_list|()
expr_stmt|;
name|RuntimeException
name|e
init|=
name|expectThrows
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
name|disruption
operator|::
name|startDisrupting
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"suspending node threads took too long"
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pauseUnderLock
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Checks that a GC disruption never blocks threads while they are doing something "unsafe"      * but does keep retrying until all threads can be safely paused      */
DECL|method|testNotBlockingUnsafeStackTraces
specifier|public
name|void
name|testNotBlockingUnsafeStackTraces
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|nodeName
init|=
literal|"test_node"
decl_stmt|;
name|LongGCDisruption
name|disruption
init|=
operator|new
name|LongGCDisruption
argument_list|(
name|random
argument_list|()
argument_list|,
name|nodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Pattern
index|[]
name|getUnsafeClasses
parameter_list|()
block|{
return|return
operator|new
name|Pattern
index|[]
block|{
name|Pattern
operator|.
name|compile
argument_list|(
name|LockedExecutor
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
block|}
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|LockedExecutor
name|lockedExecutor
init|=
operator|new
name|LockedExecutor
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|ops
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
literal|10
index|]
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|stop
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|;
name|iter
operator|++
control|)
block|{
if|if
condition|(
name|iter
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|lockedExecutor
operator|.
name|executeLocked
argument_list|(
parameter_list|()
lambda|->
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
comment|// give some chance to catch this stack trace
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
comment|// give some chance to catch this stack trace
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"["
operator|+
name|nodeName
operator|+
literal|"]["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// make sure some threads are under lock
name|disruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|long
name|first
init|=
name|ops
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|lockedExecutor
operator|.
name|lock
operator|.
name|isLocked
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// no threads should own the lock
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ops
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|first
argument_list|)
argument_list|)
expr_stmt|;
name|disruption
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertThat
argument_list|(
name|ops
operator|.
name|get
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|testBlockDetection
specifier|public
name|void
name|testBlockDetection
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|disruptedNodeName
init|=
literal|"disrupted_node"
decl_stmt|;
specifier|final
name|String
name|blockedNodeName
init|=
literal|"blocked_node"
decl_stmt|;
name|CountDownLatch
name|waitForBlockDetectionResult
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|AtomicReference
argument_list|<
name|ThreadInfo
argument_list|>
name|blockDetectionResult
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
name|LongGCDisruption
name|disruption
init|=
operator|new
name|LongGCDisruption
argument_list|(
name|random
argument_list|()
argument_list|,
name|disruptedNodeName
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|Pattern
index|[]
name|getUnsafeClasses
parameter_list|()
block|{
return|return
operator|new
name|Pattern
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|onBlockDetected
parameter_list|(
name|ThreadInfo
name|blockedThread
parameter_list|,
annotation|@
name|Nullable
name|ThreadInfo
name|blockingThread
parameter_list|)
block|{
name|blockDetectionResult
operator|.
name|set
argument_list|(
name|blockedThread
argument_list|)
expr_stmt|;
name|waitForBlockDetectionResult
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|getBlockDetectionIntervalInMillis
parameter_list|()
block|{
return|return
literal|10L
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|disruption
operator|.
name|isBlockDetectionSupported
argument_list|()
operator|==
literal|false
condition|)
block|{
return|return;
block|}
specifier|final
name|AtomicBoolean
name|stop
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|underLock
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|pauseUnderLock
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|LockedExecutor
name|lockedExecutor
init|=
operator|new
name|LockedExecutor
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|ops
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// at least one locked and one none lock thread
specifier|final
name|boolean
name|lockedExec
init|=
operator|(
name|i
operator|<
literal|4
operator|&&
name|randomBoolean
argument_list|()
operator|)
operator|||
name|i
operator|==
literal|0
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|stop
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|lockedExec
condition|)
block|{
name|lockedExecutor
operator|.
name|executeLocked
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|underLock
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|pauseUnderLock
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
block|{                                  }
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"["
operator|+
name|disruptedNodeName
operator|+
literal|"]["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
comment|// at least one locked and one none lock thread
specifier|final
name|boolean
name|lockedExec
init|=
operator|(
name|i
operator|<
literal|4
operator|&&
name|randomBoolean
argument_list|()
operator|)
operator|||
name|i
operator|==
literal|0
decl_stmt|;
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|stop
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|lockedExec
condition|)
block|{
name|lockedExecutor
operator|.
name|executeLocked
argument_list|(
parameter_list|()
lambda|->
block|{
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ops
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|setName
argument_list|(
literal|"["
operator|+
name|blockedNodeName
operator|+
literal|"]["
operator|+
name|i
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
expr_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// make sure some threads of test_node are under lock
name|underLock
operator|.
name|await
argument_list|()
expr_stmt|;
name|disruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|waitForBlockDetectionResult
operator|.
name|await
argument_list|(
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|disruption
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
name|ThreadInfo
name|threadInfo
init|=
name|blockDetectionResult
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|threadInfo
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|threadInfo
operator|.
name|getThreadName
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"["
operator|+
name|blockedNodeName
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|threadInfo
operator|.
name|getLockOwnerName
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"["
operator|+
name|disruptedNodeName
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|threadInfo
operator|.
name|getLockInfo
argument_list|()
operator|.
name|getClassName
argument_list|()
argument_list|,
name|containsString
argument_list|(
name|ReentrantLock
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stop
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|pauseUnderLock
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

