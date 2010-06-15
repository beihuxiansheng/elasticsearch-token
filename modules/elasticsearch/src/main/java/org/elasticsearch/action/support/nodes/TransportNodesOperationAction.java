begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.nodes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|nodes
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|NoSuchNodeException
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
name|BaseAction
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
name|*
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
name|AtomicReferenceArray
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|TransportNodesOperationAction
specifier|public
specifier|abstract
class|class
name|TransportNodesOperationAction
parameter_list|<
name|Request
extends|extends
name|NodesOperationRequest
parameter_list|,
name|Response
extends|extends
name|NodesOperationResponse
parameter_list|,
name|NodeRequest
extends|extends
name|NodeOperationRequest
parameter_list|,
name|NodeResponse
extends|extends
name|NodeOperationResponse
parameter_list|>
extends|extends
name|BaseAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
block|{
DECL|field|clusterName
specifier|protected
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|threadPool
specifier|protected
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|clusterService
specifier|protected
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|transportService
specifier|protected
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|method|TransportNodesOperationAction
annotation|@
name|Inject
specifier|public
name|TransportNodesOperationAction
parameter_list|(
name|Settings
name|settings
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
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|transportAction
argument_list|()
argument_list|,
operator|new
name|TransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|transportNodeAction
argument_list|()
argument_list|,
operator|new
name|NodeTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doExecute
annotation|@
name|Override
specifier|protected
name|void
name|doExecute
parameter_list|(
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
operator|new
name|AsyncAction
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|transportAction
specifier|protected
specifier|abstract
name|String
name|transportAction
parameter_list|()
function_decl|;
DECL|method|transportNodeAction
specifier|protected
specifier|abstract
name|String
name|transportNodeAction
parameter_list|()
function_decl|;
DECL|method|newRequest
specifier|protected
specifier|abstract
name|Request
name|newRequest
parameter_list|()
function_decl|;
DECL|method|newResponse
specifier|protected
specifier|abstract
name|Response
name|newResponse
parameter_list|(
name|Request
name|request
parameter_list|,
name|AtomicReferenceArray
name|nodesResponses
parameter_list|)
function_decl|;
DECL|method|newNodeRequest
specifier|protected
specifier|abstract
name|NodeRequest
name|newNodeRequest
parameter_list|()
function_decl|;
DECL|method|newNodeRequest
specifier|protected
specifier|abstract
name|NodeRequest
name|newNodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|Request
name|request
parameter_list|)
function_decl|;
DECL|method|newNodeResponse
specifier|protected
specifier|abstract
name|NodeResponse
name|newNodeResponse
parameter_list|()
function_decl|;
DECL|method|nodeOperation
specifier|protected
specifier|abstract
name|NodeResponse
name|nodeOperation
parameter_list|(
name|NodeRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|accumulateExceptions
specifier|protected
specifier|abstract
name|boolean
name|accumulateExceptions
parameter_list|()
function_decl|;
DECL|class|AsyncAction
specifier|private
class|class
name|AsyncAction
block|{
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|nodesIds
specifier|private
specifier|final
name|String
index|[]
name|nodesIds
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
decl_stmt|;
DECL|field|clusterState
specifier|private
specifier|final
name|ClusterState
name|clusterState
decl_stmt|;
DECL|field|responses
specifier|private
specifier|final
name|AtomicReferenceArray
argument_list|<
name|Object
argument_list|>
name|responses
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|AtomicInteger
name|index
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|counter
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|AsyncAction
specifier|private
name|AsyncAction
parameter_list|(
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
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|clusterState
operator|=
name|clusterService
operator|.
name|state
argument_list|()
expr_stmt|;
name|String
index|[]
name|nodesIds
init|=
name|request
operator|.
name|nodesIds
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodesIds
operator|==
literal|null
operator|||
name|nodesIds
operator|.
name|length
operator|==
literal|0
operator|||
operator|(
name|nodesIds
operator|.
name|length
operator|==
literal|1
operator|&&
name|nodesIds
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"_all"
argument_list|)
operator|)
condition|)
block|{
name|int
name|index
init|=
literal|0
decl_stmt|;
name|nodesIds
operator|=
operator|new
name|String
index|[
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|clusterState
operator|.
name|nodes
argument_list|()
control|)
block|{
name|nodesIds
index|[
name|index
operator|++
index|]
operator|=
name|node
operator|.
name|id
argument_list|()
expr_stmt|;
block|}
block|}
name|this
operator|.
name|nodesIds
operator|=
name|nodesIds
expr_stmt|;
name|this
operator|.
name|responses
operator|=
operator|new
name|AtomicReferenceArray
argument_list|<
name|Object
argument_list|>
argument_list|(
name|nodesIds
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|start
specifier|private
name|void
name|start
parameter_list|()
block|{
for|for
control|(
specifier|final
name|String
name|nodeId
range|:
name|nodesIds
control|)
block|{
specifier|final
name|DiscoveryNode
name|node
init|=
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeId
operator|.
name|equals
argument_list|(
literal|"_local"
argument_list|)
operator|||
name|nodeId
operator|.
name|equals
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|onOperation
argument_list|(
name|nodeOperation
argument_list|(
name|newNodeRequest
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|request
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeId
operator|.
name|equals
argument_list|(
literal|"_master"
argument_list|)
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|onOperation
argument_list|(
name|nodeOperation
argument_list|(
name|newNodeRequest
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|request
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|onFailure
argument_list|(
name|nodeId
argument_list|,
operator|new
name|NoSuchNodeException
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|NodeRequest
name|nodeRequest
init|=
name|newNodeRequest
argument_list|(
name|nodeId
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|transportNodeAction
argument_list|()
argument_list|,
name|nodeRequest
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|NodeResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|NodeResponse
name|newInstance
parameter_list|()
block|{
return|return
name|newNodeResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|NodeResponse
name|response
parameter_list|)
block|{
name|onOperation
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|onFailure
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|onOperation
specifier|private
name|void
name|onOperation
parameter_list|(
name|NodeResponse
name|nodeResponse
parameter_list|)
block|{
comment|// need two counters to avoid race conditions
name|responses
operator|.
name|set
argument_list|(
name|index
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
name|nodeResponse
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|.
name|incrementAndGet
argument_list|()
operator|==
name|responses
operator|.
name|length
argument_list|()
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|onFailure
specifier|private
name|void
name|onFailure
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|int
name|idx
init|=
name|index
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|accumulateExceptions
argument_list|()
condition|)
block|{
name|responses
operator|.
name|set
argument_list|(
name|idx
argument_list|,
operator|new
name|FailedNodeException
argument_list|(
name|nodeId
argument_list|,
literal|"Failed node ["
operator|+
name|nodeId
operator|+
literal|"]"
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|.
name|incrementAndGet
argument_list|()
operator|==
name|responses
operator|.
name|length
argument_list|()
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|finishHim
specifier|private
name|void
name|finishHim
parameter_list|()
block|{
if|if
condition|(
name|request
operator|.
name|listenerThreaded
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|newResponse
argument_list|(
name|request
argument_list|,
name|responses
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|newResponse
argument_list|(
name|request
argument_list|,
name|responses
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TransportHandler
specifier|private
class|class
name|TransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|Request
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|Request
name|newInstance
parameter_list|()
block|{
return|return
name|newRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|request
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|execute
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|Response
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|spawn
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|NodeTransportHandler
specifier|private
class|class
name|NodeTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|NodeRequest
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|NodeRequest
name|newInstance
parameter_list|()
block|{
return|return
name|newNodeRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|NodeRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|nodeOperation
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

