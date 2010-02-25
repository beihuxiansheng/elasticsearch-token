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
name|Sets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
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
name|action
operator|.
name|index
operator|.
name|NodeIndexCreatedAction
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
name|action
operator|.
name|index
operator|.
name|NodeIndexDeletedAction
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
name|action
operator|.
name|index
operator|.
name|NodeMappingCreatedAction
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
name|strategy
operator|.
name|ShardsRoutingStrategy
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
name|index
operator|.
name|mapper
operator|.
name|DocumentMapper
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
name|mapper
operator|.
name|InvalidTypeNameException
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
name|mapper
operator|.
name|MapperService
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
name|service
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
name|indices
operator|.
name|IndexAlreadyExistsException
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
name|IndexMissingException
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
name|indices
operator|.
name|InvalidIndexNameException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
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
name|util
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
name|util
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
name|util
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
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|IndexMetaData
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|DocumentMapper
operator|.
name|MergeFlags
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
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|MetaDataService
specifier|public
class|class
name|MetaDataService
extends|extends
name|AbstractComponent
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|shardsRoutingStrategy
specifier|private
specifier|final
name|ShardsRoutingStrategy
name|shardsRoutingStrategy
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|nodeIndexCreatedAction
specifier|private
specifier|final
name|NodeIndexCreatedAction
name|nodeIndexCreatedAction
decl_stmt|;
DECL|field|nodeIndexDeletedAction
specifier|private
specifier|final
name|NodeIndexDeletedAction
name|nodeIndexDeletedAction
decl_stmt|;
DECL|field|nodeMappingCreatedAction
specifier|private
specifier|final
name|NodeMappingCreatedAction
name|nodeMappingCreatedAction
decl_stmt|;
DECL|method|MetaDataService
annotation|@
name|Inject
specifier|public
name|MetaDataService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|ShardsRoutingStrategy
name|shardsRoutingStrategy
parameter_list|,
name|NodeIndexCreatedAction
name|nodeIndexCreatedAction
parameter_list|,
name|NodeIndexDeletedAction
name|nodeIndexDeletedAction
parameter_list|,
name|NodeMappingCreatedAction
name|nodeMappingCreatedAction
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
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|this
operator|.
name|shardsRoutingStrategy
operator|=
name|shardsRoutingStrategy
expr_stmt|;
name|this
operator|.
name|nodeIndexCreatedAction
operator|=
name|nodeIndexCreatedAction
expr_stmt|;
name|this
operator|.
name|nodeIndexDeletedAction
operator|=
name|nodeIndexDeletedAction
expr_stmt|;
name|this
operator|.
name|nodeMappingCreatedAction
operator|=
name|nodeMappingCreatedAction
expr_stmt|;
block|}
comment|// TODO should find nicer solution than sync here, since we block for timeout (same for other ops)
DECL|method|createIndex
specifier|public
specifier|synchronized
name|CreateIndexResult
name|createIndex
parameter_list|(
specifier|final
name|String
name|index
parameter_list|,
specifier|final
name|Settings
name|indexSettings
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
throws|throws
name|IndexAlreadyExistsException
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexAlreadyExistsException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|.
name|contains
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
literal|"must not contain whitespace"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|.
name|contains
argument_list|(
literal|","
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
literal|"must not contain ',"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|.
name|contains
argument_list|(
literal|"#"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
literal|"must not contain '#"
argument_list|)
throw|;
block|}
if|if
condition|(
name|index
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'_'
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
literal|"must not start with '_'"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|index
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
literal|"must be lowercase"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|Strings
operator|.
name|validFileName
argument_list|(
name|index
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidIndexNameException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|,
name|index
argument_list|,
literal|"must not contain the following characters "
operator|+
name|Strings
operator|.
name|INVALID_FILENAME_CHARS
argument_list|)
throw|;
block|}
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|NodeIndexCreatedAction
operator|.
name|Listener
name|nodeCreatedListener
init|=
operator|new
name|NodeIndexCreatedAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNodeIndexCreated
parameter_list|(
name|String
name|mIndex
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|index
operator|.
name|equals
argument_list|(
name|mIndex
argument_list|)
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|nodeIndexCreatedAction
operator|.
name|add
argument_list|(
name|nodeCreatedListener
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"create-index ["
operator|+
name|index
operator|+
literal|"]"
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
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
operator|new
name|RoutingTable
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|currentState
operator|.
name|routingTable
argument_list|()
operator|.
name|indicesRouting
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
block|}
name|ImmutableSettings
operator|.
name|Builder
name|indexSettingsBuilder
init|=
name|settingsBuilder
argument_list|()
operator|.
name|putAll
argument_list|(
name|indexSettings
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexSettings
operator|.
name|get
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|indexSettingsBuilder
operator|.
name|putInt
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexSettings
operator|.
name|get
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|)
operator|==
literal|null
condition|)
block|{
name|indexSettingsBuilder
operator|.
name|putInt
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|settings
operator|.
name|getAsInt
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Settings
name|actualIndexSettings
init|=
name|indexSettingsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|newIndexMetaDataBuilder
argument_list|(
name|index
argument_list|)
operator|.
name|settings
argument_list|(
name|actualIndexSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|MetaData
name|newMetaData
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
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
name|index
argument_list|)
operator|.
name|initializeEmpty
argument_list|(
name|newMetaData
operator|.
name|index
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingBuilder
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Creating Index [{}], shards [{}]/[{}]"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|index
block|,
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
block|,
name|indexMetaData
operator|.
name|numberOfReplicas
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|RoutingTable
name|newRoutingTable
init|=
name|shardsRoutingStrategy
operator|.
name|reroute
argument_list|(
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
argument_list|)
operator|.
name|metaData
argument_list|(
name|newMetaData
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
name|currentState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|newRoutingTable
argument_list|)
operator|.
name|metaData
argument_list|(
name|newMetaData
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|boolean
name|acknowledged
decl_stmt|;
try|try
block|{
name|acknowledged
operator|=
name|latch
operator|.
name|await
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|acknowledged
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|nodeIndexCreatedAction
operator|.
name|remove
argument_list|(
name|nodeCreatedListener
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CreateIndexResult
argument_list|(
name|acknowledged
argument_list|)
return|;
block|}
DECL|method|deleteIndex
specifier|public
specifier|synchronized
name|DeleteIndexResult
name|deleteIndex
parameter_list|(
specifier|final
name|String
name|index
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
throws|throws
name|IndexMissingException
block|{
name|RoutingTable
name|routingTable
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|routingTable
operator|.
name|hasIndex
argument_list|(
name|index
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Deleting index [{}]"
argument_list|,
name|index
argument_list|)
expr_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|NodeIndexDeletedAction
operator|.
name|Listener
name|listener
init|=
operator|new
name|NodeIndexDeletedAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNodeIndexDeleted
parameter_list|(
name|String
name|fIndex
parameter_list|,
name|String
name|nodeId
parameter_list|)
block|{
if|if
condition|(
name|fIndex
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|nodeIndexDeletedAction
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"delete-index ["
operator|+
name|index
operator|+
literal|"]"
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
name|RoutingTable
operator|.
name|Builder
name|routingTableBuilder
init|=
operator|new
name|RoutingTable
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|currentState
operator|.
name|routingTable
argument_list|()
operator|.
name|indicesRouting
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|indexRoutingTable
operator|.
name|index
argument_list|()
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|routingTableBuilder
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
block|}
block|}
name|MetaData
name|newMetaData
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
operator|.
name|remove
argument_list|(
name|index
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|newRoutingTable
init|=
name|shardsRoutingStrategy
operator|.
name|reroute
argument_list|(
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTableBuilder
argument_list|)
operator|.
name|metaData
argument_list|(
name|newMetaData
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
name|currentState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|newRoutingTable
argument_list|)
operator|.
name|metaData
argument_list|(
name|newMetaData
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|boolean
name|acknowledged
decl_stmt|;
try|try
block|{
name|acknowledged
operator|=
name|latch
operator|.
name|await
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|acknowledged
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|nodeIndexDeletedAction
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|DeleteIndexResult
argument_list|(
name|acknowledged
argument_list|)
return|;
block|}
DECL|method|updateMapping
specifier|public
specifier|synchronized
name|void
name|updateMapping
parameter_list|(
specifier|final
name|String
name|index
parameter_list|,
specifier|final
name|String
name|type
parameter_list|,
specifier|final
name|String
name|mappingSource
parameter_list|)
block|{
name|MapperService
name|mapperService
init|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|index
argument_list|)
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|DocumentMapper
name|existingMapper
init|=
name|mapperService
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
comment|// parse the updated one
name|DocumentMapper
name|updatedMapper
init|=
name|mapperService
operator|.
name|parse
argument_list|(
name|type
argument_list|,
name|mappingSource
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingMapper
operator|==
literal|null
condition|)
block|{
name|existingMapper
operator|=
name|updatedMapper
expr_stmt|;
block|}
else|else
block|{
comment|// merge from the updated into the existing, ignore duplicates (we know we have them, we just want the new ones)
name|existingMapper
operator|.
name|merge
argument_list|(
name|updatedMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
operator|.
name|ignoreDuplicates
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// build the updated mapping source
specifier|final
name|String
name|updatedMappingSource
init|=
name|existingMapper
operator|.
name|buildSource
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Index ["
operator|+
name|index
operator|+
literal|"]: Update mapping ["
operator|+
name|type
operator|+
literal|"] (dynamic) with source ["
operator|+
name|updatedMappingSource
operator|+
literal|"]"
argument_list|)
expr_stmt|;
comment|// publish the new mapping
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"update-mapping ["
operator|+
name|index
operator|+
literal|"]["
operator|+
name|type
operator|+
literal|"]"
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
name|MetaData
operator|.
name|Builder
name|builder
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
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|putMapping
argument_list|(
name|type
argument_list|,
name|updatedMappingSource
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|builder
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
DECL|method|putMapping
specifier|public
specifier|synchronized
name|PutMappingResult
name|putMapping
parameter_list|(
specifier|final
name|String
index|[]
name|indices
parameter_list|,
name|String
name|mappingType
parameter_list|,
specifier|final
name|String
name|mappingSource
parameter_list|,
name|boolean
name|ignoreDuplicates
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexRoutingTable
name|indexTable
init|=
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|indicesRouting
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexTable
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentMapper
argument_list|>
name|newMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|DocumentMapper
argument_list|>
name|existingMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|!=
literal|null
condition|)
block|{
comment|// try and parse it (no need to add it here) so we can bail early in case of parsing exception
name|DocumentMapper
name|newMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|parse
argument_list|(
name|mappingType
argument_list|,
name|mappingSource
argument_list|)
decl_stmt|;
name|newMappers
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|newMapper
argument_list|)
expr_stmt|;
name|DocumentMapper
name|existingMapper
init|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|mappingType
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingMapper
operator|!=
literal|null
condition|)
block|{
comment|// first simulate and throw an exception if something goes wrong
name|existingMapper
operator|.
name|merge
argument_list|(
name|newMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|true
argument_list|)
operator|.
name|ignoreDuplicates
argument_list|(
name|ignoreDuplicates
argument_list|)
argument_list|)
expr_stmt|;
name|existingMappers
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|newMapper
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|index
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|mappingType
operator|==
literal|null
condition|)
block|{
name|mappingType
operator|=
name|newMappers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|type
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|mappingType
operator|.
name|equals
argument_list|(
name|newMappers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidTypeNameException
argument_list|(
literal|"Type name provided does not match type name within mapping definition"
argument_list|)
throw|;
block|}
if|if
condition|(
name|mappingType
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'_'
condition|)
block|{
throw|throw
operator|new
name|InvalidTypeNameException
argument_list|(
literal|"Document mapping type name can't start with '_'"
argument_list|)
throw|;
block|}
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|mappings
init|=
name|newHashMap
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
name|DocumentMapper
argument_list|>
name|entry
range|:
name|newMappers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
decl_stmt|;
name|String
name|index
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
comment|// do the actual merge here on the master, and update the mapping source
name|DocumentMapper
name|newMapper
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|existingMappers
operator|.
name|containsKey
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
comment|// we have an existing mapping, do the merge here (on the master), it will automatically update the mapping source
name|DocumentMapper
name|existingMapper
init|=
name|existingMappers
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|existingMapper
operator|.
name|merge
argument_list|(
name|newMapper
argument_list|,
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
operator|.
name|ignoreDuplicates
argument_list|(
name|ignoreDuplicates
argument_list|)
argument_list|)
expr_stmt|;
comment|// use the merged mapping source
name|mapping
operator|=
operator|new
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|existingMapper
operator|.
name|type
argument_list|()
argument_list|,
name|existingMapper
operator|.
name|buildSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|=
operator|new
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|newMapper
operator|.
name|type
argument_list|()
argument_list|,
name|newMapper
operator|.
name|buildSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|mappings
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|mapping
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Index ["
operator|+
name|index
operator|+
literal|"]: Put mapping ["
operator|+
name|mapping
operator|.
name|v1
argument_list|()
operator|+
literal|"] with source ["
operator|+
name|mapping
operator|.
name|v2
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|size
argument_list|()
operator|*
name|indices
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|indicesSet
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|indices
argument_list|)
decl_stmt|;
specifier|final
name|String
name|fMappingType
init|=
name|mappingType
decl_stmt|;
name|NodeMappingCreatedAction
operator|.
name|Listener
name|listener
init|=
operator|new
name|NodeMappingCreatedAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNodeMappingCreated
parameter_list|(
name|NodeMappingCreatedAction
operator|.
name|NodeMappingCreatedResponse
name|response
parameter_list|)
block|{
if|if
condition|(
name|indicesSet
operator|.
name|contains
argument_list|(
name|response
operator|.
name|index
argument_list|()
argument_list|)
operator|&&
name|response
operator|.
name|type
argument_list|()
operator|.
name|equals
argument_list|(
name|fMappingType
argument_list|)
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|nodeMappingCreatedAction
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"put-mapping ["
operator|+
name|mappingType
operator|+
literal|"]"
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
name|MetaData
operator|.
name|Builder
name|builder
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
for|for
control|(
name|String
name|indexName
range|:
name|indices
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexMissingException
argument_list|(
operator|new
name|Index
argument_list|(
name|indexName
argument_list|)
argument_list|)
throw|;
block|}
name|Tuple
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mapping
init|=
name|mappings
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
name|indexMetaData
argument_list|)
operator|.
name|putMapping
argument_list|(
name|mapping
operator|.
name|v1
argument_list|()
argument_list|,
name|mapping
operator|.
name|v2
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|metaData
argument_list|(
name|builder
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|boolean
name|acknowledged
decl_stmt|;
try|try
block|{
name|acknowledged
operator|=
name|latch
operator|.
name|await
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|acknowledged
operator|=
literal|false
expr_stmt|;
block|}
finally|finally
block|{
name|nodeMappingCreatedAction
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|PutMappingResult
argument_list|(
name|acknowledged
argument_list|)
return|;
block|}
DECL|class|PutMappingResult
specifier|public
specifier|static
class|class
name|PutMappingResult
block|{
DECL|field|acknowledged
specifier|private
specifier|final
name|boolean
name|acknowledged
decl_stmt|;
DECL|method|PutMappingResult
specifier|public
name|PutMappingResult
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
name|this
operator|.
name|acknowledged
operator|=
name|acknowledged
expr_stmt|;
block|}
DECL|method|acknowledged
specifier|public
name|boolean
name|acknowledged
parameter_list|()
block|{
return|return
name|acknowledged
return|;
block|}
block|}
DECL|class|CreateIndexResult
specifier|public
specifier|static
class|class
name|CreateIndexResult
block|{
DECL|field|acknowledged
specifier|private
specifier|final
name|boolean
name|acknowledged
decl_stmt|;
DECL|method|CreateIndexResult
specifier|public
name|CreateIndexResult
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
name|this
operator|.
name|acknowledged
operator|=
name|acknowledged
expr_stmt|;
block|}
DECL|method|acknowledged
specifier|public
name|boolean
name|acknowledged
parameter_list|()
block|{
return|return
name|acknowledged
return|;
block|}
block|}
DECL|class|DeleteIndexResult
specifier|public
specifier|static
class|class
name|DeleteIndexResult
block|{
DECL|field|acknowledged
specifier|private
specifier|final
name|boolean
name|acknowledged
decl_stmt|;
DECL|method|DeleteIndexResult
specifier|public
name|DeleteIndexResult
parameter_list|(
name|boolean
name|acknowledged
parameter_list|)
block|{
name|this
operator|.
name|acknowledged
operator|=
name|acknowledged
expr_stmt|;
block|}
DECL|method|acknowledged
specifier|public
name|boolean
name|acknowledged
parameter_list|()
block|{
return|return
name|acknowledged
return|;
block|}
block|}
block|}
end_class

end_unit

