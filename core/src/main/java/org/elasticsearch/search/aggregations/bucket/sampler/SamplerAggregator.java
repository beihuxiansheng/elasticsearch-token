begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.sampler
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
name|sampler
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
name|AggregatorFactory
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
name|BestDocsDeferringCollector
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
name|DeferringBucketCollector
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
comment|/**  * Aggregate on only the top-scoring docs on a shard.  *  * TODO currently the diversity feature of this agg offers only 'script' and  * 'field' as a means of generating a de-dup value. In future it would be nice  * if users could use any of the "bucket" aggs syntax (geo, date histogram...)  * as the basis for generating de-dup values. Their syntax for creating bucket  * values would be preferable to users having to recreate this logic in a  * 'script' e.g. to turn a datetime in milliseconds into a month key value.  */
end_comment

begin_class
DECL|class|SamplerAggregator
specifier|public
class|class
name|SamplerAggregator
extends|extends
name|SingleBucketAggregator
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
name|int
name|shardSize
parameter_list|,
name|int
name|maxDocsPerValue
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|AggregationContext
name|context
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
return|return
operator|new
name|DiversifiedMapSamplerAggregator
argument_list|(
name|name
argument_list|,
name|shardSize
argument_list|,
name|factories
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|,
name|valuesSource
argument_list|,
name|maxDocsPerValue
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
DECL|method|BYTES_HASH
DECL|method|BYTES_HASH
name|BYTES_HASH
argument_list|(
operator|new
name|ParseField
argument_list|(
literal|"bytes_hash"
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
name|int
name|shardSize
parameter_list|,
name|int
name|maxDocsPerValue
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|AggregationContext
name|context
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
return|return
operator|new
name|DiversifiedBytesHashSamplerAggregator
argument_list|(
name|name
argument_list|,
name|shardSize
argument_list|,
name|factories
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|,
name|valuesSource
argument_list|,
name|maxDocsPerValue
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
name|int
name|shardSize
parameter_list|,
name|int
name|maxDocsPerValue
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|AggregationContext
name|context
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
return|return
operator|new
name|DiversifiedOrdinalsSamplerAggregator
argument_list|(
name|name
argument_list|,
name|shardSize
argument_list|,
name|factories
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
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
name|maxDocsPerValue
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
name|int
name|shardSize
parameter_list|,
name|int
name|maxDocsPerValue
parameter_list|,
name|ValuesSource
name|valuesSource
parameter_list|,
name|AggregationContext
name|context
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
DECL|field|shardSize
specifier|protected
specifier|final
name|int
name|shardSize
decl_stmt|;
DECL|field|bdd
specifier|protected
name|BestDocsDeferringCollector
name|bdd
decl_stmt|;
DECL|method|SamplerAggregator
specifier|public
name|SamplerAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|AggregatorFactories
name|factories
parameter_list|,
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
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardSize
operator|=
name|shardSize
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
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getDeferringCollector
specifier|public
name|DeferringBucketCollector
name|getDeferringCollector
parameter_list|()
block|{
name|bdd
operator|=
operator|new
name|BestDocsDeferringCollector
argument_list|(
name|shardSize
argument_list|,
name|context
operator|.
name|bigArrays
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|bdd
return|;
block|}
annotation|@
name|Override
DECL|method|shouldDefer
specifier|protected
name|boolean
name|shouldDefer
parameter_list|(
name|Aggregator
name|aggregator
parameter_list|)
block|{
return|return
literal|true
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
name|runDeferredCollections
argument_list|(
name|owningBucketOrdinal
argument_list|)
expr_stmt|;
return|return
operator|new
name|InternalSampler
argument_list|(
name|name
argument_list|,
name|bdd
operator|==
literal|null
condition|?
literal|0
else|:
name|bdd
operator|.
name|getDocCount
argument_list|(
name|owningBucketOrdinal
argument_list|)
argument_list|,
name|bucketAggregations
argument_list|(
name|owningBucketOrdinal
argument_list|)
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
name|InternalSampler
argument_list|(
name|name
argument_list|,
literal|0
argument_list|,
name|buildEmptySubAggregations
argument_list|()
argument_list|,
name|pipelineAggregators
argument_list|()
argument_list|,
name|metaData
argument_list|()
argument_list|)
return|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|AggregatorFactory
block|{
DECL|field|shardSize
specifier|private
name|int
name|shardSize
decl_stmt|;
DECL|method|Factory
specifier|public
name|Factory
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|shardSize
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalSampler
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInternal
specifier|public
name|Aggregator
name|createInternal
parameter_list|(
name|AggregationContext
name|context
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
return|return
operator|new
name|SamplerAggregator
argument_list|(
name|name
argument_list|,
name|shardSize
argument_list|,
name|factories
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
return|;
block|}
block|}
DECL|class|DiversifiedFactory
specifier|public
specifier|static
class|class
name|DiversifiedFactory
extends|extends
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
argument_list|>
block|{
DECL|field|shardSize
specifier|private
name|int
name|shardSize
decl_stmt|;
DECL|field|maxDocsPerValue
specifier|private
name|int
name|maxDocsPerValue
decl_stmt|;
DECL|field|executionHint
specifier|private
name|String
name|executionHint
decl_stmt|;
DECL|method|DiversifiedFactory
specifier|public
name|DiversifiedFactory
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|shardSize
parameter_list|,
name|String
name|executionHint
parameter_list|,
name|ValuesSourceConfig
name|vsConfig
parameter_list|,
name|int
name|maxDocsPerValue
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|InternalSampler
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|,
name|vsConfig
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardSize
operator|=
name|shardSize
expr_stmt|;
name|this
operator|.
name|maxDocsPerValue
operator|=
name|maxDocsPerValue
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
DECL|method|doCreateInternal
specifier|protected
name|Aggregator
name|doCreateInternal
parameter_list|(
name|ValuesSource
name|valuesSource
parameter_list|,
name|AggregationContext
name|context
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
name|valuesSource
operator|instanceof
name|ValuesSource
operator|.
name|Numeric
condition|)
block|{
return|return
operator|new
name|DiversifiedNumericSamplerAggregator
argument_list|(
name|name
argument_list|,
name|shardSize
argument_list|,
name|factories
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|,
operator|(
name|Numeric
operator|)
name|valuesSource
argument_list|,
name|maxDocsPerValue
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
name|context
operator|.
name|searchContext
argument_list|()
operator|.
name|parseFieldMatcher
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// In some cases using ordinals is just not supported: override
comment|// it
if|if
condition|(
name|execution
operator|==
literal|null
condition|)
block|{
name|execution
operator|=
name|ExecutionMode
operator|.
name|GLOBAL_ORDINALS
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|execution
operator|.
name|needsGlobalOrdinals
argument_list|()
operator|)
operator|&&
operator|(
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
return|return
name|execution
operator|.
name|create
argument_list|(
name|name
argument_list|,
name|factories
argument_list|,
name|shardSize
argument_list|,
name|maxDocsPerValue
argument_list|,
name|valuesSource
argument_list|,
name|context
argument_list|,
name|parent
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
literal|"Sampler aggregation cannot be applied to field ["
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
name|UnmappedSampler
name|aggregation
init|=
operator|new
name|UnmappedSampler
argument_list|(
name|name
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
block|}
annotation|@
name|Override
DECL|method|getLeafCollector
specifier|protected
name|LeafBucketCollector
name|getLeafCollector
parameter_list|(
name|LeafReaderContext
name|ctx
parameter_list|,
name|LeafBucketCollector
name|sub
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bdd
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Sampler aggregation must be used with child aggregations."
argument_list|)
throw|;
block|}
return|return
name|bdd
operator|.
name|getLeafCollector
argument_list|(
name|ctx
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
name|bdd
argument_list|)
expr_stmt|;
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

