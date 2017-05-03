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
name|cluster
operator|.
name|routing
operator|.
name|GroupShardsIterator
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
name|search
operator|.
name|SearchPhaseResult
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
name|internal
operator|.
name|AliasFilter
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
name|Transport
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
name|Executor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
import|;
end_import

begin_class
DECL|class|SearchQueryThenFetchAsyncAction
specifier|final
class|class
name|SearchQueryThenFetchAsyncAction
extends|extends
name|AbstractSearchAsyncAction
argument_list|<
name|SearchPhaseResult
argument_list|>
block|{
DECL|field|searchPhaseController
specifier|private
specifier|final
name|SearchPhaseController
name|searchPhaseController
decl_stmt|;
DECL|method|SearchQueryThenFetchAsyncAction
name|SearchQueryThenFetchAsyncAction
parameter_list|(
specifier|final
name|Logger
name|logger
parameter_list|,
specifier|final
name|SearchTransportService
name|searchTransportService
parameter_list|,
specifier|final
name|BiFunction
argument_list|<
name|String
argument_list|,
name|String
argument_list|,
name|Transport
operator|.
name|Connection
argument_list|>
name|nodeIdToConnection
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|aliasFilter
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Float
argument_list|>
name|concreteIndexBoosts
parameter_list|,
specifier|final
name|SearchPhaseController
name|searchPhaseController
parameter_list|,
specifier|final
name|Executor
name|executor
parameter_list|,
specifier|final
name|SearchRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|,
specifier|final
name|GroupShardsIterator
argument_list|<
name|SearchShardIterator
argument_list|>
name|shardsIts
parameter_list|,
specifier|final
name|TransportSearchAction
operator|.
name|SearchTimeProvider
name|timeProvider
parameter_list|,
name|long
name|clusterStateVersion
parameter_list|,
name|SearchTask
name|task
parameter_list|)
block|{
name|super
argument_list|(
literal|"query"
argument_list|,
name|logger
argument_list|,
name|searchTransportService
argument_list|,
name|nodeIdToConnection
argument_list|,
name|aliasFilter
argument_list|,
name|concreteIndexBoosts
argument_list|,
name|executor
argument_list|,
name|request
argument_list|,
name|listener
argument_list|,
name|shardsIts
argument_list|,
name|timeProvider
argument_list|,
name|clusterStateVersion
argument_list|,
name|task
argument_list|,
name|searchPhaseController
operator|.
name|newSearchPhaseResults
argument_list|(
name|request
argument_list|,
name|shardsIts
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchPhaseController
operator|=
name|searchPhaseController
expr_stmt|;
block|}
DECL|method|executePhaseOnShard
specifier|protected
name|void
name|executePhaseOnShard
parameter_list|(
specifier|final
name|SearchShardIterator
name|shardIt
parameter_list|,
specifier|final
name|ShardRouting
name|shard
parameter_list|,
specifier|final
name|SearchActionListener
argument_list|<
name|SearchPhaseResult
argument_list|>
name|listener
parameter_list|)
block|{
name|getSearchTransport
argument_list|()
operator|.
name|sendExecuteQuery
argument_list|(
name|getConnection
argument_list|(
name|shardIt
operator|.
name|getClusterAlias
argument_list|()
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
argument_list|,
name|buildShardSearchRequest
argument_list|(
name|shardIt
argument_list|)
argument_list|,
name|getTask
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNextPhase
specifier|protected
name|SearchPhase
name|getNextPhase
parameter_list|(
specifier|final
name|SearchPhaseResults
argument_list|<
name|SearchPhaseResult
argument_list|>
name|results
parameter_list|,
specifier|final
name|SearchPhaseContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|FetchSearchPhase
argument_list|(
name|results
argument_list|,
name|searchPhaseController
argument_list|,
name|context
argument_list|)
return|;
block|}
block|}
end_class

end_unit

