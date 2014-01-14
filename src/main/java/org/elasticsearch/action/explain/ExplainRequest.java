begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.explain
package|package
name|org
operator|.
name|elasticsearch
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
name|ValidateActions
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
name|action
operator|.
name|support
operator|.
name|single
operator|.
name|shard
operator|.
name|SingleShardOperationRequest
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
name|Requests
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
name|XContentType
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

begin_comment
comment|/**  * Explain request encapsulating the explain query and document identifier to get an explanation for.  */
end_comment

begin_class
DECL|class|ExplainRequest
specifier|public
class|class
name|ExplainRequest
extends|extends
name|SingleShardOperationRequest
argument_list|<
name|ExplainRequest
argument_list|>
block|{
DECL|field|contentType
specifier|private
specifier|static
specifier|final
name|XContentType
name|contentType
init|=
name|Requests
operator|.
name|CONTENT_TYPE
decl_stmt|;
DECL|field|type
specifier|private
name|String
name|type
init|=
literal|"_all"
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
decl_stmt|;
DECL|field|routing
specifier|private
name|String
name|routing
decl_stmt|;
DECL|field|preference
specifier|private
name|String
name|preference
decl_stmt|;
DECL|field|source
specifier|private
name|BytesReference
name|source
decl_stmt|;
DECL|field|sourceUnsafe
specifier|private
name|boolean
name|sourceUnsafe
decl_stmt|;
DECL|field|fields
specifier|private
name|String
index|[]
name|fields
decl_stmt|;
DECL|field|fetchSourceContext
specifier|private
name|FetchSourceContext
name|fetchSourceContext
decl_stmt|;
DECL|field|filteringAlias
specifier|private
name|String
index|[]
name|filteringAlias
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|nowInMillis
name|long
name|nowInMillis
decl_stmt|;
DECL|method|ExplainRequest
name|ExplainRequest
parameter_list|()
block|{     }
DECL|method|ExplainRequest
specifier|public
name|ExplainRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
DECL|method|type
specifier|public
name|ExplainRequest
name|type
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|id
specifier|public
name|ExplainRequest
name|id
parameter_list|(
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|routing
specifier|public
name|String
name|routing
parameter_list|()
block|{
return|return
name|routing
return|;
block|}
DECL|method|routing
specifier|public
name|ExplainRequest
name|routing
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|this
operator|.
name|routing
operator|=
name|routing
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Simple sets the routing. Since the parent is only used to get to the right shard.      */
DECL|method|parent
specifier|public
name|ExplainRequest
name|parent
parameter_list|(
name|String
name|parent
parameter_list|)
block|{
name|this
operator|.
name|routing
operator|=
name|parent
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|preference
specifier|public
name|String
name|preference
parameter_list|()
block|{
return|return
name|preference
return|;
block|}
DECL|method|preference
specifier|public
name|ExplainRequest
name|preference
parameter_list|(
name|String
name|preference
parameter_list|)
block|{
name|this
operator|.
name|preference
operator|=
name|preference
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|source
specifier|public
name|BytesReference
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
DECL|method|sourceUnsafe
specifier|public
name|boolean
name|sourceUnsafe
parameter_list|()
block|{
return|return
name|sourceUnsafe
return|;
block|}
DECL|method|source
specifier|public
name|ExplainRequest
name|source
parameter_list|(
name|QuerySourceBuilder
name|sourceBuilder
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|sourceBuilder
operator|.
name|buildAsBytes
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|this
operator|.
name|sourceUnsafe
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|source
specifier|public
name|ExplainRequest
name|source
parameter_list|(
name|BytesReference
name|source
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|sourceUnsafe
operator|=
name|unsafe
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Allows setting the {@link FetchSourceContext} for this request, controlling if and how _source should be returned.      */
DECL|method|fetchSourceContext
specifier|public
name|ExplainRequest
name|fetchSourceContext
parameter_list|(
name|FetchSourceContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|fetchSourceContext
operator|=
name|context
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|fetchSourceContext
specifier|public
name|FetchSourceContext
name|fetchSourceContext
parameter_list|()
block|{
return|return
name|fetchSourceContext
return|;
block|}
DECL|method|fields
specifier|public
name|String
index|[]
name|fields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
DECL|method|fields
specifier|public
name|ExplainRequest
name|fields
parameter_list|(
name|String
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|filteringAlias
specifier|public
name|String
index|[]
name|filteringAlias
parameter_list|()
block|{
return|return
name|filteringAlias
return|;
block|}
DECL|method|filteringAlias
specifier|public
name|ExplainRequest
name|filteringAlias
parameter_list|(
name|String
index|[]
name|filteringAlias
parameter_list|)
block|{
if|if
condition|(
name|filteringAlias
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|filteringAlias
operator|=
name|filteringAlias
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|beforeLocalFork
specifier|protected
name|void
name|beforeLocalFork
parameter_list|()
block|{
if|if
condition|(
name|sourceUnsafe
condition|)
block|{
name|source
operator|=
name|source
operator|.
name|copyBytesArray
argument_list|()
expr_stmt|;
name|sourceUnsafe
operator|=
literal|false
expr_stmt|;
block|}
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
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|ValidateActions
operator|.
name|addValidationError
argument_list|(
literal|"type is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|ValidateActions
operator|.
name|addValidationError
argument_list|(
literal|"id is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|ValidateActions
operator|.
name|addValidationError
argument_list|(
literal|"source is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
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
name|type
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|id
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|routing
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|preference
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|source
operator|=
name|in
operator|.
name|readBytesReference
argument_list|()
expr_stmt|;
name|sourceUnsafe
operator|=
literal|false
expr_stmt|;
name|filteringAlias
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|fields
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
block|}
name|fetchSourceContext
operator|=
name|FetchSourceContext
operator|.
name|optionalReadFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|nowInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
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
name|writeString
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|preference
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|filteringAlias
argument_list|)
expr_stmt|;
if|if
condition|(
name|fields
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
name|out
operator|.
name|writeStringArray
argument_list|(
name|fields
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
block|}
name|FetchSourceContext
operator|.
name|optionalWriteToStream
argument_list|(
name|fetchSourceContext
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|nowInMillis
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

