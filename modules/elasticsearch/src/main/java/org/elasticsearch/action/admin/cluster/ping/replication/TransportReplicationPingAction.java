begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.ping.replication
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
name|ping
operator|.
name|replication
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
name|replication
operator|.
name|TransportIndicesReplicationOperationAction
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
name|TransportService
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
DECL|class|TransportReplicationPingAction
specifier|public
class|class
name|TransportReplicationPingAction
extends|extends
name|TransportIndicesReplicationOperationAction
argument_list|<
name|ReplicationPingRequest
argument_list|,
name|ReplicationPingResponse
argument_list|,
name|IndexReplicationPingRequest
argument_list|,
name|IndexReplicationPingResponse
argument_list|,
name|ShardReplicationPingRequest
argument_list|,
name|ShardReplicationPingResponse
argument_list|>
block|{
DECL|method|TransportReplicationPingAction
annotation|@
name|Inject
specifier|public
name|TransportReplicationPingAction
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
name|TransportIndexReplicationPingAction
name|indexAction
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
argument_list|,
name|indexAction
argument_list|)
expr_stmt|;
block|}
DECL|method|newRequestInstance
annotation|@
name|Override
specifier|protected
name|ReplicationPingRequest
name|newRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|ReplicationPingRequest
argument_list|()
return|;
block|}
DECL|method|newResponseInstance
annotation|@
name|Override
specifier|protected
name|ReplicationPingResponse
name|newResponseInstance
parameter_list|(
name|ReplicationPingRequest
name|request
parameter_list|,
name|AtomicReferenceArray
name|indexResponses
parameter_list|)
block|{
name|ReplicationPingResponse
name|response
init|=
operator|new
name|ReplicationPingResponse
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
name|indexResponses
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|IndexReplicationPingResponse
name|indexResponse
init|=
operator|(
name|IndexReplicationPingResponse
operator|)
name|indexResponses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexResponse
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|indices
argument_list|()
operator|.
name|put
argument_list|(
name|indexResponse
operator|.
name|index
argument_list|()
argument_list|,
name|indexResponse
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
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
name|Ping
operator|.
name|REPLICATION
return|;
block|}
DECL|method|newIndexRequestInstance
annotation|@
name|Override
specifier|protected
name|IndexReplicationPingRequest
name|newIndexRequestInstance
parameter_list|(
name|ReplicationPingRequest
name|request
parameter_list|,
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|IndexReplicationPingRequest
argument_list|(
name|request
argument_list|,
name|index
argument_list|)
return|;
block|}
block|}
end_class

end_unit

