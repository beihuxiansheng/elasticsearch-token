begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.repositories.put
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
name|repositories
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
name|master
operator|.
name|AcknowledgedRequestBuilder
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Register repository request builder  */
end_comment

begin_class
DECL|class|PutRepositoryRequestBuilder
specifier|public
class|class
name|PutRepositoryRequestBuilder
extends|extends
name|AcknowledgedRequestBuilder
argument_list|<
name|PutRepositoryRequest
argument_list|,
name|PutRepositoryResponse
argument_list|,
name|PutRepositoryRequestBuilder
argument_list|>
block|{
comment|/**      * Constructs register repository request      *      * @param clusterAdminClient cluster admin client      */
DECL|method|PutRepositoryRequestBuilder
specifier|public
name|PutRepositoryRequestBuilder
parameter_list|(
name|ClusterAdminClient
name|clusterAdminClient
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|InternalClusterAdminClient
operator|)
name|clusterAdminClient
argument_list|,
operator|new
name|PutRepositoryRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs register repository request for the repository with a given name      *      * @param clusterAdminClient cluster admin client      * @param name               repository name      */
DECL|method|PutRepositoryRequestBuilder
specifier|public
name|PutRepositoryRequestBuilder
parameter_list|(
name|ClusterAdminClient
name|clusterAdminClient
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|InternalClusterAdminClient
operator|)
name|clusterAdminClient
argument_list|,
operator|new
name|PutRepositoryRequest
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the repository name      *      * @param name repository name      * @return this builder      */
DECL|method|setName
specifier|public
name|PutRepositoryRequestBuilder
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|request
operator|.
name|name
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the repository type      *      * @param type repository type      * @return this builder      */
DECL|method|setType
specifier|public
name|PutRepositoryRequestBuilder
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|request
operator|.
name|type
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the repository settings      *      * @param settings repository settings      * @return this builder      */
DECL|method|setSettings
specifier|public
name|PutRepositoryRequestBuilder
name|setSettings
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the repository settings      *      * @param settings repository settings builder      * @return this builder      */
DECL|method|setSettings
specifier|public
name|PutRepositoryRequestBuilder
name|setSettings
parameter_list|(
name|Settings
operator|.
name|Builder
name|settings
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the repository settings in Json, Yaml or properties format      *      * @param source repository settings      * @return this builder      */
DECL|method|setSettings
specifier|public
name|PutRepositoryRequestBuilder
name|setSettings
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the repository settings      *      * @param source repository settings      * @return this builder      */
DECL|method|setSettings
specifier|public
name|PutRepositoryRequestBuilder
name|setSettings
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|source
parameter_list|)
block|{
name|request
operator|.
name|settings
argument_list|(
name|source
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
name|PutRepositoryResponse
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
name|putRepository
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

