begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
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
name|ActionListener
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
name|admin
operator|.
name|cluster
operator|.
name|tasks
operator|.
name|PendingClusterTasksRequest
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
name|admin
operator|.
name|cluster
operator|.
name|tasks
operator|.
name|PendingClusterTasksResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|service
operator|.
name|PendingClusterTask
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
name|Table
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
name|inject
operator|.
name|Inject
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
name|rest
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestTable
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_class
DECL|class|RestPendingClusterTasksAction
specifier|public
class|class
name|RestPendingClusterTasksAction
extends|extends
name|AbstractCatAction
block|{
annotation|@
name|Inject
DECL|method|RestPendingClusterTasksAction
specifier|public
name|RestPendingClusterTasksAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/pending_tasks"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/pending_tasks\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
specifier|public
name|void
name|doRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|)
block|{
name|PendingClusterTasksRequest
name|pendingClusterTasksRequest
init|=
operator|new
name|PendingClusterTasksRequest
argument_list|()
decl_stmt|;
name|pendingClusterTasksRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|pendingClusterTasksRequest
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pendingClusterTasksRequest
operator|.
name|local
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"local"
argument_list|,
name|pendingClusterTasksRequest
operator|.
name|local
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|pendingClusterTasks
argument_list|(
name|pendingClusterTasksRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|PendingClusterTasksResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|PendingClusterTasksResponse
name|pendingClusterTasks
parameter_list|)
block|{
try|try
block|{
name|Table
name|tab
init|=
name|buildTable
argument_list|(
name|request
argument_list|,
name|pendingClusterTasks
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|tab
argument_list|,
name|request
argument_list|,
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentThrowableRestResponse
argument_list|(
name|request
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to send failure response"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
name|Table
name|getTableWithHeader
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
block|{
name|Table
name|t
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|t
operator|.
name|startHeaders
argument_list|()
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
literal|"insertOrder"
argument_list|,
literal|"alias:o;text-align:right;desc:task insertion order"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
literal|"timeInQueue"
argument_list|,
literal|"alias:t;text-align:right;desc:how long task has been in queue"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
literal|"priority"
argument_list|,
literal|"alias:p;desc:task priority"
argument_list|)
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
literal|"source"
argument_list|,
literal|"alias:s;desc:task source"
argument_list|)
expr_stmt|;
name|t
operator|.
name|endHeaders
argument_list|()
expr_stmt|;
return|return
name|t
return|;
block|}
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|PendingClusterTasksResponse
name|tasks
parameter_list|)
block|{
name|Table
name|t
init|=
name|getTableWithHeader
argument_list|(
name|request
argument_list|)
decl_stmt|;
for|for
control|(
name|PendingClusterTask
name|task
range|:
name|tasks
control|)
block|{
name|t
operator|.
name|startRow
argument_list|()
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
name|task
operator|.
name|getInsertOrder
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
name|task
operator|.
name|getTimeInQueue
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
name|task
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|addCell
argument_list|(
name|task
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
name|t
operator|.
name|endRow
argument_list|()
expr_stmt|;
block|}
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

