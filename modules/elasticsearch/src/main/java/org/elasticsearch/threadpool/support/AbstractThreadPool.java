begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.threadpool.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|FutureListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|component
operator|.
name|AbstractComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|AbstractThreadPool
specifier|public
specifier|abstract
class|class
name|AbstractThreadPool
extends|extends
name|AbstractComponent
implements|implements
name|ThreadPool
block|{
DECL|field|started
specifier|protected
specifier|volatile
name|boolean
name|started
decl_stmt|;
DECL|field|executorService
specifier|protected
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|scheduledExecutorService
specifier|protected
name|ScheduledExecutorService
name|scheduledExecutorService
decl_stmt|;
DECL|method|AbstractThreadPool
specifier|protected
name|AbstractThreadPool
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|getType
specifier|public
specifier|abstract
name|String
name|getType
parameter_list|()
function_decl|;
DECL|method|isStarted
annotation|@
name|Override
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|started
return|;
block|}
DECL|method|schedule
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|schedule
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|scheduledExecutorService
operator|.
name|schedule
argument_list|(
name|command
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
return|;
block|}
DECL|method|schedule
annotation|@
name|Override
specifier|public
parameter_list|<
name|V
parameter_list|>
name|ScheduledFuture
argument_list|<
name|V
argument_list|>
name|schedule
parameter_list|(
name|Callable
argument_list|<
name|V
argument_list|>
name|callable
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|scheduledExecutorService
operator|.
name|schedule
argument_list|(
name|callable
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
return|;
block|}
DECL|method|scheduleAtFixedRate
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleAtFixedRate
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|long
name|initialDelay
parameter_list|,
name|long
name|period
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|scheduledExecutorService
operator|.
name|scheduleAtFixedRate
argument_list|(
name|command
argument_list|,
name|initialDelay
argument_list|,
name|period
argument_list|,
name|unit
argument_list|)
return|;
block|}
DECL|method|scheduleWithFixedDelay
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleWithFixedDelay
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|long
name|initialDelay
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|scheduledExecutorService
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|command
argument_list|,
name|initialDelay
argument_list|,
name|delay
argument_list|,
name|unit
argument_list|)
return|;
block|}
DECL|method|shutdown
annotation|@
name|Override
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|started
operator|=
literal|false
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Shutting down {} thread pool"
argument_list|,
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|scheduledExecutorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|shutdownNow
annotation|@
name|Override
specifier|public
name|void
name|shutdownNow
parameter_list|()
block|{
name|started
operator|=
literal|false
expr_stmt|;
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
name|scheduledExecutorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
DECL|method|awaitTermination
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
name|boolean
name|result
init|=
name|executorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
decl_stmt|;
name|result
operator|&=
name|scheduledExecutorService
operator|.
name|awaitTermination
argument_list|(
name|timeout
argument_list|,
name|unit
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|submit
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Future
argument_list|<
name|T
argument_list|>
name|submit
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|task
parameter_list|)
block|{
return|return
name|executorService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
return|;
block|}
DECL|method|submit
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Future
argument_list|<
name|T
argument_list|>
name|submit
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|task
parameter_list|,
name|FutureListener
argument_list|<
name|T
argument_list|>
name|listener
parameter_list|)
block|{
return|return
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|FutureCallable
argument_list|<
name|T
argument_list|>
argument_list|(
name|task
argument_list|,
name|listener
argument_list|)
argument_list|)
return|;
block|}
DECL|method|submit
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Future
argument_list|<
name|T
argument_list|>
name|submit
parameter_list|(
name|Runnable
name|task
parameter_list|,
name|T
name|result
parameter_list|)
block|{
return|return
name|executorService
operator|.
name|submit
argument_list|(
name|task
argument_list|,
name|result
argument_list|)
return|;
block|}
DECL|method|submit
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|Future
argument_list|<
name|T
argument_list|>
name|submit
parameter_list|(
name|Runnable
name|task
parameter_list|,
name|T
name|result
parameter_list|,
name|FutureListener
argument_list|<
name|T
argument_list|>
name|listener
parameter_list|)
block|{
return|return
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|FutureRunnable
argument_list|<
name|T
argument_list|>
argument_list|(
name|task
argument_list|,
name|result
argument_list|,
name|listener
argument_list|)
argument_list|,
name|result
argument_list|)
return|;
block|}
DECL|method|submit
annotation|@
name|Override
specifier|public
name|Future
argument_list|<
name|?
argument_list|>
name|submit
parameter_list|(
name|Runnable
name|task
parameter_list|)
block|{
return|return
name|executorService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
return|;
block|}
DECL|method|submit
annotation|@
name|Override
specifier|public
name|Future
argument_list|<
name|?
argument_list|>
name|submit
parameter_list|(
name|Runnable
name|task
parameter_list|,
name|FutureListener
argument_list|<
name|?
argument_list|>
name|listener
parameter_list|)
block|{
return|return
name|executorService
operator|.
name|submit
argument_list|(
operator|new
name|FutureRunnable
argument_list|(
name|task
argument_list|,
literal|null
argument_list|,
name|listener
argument_list|)
argument_list|)
return|;
block|}
DECL|method|schedule
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|schedule
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|TimeValue
name|delay
parameter_list|)
block|{
return|return
name|schedule
argument_list|(
name|command
argument_list|,
name|delay
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
DECL|method|scheduleWithFixedDelay
annotation|@
name|Override
specifier|public
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleWithFixedDelay
parameter_list|(
name|Runnable
name|command
parameter_list|,
name|TimeValue
name|interval
parameter_list|)
block|{
return|return
name|scheduleWithFixedDelay
argument_list|(
name|command
argument_list|,
name|interval
operator|.
name|millis
argument_list|()
argument_list|,
name|interval
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
DECL|method|execute
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
name|executorService
operator|.
name|execute
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
DECL|class|FutureCallable
specifier|protected
specifier|static
class|class
name|FutureCallable
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Callable
argument_list|<
name|T
argument_list|>
block|{
DECL|field|callable
specifier|private
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|FutureListener
argument_list|<
name|T
argument_list|>
name|listener
decl_stmt|;
DECL|method|FutureCallable
specifier|public
name|FutureCallable
parameter_list|(
name|Callable
argument_list|<
name|T
argument_list|>
name|callable
parameter_list|,
name|FutureListener
argument_list|<
name|T
argument_list|>
name|listener
parameter_list|)
block|{
name|this
operator|.
name|callable
operator|=
name|callable
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|call
annotation|@
name|Override
specifier|public
name|T
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|T
name|result
init|=
name|callable
operator|.
name|call
argument_list|()
decl_stmt|;
name|listener
operator|.
name|onResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
DECL|class|FutureRunnable
specifier|protected
specifier|static
class|class
name|FutureRunnable
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Runnable
block|{
DECL|field|runnable
specifier|private
specifier|final
name|Runnable
name|runnable
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|T
name|result
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|FutureListener
argument_list|<
name|T
argument_list|>
name|listener
decl_stmt|;
DECL|method|FutureRunnable
specifier|private
name|FutureRunnable
parameter_list|(
name|Runnable
name|runnable
parameter_list|,
name|T
name|result
parameter_list|,
name|FutureListener
argument_list|<
name|T
argument_list|>
name|listener
parameter_list|)
block|{
name|this
operator|.
name|runnable
operator|=
name|runnable
expr_stmt|;
name|this
operator|.
name|result
operator|=
name|result
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
name|listener
operator|.
name|onResult
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

