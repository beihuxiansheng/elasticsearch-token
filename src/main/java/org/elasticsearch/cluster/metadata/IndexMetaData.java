begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this   * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ImmutableMap
import|;
end_import

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
name|ElasticSearchIllegalStateException
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
name|DiscoveryNodeFilters
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
name|Preconditions
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
name|MapBuilder
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|regex
operator|.
name|Regex
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
name|util
operator|.
name|concurrent
operator|.
name|Immutable
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentParser
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
name|Arrays
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
name|Map
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
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|Immutable
DECL|class|IndexMetaData
specifier|public
class|class
name|IndexMetaData
block|{
DECL|field|dynamicSettings
specifier|private
specifier|static
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|dynamicSettings
init|=
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|)
operator|.
name|add
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|dynamicSettings
specifier|public
specifier|static
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|dynamicSettings
parameter_list|()
block|{
return|return
name|dynamicSettings
return|;
block|}
DECL|method|hasDynamicSetting
specifier|public
specifier|static
name|boolean
name|hasDynamicSetting
parameter_list|(
name|String
name|key
parameter_list|)
block|{
for|for
control|(
name|String
name|dynamicSetting
range|:
name|dynamicSettings
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|dynamicSetting
argument_list|,
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|addDynamicSettings
specifier|public
specifier|static
specifier|synchronized
name|void
name|addDynamicSettings
parameter_list|(
name|String
modifier|...
name|settings
parameter_list|)
block|{
name|HashSet
argument_list|<
name|String
argument_list|>
name|updatedSettings
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|dynamicSettings
argument_list|)
decl_stmt|;
name|updatedSettings
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|settings
argument_list|)
argument_list|)
expr_stmt|;
name|dynamicSettings
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|updatedSettings
argument_list|)
expr_stmt|;
block|}
DECL|enum|State
specifier|public
specifier|static
enum|enum
name|State
block|{
DECL|enum constant|OPEN
name|OPEN
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|CLOSE
name|CLOSE
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|;
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
DECL|method|State
name|State
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|fromId
specifier|public
specifier|static
name|State
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|OPEN
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|CLOSE
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No state match for id ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|State
name|fromString
parameter_list|(
name|String
name|state
parameter_list|)
block|{
if|if
condition|(
literal|"open"
operator|.
name|equals
argument_list|(
name|state
argument_list|)
condition|)
block|{
return|return
name|OPEN
return|;
block|}
elseif|else
if|if
condition|(
literal|"close"
operator|.
name|equals
argument_list|(
name|state
argument_list|)
condition|)
block|{
return|return
name|CLOSE
return|;
block|}
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"No state match for ["
operator|+
name|state
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|field|SETTING_NUMBER_OF_SHARDS
specifier|public
specifier|static
specifier|final
name|String
name|SETTING_NUMBER_OF_SHARDS
init|=
literal|"index.number_of_shards"
decl_stmt|;
DECL|field|SETTING_NUMBER_OF_REPLICAS
specifier|public
specifier|static
specifier|final
name|String
name|SETTING_NUMBER_OF_REPLICAS
init|=
literal|"index.number_of_replicas"
decl_stmt|;
DECL|field|SETTING_AUTO_EXPAND_REPLICAS
specifier|public
specifier|static
specifier|final
name|String
name|SETTING_AUTO_EXPAND_REPLICAS
init|=
literal|"index.auto_expand_replicas"
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|State
name|state
decl_stmt|;
DECL|field|aliases
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|field|mappings
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappings
decl_stmt|;
DECL|field|totalNumberOfShards
specifier|private
specifier|transient
specifier|final
name|int
name|totalNumberOfShards
decl_stmt|;
DECL|field|includeFilters
specifier|private
specifier|final
name|DiscoveryNodeFilters
name|includeFilters
decl_stmt|;
DECL|field|excludeFilters
specifier|private
specifier|final
name|DiscoveryNodeFilters
name|excludeFilters
decl_stmt|;
DECL|method|IndexMetaData
specifier|private
name|IndexMetaData
parameter_list|(
name|String
name|index
parameter_list|,
name|State
name|state
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappings
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
operator|-
literal|1
argument_list|)
operator|!=
operator|-
literal|1
argument_list|,
literal|"must specify numberOfShards for index ["
operator|+
name|index
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
operator|-
literal|1
argument_list|)
operator|!=
operator|-
literal|1
argument_list|,
literal|"must specify numberOfReplicas for index ["
operator|+
name|index
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|mappings
operator|=
name|mappings
expr_stmt|;
name|this
operator|.
name|totalNumberOfShards
operator|=
name|numberOfShards
argument_list|()
operator|*
operator|(
name|numberOfReplicas
argument_list|()
operator|+
literal|1
operator|)
expr_stmt|;
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|includeMap
init|=
name|settings
operator|.
name|getByPrefix
argument_list|(
literal|"index.routing.allocation.include."
argument_list|)
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|includeMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|includeFilters
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|includeFilters
operator|=
name|DiscoveryNodeFilters
operator|.
name|buildFromKeyValue
argument_list|(
name|includeMap
argument_list|)
expr_stmt|;
block|}
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|excludeMap
init|=
name|settings
operator|.
name|getByPrefix
argument_list|(
literal|"index.routing.allocation.exclude."
argument_list|)
operator|.
name|getAsMap
argument_list|()
decl_stmt|;
if|if
condition|(
name|excludeMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|excludeFilters
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|excludeFilters
operator|=
name|DiscoveryNodeFilters
operator|.
name|buildFromKeyValue
argument_list|(
name|excludeMap
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|getIndex
specifier|public
name|String
name|getIndex
parameter_list|()
block|{
return|return
name|index
argument_list|()
return|;
block|}
DECL|method|state
specifier|public
name|State
name|state
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
return|;
block|}
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
argument_list|()
return|;
block|}
DECL|method|numberOfShards
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|getNumberOfShards
specifier|public
name|int
name|getNumberOfShards
parameter_list|()
block|{
return|return
name|numberOfShards
argument_list|()
return|;
block|}
DECL|method|numberOfReplicas
specifier|public
name|int
name|numberOfReplicas
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|getNumberOfReplicas
specifier|public
name|int
name|getNumberOfReplicas
parameter_list|()
block|{
return|return
name|numberOfReplicas
argument_list|()
return|;
block|}
DECL|method|totalNumberOfShards
specifier|public
name|int
name|totalNumberOfShards
parameter_list|()
block|{
return|return
name|totalNumberOfShards
return|;
block|}
DECL|method|getTotalNumberOfShards
specifier|public
name|int
name|getTotalNumberOfShards
parameter_list|()
block|{
return|return
name|totalNumberOfShards
argument_list|()
return|;
block|}
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
DECL|method|getSettings
specifier|public
name|Settings
name|getSettings
parameter_list|()
block|{
return|return
name|settings
argument_list|()
return|;
block|}
DECL|method|aliases
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
parameter_list|()
block|{
return|return
name|this
operator|.
name|aliases
return|;
block|}
DECL|method|getAliases
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|getAliases
parameter_list|()
block|{
return|return
name|aliases
argument_list|()
return|;
block|}
DECL|method|mappings
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappings
parameter_list|()
block|{
return|return
name|mappings
return|;
block|}
DECL|method|getMappings
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|getMappings
parameter_list|()
block|{
return|return
name|mappings
argument_list|()
return|;
block|}
DECL|method|mapping
specifier|public
name|MappingMetaData
name|mapping
parameter_list|(
name|String
name|mappingType
parameter_list|)
block|{
return|return
name|mappings
operator|.
name|get
argument_list|(
name|mappingType
argument_list|)
return|;
block|}
annotation|@
name|Nullable
DECL|method|includeFilters
specifier|public
name|DiscoveryNodeFilters
name|includeFilters
parameter_list|()
block|{
return|return
name|includeFilters
return|;
block|}
annotation|@
name|Nullable
DECL|method|excludeFilters
specifier|public
name|DiscoveryNodeFilters
name|excludeFilters
parameter_list|()
block|{
return|return
name|excludeFilters
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|IndexMetaData
name|that
init|=
operator|(
name|IndexMetaData
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|aliases
operator|.
name|equals
argument_list|(
name|that
operator|.
name|aliases
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|index
operator|.
name|equals
argument_list|(
name|that
operator|.
name|index
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|mappings
operator|.
name|equals
argument_list|(
name|that
operator|.
name|mappings
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|settings
operator|.
name|equals
argument_list|(
name|that
operator|.
name|settings
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|state
operator|!=
name|that
operator|.
name|state
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|index
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|state
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|aliases
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|settings
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|mappings
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|method|newIndexMetaDataBuilder
specifier|public
specifier|static
name|Builder
name|newIndexMetaDataBuilder
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|newIndexMetaDataBuilder
specifier|public
specifier|static
name|Builder
name|newIndexMetaDataBuilder
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|indexMetaData
argument_list|)
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
init|=
name|State
operator|.
name|OPEN
decl_stmt|;
DECL|field|settings
specifier|private
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
decl_stmt|;
DECL|field|mappings
specifier|private
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
name|mappings
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
DECL|field|aliases
specifier|private
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|aliases
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
name|this
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|settings
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
argument_list|)
expr_stmt|;
name|mappings
operator|.
name|putAll
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|)
expr_stmt|;
name|aliases
operator|.
name|putAll
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|)
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|indexMetaData
operator|.
name|state
expr_stmt|;
block|}
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|numberOfShards
specifier|public
name|Builder
name|numberOfShards
parameter_list|(
name|int
name|numberOfShards
parameter_list|)
block|{
name|settings
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|numberOfShards
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numberOfShards
specifier|public
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|numberOfReplicas
specifier|public
name|Builder
name|numberOfReplicas
parameter_list|(
name|int
name|numberOfReplicas
parameter_list|)
block|{
name|settings
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|numberOfReplicas
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numberOfReplicas
specifier|public
name|int
name|numberOfReplicas
parameter_list|()
block|{
return|return
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|settings
specifier|public
name|Builder
name|settings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|settings
specifier|public
name|Builder
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|removeMapping
specifier|public
name|Builder
name|removeMapping
parameter_list|(
name|String
name|mappingType
parameter_list|)
block|{
name|mappings
operator|.
name|remove
argument_list|(
name|mappingType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putMapping
specifier|public
name|Builder
name|putMapping
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|source
argument_list|)
operator|.
name|createParser
argument_list|(
name|source
argument_list|)
decl_stmt|;
try|try
block|{
name|putMapping
argument_list|(
operator|new
name|MappingMetaData
argument_list|(
name|type
argument_list|,
name|parser
operator|.
name|map
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|putMapping
specifier|public
name|Builder
name|putMapping
parameter_list|(
name|MappingMetaData
name|mappingMd
parameter_list|)
block|{
name|mappings
operator|.
name|put
argument_list|(
name|mappingMd
operator|.
name|type
argument_list|()
argument_list|,
name|mappingMd
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|state
specifier|public
name|Builder
name|state
parameter_list|(
name|State
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putAlias
specifier|public
name|Builder
name|putAlias
parameter_list|(
name|AliasMetaData
name|aliasMetaData
parameter_list|)
block|{
name|aliases
operator|.
name|put
argument_list|(
name|aliasMetaData
operator|.
name|alias
argument_list|()
argument_list|,
name|aliasMetaData
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|putAlias
specifier|public
name|Builder
name|putAlias
parameter_list|(
name|AliasMetaData
operator|.
name|Builder
name|aliasMetaData
parameter_list|)
block|{
name|aliases
operator|.
name|put
argument_list|(
name|aliasMetaData
operator|.
name|alias
argument_list|()
argument_list|,
name|aliasMetaData
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|removerAlias
specifier|public
name|Builder
name|removerAlias
parameter_list|(
name|String
name|alias
parameter_list|)
block|{
name|aliases
operator|.
name|remove
argument_list|(
name|alias
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|IndexMetaData
name|build
parameter_list|()
block|{
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|AliasMetaData
argument_list|>
name|tmpAliases
init|=
name|aliases
decl_stmt|;
name|Settings
name|tmpSettings
init|=
name|settings
decl_stmt|;
comment|// For backward compatibility
name|String
index|[]
name|legacyAliases
init|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"index.aliases"
argument_list|)
decl_stmt|;
if|if
condition|(
name|legacyAliases
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|tmpAliases
operator|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|alias
range|:
name|legacyAliases
control|)
block|{
name|AliasMetaData
name|aliasMd
init|=
name|AliasMetaData
operator|.
name|newAliasMetaDataBuilder
argument_list|(
name|alias
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|tmpAliases
operator|.
name|put
argument_list|(
name|alias
argument_list|,
name|aliasMd
argument_list|)
expr_stmt|;
block|}
name|tmpAliases
operator|.
name|putAll
argument_list|(
name|aliases
operator|.
name|immutableMap
argument_list|()
argument_list|)
expr_stmt|;
comment|// Remove index.aliases from settings once they are migrated to the new data structure
name|tmpSettings
operator|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|putArray
argument_list|(
literal|"index.aliases"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|IndexMetaData
argument_list|(
name|index
argument_list|,
name|state
argument_list|,
name|tmpSettings
argument_list|,
name|mappings
operator|.
name|immutableMap
argument_list|()
argument_list|,
name|tmpAliases
operator|.
name|immutableMap
argument_list|()
argument_list|)
return|;
block|}
DECL|method|toXContent
specifier|public
specifier|static
name|void
name|toXContent
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"state"
argument_list|,
name|indexMetaData
operator|.
name|state
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"settings"
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
name|indexMetaData
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
name|builder
operator|.
name|field
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"mappings"
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
name|MappingMetaData
argument_list|>
name|entry
range|:
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|byte
index|[]
name|data
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|source
argument_list|()
operator|.
name|uncompressed
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|data
argument_list|)
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|parser
operator|.
name|mapOrdered
argument_list|()
decl_stmt|;
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"aliases"
argument_list|)
expr_stmt|;
for|for
control|(
name|AliasMetaData
name|alias
range|:
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|AliasMetaData
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|alias
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|IndexMetaData
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"settings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ImmutableSettings
operator|.
name|Builder
name|settingsBuilder
init|=
name|settingsBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
name|String
name|key
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|String
name|value
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
name|settingsBuilder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|settings
argument_list|(
name|settingsBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"mappings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|parser
operator|.
name|mapOrdered
argument_list|()
decl_stmt|;
if|if
condition|(
name|mapping
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|String
name|mappingType
init|=
name|mapping
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|builder
operator|.
name|putMapping
argument_list|(
operator|new
name|MappingMetaData
argument_list|(
name|mappingType
argument_list|,
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"aliases"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
name|builder
operator|.
name|putAlias
argument_list|(
name|AliasMetaData
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"state"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|state
argument_list|(
name|State
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|IndexMetaData
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|(
name|in
operator|.
name|readUTF
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|state
argument_list|(
name|State
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|settings
argument_list|(
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|mappingsSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|mappingsSize
condition|;
name|i
operator|++
control|)
block|{
name|MappingMetaData
name|mappingMd
init|=
name|MappingMetaData
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|builder
operator|.
name|putMapping
argument_list|(
name|mappingMd
argument_list|)
expr_stmt|;
block|}
name|int
name|aliasesSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|aliasesSize
condition|;
name|i
operator|++
control|)
block|{
name|AliasMetaData
name|aliasMd
init|=
name|AliasMetaData
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|builder
operator|.
name|putAlias
argument_list|(
name|aliasMd
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|indexMetaData
operator|.
name|state
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|writeSettingsToStream
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|MappingMetaData
name|mappingMd
range|:
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|MappingMetaData
operator|.
name|writeTo
argument_list|(
name|mappingMd
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVInt
argument_list|(
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|AliasMetaData
name|aliasMd
range|:
name|indexMetaData
operator|.
name|aliases
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|AliasMetaData
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|aliasMd
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

