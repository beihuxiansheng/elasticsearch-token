begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.component
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
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

begin_comment
comment|/**  * Lifecycle state. Allows the following transitions:  *<ul>  *<li>INITIALIZED -> STARTED, STOPPED, CLOSED</li>  *<li>STARTED     -> STOPPED</li>  *<li>STOPPED     -> STARTED, CLOSED</li>  *<li>CLOSED      -></li>  *</ul>  *<p/>  *<p>Also allows to stay in the same state. For example, when calling stop on a component, the  * following logic can be applied:  *<p/>  *<pre>  * public void stop() {  *  if (!lifeccycleState.moveToStopped()) {  *      return;  *  }  * // continue with stop logic  * }  *</pre>  *<p/>  *<p>Note, closed is only allowed to be called when stopped, so make sure to stop the component first.  * Here is how the logic can be applied:  *<p/>  *<pre>  * public void close() {  *  if (lifecycleState.started()) {  *      stop();  *  }  *  if (!lifecycleState.moveToClosed()) {  *      return;  *  }  *  // perofrm close logic here  * }  *</pre>  */
end_comment

begin_class
DECL|class|Lifecycle
specifier|public
class|class
name|Lifecycle
block|{
DECL|enum|State
specifier|public
specifier|static
enum|enum
name|State
block|{
DECL|enum constant|INITIALIZED
name|INITIALIZED
block|,
DECL|enum constant|STOPPED
name|STOPPED
block|,
DECL|enum constant|STARTED
name|STARTED
block|,
DECL|enum constant|CLOSED
name|CLOSED
block|}
DECL|field|state
specifier|private
specifier|volatile
name|State
name|state
init|=
name|State
operator|.
name|INITIALIZED
decl_stmt|;
DECL|method|state
specifier|public
name|State
name|state
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
comment|/**      * Returns<tt>true</tt> if the state is initialized.      */
DECL|method|initialized
specifier|public
name|boolean
name|initialized
parameter_list|()
block|{
return|return
name|state
operator|==
name|State
operator|.
name|INITIALIZED
return|;
block|}
comment|/**      * Returns<tt>true</tt> if the state is started.      */
DECL|method|started
specifier|public
name|boolean
name|started
parameter_list|()
block|{
return|return
name|state
operator|==
name|State
operator|.
name|STARTED
return|;
block|}
comment|/**      * Returns<tt>true</tt> if the state is stopped.      */
DECL|method|stopped
specifier|public
name|boolean
name|stopped
parameter_list|()
block|{
return|return
name|state
operator|==
name|State
operator|.
name|STOPPED
return|;
block|}
comment|/**      * Returns<tt>true</tt> if the state is closed.      */
DECL|method|closed
specifier|public
name|boolean
name|closed
parameter_list|()
block|{
return|return
name|state
operator|==
name|State
operator|.
name|CLOSED
return|;
block|}
DECL|method|stoppedOrClosed
specifier|public
name|boolean
name|stoppedOrClosed
parameter_list|()
block|{
name|Lifecycle
operator|.
name|State
name|state
init|=
name|this
operator|.
name|state
decl_stmt|;
return|return
name|state
operator|==
name|State
operator|.
name|STOPPED
operator|||
name|state
operator|==
name|State
operator|.
name|CLOSED
return|;
block|}
DECL|method|canMoveToStarted
specifier|public
name|boolean
name|canMoveToStarted
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|State
name|localState
init|=
name|this
operator|.
name|state
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|INITIALIZED
operator|||
name|localState
operator|==
name|State
operator|.
name|STOPPED
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|STARTED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|CLOSED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started state when closed"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started with unknown state"
argument_list|)
throw|;
block|}
DECL|method|moveToStarted
specifier|public
name|boolean
name|moveToStarted
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|State
name|localState
init|=
name|this
operator|.
name|state
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|INITIALIZED
operator|||
name|localState
operator|==
name|State
operator|.
name|STOPPED
condition|)
block|{
name|state
operator|=
name|State
operator|.
name|STARTED
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|STARTED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|CLOSED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started state when closed"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started with unknown state"
argument_list|)
throw|;
block|}
DECL|method|canMoveToStopped
specifier|public
name|boolean
name|canMoveToStopped
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|State
name|localState
init|=
name|state
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|STARTED
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|INITIALIZED
operator|||
name|localState
operator|==
name|State
operator|.
name|STOPPED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|CLOSED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started state when closed"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started with unknown state"
argument_list|)
throw|;
block|}
DECL|method|moveToStopped
specifier|public
name|boolean
name|moveToStopped
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|State
name|localState
init|=
name|state
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|STARTED
condition|)
block|{
name|state
operator|=
name|State
operator|.
name|STOPPED
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|INITIALIZED
operator|||
name|localState
operator|==
name|State
operator|.
name|STOPPED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|CLOSED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started state when closed"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to started with unknown state"
argument_list|)
throw|;
block|}
DECL|method|canMoveToClosed
specifier|public
name|boolean
name|canMoveToClosed
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|State
name|localState
init|=
name|state
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|CLOSED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|STARTED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to closed before moving to stopped mode"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|moveToClosed
specifier|public
name|boolean
name|moveToClosed
parameter_list|()
throws|throws
name|IllegalStateException
block|{
name|State
name|localState
init|=
name|state
decl_stmt|;
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|CLOSED
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|localState
operator|==
name|State
operator|.
name|STARTED
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can't move to closed before moving to stopped mode"
argument_list|)
throw|;
block|}
name|state
operator|=
name|State
operator|.
name|CLOSED
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

