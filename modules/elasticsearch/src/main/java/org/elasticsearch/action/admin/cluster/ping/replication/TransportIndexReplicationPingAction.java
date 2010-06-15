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
name|TransportIndexReplicationOperationAction
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
name|routing
operator|.
name|GroupShardsIterator
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
name|IndexRoutingTable
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
name|index
operator|.
name|Index
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
name|IndexMissingException
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
DECL|class|TransportIndexReplicationPingAction
specifier|public
class|class
name|TransportIndexReplicationPingAction
extends|extends
name|TransportIndexReplicationOperationAction
argument_list|<
name|IndexReplicationPingRequest
argument_list|,
name|IndexReplicationPingResponse
argument_list|,
name|ShardReplicationPingRequest
argument_list|,
name|ShardReplicationPingResponse
argument_list|>
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|method|TransportIndexReplicationPingAction
annotation|@
name|Inject
specifier|public
name|TransportIndexReplicationPingAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportShardReplicationPingAction
name|shardReplicationPingAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|threadPool
argument_list|,
name|shardReplicationPingAction
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
block|}
DECL|method|newRequestInstance
annotation|@
name|Override
specifier|protected
name|IndexReplicationPingRequest
name|newRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|IndexReplicationPingRequest
argument_list|()
return|;
block|}
DECL|method|newResponseInstance
annotation|@
name|Override
specifier|protected
name|IndexReplicationPingResponse
name|newResponseInstance
parameter_list|(
name|IndexReplicationPingRequest
name|request
parameter_list|,
name|AtomicReferenceArray
name|shardsResponses
parameter_list|)
block|{
name|int
name|successfulShards
init|=
literal|0
decl_stmt|;
name|int
name|failedShards
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
name|shardsResponses
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|shardsResponses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|failedShards
operator|++
expr_stmt|;
block|}
else|else
block|{
name|successfulShards
operator|++
expr_stmt|;
block|}
block|}
return|return
operator|new
name|IndexReplicationPingResponse
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
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
DECL|method|transportAction
annotation|@
name|Override
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
literal|"ping/replication/index"
return|;
block|}
DECL|method|shards
annotation|@
name|Override
specifier|protected
name|GroupShardsIterator
name|shards
parameter_list|(
name|IndexReplicationPingRequest
name|indexRequest
parameter_list|)
block|{
name|IndexRoutingTable
name|indexRouting
init|=
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
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexRouting
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|indexRouting
operator|.
name|groupByShardsIt
argument_list|()
return|;
block|}
DECL|method|newShardRequestInstance
annotation|@
name|Override
specifier|protected
name|ShardReplicationPingRequest
name|newShardRequestInstance
parameter_list|(
name|IndexReplicationPingRequest
name|indexRequest
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
return|return
operator|new
name|ShardReplicationPingRequest
argument_list|(
name|indexRequest
argument_list|,
name|shardId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

