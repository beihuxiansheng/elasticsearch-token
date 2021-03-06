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
name|RepositoryMetaData
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
name|index
operator|.
name|shard
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
name|ShardId
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
name|snapshots
operator|.
name|IndexShardSnapshotStatus
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
name|snapshots
operator|.
name|SnapshotInfo
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An interface for interacting with a repository in snapshot and restore.  *<p>  * Implementations are responsible for reading and writing both metadata and shard data to and from  * a repository backend.  *<p>  * To perform a snapshot:  *<ul>  *<li>Master calls {@link #initializeSnapshot(SnapshotId, List, org.elasticsearch.cluster.metadata.MetaData)}  * with list of indices that will be included into the snapshot</li>  *<li>Data nodes call {@link Repository#snapshotShard(IndexShard, SnapshotId, IndexId, IndexCommit, IndexShardSnapshotStatus)}  * for each shard</li>  *<li>When all shard calls return master calls {@link #finalizeSnapshot} with possible list of failures</li>  *</ul>  */
end_comment

begin_interface
DECL|interface|Repository
specifier|public
interface|interface
name|Repository
extends|extends
name|LifecycleComponent
block|{
comment|/**      * An factory interface for constructing repositories.      * See {@link org.elasticsearch.plugins.RepositoryPlugin}.      */
DECL|interface|Factory
interface|interface
name|Factory
block|{
comment|/**          * Constructs a repository.          * @param metadata    metadata for the repository including name and settings          */
DECL|method|create
name|Repository
name|create
parameter_list|(
name|RepositoryMetaData
name|metadata
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
comment|/**      * Returns metadata about this repository.      */
DECL|method|getMetadata
name|RepositoryMetaData
name|getMetadata
parameter_list|()
function_decl|;
comment|/**      * Reads snapshot description from repository.      *      * @param snapshotId  snapshot id      * @return information about snapshot      */
DECL|method|getSnapshotInfo
name|SnapshotInfo
name|getSnapshotInfo
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|)
function_decl|;
comment|/**      * Returns global metadata associate with the snapshot.      *<p>      * The returned meta data contains global metadata as well as metadata for all indices listed in the indices parameter.      *      * @param snapshot snapshot      * @param indices    list of indices      * @return information about snapshot      */
DECL|method|getSnapshotMetaData
name|MetaData
name|getSnapshotMetaData
parameter_list|(
name|SnapshotInfo
name|snapshot
parameter_list|,
name|List
argument_list|<
name|IndexId
argument_list|>
name|indices
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns a {@link RepositoryData} to describe the data in the repository, including the snapshots      * and the indices across all snapshots found in the repository.  Throws a {@link RepositoryException}      * if there was an error in reading the data.      */
DECL|method|getRepositoryData
name|RepositoryData
name|getRepositoryData
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
name|List
argument_list|<
name|IndexId
argument_list|>
name|indices
parameter_list|,
name|MetaData
name|metaData
parameter_list|)
function_decl|;
comment|/**      * Finalizes snapshotting process      *<p>      * This method is called on master after all shards are snapshotted.      *      * @param snapshotId    snapshot id      * @param indices       list of indices in the snapshot      * @param startTime     start time of the snapshot      * @param failure       global failure reason or null      * @param totalShards   total number of shards      * @param shardFailures list of shard failures      * @param repositoryStateId the unique id identifying the state of the repository when the snapshot began      * @return snapshot description      */
DECL|method|finalizeSnapshot
name|SnapshotInfo
name|finalizeSnapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|List
argument_list|<
name|IndexId
argument_list|>
name|indices
parameter_list|,
name|long
name|startTime
parameter_list|,
name|String
name|failure
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|List
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|,
name|long
name|repositoryStateId
parameter_list|)
function_decl|;
comment|/**      * Deletes snapshot      *      * @param snapshotId snapshot id      * @param repositoryStateId the unique id identifying the state of the repository when the snapshot deletion began      */
DECL|method|deleteSnapshot
name|void
name|deleteSnapshot
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|long
name|repositoryStateId
parameter_list|)
function_decl|;
comment|/**      * Returns snapshot throttle time in nanoseconds      */
DECL|method|getSnapshotThrottleTimeInNanos
name|long
name|getSnapshotThrottleTimeInNanos
parameter_list|()
function_decl|;
comment|/**      * Returns restore throttle time in nanoseconds      */
DECL|method|getRestoreThrottleTimeInNanos
name|long
name|getRestoreThrottleTimeInNanos
parameter_list|()
function_decl|;
comment|/**      * Verifies repository on the master node and returns the verification token.      *<p>      * If the verification token is not null, it's passed to all data nodes for verification. If it's null - no      * additional verification is required      *      * @return verification token that should be passed to all Index Shard Repositories for additional verification or null      */
DECL|method|startVerification
name|String
name|startVerification
parameter_list|()
function_decl|;
comment|/**      * Called at the end of repository verification process.      *<p>      * This method should perform all necessary cleanup of the temporary files created in the repository      *      * @param verificationToken verification request generated by {@link #startVerification} command      */
DECL|method|endVerification
name|void
name|endVerification
parameter_list|(
name|String
name|verificationToken
parameter_list|)
function_decl|;
comment|/**      * Verifies repository settings on data node.      * @param verificationToken value returned by {@link org.elasticsearch.repositories.Repository#startVerification()}      * @param localNode         the local node information, for inclusion in verification errors      */
DECL|method|verify
name|void
name|verify
parameter_list|(
name|String
name|verificationToken
parameter_list|,
name|DiscoveryNode
name|localNode
parameter_list|)
function_decl|;
comment|/**      * Returns true if the repository supports only read operations      * @return true if the repository is read/only      */
DECL|method|isReadOnly
name|boolean
name|isReadOnly
parameter_list|()
function_decl|;
comment|/**      * Creates a snapshot of the shard based on the index commit point.      *<p>      * The index commit point can be obtained by using {@link org.elasticsearch.index.engine.Engine#acquireIndexCommit} method.      * Repository implementations shouldn't release the snapshot index commit point. It is done by the method caller.      *<p>      * As snapshot process progresses, implementation of this method should update {@link IndexShardSnapshotStatus} object and check      * {@link IndexShardSnapshotStatus#aborted()} to see if the snapshot process should be aborted.      *      * @param shard               shard to be snapshotted      * @param snapshotId          snapshot id      * @param indexId             id for the index being snapshotted      * @param snapshotIndexCommit commit point      * @param snapshotStatus      snapshot status      */
DECL|method|snapshotShard
name|void
name|snapshotShard
parameter_list|(
name|IndexShard
name|shard
parameter_list|,
name|SnapshotId
name|snapshotId
parameter_list|,
name|IndexId
name|indexId
parameter_list|,
name|IndexCommit
name|snapshotIndexCommit
parameter_list|,
name|IndexShardSnapshotStatus
name|snapshotStatus
parameter_list|)
function_decl|;
comment|/**      * Restores snapshot of the shard.      *<p>      * The index can be renamed on restore, hence different {@code shardId} and {@code snapshotShardId} are supplied.      *      * @param shard           the shard to restore the index into      * @param snapshotId      snapshot id      * @param version         version of elasticsearch that created this snapshot      * @param indexId         id of the index in the repository from which the restore is occurring      * @param snapshotShardId shard id (in the snapshot)      * @param recoveryState   recovery state      */
DECL|method|restoreShard
name|void
name|restoreShard
parameter_list|(
name|IndexShard
name|shard
parameter_list|,
name|SnapshotId
name|snapshotId
parameter_list|,
name|Version
name|version
parameter_list|,
name|IndexId
name|indexId
parameter_list|,
name|ShardId
name|snapshotShardId
parameter_list|,
name|RecoveryState
name|recoveryState
parameter_list|)
function_decl|;
comment|/**      * Retrieve shard snapshot status for the stored snapshot      *      * @param snapshotId snapshot id      * @param version    version of elasticsearch that created this snapshot      * @param indexId    the snapshotted index id for the shard to get status for      * @param shardId    shard id      * @return snapshot status      */
DECL|method|getShardSnapshotStatus
name|IndexShardSnapshotStatus
name|getShardSnapshotStatus
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|Version
name|version
parameter_list|,
name|IndexId
name|indexId
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

