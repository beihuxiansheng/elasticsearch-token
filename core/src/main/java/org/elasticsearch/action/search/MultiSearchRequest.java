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
implements|implements
name|CompositeIndicesRequest
block|{
DECL|field|maxConcurrentSearchRequests
specifier|private
name|int
name|maxConcurrentSearchRequests
init|=
literal|0
decl_stmt|;
DECL|field|requests
specifier|private
name|List
argument_list|<
name|SearchRequest
argument_list|>
name|requests
init|=
operator|new
name|ArrayList
argument_list|<>
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
comment|/**      * Returns the amount of search requests specified in this multi search requests are allowed to be ran concurrently.      */
DECL|method|maxConcurrentSearchRequests
specifier|public
name|int
name|maxConcurrentSearchRequests
parameter_list|()
block|{
return|return
name|maxConcurrentSearchRequests
return|;
block|}
comment|/**      * Sets how many search requests specified in this multi search requests are allowed to be ran concurrently.      */
DECL|method|maxConcurrentSearchRequests
specifier|public
name|MultiSearchRequest
name|maxConcurrentSearchRequests
parameter_list|(
name|int
name|maxConcurrentSearchRequests
parameter_list|)
block|{
if|if
condition|(
name|maxConcurrentSearchRequests
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxConcurrentSearchRequests must be positive"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxConcurrentSearchRequests
operator|=
name|maxConcurrentSearchRequests
expr_stmt|;
return|return
name|this
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
name|maxConcurrentSearchRequests
operator|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|maxConcurrentSearchRequests
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

