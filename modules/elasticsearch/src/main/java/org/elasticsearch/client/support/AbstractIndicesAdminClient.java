begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|support
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
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|alias
operator|.
name|IndicesAliasesRequestBuilder
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
name|indices
operator|.
name|cache
operator|.
name|clear
operator|.
name|ClearIndicesCacheRequestBuilder
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
name|indices
operator|.
name|create
operator|.
name|CreateIndexRequestBuilder
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
name|indices
operator|.
name|delete
operator|.
name|DeleteIndexRequestBuilder
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
name|indices
operator|.
name|flush
operator|.
name|FlushRequestBuilder
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
name|indices
operator|.
name|gateway
operator|.
name|snapshot
operator|.
name|GatewaySnapshotRequestBuilder
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
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequestBuilder
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
name|indices
operator|.
name|optimize
operator|.
name|OptimizeRequestBuilder
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
name|indices
operator|.
name|refresh
operator|.
name|RefreshRequestBuilder
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
name|indices
operator|.
name|settings
operator|.
name|UpdateSettingsRequestBuilder
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
name|indices
operator|.
name|status
operator|.
name|IndicesStatusRequestBuilder
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
name|InternalIndicesAdminClient
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractIndicesAdminClient
specifier|public
specifier|abstract
class|class
name|AbstractIndicesAdminClient
implements|implements
name|InternalIndicesAdminClient
block|{
DECL|method|prepareAliases
annotation|@
name|Override
specifier|public
name|IndicesAliasesRequestBuilder
name|prepareAliases
parameter_list|()
block|{
return|return
operator|new
name|IndicesAliasesRequestBuilder
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|prepareClearCache
annotation|@
name|Override
specifier|public
name|ClearIndicesCacheRequestBuilder
name|prepareClearCache
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|ClearIndicesCacheRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|prepareCreate
annotation|@
name|Override
specifier|public
name|CreateIndexRequestBuilder
name|prepareCreate
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|CreateIndexRequestBuilder
argument_list|(
name|this
argument_list|,
name|index
argument_list|)
return|;
block|}
DECL|method|prepareDelete
annotation|@
name|Override
specifier|public
name|DeleteIndexRequestBuilder
name|prepareDelete
parameter_list|(
name|String
name|index
parameter_list|)
block|{
return|return
operator|new
name|DeleteIndexRequestBuilder
argument_list|(
name|this
argument_list|,
name|index
argument_list|)
return|;
block|}
DECL|method|prepareFlush
annotation|@
name|Override
specifier|public
name|FlushRequestBuilder
name|prepareFlush
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|FlushRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|prepareGatewaySnapshot
annotation|@
name|Override
specifier|public
name|GatewaySnapshotRequestBuilder
name|prepareGatewaySnapshot
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|GatewaySnapshotRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|preparePutMapping
annotation|@
name|Override
specifier|public
name|PutMappingRequestBuilder
name|preparePutMapping
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|PutMappingRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|prepareOptimize
annotation|@
name|Override
specifier|public
name|OptimizeRequestBuilder
name|prepareOptimize
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|OptimizeRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|prepareRefresh
annotation|@
name|Override
specifier|public
name|RefreshRequestBuilder
name|prepareRefresh
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|RefreshRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|prepareStatus
annotation|@
name|Override
specifier|public
name|IndicesStatusRequestBuilder
name|prepareStatus
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|IndicesStatusRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
DECL|method|prepareUpdateSettings
annotation|@
name|Override
specifier|public
name|UpdateSettingsRequestBuilder
name|prepareUpdateSettings
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
return|return
operator|new
name|UpdateSettingsRequestBuilder
argument_list|(
name|this
argument_list|)
operator|.
name|setIndices
argument_list|(
name|indices
argument_list|)
return|;
block|}
block|}
end_class

end_unit

