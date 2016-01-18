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
name|inject
operator|.
name|Inject
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
name|XContentParser
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
name|bucket
operator|.
name|BucketUtils
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
name|significant
operator|.
name|heuristics
operator|.
name|SignificanceHeuristicParserMapper
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
name|ValuesSourceParser
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
name|internal
operator|.
name|SearchContext
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
comment|/**  *  */
end_comment

begin_class
DECL|class|SignificantTermsParser
specifier|public
class|class
name|SignificantTermsParser
implements|implements
name|Aggregator
operator|.
name|Parser
block|{
DECL|field|significanceHeuristicParserMapper
specifier|private
specifier|final
name|SignificanceHeuristicParserMapper
name|significanceHeuristicParserMapper
decl_stmt|;
annotation|@
name|Inject
DECL|method|SignificantTermsParser
specifier|public
name|SignificantTermsParser
parameter_list|(
name|SignificanceHeuristicParserMapper
name|significanceHeuristicParserMapper
parameter_list|)
block|{
name|this
operator|.
name|significanceHeuristicParserMapper
operator|=
name|significanceHeuristicParserMapper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|SignificantStringTerms
operator|.
name|TYPE
operator|.
name|name
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|AggregatorFactory
name|parse
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|SignificantTermsParametersParser
name|aggParser
init|=
operator|new
name|SignificantTermsParametersParser
argument_list|(
name|significanceHeuristicParserMapper
argument_list|)
decl_stmt|;
name|ValuesSourceParser
argument_list|<
name|ValuesSource
argument_list|>
name|vsParser
init|=
name|ValuesSourceParser
operator|.
name|any
argument_list|(
name|aggregationName
argument_list|,
name|SignificantStringTerms
operator|.
name|TYPE
argument_list|,
name|context
argument_list|)
operator|.
name|scriptable
argument_list|(
literal|false
argument_list|)
operator|.
name|formattable
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IncludeExclude
operator|.
name|Parser
name|incExcParser
init|=
operator|new
name|IncludeExclude
operator|.
name|Parser
argument_list|()
decl_stmt|;
name|aggParser
operator|.
name|parse
argument_list|(
name|aggregationName
argument_list|,
name|parser
argument_list|,
name|context
argument_list|,
name|vsParser
argument_list|,
name|incExcParser
argument_list|)
expr_stmt|;
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|bucketCountThresholds
init|=
name|aggParser
operator|.
name|getBucketCountThresholds
argument_list|()
decl_stmt|;
if|if
condition|(
name|bucketCountThresholds
operator|.
name|getShardSize
argument_list|()
operator|==
name|aggParser
operator|.
name|getDefaultBucketCountThresholds
argument_list|()
operator|.
name|getShardSize
argument_list|()
condition|)
block|{
comment|//The user has not made a shardSize selection .
comment|//Use default heuristic to avoid any wrong-ranking caused by distributed counting
comment|//but request double the usual amount.
comment|//We typically need more than the number of "top" terms requested by other aggregations
comment|//as the significance algorithm is in less of a position to down-select at shard-level -
comment|//some of the things we want to find have only one occurrence on each shard and as
comment|// such are impossible to differentiate from non-significant terms at that early stage.
name|bucketCountThresholds
operator|.
name|setShardSize
argument_list|(
literal|2
operator|*
name|BucketUtils
operator|.
name|suggestShardSideQueueSize
argument_list|(
name|bucketCountThresholds
operator|.
name|getRequiredSize
argument_list|()
argument_list|,
name|context
operator|.
name|numberOfShards
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bucketCountThresholds
operator|.
name|ensureValidity
argument_list|()
expr_stmt|;
name|SignificanceHeuristic
name|significanceHeuristic
init|=
name|aggParser
operator|.
name|getSignificanceHeuristic
argument_list|()
decl_stmt|;
if|if
condition|(
name|significanceHeuristic
operator|==
literal|null
condition|)
block|{
name|significanceHeuristic
operator|=
name|JLHScore
operator|.
name|INSTANCE
expr_stmt|;
block|}
return|return
operator|new
name|SignificantTermsAggregatorFactory
argument_list|(
name|aggregationName
argument_list|,
name|vsParser
operator|.
name|config
argument_list|()
argument_list|,
name|bucketCountThresholds
argument_list|,
name|aggParser
operator|.
name|getIncludeExclude
argument_list|()
argument_list|,
name|aggParser
operator|.
name|getExecutionHint
argument_list|()
argument_list|,
name|aggParser
operator|.
name|getFilter
argument_list|()
argument_list|,
name|significanceHeuristic
argument_list|)
return|;
block|}
block|}
end_class

end_unit

