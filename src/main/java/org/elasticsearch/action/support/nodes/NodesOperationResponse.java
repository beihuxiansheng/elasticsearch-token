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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|ActionResponse
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
name|FailedNodeException
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
name|ClusterName
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
name|Nullable
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
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NodesOperationResponse
specifier|public
specifier|abstract
class|class
name|NodesOperationResponse
parameter_list|<
name|NodeResponse
extends|extends
name|NodeOperationResponse
parameter_list|>
extends|extends
name|ActionResponse
implements|implements
name|Iterable
argument_list|<
name|NodeResponse
argument_list|>
block|{
DECL|field|clusterName
specifier|private
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|nodes
specifier|protected
name|NodeResponse
index|[]
name|nodes
decl_stmt|;
DECL|field|nodesMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|NodeResponse
argument_list|>
name|nodesMap
decl_stmt|;
DECL|method|NodesOperationResponse
specifier|protected
name|NodesOperationResponse
parameter_list|()
block|{     }
DECL|method|NodesOperationResponse
specifier|protected
name|NodesOperationResponse
parameter_list|(
name|ClusterName
name|clusterName
parameter_list|,
name|NodeResponse
index|[]
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
comment|/**      * The failed nodes, if set to be captured.      */
annotation|@
name|Nullable
DECL|method|failures
specifier|public
name|FailedNodeException
index|[]
name|failures
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|getClusterName
specifier|public
name|ClusterName
name|getClusterName
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterName
return|;
block|}
DECL|method|getClusterNameAsString
specifier|public
name|String
name|getClusterNameAsString
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterName
operator|.
name|value
argument_list|()
return|;
block|}
DECL|method|getNodes
specifier|public
name|NodeResponse
index|[]
name|getNodes
parameter_list|()
block|{
return|return
name|nodes
return|;
block|}
DECL|method|getAt
specifier|public
name|NodeResponse
name|getAt
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|nodes
index|[
name|position
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|NodeResponse
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|getNodesMap
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|getNodesMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|NodeResponse
argument_list|>
name|getNodesMap
parameter_list|()
block|{
if|if
condition|(
name|nodesMap
operator|==
literal|null
condition|)
block|{
name|nodesMap
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeResponse
name|nodeResponse
range|:
name|nodes
control|)
block|{
name|nodesMap
operator|.
name|put
argument_list|(
name|nodeResponse
operator|.
name|getNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|nodeResponse
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|nodesMap
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
name|clusterName
operator|=
name|ClusterName
operator|.
name|readClusterName
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
name|clusterName
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

