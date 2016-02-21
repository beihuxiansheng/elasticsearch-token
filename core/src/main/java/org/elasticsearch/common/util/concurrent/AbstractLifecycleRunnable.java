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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|Lifecycle
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
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * {@code AbstractLifecycleRunnable} is a service-lifecycle aware {@link AbstractRunnable}.  *<p>  * This simplifies the running and rescheduling of {@link Lifecycle}-based {@code Runnable}s.  */
end_comment

begin_class
DECL|class|AbstractLifecycleRunnable
specifier|public
specifier|abstract
class|class
name|AbstractLifecycleRunnable
extends|extends
name|AbstractRunnable
block|{
comment|/**      * The monitored lifecycle for the associated service.      */
DECL|field|lifecycle
specifier|private
specifier|final
name|Lifecycle
name|lifecycle
decl_stmt|;
comment|/**      * The service's logger (note: this is passed in!).      */
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
comment|/**      * {@link AbstractLifecycleRunnable} must be aware of the actual {@code lifecycle} to react properly.      *      * @param lifecycle The lifecycle to react too      * @param logger The logger to use when logging      * @throws NullPointerException if any parameter is {@code null}      */
DECL|method|AbstractLifecycleRunnable
specifier|public
name|AbstractLifecycleRunnable
parameter_list|(
name|Lifecycle
name|lifecycle
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|lifecycle
argument_list|,
literal|"lifecycle must not be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|logger
argument_list|,
literal|"logger must not be null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|lifecycle
operator|=
name|lifecycle
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
comment|/**      * {@inheritDoc}      *<p>      * This invokes {@link #doRunInLifecycle()}<em>only</em> if the {@link #lifecycle} is not stopped or closed. Otherwise it exits      * immediately.      */
annotation|@
name|Override
DECL|method|doRun
specifier|protected
specifier|final
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
comment|// prevent execution if the service is stopped
if|if
condition|(
name|lifecycle
operator|.
name|stoppedOrClosed
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"service is stopping. exiting"
argument_list|)
expr_stmt|;
return|return;
block|}
name|doRunInLifecycle
argument_list|()
expr_stmt|;
block|}
comment|/**      * Perform runnable logic, but only if the {@link #lifecycle} is<em>not</em> stopped or closed.      *      * @throws InterruptedException if the run method throws an {@link InterruptedException}      */
DECL|method|doRunInLifecycle
specifier|protected
specifier|abstract
name|void
name|doRunInLifecycle
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**      * {@inheritDoc}      *<p>      * This overrides the default behavior of {@code onAfter} to add the caveat that it only runs if the {@link #lifecycle} is<em>not</em>      * stopped or closed.      *<p>      * Note: this does not guarantee that it won't be stopped concurrently as it invokes {@link #onAfterInLifecycle()},      * but it's a solid attempt at preventing it. For those that use this for rescheduling purposes, the next invocation would be      * effectively cancelled immediately if that's the case.      *      * @see #onAfterInLifecycle()      */
annotation|@
name|Override
DECL|method|onAfter
specifier|public
specifier|final
name|void
name|onAfter
parameter_list|()
block|{
if|if
condition|(
name|lifecycle
operator|.
name|stoppedOrClosed
argument_list|()
operator|==
literal|false
condition|)
block|{
name|onAfterInLifecycle
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This method is invoked in the finally block of the run method, but it is only executed if the {@link #lifecycle} is<em>not</em>      * stopped or closed.      *<p>      * This method is most useful for rescheduling the next iteration of the current runnable.      */
DECL|method|onAfterInLifecycle
specifier|protected
name|void
name|onAfterInLifecycle
parameter_list|()
block|{
comment|// nothing by default
block|}
block|}
end_class

end_unit

