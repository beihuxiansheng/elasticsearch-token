begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.valuecount
package|package
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
name|valuecount
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
name|common
operator|.
name|util
operator|.
name|BigArrays
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
name|LongArray
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
name|BytesValues
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
name|ValueSourceAggregatorFactory
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
name|ValuesSourceConfig
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
name|bytes
operator|.
name|BytesValuesSource
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

begin_comment
comment|/**  * A field data based aggregator that counts the number of values a specific field has within the aggregation context.  *  * This aggregator works in a multi-bucket mode, that is, when serves as a sub-aggregator, a single aggregator instance aggregates the  * counts for all buckets owned by the parent aggregator)  */
end_comment

begin_class
DECL|class|ValueCountAggregator
specifier|public
class|class
name|ValueCountAggregator
extends|extends
name|Aggregator
block|{
DECL|field|valuesSource
specifier|private
specifier|final
name|BytesValuesSource
name|valuesSource
decl_stmt|;
comment|// a count per bucket
DECL|field|counts
name|LongArray
name|counts
decl_stmt|;
DECL|method|ValueCountAggregator
specifier|public
name|ValueCountAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|expectedBucketsCount
parameter_list|,
name|BytesValuesSource
name|valuesSource
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
name|MULTI_BUCKETS
argument_list|,
name|AggregatorFactories
operator|.
name|EMPTY
argument_list|,
literal|0
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
if|if
condition|(
name|valuesSource
operator|!=
literal|null
condition|)
block|{
comment|// expectedBucketsCount == 0 means it's a top level bucket
specifier|final
name|long
name|initialSize
init|=
name|expectedBucketsCount
operator|<
literal|2
condition|?
literal|1
else|:
name|expectedBucketsCount
decl_stmt|;
name|counts
operator|=
name|BigArrays
operator|.
name|newLongArray
argument_list|(
name|initialSize
argument_list|,
name|context
operator|.
name|pageCacheRecycler
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
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
name|valuesSource
operator|!=
literal|null
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
name|BytesValues
name|values
init|=
name|valuesSource
operator|.
name|bytesValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|counts
operator|=
name|BigArrays
operator|.
name|grow
argument_list|(
name|counts
argument_list|,
name|owningBucketOrdinal
operator|+
literal|1
argument_list|)
expr_stmt|;
name|counts
operator|.
name|increment
argument_list|(
name|owningBucketOrdinal
argument_list|,
name|values
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
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
block|{
if|if
condition|(
name|valuesSource
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|InternalValueCount
argument_list|(
name|name
argument_list|,
literal|0
argument_list|)
return|;
block|}
assert|assert
name|owningBucketOrdinal
operator|<
name|counts
operator|.
name|size
argument_list|()
assert|;
return|return
operator|new
name|InternalValueCount
argument_list|(
name|name
argument_list|,
name|counts
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
argument_list|)
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
name|InternalValueCount
argument_list|(
name|name
argument_list|,
literal|0l
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
name|counts
argument_list|)
expr_stmt|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|ValueSourceAggregatorFactory
operator|.
name|LeafOnly
argument_list|<
name|BytesValuesSource
argument_list|>
block|{
DECL|method|Factory
specifier|public
name|Factory
parameter_list|(
name|String
name|name
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|BytesValuesSource
argument_list|>
name|valuesSourceBuilder
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalValueCount
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|,
name|valuesSourceBuilder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createUnmapped
specifier|protected
name|Aggregator
name|createUnmapped
parameter_list|(
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
return|return
operator|new
name|ValueCountAggregator
argument_list|(
name|name
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Aggregator
name|create
parameter_list|(
name|BytesValuesSource
name|valuesSource
parameter_list|,
name|long
name|expectedBucketsCount
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
return|return
operator|new
name|ValueCountAggregator
argument_list|(
name|name
argument_list|,
name|expectedBucketsCount
argument_list|,
name|valuesSource
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

