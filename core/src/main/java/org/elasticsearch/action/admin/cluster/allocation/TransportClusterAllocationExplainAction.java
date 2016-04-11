begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|allocation
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
name|cursors
operator|.
name|ObjectObjectCursor
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
name|TransportMasterNodeAction
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
name|ClusterInfoService
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
name|ClusterName
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
name|IndexNameExpressionResolver
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
name|metadata
operator|.
name|MetaData
operator|.
name|Custom
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
name|RoutingNode
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
name|RoutingNodes
operator|.
name|RoutingNodesIterator
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
name|UnassignedInfo
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
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|RoutingExplanations
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
name|allocator
operator|.
name|ShardsAllocator
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
name|decider
operator|.
name|AllocationDeciders
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
name|decider
operator|.
name|Decision
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
name|Iterator
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

begin_comment
comment|/**  * The {@code TransportClusterAllocationExplainAction} is responsible for actually executing the explanation of a shard's allocation on the  * master node in the cluster.  */
end_comment

begin_class
DECL|class|TransportClusterAllocationExplainAction
specifier|public
class|class
name|TransportClusterAllocationExplainAction
extends|extends
name|TransportMasterNodeAction
argument_list|<
name|ClusterAllocationExplainRequest
argument_list|,
name|ClusterAllocationExplainResponse
argument_list|>
block|{
DECL|field|allocationService
specifier|private
specifier|final
name|AllocationService
name|allocationService
decl_stmt|;
DECL|field|clusterInfoService
specifier|private
specifier|final
name|ClusterInfoService
name|clusterInfoService
decl_stmt|;
DECL|field|allocationDeciders
specifier|private
specifier|final
name|AllocationDeciders
name|allocationDeciders
decl_stmt|;
DECL|field|shardAllocator
specifier|private
specifier|final
name|ShardsAllocator
name|shardAllocator
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClusterAllocationExplainAction
specifier|public
name|TransportClusterAllocationExplainAction
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
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|AllocationService
name|allocationService
parameter_list|,
name|ClusterInfoService
name|clusterInfoService
parameter_list|,
name|AllocationDeciders
name|allocationDeciders
parameter_list|,
name|ShardsAllocator
name|shardAllocator
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ClusterAllocationExplainAction
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
name|indexNameExpressionResolver
argument_list|,
name|ClusterAllocationExplainRequest
operator|::
operator|new
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
name|clusterInfoService
operator|=
name|clusterInfoService
expr_stmt|;
name|this
operator|.
name|allocationDeciders
operator|=
name|allocationDeciders
expr_stmt|;
name|this
operator|.
name|shardAllocator
operator|=
name|shardAllocator
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
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|ClusterAllocationExplainRequest
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
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA_READ
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClusterAllocationExplainResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|ClusterAllocationExplainResponse
argument_list|()
return|;
block|}
comment|/**      * Return the decisions for the given {@code ShardRouting} on the given {@code RoutingNode}. If {@code includeYesDecisions} is not true,      * only non-YES (NO and THROTTLE) decisions are returned.      */
DECL|method|tryShardOnNode
specifier|public
specifier|static
name|Decision
name|tryShardOnNode
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|,
name|boolean
name|includeYesDecisions
parameter_list|)
block|{
name|Decision
name|d
init|=
name|allocation
operator|.
name|deciders
argument_list|()
operator|.
name|canAllocate
argument_list|(
name|shard
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|includeYesDecisions
condition|)
block|{
return|return
name|d
return|;
block|}
else|else
block|{
name|Decision
operator|.
name|Multi
name|nonYesDecisions
init|=
operator|new
name|Decision
operator|.
name|Multi
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Decision
argument_list|>
name|decisions
init|=
name|d
operator|.
name|getDecisions
argument_list|()
decl_stmt|;
for|for
control|(
name|Decision
name|decision
range|:
name|decisions
control|)
block|{
if|if
condition|(
name|decision
operator|.
name|type
argument_list|()
operator|!=
name|Decision
operator|.
name|Type
operator|.
name|YES
condition|)
block|{
name|nonYesDecisions
operator|.
name|add
argument_list|(
name|decision
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nonYesDecisions
return|;
block|}
block|}
comment|/**      * For the given {@code ShardRouting}, return the explanation of the allocation for that shard on all nodes. If {@code      * includeYesDecisions} is true, returns all decisions, otherwise returns only 'NO' and 'THROTTLE' decisions.      */
DECL|method|explainShard
specifier|public
specifier|static
name|ClusterAllocationExplanation
name|explainShard
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|,
name|RoutingNodes
name|routingNodes
parameter_list|,
name|boolean
name|includeYesDecisions
parameter_list|,
name|ShardsAllocator
name|shardAllocator
parameter_list|)
block|{
comment|// don't short circuit deciders, we want a full explanation
name|allocation
operator|.
name|debugDecision
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// get the existing unassigned info if available
name|UnassignedInfo
name|ui
init|=
name|shard
operator|.
name|unassignedInfo
argument_list|()
decl_stmt|;
name|RoutingNodesIterator
name|iter
init|=
name|routingNodes
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|Decision
argument_list|>
name|nodeToDecision
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|RoutingNode
name|node
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|DiscoveryNode
name|discoNode
init|=
name|node
operator|.
name|node
argument_list|()
decl_stmt|;
if|if
condition|(
name|discoNode
operator|.
name|isDataNode
argument_list|()
condition|)
block|{
name|Decision
name|d
init|=
name|tryShardOnNode
argument_list|(
name|shard
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|,
name|includeYesDecisions
argument_list|)
decl_stmt|;
name|nodeToDecision
operator|.
name|put
argument_list|(
name|discoNode
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|remainingDelayNanos
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|ui
operator|!=
literal|null
condition|)
block|{
specifier|final
name|MetaData
name|metadata
init|=
name|allocation
operator|.
name|metaData
argument_list|()
decl_stmt|;
specifier|final
name|Settings
name|indexSettings
init|=
name|metadata
operator|.
name|index
argument_list|(
name|shard
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|getSettings
argument_list|()
decl_stmt|;
name|remainingDelayNanos
operator|=
name|ui
operator|.
name|getRemainingDelay
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|,
name|metadata
operator|.
name|settings
argument_list|()
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ClusterAllocationExplanation
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shard
operator|.
name|primary
argument_list|()
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|ui
argument_list|,
name|nodeToDecision
argument_list|,
name|shardAllocator
operator|.
name|weighShard
argument_list|(
name|allocation
argument_list|,
name|shard
argument_list|)
argument_list|,
name|remainingDelayNanos
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|ClusterAllocationExplainRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterAllocationExplainResponse
argument_list|>
name|listener
parameter_list|)
block|{
specifier|final
name|RoutingNodes
name|routingNodes
init|=
name|state
operator|.
name|getRoutingNodes
argument_list|()
decl_stmt|;
specifier|final
name|RoutingAllocation
name|allocation
init|=
operator|new
name|RoutingAllocation
argument_list|(
name|allocationDeciders
argument_list|,
name|routingNodes
argument_list|,
name|state
operator|.
name|nodes
argument_list|()
argument_list|,
name|clusterInfoService
operator|.
name|getClusterInfo
argument_list|()
argument_list|,
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
decl_stmt|;
name|ShardRouting
name|shardRouting
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|useAnyUnassignedShard
argument_list|()
condition|)
block|{
comment|// If we can use any shard, just pick the first unassigned one (if there are any)
name|RoutingNodes
operator|.
name|UnassignedShards
operator|.
name|UnassignedIterator
name|ui
init|=
name|routingNodes
operator|.
name|unassigned
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|ui
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|shardRouting
operator|=
name|ui
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|String
name|index
init|=
name|request
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|int
name|shard
init|=
name|request
operator|.
name|getShard
argument_list|()
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|isPrimary
argument_list|()
condition|)
block|{
comment|// If we're looking for the primary shard, there's only one copy, so pick it directly
name|shardRouting
operator|=
name|allocation
operator|.
name|routingTable
argument_list|()
operator|.
name|shardRoutingTable
argument_list|(
name|index
argument_list|,
name|shard
argument_list|)
operator|.
name|primaryShard
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// If looking for a replica, go through all the replica shards
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|replicaShardRoutings
init|=
name|allocation
operator|.
name|routingTable
argument_list|()
operator|.
name|shardRoutingTable
argument_list|(
name|index
argument_list|,
name|shard
argument_list|)
operator|.
name|replicaShards
argument_list|()
decl_stmt|;
if|if
condition|(
name|replicaShardRoutings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// Pick the first replica at the very least
name|shardRouting
operator|=
name|replicaShardRoutings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// In case there are multiple replicas where some are assigned and some aren't,
comment|// try to find one that is unassigned at least
for|for
control|(
name|ShardRouting
name|replica
range|:
name|replicaShardRoutings
control|)
block|{
if|if
condition|(
name|replica
operator|.
name|unassigned
argument_list|()
condition|)
block|{
name|shardRouting
operator|=
name|replica
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|shardRouting
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|ElasticsearchException
argument_list|(
literal|"unable to find any shards to explain [{}] in the routing table"
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"explaining the allocation for [{}], found shard [{}]"
argument_list|,
name|request
argument_list|,
name|shardRouting
argument_list|)
expr_stmt|;
name|ClusterAllocationExplanation
name|cae
init|=
name|explainShard
argument_list|(
name|shardRouting
argument_list|,
name|allocation
argument_list|,
name|routingNodes
argument_list|,
name|request
operator|.
name|includeYesDecisions
argument_list|()
argument_list|,
name|shardAllocator
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|ClusterAllocationExplainResponse
argument_list|(
name|cae
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

