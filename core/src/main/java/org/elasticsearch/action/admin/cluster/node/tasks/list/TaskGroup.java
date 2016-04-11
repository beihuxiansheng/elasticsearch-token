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
name|common
operator|.
name|xcontent
operator|.
name|ToXContent
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
name|List
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
comment|/**  * Information about a currently running task and all its subtasks.  */
end_comment

begin_class
DECL|class|TaskGroup
specifier|public
class|class
name|TaskGroup
implements|implements
name|ToXContent
block|{
DECL|field|task
specifier|private
specifier|final
name|TaskInfo
name|task
decl_stmt|;
DECL|field|childTasks
specifier|private
specifier|final
name|List
argument_list|<
name|TaskGroup
argument_list|>
name|childTasks
decl_stmt|;
DECL|method|TaskGroup
specifier|public
name|TaskGroup
parameter_list|(
name|TaskInfo
name|task
parameter_list|,
name|List
argument_list|<
name|TaskGroup
argument_list|>
name|childTasks
parameter_list|)
block|{
name|this
operator|.
name|task
operator|=
name|task
expr_stmt|;
name|this
operator|.
name|childTasks
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|childTasks
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|TaskInfo
name|taskInfo
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|taskInfo
argument_list|)
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|taskInfo
specifier|private
name|TaskInfo
name|taskInfo
decl_stmt|;
DECL|field|childTasks
specifier|private
name|List
argument_list|<
name|Builder
argument_list|>
name|childTasks
decl_stmt|;
DECL|method|Builder
specifier|private
name|Builder
parameter_list|(
name|TaskInfo
name|taskInfo
parameter_list|)
block|{
name|this
operator|.
name|taskInfo
operator|=
name|taskInfo
expr_stmt|;
name|childTasks
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
block|}
DECL|method|addGroup
specifier|public
name|void
name|addGroup
parameter_list|(
name|Builder
name|builder
parameter_list|)
block|{
name|childTasks
operator|.
name|add
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
DECL|method|getTaskInfo
specifier|public
name|TaskInfo
name|getTaskInfo
parameter_list|()
block|{
return|return
name|taskInfo
return|;
block|}
DECL|method|build
specifier|public
name|TaskGroup
name|build
parameter_list|()
block|{
return|return
operator|new
name|TaskGroup
argument_list|(
name|taskInfo
argument_list|,
name|childTasks
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
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
return|;
block|}
block|}
DECL|method|getTaskInfo
specifier|public
name|TaskInfo
name|getTaskInfo
parameter_list|()
block|{
return|return
name|task
return|;
block|}
DECL|method|getChildTasks
specifier|public
name|List
argument_list|<
name|TaskGroup
argument_list|>
name|getChildTasks
parameter_list|()
block|{
return|return
name|childTasks
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
name|task
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|childTasks
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"children"
argument_list|)
expr_stmt|;
for|for
control|(
name|TaskGroup
name|taskGroup
range|:
name|childTasks
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|taskGroup
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

