begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.timer
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|timer
package|;
end_package

begin_comment
comment|/**  * A handle associated with a {@link TimerTask} that is returned by a  * {@link Timer}.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|Timeout
specifier|public
interface|interface
name|Timeout
block|{
comment|/**      * Returns the {@link Timer} that created this handle.      */
DECL|method|getTimer
name|Timer
name|getTimer
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link TimerTask} which is associated with this handle.      */
DECL|method|getTask
name|TimerTask
name|getTask
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if and only if the {@link TimerTask} associated      * with this handle has been expired.      */
DECL|method|isExpired
name|boolean
name|isExpired
parameter_list|()
function_decl|;
comment|/**      * Returns {@code true} if and only if the {@link TimerTask} associated      * with this handle has been cancelled.      */
DECL|method|isCancelled
name|boolean
name|isCancelled
parameter_list|()
function_decl|;
comment|/**      * Cancels the {@link TimerTask} associated with this handle.  It the      * task has been executed or cancelled already, it will return with no      * side effect.      */
DECL|method|cancel
name|void
name|cancel
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

