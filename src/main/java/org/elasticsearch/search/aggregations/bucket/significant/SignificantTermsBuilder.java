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
name|FilterBuilder
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
name|AggregationBuilder
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
name|SignificanceHeuristicBuilder
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
name|AbstractTermsParametersParser
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Creates an aggregation that finds interesting or unusual occurrences of terms in a result set.  *<p/>  * This feature is marked as experimental, and may be subject to change in the future.  If you  * use this feature, please let us know your experience with it!  */
end_comment

begin_class
DECL|class|SignificantTermsBuilder
specifier|public
class|class
name|SignificantTermsBuilder
extends|extends
name|AggregationBuilder
argument_list|<
name|SignificantTermsBuilder
argument_list|>
block|{
DECL|field|bucketCountThresholds
specifier|private
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
init|=
operator|new
name|TermsAggregator
operator|.
name|BucketCountThresholds
argument_list|(
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|executionHint
specifier|private
name|String
name|executionHint
decl_stmt|;
DECL|field|includePattern
specifier|private
name|String
name|includePattern
decl_stmt|;
DECL|field|includeFlags
specifier|private
name|int
name|includeFlags
decl_stmt|;
DECL|field|excludePattern
specifier|private
name|String
name|excludePattern
decl_stmt|;
DECL|field|excludeFlags
specifier|private
name|int
name|excludeFlags
decl_stmt|;
DECL|field|filterBuilder
specifier|private
name|FilterBuilder
name|filterBuilder
decl_stmt|;
DECL|field|significanceHeuristicBuilder
specifier|private
name|SignificanceHeuristicBuilder
name|significanceHeuristicBuilder
decl_stmt|;
DECL|method|SignificantTermsBuilder
specifier|public
name|SignificantTermsBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|SignificantStringTerms
operator|.
name|TYPE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|field
specifier|public
name|SignificantTermsBuilder
name|field
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|size
specifier|public
name|SignificantTermsBuilder
name|size
parameter_list|(
name|int
name|requiredSize
parameter_list|)
block|{
name|bucketCountThresholds
operator|.
name|setRequiredSize
argument_list|(
name|requiredSize
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|shardSize
specifier|public
name|SignificantTermsBuilder
name|shardSize
parameter_list|(
name|int
name|shardSize
parameter_list|)
block|{
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
DECL|method|minDocCount
specifier|public
name|SignificantTermsBuilder
name|minDocCount
parameter_list|(
name|int
name|minDocCount
parameter_list|)
block|{
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
DECL|method|backgroundFilter
specifier|public
name|SignificantTermsBuilder
name|backgroundFilter
parameter_list|(
name|FilterBuilder
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filterBuilder
operator|=
name|filter
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|shardMinDocCount
specifier|public
name|SignificantTermsBuilder
name|shardMinDocCount
parameter_list|(
name|int
name|shardMinDocCount
parameter_list|)
block|{
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
DECL|method|executionHint
specifier|public
name|SignificantTermsBuilder
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
comment|/**      * Define a regular expression that will determine what terms should be aggregated. The regular expression is based      * on the {@link java.util.regex.Pattern} class.      *      * @see #include(String, int)      */
DECL|method|include
specifier|public
name|SignificantTermsBuilder
name|include
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
name|include
argument_list|(
name|regex
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Define a regular expression that will determine what terms should be aggregated. The regular expression is based      * on the {@link java.util.regex.Pattern} class.      *      * @see java.util.regex.Pattern#compile(String, int)      */
DECL|method|include
specifier|public
name|SignificantTermsBuilder
name|include
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|includePattern
operator|=
name|regex
expr_stmt|;
name|this
operator|.
name|includeFlags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Define a regular expression that will filter out terms that should be excluded from the aggregation. The regular      * expression is based on the {@link java.util.regex.Pattern} class.      *      * @see #exclude(String, int)      */
DECL|method|exclude
specifier|public
name|SignificantTermsBuilder
name|exclude
parameter_list|(
name|String
name|regex
parameter_list|)
block|{
return|return
name|exclude
argument_list|(
name|regex
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Define a regular expression that will filter out terms that should be excluded from the aggregation. The regular      * expression is based on the {@link java.util.regex.Pattern} class.      *      * @see java.util.regex.Pattern#compile(String, int)      */
DECL|method|exclude
specifier|public
name|SignificantTermsBuilder
name|exclude
parameter_list|(
name|String
name|regex
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|excludePattern
operator|=
name|regex
expr_stmt|;
name|this
operator|.
name|excludeFlags
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
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
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
name|field
argument_list|)
expr_stmt|;
block|}
name|bucketCountThresholds
operator|.
name|toXContent
argument_list|(
name|builder
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
name|AbstractTermsParametersParser
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
name|includePattern
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|includeFlags
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"include"
argument_list|,
name|includePattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"include"
argument_list|)
operator|.
name|field
argument_list|(
literal|"pattern"
argument_list|,
name|includePattern
argument_list|)
operator|.
name|field
argument_list|(
literal|"flags"
argument_list|,
name|includeFlags
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|excludePattern
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|excludeFlags
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"exclude"
argument_list|,
name|excludePattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"exclude"
argument_list|)
operator|.
name|field
argument_list|(
literal|"pattern"
argument_list|,
name|excludePattern
argument_list|)
operator|.
name|field
argument_list|(
literal|"flags"
argument_list|,
name|excludeFlags
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
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
name|SignificantTermsParametersParser
operator|.
name|BACKGROUND_FILTER
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|filterBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|significanceHeuristicBuilder
operator|!=
literal|null
condition|)
block|{
name|significanceHeuristicBuilder
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|method|significanceHeuristic
specifier|public
name|SignificantTermsBuilder
name|significanceHeuristic
parameter_list|(
name|SignificanceHeuristicBuilder
name|significanceHeuristicBuilder
parameter_list|)
block|{
name|this
operator|.
name|significanceHeuristicBuilder
operator|=
name|significanceHeuristicBuilder
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

