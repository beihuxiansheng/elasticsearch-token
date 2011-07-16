begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.shutdown
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
name|shutdown
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
name|master
operator|.
name|TransportMasterNodeOperationAction
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
name|collect
operator|.
name|Sets
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|VoidStreamable
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
name|BaseTransportRequestHandler
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
name|VoidTransportResponseHandler
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
name|CountDownLatch
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportNodesShutdownAction
specifier|public
class|class
name|TransportNodesShutdownAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|NodesShutdownRequest
argument_list|,
name|NodesShutdownResponse
argument_list|>
block|{
DECL|field|node
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|disabled
specifier|private
specifier|final
name|boolean
name|disabled
decl_stmt|;
DECL|field|delay
specifier|private
specifier|final
name|TimeValue
name|delay
decl_stmt|;
DECL|method|TransportNodesShutdownAction
annotation|@
name|Inject
specifier|public
name|TransportNodesShutdownAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|Node
name|node
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
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
name|this
operator|.
name|delay
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"delay"
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|.
name|registerHandler
argument_list|(
name|NodeShutdownRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|NodeShutdownRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|executor
annotation|@
name|Override
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|CACHED
return|;
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
name|SHUTDOWN
return|;
block|}
DECL|method|newRequest
annotation|@
name|Override
specifier|protected
name|NodesShutdownRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|NodesShutdownRequest
argument_list|()
return|;
block|}
DECL|method|newResponse
annotation|@
name|Override
specifier|protected
name|NodesShutdownResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|NodesShutdownResponse
argument_list|()
return|;
block|}
DECL|method|processBeforeDelegationToMaster
annotation|@
name|Override
specifier|protected
name|void
name|processBeforeDelegationToMaster
parameter_list|(
name|NodesShutdownRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|String
index|[]
name|nodesIds
init|=
name|request
operator|.
name|nodesIds
decl_stmt|;
if|if
condition|(
name|nodesIds
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodesIds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// replace the _local one, since it looses its meaning when going over to the master...
if|if
condition|(
literal|"_local"
operator|.
name|equals
argument_list|(
name|nodesIds
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|nodesIds
index|[
name|i
index|]
operator|=
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|masterOperation
annotation|@
name|Override
specifier|protected
name|NodesShutdownResponse
name|masterOperation
parameter_list|(
specifier|final
name|NodesShutdownRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
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
literal|"Shutdown is disabled"
argument_list|)
throw|;
block|}
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|isAllNodes
argument_list|(
name|request
operator|.
name|nodesIds
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[cluster_shutdown]: requested, shutting down in [{}]"
argument_list|,
name|request
operator|.
name|delay
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|addAll
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|dataNodes
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|addAll
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodes
argument_list|()
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
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
name|Thread
operator|.
name|sleep
argument_list|(
name|request
operator|.
name|delay
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
comment|// first, stop the cluster service
name|logger
operator|.
name|trace
argument_list|(
literal|"[cluster_shutdown]: stopping the cluster service so no re-routing will occur"
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|stop
argument_list|()
expr_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|state
operator|.
name|nodes
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|)
condition|)
block|{
comment|// don't shutdown the master yet...
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[cluster_shutdown]: sending shutdown request to [{}]"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|NodeShutdownRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|NodeShutdownRequest
argument_list|(
name|request
operator|.
name|exit
argument_list|)
argument_list|,
operator|new
name|VoidTransportResponseHandler
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
name|VoidStreamable
name|response
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[cluster_shutdown]: received shutdown response from [{}]"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
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
name|logger
operator|.
name|warn
argument_list|(
literal|"[cluster_shutdown]: received failed shutdown response from [{}]"
argument_list|,
name|exp
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"[cluster_shutdown]: done shutting down all nodes except master, proceeding to master"
argument_list|)
expr_stmt|;
comment|// now, kill the master
name|logger
operator|.
name|trace
argument_list|(
literal|"[cluster_shutdown]: shutting down the master [{}]"
argument_list|,
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|,
name|NodeShutdownRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|NodeShutdownRequest
argument_list|(
name|request
operator|.
name|exit
argument_list|)
argument_list|,
operator|new
name|VoidTransportResponseHandler
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
name|VoidStreamable
name|response
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[cluster_shutdown]: received shutdown response from master"
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
name|logger
operator|.
name|warn
argument_list|(
literal|"[cluster_shutdown]: received failed shutdown response master"
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
index|[]
name|nodesIds
init|=
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|resolveNodes
argument_list|(
name|request
operator|.
name|nodesIds
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"[partial_cluster_shutdown]: requested, shutting down [{}] in [{}]"
argument_list|,
name|nodesIds
argument_list|,
name|request
operator|.
name|delay
argument_list|)
expr_stmt|;
for|for
control|(
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
name|state
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
name|node
operator|!=
literal|null
condition|)
block|{
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|Thread
name|t
init|=
operator|new
name|Thread
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
name|Thread
operator|.
name|sleep
argument_list|(
name|request
operator|.
name|delay
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|nodesIds
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
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
name|state
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
name|node
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[partial_cluster_shutdown]: no node to shutdown for node_id [{}]"
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"[partial_cluster_shutdown]: sending shutdown request to [{}]"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|NodeShutdownRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|NodeShutdownRequest
argument_list|(
name|request
operator|.
name|exit
argument_list|)
argument_list|,
operator|new
name|VoidTransportResponseHandler
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
name|VoidStreamable
name|response
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[partial_cluster_shutdown]: received shutdown response from [{}]"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
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
name|logger
operator|.
name|warn
argument_list|(
literal|"[partial_cluster_shutdown]: received failed shutdown response from [{}]"
argument_list|,
name|exp
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"[partial_cluster_shutdown]: done shutting down [{}]"
argument_list|,
operator|(
operator|(
name|Object
operator|)
name|nodesIds
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NodesShutdownResponse
argument_list|(
name|clusterName
argument_list|,
name|nodes
operator|.
name|toArray
argument_list|(
operator|new
name|DiscoveryNode
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|class|NodeShutdownRequestHandler
specifier|private
class|class
name|NodeShutdownRequestHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|NodeShutdownRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"/cluster/nodes/shutdown/node"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|NodeShutdownRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|NodeShutdownRequest
argument_list|()
return|;
block|}
DECL|method|executor
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
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|NodeShutdownRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
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
literal|"Shutdown is disabled"
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"shutting down in [{}]"
argument_list|,
name|delay
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
operator|new
name|Thread
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
name|Thread
operator|.
name|sleep
argument_list|(
name|delay
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
if|if
condition|(
operator|!
name|request
operator|.
name|exit
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"initiating requested shutdown (no exit)..."
argument_list|)
expr_stmt|;
try|try
block|{
name|node
operator|.
name|close
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
literal|"Failed to shutdown"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|boolean
name|shutdownWithWrapper
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
literal|"initiating requested shutdown (using service)"
argument_list|)
expr_stmt|;
name|wrapperManager
operator|.
name|getMethod
argument_list|(
literal|"stopAndReturn"
argument_list|,
name|int
operator|.
name|class
argument_list|)
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|shutdownWithWrapper
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
name|shutdownWithWrapper
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"initiating requested shutdown..."
argument_list|)
expr_stmt|;
try|try
block|{
name|node
operator|.
name|close
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
literal|"Failed to shutdown"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// make sure we initiate the shutdown hooks, so the Bootstrap#main thread will exit
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|t
operator|.
name|start
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|VoidStreamable
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NodeShutdownRequest
specifier|static
class|class
name|NodeShutdownRequest
implements|implements
name|Streamable
block|{
DECL|field|exit
name|boolean
name|exit
decl_stmt|;
DECL|method|NodeShutdownRequest
name|NodeShutdownRequest
parameter_list|()
block|{         }
DECL|method|NodeShutdownRequest
name|NodeShutdownRequest
parameter_list|(
name|boolean
name|exit
parameter_list|)
block|{
name|this
operator|.
name|exit
operator|=
name|exit
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
name|exit
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|exit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

