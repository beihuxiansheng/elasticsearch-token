begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Version
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|Comparators
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
name|xcontent
operator|.
name|XContentBuilder
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
name|MultiBucketsAggregation
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
name|SingleBucketAggregator
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
name|Terms
operator|.
name|Bucket
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
name|Terms
operator|.
name|Order
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
name|metrics
operator|.
name|NumericMetricsAggregator
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
name|OrderPath
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalOrder
class|class
name|InternalOrder
extends|extends
name|Terms
operator|.
name|Order
block|{
DECL|field|COUNT_DESC_ID
specifier|private
specifier|static
specifier|final
name|byte
name|COUNT_DESC_ID
init|=
literal|1
decl_stmt|;
DECL|field|COUNT_ASC_ID
specifier|private
specifier|static
specifier|final
name|byte
name|COUNT_ASC_ID
init|=
literal|2
decl_stmt|;
DECL|field|TERM_DESC_ID
specifier|private
specifier|static
specifier|final
name|byte
name|TERM_DESC_ID
init|=
literal|3
decl_stmt|;
DECL|field|TERM_ASC_ID
specifier|private
specifier|static
specifier|final
name|byte
name|TERM_ASC_ID
init|=
literal|4
decl_stmt|;
comment|/**      * Order by the (higher) count of each term.      */
DECL|field|COUNT_DESC
specifier|public
specifier|static
specifier|final
name|InternalOrder
name|COUNT_DESC
init|=
operator|new
name|InternalOrder
argument_list|(
name|COUNT_DESC_ID
argument_list|,
literal|"_count"
argument_list|,
literal|false
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o2
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|o1
operator|.
name|getDocCount
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**      * Order by the (lower) count of each term.      */
DECL|field|COUNT_ASC
specifier|public
specifier|static
specifier|final
name|InternalOrder
name|COUNT_ASC
init|=
operator|new
name|InternalOrder
argument_list|(
name|COUNT_ASC_ID
argument_list|,
literal|"_count"
argument_list|,
literal|true
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|o2
operator|.
name|getDocCount
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**      * Order by the terms.      */
DECL|field|TERM_DESC
specifier|public
specifier|static
specifier|final
name|InternalOrder
name|TERM_DESC
init|=
operator|new
name|InternalOrder
argument_list|(
name|TERM_DESC_ID
argument_list|,
literal|"_term"
argument_list|,
literal|false
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
name|o2
operator|.
name|compareTerm
argument_list|(
name|o1
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|/**      * Order by the terms.      */
DECL|field|TERM_ASC
specifier|public
specifier|static
specifier|final
name|InternalOrder
name|TERM_ASC
init|=
operator|new
name|InternalOrder
argument_list|(
name|TERM_ASC_ID
argument_list|,
literal|"_term"
argument_list|,
literal|true
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
return|return
name|o1
operator|.
name|compareTerm
argument_list|(
name|o2
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
DECL|method|isCountDesc
specifier|public
specifier|static
name|boolean
name|isCountDesc
parameter_list|(
name|Terms
operator|.
name|Order
name|order
parameter_list|)
block|{
if|if
condition|(
name|order
operator|==
name|COUNT_DESC
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|order
operator|instanceof
name|CompoundOrder
condition|)
block|{
comment|// check if its a compound order with count desc and the tie breaker (term asc)
name|CompoundOrder
name|compoundOrder
init|=
operator|(
name|CompoundOrder
operator|)
name|order
decl_stmt|;
if|if
condition|(
name|compoundOrder
operator|.
name|orderElements
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|&&
name|compoundOrder
operator|.
name|orderElements
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|COUNT_DESC
operator|&&
name|compoundOrder
operator|.
name|orderElements
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|==
name|TERM_ASC
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|field|id
specifier|final
name|byte
name|id
decl_stmt|;
DECL|field|key
specifier|final
name|String
name|key
decl_stmt|;
DECL|field|asc
specifier|final
name|boolean
name|asc
decl_stmt|;
DECL|field|comparator
specifier|protected
specifier|final
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|comparator
decl_stmt|;
DECL|method|InternalOrder
name|InternalOrder
parameter_list|(
name|byte
name|id
parameter_list|,
name|String
name|key
parameter_list|,
name|boolean
name|asc
parameter_list|,
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|comparator
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
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|asc
operator|=
name|asc
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
DECL|method|id
name|byte
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
DECL|method|comparator
specifier|protected
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|comparator
parameter_list|(
name|Aggregator
name|aggregator
parameter_list|)
block|{
return|return
name|comparator
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|key
argument_list|,
name|asc
condition|?
literal|"asc"
else|:
literal|"desc"
argument_list|)
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|method|validate
specifier|public
specifier|static
name|Terms
operator|.
name|Order
name|validate
parameter_list|(
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|Aggregator
name|termsAggregator
parameter_list|)
block|{
if|if
condition|(
name|order
operator|instanceof
name|CompoundOrder
condition|)
block|{
for|for
control|(
name|Terms
operator|.
name|Order
name|innerOrder
range|:
operator|(
operator|(
name|CompoundOrder
operator|)
name|order
operator|)
operator|.
name|orderElements
control|)
block|{
name|validate
argument_list|(
name|innerOrder
argument_list|,
name|termsAggregator
argument_list|)
expr_stmt|;
block|}
return|return
name|order
return|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|order
operator|instanceof
name|Aggregation
operator|)
condition|)
block|{
return|return
name|order
return|;
block|}
name|OrderPath
name|path
init|=
operator|(
operator|(
name|Aggregation
operator|)
name|order
operator|)
operator|.
name|path
argument_list|()
decl_stmt|;
name|path
operator|.
name|validate
argument_list|(
name|termsAggregator
argument_list|)
expr_stmt|;
return|return
name|order
return|;
block|}
DECL|class|Aggregation
specifier|static
class|class
name|Aggregation
extends|extends
name|InternalOrder
block|{
DECL|field|ID
specifier|static
specifier|final
name|byte
name|ID
init|=
literal|0
decl_stmt|;
DECL|method|Aggregation
name|Aggregation
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|asc
parameter_list|)
block|{
name|super
argument_list|(
name|ID
argument_list|,
name|key
argument_list|,
name|asc
argument_list|,
operator|new
name|MultiBucketsAggregation
operator|.
name|Bucket
operator|.
name|SubAggregationComparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|(
name|key
argument_list|,
name|asc
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|path
name|OrderPath
name|path
parameter_list|()
block|{
return|return
operator|(
operator|(
name|MultiBucketsAggregation
operator|.
name|Bucket
operator|.
name|SubAggregationComparator
operator|)
name|comparator
operator|)
operator|.
name|path
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|comparator
specifier|protected
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|comparator
parameter_list|(
name|Aggregator
name|termsAggregator
parameter_list|)
block|{
if|if
condition|(
name|termsAggregator
operator|==
literal|null
condition|)
block|{
return|return
name|comparator
return|;
block|}
comment|// Internal Optimization:
comment|//
comment|// in this phase, if the order is based on sub-aggregations, we need to use a different comparator
comment|// to avoid constructing buckets for ordering purposes (we can potentially have a lot of buckets and building
comment|// them will cause loads of redundant object constructions). The "special" comparators here will fetch the
comment|// sub aggregation values directly from the sub aggregators bypassing bucket creation. Note that the comparator
comment|// attached to the order will still be used in the reduce phase of the Aggregation.
name|OrderPath
name|path
init|=
name|path
argument_list|()
decl_stmt|;
specifier|final
name|Aggregator
name|aggregator
init|=
name|path
operator|.
name|resolveAggregator
argument_list|(
name|termsAggregator
argument_list|)
decl_stmt|;
specifier|final
name|String
name|key
init|=
name|path
operator|.
name|tokens
index|[
name|path
operator|.
name|tokens
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|key
decl_stmt|;
if|if
condition|(
name|aggregator
operator|instanceof
name|SingleBucketAggregator
condition|)
block|{
assert|assert
name|key
operator|==
literal|null
operator|:
literal|"this should be picked up before the aggregation is executed - on validate"
assert|;
return|return
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
name|int
name|mul
init|=
name|asc
condition|?
literal|1
else|:
operator|-
literal|1
decl_stmt|;
name|int
name|v1
init|=
operator|(
operator|(
name|SingleBucketAggregator
operator|)
name|aggregator
operator|)
operator|.
name|bucketDocCount
argument_list|(
operator|(
operator|(
name|InternalTerms
operator|.
name|Bucket
operator|)
name|o1
operator|)
operator|.
name|bucketOrd
argument_list|)
decl_stmt|;
name|int
name|v2
init|=
operator|(
operator|(
name|SingleBucketAggregator
operator|)
name|aggregator
operator|)
operator|.
name|bucketDocCount
argument_list|(
operator|(
operator|(
name|InternalTerms
operator|.
name|Bucket
operator|)
name|o2
operator|)
operator|.
name|bucketOrd
argument_list|)
decl_stmt|;
return|return
name|mul
operator|*
operator|(
name|v1
operator|-
name|v2
operator|)
return|;
block|}
block|}
return|;
block|}
comment|// with only support single-bucket aggregators
assert|assert
operator|!
operator|(
name|aggregator
operator|instanceof
name|BucketsAggregator
operator|)
operator|:
literal|"this should be picked up before the aggregation is executed - on validate"
assert|;
if|if
condition|(
name|aggregator
operator|instanceof
name|NumericMetricsAggregator
operator|.
name|MultiValue
condition|)
block|{
assert|assert
name|key
operator|!=
literal|null
operator|:
literal|"this should be picked up before the aggregation is executed - on validate"
assert|;
return|return
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
name|double
name|v1
init|=
operator|(
operator|(
name|NumericMetricsAggregator
operator|.
name|MultiValue
operator|)
name|aggregator
operator|)
operator|.
name|metric
argument_list|(
name|key
argument_list|,
operator|(
operator|(
name|InternalTerms
operator|.
name|Bucket
operator|)
name|o1
operator|)
operator|.
name|bucketOrd
argument_list|)
decl_stmt|;
name|double
name|v2
init|=
operator|(
operator|(
name|NumericMetricsAggregator
operator|.
name|MultiValue
operator|)
name|aggregator
operator|)
operator|.
name|metric
argument_list|(
name|key
argument_list|,
operator|(
operator|(
name|InternalTerms
operator|.
name|Bucket
operator|)
name|o2
operator|)
operator|.
name|bucketOrd
argument_list|)
decl_stmt|;
comment|// some metrics may return NaN (eg. avg, variance, etc...) in which case we'd like to push all of those to
comment|// the bottom
return|return
name|Comparators
operator|.
name|compareDiscardNaN
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|,
name|asc
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|// single-value metrics agg
return|return
operator|new
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Terms
operator|.
name|Bucket
name|o1
parameter_list|,
name|Terms
operator|.
name|Bucket
name|o2
parameter_list|)
block|{
name|double
name|v1
init|=
operator|(
operator|(
name|NumericMetricsAggregator
operator|.
name|SingleValue
operator|)
name|aggregator
operator|)
operator|.
name|metric
argument_list|(
operator|(
operator|(
name|InternalTerms
operator|.
name|Bucket
operator|)
name|o1
operator|)
operator|.
name|bucketOrd
argument_list|)
decl_stmt|;
name|double
name|v2
init|=
operator|(
operator|(
name|NumericMetricsAggregator
operator|.
name|SingleValue
operator|)
name|aggregator
operator|)
operator|.
name|metric
argument_list|(
operator|(
operator|(
name|InternalTerms
operator|.
name|Bucket
operator|)
name|o2
operator|)
operator|.
name|bucketOrd
argument_list|)
decl_stmt|;
comment|// some metrics may return NaN (eg. avg, variance, etc...) in which case we'd like to push all of those to
comment|// the bottom
return|return
name|Comparators
operator|.
name|compareDiscardNaN
argument_list|(
name|v1
argument_list|,
name|v2
argument_list|,
name|asc
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
DECL|class|CompoundOrder
specifier|static
class|class
name|CompoundOrder
extends|extends
name|Terms
operator|.
name|Order
block|{
DECL|field|ID
specifier|static
specifier|final
name|byte
name|ID
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|orderElements
specifier|private
specifier|final
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|orderElements
decl_stmt|;
DECL|method|CompoundOrder
specifier|public
name|CompoundOrder
parameter_list|(
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|compoundOrder
parameter_list|)
block|{
name|this
argument_list|(
name|compoundOrder
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|CompoundOrder
specifier|public
name|CompoundOrder
parameter_list|(
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|compoundOrder
parameter_list|,
name|boolean
name|absoluteOrdering
parameter_list|)
block|{
name|this
operator|.
name|orderElements
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|(
name|compoundOrder
argument_list|)
expr_stmt|;
name|Terms
operator|.
name|Order
name|lastElement
init|=
name|compoundOrder
operator|.
name|get
argument_list|(
name|compoundOrder
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|absoluteOrdering
operator|&&
operator|!
operator|(
name|InternalOrder
operator|.
name|TERM_ASC
operator|==
name|lastElement
operator|||
name|InternalOrder
operator|.
name|TERM_DESC
operator|==
name|lastElement
operator|)
condition|)
block|{
comment|// add term order ascending as a tie-breaker to avoid non-deterministic ordering
comment|// if all user provided comparators return 0.
name|this
operator|.
name|orderElements
operator|.
name|add
argument_list|(
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|id
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
DECL|method|orderElements
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|orderElements
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|orderElements
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
for|for
control|(
name|Terms
operator|.
name|Order
name|order
range|:
name|orderElements
control|)
block|{
name|order
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|endArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|comparator
specifier|protected
name|Comparator
argument_list|<
name|Bucket
argument_list|>
name|comparator
parameter_list|(
name|Aggregator
name|aggregator
parameter_list|)
block|{
return|return
operator|new
name|CompoundOrderComparator
argument_list|(
name|orderElements
argument_list|,
name|aggregator
argument_list|)
return|;
block|}
DECL|class|CompoundOrderComparator
specifier|public
specifier|static
class|class
name|CompoundOrderComparator
implements|implements
name|Comparator
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
block|{
DECL|field|compoundOrder
specifier|private
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|compoundOrder
decl_stmt|;
DECL|field|aggregator
specifier|private
name|Aggregator
name|aggregator
decl_stmt|;
DECL|method|CompoundOrderComparator
specifier|public
name|CompoundOrderComparator
parameter_list|(
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|compoundOrder
parameter_list|,
name|Aggregator
name|aggregator
parameter_list|)
block|{
name|this
operator|.
name|compoundOrder
operator|=
name|compoundOrder
expr_stmt|;
name|this
operator|.
name|aggregator
operator|=
name|aggregator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compare
specifier|public
name|int
name|compare
parameter_list|(
name|Bucket
name|o1
parameter_list|,
name|Bucket
name|o2
parameter_list|)
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|itr
init|=
name|compoundOrder
operator|.
name|iterator
argument_list|()
init|;
name|itr
operator|.
name|hasNext
argument_list|()
operator|&&
name|result
operator|==
literal|0
condition|;
control|)
block|{
name|result
operator|=
name|itr
operator|.
name|next
argument_list|()
operator|.
name|comparator
argument_list|(
name|aggregator
argument_list|)
operator|.
name|compare
argument_list|(
name|o1
argument_list|,
name|o2
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
block|}
DECL|class|Streams
specifier|public
specifier|static
class|class
name|Streams
block|{
DECL|method|writeOrder
specifier|public
specifier|static
name|void
name|writeOrder
parameter_list|(
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|order
operator|instanceof
name|Aggregation
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|order
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|Aggregation
name|aggregationOrder
init|=
operator|(
name|Aggregation
operator|)
name|order
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
operator|(
operator|(
name|MultiBucketsAggregation
operator|.
name|Bucket
operator|.
name|SubAggregationComparator
operator|)
name|aggregationOrder
operator|.
name|comparator
operator|)
operator|.
name|asc
argument_list|()
argument_list|)
expr_stmt|;
name|OrderPath
name|path
init|=
operator|(
operator|(
name|Aggregation
operator|)
name|order
operator|)
operator|.
name|path
argument_list|()
decl_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_1_0
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// prev versions only supported sorting on a single level -> a single token;
name|OrderPath
operator|.
name|Token
name|token
init|=
name|path
operator|.
name|lastToken
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|token
operator|.
name|name
argument_list|)
expr_stmt|;
name|boolean
name|hasValueName
init|=
name|token
operator|.
name|key
operator|!=
literal|null
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasValueName
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasValueName
condition|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|token
operator|.
name|key
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|order
operator|instanceof
name|CompoundOrder
condition|)
block|{
name|CompoundOrder
name|compoundOrder
init|=
operator|(
name|CompoundOrder
operator|)
name|order
decl_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|order
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|compoundOrder
operator|.
name|orderElements
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Terms
operator|.
name|Order
name|innerOrder
range|:
name|compoundOrder
operator|.
name|orderElements
control|)
block|{
name|Streams
operator|.
name|writeOrder
argument_list|(
name|innerOrder
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|order
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|readOrder
specifier|public
specifier|static
name|Terms
operator|.
name|Order
name|readOrder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readOrder
argument_list|(
name|in
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|readOrder
specifier|public
specifier|static
name|Terms
operator|.
name|Order
name|readOrder
parameter_list|(
name|StreamInput
name|in
parameter_list|,
name|boolean
name|absoluteOrder
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|id
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|id
condition|)
block|{
case|case
name|COUNT_DESC_ID
case|:
return|return
name|absoluteOrder
condition|?
operator|new
name|CompoundOrder
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|(
name|Terms
operator|.
name|Order
operator|)
name|InternalOrder
operator|.
name|COUNT_DESC
argument_list|)
argument_list|)
else|:
name|InternalOrder
operator|.
name|COUNT_DESC
return|;
case|case
name|COUNT_ASC_ID
case|:
return|return
name|absoluteOrder
condition|?
operator|new
name|CompoundOrder
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
operator|(
name|Terms
operator|.
name|Order
operator|)
name|InternalOrder
operator|.
name|COUNT_ASC
argument_list|)
argument_list|)
else|:
name|InternalOrder
operator|.
name|COUNT_ASC
return|;
case|case
name|TERM_DESC_ID
case|:
return|return
name|InternalOrder
operator|.
name|TERM_DESC
return|;
case|case
name|TERM_ASC_ID
case|:
return|return
name|InternalOrder
operator|.
name|TERM_ASC
return|;
case|case
name|Aggregation
operator|.
name|ID
case|:
name|boolean
name|asc
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_1_1_0
argument_list|)
condition|)
block|{
return|return
operator|new
name|InternalOrder
operator|.
name|Aggregation
argument_list|(
name|key
argument_list|,
name|asc
argument_list|)
return|;
block|}
name|boolean
name|hasValueNmae
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|hasValueNmae
condition|)
block|{
return|return
operator|new
name|InternalOrder
operator|.
name|Aggregation
argument_list|(
name|key
operator|+
literal|"."
operator|+
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|asc
argument_list|)
return|;
block|}
name|Terms
operator|.
name|Order
name|order
init|=
operator|new
name|InternalOrder
operator|.
name|Aggregation
argument_list|(
name|key
argument_list|,
name|asc
argument_list|)
decl_stmt|;
return|return
name|absoluteOrder
condition|?
operator|new
name|CompoundOrder
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|order
argument_list|)
argument_list|)
else|:
name|order
return|;
case|case
name|CompoundOrder
operator|.
name|ID
case|:
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Terms
operator|.
name|Order
argument_list|>
name|compoundOrder
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|compoundOrder
operator|.
name|add
argument_list|(
name|Streams
operator|.
name|readOrder
argument_list|(
name|in
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CompoundOrder
argument_list|(
name|compoundOrder
argument_list|,
name|absoluteOrder
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unknown terms order"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

