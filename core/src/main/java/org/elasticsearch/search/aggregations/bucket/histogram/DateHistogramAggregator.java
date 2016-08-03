begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.histogram
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
name|histogram
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
name|index
operator|.
name|SortedNumericDocValues
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
name|CollectionUtil
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
name|internal
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
name|common
operator|.
name|rounding
operator|.
name|Rounding
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
name|LongHash
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
name|InternalAggregation
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
name|BucketsAggregator
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
name|ArrayList
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
comment|/**  * An aggregator for date values. Every date is rounded down using a configured  * {@link Rounding}.  *   * @see Rounding  */
end_comment

begin_class
DECL|class|DateHistogramAggregator
class|class
name|DateHistogramAggregator
extends|extends
name|BucketsAggregator
block|{
DECL|field|valuesSource
specifier|private
specifier|final
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
decl_stmt|;
DECL|field|formatter
specifier|private
specifier|final
name|DocValueFormat
name|formatter
decl_stmt|;
DECL|field|rounding
specifier|private
specifier|final
name|Rounding
name|rounding
decl_stmt|;
DECL|field|order
specifier|private
specifier|final
name|InternalOrder
name|order
decl_stmt|;
DECL|field|keyed
specifier|private
specifier|final
name|boolean
name|keyed
decl_stmt|;
DECL|field|minDocCount
specifier|private
specifier|final
name|long
name|minDocCount
decl_stmt|;
DECL|field|extendedBounds
specifier|private
specifier|final
name|ExtendedBounds
name|extendedBounds
decl_stmt|;
DECL|field|bucketOrds
specifier|private
specifier|final
name|LongHash
name|bucketOrds
decl_stmt|;
DECL|method|DateHistogramAggregator
specifier|public
name|DateHistogramAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|Rounding
name|rounding
parameter_list|,
name|InternalOrder
name|order
parameter_list|,
name|boolean
name|keyed
parameter_list|,
name|long
name|minDocCount
parameter_list|,
annotation|@
name|Nullable
name|ExtendedBounds
name|extendedBounds
parameter_list|,
annotation|@
name|Nullable
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
parameter_list|,
name|DocValueFormat
name|formatter
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
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
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|rounding
operator|=
name|rounding
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|keyed
operator|=
name|keyed
expr_stmt|;
name|this
operator|.
name|minDocCount
operator|=
name|minDocCount
expr_stmt|;
name|this
operator|.
name|extendedBounds
operator|=
name|extendedBounds
expr_stmt|;
name|this
operator|.
name|valuesSource
operator|=
name|valuesSource
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|bucketOrds
operator|=
operator|new
name|LongHash
argument_list|(
literal|1
argument_list|,
name|aggregationContext
operator|.
name|bigArrays
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|needsScores
specifier|public
name|boolean
name|needsScores
parameter_list|()
block|{
return|return
operator|(
name|valuesSource
operator|!=
literal|null
operator|&&
name|valuesSource
operator|.
name|needsScores
argument_list|()
operator|)
operator|||
name|super
operator|.
name|needsScores
argument_list|()
return|;
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
if|if
condition|(
name|valuesSource
operator|==
literal|null
condition|)
block|{
return|return
name|LeafBucketCollector
operator|.
name|NO_OP_COLLECTOR
return|;
block|}
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|valuesSource
operator|.
name|longValues
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
return|return
operator|new
name|LeafBucketCollectorBase
argument_list|(
name|sub
argument_list|,
name|values
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
assert|assert
name|bucket
operator|==
literal|0
assert|;
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|valuesCount
init|=
name|values
operator|.
name|count
argument_list|()
decl_stmt|;
name|long
name|previousRounded
init|=
name|Long
operator|.
name|MIN_VALUE
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
name|valuesCount
condition|;
operator|++
name|i
control|)
block|{
name|long
name|value
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|long
name|rounded
init|=
name|rounding
operator|.
name|round
argument_list|(
name|value
argument_list|)
decl_stmt|;
assert|assert
name|rounded
operator|>=
name|previousRounded
assert|;
if|if
condition|(
name|rounded
operator|==
name|previousRounded
condition|)
block|{
continue|continue;
block|}
name|long
name|bucketOrd
init|=
name|bucketOrds
operator|.
name|add
argument_list|(
name|rounded
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketOrd
operator|<
literal|0
condition|)
block|{
comment|// already seen
name|bucketOrd
operator|=
operator|-
literal|1
operator|-
name|bucketOrd
expr_stmt|;
name|collectExistingBucket
argument_list|(
name|sub
argument_list|,
name|doc
argument_list|,
name|bucketOrd
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|collectBucket
argument_list|(
name|sub
argument_list|,
name|doc
argument_list|,
name|bucketOrd
argument_list|)
expr_stmt|;
block|}
name|previousRounded
operator|=
name|rounded
expr_stmt|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|buildAggregation
specifier|public
name|InternalAggregation
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
name|List
argument_list|<
name|InternalDateHistogram
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
operator|(
name|int
operator|)
name|bucketOrds
operator|.
name|size
argument_list|()
argument_list|)
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
name|buckets
operator|.
name|add
argument_list|(
operator|new
name|InternalDateHistogram
operator|.
name|Bucket
argument_list|(
name|bucketOrds
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|bucketDocCount
argument_list|(
name|i
argument_list|)
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|,
name|bucketAggregations
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// the contract of the histogram aggregation is that shards must return buckets ordered by key in ascending order
name|CollectionUtil
operator|.
name|introSort
argument_list|(
name|buckets
argument_list|,
name|InternalOrder
operator|.
name|KEY_ASC
operator|.
name|comparator
argument_list|()
argument_list|)
expr_stmt|;
comment|// value source will be null for unmapped fields
name|InternalDateHistogram
operator|.
name|EmptyBucketInfo
name|emptyBucketInfo
init|=
name|minDocCount
operator|==
literal|0
condition|?
operator|new
name|InternalDateHistogram
operator|.
name|EmptyBucketInfo
argument_list|(
name|rounding
argument_list|,
name|buildEmptySubAggregations
argument_list|()
argument_list|,
name|extendedBounds
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|InternalDateHistogram
argument_list|(
name|name
argument_list|,
name|buckets
argument_list|,
name|order
argument_list|,
name|minDocCount
argument_list|,
name|emptyBucketInfo
argument_list|,
name|formatter
argument_list|,
name|keyed
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|metaData
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|buildEmptyAggregation
specifier|public
name|InternalAggregation
name|buildEmptyAggregation
parameter_list|()
block|{
name|InternalDateHistogram
operator|.
name|EmptyBucketInfo
name|emptyBucketInfo
init|=
name|minDocCount
operator|==
literal|0
condition|?
operator|new
name|InternalDateHistogram
operator|.
name|EmptyBucketInfo
argument_list|(
name|rounding
argument_list|,
name|buildEmptySubAggregations
argument_list|()
argument_list|,
name|extendedBounds
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|InternalDateHistogram
argument_list|(
name|name
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|,
name|order
argument_list|,
name|minDocCount
argument_list|,
name|emptyBucketInfo
argument_list|,
name|formatter
argument_list|,
name|keyed
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|metaData
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
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

