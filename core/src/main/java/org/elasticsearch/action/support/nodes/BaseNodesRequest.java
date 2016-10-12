begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNodes
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

begin_class
DECL|class|BaseNodesRequest
specifier|public
specifier|abstract
class|class
name|BaseNodesRequest
parameter_list|<
name|Request
extends|extends
name|BaseNodesRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|>
extends|extends
name|ActionRequest
argument_list|<
name|Request
argument_list|>
block|{
comment|/**      * the list of nodesIds that will be used to resolve this request and {@link #concreteNodes}      * will be populated. Note that if {@link #concreteNodes} is not null, it will be used and nodeIds      * will be ignored.      *      * See {@link DiscoveryNodes#resolveNodes} for a full description of the options.      *      * TODO: once we stop using the transport client as a gateway to the cluster, we can get rid of this and resolve it to concrete nodes      * in the rest layer      **/
DECL|field|nodesIds
specifier|private
name|String
index|[]
name|nodesIds
decl_stmt|;
comment|/**      * once {@link #nodesIds} are resolved this will contain the concrete nodes that are part of this request. If set, {@link #nodesIds}      * will be ignored and this will be used.      * */
DECL|field|concreteNodes
specifier|private
name|DiscoveryNode
index|[]
name|concreteNodes
decl_stmt|;
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
decl_stmt|;
DECL|method|BaseNodesRequest
specifier|protected
name|BaseNodesRequest
parameter_list|()
block|{      }
DECL|method|BaseNodesRequest
specifier|protected
name|BaseNodesRequest
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
DECL|method|BaseNodesRequest
specifier|protected
name|BaseNodesRequest
parameter_list|(
name|DiscoveryNode
modifier|...
name|concreteNodes
parameter_list|)
block|{
name|this
operator|.
name|nodesIds
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|concreteNodes
operator|=
name|concreteNodes
expr_stmt|;
block|}
DECL|method|nodesIds
specifier|public
specifier|final
name|String
index|[]
name|nodesIds
parameter_list|()
block|{
return|return
name|nodesIds
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|nodesIds
specifier|public
specifier|final
name|Request
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
operator|(
name|Request
operator|)
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|timeout
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|timeout
specifier|public
specifier|final
name|Request
name|timeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
operator|(
name|Request
operator|)
name|this
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|timeout
specifier|public
specifier|final
name|Request
name|timeout
parameter_list|(
name|String
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|timeout
argument_list|,
literal|null
argument_list|,
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".timeout"
argument_list|)
expr_stmt|;
return|return
operator|(
name|Request
operator|)
name|this
return|;
block|}
DECL|method|concreteNodes
specifier|public
name|DiscoveryNode
index|[]
name|concreteNodes
parameter_list|()
block|{
return|return
name|concreteNodes
return|;
block|}
DECL|method|setConcreteNodes
specifier|public
name|void
name|setConcreteNodes
parameter_list|(
name|DiscoveryNode
index|[]
name|concreteNodes
parameter_list|)
block|{
name|this
operator|.
name|concreteNodes
operator|=
name|concreteNodes
expr_stmt|;
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
name|nodesIds
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|concreteNodes
operator|=
name|in
operator|.
name|readOptionalArray
argument_list|(
name|DiscoveryNode
operator|::
operator|new
argument_list|,
name|DiscoveryNode
index|[]
operator|::
operator|new
argument_list|)
expr_stmt|;
name|timeout
operator|=
name|in
operator|.
name|readOptionalWriteable
argument_list|(
name|TimeValue
operator|::
operator|new
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
name|writeStringArrayNullable
argument_list|(
name|nodesIds
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalArray
argument_list|(
name|concreteNodes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalWriteable
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

