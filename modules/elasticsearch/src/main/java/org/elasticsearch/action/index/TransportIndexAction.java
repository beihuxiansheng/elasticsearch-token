begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
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
name|ShardsIterator
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
name|UUID
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
name|mapper
operator|.
name|DocumentMapper
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
name|ParsedDocument
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Performs the index operation.  *  *<p>Allows for the following settings:  *<ul>  *<li><b>autoCreateIndex</b>: When set to<tt>true</tt>, will automatically create an index if one does not exists.  * Defaults to<tt>true</tt>.  *<li><b>allowIdGeneration</b>: If the id is set not, should it be generated. Defaults to<tt>true</tt>.  *</ul>  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportIndexAction
specifier|public
class|class
name|TransportIndexAction
extends|extends
name|TransportShardReplicationOperationAction
argument_list|<
name|IndexRequest
argument_list|,
name|IndexResponse
argument_list|>
block|{
DECL|field|autoCreateIndex
specifier|private
specifier|final
name|boolean
name|autoCreateIndex
decl_stmt|;
DECL|field|allowIdGeneration
specifier|private
specifier|final
name|boolean
name|allowIdGeneration
decl_stmt|;
DECL|field|createIndexAction
specifier|private
specifier|final
name|TransportCreateIndexAction
name|createIndexAction
decl_stmt|;
DECL|field|mappingUpdatedAction
specifier|private
specifier|final
name|MappingUpdatedAction
name|mappingUpdatedAction
decl_stmt|;
DECL|method|TransportIndexAction
annotation|@
name|Inject
specifier|public
name|TransportIndexAction
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
name|MappingUpdatedAction
name|mappingUpdatedAction
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
name|this
operator|.
name|createIndexAction
operator|=
name|createIndexAction
expr_stmt|;
name|this
operator|.
name|mappingUpdatedAction
operator|=
name|mappingUpdatedAction
expr_stmt|;
name|this
operator|.
name|autoCreateIndex
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"action.auto_create_index"
argument_list|,
literal|true
argument_list|)
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
block|}
DECL|method|doExecute
annotation|@
name|Override
specifier|protected
name|void
name|doExecute
parameter_list|(
specifier|final
name|IndexRequest
name|indexRequest
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|allowIdGeneration
condition|)
block|{
if|if
condition|(
name|indexRequest
operator|.
name|id
argument_list|()
operator|==
literal|null
condition|)
block|{
name|indexRequest
operator|.
name|id
argument_list|(
name|UUID
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// since we generate the id, change it to CREATE
name|indexRequest
operator|.
name|opType
argument_list|(
name|IndexRequest
operator|.
name|OpType
operator|.
name|CREATE
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|autoCreateIndex
operator|&&
operator|!
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|hasConcreteIndex
argument_list|(
name|indexRequest
operator|.
name|index
argument_list|()
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
name|indexRequest
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|cause
argument_list|(
literal|"auto(index api)"
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
name|TransportIndexAction
operator|.
name|super
operator|.
name|doExecute
argument_list|(
name|indexRequest
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
name|TransportIndexAction
operator|.
name|super
operator|.
name|doExecute
argument_list|(
name|indexRequest
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
name|super
operator|.
name|doExecute
argument_list|(
name|indexRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkWriteConsistency
annotation|@
name|Override
specifier|protected
name|boolean
name|checkWriteConsistency
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|newRequestInstance
annotation|@
name|Override
specifier|protected
name|IndexRequest
name|newRequestInstance
parameter_list|()
block|{
return|return
operator|new
name|IndexRequest
argument_list|()
return|;
block|}
DECL|method|newResponseInstance
annotation|@
name|Override
specifier|protected
name|IndexResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|IndexResponse
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
name|TransportActions
operator|.
name|INDEX
return|;
block|}
DECL|method|checkBlock
annotation|@
name|Override
specifier|protected
name|void
name|checkBlock
parameter_list|(
name|IndexRequest
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
name|WRITE
argument_list|,
name|request
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|IndexRequest
name|request
parameter_list|)
block|{
return|return
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|indexShards
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
name|routing
argument_list|()
argument_list|)
return|;
block|}
DECL|method|shardOperationOnPrimary
annotation|@
name|Override
specifier|protected
name|IndexResponse
name|shardOperationOnPrimary
parameter_list|(
name|ShardOperationRequest
name|shardRequest
parameter_list|)
block|{
name|IndexShard
name|indexShard
init|=
name|indexShard
argument_list|(
name|shardRequest
argument_list|)
decl_stmt|;
specifier|final
name|IndexRequest
name|request
init|=
name|shardRequest
operator|.
name|request
decl_stmt|;
name|ParsedDocument
name|doc
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|opType
argument_list|()
operator|==
name|IndexRequest
operator|.
name|OpType
operator|.
name|INDEX
condition|)
block|{
name|Engine
operator|.
name|Index
name|index
init|=
name|indexShard
operator|.
name|prepareIndex
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
name|source
argument_list|()
argument_list|)
decl_stmt|;
name|index
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|indexShard
operator|.
name|index
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Engine
operator|.
name|Create
name|create
init|=
name|indexShard
operator|.
name|prepareCreate
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
name|source
argument_list|()
argument_list|)
decl_stmt|;
name|create
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|indexShard
operator|.
name|create
argument_list|(
name|create
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doc
operator|.
name|mappersAdded
argument_list|()
condition|)
block|{
name|updateMappingOnMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|IndexResponse
argument_list|(
name|request
operator|.
name|index
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
argument_list|)
return|;
block|}
DECL|method|shardOperationOnReplica
annotation|@
name|Override
specifier|protected
name|void
name|shardOperationOnReplica
parameter_list|(
name|ShardOperationRequest
name|shardRequest
parameter_list|)
block|{
name|IndexShard
name|indexShard
init|=
name|indexShard
argument_list|(
name|shardRequest
argument_list|)
decl_stmt|;
name|IndexRequest
name|request
init|=
name|shardRequest
operator|.
name|request
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|opType
argument_list|()
operator|==
name|IndexRequest
operator|.
name|OpType
operator|.
name|INDEX
condition|)
block|{
name|Engine
operator|.
name|Index
name|index
init|=
name|indexShard
operator|.
name|prepareIndex
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
name|source
argument_list|()
argument_list|)
decl_stmt|;
name|index
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|)
expr_stmt|;
name|indexShard
operator|.
name|index
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Engine
operator|.
name|Create
name|create
init|=
name|indexShard
operator|.
name|prepareCreate
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
name|source
argument_list|()
argument_list|)
decl_stmt|;
name|create
operator|.
name|refresh
argument_list|(
name|request
operator|.
name|refresh
argument_list|()
argument_list|)
expr_stmt|;
name|indexShard
operator|.
name|create
argument_list|(
name|create
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|updateMappingOnMaster
specifier|private
name|void
name|updateMappingOnMaster
parameter_list|(
specifier|final
name|IndexRequest
name|request
parameter_list|)
block|{
try|try
block|{
name|MapperService
name|mapperService
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
operator|.
name|mapperService
argument_list|()
decl_stmt|;
specifier|final
name|DocumentMapper
name|documentMapper
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|documentMapper
operator|.
name|refreshSource
argument_list|()
expr_stmt|;
name|mappingUpdatedAction
operator|.
name|execute
argument_list|(
operator|new
name|MappingUpdatedAction
operator|.
name|MappingUpdatedRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|documentMapper
operator|.
name|mappingSource
argument_list|()
argument_list|)
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|MappingUpdatedAction
operator|.
name|MappingUpdatedResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MappingUpdatedAction
operator|.
name|MappingUpdatedResponse
name|mappingUpdatedResponse
parameter_list|)
block|{
comment|// all is well
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
try|try
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to update master on updated mapping for index ["
operator|+
name|request
operator|.
name|index
argument_list|()
operator|+
literal|"], type ["
operator|+
name|request
operator|.
name|type
argument_list|()
operator|+
literal|"] and source ["
operator|+
name|documentMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
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
name|warn
argument_list|(
literal|"Failed to update master on updated mapping for index ["
operator|+
name|request
operator|.
name|index
argument_list|()
operator|+
literal|"], type ["
operator|+
name|request
operator|.
name|type
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

