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
name|ElasticsearchException
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
name|util
operator|.
name|concurrent
operator|.
name|AbstractRunnable
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
name|test
operator|.
name|ESTestCase
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
name|IndexSettingsModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Set
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
name|CyclicBarrier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|IntStream
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
name|isOneOf
import|;
end_import

begin_class
DECL|class|LocalCheckpointServiceTests
specifier|public
class|class
name|LocalCheckpointServiceTests
extends|extends
name|ESTestCase
block|{
DECL|field|checkpointService
specifier|private
name|LocalCheckpointService
name|checkpointService
decl_stmt|;
DECL|field|SMALL_CHUNK_SIZE
specifier|private
specifier|final
name|int
name|SMALL_CHUNK_SIZE
init|=
literal|4
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|checkpointService
operator|=
name|getCheckpointService
argument_list|()
expr_stmt|;
block|}
DECL|method|getCheckpointService
specifier|private
name|LocalCheckpointService
name|getCheckpointService
parameter_list|()
block|{
return|return
operator|new
name|LocalCheckpointService
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"test"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|LocalCheckpointService
operator|.
name|SETTINGS_BIT_ARRAYS_SIZE
operator|.
name|getKey
argument_list|()
argument_list|,
name|SMALL_CHUNK_SIZE
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testSimplePrimary
specifier|public
name|void
name|testSimplePrimary
parameter_list|()
block|{
name|long
name|seqNo1
decl_stmt|,
name|seqNo2
decl_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
argument_list|)
argument_list|)
expr_stmt|;
name|seqNo1
operator|=
name|checkpointService
operator|.
name|generateSeqNo
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|seqNo1
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|seqNo1
operator|=
name|checkpointService
operator|.
name|generateSeqNo
argument_list|()
expr_stmt|;
name|seqNo2
operator|=
name|checkpointService
operator|.
name|generateSeqNo
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|seqNo1
argument_list|,
name|equalTo
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|seqNo2
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleReplica
specifier|public
name|void
name|testSimpleReplica
parameter_list|()
block|{
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSimpleOverFlow
specifier|public
name|void
name|testSimpleOverFlow
parameter_list|()
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|seqNoList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|aligned
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxOps
init|=
name|SMALL_CHUNK_SIZE
operator|*
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|)
operator|+
operator|(
name|aligned
condition|?
literal|0
else|:
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|SMALL_CHUNK_SIZE
operator|-
literal|1
argument_list|)
operator|)
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
name|maxOps
condition|;
name|i
operator|++
control|)
block|{
name|seqNoList
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|shuffle
argument_list|(
name|seqNoList
argument_list|,
name|random
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Integer
name|seqNo
range|:
name|seqNoList
control|)
block|{
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|checkpoint
argument_list|,
name|equalTo
argument_list|(
name|maxOps
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|processedSeqNo
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|aligned
condition|?
literal|0
else|:
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|firstProcessedSeqNo
argument_list|,
name|equalTo
argument_list|(
operator|(
operator|(
name|long
operator|)
name|maxOps
operator|/
name|SMALL_CHUNK_SIZE
operator|)
operator|*
name|SMALL_CHUNK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcurrentPrimary
specifier|public
name|void
name|testConcurrentPrimary
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|opsPerThread
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxOps
init|=
name|opsPerThread
operator|*
name|threads
operator|.
name|length
decl_stmt|;
specifier|final
name|long
name|unFinishedSeq
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|maxOps
operator|-
literal|2
argument_list|)
decl_stmt|;
comment|// make sure we always index the last seqNo to simplify maxSeq checks
name|logger
operator|.
name|info
argument_list|(
literal|"--> will run [{}] threads, maxOps [{}], unfinished seq no [{}]"
argument_list|,
name|threads
operator|.
name|length
argument_list|,
name|maxOps
argument_list|,
name|unFinishedSeq
argument_list|)
expr_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|threads
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|threads
operator|.
name|length
condition|;
name|t
operator|++
control|)
block|{
specifier|final
name|int
name|threadId
init|=
name|t
decl_stmt|;
name|threads
index|[
name|t
index|]
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failure in background thread"
argument_list|,
name|t
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|barrier
operator|.
name|await
argument_list|()
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
name|opsPerThread
condition|;
name|i
operator|++
control|)
block|{
name|long
name|seqNo
init|=
name|checkpointService
operator|.
name|generateSeqNo
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"[t{}] started   [{}]"
argument_list|,
name|threadId
argument_list|,
name|seqNo
argument_list|)
expr_stmt|;
if|if
condition|(
name|seqNo
operator|!=
name|unFinishedSeq
condition|)
block|{
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"[t{}] completed [{}]"
argument_list|,
name|threadId
argument_list|,
name|seqNo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
literal|"testConcurrentPrimary_"
operator|+
name|threadId
argument_list|)
expr_stmt|;
name|threads
index|[
name|t
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getMaxSeqNo
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|maxOps
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|unFinishedSeq
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|unFinishedSeq
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|maxOps
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|processedSeqNo
operator|.
name|size
argument_list|()
argument_list|,
name|isOneOf
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|firstProcessedSeqNo
argument_list|,
name|equalTo
argument_list|(
operator|(
operator|(
name|long
operator|)
name|maxOps
operator|/
name|SMALL_CHUNK_SIZE
operator|)
operator|*
name|SMALL_CHUNK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testConcurrentReplica
specifier|public
name|void
name|testConcurrentReplica
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|randomIntBetween
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|opsPerThread
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|int
name|maxOps
init|=
name|opsPerThread
operator|*
name|threads
operator|.
name|length
decl_stmt|;
specifier|final
name|long
name|unFinishedSeq
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|maxOps
operator|-
literal|2
argument_list|)
decl_stmt|;
comment|// make sure we always index the last seqNo to simplify maxSeq checks
name|Set
argument_list|<
name|Integer
argument_list|>
name|seqNos
init|=
name|IntStream
operator|.
name|range
argument_list|(
literal|0
argument_list|,
name|maxOps
argument_list|)
operator|.
name|boxed
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Integer
index|[]
index|[]
name|seqNoPerThread
init|=
operator|new
name|Integer
index|[
name|threads
operator|.
name|length
index|]
index|[]
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|threads
operator|.
name|length
operator|-
literal|1
condition|;
name|t
operator|++
control|)
block|{
name|int
name|size
init|=
name|Math
operator|.
name|min
argument_list|(
name|seqNos
operator|.
name|size
argument_list|()
argument_list|,
name|randomIntBetween
argument_list|(
name|opsPerThread
operator|-
literal|4
argument_list|,
name|opsPerThread
operator|+
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|seqNoPerThread
index|[
name|t
index|]
operator|=
name|randomSubsetOf
argument_list|(
name|size
argument_list|,
name|seqNos
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|Integer
index|[
name|size
index|]
argument_list|)
expr_stmt|;
name|seqNos
operator|.
name|removeAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|seqNoPerThread
index|[
name|t
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|seqNoPerThread
index|[
name|threads
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|seqNos
operator|.
name|toArray
argument_list|(
operator|new
name|Integer
index|[
name|seqNos
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> will run [{}] threads, maxOps [{}], unfinished seq no [{}]"
argument_list|,
name|threads
operator|.
name|length
argument_list|,
name|maxOps
argument_list|,
name|unFinishedSeq
argument_list|)
expr_stmt|;
specifier|final
name|CyclicBarrier
name|barrier
init|=
operator|new
name|CyclicBarrier
argument_list|(
name|threads
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
name|threads
operator|.
name|length
condition|;
name|t
operator|++
control|)
block|{
specifier|final
name|int
name|threadId
init|=
name|t
decl_stmt|;
name|threads
index|[
name|t
index|]
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failure in background thread"
argument_list|,
name|t
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|Integer
index|[]
name|ops
init|=
name|seqNoPerThread
index|[
name|threadId
index|]
decl_stmt|;
for|for
control|(
name|int
name|seqNo
range|:
name|ops
control|)
block|{
if|if
condition|(
name|seqNo
operator|!=
name|unFinishedSeq
condition|)
block|{
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|seqNo
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"[t{}] completed [{}]"
argument_list|,
name|threadId
argument_list|,
name|seqNo
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
literal|"testConcurrentReplica_"
operator|+
name|threadId
argument_list|)
expr_stmt|;
name|threads
index|[
name|t
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|thread
range|:
name|threads
control|)
block|{
name|thread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getMaxSeqNo
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|maxOps
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|unFinishedSeq
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|checkpointService
operator|.
name|markSeqNoAsCompleted
argument_list|(
name|unFinishedSeq
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|maxOps
operator|-
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkpointService
operator|.
name|firstProcessedSeqNo
argument_list|,
name|equalTo
argument_list|(
operator|(
operator|(
name|long
operator|)
name|maxOps
operator|/
name|SMALL_CHUNK_SIZE
operator|)
operator|*
name|SMALL_CHUNK_SIZE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

