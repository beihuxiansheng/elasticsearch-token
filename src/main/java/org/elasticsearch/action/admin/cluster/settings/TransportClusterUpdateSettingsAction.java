begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.settings
package|package
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
name|settings
package|;
end_package

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
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeOperationAction
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
name|AckedClusterStateUpdateTask
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
name|cluster
operator|.
name|settings
operator|.
name|ClusterDynamicSettings
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
name|settings
operator|.
name|DynamicSettings
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
name|Nullable
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
name|Priority
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
name|util
operator|.
name|Map
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
name|builder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportClusterUpdateSettingsAction
specifier|public
class|class
name|TransportClusterUpdateSettingsAction
extends|extends
name|TransportMasterNodeOperationAction
argument_list|<
name|ClusterUpdateSettingsRequest
argument_list|,
name|ClusterUpdateSettingsResponse
argument_list|>
block|{
DECL|field|allocationService
specifier|private
specifier|final
name|AllocationService
name|allocationService
decl_stmt|;
DECL|field|dynamicSettings
specifier|private
specifier|final
name|DynamicSettings
name|dynamicSettings
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClusterUpdateSettingsAction
specifier|public
name|TransportClusterUpdateSettingsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|AllocationService
name|allocationService
parameter_list|,
annotation|@
name|ClusterDynamicSettings
name|DynamicSettings
name|dynamicSettings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ClusterUpdateSettingsAction
operator|.
name|NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
name|this
operator|.
name|allocationService
operator|=
name|allocationService
expr_stmt|;
name|this
operator|.
name|dynamicSettings
operator|=
name|dynamicSettings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|ClusterUpdateSettingsRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterUpdateSettingsRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClusterUpdateSettingsResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|ClusterUpdateSettingsResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|ClusterUpdateSettingsRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterUpdateSettingsResponse
argument_list|>
name|listener
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
specifier|final
name|ImmutableSettings
operator|.
name|Builder
name|transientUpdates
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableSettings
operator|.
name|Builder
name|persistentUpdates
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"cluster_update_settings"
argument_list|,
name|Priority
operator|.
name|IMMEDIATE
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|<
name|ClusterUpdateSettingsResponse
argument_list|>
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
block|{
specifier|private
specifier|volatile
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ClusterUpdateSettingsResponse
name|newResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
return|return
operator|new
name|ClusterUpdateSettingsResponse
argument_list|(
name|acknowledged
argument_list|,
name|transientUpdates
operator|.
name|build
argument_list|()
argument_list|,
name|persistentUpdates
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onAllNodesAcked
parameter_list|(
annotation|@
name|Nullable
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|changed
condition|)
block|{
name|reroute
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onAllNodesAcked
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onAckTimeout
parameter_list|()
block|{
if|if
condition|(
name|changed
condition|)
block|{
name|reroute
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|onAckTimeout
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|reroute
parameter_list|(
specifier|final
name|boolean
name|updateSettingsAcked
parameter_list|)
block|{
comment|// We're about to send a second update task, so we need to check if we're still the elected master
comment|// For example the minimum_master_node could have been breached and we're no longer elected master,
comment|// so we should *not* execute the reroute.
if|if
condition|(
operator|!
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Skipping reroute after cluster update settings, because node is no longer master"
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClusterUpdateSettingsResponse
argument_list|(
name|updateSettingsAcked
argument_list|,
name|transientUpdates
operator|.
name|build
argument_list|()
argument_list|,
name|persistentUpdates
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// The reason the reroute needs to be send as separate update task, is that all the *cluster* settings are encapsulate
comment|// in the components (e.g. FilterAllocationDecider), so the changes made by the first call aren't visible
comment|// to the components until the ClusterStateListener instances have been invoked, but are visible after
comment|// the first update task has been completed.
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"reroute_after_cluster_update_settings"
argument_list|,
name|Priority
operator|.
name|URGENT
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|<
name|ClusterUpdateSettingsResponse
argument_list|>
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|mustAck
parameter_list|(
name|DiscoveryNode
name|discoveryNode
parameter_list|)
block|{
comment|//we wait for the reroute ack only if the update settings was acknowledged
return|return
name|updateSettingsAcked
return|;
block|}
annotation|@
name|Override
comment|//we return when the cluster reroute is acked or it times out but the acknowledged flag depends on whether the update settings was acknowledged
specifier|protected
name|ClusterUpdateSettingsResponse
name|newResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
return|return
operator|new
name|ClusterUpdateSettingsResponse
argument_list|(
name|updateSettingsAcked
operator|&&
name|acknowledged
argument_list|,
name|transientUpdates
operator|.
name|build
argument_list|()
argument_list|,
name|persistentUpdates
operator|.
name|build
argument_list|()
argument_list|)
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
comment|//if the reroute fails we only log
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to perform [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticsearchException
argument_list|(
literal|"reroute after update settings failed"
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
specifier|final
name|ClusterState
name|currentState
parameter_list|)
block|{
comment|// now, reroute in case things that require it changed (e.g. number of replicas)
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|currentState
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|routingResult
operator|.
name|changed
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
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
block|}
argument_list|)
expr_stmt|;
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
name|debug
argument_list|(
literal|"failed to perform [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|super
operator|.
name|onFailure
argument_list|(
name|source
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
specifier|final
name|ClusterState
name|currentState
parameter_list|)
block|{
name|ImmutableSettings
operator|.
name|Builder
name|transientSettings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
name|transientSettings
operator|.
name|put
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|transientSettings
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|request
operator|.
name|transientSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|dynamicSettings
operator|.
name|hasDynamicSetting
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"logger."
argument_list|)
condition|)
block|{
name|String
name|error
init|=
name|dynamicSettings
operator|.
name|validateDynamicSetting
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
name|transientSettings
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|transientUpdates
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"ignoring transient setting [{}], [{}]"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"ignoring transient setting [{}], not dynamically updateable"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ImmutableSettings
operator|.
name|Builder
name|persistentSettings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
name|persistentSettings
operator|.
name|put
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|persistentSettings
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|request
operator|.
name|persistentSettings
argument_list|()
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|dynamicSettings
operator|.
name|hasDynamicSetting
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|||
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"logger."
argument_list|)
condition|)
block|{
name|String
name|error
init|=
name|dynamicSettings
operator|.
name|validateDynamicSetting
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
name|persistentSettings
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|persistentUpdates
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"ignoring persistent setting [{}], [{}]"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"ignoring persistent setting [{}], not dynamically updateable"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|changed
condition|)
block|{
return|return
name|currentState
return|;
block|}
name|MetaData
operator|.
name|Builder
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|persistentSettings
argument_list|(
name|persistentSettings
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|transientSettings
argument_list|(
name|transientSettings
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
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
decl_stmt|;
name|boolean
name|updatedReadOnly
init|=
name|metaData
operator|.
name|persistentSettings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
name|MetaData
operator|.
name|SETTING_READ_ONLY
argument_list|,
literal|false
argument_list|)
operator|||
name|metaData
operator|.
name|transientSettings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
name|MetaData
operator|.
name|SETTING_READ_ONLY
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatedReadOnly
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
else|else
block|{
name|blocks
operator|.
name|removeGlobalBlock
argument_list|(
name|MetaData
operator|.
name|CLUSTER_READ_ONLY_BLOCK
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|blocks
argument_list|(
name|blocks
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

