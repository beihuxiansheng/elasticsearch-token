begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.cache.clear
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
name|cache
operator|.
name|clear
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
name|broadcast
operator|.
name|node
operator|.
name|TransportBroadcastByNodeAction
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
name|List
import|;
end_import

begin_comment
comment|/**  * Indices clear cache action.  */
end_comment

begin_class
DECL|class|TransportClearIndicesCacheAction
specifier|public
class|class
name|TransportClearIndicesCacheAction
extends|extends
name|TransportBroadcastByNodeAction
argument_list|<
name|ClearIndicesCacheRequest
argument_list|,
name|ClearIndicesCacheResponse
argument_list|,
name|TransportBroadcastByNodeAction
operator|.
name|EmptyResult
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClearIndicesCacheAction
specifier|public
name|TransportClearIndicesCacheAction
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
name|ClearIndicesCacheAction
operator|.
name|NAME
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|ClearIndicesCacheRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readShardResult
specifier|protected
name|EmptyResult
name|readShardResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|EmptyResult
operator|.
name|readEmptyResultFrom
argument_list|(
name|in
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClearIndicesCacheResponse
name|newResponse
parameter_list|(
name|ClearIndicesCacheRequest
name|request
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|int
name|failedShards
parameter_list|,
name|List
argument_list|<
name|EmptyResult
argument_list|>
name|responses
parameter_list|,
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
return|return
operator|new
name|ClearIndicesCacheResponse
argument_list|(
name|totalShards
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|shardFailures
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|readRequestFrom
specifier|protected
name|ClearIndicesCacheRequest
name|readRequestFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|ClearIndicesCacheRequest
name|request
init|=
operator|new
name|ClearIndicesCacheRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|request
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|EmptyResult
name|shardOperation
parameter_list|(
name|ClearIndicesCacheRequest
name|request
parameter_list|,
name|ShardRouting
name|shardRouting
parameter_list|)
block|{
name|IndexService
name|service
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|IndexShard
name|shard
init|=
name|service
operator|.
name|getShardOrNull
argument_list|(
name|shardRouting
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|clearedAtLeastOne
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|queryCache
argument_list|()
condition|)
block|{
name|clearedAtLeastOne
operator|=
literal|true
expr_stmt|;
name|service
operator|.
name|cache
argument_list|()
operator|.
name|query
argument_list|()
operator|.
name|clear
argument_list|(
literal|"api"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|fieldDataCache
argument_list|()
condition|)
block|{
name|clearedAtLeastOne
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|==
literal|null
operator|||
name|request
operator|.
name|fields
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|service
operator|.
name|fieldData
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|field
range|:
name|request
operator|.
name|fields
argument_list|()
control|)
block|{
name|service
operator|.
name|fieldData
argument_list|()
operator|.
name|clearField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|request
operator|.
name|requestCache
argument_list|()
condition|)
block|{
name|clearedAtLeastOne
operator|=
literal|true
expr_stmt|;
name|indicesService
operator|.
name|clearRequestCache
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|recycler
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Clear CacheRecycler on index [{}]"
argument_list|,
name|service
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|clearedAtLeastOne
operator|=
literal|true
expr_stmt|;
comment|// cacheRecycler.clear();
block|}
if|if
condition|(
operator|!
name|clearedAtLeastOne
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|fields
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// only clear caches relating to the specified fields
for|for
control|(
name|String
name|field
range|:
name|request
operator|.
name|fields
argument_list|()
control|)
block|{
name|service
operator|.
name|fieldData
argument_list|()
operator|.
name|clearField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|service
operator|.
name|cache
argument_list|()
operator|.
name|clear
argument_list|(
literal|"api"
argument_list|)
expr_stmt|;
name|service
operator|.
name|fieldData
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|indicesService
operator|.
name|clearRequestCache
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|EmptyResult
operator|.
name|INSTANCE
return|;
block|}
comment|/**      * The refresh request works against *all* shards.      */
annotation|@
name|Override
DECL|method|shards
specifier|protected
name|ShardsIterator
name|shards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|ClearIndicesCacheRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|)
block|{
return|return
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|allShards
argument_list|(
name|concreteIndices
argument_list|)
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
name|ClearIndicesCacheRequest
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
name|METADATA_WRITE
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
name|ClearIndicesCacheRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
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
name|METADATA_WRITE
argument_list|,
name|concreteIndices
argument_list|)
return|;
block|}
block|}
end_class

end_unit

