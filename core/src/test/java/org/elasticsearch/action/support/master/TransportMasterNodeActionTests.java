begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.master
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
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
name|ActionFuture
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
name|ActionRequestValidationException
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
name|ActionResponse
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
name|PlainActionFuture
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
name|ThreadedActionListener
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
name|NotMasterException
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
name|block
operator|.
name|ClusterBlock
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
name|block
operator|.
name|ClusterBlockException
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
name|block
operator|.
name|ClusterBlockLevel
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
name|block
operator|.
name|ClusterBlocks
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
name|transport
operator|.
name|DummyTransportAddress
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
name|discovery
operator|.
name|Discovery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|MasterNotDiscoveredException
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
name|RestStatus
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
name|transport
operator|.
name|CapturingTransport
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
name|ConnectTransportException
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
name|Before
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
name|ExecutionException
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|instanceOf
import|;
end_import

begin_class
DECL|class|TransportMasterNodeActionTests
specifier|public
class|class
name|TransportMasterNodeActionTests
extends|extends
name|ESTestCase
block|{
DECL|field|threadPool
specifier|private
specifier|static
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|clusterService
specifier|private
name|TestClusterService
name|clusterService
decl_stmt|;
DECL|field|transportService
specifier|private
name|TransportService
name|transportService
decl_stmt|;
DECL|field|transport
specifier|private
name|CapturingTransport
name|transport
decl_stmt|;
DECL|field|localNode
specifier|private
name|DiscoveryNode
name|localNode
decl_stmt|;
DECL|field|remoteNode
specifier|private
name|DiscoveryNode
name|remoteNode
decl_stmt|;
DECL|field|allNodes
specifier|private
name|DiscoveryNode
index|[]
name|allNodes
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
literal|"TransportMasterNodeActionTests"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|transport
operator|=
operator|new
name|CapturingTransport
argument_list|()
expr_stmt|;
name|clusterService
operator|=
operator|new
name|TestClusterService
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
name|transportService
operator|=
operator|new
name|TransportService
argument_list|(
name|transport
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|start
argument_list|()
expr_stmt|;
name|transportService
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
name|localNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"local_node"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|remoteNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"remote_node"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
expr_stmt|;
name|allNodes
operator|=
operator|new
name|DiscoveryNode
index|[]
block|{
name|localNode
block|,
name|remoteNode
block|}
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
DECL|method|assertListenerThrows
parameter_list|<
name|T
parameter_list|>
name|void
name|assertListenerThrows
parameter_list|(
name|String
name|msg
parameter_list|,
name|ActionFuture
argument_list|<
name|?
argument_list|>
name|listener
parameter_list|,
name|Class
argument_list|<
name|?
argument_list|>
name|klass
parameter_list|)
throws|throws
name|InterruptedException
block|{
try|try
block|{
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|klass
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
extends|extends
name|MasterNodeRequest
argument_list|<
name|Request
argument_list|>
block|{
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
block|}
DECL|class|Response
class|class
name|Response
extends|extends
name|ActionResponse
block|{}
DECL|class|Action
class|class
name|Action
extends|extends
name|TransportMasterNodeAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
block|{
DECL|method|Action
name|Action
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|actionName
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|actionName
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
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
name|Request
operator|::
operator|new
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|Task
name|task
parameter_list|,
specifier|final
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
comment|// remove unneeded threading by wrapping listener with SAME to prevent super.doExecute from wrapping it with LISTENER
name|super
operator|.
name|doExecute
argument_list|(
name|task
argument_list|,
name|request
argument_list|,
operator|new
name|ThreadedActionListener
argument_list|<>
argument_list|(
name|logger
argument_list|,
name|threadPool
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
comment|// very lightweight operation in memory, no need to fork to a thread
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|Response
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|Response
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|Response
argument_list|()
argument_list|)
expr_stmt|;
comment|// default implementation, overridden in specific tests
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// default implementation, overridden in specific tests
block|}
block|}
DECL|method|testLocalOperationWithoutBlocks
specifier|public
name|void
name|testLocalOperationWithoutBlocks
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
specifier|final
name|boolean
name|masterOperationFailure
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Throwable
name|exception
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
specifier|final
name|Response
name|response
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|masterOperation
parameter_list|(
name|Task
name|task
parameter_list|,
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|masterOperationFailure
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
block|}
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|masterOperationFailure
condition|)
block|{
try|try
block|{
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception but returned proper result"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|exception
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertThat
argument_list|(
name|listener
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testLocalOperationWithBlocks
specifier|public
name|void
name|testLocalOperationWithBlocks
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
specifier|final
name|boolean
name|retryableBlock
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|unblockBeforeTimeout
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
operator|.
name|masterNodeTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
name|unblockBeforeTimeout
condition|?
literal|60
else|:
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|ClusterBlock
name|block
init|=
operator|new
name|ClusterBlock
argument_list|(
literal|1
argument_list|,
literal|""
argument_list|,
name|retryableBlock
argument_list|,
literal|true
argument_list|,
name|randomFrom
argument_list|(
name|RestStatus
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
name|ClusterBlockLevel
operator|.
name|ALL
argument_list|)
decl_stmt|;
name|ClusterState
name|stateWithBlock
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
operator|.
name|blocks
argument_list|(
name|ClusterBlocks
operator|.
name|builder
argument_list|()
operator|.
name|addGlobalBlock
argument_list|(
name|block
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|stateWithBlock
argument_list|)
expr_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|Set
argument_list|<
name|ClusterBlock
argument_list|>
name|blocks
init|=
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|global
argument_list|()
decl_stmt|;
return|return
name|blocks
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|new
name|ClusterBlockException
argument_list|(
name|blocks
argument_list|)
return|;
block|}
block|}
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
if|if
condition|(
name|retryableBlock
operator|&&
name|unblockBeforeTimeout
condition|)
block|{
name|assertFalse
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
operator|.
name|blocks
argument_list|(
name|ClusterBlocks
operator|.
name|EMPTY_CLUSTER_BLOCK
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
return|return;
block|}
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|retryableBlock
condition|)
block|{
try|try
block|{
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception but returned proper result"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MasterNotDiscoveredException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|ClusterBlockException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertListenerThrows
argument_list|(
literal|"ClusterBlockException should be thrown"
argument_list|,
name|listener
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testForceLocalOperation
specifier|public
name|void
name|testForceLocalOperation
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|randomFrom
argument_list|(
literal|null
argument_list|,
name|localNode
argument_list|,
name|remoteNode
argument_list|)
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|localExecute
parameter_list|(
name|Request
name|request
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|testMasterNotAvailable
specifier|public
name|void
name|testMasterNotAvailable
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
operator|.
name|masterNodeTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
literal|null
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|assertListenerThrows
argument_list|(
literal|"MasterNotDiscoveredException should be thrown"
argument_list|,
name|listener
argument_list|,
name|MasterNotDiscoveredException
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testMasterBecomesAvailable
specifier|public
name|void
name|testMasterBecomesAvailable
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
literal|null
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
DECL|method|testDelegateToMaster
specifier|public
name|void
name|testDelegateToMaster
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|remoteNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|transport
operator|.
name|capturedRequests
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|CapturingTransport
operator|.
name|CapturedRequest
name|capturedRequest
init|=
name|transport
operator|.
name|capturedRequests
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|capturedRequest
operator|.
name|node
operator|.
name|isMasterNode
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|capturedRequest
operator|.
name|request
argument_list|,
name|equalTo
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|capturedRequest
operator|.
name|action
argument_list|,
name|equalTo
argument_list|(
literal|"testAction"
argument_list|)
argument_list|)
expr_stmt|;
name|Response
name|response
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|transport
operator|.
name|handleResponse
argument_list|(
name|capturedRequest
operator|.
name|requestId
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|listener
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDelegateToFailingMaster
specifier|public
name|void
name|testDelegateToFailingMaster
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|boolean
name|failsWithConnectTransportException
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
operator|.
name|masterNodeTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
name|failsWithConnectTransportException
condition|?
literal|60
else|:
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|remoteNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|transport
operator|.
name|capturedRequests
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|CapturingTransport
operator|.
name|CapturedRequest
name|capturedRequest
init|=
name|transport
operator|.
name|capturedRequests
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|capturedRequest
operator|.
name|node
operator|.
name|isMasterNode
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|capturedRequest
operator|.
name|request
argument_list|,
name|equalTo
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|capturedRequest
operator|.
name|action
argument_list|,
name|equalTo
argument_list|(
literal|"testAction"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|failsWithConnectTransportException
condition|)
block|{
name|transport
operator|.
name|handleRemoteError
argument_list|(
name|capturedRequest
operator|.
name|requestId
argument_list|,
operator|new
name|ConnectTransportException
argument_list|(
name|remoteNode
argument_list|,
literal|"Fake error"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Throwable
name|t
init|=
operator|new
name|Throwable
argument_list|()
decl_stmt|;
name|transport
operator|.
name|handleRemoteError
argument_list|(
name|capturedRequest
operator|.
name|requestId
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|listener
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected exception but returned proper result"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|ex
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getCause
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testMasterFailoverAfterStepDown
specifier|public
name|void
name|testMasterFailoverAfterStepDown
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|Request
name|request
init|=
operator|new
name|Request
argument_list|()
operator|.
name|masterNodeTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|listener
init|=
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Response
name|response
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|localNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
operator|new
name|Action
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|"testAction"
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|masterOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
comment|// The other node has become master, simulate failures of this node while publishing cluster state through ZenDiscovery
name|TransportMasterNodeActionTests
operator|.
name|this
operator|.
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|localNode
argument_list|,
name|remoteNode
argument_list|,
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
name|Throwable
name|failure
init|=
name|randomBoolean
argument_list|()
condition|?
operator|new
name|Discovery
operator|.
name|FailedToCommitClusterStateException
argument_list|(
literal|"Fake error"
argument_list|)
else|:
operator|new
name|NotMasterException
argument_list|(
literal|"Fake error"
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|transport
operator|.
name|capturedRequests
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|CapturingTransport
operator|.
name|CapturedRequest
name|capturedRequest
init|=
name|transport
operator|.
name|capturedRequests
argument_list|()
index|[
literal|0
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|capturedRequest
operator|.
name|node
operator|.
name|isMasterNode
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|capturedRequest
operator|.
name|request
argument_list|,
name|equalTo
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|capturedRequest
operator|.
name|action
argument_list|,
name|equalTo
argument_list|(
literal|"testAction"
argument_list|)
argument_list|)
expr_stmt|;
name|transport
operator|.
name|handleResponse
argument_list|(
name|capturedRequest
operator|.
name|requestId
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listener
operator|.
name|isDone
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|listener
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
