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
name|ElasticsearchException
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|AndFilterBuilder
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
name|FilterBuilder
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
name|filters
operator|.
name|Filters
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
name|histogram
operator|.
name|Histogram
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
name|avg
operator|.
name|Avg
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
name|ElasticsearchIntegrationTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|index
operator|.
name|query
operator|.
name|FilterBuilders
operator|.
name|matchAllFilter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|FilterBuilders
operator|.
name|termFilter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
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
name|avg
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
name|filters
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
name|histogram
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
name|Matchers
operator|.
name|is
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
name|ElasticsearchIntegrationTest
operator|.
name|SuiteScopeTest
DECL|class|FiltersTests
specifier|public
class|class
name|FiltersTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|numDocs
DECL|field|numTag1Docs
DECL|field|numTag2Docs
specifier|static
name|int
name|numDocs
decl_stmt|,
name|numTag1Docs
decl_stmt|,
name|numTag2Docs
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
literal|"idx2"
argument_list|)
expr_stmt|;
name|numDocs
operator|=
name|randomIntBetween
argument_list|(
literal|5
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|numTag1Docs
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|numDocs
operator|-
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|IndexRequestBuilder
argument_list|>
name|builders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numTag1Docs
condition|;
name|i
operator|++
control|)
block|{
name|XContentBuilder
name|source
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
name|i
operator|+
literal|1
argument_list|)
operator|.
name|field
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// randomly index the document twice so that we have deleted docs that match the filter
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
name|numTag1Docs
init|;
name|i
operator|<
name|numDocs
condition|;
name|i
operator|++
control|)
block|{
name|numTag2Docs
operator|++
expr_stmt|;
name|XContentBuilder
name|source
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
name|i
argument_list|)
operator|.
name|field
argument_list|(
literal|"tag"
argument_list|,
literal|"tag2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"name"
operator|+
name|i
argument_list|)
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|prepareCreate
argument_list|(
literal|"empty_bucket_idx"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"value"
argument_list|,
literal|"type=integer"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
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
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|builders
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"empty_bucket_idx"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|i
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
literal|"value"
argument_list|,
name|i
operator|*
literal|2
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
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
name|ensureSearchable
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simple
specifier|public
name|void
name|simple
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
name|addAggregation
argument_list|(
name|filters
argument_list|(
literal|"tags"
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag2"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag2"
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
name|Filters
name|filters
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filters
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filters
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"tags"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filters
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Filters
operator|.
name|Bucket
name|bucket
init|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"tag2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag2Docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// See NullPointer issue when filters are empty:
comment|// https://github.com/elasticsearch/elasticsearch/issues/8438
annotation|@
name|Test
DECL|method|emptyFilterDeclarations
specifier|public
name|void
name|emptyFilterDeclarations
parameter_list|()
throws|throws
name|Exception
block|{
name|FilterBuilder
name|emptyFilter
init|=
operator|new
name|AndFilterBuilder
argument_list|()
decl_stmt|;
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
name|addAggregation
argument_list|(
name|filters
argument_list|(
literal|"tags"
argument_list|)
operator|.
name|filter
argument_list|(
literal|"all"
argument_list|,
name|emptyFilter
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
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
name|Filters
name|filters
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filters
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Filters
operator|.
name|Bucket
name|allBucket
init|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"all"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|allBucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numDocs
argument_list|)
argument_list|)
expr_stmt|;
name|Filters
operator|.
name|Bucket
name|bucket
init|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withSubAggregation
specifier|public
name|void
name|withSubAggregation
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
name|addAggregation
argument_list|(
name|filters
argument_list|(
literal|"tags"
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag2"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag2"
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|avg
argument_list|(
literal|"avg_value"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
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
name|Filters
name|filters
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filters
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filters
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"tags"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filters
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Object
index|[]
name|propertiesKeys
init|=
operator|(
name|Object
index|[]
operator|)
name|filters
operator|.
name|getProperty
argument_list|(
literal|"_key"
argument_list|)
decl_stmt|;
name|Object
index|[]
name|propertiesDocCounts
init|=
operator|(
name|Object
index|[]
operator|)
name|filters
operator|.
name|getProperty
argument_list|(
literal|"_count"
argument_list|)
decl_stmt|;
name|Object
index|[]
name|propertiesCounts
init|=
operator|(
name|Object
index|[]
operator|)
name|filters
operator|.
name|getProperty
argument_list|(
literal|"avg_value.value"
argument_list|)
decl_stmt|;
name|Filters
operator|.
name|Bucket
name|bucket
init|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|sum
init|=
literal|0
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
name|numTag1Docs
condition|;
operator|++
name|i
control|)
block|{
name|sum
operator|+=
name|i
operator|+
literal|1
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|asList
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|Avg
name|avgValue
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"avg_value"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|avgValue
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|avgValue
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"avg_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|avgValue
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|double
operator|)
name|sum
operator|/
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|propertiesKeys
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"tag1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|propertiesDocCounts
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|propertiesCounts
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|double
operator|)
name|sum
operator|/
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"tag2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag2Docs
argument_list|)
argument_list|)
expr_stmt|;
name|sum
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numTag1Docs
init|;
name|i
operator|<
name|numDocs
condition|;
operator|++
name|i
control|)
block|{
name|sum
operator|+=
name|i
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|asList
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|avgValue
operator|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"avg_value"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|avgValue
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|avgValue
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"avg_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|avgValue
operator|.
name|getValue
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|double
operator|)
name|sum
operator|/
name|numTag2Docs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|propertiesKeys
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"tag2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|long
operator|)
name|propertiesDocCounts
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag2Docs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|propertiesCounts
index|[
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
operator|(
name|double
operator|)
name|sum
operator|/
name|numTag2Docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|withContextBasedSubAggregation
specifier|public
name|void
name|withContextBasedSubAggregation
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
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
name|filters
argument_list|(
literal|"tags"
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
literal|"tag2"
argument_list|,
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag2"
argument_list|)
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|avg
argument_list|(
literal|"avg_value"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected execution to fail - an attempt to have a context based numeric sub-aggregation, but there is not value source"
operator|+
literal|"context which the sub-aggregation can inherit"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchException
name|ese
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
DECL|method|emptyAggregation
specifier|public
name|void
name|emptyAggregation
parameter_list|()
throws|throws
name|Exception
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"empty_bucket_idx"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|histogram
argument_list|(
literal|"histo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|)
operator|.
name|interval
argument_list|(
literal|1l
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|filters
argument_list|(
literal|"filters"
argument_list|)
operator|.
name|filter
argument_list|(
literal|"all"
argument_list|,
name|matchAllFilter
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
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|Histogram
name|histo
init|=
name|searchResponse
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"histo"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|histo
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Histogram
operator|.
name|Bucket
name|bucket
init|=
name|histo
operator|.
name|getBucketByKey
argument_list|(
literal|1l
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Filters
name|filters
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"filters"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filters
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Filters
operator|.
name|Bucket
name|all
init|=
name|filters
operator|.
name|getBucketByKey
argument_list|(
literal|"all"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|all
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|all
operator|.
name|getKey
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"all"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|all
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simple_nonKeyed
specifier|public
name|void
name|simple_nonKeyed
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
name|addAggregation
argument_list|(
name|filters
argument_list|(
literal|"tags"
argument_list|)
operator|.
name|filter
argument_list|(
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|termFilter
argument_list|(
literal|"tag"
argument_list|,
literal|"tag2"
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
name|Filters
name|filters
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tags"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filters
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filters
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"tags"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filters
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|?
extends|extends
name|Filters
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
name|filters
operator|.
name|getBuckets
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|?
extends|extends
name|Filters
operator|.
name|Bucket
argument_list|>
name|itr
init|=
name|buckets
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Filters
operator|.
name|Bucket
name|bucket
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag1Docs
argument_list|)
argument_list|)
expr_stmt|;
name|bucket
operator|=
name|itr
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|numTag2Docs
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

