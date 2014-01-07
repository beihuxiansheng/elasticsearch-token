begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Filter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Sort
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
name|cache
operator|.
name|recycler
operator|.
name|CacheRecycler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
operator|.
name|PageCacheRecycler
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
name|lease
operator|.
name|Releasable
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
name|analysis
operator|.
name|AnalysisService
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
name|cache
operator|.
name|docset
operator|.
name|DocSetCache
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
name|cache
operator|.
name|filter
operator|.
name|FilterCache
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|mapper
operator|.
name|FieldMapper
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
name|mapper
operator|.
name|FieldMappers
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
name|mapper
operator|.
name|MapperService
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
name|IndexQueryParserService
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
name|ParsedFilter
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
name|ParsedQuery
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
name|QueryParseContext
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
name|shard
operator|.
name|service
operator|.
name|IndexShard
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
name|similarity
operator|.
name|SimilarityService
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
name|ScriptService
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
name|SearchShardTarget
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
name|aggregations
operator|.
name|SearchContextAggregations
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
name|dfs
operator|.
name|DfsSearchResult
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
name|facet
operator|.
name|SearchContextFacets
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
name|FetchSearchResult
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
name|fielddata
operator|.
name|FieldDataFieldsContext
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
name|partial
operator|.
name|PartialFieldsContext
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
name|script
operator|.
name|ScriptFieldsContext
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
operator|.
name|SearchContextHighlight
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
name|lookup
operator|.
name|SearchLookup
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
name|query
operator|.
name|QuerySearchResult
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
name|rescore
operator|.
name|RescoreSearchContext
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
name|scan
operator|.
name|ScanContext
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
name|suggest
operator|.
name|SuggestionSearchContext
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SearchContext
specifier|public
specifier|abstract
class|class
name|SearchContext
implements|implements
name|Releasable
block|{
DECL|field|current
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|SearchContext
argument_list|>
name|current
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SearchContext
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|setCurrent
specifier|public
specifier|static
name|void
name|setCurrent
parameter_list|(
name|SearchContext
name|value
parameter_list|)
block|{
name|current
operator|.
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|QueryParseContext
operator|.
name|setTypes
argument_list|(
name|value
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|removeCurrent
specifier|public
specifier|static
name|void
name|removeCurrent
parameter_list|()
block|{
name|current
operator|.
name|remove
argument_list|()
expr_stmt|;
name|QueryParseContext
operator|.
name|removeTypes
argument_list|()
expr_stmt|;
block|}
DECL|method|current
specifier|public
specifier|static
name|SearchContext
name|current
parameter_list|()
block|{
return|return
name|current
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|clearAndRelease
specifier|public
specifier|abstract
name|boolean
name|clearAndRelease
parameter_list|()
function_decl|;
comment|/**      * Should be called before executing the main query and after all other parameters have been set.      */
DECL|method|preProcess
specifier|public
specifier|abstract
name|void
name|preProcess
parameter_list|()
function_decl|;
DECL|method|searchFilter
specifier|public
specifier|abstract
name|Filter
name|searchFilter
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
function_decl|;
DECL|method|id
specifier|public
specifier|abstract
name|long
name|id
parameter_list|()
function_decl|;
DECL|method|source
specifier|public
specifier|abstract
name|String
name|source
parameter_list|()
function_decl|;
DECL|method|request
specifier|public
specifier|abstract
name|ShardSearchRequest
name|request
parameter_list|()
function_decl|;
DECL|method|searchType
specifier|public
specifier|abstract
name|SearchType
name|searchType
parameter_list|()
function_decl|;
DECL|method|searchType
specifier|public
specifier|abstract
name|SearchContext
name|searchType
parameter_list|(
name|SearchType
name|searchType
parameter_list|)
function_decl|;
DECL|method|shardTarget
specifier|public
specifier|abstract
name|SearchShardTarget
name|shardTarget
parameter_list|()
function_decl|;
DECL|method|numberOfShards
specifier|public
specifier|abstract
name|int
name|numberOfShards
parameter_list|()
function_decl|;
DECL|method|hasTypes
specifier|public
specifier|abstract
name|boolean
name|hasTypes
parameter_list|()
function_decl|;
DECL|method|types
specifier|public
specifier|abstract
name|String
index|[]
name|types
parameter_list|()
function_decl|;
DECL|method|queryBoost
specifier|public
specifier|abstract
name|float
name|queryBoost
parameter_list|()
function_decl|;
DECL|method|queryBoost
specifier|public
specifier|abstract
name|SearchContext
name|queryBoost
parameter_list|(
name|float
name|queryBoost
parameter_list|)
function_decl|;
DECL|method|nowInMillis
specifier|public
specifier|abstract
name|long
name|nowInMillis
parameter_list|()
function_decl|;
DECL|method|scroll
specifier|public
specifier|abstract
name|Scroll
name|scroll
parameter_list|()
function_decl|;
DECL|method|scroll
specifier|public
specifier|abstract
name|SearchContext
name|scroll
parameter_list|(
name|Scroll
name|scroll
parameter_list|)
function_decl|;
DECL|method|aggregations
specifier|public
specifier|abstract
name|SearchContextAggregations
name|aggregations
parameter_list|()
function_decl|;
DECL|method|aggregations
specifier|public
specifier|abstract
name|SearchContext
name|aggregations
parameter_list|(
name|SearchContextAggregations
name|aggregations
parameter_list|)
function_decl|;
DECL|method|facets
specifier|public
specifier|abstract
name|SearchContextFacets
name|facets
parameter_list|()
function_decl|;
DECL|method|facets
specifier|public
specifier|abstract
name|SearchContext
name|facets
parameter_list|(
name|SearchContextFacets
name|facets
parameter_list|)
function_decl|;
DECL|method|highlight
specifier|public
specifier|abstract
name|SearchContextHighlight
name|highlight
parameter_list|()
function_decl|;
DECL|method|highlight
specifier|public
specifier|abstract
name|void
name|highlight
parameter_list|(
name|SearchContextHighlight
name|highlight
parameter_list|)
function_decl|;
DECL|method|suggest
specifier|public
specifier|abstract
name|SuggestionSearchContext
name|suggest
parameter_list|()
function_decl|;
DECL|method|suggest
specifier|public
specifier|abstract
name|void
name|suggest
parameter_list|(
name|SuggestionSearchContext
name|suggest
parameter_list|)
function_decl|;
comment|/**      * @return list of all rescore contexts.  empty if there aren't any.      */
DECL|method|rescore
specifier|public
specifier|abstract
name|List
argument_list|<
name|RescoreSearchContext
argument_list|>
name|rescore
parameter_list|()
function_decl|;
DECL|method|addRescore
specifier|public
specifier|abstract
name|void
name|addRescore
parameter_list|(
name|RescoreSearchContext
name|rescore
parameter_list|)
function_decl|;
DECL|method|hasFieldDataFields
specifier|public
specifier|abstract
name|boolean
name|hasFieldDataFields
parameter_list|()
function_decl|;
DECL|method|fieldDataFields
specifier|public
specifier|abstract
name|FieldDataFieldsContext
name|fieldDataFields
parameter_list|()
function_decl|;
DECL|method|hasScriptFields
specifier|public
specifier|abstract
name|boolean
name|hasScriptFields
parameter_list|()
function_decl|;
DECL|method|scriptFields
specifier|public
specifier|abstract
name|ScriptFieldsContext
name|scriptFields
parameter_list|()
function_decl|;
DECL|method|hasPartialFields
specifier|public
specifier|abstract
name|boolean
name|hasPartialFields
parameter_list|()
function_decl|;
DECL|method|partialFields
specifier|public
specifier|abstract
name|PartialFieldsContext
name|partialFields
parameter_list|()
function_decl|;
comment|/**      * A shortcut function to see whether there is a fetchSourceContext and it says the source is requested.      *      * @return      */
DECL|method|sourceRequested
specifier|public
specifier|abstract
name|boolean
name|sourceRequested
parameter_list|()
function_decl|;
DECL|method|hasFetchSourceContext
specifier|public
specifier|abstract
name|boolean
name|hasFetchSourceContext
parameter_list|()
function_decl|;
DECL|method|fetchSourceContext
specifier|public
specifier|abstract
name|FetchSourceContext
name|fetchSourceContext
parameter_list|()
function_decl|;
DECL|method|fetchSourceContext
specifier|public
specifier|abstract
name|SearchContext
name|fetchSourceContext
parameter_list|(
name|FetchSourceContext
name|fetchSourceContext
parameter_list|)
function_decl|;
DECL|method|searcher
specifier|public
specifier|abstract
name|ContextIndexSearcher
name|searcher
parameter_list|()
function_decl|;
DECL|method|indexShard
specifier|public
specifier|abstract
name|IndexShard
name|indexShard
parameter_list|()
function_decl|;
DECL|method|mapperService
specifier|public
specifier|abstract
name|MapperService
name|mapperService
parameter_list|()
function_decl|;
DECL|method|analysisService
specifier|public
specifier|abstract
name|AnalysisService
name|analysisService
parameter_list|()
function_decl|;
DECL|method|queryParserService
specifier|public
specifier|abstract
name|IndexQueryParserService
name|queryParserService
parameter_list|()
function_decl|;
DECL|method|similarityService
specifier|public
specifier|abstract
name|SimilarityService
name|similarityService
parameter_list|()
function_decl|;
DECL|method|scriptService
specifier|public
specifier|abstract
name|ScriptService
name|scriptService
parameter_list|()
function_decl|;
DECL|method|cacheRecycler
specifier|public
specifier|abstract
name|CacheRecycler
name|cacheRecycler
parameter_list|()
function_decl|;
DECL|method|pageCacheRecycler
specifier|public
specifier|abstract
name|PageCacheRecycler
name|pageCacheRecycler
parameter_list|()
function_decl|;
DECL|method|filterCache
specifier|public
specifier|abstract
name|FilterCache
name|filterCache
parameter_list|()
function_decl|;
DECL|method|docSetCache
specifier|public
specifier|abstract
name|DocSetCache
name|docSetCache
parameter_list|()
function_decl|;
DECL|method|fieldData
specifier|public
specifier|abstract
name|IndexFieldDataService
name|fieldData
parameter_list|()
function_decl|;
DECL|method|timeoutInMillis
specifier|public
specifier|abstract
name|long
name|timeoutInMillis
parameter_list|()
function_decl|;
DECL|method|timeoutInMillis
specifier|public
specifier|abstract
name|void
name|timeoutInMillis
parameter_list|(
name|long
name|timeoutInMillis
parameter_list|)
function_decl|;
DECL|method|minimumScore
specifier|public
specifier|abstract
name|SearchContext
name|minimumScore
parameter_list|(
name|float
name|minimumScore
parameter_list|)
function_decl|;
DECL|method|minimumScore
specifier|public
specifier|abstract
name|Float
name|minimumScore
parameter_list|()
function_decl|;
DECL|method|sort
specifier|public
specifier|abstract
name|SearchContext
name|sort
parameter_list|(
name|Sort
name|sort
parameter_list|)
function_decl|;
DECL|method|sort
specifier|public
specifier|abstract
name|Sort
name|sort
parameter_list|()
function_decl|;
DECL|method|trackScores
specifier|public
specifier|abstract
name|SearchContext
name|trackScores
parameter_list|(
name|boolean
name|trackScores
parameter_list|)
function_decl|;
DECL|method|trackScores
specifier|public
specifier|abstract
name|boolean
name|trackScores
parameter_list|()
function_decl|;
DECL|method|parsedPostFilter
specifier|public
specifier|abstract
name|SearchContext
name|parsedPostFilter
parameter_list|(
name|ParsedFilter
name|postFilter
parameter_list|)
function_decl|;
DECL|method|parsedPostFilter
specifier|public
specifier|abstract
name|ParsedFilter
name|parsedPostFilter
parameter_list|()
function_decl|;
DECL|method|aliasFilter
specifier|public
specifier|abstract
name|Filter
name|aliasFilter
parameter_list|()
function_decl|;
DECL|method|parsedQuery
specifier|public
specifier|abstract
name|SearchContext
name|parsedQuery
parameter_list|(
name|ParsedQuery
name|query
parameter_list|)
function_decl|;
DECL|method|parsedQuery
specifier|public
specifier|abstract
name|ParsedQuery
name|parsedQuery
parameter_list|()
function_decl|;
comment|/**      * The query to execute, might be rewritten.      */
DECL|method|query
specifier|public
specifier|abstract
name|Query
name|query
parameter_list|()
function_decl|;
comment|/**      * Has the query been rewritten already?      */
DECL|method|queryRewritten
specifier|public
specifier|abstract
name|boolean
name|queryRewritten
parameter_list|()
function_decl|;
comment|/**      * Rewrites the query and updates it. Only happens once.      */
DECL|method|updateRewriteQuery
specifier|public
specifier|abstract
name|SearchContext
name|updateRewriteQuery
parameter_list|(
name|Query
name|rewriteQuery
parameter_list|)
function_decl|;
DECL|method|from
specifier|public
specifier|abstract
name|int
name|from
parameter_list|()
function_decl|;
DECL|method|from
specifier|public
specifier|abstract
name|SearchContext
name|from
parameter_list|(
name|int
name|from
parameter_list|)
function_decl|;
DECL|method|size
specifier|public
specifier|abstract
name|int
name|size
parameter_list|()
function_decl|;
DECL|method|size
specifier|public
specifier|abstract
name|SearchContext
name|size
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
DECL|method|hasFieldNames
specifier|public
specifier|abstract
name|boolean
name|hasFieldNames
parameter_list|()
function_decl|;
DECL|method|fieldNames
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|()
function_decl|;
DECL|method|emptyFieldNames
specifier|public
specifier|abstract
name|void
name|emptyFieldNames
parameter_list|()
function_decl|;
DECL|method|explain
specifier|public
specifier|abstract
name|boolean
name|explain
parameter_list|()
function_decl|;
DECL|method|explain
specifier|public
specifier|abstract
name|void
name|explain
parameter_list|(
name|boolean
name|explain
parameter_list|)
function_decl|;
annotation|@
name|Nullable
DECL|method|groupStats
specifier|public
specifier|abstract
name|List
argument_list|<
name|String
argument_list|>
name|groupStats
parameter_list|()
function_decl|;
DECL|method|groupStats
specifier|public
specifier|abstract
name|void
name|groupStats
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groupStats
parameter_list|)
function_decl|;
DECL|method|version
specifier|public
specifier|abstract
name|boolean
name|version
parameter_list|()
function_decl|;
DECL|method|version
specifier|public
specifier|abstract
name|void
name|version
parameter_list|(
name|boolean
name|version
parameter_list|)
function_decl|;
DECL|method|docIdsToLoad
specifier|public
specifier|abstract
name|int
index|[]
name|docIdsToLoad
parameter_list|()
function_decl|;
DECL|method|docIdsToLoadFrom
specifier|public
specifier|abstract
name|int
name|docIdsToLoadFrom
parameter_list|()
function_decl|;
DECL|method|docIdsToLoadSize
specifier|public
specifier|abstract
name|int
name|docIdsToLoadSize
parameter_list|()
function_decl|;
DECL|method|docIdsToLoad
specifier|public
specifier|abstract
name|SearchContext
name|docIdsToLoad
parameter_list|(
name|int
index|[]
name|docIdsToLoad
parameter_list|,
name|int
name|docsIdsToLoadFrom
parameter_list|,
name|int
name|docsIdsToLoadSize
parameter_list|)
function_decl|;
DECL|method|accessed
specifier|public
specifier|abstract
name|void
name|accessed
parameter_list|(
name|long
name|accessTime
parameter_list|)
function_decl|;
DECL|method|lastAccessTime
specifier|public
specifier|abstract
name|long
name|lastAccessTime
parameter_list|()
function_decl|;
DECL|method|keepAlive
specifier|public
specifier|abstract
name|long
name|keepAlive
parameter_list|()
function_decl|;
DECL|method|keepAlive
specifier|public
specifier|abstract
name|void
name|keepAlive
parameter_list|(
name|long
name|keepAlive
parameter_list|)
function_decl|;
DECL|method|lookup
specifier|public
specifier|abstract
name|SearchLookup
name|lookup
parameter_list|()
function_decl|;
DECL|method|dfsResult
specifier|public
specifier|abstract
name|DfsSearchResult
name|dfsResult
parameter_list|()
function_decl|;
DECL|method|queryResult
specifier|public
specifier|abstract
name|QuerySearchResult
name|queryResult
parameter_list|()
function_decl|;
DECL|method|fetchResult
specifier|public
specifier|abstract
name|FetchSearchResult
name|fetchResult
parameter_list|()
function_decl|;
DECL|method|addReleasable
specifier|public
specifier|abstract
name|void
name|addReleasable
parameter_list|(
name|Releasable
name|releasable
parameter_list|)
function_decl|;
DECL|method|clearReleasables
specifier|public
specifier|abstract
name|void
name|clearReleasables
parameter_list|()
function_decl|;
DECL|method|scanContext
specifier|public
specifier|abstract
name|ScanContext
name|scanContext
parameter_list|()
function_decl|;
DECL|method|smartFieldMappers
specifier|public
specifier|abstract
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartFieldMappers
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|smartNameFieldMappers
specifier|public
specifier|abstract
name|FieldMappers
name|smartNameFieldMappers
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|smartNameFieldMapper
specifier|public
specifier|abstract
name|FieldMapper
name|smartNameFieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
DECL|method|smartNameObjectMapper
specifier|public
specifier|abstract
name|MapperService
operator|.
name|SmartNameObjectMapper
name|smartNameObjectMapper
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_class

end_unit

