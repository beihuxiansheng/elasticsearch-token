begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tribe
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
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
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthStatus
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
name|cluster
operator|.
name|ClusterState
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
name|node
operator|.
name|DiscoveryNode
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
name|node
operator|.
name|DiscoveryNodes
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
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|TestCluster
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|concurrent
operator|.
name|TimeUnit
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

begin_comment
comment|/**  * Note, when talking to tribe client, no need to set the local flag on master read operations, it  * does it by default.  */
end_comment

begin_class
DECL|class|TribeTests
specifier|public
class|class
name|TribeTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|cluster2
specifier|private
specifier|static
name|TestCluster
name|cluster2
decl_stmt|;
DECL|field|tribeNode
specifier|private
name|Node
name|tribeNode
decl_stmt|;
DECL|field|tribeClient
specifier|private
name|Client
name|tribeClient
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupSecondCluster
specifier|public
specifier|static
name|void
name|setupSecondCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|ElasticsearchIntegrationTest
operator|.
name|beforeClass
argument_list|()
expr_stmt|;
comment|// create another cluster
name|cluster2
operator|=
operator|new
name|TestCluster
argument_list|(
name|randomLong
argument_list|()
argument_list|,
literal|2
argument_list|,
literal|2
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
name|cluster2
operator|.
name|beforeTest
argument_list|(
name|getRandom
argument_list|()
argument_list|,
literal|0.1
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|ensureAtLeastNumNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDownSecondCluster
specifier|public
specifier|static
name|void
name|tearDownSecondCluster
parameter_list|()
block|{
if|if
condition|(
name|cluster2
operator|!=
literal|null
condition|)
block|{
name|cluster2
operator|.
name|afterTest
argument_list|()
expr_stmt|;
name|cluster2
operator|.
name|close
argument_list|()
expr_stmt|;
name|cluster2
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|After
DECL|method|tearDownTribeNode
specifier|public
name|void
name|tearDownTribeNode
parameter_list|()
block|{
if|if
condition|(
name|cluster2
operator|!=
literal|null
condition|)
block|{
name|cluster2
operator|.
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"_all"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|tribeNode
operator|!=
literal|null
condition|)
block|{
name|tribeNode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setupTribeNode
specifier|private
name|void
name|setupTribeNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|Settings
name|merged
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"tribe.t1.cluster.name"
argument_list|,
name|cluster
argument_list|()
operator|.
name|getClusterName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.t2.cluster.name"
argument_list|,
name|cluster2
operator|.
name|getClusterName
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.blocks.write"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.blocks.read"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|tribeNode
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|merged
argument_list|)
operator|.
name|node
argument_list|()
expr_stmt|;
name|tribeClient
operator|=
name|tribeNode
operator|.
name|client
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGlobalReadWriteBlocks
specifier|public
name|void
name|testGlobalReadWriteBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"create 2 indices, test1 on t1, and test2 on t2"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|setupTribeNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"tribe.blocks.write"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"tribe.blocks.metadata"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"wait till tribe has the same nodes as the 2 clusters"
argument_list|)
expr_stmt|;
name|awaitSameNodeCounts
argument_list|()
expr_stmt|;
comment|// wait till the tribe node connected to the cluster, by checking if the index exists in the cluster state
name|logger
operator|.
name|info
argument_list|(
literal|"wait till test1 and test2 exists in the tribe node state"
argument_list|)
expr_stmt|;
name|awaitIndicesInClusterState
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
try|try
block|{
name|tribeClient
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
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"cluster block should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well!
block|}
try|try
block|{
name|tribeClient
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOptimize
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"cluster block should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well!
block|}
try|try
block|{
name|tribeClient
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOptimize
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"cluster block should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well!
block|}
block|}
annotation|@
name|Test
DECL|method|testIndexWriteBlocks
specifier|public
name|void
name|testIndexWriteBlocks
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"create 2 indices, test1 on t1, and test2 on t2"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
name|get
argument_list|()
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
literal|"block_test1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"block_test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|setupTribeNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"tribe.blocks.write.indices"
argument_list|,
literal|"block_*"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"wait till tribe has the same nodes as the 2 clusters"
argument_list|)
expr_stmt|;
name|awaitSameNodeCounts
argument_list|()
expr_stmt|;
comment|// wait till the tribe node connected to the cluster, by checking if the index exists in the cluster state
name|logger
operator|.
name|info
argument_list|(
literal|"wait till test1 and test2 exists in the tribe node state"
argument_list|)
expr_stmt|;
name|awaitIndicesInClusterState
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"block_test1"
argument_list|,
literal|"block_test2"
argument_list|)
expr_stmt|;
name|tribeClient
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
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
try|try
block|{
name|tribeClient
operator|.
name|prepareIndex
argument_list|(
literal|"block_test1"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"cluster block should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well!
block|}
name|tribeClient
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
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
try|try
block|{
name|tribeClient
operator|.
name|prepareIndex
argument_list|(
literal|"block_test2"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"cluster block should be thrown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterBlockException
name|e
parameter_list|)
block|{
comment|// all is well!
block|}
block|}
annotation|@
name|Test
DECL|method|testOnConflictDrop
specifier|public
name|void
name|testOnConflictDrop
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"create 2 indices, test1 on t1, and test2 on t2"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
literal|"conflict"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"conflict"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|setupTribeNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"tribe.on_conflict"
argument_list|,
literal|"drop"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"wait till tribe has the same nodes as the 2 clusters"
argument_list|)
expr_stmt|;
name|awaitSameNodeCounts
argument_list|()
expr_stmt|;
comment|// wait till the tribe node connected to the cluster, by checking if the index exists in the cluster state
name|logger
operator|.
name|info
argument_list|(
literal|"wait till test1 and test2 exists in the tribe node state"
argument_list|)
expr_stmt|;
name|awaitIndicesInClusterState
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|TribeService
operator|.
name|TRIBE_NAME
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"t1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|TribeService
operator|.
name|TRIBE_NAME
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"t2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"conflict"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOnConflictPrefer
specifier|public
name|void
name|testOnConflictPrefer
parameter_list|()
throws|throws
name|Exception
block|{
name|testOnConflictPrefer
argument_list|(
name|randomBoolean
argument_list|()
condition|?
literal|"t1"
else|:
literal|"t2"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnConflictPrefer
specifier|private
name|void
name|testOnConflictPrefer
parameter_list|(
name|String
name|tribe
parameter_list|)
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"testing preference for tribe {}"
argument_list|,
name|tribe
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"create 2 indices, test1 on t1, and test2 on t2"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
literal|"conflict"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"conflict"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|setupTribeNode
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"tribe.on_conflict"
argument_list|,
literal|"prefer_"
operator|+
name|tribe
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"wait till tribe has the same nodes as the 2 clusters"
argument_list|)
expr_stmt|;
name|awaitSameNodeCounts
argument_list|()
expr_stmt|;
comment|// wait till the tribe node connected to the cluster, by checking if the index exists in the cluster state
name|logger
operator|.
name|info
argument_list|(
literal|"wait till test1 and test2 exists in the tribe node state"
argument_list|)
expr_stmt|;
name|awaitIndicesInClusterState
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|,
literal|"conflict"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|TribeService
operator|.
name|TRIBE_NAME
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"t1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|TribeService
operator|.
name|TRIBE_NAME
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"t2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
operator|.
name|getState
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"conflict"
argument_list|)
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|TribeService
operator|.
name|TRIBE_NAME
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|tribe
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTribeOnOneCluster
specifier|public
name|void
name|testTribeOnOneCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|setupTribeNode
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"create 2 indices, test1 on t1, and test2 on t2"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
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
name|get
argument_list|()
expr_stmt|;
name|cluster2
operator|.
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
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// wait till the tribe node connected to the cluster, by checking if the index exists in the cluster state
name|logger
operator|.
name|info
argument_list|(
literal|"wait till test1 and test2 exists in the tribe node state"
argument_list|)
expr_stmt|;
name|awaitIndicesInClusterState
argument_list|(
literal|"test1"
argument_list|,
literal|"test2"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"wait till tribe has the same nodes as the 2 clusters"
argument_list|)
expr_stmt|;
name|awaitSameNodeCounts
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tribeClient
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
name|get
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"create 2 docs through the tribe node"
argument_list|)
expr_stmt|;
name|tribeClient
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
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|tribeClient
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
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|tribeClient
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"verify they are there"
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|tribeClient
operator|.
name|prepareCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|tribeClient
operator|.
name|prepareSearch
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|2l
argument_list|)
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ClusterState
name|tribeState
init|=
name|tribeNode
operator|.
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
operator|.
name|getState
argument_list|()
decl_stmt|;
return|return
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type1"
argument_list|)
operator|!=
literal|null
operator|&&
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type2"
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"write to another type"
argument_list|)
expr_stmt|;
name|tribeClient
operator|.
name|prepareIndex
argument_list|(
literal|"test1"
argument_list|,
literal|"type2"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|tribeClient
operator|.
name|prepareIndex
argument_list|(
literal|"test2"
argument_list|,
literal|"type2"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|tribeClient
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareRefresh
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"verify they are there"
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|tribeClient
operator|.
name|prepareCount
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|4l
argument_list|)
expr_stmt|;
name|assertHitCount
argument_list|(
name|tribeClient
operator|.
name|prepareSearch
argument_list|()
operator|.
name|get
argument_list|()
argument_list|,
literal|4l
argument_list|)
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ClusterState
name|tribeState
init|=
name|tribeNode
operator|.
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
operator|.
name|getState
argument_list|()
decl_stmt|;
return|return
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type1"
argument_list|)
operator|!=
literal|null
operator|&&
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type2"
argument_list|)
operator|!=
literal|null
operator|&&
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type1"
argument_list|)
operator|!=
literal|null
operator|&&
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type2"
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"make sure master level write operations fail... (we don't really have a master)"
argument_list|)
expr_stmt|;
try|try
block|{
name|tribeClient
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"tribe_index"
argument_list|)
operator|.
name|setMasterNodeTimeout
argument_list|(
literal|"10ms"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MasterNotDiscoveredException
name|e
parameter_list|)
block|{
comment|// all is well!
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"delete an index, and make sure its reflected"
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test2"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ClusterState
name|tribeState
init|=
name|tribeNode
operator|.
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
operator|.
name|getState
argument_list|()
decl_stmt|;
return|return
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test1"
argument_list|)
operator|&&
operator|!
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test2"
argument_list|)
operator|&&
name|tribeState
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test1"
argument_list|)
operator|&&
operator|!
name|tribeState
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test2"
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"stop a node, make sure its reflected"
argument_list|)
expr_stmt|;
name|cluster2
operator|.
name|stopRandomNode
argument_list|()
expr_stmt|;
name|awaitSameNodeCounts
argument_list|()
expr_stmt|;
block|}
DECL|method|awaitIndicesInClusterState
specifier|private
name|void
name|awaitIndicesInClusterState
parameter_list|(
specifier|final
name|String
modifier|...
name|indices
parameter_list|)
throws|throws
name|Exception
block|{
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|ClusterState
name|tribeState
init|=
name|tribeNode
operator|.
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
operator|.
name|getState
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
if|if
condition|(
operator|!
name|tribeState
operator|.
name|getMetaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|tribeState
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|awaitSameNodeCounts
specifier|private
name|void
name|awaitSameNodeCounts
parameter_list|()
throws|throws
name|Exception
block|{
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|DiscoveryNodes
name|tribeNodes
init|=
name|tribeNode
operator|.
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
operator|.
name|getState
argument_list|()
operator|.
name|getNodes
argument_list|()
decl_stmt|;
return|return
name|countDataNodesForTribe
argument_list|(
literal|"t1"
argument_list|,
name|tribeNodes
argument_list|)
operator|==
name|cluster
argument_list|()
operator|.
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
operator|.
name|getState
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|&&
name|countDataNodesForTribe
argument_list|(
literal|"t2"
argument_list|,
name|tribeNodes
argument_list|)
operator|==
name|cluster2
operator|.
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
operator|.
name|getState
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|countDataNodesForTribe
specifier|private
name|int
name|countDataNodesForTribe
parameter_list|(
name|String
name|tribeName
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|dataNode
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|tribeName
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|get
argument_list|(
name|TribeService
operator|.
name|TRIBE_NAME
argument_list|)
argument_list|)
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

