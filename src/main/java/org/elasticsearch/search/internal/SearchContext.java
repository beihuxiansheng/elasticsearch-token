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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|MultimapBuilder
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
name|ScoreDoc
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
name|common
operator|.
name|lease
operator|.
name|Releasables
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
name|util
operator|.
name|BigArrays
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
name|cache
operator|.
name|fixedbitset
operator|.
name|FixedBitSetFilterCache
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_TERMINATE_AFTER
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_TERMINATE_AFTER
init|=
literal|0
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
DECL|field|clearables
specifier|private
name|Multimap
argument_list|<
name|Lifetime
argument_list|,
name|Releasable
argument_list|>
name|clearables
init|=
literal|null
decl_stmt|;
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|clearReleasables
argument_list|(
name|Lifetime
operator|.
name|CONTEXT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|doClose
argument_list|()
expr_stmt|;
block|}
block|}
DECL|field|nowInMillisUsed
specifier|private
name|boolean
name|nowInMillisUsed
decl_stmt|;
DECL|method|doClose
specifier|protected
specifier|abstract
name|void
name|doClose
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
specifier|final
name|long
name|nowInMillis
parameter_list|()
block|{
name|nowInMillisUsed
operator|=
literal|true
expr_stmt|;
return|return
name|nowInMillisImpl
argument_list|()
return|;
block|}
DECL|method|nowInMillisUsed
specifier|public
specifier|final
name|boolean
name|nowInMillisUsed
parameter_list|()
block|{
return|return
name|nowInMillisUsed
return|;
block|}
DECL|method|nowInMillisImpl
specifier|protected
specifier|abstract
name|long
name|nowInMillisImpl
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
DECL|method|pageCacheRecycler
specifier|public
specifier|abstract
name|PageCacheRecycler
name|pageCacheRecycler
parameter_list|()
function_decl|;
DECL|method|bigArrays
specifier|public
specifier|abstract
name|BigArrays
name|bigArrays
parameter_list|()
function_decl|;
DECL|method|filterCache
specifier|public
specifier|abstract
name|FilterCache
name|filterCache
parameter_list|()
function_decl|;
DECL|method|fixedBitSetFilterCache
specifier|public
specifier|abstract
name|FixedBitSetFilterCache
name|fixedBitSetFilterCache
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
DECL|method|terminateAfter
specifier|public
specifier|abstract
name|int
name|terminateAfter
parameter_list|()
function_decl|;
DECL|method|terminateAfter
specifier|public
specifier|abstract
name|void
name|terminateAfter
parameter_list|(
name|int
name|terminateAfter
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
DECL|method|lastEmittedDoc
specifier|public
specifier|abstract
name|void
name|lastEmittedDoc
parameter_list|(
name|ScoreDoc
name|doc
parameter_list|)
function_decl|;
DECL|method|lastEmittedDoc
specifier|public
specifier|abstract
name|ScoreDoc
name|lastEmittedDoc
parameter_list|()
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
comment|/**      * Schedule the release of a resource. The time when {@link Releasable#release()} will be called on this object      * is function of the provided {@link Lifetime}.      */
DECL|method|addReleasable
specifier|public
name|void
name|addReleasable
parameter_list|(
name|Releasable
name|releasable
parameter_list|,
name|Lifetime
name|lifetime
parameter_list|)
block|{
if|if
condition|(
name|clearables
operator|==
literal|null
condition|)
block|{
name|clearables
operator|=
name|MultimapBuilder
operator|.
name|enumKeys
argument_list|(
name|Lifetime
operator|.
name|class
argument_list|)
operator|.
name|arrayListValues
argument_list|()
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
name|clearables
operator|.
name|put
argument_list|(
name|lifetime
argument_list|,
name|releasable
argument_list|)
expr_stmt|;
block|}
DECL|method|clearReleasables
specifier|public
name|void
name|clearReleasables
parameter_list|(
name|Lifetime
name|lifetime
parameter_list|)
block|{
if|if
condition|(
name|clearables
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Collection
argument_list|<
name|Releasable
argument_list|>
argument_list|>
name|releasables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Lifetime
name|lc
range|:
name|Lifetime
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|lc
operator|.
name|compareTo
argument_list|(
name|lifetime
argument_list|)
operator|>
literal|0
condition|)
block|{
break|break;
block|}
name|releasables
operator|.
name|add
argument_list|(
name|clearables
operator|.
name|removeAll
argument_list|(
name|lc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Releasables
operator|.
name|close
argument_list|(
name|Iterables
operator|.
name|concat
argument_list|(
name|releasables
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|method|useSlowScroll
specifier|public
specifier|abstract
name|boolean
name|useSlowScroll
parameter_list|()
function_decl|;
DECL|method|useSlowScroll
specifier|public
specifier|abstract
name|SearchContext
name|useSlowScroll
parameter_list|(
name|boolean
name|useSlowScroll
parameter_list|)
function_decl|;
comment|/**      * The life time of an object that is used during search execution.      */
DECL|enum|Lifetime
specifier|public
enum|enum
name|Lifetime
block|{
comment|/**          * This life time is for objects that only live during collection time.          */
DECL|enum constant|COLLECTION
name|COLLECTION
block|,
comment|/**          * This life time is for objects that need to live until the end of the current search phase.          */
DECL|enum constant|PHASE
name|PHASE
block|,
comment|/**          * This life time is for objects that need to live until the search context they are attached to is destroyed.          */
DECL|enum constant|CONTEXT
name|CONTEXT
block|;     }
block|}
end_class

end_unit

