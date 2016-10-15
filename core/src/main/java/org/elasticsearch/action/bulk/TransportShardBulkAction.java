begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|DocWriteResponse
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
name|DocWriteRequest
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
name|delete
operator|.
name|DeleteRequest
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
name|delete
operator|.
name|DeleteResponse
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
name|index
operator|.
name|IndexRequest
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
name|index
operator|.
name|IndexResponse
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
name|replication
operator|.
name|TransportWriteAction
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
name|ReplicationResponse
operator|.
name|ShardInfo
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
name|update
operator|.
name|UpdateHelper
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
name|update
operator|.
name|UpdateRequest
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
name|update
operator|.
name|UpdateResponse
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
name|IndexMetaData
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
name|service
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
name|bytes
operator|.
name|BytesReference
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
name|common
operator|.
name|xcontent
operator|.
name|XContentHelper
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
name|xcontent
operator|.
name|XContentType
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
name|VersionConflictEngineException
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
name|TransportRequestOptions
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
name|Map
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
operator|.
name|TransportDeleteAction
operator|.
name|executeDeleteRequestOnPrimary
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|delete
operator|.
name|TransportDeleteAction
operator|.
name|executeDeleteRequestOnReplica
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|TransportIndexAction
operator|.
name|executeIndexRequestOnPrimary
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|index
operator|.
name|TransportIndexAction
operator|.
name|executeIndexRequestOnReplica
import|;
end_import

begin_import
import|import static
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
name|ReplicationOperation
operator|.
name|ignoreReplicaException
import|;
end_import

begin_import
import|import static
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
name|ReplicationOperation
operator|.
name|isConflictException
import|;
end_import

begin_comment
comment|/** Performs shard-level bulk (index, delete or update) operations */
end_comment

