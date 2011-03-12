begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|component
operator|.
name|AbstractComponent
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexingMemoryBufferController
specifier|public
class|class
name|IndexingMemoryBufferController
extends|extends
name|AbstractComponent
block|{
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
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
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
DECL|method|IndexingMemoryBufferController
annotation|@
name|Inject
specifier|public
name|IndexingMemoryBufferController
parameter_list|(
name|Settings
name|settings
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
name|logger
operator|.
name|debug
argument_list|(
literal|"using index_buffer_size [{}], with min_shard_index_buffer_size [{}], max_shard_index_buffer_size [{}]"
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
argument_list|)
expr_stmt|;
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
block|}
DECL|class|Listener
specifier|private
class|class
name|Listener
extends|extends
name|IndicesLifecycle
operator|.
name|Listener
block|{
DECL|method|afterIndexShardCreated
annotation|@
name|Override
specifier|public
name|void
name|afterIndexShardCreated
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|)
block|{
name|calcAndSetShardIndexingBuffer
argument_list|(
literal|"created_shard["
operator|+
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
operator|+
literal|"]["
operator|+
name|indexShard
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|afterIndexShardClosed
annotation|@
name|Override
specifier|public
name|void
name|afterIndexShardClosed
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|boolean
name|delete
parameter_list|)
block|{
name|calcAndSetShardIndexingBuffer
argument_list|(
literal|"removed_shard["
operator|+
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"]["
operator|+
name|shardId
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|calcAndSetShardIndexingBuffer
specifier|private
name|void
name|calcAndSetShardIndexingBuffer
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
name|calcShardIndexingBuffer
argument_list|(
name|shardsCount
argument_list|)
decl_stmt|;
if|if
condition|(
name|shardIndexingBufferSize
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|logger
operator|.
name|debug
argument_list|(
literal|"recalculating shard indexing buffer (reason={}), total is [{}] with [{}] shards, each shard set to [{}]"
argument_list|,
name|reason
argument_list|,
name|indexingBuffer
argument_list|,
name|shardsCount
argument_list|,
name|shardIndexingBufferSize
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
block|}
block|}
block|}
DECL|method|calcShardIndexingBuffer
specifier|private
name|ByteSizeValue
name|calcShardIndexingBuffer
parameter_list|(
name|int
name|shardsCount
parameter_list|)
block|{
return|return
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
return|;
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
name|shardsCount
operator|++
expr_stmt|;
block|}
block|}
return|return
name|shardsCount
return|;
block|}
block|}
block|}
end_class

end_unit

