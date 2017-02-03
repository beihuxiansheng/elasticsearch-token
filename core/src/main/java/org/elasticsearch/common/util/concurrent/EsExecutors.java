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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Setting
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
name|Setting
operator|.
name|Property
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
name|node
operator|.
name|Node
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
name|AbstractExecutorService
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
name|BlockingQueue
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
name|ExecutorService
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
name|LinkedTransferQueue
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|AtomicInteger
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

begin_class
DECL|class|EsExecutors
specifier|public
class|class
name|EsExecutors
block|{
comment|/**      * Settings key to manually set the number of available processors.      * This is used to adjust thread pools sizes etc. per node.      */
DECL|field|PROCESSORS_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|PROCESSORS_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"processors"
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
argument_list|,
literal|1
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|/**      * Returns the number of available processors. Defaults to      * {@link Runtime#availableProcessors()} but can be overridden by passing a {@link Settings}      * instance with the key "processors" set to the desired value.      *      * @param settings a {@link Settings} instance from which to derive the available processors      * @return the number of available processors      */
DECL|method|numberOfProcessors
specifier|public
specifier|static
name|int
name|numberOfProcessors
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|PROCESSORS_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|method|newSinglePrioritizing
specifier|public
specifier|static
name|PrioritizedEsThreadPoolExecutor
name|newSinglePrioritizing
parameter_list|(
name|String
name|name
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|ThreadContext
name|contextHolder
parameter_list|)
block|{
return|return
operator|new
name|PrioritizedEsThreadPoolExecutor
argument_list|(
name|name
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0L
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|threadFactory
argument_list|,
name|contextHolder
argument_list|)
return|;
block|}
DECL|method|newScaling
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newScaling
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|ThreadContext
name|contextHolder
parameter_list|)
block|{
name|ExecutorScalingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ExecutorScalingQueue
argument_list|<>
argument_list|()
decl_stmt|;
name|EsThreadPoolExecutor
name|executor
init|=
operator|new
name|EsThreadPoolExecutor
argument_list|(
name|name
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|queue
argument_list|,
name|threadFactory
argument_list|,
operator|new
name|ForceQueuePolicy
argument_list|()
argument_list|,
name|contextHolder
argument_list|)
decl_stmt|;
name|queue
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
return|return
name|executor
return|;
block|}
DECL|method|newFixed
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newFixed
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|size
parameter_list|,
name|int
name|queueCapacity
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|ThreadContext
name|contextHolder
parameter_list|)
block|{
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
decl_stmt|;
if|if
condition|(
name|queueCapacity
operator|<
literal|0
condition|)
block|{
name|queue
operator|=
name|ConcurrentCollections
operator|.
name|newBlockingQueue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|=
operator|new
name|SizeBlockingQueue
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
name|queueCapacity
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EsThreadPoolExecutor
argument_list|(
name|name
argument_list|,
name|size
argument_list|,
name|size
argument_list|,
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|queue
argument_list|,
name|threadFactory
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|,
name|contextHolder
argument_list|)
return|;
block|}
DECL|field|DIRECT_EXECUTOR_SERVICE
specifier|private
specifier|static
specifier|final
name|ExecutorService
name|DIRECT_EXECUTOR_SERVICE
init|=
operator|new
name|AbstractExecutorService
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Runnable
argument_list|>
name|shutdownNow
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isShutdown
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isTerminated
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|awaitTermination
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|command
parameter_list|)
block|{
name|command
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Returns an {@link ExecutorService} that executes submitted tasks on the current thread. This executor service does not support being      * shutdown.      *      * @return an {@link ExecutorService} that executes submitted tasks on the current thread      */
DECL|method|newDirectExecutorService
specifier|public
specifier|static
name|ExecutorService
name|newDirectExecutorService
parameter_list|()
block|{
return|return
name|DIRECT_EXECUTOR_SERVICE
return|;
block|}
DECL|method|threadName
specifier|public
specifier|static
name|String
name|threadName
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
block|{
name|String
name|namePrefix
init|=
name|Arrays
operator|.
name|stream
argument_list|(
name|names
argument_list|)
operator|.
name|filter
argument_list|(
name|name
lambda|->
name|name
operator|!=
literal|null
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|joining
argument_list|(
literal|"."
argument_list|,
literal|"["
argument_list|,
literal|"]"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|threadName
argument_list|(
name|settings
argument_list|,
name|namePrefix
argument_list|)
return|;
block|}
DECL|method|threadName
specifier|public
specifier|static
name|String
name|threadName
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|namePrefix
parameter_list|)
block|{
if|if
condition|(
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
return|return
name|threadName
argument_list|(
name|Node
operator|.
name|NODE_NAME_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|,
name|namePrefix
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|threadName
argument_list|(
literal|""
argument_list|,
name|namePrefix
argument_list|)
return|;
block|}
block|}
DECL|method|threadName
specifier|public
specifier|static
name|String
name|threadName
parameter_list|(
specifier|final
name|String
name|nodeName
parameter_list|,
specifier|final
name|String
name|namePrefix
parameter_list|)
block|{
return|return
literal|"elasticsearch"
operator|+
operator|(
name|nodeName
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|"["
operator|)
operator|+
name|nodeName
operator|+
operator|(
name|nodeName
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|"]"
operator|)
operator|+
literal|"["
operator|+
name|namePrefix
operator|+
literal|"]"
return|;
block|}
DECL|method|daemonThreadFactory
specifier|public
specifier|static
name|ThreadFactory
name|daemonThreadFactory
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|namePrefix
parameter_list|)
block|{
return|return
name|daemonThreadFactory
argument_list|(
name|threadName
argument_list|(
name|settings
argument_list|,
name|namePrefix
argument_list|)
argument_list|)
return|;
block|}
DECL|method|daemonThreadFactory
specifier|public
specifier|static
name|ThreadFactory
name|daemonThreadFactory
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
modifier|...
name|names
parameter_list|)
block|{
return|return
name|daemonThreadFactory
argument_list|(
name|threadName
argument_list|(
name|settings
argument_list|,
name|names
argument_list|)
argument_list|)
return|;
block|}
DECL|method|daemonThreadFactory
specifier|public
specifier|static
name|ThreadFactory
name|daemonThreadFactory
parameter_list|(
name|String
name|namePrefix
parameter_list|)
block|{
return|return
operator|new
name|EsThreadFactory
argument_list|(
name|namePrefix
argument_list|)
return|;
block|}
DECL|class|EsThreadFactory
specifier|static
class|class
name|EsThreadFactory
implements|implements
name|ThreadFactory
block|{
DECL|field|group
specifier|final
name|ThreadGroup
name|group
decl_stmt|;
DECL|field|threadNumber
specifier|final
name|AtomicInteger
name|threadNumber
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|namePrefix
specifier|final
name|String
name|namePrefix
decl_stmt|;
DECL|method|EsThreadFactory
name|EsThreadFactory
parameter_list|(
name|String
name|namePrefix
parameter_list|)
block|{
name|this
operator|.
name|namePrefix
operator|=
name|namePrefix
expr_stmt|;
name|SecurityManager
name|s
init|=
name|System
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
name|group
operator|=
operator|(
name|s
operator|!=
literal|null
operator|)
condition|?
name|s
operator|.
name|getThreadGroup
argument_list|()
else|:
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getThreadGroup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newThread
specifier|public
name|Thread
name|newThread
parameter_list|(
name|Runnable
name|r
parameter_list|)
block|{
name|Thread
name|t
init|=
operator|new
name|Thread
argument_list|(
name|group
argument_list|,
name|r
argument_list|,
name|namePrefix
operator|+
literal|"[T#"
operator|+
name|threadNumber
operator|.
name|getAndIncrement
argument_list|()
operator|+
literal|"]"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|t
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
comment|/**      * Cannot instantiate.      */
DECL|method|EsExecutors
specifier|private
name|EsExecutors
parameter_list|()
block|{     }
DECL|class|ExecutorScalingQueue
specifier|static
class|class
name|ExecutorScalingQueue
parameter_list|<
name|E
parameter_list|>
extends|extends
name|LinkedTransferQueue
argument_list|<
name|E
argument_list|>
block|{
DECL|field|executor
name|ThreadPoolExecutor
name|executor
decl_stmt|;
DECL|method|ExecutorScalingQueue
name|ExecutorScalingQueue
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|offer
specifier|public
name|boolean
name|offer
parameter_list|(
name|E
name|e
parameter_list|)
block|{
comment|// first try to transfer to a waiting worker thread
if|if
condition|(
operator|!
name|tryTransfer
argument_list|(
name|e
argument_list|)
condition|)
block|{
comment|// check if there might be spare capacity in the thread
comment|// pool executor
name|int
name|left
init|=
name|executor
operator|.
name|getMaximumPoolSize
argument_list|()
operator|-
name|executor
operator|.
name|getCorePoolSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|left
operator|>
literal|0
condition|)
block|{
comment|// reject queuing the task to force the thread pool
comment|// executor to add a worker if it can; combined
comment|// with ForceQueuePolicy, this causes the thread
comment|// pool to always scale up to max pool size and we
comment|// only queue when there is no spare capacity
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|offer
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
comment|/**      * A handler for rejected tasks that adds the specified element to this queue,      * waiting if necessary for space to become available.      */
DECL|class|ForceQueuePolicy
specifier|static
class|class
name|ForceQueuePolicy
implements|implements
name|XRejectedExecutionHandler
block|{
annotation|@
name|Override
DECL|method|rejectedExecution
specifier|public
name|void
name|rejectedExecution
parameter_list|(
name|Runnable
name|r
parameter_list|,
name|ThreadPoolExecutor
name|executor
parameter_list|)
block|{
try|try
block|{
name|executor
operator|.
name|getQueue
argument_list|()
operator|.
name|put
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//should never happen since we never wait
throw|throw
operator|new
name|EsRejectedExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|rejected
specifier|public
name|long
name|rejected
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

