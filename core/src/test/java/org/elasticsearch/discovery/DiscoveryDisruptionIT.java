begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
package|;
end_package

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
name|zen
operator|.
name|MembershipAction
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
name|PublishClusterStateAction
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
name|UnicastZenPing
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
name|ZenPing
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
name|elasticsearch
operator|.
name|test
operator|.
name|discovery
operator|.
name|TestZenDiscovery
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
name|disruption
operator|.
name|NetworkDisruption
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
name|disruption
operator|.
name|NetworkDisruption
operator|.
name|NetworkDisconnect
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
name|disruption
operator|.
name|NetworkDisruption
operator|.
name|TwoPartitions
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
name|disruption
operator|.
name|ServiceDisruptionScheme
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
name|disruption
operator|.
name|SlowClusterStateProcessing
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
name|transport
operator|.
name|ConnectionProfile
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
name|Transport
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
name|TransportRequest
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
name|TransportRequestOptions
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
name|IOException
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
name|CountDownLatch
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|INDEX_NUMBER_OF_REPLICAS_SETTING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|INDEX_NUMBER_OF_SHARDS_SETTING
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
name|greaterThanOrEqualTo
import|;
end_import

begin_comment
comment|/**  * Tests for discovery during disruptions.  */
end_comment

begin_class
annotation|@
name|ESIntegTestCase
operator|.
name|ClusterScope
argument_list|(
name|scope
operator|=
name|ESIntegTestCase
operator|.
name|Scope
operator|.
name|TEST
argument_list|,
name|numDataNodes
operator|=
literal|0
argument_list|,
name|transportClientRatio
operator|=
literal|0
argument_list|,
name|autoMinMasterNodes
operator|=
literal|false
argument_list|)
annotation|@
name|TestLogging
argument_list|(
literal|"_root:DEBUG,org.elasticsearch.cluster.service:TRACE"
argument_list|)
DECL|class|DiscoveryDisruptionIT
specifier|public
class|class
name|DiscoveryDisruptionIT
extends|extends
name|AbstractDisruptionTestCase
block|{
DECL|method|testIsolatedUnicastNodes
specifier|public
name|void
name|testIsolatedUnicastNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|startCluster
argument_list|(
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
decl_stmt|;
comment|// Figure out what is the elected master node
specifier|final
name|String
name|unicastTarget
init|=
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|unicastTargetSide
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|unicastTargetSide
operator|.
name|add
argument_list|(
name|unicastTarget
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|restOfClusterSide
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|restOfClusterSide
operator|.
name|addAll
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|restOfClusterSide
operator|.
name|remove
argument_list|(
name|unicastTarget
argument_list|)
expr_stmt|;
comment|// Forcefully clean temporal response lists on all nodes. Otherwise the node in the unicast host list
comment|// includes all the other nodes that have pinged it and the issue doesn't manifest
name|ZenPing
name|zenPing
init|=
operator|(
operator|(
name|TestZenDiscovery
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Discovery
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getZenPing
argument_list|()
decl_stmt|;
if|if
condition|(
name|zenPing
operator|instanceof
name|UnicastZenPing
condition|)
block|{
operator|(
operator|(
name|UnicastZenPing
operator|)
name|zenPing
operator|)
operator|.
name|clearTemporalResponses
argument_list|()
expr_stmt|;
block|}
comment|// Simulate a network issue between the unicast target node and the rest of the cluster
name|NetworkDisruption
name|networkDisconnect
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|TwoPartitions
argument_list|(
name|unicastTargetSide
argument_list|,
name|restOfClusterSide
argument_list|)
argument_list|,
operator|new
name|NetworkDisconnect
argument_list|()
argument_list|)
decl_stmt|;
name|setDisruptionScheme
argument_list|(
name|networkDisconnect
argument_list|)
expr_stmt|;
name|networkDisconnect
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
comment|// Wait until elected master has removed that the unlucky node...
name|ensureStableCluster
argument_list|(
literal|3
argument_list|,
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// The isolate master node must report no master, so it starts with pinging
name|assertNoMaster
argument_list|(
name|unicastTarget
argument_list|)
expr_stmt|;
name|networkDisconnect
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
comment|// Wait until the master node sees all 3 nodes again.
name|ensureStableCluster
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
comment|/**      * A 4 node cluster with m_m_n set to 3 and each node has one unicast endpoint. One node partitions from the master node.      * The temporal unicast responses is empty. When partition is solved the one ping response contains a master node.      * The rejoining node should take this master node and connect.      */
DECL|method|testUnicastSinglePingResponseContainsMaster
specifier|public
name|void
name|testUnicastSinglePingResponseContainsMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|startCluster
argument_list|(
literal|4
argument_list|,
operator|-
literal|1
argument_list|,
operator|new
name|int
index|[]
block|{
literal|0
block|}
argument_list|)
decl_stmt|;
comment|// Figure out what is the elected master node
specifier|final
name|String
name|masterNode
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"---> legit elected master node={}"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|otherNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|otherNodes
operator|.
name|remove
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
name|otherNodes
operator|.
name|remove
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//<-- Don't isolate the node that is in the unicast endpoint for all the other nodes.
specifier|final
name|String
name|isolatedNode
init|=
name|otherNodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Forcefully clean temporal response lists on all nodes. Otherwise the node in the unicast host list
comment|// includes all the other nodes that have pinged it and the issue doesn't manifest
name|ZenPing
name|zenPing
init|=
operator|(
operator|(
name|TestZenDiscovery
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Discovery
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getZenPing
argument_list|()
decl_stmt|;
if|if
condition|(
name|zenPing
operator|instanceof
name|UnicastZenPing
condition|)
block|{
operator|(
operator|(
name|UnicastZenPing
operator|)
name|zenPing
operator|)
operator|.
name|clearTemporalResponses
argument_list|()
expr_stmt|;
block|}
comment|// Simulate a network issue between the unlucky node and elected master node in both directions.
name|NetworkDisruption
name|networkDisconnect
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|TwoPartitions
argument_list|(
name|masterNode
argument_list|,
name|isolatedNode
argument_list|)
argument_list|,
operator|new
name|NetworkDisconnect
argument_list|()
argument_list|)
decl_stmt|;
name|setDisruptionScheme
argument_list|(
name|networkDisconnect
argument_list|)
expr_stmt|;
name|networkDisconnect
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
comment|// Wait until elected master has removed that the unlucky node...
name|ensureStableCluster
argument_list|(
literal|3
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
comment|// The isolate master node must report no master, so it starts with pinging
name|assertNoMaster
argument_list|(
name|isolatedNode
argument_list|)
expr_stmt|;
name|networkDisconnect
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
comment|// Wait until the master node sees all 4 nodes again.
name|ensureStableCluster
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// The elected master shouldn't have changed, since the isolated node never could have elected himself as
comment|// master since m_m_n of 3 could never be satisfied.
name|assertMaster
argument_list|(
name|masterNode
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test cluster join with issues in cluster state publishing *      */
DECL|method|testClusterJoinDespiteOfPublishingIssues
specifier|public
name|void
name|testClusterJoinDespiteOfPublishingIssues
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|String
argument_list|>
name|nodes
init|=
name|startCluster
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|masterNode
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|String
name|nonMasterNode
decl_stmt|;
if|if
condition|(
name|masterNode
operator|.
name|equals
argument_list|(
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|)
block|{
name|nonMasterNode
operator|=
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nonMasterNode
operator|=
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|DiscoveryNodes
name|discoveryNodes
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|,
name|nonMasterNode
argument_list|)
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|TransportService
name|masterTranspotService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|discoveryNodes
operator|.
name|getMasterNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"blocking requests from non master [{}] to master [{}]"
argument_list|,
name|nonMasterNode
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|MockTransportService
name|nonMasterTransportService
init|=
operator|(
name|MockTransportService
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|nonMasterNode
argument_list|)
decl_stmt|;
name|nonMasterTransportService
operator|.
name|addFailToSendNoConnectRule
argument_list|(
name|masterTranspotService
argument_list|)
expr_stmt|;
name|assertNoMaster
argument_list|(
name|nonMasterNode
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"blocking cluster state publishing from master [{}] to non master [{}]"
argument_list|,
name|masterNode
argument_list|,
name|nonMasterNode
argument_list|)
expr_stmt|;
name|MockTransportService
name|masterTransportService
init|=
operator|(
name|MockTransportService
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|masterNode
argument_list|)
decl_stmt|;
name|TransportService
name|localTransportService
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|discoveryNodes
operator|.
name|getLocalNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|masterTransportService
operator|.
name|addFailToSendNoConnectRule
argument_list|(
name|localTransportService
argument_list|,
name|PublishClusterStateAction
operator|.
name|SEND_ACTION_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|masterTransportService
operator|.
name|addFailToSendNoConnectRule
argument_list|(
name|localTransportService
argument_list|,
name|PublishClusterStateAction
operator|.
name|COMMIT_ACTION_NAME
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"allowing requests from non master [{}] to master [{}], waiting for two join request"
argument_list|,
name|nonMasterNode
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|countDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|nonMasterTransportService
operator|.
name|addDelegate
argument_list|(
name|masterTranspotService
argument_list|,
operator|new
name|MockTransportService
operator|.
name|DelegateTransport
argument_list|(
name|nonMasterTransportService
operator|.
name|original
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|void
name|sendRequest
parameter_list|(
name|Transport
operator|.
name|Connection
name|connection
parameter_list|,
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|,
name|TransportRequest
name|request
parameter_list|,
name|TransportRequestOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|action
operator|.
name|equals
argument_list|(
name|MembershipAction
operator|.
name|DISCOVERY_JOIN_ACTION_NAME
argument_list|)
condition|)
block|{
name|countDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|sendRequest
argument_list|(
name|connection
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Transport
operator|.
name|Connection
name|openConnection
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ConnectionProfile
name|profile
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|openConnection
argument_list|(
name|node
argument_list|,
name|profile
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|countDownLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for cluster to reform"
argument_list|)
expr_stmt|;
name|masterTransportService
operator|.
name|clearRule
argument_list|(
name|localTransportService
argument_list|)
expr_stmt|;
name|nonMasterTransportService
operator|.
name|clearRule
argument_list|(
name|localTransportService
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|// shutting down the nodes, to avoid the leakage check tripping
comment|// on the states associated with the commit requests we may have dropped
name|internalCluster
argument_list|()
operator|.
name|stopRandomNonMasterNode
argument_list|()
expr_stmt|;
block|}
DECL|method|testClusterFormingWithASlowNode
specifier|public
name|void
name|testClusterFormingWithASlowNode
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|3
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|SlowClusterStateProcessing
name|disruption
init|=
operator|new
name|SlowClusterStateProcessing
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|1000
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
comment|// don't wait for initial state, we want to add the disruption while the cluster is forming
name|internalCluster
argument_list|()
operator|.
name|startNodes
argument_list|(
literal|3
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|DiscoverySettings
operator|.
name|PUBLISH_TIMEOUT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"3s"
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
literal|"applying disruption while cluster is forming ..."
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|disruption
argument_list|)
expr_stmt|;
name|disruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
DECL|method|testElectMasterWithLatestVersion
specifier|public
name|void
name|testElectMasterWithLatestVersion
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCluster
argument_list|(
literal|3
argument_list|,
literal|null
argument_list|,
literal|2
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|startNodes
argument_list|(
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|ensureStableCluster
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ServiceDisruptionScheme
name|isolateAllNodes
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|NetworkDisruption
operator|.
name|IsolateAllNodes
argument_list|(
name|nodes
argument_list|)
argument_list|,
operator|new
name|NetworkDisconnect
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|isolateAllNodes
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> forcing a complete election to make sure \"preferred\" master is elected"
argument_list|)
expr_stmt|;
name|isolateAllNodes
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|assertNoMaster
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|internalCluster
argument_list|()
operator|.
name|clearDisruptionScheme
argument_list|()
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|3
argument_list|)
expr_stmt|;
specifier|final
name|String
name|preferredMasterName
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNode
name|preferredMaster
init|=
name|internalCluster
argument_list|()
operator|.
name|clusterService
argument_list|(
name|preferredMasterName
argument_list|)
operator|.
name|localNode
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|DiscoveryNode
name|discoveryNode
init|=
name|internalCluster
argument_list|()
operator|.
name|clusterService
argument_list|(
name|node
argument_list|)
operator|.
name|localNode
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|discoveryNode
operator|.
name|getId
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
name|preferredMaster
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> preferred master is {}"
argument_list|,
name|preferredMaster
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nonPreferredNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|nonPreferredNodes
operator|.
name|remove
argument_list|(
name|preferredMasterName
argument_list|)
expr_stmt|;
specifier|final
name|ServiceDisruptionScheme
name|isolatePreferredMaster
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|NetworkDisruption
operator|.
name|TwoPartitions
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|preferredMasterName
argument_list|)
argument_list|,
name|nonPreferredNodes
argument_list|)
argument_list|,
operator|new
name|NetworkDisconnect
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|isolatePreferredMaster
argument_list|)
expr_stmt|;
name|isolatePreferredMaster
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|(
name|randomFrom
argument_list|(
name|nonPreferredNodes
argument_list|)
argument_list|)
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
name|INDEX_NUMBER_OF_SHARDS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|1
argument_list|,
name|INDEX_NUMBER_OF_REPLICAS_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|clearDisruptionScheme
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|isolateAllNodes
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> forcing a complete election again"
argument_list|)
expr_stmt|;
name|isolateAllNodes
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|node
range|:
name|nodes
control|)
block|{
name|assertNoMaster
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|isolateAllNodes
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
specifier|final
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
if|if
condition|(
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|hasIndex
argument_list|(
literal|"test"
argument_list|)
operator|==
literal|false
condition|)
block|{
name|fail
argument_list|(
literal|"index 'test' was lost. current cluster state: "
operator|+
name|state
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Adds an asymmetric break between a master and one of the nodes and makes      * sure that the node is removed form the cluster, that the node start pinging and that      * the cluster reforms when healed.      */
DECL|method|testNodeNotReachableFromMaster
specifier|public
name|void
name|testNodeNotReachableFromMaster
parameter_list|()
throws|throws
name|Exception
block|{
name|startCluster
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|String
name|masterNode
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|String
name|nonMasterNode
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|nonMasterNode
operator|==
literal|null
condition|)
block|{
name|nonMasterNode
operator|=
name|randomFrom
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nonMasterNode
operator|.
name|equals
argument_list|(
name|masterNode
argument_list|)
condition|)
block|{
name|nonMasterNode
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"blocking request from master [{}] to [{}]"
argument_list|,
name|masterNode
argument_list|,
name|nonMasterNode
argument_list|)
expr_stmt|;
name|MockTransportService
name|masterTransportService
init|=
operator|(
name|MockTransportService
operator|)
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|masterNode
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|masterTransportService
operator|.
name|addUnresponsiveRule
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|nonMasterNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|masterTransportService
operator|.
name|addFailToSendNoConnectRule
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|nonMasterNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for [{}] to be removed from cluster"
argument_list|,
name|nonMasterNode
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|2
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"waiting for [{}] to have no master"
argument_list|,
name|nonMasterNode
argument_list|)
expr_stmt|;
name|assertNoMaster
argument_list|(
name|nonMasterNode
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"healing partition and checking cluster reforms"
argument_list|)
expr_stmt|;
name|masterTransportService
operator|.
name|clearAllRules
argument_list|()
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

