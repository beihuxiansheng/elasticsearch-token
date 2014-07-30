begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.stats
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
name|stats
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
name|AtomicReaderContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|DoubleArray
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
name|SortedNumericDoubleValues
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
name|ValuesSourceAggregatorFactory
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StatsAggegator
specifier|public
class|class
name|StatsAggegator
extends|extends
name|NumericMetricsAggregator
operator|.
name|MultiValue
block|{
DECL|field|valuesSource
specifier|private
specifier|final
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
decl_stmt|;
DECL|field|values
specifier|private
name|SortedNumericDoubleValues
name|values
decl_stmt|;
DECL|field|counts
specifier|private
name|LongArray
name|counts
decl_stmt|;
DECL|field|sums
specifier|private
name|DoubleArray
name|sums
decl_stmt|;
DECL|field|mins
specifier|private
name|DoubleArray
name|mins
decl_stmt|;
DECL|field|maxes
specifier|private
name|DoubleArray
name|maxes
decl_stmt|;
DECL|method|StatsAggegator
specifier|public
name|StatsAggegator
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|estimatedBucketsCount
parameter_list|,
name|ValuesSource
operator|.
name|Numeric
name|valuesSource
parameter_list|,
name|AggregationContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|estimatedBucketsCount
argument_list|,
name|context
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
specifier|final
name|long
name|initialSize
init|=
name|estimatedBucketsCount
operator|<
literal|2
condition|?
literal|1
else|:
name|estimatedBucketsCount
decl_stmt|;
name|counts
operator|=
name|bigArrays
operator|.
name|newLongArray
argument_list|(
name|initialSize
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|sums
operator|=
name|bigArrays
operator|.
name|newDoubleArray
argument_list|(
name|initialSize
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|mins
operator|=
name|bigArrays
operator|.
name|newDoubleArray
argument_list|(
name|initialSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|mins
operator|.
name|fill
argument_list|(
literal|0
argument_list|,
name|mins
operator|.
name|size
argument_list|()
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|maxes
operator|=
name|bigArrays
operator|.
name|newDoubleArray
argument_list|(
name|initialSize
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|maxes
operator|.
name|fill
argument_list|(
literal|0
argument_list|,
name|maxes
operator|.
name|size
argument_list|()
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
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
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|reader
parameter_list|)
block|{
name|values
operator|=
name|valuesSource
operator|.
name|doubleValues
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|owningBucketOrdinal
operator|>=
name|counts
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|long
name|from
init|=
name|counts
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|long
name|overSize
init|=
name|BigArrays
operator|.
name|overSize
argument_list|(
name|owningBucketOrdinal
operator|+
literal|1
argument_list|)
decl_stmt|;
name|counts
operator|=
name|bigArrays
operator|.
name|resize
argument_list|(
name|counts
argument_list|,
name|overSize
argument_list|)
expr_stmt|;
name|sums
operator|=
name|bigArrays
operator|.
name|resize
argument_list|(
name|sums
argument_list|,
name|overSize
argument_list|)
expr_stmt|;
name|mins
operator|=
name|bigArrays
operator|.
name|resize
argument_list|(
name|mins
argument_list|,
name|overSize
argument_list|)
expr_stmt|;
name|maxes
operator|=
name|bigArrays
operator|.
name|resize
argument_list|(
name|maxes
argument_list|,
name|overSize
argument_list|)
expr_stmt|;
name|mins
operator|.
name|fill
argument_list|(
name|from
argument_list|,
name|overSize
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
expr_stmt|;
name|maxes
operator|.
name|fill
argument_list|(
name|from
argument_list|,
name|overSize
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
expr_stmt|;
block|}
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
name|counts
operator|.
name|increment
argument_list|(
name|owningBucketOrdinal
argument_list|,
name|valuesCount
argument_list|)
expr_stmt|;
name|double
name|sum
init|=
literal|0
decl_stmt|;
name|double
name|min
init|=
name|mins
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
argument_list|)
decl_stmt|;
name|double
name|max
init|=
name|maxes
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
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
name|i
operator|++
control|)
block|{
name|double
name|value
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|sum
operator|+=
name|value
expr_stmt|;
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|sums
operator|.
name|increment
argument_list|(
name|owningBucketOrdinal
argument_list|,
name|sum
argument_list|)
expr_stmt|;
name|mins
operator|.
name|set
argument_list|(
name|owningBucketOrdinal
argument_list|,
name|min
argument_list|)
expr_stmt|;
name|maxes
operator|.
name|set
argument_list|(
name|owningBucketOrdinal
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasMetric
specifier|public
name|boolean
name|hasMetric
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
name|InternalStats
operator|.
name|Metrics
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|metric
specifier|public
name|double
name|metric
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|owningBucketOrd
parameter_list|)
block|{
switch|switch
condition|(
name|InternalStats
operator|.
name|Metrics
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
condition|)
block|{
case|case
name|count
case|:
return|return
name|valuesSource
operator|==
literal|null
condition|?
literal|0
else|:
name|counts
operator|.
name|get
argument_list|(
name|owningBucketOrd
argument_list|)
return|;
case|case
name|sum
case|:
return|return
name|valuesSource
operator|==
literal|null
condition|?
literal|0
else|:
name|sums
operator|.
name|get
argument_list|(
name|owningBucketOrd
argument_list|)
return|;
case|case
name|min
case|:
return|return
name|valuesSource
operator|==
literal|null
condition|?
name|Double
operator|.
name|POSITIVE_INFINITY
else|:
name|mins
operator|.
name|get
argument_list|(
name|owningBucketOrd
argument_list|)
return|;
case|case
name|max
case|:
return|return
name|valuesSource
operator|==
literal|null
condition|?
name|Double
operator|.
name|NEGATIVE_INFINITY
else|:
name|maxes
operator|.
name|get
argument_list|(
name|owningBucketOrd
argument_list|)
return|;
case|case
name|avg
case|:
return|return
name|valuesSource
operator|==
literal|null
condition|?
name|Double
operator|.
name|NaN
else|:
name|sums
operator|.
name|get
argument_list|(
name|owningBucketOrd
argument_list|)
operator|/
name|counts
operator|.
name|get
argument_list|(
name|owningBucketOrd
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Unknown value ["
operator|+
name|name
operator|+
literal|"] in common stats aggregation"
argument_list|)
throw|;
block|}
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
name|InternalStats
argument_list|(
name|name
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
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
name|InternalStats
argument_list|(
name|name
argument_list|,
name|counts
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
argument_list|)
argument_list|,
name|sums
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
argument_list|)
argument_list|,
name|mins
operator|.
name|get
argument_list|(
name|owningBucketOrdinal
argument_list|)
argument_list|,
name|maxes
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
name|InternalStats
argument_list|(
name|name
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|ValuesSourceAggregatorFactory
operator|.
name|LeafOnly
argument_list|<
name|ValuesSource
operator|.
name|Numeric
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
name|ValuesSource
operator|.
name|Numeric
argument_list|>
name|valuesSourceConfig
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalStats
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|,
name|valuesSourceConfig
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
name|StatsAggegator
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
name|ValuesSource
operator|.
name|Numeric
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
name|StatsAggegator
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
name|counts
argument_list|,
name|maxes
argument_list|,
name|mins
argument_list|,
name|sums
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

