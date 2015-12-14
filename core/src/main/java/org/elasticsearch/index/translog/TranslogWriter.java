begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.translog
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
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
name|codecs
operator|.
name|CodecUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|OutputStreamDataOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|RamUsageEstimator
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
name|bytes
operator|.
name|BytesArray
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
name|bytes
operator|.
name|BytesReference
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
name|Channels
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
name|Callback
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
name|ReleasableLock
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
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

begin_class
DECL|class|TranslogWriter
specifier|public
class|class
name|TranslogWriter
extends|extends
name|TranslogReader
block|{
DECL|field|TRANSLOG_CODEC
specifier|public
specifier|static
specifier|final
name|String
name|TRANSLOG_CODEC
init|=
literal|"translog"
decl_stmt|;
DECL|field|VERSION_CHECKSUMS
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CHECKSUMS
init|=
literal|1
decl_stmt|;
DECL|field|VERSION_CHECKPOINTS
specifier|public
specifier|static
specifier|final
name|int
name|VERSION_CHECKPOINTS
init|=
literal|2
decl_stmt|;
comment|// since 2.0 we have checkpoints?
DECL|field|VERSION
specifier|public
specifier|static
specifier|final
name|int
name|VERSION
init|=
name|VERSION_CHECKPOINTS
decl_stmt|;
DECL|field|shardId
specifier|protected
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|readLock
specifier|protected
specifier|final
name|ReleasableLock
name|readLock
decl_stmt|;
DECL|field|writeLock
specifier|protected
specifier|final
name|ReleasableLock
name|writeLock
decl_stmt|;
comment|/* the offset in bytes that was written when the file was last synced*/
DECL|field|lastSyncedOffset
specifier|protected
specifier|volatile
name|long
name|lastSyncedOffset
decl_stmt|;
comment|/* the number of translog operations written to this file */
DECL|field|operationCounter
specifier|protected
specifier|volatile
name|int
name|operationCounter
decl_stmt|;
comment|/* the offset in bytes written to the file */
DECL|field|writtenOffset
specifier|protected
specifier|volatile
name|long
name|writtenOffset
decl_stmt|;
comment|/* if we hit an exception that we can't recover from we assign it to this var and ship it with every AlreadyClosedException we throw */
DECL|field|tragicEvent
specifier|private
specifier|volatile
name|Throwable
name|tragicEvent
decl_stmt|;
DECL|method|TranslogWriter
specifier|public
name|TranslogWriter
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|generation
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|generation
argument_list|,
name|channelReference
argument_list|,
name|channelReference
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|ReadWriteLock
name|rwl
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
name|readLock
operator|=
operator|new
name|ReleasableLock
argument_list|(
name|rwl
operator|.
name|readLock
argument_list|()
argument_list|)
expr_stmt|;
name|writeLock
operator|=
operator|new
name|ReleasableLock
argument_list|(
name|rwl
operator|.
name|writeLock
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|writtenOffset
operator|=
name|channelReference
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|()
expr_stmt|;
name|this
operator|.
name|lastSyncedOffset
operator|=
name|channelReference
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|()
expr_stmt|;
empty_stmt|;
block|}
DECL|method|create
specifier|public
specifier|static
name|TranslogWriter
name|create
parameter_list|(
name|Type
name|type
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|String
name|translogUUID
parameter_list|,
name|long
name|fileGeneration
parameter_list|,
name|Path
name|file
parameter_list|,
name|Callback
argument_list|<
name|ChannelReference
argument_list|>
name|onClose
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|ChannelFactory
name|channelFactory
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
name|translogUUID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|headerLength
init|=
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|TRANSLOG_CODEC
argument_list|)
operator|+
name|ref
operator|.
name|length
operator|+
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
specifier|final
name|FileChannel
name|channel
init|=
name|channelFactory
operator|.
name|open
argument_list|(
name|file
argument_list|)
decl_stmt|;
try|try
block|{
comment|// This OutputStreamDataOutput is intentionally not closed because
comment|// closing it will close the FileChannel
specifier|final
name|OutputStreamDataOutput
name|out
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|Channels
operator|.
name|newOutputStream
argument_list|(
name|channel
argument_list|)
argument_list|)
decl_stmt|;
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|out
argument_list|,
name|TRANSLOG_CODEC
argument_list|,
name|VERSION
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|ref
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
argument_list|,
name|ref
operator|.
name|length
argument_list|)
expr_stmt|;
name|channel
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writeCheckpoint
argument_list|(
name|headerLength
argument_list|,
literal|0
argument_list|,
name|file
operator|.
name|getParent
argument_list|()
argument_list|,
name|fileGeneration
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
expr_stmt|;
specifier|final
name|TranslogWriter
name|writer
init|=
name|type
operator|.
name|create
argument_list|(
name|shardId
argument_list|,
name|fileGeneration
argument_list|,
operator|new
name|ChannelReference
argument_list|(
name|file
argument_list|,
name|fileGeneration
argument_list|,
name|channel
argument_list|,
name|onClose
argument_list|)
argument_list|,
name|bufferSize
argument_list|)
decl_stmt|;
return|return
name|writer
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|channel
argument_list|)
expr_stmt|;
try|try
block|{
name|Files
operator|.
name|delete
argument_list|(
name|file
argument_list|)
expr_stmt|;
comment|// remove the file as well
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|throwable
operator|.
name|addSuppressed
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
throw|throw
name|throwable
throw|;
block|}
block|}
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
DECL|method|SIMPLE
DECL|method|SIMPLE
name|SIMPLE
parameter_list|()
block|{
annotation|@
name|Override
specifier|public
name|TranslogWriter
name|create
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|generation
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|TranslogWriter
argument_list|(
name|shardId
argument_list|,
name|generation
argument_list|,
name|channelReference
argument_list|)
return|;
block|}
block|}
block|,
DECL|method|BUFFERED
DECL|method|BUFFERED
name|BUFFERED
parameter_list|()
block|{
annotation|@
name|Override
specifier|public
name|TranslogWriter
name|create
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|generation
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BufferingTranslogWriter
argument_list|(
name|shardId
argument_list|,
name|generation
argument_list|,
name|channelReference
argument_list|,
name|bufferSize
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|create
specifier|public
specifier|abstract
name|TranslogWriter
name|create
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|generation
parameter_list|,
name|ChannelReference
name|raf
parameter_list|,
name|int
name|bufferSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|fromString
specifier|public
specifier|static
name|Type
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|SIMPLE
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|SIMPLE
return|;
block|}
elseif|else
if|if
condition|(
name|BUFFERED
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BUFFERED
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No translog fs type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|closeWithTragicEvent
specifier|protected
specifier|final
name|void
name|closeWithTragicEvent
parameter_list|(
name|Throwable
name|throwable
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ReleasableLock
name|lock
init|=
name|writeLock
operator|.
name|acquire
argument_list|()
init|)
block|{
if|if
condition|(
name|throwable
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|tragicEvent
operator|==
literal|null
condition|)
block|{
name|tragicEvent
operator|=
name|throwable
expr_stmt|;
block|}
else|else
block|{
name|tragicEvent
operator|.
name|addSuppressed
argument_list|(
name|throwable
argument_list|)
expr_stmt|;
block|}
block|}
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * add the given bytes to the translog and return the location they were written at      */
DECL|method|add
specifier|public
name|Translog
operator|.
name|Location
name|add
parameter_list|(
name|BytesReference
name|data
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|long
name|position
decl_stmt|;
try|try
init|(
name|ReleasableLock
name|lock
init|=
name|writeLock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|position
operator|=
name|writtenOffset
expr_stmt|;
try|try
block|{
name|data
operator|.
name|writeTo
argument_list|(
name|channel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|closeWithTragicEvent
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|writtenOffset
operator|=
name|writtenOffset
operator|+
name|data
operator|.
name|length
argument_list|()
expr_stmt|;
name|operationCounter
operator|++
expr_stmt|;
empty_stmt|;
block|}
return|return
operator|new
name|Translog
operator|.
name|Location
argument_list|(
name|generation
argument_list|,
name|position
argument_list|,
name|data
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * change the size of the internal buffer if relevant      */
DECL|method|updateBufferSize
specifier|public
name|void
name|updateBufferSize
parameter_list|(
name|int
name|bufferSize
parameter_list|)
throws|throws
name|TranslogException
block|{     }
comment|/**      * write all buffered ops to disk and fsync file      */
DECL|method|sync
specifier|public
specifier|synchronized
name|void
name|sync
parameter_list|()
throws|throws
name|IOException
block|{
comment|// synchronized to ensure only one sync happens a time
comment|// check if we really need to sync here...
if|if
condition|(
name|syncNeeded
argument_list|()
condition|)
block|{
try|try
init|(
name|ReleasableLock
name|lock
init|=
name|writeLock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|long
name|offset
init|=
name|writtenOffset
decl_stmt|;
specifier|final
name|int
name|opsCount
init|=
name|operationCounter
decl_stmt|;
name|checkpoint
argument_list|(
name|offset
argument_list|,
name|opsCount
argument_list|,
name|channelReference
argument_list|)
expr_stmt|;
name|lastSyncedOffset
operator|=
name|offset
expr_stmt|;
block|}
block|}
block|}
comment|/**      * returns true if there are buffered ops      */
DECL|method|syncNeeded
specifier|public
name|boolean
name|syncNeeded
parameter_list|()
block|{
return|return
name|writtenOffset
operator|!=
name|lastSyncedOffset
return|;
comment|// by default nothing is buffered
block|}
annotation|@
name|Override
DECL|method|totalOperations
specifier|public
name|int
name|totalOperations
parameter_list|()
block|{
return|return
name|operationCounter
return|;
block|}
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|writtenOffset
return|;
block|}
comment|/**      * Flushes the buffer if the translog is buffered.      */
DECL|method|flush
specifier|protected
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{     }
comment|/**      * returns a new reader that follows the current writes (most importantly allows making      * repeated snapshots that includes new content)      */
DECL|method|newReaderFromWriter
specifier|public
name|TranslogReader
name|newReaderFromWriter
parameter_list|()
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|channelReference
operator|.
name|incRef
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|TranslogReader
name|reader
init|=
operator|new
name|InnerReader
argument_list|(
name|this
operator|.
name|generation
argument_list|,
name|firstOperationOffset
argument_list|,
name|channelReference
argument_list|)
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|reader
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|channelReference
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * returns a new immutable reader which only exposes the current written operation *      */
DECL|method|immutableReader
specifier|public
name|ImmutableTranslogReader
name|immutableReader
parameter_list|()
throws|throws
name|TranslogException
block|{
if|if
condition|(
name|channelReference
operator|.
name|tryIncRef
argument_list|()
condition|)
block|{
try|try
init|(
name|ReleasableLock
name|lock
init|=
name|writeLock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|ImmutableTranslogReader
name|reader
init|=
operator|new
name|ImmutableTranslogReader
argument_list|(
name|this
operator|.
name|generation
argument_list|,
name|channelReference
argument_list|,
name|firstOperationOffset
argument_list|,
name|writtenOffset
argument_list|,
name|operationCounter
argument_list|)
decl_stmt|;
name|channelReference
operator|.
name|incRef
argument_list|()
expr_stmt|;
comment|// for new reader
return|return
name|reader
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
literal|"exception while creating an immutable reader"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|channelReference
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|TranslogException
argument_list|(
name|shardId
argument_list|,
literal|"can't increment channel ["
operator|+
name|channelReference
operator|+
literal|"] ref count"
argument_list|)
throw|;
block|}
block|}
DECL|method|assertBytesAtLocation
name|boolean
name|assertBytesAtLocation
parameter_list|(
name|Translog
operator|.
name|Location
name|location
parameter_list|,
name|BytesReference
name|expectedBytes
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
name|readBytes
argument_list|(
name|buffer
argument_list|,
name|location
operator|.
name|translogLocation
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesArray
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|expectedBytes
argument_list|)
return|;
block|}
comment|/**      * this class is used when one wants a reference to this file which exposes all recently written operation.      * as such it needs access to the internals of the current reader      */
DECL|class|InnerReader
specifier|final
class|class
name|InnerReader
extends|extends
name|TranslogReader
block|{
DECL|method|InnerReader
specifier|public
name|InnerReader
parameter_list|(
name|long
name|generation
parameter_list|,
name|long
name|fistOperationOffset
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|)
block|{
name|super
argument_list|(
name|generation
argument_list|,
name|channelReference
argument_list|,
name|fistOperationOffset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|TranslogWriter
operator|.
name|this
operator|.
name|sizeInBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|totalOperations
specifier|public
name|int
name|totalOperations
parameter_list|()
block|{
return|return
name|TranslogWriter
operator|.
name|this
operator|.
name|totalOperations
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|protected
name|void
name|readBytes
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
name|TranslogWriter
operator|.
name|this
operator|.
name|readBytes
argument_list|(
name|buffer
argument_list|,
name|position
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Syncs the translog up to at least the given offset unless already synced      *      * @return<code>true</code> if this call caused an actual sync operation      */
DECL|method|syncUpTo
specifier|public
name|boolean
name|syncUpTo
parameter_list|(
name|long
name|offset
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|lastSyncedOffset
operator|<
name|offset
condition|)
block|{
name|sync
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|protected
name|void
name|readBytes
parameter_list|(
name|ByteBuffer
name|buffer
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ReleasableLock
name|lock
init|=
name|readLock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|Channels
operator|.
name|readFromFileChannelWithEofException
argument_list|(
name|channel
argument_list|,
name|position
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkpoint
specifier|protected
specifier|synchronized
name|void
name|checkpoint
parameter_list|(
name|long
name|lastSyncPosition
parameter_list|,
name|int
name|operationCounter
parameter_list|,
name|ChannelReference
name|channelReference
parameter_list|)
throws|throws
name|IOException
block|{
name|channelReference
operator|.
name|getChannel
argument_list|()
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|writeCheckpoint
argument_list|(
name|lastSyncPosition
argument_list|,
name|operationCounter
argument_list|,
name|channelReference
operator|.
name|getPath
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|,
name|channelReference
operator|.
name|getGeneration
argument_list|()
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
DECL|method|writeCheckpoint
specifier|private
specifier|static
name|void
name|writeCheckpoint
parameter_list|(
name|long
name|syncPosition
parameter_list|,
name|int
name|numOperations
parameter_list|,
name|Path
name|translogFile
parameter_list|,
name|long
name|generation
parameter_list|,
name|OpenOption
modifier|...
name|options
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|checkpointFile
init|=
name|translogFile
operator|.
name|resolve
argument_list|(
name|Translog
operator|.
name|CHECKPOINT_FILE_NAME
argument_list|)
decl_stmt|;
name|Checkpoint
name|checkpoint
init|=
operator|new
name|Checkpoint
argument_list|(
name|syncPosition
argument_list|,
name|numOperations
argument_list|,
name|generation
argument_list|)
decl_stmt|;
name|Checkpoint
operator|.
name|write
argument_list|(
name|checkpointFile
argument_list|,
name|checkpoint
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
DECL|class|ChannelFactory
specifier|static
class|class
name|ChannelFactory
block|{
DECL|field|DEFAULT
specifier|static
specifier|final
name|ChannelFactory
name|DEFAULT
init|=
operator|new
name|ChannelFactory
argument_list|()
decl_stmt|;
comment|// only for testing until we have a disk-full FileSystemt
DECL|method|open
specifier|public
name|FileChannel
name|open
parameter_list|(
name|Path
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileChannel
operator|.
name|open
argument_list|(
name|file
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|READ
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|)
return|;
block|}
block|}
DECL|method|ensureOpen
specifier|protected
specifier|final
name|void
name|ensureOpen
parameter_list|()
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"translog ["
operator|+
name|getGeneration
argument_list|()
operator|+
literal|"] is already closed"
argument_list|,
name|tragicEvent
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

