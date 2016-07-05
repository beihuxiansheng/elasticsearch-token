begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.flush
package|package
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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectIntHashMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectIntMap
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
name|SyncedFlushResponse
operator|.
name|ShardCounts
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
name|routing
operator|.
name|ShardRoutingState
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
name|TestShardRouting
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
name|BytesStreamOutput
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
name|flush
operator|.
name|ShardsSyncedFlushResult
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
name|flush
operator|.
name|SyncedFlushService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|XContentTestUtils
operator|.
name|convertToMap
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
name|hasSize
import|;
end_import

begin_class
DECL|class|SyncedFlushUnitTests
specifier|public
class|class
name|SyncedFlushUnitTests
extends|extends
name|ESTestCase
block|{
DECL|class|TestPlan
specifier|private
specifier|static
class|class
name|TestPlan
block|{
DECL|field|totalCounts
specifier|public
name|SyncedFlushResponse
operator|.
name|ShardCounts
name|totalCounts
decl_stmt|;
DECL|field|countsPerIndex
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SyncedFlushResponse
operator|.
name|ShardCounts
argument_list|>
name|countsPerIndex
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|expectedFailuresPerIndex
specifier|public
name|ObjectIntMap
argument_list|<
name|String
argument_list|>
name|expectedFailuresPerIndex
init|=
operator|new
name|ObjectIntHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|result
specifier|public
name|SyncedFlushResponse
name|result
decl_stmt|;
block|}
DECL|method|testIndicesSyncedFlushResult
specifier|public
name|void
name|testIndicesSyncedFlushResult
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|TestPlan
name|testPlan
init|=
name|createTestPlan
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|totalShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|successful
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|failedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|failed
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|restStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|failed
operator|>
literal|0
condition|?
name|RestStatus
operator|.
name|CONFLICT
else|:
name|RestStatus
operator|.
name|OK
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|asMap
init|=
name|convertToMap
argument_list|(
name|testPlan
operator|.
name|result
argument_list|)
decl_stmt|;
name|assertShardCount
argument_list|(
literal|"_shards header"
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|asMap
operator|.
name|get
argument_list|(
literal|"_shards"
argument_list|)
argument_list|,
name|testPlan
operator|.
name|totalCounts
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"unexpected number of indices"
argument_list|,
name|asMap
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
operator|+
name|testPlan
operator|.
name|countsPerIndex
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// +1 for the shards header
for|for
control|(
name|String
name|index
range|:
name|testPlan
operator|.
name|countsPerIndex
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|indexMap
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|asMap
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|assertShardCount
argument_list|(
name|index
argument_list|,
name|indexMap
argument_list|,
name|testPlan
operator|.
name|countsPerIndex
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|failureList
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|indexMap
operator|.
name|get
argument_list|(
literal|"failures"
argument_list|)
decl_stmt|;
specifier|final
name|int
name|expectedFailures
init|=
name|testPlan
operator|.
name|expectedFailuresPerIndex
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|expectedFailures
operator|==
literal|0
condition|)
block|{
name|assertNull
argument_list|(
name|index
operator|+
literal|" has unexpected failures"
argument_list|,
name|failureList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNotNull
argument_list|(
name|index
operator|+
literal|" should have failures"
argument_list|,
name|failureList
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|failureList
argument_list|,
name|hasSize
argument_list|(
name|expectedFailures
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|testResponseStreaming
specifier|public
name|void
name|testResponseStreaming
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|TestPlan
name|testPlan
init|=
name|createTestPlan
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|totalShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|successful
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|failedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|failed
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|restStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|failed
operator|>
literal|0
condition|?
name|RestStatus
operator|.
name|CONFLICT
else|:
name|RestStatus
operator|.
name|OK
argument_list|)
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|testPlan
operator|.
name|result
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|out
operator|.
name|bytes
argument_list|()
operator|.
name|streamInput
argument_list|()
decl_stmt|;
name|SyncedFlushResponse
name|readResponse
init|=
operator|new
name|SyncedFlushResponse
argument_list|()
decl_stmt|;
name|readResponse
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readResponse
operator|.
name|totalShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readResponse
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|successful
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readResponse
operator|.
name|failedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|failed
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readResponse
operator|.
name|restStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|totalCounts
operator|.
name|failed
operator|>
literal|0
condition|?
name|RestStatus
operator|.
name|CONFLICT
else|:
name|RestStatus
operator|.
name|OK
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|readResponse
operator|.
name|shardsResultPerIndex
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|testPlan
operator|.
name|result
operator|.
name|getShardsResultPerIndex
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
argument_list|>
name|entry
range|:
name|readResponse
operator|.
name|getShardsResultPerIndex
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|List
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|originalShardsResults
init|=
name|testPlan
operator|.
name|result
operator|.
name|getShardsResultPerIndex
argument_list|()
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|originalShardsResults
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|readShardsResults
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|readShardsResults
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|originalShardsResults
operator|.
name|size
argument_list|()
argument_list|)
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
name|readShardsResults
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ShardsSyncedFlushResult
name|originalShardResult
init|=
name|originalShardsResults
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ShardsSyncedFlushResult
name|readShardResult
init|=
name|readShardsResults
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|failureReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|failureReason
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|failed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|failed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|getShardId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|getShardId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|successfulShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|successfulShards
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|syncId
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|syncId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|totalShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|totalShards
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|failedShards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|failedShards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
argument_list|>
name|shardEntry
range|:
name|originalShardResult
operator|.
name|failedShards
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
name|readShardResponse
init|=
name|readShardResult
operator|.
name|failedShards
argument_list|()
operator|.
name|get
argument_list|(
name|shardEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|readShardResponse
argument_list|)
expr_stmt|;
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
name|originalShardResponse
init|=
name|shardEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|originalShardResponse
operator|.
name|failureReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResponse
operator|.
name|failureReason
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResponse
operator|.
name|success
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResponse
operator|.
name|success
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|originalShardResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
argument_list|>
name|shardEntry
range|:
name|originalShardResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
name|readShardResponse
init|=
name|readShardResult
operator|.
name|shardResponses
argument_list|()
operator|.
name|get
argument_list|(
name|shardEntry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|readShardResponse
argument_list|)
expr_stmt|;
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
name|originalShardResponse
init|=
name|shardEntry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|originalShardResponse
operator|.
name|failureReason
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResponse
operator|.
name|failureReason
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|originalShardResponse
operator|.
name|success
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|readShardResponse
operator|.
name|success
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|assertShardCount
specifier|private
name|void
name|assertShardCount
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|header
parameter_list|,
name|ShardCounts
name|expectedCounts
parameter_list|)
block|{
name|assertThat
argument_list|(
name|name
operator|+
literal|" has unexpected total count"
argument_list|,
operator|(
name|Integer
operator|)
name|header
operator|.
name|get
argument_list|(
literal|"total"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedCounts
operator|.
name|total
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|name
operator|+
literal|" has unexpected successful count"
argument_list|,
operator|(
name|Integer
operator|)
name|header
operator|.
name|get
argument_list|(
literal|"successful"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedCounts
operator|.
name|successful
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|name
operator|+
literal|" has unexpected failed count"
argument_list|,
operator|(
name|Integer
operator|)
name|header
operator|.
name|get
argument_list|(
literal|"failed"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expectedCounts
operator|.
name|failed
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createTestPlan
specifier|protected
name|TestPlan
name|createTestPlan
parameter_list|()
block|{
specifier|final
name|TestPlan
name|testPlan
init|=
operator|new
name|TestPlan
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
argument_list|>
name|indicesResults
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|indexCount
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|totalShards
init|=
literal|0
decl_stmt|;
name|int
name|totalSuccesful
init|=
literal|0
decl_stmt|;
name|int
name|totalFailed
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
name|indexCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|index
init|=
literal|"index_"
operator|+
name|i
decl_stmt|;
name|int
name|shards
init|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|int
name|replicas
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|int
name|successful
init|=
literal|0
decl_stmt|;
name|int
name|failed
init|=
literal|0
decl_stmt|;
name|int
name|failures
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|ShardsSyncedFlushResult
argument_list|>
name|shardsResults
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|shard
init|=
literal|0
init|;
name|shard
operator|<
name|shards
condition|;
name|shard
operator|++
control|)
block|{
specifier|final
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
literal|"_na_"
argument_list|,
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomInt
argument_list|(
literal|5
argument_list|)
operator|<
literal|2
condition|)
block|{
comment|// total shard failure
name|failed
operator|+=
name|replicas
operator|+
literal|1
expr_stmt|;
name|failures
operator|++
expr_stmt|;
name|shardsResults
operator|.
name|add
argument_list|(
operator|new
name|ShardsSyncedFlushResult
argument_list|(
name|shardId
argument_list|,
name|replicas
operator|+
literal|1
argument_list|,
literal|"simulated total failure"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
argument_list|>
name|shardResponses
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|copy
init|=
literal|0
init|;
name|copy
operator|<
name|replicas
operator|+
literal|1
condition|;
name|copy
operator|++
control|)
block|{
specifier|final
name|ShardRouting
name|shardRouting
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|index
argument_list|,
name|shard
argument_list|,
literal|"node_"
operator|+
name|shardId
operator|+
literal|"_"
operator|+
name|copy
argument_list|,
literal|null
argument_list|,
name|copy
operator|==
literal|0
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
decl_stmt|;
if|if
condition|(
name|randomInt
argument_list|(
literal|5
argument_list|)
operator|<
literal|2
condition|)
block|{
comment|// shard copy failure
name|failed
operator|++
expr_stmt|;
name|failures
operator|++
expr_stmt|;
name|shardResponses
operator|.
name|put
argument_list|(
name|shardRouting
argument_list|,
operator|new
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
argument_list|(
literal|"copy failure "
operator|+
name|shardId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|successful
operator|++
expr_stmt|;
name|shardResponses
operator|.
name|put
argument_list|(
name|shardRouting
argument_list|,
operator|new
name|SyncedFlushService
operator|.
name|ShardSyncedFlushResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|shardsResults
operator|.
name|add
argument_list|(
operator|new
name|ShardsSyncedFlushResult
argument_list|(
name|shardId
argument_list|,
literal|"_sync_id_"
operator|+
name|shard
argument_list|,
name|replicas
operator|+
literal|1
argument_list|,
name|shardResponses
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|indicesResults
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|shardsResults
argument_list|)
expr_stmt|;
name|testPlan
operator|.
name|countsPerIndex
operator|.
name|put
argument_list|(
name|index
argument_list|,
operator|new
name|SyncedFlushResponse
operator|.
name|ShardCounts
argument_list|(
name|shards
operator|*
operator|(
name|replicas
operator|+
literal|1
operator|)
argument_list|,
name|successful
argument_list|,
name|failed
argument_list|)
argument_list|)
expr_stmt|;
name|testPlan
operator|.
name|expectedFailuresPerIndex
operator|.
name|put
argument_list|(
name|index
argument_list|,
name|failures
argument_list|)
expr_stmt|;
name|totalFailed
operator|+=
name|failed
expr_stmt|;
name|totalShards
operator|+=
name|shards
operator|*
operator|(
name|replicas
operator|+
literal|1
operator|)
expr_stmt|;
name|totalSuccesful
operator|+=
name|successful
expr_stmt|;
block|}
name|testPlan
operator|.
name|result
operator|=
operator|new
name|SyncedFlushResponse
argument_list|(
name|indicesResults
argument_list|)
expr_stmt|;
name|testPlan
operator|.
name|totalCounts
operator|=
operator|new
name|SyncedFlushResponse
operator|.
name|ShardCounts
argument_list|(
name|totalShards
argument_list|,
name|totalSuccesful
argument_list|,
name|totalFailed
argument_list|)
expr_stmt|;
return|return
name|testPlan
return|;
block|}
block|}
end_class

end_unit

