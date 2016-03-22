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
name|get
operator|.
name|GetRequest
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
comment|/**  * A multi percolate request that encapsulates multiple {@link PercolateRequest} instances in a single api call.  */
end_comment

begin_class
DECL|class|MultiPercolateRequest
specifier|public
class|class
name|MultiPercolateRequest
extends|extends
name|ActionRequest
argument_list|<
name|MultiPercolateRequest
argument_list|>
implements|implements
name|CompositeIndicesRequest
block|{
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
decl_stmt|;
DECL|field|documentType
specifier|private
name|String
name|documentType
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
DECL|field|requests
specifier|private
name|List
argument_list|<
name|PercolateRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * Embeds a percolate request to this multi percolate request      */
DECL|method|add
specifier|public
name|MultiPercolateRequest
name|add
parameter_list|(
name|PercolateRequestBuilder
name|requestBuilder
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|requestBuilder
operator|.
name|request
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Embeds a percolate request to this multi percolate request      */
DECL|method|add
specifier|public
name|MultiPercolateRequest
name|add
parameter_list|(
name|PercolateRequest
name|request
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|==
literal|null
operator|&&
name|indices
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|documentType
argument_list|()
operator|==
literal|null
operator|&&
name|documentType
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|documentType
argument_list|(
name|documentType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|indicesOptions
argument_list|()
operator|==
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
operator|&&
name|indicesOptions
operator|!=
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
condition|)
block|{
name|request
operator|.
name|indicesOptions
argument_list|(
name|indicesOptions
argument_list|)
expr_stmt|;
block|}
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
comment|/**      * Embeds a percolate request which request body is defined as raw bytes to this multi percolate request      */
DECL|method|add
specifier|public
name|MultiPercolateRequest
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
literal|true
argument_list|)
return|;
block|}
comment|/**      * Embeds a percolate request which request body is defined as raw bytes to this multi percolate request      */
DECL|method|add
specifier|public
name|MultiPercolateRequest
name|add
parameter_list|(
name|BytesReference
name|data
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
name|PercolateRequest
name|percolateRequest
init|=
operator|new
name|PercolateRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|indices
operator|!=
literal|null
condition|)
block|{
name|percolateRequest
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|documentType
operator|!=
literal|null
condition|)
block|{
name|percolateRequest
operator|.
name|documentType
argument_list|(
name|documentType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indicesOptions
operator|!=
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
condition|)
block|{
name|percolateRequest
operator|.
name|indicesOptions
argument_list|(
name|indicesOptions
argument_list|)
expr_stmt|;
block|}
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
comment|// Move to START_OBJECT, if token is null, its an empty data
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
literal|null
condition|)
block|{
comment|// Top level json object
assert|assert
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
assert|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Expected field"
argument_list|)
throw|;
block|}
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
name|ElasticsearchParseException
argument_list|(
literal|"expected start object"
argument_list|)
throw|;
block|}
name|String
name|percolateAction
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"percolate"
operator|.
name|equals
argument_list|(
name|percolateAction
argument_list|)
condition|)
block|{
name|parsePercolateAction
argument_list|(
name|parser
argument_list|,
name|percolateRequest
argument_list|,
name|allowExplicitIndex
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"count"
operator|.
name|equals
argument_list|(
name|percolateAction
argument_list|)
condition|)
block|{
name|percolateRequest
operator|.
name|onlyCount
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|parsePercolateAction
argument_list|(
name|parser
argument_list|,
name|percolateRequest
argument_list|,
name|allowExplicitIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"[{}] isn't a supported percolate operation"
argument_list|,
name|percolateAction
argument_list|)
throw|;
block|}
block|}
block|}
block|}
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
name|percolateRequest
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
name|percolateRequest
argument_list|)
expr_stmt|;
block|}
return|return
name|this
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
name|requests
return|;
block|}
DECL|method|parsePercolateAction
specifier|private
name|void
name|parsePercolateAction
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|PercolateRequest
name|percolateRequest
parameter_list|,
name|boolean
name|allowExplicitIndex
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|globalIndex
init|=
name|indices
operator|!=
literal|null
operator|&&
name|indices
operator|.
name|length
operator|>
literal|0
condition|?
name|indices
index|[
literal|0
index|]
else|:
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|header
init|=
name|parser
operator|.
name|map
argument_list|()
decl_stmt|;
if|if
condition|(
name|header
operator|.
name|containsKey
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|GetRequest
name|getRequest
init|=
operator|new
name|GetRequest
argument_list|(
name|globalIndex
argument_list|)
decl_stmt|;
name|percolateRequest
operator|.
name|getRequest
argument_list|(
name|getRequest
argument_list|)
expr_stmt|;
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
name|header
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
literal|"id"
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
name|getRequest
operator|.
name|id
argument_list|(
name|nodeStringValue
argument_list|(
name|value
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|header
operator|.
name|put
argument_list|(
literal|"id"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
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
name|IllegalArgumentException
argument_list|(
literal|"explicit index in multi percolate is not allowed"
argument_list|)
throw|;
block|}
name|getRequest
operator|.
name|index
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
literal|"type"
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
name|getRequest
operator|.
name|type
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
name|getRequest
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
name|getRequest
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
elseif|else
if|if
condition|(
literal|"percolate_index"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"percolate_indices"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"percolateIndex"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"percolateIndices"
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
name|percolateRequest
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
literal|"percolate_type"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"percolateType"
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
name|percolateRequest
operator|.
name|documentType
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
literal|"percolate_preference"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"percolatePreference"
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
name|percolateRequest
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
literal|"percolate_routing"
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
literal|"percolateRouting"
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
name|percolateRequest
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
comment|// Setting values based on get request, if needed...
if|if
condition|(
operator|(
name|percolateRequest
operator|.
name|indices
argument_list|()
operator|==
literal|null
operator|||
name|percolateRequest
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|==
literal|0
operator|)
operator|&&
name|getRequest
operator|.
name|index
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|percolateRequest
operator|.
name|indices
argument_list|(
name|getRequest
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|percolateRequest
operator|.
name|documentType
argument_list|()
operator|==
literal|null
operator|&&
name|getRequest
operator|.
name|type
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|percolateRequest
operator|.
name|documentType
argument_list|(
name|getRequest
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|percolateRequest
operator|.
name|routing
argument_list|()
operator|==
literal|null
operator|&&
name|getRequest
operator|.
name|routing
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|percolateRequest
operator|.
name|routing
argument_list|(
name|getRequest
operator|.
name|routing
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|percolateRequest
operator|.
name|preference
argument_list|()
operator|==
literal|null
operator|&&
name|getRequest
operator|.
name|preference
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|percolateRequest
operator|.
name|preference
argument_list|(
name|getRequest
operator|.
name|preference
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
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
name|header
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
name|IllegalArgumentException
argument_list|(
literal|"explicit index in multi percolate is not allowed"
argument_list|)
throw|;
block|}
name|percolateRequest
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
condition|)
block|{
name|percolateRequest
operator|.
name|documentType
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
name|percolateRequest
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
name|percolateRequest
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
block|}
name|percolateRequest
operator|.
name|indicesOptions
argument_list|(
name|IndicesOptions
operator|.
name|fromMap
argument_list|(
name|header
argument_list|,
name|indicesOptions
argument_list|)
argument_list|)
expr_stmt|;
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
comment|/**      * @return The list of already set percolate requests.      */
DECL|method|requests
specifier|public
name|List
argument_list|<
name|PercolateRequest
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
comment|/**      * @return Returns the {@link IndicesOptions} that is used as default for all percolate requests.      */
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
comment|/**      * Sets the {@link IndicesOptions} for all percolate request that don't have this set.      *      * Warning: This should be set before adding any percolate requests. Setting this after adding percolate requests      * will have no effect on any percolate requests already added.      */
DECL|method|indicesOptions
specifier|public
name|MultiPercolateRequest
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
comment|/**      * @return The default indices for all percolate request.      */
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
comment|/**      * Sets the default indices for any percolate request that doesn't have indices defined.      *      * Warning: This should be set before adding any percolate requests. Setting this after adding percolate requests      * will have no effect on any percolate requests already added.      */
DECL|method|indices
specifier|public
name|MultiPercolateRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * @return Sets the default type for all percolate requests      */
DECL|method|documentType
specifier|public
name|String
name|documentType
parameter_list|()
block|{
return|return
name|documentType
return|;
block|}
comment|/**      * Sets the default document type for any percolate request that doesn't have a document type set.      *      * Warning: This should be set before adding any percolate requests. Setting this after adding percolate requests      * will have no effect on any percolate requests already added.      */
DECL|method|documentType
specifier|public
name|MultiPercolateRequest
name|documentType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|documentType
operator|=
name|type
expr_stmt|;
return|return
name|this
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
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|documentType
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|indicesOptions
operator|=
name|IndicesOptions
operator|.
name|readIndicesOptions
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
name|PercolateRequest
name|request
init|=
operator|new
name|PercolateRequest
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
name|writeStringArrayNullable
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|documentType
argument_list|)
expr_stmt|;
name|indicesOptions
operator|.
name|writeIndicesOptions
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
name|PercolateRequest
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

