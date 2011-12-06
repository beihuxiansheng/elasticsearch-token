begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|node
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|AdminClient
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
name|IndicesAdminClient
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
name|component
operator|.
name|AbstractComponent
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
name|inject
operator|.
name|Inject
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
comment|/**  *  */
end_comment

begin_class
DECL|class|NodeAdminClient
specifier|public
class|class
name|NodeAdminClient
extends|extends
name|AbstractComponent
implements|implements
name|AdminClient
block|{
DECL|field|indicesAdminClient
specifier|private
specifier|final
name|NodeIndicesAdminClient
name|indicesAdminClient
decl_stmt|;
DECL|field|clusterAdminClient
specifier|private
specifier|final
name|NodeClusterAdminClient
name|clusterAdminClient
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeAdminClient
specifier|public
name|NodeAdminClient
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NodeClusterAdminClient
name|clusterAdminClient
parameter_list|,
name|NodeIndicesAdminClient
name|indicesAdminClient
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicesAdminClient
operator|=
name|indicesAdminClient
expr_stmt|;
name|this
operator|.
name|clusterAdminClient
operator|=
name|clusterAdminClient
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|IndicesAdminClient
name|indices
parameter_list|()
block|{
return|return
name|indicesAdminClient
return|;
block|}
annotation|@
name|Override
DECL|method|cluster
specifier|public
name|ClusterAdminClient
name|cluster
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterAdminClient
return|;
block|}
block|}
end_class

end_unit

