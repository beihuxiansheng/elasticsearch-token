begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.settings.put
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
name|settings
operator|.
name|put
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
name|ack
operator|.
name|IndicesClusterStateUpdateRequest
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
comment|/**  * Cluster state update request that allows to update settings for some indices  */
end_comment

begin_class
DECL|class|UpdateSettingsClusterStateUpdateRequest
specifier|public
class|class
name|UpdateSettingsClusterStateUpdateRequest
extends|extends
name|IndicesClusterStateUpdateRequest
argument_list|<
name|UpdateSettingsClusterStateUpdateRequest
argument_list|>
block|{
DECL|field|settings
specifier|private
name|Settings
name|settings
decl_stmt|;
DECL|method|UpdateSettingsClusterStateUpdateRequest
specifier|public
name|UpdateSettingsClusterStateUpdateRequest
parameter_list|()
block|{      }
comment|/**      * Returns the {@link Settings} to update      */
DECL|method|settings
specifier|public
name|Settings
name|settings
parameter_list|()
block|{
return|return
name|settings
return|;
block|}
comment|/**      * Sets the {@link Settings} to update      */
DECL|method|settings
specifier|public
name|UpdateSettingsClusterStateUpdateRequest
name|settings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

