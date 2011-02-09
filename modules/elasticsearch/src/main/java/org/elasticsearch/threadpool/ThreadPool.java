begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|unit
operator|.
name|TimeValue
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
name|ScheduledFuture
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|ThreadPool
specifier|public
interface|interface
name|ThreadPool
extends|extends
name|Executor
block|{
DECL|method|info
name|ThreadPoolInfo
name|info
parameter_list|()
function_decl|;
DECL|method|stats
name|ThreadPoolStats
name|stats
parameter_list|()
function_decl|;
comment|/**      * The minimum number of threads in the thread pool.      */
DECL|method|getMinThreads
name|int
name|getMinThreads
parameter_list|()
function_decl|;
comment|/**      * The maximum number of threads in the thread pool.      */
DECL|method|getMaxThreads
name|int
name|getMaxThreads
parameter_list|()
function_decl|;
comment|/**      * The size of scheduler threads.      */
DECL|method|getSchedulerThreads
name|int
name|getSchedulerThreads
parameter_list|()
function_decl|;
comment|/**      * Returns the current number of threads in the pool.      *      * @return the number of threads      */
DECL|method|getPoolSize
name|int
name|getPoolSize
parameter_list|()
function_decl|;
comment|/**      * Returns the approximate number of threads that are actively      * executing tasks.      *      * @return the number of threads      */
DECL|method|getActiveCount
name|int
name|getActiveCount
parameter_list|()
function_decl|;
comment|/**      * The size of the scheduler thread pool.      */
DECL|method|getSchedulerPoolSize
name|int
name|getSchedulerPoolSize
parameter_list|()
function_decl|;
comment|/**      * The approximate number of threads that are actively executing scheduled      * tasks.      */
DECL|method|getSchedulerActiveCount
name|int
name|getSchedulerActiveCount
parameter_list|()
function_decl|;
comment|/**      * Returns<tt>true</tt> if the thread pool has started.      */
DECL|method|isStarted
name|boolean
name|isStarted
parameter_list|()
function_decl|;
comment|/**      * Returns a cached executor that will always allocate threads.      */
DECL|method|cached
name|Executor
name|cached
parameter_list|()
function_decl|;
DECL|method|shutdownNow
name|void
name|shutdownNow
parameter_list|()
function_decl|;
comment|/**      * Initiates an orderly shutdown in which previously submitted      * tasks are executed, but no new tasks will be accepted.      * Invocation has no additional effect if already shut down.      */
DECL|method|shutdown
name|void
name|shutdown
parameter_list|()
function_decl|;
DECL|method|awaitTermination
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
function_decl|;
DECL|method|execute
name|void
name|execute
parameter_list|(
name|Runnable
name|command
parameter_list|)
function_decl|;
comment|/**      * Scheduled a task. Note, when using {@link ExecutionType#DEFAULT}, make sure to not      * execute long running blocking tasks.      */
DECL|method|schedule
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
parameter_list|,
name|ExecutionType
name|executionType
parameter_list|)
function_decl|;
comment|/**      * Schedule a repeating task with a task that is very short lived.      */
DECL|method|scheduleWithFixedDelay
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
function_decl|;
comment|/**      * Returns an estimated current time in milliseconds.      */
DECL|method|estimatedCurrentTimeInMillis
name|long
name|estimatedCurrentTimeInMillis
parameter_list|()
function_decl|;
DECL|enum|ExecutionType
specifier|static
enum|enum
name|ExecutionType
block|{
DECL|enum constant|DEFAULT
name|DEFAULT
block|,
DECL|enum constant|THREADED
name|THREADED
block|}
block|}
end_interface

end_unit

