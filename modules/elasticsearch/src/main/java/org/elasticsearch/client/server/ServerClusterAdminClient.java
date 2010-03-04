begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.server
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|server
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|health
operator|.
name|TransportClusterHealthAction
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
name|info
operator|.
name|TransportNodesInfo
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
name|broadcast
operator|.
name|TransportBroadcastPingAction
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
name|replication
operator|.
name|TransportReplicationPingAction
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
name|ping
operator|.
name|single
operator|.
name|TransportSinglePingAction
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|state
operator|.
name|TransportClusterStateAction
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
name|ClusterAdminClient
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
name|component
operator|.
name|AbstractComponent
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
DECL|class|ServerClusterAdminClient
specifier|public
class|class
name|ServerClusterAdminClient
extends|extends
name|AbstractComponent
implements|implements
name|ClusterAdminClient
block|{
DECL|field|clusterHealthAction
specifier|private
specifier|final
name|TransportClusterHealthAction
name|clusterHealthAction
decl_stmt|;
DECL|field|clusterStateAction
specifier|private
specifier|final
name|TransportClusterStateAction
name|clusterStateAction
decl_stmt|;
DECL|field|singlePingAction
specifier|private
specifier|final
name|TransportSinglePingAction
name|singlePingAction
decl_stmt|;
DECL|field|broadcastPingAction
specifier|private
specifier|final
name|TransportBroadcastPingAction
name|broadcastPingAction
decl_stmt|;
DECL|field|replicationPingAction
specifier|private
specifier|final
name|TransportReplicationPingAction
name|replicationPingAction
decl_stmt|;
DECL|field|nodesInfo
specifier|private
specifier|final
name|TransportNodesInfo
name|nodesInfo
decl_stmt|;
DECL|method|ServerClusterAdminClient
annotation|@
name|Inject
specifier|public
name|ServerClusterAdminClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportClusterHealthAction
name|clusterHealthAction
parameter_list|,
name|TransportClusterStateAction
name|clusterStateAction
parameter_list|,
name|TransportSinglePingAction
name|singlePingAction
parameter_list|,
name|TransportBroadcastPingAction
name|broadcastPingAction
parameter_list|,
name|TransportReplicationPingAction
name|replicationPingAction
parameter_list|,
name|TransportNodesInfo
name|nodesInfo
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
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
name|nodesInfo
operator|=
name|nodesInfo
expr_stmt|;
name|this
operator|.
name|singlePingAction
operator|=
name|singlePingAction
expr_stmt|;
name|this
operator|.
name|broadcastPingAction
operator|=
name|broadcastPingAction
expr_stmt|;
name|this
operator|.
name|replicationPingAction
operator|=
name|replicationPingAction
expr_stmt|;
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
name|ClusterHealthRequest
name|request
parameter_list|)
block|{
return|return
name|clusterHealthAction
operator|.
name|execute
argument_list|(
name|request
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
name|ClusterHealthRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|clusterHealthAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
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
name|ClusterStateRequest
name|request
parameter_list|)
block|{
return|return
name|clusterStateAction
operator|.
name|execute
argument_list|(
name|request
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
name|ClusterStateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|clusterStateAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
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
name|SinglePingRequest
name|request
parameter_list|)
block|{
return|return
name|singlePingAction
operator|.
name|execute
argument_list|(
name|request
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
name|SinglePingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|SinglePingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|singlePingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
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
name|BroadcastPingRequest
name|request
parameter_list|)
block|{
return|return
name|broadcastPingAction
operator|.
name|execute
argument_list|(
name|request
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
name|BroadcastPingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|BroadcastPingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|broadcastPingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
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
name|ReplicationPingRequest
name|request
parameter_list|)
block|{
return|return
name|replicationPingAction
operator|.
name|execute
argument_list|(
name|request
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
name|ReplicationPingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ReplicationPingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|replicationPingAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
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
name|NodesInfoRequest
name|request
parameter_list|)
block|{
return|return
name|nodesInfo
operator|.
name|execute
argument_list|(
name|request
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
name|NodesInfoRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|NodesInfoResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|nodesInfo
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

