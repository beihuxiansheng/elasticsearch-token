begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ThreadInterruptedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_comment
comment|/**  * A utility class for multi threaded operation that needs to be cancellable via interrupts. Every cancellable operation should be  * executed via {@link #execute(Interruptable)}, which will capture the executing thread and make sure it is interrupted in the case  * of cancellation.  *  * Cancellation policy: This class does not support external interruption via<code>Thread#interrupt()</code>. Always use #cancel() instead.  */
end_comment

begin_class
DECL|class|CancellableThreads
specifier|public
class|class
name|CancellableThreads
block|{
DECL|field|threads
specifier|private
specifier|final
name|Set
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// needs to be volatile as it is also read outside of synchronized blocks.
DECL|field|cancelled
specifier|private
specifier|volatile
name|boolean
name|cancelled
init|=
literal|false
decl_stmt|;
DECL|field|reason
specifier|private
name|String
name|reason
decl_stmt|;
DECL|method|isCancelled
specifier|public
specifier|synchronized
name|boolean
name|isCancelled
parameter_list|()
block|{
return|return
name|cancelled
return|;
block|}
comment|/** call this will throw an exception if operation was cancelled. Override {@link #onCancel(String, Exception)} for custom failure logic */
DECL|method|checkForCancel
specifier|public
specifier|synchronized
name|void
name|checkForCancel
parameter_list|()
block|{
if|if
condition|(
name|isCancelled
argument_list|()
condition|)
block|{
name|onCancel
argument_list|(
name|reason
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * called if {@link #checkForCancel()} was invoked after the operation was cancelled.      * the default implementation always throws an {@link ExecutionCancelledException}, suppressing      * any other exception that occurred before cancellation      *  @param reason              reason for failure supplied by the caller of {@link #cancel}      * @param suppressedException any error that was encountered during the execution before the operation was cancelled.      */
DECL|method|onCancel
specifier|protected
name|void
name|onCancel
parameter_list|(
name|String
name|reason
parameter_list|,
annotation|@
name|Nullable
name|Exception
name|suppressedException
parameter_list|)
block|{
name|RuntimeException
name|e
init|=
operator|new
name|ExecutionCancelledException
argument_list|(
literal|"operation was cancelled reason ["
operator|+
name|reason
operator|+
literal|"]"
argument_list|)
decl_stmt|;
if|if
condition|(
name|suppressedException
operator|!=
literal|null
condition|)
block|{
name|e
operator|.
name|addSuppressed
argument_list|(
name|suppressedException
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
DECL|method|add
specifier|private
specifier|synchronized
name|boolean
name|add
parameter_list|()
block|{
name|checkForCancel
argument_list|()
expr_stmt|;
name|threads
operator|.
name|add
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
comment|// capture and clean the interrupted thread before we start, so we can identify
comment|// our own interrupt. we do so under lock so we know we don't clear our own.
return|return
name|Thread
operator|.
name|interrupted
argument_list|()
return|;
block|}
comment|/**      * run the Interruptable, capturing the executing thread. Concurrent calls to {@link #cancel(String)} will interrupt this thread      * causing the call to prematurely return.      *      * @param interruptable code to run      */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|Interruptable
name|interruptable
parameter_list|)
block|{
try|try
block|{
name|executeIO
argument_list|(
name|interruptable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
assert|assert
literal|false
operator|:
literal|"the passed interruptable can not result in an IOException"
assert|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unexpected IO exception"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * run the Interruptable, capturing the executing thread. Concurrent calls to {@link #cancel(String)} will interrupt this thread      * causing the call to prematurely return.      *      * @param interruptable code to run      */
DECL|method|executeIO
specifier|public
name|void
name|executeIO
parameter_list|(
name|IOInterruptable
name|interruptable
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|wasInterrupted
init|=
name|add
argument_list|()
decl_stmt|;
name|boolean
name|cancelledByExternalInterrupt
init|=
literal|false
decl_stmt|;
name|RuntimeException
name|runtimeException
init|=
literal|null
decl_stmt|;
name|IOException
name|ioException
init|=
literal|null
decl_stmt|;
try|try
block|{
name|interruptable
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
decl||
name|ThreadInterruptedException
name|e
parameter_list|)
block|{
comment|// ignore, this interrupt has been triggered by us in #cancel()...
assert|assert
name|cancelled
operator|:
literal|"Interruption via Thread#interrupt() is unsupported. Use CancellableThreads#cancel() instead"
assert|;
comment|// we can only reach here if assertions are disabled. If we reach this code and cancelled is false, this means that we've
comment|// been interrupted externally (which we don't support).
name|cancelledByExternalInterrupt
operator|=
operator|!
name|cancelled
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|t
parameter_list|)
block|{
name|runtimeException
operator|=
name|t
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|ioException
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// we are now out of threads collection so we can't be interrupted any more by this class
comment|// restore old flag and see if we need to fail
if|if
condition|(
name|wasInterrupted
condition|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// clear the flag interrupted flag as we are checking for failure..
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|isCancelled
argument_list|()
condition|)
block|{
name|onCancel
argument_list|(
name|reason
argument_list|,
name|ioException
operator|!=
literal|null
condition|?
name|ioException
else|:
name|runtimeException
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|ioException
operator|!=
literal|null
condition|)
block|{
comment|// if we're not canceling, we throw the original exception
throw|throw
name|ioException
throw|;
block|}
if|if
condition|(
name|runtimeException
operator|!=
literal|null
condition|)
block|{
comment|// if we're not canceling, we throw the original exception
throw|throw
name|runtimeException
throw|;
block|}
block|}
if|if
condition|(
name|cancelledByExternalInterrupt
condition|)
block|{
comment|// restore interrupt flag to at least adhere to expected behavior
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Interruption via Thread#interrupt() is unsupported. Use CancellableThreads#cancel() instead"
argument_list|)
throw|;
block|}
block|}
DECL|method|remove
specifier|private
specifier|synchronized
name|void
name|remove
parameter_list|()
block|{
name|threads
operator|.
name|remove
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** cancel all current running operations. Future calls to {@link #checkForCancel()} will be failed with the given reason */
DECL|method|cancel
specifier|public
specifier|synchronized
name|void
name|cancel
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
if|if
condition|(
name|cancelled
condition|)
block|{
comment|// we were already cancelled, make sure we don't interrupt threads twice
comment|// this is important in order to make sure that we don't mark
comment|// Thread.interrupted without handling it
return|return;
block|}
name|cancelled
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
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
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|threads
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
DECL|interface|Interruptable
specifier|public
interface|interface
name|Interruptable
extends|extends
name|IOInterruptable
block|{
DECL|method|run
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
function_decl|;
block|}
DECL|interface|IOInterruptable
specifier|public
interface|interface
name|IOInterruptable
block|{
DECL|method|run
name|void
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
function_decl|;
block|}
DECL|class|ExecutionCancelledException
specifier|public
specifier|static
class|class
name|ExecutionCancelledException
extends|extends
name|ElasticsearchException
block|{
DECL|method|ExecutionCancelledException
specifier|public
name|ExecutionCancelledException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|ExecutionCancelledException
specifier|public
name|ExecutionCancelledException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

