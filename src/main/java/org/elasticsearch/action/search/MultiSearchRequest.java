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
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|ActionRequest
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
name|ActionRequestValidationException
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
name|CompositeIndicesRequest
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
name|IndicesRequest
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
name|common
operator|.
name|Nullable
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|XContent
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ValidateActions
operator|.
name|addValidationError
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
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeBooleanValue
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
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeStringArrayValue
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
name|xcontent
operator|.
name|support
operator|.
name|XContentMapValues
operator|.
name|nodeStringValue
import|;
end_import

begin_comment
comment|/**  * A multi search API request.  */
end_comment

begin_class
DECL|class|MultiSearchRequest
specifier|public
class|class
name|MultiSearchRequest
extends|extends
name|ActionRequest
argument_list|<
name|MultiSearchRequest
argument_list|>
implements|implements
name|CompositeIndicesRequest
block|{
DECL|field|requests
specifier|private
name|List
argument_list|<
name|SearchRequest
argument_list|>
name|requests
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|field|indicesOptions
specifier|private
name|IndicesOptions
name|indicesOptions
init|=
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
decl_stmt|;
comment|/**      * Add a search request to execute. Note, the order is important, the search response will be returned in the      * same order as the search requests.      */
DECL|method|add
specifier|public
name|MultiSearchRequest
name|add
parameter_list|(
name|SearchRequestBuilder
name|request
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|request
operator|.
name|request
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Add a search request to execute. Note, the order is important, the search response will be returned in the      * same order as the search requests.      */
DECL|method|add
specifier|public
name|MultiSearchRequest
name|add
parameter_list|(
name|SearchRequest
name|request
parameter_list|)
block|{
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|add
specifier|public
name|MultiSearchRequest
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|indices
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|types
parameter_list|,
annotation|@
name|Nullable
name|String
name|searchType
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|add
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|length
argument_list|)
argument_list|,
name|indices
argument_list|,
name|types
argument_list|,
name|searchType
argument_list|,
literal|null
argument_list|,
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|MultiSearchRequest
name|add
parameter_list|(
name|BytesReference
name|data
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|indices
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|types
parameter_list|,
annotation|@
name|Nullable
name|String
name|searchType
parameter_list|,
name|IndicesOptions
name|indicesOptions
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|add
argument_list|(
name|data
argument_list|,
name|indices
argument_list|,
name|types
argument_list|,
name|searchType
argument_list|,
literal|null
argument_list|,
name|indicesOptions
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|add
specifier|public
name|MultiSearchRequest
name|add
parameter_list|(
name|BytesReference
name|data
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|indices
parameter_list|,
annotation|@
name|Nullable
name|String
index|[]
name|types
parameter_list|,
annotation|@
name|Nullable
name|String
name|searchType
parameter_list|,
annotation|@
name|Nullable
name|String
name|routing
parameter_list|,
name|IndicesOptions
name|indicesOptions
parameter_list|,
name|boolean
name|allowExplicitIndex
parameter_list|)
throws|throws
name|Exception
block|{
name|XContent
name|xContent
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|int
name|from
init|=
literal|0
decl_stmt|;
name|int
name|length
init|=
name|data
operator|.
name|length
argument_list|()
decl_stmt|;
name|byte
name|marker
init|=
name|xContent
operator|.
name|streamSeparator
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|nextMarker
init|=
name|findNextMarker
argument_list|(
name|marker
argument_list|,
name|from
argument_list|,
name|data
argument_list|,
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextMarker
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
comment|// support first line with \n
if|if
condition|(
name|nextMarker
operator|==
literal|0
condition|)
block|{
name|from
operator|=
name|nextMarker
operator|+
literal|1
expr_stmt|;
continue|continue;
block|}
name|SearchRequest
name|searchRequest
init|=
operator|new
name|SearchRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|indices
operator|!=
literal|null
condition|)
block|{
name|searchRequest
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indicesOptions
operator|!=
literal|null
condition|)
block|{
name|searchRequest
operator|.
name|indicesOptions
argument_list|(
name|indicesOptions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|types
operator|!=
literal|null
operator|&&
name|types
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|searchRequest
operator|.
name|types
argument_list|(
name|types
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|searchRequest
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
block|}
name|searchRequest
operator|.
name|searchType
argument_list|(
name|searchType
argument_list|)
expr_stmt|;
name|IndicesOptions
name|defaultOptions
init|=
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
decl_stmt|;
comment|// now parse the action
if|if
condition|(
name|nextMarker
operator|-
name|from
operator|>
literal|0
condition|)
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|xContent
operator|.
name|createParser
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|)
init|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
name|parser
operator|.
name|map
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|source
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Object
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"indices"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|allowExplicitIndex
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"explicit index in multi percolate is not allowed"
argument_list|)
throw|;
block|}
name|searchRequest
operator|.
name|indices
argument_list|(
name|nodeStringArrayValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"type"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"types"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|types
argument_list|(
name|nodeStringArrayValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"search_type"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"searchType"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|searchType
argument_list|(
name|nodeStringValue
argument_list|(
name|value
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"query_cache"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"queryCache"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|queryCache
argument_list|(
name|nodeBooleanValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"preference"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|preference
argument_list|(
name|nodeStringValue
argument_list|(
name|value
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"routing"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|searchRequest
operator|.
name|routing
argument_list|(
name|nodeStringValue
argument_list|(
name|value
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|defaultOptions
operator|=
name|IndicesOptions
operator|.
name|fromMap
argument_list|(
name|source
argument_list|,
name|defaultOptions
argument_list|)
expr_stmt|;
block|}
block|}
name|searchRequest
operator|.
name|indicesOptions
argument_list|(
name|defaultOptions
argument_list|)
expr_stmt|;
comment|// move pointers
name|from
operator|=
name|nextMarker
operator|+
literal|1
expr_stmt|;
comment|// now for the body
name|nextMarker
operator|=
name|findNextMarker
argument_list|(
name|marker
argument_list|,
name|from
argument_list|,
name|data
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextMarker
operator|==
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|searchRequest
operator|.
name|source
argument_list|(
name|data
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|nextMarker
operator|-
name|from
argument_list|)
argument_list|)
expr_stmt|;
comment|// move pointers
name|from
operator|=
name|nextMarker
operator|+
literal|1
expr_stmt|;
name|add
argument_list|(
name|searchRequest
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|parseArray
specifier|private
name|String
index|[]
name|parseArray
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
assert|assert
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
assert|;
while|while
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
name|END_ARRAY
condition|)
block|{
name|list
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
return|return
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|findNextMarker
specifier|private
name|int
name|findNextMarker
parameter_list|(
name|byte
name|marker
parameter_list|,
name|int
name|from
parameter_list|,
name|BytesReference
name|data
parameter_list|,
name|int
name|length
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|from
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|data
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
name|marker
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
DECL|method|requests
specifier|public
name|List
argument_list|<
name|SearchRequest
argument_list|>
name|requests
parameter_list|()
block|{
return|return
name|this
operator|.
name|requests
return|;
block|}
annotation|@
name|Override
DECL|method|subRequests
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|IndicesRequest
argument_list|>
name|subRequests
parameter_list|()
block|{
return|return
name|this
operator|.
name|requests
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|requests
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"no requests added"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|requests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ActionRequestValidationException
name|ex
init|=
name|requests
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|ex
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|validationException
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
operator|new
name|ActionRequestValidationException
argument_list|()
expr_stmt|;
block|}
name|validationException
operator|.
name|addValidationErrors
argument_list|(
name|ex
operator|.
name|validationErrors
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|validationException
return|;
block|}
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|indicesOptions
return|;
block|}
DECL|method|indicesOptions
specifier|public
name|MultiSearchRequest
name|indicesOptions
parameter_list|(
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
name|this
operator|.
name|indicesOptions
operator|=
name|indicesOptions
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SearchRequest
name|request
init|=
operator|new
name|SearchRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|requests
operator|.
name|add
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|requests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchRequest
name|request
range|:
name|requests
control|)
block|{
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

