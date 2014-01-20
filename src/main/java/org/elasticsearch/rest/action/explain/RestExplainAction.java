begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.explain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|ElasticsearchIllegalArgumentException
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
name|explain
operator|.
name|ExplainRequest
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
name|explain
operator|.
name|ExplainResponse
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
name|QuerySourceBuilder
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
name|BytesArray
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
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
name|rest
operator|.
name|RestStatus
operator|.
name|NOT_FOUND
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
name|RestStatus
operator|.
name|OK
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
name|restContentBuilder
import|;
end_import

begin_comment
comment|/**  * Rest action for computing a score explanation for specific documents.  */
end_comment

begin_class
DECL|class|RestExplainAction
specifier|public
class|class
name|RestExplainAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestExplainAction
specifier|public
name|RestExplainAction
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
literal|"/{index}/{type}/{id}/_explain"
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
literal|"/{index}/{type}/{id}/_explain"
argument_list|,
name|this
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
parameter_list|)
block|{
specifier|final
name|ExplainRequest
name|explainRequest
init|=
operator|new
name|ExplainRequest
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|,
name|request
operator|.
name|param
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|request
operator|.
name|param
argument_list|(
literal|"id"
argument_list|)
argument_list|)
decl_stmt|;
name|explainRequest
operator|.
name|parent
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"parent"
argument_list|)
argument_list|)
expr_stmt|;
name|explainRequest
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
name|explainRequest
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
name|String
name|sourceString
init|=
name|request
operator|.
name|param
argument_list|(
literal|"source"
argument_list|)
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
name|request
operator|.
name|hasContent
argument_list|()
condition|)
block|{
name|explainRequest
operator|.
name|source
argument_list|(
name|request
operator|.
name|content
argument_list|()
argument_list|,
name|request
operator|.
name|contentUnsafe
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|sourceString
operator|!=
literal|null
condition|)
block|{
name|explainRequest
operator|.
name|source
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"source"
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|queryString
operator|!=
literal|null
condition|)
block|{
name|QueryStringQueryBuilder
name|queryStringBuilder
init|=
name|QueryBuilders
operator|.
name|queryString
argument_list|(
name|queryString
argument_list|)
decl_stmt|;
name|queryStringBuilder
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
name|queryStringBuilder
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
name|queryStringBuilder
operator|.
name|analyzeWildcard
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"analyze_wildcard"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|queryStringBuilder
operator|.
name|lowercaseExpandedTerms
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"lowercase_expanded_terms"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|queryStringBuilder
operator|.
name|lenient
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"lenient"
argument_list|,
literal|null
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
name|queryStringBuilder
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
name|queryStringBuilder
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
name|ElasticsearchIllegalArgumentException
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
name|QuerySourceBuilder
name|querySourceBuilder
init|=
operator|new
name|QuerySourceBuilder
argument_list|()
decl_stmt|;
name|querySourceBuilder
operator|.
name|setQuery
argument_list|(
name|queryStringBuilder
argument_list|)
expr_stmt|;
name|explainRequest
operator|.
name|source
argument_list|(
name|querySourceBuilder
argument_list|)
expr_stmt|;
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
name|explainRequest
operator|.
name|fields
argument_list|(
name|sFields
argument_list|)
expr_stmt|;
block|}
block|}
name|explainRequest
operator|.
name|fetchSourceContext
argument_list|(
name|FetchSourceContext
operator|.
name|parseFromRestRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|explain
argument_list|(
name|explainRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ExplainResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ExplainResponse
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
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_INDEX
argument_list|,
name|explainRequest
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_TYPE
argument_list|,
name|explainRequest
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_ID
argument_list|,
name|explainRequest
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MATCHED
argument_list|,
name|response
operator|.
name|isMatch
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|.
name|hasExplanation
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|EXPLANATION
argument_list|)
expr_stmt|;
name|buildExplanation
argument_list|(
name|builder
argument_list|,
name|response
operator|.
name|getExplanation
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|GetResult
name|getResult
init|=
name|response
operator|.
name|getGetResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|getResult
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|GET
argument_list|)
expr_stmt|;
name|response
operator|.
name|getGetResult
argument_list|()
operator|.
name|toXContentEmbedded
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
block|}
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
name|response
operator|.
name|isExists
argument_list|()
condition|?
name|OK
else|:
name|NOT_FOUND
argument_list|,
name|builder
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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
specifier|private
name|void
name|buildExplanation
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Explanation
name|explanation
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VALUE
argument_list|,
name|explanation
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DESCRIPTION
argument_list|,
name|explanation
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
name|Explanation
index|[]
name|innerExps
init|=
name|explanation
operator|.
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|innerExps
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|DETAILS
argument_list|)
expr_stmt|;
for|for
control|(
name|Explanation
name|exp
range|:
name|innerExps
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|buildExplanation
argument_list|(
name|builder
argument_list|,
name|exp
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
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
DECL|class|Fields
specifier|static
class|class
name|Fields
block|{
DECL|field|_INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|_INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_index"
argument_list|)
decl_stmt|;
DECL|field|_TYPE
specifier|static
specifier|final
name|XContentBuilderString
name|_TYPE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_type"
argument_list|)
decl_stmt|;
DECL|field|_ID
specifier|static
specifier|final
name|XContentBuilderString
name|_ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
DECL|field|MATCHED
specifier|static
specifier|final
name|XContentBuilderString
name|MATCHED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"matched"
argument_list|)
decl_stmt|;
DECL|field|EXPLANATION
specifier|static
specifier|final
name|XContentBuilderString
name|EXPLANATION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"explanation"
argument_list|)
decl_stmt|;
DECL|field|VALUE
specifier|static
specifier|final
name|XContentBuilderString
name|VALUE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
DECL|field|DESCRIPTION
specifier|static
specifier|final
name|XContentBuilderString
name|DESCRIPTION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
DECL|field|DETAILS
specifier|static
specifier|final
name|XContentBuilderString
name|DETAILS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"details"
argument_list|)
decl_stmt|;
DECL|field|GET
specifier|static
specifier|final
name|XContentBuilderString
name|GET
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"get"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

