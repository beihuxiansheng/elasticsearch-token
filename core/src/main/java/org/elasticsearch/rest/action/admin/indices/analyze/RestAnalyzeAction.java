begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.indices.analyze
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|analyze
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeRequest
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
name|admin
operator|.
name|indices
operator|.
name|analyze
operator|.
name|AnalyzeResponse
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
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestToXContentListener
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RestAnalyzeAction
specifier|public
class|class
name|RestAnalyzeAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestAnalyzeAction
specifier|public
name|RestAnalyzeAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|Client
name|client
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
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_analyze"
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
literal|"/{index}/_analyze"
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
literal|"/_analyze"
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
literal|"/{index}/_analyze"
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
parameter_list|,
specifier|final
name|Client
name|client
parameter_list|)
block|{
name|String
index|[]
name|texts
init|=
name|request
operator|.
name|paramAsStringArrayOrEmptyIfAll
argument_list|(
literal|"text"
argument_list|)
decl_stmt|;
name|AnalyzeRequest
name|analyzeRequest
init|=
operator|new
name|AnalyzeRequest
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
name|analyzeRequest
operator|.
name|text
argument_list|(
name|texts
argument_list|)
expr_stmt|;
name|analyzeRequest
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
name|analyzeRequest
operator|.
name|field
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
name|analyzeRequest
operator|.
name|tokenizer
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"tokenizer"
argument_list|)
argument_list|)
expr_stmt|;
name|analyzeRequest
operator|.
name|tokenFilters
argument_list|(
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"token_filters"
argument_list|,
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"filters"
argument_list|,
name|analyzeRequest
operator|.
name|tokenFilters
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|analyzeRequest
operator|.
name|charFilters
argument_list|(
name|request
operator|.
name|paramAsStringArray
argument_list|(
literal|"char_filters"
argument_list|,
name|analyzeRequest
operator|.
name|charFilters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|XContentType
name|type
init|=
name|RestActions
operator|.
name|guessBodyContentType
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|texts
operator|==
literal|null
operator|||
name|texts
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|texts
operator|=
operator|new
name|String
index|[]
block|{
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|request
argument_list|)
operator|.
name|toUtf8
argument_list|()
block|}
expr_stmt|;
name|analyzeRequest
operator|.
name|text
argument_list|(
name|texts
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// NOTE: if rest request with xcontent body has request parameters, the parameters does not override xcontent values
name|buildFromContent
argument_list|(
name|RestActions
operator|.
name|getRestContent
argument_list|(
name|request
argument_list|)
argument_list|,
name|analyzeRequest
argument_list|)
expr_stmt|;
block|}
block|}
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|analyze
argument_list|(
name|analyzeRequest
argument_list|,
operator|new
name|RestToXContentListener
argument_list|<
name|AnalyzeResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildFromContent
specifier|public
specifier|static
name|void
name|buildFromContent
parameter_list|(
name|BytesReference
name|content
parameter_list|,
name|AnalyzeRequest
name|analyzeRequest
parameter_list|)
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
name|content
argument_list|)
init|)
block|{
if|if
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
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
literal|"Malforrmed content, must start with an object"
argument_list|)
throw|;
block|}
else|else
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
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
literal|"text"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|analyzeRequest
operator|.
name|text
argument_list|(
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
literal|"text"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|texts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
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
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|currentFieldName
operator|+
literal|" array element should only contain text"
argument_list|)
throw|;
block|}
name|texts
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|analyzeRequest
operator|.
name|text
argument_list|(
name|texts
operator|.
name|toArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"analyzer"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|analyzeRequest
operator|.
name|analyzer
argument_list|(
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
literal|"field"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|analyzeRequest
operator|.
name|field
argument_list|(
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
literal|"tokenizer"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|VALUE_STRING
condition|)
block|{
name|analyzeRequest
operator|.
name|tokenizer
argument_list|(
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
operator|(
literal|"token_filters"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"filters"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|filters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
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
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|currentFieldName
operator|+
literal|" array element should only contain token filter's name"
argument_list|)
throw|;
block|}
name|filters
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|analyzeRequest
operator|.
name|tokenFilters
argument_list|(
name|filters
operator|.
name|toArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"char_filters"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|&&
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|charFilters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
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
name|END_ARRAY
condition|)
block|{
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|currentFieldName
operator|+
literal|" array element should only contain char filter's name"
argument_list|)
throw|;
block|}
name|charFilters
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|analyzeRequest
operator|.
name|tokenFilters
argument_list|(
name|charFilters
operator|.
name|toArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameter ["
operator|+
name|currentFieldName
operator|+
literal|"] in request body or parameter is of the wrong type["
operator|+
name|token
operator|+
literal|"] "
argument_list|)
throw|;
block|}
block|}
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
name|IllegalArgumentException
argument_list|(
literal|"Failed to parse request body"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

