begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.update
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|update
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

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
name|ElasticSearchIllegalArgumentException
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
name|delete
operator|.
name|TransportDeleteAction
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
name|index
operator|.
name|TransportIndexAction
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
name|instance
operator|.
name|TransportInstanceSingleOperationAction
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
name|PlainShardIterator
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
name|cluster
operator|.
name|routing
operator|.
name|ShardRouting
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
name|unit
operator|.
name|TimeValue
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
name|engine
operator|.
name|DocumentMissingException
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
name|DocumentSourceMissingException
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
name|mapper
operator|.
name|internal
operator|.
name|ParentFieldMapper
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
name|internal
operator|.
name|RoutingFieldMapper
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
name|internal
operator|.
name|SourceFieldMapper
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
name|internal
operator|.
name|TTLFieldMapper
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
name|IllegalIndexShardStateException
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
name|script
operator|.
name|ExecutableScript
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TransportUpdateAction
specifier|public
class|class
name|TransportUpdateAction
extends|extends
name|TransportInstanceSingleOperationAction
argument_list|<
name|UpdateRequest
argument_list|,
name|UpdateResponse
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|deleteAction
specifier|private
specifier|final
name|TransportDeleteAction
name|deleteAction
decl_stmt|;
DECL|field|indexAction
specifier|private
specifier|final
name|TransportIndexAction
name|indexAction
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportUpdateAction
specifier|public
name|TransportUpdateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
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
name|TransportIndexAction
name|indexAction
parameter_list|,
name|TransportDeleteAction
name|deleteAction
parameter_list|,
name|ScriptService
name|scriptService
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
name|indexAction
operator|=
name|indexAction
expr_stmt|;
name|this
operator|.
name|deleteAction
operator|=
name|deleteAction
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
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
name|UPDATE
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
DECL|method|newRequest
specifier|protected
name|UpdateRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|UpdateRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|UpdateResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|UpdateResponse
argument_list|()
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
name|UpdateRequest
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
name|UpdateRequest
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
DECL|method|retryOnFailure
specifier|protected
name|boolean
name|retryOnFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|IllegalIndexShardStateException
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
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
name|UpdateRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|request
operator|.
name|shardId
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
return|return
name|clusterState
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
name|primaryShardIt
argument_list|()
return|;
block|}
name|ShardIterator
name|shardIterator
init|=
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
decl_stmt|;
name|ShardRouting
name|shard
decl_stmt|;
while|while
condition|(
operator|(
name|shard
operator|=
name|shardIterator
operator|.
name|nextOrNull
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
condition|)
block|{
return|return
operator|new
name|PlainShardIterator
argument_list|(
name|shardIterator
operator|.
name|shardId
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|shard
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|PlainShardIterator
argument_list|(
name|shardIterator
operator|.
name|shardId
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|ShardRouting
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|void
name|shardOperation
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|shardOperation
specifier|protected
name|void
name|shardOperation
parameter_list|(
specifier|final
name|UpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|UpdateResponse
argument_list|>
name|listener
parameter_list|,
specifier|final
name|int
name|retryCount
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
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|getDate
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
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
operator|new
name|String
index|[]
block|{
name|SourceFieldMapper
operator|.
name|NAME
block|,
name|RoutingFieldMapper
operator|.
name|NAME
block|,
name|ParentFieldMapper
operator|.
name|NAME
block|,
name|TTLFieldMapper
operator|.
name|NAME
block|}
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// no doc, what to do, what to do...
if|if
condition|(
operator|!
name|getResult
operator|.
name|exists
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|DocumentMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
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
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|getResult
operator|.
name|internalSourceRef
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// no source, we can't do nothing, through a failure...
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|DocumentSourceMissingException
argument_list|(
operator|new
name|ShardId
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
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
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|getResult
operator|.
name|internalSourceRef
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|getResult
operator|.
name|internalSourceRef
argument_list|()
operator|.
name|offset
argument_list|()
argument_list|,
name|getResult
operator|.
name|internalSourceRef
argument_list|()
operator|.
name|length
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
name|sourceAndContent
operator|.
name|v2
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"_source"
argument_list|,
name|source
argument_list|)
expr_stmt|;
try|try
block|{
name|ExecutableScript
name|script
init|=
name|scriptService
operator|.
name|executable
argument_list|(
name|request
operator|.
name|scriptLang
argument_list|,
name|request
operator|.
name|script
argument_list|,
name|request
operator|.
name|scriptParams
argument_list|)
decl_stmt|;
name|script
operator|.
name|setNextVar
argument_list|(
literal|"ctx"
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
comment|// we need to unwrap the ctx...
name|ctx
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|script
operator|.
name|unwrap
argument_list|(
name|ctx
argument_list|)
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
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"failed to execute script"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|String
name|operation
init|=
operator|(
name|String
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"op"
argument_list|)
decl_stmt|;
name|String
name|timestamp
init|=
operator|(
name|String
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"_timestamp"
argument_list|)
decl_stmt|;
name|Long
name|ttl
init|=
literal|null
decl_stmt|;
name|Object
name|fetchedTTL
init|=
name|ctx
operator|.
name|get
argument_list|(
literal|"_ttl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetchedTTL
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fetchedTTL
operator|instanceof
name|Number
condition|)
block|{
name|ttl
operator|=
operator|(
operator|(
name|Number
operator|)
name|fetchedTTL
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|ttl
operator|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
operator|(
name|String
operator|)
name|fetchedTTL
argument_list|,
literal|null
argument_list|)
operator|.
name|millis
argument_list|()
expr_stmt|;
block|}
block|}
name|source
operator|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|ctx
operator|.
name|get
argument_list|(
literal|"_source"
argument_list|)
expr_stmt|;
comment|// apply script to update the source
name|String
name|routing
init|=
name|getResult
operator|.
name|fields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|RoutingFieldMapper
operator|.
name|NAME
argument_list|)
condition|?
name|getResult
operator|.
name|field
argument_list|(
name|RoutingFieldMapper
operator|.
name|NAME
argument_list|)
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|null
decl_stmt|;
name|String
name|parent
init|=
name|getResult
operator|.
name|fields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|)
condition|?
name|getResult
operator|.
name|field
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|)
operator|.
name|value
argument_list|()
operator|.
name|toString
argument_list|()
else|:
literal|null
decl_stmt|;
comment|// No TTL has been given in the update script so we keep previous TTL value if there is one
if|if
condition|(
name|ttl
operator|==
literal|null
condition|)
block|{
name|ttl
operator|=
name|getResult
operator|.
name|fields
argument_list|()
operator|.
name|containsKey
argument_list|(
name|TTLFieldMapper
operator|.
name|NAME
argument_list|)
condition|?
operator|(
name|Long
operator|)
name|getResult
operator|.
name|field
argument_list|(
name|TTLFieldMapper
operator|.
name|NAME
argument_list|)
operator|.
name|value
argument_list|()
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|ttl
operator|!=
literal|null
condition|)
block|{
name|ttl
operator|=
name|ttl
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|getDate
operator|)
expr_stmt|;
comment|// It is an approximation of exact TTL value, could be improved
block|}
block|}
comment|// TODO: external version type, does it make sense here? does not seem like it...
if|if
condition|(
name|operation
operator|==
literal|null
operator|||
literal|"index"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
name|IndexRequest
name|indexRequest
init|=
name|Requests
operator|.
name|indexRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|,
name|sourceAndContent
operator|.
name|v1
argument_list|()
argument_list|)
operator|.
name|version
argument_list|(
name|getResult
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|replicationType
argument_list|(
name|request
operator|.
name|replicationType
argument_list|()
argument_list|)
operator|.
name|consistencyLevel
argument_list|(
name|request
operator|.
name|consistencyLevel
argument_list|()
argument_list|)
operator|.
name|timestamp
argument_list|(
name|timestamp
argument_list|)
operator|.
name|ttl
argument_list|(
name|ttl
argument_list|)
operator|.
name|percolate
argument_list|(
name|request
operator|.
name|percolate
argument_list|()
argument_list|)
decl_stmt|;
name|indexRequest
operator|.
name|operationThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|indexAction
operator|.
name|execute
argument_list|(
name|indexRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|IndexResponse
name|response
parameter_list|)
block|{
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|response
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|type
argument_list|()
argument_list|,
name|response
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|update
operator|.
name|matches
argument_list|(
name|response
operator|.
name|matches
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|update
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
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|VersionConflictEngineException
condition|)
block|{
if|if
condition|(
name|retryCount
operator|<
name|request
operator|.
name|retryOnConflict
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
name|retryCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"delete"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
name|DeleteRequest
name|deleteRequest
init|=
name|Requests
operator|.
name|deleteRequest
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|type
argument_list|(
name|request
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|id
argument_list|(
name|request
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
operator|.
name|parent
argument_list|(
name|parent
argument_list|)
operator|.
name|version
argument_list|(
name|getResult
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|replicationType
argument_list|(
name|request
operator|.
name|replicationType
argument_list|()
argument_list|)
operator|.
name|consistencyLevel
argument_list|(
name|request
operator|.
name|consistencyLevel
argument_list|()
argument_list|)
decl_stmt|;
name|deleteRequest
operator|.
name|operationThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|deleteAction
operator|.
name|execute
argument_list|(
name|deleteRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|DeleteResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|DeleteResponse
name|response
parameter_list|)
block|{
name|UpdateResponse
name|update
init|=
operator|new
name|UpdateResponse
argument_list|(
name|response
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|type
argument_list|()
argument_list|,
name|response
operator|.
name|id
argument_list|()
argument_list|,
name|response
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|update
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
name|e
operator|=
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|VersionConflictEngineException
condition|)
block|{
if|if
condition|(
name|retryCount
operator|<
name|request
operator|.
name|retryOnConflict
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|executor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|shardOperation
argument_list|(
name|request
argument_list|,
name|listener
argument_list|,
name|retryCount
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|operation
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|UpdateResponse
argument_list|(
name|getResult
operator|.
name|index
argument_list|()
argument_list|,
name|getResult
operator|.
name|type
argument_list|()
argument_list|,
name|getResult
operator|.
name|id
argument_list|()
argument_list|,
name|getResult
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Used update operation [{}] for script [{}], doing nothing..."
argument_list|,
name|operation
argument_list|,
name|request
operator|.
name|script
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|UpdateResponse
argument_list|(
name|getResult
operator|.
name|index
argument_list|()
argument_list|,
name|getResult
operator|.
name|type
argument_list|()
argument_list|,
name|getResult
operator|.
name|id
argument_list|()
argument_list|,
name|getResult
operator|.
name|version
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

