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
name|common
operator|.
name|SuppressForbidden
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
name|test
operator|.
name|InternalTestCluster
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
name|ManagementFactory
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
name|lang
operator|.
name|management
operator|.
name|ThreadMXBean
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
name|Random
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
name|AtomicReference
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Suspends all threads on the specified node in order to simulate a long gc.  */
end_comment

begin_class
DECL|class|LongGCDisruption
specifier|public
class|class
name|LongGCDisruption
extends|extends
name|SingleNodeDisruption
block|{
DECL|field|unsafeClasses
specifier|private
specifier|static
specifier|final
name|Pattern
index|[]
name|unsafeClasses
init|=
operator|new
name|Pattern
index|[]
block|{
comment|// logging has shared JVM locks; we may suspend a thread and block other nodes from doing their thing
name|Pattern
operator|.
name|compile
argument_list|(
literal|"logging\\.log4j"
argument_list|)
block|,
comment|// security manager is shared across all nodes and it uses synchronized maps internally
name|Pattern
operator|.
name|compile
argument_list|(
literal|"java\\.lang\\.SecurityManager"
argument_list|)
block|,
comment|// SecureRandom instance from SecureRandomHolder class is shared by all nodes
name|Pattern
operator|.
name|compile
argument_list|(
literal|"java\\.security\\.SecureRandom"
argument_list|)
block|}
decl_stmt|;
DECL|field|threadBean
specifier|private
specifier|static
specifier|final
name|ThreadMXBean
name|threadBean
init|=
name|ManagementFactory
operator|.
name|getThreadMXBean
argument_list|()
decl_stmt|;
DECL|field|disruptedNode
specifier|protected
specifier|final
name|String
name|disruptedNode
decl_stmt|;
DECL|field|suspendedThreads
specifier|private
name|Set
argument_list|<
name|Thread
argument_list|>
name|suspendedThreads
decl_stmt|;
DECL|field|blockDetectionThread
specifier|private
name|Thread
name|blockDetectionThread
decl_stmt|;
DECL|method|LongGCDisruption
specifier|public
name|LongGCDisruption
parameter_list|(
name|Random
name|random
parameter_list|,
name|String
name|disruptedNode
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|this
operator|.
name|disruptedNode
operator|=
name|disruptedNode
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startDisrupting
specifier|public
specifier|synchronized
name|void
name|startDisrupting
parameter_list|()
block|{
if|if
condition|(
name|suspendedThreads
operator|==
literal|null
condition|)
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|suspendedThreads
operator|=
name|ConcurrentHashMap
operator|.
name|newKeySet
argument_list|()
expr_stmt|;
specifier|final
name|String
name|currentThreadName
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
assert|assert
name|isDisruptedNodeThread
argument_list|(
name|currentThreadName
argument_list|)
operator|==
literal|false
operator|:
literal|"current thread match pattern. thread name: "
operator|+
name|currentThreadName
operator|+
literal|", node: "
operator|+
name|disruptedNode
assert|;
comment|// we spawn a background thread to protect against deadlock which can happen
comment|// if there are shared resources between caller thread and and suspended threads
comment|// see unsafeClasses to how to avoid that
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|suspendingError
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Thread
name|suspendingThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
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
name|suspendingError
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
comment|// keep trying to suspend threads, until no new threads are discovered.
while|while
condition|(
name|suspendThreads
argument_list|(
name|suspendedThreads
argument_list|)
condition|)
block|{
if|if
condition|(
name|Thread
operator|.
name|interrupted
argument_list|()
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|suspendingThread
operator|.
name|setName
argument_list|(
name|currentThreadName
operator|+
literal|"[LongGCDisruption][threadSuspender]"
argument_list|)
expr_stmt|;
name|suspendingThread
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|suspendingThread
operator|.
name|join
argument_list|(
name|getSuspendingTimeoutInMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|suspendingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// best effort to signal suspending
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|suspendingError
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown error while suspending threads"
argument_list|,
name|suspendingError
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|suspendingThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to suspend node [{}]'s threads within [{}] millis. Suspending thread stack trace:\n {}"
argument_list|,
name|disruptedNode
argument_list|,
name|getSuspendingTimeoutInMillis
argument_list|()
argument_list|,
name|stackTrace
argument_list|(
name|suspendingThread
operator|.
name|getStackTrace
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|suspendingThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// best effort;
try|try
block|{
comment|/*                          * We need to join on the suspending thread in case it has suspended a thread that is in a critical section and                          * needs to be resumed.                          */
name|suspendingThread
operator|.
name|join
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"suspending node threads took too long"
argument_list|)
throw|;
block|}
comment|// block detection checks if other threads are blocked waiting on an object that is held by one
comment|// of the threads that was suspended
if|if
condition|(
name|isBlockDetectionSupported
argument_list|()
condition|)
block|{
name|blockDetectionThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
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
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unexpected exception in blockDetectionThread"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
while|while
condition|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
operator|==
literal|false
condition|)
block|{
name|ThreadInfo
index|[]
name|threadInfos
init|=
name|threadBean
operator|.
name|dumpAllThreads
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|threadInfo
range|:
name|threadInfos
control|)
block|{
if|if
condition|(
name|isDisruptedNodeThread
argument_list|(
name|threadInfo
operator|.
name|getThreadName
argument_list|()
argument_list|)
operator|==
literal|false
operator|&&
name|threadInfo
operator|.
name|getLockOwnerName
argument_list|()
operator|!=
literal|null
operator|&&
name|isDisruptedNodeThread
argument_list|(
name|threadInfo
operator|.
name|getLockOwnerName
argument_list|()
argument_list|)
condition|)
block|{
comment|// find ThreadInfo object of the blocking thread (if available)
name|ThreadInfo
name|blockingThreadInfo
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ThreadInfo
name|otherThreadInfo
range|:
name|threadInfos
control|)
block|{
if|if
condition|(
name|otherThreadInfo
operator|.
name|getThreadId
argument_list|()
operator|==
name|threadInfo
operator|.
name|getLockOwnerId
argument_list|()
condition|)
block|{
name|blockingThreadInfo
operator|=
name|otherThreadInfo
expr_stmt|;
break|break;
block|}
block|}
name|onBlockDetected
argument_list|(
name|threadInfo
argument_list|,
name|blockingThreadInfo
argument_list|)
expr_stmt|;
block|}
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|getBlockDetectionIntervalInMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|blockDetectionThread
operator|.
name|setName
argument_list|(
name|currentThreadName
operator|+
literal|"[LongGCDisruption][blockDetection]"
argument_list|)
expr_stmt|;
name|blockDetectionThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|stopBlockDetection
argument_list|()
expr_stmt|;
comment|// resume threads if failed
name|resumeThreads
argument_list|(
name|suspendedThreads
argument_list|)
expr_stmt|;
name|suspendedThreads
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't disrupt twice, call stopDisrupting() first"
argument_list|)
throw|;
block|}
block|}
DECL|method|isDisruptedNodeThread
specifier|public
name|boolean
name|isDisruptedNodeThread
parameter_list|(
name|String
name|threadName
parameter_list|)
block|{
return|return
name|threadName
operator|.
name|contains
argument_list|(
literal|"["
operator|+
name|disruptedNode
operator|+
literal|"]"
argument_list|)
return|;
block|}
DECL|method|stackTrace
specifier|private
name|String
name|stackTrace
parameter_list|(
name|StackTraceElement
index|[]
name|stackTraceElements
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|stackTraceElements
argument_list|)
operator|.
name|map
argument_list|(
name|Object
operator|::
name|toString
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|stopDisrupting
specifier|public
specifier|synchronized
name|void
name|stopDisrupting
parameter_list|()
block|{
name|stopBlockDetection
argument_list|()
expr_stmt|;
if|if
condition|(
name|suspendedThreads
operator|!=
literal|null
condition|)
block|{
name|resumeThreads
argument_list|(
name|suspendedThreads
argument_list|)
expr_stmt|;
name|suspendedThreads
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|stopBlockDetection
specifier|private
name|void
name|stopBlockDetection
parameter_list|()
block|{
if|if
condition|(
name|blockDetectionThread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|blockDetectionThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
comment|// best effort
name|blockDetectionThread
operator|.
name|join
argument_list|(
name|getSuspendingTimeoutInMillis
argument_list|()
argument_list|)
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
name|blockDetectionThread
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeAndEnsureHealthy
specifier|public
name|void
name|removeAndEnsureHealthy
parameter_list|(
name|InternalTestCluster
name|cluster
parameter_list|)
block|{
name|removeFromCluster
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|ensureNodeCount
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expectedTimeToHeal
specifier|public
name|TimeValue
name|expectedTimeToHeal
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**      * resolves all threads belonging to given node and suspends them if their current stack trace      * is "safe". Threads are added to nodeThreads if suspended.      *      * returns true if some live threads were found. The caller is expected to call this method      * until no more "live" are found.      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// suspends/resumes threads intentionally
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"suspends/resumes threads intentionally"
argument_list|)
DECL|method|suspendThreads
specifier|protected
name|boolean
name|suspendThreads
parameter_list|(
name|Set
argument_list|<
name|Thread
argument_list|>
name|nodeThreads
parameter_list|)
block|{
name|Thread
index|[]
name|allThreads
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|allThreads
operator|==
literal|null
condition|)
block|{
name|allThreads
operator|=
operator|new
name|Thread
index|[
name|Thread
operator|.
name|activeCount
argument_list|()
index|]
expr_stmt|;
if|if
condition|(
name|Thread
operator|.
name|enumerate
argument_list|(
name|allThreads
argument_list|)
operator|>
name|allThreads
operator|.
name|length
condition|)
block|{
comment|// we didn't make enough space, retry
name|allThreads
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|boolean
name|liveThreadsFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Thread
name|thread
range|:
name|allThreads
control|)
block|{
if|if
condition|(
name|thread
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|String
name|threadName
init|=
name|thread
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|isDisruptedNodeThread
argument_list|(
name|threadName
argument_list|)
condition|)
block|{
if|if
condition|(
name|thread
operator|.
name|isAlive
argument_list|()
operator|&&
name|nodeThreads
operator|.
name|add
argument_list|(
name|thread
argument_list|)
condition|)
block|{
name|liveThreadsFound
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"suspending thread [{}]"
argument_list|,
name|threadName
argument_list|)
expr_stmt|;
comment|// we assume it is not safe to suspend the thread
name|boolean
name|safe
init|=
literal|false
decl_stmt|;
try|try
block|{
comment|/*                          * At the bottom of this try-block we will know whether or not it is safe to suspend the thread; we start by                          * assuming that it is safe.                          */
name|boolean
name|definitelySafe
init|=
literal|true
decl_stmt|;
name|thread
operator|.
name|suspend
argument_list|()
expr_stmt|;
comment|// double check the thread is not in a shared resource like logging; if so, let it go and come back
name|safe
label|:
for|for
control|(
name|StackTraceElement
name|stackElement
range|:
name|thread
operator|.
name|getStackTrace
argument_list|()
control|)
block|{
name|String
name|className
init|=
name|stackElement
operator|.
name|getClassName
argument_list|()
decl_stmt|;
for|for
control|(
name|Pattern
name|unsafePattern
range|:
name|getUnsafeClasses
argument_list|()
control|)
block|{
if|if
condition|(
name|unsafePattern
operator|.
name|matcher
argument_list|(
name|className
argument_list|)
operator|.
name|find
argument_list|()
condition|)
block|{
comment|// it is definitely not safe to suspend the thread
name|definitelySafe
operator|=
literal|false
expr_stmt|;
break|break
name|safe
break|;
block|}
block|}
block|}
name|safe
operator|=
name|definitelySafe
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|safe
condition|)
block|{
comment|/*                              * Do not log before resuming as we might be interrupted while logging in which case we will throw an                              * interrupted exception and never resume the suspended thread that is in a critical section. Also, logging                              * before resuming makes for confusing log messages if we never hit the resume.                              */
name|thread
operator|.
name|resume
argument_list|()
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"resumed thread [{}] as it is in a critical section"
argument_list|,
name|threadName
argument_list|)
expr_stmt|;
name|nodeThreads
operator|.
name|remove
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|liveThreadsFound
return|;
block|}
comment|// for testing
DECL|method|getUnsafeClasses
specifier|protected
name|Pattern
index|[]
name|getUnsafeClasses
parameter_list|()
block|{
return|return
name|unsafeClasses
return|;
block|}
comment|// for testing
DECL|method|getSuspendingTimeoutInMillis
specifier|protected
name|long
name|getSuspendingTimeoutInMillis
parameter_list|()
block|{
return|return
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
operator|.
name|getMillis
argument_list|()
return|;
block|}
DECL|method|isBlockDetectionSupported
specifier|public
name|boolean
name|isBlockDetectionSupported
parameter_list|()
block|{
return|return
name|threadBean
operator|.
name|isObjectMonitorUsageSupported
argument_list|()
operator|&&
name|threadBean
operator|.
name|isSynchronizerUsageSupported
argument_list|()
return|;
block|}
comment|// for testing
DECL|method|getBlockDetectionIntervalInMillis
specifier|protected
name|long
name|getBlockDetectionIntervalInMillis
parameter_list|()
block|{
return|return
literal|3000L
return|;
block|}
comment|// for testing
DECL|method|onBlockDetected
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
name|String
name|blockedThreadStackTrace
init|=
name|stackTrace
argument_list|(
name|blockedThread
operator|.
name|getStackTrace
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|blockingThreadStackTrace
init|=
name|blockingThread
operator|!=
literal|null
condition|?
name|stackTrace
argument_list|(
name|blockingThread
operator|.
name|getStackTrace
argument_list|()
argument_list|)
else|:
literal|"not available"
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Thread ["
operator|+
name|blockedThread
operator|.
name|getThreadName
argument_list|()
operator|+
literal|"] is blocked waiting on the resource ["
operator|+
name|blockedThread
operator|.
name|getLockInfo
argument_list|()
operator|+
literal|"] held by the suspended thread ["
operator|+
name|blockedThread
operator|.
name|getLockOwnerName
argument_list|()
operator|+
literal|"] of the disrupted node ["
operator|+
name|disruptedNode
operator|+
literal|"].\n"
operator|+
literal|"Please add this occurrence to the unsafeClasses list in ["
operator|+
name|LongGCDisruption
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"].\n"
operator|+
literal|"Stack trace of blocked thread: "
operator|+
name|blockedThreadStackTrace
operator|+
literal|"\n"
operator|+
literal|"Stack trace of blocking thread: "
operator|+
name|blockingThreadStackTrace
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
comment|// suspends/resumes threads intentionally
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"suspends/resumes threads intentionally"
argument_list|)
DECL|method|resumeThreads
specifier|protected
name|void
name|resumeThreads
parameter_list|(
name|Set
argument_list|<
name|Thread
argument_list|>
name|threads
parameter_list|)
block|{
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|resume
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

