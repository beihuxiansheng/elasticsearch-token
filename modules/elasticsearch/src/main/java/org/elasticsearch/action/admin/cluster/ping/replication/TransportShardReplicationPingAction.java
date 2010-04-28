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
name|support
operator|.
name|replication
operator|.
name|TransportShardReplicationOperationAction
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
name|action
operator|.
name|shard
operator|.
name|ShardStateAction
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
name|routing
operator|.
name|ShardsIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|TransportShardReplicationPingAction
specifier|public
class|class
name|TransportShardReplicationPingAction
extends|extends
name|TransportShardReplicationOperationAction
argument_list|<
name|ShardReplicationPingRequest
argument_list|,
name|ShardReplicationPingResponse
argument_list|>
block|{
DECL|method|TransportShardReplicationPingAction
annotation|@
name|Inject
specifier|public
name|TransportShardReplicationPingAction
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
name|IndicesService
name|indicesService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ShardStateAction
name|shardStateAction
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
name|indicesService
argument_list|,
name|threadPool
argument_list|,
name|shardStateAction
argument_list|)
expr_stmt|;
block|}
DECL|method|newRequestInstance
annotation|@
name|Override
specifier|protected
name|ShardReplicationPingRequest
name|newRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardReplicationPingRequest
argument_list|()
return|;
block|}
DECL|method|newResponseInstance
annotation|@
name|Override
specifier|protected
name|ShardReplicationPingResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardReplicationPingResponse
argument_list|()
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
literal|"ping/replication/shard"
return|;
block|}
DECL|method|shardOperationOnPrimary
annotation|@
name|Override
specifier|protected
name|ShardReplicationPingResponse
name|shardOperationOnPrimary
parameter_list|(
name|ShardOperationRequest
name|shardRequest
parameter_list|)
block|{
return|return
operator|new
name|ShardReplicationPingResponse
argument_list|()
return|;
block|}
DECL|method|shardOperationOnBackup
annotation|@
name|Override
specifier|protected
name|void
name|shardOperationOnBackup
parameter_list|(
name|ShardOperationRequest
name|shardRequest
parameter_list|)
block|{     }
DECL|method|shards
annotation|@
name|Override
specifier|protected
name|ShardsIterator
name|shards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ShardReplicationPingRequest
name|request
parameter_list|)
block|{
return|return
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|shard
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
operator|.
name|shardsIt
argument_list|()
return|;
block|}
block|}
end_class

end_unit

