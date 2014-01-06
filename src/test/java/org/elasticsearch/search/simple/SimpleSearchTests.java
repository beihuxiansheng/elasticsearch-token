begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.simple
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|settings
operator|.
name|ImmutableSettings
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
name|XContentFactory
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
name|test
operator|.
name|ElasticsearchIntegrationTest
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|concurrent
operator|.
name|ExecutionException
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
name|boolQuery
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
name|rangeQuery
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
name|assertHitCount
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

begin_class
DECL|class|SimpleSearchTests
specifier|public
class|class
name|SimpleSearchTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testSearchNullIndex
specifier|public
name|void
name|testSearchNullIndex
parameter_list|()
block|{
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
operator|(
name|String
operator|)
literal|null
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"_id"
argument_list|,
literal|"XXX1"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalArgumentException
name|e
parameter_list|)
block|{          }
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"_id"
argument_list|,
literal|"XXX1"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalArgumentException
name|e
parameter_list|)
block|{          }
block|}
annotation|@
name|Test
DECL|method|testSearchRandomPreference
specifier|public
name|void
name|testSearchRandomPreference
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
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
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
name|between
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"6"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
comment|// id is not indexed, but lets see that we automatically convert to
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|setPreference
argument_list|(
name|randomUnicodeOfLengthBetween
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|6l
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|simpleIpTests
specifier|public
name|void
name|simpleIpTests
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
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|preparePutMapping
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"from"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"ip"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"to"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"ip"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
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
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"from"
argument_list|,
literal|"192.168.0.5"
argument_list|,
literal|"to"
argument_list|,
literal|"192.168.0.10"
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|SearchResponse
name|search
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|must
argument_list|(
name|rangeQuery
argument_list|(
literal|"from"
argument_list|)
operator|.
name|lt
argument_list|(
literal|"192.168.0.7"
argument_list|)
argument_list|)
operator|.
name|must
argument_list|(
name|rangeQuery
argument_list|(
literal|"to"
argument_list|)
operator|.
name|gt
argument_list|(
literal|"192.168.0.7"
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
name|assertHitCount
argument_list|(
name|search
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simpleIdTests
specifier|public
name|void
name|simpleIdTests
parameter_list|()
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
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"XXX1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|// id is not indexed, but lets see that we automatically convert to
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|termQuery
argument_list|(
literal|"_id"
argument_list|,
literal|"XXX1"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryString
argument_list|(
literal|"_id:XXX1"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
comment|// id is not index, but we can automatically support prefix as well
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|prefixQuery
argument_list|(
literal|"_id"
argument_list|,
literal|"XXX"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryString
argument_list|(
literal|"_id:XXX*"
argument_list|)
operator|.
name|lowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simpleDateRangeWithUpperInclusiveEnabledTests
specifier|public
name|void
name|simpleDateRangeWithUpperInclusiveEnabledTests
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"2010-01-05T02:00"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"2010-01-06T02:00"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
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
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|// test include upper on ranges to include the full day on the upper bound
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"2010-01-05"
argument_list|)
operator|.
name|lte
argument_list|(
literal|"2010-01-06"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"2010-01-05"
argument_list|)
operator|.
name|lt
argument_list|(
literal|"2010-01-06"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simpleDateRangeWithUpperInclusiveDisabledTests
specifier|public
name|void
name|simpleDateRangeWithUpperInclusiveDisabledTests
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.date.round_ceil"
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"2010-01-05T02:00"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"2010-01-06T02:00"
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
name|refresh
argument_list|()
expr_stmt|;
comment|// test include upper on ranges to include the full day on the upper bound (disabled here though...)
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"2010-01-05"
argument_list|)
operator|.
name|lte
argument_list|(
literal|"2010-01-06"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|searchResponse
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"2010-01-05"
argument_list|)
operator|.
name|lt
argument_list|(
literal|"2010-01-06"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestLogging
argument_list|(
literal|"action.search.type:TRACE,action.admin.indices.refresh:TRACE"
argument_list|)
DECL|method|simpleDateMathTests
specifier|public
name|void
name|simpleDateMathTests
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"2010-01-05T02:00"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"2010-01-06T02:00"
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
name|refresh
argument_list|()
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"2010-01-03||+2d"
argument_list|)
operator|.
name|lte
argument_list|(
literal|"2010-01-04||+2d"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|searchResponse
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryString
argument_list|(
literal|"field:[2010-01-03||+2d TO 2010-01-04||+2d]"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|localDependentDateTests
specifier|public
name|void
name|localDependentDateTests
parameter_list|()
throws|throws
name|Exception
block|{
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"E, d MMM yyyy HH:mm:ss Z"
argument_list|)
operator|.
name|field
argument_list|(
literal|"locale"
argument_list|,
literal|"de"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
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
name|ensureGreen
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|""
operator|+
name|i
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"date_field"
argument_list|,
literal|"Mi, 06 Dez 2000 02:55:00 -0800"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|""
operator|+
operator|(
literal|10
operator|+
name|i
operator|)
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"date_field"
argument_list|,
literal|"Do, 07 Dez 2000 02:55:00 -0800"
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
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"Di, 05 Dez 2000 02:55:00 -0800"
argument_list|)
operator|.
name|lte
argument_list|(
literal|"Do, 07 Dez 2000 00:00:00 -0800"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|10l
argument_list|)
expr_stmt|;
name|searchResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|rangeQuery
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|gte
argument_list|(
literal|"Di, 05 Dez 2000 02:55:00 -0800"
argument_list|)
operator|.
name|lte
argument_list|(
literal|"Fr, 08 Dez 2000 00:00:00 -0800"
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|20l
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

