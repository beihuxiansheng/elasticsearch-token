begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ClusterStateUpdateTask
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
name|AllocationService
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
name|Setting
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
name|rest
operator|.
name|RestStatus
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  *  */
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
DECL|field|EXPECTED_NODES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|EXPECTED_NODES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"gateway.expected_nodes"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
DECL|field|EXPECTED_DATA_NODES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|EXPECTED_DATA_NODES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"gateway.expected_data_nodes"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
DECL|field|EXPECTED_MASTER_NODES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|EXPECTED_MASTER_NODES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"gateway.expected_master_nodes"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
DECL|field|RECOVER_AFTER_TIME_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|RECOVER_AFTER_TIME_SETTING
init|=
name|Setting
operator|.
name|positiveTimeSetting
argument_list|(
literal|"gateway.recover_after_time"
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
DECL|field|RECOVER_AFTER_NODES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|RECOVER_AFTER_NODES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"gateway.recover_after_nodes"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
DECL|field|RECOVER_AFTER_DATA_NODES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|RECOVER_AFTER_DATA_NODES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"gateway.recover_after_data_nodes"
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
DECL|field|RECOVER_AFTER_MASTER_NODES_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Integer
argument_list|>
name|RECOVER_AFTER_MASTER_NODES_SETTING
init|=
name|Setting
operator|.
name|intSetting
argument_list|(
literal|"gateway.recover_after_master_nodes"
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
decl_stmt|;
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
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
name|ClusterBlockLevel
operator|.
name|ALL
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_RECOVER_AFTER_TIME_IF_EXPECTED_NODES_IS_SET
specifier|public
specifier|static
specifier|final
name|TimeValue
name|DEFAULT_RECOVER_AFTER_TIME_IF_EXPECTED_NODES_IS_SET
init|=
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|5
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
DECL|field|allocationService
specifier|private
specifier|final
name|AllocationService
name|allocationService
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
annotation|@
name|Inject
DECL|method|GatewayService
specifier|public
name|GatewayService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Gateway
name|gateway
parameter_list|,
name|AllocationService
name|allocationService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|DiscoveryService
name|discoveryService
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
name|allocationService
operator|=
name|allocationService
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
name|threadPool
operator|=
name|threadPool
expr_stmt|;
comment|// allow to control a delay of when indices will get created
name|this
operator|.
name|expectedNodes
operator|=
name|EXPECTED_NODES_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedDataNodes
operator|=
name|EXPECTED_DATA_NODES_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedMasterNodes
operator|=
name|EXPECTED_MASTER_NODES_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
if|if
condition|(
name|RECOVER_AFTER_TIME_SETTING
operator|.
name|exists
argument_list|(
name|this
operator|.
name|settings
argument_list|)
condition|)
block|{
name|recoverAfterTime
operator|=
name|RECOVER_AFTER_TIME_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|expectedNodes
operator|>=
literal|0
operator|||
name|expectedDataNodes
operator|>=
literal|0
operator|||
name|expectedMasterNodes
operator|>=
literal|0
condition|)
block|{
name|recoverAfterTime
operator|=
name|DEFAULT_RECOVER_AFTER_TIME_IF_EXPECTED_NODES_IS_SET
expr_stmt|;
block|}
else|else
block|{
name|recoverAfterTime
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|recoverAfterNodes
operator|=
name|RECOVER_AFTER_NODES_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoverAfterDataNodes
operator|=
name|RECOVER_AFTER_DATA_NODES_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
comment|// default the recover after master nodes to the minimum master nodes in the discovery
if|if
condition|(
name|RECOVER_AFTER_MASTER_NODES_SETTING
operator|.
name|exists
argument_list|(
name|this
operator|.
name|settings
argument_list|)
condition|)
block|{
name|recoverAfterMasterNodes
operator|=
name|RECOVER_AFTER_MASTER_NODES_SETTING
operator|.
name|get
argument_list|(
name|this
operator|.
name|settings
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: change me once the minimum_master_nodes is changed too
name|recoverAfterMasterNodes
operator|=
name|settings
operator|.
name|getAsInt
argument_list|(
literal|"discovery.zen.minimum_master_nodes"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
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
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
name|clusterService
operator|.
name|addLast
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// check we didn't miss any cluster state that came in until now / during the addition
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"gateway_initial_state_recovery"
argument_list|,
operator|new
name|ClusterStateUpdateTask
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
throws|throws
name|Exception
block|{
name|checkStateMeetsSettingsAndMaybeRecover
argument_list|(
name|currentState
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|runOnlyOnMaster
parameter_list|()
block|{
comment|// It's OK to run on non masters as checkStateMeetsSettingsAndMaybeRecover checks for this
comment|// we return false to avoid unneeded failure logs
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unexpected failure while checking if state can be recovered. another attempt will be made with the next cluster state change"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{
name|clusterService
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|clusterChanged
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
name|checkStateMeetsSettingsAndMaybeRecover
argument_list|(
name|event
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|checkStateMeetsSettingsAndMaybeRecover
specifier|protected
name|void
name|checkStateMeetsSettingsAndMaybeRecover
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// not our job to recover
return|return;
block|}
if|if
condition|(
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// already recovered
return|return;
block|}
name|DiscoveryNodes
name|nodes
init|=
name|state
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|hasGlobalBlock
argument_list|(
name|discoveryService
operator|.
name|getNoMasterBlock
argument_list|()
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"not recovering from gateway, no master elected yet"
argument_list|)
expr_stmt|;
block|}
elseif|else
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
name|enforceRecoverAfterTime
decl_stmt|;
name|String
name|reason
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
comment|// no expected is set, honor the setting if they are there
name|enforceRecoverAfterTime
operator|=
literal|true
expr_stmt|;
name|reason
operator|=
literal|"recover_after_time was set to ["
operator|+
name|recoverAfterTime
operator|+
literal|"]"
expr_stmt|;
block|}
else|else
block|{
comment|// one of the expected is set, see if all of them meet the need, and ignore the timeout in this case
name|enforceRecoverAfterTime
operator|=
literal|false
expr_stmt|;
name|reason
operator|=
literal|""
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
name|enforceRecoverAfterTime
operator|=
literal|true
expr_stmt|;
name|reason
operator|=
literal|"expecting ["
operator|+
name|expectedNodes
operator|+
literal|"] nodes, but only have ["
operator|+
name|nodes
operator|.
name|masterAndDataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]"
expr_stmt|;
block|}
elseif|else
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
name|enforceRecoverAfterTime
operator|=
literal|true
expr_stmt|;
name|reason
operator|=
literal|"expecting ["
operator|+
name|expectedDataNodes
operator|+
literal|"] data nodes, but only have ["
operator|+
name|nodes
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]"
expr_stmt|;
block|}
elseif|else
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
name|enforceRecoverAfterTime
operator|=
literal|true
expr_stmt|;
name|reason
operator|=
literal|"expecting ["
operator|+
name|expectedMasterNodes
operator|+
literal|"] master nodes, but only have ["
operator|+
name|nodes
operator|.
name|masterNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|"]"
expr_stmt|;
block|}
block|}
name|performStateRecovery
argument_list|(
name|enforceRecoverAfterTime
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|performStateRecovery
specifier|private
name|void
name|performStateRecovery
parameter_list|(
name|boolean
name|enforceRecoverAfterTime
parameter_list|,
name|String
name|reason
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
argument_list|()
decl_stmt|;
if|if
condition|(
name|enforceRecoverAfterTime
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
name|info
argument_list|(
literal|"delaying initial state recovery for [{}]. {}"
argument_list|,
name|recoverAfterTime
argument_list|,
name|reason
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
name|GENERIC
argument_list|,
parameter_list|()
lambda|->
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
name|logger
operator|.
name|info
argument_list|(
literal|"recover_after_time [{}] elapsed. performing state recovery..."
argument_list|,
name|recoverAfterTime
argument_list|)
expr_stmt|;
name|gateway
operator|.
name|performStateRecovery
argument_list|(
name|recoveryListener
argument_list|)
expr_stmt|;
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
name|threadPool
operator|.
name|generic
argument_list|()
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
name|gateway
operator|.
name|performStateRecovery
argument_list|(
name|recoveryListener
argument_list|)
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
annotation|@
name|Override
DECL|method|onSuccess
specifier|public
name|void
name|onSuccess
parameter_list|(
specifier|final
name|ClusterState
name|recoveredState
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"successful state recovery, importing cluster state..."
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-gateway-elected-state"
argument_list|,
operator|new
name|ClusterStateUpdateTask
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
name|MetaData
operator|.
name|builder
argument_list|(
name|recoveredState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
comment|// automatically generate a UID for the metadata if we need to
name|metaDataBuilder
operator|.
name|generateClusterUuidIfNeeded
argument_list|()
expr_stmt|;
if|if
condition|(
name|MetaData
operator|.
name|SETTING_READ_ONLY_SETTING
operator|.
name|get
argument_list|(
name|recoveredState
operator|.
name|metaData
argument_list|()
operator|.
name|settings
argument_list|()
argument_list|)
operator|||
name|MetaData
operator|.
name|SETTING_READ_ONLY_SETTING
operator|.
name|get
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|settings
argument_list|()
argument_list|)
condition|)
block|{
name|blocks
operator|.
name|addGlobalBlock
argument_list|(
name|MetaData
operator|.
name|CLUSTER_READ_ONLY_BLOCK
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
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|blocks
operator|.
name|addBlocks
argument_list|(
name|indexMetaData
argument_list|)
expr_stmt|;
block|}
comment|// update the state to reflect the new metadata and routing
name|ClusterState
name|updatedState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
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
argument_list|(
name|updatedState
operator|.
name|routingTable
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ObjectCursor
argument_list|<
name|IndexMetaData
argument_list|>
name|cursor
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
name|routingTableBuilder
operator|.
name|addAsRecovery
argument_list|(
name|cursor
operator|.
name|value
argument_list|)
expr_stmt|;
block|}
comment|// start with 0 based versions for routing table
name|routingTableBuilder
operator|.
name|version
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// now, reroute
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
name|updatedState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|"state recovered"
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
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
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|GatewayRecoveryListener
operator|.
name|this
operator|.
name|onFailure
argument_list|(
literal|"failed to updated cluster state"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|oldState
parameter_list|,
name|ClusterState
name|newState
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"recovered [{}] indices into cluster_state"
argument_list|,
name|newState
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|recovered
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|scheduledRecovery
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// don't remove the block here, we don't want to allow anything in such a case
name|logger
operator|.
name|info
argument_list|(
literal|"metadata state not restored, reason: {}"
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|// used for testing
DECL|method|recoverAfterTime
specifier|public
name|TimeValue
name|recoverAfterTime
parameter_list|()
block|{
return|return
name|recoverAfterTime
return|;
block|}
block|}
end_class

end_unit

