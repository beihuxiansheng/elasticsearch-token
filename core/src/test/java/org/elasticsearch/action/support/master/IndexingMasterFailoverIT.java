begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.action.support.master
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|zen
operator|.
name|ElectMasterService
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
name|FaultDetection
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
name|transport
operator|.
name|MockTransportService
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
name|HashSet
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
name|BrokenBarrierException
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
name|CyclicBarrier
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
argument_list|)
DECL|class|IndexingMasterFailoverIT
specifier|public
class|class
name|IndexingMasterFailoverIT
extends|extends
name|ESIntegTestCase
block|{
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
specifier|final
name|HashSet
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|classes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|super
operator|.
name|nodePlugins
argument_list|()
argument_list|)
decl_stmt|;
name|classes
operator|.
name|add
argument_list|(
name|MockTransportService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|classes
return|;
block|}
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
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
name|nodeSettings
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|TestZenDiscovery
operator|.
name|USE_MOCK_PINGS
operator|.
name|getKey
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**      * Indexing operations which entail mapping changes require a blocking request to the master node to update the mapping.      * If the master node is being disrupted or if it cannot commit cluster state changes, it needs to retry within timeout limits.      * This retry logic is implemented in TransportMasterNodeAction and tested by the following master failover scenario.      */
DECL|method|testMasterFailoverDuringIndexingWithMappingChanges
specifier|public
name|void
name|testMasterFailoverDuringIndexingWithMappingChanges
parameter_list|()
throws|throws
name|Throwable
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> start 4 nodes, 3 master, 1 data"
argument_list|)
expr_stmt|;
specifier|final
name|Settings
name|sharedSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|FaultDetection
operator|.
name|PING_TIMEOUT_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"1s"
argument_list|)
comment|// for hitting simulated network failures quickly
operator|.
name|put
argument_list|(
name|FaultDetection
operator|.
name|PING_RETRIES_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"1"
argument_list|)
comment|// for hitting simulated network failures quickly
operator|.
name|put
argument_list|(
literal|"discovery.zen.join_timeout"
argument_list|,
literal|"10s"
argument_list|)
comment|// still long to induce failures but to long so test won't time out
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
literal|"1s"
argument_list|)
comment|//<-- for hitting simulated network failures quickly
operator|.
name|put
argument_list|(
name|ElectMasterService
operator|.
name|DISCOVERY_ZEN_MINIMUM_MASTER_NODES_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|startMasterOnlyNodesAsync
argument_list|(
literal|3
argument_list|,
name|sharedSettings
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|dataNode
init|=
name|internalCluster
argument_list|()
operator|.
name|startDataOnlyNode
argument_list|(
name|sharedSettings
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> wait for all nodes to join the cluster"
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|4
argument_list|)
expr_stmt|;
comment|// We index data with mapping changes into cluster and have master failover at same time
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
literal|"myindex"
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
literal|"index.number_of_shards"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.number_of_replicas"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"myindex"
argument_list|)
expr_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|Thread
name|indexingThread
init|=
operator|new
name|Thread
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
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Barrier interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|BrokenBarrierException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Broken barrier"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// index data with mapping changes
name|IndexResponse
name|response
init|=
name|client
argument_list|(
name|dataNode
argument_list|)
operator|.
name|prepareIndex
argument_list|(
literal|"myindex"
argument_list|,
literal|"mytype"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field_"
operator|+
name|i
argument_list|,
literal|"val"
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
name|CREATED
argument_list|,
name|response
operator|.
name|getResult
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|indexingThread
operator|.
name|setName
argument_list|(
literal|"indexingThread"
argument_list|)
expr_stmt|;
name|indexingThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// interrupt communication between master and other nodes in cluster
name|String
name|master
init|=
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|otherNodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|otherNodes
operator|.
name|remove
argument_list|(
name|master
argument_list|)
expr_stmt|;
name|NetworkDisruption
name|partition
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|TwoPartitions
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|master
argument_list|)
argument_list|,
name|otherNodes
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
name|partition
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> disrupting network"
argument_list|)
expr_stmt|;
name|partition
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting for new master to be elected"
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|3
argument_list|,
name|dataNode
argument_list|)
expr_stmt|;
name|partition
operator|.
name|stopDisrupting
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> waiting to heal"
argument_list|)
expr_stmt|;
name|ensureStableCluster
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|indexingThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"myindex"
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"myindex"
argument_list|)
operator|.
name|get
argument_list|()
operator|.
name|getHits
argument_list|()
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|10L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

