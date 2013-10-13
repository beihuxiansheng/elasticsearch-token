begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.matchedqueries
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|matchedqueries
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
name|SearchHit
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
name|AbstractIntegrationTest
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
name|*
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
name|*
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
name|hasItemInArray
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MatchedQueriesTests
specifier|public
class|class
name|MatchedQueriesTests
extends|extends
name|AbstractIntegrationTest
block|{
annotation|@
name|Test
DECL|method|simpleMatchedQueryFromFilteredQuery
specifier|public
name|void
name|simpleMatchedQueryFromFilteredQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
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
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"test1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"number"
argument_list|,
literal|1
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
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"test2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"number"
argument_list|,
literal|2
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
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
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
literal|"name"
argument_list|,
literal|"test3"
argument_list|)
operator|.
name|field
argument_list|(
literal|"number"
argument_list|,
literal|3
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
argument_list|()
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|orFilter
argument_list|(
name|rangeFilter
argument_list|(
literal|"number"
argument_list|)
operator|.
name|lte
argument_list|(
literal|2
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|rangeFilter
argument_list|(
literal|"number"
argument_list|)
operator|.
name|gt
argument_list|(
literal|2
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"test2"
argument_list|)
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
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected document returned with id "
operator|+
name|hit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|boolQuery
argument_list|()
operator|.
name|should
argument_list|(
name|rangeQuery
argument_list|(
literal|"number"
argument_list|)
operator|.
name|lte
argument_list|(
literal|2
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
operator|.
name|should
argument_list|(
name|rangeQuery
argument_list|(
literal|"number"
argument_list|)
operator|.
name|gt
argument_list|(
literal|2
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"test2"
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
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected document returned with id "
operator|+
name|hit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|simpleMatchedQueryFromTopLevelFilter
specifier|public
name|void
name|simpleMatchedQueryFromTopLevelFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
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
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"title"
argument_list|,
literal|"title1"
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
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"test"
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
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
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
literal|"name"
argument_list|,
literal|"test"
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
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|setFilter
argument_list|(
name|orFilter
argument_list|(
name|termFilter
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|termFilter
argument_list|(
literal|"title"
argument_list|,
literal|"title1"
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"title"
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
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected document returned with id "
operator|+
name|hit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|setFilter
argument_list|(
name|queryFilter
argument_list|(
name|boolQuery
argument_list|()
operator|.
name|should
argument_list|(
name|termQuery
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"name"
argument_list|)
argument_list|)
operator|.
name|should
argument_list|(
name|termQuery
argument_list|(
literal|"title"
argument_list|,
literal|"title1"
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"title"
argument_list|)
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
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected document returned with id "
operator|+
name|hit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|simpleMatchedQueryFromTopLevelFilterAndFilteredQuery
specifier|public
name|void
name|simpleMatchedQueryFromTopLevelFilterAndFilteredQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ensureGreen
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
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"title"
argument_list|,
literal|"title1"
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
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"title"
argument_list|,
literal|"title2"
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
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
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
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|field
argument_list|(
literal|"title"
argument_list|,
literal|"title3"
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
argument_list|()
operator|.
name|setQuery
argument_list|(
name|filteredQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|,
name|termsFilter
argument_list|(
literal|"title"
argument_list|,
literal|"title1"
argument_list|,
literal|"title2"
argument_list|,
literal|"title3"
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"title"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|setFilter
argument_list|(
name|termFilter
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|filterName
argument_list|(
literal|"name"
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
literal|3l
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected document returned with id "
operator|+
name|hit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|termsQuery
argument_list|(
literal|"title"
argument_list|,
literal|"title1"
argument_list|,
literal|"title2"
argument_list|,
literal|"title3"
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"title"
argument_list|)
argument_list|)
operator|.
name|setFilter
argument_list|(
name|queryFilter
argument_list|(
name|matchQuery
argument_list|(
literal|"name"
argument_list|,
literal|"test"
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"name"
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
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|searchResponse
operator|.
name|getHits
argument_list|()
control|)
block|{
if|if
condition|(
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"2"
argument_list|)
operator|||
name|hit
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
literal|"3"
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|hit
operator|.
name|matchedQueries
argument_list|()
argument_list|,
name|hasItemInArray
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected document returned with id "
operator|+
name|hit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

