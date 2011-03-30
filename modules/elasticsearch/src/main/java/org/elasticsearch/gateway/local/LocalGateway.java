begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|IndexShardRoutingTable
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
name|MutableShardRouting
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
name|RoutingNode
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
name|compress
operator|.
name|lzf
operator|.
name|LZF
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
name|compress
operator|.
name|lzf
operator|.
name|LZFOutputStream
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
name|io
operator|.
name|Streams
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
name|BytesStreamInput
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
name|CachedStreamInput
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
name|LZFStreamInput
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
name|xcontent
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
name|index
operator|.
name|gateway
operator|.
name|local
operator|.
name|LocalIndexGatewayModule
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|ExecutorService
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|EsExecutors
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
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
DECL|field|location
specifier|private
name|File
name|location
decl_stmt|;
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
DECL|field|listGatewayMetaState
specifier|private
specifier|final
name|TransportNodesListGatewayMetaState
name|listGatewayMetaState
decl_stmt|;
DECL|field|listGatewayStartedShards
specifier|private
specifier|final
name|TransportNodesListGatewayStartedShards
name|listGatewayStartedShards
decl_stmt|;
DECL|field|compress
specifier|private
specifier|final
name|boolean
name|compress
decl_stmt|;
DECL|field|currentMetaState
specifier|private
specifier|volatile
name|LocalGatewayMetaState
name|currentMetaState
decl_stmt|;
DECL|field|currentStartedShards
specifier|private
specifier|volatile
name|LocalGatewayStartedShards
name|currentStartedShards
decl_stmt|;
DECL|field|executor
specifier|private
specifier|volatile
name|ExecutorService
name|executor
decl_stmt|;
DECL|field|initialized
specifier|private
specifier|volatile
name|boolean
name|initialized
init|=
literal|false
decl_stmt|;
DECL|method|LocalGateway
annotation|@
name|Inject
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
name|TransportNodesListGatewayMetaState
name|listGatewayMetaState
parameter_list|,
name|TransportNodesListGatewayStartedShards
name|listGatewayStartedShards
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
name|listGatewayMetaState
operator|=
name|listGatewayMetaState
operator|.
name|initGateway
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|listGatewayStartedShards
operator|=
name|listGatewayStartedShards
operator|.
name|initGateway
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|compress
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"compress"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|type
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"local"
return|;
block|}
DECL|method|currentMetaState
specifier|public
name|LocalGatewayMetaState
name|currentMetaState
parameter_list|()
block|{
name|lazyInitialize
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|currentMetaState
return|;
block|}
DECL|method|currentStartedShards
specifier|public
name|LocalGatewayStartedShards
name|currentStartedShards
parameter_list|()
block|{
name|lazyInitialize
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|currentStartedShards
return|;
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
name|this
operator|.
name|executor
operator|=
name|newSingleThreadExecutor
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"gateway"
argument_list|)
argument_list|)
expr_stmt|;
name|lazyInitialize
argument_list|()
expr_stmt|;
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
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|executor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
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
block|{     }
DECL|method|performStateRecovery
annotation|@
name|Override
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
name|Set
argument_list|<
name|String
argument_list|>
name|nodesIds
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|nodesIds
operator|.
name|addAll
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
name|keySet
argument_list|()
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
argument_list|,
literal|null
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
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
name|TransportNodesListGatewayMetaState
operator|.
name|NodeLocalGatewayMetaState
name|electedState
init|=
literal|null
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
name|state
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|electedState
operator|==
literal|null
condition|)
block|{
name|electedState
operator|=
name|nodeState
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nodeState
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
operator|>
name|electedState
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
condition|)
block|{
name|electedState
operator|=
name|nodeState
expr_stmt|;
block|}
block|}
if|if
condition|(
name|electedState
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"no state elected"
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onSuccess
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"elected state from [{}]"
argument_list|,
name|electedState
operator|.
name|node
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onSuccess
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|()
operator|.
name|version
argument_list|(
name|electedState
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|metaData
argument_list|(
name|electedState
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|suggestIndexGateway
annotation|@
name|Override
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
DECL|method|reset
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|Exception
block|{
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|nodeEnv
operator|.
name|nodeDataLocation
argument_list|()
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
specifier|final
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
comment|// the location is set to null, so we should not store it (for example, its not a data/master node)
if|if
condition|(
name|location
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|// nothing to do until we actually recover from the gateway or any other block indicates we need to disable persistency
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|blocks
argument_list|()
operator|.
name|disableStatePersistence
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// we only write the local metadata if this is a possible master node, the metadata has changed, and
comment|// we don't have a NO_MASTER block (in which case, the routing is cleaned, and we don't want to override what
comment|// we have now, since it might be needed when later on performing full state recovery)
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|&&
name|event
operator|.
name|metaDataChanged
argument_list|()
condition|)
block|{
name|executor
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
name|LocalGatewayMetaState
operator|.
name|Builder
name|builder
init|=
name|LocalGatewayMetaState
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentMetaState
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|state
argument_list|(
name|currentMetaState
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|version
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|metaData
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|LocalGatewayMetaState
name|stateToWrite
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|XContentBuilder
name|xContentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|LocalGatewayMetaState
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|stateToWrite
argument_list|,
name|xContentBuilder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|File
name|stateFile
init|=
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"metadata-"
operator|+
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|stateFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|compress
condition|)
block|{
name|fos
operator|=
operator|new
name|LZFOutputStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
name|fos
operator|.
name|write
argument_list|(
name|xContentBuilder
operator|.
name|unsafeBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|xContentBuilder
operator|.
name|unsafeBytesLength
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileSystemUtils
operator|.
name|syncFile
argument_list|(
name|stateFile
argument_list|)
expr_stmt|;
name|currentMetaState
operator|=
name|stateToWrite
expr_stmt|;
comment|// delete all the other files
name|File
index|[]
name|files
init|=
name|location
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"metadata-"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"metadata-"
operator|+
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to write updated state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|dataNode
argument_list|()
operator|&&
name|event
operator|.
name|routingTableChanged
argument_list|()
condition|)
block|{
name|executor
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
name|LocalGatewayStartedShards
operator|.
name|Builder
name|builder
init|=
name|LocalGatewayStartedShards
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentStartedShards
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|state
argument_list|(
name|currentStartedShards
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|version
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove from the current state all the shards that are primary and started somewhere, we won't need them anymore
comment|// and if they are still here, we will add them in the next phase
comment|// Also note, this works well when closing an index, since a closed index will have no routing shards entries
comment|// so they won't get removed (we want to keep the fact that those shards are allocated on this node if needed)
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|event
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
control|)
block|{
for|for
control|(
name|IndexShardRoutingTable
name|indexShardRoutingTable
range|:
name|indexRoutingTable
control|)
block|{
if|if
condition|(
name|indexShardRoutingTable
operator|.
name|primaryShard
argument_list|()
operator|.
name|active
argument_list|()
condition|)
block|{
name|builder
operator|.
name|remove
argument_list|(
name|indexShardRoutingTable
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// now, add all the ones that are active and on this node
name|RoutingNode
name|routingNode
init|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|readOnlyRoutingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|routingNode
operator|!=
literal|null
condition|)
block|{
comment|// out node is not in play yet...
for|for
control|(
name|MutableShardRouting
name|shardRouting
range|:
name|routingNode
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|active
argument_list|()
condition|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|LocalGatewayStartedShards
name|stateToWrite
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
name|XContentBuilder
name|xContentBuilder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|LocalGatewayStartedShards
operator|.
name|Builder
operator|.
name|toXContent
argument_list|(
name|stateToWrite
argument_list|,
name|xContentBuilder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|File
name|stateFile
init|=
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"shards-"
operator|+
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|OutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|stateFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|compress
condition|)
block|{
name|fos
operator|=
operator|new
name|LZFOutputStream
argument_list|(
name|fos
argument_list|)
expr_stmt|;
block|}
name|fos
operator|.
name|write
argument_list|(
name|xContentBuilder
operator|.
name|unsafeBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|xContentBuilder
operator|.
name|unsafeBytesLength
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileSystemUtils
operator|.
name|syncFile
argument_list|(
name|stateFile
argument_list|)
expr_stmt|;
name|currentStartedShards
operator|=
name|stateToWrite
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to write updated state"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// delete all the other files
name|File
index|[]
name|files
init|=
name|location
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|name
operator|.
name|startsWith
argument_list|(
literal|"shards-"
argument_list|)
operator|&&
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"shards-"
operator|+
name|event
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * We do here lazy initialization on not only on start(), since we might be called before start by another node (really will      * happen in term of timing in testing, but still), and we want to return the cluster state when we can.      *      * It is synchronized since we want to wait for it to be loaded if called concurrently. There should really be a nicer      * solution here, but for now, its good enough.      */
DECL|method|lazyInitialize
specifier|private
specifier|synchronized
name|void
name|lazyInitialize
parameter_list|()
block|{
if|if
condition|(
name|initialized
condition|)
block|{
return|return;
block|}
name|initialized
operator|=
literal|true
expr_stmt|;
comment|// if this is not a possible master node or data node, bail, we won't save anything here...
if|if
condition|(
operator|!
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|&&
operator|!
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|dataNode
argument_list|()
condition|)
block|{
name|location
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// create the location where the state will be stored
name|this
operator|.
name|location
operator|=
operator|new
name|File
argument_list|(
name|nodeEnv
operator|.
name|nodeDataLocation
argument_list|()
argument_list|,
literal|"_state"
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
if|if
condition|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|masterNode
argument_list|()
condition|)
block|{
try|try
block|{
name|long
name|version
init|=
name|findLatestMetaStateVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|currentMetaState
operator|=
name|readMetaState
argument_list|(
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"metadata-"
operator|+
name|version
argument_list|)
argument_list|)
argument_list|)
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
literal|"failed to read local state (metadata)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|dataNode
argument_list|()
condition|)
block|{
try|try
block|{
name|long
name|version
init|=
name|findLatestStartedShardsVersion
argument_list|()
decl_stmt|;
if|if
condition|(
name|version
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|currentStartedShards
operator|=
name|readStartedShards
argument_list|(
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"shards-"
operator|+
name|version
argument_list|)
argument_list|)
argument_list|)
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
literal|"failed to read local state (started shards)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|findLatestStartedShardsVersion
specifier|private
name|long
name|findLatestStartedShardsVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|index
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|File
name|stateFile
range|:
name|location
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[findLatestState]: Processing ["
operator|+
name|stateFile
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"shards-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|long
name|fileIndex
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileIndex
operator|>=
name|index
condition|)
block|{
comment|// try and read the meta data
try|try
block|{
name|readStartedShards
argument_list|(
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|stateFile
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|fileIndex
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[findLatestState]: Failed to read state from ["
operator|+
name|name
operator|+
literal|"], ignoring..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|index
return|;
block|}
DECL|method|findLatestMetaStateVersion
specifier|private
name|long
name|findLatestMetaStateVersion
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|index
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|File
name|stateFile
range|:
name|location
operator|.
name|listFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[findLatestState]: Processing ["
operator|+
name|stateFile
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|String
name|name
init|=
name|stateFile
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|name
operator|.
name|startsWith
argument_list|(
literal|"metadata-"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|long
name|fileIndex
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileIndex
operator|>=
name|index
condition|)
block|{
comment|// try and read the meta data
try|try
block|{
name|readMetaState
argument_list|(
name|Streams
operator|.
name|copyToByteArray
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|stateFile
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|index
operator|=
name|fileIndex
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"[findLatestState]: Failed to read state from ["
operator|+
name|name
operator|+
literal|"], ignoring..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|index
return|;
block|}
DECL|method|readMetaState
specifier|private
name|LocalGatewayMetaState
name|readMetaState
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|LZF
operator|.
name|isCompressed
argument_list|(
name|data
argument_list|)
condition|)
block|{
name|BytesStreamInput
name|siBytes
init|=
operator|new
name|BytesStreamInput
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|LZFStreamInput
name|siLzf
init|=
name|CachedStreamInput
operator|.
name|cachedLzf
argument_list|(
name|siBytes
argument_list|)
decl_stmt|;
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|siLzf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|LocalGatewayMetaState
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|readStartedShards
specifier|private
name|LocalGatewayStartedShards
name|readStartedShards
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|LZF
operator|.
name|isCompressed
argument_list|(
name|data
argument_list|)
condition|)
block|{
name|BytesStreamInput
name|siBytes
init|=
operator|new
name|BytesStreamInput
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|LZFStreamInput
name|siLzf
init|=
name|CachedStreamInput
operator|.
name|cachedLzf
argument_list|(
name|siBytes
argument_list|)
decl_stmt|;
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|siLzf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|LocalGatewayStartedShards
operator|.
name|Builder
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|parser
operator|!=
literal|null
condition|)
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

