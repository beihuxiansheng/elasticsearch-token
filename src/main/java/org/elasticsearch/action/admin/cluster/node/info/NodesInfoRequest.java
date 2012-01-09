begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.info
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
name|node
operator|.
name|info
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
name|nodes
operator|.
name|NodesOperationRequest
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
comment|/**  * A request to get node (cluster) level information.  */
end_comment

begin_class
DECL|class|NodesInfoRequest
specifier|public
class|class
name|NodesInfoRequest
extends|extends
name|NodesOperationRequest
block|{
DECL|field|settings
specifier|private
name|boolean
name|settings
init|=
literal|false
decl_stmt|;
DECL|field|os
specifier|private
name|boolean
name|os
init|=
literal|false
decl_stmt|;
DECL|field|process
specifier|private
name|boolean
name|process
init|=
literal|false
decl_stmt|;
DECL|field|jvm
specifier|private
name|boolean
name|jvm
init|=
literal|false
decl_stmt|;
DECL|field|network
specifier|private
name|boolean
name|network
init|=
literal|false
decl_stmt|;
DECL|field|transport
specifier|private
name|boolean
name|transport
init|=
literal|false
decl_stmt|;
DECL|field|http
specifier|private
name|boolean
name|http
init|=
literal|false
decl_stmt|;
DECL|method|NodesInfoRequest
specifier|public
name|NodesInfoRequest
parameter_list|()
block|{     }
comment|/**      * Get information from nodes based on the nodes ids specified. If none are passed, information      * for all nodes will be returned.      */
DECL|method|NodesInfoRequest
specifier|public
name|NodesInfoRequest
parameter_list|(
name|String
modifier|...
name|nodesIds
parameter_list|)
block|{
name|super
argument_list|(
name|nodesIds
argument_list|)
expr_stmt|;
block|}
comment|/**      * Clears all info flags.      */
DECL|method|clear
specifier|public
name|NodesInfoRequest
name|clear
parameter_list|()
block|{
name|settings
operator|=
literal|false
expr_stmt|;
name|os
operator|=
literal|false
expr_stmt|;
name|process
operator|=
literal|false
expr_stmt|;
name|jvm
operator|=
literal|false
expr_stmt|;
name|network
operator|=
literal|false
expr_stmt|;
name|transport
operator|=
literal|false
expr_stmt|;
name|http
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node settings be returned.      */
DECL|method|settings
specifier|public
name|boolean
name|settings
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
return|;
block|}
comment|/**      * Should the node settings be returned.      */
DECL|method|settings
specifier|public
name|NodesInfoRequest
name|settings
parameter_list|(
name|boolean
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node OS be returned.      */
DECL|method|os
specifier|public
name|boolean
name|os
parameter_list|()
block|{
return|return
name|this
operator|.
name|os
return|;
block|}
comment|/**      * Should the node OS be returned.      */
DECL|method|os
specifier|public
name|NodesInfoRequest
name|os
parameter_list|(
name|boolean
name|os
parameter_list|)
block|{
name|this
operator|.
name|os
operator|=
name|os
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node Process be returned.      */
DECL|method|process
specifier|public
name|boolean
name|process
parameter_list|()
block|{
return|return
name|this
operator|.
name|process
return|;
block|}
comment|/**      * Should the node Process be returned.      */
DECL|method|process
specifier|public
name|NodesInfoRequest
name|process
parameter_list|(
name|boolean
name|process
parameter_list|)
block|{
name|this
operator|.
name|process
operator|=
name|process
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node JVM be returned.      */
DECL|method|jvm
specifier|public
name|boolean
name|jvm
parameter_list|()
block|{
return|return
name|this
operator|.
name|jvm
return|;
block|}
comment|/**      * Should the node JVM be returned.      */
DECL|method|jvm
specifier|public
name|NodesInfoRequest
name|jvm
parameter_list|(
name|boolean
name|jvm
parameter_list|)
block|{
name|this
operator|.
name|jvm
operator|=
name|jvm
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node Network be returned.      */
DECL|method|network
specifier|public
name|boolean
name|network
parameter_list|()
block|{
return|return
name|this
operator|.
name|network
return|;
block|}
comment|/**      * Should the node Network be returned.      */
DECL|method|network
specifier|public
name|NodesInfoRequest
name|network
parameter_list|(
name|boolean
name|network
parameter_list|)
block|{
name|this
operator|.
name|network
operator|=
name|network
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node Transport be returned.      */
DECL|method|transport
specifier|public
name|boolean
name|transport
parameter_list|()
block|{
return|return
name|this
operator|.
name|transport
return|;
block|}
comment|/**      * Should the node Transport be returned.      */
DECL|method|transport
specifier|public
name|NodesInfoRequest
name|transport
parameter_list|(
name|boolean
name|transport
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the node HTTP be returned.      */
DECL|method|http
specifier|public
name|boolean
name|http
parameter_list|()
block|{
return|return
name|this
operator|.
name|http
return|;
block|}
comment|/**      * Should the node HTTP be returned.      */
DECL|method|http
specifier|public
name|NodesInfoRequest
name|http
parameter_list|(
name|boolean
name|http
parameter_list|)
block|{
name|this
operator|.
name|http
operator|=
name|http
expr_stmt|;
return|return
name|this
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
name|settings
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|os
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|process
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|jvm
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|network
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|transport
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|http
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
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
name|settings
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|process
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|jvm
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|network
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|transport
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|http
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

