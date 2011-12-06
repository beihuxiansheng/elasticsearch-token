begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.none
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|none
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
name|allocation
operator|.
name|FailedRerouteAllocation
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
name|StartedRerouteAllocation
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
name|GatewayAllocator
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|NoneGatewayAllocator
specifier|public
class|class
name|NoneGatewayAllocator
implements|implements
name|GatewayAllocator
block|{
annotation|@
name|Override
DECL|method|applyStartedShards
specifier|public
name|void
name|applyStartedShards
parameter_list|(
name|StartedRerouteAllocation
name|allocation
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|applyFailedShards
specifier|public
name|void
name|applyFailedShards
parameter_list|(
name|FailedRerouteAllocation
name|allocation
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|allocateUnassigned
specifier|public
name|boolean
name|allocateUnassigned
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

