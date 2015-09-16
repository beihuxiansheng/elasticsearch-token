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
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|ActionModule
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
name|ActionRequest
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
name|ActionResponse
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsAction
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
name|support
operator|.
name|ActionFilter
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
name|ActionFilters
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
name|RoutingNodes
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
name|allocation
operator|.
name|decider
operator|.
name|EnableAllocationDecider
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
name|inject
operator|.
name|Inject
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
name|index
operator|.
name|store
operator|.
name|Store
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
name|InternalTestCluster
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
name|TransportException
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
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|Collection
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
name|ExecutionException
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
name|atomic
operator|.
name|AtomicBoolean
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
name|Settings
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
name|CoreMatchers
operator|.
name|equalTo
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
name|greaterThan
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
comment|/**  * Integration tests for the ClusterInfoService collecting information  */
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
argument_list|)
DECL|class|ClusterInfoServiceIT
specifier|public
class|class
name|ClusterInfoServiceIT
extends|extends
name|ESIntegTestCase
block|{
DECL|class|TestPlugin
specifier|public
specifier|static
class|class
name|TestPlugin
extends|extends
name|Plugin
block|{
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"ClusterInfoServiceIT"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"ClusterInfoServiceIT"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ActionModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|registerFilter
argument_list|(
name|BlockingActionFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|BlockingActionFilter
specifier|public
specifier|static
class|class
name|BlockingActionFilter
extends|extends
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|ActionFilter
operator|.
name|Simple
block|{
DECL|field|blockedActions
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|blockedActions
init|=
name|ImmutableSet
operator|.
name|of
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|BlockingActionFilter
specifier|public
name|BlockingActionFilter
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|protected
name|boolean
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionRequest
name|request
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|blockedActions
operator|.
name|contains
argument_list|(
name|action
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"force exception on ["
operator|+
name|action
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|protected
name|boolean
name|apply
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionResponse
name|response
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|order
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|blockActions
specifier|public
name|void
name|blockActions
parameter_list|(
name|String
modifier|...
name|actions
parameter_list|)
block|{
name|blockedActions
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|actions
argument_list|)
expr_stmt|;
block|}
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
comment|// manual collection or upon cluster forming.
operator|.
name|put
argument_list|(
name|InternalClusterInfoService
operator|.
name|INTERNAL_CLUSTER_INFO_TIMEOUT
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
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
return|return
name|pluginList
argument_list|(
name|TestPlugin
operator|.
name|class
argument_list|,
name|MockTransportService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Test
DECL|method|testClusterInfoServiceCollectsInformation
specifier|public
name|void
name|testClusterInfoServiceCollectsInformation
parameter_list|()
throws|throws
name|Exception
block|{
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|InternalClusterInfoService
operator|.
name|INTERNAL_CLUSTER_INFO_UPDATE_INTERVAL
argument_list|,
literal|"200ms"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|Store
operator|.
name|INDEX_STORE_STATS_REFRESH_INTERVAL
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_REBALANCE_ENABLE
argument_list|,
name|EnableAllocationDecider
operator|.
name|Rebalance
operator|.
name|NONE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|InternalTestCluster
name|internalTestCluster
init|=
name|internalCluster
argument_list|()
decl_stmt|;
comment|// Get the cluster info service on the master node
specifier|final
name|InternalClusterInfoService
name|infoService
init|=
operator|(
name|InternalClusterInfoService
operator|)
name|internalTestCluster
operator|.
name|getInstance
argument_list|(
name|ClusterInfoService
operator|.
name|class
argument_list|,
name|internalTestCluster
operator|.
name|getMasterName
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterInfo
name|info
init|=
name|infoService
operator|.
name|refresh
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"info should not be null"
argument_list|,
name|info
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|leastUsages
init|=
name|info
operator|.
name|getNodeLeastAvailableDiskUsages
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|mostUsages
init|=
name|info
operator|.
name|getNodeMostAvailableDiskUsages
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|shardSizes
init|=
name|info
operator|.
name|shardSizes
decl_stmt|;
name|assertNotNull
argument_list|(
name|leastUsages
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|shardSizes
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"some usages are populated"
argument_list|,
name|leastUsages
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"some shard sizes are populated"
argument_list|,
name|shardSizes
operator|.
name|values
argument_list|()
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
name|DiskUsage
name|usage
range|:
name|leastUsages
operator|.
name|values
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> usage: {}"
argument_list|,
name|usage
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"usage has be retrieved"
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|DiskUsage
name|usage
range|:
name|mostUsages
operator|.
name|values
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> usage: {}"
argument_list|,
name|usage
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"usage has be retrieved"
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Long
name|size
range|:
name|shardSizes
operator|.
name|values
argument_list|()
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> shard size: {}"
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"shard size is greater than 0"
argument_list|,
name|size
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|ClusterService
name|clusterService
init|=
name|internalTestCluster
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|,
name|internalTestCluster
operator|.
name|getMasterName
argument_list|()
argument_list|)
decl_stmt|;
name|ClusterState
name|state
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|RoutingNodes
name|routingNodes
init|=
name|state
operator|.
name|getRoutingNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|routingNodes
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|allShards
argument_list|()
control|)
block|{
name|String
name|dataPath
init|=
name|info
operator|.
name|getDataPath
argument_list|(
name|shard
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|dataPath
argument_list|)
expr_stmt|;
name|String
name|nodeId
init|=
name|shard
operator|.
name|currentNodeId
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|discoveryNode
init|=
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
name|IndicesService
name|indicesService
init|=
name|internalTestCluster
operator|.
name|getInstance
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|,
name|discoveryNode
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|indexShard
init|=
name|indexService
operator|.
name|shard
argument_list|(
name|shard
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|indexShard
operator|.
name|shardPath
argument_list|()
operator|.
name|getRootDataPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|dataPath
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testClusterInfoServiceInformationClearOnError
specifier|public
name|void
name|testClusterInfoServiceInformationClearOnError
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|internalCluster
argument_list|()
operator|.
name|startNodesAsync
argument_list|(
literal|2
argument_list|,
comment|// manually control publishing
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|InternalClusterInfoService
operator|.
name|INTERNAL_CLUSTER_INFO_UPDATE_INTERVAL
argument_list|,
literal|"60m"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|InternalTestCluster
name|internalTestCluster
init|=
name|internalCluster
argument_list|()
decl_stmt|;
name|InternalClusterInfoService
name|infoService
init|=
operator|(
name|InternalClusterInfoService
operator|)
name|internalTestCluster
operator|.
name|getInstance
argument_list|(
name|ClusterInfoService
operator|.
name|class
argument_list|,
name|internalTestCluster
operator|.
name|getMasterName
argument_list|()
argument_list|)
decl_stmt|;
comment|// get one healthy sample
name|ClusterInfo
name|info
init|=
name|infoService
operator|.
name|refresh
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"failed to collect info"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"some usages are populated"
argument_list|,
name|info
operator|.
name|getNodeLeastAvailableDiskUsages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"some shard sizes are populated"
argument_list|,
name|info
operator|.
name|shardSizes
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
name|MockTransportService
name|mockTransportService
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
name|internalTestCluster
operator|.
name|getMasterName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|timeout
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|blockedActions
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NodesStatsAction
operator|.
name|NAME
argument_list|,
name|NodesStatsAction
operator|.
name|NAME
operator|+
literal|"[n]"
argument_list|,
name|IndicesStatsAction
operator|.
name|NAME
argument_list|,
name|IndicesStatsAction
operator|.
name|NAME
operator|+
literal|"[n]"
argument_list|)
decl_stmt|;
comment|// drop all outgoing stats requests to force a timeout.
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|internalTestCluster
operator|.
name|clusterService
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|mockTransportService
operator|.
name|addDelegate
argument_list|(
name|node
argument_list|,
operator|new
name|MockTransportService
operator|.
name|DelegateTransport
argument_list|(
name|mockTransportService
operator|.
name|original
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|sendRequest
parameter_list|(
name|DiscoveryNode
name|node
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
throws|,
name|TransportException
block|{
if|if
condition|(
name|blockedActions
operator|.
name|contains
argument_list|(
name|action
argument_list|)
condition|)
block|{
if|if
condition|(
name|timeout
operator|.
name|get
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"dropping [{}] to [{}]"
argument_list|,
name|action
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|super
operator|.
name|sendRequest
argument_list|(
name|node
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
block|}
argument_list|)
expr_stmt|;
block|}
comment|// timeouts shouldn't clear the info
name|timeout
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|info
operator|=
name|infoService
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"info should not be null"
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// node info will time out both on the request level on the count down latch. this means
comment|// it is likely to update the node disk usage based on the one response that came be from local
comment|// node.
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeLeastAvailableDiskUsages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeMostAvailableDiskUsages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// indices is guaranteed to time out on the latch, not updating anything.
name|assertThat
argument_list|(
name|info
operator|.
name|shardSizes
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// now we cause an exception
name|timeout
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|ActionFilters
name|actionFilters
init|=
name|internalTestCluster
operator|.
name|getInstance
argument_list|(
name|ActionFilters
operator|.
name|class
argument_list|,
name|internalTestCluster
operator|.
name|getMasterName
argument_list|()
argument_list|)
decl_stmt|;
name|BlockingActionFilter
name|blockingActionFilter
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ActionFilter
name|filter
range|:
name|actionFilters
operator|.
name|filters
argument_list|()
control|)
block|{
if|if
condition|(
name|filter
operator|instanceof
name|BlockingActionFilter
condition|)
block|{
name|blockingActionFilter
operator|=
operator|(
name|BlockingActionFilter
operator|)
name|filter
expr_stmt|;
break|break;
block|}
block|}
name|assertNotNull
argument_list|(
literal|"failed to find BlockingActionFilter"
argument_list|,
name|blockingActionFilter
argument_list|)
expr_stmt|;
name|blockingActionFilter
operator|.
name|blockActions
argument_list|(
name|blockedActions
operator|.
name|toArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|=
name|infoService
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"info should not be null"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeLeastAvailableDiskUsages
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
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeMostAvailableDiskUsages
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
name|assertThat
argument_list|(
name|info
operator|.
name|shardSizes
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
comment|// check we recover
name|blockingActionFilter
operator|.
name|blockActions
argument_list|()
expr_stmt|;
name|info
operator|=
name|infoService
operator|.
name|refresh
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"info should not be null"
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeLeastAvailableDiskUsages
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
name|assertThat
argument_list|(
name|info
operator|.
name|getNodeMostAvailableDiskUsages
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
name|assertThat
argument_list|(
name|info
operator|.
name|shardSizes
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
block|}
block|}
end_class

end_unit

