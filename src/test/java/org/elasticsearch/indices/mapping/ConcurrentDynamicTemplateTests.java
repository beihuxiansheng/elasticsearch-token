begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.mapping
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|mapping
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
name|ActionListener
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
name|IndexResponse
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
name|Strings
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
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|CopyOnWriteArrayList
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
name|CountDownLatch
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
name|ElasticSearchAssertions
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
name|emptyIterable
import|;
end_import

begin_class
DECL|class|ConcurrentDynamicTemplateTests
specifier|public
class|class
name|ConcurrentDynamicTemplateTests
extends|extends
name|AbstractIntegrationTest
block|{
DECL|field|mappingType
specifier|private
specifier|final
name|String
name|mappingType
init|=
literal|"test-mapping"
decl_stmt|;
annotation|@
name|Test
comment|// see #3544
DECL|method|testConcurrentDynamicMapping
specifier|public
name|void
name|testConcurrentDynamicMapping
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|mapping
init|=
literal|"{"
operator|+
name|mappingType
operator|+
literal|": {"
operator|+
literal|"\"properties\": {"
operator|+
literal|"\"an_id\": {"
operator|+
literal|"\"type\": \"string\","
operator|+
literal|"\"store\": \"yes\","
operator|+
literal|"\"index\": \"not_analyzed\""
operator|+
literal|"}"
operator|+
literal|"},"
operator|+
literal|"\"dynamic_templates\": ["
operator|+
literal|"{"
operator|+
literal|"\"participants\": {"
operator|+
literal|"\"path_match\": \"*\","
operator|+
literal|"\"mapping\": {"
operator|+
literal|"\"type\": \"string\","
operator|+
literal|"\"store\": \"yes\","
operator|+
literal|"\"index\": \"analyzed\","
operator|+
literal|"\"analyzer\": \"whitespace\""
operator|+
literal|"}"
operator|+
literal|"}"
operator|+
literal|"}"
operator|+
literal|"]"
operator|+
literal|"}"
operator|+
literal|"}"
decl_stmt|;
comment|// The 'fieldNames' array is used to help with retrieval of index terms
comment|// after testing
specifier|final
name|String
name|fieldName
init|=
literal|"participants.ACCEPTED"
decl_stmt|;
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|5
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
name|wipeIndex
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
literal|"number_of_shards"
argument_list|,
name|between
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
name|between
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|addMapping
argument_list|(
name|mappingType
argument_list|,
name|mapping
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|ensureYellow
argument_list|()
expr_stmt|;
name|int
name|numDocs
init|=
name|atLeast
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numDocs
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Throwable
argument_list|>
name|throwable
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|source
operator|.
name|put
argument_list|(
literal|"an_id"
argument_list|,
name|Strings
operator|.
name|randomBase64UUID
argument_list|(
name|getRandom
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|source
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
literal|"test-user"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
name|mappingType
argument_list|)
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|ActionListener
argument_list|<
name|IndexResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|IndexResponse
name|response
parameter_list|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|throwable
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|throwable
argument_list|,
name|emptyIterable
argument_list|()
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
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
name|matchQuery
argument_list|(
name|fieldName
argument_list|,
literal|"test-user"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
name|numDocs
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
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
name|matchQuery
argument_list|(
name|fieldName
argument_list|,
literal|"test user"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

