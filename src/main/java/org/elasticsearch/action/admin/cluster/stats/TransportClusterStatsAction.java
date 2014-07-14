begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.stats
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
name|stats
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|health
operator|.
name|ClusterHealthStatus
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
name|health
operator|.
name|ClusterIndexHealth
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
name|stats
operator|.
name|NodeStats
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
name|indices
operator|.
name|stats
operator|.
name|CommonStatsFlags
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
name|indices
operator|.
name|stats
operator|.
name|ShardStats
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
name|NodeOperationRequest
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
name|TransportNodesOperationAction
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
name|cluster
operator|.
name|ClusterService
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
name|index
operator|.
name|service
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|service
operator|.
name|IndexShard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|service
operator|.
name|NodeService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReferenceArray
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportClusterStatsAction
specifier|public
class|class
name|TransportClusterStatsAction
extends|extends
name|TransportNodesOperationAction
argument_list|<
name|ClusterStatsRequest
argument_list|,
name|ClusterStatsResponse
argument_list|,
name|TransportClusterStatsAction
operator|.
name|ClusterStatsNodeRequest
argument_list|,
name|ClusterStatsNodeResponse
argument_list|>
block|{
DECL|field|SHARD_STATS_FLAGS
specifier|private
specifier|static
specifier|final
name|CommonStatsFlags
name|SHARD_STATS_FLAGS
init|=
operator|new
name|CommonStatsFlags
argument_list|(
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|Docs
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|Store
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|FieldData
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|FilterCache
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|IdCache
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|Completion
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|Segments
argument_list|,
name|CommonStatsFlags
operator|.
name|Flag
operator|.
name|Percolate
argument_list|)
decl_stmt|;
DECL|field|nodeService
specifier|private
specifier|final
name|NodeService
name|nodeService
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClusterStatsAction
specifier|public
name|TransportClusterStatsAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|NodeService
name|nodeService
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ClusterStatsAction
operator|.
name|NAME
argument_list|,
name|clusterName
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeService
operator|=
name|nodeService
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClusterStatsResponse
name|newResponse
parameter_list|(
name|ClusterStatsRequest
name|clusterStatsRequest
parameter_list|,
name|AtomicReferenceArray
name|responses
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|ClusterStatsNodeResponse
argument_list|>
name|nodeStats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|responses
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|responses
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|resp
init|=
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|resp
operator|instanceof
name|ClusterStatsNodeResponse
condition|)
block|{
name|nodeStats
operator|.
name|add
argument_list|(
operator|(
name|ClusterStatsNodeResponse
operator|)
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ClusterStatsResponse
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|clusterName
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|uuid
argument_list|()
argument_list|,
name|nodeStats
operator|.
name|toArray
argument_list|(
operator|new
name|ClusterStatsNodeResponse
index|[
name|nodeStats
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|ClusterStatsRequest
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterStatsRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeRequest
specifier|protected
name|ClusterStatsNodeRequest
name|newNodeRequest
parameter_list|()
block|{
return|return
operator|new
name|ClusterStatsNodeRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeRequest
specifier|protected
name|ClusterStatsNodeRequest
name|newNodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|ClusterStatsRequest
name|request
parameter_list|)
block|{
return|return
operator|new
name|ClusterStatsNodeRequest
argument_list|(
name|nodeId
argument_list|,
name|request
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeResponse
specifier|protected
name|ClusterStatsNodeResponse
name|newNodeResponse
parameter_list|()
block|{
return|return
operator|new
name|ClusterStatsNodeResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodeOperation
specifier|protected
name|ClusterStatsNodeResponse
name|nodeOperation
parameter_list|(
name|ClusterStatsNodeRequest
name|nodeRequest
parameter_list|)
throws|throws
name|ElasticsearchException
block|{
name|NodeInfo
name|nodeInfo
init|=
name|nodeService
operator|.
name|info
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|NodeStats
name|nodeStats
init|=
name|nodeService
operator|.
name|stats
argument_list|(
name|CommonStatsFlags
operator|.
name|NONE
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardStats
argument_list|>
name|shardsStats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indicesService
operator|.
name|indices
argument_list|()
control|)
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|IndexShard
name|indexShard
range|:
name|indexService
control|)
block|{
if|if
condition|(
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|active
argument_list|()
condition|)
block|{
comment|// only report on fully started shards
name|shardsStats
operator|.
name|add
argument_list|(
operator|new
name|ShardStats
argument_list|(
name|indexShard
argument_list|,
name|SHARD_STATS_FLAGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ClusterHealthStatus
name|clusterStatus
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
comment|// populate cluster status
name|clusterStatus
operator|=
name|ClusterHealthStatus
operator|.
name|GREEN
expr_stmt|;
for|for
control|(
name|IndexRoutingTable
name|indexRoutingTable
range|:
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
control|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|indexRoutingTable
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexRoutingTable
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ClusterIndexHealth
name|indexHealth
init|=
operator|new
name|ClusterIndexHealth
argument_list|(
name|indexMetaData
argument_list|,
name|indexRoutingTable
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|indexHealth
operator|.
name|getStatus
argument_list|()
condition|)
block|{
case|case
name|RED
case|:
name|clusterStatus
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
break|break;
case|case
name|YELLOW
case|:
if|if
condition|(
name|clusterStatus
operator|!=
name|ClusterHealthStatus
operator|.
name|RED
condition|)
block|{
name|clusterStatus
operator|=
name|ClusterHealthStatus
operator|.
name|YELLOW
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
return|return
operator|new
name|ClusterStatsNodeResponse
argument_list|(
name|nodeInfo
operator|.
name|getNode
argument_list|()
argument_list|,
name|clusterStatus
argument_list|,
name|nodeInfo
argument_list|,
name|nodeStats
argument_list|,
name|shardsStats
operator|.
name|toArray
argument_list|(
operator|new
name|ShardStats
index|[
name|shardsStats
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|accumulateExceptions
specifier|protected
name|boolean
name|accumulateExceptions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|class|ClusterStatsNodeRequest
specifier|static
class|class
name|ClusterStatsNodeRequest
extends|extends
name|NodeOperationRequest
block|{
DECL|field|request
name|ClusterStatsRequest
name|request
decl_stmt|;
DECL|method|ClusterStatsNodeRequest
name|ClusterStatsNodeRequest
parameter_list|()
block|{         }
DECL|method|ClusterStatsNodeRequest
name|ClusterStatsNodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|ClusterStatsRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
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
name|request
operator|=
operator|new
name|ClusterStatsRequest
argument_list|()
expr_stmt|;
name|request
operator|.
name|readFrom
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
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

