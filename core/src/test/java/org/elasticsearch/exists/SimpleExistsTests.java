begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.exists
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|exists
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
name|exists
operator|.
name|ExistsResponse
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
name|assertExists
import|;
end_import

begin_class
DECL|class|SimpleExistsTests
specifier|public
class|class
name|SimpleExistsTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testExistsRandomPreference
specifier|public
name|void
name|testExistsRandomPreference
parameter_list|()
throws|throws
name|Exception
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
name|scaledRandomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
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
name|String
name|randomPreference
init|=
name|randomUnicodeOfLengthBetween
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
decl_stmt|;
comment|// randomPreference should not start with '_' (reserved for known preference types (e.g. _shards, _primary)
while|while
condition|(
name|randomPreference
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|randomPreference
operator|=
name|randomUnicodeOfLengthBetween
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
block|}
comment|// id is not indexed, but lets see that we automatically convert to
name|ExistsResponse
name|existsResponse
init|=
name|client
argument_list|()
operator|.
name|prepareExists
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
name|randomPreference
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|true
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
name|createIndex
argument_list|(
literal|"test"
argument_list|)
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
name|ExistsResponse
name|existsResponse
init|=
name|client
argument_list|()
operator|.
name|prepareExists
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
name|get
argument_list|()
decl_stmt|;
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|existsResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareExists
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
literal|"192.168.0.4"
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
literal|"192.168.0.11"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|false
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
name|ExistsResponse
name|existsResponse
init|=
name|client
argument_list|()
operator|.
name|prepareExists
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
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|existsResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareExists
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryStringQuery
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
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|existsResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareExists
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
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|existsResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareExists
argument_list|()
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryStringQuery
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
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|simpleNonExistenceTests
specifier|public
name|void
name|simpleNonExistenceTests
parameter_list|()
throws|throws
name|Exception
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
literal|"field"
argument_list|,
literal|2
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
literal|5
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
literal|"str_field"
argument_list|,
literal|"value"
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
name|ExistsResponse
name|existsResponse
init|=
name|client
argument_list|()
operator|.
name|prepareExists
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
literal|6
argument_list|)
operator|.
name|lte
argument_list|(
literal|8
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|existsResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareExists
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setQuery
argument_list|(
name|QueryBuilders
operator|.
name|queryStringQuery
argument_list|(
literal|"_id:XXY*"
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
name|assertExists
argument_list|(
name|existsResponse
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

