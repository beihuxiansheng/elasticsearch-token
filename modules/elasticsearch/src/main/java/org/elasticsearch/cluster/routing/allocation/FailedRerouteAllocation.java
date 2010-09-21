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
name|ShardRouting
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FailedRerouteAllocation
specifier|public
class|class
name|FailedRerouteAllocation
extends|extends
name|RoutingAllocation
block|{
DECL|field|failedShards
specifier|private
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|failedShards
decl_stmt|;
DECL|method|FailedRerouteAllocation
specifier|public
name|FailedRerouteAllocation
parameter_list|(
name|RoutingNodes
name|routingNodes
parameter_list|,
name|DiscoveryNodes
name|nodes
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|failedShards
parameter_list|)
block|{
name|super
argument_list|(
name|routingNodes
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|failedShards
operator|=
name|failedShards
expr_stmt|;
block|}
DECL|method|failedShards
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|ShardRouting
argument_list|>
name|failedShards
parameter_list|()
block|{
return|return
name|failedShards
return|;
block|}
block|}
end_class

end_unit

