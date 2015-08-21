begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.bootstrap
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|bootstrap
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
name|inject
operator|.
name|CreationException
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
name|inject
operator|.
name|spi
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_comment
comment|/**  * Wraps an exception in a special way that it gets formatted  * "reasonably". This means limits on stacktrace frames and  * cleanup for guice, and some guidance about consulting full  * logs for the whole exception.  */
end_comment

begin_comment
comment|//TODO: remove this when guice is removed, and exceptions are cleaned up
end_comment

begin_comment
comment|//this is horrible, but its what we must do
end_comment

begin_class
DECL|class|StartupError
class|class
name|StartupError
extends|extends
name|RuntimeException
block|{
comment|/** maximum length of a stacktrace, before we truncate it */
DECL|field|STACKTRACE_LIMIT
specifier|static
specifier|final
name|int
name|STACKTRACE_LIMIT
init|=
literal|30
decl_stmt|;
comment|/** all lines from this package are RLE-compressed */
DECL|field|GUICE_PACKAGE
specifier|static
specifier|final
name|String
name|GUICE_PACKAGE
init|=
literal|"org.elasticsearch.common.inject"
decl_stmt|;
comment|/**       * Create a new StartupError that will format {@code cause}      * to the console on failure.      */
DECL|method|StartupError
name|StartupError
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/*      * This logic actually prints the exception to the console, its      * what is invoked by the JVM when we throw the exception from main()      */
annotation|@
name|Override
DECL|method|printStackTrace
specifier|public
name|void
name|printStackTrace
parameter_list|(
name|PrintStream
name|s
parameter_list|)
block|{
name|Throwable
name|originalCause
init|=
name|getCause
argument_list|()
decl_stmt|;
name|Throwable
name|cause
init|=
name|originalCause
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|CreationException
condition|)
block|{
name|cause
operator|=
name|getFirstGuiceCause
argument_list|(
operator|(
name|CreationException
operator|)
name|cause
argument_list|)
expr_stmt|;
block|}
name|String
name|message
init|=
name|cause
operator|.
name|toString
argument_list|()
decl_stmt|;
name|s
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
comment|// walk to the root cause
while|while
condition|(
name|cause
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cause
operator|=
name|cause
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
comment|// print the root cause message, only if it differs!
if|if
condition|(
name|cause
operator|!=
name|originalCause
operator|&&
operator|(
name|message
operator|.
name|equals
argument_list|(
name|cause
operator|.
name|toString
argument_list|()
argument_list|)
operator|==
literal|false
operator|)
condition|)
block|{
name|s
operator|.
name|println
argument_list|(
literal|"Likely root cause: "
operator|+
name|cause
argument_list|)
expr_stmt|;
block|}
comment|// print stacktrace of cause
name|StackTraceElement
name|stack
index|[]
init|=
name|cause
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
name|int
name|linesWritten
init|=
literal|0
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
name|stack
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|linesWritten
operator|==
name|STACKTRACE_LIMIT
condition|)
block|{
name|s
operator|.
name|println
argument_list|(
literal|"\t<<<truncated>>>"
argument_list|)
expr_stmt|;
break|break;
block|}
name|String
name|line
init|=
name|stack
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// skip past contiguous runs of this garbage:
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|GUICE_PACKAGE
argument_list|)
condition|)
block|{
while|while
condition|(
name|i
operator|+
literal|1
operator|<
name|stack
operator|.
name|length
operator|&&
name|stack
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|toString
argument_list|()
operator|.
name|startsWith
argument_list|(
name|GUICE_PACKAGE
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|s
operator|.
name|println
argument_list|(
literal|"\tat<<<guice>>>"
argument_list|)
expr_stmt|;
name|linesWritten
operator|++
expr_stmt|;
continue|continue;
block|}
name|s
operator|.
name|println
argument_list|(
literal|"\tat "
operator|+
name|line
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|linesWritten
operator|++
expr_stmt|;
block|}
block|}
name|s
operator|.
name|println
argument_list|(
literal|"Refer to the log for complete error details."
argument_list|)
expr_stmt|;
block|}
comment|/**       * Returns first cause from a guice error (it can have multiple).      */
DECL|method|getFirstGuiceCause
specifier|static
name|Throwable
name|getFirstGuiceCause
parameter_list|(
name|CreationException
name|guice
parameter_list|)
block|{
for|for
control|(
name|Message
name|message
range|:
name|guice
operator|.
name|getErrorMessages
argument_list|()
control|)
block|{
name|Throwable
name|cause
init|=
name|message
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
return|return
name|cause
return|;
block|}
block|}
return|return
name|guice
return|;
comment|// we tried
block|}
block|}
end_class

end_unit

