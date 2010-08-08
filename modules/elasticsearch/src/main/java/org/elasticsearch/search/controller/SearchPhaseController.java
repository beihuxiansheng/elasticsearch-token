begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.controller
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|controller
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
name|index
operator|.
name|Term
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
name|*
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
name|PriorityQueue
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
name|collect
operator|.
name|Iterables
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
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
name|trove
operator|.
name|ExtTIntArrayList
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
name|trove
operator|.
name|ExtTObjectIntHasMap
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
name|AggregatedDfs
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
name|facets
operator|.
name|Facet
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
name|internal
operator|.
name|InternalFacet
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
name|internal
operator|.
name|InternalFacets
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
name|FetchSearchResultProvider
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
name|InternalSearchHit
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
name|InternalSearchHits
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
name|InternalSearchResponse
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
name|query
operator|.
name|QuerySearchResultProvider
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|SearchPhaseController
specifier|public
class|class
name|SearchPhaseController
block|{
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|ShardDoc
index|[]
name|EMPTY
init|=
operator|new
name|ShardDoc
index|[
literal|0
index|]
decl_stmt|;
DECL|method|aggregateDfs
specifier|public
name|AggregatedDfs
name|aggregateDfs
parameter_list|(
name|Iterable
argument_list|<
name|DfsSearchResult
argument_list|>
name|results
parameter_list|)
block|{
name|ExtTObjectIntHasMap
argument_list|<
name|Term
argument_list|>
name|dfMap
init|=
operator|new
name|ExtTObjectIntHasMap
argument_list|<
name|Term
argument_list|>
argument_list|()
operator|.
name|defaultReturnValue
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
name|long
name|aggMaxDoc
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DfsSearchResult
name|result
range|:
name|results
control|)
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
name|result
operator|.
name|freqs
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dfMap
operator|.
name|adjustOrPutValue
argument_list|(
name|result
operator|.
name|terms
argument_list|()
index|[
name|i
index|]
argument_list|,
name|result
operator|.
name|freqs
argument_list|()
index|[
name|i
index|]
argument_list|,
name|result
operator|.
name|freqs
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|aggMaxDoc
operator|+=
name|result
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|AggregatedDfs
argument_list|(
name|dfMap
argument_list|,
name|aggMaxDoc
argument_list|)
return|;
block|}
DECL|method|sortDocs
specifier|public
name|ShardDoc
index|[]
name|sortDocs
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|QuerySearchResultProvider
argument_list|>
name|results
parameter_list|)
block|{
if|if
condition|(
name|results
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
name|QuerySearchResultProvider
name|queryResultProvider
init|=
name|Iterables
operator|.
name|get
argument_list|(
name|results
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|totalNumDocs
init|=
literal|0
decl_stmt|;
name|int
name|queueSize
init|=
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|from
argument_list|()
operator|+
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryResultProvider
operator|.
name|includeFetch
argument_list|()
condition|)
block|{
comment|// if we did both query and fetch on the same go, we have fetched all the docs from each shards already, use them...
name|queueSize
operator|*=
name|results
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|PriorityQueue
name|queue
decl_stmt|;
if|if
condition|(
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|instanceof
name|TopFieldDocs
condition|)
block|{
comment|// sorting, first if the type is a String, chance CUSTOM to STRING so we handle nulls properly (since our CUSTOM String sorting might return null)
name|TopFieldDocs
name|fieldDocs
init|=
operator|(
name|TopFieldDocs
operator|)
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldDocs
operator|.
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|allValuesAreNull
init|=
literal|true
decl_stmt|;
name|boolean
name|resolvedField
init|=
literal|false
decl_stmt|;
for|for
control|(
name|QuerySearchResultProvider
name|resultProvider
range|:
name|results
control|)
block|{
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|resultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
control|)
block|{
name|FieldDoc
name|fDoc
init|=
operator|(
name|FieldDoc
operator|)
name|doc
decl_stmt|;
if|if
condition|(
name|fDoc
operator|.
name|fields
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|allValuesAreNull
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|fDoc
operator|.
name|fields
index|[
name|i
index|]
operator|instanceof
name|String
condition|)
block|{
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|,
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|resolvedField
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|resolvedField
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|resolvedField
operator|&&
name|allValuesAreNull
condition|)
block|{
comment|// we did not manage to resolve a field, and all the fields are null (which can only happen for STRING), make it a STRING
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|=
operator|new
name|SortField
argument_list|(
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
argument_list|,
name|SortField
operator|.
name|STRING
argument_list|,
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|getReverse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|queue
operator|=
operator|new
name|ShardFieldDocSortedHitQueue
argument_list|(
name|fieldDocs
operator|.
name|fields
argument_list|,
name|queueSize
argument_list|)
expr_stmt|;
comment|// we need to accumulate for all and then filter the from
for|for
control|(
name|QuerySearchResultProvider
name|resultProvider
range|:
name|results
control|)
block|{
name|QuerySearchResult
name|result
init|=
name|resultProvider
operator|.
name|queryResult
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|result
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|totalNumDocs
operator|+=
name|scoreDocs
operator|.
name|length
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|scoreDocs
control|)
block|{
name|ShardFieldDoc
name|nodeFieldDoc
init|=
operator|new
name|ShardFieldDoc
argument_list|(
name|result
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|doc
operator|.
name|doc
argument_list|,
name|doc
operator|.
name|score
argument_list|,
operator|(
operator|(
name|FieldDoc
operator|)
name|doc
operator|)
operator|.
name|fields
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|insertWithOverflow
argument_list|(
name|nodeFieldDoc
argument_list|)
operator|==
name|nodeFieldDoc
condition|)
block|{
comment|// filled the queue, break
break|break;
block|}
block|}
block|}
block|}
else|else
block|{
name|queue
operator|=
operator|new
name|ScoreDocQueue
argument_list|(
name|queueSize
argument_list|)
expr_stmt|;
comment|// we need to accumulate for all and then filter the from
for|for
control|(
name|QuerySearchResultProvider
name|resultProvider
range|:
name|results
control|)
block|{
name|QuerySearchResult
name|result
init|=
name|resultProvider
operator|.
name|queryResult
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|scoreDocs
init|=
name|result
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
decl_stmt|;
name|totalNumDocs
operator|+=
name|scoreDocs
operator|.
name|length
expr_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|scoreDocs
control|)
block|{
name|ShardScoreDoc
name|nodeScoreDoc
init|=
operator|new
name|ShardScoreDoc
argument_list|(
name|result
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|doc
operator|.
name|doc
argument_list|,
name|doc
operator|.
name|score
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|.
name|insertWithOverflow
argument_list|(
name|nodeScoreDoc
argument_list|)
operator|==
name|nodeScoreDoc
condition|)
block|{
comment|// filled the queue, break
break|break;
block|}
block|}
block|}
block|}
name|int
name|resultDocsSize
init|=
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|queryResultProvider
operator|.
name|includeFetch
argument_list|()
condition|)
block|{
comment|// if we did both query and fetch on the same go, we have fetched all the docs from each shards already, use them...
name|resultDocsSize
operator|*=
name|results
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|totalNumDocs
operator|<
name|queueSize
condition|)
block|{
name|resultDocsSize
operator|=
name|totalNumDocs
operator|-
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|from
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|resultDocsSize
operator|<=
literal|0
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
name|ShardDoc
index|[]
name|shardDocs
init|=
operator|new
name|ShardDoc
index|[
name|resultDocsSize
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|resultDocsSize
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
comment|// put docs in array
name|shardDocs
index|[
name|i
index|]
operator|=
operator|(
name|ShardDoc
operator|)
name|queue
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
name|shardDocs
return|;
block|}
DECL|method|docIdsToLoad
specifier|public
name|Map
argument_list|<
name|SearchShardTarget
argument_list|,
name|ExtTIntArrayList
argument_list|>
name|docIdsToLoad
parameter_list|(
name|ShardDoc
index|[]
name|shardDocs
parameter_list|)
block|{
name|Map
argument_list|<
name|SearchShardTarget
argument_list|,
name|ExtTIntArrayList
argument_list|>
name|result
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardDoc
name|shardDoc
range|:
name|shardDocs
control|)
block|{
name|ExtTIntArrayList
name|list
init|=
name|result
operator|.
name|get
argument_list|(
name|shardDoc
operator|.
name|shardTarget
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ExtTIntArrayList
argument_list|()
expr_stmt|;
comment|// can't be shared!, uses unsafe on it later on
name|result
operator|.
name|put
argument_list|(
name|shardDoc
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|shardDoc
operator|.
name|docId
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|merge
specifier|public
name|InternalSearchResponse
name|merge
parameter_list|(
name|ShardDoc
index|[]
name|sortedDocs
parameter_list|,
name|Map
argument_list|<
name|SearchShardTarget
argument_list|,
name|?
extends|extends
name|QuerySearchResultProvider
argument_list|>
name|queryResults
parameter_list|,
name|Map
argument_list|<
name|SearchShardTarget
argument_list|,
name|?
extends|extends
name|FetchSearchResultProvider
argument_list|>
name|fetchResults
parameter_list|)
block|{
name|boolean
name|sorted
init|=
literal|false
decl_stmt|;
name|int
name|sortScoreIndex
init|=
operator|-
literal|1
decl_stmt|;
name|QuerySearchResult
name|querySearchResult
init|=
name|Iterables
operator|.
name|get
argument_list|(
name|queryResults
operator|.
name|values
argument_list|()
argument_list|,
literal|0
argument_list|)
operator|.
name|queryResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|querySearchResult
operator|.
name|topDocs
argument_list|()
operator|instanceof
name|TopFieldDocs
condition|)
block|{
name|sorted
operator|=
literal|true
expr_stmt|;
name|TopFieldDocs
name|fieldDocs
init|=
operator|(
name|TopFieldDocs
operator|)
name|querySearchResult
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldDocs
operator|.
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|getType
argument_list|()
operator|==
name|SortField
operator|.
name|SCORE
condition|)
block|{
name|sortScoreIndex
operator|=
name|i
expr_stmt|;
block|}
block|}
block|}
comment|// merge facets
name|InternalFacets
name|facets
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|queryResults
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// we rely on the fact that the order of facets is the same on all query results
name|QuerySearchResult
name|queryResult
init|=
name|queryResults
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|queryResult
argument_list|()
decl_stmt|;
comment|// we assume the facets are in the same order!
if|if
condition|(
name|queryResult
operator|.
name|facets
argument_list|()
operator|!=
literal|null
operator|&&
name|queryResult
operator|.
name|facets
argument_list|()
operator|.
name|facets
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|queryResult
operator|.
name|facets
argument_list|()
operator|.
name|facets
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|Facet
argument_list|>
name|allFacets
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|QuerySearchResultProvider
name|queryResultProvider
range|:
name|queryResults
operator|.
name|values
argument_list|()
control|)
block|{
name|allFacets
operator|.
name|addAll
argument_list|(
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|facets
argument_list|()
operator|.
name|facets
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Facet
argument_list|>
name|mergedFacets
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Facet
name|facet
range|:
name|queryResult
operator|.
name|facets
argument_list|()
operator|.
name|facets
argument_list|()
control|)
block|{
name|mergedFacets
operator|.
name|add
argument_list|(
operator|(
operator|(
name|InternalFacet
operator|)
name|facet
operator|)
operator|.
name|aggregate
argument_list|(
name|allFacets
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|facets
operator|=
operator|new
name|InternalFacets
argument_list|(
name|mergedFacets
argument_list|)
expr_stmt|;
block|}
block|}
comment|// count the total (we use the query result provider here, since we might not get any hits (we scrolled past them))
name|long
name|totalHits
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
name|Float
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
for|for
control|(
name|QuerySearchResultProvider
name|queryResultProvider
range|:
name|queryResults
operator|.
name|values
argument_list|()
control|)
block|{
name|totalHits
operator|+=
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|totalHits
expr_stmt|;
if|if
condition|(
operator|!
name|Float
operator|.
name|isNaN
argument_list|(
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
argument_list|)
condition|)
block|{
name|maxScore
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxScore
argument_list|,
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|getMaxScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Float
operator|.
name|isInfinite
argument_list|(
name|maxScore
argument_list|)
condition|)
block|{
name|maxScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
block|}
comment|// clean the fetch counter
for|for
control|(
name|FetchSearchResultProvider
name|fetchSearchResultProvider
range|:
name|fetchResults
operator|.
name|values
argument_list|()
control|)
block|{
name|fetchSearchResultProvider
operator|.
name|fetchResult
argument_list|()
operator|.
name|initCounter
argument_list|()
expr_stmt|;
block|}
comment|// merge hits
name|List
argument_list|<
name|InternalSearchHit
argument_list|>
name|hits
init|=
operator|new
name|ArrayList
argument_list|<
name|InternalSearchHit
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fetchResults
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|ShardDoc
name|shardDoc
range|:
name|sortedDocs
control|)
block|{
name|FetchSearchResultProvider
name|fetchResultProvider
init|=
name|fetchResults
operator|.
name|get
argument_list|(
name|shardDoc
operator|.
name|shardTarget
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fetchResultProvider
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|FetchSearchResult
name|fetchResult
init|=
name|fetchResultProvider
operator|.
name|fetchResult
argument_list|()
decl_stmt|;
name|int
name|index
init|=
name|fetchResult
operator|.
name|counterGetAndIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|index
operator|<
name|fetchResult
operator|.
name|hits
argument_list|()
operator|.
name|internalHits
argument_list|()
operator|.
name|length
condition|)
block|{
name|InternalSearchHit
name|searchHit
init|=
name|fetchResult
operator|.
name|hits
argument_list|()
operator|.
name|internalHits
argument_list|()
index|[
name|index
index|]
decl_stmt|;
name|searchHit
operator|.
name|score
argument_list|(
name|shardDoc
operator|.
name|score
argument_list|()
argument_list|)
expr_stmt|;
name|searchHit
operator|.
name|shard
argument_list|(
name|fetchResult
operator|.
name|shardTarget
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sorted
condition|)
block|{
name|FieldDoc
name|fieldDoc
init|=
operator|(
name|FieldDoc
operator|)
name|shardDoc
decl_stmt|;
name|searchHit
operator|.
name|sortValues
argument_list|(
name|fieldDoc
operator|.
name|fields
argument_list|)
expr_stmt|;
if|if
condition|(
name|sortScoreIndex
operator|!=
operator|-
literal|1
condition|)
block|{
name|searchHit
operator|.
name|score
argument_list|(
operator|(
operator|(
name|Number
operator|)
name|fieldDoc
operator|.
name|fields
index|[
name|sortScoreIndex
index|]
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|hits
operator|.
name|add
argument_list|(
name|searchHit
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|InternalSearchHits
name|searchHits
init|=
operator|new
name|InternalSearchHits
argument_list|(
name|hits
operator|.
name|toArray
argument_list|(
operator|new
name|InternalSearchHit
index|[
name|hits
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|totalHits
argument_list|,
name|maxScore
argument_list|)
decl_stmt|;
return|return
operator|new
name|InternalSearchResponse
argument_list|(
name|searchHits
argument_list|,
name|facets
argument_list|)
return|;
block|}
block|}
end_class

end_unit

