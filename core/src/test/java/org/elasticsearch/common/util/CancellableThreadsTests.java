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
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|CancellableThreads
operator|.
name|IOInterruptable
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
name|CancellableThreads
operator|.
name|Interruptable
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
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|concurrent
operator|.
name|CountDownLatch
import|;
end_import

begin_class
DECL|class|CancellableThreadsTests
specifier|public
class|class
name|CancellableThreadsTests
extends|extends
name|ESTestCase
block|{
DECL|class|CustomException
specifier|public
specifier|static
class|class
name|CustomException
extends|extends
name|RuntimeException
block|{
DECL|method|CustomException
specifier|public
name|CustomException
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
block|}
DECL|class|IOCustomException
specifier|public
specifier|static
class|class
name|IOCustomException
extends|extends
name|IOException
block|{
DECL|method|IOCustomException
specifier|public
name|IOCustomException
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
block|}
DECL|class|TestPlan
specifier|private
class|class
name|TestPlan
block|{
DECL|field|id
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
DECL|field|busySpin
specifier|public
specifier|final
name|boolean
name|busySpin
decl_stmt|;
DECL|field|exceptBeforeCancel
specifier|public
specifier|final
name|boolean
name|exceptBeforeCancel
decl_stmt|;
DECL|field|exitBeforeCancel
specifier|public
specifier|final
name|boolean
name|exitBeforeCancel
decl_stmt|;
DECL|field|exceptAfterCancel
specifier|public
specifier|final
name|boolean
name|exceptAfterCancel
decl_stmt|;
DECL|field|presetInterrupt
specifier|public
specifier|final
name|boolean
name|presetInterrupt
decl_stmt|;
DECL|field|ioOp
specifier|public
specifier|final
name|boolean
name|ioOp
decl_stmt|;
DECL|field|ioException
specifier|private
specifier|final
name|boolean
name|ioException
decl_stmt|;
DECL|method|TestPlan
specifier|private
name|TestPlan
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|busySpin
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|exceptBeforeCancel
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|exitBeforeCancel
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|exceptAfterCancel
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|presetInterrupt
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|ioOp
operator|=
name|randomBoolean
argument_list|()
expr_stmt|;
name|this
operator|.
name|ioException
operator|=
name|ioOp
operator|&&
name|randomBoolean
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|TestRunnable
specifier|static
class|class
name|TestRunnable
implements|implements
name|Interruptable
block|{
DECL|field|plan
specifier|final
name|TestPlan
name|plan
decl_stmt|;
DECL|field|readyForCancel
specifier|final
name|CountDownLatch
name|readyForCancel
decl_stmt|;
DECL|method|TestRunnable
name|TestRunnable
parameter_list|(
name|TestPlan
name|plan
parameter_list|,
name|CountDownLatch
name|readyForCancel
parameter_list|)
block|{
name|this
operator|.
name|plan
operator|=
name|plan
expr_stmt|;
name|this
operator|.
name|readyForCancel
operator|=
name|readyForCancel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|assertFalse
argument_list|(
literal|"interrupt thread should have been clear"
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|plan
operator|.
name|exceptBeforeCancel
condition|)
block|{
throw|throw
operator|new
name|CustomException
argument_list|(
literal|"thread ["
operator|+
name|plan
operator|.
name|id
operator|+
literal|"] pre-cancel exception"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|plan
operator|.
name|exitBeforeCancel
condition|)
block|{
return|return;
block|}
name|readyForCancel
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|plan
operator|.
name|busySpin
condition|)
block|{
while|while
condition|(
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{                     }
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50000
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|plan
operator|.
name|exceptAfterCancel
condition|)
block|{
throw|throw
operator|new
name|CustomException
argument_list|(
literal|"thread ["
operator|+
name|plan
operator|.
name|id
operator|+
literal|"] post-cancel exception"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|class|TestIORunnable
specifier|static
class|class
name|TestIORunnable
implements|implements
name|IOInterruptable
block|{
DECL|field|plan
specifier|final
name|TestPlan
name|plan
decl_stmt|;
DECL|field|readyForCancel
specifier|final
name|CountDownLatch
name|readyForCancel
decl_stmt|;
DECL|method|TestIORunnable
name|TestIORunnable
parameter_list|(
name|TestPlan
name|plan
parameter_list|,
name|CountDownLatch
name|readyForCancel
parameter_list|)
block|{
name|this
operator|.
name|plan
operator|=
name|plan
expr_stmt|;
name|this
operator|.
name|readyForCancel
operator|=
name|readyForCancel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|assertFalse
argument_list|(
literal|"interrupt thread should have been clear"
argument_list|,
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|plan
operator|.
name|exceptBeforeCancel
condition|)
block|{
throw|throw
operator|new
name|IOCustomException
argument_list|(
literal|"thread ["
operator|+
name|plan
operator|.
name|id
operator|+
literal|"] pre-cancel exception"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|plan
operator|.
name|exitBeforeCancel
condition|)
block|{
return|return;
block|}
name|readyForCancel
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|plan
operator|.
name|busySpin
condition|)
block|{
while|while
condition|(
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{                     }
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|50000
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|plan
operator|.
name|exceptAfterCancel
condition|)
block|{
throw|throw
operator|new
name|IOCustomException
argument_list|(
literal|"thread ["
operator|+
name|plan
operator|.
name|id
operator|+
literal|"] post-cancel exception"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|testCancellableThreads
specifier|public
name|void
name|testCancellableThreads
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|randomIntBetween
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
index|]
decl_stmt|;
specifier|final
name|TestPlan
index|[]
name|plans
init|=
operator|new
name|TestPlan
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|Exception
index|[]
name|exceptions
init|=
operator|new
name|Exception
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|boolean
index|[]
name|interrupted
init|=
operator|new
name|boolean
index|[
name|threads
operator|.
name|length
index|]
decl_stmt|;
specifier|final
name|CancellableThreads
name|cancellableThreads
init|=
operator|new
name|CancellableThreads
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|readyForCancel
init|=
operator|new
name|CountDownLatch
argument_list|(
name|threads
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TestPlan
name|plan
init|=
operator|new
name|TestPlan
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|plans
index|[
name|i
index|]
operator|=
name|plan
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
if|if
condition|(
name|plan
operator|.
name|presetInterrupt
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
if|if
condition|(
name|plan
operator|.
name|ioOp
condition|)
block|{
if|if
condition|(
name|plan
operator|.
name|ioException
condition|)
block|{
name|cancellableThreads
operator|.
name|executeIO
argument_list|(
operator|new
name|TestIORunnable
argument_list|(
name|plan
argument_list|,
name|readyForCancel
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cancellableThreads
operator|.
name|executeIO
argument_list|(
operator|new
name|TestRunnable
argument_list|(
name|plan
argument_list|,
name|readyForCancel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|cancellableThreads
operator|.
name|execute
argument_list|(
operator|new
name|TestRunnable
argument_list|(
name|plan
argument_list|,
name|readyForCancel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exceptions
index|[
name|plan
operator|.
name|id
index|]
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|plan
operator|.
name|exceptBeforeCancel
operator|||
name|plan
operator|.
name|exitBeforeCancel
condition|)
block|{
comment|// we have to mark we're ready now (actually done).
name|readyForCancel
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|interrupted
index|[
name|plan
operator|.
name|id
index|]
operator|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|readyForCancel
operator|.
name|await
argument_list|()
expr_stmt|;
name|cancellableThreads
operator|.
name|cancel
argument_list|(
literal|"test"
argument_list|)
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
name|join
argument_list|(
literal|20000
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|thread
operator|.
name|isAlive
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|TestPlan
name|plan
init|=
name|plans
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|exceptionClass
init|=
name|plan
operator|.
name|ioException
condition|?
name|IOCustomException
operator|.
name|class
else|:
name|CustomException
operator|.
name|class
decl_stmt|;
if|if
condition|(
name|plan
operator|.
name|exceptBeforeCancel
condition|)
block|{
name|assertThat
argument_list|(
name|exceptions
index|[
name|i
index|]
argument_list|,
name|Matchers
operator|.
name|instanceOf
argument_list|(
name|exceptionClass
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|plan
operator|.
name|exitBeforeCancel
condition|)
block|{
name|assertNull
argument_list|(
name|exceptions
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// in all other cases, we expect a cancellation exception.
name|assertThat
argument_list|(
name|exceptions
index|[
name|i
index|]
argument_list|,
name|Matchers
operator|.
name|instanceOf
argument_list|(
name|CancellableThreads
operator|.
name|ExecutionCancelledException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|plan
operator|.
name|exceptAfterCancel
condition|)
block|{
name|assertThat
argument_list|(
name|exceptions
index|[
name|i
index|]
operator|.
name|getSuppressed
argument_list|()
argument_list|,
name|Matchers
operator|.
name|arrayContaining
argument_list|(
name|Matchers
operator|.
name|instanceOf
argument_list|(
name|exceptionClass
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|exceptions
index|[
name|i
index|]
operator|.
name|getSuppressed
argument_list|()
argument_list|,
name|Matchers
operator|.
name|emptyArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|interrupted
index|[
name|plan
operator|.
name|id
index|]
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|plan
operator|.
name|presetInterrupt
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

