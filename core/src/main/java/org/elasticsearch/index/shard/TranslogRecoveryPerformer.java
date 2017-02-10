begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|Logger
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|XContentFactory
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
name|engine
operator|.
name|IgnoreOnRecoveryEngineException
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
name|mapper
operator|.
name|DocumentMapperForType
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
name|mapper
operator|.
name|MapperException
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
name|mapper
operator|.
name|MapperService
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
name|mapper
operator|.
name|Mapping
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
name|mapper
operator|.
name|Uid
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
name|seqno
operator|.
name|SequenceNumbersService
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
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|index
operator|.
name|mapper
operator|.
name|SourceToParse
operator|.
name|source
import|;
end_import

begin_comment
comment|/**  * The TranslogRecoveryPerformer encapsulates all the logic needed to transform a translog entry into an  * indexing operation including source parsing and field creation from the source.  */
end_comment

begin_class
DECL|class|TranslogRecoveryPerformer
specifier|public
class|class
name|TranslogRecoveryPerformer
block|{
DECL|field|mapperService
specifier|private
specifier|final
name|MapperService
name|mapperService
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|recoveredTypes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Mapping
argument_list|>
name|recoveredTypes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|method|TranslogRecoveryPerformer
specifier|protected
name|TranslogRecoveryPerformer
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|MapperService
name|mapperService
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|mapperService
operator|=
name|mapperService
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
DECL|method|docMapper
specifier|protected
name|DocumentMapperForType
name|docMapper
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|mapperService
operator|.
name|documentMapperWithAutoCreate
argument_list|(
name|type
argument_list|)
return|;
comment|// protected for testing
block|}
comment|/**      * Applies all operations in the iterable to the current engine and returns the number of operations applied.      * This operation will stop applying operations once an operation failed to apply.      *      * Throws a {@link MapperException} to be thrown if a mapping update is encountered.      */
DECL|method|performBatchRecovery
name|int
name|performBatchRecovery
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Iterable
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|operations
parameter_list|)
block|{
name|int
name|numOps
init|=
literal|0
decl_stmt|;
try|try
block|{
for|for
control|(
name|Translog
operator|.
name|Operation
name|operation
range|:
name|operations
control|)
block|{
name|performRecoveryOperation
argument_list|(
name|engine
argument_list|,
name|operation
argument_list|,
literal|false
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|PEER_RECOVERY
argument_list|)
expr_stmt|;
name|numOps
operator|++
expr_stmt|;
block|}
name|engine
operator|.
name|getTranslog
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BatchOperationException
argument_list|(
name|shardId
argument_list|,
literal|"failed to apply batch translog operation"
argument_list|,
name|numOps
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|numOps
return|;
block|}
DECL|method|recoveryFromSnapshot
specifier|public
name|int
name|recoveryFromSnapshot
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Translog
operator|.
name|Snapshot
name|snapshot
parameter_list|)
throws|throws
name|IOException
block|{
name|Translog
operator|.
name|Operation
name|operation
decl_stmt|;
name|int
name|opsRecovered
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|operation
operator|=
name|snapshot
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|performRecoveryOperation
argument_list|(
name|engine
argument_list|,
name|operation
argument_list|,
literal|true
argument_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
operator|.
name|LOCAL_TRANSLOG_RECOVERY
argument_list|)
expr_stmt|;
name|opsRecovered
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|status
argument_list|()
operator|==
name|RestStatus
operator|.
name|BAD_REQUEST
condition|)
block|{
comment|// mainly for MapperParsingException and Failure to detect xcontent
name|logger
operator|.
name|info
argument_list|(
literal|"ignoring recovery of a corrupt translog entry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
return|return
name|opsRecovered
return|;
block|}
DECL|class|BatchOperationException
specifier|public
specifier|static
class|class
name|BatchOperationException
extends|extends
name|ElasticsearchException
block|{
DECL|field|completedOperations
specifier|private
specifier|final
name|int
name|completedOperations
decl_stmt|;
DECL|method|BatchOperationException
specifier|public
name|BatchOperationException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|msg
parameter_list|,
name|int
name|completedOperations
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|setShard
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|this
operator|.
name|completedOperations
operator|=
name|completedOperations
expr_stmt|;
block|}
DECL|method|BatchOperationException
specifier|public
name|BatchOperationException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|completedOperations
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|completedOperations
argument_list|)
expr_stmt|;
block|}
comment|/** the number of successful operations performed before the exception was thrown */
DECL|method|completedOperations
specifier|public
name|int
name|completedOperations
parameter_list|()
block|{
return|return
name|completedOperations
return|;
block|}
block|}
DECL|method|maybeAddMappingUpdate
specifier|private
name|void
name|maybeAddMappingUpdate
parameter_list|(
name|String
name|type
parameter_list|,
name|Mapping
name|update
parameter_list|,
name|String
name|docId
parameter_list|,
name|boolean
name|allowMappingUpdates
parameter_list|)
block|{
if|if
condition|(
name|update
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|allowMappingUpdates
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|MapperException
argument_list|(
literal|"mapping updates are not allowed (type: ["
operator|+
name|type
operator|+
literal|"], id: ["
operator|+
name|docId
operator|+
literal|"])"
argument_list|)
throw|;
block|}
name|Mapping
name|currentUpdate
init|=
name|recoveredTypes
operator|.
name|get
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentUpdate
operator|==
literal|null
condition|)
block|{
name|recoveredTypes
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|update
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentUpdate
operator|=
name|currentUpdate
operator|.
name|merge
argument_list|(
name|update
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Performs a single recovery operation.      *      * @param allowMappingUpdates true if mapping update should be accepted (but collected). Setting it to false will      *                            cause a {@link MapperException} to be thrown if an update      *                            is encountered.      */
DECL|method|performRecoveryOperation
specifier|private
name|void
name|performRecoveryOperation
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Translog
operator|.
name|Operation
name|operation
parameter_list|,
name|boolean
name|allowMappingUpdates
parameter_list|,
name|Engine
operator|.
name|Operation
operator|.
name|Origin
name|origin
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
switch|switch
condition|(
name|operation
operator|.
name|opType
argument_list|()
condition|)
block|{
case|case
name|INDEX
case|:
name|Translog
operator|.
name|Index
name|index
init|=
operator|(
name|Translog
operator|.
name|Index
operator|)
name|operation
decl_stmt|;
comment|// we set canHaveDuplicates to true all the time such that we de-optimze the translog case and ensure that all
comment|// autoGeneratedID docs that are coming from the primary are updated correctly.
name|Engine
operator|.
name|Index
name|engineIndex
init|=
name|IndexShard
operator|.
name|prepareIndex
argument_list|(
name|docMapper
argument_list|(
name|index
operator|.
name|type
argument_list|()
argument_list|)
argument_list|,
name|source
argument_list|(
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|index
operator|.
name|type
argument_list|()
argument_list|,
name|index
operator|.
name|id
argument_list|()
argument_list|,
name|index
operator|.
name|source
argument_list|()
argument_list|,
name|XContentFactory
operator|.
name|xContentType
argument_list|(
name|index
operator|.
name|source
argument_list|()
argument_list|)
argument_list|)
operator|.
name|routing
argument_list|(
name|index
operator|.
name|routing
argument_list|()
argument_list|)
operator|.
name|parent
argument_list|(
name|index
operator|.
name|parent
argument_list|()
argument_list|)
argument_list|,
name|index
operator|.
name|seqNo
argument_list|()
argument_list|,
name|index
operator|.
name|primaryTerm
argument_list|()
argument_list|,
name|index
operator|.
name|version
argument_list|()
argument_list|,
name|index
operator|.
name|versionType
argument_list|()
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|,
name|origin
argument_list|,
name|index
operator|.
name|getAutoGeneratedIdTimestamp
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|maybeAddMappingUpdate
argument_list|(
name|engineIndex
operator|.
name|type
argument_list|()
argument_list|,
name|engineIndex
operator|.
name|parsedDoc
argument_list|()
operator|.
name|dynamicMappingsUpdate
argument_list|()
argument_list|,
name|engineIndex
operator|.
name|id
argument_list|()
argument_list|,
name|allowMappingUpdates
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"[translog] recover [index] op [({}, {})] of [{}][{}]"
argument_list|,
name|index
operator|.
name|seqNo
argument_list|()
argument_list|,
name|index
operator|.
name|primaryTerm
argument_list|()
argument_list|,
name|index
operator|.
name|type
argument_list|()
argument_list|,
name|index
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|engine
argument_list|,
name|engineIndex
argument_list|)
expr_stmt|;
break|break;
case|case
name|DELETE
case|:
name|Translog
operator|.
name|Delete
name|delete
init|=
operator|(
name|Translog
operator|.
name|Delete
operator|)
name|operation
decl_stmt|;
name|Uid
name|uid
init|=
name|Uid
operator|.
name|createUid
argument_list|(
name|delete
operator|.
name|uid
argument_list|()
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"[translog] recover [delete] op [({}, {})] of [{}][{}]"
argument_list|,
name|delete
operator|.
name|seqNo
argument_list|()
argument_list|,
name|delete
operator|.
name|primaryTerm
argument_list|()
argument_list|,
name|uid
operator|.
name|type
argument_list|()
argument_list|,
name|uid
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Engine
operator|.
name|Delete
name|engineDelete
init|=
operator|new
name|Engine
operator|.
name|Delete
argument_list|(
name|uid
operator|.
name|type
argument_list|()
argument_list|,
name|uid
operator|.
name|id
argument_list|()
argument_list|,
name|delete
operator|.
name|uid
argument_list|()
argument_list|,
name|delete
operator|.
name|seqNo
argument_list|()
argument_list|,
name|delete
operator|.
name|primaryTerm
argument_list|()
argument_list|,
name|delete
operator|.
name|version
argument_list|()
argument_list|,
name|delete
operator|.
name|versionType
argument_list|()
operator|.
name|versionTypeForReplicationAndRecovery
argument_list|()
argument_list|,
name|origin
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
name|delete
argument_list|(
name|engine
argument_list|,
name|engineDelete
argument_list|)
expr_stmt|;
break|break;
case|case
name|NO_OP
case|:
specifier|final
name|Translog
operator|.
name|NoOp
name|noOp
init|=
operator|(
name|Translog
operator|.
name|NoOp
operator|)
name|operation
decl_stmt|;
specifier|final
name|long
name|seqNo
init|=
name|noOp
operator|.
name|seqNo
argument_list|()
decl_stmt|;
specifier|final
name|long
name|primaryTerm
init|=
name|noOp
operator|.
name|primaryTerm
argument_list|()
decl_stmt|;
specifier|final
name|String
name|reason
init|=
name|noOp
operator|.
name|reason
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"[translog] recover [no_op] op [({}, {})] of [{}]"
argument_list|,
name|seqNo
argument_list|,
name|primaryTerm
argument_list|,
name|reason
argument_list|)
expr_stmt|;
specifier|final
name|Engine
operator|.
name|NoOp
name|engineNoOp
init|=
operator|new
name|Engine
operator|.
name|NoOp
argument_list|(
literal|null
argument_list|,
name|seqNo
argument_list|,
name|primaryTerm
argument_list|,
literal|0
argument_list|,
name|VersionType
operator|.
name|INTERNAL
argument_list|,
name|origin
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|reason
argument_list|)
decl_stmt|;
name|noOp
argument_list|(
name|engine
argument_list|,
name|engineNoOp
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"No operation defined for ["
operator|+
name|operation
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|e
parameter_list|)
block|{
name|boolean
name|hasIgnoreOnRecoveryException
init|=
literal|false
decl_stmt|;
name|ElasticsearchException
name|current
init|=
name|e
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|instanceof
name|IgnoreOnRecoveryEngineException
condition|)
block|{
name|hasIgnoreOnRecoveryException
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|current
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ElasticsearchException
condition|)
block|{
name|current
operator|=
operator|(
name|ElasticsearchException
operator|)
name|current
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|hasIgnoreOnRecoveryException
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
name|operationProcessed
argument_list|()
expr_stmt|;
block|}
DECL|method|index
specifier|protected
name|void
name|index
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Engine
operator|.
name|Index
name|engineIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|engine
operator|.
name|index
argument_list|(
name|engineIndex
argument_list|)
expr_stmt|;
block|}
DECL|method|delete
specifier|protected
name|void
name|delete
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Engine
operator|.
name|Delete
name|engineDelete
parameter_list|)
throws|throws
name|IOException
block|{
name|engine
operator|.
name|delete
argument_list|(
name|engineDelete
argument_list|)
expr_stmt|;
block|}
DECL|method|noOp
specifier|protected
name|void
name|noOp
parameter_list|(
name|Engine
name|engine
parameter_list|,
name|Engine
operator|.
name|NoOp
name|engineNoOp
parameter_list|)
block|{
name|engine
operator|.
name|noOp
argument_list|(
name|engineNoOp
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called once for every processed operation by this recovery performer.      * This can be used to get progress information on the translog execution.      */
DECL|method|operationProcessed
specifier|protected
name|void
name|operationProcessed
parameter_list|()
block|{
comment|// noop
block|}
comment|/**      * Returns the recovered types modifying the mapping during the recovery      */
DECL|method|getRecoveredTypes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Mapping
argument_list|>
name|getRecoveredTypes
parameter_list|()
block|{
return|return
name|recoveredTypes
return|;
block|}
block|}
end_class

end_unit

