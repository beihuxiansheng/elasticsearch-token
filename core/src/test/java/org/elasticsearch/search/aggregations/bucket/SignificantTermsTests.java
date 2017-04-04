begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket
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
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|RegExp
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
name|QueryBuilders
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
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
name|BaseAggregationTestCase
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
name|SignificantTermsAggregationBuilder
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
name|ChiSquare
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
name|GND
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
name|MutualInformation
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
name|PercentageScore
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
name|ScriptHeuristic
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
name|TermsAggregatorFactory
operator|.
name|ExecutionMode
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
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_class
DECL|class|SignificantTermsTests
specifier|public
class|class
name|SignificantTermsTests
extends|extends
name|BaseAggregationTestCase
argument_list|<
name|SignificantTermsAggregationBuilder
argument_list|>
block|{
DECL|field|executionHints
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|executionHints
decl_stmt|;
static|static
block|{
name|ExecutionMode
index|[]
name|executionModes
init|=
name|ExecutionMode
operator|.
name|values
argument_list|()
decl_stmt|;
name|executionHints
operator|=
operator|new
name|String
index|[
name|executionModes
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|executionModes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|executionHints
index|[
name|i
index|]
operator|=
name|executionModes
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|createTestAggregatorBuilder
specifier|protected
name|SignificantTermsAggregationBuilder
name|createTestAggregatorBuilder
parameter_list|()
block|{
name|String
name|name
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|SignificantTermsAggregationBuilder
name|factory
init|=
operator|new
name|SignificantTermsAggregationBuilder
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|randomAlphaOfLengthBetween
argument_list|(
literal|3
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|int
name|randomFieldBranch
init|=
name|randomInt
argument_list|(
literal|2
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|randomFieldBranch
condition|)
block|{
case|case
literal|0
case|:
name|factory
operator|.
name|field
argument_list|(
name|field
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|factory
operator|.
name|field
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|factory
operator|.
name|script
argument_list|(
operator|new
name|Script
argument_list|(
literal|"_value + 1"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|factory
operator|.
name|script
argument_list|(
operator|new
name|Script
argument_list|(
literal|"doc["
operator|+
name|field
operator|+
literal|"] + 1"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|()
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
name|missing
argument_list|(
literal|"MISSING"
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
name|bucketCountThresholds
argument_list|()
operator|.
name|setRequiredSize
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
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
name|bucketCountThresholds
argument_list|()
operator|.
name|setShardSize
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
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
name|int
name|minDocCount
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|minDocCount
condition|)
block|{
case|case
literal|0
case|:
break|break;
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
name|minDocCount
operator|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
break|break;
block|}
name|factory
operator|.
name|bucketCountThresholds
argument_list|()
operator|.
name|setMinDocCount
argument_list|(
name|minDocCount
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|int
name|shardMinDocCount
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|shardMinDocCount
condition|)
block|{
case|case
literal|0
case|:
break|break;
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
name|shardMinDocCount
operator|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|()
expr_stmt|;
block|}
name|factory
operator|.
name|bucketCountThresholds
argument_list|()
operator|.
name|setShardMinDocCount
argument_list|(
name|shardMinDocCount
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
name|executionHint
argument_list|(
name|randomFrom
argument_list|(
name|executionHints
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
name|format
argument_list|(
literal|"###.##"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|IncludeExclude
name|incExc
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|randomInt
argument_list|(
literal|5
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|incExc
operator|=
operator|new
name|IncludeExclude
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|incExc
operator|=
operator|new
name|IncludeExclude
argument_list|(
literal|null
argument_list|,
operator|new
name|RegExp
argument_list|(
literal|"foobaz"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|incExc
operator|=
operator|new
name|IncludeExclude
argument_list|(
operator|new
name|RegExp
argument_list|(
literal|"foobar"
argument_list|)
argument_list|,
operator|new
name|RegExp
argument_list|(
literal|"foobaz"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|includeValues
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numIncs
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
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
name|numIncs
condition|;
name|i
operator|++
control|)
block|{
name|includeValues
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|excludeValues
init|=
literal|null
decl_stmt|;
name|incExc
operator|=
operator|new
name|IncludeExclude
argument_list|(
name|includeValues
argument_list|,
name|excludeValues
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|includeValues2
init|=
literal|null
decl_stmt|;
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|excludeValues2
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numExcs2
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
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
name|numExcs2
condition|;
name|i
operator|++
control|)
block|{
name|excludeValues2
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|incExc
operator|=
operator|new
name|IncludeExclude
argument_list|(
name|includeValues2
argument_list|,
name|excludeValues2
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|includeValues3
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numIncs3
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
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
name|numIncs3
condition|;
name|i
operator|++
control|)
block|{
name|includeValues3
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SortedSet
argument_list|<
name|BytesRef
argument_list|>
name|excludeValues3
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numExcs3
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
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
name|numExcs3
condition|;
name|i
operator|++
control|)
block|{
name|excludeValues3
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|30
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|incExc
operator|=
operator|new
name|IncludeExclude
argument_list|(
name|includeValues3
argument_list|,
name|excludeValues3
argument_list|)
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|()
expr_stmt|;
block|}
name|factory
operator|.
name|includeExclude
argument_list|(
name|incExc
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|SignificanceHeuristic
name|significanceHeuristic
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|randomInt
argument_list|(
literal|5
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|significanceHeuristic
operator|=
operator|new
name|PercentageScore
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|significanceHeuristic
operator|=
operator|new
name|ChiSquare
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|significanceHeuristic
operator|=
operator|new
name|GND
argument_list|(
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|significanceHeuristic
operator|=
operator|new
name|MutualInformation
argument_list|(
name|randomBoolean
argument_list|()
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|significanceHeuristic
operator|=
operator|new
name|ScriptHeuristic
argument_list|(
operator|new
name|Script
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
name|significanceHeuristic
operator|=
operator|new
name|JLHScore
argument_list|()
expr_stmt|;
break|break;
default|default:
name|fail
argument_list|()
expr_stmt|;
block|}
name|factory
operator|.
name|significanceHeuristic
argument_list|(
name|significanceHeuristic
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
name|backgroundFilter
argument_list|(
name|QueryBuilders
operator|.
name|termsQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

