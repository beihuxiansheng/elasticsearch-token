begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
operator|.
name|local
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
name|index
operator|.
name|IndexWriter
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
name|IndexWriterConfig
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
name|SegmentInfos
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
name|ExceptionsHelper
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
name|io
operator|.
name|stream
operator|.
name|InputStreamStreamInput
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
name|Lucene
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
name|TimeValue
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
name|RecoveryStatus
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
name|SnapshotStatus
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
name|IndexShardState
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
name|TranslogStreams
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
name|fs
operator|.
name|FsTranslog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|Arrays
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
name|ScheduledFuture
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LocalIndexShardGateway
specifier|public
class|class
name|LocalIndexShardGateway
extends|extends
name|AbstractIndexShardComponent
implements|implements
name|IndexShardGateway
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|indexShard
specifier|private
specifier|final
name|InternalIndexShard
name|indexShard
decl_stmt|;
DECL|field|recoveryStatus
specifier|private
specifier|final
name|RecoveryStatus
name|recoveryStatus
init|=
operator|new
name|RecoveryStatus
argument_list|()
decl_stmt|;
DECL|field|flushScheduler
specifier|private
specifier|volatile
name|ScheduledFuture
name|flushScheduler
decl_stmt|;
DECL|field|syncInterval
specifier|private
specifier|final
name|TimeValue
name|syncInterval
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalIndexShardGateway
specifier|public
name|LocalIndexShardGateway
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
name|IndexShard
name|indexShard
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
name|threadPool
operator|=
name|threadPool
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
name|syncInterval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"sync"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|syncInterval
operator|.
name|millis
argument_list|()
operator|>
literal|0
condition|)
block|{
name|this
operator|.
name|indexShard
operator|.
name|translog
argument_list|()
operator|.
name|syncOnEachOperation
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|flushScheduler
operator|=
name|threadPool
operator|.
name|schedule
argument_list|(
name|syncInterval
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
operator|new
name|Sync
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|syncInterval
operator|.
name|millis
argument_list|()
operator|==
literal|0
condition|)
block|{
name|flushScheduler
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|indexShard
operator|.
name|translog
argument_list|()
operator|.
name|syncOnEachOperation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flushScheduler
operator|=
literal|null
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
literal|"local"
return|;
block|}
annotation|@
name|Override
DECL|method|recoveryStatus
specifier|public
name|RecoveryStatus
name|recoveryStatus
parameter_list|()
block|{
return|return
name|recoveryStatus
return|;
block|}
annotation|@
name|Override
DECL|method|recover
specifier|public
name|void
name|recover
parameter_list|(
name|boolean
name|indexShouldExists
parameter_list|,
name|RecoveryStatus
name|recoveryStatus
parameter_list|)
throws|throws
name|IndexShardGatewayRecoveryException
block|{
name|recoveryStatus
operator|.
name|index
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
name|recoveryStatus
operator|.
name|updateStage
argument_list|(
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|INDEX
argument_list|)
expr_stmt|;
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|translogId
init|=
operator|-
literal|1
decl_stmt|;
try|try
block|{
name|SegmentInfos
name|si
init|=
literal|null
decl_stmt|;
try|try
block|{
name|si
operator|=
name|Lucene
operator|.
name|readSegmentInfos
argument_list|(
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|directory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|String
name|files
init|=
literal|"_unknown_"
decl_stmt|;
try|try
block|{
name|files
operator|=
name|Arrays
operator|.
name|toString
argument_list|(
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|directory
argument_list|()
operator|.
name|listAll
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
name|files
operator|+=
literal|" (failure="
operator|+
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e1
argument_list|)
operator|+
literal|")"
expr_stmt|;
block|}
if|if
condition|(
name|indexShouldExists
operator|&&
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|indexStore
argument_list|()
operator|.
name|persistent
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|()
argument_list|,
literal|"shard allocated for local recovery (post api), should exist, but doesn't, current files: "
operator|+
name|files
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|si
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|indexShouldExists
condition|)
block|{
name|version
operator|=
name|si
operator|.
name|getVersion
argument_list|()
expr_stmt|;
if|if
condition|(
name|si
operator|.
name|getUserData
argument_list|()
operator|.
name|containsKey
argument_list|(
name|Translog
operator|.
name|TRANSLOG_ID_KEY
argument_list|)
condition|)
block|{
name|translogId
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|si
operator|.
name|getUserData
argument_list|()
operator|.
name|get
argument_list|(
name|Translog
operator|.
name|TRANSLOG_ID_KEY
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|translogId
operator|=
name|version
expr_stmt|;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"using existing shard data, translog id [{}]"
argument_list|,
name|translogId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// it exists on the directory, but shouldn't exist on the FS, its a leftover (possibly dangling)
comment|// its a "new index create" API, we have to do something, so better to clean it than use same data
name|logger
operator|.
name|trace
argument_list|(
literal|"cleaning existing shard, shouldn't exists"
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|directory
argument_list|()
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
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
literal|"failed to fetch index version after copying it over"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|updateVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
comment|// since we recover from local, just fill the files and size
try|try
block|{
name|int
name|numberOfFiles
init|=
literal|0
decl_stmt|;
name|long
name|totalSizeInBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|directory
argument_list|()
operator|.
name|listAll
argument_list|()
control|)
block|{
name|numberOfFiles
operator|++
expr_stmt|;
name|totalSizeInBytes
operator|+=
name|indexShard
operator|.
name|store
argument_list|()
operator|.
name|directory
argument_list|()
operator|.
name|fileLength
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|recoveryStatus
operator|.
name|index
argument_list|()
operator|.
name|files
argument_list|(
name|numberOfFiles
argument_list|,
name|totalSizeInBytes
argument_list|,
name|numberOfFiles
argument_list|,
name|totalSizeInBytes
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
name|recoveryStatus
operator|.
name|start
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
name|recoveryStatus
operator|.
name|updateStage
argument_list|(
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|START
argument_list|)
expr_stmt|;
if|if
condition|(
name|translogId
operator|==
operator|-
literal|1
condition|)
block|{
comment|// no translog files, bail
name|indexShard
operator|.
name|postRecovery
argument_list|(
literal|"post recovery from gateway, no translog"
argument_list|)
expr_stmt|;
comment|// no index, just start the shard and bail
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|checkIndexTime
argument_list|(
name|indexShard
operator|.
name|checkIndexTook
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// move an existing translog, if exists, to "recovering" state, and start reading from it
name|FsTranslog
name|translog
init|=
operator|(
name|FsTranslog
operator|)
name|indexShard
operator|.
name|translog
argument_list|()
decl_stmt|;
name|String
name|translogName
init|=
literal|"translog-"
operator|+
name|translogId
decl_stmt|;
name|String
name|recoverTranslogName
init|=
name|translogName
operator|+
literal|".recovering"
decl_stmt|;
name|File
name|recoveringTranslogFile
init|=
literal|null
decl_stmt|;
for|for
control|(
name|File
name|translogLocation
range|:
name|translog
operator|.
name|locations
argument_list|()
control|)
block|{
name|File
name|tmpRecoveringFile
init|=
operator|new
name|File
argument_list|(
name|translogLocation
argument_list|,
name|recoverTranslogName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|tmpRecoveringFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
name|tmpTranslogFile
init|=
operator|new
name|File
argument_list|(
name|translogLocation
argument_list|,
name|translogName
argument_list|)
decl_stmt|;
if|if
condition|(
name|tmpTranslogFile
operator|.
name|exists
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|tmpTranslogFile
operator|.
name|renameTo
argument_list|(
name|tmpRecoveringFile
argument_list|)
condition|)
block|{
name|recoveringTranslogFile
operator|=
name|tmpRecoveringFile
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
else|else
block|{
name|recoveringTranslogFile
operator|=
name|tmpRecoveringFile
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|recoveringTranslogFile
operator|==
literal|null
operator|||
operator|!
name|recoveringTranslogFile
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// no translog to recovery from, start and bail
comment|// no translog files, bail
name|indexShard
operator|.
name|postRecovery
argument_list|(
literal|"post recovery from gateway, no translog"
argument_list|)
expr_stmt|;
comment|// no index, just start the shard and bail
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|checkIndexTime
argument_list|(
name|indexShard
operator|.
name|checkIndexTook
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// recover from the translog file
name|indexShard
operator|.
name|performRecoveryPrepareForTranslog
argument_list|()
expr_stmt|;
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|start
argument_list|()
operator|.
name|checkIndexTime
argument_list|(
name|indexShard
operator|.
name|checkIndexTook
argument_list|()
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|translog
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
name|recoveryStatus
operator|.
name|updateStage
argument_list|(
name|RecoveryStatus
operator|.
name|Stage
operator|.
name|TRANSLOG
argument_list|)
expr_stmt|;
name|FileInputStream
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fs
operator|=
operator|new
name|FileInputStream
argument_list|(
name|recoveringTranslogFile
argument_list|)
expr_stmt|;
name|InputStreamStreamInput
name|si
init|=
operator|new
name|InputStreamStreamInput
argument_list|(
name|fs
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Translog
operator|.
name|Operation
name|operation
decl_stmt|;
try|try
block|{
name|int
name|opSize
init|=
name|si
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|operation
operator|=
name|TranslogStreams
operator|.
name|readTranslogOperation
argument_list|(
name|si
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
comment|// ignore, not properly written the last op
break|break;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore, not properly written last op
break|break;
block|}
try|try
block|{
name|indexShard
operator|.
name|performRecoveryOperation
argument_list|(
name|operation
argument_list|)
expr_stmt|;
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|addTranslogOperations
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticSearchException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|status
argument_list|()
operator|==
name|RestStatus
operator|.
name|BAD_REQUEST
condition|)
block|{
comment|// mainly for MapperParsingException and Failure to detect xcontent
name|logger
operator|.
name|info
argument_list|(
literal|"ignoring recovery of a corrupt translog entry"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// we failed to recovery, make sure to delete the translog file (and keep the recovering one)
name|indexShard
operator|.
name|translog
argument_list|()
operator|.
name|closeWithDelete
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IndexShardGatewayRecoveryException
argument_list|(
name|shardId
argument_list|,
literal|"failed to recover shard"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|fs
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
name|indexShard
operator|.
name|performRecoveryFinalization
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|recoveringTranslogFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|time
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|recoveryStatus
operator|.
name|translog
argument_list|()
operator|.
name|startTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
literal|"local"
return|;
block|}
annotation|@
name|Override
DECL|method|snapshot
specifier|public
name|SnapshotStatus
name|snapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|lastSnapshotStatus
specifier|public
name|SnapshotStatus
name|lastSnapshotStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|currentSnapshotStatus
specifier|public
name|SnapshotStatus
name|currentSnapshotStatus
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|requiresSnapshot
specifier|public
name|boolean
name|requiresSnapshot
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|requiresSnapshotScheduling
specifier|public
name|boolean
name|requiresSnapshotScheduling
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|flushScheduler
operator|!=
literal|null
condition|)
block|{
name|flushScheduler
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|obtainSnapshotLock
specifier|public
name|SnapshotLock
name|obtainSnapshotLock
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|NO_SNAPSHOT_LOCK
return|;
block|}
DECL|class|Sync
class|class
name|Sync
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// don't re-schedule  if its closed..., we are done
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|CLOSED
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|STARTED
operator|&&
name|indexShard
operator|.
name|translog
argument_list|()
operator|.
name|syncNeeded
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SNAPSHOT
argument_list|)
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
name|indexShard
operator|.
name|translog
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
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|==
name|IndexShardState
operator|.
name|STARTED
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to sync translog"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|indexShard
operator|.
name|state
argument_list|()
operator|!=
name|IndexShardState
operator|.
name|CLOSED
condition|)
block|{
name|flushScheduler
operator|=
name|threadPool
operator|.
name|schedule
argument_list|(
name|syncInterval
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|Sync
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|flushScheduler
operator|=
name|threadPool
operator|.
name|schedule
argument_list|(
name|syncInterval
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|Sync
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

