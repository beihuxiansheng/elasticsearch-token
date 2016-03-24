begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|indices
operator|.
name|IndexClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|action
operator|.
name|SearchTransportService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|controller
operator|.
name|SearchPhaseController
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
name|Map
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
operator|.
name|SearchType
operator|.
name|QUERY_AND_FETCH
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
name|search
operator|.
name|SearchType
operator|.
name|QUERY_THEN_FETCH
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportSearchAction
specifier|public
class|class
name|TransportSearchAction
extends|extends
name|HandledTransportAction
argument_list|<
name|SearchRequest
argument_list|,
name|SearchResponse
argument_list|>
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|searchTransportService
specifier|private
specifier|final
name|SearchTransportService
name|searchTransportService
decl_stmt|;
DECL|field|searchPhaseController
specifier|private
specifier|final
name|SearchPhaseController
name|searchPhaseController
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportSearchAction
specifier|public
name|TransportSearchAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|SearchPhaseController
name|searchPhaseController
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|SearchTransportService
name|searchTransportService
parameter_list|,
name|ClusterService
name|clusterService
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
name|SearchAction
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
name|SearchRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchPhaseController
operator|=
name|searchPhaseController
expr_stmt|;
name|this
operator|.
name|searchTransportService
operator|=
name|searchTransportService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
comment|// optimize search type for cases where there is only one shard group to search on
try|try
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|String
index|[]
name|concreteIndices
init|=
name|indexNameExpressionResolver
operator|.
name|concreteIndexNames
argument_list|(
name|clusterState
argument_list|,
name|searchRequest
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|routingMap
init|=
name|indexNameExpressionResolver
operator|.
name|resolveSearchRouting
argument_list|(
name|clusterState
argument_list|,
name|searchRequest
operator|.
name|routing
argument_list|()
argument_list|,
name|searchRequest
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|shardCount
init|=
name|clusterService
operator|.
name|operationRouting
argument_list|()
operator|.
name|searchShardsCount
argument_list|(
name|clusterState
argument_list|,
name|concreteIndices
argument_list|,
name|routingMap
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardCount
operator|==
literal|1
condition|)
block|{
comment|// if we only have one group, then we always want Q_A_F, no need for DFS, and no need to do THEN since we hit one shard
name|searchRequest
operator|.
name|searchType
argument_list|(
name|QUERY_AND_FETCH
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchRequest
operator|.
name|isSuggestOnly
argument_list|()
condition|)
block|{
comment|// disable request cache if we have only suggest
name|searchRequest
operator|.
name|requestCache
argument_list|(
literal|false
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|searchRequest
operator|.
name|searchType
argument_list|()
condition|)
block|{
case|case
name|DFS_QUERY_AND_FETCH
case|:
case|case
name|DFS_QUERY_THEN_FETCH
case|:
comment|// convert to Q_T_F if we have only suggest
name|searchRequest
operator|.
name|searchType
argument_list|(
name|QUERY_THEN_FETCH
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IndexNotFoundException
decl||
name|IndexClosedException
name|e
parameter_list|)
block|{
comment|// ignore these failures, we will notify the search response if its really the case from the actual action
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to optimize search type, continue as normal"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|AbstractSearchAsyncAction
name|searchAsyncAction
decl_stmt|;
switch|switch
condition|(
name|searchRequest
operator|.
name|searchType
argument_list|()
condition|)
block|{
case|case
name|DFS_QUERY_THEN_FETCH
case|:
name|searchAsyncAction
operator|=
operator|new
name|SearchDfsQueryThenFetchAsyncAction
argument_list|(
name|logger
argument_list|,
name|searchTransportService
argument_list|,
name|clusterService
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|searchPhaseController
argument_list|,
name|threadPool
argument_list|,
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
break|break;
case|case
name|QUERY_THEN_FETCH
case|:
name|searchAsyncAction
operator|=
operator|new
name|SearchQueryThenFetchAsyncAction
argument_list|(
name|logger
argument_list|,
name|searchTransportService
argument_list|,
name|clusterService
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|searchPhaseController
argument_list|,
name|threadPool
argument_list|,
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
break|break;
case|case
name|DFS_QUERY_AND_FETCH
case|:
name|searchAsyncAction
operator|=
operator|new
name|SearchDfsQueryAndFetchAsyncAction
argument_list|(
name|logger
argument_list|,
name|searchTransportService
argument_list|,
name|clusterService
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|searchPhaseController
argument_list|,
name|threadPool
argument_list|,
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
break|break;
case|case
name|QUERY_AND_FETCH
case|:
name|searchAsyncAction
operator|=
operator|new
name|SearchQueryAndFetchAsyncAction
argument_list|(
name|logger
argument_list|,
name|searchTransportService
argument_list|,
name|clusterService
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|searchPhaseController
argument_list|,
name|threadPool
argument_list|,
name|searchRequest
argument_list|,
name|listener
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown search type: ["
operator|+
name|searchRequest
operator|.
name|searchType
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|searchAsyncAction
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

