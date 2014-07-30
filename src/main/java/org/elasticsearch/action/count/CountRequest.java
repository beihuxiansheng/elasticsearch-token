begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.count
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|count
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
name|ElasticsearchIllegalArgumentException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|XContentHelper
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
operator|.
name|DEFAULT_TERMINATE_AFTER
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
name|Map
import|;
end_import

begin_comment
comment|/**  * A request to count the number of documents matching a specific query. Best created with  * {@link org.elasticsearch.client.Requests#countRequest(String...)}.  *<p/>  *<p>The request requires the query source to be set either using {@link #source(QuerySourceBuilder)},  * or {@link #source(byte[])}.  *  * @see CountResponse  * @see org.elasticsearch.client.Client#count(CountRequest)  * @see org.elasticsearch.client.Requests#countRequest(String...)  */
end_comment

begin_class
DECL|class|CountRequest
specifier|public
class|class
name|CountRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|CountRequest
argument_list|>
block|{
DECL|field|DEFAULT_MIN_SCORE
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_MIN_SCORE
init|=
operator|-
literal|1f
decl_stmt|;
DECL|field|minScore
specifier|private
name|float
name|minScore
init|=
name|DEFAULT_MIN_SCORE
decl_stmt|;
annotation|@
name|Nullable
DECL|field|routing
specifier|protected
name|String
name|routing
decl_stmt|;
annotation|@
name|Nullable
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
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|nowInMillis
name|long
name|nowInMillis
decl_stmt|;
DECL|field|terminateAfter
specifier|private
name|int
name|terminateAfter
init|=
name|DEFAULT_TERMINATE_AFTER
decl_stmt|;
DECL|method|CountRequest
name|CountRequest
parameter_list|()
block|{     }
comment|/**      * Constructs a new count request against the provided indices. No indices provided means it will      * run against all indices.      */
DECL|method|CountRequest
specifier|public
name|CountRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|)
expr_stmt|;
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
return|return
name|validationException
return|;
block|}
annotation|@
name|Override
DECL|method|beforeStart
specifier|protected
name|void
name|beforeStart
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
comment|/**      * The minimum score of the documents to include in the count.      */
DECL|method|minScore
name|float
name|minScore
parameter_list|()
block|{
return|return
name|minScore
return|;
block|}
comment|/**      * The minimum score of the documents to include in the count. Defaults to<tt>-1</tt> which means all      * documents will be included in the count.      */
DECL|method|minScore
specifier|public
name|CountRequest
name|minScore
parameter_list|(
name|float
name|minScore
parameter_list|)
block|{
name|this
operator|.
name|minScore
operator|=
name|minScore
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The source to execute.      */
DECL|method|source
name|BytesReference
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
comment|/**      * The source to execute.      */
DECL|method|source
specifier|public
name|CountRequest
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
name|Requests
operator|.
name|CONTENT_TYPE
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
comment|/**      * The source to execute in the form of a map.      */
DECL|method|source
specifier|public
name|CountRequest
name|source
parameter_list|(
name|Map
name|querySource
parameter_list|)
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
name|Requests
operator|.
name|CONTENT_TYPE
argument_list|)
decl_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|querySource
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
name|querySource
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
name|CountRequest
name|source
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|builder
operator|.
name|bytes
argument_list|()
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
comment|/**      * The source to execute. It is preferable to use either {@link #source(byte[])}      * or {@link #source(QuerySourceBuilder)}.      */
DECL|method|source
specifier|public
name|CountRequest
name|source
parameter_list|(
name|String
name|querySource
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
operator|new
name|BytesArray
argument_list|(
name|querySource
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
comment|/**      * The source to execute.      */
DECL|method|source
specifier|public
name|CountRequest
name|source
parameter_list|(
name|byte
index|[]
name|querySource
parameter_list|)
block|{
return|return
name|source
argument_list|(
name|querySource
argument_list|,
literal|0
argument_list|,
name|querySource
operator|.
name|length
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * The source to execute.      */
DECL|method|source
specifier|public
name|CountRequest
name|source
parameter_list|(
name|byte
index|[]
name|querySource
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
name|querySource
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
name|CountRequest
name|source
parameter_list|(
name|BytesReference
name|querySource
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|querySource
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
comment|/**      * The types of documents the query will run against. Defaults to all types.      */
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|this
operator|.
name|types
return|;
block|}
comment|/**      * The types of documents the query will run against. Defaults to all types.      */
DECL|method|types
specifier|public
name|CountRequest
name|types
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A comma separated list of routing values to control the shards the search will be executed on.      */
DECL|method|routing
specifier|public
name|String
name|routing
parameter_list|()
block|{
return|return
name|this
operator|.
name|routing
return|;
block|}
comment|/**      * A comma separated list of routing values to control the shards the search will be executed on.      */
DECL|method|routing
specifier|public
name|CountRequest
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
comment|/**      * The routing values to control the shards that the search will be executed on.      */
DECL|method|routing
specifier|public
name|CountRequest
name|routing
parameter_list|(
name|String
modifier|...
name|routings
parameter_list|)
block|{
name|this
operator|.
name|routing
operator|=
name|Strings
operator|.
name|arrayToCommaDelimitedString
argument_list|(
name|routings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|preference
specifier|public
name|CountRequest
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
DECL|method|preference
specifier|public
name|String
name|preference
parameter_list|()
block|{
return|return
name|this
operator|.
name|preference
return|;
block|}
comment|/**      * Upon reaching<code>terminateAfter</code> counts, the count request will early terminate      */
DECL|method|terminateAfter
specifier|public
name|CountRequest
name|terminateAfter
parameter_list|(
name|int
name|terminateAfterCount
parameter_list|)
block|{
if|if
condition|(
name|terminateAfterCount
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"terminateAfter must be> 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|terminateAfter
operator|=
name|terminateAfterCount
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|terminateAfter
specifier|public
name|int
name|terminateAfter
parameter_list|()
block|{
return|return
name|this
operator|.
name|terminateAfter
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
name|minScore
operator|=
name|in
operator|.
name|readFloat
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
name|sourceUnsafe
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
name|types
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
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_4_0
argument_list|)
condition|)
block|{
name|terminateAfter
operator|=
name|in
operator|.
name|readVInt
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|minScore
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
name|types
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_4_0
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|terminateAfter
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|sSource
init|=
literal|"_na_"
decl_stmt|;
try|try
block|{
name|sSource
operator|=
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|source
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
literal|"["
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|indices
argument_list|)
operator|+
literal|"]"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
operator|+
literal|", source["
operator|+
name|sSource
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

