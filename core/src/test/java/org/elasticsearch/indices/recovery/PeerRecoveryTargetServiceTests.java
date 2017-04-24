begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
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
name|common
operator|.
name|bytes
operator|.
name|BytesArray
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
name|xcontent
operator|.
name|XContentType
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
name|VersionType
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
name|mapper
operator|.
name|SourceToParse
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
name|seqno
operator|.
name|SequenceNumbersService
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
name|IndexShardTestCase
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

begin_class
DECL|class|PeerRecoveryTargetServiceTests
specifier|public
class|class
name|PeerRecoveryTargetServiceTests
extends|extends
name|IndexShardTestCase
block|{
DECL|method|testGetStartingSeqNo
specifier|public
name|void
name|testGetStartingSeqNo
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexShard
name|replica
init|=
name|newShard
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|RecoveryTarget
name|recoveryTarget
init|=
operator|new
name|RecoveryTarget
argument_list|(
name|replica
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|recoveryEmptyReplica
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|int
name|docs
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|String
name|index
init|=
name|replica
operator|.
name|shardId
argument_list|()
operator|.
name|getIndexName
argument_list|()
decl_stmt|;
name|long
name|seqNo
init|=
literal|0
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
name|docs
condition|;
name|i
operator|++
control|)
block|{
name|Engine
operator|.
name|Index
name|indexOp
init|=
name|replica
operator|.
name|prepareIndexOnReplica
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
name|index
argument_list|,
literal|"type"
argument_list|,
literal|"doc_"
operator|+
name|i
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{}"
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|,
name|seqNo
operator|++
argument_list|,
literal|1
argument_list|,
name|VersionType
operator|.
name|EXTERNAL
argument_list|,
name|IndexRequest
operator|.
name|UNSET_AUTO_GENERATED_TIMESTAMP
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|replica
operator|.
name|index
argument_list|(
name|indexOp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
comment|// insert a gap
name|seqNo
operator|++
expr_stmt|;
block|}
block|}
specifier|final
name|long
name|maxSeqNo
init|=
name|replica
operator|.
name|seqNoStats
argument_list|()
operator|.
name|getMaxSeqNo
argument_list|()
decl_stmt|;
specifier|final
name|long
name|localCheckpoint
init|=
name|replica
operator|.
name|getLocalCheckpoint
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|PeerRecoveryTargetService
operator|.
name|getStartingSeqNo
argument_list|(
name|recoveryTarget
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|)
argument_list|)
expr_stmt|;
name|replica
operator|.
name|updateGlobalCheckpointOnReplica
argument_list|(
name|maxSeqNo
operator|-
literal|1
argument_list|)
expr_stmt|;
name|replica
operator|.
name|getTranslog
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
comment|// commit is enough, global checkpoint is below max *committed* which is NO_OPS_PERFORMED
name|assertThat
argument_list|(
name|PeerRecoveryTargetService
operator|.
name|getStartingSeqNo
argument_list|(
name|recoveryTarget
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|replica
operator|.
name|flush
argument_list|(
operator|new
name|FlushRequest
argument_list|()
argument_list|)
expr_stmt|;
comment|// commit is still not good enough, global checkpoint is below max
name|assertThat
argument_list|(
name|PeerRecoveryTargetService
operator|.
name|getStartingSeqNo
argument_list|(
name|recoveryTarget
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
argument_list|)
argument_list|)
expr_stmt|;
name|replica
operator|.
name|updateGlobalCheckpointOnReplica
argument_list|(
name|maxSeqNo
argument_list|)
expr_stmt|;
name|replica
operator|.
name|getTranslog
argument_list|()
operator|.
name|sync
argument_list|()
expr_stmt|;
comment|// commit is enough, global checkpoint is below max
name|assertThat
argument_list|(
name|PeerRecoveryTargetService
operator|.
name|getStartingSeqNo
argument_list|(
name|recoveryTarget
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|localCheckpoint
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeShards
argument_list|(
name|replica
argument_list|)
expr_stmt|;
name|recoveryTarget
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

