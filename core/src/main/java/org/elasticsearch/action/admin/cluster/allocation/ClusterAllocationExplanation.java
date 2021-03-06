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
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterInfo
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
name|ShardRoutingState
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
name|AllocationDecision
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
name|ShardAllocationDecision
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|Locale
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
name|routing
operator|.
name|allocation
operator|.
name|AbstractAllocationDecision
operator|.
name|discoveryNodeToXContent
import|;
end_import

begin_comment
comment|/**  * A {@code ClusterAllocationExplanation} is an explanation of why a shard is unassigned,  * or if it is not unassigned, then which nodes it could possibly be relocated to.  * It is an immutable class.  */
end_comment

begin_class
DECL|class|ClusterAllocationExplanation
specifier|public
specifier|final
class|class
name|ClusterAllocationExplanation
implements|implements
name|ToXContent
implements|,
name|Writeable
block|{
DECL|field|shardRouting
specifier|private
specifier|final
name|ShardRouting
name|shardRouting
decl_stmt|;
DECL|field|currentNode
specifier|private
specifier|final
name|DiscoveryNode
name|currentNode
decl_stmt|;
DECL|field|relocationTargetNode
specifier|private
specifier|final
name|DiscoveryNode
name|relocationTargetNode
decl_stmt|;
DECL|field|clusterInfo
specifier|private
specifier|final
name|ClusterInfo
name|clusterInfo
decl_stmt|;
DECL|field|shardAllocationDecision
specifier|private
specifier|final
name|ShardAllocationDecision
name|shardAllocationDecision
decl_stmt|;
DECL|method|ClusterAllocationExplanation
specifier|public
name|ClusterAllocationExplanation
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
annotation|@
name|Nullable
name|DiscoveryNode
name|currentNode
parameter_list|,
annotation|@
name|Nullable
name|DiscoveryNode
name|relocationTargetNode
parameter_list|,
annotation|@
name|Nullable
name|ClusterInfo
name|clusterInfo
parameter_list|,
name|ShardAllocationDecision
name|shardAllocationDecision
parameter_list|)
block|{
name|this
operator|.
name|shardRouting
operator|=
name|shardRouting
expr_stmt|;
name|this
operator|.
name|currentNode
operator|=
name|currentNode
expr_stmt|;
name|this
operator|.
name|relocationTargetNode
operator|=
name|relocationTargetNode
expr_stmt|;
name|this
operator|.
name|clusterInfo
operator|=
name|clusterInfo
expr_stmt|;
name|this
operator|.
name|shardAllocationDecision
operator|=
name|shardAllocationDecision
expr_stmt|;
block|}
DECL|method|ClusterAllocationExplanation
specifier|public
name|ClusterAllocationExplanation
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|shardRouting
operator|=
operator|new
name|ShardRouting
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentNode
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|DiscoveryNode
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|relocationTargetNode
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|DiscoveryNode
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterInfo
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|ClusterInfo
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardAllocationDecision
operator|=
operator|new
name|ShardAllocationDecision
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|shardRouting
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|currentNode
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|relocationTargetNode
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|clusterInfo
argument_list|)
expr_stmt|;
name|shardAllocationDecision
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the shard that the explanation is about.      */
DECL|method|getShard
specifier|public
name|ShardId
name|getShard
parameter_list|()
block|{
return|return
name|shardRouting
operator|.
name|shardId
argument_list|()
return|;
block|}
comment|/**      * Returns {@code true} if the explained shard is primary, {@code false} otherwise.      */
DECL|method|isPrimary
specifier|public
name|boolean
name|isPrimary
parameter_list|()
block|{
return|return
name|shardRouting
operator|.
name|primary
argument_list|()
return|;
block|}
comment|/**      * Returns the current {@link ShardRoutingState} of the shard.      */
DECL|method|getShardState
specifier|public
name|ShardRoutingState
name|getShardState
parameter_list|()
block|{
return|return
name|shardRouting
operator|.
name|state
argument_list|()
return|;
block|}
comment|/**      * Returns the currently assigned node, or {@code null} if the shard is unassigned.      */
annotation|@
name|Nullable
DECL|method|getCurrentNode
specifier|public
name|DiscoveryNode
name|getCurrentNode
parameter_list|()
block|{
return|return
name|currentNode
return|;
block|}
comment|/**      * Returns the relocating target node, or {@code null} if the shard is not in the {@link ShardRoutingState#RELOCATING} state.      */
annotation|@
name|Nullable
DECL|method|getRelocationTargetNode
specifier|public
name|DiscoveryNode
name|getRelocationTargetNode
parameter_list|()
block|{
return|return
name|relocationTargetNode
return|;
block|}
comment|/**      * Returns the unassigned info for the shard, or {@code null} if the shard is active.      */
annotation|@
name|Nullable
DECL|method|getUnassignedInfo
specifier|public
name|UnassignedInfo
name|getUnassignedInfo
parameter_list|()
block|{
return|return
name|shardRouting
operator|.
name|unassignedInfo
argument_list|()
return|;
block|}
comment|/**      * Returns the cluster disk info for the cluster, or {@code null} if none available.      */
annotation|@
name|Nullable
DECL|method|getClusterInfo
specifier|public
name|ClusterInfo
name|getClusterInfo
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterInfo
return|;
block|}
comment|/** \      * Returns the shard allocation decision for attempting to assign or move the shard.      */
DECL|method|getShardAllocationDecision
specifier|public
name|ShardAllocationDecision
name|getShardAllocationDecision
parameter_list|()
block|{
return|return
name|shardAllocationDecision
return|;
block|}
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|shardRouting
operator|.
name|getIndexName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"shard"
argument_list|,
name|shardRouting
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"primary"
argument_list|,
name|shardRouting
operator|.
name|primary
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"current_state"
argument_list|,
name|shardRouting
operator|.
name|state
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|unassignedInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|unassignedInfoToXContent
argument_list|(
name|shardRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|,
name|builder
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|currentNode
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"current_node"
argument_list|)
expr_stmt|;
block|{
name|discoveryNodeToXContent
argument_list|(
name|currentNode
argument_list|,
literal|true
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardAllocationDecision
operator|.
name|getMoveDecision
argument_list|()
operator|.
name|isDecisionTaken
argument_list|()
operator|&&
name|shardAllocationDecision
operator|.
name|getMoveDecision
argument_list|()
operator|.
name|getCurrentNodeRanking
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"weight_ranking"
argument_list|,
name|shardAllocationDecision
operator|.
name|getMoveDecision
argument_list|()
operator|.
name|getCurrentNodeRanking
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|clusterInfo
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"cluster_info"
argument_list|)
expr_stmt|;
block|{
name|this
operator|.
name|clusterInfo
operator|.
name|toXContent
argument_list|(
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
comment|// end "cluster_info"
block|}
if|if
condition|(
name|shardAllocationDecision
operator|.
name|isDecisionTaken
argument_list|()
condition|)
block|{
name|shardAllocationDecision
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|explanation
decl_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|state
argument_list|()
operator|==
name|ShardRoutingState
operator|.
name|RELOCATING
condition|)
block|{
name|explanation
operator|=
literal|"the shard is in the process of relocating from node ["
operator|+
name|currentNode
operator|.
name|getName
argument_list|()
operator|+
literal|"] "
operator|+
literal|"to node ["
operator|+
name|relocationTargetNode
operator|.
name|getName
argument_list|()
operator|+
literal|"], wait until relocation has completed"
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|shardRouting
operator|.
name|state
argument_list|()
operator|==
name|ShardRoutingState
operator|.
name|INITIALIZING
assert|;
name|explanation
operator|=
literal|"the shard is in the process of initializing on node ["
operator|+
name|currentNode
operator|.
name|getName
argument_list|()
operator|+
literal|"], "
operator|+
literal|"wait until initialization has completed"
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"explanation"
argument_list|,
name|explanation
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
comment|// end wrapping object
return|return
name|builder
return|;
block|}
DECL|method|unassignedInfoToXContent
specifier|private
name|XContentBuilder
name|unassignedInfoToXContent
parameter_list|(
name|UnassignedInfo
name|unassignedInfo
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"unassigned_info"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"reason"
argument_list|,
name|unassignedInfo
operator|.
name|getReason
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"at"
argument_list|,
name|UnassignedInfo
operator|.
name|DATE_TIME_FORMATTER
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|unassignedInfo
operator|.
name|getUnassignedTimeInMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|unassignedInfo
operator|.
name|getNumFailedAllocations
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"failed_allocation_attempts"
argument_list|,
name|unassignedInfo
operator|.
name|getNumFailedAllocations
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|details
init|=
name|unassignedInfo
operator|.
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|details
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"details"
argument_list|,
name|details
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"last_allocation_status"
argument_list|,
name|AllocationDecision
operator|.
name|fromAllocationStatus
argument_list|(
name|unassignedInfo
operator|.
name|getLastAllocationStatus
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

