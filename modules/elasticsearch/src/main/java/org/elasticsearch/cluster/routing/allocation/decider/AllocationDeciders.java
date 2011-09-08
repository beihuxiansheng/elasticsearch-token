begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.decider
package|package
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
name|collect
operator|.
name|ImmutableSet
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
name|node
operator|.
name|settings
operator|.
name|NodeSettingsService
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
comment|/**  * Holds several {@link AllocationDecider}s and combines them into a single allocation decision.  */
end_comment

begin_class
DECL|class|AllocationDeciders
specifier|public
class|class
name|AllocationDeciders
extends|extends
name|AllocationDecider
block|{
DECL|field|allocations
specifier|private
specifier|final
name|AllocationDecider
index|[]
name|allocations
decl_stmt|;
DECL|method|AllocationDeciders
specifier|public
name|AllocationDeciders
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeSettingsService
name|nodeSettingsService
parameter_list|)
block|{
name|this
argument_list|(
name|settings
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|AllocationDecider
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|SameShardAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|FilterAllocationDecider
argument_list|(
name|settings
argument_list|,
name|nodeSettingsService
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ReplicaAfterPrimaryActiveAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ThrottlingAllocationDecider
argument_list|(
name|settings
argument_list|,
name|nodeSettingsService
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|RebalanceOnlyWhenActiveAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ClusterRebalanceAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
operator|new
name|ConcurrentRebalanceAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|AllocationDeciders
annotation|@
name|Inject
specifier|public
name|AllocationDeciders
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Set
argument_list|<
name|AllocationDecider
argument_list|>
name|allocations
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|allocations
operator|=
name|allocations
operator|.
name|toArray
argument_list|(
operator|new
name|AllocationDecider
index|[
name|allocations
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
DECL|method|canRebalance
annotation|@
name|Override
specifier|public
name|boolean
name|canRebalance
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
for|for
control|(
name|AllocationDecider
name|allocation1
range|:
name|allocations
control|)
block|{
if|if
condition|(
operator|!
name|allocation1
operator|.
name|canRebalance
argument_list|(
name|shardRouting
argument_list|,
name|allocation
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|canAllocate
annotation|@
name|Override
specifier|public
name|Decision
name|canAllocate
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
name|Decision
name|ret
init|=
name|Decision
operator|.
name|YES
decl_stmt|;
comment|// first, check if its in the ignored, if so, return NO
if|if
condition|(
name|allocation
operator|.
name|shouldIgnoreShardForNode
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|Decision
operator|.
name|NO
return|;
block|}
comment|// now, go over the registered allocations
for|for
control|(
name|AllocationDecider
name|allocation1
range|:
name|allocations
control|)
block|{
name|Decision
name|decision
init|=
name|allocation1
operator|.
name|canAllocate
argument_list|(
name|shardRouting
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|decision
operator|==
name|Decision
operator|.
name|NO
condition|)
block|{
return|return
name|Decision
operator|.
name|NO
return|;
block|}
elseif|else
if|if
condition|(
name|decision
operator|==
name|Decision
operator|.
name|THROTTLE
condition|)
block|{
name|ret
operator|=
name|Decision
operator|.
name|THROTTLE
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
DECL|method|canRemain
annotation|@
name|Override
specifier|public
name|boolean
name|canRemain
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|RoutingNode
name|node
parameter_list|,
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
if|if
condition|(
name|allocation
operator|.
name|shouldIgnoreShardForNode
argument_list|(
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|AllocationDecider
name|allocation1
range|:
name|allocations
control|)
block|{
if|if
condition|(
operator|!
name|allocation1
operator|.
name|canRemain
argument_list|(
name|shardRouting
argument_list|,
name|node
argument_list|,
name|allocation
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

