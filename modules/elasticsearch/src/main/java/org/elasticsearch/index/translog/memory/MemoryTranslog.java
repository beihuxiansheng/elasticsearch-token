begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog.memory
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadSafe
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
name|jsr166y
operator|.
name|LinkedTransferQueue
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
name|settings
operator|.
name|IndexSettings
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
name|AbstractIndexShardComponent
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
name|index
operator|.
name|translog
operator|.
name|TranslogException
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
name|Queue
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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
annotation|@
name|ThreadSafe
DECL|class|MemoryTranslog
specifier|public
class|class
name|MemoryTranslog
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|Translog
block|{
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
DECL|field|idGenerator
specifier|private
specifier|final
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|estimatedMemorySize
specifier|private
specifier|final
name|AtomicLong
name|estimatedMemorySize
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|id
specifier|private
specifier|volatile
name|long
name|id
decl_stmt|;
comment|// we use LinkedBlockingQueue and not LinkedTransferQueue since we clear it on #newTranslog
comment|// and with LinkedTransferQueue, nodes are not really cleared, just marked causing for memory
comment|// not to be cleaned properly (besides, clear is  heavy..., "while ... poll").
DECL|field|operations
specifier|private
specifier|volatile
name|Queue
argument_list|<
name|Operation
argument_list|>
name|operations
decl_stmt|;
DECL|method|MemoryTranslog
annotation|@
name|Inject
specifier|public
name|MemoryTranslog
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|newTranslog
argument_list|()
expr_stmt|;
block|}
DECL|method|currentId
annotation|@
name|Override
specifier|public
name|long
name|currentId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|size
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|operations
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|estimateMemorySize
annotation|@
name|Override
specifier|public
name|ByteSizeValue
name|estimateMemorySize
parameter_list|()
block|{
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|estimatedMemorySize
operator|.
name|get
argument_list|()
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
return|;
block|}
DECL|method|newTranslog
annotation|@
name|Override
specifier|public
name|void
name|newTranslog
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|estimatedMemorySize
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|operations
operator|=
operator|new
name|LinkedTransferQueue
argument_list|<
name|Operation
argument_list|>
argument_list|()
expr_stmt|;
name|id
operator|=
name|idGenerator
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|add
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|Operation
name|operation
parameter_list|)
throws|throws
name|TranslogException
block|{
name|operations
operator|.
name|add
argument_list|(
name|operation
argument_list|)
expr_stmt|;
name|estimatedMemorySize
operator|.
name|addAndGet
argument_list|(
name|operation
operator|.
name|estimateSize
argument_list|()
operator|+
literal|50
argument_list|)
expr_stmt|;
block|}
DECL|method|snapshot
annotation|@
name|Override
specifier|public
name|Snapshot
name|snapshot
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
return|return
operator|new
name|MemorySnapshot
argument_list|(
name|currentId
argument_list|()
argument_list|,
name|operations
operator|.
name|toArray
argument_list|(
operator|new
name|Operation
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|snapshot
annotation|@
name|Override
specifier|public
name|Snapshot
name|snapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|MemorySnapshot
name|memorySnapshot
init|=
operator|(
name|MemorySnapshot
operator|)
name|snapshot
decl_stmt|;
if|if
condition|(
name|currentId
argument_list|()
operator|!=
name|snapshot
operator|.
name|translogId
argument_list|()
condition|)
block|{
return|return
name|snapshot
argument_list|()
return|;
block|}
name|ArrayList
argument_list|<
name|Operation
argument_list|>
name|retVal
init|=
operator|new
name|ArrayList
argument_list|<
name|Operation
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|counter
init|=
literal|0
decl_stmt|;
name|int
name|snapshotSize
init|=
name|memorySnapshot
operator|.
name|operations
operator|.
name|length
decl_stmt|;
for|for
control|(
name|Operation
name|operation
range|:
name|operations
control|)
block|{
if|if
condition|(
operator|++
name|counter
operator|>
name|snapshotSize
condition|)
block|{
name|retVal
operator|.
name|add
argument_list|(
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MemorySnapshot
argument_list|(
name|currentId
argument_list|()
argument_list|,
name|retVal
operator|.
name|toArray
argument_list|(
operator|new
name|Operation
index|[
name|retVal
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{     }
block|}
end_class

end_unit

