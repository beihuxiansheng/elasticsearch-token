begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.tribe
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
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
name|Lists
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
name|Maps
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
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeReadOperationAction
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
name|ConcurrentCollections
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
name|gateway
operator|.
name|GatewayService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|java
operator|.
name|util
operator|.
name|EnumSet
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

begin_comment
comment|/**  * The tribe service holds a list of node clients connected to a list of tribe members, and uses their  * cluster state events to update this local node cluster state with the merged view of it.  *<p/>  * The {@link #processSettings(org.elasticsearch.common.settings.Settings)} method should be called before  * starting the node, so it will make sure to configure this current node properly with the relevant tribe node  * settings.  *<p/>  * The tribe node settings make sure the discovery used is "local", but with no master elected. This means no  * write level master node operations will work ({@link org.elasticsearch.discovery.MasterNotDiscoveredException}  * will be thrown), and state level metadata operations with automatically use the local flag.  *<p/>  * The state merged from different clusters include the list of nodes, metadata, and routing table. Each node merged  * will have in its tribe which tribe member it came from. Each index merged will have in its settings which tribe  * member it came from. In case an index has already been merged from one cluster, and the same name index is discovered  * in another cluster, the conflict one will be discarded. This happens because we need to have the correct index name  * to propagate to the relevant cluster.  */
end_comment

