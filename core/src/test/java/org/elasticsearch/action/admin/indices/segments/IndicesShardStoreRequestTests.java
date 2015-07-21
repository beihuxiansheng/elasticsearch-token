begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.segments
package|package
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
name|segments
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|IntObjectCursor
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectCursor
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
name|base
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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
name|shards
operator|.
name|IndicesShardStoresResponse
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
name|routing
operator|.
name|*
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
name|collect
operator|.
name|ImmutableOpenIntMap
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
name|collect
operator|.
name|ImmutableOpenMap
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
name|index
operator|.
name|IndexService
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
name|shard
operator|.
name|IndexShard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|elasticsearch
operator|.
name|test
operator|.
name|store
operator|.
name|MockFSDirectoryService
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
name|*
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
name|*
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
DECL|class|IndicesShardStoreRequestTests
specifier|public
class|class
name|IndicesShardStoreRequestTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testEmpty
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|ensureGreen
argument_list|()
expr_stmt|;
name|IndicesShardStoresResponse
name|rsp
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
name|prepareShardStores
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|rsp
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|TestLogging
argument_list|(
literal|"action.admin.indices.shards:TRACE,cluster.service:TRACE"
argument_list|)
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|index
init|=
literal|"test"
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|ensureAtLeastNumDataNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandomData
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
name|index
argument_list|)
expr_stmt|;
comment|// no unallocated shards
name|IndicesShardStoresResponse
name|response
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
name|prepareShardStores
argument_list|(
name|index
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// all shards
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|shardStores
argument_list|(
name|Requests
operator|.
name|indicesShardStoresRequest
argument_list|(
name|index
argument_list|)
operator|.
name|shardStatuses
argument_list|(
literal|"all"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|containsKey
argument_list|(
name|index
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|ImmutableOpenIntMap
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStores
init|=
name|response
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shardStores
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStoreStatuses
range|:
name|shardStores
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|storeStatus
range|:
name|shardStoreStatuses
operator|.
name|value
control|)
block|{
name|assertThat
argument_list|(
name|storeStatus
operator|.
name|getVersion
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
operator|-
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|storeStatus
operator|.
name|getNode
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|storeStatus
operator|.
name|getStoreException
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// default with unassigned shards
name|ensureGreen
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> disable allocation"
argument_list|)
expr_stmt|;
name|disableAllocation
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stop random node"
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|(
operator|new
name|IndexNodePredicate
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|ensureYellow
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|ClusterState
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
operator|.
name|getState
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|unassignedShards
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|shardsWithState
argument_list|(
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|)
decl_stmt|;
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|shardStores
argument_list|(
name|Requests
operator|.
name|indicesShardStoresRequest
argument_list|(
name|index
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|response
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|containsKey
argument_list|(
name|index
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|ImmutableOpenIntMap
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStoresStatuses
init|=
name|response
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shardStoresStatuses
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|unassignedShards
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|IntObjectCursor
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|storesStatus
range|:
name|shardStoresStatuses
control|)
block|{
name|assertThat
argument_list|(
literal|"must report for one store"
argument_list|,
name|storesStatus
operator|.
name|value
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"reported store should be primary"
argument_list|,
name|storesStatus
operator|.
name|value
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAllocation
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
operator|.
name|Allocation
operator|.
name|PRIMARY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> enable allocation"
argument_list|)
expr_stmt|;
name|enableAllocation
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIndices
specifier|public
name|void
name|testIndices
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|index1
init|=
literal|"test1"
decl_stmt|;
name|String
name|index2
init|=
literal|"test2"
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|ensureAtLeastNumDataNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|index1
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|index2
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandomData
argument_list|(
name|index1
argument_list|)
expr_stmt|;
name|indexRandomData
argument_list|(
name|index2
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|IndicesShardStoresResponse
name|response
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
name|shardStores
argument_list|(
name|Requests
operator|.
name|indicesShardStoresRequest
argument_list|()
operator|.
name|shardStatuses
argument_list|(
literal|"all"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenIntMap
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
argument_list|>
name|shardStatuses
init|=
name|response
operator|.
name|getStoreStatuses
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|containsKey
argument_list|(
name|index1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|containsKey
argument_list|(
name|index2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|get
argument_list|(
name|index1
argument_list|)
operator|.
name|size
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
name|shardStatuses
operator|.
name|get
argument_list|(
name|index2
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// ensure index filtering works
name|response
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|shardStores
argument_list|(
name|Requests
operator|.
name|indicesShardStoresRequest
argument_list|(
name|index1
argument_list|)
operator|.
name|shardStatuses
argument_list|(
literal|"all"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|shardStatuses
operator|=
name|response
operator|.
name|getStoreStatuses
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|containsKey
argument_list|(
name|index1
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|containsKey
argument_list|(
name|index2
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|get
argument_list|(
name|index1
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorruptedShards
specifier|public
name|void
name|testCorruptedShards
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|index
init|=
literal|"test"
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|ensureAtLeastNumDataNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
name|index
argument_list|)
operator|.
name|setSettings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|"5"
argument_list|)
operator|.
name|put
argument_list|(
name|MockFSDirectoryService
operator|.
name|CHECK_INDEX_ON_CLOSE
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|indexRandomData
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> disable allocation"
argument_list|)
expr_stmt|;
name|disableAllocation
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> corrupt random shard copies"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|corruptedShardIDMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|internalCluster
argument_list|()
operator|.
name|nodesInclude
argument_list|(
name|index
argument_list|)
control|)
block|{
name|IndicesService
name|indexServices
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|IndexService
name|indexShards
init|=
name|indexServices
operator|.
name|indexServiceSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
for|for
control|(
name|Integer
name|shardId
range|:
name|indexShards
operator|.
name|shardIds
argument_list|()
control|)
block|{
name|IndexShard
name|shard
init|=
name|indexShards
operator|.
name|shardSafe
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|shard
operator|.
name|failShard
argument_list|(
literal|"test"
argument_list|,
operator|new
name|CorruptIndexException
argument_list|(
literal|"test corrupted"
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|corruptedShardIDMap
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodes
operator|==
literal|null
condition|)
block|{
name|nodes
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|corruptedShardIDMap
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|IndicesShardStoresResponse
name|rsp
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
name|prepareShardStores
argument_list|(
name|index
argument_list|)
operator|.
name|setShardStatuses
argument_list|(
literal|"all"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|ImmutableOpenIntMap
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStatuses
init|=
name|rsp
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|shardStatuses
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardStatuses
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|IntObjectCursor
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStatus
range|:
name|shardStatuses
control|)
block|{
for|for
control|(
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|status
range|:
name|shardStatus
operator|.
name|value
control|)
block|{
if|if
condition|(
name|corruptedShardIDMap
operator|.
name|containsKey
argument_list|(
name|shardStatus
operator|.
name|key
argument_list|)
operator|&&
name|corruptedShardIDMap
operator|.
name|get
argument_list|(
name|shardStatus
operator|.
name|key
argument_list|)
operator|.
name|contains
argument_list|(
name|status
operator|.
name|getNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|status
operator|.
name|getVersion
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|status
operator|.
name|getStoreException
argument_list|()
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|status
operator|.
name|getVersion
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|status
operator|.
name|getStoreException
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> enable allocation"
argument_list|)
expr_stmt|;
name|enableAllocation
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
DECL|method|indexRandomData
specifier|private
name|void
name|indexRandomData
parameter_list|(
name|String
name|index
parameter_list|)
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
name|int
name|numDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|IndexRequestBuilder
index|[]
name|builders
init|=
operator|new
name|IndexRequestBuilder
index|[
name|numDocs
index|]
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
name|builders
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builders
index|[
name|i
index|]
operator|=
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
name|index
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
block|}
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|builders
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
name|prepareFlush
argument_list|()
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
operator|.
name|setWaitIfOngoing
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
block|}
DECL|class|IndexNodePredicate
specifier|private
specifier|final
specifier|static
class|class
name|IndexNodePredicate
implements|implements
name|Predicate
argument_list|<
name|Settings
argument_list|>
block|{
DECL|field|nodesWithShard
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodesWithShard
decl_stmt|;
DECL|method|IndexNodePredicate
specifier|public
name|IndexNodePredicate
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|nodesWithShard
operator|=
name|findNodesWithShard
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|nodesWithShard
operator|.
name|contains
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|)
return|;
block|}
DECL|method|findNodesWithShard
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|findNodesWithShard
parameter_list|(
name|String
name|index
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
name|get
argument_list|()
operator|.
name|getState
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|startedShards
init|=
name|indexRoutingTable
operator|.
name|shardsWithState
argument_list|(
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|nodesWithShard
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardRouting
name|startedShard
range|:
name|startedShards
control|)
block|{
name|nodesWithShard
operator|.
name|add
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|startedShard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|nodesWithShard
return|;
block|}
block|}
block|}
end_class

end_unit

