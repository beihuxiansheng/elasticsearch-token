begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.tasks.cancel
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
name|cancel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ResourceNotFoundException
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
name|ActionFilters
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
name|TransportTasksAction
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
name|ClusterState
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
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|CancellableTask
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
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|EmptyTransportResponseHandler
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
name|TransportChannel
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
name|TransportException
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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportRequestHandler
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
name|TransportResponse
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
name|TransportService
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
name|List
import|;
end_import

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
name|atomic
operator|.
name|AtomicInteger
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_comment
comment|/**  * Transport action that can be used to cancel currently running cancellable tasks.  *<p>  * For a task to be cancellable it has to return an instance of  * {@link CancellableTask} from {@link TransportRequest#createTask(long, String, String, TaskId)}  */
end_comment

begin_class
DECL|class|TransportCancelTasksAction
specifier|public
class|class
name|TransportCancelTasksAction
extends|extends
name|TransportTasksAction
argument_list|<
name|CancellableTask
argument_list|,
name|CancelTasksRequest
argument_list|,
name|CancelTasksResponse
argument_list|,
name|TaskInfo
argument_list|>
block|{
DECL|field|BAN_PARENT_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|BAN_PARENT_ACTION_NAME
init|=
literal|"internal:admin/tasks/ban"
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportCancelTasksAction
specifier|public
name|TransportCancelTasksAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|CancelTasksAction
operator|.
name|NAME
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|CancelTasksRequest
operator|::
operator|new
argument_list|,
name|CancelTasksResponse
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|BAN_PARENT_ACTION_NAME
argument_list|,
name|BanParentTaskRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
operator|new
name|BanParentRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|CancelTasksResponse
name|newResponse
parameter_list|(
name|CancelTasksRequest
name|request
parameter_list|,
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
name|taskOperationFailures
parameter_list|,
name|List
argument_list|<
name|FailedNodeException
argument_list|>
name|failedNodeExceptions
parameter_list|)
block|{
return|return
operator|new
name|CancelTasksResponse
argument_list|(
name|tasks
argument_list|,
name|taskOperationFailures
argument_list|,
name|failedNodeExceptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readTaskResponse
specifier|protected
name|TaskInfo
name|readTaskResponse
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TaskInfo
argument_list|(
name|in
argument_list|)
return|;
block|}
DECL|method|processTasks
specifier|protected
name|void
name|processTasks
parameter_list|(
name|CancelTasksRequest
name|request
parameter_list|,
name|Consumer
argument_list|<
name|CancellableTask
argument_list|>
name|operation
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|getTaskId
argument_list|()
operator|.
name|isSet
argument_list|()
condition|)
block|{
comment|// we are only checking one task, we can optimize it
name|CancellableTask
name|task
init|=
name|taskManager
operator|.
name|getCancellableTask
argument_list|(
name|request
operator|.
name|getTaskId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|match
argument_list|(
name|task
argument_list|)
condition|)
block|{
name|operation
operator|.
name|accept
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"task ["
operator|+
name|request
operator|.
name|getTaskId
argument_list|()
operator|+
literal|"] doesn't support this operation"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|taskManager
operator|.
name|getTask
argument_list|(
name|request
operator|.
name|getTaskId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// The task exists, but doesn't support cancellation
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"task ["
operator|+
name|request
operator|.
name|getTaskId
argument_list|()
operator|+
literal|"] doesn't support cancellation"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"task [{}] doesn't support cancellation"
argument_list|,
name|request
operator|.
name|getTaskId
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|CancellableTask
name|task
range|:
name|taskManager
operator|.
name|getCancellableTasks
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|request
operator|.
name|match
argument_list|(
name|task
argument_list|)
condition|)
block|{
name|operation
operator|.
name|accept
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|taskOperation
specifier|protected
specifier|synchronized
name|void
name|taskOperation
parameter_list|(
name|CancelTasksRequest
name|request
parameter_list|,
name|CancellableTask
name|cancellableTask
parameter_list|,
name|ActionListener
argument_list|<
name|TaskInfo
argument_list|>
name|listener
parameter_list|)
block|{
specifier|final
name|BanLock
name|banLock
init|=
operator|new
name|BanLock
argument_list|(
name|nodes
lambda|->
name|removeBanOnNodes
argument_list|(
name|cancellableTask
argument_list|,
name|nodes
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|childNodes
init|=
name|taskManager
operator|.
name|cancel
argument_list|(
name|cancellableTask
argument_list|,
name|request
operator|.
name|getReason
argument_list|()
argument_list|,
name|banLock
operator|::
name|onTaskFinished
argument_list|)
decl_stmt|;
if|if
condition|(
name|childNodes
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|childNodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"cancelling task {} with no children"
argument_list|,
name|cancellableTask
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|cancellableTask
operator|.
name|taskInfo
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"cancelling task {} with children on nodes [{}]"
argument_list|,
name|cancellableTask
operator|.
name|getId
argument_list|()
argument_list|,
name|childNodes
argument_list|)
expr_stmt|;
name|setBanOnNodes
argument_list|(
name|request
operator|.
name|getReason
argument_list|()
argument_list|,
name|cancellableTask
argument_list|,
name|childNodes
argument_list|,
name|banLock
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|cancellableTask
operator|.
name|taskInfo
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"task {} is already cancelled"
argument_list|,
name|cancellableTask
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"task with id "
operator|+
name|cancellableTask
operator|.
name|getId
argument_list|()
operator|+
literal|" is already cancelled"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|accumulateExceptions
specifier|protected
name|boolean
name|accumulateExceptions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|setBanOnNodes
specifier|private
name|void
name|setBanOnNodes
parameter_list|(
name|String
name|reason
parameter_list|,
name|CancellableTask
name|task
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|,
name|BanLock
name|banLock
parameter_list|)
block|{
name|sendSetBanRequest
argument_list|(
name|nodes
argument_list|,
name|BanParentTaskRequest
operator|.
name|createSetBanParentTaskRequest
argument_list|(
operator|new
name|TaskId
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|reason
argument_list|)
argument_list|,
name|banLock
argument_list|)
expr_stmt|;
block|}
DECL|method|removeBanOnNodes
specifier|private
name|void
name|removeBanOnNodes
parameter_list|(
name|CancellableTask
name|task
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|)
block|{
name|sendRemoveBanRequest
argument_list|(
name|nodes
argument_list|,
name|BanParentTaskRequest
operator|.
name|createRemoveBanParentTaskRequest
argument_list|(
operator|new
name|TaskId
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|task
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|sendSetBanRequest
specifier|private
name|void
name|sendSetBanRequest
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|,
name|BanParentTaskRequest
name|request
parameter_list|,
name|BanLock
name|banLock
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|DiscoveryNode
name|discoveryNode
init|=
name|clusterState
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|discoveryNode
operator|!=
literal|null
condition|)
block|{
comment|// Check if node still in the cluster
name|logger
operator|.
name|debug
argument_list|(
literal|"Sending ban for tasks with the parent [{}] to the node [{}], ban [{}]"
argument_list|,
name|request
operator|.
name|parentTaskId
argument_list|,
name|node
argument_list|,
name|request
operator|.
name|ban
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|discoveryNode
argument_list|,
name|BAN_PARENT_ACTION_NAME
argument_list|,
name|request
argument_list|,
operator|new
name|EmptyTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|TransportResponse
operator|.
name|Empty
name|response
parameter_list|)
block|{
name|banLock
operator|.
name|onBanSet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
name|banLock
operator|.
name|onBanSet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|banLock
operator|.
name|onBanSet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Cannot send ban for tasks with the parent [{}] to the node [{}] - the node no longer in the cluster"
argument_list|,
name|request
operator|.
name|parentTaskId
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|sendRemoveBanRequest
specifier|private
name|void
name|sendRemoveBanRequest
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|,
name|BanParentTaskRequest
name|request
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|DiscoveryNode
name|discoveryNode
init|=
name|clusterState
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|discoveryNode
operator|!=
literal|null
condition|)
block|{
comment|// Check if node still in the cluster
name|logger
operator|.
name|debug
argument_list|(
literal|"Sending remove ban for tasks with the parent [{}] to the node [{}]"
argument_list|,
name|request
operator|.
name|parentTaskId
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|discoveryNode
argument_list|,
name|BAN_PARENT_ACTION_NAME
argument_list|,
name|request
argument_list|,
name|EmptyTransportResponseHandler
operator|.
name|INSTANCE_SAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Cannot send remove ban request for tasks with the parent [{}] to the node [{}] - the node no longer in "
operator|+
literal|"the cluster"
argument_list|,
name|request
operator|.
name|parentTaskId
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|BanLock
specifier|private
specifier|static
class|class
name|BanLock
block|{
DECL|field|finish
specifier|private
specifier|final
name|Consumer
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|finish
decl_stmt|;
DECL|field|counter
specifier|private
specifier|final
name|AtomicInteger
name|counter
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|AtomicReference
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodes
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|BanLock
specifier|public
name|BanLock
parameter_list|(
name|Consumer
argument_list|<
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|finish
parameter_list|)
block|{
name|counter
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|finish
operator|=
name|finish
expr_stmt|;
block|}
DECL|method|onBanSet
specifier|public
name|void
name|onBanSet
parameter_list|()
block|{
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|onTaskFinished
specifier|public
name|void
name|onTaskFinished
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|.
name|set
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|.
name|addAndGet
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
operator|==
literal|0
condition|)
block|{
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
block|{
name|finish
operator|.
name|accept
argument_list|(
name|nodes
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|BanParentTaskRequest
specifier|private
specifier|static
class|class
name|BanParentTaskRequest
extends|extends
name|TransportRequest
block|{
DECL|field|parentTaskId
specifier|private
name|TaskId
name|parentTaskId
decl_stmt|;
DECL|field|ban
specifier|private
name|boolean
name|ban
decl_stmt|;
DECL|field|reason
specifier|private
name|String
name|reason
decl_stmt|;
DECL|method|createSetBanParentTaskRequest
specifier|static
name|BanParentTaskRequest
name|createSetBanParentTaskRequest
parameter_list|(
name|TaskId
name|parentTaskId
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
return|return
operator|new
name|BanParentTaskRequest
argument_list|(
name|parentTaskId
argument_list|,
name|reason
argument_list|)
return|;
block|}
DECL|method|createRemoveBanParentTaskRequest
specifier|static
name|BanParentTaskRequest
name|createRemoveBanParentTaskRequest
parameter_list|(
name|TaskId
name|parentTaskId
parameter_list|)
block|{
return|return
operator|new
name|BanParentTaskRequest
argument_list|(
name|parentTaskId
argument_list|)
return|;
block|}
DECL|method|BanParentTaskRequest
specifier|private
name|BanParentTaskRequest
parameter_list|(
name|TaskId
name|parentTaskId
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|parentTaskId
operator|=
name|parentTaskId
expr_stmt|;
name|this
operator|.
name|ban
operator|=
literal|true
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
block|}
DECL|method|BanParentTaskRequest
specifier|private
name|BanParentTaskRequest
parameter_list|(
name|TaskId
name|parentTaskId
parameter_list|)
block|{
name|this
operator|.
name|parentTaskId
operator|=
name|parentTaskId
expr_stmt|;
name|this
operator|.
name|ban
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|BanParentTaskRequest
specifier|public
name|BanParentTaskRequest
parameter_list|()
block|{         }
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
name|ban
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
if|if
condition|(
name|ban
condition|)
block|{
name|reason
operator|=
name|in
operator|.
name|readString
argument_list|()
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
name|parentTaskId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|ban
argument_list|)
expr_stmt|;
if|if
condition|(
name|ban
condition|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|BanParentRequestHandler
class|class
name|BanParentRequestHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|BanParentTaskRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|BanParentTaskRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|request
operator|.
name|ban
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Received ban for the parent [{}] on the node [{}], reason: [{}]"
argument_list|,
name|request
operator|.
name|parentTaskId
argument_list|,
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|request
operator|.
name|reason
argument_list|)
expr_stmt|;
name|taskManager
operator|.
name|setBan
argument_list|(
name|request
operator|.
name|parentTaskId
argument_list|,
name|request
operator|.
name|reason
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Removing ban for the parent [{}] on the node [{}]"
argument_list|,
name|request
operator|.
name|parentTaskId
argument_list|,
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|taskManager
operator|.
name|removeBan
argument_list|(
name|request
operator|.
name|parentTaskId
argument_list|)
expr_stmt|;
block|}
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

