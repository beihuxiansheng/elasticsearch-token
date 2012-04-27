begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/**  */
end_comment

begin_class
DECL|class|BufferingFsTranslogFile
specifier|public
class|class
name|BufferingFsTranslogFile
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
DECL|field|operationCounter
specifier|private
specifier|volatile
name|int
name|operationCounter
decl_stmt|;
DECL|field|lastPosition
specifier|private
name|long
name|lastPosition
decl_stmt|;
DECL|field|lastWrittenPosition
specifier|private
specifier|volatile
name|long
name|lastWrittenPosition
decl_stmt|;
DECL|field|lastSyncPosition
specifier|private
specifier|volatile
name|long
name|lastSyncPosition
init|=
literal|0
decl_stmt|;
DECL|field|buffer
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
DECL|field|bufferCount
specifier|private
name|int
name|bufferCount
decl_stmt|;
DECL|method|BufferingFsTranslogFile
specifier|public
name|BufferingFsTranslogFile
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|id
parameter_list|,
name|RafReference
name|raf
parameter_list|,
name|int
name|bufferSize
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
name|this
operator|.
name|buffer
operator|=
operator|new
name|byte
index|[
name|bufferSize
index|]
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
return|;
block|}
annotation|@
name|Override
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
name|operationCounter
operator|++
expr_stmt|;
name|long
name|position
init|=
name|lastPosition
decl_stmt|;
if|if
condition|(
name|size
operator|>=
name|buffer
operator|.
name|length
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
comment|// we use the channel to write, since on windows, writing to the RAF might not be reflected
comment|// when reading through the channel
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
argument_list|)
expr_stmt|;
name|lastWrittenPosition
operator|+=
name|size
expr_stmt|;
name|lastPosition
operator|+=
name|size
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
if|if
condition|(
name|size
operator|>
name|buffer
operator|.
name|length
operator|-
name|bufferCount
condition|)
block|{
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|from
argument_list|,
name|buffer
argument_list|,
name|bufferCount
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|bufferCount
operator|+=
name|size
expr_stmt|;
name|lastPosition
operator|+=
name|size
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
DECL|method|flushBuffer
specifier|private
name|void
name|flushBuffer
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|bufferCount
operator|>
literal|0
condition|)
block|{
comment|// we use the channel to write, since on windows, writing to the RAF might not be reflected
comment|// when reading through the channel
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
name|buffer
argument_list|,
literal|0
argument_list|,
name|bufferCount
argument_list|)
argument_list|)
expr_stmt|;
name|lastWrittenPosition
operator|+=
name|bufferCount
expr_stmt|;
name|bufferCount
operator|=
literal|0
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
if|if
condition|(
name|location
operator|.
name|translogLocation
operator|>=
name|lastWrittenPosition
condition|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|location
operator|.
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
call|(
name|int
call|)
argument_list|(
name|location
operator|.
name|translogLocation
operator|-
name|lastWrittenPosition
argument_list|)
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|location
operator|.
name|size
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
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
annotation|@
name|Override
DECL|method|snapshot
specifier|public
name|FsChannelSnapshot
name|snapshot
parameter_list|()
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
name|flushBuffer
argument_list|()
expr_stmt|;
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
argument_list|,
name|operationCounter
argument_list|)
return|;
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
literal|"failed to flush"
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
annotation|@
name|Override
DECL|method|syncNeeded
specifier|public
name|boolean
name|syncNeeded
parameter_list|()
block|{
return|return
name|lastPosition
operator|!=
name|lastSyncPosition
return|;
block|}
annotation|@
name|Override
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
name|lastPosition
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
name|flushBuffer
argument_list|()
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
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|(
name|boolean
name|delete
parameter_list|)
block|{
if|if
condition|(
operator|!
name|delete
condition|)
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
name|flushBuffer
argument_list|()
expr_stmt|;
name|sync
argument_list|()
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
literal|"failed to close"
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
name|raf
operator|.
name|decreaseRefCount
argument_list|(
name|delete
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reuse
specifier|public
name|void
name|reuse
parameter_list|(
name|FsTranslogFile
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|BufferingFsTranslogFile
operator|)
condition|)
block|{
return|return;
block|}
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
name|flushBuffer
argument_list|()
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
operator|(
operator|(
name|BufferingFsTranslogFile
operator|)
name|other
operator|)
operator|.
name|buffer
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
literal|"failed to flush"
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
block|}
end_class

end_unit

