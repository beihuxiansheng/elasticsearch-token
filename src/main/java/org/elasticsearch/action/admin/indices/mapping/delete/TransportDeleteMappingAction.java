begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.mapping.delete
package|package
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
name|mapping
operator|.
name|delete
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectObjectCursor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|ShardOperationFailedException
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
name|flush
operator|.
name|FlushResponse
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
name|flush
operator|.
name|TransportFlushAction
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
name|refresh
operator|.
name|RefreshResponse
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
name|refresh
operator|.
name|TransportRefreshAction
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
name|deletebyquery
operator|.
name|DeleteByQueryResponse
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
name|deletebyquery
operator|.
name|IndexDeleteByQueryResponse
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
name|deletebyquery
operator|.
name|TransportDeleteByQueryAction
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
name|DestructiveOperations
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
name|QuerySourceBuilder
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
name|broadcast
operator|.
name|BroadcastOperationResponse
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
name|client
operator|.
name|Requests
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
name|ack
operator|.
name|ClusterStateUpdateResponse
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
name|metadata
operator|.
name|MetaDataMappingService
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
name|ImmutableOpenMap
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
name|index
operator|.
name|query
operator|.
name|BoolFilterBuilder
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
name|query
operator|.
name|QueryBuilders
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
name|query
operator|.
name|TypeFilterBuilder
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
name|TypeMissingException
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
name|settings
operator|.
name|NodeSettingsService
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
name|HashSet
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

begin_comment
comment|/**  * Delete mapping action.  */
end_comment

