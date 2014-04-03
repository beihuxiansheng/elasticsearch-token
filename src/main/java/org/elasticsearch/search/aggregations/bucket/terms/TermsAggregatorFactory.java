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
name|ParseField
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
name|*
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
operator|.
name|BucketAggregationMode
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TermsAggregatorFactory
specifier|public
class|class
name|TermsAggregatorFactory
extends|extends
name|ValuesSourceAggregatorFactory
block|{
DECL|enum|ExecutionMode
specifier|public
enum|enum
name|ExecutionMode
block|{
DECL|method|MAP
DECL|method|MAP
name|MAP
argument_list|(
operator|new
name|ParseField
argument_list|(
literal|"map"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
name|Aggregator
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
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
name|long
name|minDocCount
parameter_list|,
name|IncludeExclude
name|includeExclude
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
name|StringTermsAggregator
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|valuesSource
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|includeExclude
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|needsGlobalOrdinals
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|,
DECL|method|ORDINALS
DECL|method|ORDINALS
name|ORDINALS
argument_list|(
operator|new
name|ParseField
argument_list|(
literal|"ordinals"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
name|Aggregator
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
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
name|long
name|minDocCount
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"The `"
operator|+
name|this
operator|+
literal|"` execution mode cannot filter terms."
argument_list|)
throw|;
block|}
return|return
operator|new
name|StringTermsAggregator
operator|.
name|WithOrdinals
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
operator|(
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|)
name|valuesSource
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|needsGlobalOrdinals
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|,
DECL|method|GLOBAL_ORDINALS
DECL|method|GLOBAL_ORDINALS
name|GLOBAL_ORDINALS
argument_list|(
operator|new
name|ParseField
argument_list|(
literal|"global_ordinals"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
name|Aggregator
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
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
name|long
name|minDocCount
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"The `"
operator|+
name|this
operator|+
literal|"` execution mode cannot filter terms."
argument_list|)
throw|;
block|}
return|return
operator|new
name|GlobalOrdinalsStringTermsAggregator
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
operator|(
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|.
name|FieldData
operator|)
name|valuesSource
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|needsGlobalOrdinals
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|,
DECL|method|GLOBAL_ORDINALS_HASH
DECL|method|GLOBAL_ORDINALS_HASH
name|GLOBAL_ORDINALS_HASH
argument_list|(
operator|new
name|ParseField
argument_list|(
literal|"global_ordinals_hash"
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
name|Aggregator
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
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
name|long
name|minDocCount
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
block|{
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"The `"
operator|+
name|this
operator|+
literal|"` execution mode cannot filter terms."
argument_list|)
throw|;
block|}
return|return
operator|new
name|GlobalOrdinalsStringTermsAggregator
operator|.
name|WithHash
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
operator|(
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|.
name|FieldData
operator|)
name|valuesSource
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|needsGlobalOrdinals
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
block|;
DECL|method|fromString
specifier|public
specifier|static
name|ExecutionMode
name|fromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
for|for
control|(
name|ExecutionMode
name|mode
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|mode
operator|.
name|parseField
operator|.
name|match
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|mode
return|;
block|}
block|}
throw|throw
operator|new
name|ElasticsearchIllegalArgumentException
argument_list|(
literal|"Unknown `execution_hint`: ["
operator|+
name|value
operator|+
literal|"], expected any of "
operator|+
name|values
argument_list|()
argument_list|)
throw|;
block|}
DECL|field|parseField
specifier|private
specifier|final
name|ParseField
name|parseField
decl_stmt|;
DECL|method|ExecutionMode
name|ExecutionMode
parameter_list|(
name|ParseField
name|parseField
parameter_list|)
block|{
name|this
operator|.
name|parseField
operator|=
name|parseField
expr_stmt|;
block|}
DECL|method|create
specifier|abstract
name|Aggregator
name|create
parameter_list|(
name|String
name|name
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
name|ValuesSource
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
name|long
name|minDocCount
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|)
function_decl|;
DECL|method|needsGlobalOrdinals
specifier|abstract
name|boolean
name|needsGlobalOrdinals
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|parseField
operator|.
name|getPreferredName
argument_list|()
return|;
block|}
block|}
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
DECL|field|minDocCount
specifier|private
specifier|final
name|long
name|minDocCount
decl_stmt|;
DECL|field|includeExclude
specifier|private
specifier|final
name|IncludeExclude
name|includeExclude
decl_stmt|;
DECL|field|executionHint
specifier|private
specifier|final
name|String
name|executionHint
decl_stmt|;
DECL|method|TermsAggregatorFactory
specifier|public
name|TermsAggregatorFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|ValuesSourceConfig
name|config
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
name|long
name|minDocCount
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|String
name|executionHint
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|StringTerms
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|order
operator|=
name|order
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
name|this
operator|.
name|minDocCount
operator|=
name|minDocCount
expr_stmt|;
name|this
operator|.
name|includeExclude
operator|=
name|includeExclude
expr_stmt|;
name|this
operator|.
name|executionHint
operator|=
name|executionHint
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
specifier|final
name|InternalAggregation
name|aggregation
init|=
operator|new
name|UnmappedTerms
argument_list|(
name|name
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|minDocCount
argument_list|)
decl_stmt|;
return|return
operator|new
name|NonCollectingAggregator
argument_list|(
name|name
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|buildEmptyAggregation
parameter_list|()
block|{
return|return
name|aggregation
return|;
block|}
block|}
return|;
block|}
DECL|method|hasParentBucketAggregator
specifier|private
specifier|static
name|boolean
name|hasParentBucketAggregator
parameter_list|(
name|Aggregator
name|parent
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|parent
operator|.
name|bucketAggregationMode
argument_list|()
operator|==
name|BucketAggregationMode
operator|.
name|PER_BUCKET
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|hasParentBucketAggregator
argument_list|(
name|parent
operator|.
name|parent
argument_list|()
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|protected
name|Aggregator
name|create
parameter_list|(
name|ValuesSource
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
name|long
name|estimatedBucketCount
init|=
name|valuesSource
operator|.
name|metaData
argument_list|()
operator|.
name|maxAtomicUniqueValuesCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|estimatedBucketCount
operator|<
literal|0
condition|)
block|{
comment|// there isn't an estimation available.. 50 should be a good start
name|estimatedBucketCount
operator|=
literal|50
expr_stmt|;
block|}
comment|// adding an upper bound on the estimation as some atomic field data in the future (binary doc values) and not
comment|// going to know their exact cardinality and will return upper bounds in AtomicFieldData.getNumberUniqueValues()
comment|// that may be largely over-estimated.. the value chosen here is arbitrary just to play nice with typical CPU cache
comment|//
comment|// Another reason is that it may be faster to resize upon growth than to start directly with the appropriate size.
comment|// And that all values are not necessarily visited by the matches.
name|estimatedBucketCount
operator|=
name|Math
operator|.
name|min
argument_list|(
name|estimatedBucketCount
argument_list|,
literal|512
argument_list|)
expr_stmt|;
if|if
condition|(
name|valuesSource
operator|instanceof
name|ValuesSource
operator|.
name|Bytes
condition|)
block|{
name|ExecutionMode
name|execution
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|executionHint
operator|!=
literal|null
condition|)
block|{
name|execution
operator|=
name|ExecutionMode
operator|.
name|fromString
argument_list|(
name|executionHint
argument_list|)
expr_stmt|;
block|}
comment|// In some cases, using ordinals is just not supported: override it
if|if
condition|(
operator|!
operator|(
name|valuesSource
operator|instanceof
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
operator|)
condition|)
block|{
name|execution
operator|=
name|ExecutionMode
operator|.
name|MAP
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
name|execution
operator|=
name|ExecutionMode
operator|.
name|MAP
expr_stmt|;
block|}
comment|// Let's try to use a good default
if|if
condition|(
name|execution
operator|==
literal|null
condition|)
block|{
comment|// if there is a parent bucket aggregator the number of instances of this aggregator is going
comment|// to be unbounded and most instances may only aggregate few documents, so use hashed based
comment|// global ordinals to keep the bucket ords dense.
if|if
condition|(
name|hasParentBucketAggregator
argument_list|(
name|parent
argument_list|)
condition|)
block|{
name|execution
operator|=
name|ExecutionMode
operator|.
name|GLOBAL_ORDINALS_HASH
expr_stmt|;
block|}
else|else
block|{
name|execution
operator|=
name|ExecutionMode
operator|.
name|GLOBAL_ORDINALS
expr_stmt|;
block|}
block|}
assert|assert
name|execution
operator|!=
literal|null
assert|;
name|valuesSource
operator|.
name|setNeedsGlobalOrdinals
argument_list|(
name|execution
operator|.
name|needsGlobalOrdinals
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|execution
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|valuesSource
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|includeExclude
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Aggregation ["
operator|+
name|name
operator|+
literal|"] cannot support the include/exclude "
operator|+
literal|"settings as it can only be applied to string values"
argument_list|)
throw|;
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
if|if
condition|(
operator|(
operator|(
name|ValuesSource
operator|.
name|Numeric
operator|)
name|valuesSource
operator|)
operator|.
name|isFloatingPoint
argument_list|()
condition|)
block|{
return|return
operator|new
name|DoubleTermsAggregator
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
operator|(
name|ValuesSource
operator|.
name|Numeric
operator|)
name|valuesSource
argument_list|,
name|config
operator|.
name|format
argument_list|()
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
return|return
operator|new
name|LongTermsAggregator
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
operator|(
name|ValuesSource
operator|.
name|Numeric
operator|)
name|valuesSource
argument_list|,
name|config
operator|.
name|format
argument_list|()
argument_list|,
name|estimatedBucketCount
argument_list|,
name|order
argument_list|,
name|requiredSize
argument_list|,
name|shardSize
argument_list|,
name|minDocCount
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"terms aggregation cannot be applied to field ["
operator|+
name|config
operator|.
name|fieldContext
argument_list|()
operator|.
name|field
argument_list|()
operator|+
literal|"]. It can only be applied to numeric or string fields."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

