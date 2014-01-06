begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticsearchGenerationException
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
name|broadcast
operator|.
name|BroadcastOperationRequest
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PercolateRequest
specifier|public
class|class
name|PercolateRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|PercolateRequest
argument_list|>
block|{
DECL|field|contentType
specifier|public
specifier|static
specifier|final
name|XContentType
name|contentType
init|=
name|Requests
operator|.
name|CONTENT_TYPE
decl_stmt|;
DECL|field|documentType
specifier|private
name|String
name|documentType
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
DECL|field|getRequest
specifier|private
name|GetRequest
name|getRequest
decl_stmt|;
DECL|field|onlyCount
specifier|private
name|boolean
name|onlyCount
decl_stmt|;
DECL|field|source
specifier|private
name|BytesReference
name|source
decl_stmt|;
DECL|field|unsafe
specifier|private
name|boolean
name|unsafe
decl_stmt|;
DECL|field|docSource
specifier|private
name|BytesReference
name|docSource
decl_stmt|;
comment|// Used internally in order to compute tookInMillis, TransportBroadcastOperationAction itself doesn't allow
comment|// to hold it temporarily in an easy way
DECL|field|startTime
name|long
name|startTime
decl_stmt|;
DECL|method|PercolateRequest
specifier|public
name|PercolateRequest
parameter_list|()
block|{     }
DECL|method|PercolateRequest
specifier|public
name|PercolateRequest
parameter_list|(
name|PercolateRequest
name|request
parameter_list|,
name|BytesReference
name|docSource
parameter_list|)
block|{
name|super
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|operationThreading
argument_list|(
name|request
operator|.
name|operationThreading
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|documentType
operator|=
name|request
operator|.
name|documentType
argument_list|()
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|request
operator|.
name|routing
argument_list|()
expr_stmt|;
name|this
operator|.
name|preference
operator|=
name|request
operator|.
name|preference
argument_list|()
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|request
operator|.
name|source
expr_stmt|;
name|this
operator|.
name|docSource
operator|=
name|docSource
expr_stmt|;
name|this
operator|.
name|onlyCount
operator|=
name|request
operator|.
name|onlyCount
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|request
operator|.
name|startTime
expr_stmt|;
block|}
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
DECL|method|documentType
specifier|public
name|void
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
name|PercolateRequest
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
name|PercolateRequest
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
DECL|method|getRequest
specifier|public
name|GetRequest
name|getRequest
parameter_list|()
block|{
return|return
name|getRequest
return|;
block|}
DECL|method|getRequest
specifier|public
name|void
name|getRequest
parameter_list|(
name|GetRequest
name|getRequest
parameter_list|)
block|{
name|this
operator|.
name|getRequest
operator|=
name|getRequest
expr_stmt|;
block|}
comment|/**      * Before we fork on a local thread, make sure we copy over the bytes if they are unsafe      */
annotation|@
name|Override
DECL|method|beforeLocalFork
specifier|public
name|void
name|beforeLocalFork
parameter_list|()
block|{
if|if
condition|(
name|unsafe
condition|)
block|{
name|source
operator|=
name|source
operator|.
name|copyBytesArray
argument_list|()
expr_stmt|;
name|unsafe
operator|=
literal|false
expr_stmt|;
block|}
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
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|Map
name|document
parameter_list|)
throws|throws
name|ElasticsearchGenerationException
block|{
return|return
name|source
argument_list|(
name|document
argument_list|,
name|contentType
argument_list|)
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|Map
name|document
parameter_list|,
name|XContentType
name|contentType
parameter_list|)
throws|throws
name|ElasticsearchGenerationException
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|document
argument_list|)
expr_stmt|;
return|return
name|source
argument_list|(
name|builder
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchGenerationException
argument_list|(
literal|"Failed to generate ["
operator|+
name|document
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|String
name|document
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
operator|new
name|BytesArray
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|this
operator|.
name|unsafe
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|XContentBuilder
name|documentBuilder
parameter_list|)
block|{
name|source
operator|=
name|documentBuilder
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|unsafe
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|byte
index|[]
name|document
parameter_list|)
block|{
return|return
name|source
argument_list|(
name|document
argument_list|,
literal|0
argument_list|,
name|document
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
name|source
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
return|return
name|source
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|,
name|unsafe
argument_list|)
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
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
name|unsafe
operator|=
name|unsafe
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|PercolateSourceBuilder
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
name|unsafe
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|onlyCount
specifier|public
name|boolean
name|onlyCount
parameter_list|()
block|{
return|return
name|onlyCount
return|;
block|}
DECL|method|onlyCount
specifier|public
name|void
name|onlyCount
parameter_list|(
name|boolean
name|onlyCount
parameter_list|)
block|{
name|this
operator|.
name|onlyCount
operator|=
name|onlyCount
expr_stmt|;
block|}
DECL|method|docSource
name|BytesReference
name|docSource
parameter_list|()
block|{
return|return
name|docSource
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
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|documentType
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
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
name|source
operator|==
literal|null
operator|&&
name|getRequest
operator|==
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"source or get is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getRequest
operator|!=
literal|null
operator|&&
name|getRequest
operator|.
name|fields
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"get fields option isn't supported via percolate request"
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
name|startTime
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|documentType
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
name|unsafe
operator|=
literal|false
expr_stmt|;
name|source
operator|=
name|in
operator|.
name|readBytesReference
argument_list|()
expr_stmt|;
name|docSource
operator|=
name|in
operator|.
name|readBytesReference
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
name|getRequest
operator|=
operator|new
name|GetRequest
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|getRequest
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|onlyCount
operator|=
name|in
operator|.
name|readBoolean
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
name|writeVLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|documentType
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
name|writeBytesReference
argument_list|(
name|docSource
argument_list|)
expr_stmt|;
if|if
condition|(
name|getRequest
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
name|getRequest
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
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|onlyCount
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

