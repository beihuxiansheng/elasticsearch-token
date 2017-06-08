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
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskId
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
comment|/**  * A request to execute search against one or more indices (or all). Best created using  * {@link org.elasticsearch.client.Requests#searchRequest(String...)}.  *<p>  * Note, the search {@link #source(org.elasticsearch.search.builder.SearchSourceBuilder)}  * is required. The search source is the different search options, including aggregations and such.  *</p>  *  * @see org.elasticsearch.client.Requests#searchRequest(String...)  * @see org.elasticsearch.client.Client#search(SearchRequest)  * @see SearchResponse  */
end_comment

begin_class
DECL|class|SearchRequest
specifier|public
specifier|final
class|class
name|SearchRequest
extends|extends
name|ActionRequest
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
DECL|field|FORMAT_PARAMS
specifier|private
specifier|static
specifier|final
name|ToXContent
operator|.
name|Params
name|FORMAT_PARAMS
init|=
operator|new
name|ToXContent
operator|.
name|MapParams
argument_list|(
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"pretty"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
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
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
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
DECL|field|batchedReduceSize
specifier|private
name|int
name|batchedReduceSize
init|=
literal|512
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
DECL|method|SearchRequest
specifier|public
name|SearchRequest
parameter_list|()
block|{     }
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
if|if
condition|(
name|source
operator|!=
literal|null
operator|&&
name|source
operator|.
name|trackTotalHits
argument_list|()
operator|==
literal|false
operator|&&
name|scroll
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|validationException
operator|=
name|addValidationError
argument_list|(
literal|"disabling [track_total_hits] is not allowed in a scroll context"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|indices
argument_list|,
literal|"indices must not be null"
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|index
argument_list|,
literal|"index must not be null"
argument_list|)
expr_stmt|;
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|indicesOptions
argument_list|,
literal|"indicesOptions must not be null"
argument_list|)
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|types
argument_list|,
literal|"types must not be null"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|type
range|:
name|types
control|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|,
literal|"type must not be null"
argument_list|)
expr_stmt|;
block|}
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
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|searchType
argument_list|,
literal|"searchType must not be null"
argument_list|)
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
name|this
operator|.
name|source
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sourceBuilder
argument_list|,
literal|"source must not be null"
argument_list|)
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
comment|/**      * Sets the number of shard results that should be reduced at once on the coordinating node. This value should be used as a protection      * mechanism to reduce the memory overhead per search request if the potential number of shards in the request can be large.      */
DECL|method|setBatchedReduceSize
specifier|public
name|void
name|setBatchedReduceSize
parameter_list|(
name|int
name|batchedReduceSize
parameter_list|)
block|{
if|if
condition|(
name|batchedReduceSize
operator|<=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"batchedReduceSize must be>= 2"
argument_list|)
throw|;
block|}
name|this
operator|.
name|batchedReduceSize
operator|=
name|batchedReduceSize
expr_stmt|;
block|}
comment|/**      * Returns the number of shard results that should be reduced at once on the coordinating node. This value should be used as a      * protection mechanism to reduce the memory overhead per search request if the potential number of shards in the request can be large.      */
DECL|method|getBatchedReduceSize
specifier|public
name|int
name|getBatchedReduceSize
parameter_list|()
block|{
return|return
name|batchedReduceSize
return|;
block|}
comment|/**      * @return true if the request only has suggest      */
DECL|method|isSuggestOnly
specifier|public
name|boolean
name|isSuggestOnly
parameter_list|()
block|{
return|return
name|source
operator|!=
literal|null
operator|&&
name|source
operator|.
name|isSuggestOnly
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|createTask
specifier|public
name|Task
name|createTask
parameter_list|(
name|long
name|id
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|action
parameter_list|,
name|TaskId
name|parentTaskId
parameter_list|)
block|{
comment|// generating description in a lazy way since source can be quite big
return|return
operator|new
name|SearchTask
argument_list|(
name|id
argument_list|,
name|type
argument_list|,
name|action
argument_list|,
literal|null
argument_list|,
name|parentTaskId
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"indices["
argument_list|)
expr_stmt|;
name|Strings
operator|.
name|arrayToDelimitedString
argument_list|(
name|indices
argument_list|,
literal|","
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"types["
argument_list|)
expr_stmt|;
name|Strings
operator|.
name|arrayToDelimitedString
argument_list|(
name|types
argument_list|,
literal|","
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"search_type["
argument_list|)
operator|.
name|append
argument_list|(
name|searchType
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
expr_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"source["
argument_list|)
operator|.
name|append
argument_list|(
name|source
operator|.
name|toString
argument_list|(
name|FORMAT_PARAMS
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"source[]"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
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
name|scroll
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|Scroll
operator|::
operator|new
argument_list|)
expr_stmt|;
name|source
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|SearchSourceBuilder
operator|::
operator|new
argument_list|)
expr_stmt|;
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
name|batchedReduceSize
operator|=
name|in
operator|.
name|readVInt
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
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|scroll
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
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
name|writeVInt
argument_list|(
name|batchedReduceSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SearchRequest
name|that
init|=
operator|(
name|SearchRequest
operator|)
name|o
decl_stmt|;
return|return
name|searchType
operator|==
name|that
operator|.
name|searchType
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|indices
argument_list|,
name|that
operator|.
name|indices
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|routing
argument_list|,
name|that
operator|.
name|routing
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|preference
argument_list|,
name|that
operator|.
name|preference
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|source
argument_list|,
name|that
operator|.
name|source
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|requestCache
argument_list|,
name|that
operator|.
name|requestCache
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|scroll
argument_list|,
name|that
operator|.
name|scroll
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|types
argument_list|,
name|that
operator|.
name|types
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|indicesOptions
argument_list|,
name|that
operator|.
name|indicesOptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|searchType
argument_list|,
name|Arrays
operator|.
name|hashCode
argument_list|(
name|indices
argument_list|)
argument_list|,
name|routing
argument_list|,
name|preference
argument_list|,
name|source
argument_list|,
name|requestCache
argument_list|,
name|scroll
argument_list|,
name|Arrays
operator|.
name|hashCode
argument_list|(
name|types
argument_list|)
argument_list|,
name|indicesOptions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SearchRequest{"
operator|+
literal|"searchType="
operator|+
name|searchType
operator|+
literal|", indices="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|indices
argument_list|)
operator|+
literal|", indicesOptions="
operator|+
name|indicesOptions
operator|+
literal|", types="
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
operator|+
literal|", routing='"
operator|+
name|routing
operator|+
literal|'\''
operator|+
literal|", preference='"
operator|+
name|preference
operator|+
literal|'\''
operator|+
literal|", requestCache="
operator|+
name|requestCache
operator|+
literal|", scroll="
operator|+
name|scroll
operator|+
literal|", source="
operator|+
name|source
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

