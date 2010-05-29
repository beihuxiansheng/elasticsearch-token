begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.action.admin.cluster.state
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|state
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
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ListenableActionFuture
import|;
end_import

begin_import
import|import
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
name|state
operator|.
name|ClusterStateRequest
import|;
end_import

begin_import
import|import
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
name|state
operator|.
name|ClusterStateResponse
import|;
end_import

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
name|PlainListenableActionFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|internal
operator|.
name|InternalClusterAdminClient
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ClusterStateRequestBuilder
specifier|public
class|class
name|ClusterStateRequestBuilder
block|{
DECL|field|clusterClient
specifier|private
specifier|final
name|InternalClusterAdminClient
name|clusterClient
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|ClusterStateRequest
name|request
decl_stmt|;
DECL|method|ClusterStateRequestBuilder
specifier|public
name|ClusterStateRequestBuilder
parameter_list|(
name|InternalClusterAdminClient
name|clusterClient
parameter_list|)
block|{
name|this
operator|.
name|clusterClient
operator|=
name|clusterClient
expr_stmt|;
name|this
operator|.
name|request
operator|=
operator|new
name|ClusterStateRequest
argument_list|()
expr_stmt|;
block|}
comment|/**      * Executes the operation asynchronously and returns a future.      */
DECL|method|execute
specifier|public
name|ListenableActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
name|execute
parameter_list|()
block|{
name|PlainListenableActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
name|future
init|=
operator|new
name|PlainListenableActionFuture
argument_list|<
name|ClusterStateResponse
argument_list|>
argument_list|(
name|request
operator|.
name|listenerThreaded
argument_list|()
argument_list|,
name|clusterClient
operator|.
name|threadPool
argument_list|()
argument_list|)
decl_stmt|;
name|clusterClient
operator|.
name|state
argument_list|(
name|request
argument_list|,
name|future
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
comment|/**      * Executes the operation asynchronously with the provided listener.      */
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|(
name|ActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|clusterClient
operator|.
name|state
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

