begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.movavg
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
name|movavg
package|;
end_package

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
name|Function
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
name|collect
operator|.
name|EvictingQueue
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
name|collect
operator|.
name|Lists
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
name|search
operator|.
name|aggregations
operator|.
name|Aggregation
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
name|InternalSimpleValue
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
name|pipeline
operator|.
name|movavg
operator|.
name|models
operator|.
name|MovAvgModel
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
name|movavg
operator|.
name|models
operator|.
name|MovAvgModelStreams
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
DECL|class|MovAvgPipelineAggregator
specifier|public
class|class
name|MovAvgPipelineAggregator
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
literal|"moving_avg"
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
name|MovAvgPipelineAggregator
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|MovAvgPipelineAggregator
name|result
init|=
operator|new
name|MovAvgPipelineAggregator
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
DECL|field|FUNCTION
specifier|private
specifier|static
specifier|final
name|Function
argument_list|<
name|Aggregation
argument_list|,
name|InternalAggregation
argument_list|>
name|FUNCTION
init|=
operator|new
name|Function
argument_list|<
name|Aggregation
argument_list|,
name|InternalAggregation
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalAggregation
name|apply
parameter_list|(
name|Aggregation
name|input
parameter_list|)
block|{
return|return
operator|(
name|InternalAggregation
operator|)
name|input
return|;
block|}
block|}
decl_stmt|;
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
DECL|field|window
specifier|private
name|int
name|window
decl_stmt|;
DECL|field|model
specifier|private
name|MovAvgModel
name|model
decl_stmt|;
DECL|field|predict
specifier|private
name|int
name|predict
decl_stmt|;
DECL|method|MovAvgPipelineAggregator
specifier|public
name|MovAvgPipelineAggregator
parameter_list|()
block|{     }
DECL|method|MovAvgPipelineAggregator
specifier|public
name|MovAvgPipelineAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|GapPolicy
name|gapPolicy
parameter_list|,
name|int
name|window
parameter_list|,
name|int
name|predict
parameter_list|,
name|MovAvgModel
name|model
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
name|window
operator|=
name|window
expr_stmt|;
name|this
operator|.
name|model
operator|=
name|model
expr_stmt|;
name|this
operator|.
name|predict
operator|=
name|predict
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
name|EvictingQueue
argument_list|<
name|Double
argument_list|>
name|values
init|=
name|EvictingQueue
operator|.
name|create
argument_list|(
name|this
operator|.
name|window
argument_list|)
decl_stmt|;
name|long
name|lastKey
init|=
literal|0
decl_stmt|;
name|Object
name|currentKey
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
name|currentKey
operator|=
name|bucket
operator|.
name|getKey
argument_list|()
expr_stmt|;
comment|// Default is to reuse existing bucket.  Simplifies the rest of the logic,
comment|// since we only change newBucket if we can add to it
name|InternalHistogram
operator|.
name|Bucket
name|newBucket
init|=
name|bucket
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|thisBucketValue
operator|==
literal|null
operator|||
name|thisBucketValue
operator|.
name|equals
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
operator|)
condition|)
block|{
name|values
operator|.
name|offer
argument_list|(
name|thisBucketValue
argument_list|)
expr_stmt|;
comment|// Some models (e.g. HoltWinters) have certain preconditions that must be met
if|if
condition|(
name|model
operator|.
name|hasValue
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
condition|)
block|{
name|double
name|movavg
init|=
name|model
operator|.
name|next
argument_list|(
name|values
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|asList
argument_list|()
argument_list|,
name|AGGREGATION_TRANFORM_FUNCTION
argument_list|)
argument_list|)
decl_stmt|;
name|aggs
operator|.
name|add
argument_list|(
operator|new
name|InternalSimpleValue
argument_list|(
name|name
argument_list|()
argument_list|,
name|movavg
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
name|newBucket
operator|=
name|factory
operator|.
name|createBucket
argument_list|(
name|currentKey
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
expr_stmt|;
block|}
block|}
name|newBuckets
operator|.
name|add
argument_list|(
name|newBucket
argument_list|)
expr_stmt|;
if|if
condition|(
name|predict
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|currentKey
operator|instanceof
name|Number
condition|)
block|{
name|lastKey
operator|=
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
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentKey
operator|instanceof
name|DateTime
condition|)
block|{
name|lastKey
operator|=
operator|(
operator|(
name|DateTime
operator|)
name|bucket
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|getMillis
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AggregationExecutionException
argument_list|(
literal|"Expected key of type Number or DateTime but got ["
operator|+
name|currentKey
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|buckets
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|predict
operator|>
literal|0
condition|)
block|{
name|boolean
name|keyed
decl_stmt|;
name|ValueFormatter
name|formatter
decl_stmt|;
name|keyed
operator|=
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKeyed
argument_list|()
expr_stmt|;
name|formatter
operator|=
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getFormatter
argument_list|()
expr_stmt|;
name|double
index|[]
name|predictions
init|=
name|model
operator|.
name|predict
argument_list|(
name|values
argument_list|,
name|predict
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
name|predictions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|aggs
operator|.
name|add
argument_list|(
operator|new
name|InternalSimpleValue
argument_list|(
name|name
argument_list|()
argument_list|,
name|predictions
index|[
name|i
index|]
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
name|long
name|newKey
init|=
name|histo
operator|.
name|getRounding
argument_list|()
operator|.
name|nextRoundingValue
argument_list|(
name|lastKey
argument_list|)
decl_stmt|;
name|InternalHistogram
operator|.
name|Bucket
name|newBucket
init|=
name|factory
operator|.
name|createBucket
argument_list|(
name|newKey
argument_list|,
literal|0
argument_list|,
operator|new
name|InternalAggregations
argument_list|(
name|aggs
argument_list|)
argument_list|,
name|keyed
argument_list|,
name|formatter
argument_list|)
decl_stmt|;
name|newBuckets
operator|.
name|add
argument_list|(
name|newBucket
argument_list|)
expr_stmt|;
name|lastKey
operator|=
name|newKey
expr_stmt|;
block|}
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
name|window
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|predict
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|model
operator|=
name|MovAvgModelStreams
operator|.
name|read
argument_list|(
name|in
argument_list|)
expr_stmt|;
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
name|out
operator|.
name|writeVInt
argument_list|(
name|window
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|predict
argument_list|)
expr_stmt|;
name|model
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
DECL|class|Factory
specifier|public
specifier|static
class|class
name|Factory
extends|extends
name|PipelineAggregatorFactory
block|{
DECL|field|formatter
specifier|private
specifier|final
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|field|gapPolicy
specifier|private
name|GapPolicy
name|gapPolicy
decl_stmt|;
DECL|field|window
specifier|private
name|int
name|window
decl_stmt|;
DECL|field|model
specifier|private
name|MovAvgModel
name|model
decl_stmt|;
DECL|field|predict
specifier|private
name|int
name|predict
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
parameter_list|,
annotation|@
name|Nullable
name|ValueFormatter
name|formatter
parameter_list|,
name|GapPolicy
name|gapPolicy
parameter_list|,
name|int
name|window
parameter_list|,
name|int
name|predict
parameter_list|,
name|MovAvgModel
name|model
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
name|window
operator|=
name|window
expr_stmt|;
name|this
operator|.
name|model
operator|=
name|model
expr_stmt|;
name|this
operator|.
name|predict
operator|=
name|predict
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
return|return
operator|new
name|MovAvgPipelineAggregator
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|,
name|formatter
argument_list|,
name|gapPolicy
argument_list|,
name|window
argument_list|,
name|predict
argument_list|,
name|model
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
name|pipelineAggregatoractories
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
literal|"moving average aggregation ["
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
literal|"parent histogram of moving average aggregation ["
operator|+
name|name
operator|+
literal|"] must have min_doc_count of 0"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