begin_class
DECL|class|TribeService
specifier|public
class|class
name|TribeService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|TribeService
argument_list|>
block|{
DECL|field|TRIBE_METADATA_BLOCK
specifier|public
specifier|static
specifier|final
name|ClusterBlock
name|TRIBE_METADATA_BLOCK
init|=
operator|new
name|ClusterBlock
argument_list|(
literal|10
argument_list|,
literal|"tribe node, metadata not allowed"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|RestStatus
operator|.
name|BAD_REQUEST
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|TRIBE_WRITE_BLOCK
specifier|public
specifier|static
specifier|final
name|ClusterBlock
name|TRIBE_WRITE_BLOCK
init|=
operator|new
name|ClusterBlock
argument_list|(
literal|11
argument_list|,
literal|"tribe node, write not allowed"
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|RestStatus
operator|.
name|BAD_REQUEST
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|processSettings
specifier|public
specifier|static
name|Settings
name|processSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|settings
operator|.
name|get
argument_list|(
name|TRIBE_NAME
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// if its a node client started by this service as tribe, remove any tribe group setting
comment|// to avoid recursive configuration
name|ImmutableSettings
operator|.
name|Builder
name|sb
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|settings
operator|.
name|getAsMap
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"tribe."
argument_list|)
operator|&&
operator|!
name|s
operator|.
name|equals
argument_list|(
name|TRIBE_NAME
argument_list|)
condition|)
block|{
name|sb
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|build
argument_list|()
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|nodesSettings
init|=
name|settings
operator|.
name|getGroups
argument_list|(
literal|"tribe"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodesSettings
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|settings
return|;
block|}
comment|// its a tribe configured node..., force settings
name|ImmutableSettings
operator|.
name|Builder
name|sb
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|sb
operator|.
name|put
argument_list|(
literal|"node.client"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// this node should just act as a node client
name|sb
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"local"
argument_list|)
expr_stmt|;
comment|// a tribe node should not use zen discovery
name|sb
operator|.
name|put
argument_list|(
literal|"discovery.initial_state_timeout"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// nothing is going to be discovered, since no master will be elected
if|if
condition|(
name|sb
operator|.
name|get
argument_list|(
literal|"cluster.name"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|put
argument_list|(
literal|"cluster.name"
argument_list|,
literal|"tribe_"
operator|+
name|Strings
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
expr_stmt|;
comment|// make sure it won't join other tribe nodes in the same JVM
block|}
name|sb
operator|.
name|put
argument_list|(
name|TransportMasterNodeReadOperationAction
operator|.
name|FORCE_LOCAL_SETTING
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|build
argument_list|()
return|;
block|}
DECL|field|TRIBE_NAME
specifier|public
specifier|static
specifier|final
name|String
name|TRIBE_NAME
init|=
literal|"tribe.name"
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|blockIndicesWrite
specifier|private
specifier|final
name|String
index|[]
name|blockIndicesWrite
decl_stmt|;
DECL|field|blockIndicesRead
specifier|private
specifier|final
name|String
index|[]
name|blockIndicesRead
decl_stmt|;
DECL|field|blockIndicesMetadata
specifier|private
specifier|final
name|String
index|[]
name|blockIndicesMetadata
decl_stmt|;
DECL|field|ON_CONFLICT_ANY
DECL|field|ON_CONFLICT_DROP
DECL|field|ON_CONFLICT_PREFER
specifier|private
specifier|static
specifier|final
name|String
name|ON_CONFLICT_ANY
init|=
literal|"any"
decl_stmt|,
name|ON_CONFLICT_DROP
init|=
literal|"drop"
decl_stmt|,
name|ON_CONFLICT_PREFER
init|=
literal|"prefer_"
decl_stmt|;
DECL|field|onConflict
specifier|private
specifier|final
name|String
name|onConflict
decl_stmt|;
DECL|field|droppedIndices
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|droppedIndices
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentSet
argument_list|()
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
name|Lists
operator|.
name|newCopyOnWriteArrayList
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|TribeService
specifier|public
name|TribeService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|DiscoveryService
name|discoveryService
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
name|Map
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|nodesSettings
init|=
name|Maps
operator|.
name|newHashMap
argument_list|(
name|settings
operator|.
name|getGroups
argument_list|(
literal|"tribe"
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|nodesSettings
operator|.
name|remove
argument_list|(
literal|"blocks"
argument_list|)
expr_stmt|;
comment|// remove prefix settings that don't indicate a client
name|nodesSettings
operator|.
name|remove
argument_list|(
literal|"on_conflict"
argument_list|)
expr_stmt|;
comment|// remove prefix settings that don't indicate a client
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Settings
argument_list|>
name|entry
range|:
name|nodesSettings
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ImmutableSettings
operator|.
name|Builder
name|sb
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|.
name|put
argument_list|(
literal|"node.name"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|"/"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|put
argument_list|(
name|TRIBE_NAME
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|sb
operator|.
name|get
argument_list|(
literal|"http.enabled"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|sb
operator|.
name|put
argument_list|(
literal|"http.enabled"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|.
name|add
argument_list|(
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|settings
argument_list|(
name|sb
argument_list|)
operator|.
name|client
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|blockIndicesWrite
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
name|String
index|[]
name|blockIndicesRead
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
name|String
index|[]
name|blockIndicesMetadata
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
if|if
condition|(
operator|!
name|nodes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// remove the initial election / recovery blocks since we are not going to have a
comment|// master elected in this single tribe  node local "cluster"
name|clusterService
operator|.
name|removeInitialStateBlock
argument_list|(
name|discoveryService
operator|.
name|getNoMasterBlock
argument_list|()
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|removeInitialStateBlock
argument_list|(
name|GatewayService
operator|.
name|STATE_NOT_RECOVERED_BLOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"tribe.blocks.write"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|clusterService
operator|.
name|addInitialStateBlock
argument_list|(
name|TRIBE_WRITE_BLOCK
argument_list|)
expr_stmt|;
block|}
name|blockIndicesWrite
operator|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"tribe.blocks.write.indices"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
if|if
condition|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"tribe.blocks.metadata"
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|clusterService
operator|.
name|addInitialStateBlock
argument_list|(
name|TRIBE_METADATA_BLOCK
argument_list|)
expr_stmt|;
block|}
name|blockIndicesMetadata
operator|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"tribe.blocks.metadata.indices"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
name|blockIndicesRead
operator|=
name|settings
operator|.
name|getAsArray
argument_list|(
literal|"tribe.blocks.read.indices"
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|TribeClusterStateListener
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|blockIndicesMetadata
operator|=
name|blockIndicesMetadata
expr_stmt|;
name|this
operator|.
name|blockIndicesRead
operator|=
name|blockIndicesRead
expr_stmt|;
name|this
operator|.
name|blockIndicesWrite
operator|=
name|blockIndicesWrite
expr_stmt|;
name|this
operator|.
name|onConflict
operator|=
name|settings
operator|.
name|get
argument_list|(
literal|"tribe.on_conflict"
argument_list|,
name|ON_CONFLICT_ANY
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
throws|throws
name|ElasticsearchException
block|{
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
try|try
block|{
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// calling close is safe for non started nodes, we can just iterate over all
for|for
control|(
name|Node
name|otherNode
range|:
name|nodes
control|)
block|{
try|try
block|{
name|otherNode
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to close node {} on failed start"
argument_list|,
name|otherNode
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|e
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|e
throw|;
block|}
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
try|try
block|{
name|node
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to stop node {}"
argument_list|,
name|t
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
try|try
block|{
name|node
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to close node {}"
argument_list|,
name|t
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|TribeClusterStateListener
class|class
name|TribeClusterStateListener
implements|implements
name|ClusterStateListener
block|{
DECL|field|tribeNode
specifier|private
specifier|final
name|Node
name|tribeNode
decl_stmt|;
DECL|field|tribeName
specifier|private
specifier|final
name|String
name|tribeName
decl_stmt|;
DECL|method|TribeClusterStateListener
name|TribeClusterStateListener
parameter_list|(
name|Node
name|tribeNode
parameter_list|)
block|{
name|this
operator|.
name|tribeNode
operator|=
name|tribeNode
expr_stmt|;
name|this
operator|.
name|tribeName
operator|=
name|tribeNode
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|TRIBE_NAME
argument_list|)
expr_stmt|;
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
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] received cluster event, [{}]"
argument_list|,
name|tribeName
argument_list|,
name|event
operator|.
name|source
argument_list|()
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"cluster event from "
operator|+
name|tribeName
operator|+
literal|", "
operator|+
name|event
operator|.
name|source
argument_list|()
argument_list|,
operator|new
name|ClusterStateNonMasterUpdateTask
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
name|ClusterState
name|tribeState
init|=
name|event
operator|.
name|state
argument_list|()
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|currentState
operator|.
name|nodes
argument_list|()
argument_list|)
decl_stmt|;
comment|// -- merge nodes
comment|// go over existing nodes, and see if they need to be removed
for|for
control|(
name|DiscoveryNode
name|discoNode
range|:
name|currentState
operator|.
name|nodes
argument_list|()
control|)
block|{
name|String
name|markedTribeName
init|=
name|discoNode
operator|.
name|attributes
argument_list|()
operator|.
name|get
argument_list|(
name|TRIBE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|markedTribeName
operator|!=
literal|null
operator|&&
name|markedTribeName
operator|.
name|equals
argument_list|(
name|tribeName
argument_list|)
condition|)
block|{
if|if
condition|(
name|tribeState
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|discoNode
operator|.
name|id
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] removing node [{}]"
argument_list|,
name|tribeName
argument_list|,
name|discoNode
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|remove
argument_list|(
name|discoNode
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// go over tribe nodes, and see if they need to be added
for|for
control|(
name|DiscoveryNode
name|tribe
range|:
name|tribeState
operator|.
name|nodes
argument_list|()
control|)
block|{
if|if
condition|(
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|get
argument_list|(
name|tribe
operator|.
name|id
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// a new node, add it, but also add the tribe name to the attributes
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tribeAttr
init|=
name|MapBuilder
operator|.
name|newMapBuilder
argument_list|(
name|tribe
operator|.
name|attributes
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TRIBE_NAME
argument_list|,
name|tribeName
argument_list|)
operator|.
name|immutableMap
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|discoNode
init|=
operator|new
name|DiscoveryNode
argument_list|(
name|tribe
operator|.
name|name
argument_list|()
argument_list|,
name|tribe
operator|.
name|id
argument_list|()
argument_list|,
name|tribe
operator|.
name|getHostName
argument_list|()
argument_list|,
name|tribe
operator|.
name|getHostAddress
argument_list|()
argument_list|,
name|tribe
operator|.
name|address
argument_list|()
argument_list|,
name|tribeAttr
argument_list|,
name|tribe
operator|.
name|version
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] adding node [{}]"
argument_list|,
name|tribeName
argument_list|,
name|discoNode
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|discoNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|// -- merge metadata
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
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTable
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
comment|// go over existing indices, and see if they need to be removed
for|for
control|(
name|IndexMetaData
name|index
range|:
name|currentState
operator|.
name|metaData
argument_list|()
control|)
block|{
name|String
name|markedTribeName
init|=
name|index
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|TRIBE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|markedTribeName
operator|!=
literal|null
operator|&&
name|markedTribeName
operator|.
name|equals
argument_list|(
name|tribeName
argument_list|)
condition|)
block|{
name|IndexMetaData
name|tribeIndex
init|=
name|tribeState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tribeIndex
operator|==
literal|null
operator|||
name|tribeIndex
operator|.
name|state
argument_list|()
operator|==
name|IndexMetaData
operator|.
name|State
operator|.
name|CLOSE
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] removing index [{}]"
argument_list|,
name|tribeName
argument_list|,
name|index
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|removeIndex
argument_list|(
name|blocks
argument_list|,
name|metaData
argument_list|,
name|routingTable
argument_list|,
name|index
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// always make sure to update the metadata and routing table, in case
comment|// there are changes in them (new mapping, shards moving from initializing to started)
name|routingTable
operator|.
name|add
argument_list|(
name|tribeState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Settings
name|tribeSettings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|tribeIndex
operator|.
name|settings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TRIBE_NAME
argument_list|,
name|tribeName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metaData
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|tribeIndex
argument_list|)
operator|.
name|settings
argument_list|(
name|tribeSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// go over tribe one, and see if they need to be added
for|for
control|(
name|IndexMetaData
name|tribeIndex
range|:
name|tribeState
operator|.
name|metaData
argument_list|()
control|)
block|{
comment|// if there is no routing table yet, do nothing with it...
name|IndexRoutingTable
name|table
init|=
name|tribeState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
specifier|final
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
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|droppedIndices
operator|.
name|contains
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
comment|// a new index, add it, and add the tribe name as a setting
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] adding index [{}]"
argument_list|,
name|tribeName
argument_list|,
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|addNewIndex
argument_list|(
name|tribeState
argument_list|,
name|blocks
argument_list|,
name|metaData
argument_list|,
name|routingTable
argument_list|,
name|tribeIndex
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|existingFromTribe
init|=
name|indexMetaData
operator|.
name|getSettings
argument_list|()
operator|.
name|get
argument_list|(
name|TRIBE_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tribeName
operator|.
name|equals
argument_list|(
name|existingFromTribe
argument_list|)
condition|)
block|{
comment|// we have a potential conflict on index names, decide what to do...
if|if
condition|(
name|ON_CONFLICT_ANY
operator|.
name|equals
argument_list|(
name|onConflict
argument_list|)
condition|)
block|{
comment|// we chose any tribe, carry on
block|}
elseif|else
if|if
condition|(
name|ON_CONFLICT_DROP
operator|.
name|equals
argument_list|(
name|onConflict
argument_list|)
condition|)
block|{
comment|// drop the indices, there is a conflict
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] dropping index [{}] due to conflict with [{}]"
argument_list|,
name|tribeName
argument_list|,
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|,
name|existingFromTribe
argument_list|)
expr_stmt|;
name|removeIndex
argument_list|(
name|blocks
argument_list|,
name|metaData
argument_list|,
name|routingTable
argument_list|,
name|tribeIndex
argument_list|)
expr_stmt|;
name|droppedIndices
operator|.
name|add
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|onConflict
operator|.
name|startsWith
argument_list|(
name|ON_CONFLICT_PREFER
argument_list|)
condition|)
block|{
comment|// on conflict, prefer a tribe...
name|String
name|preferredTribeName
init|=
name|onConflict
operator|.
name|substring
argument_list|(
name|ON_CONFLICT_PREFER
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|tribeName
operator|.
name|equals
argument_list|(
name|preferredTribeName
argument_list|)
condition|)
block|{
comment|// the new one is hte preferred one, replace...
name|logger
operator|.
name|info
argument_list|(
literal|"[{}] adding index [{}], preferred over [{}]"
argument_list|,
name|tribeName
argument_list|,
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|,
name|existingFromTribe
argument_list|)
expr_stmt|;
name|removeIndex
argument_list|(
name|blocks
argument_list|,
name|metaData
argument_list|,
name|routingTable
argument_list|,
name|tribeIndex
argument_list|)
expr_stmt|;
name|addNewIndex
argument_list|(
name|tribeState
argument_list|,
name|blocks
argument_list|,
name|metaData
argument_list|,
name|routingTable
argument_list|,
name|tribeIndex
argument_list|)
expr_stmt|;
block|}
comment|// else: either the existing one is the preferred one, or we haven't seen one, carry on
block|}
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
name|blocks
argument_list|(
name|blocks
argument_list|)
operator|.
name|nodes
argument_list|(
name|nodes
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|void
name|removeIndex
parameter_list|(
name|ClusterBlocks
operator|.
name|Builder
name|blocks
parameter_list|,
name|MetaData
operator|.
name|Builder
name|metaData
parameter_list|,
name|RoutingTable
operator|.
name|Builder
name|routingTable
parameter_list|,
name|IndexMetaData
name|index
parameter_list|)
block|{
name|metaData
operator|.
name|remove
argument_list|(
name|index
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|routingTable
operator|.
name|remove
argument_list|(
name|index
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|blocks
operator|.
name|removeIndexBlocks
argument_list|(
name|index
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addNewIndex
parameter_list|(
name|ClusterState
name|tribeState
parameter_list|,
name|ClusterBlocks
operator|.
name|Builder
name|blocks
parameter_list|,
name|MetaData
operator|.
name|Builder
name|metaData
parameter_list|,
name|RoutingTable
operator|.
name|Builder
name|routingTable
parameter_list|,
name|IndexMetaData
name|tribeIndex
parameter_list|)
block|{
name|Settings
name|tribeSettings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|tribeIndex
operator|.
name|settings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|TRIBE_NAME
argument_list|,
name|tribeName
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|metaData
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|tribeIndex
argument_list|)
operator|.
name|settings
argument_list|(
name|tribeSettings
argument_list|)
argument_list|)
expr_stmt|;
name|routingTable
operator|.
name|add
argument_list|(
name|tribeState
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|blockIndicesMetadata
argument_list|,
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_METADATA_BLOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|blockIndicesRead
argument_list|,
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_BLOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|blockIndicesWrite
argument_list|,
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|)
condition|)
block|{
name|blocks
operator|.
name|addIndexBlock
argument_list|(
name|tribeIndex
operator|.
name|index
argument_list|()
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_WRITE_BLOCK
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
literal|"failed to process [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

