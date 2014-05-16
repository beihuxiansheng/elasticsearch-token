begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|local
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
name|base
operator|.
name|Objects
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
name|ElasticsearchIllegalStateException
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
name|node
operator|.
name|DiscoveryNodeService
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
name|internal
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
name|*
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
name|service
operator|.
name|NodeService
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
name|Queue
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
name|ConcurrentMap
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
name|CopyOnWriteArrayList
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
name|atomic
operator|.
name|AtomicBoolean
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
name|Sets
operator|.
name|newHashSet
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
name|Builder
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LocalDiscovery
specifier|public
class|class
name|LocalDiscovery
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Discovery
argument_list|>
implements|implements
name|Discovery
block|{
DECL|field|NO_MEMBERS
specifier|private
specifier|static
specifier|final
name|LocalDiscovery
index|[]
name|NO_MEMBERS
init|=
operator|new
name|LocalDiscovery
index|[
literal|0
index|]
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|discoveryNodeService
specifier|private
specifier|final
name|DiscoveryNodeService
name|discoveryNodeService
decl_stmt|;
DECL|field|allocationService
specifier|private
name|AllocationService
name|allocationService
decl_stmt|;
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|discoverySettings
specifier|private
specifier|final
name|DiscoverySettings
name|discoverySettings
decl_stmt|;
DECL|field|localNode
specifier|private
name|DiscoveryNode
name|localNode
decl_stmt|;
DECL|field|master
specifier|private
specifier|volatile
name|boolean
name|master
init|=
literal|false
decl_stmt|;
DECL|field|initialStateSent
specifier|private
specifier|final
name|AtomicBoolean
name|initialStateSent
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|initialStateListeners
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|InitialStateDiscoveryListener
argument_list|>
name|initialStateListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|clusterGroups
specifier|private
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|ClusterName
argument_list|,
name|ClusterGroup
argument_list|>
name|clusterGroups
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalDiscovery
specifier|public
name|LocalDiscovery
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|DiscoveryNodeService
name|discoveryNodeService
parameter_list|,
name|Version
name|version
parameter_list|,
name|DiscoverySettings
name|discoverySettings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|discoveryNodeService
operator|=
name|discoveryNodeService
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|discoverySettings
operator|=
name|discoverySettings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNodeService
specifier|public
name|void
name|setNodeService
parameter_list|(
annotation|@
name|Nullable
name|NodeService
name|nodeService
parameter_list|)
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
DECL|method|setAllocationService
specifier|public
name|void
name|setAllocationService
parameter_list|(
name|AllocationService
name|allocationService
parameter_list|)
block|{
name|this
operator|.
name|allocationService
operator|=
name|allocationService
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
synchronized|synchronized
init|(
name|clusterGroups
init|)
block|{
name|ClusterGroup
name|clusterGroup
init|=
name|clusterGroups
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterGroup
operator|==
literal|null
condition|)
block|{
name|clusterGroup
operator|=
operator|new
name|ClusterGroup
argument_list|()
expr_stmt|;
name|clusterGroups
operator|.
name|put
argument_list|(
name|clusterName
argument_list|,
name|clusterGroup
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Connected to cluster [{}]"
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
name|this
operator|.
name|localNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|DiscoveryService
operator|.
name|generateNodeId
argument_list|(
name|settings
argument_list|)
argument_list|,
name|transportService
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|,
name|discoveryNodeService
operator|.
name|buildAttributes
argument_list|()
argument_list|,
name|version
argument_list|)
expr_stmt|;
name|clusterGroup
operator|.
name|members
argument_list|()
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|LocalDiscovery
name|firstMaster
init|=
literal|null
decl_stmt|;
for|for
control|(
name|LocalDiscovery
name|localDiscovery
range|:
name|clusterGroup
operator|.
name|members
argument_list|()
control|)
block|{
if|if
condition|(
name|localDiscovery
operator|.
name|localNode
argument_list|()
operator|.
name|masterNode
argument_list|()
condition|)
block|{
name|firstMaster
operator|=
name|localDiscovery
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|firstMaster
operator|!=
literal|null
operator|&&
name|firstMaster
operator|.
name|equals
argument_list|(
name|this
argument_list|)
condition|)
block|{
comment|// we are the first master (and the master)
name|master
operator|=
literal|true
expr_stmt|;
specifier|final
name|LocalDiscovery
name|master
init|=
name|firstMaster
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-disco-initial_connect(master)"
argument_list|,
operator|new
name|ProcessedClusterStateNonMasterUpdateTask
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
name|DiscoveryNodes
operator|.
name|Builder
name|nodesBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|LocalDiscovery
name|discovery
range|:
name|clusterGroups
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
operator|.
name|members
argument_list|()
control|)
block|{
name|nodesBuilder
operator|.
name|put
argument_list|(
name|discovery
operator|.
name|localNode
argument_list|)
expr_stmt|;
block|}
name|nodesBuilder
operator|.
name|localNodeId
argument_list|(
name|master
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|masterNodeId
argument_list|(
name|master
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove the NO_MASTER block in this case
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
operator|.
name|removeGlobalBlock
argument_list|(
name|discoverySettings
operator|.
name|getNoMasterBlock
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
argument_list|)
operator|.
name|nodes
argument_list|(
name|nodesBuilder
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
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
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
block|{
name|sendInitialStateEventIfNeeded
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|firstMaster
operator|!=
literal|null
condition|)
block|{
comment|// update as fast as we can the local node state with the new metadata (so we create indices for example)
specifier|final
name|ClusterState
name|masterState
init|=
name|firstMaster
operator|.
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-disco(detected_master)"
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
block|{
comment|// make sure we have the local node id set, we might need it as a result of the new metadata
name|DiscoveryNodes
operator|.
name|Builder
name|nodesBuilder
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
operator|.
name|put
argument_list|(
name|localNode
argument_list|)
operator|.
name|localNodeId
argument_list|(
name|localNode
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
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
name|masterState
operator|.
name|metaData
argument_list|()
argument_list|)
operator|.
name|nodes
argument_list|(
name|nodesBuilder
argument_list|)
operator|.
name|build
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
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
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
comment|// tell the master to send the fact that we are here
specifier|final
name|LocalDiscovery
name|master
init|=
name|firstMaster
decl_stmt|;
name|firstMaster
operator|.
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-disco-receive(from node["
operator|+
name|localNode
operator|+
literal|"])"
argument_list|,
operator|new
name|ProcessedClusterStateNonMasterUpdateTask
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
name|DiscoveryNodes
operator|.
name|Builder
name|nodesBuilder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|LocalDiscovery
name|discovery
range|:
name|clusterGroups
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
operator|.
name|members
argument_list|()
control|)
block|{
name|nodesBuilder
operator|.
name|put
argument_list|(
name|discovery
operator|.
name|localNode
argument_list|)
expr_stmt|;
block|}
name|nodesBuilder
operator|.
name|localNodeId
argument_list|(
name|master
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|masterNodeId
argument_list|(
name|master
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|currentState
argument_list|)
operator|.
name|nodes
argument_list|(
name|nodesBuilder
argument_list|)
operator|.
name|build
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
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
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
block|{
name|sendInitialStateEventIfNeeded
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
comment|// else, no master node, the next node that will start will fill things in...
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
synchronized|synchronized
init|(
name|clusterGroups
init|)
block|{
name|ClusterGroup
name|clusterGroup
init|=
name|clusterGroups
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterGroup
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Illegal state, should not have an empty cluster group when stopping, I should be there at teh very least..."
argument_list|)
expr_stmt|;
return|return;
block|}
name|clusterGroup
operator|.
name|members
argument_list|()
operator|.
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|clusterGroup
operator|.
name|members
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no more members, remove and return
name|clusterGroups
operator|.
name|remove
argument_list|(
name|clusterName
argument_list|)
expr_stmt|;
return|return;
block|}
name|LocalDiscovery
name|firstMaster
init|=
literal|null
decl_stmt|;
for|for
control|(
name|LocalDiscovery
name|localDiscovery
range|:
name|clusterGroup
operator|.
name|members
argument_list|()
control|)
block|{
if|if
condition|(
name|localDiscovery
operator|.
name|localNode
argument_list|()
operator|.
name|masterNode
argument_list|()
condition|)
block|{
name|firstMaster
operator|=
name|localDiscovery
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|firstMaster
operator|!=
literal|null
condition|)
block|{
comment|// if the removed node is the master, make the next one as the master
if|if
condition|(
name|master
condition|)
block|{
name|firstMaster
operator|.
name|master
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|newMembers
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|LocalDiscovery
name|discovery
range|:
name|clusterGroup
operator|.
name|members
argument_list|()
control|)
block|{
name|newMembers
operator|.
name|add
argument_list|(
name|discovery
operator|.
name|localNode
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LocalDiscovery
name|master
init|=
name|firstMaster
decl_stmt|;
name|master
operator|.
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-disco-update"
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
block|{
name|DiscoveryNodes
name|newNodes
init|=
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|removeDeadMembers
argument_list|(
name|newMembers
argument_list|,
name|master
operator|.
name|localNode
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
name|DiscoveryNodes
operator|.
name|Delta
name|delta
init|=
name|newNodes
operator|.
name|delta
argument_list|(
name|currentState
operator|.
name|nodes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|.
name|added
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"No new nodes should be created when a new discovery view is accepted"
argument_list|)
expr_stmt|;
block|}
comment|// reroute here, so we eagerly remove dead nodes from the routing
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
name|nodes
argument_list|(
name|newNodes
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|master
operator|.
name|allocationService
operator|.
name|reroute
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
name|updatedState
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
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
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
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
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
annotation|@
name|Override
DECL|method|localNode
specifier|public
name|DiscoveryNode
name|localNode
parameter_list|()
block|{
return|return
name|localNode
return|;
block|}
annotation|@
name|Override
DECL|method|addListener
specifier|public
name|void
name|addListener
parameter_list|(
name|InitialStateDiscoveryListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|initialStateListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeListener
specifier|public
name|void
name|removeListener
parameter_list|(
name|InitialStateDiscoveryListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|initialStateListeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|nodeDescription
specifier|public
name|String
name|nodeDescription
parameter_list|()
block|{
return|return
name|clusterName
operator|.
name|value
argument_list|()
operator|+
literal|"/"
operator|+
name|localNode
operator|.
name|id
argument_list|()
return|;
block|}
DECL|method|publish
specifier|public
name|void
name|publish
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|Discovery
operator|.
name|AckListener
name|ackListener
parameter_list|)
block|{
if|if
condition|(
operator|!
name|master
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Shouldn't publish state when not master"
argument_list|)
throw|;
block|}
name|LocalDiscovery
index|[]
name|members
init|=
name|members
argument_list|()
decl_stmt|;
if|if
condition|(
name|members
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|publish
argument_list|(
name|members
argument_list|,
name|clusterState
argument_list|,
operator|new
name|AckClusterStatePublishResponseHandler
argument_list|(
name|members
operator|.
name|length
operator|-
literal|1
argument_list|,
name|ackListener
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|members
specifier|private
name|LocalDiscovery
index|[]
name|members
parameter_list|()
block|{
name|ClusterGroup
name|clusterGroup
init|=
name|clusterGroups
operator|.
name|get
argument_list|(
name|clusterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|clusterGroup
operator|==
literal|null
condition|)
block|{
return|return
name|NO_MEMBERS
return|;
block|}
name|Queue
argument_list|<
name|LocalDiscovery
argument_list|>
name|members
init|=
name|clusterGroup
operator|.
name|members
argument_list|()
decl_stmt|;
return|return
name|members
operator|.
name|toArray
argument_list|(
operator|new
name|LocalDiscovery
index|[
name|members
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|method|publish
specifier|private
name|void
name|publish
parameter_list|(
name|LocalDiscovery
index|[]
name|members
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|ClusterStatePublishResponseHandler
name|publishResponseHandler
parameter_list|)
block|{
try|try
block|{
comment|// we do the marshaling intentionally, to check it works well...
specifier|final
name|byte
index|[]
name|clusterStateBytes
init|=
name|Builder
operator|.
name|toBytes
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|LocalDiscovery
name|discovery
range|:
name|members
control|)
block|{
if|if
condition|(
name|discovery
operator|.
name|master
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ClusterState
name|nodeSpecificClusterState
init|=
name|ClusterState
operator|.
name|Builder
operator|.
name|fromBytes
argument_list|(
name|clusterStateBytes
argument_list|,
name|discovery
operator|.
name|localNode
argument_list|)
decl_stmt|;
name|nodeSpecificClusterState
operator|.
name|status
argument_list|(
name|ClusterState
operator|.
name|ClusterStateStatus
operator|.
name|RECEIVED
argument_list|)
expr_stmt|;
comment|// ignore cluster state messages that do not include "me", not in the game yet...
if|if
condition|(
name|nodeSpecificClusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|discovery
operator|.
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"local-disco-receive(from master)"
argument_list|,
operator|new
name|ProcessedClusterStateNonMasterUpdateTask
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
if|if
condition|(
name|nodeSpecificClusterState
operator|.
name|version
argument_list|()
operator|<
name|currentState
operator|.
name|version
argument_list|()
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|nodeSpecificClusterState
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|currentState
return|;
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
name|nodeSpecificClusterState
argument_list|)
decl_stmt|;
comment|// if the routing table did not change, use the original one
if|if
condition|(
name|nodeSpecificClusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|version
argument_list|()
operator|==
name|currentState
operator|.
name|routingTable
argument_list|()
operator|.
name|version
argument_list|()
condition|)
block|{
name|builder
operator|.
name|routingTable
argument_list|(
name|currentState
operator|.
name|routingTable
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeSpecificClusterState
operator|.
name|metaData
argument_list|()
operator|.
name|version
argument_list|()
operator|==
name|currentState
operator|.
name|metaData
argument_list|()
operator|.
name|version
argument_list|()
condition|)
block|{
name|builder
operator|.
name|metaData
argument_list|(
name|currentState
operator|.
name|metaData
argument_list|()
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
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|publishResponseHandler
operator|.
name|onFailure
argument_list|(
name|discovery
operator|.
name|localNode
argument_list|,
name|t
argument_list|)
expr_stmt|;
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
block|{
name|sendInitialStateEventIfNeeded
argument_list|()
expr_stmt|;
name|publishResponseHandler
operator|.
name|onResponse
argument_list|(
name|discovery
operator|.
name|localNode
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|publishResponseHandler
operator|.
name|onResponse
argument_list|(
name|discovery
operator|.
name|localNode
argument_list|)
expr_stmt|;
block|}
block|}
name|TimeValue
name|publishTimeout
init|=
name|discoverySettings
operator|.
name|getPublishTimeout
argument_list|()
decl_stmt|;
if|if
condition|(
name|publishTimeout
operator|.
name|millis
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|boolean
name|awaited
init|=
name|publishResponseHandler
operator|.
name|awaitAllNodes
argument_list|(
name|publishTimeout
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|awaited
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"awaiting all nodes to process published state {} timed out, timeout {}"
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|,
name|publishTimeout
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore& restore interrupt
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// failure to marshal or un-marshal
throw|throw
operator|new
name|ElasticsearchIllegalStateException
argument_list|(
literal|"Cluster state failed to serialize"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|sendInitialStateEventIfNeeded
specifier|private
name|void
name|sendInitialStateEventIfNeeded
parameter_list|()
block|{
if|if
condition|(
name|initialStateSent
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
for|for
control|(
name|InitialStateDiscoveryListener
name|listener
range|:
name|initialStateListeners
control|)
block|{
name|listener
operator|.
name|initialStateProcessed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|ClusterGroup
specifier|private
class|class
name|ClusterGroup
block|{
DECL|field|members
specifier|private
name|Queue
argument_list|<
name|LocalDiscovery
argument_list|>
name|members
init|=
name|ConcurrentCollections
operator|.
name|newQueue
argument_list|()
decl_stmt|;
DECL|method|members
name|Queue
argument_list|<
name|LocalDiscovery
argument_list|>
name|members
parameter_list|()
block|{
return|return
name|members
return|;
block|}
block|}
block|}
end_class

end_unit

