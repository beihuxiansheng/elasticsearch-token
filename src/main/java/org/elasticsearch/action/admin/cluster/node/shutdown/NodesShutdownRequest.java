begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.shutdown
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
name|shutdown
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|readTimeValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NodesShutdownRequest
specifier|public
class|class
name|NodesShutdownRequest
extends|extends
name|MasterNodeOperationRequest
argument_list|<
name|NodesShutdownRequest
argument_list|>
block|{
DECL|field|nodesIds
name|String
index|[]
name|nodesIds
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
DECL|field|delay
name|TimeValue
name|delay
init|=
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|exit
name|boolean
name|exit
init|=
literal|true
decl_stmt|;
DECL|method|NodesShutdownRequest
name|NodesShutdownRequest
parameter_list|()
block|{     }
DECL|method|NodesShutdownRequest
specifier|public
name|NodesShutdownRequest
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
DECL|method|nodesIds
specifier|public
name|NodesShutdownRequest
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
comment|/**      * The delay for the shutdown to occur. Defaults to<tt>1s</tt>.      */
DECL|method|delay
specifier|public
name|NodesShutdownRequest
name|delay
parameter_list|(
name|TimeValue
name|delay
parameter_list|)
block|{
name|this
operator|.
name|delay
operator|=
name|delay
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|delay
specifier|public
name|TimeValue
name|delay
parameter_list|()
block|{
return|return
name|this
operator|.
name|delay
return|;
block|}
comment|/**      * The delay for the shutdown to occur. Defaults to<tt>1s</tt>.      */
DECL|method|delay
specifier|public
name|NodesShutdownRequest
name|delay
parameter_list|(
name|String
name|delay
parameter_list|)
block|{
return|return
name|delay
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|delay
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Should the JVM be exited as well or not. Defaults to<tt>true</tt>.      */
DECL|method|exit
specifier|public
name|NodesShutdownRequest
name|exit
parameter_list|(
name|boolean
name|exit
parameter_list|)
block|{
name|this
operator|.
name|exit
operator|=
name|exit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Should the JVM be exited as well or not. Defaults to<tt>true</tt>.      */
DECL|method|exit
specifier|public
name|boolean
name|exit
parameter_list|()
block|{
return|return
name|exit
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
return|return
literal|null
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
name|delay
operator|=
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|nodesIds
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|exit
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
name|delay
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArrayNullable
argument_list|(
name|nodesIds
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|exit
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

