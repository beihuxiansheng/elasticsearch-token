begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|LifecycleScope
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
name|repositories
operator|.
name|put
operator|.
name|PutRepositoryResponse
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
name|client
operator|.
name|Client
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
name|snapshots
operator|.
name|mockstore
operator|.
name|MockRepositoryModule
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
name|store
operator|.
name|MockDirectoryHelper
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
name|ArrayList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
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
comment|/**  */
end_comment

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
argument_list|,
name|numNodes
operator|=
literal|0
argument_list|)
DECL|class|DedicatedClusterSnapshotRestoreTests
specifier|public
class|class
name|DedicatedClusterSnapshotRestoreTests
extends|extends
name|AbstractSnapshotTests
block|{
annotation|@
name|Test
DECL|method|restorePersistentSettingsTest
specifier|public
name|void
name|restorePersistentSettingsTest
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> start node"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"local"
argument_list|)
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
comment|// Add dummy persistent setting
name|logger
operator|.
name|info
argument_list|(
literal|"--> set test persistent setting"
argument_list|)
expr_stmt|;
name|String
name|settingValue
init|=
literal|"test-"
operator|+
name|randomInt
argument_list|()
decl_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|()
operator|.
name|setPersistentSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|THREADPOOL_GROUP
operator|+
literal|"dummy.value"
argument_list|,
name|settingValue
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|client
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
name|setFilterRoutingTable
argument_list|(
literal|true
argument_list|)
operator|.
name|setFilterNodes
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
operator|.
name|getMetaData
argument_list|()
operator|.
name|persistentSettings
argument_list|()
operator|.
name|get
argument_list|(
name|ThreadPool
operator|.
name|THREADPOOL_GROUP
operator|+
literal|"dummy.value"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|settingValue
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> create repository"
argument_list|)
expr_stmt|;
name|PutRepositoryResponse
name|putRepositoryResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"fs"
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
literal|"location"
argument_list|,
name|newTempDir
argument_list|()
argument_list|)
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
name|putRepositoryResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> start snapshot"
argument_list|)
expr_stmt|;
name|CreateSnapshotResponse
name|createSnapshotResponse
init|=
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
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|)
operator|.
name|setWaitForCompletion
argument_list|(
literal|true
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
name|createSnapshotResponse
operator|.
name|getSnapshotInfo
argument_list|()
operator|.
name|totalShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|createSnapshotResponse
operator|.
name|getSnapshotInfo
argument_list|()
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareGetSnapshots
argument_list|(
literal|"test-repo"
argument_list|)
operator|.
name|setSnapshots
argument_list|(
literal|"test-snap"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getSnapshots
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|state
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SnapshotState
operator|.
name|SUCCESS
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> clean the test persistent setting"
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
name|prepareUpdateSettings
argument_list|()
operator|.
name|setPersistentSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|ThreadPool
operator|.
name|THREADPOOL_GROUP
operator|+
literal|"dummy.value"
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|client
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
name|setFilterRoutingTable
argument_list|(
literal|true
argument_list|)
operator|.
name|setFilterNodes
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
operator|.
name|getMetaData
argument_list|()
operator|.
name|persistentSettings
argument_list|()
operator|.
name|get
argument_list|(
name|ThreadPool
operator|.
name|THREADPOOL_GROUP
operator|+
literal|"dummy.value"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> restore snapshot"
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
name|prepareRestoreSnapshot
argument_list|(
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|)
operator|.
name|setRestoreGlobalState
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitForCompletion
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
name|assertThat
argument_list|(
name|client
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
name|setFilterRoutingTable
argument_list|(
literal|true
argument_list|)
operator|.
name|setFilterNodes
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
operator|.
name|getMetaData
argument_list|()
operator|.
name|persistentSettings
argument_list|()
operator|.
name|get
argument_list|(
name|ThreadPool
operator|.
name|THREADPOOL_GROUP
operator|+
literal|"dummy.value"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|settingValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|snapshotDuringNodeShutdownTest
specifier|public
name|void
name|snapshotDuringNodeShutdownTest
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> start 2 nodes"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|()
argument_list|)
expr_stmt|;
name|Client
name|client
init|=
name|client
argument_list|()
decl_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test-idx"
argument_list|,
literal|2
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"number_of_shards"
argument_list|,
literal|2
argument_list|)
operator|.
name|put
argument_list|(
literal|"number_of_replicas"
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|MockDirectoryHelper
operator|.
name|RANDOM_NO_DELETE_OPEN_FILE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing some data"
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|index
argument_list|(
literal|"test-idx"
argument_list|,
literal|"doc"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|client
operator|.
name|prepareCount
argument_list|(
literal|"test-idx"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100L
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> create repository"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating repository"
argument_list|)
expr_stmt|;
name|PutRepositoryResponse
name|putRepositoryResponse
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|preparePutRepository
argument_list|(
literal|"test-repo"
argument_list|)
operator|.
name|setType
argument_list|(
name|MockRepositoryModule
operator|.
name|class
operator|.
name|getCanonicalName
argument_list|()
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
literal|"location"
argument_list|,
name|newTempDir
argument_list|(
name|LifecycleScope
operator|.
name|TEST
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"random"
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"wait_after_unblock"
argument_list|,
literal|200
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|putRepositoryResponse
operator|.
name|isAcknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Pick one node and block it
name|String
name|blockedNode
init|=
name|blockNodeWithIndex
argument_list|(
literal|"test-idx"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> snapshot"
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
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|)
operator|.
name|setWaitForCompletion
argument_list|(
literal|false
argument_list|)
operator|.
name|setIndices
argument_list|(
literal|"test-idx"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for block to kick in"
argument_list|)
expr_stmt|;
name|waitForBlock
argument_list|(
name|blockedNode
argument_list|,
literal|"test-repo"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|60
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> execution was blocked on node [{}], shutting it down"
argument_list|,
name|blockedNode
argument_list|)
expr_stmt|;
name|unblockNode
argument_list|(
name|blockedNode
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stopping node"
argument_list|,
name|blockedNode
argument_list|)
expr_stmt|;
name|stopNode
argument_list|(
name|blockedNode
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for completion"
argument_list|)
expr_stmt|;
name|SnapshotInfo
name|snapshotInfo
init|=
name|waitForCompletion
argument_list|(
literal|"test-repo"
argument_list|,
literal|"test-snap"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|60
argument_list|)
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Number of failed shards [{}]"
argument_list|,
name|snapshotInfo
operator|.
name|shardFailures
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> done"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

