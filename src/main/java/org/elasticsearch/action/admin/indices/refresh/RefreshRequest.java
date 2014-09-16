begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.refresh
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
name|refresh
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
name|ActionRequest
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
name|BroadcastOperationRequest
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * A refresh request making all operations performed since the last refresh available for search. The (near) real-time  * capabilities depends on the index engine used. For example, the internal one requires refresh to be called, but by  * default a refresh is scheduled periodically.  *  * @see org.elasticsearch.client.Requests#refreshRequest(String...)  * @see org.elasticsearch.client.IndicesAdminClient#refresh(RefreshRequest)  * @see RefreshResponse  */
end_comment

begin_class
DECL|class|RefreshRequest
specifier|public
class|class
name|RefreshRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|RefreshRequest
argument_list|>
block|{
DECL|field|force
specifier|private
name|boolean
name|force
init|=
literal|true
decl_stmt|;
DECL|method|RefreshRequest
name|RefreshRequest
parameter_list|()
block|{     }
comment|/**      * Copy constructor that creates a new refresh request that is a copy of the one provided as an argument.      * The new request will inherit though headers and context from the original request that caused it.      */
DECL|method|RefreshRequest
specifier|public
name|RefreshRequest
parameter_list|(
name|ActionRequest
name|originalRequest
parameter_list|)
block|{
name|super
argument_list|(
name|originalRequest
argument_list|)
expr_stmt|;
block|}
DECL|method|RefreshRequest
specifier|public
name|RefreshRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|)
expr_stmt|;
block|}
DECL|method|force
specifier|public
name|boolean
name|force
parameter_list|()
block|{
return|return
name|force
return|;
block|}
comment|/**      * Forces calling refresh, overriding the check that dirty operations even happened. Defaults      * to true (note, still lightweight if no refresh is needed).      */
DECL|method|force
specifier|public
name|RefreshRequest
name|force
parameter_list|(
name|boolean
name|force
parameter_list|)
block|{
name|this
operator|.
name|force
operator|=
name|force
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|force
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|force
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

