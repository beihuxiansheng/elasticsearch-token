begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|significant
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
name|IndexReader
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
name|index
operator|.
name|LeafReaderContext
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
name|BytesRef
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
name|search
operator|.
name|DocValueFormat
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
name|Aggregator
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
name|AggregatorFactories
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
name|LeafBucketCollector
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
name|LeafBucketCollectorBase
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
name|bucket
operator|.
name|significant
operator|.
name|heuristics
operator|.
name|SignificanceHeuristic
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
name|bucket
operator|.
name|terms
operator|.
name|StringTermsAggregator
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
name|bucket
operator|.
name|terms
operator|.
name|support
operator|.
name|IncludeExclude
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
name|pipeline
operator|.
name|PipelineAggregator
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
name|support
operator|.
name|ValuesSource
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

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
import|;
end_import

begin_comment
comment|/**  * An aggregator of significant string values.  */
end_comment

begin_class
DECL|class|SignificantStringTermsAggregator
specifier|public
class|class
name|SignificantStringTermsAggregator
extends|extends
name|StringTermsAggregator
block|{
DECL|field|numCollectedDocs
specifier|protected
name|long
name|numCollectedDocs
decl_stmt|;
DECL|field|termsAggFactory
specifier|protected
specifier|final
name|SignificantTermsAggregatorFactory
name|termsAggFactory
decl_stmt|;
DECL|field|significanceHeuristic
specifier|private
specifier|final
name|SignificanceHeuristic
name|significanceHeuristic
decl_stmt|;
DECL|method|SignificantStringTermsAggregator
specifier|public
name|SignificantStringTermsAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|DocValueFormat
name|format
parameter_list|,
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|,
name|IncludeExclude
operator|.
name|StringFilter
name|includeExclude
parameter_list|,
name|SearchContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|SignificanceHeuristic
name|significanceHeuristic
parameter_list|,
name|SignificantTermsAggregatorFactory
name|termsAggFactory
parameter_list|,
name|List
argument_list|<
name|PipelineAggregator
argument_list|>
name|pipelineAggregators
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|valuesSource
argument_list|,
literal|null
argument_list|,
name|format
argument_list|,
name|bucketCountThresholds
argument_list|,
name|includeExclude
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|SubAggCollectionMode
operator|.
name|DEPTH_FIRST
argument_list|,
literal|false
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|significanceHeuristic
operator|=
name|significanceHeuristic
expr_stmt|;
name|this
operator|.
name|termsAggFactory
operator|=
name|termsAggFactory
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|public
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
specifier|final
name|LeafBucketCollector
name|sub
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|LeafBucketCollectorBase
argument_list|(
name|super
operator|.
name|getLeafCollector
argument_list|(
name|ctx
argument_list|,
name|sub
argument_list|)
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
name|bucket
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|collect
argument_list|(
name|doc
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
name|numCollectedDocs
operator|++
expr_stmt|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|buildAggregation
specifier|public
name|SignificantStringTerms
name|buildAggregation
parameter_list|(
name|long
name|owningBucketOrdinal
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|owningBucketOrdinal
operator|==
literal|0
assert|;
specifier|final
name|int
name|size
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|bucketOrds
operator|.
name|size
argument_list|()
argument_list|,
name|bucketCountThresholds
operator|.
name|getShardSize
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|supersetSize
init|=
name|termsAggFactory
operator|.
name|getSupersetNumDocs
argument_list|()
decl_stmt|;
name|long
name|subsetSize
init|=
name|numCollectedDocs
decl_stmt|;
name|BucketSignificancePriorityQueue
argument_list|<
name|SignificantStringTerms
operator|.
name|Bucket
argument_list|>
name|ordered
init|=
operator|new
name|BucketSignificancePriorityQueue
argument_list|<>
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|SignificantStringTerms
operator|.
name|Bucket
name|spare
init|=
literal|null
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
name|bucketOrds
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|docCount
init|=
name|bucketDocCount
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|docCount
operator|<
name|bucketCountThresholds
operator|.
name|getShardMinDocCount
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|spare
operator|==
literal|null
condition|)
block|{
name|spare
operator|=
operator|new
name|SignificantStringTerms
operator|.
name|Bucket
argument_list|(
operator|new
name|BytesRef
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|format
argument_list|)
expr_stmt|;
block|}
name|bucketOrds
operator|.
name|get
argument_list|(
name|i
argument_list|,
name|spare
operator|.
name|termBytes
argument_list|)
expr_stmt|;
name|spare
operator|.
name|subsetDf
operator|=
name|docCount
expr_stmt|;
name|spare
operator|.
name|subsetSize
operator|=
name|subsetSize
expr_stmt|;
name|spare
operator|.
name|supersetDf
operator|=
name|termsAggFactory
operator|.
name|getBackgroundFrequency
argument_list|(
name|spare
operator|.
name|termBytes
argument_list|)
expr_stmt|;
name|spare
operator|.
name|supersetSize
operator|=
name|supersetSize
expr_stmt|;
comment|// During shard-local down-selection we use subset/superset stats
comment|// that are for this shard only
comment|// Back at the central reducer these properties will be updated with
comment|// global stats
name|spare
operator|.
name|updateScore
argument_list|(
name|significanceHeuristic
argument_list|)
expr_stmt|;
name|spare
operator|.
name|bucketOrd
operator|=
name|i
expr_stmt|;
name|spare
operator|=
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
name|spare
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SignificantStringTerms
operator|.
name|Bucket
index|[]
name|list
init|=
operator|new
name|SignificantStringTerms
operator|.
name|Bucket
index|[
name|ordered
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ordered
operator|.
name|size
argument_list|()
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
block|{
specifier|final
name|SignificantStringTerms
operator|.
name|Bucket
name|bucket
init|=
name|ordered
operator|.
name|pop
argument_list|()
decl_stmt|;
comment|// the terms are owned by the BytesRefHash, we need to pull a copy since the BytesRef hash data may be recycled at some point
name|bucket
operator|.
name|termBytes
operator|=
name|BytesRef
operator|.
name|deepCopyOf
argument_list|(
name|bucket
operator|.
name|termBytes
argument_list|)
expr_stmt|;
name|bucket
operator|.
name|aggregations
operator|=
name|bucketAggregations
argument_list|(
name|bucket
operator|.
name|bucketOrd
argument_list|)
expr_stmt|;
name|list
index|[
name|i
index|]
operator|=
name|bucket
expr_stmt|;
block|}
return|return
operator|new
name|SignificantStringTerms
argument_list|(
name|name
argument_list|,
name|bucketCountThresholds
operator|.
name|getRequiredSize
argument_list|()
argument_list|,
name|bucketCountThresholds
operator|.
name|getMinDocCount
argument_list|()
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|metaData
argument_list|()
argument_list|,
name|format
argument_list|,
name|subsetSize
argument_list|,
name|supersetSize
argument_list|,
name|significanceHeuristic
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildEmptyAggregation
specifier|public
name|SignificantStringTerms
name|buildEmptyAggregation
parameter_list|()
block|{
comment|// We need to account for the significance of a miss in our global stats - provide corpus size as context
name|ContextIndexSearcher
name|searcher
init|=
name|context
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|IndexReader
name|topReader
init|=
name|searcher
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|int
name|supersetSize
init|=
name|topReader
operator|.
name|numDocs
argument_list|()
decl_stmt|;
return|return
operator|new
name|SignificantStringTerms
argument_list|(
name|name
argument_list|,
name|bucketCountThresholds
operator|.
name|getRequiredSize
argument_list|()
argument_list|,
name|bucketCountThresholds
operator|.
name|getMinDocCount
argument_list|()
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|metaData
argument_list|()
argument_list|,
name|format
argument_list|,
literal|0
argument_list|,
name|supersetSize
argument_list|,
name|significanceHeuristic
argument_list|,
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|public
name|void
name|doClose
parameter_list|()
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|bucketOrds
argument_list|,
name|termsAggFactory
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

