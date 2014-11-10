begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|query
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
name|admin
operator|.
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequestBuilder
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
name|SimpleQueryStringBuilder
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
name|SimpleQueryStringFlag
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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|queryString
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
name|simpleQueryString
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
name|assertSearchHits
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

begin_comment
comment|/**  * Tests for the {@code simple_query_string} query  */
end_comment

begin_class
DECL|class|SimpleQueryStringTests
specifier|public
class|class
name|SimpleQueryStringTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testSimpleQueryString
specifier|public
name|void
name|testSimpleQueryString
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
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
literal|"body"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
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
literal|"body"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
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
literal|"body"
argument_list|,
literal|"foo bar"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"body"
argument_list|,
literal|"quux baz eggplant"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"body"
argument_list|,
literal|"quux baz spaghetti"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"6"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"otherbody"
argument_list|,
literal|"spaghetti"
argument_list|)
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"foo bar"
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
literal|3l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
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
name|simpleQueryString
argument_list|(
literal|"foo bar"
argument_list|)
operator|.
name|defaultOperator
argument_list|(
name|SimpleQueryStringBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|searchResponse
argument_list|,
name|hasId
argument_list|(
literal|"3"
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"\"quux baz\" +(eggplant | spaghetti)"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"4"
argument_list|,
literal|"5"
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
name|simpleQueryString
argument_list|(
literal|"eggplants"
argument_list|)
operator|.
name|analyzer
argument_list|(
literal|"snowball"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|searchResponse
argument_list|,
name|hasId
argument_list|(
literal|"4"
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"spaghetti"
argument_list|)
operator|.
name|field
argument_list|(
literal|"body"
argument_list|,
literal|1000.0f
argument_list|)
operator|.
name|field
argument_list|(
literal|"otherbody"
argument_list|,
literal|2.0f
argument_list|)
operator|.
name|queryName
argument_list|(
literal|"myquery"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|searchResponse
argument_list|,
name|hasId
argument_list|(
literal|"5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"5"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|searchResponse
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getMatchedQueries
argument_list|()
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"myquery"
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"spaghetti"
argument_list|)
operator|.
name|field
argument_list|(
literal|"*body"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"5"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
comment|// Have to bypass the builder here because the builder always uses "fields" instead of "field"
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
literal|"{\"simple_query_string\": {\"query\": \"spaghetti\", \"field\": \"_all\"}}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"5"
argument_list|,
literal|"6"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleQueryStringLowercasing
specifier|public
name|void
name|testSimpleQueryStringLowercasing
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
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
literal|"body"
argument_list|,
literal|"Professional"
argument_list|)
operator|.
name|get
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
name|simpleQueryString
argument_list|(
literal|"Professio*"
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
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|simpleQueryString
argument_list|(
literal|"Professio*"
argument_list|)
operator|.
name|lowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|0l
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
name|simpleQueryString
argument_list|(
literal|"Professionan~1"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|simpleQueryString
argument_list|(
literal|"Professionan~1"
argument_list|)
operator|.
name|lowercaseExpandedTerms
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testQueryStringLocale
specifier|public
name|void
name|testQueryStringLocale
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
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
literal|"body"
argument_list|,
literal|"bÄ±lly"
argument_list|)
operator|.
name|get
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
name|simpleQueryString
argument_list|(
literal|"BILL*"
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
literal|0l
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
name|queryString
argument_list|(
literal|"body:BILL*"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|0l
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
name|simpleQueryString
argument_list|(
literal|"BILL*"
argument_list|)
operator|.
name|locale
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"tr"
argument_list|,
literal|"TR"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|queryString
argument_list|(
literal|"body:BILL*"
argument_list|)
operator|.
name|locale
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"tr"
argument_list|,
literal|"TR"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNestedFieldSimpleQueryString
specifier|public
name|void
name|testNestedFieldSimpleQueryString
parameter_list|()
throws|throws
name|IOException
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
literal|"body"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"sub"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
comment|// sub
operator|.
name|endObject
argument_list|()
comment|// fields
operator|.
name|endObject
argument_list|()
comment|// body
operator|.
name|endObject
argument_list|()
comment|// properties
operator|.
name|endObject
argument_list|()
comment|// type1
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
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
literal|"body"
argument_list|,
literal|"foo bar baz"
argument_list|)
operator|.
name|get
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
name|simpleQueryString
argument_list|(
literal|"foo bar baz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"body"
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
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|simpleQueryString
argument_list|(
literal|"foo bar baz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type1.body"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|simpleQueryString
argument_list|(
literal|"foo bar baz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"body.sub"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|simpleQueryString
argument_list|(
literal|"foo bar baz"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type1.body.sub"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleQueryStringFlags
specifier|public
name|void
name|testSimpleQueryStringFlags
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
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
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"body"
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
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
literal|"body"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
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
literal|"body"
argument_list|,
literal|"foo bar"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"4"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"body"
argument_list|,
literal|"quux baz eggplant"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"5"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"body"
argument_list|,
literal|"quux baz spaghetti"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"6"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"otherbody"
argument_list|,
literal|"spaghetti"
argument_list|)
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"foo bar"
argument_list|)
operator|.
name|flags
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|ALL
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
literal|3l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
argument_list|)
expr_stmt|;
comment|// Sending a negative 'flags' value is the same as SimpleQueryStringFlag.ALL
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
literal|"{\"simple_query_string\": {\"query\": \"foo bar\", \"flags\": -1}}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
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
name|simpleQueryString
argument_list|(
literal|"foo | bar"
argument_list|)
operator|.
name|defaultOperator
argument_list|(
name|SimpleQueryStringBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
operator|.
name|flags
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|OR
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|,
literal|"2"
argument_list|,
literal|"3"
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
name|simpleQueryString
argument_list|(
literal|"foo | bar"
argument_list|)
operator|.
name|defaultOperator
argument_list|(
name|SimpleQueryStringBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
operator|.
name|flags
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|NONE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|searchResponse
argument_list|,
name|hasId
argument_list|(
literal|"3"
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"baz | egg*"
argument_list|)
operator|.
name|defaultOperator
argument_list|(
name|SimpleQueryStringBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
operator|.
name|flags
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|NONE
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|0l
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
name|setSource
argument_list|(
literal|"{\n"
operator|+
literal|"  \"query\": {\n"
operator|+
literal|"    \"simple_query_string\": {\n"
operator|+
literal|"      \"query\": \"foo|bar\",\n"
operator|+
literal|"      \"default_operator\": \"AND\","
operator|+
literal|"      \"flags\": \"NONE\"\n"
operator|+
literal|"    }\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|)
operator|.
name|get
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
name|simpleQueryString
argument_list|(
literal|"baz | egg*"
argument_list|)
operator|.
name|defaultOperator
argument_list|(
name|SimpleQueryStringBuilder
operator|.
name|Operator
operator|.
name|AND
argument_list|)
operator|.
name|flags
argument_list|(
name|SimpleQueryStringFlag
operator|.
name|WHITESPACE
argument_list|,
name|SimpleQueryStringFlag
operator|.
name|PREFIX
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|searchResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|assertFirstHit
argument_list|(
name|searchResponse
argument_list|,
name|hasId
argument_list|(
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleQueryStringLenient
specifier|public
name|void
name|testSimpleQueryStringLenient
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
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
literal|"test1"
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
literal|"foo"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test2"
argument_list|,
literal|"type1"
argument_list|,
literal|"10"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|5
argument_list|)
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertFailures
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
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
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
name|simpleQueryString
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field"
argument_list|)
operator|.
name|lenient
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
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
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// see: https://github.com/elasticsearch/elasticsearch/issues/7967
DECL|method|testLenientFlagBeingTooLenient
specifier|public
name|void
name|testLenientFlagBeingTooLenient
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|"doc"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"num"
argument_list|,
literal|1
argument_list|,
literal|"body"
argument_list|,
literal|"foo bar baz"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"doc"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"num"
argument_list|,
literal|2
argument_list|,
literal|"body"
argument_list|,
literal|"eggplant spaghetti lasagna"
argument_list|)
argument_list|)
expr_stmt|;
name|BoolQueryBuilder
name|q
init|=
name|boolQuery
argument_list|()
operator|.
name|should
argument_list|(
name|simpleQueryString
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|field
argument_list|(
literal|"num"
argument_list|)
operator|.
name|field
argument_list|(
literal|"body"
argument_list|)
operator|.
name|lenient
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|SearchResponse
name|resp
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
name|q
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|resp
argument_list|)
expr_stmt|;
comment|// the bug is that this would be parsed into basically a match_all
comment|// query and this would match both documents
name|assertHitCount
argument_list|(
name|resp
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertSearchHits
argument_list|(
name|resp
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleQueryStringAnalyzeWildcard
specifier|public
name|void
name|testSimpleQueryStringAnalyzeWildcard
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|String
name|mapping
init|=
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
literal|"location"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
literal|"german"
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
operator|.
name|string
argument_list|()
decl_stmt|;
name|CreateIndexRequestBuilder
name|mappingRequest
init|=
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
literal|"test1"
argument_list|)
operator|.
name|addMapping
argument_list|(
literal|"type1"
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
name|mappingRequest
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
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
literal|"test1"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"location"
argument_list|,
literal|"KÃ¶ln"
argument_list|)
argument_list|)
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
name|simpleQueryString
argument_list|(
literal|"KÃ¶ln*"
argument_list|)
operator|.
name|analyzeWildcard
argument_list|(
literal|true
argument_list|)
operator|.
name|field
argument_list|(
literal|"location"
argument_list|)
argument_list|)
operator|.
name|get
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
name|assertSearchHits
argument_list|(
name|searchResponse
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

