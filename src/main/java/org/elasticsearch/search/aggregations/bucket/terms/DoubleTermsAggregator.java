begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
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
name|terms
package|;
end_package

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
name|index
operator|.
name|fielddata
operator|.
name|DoubleValues
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
name|bucket
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
name|aggregations
operator|.
name|bucket
operator|.
name|terms
operator|.
name|support
operator|.
name|BucketPriorityQueue
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
name|numeric
operator|.
name|NumericValuesSource
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
DECL|class|DoubleTermsAggregator
specifier|public
class|class
name|DoubleTermsAggregator
extends|extends
name|BucketsAggregator
block|{
DECL|field|order
specifier|private
specifier|final
name|InternalOrder
name|order
decl_stmt|;
DECL|field|requiredSize
specifier|private
specifier|final
name|int
name|requiredSize
decl_stmt|;
DECL|field|shardSize
specifier|private
specifier|final
name|int
name|shardSize
decl_stmt|;
DECL|field|valuesSource
specifier|private
specifier|final
name|NumericValuesSource
name|valuesSource
decl_stmt|;
DECL|field|bucketOrds
specifier|private
specifier|final
name|LongHash
name|bucketOrds
decl_stmt|;
DECL|method|DoubleTermsAggregator
specifier|public
name|DoubleTermsAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|NumericValuesSource
name|valuesSource
parameter_list|,
name|long
name|estimatedBucketCount
parameter_list|,
name|InternalOrder
name|order
parameter_list|,
name|int
name|requiredSize
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|BucketAggregationMode
operator|.
name|PER_BUCKET
argument_list|,
name|factories
argument_list|,
name|estimatedBucketCount
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|valuesSource
operator|=
name|valuesSource
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|InternalOrder
operator|.
name|validate
argument_list|(
name|order
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredSize
operator|=
name|requiredSize
expr_stmt|;
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
name|bucketOrds
operator|=
operator|new
name|LongHash
argument_list|(
name|estimatedBucketCount
argument_list|,
name|aggregationContext
operator|.
name|pageCacheRecycler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shouldCollect
specifier|public
name|boolean
name|shouldCollect
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
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
assert|assert
name|owningBucketOrdinal
operator|==
literal|0
assert|;
specifier|final
name|DoubleValues
name|values
init|=
name|valuesSource
operator|.
name|doubleValues
argument_list|()
decl_stmt|;
specifier|final
name|int
name|valuesCount
init|=
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
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
specifier|final
name|double
name|val
init|=
name|values
operator|.
name|nextValue
argument_list|()
decl_stmt|;
specifier|final
name|long
name|bits
init|=
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|long
name|bucketOrdinal
init|=
name|bucketOrds
operator|.
name|add
argument_list|(
name|bits
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketOrdinal
operator|<
literal|0
condition|)
block|{
comment|// already seen
name|bucketOrdinal
operator|=
operator|-
literal|1
operator|-
name|bucketOrdinal
expr_stmt|;
block|}
name|collectBucket
argument_list|(
name|doc
argument_list|,
name|bucketOrdinal
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|buildAggregation
specifier|public
name|DoubleTerms
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
name|shardSize
argument_list|)
decl_stmt|;
name|BucketPriorityQueue
name|ordered
init|=
operator|new
name|BucketPriorityQueue
argument_list|(
name|size
argument_list|,
name|order
operator|.
name|comparator
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|DoubleTerms
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
name|capacity
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|long
name|ord
init|=
name|bucketOrds
operator|.
name|id
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|<
literal|0
condition|)
block|{
comment|// slot is not allocated
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
name|DoubleTerms
operator|.
name|Bucket
argument_list|(
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
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|bucketOrds
operator|.
name|key
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|spare
operator|.
name|docCount
operator|=
name|bucketDocCount
argument_list|(
name|ord
argument_list|)
expr_stmt|;
name|spare
operator|.
name|bucketOrd
operator|=
name|ord
expr_stmt|;
name|spare
operator|=
operator|(
name|DoubleTerms
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
specifier|final
name|InternalTerms
operator|.
name|Bucket
index|[]
name|list
init|=
operator|new
name|InternalTerms
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
operator|--
name|i
control|)
block|{
specifier|final
name|DoubleTerms
operator|.
name|Bucket
name|bucket
init|=
operator|(
name|DoubleTerms
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
name|DoubleTerms
argument_list|(
name|name
argument_list|,
name|order
argument_list|,
name|valuesSource
operator|.
name|formatter
argument_list|()
argument_list|,
name|requiredSize
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
name|DoubleTerms
name|buildEmptyAggregation
parameter_list|()
block|{
return|return
operator|new
name|DoubleTerms
argument_list|(
name|name
argument_list|,
name|order
argument_list|,
name|valuesSource
operator|.
name|formatter
argument_list|()
argument_list|,
name|requiredSize
argument_list|,
name|Collections
operator|.
expr|<
name|InternalTerms
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
DECL|method|doRelease
specifier|public
name|void
name|doRelease
parameter_list|()
block|{
name|Releasables
operator|.
name|release
argument_list|(
name|bucketOrds
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

