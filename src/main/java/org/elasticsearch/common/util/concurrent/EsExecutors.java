begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|jsr166y
operator|.
name|LinkedTransferQueue
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|*
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|EsExecutors
specifier|public
class|class
name|EsExecutors
block|{
comment|/**      * Returns the number of processors available but at most<tt>32</tt>.      */
DECL|method|boundedNumberOfProcessors
specifier|public
specifier|static
name|int
name|boundedNumberOfProcessors
parameter_list|()
block|{
comment|/* This relates to issues where machines with large number of cores          * ie.>= 48 create too many threads and run into OOM see #3478          * We just use an 32 core upper-bound here to not stress the system          * too much with too many created threads */
return|return
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
return|;
block|}
DECL|method|newSinglePrioritizing
specifier|public
specifier|static
name|PrioritizedEsThreadPoolExecutor
name|newSinglePrioritizing
parameter_list|(
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|PrioritizedEsThreadPoolExecutor
argument_list|(
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
argument_list|)
return|;
block|}
DECL|method|newScaling
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newScaling
parameter_list|(
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
argument_list|<
name|Runnable
argument_list|>
argument_list|()
decl_stmt|;
comment|// we force the execution, since we might run into concurrency issues in offer for ScalingBlockingQueue
name|EsThreadPoolExecutor
name|executor
init|=
operator|new
name|EsThreadPoolExecutor
argument_list|(
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
DECL|method|newCached
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newCached
parameter_list|(
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|EsThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
name|threadFactory
argument_list|)
return|;
block|}
DECL|method|newFixed
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newFixed
parameter_list|(
name|int
name|size
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
return|return
operator|new
name|EsThreadPoolExecutor
argument_list|(
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
argument_list|)
return|;
block|}
DECL|method|newFixed
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newFixed
parameter_list|(
name|int
name|size
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|XRejectedExecutionHandler
name|rejectedExecutionHandler
parameter_list|)
block|{
return|return
operator|new
name|EsThreadPoolExecutor
argument_list|(
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
name|rejectedExecutionHandler
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
name|String
name|name
init|=
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|name
operator|=
literal|"elasticsearch"
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
literal|"elasticsearch["
operator|+
name|name
operator|+
literal|"]"
expr_stmt|;
block|}
return|return
name|name
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
specifier|public
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
specifier|public
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
if|if
condition|(
operator|!
name|tryTransfer
argument_list|(
name|e
argument_list|)
condition|)
block|{
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

