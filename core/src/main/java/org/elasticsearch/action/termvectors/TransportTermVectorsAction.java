begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.termvectors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
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
name|single
operator|.
name|shard
operator|.
name|TransportSingleShardAction
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
name|termvectors
operator|.
name|TermVectorsService
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
comment|/**  * Performs the get operation.  */
end_comment

begin_class
DECL|class|TransportTermVectorsAction
specifier|public
class|class
name|TransportTermVectorsAction
extends|extends
name|TransportSingleShardAction
argument_list|<
name|TermVectorsRequest
argument_list|,
name|TermVectorsResponse
argument_list|>
block|{
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|termVectorsService
specifier|private
specifier|final
name|TermVectorsService
name|termVectorsService
decl_stmt|;
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|TermVectorsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|request
operator|.
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
name|Inject
DECL|method|TransportTermVectorsAction
specifier|public
name|TransportTermVectorsAction
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
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|TermVectorsService
name|termVectorsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|TermVectorsAction
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
name|TermVectorsRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GET
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
name|termVectorsService
operator|=
name|termVectorsService
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
name|state
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
name|getShards
argument_list|(
name|state
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
argument_list|,
name|request
operator|.
name|request
argument_list|()
operator|.
name|preference
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|resolveIndex
specifier|protected
name|boolean
name|resolveIndex
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|resolveRequest
specifier|protected
name|void
name|resolveRequest
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|InternalRequest
name|request
parameter_list|)
block|{
comment|// update the routing (request#index here is possibly an alias or a parent)
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
name|parent
argument_list|()
argument_list|,
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
comment|// Fail fast on the node that received the request.
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
operator|&&
name|state
operator|.
name|getMetaData
argument_list|()
operator|.
name|routingRequired
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
argument_list|)
condition|)
block|{
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
annotation|@
name|Override
DECL|method|shardOperation
specifier|protected
name|TermVectorsResponse
name|shardOperation
parameter_list|(
name|TermVectorsRequest
name|request
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|IndexService
name|indexService
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
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|indexService
operator|.
name|getShard
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|TermVectorsResponse
name|response
init|=
name|termVectorsService
operator|.
name|getTermVectors
argument_list|(
name|indexShard
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|response
operator|.
name|updateTookInMillis
argument_list|(
name|request
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|TermVectorsResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|TermVectorsResponse
argument_list|()
return|;
block|}
block|}
end_class

end_unit

