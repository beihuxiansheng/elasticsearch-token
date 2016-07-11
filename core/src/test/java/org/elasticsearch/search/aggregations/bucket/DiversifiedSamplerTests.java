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
name|sampler
operator|.
name|DiversifiedAggregationBuilder
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
name|sampler
operator|.
name|SamplerAggregator
operator|.
name|ExecutionMode
import|;
end_import

begin_class
DECL|class|DiversifiedSamplerTests
specifier|public
class|class
name|DiversifiedSamplerTests
extends|extends
name|BaseAggregationTestCase
argument_list|<
name|DiversifiedAggregationBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestAggregatorBuilder
specifier|protected
specifier|final
name|DiversifiedAggregationBuilder
name|createTestAggregatorBuilder
parameter_list|()
block|{
name|DiversifiedAggregationBuilder
name|factory
init|=
operator|new
name|DiversifiedAggregationBuilder
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|String
name|field
init|=
name|randomNumericField
argument_list|()
decl_stmt|;
name|int
name|randomFieldBranch
init|=
name|randomInt
argument_list|(
literal|3
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
name|maxDocsPerValue
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1000
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
name|shardSize
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1000
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
name|executionHint
argument_list|(
name|randomFrom
argument_list|(
name|ExecutionMode
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
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

