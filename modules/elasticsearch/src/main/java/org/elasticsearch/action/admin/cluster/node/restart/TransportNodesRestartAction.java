begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.restart
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
name|restart
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
name|ElasticSearchIllegalStateException
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
name|TransportActions
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
name|NodeOperationRequest
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
name|TransportNodesOperationAction
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
name|node
operator|.
name|Node
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
name|util
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
name|util
operator|.
name|guice
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
name|util
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
name|util
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
name|util
operator|.
name|settings
operator|.
name|Settings
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|TimeValue
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportNodesRestartAction
specifier|public
class|class
name|TransportNodesRestartAction
extends|extends
name|TransportNodesOperationAction
argument_list|<
name|NodesRestartRequest
argument_list|,
name|NodesRestartResponse
argument_list|,
name|TransportNodesRestartAction
operator|.
name|NodeRestartRequest
argument_list|,
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
argument_list|>
block|{
DECL|field|node
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
DECL|field|disabled
specifier|private
specifier|final
name|boolean
name|disabled
decl_stmt|;
DECL|method|TransportNodesRestartAction
annotation|@
name|Inject
specifier|public
name|TransportNodesRestartAction
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
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|super
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|disabled
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"disabled"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|transportAction
annotation|@
name|Override
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|TransportActions
operator|.
name|Admin
operator|.
name|Cluster
operator|.
name|Node
operator|.
name|RESTART
return|;
block|}
DECL|method|transportNodeAction
annotation|@
name|Override
specifier|protected
name|String
name|transportNodeAction
parameter_list|()
block|{
return|return
literal|"/cluster/nodes/restart/node"
return|;
block|}
DECL|method|newResponse
annotation|@
name|Override
specifier|protected
name|NodesRestartResponse
name|newResponse
parameter_list|(
name|NodesRestartRequest
name|nodesShutdownRequest
parameter_list|,
name|AtomicReferenceArray
name|responses
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
argument_list|>
name|nodeRestartResponses
init|=
name|newArrayList
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
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
condition|)
block|{
name|nodeRestartResponses
operator|.
name|add
argument_list|(
operator|(
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
operator|)
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodesRestartResponse
argument_list|(
name|clusterName
argument_list|,
name|nodeRestartResponses
operator|.
name|toArray
argument_list|(
operator|new
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
index|[
name|nodeRestartResponses
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|newRequest
annotation|@
name|Override
specifier|protected
name|NodesRestartRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|NodesRestartRequest
argument_list|()
return|;
block|}
DECL|method|newNodeRequest
annotation|@
name|Override
specifier|protected
name|NodeRestartRequest
name|newNodeRequest
parameter_list|()
block|{
return|return
operator|new
name|NodeRestartRequest
argument_list|()
return|;
block|}
DECL|method|newNodeRequest
annotation|@
name|Override
specifier|protected
name|NodeRestartRequest
name|newNodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|NodesRestartRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|NodeRestartRequest
argument_list|(
name|nodeId
argument_list|,
name|request
operator|.
name|delay
argument_list|)
return|;
block|}
DECL|method|newNodeResponse
annotation|@
name|Override
specifier|protected
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
name|newNodeResponse
parameter_list|()
block|{
return|return
operator|new
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
argument_list|()
return|;
block|}
DECL|method|nodeOperation
annotation|@
name|Override
specifier|protected
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
name|nodeOperation
parameter_list|(
name|NodeRestartRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|disabled
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Restart is disabled"
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Restarting in [{}]"
argument_list|,
name|request
operator|.
name|delay
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|schedule
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
name|boolean
name|restartWithWrapper
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"elasticsearch-service"
argument_list|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Class
name|wrapperManager
init|=
name|settings
operator|.
name|getClassLoader
argument_list|()
operator|.
name|loadClass
argument_list|(
literal|"org.tanukisoftware.wrapper.WrapperManager"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Initiating requested restart (using service)"
argument_list|)
expr_stmt|;
name|wrapperManager
operator|.
name|getMethod
argument_list|(
literal|"restartAndReturn"
argument_list|)
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|restartWithWrapper
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|restartWithWrapper
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initiating requested restart"
argument_list|)
expr_stmt|;
try|try
block|{
name|node
operator|.
name|stop
argument_list|()
expr_stmt|;
name|node
operator|.
name|start
argument_list|()
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
name|warn
argument_list|(
literal|"Failed to restart"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
name|request
operator|.
name|delay
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
return|return
operator|new
name|NodesRestartResponse
operator|.
name|NodeRestartResponse
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
argument_list|)
return|;
block|}
DECL|method|accumulateExceptions
annotation|@
name|Override
specifier|protected
name|boolean
name|accumulateExceptions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|class|NodeRestartRequest
specifier|protected
specifier|static
class|class
name|NodeRestartRequest
extends|extends
name|NodeOperationRequest
block|{
DECL|field|delay
name|TimeValue
name|delay
decl_stmt|;
DECL|method|NodeRestartRequest
specifier|private
name|NodeRestartRequest
parameter_list|()
block|{         }
DECL|method|NodeRestartRequest
specifier|private
name|NodeRestartRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|TimeValue
name|delay
parameter_list|)
block|{
name|super
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|delay
operator|=
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|delay
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

