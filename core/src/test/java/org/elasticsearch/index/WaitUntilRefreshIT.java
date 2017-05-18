begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|ActionFuture
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
name|DocWriteResponse
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
name|bulk
operator|.
name|BulkItemResponse
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
name|bulk
operator|.
name|BulkRequestBuilder
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
name|bulk
operator|.
name|BulkResponse
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
name|delete
operator|.
name|DeleteResponse
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
name|action
operator|.
name|support
operator|.
name|WriteRequest
operator|.
name|RefreshPolicy
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
name|update
operator|.
name|UpdateResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
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
name|plugins
operator|.
name|Plugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|MockScriptPlugin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|Script
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptType
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
name|junit
operator|.
name|Before
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
name|Collections
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
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyMap
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singleton
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
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
name|assertNoFailures
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
name|assertNoSearchHits
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

begin_comment
comment|/**  * Tests that requests with RefreshPolicy.WAIT_UNTIL will be visible when they return.  */
end_comment

begin_class
DECL|class|WaitUntilRefreshIT
specifier|public
class|class
name|WaitUntilRefreshIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|indexSettings
specifier|public
name|Settings
name|indexSettings
parameter_list|()
block|{
comment|// Use a shorter refresh interval to speed up the tests. We'll be waiting on this interval several times.
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|super
operator|.
name|indexSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
literal|"40ms"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Before
DECL|method|createTestIndex
specifier|public
name|void
name|createTestIndex
parameter_list|()
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndex
specifier|public
name|void
name|testIndex
parameter_list|()
block|{
name|IndexResponse
name|index
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"index"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|RestStatus
operator|.
name|CREATED
argument_list|,
name|index
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|index
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
DECL|method|testDelete
specifier|public
name|void
name|testDelete
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
comment|// Index normally
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
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Now delete with blockUntilRefresh
name|DeleteResponse
name|delete
init|=
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|DocWriteResponse
operator|.
name|Result
operator|.
name|DELETED
argument_list|,
name|delete
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|delete
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testUpdate
specifier|public
name|void
name|testUpdate
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
comment|// Index normally
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
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Update with RefreshPolicy.WAIT_UNTIL
name|UpdateResponse
name|update
init|=
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setDoc
argument_list|(
name|Requests
operator|.
name|INDEX_CONTENT_TYPE
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|update
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|update
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Upsert with RefreshPolicy.WAIT_UNTIL
name|update
operator|=
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setDocAsUpsert
argument_list|(
literal|true
argument_list|)
operator|.
name|setDoc
argument_list|(
name|Requests
operator|.
name|INDEX_CONTENT_TYPE
argument_list|,
literal|"foo"
argument_list|,
literal|"cat"
argument_list|)
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|update
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|update
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
comment|// Update-becomes-delete with RefreshPolicy.WAIT_UNTIL
name|update
operator|=
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setScript
argument_list|(
operator|new
name|Script
argument_list|(
name|ScriptType
operator|.
name|INLINE
argument_list|,
literal|"mockscript"
argument_list|,
literal|"delete_plz"
argument_list|,
name|emptyMap
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|update
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|update
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"cat"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBulk
specifier|public
name|void
name|testBulk
parameter_list|()
block|{
comment|// Index by bulk with RefreshPolicy.WAIT_UNTIL
name|BulkRequestBuilder
name|bulk
init|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
decl_stmt|;
name|bulk
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBulkSuccess
argument_list|(
name|bulk
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Update by bulk with RefreshPolicy.WAIT_UNTIL
name|bulk
operator|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
expr_stmt|;
name|bulk
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setDoc
argument_list|(
name|Requests
operator|.
name|INDEX_CONTENT_TYPE
argument_list|,
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBulkSuccess
argument_list|(
name|bulk
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"baz"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
comment|// Delete by bulk with RefreshPolicy.WAIT_UNTIL
name|bulk
operator|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
expr_stmt|;
name|bulk
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBulkSuccess
argument_list|(
name|bulk
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertNoSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
comment|// Update makes a noop
name|bulk
operator|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
expr_stmt|;
name|bulk
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertBulkSuccess
argument_list|(
name|bulk
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests that an explicit request makes block_until_refresh return. It doesn't check that block_until_refresh doesn't return until the      * explicit refresh if the interval is -1 because we don't have that kind of control over refresh. It can happen all on its own.      */
DECL|method|testNoRefreshInterval
specifier|public
name|void
name|testNoRefreshInterval
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
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|singletonMap
argument_list|(
literal|"index.refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ActionFuture
argument_list|<
name|IndexResponse
argument_list|>
name|index
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"index"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|setRefreshPolicy
argument_list|(
name|RefreshPolicy
operator|.
name|WAIT_UNTIL
argument_list|)
operator|.
name|execute
argument_list|()
decl_stmt|;
while|while
condition|(
literal|false
operator|==
name|index
operator|.
name|isDone
argument_list|()
condition|)
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
name|prepareRefresh
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|RestStatus
operator|.
name|CREATED
argument_list|,
name|index
operator|.
name|get
argument_list|()
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|index
operator|.
name|get
argument_list|()
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
name|assertSearchHits
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
name|matchQuery
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertBulkSuccess
specifier|private
name|void
name|assertBulkSuccess
parameter_list|(
name|BulkResponse
name|response
parameter_list|)
block|{
name|assertNoFailures
argument_list|(
name|response
argument_list|)
expr_stmt|;
for|for
control|(
name|BulkItemResponse
name|item
range|:
name|response
control|)
block|{
name|assertFalse
argument_list|(
literal|"request shouldn't have forced a refresh"
argument_list|,
name|item
operator|.
name|getResponse
argument_list|()
operator|.
name|forcedRefresh
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|singleton
argument_list|(
name|DeletePlzPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|class|DeletePlzPlugin
specifier|public
specifier|static
class|class
name|DeletePlzPlugin
extends|extends
name|MockScriptPlugin
block|{
annotation|@
name|Override
DECL|method|pluginScripts
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Function
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|,
name|Object
argument_list|>
argument_list|>
name|pluginScripts
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonMap
argument_list|(
literal|"delete_plz"
argument_list|,
name|params
lambda|->
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|ctx
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|params
operator|.
name|get
argument_list|(
literal|"ctx"
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|put
argument_list|(
literal|"op"
argument_list|,
literal|"delete"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

