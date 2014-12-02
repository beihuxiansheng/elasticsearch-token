begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.repositories
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
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
name|ImmutableList
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
name|MetaData
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
name|common
operator|.
name|component
operator|.
name|LifecycleComponent
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
name|Streamable
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
name|Snapshot
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
name|SnapshotShardFailure
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

begin_comment
comment|/**  * Snapshot repository interface.  *<p/>  * Responsible for index and cluster level operations. It's called only on master.  * Shard-level operations are performed using {@link org.elasticsearch.index.snapshots.IndexShardRepository}  * interface on data nodes.  *<p/>  * Typical snapshot usage pattern:  *<ul>  *<li>Master calls {@link #initializeSnapshot(org.elasticsearch.cluster.metadata.SnapshotId, com.google.common.collect.ImmutableList, org.elasticsearch.cluster.metadata.MetaData)}  * with list of indices that will be included into the snapshot</li>  *<li>Data nodes call {@link org.elasticsearch.index.snapshots.IndexShardRepository#snapshot(org.elasticsearch.cluster.metadata.SnapshotId, org.elasticsearch.index.shard.ShardId, org.elasticsearch.index.deletionpolicy.SnapshotIndexCommit, org.elasticsearch.index.snapshots.IndexShardSnapshotStatus)} for each shard</li>  *<li>When all shard calls return master calls {@link #finalizeSnapshot(org.elasticsearch.cluster.metadata.SnapshotId, String, int, com.google.common.collect.ImmutableList)}  * with possible list of failures</li>  *</ul>  */
end_comment

begin_interface
DECL|interface|Repository
specifier|public
interface|interface
name|Repository
extends|extends
name|LifecycleComponent
argument_list|<
name|Repository
argument_list|>
block|{
comment|/**      * Reads snapshot description from repository.      *      * @param snapshotId snapshot ID      * @return information about snapshot      */
DECL|method|readSnapshot
name|Snapshot
name|readSnapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|)
function_decl|;
comment|/**      * Returns global metadata associate with the snapshot.      *<p/>      * The returned meta data contains global metadata as well as metadata for all indices listed in the indices parameter.      *      * @param snapshotId snapshot ID      * @param indices    list of indices      * @return information about snapshot      */
DECL|method|readSnapshotMetaData
name|MetaData
name|readSnapshotMetaData
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the list of snapshots currently stored in the repository      *      * @return snapshot list      */
DECL|method|snapshots
name|ImmutableList
argument_list|<
name|SnapshotId
argument_list|>
name|snapshots
parameter_list|()
function_decl|;
comment|/**      * Starts snapshotting process      *      * @param snapshotId snapshot id      * @param indices    list of indices to be snapshotted      * @param metaData   cluster metadata      */
DECL|method|initializeSnapshot
name|void
name|initializeSnapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|,
name|MetaData
name|metaData
parameter_list|)
function_decl|;
comment|/**      * Finalizes snapshotting process      *<p/>      * This method is called on master after all shards are snapshotted.      *      * @param snapshotId    snapshot id      * @param failure       global failure reason or null      * @param totalShards   total number of shards      * @param shardFailures list of shard failures      * @return snapshot description      */
DECL|method|finalizeSnapshot
name|Snapshot
name|finalizeSnapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|String
name|failure
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|)
function_decl|;
comment|/**      * Deletes snapshot      *      * @param snapshotId snapshot id      */
DECL|method|deleteSnapshot
name|void
name|deleteSnapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|)
function_decl|;
comment|/**      * Returns snapshot throttle time in nanoseconds      */
DECL|method|snapshotThrottleTimeInNanos
name|long
name|snapshotThrottleTimeInNanos
parameter_list|()
function_decl|;
comment|/**      * Returns restore throttle time in nanoseconds      */
DECL|method|restoreThrottleTimeInNanos
name|long
name|restoreThrottleTimeInNanos
parameter_list|()
function_decl|;
comment|/**      * Verifies repository on the master node and returns the verification token.      *      * If the verification token is not null, it's passed to all data nodes for verification. If it's null - no      * additional verification is required      *      * @return verification token that should be passed to all Index Shard Repositories for additional verification or null      */
DECL|method|startVerification
name|String
name|startVerification
parameter_list|()
function_decl|;
comment|/**      * Called at the end of repository verification process.      *      * This method should perform all necessary cleanup of the temporary files created in the repository      *      * @param verificationToken verification request generated by {@link #startVerification} command      */
DECL|method|endVerification
name|void
name|endVerification
parameter_list|(
name|String
name|verificationToken
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

