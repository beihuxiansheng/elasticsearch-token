begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.seqno
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|seqno
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectLongMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|cursors
operator|.
name|ObjectLongCursor
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
name|IndexSettings
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
name|AbstractIndexShardComponent
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * A shard component that is responsible of tracking the global checkpoint. The global checkpoint  * is the highest seq_no for which all lower (or equal) seq_no have been processed on all shards that  * are currently active. Since shards count as "active" when the master starts them, and before this primary shard  * has been notified of this fact, we also include shards in that are in the  * {@link org.elasticsearch.index.shard.IndexShardState#POST_RECOVERY} state when checking for global checkpoint advancement.  * We call these shards "in sync" with all operations on the primary (see {@link #inSyncLocalCheckpoints}.  *  *<p>  * The global checkpoint is maintained by the primary shard and is replicated to all the replicas  * (via {@link GlobalCheckpointSyncAction}).  */
end_comment

begin_class
DECL|class|GlobalCheckpointService
specifier|public
class|class
name|GlobalCheckpointService
extends|extends
name|AbstractIndexShardComponent
block|{
comment|/**      * This map holds the last known local checkpoint for every shard copy that's active.      * All shard copies in this map participate in determining the global checkpoint      * keyed by allocation ids      */
DECL|field|activeLocalCheckpoints
specifier|private
specifier|final
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|activeLocalCheckpoints
decl_stmt|;
comment|/**      * This map holds the last known local checkpoint for every initializing shard copy that's has been brought up      * to speed through recovery. These shards are treated as valid copies and participate in determining the global      * checkpoint.      *<p>      * Keyed by allocation ids.      */
DECL|field|inSyncLocalCheckpoints
specifier|private
specifier|final
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|inSyncLocalCheckpoints
decl_stmt|;
comment|// keyed by allocation ids
comment|/**      * This map holds the last known local checkpoint for every initializing shard copy that is still undergoing recovery.      * These shards<strong>do not</strong> participate in determining the global checkpoint. This map is needed to make sure that when      * shards are promoted to {@link #inSyncLocalCheckpoints} we use the highest known checkpoint, even if we index concurrently      * while recovering the shard.      * Keyed by allocation ids      */
DECL|field|trackingLocalCheckpoint
specifier|private
specifier|final
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|trackingLocalCheckpoint
decl_stmt|;
DECL|field|globalCheckpoint
specifier|private
name|long
name|globalCheckpoint
decl_stmt|;
comment|/**      * Initialize the global checkpoint service. The {@code globalCheckpoint}      * should be set to the last known global checkpoint for this shard, or      * {@link SequenceNumbersService#NO_OPS_PERFORMED}.      *      * @param shardId          the shard this service is providing tracking      *                         local checkpoints for      * @param indexSettings    the index settings      * @param globalCheckpoint the last known global checkpoint for this shard,      *                         or      *                         {@link SequenceNumbersService#UNASSIGNED_SEQ_NO}      */
DECL|method|GlobalCheckpointService
name|GlobalCheckpointService
parameter_list|(
specifier|final
name|ShardId
name|shardId
parameter_list|,
specifier|final
name|IndexSettings
name|indexSettings
parameter_list|,
specifier|final
name|long
name|globalCheckpoint
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|activeLocalCheckpoints
operator|=
operator|new
name|ObjectLongHashMap
argument_list|<>
argument_list|(
literal|1
operator|+
name|indexSettings
operator|.
name|getNumberOfReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|inSyncLocalCheckpoints
operator|=
operator|new
name|ObjectLongHashMap
argument_list|<>
argument_list|(
name|indexSettings
operator|.
name|getNumberOfReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|trackingLocalCheckpoint
operator|=
operator|new
name|ObjectLongHashMap
argument_list|<>
argument_list|(
name|indexSettings
operator|.
name|getNumberOfReplicas
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|globalCheckpoint
operator|=
name|globalCheckpoint
expr_stmt|;
block|}
comment|/**      * notifies the service of a local checkpoint. if the checkpoint is lower than the currently known one,      * this is a noop. Last, if the allocation id is not yet known, it is ignored. This to prevent late      * arrivals from shards that are removed to be re-added.      */
DECL|method|updateLocalCheckpoint
specifier|public
specifier|synchronized
name|void
name|updateLocalCheckpoint
parameter_list|(
name|String
name|allocationId
parameter_list|,
name|long
name|localCheckpoint
parameter_list|)
block|{
if|if
condition|(
name|updateLocalCheckpointInMap
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|activeLocalCheckpoints
argument_list|,
literal|"active"
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|updateLocalCheckpointInMap
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|inSyncLocalCheckpoints
argument_list|,
literal|"inSync"
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|updateLocalCheckpointInMap
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|trackingLocalCheckpoint
argument_list|,
literal|"tracking"
argument_list|)
condition|)
block|{
return|return;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"local checkpoint of [{}] ([{}]) wasn't found in any map. ignoring."
argument_list|,
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
block|}
DECL|method|updateLocalCheckpointInMap
specifier|private
name|boolean
name|updateLocalCheckpointInMap
parameter_list|(
name|String
name|allocationId
parameter_list|,
name|long
name|localCheckpoint
parameter_list|,
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|checkpointsMap
parameter_list|,
name|String
name|name
parameter_list|)
block|{
assert|assert
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
assert|;
name|int
name|indexOfKey
init|=
name|checkpointsMap
operator|.
name|indexOf
argument_list|(
name|allocationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfKey
operator|<
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|long
name|current
init|=
name|checkpointsMap
operator|.
name|indexGet
argument_list|(
name|indexOfKey
argument_list|)
decl_stmt|;
comment|// nocommit: this can change when we introduces rollback/resync
if|if
condition|(
name|current
operator|<
name|localCheckpoint
condition|)
block|{
name|checkpointsMap
operator|.
name|indexReplace
argument_list|(
name|indexOfKey
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
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
literal|"updated local checkpoint of [{}] to [{}] (type [{}])"
argument_list|,
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"skipping update local checkpoint [{}], current check point is higher "
operator|+
literal|"(current [{}], incoming [{}], type [{}])"
argument_list|,
name|allocationId
argument_list|,
name|current
argument_list|,
name|localCheckpoint
argument_list|,
name|allocationId
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Scans through the currently known local checkpoints and updates the global checkpoint accordingly.      *      * @return true if the checkpoint has been updated or if it can not be updated since one of the local checkpoints      * of one of the active allocations is not known.      */
DECL|method|updateCheckpointOnPrimary
specifier|synchronized
name|boolean
name|updateCheckpointOnPrimary
parameter_list|()
block|{
name|long
name|minCheckpoint
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|activeLocalCheckpoints
operator|.
name|isEmpty
argument_list|()
operator|&&
name|inSyncLocalCheckpoints
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|ObjectLongCursor
argument_list|<
name|String
argument_list|>
name|cp
range|:
name|activeLocalCheckpoints
control|)
block|{
if|if
condition|(
name|cp
operator|.
name|value
operator|==
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"unknown local checkpoint for active allocationId [{}], requesting a sync"
argument_list|,
name|cp
operator|.
name|key
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|minCheckpoint
operator|=
name|Math
operator|.
name|min
argument_list|(
name|cp
operator|.
name|value
argument_list|,
name|minCheckpoint
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ObjectLongCursor
argument_list|<
name|String
argument_list|>
name|cp
range|:
name|inSyncLocalCheckpoints
control|)
block|{
assert|assert
name|cp
operator|.
name|value
operator|!=
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
operator|:
literal|"in sync allocation ids can not have an unknown checkpoint (aId ["
operator|+
name|cp
operator|.
name|key
operator|+
literal|"])"
assert|;
name|minCheckpoint
operator|=
name|Math
operator|.
name|min
argument_list|(
name|cp
operator|.
name|value
argument_list|,
name|minCheckpoint
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minCheckpoint
operator|<
name|globalCheckpoint
condition|)
block|{
comment|// nocommit: if this happens - do you we fail the shard?
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|shardId
operator|+
literal|" new global checkpoint ["
operator|+
name|minCheckpoint
operator|+
literal|"] is lower than previous one ["
operator|+
name|globalCheckpoint
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|globalCheckpoint
operator|!=
name|minCheckpoint
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"global checkpoint updated to [{}]"
argument_list|,
name|minCheckpoint
argument_list|)
expr_stmt|;
name|globalCheckpoint
operator|=
name|minCheckpoint
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      * gets the current global checkpoint. See java docs for {@link GlobalCheckpointService} for more details      */
DECL|method|getCheckpoint
specifier|public
specifier|synchronized
name|long
name|getCheckpoint
parameter_list|()
block|{
return|return
name|globalCheckpoint
return|;
block|}
comment|/**      * updates the global checkpoint on a replica shard (after it has been updated by the primary).      */
DECL|method|updateCheckpointOnReplica
specifier|synchronized
name|void
name|updateCheckpointOnReplica
parameter_list|(
name|long
name|globalCheckpoint
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|globalCheckpoint
operator|<=
name|globalCheckpoint
condition|)
block|{
name|this
operator|.
name|globalCheckpoint
operator|=
name|globalCheckpoint
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"global checkpoint updated from primary to [{}]"
argument_list|,
name|globalCheckpoint
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// nocommit: fail the shard?
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"global checkpoint from primary should never decrease. current ["
operator|+
name|this
operator|.
name|globalCheckpoint
operator|+
literal|"], got ["
operator|+
name|globalCheckpoint
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Notifies the service of the current allocation ids in the cluster state. This method trims any shards that      * have been removed and adds/promotes any active allocations to the {@link #activeLocalCheckpoints}.      *      * @param activeAllocationIds       the allocation ids of the currently active shard copies      * @param initializingAllocationIds the allocation ids of the currently initializing shard copies      */
DECL|method|updateAllocationIdsFromMaster
specifier|public
specifier|synchronized
name|void
name|updateAllocationIdsFromMaster
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|activeAllocationIds
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|initializingAllocationIds
parameter_list|)
block|{
name|activeLocalCheckpoints
operator|.
name|removeAll
argument_list|(
name|key
lambda|->
name|activeAllocationIds
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|activeId
range|:
name|activeAllocationIds
control|)
block|{
if|if
condition|(
name|activeLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|activeId
argument_list|)
operator|==
literal|false
condition|)
block|{
name|long
name|knownCheckpoint
init|=
name|trackingLocalCheckpoint
operator|.
name|getOrDefault
argument_list|(
name|activeId
argument_list|,
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|)
decl_stmt|;
name|knownCheckpoint
operator|=
name|inSyncLocalCheckpoints
operator|.
name|getOrDefault
argument_list|(
name|activeId
argument_list|,
name|knownCheckpoint
argument_list|)
expr_stmt|;
name|activeLocalCheckpoints
operator|.
name|put
argument_list|(
name|activeId
argument_list|,
name|knownCheckpoint
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"marking [{}] as active. known checkpoint [{}]"
argument_list|,
name|activeId
argument_list|,
name|knownCheckpoint
argument_list|)
expr_stmt|;
block|}
block|}
name|inSyncLocalCheckpoints
operator|.
name|removeAll
argument_list|(
name|key
lambda|->
name|initializingAllocationIds
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
name|trackingLocalCheckpoint
operator|.
name|removeAll
argument_list|(
name|key
lambda|->
name|initializingAllocationIds
operator|.
name|contains
argument_list|(
name|key
argument_list|)
operator|==
literal|false
argument_list|)
expr_stmt|;
comment|// add initializing shards to tracking
for|for
control|(
name|String
name|initID
range|:
name|initializingAllocationIds
control|)
block|{
if|if
condition|(
name|inSyncLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|initID
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|trackingLocalCheckpoint
operator|.
name|containsKey
argument_list|(
name|initID
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|trackingLocalCheckpoint
operator|.
name|put
argument_list|(
name|initID
argument_list|,
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"added [{}] to the tracking map due to a CS update"
argument_list|,
name|initID
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * marks the allocationId as "in sync" with the primary shard. This should be called at the end of recovery      * where the primary knows all operation bellow the global checkpoint have been completed on this shard.      *      * @param allocationId    allocationId of the recovering shard      * @param localCheckpoint the local checkpoint of the shard in question      */
DECL|method|markAllocationIdAsInSync
specifier|public
specifier|synchronized
name|void
name|markAllocationIdAsInSync
parameter_list|(
name|String
name|allocationId
parameter_list|,
name|long
name|localCheckpoint
parameter_list|)
block|{
if|if
condition|(
name|trackingLocalCheckpoint
operator|.
name|containsKey
argument_list|(
name|allocationId
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// master have change its mind and removed this allocation, ignore.
return|return;
block|}
name|long
name|current
init|=
name|trackingLocalCheckpoint
operator|.
name|remove
argument_list|(
name|allocationId
argument_list|)
decl_stmt|;
name|localCheckpoint
operator|=
name|Math
operator|.
name|max
argument_list|(
name|current
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"marked [{}] as in sync with a local checkpoint of [{}]"
argument_list|,
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
name|inSyncLocalCheckpoints
operator|.
name|put
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
block|}
comment|// for testing
DECL|method|getLocalCheckpointForAllocation
specifier|synchronized
name|long
name|getLocalCheckpointForAllocation
parameter_list|(
name|String
name|allocationId
parameter_list|)
block|{
if|if
condition|(
name|activeLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|allocationId
argument_list|)
condition|)
block|{
return|return
name|activeLocalCheckpoints
operator|.
name|get
argument_list|(
name|allocationId
argument_list|)
return|;
block|}
if|if
condition|(
name|inSyncLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|allocationId
argument_list|)
condition|)
block|{
return|return
name|inSyncLocalCheckpoints
operator|.
name|get
argument_list|(
name|allocationId
argument_list|)
return|;
block|}
if|if
condition|(
name|trackingLocalCheckpoint
operator|.
name|containsKey
argument_list|(
name|allocationId
argument_list|)
condition|)
block|{
return|return
name|trackingLocalCheckpoint
operator|.
name|get
argument_list|(
name|allocationId
argument_list|)
return|;
block|}
return|return
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
return|;
block|}
block|}
end_class

end_unit

