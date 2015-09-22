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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * This allocation decider allows shard allocations / rebalancing via the cluster wide settings {@link #CLUSTER_ROUTING_ALLOCATION_ENABLE} /  * {@link #CLUSTER_ROUTING_REBALANCE_ENABLE} and the per index setting {@link #INDEX_ROUTING_ALLOCATION_ENABLE} / {@link #INDEX_ROUTING_REBALANCE_ENABLE}.  * The per index settings overrides the cluster wide setting.  *  *<p>  * Allocation settings can have the following values (non-casesensitive):  *<ul>  *<li><code>NONE</code> - no shard allocation is allowed.  *<li><code>NEW_PRIMARIES</code> - only primary shards of new indices are allowed to be allocated  *<li><code>PRIMARIES</code> - only primary shards are allowed to be allocated  *<li><code>ALL</code> - all shards are allowed to be allocated  *</ul>  *  *<p>  * Rebalancing settings can have the following values (non-casesensitive):  *<ul>  *<li><code>NONE</code> - no shard rebalancing is allowed.  *<li><code>REPLICAS</code> - only replica shards are allowed to be balanced  *<li><code>PRIMARIES</code> - only primary shards are allowed to be balanced  *<li><code>ALL</code> - all shards are allowed to be balanced  *</ul>  *  * @see Rebalance  * @see Allocation  */
end_comment

begin_class
DECL|class|EnableAllocationDecider
specifier|public
class|class
name|EnableAllocationDecider
extends|extends
name|AllocationDecider
implements|implements
name|NodeSettingsService
operator|.
name|Listener
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"enable"
decl_stmt|;
DECL|field|CLUSTER_ROUTING_ALLOCATION_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ROUTING_ALLOCATION_ENABLE
init|=
literal|"cluster.routing.allocation.enable"
decl_stmt|;
DECL|field|INDEX_ROUTING_ALLOCATION_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ROUTING_ALLOCATION_ENABLE
init|=
literal|"index.routing.allocation.enable"
decl_stmt|;
DECL|field|CLUSTER_ROUTING_REBALANCE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_ROUTING_REBALANCE_ENABLE
init|=
literal|"cluster.routing.rebalance.enable"
decl_stmt|;
DECL|field|INDEX_ROUTING_REBALANCE_ENABLE
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ROUTING_REBALANCE_ENABLE
init|=
literal|"index.routing.rebalance.enable"
decl_stmt|;
DECL|field|enableRebalance
specifier|private
specifier|volatile
name|Rebalance
name|enableRebalance
decl_stmt|;
DECL|field|enableAllocation
specifier|private
specifier|volatile
name|Allocation
name|enableAllocation
decl_stmt|;
annotation|@
name|Inject
DECL|method|EnableAllocationDecider
specifier|public
name|EnableAllocationDecider
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
name|enableAllocation
operator|=
name|Allocation
operator|.
name|parse
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_ENABLE
argument_list|,
name|Allocation
operator|.
name|ALL
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|enableRebalance
operator|=
name|Rebalance
operator|.
name|parse
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|CLUSTER_ROUTING_REBALANCE_ENABLE
argument_list|,
name|Rebalance
operator|.
name|ALL
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodeSettingsService
operator|.
name|addListener
argument_list|(
name|this
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
name|String
name|enableIndexValue
init|=
name|indexSettings
operator|.
name|get
argument_list|(
name|INDEX_ROUTING_ALLOCATION_ENABLE
argument_list|)
decl_stmt|;
specifier|final
name|Allocation
name|enable
decl_stmt|;
if|if
condition|(
name|enableIndexValue
operator|!=
literal|null
condition|)
block|{
name|enable
operator|=
name|Allocation
operator|.
name|parse
argument_list|(
name|enableIndexValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|enable
operator|=
name|this
operator|.
name|enableAllocation
expr_stmt|;
block|}
switch|switch
condition|(
name|enable
condition|)
block|{
case|case
name|ALL
case|:
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
literal|"all allocations are allowed"
argument_list|)
return|;
case|case
name|NONE
case|:
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
literal|"no allocations are allowed"
argument_list|)
return|;
case|case
name|NEW_PRIMARIES
case|:
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
literal|"new primary allocations are allowed"
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
literal|"non-new primary allocations are forbidden"
argument_list|)
return|;
block|}
case|case
name|PRIMARIES
case|:
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
literal|"primary allocations are allowed"
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
literal|"replica allocations are forbidden"
argument_list|)
return|;
block|}
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown allocation option"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|canRebalance
specifier|public
name|Decision
name|canRebalance
parameter_list|(
name|ShardRouting
name|shardRouting
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
literal|"rebalance disabling is ignored"
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
name|String
name|enableIndexValue
init|=
name|indexSettings
operator|.
name|get
argument_list|(
name|INDEX_ROUTING_REBALANCE_ENABLE
argument_list|)
decl_stmt|;
specifier|final
name|Rebalance
name|enable
decl_stmt|;
if|if
condition|(
name|enableIndexValue
operator|!=
literal|null
condition|)
block|{
name|enable
operator|=
name|Rebalance
operator|.
name|parse
argument_list|(
name|enableIndexValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|enable
operator|=
name|this
operator|.
name|enableRebalance
expr_stmt|;
block|}
switch|switch
condition|(
name|enable
condition|)
block|{
case|case
name|ALL
case|:
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
literal|"all rebalancing is allowed"
argument_list|)
return|;
case|case
name|NONE
case|:
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
literal|"no rebalancing is allowed"
argument_list|)
return|;
case|case
name|PRIMARIES
case|:
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
literal|"primary rebalancing is allowed"
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
literal|"replica rebalancing is forbidden"
argument_list|)
return|;
block|}
case|case
name|REPLICAS
case|:
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
operator|==
literal|false
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
literal|"replica rebalancing is allowed"
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
literal|"primary rebalancing is forbidden"
argument_list|)
return|;
block|}
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown rebalance option"
argument_list|)
throw|;
block|}
block|}
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
specifier|final
name|Allocation
name|enable
init|=
name|Allocation
operator|.
name|parse
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|CLUSTER_ROUTING_ALLOCATION_ENABLE
argument_list|,
name|this
operator|.
name|enableAllocation
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|enable
operator|!=
name|this
operator|.
name|enableAllocation
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [{}] from [{}] to [{}]"
argument_list|,
name|CLUSTER_ROUTING_ALLOCATION_ENABLE
argument_list|,
name|this
operator|.
name|enableAllocation
argument_list|,
name|enable
argument_list|)
expr_stmt|;
name|EnableAllocationDecider
operator|.
name|this
operator|.
name|enableAllocation
operator|=
name|enable
expr_stmt|;
block|}
specifier|final
name|Rebalance
name|enableRebalance
init|=
name|Rebalance
operator|.
name|parse
argument_list|(
name|settings
operator|.
name|get
argument_list|(
name|CLUSTER_ROUTING_REBALANCE_ENABLE
argument_list|,
name|this
operator|.
name|enableRebalance
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableRebalance
operator|!=
name|this
operator|.
name|enableRebalance
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"updating [{}] from [{}] to [{}]"
argument_list|,
name|CLUSTER_ROUTING_REBALANCE_ENABLE
argument_list|,
name|this
operator|.
name|enableRebalance
argument_list|,
name|enableRebalance
argument_list|)
expr_stmt|;
name|EnableAllocationDecider
operator|.
name|this
operator|.
name|enableRebalance
operator|=
name|enableRebalance
expr_stmt|;
block|}
block|}
comment|/**      * Allocation values or rather their string representation to be used used with      * {@link EnableAllocationDecider#CLUSTER_ROUTING_ALLOCATION_ENABLE} / {@link EnableAllocationDecider#INDEX_ROUTING_ALLOCATION_ENABLE}      * via cluster / index settings.      */
DECL|enum|Allocation
specifier|public
enum|enum
name|Allocation
block|{
DECL|enum constant|NONE
name|NONE
block|,
DECL|enum constant|NEW_PRIMARIES
name|NEW_PRIMARIES
block|,
DECL|enum constant|PRIMARIES
name|PRIMARIES
block|,
DECL|enum constant|ALL
name|ALL
block|;
DECL|method|parse
specifier|public
specifier|static
name|Allocation
name|parse
parameter_list|(
name|String
name|strValue
parameter_list|)
block|{
if|if
condition|(
name|strValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|strValue
operator|=
name|strValue
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|Allocation
operator|.
name|valueOf
argument_list|(
name|strValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal allocation.enable value ["
operator|+
name|strValue
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|/**      * Rebalance values or rather their string representation to be used used with      * {@link EnableAllocationDecider#CLUSTER_ROUTING_REBALANCE_ENABLE} / {@link EnableAllocationDecider#INDEX_ROUTING_REBALANCE_ENABLE}      * via cluster / index settings.      */
DECL|enum|Rebalance
specifier|public
enum|enum
name|Rebalance
block|{
DECL|enum constant|NONE
name|NONE
block|,
DECL|enum constant|PRIMARIES
name|PRIMARIES
block|,
DECL|enum constant|REPLICAS
name|REPLICAS
block|,
DECL|enum constant|ALL
name|ALL
block|;
DECL|method|parse
specifier|public
specifier|static
name|Rebalance
name|parse
parameter_list|(
name|String
name|strValue
parameter_list|)
block|{
if|if
condition|(
name|strValue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|strValue
operator|=
name|strValue
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|Rebalance
operator|.
name|valueOf
argument_list|(
name|strValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal rebalance.enable value ["
operator|+
name|strValue
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

