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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * Schedules {@link TimerTask}s for one-time future execution in a background  * thread.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|Timer
specifier|public
interface|interface
name|Timer
block|{
comment|/**      * Schedules the specified {@link TimerTask} for one-time execution after      * the specified delay.      *      * @return a handle which is associated with the specified task      * @throws IllegalStateException if this timer has been      *                               {@linkplain #stop() stopped} already      */
DECL|method|newTimeout
name|Timeout
name|newTimeout
parameter_list|(
name|TimerTask
name|task
parameter_list|,
name|long
name|delay
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
function_decl|;
comment|/**      * Releases all resources acquired by this {@link Timer} and cancels all      * tasks which were scheduled but not executed yet.      *      * @return the handles associated with the tasks which were canceled by      *         this method      */
DECL|method|stop
name|Set
argument_list|<
name|Timeout
argument_list|>
name|stop
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

