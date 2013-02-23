begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local.state.shards
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
operator|.
name|state
operator|.
name|shards
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
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|ActionFuture
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
name|action
operator|.
name|support
operator|.
name|nodes
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
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
DECL|class|TransportNodesListGatewayStartedShards
specifier|public
class|class
name|TransportNodesListGatewayStartedShards
extends|extends
name|TransportNodesOperationAction
argument_list|<
name|TransportNodesListGatewayStartedShards
operator|.
name|Request
argument_list|,
name|TransportNodesListGatewayStartedShards
operator|.
name|NodesLocalGatewayStartedShards
argument_list|,
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeRequest
argument_list|,
name|TransportNodesListGatewayStartedShards
operator|.
name|NodeLocalGatewayStartedShards
argument_list|>
block|{
DECL|field|shardsState
specifier|private
name|LocalGatewayShardsState
name|shardsState
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportNodesListGatewayStartedShards
specifier|public
name|TransportNodesListGatewayStartedShards
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
parameter_list|)
block|{
name|super
argument_list|(
name|settings
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
block|}
DECL|method|initGateway
name|TransportNodesListGatewayStartedShards
name|initGateway
parameter_list|(
name|LocalGatewayShardsState
name|shardsState
parameter_list|)
block|{
name|this
operator|.
name|shardsState
operator|=
name|shardsState
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|list
specifier|public
name|ActionFuture
argument_list|<
name|NodesLocalGatewayStartedShards
argument_list|>
name|list
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodesIds
parameter_list|,
annotation|@
name|Nullable
name|TimeValue
name|timeout
parameter_list|)
block|{
return|return
name|execute
argument_list|(
operator|new
name|Request
argument_list|(
name|shardId
argument_list|,
name|nodesIds
argument_list|)
operator|.
name|timeout
argument_list|(
name|timeout
argument_list|)
argument_list|)
return|;
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
name|GENERIC
return|;
block|}
annotation|@
name|Override
DECL|method|transportAction
specifier|protected
name|String
name|transportAction
parameter_list|()
block|{
return|return
literal|"/gateway/local/started-shards"
return|;
block|}
annotation|@
name|Override
DECL|method|transportCompress
specifier|protected
name|boolean
name|transportCompress
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// this can become big...
block|}
annotation|@
name|Override
DECL|method|newRequest
specifier|protected
name|Request
name|newRequest
parameter_list|()
block|{
return|return
operator|new
name|Request
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeRequest
specifier|protected
name|NodeRequest
name|newNodeRequest
parameter_list|()
block|{
return|return
operator|new
name|NodeRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newNodeRequest
specifier|protected
name|NodeRequest
name|newNodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
return|return
operator|new
name|NodeRequest
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
name|NodeLocalGatewayStartedShards
name|newNodeResponse
parameter_list|()
block|{
return|return
operator|new
name|NodeLocalGatewayStartedShards
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|NodesLocalGatewayStartedShards
name|newResponse
parameter_list|(
name|Request
name|request
parameter_list|,
name|AtomicReferenceArray
name|responses
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|NodeLocalGatewayStartedShards
argument_list|>
name|nodesList
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FailedNodeException
argument_list|>
name|failures
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|NodeLocalGatewayStartedShards
condition|)
block|{
comment|// will also filter out null response for unallocated ones
name|nodesList
operator|.
name|add
argument_list|(
operator|(
name|NodeLocalGatewayStartedShards
operator|)
name|resp
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|resp
operator|instanceof
name|FailedNodeException
condition|)
block|{
name|failures
operator|.
name|add
argument_list|(
operator|(
name|FailedNodeException
operator|)
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodesLocalGatewayStartedShards
argument_list|(
name|clusterName
argument_list|,
name|nodesList
operator|.
name|toArray
argument_list|(
operator|new
name|NodeLocalGatewayStartedShards
index|[
name|nodesList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|failures
operator|.
name|toArray
argument_list|(
operator|new
name|FailedNodeException
index|[
name|failures
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
DECL|method|nodeOperation
specifier|protected
name|NodeLocalGatewayStartedShards
name|nodeOperation
parameter_list|(
name|NodeRequest
name|request
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
try|try
block|{
name|ShardStateInfo
name|shardStateInfo
init|=
name|shardsState
operator|.
name|loadShardInfo
argument_list|(
name|request
operator|.
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardStateInfo
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|NodeLocalGatewayStartedShards
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|,
name|shardStateInfo
operator|.
name|version
argument_list|)
return|;
block|}
return|return
operator|new
name|NodeLocalGatewayStartedShards
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"failed to load started shards"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
literal|true
return|;
block|}
DECL|class|Request
specifier|static
class|class
name|Request
extends|extends
name|NodesOperationRequest
argument_list|<
name|Request
argument_list|>
block|{
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
DECL|method|Request
specifier|public
name|Request
parameter_list|()
block|{         }
DECL|method|Request
specifier|public
name|Request
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|nodesIds
parameter_list|)
block|{
name|super
argument_list|(
name|nodesIds
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|nodesIds
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
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
name|shardId
operator|=
name|ShardId
operator|.
name|readShardId
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
name|shardId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NodesLocalGatewayStartedShards
specifier|public
specifier|static
class|class
name|NodesLocalGatewayStartedShards
extends|extends
name|NodesOperationResponse
argument_list|<
name|NodeLocalGatewayStartedShards
argument_list|>
block|{
DECL|field|failures
specifier|private
name|FailedNodeException
index|[]
name|failures
decl_stmt|;
DECL|method|NodesLocalGatewayStartedShards
name|NodesLocalGatewayStartedShards
parameter_list|()
block|{         }
DECL|method|NodesLocalGatewayStartedShards
specifier|public
name|NodesLocalGatewayStartedShards
parameter_list|(
name|ClusterName
name|clusterName
parameter_list|,
name|NodeLocalGatewayStartedShards
index|[]
name|nodes
parameter_list|,
name|FailedNodeException
index|[]
name|failures
parameter_list|)
block|{
name|super
argument_list|(
name|clusterName
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|this
operator|.
name|failures
operator|=
name|failures
expr_stmt|;
block|}
DECL|method|failures
specifier|public
name|FailedNodeException
index|[]
name|failures
parameter_list|()
block|{
return|return
name|failures
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
name|nodes
operator|=
operator|new
name|NodeLocalGatewayStartedShards
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
operator|new
name|NodeLocalGatewayStartedShards
argument_list|()
expr_stmt|;
name|nodes
index|[
name|i
index|]
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|writeVInt
argument_list|(
name|nodes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeLocalGatewayStartedShards
name|response
range|:
name|nodes
control|)
block|{
name|response
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|NodeRequest
specifier|static
class|class
name|NodeRequest
extends|extends
name|NodeOperationRequest
block|{
DECL|field|shardId
name|ShardId
name|shardId
decl_stmt|;
DECL|method|NodeRequest
name|NodeRequest
parameter_list|()
block|{         }
DECL|method|NodeRequest
name|NodeRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|TransportNodesListGatewayStartedShards
operator|.
name|Request
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
name|shardId
operator|=
name|request
operator|.
name|shardId
argument_list|()
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
name|shardId
operator|=
name|ShardId
operator|.
name|readShardId
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
name|shardId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|NodeLocalGatewayStartedShards
specifier|public
specifier|static
class|class
name|NodeLocalGatewayStartedShards
extends|extends
name|NodeOperationResponse
block|{
DECL|field|version
specifier|private
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|NodeLocalGatewayStartedShards
name|NodeLocalGatewayStartedShards
parameter_list|()
block|{         }
DECL|method|NodeLocalGatewayStartedShards
specifier|public
name|NodeLocalGatewayStartedShards
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
DECL|method|hasVersion
specifier|public
name|boolean
name|hasVersion
parameter_list|()
block|{
return|return
name|version
operator|!=
operator|-
literal|1
return|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
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
name|version
operator|=
name|in
operator|.
name|readLong
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
name|writeLong
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

