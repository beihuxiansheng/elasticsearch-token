begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|NumericDocValuesField
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
name|document
operator|.
name|TextField
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
name|KeepOnlyLastCommitDeletionPolicy
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
name|SnapshotDeletionPolicy
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
name|Term
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
name|IndexSearcher
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
name|action
operator|.
name|index
operator|.
name|IndexRequest
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
name|Nullable
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
name|lucene
operator|.
name|uid
operator|.
name|Versions
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
name|common
operator|.
name|util
operator|.
name|BigArrays
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
name|Index
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
name|codec
operator|.
name|CodecService
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
name|engine
operator|.
name|Engine
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
name|engine
operator|.
name|EngineConfig
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
name|engine
operator|.
name|InternalEngine
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
name|engine
operator|.
name|InternalEngineTests
operator|.
name|TranslogHandler
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
name|fieldvisitor
operator|.
name|SingleFieldsVisitor
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
name|mapper
operator|.
name|ParseContext
operator|.
name|Document
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
name|mapper
operator|.
name|ParsedDocument
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
name|mapper
operator|.
name|SeqNoFieldMapper
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
name|mapper
operator|.
name|UidFieldMapper
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
name|DirectoryService
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
name|TranslogConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|DummyShardLock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|IndexSettingsModule
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
name|TestThreadPool
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
name|threadpool
operator|.
name|ThreadPool
operator|.
name|Cancellable
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
operator|.
name|Names
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|Locale
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
name|AtomicReference
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
name|Consumer
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
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueMillis
import|;
end_import

begin_comment
comment|/**  * Tests how {@linkplain RefreshListeners} interacts with {@linkplain InternalEngine}.  */
end_comment

