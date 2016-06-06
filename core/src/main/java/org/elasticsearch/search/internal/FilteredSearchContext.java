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
name|Collector
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
name|FieldDoc
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
name|util
operator|.
name|Counter
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
name|bitset
operator|.
name|BitsetFilterCache
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
name|MappedFieldType
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|QueryShardContext
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
name|FetchPhase
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
name|FetchSubPhase
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
name|FetchSubPhaseContext
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
name|innerhits
operator|.
name|InnerHitsContext
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
name|profile
operator|.
name|Profilers
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
name|sort
operator|.
name|SortAndFormats
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|FilteredSearchContext
specifier|public
specifier|abstract
class|class
name|FilteredSearchContext
extends|extends
name|SearchContext
block|{
DECL|field|in
specifier|private
specifier|final
name|SearchContext
name|in
decl_stmt|;
DECL|method|FilteredSearchContext
specifier|public
name|FilteredSearchContext
parameter_list|(
name|SearchContext
name|in
parameter_list|)
block|{
comment|//inner_hits in percolator ends up with null inner search context
name|super
argument_list|(
name|in
operator|==
literal|null
condition|?
name|ParseFieldMatcher
operator|.
name|EMPTY
else|:
name|in
operator|.
name|parseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|in
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|()
block|{
name|in
operator|.
name|preProcess
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|searchFilter
specifier|public
name|Query
name|searchFilter
parameter_list|(
name|String
index|[]
name|types
parameter_list|)
block|{
return|return
name|in
operator|.
name|searchFilter
argument_list|(
name|types
argument_list|)
return|;
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
name|in
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
name|in
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
name|in
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
name|in
operator|.
name|searchType
argument_list|()
return|;
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
name|in
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
name|in
operator|.
name|numberOfShards
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
name|in
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
return|return
name|in
operator|.
name|queryBoost
argument_list|(
name|queryBoost
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getOriginNanoTime
specifier|public
name|long
name|getOriginNanoTime
parameter_list|()
block|{
return|return
name|in
operator|.
name|getOriginNanoTime
argument_list|()
return|;
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
name|in
operator|.
name|nowInMillisImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scrollContext
specifier|public
name|ScrollContext
name|scrollContext
parameter_list|()
block|{
return|return
name|in
operator|.
name|scrollContext
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|scrollContext
specifier|public
name|SearchContext
name|scrollContext
parameter_list|(
name|ScrollContext
name|scroll
parameter_list|)
block|{
return|return
name|in
operator|.
name|scrollContext
argument_list|(
name|scroll
argument_list|)
return|;
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
name|in
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
return|return
name|in
operator|.
name|aggregations
argument_list|(
name|aggregations
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|highlight
specifier|public
name|SearchContextHighlight
name|highlight
parameter_list|()
block|{
return|return
name|in
operator|.
name|highlight
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|highlight
specifier|public
name|void
name|highlight
parameter_list|(
name|SearchContextHighlight
name|highlight
parameter_list|)
block|{
name|in
operator|.
name|highlight
argument_list|(
name|highlight
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|innerHits
specifier|public
name|InnerHitsContext
name|innerHits
parameter_list|()
block|{
return|return
name|in
operator|.
name|innerHits
argument_list|()
return|;
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
name|in
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
name|in
operator|.
name|suggest
argument_list|(
name|suggest
argument_list|)
expr_stmt|;
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
name|in
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
name|in
operator|.
name|addRescore
argument_list|(
name|rescore
argument_list|)
expr_stmt|;
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
name|in
operator|.
name|hasScriptFields
argument_list|()
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
return|return
name|in
operator|.
name|scriptFields
argument_list|()
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
name|in
operator|.
name|sourceRequested
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
name|in
operator|.
name|hasFetchSourceContext
argument_list|()
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
name|in
operator|.
name|fetchSourceContext
argument_list|()
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
return|return
name|in
operator|.
name|fetchSourceContext
argument_list|(
name|fetchSourceContext
argument_list|)
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
name|in
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
name|in
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
name|in
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
name|in
operator|.
name|analysisService
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
name|in
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
name|in
operator|.
name|scriptService
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
name|in
operator|.
name|bigArrays
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|bitsetFilterCache
specifier|public
name|BitsetFilterCache
name|bitsetFilterCache
parameter_list|()
block|{
return|return
name|in
operator|.
name|bitsetFilterCache
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
name|in
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
name|in
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
name|in
operator|.
name|timeoutInMillis
argument_list|(
name|timeoutInMillis
argument_list|)
expr_stmt|;
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
name|in
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
name|in
operator|.
name|terminateAfter
argument_list|(
name|terminateAfter
argument_list|)
expr_stmt|;
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
return|return
name|in
operator|.
name|minimumScore
argument_list|(
name|minimumScore
argument_list|)
return|;
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
name|in
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
name|SortAndFormats
name|sort
parameter_list|)
block|{
return|return
name|in
operator|.
name|sort
argument_list|(
name|sort
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sort
specifier|public
name|SortAndFormats
name|sort
parameter_list|()
block|{
return|return
name|in
operator|.
name|sort
argument_list|()
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
return|return
name|in
operator|.
name|trackScores
argument_list|(
name|trackScores
argument_list|)
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
name|in
operator|.
name|trackScores
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|searchAfter
specifier|public
name|SearchContext
name|searchAfter
parameter_list|(
name|FieldDoc
name|searchAfter
parameter_list|)
block|{
return|return
name|in
operator|.
name|searchAfter
argument_list|(
name|searchAfter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|searchAfter
specifier|public
name|FieldDoc
name|searchAfter
parameter_list|()
block|{
return|return
name|in
operator|.
name|searchAfter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parsedPostFilter
specifier|public
name|SearchContext
name|parsedPostFilter
parameter_list|(
name|ParsedQuery
name|postFilter
parameter_list|)
block|{
return|return
name|in
operator|.
name|parsedPostFilter
argument_list|(
name|postFilter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parsedPostFilter
specifier|public
name|ParsedQuery
name|parsedPostFilter
parameter_list|()
block|{
return|return
name|in
operator|.
name|parsedPostFilter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|aliasFilter
specifier|public
name|Query
name|aliasFilter
parameter_list|()
block|{
return|return
name|in
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
name|in
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
name|in
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
name|in
operator|.
name|query
argument_list|()
return|;
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
name|in
operator|.
name|from
argument_list|()
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
return|return
name|in
operator|.
name|from
argument_list|(
name|from
argument_list|)
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
name|in
operator|.
name|size
argument_list|()
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
return|return
name|in
operator|.
name|size
argument_list|(
name|size
argument_list|)
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
name|in
operator|.
name|hasFieldNames
argument_list|()
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
return|return
name|in
operator|.
name|fieldNames
argument_list|()
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
name|in
operator|.
name|emptyFieldNames
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
name|in
operator|.
name|explain
argument_list|()
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
name|in
operator|.
name|explain
argument_list|(
name|explain
argument_list|)
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
name|in
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
name|in
operator|.
name|groupStats
argument_list|(
name|groupStats
argument_list|)
expr_stmt|;
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
name|in
operator|.
name|version
argument_list|()
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
name|in
operator|.
name|version
argument_list|(
name|version
argument_list|)
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
name|in
operator|.
name|docIdsToLoad
argument_list|()
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
name|in
operator|.
name|docIdsToLoadFrom
argument_list|()
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
name|in
operator|.
name|docIdsToLoadSize
argument_list|()
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
return|return
name|in
operator|.
name|docIdsToLoad
argument_list|(
name|docIdsToLoad
argument_list|,
name|docsIdsToLoadFrom
argument_list|,
name|docsIdsToLoadSize
argument_list|)
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
name|in
operator|.
name|accessed
argument_list|(
name|accessTime
argument_list|)
expr_stmt|;
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
name|in
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
name|in
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
name|in
operator|.
name|keepAlive
argument_list|(
name|keepAlive
argument_list|)
expr_stmt|;
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
name|in
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
name|in
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
name|in
operator|.
name|queryResult
argument_list|()
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
name|in
operator|.
name|fetchResult
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fetchPhase
specifier|public
name|FetchPhase
name|fetchPhase
parameter_list|()
block|{
return|return
name|in
operator|.
name|fetchPhase
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|smartNameFieldType
specifier|public
name|MappedFieldType
name|smartNameFieldType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|in
operator|.
name|smartNameFieldType
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getObjectMapper
specifier|public
name|ObjectMapper
name|getObjectMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|in
operator|.
name|getObjectMapper
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|timeEstimateCounter
specifier|public
name|Counter
name|timeEstimateCounter
parameter_list|()
block|{
return|return
name|in
operator|.
name|timeEstimateCounter
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFetchSubPhaseContext
specifier|public
parameter_list|<
name|SubPhaseContext
extends|extends
name|FetchSubPhaseContext
parameter_list|>
name|SubPhaseContext
name|getFetchSubPhaseContext
parameter_list|(
name|FetchSubPhase
operator|.
name|ContextFactory
argument_list|<
name|SubPhaseContext
argument_list|>
name|contextFactory
parameter_list|)
block|{
return|return
name|in
operator|.
name|getFetchSubPhaseContext
argument_list|(
name|contextFactory
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getProfilers
specifier|public
name|Profilers
name|getProfilers
parameter_list|()
block|{
return|return
name|in
operator|.
name|getProfilers
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|queryCollectors
specifier|public
name|Map
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|,
name|Collector
argument_list|>
name|queryCollectors
parameter_list|()
block|{
return|return
name|in
operator|.
name|queryCollectors
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getQueryShardContext
specifier|public
name|QueryShardContext
name|getQueryShardContext
parameter_list|()
block|{
return|return
name|in
operator|.
name|getQueryShardContext
argument_list|()
return|;
block|}
block|}
end_class

end_unit

