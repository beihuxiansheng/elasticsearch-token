begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|replication
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|admin
operator|.
name|indices
operator|.
name|flush
operator|.
name|FlushRequest
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
name|index
operator|.
name|IndexRequest
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
name|indices
operator|.
name|recovery
operator|.
name|PeerRecoveryTargetService
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
name|indices
operator|.
name|recovery
operator|.
name|RecoveryTarget
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
name|junit
operator|.
name|annotations
operator|.
name|TestLogging
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
name|EnumSet
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
name|concurrent
operator|.
name|CountDownLatch
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
name|Future
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|empty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|not
import|;
end_import

begin_class
DECL|class|RecoveryDuringReplicationTests
specifier|public
class|class
name|RecoveryDuringReplicationTests
extends|extends
name|ESIndexLevelReplicationTestCase
block|{
DECL|method|testIndexingDuringFileRecovery
specifier|public
name|void
name|testIndexingDuringFileRecovery
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|ReplicationGroup
name|shards
init|=
name|createGroup
argument_list|(
name|randomInt
argument_list|(
literal|1
argument_list|)
argument_list|)
init|)
block|{
name|shards
operator|.
name|startAll
argument_list|()
expr_stmt|;
name|int
name|docs
init|=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|50
argument_list|)
argument_list|)
decl_stmt|;
name|shards
operator|.
name|flush
argument_list|()
expr_stmt|;
name|IndexShard
name|replica
init|=
name|shards
operator|.
name|addReplica
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|recoveryBlocked
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|releaseRecovery
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RecoveryState
operator|.
name|Stage
name|blockOnStage
init|=
name|randomFrom
argument_list|(
name|BlockingTarget
operator|.
name|SUPPORTED_STAGES
argument_list|)
decl_stmt|;
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|recoveryFuture
init|=
name|shards
operator|.
name|asyncRecoverReplica
argument_list|(
name|replica
argument_list|,
parameter_list|(
name|indexShard
parameter_list|,
name|node
parameter_list|)
lambda|->
operator|new
name|BlockingTarget
argument_list|(
name|blockOnStage
argument_list|,
name|recoveryBlocked
argument_list|,
name|releaseRecovery
argument_list|,
name|indexShard
argument_list|,
name|node
argument_list|,
name|recoveryListener
argument_list|,
name|logger
argument_list|)
argument_list|)
decl_stmt|;
name|recoveryBlocked
operator|.
name|await
argument_list|()
expr_stmt|;
name|docs
operator|+=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|releaseRecovery
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|recoveryFuture
operator|.
name|get
argument_list|()
expr_stmt|;
name|shards
operator|.
name|assertAllEqual
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRecoveryOfDisconnectedReplica
specifier|public
name|void
name|testRecoveryOfDisconnectedReplica
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|ReplicationGroup
name|shards
init|=
name|createGroup
argument_list|(
literal|1
argument_list|)
init|)
block|{
name|shards
operator|.
name|startAll
argument_list|()
expr_stmt|;
name|int
name|docs
init|=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|50
argument_list|)
argument_list|)
decl_stmt|;
name|shards
operator|.
name|flush
argument_list|()
expr_stmt|;
name|shards
operator|.
name|getPrimary
argument_list|()
operator|.
name|updateGlobalCheckpointOnPrimary
argument_list|()
expr_stmt|;
specifier|final
name|IndexShard
name|originalReplica
init|=
name|shards
operator|.
name|getReplicas
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|long
name|replicaCommittedLocalCheckpoint
init|=
name|docs
operator|-
literal|1
decl_stmt|;
name|boolean
name|replicaHasDocsSinceLastFlushedCheckpoint
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|randomInt
argument_list|(
literal|2
argument_list|)
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|indexedDocs
init|=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|docs
operator|+=
name|indexedDocs
expr_stmt|;
if|if
condition|(
name|indexedDocs
operator|>
literal|0
condition|)
block|{
name|replicaHasDocsSinceLastFlushedCheckpoint
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|boolean
name|flush
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|flush
condition|)
block|{
name|originalReplica
operator|.
name|flush
argument_list|(
operator|new
name|FlushRequest
argument_list|()
argument_list|)
expr_stmt|;
name|replicaHasDocsSinceLastFlushedCheckpoint
operator|=
literal|false
expr_stmt|;
name|replicaCommittedLocalCheckpoint
operator|=
name|docs
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|boolean
name|sync
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|sync
condition|)
block|{
name|shards
operator|.
name|getPrimary
argument_list|()
operator|.
name|updateGlobalCheckpointOnPrimary
argument_list|()
expr_stmt|;
block|}
block|}
name|shards
operator|.
name|removeReplica
argument_list|(
name|originalReplica
argument_list|)
expr_stmt|;
specifier|final
name|int
name|missingOnReplica
init|=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
decl_stmt|;
name|docs
operator|+=
name|missingOnReplica
expr_stmt|;
name|replicaHasDocsSinceLastFlushedCheckpoint
operator||=
name|missingOnReplica
operator|>
literal|0
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|shards
operator|.
name|getPrimary
argument_list|()
operator|.
name|updateGlobalCheckpointOnPrimary
argument_list|()
expr_stmt|;
block|}
specifier|final
name|boolean
name|flushPrimary
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|flushPrimary
condition|)
block|{
name|shards
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
name|originalReplica
operator|.
name|close
argument_list|(
literal|"disconnected"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|originalReplica
operator|.
name|store
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|IndexShard
name|recoveredReplica
init|=
name|shards
operator|.
name|addReplicaWithExistingPath
argument_list|(
name|originalReplica
operator|.
name|shardPath
argument_list|()
argument_list|,
name|originalReplica
operator|.
name|routingEntry
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|shards
operator|.
name|recoverReplica
argument_list|(
name|recoveredReplica
argument_list|)
expr_stmt|;
if|if
condition|(
name|flushPrimary
operator|&&
name|replicaHasDocsSinceLastFlushedCheckpoint
condition|)
block|{
comment|// replica has something to catch up with, but since we flushed the primary, we should fall back to full recovery
name|assertThat
argument_list|(
name|recoveredReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|.
name|fileDetails
argument_list|()
argument_list|,
name|not
argument_list|(
name|empty
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|recoveredReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|.
name|fileDetails
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|recoveredReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getTranslog
argument_list|()
operator|.
name|recoveredOperations
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Math
operator|.
name|toIntExact
argument_list|(
name|docs
operator|-
operator|(
name|replicaCommittedLocalCheckpoint
operator|+
literal|1
operator|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|docs
operator|+=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|shards
operator|.
name|assertAllEqual
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|TestLogging
argument_list|(
literal|"org.elasticsearch.index.shard:TRACE,org.elasticsearch.indices.recovery:TRACE"
argument_list|)
DECL|method|testRecoveryAfterPrimaryPromotion
specifier|public
name|void
name|testRecoveryAfterPrimaryPromotion
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|ReplicationGroup
name|shards
init|=
name|createGroup
argument_list|(
literal|2
argument_list|)
init|)
block|{
name|shards
operator|.
name|startAll
argument_list|()
expr_stmt|;
name|int
name|totalDocs
init|=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomInt
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|committedDocs
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|shards
operator|.
name|flush
argument_list|()
expr_stmt|;
name|committedDocs
operator|=
name|totalDocs
expr_stmt|;
block|}
comment|// we need some indexing to happen to transfer local checkpoint information to the primary
comment|// so it can update the global checkpoint and communicate to replicas
name|boolean
name|expectSeqNoRecovery
init|=
name|totalDocs
operator|>
literal|0
decl_stmt|;
specifier|final
name|IndexShard
name|oldPrimary
init|=
name|shards
operator|.
name|getPrimary
argument_list|()
decl_stmt|;
specifier|final
name|IndexShard
name|newPrimary
init|=
name|shards
operator|.
name|getReplicas
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|IndexShard
name|replica
init|=
name|shards
operator|.
name|getReplicas
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// simulate docs that were inflight when primary failed, these will be rolled back
specifier|final
name|int
name|rollbackDocs
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> indexing {} rollback docs"
argument_list|,
name|rollbackDocs
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rollbackDocs
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|IndexRequest
name|indexRequest
init|=
operator|new
name|IndexRequest
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|,
literal|"type"
argument_list|,
literal|"rollback_"
operator|+
name|i
argument_list|)
operator|.
name|source
argument_list|(
literal|"{}"
argument_list|)
decl_stmt|;
name|indexOnPrimary
argument_list|(
name|indexRequest
argument_list|,
name|oldPrimary
argument_list|)
expr_stmt|;
name|indexOnReplica
argument_list|(
name|indexRequest
argument_list|,
name|replica
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|oldPrimary
operator|.
name|flush
argument_list|(
operator|new
name|FlushRequest
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|expectSeqNoRecovery
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|shards
operator|.
name|promoteReplicaToPrimary
argument_list|(
name|newPrimary
argument_list|)
expr_stmt|;
comment|// index some more
name|totalDocs
operator|+=
name|shards
operator|.
name|indexDocs
argument_list|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|oldPrimary
operator|.
name|close
argument_list|(
literal|"demoted"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|oldPrimary
operator|.
name|store
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexShard
name|newReplica
init|=
name|shards
operator|.
name|addReplicaWithExistingPath
argument_list|(
name|oldPrimary
operator|.
name|shardPath
argument_list|()
argument_list|,
name|oldPrimary
operator|.
name|routingEntry
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|shards
operator|.
name|recoverReplica
argument_list|(
name|newReplica
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectSeqNoRecovery
condition|)
block|{
name|assertThat
argument_list|(
name|newReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|.
name|fileDetails
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getTranslog
argument_list|()
operator|.
name|recoveredOperations
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|totalDocs
operator|-
name|committedDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|newReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|.
name|fileDetails
argument_list|()
argument_list|,
name|not
argument_list|(
name|empty
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|newReplica
operator|.
name|recoveryState
argument_list|()
operator|.
name|getTranslog
argument_list|()
operator|.
name|recoveredOperations
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|totalDocs
operator|-
name|committedDocs
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shards
operator|.
name|removeReplica
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|replica
operator|.
name|close
argument_list|(
literal|"resync"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|replica
operator|.
name|store
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|newReplica
operator|=
name|shards
operator|.
name|addReplicaWithExistingPath
argument_list|(
name|replica
operator|.
name|shardPath
argument_list|()
argument_list|,
name|replica
operator|.
name|routingEntry
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|)
expr_stmt|;
name|shards
operator|.
name|recoverReplica
argument_list|(
name|newReplica
argument_list|)
expr_stmt|;
name|shards
operator|.
name|assertAllEqual
argument_list|(
name|totalDocs
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|BlockingTarget
specifier|private
specifier|static
class|class
name|BlockingTarget
extends|extends
name|RecoveryTarget
block|{
DECL|field|recoveryBlocked
specifier|private
specifier|final
name|CountDownLatch
name|recoveryBlocked
decl_stmt|;
DECL|field|releaseRecovery
specifier|private
specifier|final
name|CountDownLatch
name|releaseRecovery
decl_stmt|;
DECL|field|stageToBlock
specifier|private
specifier|final
name|RecoveryState
operator|.
name|Stage
name|stageToBlock
decl_stmt|;
DECL|field|SUPPORTED_STAGES
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|RecoveryState
operator|.
name|Stage
argument_list|>
name|SUPPORTED_STAGES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|RecoveryState
operator|.
name|Stage
operator|.
name|INDEX
argument_list|,
name|RecoveryState
operator|.
name|Stage
operator|.
name|TRANSLOG
argument_list|,
name|RecoveryState
operator|.
name|Stage
operator|.
name|FINALIZE
argument_list|)
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|method|BlockingTarget
name|BlockingTarget
parameter_list|(
name|RecoveryState
operator|.
name|Stage
name|stageToBlock
parameter_list|,
name|CountDownLatch
name|recoveryBlocked
parameter_list|,
name|CountDownLatch
name|releaseRecovery
parameter_list|,
name|IndexShard
name|shard
parameter_list|,
name|DiscoveryNode
name|sourceNode
parameter_list|,
name|PeerRecoveryTargetService
operator|.
name|RecoveryListener
name|listener
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|super
argument_list|(
name|shard
argument_list|,
name|sourceNode
argument_list|,
name|listener
argument_list|,
name|version
lambda|->
block|{}
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoveryBlocked
operator|=
name|recoveryBlocked
expr_stmt|;
name|this
operator|.
name|releaseRecovery
operator|=
name|releaseRecovery
expr_stmt|;
name|this
operator|.
name|stageToBlock
operator|=
name|stageToBlock
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
if|if
condition|(
name|SUPPORTED_STAGES
operator|.
name|contains
argument_list|(
name|stageToBlock
argument_list|)
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|stageToBlock
operator|+
literal|" is not supported"
argument_list|)
throw|;
block|}
block|}
DECL|method|hasBlocked
specifier|private
name|boolean
name|hasBlocked
parameter_list|()
block|{
return|return
name|recoveryBlocked
operator|.
name|getCount
argument_list|()
operator|==
literal|0
return|;
block|}
DECL|method|blockIfNeeded
specifier|private
name|void
name|blockIfNeeded
parameter_list|(
name|RecoveryState
operator|.
name|Stage
name|currentStage
parameter_list|)
block|{
if|if
condition|(
name|currentStage
operator|==
name|stageToBlock
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> blocking recovery on stage [{}]"
argument_list|,
name|currentStage
argument_list|)
expr_stmt|;
name|recoveryBlocked
operator|.
name|countDown
argument_list|()
expr_stmt|;
try|try
block|{
name|releaseRecovery
operator|.
name|await
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> recovery continues from stage [{}]"
argument_list|,
name|currentStage
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"blockage released"
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|indexTranslogOperations
specifier|public
name|void
name|indexTranslogOperations
parameter_list|(
name|List
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|operations
parameter_list|,
name|int
name|totalTranslogOps
parameter_list|)
block|{
if|if
condition|(
name|hasBlocked
argument_list|()
operator|==
literal|false
condition|)
block|{
name|blockIfNeeded
argument_list|(
name|RecoveryState
operator|.
name|Stage
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|indexTranslogOperations
argument_list|(
name|operations
argument_list|,
name|totalTranslogOps
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cleanFiles
specifier|public
name|void
name|cleanFiles
parameter_list|(
name|int
name|totalTranslogOps
parameter_list|,
name|Store
operator|.
name|MetadataSnapshot
name|sourceMetaData
parameter_list|)
throws|throws
name|IOException
block|{
name|blockIfNeeded
argument_list|(
name|RecoveryState
operator|.
name|Stage
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|super
operator|.
name|cleanFiles
argument_list|(
name|totalTranslogOps
argument_list|,
name|sourceMetaData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|finalizeRecovery
specifier|public
name|void
name|finalizeRecovery
parameter_list|(
name|long
name|globalCheckpoint
parameter_list|)
block|{
if|if
condition|(
name|hasBlocked
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// it maybe that not ops have been transferred, block now
name|blockIfNeeded
argument_list|(
name|RecoveryState
operator|.
name|Stage
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
block|}
name|blockIfNeeded
argument_list|(
name|RecoveryState
operator|.
name|Stage
operator|.
name|FINALIZE
argument_list|)
expr_stmt|;
name|super
operator|.
name|finalizeRecovery
argument_list|(
name|globalCheckpoint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

