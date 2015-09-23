begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|search
operator|.
name|SearchType
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
name|IndicesOptions
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
name|Strings
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
name|XContentParser
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
name|rest
operator|.
name|BaseRestHandler
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
name|RestChannel
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
name|RestController
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
name|RestRequest
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
name|action
operator|.
name|exists
operator|.
name|RestExistsAction
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
name|action
operator|.
name|support
operator|.
name|RestActions
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
name|action
operator|.
name|support
operator|.
name|RestStatusToXContentListener
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
name|Scroll
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
name|search
operator|.
name|fetch
operator|.
name|source
operator|.
name|FetchSourceContext
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
name|sort
operator|.
name|SortOrder
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
name|suggest
operator|.
name|SuggestBuilder
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|parseTimeValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|POST
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|suggest
operator|.
name|SuggestBuilders
operator|.
name|termSuggestion
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestSearchAction
specifier|public
class|class
name|RestSearchAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|queryRegistry
specifier|private
specifier|final
name|IndicesQueriesRegistry
name|queryRegistry
decl_stmt|;
annotation|@
name|Inject
DECL|method|RestSearchAction
specifier|public
name|RestSearchAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
parameter_list|,
name|IndicesQueriesRegistry
name|queryRegistry
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryRegistry
operator|=
name|queryRegistry
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_search"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/_search"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_search"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_search"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/{type}/_search"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_search"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_search/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/_search/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_search/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_search/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/{type}/_search/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_search/template"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|RestExistsAction
name|restExistsAction
init|=
operator|new
name|RestExistsAction
argument_list|(
name|settings
argument_list|,
name|controller
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_search/exists"
argument_list|,
name|restExistsAction
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/_search/exists"
argument_list|,
name|restExistsAction
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/_search/exists"
argument_list|,
name|restExistsAction
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/_search/exists"
argument_list|,
name|restExistsAction
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/{index}/{type}/_search/exists"
argument_list|,
name|restExistsAction
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|POST
argument_list|,
literal|"/{index}/{type}/_search/exists"
argument_list|,
name|restExistsAction
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
throws|throws
name|IOException
block|{
name|SearchRequest
name|searchRequest
decl_stmt|;
name|searchRequest
operator|=
name|RestSearchAction
operator|.
name|parseSearchRequest
argument_list|(
name|request
argument_list|,
name|parseFieldMatcher
argument_list|,
name|queryRegistry
argument_list|)
expr_stmt|;
name|client
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|,
operator|new
name|RestStatusToXContentListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseSearchRequest
specifier|public
specifier|static
name|SearchRequest
name|parseSearchRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
parameter_list|,
name|IndicesQueriesRegistry
name|queryRegistry
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
name|SearchRequest
name|searchRequest
init|=
operator|new
name|SearchRequest
argument_list|(
name|indices
argument_list|)
decl_stmt|;
comment|// get the content, and put it in the body
comment|// add content/source as template if template flag is set
name|boolean
name|isTemplateRequest
init|=
name|request
operator|.
name|path
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"/template"
argument_list|)
decl_stmt|;
if|if
condition|(
name|RestActions
operator|.
name|hasBodyContent
argument_list|(
name|request
argument_list|)
condition|)
block|{
if|if
condition|(
name|isTemplateRequest
condition|)
block|{
name|searchRequest
operator|.
name|templateSource
argument_list|(
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesReference
name|sourceBytes
init|=
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|sourceBytes
argument_list|)
operator|.
name|createParser
argument_list|(
name|sourceBytes
argument_list|)
decl_stmt|;
name|QueryParseContext
name|queryParseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queryRegistry
argument_list|)
decl_stmt|;
name|queryParseContext
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|source
argument_list|(
name|SearchSourceBuilder
operator|.
name|PROTOTYPE
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|,
name|queryParseContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// do not allow 'query_and_fetch' or 'dfs_query_and_fetch' search types
comment|// from the REST layer. these modes are an internal optimization and should
comment|// not be specified explicitly by the user.
name|String
name|searchType
init|=
name|request
operator|.
name|param
argument_list|(
literal|"search_type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|SearchType
operator|.
name|fromString
argument_list|(
name|searchType
argument_list|,
name|parseFieldMatcher
argument_list|)
operator|.
name|equals
argument_list|(
name|SearchType
operator|.
name|QUERY_AND_FETCH
argument_list|)
operator|||
name|SearchType
operator|.
name|fromString
argument_list|(
name|searchType
argument_list|,
name|parseFieldMatcher
argument_list|)
operator|.
name|equals
argument_list|(
name|SearchType
operator|.
name|DFS_QUERY_AND_FETCH
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unsupported search type ["
operator|+
name|searchType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
name|searchRequest
operator|.
name|searchType
argument_list|(
name|searchType
argument_list|)
expr_stmt|;
block|}
name|searchRequest
operator|.
name|extraSource
argument_list|(
name|parseSearchSource
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|requestCache
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"request_cache"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|scroll
init|=
name|request
operator|.
name|param
argument_list|(
literal|"scroll"
argument_list|)
decl_stmt|;
if|if
condition|(
name|scroll
operator|!=
literal|null
condition|)
block|{
name|searchRequest
operator|.
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|parseTimeValue
argument_list|(
name|scroll
argument_list|,
literal|null
argument_list|,
literal|"scroll"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|searchRequest
operator|.
name|types
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"type"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|routing
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"routing"
argument_list|)
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|preference
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"preference"
argument_list|)
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|indicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromRequest
argument_list|(
name|request
argument_list|,
name|searchRequest
operator|.
name|indicesOptions
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|searchRequest
return|;
block|}
DECL|method|parseSearchSource
specifier|public
specifier|static
name|SearchSourceBuilder
name|parseSearchSource
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|SearchSourceBuilder
name|searchSourceBuilder
init|=
literal|null
decl_stmt|;
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|queryBuilder
init|=
name|RestActions
operator|.
name|parseQuerySource
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryBuilder
operator|!=
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
name|searchSourceBuilder
operator|.
name|query
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
block|}
name|int
name|from
init|=
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"from"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|from
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|from
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
name|int
name|size
init|=
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"size"
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|size
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"explain"
argument_list|)
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|explain
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"explain"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"version"
argument_list|)
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|version
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"version"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"timeout"
argument_list|)
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|timeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"timeout"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"terminate_after"
argument_list|)
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|int
name|terminateAfter
init|=
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"terminate_after"
argument_list|,
name|SearchContext
operator|.
name|DEFAULT_TERMINATE_AFTER
argument_list|)
decl_stmt|;
if|if
condition|(
name|terminateAfter
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"terminateAfter must be> 0"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|terminateAfter
operator|>
literal|0
condition|)
block|{
name|searchSourceBuilder
operator|.
name|terminateAfter
argument_list|(
name|terminateAfter
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|sField
init|=
name|request
operator|.
name|param
argument_list|(
literal|"fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sField
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|sField
argument_list|)
condition|)
block|{
name|searchSourceBuilder
operator|.
name|noFields
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|sFields
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sField
argument_list|)
decl_stmt|;
if|if
condition|(
name|sFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|sFields
control|)
block|{
name|searchSourceBuilder
operator|.
name|field
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|String
name|sFieldDataFields
init|=
name|request
operator|.
name|param
argument_list|(
literal|"fielddata_fields"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sFieldDataFields
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|sFieldDataFields
argument_list|)
condition|)
block|{
name|String
index|[]
name|sFields
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sFieldDataFields
argument_list|)
decl_stmt|;
if|if
condition|(
name|sFields
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|field
range|:
name|sFields
control|)
block|{
name|searchSourceBuilder
operator|.
name|fieldDataField
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|FetchSourceContext
name|fetchSourceContext
init|=
name|FetchSourceContext
operator|.
name|parseFromRestRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetchSourceContext
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|fetchSource
argument_list|(
name|fetchSourceContext
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|hasParam
argument_list|(
literal|"track_scores"
argument_list|)
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|trackScores
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"track_scores"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|sSorts
init|=
name|request
operator|.
name|param
argument_list|(
literal|"sort"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sSorts
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|String
index|[]
name|sorts
init|=
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sSorts
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|sort
range|:
name|sorts
control|)
block|{
name|int
name|delimiter
init|=
name|sort
operator|.
name|lastIndexOf
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
if|if
condition|(
name|delimiter
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|sortField
init|=
name|sort
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|delimiter
argument_list|)
decl_stmt|;
name|String
name|reverse
init|=
name|sort
operator|.
name|substring
argument_list|(
name|delimiter
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"asc"
operator|.
name|equals
argument_list|(
name|reverse
argument_list|)
condition|)
block|{
name|searchSourceBuilder
operator|.
name|sort
argument_list|(
name|sortField
argument_list|,
name|SortOrder
operator|.
name|ASC
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"desc"
operator|.
name|equals
argument_list|(
name|reverse
argument_list|)
condition|)
block|{
name|searchSourceBuilder
operator|.
name|sort
argument_list|(
name|sortField
argument_list|,
name|SortOrder
operator|.
name|DESC
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|searchSourceBuilder
operator|.
name|sort
argument_list|(
name|sort
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|sStats
init|=
name|request
operator|.
name|param
argument_list|(
literal|"stats"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sStats
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|stats
argument_list|(
name|Strings
operator|.
name|splitStringByCommaToArray
argument_list|(
name|sStats
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|suggestField
init|=
name|request
operator|.
name|param
argument_list|(
literal|"suggest_field"
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggestField
operator|!=
literal|null
condition|)
block|{
name|String
name|suggestText
init|=
name|request
operator|.
name|param
argument_list|(
literal|"suggest_text"
argument_list|,
name|request
operator|.
name|param
argument_list|(
literal|"q"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|suggestSize
init|=
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"suggest_size"
argument_list|,
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchSourceBuilder
operator|==
literal|null
condition|)
block|{
name|searchSourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
name|String
name|suggestMode
init|=
name|request
operator|.
name|param
argument_list|(
literal|"suggest_mode"
argument_list|)
decl_stmt|;
name|searchSourceBuilder
operator|.
name|suggest
argument_list|(
operator|new
name|SuggestBuilder
argument_list|()
operator|.
name|addSuggestion
argument_list|(
name|termSuggestion
argument_list|(
name|suggestField
argument_list|)
operator|.
name|field
argument_list|(
name|suggestField
argument_list|)
operator|.
name|text
argument_list|(
name|suggestText
argument_list|)
operator|.
name|size
argument_list|(
name|suggestSize
argument_list|)
operator|.
name|suggestMode
argument_list|(
name|suggestMode
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|searchSourceBuilder
return|;
block|}
block|}
end_class

end_unit

