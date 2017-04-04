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
name|filter
operator|.
name|FilterAggregationBuilder
import|;
end_import

begin_class
DECL|class|FilterTests
specifier|public
class|class
name|FilterTests
extends|extends
name|BaseAggregationTestCase
argument_list|<
name|FilterAggregationBuilder
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createTestAggregatorBuilder
specifier|protected
name|FilterAggregationBuilder
name|createTestAggregatorBuilder
parameter_list|()
block|{
name|FilterAggregationBuilder
name|factory
init|=
operator|new
name|FilterAggregationBuilder
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|20
argument_list|)
argument_list|,
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|,
name|randomAlphaOfLengthBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// NORELEASE make RandomQueryBuilder work outside of the
comment|// AbstractQueryTestCase
comment|// builder.query(RandomQueryBuilder.createQuery(getRandom()));
return|return
name|factory
return|;
block|}
block|}
end_class

end_unit

