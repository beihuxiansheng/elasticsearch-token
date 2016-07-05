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
comment|/**  * a very light weight implementation. will be replaced with proper machinery later  */
end_comment

begin_class
DECL|class|SequenceNumbersService
specifier|public
class|class
name|SequenceNumbersService
extends|extends
name|AbstractIndexShardComponent
block|{
DECL|field|UNASSIGNED_SEQ_NO
specifier|public
specifier|static
specifier|final
name|long
name|UNASSIGNED_SEQ_NO
init|=
operator|-
literal|2L
decl_stmt|;
comment|/**      * Represents no operations have been performed on the shard.      */
DECL|field|NO_OPS_PERFORMED
specifier|public
specifier|static
specifier|final
name|long
name|NO_OPS_PERFORMED
init|=
operator|-
literal|1L
decl_stmt|;
DECL|field|localCheckpointService
specifier|final
name|LocalCheckpointService
name|localCheckpointService
decl_stmt|;
DECL|field|globalCheckpointService
specifier|final
name|GlobalCheckpointService
name|globalCheckpointService
decl_stmt|;
comment|/**      * Initialize the sequence number service. The {@code maxSeqNo}      * should be set to the last sequence number assigned by this      * shard, or {@link SequenceNumbersService#NO_OPS_PERFORMED},      * {@code localCheckpoint} should be set to the last known local      * checkpoint for this shard, or      * {@link SequenceNumbersService#NO_OPS_PERFORMED}, and      * {@code globalCheckpoint} should be set to the last known global      * checkpoint for this shard, or      * {@link SequenceNumbersService#UNASSIGNED_SEQ_NO}.      *      * @param shardId          the shard this service is providing tracking      *                         local checkpoints for      * @param indexSettings    the index settings      * @param maxSeqNo         the last sequence number assigned by this      *                         shard, or      *                         {@link SequenceNumbersService#NO_OPS_PERFORMED}      * @param localCheckpoint  the last known local checkpoint for this shard,      *                         or {@link SequenceNumbersService#NO_OPS_PERFORMED}      * @param globalCheckpoint the last known global checkpoint for this shard,      *                         or {@link SequenceNumbersService#UNASSIGNED_SEQ_NO}      */
DECL|method|SequenceNumbersService
specifier|public
name|SequenceNumbersService
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
name|maxSeqNo
parameter_list|,
specifier|final
name|long
name|localCheckpoint
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
name|localCheckpointService
operator|=
operator|new
name|LocalCheckpointService
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|maxSeqNo
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
name|globalCheckpointService
operator|=
operator|new
name|GlobalCheckpointService
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|globalCheckpoint
argument_list|)
expr_stmt|;
block|}
comment|/**      * generates a new sequence number.      * Note: you must call {@link #markSeqNoAsCompleted(long)} after the operation for which this seq# was generated      * was completed (whether successfully or with a failure)      */
DECL|method|generateSeqNo
specifier|public
name|long
name|generateSeqNo
parameter_list|()
block|{
return|return
name|localCheckpointService
operator|.
name|generateSeqNo
argument_list|()
return|;
block|}
comment|/**      * marks the given seqNo as completed. See {@link LocalCheckpointService#markSeqNoAsCompleted(long)}      * more details      */
DECL|method|markSeqNoAsCompleted
specifier|public
name|void
name|markSeqNoAsCompleted
parameter_list|(
name|long
name|seqNo
parameter_list|)
block|{
name|localCheckpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets sequence number related stats      */
DECL|method|stats
specifier|public
name|SeqNoStats
name|stats
parameter_list|()
block|{
return|return
operator|new
name|SeqNoStats
argument_list|(
name|localCheckpointService
operator|.
name|getMaxSeqNo
argument_list|()
argument_list|,
name|localCheckpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|globalCheckpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * notifies the service of a local checkpoint.      * see {@link GlobalCheckpointService#updateLocalCheckpoint(String, long)} for details.      */
DECL|method|updateLocalCheckpointForShard
specifier|public
name|void
name|updateLocalCheckpointForShard
parameter_list|(
name|String
name|allocationId
parameter_list|,
name|long
name|checkpoint
parameter_list|)
block|{
name|globalCheckpointService
operator|.
name|updateLocalCheckpoint
argument_list|(
name|allocationId
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
comment|/**      * marks the allocationId as "in sync" with the primary shard.      * see {@link GlobalCheckpointService#markAllocationIdAsInSync(String, long)} for details.      *      * @param allocationId    allocationId of the recovering shard      * @param localCheckpoint the local checkpoint of the shard in question      */
DECL|method|markAllocationIdAsInSync
specifier|public
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
name|globalCheckpointService
operator|.
name|markAllocationIdAsInSync
argument_list|(
name|allocationId
argument_list|,
name|localCheckpoint
argument_list|)
expr_stmt|;
block|}
DECL|method|getLocalCheckpoint
specifier|public
name|long
name|getLocalCheckpoint
parameter_list|()
block|{
return|return
name|localCheckpointService
operator|.
name|getCheckpoint
argument_list|()
return|;
block|}
DECL|method|getGlobalCheckpoint
specifier|public
name|long
name|getGlobalCheckpoint
parameter_list|()
block|{
return|return
name|globalCheckpointService
operator|.
name|getCheckpoint
argument_list|()
return|;
block|}
comment|/**      * updates the global checkpoint on a replica shard (after it has been updated by the primary).      */
DECL|method|updateGlobalCheckpointOnReplica
specifier|public
name|void
name|updateGlobalCheckpointOnReplica
parameter_list|(
name|long
name|checkpoint
parameter_list|)
block|{
name|globalCheckpointService
operator|.
name|updateCheckpointOnReplica
argument_list|(
name|checkpoint
argument_list|)
expr_stmt|;
block|}
comment|/**      * Notifies the service of the current allocation ids in the cluster state.      * see {@link GlobalCheckpointService#updateAllocationIdsFromMaster(Set, Set)} for details.      *      * @param activeAllocationIds       the allocation ids of the currently active shard copies      * @param initializingAllocationIds the allocation ids of the currently initializing shard copies      */
DECL|method|updateAllocationIdsFromMaster
specifier|public
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
name|globalCheckpointService
operator|.
name|updateAllocationIdsFromMaster
argument_list|(
name|activeAllocationIds
argument_list|,
name|initializingAllocationIds
argument_list|)
expr_stmt|;
block|}
comment|/**      * Scans through the currently known local checkpoint and updates the global checkpoint accordingly.      *      * @return true if the checkpoint has been updated or if it can not be updated since one of the local checkpoints      * of one of the active allocations is not known.      */
DECL|method|updateGlobalCheckpointOnPrimary
specifier|public
name|boolean
name|updateGlobalCheckpointOnPrimary
parameter_list|()
block|{
return|return
name|globalCheckpointService
operator|.
name|updateCheckpointOnPrimary
argument_list|()
return|;
block|}
block|}
end_class

end_unit

