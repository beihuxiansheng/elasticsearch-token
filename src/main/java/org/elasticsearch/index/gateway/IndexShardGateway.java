begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchIllegalStateException
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
name|CloseableIndexComponent
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
name|shard
operator|.
name|IndexShardComponent
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

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|IndexShardGateway
specifier|public
interface|interface
name|IndexShardGateway
extends|extends
name|IndexShardComponent
extends|,
name|CloseableIndexComponent
block|{
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * The last / on going recovery status.      */
DECL|method|recoveryStatus
name|RecoveryStatus
name|recoveryStatus
parameter_list|()
function_decl|;
comment|/**      * The last snapshot status performed. Can be<tt>null</tt>.      */
DECL|method|lastSnapshotStatus
name|SnapshotStatus
name|lastSnapshotStatus
parameter_list|()
function_decl|;
comment|/**      * The current snapshot status being performed. Can be<tt>null</tt> indicating that no snapshot      * is being executed currently.      */
DECL|method|currentSnapshotStatus
name|SnapshotStatus
name|currentSnapshotStatus
parameter_list|()
function_decl|;
comment|/**      * Recovers the state of the shard from the gateway.      */
DECL|method|recover
name|void
name|recover
parameter_list|(
name|boolean
name|indexShouldExists
parameter_list|,
name|RecoveryStatus
name|recoveryStatus
parameter_list|)
throws|throws
name|IndexShardGatewayRecoveryException
function_decl|;
comment|/**      * Snapshots the given shard into the gateway.      */
DECL|method|snapshot
name|SnapshotStatus
name|snapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
throws|throws
name|IndexShardGatewaySnapshotFailedException
function_decl|;
comment|/**      * Returns<tt>true</tt> if snapshot is even required on this gateway (i.e. mainly handles recovery).      */
DECL|method|requiresSnapshot
name|boolean
name|requiresSnapshot
parameter_list|()
function_decl|;
comment|/**      * Returns<tt>true</tt> if this gateway requires scheduling management for snapshot      * operations.      */
DECL|method|requiresSnapshotScheduling
name|boolean
name|requiresSnapshotScheduling
parameter_list|()
function_decl|;
DECL|method|obtainSnapshotLock
name|SnapshotLock
name|obtainSnapshotLock
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|interface|SnapshotLock
specifier|public
specifier|static
interface|interface
name|SnapshotLock
block|{
DECL|method|release
name|void
name|release
parameter_list|()
function_decl|;
block|}
DECL|field|NO_SNAPSHOT_LOCK
specifier|public
specifier|static
specifier|final
name|SnapshotLock
name|NO_SNAPSHOT_LOCK
init|=
operator|new
name|SnapshotLock
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|()
block|{         }
block|}
decl_stmt|;
DECL|class|Snapshot
specifier|public
specifier|static
class|class
name|Snapshot
block|{
DECL|field|indexCommit
specifier|private
specifier|final
name|SnapshotIndexCommit
name|indexCommit
decl_stmt|;
DECL|field|translogSnapshot
specifier|private
specifier|final
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
decl_stmt|;
DECL|field|lastIndexVersion
specifier|private
specifier|final
name|long
name|lastIndexVersion
decl_stmt|;
DECL|field|lastTranslogId
specifier|private
specifier|final
name|long
name|lastTranslogId
decl_stmt|;
DECL|field|lastTranslogLength
specifier|private
specifier|final
name|long
name|lastTranslogLength
decl_stmt|;
DECL|field|lastTotalTranslogOperations
specifier|private
specifier|final
name|int
name|lastTotalTranslogOperations
decl_stmt|;
DECL|method|Snapshot
specifier|public
name|Snapshot
parameter_list|(
name|SnapshotIndexCommit
name|indexCommit
parameter_list|,
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
parameter_list|,
name|long
name|lastIndexVersion
parameter_list|,
name|long
name|lastTranslogId
parameter_list|,
name|long
name|lastTranslogLength
parameter_list|,
name|int
name|lastTotalTranslogOperations
parameter_list|)
block|{
name|this
operator|.
name|indexCommit
operator|=
name|indexCommit
expr_stmt|;
name|this
operator|.
name|translogSnapshot
operator|=
name|translogSnapshot
expr_stmt|;
name|this
operator|.
name|lastIndexVersion
operator|=
name|lastIndexVersion
expr_stmt|;
name|this
operator|.
name|lastTranslogId
operator|=
name|lastTranslogId
expr_stmt|;
name|this
operator|.
name|lastTranslogLength
operator|=
name|lastTranslogLength
expr_stmt|;
name|this
operator|.
name|lastTotalTranslogOperations
operator|=
name|lastTotalTranslogOperations
expr_stmt|;
block|}
comment|/**          * Indicates that the index has changed from the latest snapshot.          */
DECL|method|indexChanged
specifier|public
name|boolean
name|indexChanged
parameter_list|()
block|{
return|return
name|lastIndexVersion
operator|!=
name|indexCommit
operator|.
name|getVersion
argument_list|()
return|;
block|}
comment|/**          * Indicates that a new transaction log has been created. Note check this<b>before</b> you          * check {@link #sameTranslogNewOperations()}.          */
DECL|method|newTranslogCreated
specifier|public
name|boolean
name|newTranslogCreated
parameter_list|()
block|{
return|return
name|translogSnapshot
operator|.
name|translogId
argument_list|()
operator|!=
name|lastTranslogId
return|;
block|}
comment|/**          * Indicates that the same translog exists, but new operations have been appended to it. Throws          * {@link ElasticSearchIllegalStateException} if {@link #newTranslogCreated()} is<tt>true</tt>, so          * always check that first.          */
DECL|method|sameTranslogNewOperations
specifier|public
name|boolean
name|sameTranslogNewOperations
parameter_list|()
block|{
if|if
condition|(
name|newTranslogCreated
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Should not be called when there is a new translog"
argument_list|)
throw|;
block|}
return|return
name|translogSnapshot
operator|.
name|length
argument_list|()
operator|>
name|lastTranslogLength
return|;
block|}
DECL|method|indexCommit
specifier|public
name|SnapshotIndexCommit
name|indexCommit
parameter_list|()
block|{
return|return
name|indexCommit
return|;
block|}
DECL|method|translogSnapshot
specifier|public
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
parameter_list|()
block|{
return|return
name|translogSnapshot
return|;
block|}
DECL|method|lastIndexVersion
specifier|public
name|long
name|lastIndexVersion
parameter_list|()
block|{
return|return
name|lastIndexVersion
return|;
block|}
DECL|method|lastTranslogId
specifier|public
name|long
name|lastTranslogId
parameter_list|()
block|{
return|return
name|lastTranslogId
return|;
block|}
DECL|method|lastTranslogLength
specifier|public
name|long
name|lastTranslogLength
parameter_list|()
block|{
return|return
name|lastTranslogLength
return|;
block|}
DECL|method|lastTotalTranslogOperations
specifier|public
name|int
name|lastTotalTranslogOperations
parameter_list|()
block|{
return|return
name|this
operator|.
name|lastTotalTranslogOperations
return|;
block|}
block|}
block|}
end_interface

end_unit

