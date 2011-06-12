begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|ClusterService
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
name|ClusterStateListener
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
name|ProcessedClusterStateUpdateTask
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
name|ClusterBlock
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
name|ClusterBlockLevel
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
name|ClusterBlocks
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
name|metadata
operator|.
name|IndexTemplateMetaData
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
name|MetaData
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
name|MetaDataCreateIndexService
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
name|MetaDataStateIndexService
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
name|routing
operator|.
name|IndexRoutingTable
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
name|RoutingTable
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
name|RoutingAllocation
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
name|ShardsAllocation
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
name|component
operator|.
name|AbstractLifecycleComponent
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
name|discovery
operator|.
name|DiscoveryService
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
name|CountDownLatch
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
name|cluster
operator|.
name|ClusterState
operator|.
name|*
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
name|MetaData
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|GatewayService
specifier|public
class|class
name|GatewayService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|GatewayService
argument_list|>
implements|implements
name|ClusterStateListener
block|{
DECL|field|STATE_NOT_RECOVERED_BLOCK
specifier|public
specifier|static
specifier|final
name|ClusterBlock
name|STATE_NOT_RECOVERED_BLOCK
init|=
operator|new
name|ClusterBlock
argument_list|(
literal|1
argument_list|,
literal|"state not recovered / initialized"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
name|ClusterBlockLevel
operator|.
name|ALL
argument_list|)
decl_stmt|;
DECL|field|gateway
specifier|private
specifier|final
name|Gateway
name|gateway
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|shardsAllocation
specifier|private
specifier|final
name|ShardsAllocation
name|shardsAllocation
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|discoveryService
specifier|private
specifier|final
name|DiscoveryService
name|discoveryService
decl_stmt|;
DECL|field|createIndexService
specifier|private
specifier|final
name|MetaDataCreateIndexService
name|createIndexService
decl_stmt|;
DECL|field|recoverAfterTime
specifier|private
specifier|final
name|TimeValue
name|recoverAfterTime
decl_stmt|;
DECL|field|recoverAfterNodes
specifier|private
specifier|final
name|int
name|recoverAfterNodes
decl_stmt|;
DECL|field|expectedNodes
specifier|private
specifier|final
name|int
name|expectedNodes
decl_stmt|;
DECL|field|recoverAfterDataNodes
specifier|private
specifier|final
name|int
name|recoverAfterDataNodes
decl_stmt|;
DECL|field|expectedDataNodes
specifier|private
specifier|final
name|int
name|expectedDataNodes
decl_stmt|;
DECL|field|recoverAfterMasterNodes
specifier|private
specifier|final
name|int
name|recoverAfterMasterNodes
decl_stmt|;
DECL|field|expectedMasterNodes
specifier|private
specifier|final
name|int
name|expectedMasterNodes
decl_stmt|;
DECL|field|recovered
specifier|private
specifier|final
name|AtomicBoolean
name|recovered
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|scheduledRecovery
specifier|private
specifier|final
name|AtomicBoolean
name|scheduledRecovery
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|GatewayService
annotation|@
name|Inject
specifier|public
name|GatewayService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Gateway
name|gateway
parameter_list|,
name|ShardsAllocation
name|shardsAllocation
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|DiscoveryService
name|discoveryService
parameter_list|,
name|MetaDataCreateIndexService
name|createIndexService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|gateway
operator|=
name|gateway
expr_stmt|;
name|this
operator|.
name|shardsAllocation
operator|=
name|shardsAllocation
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|discoveryService
operator|=
name|discoveryService
expr_stmt|;
name|this
operator|.
name|createIndexService
operator|=
name|createIndexService
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
comment|// allow to control a delay of when indices will get created
name|this
operator|.
name|recoverAfterTime
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"recover_after_time"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoverAfterNodes
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"recover_after_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedNodes
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"expected_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoverAfterDataNodes
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"recover_after_data_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedDataNodes
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"expected_data_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoverAfterMasterNodes
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"recover_after_master_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedMasterNodes
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"expected_master_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// Add the not recovered as initial state block, we don't allow anything until
name|this
operator|.
name|clusterService
operator|.
name|addInitialStateBlock
argument_list|(
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|gateway
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// if we received initial state, see if we can recover within the start phase, so we hold the
comment|// node from starting until we recovered properly
if|if
condition|(
name|discoveryService
operator|.
name|initialStateReceived
argument_list|()
condition|)
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|DiscoveryNodes
name|nodes
init|=
name|clusterState
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
operator|&&
name|clusterState
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
condition|)
block|{
if|if
condition|(
name|recoverAfterNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|<
name|recoverAfterNodes
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, nodes_size (data+master) ["
operator|+
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]< recover_after_nodes ["
operator|+
name|recoverAfterNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|recoverAfterDataNodes
operator|!=
operator|-
literal|1
operator|&&
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|recoverAfterDataNodes
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, nodes_size (data) ["
operator|+
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]< recover_after_data_nodes ["
operator|+
name|recoverAfterDataNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|recoverAfterMasterNodes
operator|!=
operator|-
literal|1
operator|&&
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|recoverAfterMasterNodes
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, nodes_size (master) ["
operator|+
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]< recover_after_master_nodes ["
operator|+
name|recoverAfterMasterNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|ignoreRecoverAfterTime
decl_stmt|;
if|if
condition|(
name|expectedNodes
operator|==
operator|-
literal|1
operator|&&
name|expectedMasterNodes
operator|==
operator|-
literal|1
operator|&&
name|expectedDataNodes
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no expected is set, don't ignore the timeout
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// one of the expected is set, see if all of them meet the need, and ignore the timeout in this case
name|ignoreRecoverAfterTime
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|expectedNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|expectedNodes
operator|)
condition|)
block|{
comment|// does not meet the expected...
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|expectedMasterNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|expectedMasterNodes
operator|)
condition|)
block|{
comment|// does not meet the expected...
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|expectedDataNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|expectedDataNodes
operator|)
condition|)
block|{
comment|// does not meet the expected...
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|performStateRecovery
argument_list|(
name|ignoreRecoverAfterTime
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"can't wait on start for (possibly) reading state from gateway, will do it asynchronously"
argument_list|)
expr_stmt|;
block|}
name|clusterService
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|clusterService
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|gateway
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|gateway
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|clusterChanged
annotation|@
name|Override
specifier|public
name|void
name|clusterChanged
parameter_list|(
specifier|final
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|lifecycle
operator|.
name|stoppedOrClosed
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|event
operator|.
name|localNodeMaster
argument_list|()
operator|&&
name|event
operator|.
name|state
argument_list|()
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
condition|)
block|{
name|ClusterState
name|clusterState
init|=
name|event
operator|.
name|state
argument_list|()
decl_stmt|;
name|DiscoveryNodes
name|nodes
init|=
name|clusterState
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|recoverAfterNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|)
operator|<
name|recoverAfterNodes
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, nodes_size (data+master) ["
operator|+
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]< recover_after_nodes ["
operator|+
name|recoverAfterNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|recoverAfterDataNodes
operator|!=
operator|-
literal|1
operator|&&
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|recoverAfterDataNodes
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, nodes_size (data) ["
operator|+
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]< recover_after_data_nodes ["
operator|+
name|recoverAfterDataNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|recoverAfterMasterNodes
operator|!=
operator|-
literal|1
operator|&&
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|recoverAfterMasterNodes
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, nodes_size (master) ["
operator|+
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]< recover_after_master_nodes ["
operator|+
name|recoverAfterMasterNodes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|ignoreRecoverAfterTime
decl_stmt|;
if|if
condition|(
name|expectedNodes
operator|==
operator|-
literal|1
operator|&&
name|expectedMasterNodes
operator|==
operator|-
literal|1
operator|&&
name|expectedDataNodes
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no expected is set, don't ignore the timeout
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
comment|// one of the expected is set, see if all of them meet the need, and ignore the timeout in this case
name|ignoreRecoverAfterTime
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|expectedNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|expectedNodes
operator|)
condition|)
block|{
comment|// does not meet the expected...
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|expectedMasterNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|expectedMasterNodes
operator|)
condition|)
block|{
comment|// does not meet the expected...
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|expectedDataNodes
operator|!=
operator|-
literal|1
operator|&&
operator|(
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|<
name|expectedDataNodes
operator|)
condition|)
block|{
comment|// does not meet the expected...
name|ignoreRecoverAfterTime
operator|=
literal|false
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|fIgnoreRecoverAfterTime
init|=
name|ignoreRecoverAfterTime
decl_stmt|;
name|threadPool
operator|.
name|cached
argument_list|()
operator|.
name|execute
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
name|performStateRecovery
argument_list|(
name|fIgnoreRecoverAfterTime
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|performStateRecovery
specifier|private
name|void
name|performStateRecovery
parameter_list|(
name|boolean
name|ignoreRecoverAfterTime
parameter_list|)
block|{
specifier|final
name|Gateway
operator|.
name|GatewayStateRecoveredListener
name|recoveryListener
init|=
operator|new
name|GatewayRecoveryListener
argument_list|(
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|ignoreRecoverAfterTime
operator|&&
name|recoverAfterTime
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|scheduledRecovery
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"delaying initial state recovery for [{}]"
argument_list|,
name|recoverAfterTime
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|schedule
argument_list|(
name|recoverAfterTime
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|CACHED
argument_list|,
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
if|if
condition|(
name|recovered
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|gateway
operator|.
name|performStateRecovery
argument_list|(
name|recoveryListener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|recovered
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|gateway
operator|.
name|performStateRecovery
argument_list|(
name|recoveryListener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|GatewayRecoveryListener
class|class
name|GatewayRecoveryListener
implements|implements
name|Gateway
operator|.
name|GatewayStateRecoveredListener
block|{
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|method|GatewayRecoveryListener
name|GatewayRecoveryListener
parameter_list|(
name|CountDownLatch
name|latch
parameter_list|)
block|{
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
DECL|method|onSuccess
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|ClusterState
name|recoveredState
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-gateway-elected-state"
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
assert|assert
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|isEmpty
argument_list|()
assert|;
comment|// remove the block, since we recovered from gateway
name|ClusterBlocks
operator|.
name|Builder
name|blocks
init|=
name|ClusterBlocks
operator|.
name|builder
argument_list|()
operator|.
name|blocks
argument_list|(
name|currentState
operator|.
name|blocks
argument_list|()
argument_list|)
operator|.
name|blocks
argument_list|(
name|recoveredState
operator|.
name|blocks
argument_list|()
argument_list|)
operator|.
name|removeGlobalBlock
argument_list|(
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaDataBuilder
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
name|metaDataBuilder
operator|.
name|version
argument_list|(
name|recoveredState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
comment|// add the index templates
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexTemplateMetaData
argument_list|>
name|entry
range|:
name|recoveredState
operator|.
name|metaData
argument_list|()
operator|.
name|templates
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|recoveredState
operator|.
name|metaData
argument_list|()
control|)
block|{
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|)
expr_stmt|;
if|if
condition|(
name|indexMetaData
operator|.
name|state
argument_list|()
operator|==
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|MetaDataStateIndexService
operator|.
name|INDEX_CLOSED_BLOCK
argument_list|)
expr_stmt|;
block|}
block|}
comment|// update the state to reflect the new metadata and routing
name|ClusterState
name|updatedState
init|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|version
argument_list|(
name|recoveredState
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|blocks
argument_list|(
name|blocks
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataBuilder
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// initialize all index routing tables as empty
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|routingTable
argument_list|(
name|updatedState
operator|.
name|routingTable
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|indexMetaData
operator|.
name|state
argument_list|()
operator|==
name|IndexMetaData
operator|.
name|State
operator|.
name|OPEN
condition|)
block|{
name|IndexRoutingTable
operator|.
name|Builder
name|indexRoutingBuilder
init|=
operator|new
name|IndexRoutingTable
operator|.
name|Builder
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|initializeEmpty
argument_list|(
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
literal|false
comment|/*not from API*/
argument_list|)
decl_stmt|;
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingBuilder
argument_list|)
expr_stmt|;
block|}
block|}
name|routingTableBuilder
operator|.
name|version
argument_list|(
name|recoveredState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
comment|// now, reroute
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|shardsAllocation
operator|.
name|reroute
argument_list|(
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|routingResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"recovered [{}] indices into cluster_state"
argument_list|,
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|onFailure
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// don't remove the block here, we don't want to allow anything in such a case
name|logger
operator|.
name|error
argument_list|(
literal|"failed recover state, blocking..."
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

