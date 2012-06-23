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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|EsExecutors
specifier|public
class|class
name|EsExecutors
block|{
DECL|method|newScalingExecutorService
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newScalingExecutorService
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
DECL|method|newBlockingExecutorService
specifier|public
specifier|static
name|EsThreadPoolExecutor
name|newBlockingExecutorService
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
parameter_list|,
name|int
name|capacity
parameter_list|,
name|long
name|waitTime
parameter_list|,
name|TimeUnit
name|waitTimeUnit
parameter_list|)
block|{
name|ExecutorBlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|queue
init|=
operator|new
name|ExecutorBlockingQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|(
name|capacity
argument_list|)
decl_stmt|;
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
name|TimedBlockingPolicy
argument_list|(
name|waitTimeUnit
operator|.
name|toMillis
argument_list|(
name|waitTime
argument_list|)
argument_list|)
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
name|namePrefix
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
comment|/**      * A priority based thread factory, for all Thread priority constants:      *<tt>Thread.MIN_PRIORITY, Thread.NORM_PRIORITY, Thread.MAX_PRIORITY</tt>;      *<p/>      * This factory is used instead of Executers.DefaultThreadFactory to allow      * manipulation of priority and thread owner name.      *      * @param namePrefix a name prefix for this thread      * @return a thread factory based on given priority.      */
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
specifier|final
name|ThreadFactory
name|f
init|=
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|defaultThreadFactory
argument_list|()
decl_stmt|;
specifier|final
name|String
name|o
init|=
name|namePrefix
operator|+
literal|"-"
decl_stmt|;
return|return
operator|new
name|ThreadFactory
argument_list|()
block|{
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
name|f
operator|.
name|newThread
argument_list|(
name|r
argument_list|)
decl_stmt|;
comment|/*                  * Thread name: owner-pool-N-thread-M, where N is the sequence                  * number of this factory, and M is the sequence number of the                  * thread created by this factory.                  */
name|t
operator|.
name|setName
argument_list|(
name|o
operator|+
name|t
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|/* override default definition t.setDaemon(false); */
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
return|;
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
operator|!
name|tryTransfer
argument_list|(
name|e
argument_list|)
condition|)
block|{
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
DECL|class|ExecutorBlockingQueue
specifier|static
class|class
name|ExecutorBlockingQueue
parameter_list|<
name|E
parameter_list|>
extends|extends
name|ArrayBlockingQueue
argument_list|<
name|E
argument_list|>
block|{
DECL|field|executor
name|ThreadPoolExecutor
name|executor
decl_stmt|;
DECL|method|ExecutorBlockingQueue
name|ExecutorBlockingQueue
parameter_list|(
name|int
name|capacity
parameter_list|)
block|{
name|super
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|offer
specifier|public
name|boolean
name|offer
parameter_list|(
name|E
name|o
parameter_list|)
block|{
name|int
name|allWorkingThreads
init|=
name|executor
operator|.
name|getActiveCount
argument_list|()
operator|+
name|super
operator|.
name|size
argument_list|()
decl_stmt|;
return|return
name|allWorkingThreads
operator|<
name|executor
operator|.
name|getPoolSize
argument_list|()
operator|&&
name|super
operator|.
name|offer
argument_list|(
name|o
argument_list|)
return|;
block|}
block|}
comment|/**      * A handler for rejected tasks that adds the specified element to this queue,      * waiting if necessary for space to become available.      */
DECL|class|ForceQueuePolicy
specifier|static
class|class
name|ForceQueuePolicy
implements|implements
name|RejectedExecutionHandler
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
block|}
comment|/**      * A handler for rejected tasks that inserts the specified element into this      * queue, waiting if necessary up to the specified wait time for space to become      * available.      */
DECL|class|TimedBlockingPolicy
specifier|static
class|class
name|TimedBlockingPolicy
implements|implements
name|RejectedExecutionHandler
block|{
DECL|field|waitTime
specifier|private
specifier|final
name|long
name|waitTime
decl_stmt|;
comment|/**          * @param waitTime wait time in milliseconds for space to become available.          */
DECL|method|TimedBlockingPolicy
specifier|public
name|TimedBlockingPolicy
parameter_list|(
name|long
name|waitTime
parameter_list|)
block|{
name|this
operator|.
name|waitTime
operator|=
name|waitTime
expr_stmt|;
block|}
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
name|boolean
name|successful
init|=
name|executor
operator|.
name|getQueue
argument_list|()
operator|.
name|offer
argument_list|(
name|r
argument_list|,
name|waitTime
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|successful
condition|)
throw|throw
operator|new
name|EsRejectedExecutionException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EsRejectedExecutionException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

