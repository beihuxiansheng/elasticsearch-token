begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.action.admin.cluster.settings
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
name|settings
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
name|settings
operator|.
name|ClusterUpdateSettingsRequest
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
name|settings
operator|.
name|ClusterUpdateSettingsResponse
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
name|settings
operator|.
name|Settings
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ClusterUpdateSettingsRequestBuilder
specifier|public
class|class
name|ClusterUpdateSettingsRequestBuilder
extends|extends
name|BaseClusterRequestBuilder
argument_list|<
name|ClusterUpdateSettingsRequest
argument_list|,
name|ClusterUpdateSettingsResponse
argument_list|>
block|{
DECL|method|ClusterUpdateSettingsRequestBuilder
specifier|public
name|ClusterUpdateSettingsRequestBuilder
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
name|ClusterUpdateSettingsRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setTransientSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setTransientSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|request
operator|.
name|transientSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTransientSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setTransientSettings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|request
operator|.
name|transientSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTransientSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setTransientSettings
parameter_list|(
name|String
name|settings
parameter_list|)
block|{
name|request
operator|.
name|transientSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setTransientSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setTransientSettings
parameter_list|(
name|Map
name|settings
parameter_list|)
block|{
name|request
operator|.
name|transientSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPersistentSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setPersistentSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|request
operator|.
name|persistentSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPersistentSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setPersistentSettings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|request
operator|.
name|persistentSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPersistentSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setPersistentSettings
parameter_list|(
name|String
name|settings
parameter_list|)
block|{
name|request
operator|.
name|persistentSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setPersistentSettings
specifier|public
name|ClusterUpdateSettingsRequestBuilder
name|setPersistentSettings
parameter_list|(
name|Map
name|settings
parameter_list|)
block|{
name|request
operator|.
name|persistentSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the master node timeout in case the master has not yet been discovered.      */
DECL|method|setMasterNodeTimeout
specifier|public
name|ClusterUpdateSettingsRequestBuilder
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
name|ClusterUpdateSettingsRequestBuilder
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
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|ClusterUpdateSettingsResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|updateSettings
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

