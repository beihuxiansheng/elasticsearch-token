begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Throwables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|GenericAction
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
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteAction
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
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteResponse
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
name|cluster
operator|.
name|snapshots
operator|.
name|create
operator|.
name|CreateSnapshotAction
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
name|cluster
operator|.
name|snapshots
operator|.
name|create
operator|.
name|CreateSnapshotResponse
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
name|cluster
operator|.
name|stats
operator|.
name|ClusterStatsAction
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
name|cluster
operator|.
name|stats
operator|.
name|ClusterStatsResponse
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
name|cache
operator|.
name|clear
operator|.
name|ClearIndicesCacheAction
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
name|cache
operator|.
name|clear
operator|.
name|ClearIndicesCacheResponse
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
name|create
operator|.
name|CreateIndexAction
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
name|flush
operator|.
name|FlushAction
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
name|flush
operator|.
name|FlushResponse
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
name|stats
operator|.
name|IndicesStatsAction
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
name|stats
operator|.
name|IndicesStatsResponse
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
name|DeleteAction
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
name|get
operator|.
name|GetAction
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
name|get
operator|.
name|GetResponse
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
name|IndexAction
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
name|indexedscripts
operator|.
name|delete
operator|.
name|DeleteIndexedScriptAction
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
name|indexedscripts
operator|.
name|delete
operator|.
name|DeleteIndexedScriptResponse
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
name|SearchAction
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
name|client
operator|.
name|support
operator|.
name|Headers
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Map
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|AbstractClientHeadersTests
specifier|public
specifier|abstract
class|class
name|AbstractClientHeadersTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|HEADER_SETTINGS
specifier|protected
specifier|static
specifier|final
name|Settings
name|HEADER_SETTINGS
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|Headers
operator|.
name|PREFIX
operator|+
literal|".key1"
argument_list|,
literal|"val1"
argument_list|)
operator|.
name|put
argument_list|(
name|Headers
operator|.
name|PREFIX
operator|+
literal|".key2"
argument_list|,
literal|"val 2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|field|ACTIONS
specifier|private
specifier|static
specifier|final
name|GenericAction
index|[]
name|ACTIONS
init|=
operator|new
name|GenericAction
index|[]
block|{
comment|// client actions
name|GetAction
operator|.
name|INSTANCE
block|,
name|SearchAction
operator|.
name|INSTANCE
block|,
name|DeleteAction
operator|.
name|INSTANCE
block|,
name|DeleteIndexedScriptAction
operator|.
name|INSTANCE
block|,
name|IndexAction
operator|.
name|INSTANCE
block|,
comment|// cluster admin actions
name|ClusterStatsAction
operator|.
name|INSTANCE
block|,
name|CreateSnapshotAction
operator|.
name|INSTANCE
block|,
name|ClusterRerouteAction
operator|.
name|INSTANCE
block|,
comment|// indices admin actions
name|CreateIndexAction
operator|.
name|INSTANCE
block|,
name|IndicesStatsAction
operator|.
name|INSTANCE
block|,
name|ClearIndicesCacheAction
operator|.
name|INSTANCE
block|,
name|FlushAction
operator|.
name|INSTANCE
block|}
decl_stmt|;
DECL|field|threadPool
specifier|protected
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|client
specifier|private
name|Client
name|client
decl_stmt|;
annotation|@
name|Before
DECL|method|initClient
specifier|public
name|void
name|initClient
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|HEADER_SETTINGS
argument_list|)
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|threadPool
operator|=
operator|new
name|ThreadPool
argument_list|(
literal|"test-"
operator|+
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|client
operator|=
name|buildClient
argument_list|(
name|settings
argument_list|,
name|ACTIONS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanupClient
specifier|public
name|void
name|cleanupClient
parameter_list|()
throws|throws
name|Exception
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|buildClient
specifier|protected
specifier|abstract
name|Client
name|buildClient
parameter_list|(
name|Settings
name|headersSettings
parameter_list|,
name|GenericAction
index|[]
name|testedActions
parameter_list|)
function_decl|;
annotation|@
name|Test
DECL|method|testActions
specifier|public
name|void
name|testActions
parameter_list|()
block|{
comment|// TODO this is a really shitty way to test it, we need to figure out a way to test all the client methods
comment|//      without specifying each one (reflection doesn't as each action needs its own special settings, without
comment|//      them, request validation will fail before the test is executed. (one option is to enable disabling the
comment|//      validation in the settings??? - ugly and conceptually wrong)
comment|// choosing arbitrary top level actions to test
name|client
operator|.
name|prepareGet
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|GetResponse
argument_list|>
argument_list|(
name|GetAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|prepareSearch
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|SearchResponse
argument_list|>
argument_list|(
name|SearchAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|prepareDelete
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|DeleteResponse
argument_list|>
argument_list|(
name|DeleteAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|prepareDeleteIndexedScript
argument_list|(
literal|"lang"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|DeleteIndexedScriptResponse
argument_list|>
argument_list|(
name|DeleteIndexedScriptAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"source"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|IndexResponse
argument_list|>
argument_list|(
name|IndexAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// choosing arbitrary cluster admin actions to test
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|ClusterStatsResponse
argument_list|>
argument_list|(
name|ClusterStatsAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareCreateSnapshot
argument_list|(
literal|"repo"
argument_list|,
literal|"bck"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|CreateSnapshotResponse
argument_list|>
argument_list|(
name|CreateSnapshotAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareReroute
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|ClusterRerouteResponse
argument_list|>
argument_list|(
name|ClusterRerouteAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// choosing arbitrary indices admin actions to test
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
argument_list|(
name|CreateIndexAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareStats
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|IndicesStatsResponse
argument_list|>
argument_list|(
name|IndicesStatsAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClearCache
argument_list|(
literal|"idx1"
argument_list|,
literal|"idx2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|ClearIndicesCacheResponse
argument_list|>
argument_list|(
name|ClearIndicesCacheAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|FlushResponse
argument_list|>
argument_list|(
name|FlushAction
operator|.
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOverideHeader
specifier|public
name|void
name|testOverideHeader
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|key1Val
init|=
name|randomAsciiOfLength
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"key1"
argument_list|,
name|key1Val
argument_list|)
decl|.
name|put
argument_list|(
literal|"key2"
argument_list|,
literal|"val 2"
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|client
operator|.
name|prepareGet
argument_list|(
literal|"idx"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|)
operator|.
name|putHeader
argument_list|(
literal|"key1"
argument_list|,
name|key1Val
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|GetResponse
argument_list|>
argument_list|(
name|GetAction
operator|.
name|NAME
argument_list|,
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareClusterStats
argument_list|()
operator|.
name|putHeader
argument_list|(
literal|"key1"
argument_list|,
name|key1Val
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|ClusterStatsResponse
argument_list|>
argument_list|(
name|ClusterStatsAction
operator|.
name|NAME
argument_list|,
name|expected
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"idx"
argument_list|)
operator|.
name|putHeader
argument_list|(
literal|"key1"
argument_list|,
name|key1Val
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|addListener
argument_list|(
operator|new
name|AssertingActionListener
argument_list|<
name|CreateIndexResponse
argument_list|>
argument_list|(
name|CreateIndexAction
operator|.
name|NAME
argument_list|,
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHeaders
specifier|protected
specifier|static
name|void
name|assertHeaders
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|headers
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedEntry
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
name|expectedEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedEntry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertHeaders
specifier|protected
specifier|static
name|void
name|assertHeaders
parameter_list|(
name|TransportMessage
argument_list|<
name|?
argument_list|>
name|message
parameter_list|)
block|{
name|assertHeaders
argument_list|(
name|message
argument_list|,
name|HEADER_SETTINGS
operator|.
name|getAsSettings
argument_list|(
name|Headers
operator|.
name|PREFIX
argument_list|)
operator|.
name|getAsStructuredMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertHeaders
specifier|protected
specifier|static
name|void
name|assertHeaders
parameter_list|(
name|TransportMessage
argument_list|<
name|?
argument_list|>
name|message
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expected
parameter_list|)
block|{
name|assertThat
argument_list|(
name|message
operator|.
name|getHeaders
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|message
operator|.
name|getHeaders
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedEntry
range|:
name|expected
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|message
operator|.
name|getHeader
argument_list|(
name|expectedEntry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedEntry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InternalException
specifier|protected
specifier|static
class|class
name|InternalException
extends|extends
name|Exception
block|{
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|headers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
decl_stmt|;
DECL|method|InternalException
specifier|public
name|InternalException
parameter_list|(
name|String
name|action
parameter_list|,
name|TransportMessage
argument_list|<
name|?
argument_list|>
name|message
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|headers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|message
operator|.
name|getHeaders
argument_list|()
control|)
block|{
name|headers
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|message
operator|.
name|getHeader
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|AssertingActionListener
specifier|protected
specifier|static
class|class
name|AssertingActionListener
parameter_list|<
name|T
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|T
argument_list|>
block|{
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|expectedHeaders
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedHeaders
decl_stmt|;
DECL|method|AssertingActionListener
specifier|public
name|AssertingActionListener
parameter_list|(
name|String
name|action
parameter_list|)
block|{
name|this
argument_list|(
name|action
argument_list|,
name|HEADER_SETTINGS
operator|.
name|getAsSettings
argument_list|(
name|Headers
operator|.
name|PREFIX
argument_list|)
operator|.
name|getAsStructuredMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AssertingActionListener
specifier|public
name|AssertingActionListener
parameter_list|(
name|String
name|action
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|expectedHeaders
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|expectedHeaders
operator|=
name|expectedHeaders
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|T
name|t
parameter_list|)
block|{
name|fail
argument_list|(
literal|"an internal exception was expected for action ["
operator|+
name|action
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|Throwable
name|e
init|=
name|unwrap
argument_list|(
name|t
argument_list|,
name|InternalException
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"expected action ["
operator|+
name|action
operator|+
literal|"] to throw an internal exception"
argument_list|,
name|e
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|action
argument_list|,
name|equalTo
argument_list|(
operator|(
operator|(
name|InternalException
operator|)
name|e
operator|)
operator|.
name|action
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|headers
init|=
operator|(
operator|(
name|InternalException
operator|)
name|e
operator|)
operator|.
name|headers
decl_stmt|;
name|assertHeaders
argument_list|(
name|headers
argument_list|,
name|expectedHeaders
argument_list|)
expr_stmt|;
block|}
DECL|method|unwrap
specifier|public
name|Throwable
name|unwrap
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
name|exceptionType
parameter_list|)
block|{
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|Throwable
name|result
init|=
name|t
decl_stmt|;
while|while
condition|(
operator|!
name|exceptionType
operator|.
name|isInstance
argument_list|(
name|result
argument_list|)
condition|)
block|{
if|if
condition|(
name|result
operator|.
name|getCause
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|result
operator|.
name|getCause
argument_list|()
operator|==
name|result
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|counter
operator|++
operator|>
literal|10
condition|)
block|{
comment|// dear god, if we got more than 10 levels down, WTF? just bail
name|fail
argument_list|(
literal|"Exception cause unwrapping ran for 10 levels: "
operator|+
name|Throwables
operator|.
name|getStackTraceAsString
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|result
operator|=
name|result
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

