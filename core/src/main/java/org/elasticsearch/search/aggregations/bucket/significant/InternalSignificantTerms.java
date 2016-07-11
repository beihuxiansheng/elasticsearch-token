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
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|ToXContent
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
name|Aggregations
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
name|InternalAggregations
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
name|InternalMultiBucketAggregation
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
name|pipeline
operator|.
name|PipelineAggregator
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalSignificantTerms
specifier|public
specifier|abstract
class|class
name|InternalSignificantTerms
parameter_list|<
name|A
extends|extends
name|InternalSignificantTerms
parameter_list|,
name|B
extends|extends
name|InternalSignificantTerms
operator|.
name|Bucket
parameter_list|>
extends|extends
name|InternalMultiBucketAggregation
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
implements|implements
name|SignificantTerms
implements|,
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|significanceHeuristic
specifier|protected
name|SignificanceHeuristic
name|significanceHeuristic
decl_stmt|;
DECL|field|requiredSize
specifier|protected
name|int
name|requiredSize
decl_stmt|;
DECL|field|minDocCount
specifier|protected
name|long
name|minDocCount
decl_stmt|;
DECL|field|buckets
specifier|protected
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
decl_stmt|;
DECL|field|bucketMap
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Bucket
argument_list|>
name|bucketMap
decl_stmt|;
DECL|field|subsetSize
specifier|protected
name|long
name|subsetSize
decl_stmt|;
DECL|field|supersetSize
specifier|protected
name|long
name|supersetSize
decl_stmt|;
DECL|method|InternalSignificantTerms
specifier|protected
name|InternalSignificantTerms
parameter_list|()
block|{}
comment|// for serialization
annotation|@
name|SuppressWarnings
argument_list|(
literal|"PMD.ConstructorCallsOverridableMethod"
argument_list|)
DECL|class|Bucket
specifier|public
specifier|abstract
specifier|static
class|class
name|Bucket
extends|extends
name|SignificantTerms
operator|.
name|Bucket
block|{
DECL|field|bucketOrd
name|long
name|bucketOrd
decl_stmt|;
DECL|field|aggregations
specifier|protected
name|InternalAggregations
name|aggregations
decl_stmt|;
DECL|field|score
name|double
name|score
decl_stmt|;
DECL|field|format
specifier|final
specifier|transient
name|DocValueFormat
name|format
decl_stmt|;
DECL|method|Bucket
specifier|protected
name|Bucket
parameter_list|(
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|DocValueFormat
name|format
parameter_list|)
block|{
comment|// for serialization
name|super
argument_list|(
name|subsetSize
argument_list|,
name|supersetSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
DECL|method|Bucket
specifier|protected
name|Bucket
parameter_list|(
name|long
name|subsetDf
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetDf
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|,
name|DocValueFormat
name|format
parameter_list|)
block|{
name|super
argument_list|(
name|subsetDf
argument_list|,
name|subsetSize
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|aggregations
operator|=
name|aggregations
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSubsetDf
specifier|public
name|long
name|getSubsetDf
parameter_list|()
block|{
return|return
name|subsetDf
return|;
block|}
annotation|@
name|Override
DECL|method|getSupersetDf
specifier|public
name|long
name|getSupersetDf
parameter_list|()
block|{
return|return
name|supersetDf
return|;
block|}
annotation|@
name|Override
DECL|method|getSupersetSize
specifier|public
name|long
name|getSupersetSize
parameter_list|()
block|{
return|return
name|supersetSize
return|;
block|}
annotation|@
name|Override
DECL|method|getSubsetSize
specifier|public
name|long
name|getSubsetSize
parameter_list|()
block|{
return|return
name|subsetSize
return|;
block|}
DECL|method|updateScore
specifier|public
name|void
name|updateScore
parameter_list|(
name|SignificanceHeuristic
name|significanceHeuristic
parameter_list|)
block|{
name|score
operator|=
name|significanceHeuristic
operator|.
name|getScore
argument_list|(
name|subsetDf
argument_list|,
name|subsetSize
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocCount
specifier|public
name|long
name|getDocCount
parameter_list|()
block|{
return|return
name|subsetDf
return|;
block|}
annotation|@
name|Override
DECL|method|getAggregations
specifier|public
name|Aggregations
name|getAggregations
parameter_list|()
block|{
return|return
name|aggregations
return|;
block|}
DECL|method|reduce
specifier|public
name|Bucket
name|reduce
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
parameter_list|,
name|ReduceContext
name|context
parameter_list|)
block|{
name|long
name|subsetDf
init|=
literal|0
decl_stmt|;
name|long
name|supersetDf
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|InternalAggregations
argument_list|>
name|aggregationsList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|subsetDf
operator|+=
name|bucket
operator|.
name|subsetDf
expr_stmt|;
name|supersetDf
operator|+=
name|bucket
operator|.
name|supersetDf
expr_stmt|;
name|aggregationsList
operator|.
name|add
argument_list|(
name|bucket
operator|.
name|aggregations
argument_list|)
expr_stmt|;
block|}
name|InternalAggregations
name|aggs
init|=
name|InternalAggregations
operator|.
name|reduce
argument_list|(
name|aggregationsList
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
name|newBucket
argument_list|(
name|subsetDf
argument_list|,
name|subsetSize
argument_list|,
name|supersetDf
argument_list|,
name|supersetSize
argument_list|,
name|aggs
argument_list|)
return|;
block|}
DECL|method|newBucket
specifier|abstract
name|Bucket
name|newBucket
parameter_list|(
name|long
name|subsetDf
parameter_list|,
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetDf
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|getSignificanceScore
specifier|public
name|double
name|getSignificanceScore
parameter_list|()
block|{
return|return
name|score
return|;
block|}
block|}
DECL|field|format
specifier|protected
name|DocValueFormat
name|format
decl_stmt|;
DECL|method|InternalSignificantTerms
specifier|protected
name|InternalSignificantTerms
parameter_list|(
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|String
name|name
parameter_list|,
name|DocValueFormat
name|format
parameter_list|,
name|int
name|requiredSize
parameter_list|,
name|long
name|minDocCount
parameter_list|,
name|SignificanceHeuristic
name|significanceHeuristic
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
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
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
name|minDocCount
operator|=
name|minDocCount
expr_stmt|;
name|this
operator|.
name|buckets
operator|=
name|buckets
expr_stmt|;
name|this
operator|.
name|subsetSize
operator|=
name|subsetSize
expr_stmt|;
name|this
operator|.
name|supersetSize
operator|=
name|supersetSize
expr_stmt|;
name|this
operator|.
name|significanceHeuristic
operator|=
name|significanceHeuristic
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|format
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|SignificantTerms
operator|.
name|Bucket
argument_list|>
name|iterator
parameter_list|()
block|{
name|Object
name|o
init|=
name|buckets
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|(
name|Iterator
argument_list|<
name|SignificantTerms
operator|.
name|Bucket
argument_list|>
operator|)
name|o
return|;
block|}
annotation|@
name|Override
DECL|method|getBuckets
specifier|public
name|List
argument_list|<
name|SignificantTerms
operator|.
name|Bucket
argument_list|>
name|getBuckets
parameter_list|()
block|{
name|Object
name|o
init|=
name|buckets
decl_stmt|;
return|return
operator|(
name|List
argument_list|<
name|SignificantTerms
operator|.
name|Bucket
argument_list|>
operator|)
name|o
return|;
block|}
annotation|@
name|Override
DECL|method|getBucketByKey
specifier|public
name|SignificantTerms
operator|.
name|Bucket
name|getBucketByKey
parameter_list|(
name|String
name|term
parameter_list|)
block|{
if|if
condition|(
name|bucketMap
operator|==
literal|null
condition|)
block|{
name|bucketMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|bucketMap
operator|.
name|put
argument_list|(
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|bucketMap
operator|.
name|get
argument_list|(
name|term
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
name|InternalAggregation
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|long
name|globalSubsetSize
init|=
literal|0
decl_stmt|;
name|long
name|globalSupersetSize
init|=
literal|0
decl_stmt|;
comment|// Compute the overall result set size and the corpus size using the
comment|// top-level Aggregations from each shard
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalSignificantTerms
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|terms
init|=
operator|(
name|InternalSignificantTerms
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
operator|)
name|aggregation
decl_stmt|;
name|globalSubsetSize
operator|+=
name|terms
operator|.
name|subsetSize
expr_stmt|;
name|globalSupersetSize
operator|+=
name|terms
operator|.
name|supersetSize
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|InternalSignificantTerms
operator|.
name|Bucket
argument_list|>
argument_list|>
name|buckets
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|InternalAggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
name|InternalSignificantTerms
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|terms
init|=
operator|(
name|InternalSignificantTerms
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
operator|)
name|aggregation
decl_stmt|;
for|for
control|(
name|Bucket
name|bucket
range|:
name|terms
operator|.
name|buckets
control|)
block|{
name|List
argument_list|<
name|Bucket
argument_list|>
name|existingBuckets
init|=
name|buckets
operator|.
name|get
argument_list|(
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingBuckets
operator|==
literal|null
condition|)
block|{
name|existingBuckets
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|aggregations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|buckets
operator|.
name|put
argument_list|(
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|,
name|existingBuckets
argument_list|)
expr_stmt|;
block|}
comment|// Adjust the buckets with the global stats representing the
comment|// total size of the pots from which the stats are drawn
name|existingBuckets
operator|.
name|add
argument_list|(
name|bucket
operator|.
name|newBucket
argument_list|(
name|bucket
operator|.
name|getSubsetDf
argument_list|()
argument_list|,
name|globalSubsetSize
argument_list|,
name|bucket
operator|.
name|getSupersetDf
argument_list|()
argument_list|,
name|globalSupersetSize
argument_list|,
name|bucket
operator|.
name|aggregations
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|significanceHeuristic
operator|.
name|initialize
argument_list|(
name|reduceContext
argument_list|)
expr_stmt|;
specifier|final
name|int
name|size
init|=
name|Math
operator|.
name|min
argument_list|(
name|requiredSize
argument_list|,
name|buckets
operator|.
name|size
argument_list|()
argument_list|)
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
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Bucket
argument_list|>
argument_list|>
name|entry
range|:
name|buckets
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|Bucket
argument_list|>
name|sameTermBuckets
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|Bucket
name|b
init|=
name|sameTermBuckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reduce
argument_list|(
name|sameTermBuckets
argument_list|,
name|reduceContext
argument_list|)
decl_stmt|;
name|b
operator|.
name|updateScore
argument_list|(
name|significanceHeuristic
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|.
name|score
operator|>
literal|0
operator|)
operator|&&
operator|(
name|b
operator|.
name|subsetDf
operator|>=
name|minDocCount
operator|)
condition|)
block|{
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
name|Bucket
index|[]
name|list
init|=
operator|new
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
name|list
index|[
name|i
index|]
operator|=
operator|(
name|Bucket
operator|)
name|ordered
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
return|return
name|create
argument_list|(
name|globalSubsetSize
argument_list|,
name|globalSupersetSize
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|list
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
DECL|method|create
specifier|protected
specifier|abstract
name|A
name|create
parameter_list|(
name|long
name|subsetSize
parameter_list|,
name|long
name|supersetSize
parameter_list|,
name|List
argument_list|<
name|InternalSignificantTerms
operator|.
name|Bucket
argument_list|>
name|buckets
parameter_list|,
name|InternalSignificantTerms
name|prototype
parameter_list|)
function_decl|;
block|}
end_class

end_unit

