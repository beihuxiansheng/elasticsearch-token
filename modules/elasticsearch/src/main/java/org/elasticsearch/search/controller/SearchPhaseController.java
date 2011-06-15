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
name|ShardFieldDocSortedHitQueue
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
name|SortField
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
name|TopFieldDocs
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
name|collect
operator|.
name|Ordering
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
name|component
operator|.
name|AbstractComponent
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
name|settings
operator|.
name|Settings
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
name|impl
operator|.
name|Constants
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
name|map
operator|.
name|hash
operator|.
name|TObjectIntHashMap
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
name|facet
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
name|facet
operator|.
name|FacetProcessors
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SearchPhaseController
specifier|public
class|class
name|SearchPhaseController
extends|extends
name|AbstractComponent
block|{
DECL|field|QUERY_RESULT_ORDERING
specifier|public
specifier|static
name|Ordering
argument_list|<
name|QuerySearchResultProvider
argument_list|>
name|QUERY_RESULT_ORDERING
init|=
operator|new
name|Ordering
argument_list|<
name|QuerySearchResultProvider
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
annotation|@
name|Nullable
name|QuerySearchResultProvider
name|o1
parameter_list|,
annotation|@
name|Nullable
name|QuerySearchResultProvider
name|o2
parameter_list|)
block|{
name|int
name|i
init|=
name|o1
operator|.
name|shardTarget
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o2
operator|.
name|shardTarget
argument_list|()
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|i
operator|=
name|o1
operator|.
name|shardTarget
argument_list|()
operator|.
name|shardId
argument_list|()
operator|-
name|o2
operator|.
name|shardTarget
argument_list|()
operator|.
name|shardId
argument_list|()
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
block|}
decl_stmt|;
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
DECL|field|facetProcessors
specifier|private
specifier|final
name|FacetProcessors
name|facetProcessors
decl_stmt|;
DECL|field|optimizeSingleShard
specifier|private
specifier|final
name|boolean
name|optimizeSingleShard
decl_stmt|;
DECL|method|SearchPhaseController
annotation|@
name|Inject
specifier|public
name|SearchPhaseController
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|FacetProcessors
name|facetProcessors
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|facetProcessors
operator|=
name|facetProcessors
expr_stmt|;
name|this
operator|.
name|optimizeSingleShard
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"optimize_single_shard"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|optimizeSingleShard
specifier|public
name|boolean
name|optimizeSingleShard
parameter_list|()
block|{
return|return
name|optimizeSingleShard
return|;
block|}
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
name|TObjectIntHashMap
argument_list|<
name|Term
argument_list|>
name|dfMap
init|=
operator|new
name|TObjectIntHashMap
argument_list|<
name|Term
argument_list|>
argument_list|(
name|Constants
operator|.
name|DEFAULT_CAPACITY
argument_list|,
name|Constants
operator|.
name|DEFAULT_LOAD_FACTOR
argument_list|,
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
name|results1
parameter_list|)
block|{
if|if
condition|(
name|results1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
if|if
condition|(
name|optimizeSingleShard
condition|)
block|{
name|boolean
name|canOptimize
init|=
literal|false
decl_stmt|;
name|QuerySearchResult
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|results1
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|canOptimize
operator|=
literal|true
expr_stmt|;
name|result
operator|=
name|results1
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|queryResult
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// lets see if we only got hits from a single shard, if so, we can optimize...
for|for
control|(
name|QuerySearchResultProvider
name|queryResult
range|:
name|results1
control|)
block|{
if|if
condition|(
name|queryResult
operator|.
name|queryResult
argument_list|()
operator|.
name|topDocs
argument_list|()
operator|.
name|scoreDocs
operator|.
name|length
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
comment|// we already have one, can't really optimize
name|canOptimize
operator|=
literal|false
expr_stmt|;
break|break;
block|}
name|canOptimize
operator|=
literal|true
expr_stmt|;
name|result
operator|=
name|queryResult
operator|.
name|queryResult
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|canOptimize
condition|)
block|{
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
if|if
condition|(
name|scoreDocs
operator|.
name|length
operator|<
name|result
operator|.
name|from
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
name|int
name|resultDocsSize
init|=
name|result
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|scoreDocs
operator|.
name|length
operator|-
name|result
operator|.
name|from
argument_list|()
operator|)
operator|<
name|resultDocsSize
condition|)
block|{
name|resultDocsSize
operator|=
name|scoreDocs
operator|.
name|length
operator|-
name|result
operator|.
name|from
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|topDocs
argument_list|()
operator|instanceof
name|TopFieldDocs
condition|)
block|{
name|ShardDoc
index|[]
name|docs
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
literal|0
init|;
name|i
operator|<
name|resultDocsSize
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|result
operator|.
name|from
argument_list|()
operator|+
name|i
index|]
decl_stmt|;
name|docs
index|[
name|i
index|]
operator|=
operator|new
name|ShardFieldDoc
argument_list|(
name|result
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|scoreDoc
operator|.
name|doc
argument_list|,
name|scoreDoc
operator|.
name|score
argument_list|,
operator|(
operator|(
name|FieldDoc
operator|)
name|scoreDoc
operator|)
operator|.
name|fields
argument_list|)
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
else|else
block|{
name|ShardDoc
index|[]
name|docs
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
literal|0
init|;
name|i
operator|<
name|resultDocsSize
condition|;
name|i
operator|++
control|)
block|{
name|ScoreDoc
name|scoreDoc
init|=
name|scoreDocs
index|[
name|result
operator|.
name|from
argument_list|()
operator|+
name|i
index|]
decl_stmt|;
name|docs
index|[
name|i
index|]
operator|=
operator|new
name|ShardScoreDoc
argument_list|(
name|result
operator|.
name|shardTarget
argument_list|()
argument_list|,
name|scoreDoc
operator|.
name|doc
argument_list|,
name|scoreDoc
operator|.
name|score
argument_list|)
expr_stmt|;
block|}
return|return
name|docs
return|;
block|}
block|}
block|}
name|List
argument_list|<
name|?
extends|extends
name|QuerySearchResultProvider
argument_list|>
name|results
init|=
name|QUERY_RESULT_ORDERING
operator|.
name|sortedCopy
argument_list|(
name|results1
argument_list|)
decl_stmt|;
name|QuerySearchResultProvider
name|queryResultProvider
init|=
name|results
operator|.
name|get
argument_list|(
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
comment|// this is also important since we shortcut and fetch only docs from "from" and up to "size"
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
operator|&&
name|fieldDocs
operator|.
name|fields
index|[
name|i
index|]
operator|.
name|getField
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// we did not manage to resolve a field (and its not score or doc, which have no field), and all the fields are null (which can only happen for STRING), make it a STRING
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
comment|// we only pop the first, this handles "from" nicely since the "from" are down the queue
comment|// that we already fetched, so we are actually popping the "from" and up to "size"
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
decl_stmt|;
try|try
block|{
name|querySearchResult
operator|=
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
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// no results, return an empty response
return|return
name|InternalSearchResponse
operator|.
name|EMPTY
return|;
block|}
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
name|aggregatedFacets
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Facet
argument_list|>
name|namedFacets
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
control|)
block|{
comment|// aggregate each facet name into a single list, and aggregate it
name|namedFacets
operator|.
name|clear
argument_list|()
expr_stmt|;
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
for|for
control|(
name|Facet
name|facet1
range|:
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|facets
argument_list|()
control|)
block|{
if|if
condition|(
name|facet
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|facet1
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|namedFacets
operator|.
name|add
argument_list|(
name|facet1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Facet
name|aggregatedFacet
init|=
name|facetProcessors
operator|.
name|processor
argument_list|(
name|facet
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|reduce
argument_list|(
name|facet
operator|.
name|name
argument_list|()
argument_list|,
name|namedFacets
argument_list|)
decl_stmt|;
name|aggregatedFacets
operator|.
name|add
argument_list|(
name|aggregatedFacet
argument_list|)
expr_stmt|;
block|}
name|facets
operator|=
operator|new
name|InternalFacets
argument_list|(
name|aggregatedFacets
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
name|boolean
name|timedOut
init|=
literal|false
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
if|if
condition|(
name|queryResultProvider
operator|.
name|queryResult
argument_list|()
operator|.
name|searchTimedOut
argument_list|()
condition|)
block|{
name|timedOut
operator|=
literal|true
expr_stmt|;
block|}
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
argument_list|,
name|timedOut
argument_list|)
return|;
block|}
block|}
end_class

end_unit

