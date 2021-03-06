begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
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
name|HandledTransportAction
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
name|NodeShouldNotConnectException
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
name|TransportRequestOptions
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
name|TransportResponseHandler
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
name|Arrays
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
name|Objects
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

begin_class
DECL|class|TransportNodesAction
specifier|public
specifier|abstract
class|class
name|TransportNodesAction
parameter_list|<
name|NodesRequest
extends|extends
name|BaseNodesRequest
parameter_list|<
name|NodesRequest
parameter_list|>
parameter_list|,
name|NodesResponse
extends|extends
name|BaseNodesResponse
parameter_list|,
name|NodeRequest
extends|extends
name|BaseNodeRequest
parameter_list|,
name|NodeResponse
extends|extends
name|BaseNodeResponse
parameter_list|>
extends|extends
name|HandledTransportAction
argument_list|<
name|NodesRequest
argument_list|,
name|NodesResponse
argument_list|>
block|{
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
DECL|field|nodeResponseClass
specifier|protected
specifier|final
name|Class
argument_list|<
name|NodeResponse
argument_list|>
name|nodeResponseClass
decl_stmt|;
DECL|field|transportNodeAction
specifier|final
name|String
name|transportNodeAction
decl_stmt|;
DECL|method|TransportNodesAction
specifier|protected
name|TransportNodesAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|actionName
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
parameter_list|,
name|String
name|nodeExecutor
parameter_list|,
name|Class
argument_list|<
name|NodeResponse
argument_list|>
name|nodeResponseClass
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|actionName
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|clusterService
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transportService
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeResponseClass
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|nodeResponseClass
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportNodeAction
operator|=
name|actionName
operator|+
literal|"[n]"
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|transportNodeAction
argument_list|,
name|nodeRequest
argument_list|,
name|nodeExecutor
argument_list|,
operator|new
name|NodeTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
specifier|final
name|void
name|doExecute
parameter_list|(
name|NodesRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"attempt to execute a transport nodes operation without a task"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"task parameter is required for this operation"
argument_list|)
throw|;
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
name|NodesRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesResponse
argument_list|>
name|listener
parameter_list|)
block|{
operator|new
name|AsyncAction
argument_list|(
name|task
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|transportCompress
specifier|protected
name|boolean
name|transportCompress
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Map the responses into {@code nodeResponseClass} responses and {@link FailedNodeException}s.      *      * @param request The associated request.      * @param nodesResponses All node-level responses      * @return Never {@code null}.      * @throws NullPointerException if {@code nodesResponses} is {@code null}      * @see #newResponse(BaseNodesRequest, List, List)      */
DECL|method|newResponse
specifier|protected
name|NodesResponse
name|newResponse
parameter_list|(
name|NodesRequest
name|request
parameter_list|,
name|AtomicReferenceArray
name|nodesResponses
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|NodeResponse
argument_list|>
name|responses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FailedNodeException
argument_list|>
name|failures
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|nodesResponses
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|Object
name|response
init|=
name|nodesResponses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|instanceof
name|FailedNodeException
condition|)
block|{
name|failures
operator|.
name|add
argument_list|(
operator|(
name|FailedNodeException
operator|)
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|responses
operator|.
name|add
argument_list|(
name|nodeResponseClass
operator|.
name|cast
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newResponse
argument_list|(
name|request
argument_list|,
name|responses
argument_list|,
name|failures
argument_list|)
return|;
block|}
comment|/**      * Create a new {@link NodesResponse} (multi-node response).      *      * @param request The associated request.      * @param responses All successful node-level responses.      * @param failures All node-level failures.      * @return Never {@code null}.      * @throws NullPointerException if any parameter is {@code null}.      */
DECL|method|newResponse
specifier|protected
specifier|abstract
name|NodesResponse
name|newResponse
parameter_list|(
name|NodesRequest
name|request
parameter_list|,
name|List
argument_list|<
name|NodeResponse
argument_list|>
name|responses
parameter_list|,
name|List
argument_list|<
name|FailedNodeException
argument_list|>
name|failures
parameter_list|)
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
name|NodesRequest
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
function_decl|;
DECL|method|nodeOperation
specifier|protected
name|NodeResponse
name|nodeOperation
parameter_list|(
name|NodeRequest
name|request
parameter_list|,
name|Task
name|task
parameter_list|)
block|{
return|return
name|nodeOperation
argument_list|(
name|request
argument_list|)
return|;
block|}
comment|/**      * resolve node ids to concrete nodes of the incoming request      **/
DECL|method|resolveRequest
specifier|protected
name|void
name|resolveRequest
parameter_list|(
name|NodesRequest
name|request
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
assert|assert
name|request
operator|.
name|concreteNodes
argument_list|()
operator|==
literal|null
operator|:
literal|"request concreteNodes shouldn't be set"
assert|;
name|String
index|[]
name|nodesIds
init|=
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|resolveNodes
argument_list|(
name|request
operator|.
name|nodesIds
argument_list|()
argument_list|)
decl_stmt|;
name|request
operator|.
name|setConcreteNodes
argument_list|(
name|Arrays
operator|.
name|stream
argument_list|(
name|nodesIds
argument_list|)
operator|.
name|map
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|::
name|get
argument_list|)
operator|.
name|toArray
argument_list|(
name|DiscoveryNode
index|[]
operator|::
operator|new
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|AsyncAction
class|class
name|AsyncAction
block|{
DECL|field|request
specifier|private
specifier|final
name|NodesRequest
name|request
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
argument_list|<
name|NodesResponse
argument_list|>
name|listener
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
DECL|field|task
specifier|private
specifier|final
name|Task
name|task
decl_stmt|;
DECL|method|AsyncAction
name|AsyncAction
parameter_list|(
name|Task
name|task
parameter_list|,
name|NodesRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesResponse
argument_list|>
name|listener
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
if|if
condition|(
name|request
operator|.
name|concreteNodes
argument_list|()
operator|==
literal|null
condition|)
block|{
name|resolveRequest
argument_list|(
name|request
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|request
operator|.
name|concreteNodes
argument_list|()
operator|!=
literal|null
assert|;
block|}
name|this
operator|.
name|responses
operator|=
operator|new
name|AtomicReferenceArray
argument_list|<>
argument_list|(
name|request
operator|.
name|concreteNodes
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|start
name|void
name|start
parameter_list|()
block|{
specifier|final
name|DiscoveryNode
index|[]
name|nodes
init|=
name|request
operator|.
name|concreteNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// nothing to notify
name|threadPool
operator|.
name|generic
argument_list|()
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
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
argument_list|)
expr_stmt|;
return|return;
block|}
name|TransportRequestOptions
operator|.
name|Builder
name|builder
init|=
name|TransportRequestOptions
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|timeout
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|withTimeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|withCompress
argument_list|(
name|transportCompress
argument_list|()
argument_list|)
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|idx
init|=
name|i
decl_stmt|;
specifier|final
name|DiscoveryNode
name|node
init|=
name|nodes
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|String
name|nodeId
init|=
name|node
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
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
name|idx
argument_list|,
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
name|TransportRequest
name|nodeRequest
init|=
name|newNodeRequest
argument_list|(
name|nodeId
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
name|nodeRequest
operator|.
name|setParentTask
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
expr_stmt|;
block|}
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|transportNodeAction
argument_list|,
name|nodeRequest
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|TransportResponseHandler
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
name|idx
argument_list|,
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
name|TransportException
name|exp
parameter_list|)
block|{
name|onFailure
argument_list|(
name|idx
argument_list|,
name|node
operator|.
name|getId
argument_list|()
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|idx
argument_list|,
name|nodeId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|onOperation
specifier|private
name|void
name|onOperation
parameter_list|(
name|int
name|idx
parameter_list|,
name|NodeResponse
name|nodeResponse
parameter_list|)
block|{
name|responses
operator|.
name|set
argument_list|(
name|idx
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
name|int
name|idx
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
operator|&&
operator|!
operator|(
name|t
operator|instanceof
name|NodeShouldNotConnectException
operator|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
call|(
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"failed to execute on node [{}]"
argument_list|,
name|nodeId
argument_list|)
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
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
name|NodesResponse
name|finalResponse
decl_stmt|;
try|try
block|{
name|finalResponse
operator|=
name|newResponse
argument_list|(
name|request
argument_list|,
name|responses
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to combine responses from nodes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|listener
operator|.
name|onResponse
argument_list|(
name|finalResponse
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NodeTransportHandler
class|class
name|NodeTransportHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|NodeRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|NodeRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|,
name|Task
name|task
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
argument_list|,
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
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

