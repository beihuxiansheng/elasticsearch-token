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
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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
name|ExceptionsHelper
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
name|shards
operator|.
name|IndicesShardStoresRequest
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
name|shards
operator|.
name|IndicesShardStoresResponse
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
name|shards
operator|.
name|TransportIndicesShardStoresAction
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
name|collect
operator|.
name|ImmutableOpenIntMap
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
DECL|field|shardStoresAction
specifier|private
specifier|final
name|TransportIndicesShardStoresAction
name|shardStoresAction
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
parameter_list|,
name|TransportIndicesShardStoresAction
name|shardStoresAction
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
name|this
operator|.
name|shardStoresAction
operator|=
name|shardStoresAction
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
comment|/**      * Construct a {@code NodeExplanation} object for the given shard given all the metadata. This also attempts to construct the human      * readable FinalDecision and final explanation as part of the explanation.      */
DECL|method|calculateNodeExplanation
specifier|public
specifier|static
name|NodeExplanation
name|calculateNodeExplanation
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|,
name|Decision
name|nodeDecision
parameter_list|,
name|Float
name|nodeWeight
parameter_list|,
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|storeStatus
parameter_list|,
name|String
name|assignedNodeId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|activeAllocationIds
parameter_list|)
block|{
specifier|final
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
name|finalDecision
decl_stmt|;
specifier|final
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
name|storeCopy
decl_stmt|;
specifier|final
name|String
name|finalExplanation
decl_stmt|;
if|if
condition|(
name|storeStatus
operator|==
literal|null
condition|)
block|{
comment|// No copies of the data
name|storeCopy
operator|=
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|NONE
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Throwable
name|storeErr
init|=
name|storeStatus
operator|.
name|getStoreException
argument_list|()
decl_stmt|;
if|if
condition|(
name|storeErr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|storeErr
argument_list|)
operator|instanceof
name|CorruptIndexException
condition|)
block|{
name|storeCopy
operator|=
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|CORRUPT
expr_stmt|;
block|}
else|else
block|{
name|storeCopy
operator|=
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|IO_ERROR
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|activeAllocationIds
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// The ids are only empty if dealing with a legacy index
comment|// TODO: fetch the shard state versions and display here?
name|storeCopy
operator|=
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|UNKNOWN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|activeAllocationIds
operator|.
name|contains
argument_list|(
name|storeStatus
operator|.
name|getAllocationId
argument_list|()
argument_list|)
condition|)
block|{
name|storeCopy
operator|=
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|AVAILABLE
expr_stmt|;
block|}
else|else
block|{
comment|// Otherwise, this is a stale copy of the data (allocation ids don't match)
name|storeCopy
operator|=
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|STALE
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|assignedNodeId
argument_list|)
condition|)
block|{
name|finalDecision
operator|=
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|ALREADY_ASSIGNED
expr_stmt|;
name|finalExplanation
operator|=
literal|"the shard is already assigned to this node"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|&&
name|shard
operator|.
name|unassigned
argument_list|()
operator|&&
name|storeCopy
operator|==
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|STALE
condition|)
block|{
name|finalExplanation
operator|=
literal|"the copy of the shard is stale, allocation ids do not match"
expr_stmt|;
name|finalDecision
operator|=
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|&&
name|shard
operator|.
name|unassigned
argument_list|()
operator|&&
name|storeCopy
operator|==
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|CORRUPT
condition|)
block|{
name|finalExplanation
operator|=
literal|"the copy of the shard is corrupt"
expr_stmt|;
name|finalDecision
operator|=
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|&&
name|shard
operator|.
name|unassigned
argument_list|()
operator|&&
name|storeCopy
operator|==
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|IO_ERROR
condition|)
block|{
name|finalExplanation
operator|=
literal|"the copy of the shard cannot be read"
expr_stmt|;
name|finalDecision
operator|=
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|nodeDecision
operator|.
name|type
argument_list|()
operator|==
name|Decision
operator|.
name|Type
operator|.
name|NO
condition|)
block|{
name|finalDecision
operator|=
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|NO
expr_stmt|;
name|finalExplanation
operator|=
literal|"the shard cannot be assigned because one or more allocation decider returns a 'NO' decision"
expr_stmt|;
block|}
else|else
block|{
name|finalDecision
operator|=
name|ClusterAllocationExplanation
operator|.
name|FinalDecision
operator|.
name|YES
expr_stmt|;
if|if
condition|(
name|storeCopy
operator|==
name|ClusterAllocationExplanation
operator|.
name|StoreCopy
operator|.
name|AVAILABLE
condition|)
block|{
name|finalExplanation
operator|=
literal|"the shard can be assigned and the node contains a valid copy of the shard data"
expr_stmt|;
block|}
else|else
block|{
name|finalExplanation
operator|=
literal|"the shard can be assigned"
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|NodeExplanation
argument_list|(
name|node
argument_list|,
name|nodeDecision
argument_list|,
name|nodeWeight
argument_list|,
name|storeStatus
argument_list|,
name|finalDecision
argument_list|,
name|finalExplanation
argument_list|,
name|storeCopy
argument_list|)
return|;
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
parameter_list|,
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
name|shardStores
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
name|remainingDelayMillis
init|=
literal|0
decl_stmt|;
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
name|IndexMetaData
name|indexMetaData
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
decl_stmt|;
if|if
condition|(
name|ui
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Settings
name|indexSettings
init|=
name|indexMetaData
operator|.
name|getSettings
argument_list|()
decl_stmt|;
name|long
name|remainingDelayNanos
init|=
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
decl_stmt|;
name|remainingDelayMillis
operator|=
name|TimeValue
operator|.
name|timeValueNanos
argument_list|(
name|remainingDelayNanos
argument_list|)
operator|.
name|millis
argument_list|()
expr_stmt|;
block|}
comment|// Calculate weights for each of the nodes
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|Float
argument_list|>
name|weights
init|=
name|shardAllocator
operator|.
name|weighShard
argument_list|(
name|allocation
argument_list|,
name|shard
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
name|nodeToStatus
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|shardStores
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|status
range|:
name|shardStores
control|)
block|{
name|nodeToStatus
operator|.
name|put
argument_list|(
name|status
operator|.
name|getNode
argument_list|()
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|NodeExplanation
argument_list|>
name|explanations
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|shardStores
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DiscoveryNode
argument_list|,
name|Decision
argument_list|>
name|entry
range|:
name|nodeToDecision
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DiscoveryNode
name|node
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Decision
name|decision
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|Float
name|weight
init|=
name|weights
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
name|storeStatus
init|=
name|nodeToStatus
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|NodeExplanation
name|nodeExplanation
init|=
name|calculateNodeExplanation
argument_list|(
name|shard
argument_list|,
name|indexMetaData
argument_list|,
name|node
argument_list|,
name|decision
argument_list|,
name|weight
argument_list|,
name|storeStatus
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|indexMetaData
operator|.
name|activeAllocationIds
argument_list|(
name|shard
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|explanations
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|nodeExplanation
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
name|remainingDelayMillis
argument_list|,
name|ui
argument_list|,
name|explanations
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
name|foundShard
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
name|foundShard
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
name|foundShard
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
name|foundShard
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
name|foundShard
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
name|foundShard
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
specifier|final
name|ShardRouting
name|shardRouting
init|=
name|foundShard
decl_stmt|;
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
name|getShardStores
argument_list|(
name|shardRouting
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|IndicesShardStoresResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|IndicesShardStoresResponse
name|shardStoreResponse
parameter_list|)
block|{
name|ImmutableOpenIntMap
argument_list|<
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
argument_list|>
name|shardStatuses
init|=
name|shardStoreResponse
operator|.
name|getStoreStatuses
argument_list|()
operator|.
name|get
argument_list|(
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|IndicesShardStoresResponse
operator|.
name|StoreStatus
argument_list|>
name|shardStoreStatus
init|=
name|shardStatuses
operator|.
name|get
argument_list|(
name|shardRouting
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
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
argument_list|,
name|shardStoreStatus
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
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
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
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|getShardStores
specifier|private
name|void
name|getShardStores
parameter_list|(
name|ShardRouting
name|shard
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|IndicesShardStoresResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|IndicesShardStoresRequest
name|request
init|=
operator|new
name|IndicesShardStoresRequest
argument_list|(
name|shard
operator|.
name|getIndexName
argument_list|()
argument_list|)
decl_stmt|;
name|request
operator|.
name|shardStatuses
argument_list|(
literal|"all"
argument_list|)
expr_stmt|;
name|shardStoresAction
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

