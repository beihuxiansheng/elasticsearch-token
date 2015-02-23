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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Filter
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
name|SearchParseException
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
name|SignificanceHeuristicParser
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

begin_class
DECL|class|SignificantTermsParametersParser
specifier|public
class|class
name|SignificantTermsParametersParser
extends|extends
name|AbstractTermsParametersParser
block|{
DECL|field|DEFAULT_BUCKET_COUNT_THRESHOLDS
specifier|private
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
DECL|field|significanceHeuristicParserMapper
specifier|private
specifier|final
name|SignificanceHeuristicParserMapper
name|significanceHeuristicParserMapper
decl_stmt|;
DECL|method|SignificantTermsParametersParser
specifier|public
name|SignificantTermsParametersParser
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
DECL|method|getFilter
specifier|public
name|Filter
name|getFilter
parameter_list|()
block|{
return|return
name|filter
return|;
block|}
DECL|field|filter
specifier|private
name|Filter
name|filter
init|=
literal|null
decl_stmt|;
DECL|field|significanceHeuristic
specifier|private
name|SignificanceHeuristic
name|significanceHeuristic
decl_stmt|;
annotation|@
name|Override
DECL|method|getDefaultBucketCountThresholds
specifier|public
name|TermsAggregator
operator|.
name|BucketCountThresholds
name|getDefaultBucketCountThresholds
parameter_list|()
block|{
return|return
operator|new
name|TermsAggregator
operator|.
name|BucketCountThresholds
argument_list|(
name|DEFAULT_BUCKET_COUNT_THRESHOLDS
argument_list|)
return|;
block|}
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
annotation|@
name|Override
DECL|method|parseSpecial
specifier|public
name|void
name|parseSpecial
parameter_list|(
name|String
name|aggregationName
parameter_list|,
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|,
name|XContentParser
operator|.
name|Token
name|token
parameter_list|,
name|String
name|currentFieldName
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|SignificanceHeuristicParser
name|significanceHeuristicParser
init|=
name|significanceHeuristicParserMapper
operator|.
name|get
argument_list|(
name|currentFieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|significanceHeuristicParser
operator|!=
literal|null
condition|)
block|{
name|significanceHeuristic
operator|=
name|significanceHeuristicParser
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|BACKGROUND_FILTER
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filter
operator|=
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseInnerFilter
argument_list|(
name|parser
argument_list|)
operator|.
name|filter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Unknown key for a "
operator|+
name|token
operator|+
literal|" in ["
operator|+
name|aggregationName
operator|+
literal|"]: ["
operator|+
name|currentFieldName
operator|+
literal|"]."
argument_list|)
throw|;
block|}
block|}
DECL|method|getSignificanceHeuristic
specifier|public
name|SignificanceHeuristic
name|getSignificanceHeuristic
parameter_list|()
block|{
return|return
name|significanceHeuristic
return|;
block|}
block|}
end_class

end_unit

