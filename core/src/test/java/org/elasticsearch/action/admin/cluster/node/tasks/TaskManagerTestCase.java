begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.tasks
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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|tasks
operator|.
name|cancel
operator|.
name|TransportCancelTasksAction
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
name|TransportListTasksAction
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
name|nodes
operator|.
name|BaseNodeRequest
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
name|nodes
operator|.
name|BaseNodeResponse
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
name|nodes
operator|.
name|BaseNodesRequest
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
name|nodes
operator|.
name|BaseNodesResponse
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
name|nodes
operator|.
name|TransportNodesAction
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
name|replication
operator|.
name|ClusterStateCreationUtils
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
name|ClusterName
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
name|ClusterService
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|NamedWriteableRegistry
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
name|lease
operator|.
name|Releasable
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
name|TaskManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|cluster
operator|.
name|TestClusterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|tasks
operator|.
name|MockTaskManager
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
name|TransportService
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
name|local
operator|.
name|LocalTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|HashSet
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
name|concurrent
operator|.
name|TimeUnit
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
name|AtomicReferenceArray
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
name|Supplier
import|;
end_import

begin_comment
comment|/**  * The test case for unit testing task manager and related transport actions  */
end_comment

begin_class
DECL|class|TaskManagerTestCase
specifier|public
specifier|abstract
class|class
name|TaskManagerTestCase
extends|extends
name|ESTestCase
block|{
DECL|field|threadPool
specifier|protected
specifier|static
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|clusterName
specifier|public
specifier|static
specifier|final
name|ClusterName
name|clusterName
init|=
operator|new
name|ClusterName
argument_list|(
literal|"test-cluster"
argument_list|)
decl_stmt|;
DECL|field|testNodes
specifier|protected
name|TestNode
index|[]
name|testNodes
decl_stmt|;
DECL|field|nodesCount
specifier|protected
name|int
name|nodesCount
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
block|{
name|threadPool
operator|=
operator|new
name|ThreadPool
argument_list|(
name|TransportTasksActionTests
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|threadPool
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|threadPool
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|setupTestNodes
specifier|public
name|void
name|setupTestNodes
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|nodesCount
operator|=
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|)
expr_stmt|;
name|testNodes
operator|=
operator|new
name|TestNode
index|[
name|nodesCount
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|testNodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|testNodes
index|[
name|i
index|]
operator|=
operator|new
name|TestNode
argument_list|(
literal|"node"
operator|+
name|i
argument_list|,
name|threadPool
argument_list|,
name|settings
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|shutdownTestNodes
specifier|public
specifier|final
name|void
name|shutdownTestNodes
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|TestNode
name|testNode
range|:
name|testNodes
control|)
block|{
name|testNode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|NodeResponse
specifier|static
class|class
name|NodeResponse
extends|extends
name|BaseNodeResponse
block|{
DECL|method|NodeResponse
specifier|protected
name|NodeResponse
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|NodeResponse
specifier|protected
name|NodeResponse
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NodesResponse
specifier|static
class|class
name|NodesResponse
extends|extends
name|BaseNodesResponse
argument_list|<
name|NodeResponse
argument_list|>
block|{
DECL|field|failureCount
specifier|private
name|int
name|failureCount
decl_stmt|;
DECL|method|NodesResponse
specifier|protected
name|NodesResponse
parameter_list|(
name|ClusterName
name|clusterName
parameter_list|,
name|NodeResponse
index|[]
name|nodes
parameter_list|,
name|int
name|failureCount
parameter_list|)
block|{
name|super
argument_list|(
name|clusterName
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|failureCount
operator|=
name|failureCount
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
name|failureCount
operator|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|writeVInt
argument_list|(
name|failureCount
argument_list|)
expr_stmt|;
block|}
DECL|method|failureCount
specifier|public
name|int
name|failureCount
parameter_list|()
block|{
return|return
name|failureCount
return|;
block|}
block|}
comment|/**      * Simulates node-based task that can be used to block node tasks so they are guaranteed to be registered by task manager      */
DECL|class|AbstractTestNodesAction
specifier|abstract
class|class
name|AbstractTestNodesAction
parameter_list|<
name|NodesRequest
extends|extends
name|BaseNodesRequest
parameter_list|<
name|NodesRequest
parameter_list|>
parameter_list|,
name|NodeRequest
extends|extends
name|BaseNodeRequest
parameter_list|>
extends|extends
name|TransportNodesAction
argument_list|<
name|NodesRequest
argument_list|,
name|NodesResponse
argument_list|,
name|NodeRequest
argument_list|,
name|NodeResponse
argument_list|>
block|{
DECL|method|AbstractTestNodesAction
name|AbstractTestNodesAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|actionName
parameter_list|,
name|ClusterName
name|clusterName
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
name|Supplier
argument_list|<
name|NodesRequest
argument_list|>
name|request
parameter_list|,
name|Supplier
argument_list|<
name|NodeRequest
argument_list|>
name|nodeRequest
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|actionName
argument_list|,
name|clusterName
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
operator|new
name|ActionFilters
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|)
argument_list|,
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|,
name|request
argument_list|,
name|nodeRequest
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|NodesResponse
name|newResponse
parameter_list|(
name|NodesRequest
name|request
parameter_list|,
name|AtomicReferenceArray
name|responses
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|NodeResponse
argument_list|>
name|nodesList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|failureCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|responses
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|resp
init|=
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|instanceof
name|NodeResponse
condition|)
block|{
comment|// will also filter out null response for unallocated ones
name|nodesList
operator|.
name|add
argument_list|(
operator|(
name|NodeResponse
operator|)
name|resp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resp
operator|instanceof
name|FailedNodeException
condition|)
block|{
name|failureCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unknown response type [{}], expected NodeLocalGatewayMetaState or FailedNodeException"
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodesResponse
argument_list|(
name|clusterName
argument_list|,
name|nodesList
operator|.
name|toArray
argument_list|(
operator|new
name|NodeResponse
index|[
name|nodesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|failureCount
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeResponse
specifier|protected
name|NodeResponse
name|newNodeResponse
parameter_list|()
block|{
return|return
operator|new
name|NodeResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodeOperation
specifier|protected
specifier|abstract
name|NodeResponse
name|nodeOperation
parameter_list|(
name|NodeRequest
name|request
parameter_list|)
function_decl|;
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
block|}
DECL|class|TestNode
specifier|public
specifier|static
class|class
name|TestNode
implements|implements
name|Releasable
block|{
DECL|method|TestNode
specifier|public
name|TestNode
parameter_list|(
name|String
name|name
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Settings
name|settings
parameter_list|)
block|{
name|transportService
operator|=
operator|new
name|TransportService
argument_list|(
name|settings
argument_list|,
operator|new
name|LocalTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
argument_list|,
name|threadPool
argument_list|,
operator|new
name|NamedWriteableRegistry
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|TaskManager
name|createTaskManager
parameter_list|()
block|{
if|if
condition|(
name|MockTaskManager
operator|.
name|USE_MOCK_TASK_MANAGER_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
condition|)
block|{
return|return
operator|new
name|MockTaskManager
argument_list|(
name|settings
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|createTaskManager
argument_list|()
return|;
block|}
block|}
block|}
expr_stmt|;
name|transportService
operator|.
name|start
argument_list|()
expr_stmt|;
name|clusterService
operator|=
operator|new
name|TestClusterService
argument_list|(
name|threadPool
argument_list|,
name|transportService
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|add
argument_list|(
name|transportService
operator|.
name|getTaskManager
argument_list|()
argument_list|)
expr_stmt|;
name|discoveryNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|name
argument_list|,
name|transportService
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
init|=
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|ActionFilters
name|actionFilters
init|=
operator|new
name|ActionFilters
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|transportListTasksAction
operator|=
operator|new
name|TransportListTasksAction
argument_list|(
name|settings
argument_list|,
name|clusterName
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
argument_list|)
expr_stmt|;
name|transportCancelTasksAction
operator|=
operator|new
name|TransportCancelTasksAction
argument_list|(
name|settings
argument_list|,
name|clusterName
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
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
block|}
DECL|field|clusterService
specifier|public
specifier|final
name|TestClusterService
name|clusterService
decl_stmt|;
DECL|field|transportService
specifier|public
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|discoveryNode
specifier|public
specifier|final
name|DiscoveryNode
name|discoveryNode
decl_stmt|;
DECL|field|transportListTasksAction
specifier|public
specifier|final
name|TransportListTasksAction
name|transportListTasksAction
decl_stmt|;
DECL|field|transportCancelTasksAction
specifier|public
specifier|final
name|TransportCancelTasksAction
name|transportCancelTasksAction
decl_stmt|;
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|transportService
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|connectNodes
specifier|public
specifier|static
name|void
name|connectNodes
parameter_list|(
name|TestNode
modifier|...
name|nodes
parameter_list|)
block|{
name|DiscoveryNode
index|[]
name|discoveryNodes
init|=
operator|new
name|DiscoveryNode
index|[
name|nodes
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|discoveryNodes
index|[
name|i
index|]
operator|=
name|nodes
index|[
name|i
index|]
operator|.
name|discoveryNode
expr_stmt|;
block|}
name|DiscoveryNode
name|master
init|=
name|discoveryNodes
index|[
literal|0
index|]
decl_stmt|;
for|for
control|(
name|TestNode
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|node
operator|.
name|discoveryNode
argument_list|,
name|master
argument_list|,
name|discoveryNodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|TestNode
name|nodeA
range|:
name|nodes
control|)
block|{
for|for
control|(
name|TestNode
name|nodeB
range|:
name|nodes
control|)
block|{
name|nodeA
operator|.
name|transportService
operator|.
name|connectToNode
argument_list|(
name|nodeB
operator|.
name|discoveryNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setupListeners
specifier|public
specifier|static
name|RecordingTaskManagerListener
index|[]
name|setupListeners
parameter_list|(
name|TestNode
index|[]
name|nodes
parameter_list|,
name|String
modifier|...
name|actionMasks
parameter_list|)
block|{
name|RecordingTaskManagerListener
index|[]
name|listeners
init|=
operator|new
name|RecordingTaskManagerListener
index|[
name|nodes
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|listeners
index|[
name|i
index|]
operator|=
operator|new
name|RecordingTaskManagerListener
argument_list|(
name|nodes
index|[
name|i
index|]
operator|.
name|discoveryNode
argument_list|,
name|actionMasks
argument_list|)
expr_stmt|;
operator|(
call|(
name|MockTaskManager
call|)
argument_list|(
name|nodes
index|[
name|i
index|]
operator|.
name|clusterService
operator|.
name|getTaskManager
argument_list|()
argument_list|)
operator|)
operator|.
name|addListener
argument_list|(
name|listeners
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|listeners
return|;
block|}
block|}
end_class

end_unit

