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
name|action
operator|.
name|search
operator|.
name|SearchResponse
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
operator|.
name|SubAggCollectionMode
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
name|Terms
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
name|test
operator|.
name|ESIntegTestCase
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|AggregationBuilders
operator|.
name|terms
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertSearchResponse
import|;
end_import

begin_class
DECL|class|AbstractTermsTests
specifier|public
specifier|abstract
class|class
name|AbstractTermsTests
extends|extends
name|ESIntegTestCase
block|{
DECL|method|randomExecutionHint
specifier|public
name|String
name|randomExecutionHint
parameter_list|()
block|{
return|return
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
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
return|;
block|}
DECL|method|sumOfDocCounts
specifier|private
specifier|static
name|long
name|sumOfDocCounts
parameter_list|(
name|Terms
name|terms
parameter_list|)
block|{
name|long
name|sumOfDocCounts
init|=
name|terms
operator|.
name|getSumOfOtherDocCounts
argument_list|()
decl_stmt|;
for|for
control|(
name|Terms
operator|.
name|Bucket
name|b
range|:
name|terms
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|sumOfDocCounts
operator|+=
name|b
operator|.
name|getDocCount
argument_list|()
expr_stmt|;
block|}
return|return
name|sumOfDocCounts
return|;
block|}
DECL|method|testOtherDocCount
specifier|public
name|void
name|testOtherDocCount
parameter_list|(
name|String
modifier|...
name|fieldNames
parameter_list|)
block|{
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
name|SearchResponse
name|allTerms
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|executionHint
argument_list|(
name|randomExecutionHint
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|fieldName
argument_list|)
operator|.
name|size
argument_list|(
literal|0
argument_list|)
operator|.
name|collectMode
argument_list|(
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|allTerms
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|allTerms
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|terms
operator|.
name|getSumOfOtherDocCounts
argument_list|()
argument_list|)
expr_stmt|;
comment|// size is 0
specifier|final
name|long
name|sumOfDocCounts
init|=
name|sumOfDocCounts
argument_list|(
name|terms
argument_list|)
decl_stmt|;
specifier|final
name|int
name|totalNumTerms
init|=
name|terms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|size
init|=
literal|1
init|;
name|size
operator|<
name|totalNumTerms
operator|+
literal|2
condition|;
name|size
operator|+=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
control|)
block|{
for|for
control|(
name|int
name|shardSize
init|=
name|size
init|;
name|shardSize
operator|<=
name|totalNumTerms
operator|+
literal|2
condition|;
name|shardSize
operator|+=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
control|)
block|{
name|SearchResponse
name|resp
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|executionHint
argument_list|(
name|randomExecutionHint
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|fieldName
argument_list|)
operator|.
name|size
argument_list|(
name|size
argument_list|)
operator|.
name|shardSize
argument_list|(
name|shardSize
argument_list|)
operator|.
name|collectMode
argument_list|(
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|resp
argument_list|)
expr_stmt|;
name|terms
operator|=
name|resp
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|size
argument_list|,
name|totalNumTerms
argument_list|)
argument_list|,
name|terms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sumOfDocCounts
argument_list|,
name|sumOfDocCounts
argument_list|(
name|terms
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