begin_class
DECL|class|RefreshListenersTests
specifier|public
class|class
name|RefreshListenersTests
extends|extends
name|ESTestCase
block|{
DECL|field|listeners
specifier|private
name|RefreshListeners
name|listeners
decl_stmt|;
DECL|field|engine
specifier|private
name|Engine
name|engine
decl_stmt|;
DECL|field|maxListeners
specifier|private
specifier|volatile
name|int
name|maxListeners
decl_stmt|;
DECL|field|threadPool
specifier|private
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|store
specifier|private
name|Store
name|store
decl_stmt|;
annotation|@
name|Before
DECL|method|setupListeners
specifier|public
name|void
name|setupListeners
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Setup dependencies of the listeners
name|maxListeners
operator|=
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|listeners
operator|=
operator|new
name|RefreshListeners
argument_list|(
parameter_list|()
lambda|->
name|maxListeners
argument_list|,
parameter_list|()
lambda|->
name|engine
operator|.
name|refresh
argument_list|(
literal|"too-many-listeners"
argument_list|)
argument_list|,
comment|// Immediately run listeners rather than adding them to the listener thread pool like IndexShard does to simplify the test.
name|Runnable
operator|::
name|run
argument_list|,
name|logger
argument_list|)
expr_stmt|;
comment|// Now setup the InternalEngine which is much more complicated because we aren't mocking anything
name|threadPool
operator|=
operator|new
name|TestThreadPool
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSettings
name|indexSettings
init|=
name|IndexSettingsModule
operator|.
name|newIndexSettings
argument_list|(
literal|"index"
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
operator|new
name|Index
argument_list|(
literal|"index"
argument_list|,
literal|"_na_"
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|DirectoryService
name|directoryService
init|=
operator|new
name|DirectoryService
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Directory
name|newDirectory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|directory
return|;
block|}
block|}
decl_stmt|;
name|store
operator|=
operator|new
name|Store
argument_list|(
name|shardId
argument_list|,
name|indexSettings
argument_list|,
name|directoryService
argument_list|,
operator|new
name|DummyShardLock
argument_list|(
name|shardId
argument_list|)
argument_list|)
expr_stmt|;
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|TranslogConfig
name|translogConfig
init|=
operator|new
name|TranslogConfig
argument_list|(
name|shardId
argument_list|,
name|createTempDir
argument_list|(
literal|"translog"
argument_list|)
argument_list|,
name|indexSettings
argument_list|,
name|BigArrays
operator|.
name|NON_RECYCLING_INSTANCE
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|EventListener
name|eventListener
init|=
operator|new
name|Engine
operator|.
name|EventListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailedEngine
parameter_list|(
name|String
name|reason
parameter_list|,
annotation|@
name|Nullable
name|Exception
name|e
parameter_list|)
block|{
comment|// we don't need to notify anybody in this test
block|}
block|}
decl_stmt|;
name|TranslogHandler
name|translogHandler
init|=
operator|new
name|TranslogHandler
argument_list|(
name|xContentRegistry
argument_list|()
argument_list|,
name|shardId
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|EngineConfig
name|config
init|=
operator|new
name|EngineConfig
argument_list|(
name|EngineConfig
operator|.
name|OpenMode
operator|.
name|CREATE_INDEX_AND_TRANSLOG
argument_list|,
name|shardId
argument_list|,
name|threadPool
argument_list|,
name|indexSettings
argument_list|,
literal|null
argument_list|,
name|store
argument_list|,
operator|new
name|SnapshotDeletionPolicy
argument_list|(
operator|new
name|KeepOnlyLastCommitDeletionPolicy
argument_list|()
argument_list|)
argument_list|,
name|newMergePolicy
argument_list|()
argument_list|,
name|iwc
operator|.
name|getAnalyzer
argument_list|()
argument_list|,
name|iwc
operator|.
name|getSimilarity
argument_list|()
argument_list|,
operator|new
name|CodecService
argument_list|(
literal|null
argument_list|,
name|logger
argument_list|)
argument_list|,
name|eventListener
argument_list|,
name|translogHandler
argument_list|,
name|IndexSearcher
operator|.
name|getDefaultQueryCache
argument_list|()
argument_list|,
name|IndexSearcher
operator|.
name|getDefaultQueryCachingPolicy
argument_list|()
argument_list|,
name|translogConfig
argument_list|,
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|5
argument_list|)
argument_list|,
name|listeners
argument_list|,
name|IndexRequest
operator|.
name|UNSET_AUTO_GENERATED_TIMESTAMP
argument_list|)
decl_stmt|;
name|engine
operator|=
operator|new
name|InternalEngine
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|listeners
operator|.
name|setTranslog
argument_list|(
name|engine
operator|.
name|getTranslog
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDownListeners
specifier|public
name|void
name|tearDownListeners
parameter_list|()
throws|throws
name|Exception
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|engine
argument_list|,
name|store
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|testBeforeRefresh
specifier|public
name|void
name|testBeforeRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
name|Engine
operator|.
name|IndexResult
name|index
init|=
name|index
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|index
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
name|engine
operator|.
name|refresh
argument_list|(
literal|"I said so"
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testAfterRefresh
specifier|public
name|void
name|testAfterRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
name|Engine
operator|.
name|IndexResult
name|index
init|=
name|index
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|engine
operator|.
name|refresh
argument_list|(
literal|"I said so"
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|index
argument_list|(
name|randomFrom
argument_list|(
literal|"1"
comment|/* same document */
argument_list|,
literal|"2"
comment|/* different document */
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|engine
operator|.
name|refresh
argument_list|(
literal|"I said so"
argument_list|)
expr_stmt|;
block|}
block|}
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|index
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTooMany
specifier|public
name|void
name|testTooMany
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listeners
operator|.
name|refreshNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|Engine
operator|.
name|IndexResult
name|index
init|=
name|index
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
comment|// Fill the listener slots
name|List
argument_list|<
name|DummyRefreshListener
argument_list|>
name|nonForcedListeners
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|maxListeners
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
name|maxListeners
condition|;
name|i
operator|++
control|)
block|{
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|nonForcedListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|index
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|listeners
operator|.
name|refreshNeeded
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// We shouldn't have called any of them
for|for
control|(
name|DummyRefreshListener
name|listener
range|:
name|nonForcedListeners
control|)
block|{
name|assertNull
argument_list|(
literal|"Called listener too early!"
argument_list|,
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|maxListeners
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Add one more listener which should cause a refresh.
name|DummyRefreshListener
name|forcingListener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|index
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|forcingListener
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Forced listener wasn't forced?"
argument_list|,
name|forcingListener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|forcingListener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
comment|// That forces all the listeners through. It would be on the listener ThreadPool but we've made all of those execute immediately.
for|for
control|(
name|DummyRefreshListener
name|listener
range|:
name|nonForcedListeners
control|)
block|{
name|assertEquals
argument_list|(
literal|"Expected listener called with unforced refresh!"
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|,
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|listeners
operator|.
name|refreshNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testClose
specifier|public
name|void
name|testClose
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
name|Engine
operator|.
name|IndexResult
name|refreshedOperation
init|=
name|index
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|engine
operator|.
name|refresh
argument_list|(
literal|"I said so"
argument_list|)
expr_stmt|;
name|Engine
operator|.
name|IndexResult
name|unrefreshedOperation
init|=
name|index
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
block|{
comment|/* Closing flushed pending listeners as though they were refreshed. Since this can only happen when the index is closed and no              * longer useful there doesn't seem much point in sending the listener some kind of "I'm closed now, go away" enum value. */
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|unrefreshedOperation
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listeners
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|listeners
operator|.
name|refreshNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|// If you add a listener for an already refreshed location then it'll just fire even if closed
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|refreshedOperation
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|listeners
operator|.
name|refreshNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|{
comment|// But adding a listener to a non-refreshed location will fail
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|unrefreshedOperation
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"can't wait for refresh on a closed index"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|listeners
operator|.
name|refreshNeeded
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|listeners
operator|.
name|pendingCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Attempts to add a listener at the same time as a refresh occurs by having a background thread force a refresh as fast as it can while      * adding listeners. This can catch the situation where a refresh happens right as the listener is being added such that the listener      * misses the refresh and has to catch the next one. If the listener wasn't able to properly catch the next one then this would fail.      */
DECL|method|testConcurrentRefresh
specifier|public
name|void
name|testConcurrentRefresh
parameter_list|()
throws|throws
name|Exception
block|{
name|AtomicBoolean
name|run
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|Thread
name|refresher
init|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
while|while
condition|(
name|run
operator|.
name|get
argument_list|()
condition|)
block|{
name|engine
operator|.
name|refresh
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|refresher
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
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
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|Engine
operator|.
name|IndexResult
name|index
init|=
name|index
argument_list|(
literal|"1"
argument_list|)
decl_stmt|;
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|boolean
name|immediate
init|=
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|index
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
decl_stmt|;
if|if
condition|(
name|immediate
condition|)
block|{
name|assertNotNull
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertNotNull
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|run
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|refresher
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Uses a bunch of threads to index, wait for refresh, and non-realtime get documents to validate that they are visible after waiting      * regardless of what crazy sequence of events causes the refresh listener to fire.      */
DECL|method|testLotsOfThreads
specifier|public
name|void
name|testLotsOfThreads
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|threadCount
init|=
name|between
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|maxListeners
operator|=
name|between
argument_list|(
literal|1
argument_list|,
name|threadCount
operator|*
literal|2
argument_list|)
expr_stmt|;
comment|// This thread just refreshes every once in a while to cause trouble.
name|Cancellable
name|refresher
init|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
argument_list|(
parameter_list|()
lambda|->
name|engine
operator|.
name|refresh
argument_list|(
literal|"because test"
argument_list|)
argument_list|,
name|timeValueMillis
argument_list|(
literal|100
argument_list|)
argument_list|,
name|Names
operator|.
name|SAME
argument_list|)
decl_stmt|;
comment|// These threads add and block until the refresh makes the change visible and then do a non-realtime get.
name|Thread
index|[]
name|indexers
init|=
operator|new
name|Thread
index|[
name|threadCount
index|]
decl_stmt|;
for|for
control|(
name|int
name|thread
init|=
literal|0
init|;
name|thread
operator|<
name|threadCount
condition|;
name|thread
operator|++
control|)
block|{
specifier|final
name|String
name|threadId
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%04d"
argument_list|,
name|thread
argument_list|)
decl_stmt|;
name|indexers
index|[
name|thread
index|]
operator|=
operator|new
name|Thread
argument_list|(
parameter_list|()
lambda|->
block|{
for|for
control|(
name|int
name|iteration
init|=
literal|1
init|;
name|iteration
operator|<=
literal|50
condition|;
name|iteration
operator|++
control|)
block|{
try|try
block|{
name|String
name|testFieldValue
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%s%04d"
argument_list|,
name|threadId
argument_list|,
name|iteration
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|IndexResult
name|index
init|=
name|index
argument_list|(
name|threadId
argument_list|,
name|testFieldValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|iteration
argument_list|,
name|index
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|DummyRefreshListener
name|listener
init|=
operator|new
name|DummyRefreshListener
argument_list|()
decl_stmt|;
name|listeners
operator|.
name|addOrNotify
argument_list|(
name|index
operator|.
name|getTranslogLocation
argument_list|()
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|assertBusy
argument_list|(
parameter_list|()
lambda|->
name|assertNotNull
argument_list|(
literal|"listener never called"
argument_list|,
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|threadCount
operator|<
name|maxListeners
condition|)
block|{
name|assertFalse
argument_list|(
name|listener
operator|.
name|forcedRefresh
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|assertNoError
argument_list|()
expr_stmt|;
name|Engine
operator|.
name|Get
name|get
init|=
operator|new
name|Engine
operator|.
name|Get
argument_list|(
literal|false
argument_list|,
operator|new
name|Term
argument_list|(
literal|"_uid"
argument_list|,
literal|"test:"
operator|+
name|threadId
argument_list|)
argument_list|)
decl_stmt|;
try|try
init|(
name|Engine
operator|.
name|GetResult
name|getResult
init|=
name|engine
operator|.
name|get
argument_list|(
name|get
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
literal|"document not found"
argument_list|,
name|getResult
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|iteration
argument_list|,
name|getResult
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|SingleFieldsVisitor
name|visitor
init|=
operator|new
name|SingleFieldsVisitor
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|getResult
operator|.
name|docIdAndVersion
argument_list|()
operator|.
name|context
operator|.
name|reader
argument_list|()
operator|.
name|document
argument_list|(
name|getResult
operator|.
name|docIdAndVersion
argument_list|()
operator|.
name|docId
argument_list|,
name|visitor
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|testFieldValue
argument_list|)
argument_list|,
name|visitor
operator|.
name|fields
argument_list|()
operator|.
name|get
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failure on the ["
operator|+
name|iteration
operator|+
literal|"] iteration of thread ["
operator|+
name|threadId
operator|+
literal|"]"
argument_list|,
name|t
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|indexers
index|[
name|thread
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|indexer
range|:
name|indexers
control|)
block|{
name|indexer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|refresher
operator|.
name|cancel
argument_list|()
expr_stmt|;
block|}
DECL|method|index
specifier|private
name|Engine
operator|.
name|IndexResult
name|index
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|index
argument_list|(
name|id
argument_list|,
literal|"test"
argument_list|)
return|;
block|}
DECL|method|index
specifier|private
name|Engine
operator|.
name|IndexResult
name|index
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|testFieldValue
parameter_list|)
block|{
name|String
name|type
init|=
literal|"test"
decl_stmt|;
name|String
name|uid
init|=
name|type
operator|+
literal|":"
operator|+
name|id
decl_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"test"
argument_list|,
name|testFieldValue
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|Field
name|uidField
init|=
operator|new
name|Field
argument_list|(
literal|"_uid"
argument_list|,
name|type
operator|+
literal|":"
operator|+
name|id
argument_list|,
name|UidFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
argument_list|)
decl_stmt|;
name|Field
name|versionField
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"_version"
argument_list|,
name|Versions
operator|.
name|MATCH_ANY
argument_list|)
decl_stmt|;
name|SeqNoFieldMapper
operator|.
name|SequenceID
name|seqID
init|=
name|SeqNoFieldMapper
operator|.
name|SequenceID
operator|.
name|emptySeqID
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|uidField
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|versionField
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|seqID
operator|.
name|seqNo
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|seqID
operator|.
name|seqNoDocValue
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|seqID
operator|.
name|primaryTerm
argument_list|)
expr_stmt|;
name|BytesReference
name|source
init|=
operator|new
name|BytesArray
argument_list|(
operator|new
name|byte
index|[]
block|{
literal|1
block|}
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
operator|new
name|ParsedDocument
argument_list|(
name|versionField
argument_list|,
name|seqID
argument_list|,
name|id
argument_list|,
name|type
argument_list|,
literal|null
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|document
argument_list|)
argument_list|,
name|source
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Index
name|index
init|=
operator|new
name|Engine
operator|.
name|Index
argument_list|(
operator|new
name|Term
argument_list|(
literal|"_uid"
argument_list|,
name|uid
argument_list|)
argument_list|,
name|doc
argument_list|)
decl_stmt|;
return|return
name|engine
operator|.
name|index
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|class|DummyRefreshListener
specifier|private
specifier|static
class|class
name|DummyRefreshListener
implements|implements
name|Consumer
argument_list|<
name|Boolean
argument_list|>
block|{
comment|/**          * When the listener is called this captures it's only argument.          */
DECL|field|forcedRefresh
specifier|final
name|AtomicReference
argument_list|<
name|Boolean
argument_list|>
name|forcedRefresh
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|error
specifier|private
specifier|volatile
name|Exception
name|error
decl_stmt|;
annotation|@
name|Override
DECL|method|accept
specifier|public
name|void
name|accept
parameter_list|(
name|Boolean
name|forcedRefresh
parameter_list|)
block|{
try|try
block|{
name|Boolean
name|oldValue
init|=
name|this
operator|.
name|forcedRefresh
operator|.
name|getAndSet
argument_list|(
name|forcedRefresh
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
literal|"Listener called twice"
argument_list|,
name|oldValue
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|=
name|e
expr_stmt|;
block|}
block|}
DECL|method|assertNoError
specifier|public
name|void
name|assertNoError
parameter_list|()
block|{
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|error
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

