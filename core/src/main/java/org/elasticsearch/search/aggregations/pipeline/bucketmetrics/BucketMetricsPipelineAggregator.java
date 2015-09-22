begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.bucketmetrics
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
name|Aggregations
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
name|InternalMultiBucketAggregation
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
name|MultiBucketsAggregation
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
name|pipeline
operator|.
name|BucketHelpers
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
name|SiblingPipelineAggregator
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
name|AggregationPath
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

begin_comment
comment|/**  * A class of sibling pipeline aggregations which calculate metrics across the  * buckets of a sibling aggregation  */
end_comment

begin_class
DECL|class|BucketMetricsPipelineAggregator
specifier|public
specifier|abstract
class|class
name|BucketMetricsPipelineAggregator
extends|extends
name|SiblingPipelineAggregator
block|{
DECL|field|formatter
specifier|protected
name|ValueFormatter
name|formatter
decl_stmt|;
DECL|field|gapPolicy
specifier|protected
name|GapPolicy
name|gapPolicy
decl_stmt|;
DECL|method|BucketMetricsPipelineAggregator
specifier|public
name|BucketMetricsPipelineAggregator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|BucketMetricsPipelineAggregator
specifier|protected
name|BucketMetricsPipelineAggregator
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
name|metaData
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
block|}
annotation|@
name|Override
DECL|method|doReduce
specifier|public
specifier|final
name|InternalAggregation
name|doReduce
parameter_list|(
name|Aggregations
name|aggregations
parameter_list|,
name|ReduceContext
name|context
parameter_list|)
block|{
name|preCollection
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|bucketsPath
init|=
name|AggregationPath
operator|.
name|parse
argument_list|(
name|bucketsPaths
argument_list|()
index|[
literal|0
index|]
argument_list|)
operator|.
name|getPathElementsAsStringList
argument_list|()
decl_stmt|;
for|for
control|(
name|Aggregation
name|aggregation
range|:
name|aggregations
control|)
block|{
if|if
condition|(
name|aggregation
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|bucketsPath
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|bucketsPath
operator|=
name|bucketsPath
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|bucketsPath
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|InternalMultiBucketAggregation
name|multiBucketsAgg
init|=
operator|(
name|InternalMultiBucketAggregation
operator|)
name|aggregation
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|Bucket
argument_list|>
name|buckets
init|=
name|multiBucketsAgg
operator|.
name|getBuckets
argument_list|()
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
name|buckets
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Bucket
name|bucket
init|=
name|buckets
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Double
name|bucketValue
init|=
name|BucketHelpers
operator|.
name|resolveBucketValue
argument_list|(
name|multiBucketsAgg
argument_list|,
name|bucket
argument_list|,
name|bucketsPath
argument_list|,
name|gapPolicy
argument_list|)
decl_stmt|;
if|if
condition|(
name|bucketValue
operator|!=
literal|null
operator|&&
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|bucketValue
argument_list|)
condition|)
block|{
name|collectBucketValue
argument_list|(
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|,
name|bucketValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|buildAggregation
argument_list|(
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|metaData
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Called before initial collection and between successive collection runs.      * A chance to initialize or re-initialize state      */
DECL|method|preCollection
specifier|protected
name|void
name|preCollection
parameter_list|()
block|{     }
comment|/**      * Called after a collection run is finished to build the aggregation for      * the collected state.      *      * @param pipelineAggregators      *            the pipeline aggregators to add to the resulting aggregation      * @param metadata      *            the metadata to add to the resulting aggregation      */
DECL|method|buildAggregation
specifier|protected
specifier|abstract
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
function_decl|;
comment|/**      * Called for each bucket with a value so the state can be modified based on      * the key and metric value for this bucket      *      * @param bucketKey      *            the key for this bucket as a String      * @param bucketValue      *            the value of the metric specified in<code>bucketsPath</code>      *            for this bucket      */
DECL|method|collectBucketValue
specifier|protected
specifier|abstract
name|void
name|collectBucketValue
parameter_list|(
name|String
name|bucketKey
parameter_list|,
name|Double
name|bucketValue
parameter_list|)
function_decl|;
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
block|}
block|}
end_class

end_unit

