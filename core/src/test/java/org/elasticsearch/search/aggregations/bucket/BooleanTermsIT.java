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
name|index
operator|.
name|IndexRequestBuilder
import|;
end_import

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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsNull
operator|.
name|notNullValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|BooleanTermsIT
specifier|public
class|class
name|BooleanTermsIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|SINGLE_VALUED_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|SINGLE_VALUED_FIELD_NAME
init|=
literal|"b_value"
decl_stmt|;
DECL|field|MULTI_VALUED_FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|MULTI_VALUED_FIELD_NAME
init|=
literal|"b_values"
decl_stmt|;
DECL|field|numSingleTrues
DECL|field|numSingleFalses
DECL|field|numMultiTrues
DECL|field|numMultiFalses
specifier|static
name|int
name|numSingleTrues
decl_stmt|,
name|numSingleFalses
decl_stmt|,
name|numMultiTrues
decl_stmt|,
name|numMultiFalses
decl_stmt|;
annotation|@
name|Override
DECL|method|setupSuiteScopeCluster
specifier|public
name|void
name|setupSuiteScopeCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"idx"
argument_list|)
expr_stmt|;
name|createIndex
argument_list|(
literal|"idx_unmapped"
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|randomInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|IndexRequestBuilder
index|[]
name|builders
init|=
operator|new
name|IndexRequestBuilder
index|[
name|numDocs
index|]
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
name|builders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|boolean
name|singleValue
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|singleValue
condition|)
block|{
name|numSingleTrues
operator|++
expr_stmt|;
block|}
else|else
block|{
name|numSingleFalses
operator|++
expr_stmt|;
block|}
specifier|final
name|boolean
index|[]
name|multiValue
decl_stmt|;
switch|switch
condition|(
name|randomInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|multiValue
operator|=
operator|new
name|boolean
index|[
literal|0
index|]
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|numMultiFalses
operator|++
expr_stmt|;
name|multiValue
operator|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|}
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|numMultiTrues
operator|++
expr_stmt|;
name|multiValue
operator|=
operator|new
name|boolean
index|[]
block|{
literal|true
block|}
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|numMultiFalses
operator|++
expr_stmt|;
name|numMultiTrues
operator|++
expr_stmt|;
name|multiValue
operator|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|}
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|builders
index|[
name|i
index|]
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|,
name|singleValue
argument_list|)
operator|.
name|field
argument_list|(
name|MULTI_VALUED_FIELD_NAME
argument_list|,
name|multiValue
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleValueField
specifier|public
name|void
name|testSingleValueField
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
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
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|terms
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bucketCount
init|=
name|numSingleFalses
operator|>
literal|0
operator|&&
name|numSingleTrues
operator|>
literal|0
condition|?
literal|2
else|:
name|numSingleFalses
operator|+
name|numSingleTrues
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|bucketCount
argument_list|)
argument_list|)
expr_stmt|;
name|Terms
operator|.
name|Bucket
name|bucket
init|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"false"
argument_list|)
decl_stmt|;
if|if
condition|(
name|numSingleFalses
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numSingleFalses
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bucket
operator|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|numSingleTrues
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numSingleTrues
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testMultiValueField
specifier|public
name|void
name|testMultiValueField
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
name|MULTI_VALUED_FIELD_NAME
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
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|terms
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|int
name|bucketCount
init|=
name|numMultiFalses
operator|>
literal|0
operator|&&
name|numMultiTrues
operator|>
literal|0
condition|?
literal|2
else|:
name|numMultiFalses
operator|+
name|numMultiTrues
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
decl_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|bucketCount
argument_list|)
argument_list|)
expr_stmt|;
name|Terms
operator|.
name|Bucket
name|bucket
init|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"false"
argument_list|)
decl_stmt|;
if|if
condition|(
name|numMultiFalses
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numMultiFalses
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|bucket
operator|=
name|terms
operator|.
name|getBucketByKey
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|numMultiTrues
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|bucket
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numMultiTrues
argument_list|,
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|bucket
operator|.
name|getKeyAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testUnmapped
specifier|public
name|void
name|testUnmapped
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"idx_unmapped"
argument_list|)
operator|.
name|setTypes
argument_list|(
literal|"type"
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
name|SINGLE_VALUED_FIELD_NAME
argument_list|)
operator|.
name|size
argument_list|(
name|randomInt
argument_list|(
literal|5
argument_list|)
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
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertSearchResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|Terms
name|terms
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"terms"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|terms
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

