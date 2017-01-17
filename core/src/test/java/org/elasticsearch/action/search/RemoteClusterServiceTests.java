begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|shards
operator|.
name|ClusterSearchShardsGroup
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
name|shards
operator|.
name|ClusterSearchShardsResponse
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
name|routing
operator|.
name|ShardIterator
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
name|ShardRouting
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
name|ShardRoutingState
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
name|TestShardRouting
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
name|ClusterSettings
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
name|transport
operator|.
name|TransportAddress
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
name|MatchAllQueryBuilder
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
name|TermsQueryBuilder
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
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|AliasFilter
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
name|ESTestCase
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
name|transport
operator|.
name|MockTransportService
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
name|TestThreadPool
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|TimeUnit
import|;
end_import

begin_class
DECL|class|RemoteClusterServiceTests
specifier|public
class|class
name|RemoteClusterServiceTests
extends|extends
name|ESTestCase
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|threadPool
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|startTransport
specifier|private
name|MockTransportService
name|startTransport
parameter_list|(
name|String
name|id
parameter_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|knownNodes
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
return|return
name|RemoteClusterConnectionTests
operator|.
name|startTransport
argument_list|(
name|id
argument_list|,
name|knownNodes
argument_list|,
name|version
argument_list|,
name|threadPool
argument_list|)
return|;
block|}
DECL|method|testSettingsAreRegistered
specifier|public
name|void
name|testSettingsAreRegistered
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
operator|.
name|contains
argument_list|(
name|RemoteClusterService
operator|.
name|REMOTE_CLUSTERS_SEEDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
operator|.
name|contains
argument_list|(
name|RemoteClusterService
operator|.
name|REMOTE_CONNECTIONS_PER_CLUSTER
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
operator|.
name|contains
argument_list|(
name|RemoteClusterService
operator|.
name|REMOTE_INITIAL_CONNECTION_TIMEOUT_SETTING
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
operator|.
name|contains
argument_list|(
name|RemoteClusterService
operator|.
name|REMOTE_NODE_ATTRIBUTE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRemoteClusterSeedSetting
specifier|public
name|void
name|testRemoteClusterSeedSetting
parameter_list|()
block|{
comment|// simple validation
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
literal|"search.remote.foo.seeds"
argument_list|,
literal|"192.168.0.1:8080"
argument_list|)
operator|.
name|put
argument_list|(
literal|"search.remote.bar.seed"
argument_list|,
literal|"[::1]:9090"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RemoteClusterService
operator|.
name|REMOTE_CLUSTERS_SEEDS
operator|.
name|getAllConcreteSettings
argument_list|(
name|settings
argument_list|)
operator|.
name|forEach
argument_list|(
name|setting
lambda|->
name|setting
operator|.
name|get
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|Settings
name|brokenSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"search.remote.foo.seeds"
argument_list|,
literal|"192.168.0.1"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|RemoteClusterService
operator|.
name|REMOTE_CLUSTERS_SEEDS
operator|.
name|getAllConcreteSettings
argument_list|(
name|brokenSettings
argument_list|)
operator|.
name|forEach
argument_list|(
name|setting
lambda|->
name|setting
operator|.
name|get
argument_list|(
name|brokenSettings
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBuiltRemoteClustersSeeds
specifier|public
name|void
name|testBuiltRemoteClustersSeeds
parameter_list|()
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
argument_list|>
name|map
init|=
name|RemoteClusterService
operator|.
name|buildRemoteClustersSeeds
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"search.remote.foo.seeds"
argument_list|,
literal|"192.168.0.1:8080"
argument_list|)
operator|.
name|put
argument_list|(
literal|"search.remote.bar.seeds"
argument_list|,
literal|"[::1]:9090"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|map
operator|.
name|containsKey
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|map
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|foo
init|=
name|map
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|foo
operator|.
name|getAddress
argument_list|()
argument_list|,
operator|new
name|TransportAddress
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"192.168.0.1"
argument_list|)
argument_list|,
literal|8080
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|foo
operator|.
name|getId
argument_list|()
argument_list|,
literal|"foo#192.168.0.1:8080"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|foo
operator|.
name|getVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|bar
init|=
name|map
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|bar
operator|.
name|getAddress
argument_list|()
argument_list|,
operator|new
name|TransportAddress
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
literal|"[::1]"
argument_list|)
argument_list|,
literal|9090
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bar
operator|.
name|getId
argument_list|()
argument_list|,
literal|"bar#[::1]:9090"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|bar
operator|.
name|getVersion
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testGroupClusterIndices
specifier|public
name|void
name|testGroupClusterIndices
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|knownNodes
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|MockTransportService
name|seedTransport
init|=
name|startTransport
argument_list|(
literal|"cluster_1_node"
argument_list|,
name|knownNodes
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
init|;
name|MockTransportService
name|otherSeedTransport
operator|=
name|startTransport
argument_list|(
literal|"cluster_2_node"
argument_list|,
name|knownNodes
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
init|)
block|{
name|DiscoveryNode
name|seedNode
init|=
name|seedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|otherSeedNode
init|=
name|otherSeedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
decl_stmt|;
name|knownNodes
operator|.
name|add
argument_list|(
name|seedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
argument_list|)
expr_stmt|;
name|knownNodes
operator|.
name|add
argument_list|(
name|otherSeedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|knownNodes
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|MockTransportService
name|transportService
init|=
name|MockTransportService
operator|.
name|createNewService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|threadPool
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|transportService
operator|.
name|start
argument_list|()
expr_stmt|;
name|transportService
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|putArray
argument_list|(
literal|"search.remote.cluster_1.seeds"
argument_list|,
name|seedNode
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|putArray
argument_list|(
literal|"search.remote.cluster_2.seeds"
argument_list|,
name|otherSeedNode
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|RemoteClusterService
name|service
init|=
operator|new
name|RemoteClusterService
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
name|transportService
argument_list|)
init|)
block|{
name|assertFalse
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|initializeRemoteClusters
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"cluster_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"cluster_2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|perClusterIndices
init|=
name|service
operator|.
name|groupClusterIndices
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo:bar"
block|,
literal|"cluster_1:bar"
block|,
literal|"cluster_2:foo:bar"
block|,
literal|"cluster_1:test"
block|,
literal|"cluster_2:foo*"
block|,
literal|"foo"
block|}
argument_list|,
name|i
lambda|->
literal|false
argument_list|)
decl_stmt|;
name|String
index|[]
name|localIndices
init|=
name|perClusterIndices
operator|.
name|computeIfAbsent
argument_list|(
name|RemoteClusterService
operator|.
name|LOCAL_CLUSTER_GROUP_KEY
argument_list|,
name|k
lambda|->
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|perClusterIndices
operator|.
name|remove
argument_list|(
name|RemoteClusterService
operator|.
name|LOCAL_CLUSTER_GROUP_KEY
argument_list|)
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo:bar"
block|,
literal|"foo"
block|}
argument_list|,
name|localIndices
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|perClusterIndices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"bar"
argument_list|,
literal|"test"
argument_list|)
argument_list|,
name|perClusterIndices
operator|.
name|get
argument_list|(
literal|"cluster_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"foo:bar"
argument_list|,
literal|"foo*"
argument_list|)
argument_list|,
name|perClusterIndices
operator|.
name|get
argument_list|(
literal|"cluster_2"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|iae
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|service
operator|.
name|groupClusterIndices
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"foo:bar"
block|,
literal|"cluster_1:bar"
block|,
literal|"cluster_2:foo:bar"
block|,
literal|"cluster_1:test"
block|,
literal|"cluster_2:foo*"
block|,
literal|"foo"
block|}
argument_list|,
name|i
lambda|->
literal|"cluster_1:bar"
operator|.
name|equals
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Can not filter indices; index cluster_1:bar exists but there is also a remote cluster named:"
operator|+
literal|" cluster_1 can't filter indices"
argument_list|,
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testIncrementallyAddClusters
specifier|public
name|void
name|testIncrementallyAddClusters
parameter_list|()
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|knownNodes
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|MockTransportService
name|seedTransport
init|=
name|startTransport
argument_list|(
literal|"cluster_1_node"
argument_list|,
name|knownNodes
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
init|;
name|MockTransportService
name|otherSeedTransport
operator|=
name|startTransport
argument_list|(
literal|"cluster_2_node"
argument_list|,
name|knownNodes
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
init|)
block|{
name|DiscoveryNode
name|seedNode
init|=
name|seedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|otherSeedNode
init|=
name|otherSeedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
decl_stmt|;
name|knownNodes
operator|.
name|add
argument_list|(
name|seedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
argument_list|)
expr_stmt|;
name|knownNodes
operator|.
name|add
argument_list|(
name|otherSeedTransport
operator|.
name|getLocalDiscoNode
argument_list|()
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|knownNodes
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|MockTransportService
name|transportService
init|=
name|MockTransportService
operator|.
name|createNewService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|threadPool
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|transportService
operator|.
name|start
argument_list|()
expr_stmt|;
name|transportService
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|putArray
argument_list|(
literal|"search.remote.cluster_1.seeds"
argument_list|,
name|seedNode
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|putArray
argument_list|(
literal|"search.remote.cluster_2.seeds"
argument_list|,
name|otherSeedNode
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
name|RemoteClusterService
name|service
init|=
operator|new
name|RemoteClusterService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|transportService
argument_list|)
init|)
block|{
name|assertFalse
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|initializeRemoteClusters
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|updateRemoteCluster
argument_list|(
literal|"cluster_1"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|seedNode
operator|.
name|getAddress
argument_list|()
operator|.
name|address
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"cluster_1"
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|updateRemoteCluster
argument_list|(
literal|"cluster_2"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|otherSeedNode
operator|.
name|getAddress
argument_list|()
operator|.
name|address
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"cluster_1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"cluster_2"
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|updateRemoteCluster
argument_list|(
literal|"cluster_2"
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|service
operator|.
name|isRemoteClusterRegistered
argument_list|(
literal|"cluster_2"
argument_list|)
argument_list|)
expr_stmt|;
name|IllegalArgumentException
name|iae
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|service
operator|.
name|updateRemoteCluster
argument_list|(
name|RemoteClusterService
operator|.
name|LOCAL_CLUSTER_GROUP_KEY
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"remote clusters must not have the empty string as its key"
argument_list|,
name|iae
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|testProcessRemoteShards
specifier|public
name|void
name|testProcessRemoteShards
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
name|RemoteClusterService
name|service
init|=
operator|new
name|RemoteClusterService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|)
init|)
block|{
name|assertFalse
argument_list|(
name|service
operator|.
name|isCrossClusterSearchEnabled
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ShardIterator
argument_list|>
name|iteratorList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|ClusterSearchShardsResponse
argument_list|>
name|searchShardsResponseMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|DiscoveryNode
index|[]
name|nodes
init|=
operator|new
name|DiscoveryNode
index|[]
block|{
operator|new
name|DiscoveryNode
argument_list|(
literal|"node1"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
block|,
operator|new
name|DiscoveryNode
argument_list|(
literal|"node2"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
block|}
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|indicesAndAliases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|indicesAndAliases
operator|.
name|put
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|AliasFilter
argument_list|(
operator|new
name|TermsQueryBuilder
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|indicesAndAliases
operator|.
name|put
argument_list|(
literal|"bar"
argument_list|,
operator|new
name|AliasFilter
argument_list|(
operator|new
name|MatchAllQueryBuilder
argument_list|()
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterSearchShardsGroup
index|[]
name|groups
init|=
operator|new
name|ClusterSearchShardsGroup
index|[]
block|{
operator|new
name|ClusterSearchShardsGroup
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_id"
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|ShardRouting
index|[]
block|{
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|"node1"
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
block|,
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|"node2"
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
block|}
argument_list|)
block|,
operator|new
name|ClusterSearchShardsGroup
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|"foo_id"
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|ShardRouting
index|[]
block|{
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"foo"
argument_list|,
literal|0
argument_list|,
literal|"node1"
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
block|,
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|,
literal|"node2"
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
block|}
argument_list|)
block|,
operator|new
name|ClusterSearchShardsGroup
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"bar"
argument_list|,
literal|"bar_id"
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|ShardRouting
index|[]
block|{
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|,
literal|"node2"
argument_list|,
literal|true
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
block|,
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"bar"
argument_list|,
literal|0
argument_list|,
literal|"node1"
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
block|}
argument_list|)
block|}
decl_stmt|;
name|searchShardsResponseMap
operator|.
name|put
argument_list|(
literal|"test_cluster_1"
argument_list|,
operator|new
name|ClusterSearchShardsResponse
argument_list|(
name|groups
argument_list|,
name|nodes
argument_list|,
name|indicesAndAliases
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|AliasFilter
argument_list|>
name|remoteAliases
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|service
operator|.
name|processRemoteShards
argument_list|(
name|searchShardsResponseMap
argument_list|,
name|iteratorList
argument_list|,
name|remoteAliases
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|iteratorList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardIterator
name|iterator
range|:
name|iteratorList
control|)
block|{
if|if
condition|(
name|iterator
operator|.
name|shardId
argument_list|()
operator|.
name|getIndexName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"foo"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|iterator
operator|.
name|shardId
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|0
operator|||
name|iterator
operator|.
name|shardId
argument_list|()
operator|.
name|getId
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test_cluster_1:foo"
argument_list|,
name|iterator
operator|.
name|shardId
argument_list|()
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|shardRouting
init|=
name|iterator
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|shardRouting
operator|=
name|iterator
operator|.
name|nextOrNull
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|iterator
operator|.
name|nextOrNull
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|iterator
operator|.
name|shardId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test_cluster_1:bar"
argument_list|,
name|iterator
operator|.
name|shardId
argument_list|()
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|shardRouting
init|=
name|iterator
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|shardRouting
operator|=
name|iterator
operator|.
name|nextOrNull
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|iterator
operator|.
name|nextOrNull
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|remoteAliases
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|remoteAliases
operator|.
name|toString
argument_list|()
argument_list|,
name|remoteAliases
operator|.
name|containsKey
argument_list|(
literal|"foo_id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|remoteAliases
operator|.
name|toString
argument_list|()
argument_list|,
name|remoteAliases
operator|.
name|containsKey
argument_list|(
literal|"bar_id"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TermsQueryBuilder
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
argument_list|,
name|remoteAliases
operator|.
name|get
argument_list|(
literal|"foo_id"
argument_list|)
operator|.
name|getQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|MatchAllQueryBuilder
argument_list|()
argument_list|,
name|remoteAliases
operator|.
name|get
argument_list|(
literal|"bar_id"
argument_list|)
operator|.
name|getQueryBuilder
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

