begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics.cardinality
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
name|cardinality
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|hash
operator|.
name|MurmurHash3
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|index
operator|.
name|RandomAccessOrds
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
name|search
operator|.
name|DocIdSetIterator
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|FixedBitSet
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
name|RamUsageEstimator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|Releasable
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
name|LongArray
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
name|ObjectArray
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
name|SortedBinaryDocValues
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
name|reducers
operator|.
name|Reducer
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
comment|/**  * An aggregator that computes approximate counts of unique values.  */
end_comment

begin_class
DECL|class|CardinalityAggregator
specifier|public
class|class
name|CardinalityAggregator
extends|extends
name|NumericMetricsAggregator
operator|.
name|SingleValue
block|{
DECL|field|precision
specifier|private
specifier|final
name|int
name|precision
decl_stmt|;
DECL|field|rehash
specifier|private
specifier|final
name|boolean
name|rehash
decl_stmt|;
DECL|field|valuesSource
specifier|private
specifier|final
name|ValuesSource
name|valuesSource
decl_stmt|;
comment|// Expensive to initialize, so we only initialize it when we have an actual value source
annotation|@
name|Nullable
DECL|field|counts
specifier|private
name|HyperLogLogPlusPlus
name|counts
decl_stmt|;
DECL|field|collector
specifier|private
name|Collector
name|collector
decl_stmt|;
DECL|field|formatter
specifier|private
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|method|CardinalityAggregator
specifier|public
name|CardinalityAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|boolean
name|rehash
parameter_list|,
name|int
name|precision
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|AggregationContext
name|context
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|List
argument_list|<
name|Reducer
argument_list|>
name|reducers
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
name|context
argument_list|,
name|parent
argument_list|,
name|reducers
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
name|rehash
operator|=
name|rehash
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|counts
operator|=
name|valuesSource
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|precision
argument_list|,
name|context
operator|.
name|bigArrays
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
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
name|valuesSource
operator|!=
literal|null
operator|&&
name|valuesSource
operator|.
name|needsScores
argument_list|()
return|;
block|}
DECL|method|pickCollector
specifier|private
name|Collector
name|pickCollector
parameter_list|(
name|LeafReaderContext
name|ctx
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
operator|new
name|EmptyCollector
argument_list|()
return|;
block|}
comment|// if rehash is false then the value source is either already hashed, or the user explicitly
comment|// requested not to hash the values (perhaps they already hashed the values themselves before indexing the doc)
comment|// so we can just work with the original value source as is
if|if
condition|(
operator|!
name|rehash
condition|)
block|{
name|MurmurHash3Values
name|hashValues
init|=
name|MurmurHash3Values
operator|.
name|cast
argument_list|(
operator|(
operator|(
name|ValuesSource
operator|.
name|Numeric
operator|)
name|valuesSource
operator|)
operator|.
name|longValues
argument_list|(
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DirectCollector
argument_list|(
name|counts
argument_list|,
name|hashValues
argument_list|)
return|;
block|}
if|if
condition|(
name|valuesSource
operator|instanceof
name|ValuesSource
operator|.
name|Numeric
condition|)
block|{
name|ValuesSource
operator|.
name|Numeric
name|source
init|=
operator|(
name|ValuesSource
operator|.
name|Numeric
operator|)
name|valuesSource
decl_stmt|;
name|MurmurHash3Values
name|hashValues
init|=
name|source
operator|.
name|isFloatingPoint
argument_list|()
condition|?
name|MurmurHash3Values
operator|.
name|hash
argument_list|(
name|source
operator|.
name|doubleValues
argument_list|(
name|ctx
argument_list|)
argument_list|)
else|:
name|MurmurHash3Values
operator|.
name|hash
argument_list|(
name|source
operator|.
name|longValues
argument_list|(
name|ctx
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|DirectCollector
argument_list|(
name|counts
argument_list|,
name|hashValues
argument_list|)
return|;
block|}
if|if
condition|(
name|valuesSource
operator|instanceof
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
condition|)
block|{
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
name|source
init|=
operator|(
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|)
name|valuesSource
decl_stmt|;
specifier|final
name|RandomAccessOrds
name|ordinalValues
init|=
name|source
operator|.
name|ordinalsValues
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
specifier|final
name|long
name|maxOrd
init|=
name|ordinalValues
operator|.
name|getValueCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxOrd
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|EmptyCollector
argument_list|()
return|;
block|}
specifier|final
name|long
name|ordinalsMemoryUsage
init|=
name|OrdinalsCollector
operator|.
name|memoryOverhead
argument_list|(
name|maxOrd
argument_list|)
decl_stmt|;
specifier|final
name|long
name|countsMemoryUsage
init|=
name|HyperLogLogPlusPlus
operator|.
name|memoryUsage
argument_list|(
name|precision
argument_list|)
decl_stmt|;
comment|// only use ordinals if they don't increase memory usage by more than 25%
if|if
condition|(
name|ordinalsMemoryUsage
operator|<
name|countsMemoryUsage
operator|/
literal|4
condition|)
block|{
return|return
operator|new
name|OrdinalsCollector
argument_list|(
name|counts
argument_list|,
name|ordinalValues
argument_list|,
name|context
operator|.
name|bigArrays
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|DirectCollector
argument_list|(
name|counts
argument_list|,
name|MurmurHash3Values
operator|.
name|hash
argument_list|(
name|valuesSource
operator|.
name|bytesValues
argument_list|(
name|ctx
argument_list|)
argument_list|)
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
name|postCollectLastCollector
argument_list|()
expr_stmt|;
name|collector
operator|=
name|pickCollector
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
return|return
name|collector
return|;
block|}
DECL|method|postCollectLastCollector
specifier|private
name|void
name|postCollectLastCollector
parameter_list|()
block|{
if|if
condition|(
name|collector
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|collector
operator|.
name|postCollect
argument_list|()
expr_stmt|;
name|collector
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|collector
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doPostCollection
specifier|protected
name|void
name|doPostCollection
parameter_list|()
block|{
name|postCollectLastCollector
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|metric
specifier|public
name|double
name|metric
parameter_list|(
name|long
name|owningBucketOrd
parameter_list|)
block|{
return|return
name|counts
operator|==
literal|null
condition|?
literal|0
else|:
name|counts
operator|.
name|cardinality
argument_list|(
name|owningBucketOrd
argument_list|)
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
block|{
if|if
condition|(
name|counts
operator|==
literal|null
operator|||
name|owningBucketOrdinal
operator|>=
name|counts
operator|.
name|maxBucket
argument_list|()
operator|||
name|counts
operator|.
name|cardinality
argument_list|(
name|owningBucketOrdinal
argument_list|)
operator|==
literal|0
condition|)
block|{
return|return
name|buildEmptyAggregation
argument_list|()
return|;
block|}
comment|// We need to build a copy because the returned Aggregation needs remain usable after
comment|// this Aggregator (and its HLL++ counters) is released.
name|HyperLogLogPlusPlus
name|copy
init|=
operator|new
name|HyperLogLogPlusPlus
argument_list|(
name|precision
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|copy
operator|.
name|merge
argument_list|(
literal|0
argument_list|,
name|counts
argument_list|,
name|owningBucketOrdinal
argument_list|)
expr_stmt|;
return|return
operator|new
name|InternalCardinality
argument_list|(
name|name
argument_list|,
name|copy
argument_list|,
name|formatter
argument_list|,
name|reducers
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
name|InternalCardinality
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|formatter
argument_list|,
name|reducers
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
specifier|protected
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
name|collector
argument_list|)
expr_stmt|;
block|}
DECL|class|Collector
specifier|private
specifier|static
specifier|abstract
class|class
name|Collector
extends|extends
name|LeafBucketCollector
implements|implements
name|Releasable
block|{
DECL|method|postCollect
specifier|public
specifier|abstract
name|void
name|postCollect
parameter_list|()
function_decl|;
block|}
DECL|class|EmptyCollector
specifier|private
specifier|static
class|class
name|EmptyCollector
extends|extends
name|Collector
block|{
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
name|bucketOrd
parameter_list|)
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|postCollect
specifier|public
name|void
name|postCollect
parameter_list|()
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no-op
block|}
block|}
DECL|class|DirectCollector
specifier|private
specifier|static
class|class
name|DirectCollector
extends|extends
name|Collector
block|{
DECL|field|hashes
specifier|private
specifier|final
name|MurmurHash3Values
name|hashes
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|HyperLogLogPlusPlus
name|counts
decl_stmt|;
DECL|method|DirectCollector
name|DirectCollector
parameter_list|(
name|HyperLogLogPlusPlus
name|counts
parameter_list|,
name|MurmurHash3Values
name|values
parameter_list|)
block|{
name|this
operator|.
name|counts
operator|=
name|counts
expr_stmt|;
name|this
operator|.
name|hashes
operator|=
name|values
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
name|bucketOrd
parameter_list|)
block|{
name|hashes
operator|.
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|int
name|valueCount
init|=
name|hashes
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
name|counts
operator|.
name|collect
argument_list|(
name|bucketOrd
argument_list|,
name|hashes
operator|.
name|valueAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postCollect
specifier|public
name|void
name|postCollect
parameter_list|()
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// no-op
block|}
block|}
DECL|class|OrdinalsCollector
specifier|private
specifier|static
class|class
name|OrdinalsCollector
extends|extends
name|Collector
block|{
DECL|field|SHALLOW_FIXEDBITSET_SIZE
specifier|private
specifier|static
specifier|final
name|long
name|SHALLOW_FIXEDBITSET_SIZE
init|=
name|RamUsageEstimator
operator|.
name|shallowSizeOfInstance
argument_list|(
name|FixedBitSet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**          * Return an approximate memory overhead per bucket for this collector.          */
DECL|method|memoryOverhead
specifier|public
specifier|static
name|long
name|memoryOverhead
parameter_list|(
name|long
name|maxOrd
parameter_list|)
block|{
return|return
name|RamUsageEstimator
operator|.
name|NUM_BYTES_OBJECT_REF
operator|+
name|SHALLOW_FIXEDBITSET_SIZE
operator|+
operator|(
name|maxOrd
operator|+
literal|7
operator|)
operator|/
literal|8
return|;
comment|// 1 bit per ord
block|}
DECL|field|bigArrays
specifier|private
specifier|final
name|BigArrays
name|bigArrays
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|RandomAccessOrds
name|values
decl_stmt|;
DECL|field|maxOrd
specifier|private
specifier|final
name|int
name|maxOrd
decl_stmt|;
DECL|field|counts
specifier|private
specifier|final
name|HyperLogLogPlusPlus
name|counts
decl_stmt|;
DECL|field|visitedOrds
specifier|private
name|ObjectArray
argument_list|<
name|FixedBitSet
argument_list|>
name|visitedOrds
decl_stmt|;
DECL|method|OrdinalsCollector
name|OrdinalsCollector
parameter_list|(
name|HyperLogLogPlusPlus
name|counts
parameter_list|,
name|RandomAccessOrds
name|values
parameter_list|,
name|BigArrays
name|bigArrays
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|values
operator|.
name|getValueCount
argument_list|()
operator|<=
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
name|maxOrd
operator|=
operator|(
name|int
operator|)
name|values
operator|.
name|getValueCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|bigArrays
operator|=
name|bigArrays
expr_stmt|;
name|this
operator|.
name|counts
operator|=
name|counts
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|visitedOrds
operator|=
name|bigArrays
operator|.
name|newObjectArray
argument_list|(
literal|1
argument_list|)
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
name|bucketOrd
parameter_list|)
block|{
name|visitedOrds
operator|=
name|bigArrays
operator|.
name|grow
argument_list|(
name|visitedOrds
argument_list|,
name|bucketOrd
operator|+
literal|1
argument_list|)
expr_stmt|;
name|FixedBitSet
name|bits
init|=
name|visitedOrds
operator|.
name|get
argument_list|(
name|bucketOrd
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|==
literal|null
condition|)
block|{
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|maxOrd
argument_list|)
expr_stmt|;
name|visitedOrds
operator|.
name|set
argument_list|(
name|bucketOrd
argument_list|,
name|bits
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
name|valueCount
init|=
name|values
operator|.
name|cardinality
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
name|bits
operator|.
name|set
argument_list|(
operator|(
name|int
operator|)
name|values
operator|.
name|ordAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postCollect
specifier|public
name|void
name|postCollect
parameter_list|()
block|{
specifier|final
name|FixedBitSet
name|allVisitedOrds
init|=
operator|new
name|FixedBitSet
argument_list|(
name|maxOrd
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|bucket
init|=
name|visitedOrds
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|bucket
operator|>=
literal|0
condition|;
operator|--
name|bucket
control|)
block|{
specifier|final
name|FixedBitSet
name|bits
init|=
name|visitedOrds
operator|.
name|get
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|allVisitedOrds
operator|.
name|or
argument_list|(
name|bits
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
operator|.
name|MurmurHash3
operator|.
name|Hash128
name|hash
init|=
operator|new
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
operator|.
name|MurmurHash3
operator|.
name|Hash128
argument_list|()
decl_stmt|;
try|try
init|(
name|LongArray
name|hashes
init|=
name|bigArrays
operator|.
name|newLongArray
argument_list|(
name|maxOrd
argument_list|,
literal|false
argument_list|)
init|)
block|{
for|for
control|(
name|int
name|ord
init|=
name|allVisitedOrds
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|ord
operator|<
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|ord
operator|=
name|ord
operator|+
literal|1
operator|<
name|maxOrd
condition|?
name|allVisitedOrds
operator|.
name|nextSetBit
argument_list|(
name|ord
operator|+
literal|1
argument_list|)
else|:
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
control|)
block|{
specifier|final
name|BytesRef
name|value
init|=
name|values
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
operator|.
name|MurmurHash3
operator|.
name|hash128
argument_list|(
name|value
operator|.
name|bytes
argument_list|,
name|value
operator|.
name|offset
argument_list|,
name|value
operator|.
name|length
argument_list|,
literal|0
argument_list|,
name|hash
argument_list|)
expr_stmt|;
name|hashes
operator|.
name|set
argument_list|(
name|ord
argument_list|,
name|hash
operator|.
name|h1
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|long
name|bucket
init|=
name|visitedOrds
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|bucket
operator|>=
literal|0
condition|;
operator|--
name|bucket
control|)
block|{
specifier|final
name|FixedBitSet
name|bits
init|=
name|visitedOrds
operator|.
name|get
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|ord
init|=
name|bits
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|ord
operator|<
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|ord
operator|=
name|ord
operator|+
literal|1
operator|<
name|maxOrd
condition|?
name|bits
operator|.
name|nextSetBit
argument_list|(
name|ord
operator|+
literal|1
argument_list|)
else|:
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
control|)
block|{
name|counts
operator|.
name|collect
argument_list|(
name|bucket
argument_list|,
name|hashes
operator|.
name|get
argument_list|(
name|ord
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|visitedOrds
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Representation of a list of hash values. There might be dups and there is no guarantee on the order.      */
DECL|class|MurmurHash3Values
specifier|static
specifier|abstract
class|class
name|MurmurHash3Values
block|{
DECL|method|setDocument
specifier|public
specifier|abstract
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
function_decl|;
DECL|method|count
specifier|public
specifier|abstract
name|int
name|count
parameter_list|()
function_decl|;
DECL|method|valueAt
specifier|public
specifier|abstract
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**          * Return a {@link MurmurHash3Values} instance that returns each value as its hash.          */
DECL|method|cast
specifier|public
specifier|static
name|MurmurHash3Values
name|cast
parameter_list|(
specifier|final
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
return|return
operator|new
name|MurmurHash3Values
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|values
operator|.
name|valueAt
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**          * Return a {@link MurmurHash3Values} instance that computes hashes on the fly for each double value.          */
DECL|method|hash
specifier|public
specifier|static
name|MurmurHash3Values
name|hash
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
return|return
operator|new
name|Double
argument_list|(
name|values
argument_list|)
return|;
block|}
comment|/**          * Return a {@link MurmurHash3Values} instance that computes hashes on the fly for each long value.          */
DECL|method|hash
specifier|public
specifier|static
name|MurmurHash3Values
name|hash
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
return|return
operator|new
name|Long
argument_list|(
name|values
argument_list|)
return|;
block|}
comment|/**          * Return a {@link MurmurHash3Values} instance that computes hashes on the fly for each binary value.          */
DECL|method|hash
specifier|public
specifier|static
name|MurmurHash3Values
name|hash
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
return|return
operator|new
name|Bytes
argument_list|(
name|values
argument_list|)
return|;
block|}
DECL|class|Long
specifier|private
specifier|static
class|class
name|Long
extends|extends
name|MurmurHash3Values
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDocValues
name|values
decl_stmt|;
DECL|method|Long
specifier|public
name|Long
parameter_list|(
name|SortedNumericDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|MurmurHash3
operator|.
name|hash
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|Double
specifier|private
specifier|static
class|class
name|Double
extends|extends
name|MurmurHash3Values
block|{
DECL|field|values
specifier|private
specifier|final
name|SortedNumericDoubleValues
name|values
decl_stmt|;
DECL|method|Double
specifier|public
name|Double
parameter_list|(
name|SortedNumericDoubleValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|MurmurHash3
operator|.
name|hash
argument_list|(
name|java
operator|.
name|lang
operator|.
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|values
operator|.
name|valueAt
argument_list|(
name|index
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|class|Bytes
specifier|private
specifier|static
class|class
name|Bytes
extends|extends
name|MurmurHash3Values
block|{
DECL|field|hash
specifier|private
specifier|final
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
operator|.
name|MurmurHash3
operator|.
name|Hash128
name|hash
init|=
operator|new
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
operator|.
name|MurmurHash3
operator|.
name|Hash128
argument_list|()
decl_stmt|;
DECL|field|values
specifier|private
specifier|final
name|SortedBinaryDocValues
name|values
decl_stmt|;
DECL|method|Bytes
specifier|public
name|Bytes
parameter_list|(
name|SortedBinaryDocValues
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|void
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|values
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|count
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|values
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|valueAt
specifier|public
name|long
name|valueAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|BytesRef
name|bytes
init|=
name|values
operator|.
name|valueAt
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|hash
operator|.
name|MurmurHash3
operator|.
name|hash128
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|,
literal|0
argument_list|,
name|hash
argument_list|)
expr_stmt|;
return|return
name|hash
operator|.
name|h1
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

