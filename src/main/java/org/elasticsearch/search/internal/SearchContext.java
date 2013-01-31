begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ImmutableList
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
name|Lists
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
name|Sort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|lucene
operator|.
name|search
operator|.
name|Queries
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
name|lucene
operator|.
name|search
operator|.
name|XConstantScoreQuery
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
name|lucene
operator|.
name|search
operator|.
name|XFilteredQuery
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|BoostScoreFunction
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
name|lucene
operator|.
name|search
operator|.
name|function
operator|.
name|FunctionScoreQuery
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
name|id
operator|.
name|IdCache
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
name|engine
operator|.
name|Engine
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
name|service
operator|.
name|IndexService
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
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SearchContext
specifier|public
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
DECL|interface|Rewrite
specifier|public
specifier|static
interface|interface
name|Rewrite
block|{
DECL|method|contextRewrite
name|void
name|contextRewrite
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|contextClear
name|void
name|contextClear
parameter_list|()
function_decl|;
block|}
DECL|field|id
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|ShardSearchRequest
name|request
decl_stmt|;
DECL|field|shardTarget
specifier|private
specifier|final
name|SearchShardTarget
name|shardTarget
decl_stmt|;
DECL|field|searchType
specifier|private
name|SearchType
name|searchType
decl_stmt|;
DECL|field|engineSearcher
specifier|private
specifier|final
name|Engine
operator|.
name|Searcher
name|engineSearcher
decl_stmt|;
DECL|field|scriptService
specifier|private
specifier|final
name|ScriptService
name|scriptService
decl_stmt|;
DECL|field|indexShard
specifier|private
specifier|final
name|IndexShard
name|indexShard
decl_stmt|;
DECL|field|indexService
specifier|private
specifier|final
name|IndexService
name|indexService
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|final
name|ContextIndexSearcher
name|searcher
decl_stmt|;
DECL|field|dfsResult
specifier|private
specifier|final
name|DfsSearchResult
name|dfsResult
decl_stmt|;
DECL|field|queryResult
specifier|private
specifier|final
name|QuerySearchResult
name|queryResult
decl_stmt|;
DECL|field|fetchResult
specifier|private
specifier|final
name|FetchSearchResult
name|fetchResult
decl_stmt|;
comment|// lazy initialized only if needed
DECL|field|scanContext
specifier|private
name|ScanContext
name|scanContext
decl_stmt|;
DECL|field|queryBoost
specifier|private
name|float
name|queryBoost
init|=
literal|1.0f
decl_stmt|;
comment|// timeout in millis
DECL|field|timeoutInMillis
specifier|private
name|long
name|timeoutInMillis
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|groupStats
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|groupStats
decl_stmt|;
DECL|field|scroll
specifier|private
name|Scroll
name|scroll
decl_stmt|;
DECL|field|explain
specifier|private
name|boolean
name|explain
decl_stmt|;
DECL|field|version
specifier|private
name|boolean
name|version
init|=
literal|false
decl_stmt|;
comment|// by default, we don't return versions
DECL|field|fieldNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
DECL|field|scriptFields
specifier|private
name|ScriptFieldsContext
name|scriptFields
decl_stmt|;
DECL|field|partialFields
specifier|private
name|PartialFieldsContext
name|partialFields
decl_stmt|;
DECL|field|from
specifier|private
name|int
name|from
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|field|minimumScore
specifier|private
name|Float
name|minimumScore
decl_stmt|;
DECL|field|trackScores
specifier|private
name|boolean
name|trackScores
init|=
literal|false
decl_stmt|;
comment|// when sorting, track scores as well...
DECL|field|originalQuery
specifier|private
name|ParsedQuery
name|originalQuery
decl_stmt|;
DECL|field|query
specifier|private
name|Query
name|query
decl_stmt|;
DECL|field|filter
specifier|private
name|Filter
name|filter
decl_stmt|;
DECL|field|aliasFilter
specifier|private
name|Filter
name|aliasFilter
decl_stmt|;
DECL|field|docIdsToLoad
specifier|private
name|int
index|[]
name|docIdsToLoad
decl_stmt|;
DECL|field|docsIdsToLoadFrom
specifier|private
name|int
name|docsIdsToLoadFrom
decl_stmt|;
DECL|field|docsIdsToLoadSize
specifier|private
name|int
name|docsIdsToLoadSize
decl_stmt|;
DECL|field|facets
specifier|private
name|SearchContextFacets
name|facets
decl_stmt|;
DECL|field|highlight
specifier|private
name|SearchContextHighlight
name|highlight
decl_stmt|;
DECL|field|suggest
specifier|private
name|SuggestionSearchContext
name|suggest
decl_stmt|;
DECL|field|searchLookup
specifier|private
name|SearchLookup
name|searchLookup
decl_stmt|;
DECL|field|queryRewritten
specifier|private
name|boolean
name|queryRewritten
decl_stmt|;
DECL|field|keepAlive
specifier|private
specifier|volatile
name|long
name|keepAlive
decl_stmt|;
DECL|field|lastAccessTime
specifier|private
specifier|volatile
name|long
name|lastAccessTime
decl_stmt|;
DECL|field|rewrites
specifier|private
name|List
argument_list|<
name|Rewrite
argument_list|>
name|rewrites
init|=
literal|null
decl_stmt|;
DECL|method|SearchContext
specifier|public
name|SearchContext
parameter_list|(
name|long
name|id
parameter_list|,
name|ShardSearchRequest
name|request
parameter_list|,
name|SearchShardTarget
name|shardTarget
parameter_list|,
name|Engine
operator|.
name|Searcher
name|engineSearcher
parameter_list|,
name|IndexService
name|indexService
parameter_list|,
name|IndexShard
name|indexShard
parameter_list|,
name|ScriptService
name|scriptService
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|searchType
operator|=
name|request
operator|.
name|searchType
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardTarget
operator|=
name|shardTarget
expr_stmt|;
name|this
operator|.
name|engineSearcher
operator|=
name|engineSearcher
expr_stmt|;
name|this
operator|.
name|scriptService
operator|=
name|scriptService
expr_stmt|;
name|this
operator|.
name|dfsResult
operator|=
operator|new
name|DfsSearchResult
argument_list|(
name|id
argument_list|,
name|shardTarget
argument_list|)
expr_stmt|;
name|this
operator|.
name|queryResult
operator|=
operator|new
name|QuerySearchResult
argument_list|(
name|id
argument_list|,
name|shardTarget
argument_list|)
expr_stmt|;
name|this
operator|.
name|fetchResult
operator|=
operator|new
name|FetchSearchResult
argument_list|(
name|id
argument_list|,
name|shardTarget
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexShard
operator|=
name|indexShard
expr_stmt|;
name|this
operator|.
name|indexService
operator|=
name|indexService
expr_stmt|;
name|this
operator|.
name|searcher
operator|=
operator|new
name|ContextIndexSearcher
argument_list|(
name|this
argument_list|,
name|engineSearcher
argument_list|)
expr_stmt|;
comment|// initialize the filtering alias based on the provided filters
name|aliasFilter
operator|=
name|indexService
operator|.
name|aliasesService
argument_list|()
operator|.
name|aliasFilter
argument_list|(
name|request
operator|.
name|filteringAliases
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|release
specifier|public
name|boolean
name|release
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|scanContext
operator|!=
literal|null
condition|)
block|{
name|scanContext
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// clear and scope phase we  have
if|if
condition|(
name|rewrites
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Rewrite
name|rewrite
range|:
name|rewrites
control|)
block|{
name|rewrite
operator|.
name|contextClear
argument_list|()
expr_stmt|;
block|}
block|}
name|engineSearcher
operator|.
name|release
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Should be called before executing the main query and after all other parameters have been set.      */
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|()
block|{
if|if
condition|(
name|query
argument_list|()
operator|==
literal|null
condition|)
block|{
name|parsedQuery
argument_list|(
name|ParsedQuery
operator|.
name|MATCH_ALL_PARSED_QUERY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBoost
argument_list|()
operator|!=
literal|1.0f
condition|)
block|{
name|parsedQuery
argument_list|(
operator|new
name|ParsedQuery
argument_list|(
operator|new
name|FunctionScoreQuery
argument_list|(
name|query
argument_list|()
argument_list|,
operator|new
name|BoostScoreFunction
argument_list|(
name|queryBoost
argument_list|)
argument_list|)
argument_list|,
name|parsedQuery
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Filter
name|searchFilter
init|=
name|mapperService
argument_list|()
operator|.
name|searchFilter
argument_list|(
name|types
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|searchFilter
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Queries
operator|.
name|isConstantMatchAllQuery
argument_list|(
name|query
argument_list|()
argument_list|)
condition|)
block|{
name|Query
name|q
init|=
operator|new
name|XConstantScoreQuery
argument_list|(
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|searchFilter
argument_list|)
argument_list|)
decl_stmt|;
name|q
operator|.
name|setBoost
argument_list|(
name|query
argument_list|()
operator|.
name|getBoost
argument_list|()
argument_list|)
expr_stmt|;
name|parsedQuery
argument_list|(
operator|new
name|ParsedQuery
argument_list|(
name|q
argument_list|,
name|parsedQuery
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parsedQuery
argument_list|(
operator|new
name|ParsedQuery
argument_list|(
operator|new
name|XFilteredQuery
argument_list|(
name|query
argument_list|()
argument_list|,
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|searchFilter
argument_list|)
argument_list|)
argument_list|,
name|parsedQuery
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|request
specifier|public
name|ShardSearchRequest
name|request
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
return|;
block|}
DECL|method|searchType
specifier|public
name|SearchType
name|searchType
parameter_list|()
block|{
return|return
name|this
operator|.
name|searchType
return|;
block|}
DECL|method|searchType
specifier|public
name|SearchContext
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
DECL|method|shardTarget
specifier|public
name|SearchShardTarget
name|shardTarget
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardTarget
return|;
block|}
DECL|method|numberOfShards
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|request
operator|.
name|numberOfShards
argument_list|()
return|;
block|}
DECL|method|hasTypes
specifier|public
name|boolean
name|hasTypes
parameter_list|()
block|{
return|return
name|request
operator|.
name|types
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|types
argument_list|()
operator|.
name|length
operator|>
literal|0
return|;
block|}
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|request
operator|.
name|types
argument_list|()
return|;
block|}
DECL|method|queryBoost
specifier|public
name|float
name|queryBoost
parameter_list|()
block|{
return|return
name|queryBoost
return|;
block|}
DECL|method|queryBoost
specifier|public
name|SearchContext
name|queryBoost
parameter_list|(
name|float
name|queryBoost
parameter_list|)
block|{
name|this
operator|.
name|queryBoost
operator|=
name|queryBoost
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|nowInMillis
specifier|public
name|long
name|nowInMillis
parameter_list|()
block|{
return|return
name|request
operator|.
name|nowInMillis
argument_list|()
return|;
block|}
DECL|method|scroll
specifier|public
name|Scroll
name|scroll
parameter_list|()
block|{
return|return
name|this
operator|.
name|scroll
return|;
block|}
DECL|method|scroll
specifier|public
name|SearchContext
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
DECL|method|facets
specifier|public
name|SearchContextFacets
name|facets
parameter_list|()
block|{
return|return
name|facets
return|;
block|}
DECL|method|facets
specifier|public
name|SearchContext
name|facets
parameter_list|(
name|SearchContextFacets
name|facets
parameter_list|)
block|{
name|this
operator|.
name|facets
operator|=
name|facets
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|highlight
specifier|public
name|SearchContextHighlight
name|highlight
parameter_list|()
block|{
return|return
name|highlight
return|;
block|}
DECL|method|highlight
specifier|public
name|void
name|highlight
parameter_list|(
name|SearchContextHighlight
name|highlight
parameter_list|)
block|{
name|this
operator|.
name|highlight
operator|=
name|highlight
expr_stmt|;
block|}
DECL|method|suggest
specifier|public
name|SuggestionSearchContext
name|suggest
parameter_list|()
block|{
return|return
name|suggest
return|;
block|}
DECL|method|suggest
specifier|public
name|void
name|suggest
parameter_list|(
name|SuggestionSearchContext
name|suggest
parameter_list|)
block|{
name|this
operator|.
name|suggest
operator|=
name|suggest
expr_stmt|;
block|}
DECL|method|hasScriptFields
specifier|public
name|boolean
name|hasScriptFields
parameter_list|()
block|{
return|return
name|scriptFields
operator|!=
literal|null
return|;
block|}
DECL|method|scriptFields
specifier|public
name|ScriptFieldsContext
name|scriptFields
parameter_list|()
block|{
if|if
condition|(
name|scriptFields
operator|==
literal|null
condition|)
block|{
name|scriptFields
operator|=
operator|new
name|ScriptFieldsContext
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|scriptFields
return|;
block|}
DECL|method|hasPartialFields
specifier|public
name|boolean
name|hasPartialFields
parameter_list|()
block|{
return|return
name|partialFields
operator|!=
literal|null
return|;
block|}
DECL|method|partialFields
specifier|public
name|PartialFieldsContext
name|partialFields
parameter_list|()
block|{
if|if
condition|(
name|partialFields
operator|==
literal|null
condition|)
block|{
name|partialFields
operator|=
operator|new
name|PartialFieldsContext
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|partialFields
return|;
block|}
DECL|method|searcher
specifier|public
name|ContextIndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|this
operator|.
name|searcher
return|;
block|}
DECL|method|indexShard
specifier|public
name|IndexShard
name|indexShard
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexShard
return|;
block|}
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|mapperService
argument_list|()
return|;
block|}
DECL|method|analysisService
specifier|public
name|AnalysisService
name|analysisService
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|analysisService
argument_list|()
return|;
block|}
DECL|method|queryParserService
specifier|public
name|IndexQueryParserService
name|queryParserService
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|queryParserService
argument_list|()
return|;
block|}
DECL|method|similarityService
specifier|public
name|SimilarityService
name|similarityService
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|similarityService
argument_list|()
return|;
block|}
DECL|method|scriptService
specifier|public
name|ScriptService
name|scriptService
parameter_list|()
block|{
return|return
name|scriptService
return|;
block|}
DECL|method|filterCache
specifier|public
name|FilterCache
name|filterCache
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|cache
argument_list|()
operator|.
name|filter
argument_list|()
return|;
block|}
DECL|method|fieldData
specifier|public
name|IndexFieldDataService
name|fieldData
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|fieldData
argument_list|()
return|;
block|}
DECL|method|idCache
specifier|public
name|IdCache
name|idCache
parameter_list|()
block|{
return|return
name|indexService
operator|.
name|cache
argument_list|()
operator|.
name|idCache
argument_list|()
return|;
block|}
DECL|method|timeoutInMillis
specifier|public
name|long
name|timeoutInMillis
parameter_list|()
block|{
return|return
name|timeoutInMillis
return|;
block|}
DECL|method|timeoutInMillis
specifier|public
name|void
name|timeoutInMillis
parameter_list|(
name|long
name|timeoutInMillis
parameter_list|)
block|{
name|this
operator|.
name|timeoutInMillis
operator|=
name|timeoutInMillis
expr_stmt|;
block|}
DECL|method|minimumScore
specifier|public
name|SearchContext
name|minimumScore
parameter_list|(
name|float
name|minimumScore
parameter_list|)
block|{
name|this
operator|.
name|minimumScore
operator|=
name|minimumScore
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|minimumScore
specifier|public
name|Float
name|minimumScore
parameter_list|()
block|{
return|return
name|this
operator|.
name|minimumScore
return|;
block|}
DECL|method|sort
specifier|public
name|SearchContext
name|sort
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
name|this
operator|.
name|sort
operator|=
name|sort
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|sort
specifier|public
name|Sort
name|sort
parameter_list|()
block|{
return|return
name|this
operator|.
name|sort
return|;
block|}
DECL|method|trackScores
specifier|public
name|SearchContext
name|trackScores
parameter_list|(
name|boolean
name|trackScores
parameter_list|)
block|{
name|this
operator|.
name|trackScores
operator|=
name|trackScores
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|trackScores
specifier|public
name|boolean
name|trackScores
parameter_list|()
block|{
return|return
name|this
operator|.
name|trackScores
return|;
block|}
DECL|method|parsedFilter
specifier|public
name|SearchContext
name|parsedFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|parsedFilter
specifier|public
name|Filter
name|parsedFilter
parameter_list|()
block|{
return|return
name|this
operator|.
name|filter
return|;
block|}
DECL|method|aliasFilter
specifier|public
name|Filter
name|aliasFilter
parameter_list|()
block|{
return|return
name|aliasFilter
return|;
block|}
DECL|method|parsedQuery
specifier|public
name|SearchContext
name|parsedQuery
parameter_list|(
name|ParsedQuery
name|query
parameter_list|)
block|{
name|queryRewritten
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|originalQuery
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
operator|.
name|query
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|parsedQuery
specifier|public
name|ParsedQuery
name|parsedQuery
parameter_list|()
block|{
return|return
name|this
operator|.
name|originalQuery
return|;
block|}
comment|/**      * The query to execute, might be rewritten.      */
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|this
operator|.
name|query
return|;
block|}
comment|/**      * Has the query been rewritten already?      */
DECL|method|queryRewritten
specifier|public
name|boolean
name|queryRewritten
parameter_list|()
block|{
return|return
name|queryRewritten
return|;
block|}
comment|/**      * Rewrites the query and updates it. Only happens once.      */
DECL|method|updateRewriteQuery
specifier|public
name|SearchContext
name|updateRewriteQuery
parameter_list|(
name|Query
name|rewriteQuery
parameter_list|)
block|{
name|query
operator|=
name|rewriteQuery
expr_stmt|;
name|queryRewritten
operator|=
literal|true
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|from
specifier|public
name|int
name|from
parameter_list|()
block|{
return|return
name|from
return|;
block|}
DECL|method|from
specifier|public
name|SearchContext
name|from
parameter_list|(
name|int
name|from
parameter_list|)
block|{
name|this
operator|.
name|from
operator|=
name|from
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
DECL|method|size
specifier|public
name|SearchContext
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|hasFieldNames
specifier|public
name|boolean
name|hasFieldNames
parameter_list|()
block|{
return|return
name|fieldNames
operator|!=
literal|null
return|;
block|}
DECL|method|fieldNames
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|()
block|{
if|if
condition|(
name|fieldNames
operator|==
literal|null
condition|)
block|{
name|fieldNames
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|()
expr_stmt|;
block|}
return|return
name|fieldNames
return|;
block|}
DECL|method|emptyFieldNames
specifier|public
name|void
name|emptyFieldNames
parameter_list|()
block|{
name|this
operator|.
name|fieldNames
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
DECL|method|explain
specifier|public
name|boolean
name|explain
parameter_list|()
block|{
return|return
name|explain
return|;
block|}
DECL|method|explain
specifier|public
name|void
name|explain
parameter_list|(
name|boolean
name|explain
parameter_list|)
block|{
name|this
operator|.
name|explain
operator|=
name|explain
expr_stmt|;
block|}
annotation|@
name|Nullable
DECL|method|groupStats
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|groupStats
parameter_list|()
block|{
return|return
name|this
operator|.
name|groupStats
return|;
block|}
DECL|method|groupStats
specifier|public
name|void
name|groupStats
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|groupStats
parameter_list|)
block|{
name|this
operator|.
name|groupStats
operator|=
name|groupStats
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|boolean
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|version
specifier|public
name|void
name|version
parameter_list|(
name|boolean
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|docIdsToLoad
specifier|public
name|int
index|[]
name|docIdsToLoad
parameter_list|()
block|{
return|return
name|docIdsToLoad
return|;
block|}
DECL|method|docIdsToLoadFrom
specifier|public
name|int
name|docIdsToLoadFrom
parameter_list|()
block|{
return|return
name|docsIdsToLoadFrom
return|;
block|}
DECL|method|docIdsToLoadSize
specifier|public
name|int
name|docIdsToLoadSize
parameter_list|()
block|{
return|return
name|docsIdsToLoadSize
return|;
block|}
DECL|method|docIdsToLoad
specifier|public
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
block|{
name|this
operator|.
name|docIdsToLoad
operator|=
name|docIdsToLoad
expr_stmt|;
name|this
operator|.
name|docsIdsToLoadFrom
operator|=
name|docsIdsToLoadFrom
expr_stmt|;
name|this
operator|.
name|docsIdsToLoadSize
operator|=
name|docsIdsToLoadSize
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|accessed
specifier|public
name|void
name|accessed
parameter_list|(
name|long
name|accessTime
parameter_list|)
block|{
name|this
operator|.
name|lastAccessTime
operator|=
name|accessTime
expr_stmt|;
block|}
DECL|method|lastAccessTime
specifier|public
name|long
name|lastAccessTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastAccessTime
return|;
block|}
DECL|method|keepAlive
specifier|public
name|long
name|keepAlive
parameter_list|()
block|{
return|return
name|this
operator|.
name|keepAlive
return|;
block|}
DECL|method|keepAlive
specifier|public
name|void
name|keepAlive
parameter_list|(
name|long
name|keepAlive
parameter_list|)
block|{
name|this
operator|.
name|keepAlive
operator|=
name|keepAlive
expr_stmt|;
block|}
DECL|method|lookup
specifier|public
name|SearchLookup
name|lookup
parameter_list|()
block|{
comment|// TODO: The types should take into account the parsing context in QueryParserContext...
if|if
condition|(
name|searchLookup
operator|==
literal|null
condition|)
block|{
name|searchLookup
operator|=
operator|new
name|SearchLookup
argument_list|(
name|mapperService
argument_list|()
argument_list|,
name|fieldData
argument_list|()
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|searchLookup
return|;
block|}
DECL|method|dfsResult
specifier|public
name|DfsSearchResult
name|dfsResult
parameter_list|()
block|{
return|return
name|dfsResult
return|;
block|}
DECL|method|queryResult
specifier|public
name|QuerySearchResult
name|queryResult
parameter_list|()
block|{
return|return
name|queryResult
return|;
block|}
DECL|method|fetchResult
specifier|public
name|FetchSearchResult
name|fetchResult
parameter_list|()
block|{
return|return
name|fetchResult
return|;
block|}
DECL|method|addRewrite
specifier|public
name|void
name|addRewrite
parameter_list|(
name|Rewrite
name|rewrite
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|rewrites
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|rewrites
operator|=
operator|new
name|ArrayList
argument_list|<
name|Rewrite
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|rewrites
operator|.
name|add
argument_list|(
name|rewrite
argument_list|)
expr_stmt|;
block|}
DECL|method|rewrites
specifier|public
name|List
argument_list|<
name|Rewrite
argument_list|>
name|rewrites
parameter_list|()
block|{
return|return
name|this
operator|.
name|rewrites
return|;
block|}
DECL|method|scanContext
specifier|public
name|ScanContext
name|scanContext
parameter_list|()
block|{
if|if
condition|(
name|scanContext
operator|==
literal|null
condition|)
block|{
name|scanContext
operator|=
operator|new
name|ScanContext
argument_list|()
expr_stmt|;
block|}
return|return
name|scanContext
return|;
block|}
DECL|method|smartFieldMappers
specifier|public
name|MapperService
operator|.
name|SmartNameFieldMappers
name|smartFieldMappers
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mapperService
argument_list|()
operator|.
name|smartName
argument_list|(
name|name
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
return|;
block|}
DECL|method|smartNameFieldMappers
specifier|public
name|FieldMappers
name|smartNameFieldMappers
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMappers
argument_list|(
name|name
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
return|;
block|}
DECL|method|smartNameFieldMapper
specifier|public
name|FieldMapper
name|smartNameFieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mapperService
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
name|name
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
return|;
block|}
DECL|method|smartNameObjectMapper
specifier|public
name|MapperService
operator|.
name|SmartNameObjectMapper
name|smartNameObjectMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mapperService
argument_list|()
operator|.
name|smartNameObjectMapper
argument_list|(
name|name
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

