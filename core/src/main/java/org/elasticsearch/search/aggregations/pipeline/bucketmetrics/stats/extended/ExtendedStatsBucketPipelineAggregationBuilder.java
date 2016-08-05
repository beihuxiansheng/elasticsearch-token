begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.extended
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
name|stats
operator|.
name|extended
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
name|PipelineAggregationBuilder
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
name|PipelineAggregator
operator|.
name|Parser
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
name|BucketMetricsPipelineAggregationBuilder
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
DECL|class|ExtendedStatsBucketPipelineAggregationBuilder
specifier|public
class|class
name|ExtendedStatsBucketPipelineAggregationBuilder
extends|extends
name|BucketMetricsPipelineAggregationBuilder
argument_list|<
name|ExtendedStatsBucketPipelineAggregationBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"extended_stats_bucket"
decl_stmt|;
DECL|field|sigma
specifier|private
name|double
name|sigma
init|=
literal|2.0
decl_stmt|;
DECL|method|ExtendedStatsBucketPipelineAggregationBuilder
specifier|public
name|ExtendedStatsBucketPipelineAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|bucketsPath
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|NAME
argument_list|,
operator|new
name|String
index|[]
block|{
name|bucketsPath
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ExtendedStatsBucketPipelineAggregationBuilder
specifier|public
name|ExtendedStatsBucketPipelineAggregationBuilder
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
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|sigma
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|innerWriteTo
specifier|protected
name|void
name|innerWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|sigma
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set the value of sigma to use when calculating the standard deviation      * bounds      */
DECL|method|sigma
specifier|public
name|ExtendedStatsBucketPipelineAggregationBuilder
name|sigma
parameter_list|(
name|double
name|sigma
parameter_list|)
block|{
if|if
condition|(
name|sigma
operator|<
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|ExtendedStatsBucketParser
operator|.
name|SIGMA
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|" must be a non-negative double"
argument_list|)
throw|;
block|}
name|this
operator|.
name|sigma
operator|=
name|sigma
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get the value of sigma to use when calculating the standard deviation      * bounds      */
DECL|method|sigma
specifier|public
name|double
name|sigma
parameter_list|()
block|{
return|return
name|sigma
return|;
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
name|ExtendedStatsBucketPipelineAggregator
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|,
name|sigma
argument_list|,
name|gapPolicy
argument_list|()
argument_list|,
name|formatter
argument_list|()
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
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
index|[]
name|aggFactories
parameter_list|,
name|List
argument_list|<
name|PipelineAggregationBuilder
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
name|sigma
operator|<
literal|0.0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|ExtendedStatsBucketParser
operator|.
name|SIGMA
operator|.
name|getPreferredName
argument_list|()
operator|+
literal|" must be a non-negative double"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
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
name|field
argument_list|(
name|ExtendedStatsBucketParser
operator|.
name|SIGMA
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|sigma
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|innerHashCode
specifier|protected
name|int
name|innerHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|sigma
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|innerEquals
specifier|protected
name|boolean
name|innerEquals
parameter_list|(
name|BucketMetricsPipelineAggregationBuilder
argument_list|<
name|ExtendedStatsBucketPipelineAggregationBuilder
argument_list|>
name|obj
parameter_list|)
block|{
name|ExtendedStatsBucketPipelineAggregationBuilder
name|other
init|=
operator|(
name|ExtendedStatsBucketPipelineAggregationBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|sigma
argument_list|,
name|other
operator|.
name|sigma
argument_list|)
return|;
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
name|NAME
return|;
block|}
block|}
end_class

end_unit

