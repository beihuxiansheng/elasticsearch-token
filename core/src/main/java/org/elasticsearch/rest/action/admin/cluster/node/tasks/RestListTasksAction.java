begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.cluster.node.tasks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksRequest
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
name|node
operator|.
name|tasks
operator|.
name|list
operator|.
name|ListTasksResponse
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
name|node
operator|.
name|NodeClient
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
name|ClusterService
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
name|rest
operator|.
name|BaseRestHandler
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
name|RestChannel
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
name|RestController
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
name|RestRequest
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
name|RestToXContentListener
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
DECL|class|RestListTasksAction
specifier|public
class|class
name|RestListTasksAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
annotation|@
name|Inject
DECL|method|RestListTasksAction
specifier|public
name|RestListTasksAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_tasks"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|generateListTasksRequest
specifier|public
specifier|static
name|ListTasksRequest
name|generateListTasksRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|boolean
name|detailed
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"detailed"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|String
index|[]
name|nodesIds
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"node_id"
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|actions
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"actions"
argument_list|)
argument_list|)
decl_stmt|;
name|TaskId
name|parentTaskId
init|=
operator|new
name|TaskId
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"parent_task_id"
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|waitForCompletion
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"wait_for_completion"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|TimeValue
name|timeout
init|=
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"timeout"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ListTasksRequest
name|listTasksRequest
init|=
operator|new
name|ListTasksRequest
argument_list|()
decl_stmt|;
name|listTasksRequest
operator|.
name|setNodesIds
argument_list|(
name|nodesIds
argument_list|)
expr_stmt|;
name|listTasksRequest
operator|.
name|setDetailed
argument_list|(
name|detailed
argument_list|)
expr_stmt|;
name|listTasksRequest
operator|.
name|setActions
argument_list|(
name|actions
argument_list|)
expr_stmt|;
name|listTasksRequest
operator|.
name|setParentTaskId
argument_list|(
name|parentTaskId
argument_list|)
expr_stmt|;
name|listTasksRequest
operator|.
name|setWaitForCompletion
argument_list|(
name|waitForCompletion
argument_list|)
expr_stmt|;
name|listTasksRequest
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|listTasksRequest
return|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|NodeClient
name|client
parameter_list|)
block|{
name|ActionListener
argument_list|<
name|ListTasksResponse
argument_list|>
name|listener
init|=
name|nodeSettingListener
argument_list|(
name|clusterService
argument_list|,
operator|new
name|RestToXContentListener
argument_list|<>
argument_list|(
name|channel
argument_list|)
argument_list|)
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|listTasks
argument_list|(
name|generateListTasksRequest
argument_list|(
name|request
argument_list|)
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wrap the normal channel listener in one that sets the discovery nodes on the response so we can support all of it's toXContent      * formats.      */
DECL|method|nodeSettingListener
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|ListTasksResponse
parameter_list|>
name|ActionListener
argument_list|<
name|T
argument_list|>
name|nodeSettingListener
parameter_list|(
name|ClusterService
name|clusterService
parameter_list|,
name|ActionListener
argument_list|<
name|T
argument_list|>
name|channelListener
parameter_list|)
block|{
return|return
operator|new
name|ActionListener
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|T
name|response
parameter_list|)
block|{
name|response
operator|.
name|setDiscoveryNodes
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
argument_list|)
expr_stmt|;
name|channelListener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|channelListener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|canTripCircuitBreaker
specifier|public
name|boolean
name|canTripCircuitBreaker
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

