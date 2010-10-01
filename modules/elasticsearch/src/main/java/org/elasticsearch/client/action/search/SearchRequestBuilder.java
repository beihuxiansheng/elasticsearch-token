begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
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
name|ElasticSearchIllegalArgumentException
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
name|search
operator|.
name|SearchOperationThreading
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
name|search
operator|.
name|SearchRequest
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
name|search
operator|.
name|SearchResponse
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
name|search
operator|.
name|SearchType
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
name|client
operator|.
name|action
operator|.
name|support
operator|.
name|BaseRequestBuilder
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
name|index
operator|.
name|query
operator|.
name|xcontent
operator|.
name|XContentQueryBuilder
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
name|search
operator|.
name|facets
operator|.
name|AbstractFacetBuilder
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
name|highlight
operator|.
name|HighlightBuilder
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
name|sort
operator|.
name|SortBuilder
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
name|sort
operator|.
name|SortOrder
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
comment|/**  * A search action request builder.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SearchRequestBuilder
specifier|public
class|class
name|SearchRequestBuilder
extends|extends
name|BaseRequestBuilder
argument_list|<
name|SearchRequest
argument_list|,
name|SearchResponse
argument_list|>
block|{
DECL|field|sourceBuilder
specifier|private
name|SearchSourceBuilder
name|sourceBuilder
decl_stmt|;
DECL|method|SearchRequestBuilder
specifier|public
name|SearchRequestBuilder
parameter_list|(
name|Client
name|client
parameter_list|)
block|{
name|super
argument_list|(
name|client
argument_list|,
operator|new
name|SearchRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the indices the search will be executed on.      */
DECL|method|setIndices
specifier|public
name|SearchRequestBuilder
name|setIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The document types to execute the search against. Defaults to be executed against      * all types.      */
DECL|method|setTypes
specifier|public
name|SearchRequestBuilder
name|setTypes
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|request
operator|.
name|types
argument_list|(
name|types
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The search type to execute, defaults to {@link org.elasticsearch.action.search.SearchType#DEFAULT}.      */
DECL|method|setSearchType
specifier|public
name|SearchRequestBuilder
name|setSearchType
parameter_list|(
name|SearchType
name|searchType
parameter_list|)
block|{
name|request
operator|.
name|searchType
argument_list|(
name|searchType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The a string representation search type to execute, defaults to {@link SearchType#DEFAULT}. Can be      * one of "dfs_query_then_fetch"/"dfsQueryThenFetch", "dfs_query_and_fetch"/"dfsQueryAndFetch",      * "query_then_fetch"/"queryThenFetch", and "query_and_fetch"/"queryAndFetch".      */
DECL|method|setSearchType
specifier|public
name|SearchRequestBuilder
name|setSearchType
parameter_list|(
name|String
name|searchType
parameter_list|)
throws|throws
name|ElasticSearchIllegalArgumentException
block|{
name|request
operator|.
name|searchType
argument_list|(
name|searchType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If set, will enable scrolling of the search request.      */
DECL|method|setScroll
specifier|public
name|SearchRequestBuilder
name|setScroll
parameter_list|(
name|Scroll
name|scroll
parameter_list|)
block|{
name|request
operator|.
name|scroll
argument_list|(
name|scroll
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If set, will enable scrolling of the search request for the specified timeout.      */
DECL|method|setScroll
specifier|public
name|SearchRequestBuilder
name|setScroll
parameter_list|(
name|TimeValue
name|keepAlive
parameter_list|)
block|{
name|request
operator|.
name|scroll
argument_list|(
name|keepAlive
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * If set, will enable scrolling of the search request for the specified timeout.      */
DECL|method|setScroll
specifier|public
name|SearchRequestBuilder
name|setScroll
parameter_list|(
name|String
name|keepAlive
parameter_list|)
block|{
name|request
operator|.
name|scroll
argument_list|(
name|keepAlive
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional timeout to control how long search is allowed to take.      */
DECL|method|setTimeout
specifier|public
name|SearchRequestBuilder
name|setTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional timeout to control how long search is allowed to take.      */
DECL|method|setTimeout
specifier|public
name|SearchRequestBuilder
name|setTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A query hint to optionally later be used when routing the request.      */
DECL|method|setQueryHint
specifier|public
name|SearchRequestBuilder
name|setQueryHint
parameter_list|(
name|String
name|queryHint
parameter_list|)
block|{
name|request
operator|.
name|queryHint
argument_list|(
name|queryHint
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Controls the the search operation threading model.      */
DECL|method|setOperationThreading
specifier|public
name|SearchRequestBuilder
name|setOperationThreading
parameter_list|(
name|SearchOperationThreading
name|operationThreading
parameter_list|)
block|{
name|request
operator|.
name|operationThreading
argument_list|(
name|operationThreading
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the string representation of the operation threading model. Can be one of      * "no_threads", "single_thread" and "thread_per_shard".      */
DECL|method|setOperationThreading
specifier|public
name|SearchRequestBuilder
name|setOperationThreading
parameter_list|(
name|String
name|operationThreading
parameter_list|)
block|{
name|request
operator|.
name|operationThreading
argument_list|(
name|operationThreading
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the listener be called on a separate thread if needed.      */
DECL|method|setListenerThreaded
specifier|public
name|SearchRequestBuilder
name|setListenerThreaded
parameter_list|(
name|boolean
name|listenerThreaded
parameter_list|)
block|{
name|request
operator|.
name|listenerThreaded
argument_list|(
name|listenerThreaded
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a new search source builder with a search query.      *      * @see org.elasticsearch.index.query.xcontent.QueryBuilders      */
DECL|method|setQuery
specifier|public
name|SearchRequestBuilder
name|setQuery
parameter_list|(
name|XContentQueryBuilder
name|queryBuilder
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|queryBuilder
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a new search source builder with a raw search query.      */
DECL|method|setQuery
specifier|public
name|SearchRequestBuilder
name|setQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Constructs a new search source builder with a raw search query.      */
DECL|method|setQuery
specifier|public
name|SearchRequestBuilder
name|setQuery
parameter_list|(
name|byte
index|[]
name|queryBinary
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|query
argument_list|(
name|queryBinary
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * From index to start the search from. Defaults to<tt>0</tt>.      */
DECL|method|setFrom
specifier|public
name|SearchRequestBuilder
name|setFrom
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|from
argument_list|(
name|from
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The number of search hits to return. Defaults to<tt>10</tt>.      */
DECL|method|setSize
specifier|public
name|SearchRequestBuilder
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|size
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * An optional query parser name to use.      */
DECL|method|setQueryParserName
specifier|public
name|SearchRequestBuilder
name|setQueryParserName
parameter_list|(
name|String
name|queryParserName
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|queryParserName
argument_list|(
name|queryParserName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should each {@link org.elasticsearch.search.SearchHit} be returned with an      * explanation of the hit (ranking).      */
DECL|method|setExplain
specifier|public
name|SearchRequestBuilder
name|setExplain
parameter_list|(
name|boolean
name|explain
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|explain
argument_list|(
name|explain
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the boost a specific index will receive when the query is executeed against it.      *      * @param index      The index to apply the boost against      * @param indexBoost The boost to apply to the index      */
DECL|method|addIndexBoost
specifier|public
name|SearchRequestBuilder
name|addIndexBoost
parameter_list|(
name|String
name|index
parameter_list|,
name|float
name|indexBoost
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|indexBoost
argument_list|(
name|index
argument_list|,
name|indexBoost
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to load and return (note, it must be stored) as part of the search request.      * If none are specified, the source of the document will be return.      */
DECL|method|addField
specifier|public
name|SearchRequestBuilder
name|addField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|field
argument_list|(
name|field
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a script based field to load and return. The field does not have to be stored,      * but its recommended to use non analyzed or numeric fields.      *      * @param name   The name that will represent this value in the return hit      * @param script The script to use      */
DECL|method|addScriptField
specifier|public
name|SearchRequestBuilder
name|addScriptField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|script
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|scriptField
argument_list|(
name|name
argument_list|,
name|script
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a script based field to load and return. The field does not have to be stored,      * but its recommended to use non analyzed or numeric fields.      *      * @param name   The name that will represent this value in the return hit      * @param script The script to use      * @param params Parameters that the script can use.      */
DECL|method|addScriptField
specifier|public
name|SearchRequestBuilder
name|addScriptField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|scriptField
argument_list|(
name|name
argument_list|,
name|script
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a script based field to load and return. The field does not have to be stored,      * but its recommended to use non analyzed or numeric fields.      *      * @param name   The name that will represent this value in the return hit      * @param lang   The language of the script      * @param script The script to use      * @param params Parameters that the script can use (can be<tt>null</tt>).      */
DECL|method|addScriptField
specifier|public
name|SearchRequestBuilder
name|addScriptField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|lang
parameter_list|,
name|String
name|script
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|scriptField
argument_list|(
name|name
argument_list|,
name|lang
argument_list|,
name|script
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a sort against the given field name and the sort ordering.      *      * @param field The name of the field      * @param order The sort ordering      */
DECL|method|addSort
specifier|public
name|SearchRequestBuilder
name|addSort
parameter_list|(
name|String
name|field
parameter_list|,
name|SortOrder
name|order
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|sort
argument_list|(
name|field
argument_list|,
name|order
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a generic sort builder.      *      * @see org.elasticsearch.search.sort.SortBuilders      */
DECL|method|addSort
specifier|public
name|SearchRequestBuilder
name|addSort
parameter_list|(
name|SortBuilder
name|sort
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|sort
argument_list|(
name|sort
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds the fields to load and return as part of the search request. If none are specified,      * the source of the document will be returned.      */
DECL|method|addFields
specifier|public
name|SearchRequestBuilder
name|addFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a facet to the search operation.      */
DECL|method|addFacet
specifier|public
name|SearchRequestBuilder
name|addFacet
parameter_list|(
name|AbstractFacetBuilder
name|facet
parameter_list|)
block|{
name|sourceBuilder
argument_list|()
operator|.
name|facet
argument_list|(
name|facet
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to be highlighted with default fragment size of 100 characters, and      * default number of fragments of 5.      *      * @param name The field to highlight      */
DECL|method|addHighlightedField
specifier|public
name|SearchRequestBuilder
name|addHighlightedField
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to be highlighted with a provided fragment size (in characters), and      * default number of fragments of 5.      *      * @param name         The field to highlight      * @param fragmentSize The size of a fragment in characters      */
DECL|method|addHighlightedField
specifier|public
name|SearchRequestBuilder
name|addHighlightedField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fragmentSize
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|fragmentSize
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Adds a field to be highlighted with a provided fragment size (in characters), and      * a provided (maximum) number of fragments.      *      * @param name              The field to highlight      * @param fragmentSize      The size of a fragment in characters      * @param numberOfFragments The (maximum) number of fragments      */
DECL|method|addHighlightedField
specifier|public
name|SearchRequestBuilder
name|addHighlightedField
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|fragmentSize
parameter_list|,
name|int
name|numberOfFragments
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|fragmentSize
argument_list|,
name|numberOfFragments
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set a tag scheme that encapsulates a built in pre and post tags. The allows schemes      * are<tt>styled</tt> and<tt>default</tt>.      *      * @param schemaName The tag scheme name      */
DECL|method|setHighlighterTagsSchema
specifier|public
name|SearchRequestBuilder
name|setHighlighterTagsSchema
parameter_list|(
name|String
name|schemaName
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|tagsSchema
argument_list|(
name|schemaName
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the pre tags that will be used for highlighting.      */
DECL|method|setHighlighterPreTags
specifier|public
name|SearchRequestBuilder
name|setHighlighterPreTags
parameter_list|(
name|String
modifier|...
name|preTags
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|preTags
argument_list|(
name|preTags
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Explicitly set the post tags that will be used for highlighting.      */
DECL|method|setHighlighterPostTags
specifier|public
name|SearchRequestBuilder
name|setHighlighterPostTags
parameter_list|(
name|String
modifier|...
name|postTags
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|postTags
argument_list|(
name|postTags
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The order of fragments per field. By default, ordered by the order in the      * highlighted text. Can be<tt>score</tt>, which then it will be ordered      * by score of the fragments.      */
DECL|method|setHighlighterOrder
specifier|public
name|SearchRequestBuilder
name|setHighlighterOrder
parameter_list|(
name|String
name|order
parameter_list|)
block|{
name|highlightBuilder
argument_list|()
operator|.
name|order
argument_list|(
name|order
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|internalBuilder
specifier|public
name|SearchSourceBuilder
name|internalBuilder
parameter_list|()
block|{
return|return
name|sourceBuilder
argument_list|()
return|;
block|}
DECL|method|doExecute
annotation|@
name|Override
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|SearchResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|request
operator|.
name|source
argument_list|(
name|sourceBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|.
name|search
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|sourceBuilder
specifier|private
name|SearchSourceBuilder
name|sourceBuilder
parameter_list|()
block|{
if|if
condition|(
name|sourceBuilder
operator|==
literal|null
condition|)
block|{
name|sourceBuilder
operator|=
operator|new
name|SearchSourceBuilder
argument_list|()
expr_stmt|;
block|}
return|return
name|sourceBuilder
return|;
block|}
DECL|method|highlightBuilder
specifier|private
name|HighlightBuilder
name|highlightBuilder
parameter_list|()
block|{
return|return
name|sourceBuilder
argument_list|()
operator|.
name|highlighter
argument_list|()
return|;
block|}
block|}
end_class

end_unit

