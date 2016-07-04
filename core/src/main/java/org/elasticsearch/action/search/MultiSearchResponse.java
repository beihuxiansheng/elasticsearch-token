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
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|ActionResponse
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|ToXContent
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A multi search response.  */
end_comment

begin_class
DECL|class|MultiSearchResponse
specifier|public
class|class
name|MultiSearchResponse
extends|extends
name|ActionResponse
implements|implements
name|Iterable
argument_list|<
name|MultiSearchResponse
operator|.
name|Item
argument_list|>
implements|,
name|ToXContent
block|{
comment|/**      * A search response item, holding the actual search response, or an error message if it failed.      */
DECL|class|Item
specifier|public
specifier|static
class|class
name|Item
implements|implements
name|Streamable
block|{
DECL|field|response
specifier|private
name|SearchResponse
name|response
decl_stmt|;
DECL|field|exception
specifier|private
name|Exception
name|exception
decl_stmt|;
DECL|method|Item
name|Item
parameter_list|()
block|{          }
DECL|method|Item
specifier|public
name|Item
parameter_list|(
name|SearchResponse
name|response
parameter_list|,
name|Exception
name|exception
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
block|}
comment|/**          * Is it a failed search?          */
DECL|method|isFailure
specifier|public
name|boolean
name|isFailure
parameter_list|()
block|{
return|return
name|exception
operator|!=
literal|null
return|;
block|}
comment|/**          * The actual failure message, null if its not a failure.          */
annotation|@
name|Nullable
DECL|method|getFailureMessage
specifier|public
name|String
name|getFailureMessage
parameter_list|()
block|{
return|return
name|exception
operator|==
literal|null
condition|?
literal|null
else|:
name|exception
operator|.
name|getMessage
argument_list|()
return|;
block|}
comment|/**          * The actual search response, null if its a failure.          */
annotation|@
name|Nullable
DECL|method|getResponse
specifier|public
name|SearchResponse
name|getResponse
parameter_list|()
block|{
return|return
name|this
operator|.
name|response
return|;
block|}
DECL|method|readItem
specifier|public
specifier|static
name|Item
name|readItem
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Item
name|item
init|=
operator|new
name|Item
argument_list|()
decl_stmt|;
name|item
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|item
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|this
operator|.
name|response
operator|=
operator|new
name|SearchResponse
argument_list|()
expr_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|exception
operator|=
name|in
operator|.
name|readException
argument_list|()
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
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeThrowable
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getFailure
specifier|public
name|Exception
name|getFailure
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
block|}
DECL|field|items
specifier|private
name|Item
index|[]
name|items
decl_stmt|;
DECL|method|MultiSearchResponse
name|MultiSearchResponse
parameter_list|()
block|{     }
DECL|method|MultiSearchResponse
specifier|public
name|MultiSearchResponse
parameter_list|(
name|Item
index|[]
name|items
parameter_list|)
block|{
name|this
operator|.
name|items
operator|=
name|items
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Item
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|items
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/**      * The list of responses, the order is the same as the one provided in the request.      */
DECL|method|getResponses
specifier|public
name|Item
index|[]
name|getResponses
parameter_list|()
block|{
return|return
name|this
operator|.
name|items
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
name|items
operator|=
operator|new
name|Item
index|[
name|in
operator|.
name|readVInt
argument_list|()
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|items
index|[
name|i
index|]
operator|=
name|Item
operator|.
name|readItem
argument_list|(
name|in
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
name|items
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|items
control|)
block|{
name|item
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|RESPONSES
argument_list|)
expr_stmt|;
for|for
control|(
name|Item
name|item
range|:
name|items
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|item
operator|.
name|isFailure
argument_list|()
condition|)
block|{
name|ElasticsearchException
operator|.
name|renderException
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
name|item
operator|.
name|getFailure
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATUS
argument_list|,
name|ExceptionsHelper
operator|.
name|status
argument_list|(
name|item
operator|.
name|getFailure
argument_list|()
argument_list|)
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|.
name|getResponse
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATUS
argument_list|,
name|item
operator|.
name|getResponse
argument_list|()
operator|.
name|status
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
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
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|RESPONSES
specifier|static
specifier|final
name|String
name|RESPONSES
init|=
literal|"responses"
decl_stmt|;
DECL|field|STATUS
specifier|static
specifier|final
name|String
name|STATUS
init|=
literal|"status"
decl_stmt|;
DECL|field|ERROR
specifier|static
specifier|final
name|String
name|ERROR
init|=
literal|"error"
decl_stmt|;
DECL|field|ROOT_CAUSE
specifier|static
specifier|final
name|String
name|ROOT_CAUSE
init|=
literal|"root_cause"
decl_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|"{ \"error\" : \""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\"}"
return|;
block|}
block|}
block|}
end_class

end_unit

