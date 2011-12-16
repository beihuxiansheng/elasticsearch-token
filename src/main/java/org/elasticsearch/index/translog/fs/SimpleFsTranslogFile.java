begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog.fs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|fs
package|;
end_package

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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|AtomicInteger
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

begin_class
DECL|class|SimpleFsTranslogFile
specifier|public
class|class
name|SimpleFsTranslogFile
implements|implements
name|FsTranslogFile
block|{
DECL|field|id
specifier|private
specifier|final
name|long
name|id
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|raf
specifier|private
specifier|final
name|RafReference
name|raf
decl_stmt|;
DECL|field|operationCounter
specifier|private
specifier|final
name|AtomicInteger
name|operationCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|lastPosition
specifier|private
specifier|final
name|AtomicLong
name|lastPosition
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|lastWrittenPosition
specifier|private
specifier|final
name|AtomicLong
name|lastWrittenPosition
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|lastSyncPosition
specifier|private
specifier|volatile
name|long
name|lastSyncPosition
init|=
literal|0
decl_stmt|;
DECL|method|SimpleFsTranslogFile
specifier|public
name|SimpleFsTranslogFile
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|id
parameter_list|,
name|RafReference
name|raf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|raf
operator|=
name|raf
expr_stmt|;
name|raf
operator|.
name|raf
argument_list|()
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|long
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|estimatedNumberOfOperations
specifier|public
name|int
name|estimatedNumberOfOperations
parameter_list|()
block|{
return|return
name|operationCounter
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|translogSizeInBytes
specifier|public
name|long
name|translogSizeInBytes
parameter_list|()
block|{
return|return
name|lastWrittenPosition
operator|.
name|get
argument_list|()
return|;
block|}
DECL|method|add
specifier|public
name|Translog
operator|.
name|Location
name|add
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|from
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|position
init|=
name|lastPosition
operator|.
name|getAndAdd
argument_list|(
name|size
argument_list|)
decl_stmt|;
name|raf
operator|.
name|channel
argument_list|()
operator|.
name|write
argument_list|(
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|size
argument_list|)
argument_list|,
name|position
argument_list|)
expr_stmt|;
name|lastWrittenPosition
operator|.
name|getAndAdd
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|operationCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
return|return
operator|new
name|Translog
operator|.
name|Location
argument_list|(
name|id
argument_list|,
name|position
argument_list|,
name|size
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|byte
index|[]
name|read
parameter_list|(
name|Translog
operator|.
name|Location
name|location
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteBuffer
name|buffer
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|location
operator|.
name|size
argument_list|)
decl_stmt|;
name|raf
operator|.
name|channel
argument_list|()
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|location
operator|.
name|translogLocation
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|array
argument_list|()
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
name|raf
operator|.
name|decreaseRefCount
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a snapshot on this file,<tt>null</tt> if it failed to snapshot.      */
DECL|method|snapshot
specifier|public
name|FsChannelSnapshot
name|snapshot
parameter_list|()
throws|throws
name|TranslogException
block|{
try|try
block|{
if|if
condition|(
operator|!
name|raf
operator|.
name|increaseRefCount
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|FsChannelSnapshot
argument_list|(
name|this
operator|.
name|id
argument_list|,
name|raf
argument_list|,
name|lastWrittenPosition
operator|.
name|get
argument_list|()
argument_list|,
name|operationCounter
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TranslogException
argument_list|(
name|shardId
argument_list|,
literal|"Failed to snapshot"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|()
block|{
try|try
block|{
comment|// check if we really need to sync here...
name|long
name|last
init|=
name|lastWrittenPosition
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|last
operator|==
name|lastSyncPosition
condition|)
block|{
return|return;
block|}
name|lastSyncPosition
operator|=
name|last
expr_stmt|;
name|raf
operator|.
name|channel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
end_class

end_unit

