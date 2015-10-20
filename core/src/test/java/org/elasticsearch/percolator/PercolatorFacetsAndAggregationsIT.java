begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.percolator
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
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
name|percolate
operator|.
name|PercolateRequestBuilder
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
name|percolate
operator|.
name|PercolateResponse
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
name|settings
operator|.
name|Settings
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
name|QueryBuilder
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
name|Aggregation
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
name|AggregationBuilders
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
name|Aggregations
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
name|Terms
operator|.
name|Order
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
name|PipelineAggregatorBuilders
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
name|bucketmetrics
operator|.
name|InternalBucketMetricValue
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
operator|.
name|PercolateSourceBuilder
operator|.
name|docBuilder
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
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchQuery
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
name|assertAcked
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
name|assertMatchCount
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
name|assertNoFailures
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
name|arrayWithSize
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
name|notNullValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|PercolatorFacetsAndAggregationsIT
specifier|public
class|class
name|PercolatorFacetsAndAggregationsIT
extends|extends
name|ESIntegTestCase
block|{
comment|// Just test the integration with facets and aggregations, not the facet and aggregation functionality!
DECL|method|testFacetsAndAggregations
specifier|public
name|void
name|testFacetsAndAggregations
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"field1"
argument_list|,
literal|"type=string"
argument_list|,
literal|"field2"
argument_list|,
literal|"type=string"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|int
name|numQueries
init|=
name|scaledRandomIntBetween
argument_list|(
literal|250
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|int
name|numUniqueQueries
init|=
name|between
argument_list|(
literal|1
argument_list|,
name|numQueries
operator|/
literal|2
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|numUniqueQueries
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
literal|"value"
operator|+
name|i
expr_stmt|;
block|}
name|int
index|[]
name|expectedCount
init|=
operator|new
name|int
index|[
name|numUniqueQueries
index|]
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> registering {} queries"
argument_list|,
name|numQueries
argument_list|)
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
name|values
index|[
name|i
operator|%
name|numUniqueQueries
index|]
decl_stmt|;
name|expectedCount
index|[
name|i
operator|%
name|numUniqueQueries
index|]
operator|++
expr_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
literal|"query"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
literal|"test"
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
name|values
index|[
name|i
operator|%
name|numUniqueQueries
index|]
decl_stmt|;
name|PercolateRequestBuilder
name|percolateRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setPercolateDoc
argument_list|(
name|docBuilder
argument_list|()
operator|.
name|setDoc
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SubAggCollectionMode
name|aggCollectionMode
init|=
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|percolateRequestBuilder
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|terms
argument_list|(
literal|"a"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|collectMode
argument_list|(
name|aggCollectionMode
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setPercolateQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|percolateRequestBuilder
operator|.
name|setSortByScore
argument_list|(
literal|true
argument_list|)
operator|.
name|setSize
argument_list|(
name|numQueries
argument_list|)
expr_stmt|;
block|}
name|boolean
name|countOnly
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|countOnly
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setOnlyCount
argument_list|(
name|countOnly
argument_list|)
expr_stmt|;
block|}
name|PercolateResponse
name|response
init|=
name|percolateRequestBuilder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertMatchCount
argument_list|(
name|response
argument_list|,
name|expectedCount
index|[
name|i
operator|%
name|numUniqueQueries
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|countOnly
condition|)
block|{
name|assertThat
argument_list|(
name|response
operator|.
name|getMatches
argument_list|()
argument_list|,
name|arrayWithSize
argument_list|(
name|expectedCount
index|[
name|i
operator|%
name|numUniqueQueries
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Aggregation
argument_list|>
name|aggregations
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aggregations
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|aggregations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
operator|(
operator|(
name|Terms
operator|)
name|aggregations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getBuckets
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKeyAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|expectedCount
index|[
name|i
operator|%
name|values
operator|.
name|length
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Just test the integration with facets and aggregations, not the facet and aggregation functionality!
DECL|method|testAggregationsAndPipelineAggregations
specifier|public
name|void
name|testAggregationsAndPipelineAggregations
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"field1"
argument_list|,
literal|"type=string"
argument_list|,
literal|"field2"
argument_list|,
literal|"type=string"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|int
name|numQueries
init|=
name|scaledRandomIntBetween
argument_list|(
literal|250
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|int
name|numUniqueQueries
init|=
name|between
argument_list|(
literal|1
argument_list|,
name|numQueries
operator|/
literal|2
argument_list|)
decl_stmt|;
name|String
index|[]
name|values
init|=
operator|new
name|String
index|[
name|numUniqueQueries
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
literal|"value"
operator|+
name|i
expr_stmt|;
block|}
name|int
index|[]
name|expectedCount
init|=
operator|new
name|int
index|[
name|numUniqueQueries
index|]
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> registering {} queries"
argument_list|,
name|numQueries
argument_list|)
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
name|values
index|[
name|i
operator|%
name|numUniqueQueries
index|]
decl_stmt|;
name|expectedCount
index|[
name|i
operator|%
name|numUniqueQueries
index|]
operator|++
expr_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
literal|"query"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"b"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
literal|"test"
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
name|values
index|[
name|i
operator|%
name|numUniqueQueries
index|]
decl_stmt|;
name|PercolateRequestBuilder
name|percolateRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setPercolateDoc
argument_list|(
name|docBuilder
argument_list|()
operator|.
name|setDoc
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SubAggCollectionMode
name|aggCollectionMode
init|=
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|percolateRequestBuilder
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|terms
argument_list|(
literal|"a"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|collectMode
argument_list|(
name|aggCollectionMode
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setPercolateQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|percolateRequestBuilder
operator|.
name|setSortByScore
argument_list|(
literal|true
argument_list|)
operator|.
name|setSize
argument_list|(
name|numQueries
argument_list|)
expr_stmt|;
block|}
name|boolean
name|countOnly
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|countOnly
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setOnlyCount
argument_list|(
name|countOnly
argument_list|)
expr_stmt|;
block|}
name|percolateRequestBuilder
operator|.
name|addAggregation
argument_list|(
name|PipelineAggregatorBuilders
operator|.
name|maxBucket
argument_list|(
literal|"max_a"
argument_list|)
operator|.
name|setBucketsPaths
argument_list|(
literal|"a>_count"
argument_list|)
argument_list|)
expr_stmt|;
name|PercolateResponse
name|response
init|=
name|percolateRequestBuilder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertMatchCount
argument_list|(
name|response
argument_list|,
name|expectedCount
index|[
name|i
operator|%
name|numUniqueQueries
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|countOnly
condition|)
block|{
name|assertThat
argument_list|(
name|response
operator|.
name|getMatches
argument_list|()
argument_list|,
name|arrayWithSize
argument_list|(
name|expectedCount
index|[
name|i
operator|%
name|numUniqueQueries
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Aggregations
name|aggregations
init|=
name|response
operator|.
name|getAggregations
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aggregations
operator|.
name|asList
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
name|Terms
name|terms
init|=
name|aggregations
operator|.
name|get
argument_list|(
literal|"a"
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
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
operator|.
name|getBuckets
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKeyAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|long
operator|)
name|expectedCount
index|[
name|i
operator|%
name|values
operator|.
name|length
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|InternalBucketMetricValue
name|maxA
init|=
name|aggregations
operator|.
name|get
argument_list|(
literal|"max_a"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|maxA
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|maxA
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"max_a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|maxA
operator|.
name|value
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|double
operator|)
name|expectedCount
index|[
name|i
operator|%
name|values
operator|.
name|length
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|maxA
operator|.
name|keys
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"b"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSignificantAggs
specifier|public
name|void
name|testSignificantAggs
parameter_list|()
throws|throws
name|Exception
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|PercolateRequestBuilder
name|percolateRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setPercolateDoc
argument_list|(
name|docBuilder
argument_list|()
operator|.
name|setDoc
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|significantTerms
argument_list|(
literal|"a"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|)
argument_list|)
decl_stmt|;
name|PercolateResponse
name|response
init|=
name|percolateRequestBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleShardAggregations
specifier|public
name|void
name|testSingleShardAggregations
parameter_list|()
throws|throws
name|Exception
block|{
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"SETTING_NUMBER_OF_SHARDS"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type"
argument_list|,
literal|"field1"
argument_list|,
literal|"type=string"
argument_list|,
literal|"field2"
argument_list|,
literal|"type=string"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|int
name|numQueries
init|=
name|scaledRandomIntBetween
argument_list|(
literal|250
argument_list|,
literal|500
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> registering {} queries"
argument_list|,
name|numQueries
argument_list|)
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
literal|"value0"
decl_stmt|;
name|QueryBuilder
name|queryBuilder
init|=
name|matchQuery
argument_list|(
literal|"field1"
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|PercolatorService
operator|.
name|TYPE_NAME
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
literal|"query"
argument_list|,
name|queryBuilder
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
name|i
operator|%
literal|3
operator|==
literal|0
condition|?
literal|"b"
else|:
literal|"a"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|(
literal|"test"
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
name|numQueries
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
literal|"value0"
decl_stmt|;
name|PercolateRequestBuilder
name|percolateRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type"
argument_list|)
operator|.
name|setPercolateDoc
argument_list|(
name|docBuilder
argument_list|()
operator|.
name|setDoc
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SubAggCollectionMode
name|aggCollectionMode
init|=
name|randomFrom
argument_list|(
name|SubAggCollectionMode
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
name|percolateRequestBuilder
operator|.
name|addAggregation
argument_list|(
name|AggregationBuilders
operator|.
name|terms
argument_list|(
literal|"terms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|collectMode
argument_list|(
name|aggCollectionMode
argument_list|)
operator|.
name|order
argument_list|(
name|Order
operator|.
name|term
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|shardSize
argument_list|(
literal|2
argument_list|)
operator|.
name|size
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setPercolateQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setScore
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|percolateRequestBuilder
operator|.
name|setSortByScore
argument_list|(
literal|true
argument_list|)
operator|.
name|setSize
argument_list|(
name|numQueries
argument_list|)
expr_stmt|;
block|}
name|boolean
name|countOnly
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|countOnly
condition|)
block|{
name|percolateRequestBuilder
operator|.
name|setOnlyCount
argument_list|(
name|countOnly
argument_list|)
expr_stmt|;
block|}
name|percolateRequestBuilder
operator|.
name|addAggregation
argument_list|(
name|PipelineAggregatorBuilders
operator|.
name|maxBucket
argument_list|(
literal|"max_terms"
argument_list|)
operator|.
name|setBucketsPaths
argument_list|(
literal|"terms>_count"
argument_list|)
argument_list|)
expr_stmt|;
name|PercolateResponse
name|response
init|=
name|percolateRequestBuilder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertMatchCount
argument_list|(
name|response
argument_list|,
name|numQueries
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|countOnly
condition|)
block|{
name|assertThat
argument_list|(
name|response
operator|.
name|getMatches
argument_list|()
argument_list|,
name|arrayWithSize
argument_list|(
name|numQueries
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Aggregations
name|aggregations
init|=
name|response
operator|.
name|getAggregations
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|aggregations
operator|.
name|asList
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
name|Terms
name|terms
init|=
name|aggregations
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
name|List
argument_list|<
name|Terms
operator|.
name|Bucket
argument_list|>
name|buckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|terms
operator|.
name|getBuckets
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|buckets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKeyAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|InternalBucketMetricValue
name|maxA
init|=
name|aggregations
operator|.
name|get
argument_list|(
literal|"max_terms"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|maxA
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|maxA
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"max_terms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|maxA
operator|.
name|keys
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"a"
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

