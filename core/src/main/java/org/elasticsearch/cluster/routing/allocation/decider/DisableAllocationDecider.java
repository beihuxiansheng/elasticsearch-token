begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  * This {@link AllocationDecider} prevents cluster-wide shard allocations. The  * behavior of this {@link AllocationDecider} can be changed in real-time via  * the cluster settings API. It respects the following settings:  *<ul>  *<li><tt>cluster.routing.allocation.disable_new_allocation</tt> - if set to  *<code>true</code> no new shard-allocation are allowed. Note: this setting is  * only applied if the allocated shard is a primary and it has not been  * allocated before the this setting was applied.</li>  *<p/>  *<li><tt>cluster.routing.allocation.disable_allocation</tt> - if set to  *<code>true</code> cluster wide allocations are disabled</li>  *<p/>  *<li><tt>cluster.routing.allocation.disable_replica_allocation</tt> - if set  * to<code>true</code> cluster wide replica allocations are disabled while  * primary shards can still be allocated</li>  *</ul>  *<p/>  *<p>  * Note: all of the above settings might be ignored if the allocation happens on  * a shard that explicitly ignores disabled allocations via  * {@link RoutingAllocation#ignoreDisable()}. Which is set if allocation are  * explicit.  *</p>  *  * @deprecated In favour for {@link EnableAllocationDecider}.  */
end_comment

begin_class
annotation|@
name|Deprecated
DECL|class|DisableAllocationDecider
specifier|public
class|class
name|DisableAllocationDecider
extends|extends
name|AllocationDecider
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"disable"
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
init|=
literal|"cluster.routing.allocation.disable_new_allocation"
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
init|=
literal|"cluster.routing.allocation.disable_allocation"
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
init|=
literal|"cluster.routing.allocation.disable_replica_allocation"
decl_stmt|;
DECL|field|INDEX_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
init|=
literal|"index.routing.allocation.disable_new_allocation"
decl_stmt|;
DECL|field|INDEX_ROUTING_ALLOCATION_DISABLE_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ROUTING_ALLOCATION_DISABLE_ALLOCATION
init|=
literal|"index.routing.allocation.disable_allocation"
decl_stmt|;
DECL|field|INDEX_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
init|=
literal|"index.routing.allocation.disable_replica_allocation"
decl_stmt|;
DECL|class|ApplySettings
class|class
name|ApplySettings
implements|implements
name|NodeSettingsService
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|onRefreshSettings
specifier|public
name|void
name|onRefreshSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|boolean
name|disableNewAllocation
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
argument_list|,
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableNewAllocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableNewAllocation
operator|!=
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableNewAllocation
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [cluster.routing.allocation.disable_new_allocation] from [{}] to [{}]"
argument_list|,
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableNewAllocation
argument_list|,
name|disableNewAllocation
argument_list|)
expr_stmt|;
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableNewAllocation
operator|=
name|disableNewAllocation
expr_stmt|;
block|}
name|boolean
name|disableAllocation
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
argument_list|,
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableAllocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableAllocation
operator|!=
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableAllocation
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [cluster.routing.allocation.disable_allocation] from [{}] to [{}]"
argument_list|,
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableAllocation
argument_list|,
name|disableAllocation
argument_list|)
expr_stmt|;
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableAllocation
operator|=
name|disableAllocation
expr_stmt|;
block|}
name|boolean
name|disableReplicaAllocation
init|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
argument_list|,
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableReplicaAllocation
argument_list|)
decl_stmt|;
if|if
condition|(
name|disableReplicaAllocation
operator|!=
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableReplicaAllocation
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [cluster.routing.allocation.disable_replica_allocation] from [{}] to [{}]"
argument_list|,
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableReplicaAllocation
argument_list|,
name|disableReplicaAllocation
argument_list|)
expr_stmt|;
name|DisableAllocationDecider
operator|.
name|this
operator|.
name|disableReplicaAllocation
operator|=
name|disableReplicaAllocation
expr_stmt|;
block|}
block|}
block|}
DECL|field|disableNewAllocation
specifier|private
specifier|volatile
name|boolean
name|disableNewAllocation
decl_stmt|;
DECL|field|disableAllocation
specifier|private
specifier|volatile
name|boolean
name|disableAllocation
decl_stmt|;
DECL|field|disableReplicaAllocation
specifier|private
specifier|volatile
name|boolean
name|disableReplicaAllocation
decl_stmt|;
annotation|@
name|Inject
DECL|method|DisableAllocationDecider
specifier|public
name|DisableAllocationDecider
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeSettingsService
name|nodeSettingsService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|disableNewAllocation
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|disableAllocation
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_ALLOCATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|disableReplicaAllocation
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|nodeSettingsService
operator|.
name|addListener
argument_list|(
operator|new
name|ApplySettings
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canAllocate
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
if|if
condition|(
name|allocation
operator|.
name|ignoreDisable
argument_list|()
condition|)
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
name|NAME
argument_list|,
literal|"allocation disabling is ignored"
argument_list|)
return|;
block|}
name|Settings
name|indexSettings
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|settings
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
operator|&&
name|shardRouting
operator|.
name|allocatedPostIndexCreate
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// if its primary, and it hasn't been allocated post API (meaning its a "fresh newly created shard"), only disable allocation
comment|// on a special disable allocation flag
if|if
condition|(
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
name|INDEX_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
argument_list|,
name|disableNewAllocation
argument_list|)
condition|)
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
name|NAME
argument_list|,
literal|"new primary allocation is disabled"
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
name|NAME
argument_list|,
literal|"new primary allocation is enabled"
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
name|INDEX_ROUTING_ALLOCATION_DISABLE_ALLOCATION
argument_list|,
name|disableAllocation
argument_list|)
condition|)
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
name|NAME
argument_list|,
literal|"all allocation is disabled"
argument_list|)
return|;
block|}
if|if
condition|(
name|indexSettings
operator|.
name|getAsBoolean
argument_list|(
name|INDEX_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
argument_list|,
name|disableReplicaAllocation
argument_list|)
condition|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
name|NAME
argument_list|,
literal|"primary allocation is enabled"
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
name|NAME
argument_list|,
literal|"replica allocation is disabled"
argument_list|)
return|;
block|}
block|}
return|return
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
name|NAME
argument_list|,
literal|"all allocation is enabled"
argument_list|)
return|;
block|}
block|}
end_class

end_unit
