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
name|bucket
operator|.
name|terms
operator|.
name|LongTermsAggregator
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
name|AggregationContext
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
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueFormat
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
name|Collections
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SignificantLongTermsAggregator
specifier|public
class|class
name|SignificantLongTermsAggregator
extends|extends
name|LongTermsAggregator
block|{
DECL|method|SignificantLongTermsAggregator
specifier|public
name|SignificantLongTermsAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
parameter_list|,
annotation|@
name|Nullable
name|ValueFormat
name|format
parameter_list|,
name|long
name|estimatedBucketCount
parameter_list|,
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|SignificantTermsAggregatorFactory
name|termsAggFactory
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|valuesSource
argument_list|,
name|format
argument_list|,
name|estimatedBucketCount
argument_list|,
literal|null
argument_list|,
name|bucketCountThresholds
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|SubAggCollectionMode
operator|.
name|DEPTH_FIRST
argument_list|)
expr_stmt|;
name|this
operator|.
name|termsAggFactory
operator|=
name|termsAggFactory
expr_stmt|;
block|}
DECL|field|numCollectedDocs
specifier|protected
name|long
name|numCollectedDocs
decl_stmt|;
DECL|field|termsAggFactory
specifier|private
specifier|final
name|SignificantTermsAggregatorFactory
name|termsAggFactory
decl_stmt|;
annotation|@
name|Override
DECL|method|collect
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|,
name|long
name|owningBucketOrdinal
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
name|owningBucketOrdinal
argument_list|)
expr_stmt|;
name|numCollectedDocs
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildAggregation
specifier|public
name|SignificantLongTerms
name|buildAggregation
parameter_list|(
name|long
name|owningBucketOrdinal
parameter_list|)
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
name|prepareBackground
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|long
name|subsetSize
init|=
name|numCollectedDocs
decl_stmt|;
name|BucketSignificancePriorityQueue
name|ordered
init|=
operator|new
name|BucketSignificancePriorityQueue
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|SignificantLongTerms
operator|.
name|Bucket
name|spare
init|=
literal|null
decl_stmt|;
for|for
control|(
name|long
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
name|SignificantLongTerms
operator|.
name|Bucket
argument_list|(
literal|0
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
argument_list|)
expr_stmt|;
block|}
name|spare
operator|.
name|term
operator|=
name|bucketOrds
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|spare
operator|.
name|subsetDf
operator|=
name|bucketDocCount
argument_list|(
name|i
argument_list|)
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
name|term
argument_list|)
expr_stmt|;
name|spare
operator|.
name|supersetSize
operator|=
name|supersetSize
expr_stmt|;
assert|assert
name|spare
operator|.
name|subsetDf
operator|<=
name|spare
operator|.
name|supersetDf
assert|;
comment|// During shard-local down-selection we use subset/superset stats that are for this shard only
comment|// Back at the central reducer these properties will be updated with global stats
name|spare
operator|.
name|updateScore
argument_list|()
expr_stmt|;
name|spare
operator|.
name|bucketOrd
operator|=
name|i
expr_stmt|;
if|if
condition|(
name|spare
operator|.
name|subsetDf
operator|>=
name|bucketCountThresholds
operator|.
name|getShardMinDocCount
argument_list|()
condition|)
block|{
name|spare
operator|=
operator|(
name|SignificantLongTerms
operator|.
name|Bucket
operator|)
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
name|spare
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|InternalSignificantTerms
operator|.
name|Bucket
index|[]
name|list
init|=
operator|new
name|InternalSignificantTerms
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
name|SignificantLongTerms
operator|.
name|Bucket
name|bucket
init|=
operator|(
name|SignificantLongTerms
operator|.
name|Bucket
operator|)
name|ordered
operator|.
name|pop
argument_list|()
decl_stmt|;
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
name|SignificantLongTerms
argument_list|(
name|subsetSize
argument_list|,
name|supersetSize
argument_list|,
name|name
argument_list|,
name|formatter
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
name|SignificantLongTerms
name|buildEmptyAggregation
parameter_list|()
block|{
comment|// We need to account for the significance of a miss in our global stats - provide corpus size as context
name|ContextIndexSearcher
name|searcher
init|=
name|context
operator|.
name|searchContext
argument_list|()
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
name|SignificantLongTerms
argument_list|(
literal|0
argument_list|,
name|supersetSize
argument_list|,
name|name
argument_list|,
name|formatter
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
name|Collections
operator|.
expr|<
name|InternalSignificantTerms
operator|.
name|Bucket
operator|>
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

