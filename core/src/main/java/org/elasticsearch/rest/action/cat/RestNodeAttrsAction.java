begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodeInfo
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
name|info
operator|.
name|NodesInfoRequest
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
name|info
operator|.
name|NodesInfoResponse
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
name|node
operator|.
name|NodeClient
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
name|RestChannel
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
name|RestController
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
name|RestRequest
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
name|RestResponse
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
name|RestActionListener
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
name|RestResponseListener
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
name|RestTable
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
DECL|class|RestNodeAttrsAction
specifier|public
class|class
name|RestNodeAttrsAction
extends|extends
name|AbstractCatAction
block|{
annotation|@
name|Inject
DECL|method|RestNodeAttrsAction
specifier|public
name|RestNodeAttrsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|RestController
name|controller
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|controller
operator|.
name|registerHandler
argument_list|(
name|GET
argument_list|,
literal|"/_cat/nodeattrs"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|documentation
specifier|protected
name|void
name|documentation
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"/_cat/nodeattrs\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doRequest
specifier|public
name|void
name|doRequest
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|NodeClient
name|client
parameter_list|)
block|{
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
name|clear
argument_list|()
operator|.
name|nodes
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|local
argument_list|(
name|request
operator|.
name|paramAsBoolean
argument_list|(
literal|"local"
argument_list|,
name|clusterStateRequest
operator|.
name|local
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|clusterStateRequest
operator|.
name|masterNodeTimeout
argument_list|(
name|request
operator|.
name|paramAsTime
argument_list|(
literal|"master_timeout"
argument_list|,
name|clusterStateRequest
operator|.
name|masterNodeTimeout
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
name|state
argument_list|(
name|clusterStateRequest
argument_list|,
operator|new
name|RestActionListener
argument_list|<
name|ClusterStateResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|processResponse
parameter_list|(
specifier|final
name|ClusterStateResponse
name|clusterStateResponse
parameter_list|)
block|{
name|NodesInfoRequest
name|nodesInfoRequest
init|=
operator|new
name|NodesInfoRequest
argument_list|()
decl_stmt|;
name|nodesInfoRequest
operator|.
name|clear
argument_list|()
operator|.
name|jvm
argument_list|(
literal|false
argument_list|)
operator|.
name|os
argument_list|(
literal|false
argument_list|)
operator|.
name|process
argument_list|(
literal|true
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
name|nodesInfo
argument_list|(
name|nodesInfoRequest
argument_list|,
operator|new
name|RestResponseListener
argument_list|<
name|NodesInfoResponse
argument_list|>
argument_list|(
name|channel
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RestResponse
name|buildResponse
parameter_list|(
name|NodesInfoResponse
name|nodesInfoResponse
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|RestTable
operator|.
name|buildResponse
argument_list|(
name|buildTable
argument_list|(
name|request
argument_list|,
name|clusterStateResponse
argument_list|,
name|nodesInfoResponse
argument_list|)
argument_list|,
name|channel
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTableWithHeader
specifier|protected
name|Table
name|getTableWithHeader
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|)
block|{
name|Table
name|table
init|=
operator|new
name|Table
argument_list|()
decl_stmt|;
name|table
operator|.
name|startHeaders
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"node"
argument_list|,
literal|"default:true;alias:name;desc:node name"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"id"
argument_list|,
literal|"default:false;alias:id,nodeId;desc:unique node id"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"pid"
argument_list|,
literal|"default:false;alias:p;desc:process id"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"host"
argument_list|,
literal|"alias:h;desc:host name"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"ip"
argument_list|,
literal|"alias:i;desc:ip address"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"port"
argument_list|,
literal|"default:false;alias:po;desc:bound transport port"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"attr"
argument_list|,
literal|"default:true;alias:attr.name;desc:attribute description"
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
literal|"value"
argument_list|,
literal|"default:true;alias:attr.value;desc:attribute value"
argument_list|)
expr_stmt|;
name|table
operator|.
name|endHeaders
argument_list|()
expr_stmt|;
return|return
name|table
return|;
block|}
DECL|method|buildTable
specifier|private
name|Table
name|buildTable
parameter_list|(
name|RestRequest
name|req
parameter_list|,
name|ClusterStateResponse
name|state
parameter_list|,
name|NodesInfoResponse
name|nodesInfo
parameter_list|)
block|{
name|boolean
name|fullId
init|=
name|req
operator|.
name|paramAsBoolean
argument_list|(
literal|"full_id"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|DiscoveryNodes
name|nodes
init|=
name|state
operator|.
name|getState
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|Table
name|table
init|=
name|getTableWithHeader
argument_list|(
name|req
argument_list|)
decl_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
name|NodeInfo
name|info
init|=
name|nodesInfo
operator|.
name|getNodesMap
argument_list|()
operator|.
name|get
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attrEntry
range|:
name|node
operator|.
name|getAttributes
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|table
operator|.
name|startRow
argument_list|()
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|node
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|fullId
condition|?
name|node
operator|.
name|getId
argument_list|()
else|:
name|Strings
operator|.
name|substring
argument_list|(
name|node
operator|.
name|getId
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|info
operator|==
literal|null
condition|?
literal|null
else|:
name|info
operator|.
name|getProcess
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|node
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|node
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getAddress
argument_list|()
operator|instanceof
name|InetSocketTransportAddress
condition|)
block|{
name|table
operator|.
name|addCell
argument_list|(
operator|(
operator|(
name|InetSocketTransportAddress
operator|)
name|node
operator|.
name|getAddress
argument_list|()
operator|)
operator|.
name|address
argument_list|()
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|table
operator|.
name|addCell
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
block|}
name|table
operator|.
name|addCell
argument_list|(
name|attrEntry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|addCell
argument_list|(
name|attrEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|table
operator|.
name|endRow
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|table
return|;
block|}
block|}
end_class

end_unit

