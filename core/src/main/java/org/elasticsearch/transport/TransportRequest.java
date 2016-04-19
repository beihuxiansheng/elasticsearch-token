begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|StreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskId
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportRequest
specifier|public
specifier|abstract
class|class
name|TransportRequest
extends|extends
name|TransportMessage
block|{
DECL|class|Empty
specifier|public
specifier|static
class|class
name|Empty
extends|extends
name|TransportRequest
block|{
DECL|field|INSTANCE
specifier|public
specifier|static
specifier|final
name|Empty
name|INSTANCE
init|=
operator|new
name|Empty
argument_list|()
decl_stmt|;
block|}
comment|/**      * Parent of this request. Defaults to {@link TaskId#EMPTY_TASK_ID}, meaning "no parent".      */
DECL|field|parentTaskId
specifier|private
name|TaskId
name|parentTaskId
init|=
name|TaskId
operator|.
name|EMPTY_TASK_ID
decl_stmt|;
DECL|method|TransportRequest
specifier|public
name|TransportRequest
parameter_list|()
block|{     }
comment|/**      * Set a reference to task that caused this task to be run.      */
DECL|method|setParentTask
specifier|public
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
specifier|public
name|void
name|setParentTask
parameter_list|(
name|TaskId
name|taskId
parameter_list|)
block|{
name|this
operator|.
name|parentTaskId
operator|=
name|taskId
expr_stmt|;
block|}
comment|/**      * Get a reference to the task that created this request. Defaults to {@link TaskId#EMPTY_TASK_ID}, meaning "there is no parent".      */
DECL|method|getParentTask
specifier|public
name|TaskId
name|getParentTask
parameter_list|()
block|{
return|return
name|parentTaskId
return|;
block|}
comment|/**      * Returns the task object that should be used to keep track of the processing of the request.      *      * A request can override this method and return null to avoid being tracked by the task manager.      */
DECL|method|createTask
specifier|public
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
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|parentTaskId
operator|=
name|TaskId
operator|.
name|readFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|parentTaskId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

