begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|CachedStreamOutput
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
name|env
operator|.
name|NodeEnvironment
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|TranslogStreams
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|FsTranslog
specifier|public
class|class
name|FsTranslog
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|Translog
block|{
DECL|field|location
specifier|private
specifier|final
name|File
name|location
decl_stmt|;
DECL|field|useStream
specifier|private
specifier|final
name|boolean
name|useStream
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
DECL|field|syncOnEachOperation
specifier|private
name|boolean
name|syncOnEachOperation
init|=
literal|false
decl_stmt|;
DECL|field|id
specifier|private
specifier|volatile
name|long
name|id
init|=
literal|0
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
name|long
name|lastPosition
init|=
literal|0
decl_stmt|;
DECL|field|raf
specifier|private
name|RafReference
name|raf
decl_stmt|;
DECL|method|FsTranslog
annotation|@
name|Inject
specifier|public
name|FsTranslog
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|NodeEnvironment
name|nodeEnv
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
operator|new
name|File
argument_list|(
name|nodeEnv
operator|.
name|shardLocation
argument_list|(
name|shardId
argument_list|)
argument_list|,
literal|"translog"
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|this
operator|.
name|useStream
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"use_stream"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FsTranslog
specifier|public
name|FsTranslog
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|File
name|location
parameter_list|)
block|{
name|this
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|location
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FsTranslog
specifier|public
name|FsTranslog
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|File
name|location
parameter_list|,
name|boolean
name|useStream
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|this
operator|.
name|location
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|this
operator|.
name|useStream
operator|=
name|useStream
expr_stmt|;
block|}
DECL|method|location
specifier|public
name|File
name|location
parameter_list|()
block|{
return|return
name|location
return|;
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
name|operationCounter
operator|.
name|get
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
literal|0
argument_list|,
name|ByteSizeUnit
operator|.
name|BYTES
argument_list|)
return|;
block|}
DECL|method|clearUnreferenced
annotation|@
name|Override
specifier|public
name|void
name|clearUnreferenced
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|File
index|[]
name|files
init|=
name|location
operator|.
name|listFiles
argument_list|()
decl_stmt|;
if|if
condition|(
name|files
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"translog-"
operator|+
name|id
argument_list|)
condition|)
block|{
continue|continue;
block|}
try|try
block|{
name|file
operator|.
name|delete
argument_list|()
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
block|}
block|}
DECL|method|newTranslog
annotation|@
name|Override
specifier|public
name|void
name|newTranslog
parameter_list|()
throws|throws
name|TranslogException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|operationCounter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|raf
operator|!=
literal|null
condition|)
block|{
name|raf
operator|.
name|decreaseRefCount
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|raf
operator|=
operator|new
name|RafReference
argument_list|(
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"translog-"
operator|+
name|id
argument_list|)
argument_list|)
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|raf
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|TranslogException
argument_list|(
name|shardId
argument_list|,
literal|"translog not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|method|newTranslog
annotation|@
name|Override
specifier|public
name|void
name|newTranslog
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|TranslogException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|operationCounter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|lastPosition
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
if|if
condition|(
name|raf
operator|!=
literal|null
condition|)
block|{
name|raf
operator|.
name|decreaseRefCount
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|raf
operator|=
operator|new
name|RafReference
argument_list|(
operator|new
name|File
argument_list|(
name|location
argument_list|,
literal|"translog-"
operator|+
name|id
argument_list|)
argument_list|)
expr_stmt|;
comment|// clean the file if it exists
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|raf
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|TranslogException
argument_list|(
name|shardId
argument_list|,
literal|"translog not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|BytesStreamOutput
name|out
init|=
name|CachedStreamOutput
operator|.
name|cachedBytes
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// marker for the size...
name|TranslogStreams
operator|.
name|writeTranslogOperation
argument_list|(
name|out
argument_list|,
name|operation
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|out
operator|.
name|size
argument_list|()
decl_stmt|;
name|out
operator|.
name|seek
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|size
operator|-
literal|4
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|raf
operator|.
name|raf
argument_list|()
operator|.
name|write
argument_list|(
name|out
operator|.
name|unsafeByteArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
if|if
condition|(
name|syncOnEachOperation
condition|)
block|{
name|sync
argument_list|()
expr_stmt|;
block|}
name|lastPosition
operator|+=
name|size
expr_stmt|;
name|operationCounter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
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
literal|"Failed to write operation ["
operator|+
name|operation
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|snapshot
annotation|@
name|Override
specifier|public
name|Snapshot
name|snapshot
parameter_list|()
throws|throws
name|TranslogException
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
try|try
block|{
name|raf
operator|.
name|increaseRefCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|useStream
condition|)
block|{
return|return
operator|new
name|FsStreamSnapshot
argument_list|(
name|shardId
argument_list|,
name|this
operator|.
name|id
argument_list|,
name|raf
argument_list|,
name|lastPosition
argument_list|,
name|operationCounter
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
else|else
block|{
return|return
operator|new
name|FsChannelSnapshot
argument_list|(
name|shardId
argument_list|,
name|this
operator|.
name|id
argument_list|,
name|raf
argument_list|,
name|lastPosition
argument_list|,
name|operationCounter
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
try|try
block|{
name|raf
operator|.
name|increaseRefCount
argument_list|()
expr_stmt|;
if|if
condition|(
name|useStream
condition|)
block|{
name|FsStreamSnapshot
name|newSnapshot
init|=
operator|new
name|FsStreamSnapshot
argument_list|(
name|shardId
argument_list|,
name|id
argument_list|,
name|raf
argument_list|,
name|lastPosition
argument_list|,
name|operationCounter
operator|.
name|get
argument_list|()
argument_list|,
name|operationCounter
operator|.
name|get
argument_list|()
operator|-
name|snapshot
operator|.
name|totalOperations
argument_list|()
argument_list|)
decl_stmt|;
name|newSnapshot
operator|.
name|seekForward
argument_list|(
name|snapshot
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newSnapshot
return|;
block|}
else|else
block|{
name|FsChannelSnapshot
name|newSnapshot
init|=
operator|new
name|FsChannelSnapshot
argument_list|(
name|shardId
argument_list|,
name|id
argument_list|,
name|raf
argument_list|,
name|lastPosition
argument_list|,
name|operationCounter
operator|.
name|get
argument_list|()
argument_list|,
name|operationCounter
operator|.
name|get
argument_list|()
operator|-
name|snapshot
operator|.
name|totalOperations
argument_list|()
argument_list|)
decl_stmt|;
name|newSnapshot
operator|.
name|seekForward
argument_list|(
name|snapshot
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newSnapshot
return|;
block|}
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
block|}
DECL|method|sync
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|()
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|raf
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|raf
operator|.
name|raf
argument_list|()
operator|.
name|getFD
argument_list|()
operator|.
name|sync
argument_list|()
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
block|}
DECL|method|syncOnEachOperation
annotation|@
name|Override
specifier|public
name|void
name|syncOnEachOperation
parameter_list|(
name|boolean
name|syncOnEachOperation
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|this
operator|.
name|syncOnEachOperation
operator|=
name|syncOnEachOperation
expr_stmt|;
block|}
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
if|if
condition|(
name|raf
operator|!=
literal|null
condition|)
block|{
name|raf
operator|.
name|decreaseRefCount
argument_list|(
name|delete
argument_list|)
expr_stmt|;
name|raf
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

