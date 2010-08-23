begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
package|;
end_package

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
name|CloseableIndexComponent
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
name|unit
operator|.
name|ByteSizeValue
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
name|deletionpolicy
operator|.
name|SnapshotIndexCommit
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
name|Engine
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
name|EngineException
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
name|SnapshotFailedEngineException
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
name|settings
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
name|*
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
name|index
operator|.
name|shard
operator|.
name|service
operator|.
name|InternalIndexShard
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
name|store
operator|.
name|Store
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
name|translog
operator|.
name|Translog
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ScheduledFuture
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexShardGatewayService
specifier|public
class|class
name|IndexShardGatewayService
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|CloseableIndexComponent
block|{
DECL|field|snapshotOnClose
specifier|private
specifier|final
name|boolean
name|snapshotOnClose
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|indexShard
specifier|private
specifier|final
name|InternalIndexShard
name|indexShard
decl_stmt|;
DECL|field|shardGateway
specifier|private
specifier|final
name|IndexShardGateway
name|shardGateway
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|lastIndexVersion
specifier|private
specifier|volatile
name|long
name|lastIndexVersion
decl_stmt|;
DECL|field|lastTranslogId
specifier|private
specifier|volatile
name|long
name|lastTranslogId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|lastTranslogPosition
specifier|private
specifier|volatile
name|long
name|lastTranslogPosition
decl_stmt|;
DECL|field|lastTotalTranslogOperations
specifier|private
specifier|volatile
name|int
name|lastTotalTranslogOperations
decl_stmt|;
DECL|field|lastTranslogLength
specifier|private
specifier|volatile
name|long
name|lastTranslogLength
decl_stmt|;
DECL|field|snapshotInterval
specifier|private
specifier|final
name|TimeValue
name|snapshotInterval
decl_stmt|;
DECL|field|snapshotScheduleFuture
specifier|private
specifier|volatile
name|ScheduledFuture
name|snapshotScheduleFuture
decl_stmt|;
DECL|field|recoveryStatus
specifier|private
name|RecoveryStatus
name|recoveryStatus
decl_stmt|;
DECL|method|IndexShardGatewayService
annotation|@
name|Inject
specifier|public
name|IndexShardGatewayService
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|IndexShard
name|indexShard
parameter_list|,
name|IndexShardGateway
name|shardGateway
parameter_list|,
name|Store
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|indexShard
operator|=
operator|(
name|InternalIndexShard
operator|)
name|indexShard
expr_stmt|;
name|this
operator|.
name|shardGateway
operator|=
name|shardGateway
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|snapshotOnClose
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"snapshot_on_close"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|snapshotInterval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"snapshot_interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Should be called when the shard routing state has changed (note, after the state has been set on the shard).      */
DECL|method|routingStateChanged
specifier|public
name|void
name|routingStateChanged
parameter_list|()
block|{
name|scheduleSnapshotIfNeeded
argument_list|()
expr_stmt|;
block|}
DECL|interface|RecoveryListener
specifier|public
specifier|static
interface|interface
name|RecoveryListener
block|{
DECL|method|onRecoveryDone
name|void
name|onRecoveryDone
parameter_list|()
function_decl|;
DECL|method|onIgnoreRecovery
name|void
name|onIgnoreRecovery
parameter_list|(
name|String
name|reason
parameter_list|)
function_decl|;
DECL|method|onRecoveryFailed
name|void
name|onRecoveryFailed
parameter_list|(
name|IndexShardGatewayRecoveryException
name|e
parameter_list|)
function_decl|;
block|}
DECL|method|recoveryStatus
specifier|public
name|RecoveryStatus
name|recoveryStatus
parameter_list|()
block|{
if|if
condition|(
name|recoveryStatus
operator|==
literal|null
condition|)
block|{
return|return
name|recoveryStatus
return|;
block|}
if|if
condition|(
name|recoveryStatus
operator|.
name|startTime
argument_list|()
operator|>
literal|0
operator|&&
name|recoveryStatus
operator|.
name|stage
argument_list|()
operator|!=
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|DONE
condition|)
block|{
name|recoveryStatus
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|recoveryStatus
return|;
block|}
DECL|method|snapshotStatus
specifier|public
name|SnapshotStatus
name|snapshotStatus
parameter_list|()
block|{
name|SnapshotStatus
name|snapshotStatus
init|=
name|shardGateway
operator|.
name|currentSnapshotStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|snapshotStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|snapshotStatus
return|;
block|}
return|return
name|shardGateway
operator|.
name|lastSnapshotStatus
argument_list|()
return|;
block|}
comment|/**      * Recovers the state of the shard from the gateway.      */
DECL|method|recover
specifier|public
name|void
name|recover
parameter_list|(
specifier|final
name|RecoveryListener
name|listener
parameter_list|)
throws|throws
name|IndexShardGatewayRecoveryException
throws|,
name|IgnoreGatewayRecoveryException
block|{
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|CLOSED
condition|)
block|{
comment|// got closed on us, just ignore this recovery
name|listener
operator|.
name|onIgnoreRecovery
argument_list|(
literal|"shard closed"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|primary
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onRecoveryFailed
argument_list|(
operator|new
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|,
literal|"Trying to recover when the shard is in backup state"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
name|indexShard
operator|.
name|recovering
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalIndexShardStateException
name|e
parameter_list|)
block|{
comment|// that's fine, since we might be called concurrently, just ignore this, we are already recovering
name|listener
operator|.
name|onIgnoreRecovery
argument_list|(
literal|"already in recovering process, "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|threadPool
operator|.
name|cached
argument_list|()
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|recoveryStatus
operator|=
operator|new
name|RecoveryStatus
argument_list|()
expr_stmt|;
name|recoveryStatus
operator|.
name|updateStage
argument_list|(
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|INIT
argument_list|)
expr_stmt|;
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"starting recovery from {} ..."
argument_list|,
name|shardGateway
argument_list|)
expr_stmt|;
name|shardGateway
operator|.
name|recover
argument_list|(
name|recoveryStatus
argument_list|)
expr_stmt|;
name|lastIndexVersion
operator|=
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|version
argument_list|()
expr_stmt|;
name|lastTranslogId
operator|=
operator|-
literal|1
expr_stmt|;
name|lastTranslogPosition
operator|=
literal|0
expr_stmt|;
name|lastTranslogLength
operator|=
literal|0
expr_stmt|;
name|lastTotalTranslogOperations
operator|=
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|currentTranslogOperations
argument_list|()
expr_stmt|;
comment|// start the shard if the gateway has not started it already
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|!=
name|IndexShardState
operator|.
name|STARTED
condition|)
block|{
name|indexShard
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// refresh the shard
name|indexShard
operator|.
name|refresh
argument_list|(
operator|new
name|Engine
operator|.
name|Refresh
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|updateStage
argument_list|(
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|DONE
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"recovery completed from "
argument_list|)
operator|.
name|append
argument_list|(
name|shardGateway
argument_list|)
operator|.
name|append
argument_list|(
literal|", took ["
argument_list|)
operator|.
name|append
argument_list|(
name|timeValueMillis
argument_list|(
name|recoveryStatus
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    index    : total_size ["
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|totalSize
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], took["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], took ["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"             : recovered_files ["
argument_list|)
operator|.
name|append
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|numberOfFiles
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with total_size ["
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|reusedTotalSize
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], took ["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"             : reusing_files   ["
argument_list|)
operator|.
name|append
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|numberOfReusedFiles
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with total_size ["
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|reusedTotalSize
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    translog : number_of_operations ["
argument_list|)
operator|.
name|append
argument_list|(
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|currentTranslogOperations
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"], took ["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onRecoveryDone
argument_list|()
expr_stmt|;
name|scheduleSnapshotIfNeeded
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexShardGatewayRecoveryException
name|e
parameter_list|)
block|{
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|CLOSED
condition|)
block|{
comment|// got closed on us, just ignore this recovery
name|listener
operator|.
name|onIgnoreRecovery
argument_list|(
literal|"shard closed"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IndexShardClosedException
operator|)
operator|||
operator|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IndexShardNotStartedException
operator|)
condition|)
block|{
comment|// got closed on us, just ignore this recovery
name|listener
operator|.
name|onIgnoreRecovery
argument_list|(
literal|"shard closed"
argument_list|)
expr_stmt|;
return|return;
block|}
name|listener
operator|.
name|onRecoveryFailed
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexShardClosedException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onIgnoreRecovery
argument_list|(
literal|"shard closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexShardNotStartedException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onIgnoreRecovery
argument_list|(
literal|"shard closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onRecoveryFailed
argument_list|(
operator|new
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|,
literal|"failed recovery"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**      * Snapshots the given shard into the gateway.      */
DECL|method|snapshot
specifier|public
specifier|synchronized
name|void
name|snapshot
parameter_list|(
specifier|final
name|String
name|reason
parameter_list|)
throws|throws
name|IndexShardGatewaySnapshotFailedException
block|{
if|if
condition|(
operator|!
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|primary
argument_list|()
condition|)
block|{
return|return;
comment|//            throw new IndexShardGatewaySnapshotNotAllowedException(shardId, "Snapshot not allowed on non primary shard");
block|}
if|if
condition|(
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|relocating
argument_list|()
condition|)
block|{
comment|// do not snapshot when in the process of relocation of primaries so we won't get conflicts
return|return;
block|}
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|CREATED
condition|)
block|{
comment|// shard has just been created, ignore it and return
return|return;
block|}
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|RECOVERING
condition|)
block|{
comment|// shard is recovering, don't snapshot
return|return;
block|}
try|try
block|{
name|SnapshotStatus
name|snapshotStatus
init|=
name|indexShard
operator|.
name|snapshot
argument_list|(
operator|new
name|Engine
operator|.
name|SnapshotHandler
argument_list|<
name|SnapshotStatus
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|SnapshotStatus
name|snapshot
parameter_list|(
name|SnapshotIndexCommit
name|snapshotIndexCommit
parameter_list|,
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
parameter_list|)
throws|throws
name|EngineException
block|{
if|if
condition|(
name|lastIndexVersion
operator|!=
name|snapshotIndexCommit
operator|.
name|getVersion
argument_list|()
operator|||
name|lastTranslogId
operator|!=
name|translogSnapshot
operator|.
name|translogId
argument_list|()
operator|||
name|lastTranslogLength
operator|<
name|translogSnapshot
operator|.
name|length
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"snapshot ({}) to {} ..."
argument_list|,
name|reason
argument_list|,
name|shardGateway
argument_list|)
expr_stmt|;
name|SnapshotStatus
name|snapshotStatus
init|=
name|shardGateway
operator|.
name|snapshot
argument_list|(
operator|new
name|IndexShardGateway
operator|.
name|Snapshot
argument_list|(
name|snapshotIndexCommit
argument_list|,
name|translogSnapshot
argument_list|,
name|lastIndexVersion
argument_list|,
name|lastTranslogId
argument_list|,
name|lastTranslogPosition
argument_list|,
name|lastTranslogLength
argument_list|,
name|lastTotalTranslogOperations
argument_list|)
argument_list|)
decl_stmt|;
name|lastIndexVersion
operator|=
name|snapshotIndexCommit
operator|.
name|getVersion
argument_list|()
expr_stmt|;
name|lastTranslogId
operator|=
name|translogSnapshot
operator|.
name|translogId
argument_list|()
expr_stmt|;
name|lastTranslogPosition
operator|=
name|translogSnapshot
operator|.
name|position
argument_list|()
expr_stmt|;
name|lastTranslogLength
operator|=
name|translogSnapshot
operator|.
name|length
argument_list|()
expr_stmt|;
name|lastTotalTranslogOperations
operator|=
name|translogSnapshot
operator|.
name|totalOperations
argument_list|()
expr_stmt|;
return|return
name|snapshotStatus
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|snapshotStatus
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"snapshot ("
argument_list|)
operator|.
name|append
argument_list|(
name|reason
argument_list|)
operator|.
name|append
argument_list|(
literal|") completed to "
argument_list|)
operator|.
name|append
argument_list|(
name|shardGateway
argument_list|)
operator|.
name|append
argument_list|(
literal|", took ["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|snapshotStatus
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    index    : version ["
argument_list|)
operator|.
name|append
argument_list|(
name|lastIndexVersion
argument_list|)
operator|.
name|append
argument_list|(
literal|"], number_of_files ["
argument_list|)
operator|.
name|append
argument_list|(
name|snapshotStatus
operator|.
name|index
argument_list|()
operator|.
name|numberOfFiles
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with total_size ["
argument_list|)
operator|.
name|append
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
name|snapshotStatus
operator|.
name|index
argument_list|()
operator|.
name|totalSize
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"], took ["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|snapshotStatus
operator|.
name|index
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"    translog : id      ["
argument_list|)
operator|.
name|append
argument_list|(
name|lastTranslogId
argument_list|)
operator|.
name|append
argument_list|(
literal|"], number_of_operations ["
operator|+
name|snapshotStatus
operator|.
name|translog
argument_list|()
operator|.
name|expectedNumberOfOperations
argument_list|()
operator|+
literal|"], took ["
argument_list|)
operator|.
name|append
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|snapshotStatus
operator|.
name|translog
argument_list|()
operator|.
name|time
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SnapshotFailedEngineException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IllegalStateException
condition|)
block|{
comment|// ignore, that's fine, snapshot has not started yet
block|}
else|else
block|{
throw|throw
operator|new
name|IndexShardGatewaySnapshotFailedException
argument_list|(
name|shardId
argument_list|,
literal|"Failed to snapshot"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalIndexShardStateException
name|e
parameter_list|)
block|{
comment|// ignore, that's fine, snapshot has not started yet
block|}
catch|catch
parameter_list|(
name|IndexShardGatewaySnapshotFailedException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IndexShardGatewaySnapshotFailedException
argument_list|(
name|shardId
argument_list|,
literal|"Failed to snapshot"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|snapshotOnClose
specifier|public
name|void
name|snapshotOnClose
parameter_list|()
block|{
if|if
condition|(
name|snapshotOnClose
condition|)
block|{
try|try
block|{
name|snapshot
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to snapshot on close"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
if|if
condition|(
name|snapshotScheduleFuture
operator|!=
literal|null
condition|)
block|{
name|snapshotScheduleFuture
operator|.
name|cancel
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|snapshotScheduleFuture
operator|=
literal|null
expr_stmt|;
block|}
comment|// don't really delete the shard gateway if we are *not* primary,
comment|// the primary will close it
if|if
condition|(
operator|!
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|primary
argument_list|()
condition|)
block|{
name|delete
operator|=
literal|false
expr_stmt|;
block|}
name|shardGateway
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
DECL|method|scheduleSnapshotIfNeeded
specifier|private
specifier|synchronized
name|void
name|scheduleSnapshotIfNeeded
parameter_list|()
block|{
if|if
condition|(
operator|!
name|shardGateway
operator|.
name|requiresSnapshotScheduling
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|primary
argument_list|()
condition|)
block|{
comment|// we only do snapshotting on the primary shard
return|return;
block|}
if|if
condition|(
operator|!
name|indexShard
operator|.
name|routingEntry
argument_list|()
operator|.
name|started
argument_list|()
condition|)
block|{
comment|// we only schedule when the cluster assumes we have started
return|return;
block|}
if|if
condition|(
name|snapshotScheduleFuture
operator|!=
literal|null
condition|)
block|{
comment|// we are already scheduling this one, ignore
return|return;
block|}
if|if
condition|(
name|snapshotInterval
operator|.
name|millis
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// we need to schedule snapshot
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
literal|"scheduling snapshot every [{}]"
argument_list|,
name|snapshotInterval
argument_list|)
expr_stmt|;
block|}
name|snapshotScheduleFuture
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|SnapshotRunnable
argument_list|()
argument_list|,
name|snapshotInterval
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|SnapshotRunnable
specifier|private
class|class
name|SnapshotRunnable
implements|implements
name|Runnable
block|{
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|snapshot
argument_list|(
literal|"scheduled"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to snapshot (scheduled)"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

