begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
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
name|ExceptionsHelper
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
name|UnavailableShardsException
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
name|ActiveShardCount
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
name|TransportActions
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
name|routing
operator|.
name|AllocationId
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
name|util
operator|.
name|set
operator|.
name|Sets
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
name|engine
operator|.
name|VersionConflictEngineException
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
name|rest
operator|.
name|RestStatus
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
name|Collections
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
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|AtomicBoolean
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
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_class
DECL|class|ReplicationOperation
specifier|public
class|class
name|ReplicationOperation
parameter_list|<
name|Request
extends|extends
name|ReplicationRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|ReplicaRequest
extends|extends
name|ReplicationRequest
parameter_list|<
name|ReplicaRequest
parameter_list|>
parameter_list|,
name|PrimaryResultT
extends|extends
name|ReplicationOperation
operator|.
name|PrimaryResult
parameter_list|<
name|ReplicaRequest
parameter_list|>
parameter_list|>
block|{
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|clusterStateSupplier
specifier|private
specifier|final
name|Supplier
argument_list|<
name|ClusterState
argument_list|>
name|clusterStateSupplier
decl_stmt|;
DECL|field|opType
specifier|private
specifier|final
name|String
name|opType
decl_stmt|;
DECL|field|totalShards
specifier|private
specifier|final
name|AtomicInteger
name|totalShards
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
comment|/**      * The number of pending sub-operations in this operation. This is incremented when the following operations start and decremented when      * they complete:      *<ul>      *<li>The operation on the primary</li>      *<li>The operation on each replica</li>      *<li>Coordination of the operation as a whole. This prevents the operation from terminating early if we haven't started any replica      * operations and the primary finishes.</li>      *</ul>      */
DECL|field|pendingActions
specifier|private
specifier|final
name|AtomicInteger
name|pendingActions
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|successfulShards
specifier|private
specifier|final
name|AtomicInteger
name|successfulShards
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|executeOnReplicas
specifier|private
specifier|final
name|boolean
name|executeOnReplicas
decl_stmt|;
DECL|field|primary
specifier|private
specifier|final
name|Primary
argument_list|<
name|Request
argument_list|,
name|ReplicaRequest
argument_list|,
name|PrimaryResultT
argument_list|>
name|primary
decl_stmt|;
DECL|field|replicasProxy
specifier|private
specifier|final
name|Replicas
argument_list|<
name|ReplicaRequest
argument_list|>
name|replicasProxy
decl_stmt|;
DECL|field|finished
specifier|private
specifier|final
name|AtomicBoolean
name|finished
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|resultListener
specifier|protected
specifier|final
name|ActionListener
argument_list|<
name|PrimaryResultT
argument_list|>
name|resultListener
decl_stmt|;
DECL|field|primaryResult
specifier|private
specifier|volatile
name|PrimaryResultT
name|primaryResult
init|=
literal|null
decl_stmt|;
DECL|field|shardReplicaFailures
specifier|private
specifier|final
name|List
argument_list|<
name|ReplicationResponse
operator|.
name|ShardInfo
operator|.
name|Failure
argument_list|>
name|shardReplicaFailures
init|=
name|Collections
operator|.
name|synchronizedList
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|ReplicationOperation
specifier|public
name|ReplicationOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|Primary
argument_list|<
name|Request
argument_list|,
name|ReplicaRequest
argument_list|,
name|PrimaryResultT
argument_list|>
name|primary
parameter_list|,
name|ActionListener
argument_list|<
name|PrimaryResultT
argument_list|>
name|listener
parameter_list|,
name|boolean
name|executeOnReplicas
parameter_list|,
name|Replicas
argument_list|<
name|ReplicaRequest
argument_list|>
name|replicas
parameter_list|,
name|Supplier
argument_list|<
name|ClusterState
argument_list|>
name|clusterStateSupplier
parameter_list|,
name|Logger
name|logger
parameter_list|,
name|String
name|opType
parameter_list|)
block|{
name|this
operator|.
name|executeOnReplicas
operator|=
name|executeOnReplicas
expr_stmt|;
name|this
operator|.
name|replicasProxy
operator|=
name|replicas
expr_stmt|;
name|this
operator|.
name|primary
operator|=
name|primary
expr_stmt|;
name|this
operator|.
name|resultListener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|clusterStateSupplier
operator|=
name|clusterStateSupplier
expr_stmt|;
name|this
operator|.
name|opType
operator|=
name|opType
expr_stmt|;
block|}
DECL|method|execute
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|activeShardCountFailure
init|=
name|checkActiveShardCount
argument_list|()
decl_stmt|;
specifier|final
name|ShardRouting
name|primaryRouting
init|=
name|primary
operator|.
name|routingEntry
argument_list|()
decl_stmt|;
specifier|final
name|ShardId
name|primaryId
init|=
name|primaryRouting
operator|.
name|shardId
argument_list|()
decl_stmt|;
if|if
condition|(
name|activeShardCountFailure
operator|!=
literal|null
condition|)
block|{
name|finishAsFailed
argument_list|(
operator|new
name|UnavailableShardsException
argument_list|(
name|primaryId
argument_list|,
literal|"{} Timeout: [{}], request: [{}]"
argument_list|,
name|activeShardCountFailure
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|,
name|request
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|totalShards
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|pendingActions
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// increase by 1 until we finish all primary coordination
name|primaryResult
operator|=
name|primary
operator|.
name|perform
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|primary
operator|.
name|updateLocalCheckpointForShard
argument_list|(
name|primaryRouting
operator|.
name|allocationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|,
name|primary
operator|.
name|localCheckpoint
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ReplicaRequest
name|replicaRequest
init|=
name|primaryResult
operator|.
name|replicaRequest
argument_list|()
decl_stmt|;
if|if
condition|(
name|replicaRequest
operator|!=
literal|null
condition|)
block|{
assert|assert
name|replicaRequest
operator|.
name|primaryTerm
argument_list|()
operator|>
literal|0
operator|:
literal|"replicaRequest doesn't have a primary term"
assert|;
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] op [{}] completed on primary for request [{}]"
argument_list|,
name|primaryId
argument_list|,
name|opType
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
comment|// we have to get a new state after successfully indexing into the primary in order to honour recovery semantics.
comment|// we have to make sure that every operation indexed into the primary after recovery start will also be replicated
comment|// to the recovery target. If we use an old cluster state, we may miss a relocation that has started since then.
name|ClusterState
name|clusterState
init|=
name|clusterStateSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
init|=
name|getShards
argument_list|(
name|primaryId
argument_list|,
name|clusterState
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|inSyncAllocationIds
init|=
name|getInSyncAllocationIds
argument_list|(
name|primaryId
argument_list|,
name|clusterState
argument_list|)
decl_stmt|;
name|markUnavailableShardsAsStale
argument_list|(
name|replicaRequest
argument_list|,
name|inSyncAllocationIds
argument_list|,
name|shards
argument_list|)
expr_stmt|;
name|performOnReplicas
argument_list|(
name|replicaRequest
argument_list|,
name|shards
argument_list|)
expr_stmt|;
block|}
name|successfulShards
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
comment|// mark primary as successful
name|decPendingAndFinishIfNeeded
argument_list|()
expr_stmt|;
block|}
DECL|method|markUnavailableShardsAsStale
specifier|private
name|void
name|markUnavailableShardsAsStale
parameter_list|(
name|ReplicaRequest
name|replicaRequest
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|inSyncAllocationIds
parameter_list|,
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
parameter_list|)
block|{
if|if
condition|(
name|inSyncAllocationIds
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
operator|&&
name|shards
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|availableAllocationIds
init|=
name|shards
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|ShardRouting
operator|::
name|allocationId
argument_list|)
operator|.
name|filter
argument_list|(
name|Objects
operator|::
name|nonNull
argument_list|)
operator|.
name|map
argument_list|(
name|AllocationId
operator|::
name|getId
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
comment|// if inSyncAllocationIds contains allocation ids of shards that don't exist in RoutingTable, mark copies as stale
for|for
control|(
name|String
name|allocationId
range|:
name|Sets
operator|.
name|difference
argument_list|(
name|inSyncAllocationIds
argument_list|,
name|availableAllocationIds
argument_list|)
control|)
block|{
comment|// mark copy as stale
name|pendingActions
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|replicasProxy
operator|.
name|markShardCopyAsStaleIfNeeded
argument_list|(
name|replicaRequest
operator|.
name|shardId
argument_list|()
argument_list|,
name|allocationId
argument_list|,
name|replicaRequest
operator|.
name|primaryTerm
argument_list|()
argument_list|,
name|ReplicationOperation
operator|.
name|this
operator|::
name|decPendingAndFinishIfNeeded
argument_list|,
name|ReplicationOperation
operator|.
name|this
operator|::
name|onPrimaryDemoted
argument_list|,
name|throwable
lambda|->
name|decPendingAndFinishIfNeeded
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|performOnReplicas
specifier|private
name|void
name|performOnReplicas
parameter_list|(
name|ReplicaRequest
name|replicaRequest
parameter_list|,
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
parameter_list|)
block|{
specifier|final
name|String
name|localNodeId
init|=
name|primary
operator|.
name|routingEntry
argument_list|()
operator|.
name|currentNodeId
argument_list|()
decl_stmt|;
comment|// If the index gets deleted after primary operation, we skip replication
for|for
control|(
specifier|final
name|ShardRouting
name|shard
range|:
name|shards
control|)
block|{
if|if
condition|(
name|executeOnReplicas
operator|==
literal|false
operator|||
name|shard
operator|.
name|unassigned
argument_list|()
condition|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|==
literal|false
condition|)
block|{
name|totalShards
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
continue|continue;
block|}
if|if
condition|(
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|localNodeId
argument_list|)
operator|==
literal|false
condition|)
block|{
name|performOnReplica
argument_list|(
name|shard
argument_list|,
name|replicaRequest
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shard
operator|.
name|relocating
argument_list|()
operator|&&
name|shard
operator|.
name|relocatingNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|localNodeId
argument_list|)
operator|==
literal|false
condition|)
block|{
name|performOnReplica
argument_list|(
name|shard
operator|.
name|getTargetRelocatingShard
argument_list|()
argument_list|,
name|replicaRequest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|performOnReplica
specifier|private
name|void
name|performOnReplica
parameter_list|(
specifier|final
name|ShardRouting
name|shard
parameter_list|,
specifier|final
name|ReplicaRequest
name|replicaRequest
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] sending op [{}] to replica {} for request [{}]"
argument_list|,
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|opType
argument_list|,
name|shard
argument_list|,
name|replicaRequest
argument_list|)
expr_stmt|;
block|}
name|totalShards
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|pendingActions
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|replicasProxy
operator|.
name|performOn
argument_list|(
name|shard
argument_list|,
name|replicaRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|ReplicaResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|ReplicaResponse
name|response
parameter_list|)
block|{
name|successfulShards
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|primary
operator|.
name|updateLocalCheckpointForShard
argument_list|(
name|response
operator|.
name|allocationId
argument_list|()
argument_list|,
name|response
operator|.
name|localCheckpoint
argument_list|()
argument_list|)
expr_stmt|;
name|decPendingAndFinishIfNeeded
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|replicaException
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
call|(
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"[{}] failure while performing [{}] on replica {}, request [{}]"
argument_list|,
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|opType
argument_list|,
name|shard
argument_list|,
name|replicaRequest
argument_list|)
argument_list|,
name|replicaException
argument_list|)
expr_stmt|;
if|if
condition|(
name|TransportActions
operator|.
name|isShardNotAvailableException
argument_list|(
name|replicaException
argument_list|)
condition|)
block|{
name|decPendingAndFinishIfNeeded
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RestStatus
name|restStatus
init|=
name|ExceptionsHelper
operator|.
name|status
argument_list|(
name|replicaException
argument_list|)
decl_stmt|;
name|shardReplicaFailures
operator|.
name|add
argument_list|(
operator|new
name|ReplicationResponse
operator|.
name|ShardInfo
operator|.
name|Failure
argument_list|(
name|shard
operator|.
name|shardId
argument_list|()
argument_list|,
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|replicaException
argument_list|,
name|restStatus
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"failed to perform %s on replica %s"
argument_list|,
name|opType
argument_list|,
name|shard
argument_list|)
decl_stmt|;
name|replicasProxy
operator|.
name|failShardIfNeeded
argument_list|(
name|shard
argument_list|,
name|replicaRequest
operator|.
name|primaryTerm
argument_list|()
argument_list|,
name|message
argument_list|,
name|replicaException
argument_list|,
name|ReplicationOperation
operator|.
name|this
operator|::
name|decPendingAndFinishIfNeeded
argument_list|,
name|ReplicationOperation
operator|.
name|this
operator|::
name|onPrimaryDemoted
argument_list|,
name|throwable
lambda|->
name|decPendingAndFinishIfNeeded
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|onPrimaryDemoted
specifier|private
name|void
name|onPrimaryDemoted
parameter_list|(
name|Exception
name|demotionFailure
parameter_list|)
block|{
name|String
name|primaryFail
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"primary shard [%s] was demoted while failing replica shard"
argument_list|,
name|primary
operator|.
name|routingEntry
argument_list|()
argument_list|)
decl_stmt|;
comment|// we are no longer the primary, fail ourselves and start over
name|primary
operator|.
name|failShard
argument_list|(
name|primaryFail
argument_list|,
name|demotionFailure
argument_list|)
expr_stmt|;
name|finishAsFailed
argument_list|(
operator|new
name|RetryOnPrimaryException
argument_list|(
name|primary
operator|.
name|routingEntry
argument_list|()
operator|.
name|shardId
argument_list|()
argument_list|,
name|primaryFail
argument_list|,
name|demotionFailure
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks whether we can perform a write based on the required active shard count setting.      * Returns **null* if OK to proceed, or a string describing the reason to stop      */
DECL|method|checkActiveShardCount
specifier|protected
name|String
name|checkActiveShardCount
parameter_list|()
block|{
specifier|final
name|ShardId
name|shardId
init|=
name|primary
operator|.
name|routingEntry
argument_list|()
operator|.
name|shardId
argument_list|()
decl_stmt|;
specifier|final
name|String
name|indexName
init|=
name|shardId
operator|.
name|getIndexName
argument_list|()
decl_stmt|;
specifier|final
name|ClusterState
name|state
init|=
name|clusterStateSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
name|state
operator|!=
literal|null
operator|:
literal|"replication operation must have access to the cluster state"
assert|;
specifier|final
name|ActiveShardCount
name|waitForActiveShards
init|=
name|request
operator|.
name|waitForActiveShards
argument_list|()
decl_stmt|;
if|if
condition|(
name|waitForActiveShards
operator|==
name|ActiveShardCount
operator|.
name|NONE
condition|)
block|{
return|return
literal|null
return|;
comment|// not waiting for any shards
block|}
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|state
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|index
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexRoutingTable
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] index not found in the routing table"
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
return|return
literal|"Index "
operator|+
name|indexName
operator|+
literal|" not found in the routing table"
return|;
block|}
name|IndexShardRoutingTable
name|shardRoutingTable
init|=
name|indexRoutingTable
operator|.
name|shard
argument_list|(
name|shardId
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardRoutingTable
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] shard not found in the routing table"
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
return|return
literal|"Shard "
operator|+
name|shardId
operator|+
literal|" not found in the routing table"
return|;
block|}
if|if
condition|(
name|waitForActiveShards
operator|.
name|enoughShardsActive
argument_list|(
name|shardRoutingTable
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|String
name|resolvedShards
init|=
name|waitForActiveShards
operator|==
name|ActiveShardCount
operator|.
name|ALL
condition|?
name|Integer
operator|.
name|toString
argument_list|(
name|shardRoutingTable
operator|.
name|shards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
else|:
name|waitForActiveShards
operator|.
name|toString
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] not enough active copies to meet shard count of [{}] (have {}, needed {}), scheduling a retry. op [{}], "
operator|+
literal|"request [{}]"
argument_list|,
name|shardId
argument_list|,
name|waitForActiveShards
argument_list|,
name|shardRoutingTable
operator|.
name|activeShards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|resolvedShards
argument_list|,
name|opType
argument_list|,
name|request
argument_list|)
expr_stmt|;
return|return
literal|"Not enough active copies to meet shard count of ["
operator|+
name|waitForActiveShards
operator|+
literal|"] (have "
operator|+
name|shardRoutingTable
operator|.
name|activeShards
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|", needed "
operator|+
name|resolvedShards
operator|+
literal|")."
return|;
block|}
block|}
DECL|method|getInSyncAllocationIds
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|getInSyncAllocationIds
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|shardId
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexMetaData
operator|!=
literal|null
condition|)
block|{
return|return
name|indexMetaData
operator|.
name|inSyncAllocationIds
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
DECL|method|getShards
specifier|protected
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|getShards
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
comment|// can be null if the index is deleted / closed on us..
specifier|final
name|IndexShardRoutingTable
name|shardRoutingTable
init|=
name|state
operator|.
name|getRoutingTable
argument_list|()
operator|.
name|shardRoutingTableOrNull
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
init|=
name|shardRoutingTable
operator|==
literal|null
condition|?
name|Collections
operator|.
name|emptyList
argument_list|()
else|:
name|shardRoutingTable
operator|.
name|shards
argument_list|()
decl_stmt|;
return|return
name|shards
return|;
block|}
DECL|method|decPendingAndFinishIfNeeded
specifier|private
name|void
name|decPendingAndFinishIfNeeded
parameter_list|()
block|{
assert|assert
name|pendingActions
operator|.
name|get
argument_list|()
operator|>
literal|0
operator|:
literal|"pending action count goes below 0 for request ["
operator|+
name|request
operator|+
literal|"]"
assert|;
if|if
condition|(
name|pendingActions
operator|.
name|decrementAndGet
argument_list|()
operator|==
literal|0
condition|)
block|{
name|finish
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|finish
specifier|private
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|finished
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
specifier|final
name|ReplicationResponse
operator|.
name|ShardInfo
operator|.
name|Failure
index|[]
name|failuresArray
decl_stmt|;
if|if
condition|(
name|shardReplicaFailures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|failuresArray
operator|=
name|ReplicationResponse
operator|.
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|failuresArray
operator|=
operator|new
name|ReplicationResponse
operator|.
name|ShardInfo
operator|.
name|Failure
index|[
name|shardReplicaFailures
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|shardReplicaFailures
operator|.
name|toArray
argument_list|(
name|failuresArray
argument_list|)
expr_stmt|;
block|}
name|primaryResult
operator|.
name|setShardInfo
argument_list|(
operator|new
name|ReplicationResponse
operator|.
name|ShardInfo
argument_list|(
name|totalShards
operator|.
name|get
argument_list|()
argument_list|,
name|successfulShards
operator|.
name|get
argument_list|()
argument_list|,
name|failuresArray
argument_list|)
argument_list|)
expr_stmt|;
name|resultListener
operator|.
name|onResponse
argument_list|(
name|primaryResult
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|finishAsFailed
specifier|private
name|void
name|finishAsFailed
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
if|if
condition|(
name|finished
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|resultListener
operator|.
name|onFailure
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * An encapsulation of an operation that is to be performed on the primary shard      */
DECL|interface|Primary
specifier|public
interface|interface
name|Primary
parameter_list|<
name|RequestT
extends|extends
name|ReplicationRequest
parameter_list|<
name|RequestT
parameter_list|>
parameter_list|,
name|ReplicaRequestT
extends|extends
name|ReplicationRequest
parameter_list|<
name|ReplicaRequestT
parameter_list|>
parameter_list|,
name|PrimaryResultT
extends|extends
name|PrimaryResult
parameter_list|<
name|ReplicaRequestT
parameter_list|>
parameter_list|>
block|{
comment|/**          * routing entry for this primary          */
DECL|method|routingEntry
name|ShardRouting
name|routingEntry
parameter_list|()
function_decl|;
comment|/**          * fail the primary, typically due to the fact that the operation has learned the primary has been demoted by the master          */
DECL|method|failShard
name|void
name|failShard
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|)
function_decl|;
comment|/**          * Performs the given request on this primary. Yes, this returns as soon as it can with the request for the replicas and calls a          * listener when the primary request is completed. Yes, the primary request might complete before the method returns. Yes, it might          * also complete after. Deal with it.          *          * @param request the request to perform          * @return the request to send to the repicas          */
DECL|method|perform
name|PrimaryResultT
name|perform
parameter_list|(
name|RequestT
name|request
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**          * Notifies the primary of a local checkpoint for the given allocation.          *          * Note: The primary will use this information to advance the global checkpoint if possible.          *          * @param allocationId allocation ID of the shard corresponding to the supplied local checkpoint          * @param checkpoint the *local* checkpoint for the shard          */
DECL|method|updateLocalCheckpointForShard
name|void
name|updateLocalCheckpointForShard
parameter_list|(
name|String
name|allocationId
parameter_list|,
name|long
name|checkpoint
parameter_list|)
function_decl|;
comment|/** returns the local checkpoint of the primary shard */
DECL|method|localCheckpoint
name|long
name|localCheckpoint
parameter_list|()
function_decl|;
block|}
comment|/**      * An encapsulation of an operation that will be executed on the replica shards, if present.      */
DECL|interface|Replicas
specifier|public
interface|interface
name|Replicas
parameter_list|<
name|RequestT
extends|extends
name|ReplicationRequest
parameter_list|<
name|RequestT
parameter_list|>
parameter_list|>
block|{
comment|/**          * performs the the given request on the specified replica          *          * @param replica        {@link ShardRouting} of the shard this request should be executed on          * @param replicaRequest operation to peform          * @param listener       a callback to call once the operation has been complicated, either successfully or with an error.          */
DECL|method|performOn
name|void
name|performOn
parameter_list|(
name|ShardRouting
name|replica
parameter_list|,
name|RequestT
name|replicaRequest
parameter_list|,
name|ActionListener
argument_list|<
name|ReplicaResponse
argument_list|>
name|listener
parameter_list|)
function_decl|;
comment|/**          * Fail the specified shard if needed, removing it from the current set          * of active shards. Whether a failure is needed is left up to the          * implementation.          *          * @param replica          shard to fail          * @param primaryTerm      the primary term of the primary shard when requesting the failure          * @param message          a (short) description of the reason          * @param exception        the original exception which caused the ReplicationOperation to request the shard to be failed          * @param onSuccess        a callback to call when the shard has been successfully removed from the active set.          * @param onPrimaryDemoted a callback to call when the shard can not be failed because the current primary has been demoted          *                         by the master.          * @param onIgnoredFailure a callback to call when failing a shard has failed, but it that failure can be safely ignored and the          */
DECL|method|failShardIfNeeded
name|void
name|failShardIfNeeded
parameter_list|(
name|ShardRouting
name|replica
parameter_list|,
name|long
name|primaryTerm
parameter_list|,
name|String
name|message
parameter_list|,
name|Exception
name|exception
parameter_list|,
name|Runnable
name|onSuccess
parameter_list|,
name|Consumer
argument_list|<
name|Exception
argument_list|>
name|onPrimaryDemoted
parameter_list|,
name|Consumer
argument_list|<
name|Exception
argument_list|>
name|onIgnoredFailure
parameter_list|)
function_decl|;
comment|/**          * Marks shard copy as stale if needed, removing its allocation id from          * the set of in-sync allocation ids. Whether marking as stale is needed          * is left up to the implementation.          *          * @param shardId          shard id          * @param allocationId     allocation id to remove from the set of in-sync allocation ids          * @param primaryTerm      the primary term of the primary shard when requesting the failure          * @param onSuccess        a callback to call when the allocation id has been successfully removed from the in-sync set.          * @param onPrimaryDemoted a callback to call when the request failed because the current primary was already demoted          *                         by the master.          * @param onIgnoredFailure a callback to call when the request failed, but the failure can be safely ignored.          */
DECL|method|markShardCopyAsStaleIfNeeded
name|void
name|markShardCopyAsStaleIfNeeded
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|allocationId
parameter_list|,
name|long
name|primaryTerm
parameter_list|,
name|Runnable
name|onSuccess
parameter_list|,
name|Consumer
argument_list|<
name|Exception
argument_list|>
name|onPrimaryDemoted
parameter_list|,
name|Consumer
argument_list|<
name|Exception
argument_list|>
name|onIgnoredFailure
parameter_list|)
function_decl|;
block|}
comment|/**      * An interface to encapsulate the metadata needed from replica shards when they respond to operations performed on them      */
DECL|interface|ReplicaResponse
specifier|public
interface|interface
name|ReplicaResponse
block|{
comment|/** the local check point for the shard. see {@link org.elasticsearch.index.seqno.SequenceNumbersService#getLocalCheckpoint()} */
DECL|method|localCheckpoint
name|long
name|localCheckpoint
parameter_list|()
function_decl|;
comment|/** the allocation id of the replica shard */
DECL|method|allocationId
name|String
name|allocationId
parameter_list|()
function_decl|;
block|}
DECL|class|RetryOnPrimaryException
specifier|public
specifier|static
class|class
name|RetryOnPrimaryException
extends|extends
name|ElasticsearchException
block|{
DECL|method|RetryOnPrimaryException
specifier|public
name|RetryOnPrimaryException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
argument_list|(
name|shardId
argument_list|,
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|RetryOnPrimaryException
specifier|public
name|RetryOnPrimaryException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
name|setShard
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
DECL|method|RetryOnPrimaryException
specifier|public
name|RetryOnPrimaryException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|PrimaryResult
specifier|public
interface|interface
name|PrimaryResult
parameter_list|<
name|RequestT
extends|extends
name|ReplicationRequest
parameter_list|<
name|RequestT
parameter_list|>
parameter_list|>
block|{
comment|/**          * @return null if no operation needs to be sent to a replica          * (for example when the operation failed on the primary due to a parsing exception)          */
DECL|method|replicaRequest
annotation|@
name|Nullable
name|RequestT
name|replicaRequest
parameter_list|()
function_decl|;
DECL|method|setShardInfo
name|void
name|setShardInfo
parameter_list|(
name|ReplicationResponse
operator|.
name|ShardInfo
name|shardInfo
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

