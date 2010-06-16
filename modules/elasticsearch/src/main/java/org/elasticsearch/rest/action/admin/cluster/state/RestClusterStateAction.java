begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.admin.cluster.state
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
name|state
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
name|cluster
operator|.
name|ClusterState
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
name|metadata
operator|.
name|IndexMetaData
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
name|routing
operator|.
name|IndexRoutingTable
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
name|routing
operator|.
name|IndexShardRoutingTable
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
name|routing
operator|.
name|RoutingNode
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
name|routing
operator|.
name|ShardRouting
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
name|settings
operator|.
name|SettingsFilter
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
name|xcontent
operator|.
name|XContentFactory
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
name|xcontent
operator|.
name|XContentParser
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
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
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
name|RestXContentBuilder
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
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|RestClusterStateAction
specifier|public
class|class
name|RestClusterStateAction
extends|extends
name|BaseRestHandler
block|{
DECL|field|settingsFilter
specifier|private
specifier|final
name|SettingsFilter
name|settingsFilter
decl_stmt|;
DECL|method|RestClusterStateAction
annotation|@
name|Inject
specifier|public
name|RestClusterStateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Client
name|client
parameter_list|,
name|RestController
name|controller
parameter_list|,
name|SettingsFilter
name|settingsFilter
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
literal|"/_cluster/state"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|settingsFilter
operator|=
name|settingsFilter
expr_stmt|;
block|}
DECL|method|handleRequest
annotation|@
name|Override
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
operator|new
name|ClusterStateRequest
argument_list|()
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
name|ClusterStateResponse
name|response
parameter_list|)
block|{
try|try
block|{
name|ClusterState
name|state
init|=
name|response
operator|.
name|state
argument_list|()
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|RestXContentBuilder
operator|.
name|restContentBuilder
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"cluster_name"
argument_list|,
name|response
operator|.
name|clusterName
argument_list|()
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"master_node"
argument_list|,
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// nodes
name|builder
operator|.
name|startObject
argument_list|(
literal|"nodes"
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|state
operator|.
name|nodes
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|node
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"transport_address"
argument_list|,
name|node
operator|.
name|address
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
comment|// meta data
name|builder
operator|.
name|startObject
argument_list|(
literal|"metadata"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"max_number_of_shards_per_node"
argument_list|,
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|maxNumberOfShardsPerNode
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"indices"
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexMetaData
name|indexMetaData
range|:
name|state
operator|.
name|metaData
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"settings"
argument_list|)
expr_stmt|;
name|Settings
name|settings
init|=
name|settingsFilter
operator|.
name|filterSettings
argument_list|(
name|indexMetaData
operator|.
name|settings
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
name|entry
range|:
name|settings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"mappings"
argument_list|)
expr_stmt|;
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
name|entry
range|:
name|indexMetaData
operator|.
name|mappings
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|createParser
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|mapping
init|=
name|parser
operator|.
name|map
argument_list|()
decl_stmt|;
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
comment|// routing table
name|builder
operator|.
name|startObject
argument_list|(
literal|"routing_table"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"indices"
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|state
operator|.
name|routingTable
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexRoutingTable
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"shards"
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexShardRoutingTable
name|indexShardRoutingTable
range|:
name|indexRoutingTable
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|indexShardRoutingTable
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|indexShardRoutingTable
control|)
block|{
name|jsonShardRouting
argument_list|(
name|builder
argument_list|,
name|shardRouting
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
comment|// routing nodes
name|builder
operator|.
name|startObject
argument_list|(
literal|"routing_nodes"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"unassigned"
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|state
operator|.
name|readOnlyRoutingNodes
argument_list|()
operator|.
name|unassigned
argument_list|()
control|)
block|{
name|jsonShardRouting
argument_list|(
name|builder
argument_list|,
name|shardRouting
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"nodes"
argument_list|)
expr_stmt|;
for|for
control|(
name|RoutingNode
name|routingNode
range|:
name|state
operator|.
name|readOnlyRoutingNodes
argument_list|()
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|routingNode
operator|.
name|nodeId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|routingNode
control|)
block|{
name|jsonShardRouting
argument_list|(
name|builder
argument_list|,
name|shardRouting
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|XContentRestResponse
argument_list|(
name|request
argument_list|,
name|RestResponse
operator|.
name|Status
operator|.
name|OK
argument_list|,
name|builder
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
specifier|private
name|void
name|jsonShardRouting
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|ShardRouting
name|shardRouting
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"state"
argument_list|,
name|shardRouting
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"primary"
argument_list|,
name|shardRouting
operator|.
name|primary
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"node"
argument_list|,
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"relocating_node"
argument_list|,
name|shardRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"shard"
argument_list|,
name|shardRouting
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|shardRouting
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
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

