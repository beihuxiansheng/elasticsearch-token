begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.shards
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
name|shards
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
name|IndicesOptions
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
name|master
operator|.
name|MasterNodeOperationRequestBuilder
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
comment|/**  */
end_comment

begin_class
DECL|class|ClusterSearchShardsRequestBuilder
specifier|public
class|class
name|ClusterSearchShardsRequestBuilder
extends|extends
name|MasterNodeOperationRequestBuilder
argument_list|<
name|ClusterSearchShardsRequest
argument_list|,
name|ClusterSearchShardsResponse
argument_list|,
name|ClusterSearchShardsRequestBuilder
argument_list|>
block|{
DECL|method|ClusterSearchShardsRequestBuilder
specifier|public
name|ClusterSearchShardsRequestBuilder
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
name|ClusterSearchShardsRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the indices the search will be executed on.      */
DECL|method|setIndices
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setIndices
parameter_list|(
name|String
modifier|...
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
comment|/**      * The document types to execute the search against. Defaults to be executed against      * all types.      */
DECL|method|setTypes
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setTypes
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
name|request
operator|.
name|types
argument_list|(
name|types
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * A comma separated list of routing values to control the shards the search will be executed on.      */
DECL|method|setRouting
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setRouting
parameter_list|(
name|String
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * The routing values to control the shards that the search will be executed on.      */
DECL|method|setRouting
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setRouting
parameter_list|(
name|String
modifier|...
name|routing
parameter_list|)
block|{
name|request
operator|.
name|routing
argument_list|(
name|routing
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the preference to execute the search. Defaults to randomize across shards. Can be set to      *<tt>_local</tt> to prefer local shards,<tt>_primary</tt> to execute only on primary shards, or      * a custom value, which guarantees that the same order will be used across different requests.      */
DECL|method|setPreference
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setPreference
parameter_list|(
name|String
name|preference
parameter_list|)
block|{
name|request
operator|.
name|preference
argument_list|(
name|preference
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specifies what type of requested indices to ignore and how to deal indices wildcard expressions.      * For example indices that don't exist.      */
DECL|method|setIndicesOptions
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setIndicesOptions
parameter_list|(
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
name|request
argument_list|()
operator|.
name|indicesOptions
argument_list|(
name|indicesOptions
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Specifies if request should be executed on local node rather than on master.      */
DECL|method|setLocal
specifier|public
name|ClusterSearchShardsRequestBuilder
name|setLocal
parameter_list|(
name|boolean
name|local
parameter_list|)
block|{
name|request
argument_list|()
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
name|ClusterSearchShardsResponse
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
name|searchShards
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