begin_class
DECL|class|TransportDeleteMappingAction
specifier|public
class|class
name|TransportDeleteMappingAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|DeleteMappingRequest
argument_list|,
name|DeleteMappingResponse
argument_list|>
block|{
DECL|field|metaDataMappingService
specifier|private
specifier|final
name|MetaDataMappingService
name|metaDataMappingService
decl_stmt|;
DECL|field|flushAction
specifier|private
specifier|final
name|TransportFlushAction
name|flushAction
decl_stmt|;
DECL|field|deleteByQueryAction
specifier|private
specifier|final
name|TransportDeleteByQueryAction
name|deleteByQueryAction
decl_stmt|;
DECL|field|refreshAction
specifier|private
specifier|final
name|TransportRefreshAction
name|refreshAction
decl_stmt|;
DECL|field|destructiveOperations
specifier|private
specifier|final
name|DestructiveOperations
name|destructiveOperations
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportDeleteMappingAction
specifier|public
name|TransportDeleteMappingAction
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
name|MetaDataMappingService
name|metaDataMappingService
parameter_list|,
name|TransportDeleteByQueryAction
name|deleteByQueryAction
parameter_list|,
name|TransportRefreshAction
name|refreshAction
parameter_list|,
name|TransportFlushAction
name|flushAction
parameter_list|,
name|NodeSettingsService
name|nodeSettingsService
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
name|metaDataMappingService
operator|=
name|metaDataMappingService
expr_stmt|;
name|this
operator|.
name|deleteByQueryAction
operator|=
name|deleteByQueryAction
expr_stmt|;
name|this
operator|.
name|refreshAction
operator|=
name|refreshAction
expr_stmt|;
name|this
operator|.
name|flushAction
operator|=
name|flushAction
expr_stmt|;
name|this
operator|.
name|destructiveOperations
operator|=
operator|new
name|DestructiveOperations
argument_list|(
name|logger
argument_list|,
name|settings
argument_list|,
name|nodeSettingsService
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
comment|// no need for fork on another thread pool, we go async right away
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
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
name|DeleteMappingAction
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|DeleteMappingRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|DeleteMappingRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|DeleteMappingResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|DeleteMappingResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|DeleteMappingRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|DeleteMappingResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|destructiveOperations
operator|.
name|failDestructive
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
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
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|DeleteMappingRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indicesBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|DeleteMappingRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|DeleteMappingResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|request
operator|.
name|indices
argument_list|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|flushAction
operator|.
name|execute
argument_list|(
name|Requests
operator|.
name|flushRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|FlushResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|FlushResponse
name|flushResponse
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|traceLogResponse
argument_list|(
literal|"Flush"
argument_list|,
name|flushResponse
argument_list|)
expr_stmt|;
block|}
comment|// get all types that need to be deleted.
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|result
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|findMappings
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
decl_stmt|;
comment|// create OrFilter with type filters within to account for different types
name|BoolFilterBuilder
name|filterBuilder
init|=
operator|new
name|BoolFilterBuilder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|types
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|typesMeta
range|:
name|result
control|)
block|{
for|for
control|(
name|ObjectObjectCursor
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|type
range|:
name|typesMeta
operator|.
name|value
control|)
block|{
name|filterBuilder
operator|.
name|should
argument_list|(
operator|new
name|TypeFilterBuilder
argument_list|(
name|type
operator|.
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
name|type
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|types
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|TypeMissingException
argument_list|(
operator|new
name|Index
argument_list|(
literal|"_all"
argument_list|)
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|,
literal|"No index has the type."
argument_list|)
throw|;
block|}
name|request
operator|.
name|types
argument_list|(
name|types
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|types
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|QuerySourceBuilder
name|querySourceBuilder
init|=
operator|new
name|QuerySourceBuilder
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|filteredQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|,
name|filterBuilder
argument_list|)
argument_list|)
decl_stmt|;
name|deleteByQueryAction
operator|.
name|execute
argument_list|(
name|Requests
operator|.
name|deleteByQueryRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
operator|.
name|source
argument_list|(
name|querySourceBuilder
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|DeleteByQueryResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|DeleteByQueryResponse
name|deleteByQueryResponse
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
for|for
control|(
name|IndexDeleteByQueryResponse
name|indexResponse
range|:
name|deleteByQueryResponse
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Delete by query[{}] completed with total[{}], successful[{}] and failed[{}]"
argument_list|,
name|indexResponse
operator|.
name|getIndex
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getTotalShards
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getFailedShards
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexResponse
operator|.
name|getFailedShards
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|ShardOperationFailedException
name|failure
range|:
name|indexResponse
operator|.
name|getFailures
argument_list|()
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}/{}] Delete by query shard failure reason: {}"
argument_list|,
name|failure
operator|.
name|index
argument_list|()
argument_list|,
name|failure
operator|.
name|shardId
argument_list|()
argument_list|,
name|failure
operator|.
name|reason
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|refreshAction
operator|.
name|execute
argument_list|(
name|Requests
operator|.
name|refreshRequest
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|RefreshResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|RefreshResponse
name|refreshResponse
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|traceLogResponse
argument_list|(
literal|"Refresh"
argument_list|,
name|refreshResponse
argument_list|)
expr_stmt|;
block|}
name|removeMapping
argument_list|()
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
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Refresh failed completely"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|removeMapping
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|removeMapping
parameter_list|()
block|{
name|DeleteMappingClusterStateUpdateRequest
name|clusterStateUpdateRequest
init|=
operator|new
name|DeleteMappingClusterStateUpdateRequest
argument_list|()
operator|.
name|indices
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
operator|.
name|types
argument_list|(
name|request
operator|.
name|types
argument_list|()
argument_list|)
operator|.
name|ackTimeout
argument_list|(
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|)
decl_stmt|;
name|metaDataMappingService
operator|.
name|removeMapping
argument_list|(
name|clusterStateUpdateRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClusterStateUpdateResponse
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|DeleteMappingResponse
argument_list|(
name|response
operator|.
name|isAcknowledged
argument_list|()
argument_list|)
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
name|t
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
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
name|t
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
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
name|t
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|traceLogResponse
specifier|private
name|void
name|traceLogResponse
parameter_list|(
name|String
name|action
parameter_list|,
name|BroadcastOperationResponse
name|response
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{} completed with total[{}], successful[{}] and failed[{}]"
argument_list|,
name|action
argument_list|,
name|response
operator|.
name|getTotalShards
argument_list|()
argument_list|,
name|response
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|response
operator|.
name|getFailedShards
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|getFailedShards
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|ShardOperationFailedException
name|failure
range|:
name|response
operator|.
name|getShardFailures
argument_list|()
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}/{}] {} shard failure reason: {}"
argument_list|,
name|failure
operator|.
name|index
argument_list|()
argument_list|,
name|failure
operator|.
name|shardId
argument_list|()
argument_list|,
name|action
argument_list|,
name|failure
operator|.
name|reason
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

