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
name|StringField
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
name|NoMergePolicy
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
name|SegmentCommitInfo
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
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
name|RecoveryState
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
name|Files
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
name|attribute
operator|.
name|BasicFileAttributes
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|AccessControlException
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
name|function
operator|.
name|Predicate
import|;
end_import

begin_class
DECL|class|StoreRecoveryTests
specifier|public
class|class
name|StoreRecoveryTests
extends|extends
name|ESTestCase
block|{
DECL|method|testAddIndices
specifier|public
name|void
name|testAddIndices
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
index|[]
name|dirs
init|=
operator|new
name|Directory
index|[
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
index|]
decl_stmt|;
specifier|final
name|int
name|numDocs
init|=
name|randomIntBetween
argument_list|(
literal|50
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|int
name|id
init|=
literal|0
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
name|dirs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dirs
index|[
name|i
index|]
operator|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dirs
index|[
name|i
index|]
argument_list|,
name|newIndexWriterConfig
argument_list|()
operator|.
name|setMergePolicy
argument_list|(
name|NoMergePolicy
operator|.
name|INSTANCE
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numDocs
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|id
operator|++
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|StoreRecovery
name|storeRecovery
init|=
operator|new
name|StoreRecovery
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|RecoveryState
operator|.
name|Index
name|indexStats
init|=
operator|new
name|RecoveryState
operator|.
name|Index
argument_list|()
decl_stmt|;
name|Directory
name|target
init|=
name|newFSDirectory
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
decl_stmt|;
name|storeRecovery
operator|.
name|addIndices
argument_list|(
name|indexStats
argument_list|,
name|target
argument_list|,
name|dirs
argument_list|)
expr_stmt|;
name|int
name|numFiles
init|=
literal|0
decl_stmt|;
name|Predicate
argument_list|<
name|String
argument_list|>
name|filesFilter
init|=
parameter_list|(
name|f
parameter_list|)
lambda|->
name|f
operator|.
name|startsWith
argument_list|(
literal|"segments"
argument_list|)
operator|==
literal|false
operator|&&
name|f
operator|.
name|equals
argument_list|(
literal|"write.lock"
argument_list|)
operator|==
literal|false
operator|&&
name|f
operator|.
name|startsWith
argument_list|(
literal|"extra"
argument_list|)
operator|==
literal|false
decl_stmt|;
for|for
control|(
name|Directory
name|d
range|:
name|dirs
control|)
block|{
name|numFiles
operator|+=
name|Arrays
operator|.
name|asList
argument_list|(
name|d
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|filesFilter
argument_list|)
operator|.
name|count
argument_list|()
expr_stmt|;
block|}
specifier|final
name|long
name|targetNumFiles
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|target
operator|.
name|listAll
argument_list|()
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|filesFilter
argument_list|)
operator|.
name|count
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|numFiles
argument_list|,
name|targetNumFiles
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indexStats
operator|.
name|totalFileCount
argument_list|()
argument_list|,
name|targetNumFiles
argument_list|)
expr_stmt|;
if|if
condition|(
name|hardLinksSupported
argument_list|(
name|createTempDir
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"upgrade to HardlinkCopyDirectoryWrapper in Lucene 6.1"
argument_list|,
name|Version
operator|.
name|LATEST
argument_list|,
name|Version
operator|.
name|LUCENE_6_0_0
argument_list|)
expr_stmt|;
comment|// assertEquals(indexStats.reusedFileCount(), targetNumFiles); -- uncomment this once upgraded to Lucene 6.1
name|assertEquals
argument_list|(
name|indexStats
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|indexStats
operator|.
name|reusedFileCount
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|DirectoryReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|SegmentInfos
name|segmentCommitInfos
init|=
name|SegmentInfos
operator|.
name|readLatestCommit
argument_list|(
name|target
argument_list|)
decl_stmt|;
for|for
control|(
name|SegmentCommitInfo
name|info
range|:
name|segmentCommitInfos
control|)
block|{
comment|// check that we didn't merge
name|assertEquals
argument_list|(
literal|"all sources must be flush"
argument_list|,
name|info
operator|.
name|info
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|get
argument_list|(
literal|"source"
argument_list|)
argument_list|,
literal|"flush"
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|reader
operator|.
name|numDeletedDocs
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|target
operator|.
name|close
argument_list|()
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dirs
argument_list|)
expr_stmt|;
block|}
DECL|method|testStatsDirWrapper
specifier|public
name|void
name|testStatsDirWrapper
parameter_list|()
throws|throws
name|IOException
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|Directory
name|target
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RecoveryState
operator|.
name|Index
name|indexStats
init|=
operator|new
name|RecoveryState
operator|.
name|Index
argument_list|()
decl_stmt|;
name|StoreRecovery
operator|.
name|StatsDirectoryWrapper
name|wrapper
init|=
operator|new
name|StoreRecovery
operator|.
name|StatsDirectoryWrapper
argument_list|(
name|target
argument_list|,
name|indexStats
argument_list|)
decl_stmt|;
try|try
init|(
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo.bar"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
init|)
block|{
name|CodecUtil
operator|.
name|writeHeader
argument_list|(
name|output
argument_list|,
literal|"foo"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|int
name|numBytes
init|=
name|randomIntBetween
argument_list|(
literal|100
argument_list|,
literal|20000
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
name|numBytes
condition|;
name|i
operator|++
control|)
block|{
name|output
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
name|CodecUtil
operator|.
name|writeFooter
argument_list|(
name|output
argument_list|)
expr_stmt|;
block|}
name|wrapper
operator|.
name|copyFrom
argument_list|(
name|dir
argument_list|,
literal|"foo.bar"
argument_list|,
literal|"bar.foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|indexStats
operator|.
name|getFileDetails
argument_list|(
literal|"bar.foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|indexStats
operator|.
name|getFileDetails
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|.
name|fileLength
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|,
name|indexStats
operator|.
name|getFileDetails
argument_list|(
literal|"bar.foo"
argument_list|)
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|dir
operator|.
name|fileLength
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|,
name|indexStats
operator|.
name|getFileDetails
argument_list|(
literal|"bar.foo"
argument_list|)
operator|.
name|recovered
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|indexStats
operator|.
name|getFileDetails
argument_list|(
literal|"bar.foo"
argument_list|)
operator|.
name|reused
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|dir
argument_list|,
name|target
argument_list|)
expr_stmt|;
block|}
DECL|method|hardLinksSupported
specifier|public
name|boolean
name|hardLinksSupported
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Files
operator|.
name|createFile
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
name|Files
operator|.
name|createLink
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|path
operator|.
name|resolve
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
name|BasicFileAttributes
name|destAttr
init|=
name|Files
operator|.
name|readAttributes
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|BasicFileAttributes
operator|.
name|class
argument_list|)
decl_stmt|;
name|BasicFileAttributes
name|sourceAttr
init|=
name|Files
operator|.
name|readAttributes
argument_list|(
name|path
operator|.
name|resolve
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|,
name|BasicFileAttributes
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// we won't get here - no permission ;)
return|return
name|destAttr
operator|.
name|fileKey
argument_list|()
operator|!=
literal|null
operator|&&
name|destAttr
operator|.
name|fileKey
argument_list|()
operator|.
name|equals
argument_list|(
name|sourceAttr
operator|.
name|fileKey
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AccessControlException
name|ex
parameter_list|)
block|{
return|return
literal|true
return|;
comment|// if we run into that situation we know it's supported.
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|ex
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

