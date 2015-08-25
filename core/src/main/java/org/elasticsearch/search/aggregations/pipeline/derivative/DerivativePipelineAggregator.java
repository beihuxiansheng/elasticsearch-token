begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.derivative
package|package
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
name|derivative
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
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|rounding
operator|.
name|DateTimeUnit
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
name|unit
operator|.
name|TimeValue
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
name|XContentBuilder
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
name|InternalAggregation
operator|.
name|ReduceContext
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
operator|.
name|Type
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
name|histogram
operator|.
name|HistogramAggregator
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
name|histogram
operator|.
name|InternalHistogram
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
name|BucketHelpers
operator|.
name|GapPolicy
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
name|pipeline
operator|.
name|PipelineAggregatorFactory
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
name|PipelineAggregatorStreams
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
name|ValueFormatterStreams
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTime
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
import|;
end_import

begin_import
import|import static
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
name|BucketHelpers
operator|.
name|resolveBucketValue
import|;
end_import

begin_class
DECL|class|DerivativePipelineAggregator
specifier|public
class|class
name|DerivativePipelineAggregator
extends|extends
name|PipelineAggregator
block|{
DECL|field|TYPE
specifier|public
specifier|final
specifier|static
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
literal|"derivative"
argument_list|)
decl_stmt|;
DECL|field|STREAM
specifier|public
specifier|final
specifier|static
name|PipelineAggregatorStreams
operator|.
name|Stream
name|STREAM
init|=
operator|new
name|PipelineAggregatorStreams
operator|.
name|Stream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DerivativePipelineAggregator
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DerivativePipelineAggregator
name|result
init|=
operator|new
name|DerivativePipelineAggregator
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
decl_stmt|;
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
parameter_list|()
block|{
name|PipelineAggregatorStreams
operator|.
name|registerStream
argument_list|(
name|STREAM
argument_list|,
name|TYPE
operator|.
name|stream
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|formatter
specifier|private
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|field|gapPolicy
specifier|private
name|GapPolicy
name|gapPolicy
decl_stmt|;
DECL|field|xAxisUnits
specifier|private
name|Double
name|xAxisUnits
decl_stmt|;
DECL|method|DerivativePipelineAggregator
specifier|public
name|DerivativePipelineAggregator
parameter_list|()
block|{     }
DECL|method|DerivativePipelineAggregator
specifier|public
name|DerivativePipelineAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|,
name|ValueFormatter
name|formatter
parameter_list|,
name|GapPolicy
name|gapPolicy
parameter_list|,
name|Long
name|xAxisUnits
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metadata
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|this
operator|.
name|gapPolicy
operator|=
name|gapPolicy
expr_stmt|;
name|this
operator|.
name|xAxisUnits
operator|=
name|xAxisUnits
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|double
operator|)
name|xAxisUnits
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
annotation|@
name|Override
DECL|method|reduce
specifier|public
name|InternalAggregation
name|reduce
parameter_list|(
name|InternalAggregation
name|aggregation
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
name|InternalHistogram
name|histo
init|=
operator|(
name|InternalHistogram
operator|)
name|aggregation
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|InternalHistogram
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
name|histo
operator|.
name|getBuckets
argument_list|()
decl_stmt|;
name|InternalHistogram
operator|.
name|Factory
argument_list|<
name|?
extends|extends
name|InternalHistogram
operator|.
name|Bucket
argument_list|>
name|factory
init|=
name|histo
operator|.
name|getFactory
argument_list|()
decl_stmt|;
name|List
name|newBuckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Long
name|lastBucketKey
init|=
literal|null
decl_stmt|;
name|Double
name|lastBucketValue
init|=
literal|null
decl_stmt|;
for|for
control|(
name|InternalHistogram
operator|.
name|Bucket
name|bucket
range|:
name|buckets
control|)
block|{
name|Long
name|thisBucketKey
init|=
name|resolveBucketKeyAsLong
argument_list|(
name|bucket
argument_list|)
decl_stmt|;
name|Double
name|thisBucketValue
init|=
name|resolveBucketValue
argument_list|(
name|histo
argument_list|,
name|bucket
argument_list|,
name|bucketsPaths
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|gapPolicy
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastBucketValue
operator|!=
literal|null
condition|)
block|{
name|double
name|gradient
init|=
name|thisBucketValue
operator|-
name|lastBucketValue
decl_stmt|;
name|double
name|xDiff
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|xAxisUnits
operator|!=
literal|null
condition|)
block|{
name|xDiff
operator|=
operator|(
name|thisBucketKey
operator|-
name|lastBucketKey
operator|)
operator|/
name|xAxisUnits
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggs
init|=
name|StreamSupport
operator|.
name|stream
argument_list|(
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
parameter_list|(
name|p
parameter_list|)
lambda|->
block|{
return|return
operator|(
name|InternalAggregation
operator|)
name|p
return|;
block|}
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|aggs
operator|.
name|add
argument_list|(
operator|new
name|InternalDerivative
argument_list|(
name|name
argument_list|()
argument_list|,
name|gradient
argument_list|,
name|xDiff
argument_list|,
name|formatter
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|PipelineAggregator
argument_list|>
argument_list|()
argument_list|,
name|metaData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|InternalHistogram
operator|.
name|Bucket
name|newBucket
init|=
name|factory
operator|.
name|createBucket
argument_list|(
name|bucket
operator|.
name|getKey
argument_list|()
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
operator|new
name|InternalAggregations
argument_list|(
name|aggs
argument_list|)
argument_list|,
name|bucket
operator|.
name|getKeyed
argument_list|()
argument_list|,
name|bucket
operator|.
name|getFormatter
argument_list|()
argument_list|)
decl_stmt|;
name|newBuckets
operator|.
name|add
argument_list|(
name|newBucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newBuckets
operator|.
name|add
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
name|lastBucketKey
operator|=
name|thisBucketKey
expr_stmt|;
name|lastBucketValue
operator|=
name|thisBucketValue
expr_stmt|;
block|}
return|return
name|factory
operator|.
name|create
argument_list|(
name|newBuckets
argument_list|,
name|histo
argument_list|)
return|;
block|}
DECL|method|resolveBucketKeyAsLong
specifier|private
name|Long
name|resolveBucketKeyAsLong
parameter_list|(
name|InternalHistogram
operator|.
name|Bucket
name|bucket
parameter_list|)
block|{
name|Object
name|key
init|=
name|bucket
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|instanceof
name|DateTime
condition|)
block|{
return|return
operator|(
operator|(
name|DateTime
operator|)
name|key
operator|)
operator|.
name|getMillis
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|key
operator|instanceof
name|Number
condition|)
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|key
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"InternalBucket keys must be either a Number or a DateTime for aggregation "
operator|+
name|name
argument_list|()
operator|+
literal|". Found bucket with key "
operator|+
name|key
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|public
name|void
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|formatter
operator|=
name|ValueFormatterStreams
operator|.
name|readOptional
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|gapPolicy
operator|=
name|GapPolicy
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|xAxisUnits
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|xAxisUnits
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|public
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|ValueFormatterStreams
operator|.
name|writeOptional
argument_list|(
name|formatter
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|gapPolicy
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|boolean
name|hasXAxisUnitsValue
init|=
name|xAxisUnits
operator|!=
literal|null
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasXAxisUnitsValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasXAxisUnitsValue
condition|)
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|xAxisUnits
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|PipelineAggregatorFactory
block|{
DECL|field|format
specifier|private
name|String
name|format
decl_stmt|;
DECL|field|gapPolicy
specifier|private
name|GapPolicy
name|gapPolicy
init|=
name|GapPolicy
operator|.
name|SKIP
decl_stmt|;
DECL|field|units
specifier|private
name|String
name|units
decl_stmt|;
DECL|method|Factory
specifier|public
name|Factory
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
operator|.
name|name
argument_list|()
argument_list|,
name|bucketsPaths
argument_list|)
expr_stmt|;
block|}
DECL|method|format
specifier|public
name|void
name|format
parameter_list|(
name|String
name|format
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
DECL|method|gapPolicy
specifier|public
name|void
name|gapPolicy
parameter_list|(
name|GapPolicy
name|gapPolicy
parameter_list|)
block|{
name|this
operator|.
name|gapPolicy
operator|=
name|gapPolicy
expr_stmt|;
block|}
DECL|method|units
specifier|public
name|void
name|units
parameter_list|(
name|String
name|units
parameter_list|)
block|{
name|this
operator|.
name|units
operator|=
name|units
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createInternal
specifier|protected
name|PipelineAggregator
name|createInternal
parameter_list|(
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
name|ValueFormatter
name|formatter
decl_stmt|;
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|formatter
operator|=
name|ValueFormat
operator|.
name|Patternable
operator|.
name|Number
operator|.
name|format
argument_list|(
name|format
argument_list|)
operator|.
name|formatter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|formatter
operator|=
name|ValueFormatter
operator|.
name|RAW
expr_stmt|;
block|}
name|Long
name|xAxisUnits
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|units
operator|!=
literal|null
condition|)
block|{
name|DateTimeUnit
name|dateTimeUnit
init|=
name|HistogramAggregator
operator|.
name|DateHistogramFactory
operator|.
name|DATE_FIELD_UNITS
operator|.
name|get
argument_list|(
name|units
argument_list|)
decl_stmt|;
if|if
condition|(
name|dateTimeUnit
operator|!=
literal|null
condition|)
block|{
name|xAxisUnits
operator|=
name|dateTimeUnit
operator|.
name|field
argument_list|()
operator|.
name|getDurationField
argument_list|()
operator|.
name|getUnitMillis
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|TimeValue
name|timeValue
init|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|units
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".unit"
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeValue
operator|!=
literal|null
condition|)
block|{
name|xAxisUnits
operator|=
name|timeValue
operator|.
name|getMillis
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|DerivativePipelineAggregator
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|,
name|formatter
argument_list|,
name|gapPolicy
argument_list|,
name|xAxisUnits
argument_list|,
name|metaData
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doValidate
specifier|public
name|void
name|doValidate
parameter_list|(
name|AggregatorFactory
name|parent
parameter_list|,
name|AggregatorFactory
index|[]
name|aggFactories
parameter_list|,
name|List
argument_list|<
name|PipelineAggregatorFactory
argument_list|>
name|pipelineAggregatorFactories
parameter_list|)
block|{
if|if
condition|(
name|bucketsPaths
operator|.
name|length
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|PipelineAggregator
operator|.
name|Parser
operator|.
name|BUCKETS_PATH
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|" must contain a single entry for aggregation ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
operator|(
name|parent
operator|instanceof
name|HistogramAggregator
operator|.
name|Factory
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"derivative aggregation ["
operator|+
name|name
operator|+
literal|"] must have a histogram or date_histogram as parent"
argument_list|)
throw|;
block|}
else|else
block|{
name|HistogramAggregator
operator|.
name|Factory
name|histoParent
init|=
operator|(
name|HistogramAggregator
operator|.
name|Factory
operator|)
name|parent
decl_stmt|;
if|if
condition|(
name|histoParent
operator|.
name|minDocCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"parent histogram of derivative aggregation ["
operator|+
name|name
operator|+
literal|"] must have min_doc_count of 0"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|PipelineAggregatorFactory
name|doReadFrom
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|,
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Factory
name|factory
init|=
operator|new
name|Factory
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|)
decl_stmt|;
name|factory
operator|.
name|format
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|gapPolicy
operator|=
name|GapPolicy
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|factory
operator|.
name|units
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
return|return
name|factory
return|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeOptionalString
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|boolean
name|hasGapPolicy
init|=
name|gapPolicy
operator|!=
literal|null
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasGapPolicy
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasGapPolicy
condition|)
block|{
name|gapPolicy
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeOptionalString
argument_list|(
name|units
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|internalXContent
specifier|protected
name|XContentBuilder
name|internalXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|format
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|DerivativeParser
operator|.
name|FORMAT
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|format
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gapPolicy
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|DerivativeParser
operator|.
name|GAP_POLICY
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|gapPolicy
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|units
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|DerivativeParser
operator|.
name|UNIT
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|units
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|Factory
name|other
init|=
operator|(
name|Factory
operator|)
name|obj
decl_stmt|;
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|format
argument_list|,
name|other
operator|.
name|format
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|gapPolicy
argument_list|,
name|other
operator|.
name|gapPolicy
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Objects
operator|.
name|equals
argument_list|(
name|units
argument_list|,
name|other
operator|.
name|units
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|format
argument_list|,
name|gapPolicy
argument_list|,
name|units
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

