begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.get
package|package
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
name|get
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
name|ImmutableList
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
name|get
operator|.
name|GetIndexRequest
operator|.
name|Feature
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
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|info
operator|.
name|TransportClusterInfoAction
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
name|ClusterBlockException
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
name|metadata
operator|.
name|AliasMetaData
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
name|MappingMetaData
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
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|warmer
operator|.
name|IndexWarmersMetaData
operator|.
name|Entry
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

begin_comment
comment|/**  * Get index action.  */
end_comment

begin_class
DECL|class|TransportGetIndexAction
specifier|public
class|class
name|TransportGetIndexAction
extends|extends
name|TransportClusterInfoAction
argument_list|<
name|GetIndexRequest
argument_list|,
name|GetIndexResponse
argument_list|>
block|{
annotation|@
name|Inject
DECL|method|TransportGetIndexAction
specifier|public
name|TransportGetIndexAction
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
name|ActionFilters
name|actionFilters
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|GetIndexAction
operator|.
name|NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|actionFilters
argument_list|,
name|GetIndexRequest
operator|.
name|class
argument_list|)
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
comment|// very lightweight operation, no need to fork
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
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|GetIndexRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indicesBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA_READ
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|request
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|GetIndexResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|GetIndexResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doMasterOperation
specifier|protected
name|void
name|doMasterOperation
parameter_list|(
specifier|final
name|GetIndexRequest
name|request
parameter_list|,
name|String
index|[]
name|concreteIndices
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|GetIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|Entry
argument_list|>
argument_list|>
name|warmersResult
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|MappingMetaData
argument_list|>
argument_list|>
name|mappingsResult
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|ImmutableList
argument_list|<
name|AliasMetaData
argument_list|>
argument_list|>
name|aliasesResult
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
name|ImmutableOpenMap
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|settings
init|=
name|ImmutableOpenMap
operator|.
name|of
argument_list|()
decl_stmt|;
name|Feature
index|[]
name|features
init|=
name|request
operator|.
name|features
argument_list|()
decl_stmt|;
name|boolean
name|doneAliases
init|=
literal|false
decl_stmt|;
name|boolean
name|doneMappings
init|=
literal|false
decl_stmt|;
name|boolean
name|doneSettings
init|=
literal|false
decl_stmt|;
name|boolean
name|doneWarmers
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Feature
name|feature
range|:
name|features
control|)
block|{
switch|switch
condition|(
name|feature
condition|)
block|{
case|case
name|WARMERS
case|:
if|if
condition|(
operator|!
name|doneWarmers
condition|)
block|{
name|warmersResult
operator|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|findWarmers
argument_list|(
name|concreteIndices
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|doneWarmers
operator|=
literal|true
expr_stmt|;
block|}
break|break;
case|case
name|MAPPINGS
case|:
if|if
condition|(
operator|!
name|doneMappings
condition|)
block|{
name|mappingsResult
operator|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|findMappings
argument_list|(
name|concreteIndices
argument_list|,
name|request
operator|.
name|types
argument_list|()
argument_list|)
expr_stmt|;
name|doneMappings
operator|=
literal|true
expr_stmt|;
block|}
break|break;
case|case
name|ALIASES
case|:
if|if
condition|(
operator|!
name|doneAliases
condition|)
block|{
name|aliasesResult
operator|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|findAliases
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
name|concreteIndices
argument_list|)
expr_stmt|;
name|doneAliases
operator|=
literal|true
expr_stmt|;
block|}
break|break;
case|case
name|SETTINGS
case|:
if|if
condition|(
operator|!
name|doneSettings
condition|)
block|{
name|ImmutableOpenMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|settingsMapBuilder
init|=
name|ImmutableOpenMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
name|Settings
name|indexSettings
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getSettings
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|humanReadable
argument_list|()
condition|)
block|{
name|indexSettings
operator|=
name|IndexMetaData
operator|.
name|addHumanReadableSettings
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
block|}
name|settingsMapBuilder
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
name|settings
operator|=
name|settingsMapBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|doneSettings
operator|=
literal|true
expr_stmt|;
block|}
break|break;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"feature ["
operator|+
name|feature
operator|+
literal|"] is not valid"
argument_list|)
throw|;
block|}
block|}
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|GetIndexResponse
argument_list|(
name|concreteIndices
argument_list|,
name|warmersResult
argument_list|,
name|mappingsResult
argument_list|,
name|aliasesResult
argument_list|,
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

