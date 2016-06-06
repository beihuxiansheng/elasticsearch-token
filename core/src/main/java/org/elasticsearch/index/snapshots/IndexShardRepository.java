begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|snapshots
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexCommit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|SnapshotId
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
name|indices
operator|.
name|recovery
operator|.
name|RecoveryState
import|;
end_import

begin_comment
comment|/**  * Shard-level snapshot repository  *<p>  * IndexShardRepository is used on data node to create snapshots of individual shards. See {@link org.elasticsearch.repositories.Repository}  * for more information.  */
end_comment

begin_interface
DECL|interface|IndexShardRepository
specifier|public
interface|interface
name|IndexShardRepository
block|{
comment|/**      * Creates a snapshot of the shard based on the index commit point.      *<p>      * The index commit point can be obtained by using {@link org.elasticsearch.index.engine.Engine#snapshotIndex} method.      * IndexShardRepository implementations shouldn't release the snapshot index commit point. It is done by the method caller.      *<p>      * As snapshot process progresses, implementation of this method should update {@link IndexShardSnapshotStatus} object and check      * {@link IndexShardSnapshotStatus#aborted()} to see if the snapshot process should be aborted.      *      * @param snapshotId          snapshot id      * @param shardId             shard to be snapshotted      * @param snapshotIndexCommit commit point      * @param snapshotStatus      snapshot status      */
DECL|method|snapshot
name|void
name|snapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|IndexCommit
name|snapshotIndexCommit
parameter_list|,
name|IndexShardSnapshotStatus
name|snapshotStatus
parameter_list|)
function_decl|;
comment|/**      * Restores snapshot of the shard.      *<p>      * The index can be renamed on restore, hence different {@code shardId} and {@code snapshotShardId} are supplied.      *      * @param snapshotId      snapshot id      * @param shardId         shard id (in the current index)      * @param version   version of elasticsearch that created this snapshot      * @param snapshotShardId shard id (in the snapshot)      * @param recoveryState   recovery state      */
DECL|method|restore
name|void
name|restore
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|Version
name|version
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|ShardId
name|snapshotShardId
parameter_list|,
name|RecoveryState
name|recoveryState
parameter_list|)
function_decl|;
comment|/**      * Retrieve shard snapshot status for the stored snapshot      *      * @param snapshotId snapshot id      * @param version   version of elasticsearch that created this snapshot      * @param shardId    shard id      * @return snapshot status      */
DECL|method|snapshotStatus
name|IndexShardSnapshotStatus
name|snapshotStatus
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|Version
name|version
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
function_decl|;
comment|/**      * Verifies repository settings on data node      * @param verificationToken value returned by {@link org.elasticsearch.repositories.Repository#startVerification()}      */
DECL|method|verify
name|void
name|verify
parameter_list|(
name|String
name|verificationToken
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

