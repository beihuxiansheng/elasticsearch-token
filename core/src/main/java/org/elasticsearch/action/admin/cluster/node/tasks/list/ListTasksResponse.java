begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.tasks.list
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|list
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
name|FailedNodeException
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
name|TaskOperationFailure
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
name|support
operator|.
name|tasks
operator|.
name|BaseTasksResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNodes
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
name|xcontent
operator|.
name|ToXContentObject
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
name|xcontent
operator|.
name|XContentBuilder
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
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskInfo
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Returns the list of tasks currently running on the nodes  */
end_comment

begin_class
DECL|class|ListTasksResponse
specifier|public
class|class
name|ListTasksResponse
extends|extends
name|BaseTasksResponse
implements|implements
name|ToXContentObject
block|{
DECL|field|tasks
specifier|private
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
decl_stmt|;
DECL|field|perNodeTasks
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TaskInfo
argument_list|>
argument_list|>
name|perNodeTasks
decl_stmt|;
DECL|field|groups
specifier|private
name|List
argument_list|<
name|TaskGroup
argument_list|>
name|groups
decl_stmt|;
DECL|method|ListTasksResponse
specifier|public
name|ListTasksResponse
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|ListTasksResponse
specifier|public
name|ListTasksResponse
parameter_list|(
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|tasks
parameter_list|,
name|List
argument_list|<
name|TaskOperationFailure
argument_list|>
name|taskFailures
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|FailedNodeException
argument_list|>
name|nodeFailures
parameter_list|)
block|{
name|super
argument_list|(
name|taskFailures
argument_list|,
name|nodeFailures
argument_list|)
expr_stmt|;
name|this
operator|.
name|tasks
operator|=
name|tasks
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptyList
argument_list|()
else|:
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|tasks
argument_list|)
argument_list|)
expr_stmt|;
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
name|tasks
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|in
operator|.
name|readList
argument_list|(
name|TaskInfo
operator|::
operator|new
argument_list|)
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
name|out
operator|.
name|writeList
argument_list|(
name|tasks
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the list of tasks by node      */
DECL|method|getPerNodeTasks
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TaskInfo
argument_list|>
argument_list|>
name|getPerNodeTasks
parameter_list|()
block|{
if|if
condition|(
name|perNodeTasks
operator|==
literal|null
condition|)
block|{
name|perNodeTasks
operator|=
name|tasks
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|groupingBy
argument_list|(
name|t
lambda|->
name|t
operator|.
name|getTaskId
argument_list|()
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|perNodeTasks
return|;
block|}
comment|/**      * Get the tasks found by this request grouped by parent tasks.      */
DECL|method|getTaskGroups
specifier|public
name|List
argument_list|<
name|TaskGroup
argument_list|>
name|getTaskGroups
parameter_list|()
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
name|buildTaskGroups
argument_list|()
expr_stmt|;
block|}
return|return
name|groups
return|;
block|}
DECL|method|buildTaskGroups
specifier|private
name|void
name|buildTaskGroups
parameter_list|()
block|{
name|Map
argument_list|<
name|TaskId
argument_list|,
name|TaskGroup
operator|.
name|Builder
argument_list|>
name|taskGroups
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|TaskGroup
operator|.
name|Builder
argument_list|>
name|topLevelTasks
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// First populate all tasks
for|for
control|(
name|TaskInfo
name|taskInfo
range|:
name|this
operator|.
name|tasks
control|)
block|{
name|taskGroups
operator|.
name|put
argument_list|(
name|taskInfo
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|TaskGroup
operator|.
name|builder
argument_list|(
name|taskInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Now go through all task group builders and add children to their parents
for|for
control|(
name|TaskGroup
operator|.
name|Builder
name|taskGroup
range|:
name|taskGroups
operator|.
name|values
argument_list|()
control|)
block|{
name|TaskId
name|parentTaskId
init|=
name|taskGroup
operator|.
name|getTaskInfo
argument_list|()
operator|.
name|getParentTaskId
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentTaskId
operator|.
name|isSet
argument_list|()
condition|)
block|{
name|TaskGroup
operator|.
name|Builder
name|parentTask
init|=
name|taskGroups
operator|.
name|get
argument_list|(
name|parentTaskId
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentTask
operator|!=
literal|null
condition|)
block|{
comment|// we found parent in the list of tasks - add it to the parent list
name|parentTask
operator|.
name|addGroup
argument_list|(
name|taskGroup
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we got zombie or the parent was filtered out - add it to the the top task list
name|topLevelTasks
operator|.
name|add
argument_list|(
name|taskGroup
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// top level task - add it to the top task list
name|topLevelTasks
operator|.
name|add
argument_list|(
name|taskGroup
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|groups
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|topLevelTasks
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|TaskGroup
operator|.
name|Builder
operator|::
name|build
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the tasks found by this request.      */
DECL|method|getTasks
specifier|public
name|List
argument_list|<
name|TaskInfo
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|tasks
return|;
block|}
comment|/**      * Convert this task response to XContent grouping by executing nodes.      */
DECL|method|toXContentGroupedByNode
specifier|public
name|XContentBuilder
name|toXContentGroupedByNode
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|,
name|DiscoveryNodes
name|discoveryNodes
parameter_list|)
throws|throws
name|IOException
block|{
name|toXContentCommon
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"nodes"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TaskInfo
argument_list|>
argument_list|>
name|entry
range|:
name|getPerNodeTasks
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|discoveryNodes
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
comment|// If the node is no longer part of the cluster, oh well, we'll just skip it's useful information.
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|node
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"transport_address"
argument_list|,
name|node
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"host"
argument_list|,
name|node
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"ip"
argument_list|,
name|node
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"roles"
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
operator|.
name|Role
name|role
range|:
name|node
operator|.
name|getRoles
argument_list|()
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|role
operator|.
name|getRoleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"attributes"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrEntry
range|:
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|attrEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|attrEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|startObject
argument_list|(
literal|"tasks"
argument_list|)
expr_stmt|;
for|for
control|(
name|TaskInfo
name|task
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|task
operator|.
name|getTaskId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|task
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * Convert this response to XContent grouping by parent tasks.      */
DECL|method|toXContentGroupedByParents
specifier|public
name|XContentBuilder
name|toXContentGroupedByParents
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|toXContentCommon
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"tasks"
argument_list|)
expr_stmt|;
for|for
control|(
name|TaskGroup
name|group
range|:
name|getTaskGroups
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|group
operator|.
name|getTaskInfo
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|group
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContentGroupedByParents
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|toXContentCommon
specifier|private
name|void
name|toXContentCommon
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|getTaskFailures
argument_list|()
operator|!=
literal|null
operator|&&
name|getTaskFailures
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"task_failures"
argument_list|)
expr_stmt|;
for|for
control|(
name|TaskOperationFailure
name|ex
range|:
name|getTaskFailures
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|value
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getNodeFailures
argument_list|()
operator|!=
literal|null
operator|&&
name|getNodeFailures
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"node_failures"
argument_list|)
expr_stmt|;
for|for
control|(
name|FailedNodeException
name|ex
range|:
name|getNodeFailures
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|ex
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
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
name|Strings
operator|.
name|toString
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

