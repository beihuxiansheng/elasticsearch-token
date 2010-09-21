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
name|RoutingTable
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RoutingAllocation
specifier|public
class|class
name|RoutingAllocation
block|{
DECL|class|Result
specifier|public
specifier|static
class|class
name|Result
block|{
DECL|field|changed
specifier|private
specifier|final
name|boolean
name|changed
decl_stmt|;
DECL|field|routingTable
specifier|private
specifier|final
name|RoutingTable
name|routingTable
decl_stmt|;
DECL|field|explanation
specifier|private
specifier|final
name|AllocationExplanation
name|explanation
decl_stmt|;
DECL|method|Result
specifier|public
name|Result
parameter_list|(
name|boolean
name|changed
parameter_list|,
name|RoutingTable
name|routingTable
parameter_list|,
name|AllocationExplanation
name|explanation
parameter_list|)
block|{
name|this
operator|.
name|changed
operator|=
name|changed
expr_stmt|;
name|this
operator|.
name|routingTable
operator|=
name|routingTable
expr_stmt|;
name|this
operator|.
name|explanation
operator|=
name|explanation
expr_stmt|;
block|}
DECL|method|changed
specifier|public
name|boolean
name|changed
parameter_list|()
block|{
return|return
name|this
operator|.
name|changed
return|;
block|}
DECL|method|routingTable
specifier|public
name|RoutingTable
name|routingTable
parameter_list|()
block|{
return|return
name|routingTable
return|;
block|}
DECL|method|explanation
specifier|public
name|AllocationExplanation
name|explanation
parameter_list|()
block|{
return|return
name|explanation
return|;
block|}
block|}
DECL|field|routingNodes
specifier|private
specifier|final
name|RoutingNodes
name|routingNodes
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|DiscoveryNodes
name|nodes
decl_stmt|;
DECL|field|explanation
specifier|private
specifier|final
name|AllocationExplanation
name|explanation
init|=
operator|new
name|AllocationExplanation
argument_list|()
decl_stmt|;
DECL|method|RoutingAllocation
specifier|public
name|RoutingAllocation
parameter_list|(
name|RoutingNodes
name|routingNodes
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|routingNodes
operator|=
name|routingNodes
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
DECL|method|routingTable
specifier|public
name|RoutingTable
name|routingTable
parameter_list|()
block|{
return|return
name|routingNodes
operator|.
name|routingTable
argument_list|()
return|;
block|}
DECL|method|routingNodes
specifier|public
name|RoutingNodes
name|routingNodes
parameter_list|()
block|{
return|return
name|routingNodes
return|;
block|}
DECL|method|nodes
specifier|public
name|DiscoveryNodes
name|nodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
DECL|method|explanation
specifier|public
name|AllocationExplanation
name|explanation
parameter_list|()
block|{
return|return
name|explanation
return|;
block|}
block|}
end_class

end_unit

