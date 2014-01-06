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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

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
name|service
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
name|service
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
name|service
operator|.
name|InternalIndexShard
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
name|IndicesLifecycle
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
name|concurrent
operator|.
name|ScheduledFuture
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

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
DECL|field|shardsCreatedOrDeleted
specifier|private
specifier|final
name|AtomicBoolean
name|shardsCreatedOrDeleted
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|Listener
name|listener
init|=
operator|new
name|Listener
argument_list|()
decl_stmt|;
DECL|field|shardsIndicesStatus
specifier|private
specifier|final
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardIndexingStatus
argument_list|>
name|shardsIndicesStatus
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|scheduler
specifier|private
specifier|volatile
name|ScheduledFuture
name|scheduler
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
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
name|componentSettings
operator|.
name|get
argument_list|(
literal|"index_buffer_size"
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
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|mem
argument_list|()
operator|.
name|heapMax
argument_list|()
operator|.
name|bytes
argument_list|()
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"min_index_buffer_size"
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_index_buffer_size"
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
literal|null
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"min_shard_index_buffer_size"
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_shard_index_buffer_size"
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
name|componentSettings
operator|.
name|get
argument_list|(
literal|"translog_buffer_size"
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
name|JvmInfo
operator|.
name|jvmInfo
argument_list|()
operator|.
name|mem
argument_list|()
operator|.
name|heapMax
argument_list|()
operator|.
name|bytes
argument_list|()
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"min_translog_buffer_size"
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_translog_buffer_size"
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
literal|null
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"min_shard_translog_buffer_size"
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
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_shard_translog_buffer_size"
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
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"shard_inactive_time"
argument_list|,
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
comment|// we need to have this relatively small to move a shard from inactive to active fast (enough)
name|this
operator|.
name|interval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using index_buffer_size [{}], with min_shard_index_buffer_size [{}], max_shard_index_buffer_size [{}], shard_inactive_time [{}]"
argument_list|,
name|this
operator|.
name|indexingBuffer
argument_list|,
name|this
operator|.
name|minShardIndexBufferSize
argument_list|,
name|this
operator|.
name|maxShardIndexBufferSize
argument_list|,
name|this
operator|.
name|inactiveTime
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
throws|throws
name|ElasticsearchException
block|{
name|indicesService
operator|.
name|indicesLifecycle
argument_list|()
operator|.
name|addListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
comment|// its fine to run it on the scheduler thread, no busy work
name|this
operator|.
name|scheduler
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
operator|new
name|ShardsIndicesStatusChecker
argument_list|()
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
throws|throws
name|ElasticsearchException
block|{
name|indicesService
operator|.
name|indicesLifecycle
argument_list|()
operator|.
name|removeListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
if|if
condition|(
name|scheduler
operator|!=
literal|null
condition|)
block|{
name|scheduler
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|scheduler
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticsearchException
block|{     }
DECL|class|ShardsIndicesStatusChecker
class|class
name|ShardsIndicesStatusChecker
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|boolean
name|activeInactiveStatusChanges
init|=
literal|false
decl_stmt|;
name|List
argument_list|<
name|IndexShard
argument_list|>
name|activeToInactiveIndexingShards
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|IndexShard
argument_list|>
name|inactiveToActiveIndexingShards
init|=
name|Lists
operator|.
name|newArrayList
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
name|long
name|time
init|=
name|threadPool
operator|.
name|estimatedTimeInMillis
argument_list|()
decl_stmt|;
name|Translog
name|translog
init|=
operator|(
operator|(
name|InternalIndexShard
operator|)
name|indexShard
operator|)
operator|.
name|translog
argument_list|()
decl_stmt|;
name|ShardIndexingStatus
name|status
init|=
name|shardsIndicesStatus
operator|.
name|get
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
comment|// not added yet
continue|continue;
block|}
comment|// check if it is deemed to be inactive (sam translogId and numberOfOperations over a long period of time)
if|if
condition|(
name|status
operator|.
name|translogId
operator|==
name|translog
operator|.
name|currentId
argument_list|()
operator|&&
name|translog
operator|.
name|estimatedNumberOfOperations
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|status
operator|.
name|time
operator|==
operator|-
literal|1
condition|)
block|{
comment|// first time
name|status
operator|.
name|time
operator|=
name|time
expr_stmt|;
block|}
comment|// inactive?
if|if
condition|(
operator|!
name|status
operator|.
name|inactiveIndexing
condition|)
block|{
comment|// mark it as inactive only if enough time has passed and there are no ongoing merges going on...
if|if
condition|(
operator|(
name|time
operator|-
name|status
operator|.
name|time
operator|)
operator|>
name|inactiveTime
operator|.
name|millis
argument_list|()
operator|&&
name|indexShard
operator|.
name|mergeStats
argument_list|()
operator|.
name|getCurrent
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// inactive for this amount of time, mark it
name|activeToInactiveIndexingShards
operator|.
name|add
argument_list|(
name|indexShard
argument_list|)
expr_stmt|;
name|status
operator|.
name|inactiveIndexing
operator|=
literal|true
expr_stmt|;
name|activeInactiveStatusChanges
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"marking shard [{}][{}] as inactive (inactive_time[{}]) indexing wise, setting size to [{}]"
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|inactiveTime
argument_list|,
name|Engine
operator|.
name|INACTIVE_SHARD_INDEXING_BUFFER
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|status
operator|.
name|inactiveIndexing
condition|)
block|{
name|inactiveToActiveIndexingShards
operator|.
name|add
argument_list|(
name|indexShard
argument_list|)
expr_stmt|;
name|status
operator|.
name|inactiveIndexing
operator|=
literal|false
expr_stmt|;
name|activeInactiveStatusChanges
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"marking shard [{}][{}] as active indexing wise"
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|status
operator|.
name|time
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|status
operator|.
name|translogId
operator|=
name|translog
operator|.
name|currentId
argument_list|()
expr_stmt|;
name|status
operator|.
name|translogNumberOfOperations
operator|=
name|translog
operator|.
name|estimatedNumberOfOperations
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
name|IndexShard
name|indexShard
range|:
name|activeToInactiveIndexingShards
control|)
block|{
comment|// update inactive indexing buffer size
try|try
block|{
operator|(
operator|(
name|InternalIndexShard
operator|)
name|indexShard
operator|)
operator|.
name|engine
argument_list|()
operator|.
name|updateIndexingBufferSize
argument_list|(
name|Engine
operator|.
name|INACTIVE_SHARD_INDEXING_BUFFER
argument_list|)
expr_stmt|;
operator|(
operator|(
name|InternalIndexShard
operator|)
name|indexShard
operator|)
operator|.
name|translog
argument_list|()
operator|.
name|updateBuffer
argument_list|(
name|Translog
operator|.
name|INACTIVE_SHARD_TRANSLOG_BUFFER
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
block|}
name|boolean
name|shardsCreatedOrDeleted
init|=
name|IndexingMemoryController
operator|.
name|this
operator|.
name|shardsCreatedOrDeleted
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardsCreatedOrDeleted
operator|||
name|activeInactiveStatusChanges
condition|)
block|{
name|calcAndSetShardBuffers
argument_list|(
literal|"active/inactive["
operator|+
name|activeInactiveStatusChanges
operator|+
literal|"] created/deleted["
operator|+
name|shardsCreatedOrDeleted
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|Listener
class|class
name|Listener
extends|extends
name|IndicesLifecycle
operator|.
name|Listener
block|{
annotation|@
name|Override
DECL|method|afterIndexShardCreated
specifier|public
name|void
name|afterIndexShardCreated
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|shardsIndicesStatus
operator|.
name|put
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|,
operator|new
name|ShardIndexingStatus
argument_list|()
argument_list|)
expr_stmt|;
name|shardsCreatedOrDeleted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|afterIndexShardClosed
specifier|public
name|void
name|afterIndexShardClosed
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|shardsIndicesStatus
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|shardsCreatedOrDeleted
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|calcAndSetShardBuffers
specifier|private
name|void
name|calcAndSetShardBuffers
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
name|int
name|shardsCount
init|=
name|countShards
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardsCount
operator|==
literal|0
condition|)
block|{
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
name|shardsCount
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
name|shardsCount
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
name|shardsCount
argument_list|,
name|shardIndexingBufferSize
argument_list|,
name|shardTranslogBufferSize
argument_list|)
expr_stmt|;
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
name|ShardIndexingStatus
name|status
init|=
name|shardsIndicesStatus
operator|.
name|get
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
operator|||
operator|!
name|status
operator|.
name|inactiveIndexing
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|InternalIndexShard
operator|)
name|indexShard
operator|)
operator|.
name|engine
argument_list|()
operator|.
name|updateIndexingBufferSize
argument_list|(
name|shardIndexingBufferSize
argument_list|)
expr_stmt|;
operator|(
operator|(
name|InternalIndexShard
operator|)
name|indexShard
operator|)
operator|.
name|translog
argument_list|()
operator|.
name|updateBuffer
argument_list|(
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
continue|continue;
block|}
catch|catch
parameter_list|(
name|FlushNotAllowedEngineException
name|e
parameter_list|)
block|{
comment|// ignore
continue|continue;
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
literal|"failed to set shard [{}][{}] index buffer to [{}]"
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|shardIndexingBufferSize
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|countShards
specifier|private
name|int
name|countShards
parameter_list|()
block|{
name|int
name|shardsCount
init|=
literal|0
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
name|ShardIndexingStatus
name|status
init|=
name|shardsIndicesStatus
operator|.
name|get
argument_list|(
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
operator|||
operator|!
name|status
operator|.
name|inactiveIndexing
condition|)
block|{
name|shardsCount
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|shardsCount
return|;
block|}
DECL|class|ShardIndexingStatus
specifier|static
class|class
name|ShardIndexingStatus
block|{
DECL|field|translogId
name|long
name|translogId
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|translogNumberOfOperations
name|int
name|translogNumberOfOperations
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|inactiveIndexing
name|boolean
name|inactiveIndexing
init|=
literal|false
decl_stmt|;
DECL|field|time
name|long
name|time
init|=
operator|-
literal|1
decl_stmt|;
comment|// contains the first time we saw this shard with no operations done on it
block|}
block|}
end_class

end_unit

