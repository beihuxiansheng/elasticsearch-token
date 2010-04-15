begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|terms
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|broadcast
operator|.
name|BroadcastOperationThreading
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
name|terms
operator|.
name|FieldTermsFreq
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
name|terms
operator|.
name|TermFreq
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
name|terms
operator|.
name|TermsRequest
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
name|terms
operator|.
name|TermsResponse
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
name|RestJsonBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|Settings
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
name|RestActions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RestTermsAction
specifier|public
class|class
name|RestTermsAction
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
block|}
DECL|method|RestTermsAction
annotation|@
name|Inject
specifier|public
name|RestTermsAction
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
name|RestRequest
operator|.
name|Method
operator|.
name|POST
argument_list|,
literal|"/_terms"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_terms"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|POST
argument_list|,
literal|"/{index}/_terms"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/{index}/_terms"
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
name|TermsRequest
name|termsRequest
init|=
operator|new
name|TermsRequest
argument_list|(
name|splitIndices
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"index"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// we just send back a response, no need to fork a listener
name|termsRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|BroadcastOperationThreading
name|operationThreading
init|=
name|BroadcastOperationThreading
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
name|BroadcastOperationThreading
operator|.
name|SINGLE_THREAD
argument_list|)
decl_stmt|;
if|if
condition|(
name|operationThreading
operator|==
name|BroadcastOperationThreading
operator|.
name|NO_THREADS
condition|)
block|{
comment|// since we don't spawn, don't allow no_threads, but change it to a single thread
name|operationThreading
operator|=
name|BroadcastOperationThreading
operator|.
name|SINGLE_THREAD
expr_stmt|;
block|}
name|termsRequest
operator|.
name|operationThreading
argument_list|(
name|operationThreading
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|request
operator|.
name|params
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
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
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|String
name|field
range|:
name|sFields
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|termsRequest
operator|.
name|fields
argument_list|(
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|from
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"from"
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|to
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"to"
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|fromInclusive
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"from_inclusive"
argument_list|,
name|termsRequest
operator|.
name|fromInclusive
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|toInclusive
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"to_inclusive"
argument_list|,
name|termsRequest
operator|.
name|toInclusive
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|temp
init|=
name|request
operator|.
name|param
argument_list|(
literal|"gt"
argument_list|)
decl_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|termsRequest
operator|.
name|gt
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|temp
operator|=
name|request
operator|.
name|param
argument_list|(
literal|"gte"
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|termsRequest
operator|.
name|gte
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
name|temp
operator|=
name|request
operator|.
name|param
argument_list|(
literal|"lt"
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|termsRequest
operator|.
name|lt
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|temp
operator|=
name|request
operator|.
name|param
argument_list|(
literal|"lte"
argument_list|)
expr_stmt|;
if|if
condition|(
name|temp
operator|!=
literal|null
condition|)
block|{
name|termsRequest
operator|.
name|lte
argument_list|(
name|temp
argument_list|)
expr_stmt|;
block|}
block|}
name|termsRequest
operator|.
name|exact
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"exact"
argument_list|,
name|termsRequest
operator|.
name|exact
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|minFreq
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"min_freq"
argument_list|,
name|termsRequest
operator|.
name|minFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|maxFreq
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"max_freq"
argument_list|,
name|termsRequest
operator|.
name|maxFreq
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|size
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"size"
argument_list|,
name|termsRequest
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|prefix
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"prefix"
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|regexp
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"regexp"
argument_list|)
argument_list|)
expr_stmt|;
name|termsRequest
operator|.
name|sortType
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"sort"
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
try|try
block|{
name|JsonBuilder
name|builder
init|=
name|RestJsonBuilder
operator|.
name|restJsonBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|JsonRestResponse
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
specifier|final
name|boolean
name|termsAsArray
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"terms_as_array"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|client
operator|.
name|terms
argument_list|(
name|termsRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|TermsResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|TermsResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|JsonBuilder
name|builder
init|=
name|RestJsonBuilder
operator|.
name|restJsonBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|buildBroadcastShardsHeader
argument_list|(
name|builder
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"docs"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"num_docs"
argument_list|,
name|response
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"max_doc"
argument_list|,
name|response
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"deleted_docs"
argument_list|,
name|response
operator|.
name|deletedDocs
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
expr_stmt|;
for|for
control|(
name|FieldTermsFreq
name|fieldTermsFreq
range|:
name|response
operator|.
name|fields
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|fieldTermsFreq
operator|.
name|fieldName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|termsAsArray
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"terms"
argument_list|)
expr_stmt|;
for|for
control|(
name|TermFreq
name|termFreq
range|:
name|fieldTermsFreq
operator|.
name|termsFreqs
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|termFreq
operator|.
name|termAsString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"doc_freq"
argument_list|,
name|termFreq
operator|.
name|docFreq
argument_list|()
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
block|}
else|else
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"terms"
argument_list|)
expr_stmt|;
for|for
control|(
name|TermFreq
name|termFreq
range|:
name|fieldTermsFreq
operator|.
name|termsFreqs
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"term"
argument_list|,
name|termFreq
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"doc_freq"
argument_list|,
name|termFreq
operator|.
name|docFreq
argument_list|()
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
name|JsonRestResponse
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
name|JsonThrowableRestResponse
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
block|}
end_class

end_unit

