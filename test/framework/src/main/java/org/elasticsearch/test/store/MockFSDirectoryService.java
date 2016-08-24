begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|store
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|SeedUtils
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomPicks
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
name|CheckIndex
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
name|BaseDirectoryWrapper
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
name|store
operator|.
name|LockFactory
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
name|LockObtainFailedException
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
name|MockDirectoryWrapper
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
name|StoreRateLimiting
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
name|LuceneTestCase
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
name|TestRuleMarkFailure
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
name|metadata
operator|.
name|IndexMetaData
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
name|Setting
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
name|Setting
operator|.
name|Property
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
name|IndexModule
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
name|ShardPath
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
name|FsDirectoryService
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
name|IndexStore
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
name|test
operator|.
name|ESIntegTestCase
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
name|junit
operator|.
name|Assert
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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Random
import|;
end_import

begin_class
DECL|class|MockFSDirectoryService
specifier|public
class|class
name|MockFSDirectoryService
extends|extends
name|FsDirectoryService
block|{
DECL|field|RANDOM_IO_EXCEPTION_RATE_ON_OPEN_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Double
argument_list|>
name|RANDOM_IO_EXCEPTION_RATE_ON_OPEN_SETTING
init|=
name|Setting
operator|.
name|doubleSetting
argument_list|(
literal|"index.store.mock.random.io_exception_rate_on_open"
argument_list|,
literal|0.0d
argument_list|,
literal|0.0d
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|RANDOM_IO_EXCEPTION_RATE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Double
argument_list|>
name|RANDOM_IO_EXCEPTION_RATE_SETTING
init|=
name|Setting
operator|.
name|doubleSetting
argument_list|(
literal|"index.store.mock.random.io_exception_rate"
argument_list|,
literal|0.0d
argument_list|,
literal|0.0d
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|RANDOM_PREVENT_DOUBLE_WRITE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|RANDOM_PREVENT_DOUBLE_WRITE_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.store.mock.random.prevent_double_write"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|// true is default in MDW
DECL|field|RANDOM_NO_DELETE_OPEN_FILE_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|RANDOM_NO_DELETE_OPEN_FILE_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.store.mock.random.no_delete_open_file"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|// true is default in MDW
DECL|field|CRASH_INDEX_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|Boolean
argument_list|>
name|CRASH_INDEX_SETTING
init|=
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.store.mock.random.crash_index"
argument_list|,
literal|true
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
comment|// true is default in MDW
DECL|field|delegateService
specifier|private
specifier|final
name|FsDirectoryService
name|delegateService
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|randomIOExceptionRate
specifier|private
specifier|final
name|double
name|randomIOExceptionRate
decl_stmt|;
DECL|field|randomIOExceptionRateOnOpen
specifier|private
specifier|final
name|double
name|randomIOExceptionRateOnOpen
decl_stmt|;
DECL|field|throttle
specifier|private
specifier|final
name|MockDirectoryWrapper
operator|.
name|Throttling
name|throttle
decl_stmt|;
DECL|field|preventDoubleWrite
specifier|private
specifier|final
name|boolean
name|preventDoubleWrite
decl_stmt|;
DECL|field|noDeleteOpenFile
specifier|private
specifier|final
name|boolean
name|noDeleteOpenFile
decl_stmt|;
DECL|field|crashIndex
specifier|private
specifier|final
name|boolean
name|crashIndex
decl_stmt|;
annotation|@
name|Inject
DECL|method|MockFSDirectoryService
specifier|public
name|MockFSDirectoryService
parameter_list|(
name|IndexSettings
name|idxSettings
parameter_list|,
name|IndexStore
name|indexStore
parameter_list|,
specifier|final
name|ShardPath
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|idxSettings
argument_list|,
name|indexStore
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|Settings
name|indexSettings
init|=
name|idxSettings
operator|.
name|getSettings
argument_list|()
decl_stmt|;
specifier|final
name|long
name|seed
init|=
name|idxSettings
operator|.
name|getValue
argument_list|(
name|ESIntegTestCase
operator|.
name|INDEX_TEST_SEED_SETTING
argument_list|)
decl_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|randomIOExceptionRate
operator|=
name|RANDOM_IO_EXCEPTION_RATE_SETTING
operator|.
name|get
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|randomIOExceptionRateOnOpen
operator|=
name|RANDOM_IO_EXCEPTION_RATE_ON_OPEN_SETTING
operator|.
name|get
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|preventDoubleWrite
operator|=
name|RANDOM_PREVENT_DOUBLE_WRITE_SETTING
operator|.
name|get
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
name|noDeleteOpenFile
operator|=
name|RANDOM_NO_DELETE_OPEN_FILE_SETTING
operator|.
name|exists
argument_list|(
name|indexSettings
argument_list|)
condition|?
name|RANDOM_NO_DELETE_OPEN_FILE_SETTING
operator|.
name|get
argument_list|(
name|indexSettings
argument_list|)
else|:
name|random
operator|.
name|nextBoolean
argument_list|()
expr_stmt|;
name|random
operator|.
name|nextInt
argument_list|(
name|shardId
operator|.
name|getId
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// some randomness per shard
name|throttle
operator|=
name|MockDirectoryWrapper
operator|.
name|Throttling
operator|.
name|NEVER
expr_stmt|;
name|crashIndex
operator|=
name|CRASH_INDEX_SETTING
operator|.
name|get
argument_list|(
name|indexSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Using MockDirWrapper with seed [{}] throttle: [{}] crashIndex: [{}]"
argument_list|,
name|SeedUtils
operator|.
name|formatSeed
argument_list|(
name|seed
argument_list|)
argument_list|,
name|throttle
argument_list|,
name|crashIndex
argument_list|)
expr_stmt|;
block|}
name|delegateService
operator|=
name|randomDirectorService
argument_list|(
name|indexStore
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newDirectory
specifier|public
name|Directory
name|newDirectory
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|wrap
argument_list|(
name|delegateService
operator|.
name|newDirectory
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newFSDirectory
specifier|protected
specifier|synchronized
name|Directory
name|newFSDirectory
parameter_list|(
name|Path
name|location
parameter_list|,
name|LockFactory
name|lockFactory
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|checkIndex
specifier|public
specifier|static
name|void
name|checkIndex
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|Store
name|store
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
if|if
condition|(
name|store
operator|.
name|tryIncRef
argument_list|()
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"start check index"
argument_list|)
expr_stmt|;
try|try
block|{
name|Directory
name|dir
init|=
name|store
operator|.
name|directory
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Lucene
operator|.
name|indexExists
argument_list|(
name|dir
argument_list|)
condition|)
block|{
return|return;
block|}
try|try
init|(
name|CheckIndex
name|checkIndex
init|=
operator|new
name|CheckIndex
argument_list|(
name|dir
argument_list|)
init|)
block|{
name|BytesStreamOutput
name|os
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
name|os
argument_list|,
literal|false
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
name|checkIndex
operator|.
name|setInfoStream
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
name|CheckIndex
operator|.
name|Status
name|status
init|=
name|checkIndex
operator|.
name|checkIndex
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|status
operator|.
name|clean
condition|)
block|{
name|ESTestCase
operator|.
name|checkIndexFailed
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"check index [failure] index files={}\n{}"
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|dir
operator|.
name|listAll
argument_list|()
argument_list|)
argument_list|,
name|os
operator|.
name|bytes
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"index check failure"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"check index [success]\n{}"
argument_list|,
name|os
operator|.
name|bytes
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|e
parameter_list|)
block|{
name|ESTestCase
operator|.
name|checkIndexFailed
operator|=
literal|true
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"IndexWriter is still open on shard "
operator|+
name|shardId
argument_list|,
name|e
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
literal|"failed to check index"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"end check index"
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
annotation|@
name|Override
DECL|method|onPause
specifier|public
name|void
name|onPause
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
name|delegateService
operator|.
name|onPause
argument_list|(
name|nanos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rateLimiting
specifier|public
name|StoreRateLimiting
name|rateLimiting
parameter_list|()
block|{
return|return
name|delegateService
operator|.
name|rateLimiting
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|throttleTimeInNanos
specifier|public
name|long
name|throttleTimeInNanos
parameter_list|()
block|{
return|return
name|delegateService
operator|.
name|throttleTimeInNanos
argument_list|()
return|;
block|}
DECL|method|wrap
specifier|private
name|Directory
name|wrap
parameter_list|(
name|Directory
name|dir
parameter_list|)
block|{
specifier|final
name|ElasticsearchMockDirectoryWrapper
name|w
init|=
operator|new
name|ElasticsearchMockDirectoryWrapper
argument_list|(
name|random
argument_list|,
name|dir
argument_list|,
name|this
operator|.
name|crashIndex
argument_list|)
decl_stmt|;
name|w
operator|.
name|setRandomIOExceptionRate
argument_list|(
name|randomIOExceptionRate
argument_list|)
expr_stmt|;
name|w
operator|.
name|setRandomIOExceptionRateOnOpen
argument_list|(
name|randomIOExceptionRateOnOpen
argument_list|)
expr_stmt|;
name|w
operator|.
name|setThrottling
argument_list|(
name|throttle
argument_list|)
expr_stmt|;
name|w
operator|.
name|setCheckIndexOnClose
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// we do this on the index level
comment|//w.setPreventDoubleWrite(preventDoubleWrite);
comment|// TODO: make this test robust to virus scanner
name|w
operator|.
name|setAssertNoDeleteOpenFile
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|w
operator|.
name|setUseSlowOpenClosers
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|LuceneTestCase
operator|.
name|closeAfterSuite
argument_list|(
operator|new
name|CloseableDirectory
argument_list|(
name|w
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|w
return|;
block|}
DECL|method|randomDirectorService
specifier|private
name|FsDirectoryService
name|randomDirectorService
parameter_list|(
name|IndexStore
name|indexStore
parameter_list|,
name|ShardPath
name|path
parameter_list|)
block|{
specifier|final
name|IndexSettings
name|indexSettings
init|=
name|indexStore
operator|.
name|getIndexSettings
argument_list|()
decl_stmt|;
specifier|final
name|IndexMetaData
name|build
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|indexSettings
operator|.
name|getIndexMetaData
argument_list|()
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|indexSettings
operator|.
name|getSettings
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|IndexModule
operator|.
name|INDEX_STORE_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|RandomPicks
operator|.
name|randomFrom
argument_list|(
name|random
argument_list|,
name|IndexModule
operator|.
name|Type
operator|.
name|values
argument_list|()
argument_list|)
operator|.
name|getSettingsKey
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|IndexSettings
name|newIndexSettings
init|=
operator|new
name|IndexSettings
argument_list|(
name|build
argument_list|,
name|indexSettings
operator|.
name|getNodeSettings
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|FsDirectoryService
argument_list|(
name|newIndexSettings
argument_list|,
name|indexStore
argument_list|,
name|path
argument_list|)
return|;
block|}
DECL|class|ElasticsearchMockDirectoryWrapper
specifier|public
specifier|static
specifier|final
class|class
name|ElasticsearchMockDirectoryWrapper
extends|extends
name|MockDirectoryWrapper
block|{
DECL|field|crash
specifier|private
specifier|final
name|boolean
name|crash
decl_stmt|;
DECL|method|ElasticsearchMockDirectoryWrapper
specifier|public
name|ElasticsearchMockDirectoryWrapper
parameter_list|(
name|Random
name|random
parameter_list|,
name|Directory
name|delegate
parameter_list|,
name|boolean
name|crash
parameter_list|)
block|{
name|super
argument_list|(
name|random
argument_list|,
name|delegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|crash
operator|=
name|crash
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|crash
specifier|public
specifier|synchronized
name|void
name|crash
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|crash
condition|)
block|{
name|super
operator|.
name|crash
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|CloseableDirectory
specifier|final
class|class
name|CloseableDirectory
implements|implements
name|Closeable
block|{
DECL|field|dir
specifier|private
specifier|final
name|BaseDirectoryWrapper
name|dir
decl_stmt|;
DECL|field|failureMarker
specifier|private
specifier|final
name|TestRuleMarkFailure
name|failureMarker
decl_stmt|;
DECL|method|CloseableDirectory
specifier|public
name|CloseableDirectory
parameter_list|(
name|BaseDirectoryWrapper
name|dir
parameter_list|)
block|{
name|this
operator|.
name|dir
operator|=
name|dir
expr_stmt|;
name|this
operator|.
name|failureMarker
operator|=
name|ESTestCase
operator|.
name|getSuiteFailureMarker
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// We only attempt to check open/closed state if there were no other test
comment|// failures.
try|try
block|{
if|if
condition|(
name|failureMarker
operator|.
name|wasSuccessful
argument_list|()
operator|&&
name|dir
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Directory not closed: "
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// TODO: perform real close of the delegate: LUCENE-4058
comment|// dir.close();
block|}
block|}
block|}
block|}
end_class

end_unit

