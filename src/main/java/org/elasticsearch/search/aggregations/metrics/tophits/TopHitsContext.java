begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.tophits
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|tophits
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
name|internal
operator|.
name|ContextIndexSearcher
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
name|internal
operator|.
name|SearchContext
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
name|internal
operator|.
name|ShardSearchRequest
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
DECL|class|TopHitsContext
specifier|public
class|class
name|TopHitsContext
extends|extends
name|SearchContext
block|{
comment|// By default return 3 hits per bucket. A higher default would make the response really large by default, since
comment|// the to hits are returned per bucket.
DECL|field|DEFAULT_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|DEFAULT_SIZE
init|=
literal|3
decl_stmt|;
DECL|field|from
specifier|private
name|int
name|from
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
init|=
name|DEFAULT_SIZE
decl_stmt|;
DECL|field|sort
specifier|private
name|Sort
name|sort
decl_stmt|;
DECL|field|fetchSearchResult
specifier|private
specifier|final
name|FetchSearchResult
name|fetchSearchResult
decl_stmt|;
DECL|field|querySearchResult
specifier|private
specifier|final
name|QuerySearchResult
name|querySearchResult
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
DECL|field|context
specifier|private
specifier|final
name|SearchContext
name|context
decl_stmt|;
DECL|field|fieldNames
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
DECL|field|fieldDataFields
specifier|private
name|FieldDataFieldsContext
name|fieldDataFields
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
DECL|field|fetchSourceContext
specifier|private
name|FetchSourceContext
name|fetchSourceContext
decl_stmt|;
DECL|field|highlight
specifier|private
name|SearchContextHighlight
name|highlight
decl_stmt|;
DECL|field|explain
specifier|private
name|boolean
name|explain
decl_stmt|;
DECL|field|trackScores
specifier|private
name|boolean
name|trackScores
decl_stmt|;
DECL|field|version
specifier|private
name|boolean
name|version
decl_stmt|;
DECL|method|TopHitsContext
specifier|public
name|TopHitsContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|fetchSearchResult
operator|=
operator|new
name|FetchSearchResult
argument_list|()
expr_stmt|;
name|this
operator|.
name|querySearchResult
operator|=
operator|new
name|QuerySearchResult
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|searchFilter
specifier|public
name|Filter
name|searchFilter
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this context should be read only"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|context
operator|.
name|id
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|source
specifier|public
name|String
name|source
parameter_list|()
block|{
return|return
name|context
operator|.
name|source
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|request
specifier|public
name|ShardSearchRequest
name|request
parameter_list|()
block|{
return|return
name|context
operator|.
name|request
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|searchType
specifier|public
name|SearchType
name|searchType
parameter_list|()
block|{
return|return
name|context
operator|.
name|searchType
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|searchType
specifier|public
name|SearchContext
name|searchType
parameter_list|(
name|SearchType
name|searchType
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"this context should be read only"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|shardTarget
specifier|public
name|SearchShardTarget
name|shardTarget
parameter_list|()
block|{
return|return
name|context
operator|.
name|shardTarget
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|numberOfShards
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|context
operator|.
name|numberOfShards
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasTypes
specifier|public
name|boolean
name|hasTypes
parameter_list|()
block|{
return|return
name|context
operator|.
name|hasTypes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|context
operator|.
name|types
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|queryBoost
specifier|public
name|float
name|queryBoost
parameter_list|()
block|{
return|return
name|context
operator|.
name|queryBoost
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|queryBoost
specifier|public
name|SearchContext
name|queryBoost
parameter_list|(
name|float
name|queryBoost
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|nowInMillisImpl
specifier|protected
name|long
name|nowInMillisImpl
parameter_list|()
block|{
return|return
name|context
operator|.
name|nowInMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scroll
specifier|public
name|Scroll
name|scroll
parameter_list|()
block|{
return|return
name|context
operator|.
name|scroll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scroll
specifier|public
name|SearchContext
name|scroll
parameter_list|(
name|Scroll
name|scroll
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|aggregations
specifier|public
name|SearchContextAggregations
name|aggregations
parameter_list|()
block|{
return|return
name|context
operator|.
name|aggregations
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|aggregations
specifier|public
name|SearchContext
name|aggregations
parameter_list|(
name|SearchContextAggregations
name|aggregations
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|facets
specifier|public
name|SearchContextFacets
name|facets
parameter_list|()
block|{
return|return
name|context
operator|.
name|facets
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|facets
specifier|public
name|SearchContext
name|facets
parameter_list|(
name|SearchContextFacets
name|facets
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
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
annotation|@
name|Override
DECL|method|suggest
specifier|public
name|SuggestionSearchContext
name|suggest
parameter_list|()
block|{
return|return
name|context
operator|.
name|suggest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|suggest
specifier|public
name|void
name|suggest
parameter_list|(
name|SuggestionSearchContext
name|suggest
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|rescore
specifier|public
name|List
argument_list|<
name|RescoreSearchContext
argument_list|>
name|rescore
parameter_list|()
block|{
return|return
name|context
operator|.
name|rescore
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|addRescore
specifier|public
name|void
name|addRescore
parameter_list|(
name|RescoreSearchContext
name|rescore
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|hasFieldDataFields
specifier|public
name|boolean
name|hasFieldDataFields
parameter_list|()
block|{
return|return
name|fieldDataFields
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|fieldDataFields
specifier|public
name|FieldDataFieldsContext
name|fieldDataFields
parameter_list|()
block|{
if|if
condition|(
name|fieldDataFields
operator|==
literal|null
condition|)
block|{
name|fieldDataFields
operator|=
operator|new
name|FieldDataFieldsContext
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|fieldDataFields
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|sourceRequested
specifier|public
name|boolean
name|sourceRequested
parameter_list|()
block|{
return|return
name|fetchSourceContext
operator|!=
literal|null
operator|&&
name|fetchSourceContext
operator|.
name|fetchSource
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasFetchSourceContext
specifier|public
name|boolean
name|hasFetchSourceContext
parameter_list|()
block|{
return|return
name|fetchSourceContext
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|fetchSourceContext
specifier|public
name|SearchContext
name|fetchSourceContext
parameter_list|(
name|FetchSourceContext
name|fetchSourceContext
parameter_list|)
block|{
name|this
operator|.
name|fetchSourceContext
operator|=
name|fetchSourceContext
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|searcher
specifier|public
name|ContextIndexSearcher
name|searcher
parameter_list|()
block|{
return|return
name|context
operator|.
name|searcher
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|indexShard
specifier|public
name|IndexShard
name|indexShard
parameter_list|()
block|{
return|return
name|context
operator|.
name|indexShard
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|mapperService
specifier|public
name|MapperService
name|mapperService
parameter_list|()
block|{
return|return
name|context
operator|.
name|mapperService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|analysisService
specifier|public
name|AnalysisService
name|analysisService
parameter_list|()
block|{
return|return
name|context
operator|.
name|analysisService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|queryParserService
specifier|public
name|IndexQueryParserService
name|queryParserService
parameter_list|()
block|{
return|return
name|context
operator|.
name|queryParserService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|similarityService
specifier|public
name|SimilarityService
name|similarityService
parameter_list|()
block|{
return|return
name|context
operator|.
name|similarityService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scriptService
specifier|public
name|ScriptService
name|scriptService
parameter_list|()
block|{
return|return
name|context
operator|.
name|scriptService
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|cacheRecycler
specifier|public
name|CacheRecycler
name|cacheRecycler
parameter_list|()
block|{
return|return
name|context
operator|.
name|cacheRecycler
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|pageCacheRecycler
specifier|public
name|PageCacheRecycler
name|pageCacheRecycler
parameter_list|()
block|{
return|return
name|context
operator|.
name|pageCacheRecycler
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|bigArrays
specifier|public
name|BigArrays
name|bigArrays
parameter_list|()
block|{
return|return
name|context
operator|.
name|bigArrays
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|filterCache
specifier|public
name|FilterCache
name|filterCache
parameter_list|()
block|{
return|return
name|context
operator|.
name|filterCache
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docSetCache
specifier|public
name|DocSetCache
name|docSetCache
parameter_list|()
block|{
return|return
name|context
operator|.
name|docSetCache
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fieldData
specifier|public
name|IndexFieldDataService
name|fieldData
parameter_list|()
block|{
return|return
name|context
operator|.
name|fieldData
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|timeoutInMillis
specifier|public
name|long
name|timeoutInMillis
parameter_list|()
block|{
return|return
name|context
operator|.
name|timeoutInMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|timeoutInMillis
specifier|public
name|void
name|timeoutInMillis
parameter_list|(
name|long
name|timeoutInMillis
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|terminateAfter
specifier|public
name|int
name|terminateAfter
parameter_list|()
block|{
return|return
name|context
operator|.
name|terminateAfter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|terminateAfter
specifier|public
name|void
name|terminateAfter
parameter_list|(
name|int
name|terminateAfter
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|minimumScore
specifier|public
name|SearchContext
name|minimumScore
parameter_list|(
name|float
name|minimumScore
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|minimumScore
specifier|public
name|Float
name|minimumScore
parameter_list|()
block|{
return|return
name|context
operator|.
name|minimumScore
argument_list|()
return|;
block|}
annotation|@
name|Override
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
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|sort
specifier|public
name|Sort
name|sort
parameter_list|()
block|{
return|return
name|sort
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|trackScores
specifier|public
name|boolean
name|trackScores
parameter_list|()
block|{
return|return
name|trackScores
return|;
block|}
annotation|@
name|Override
DECL|method|parsedPostFilter
specifier|public
name|SearchContext
name|parsedPostFilter
parameter_list|(
name|ParsedFilter
name|postFilter
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|parsedPostFilter
specifier|public
name|ParsedFilter
name|parsedPostFilter
parameter_list|()
block|{
return|return
name|context
operator|.
name|parsedPostFilter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|aliasFilter
specifier|public
name|Filter
name|aliasFilter
parameter_list|()
block|{
return|return
name|context
operator|.
name|aliasFilter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parsedQuery
specifier|public
name|SearchContext
name|parsedQuery
parameter_list|(
name|ParsedQuery
name|query
parameter_list|)
block|{
return|return
name|context
operator|.
name|parsedQuery
argument_list|(
name|query
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parsedQuery
specifier|public
name|ParsedQuery
name|parsedQuery
parameter_list|()
block|{
return|return
name|context
operator|.
name|parsedQuery
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|context
operator|.
name|query
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|queryRewritten
specifier|public
name|boolean
name|queryRewritten
parameter_list|()
block|{
return|return
name|context
operator|.
name|queryRewritten
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|updateRewriteQuery
specifier|public
name|SearchContext
name|updateRewriteQuery
parameter_list|(
name|Query
name|rewriteQuery
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
name|Override
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
name|context
operator|.
name|groupStats
argument_list|()
return|;
block|}
annotation|@
name|Override
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|accessed
specifier|public
name|void
name|accessed
parameter_list|(
name|long
name|accessTime
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|lastAccessTime
specifier|public
name|long
name|lastAccessTime
parameter_list|()
block|{
return|return
name|context
operator|.
name|lastAccessTime
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|keepAlive
specifier|public
name|long
name|keepAlive
parameter_list|()
block|{
return|return
name|context
operator|.
name|keepAlive
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|keepAlive
specifier|public
name|void
name|keepAlive
parameter_list|(
name|long
name|keepAlive
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|lastEmittedDoc
specifier|public
name|void
name|lastEmittedDoc
parameter_list|(
name|ScoreDoc
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|lastEmittedDoc
specifier|public
name|ScoreDoc
name|lastEmittedDoc
parameter_list|()
block|{
return|return
name|context
operator|.
name|lastEmittedDoc
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookup
specifier|public
name|SearchLookup
name|lookup
parameter_list|()
block|{
return|return
name|context
operator|.
name|lookup
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|dfsResult
specifier|public
name|DfsSearchResult
name|dfsResult
parameter_list|()
block|{
return|return
name|context
operator|.
name|dfsResult
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|queryResult
specifier|public
name|QuerySearchResult
name|queryResult
parameter_list|()
block|{
return|return
name|querySearchResult
return|;
block|}
annotation|@
name|Override
DECL|method|fetchResult
specifier|public
name|FetchSearchResult
name|fetchResult
parameter_list|()
block|{
return|return
name|fetchSearchResult
return|;
block|}
annotation|@
name|Override
DECL|method|scanContext
specifier|public
name|ScanContext
name|scanContext
parameter_list|()
block|{
return|return
name|context
operator|.
name|scanContext
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|context
operator|.
name|smartFieldMappers
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|context
operator|.
name|smartNameFieldMappers
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|context
operator|.
name|smartNameFieldMapper
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|context
operator|.
name|smartNameObjectMapper
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|useSlowScroll
specifier|public
name|boolean
name|useSlowScroll
parameter_list|()
block|{
return|return
name|context
operator|.
name|useSlowScroll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|useSlowScroll
specifier|public
name|SearchContext
name|useSlowScroll
parameter_list|(
name|boolean
name|useSlowScroll
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

