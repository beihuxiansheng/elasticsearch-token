begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.percolate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ResourceNotFoundException
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
name|get
operator|.
name|GetResponse
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
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchResponse
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
name|client
operator|.
name|Client
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
name|common
operator|.
name|ParseFieldMatcher
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
name|text
operator|.
name|Text
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
name|XContentBuilder
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
name|XContentParser
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
name|query
operator|.
name|BoolQueryBuilder
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
name|query
operator|.
name|ConstantScoreQueryBuilder
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
name|query
operator|.
name|PercolatorQueryBuilder
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
name|query
operator|.
name|QueryBuilder
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
name|query
operator|.
name|QueryBuilders
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
name|query
operator|.
name|QueryParseContext
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
name|query
operator|.
name|IndicesQueriesRegistry
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
name|SearchHit
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
name|SearchHits
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
name|aggregations
operator|.
name|AggregatorParsers
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
name|aggregations
operator|.
name|InternalAggregations
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
name|builder
operator|.
name|SearchSourceBuilder
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

begin_class
DECL|class|TransportPercolateAction
specifier|public
class|class
name|TransportPercolateAction
extends|extends
name|HandledTransportAction
argument_list|<
name|PercolateRequest
argument_list|,
name|PercolateResponse
argument_list|>
block|{
DECL|field|client
specifier|private
specifier|final
name|Client
name|client
decl_stmt|;
DECL|field|parseFieldMatcher
specifier|private
specifier|final
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|field|queryRegistry
specifier|private
specifier|final
name|IndicesQueriesRegistry
name|queryRegistry
decl_stmt|;
DECL|field|aggParsers
specifier|private
specifier|final
name|AggregatorParsers
name|aggParsers
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportPercolateAction
specifier|public
name|TransportPercolateAction
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
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|Client
name|client
parameter_list|,
name|IndicesQueriesRegistry
name|indicesQueriesRegistry
parameter_list|,
name|AggregatorParsers
name|aggParsers
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|PercolateAction
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
name|PercolateRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
name|this
operator|.
name|aggParsers
operator|=
name|aggParsers
expr_stmt|;
name|this
operator|.
name|parseFieldMatcher
operator|=
operator|new
name|ParseFieldMatcher
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryRegistry
operator|=
name|indicesQueriesRegistry
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|PercolateRequest
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|getRequest
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|get
argument_list|(
name|request
operator|.
name|getRequest
argument_list|()
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|GetResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|GetResponse
name|getResponse
parameter_list|)
block|{
if|if
condition|(
name|getResponse
operator|.
name|isExists
argument_list|()
condition|)
block|{
name|innerDoExecute
argument_list|(
name|request
argument_list|,
name|getResponse
operator|.
name|getSourceAsBytesRef
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|onFailure
argument_list|(
operator|new
name|ResourceNotFoundException
argument_list|(
literal|"percolate document [{}/{}/{}] doesn't exist"
argument_list|,
name|request
operator|.
name|getRequest
argument_list|()
operator|.
name|index
argument_list|()
argument_list|,
name|request
operator|.
name|getRequest
argument_list|()
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|getRequest
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
else|else
block|{
name|innerDoExecute
argument_list|(
name|request
argument_list|,
literal|null
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerDoExecute
specifier|private
name|void
name|innerDoExecute
parameter_list|(
name|PercolateRequest
name|request
parameter_list|,
name|BytesReference
name|docSource
parameter_list|,
name|ActionListener
argument_list|<
name|PercolateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|SearchRequest
name|searchRequest
decl_stmt|;
try|try
block|{
name|searchRequest
operator|=
name|createSearchRequest
argument_list|(
name|request
argument_list|,
name|docSource
argument_list|,
name|queryRegistry
argument_list|,
name|aggParsers
argument_list|,
name|parseFieldMatcher
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|client
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|SearchResponse
name|searchResponse
parameter_list|)
block|{
try|try
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|createPercolateResponse
argument_list|(
name|searchResponse
argument_list|,
name|request
operator|.
name|onlyCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
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
DECL|method|createSearchRequest
specifier|public
specifier|static
name|SearchRequest
name|createSearchRequest
parameter_list|(
name|PercolateRequest
name|percolateRequest
parameter_list|,
name|BytesReference
name|documentSource
parameter_list|,
name|IndicesQueriesRegistry
name|queryRegistry
parameter_list|,
name|AggregatorParsers
name|aggParsers
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|)
throws|throws
name|IOException
block|{
name|SearchRequest
name|searchRequest
init|=
operator|new
name|SearchRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|percolateRequest
operator|.
name|indices
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|searchRequest
operator|.
name|indices
argument_list|(
name|percolateRequest
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|searchRequest
operator|.
name|indicesOptions
argument_list|(
name|percolateRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|routing
argument_list|(
name|percolateRequest
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|preference
argument_list|(
name|percolateRequest
operator|.
name|preference
argument_list|()
argument_list|)
expr_stmt|;
name|BytesReference
name|querySource
init|=
literal|null
decl_stmt|;
name|XContentBuilder
name|searchSource
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
if|if
condition|(
name|percolateRequest
operator|.
name|source
argument_list|()
operator|!=
literal|null
operator|&&
name|percolateRequest
operator|.
name|source
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|percolateRequest
operator|.
name|source
argument_list|()
argument_list|)
init|)
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"doc"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|builder
operator|.
name|flush
argument_list|()
expr_stmt|;
name|documentSource
operator|=
name|builder
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|builder
operator|.
name|flush
argument_list|()
expr_stmt|;
name|querySource
operator|=
name|builder
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sort"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"sort"
argument_list|)
expr_stmt|;
name|searchSource
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"aggregations"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"aggregations"
argument_list|)
expr_stmt|;
name|searchSource
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"highlight"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"highlight"
argument_list|)
expr_stmt|;
name|searchSource
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown field ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"sort"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"sort"
argument_list|)
expr_stmt|;
name|searchSource
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown field ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"size"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"size"
argument_list|,
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"sort"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"sort"
argument_list|,
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"track_scores"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"trackScores"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"track_scores"
argument_list|,
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown field ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|percolateRequest
operator|.
name|onlyCount
argument_list|()
condition|)
block|{
name|searchSource
operator|.
name|field
argument_list|(
literal|"size"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|PercolatorQueryBuilder
name|percolatorQueryBuilder
init|=
operator|new
name|PercolatorQueryBuilder
argument_list|(
name|percolateRequest
operator|.
name|documentType
argument_list|()
argument_list|,
name|documentSource
argument_list|)
decl_stmt|;
if|if
condition|(
name|querySource
operator|!=
literal|null
condition|)
block|{
name|QueryParseContext
name|queryParseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queryRegistry
argument_list|,
name|XContentHelper
operator|.
name|createParser
argument_list|(
name|querySource
argument_list|)
argument_list|,
name|parseFieldMatcher
argument_list|)
decl_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
init|=
name|queryParseContext
operator|.
name|parseInnerQueryBuilder
argument_list|()
decl_stmt|;
name|BoolQueryBuilder
name|boolQueryBuilder
init|=
name|QueryBuilders
operator|.
name|boolQuery
argument_list|()
decl_stmt|;
name|boolQueryBuilder
operator|.
name|must
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
name|boolQueryBuilder
operator|.
name|filter
argument_list|(
name|percolatorQueryBuilder
argument_list|)
expr_stmt|;
name|searchSource
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
name|boolQueryBuilder
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// wrapping in a constant score query with boost 0 for bwc reason.
comment|// percolator api didn't emit scores before and never included scores
comment|// for how well percolator queries matched with the document being percolated
name|searchSource
operator|.
name|field
argument_list|(
literal|"query"
argument_list|,
operator|new
name|ConstantScoreQueryBuilder
argument_list|(
name|percolatorQueryBuilder
argument_list|)
operator|.
name|boost
argument_list|(
literal|0f
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|searchSource
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|searchSource
operator|.
name|flush
argument_list|()
expr_stmt|;
name|BytesReference
name|source
init|=
name|searchSource
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
try|try
init|(
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
init|)
block|{
name|QueryParseContext
name|context
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queryRegistry
argument_list|,
name|parser
argument_list|,
name|parseFieldMatcher
argument_list|)
decl_stmt|;
name|searchSourceBuilder
operator|.
name|parseXContent
argument_list|(
name|context
argument_list|,
name|aggParsers
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|source
argument_list|(
name|searchSourceBuilder
argument_list|)
expr_stmt|;
return|return
name|searchRequest
return|;
block|}
block|}
DECL|method|createPercolateResponse
specifier|public
specifier|static
name|PercolateResponse
name|createPercolateResponse
parameter_list|(
name|SearchResponse
name|searchResponse
parameter_list|,
name|boolean
name|onlyCount
parameter_list|)
block|{
name|SearchHits
name|hits
init|=
name|searchResponse
operator|.
name|getHits
argument_list|()
decl_stmt|;
name|PercolateResponse
operator|.
name|Match
index|[]
name|matches
decl_stmt|;
if|if
condition|(
name|onlyCount
condition|)
block|{
name|matches
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|matches
operator|=
operator|new
name|PercolateResponse
operator|.
name|Match
index|[
name|hits
operator|.
name|getHits
argument_list|()
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|getHits
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SearchHit
name|hit
init|=
name|hits
operator|.
name|getHits
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|matches
index|[
name|i
index|]
operator|=
operator|new
name|PercolateResponse
operator|.
name|Match
argument_list|(
operator|new
name|Text
argument_list|(
name|hit
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|hit
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|hit
operator|.
name|getScore
argument_list|()
argument_list|,
name|hit
operator|.
name|getHighlightFields
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PercolateResponse
argument_list|(
name|searchResponse
operator|.
name|getTotalShards
argument_list|()
argument_list|,
name|searchResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|searchResponse
operator|.
name|getFailedShards
argument_list|()
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|searchResponse
operator|.
name|getShardFailures
argument_list|()
argument_list|)
argument_list|,
name|matches
argument_list|,
name|hits
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|searchResponse
operator|.
name|getTookInMillis
argument_list|()
argument_list|,
operator|(
name|InternalAggregations
operator|)
name|searchResponse
operator|.
name|getAggregations
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

