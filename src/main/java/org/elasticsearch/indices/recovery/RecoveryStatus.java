begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.recovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
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
name|store
operator|.
name|Directory
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
name|IOContext
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
name|IndexOutput
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
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|Loggers
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
name|AbstractRefCounted
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
name|ConcurrentCollections
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
name|store
operator|.
name|Store
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
name|store
operator|.
name|StoreFileMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|file
operator|.
name|NoSuchFileException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ConcurrentMap
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
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
name|AtomicReference
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RecoveryStatus
specifier|public
class|class
name|RecoveryStatus
extends|extends
name|AbstractRefCounted
block|{
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|idGenerator
specifier|private
specifier|final
specifier|static
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|RECOVERY_PREFIX
specifier|private
specifier|final
name|String
name|RECOVERY_PREFIX
init|=
literal|"recovery."
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|recoveryId
specifier|private
specifier|final
name|long
name|recoveryId
decl_stmt|;
DECL|field|indexShard
specifier|private
specifier|final
name|InternalIndexShard
name|indexShard
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|RecoveryState
name|state
decl_stmt|;
DECL|field|sourceNode
specifier|private
specifier|final
name|DiscoveryNode
name|sourceNode
decl_stmt|;
DECL|field|tempFilePrefix
specifier|private
specifier|final
name|String
name|tempFilePrefix
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|RecoveryTarget
operator|.
name|RecoveryListener
name|listener
decl_stmt|;
DECL|field|waitingRecoveryThread
specifier|private
name|AtomicReference
argument_list|<
name|Thread
argument_list|>
name|waitingRecoveryThread
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|finished
specifier|private
specifier|final
name|AtomicBoolean
name|finished
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|openIndexOutputs
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|IndexOutput
argument_list|>
name|openIndexOutputs
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|legacyChecksums
specifier|private
specifier|final
name|Store
operator|.
name|LegacyChecksums
name|legacyChecksums
init|=
operator|new
name|Store
operator|.
name|LegacyChecksums
argument_list|()
decl_stmt|;
DECL|method|RecoveryStatus
specifier|public
name|RecoveryStatus
parameter_list|(
name|InternalIndexShard
name|indexShard
parameter_list|,
name|DiscoveryNode
name|sourceNode
parameter_list|,
name|RecoveryState
name|state
parameter_list|,
name|RecoveryTarget
operator|.
name|RecoveryListener
name|listener
parameter_list|)
block|{
name|super
argument_list|(
literal|"recovery_status"
argument_list|)
expr_stmt|;
name|this
operator|.
name|recoveryId
operator|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|indexShard
operator|.
name|indexSettings
argument_list|()
argument_list|,
name|indexShard
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexShard
operator|=
name|indexShard
expr_stmt|;
name|this
operator|.
name|sourceNode
operator|=
name|sourceNode
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|indexShard
operator|.
name|shardId
argument_list|()
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|state
operator|.
name|getTimer
argument_list|()
operator|.
name|startTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|tempFilePrefix
operator|=
name|RECOVERY_PREFIX
operator|+
name|this
operator|.
name|state
operator|.
name|getTimer
argument_list|()
operator|.
name|startTime
argument_list|()
operator|+
literal|"."
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|indexShard
operator|.
name|store
argument_list|()
expr_stmt|;
comment|// make sure the store is not released until we are done.
name|store
operator|.
name|incRef
argument_list|()
expr_stmt|;
block|}
DECL|field|tempFileNames
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|tempFileNames
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentSet
argument_list|()
decl_stmt|;
DECL|method|recoveryId
specifier|public
name|long
name|recoveryId
parameter_list|()
block|{
return|return
name|recoveryId
return|;
block|}
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|indexShard
specifier|public
name|InternalIndexShard
name|indexShard
parameter_list|()
block|{
name|ensureNotFinished
argument_list|()
expr_stmt|;
return|return
name|indexShard
return|;
block|}
DECL|method|sourceNode
specifier|public
name|DiscoveryNode
name|sourceNode
parameter_list|()
block|{
return|return
name|this
operator|.
name|sourceNode
return|;
block|}
DECL|method|state
specifier|public
name|RecoveryState
name|state
parameter_list|()
block|{
return|return
name|state
return|;
block|}
DECL|method|store
specifier|public
name|Store
name|store
parameter_list|()
block|{
name|ensureNotFinished
argument_list|()
expr_stmt|;
return|return
name|store
return|;
block|}
comment|/** set a thread that should be interrupted if the recovery is canceled */
DECL|method|setWaitingRecoveryThread
specifier|public
name|void
name|setWaitingRecoveryThread
parameter_list|(
name|Thread
name|thread
parameter_list|)
block|{
name|waitingRecoveryThread
operator|.
name|set
argument_list|(
name|thread
argument_list|)
expr_stmt|;
block|}
comment|/**      * clear the thread set by {@link #setWaitingRecoveryThread(Thread)}, making sure we      * do not override another thread.      */
DECL|method|clearWaitingRecoveryThread
specifier|public
name|void
name|clearWaitingRecoveryThread
parameter_list|(
name|Thread
name|threadToClear
parameter_list|)
block|{
name|waitingRecoveryThread
operator|.
name|compareAndSet
argument_list|(
name|threadToClear
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|stage
specifier|public
name|void
name|stage
parameter_list|(
name|RecoveryState
operator|.
name|Stage
name|stage
parameter_list|)
block|{
name|state
operator|.
name|setStage
argument_list|(
name|stage
argument_list|)
expr_stmt|;
block|}
DECL|method|stage
specifier|public
name|RecoveryState
operator|.
name|Stage
name|stage
parameter_list|()
block|{
return|return
name|state
operator|.
name|getStage
argument_list|()
return|;
block|}
DECL|method|legacyChecksums
specifier|public
name|Store
operator|.
name|LegacyChecksums
name|legacyChecksums
parameter_list|()
block|{
return|return
name|legacyChecksums
return|;
block|}
comment|/** renames all temporary files to their true name, potentially overriding existing files */
DECL|method|renameAllTempFiles
specifier|public
name|void
name|renameAllTempFiles
parameter_list|()
throws|throws
name|IOException
block|{
name|ensureNotFinished
argument_list|()
expr_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|tempFileIterator
init|=
name|tempFileNames
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Directory
name|directory
init|=
name|store
operator|.
name|directory
argument_list|()
decl_stmt|;
while|while
condition|(
name|tempFileIterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|tempFile
init|=
name|tempFileIterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|origFile
init|=
name|originalNameForTempFile
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
comment|// first, go and delete the existing ones
try|try
block|{
name|directory
operator|.
name|deleteFile
argument_list|(
name|origFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|e
parameter_list|)
block|{             }
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"failed to delete file [{}]"
argument_list|,
name|ex
argument_list|,
name|origFile
argument_list|)
expr_stmt|;
block|}
comment|// now, rename the files... and fail it it won't work
name|store
operator|.
name|renameFile
argument_list|(
name|tempFile
argument_list|,
name|origFile
argument_list|)
expr_stmt|;
comment|// upon success, remove the temp file
name|tempFileIterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** cancel the recovery. calling this method will clean temporary files and release the store      * unless this object is in use (in which case it will be cleaned once all ongoing users call      * {@link #decRef()}      *      * if {@link #setWaitingRecoveryThread(Thread)} was used, the thread will be interrupted.      */
DECL|method|cancel
specifier|public
name|void
name|cancel
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
if|if
condition|(
name|finished
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"recovery canceled (reason: [{}])"
argument_list|,
name|reason
argument_list|)
expr_stmt|;
comment|// release the initial reference. recovery files will be cleaned as soon as ref count goes to zero, potentially now
name|decRef
argument_list|()
expr_stmt|;
specifier|final
name|Thread
name|thread
init|=
name|waitingRecoveryThread
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|thread
operator|!=
literal|null
condition|)
block|{
name|thread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * fail the recovery and call listener      *      * @param e exception that encapsulating the failure      * @param sendShardFailure indicates whether to notify the master of the shard failure      **/
DECL|method|fail
specifier|public
name|void
name|fail
parameter_list|(
name|RecoveryFailedException
name|e
parameter_list|,
name|boolean
name|sendShardFailure
parameter_list|)
block|{
if|if
condition|(
name|finished
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|listener
operator|.
name|onRecoveryFailure
argument_list|(
name|state
argument_list|,
name|e
argument_list|,
name|sendShardFailure
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// release the initial reference. recovery files will be cleaned as soon as ref count goes to zero, potentially now
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** mark the current recovery as done */
DECL|method|markAsDone
specifier|public
name|void
name|markAsDone
parameter_list|()
block|{
if|if
condition|(
name|finished
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
assert|assert
name|tempFileNames
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"not all temporary files are renamed"
assert|;
comment|// release the initial reference. recovery files will be cleaned as soon as ref count goes to zero, potentially now
name|decRef
argument_list|()
expr_stmt|;
name|listener
operator|.
name|onRecoveryDone
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getTempNameForFile
specifier|private
name|String
name|getTempNameForFile
parameter_list|(
name|String
name|origFile
parameter_list|)
block|{
return|return
name|tempFilePrefix
operator|+
name|origFile
return|;
block|}
comment|/** return true if the give file is a temporary file name issued by this recovery */
DECL|method|isTempFile
specifier|private
name|boolean
name|isTempFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
return|return
name|tempFileNames
operator|.
name|contains
argument_list|(
name|filename
argument_list|)
return|;
block|}
DECL|method|getOpenIndexOutput
specifier|public
name|IndexOutput
name|getOpenIndexOutput
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|ensureNotFinished
argument_list|()
expr_stmt|;
return|return
name|openIndexOutputs
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/** returns the original file name for a temporary file name issued by this recovery */
DECL|method|originalNameForTempFile
specifier|private
name|String
name|originalNameForTempFile
parameter_list|(
name|String
name|tempFile
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isTempFile
argument_list|(
name|tempFile
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"["
operator|+
name|tempFile
operator|+
literal|"] is not a temporary file made by this recovery"
argument_list|)
throw|;
block|}
return|return
name|tempFile
operator|.
name|substring
argument_list|(
name|tempFilePrefix
operator|.
name|length
argument_list|()
argument_list|)
return|;
block|}
comment|/** remove and {@link org.apache.lucene.store.IndexOutput} for a given file. It is the caller's responsibility to close it */
DECL|method|removeOpenIndexOutputs
specifier|public
name|IndexOutput
name|removeOpenIndexOutputs
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|ensureNotFinished
argument_list|()
expr_stmt|;
return|return
name|openIndexOutputs
operator|.
name|remove
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Creates an {@link org.apache.lucene.store.IndexOutput} for the given file name. Note that the      * IndexOutput actually point at a temporary file.      *<p/>      * Note: You can use {@link #getOpenIndexOutput(String)} with the same filename to retrieve the same IndexOutput      * at a later stage      */
DECL|method|openAndPutIndexOutput
specifier|public
name|IndexOutput
name|openAndPutIndexOutput
parameter_list|(
name|String
name|fileName
parameter_list|,
name|StoreFileMetaData
name|metaData
parameter_list|,
name|Store
name|store
parameter_list|)
throws|throws
name|IOException
block|{
name|ensureNotFinished
argument_list|()
expr_stmt|;
name|String
name|tempFileName
init|=
name|getTempNameForFile
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
comment|// add first, before it's created
name|tempFileNames
operator|.
name|add
argument_list|(
name|tempFileName
argument_list|)
expr_stmt|;
name|IndexOutput
name|indexOutput
init|=
name|store
operator|.
name|createVerifyingOutput
argument_list|(
name|tempFileName
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|,
name|metaData
argument_list|)
decl_stmt|;
name|openIndexOutputs
operator|.
name|put
argument_list|(
name|fileName
argument_list|,
name|indexOutput
argument_list|)
expr_stmt|;
return|return
name|indexOutput
return|;
block|}
annotation|@
name|Override
DECL|method|closeInternal
specifier|protected
name|void
name|closeInternal
parameter_list|()
block|{
try|try
block|{
comment|// clean open index outputs
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexOutput
argument_list|>
argument_list|>
name|iterator
init|=
name|openIndexOutputs
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|IndexOutput
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// trash temporary files
for|for
control|(
name|String
name|file
range|:
name|tempFileNames
control|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"cleaning temporary file [{}]"
argument_list|,
name|file
argument_list|)
expr_stmt|;
name|store
operator|.
name|deleteQuiet
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
name|legacyChecksums
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
comment|// free store. increment happens in constructor
name|store
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|shardId
operator|+
literal|" ["
operator|+
name|recoveryId
operator|+
literal|"]"
return|;
block|}
DECL|method|ensureNotFinished
specifier|private
name|void
name|ensureNotFinished
parameter_list|()
block|{
if|if
condition|(
name|finished
operator|.
name|get
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"RecoveryStatus is used after it was finished. Probably a mismatch between incRef/decRef calls"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

