begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.nodes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|nodes
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
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|NodesOperationRequest
specifier|public
specifier|abstract
class|class
name|NodesOperationRequest
implements|implements
name|ActionRequest
block|{
DECL|field|ALL_NODES
specifier|public
specifier|static
name|String
index|[]
name|ALL_NODES
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|nodesIds
specifier|private
name|String
index|[]
name|nodesIds
decl_stmt|;
DECL|field|listenerThreaded
specifier|private
name|boolean
name|listenerThreaded
init|=
literal|false
decl_stmt|;
DECL|method|NodesOperationRequest
specifier|protected
name|NodesOperationRequest
parameter_list|()
block|{      }
DECL|method|NodesOperationRequest
specifier|protected
name|NodesOperationRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
name|this
operator|.
name|nodesIds
operator|=
name|nodesIds
expr_stmt|;
block|}
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|NodesOperationRequest
name|listenerThreaded
parameter_list|(
name|boolean
name|listenerThreaded
parameter_list|)
block|{
name|this
operator|.
name|listenerThreaded
operator|=
name|listenerThreaded
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|boolean
name|listenerThreaded
parameter_list|()
block|{
return|return
name|this
operator|.
name|listenerThreaded
return|;
block|}
DECL|method|nodesIds
specifier|public
name|String
index|[]
name|nodesIds
parameter_list|()
block|{
return|return
name|nodesIds
return|;
block|}
DECL|method|nodesIds
specifier|public
name|NodesOperationRequest
name|nodesIds
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
name|this
operator|.
name|nodesIds
operator|=
name|nodesIds
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|validate
annotation|@
name|Override
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|nodesIds
operator|=
operator|new
name|String
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodesIds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodesIds
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
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
if|if
condition|(
name|nodesIds
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|nodesIds
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|nodeId
range|:
name|nodesIds
control|)
block|{
name|out
operator|.
name|writeUTF
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