begin_class
DECL|class|TransportShardBulkAction
specifier|public
class|class
name|TransportShardBulkAction
extends|extends
name|TransportWriteAction
argument_list|<
name|BulkShardRequest
argument_list|,
name|BulkShardRequest
argument_list|,
name|BulkShardResponse
argument_list|>
block|{
DECL|field|ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
name|BulkAction
operator|.
name|NAME
operator|+
literal|"[s]"
decl_stmt|;
DECL|field|updateHelper
specifier|private
specifier|final
name|UpdateHelper
name|updateHelper
decl_stmt|;
DECL|field|allowIdGeneration
specifier|private
specifier|final
name|boolean
name|allowIdGeneration
decl_stmt|;
DECL|field|mappingUpdatedAction
specifier|private
specifier|final
name|MappingUpdatedAction
name|mappingUpdatedAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportShardBulkAction
specifier|public
name|TransportShardBulkAction
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
name|MappingUpdatedAction
name|mappingUpdatedAction
parameter_list|,
name|UpdateHelper
name|updateHelper
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
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
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|BulkShardRequest
operator|::
operator|new
argument_list|,
name|BulkShardRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|BULK
argument_list|)
expr_stmt|;
name|this
operator|.
name|updateHelper
operator|=
name|updateHelper
expr_stmt|;
name|this
operator|.
name|allowIdGeneration
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"action.allow_id_generation"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|mappingUpdatedAction
operator|=
name|mappingUpdatedAction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transportOptions
specifier|protected
name|TransportRequestOptions
name|transportOptions
parameter_list|()
block|{
return|return
name|BulkAction
operator|.
name|INSTANCE
operator|.
name|transportOptions
argument_list|(
name|settings
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponseInstance
specifier|protected
name|BulkShardResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|BulkShardResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|resolveIndex
specifier|protected
name|boolean
name|resolveIndex
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|onPrimaryShard
specifier|protected
name|PrimaryOperationResult
argument_list|<
name|BulkShardResponse
argument_list|>
name|onPrimaryShard
parameter_list|(
name|BulkShardRequest
name|request
parameter_list|,
name|IndexShard
name|primary
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|IndexMetaData
name|metaData
init|=
name|primary
operator|.
name|indexSettings
argument_list|()
operator|.
name|getIndexMetaData
argument_list|()
decl_stmt|;
name|long
index|[]
name|preVersions
init|=
operator|new
name|long
index|[
name|request
operator|.
name|items
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
name|VersionType
index|[]
name|preVersionTypes
init|=
operator|new
name|VersionType
index|[
name|request
operator|.
name|items
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
name|Translog
operator|.
name|Location
name|location
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|requestIndex
init|=
literal|0
init|;
name|requestIndex
operator|<
name|request
operator|.
name|items
argument_list|()
operator|.
name|length
condition|;
name|requestIndex
operator|++
control|)
block|{
name|location
operator|=
name|executeBulkItemRequest
argument_list|(
name|metaData
argument_list|,
name|primary
argument_list|,
name|request
argument_list|,
name|preVersions
argument_list|,
name|preVersionTypes
argument_list|,
name|location
argument_list|,
name|requestIndex
argument_list|)
expr_stmt|;
block|}
name|BulkItemResponse
index|[]
name|responses
init|=
operator|new
name|BulkItemResponse
index|[
name|request
operator|.
name|items
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
name|BulkItemRequest
index|[]
name|items
init|=
name|request
operator|.
name|items
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|responses
index|[
name|i
index|]
operator|=
name|items
index|[
name|i
index|]
operator|.
name|getPrimaryResponse
argument_list|()
expr_stmt|;
block|}
name|BulkShardResponse
name|response
init|=
operator|new
name|BulkShardResponse
argument_list|(
name|request
operator|.
name|shardId
argument_list|()
argument_list|,
name|responses
argument_list|)
decl_stmt|;
return|return
operator|new
name|PrimaryOperationResult
argument_list|<>
argument_list|(
name|response
argument_list|,
name|location
argument_list|)
return|;
block|}
comment|/** Executes bulk item requests and handles request execution exceptions */
DECL|method|executeBulkItemRequest
specifier|private
name|Translog
operator|.
name|Location
name|executeBulkItemRequest
parameter_list|(
name|IndexMetaData
name|metaData
parameter_list|,
name|IndexShard
name|primary
parameter_list|,
name|BulkShardRequest
name|request
parameter_list|,
name|long
index|[]
name|preVersions
parameter_list|,
name|VersionType
index|[]
name|preVersionTypes
parameter_list|,
name|Translog
operator|.
name|Location
name|location
parameter_list|,
name|int
name|requestIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|preVersions
index|[
name|requestIndex
index|]
operator|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|request
argument_list|()
operator|.
name|version
argument_list|()
expr_stmt|;
name|preVersionTypes
index|[
name|requestIndex
index|]
operator|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|request
argument_list|()
operator|.
name|versionType
argument_list|()
expr_stmt|;
name|DocWriteRequest
operator|.
name|OpType
name|opType
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|request
argument_list|()
operator|.
name|opType
argument_list|()
decl_stmt|;
try|try
block|{
name|DocWriteRequest
name|itemRequest
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|request
argument_list|()
decl_stmt|;
specifier|final
name|PrimaryOperationResult
argument_list|<
name|?
extends|extends
name|DocWriteResponse
argument_list|>
name|primaryOperationResult
decl_stmt|;
switch|switch
condition|(
name|itemRequest
operator|.
name|opType
argument_list|()
condition|)
block|{
case|case
name|CREATE
case|:
case|case
name|INDEX
case|:
name|primaryOperationResult
operator|=
name|executeIndexRequestOnPrimary
argument_list|(
operator|(
operator|(
name|IndexRequest
operator|)
name|itemRequest
operator|)
argument_list|,
name|primary
argument_list|,
name|mappingUpdatedAction
argument_list|)
expr_stmt|;
break|break;
case|case
name|UPDATE
case|:
name|int
name|maxAttempts
init|=
operator|(
operator|(
name|UpdateRequest
operator|)
name|itemRequest
operator|)
operator|.
name|retryOnConflict
argument_list|()
decl_stmt|;
name|PrimaryOperationResult
argument_list|<
name|?
extends|extends
name|DocWriteResponse
argument_list|>
name|shardUpdateOperation
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|attemptCount
init|=
literal|0
init|;
name|attemptCount
operator|<=
name|maxAttempts
condition|;
name|attemptCount
operator|++
control|)
block|{
name|shardUpdateOperation
operator|=
name|shardUpdateOperation
argument_list|(
name|metaData
argument_list|,
name|primary
argument_list|,
name|request
argument_list|,
name|requestIndex
argument_list|,
operator|(
operator|(
name|UpdateRequest
operator|)
name|itemRequest
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardUpdateOperation
operator|.
name|success
argument_list|()
operator|||
name|shardUpdateOperation
operator|.
name|getFailure
argument_list|()
operator|instanceof
name|VersionConflictEngineException
operator|==
literal|false
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|shardUpdateOperation
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"version conflict exception should bubble up on last attempt"
argument_list|)
throw|;
block|}
name|primaryOperationResult
operator|=
name|shardUpdateOperation
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|primaryOperationResult
operator|=
name|executeDeleteRequestOnPrimary
argument_list|(
operator|(
operator|(
name|DeleteRequest
operator|)
name|itemRequest
operator|)
argument_list|,
name|primary
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unexpected opType ["
operator|+
name|itemRequest
operator|.
name|opType
argument_list|()
operator|+
literal|"] found"
argument_list|)
throw|;
block|}
if|if
condition|(
name|primaryOperationResult
operator|.
name|success
argument_list|()
condition|)
block|{
if|if
condition|(
name|primaryOperationResult
operator|.
name|getLocation
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|location
operator|=
name|locationToSync
argument_list|(
name|location
argument_list|,
name|primaryOperationResult
operator|.
name|getLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|primaryOperationResult
operator|.
name|getResponse
argument_list|()
operator|.
name|getResult
argument_list|()
operator|==
name|DocWriteResponse
operator|.
name|Result
operator|.
name|NOOP
operator|:
literal|"only noop operation can have null next operation"
assert|;
block|}
comment|// update the bulk item request because update request execution can mutate the bulk item request
name|BulkItemRequest
name|item
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
decl_stmt|;
comment|// add the response
name|setResponse
argument_list|(
name|item
argument_list|,
operator|new
name|BulkItemResponse
argument_list|(
name|item
operator|.
name|id
argument_list|()
argument_list|,
name|opType
argument_list|,
name|primaryOperationResult
operator|.
name|getResponse
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BulkItemRequest
name|item
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
decl_stmt|;
name|DocWriteRequest
name|docWriteRequest
init|=
name|item
operator|.
name|request
argument_list|()
decl_stmt|;
name|Exception
name|failure
init|=
name|primaryOperationResult
operator|.
name|getFailure
argument_list|()
decl_stmt|;
if|if
condition|(
name|isConflictException
argument_list|(
name|failure
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{} failed to execute bulk item ({}) {}"
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|,
name|docWriteRequest
operator|.
name|opType
argument_list|()
operator|.
name|getLowercase
argument_list|()
argument_list|,
name|request
argument_list|)
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"{} failed to execute bulk item ({}) {}"
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|,
name|docWriteRequest
operator|.
name|opType
argument_list|()
operator|.
name|getLowercase
argument_list|()
argument_list|,
name|request
argument_list|)
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
comment|// if its a conflict failure, and we already executed the request on a primary (and we execute it
comment|// again, due to primary relocation and only processing up to N bulk items when the shard gets closed)
comment|// then just use the response we got from the successful execution
if|if
condition|(
name|item
operator|.
name|getPrimaryResponse
argument_list|()
operator|!=
literal|null
operator|&&
name|isConflictException
argument_list|(
name|failure
argument_list|)
condition|)
block|{
name|setResponse
argument_list|(
name|item
argument_list|,
name|item
operator|.
name|getPrimaryResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setResponse
argument_list|(
name|item
argument_list|,
operator|new
name|BulkItemResponse
argument_list|(
name|item
operator|.
name|id
argument_list|()
argument_list|,
name|docWriteRequest
operator|.
name|opType
argument_list|()
argument_list|,
operator|new
name|BulkItemResponse
operator|.
name|Failure
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|docWriteRequest
operator|.
name|type
argument_list|()
argument_list|,
name|docWriteRequest
operator|.
name|id
argument_list|()
argument_list|,
name|failure
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// rethrow the failure if we are going to retry on primary and let parent failure to handle it
if|if
condition|(
name|retryPrimaryException
argument_list|(
name|e
argument_list|)
condition|)
block|{
comment|// restore updated versions...
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|requestIndex
condition|;
name|j
operator|++
control|)
block|{
name|DocWriteRequest
name|docWriteRequest
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|j
index|]
operator|.
name|request
argument_list|()
decl_stmt|;
name|docWriteRequest
operator|.
name|version
argument_list|(
name|preVersions
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|docWriteRequest
operator|.
name|versionType
argument_list|(
name|preVersionTypes
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
comment|// TODO: maybe this assert is too strict, we can still get environment failures while executing write operations
assert|assert
literal|false
operator|:
literal|"unexpected exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" class:"
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
assert|;
block|}
assert|assert
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|getPrimaryResponse
argument_list|()
operator|!=
literal|null
assert|;
assert|assert
name|preVersionTypes
index|[
name|requestIndex
index|]
operator|!=
literal|null
assert|;
return|return
name|location
return|;
block|}
DECL|method|setResponse
specifier|private
name|void
name|setResponse
parameter_list|(
name|BulkItemRequest
name|request
parameter_list|,
name|BulkItemResponse
name|response
parameter_list|)
block|{
name|request
operator|.
name|setPrimaryResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|isFailed
argument_list|()
condition|)
block|{
name|request
operator|.
name|setIgnoreOnReplica
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Set the ShardInfo to 0 so we can safely send it to the replicas. We won't use it in the real response though.
name|response
operator|.
name|getResponse
argument_list|()
operator|.
name|setShardInfo
argument_list|(
operator|new
name|ShardInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Executes update request, doing a get and translating update to a index or delete operation      * NOTE: all operations except NOOP, reassigns the bulk item request      */
DECL|method|shardUpdateOperation
specifier|private
name|PrimaryOperationResult
argument_list|<
name|?
extends|extends
name|DocWriteResponse
argument_list|>
name|shardUpdateOperation
parameter_list|(
name|IndexMetaData
name|metaData
parameter_list|,
name|IndexShard
name|primary
parameter_list|,
name|BulkShardRequest
name|request
parameter_list|,
name|int
name|requestIndex
parameter_list|,
name|UpdateRequest
name|updateRequest
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|UpdateHelper
operator|.
name|Result
name|translate
decl_stmt|;
try|try
block|{
name|translate
operator|=
name|updateHelper
operator|.
name|prepare
argument_list|(
name|updateRequest
argument_list|,
name|primary
argument_list|,
name|threadPool
operator|::
name|estimatedTimeInMillis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|PrimaryOperationResult
argument_list|<>
argument_list|(
name|e
argument_list|)
return|;
block|}
switch|switch
condition|(
name|translate
operator|.
name|getResponseResult
argument_list|()
condition|)
block|{
case|case
name|CREATED
case|:
case|case
name|UPDATED
case|:
name|IndexRequest
name|indexRequest
init|=
name|translate
operator|.
name|action
argument_list|()
decl_stmt|;
name|MappingMetaData
name|mappingMd
init|=
name|metaData
operator|.
name|mappingOrDefault
argument_list|(
name|indexRequest
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|indexRequest
operator|.
name|process
argument_list|(
name|mappingMd
argument_list|,
name|allowIdGeneration
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|PrimaryOperationResult
argument_list|<
name|IndexResponse
argument_list|>
name|writeResult
init|=
name|executeIndexRequestOnPrimary
argument_list|(
name|indexRequest
argument_list|,
name|primary
argument_list|,
name|mappingUpdatedAction
argument_list|)
decl_stmt|;
if|if
condition|(
name|writeResult
operator|.
name|success
argument_list|()
condition|)
block|{
name|BytesReference
name|indexSourceAsBytes
init|=
name|indexRequest
operator|.
name|source
argument_list|()
decl_stmt|;
name|IndexResponse
name|indexResponse
init|=
name|writeResult
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|indexResponse
operator|.
name|getShardInfo
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getShardId
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getType
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getId
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getVersion
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getResult
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|updateRequest
operator|.
name|fetchSource
argument_list|()
operator|!=
literal|null
operator|&&
name|updateRequest
operator|.
name|fetchSource
argument_list|()
operator|.
name|fetchSource
argument_list|()
operator|)
operator|||
operator|(
name|updateRequest
operator|.
name|fields
argument_list|()
operator|!=
literal|null
operator|&&
name|updateRequest
operator|.
name|fields
argument_list|()
operator|.
name|length
operator|>
literal|0
operator|)
condition|)
block|{
name|Tuple
argument_list|<
name|XContentType
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|sourceAndContent
init|=
name|XContentHelper
operator|.
name|convertToMap
argument_list|(
name|indexSourceAsBytes
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|update
operator|.
name|setGetResult
argument_list|(
name|updateHelper
operator|.
name|extractGetResult
argument_list|(
name|updateRequest
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|indexResponse
operator|.
name|getVersion
argument_list|()
argument_list|,
name|sourceAndContent
operator|.
name|v2
argument_list|()
argument_list|,
name|sourceAndContent
operator|.
name|v1
argument_list|()
argument_list|,
name|indexSourceAsBytes
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Replace the update request to the translated index request to execute on the replica.
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|=
operator|new
name|BulkItemRequest
argument_list|(
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|id
argument_list|()
argument_list|,
name|indexRequest
argument_list|)
expr_stmt|;
return|return
operator|new
name|PrimaryOperationResult
argument_list|<>
argument_list|(
name|update
argument_list|,
name|writeResult
operator|.
name|getLocation
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|writeResult
return|;
block|}
case|case
name|DELETED
case|:
name|DeleteRequest
name|deleteRequest
init|=
name|translate
operator|.
name|action
argument_list|()
decl_stmt|;
name|PrimaryOperationResult
argument_list|<
name|DeleteResponse
argument_list|>
name|deleteResult
init|=
name|executeDeleteRequestOnPrimary
argument_list|(
name|deleteRequest
argument_list|,
name|primary
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleteResult
operator|.
name|success
argument_list|()
condition|)
block|{
name|DeleteResponse
name|response
init|=
name|deleteResult
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|UpdateResponse
name|deleteUpdateResponse
init|=
operator|new
name|UpdateResponse
argument_list|(
name|response
operator|.
name|getShardInfo
argument_list|()
argument_list|,
name|response
operator|.
name|getShardId
argument_list|()
argument_list|,
name|response
operator|.
name|getType
argument_list|()
argument_list|,
name|response
operator|.
name|getId
argument_list|()
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
decl_stmt|;
name|deleteUpdateResponse
operator|.
name|setGetResult
argument_list|(
name|updateHelper
operator|.
name|extractGetResult
argument_list|(
name|updateRequest
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getVersion
argument_list|()
argument_list|,
name|translate
operator|.
name|updatedSourceAsMap
argument_list|()
argument_list|,
name|translate
operator|.
name|updateSourceContentType
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Replace the update request to the translated delete request to execute on the replica.
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|=
operator|new
name|BulkItemRequest
argument_list|(
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
operator|.
name|id
argument_list|()
argument_list|,
name|deleteRequest
argument_list|)
expr_stmt|;
return|return
operator|new
name|PrimaryOperationResult
argument_list|<>
argument_list|(
name|deleteUpdateResponse
argument_list|,
name|deleteResult
operator|.
name|getLocation
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|deleteResult
return|;
block|}
case|case
name|NOOP
case|:
name|BulkItemRequest
name|item
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|requestIndex
index|]
decl_stmt|;
name|primary
operator|.
name|noopUpdate
argument_list|(
name|updateRequest
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|item
operator|.
name|setIgnoreOnReplica
argument_list|()
expr_stmt|;
comment|// no need to go to the replica
return|return
operator|new
name|PrimaryOperationResult
argument_list|<>
argument_list|(
name|translate
operator|.
name|action
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Illegal update operation "
operator|+
name|translate
operator|.
name|getResponseResult
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|onReplicaShard
specifier|protected
name|ReplicaOperationResult
name|onReplicaShard
parameter_list|(
name|BulkShardRequest
name|request
parameter_list|,
name|IndexShard
name|replica
parameter_list|)
throws|throws
name|Exception
block|{
name|Translog
operator|.
name|Location
name|location
init|=
literal|null
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
name|items
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|BulkItemRequest
name|item
init|=
name|request
operator|.
name|items
argument_list|()
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|item
operator|==
literal|null
operator|||
name|item
operator|.
name|isIgnoreOnReplica
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|DocWriteRequest
name|docWriteRequest
init|=
name|item
operator|.
name|request
argument_list|()
decl_stmt|;
specifier|final
name|ReplicaOperationResult
name|replicaResult
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|docWriteRequest
operator|.
name|opType
argument_list|()
condition|)
block|{
case|case
name|CREATE
case|:
case|case
name|INDEX
case|:
name|replicaResult
operator|=
name|executeIndexRequestOnReplica
argument_list|(
operator|(
operator|(
name|IndexRequest
operator|)
name|docWriteRequest
operator|)
argument_list|,
name|replica
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|replicaResult
operator|=
name|executeDeleteRequestOnReplica
argument_list|(
operator|(
operator|(
name|DeleteRequest
operator|)
name|docWriteRequest
operator|)
argument_list|,
name|replica
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unexpected request operation type on replica: "
operator|+
name|docWriteRequest
operator|.
name|opType
argument_list|()
operator|.
name|getLowercase
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|replicaResult
operator|.
name|success
argument_list|()
condition|)
block|{
name|location
operator|=
name|locationToSync
argument_list|(
name|location
argument_list|,
name|replicaResult
operator|.
name|getLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// check if any transient write operation failures should be bubbled up
name|Exception
name|failure
init|=
name|replicaResult
operator|.
name|getFailure
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ignoreReplicaException
argument_list|(
name|failure
argument_list|)
condition|)
block|{
throw|throw
name|failure
throw|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// if its not an ignore replica failure, we need to make sure to bubble up the failure
comment|// so we will fail the shard
if|if
condition|(
operator|!
name|ignoreReplicaException
argument_list|(
name|e
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
return|return
operator|new
name|ReplicaOperationResult
argument_list|(
name|location
argument_list|)
return|;
block|}
DECL|method|locationToSync
specifier|private
name|Translog
operator|.
name|Location
name|locationToSync
parameter_list|(
name|Translog
operator|.
name|Location
name|current
parameter_list|,
name|Translog
operator|.
name|Location
name|next
parameter_list|)
block|{
comment|/* here we are moving forward in the translog with each operation. Under the hood          * this might cross translog files which is ok since from the user perspective          * the translog is like a tape where only the highest location needs to be fsynced          * in order to sync all previous locations even though they are not in the same file.          * When the translog rolls over files the previous file is fsynced on after closing if needed.*/
assert|assert
name|next
operator|!=
literal|null
operator|:
literal|"next operation can't be null"
assert|;
assert|assert
name|current
operator|==
literal|null
operator|||
name|current
operator|.
name|compareTo
argument_list|(
name|next
argument_list|)
operator|<
literal|0
operator|:
literal|"translog locations are not increasing"
assert|;
return|return
name|next
return|;
block|}
block|}
end_class

end_unit

