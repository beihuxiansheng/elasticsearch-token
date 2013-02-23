begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.stats
package|package
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
name|node
operator|.
name|stats
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
name|support
operator|.
name|nodes
operator|.
name|NodesOperationRequestBuilder
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
name|ClusterAdminClient
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
comment|/**  *  */
end_comment

begin_class
DECL|class|NodesStatsRequestBuilder
specifier|public
class|class
name|NodesStatsRequestBuilder
extends|extends
name|NodesOperationRequestBuilder
argument_list|<
name|NodesStatsRequest
argument_list|,
name|NodesStatsResponse
argument_list|,
name|NodesStatsRequestBuilder
argument_list|>
block|{
DECL|method|NodesStatsRequestBuilder
specifier|public
name|NodesStatsRequestBuilder
parameter_list|(
name|ClusterAdminClient
name|clusterClient
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|InternalClusterAdminClient
operator|)
name|clusterClient
argument_list|,
operator|new
name|NodesStatsRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets all the request flags.      */
DECL|method|all
specifier|public
name|NodesStatsRequestBuilder
name|all
parameter_list|()
block|{
name|request
operator|.
name|all
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Clears all stats flags.      */
DECL|method|clear
specifier|public
name|NodesStatsRequestBuilder
name|clear
parameter_list|()
block|{
name|request
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node indices stats be returned.      */
DECL|method|setIndices
specifier|public
name|NodesStatsRequestBuilder
name|setIndices
parameter_list|(
name|boolean
name|indices
parameter_list|)
block|{
name|request
operator|.
name|indices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node OS stats be returned.      */
DECL|method|setOs
specifier|public
name|NodesStatsRequestBuilder
name|setOs
parameter_list|(
name|boolean
name|os
parameter_list|)
block|{
name|request
operator|.
name|os
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node OS stats be returned.      */
DECL|method|setProcess
specifier|public
name|NodesStatsRequestBuilder
name|setProcess
parameter_list|(
name|boolean
name|process
parameter_list|)
block|{
name|request
operator|.
name|process
argument_list|(
name|process
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node JVM stats be returned.      */
DECL|method|setJvm
specifier|public
name|NodesStatsRequestBuilder
name|setJvm
parameter_list|(
name|boolean
name|jvm
parameter_list|)
block|{
name|request
operator|.
name|jvm
argument_list|(
name|jvm
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node thread pool stats be returned.      */
DECL|method|setThreadPool
specifier|public
name|NodesStatsRequestBuilder
name|setThreadPool
parameter_list|(
name|boolean
name|threadPool
parameter_list|)
block|{
name|request
operator|.
name|threadPool
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node Network stats be returned.      */
DECL|method|setNetwork
specifier|public
name|NodesStatsRequestBuilder
name|setNetwork
parameter_list|(
name|boolean
name|network
parameter_list|)
block|{
name|request
operator|.
name|network
argument_list|(
name|network
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node file system stats be returned.      */
DECL|method|setFs
specifier|public
name|NodesStatsRequestBuilder
name|setFs
parameter_list|(
name|boolean
name|fs
parameter_list|)
block|{
name|request
operator|.
name|fs
argument_list|(
name|fs
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node Transport stats be returned.      */
DECL|method|setTransport
specifier|public
name|NodesStatsRequestBuilder
name|setTransport
parameter_list|(
name|boolean
name|transport
parameter_list|)
block|{
name|request
operator|.
name|transport
argument_list|(
name|transport
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node HTTP stats be returned.      */
DECL|method|setHttp
specifier|public
name|NodesStatsRequestBuilder
name|setHttp
parameter_list|(
name|boolean
name|http
parameter_list|)
block|{
name|request
operator|.
name|http
argument_list|(
name|http
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|NodesStatsResponse
argument_list|>
name|listener
parameter_list|)
block|{
operator|(
operator|(
name|ClusterAdminClient
operator|)
name|client
operator|)
operator|.
name|nodesStats
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

