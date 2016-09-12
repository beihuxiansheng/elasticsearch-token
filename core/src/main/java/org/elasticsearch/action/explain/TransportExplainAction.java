begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.explain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|explain
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Explanation
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
name|lease
operator|.
name|Releasables
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
name|mapper
operator|.
name|UidFieldMapper
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
name|search
operator|.
name|SearchService
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
name|SearchContext
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
name|ShardSearchLocalRequest
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
name|rescore
operator|.
name|RescoreSearchContext
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
name|rescore
operator|.
name|Rescorer
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
comment|/**  * Explain transport action. Computes the explain on the targeted shard.  */
end_comment

begin_comment
comment|// TODO: AggregatedDfs. Currently the idf can be different then when executing a normal search with explain.
end_comment

begin_class
DECL|class|TransportExplainAction
specifier|public
class|class
name|TransportExplainAction
extends|extends
name|TransportSingleShardAction
argument_list|<
name|ExplainRequest
argument_list|,
name|ExplainResponse
argument_list|>
block|{
DECL|field|searchService
specifier|private
specifier|final
name|SearchService
name|searchService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportExplainAction
specifier|public
name|TransportExplainAction
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
name|SearchService
name|searchService
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
name|ExplainAction
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
name|ExplainRequest
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
name|searchService
operator|=
name|searchService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ExplainRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|ExplainResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|request
operator|.
name|nowInMillis
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
name|Override
DECL|method|resolveIndex
specifier|protected
name|boolean
name|resolveIndex
parameter_list|(
name|ExplainRequest
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
name|request
operator|.
name|request
argument_list|()
operator|.
name|filteringAlias
argument_list|(
name|indexNameExpressionResolver
operator|.
name|filteringAliases
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
name|ExplainResponse
name|shardOperation
parameter_list|(
name|ExplainRequest
name|request
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|ShardSearchLocalRequest
name|shardSearchLocalRequest
init|=
operator|new
name|ShardSearchLocalRequest
argument_list|(
name|shardId
argument_list|,
operator|new
name|String
index|[]
block|{
name|request
operator|.
name|type
argument_list|()
block|}
argument_list|,
name|request
operator|.
name|nowInMillis
argument_list|,
name|request
operator|.
name|filteringAlias
argument_list|()
argument_list|)
decl_stmt|;
name|SearchContext
name|context
init|=
name|searchService
operator|.
name|createSearchContext
argument_list|(
name|shardSearchLocalRequest
argument_list|,
name|SearchService
operator|.
name|NO_TIMEOUT
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Term
name|uidTerm
init|=
operator|new
name|Term
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUidAsBytes
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
argument_list|)
argument_list|)
decl_stmt|;
name|SearchContext
operator|.
name|setCurrent
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|Engine
operator|.
name|GetResult
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|context
operator|.
name|indexShard
argument_list|()
operator|.
name|get
argument_list|(
operator|new
name|Engine
operator|.
name|Get
argument_list|(
literal|false
argument_list|,
name|uidTerm
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
operator|new
name|ExplainResponse
argument_list|(
name|shardId
operator|.
name|getIndexName
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
literal|false
argument_list|)
return|;
block|}
name|context
operator|.
name|parsedQuery
argument_list|(
name|context
operator|.
name|getQueryShardContext
argument_list|()
operator|.
name|toQuery
argument_list|(
name|request
operator|.
name|query
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|preProcess
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|int
name|topLevelDocId
init|=
name|result
operator|.
name|docIdAndVersion
argument_list|()
operator|.
name|docId
operator|+
name|result
operator|.
name|docIdAndVersion
argument_list|()
operator|.
name|context
operator|.
name|docBase
decl_stmt|;
name|Explanation
name|explanation
init|=
name|context
operator|.
name|searcher
argument_list|()
operator|.
name|explain
argument_list|(
name|context
operator|.
name|query
argument_list|()
argument_list|,
name|topLevelDocId
argument_list|)
decl_stmt|;
for|for
control|(
name|RescoreSearchContext
name|ctx
range|:
name|context
operator|.
name|rescore
argument_list|()
control|)
block|{
name|Rescorer
name|rescorer
init|=
name|ctx
operator|.
name|rescorer
argument_list|()
decl_stmt|;
name|explanation
operator|=
name|rescorer
operator|.
name|explain
argument_list|(
name|topLevelDocId
argument_list|,
name|context
argument_list|,
name|ctx
argument_list|,
name|explanation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|fields
argument_list|()
operator|!=
literal|null
operator|||
operator|(
name|request
operator|.
name|fetchSourceContext
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|fetchSourceContext
argument_list|()
operator|.
name|fetchSource
argument_list|()
operator|)
condition|)
block|{
comment|// Advantage is that we're not opening a second searcher to retrieve the _source. Also
comment|// because we are working in the same searcher in engineGetResult we can be sure that a
comment|// doc isn't deleted between the initial get and this call.
name|GetResult
name|getResult
init|=
name|context
operator|.
name|indexShard
argument_list|()
operator|.
name|getService
argument_list|()
operator|.
name|get
argument_list|(
name|result
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|fields
argument_list|()
argument_list|,
name|request
operator|.
name|fetchSourceContext
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExplainResponse
argument_list|(
name|shardId
operator|.
name|getIndexName
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
literal|true
argument_list|,
name|explanation
argument_list|,
name|getResult
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ExplainResponse
argument_list|(
name|shardId
operator|.
name|getIndexName
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
literal|true
argument_list|,
name|explanation
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Could not explain"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|result
argument_list|,
name|context
argument_list|,
parameter_list|()
lambda|->
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ExplainResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|ExplainResponse
argument_list|()
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
name|clusterService
operator|.
name|state
argument_list|()
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
block|}
end_class

end_unit

