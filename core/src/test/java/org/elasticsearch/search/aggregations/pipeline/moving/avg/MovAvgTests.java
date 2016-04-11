begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.moving.avg
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
name|moving
operator|.
name|avg
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
name|XContentFactory
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|BasePipelineAggregationTestCase
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
name|PipelineAggregatorBuilder
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
name|movavg
operator|.
name|MovAvgPipelineAggregatorBuilder
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
name|EwmaModel
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
name|HoltLinearModel
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
name|HoltWintersModel
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
name|HoltWintersModel
operator|.
name|SeasonalityType
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
name|LinearModel
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
name|SimpleModel
import|;
end_import

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_class
DECL|class|MovAvgTests
specifier|public
class|class
name|MovAvgTests
extends|extends
name|BasePipelineAggregationTestCase
argument_list|<
name|MovAvgPipelineAggregatorBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestAggregatorFactory
specifier|protected
name|MovAvgPipelineAggregatorBuilder
name|createTestAggregatorFactory
parameter_list|()
block|{
name|String
name|name
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|String
name|bucketsPath
init|=
name|randomAsciiOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|MovAvgPipelineAggregatorBuilder
name|factory
init|=
operator|new
name|MovAvgPipelineAggregatorBuilder
argument_list|(
name|name
argument_list|,
name|bucketsPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|format
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|gapPolicy
argument_list|(
name|randomFrom
argument_list|(
name|GapPolicy
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|randomInt
argument_list|(
literal|4
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|SimpleModel
operator|.
name|SimpleModelBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|LinearModel
operator|.
name|LinearModelBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|EwmaModel
operator|.
name|EWMAModelBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|EwmaModel
operator|.
name|EWMAModelBuilder
argument_list|()
operator|.
name|alpha
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|3
case|:
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|HoltLinearModel
operator|.
name|HoltLinearModelBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|HoltLinearModel
operator|.
name|HoltLinearModelBuilder
argument_list|()
operator|.
name|alpha
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
operator|.
name|beta
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|4
case|:
default|default:
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|HoltWintersModel
operator|.
name|HoltWintersModelBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|int
name|period
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|factory
operator|.
name|modelBuilder
argument_list|(
operator|new
name|HoltWintersModel
operator|.
name|HoltWintersModelBuilder
argument_list|()
operator|.
name|alpha
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
operator|.
name|beta
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
operator|.
name|gamma
argument_list|(
name|randomDouble
argument_list|()
argument_list|)
operator|.
name|period
argument_list|(
name|period
argument_list|)
operator|.
name|seasonalityType
argument_list|(
name|randomFrom
argument_list|(
name|SeasonalityType
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
operator|.
name|pad
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|factory
operator|.
name|window
argument_list|(
name|randomIntBetween
argument_list|(
literal|2
operator|*
name|period
argument_list|,
literal|200
operator|*
name|period
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
name|factory
operator|.
name|predict
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|50
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|factory
operator|.
name|model
argument_list|()
operator|.
name|canBeMinimized
argument_list|()
operator|&&
name|randomBoolean
argument_list|()
condition|)
block|{
name|factory
operator|.
name|minimize
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
DECL|method|testDefaultParsing
specifier|public
name|void
name|testDefaultParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|MovAvgPipelineAggregatorBuilder
name|expected
init|=
operator|new
name|MovAvgPipelineAggregatorBuilder
argument_list|(
literal|"commits_moving_avg"
argument_list|,
literal|"commits"
argument_list|)
decl_stmt|;
name|String
name|json
init|=
literal|"{"
operator|+
literal|"    \"commits_moving_avg\": {"
operator|+
literal|"        \"moving_avg\": {"
operator|+
literal|"            \"buckets_path\": \"commits\""
operator|+
literal|"        }"
operator|+
literal|"    }"
operator|+
literal|"}"
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|json
argument_list|)
operator|.
name|createParser
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|QueryParseContext
name|parseContext
init|=
operator|new
name|QueryParseContext
argument_list|(
name|queriesRegistry
argument_list|)
decl_stmt|;
name|parseContext
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|(
name|parseFieldMatcher
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|name
argument_list|()
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|type
argument_list|()
argument_list|,
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|PipelineAggregatorBuilder
argument_list|<
name|?
argument_list|>
name|newAgg
init|=
name|aggParsers
operator|.
name|pipelineParser
argument_list|(
name|expected
operator|.
name|getWriteableName
argument_list|()
argument_list|,
name|parser
argument_list|)
operator|.
name|parse
argument_list|(
name|expected
operator|.
name|name
argument_list|()
argument_list|,
name|parser
argument_list|,
name|parseContext
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
argument_list|,
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|newAgg
argument_list|)
expr_stmt|;
name|assertNotSame
argument_list|(
name|newAgg
argument_list|,
name|expected
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|newAgg
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
operator|.
name|hashCode
argument_list|()
argument_list|,
name|newAgg
operator|.
name|hashCode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

