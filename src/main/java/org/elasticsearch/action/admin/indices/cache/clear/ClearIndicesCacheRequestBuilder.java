begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.cache.clear
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
name|cache
operator|.
name|clear
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
name|broadcast
operator|.
name|BroadcastOperationRequestBuilder
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
name|Client
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClearIndicesCacheRequestBuilder
specifier|public
class|class
name|ClearIndicesCacheRequestBuilder
extends|extends
name|BroadcastOperationRequestBuilder
argument_list|<
name|ClearIndicesCacheRequest
argument_list|,
name|ClearIndicesCacheResponse
argument_list|,
name|ClearIndicesCacheRequestBuilder
argument_list|,
name|IndicesAdminClient
argument_list|>
block|{
DECL|method|ClearIndicesCacheRequestBuilder
specifier|public
name|ClearIndicesCacheRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|indicesClient
parameter_list|)
block|{
name|super
argument_list|(
name|indicesClient
argument_list|,
operator|new
name|ClearIndicesCacheRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setFilterCache
specifier|public
name|ClearIndicesCacheRequestBuilder
name|setFilterCache
parameter_list|(
name|boolean
name|filterCache
parameter_list|)
block|{
name|request
operator|.
name|filterCache
argument_list|(
name|filterCache
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setQueryCache
specifier|public
name|ClearIndicesCacheRequestBuilder
name|setQueryCache
parameter_list|(
name|boolean
name|queryCache
parameter_list|)
block|{
name|request
operator|.
name|queryCache
argument_list|(
name|queryCache
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFieldDataCache
specifier|public
name|ClearIndicesCacheRequestBuilder
name|setFieldDataCache
parameter_list|(
name|boolean
name|fieldDataCache
parameter_list|)
block|{
name|request
operator|.
name|fieldDataCache
argument_list|(
name|fieldDataCache
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setFields
specifier|public
name|ClearIndicesCacheRequestBuilder
name|setFields
parameter_list|(
name|String
modifier|...
name|fields
parameter_list|)
block|{
name|request
operator|.
name|fields
argument_list|(
name|fields
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|setIdCache
specifier|public
name|ClearIndicesCacheRequestBuilder
name|setIdCache
parameter_list|(
name|boolean
name|idCache
parameter_list|)
block|{
name|request
operator|.
name|idCache
argument_list|(
name|idCache
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
name|ClearIndicesCacheResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|clearCache
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

