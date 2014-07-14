begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.delete.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
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
name|block
operator|.
name|ClusterBlockException
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
name|VersionType
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportShardDeleteAction
specifier|public
class|class
name|TransportShardDeleteAction
extends|extends
name|TransportShardReplicationOperationAction
argument_list|<
name|ShardDeleteRequest
argument_list|,
name|ShardDeleteRequest
argument_list|,
name|ShardDeleteResponse
argument_list|>
block|{
DECL|field|ACTION_NAME
specifier|private
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
literal|"indices/index/b_shard/delete"
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportShardDeleteAction
specifier|public
name|TransportShardDeleteAction
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
name|ACTION_NAME
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
annotation|@
name|Override
DECL|method|checkWriteConsistency
specifier|protected
name|boolean
name|checkWriteConsistency
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|newRequestInstance
specifier|protected
name|ShardDeleteRequest
name|newRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardDeleteRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newReplicaRequestInstance
specifier|protected
name|ShardDeleteRequest
name|newReplicaRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardDeleteRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponseInstance
specifier|protected
name|ShardDeleteResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardDeleteResponse
argument_list|()
return|;
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
name|INDEX
return|;
block|}
annotation|@
name|Override
DECL|method|checkGlobalBlock
specifier|protected
name|ClusterBlockException
name|checkGlobalBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|ShardDeleteRequest
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|checkRequestBlock
specifier|protected
name|ClusterBlockException
name|checkRequestBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|ShardDeleteRequest
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnPrimary
specifier|protected
name|PrimaryResponse
argument_list|<
name|ShardDeleteResponse
argument_list|,
name|ShardDeleteRequest
argument_list|>
name|shardOperationOnPrimary
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|PrimaryOperationRequest
name|shardRequest
parameter_list|)
block|{
name|ShardDeleteRequest
name|request
init|=
name|shardRequest
operator|.
name|request
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|shardRequest
operator|.
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|shardSafe
argument_list|(
name|shardRequest
operator|.
name|shardId
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Delete
name|delete
init|=
name|indexShard
operator|.
name|prepareDelete
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|version
argument_list|()
argument_list|,
name|VersionType
operator|.
name|INTERNAL
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|PRIMARY
argument_list|)
decl_stmt|;
name|indexShard
operator|.
name|delete
argument_list|(
name|delete
argument_list|)
expr_stmt|;
comment|// update the version to happen on the replicas
name|request
operator|.
name|version
argument_list|(
name|delete
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|refresh
argument_list|()
condition|)
block|{
try|try
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
literal|"refresh_flag_delete"
argument_list|)
operator|.
name|force
argument_list|(
literal|false
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
comment|// ignore
block|}
block|}
name|ShardDeleteResponse
name|response
init|=
operator|new
name|ShardDeleteResponse
argument_list|(
name|delete
operator|.
name|version
argument_list|()
argument_list|,
name|delete
operator|.
name|found
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrimaryResponse
argument_list|<>
argument_list|(
name|shardRequest
operator|.
name|request
argument_list|,
name|response
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnReplica
specifier|protected
name|void
name|shardOperationOnReplica
parameter_list|(
name|ReplicaOperationRequest
name|shardRequest
parameter_list|)
block|{
name|ShardDeleteRequest
name|request
init|=
name|shardRequest
operator|.
name|request
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|shardRequest
operator|.
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|shardSafe
argument_list|(
name|shardRequest
operator|.
name|shardId
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Delete
name|delete
init|=
name|indexShard
operator|.
name|prepareDelete
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|version
argument_list|()
argument_list|,
name|VersionType
operator|.
name|INTERNAL
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|REPLICA
argument_list|)
decl_stmt|;
comment|// IndexDeleteAction doesn't support version type at the moment. Hard coded for the INTERNAL version
name|delete
operator|=
operator|new
name|Engine
operator|.
name|Delete
argument_list|(
name|delete
argument_list|,
name|VersionType
operator|.
name|INTERNAL
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|delete
operator|.
name|versionType
argument_list|()
operator|.
name|validateVersionForWrites
argument_list|(
name|delete
operator|.
name|version
argument_list|()
argument_list|)
assert|;
name|indexShard
operator|.
name|delete
argument_list|(
name|delete
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|refresh
argument_list|()
condition|)
block|{
try|try
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
literal|"refresh_flag_delete"
argument_list|)
operator|.
name|force
argument_list|(
literal|false
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
comment|// ignore
block|}
block|}
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
name|ShardDeleteRequest
name|request
parameter_list|)
block|{
name|GroupShardsIterator
name|group
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|broadcastDeleteShards
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
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardIterator
name|shardIt
range|:
name|group
control|)
block|{
if|if
condition|(
name|shardIt
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
operator|==
name|request
operator|.
name|shardId
argument_list|()
condition|)
block|{
return|return
name|shardIt
return|;
block|}
block|}
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"No shards iterator found for shard ["
operator|+
name|request
operator|.
name|shardId
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

