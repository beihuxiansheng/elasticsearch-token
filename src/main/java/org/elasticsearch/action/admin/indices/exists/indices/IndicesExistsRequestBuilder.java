begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.exists.indices
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
name|exists
operator|.
name|indices
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
name|MasterNodeReadOperationRequestBuilder
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
name|client
operator|.
name|internal
operator|.
name|InternalIndicesAdminClient
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IndicesExistsRequestBuilder
specifier|public
class|class
name|IndicesExistsRequestBuilder
extends|extends
name|MasterNodeReadOperationRequestBuilder
argument_list|<
name|IndicesExistsRequest
argument_list|,
name|IndicesExistsResponse
argument_list|,
name|IndicesExistsRequestBuilder
argument_list|>
block|{
DECL|method|IndicesExistsRequestBuilder
specifier|public
name|IndicesExistsRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|indicesClient
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
operator|(
name|InternalIndicesAdminClient
operator|)
name|indicesClient
argument_list|,
operator|new
name|IndicesExistsRequest
argument_list|(
name|indices
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setIndices
specifier|public
name|IndicesExistsRequestBuilder
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
comment|/**      * Specifies what type of requested indices to ignore and wildcard indices expressions.      *      * For example indices that don't exist.      */
DECL|method|setIndicesOptions
specifier|public
name|IndicesExistsRequestBuilder
name|setIndicesOptions
parameter_list|(
name|IndicesOptions
name|options
parameter_list|)
block|{
name|request
operator|.
name|indicesOptions
argument_list|(
name|options
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
name|IndicesExistsResponse
argument_list|>
name|listener
parameter_list|)
block|{
operator|(
operator|(
name|IndicesAdminClient
operator|)
name|client
operator|)
operator|.
name|exists
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

