begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.status
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
name|status
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
name|Strings
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
comment|/**  *  */
end_comment

begin_class
DECL|class|IndicesStatusRequest
specifier|public
class|class
name|IndicesStatusRequest
extends|extends
name|BroadcastOperationRequest
argument_list|<
name|IndicesStatusRequest
argument_list|>
block|{
DECL|field|recovery
specifier|private
name|boolean
name|recovery
init|=
literal|false
decl_stmt|;
DECL|field|snapshot
specifier|private
name|boolean
name|snapshot
init|=
literal|false
decl_stmt|;
DECL|method|IndicesStatusRequest
specifier|public
name|IndicesStatusRequest
parameter_list|()
block|{
name|this
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
DECL|method|IndicesStatusRequest
specifier|public
name|IndicesStatusRequest
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
comment|/**      * Should the status include recovery information. Defaults to<tt>false</tt>.      */
DECL|method|recovery
specifier|public
name|IndicesStatusRequest
name|recovery
parameter_list|(
name|boolean
name|recovery
parameter_list|)
block|{
name|this
operator|.
name|recovery
operator|=
name|recovery
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|recovery
specifier|public
name|boolean
name|recovery
parameter_list|()
block|{
return|return
name|this
operator|.
name|recovery
return|;
block|}
comment|/**      * Should the status include recovery information. Defaults to<tt>false</tt>.      */
DECL|method|snapshot
specifier|public
name|IndicesStatusRequest
name|snapshot
parameter_list|(
name|boolean
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|snapshot
operator|=
name|snapshot
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|snapshot
specifier|public
name|boolean
name|snapshot
parameter_list|()
block|{
return|return
name|this
operator|.
name|snapshot
return|;
block|}
annotation|@
name|Override
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
name|recovery
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|recovery
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|snapshot
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

