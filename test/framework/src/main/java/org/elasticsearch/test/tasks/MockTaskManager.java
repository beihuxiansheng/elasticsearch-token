begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|common
operator|.
name|settings
operator|.
name|Setting
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
name|settings
operator|.
name|Setting
operator|.
name|SettingsProperty
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
name|settings
operator|.
name|Settings
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
name|TaskManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/**  * A mock task manager that allows adding listeners for events  */
end_comment

begin_class
DECL|class|MockTaskManager
specifier|public
class|class
name|MockTaskManager
extends|extends
name|TaskManager
block|{
DECL|field|USE_MOCK_TASK_MANAGER_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|USE_MOCK_TASK_MANAGER_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"tests.mock.taskmanager.enabled"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|SettingsProperty
operator|.
name|ClusterScope
argument_list|)
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|Collection
argument_list|<
name|MockTaskManagerListener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MockTaskManager
specifier|public
name|MockTaskManager
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|register
specifier|public
name|Task
name|register
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|action
parameter_list|,
name|TransportRequest
name|request
parameter_list|)
block|{
name|Task
name|task
init|=
name|super
operator|.
name|register
argument_list|(
name|type
argument_list|,
name|action
argument_list|,
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MockTaskManagerListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onTaskRegistered
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to notify task manager listener about unregistering the task with id {}"
argument_list|,
name|t
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|task
return|;
block|}
annotation|@
name|Override
DECL|method|unregister
specifier|public
name|Task
name|unregister
parameter_list|(
name|Task
name|task
parameter_list|)
block|{
name|Task
name|removedTask
init|=
name|super
operator|.
name|unregister
argument_list|(
name|task
argument_list|)
decl_stmt|;
if|if
condition|(
name|removedTask
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|MockTaskManagerListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onTaskUnregistered
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to notify task manager listener about unregistering the task with id {}"
argument_list|,
name|t
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"trying to remove the same with id {} twice"
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|removedTask
return|;
block|}
DECL|method|addListener
specifier|public
name|void
name|addListener
parameter_list|(
name|MockTaskManagerListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|removeListener
specifier|public
name|void
name|removeListener
parameter_list|(
name|MockTaskManagerListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

