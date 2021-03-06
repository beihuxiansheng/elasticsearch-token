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
name|elasticsearch
operator|.
name|Assertions
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
name|collect
operator|.
name|Tuple
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
name|seqno
operator|.
name|SequenceNumbers
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
name|seqno
operator|.
name|SequenceNumbersService
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
name|BufferedOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|io
operator|.
name|OutputStream
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
name|Path
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
name|StandardOpenOption
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
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|LongSupplier
import|;
end_import

begin_class
DECL|class|TranslogWriter
specifier|public
class|class
name|TranslogWriter
extends|extends
name|BaseTranslogReader
implements|implements
name|Closeable
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
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|channelFactory
specifier|private
specifier|final
name|ChannelFactory
name|channelFactory
decl_stmt|;
comment|// the last checkpoint that was written when the translog was last synced
DECL|field|lastSyncedCheckpoint
specifier|private
specifier|volatile
name|Checkpoint
name|lastSyncedCheckpoint
decl_stmt|;
comment|/* the number of translog operations written to this file */
DECL|field|operationCounter
specifier|private
specifier|volatile
name|int
name|operationCounter
decl_stmt|;
comment|/* if we hit an exception that we can't recover from we assign it to this var and ship it with every AlreadyClosedException we throw */
DECL|field|tragedy
specifier|private
specifier|volatile
name|Exception
name|tragedy
decl_stmt|;
comment|/* A buffered outputstream what writes to the writers channel */
DECL|field|outputStream
specifier|private
specifier|final
name|OutputStream
name|outputStream
decl_stmt|;
comment|/* the total offset of this file including the bytes written to the file as well as into the buffer */
DECL|field|totalOffset
specifier|private
specifier|volatile
name|long
name|totalOffset
decl_stmt|;
DECL|field|minSeqNo
specifier|private
specifier|volatile
name|long
name|minSeqNo
decl_stmt|;
DECL|field|maxSeqNo
specifier|private
specifier|volatile
name|long
name|maxSeqNo
decl_stmt|;
DECL|field|globalCheckpointSupplier
specifier|private
specifier|final
name|LongSupplier
name|globalCheckpointSupplier
decl_stmt|;
DECL|field|minTranslogGenerationSupplier
specifier|private
specifier|final
name|LongSupplier
name|minTranslogGenerationSupplier
decl_stmt|;
DECL|field|closed
specifier|protected
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// lock order synchronized(syncLock) -> synchronized(this)
DECL|field|syncLock
specifier|private
specifier|final
name|Object
name|syncLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|seenSequenceNumbers
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Tuple
argument_list|<
name|BytesReference
argument_list|,
name|Exception
argument_list|>
argument_list|>
name|seenSequenceNumbers
decl_stmt|;
DECL|method|TranslogWriter
specifier|private
name|TranslogWriter
parameter_list|(
specifier|final
name|ChannelFactory
name|channelFactory
parameter_list|,
specifier|final
name|ShardId
name|shardId
parameter_list|,
specifier|final
name|Checkpoint
name|initialCheckpoint
parameter_list|,
specifier|final
name|FileChannel
name|channel
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|,
specifier|final
name|ByteSizeValue
name|bufferSize
parameter_list|,
specifier|final
name|LongSupplier
name|globalCheckpointSupplier
parameter_list|,
name|LongSupplier
name|minTranslogGenerationSupplier
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|initialCheckpoint
operator|.
name|generation
argument_list|,
name|channel
argument_list|,
name|path
argument_list|,
name|channel
operator|.
name|position
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|initialCheckpoint
operator|.
name|offset
operator|==
name|channel
operator|.
name|position
argument_list|()
operator|:
literal|"initial checkpoint offset ["
operator|+
name|initialCheckpoint
operator|.
name|offset
operator|+
literal|"] is different than current channel poistion ["
operator|+
name|channel
operator|.
name|position
argument_list|()
operator|+
literal|"]"
assert|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|channelFactory
operator|=
name|channelFactory
expr_stmt|;
name|this
operator|.
name|minTranslogGenerationSupplier
operator|=
name|minTranslogGenerationSupplier
expr_stmt|;
name|this
operator|.
name|outputStream
operator|=
operator|new
name|BufferedChannelOutputStream
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
argument_list|,
name|bufferSize
operator|.
name|bytesAsInt
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastSyncedCheckpoint
operator|=
name|initialCheckpoint
expr_stmt|;
name|this
operator|.
name|totalOffset
operator|=
name|initialCheckpoint
operator|.
name|offset
expr_stmt|;
assert|assert
name|initialCheckpoint
operator|.
name|minSeqNo
operator|==
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
operator|:
name|initialCheckpoint
operator|.
name|minSeqNo
assert|;
name|this
operator|.
name|minSeqNo
operator|=
name|initialCheckpoint
operator|.
name|minSeqNo
expr_stmt|;
assert|assert
name|initialCheckpoint
operator|.
name|maxSeqNo
operator|==
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
operator|:
name|initialCheckpoint
operator|.
name|maxSeqNo
assert|;
name|this
operator|.
name|maxSeqNo
operator|=
name|initialCheckpoint
operator|.
name|maxSeqNo
expr_stmt|;
name|this
operator|.
name|globalCheckpointSupplier
operator|=
name|globalCheckpointSupplier
expr_stmt|;
name|this
operator|.
name|seenSequenceNumbers
operator|=
name|Assertions
operator|.
name|ENABLED
condition|?
operator|new
name|HashMap
argument_list|<>
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
DECL|method|getHeaderLength
specifier|static
name|int
name|getHeaderLength
parameter_list|(
name|String
name|translogUUID
parameter_list|)
block|{
return|return
name|getHeaderLength
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|translogUUID
argument_list|)
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|getHeaderLength
specifier|static
name|int
name|getHeaderLength
parameter_list|(
name|int
name|uuidLength
parameter_list|)
block|{
return|return
name|CodecUtil
operator|.
name|headerLength
argument_list|(
name|TRANSLOG_CODEC
argument_list|)
operator|+
name|uuidLength
operator|+
name|Integer
operator|.
name|BYTES
return|;
block|}
DECL|method|writeHeader
specifier|static
name|void
name|writeHeader
parameter_list|(
name|OutputStreamDataOutput
name|out
parameter_list|,
name|BytesRef
name|ref
parameter_list|)
throws|throws
name|IOException
block|{
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
block|}
DECL|method|create
specifier|public
specifier|static
name|TranslogWriter
name|create
parameter_list|(
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
name|ChannelFactory
name|channelFactory
parameter_list|,
name|ByteSizeValue
name|bufferSize
parameter_list|,
specifier|final
name|LongSupplier
name|globalCheckpointSupplier
parameter_list|,
specifier|final
name|long
name|initialMinTranslogGen
parameter_list|,
specifier|final
name|LongSupplier
name|minTranslogGenerationSupplier
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
name|firstOperationOffset
init|=
name|getHeaderLength
argument_list|(
name|ref
operator|.
name|length
argument_list|)
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
name|writeHeader
argument_list|(
name|out
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|channel
operator|.
name|force
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Checkpoint
name|checkpoint
init|=
name|Checkpoint
operator|.
name|emptyTranslogCheckpoint
argument_list|(
name|firstOperationOffset
argument_list|,
name|fileGeneration
argument_list|,
name|globalCheckpointSupplier
operator|.
name|getAsLong
argument_list|()
argument_list|,
name|initialMinTranslogGen
argument_list|)
decl_stmt|;
name|writeCheckpoint
argument_list|(
name|channelFactory
argument_list|,
name|file
operator|.
name|getParent
argument_list|()
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return
operator|new
name|TranslogWriter
argument_list|(
name|channelFactory
argument_list|,
name|shardId
argument_list|,
name|checkpoint
argument_list|,
name|channel
argument_list|,
name|file
argument_list|,
name|bufferSize
argument_list|,
name|globalCheckpointSupplier
argument_list|,
name|minTranslogGenerationSupplier
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|exception
parameter_list|)
block|{
comment|// if we fail to bake the file-generation into the checkpoint we stick with the file and once we recover and that
comment|// file exists we remove it. We only apply this logic to the checkpoint.generation+1 any other file with a higher generation is an error condition
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|channel
argument_list|)
expr_stmt|;
throw|throw
name|exception
throw|;
block|}
block|}
comment|/**      * If this {@code TranslogWriter} was closed as a side-effect of a tragic exception,      * e.g. disk full while flushing a new segment, this returns the root cause exception.      * Otherwise (no tragic exception has occurred) it returns null.      */
DECL|method|getTragicException
specifier|public
name|Exception
name|getTragicException
parameter_list|()
block|{
return|return
name|tragedy
return|;
block|}
DECL|method|closeWithTragicEvent
specifier|private
specifier|synchronized
name|void
name|closeWithTragicEvent
parameter_list|(
name|Exception
name|exception
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|exception
operator|!=
literal|null
assert|;
if|if
condition|(
name|tragedy
operator|==
literal|null
condition|)
block|{
name|tragedy
operator|=
name|exception
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tragedy
operator|!=
name|exception
condition|)
block|{
comment|// it should be safe to call closeWithTragicEvents on multiple layers without
comment|// worrying about self suppression.
name|tragedy
operator|.
name|addSuppressed
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**      * add the given bytes to the translog and return the location they were written at      */
comment|/**      * Add the given bytes to the translog with the specified sequence number; returns the location the bytes were written to.      *      * @param data  the bytes to write      * @param seqNo the sequence number associated with the operation      * @return the location the bytes were written to      * @throws IOException if writing to the translog resulted in an I/O exception      */
DECL|method|add
specifier|public
specifier|synchronized
name|Translog
operator|.
name|Location
name|add
parameter_list|(
specifier|final
name|BytesReference
name|data
parameter_list|,
specifier|final
name|long
name|seqNo
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
specifier|final
name|long
name|offset
init|=
name|totalOffset
decl_stmt|;
try|try
block|{
name|data
operator|.
name|writeTo
argument_list|(
name|outputStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|closeWithTragicEvent
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|inner
parameter_list|)
block|{
name|ex
operator|.
name|addSuppressed
argument_list|(
name|inner
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
name|totalOffset
operator|+=
name|data
operator|.
name|length
argument_list|()
expr_stmt|;
if|if
condition|(
name|minSeqNo
operator|==
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
condition|)
block|{
assert|assert
name|operationCounter
operator|==
literal|0
assert|;
block|}
if|if
condition|(
name|maxSeqNo
operator|==
name|SequenceNumbersService
operator|.
name|NO_OPS_PERFORMED
condition|)
block|{
assert|assert
name|operationCounter
operator|==
literal|0
assert|;
block|}
name|minSeqNo
operator|=
name|SequenceNumbers
operator|.
name|min
argument_list|(
name|minSeqNo
argument_list|,
name|seqNo
argument_list|)
expr_stmt|;
name|maxSeqNo
operator|=
name|SequenceNumbers
operator|.
name|max
argument_list|(
name|maxSeqNo
argument_list|,
name|seqNo
argument_list|)
expr_stmt|;
name|operationCounter
operator|++
expr_stmt|;
assert|assert
name|assertNoSeqNumberConflict
argument_list|(
name|seqNo
argument_list|,
name|data
argument_list|)
assert|;
return|return
operator|new
name|Translog
operator|.
name|Location
argument_list|(
name|generation
argument_list|,
name|offset
argument_list|,
name|data
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
DECL|method|assertNoSeqNumberConflict
specifier|private
specifier|synchronized
name|boolean
name|assertNoSeqNumberConflict
parameter_list|(
name|long
name|seqNo
parameter_list|,
name|BytesReference
name|data
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|seqNo
operator|==
name|SequenceNumbersService
operator|.
name|UNASSIGNED_SEQ_NO
condition|)
block|{
comment|// nothing to do
block|}
elseif|else
if|if
condition|(
name|seenSequenceNumbers
operator|.
name|containsKey
argument_list|(
name|seqNo
argument_list|)
condition|)
block|{
specifier|final
name|Tuple
argument_list|<
name|BytesReference
argument_list|,
name|Exception
argument_list|>
name|previous
init|=
name|seenSequenceNumbers
operator|.
name|get
argument_list|(
name|seqNo
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|.
name|v1
argument_list|()
operator|.
name|equals
argument_list|(
name|data
argument_list|)
operator|==
literal|false
condition|)
block|{
name|Translog
operator|.
name|Operation
name|newOp
init|=
name|Translog
operator|.
name|readOperation
argument_list|(
operator|new
name|BufferedChecksumStreamInput
argument_list|(
name|data
operator|.
name|streamInput
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Translog
operator|.
name|Operation
name|prvOp
init|=
name|Translog
operator|.
name|readOperation
argument_list|(
operator|new
name|BufferedChecksumStreamInput
argument_list|(
name|previous
operator|.
name|v1
argument_list|()
operator|.
name|streamInput
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"seqNo ["
operator|+
name|seqNo
operator|+
literal|"] was processed twice in generation ["
operator|+
name|generation
operator|+
literal|"], with different data. "
operator|+
literal|"prvOp ["
operator|+
name|prvOp
operator|+
literal|"], newOp ["
operator|+
name|newOp
operator|+
literal|"]"
argument_list|,
name|previous
operator|.
name|v2
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|seenSequenceNumbers
operator|.
name|put
argument_list|(
name|seqNo
argument_list|,
operator|new
name|Tuple
argument_list|<>
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|data
operator|.
name|toBytesRef
argument_list|()
argument_list|,
literal|true
argument_list|)
argument_list|,
operator|new
name|RuntimeException
argument_list|(
literal|"stack capture previous op"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * write all buffered ops to disk and fsync file.      *      * Note: any exception during the sync process will be interpreted as a tragic exception and the writer will be closed before      * raising the exception.      */
DECL|method|sync
specifier|public
name|void
name|sync
parameter_list|()
throws|throws
name|IOException
block|{
name|syncUpTo
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns<code>true</code> if there are buffered operations that have not been flushed and fsynced to disk or if the latest global      * checkpoint has not yet been fsynced      */
DECL|method|syncNeeded
specifier|public
name|boolean
name|syncNeeded
parameter_list|()
block|{
return|return
name|totalOffset
operator|!=
name|lastSyncedCheckpoint
operator|.
name|offset
operator|||
name|globalCheckpointSupplier
operator|.
name|getAsLong
argument_list|()
operator|!=
name|lastSyncedCheckpoint
operator|.
name|globalCheckpoint
operator|||
name|minTranslogGenerationSupplier
operator|.
name|getAsLong
argument_list|()
operator|!=
name|lastSyncedCheckpoint
operator|.
name|minTranslogGeneration
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
name|operationCounter
return|;
block|}
annotation|@
name|Override
DECL|method|getCheckpoint
name|Checkpoint
name|getCheckpoint
parameter_list|()
block|{
return|return
name|getLastSyncedCheckpoint
argument_list|()
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
name|totalOffset
return|;
block|}
comment|/**      * Closes this writer and transfers its underlying file channel to a new immutable {@link TranslogReader}      * @return a new {@link TranslogReader}      * @throws IOException if any of the file operations resulted in an I/O exception      */
DECL|method|closeIntoReader
specifier|public
name|TranslogReader
name|closeIntoReader
parameter_list|()
throws|throws
name|IOException
block|{
comment|// make sure to acquire the sync lock first, to prevent dead locks with threads calling
comment|// syncUpTo() , where the sync lock is acquired first, following by the synchronize(this)
comment|//
comment|// Note: While this is not strictly needed as this method is called while blocking all ops on the translog,
comment|//       we do this to for correctness and preventing future issues.
synchronized|synchronized
init|(
name|syncLock
init|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|sync
argument_list|()
expr_stmt|;
comment|// sync before we close..
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
try|try
block|{
name|closeWithTragicEvent
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|e
operator|.
name|addSuppressed
argument_list|(
name|inner
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
return|return
operator|new
name|TranslogReader
argument_list|(
name|getLastSyncedCheckpoint
argument_list|()
argument_list|,
name|channel
argument_list|,
name|path
argument_list|,
name|getFirstOperationOffset
argument_list|()
argument_list|)
return|;
block|}
else|else
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
literal|"] is already closed (path ["
operator|+
name|path
operator|+
literal|"]"
argument_list|,
name|tragedy
argument_list|)
throw|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|newSnapshot
specifier|public
name|Translog
operator|.
name|Snapshot
name|newSnapshot
parameter_list|()
block|{
comment|// make sure to acquire the sync lock first, to prevent dead locks with threads calling
comment|// syncUpTo() , where the sync lock is acquired first, following by the synchronize(this)
synchronized|synchronized
init|(
name|syncLock
init|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
try|try
block|{
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
literal|"exception while syncing before creating a snapshot"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|super
operator|.
name|newSnapshot
argument_list|()
return|;
block|}
block|}
block|}
DECL|method|getWrittenOffset
specifier|private
name|long
name|getWrittenOffset
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|channel
operator|.
name|position
argument_list|()
return|;
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
name|lastSyncedCheckpoint
operator|.
name|offset
operator|<
name|offset
operator|&&
name|syncNeeded
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|syncLock
init|)
block|{
comment|// only one sync/checkpoint should happen concurrently but we wait
if|if
condition|(
name|lastSyncedCheckpoint
operator|.
name|offset
operator|<
name|offset
operator|&&
name|syncNeeded
argument_list|()
condition|)
block|{
comment|// double checked locking - we don't want to fsync unless we have to and now that we have
comment|// the lock we should check again since if this code is busy we might have fsynced enough already
specifier|final
name|long
name|offsetToSync
decl_stmt|;
specifier|final
name|int
name|opsCounter
decl_stmt|;
specifier|final
name|long
name|currentMinSeqNo
decl_stmt|;
specifier|final
name|long
name|currentMaxSeqNo
decl_stmt|;
specifier|final
name|long
name|currentGlobalCheckpoint
decl_stmt|;
specifier|final
name|long
name|currentMinTranslogGeneration
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
try|try
block|{
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|offsetToSync
operator|=
name|totalOffset
expr_stmt|;
name|opsCounter
operator|=
name|operationCounter
expr_stmt|;
name|currentMinSeqNo
operator|=
name|minSeqNo
expr_stmt|;
name|currentMaxSeqNo
operator|=
name|maxSeqNo
expr_stmt|;
name|currentGlobalCheckpoint
operator|=
name|globalCheckpointSupplier
operator|.
name|getAsLong
argument_list|()
expr_stmt|;
name|currentMinTranslogGeneration
operator|=
name|minTranslogGenerationSupplier
operator|.
name|getAsLong
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|closeWithTragicEvent
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|ex
operator|.
name|addSuppressed
argument_list|(
name|inner
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
block|}
comment|// now do the actual fsync outside of the synchronized block such that
comment|// we can continue writing to the buffer etc.
specifier|final
name|Checkpoint
name|checkpoint
decl_stmt|;
try|try
block|{
name|channel
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkpoint
operator|=
name|writeCheckpoint
argument_list|(
name|channelFactory
argument_list|,
name|offsetToSync
argument_list|,
name|opsCounter
argument_list|,
name|currentMinSeqNo
argument_list|,
name|currentMaxSeqNo
argument_list|,
name|currentGlobalCheckpoint
argument_list|,
name|currentMinTranslogGeneration
argument_list|,
name|path
operator|.
name|getParent
argument_list|()
argument_list|,
name|generation
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|closeWithTragicEvent
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|ex
operator|.
name|addSuppressed
argument_list|(
name|inner
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
assert|assert
name|lastSyncedCheckpoint
operator|.
name|offset
operator|<=
name|offsetToSync
operator|:
literal|"illegal state: "
operator|+
name|lastSyncedCheckpoint
operator|.
name|offset
operator|+
literal|"<= "
operator|+
name|offsetToSync
assert|;
name|lastSyncedCheckpoint
operator|=
name|checkpoint
expr_stmt|;
comment|// write protected by syncLock
return|return
literal|true
return|;
block|}
block|}
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
name|targetBuffer
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|position
operator|+
name|targetBuffer
operator|.
name|remaining
argument_list|()
operator|>
name|getWrittenOffset
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
comment|// we only flush here if it's really really needed - try to minimize the impact of the read operation
comment|// in some cases ie. a tragic event we might still be able to read the relevant value
comment|// which is not really important in production but some test can make most strict assumptions
comment|// if we don't fail in this call unless absolutely necessary.
if|if
condition|(
name|position
operator|+
name|targetBuffer
operator|.
name|remaining
argument_list|()
operator|>
name|getWrittenOffset
argument_list|()
condition|)
block|{
name|outputStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// we don't have to have a lock here because we only write ahead to the file, so all writes has been complete
comment|// for the requested location.
name|Channels
operator|.
name|readFromFileChannelWithEofException
argument_list|(
name|channel
argument_list|,
name|position
argument_list|,
name|targetBuffer
argument_list|)
expr_stmt|;
block|}
DECL|method|writeCheckpoint
specifier|private
specifier|static
name|Checkpoint
name|writeCheckpoint
parameter_list|(
name|ChannelFactory
name|channelFactory
parameter_list|,
name|long
name|syncPosition
parameter_list|,
name|int
name|numOperations
parameter_list|,
name|long
name|minSeqNo
parameter_list|,
name|long
name|maxSeqNo
parameter_list|,
name|long
name|globalCheckpoint
parameter_list|,
name|long
name|minTranslogGeneration
parameter_list|,
name|Path
name|translogFile
parameter_list|,
name|long
name|generation
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
argument_list|,
name|minSeqNo
argument_list|,
name|maxSeqNo
argument_list|,
name|globalCheckpoint
argument_list|,
name|minTranslogGeneration
argument_list|)
decl_stmt|;
name|writeCheckpoint
argument_list|(
name|channelFactory
argument_list|,
name|translogFile
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return
name|checkpoint
return|;
block|}
DECL|method|writeCheckpoint
specifier|private
specifier|static
name|void
name|writeCheckpoint
parameter_list|(
specifier|final
name|ChannelFactory
name|channelFactory
parameter_list|,
specifier|final
name|Path
name|translogFile
parameter_list|,
specifier|final
name|Checkpoint
name|checkpoint
parameter_list|)
throws|throws
name|IOException
block|{
name|Checkpoint
operator|.
name|write
argument_list|(
name|channelFactory
argument_list|,
name|translogFile
operator|.
name|resolve
argument_list|(
name|Translog
operator|.
name|CHECKPOINT_FILE_NAME
argument_list|)
argument_list|,
name|checkpoint
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
expr_stmt|;
block|}
comment|/**      * The last synced checkpoint for this translog.      *      * @return the last synced checkpoint      */
DECL|method|getLastSyncedCheckpoint
name|Checkpoint
name|getLastSyncedCheckpoint
parameter_list|()
block|{
return|return
name|lastSyncedCheckpoint
return|;
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
name|tragedy
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|isClosed
specifier|protected
specifier|final
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|closed
operator|.
name|get
argument_list|()
return|;
block|}
DECL|class|BufferedChannelOutputStream
specifier|private
specifier|final
class|class
name|BufferedChannelOutputStream
extends|extends
name|BufferedOutputStream
block|{
DECL|method|BufferedChannelOutputStream
name|BufferedChannelOutputStream
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|out
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
specifier|synchronized
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|count
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
name|super
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
try|try
block|{
name|closeWithTragicEvent
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|ex
operator|.
name|addSuppressed
argument_list|(
name|inner
argument_list|)
expr_stmt|;
block|}
throw|throw
name|ex
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// the stream is intentionally not closed because
comment|// closing it will close the FileChannel
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"never close this stream"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

