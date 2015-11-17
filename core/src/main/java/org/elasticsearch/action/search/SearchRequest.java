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
name|ParseFieldMatcher
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Template
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
name|Scroll
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
name|builder
operator|.
name|SearchSourceBuilder
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|Scroll
operator|.
name|readScroll
import|;
end_import

begin_comment
comment|/**  * A request to execute search against one or more indices (or all). Best created using  * {@link org.elasticsearch.client.Requests#searchRequest(String...)}.  *<p>  * Note, the search {@link #source(org.elasticsearch.search.builder.SearchSourceBuilder)}  * is required. The search source is the different search options, including aggregations and such.  *</p>  * @see org.elasticsearch.client.Requests#searchRequest(String...)  * @see org.elasticsearch.client.Client#search(SearchRequest)  * @see SearchResponse  */
end_comment

begin_class
DECL|class|SearchRequest
specifier|public
class|class
name|SearchRequest
extends|extends
name|ActionRequest
argument_list|<
name|SearchRequest
argument_list|>
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
DECL|field|searchType
specifier|private
name|SearchType
name|searchType
init|=
name|SearchType
operator|.
name|DEFAULT
decl_stmt|;
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
decl_stmt|;
annotation|@
name|Nullable
DECL|field|routing
specifier|private
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
name|SearchSourceBuilder
name|source
decl_stmt|;
DECL|field|requestCache
specifier|private
name|Boolean
name|requestCache
decl_stmt|;
DECL|field|scroll
specifier|private
name|Scroll
name|scroll
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
DECL|field|DEFAULT_INDICES_OPTIONS
specifier|public
specifier|static
specifier|final
name|IndicesOptions
name|DEFAULT_INDICES_OPTIONS
init|=
name|IndicesOptions
operator|.
name|strictExpandOpenAndForbidClosed
argument_list|()
decl_stmt|;
DECL|field|indicesOptions
specifier|private
name|IndicesOptions
name|indicesOptions
init|=
name|DEFAULT_INDICES_OPTIONS
decl_stmt|;
DECL|field|template
specifier|private
name|Template
name|template
decl_stmt|;
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|()
block|{     }
comment|/**      * Copy constructor that creates a new search request that is a copy of the one provided as an argument.      * The new request will inherit though headers and context from the original request that caused it.      */
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|SearchRequest
name|searchRequest
parameter_list|,
name|ActionRequest
name|originalRequest
parameter_list|)
block|{
name|super
argument_list|(
name|originalRequest
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchType
operator|=
name|searchRequest
operator|.
name|searchType
expr_stmt|;
name|this
operator|.
name|indices
operator|=
name|searchRequest
operator|.
name|indices
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|searchRequest
operator|.
name|routing
expr_stmt|;
name|this
operator|.
name|preference
operator|=
name|searchRequest
operator|.
name|preference
expr_stmt|;
name|this
operator|.
name|template
operator|=
name|searchRequest
operator|.
name|template
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|searchRequest
operator|.
name|source
expr_stmt|;
name|this
operator|.
name|requestCache
operator|=
name|searchRequest
operator|.
name|requestCache
expr_stmt|;
name|this
operator|.
name|scroll
operator|=
name|searchRequest
operator|.
name|scroll
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|searchRequest
operator|.
name|types
expr_stmt|;
name|this
operator|.
name|indicesOptions
operator|=
name|searchRequest
operator|.
name|indicesOptions
expr_stmt|;
block|}
comment|/**      * Constructs a new search request starting from the provided request, meaning that it will      * inherit its headers and context      */
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|ActionRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
comment|/**      * Constructs a new search request against the indices. No indices provided here means that search      * will run against all indices.      */
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
argument_list|(
name|indices
argument_list|,
operator|new
name|SearchSourceBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a new search request against the provided indices with the given search source.      */
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|(
name|String
index|[]
name|indices
parameter_list|,
name|SearchSourceBuilder
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"source must not be null"
argument_list|)
throw|;
block|}
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|source
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
literal|null
decl_stmt|;
comment|// no need to check, we resolve to match all query
comment|//        if (source == null&& extraSource == null) {
comment|//            validationException = addValidationError("search source is missing", validationException);
comment|//        }
return|return
name|validationException
return|;
block|}
comment|/**      * Sets the indices the search will be executed on.      */
annotation|@
name|Override
DECL|method|indices
specifier|public
name|SearchRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
if|if
condition|(
name|indices
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"indices must not be null"
argument_list|)
throw|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|indices
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"indices["
operator|+
name|i
operator|+
literal|"] must not be null"
argument_list|)
throw|;
block|}
block|}
block|}
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
annotation|@
name|Override
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
name|SearchRequest
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
comment|/**      * The document types to execute the search against. Defaults to be executed against      * all types.      */
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|types
return|;
block|}
comment|/**      * The document types to execute the search against. Defaults to be executed against      * all types.      */
DECL|method|types
specifier|public
name|SearchRequest
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
name|SearchRequest
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
name|SearchRequest
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
comment|/**      * Sets the preference to execute the search. Defaults to randomize across shards. Can be set to      *<tt>_local</tt> to prefer local shards,<tt>_primary</tt> to execute only on primary shards, or      * a custom value, which guarantees that the same order will be used across different requests.      */
DECL|method|preference
specifier|public
name|SearchRequest
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
comment|/**      * The search type to execute, defaults to {@link SearchType#DEFAULT}.      */
DECL|method|searchType
specifier|public
name|SearchRequest
name|searchType
parameter_list|(
name|SearchType
name|searchType
parameter_list|)
block|{
name|this
operator|.
name|searchType
operator|=
name|searchType
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The a string representation search type to execute, defaults to {@link SearchType#DEFAULT}. Can be      * one of "dfs_query_then_fetch"/"dfsQueryThenFetch", "dfs_query_and_fetch"/"dfsQueryAndFetch",      * "query_then_fetch"/"queryThenFetch", and "query_and_fetch"/"queryAndFetch".      */
DECL|method|searchType
specifier|public
name|SearchRequest
name|searchType
parameter_list|(
name|String
name|searchType
parameter_list|)
block|{
return|return
name|searchType
argument_list|(
name|SearchType
operator|.
name|fromString
argument_list|(
name|searchType
argument_list|,
name|ParseFieldMatcher
operator|.
name|EMPTY
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * The source of the search request.      */
DECL|method|source
specifier|public
name|SearchRequest
name|source
parameter_list|(
name|SearchSourceBuilder
name|sourceBuilder
parameter_list|)
block|{
if|if
condition|(
name|sourceBuilder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"source must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|source
operator|=
name|sourceBuilder
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The search source to execute.      */
DECL|method|source
specifier|public
name|SearchSourceBuilder
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
comment|/**      * The stored template      */
DECL|method|template
specifier|public
name|void
name|template
parameter_list|(
name|Template
name|template
parameter_list|)
block|{
name|this
operator|.
name|template
operator|=
name|template
expr_stmt|;
block|}
comment|/**      * The stored template      */
DECL|method|template
specifier|public
name|Template
name|template
parameter_list|()
block|{
return|return
name|template
return|;
block|}
comment|/**      * The tye of search to execute.      */
DECL|method|searchType
specifier|public
name|SearchType
name|searchType
parameter_list|()
block|{
return|return
name|searchType
return|;
block|}
comment|/**      * The indices      */
annotation|@
name|Override
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
comment|/**      * If set, will enable scrolling of the search request.      */
DECL|method|scroll
specifier|public
name|Scroll
name|scroll
parameter_list|()
block|{
return|return
name|scroll
return|;
block|}
comment|/**      * If set, will enable scrolling of the search request.      */
DECL|method|scroll
specifier|public
name|SearchRequest
name|scroll
parameter_list|(
name|Scroll
name|scroll
parameter_list|)
block|{
name|this
operator|.
name|scroll
operator|=
name|scroll
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If set, will enable scrolling of the search request for the specified timeout.      */
DECL|method|scroll
specifier|public
name|SearchRequest
name|scroll
parameter_list|(
name|TimeValue
name|keepAlive
parameter_list|)
block|{
return|return
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|keepAlive
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * If set, will enable scrolling of the search request for the specified timeout.      */
DECL|method|scroll
specifier|public
name|SearchRequest
name|scroll
parameter_list|(
name|String
name|keepAlive
parameter_list|)
block|{
return|return
name|scroll
argument_list|(
operator|new
name|Scroll
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|keepAlive
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".Scroll.keepAlive"
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Sets if this request should use the request cache or not, assuming that it can (for      * example, if "now" is used, it will never be cached). By default (not set, or null,      * will default to the index level setting if request cache is enabled or not).      */
DECL|method|requestCache
specifier|public
name|SearchRequest
name|requestCache
parameter_list|(
name|Boolean
name|requestCache
parameter_list|)
block|{
name|this
operator|.
name|requestCache
operator|=
name|requestCache
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|requestCache
specifier|public
name|Boolean
name|requestCache
parameter_list|()
block|{
return|return
name|this
operator|.
name|requestCache
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
name|searchType
operator|=
name|SearchType
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|indices
operator|=
operator|new
name|String
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|scroll
operator|=
name|readScroll
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|source
operator|=
name|SearchSourceBuilder
operator|.
name|readSearchSourceFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|types
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
name|requestCache
operator|=
name|in
operator|.
name|readOptionalBoolean
argument_list|()
expr_stmt|;
name|template
operator|=
name|in
operator|.
name|readOptionalStreamable
argument_list|(
name|Template
operator|::
operator|new
argument_list|)
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
name|writeByte
argument_list|(
name|searchType
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|indices
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|scroll
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|writeTo
argument_list|(
name|out
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
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|source
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeStringArray
argument_list|(
name|types
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
name|writeOptionalBoolean
argument_list|(
name|requestCache
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|template
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

