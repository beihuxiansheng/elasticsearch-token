begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
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
name|MutableShardRouting
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
name|ShardRouting
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
comment|/**  * An allocation strategy that only allows for a replica to be allocated when the primary is active.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ReplicaAfterPrimaryActiveNodeAllocation
specifier|public
class|class
name|ReplicaAfterPrimaryActiveNodeAllocation
extends|extends
name|NodeAllocation
block|{
DECL|method|ReplicaAfterPrimaryActiveNodeAllocation
annotation|@
name|Inject
specifier|public
name|ReplicaAfterPrimaryActiveNodeAllocation
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
block|}
DECL|method|allocate
annotation|@
name|Override
specifier|public
name|boolean
name|allocate
parameter_list|(
name|NodeAllocations
name|nodeAllocations
parameter_list|,
name|RoutingNodes
name|routingNodes
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
return|return
literal|false
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
name|RoutingNodes
name|routingNodes
parameter_list|)
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
name|Decision
operator|.
name|YES
return|;
block|}
name|MutableShardRouting
name|primary
init|=
name|routingNodes
operator|.
name|findPrimaryForReplica
argument_list|(
name|shardRouting
argument_list|)
decl_stmt|;
if|if
condition|(
name|primary
operator|==
literal|null
operator|||
operator|!
name|primary
operator|.
name|active
argument_list|()
condition|)
block|{
return|return
name|Decision
operator|.
name|NO
return|;
block|}
return|return
name|Decision
operator|.
name|YES
return|;
block|}
block|}
end_class

end_unit

