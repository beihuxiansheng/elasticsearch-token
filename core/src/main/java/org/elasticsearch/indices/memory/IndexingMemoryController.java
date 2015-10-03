begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.memory
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|memory
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Nullable
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
name|AbstractLifecycleComponent
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|FutureUtils
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
name|EngineClosedException
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
name|EngineConfig
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
name|FlushNotAllowedEngineException
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
name|IndexShardState
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
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmInfo
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
name|*
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

begin_class
DECL|class|IndexingMemoryController
specifier|public
class|class
name|IndexingMemoryController
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|IndexingMemoryController
argument_list|>
block|{
comment|/** How much heap (% or bytes) we will share across all actively indexing shards on this node (default: 10%). */
DECL|field|INDEX_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.index_buffer_size"
decl_stmt|;
comment|/** Only applies when<code>indices.memory.index_buffer_size</code> is a %, to set a floor on the actual size in bytes (default: 48 MB). */
DECL|field|MIN_INDEX_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MIN_INDEX_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.min_index_buffer_size"
decl_stmt|;
comment|/** Only applies when<code>indices.memory.index_buffer_size</code> is a %, to set a ceiling on the actual size in bytes (default: not set). */
DECL|field|MAX_INDEX_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MAX_INDEX_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.max_index_buffer_size"
decl_stmt|;
comment|/** Sets a floor on the per-shard index buffer size (default: 4 MB). */
DECL|field|MIN_SHARD_INDEX_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MIN_SHARD_INDEX_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.min_shard_index_buffer_size"
decl_stmt|;
comment|/** Sets a ceiling on the per-shard index buffer size (default: 512 MB). */
DECL|field|MAX_SHARD_INDEX_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SHARD_INDEX_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.max_shard_index_buffer_size"
decl_stmt|;
comment|/** How much heap (% or bytes) we will share across all actively indexing shards for the translog buffer (default: 1%). */
DECL|field|TRANSLOG_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|TRANSLOG_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.translog_buffer_size"
decl_stmt|;
comment|/** Only applies when<code>indices.memory.translog_buffer_size</code> is a %, to set a floor on the actual size in bytes (default: 256 KB). */
DECL|field|MIN_TRANSLOG_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MIN_TRANSLOG_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.min_translog_buffer_size"
decl_stmt|;
comment|/** Only applies when<code>indices.memory.translog_buffer_size</code> is a %, to set a ceiling on the actual size in bytes (default: not set). */
DECL|field|MAX_TRANSLOG_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MAX_TRANSLOG_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.max_translog_buffer_size"
decl_stmt|;
comment|/** Sets a floor on the per-shard translog buffer size (default: 2 KB). */
DECL|field|MIN_SHARD_TRANSLOG_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MIN_SHARD_TRANSLOG_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.min_shard_translog_buffer_size"
decl_stmt|;
comment|/** Sets a ceiling on the per-shard translog buffer size (default: 64 KB). */
DECL|field|MAX_SHARD_TRANSLOG_BUFFER_SIZE_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SHARD_TRANSLOG_BUFFER_SIZE_SETTING
init|=
literal|"indices.memory.max_shard_translog_buffer_size"
decl_stmt|;
comment|/** If we see no indexing operations after this much time for a given shard, we consider that shard inactive (default: 5 minutes). */
DECL|field|SHARD_INACTIVE_TIME_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_INACTIVE_TIME_SETTING
init|=
literal|"indices.memory.shard_inactive_time"
decl_stmt|;
comment|/** How frequently we check shards to find inactive ones (default: 30 seconds). */
DECL|field|SHARD_INACTIVE_INTERVAL_TIME_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|SHARD_INACTIVE_INTERVAL_TIME_SETTING
init|=
literal|"indices.memory.interval"
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|indexingBuffer
specifier|private
specifier|final
name|ByteSizeValue
name|indexingBuffer
decl_stmt|;
DECL|field|minShardIndexBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|minShardIndexBufferSize
decl_stmt|;
DECL|field|maxShardIndexBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|maxShardIndexBufferSize
decl_stmt|;
DECL|field|translogBuffer
specifier|private
specifier|final
name|ByteSizeValue
name|translogBuffer
decl_stmt|;
DECL|field|minShardTranslogBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|minShardTranslogBufferSize
decl_stmt|;
DECL|field|maxShardTranslogBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|maxShardTranslogBufferSize
decl_stmt|;
DECL|field|inactiveTime
specifier|private
specifier|final
name|TimeValue
name|inactiveTime
decl_stmt|;
DECL|field|interval
specifier|private
specifier|final
name|TimeValue
name|interval
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|volatile
name|ScheduledFuture
name|scheduler
decl_stmt|;
DECL|field|CAN_UPDATE_INDEX_BUFFER_STATES
specifier|private
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|IndexShardState
argument_list|>
name|CAN_UPDATE_INDEX_BUFFER_STATES
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|IndexShardState
operator|.
name|RECOVERING
argument_list|,
name|IndexShardState
operator|.
name|POST_RECOVERY
argument_list|,
name|IndexShardState
operator|.
name|STARTED
argument_list|,
name|IndexShardState
operator|.
name|RELOCATED
argument_list|)
decl_stmt|;
DECL|field|statusChecker
specifier|private
specifier|final
name|ShardsIndicesStatusChecker
name|statusChecker
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndexingMemoryController
specifier|public
name|IndexingMemoryController
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|)
block|{
name|this
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|indicesService
argument_list|,
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|getMem
argument_list|()
operator|.
name|getHeapMax
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// for testing
DECL|method|IndexingMemoryController
specifier|protected
name|IndexingMemoryController
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|IndicesService
name|indicesService
parameter_list|,
name|long
name|jvmMemoryInBytes
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|ByteSizeValue
name|indexingBuffer
decl_stmt|;
name|String
name|indexingBufferSetting
init|=
name|this
operator|.
name|settings
operator|.
name|get
argument_list|(
name|INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|"10%"
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexingBufferSetting
operator|.
name|endsWith
argument_list|(
literal|"%"
argument_list|)
condition|)
block|{
name|double
name|percent
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|indexingBufferSetting
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|indexingBufferSetting
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|indexingBuffer
operator|=
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
operator|(
name|double
operator|)
name|jvmMemoryInBytes
operator|)
operator|*
operator|(
name|percent
operator|/
literal|100
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|ByteSizeValue
name|minIndexingBuffer
init|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MIN_INDEX_BUFFER_SIZE_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|48
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
decl_stmt|;
name|ByteSizeValue
name|maxIndexingBuffer
init|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MAX_INDEX_BUFFER_SIZE_SETTING
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexingBuffer
operator|.
name|bytes
argument_list|()
operator|<
name|minIndexingBuffer
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|indexingBuffer
operator|=
name|minIndexingBuffer
expr_stmt|;
block|}
if|if
condition|(
name|maxIndexingBuffer
operator|!=
literal|null
operator|&&
name|indexingBuffer
operator|.
name|bytes
argument_list|()
operator|>
name|maxIndexingBuffer
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|indexingBuffer
operator|=
name|maxIndexingBuffer
expr_stmt|;
block|}
block|}
else|else
block|{
name|indexingBuffer
operator|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
name|indexingBufferSetting
argument_list|,
name|INDEX_BUFFER_SIZE_SETTING
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indexingBuffer
operator|=
name|indexingBuffer
expr_stmt|;
name|this
operator|.
name|minShardIndexBufferSize
operator|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MIN_SHARD_INDEX_BUFFER_SIZE_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|4
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
expr_stmt|;
comment|// LUCENE MONITOR: Based on this thread, currently (based on Mike), having a large buffer does not make a lot of sense: https://issues.apache.org/jira/browse/LUCENE-2324?focusedCommentId=13005155&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-13005155
name|this
operator|.
name|maxShardIndexBufferSize
operator|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MAX_SHARD_INDEX_BUFFER_SIZE_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|512
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
expr_stmt|;
name|ByteSizeValue
name|translogBuffer
decl_stmt|;
name|String
name|translogBufferSetting
init|=
name|this
operator|.
name|settings
operator|.
name|get
argument_list|(
name|TRANSLOG_BUFFER_SIZE_SETTING
argument_list|,
literal|"1%"
argument_list|)
decl_stmt|;
if|if
condition|(
name|translogBufferSetting
operator|.
name|endsWith
argument_list|(
literal|"%"
argument_list|)
condition|)
block|{
name|double
name|percent
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|translogBufferSetting
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|translogBufferSetting
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|translogBuffer
operator|=
operator|new
name|ByteSizeValue
argument_list|(
call|(
name|long
call|)
argument_list|(
operator|(
operator|(
name|double
operator|)
name|jvmMemoryInBytes
operator|)
operator|*
operator|(
name|percent
operator|/
literal|100
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|ByteSizeValue
name|minTranslogBuffer
init|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MIN_TRANSLOG_BUFFER_SIZE_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|256
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
decl_stmt|;
name|ByteSizeValue
name|maxTranslogBuffer
init|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MAX_TRANSLOG_BUFFER_SIZE_SETTING
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|translogBuffer
operator|.
name|bytes
argument_list|()
operator|<
name|minTranslogBuffer
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|translogBuffer
operator|=
name|minTranslogBuffer
expr_stmt|;
block|}
if|if
condition|(
name|maxTranslogBuffer
operator|!=
literal|null
operator|&&
name|translogBuffer
operator|.
name|bytes
argument_list|()
operator|>
name|maxTranslogBuffer
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|translogBuffer
operator|=
name|maxTranslogBuffer
expr_stmt|;
block|}
block|}
else|else
block|{
name|translogBuffer
operator|=
name|ByteSizeValue
operator|.
name|parseBytesSizeValue
argument_list|(
name|translogBufferSetting
argument_list|,
name|TRANSLOG_BUFFER_SIZE_SETTING
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|translogBuffer
operator|=
name|translogBuffer
expr_stmt|;
name|this
operator|.
name|minShardTranslogBufferSize
operator|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MIN_SHARD_TRANSLOG_BUFFER_SIZE_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|2
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxShardTranslogBufferSize
operator|=
name|this
operator|.
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|MAX_SHARD_TRANSLOG_BUFFER_SIZE_SETTING
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|64
argument_list|,
name|ByteSizeUnit
operator|.
name|KB
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|inactiveTime
operator|=
name|this
operator|.
name|settings
operator|.
name|getAsTime
argument_list|(
name|SHARD_INACTIVE_TIME_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
comment|// we need to have this relatively small to move a shard from inactive to active fast (enough)
name|this
operator|.
name|interval
operator|=
name|this
operator|.
name|settings
operator|.
name|getAsTime
argument_list|(
name|SHARD_INACTIVE_INTERVAL_TIME_SETTING
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|statusChecker
operator|=
operator|new
name|ShardsIndicesStatusChecker
argument_list|()
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using indexing buffer size [{}], with {} [{}], {} [{}], {} [{}], {} [{}]"
argument_list|,
name|this
operator|.
name|indexingBuffer
argument_list|,
name|MIN_SHARD_INDEX_BUFFER_SIZE_SETTING
argument_list|,
name|this
operator|.
name|minShardIndexBufferSize
argument_list|,
name|MAX_SHARD_INDEX_BUFFER_SIZE_SETTING
argument_list|,
name|this
operator|.
name|maxShardIndexBufferSize
argument_list|,
name|SHARD_INACTIVE_TIME_SETTING
argument_list|,
name|this
operator|.
name|inactiveTime
argument_list|,
name|SHARD_INACTIVE_INTERVAL_TIME_SETTING
argument_list|,
name|this
operator|.
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
comment|// it's fine to run it on the scheduler thread, no busy work
name|this
operator|.
name|scheduler
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
name|statusChecker
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|scheduler
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{     }
comment|/**      * returns the current budget for the total amount of indexing buffers of      * active shards on this node      */
DECL|method|indexingBufferSize
specifier|public
name|ByteSizeValue
name|indexingBufferSize
parameter_list|()
block|{
return|return
name|indexingBuffer
return|;
block|}
comment|/**      * returns the current budget for the total amount of translog buffers of      * active shards on this node      */
DECL|method|translogBufferSize
specifier|public
name|ByteSizeValue
name|translogBufferSize
parameter_list|()
block|{
return|return
name|translogBuffer
return|;
block|}
DECL|method|availableShards
specifier|protected
name|List
argument_list|<
name|ShardId
argument_list|>
name|availableShards
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ShardId
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|IndexService
name|indexService
range|:
name|indicesService
control|)
block|{
for|for
control|(
name|IndexShard
name|indexShard
range|:
name|indexService
control|)
block|{
if|if
condition|(
name|shardAvailable
argument_list|(
name|indexShard
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
comment|/** returns true if shard exists and is availabe for updates */
DECL|method|shardAvailable
specifier|protected
name|boolean
name|shardAvailable
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
return|return
name|shardAvailable
argument_list|(
name|getShard
argument_list|(
name|shardId
argument_list|)
argument_list|)
return|;
block|}
comment|/** returns true if shard exists and is availabe for updates */
DECL|method|shardAvailable
specifier|protected
name|boolean
name|shardAvailable
parameter_list|(
annotation|@
name|Nullable
name|IndexShard
name|shard
parameter_list|)
block|{
comment|// shadow replica doesn't have an indexing buffer
return|return
name|shard
operator|!=
literal|null
operator|&&
name|shard
operator|.
name|canIndex
argument_list|()
operator|&&
name|CAN_UPDATE_INDEX_BUFFER_STATES
operator|.
name|contains
argument_list|(
name|shard
operator|.
name|state
argument_list|()
argument_list|)
return|;
block|}
comment|/** gets an {@link IndexShard} instance for the given shard. returns null if the shard doesn't exist */
DECL|method|getShard
specifier|protected
name|IndexShard
name|getShard
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
name|IndexService
name|indexService
init|=
name|indicesService
operator|.
name|indexService
argument_list|(
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexService
operator|!=
literal|null
condition|)
block|{
return|return
name|indexService
operator|.
name|shard
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|updateShardBuffers
specifier|protected
name|void
name|updateShardBuffers
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|ByteSizeValue
name|shardIndexingBufferSize
parameter_list|,
name|ByteSizeValue
name|shardTranslogBufferSize
parameter_list|)
block|{
specifier|final
name|IndexShard
name|shard
init|=
name|getShard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|shard
operator|.
name|updateBufferSize
argument_list|(
name|shardIndexingBufferSize
argument_list|,
name|shardTranslogBufferSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EngineClosedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|FlushNotAllowedEngineException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to set shard {} index buffer to [{}]"
argument_list|,
name|shardId
argument_list|,
name|shardIndexingBufferSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|isShardInactive
specifier|protected
name|boolean
name|isShardInactive
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|inactiveTimeNS
parameter_list|)
block|{
specifier|final
name|IndexShard
name|shard
init|=
name|getShard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|currentTimeInNanos
argument_list|()
operator|-
name|shard
operator|.
name|getLastWriteNS
argument_list|()
operator|>=
name|inactiveTimeNS
return|;
block|}
comment|/** returns the current translog status (generation id + ops) for the given shard id. Returns null if unavailable. */
DECL|method|getShardActive
specifier|protected
name|Boolean
name|getShardActive
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
specifier|final
name|IndexShard
name|indexShard
init|=
name|getShard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexShard
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|indexShard
operator|.
name|getActive
argument_list|()
return|;
block|}
comment|/** Check if any shards active status changed, now. */
DECL|method|forceCheck
specifier|public
name|void
name|forceCheck
parameter_list|()
block|{
name|statusChecker
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
DECL|class|ShardsIndicesStatusChecker
class|class
name|ShardsIndicesStatusChecker
implements|implements
name|Runnable
block|{
comment|// True if the shard was active last time we checked
DECL|field|shardWasActive
specifier|private
specifier|final
name|Map
argument_list|<
name|ShardId
argument_list|,
name|Boolean
argument_list|>
name|shardWasActive
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|run
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|EnumSet
argument_list|<
name|ShardStatusChangeType
argument_list|>
name|changes
init|=
name|purgeDeletedAndClosedShards
argument_list|()
decl_stmt|;
specifier|final
name|int
name|activeShardCount
init|=
name|updateShardStatuses
argument_list|(
name|changes
argument_list|)
decl_stmt|;
if|if
condition|(
name|changes
operator|.
name|isEmpty
argument_list|()
operator|==
literal|false
condition|)
block|{
comment|// Something changed: recompute indexing buffers:
name|calcAndSetShardBuffers
argument_list|(
name|activeShardCount
argument_list|,
literal|"["
operator|+
name|changes
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * goes through all existing shards and check whether the changes their active status          *          * @return the current count of active shards          */
DECL|method|updateShardStatuses
specifier|private
name|int
name|updateShardStatuses
parameter_list|(
name|EnumSet
argument_list|<
name|ShardStatusChangeType
argument_list|>
name|changes
parameter_list|)
block|{
name|int
name|activeShardCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardId
name|shardId
range|:
name|availableShards
argument_list|()
control|)
block|{
comment|// Is the shard active now?
name|Boolean
name|isActive
init|=
name|getShardActive
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|isActive
operator|==
literal|null
condition|)
block|{
comment|// shard was closed..
continue|continue;
block|}
elseif|else
if|if
condition|(
name|isActive
condition|)
block|{
name|activeShardCount
operator|++
expr_stmt|;
block|}
comment|// Was the shard active last time we checked?
name|Boolean
name|wasActive
init|=
name|shardWasActive
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|wasActive
operator|==
literal|null
condition|)
block|{
comment|// First time we are seeing this shard
name|shardWasActive
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|isActive
argument_list|)
expr_stmt|;
name|changes
operator|.
name|add
argument_list|(
name|ShardStatusChangeType
operator|.
name|ADDED
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isActive
condition|)
block|{
comment|// Shard is active now
if|if
condition|(
name|wasActive
operator|==
literal|false
condition|)
block|{
comment|// Shard became active itself, since we last checked (due to new indexing op arriving)
name|changes
operator|.
name|add
argument_list|(
name|ShardStatusChangeType
operator|.
name|BECAME_ACTIVE
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"marking shard {} as active indexing wise"
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|shardWasActive
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|isShardInactive
argument_list|(
name|shardId
argument_list|,
name|inactiveTime
operator|.
name|nanos
argument_list|()
argument_list|)
condition|)
block|{
comment|// Make shard inactive now
name|changes
operator|.
name|add
argument_list|(
name|ShardStatusChangeType
operator|.
name|BECAME_INACTIVE
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"marking shard {} as inactive (inactive_time[{}]) indexing wise"
argument_list|,
name|shardId
argument_list|,
name|inactiveTime
argument_list|)
expr_stmt|;
name|markShardAsInactive
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|shardWasActive
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|activeShardCount
return|;
block|}
comment|/**          * purge any existing statuses that are no longer updated          *          * @return true if any change          */
DECL|method|purgeDeletedAndClosedShards
specifier|private
name|EnumSet
argument_list|<
name|ShardStatusChangeType
argument_list|>
name|purgeDeletedAndClosedShards
parameter_list|()
block|{
name|EnumSet
argument_list|<
name|ShardStatusChangeType
argument_list|>
name|changes
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|ShardStatusChangeType
operator|.
name|class
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|ShardId
argument_list|>
name|statusShardIdIterator
init|=
name|shardWasActive
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|statusShardIdIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ShardId
name|shardId
init|=
name|statusShardIdIterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardAvailable
argument_list|(
name|shardId
argument_list|)
operator|==
literal|false
condition|)
block|{
name|changes
operator|.
name|add
argument_list|(
name|ShardStatusChangeType
operator|.
name|DELETED
argument_list|)
expr_stmt|;
name|statusShardIdIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|changes
return|;
block|}
DECL|method|calcAndSetShardBuffers
specifier|private
name|void
name|calcAndSetShardBuffers
parameter_list|(
name|int
name|activeShardCount
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
comment|// TODO: we could be smarter here by taking into account how RAM the IndexWriter on each shard
comment|// is actually using (using IW.ramBytesUsed), so that small indices (e.g. Marvel) would not
comment|// get the same indexing buffer as large indices.  But it quickly gets tricky...
if|if
condition|(
name|activeShardCount
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"no active shards (reason={})"
argument_list|,
name|reason
argument_list|)
expr_stmt|;
return|return;
block|}
name|ByteSizeValue
name|shardIndexingBufferSize
init|=
operator|new
name|ByteSizeValue
argument_list|(
name|indexingBuffer
operator|.
name|bytes
argument_list|()
operator|/
name|activeShardCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardIndexingBufferSize
operator|.
name|bytes
argument_list|()
operator|<
name|minShardIndexBufferSize
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|shardIndexingBufferSize
operator|=
name|minShardIndexBufferSize
expr_stmt|;
block|}
if|if
condition|(
name|shardIndexingBufferSize
operator|.
name|bytes
argument_list|()
operator|>
name|maxShardIndexBufferSize
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|shardIndexingBufferSize
operator|=
name|maxShardIndexBufferSize
expr_stmt|;
block|}
name|ByteSizeValue
name|shardTranslogBufferSize
init|=
operator|new
name|ByteSizeValue
argument_list|(
name|translogBuffer
operator|.
name|bytes
argument_list|()
operator|/
name|activeShardCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardTranslogBufferSize
operator|.
name|bytes
argument_list|()
operator|<
name|minShardTranslogBufferSize
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|shardTranslogBufferSize
operator|=
name|minShardTranslogBufferSize
expr_stmt|;
block|}
if|if
condition|(
name|shardTranslogBufferSize
operator|.
name|bytes
argument_list|()
operator|>
name|maxShardTranslogBufferSize
operator|.
name|bytes
argument_list|()
condition|)
block|{
name|shardTranslogBufferSize
operator|=
name|maxShardTranslogBufferSize
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"recalculating shard indexing buffer (reason={}), total is [{}] with [{}] active shards, each shard set to indexing=[{}], translog=[{}]"
argument_list|,
name|reason
argument_list|,
name|indexingBuffer
argument_list|,
name|activeShardCount
argument_list|,
name|shardIndexingBufferSize
argument_list|,
name|shardTranslogBufferSize
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardId
name|shardId
range|:
name|availableShards
argument_list|()
control|)
block|{
if|if
condition|(
name|shardWasActive
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
operator|==
name|Boolean
operator|.
name|TRUE
condition|)
block|{
name|updateShardBuffers
argument_list|(
name|shardId
argument_list|,
name|shardIndexingBufferSize
argument_list|,
name|shardTranslogBufferSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|currentTimeInNanos
specifier|protected
name|long
name|currentTimeInNanos
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
return|;
block|}
comment|// update inactive indexing buffer size
DECL|method|markShardAsInactive
specifier|protected
name|void
name|markShardAsInactive
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
name|String
name|ignoreReason
init|=
literal|null
decl_stmt|;
specifier|final
name|IndexShard
name|shard
init|=
name|getShard
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|shard
operator|.
name|markAsInactive
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EngineClosedException
name|e
parameter_list|)
block|{
comment|// ignore
name|ignoreReason
operator|=
literal|"EngineClosedException"
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FlushNotAllowedEngineException
name|e
parameter_list|)
block|{
comment|// ignore
name|ignoreReason
operator|=
literal|"FlushNotAllowedEngineException"
expr_stmt|;
block|}
block|}
else|else
block|{
name|ignoreReason
operator|=
literal|"shard not found"
expr_stmt|;
block|}
if|if
condition|(
name|ignoreReason
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"ignore [{}] while marking shard {} as inactive"
argument_list|,
name|ignoreReason
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|enum|ShardStatusChangeType
specifier|private
specifier|static
enum|enum
name|ShardStatusChangeType
block|{
DECL|enum constant|ADDED
DECL|enum constant|DELETED
DECL|enum constant|BECAME_ACTIVE
DECL|enum constant|BECAME_INACTIVE
name|ADDED
block|,
name|DELETED
block|,
name|BECAME_ACTIVE
block|,
name|BECAME_INACTIVE
block|}
block|}
end_class

end_unit

