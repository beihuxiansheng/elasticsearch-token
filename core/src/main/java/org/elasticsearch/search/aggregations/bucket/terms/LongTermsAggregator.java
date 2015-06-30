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
name|bucket
operator|.
name|terms
operator|.
name|support
operator|.
name|IncludeExclude
operator|.
name|LongFilter
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
name|aggregations
operator|.
name|support
operator|.
name|format
operator|.
name|ValueFormatter
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
comment|/**  *  */
end_comment

begin_class
DECL|class|LongTermsAggregator
specifier|public
class|class
name|LongTermsAggregator
extends|extends
name|TermsAggregator
block|{
DECL|field|valuesSource
specifier|protected
specifier|final
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
decl_stmt|;
DECL|field|formatter
specifier|protected
specifier|final
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|field|bucketOrds
specifier|protected
specifier|final
name|LongHash
name|bucketOrds
decl_stmt|;
DECL|field|showTermDocCountError
specifier|private
name|boolean
name|showTermDocCountError
decl_stmt|;
DECL|field|longFilter
specifier|private
name|LongFilter
name|longFilter
decl_stmt|;
DECL|method|LongTermsAggregator
specifier|public
name|LongTermsAggregator
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
name|ValueFormat
name|format
parameter_list|,
name|Terms
operator|.
name|Order
name|order
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
name|SubAggCollectionMode
name|subAggCollectMode
parameter_list|,
name|boolean
name|showTermDocCountError
parameter_list|,
name|IncludeExclude
operator|.
name|LongFilter
name|longFilter
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
name|bucketCountThresholds
argument_list|,
name|order
argument_list|,
name|subAggCollectMode
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
name|showTermDocCountError
operator|=
name|showTermDocCountError
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|format
operator|.
name|formatter
argument_list|()
expr_stmt|;
name|this
operator|.
name|longFilter
operator|=
name|longFilter
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
DECL|method|getValues
specifier|protected
name|SortedNumericDocValues
name|getValues
parameter_list|(
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
parameter_list|,
name|LeafReaderContext
name|ctx
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|valuesSource
operator|.
name|longValues
argument_list|(
name|ctx
argument_list|)
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
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|getValues
argument_list|(
name|valuesSource
argument_list|,
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
name|previous
init|=
name|Long
operator|.
name|MAX_VALUE
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
name|long
name|val
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
name|val
operator|||
name|i
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|(
name|longFilter
operator|==
literal|null
operator|)
operator|||
operator|(
name|longFilter
operator|.
name|accept
argument_list|(
name|val
argument_list|)
operator|)
condition|)
block|{
name|long
name|bucketOrdinal
init|=
name|bucketOrds
operator|.
name|add
argument_list|(
name|val
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
name|collectExistingBucket
argument_list|(
name|sub
argument_list|,
name|doc
argument_list|,
name|bucketOrdinal
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
name|bucketOrdinal
argument_list|)
expr_stmt|;
block|}
block|}
name|previous
operator|=
name|val
expr_stmt|;
block|}
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
if|if
condition|(
name|bucketCountThresholds
operator|.
name|getMinDocCount
argument_list|()
operator|==
literal|0
operator|&&
operator|(
name|order
operator|!=
name|InternalOrder
operator|.
name|COUNT_DESC
operator|||
name|bucketOrds
operator|.
name|size
argument_list|()
operator|<
name|bucketCountThresholds
operator|.
name|getRequiredSize
argument_list|()
operator|)
condition|)
block|{
comment|// we need to fill-in the blanks
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|context
operator|.
name|searchContext
argument_list|()
operator|.
name|searcher
argument_list|()
operator|.
name|getTopReaderContext
argument_list|()
operator|.
name|leaves
argument_list|()
control|)
block|{
specifier|final
name|SortedNumericDocValues
name|values
init|=
name|getValues
argument_list|(
name|valuesSource
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|docId
init|=
literal|0
init|;
name|docId
operator|<
name|ctx
operator|.
name|reader
argument_list|()
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|docId
control|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
specifier|final
name|int
name|valueCount
init|=
name|values
operator|.
name|count
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
name|valueCount
condition|;
operator|++
name|i
control|)
block|{
name|bucketOrds
operator|.
name|add
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
name|otherDocCount
init|=
literal|0
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
name|LongTerms
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
name|LongTerms
operator|.
name|Bucket
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|showTermDocCountError
argument_list|,
literal|0
argument_list|,
name|formatter
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
name|docCount
operator|=
name|bucketDocCount
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|otherDocCount
operator|+=
name|spare
operator|.
name|docCount
expr_stmt|;
name|spare
operator|.
name|bucketOrd
operator|=
name|i
expr_stmt|;
if|if
condition|(
name|bucketCountThresholds
operator|.
name|getShardMinDocCount
argument_list|()
operator|<=
name|spare
operator|.
name|docCount
condition|)
block|{
name|spare
operator|=
operator|(
name|LongTerms
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
comment|// Get the top buckets
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
name|long
name|survivingBucketOrds
index|[]
init|=
operator|new
name|long
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
name|LongTerms
operator|.
name|Bucket
name|bucket
init|=
operator|(
name|LongTerms
operator|.
name|Bucket
operator|)
name|ordered
operator|.
name|pop
argument_list|()
decl_stmt|;
name|survivingBucketOrds
index|[
name|i
index|]
operator|=
name|bucket
operator|.
name|bucketOrd
expr_stmt|;
name|list
index|[
name|i
index|]
operator|=
name|bucket
expr_stmt|;
name|otherDocCount
operator|-=
name|bucket
operator|.
name|docCount
expr_stmt|;
block|}
name|runDeferredCollections
argument_list|(
name|survivingBucketOrds
argument_list|)
expr_stmt|;
comment|//Now build the aggs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|list
index|[
name|i
index|]
operator|.
name|aggregations
operator|=
name|bucketAggregations
argument_list|(
name|list
index|[
name|i
index|]
operator|.
name|bucketOrd
argument_list|)
expr_stmt|;
name|list
index|[
name|i
index|]
operator|.
name|docCountError
operator|=
literal|0
expr_stmt|;
block|}
return|return
operator|new
name|LongTerms
argument_list|(
name|name
argument_list|,
name|order
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
name|getShardSize
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
argument_list|,
name|showTermDocCountError
argument_list|,
literal|0
argument_list|,
name|otherDocCount
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
return|return
operator|new
name|LongTerms
argument_list|(
name|name
argument_list|,
name|order
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
name|getShardSize
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
name|InternalTerms
operator|.
name|Bucket
operator|>
name|emptyList
argument_list|()
argument_list|,
name|showTermDocCountError
argument_list|,
literal|0
argument_list|,
literal|0
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

