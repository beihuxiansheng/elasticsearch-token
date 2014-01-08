begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
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
name|ImmutableMap
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
name|TopDocs
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
name|TotalHitCountCollector
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
name|inject
operator|.
name|Inject
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
name|Lucene
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
name|SearchParseElement
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
name|SearchPhase
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
name|AggregationPhase
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
name|FacetPhase
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
name|rescore
operator|.
name|RescorePhase
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
name|SortParseElement
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
name|TrackScoresParseElement
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
name|SuggestPhase
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
comment|/**  *  */
end_comment

begin_class
DECL|class|QueryPhase
specifier|public
class|class
name|QueryPhase
implements|implements
name|SearchPhase
block|{
DECL|field|facetPhase
specifier|private
specifier|final
name|FacetPhase
name|facetPhase
decl_stmt|;
DECL|field|aggregationPhase
specifier|private
specifier|final
name|AggregationPhase
name|aggregationPhase
decl_stmt|;
DECL|field|suggestPhase
specifier|private
specifier|final
name|SuggestPhase
name|suggestPhase
decl_stmt|;
DECL|field|rescorePhase
specifier|private
name|RescorePhase
name|rescorePhase
decl_stmt|;
annotation|@
name|Inject
DECL|method|QueryPhase
specifier|public
name|QueryPhase
parameter_list|(
name|FacetPhase
name|facetPhase
parameter_list|,
name|AggregationPhase
name|aggregationPhase
parameter_list|,
name|SuggestPhase
name|suggestPhase
parameter_list|,
name|RescorePhase
name|rescorePhase
parameter_list|)
block|{
name|this
operator|.
name|facetPhase
operator|=
name|facetPhase
expr_stmt|;
name|this
operator|.
name|aggregationPhase
operator|=
name|aggregationPhase
expr_stmt|;
name|this
operator|.
name|suggestPhase
operator|=
name|suggestPhase
expr_stmt|;
name|this
operator|.
name|rescorePhase
operator|=
name|rescorePhase
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseElements
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SearchParseElement
argument_list|>
name|parseElements
parameter_list|()
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SearchParseElement
argument_list|>
name|parseElements
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|parseElements
operator|.
name|put
argument_list|(
literal|"from"
argument_list|,
operator|new
name|FromParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"size"
argument_list|,
operator|new
name|SizeParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"indices_boost"
argument_list|,
operator|new
name|IndicesBoostParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"indicesBoost"
argument_list|,
operator|new
name|IndicesBoostParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
operator|new
name|QueryParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"queryBinary"
argument_list|,
operator|new
name|QueryBinaryParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"query_binary"
argument_list|,
operator|new
name|QueryBinaryParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter"
argument_list|,
operator|new
name|PostFilterParseElement
argument_list|()
argument_list|)
comment|// For bw comp reason, should be removed in version 1.1
operator|.
name|put
argument_list|(
literal|"post_filter"
argument_list|,
operator|new
name|PostFilterParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"postFilter"
argument_list|,
operator|new
name|PostFilterParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"filterBinary"
argument_list|,
operator|new
name|FilterBinaryParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"filter_binary"
argument_list|,
operator|new
name|FilterBinaryParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
operator|new
name|SortParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"trackScores"
argument_list|,
operator|new
name|TrackScoresParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"track_scores"
argument_list|,
operator|new
name|TrackScoresParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"min_score"
argument_list|,
operator|new
name|MinScoreParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"minScore"
argument_list|,
operator|new
name|MinScoreParseElement
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"timeout"
argument_list|,
operator|new
name|TimeoutParseElement
argument_list|()
argument_list|)
operator|.
name|putAll
argument_list|(
name|facetPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
operator|.
name|putAll
argument_list|(
name|aggregationPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
operator|.
name|putAll
argument_list|(
name|suggestPhase
operator|.
name|parseElements
argument_list|()
argument_list|)
operator|.
name|putAll
argument_list|(
name|rescorePhase
operator|.
name|parseElements
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|parseElements
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|preProcess
specifier|public
name|void
name|preProcess
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
name|context
operator|.
name|preProcess
argument_list|()
expr_stmt|;
name|facetPhase
operator|.
name|preProcess
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|aggregationPhase
operator|.
name|preProcess
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
throws|throws
name|QueryPhaseExecutionException
block|{
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|searchTimedOut
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|inStage
argument_list|(
name|ContextIndexSearcher
operator|.
name|Stage
operator|.
name|MAIN_QUERY
argument_list|)
expr_stmt|;
name|boolean
name|rescore
init|=
literal|false
decl_stmt|;
try|try
block|{
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|from
argument_list|(
name|searchContext
operator|.
name|from
argument_list|()
argument_list|)
expr_stmt|;
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|size
argument_list|(
name|searchContext
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|searchContext
operator|.
name|query
argument_list|()
decl_stmt|;
name|TopDocs
name|topDocs
decl_stmt|;
name|int
name|numDocs
init|=
name|searchContext
operator|.
name|from
argument_list|()
operator|+
name|searchContext
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchContext
operator|.
name|searchType
argument_list|()
operator|==
name|SearchType
operator|.
name|COUNT
operator|||
name|numDocs
operator|==
literal|0
condition|)
block|{
name|TotalHitCountCollector
name|collector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|topDocs
operator|=
operator|new
name|TopDocs
argument_list|(
name|collector
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|Lucene
operator|.
name|EMPTY_SCORE_DOCS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|searchContext
operator|.
name|searchType
argument_list|()
operator|==
name|SearchType
operator|.
name|SCAN
condition|)
block|{
name|topDocs
operator|=
name|searchContext
operator|.
name|scanContext
argument_list|()
operator|.
name|execute
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|searchContext
operator|.
name|sort
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|topDocs
operator|=
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|null
argument_list|,
name|numDocs
argument_list|,
name|searchContext
operator|.
name|sort
argument_list|()
argument_list|,
name|searchContext
operator|.
name|trackScores
argument_list|()
argument_list|,
name|searchContext
operator|.
name|trackScores
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|searchContext
operator|.
name|rescore
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rescore
operator|=
literal|true
expr_stmt|;
name|numDocs
operator|=
name|Math
operator|.
name|max
argument_list|(
name|searchContext
operator|.
name|rescore
argument_list|()
operator|.
name|window
argument_list|()
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|topDocs
operator|=
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
block|}
name|searchContext
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|(
name|topDocs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|QueryPhaseExecutionException
argument_list|(
name|searchContext
argument_list|,
literal|"Failed to execute main query"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|finishStage
argument_list|(
name|ContextIndexSearcher
operator|.
name|Stage
operator|.
name|MAIN_QUERY
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rescore
condition|)
block|{
comment|// only if we do a regular search
name|rescorePhase
operator|.
name|execute
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
name|suggestPhase
operator|.
name|execute
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
name|facetPhase
operator|.
name|execute
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
name|aggregationPhase
operator|.
name|execute
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

