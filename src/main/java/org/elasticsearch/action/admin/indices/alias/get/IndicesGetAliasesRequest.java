begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.alias.get
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
name|alias
operator|.
name|get
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
name|ActionRequestValidationException
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
name|IgnoreIndices
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
name|MasterNodeOperationRequest
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndicesGetAliasesRequest
specifier|public
class|class
name|IndicesGetAliasesRequest
extends|extends
name|MasterNodeOperationRequest
argument_list|<
name|IndicesGetAliasesRequest
argument_list|>
block|{
DECL|field|indices
specifier|private
name|String
index|[]
name|indices
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|aliases
specifier|private
name|String
index|[]
name|aliases
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|ignoreIndices
specifier|private
name|IgnoreIndices
name|ignoreIndices
init|=
name|IgnoreIndices
operator|.
name|NONE
decl_stmt|;
DECL|method|IndicesGetAliasesRequest
specifier|public
name|IndicesGetAliasesRequest
parameter_list|(
name|String
index|[]
name|aliases
parameter_list|)
block|{
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
block|}
DECL|method|IndicesGetAliasesRequest
specifier|public
name|IndicesGetAliasesRequest
parameter_list|(
name|String
name|alias
parameter_list|)
block|{
name|this
operator|.
name|aliases
operator|=
operator|new
name|String
index|[]
block|{
name|alias
block|}
expr_stmt|;
block|}
DECL|method|IndicesGetAliasesRequest
specifier|public
name|IndicesGetAliasesRequest
parameter_list|()
block|{     }
DECL|method|indices
specifier|public
name|IndicesGetAliasesRequest
name|indices
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|aliases
specifier|public
name|IndicesGetAliasesRequest
name|aliases
parameter_list|(
name|String
modifier|...
name|aliases
parameter_list|)
block|{
name|this
operator|.
name|aliases
operator|=
name|aliases
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|ignoreIndices
specifier|public
name|IndicesGetAliasesRequest
name|ignoreIndices
parameter_list|(
name|IgnoreIndices
name|ignoreIndices
parameter_list|)
block|{
name|this
operator|.
name|ignoreIndices
operator|=
name|ignoreIndices
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
DECL|method|aliases
specifier|public
name|String
index|[]
name|aliases
parameter_list|()
block|{
return|return
name|aliases
return|;
block|}
DECL|method|ignoreIndices
specifier|public
name|IgnoreIndices
name|ignoreIndices
parameter_list|()
block|{
return|return
name|ignoreIndices
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
if|if
condition|(
name|aliases
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|addValidationError
argument_list|(
literal|"No alias specified"
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
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
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|aliases
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|ignoreIndices
operator|=
name|IgnoreIndices
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
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
name|writeStringArray
argument_list|(
name|indices
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|aliases
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|ignoreIndices
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

