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
name|search
operator|.
name|IndexSearcher
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
name|common
operator|.
name|ParseFieldMatcher
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
name|AggregationExecutionException
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
name|Aggregator
operator|.
name|SubAggCollectionMode
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
name|NonCollectingAggregator
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
DECL|class|TermsAggregatorFactory
specifier|public
class|class
name|TermsAggregatorFactory
extends|extends
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
argument_list|>
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
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|TermsAggregator
operator|.
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
name|subAggCollectMode
parameter_list|,
name|boolean
name|showTermDocCountError
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
specifier|final
name|IncludeExclude
operator|.
name|StringFilter
name|filter
init|=
name|includeExclude
operator|==
literal|null
condition|?
literal|null
else|:
name|includeExclude
operator|.
name|convertToStringFilter
argument_list|()
decl_stmt|;
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
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|filter
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|subAggCollectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|TermsAggregator
operator|.
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
name|subAggCollectMode
parameter_list|,
name|boolean
name|showTermDocCountError
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
specifier|final
name|IncludeExclude
operator|.
name|OrdinalsFilter
name|filter
init|=
name|includeExclude
operator|==
literal|null
condition|?
literal|null
else|:
name|includeExclude
operator|.
name|convertToOrdinalsFilter
argument_list|()
decl_stmt|;
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
operator|)
name|valuesSource
argument_list|,
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|filter
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|subAggCollectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|TermsAggregator
operator|.
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
name|subAggCollectMode
parameter_list|,
name|boolean
name|showTermDocCountError
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
specifier|final
name|IncludeExclude
operator|.
name|OrdinalsFilter
name|filter
init|=
name|includeExclude
operator|==
literal|null
condition|?
literal|null
else|:
name|includeExclude
operator|.
name|convertToOrdinalsFilter
argument_list|()
decl_stmt|;
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
operator|)
name|valuesSource
argument_list|,
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|filter
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|subAggCollectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
DECL|method|GLOBAL_ORDINALS_LOW_CARDINALITY
DECL|method|GLOBAL_ORDINALS_LOW_CARDINALITY
name|GLOBAL_ORDINALS_LOW_CARDINALITY
argument_list|(
operator|new
name|ParseField
argument_list|(
literal|"global_ordinals_low_cardinality"
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
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|TermsAggregator
operator|.
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
name|subAggCollectMode
parameter_list|,
name|boolean
name|showTermDocCountError
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
if|if
condition|(
name|includeExclude
operator|!=
literal|null
operator|||
name|factories
operator|.
name|count
argument_list|()
operator|>
literal|0
comment|// we need the FieldData impl to be able to extract the
comment|// segment to global ord mapping
operator|||
name|valuesSource
operator|.
name|getClass
argument_list|()
operator|!=
name|ValuesSource
operator|.
name|Bytes
operator|.
name|FieldData
operator|.
name|class
condition|)
block|{
return|return
name|GLOBAL_ORDINALS
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|valuesSource
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
name|subAggCollectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
return|return
operator|new
name|GlobalOrdinalsStringTermsAggregator
operator|.
name|LowCardinality
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
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|subAggCollectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
parameter_list|,
name|ParseFieldMatcher
name|parseFieldMatcher
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
name|parseFieldMatcher
operator|.
name|match
argument_list|(
name|value
argument_list|,
name|mode
operator|.
name|parseField
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
name|IllegalArgumentException
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
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|TermsAggregator
operator|.
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
name|subAggCollectMode
parameter_list|,
name|boolean
name|showTermDocCountError
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
name|Terms
operator|.
name|Order
name|order
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
DECL|field|collectMode
specifier|private
specifier|final
name|SubAggCollectionMode
name|collectMode
decl_stmt|;
DECL|field|bucketCountThresholds
specifier|private
specifier|final
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
decl_stmt|;
DECL|field|showTermDocCountError
specifier|private
specifier|final
name|boolean
name|showTermDocCountError
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
name|Terms
operator|.
name|Order
name|order
parameter_list|,
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|,
name|IncludeExclude
name|includeExclude
parameter_list|,
name|String
name|executionHint
parameter_list|,
name|SubAggCollectionMode
name|executionMode
parameter_list|,
name|boolean
name|showTermDocCountError
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
name|this
operator|.
name|bucketCountThresholds
operator|=
name|bucketCountThresholds
expr_stmt|;
name|this
operator|.
name|collectMode
operator|=
name|executionMode
expr_stmt|;
name|this
operator|.
name|showTermDocCountError
operator|=
name|showTermDocCountError
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
name|pipelineAggregators
argument_list|,
name|metaData
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
argument_list|,
name|factories
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
block|{
block|{
comment|// even in the case of an unmapped aggregator, validate the order
name|InternalOrder
operator|.
name|validate
parameter_list|(
name|order
parameter_list|,
name|this
parameter_list|)
constructor_decl|;
block|}
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
annotation|@
name|Override
DECL|method|doCreateInternal
specifier|protected
name|Aggregator
name|doCreateInternal
parameter_list|(
name|ValuesSource
name|valuesSource
parameter_list|,
name|AggregationContext
name|aggregationContext
parameter_list|,
name|Aggregator
name|parent
parameter_list|,
name|boolean
name|collectsFromSingleBucket
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
if|if
condition|(
name|collectsFromSingleBucket
operator|==
literal|false
condition|)
block|{
return|return
name|asMultiBucketAggregator
argument_list|(
name|this
argument_list|,
name|aggregationContext
argument_list|,
name|parent
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
argument_list|,
name|aggregationContext
operator|.
name|searchContext
argument_list|()
operator|.
name|parseFieldMatcher
argument_list|()
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
specifier|final
name|long
name|maxOrd
decl_stmt|;
specifier|final
name|double
name|ratio
decl_stmt|;
if|if
condition|(
name|execution
operator|==
literal|null
operator|||
name|execution
operator|.
name|needsGlobalOrdinals
argument_list|()
condition|)
block|{
name|ValuesSource
operator|.
name|Bytes
operator|.
name|WithOrdinals
name|valueSourceWithOrdinals
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
name|IndexSearcher
name|indexSearcher
init|=
name|aggregationContext
operator|.
name|searchContext
argument_list|()
operator|.
name|searcher
argument_list|()
decl_stmt|;
name|maxOrd
operator|=
name|valueSourceWithOrdinals
operator|.
name|globalMaxOrd
argument_list|(
name|indexSearcher
argument_list|)
expr_stmt|;
name|ratio
operator|=
name|maxOrd
operator|/
operator|(
operator|(
name|double
operator|)
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|numDocs
argument_list|()
operator|)
expr_stmt|;
block|}
else|else
block|{
name|maxOrd
operator|=
operator|-
literal|1
expr_stmt|;
name|ratio
operator|=
operator|-
literal|1
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
name|Aggregator
operator|.
name|descendsFromBucketAggregator
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
if|if
condition|(
name|factories
operator|==
name|AggregatorFactories
operator|.
name|EMPTY
condition|)
block|{
if|if
condition|(
name|ratio
operator|<=
literal|0.5
operator|&&
name|maxOrd
operator|<=
literal|2048
condition|)
block|{
comment|// 0.5: At least we need reduce the number of global ordinals look-ups by half
comment|// 2048: GLOBAL_ORDINALS_LOW_CARDINALITY has additional memory usage, which directly linked to maxOrd, so we need to limit.
name|execution
operator|=
name|ExecutionMode
operator|.
name|GLOBAL_ORDINALS_LOW_CARDINALITY
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
block|}
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
name|collectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
if|if
condition|(
operator|(
name|includeExclude
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|includeExclude
operator|.
name|isRegexBased
argument_list|()
operator|)
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
literal|"] cannot support regular expression style include/exclude "
operator|+
literal|"settings as they can only be applied to string fields. Use an array of numeric values for include/exclude clauses used to filter numeric fields"
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
name|IncludeExclude
operator|.
name|LongFilter
name|longFilter
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
name|longFilter
operator|=
name|includeExclude
operator|.
name|convertToDoubleFilter
argument_list|()
expr_stmt|;
block|}
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
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|collectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|longFilter
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
name|longFilter
operator|=
name|includeExclude
operator|.
name|convertToLongFilter
argument_list|()
expr_stmt|;
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
name|order
argument_list|,
name|bucketCountThresholds
argument_list|,
name|aggregationContext
argument_list|,
name|parent
argument_list|,
name|collectMode
argument_list|,
name|showTermDocCountError
argument_list|,
name|longFilter
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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

