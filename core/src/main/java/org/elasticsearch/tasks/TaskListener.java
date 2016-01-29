begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
package|;
end_package

begin_comment
comment|/**  * Listener for Task success or failure.  */
end_comment

begin_interface
DECL|interface|TaskListener
specifier|public
interface|interface
name|TaskListener
parameter_list|<
name|Response
parameter_list|>
block|{
comment|/**      * Handle task response. This response may constitute a failure or a success      * but it is up to the listener to make that decision.      *      * @param task      *            the task being executed. May be null if the action doesn't      *            create a task      * @param response      *            the response from the action that executed the task      */
DECL|method|onResponse
name|void
name|onResponse
parameter_list|(
name|Task
name|task
parameter_list|,
name|Response
name|response
parameter_list|)
function_decl|;
comment|/**      * A failure caused by an exception at some phase of the task.      *      * @param task      *            the task being executed. May be null if the action doesn't      *            create a task      * @param e      *            the failure      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Task
name|task
parameter_list|,
name|Throwable
name|e
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

