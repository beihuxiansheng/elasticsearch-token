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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|NodesStatsResponse
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
name|TransportNodesStatsAction
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
name|admin
operator|.
name|indices
operator|.
name|stats
operator|.
name|TransportIndicesStatsAction
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
name|ShardRouting
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
name|DummyTransportAddress
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
name|monitor
operator|.
name|fs
operator|.
name|FsInfo
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
name|threadpool
operator|.
name|ThreadPool
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

begin_comment
comment|/**  * Fake ClusterInfoService class that allows updating the nodes stats disk  * usage with fake values  */
end_comment

begin_class
DECL|class|MockInternalClusterInfoService
specifier|public
class|class
name|MockInternalClusterInfoService
extends|extends
name|InternalClusterInfoService
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
literal|"mock-cluster-info-service"
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
literal|"a mock cluster info service for testing"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|ClusterModule
name|module
parameter_list|)
block|{
name|module
operator|.
name|clusterInfoServiceImpl
operator|=
name|MockInternalClusterInfoService
operator|.
name|class
expr_stmt|;
block|}
block|}
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|stats
specifier|private
specifier|volatile
name|NodeStats
index|[]
name|stats
init|=
operator|new
name|NodeStats
index|[
literal|3
index|]
decl_stmt|;
comment|/** Create a fake NodeStats for the given node and usage */
DECL|method|makeStats
specifier|public
specifier|static
name|NodeStats
name|makeStats
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|DiskUsage
name|usage
parameter_list|)
block|{
name|FsInfo
operator|.
name|Path
index|[]
name|paths
init|=
operator|new
name|FsInfo
operator|.
name|Path
index|[
literal|1
index|]
decl_stmt|;
name|FsInfo
operator|.
name|Path
name|path
init|=
operator|new
name|FsInfo
operator|.
name|Path
argument_list|(
literal|"/dev/null"
argument_list|,
literal|null
argument_list|,
name|usage
operator|.
name|getTotalBytes
argument_list|()
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|,
name|usage
operator|.
name|getFreeBytes
argument_list|()
argument_list|)
decl_stmt|;
name|paths
index|[
literal|0
index|]
operator|=
name|path
expr_stmt|;
name|FsInfo
name|fsInfo
init|=
operator|new
name|FsInfo
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|paths
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeStats
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
name|nodeName
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|fsInfo
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Inject
DECL|method|MockInternalClusterInfoService
specifier|public
name|MockInternalClusterInfoService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterSettings
name|clusterSettings
parameter_list|,
name|TransportNodesStatsAction
name|transportNodesStatsAction
parameter_list|,
name|TransportIndicesStatsAction
name|transportIndicesStatsAction
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|clusterSettings
argument_list|,
name|transportNodesStatsAction
argument_list|,
name|transportIndicesStatsAction
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|ClusterName
operator|.
name|clusterNameFromSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|stats
index|[
literal|0
index|]
operator|=
name|makeStats
argument_list|(
literal|"node_t1"
argument_list|,
operator|new
name|DiskUsage
argument_list|(
literal|"node_t1"
argument_list|,
literal|"n1"
argument_list|,
literal|"/dev/null"
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|stats
index|[
literal|1
index|]
operator|=
name|makeStats
argument_list|(
literal|"node_t2"
argument_list|,
operator|new
name|DiskUsage
argument_list|(
literal|"node_t2"
argument_list|,
literal|"n2"
argument_list|,
literal|"/dev/null"
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|stats
index|[
literal|2
index|]
operator|=
name|makeStats
argument_list|(
literal|"node_t3"
argument_list|,
operator|new
name|DiskUsage
argument_list|(
literal|"node_t3"
argument_list|,
literal|"n3"
argument_list|,
literal|"/dev/null"
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setN1Usage
specifier|public
name|void
name|setN1Usage
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|DiskUsage
name|newUsage
parameter_list|)
block|{
name|stats
index|[
literal|0
index|]
operator|=
name|makeStats
argument_list|(
name|nodeName
argument_list|,
name|newUsage
argument_list|)
expr_stmt|;
block|}
DECL|method|setN2Usage
specifier|public
name|void
name|setN2Usage
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|DiskUsage
name|newUsage
parameter_list|)
block|{
name|stats
index|[
literal|1
index|]
operator|=
name|makeStats
argument_list|(
name|nodeName
argument_list|,
name|newUsage
argument_list|)
expr_stmt|;
block|}
DECL|method|setN3Usage
specifier|public
name|void
name|setN3Usage
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|DiskUsage
name|newUsage
parameter_list|)
block|{
name|stats
index|[
literal|2
index|]
operator|=
name|makeStats
argument_list|(
name|nodeName
argument_list|,
name|newUsage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateNodeStats
specifier|public
name|CountDownLatch
name|updateNodeStats
parameter_list|(
specifier|final
name|ActionListener
argument_list|<
name|NodesStatsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|NodesStatsResponse
name|response
init|=
operator|new
name|NodesStatsResponse
argument_list|(
name|clusterName
argument_list|,
name|stats
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
operator|new
name|CountDownLatch
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|updateIndicesStats
specifier|public
name|CountDownLatch
name|updateIndicesStats
parameter_list|(
specifier|final
name|ActionListener
argument_list|<
name|IndicesStatsResponse
argument_list|>
name|listener
parameter_list|)
block|{
comment|// Not used, so noop
return|return
operator|new
name|CountDownLatch
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterInfo
specifier|public
name|ClusterInfo
name|getClusterInfo
parameter_list|()
block|{
name|ClusterInfo
name|clusterInfo
init|=
name|super
operator|.
name|getClusterInfo
argument_list|()
decl_stmt|;
return|return
operator|new
name|DevNullClusterInfo
argument_list|(
name|clusterInfo
operator|.
name|getNodeLeastAvailableDiskUsages
argument_list|()
argument_list|,
name|clusterInfo
operator|.
name|getNodeMostAvailableDiskUsages
argument_list|()
argument_list|,
name|clusterInfo
operator|.
name|shardSizes
argument_list|)
return|;
block|}
comment|/**      * ClusterInfo that always points to DevNull.      */
DECL|class|DevNullClusterInfo
specifier|public
specifier|static
class|class
name|DevNullClusterInfo
extends|extends
name|ClusterInfo
block|{
DECL|method|DevNullClusterInfo
specifier|public
name|DevNullClusterInfo
parameter_list|(
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|leastAvailableSpaceUsage
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|DiskUsage
argument_list|>
name|mostAvailableSpaceUsage
parameter_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|shardSizes
parameter_list|)
block|{
name|super
argument_list|(
name|leastAvailableSpaceUsage
argument_list|,
name|mostAvailableSpaceUsage
argument_list|,
name|shardSizes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDataPath
specifier|public
name|String
name|getDataPath
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|)
block|{
return|return
literal|"/dev/null"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|setUpdateFrequency
specifier|public
name|void
name|setUpdateFrequency
parameter_list|(
name|TimeValue
name|updateFrequency
parameter_list|)
block|{
name|super
operator|.
name|setUpdateFrequency
argument_list|(
name|updateFrequency
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

