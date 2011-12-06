begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.action.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|action
operator|.
name|shard
package|;
end_package

begin_import
import|import
name|jsr166y
operator|.
name|LinkedTransferQueue
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
name|ClusterStateUpdateTask
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
name|RoutingTable
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
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|AllocationService
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
name|allocation
operator|.
name|RoutingAllocation
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
name|component
operator|.
name|AbstractComponent
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|VoidStreamable
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
name|BlockingQueue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
operator|.
name|newClusterStateBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ImmutableShardRouting
operator|.
name|readShardRoutingEntry
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ShardStateAction
specifier|public
class|class
name|ShardStateAction
extends|extends
name|AbstractComponent
block|{
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|allocationService
specifier|private
specifier|final
name|AllocationService
name|allocationService
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|startedShardsQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|ShardRouting
argument_list|>
name|startedShardsQueue
init|=
operator|new
name|LinkedTransferQueue
argument_list|<
name|ShardRouting
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|ShardStateAction
specifier|public
name|ShardStateAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|AllocationService
name|allocationService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|allocationService
operator|=
name|allocationService
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|ShardStartedTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|ShardStartedTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|ShardFailedTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|ShardFailedTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|shardFailed
specifier|public
name|void
name|shardFailed
parameter_list|(
specifier|final
name|ShardRouting
name|shardRouting
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"sending failed shard for {}, reason [{}]"
argument_list|,
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|DiscoveryNodes
name|nodes
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|innerShardFailed
argument_list|(
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|,
name|ShardFailedTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|ShardRoutingEntry
argument_list|(
name|shardRouting
argument_list|,
name|reason
argument_list|)
argument_list|,
operator|new
name|VoidTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to send failed shard to [{}]"
argument_list|,
name|exp
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|shardStarted
specifier|public
name|void
name|shardStarted
parameter_list|(
specifier|final
name|ShardRouting
name|shardRouting
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"sending shard started for {}, reason [{}]"
argument_list|,
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
name|DiscoveryNodes
name|nodes
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|innerShardStarted
argument_list|(
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|,
name|ShardStartedTransportHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|ShardRoutingEntry
argument_list|(
name|shardRouting
argument_list|,
name|reason
argument_list|)
argument_list|,
operator|new
name|VoidTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to send shard started to [{}]"
argument_list|,
name|exp
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerShardFailed
specifier|private
name|void
name|innerShardFailed
parameter_list|(
specifier|final
name|ShardRouting
name|shardRouting
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"received shard failed for {}, reason [{}]"
argument_list|,
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"shard-failed ("
operator|+
name|shardRouting
operator|+
literal|"), reason ["
operator|+
name|reason
operator|+
literal|"]"
argument_list|,
operator|new
name|ClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Received failed shard {}, reason [{}]"
argument_list|,
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|applyFailedShard
argument_list|(
name|currentState
argument_list|,
name|shardRouting
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|routingResult
operator|.
name|changed
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Applying failed shard {}, reason [{}]"
argument_list|,
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|routingResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|innerShardStarted
specifier|private
name|void
name|innerShardStarted
parameter_list|(
specifier|final
name|ShardRouting
name|shardRouting
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"received shard started for {}, reason [{}]"
argument_list|,
name|shardRouting
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
comment|// buffer shard started requests, and the state update tasks will simply drain it
comment|// this is to optimize the number of "started" events we generate, and batch them
comment|// possibly, we can do time based batching as well, but usually, we would want to
comment|// process started events as fast as possible, to make shards available
name|startedShardsQueue
operator|.
name|add
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"shard-started ("
operator|+
name|shardRouting
operator|+
literal|"), reason ["
operator|+
name|reason
operator|+
literal|"]"
argument_list|,
operator|new
name|ClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
init|=
operator|new
name|ArrayList
argument_list|<
name|ShardRouting
argument_list|>
argument_list|()
decl_stmt|;
name|startedShardsQueue
operator|.
name|drainTo
argument_list|(
name|shards
argument_list|)
expr_stmt|;
comment|// nothing to process (a previous event has process it already)
if|if
condition|(
name|shards
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
name|RoutingTable
name|routingTable
init|=
name|currentState
operator|.
name|routingTable
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
name|shards
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ShardRouting
name|shardRouting
init|=
name|shards
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|routingTable
operator|.
name|index
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
comment|// if there is no routing table, the index has been deleted while it was being allocated
comment|// which is fine, we should just ignore this
if|if
condition|(
name|indexRoutingTable
operator|==
literal|null
condition|)
block|{
name|shards
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// find the one that maps to us, if its already started, no need to do anything...
comment|// the shard might already be started since the nodes that is starting the shards might get cluster events
comment|// with the shard still initializing, and it will try and start it again (until the verification comes)
name|IndexShardRoutingTable
name|indexShardRoutingTable
init|=
name|indexRoutingTable
operator|.
name|shard
argument_list|(
name|shardRouting
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardRouting
name|entry
range|:
name|indexShardRoutingTable
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|currentNodeId
argument_list|()
argument_list|)
condition|)
block|{
comment|// we found the same shard that exists on the same node id
if|if
condition|(
name|entry
operator|.
name|started
argument_list|()
condition|)
block|{
comment|// already started, do nothing here...
name|shards
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|shards
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"applying started shards {}, reason [{}]"
argument_list|,
name|shards
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
name|RoutingAllocation
operator|.
name|Result
name|routingResult
init|=
name|allocationService
operator|.
name|applyStartedShards
argument_list|(
name|currentState
argument_list|,
name|shards
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|routingResult
operator|.
name|changed
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|routingResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|ShardFailedTransportHandler
specifier|private
class|class
name|ShardFailedTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|ShardRoutingEntry
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"cluster/shardFailure"
decl_stmt|;
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|ShardRoutingEntry
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardRoutingEntry
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ShardRoutingEntry
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|innerShardFailed
argument_list|(
name|request
operator|.
name|shardRouting
argument_list|,
name|request
operator|.
name|reason
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|VoidStreamable
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
block|}
DECL|class|ShardStartedTransportHandler
specifier|private
class|class
name|ShardStartedTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|ShardRoutingEntry
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"cluster/shardStarted"
decl_stmt|;
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|ShardRoutingEntry
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardRoutingEntry
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ShardRoutingEntry
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|innerShardStarted
argument_list|(
name|request
operator|.
name|shardRouting
argument_list|,
name|request
operator|.
name|reason
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|VoidStreamable
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|public
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
block|}
DECL|class|ShardRoutingEntry
specifier|private
specifier|static
class|class
name|ShardRoutingEntry
implements|implements
name|Streamable
block|{
DECL|field|shardRouting
specifier|private
name|ShardRouting
name|shardRouting
decl_stmt|;
DECL|field|reason
specifier|private
name|String
name|reason
decl_stmt|;
DECL|method|ShardRoutingEntry
specifier|private
name|ShardRoutingEntry
parameter_list|()
block|{         }
DECL|method|ShardRoutingEntry
specifier|private
name|ShardRoutingEntry
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|this
operator|.
name|shardRouting
operator|=
name|shardRouting
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
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
name|shardRouting
operator|=
name|readShardRoutingEntry
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|reason
operator|=
name|in
operator|.
name|readUTF
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
name|shardRouting
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

