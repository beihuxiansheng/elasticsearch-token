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
name|HandledTransportAction
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
name|util
operator|.
name|concurrent
operator|.
name|AtomicArray
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
name|IndexNotFoundException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_class
DECL|class|TransportMultiTermVectorsAction
specifier|public
class|class
name|TransportMultiTermVectorsAction
extends|extends
name|HandledTransportAction
argument_list|<
name|MultiTermVectorsRequest
argument_list|,
name|MultiTermVectorsResponse
argument_list|>
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|shardAction
specifier|private
specifier|final
name|TransportShardMultiTermsVectorAction
name|shardAction
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportMultiTermVectorsAction
specifier|public
name|TransportMultiTermVectorsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportShardMultiTermsVectorAction
name|shardAction
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
name|MultiTermVectorsAction
operator|.
name|NAME
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|MultiTermVectorsRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|shardAction
operator|=
name|shardAction
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
name|MultiTermVectorsRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|MultiTermVectorsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|clusterState
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedRaiseException
argument_list|(
name|ClusterBlockLevel
operator|.
name|READ
argument_list|)
expr_stmt|;
specifier|final
name|AtomicArray
argument_list|<
name|MultiTermVectorsItemResponse
argument_list|>
name|responses
init|=
operator|new
name|AtomicArray
argument_list|<>
argument_list|(
name|request
operator|.
name|requests
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ShardId
argument_list|,
name|MultiTermVectorsShardRequest
argument_list|>
name|shardRequests
init|=
operator|new
name|HashMap
argument_list|<>
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
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TermVectorsRequest
name|termVectorsRequest
init|=
name|request
operator|.
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|termVectorsRequest
operator|.
name|routing
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|resolveIndexRouting
argument_list|(
name|termVectorsRequest
operator|.
name|parent
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|routing
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|hasConcreteIndex
argument_list|(
name|termVectorsRequest
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|responses
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|new
name|MultiTermVectorsItemResponse
argument_list|(
literal|null
argument_list|,
operator|new
name|MultiTermVectorsResponse
operator|.
name|Failure
argument_list|(
name|termVectorsRequest
operator|.
name|index
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|type
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|id
argument_list|()
argument_list|,
operator|new
name|IndexNotFoundException
argument_list|(
name|termVectorsRequest
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|concreteSingleIndex
init|=
name|indexNameExpressionResolver
operator|.
name|concreteSingleIndex
argument_list|(
name|clusterState
argument_list|,
name|termVectorsRequest
argument_list|)
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|termVectorsRequest
operator|.
name|routing
argument_list|()
operator|==
literal|null
operator|&&
name|clusterState
operator|.
name|getMetaData
argument_list|()
operator|.
name|routingRequired
argument_list|(
name|concreteSingleIndex
argument_list|,
name|termVectorsRequest
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
name|responses
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|new
name|MultiTermVectorsItemResponse
argument_list|(
literal|null
argument_list|,
operator|new
name|MultiTermVectorsResponse
operator|.
name|Failure
argument_list|(
name|concreteSingleIndex
argument_list|,
name|termVectorsRequest
operator|.
name|type
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|id
argument_list|()
argument_list|,
operator|new
name|IllegalArgumentException
argument_list|(
literal|"routing is required for ["
operator|+
name|concreteSingleIndex
operator|+
literal|"]/["
operator|+
name|termVectorsRequest
operator|.
name|type
argument_list|()
operator|+
literal|"]/["
operator|+
name|termVectorsRequest
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ShardId
name|shardId
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|shardId
argument_list|(
name|clusterState
argument_list|,
name|concreteSingleIndex
argument_list|,
name|termVectorsRequest
operator|.
name|id
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|routing
argument_list|()
argument_list|)
decl_stmt|;
name|MultiTermVectorsShardRequest
name|shardRequest
init|=
name|shardRequests
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardRequest
operator|==
literal|null
condition|)
block|{
name|shardRequest
operator|=
operator|new
name|MultiTermVectorsShardRequest
argument_list|(
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|shardRequest
operator|.
name|preference
argument_list|(
name|request
operator|.
name|preference
argument_list|)
expr_stmt|;
name|shardRequests
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|shardRequest
argument_list|)
expr_stmt|;
block|}
name|shardRequest
operator|.
name|add
argument_list|(
name|i
argument_list|,
name|termVectorsRequest
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardRequests
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// only failures..
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|MultiTermVectorsResponse
argument_list|(
name|responses
operator|.
name|toArray
argument_list|(
operator|new
name|MultiTermVectorsItemResponse
index|[
name|responses
operator|.
name|length
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|(
name|shardRequests
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|MultiTermVectorsShardRequest
name|shardRequest
range|:
name|shardRequests
operator|.
name|values
argument_list|()
control|)
block|{
name|shardAction
operator|.
name|execute
argument_list|(
name|shardRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|MultiTermVectorsShardResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|MultiTermVectorsShardResponse
name|response
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|response
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
name|responses
operator|.
name|set
argument_list|(
name|response
operator|.
name|locations
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|MultiTermVectorsItemResponse
argument_list|(
name|response
operator|.
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|response
operator|.
name|failures
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// create failures for all relevant requests
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shardRequest
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
name|TermVectorsRequest
name|termVectorsRequest
init|=
name|shardRequest
operator|.
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|responses
operator|.
name|set
argument_list|(
name|shardRequest
operator|.
name|locations
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
operator|new
name|MultiTermVectorsItemResponse
argument_list|(
literal|null
argument_list|,
operator|new
name|MultiTermVectorsResponse
operator|.
name|Failure
argument_list|(
name|shardRequest
operator|.
name|index
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|type
argument_list|()
argument_list|,
name|termVectorsRequest
operator|.
name|id
argument_list|()
argument_list|,
name|e
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|counter
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finishHim
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|finishHim
parameter_list|()
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|MultiTermVectorsResponse
argument_list|(
name|responses
operator|.
name|toArray
argument_list|(
operator|new
name|MultiTermVectorsItemResponse
index|[
name|responses
operator|.
name|length
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

