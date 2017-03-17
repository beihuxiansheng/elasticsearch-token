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
comment|/**  * An interface for a request that can be used to register a task manager task  */
end_comment

begin_interface
DECL|interface|TaskAwareRequest
specifier|public
interface|interface
name|TaskAwareRequest
block|{
comment|/**      * Set a reference to task that caused this task to be run.      */
DECL|method|setParentTask
specifier|default
name|void
name|setParentTask
parameter_list|(
name|String
name|parentTaskNode
parameter_list|,
name|long
name|parentTaskId
parameter_list|)
block|{
name|setParentTask
argument_list|(
operator|new
name|TaskId
argument_list|(
name|parentTaskNode
argument_list|,
name|parentTaskId
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set a reference to task that created this request.      */
DECL|method|setParentTask
name|void
name|setParentTask
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
function_decl|;
comment|/**      * Get a reference to the task that created this request. Implementers should default to      * {@link TaskId#EMPTY_TASK_ID}, meaning "there is no parent".      */
DECL|method|getParentTask
name|TaskId
name|getParentTask
parameter_list|()
function_decl|;
comment|/**      * Returns the task object that should be used to keep track of the processing of the request.      *      * A request can override this method and return null to avoid being tracked by the task      * manager.      */
DECL|method|createTask
specifier|default
name|Task
name|createTask
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|action
parameter_list|,
name|TaskId
name|parentTaskId
parameter_list|)
block|{
return|return
operator|new
name|Task
argument_list|(
name|id
argument_list|,
name|type
argument_list|,
name|action
argument_list|,
name|getDescription
argument_list|()
argument_list|,
name|parentTaskId
argument_list|)
return|;
block|}
comment|/**      * Returns optional description of the request to be displayed by the task manager      */
DECL|method|getDescription
specifier|default
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
block|}
end_interface

end_unit

