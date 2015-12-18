begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.bucketmetrics.percentile
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
name|bucketmetrics
operator|.
name|percentile
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
name|bucketmetrics
operator|.
name|BucketMetricsPipelineAggregator
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|GapPolicy
import|;
end_import

begin_class
DECL|class|PercentilesBucketPipelineAggregator
specifier|public
class|class
name|PercentilesBucketPipelineAggregator
extends|extends
name|BucketMetricsPipelineAggregator
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
literal|"percentiles_bucket"
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
name|PercentilesBucketPipelineAggregator
name|readResult
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|PercentilesBucketPipelineAggregator
name|result
init|=
operator|new
name|PercentilesBucketPipelineAggregator
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
name|InternalPercentilesBucket
operator|.
name|registerStreams
argument_list|()
expr_stmt|;
block|}
DECL|field|percents
specifier|private
name|double
index|[]
name|percents
decl_stmt|;
DECL|field|data
specifier|private
name|List
argument_list|<
name|Double
argument_list|>
name|data
decl_stmt|;
DECL|method|PercentilesBucketPipelineAggregator
specifier|private
name|PercentilesBucketPipelineAggregator
parameter_list|()
block|{     }
DECL|method|PercentilesBucketPipelineAggregator
specifier|protected
name|PercentilesBucketPipelineAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|double
index|[]
name|percents
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|,
name|GapPolicy
name|gapPolicy
parameter_list|,
name|ValueFormatter
name|formatter
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|,
name|gapPolicy
argument_list|,
name|formatter
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
name|this
operator|.
name|percents
operator|=
name|percents
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
DECL|method|preCollection
specifier|protected
name|void
name|preCollection
parameter_list|()
block|{
name|data
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1024
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|collectBucketValue
specifier|protected
name|void
name|collectBucketValue
parameter_list|(
name|String
name|bucketKey
parameter_list|,
name|Double
name|bucketValue
parameter_list|)
block|{
name|data
operator|.
name|add
argument_list|(
name|bucketValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildAggregation
specifier|protected
name|InternalAggregation
name|buildAggregation
parameter_list|(
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
name|metadata
parameter_list|)
block|{
comment|// Perform the sorting and percentile collection now that all the data
comment|// has been collected.
name|Collections
operator|.
name|sort
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|double
index|[]
name|percentiles
init|=
operator|new
name|double
index|[
name|percents
operator|.
name|length
index|]
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|percents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|percentiles
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|percents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
call|(
name|int
call|)
argument_list|(
operator|(
name|percents
index|[
name|i
index|]
operator|/
literal|100.0
operator|)
operator|*
name|data
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|percentiles
index|[
name|i
index|]
operator|=
name|data
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
comment|// todo need postCollection() to clean up temp sorted data?
return|return
operator|new
name|InternalPercentilesBucket
argument_list|(
name|name
argument_list|()
argument_list|,
name|percents
argument_list|,
name|percentiles
argument_list|,
name|formatter
argument_list|,
name|pipelineAggregators
argument_list|,
name|metadata
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
name|super
operator|.
name|doReadFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|percents
operator|=
name|in
operator|.
name|readDoubleArray
argument_list|()
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
name|super
operator|.
name|doWriteTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDoubleArray
argument_list|(
name|percents
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
specifier|final
name|GapPolicy
name|gapPolicy
decl_stmt|;
DECL|field|percents
specifier|private
specifier|final
name|double
index|[]
name|percents
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
name|GapPolicy
name|gapPolicy
parameter_list|,
name|ValueFormatter
name|formatter
parameter_list|,
name|double
index|[]
name|percents
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
name|gapPolicy
operator|=
name|gapPolicy
expr_stmt|;
name|this
operator|.
name|formatter
operator|=
name|formatter
expr_stmt|;
name|this
operator|.
name|percents
operator|=
name|percents
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
name|PercentilesBucketPipelineAggregator
argument_list|(
name|name
argument_list|,
name|percents
argument_list|,
name|bucketsPaths
argument_list|,
name|gapPolicy
argument_list|,
name|formatter
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
for|for
control|(
name|Double
name|p
range|:
name|percents
control|)
block|{
if|if
condition|(
name|p
operator|==
literal|null
operator|||
name|p
argument_list|<
literal|0.0
operator|||
name|p
argument_list|>
literal|100.0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|PercentilesBucketParser
operator|.
name|PERCENTS
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|" must only contain non-null doubles from 0.0-100.0 inclusive"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

