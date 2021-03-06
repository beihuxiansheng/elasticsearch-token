begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.restore
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|restore
package|;
end_package

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
name|support
operator|.
name|ActionFilters
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
name|master
operator|.
name|TransportMasterNodeAction
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
name|ClusterChangedEvent
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
name|ClusterStateListener
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
name|RestoreInProgress
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
name|block
operator|.
name|ClusterBlockException
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
name|block
operator|.
name|ClusterBlockLevel
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
name|IndexNameExpressionResolver
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
name|service
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
name|common
operator|.
name|collect
operator|.
name|ImmutableOpenMap
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
name|snapshots
operator|.
name|RestoreInfo
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
name|RestoreService
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
name|RestoreService
operator|.
name|RestoreCompletionResponse
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
name|TransportService
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
operator|.
name|RestoreService
operator|.
name|restoreInProgress
import|;
end_import

begin_comment
comment|/**  * Transport action for restore snapshot operation  */
end_comment

begin_class
DECL|class|TransportRestoreSnapshotAction
specifier|public
class|class
name|TransportRestoreSnapshotAction
extends|extends
name|TransportMasterNodeAction
argument_list|<
name|RestoreSnapshotRequest
argument_list|,
name|RestoreSnapshotResponse
argument_list|>
block|{
DECL|field|restoreService
specifier|private
specifier|final
name|RestoreService
name|restoreService
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportRestoreSnapshotAction
specifier|public
name|TransportRestoreSnapshotAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|RestoreService
name|restoreService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|RestoreSnapshotAction
operator|.
name|NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|RestoreSnapshotRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|restoreService
operator|=
name|restoreService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SNAPSHOT
return|;
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|RestoreSnapshotResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|RestoreSnapshotResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|RestoreSnapshotRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
comment|// Restoring a snapshot might change the global state and create/change an index,
comment|// so we need to check for METADATA_WRITE and WRITE blocks
name|ClusterBlockException
name|blockException
init|=
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|METADATA_WRITE
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockException
operator|!=
literal|null
condition|)
block|{
return|return
name|blockException
return|;
block|}
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|RestoreSnapshotRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|state
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|RestoreSnapshotResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|RestoreService
operator|.
name|RestoreRequest
name|restoreRequest
init|=
operator|new
name|RestoreService
operator|.
name|RestoreRequest
argument_list|(
name|request
operator|.
name|repository
argument_list|()
argument_list|,
name|request
operator|.
name|snapshot
argument_list|()
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|,
name|request
operator|.
name|indicesOptions
argument_list|()
argument_list|,
name|request
operator|.
name|renamePattern
argument_list|()
argument_list|,
name|request
operator|.
name|renameReplacement
argument_list|()
argument_list|,
name|request
operator|.
name|settings
argument_list|()
argument_list|,
name|request
operator|.
name|masterNodeTimeout
argument_list|()
argument_list|,
name|request
operator|.
name|includeGlobalState
argument_list|()
argument_list|,
name|request
operator|.
name|partial
argument_list|()
argument_list|,
name|request
operator|.
name|includeAliases
argument_list|()
argument_list|,
name|request
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|request
operator|.
name|ignoreIndexSettings
argument_list|()
argument_list|,
literal|"restore_snapshot["
operator|+
name|request
operator|.
name|snapshot
argument_list|()
operator|+
literal|"]"
argument_list|)
decl_stmt|;
name|restoreService
operator|.
name|restoreSnapshot
argument_list|(
name|restoreRequest
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|RestoreCompletionResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|RestoreCompletionResponse
name|restoreCompletionResponse
parameter_list|)
block|{
if|if
condition|(
name|restoreCompletionResponse
operator|.
name|getRestoreInfo
argument_list|()
operator|==
literal|null
operator|&&
name|request
operator|.
name|waitForCompletion
argument_list|()
condition|)
block|{
specifier|final
name|Snapshot
name|snapshot
init|=
name|restoreCompletionResponse
operator|.
name|getSnapshot
argument_list|()
decl_stmt|;
name|ClusterStateListener
name|clusterStateListener
init|=
operator|new
name|ClusterStateListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|clusterChanged
parameter_list|(
name|ClusterChangedEvent
name|changedEvent
parameter_list|)
block|{
specifier|final
name|RestoreInProgress
operator|.
name|Entry
name|prevEntry
init|=
name|restoreInProgress
argument_list|(
name|changedEvent
operator|.
name|previousState
argument_list|()
argument_list|,
name|snapshot
argument_list|)
decl_stmt|;
specifier|final
name|RestoreInProgress
operator|.
name|Entry
name|newEntry
init|=
name|restoreInProgress
argument_list|(
name|changedEvent
operator|.
name|state
argument_list|()
argument_list|,
name|snapshot
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevEntry
operator|==
literal|null
condition|)
block|{
comment|// When there is a master failure after a restore has been started, this listener might not be registered
comment|// on the current master and as such it might miss some intermediary cluster states due to batching.
comment|// Clean up listener in that case and acknowledge completion of restore operation to client.
name|clusterService
operator|.
name|removeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|RestoreSnapshotResponse
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|newEntry
operator|==
literal|null
condition|)
block|{
name|clusterService
operator|.
name|removeListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ImmutableOpenMap
argument_list|<
name|ShardId
argument_list|,
name|RestoreInProgress
operator|.
name|ShardRestoreStatus
argument_list|>
name|shards
init|=
name|prevEntry
operator|.
name|shards
argument_list|()
decl_stmt|;
assert|assert
name|prevEntry
operator|.
name|state
argument_list|()
operator|.
name|completed
argument_list|()
operator|:
literal|"expected completed snapshot state but was "
operator|+
name|prevEntry
operator|.
name|state
argument_list|()
assert|;
assert|assert
name|RestoreService
operator|.
name|completed
argument_list|(
name|shards
argument_list|)
operator|:
literal|"expected all restore entries to be completed"
assert|;
name|RestoreInfo
name|ri
init|=
operator|new
name|RestoreInfo
argument_list|(
name|prevEntry
operator|.
name|snapshot
argument_list|()
operator|.
name|getSnapshotId
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|prevEntry
operator|.
name|indices
argument_list|()
argument_list|,
name|shards
operator|.
name|size
argument_list|()
argument_list|,
name|shards
operator|.
name|size
argument_list|()
operator|-
name|RestoreService
operator|.
name|failedShards
argument_list|(
name|shards
argument_list|)
argument_list|)
decl_stmt|;
name|RestoreSnapshotResponse
name|response
init|=
operator|new
name|RestoreSnapshotResponse
argument_list|(
name|ri
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"restore of [{}] completed"
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// restore not completed yet, wait for next cluster state update
block|}
block|}
block|}
decl_stmt|;
name|clusterService
operator|.
name|addListener
argument_list|(
name|clusterStateListener
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onResponse
argument_list|(
operator|new
name|RestoreSnapshotResponse
argument_list|(
name|restoreCompletionResponse
operator|.
name|getRestoreInfo
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

