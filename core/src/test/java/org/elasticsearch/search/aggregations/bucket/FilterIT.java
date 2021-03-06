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
name|BoolQueryBuilder
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
name|InternalAggregation
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
name|Filter
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
name|ESIntegTestCase
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
name|termQuery
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
name|filter
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
name|greaterThanOrEqualTo
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

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|SuiteScopeTestCase
DECL|class|FilterIT
specifier|public
class|class
name|FilterIT
extends|extends
name|ESIntegTestCase
block|{
DECL|field|numDocs
DECL|field|numTag1Docs
specifier|static
name|int
name|numDocs
decl_stmt|,
name|numTag1Docs
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
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|method|testSimple
specifier|public
name|void
name|testSimple
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
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termQuery
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
name|Filter
name|filter
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"tag1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
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
comment|// See NullPointer issue when filters are empty:
comment|// https://github.com/elastic/elasticsearch/issues/8438
DECL|method|testEmptyFilterDeclarations
specifier|public
name|void
name|testEmptyFilterDeclarations
parameter_list|()
throws|throws
name|Exception
block|{
name|QueryBuilder
name|emptyFilter
init|=
operator|new
name|BoolQueryBuilder
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
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|emptyFilter
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
name|Filter
name|filter
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
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
block|}
DECL|method|testWithSubAggregation
specifier|public
name|void
name|testWithSubAggregation
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
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termQuery
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
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
name|Filter
name|filter
init|=
name|response
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"tag1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"tag1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
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
name|assertThat
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
name|InternalAggregation
operator|)
name|filter
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"_count"
argument_list|)
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
name|filter
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
name|filter
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
call|(
name|double
call|)
argument_list|(
operator|(
name|InternalAggregation
operator|)
name|filter
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"avg_value.value"
argument_list|)
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
block|}
DECL|method|testAsSubAggregation
specifier|public
name|void
name|testAsSubAggregation
parameter_list|()
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
literal|2L
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|filter
argument_list|(
literal|"filter"
argument_list|,
name|matchAllQuery
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
name|response
argument_list|)
expr_stmt|;
name|Histogram
name|histo
init|=
name|response
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
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|histo
operator|.
name|getBuckets
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Histogram
operator|.
name|Bucket
name|bucket
range|:
name|histo
operator|.
name|getBuckets
argument_list|()
control|)
block|{
name|Filter
name|filter
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bucket
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|filter
operator|.
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWithContextBasedSubAggregation
specifier|public
name|void
name|testWithContextBasedSubAggregation
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
name|filter
argument_list|(
literal|"tag1"
argument_list|,
name|termQuery
argument_list|(
literal|"tag"
argument_list|,
literal|"tag1"
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
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"all shards failed"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyAggregation
specifier|public
name|void
name|testEmptyAggregation
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
literal|1L
argument_list|)
operator|.
name|minDocCount
argument_list|(
literal|0
argument_list|)
operator|.
name|subAggregation
argument_list|(
name|filter
argument_list|(
literal|"filter"
argument_list|,
name|matchAllQuery
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
literal|2L
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
name|getBuckets
argument_list|()
operator|.
name|get
argument_list|(
literal|1
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
name|Filter
name|filter
init|=
name|bucket
operator|.
name|getAggregations
argument_list|()
operator|.
name|get
argument_list|(
literal|"filter"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
argument_list|,
name|Matchers
operator|.
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"filter"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|filter
operator|.
name|getDocCount
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

