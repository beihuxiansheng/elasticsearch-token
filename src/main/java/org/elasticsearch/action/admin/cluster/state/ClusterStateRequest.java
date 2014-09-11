begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.state
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
name|state
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|IndicesRequest
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
name|MasterNodeReadOperationRequest
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
DECL|class|ClusterStateRequest
specifier|public
class|class
name|ClusterStateRequest
extends|extends
name|MasterNodeReadOperationRequest
argument_list|<
name|ClusterStateRequest
argument_list|>
implements|implements
name|IndicesRequest
operator|.
name|Replaceable
block|{
DECL|field|routingTable
specifier|private
name|boolean
name|routingTable
init|=
literal|true
decl_stmt|;
DECL|field|nodes
specifier|private
name|boolean
name|nodes
init|=
literal|true
decl_stmt|;
DECL|field|metaData
specifier|private
name|boolean
name|metaData
init|=
literal|true
decl_stmt|;
DECL|field|blocks
specifier|private
name|boolean
name|blocks
init|=
literal|true
decl_stmt|;
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
DECL|method|ClusterStateRequest
specifier|public
name|ClusterStateRequest
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|all
specifier|public
name|ClusterStateRequest
name|all
parameter_list|()
block|{
name|routingTable
operator|=
literal|true
expr_stmt|;
name|nodes
operator|=
literal|true
expr_stmt|;
name|metaData
operator|=
literal|true
expr_stmt|;
name|blocks
operator|=
literal|true
expr_stmt|;
name|indices
operator|=
name|Strings
operator|.
name|EMPTY_ARRAY
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|clear
specifier|public
name|ClusterStateRequest
name|clear
parameter_list|()
block|{
name|routingTable
operator|=
literal|false
expr_stmt|;
name|nodes
operator|=
literal|false
expr_stmt|;
name|metaData
operator|=
literal|false
expr_stmt|;
name|blocks
operator|=
literal|false
expr_stmt|;
name|indices
operator|=
name|Strings
operator|.
name|EMPTY_ARRAY
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|routingTable
specifier|public
name|boolean
name|routingTable
parameter_list|()
block|{
return|return
name|routingTable
return|;
block|}
DECL|method|routingTable
specifier|public
name|ClusterStateRequest
name|routingTable
parameter_list|(
name|boolean
name|routingTable
parameter_list|)
block|{
name|this
operator|.
name|routingTable
operator|=
name|routingTable
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|nodes
specifier|public
name|boolean
name|nodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
DECL|method|nodes
specifier|public
name|ClusterStateRequest
name|nodes
parameter_list|(
name|boolean
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|metaData
specifier|public
name|boolean
name|metaData
parameter_list|()
block|{
return|return
name|metaData
return|;
block|}
DECL|method|metaData
specifier|public
name|ClusterStateRequest
name|metaData
parameter_list|(
name|boolean
name|metaData
parameter_list|)
block|{
name|this
operator|.
name|metaData
operator|=
name|metaData
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|blocks
specifier|public
name|boolean
name|blocks
parameter_list|()
block|{
return|return
name|blocks
return|;
block|}
DECL|method|blocks
specifier|public
name|ClusterStateRequest
name|blocks
parameter_list|(
name|boolean
name|blocks
parameter_list|)
block|{
name|this
operator|.
name|blocks
operator|=
name|blocks
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|indices
specifier|public
name|ClusterStateRequest
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
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|IndicesOptions
operator|.
name|lenientExpandOpen
argument_list|()
return|;
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
name|routingTable
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|nodes
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|metaData
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|blocks
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|indices
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
comment|// fake support for indices in pre 1.2.0 versions
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_2_0
argument_list|)
condition|)
block|{
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
block|}
name|readLocal
argument_list|(
name|in
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
name|writeBoolean
argument_list|(
name|routingTable
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|metaData
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|blocks
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|indices
argument_list|)
expr_stmt|;
comment|// fake support for indices in pre 1.2.0 versions
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_1_2_0
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeStringArray
argument_list|(
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|)
expr_stmt|;
block|}
name|writeLocal
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

