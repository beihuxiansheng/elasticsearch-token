begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.delete
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
package|;
end_package

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
name|RoutingMissingException
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
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequest
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
name|indices
operator|.
name|create
operator|.
name|CreateIndexResponse
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
name|indices
operator|.
name|create
operator|.
name|TransportCreateIndexAction
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
name|AutoCreateIndex
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
name|TransportReplicationAction
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
name|index
operator|.
name|MappingUpdatedAction
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
name|metadata
operator|.
name|MappingMetaData
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
name|collect
operator|.
name|Tuple
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
name|IndexShard
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
name|ShardId
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
name|translog
operator|.
name|Translog
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
name|IndexAlreadyExistsException
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
comment|/**  * Performs the delete operation.  */
end_comment

begin_class
DECL|class|TransportDeleteAction
specifier|public
class|class
name|TransportDeleteAction
extends|extends
name|TransportReplicationAction
argument_list|<
name|DeleteRequest
argument_list|,
name|DeleteRequest
argument_list|,
name|DeleteResponse
argument_list|>
block|{
DECL|field|autoCreateIndex
specifier|private
specifier|final
name|AutoCreateIndex
name|autoCreateIndex
decl_stmt|;
DECL|field|createIndexAction
specifier|private
specifier|final
name|TransportCreateIndexAction
name|createIndexAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportDeleteAction
specifier|public
name|TransportDeleteAction
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
parameter_list|,
name|TransportCreateIndexAction
name|createIndexAction
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|MappingUpdatedAction
name|mappingUpdatedAction
parameter_list|,
name|AutoCreateIndex
name|autoCreateIndex
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|DeleteAction
operator|.
name|NAME
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
argument_list|,
name|mappingUpdatedAction
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|DeleteRequest
operator|::
operator|new
argument_list|,
name|DeleteRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|this
operator|.
name|createIndexAction
operator|=
name|createIndexAction
expr_stmt|;
name|this
operator|.
name|autoCreateIndex
operator|=
name|autoCreateIndex
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
specifier|final
name|DeleteRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|ClusterState
name|state
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
if|if
condition|(
name|autoCreateIndex
operator|.
name|shouldAutoCreate
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|state
argument_list|)
condition|)
block|{
name|createIndexAction
operator|.
name|execute
argument_list|(
operator|new
name|CreateIndexRequest
argument_list|(
name|request
argument_list|)
operator|.
name|index
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|cause
argument_list|(
literal|"auto(delete api)"
argument_list|)
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|CreateIndexResponse
name|result
parameter_list|)
block|{
name|innerExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
operator|instanceof
name|IndexAlreadyExistsException
condition|)
block|{
comment|// we have the index, do it
name|innerExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|innerExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|resolveRequest
specifier|protected
name|void
name|resolveRequest
parameter_list|(
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|InternalRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|request
operator|.
name|request
argument_list|()
operator|.
name|routing
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|resolveIndexRouting
argument_list|(
name|request
operator|.
name|request
argument_list|()
operator|.
name|routing
argument_list|()
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|)
condition|)
block|{
comment|// check if routing is required, if so, do a broadcast delete
name|MappingMetaData
name|mappingMd
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|)
operator|.
name|mappingOrDefault
argument_list|(
name|request
operator|.
name|request
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappingMd
operator|!=
literal|null
operator|&&
name|mappingMd
operator|.
name|routing
argument_list|()
operator|.
name|required
argument_list|()
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|request
argument_list|()
operator|.
name|routing
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|request
argument_list|()
operator|.
name|versionType
argument_list|()
operator|!=
name|VersionType
operator|.
name|INTERNAL
condition|)
block|{
comment|// TODO: implement this feature
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"routing value is required for deleting documents of type ["
operator|+
name|request
operator|.
name|request
argument_list|()
operator|.
name|type
argument_list|()
operator|+
literal|"] while using version_type ["
operator|+
name|request
operator|.
name|request
argument_list|()
operator|.
name|versionType
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|RoutingMissingException
argument_list|(
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|method|innerExecute
specifier|private
name|void
name|innerExecute
parameter_list|(
specifier|final
name|DeleteRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
name|listener
parameter_list|)
block|{
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
DECL|method|newResponseInstance
specifier|protected
name|DeleteResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|DeleteResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnPrimary
specifier|protected
name|Tuple
argument_list|<
name|DeleteResponse
argument_list|,
name|DeleteRequest
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
name|DeleteRequest
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
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|shardSafe
argument_list|(
name|shardRequest
operator|.
name|shardId
operator|.
name|id
argument_list|()
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
name|request
operator|.
name|versionType
argument_list|()
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
comment|// update the request with teh version so it will go to the replicas
name|request
operator|.
name|versionType
argument_list|(
name|delete
operator|.
name|versionType
argument_list|()
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|)
expr_stmt|;
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
assert|assert
name|request
operator|.
name|versionType
argument_list|()
operator|.
name|validateVersionForWrites
argument_list|(
name|request
operator|.
name|version
argument_list|()
argument_list|)
assert|;
name|processAfter
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|,
name|indexShard
argument_list|,
name|delete
operator|.
name|getTranslogLocation
argument_list|()
argument_list|)
expr_stmt|;
name|DeleteResponse
name|response
init|=
operator|new
name|DeleteResponse
argument_list|(
name|shardRequest
operator|.
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|,
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
name|Tuple
argument_list|<>
argument_list|(
name|response
argument_list|,
name|shardRequest
operator|.
name|request
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
name|ShardId
name|shardId
parameter_list|,
name|DeleteRequest
name|request
parameter_list|)
block|{
name|IndexShard
name|indexShard
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|)
operator|.
name|shardSafe
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
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
name|request
operator|.
name|versionType
argument_list|()
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
name|indexShard
operator|.
name|delete
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|processAfter
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|,
name|indexShard
argument_list|,
name|delete
operator|.
name|getTranslogLocation
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
name|InternalRequest
name|request
parameter_list|)
block|{
return|return
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|deleteShards
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|routing
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

