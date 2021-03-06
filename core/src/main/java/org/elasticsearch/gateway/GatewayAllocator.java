begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|nodes
operator|.
name|BaseNodeResponse
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
name|nodes
operator|.
name|BaseNodesResponse
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
name|RoutingNodes
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
name|RoutingService
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
name|ShardRouting
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
name|AllocateUnassignedDecision
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
name|FailedShard
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
name|lease
operator|.
name|Releasables
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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|store
operator|.
name|TransportNodesListShardStoreMetaData
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
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_class
DECL|class|GatewayAllocator
specifier|public
class|class
name|GatewayAllocator
extends|extends
name|AbstractComponent
block|{
DECL|field|routingService
specifier|private
specifier|final
name|RoutingService
name|routingService
decl_stmt|;
DECL|field|primaryShardAllocator
specifier|private
specifier|final
name|PrimaryShardAllocator
name|primaryShardAllocator
decl_stmt|;
DECL|field|replicaShardAllocator
specifier|private
specifier|final
name|ReplicaShardAllocator
name|replicaShardAllocator
decl_stmt|;
DECL|field|asyncFetchStarted
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ShardId
argument_list|,
name|AsyncShardFetch
argument_list|<
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeGatewayStartedShards
argument_list|>
argument_list|>
name|asyncFetchStarted
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|asyncFetchStore
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|ShardId
argument_list|,
name|AsyncShardFetch
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
argument_list|>
name|asyncFetchStore
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|GatewayAllocator
specifier|public
name|GatewayAllocator
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|RoutingService
name|routingService
parameter_list|,
name|TransportNodesListGatewayStartedShards
name|startedAction
parameter_list|,
name|TransportNodesListShardStoreMetaData
name|storeAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|routingService
operator|=
name|routingService
expr_stmt|;
name|this
operator|.
name|primaryShardAllocator
operator|=
operator|new
name|InternalPrimaryShardAllocator
argument_list|(
name|settings
argument_list|,
name|startedAction
argument_list|)
expr_stmt|;
name|this
operator|.
name|replicaShardAllocator
operator|=
operator|new
name|InternalReplicaShardAllocator
argument_list|(
name|settings
argument_list|,
name|storeAction
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|addStateApplier
argument_list|(
name|event
lambda|->
block|{
name|boolean
name|cleanCache
init|=
literal|false
decl_stmt|;
name|DiscoveryNode
name|localNode
init|=
name|event
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|getLocalNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|localNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|localNode
operator|.
name|isMasterNode
argument_list|()
operator|&&
name|event
operator|.
name|localNodeMaster
argument_list|()
operator|==
literal|false
condition|)
block|{
name|cleanCache
operator|=
literal|true
expr_stmt|;
block|}
block|}
else|else
block|{
name|cleanCache
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|cleanCache
condition|)
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|asyncFetchStarted
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|asyncFetchStarted
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Releasables
operator|.
name|close
argument_list|(
name|asyncFetchStore
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
name|asyncFetchStore
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|// for tests
DECL|method|GatewayAllocator
specifier|protected
name|GatewayAllocator
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|routingService
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|primaryShardAllocator
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|replicaShardAllocator
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|getNumberOfInFlightFetch
specifier|public
name|int
name|getNumberOfInFlightFetch
parameter_list|()
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|AsyncShardFetch
argument_list|<
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeGatewayStartedShards
argument_list|>
name|fetch
range|:
name|asyncFetchStarted
operator|.
name|values
argument_list|()
control|)
block|{
name|count
operator|+=
name|fetch
operator|.
name|getNumberOfInFlightFetches
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|AsyncShardFetch
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|fetch
range|:
name|asyncFetchStore
operator|.
name|values
argument_list|()
control|)
block|{
name|count
operator|+=
name|fetch
operator|.
name|getNumberOfInFlightFetches
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
DECL|method|applyStartedShards
specifier|public
name|void
name|applyStartedShards
parameter_list|(
specifier|final
name|RoutingAllocation
name|allocation
parameter_list|,
specifier|final
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|startedShards
parameter_list|)
block|{
for|for
control|(
name|ShardRouting
name|startedShard
range|:
name|startedShards
control|)
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|asyncFetchStarted
operator|.
name|remove
argument_list|(
name|startedShard
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Releasables
operator|.
name|close
argument_list|(
name|asyncFetchStore
operator|.
name|remove
argument_list|(
name|startedShard
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|applyFailedShards
specifier|public
name|void
name|applyFailedShards
parameter_list|(
specifier|final
name|RoutingAllocation
name|allocation
parameter_list|,
specifier|final
name|List
argument_list|<
name|FailedShard
argument_list|>
name|failedShards
parameter_list|)
block|{
for|for
control|(
name|FailedShard
name|failedShard
range|:
name|failedShards
control|)
block|{
name|Releasables
operator|.
name|close
argument_list|(
name|asyncFetchStarted
operator|.
name|remove
argument_list|(
name|failedShard
operator|.
name|getRoutingEntry
argument_list|()
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Releasables
operator|.
name|close
argument_list|(
name|asyncFetchStore
operator|.
name|remove
argument_list|(
name|failedShard
operator|.
name|getRoutingEntry
argument_list|()
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|allocateUnassigned
specifier|public
name|void
name|allocateUnassigned
parameter_list|(
specifier|final
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|innerAllocatedUnassigned
argument_list|(
name|allocation
argument_list|,
name|primaryShardAllocator
argument_list|,
name|replicaShardAllocator
argument_list|)
expr_stmt|;
block|}
comment|// allow for testing infra to change shard allocators implementation
DECL|method|innerAllocatedUnassigned
specifier|protected
specifier|static
name|void
name|innerAllocatedUnassigned
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|,
name|PrimaryShardAllocator
name|primaryShardAllocator
parameter_list|,
name|ReplicaShardAllocator
name|replicaShardAllocator
parameter_list|)
block|{
name|RoutingNodes
operator|.
name|UnassignedShards
name|unassigned
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|unassigned
argument_list|()
decl_stmt|;
name|unassigned
operator|.
name|sort
argument_list|(
name|PriorityComparator
operator|.
name|getAllocationComparator
argument_list|(
name|allocation
argument_list|)
argument_list|)
expr_stmt|;
comment|// sort for priority ordering
name|primaryShardAllocator
operator|.
name|allocateUnassigned
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
name|replicaShardAllocator
operator|.
name|processExistingRecoveries
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
name|replicaShardAllocator
operator|.
name|allocateUnassigned
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
block|}
comment|/**      * Computes and returns the design for allocating a single unassigned shard.  If called on an assigned shard,      * {@link AllocateUnassignedDecision#NOT_TAKEN} is returned.      */
DECL|method|decideUnassignedShardAllocation
specifier|public
name|AllocateUnassignedDecision
name|decideUnassignedShardAllocation
parameter_list|(
name|ShardRouting
name|unassignedShard
parameter_list|,
name|RoutingAllocation
name|routingAllocation
parameter_list|)
block|{
if|if
condition|(
name|unassignedShard
operator|.
name|primary
argument_list|()
condition|)
block|{
return|return
name|primaryShardAllocator
operator|.
name|makeAllocationDecision
argument_list|(
name|unassignedShard
argument_list|,
name|routingAllocation
argument_list|,
name|logger
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|replicaShardAllocator
operator|.
name|makeAllocationDecision
argument_list|(
name|unassignedShard
argument_list|,
name|routingAllocation
argument_list|,
name|logger
argument_list|)
return|;
block|}
block|}
DECL|class|InternalAsyncFetch
class|class
name|InternalAsyncFetch
parameter_list|<
name|T
extends|extends
name|BaseNodeResponse
parameter_list|>
extends|extends
name|AsyncShardFetch
argument_list|<
name|T
argument_list|>
block|{
DECL|method|InternalAsyncFetch
name|InternalAsyncFetch
parameter_list|(
name|Logger
name|logger
parameter_list|,
name|String
name|type
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|Lister
argument_list|<
name|?
extends|extends
name|BaseNodesResponse
argument_list|<
name|T
argument_list|>
argument_list|,
name|T
argument_list|>
name|action
parameter_list|)
block|{
name|super
argument_list|(
name|logger
argument_list|,
name|type
argument_list|,
name|shardId
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reroute
specifier|protected
name|void
name|reroute
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{} scheduling reroute for {}"
argument_list|,
name|shardId
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|routingService
operator|.
name|reroute
argument_list|(
literal|"async_shard_fetch"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|InternalPrimaryShardAllocator
class|class
name|InternalPrimaryShardAllocator
extends|extends
name|PrimaryShardAllocator
block|{
DECL|field|startedAction
specifier|private
specifier|final
name|TransportNodesListGatewayStartedShards
name|startedAction
decl_stmt|;
DECL|method|InternalPrimaryShardAllocator
name|InternalPrimaryShardAllocator
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportNodesListGatewayStartedShards
name|startedAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|startedAction
operator|=
name|startedAction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fetchData
specifier|protected
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeGatewayStartedShards
argument_list|>
name|fetchData
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|AsyncShardFetch
argument_list|<
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeGatewayStartedShards
argument_list|>
name|fetch
init|=
name|asyncFetchStarted
operator|.
name|computeIfAbsent
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardId
lambda|->
operator|new
name|InternalAsyncFetch
argument_list|<>
argument_list|(
name|logger
argument_list|,
literal|"shard_started"
argument_list|,
name|shardId
argument_list|,
name|startedAction
argument_list|)
argument_list|)
decl_stmt|;
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeGatewayStartedShards
argument_list|>
name|shardState
init|=
name|fetch
operator|.
name|fetchData
argument_list|(
name|allocation
operator|.
name|nodes
argument_list|()
argument_list|,
name|allocation
operator|.
name|getIgnoreNodes
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardState
operator|.
name|hasData
argument_list|()
condition|)
block|{
name|shardState
operator|.
name|processAllocation
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
block|}
return|return
name|shardState
return|;
block|}
block|}
DECL|class|InternalReplicaShardAllocator
class|class
name|InternalReplicaShardAllocator
extends|extends
name|ReplicaShardAllocator
block|{
DECL|field|storeAction
specifier|private
specifier|final
name|TransportNodesListShardStoreMetaData
name|storeAction
decl_stmt|;
DECL|method|InternalReplicaShardAllocator
name|InternalReplicaShardAllocator
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportNodesListShardStoreMetaData
name|storeAction
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|storeAction
operator|=
name|storeAction
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fetchData
specifier|protected
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|fetchData
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|AsyncShardFetch
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|fetch
init|=
name|asyncFetchStore
operator|.
name|computeIfAbsent
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardId
lambda|->
operator|new
name|InternalAsyncFetch
argument_list|<>
argument_list|(
name|logger
argument_list|,
literal|"shard_store"
argument_list|,
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|storeAction
argument_list|)
argument_list|)
decl_stmt|;
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|TransportNodesListShardStoreMetaData
operator|.
name|NodeStoreFilesMetaData
argument_list|>
name|shardStores
init|=
name|fetch
operator|.
name|fetchData
argument_list|(
name|allocation
operator|.
name|nodes
argument_list|()
argument_list|,
name|allocation
operator|.
name|getIgnoreNodes
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardStores
operator|.
name|hasData
argument_list|()
condition|)
block|{
name|shardStores
operator|.
name|processAllocation
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
block|}
return|return
name|shardStores
return|;
block|}
annotation|@
name|Override
DECL|method|hasInitiatedFetching
specifier|protected
name|boolean
name|hasInitiatedFetching
parameter_list|(
name|ShardRouting
name|shard
parameter_list|)
block|{
return|return
name|asyncFetchStore
operator|.
name|get
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|)
operator|!=
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

