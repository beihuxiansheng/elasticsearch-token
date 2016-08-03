begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
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
name|DirectoryReader
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
name|IndexCommit
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|SearcherFactory
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
name|search
operator|.
name|SearcherManager
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
name|lucene
operator|.
name|index
operator|.
name|ElasticsearchDirectoryReader
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
name|translog
operator|.
name|Translog
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
name|List
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
name|Function
import|;
end_import

begin_comment
comment|/**  * ShadowEngine is a specialized engine that only allows read-only operations  * on the underlying Lucene index. An {@code IndexReader} is opened instead of  * an {@code IndexWriter}. All methods that would usually perform write  * operations are no-ops, this means:  *  * - No operations are written to or read from the translog  * - Create, Index, and Delete do nothing  * - Flush does not fsync any files, or make any on-disk changes  *  * In order for new segments to become visible, the ShadowEngine may perform  * stage1 of the traditional recovery process (copying segment files) from a  * regular primary (which uses {@link org.elasticsearch.index.engine.InternalEngine})  *  * Notice that since this Engine does not deal with the translog, any  * {@link #get(Get get)} request goes directly to the searcher, meaning it is  * non-realtime.  */
end_comment

begin_class
DECL|class|ShadowEngine
specifier|public
class|class
name|ShadowEngine
extends|extends
name|Engine
block|{
comment|/** how long to wait for an index to exist */
DECL|field|NONEXISTENT_INDEX_RETRY_WAIT
specifier|public
specifier|static
specifier|final
name|String
name|NONEXISTENT_INDEX_RETRY_WAIT
init|=
literal|"index.shadow.wait_for_initial_commit"
decl_stmt|;
DECL|field|DEFAULT_NONEXISTENT_INDEX_RETRY_WAIT
specifier|public
specifier|static
specifier|final
name|TimeValue
name|DEFAULT_NONEXISTENT_INDEX_RETRY_WAIT
init|=
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|5
argument_list|)
decl_stmt|;
DECL|field|searcherManager
specifier|private
specifier|volatile
name|SearcherManager
name|searcherManager
decl_stmt|;
DECL|field|lastCommittedSegmentInfos
specifier|private
specifier|volatile
name|SegmentInfos
name|lastCommittedSegmentInfos
decl_stmt|;
DECL|method|ShadowEngine
specifier|public
name|ShadowEngine
parameter_list|(
name|EngineConfig
name|engineConfig
parameter_list|)
block|{
name|super
argument_list|(
name|engineConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|engineConfig
operator|.
name|getRefreshListeners
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ShadowEngine doesn't support RefreshListeners"
argument_list|)
throw|;
block|}
name|SearcherFactory
name|searcherFactory
init|=
operator|new
name|EngineSearcherFactory
argument_list|(
name|engineConfig
argument_list|)
decl_stmt|;
specifier|final
name|long
name|nonexistentRetryTime
init|=
name|engineConfig
operator|.
name|getIndexSettings
argument_list|()
operator|.
name|getSettings
argument_list|()
operator|.
name|getAsTime
argument_list|(
name|NONEXISTENT_INDEX_RETRY_WAIT
argument_list|,
name|DEFAULT_NONEXISTENT_INDEX_RETRY_WAIT
argument_list|)
operator|.
name|getMillis
argument_list|()
decl_stmt|;
try|try
block|{
name|DirectoryReader
name|reader
init|=
literal|null
decl_stmt|;
name|store
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
if|if
condition|(
name|Lucene
operator|.
name|waitForIndex
argument_list|(
name|store
operator|.
name|directory
argument_list|()
argument_list|,
name|nonexistentRetryTime
argument_list|)
condition|)
block|{
name|reader
operator|=
name|ElasticsearchDirectoryReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|store
operator|.
name|directory
argument_list|()
argument_list|)
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|this
operator|.
name|searcherManager
operator|=
operator|new
name|SearcherManager
argument_list|(
name|reader
argument_list|,
name|searcherFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastCommittedSegmentInfos
operator|=
name|readLastCommittedSegmentInfos
argument_list|(
name|searcherManager
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"failed to open a shadow engine after"
operator|+
name|nonexistentRetryTime
operator|+
literal|"ms, "
operator|+
literal|"directory is not an index"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to create new reader"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
operator|==
literal|false
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|store
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|EngineCreationFailureException
argument_list|(
name|shardId
argument_list|,
literal|"failed to open index reader"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|logger
operator|.
name|trace
argument_list|(
literal|"created new ShadowEngine"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|boolean
name|index
parameter_list|(
name|Index
name|index
parameter_list|)
throws|throws
name|EngineException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|shardId
operator|+
literal|" index operation not allowed on shadow engine"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|Delete
name|delete
parameter_list|)
throws|throws
name|EngineException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|shardId
operator|+
literal|" delete operation not allowed on shadow engine"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|syncFlush
specifier|public
name|SyncedFlushResult
name|syncFlush
parameter_list|(
name|String
name|syncId
parameter_list|,
name|CommitId
name|expectedCommitId
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
name|shardId
operator|+
literal|" sync commit operation not allowed on shadow engine"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|CommitId
name|flush
parameter_list|()
throws|throws
name|EngineException
block|{
return|return
name|flush
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|flush
specifier|public
name|CommitId
name|flush
parameter_list|(
name|boolean
name|force
parameter_list|,
name|boolean
name|waitIfOngoing
parameter_list|)
throws|throws
name|EngineException
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"skipping FLUSH on shadow engine"
argument_list|)
expr_stmt|;
comment|// reread the last committed segment infos
name|refresh
argument_list|(
literal|"flush"
argument_list|)
expr_stmt|;
comment|/*          * we have to inc-ref the store here since if the engine is closed by a tragic event          * we don't acquire the write lock and wait until we have exclusive access. This might also          * dec the store reference which can essentially close the store and unless we can inc the reference          * we can't use it.          */
name|store
operator|.
name|incRef
argument_list|()
expr_stmt|;
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
comment|// reread the last committed segment infos
name|lastCommittedSegmentInfos
operator|=
name|readLastCommittedSegmentInfos
argument_list|(
name|searcherManager
argument_list|,
name|store
argument_list|)
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
name|isClosed
operator|.
name|get
argument_list|()
operator|==
literal|false
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to read latest segment infos on flush"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|Lucene
operator|.
name|isCorruptionException
argument_list|(
name|e
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FlushFailedEngineException
argument_list|(
name|shardId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
name|store
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|CommitId
argument_list|(
name|lastCommittedSegmentInfos
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|forceMerge
specifier|public
name|void
name|forceMerge
parameter_list|(
name|boolean
name|flush
parameter_list|,
name|int
name|maxNumSegments
parameter_list|,
name|boolean
name|onlyExpungeDeletes
parameter_list|,
name|boolean
name|upgrade
parameter_list|,
name|boolean
name|upgradeOnlyAncientSegments
parameter_list|)
throws|throws
name|EngineException
block|{
comment|// no-op
name|logger
operator|.
name|trace
argument_list|(
literal|"skipping FORCE-MERGE on shadow engine"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|GetResult
name|get
parameter_list|(
name|Get
name|get
parameter_list|,
name|Function
argument_list|<
name|String
argument_list|,
name|Searcher
argument_list|>
name|searcherFacotry
parameter_list|)
throws|throws
name|EngineException
block|{
comment|// There is no translog, so we can get it directly from the searcher
return|return
name|getFromSearcher
argument_list|(
name|get
argument_list|,
name|searcherFacotry
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getTranslog
specifier|public
name|Translog
name|getTranslog
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"shadow engines don't have translogs"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|segments
specifier|public
name|List
argument_list|<
name|Segment
argument_list|>
name|segments
parameter_list|(
name|boolean
name|verbose
parameter_list|)
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
name|Segment
index|[]
name|segmentsArr
init|=
name|getSegmentInfo
argument_list|(
name|lastCommittedSegmentInfos
argument_list|,
name|verbose
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|segmentsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// hard code all segments as committed, because they are in
comment|// order for the shadow replica to see them
name|segmentsArr
index|[
name|i
index|]
operator|.
name|committed
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|segmentsArr
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|refresh
specifier|public
name|void
name|refresh
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|EngineException
block|{
comment|// we obtain a read lock here, since we don't want a flush to happen while we are refreshing
comment|// since it flushes the index as well (though, in terms of concurrency, we are allowed to do it)
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
name|ensureOpen
argument_list|()
expr_stmt|;
name|searcherManager
operator|.
name|maybeRefreshBlocking
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AlreadyClosedException
name|e
parameter_list|)
block|{
name|ensureOpen
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EngineClosedException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|failEngine
argument_list|(
literal|"refresh failed"
argument_list|,
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
operator|new
name|RefreshFailedEngineException
argument_list|(
name|shardId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|acquireIndexCommit
specifier|public
name|IndexCommit
name|acquireIndexCommit
parameter_list|(
name|boolean
name|flushFirst
parameter_list|)
throws|throws
name|EngineException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Can not take snapshot from a shadow engine"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getSearcherManager
specifier|protected
name|SearcherManager
name|getSearcherManager
parameter_list|()
block|{
return|return
name|searcherManager
return|;
block|}
annotation|@
name|Override
DECL|method|closeNoLock
specifier|protected
name|void
name|closeNoLock
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
if|if
condition|(
name|isClosed
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
name|logger
operator|.
name|debug
argument_list|(
literal|"shadow replica close searcher manager refCount: {}"
argument_list|,
name|store
operator|.
name|refCount
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|searcherManager
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
name|warn
argument_list|(
literal|"shadow replica failed to close searcher manager"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|store
operator|.
name|decRef
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|getLastCommittedSegmentInfos
specifier|protected
name|SegmentInfos
name|getLastCommittedSegmentInfos
parameter_list|()
block|{
return|return
name|lastCommittedSegmentInfos
return|;
block|}
annotation|@
name|Override
DECL|method|getIndexBufferRAMBytesUsed
specifier|public
name|long
name|getIndexBufferRAMBytesUsed
parameter_list|()
block|{
comment|// No IndexWriter nor version map
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ShadowEngine has no IndexWriter"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|writeIndexingBuffer
specifier|public
name|void
name|writeIndexingBuffer
parameter_list|()
block|{
comment|// No indexing buffer
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ShadowEngine has no IndexWriter"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|activateThrottling
specifier|public
name|void
name|activateThrottling
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ShadowEngine has no IndexWriter"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|deactivateThrottling
specifier|public
name|void
name|deactivateThrottling
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"ShadowEngine has no IndexWriter"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|recoverFromTranslog
specifier|public
name|Engine
name|recoverFromTranslog
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"can't recover on a shadow engine"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

