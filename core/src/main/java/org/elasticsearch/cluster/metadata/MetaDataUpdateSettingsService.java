begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|indices
operator|.
name|settings
operator|.
name|put
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
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|upgrade
operator|.
name|post
operator|.
name|UpgradeSettingsClusterStateUpdateRequest
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
name|collect
operator|.
name|Tuple
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
name|IndexScopedSettings
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
name|index
operator|.
name|Index
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
name|HashMap
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
name|Locale
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|ContextPreservingActionListener
operator|.
name|wrapPreservingContext
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
DECL|field|indexScopedSettings
specifier|private
specifier|final
name|IndexScopedSettings
name|indexScopedSettings
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
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
name|IndexScopedSettings
name|indexScopedSettings
parameter_list|,
name|IndicesService
name|indicesService
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
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|clusterService
operator|.
name|addListener
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
name|indexScopedSettings
operator|=
name|indexScopedSettings
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
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
name|isLocalNodeElectedMaster
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// we will want to know this for translating "all" to a number
specifier|final
name|int
name|dataNodeCount
init|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|getDataNodes
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|Index
argument_list|>
argument_list|>
name|nrReplicasChanged
init|=
operator|new
name|HashMap
argument_list|<>
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
name|AutoExpandReplicas
name|autoExpandReplicas
init|=
name|IndexMetaData
operator|.
name|INDEX_AUTO_EXPAND_REPLICAS_SETTING
operator|.
name|get
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|autoExpandReplicas
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
comment|/*                  * we have to expand the number of replicas for this index to at least min and at most max nodes here                  * so we are bumping it up if we have to or reduce it depending on min/max and the number of datanodes.                  * If we change the number of replicas we just let the shard allocator do it's thing once we updated it                  * since it goes through the index metadata to figure out if something needs to be done anyway. Do do that                  * we issue a cluster settings update command below and kicks off a reroute.                  */
specifier|final
name|int
name|min
init|=
name|autoExpandReplicas
operator|.
name|getMinReplicas
argument_list|()
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|autoExpandReplicas
operator|.
name|getMaxReplicas
argument_list|(
name|dataNodeCount
argument_list|)
decl_stmt|;
name|int
name|numberOfReplicas
init|=
name|dataNodeCount
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
name|getNumberOfReplicas
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
argument_list|<>
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
name|getIndex
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
comment|// update settings and kick of a reroute (implicit) for them to take effect
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
name|Settings
operator|.
name|builder
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
name|Index
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
name|Index
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
name|ActionListener
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
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
name|Index
name|index
range|:
name|indices
control|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"{} auto expanded replicas to [{}]"
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
name|Exception
name|t
parameter_list|)
block|{
for|for
control|(
name|Index
name|index
range|:
name|indices
control|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{} fail to auto expand replicas to [{}]"
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
name|ActionListener
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
specifier|final
name|Settings
name|normalizedSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|request
operator|.
name|settings
argument_list|()
argument_list|)
operator|.
name|normalizePrefix
argument_list|(
name|IndexMetaData
operator|.
name|INDEX_SETTING_PREFIX
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsForClosedIndices
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|settingsForOpenIndices
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|skipppedSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|indexScopedSettings
operator|.
name|validate
argument_list|(
name|normalizedSettings
argument_list|)
expr_stmt|;
comment|// never allow to change the number of shards
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
name|normalizedSettings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Setting
name|setting
init|=
name|indexScopedSettings
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|setting
operator|!=
literal|null
assert|;
comment|// we already validated the normalized settings
name|settingsForClosedIndices
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
if|if
condition|(
name|setting
operator|.
name|isDynamic
argument_list|()
condition|)
block|{
name|settingsForOpenIndices
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
else|else
block|{
name|skipppedSettings
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
specifier|final
name|Settings
name|skippedSettigns
init|=
name|skipppedSettings
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|closedSettings
init|=
name|settingsForClosedIndices
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|openSettings
init|=
name|settingsForOpenIndices
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|preserveExisting
init|=
name|request
operator|.
name|isPreserveExisting
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"update-settings"
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
argument_list|(
name|Priority
operator|.
name|URGENT
argument_list|,
name|request
argument_list|,
name|wrapPreservingContext
argument_list|(
name|listener
argument_list|,
name|threadPool
operator|.
name|getThreadContext
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ClusterStateUpdateResponse
name|newResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
return|return
operator|new
name|ClusterStateUpdateResponse
argument_list|(
name|acknowledged
argument_list|)
return|;
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
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|RoutingTable
operator|.
name|builder
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
name|Index
argument_list|>
name|openIndices
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Index
argument_list|>
name|closeIndices
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|actualIndices
init|=
operator|new
name|String
index|[
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Index
name|index
init|=
name|request
operator|.
name|indices
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|actualIndices
index|[
name|i
index|]
operator|=
name|index
operator|.
name|getName
argument_list|()
expr_stmt|;
specifier|final
name|IndexMetaData
name|metaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|getIndexSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|metaData
operator|.
name|getState
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
name|skippedSettigns
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
name|IllegalArgumentException
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Can't update non dynamic settings [%s] for open indices %s"
argument_list|,
name|skippedSettigns
operator|.
name|getAsMap
argument_list|()
operator|.
name|keySet
argument_list|()
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
operator|&&
name|preserveExisting
operator|==
literal|false
condition|)
block|{
comment|// we do *not* update the in sync allocation ids as they will be removed upon the first index
comment|// operation which make these copies stale
comment|// TODO: update the list once the data is deleted by the node?
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
name|maybeUpdateClusterBlock
argument_list|(
name|actualIndices
argument_list|,
name|blocks
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_ONLY_BLOCK
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_ONLY_SETTING
argument_list|,
name|openSettings
argument_list|)
expr_stmt|;
name|maybeUpdateClusterBlock
argument_list|(
name|actualIndices
argument_list|,
name|blocks
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_ONLY_ALLOW_DELETE_BLOCK
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_READ_ONLY_ALLOW_DELETE_SETTING
argument_list|,
name|openSettings
argument_list|)
expr_stmt|;
name|maybeUpdateClusterBlock
argument_list|(
name|actualIndices
argument_list|,
name|blocks
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_METADATA_BLOCK
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_METADATA_SETTING
argument_list|,
name|openSettings
argument_list|)
expr_stmt|;
name|maybeUpdateClusterBlock
argument_list|(
name|actualIndices
argument_list|,
name|blocks
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_WRITE_BLOCK
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_WRITE_SETTING
argument_list|,
name|openSettings
argument_list|)
expr_stmt|;
name|maybeUpdateClusterBlock
argument_list|(
name|actualIndices
argument_list|,
name|blocks
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_BLOCK
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_READ_SETTING
argument_list|,
name|openSettings
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|openIndices
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Index
name|index
range|:
name|openIndices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|metaDataBuilder
operator|.
name|getSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|Settings
operator|.
name|Builder
name|updates
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|indexSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexScopedSettings
operator|.
name|updateDynamicSettings
argument_list|(
name|openSettings
argument_list|,
name|indexSettings
argument_list|,
name|updates
argument_list|,
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|preserveExisting
condition|)
block|{
name|indexSettings
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|settings
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
for|for
control|(
name|Index
name|index
range|:
name|closeIndices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|metaDataBuilder
operator|.
name|getSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|Settings
operator|.
name|Builder
name|updates
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Settings
operator|.
name|Builder
name|indexSettings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexScopedSettings
operator|.
name|updateSettings
argument_list|(
name|closedSettings
argument_list|,
name|indexSettings
argument_list|,
name|updates
argument_list|,
name|index
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|preserveExisting
condition|)
block|{
name|indexSettings
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|settings
argument_list|(
name|indexSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|metaData
argument_list|(
name|metaDataBuilder
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
name|blocks
argument_list|(
name|blocks
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// now, reroute in case things change that require it (like number of replicas)
name|updatedState
operator|=
name|allocationService
operator|.
name|reroute
argument_list|(
name|updatedState
argument_list|,
literal|"settings update"
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|Index
name|index
range|:
name|openIndices
control|)
block|{
specifier|final
name|IndexMetaData
name|currentMetaData
init|=
name|currentState
operator|.
name|getMetaData
argument_list|()
operator|.
name|getIndexSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|IndexMetaData
name|updatedMetaData
init|=
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|getIndexSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|indicesService
operator|.
name|verifyIndexMetadata
argument_list|(
name|currentMetaData
argument_list|,
name|updatedMetaData
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Index
name|index
range|:
name|closeIndices
control|)
block|{
specifier|final
name|IndexMetaData
name|currentMetaData
init|=
name|currentState
operator|.
name|getMetaData
argument_list|()
operator|.
name|getIndexSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|IndexMetaData
name|updatedMetaData
init|=
name|updatedState
operator|.
name|metaData
argument_list|()
operator|.
name|getIndexSafe
argument_list|(
name|index
argument_list|)
decl_stmt|;
comment|// Verifies that the current index settings can be updated with the updated dynamic settings.
name|indicesService
operator|.
name|verifyIndexMetadata
argument_list|(
name|currentMetaData
argument_list|,
name|updatedMetaData
argument_list|)
expr_stmt|;
comment|// Now check that we can create the index with the updated settings (dynamic and non-dynamic).
comment|// This step is mandatory since we allow to update non-dynamic settings on closed indices.
name|indicesService
operator|.
name|verifyIndexMetadata
argument_list|(
name|updatedMetaData
argument_list|,
name|updatedMetaData
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
name|ExceptionsHelper
operator|.
name|convertToElastic
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
name|updatedState
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Updates the cluster block only iff the setting exists in the given settings      */
DECL|method|maybeUpdateClusterBlock
specifier|private
specifier|static
name|void
name|maybeUpdateClusterBlock
parameter_list|(
name|String
index|[]
name|actualIndices
parameter_list|,
name|ClusterBlocks
operator|.
name|Builder
name|blocks
parameter_list|,
name|ClusterBlock
name|block
parameter_list|,
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|setting
parameter_list|,
name|Settings
name|openSettings
parameter_list|)
block|{
if|if
condition|(
name|setting
operator|.
name|exists
argument_list|(
name|openSettings
argument_list|)
condition|)
block|{
specifier|final
name|boolean
name|updateReadBlock
init|=
name|setting
operator|.
name|get
argument_list|(
name|openSettings
argument_list|)
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
name|updateReadBlock
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|index
argument_list|,
name|block
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
name|block
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|upgradeIndexSettings
specifier|public
name|void
name|upgradeIndexSettings
parameter_list|(
specifier|final
name|UpgradeSettingsClusterStateUpdateRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"update-index-compatibility-versions"
argument_list|,
operator|new
name|AckedClusterStateUpdateTask
argument_list|<
name|ClusterStateUpdateResponse
argument_list|>
argument_list|(
name|Priority
operator|.
name|URGENT
argument_list|,
name|request
argument_list|,
name|wrapPreservingContext
argument_list|(
name|listener
argument_list|,
name|threadPool
operator|.
name|getThreadContext
argument_list|()
argument_list|)
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|ClusterStateUpdateResponse
name|newResponse
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
return|return
operator|new
name|ClusterStateUpdateResponse
argument_list|(
name|acknowledged
argument_list|)
return|;
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
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|<
name|Version
argument_list|,
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|request
operator|.
name|versions
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|index
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|metaDataBuilder
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|Version
operator|.
name|CURRENT
operator|.
name|equals
argument_list|(
name|indexMetaData
operator|.
name|getCreationVersion
argument_list|()
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// No reason to pollute the settings, we didn't really upgrade anything
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_UPGRADED
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|v1
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaDataBuilder
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

