begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchIllegalArgumentException
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
name|cluster
operator|.
name|routing
operator|.
name|RoutingTable
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
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
DECL|method|MetaDataUpdateSettingsService
annotation|@
name|Inject
specifier|public
name|MetaDataUpdateSettingsService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
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
block|}
DECL|method|clusterChanged
annotation|@
name|Override
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
comment|// TODO we only need to do that on first create of an index, or the number of nodes changed
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
specifier|final
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
name|numberOfReplicas
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|updateSettings
argument_list|(
name|settings
argument_list|,
operator|new
name|String
index|[]
block|{
name|indexMetaData
operator|.
name|index
argument_list|()
block|}
argument_list|,
operator|new
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onSuccess
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] auto expanded replicas to [{}]"
argument_list|,
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|numberOfReplicas
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|warn
argument_list|(
literal|"[{}] fail to auto expand replicas to [{}]"
argument_list|,
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|numberOfReplicas
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
DECL|method|updateSettings
specifier|public
name|void
name|updateSettings
parameter_list|(
specifier|final
name|Settings
name|pSettings
parameter_list|,
specifier|final
name|String
index|[]
name|indices
parameter_list|,
specifier|final
name|Listener
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
name|pSettings
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
operator|!
name|IndexMetaData
operator|.
name|dynamicSettings
argument_list|()
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|removedSettings
operator|.
name|add
argument_list|(
name|key
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
try|try
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
name|indices
argument_list|)
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
name|newRoutingTableBuilder
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
if|if
condition|(
operator|!
name|removedSettings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{} ignoring non dynamic index level settings for open indices: {}"
argument_list|,
name|indices
argument_list|,
name|removedSettings
argument_list|)
expr_stmt|;
block|}
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
return|return
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
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
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
name|listener
operator|.
name|onSuccess
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onSuccess
name|void
name|onSuccess
parameter_list|()
function_decl|;
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

