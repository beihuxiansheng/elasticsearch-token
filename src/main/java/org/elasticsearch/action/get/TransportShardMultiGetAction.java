begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|get
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
name|ExceptionsHelper
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
name|single
operator|.
name|shard
operator|.
name|TransportShardSingleOperationAction
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
name|routing
operator|.
name|ShardIterator
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
name|engine
operator|.
name|Engine
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
name|get
operator|.
name|GetResult
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
name|service
operator|.
name|IndexService
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
name|shard
operator|.
name|service
operator|.
name|IndexShard
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

begin_class
DECL|class|TransportShardMultiGetAction
specifier|public
class|class
name|TransportShardMultiGetAction
extends|extends
name|TransportShardSingleOperationAction
argument_list|<
name|MultiGetShardRequest
argument_list|,
name|MultiGetShardResponse
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|realtime
specifier|private
specifier|final
name|boolean
name|realtime
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportShardMultiGetAction
specifier|public
name|TransportShardMultiGetAction
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
name|IndicesService
name|indicesService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
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
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|realtime
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"action.get.realtime"
argument_list|,
literal|true
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
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SEARCH
return|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
name|TransportActions
operator|.
name|MULTI_GET
operator|+
literal|"/shard"
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|MultiGetShardRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|MultiGetShardRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|MultiGetShardResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|MultiGetShardResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|void
name|checkBlock
parameter_list|(
name|MultiGetShardRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedRaiseException
argument_list|(
name|ClusterBlockLevel
operator|.
name|READ
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shards
specifier|protected
name|ShardIterator
name|shards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|MultiGetShardRequest
name|request
parameter_list|)
block|{
return|return
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|getShards
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|,
name|request
operator|.
name|preference
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|MultiGetShardRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|MultiGetShardResponse
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|realtime
operator|==
literal|null
condition|)
block|{
name|request
operator|.
name|realtime
operator|=
name|this
operator|.
name|realtime
expr_stmt|;
block|}
name|super
operator|.
name|doExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|MultiGetShardResponse
name|shardOperation
parameter_list|(
name|MultiGetShardRequest
name|request
parameter_list|,
name|int
name|shardId
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|indexService
operator|.
name|shardSafe
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|refresh
argument_list|()
operator|&&
operator|!
name|request
operator|.
name|realtime
argument_list|()
condition|)
block|{
name|indexShard
operator|.
name|refresh
argument_list|(
operator|new
name|Engine
operator|.
name|Refresh
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MultiGetShardResponse
name|response
init|=
operator|new
name|MultiGetShardResponse
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
name|request
operator|.
name|locations
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|type
init|=
name|request
operator|.
name|types
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|id
init|=
name|request
operator|.
name|ids
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
index|[]
name|fields
init|=
name|request
operator|.
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|GetResult
name|getResult
init|=
name|indexShard
operator|.
name|getService
argument_list|()
operator|.
name|get
argument_list|(
name|type
argument_list|,
name|id
argument_list|,
name|fields
argument_list|,
name|request
operator|.
name|realtime
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|.
name|add
argument_list|(
name|request
operator|.
name|locations
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|GetResponse
argument_list|(
name|getResult
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
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}][{}] failed to execute multi_get for [{}]/[{}]"
argument_list|,
name|e
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|shardId
argument_list|,
name|type
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|response
operator|.
name|add
argument_list|(
name|request
operator|.
name|locations
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|MultiGetResponse
operator|.
name|Failure
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|type
argument_list|,
name|id
argument_list|,
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
return|;
block|}
block|}
end_class

end_unit

