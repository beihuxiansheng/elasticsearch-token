begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.cluster.node.hotthreads
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
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
name|ActionListener
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|hotthreads
operator|.
name|NodeHotThreads
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|hotthreads
operator|.
name|NodesHotThreadsRequest
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|hotthreads
operator|.
name|NodesHotThreadsResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
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
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|support
operator|.
name|RestActions
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
comment|/**  */
end_comment

begin_class
DECL|class|RestNodesHotThreadsAction
specifier|public
class|class
name|RestNodesHotThreadsAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestNodesHotThreadsAction
specifier|public
name|RestNodesHotThreadsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|client
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_cluster/nodes/hotthreads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_cluster/nodes/hot_threads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_cluster/nodes/{nodeId}/hotthreads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_cluster/nodes/{nodeId}/hot_threads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_nodes/hotthreads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_nodes/hot_threads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_nodes/{nodeId}/hotthreads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/_nodes/{nodeId}/hot_threads"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleRequest
specifier|public
name|void
name|handleRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|)
block|{
name|String
index|[]
name|nodesIds
init|=
name|RestActions
operator|.
name|splitNodes
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"nodeId"
argument_list|)
argument_list|)
decl_stmt|;
name|NodesHotThreadsRequest
name|nodesHotThreadsRequest
init|=
operator|new
name|NodesHotThreadsRequest
argument_list|(
name|nodesIds
argument_list|)
decl_stmt|;
name|nodesHotThreadsRequest
operator|.
name|threads
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"threads"
argument_list|,
name|nodesHotThreadsRequest
operator|.
name|threads
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodesHotThreadsRequest
operator|.
name|type
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"type"
argument_list|,
name|nodesHotThreadsRequest
operator|.
name|type
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodesHotThreadsRequest
operator|.
name|interval
argument_list|(
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
name|request
operator|.
name|param
argument_list|(
literal|"interval"
argument_list|)
argument_list|,
name|nodesHotThreadsRequest
operator|.
name|interval
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodesHotThreadsRequest
operator|.
name|snapshots
argument_list|(
name|request
operator|.
name|paramAsInt
argument_list|(
literal|"snapshots"
argument_list|,
name|nodesHotThreadsRequest
operator|.
name|snapshots
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|nodesHotThreads
argument_list|(
name|nodesHotThreadsRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|NodesHotThreadsResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|NodesHotThreadsResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeHotThreads
name|node
range|:
name|response
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"::: "
argument_list|)
operator|.
name|append
argument_list|(
name|node
operator|.
name|node
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|Strings
operator|.
name|spaceify
argument_list|(
literal|3
argument_list|,
name|node
operator|.
name|hotThreads
argument_list|()
argument_list|,
name|sb
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|RestStatus
operator|.
name|OK
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentThrowableRestResponse
argument_list|(
name|request
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to send failure response"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

