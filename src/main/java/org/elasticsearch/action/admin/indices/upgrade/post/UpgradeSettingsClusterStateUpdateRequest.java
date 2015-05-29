begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.upgrade.post
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
name|upgrade
operator|.
name|post
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
name|ClusterStateUpdateRequest
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
comment|/**  * Cluster state update request that allows to change minimum compatibility settings for some indices  */
end_comment

begin_class
DECL|class|UpgradeSettingsClusterStateUpdateRequest
specifier|public
class|class
name|UpgradeSettingsClusterStateUpdateRequest
extends|extends
name|ClusterStateUpdateRequest
argument_list|<
name|UpgradeSettingsClusterStateUpdateRequest
argument_list|>
block|{
DECL|field|versions
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|versions
decl_stmt|;
DECL|method|UpgradeSettingsClusterStateUpdateRequest
specifier|public
name|UpgradeSettingsClusterStateUpdateRequest
parameter_list|()
block|{      }
comment|/**      * Returns the index to version map for indices that should be updated      */
DECL|method|versions
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|versions
parameter_list|()
block|{
return|return
name|versions
return|;
block|}
comment|/**      * Sets the index to version map for indices that should be updated      */
DECL|method|versions
specifier|public
name|UpgradeSettingsClusterStateUpdateRequest
name|versions
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|versions
parameter_list|)
block|{
name|this
operator|.
name|versions
operator|=
name|versions
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

