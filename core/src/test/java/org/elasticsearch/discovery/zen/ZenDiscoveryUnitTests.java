begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

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
name|support
operator|.
name|replication
operator|.
name|ClusterStateCreationUtils
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
name|ClusterChangedEvent
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
name|ClusterName
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
name|DiscoveryNode
operator|.
name|Role
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
name|cluster
operator|.
name|service
operator|.
name|ClusterService
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
name|discovery
operator|.
name|Discovery
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
name|zen
operator|.
name|ping
operator|.
name|ZenPing
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
name|zen
operator|.
name|ping
operator|.
name|ZenPingService
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
name|zen
operator|.
name|publish
operator|.
name|PublishClusterStateActionTests
operator|.
name|AssertingAckListener
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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|HashSet
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
name|Set
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
name|emptySet
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|ElectMasterService
operator|.
name|DISCOVERY_ZEN_MINIMUM_MASTER_NODES_SETTING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|ZenDiscovery
operator|.
name|shouldIgnoreOrRejectNewClusterState
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
name|ClusterServiceUtils
operator|.
name|createClusterService
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
name|ClusterServiceUtils
operator|.
name|setState
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
name|containsString
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

begin_class
DECL|class|ZenDiscoveryUnitTests
specifier|public
class|class
name|ZenDiscoveryUnitTests
extends|extends
name|ESTestCase
block|{
DECL|method|testShouldIgnoreNewClusterState
specifier|public
name|void
name|testShouldIgnoreNewClusterState
parameter_list|()
block|{
name|ClusterName
name|clusterName
init|=
operator|new
name|ClusterName
argument_list|(
literal|"abc"
argument_list|)
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|currentNodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|currentNodes
operator|.
name|masterNodeId
argument_list|(
literal|"a"
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"a"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|newNodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
name|newNodes
operator|.
name|masterNodeId
argument_list|(
literal|"a"
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"a"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
name|currentState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
name|currentState
operator|.
name|nodes
argument_list|(
name|currentNodes
argument_list|)
expr_stmt|;
name|ClusterState
operator|.
name|Builder
name|newState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
name|newState
operator|.
name|nodes
argument_list|(
name|newNodes
argument_list|)
expr_stmt|;
name|currentState
operator|.
name|version
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should ignore, because new state's version is lower to current state's version"
argument_list|,
name|shouldIgnoreOrRejectNewClusterState
argument_list|(
name|logger
argument_list|,
name|currentState
operator|.
name|build
argument_list|()
argument_list|,
name|newState
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|currentState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"should ignore, because new state's version is equal to current state's version"
argument_list|,
name|shouldIgnoreOrRejectNewClusterState
argument_list|(
name|logger
argument_list|,
name|currentState
operator|.
name|build
argument_list|()
argument_list|,
name|newState
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|currentState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"should not ignore, because new state's version is higher to current state's version"
argument_list|,
name|shouldIgnoreOrRejectNewClusterState
argument_list|(
name|logger
argument_list|,
name|currentState
operator|.
name|build
argument_list|()
argument_list|,
name|newState
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|currentNodes
operator|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
expr_stmt|;
name|currentNodes
operator|.
name|masterNodeId
argument_list|(
literal|"b"
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"b"
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|emptyMap
argument_list|()
argument_list|,
name|emptySet
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
expr_stmt|;
comment|// version isn't taken into account, so randomize it to ensure this.
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|currentState
operator|.
name|version
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|currentState
operator|.
name|nodes
argument_list|(
name|currentNodes
argument_list|)
expr_stmt|;
try|try
block|{
name|shouldIgnoreOrRejectNewClusterState
argument_list|(
name|logger
argument_list|,
name|currentState
operator|.
name|build
argument_list|()
argument_list|,
name|newState
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should ignore, because current state's master is not equal to new state's master"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"cluster state from a different master than the current one, rejecting"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|currentNodes
operator|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
expr_stmt|;
name|currentNodes
operator|.
name|masterNodeId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|currentState
operator|.
name|nodes
argument_list|(
name|currentNodes
argument_list|)
expr_stmt|;
comment|// version isn't taken into account, so randomize it to ensure this.
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|currentState
operator|.
name|version
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentState
operator|.
name|version
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|newState
operator|.
name|version
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
literal|"should not ignore, because current state doesn't have a master"
argument_list|,
name|shouldIgnoreOrRejectNewClusterState
argument_list|(
name|logger
argument_list|,
name|currentState
operator|.
name|build
argument_list|()
argument_list|,
name|newState
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFilterNonMasterPingResponse
specifier|public
name|void
name|testFilterNonMasterPingResponse
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ZenPing
operator|.
name|PingResponse
argument_list|>
name|responses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|DiscoveryNode
argument_list|>
name|masterNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|DiscoveryNode
argument_list|>
name|allNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Set
argument_list|<
name|Role
argument_list|>
name|roles
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Role
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_"
operator|+
name|i
argument_list|,
literal|"id_"
operator|+
name|i
argument_list|,
name|buildNewFakeTransportAddress
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|roles
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|responses
operator|.
name|add
argument_list|(
operator|new
name|ZenPing
operator|.
name|PingResponse
argument_list|(
name|node
argument_list|,
name|randomBoolean
argument_list|()
condition|?
literal|null
else|:
name|node
argument_list|,
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|randomLong
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|allNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|isMasterNode
argument_list|()
condition|)
block|{
name|masterNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|ignore
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ZenPing
operator|.
name|PingResponse
argument_list|>
name|filtered
init|=
name|ZenDiscovery
operator|.
name|filterPingResponses
argument_list|(
name|responses
argument_list|,
name|ignore
argument_list|,
name|logger
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|filteredNodes
init|=
name|filtered
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ZenPing
operator|.
name|PingResponse
operator|::
name|node
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|ignore
condition|)
block|{
name|assertThat
argument_list|(
name|filteredNodes
argument_list|,
name|equalTo
argument_list|(
name|masterNodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|filteredNodes
argument_list|,
name|equalTo
argument_list|(
name|allNodes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testNodesUpdatedAfterClusterStatePublished
specifier|public
name|void
name|testNodesUpdatedAfterClusterStatePublished
parameter_list|()
throws|throws
name|Exception
block|{
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
comment|// randomly make minimum_master_nodes a value higher than we have nodes for, so it will force failure
name|int
name|minMasterNodes
init|=
name|randomBoolean
argument_list|()
condition|?
literal|3
else|:
literal|1
decl_stmt|;
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
name|DISCOVERY_ZEN_MINIMUM_MASTER_NODES_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|minMasterNodes
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|Closeable
argument_list|>
name|toClose
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|expectedFDNodes
init|=
literal|null
decl_stmt|;
specifier|final
name|MockTransportService
name|masterTransport
init|=
name|MockTransportService
operator|.
name|createNewService
argument_list|(
name|settings
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|threadPool
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|masterTransport
operator|.
name|start
argument_list|()
expr_stmt|;
name|DiscoveryNode
name|masterNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"master"
argument_list|,
name|masterTransport
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|masterTransport
argument_list|)
expr_stmt|;
name|masterTransport
operator|.
name|setLocalNode
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
name|ClusterState
name|state
init|=
name|ClusterStateCreationUtils
operator|.
name|state
argument_list|(
name|masterNode
argument_list|,
name|masterNode
argument_list|,
name|masterNode
argument_list|)
decl_stmt|;
comment|// build the zen discovery and cluster service
name|ClusterService
name|masterClusterService
init|=
name|createClusterService
argument_list|(
name|threadPool
argument_list|,
name|masterNode
argument_list|)
decl_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|masterClusterService
argument_list|)
expr_stmt|;
comment|// TODO: clustername shouldn't be stored twice in cluster service, but for now, work around it
name|state
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|masterClusterService
operator|.
name|getClusterName
argument_list|()
argument_list|)
operator|.
name|nodes
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|setState
argument_list|(
name|masterClusterService
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|ZenDiscovery
name|masterZen
init|=
name|buildZenDiscovery
argument_list|(
name|settings
argument_list|,
name|masterTransport
argument_list|,
name|masterClusterService
argument_list|,
name|threadPool
argument_list|)
decl_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|masterZen
argument_list|)
expr_stmt|;
name|masterTransport
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
specifier|final
name|MockTransportService
name|otherTransport
init|=
name|MockTransportService
operator|.
name|createNewService
argument_list|(
name|settings
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|threadPool
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|otherTransport
operator|.
name|start
argument_list|()
expr_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|otherTransport
argument_list|)
expr_stmt|;
name|DiscoveryNode
name|otherNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"other"
argument_list|,
name|otherTransport
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
name|otherTransport
operator|.
name|setLocalNode
argument_list|(
name|otherNode
argument_list|)
expr_stmt|;
specifier|final
name|ClusterState
name|otherState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|masterClusterService
operator|.
name|getClusterName
argument_list|()
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|otherNode
argument_list|)
operator|.
name|localNodeId
argument_list|(
name|otherNode
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterService
name|otherClusterService
init|=
name|createClusterService
argument_list|(
name|threadPool
argument_list|,
name|masterNode
argument_list|)
decl_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|otherClusterService
argument_list|)
expr_stmt|;
name|setState
argument_list|(
name|otherClusterService
argument_list|,
name|otherState
argument_list|)
expr_stmt|;
name|ZenDiscovery
name|otherZen
init|=
name|buildZenDiscovery
argument_list|(
name|settings
argument_list|,
name|otherTransport
argument_list|,
name|otherClusterService
argument_list|,
name|threadPool
argument_list|)
decl_stmt|;
name|toClose
operator|.
name|add
argument_list|(
name|otherZen
argument_list|)
expr_stmt|;
name|otherTransport
operator|.
name|acceptIncomingRequests
argument_list|()
expr_stmt|;
name|masterTransport
operator|.
name|connectToNode
argument_list|(
name|otherNode
argument_list|)
expr_stmt|;
name|otherTransport
operator|.
name|connectToNode
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
comment|// a new cluster state with a new discovery node (we will test if the cluster state
comment|// was updated by the presence of this node in NodesFaultDetection)
name|ClusterState
name|newState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|masterClusterService
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|incrementVersion
argument_list|()
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|state
operator|.
name|nodes
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|otherNode
argument_list|)
operator|.
name|masterNodeId
argument_list|(
name|masterNode
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
comment|// publishing a new cluster state
name|ClusterChangedEvent
name|clusterChangedEvent
init|=
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"testing"
argument_list|,
name|newState
argument_list|,
name|state
argument_list|)
decl_stmt|;
name|AssertingAckListener
name|listener
init|=
operator|new
name|AssertingAckListener
argument_list|(
name|newState
operator|.
name|nodes
argument_list|()
operator|.
name|getSize
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|expectedFDNodes
operator|=
name|masterZen
operator|.
name|getFaultDetectionNodes
argument_list|()
expr_stmt|;
name|masterZen
operator|.
name|publish
argument_list|(
name|clusterChangedEvent
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|await
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
expr_stmt|;
comment|// publish was a success, update expected FD nodes based on new cluster state
name|expectedFDNodes
operator|=
name|fdNodesForState
argument_list|(
name|newState
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Discovery
operator|.
name|FailedToCommitClusterStateException
name|e
parameter_list|)
block|{
comment|// not successful, so expectedFDNodes above should remain what it was originally assigned
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|minMasterNodes
argument_list|)
expr_stmt|;
comment|// ensure min master nodes is the higher value, otherwise we shouldn't fail
block|}
name|assertEquals
argument_list|(
name|expectedFDNodes
argument_list|,
name|masterZen
operator|.
name|getFaultDetectionNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|toClose
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|buildZenDiscovery
specifier|private
name|ZenDiscovery
name|buildZenDiscovery
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|service
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|ClusterSettings
name|clusterSettings
init|=
operator|new
name|ClusterSettings
argument_list|(
name|settings
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
decl_stmt|;
name|ZenPingService
name|zenPingService
init|=
operator|new
name|ZenPingService
argument_list|(
name|settings
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|ZenDiscovery
name|zenDiscovery
init|=
operator|new
name|ZenDiscovery
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|service
argument_list|,
name|clusterService
argument_list|,
name|clusterSettings
argument_list|,
name|zenPingService
argument_list|)
decl_stmt|;
name|zenDiscovery
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
name|zenDiscovery
return|;
block|}
DECL|method|fdNodesForState
specifier|private
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|fdNodesForState
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|DiscoveryNode
name|localNode
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoveryNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|clusterState
operator|.
name|getNodes
argument_list|()
operator|.
name|getNodes
argument_list|()
operator|.
name|valuesIt
argument_list|()
operator|.
name|forEachRemaining
argument_list|(
name|discoveryNode
lambda|->
block|{
comment|// the local node isn't part of the nodes that are pinged (don't ping ourselves)
if|if
condition|(
name|discoveryNode
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|localNode
operator|.
name|getId
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
name|discoveryNodes
operator|.
name|add
argument_list|(
name|discoveryNode
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|discoveryNodes
return|;
block|}
block|}
end_class

end_unit

