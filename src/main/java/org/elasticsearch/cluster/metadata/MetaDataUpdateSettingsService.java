begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
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
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|settings
operator|.
name|UpdateSettingsClusterStateUpdateRequest
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
name|*
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
name|ack
operator|.
name|ClusterStateUpdateListener
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
name|ack
operator|.
name|ClusterStateUpdateResponse
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
name|Booleans
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
name|component
operator|.
name|AbstractComponent
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
name|index
operator|.
name|settings
operator|.
name|IndexDynamicSettings
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
operator|.
name|newClusterStateBuilder
import|;
end_import

begin_comment
comment|/**  * Service responsible for submitting update index settings requests  */
end_comment

begin_class
DECL|class|MetaDataUpdateSettingsService
specifier|public
class|class
name|MetaDataUpdateSettingsService
extends|extends
name|AbstractComponent
implements|implements
name|ClusterStateListener
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
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
DECL|method|MetaDataUpdateSettingsService
specifier|public
name|MetaDataUpdateSettingsService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|AllocationService
name|allocationService
parameter_list|,
annotation|@
name|IndexDynamicSettings
name|DynamicSettings
name|dynamicSettings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|.
name|add
argument_list|(
name|this
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
DECL|method|clusterChanged
specifier|public
name|void
name|clusterChanged
parameter_list|(
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
comment|// update an index with number of replicas based on data nodes if possible
if|if
condition|(
operator|!
name|event
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
return|return;
block|}
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|nrReplicasChanged
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|// we need to do this each time in case it was changed by update settings
for|for
control|(
specifier|final
name|IndexMetaData
name|indexMetaData
range|:
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
control|)
block|{
name|String
name|autoExpandReplicas
init|=
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|)
decl_stmt|;
if|if
condition|(
name|autoExpandReplicas
operator|!=
literal|null
operator|&&
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|autoExpandReplicas
argument_list|,
literal|true
argument_list|)
condition|)
block|{
comment|// Booleans only work for false values, just as we want it here
try|try
block|{
name|int
name|min
decl_stmt|;
name|int
name|max
decl_stmt|;
try|try
block|{
name|min
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|autoExpandReplicas
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|autoExpandReplicas
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|sMax
init|=
name|autoExpandReplicas
operator|.
name|substring
argument_list|(
name|autoExpandReplicas
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|sMax
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
condition|)
block|{
name|max
operator|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
name|max
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|sMax
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to set [{}], wrong format [{}]"
argument_list|,
name|e
argument_list|,
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|,
name|autoExpandReplicas
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|int
name|numberOfReplicas
init|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|dataNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|numberOfReplicas
operator|<
name|min
condition|)
block|{
name|numberOfReplicas
operator|=
name|min
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|numberOfReplicas
operator|>
name|max
condition|)
block|{
name|numberOfReplicas
operator|=
name|max
expr_stmt|;
block|}
comment|// same value, nothing to do there
if|if
condition|(
name|numberOfReplicas
operator|==
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|numberOfReplicas
operator|>=
name|min
operator|&&
name|numberOfReplicas
operator|<=
name|max
condition|)
block|{
if|if
condition|(
operator|!
name|nrReplicasChanged
operator|.
name|containsKey
argument_list|(
name|numberOfReplicas
argument_list|)
condition|)
block|{
name|nrReplicasChanged
operator|.
name|put
argument_list|(
name|numberOfReplicas
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nrReplicasChanged
operator|.
name|get
argument_list|(
name|numberOfReplicas
argument_list|)
operator|.
name|add
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] failed to parse auto expand replicas"
argument_list|,
name|e
argument_list|,
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|nrReplicasChanged
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
specifier|final
name|Integer
name|fNumberOfReplicas
range|:
name|nrReplicasChanged
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|fNumberOfReplicas
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|nrReplicasChanged
operator|.
name|get
argument_list|(
name|fNumberOfReplicas
argument_list|)
decl_stmt|;
name|UpdateSettingsClusterStateUpdateRequest
name|updateRequest
init|=
operator|new
name|UpdateSettingsClusterStateUpdateRequest
argument_list|()
operator|.
name|indices
argument_list|(
name|indices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indices
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
operator|.
name|ackTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|)
comment|//no need to wait for ack here
operator|.
name|masterNodeTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|updateSettings
argument_list|(
name|updateRequest
argument_list|,
operator|new
name|ClusterStateUpdateListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ClusterStateUpdateResponse
name|response
parameter_list|)
block|{
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] auto expanded replicas to [{}]"
argument_list|,
name|index
argument_list|,
name|fNumberOfReplicas
argument_list|)
expr_stmt|;
block|}
block|}
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
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] fail to auto expand replicas to [{}]"
argument_list|,
name|index
argument_list|,
name|fNumberOfReplicas
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|updateSettings
specifier|public
name|void
name|updateSettings
parameter_list|(
specifier|final
name|UpdateSettingsClusterStateUpdateRequest
name|request
parameter_list|,
specifier|final
name|ClusterStateUpdateListener
name|listener
parameter_list|)
block|{
name|ImmutableSettings
operator|.
name|Builder
name|updatedSettingsBuilder
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
decl_stmt|;
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
name|settings
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
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"index"
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"index."
argument_list|)
condition|)
block|{
name|updatedSettingsBuilder
operator|.
name|put
argument_list|(
literal|"index."
operator|+
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
block|}
else|else
block|{
name|updatedSettingsBuilder
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
block|}
block|}
comment|// never allow to change the number of shards
for|for
control|(
name|String
name|key
range|:
name|updatedSettingsBuilder
operator|.
name|internalMap
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|)
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"can't change the number of shards for an index"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
specifier|final
name|Settings
name|closeSettings
init|=
name|updatedSettingsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|removedSettings
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|errors
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
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
name|setting
range|:
name|updatedSettingsBuilder
operator|.
name|internalMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|dynamicSettings
operator|.
name|hasDynamicSetting
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|removedSettings
operator|.
name|add
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|error
init|=
name|dynamicSettings
operator|.
name|validateDynamicSetting
argument_list|(
name|setting
operator|.
name|getKey
argument_list|()
argument_list|,
name|setting
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|errors
operator|.
name|add
argument_list|(
literal|"["
operator|+
name|setting
operator|.
name|getKey
argument_list|()
operator|+
literal|"] - "
operator|+
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|errors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"can't process the settings: "
operator|+
name|errors
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|removedSettings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|removedSetting
range|:
name|removedSettings
control|)
block|{
name|updatedSettingsBuilder
operator|.
name|remove
argument_list|(
name|removedSetting
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Settings
name|openSettings
init|=
name|updatedSettingsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"update-settings"
argument_list|,
name|Priority
operator|.
name|URGENT
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|()
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
return|return
literal|true
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
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClusterStateUpdateResponse
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onAckTimeout
parameter_list|()
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClusterStateUpdateResponse
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TimeValue
name|ackTimeout
parameter_list|()
block|{
return|return
name|request
operator|.
name|ackTimeout
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|request
operator|.
name|masterNodeTimeout
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
name|listener
operator|.
name|onFailure
argument_list|(
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
name|ClusterState
name|currentState
parameter_list|)
block|{
name|String
index|[]
name|actualIndices
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|indices
argument_list|()
argument_list|)
decl_stmt|;
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
name|currentState
operator|.
name|routingTable
argument_list|()
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
name|currentState
operator|.
name|metaData
argument_list|()
argument_list|)
decl_stmt|;
comment|// allow to change any settings to a close index, and only allow dynamic settings to be changed
comment|// on an open index
name|Set
argument_list|<
name|String
argument_list|>
name|openIndices
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|closeIndices
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|actualIndices
control|)
block|{
if|if
condition|(
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
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
name|openIndices
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|closeIndices
operator|.
name|add
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|removedSettings
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|openIndices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Can't update non dynamic settings[%s] for open indices[%s]"
argument_list|,
name|removedSettings
argument_list|,
name|openIndices
argument_list|)
argument_list|)
throw|;
block|}
name|int
name|updatedNumberOfReplicas
init|=
name|openSettings
operator|.
name|getAsInt
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatedNumberOfReplicas
operator|!=
operator|-
literal|1
condition|)
block|{
name|routingTableBuilder
operator|.
name|updateNumberOfReplicas
argument_list|(
name|updatedNumberOfReplicas
argument_list|,
name|actualIndices
argument_list|)
expr_stmt|;
name|metaDataBuilder
operator|.
name|updateNumberOfReplicas
argument_list|(
name|updatedNumberOfReplicas
argument_list|,
name|actualIndices
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"updating number_of_replicas to [{}] for indices {}"
argument_list|,
name|updatedNumberOfReplicas
argument_list|,
name|actualIndices
argument_list|)
expr_stmt|;
block|}
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
name|Boolean
name|updatedReadOnly
init|=
name|openSettings
operator|.
name|getAsBoolean
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_READ_ONLY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|updatedReadOnly
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|index
range|:
name|actualIndices
control|)
block|{
if|if
condition|(
name|updatedReadOnly
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_ONLY_BLOCK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blocks
operator|.
name|removeIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_ONLY_BLOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Boolean
name|updateMetaDataBlock
init|=
name|openSettings
operator|.
name|getAsBoolean
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_METADATA
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|updateMetaDataBlock
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|index
range|:
name|actualIndices
control|)
block|{
if|if
condition|(
name|updateMetaDataBlock
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_METADATA_BLOCK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blocks
operator|.
name|removeIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_METADATA_BLOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Boolean
name|updateWriteBlock
init|=
name|openSettings
operator|.
name|getAsBoolean
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_WRITE
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|updateWriteBlock
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|index
range|:
name|actualIndices
control|)
block|{
if|if
condition|(
name|updateWriteBlock
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_WRITE_BLOCK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blocks
operator|.
name|removeIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_WRITE_BLOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Boolean
name|updateReadBlock
init|=
name|openSettings
operator|.
name|getAsBoolean
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_READ
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|updateReadBlock
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|String
name|index
range|:
name|actualIndices
control|)
block|{
if|if
condition|(
name|updateReadBlock
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_BLOCK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|blocks
operator|.
name|removeIndexBlock
argument_list|(
name|index
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_BLOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|openIndices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|indices
init|=
name|openIndices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|openIndices
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|metaDataBuilder
operator|.
name|updateSettings
argument_list|(
name|openSettings
argument_list|,
name|indices
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|closeIndices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
index|[]
name|indices
init|=
name|closeIndices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|closeIndices
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|metaDataBuilder
operator|.
name|updateSettings
argument_list|(
name|closeSettings
argument_list|,
name|indices
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|updatedState
init|=
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataBuilder
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
argument_list|)
operator|.
name|blocks
argument_list|(
name|blocks
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// now, reroute in case things change that require it (like number of replicas)
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|updatedState
argument_list|)
decl_stmt|;
name|updatedState
operator|=
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
expr_stmt|;
return|return
name|updatedState
return|;
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
block|{             }
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

