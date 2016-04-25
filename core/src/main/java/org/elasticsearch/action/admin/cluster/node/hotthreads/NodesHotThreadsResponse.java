begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.hotthreads
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
name|hotthreads
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
name|FailedNodeException
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
name|nodes
operator|.
name|BaseNodesResponse
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
name|List
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|NodesHotThreadsResponse
specifier|public
class|class
name|NodesHotThreadsResponse
extends|extends
name|BaseNodesResponse
argument_list|<
name|NodeHotThreads
argument_list|>
block|{
DECL|method|NodesHotThreadsResponse
name|NodesHotThreadsResponse
parameter_list|()
block|{     }
DECL|method|NodesHotThreadsResponse
specifier|public
name|NodesHotThreadsResponse
parameter_list|(
name|ClusterName
name|clusterName
parameter_list|,
name|List
argument_list|<
name|NodeHotThreads
argument_list|>
name|nodes
parameter_list|,
name|List
argument_list|<
name|FailedNodeException
argument_list|>
name|failures
parameter_list|)
block|{
name|super
argument_list|(
name|clusterName
argument_list|,
name|nodes
argument_list|,
name|failures
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readNodesFrom
specifier|protected
name|List
argument_list|<
name|NodeHotThreads
argument_list|>
name|readNodesFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readList
argument_list|(
name|NodeHotThreads
operator|::
name|readNodeHotThreads
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|writeNodesTo
specifier|protected
name|void
name|writeNodesTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|,
name|List
argument_list|<
name|NodeHotThreads
argument_list|>
name|nodes
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeStreamableList
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

