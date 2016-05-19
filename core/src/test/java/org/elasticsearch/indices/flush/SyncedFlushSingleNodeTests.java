begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.flush
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|flush
package|;
end_package

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
name|routing
operator|.
name|IndexShardRoutingTable
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
name|routing
operator|.
name|ShardRouting
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
name|UUIDs
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
name|lease
operator|.
name|Releasable
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
name|IndexService
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
name|shard
operator|.
name|ShardNotFoundException
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
name|test
operator|.
name|ESSingleNodeTestCase
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SyncedFlushSingleNodeTests
specifier|public
class|class
name|SyncedFlushSingleNodeTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testModificationPreventsFlushing
specifier|public
name|void
name|testModificationPreventsFlushing
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndexService
name|test
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|test
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SyncedFlushService
name|flushService
init|=
name|getInstanceFromNode
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|shard
operator|.
name|shardId
argument_list|()
decl_stmt|;
specifier|final
name|ClusterState
name|state
init|=
name|getInstanceFromNode
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|state
argument_list|()
decl_stmt|;
specifier|final
name|IndexShardRoutingTable
name|shardRoutingTable
init|=
name|flushService
operator|.
name|getShardRoutingTable
argument_list|(
name|shardId
argument_list|,
name|state
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|activeShards
init|=
name|shardRoutingTable
operator|.
name|activeShards
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exactly one active shard"
argument_list|,
literal|1
argument_list|,
name|activeShards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Engine
operator|.
name|CommitId
argument_list|>
name|commitIds
init|=
name|SyncedFlushUtil
operator|.
name|sendPreSyncRequests
argument_list|(
name|flushService
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exactly one commit id"
argument_list|,
literal|1
argument_list|,
name|commitIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|syncId
init|=
name|UUIDs
operator|.
name|base64UUID
argument_list|()
decl_stmt|;
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|listener
init|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<>
argument_list|()
decl_stmt|;
name|flushService
operator|.
name|sendSyncRequests
argument_list|(
name|syncId
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|commitIds
argument_list|,
name|shardId
argument_list|,
name|shardRoutingTable
operator|.
name|size
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|ShardsSyncedFlushResult
name|syncedFlushResult
init|=
name|listener
operator|.
name|result
decl_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|syncedFlushResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|syncedFlushResult
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|syncId
argument_list|,
name|syncedFlushResult
operator|.
name|syncId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|success
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"pending operations"
argument_list|,
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
name|SyncedFlushUtil
operator|.
name|sendPreSyncRequests
argument_list|(
name|flushService
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
comment|// pull another commit and make sure we can't sync-flush with the old one
name|listener
operator|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
expr_stmt|;
name|flushService
operator|.
name|sendSyncRequests
argument_list|(
name|syncId
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|commitIds
argument_list|,
name|shardId
argument_list|,
name|shardRoutingTable
operator|.
name|size
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|syncedFlushResult
operator|=
name|listener
operator|.
name|result
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|syncedFlushResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|syncedFlushResult
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|syncId
argument_list|,
name|syncedFlushResult
operator|.
name|syncId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|success
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"commit has changed"
argument_list|,
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSingleShardSuccess
specifier|public
name|void
name|testSingleShardSuccess
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndexService
name|test
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|test
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SyncedFlushService
name|flushService
init|=
name|getInstanceFromNode
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|shard
operator|.
name|shardId
argument_list|()
decl_stmt|;
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|listener
init|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
decl_stmt|;
name|flushService
operator|.
name|attemptSyncedFlush
argument_list|(
name|shardId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|ShardsSyncedFlushResult
name|syncedFlushResult
init|=
name|listener
operator|.
name|result
decl_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|syncedFlushResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|syncedFlushResult
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
name|response
init|=
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|response
operator|.
name|success
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSyncFailsIfOperationIsInFlight
specifier|public
name|void
name|testSyncFailsIfOperationIsInFlight
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndexService
name|test
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|test
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SyncedFlushService
name|flushService
init|=
name|getInstanceFromNode
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|shard
operator|.
name|shardId
argument_list|()
decl_stmt|;
try|try
init|(
name|Releasable
name|operationLock
init|=
name|shard
operator|.
name|acquirePrimaryOperationLock
argument_list|()
init|)
block|{
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|listener
init|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<>
argument_list|()
decl_stmt|;
name|flushService
operator|.
name|attemptSyncedFlush
argument_list|(
name|shardId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|ShardsSyncedFlushResult
name|syncedFlushResult
init|=
name|listener
operator|.
name|result
decl_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|syncedFlushResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotEquals
argument_list|(
literal|0
argument_list|,
name|syncedFlushResult
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1] ongoing operations on primary"
argument_list|,
name|syncedFlushResult
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSyncFailsOnIndexClosedOrMissing
specifier|public
name|void
name|testSyncFailsOnIndexClosedOrMissing
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|IndexService
name|test
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|test
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SyncedFlushService
name|flushService
init|=
name|getInstanceFromNode
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
name|SyncedFlushUtil
operator|.
name|LatchedListener
name|listener
init|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
decl_stmt|;
name|flushService
operator|.
name|attemptSyncedFlush
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ShardNotFoundException
operator|.
name|class
argument_list|,
name|listener
operator|.
name|error
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no such shard"
argument_list|,
name|listener
operator|.
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|shard
operator|.
name|shardId
argument_list|()
decl_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|listener
operator|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
expr_stmt|;
name|flushService
operator|.
name|attemptSyncedFlush
argument_list|(
name|shardId
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"closed"
argument_list|,
name|listener
operator|.
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
expr_stmt|;
name|flushService
operator|.
name|attemptSyncedFlush
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"index not found"
argument_list|,
literal|"_na_"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no such index"
argument_list|,
name|listener
operator|.
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailAfterIntermediateCommit
specifier|public
name|void
name|testFailAfterIntermediateCommit
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndexService
name|test
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|test
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SyncedFlushService
name|flushService
init|=
name|getInstanceFromNode
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|shard
operator|.
name|shardId
argument_list|()
decl_stmt|;
specifier|final
name|ClusterState
name|state
init|=
name|getInstanceFromNode
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|state
argument_list|()
decl_stmt|;
specifier|final
name|IndexShardRoutingTable
name|shardRoutingTable
init|=
name|flushService
operator|.
name|getShardRoutingTable
argument_list|(
name|shardId
argument_list|,
name|state
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|activeShards
init|=
name|shardRoutingTable
operator|.
name|activeShards
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exactly one active shard"
argument_list|,
literal|1
argument_list|,
name|activeShards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Engine
operator|.
name|CommitId
argument_list|>
name|commitIds
init|=
name|SyncedFlushUtil
operator|.
name|sendPreSyncRequests
argument_list|(
name|flushService
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exactly one commit id"
argument_list|,
literal|1
argument_list|,
name|commitIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareFlush
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setForce
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|String
name|syncId
init|=
name|UUIDs
operator|.
name|base64UUID
argument_list|()
decl_stmt|;
specifier|final
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|listener
init|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
decl_stmt|;
name|flushService
operator|.
name|sendSyncRequests
argument_list|(
name|syncId
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|commitIds
argument_list|,
name|shardId
argument_list|,
name|shardRoutingTable
operator|.
name|size
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|ShardsSyncedFlushResult
name|syncedFlushResult
init|=
name|listener
operator|.
name|result
decl_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|syncedFlushResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|syncedFlushResult
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|syncId
argument_list|,
name|syncedFlushResult
operator|.
name|syncId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|success
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"commit has changed"
argument_list|,
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailWhenCommitIsMissing
specifier|public
name|void
name|testFailWhenCommitIsMissing
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"test"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"{}"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|IndexService
name|test
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|test
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SyncedFlushService
name|flushService
init|=
name|getInstanceFromNode
argument_list|(
name|SyncedFlushService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ShardId
name|shardId
init|=
name|shard
operator|.
name|shardId
argument_list|()
decl_stmt|;
specifier|final
name|ClusterState
name|state
init|=
name|getInstanceFromNode
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
operator|.
name|state
argument_list|()
decl_stmt|;
specifier|final
name|IndexShardRoutingTable
name|shardRoutingTable
init|=
name|flushService
operator|.
name|getShardRoutingTable
argument_list|(
name|shardId
argument_list|,
name|state
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|activeShards
init|=
name|shardRoutingTable
operator|.
name|activeShards
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exactly one active shard"
argument_list|,
literal|1
argument_list|,
name|activeShards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Engine
operator|.
name|CommitId
argument_list|>
name|commitIds
init|=
name|SyncedFlushUtil
operator|.
name|sendPreSyncRequests
argument_list|(
name|flushService
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|shardId
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exactly one commit id"
argument_list|,
literal|1
argument_list|,
name|commitIds
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|commitIds
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// wipe it...
name|String
name|syncId
init|=
name|UUIDs
operator|.
name|base64UUID
argument_list|()
decl_stmt|;
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|listener
init|=
operator|new
name|SyncedFlushUtil
operator|.
name|LatchedListener
argument_list|()
decl_stmt|;
name|flushService
operator|.
name|sendSyncRequests
argument_list|(
name|syncId
argument_list|,
name|activeShards
argument_list|,
name|state
argument_list|,
name|commitIds
argument_list|,
name|shardId
argument_list|,
name|shardRoutingTable
operator|.
name|size
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|error
argument_list|)
expr_stmt|;
name|ShardsSyncedFlushResult
name|syncedFlushResult
init|=
name|listener
operator|.
name|result
decl_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|syncedFlushResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|syncedFlushResult
operator|.
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|syncId
argument_list|,
name|syncedFlushResult
operator|.
name|syncId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|success
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"no commit id from pre-sync flush"
argument_list|,
name|syncedFlushResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|activeShards
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

