begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.search.indicesboost
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|search
operator|.
name|indicesboost
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
name|action
operator|.
name|search
operator|.
name|SearchType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|test
operator|.
name|integration
operator|.
name|AbstractSharedClusterTest
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
name|client
operator|.
name|Requests
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
name|builder
operator|.
name|SearchSourceBuilder
operator|.
name|searchSource
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
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
comment|/**  *  */
end_comment

begin_class
DECL|class|SimpleIndicesBoostSearchTests
specifier|public
class|class
name|SimpleIndicesBoostSearchTests
extends|extends
name|AbstractSharedClusterTest
block|{
DECL|field|DEFAULT_SETTINGS
specifier|private
specifier|static
specifier|final
name|Settings
name|DEFAULT_SETTINGS
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testIndicesBoost
specifier|public
name|void
name|testIndicesBoost
parameter_list|()
throws|throws
name|Exception
block|{
comment|// execute a search before we create an index
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
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
operator|:
literal|"should fail"
assert|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore, no indices
block|}
try|try
block|{
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
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
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
operator|:
literal|"should fail"
assert|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore, no indices
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
name|create
argument_list|(
name|createIndexRequest
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|settings
argument_list|(
name|DEFAULT_SETTINGS
argument_list|)
argument_list|)
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
name|create
argument_list|(
name|createIndexRequest
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|settings
argument_list|(
name|DEFAULT_SETTINGS
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|index
argument_list|(
name|indexRequest
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value check"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|index
argument_list|(
name|indexRequest
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value beck"
argument_list|)
operator|.
name|endObject
argument_list|()
argument_list|)
argument_list|)
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
name|refresh
argument_list|(
name|refreshRequest
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|float
name|indexBoost
init|=
literal|1.1f
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--- QUERY_THEN_FETCH"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Query with test1 boosted"
argument_list|)
expr_stmt|;
name|SearchResponse
name|response
init|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|indexBoost
argument_list|(
literal|"test1"
argument_list|,
name|indexBoost
argument_list|)
operator|.
name|query
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[0] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Query with test2 boosted"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|indexBoost
argument_list|(
literal|"test2"
argument_list|,
name|indexBoost
argument_list|)
operator|.
name|query
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[0] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--- DFS_QUERY_THEN_FETCH"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Query with test1 boosted"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|indexBoost
argument_list|(
literal|"test1"
argument_list|,
name|indexBoost
argument_list|)
operator|.
name|query
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[0] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Query with test2 boosted"
argument_list|)
expr_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|search
argument_list|(
name|searchRequest
argument_list|()
operator|.
name|searchType
argument_list|(
name|SearchType
operator|.
name|DFS_QUERY_THEN_FETCH
argument_list|)
operator|.
name|source
argument_list|(
name|searchSource
argument_list|()
operator|.
name|explain
argument_list|(
literal|true
argument_list|)
operator|.
name|indexBoost
argument_list|(
literal|"test2"
argument_list|,
name|indexBoost
argument_list|)
operator|.
name|query
argument_list|(
name|termQuery
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|totalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[0] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Hit[1] {} Explanation {}"
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|explanation
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|0
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getHits
argument_list|()
operator|.
name|getAt
argument_list|(
literal|1
argument_list|)
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

