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
name|FileSystemUtils
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
name|util
operator|.
name|concurrent
operator|.
name|jsr166y
operator|.
name|ThreadLocalRandom
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
name|nio
operator|.
name|channels
operator|.
name|ClosedChannelException
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
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
DECL|field|rwl
specifier|private
specifier|final
name|ReadWriteLock
name|rwl
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|locations
specifier|private
specifier|final
name|File
index|[]
name|locations
decl_stmt|;
DECL|field|current
specifier|private
specifier|volatile
name|FsTranslogFile
name|current
decl_stmt|;
DECL|field|trans
specifier|private
specifier|volatile
name|FsTranslogFile
name|trans
decl_stmt|;
DECL|field|syncOnEachOperation
specifier|private
name|boolean
name|syncOnEachOperation
init|=
literal|false
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
name|File
index|[]
name|shardLocations
init|=
name|nodeEnv
operator|.
name|shardLocations
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
name|this
operator|.
name|locations
operator|=
operator|new
name|File
index|[
name|shardLocations
operator|.
name|length
index|]
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
name|shardLocations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|locations
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|shardLocations
index|[
name|i
index|]
argument_list|,
literal|"translog"
argument_list|)
expr_stmt|;
name|FileSystemUtils
operator|.
name|mkdirs
argument_list|(
name|locations
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
name|super
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|locations
operator|=
operator|new
name|File
index|[]
block|{
name|location
block|}
expr_stmt|;
name|FileSystemUtils
operator|.
name|mkdirs
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
DECL|method|locations
specifier|public
name|File
index|[]
name|locations
parameter_list|()
block|{
return|return
name|locations
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
name|FsTranslogFile
name|current1
init|=
name|this
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|current1
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|current1
operator|.
name|id
argument_list|()
return|;
block|}
DECL|method|estimatedNumberOfOperations
annotation|@
name|Override
specifier|public
name|int
name|estimatedNumberOfOperations
parameter_list|()
block|{
name|FsTranslogFile
name|current1
init|=
name|this
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|current1
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|current1
operator|.
name|estimatedNumberOfOperations
argument_list|()
return|;
block|}
DECL|method|memorySizeInBytes
annotation|@
name|Override
specifier|public
name|long
name|memorySizeInBytes
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
DECL|method|translogSizeInBytes
annotation|@
name|Override
specifier|public
name|long
name|translogSizeInBytes
parameter_list|()
block|{
name|FsTranslogFile
name|current1
init|=
name|this
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|current1
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|current1
operator|.
name|translogSizeInBytes
argument_list|()
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
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
for|for
control|(
name|File
name|location
range|:
name|locations
control|)
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
name|current
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|trans
operator|!=
literal|null
operator|&&
name|file
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"translog-"
operator|+
name|trans
operator|.
name|id
argument_list|()
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
finally|finally
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
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
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|FsTranslogFile
name|newFile
decl_stmt|;
name|long
name|size
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|File
name|location
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|locations
control|)
block|{
name|long
name|currentFree
init|=
name|file
operator|.
name|getFreeSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentFree
operator|<
name|size
condition|)
block|{
name|size
operator|=
name|currentFree
expr_stmt|;
name|location
operator|=
name|file
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentFree
operator|==
name|size
operator|&&
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|location
operator|=
name|file
expr_stmt|;
block|}
block|}
try|try
block|{
name|newFile
operator|=
operator|new
name|FsTranslogFile
argument_list|(
name|shardId
argument_list|,
name|id
argument_list|,
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
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TranslogException
argument_list|(
name|shardId
argument_list|,
literal|"failed to create new translog file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|FsTranslogFile
name|old
init|=
name|current
decl_stmt|;
name|current
operator|=
name|newFile
expr_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
comment|// we might create a new translog overriding the current translog id
name|boolean
name|delete
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|old
operator|.
name|id
argument_list|()
operator|==
name|id
condition|)
block|{
name|delete
operator|=
literal|false
expr_stmt|;
block|}
name|old
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|newTransientTranslog
annotation|@
name|Override
specifier|public
name|void
name|newTransientTranslog
parameter_list|(
name|long
name|id
parameter_list|)
throws|throws
name|TranslogException
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
assert|assert
name|this
operator|.
name|trans
operator|==
literal|null
assert|;
name|long
name|size
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
name|File
name|location
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|locations
control|)
block|{
name|long
name|currentFree
init|=
name|file
operator|.
name|getFreeSpace
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentFree
operator|<
name|size
condition|)
block|{
name|size
operator|=
name|currentFree
expr_stmt|;
name|location
operator|=
name|file
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentFree
operator|==
name|size
operator|&&
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|location
operator|=
name|file
expr_stmt|;
block|}
block|}
name|this
operator|.
name|trans
operator|=
operator|new
name|FsTranslogFile
argument_list|(
name|shardId
argument_list|,
name|id
argument_list|,
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
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TranslogException
argument_list|(
name|shardId
argument_list|,
literal|"failed to create new translog file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|makeTransientCurrent
annotation|@
name|Override
specifier|public
name|void
name|makeTransientCurrent
parameter_list|()
block|{
name|FsTranslogFile
name|old
decl_stmt|;
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
assert|assert
name|this
operator|.
name|trans
operator|!=
literal|null
assert|;
name|old
operator|=
name|current
expr_stmt|;
name|this
operator|.
name|current
operator|=
name|this
operator|.
name|trans
expr_stmt|;
name|this
operator|.
name|trans
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|old
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|revertTransient
annotation|@
name|Override
specifier|public
name|void
name|revertTransient
parameter_list|()
block|{
name|FsTranslogFile
name|old
decl_stmt|;
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|old
operator|=
name|trans
expr_stmt|;
name|this
operator|.
name|trans
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|old
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|byte
index|[]
name|read
parameter_list|(
name|Location
name|location
parameter_list|)
block|{
name|rwl
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|FsTranslogFile
name|trans
init|=
name|this
operator|.
name|trans
decl_stmt|;
if|if
condition|(
name|trans
operator|!=
literal|null
operator|&&
name|trans
operator|.
name|id
argument_list|()
operator|==
name|location
operator|.
name|translogId
condition|)
block|{
try|try
block|{
return|return
name|trans
operator|.
name|read
argument_list|(
name|location
argument_list|)
return|;
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
if|if
condition|(
name|current
operator|.
name|id
argument_list|()
operator|==
name|location
operator|.
name|translogId
condition|)
block|{
try|try
block|{
return|return
name|current
operator|.
name|read
argument_list|(
name|location
argument_list|)
return|;
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
return|return
literal|null
return|;
block|}
finally|finally
block|{
name|rwl
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|add
annotation|@
name|Override
specifier|public
name|Location
name|add
parameter_list|(
name|Operation
name|operation
parameter_list|)
throws|throws
name|TranslogException
block|{
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
name|rwl
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|BytesStreamOutput
name|out
init|=
name|cachedEntry
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
name|Location
name|location
init|=
name|current
operator|.
name|add
argument_list|(
name|out
operator|.
name|underlyingBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|syncOnEachOperation
condition|)
block|{
name|current
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
name|FsTranslogFile
name|trans
init|=
name|this
operator|.
name|trans
decl_stmt|;
if|if
condition|(
name|trans
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|location
operator|=
name|trans
operator|.
name|add
argument_list|(
name|out
operator|.
name|underlyingBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClosedChannelException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|location
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
finally|finally
block|{
name|rwl
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|snapshot
annotation|@
name|Override
specifier|public
name|FsChannelSnapshot
name|snapshot
parameter_list|()
throws|throws
name|TranslogException
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|FsChannelSnapshot
name|snapshot
init|=
name|current
operator|.
name|snapshot
argument_list|()
decl_stmt|;
if|if
condition|(
name|snapshot
operator|!=
literal|null
condition|)
block|{
return|return
name|snapshot
return|;
block|}
name|Thread
operator|.
name|yield
argument_list|()
expr_stmt|;
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
name|FsChannelSnapshot
name|snap
init|=
name|snapshot
argument_list|()
decl_stmt|;
if|if
condition|(
name|snap
operator|.
name|translogId
argument_list|()
operator|==
name|snapshot
operator|.
name|translogId
argument_list|()
condition|)
block|{
name|snap
operator|.
name|seekForward
argument_list|(
name|snapshot
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|snap
return|;
block|}
DECL|method|sync
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|()
block|{
name|FsTranslogFile
name|current1
init|=
name|this
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|current1
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|current1
operator|.
name|sync
argument_list|()
expr_stmt|;
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
name|this
operator|.
name|syncOnEachOperation
operator|=
name|syncOnEachOperation
expr_stmt|;
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
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|FsTranslogFile
name|current1
init|=
name|this
operator|.
name|current
decl_stmt|;
if|if
condition|(
name|current1
operator|!=
literal|null
condition|)
block|{
name|current1
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
name|current1
operator|=
name|this
operator|.
name|trans
expr_stmt|;
if|if
condition|(
name|current1
operator|!=
literal|null
condition|)
block|{
name|current1
operator|.
name|close
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rwl
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

