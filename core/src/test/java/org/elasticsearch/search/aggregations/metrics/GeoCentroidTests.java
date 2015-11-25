begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.metrics
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
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
name|metrics
operator|.
name|geocentroid
operator|.
name|GeoCentroidAggregator
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
name|metrics
operator|.
name|geocentroid
operator|.
name|GeoCentroidAggregator
operator|.
name|Factory
import|;
end_import

begin_class
DECL|class|GeoCentroidTests
specifier|public
class|class
name|GeoCentroidTests
extends|extends
name|BaseAggregationTestCase
argument_list|<
name|GeoCentroidAggregator
operator|.
name|Factory
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestAggregatorFactory
specifier|protected
name|Factory
name|createTestAggregatorFactory
parameter_list|()
block|{
name|Factory
name|factory
init|=
operator|new
name|Factory
argument_list|(
name|randomAsciiOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
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
literal|"0,0"
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

