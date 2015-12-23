begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|tasks
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
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
name|Strings
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
name|common
operator|.
name|regex
operator|.
name|Regex
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
name|unit
operator|.
name|TimeValue
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
name|ChildTask
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A base class for task requests  */
end_comment

begin_class
DECL|class|BaseTasksRequest
specifier|public
class|class
name|BaseTasksRequest
parameter_list|<
name|T
extends|extends
name|BaseTasksRequest
parameter_list|>
extends|extends
name|ActionRequest
argument_list|<
name|T
argument_list|>
block|{
DECL|field|ALL_ACTIONS
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|ALL_ACTIONS
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|ALL_NODES
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|ALL_NODES
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|ALL_TASKS
specifier|public
specifier|static
specifier|final
name|long
name|ALL_TASKS
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|nodesIds
specifier|private
name|String
index|[]
name|nodesIds
init|=
name|ALL_NODES
decl_stmt|;
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
decl_stmt|;
DECL|field|actions
specifier|private
name|String
index|[]
name|actions
init|=
name|ALL_ACTIONS
decl_stmt|;
DECL|field|parentNode
specifier|private
name|String
name|parentNode
decl_stmt|;
DECL|field|parentTaskId
specifier|private
name|long
name|parentTaskId
init|=
name|ALL_TASKS
decl_stmt|;
DECL|method|BaseTasksRequest
specifier|public
name|BaseTasksRequest
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get information about tasks from nodes based on the nodes ids specified.      * If none are passed, information for all nodes will be returned.      */
DECL|method|BaseTasksRequest
specifier|public
name|BaseTasksRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
name|this
operator|.
name|nodesIds
operator|=
name|nodesIds
expr_stmt|;
block|}
comment|/**      * Sets the list of action masks for the actions that should be returned      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|actions
specifier|public
specifier|final
name|T
name|actions
parameter_list|(
name|String
modifier|...
name|actions
parameter_list|)
block|{
name|this
operator|.
name|actions
operator|=
name|actions
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * Return the list of action masks for the actions that should be returned      */
DECL|method|actions
specifier|public
name|String
index|[]
name|actions
parameter_list|()
block|{
return|return
name|actions
return|;
block|}
DECL|method|nodesIds
specifier|public
specifier|final
name|String
index|[]
name|nodesIds
parameter_list|()
block|{
return|return
name|nodesIds
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|nodesIds
specifier|public
specifier|final
name|T
name|nodesIds
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
name|this
operator|.
name|nodesIds
operator|=
name|nodesIds
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * Returns the parent node id that tasks should be filtered by      */
DECL|method|parentNode
specifier|public
name|String
name|parentNode
parameter_list|()
block|{
return|return
name|parentNode
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|parentNode
specifier|public
name|T
name|parentNode
parameter_list|(
name|String
name|parentNode
parameter_list|)
block|{
name|this
operator|.
name|parentNode
operator|=
name|parentNode
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
comment|/**      * Returns the parent task id that tasks should be filtered by      */
DECL|method|parentTaskId
specifier|public
name|long
name|parentTaskId
parameter_list|()
block|{
return|return
name|parentTaskId
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|parentTaskId
specifier|public
name|T
name|parentTaskId
parameter_list|(
name|long
name|parentTaskId
parameter_list|)
block|{
name|this
operator|.
name|parentTaskId
operator|=
name|parentTaskId
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|timeout
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|timeout
specifier|public
specifier|final
name|T
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|timeout
specifier|public
specifier|final
name|T
name|timeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|timeout
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".timeout"
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|this
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
name|nodesIds
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|actions
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|parentNode
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|parentTaskId
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|timeout
operator|=
name|TimeValue
operator|.
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|nodesIds
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|actions
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|parentNode
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|parentTaskId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
DECL|method|match
specifier|public
name|boolean
name|match
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
if|if
condition|(
name|actions
argument_list|()
operator|!=
literal|null
operator|&&
name|actions
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|&&
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|actions
argument_list|()
argument_list|,
name|task
operator|.
name|getAction
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|parentNode
argument_list|()
operator|!=
literal|null
operator|||
name|parentTaskId
argument_list|()
operator|!=
name|BaseTasksRequest
operator|.
name|ALL_TASKS
condition|)
block|{
if|if
condition|(
name|task
operator|instanceof
name|ChildTask
condition|)
block|{
if|if
condition|(
name|parentNode
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|parentNode
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ChildTask
operator|)
name|task
operator|)
operator|.
name|getParentNode
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|parentTaskId
argument_list|()
operator|!=
name|BaseTasksRequest
operator|.
name|ALL_TASKS
condition|)
block|{
if|if
condition|(
name|parentTaskId
argument_list|()
operator|!=
operator|(
operator|(
name|ChildTask
operator|)
name|task
operator|)
operator|.
name|getParentId
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
else|else
block|{
comment|// This is not a child task and we need to match parent node or id
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

