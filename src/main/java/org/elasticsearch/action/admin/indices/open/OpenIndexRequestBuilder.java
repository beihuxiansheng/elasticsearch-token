begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.open
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
name|open
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
name|IndicesAdminClient
import|;
end_import

begin_comment
comment|/**  * Builder for for open index request  */
end_comment

begin_class
DECL|class|OpenIndexRequestBuilder
specifier|public
class|class
name|OpenIndexRequestBuilder
extends|extends
name|AcknowledgedRequestBuilder
argument_list|<
name|OpenIndexRequest
argument_list|,
name|OpenIndexResponse
argument_list|,
name|OpenIndexRequestBuilder
argument_list|,
name|IndicesAdminClient
argument_list|>
block|{
DECL|method|OpenIndexRequestBuilder
specifier|public
name|OpenIndexRequestBuilder
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
name|OpenIndexRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|OpenIndexRequestBuilder
specifier|public
name|OpenIndexRequestBuilder
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
name|indicesClient
argument_list|,
operator|new
name|OpenIndexRequest
argument_list|(
name|indices
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the indices to be opened      * @param indices the indices to be opened      * @return the request itself      */
DECL|method|setIndices
specifier|public
name|OpenIndexRequestBuilder
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
comment|/**      * Specifies what type of requested indices to ignore and how to deal with wildcard indices expressions.      * For example indices that don't exist.      *      * @param indicesOptions the desired behaviour regarding indices to ignore and wildcard indices expressions      * @return the request itself      */
DECL|method|setIndicesOptions
specifier|public
name|OpenIndexRequestBuilder
name|setIndicesOptions
parameter_list|(
name|IndicesOptions
name|indicesOptions
parameter_list|)
block|{
name|request
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
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|ActionListener
argument_list|<
name|OpenIndexResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|client
operator|.
name|open
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

