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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|Beta
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
name|Nullable
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
name|locks
operator|.
name|AbstractQueuedSynchronizer
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * An abstract implementation of the {@link com.google.common.util.concurrent.ListenableFuture} interface. This  * class is preferable to {@link java.util.concurrent.FutureTask} for two  * reasons: It implements {@code ListenableFuture}, and it does not implement  * {@code Runnable}. (If you want a {@code Runnable} implementation of {@code  * ListenableFuture}, create a {@link com.google.common.util.concurrent.ListenableFutureTask}, or submit your  * tasks to a {@link com.google.common.util.concurrent.ListeningExecutorService}.)  *<p/>  *<p>This class implements all methods in {@code ListenableFuture}.  * Subclasses should provide a way to set the result of the computation through  * the protected methods {@link #set(Object)} and  * {@link #setException(Throwable)}. Subclasses may also override {@link  * #interruptTask()}, which will be invoked automatically if a call to {@link  * #cancel(boolean) cancel(true)} succeeds in canceling the future.  *<p/>  *<p>{@code AbstractFuture} uses an {@link AbstractQueuedSynchronizer} to deal  * with concurrency issues and guarantee thread safety.  *<p/>  *<p>The state changing methods all return a boolean indicating success or  * failure in changing the future's state.  Valid states are running,  * completed, failed, or cancelled.  *<p/>  *<p>This class uses an {@link com.google.common.util.concurrent.ExecutionList} to guarantee that all registered  * listeners will be executed, either when the future finishes or, for listeners  * that are added after the future completes, immediately.  * {@code Runnable}-{@code Executor} pairs are stored in the execution list but  * are not necessarily executed in the order in which they were added.  (If a  * listener is added after the Future is complete, it will be executed  * immediately, even if earlier listeners have not been executed. Additionally,  * executors need not guarantee FIFO execution, or different listeners may run  * in different executors.)  *  * @author Sven Mawson  * @since 1.0  */
end_comment

begin_comment
comment|// Same as AbstractFuture from Guava, but without the listeners
end_comment

begin_class
DECL|class|BaseFuture
specifier|public
specifier|abstract
class|class
name|BaseFuture
parameter_list|<
name|V
parameter_list|>
implements|implements
name|Future
argument_list|<
name|V
argument_list|>
block|{
comment|/**      * Synchronization control for AbstractFutures.      */
DECL|field|sync
specifier|private
specifier|final
name|Sync
argument_list|<
name|V
argument_list|>
name|sync
init|=
operator|new
name|Sync
argument_list|<
name|V
argument_list|>
argument_list|()
decl_stmt|;
comment|/*     * Improve the documentation of when InterruptedException is thrown. Our     * behavior matches the JDK's, but the JDK's documentation is misleading.     */
comment|/**      * {@inheritDoc}      *<p/>      *<p>The default {@link BaseFuture} implementation throws {@code      * InterruptedException} if the current thread is interrupted before or during      * the call, even if the value is already available.      *      * @throws InterruptedException  if the current thread was interrupted before      *                               or during the call (optional but recommended).      * @throws CancellationException {@inheritDoc}      */
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|TimeoutException
throws|,
name|ExecutionException
block|{
return|return
name|sync
operator|.
name|get
argument_list|(
name|unit
operator|.
name|toNanos
argument_list|(
name|timeout
argument_list|)
argument_list|)
return|;
block|}
comment|/*     * Improve the documentation of when InterruptedException is thrown. Our     * behavior matches the JDK's, but the JDK's documentation is misleading.     */
comment|/**      * {@inheritDoc}      *<p/>      *<p>The default {@link BaseFuture} implementation throws {@code      * InterruptedException} if the current thread is interrupted before or during      * the call, even if the value is already available.      *      * @throws InterruptedException  if the current thread was interrupted before      *                               or during the call (optional but recommended).      * @throws CancellationException {@inheritDoc}      */
annotation|@
name|Override
DECL|method|get
specifier|public
name|V
name|get
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
return|return
name|sync
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isDone
specifier|public
name|boolean
name|isDone
parameter_list|()
block|{
return|return
name|sync
operator|.
name|isDone
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isCancelled
specifier|public
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|sync
operator|.
name|isCancelled
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cancel
specifier|public
name|boolean
name|cancel
parameter_list|(
name|boolean
name|mayInterruptIfRunning
parameter_list|)
block|{
if|if
condition|(
operator|!
name|sync
operator|.
name|cancel
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|done
argument_list|()
expr_stmt|;
if|if
condition|(
name|mayInterruptIfRunning
condition|)
block|{
name|interruptTask
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Subclasses can override this method to implement interruption of the      * future's computation. The method is invoked automatically by a successful      * call to {@link #cancel(boolean) cancel(true)}.      *<p/>      *<p>The default implementation does nothing.      *      * @since 10.0      */
DECL|method|interruptTask
specifier|protected
name|void
name|interruptTask
parameter_list|()
block|{     }
comment|/**      * Subclasses should invoke this method to set the result of the computation      * to {@code value}.  This will set the state of the future to      * {@link BaseFuture.Sync#COMPLETED} and call {@link #done()} if the      * state was successfully changed.      *      * @param value the value that was the result of the task.      * @return true if the state was successfully changed.      */
DECL|method|set
specifier|protected
name|boolean
name|set
parameter_list|(
annotation|@
name|Nullable
name|V
name|value
parameter_list|)
block|{
name|boolean
name|result
init|=
name|sync
operator|.
name|set
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|done
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Subclasses should invoke this method to set the result of the computation      * to an error, {@code throwable}.  This will set the state of the future to      * {@link BaseFuture.Sync#COMPLETED} and call {@link #done()} if the      * state was successfully changed.      *      * @param throwable the exception that the task failed with.      * @return true if the state was successfully changed.      * @throws Error if the throwable was an {@link Error}.      */
DECL|method|setException
specifier|protected
name|boolean
name|setException
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|boolean
name|result
init|=
name|sync
operator|.
name|setException
argument_list|(
name|checkNotNull
argument_list|(
name|throwable
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|done
argument_list|()
expr_stmt|;
block|}
comment|// If it's an Error, we want to make sure it reaches the top of the
comment|// call stack, so we rethrow it.
if|if
condition|(
name|throwable
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|throwable
throw|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Beta
DECL|method|done
specifier|protected
name|void
name|done
parameter_list|()
block|{     }
comment|/**      *<p>Following the contract of {@link AbstractQueuedSynchronizer} we create a      * private subclass to hold the synchronizer.  This synchronizer is used to      * implement the blocking and waiting calls as well as to handle state changes      * in a thread-safe manner.  The current state of the future is held in the      * Sync state, and the lock is released whenever the state changes to either      * {@link #COMPLETED} or {@link #CANCELLED}.      *<p/>      *<p>To avoid races between threads doing release and acquire, we transition      * to the final state in two steps.  One thread will successfully CAS from      * RUNNING to COMPLETING, that thread will then set the result of the      * computation, and only then transition to COMPLETED or CANCELLED.      *<p/>      *<p>We don't use the integer argument passed between acquire methods so we      * pass around a -1 everywhere.      */
DECL|class|Sync
specifier|static
specifier|final
class|class
name|Sync
parameter_list|<
name|V
parameter_list|>
extends|extends
name|AbstractQueuedSynchronizer
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|0L
decl_stmt|;
comment|/* Valid states. */
DECL|field|RUNNING
specifier|static
specifier|final
name|int
name|RUNNING
init|=
literal|0
decl_stmt|;
DECL|field|COMPLETING
specifier|static
specifier|final
name|int
name|COMPLETING
init|=
literal|1
decl_stmt|;
DECL|field|COMPLETED
specifier|static
specifier|final
name|int
name|COMPLETED
init|=
literal|2
decl_stmt|;
DECL|field|CANCELLED
specifier|static
specifier|final
name|int
name|CANCELLED
init|=
literal|4
decl_stmt|;
DECL|field|value
specifier|private
name|V
name|value
decl_stmt|;
DECL|field|exception
specifier|private
name|Throwable
name|exception
decl_stmt|;
comment|/*         * Acquisition succeeds if the future is done, otherwise it fails.         */
annotation|@
name|Override
DECL|method|tryAcquireShared
specifier|protected
name|int
name|tryAcquireShared
parameter_list|(
name|int
name|ignored
parameter_list|)
block|{
if|if
condition|(
name|isDone
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
comment|/*         * We always allow a release to go through, this means the state has been         * successfully changed and the result is available.         */
annotation|@
name|Override
DECL|method|tryReleaseShared
specifier|protected
name|boolean
name|tryReleaseShared
parameter_list|(
name|int
name|finalState
parameter_list|)
block|{
name|setState
argument_list|(
name|finalState
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**          * Blocks until the task is complete or the timeout expires.  Throws a          * {@link TimeoutException} if the timer expires, otherwise behaves like          * {@link #get()}.          */
DECL|method|get
name|V
name|get
parameter_list|(
name|long
name|nanos
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|CancellationException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
comment|// Attempt to acquire the shared lock with a timeout.
if|if
condition|(
operator|!
name|tryAcquireSharedNanos
argument_list|(
operator|-
literal|1
argument_list|,
name|nanos
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|TimeoutException
argument_list|(
literal|"Timeout waiting for task."
argument_list|)
throw|;
block|}
return|return
name|getValue
argument_list|()
return|;
block|}
comment|/**          * Blocks until {@link #complete(Object, Throwable, int)} has been          * successfully called.  Throws a {@link CancellationException} if the task          * was cancelled, or a {@link ExecutionException} if the task completed with          * an error.          */
DECL|method|get
name|V
name|get
parameter_list|()
throws|throws
name|CancellationException
throws|,
name|ExecutionException
throws|,
name|InterruptedException
block|{
comment|// Acquire the shared lock allowing interruption.
name|acquireSharedInterruptibly
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
name|getValue
argument_list|()
return|;
block|}
comment|/**          * Implementation of the actual value retrieval.  Will return the value          * on success, an exception on failure, a cancellation on cancellation, or          * an illegal state if the synchronizer is in an invalid state.          */
DECL|method|getValue
specifier|private
name|V
name|getValue
parameter_list|()
throws|throws
name|CancellationException
throws|,
name|ExecutionException
block|{
name|int
name|state
init|=
name|getState
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|COMPLETED
case|:
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ExecutionException
argument_list|(
name|exception
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|value
return|;
block|}
case|case
name|CANCELLED
case|:
throw|throw
operator|new
name|CancellationException
argument_list|(
literal|"Task was cancelled."
argument_list|)
throw|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Error, synchronizer in invalid state: "
operator|+
name|state
argument_list|)
throw|;
block|}
block|}
comment|/**          * Checks if the state is {@link #COMPLETED} or {@link #CANCELLED}.          */
DECL|method|isDone
name|boolean
name|isDone
parameter_list|()
block|{
return|return
operator|(
name|getState
argument_list|()
operator|&
operator|(
name|COMPLETED
operator||
name|CANCELLED
operator|)
operator|)
operator|!=
literal|0
return|;
block|}
comment|/**          * Checks if the state is {@link #CANCELLED}.          */
DECL|method|isCancelled
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|getState
argument_list|()
operator|==
name|CANCELLED
return|;
block|}
comment|/**          * Transition to the COMPLETED state and set the value.          */
DECL|method|set
name|boolean
name|set
parameter_list|(
annotation|@
name|Nullable
name|V
name|v
parameter_list|)
block|{
return|return
name|complete
argument_list|(
name|v
argument_list|,
literal|null
argument_list|,
name|COMPLETED
argument_list|)
return|;
block|}
comment|/**          * Transition to the COMPLETED state and set the exception.          */
DECL|method|setException
name|boolean
name|setException
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
return|return
name|complete
argument_list|(
literal|null
argument_list|,
name|t
argument_list|,
name|COMPLETED
argument_list|)
return|;
block|}
comment|/**          * Transition to the CANCELLED state.          */
DECL|method|cancel
name|boolean
name|cancel
parameter_list|()
block|{
return|return
name|complete
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|CANCELLED
argument_list|)
return|;
block|}
comment|/**          * Implementation of completing a task.  Either {@code v} or {@code t} will          * be set but not both.  The {@code finalState} is the state to change to          * from {@link #RUNNING}.  If the state is not in the RUNNING state we          * return {@code false}.          *          * @param v          the value to set as the result of the computation.          * @param t          the exception to set as the result of the computation.          * @param finalState the state to transition to.          */
DECL|method|complete
specifier|private
name|boolean
name|complete
parameter_list|(
annotation|@
name|Nullable
name|V
name|v
parameter_list|,
name|Throwable
name|t
parameter_list|,
name|int
name|finalState
parameter_list|)
block|{
if|if
condition|(
name|compareAndSetState
argument_list|(
name|RUNNING
argument_list|,
name|COMPLETING
argument_list|)
condition|)
block|{
name|this
operator|.
name|value
operator|=
name|v
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|t
expr_stmt|;
name|releaseShared
argument_list|(
name|finalState
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// The state was not RUNNING, so there are no valid transitions.
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

