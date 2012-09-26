begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.gateway.snapshot
package|package
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
name|gateway
operator|.
name|snapshot
package|;
end_package

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
name|broadcast
operator|.
name|BroadcastOperationRequest
import|;
end_import

begin_comment
comment|/**  * Gateway snapshot allows to explicitly perform a snapshot through the gateway of one or more indices (backup them).  * By default, each index gateway periodically snapshot changes, though it can be disabled and be controlled completely  * through this API. Best created using {@link org.elasticsearch.client.Requests#gatewaySnapshotRequest(String...)}.  *  * @see org.elasticsearch.client.Requests#gatewaySnapshotRequest(String...)  * @see org.elasticsearch.client.IndicesAdminClient#gatewaySnapshot(GatewaySnapshotRequest)  * @see GatewaySnapshotResponse  */
end_comment

begin_class
DECL|class|GatewaySnapshotRequest
specifier|public
class|class
name|GatewaySnapshotRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|GatewaySnapshotRequest
argument_list|>
block|{
DECL|method|GatewaySnapshotRequest
name|GatewaySnapshotRequest
parameter_list|()
block|{      }
comment|/**      * Constructs a new gateway snapshot against one or more indices. No indices means the gateway snapshot      * will be executed against all indices.      */
DECL|method|GatewaySnapshotRequest
specifier|public
name|GatewaySnapshotRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
block|}
block|}
end_class

end_unit

