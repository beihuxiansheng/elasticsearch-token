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
name|Version
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
name|replication
operator|.
name|ReplicationOperation
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
name|replication
operator|.
name|ReplicationRequest
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
name|replication
operator|.
name|ReplicationResponse
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
name|replication
operator|.
name|TransportReplicationAction
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
name|action
operator|.
name|shard
operator|.
name|ShardStateAction
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
name|IndexEventListener
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
name|indices
operator|.
name|IndicesService
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

begin_comment
comment|/**  * Background global checkpoint sync action initiated when a shard goes inactive. This is needed because while we send the global checkpoint  * on every replication operation, after the last operation completes the global checkpoint could advance but without a follow-up operation  * the global checkpoint will never be synced to the replicas.  */
end_comment

begin_class
DECL|class|GlobalCheckpointSyncAction
specifier|public
class|class
name|GlobalCheckpointSyncAction
extends|extends
name|TransportReplicationAction
argument_list|<
name|GlobalCheckpointSyncAction
operator|.
name|Request
argument_list|,
name|GlobalCheckpointSyncAction
operator|.
name|Request
argument_list|,
name|ReplicationResponse
argument_list|>
implements|implements
name|IndexEventListener
block|{
DECL|field|ACTION_NAME
specifier|public
specifier|static
name|String
name|ACTION_NAME
init|=
literal|"indices:admin/seq_no/global_checkpoint_sync"
decl_stmt|;
annotation|@
name|Inject
DECL|method|GlobalCheckpointSyncAction
specifier|public
name|GlobalCheckpointSyncAction
parameter_list|(
specifier|final
name|Settings
name|settings
parameter_list|,
specifier|final
name|TransportService
name|transportService
parameter_list|,
specifier|final
name|ClusterService
name|clusterService
parameter_list|,
specifier|final
name|IndicesService
name|indicesService
parameter_list|,
specifier|final
name|ThreadPool
name|threadPool
parameter_list|,
specifier|final
name|ShardStateAction
name|shardStateAction
parameter_list|,
specifier|final
name|ActionFilters
name|actionFilters
parameter_list|,
specifier|final
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ACTION_NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|indicesService
argument_list|,
name|threadPool
argument_list|,
name|shardStateAction
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|Request
operator|::
operator|new
argument_list|,
name|Request
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newResponseInstance
specifier|protected
name|ReplicationResponse
name|newResponseInstance
parameter_list|()
block|{
return|return
operator|new
name|ReplicationResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sendReplicaRequest
specifier|protected
name|void
name|sendReplicaRequest
parameter_list|(
specifier|final
name|ConcreteReplicaRequest
argument_list|<
name|Request
argument_list|>
name|replicaRequest
parameter_list|,
specifier|final
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ReplicationOperation
operator|.
name|ReplicaResponse
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha1
argument_list|)
condition|)
block|{
name|super
operator|.
name|sendReplicaRequest
argument_list|(
name|replicaRequest
argument_list|,
name|node
argument_list|,
name|listener
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
name|ReplicaResponse
argument_list|(
name|replicaRequest
operator|.
name|getTargetAllocationID
argument_list|()
argument_list|,
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onShardInactive
specifier|public
name|void
name|onShardInactive
parameter_list|(
specifier|final
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|execute
argument_list|(
operator|new
name|Request
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnPrimary
specifier|protected
name|PrimaryResult
argument_list|<
name|Request
argument_list|,
name|ReplicationResponse
argument_list|>
name|shardOperationOnPrimary
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|IndexShard
name|indexShard
parameter_list|)
throws|throws
name|Exception
block|{
name|indexShard
operator|.
name|getTranslog
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
return|return
operator|new
name|PrimaryResult
argument_list|<>
argument_list|(
name|request
argument_list|,
operator|new
name|ReplicationResponse
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|shardOperationOnReplica
specifier|protected
name|ReplicaResult
name|shardOperationOnReplica
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|IndexShard
name|indexShard
parameter_list|)
throws|throws
name|Exception
block|{
name|indexShard
operator|.
name|getTranslog
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
return|return
operator|new
name|ReplicaResult
argument_list|()
return|;
block|}
DECL|class|Request
specifier|public
specifier|static
specifier|final
class|class
name|Request
extends|extends
name|ReplicationRequest
argument_list|<
name|Request
argument_list|>
block|{
DECL|method|Request
specifier|private
name|Request
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|Request
specifier|public
name|Request
parameter_list|(
specifier|final
name|ShardId
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"GlobalCheckpointSyncAction.Request{"
operator|+
literal|"shardId="
operator|+
name|shardId
operator|+
literal|", timeout="
operator|+
name|timeout
operator|+
literal|", index='"
operator|+
name|index
operator|+
literal|'\''
operator|+
literal|", waitForActiveShards="
operator|+
name|waitForActiveShards
operator|+
literal|"}"
return|;
block|}
block|}
block|}
end_class

end_unit

