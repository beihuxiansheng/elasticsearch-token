begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline
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
name|script
operator|.
name|ScriptType
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
name|bucketscript
operator|.
name|BucketScriptPipelineAggregationBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
DECL|class|BucketScriptTests
specifier|public
class|class
name|BucketScriptTests
extends|extends
name|BasePipelineAggregationTestCase
argument_list|<
name|BucketScriptPipelineAggregationBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestAggregatorFactory
specifier|protected
name|BucketScriptPipelineAggregationBuilder
name|createTestAggregatorFactory
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bucketsPaths
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|numBucketPaths
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
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
name|numBucketPaths
condition|;
name|i
operator|++
control|)
block|{
name|bucketsPaths
operator|.
name|put
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|,
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|40
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Script
name|script
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|script
operator|=
name|mockScript
argument_list|(
literal|"script"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|params
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|ScriptType
name|type
init|=
name|randomFrom
argument_list|(
name|ScriptType
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|script
operator|=
operator|new
name|Script
argument_list|(
name|type
argument_list|,
name|type
operator|==
name|ScriptType
operator|.
name|STORED
condition|?
literal|null
else|:
name|randomFrom
argument_list|(
literal|"my_lang"
argument_list|,
name|Script
operator|.
name|DEFAULT_SCRIPT_LANG
argument_list|)
argument_list|,
literal|"script"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|BucketScriptPipelineAggregationBuilder
name|factory
init|=
operator|new
name|BucketScriptPipelineAggregationBuilder
argument_list|(
name|name
argument_list|,
name|bucketsPaths
argument_list|,
name|script
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
name|randomAlphaOfLengthBetween
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
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

