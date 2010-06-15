begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway.hdfs
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|hdfs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|*
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
name|index
operator|.
name|IndexReader
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
name|IndexInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
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
name|Lists
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
name|lucene
operator|.
name|Directories
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
name|index
operator|.
name|deletionpolicy
operator|.
name|SnapshotIndexCommit
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
name|gateway
operator|.
name|IndexGateway
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
name|gateway
operator|.
name|IndexShardGateway
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
name|gateway
operator|.
name|IndexShardGatewayRecoveryException
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
name|gateway
operator|.
name|IndexShardGatewaySnapshotFailedException
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
name|recovery
operator|.
name|throttler
operator|.
name|RecoveryThrottler
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
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|SizeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|SizeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|DataInputStreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|DataOutputStreamOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|concurrent
operator|.
name|CountDownLatch
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|Directories
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|translog
operator|.
name|TranslogStreams
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|HdfsIndexShardGateway
specifier|public
class|class
name|HdfsIndexShardGateway
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|IndexShardGateway
block|{
DECL|field|indexShard
specifier|private
specifier|final
name|InternalIndexShard
name|indexShard
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|recoveryThrottler
specifier|private
specifier|final
name|RecoveryThrottler
name|recoveryThrottler
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|fileSystem
specifier|private
specifier|final
name|FileSystem
name|fileSystem
decl_stmt|;
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|indexPath
specifier|private
specifier|final
name|Path
name|indexPath
decl_stmt|;
DECL|field|translogPath
specifier|private
specifier|final
name|Path
name|translogPath
decl_stmt|;
DECL|field|currentTranslogStream
specifier|private
specifier|volatile
name|FSDataOutputStream
name|currentTranslogStream
init|=
literal|null
decl_stmt|;
DECL|method|HdfsIndexShardGateway
annotation|@
name|Inject
specifier|public
name|HdfsIndexShardGateway
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|IndexGateway
name|hdfsIndexGateway
parameter_list|,
name|IndexShard
name|indexShard
parameter_list|,
name|Store
name|store
parameter_list|,
name|RecoveryThrottler
name|recoveryThrottler
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
name|indexShard
operator|=
operator|(
name|InternalIndexShard
operator|)
name|indexShard
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|recoveryThrottler
operator|=
name|recoveryThrottler
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|fileSystem
operator|=
operator|(
operator|(
name|HdfsIndexGateway
operator|)
name|hdfsIndexGateway
operator|)
operator|.
name|fileSystem
argument_list|()
expr_stmt|;
name|this
operator|.
name|path
operator|=
operator|new
name|Path
argument_list|(
operator|(
operator|(
name|HdfsIndexGateway
operator|)
name|hdfsIndexGateway
operator|)
operator|.
name|indexPath
argument_list|()
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
literal|"index"
argument_list|)
expr_stmt|;
name|this
operator|.
name|translogPath
operator|=
operator|new
name|Path
argument_list|(
name|path
argument_list|,
literal|"translog"
argument_list|)
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
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|currentTranslogStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|currentTranslogStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
if|if
condition|(
name|delete
condition|)
block|{
try|try
block|{
name|fileSystem
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to delete [{}]"
argument_list|,
name|e
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|requiresSnapshotScheduling
annotation|@
name|Override
specifier|public
name|boolean
name|requiresSnapshotScheduling
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|recover
annotation|@
name|Override
specifier|public
name|RecoveryStatus
name|recover
parameter_list|()
throws|throws
name|IndexShardGatewayRecoveryException
block|{
name|RecoveryStatus
operator|.
name|Index
name|recoveryStatusIndex
init|=
name|recoverIndex
argument_list|()
decl_stmt|;
name|RecoveryStatus
operator|.
name|Translog
name|recoveryStatusTranslog
init|=
name|recoverTranslog
argument_list|()
decl_stmt|;
return|return
operator|new
name|RecoveryStatus
argument_list|(
name|recoveryStatusIndex
argument_list|,
name|recoveryStatusTranslog
argument_list|)
return|;
block|}
DECL|method|snapshot
annotation|@
name|Override
specifier|public
name|SnapshotStatus
name|snapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|long
name|totalTimeStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|boolean
name|indexDirty
init|=
literal|false
decl_stmt|;
name|boolean
name|translogDirty
init|=
literal|false
decl_stmt|;
specifier|final
name|SnapshotIndexCommit
name|snapshotIndexCommit
init|=
name|snapshot
operator|.
name|indexCommit
argument_list|()
decl_stmt|;
specifier|final
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
init|=
name|snapshot
operator|.
name|translogSnapshot
argument_list|()
decl_stmt|;
name|int
name|indexNumberOfFiles
init|=
literal|0
decl_stmt|;
name|long
name|indexTotalFilesSize
init|=
literal|0
decl_stmt|;
name|long
name|indexTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|snapshot
operator|.
name|indexChanged
argument_list|()
condition|)
block|{
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|indexDirty
operator|=
literal|true
expr_stmt|;
comment|// snapshot into the index
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|snapshotIndexCommit
operator|.
name|getFiles
argument_list|()
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|lastException
init|=
operator|new
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|snapshotIndexCommit
operator|.
name|getFiles
argument_list|()
control|)
block|{
comment|// don't copy over the segments file, it will be copied over later on as part of the
comment|// final snapshot phase
if|if
condition|(
name|fileName
operator|.
name|equals
argument_list|(
name|snapshotIndexCommit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
condition|)
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|IndexInput
name|indexInput
init|=
literal|null
decl_stmt|;
try|try
block|{
name|indexInput
operator|=
name|snapshotIndexCommit
operator|.
name|getDirectory
argument_list|()
operator|.
name|openInput
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fileSystem
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|indexPath
argument_list|,
name|fileName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileStatus
operator|.
name|getLen
argument_list|()
operator|==
name|indexInput
operator|.
name|length
argument_list|()
condition|)
block|{
comment|// we assume its the same one, no need to copy
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// that's fine!
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Failed to verify file equality based on length, copying..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|indexInput
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|indexInput
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
name|indexNumberOfFiles
operator|++
expr_stmt|;
try|try
block|{
name|indexTotalFilesSize
operator|+=
name|snapshotIndexCommit
operator|.
name|getDirectory
argument_list|()
operator|.
name|fileLength
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore...
block|}
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|Path
name|copyTo
init|=
operator|new
name|Path
argument_list|(
name|indexPath
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|fileStream
decl_stmt|;
try|try
block|{
name|fileStream
operator|=
name|fileSystem
operator|.
name|create
argument_list|(
name|copyTo
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|copyFromDirectory
argument_list|(
name|snapshotIndexCommit
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|fileName
argument_list|,
name|fileStream
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|lastException
operator|.
name|set
argument_list|(
operator|new
name|IndexShardGatewaySnapshotFailedException
argument_list|(
name|shardId
argument_list|,
literal|"Failed to copy to ["
operator|+
name|copyTo
operator|+
literal|"], from dir ["
operator|+
name|snapshotIndexCommit
operator|.
name|getDirectory
argument_list|()
operator|+
literal|"] and file ["
operator|+
name|fileName
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|lastException
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastException
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardGatewaySnapshotFailedException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to perform snapshot (index files)"
argument_list|,
name|lastException
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
name|indexTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
block|}
name|int
name|translogNumberOfOperations
init|=
literal|0
decl_stmt|;
name|long
name|translogTime
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|snapshot
operator|.
name|newTranslogCreated
argument_list|()
operator|||
name|currentTranslogStream
operator|==
literal|null
condition|)
block|{
name|translogDirty
operator|=
literal|true
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// a new translog, close the current stream
if|if
condition|(
name|currentTranslogStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|currentTranslogStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
name|Path
name|currentTranslogPath
init|=
operator|new
name|Path
argument_list|(
name|translogPath
argument_list|,
literal|"translog-"
operator|+
name|translogSnapshot
operator|.
name|translogId
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|currentTranslogStream
operator|=
name|fileSystem
operator|.
name|create
argument_list|(
name|currentTranslogPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|StreamOutput
name|out
init|=
operator|new
name|DataOutputStreamOutput
argument_list|(
name|currentTranslogStream
argument_list|)
decl_stmt|;
for|for
control|(
name|Translog
operator|.
name|Operation
name|operation
range|:
name|translogSnapshot
control|)
block|{
name|translogNumberOfOperations
operator|++
expr_stmt|;
name|writeTranslogOperation
argument_list|(
name|out
argument_list|,
name|operation
argument_list|)
expr_stmt|;
block|}
name|currentTranslogStream
operator|.
name|flush
argument_list|()
expr_stmt|;
name|currentTranslogStream
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
name|currentTranslogPath
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|currentTranslogStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|currentTranslogStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
name|currentTranslogStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|IndexShardGatewaySnapshotFailedException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to snapshot translog into ["
operator|+
name|currentTranslogPath
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|translogTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|snapshot
operator|.
name|sameTranslogNewOperations
argument_list|()
condition|)
block|{
name|translogDirty
operator|=
literal|true
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|StreamOutput
name|out
init|=
operator|new
name|DataOutputStreamOutput
argument_list|(
name|currentTranslogStream
argument_list|)
decl_stmt|;
for|for
control|(
name|Translog
operator|.
name|Operation
name|operation
range|:
name|translogSnapshot
operator|.
name|skipTo
argument_list|(
name|snapshot
operator|.
name|lastTranslogSize
argument_list|()
argument_list|)
control|)
block|{
name|translogNumberOfOperations
operator|++
expr_stmt|;
name|writeTranslogOperation
argument_list|(
name|out
argument_list|,
name|operation
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|currentTranslogStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
comment|// ignore
block|}
finally|finally
block|{
name|currentTranslogStream
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|translogTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
expr_stmt|;
block|}
comment|// now write the segments file and update the translog header
if|if
condition|(
name|indexDirty
condition|)
block|{
name|Path
name|segmentsPath
init|=
operator|new
name|Path
argument_list|(
name|indexPath
argument_list|,
name|snapshotIndexCommit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|indexNumberOfFiles
operator|++
expr_stmt|;
name|indexTotalFilesSize
operator|+=
name|snapshotIndexCommit
operator|.
name|getDirectory
argument_list|()
operator|.
name|fileLength
argument_list|(
name|snapshotIndexCommit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|time
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|fileStream
decl_stmt|;
name|fileStream
operator|=
name|fileSystem
operator|.
name|create
argument_list|(
name|segmentsPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|copyFromDirectory
argument_list|(
name|snapshotIndexCommit
operator|.
name|getDirectory
argument_list|()
argument_list|,
name|snapshotIndexCommit
operator|.
name|getSegmentsFileName
argument_list|()
argument_list|,
name|fileStream
argument_list|)
expr_stmt|;
name|indexTime
operator|+=
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|time
operator|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IndexShardGatewaySnapshotFailedException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to finalize index snapshot into ["
operator|+
name|segmentsPath
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|// delete the old translog
if|if
condition|(
name|snapshot
operator|.
name|newTranslogCreated
argument_list|()
condition|)
block|{
try|try
block|{
name|fileSystem
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|translogPath
argument_list|,
literal|"translog-"
operator|+
name|snapshot
operator|.
name|lastTranslogId
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
comment|// delete files that no longer exists in the index
if|if
condition|(
name|indexDirty
condition|)
block|{
try|try
block|{
name|FileStatus
index|[]
name|existingFiles
init|=
name|fileSystem
operator|.
name|listStatus
argument_list|(
name|indexPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingFiles
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|FileStatus
name|existingFile
range|:
name|existingFiles
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|fileName
range|:
name|snapshotIndexCommit
operator|.
name|getFiles
argument_list|()
control|)
block|{
if|if
condition|(
name|existingFile
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|fileSystem
operator|.
name|delete
argument_list|(
name|existingFile
operator|.
name|getPath
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// no worries, failed to clean old ones, will clean them later
block|}
block|}
return|return
operator|new
name|SnapshotStatus
argument_list|(
operator|new
name|TimeValue
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|totalTimeStart
argument_list|)
argument_list|,
operator|new
name|SnapshotStatus
operator|.
name|Index
argument_list|(
name|indexNumberOfFiles
argument_list|,
operator|new
name|SizeValue
argument_list|(
name|indexTotalFilesSize
argument_list|)
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|indexTime
argument_list|)
argument_list|)
argument_list|,
operator|new
name|SnapshotStatus
operator|.
name|Translog
argument_list|(
name|translogNumberOfOperations
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|translogTime
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|recoverIndex
specifier|private
name|RecoveryStatus
operator|.
name|Index
name|recoverIndex
parameter_list|()
throws|throws
name|IndexShardGatewayRecoveryException
block|{
name|FileStatus
index|[]
name|files
decl_stmt|;
try|try
block|{
name|files
operator|=
name|fileSystem
operator|.
name|listStatus
argument_list|(
name|indexPath
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
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to list files"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|files
operator|==
literal|null
operator|||
name|files
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|RecoveryStatus
operator|.
name|Index
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
operator|new
name|SizeValue
argument_list|(
literal|0
argument_list|,
name|SizeUnit
operator|.
name|BYTES
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|files
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|lastException
init|=
operator|new
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|throttlingWaitTime
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FileStatus
name|file
range|:
name|files
control|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|long
name|throttlingStartTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|recoveryThrottler
operator|.
name|tryStream
argument_list|(
name|shardId
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|recoveryThrottler
operator|.
name|throttleInterval
argument_list|()
operator|.
name|millis
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|throttlingWaitTime
operator|.
name|addAndGet
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|throttlingStartTime
argument_list|)
expr_stmt|;
name|FSDataInputStream
name|fileStream
init|=
name|fileSystem
operator|.
name|open
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Directories
operator|.
name|copyToDirectory
argument_list|(
name|fileStream
argument_list|,
name|store
operator|.
name|directory
argument_list|()
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Failed to read ["
operator|+
name|file
operator|+
literal|"] into ["
operator|+
name|store
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|lastException
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|recoveryThrottler
operator|.
name|streamDone
argument_list|(
name|shardId
argument_list|,
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|lastException
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lastException
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to recover index files"
argument_list|,
name|lastException
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
name|long
name|totalSize
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FileStatus
name|file
range|:
name|files
control|)
block|{
name|totalSize
operator|+=
name|file
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
if|if
condition|(
name|IndexReader
operator|.
name|indexExists
argument_list|(
name|store
operator|.
name|directory
argument_list|()
argument_list|)
condition|)
block|{
name|version
operator|=
name|IndexReader
operator|.
name|getCurrentVersion
argument_list|(
name|store
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to fetch index version after copying it over"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|RecoveryStatus
operator|.
name|Index
argument_list|(
name|version
argument_list|,
name|files
operator|.
name|length
argument_list|,
operator|new
name|SizeValue
argument_list|(
name|totalSize
argument_list|,
name|SizeUnit
operator|.
name|BYTES
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|throttlingWaitTime
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|recoverTranslog
specifier|private
name|RecoveryStatus
operator|.
name|Translog
name|recoverTranslog
parameter_list|()
throws|throws
name|IndexShardGatewayRecoveryException
block|{
name|FSDataInputStream
name|fileStream
init|=
literal|null
decl_stmt|;
try|try
block|{
name|long
name|recoveryTranslogId
init|=
name|findLatestTranslogId
argument_list|()
decl_stmt|;
if|if
condition|(
name|recoveryTranslogId
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no recovery file found, start the shard and bail
name|indexShard
operator|.
name|start
argument_list|()
expr_stmt|;
return|return
operator|new
name|RecoveryStatus
operator|.
name|Translog
argument_list|(
operator|-
literal|1
argument_list|,
literal|0
argument_list|,
operator|new
name|SizeValue
argument_list|(
literal|0
argument_list|,
name|SizeUnit
operator|.
name|BYTES
argument_list|)
argument_list|)
return|;
block|}
name|FileStatus
name|status
init|=
name|fileSystem
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|translogPath
argument_list|,
literal|"translog-"
operator|+
name|recoveryTranslogId
argument_list|)
argument_list|)
decl_stmt|;
name|fileStream
operator|=
name|fileSystem
operator|.
name|open
argument_list|(
name|status
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|Translog
operator|.
name|Operation
argument_list|>
name|operations
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
name|operations
operator|.
name|add
argument_list|(
name|readTranslogOperation
argument_list|(
operator|new
name|DataInputStreamInput
argument_list|(
name|fileStream
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// reached end of stream
break|break;
block|}
block|}
name|indexShard
operator|.
name|performRecovery
argument_list|(
name|operations
argument_list|)
expr_stmt|;
return|return
operator|new
name|RecoveryStatus
operator|.
name|Translog
argument_list|(
name|recoveryTranslogId
argument_list|,
name|operations
operator|.
name|size
argument_list|()
argument_list|,
operator|new
name|SizeValue
argument_list|(
name|status
operator|.
name|getLen
argument_list|()
argument_list|,
name|SizeUnit
operator|.
name|BYTES
argument_list|)
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
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"Failed to perform recovery of translog"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|fileStream
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|fileStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
block|}
block|}
DECL|method|findLatestTranslogId
specifier|private
name|long
name|findLatestTranslogId
parameter_list|()
throws|throws
name|IOException
block|{
name|FileStatus
index|[]
name|files
init|=
name|fileSystem
operator|.
name|listStatus
argument_list|(
name|translogPath
argument_list|,
operator|new
name|PathFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
name|path
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"translog-"
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|files
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|long
name|index
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|FileStatus
name|file
range|:
name|files
control|)
block|{
name|String
name|name
init|=
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|long
name|fileIndex
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileIndex
operator|>=
name|index
condition|)
block|{
name|index
operator|=
name|fileIndex
expr_stmt|;
block|}
block|}
return|return
name|index
return|;
block|}
block|}
end_class

end_unit

