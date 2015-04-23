begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.blocks
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|blocks
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
name|CreateIndexResponse
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
name|admin
operator|.
name|indices
operator|.
name|exists
operator|.
name|indices
operator|.
name|IndicesExistsResponse
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|put
operator|.
name|UpdateSettingsRequestBuilder
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
name|admin
operator|.
name|indices
operator|.
name|settings
operator|.
name|put
operator|.
name|UpdateSettingsResponse
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
name|cluster
operator|.
name|block
operator|.
name|ClusterBlockException
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
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|util
operator|.
name|HashMap
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
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
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

begin_class
annotation|@
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ElasticsearchIntegrationTest
operator|.
name|Scope
operator|.
name|TEST
argument_list|)
DECL|class|SimpleBlocksTests
specifier|public
class|class
name|SimpleBlocksTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|verifyIndexAndClusterReadOnly
specifier|public
name|void
name|verifyIndexAndClusterReadOnly
parameter_list|()
throws|throws
name|Exception
block|{
comment|// cluster.read_only = null: write and metadata not blocked
name|canCreateIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|setIndexReadOnly
argument_list|(
literal|"test1"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
comment|// cluster.read_only = true: block write and metadata
name|setClusterReadOnly
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|canNotCreateIndex
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
comment|// even if index has index.read_only = false
name|canNotIndexDocument
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
comment|// cluster.read_only = false: removes the block
name|setClusterReadOnly
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|canCreateIndex
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
comment|// newly created an index has no blocks
name|canCreateIndex
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
comment|// adds index write and metadata block
name|setIndexReadOnly
argument_list|(
literal|"ro"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|canNotIndexDocument
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
comment|// other indices not blocked
name|canCreateIndex
argument_list|(
literal|"rw"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"rw"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"rw"
argument_list|)
expr_stmt|;
comment|// blocks can be removed
name|setIndexReadOnly
argument_list|(
literal|"ro"
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
name|canIndexExists
argument_list|(
literal|"ro"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndexReadWriteMetaDataBlocks
specifier|public
name|void
name|testIndexReadWriteMetaDataBlocks
parameter_list|()
block|{
name|canCreateIndex
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
name|canIndexDocument
argument_list|(
literal|"test1"
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
name|prepareUpdateSettings
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_WRITE
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|canNotIndexDocument
argument_list|(
literal|"test1"
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
name|prepareUpdateSettings
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_WRITE
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
name|canIndexDocument
argument_list|(
literal|"test1"
argument_list|)
expr_stmt|;
block|}
DECL|method|canCreateIndex
specifier|private
name|void
name|canCreateIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
try|try
block|{
name|CreateIndexResponse
name|r
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
name|index
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
name|r
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|canNotCreateIndex
specifier|private
name|void
name|canNotCreateIndex
parameter_list|(
name|String
name|index
parameter_list|)
block|{
try|try
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
name|index
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
block|}
DECL|method|canIndexDocument
specifier|private
name|void
name|canIndexDocument
parameter_list|(
name|String
name|index
parameter_list|)
block|{
try|try
block|{
name|IndexRequestBuilder
name|builder
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
literal|"zzz"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|IndexResponse
name|r
init|=
name|builder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|r
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|canNotIndexDocument
specifier|private
name|void
name|canNotIndexDocument
parameter_list|(
name|String
name|index
parameter_list|)
block|{
try|try
block|{
name|IndexRequestBuilder
name|builder
init|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
literal|"zzz"
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setSource
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
block|}
DECL|method|canIndexExists
specifier|private
name|void
name|canIndexExists
parameter_list|(
name|String
name|index
parameter_list|)
block|{
try|try
block|{
name|IndicesExistsResponse
name|r
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
name|prepareExists
argument_list|(
name|index
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
name|r
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|canNotIndexExists
specifier|private
name|void
name|canNotIndexExists
parameter_list|(
name|String
name|index
parameter_list|)
block|{
try|try
block|{
name|IndicesExistsResponse
name|r
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
name|prepareExists
argument_list|(
name|index
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
block|}
DECL|method|setIndexReadOnly
specifier|private
name|void
name|setIndexReadOnly
parameter_list|(
name|String
name|index
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|newSettings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|newSettings
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_READ_ONLY
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|UpdateSettingsRequestBuilder
name|settingsRequest
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
name|prepareUpdateSettings
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|settingsRequest
operator|.
name|setSettings
argument_list|(
name|newSettings
argument_list|)
expr_stmt|;
name|UpdateSettingsResponse
name|settingsResponse
init|=
name|settingsRequest
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|settingsResponse
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

