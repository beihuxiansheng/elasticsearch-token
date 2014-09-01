begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
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
name|Predicate
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
name|ActionRequestBuilder
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
name|state
operator|.
name|ClusterStateResponse
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
name|count
operator|.
name|CountResponse
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
name|percolate
operator|.
name|PercolateSourceBuilder
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|discovery
operator|.
name|DiscoverySettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|MasterNotDiscoveredException
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
name|ScriptService
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
name|ElasticsearchIntegrationTest
operator|.
name|ClusterScope
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
name|HashMap
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
operator|.
name|PercolateSourceBuilder
operator|.
name|docBuilder
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
name|elasticsearch
operator|.
name|test
operator|.
name|ElasticsearchIntegrationTest
operator|.
name|Scope
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|)
DECL|class|NoMasterNodeTests
specifier|public
class|class
name|NoMasterNodeTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
annotation|@
name|TestLogging
argument_list|(
literal|"action:TRACE,cluster.service:TRACE"
argument_list|)
DECL|method|testNoMasterActions
specifier|public
name|void
name|testNoMasterActions
parameter_list|()
throws|throws
name|Exception
block|{
comment|// note, sometimes, we want to check with the fact that an index gets created, sometimes not...
name|boolean
name|autoCreateIndex
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"auto_create_index set to {}"
argument_list|,
name|autoCreateIndex
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"zen"
argument_list|)
operator|.
name|put
argument_list|(
literal|"action.auto_create_index"
argument_list|,
name|autoCreateIndex
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.minimum_master_nodes"
argument_list|,
literal|2
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.ping_timeout"
argument_list|,
literal|"200ms"
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.initial_state_timeout"
argument_list|,
literal|"500ms"
argument_list|)
operator|.
name|put
argument_list|(
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK
argument_list|,
literal|"all"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TimeValue
name|timeout
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
comment|// start a second node, create an index, and then shut it down so we have no master block
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
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
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|ClusterState
name|state
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|setLocal
argument_list|(
literal|true
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK_ID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"no_index"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareMultiGet
argument_list|()
operator|.
name|add
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareMultiGet
argument_list|()
operator|.
name|add
argument_list|(
literal|"no_index"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|PercolateSourceBuilder
name|percolateSource
init|=
operator|new
name|PercolateSourceBuilder
argument_list|()
decl_stmt|;
name|percolateSource
operator|.
name|setDoc
argument_list|(
name|docBuilder
argument_list|()
operator|.
name|setDoc
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|percolateSource
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|percolateSource
operator|=
operator|new
name|PercolateSourceBuilder
argument_list|()
expr_stmt|;
name|percolateSource
operator|.
name|setDoc
argument_list|(
name|docBuilder
argument_list|()
operator|.
name|setDoc
argument_list|(
operator|new
name|HashMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|preparePercolate
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|"no_index"
argument_list|)
operator|.
name|setDocumentType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|percolateSource
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAnalyze
argument_list|(
literal|"test"
argument_list|,
literal|"this is a test"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareAnalyze
argument_list|(
literal|"no_index"
argument_list|,
literal|"this is a test"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|(
literal|"no_index"
argument_list|)
argument_list|,
name|ClusterBlockException
operator|.
name|class
argument_list|,
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
expr_stmt|;
name|checkWriteAction
argument_list|(
literal|false
argument_list|,
name|timeout
argument_list|,
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setScript
argument_list|(
literal|"test script"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
name|checkWriteAction
argument_list|(
name|autoCreateIndex
argument_list|,
name|timeout
argument_list|,
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"no_index"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setScript
argument_list|(
literal|"test script"
argument_list|,
name|ScriptService
operator|.
name|ScriptType
operator|.
name|INLINE
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
name|checkWriteAction
argument_list|(
literal|false
argument_list|,
name|timeout
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
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
name|checkWriteAction
argument_list|(
name|autoCreateIndex
argument_list|,
name|timeout
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"no_index"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
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
name|endObject
argument_list|()
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
name|BulkRequestBuilder
name|bulkRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
decl_stmt|;
name|bulkRequestBuilder
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
literal|"type1"
argument_list|,
literal|"1"
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
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bulkRequestBuilder
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
literal|"type1"
argument_list|,
literal|"2"
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
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkBulkAction
argument_list|(
literal|false
argument_list|,
name|bulkRequestBuilder
argument_list|)
expr_stmt|;
name|bulkRequestBuilder
operator|=
name|client
argument_list|()
operator|.
name|prepareBulk
argument_list|()
expr_stmt|;
name|bulkRequestBuilder
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"no_index"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
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
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|bulkRequestBuilder
operator|.
name|add
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"no_index"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
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
name|endObject
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|checkBulkAction
argument_list|(
name|autoCreateIndex
argument_list|,
name|bulkRequestBuilder
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|checkWriteAction
name|void
name|checkWriteAction
parameter_list|(
name|boolean
name|autoCreateIndex
parameter_list|,
name|TimeValue
name|timeout
parameter_list|,
name|ActionRequestBuilder
argument_list|<
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|,
name|?
argument_list|>
name|builder
parameter_list|)
block|{
comment|// we clean the metadata when loosing a master, therefore all operations on indices will auto create it, if allowed
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expected ClusterBlockException or MasterNotDiscoveredException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
decl||
name|MasterNotDiscoveredException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|MasterNotDiscoveredException
condition|)
block|{
name|assertTrue
argument_list|(
name|autoCreateIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|autoCreateIndex
argument_list|)
expr_stmt|;
block|}
comment|// verify we waited before giving up...
name|assertThat
argument_list|(
name|e
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
argument_list|,
name|greaterThan
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|-
literal|50
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkBulkAction
name|void
name|checkBulkAction
parameter_list|(
name|boolean
name|indexShouldBeAutoCreated
parameter_list|,
name|BulkRequestBuilder
name|builder
parameter_list|)
block|{
comment|// bulk operation do not throw MasterNotDiscoveredException exceptions. The only test that auto create kicked in and failed is
comment|// via the timeout, as bulk operation do not wait on blocks.
name|TimeValue
name|timeout
decl_stmt|;
if|if
condition|(
name|indexShouldBeAutoCreated
condition|)
block|{
comment|// we expect the bulk to fail because it will try to go to the master. Use small timeout and detect it has passed
name|timeout
operator|=
operator|new
name|TimeValue
argument_list|(
literal|200
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// the request should fail very quickly - use a large timeout and make sure it didn't pass...
name|timeout
operator|=
operator|new
name|TimeValue
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|builder
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ClusterBlockException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
if|if
condition|(
name|indexShouldBeAutoCreated
condition|)
block|{
comment|// timeout is 200
name|assertThat
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
argument_list|,
name|greaterThan
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|-
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// timeout is 5000
name|assertThat
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
argument_list|,
name|lessThan
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|-
literal|50
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testNoMasterActions_writeMasterBlock
specifier|public
name|void
name|testNoMasterActions_writeMasterBlock
parameter_list|()
throws|throws
name|Exception
block|{
name|Settings
name|settings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"zen"
argument_list|)
operator|.
name|put
argument_list|(
literal|"action.auto_create_index"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.minimum_master_nodes"
argument_list|,
literal|2
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.zen.ping_timeout"
argument_list|,
literal|"200ms"
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.initial_state_timeout"
argument_list|,
literal|"500ms"
argument_list|)
operator|.
name|put
argument_list|(
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK
argument_list|,
literal|"write"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
comment|// start a second node, create an index, and then shut it down so we have no master block
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|2
argument_list|,
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
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
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test2"
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
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|ensureSearchable
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|ClusterStateResponse
name|clusterState
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Cluster state:\n"
operator|+
name|clusterState
operator|.
name|getState
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomDataNode
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ClusterState
name|state
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|setLocal
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK_ID
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|GetResponse
name|getResponse
init|=
name|client
argument_list|()
operator|.
name|prepareGet
argument_list|(
literal|"test1"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertExists
argument_list|(
name|getResponse
argument_list|)
expr_stmt|;
name|CountResponse
name|countResponse
init|=
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertHitCount
argument_list|(
name|countResponse
argument_list|,
literal|1l
argument_list|)
expr_stmt|;
name|SearchResponse
name|searchResponse
init|=
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test1"
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
name|countResponse
operator|=
name|client
argument_list|()
operator|.
name|prepareCount
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|countResponse
operator|.
name|getTotalShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|countResponse
operator|.
name|getSuccessfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|TimeValue
name|timeout
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|prepareUpdate
argument_list|(
literal|"test1"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setDoc
argument_list|(
literal|"field"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ClusterBlockException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
argument_list|,
name|greaterThan
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|-
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|now
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
try|try
block|{
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
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|endObject
argument_list|()
argument_list|)
operator|.
name|setTimeout
argument_list|(
name|timeout
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ClusterBlockException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|now
argument_list|,
name|greaterThan
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|-
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|internalCluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|setWaitForNodes
argument_list|(
literal|"2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

