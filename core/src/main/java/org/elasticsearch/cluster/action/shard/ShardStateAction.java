begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|RoutingService
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
name|FailedRerouteAllocation
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
name|Priority
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
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|routing
operator|.
name|ShardRouting
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
DECL|field|SHARD_STARTED_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_STARTED_ACTION_NAME
init|=
literal|"internal:cluster/shard/started"
decl_stmt|;
DECL|field|SHARD_FAILED_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_FAILED_ACTION_NAME
init|=
literal|"internal:cluster/shard/failure"
decl_stmt|;
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
DECL|field|routingService
specifier|private
specifier|final
name|RoutingService
name|routingService
decl_stmt|;
DECL|field|startedShardsQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|ShardRoutingEntry
argument_list|>
name|startedShardsQueue
init|=
name|ConcurrentCollections
operator|.
name|newBlockingQueue
argument_list|()
decl_stmt|;
DECL|field|failedShardQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|ShardRoutingEntry
argument_list|>
name|failedShardQueue
init|=
name|ConcurrentCollections
operator|.
name|newBlockingQueue
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
name|RoutingService
name|routingService
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
name|routingService
operator|=
name|routingService
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|SHARD_STARTED_ACTION_NAME
argument_list|,
name|ShardRoutingEntry
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
operator|new
name|ShardStartedTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|SHARD_FAILED_ACTION_NAME
argument_list|,
name|ShardRoutingEntry
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
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
name|indexUUID
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Throwable
name|failure
parameter_list|)
block|{
name|DiscoveryNode
name|masterNode
init|=
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
decl_stmt|;
if|if
condition|(
name|masterNode
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"can't send shard failed for {}, no master known."
argument_list|,
name|shardRouting
argument_list|)
expr_stmt|;
return|return;
block|}
name|innerShardFailed
argument_list|(
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|masterNode
argument_list|,
name|message
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|method|resendShardFailed
specifier|public
name|void
name|resendShardFailed
parameter_list|(
specifier|final
name|ShardRouting
name|shardRouting
parameter_list|,
specifier|final
name|String
name|indexUUID
parameter_list|,
specifier|final
name|DiscoveryNode
name|masterNode
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Throwable
name|failure
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"{} re-sending failed shard for {}, indexUUID [{}], reason [{}]"
argument_list|,
name|failure
argument_list|,
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|innerShardFailed
argument_list|(
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|masterNode
argument_list|,
name|message
argument_list|,
name|failure
argument_list|)
expr_stmt|;
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
name|indexUUID
parameter_list|,
specifier|final
name|DiscoveryNode
name|masterNode
parameter_list|,
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|Throwable
name|failure
parameter_list|)
block|{
name|ShardRoutingEntry
name|shardRoutingEntry
init|=
operator|new
name|ShardRoutingEntry
argument_list|(
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|message
argument_list|,
name|failure
argument_list|)
decl_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|masterNode
argument_list|,
name|SHARD_FAILED_ACTION_NAME
argument_list|,
name|shardRoutingEntry
argument_list|,
operator|new
name|EmptyTransportResponseHandler
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
literal|"failed to send failed shard to {}"
argument_list|,
name|exp
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
name|String
name|indexUUID
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
name|DiscoveryNode
name|masterNode
init|=
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
decl_stmt|;
if|if
condition|(
name|masterNode
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{} can't send shard started for {}, no master known."
argument_list|,
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardRouting
argument_list|)
expr_stmt|;
return|return;
block|}
name|shardStarted
argument_list|(
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|reason
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
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
name|String
name|indexUUID
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|,
specifier|final
name|DiscoveryNode
name|masterNode
parameter_list|)
block|{
name|ShardRoutingEntry
name|shardRoutingEntry
init|=
operator|new
name|ShardRoutingEntry
argument_list|(
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|reason
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"{} sending shard started for {}"
argument_list|,
name|shardRoutingEntry
operator|.
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardRoutingEntry
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|masterNode
argument_list|,
name|SHARD_STARTED_ACTION_NAME
argument_list|,
operator|new
name|ShardRoutingEntry
argument_list|(
name|shardRouting
argument_list|,
name|indexUUID
argument_list|,
name|reason
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|EmptyTransportResponseHandler
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
name|masterNode
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|handleShardFailureOnMaster
specifier|private
name|void
name|handleShardFailureOnMaster
parameter_list|(
specifier|final
name|ShardRoutingEntry
name|shardRoutingEntry
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{} received shard failed for {}"
argument_list|,
name|shardRoutingEntry
operator|.
name|failure
argument_list|,
name|shardRoutingEntry
operator|.
name|shardRouting
operator|.
name|shardId
argument_list|()
argument_list|,
name|shardRoutingEntry
argument_list|)
expr_stmt|;
name|failedShardQueue
operator|.
name|add
argument_list|(
name|shardRoutingEntry
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"shard-failed ("
operator|+
name|shardRoutingEntry
operator|.
name|shardRouting
operator|+
literal|"), message ["
operator|+
name|shardRoutingEntry
operator|.
name|message
operator|+
literal|"]"
argument_list|,
name|Priority
operator|.
name|HIGH
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
name|shardRoutingEntry
operator|.
name|processed
condition|)
block|{
return|return
name|currentState
return|;
block|}
name|List
argument_list|<
name|ShardRoutingEntry
argument_list|>
name|shardRoutingEntries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|failedShardQueue
operator|.
name|drainTo
argument_list|(
name|shardRoutingEntries
argument_list|)
expr_stmt|;
comment|// nothing to process (a previous event has processed it already)
if|if
condition|(
name|shardRoutingEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
name|List
argument_list|<
name|FailedRerouteAllocation
operator|.
name|FailedShard
argument_list|>
name|shardRoutingsToBeApplied
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|shardRoutingEntries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// mark all entries as processed
for|for
control|(
name|ShardRoutingEntry
name|entry
range|:
name|shardRoutingEntries
control|)
block|{
name|entry
operator|.
name|processed
operator|=
literal|true
expr_stmt|;
name|shardRoutingsToBeApplied
operator|.
name|add
argument_list|(
operator|new
name|FailedRerouteAllocation
operator|.
name|FailedShard
argument_list|(
name|entry
operator|.
name|shardRouting
argument_list|,
name|entry
operator|.
name|message
argument_list|,
name|entry
operator|.
name|failure
argument_list|)
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
name|applyFailedShards
argument_list|(
name|currentState
argument_list|,
name|shardRoutingsToBeApplied
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
name|ClusterState
operator|.
name|builder
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
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|oldState
parameter_list|,
name|ClusterState
name|newState
parameter_list|)
block|{
if|if
condition|(
name|oldState
operator|!=
name|newState
operator|&&
name|newState
operator|.
name|getRoutingNodes
argument_list|()
operator|.
name|hasUnassigned
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"unassigned shards after shard failures. scheduling a reroute."
argument_list|)
expr_stmt|;
name|routingService
operator|.
name|reroute
argument_list|(
literal|"unassigned shards after shard failures, scheduling a reroute"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|shardStartedOnMaster
specifier|private
name|void
name|shardStartedOnMaster
parameter_list|(
specifier|final
name|ShardRoutingEntry
name|shardRoutingEntry
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"received shard started for {}"
argument_list|,
name|shardRoutingEntry
argument_list|)
expr_stmt|;
comment|// buffer shard started requests, and the state update tasks will simply drain it
comment|// this is to optimize the number of "started" events we generate, and batch them
comment|// possibly, we can do time based batching as well, but usually, we would want to
comment|// process started events as fast as possible, to make shards available
name|startedShardsQueue
operator|.
name|add
argument_list|(
name|shardRoutingEntry
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"shard-started ("
operator|+
name|shardRoutingEntry
operator|.
name|shardRouting
operator|+
literal|"), reason ["
operator|+
name|shardRoutingEntry
operator|.
name|message
operator|+
literal|"]"
argument_list|,
name|Priority
operator|.
name|URGENT
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
name|shardRoutingEntry
operator|.
name|processed
condition|)
block|{
return|return
name|currentState
return|;
block|}
name|List
argument_list|<
name|ShardRoutingEntry
argument_list|>
name|shardRoutingEntries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|startedShardsQueue
operator|.
name|drainTo
argument_list|(
name|shardRoutingEntries
argument_list|)
expr_stmt|;
comment|// nothing to process (a previous event has processed it already)
if|if
condition|(
name|shardRoutingEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
block|}
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shardRoutingToBeApplied
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|shardRoutingEntries
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// mark all entries as processed
for|for
control|(
name|ShardRoutingEntry
name|entry
range|:
name|shardRoutingEntries
control|)
block|{
name|entry
operator|.
name|processed
operator|=
literal|true
expr_stmt|;
name|shardRoutingToBeApplied
operator|.
name|add
argument_list|(
name|entry
operator|.
name|shardRouting
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shardRoutingToBeApplied
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|currentState
return|;
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
name|shardRoutingToBeApplied
argument_list|,
literal|true
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
name|ClusterState
operator|.
name|builder
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
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|ShardFailedTransportHandler
specifier|private
class|class
name|ShardFailedTransportHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|ShardRoutingEntry
argument_list|>
block|{
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
name|handleShardFailureOnMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ShardStartedTransportHandler
class|class
name|ShardStartedTransportHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|ShardRoutingEntry
argument_list|>
block|{
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
name|shardStartedOnMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|TransportResponse
operator|.
name|Empty
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ShardRoutingEntry
specifier|public
specifier|static
class|class
name|ShardRoutingEntry
extends|extends
name|TransportRequest
block|{
DECL|field|shardRouting
name|ShardRouting
name|shardRouting
decl_stmt|;
DECL|field|indexUUID
name|String
name|indexUUID
init|=
name|IndexMetaData
operator|.
name|INDEX_UUID_NA_VALUE
decl_stmt|;
DECL|field|message
name|String
name|message
decl_stmt|;
DECL|field|failure
name|Throwable
name|failure
decl_stmt|;
DECL|field|processed
specifier|volatile
name|boolean
name|processed
decl_stmt|;
comment|// state field, no need to serialize
DECL|method|ShardRoutingEntry
specifier|public
name|ShardRoutingEntry
parameter_list|()
block|{         }
DECL|method|ShardRoutingEntry
name|ShardRoutingEntry
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|String
name|indexUUID
parameter_list|,
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
name|Throwable
name|failure
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
name|indexUUID
operator|=
name|indexUUID
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|failure
operator|=
name|failure
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
name|shardRouting
operator|=
name|readShardRoutingEntry
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|indexUUID
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|message
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|failure
operator|=
name|in
operator|.
name|readThrowable
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
name|shardRouting
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|indexUUID
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeThrowable
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|shardRouting
operator|+
literal|", indexUUID ["
operator|+
name|indexUUID
operator|+
literal|"], message ["
operator|+
name|message
operator|+
literal|"], failure ["
operator|+
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|failure
argument_list|)
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

