begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.seal
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
name|seal
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
name|routing
operator|.
name|ImmutableShardRouting
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
name|SyncedFlushService
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
name|ElasticsearchTestCase
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
name|*
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

begin_class
DECL|class|SealIndicesTests
specifier|public
class|class
name|SealIndicesTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|testSealIndicesResponseStreaming
specifier|public
name|void
name|testSealIndicesResponseStreaming
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|>
name|shardResults
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// add one result where one shard failed and one succeeded
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|syncedFlushResult
init|=
name|createSyncedFlushResult
argument_list|(
literal|0
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|shardResults
operator|.
name|add
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
comment|// add one result where all failed
name|syncedFlushResult
operator|=
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"all failed :("
argument_list|)
expr_stmt|;
name|shardResults
operator|.
name|add
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|SealIndicesResponse
name|sealIndicesResponse
init|=
operator|new
name|SealIndicesResponse
argument_list|(
name|shardResults
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|sealIndicesResponse
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|SealIndicesResponse
name|readResponse
init|=
operator|new
name|SealIndicesResponse
argument_list|()
decl_stmt|;
name|readResponse
operator|.
name|readFrom
argument_list|(
name|in
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
name|readResponse
argument_list|)
decl_stmt|;
name|assertResponse
argument_list|(
name|asMap
argument_list|)
expr_stmt|;
block|}
DECL|method|testXContentResponse
specifier|public
name|void
name|testXContentResponse
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|>
name|shardResults
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// add one result where one shard failed and one succeeded
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|syncedFlushResult
init|=
name|createSyncedFlushResult
argument_list|(
literal|0
argument_list|,
literal|"test"
argument_list|)
decl_stmt|;
name|shardResults
operator|.
name|add
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
comment|// add one result where all failed
name|syncedFlushResult
operator|=
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|"all failed :("
argument_list|)
expr_stmt|;
name|shardResults
operator|.
name|add
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
name|SealIndicesResponse
name|sealIndicesResponse
init|=
operator|new
name|SealIndicesResponse
argument_list|(
name|shardResults
argument_list|)
decl_stmt|;
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
name|sealIndicesResponse
argument_list|)
decl_stmt|;
name|assertResponse
argument_list|(
name|asMap
argument_list|)
expr_stmt|;
block|}
DECL|method|assertResponse
specifier|protected
name|void
name|assertResponse
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|asMap
parameter_list|)
block|{
name|assertNotNull
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|Integer
call|)
argument_list|(
operator|(
call|(
name|HashMap
call|)
argument_list|(
operator|(
name|ArrayList
operator|)
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"shard_id"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
operator|(
call|(
name|HashMap
call|)
argument_list|(
operator|(
name|ArrayList
operator|)
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"message"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"failed on some copies"
argument_list|)
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|shardResponses
init|=
call|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
call|)
argument_list|(
call|(
name|HashMap
call|)
argument_list|(
operator|(
name|ArrayList
operator|)
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
literal|"responses"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|shardResponses
operator|.
name|get
argument_list|(
literal|"node_1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"failed for some reason"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardResponses
operator|.
name|get
argument_list|(
literal|"node_2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"success"
argument_list|)
argument_list|)
expr_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|failedShard
init|=
call|(
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
call|)
argument_list|(
operator|(
operator|(
name|ArrayList
operator|)
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
call|(
name|Integer
call|)
argument_list|(
name|failedShard
operator|.
name|get
argument_list|(
literal|"shard_id"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
call|(
name|String
call|)
argument_list|(
name|failedShard
operator|.
name|get
argument_list|(
literal|"message"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"all failed :("
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testXContentResponseSortsShards
specifier|public
name|void
name|testXContentResponseSortsShards
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|>
name|shardResults
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
comment|// add one result where one shard failed and one succeeded
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|syncedFlushResult
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|100000
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|syncedFlushResult
operator|=
name|createSyncedFlushResult
argument_list|(
name|i
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|shardResults
operator|.
name|add
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|syncedFlushResult
operator|=
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
name|i
argument_list|)
argument_list|,
literal|"all failed :("
argument_list|)
expr_stmt|;
name|shardResults
operator|.
name|add
argument_list|(
name|syncedFlushResult
argument_list|)
expr_stmt|;
block|}
block|}
name|SealIndicesResponse
name|sealIndicesResponse
init|=
operator|new
name|SealIndicesResponse
argument_list|(
name|shardResults
argument_list|)
decl_stmt|;
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
name|sealIndicesResponse
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
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
literal|100000
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
call|(
name|Integer
call|)
argument_list|(
operator|(
call|(
name|HashMap
call|)
argument_list|(
operator|(
name|ArrayList
operator|)
name|asMap
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|"shard_id"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createSyncedFlushResult
specifier|protected
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|createSyncedFlushResult
parameter_list|(
name|int
name|shardId
parameter_list|,
name|String
name|index
parameter_list|)
block|{
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|responses
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|ImmutableShardRouting
name|shardRouting
init|=
operator|new
name|ImmutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|"node_1"
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|RELOCATING
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
name|syncedFlushResponse
init|=
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|(
literal|"failed for some reason"
argument_list|)
decl_stmt|;
name|responses
operator|.
name|put
argument_list|(
name|shardRouting
argument_list|,
name|syncedFlushResponse
argument_list|)
expr_stmt|;
name|shardRouting
operator|=
operator|new
name|ImmutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|"node_2"
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|RELOCATING
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|syncedFlushResponse
operator|=
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|()
expr_stmt|;
name|responses
operator|.
name|put
argument_list|(
name|shardRouting
argument_list|,
name|syncedFlushResponse
argument_list|)
expr_stmt|;
return|return
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|,
literal|"some_sync_id"
argument_list|,
name|responses
argument_list|)
return|;
block|}
block|}
end_class

end_unit

