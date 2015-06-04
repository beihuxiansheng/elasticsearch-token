begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.status
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
name|snapshots
operator|.
name|status
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
name|ImmutableMap
import|;
end_import

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
name|ActionFilters
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
name|metadata
operator|.
name|SnapshotId
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
name|index
operator|.
name|snapshots
operator|.
name|IndexShardSnapshotStatus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|SnapshotsService
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReferenceArray
import|;
end_import

begin_comment
comment|/**  * Transport client that collects snapshot shard statuses from data nodes  */
end_comment

begin_class
DECL|class|TransportNodesSnapshotsStatus
specifier|public
class|class
name|TransportNodesSnapshotsStatus
extends|extends
name|TransportNodesAction
argument_list|<
name|TransportNodesSnapshotsStatus
operator|.
name|Request
argument_list|,
name|TransportNodesSnapshotsStatus
operator|.
name|NodesSnapshotStatus
argument_list|,
name|TransportNodesSnapshotsStatus
operator|.
name|NodeRequest
argument_list|,
name|TransportNodesSnapshotsStatus
operator|.
name|NodeSnapshotStatus
argument_list|>
block|{
DECL|field|ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_NAME
init|=
name|SnapshotsStatusAction
operator|.
name|NAME
operator|+
literal|"[nodes]"
decl_stmt|;
DECL|field|snapshotsService
specifier|private
specifier|final
name|SnapshotsService
name|snapshotsService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportNodesSnapshotsStatus
specifier|public
name|TransportNodesSnapshotsStatus
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
name|SnapshotsService
name|snapshotsService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ACTION_NAME
argument_list|,
name|clusterName
argument_list|,
name|threadPool
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|Request
operator|.
name|class
argument_list|,
name|NodeRequest
operator|.
name|class
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|)
expr_stmt|;
name|this
operator|.
name|snapshotsService
operator|=
name|snapshotsService
expr_stmt|;
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
comment|// compress since the metadata can become large
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
name|NodeSnapshotStatus
name|newNodeResponse
parameter_list|()
block|{
return|return
operator|new
name|NodeSnapshotStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|NodesSnapshotStatus
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
name|NodeSnapshotStatus
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
name|NodeSnapshotStatus
condition|)
block|{
comment|// will also filter out null response for unallocated ones
name|nodesList
operator|.
name|add
argument_list|(
operator|(
name|NodeSnapshotStatus
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
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unknown response type [{}], expected NodeSnapshotStatus or FailedNodeException"
argument_list|,
name|resp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|NodesSnapshotStatus
argument_list|(
name|clusterName
argument_list|,
name|nodesList
operator|.
name|toArray
argument_list|(
operator|new
name|NodeSnapshotStatus
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
name|NodeSnapshotStatus
name|nodeOperation
parameter_list|(
name|NodeRequest
name|request
parameter_list|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|SnapshotId
argument_list|,
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
argument_list|>
name|snapshotMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|nodeId
init|=
name|clusterService
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
decl_stmt|;
for|for
control|(
name|SnapshotId
name|snapshotId
range|:
name|request
operator|.
name|snapshotIds
control|)
block|{
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|IndexShardSnapshotStatus
argument_list|>
name|shardsStatus
init|=
name|snapshotsService
operator|.
name|currentSnapshotShards
argument_list|(
name|snapshotId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardsStatus
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
name|shardMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|ImmutableMap
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|IndexShardSnapshotStatus
argument_list|>
name|shardEntry
range|:
name|shardsStatus
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SnapshotIndexShardStatus
name|shardStatus
decl_stmt|;
name|IndexShardSnapshotStatus
operator|.
name|Stage
name|stage
init|=
name|shardEntry
operator|.
name|getValue
argument_list|()
operator|.
name|stage
argument_list|()
decl_stmt|;
if|if
condition|(
name|stage
operator|!=
name|IndexShardSnapshotStatus
operator|.
name|Stage
operator|.
name|DONE
operator|&&
name|stage
operator|!=
name|IndexShardSnapshotStatus
operator|.
name|Stage
operator|.
name|FAILURE
condition|)
block|{
comment|// Store node id for the snapshots that are currently running.
name|shardStatus
operator|=
operator|new
name|SnapshotIndexShardStatus
argument_list|(
name|shardEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|shardEntry
operator|.
name|getValue
argument_list|()
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|shardStatus
operator|=
operator|new
name|SnapshotIndexShardStatus
argument_list|(
name|shardEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|shardEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|shardMapBuilder
operator|.
name|put
argument_list|(
name|shardEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|shardStatus
argument_list|)
expr_stmt|;
block|}
name|snapshotMapBuilder
operator|.
name|put
argument_list|(
name|snapshotId
argument_list|,
name|shardMapBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|NodeSnapshotStatus
argument_list|(
name|clusterService
operator|.
name|localNode
argument_list|()
argument_list|,
name|snapshotMapBuilder
operator|.
name|build
argument_list|()
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
name|ElasticsearchException
argument_list|(
literal|"failed to load metadata"
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
name|BaseNodesRequest
argument_list|<
name|Request
argument_list|>
block|{
DECL|field|snapshotIds
specifier|private
name|SnapshotId
index|[]
name|snapshotIds
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
name|ActionRequest
name|request
parameter_list|,
name|String
index|[]
name|nodesIds
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|nodesIds
argument_list|)
expr_stmt|;
block|}
DECL|method|snapshotIds
specifier|public
name|Request
name|snapshotIds
parameter_list|(
name|SnapshotId
index|[]
name|snapshotIds
parameter_list|)
block|{
name|this
operator|.
name|snapshotIds
operator|=
name|snapshotIds
expr_stmt|;
return|return
name|this
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
comment|// This operation is never executed remotely
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"shouldn't be here"
argument_list|)
throw|;
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
comment|// This operation is never executed remotely
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"shouldn't be here"
argument_list|)
throw|;
block|}
block|}
DECL|class|NodesSnapshotStatus
specifier|public
specifier|static
class|class
name|NodesSnapshotStatus
extends|extends
name|BaseNodesResponse
argument_list|<
name|NodeSnapshotStatus
argument_list|>
block|{
DECL|field|failures
specifier|private
name|FailedNodeException
index|[]
name|failures
decl_stmt|;
DECL|method|NodesSnapshotStatus
name|NodesSnapshotStatus
parameter_list|()
block|{         }
DECL|method|NodesSnapshotStatus
specifier|public
name|NodesSnapshotStatus
parameter_list|(
name|ClusterName
name|clusterName
parameter_list|,
name|NodeSnapshotStatus
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
name|NodeSnapshotStatus
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
name|NodeSnapshotStatus
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
name|NodeSnapshotStatus
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
name|BaseNodeRequest
block|{
DECL|field|snapshotIds
specifier|private
name|SnapshotId
index|[]
name|snapshotIds
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
name|TransportNodesSnapshotsStatus
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
name|snapshotIds
operator|=
name|request
operator|.
name|snapshotIds
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
name|int
name|n
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|snapshotIds
operator|=
operator|new
name|SnapshotId
index|[
name|n
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|snapshotIds
index|[
name|i
index|]
operator|=
name|SnapshotId
operator|.
name|readSnapshotId
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
if|if
condition|(
name|snapshotIds
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|snapshotIds
operator|.
name|length
argument_list|)
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
name|snapshotIds
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|snapshotIds
index|[
name|i
index|]
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|NodeSnapshotStatus
specifier|public
specifier|static
class|class
name|NodeSnapshotStatus
extends|extends
name|BaseNodeResponse
block|{
DECL|field|status
specifier|private
name|ImmutableMap
argument_list|<
name|SnapshotId
argument_list|,
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
argument_list|>
name|status
decl_stmt|;
DECL|method|NodeSnapshotStatus
name|NodeSnapshotStatus
parameter_list|()
block|{         }
DECL|method|NodeSnapshotStatus
specifier|public
name|NodeSnapshotStatus
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|ImmutableMap
argument_list|<
name|SnapshotId
argument_list|,
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
argument_list|>
name|status
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
block|}
DECL|method|status
specifier|public
name|ImmutableMap
argument_list|<
name|SnapshotId
argument_list|,
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
argument_list|>
name|status
parameter_list|()
block|{
return|return
name|status
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
name|int
name|numberOfSnapshots
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|SnapshotId
argument_list|,
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
argument_list|>
name|snapshotMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
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
name|numberOfSnapshots
condition|;
name|i
operator|++
control|)
block|{
name|SnapshotId
name|snapshotId
init|=
name|SnapshotId
operator|.
name|readSnapshotId
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
name|shardMapBuilder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|numberOfShards
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numberOfShards
condition|;
name|j
operator|++
control|)
block|{
name|ShardId
name|shardId
init|=
name|ShardId
operator|.
name|readShardId
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|SnapshotIndexShardStatus
name|status
init|=
name|SnapshotIndexShardStatus
operator|.
name|readShardSnapshotStatus
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|shardMapBuilder
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
name|snapshotMapBuilder
operator|.
name|put
argument_list|(
name|snapshotId
argument_list|,
name|shardMapBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|status
operator|=
name|snapshotMapBuilder
operator|.
name|build
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
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|status
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ImmutableMap
operator|.
name|Entry
argument_list|<
name|SnapshotId
argument_list|,
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
argument_list|>
name|entry
range|:
name|status
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getKey
argument_list|()
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
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ImmutableMap
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|SnapshotIndexShardStatus
argument_list|>
name|shardEntry
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|shardEntry
operator|.
name|getKey
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|shardEntry
operator|.
name|getValue
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

