begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
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
name|state
operator|.
name|ClusterStateRequest
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
name|state
operator|.
name|ClusterStateResponse
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
name|table
operator|.
name|Row
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
name|table
operator|.
name|Table
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
name|transport
operator|.
name|InetSocketTransportAddress
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
name|rest
operator|.
name|RestRequest
operator|.
name|Method
operator|.
name|GET
import|;
end_import

begin_class
DECL|class|RestMasterAction
specifier|public
class|class
name|RestMasterAction
extends|extends
name|BaseRestHandler
block|{
annotation|@
name|Inject
DECL|method|RestMasterAction
specifier|public
name|RestMasterAction
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
name|GET
argument_list|,
literal|"/_cat/master"
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
specifier|final
name|boolean
name|verbose
init|=
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"verbose"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ClusterStateRequest
name|clusterStateRequest
init|=
operator|new
name|ClusterStateRequest
argument_list|()
decl_stmt|;
name|clusterStateRequest
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|filterMetaData
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|local
argument_list|(
literal|false
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
name|state
argument_list|(
name|clusterStateRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
specifier|final
name|ClusterStateResponse
name|clusterStateResponse
parameter_list|)
block|{
try|try
block|{
name|RestStatus
name|status
init|=
name|RestStatus
operator|.
name|OK
decl_stmt|;
name|Table
name|tab
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|tab
operator|.
name|addRow
argument_list|(
operator|new
name|Row
argument_list|()
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"transport addr"
argument_list|)
operator|.
name|addCell
argument_list|(
literal|"name"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|tab
operator|.
name|addRow
argument_list|(
operator|new
name|Row
argument_list|()
operator|.
name|addCell
argument_list|(
name|clusterStateResponse
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|addCell
argument_list|(
operator|(
operator|(
name|InetSocketTransportAddress
operator|)
name|clusterStateResponse
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|address
argument_list|()
operator|)
operator|.
name|address
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|getHostAddress
argument_list|()
argument_list|)
operator|.
name|addCell
argument_list|(
name|clusterStateResponse
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|StringRestResponse
argument_list|(
name|status
argument_list|,
name|tab
operator|.
name|render
argument_list|(
name|verbose
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
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

