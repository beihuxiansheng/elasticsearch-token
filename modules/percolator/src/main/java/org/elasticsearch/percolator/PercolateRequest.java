begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
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
comment|/**  * A request to execute a percolate operation.  *  * @deprecated Instead use search API with {@link PercolateQueryBuilder}  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|PercolateRequest
specifier|public
class|class
name|PercolateRequest
extends|extends
name|ActionRequest
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
DECL|field|indices
specifier|protected
name|String
index|[]
name|indices
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
DECL|field|onlyCount
specifier|private
name|boolean
name|onlyCount
decl_stmt|;
DECL|field|getRequest
specifier|private
name|GetRequest
name|getRequest
decl_stmt|;
DECL|field|source
specifier|private
name|BytesReference
name|source
decl_stmt|;
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
DECL|method|indices
specifier|public
specifier|final
name|PercolateRequest
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
name|PercolateRequest
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
comment|/**      * Getter for {@link #documentType(String)}      */
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
comment|/**      * Sets the type of the document to percolate. This is important as it selects the mapping to be used to parse      * the document.      */
DECL|method|documentType
specifier|public
name|PercolateRequest
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
comment|/**      * Getter for {@link #routing(String)}      */
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
comment|/**      * A comma separated list of routing values to control the shards the search will be executed on.      */
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
comment|/**      * Getter for {@link #preference(String)}      */
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
comment|/**      * Sets the preference to execute the search. Defaults to randomize across shards. Can be set to      *<tt>_local</tt> to prefer local shards,<tt>_primary</tt> to execute only on primary shards, or      * a custom value, which guarantees that the same order will be used across different requests.      */
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
comment|/**      * Getter for {@link #getRequest(GetRequest)}      */
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
comment|/**      * This defines where to fetch the document to be percolated from, which is an alternative of defining the document      * to percolate in the request body.      *      * If this defined than this will override the document specified in the request body.      */
DECL|method|getRequest
specifier|public
name|PercolateRequest
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
return|return
name|this
return|;
block|}
comment|/**      * @return The request body in its raw form.      */
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
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
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
name|Requests
operator|.
name|CONTENT_TYPE
argument_list|)
return|;
block|}
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
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
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
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
return|return
name|this
return|;
block|}
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
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
return|return
name|this
return|;
block|}
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
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
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
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
operator|new
name|BytesArray
argument_list|(
name|source
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Raw version of {@link #source(PercolateSourceBuilder)}      */
DECL|method|source
specifier|public
name|PercolateRequest
name|source
parameter_list|(
name|BytesReference
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the request body definition for this percolate request as raw bytes.      *      * This is the preferred way to set the request body.      */
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
name|Requests
operator|.
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Getter for {@link #onlyCount(boolean)}      */
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
comment|/**      * Sets whether this percolate request should only count the number of percolator queries that matches with      * the document being percolated and don't keep track of the actual queries that have matched.      */
DECL|method|onlyCount
specifier|public
name|PercolateRequest
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
name|storedFields
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"get stored fields option isn't supported via percolate request"
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
name|indices
operator|=
name|in
operator|.
name|readStringArray
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
name|source
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
argument_list|()
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
name|writeStringArrayNullable
argument_list|(
name|indices
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

