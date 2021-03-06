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
name|bucketmetrics
operator|.
name|BucketMetricsPipelineAggregator
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

begin_class
DECL|class|ExtendedStatsBucketPipelineAggregator
specifier|public
class|class
name|ExtendedStatsBucketPipelineAggregator
extends|extends
name|BucketMetricsPipelineAggregator
block|{
DECL|field|sigma
specifier|private
specifier|final
name|double
name|sigma
decl_stmt|;
DECL|field|sum
specifier|private
name|double
name|sum
init|=
literal|0
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
init|=
literal|0
decl_stmt|;
DECL|field|min
specifier|private
name|double
name|min
init|=
name|Double
operator|.
name|POSITIVE_INFINITY
decl_stmt|;
DECL|field|max
specifier|private
name|double
name|max
init|=
name|Double
operator|.
name|NEGATIVE_INFINITY
decl_stmt|;
DECL|field|sumOfSqrs
specifier|private
name|double
name|sumOfSqrs
init|=
literal|1
decl_stmt|;
DECL|method|ExtendedStatsBucketPipelineAggregator
specifier|protected
name|ExtendedStatsBucketPipelineAggregator
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|bucketsPaths
parameter_list|,
name|double
name|sigma
parameter_list|,
name|GapPolicy
name|gapPolicy
parameter_list|,
name|DocValueFormat
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
name|sigma
operator|=
name|sigma
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|ExtendedStatsBucketPipelineAggregator
specifier|public
name|ExtendedStatsBucketPipelineAggregator
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
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|ExtendedStatsBucketPipelineAggregationBuilder
operator|.
name|NAME
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
name|sum
operator|=
literal|0
expr_stmt|;
name|count
operator|=
literal|0
expr_stmt|;
name|min
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
name|max
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
name|sumOfSqrs
operator|=
literal|0
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
name|sum
operator|+=
name|bucketValue
expr_stmt|;
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|bucketValue
argument_list|)
expr_stmt|;
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|bucketValue
argument_list|)
expr_stmt|;
name|count
operator|+=
literal|1
expr_stmt|;
name|sumOfSqrs
operator|+=
name|bucketValue
operator|*
name|bucketValue
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
return|return
operator|new
name|InternalExtendedStatsBucket
argument_list|(
name|name
argument_list|()
argument_list|,
name|count
argument_list|,
name|sum
argument_list|,
name|min
argument_list|,
name|max
argument_list|,
name|sumOfSqrs
argument_list|,
name|sigma
argument_list|,
name|format
argument_list|,
name|pipelineAggregators
argument_list|,
name|metadata
argument_list|)
return|;
block|}
block|}
end_class

end_unit

