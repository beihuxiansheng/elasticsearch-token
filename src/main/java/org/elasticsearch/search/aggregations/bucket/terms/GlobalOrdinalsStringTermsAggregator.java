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
name|AtomicReaderContext
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
name|ArrayUtil
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
name|LongBitSet
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
name|ExceptionsHelper
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
name|text
operator|.
name|Text
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
name|IntArray
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
name|index
operator|.
name|fielddata
operator|.
name|ordinals
operator|.
name|GlobalOrdinalMapping
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
name|bucket
operator|.
name|terms
operator|.
name|InternalTerms
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
name|Arrays
import|;
end_import

begin_comment
comment|/**  * An aggregator of string values that relies on global ordinals in order to build buckets.  */
end_comment

begin_class
DECL|class|GlobalOrdinalsStringTermsAggregator
specifier|public
class|class
name|GlobalOrdinalsStringTermsAggregator
extends|extends
name|AbstractStringTermsAggregator
block|{
DECL|field|valuesSource
specifier|protected
specifier|final
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|.
name|FieldData
name|valuesSource
decl_stmt|;
DECL|field|includeExclude
specifier|protected
specifier|final
name|IncludeExclude
name|includeExclude
decl_stmt|;
DECL|field|globalValues
specifier|protected
name|BytesValues
operator|.
name|WithOrdinals
name|globalValues
decl_stmt|;
comment|// TODO: cache the acceptedglobalValues per aggregation definition.
comment|// We can't cache this yet in ValuesSource, since ValuesSource is reused per field for aggs during the execution.
comment|// If aggs with same field, but different include/exclude are defined, then the last defined one will override the
comment|// first defined one.
comment|// So currently for each instance of this aggregator the acceptedglobalValues will be computed, this is unnecessary
comment|// especially if this agg is on a second layer or deeper.
DECL|field|acceptedGlobalOrdinals
specifier|protected
name|LongBitSet
name|acceptedGlobalOrdinals
decl_stmt|;
DECL|method|GlobalOrdinalsStringTermsAggregator
specifier|public
name|GlobalOrdinalsStringTermsAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|.
name|FieldData
name|valuesSource
parameter_list|,
name|long
name|estimatedBucketCount
parameter_list|,
name|long
name|maxOrd
parameter_list|,
name|InternalOrder
name|order
parameter_list|,
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|SubAggCollectionMode
name|collectionMode
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|maxOrd
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|collectionMode
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
name|includeExclude
operator|=
name|includeExclude
expr_stmt|;
block|}
DECL|method|getBucketOrd
specifier|protected
name|long
name|getBucketOrd
parameter_list|(
name|long
name|termOrd
parameter_list|)
block|{
return|return
name|termOrd
return|;
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
DECL|method|setNextReader
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|reader
parameter_list|)
block|{
name|globalValues
operator|=
name|valuesSource
operator|.
name|globalBytesValues
argument_list|()
expr_stmt|;
if|if
condition|(
name|acceptedGlobalOrdinals
operator|!=
literal|null
condition|)
block|{
name|globalValues
operator|=
operator|new
name|FilteredOrdinals
argument_list|(
name|globalValues
argument_list|,
name|acceptedGlobalOrdinals
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
name|acceptedGlobalOrdinals
operator|=
name|includeExclude
operator|.
name|acceptedGlobalOrdinals
argument_list|(
name|globalValues
argument_list|,
name|valuesSource
argument_list|)
expr_stmt|;
name|globalValues
operator|=
operator|new
name|FilteredOrdinals
argument_list|(
name|globalValues
argument_list|,
name|acceptedGlobalOrdinals
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|int
name|numOrds
init|=
name|globalValues
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
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|globalOrd
init|=
name|globalValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
name|collectExistingBucket
argument_list|(
name|doc
argument_list|,
name|globalOrd
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|copy
specifier|protected
specifier|static
name|void
name|copy
parameter_list|(
name|BytesRef
name|from
parameter_list|,
name|BytesRef
name|to
parameter_list|)
block|{
if|if
condition|(
name|to
operator|.
name|bytes
operator|.
name|length
operator|<
name|from
operator|.
name|length
condition|)
block|{
name|to
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|from
operator|.
name|length
argument_list|,
name|RamUsageEstimator
operator|.
name|NUM_BYTES_BYTE
argument_list|)
index|]
expr_stmt|;
block|}
name|to
operator|.
name|offset
operator|=
literal|0
expr_stmt|;
name|to
operator|.
name|length
operator|=
name|from
operator|.
name|length
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|from
operator|.
name|bytes
argument_list|,
name|from
operator|.
name|offset
argument_list|,
name|to
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|from
operator|.
name|length
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
name|globalValues
operator|==
literal|null
condition|)
block|{
comment|// no context in this reader
return|return
name|buildEmptyAggregation
argument_list|()
return|;
block|}
specifier|final
name|int
name|size
decl_stmt|;
if|if
condition|(
name|bucketCountThresholds
operator|.
name|getMinDocCount
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// if minDocCount == 0 then we can end up with more buckets then maxBucketOrd() returns
name|size
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|globalValues
operator|.
name|getMaxOrd
argument_list|()
argument_list|,
name|bucketCountThresholds
operator|.
name|getShardSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|size
operator|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|maxBucketOrd
argument_list|()
argument_list|,
name|bucketCountThresholds
operator|.
name|getShardSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|OrdBucket
name|spare
init|=
operator|new
name|OrdBucket
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|globalTermOrd
init|=
name|BytesValues
operator|.
name|WithOrdinals
operator|.
name|MIN_ORDINAL
init|;
name|globalTermOrd
operator|<
name|globalValues
operator|.
name|getMaxOrd
argument_list|()
condition|;
operator|++
name|globalTermOrd
control|)
block|{
if|if
condition|(
name|includeExclude
operator|!=
literal|null
operator|&&
operator|!
name|acceptedGlobalOrdinals
operator|.
name|get
argument_list|(
name|globalTermOrd
argument_list|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|long
name|bucketOrd
init|=
name|getBucketOrd
argument_list|(
name|globalTermOrd
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bucketDocCount
init|=
name|bucketOrd
operator|<
literal|0
condition|?
literal|0
else|:
name|bucketDocCount
argument_list|(
name|bucketOrd
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketCountThresholds
operator|.
name|getMinDocCount
argument_list|()
operator|>
literal|0
operator|&&
name|bucketDocCount
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|spare
operator|.
name|globalOrd
operator|=
name|globalTermOrd
expr_stmt|;
name|spare
operator|.
name|bucketOrd
operator|=
name|bucketOrd
expr_stmt|;
name|spare
operator|.
name|docCount
operator|=
name|bucketDocCount
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
name|OrdBucket
operator|)
name|ordered
operator|.
name|insertWithOverflow
argument_list|(
name|spare
argument_list|)
expr_stmt|;
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
name|OrdBucket
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
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
name|OrdBucket
name|bucket
init|=
operator|(
name|OrdBucket
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
name|BytesRef
name|scratch
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
name|copy
argument_list|(
name|globalValues
operator|.
name|getValueByOrd
argument_list|(
name|bucket
operator|.
name|globalOrd
argument_list|)
argument_list|,
name|scratch
argument_list|)
expr_stmt|;
name|list
index|[
name|i
index|]
operator|=
operator|new
name|StringTerms
operator|.
name|Bucket
argument_list|(
name|scratch
argument_list|,
name|bucket
operator|.
name|docCount
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|list
index|[
name|i
index|]
operator|.
name|bucketOrd
operator|=
name|bucket
operator|.
name|bucketOrd
expr_stmt|;
block|}
comment|//replay any deferred collections
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
name|Bucket
name|bucket
init|=
name|list
index|[
name|i
index|]
decl_stmt|;
name|bucket
operator|.
name|aggregations
operator|=
name|bucket
operator|.
name|docCount
operator|==
literal|0
condition|?
name|bucketEmptyAggregations
argument_list|()
else|:
name|bucketAggregations
argument_list|(
name|bucket
operator|.
name|bucketOrd
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|StringTerms
argument_list|(
name|name
argument_list|,
name|order
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
comment|/** This is used internally only, just for compare using global ordinal instead of term bytes in the PQ */
DECL|class|OrdBucket
specifier|static
class|class
name|OrdBucket
extends|extends
name|InternalTerms
operator|.
name|Bucket
block|{
DECL|field|globalOrd
name|long
name|globalOrd
decl_stmt|;
DECL|method|OrdBucket
name|OrdBucket
parameter_list|(
name|long
name|globalOrd
parameter_list|,
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggregations
parameter_list|)
block|{
name|super
argument_list|(
name|docCount
argument_list|,
name|aggregations
argument_list|)
expr_stmt|;
name|this
operator|.
name|globalOrd
operator|=
name|globalOrd
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|compareTerm
name|int
name|compareTerm
parameter_list|(
name|Terms
operator|.
name|Bucket
name|other
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|globalOrd
argument_list|,
operator|(
operator|(
name|OrdBucket
operator|)
name|other
operator|)
operator|.
name|globalOrd
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getKey
specifier|public
name|String
name|getKey
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsText
specifier|public
name|Text
name|getKeyAsText
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsObject
name|Object
name|getKeyAsObject
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|newBucket
name|Bucket
name|newBucket
parameter_list|(
name|long
name|docCount
parameter_list|,
name|InternalAggregations
name|aggs
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getKeyAsNumber
specifier|public
name|Number
name|getKeyAsNumber
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
comment|/**      * Variant of {@link GlobalOrdinalsStringTermsAggregator} that rebases hashes in order to make them dense. Might be      * useful in case few hashes are visited.      */
DECL|class|WithHash
specifier|public
specifier|static
class|class
name|WithHash
extends|extends
name|GlobalOrdinalsStringTermsAggregator
block|{
DECL|field|bucketOrds
specifier|private
specifier|final
name|LongHash
name|bucketOrds
decl_stmt|;
DECL|method|WithHash
specifier|public
name|WithHash
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|.
name|FieldData
name|valuesSource
parameter_list|,
name|long
name|estimatedBucketCount
parameter_list|,
name|long
name|maxOrd
parameter_list|,
name|InternalOrder
name|order
parameter_list|,
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|SubAggCollectionMode
name|collectionMode
parameter_list|)
block|{
comment|// Set maxOrd to estimatedBucketCount! To be conservative with memory.
name|super
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|valuesSource
argument_list|,
name|estimatedBucketCount
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|includeExclude
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|collectionMode
argument_list|)
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
name|bigArrays
argument_list|()
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
name|owningBucketOrdinal
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numOrds
init|=
name|globalValues
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
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|globalOrd
init|=
name|globalValues
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
name|long
name|bucketOrd
init|=
name|bucketOrds
operator|.
name|add
argument_list|(
name|globalOrd
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketOrd
operator|<
literal|0
condition|)
block|{
name|bucketOrd
operator|=
operator|-
literal|1
operator|-
name|bucketOrd
expr_stmt|;
name|collectExistingBucket
argument_list|(
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
name|doc
argument_list|,
name|bucketOrd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getBucketOrd
specifier|protected
name|long
name|getBucketOrd
parameter_list|(
name|long
name|termOrd
parameter_list|)
block|{
return|return
name|bucketOrds
operator|.
name|find
argument_list|(
name|termOrd
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
name|bucketOrds
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Variant of {@link GlobalOrdinalsStringTermsAggregator} that resolves global ordinals post segment collection      * instead of on the fly for each match.This is beneficial for low cardinality fields, because it can reduce      * the amount of look-ups significantly.      */
DECL|class|LowCardinality
specifier|public
specifier|static
class|class
name|LowCardinality
extends|extends
name|GlobalOrdinalsStringTermsAggregator
block|{
DECL|field|segmentDocCounts
specifier|private
specifier|final
name|IntArray
name|segmentDocCounts
decl_stmt|;
DECL|field|segmentOrdinals
specifier|private
name|BytesValues
operator|.
name|WithOrdinals
name|segmentOrdinals
decl_stmt|;
DECL|field|current
specifier|private
name|IntArray
name|current
decl_stmt|;
DECL|method|LowCardinality
specifier|public
name|LowCardinality
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|.
name|FieldData
name|valuesSource
parameter_list|,
name|long
name|estimatedBucketCount
parameter_list|,
name|long
name|maxOrd
parameter_list|,
name|InternalOrder
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
name|estimatedBucketCount
argument_list|,
name|maxOrd
argument_list|,
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
literal|null
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|collectionMode
argument_list|)
expr_stmt|;
name|this
operator|.
name|segmentDocCounts
operator|=
name|bigArrays
operator|.
name|newIntArray
argument_list|(
name|maxOrd
argument_list|,
literal|true
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
name|owningBucketOrdinal
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numOrds
init|=
name|segmentOrdinals
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
name|numOrds
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|long
name|segmentOrd
init|=
name|segmentOrdinals
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
name|current
operator|.
name|increment
argument_list|(
name|segmentOrd
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|segmentOrdinals
operator|!=
literal|null
operator|&&
name|segmentOrdinals
operator|.
name|getMaxOrd
argument_list|()
operator|!=
name|globalValues
operator|.
name|getMaxOrd
argument_list|()
condition|)
block|{
name|mapSegmentCountsToGlobalCounts
argument_list|()
expr_stmt|;
block|}
name|globalValues
operator|=
name|valuesSource
operator|.
name|globalBytesValues
argument_list|()
expr_stmt|;
name|segmentOrdinals
operator|=
name|valuesSource
operator|.
name|bytesValues
argument_list|()
expr_stmt|;
if|if
condition|(
name|segmentOrdinals
operator|.
name|getMaxOrd
argument_list|()
operator|!=
name|globalValues
operator|.
name|getMaxOrd
argument_list|()
condition|)
block|{
name|current
operator|=
name|segmentDocCounts
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
name|getDocCounts
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|segmentOrdinals
operator|.
name|getMaxOrd
argument_list|()
operator|!=
name|globalValues
operator|.
name|getMaxOrd
argument_list|()
condition|)
block|{
name|mapSegmentCountsToGlobalCounts
argument_list|()
expr_stmt|;
block|}
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
name|segmentDocCounts
argument_list|)
expr_stmt|;
block|}
DECL|method|mapSegmentCountsToGlobalCounts
specifier|private
name|void
name|mapSegmentCountsToGlobalCounts
parameter_list|()
block|{
comment|// There is no public method in Ordinals.Docs that allows for this mapping...
comment|// This is the cleanest way I can think of so far
name|GlobalOrdinalMapping
name|mapping
init|=
operator|(
name|GlobalOrdinalMapping
operator|)
name|globalValues
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
name|segmentDocCounts
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|inc
init|=
name|segmentDocCounts
operator|.
name|set
argument_list|(
name|i
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|inc
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
specifier|final
name|long
name|globalOrd
init|=
name|mapping
operator|.
name|getGlobalOrd
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|incrementBucketDocCount
argument_list|(
name|inc
argument_list|,
name|globalOrd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
block|}
DECL|class|FilteredOrdinals
specifier|private
specifier|static
specifier|final
class|class
name|FilteredOrdinals
extends|extends
name|BytesValues
operator|.
name|WithOrdinals
block|{
DECL|field|inner
specifier|private
specifier|final
name|BytesValues
operator|.
name|WithOrdinals
name|inner
decl_stmt|;
DECL|field|accepted
specifier|private
specifier|final
name|LongBitSet
name|accepted
decl_stmt|;
DECL|field|currentOrd
specifier|private
name|long
name|currentOrd
decl_stmt|;
DECL|field|buffer
specifier|private
name|long
index|[]
name|buffer
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
DECL|field|bufferSlot
specifier|private
name|int
name|bufferSlot
decl_stmt|;
DECL|method|FilteredOrdinals
specifier|private
name|FilteredOrdinals
parameter_list|(
name|BytesValues
operator|.
name|WithOrdinals
name|inner
parameter_list|,
name|LongBitSet
name|accepted
parameter_list|)
block|{
name|super
argument_list|(
name|inner
operator|.
name|isMultiValued
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|inner
operator|=
name|inner
expr_stmt|;
name|this
operator|.
name|accepted
operator|=
name|accepted
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
name|long
name|getMaxOrd
parameter_list|()
block|{
return|return
name|inner
operator|.
name|getMaxOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|long
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|long
name|ord
init|=
name|inner
operator|.
name|getOrd
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|accepted
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
return|return
name|currentOrd
operator|=
name|ord
return|;
block|}
else|else
block|{
return|return
name|currentOrd
operator|=
name|MISSING_ORDINAL
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|nextOrd
specifier|public
name|long
name|nextOrd
parameter_list|()
block|{
return|return
name|currentOrd
operator|=
name|buffer
index|[
name|bufferSlot
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|setDocument
specifier|public
name|int
name|setDocument
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|int
name|numDocs
init|=
name|inner
operator|.
name|setDocument
argument_list|(
name|docId
argument_list|)
decl_stmt|;
name|buffer
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|buffer
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|bufferSlot
operator|=
literal|0
expr_stmt|;
name|int
name|numAcceptedOrds
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|slot
init|=
literal|0
init|;
name|slot
operator|<
name|numDocs
condition|;
name|slot
operator|++
control|)
block|{
name|long
name|ord
init|=
name|inner
operator|.
name|nextOrd
argument_list|()
decl_stmt|;
if|if
condition|(
name|accepted
operator|.
name|get
argument_list|(
name|ord
argument_list|)
condition|)
block|{
name|buffer
index|[
name|numAcceptedOrds
index|]
operator|=
name|ord
expr_stmt|;
name|numAcceptedOrds
operator|++
expr_stmt|;
block|}
block|}
return|return
name|numAcceptedOrds
return|;
block|}
annotation|@
name|Override
DECL|method|currentOrd
specifier|public
name|long
name|currentOrd
parameter_list|()
block|{
return|return
name|currentOrd
return|;
block|}
annotation|@
name|Override
DECL|method|getValueByOrd
specifier|public
name|BytesRef
name|getValueByOrd
parameter_list|(
name|long
name|ord
parameter_list|)
block|{
return|return
name|inner
operator|.
name|getValueByOrd
argument_list|(
name|ord
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|copyShared
specifier|public
name|BytesRef
name|copyShared
parameter_list|()
block|{
return|return
name|inner
operator|.
name|copyShared
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

