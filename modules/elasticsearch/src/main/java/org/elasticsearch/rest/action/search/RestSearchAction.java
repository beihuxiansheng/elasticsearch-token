begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchIllegalArgumentException
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
name|search
operator|.
name|SearchOperationThreading
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
name|XContentBuilder
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
name|xcontent
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
name|xcontent
operator|.
name|QueryStringQueryBuilder
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
name|*
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
name|sort
operator|.
name|SortOrder
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
name|regex
operator|.
name|Pattern
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
name|*
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
name|*
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
name|RestResponse
operator|.
name|Status
operator|.
name|*
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
name|action
operator|.
name|support
operator|.
name|RestXContentBuilder
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RestSearchAction
specifier|public
class|class
name|RestSearchAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|fieldsPattern
specifier|private
specifier|final
specifier|static
name|Pattern
name|fieldsPattern
decl_stmt|;
DECL|field|indicesBoostPattern
specifier|private
specifier|final
specifier|static
name|Pattern
name|indicesBoostPattern
decl_stmt|;
static|static
block|{
name|fieldsPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|indicesBoostPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
DECL|method|RestSearchAction
annotation|@
name|Inject
specifier|public
name|RestSearchAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
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
block|}
DECL|method|handleRequest
annotation|@
name|Override
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
parameter_list|)
block|{
name|SearchRequest
name|searchRequest
decl_stmt|;
try|try
block|{
name|searchRequest
operator|=
name|parseSearchRequest
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|searchRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|SearchOperationThreading
name|operationThreading
init|=
name|SearchOperationThreading
operator|.
name|fromString
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"operation_threading"
argument_list|)
argument_list|,
name|SearchOperationThreading
operator|.
name|SINGLE_THREAD
argument_list|)
decl_stmt|;
if|if
condition|(
name|operationThreading
operator|==
name|SearchOperationThreading
operator|.
name|NO_THREADS
condition|)
block|{
comment|// since we don't spawn, don't allow no_threads, but change it to a single thread
name|operationThreading
operator|=
name|SearchOperationThreading
operator|.
name|SINGLE_THREAD
expr_stmt|;
block|}
name|searchRequest
operator|.
name|operationThreading
argument_list|(
name|operationThreading
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|restContentBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentRestResponse
argument_list|(
name|request
argument_list|,
name|BAD_REQUEST
argument_list|,
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"error"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to send failure response"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
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
name|response
parameter_list|)
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|restContentBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|response
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentRestResponse
argument_list|(
name|request
argument_list|,
name|OK
argument_list|,
name|builder
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
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to execute search (building response)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentThrowableRestResponse
argument_list|(
name|request
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to send failure response"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|parseSearchRequest
specifier|private
name|SearchRequest
name|parseSearchRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|)
block|{
name|String
index|[]
name|indices
init|=
name|RestActions
operator|.
name|splitIndices
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
if|if
condition|(
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
name|searchRequest
operator|.
name|source
argument_list|(
name|request
operator|.
name|contentByteArray
argument_list|()
argument_list|,
name|request
operator|.
name|contentByteArrayOffset
argument_list|()
argument_list|,
name|request
operator|.
name|contentLength
argument_list|()
argument_list|,
name|request
operator|.
name|contentUnsafe
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|source
init|=
name|request
operator|.
name|param
argument_list|(
literal|"source"
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|searchRequest
operator|.
name|source
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add extra source based on the request parameters
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
name|searchType
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"search_type"
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
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|searchRequest
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
name|searchRequest
operator|.
name|types
argument_list|(
name|RestActions
operator|.
name|splitTypes
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
name|queryHint
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"query_hint"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|searchRequest
return|;
block|}
DECL|method|parseSearchSource
specifier|private
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
operator|new
name|SearchSourceBuilder
argument_list|()
decl_stmt|;
name|String
name|queryString
init|=
name|request
operator|.
name|param
argument_list|(
literal|"q"
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryString
operator|!=
literal|null
condition|)
block|{
name|QueryStringQueryBuilder
name|queryBuilder
init|=
name|QueryBuilders
operator|.
name|queryString
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|queryBuilder
operator|.
name|defaultField
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"df"
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|analyzer
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"analyzer"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|defaultOperator
init|=
name|request
operator|.
name|param
argument_list|(
literal|"default_operator"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultOperator
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"OR"
operator|.
name|equals
argument_list|(
name|defaultOperator
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|defaultOperator
argument_list|(
name|QueryStringQueryBuilder
operator|.
name|Operator
operator|.
name|OR
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"AND"
operator|.
name|equals
argument_list|(
name|defaultOperator
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|defaultOperator
argument_list|(
name|QueryStringQueryBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Unsupported defaultOperator ["
operator|+
name|defaultOperator
operator|+
literal|"], can either be [OR] or [AND]"
argument_list|)
throw|;
block|}
block|}
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
name|searchSourceBuilder
operator|.
name|size
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
name|searchSourceBuilder
operator|.
name|queryParserName
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"query_parser_name"
argument_list|)
argument_list|)
expr_stmt|;
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
name|String
index|[]
name|sFields
init|=
name|fieldsPattern
operator|.
name|split
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
name|String
index|[]
name|sorts
init|=
name|fieldsPattern
operator|.
name|split
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
name|sIndicesBoost
init|=
name|request
operator|.
name|param
argument_list|(
literal|"indices_boost"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sIndicesBoost
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|indicesBoost
init|=
name|indicesBoostPattern
operator|.
name|split
argument_list|(
name|sIndicesBoost
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|indexBoost
range|:
name|indicesBoost
control|)
block|{
name|int
name|divisor
init|=
name|indexBoost
operator|.
name|indexOf
argument_list|(
literal|','
argument_list|)
decl_stmt|;
if|if
condition|(
name|divisor
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Illegal index boost ["
operator|+
name|indexBoost
operator|+
literal|"], no ','"
argument_list|)
throw|;
block|}
name|String
name|indexName
init|=
name|indexBoost
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|divisor
argument_list|)
decl_stmt|;
name|String
name|sBoost
init|=
name|indexBoost
operator|.
name|substring
argument_list|(
name|divisor
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|searchSourceBuilder
operator|.
name|indexBoost
argument_list|(
name|indexName
argument_list|,
name|Float
operator|.
name|parseFloat
argument_list|(
name|sBoost
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Illegal index boost ["
operator|+
name|indexBoost
operator|+
literal|"], boost not a float number"
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|searchSourceBuilder
return|;
block|}
block|}
end_class

end_unit

