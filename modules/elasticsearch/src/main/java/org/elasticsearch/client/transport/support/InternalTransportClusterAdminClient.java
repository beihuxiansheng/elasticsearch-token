begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.transport.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|transport
operator|.
name|support
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
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthRequest
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
name|health
operator|.
name|ClusterHealthResponse
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
name|info
operator|.
name|NodesInfoRequest
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
name|info
operator|.
name|NodesInfoResponse
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
name|restart
operator|.
name|NodesRestartRequest
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
name|restart
operator|.
name|NodesRestartResponse
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
name|shutdown
operator|.
name|NodesShutdownRequest
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
name|shutdown
operator|.
name|NodesShutdownResponse
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
name|stats
operator|.
name|NodesStatsRequest
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
name|stats
operator|.
name|NodesStatsResponse
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
name|ping
operator|.
name|broadcast
operator|.
name|BroadcastPingRequest
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
name|ping
operator|.
name|broadcast
operator|.
name|BroadcastPingResponse
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
name|ping
operator|.
name|replication
operator|.
name|ReplicationPingRequest
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
name|ping
operator|.
name|replication
operator|.
name|ReplicationPingResponse
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
name|ping
operator|.
name|single
operator|.
name|SinglePingRequest
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
name|ping
operator|.
name|single
operator|.
name|SinglePingResponse
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
name|state
operator|.
name|ClusterStateRequest
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
name|state
operator|.
name|ClusterStateResponse
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
name|internal
operator|.
name|InternalClusterAdminClient
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
name|support
operator|.
name|AbstractClusterAdminClient
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
name|transport
operator|.
name|TransportClientNodesService
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClientTransportClusterHealthAction
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|ClientTransportNodesInfoAction
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
name|transport
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
operator|.
name|ClientTransportNodesRestartAction
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
name|transport
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
operator|.
name|ClientTransportNodesShutdownAction
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|ClientTransportNodesStatsAction
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|ping
operator|.
name|broadcast
operator|.
name|ClientTransportBroadcastPingAction
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|ping
operator|.
name|replication
operator|.
name|ClientTransportReplicationPingAction
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|ping
operator|.
name|single
operator|.
name|ClientTransportSinglePingAction
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
name|transport
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|state
operator|.
name|ClientTransportClusterStateAction
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
name|util
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|InternalTransportClusterAdminClient
specifier|public
class|class
name|InternalTransportClusterAdminClient
extends|extends
name|AbstractClusterAdminClient
implements|implements
name|InternalClusterAdminClient
block|{
DECL|field|nodesService
specifier|private
specifier|final
name|TransportClientNodesService
name|nodesService
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|clusterHealthAction
specifier|private
specifier|final
name|ClientTransportClusterHealthAction
name|clusterHealthAction
decl_stmt|;
DECL|field|clusterStateAction
specifier|private
specifier|final
name|ClientTransportClusterStateAction
name|clusterStateAction
decl_stmt|;
DECL|field|singlePingAction
specifier|private
specifier|final
name|ClientTransportSinglePingAction
name|singlePingAction
decl_stmt|;
DECL|field|replicationPingAction
specifier|private
specifier|final
name|ClientTransportReplicationPingAction
name|replicationPingAction
decl_stmt|;
DECL|field|broadcastPingAction
specifier|private
specifier|final
name|ClientTransportBroadcastPingAction
name|broadcastPingAction
decl_stmt|;
DECL|field|nodesInfoAction
specifier|private
specifier|final
name|ClientTransportNodesInfoAction
name|nodesInfoAction
decl_stmt|;
DECL|field|nodesStatsAction
specifier|private
specifier|final
name|ClientTransportNodesStatsAction
name|nodesStatsAction
decl_stmt|;
DECL|field|nodesShutdownAction
specifier|private
specifier|final
name|ClientTransportNodesShutdownAction
name|nodesShutdownAction
decl_stmt|;
DECL|field|nodesRestartAction
specifier|private
specifier|final
name|ClientTransportNodesRestartAction
name|nodesRestartAction
decl_stmt|;
DECL|method|InternalTransportClusterAdminClient
annotation|@
name|Inject
specifier|public
name|InternalTransportClusterAdminClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportClientNodesService
name|nodesService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClientTransportClusterHealthAction
name|clusterHealthAction
parameter_list|,
name|ClientTransportClusterStateAction
name|clusterStateAction
parameter_list|,
name|ClientTransportSinglePingAction
name|singlePingAction
parameter_list|,
name|ClientTransportReplicationPingAction
name|replicationPingAction
parameter_list|,
name|ClientTransportBroadcastPingAction
name|broadcastPingAction
parameter_list|,
name|ClientTransportNodesInfoAction
name|nodesInfoAction
parameter_list|,
name|ClientTransportNodesShutdownAction
name|nodesShutdownAction
parameter_list|,
name|ClientTransportNodesRestartAction
name|nodesRestartAction
parameter_list|,
name|ClientTransportNodesStatsAction
name|nodesStatsAction
parameter_list|)
block|{
name|this
operator|.
name|nodesService
operator|=
name|nodesService
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|clusterHealthAction
operator|=
name|clusterHealthAction
expr_stmt|;
name|this
operator|.
name|clusterStateAction
operator|=
name|clusterStateAction
expr_stmt|;
name|this
operator|.
name|nodesInfoAction
operator|=
name|nodesInfoAction
expr_stmt|;
name|this
operator|.
name|nodesShutdownAction
operator|=
name|nodesShutdownAction
expr_stmt|;
name|this
operator|.
name|nodesRestartAction
operator|=
name|nodesRestartAction
expr_stmt|;
name|this
operator|.
name|singlePingAction
operator|=
name|singlePingAction
expr_stmt|;
name|this
operator|.
name|replicationPingAction
operator|=
name|replicationPingAction
expr_stmt|;
name|this
operator|.
name|broadcastPingAction
operator|=
name|broadcastPingAction
expr_stmt|;
name|this
operator|.
name|nodesStatsAction
operator|=
name|nodesStatsAction
expr_stmt|;
block|}
DECL|method|threadPool
annotation|@
name|Override
specifier|public
name|ThreadPool
name|threadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
DECL|method|health
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|health
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|ClusterHealthResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|clusterHealthAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|health
annotation|@
name|Override
specifier|public
name|void
name|health
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|clusterHealthAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|state
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
name|state
parameter_list|(
specifier|final
name|ClusterStateRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|clusterStateAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|state
annotation|@
name|Override
specifier|public
name|void
name|state
parameter_list|(
specifier|final
name|ClusterStateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|clusterStateAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SinglePingResponse
argument_list|>
name|ping
parameter_list|(
specifier|final
name|SinglePingRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|SinglePingResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|SinglePingResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|singlePingAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|void
name|ping
parameter_list|(
specifier|final
name|SinglePingRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SinglePingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|singlePingAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|BroadcastPingResponse
argument_list|>
name|ping
parameter_list|(
specifier|final
name|BroadcastPingRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|BroadcastPingResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|BroadcastPingResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|broadcastPingAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|void
name|ping
parameter_list|(
specifier|final
name|BroadcastPingRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|BroadcastPingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|broadcastPingAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ReplicationPingResponse
argument_list|>
name|ping
parameter_list|(
specifier|final
name|ReplicationPingRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|ReplicationPingResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|ReplicationPingResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|replicationPingAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|void
name|ping
parameter_list|(
specifier|final
name|ReplicationPingRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ReplicationPingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|replicationPingAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|nodesInfo
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesInfoResponse
argument_list|>
name|nodesInfo
parameter_list|(
specifier|final
name|NodesInfoRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|NodesInfoResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesInfoResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|nodesInfoAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|nodesInfo
annotation|@
name|Override
specifier|public
name|void
name|nodesInfo
parameter_list|(
specifier|final
name|NodesInfoRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|NodesInfoResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|nodesInfoAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|nodesStats
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesStatsResponse
argument_list|>
name|nodesStats
parameter_list|(
specifier|final
name|NodesStatsRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|NodesStatsResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesStatsResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|nodesStatsAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|nodesStats
annotation|@
name|Override
specifier|public
name|void
name|nodesStats
parameter_list|(
specifier|final
name|NodesStatsRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|NodesStatsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|nodesStatsAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|nodesShutdown
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesShutdownResponse
argument_list|>
name|nodesShutdown
parameter_list|(
specifier|final
name|NodesShutdownRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|NodesShutdownResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesShutdownResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|nodesShutdownAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|nodesShutdown
annotation|@
name|Override
specifier|public
name|void
name|nodesShutdown
parameter_list|(
specifier|final
name|NodesShutdownRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|NodesShutdownResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|Void
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|nodesShutdownAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|nodesRestart
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesRestartResponse
argument_list|>
name|nodesRestart
parameter_list|(
specifier|final
name|NodesRestartRequest
name|request
parameter_list|)
block|{
return|return
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionFuture
argument_list|<
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
operator|.
name|NodesRestartResponse
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|NodesRestartResponse
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
return|return
name|nodesRestartAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|nodesRestart
annotation|@
name|Override
specifier|public
name|void
name|nodesRestart
parameter_list|(
specifier|final
name|NodesRestartRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|NodesRestartResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesService
operator|.
name|execute
argument_list|(
operator|new
name|TransportClientNodesService
operator|.
name|NodeCallback
argument_list|<
name|ActionFuture
argument_list|<
name|Void
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ActionFuture
argument_list|<
name|Void
argument_list|>
name|doWithNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|nodesRestartAction
operator|.
name|execute
argument_list|(
name|node
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

