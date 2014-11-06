begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
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
name|ObjectFloatOpenHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectOpenHashSet
import|;
end_import

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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

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
name|FailedNodeException
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
name|inject
operator|.
name|Module
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
name|FileSystemUtils
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
name|env
operator|.
name|NodeEnvironment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|Gateway
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|GatewayException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|meta
operator|.
name|LocalGatewayMetaState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|meta
operator|.
name|TransportNodesListGatewayMetaState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|shards
operator|.
name|LocalGatewayShardsState
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
name|gateway
operator|.
name|local
operator|.
name|LocalIndexGatewayModule
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LocalGateway
specifier|public
class|class
name|LocalGateway
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Gateway
argument_list|>
implements|implements
name|Gateway
implements|,
name|ClusterStateListener
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|nodeEnv
specifier|private
specifier|final
name|NodeEnvironment
name|nodeEnv
decl_stmt|;
DECL|field|shardsState
specifier|private
specifier|final
name|LocalGatewayShardsState
name|shardsState
decl_stmt|;
DECL|field|metaState
specifier|private
specifier|final
name|LocalGatewayMetaState
name|metaState
decl_stmt|;
DECL|field|listGatewayMetaState
specifier|private
specifier|final
name|TransportNodesListGatewayMetaState
name|listGatewayMetaState
decl_stmt|;
DECL|field|initialMeta
specifier|private
specifier|final
name|String
name|initialMeta
decl_stmt|;
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalGateway
specifier|public
name|LocalGateway
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|,
name|LocalGatewayShardsState
name|shardsState
parameter_list|,
name|LocalGatewayMetaState
name|metaState
parameter_list|,
name|TransportNodesListGatewayMetaState
name|listGatewayMetaState
parameter_list|,
name|ClusterName
name|clusterName
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
name|nodeEnv
operator|=
name|nodeEnv
expr_stmt|;
name|this
operator|.
name|metaState
operator|=
name|metaState
expr_stmt|;
name|this
operator|.
name|listGatewayMetaState
operator|=
name|listGatewayMetaState
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|shardsState
operator|=
name|shardsState
expr_stmt|;
name|clusterService
operator|.
name|addLast
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// we define what is our minimum "master" nodes, use that to allow for recovery
name|this
operator|.
name|initialMeta
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"initial_meta"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"discovery.zen.minimum_master_nodes"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"local"
return|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
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
DECL|method|performStateRecovery
specifier|public
name|void
name|performStateRecovery
parameter_list|(
specifier|final
name|GatewayStateRecoveredListener
name|listener
parameter_list|)
throws|throws
name|GatewayException
block|{
name|ObjectOpenHashSet
argument_list|<
name|String
argument_list|>
name|nodesIds
init|=
name|ObjectOpenHashSet
operator|.
name|from
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodes
argument_list|()
operator|.
name|keys
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"performing state recovery from {}"
argument_list|,
name|nodesIds
argument_list|)
expr_stmt|;
name|TransportNodesListGatewayMetaState
operator|.
name|NodesLocalGatewayMetaState
name|nodesState
init|=
name|listGatewayMetaState
operator|.
name|list
argument_list|(
name|nodesIds
operator|.
name|toArray
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
literal|null
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|int
name|requiredAllocation
init|=
literal|1
decl_stmt|;
try|try
block|{
if|if
condition|(
literal|"quorum"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
condition|)
block|{
if|if
condition|(
name|nodesIds
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|requiredAllocation
operator|=
operator|(
name|nodesIds
operator|.
name|size
argument_list|()
operator|/
literal|2
operator|)
operator|+
literal|1
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"quorum-1"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
operator|||
literal|"half"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
condition|)
block|{
if|if
condition|(
name|nodesIds
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|requiredAllocation
operator|=
operator|(
operator|(
literal|1
operator|+
name|nodesIds
operator|.
name|size
argument_list|()
operator|)
operator|/
literal|2
operator|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"one"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
condition|)
block|{
name|requiredAllocation
operator|=
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"full"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
operator|||
literal|"all"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
condition|)
block|{
name|requiredAllocation
operator|=
name|nodesIds
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"full-1"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
operator|||
literal|"all-1"
operator|.
name|equals
argument_list|(
name|initialMeta
argument_list|)
condition|)
block|{
if|if
condition|(
name|nodesIds
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|requiredAllocation
operator|=
name|nodesIds
operator|.
name|size
argument_list|()
operator|-
literal|1
expr_stmt|;
block|}
block|}
else|else
block|{
name|requiredAllocation
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|initialMeta
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
literal|"failed to derived initial_meta from value {}"
argument_list|,
name|initialMeta
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodesState
operator|.
name|failures
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|FailedNodeException
name|failedNodeException
range|:
name|nodesState
operator|.
name|failures
argument_list|()
control|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to fetch state from node"
argument_list|,
name|failedNodeException
argument_list|)
expr_stmt|;
block|}
block|}
name|ObjectFloatOpenHashMap
argument_list|<
name|String
argument_list|>
name|indices
init|=
operator|new
name|ObjectFloatOpenHashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|MetaData
name|electedGlobalState
init|=
literal|null
decl_stmt|;
name|int
name|found
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TransportNodesListGatewayMetaState
operator|.
name|NodeLocalGatewayMetaState
name|nodeState
range|:
name|nodesState
control|)
block|{
if|if
condition|(
name|nodeState
operator|.
name|metaData
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|found
operator|++
expr_stmt|;
if|if
condition|(
name|electedGlobalState
operator|==
literal|null
condition|)
block|{
name|electedGlobalState
operator|=
name|nodeState
operator|.
name|metaData
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeState
operator|.
name|metaData
argument_list|()
operator|.
name|version
argument_list|()
operator|>
name|electedGlobalState
operator|.
name|version
argument_list|()
condition|)
block|{
name|electedGlobalState
operator|=
name|nodeState
operator|.
name|metaData
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|ObjectCursor
argument_list|<
name|IndexMetaData
argument_list|>
name|cursor
range|:
name|nodeState
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
name|indices
operator|.
name|addTo
argument_list|(
name|cursor
operator|.
name|value
operator|.
name|index
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|found
operator|<
name|requiredAllocation
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
literal|"found ["
operator|+
name|found
operator|+
literal|"] metadata states, required ["
operator|+
name|requiredAllocation
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// update the global state, and clean the indices, we elect them in the next phase
name|MetaData
operator|.
name|Builder
name|metaDataBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|(
name|electedGlobalState
argument_list|)
operator|.
name|removeAllIndices
argument_list|()
decl_stmt|;
specifier|final
name|boolean
index|[]
name|states
init|=
name|indices
operator|.
name|allocated
decl_stmt|;
specifier|final
name|Object
index|[]
name|keys
init|=
name|indices
operator|.
name|keys
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
name|states
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|states
index|[
name|i
index|]
condition|)
block|{
name|String
name|index
init|=
operator|(
name|String
operator|)
name|keys
index|[
name|i
index|]
decl_stmt|;
name|IndexMetaData
name|electedIndexMetaData
init|=
literal|null
decl_stmt|;
name|int
name|indexMetaDataCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TransportNodesListGatewayMetaState
operator|.
name|NodeLocalGatewayMetaState
name|nodeState
range|:
name|nodesState
control|)
block|{
if|if
condition|(
name|nodeState
operator|.
name|metaData
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|IndexMetaData
name|indexMetaData
init|=
name|nodeState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|electedIndexMetaData
operator|==
literal|null
condition|)
block|{
name|electedIndexMetaData
operator|=
name|indexMetaData
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|indexMetaData
operator|.
name|version
argument_list|()
operator|>
name|electedIndexMetaData
operator|.
name|version
argument_list|()
condition|)
block|{
name|electedIndexMetaData
operator|=
name|indexMetaData
expr_stmt|;
block|}
name|indexMetaDataCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|electedIndexMetaData
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|indexMetaDataCount
operator|<
name|requiredAllocation
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] found [{}], required [{}], not adding"
argument_list|,
name|index
argument_list|,
name|indexMetaDataCount
argument_list|,
name|requiredAllocation
argument_list|)
expr_stmt|;
block|}
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|electedIndexMetaData
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ClusterState
operator|.
name|Builder
name|builder
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
name|builder
operator|.
name|metaData
argument_list|(
name|metaDataBuilder
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onSuccess
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|suggestIndexGateway
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Module
argument_list|>
name|suggestIndexGateway
parameter_list|()
block|{
return|return
name|LocalIndexGatewayModule
operator|.
name|class
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|IOUtils
operator|.
name|rm
argument_list|(
name|FileSystemUtils
operator|.
name|toPaths
argument_list|(
name|nodeEnv
operator|.
name|nodeDataLocations
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to delete shard locations"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// order is important, first metaState, and then shardsState
comment|// so dangling indices will be recorded
name|metaState
operator|.
name|clusterChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|shardsState
operator|.
name|clusterChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

