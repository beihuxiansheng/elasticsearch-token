begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.flush
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
name|flush
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
DECL|class|FlushRequestBuilder
specifier|public
class|class
name|FlushRequestBuilder
extends|extends
name|BroadcastOperationRequestBuilder
argument_list|<
name|FlushRequest
argument_list|,
name|FlushResponse
argument_list|,
name|FlushRequestBuilder
argument_list|>
block|{
DECL|method|FlushRequestBuilder
specifier|public
name|FlushRequestBuilder
parameter_list|(
name|IndicesAdminClient
name|indicesClient
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
name|FlushRequest
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setFull
specifier|public
name|FlushRequestBuilder
name|setFull
parameter_list|(
name|boolean
name|full
parameter_list|)
block|{
name|request
operator|.
name|full
argument_list|(
name|full
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
name|FlushResponse
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
name|flush
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

