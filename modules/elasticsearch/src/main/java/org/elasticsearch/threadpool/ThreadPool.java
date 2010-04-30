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
name|util
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
name|*
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
block|{
DECL|method|isStarted
name|boolean
name|isStarted
parameter_list|()
function_decl|;
comment|/**      * Attempts to stop all actively executing tasks, halts the      * processing of waiting tasks, and returns a list of the tasks that were      * awaiting execution.      *      *<p>There are no guarantees beyond best-effort attempts to stop      * processing actively executing tasks.  For example, typical      * implementations will cancel via {@link Thread#interrupt}, so any      * task that fails to respond to interrupts may never terminate.      */
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
comment|/**      * Submits a value-returning task for execution and returns a      * Future representing the pending results of the task. The      * Future's<tt>get</tt> method will return the task's result upon      * successful completion.      *      *<p>      * If you would like to immediately block waiting      * for a task, you can use constructions of the form      *<tt>result = exec.submit(aCallable).get();</tt>      *      *<p> Note: The {@link Executors} class includes a set of methods      * that can convert some other common closure-like objects,      * for example, {@link java.security.PrivilegedAction} to      * {@link Callable} form so they can be submitted.      *      * @param task the task to submit      * @return a Future representing pending completion of the task      * @throws RejectedExecutionException if the task cannot be      *                                    scheduled for execution      * @throws NullPointerException       if the task is null      */
DECL|method|submit
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
function_decl|;
comment|/**      * Submits a Runnable task for execution and returns a Future      * representing that task. The Future's<tt>get</tt> method will      * return the given result upon successful completion.      *      * @param task   the task to submit      * @param result the result to return      * @return a Future representing pending completion of the task      * @throws RejectedExecutionException if the task cannot be      *                                    scheduled for execution      * @throws NullPointerException       if the task is null      */
DECL|method|submit
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
function_decl|;
comment|/**      * Submits a Runnable task for execution and returns a Future      * representing that task. The Future's<tt>get</tt> method will      * return<tt>null</tt> upon<em>successful</em> completion.      *      * @param task the task to submit      * @return a Future representing pending completion of the task      * @throws RejectedExecutionException if the task cannot be      *                                    scheduled for execution      * @throws NullPointerException       if the task is null      */
DECL|method|submit
name|Future
argument_list|<
name|?
argument_list|>
name|submit
parameter_list|(
name|Runnable
name|task
parameter_list|)
function_decl|;
DECL|method|submit
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
function_decl|;
DECL|method|submit
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
function_decl|;
DECL|method|submit
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
function_decl|;
comment|/**      * Creates and executes a one-shot action that becomes enabled      * after the given delay.      *      * @param command the task to execute      * @param delay   the time from now to delay execution      * @param unit    the time unit of the delay parameter      * @return a ScheduledFuture representing pending completion of      *         the task and whose<tt>get()</tt> method will return      *<tt>null</tt> upon completion      * @throws java.util.concurrent.RejectedExecutionException      *                              if the task cannot be      *                              scheduled for execution      * @throws NullPointerException if command is null      */
DECL|method|schedule
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
function_decl|;
comment|/**      * Creates and executes a ScheduledFuture that becomes enabled after the      * given delay.      *      * @param callable the function to execute      * @param delay    the time from now to delay execution      * @param unit     the time unit of the delay parameter      * @return a ScheduledFuture that can be used to extract result or cancel      * @throws RejectedExecutionException if the task cannot be      *                                    scheduled for execution      * @throws NullPointerException       if callable is null      */
DECL|method|schedule
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
function_decl|;
comment|/**      * Creates and executes a periodic action that becomes enabled first      * after the given initial delay, and subsequently with the given      * period; that is executions will commence after      *<tt>initialDelay</tt> then<tt>initialDelay+period</tt>, then      *<tt>initialDelay + 2 * period</tt>, and so on.      * If any execution of the task      * encounters an exception, subsequent executions are suppressed.      * Otherwise, the task will only terminate via cancellation or      * termination of the executor.  If any execution of this task      * takes longer than its period, then subsequent executions      * may start late, but will not concurrently execute.      *      * @param command      the task to execute      * @param initialDelay the time to delay first execution      * @param period       the period between successive executions      * @param unit         the time unit of the initialDelay and period parameters      * @return a ScheduledFuture representing pending completion of      *         the task, and whose<tt>get()</tt> method will throw an      *         exception upon cancellation      * @throws RejectedExecutionException if the task cannot be      *                                    scheduled for execution      * @throws NullPointerException       if command is null      * @throws IllegalArgumentException   if period less than or equal to zero      */
DECL|method|scheduleAtFixedRate
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
function_decl|;
comment|/**      * Creates and executes a periodic action that becomes enabled first      * after the given initial delay, and subsequently with the      * given delay between the termination of one execution and the      * commencement of the next.  If any execution of the task      * encounters an exception, subsequent executions are suppressed.      * Otherwise, the task will only terminate via cancellation or      * termination of the executor.      *      * @param command      the task to execute      * @param initialDelay the time to delay first execution      * @param delay        the delay between the termination of one      *                     execution and the commencement of the next      * @param unit         the time unit of the initialDelay and delay parameters      * @return a ScheduledFuture representing pending completion of      *         the task, and whose<tt>get()</tt> method will throw an      *         exception upon cancellation      * @throws RejectedExecutionException if the task cannot be      *                                    scheduled for execution      * @throws NullPointerException       if command is null      * @throws IllegalArgumentException   if delay less than or equal to zero      */
DECL|method|scheduleWithFixedDelay
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
function_decl|;
DECL|method|schedule
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
function_decl|;
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
block|}
end_interface

end_unit

