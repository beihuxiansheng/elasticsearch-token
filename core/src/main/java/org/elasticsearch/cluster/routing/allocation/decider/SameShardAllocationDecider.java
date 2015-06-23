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

begin_comment
comment|/**  * An allocation decider that prevents multiple instances of the same shard to  * be allocated on the same<tt>node</tt>.  *  * The {@value #SAME_HOST_SETTING} setting allows to perform a check to prevent  * allocation of multiple instances of the same shard on a single<tt>host</tt>,  * based on host name and host address. Defaults to `false`, meaning that no  * check is performed by default.  *  *<p>  * Note: this setting only applies if multiple nodes are started on the same  *<tt>host</tt>. Allocations of multiple copies of the same shard on the same  *<tt>node</tt> are not allowed independently of this setting.  *</p>  */
end_comment

begin_class
DECL|class|SameShardAllocationDecider
specifier|public
class|class
name|SameShardAllocationDecider
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
literal|"same_shard"
decl_stmt|;
DECL|field|SAME_HOST_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|SAME_HOST_SETTING
init|=
literal|"cluster.routing.allocation.same_shard.host"
decl_stmt|;
DECL|field|sameHost
specifier|private
specifier|final
name|boolean
name|sameHost
decl_stmt|;
annotation|@
name|Inject
DECL|method|SameShardAllocationDecider
specifier|public
name|SameShardAllocationDecider
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
name|sameHost
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|SAME_HOST_SETTING
argument_list|,
literal|false
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
name|Iterable
argument_list|<
name|ShardRouting
argument_list|>
name|assignedShards
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|assignedShards
argument_list|(
name|shardRouting
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardRouting
name|assignedShard
range|:
name|assignedShards
control|)
block|{
if|if
condition|(
name|node
operator|.
name|nodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|assignedShard
operator|.
name|currentNodeId
argument_list|()
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
literal|"shard cannot be allocated on same node [%s] it already exists on"
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|sameHost
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|node
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|RoutingNode
name|checkNode
range|:
name|allocation
operator|.
name|routingNodes
argument_list|()
control|)
block|{
if|if
condition|(
name|checkNode
operator|.
name|node
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// check if its on the same host as the one we want to allocate to
name|boolean
name|checkNodeOnSameHost
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|checkNode
operator|.
name|node
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
operator|&&
name|Strings
operator|.
name|hasLength
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkNode
operator|.
name|node
argument_list|()
operator|.
name|getHostAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
condition|)
block|{
name|checkNodeOnSameHost
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|checkNode
operator|.
name|node
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
operator|&&
name|Strings
operator|.
name|hasLength
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|checkNode
operator|.
name|node
argument_list|()
operator|.
name|getHostName
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|getHostName
argument_list|()
argument_list|)
condition|)
block|{
name|checkNodeOnSameHost
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|checkNodeOnSameHost
condition|)
block|{
for|for
control|(
name|ShardRouting
name|assignedShard
range|:
name|assignedShards
control|)
block|{
if|if
condition|(
name|checkNode
operator|.
name|nodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|assignedShard
operator|.
name|currentNodeId
argument_list|()
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
literal|"shard cannot be allocated on same host [%s] it already exists on"
argument_list|,
name|node
operator|.
name|nodeId
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
block|}
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
literal|"shard is not allocated to same node or host"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

