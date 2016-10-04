begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.significant
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
name|significant
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
name|index
operator|.
name|query
operator|.
name|QueryBuilder
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
operator|.
name|Builder
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
name|bucket
operator|.
name|significant
operator|.
name|heuristics
operator|.
name|JLHScore
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
name|significant
operator|.
name|heuristics
operator|.
name|SignificanceHeuristic
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
name|TermsAggregationBuilder
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
name|TermsAggregator
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
name|TermsAggregator
operator|.
name|BucketCountThresholds
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
name|ValueType
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
name|ValuesSourceAggregationBuilder
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
name|ValuesSourceType
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
name|Objects
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SignificantTermsAggregationBuilder
specifier|public
class|class
name|SignificantTermsAggregationBuilder
extends|extends
name|ValuesSourceAggregationBuilder
argument_list|<
name|ValuesSource
argument_list|,
name|SignificantTermsAggregationBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"significant_terms"
decl_stmt|;
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|InternalAggregation
operator|.
name|Type
name|TYPE
init|=
operator|new
name|Type
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|BACKGROUND_FILTER
specifier|static
specifier|final
name|ParseField
name|BACKGROUND_FILTER
init|=
operator|new
name|ParseField
argument_list|(
literal|"background_filter"
argument_list|)
decl_stmt|;
DECL|field|HEURISTIC
specifier|static
specifier|final
name|ParseField
name|HEURISTIC
init|=
operator|new
name|ParseField
argument_list|(
literal|"significance_heuristic"
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_BUCKET_COUNT_THRESHOLDS
specifier|static
specifier|final
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|DEFAULT_BUCKET_COUNT_THRESHOLDS
init|=
operator|new
name|TermsAggregator
operator|.
name|BucketCountThresholds
argument_list|(
literal|3
argument_list|,
literal|0
argument_list|,
literal|10
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_SIGNIFICANCE_HEURISTIC
specifier|static
specifier|final
name|SignificanceHeuristic
name|DEFAULT_SIGNIFICANCE_HEURISTIC
init|=
operator|new
name|JLHScore
argument_list|()
decl_stmt|;
DECL|field|includeExclude
specifier|private
name|IncludeExclude
name|includeExclude
init|=
literal|null
decl_stmt|;
DECL|field|executionHint
specifier|private
name|String
name|executionHint
init|=
literal|null
decl_stmt|;
DECL|field|filterBuilder
specifier|private
name|QueryBuilder
name|filterBuilder
init|=
literal|null
decl_stmt|;
DECL|field|bucketCountThresholds
specifier|private
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
init|=
operator|new
name|BucketCountThresholds
argument_list|(
name|DEFAULT_BUCKET_COUNT_THRESHOLDS
argument_list|)
decl_stmt|;
DECL|field|significanceHeuristic
specifier|private
name|SignificanceHeuristic
name|significanceHeuristic
init|=
name|DEFAULT_SIGNIFICANCE_HEURISTIC
decl_stmt|;
DECL|method|SignificantTermsAggregationBuilder
specifier|public
name|SignificantTermsAggregationBuilder
parameter_list|(
name|String
name|name
parameter_list|,
name|ValueType
name|valueType
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|TYPE
argument_list|,
name|ValuesSourceType
operator|.
name|ANY
argument_list|,
name|valueType
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a Stream.      */
DECL|method|SignificantTermsAggregationBuilder
specifier|public
name|SignificantTermsAggregationBuilder
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
name|TYPE
argument_list|,
name|ValuesSourceType
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|bucketCountThresholds
operator|=
operator|new
name|BucketCountThresholds
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|executionHint
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|filterBuilder
operator|=
name|in
operator|.
name|readOptionalNamedWriteable
argument_list|(
name|QueryBuilder
operator|.
name|class
argument_list|)
expr_stmt|;
name|includeExclude
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|IncludeExclude
operator|::
operator|new
argument_list|)
expr_stmt|;
name|significanceHeuristic
operator|=
name|in
operator|.
name|readNamedWriteable
argument_list|(
name|SignificanceHeuristic
operator|.
name|class
argument_list|)
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
name|bucketCountThresholds
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|executionHint
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalNamedWriteable
argument_list|(
name|filterBuilder
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|includeExclude
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeNamedWriteable
argument_list|(
name|significanceHeuristic
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serializeTargetValueType
specifier|protected
name|boolean
name|serializeTargetValueType
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|getBucketCountThresholds
specifier|protected
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|getBucketCountThresholds
parameter_list|()
block|{
return|return
operator|new
name|TermsAggregator
operator|.
name|BucketCountThresholds
argument_list|(
name|bucketCountThresholds
argument_list|)
return|;
block|}
DECL|method|bucketCountThresholds
specifier|public
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|()
block|{
return|return
name|bucketCountThresholds
return|;
block|}
DECL|method|bucketCountThresholds
specifier|public
name|SignificantTermsAggregationBuilder
name|bucketCountThresholds
parameter_list|(
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
parameter_list|)
block|{
if|if
condition|(
name|bucketCountThresholds
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[bucketCountThresholds] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|bucketCountThresholds
operator|=
name|bucketCountThresholds
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the size - indicating how many term buckets should be returned      * (defaults to 10)      */
DECL|method|size
specifier|public
name|SignificantTermsAggregationBuilder
name|size
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[size] must be greater than 0. Found ["
operator|+
name|size
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|bucketCountThresholds
operator|.
name|setRequiredSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the shard_size - indicating the number of term buckets each shard      * will return to the coordinating node (the node that coordinates the      * search execution). The higher the shard size is, the more accurate the      * results are.      */
DECL|method|shardSize
specifier|public
name|SignificantTermsAggregationBuilder
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
if|if
condition|(
name|shardSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[shardSize] must be greater than  0. Found ["
operator|+
name|shardSize
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|bucketCountThresholds
operator|.
name|setShardSize
argument_list|(
name|shardSize
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the minimum document count terms should have in order to appear in      * the response.      */
DECL|method|minDocCount
specifier|public
name|SignificantTermsAggregationBuilder
name|minDocCount
parameter_list|(
name|long
name|minDocCount
parameter_list|)
block|{
if|if
condition|(
name|minDocCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[minDocCount] must be greater than or equal to 0. Found ["
operator|+
name|minDocCount
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|bucketCountThresholds
operator|.
name|setMinDocCount
argument_list|(
name|minDocCount
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Set the minimum document count terms should have on the shard in order to      * appear in the response.      */
DECL|method|shardMinDocCount
specifier|public
name|SignificantTermsAggregationBuilder
name|shardMinDocCount
parameter_list|(
name|long
name|shardMinDocCount
parameter_list|)
block|{
if|if
condition|(
name|shardMinDocCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[shardMinDocCount] must be greater than or equal to 0. Found ["
operator|+
name|shardMinDocCount
operator|+
literal|"] in ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|bucketCountThresholds
operator|.
name|setShardMinDocCount
argument_list|(
name|shardMinDocCount
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Expert: sets an execution hint to the aggregation.      */
DECL|method|executionHint
specifier|public
name|SignificantTermsAggregationBuilder
name|executionHint
parameter_list|(
name|String
name|executionHint
parameter_list|)
block|{
name|this
operator|.
name|executionHint
operator|=
name|executionHint
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Expert: gets an execution hint to the aggregation.      */
DECL|method|executionHint
specifier|public
name|String
name|executionHint
parameter_list|()
block|{
return|return
name|executionHint
return|;
block|}
DECL|method|backgroundFilter
specifier|public
name|SignificantTermsAggregationBuilder
name|backgroundFilter
parameter_list|(
name|QueryBuilder
name|backgroundFilter
parameter_list|)
block|{
if|if
condition|(
name|backgroundFilter
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[backgroundFilter] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|filterBuilder
operator|=
name|backgroundFilter
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|backgroundFilter
specifier|public
name|QueryBuilder
name|backgroundFilter
parameter_list|()
block|{
return|return
name|filterBuilder
return|;
block|}
comment|/**      * Set terms to include and exclude from the aggregation results      */
DECL|method|includeExclude
specifier|public
name|SignificantTermsAggregationBuilder
name|includeExclude
parameter_list|(
name|IncludeExclude
name|includeExclude
parameter_list|)
block|{
name|this
operator|.
name|includeExclude
operator|=
name|includeExclude
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Get terms to include and exclude from the aggregation results      */
DECL|method|includeExclude
specifier|public
name|IncludeExclude
name|includeExclude
parameter_list|()
block|{
return|return
name|includeExclude
return|;
block|}
DECL|method|significanceHeuristic
specifier|public
name|SignificantTermsAggregationBuilder
name|significanceHeuristic
parameter_list|(
name|SignificanceHeuristic
name|significanceHeuristic
parameter_list|)
block|{
if|if
condition|(
name|significanceHeuristic
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[significanceHeuristic] must not be null: ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|significanceHeuristic
operator|=
name|significanceHeuristic
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|significanceHeuristic
specifier|public
name|SignificanceHeuristic
name|significanceHeuristic
parameter_list|()
block|{
return|return
name|significanceHeuristic
return|;
block|}
annotation|@
name|Override
DECL|method|innerBuild
specifier|protected
name|ValuesSourceAggregatorFactory
argument_list|<
name|ValuesSource
argument_list|,
name|?
argument_list|>
name|innerBuild
parameter_list|(
name|AggregationContext
name|context
parameter_list|,
name|ValuesSourceConfig
argument_list|<
name|ValuesSource
argument_list|>
name|config
parameter_list|,
name|AggregatorFactory
argument_list|<
name|?
argument_list|>
name|parent
parameter_list|,
name|Builder
name|subFactoriesBuilder
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|significanceHeuristic
operator|.
name|canCache
argument_list|()
condition|)
block|{
name|context
operator|.
name|searchContext
argument_list|()
operator|.
name|markAsNotCachable
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SignificantTermsAggregatorFactory
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|config
argument_list|,
name|includeExclude
argument_list|,
name|executionHint
argument_list|,
name|filterBuilder
argument_list|,
name|bucketCountThresholds
argument_list|,
name|significanceHeuristic
argument_list|,
name|context
argument_list|,
name|parent
argument_list|,
name|subFactoriesBuilder
argument_list|,
name|metaData
argument_list|)
return|;
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
name|bucketCountThresholds
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|executionHint
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|TermsAggregationBuilder
operator|.
name|EXECUTION_HINT_FIELD_NAME
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|executionHint
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterBuilder
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|BACKGROUND_FILTER
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|filterBuilder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includeExclude
operator|!=
literal|null
condition|)
block|{
name|includeExclude
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|significanceHeuristic
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
name|bucketCountThresholds
argument_list|,
name|executionHint
argument_list|,
name|filterBuilder
argument_list|,
name|includeExclude
argument_list|,
name|significanceHeuristic
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
name|Object
name|obj
parameter_list|)
block|{
name|SignificantTermsAggregationBuilder
name|other
init|=
operator|(
name|SignificantTermsAggregationBuilder
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|bucketCountThresholds
argument_list|,
name|other
operator|.
name|bucketCountThresholds
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|executionHint
argument_list|,
name|other
operator|.
name|executionHint
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|filterBuilder
argument_list|,
name|other
operator|.
name|filterBuilder
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|includeExclude
argument_list|,
name|other
operator|.
name|includeExclude
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|significanceHeuristic
argument_list|,
name|other
operator|.
name|significanceHeuristic
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

