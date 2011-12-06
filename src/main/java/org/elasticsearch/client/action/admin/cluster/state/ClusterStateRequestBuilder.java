begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|support
operator|.
name|BaseClusterRequestBuilder
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterStateRequestBuilder
specifier|public
class|class
name|ClusterStateRequestBuilder
extends|extends
name|BaseClusterRequestBuilder
argument_list|<
name|ClusterStateRequest
argument_list|,
name|ClusterStateResponse
argument_list|>
block|{
DECL|method|ClusterStateRequestBuilder
specifier|public
name|ClusterStateRequestBuilder
parameter_list|(
name|ClusterAdminClient
name|clusterClient
parameter_list|)
block|{
name|super
argument_list|(
name|clusterClient
argument_list|,
operator|new
name|ClusterStateRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Should the cluster state result include the {@link org.elasticsearch.cluster.metadata.MetaData}. Defaults      * to<tt>false</tt>.      */
DECL|method|setFilterMetaData
specifier|public
name|ClusterStateRequestBuilder
name|setFilterMetaData
parameter_list|(
name|boolean
name|filter
parameter_list|)
block|{
name|request
operator|.
name|filterMetaData
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the cluster state result include the {@link org.elasticsearch.cluster.node.DiscoveryNodes}. Defaults      * to<tt>false</tt>.      */
DECL|method|setFilterNodes
specifier|public
name|ClusterStateRequestBuilder
name|setFilterNodes
parameter_list|(
name|boolean
name|filter
parameter_list|)
block|{
name|request
operator|.
name|filterNodes
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the cluster state result include teh {@link org.elasticsearch.cluster.routing.RoutingTable}. Defaults      * to<tt>false</tt>.      */
DECL|method|setFilterRoutingTable
specifier|public
name|ClusterStateRequestBuilder
name|setFilterRoutingTable
parameter_list|(
name|boolean
name|filter
parameter_list|)
block|{
name|request
operator|.
name|filterRoutingTable
argument_list|(
name|filter
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * When {@link #setFilterMetaData(boolean)} is not set, which indices to return the {@link org.elasticsearch.cluster.metadata.IndexMetaData}      * for. Defaults to all indices.      */
DECL|method|setFilterIndices
specifier|public
name|ClusterStateRequestBuilder
name|setFilterIndices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|request
operator|.
name|filteredIndices
argument_list|(
name|indices
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the master node timeout in case the master has not yet been discovered.      */
DECL|method|setMasterNodeTimeout
specifier|public
name|ClusterStateRequestBuilder
name|setMasterNodeTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|masterNodeTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the master node timeout in case the master has not yet been discovered.      */
DECL|method|setMasterNodeTimeout
specifier|public
name|ClusterStateRequestBuilder
name|setMasterNodeTimeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|request
operator|.
name|masterNodeTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets if the cluster state request should be executed locally on the node, and not go to the master.      */
DECL|method|setLocal
specifier|public
name|ClusterStateRequestBuilder
name|setLocal
parameter_list|(
name|boolean
name|local
parameter_list|)
block|{
name|request
operator|.
name|local
argument_list|(
name|local
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
name|ClusterStateResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
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

