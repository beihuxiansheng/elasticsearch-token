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
name|lang
operator|.
name|IllegalStateException
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
comment|/**  * An extension to thread pool executor, allowing (in the future) to add specific additional stats to it.  */
end_comment

begin_class
DECL|class|EsThreadPoolExecutor
specifier|public
class|class
name|EsThreadPoolExecutor
extends|extends
name|ThreadPoolExecutor
block|{
DECL|field|listener
specifier|private
specifier|volatile
name|ShutdownListener
name|listener
decl_stmt|;
DECL|field|monitor
specifier|private
specifier|final
name|Object
name|monitor
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|EsThreadPoolExecutor
name|EsThreadPoolExecutor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|int
name|maximumPoolSize
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|)
block|{
name|this
argument_list|(
name|corePoolSize
argument_list|,
name|maximumPoolSize
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|,
name|threadFactory
argument_list|,
operator|new
name|EsAbortPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|EsThreadPoolExecutor
name|EsThreadPoolExecutor
parameter_list|(
name|int
name|corePoolSize
parameter_list|,
name|int
name|maximumPoolSize
parameter_list|,
name|long
name|keepAliveTime
parameter_list|,
name|TimeUnit
name|unit
parameter_list|,
name|BlockingQueue
argument_list|<
name|Runnable
argument_list|>
name|workQueue
parameter_list|,
name|ThreadFactory
name|threadFactory
parameter_list|,
name|XRejectedExecutionHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|(
name|corePoolSize
argument_list|,
name|maximumPoolSize
argument_list|,
name|keepAliveTime
argument_list|,
name|unit
argument_list|,
name|workQueue
argument_list|,
name|threadFactory
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|(
name|ShutdownListener
name|listener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|monitor
init|)
block|{
if|if
condition|(
name|this
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Shutdown was already called on this thread pool"
argument_list|)
throw|;
block|}
if|if
condition|(
name|isTerminated
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onTerminated
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
block|}
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|terminated
specifier|protected
specifier|synchronized
name|void
name|terminated
parameter_list|()
block|{
name|super
operator|.
name|terminated
argument_list|()
expr_stmt|;
synchronized|synchronized
init|(
name|monitor
init|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|listener
operator|.
name|onTerminated
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|listener
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|interface|ShutdownListener
specifier|public
specifier|static
interface|interface
name|ShutdownListener
block|{
DECL|method|onTerminated
specifier|public
name|void
name|onTerminated
parameter_list|()
function_decl|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|command
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|execute
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EsRejectedExecutionException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|command
operator|instanceof
name|AbstractRunnable
condition|)
block|{
comment|// If we are an abstract runnable we can handle the rejection
comment|// directly and don't need to rethrow it.
try|try
block|{
operator|(
operator|(
name|AbstractRunnable
operator|)
name|command
operator|)
operator|.
name|onRejection
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
operator|(
operator|(
name|AbstractRunnable
operator|)
name|command
operator|)
operator|.
name|onAfter
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

