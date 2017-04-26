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
name|DocValueFormat
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
name|InternalNumericMetricsAggregation
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
name|max
operator|.
name|InternalMax
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
name|percentiles
operator|.
name|Percentile
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_class
DECL|class|InternalPercentilesBucket
specifier|public
class|class
name|InternalPercentilesBucket
extends|extends
name|InternalNumericMetricsAggregation
operator|.
name|MultiValue
implements|implements
name|PercentilesBucket
block|{
DECL|field|percentiles
specifier|private
name|double
index|[]
name|percentiles
decl_stmt|;
DECL|field|percents
specifier|private
name|double
index|[]
name|percents
decl_stmt|;
DECL|field|percentileLookups
specifier|private
specifier|final
specifier|transient
name|Map
argument_list|<
name|Double
argument_list|,
name|Double
argument_list|>
name|percentileLookups
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|InternalPercentilesBucket
specifier|public
name|InternalPercentilesBucket
parameter_list|(
name|String
name|name
parameter_list|,
name|double
index|[]
name|percents
parameter_list|,
name|double
index|[]
name|percentiles
parameter_list|,
name|DocValueFormat
name|formatter
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
block|{
name|super
argument_list|(
name|name
argument_list|,
name|pipelineAggregators
argument_list|,
name|metaData
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|percentiles
operator|.
name|length
operator|==
name|percents
operator|.
name|length
operator|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The number of provided percents and percentiles didn't match. percents: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|percents
argument_list|)
operator|+
literal|", percentiles: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|percentiles
argument_list|)
argument_list|)
throw|;
block|}
name|this
operator|.
name|format
operator|=
name|formatter
expr_stmt|;
name|this
operator|.
name|percentiles
operator|=
name|percentiles
expr_stmt|;
name|this
operator|.
name|percents
operator|=
name|percents
expr_stmt|;
name|computeLookup
argument_list|()
expr_stmt|;
block|}
DECL|method|computeLookup
specifier|private
name|void
name|computeLookup
parameter_list|()
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
name|percentileLookups
operator|.
name|put
argument_list|(
name|percents
index|[
name|i
index|]
argument_list|,
name|percentiles
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Read from a stream.      */
DECL|method|InternalPercentilesBucket
specifier|public
name|InternalPercentilesBucket
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|format
operator|=
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|DocValueFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|percentiles
operator|=
name|in
operator|.
name|readDoubleArray
argument_list|()
expr_stmt|;
name|percents
operator|=
name|in
operator|.
name|readDoubleArray
argument_list|()
expr_stmt|;
name|computeLookup
argument_list|()
expr_stmt|;
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
name|writeNamedWriteable
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDoubleArray
argument_list|(
name|percentiles
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
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|PercentilesBucketPipelineAggregationBuilder
operator|.
name|NAME
return|;
block|}
annotation|@
name|Override
DECL|method|percentile
specifier|public
name|double
name|percentile
parameter_list|(
name|double
name|percent
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Double
name|percentile
init|=
name|percentileLookups
operator|.
name|get
argument_list|(
name|percent
argument_list|)
decl_stmt|;
if|if
condition|(
name|percentile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Percent requested ["
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|percent
argument_list|)
operator|+
literal|"] was not"
operator|+
literal|" one of the computed percentiles.  Available keys are: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|percents
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|percentile
return|;
block|}
annotation|@
name|Override
DECL|method|percentileAsString
specifier|public
name|String
name|percentileAsString
parameter_list|(
name|double
name|percent
parameter_list|)
block|{
return|return
name|format
operator|.
name|format
argument_list|(
name|percentile
argument_list|(
name|percent
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Percentile
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iter
argument_list|(
name|percents
argument_list|,
name|percentiles
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|value
specifier|public
name|double
name|value
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|percentile
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
name|InternalMax
name|doReduce
parameter_list|(
name|List
argument_list|<
name|InternalAggregation
argument_list|>
name|aggregations
parameter_list|,
name|ReduceContext
name|reduceContext
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|public
name|XContentBuilder
name|doXContentBody
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
name|builder
operator|.
name|startObject
argument_list|(
literal|"values"
argument_list|)
expr_stmt|;
for|for
control|(
name|double
name|percent
range|:
name|percents
control|)
block|{
name|double
name|value
init|=
name|percentile
argument_list|(
name|percent
argument_list|)
decl_stmt|;
name|boolean
name|hasValue
init|=
operator|!
operator|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
operator|||
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
operator|)
decl_stmt|;
name|String
name|key
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|percent
argument_list|)
decl_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|key
argument_list|,
name|hasValue
condition|?
name|value
else|:
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasValue
operator|&&
name|format
operator|!=
name|DocValueFormat
operator|.
name|RAW
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|key
operator|+
literal|"_as_string"
argument_list|,
name|percentileAsString
argument_list|(
name|percent
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
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
name|InternalPercentilesBucket
name|that
init|=
operator|(
name|InternalPercentilesBucket
operator|)
name|obj
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|percents
argument_list|,
name|that
operator|.
name|percents
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|percentiles
argument_list|,
name|that
operator|.
name|percentiles
argument_list|)
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|percents
argument_list|)
argument_list|,
name|Arrays
operator|.
name|hashCode
argument_list|(
name|percentiles
argument_list|)
argument_list|)
return|;
block|}
DECL|class|Iter
specifier|public
specifier|static
class|class
name|Iter
implements|implements
name|Iterator
argument_list|<
name|Percentile
argument_list|>
block|{
DECL|field|percents
specifier|private
specifier|final
name|double
index|[]
name|percents
decl_stmt|;
DECL|field|percentiles
specifier|private
specifier|final
name|double
index|[]
name|percentiles
decl_stmt|;
DECL|field|i
specifier|private
name|int
name|i
decl_stmt|;
DECL|method|Iter
specifier|public
name|Iter
parameter_list|(
name|double
index|[]
name|percents
parameter_list|,
name|double
index|[]
name|percentiles
parameter_list|)
block|{
name|this
operator|.
name|percents
operator|=
name|percents
expr_stmt|;
name|this
operator|.
name|percentiles
operator|=
name|percentiles
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|<
name|percents
operator|.
name|length
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|Percentile
name|next
parameter_list|()
block|{
specifier|final
name|Percentile
name|next
init|=
operator|new
name|Percentile
argument_list|(
name|percents
index|[
name|i
index|]
argument_list|,
name|percentiles
index|[
name|i
index|]
argument_list|)
decl_stmt|;
operator|++
name|i
expr_stmt|;
return|return
name|next
return|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
specifier|final
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

