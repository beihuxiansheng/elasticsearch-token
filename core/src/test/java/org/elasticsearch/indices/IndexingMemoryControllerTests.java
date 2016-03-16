begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|DirectoryReader
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
name|forcemerge
operator|.
name|ForceMergeResponse
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
name|unit
operator|.
name|ByteSizeUnit
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
name|unit
operator|.
name|ByteSizeValue
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
name|unit
operator|.
name|TimeValue
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
name|test
operator|.
name|ESSingleNodeTestCase
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
name|Collections
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
name|HashSet
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
name|ScheduledFuture
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
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
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertNoFailures
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
DECL|class|IndexingMemoryControllerTests
specifier|public
class|class
name|IndexingMemoryControllerTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|class|MockController
specifier|static
class|class
name|MockController
extends|extends
name|IndexingMemoryController
block|{
comment|// Size of each shard's indexing buffer
DECL|field|indexBufferRAMBytesUsed
specifier|final
name|Map
argument_list|<
name|IndexShard
argument_list|,
name|Long
argument_list|>
name|indexBufferRAMBytesUsed
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// How many bytes this shard is currently moving to disk
DECL|field|writingBytes
specifier|final
name|Map
argument_list|<
name|IndexShard
argument_list|,
name|Long
argument_list|>
name|writingBytes
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Shards that are currently throttled
DECL|field|throttled
specifier|final
name|Set
argument_list|<
name|IndexShard
argument_list|>
name|throttled
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|MockController
specifier|public
name|MockController
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SHARD_MEMORY_INTERVAL_TIME_SETTING
argument_list|,
literal|"200h"
argument_list|)
comment|// disable it
operator|.
name|put
argument_list|(
name|settings
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|100
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// fix jvm mem size to 100mb
block|}
DECL|method|deleteShard
specifier|public
name|void
name|deleteShard
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|indexBufferRAMBytesUsed
operator|.
name|remove
argument_list|(
name|shard
argument_list|)
expr_stmt|;
name|writingBytes
operator|.
name|remove
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|availableShards
specifier|protected
name|List
argument_list|<
name|IndexShard
argument_list|>
name|availableShards
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|indexBufferRAMBytesUsed
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexBufferRAMBytesUsed
specifier|protected
name|long
name|getIndexBufferRAMBytesUsed
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
return|return
name|indexBufferRAMBytesUsed
operator|.
name|get
argument_list|(
name|shard
argument_list|)
operator|+
name|writingBytes
operator|.
name|get
argument_list|(
name|shard
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getShardWritingBytes
specifier|protected
name|long
name|getShardWritingBytes
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|Long
name|bytes
init|=
name|writingBytes
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
return|return
name|bytes
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|checkIdle
specifier|protected
name|void
name|checkIdle
parameter_list|(
name|IndexShard
name|shard
parameter_list|,
name|long
name|inactiveTimeNS
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|writeIndexingBufferAsync
specifier|public
name|void
name|writeIndexingBufferAsync
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|long
name|bytes
init|=
name|indexBufferRAMBytesUsed
operator|.
name|put
argument_list|(
name|shard
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|writingBytes
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|writingBytes
operator|.
name|get
argument_list|(
name|shard
argument_list|)
operator|+
name|bytes
argument_list|)
expr_stmt|;
name|indexBufferRAMBytesUsed
operator|.
name|put
argument_list|(
name|shard
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|activateThrottling
specifier|public
name|void
name|activateThrottling
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|throttled
operator|.
name|add
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|deactivateThrottling
specifier|public
name|void
name|deactivateThrottling
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|throttled
operator|.
name|remove
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doneWriting
specifier|public
name|void
name|doneWriting
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|writingBytes
operator|.
name|put
argument_list|(
name|shard
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
DECL|method|assertBuffer
specifier|public
name|void
name|assertBuffer
parameter_list|(
name|IndexShard
name|shard
parameter_list|,
name|int
name|expectedMB
parameter_list|)
block|{
name|Long
name|actual
init|=
name|indexBufferRAMBytesUsed
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|actual
operator|=
literal|0L
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedMB
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
name|actual
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|assertThrottled
specifier|public
name|void
name|assertThrottled
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|throttled
operator|.
name|contains
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotThrottled
specifier|public
name|void
name|assertNotThrottled
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|assertFalse
argument_list|(
name|throttled
operator|.
name|contains
argument_list|(
name|shard
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertWriting
specifier|public
name|void
name|assertWriting
parameter_list|(
name|IndexShard
name|shard
parameter_list|,
name|int
name|expectedMB
parameter_list|)
block|{
name|Long
name|actual
init|=
name|writingBytes
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|actual
operator|==
literal|null
condition|)
block|{
name|actual
operator|=
literal|0L
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedMB
operator|*
literal|1024
operator|*
literal|1024
argument_list|,
name|actual
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|simulateIndexing
specifier|public
name|void
name|simulateIndexing
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
name|Long
name|bytes
init|=
name|indexBufferRAMBytesUsed
operator|.
name|get
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|=
literal|0L
expr_stmt|;
comment|// First time we are seeing this shard:
name|writingBytes
operator|.
name|put
argument_list|(
name|shard
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|// Each doc we index takes up a megabyte!
name|bytes
operator|+=
literal|1024
operator|*
literal|1024
expr_stmt|;
name|indexBufferRAMBytesUsed
operator|.
name|put
argument_list|(
name|shard
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|forceCheck
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|scheduleTask
specifier|protected
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleTask
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|testShardAdditionAndRemoval
specifier|public
name|void
name|testShardAdditionAndRemoval
parameter_list|()
block|{
name|createIndex
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
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexService
name|test
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|MockController
name|controller
init|=
operator|new
name|MockController
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"4mb"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|shard0
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// add another shard
name|IndexShard
name|shard1
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// remove first shard
name|controller
operator|.
name|deleteShard
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|forceCheck
argument_list|()
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// remove second shard
name|controller
operator|.
name|deleteShard
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|forceCheck
argument_list|()
expr_stmt|;
comment|// add a new one
name|IndexShard
name|shard2
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard2
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testActiveInactive
specifier|public
name|void
name|testActiveInactive
parameter_list|()
block|{
name|createIndex
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
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|2
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexService
name|test
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|MockController
name|controller
init|=
operator|new
name|MockController
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"5mb"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|shard0
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|IndexShard
name|shard1
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// index into one shard only, crosses the 5mb limit, so shard1 is refreshed
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
comment|// shard1 crossed 5 mb and is now cleared:
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinBufferSizes
specifier|public
name|void
name|testMinBufferSizes
parameter_list|()
block|{
name|MockController
name|controller
init|=
operator|new
name|MockController
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"0.001%"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|MIN_INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"6mb"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|controller
operator|.
name|indexingBufferSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|6
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxBufferSizes
specifier|public
name|void
name|testMaxBufferSizes
parameter_list|()
block|{
name|MockController
name|controller
init|=
operator|new
name|MockController
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"90%"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|MAX_INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"6mb"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|controller
operator|.
name|indexingBufferSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|6
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testThrottling
specifier|public
name|void
name|testThrottling
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
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
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|3
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexService
name|test
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"test"
argument_list|)
argument_list|)
decl_stmt|;
name|MockController
name|controller
init|=
operator|new
name|MockController
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"4mb"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|IndexShard
name|shard0
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|IndexShard
name|shard1
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|IndexShard
name|shard2
init|=
name|test
operator|.
name|getShard
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
comment|// We are now using 5 MB, so we should be writing shard0 since it's using the most heap:
name|controller
operator|.
name|assertWriting
argument_list|(
name|shard0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertWriting
argument_list|(
name|shard1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
comment|// Now we are still writing 3 MB (shard0), and using 5 MB index buffers, so we should now 1) be writing shard1, and 2) be throttling shard1:
name|controller
operator|.
name|assertWriting
argument_list|(
name|shard0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertWriting
argument_list|(
name|shard1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertNotThrottled
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertThrottled
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> Indexing more data"
argument_list|)
expr_stmt|;
comment|// More indexing to shard0
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|simulateIndexing
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
comment|// Now we are using 5 MB again, so shard0 should also be writing and now also be throttled:
name|controller
operator|.
name|assertWriting
argument_list|(
name|shard0
argument_list|,
literal|8
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertWriting
argument_list|(
name|shard1
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertBuffer
argument_list|(
name|shard1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertThrottled
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertThrottled
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
comment|// Both shards finally finish writing, and throttling should stop:
name|controller
operator|.
name|doneWriting
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|doneWriting
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
name|controller
operator|.
name|forceCheck
argument_list|()
expr_stmt|;
name|controller
operator|.
name|assertNotThrottled
argument_list|(
name|shard0
argument_list|)
expr_stmt|;
name|controller
operator|.
name|assertNotThrottled
argument_list|(
name|shard1
argument_list|)
expr_stmt|;
block|}
comment|// #10312
DECL|method|testDeletesAloneCanTriggerRefresh
specifier|public
name|void
name|testDeletesAloneCanTriggerRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|0
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|resolveIndex
argument_list|(
literal|"index"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexShard
name|shard
init|=
name|indexService
operator|.
name|getShardOrNull
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|shard
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
name|id
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
comment|// Force merge so we know all merges are done before we start deleting:
name|ForceMergeResponse
name|r
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareForceMerge
argument_list|()
operator|.
name|setMaxNumSegments
argument_list|(
literal|1
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertNoFailures
argument_list|(
name|r
argument_list|)
expr_stmt|;
comment|// Make a shell of an IMC to check up on indexing buffer usage:
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexingMemoryController
operator|.
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"1kb"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// TODO: would be cleaner if I could pass this 1kb setting to the single node this test created....
name|IndexingMemoryController
name|imc
init|=
operator|new
name|IndexingMemoryController
argument_list|(
name|settings
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|List
argument_list|<
name|IndexShard
argument_list|>
name|availableShards
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|shard
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|long
name|getIndexBufferRAMBytesUsed
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
return|return
name|shard
operator|.
name|getIndexBufferRAMBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|writeIndexingBufferAsync
parameter_list|(
name|IndexShard
name|shard
parameter_list|)
block|{
comment|// just do it sync'd for this test
name|shard
operator|.
name|writeIndexingBuffer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|scheduleTask
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|String
name|id
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
name|id
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|indexingBufferBytes1
init|=
name|shard
operator|.
name|getIndexBufferRAMBytesUsed
argument_list|()
decl_stmt|;
name|imc
operator|.
name|forceCheck
argument_list|()
expr_stmt|;
comment|// We must assertBusy because the writeIndexingBufferAsync is done in background (REFRESH) thread pool:
name|assertBusy
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
init|(
name|Engine
operator|.
name|Searcher
name|s2
init|=
name|shard
operator|.
name|acquireSearcher
argument_list|(
literal|"index"
argument_list|)
init|)
block|{
comment|// 100 buffered deletes will easily exceed our 1 KB indexing buffer so it should trigger a write:
specifier|final
name|long
name|indexingBufferBytes2
init|=
name|shard
operator|.
name|getIndexBufferRAMBytesUsed
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|indexingBufferBytes2
operator|<
name|indexingBufferBytes1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

