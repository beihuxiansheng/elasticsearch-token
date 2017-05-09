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
name|common
operator|.
name|SuppressForbidden
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
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * This class is responsible of tracking the global checkpoint. The global checkpoint is the highest sequence number for which all lower (or  * equal) sequence number have been processed on all shards that are currently active. Since shards count as "active" when the master starts  * them, and before this primary shard has been notified of this fact, we also include shards that have completed recovery. These shards  * have received all old operations via the recovery mechanism and are kept up to date by the various replications actions. The set of  * shards that are taken into account for the global checkpoint calculation are called the "in-sync shards".  *<p>  * The global checkpoint is maintained by the primary shard and is replicated to all the replicas (via {@link GlobalCheckpointSyncAction}).  */
end_comment

begin_class
DECL|class|GlobalCheckpointTracker
specifier|public
class|class
name|GlobalCheckpointTracker
extends|extends
name|AbstractIndexShardComponent
block|{
comment|/*      * This map holds the last known local checkpoint for every active shard and initializing shard copies that has been brought up to speed      * through recovery. These shards are treated as valid copies and participate in determining the global checkpoint. This map is keyed by      * allocation IDs. All accesses to this set are guarded by a lock on this.      */
DECL|field|inSyncLocalCheckpoints
specifier|final
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|inSyncLocalCheckpoints
decl_stmt|;
comment|/*      * This map holds the last known local checkpoint for initializing shards that are undergoing recovery. Such shards do not participate      * in determining the global checkpoint. We must track these local checkpoints so that when a shard is activated we use the highest      * known checkpoint.      */
DECL|field|trackingLocalCheckpoints
specifier|final
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|trackingLocalCheckpoints
decl_stmt|;
comment|/*      * This set contains allocation IDs for which there is a thread actively waiting for the local checkpoint to advance to at least the      * current global checkpoint.      */
DECL|field|pendingInSync
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|pendingInSync
decl_stmt|;
comment|/*      * The current global checkpoint for this shard. Note that this field is guarded by a lock on this and thus this field does not need to      * be volatile.      */
DECL|field|globalCheckpoint
specifier|private
name|long
name|globalCheckpoint
decl_stmt|;
comment|/**      * Initialize the global checkpoint service. The specified global checkpoint should be set to the last known global checkpoint, or      * {@link SequenceNumbersService#UNASSIGNED_SEQ_NO}.      *      * @param shardId          the shard ID      * @param indexSettings    the index settings      * @param globalCheckpoint the last known global checkpoint for this shard, or {@link SequenceNumbersService#UNASSIGNED_SEQ_NO}      */
DECL|method|GlobalCheckpointTracker
name|GlobalCheckpointTracker
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
assert|assert
name|globalCheckpoint
operator|>=
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
operator|:
literal|"illegal initial global checkpoint: "
operator|+
name|globalCheckpoint
assert|;
name|this
operator|.
name|inSyncLocalCheckpoints
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
name|this
operator|.
name|trackingLocalCheckpoints
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
name|this
operator|.
name|pendingInSync
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Notifies the service to update the local checkpoint for the shard with the provided allocation ID. If the checkpoint is lower than      * the currently known one, this is a no-op. If the allocation ID is not tracked, it is ignored. This is to prevent late arrivals from      * shards that are removed to be re-added.      *      * @param allocationId    the allocation ID of the shard to update the local checkpoint for      * @param localCheckpoint the local checkpoint for the shard      */
DECL|method|updateLocalCheckpoint
specifier|public
specifier|synchronized
name|void
name|updateLocalCheckpoint
parameter_list|(
specifier|final
name|String
name|allocationId
parameter_list|,
specifier|final
name|long
name|localCheckpoint
parameter_list|)
block|{
specifier|final
name|boolean
name|updated
decl_stmt|;
if|if
condition|(
name|updateLocalCheckpoint
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|inSyncLocalCheckpoints
argument_list|,
literal|"in-sync"
argument_list|)
condition|)
block|{
name|updated
operator|=
literal|true
expr_stmt|;
name|updateGlobalCheckpointOnPrimary
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|updateLocalCheckpoint
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|trackingLocalCheckpoints
argument_list|,
literal|"tracking"
argument_list|)
condition|)
block|{
name|updated
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"ignored local checkpoint [{}] of [{}], allocation ID is not tracked"
argument_list|,
name|localCheckpoint
argument_list|,
name|allocationId
argument_list|)
expr_stmt|;
name|updated
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|updated
condition|)
block|{
name|notifyAllWaiters
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Notify all threads waiting on the monitor on this tracker. These threads should be waiting for the local checkpoint on a specific      * allocation ID to catch up to the global checkpoint.      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Object#notifyAll waiters for local checkpoint advancement"
argument_list|)
DECL|method|notifyAllWaiters
specifier|private
specifier|synchronized
name|void
name|notifyAllWaiters
parameter_list|()
block|{
name|this
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/**      * Update the local checkpoint for the specified allocation ID in the specified tracking map. If the checkpoint is lower than the      * currently known one, this is a no-op. If the allocation ID is not tracked, it is ignored.      *      * @param allocationId the allocation ID of the shard to update the local checkpoint for      * @param localCheckpoint the local checkpoint for the shard      * @param map the tracking map      * @param reason the reason for the update (used for logging)      * @return {@code true} if the local checkpoint was updated, otherwise {@code false} if this was a no-op      */
DECL|method|updateLocalCheckpoint
specifier|private
name|boolean
name|updateLocalCheckpoint
parameter_list|(
specifier|final
name|String
name|allocationId
parameter_list|,
specifier|final
name|long
name|localCheckpoint
parameter_list|,
name|ObjectLongMap
argument_list|<
name|String
argument_list|>
name|map
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|map
operator|.
name|indexOf
argument_list|(
name|allocationId
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
specifier|final
name|long
name|current
init|=
name|map
operator|.
name|indexGet
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|<
name|localCheckpoint
condition|)
block|{
name|map
operator|.
name|indexReplace
argument_list|(
name|index
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"updated local checkpoint of [{}] in [{}] from [{}] to [{}]"
argument_list|,
name|allocationId
argument_list|,
name|reason
argument_list|,
name|current
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"skipped updating local checkpoint of [{}] in [{}] from [{}] to [{}], current checkpoint is higher"
argument_list|,
name|allocationId
argument_list|,
name|reason
argument_list|,
name|current
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * Scans through the currently known local checkpoint and updates the global checkpoint accordingly.      */
DECL|method|updateGlobalCheckpointOnPrimary
specifier|private
specifier|synchronized
name|void
name|updateGlobalCheckpointOnPrimary
parameter_list|()
block|{
name|long
name|minLocalCheckpoint
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|inSyncLocalCheckpoints
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|pendingInSync
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
specifier|final
name|ObjectLongCursor
argument_list|<
name|String
argument_list|>
name|localCheckpoint
range|:
name|inSyncLocalCheckpoints
control|)
block|{
if|if
condition|(
name|localCheckpoint
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
literal|"unknown local checkpoint for active allocation ID [{}], requesting a sync"
argument_list|,
name|localCheckpoint
operator|.
name|key
argument_list|)
expr_stmt|;
return|return;
block|}
name|minLocalCheckpoint
operator|=
name|Math
operator|.
name|min
argument_list|(
name|localCheckpoint
operator|.
name|value
argument_list|,
name|minLocalCheckpoint
argument_list|)
expr_stmt|;
block|}
assert|assert
name|minLocalCheckpoint
operator|!=
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
operator|:
literal|"new global checkpoint must be assigned"
assert|;
if|if
condition|(
name|minLocalCheckpoint
operator|<
name|globalCheckpoint
condition|)
block|{
specifier|final
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
literal|"new global checkpoint [%d] is lower than previous one [%d]"
argument_list|,
name|minLocalCheckpoint
argument_list|,
name|globalCheckpoint
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
if|if
condition|(
name|globalCheckpoint
operator|!=
name|minLocalCheckpoint
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"global checkpoint updated to [{}]"
argument_list|,
name|minLocalCheckpoint
argument_list|)
expr_stmt|;
name|globalCheckpoint
operator|=
name|minLocalCheckpoint
expr_stmt|;
block|}
block|}
comment|/**      * Returns the global checkpoint for the shard.      *      * @return the global checkpoint      */
DECL|method|getGlobalCheckpoint
specifier|public
specifier|synchronized
name|long
name|getGlobalCheckpoint
parameter_list|()
block|{
return|return
name|globalCheckpoint
return|;
block|}
comment|/**      * Updates the global checkpoint on a replica shard after it has been updated by the primary.      *      * @param globalCheckpoint the global checkpoint      */
DECL|method|updateGlobalCheckpointOnReplica
specifier|synchronized
name|void
name|updateGlobalCheckpointOnReplica
parameter_list|(
specifier|final
name|long
name|globalCheckpoint
parameter_list|)
block|{
comment|/*          * The global checkpoint here is a local knowledge which is updated under the mandate of the primary. It can happen that the primary          * information is lagging compared to a replica (e.g., if a replica is promoted to primary but has stale info relative to other          * replica shards). In these cases, the local knowledge of the global checkpoint could be higher than sync from the lagging primary.          */
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
block|}
comment|/**      * Notifies the service of the current allocation ids in the cluster state. This method trims any shards that have been removed.      *      * @param activeAllocationIds       the allocation IDs of the currently active shard copies      * @param initializingAllocationIds the allocation IDs of the currently initializing shard copies      */
DECL|method|updateAllocationIdsFromMaster
specifier|public
specifier|synchronized
name|void
name|updateAllocationIdsFromMaster
parameter_list|(
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|activeAllocationIds
parameter_list|,
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|initializingAllocationIds
parameter_list|)
block|{
comment|// remove shards whose allocation ID no longer exists
name|inSyncLocalCheckpoints
operator|.
name|removeAll
argument_list|(
name|a
lambda|->
operator|!
name|activeAllocationIds
operator|.
name|contains
argument_list|(
name|a
argument_list|)
operator|&&
operator|!
name|initializingAllocationIds
operator|.
name|contains
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
comment|// add any new active allocation IDs
for|for
control|(
specifier|final
name|String
name|a
range|:
name|activeAllocationIds
control|)
block|{
if|if
condition|(
operator|!
name|inSyncLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|a
argument_list|)
condition|)
block|{
specifier|final
name|long
name|localCheckpoint
init|=
name|trackingLocalCheckpoints
operator|.
name|getOrDefault
argument_list|(
name|a
argument_list|,
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|)
decl_stmt|;
name|inSyncLocalCheckpoints
operator|.
name|put
argument_list|(
name|a
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"marked [{}] as in-sync with local checkpoint [{}] via cluster state update from master"
argument_list|,
name|a
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
block|}
block|}
name|trackingLocalCheckpoints
operator|.
name|removeAll
argument_list|(
name|a
lambda|->
operator|!
name|initializingAllocationIds
operator|.
name|contains
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|a
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
name|a
argument_list|)
condition|)
block|{
comment|/*                  * This can happen if we mark the allocation ID as in sync at the end of recovery before seeing a cluster state update from                  * marking the shard as active.                  */
continue|continue;
block|}
if|if
condition|(
name|trackingLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|a
argument_list|)
condition|)
block|{
comment|// we are already tracking this allocation ID
continue|continue;
block|}
comment|// this is a new allocation ID
name|trackingLocalCheckpoints
operator|.
name|put
argument_list|(
name|a
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
literal|"tracking [{}] via cluster state update from master"
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
name|updateGlobalCheckpointOnPrimary
argument_list|()
expr_stmt|;
block|}
comment|/**      * Marks the shard with the provided allocation ID as in-sync with the primary shard. This method will block until the local checkpoint      * on the specified shard advances above the current global checkpoint.      *      * @param allocationId    the allocation ID of the shard to mark as in-sync      * @param localCheckpoint the current local checkpoint on the shard      *      * @throws InterruptedException if the thread is interrupted waiting for the local checkpoint on the shard to advance      */
DECL|method|markAllocationIdAsInSync
specifier|public
specifier|synchronized
name|void
name|markAllocationIdAsInSync
parameter_list|(
specifier|final
name|String
name|allocationId
parameter_list|,
specifier|final
name|long
name|localCheckpoint
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|trackingLocalCheckpoints
operator|.
name|containsKey
argument_list|(
name|allocationId
argument_list|)
condition|)
block|{
comment|/*              * This can happen if the recovery target has been failed and the cluster state update from the master has triggered removing              * this allocation ID from the tracking map but this recovery thread has not yet been made aware that the recovery is              * cancelled.              */
return|return;
block|}
name|updateLocalCheckpoint
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|,
name|trackingLocalCheckpoints
argument_list|,
literal|"tracking"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|pendingInSync
operator|.
name|add
argument_list|(
name|allocationId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"there is already a pending sync in progress for allocation ID ["
operator|+
name|allocationId
operator|+
literal|"]"
argument_list|)
throw|;
block|}
try|try
block|{
name|waitForAllocationIdToBeInSync
argument_list|(
name|allocationId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pendingInSync
operator|.
name|remove
argument_list|(
name|allocationId
argument_list|)
expr_stmt|;
name|updateGlobalCheckpointOnPrimary
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Wait for knowledge of the local checkpoint for the specified allocation ID to advance to the global checkpoint. Global checkpoint      * advancement is blocked while there are any allocation IDs waiting to catch up to the global checkpoint.      *      * @param allocationId the allocation ID      * @throws InterruptedException if this thread was interrupted before of during waiting      */
DECL|method|waitForAllocationIdToBeInSync
specifier|private
specifier|synchronized
name|void
name|waitForAllocationIdToBeInSync
parameter_list|(
specifier|final
name|String
name|allocationId
parameter_list|)
throws|throws
name|InterruptedException
block|{
while|while
condition|(
literal|true
condition|)
block|{
comment|/*              * If the allocation has been cancelled and so removed from the tracking map from a cluster state update from the master it              * means that this recovery will be cancelled; we are here on a cancellable recovery thread and so this thread will throw an              * interrupted exception as soon as it tries to wait on the monitor.              */
specifier|final
name|long
name|current
init|=
name|trackingLocalCheckpoints
operator|.
name|getOrDefault
argument_list|(
name|allocationId
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|current
operator|>=
name|globalCheckpoint
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"marked [{}] as in-sync with local checkpoint [{}]"
argument_list|,
name|allocationId
argument_list|,
name|current
argument_list|)
expr_stmt|;
name|trackingLocalCheckpoints
operator|.
name|remove
argument_list|(
name|allocationId
argument_list|)
expr_stmt|;
comment|/*                  * This is prematurely adding the allocation ID to the in-sync map as at this point recovery is not yet finished and could                  * still abort. At this point we will end up with a shard in the in-sync map holding back the global checkpoint because the                  * shard never recovered and we would have to wait until either the recovery retries and completes successfully, or the                  * master fails the shard and issues a cluster state update that removes the shard from the set of active allocation IDs.                  */
name|inSyncLocalCheckpoints
operator|.
name|put
argument_list|(
name|allocationId
argument_list|,
name|current
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|waitForLocalCheckpointToAdvance
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Wait for the local checkpoint to advance to the global checkpoint.      *      * @throws InterruptedException if this thread was interrupted before of during waiting      */
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"Object#wait for local checkpoint advancement"
argument_list|)
DECL|method|waitForLocalCheckpointToAdvance
specifier|private
specifier|synchronized
name|void
name|waitForLocalCheckpointToAdvance
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|this
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
comment|/**      * Check if there are any recoveries pending in-sync.      *      * @return {@code true} if there is at least one shard pending in-sync, otherwise false      */
DECL|method|pendingInSync
specifier|public
name|boolean
name|pendingInSync
parameter_list|()
block|{
return|return
operator|!
name|pendingInSync
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**      * Returns the local checkpoint for the shard with the specified allocation ID, or {@link SequenceNumbersService#UNASSIGNED_SEQ_NO} if      * the shard is not in-sync.      *      * @param allocationId the allocation ID of the shard to obtain the local checkpoint for      * @return the local checkpoint, or {@link SequenceNumbersService#UNASSIGNED_SEQ_NO}      */
DECL|method|getLocalCheckpointForAllocationId
specifier|synchronized
name|long
name|getLocalCheckpointForAllocationId
parameter_list|(
specifier|final
name|String
name|allocationId
parameter_list|)
block|{
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
return|return
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
return|;
block|}
block|}
end_class

end_unit

