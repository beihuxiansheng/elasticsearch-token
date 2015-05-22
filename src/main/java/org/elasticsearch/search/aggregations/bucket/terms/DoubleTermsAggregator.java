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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|NumericUtils
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
name|index
operator|.
name|fielddata
operator|.
name|FieldData
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
name|ValuesSource
operator|.
name|Numeric
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DoubleTermsAggregator
specifier|public
class|class
name|DoubleTermsAggregator
extends|extends
name|LongTermsAggregator
block|{
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
name|collectionMode
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
name|valuesSource
argument_list|,
name|format
argument_list|,
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|collectionMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|longFilter
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValues
specifier|protected
name|SortedNumericDocValues
name|getValues
parameter_list|(
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
name|FieldData
operator|.
name|toSortableLongBits
argument_list|(
name|valuesSource
operator|.
name|doubleValues
argument_list|(
name|ctx
argument_list|)
argument_list|)
return|;
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
throws|throws
name|IOException
block|{
specifier|final
name|LongTerms
name|terms
init|=
operator|(
name|LongTerms
operator|)
name|super
operator|.
name|buildAggregation
argument_list|(
name|owningBucketOrdinal
argument_list|)
decl_stmt|;
return|return
name|convertToDouble
argument_list|(
name|terms
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
specifier|final
name|LongTerms
name|terms
init|=
operator|(
name|LongTerms
operator|)
name|super
operator|.
name|buildEmptyAggregation
argument_list|()
decl_stmt|;
return|return
name|convertToDouble
argument_list|(
name|terms
argument_list|)
return|;
block|}
DECL|method|convertToDouble
specifier|private
specifier|static
name|DoubleTerms
operator|.
name|Bucket
name|convertToDouble
parameter_list|(
name|InternalTerms
operator|.
name|Bucket
name|bucket
parameter_list|)
block|{
specifier|final
name|long
name|term
init|=
operator|(
operator|(
name|Number
operator|)
name|bucket
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
specifier|final
name|double
name|value
init|=
name|NumericUtils
operator|.
name|sortableLongToDouble
argument_list|(
name|term
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleTerms
operator|.
name|Bucket
argument_list|(
name|value
argument_list|,
name|bucket
operator|.
name|docCount
argument_list|,
name|bucket
operator|.
name|aggregations
argument_list|,
name|bucket
operator|.
name|showDocCountError
argument_list|,
name|bucket
operator|.
name|docCountError
argument_list|,
name|bucket
operator|.
name|formatter
argument_list|)
return|;
block|}
DECL|method|convertToDouble
specifier|private
specifier|static
name|DoubleTerms
name|convertToDouble
parameter_list|(
name|LongTerms
name|terms
parameter_list|)
block|{
specifier|final
name|InternalTerms
operator|.
name|Bucket
index|[]
name|buckets
init|=
name|terms
operator|.
name|getBuckets
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|InternalTerms
operator|.
name|Bucket
index|[
literal|0
index|]
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
name|buckets
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|buckets
index|[
name|i
index|]
operator|=
name|convertToDouble
argument_list|(
name|buckets
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DoubleTerms
argument_list|(
name|terms
operator|.
name|getName
argument_list|()
argument_list|,
name|terms
operator|.
name|order
argument_list|,
name|terms
operator|.
name|formatter
argument_list|,
name|terms
operator|.
name|requiredSize
argument_list|,
name|terms
operator|.
name|shardSize
argument_list|,
name|terms
operator|.
name|minDocCount
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|buckets
argument_list|)
argument_list|,
name|terms
operator|.
name|showTermDocCountError
argument_list|,
name|terms
operator|.
name|docCountError
argument_list|,
name|terms
operator|.
name|otherDocCount
argument_list|,
name|terms
operator|.
name|pipelineAggregators
argument_list|()
argument_list|,
name|terms
operator|.
name|getMetaData
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

