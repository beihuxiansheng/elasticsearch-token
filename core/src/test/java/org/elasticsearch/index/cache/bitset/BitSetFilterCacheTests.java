begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.bitset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|bitset
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|Document
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
name|LeafReaderContext
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
name|LogByteSizeMergePolicy
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
name|search
operator|.
name|QueryWrapperFilter
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
name|TermQuery
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
name|join
operator|.
name|BitSetProducer
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
name|RAMDirectory
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
name|Accountable
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
name|BitSet
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
name|Test
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|BitSetFilterCacheTests
specifier|public
class|class
name|BitSetFilterCacheTests
extends|extends
name|ESTestCase
block|{
DECL|method|matchCount
specifier|private
specifier|static
name|int
name|matchCount
parameter_list|(
name|BitSetProducer
name|producer
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|ctx
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
specifier|final
name|BitSet
name|bitSet
init|=
name|producer
operator|.
name|getBitSet
argument_list|(
name|ctx
argument_list|)
decl_stmt|;
if|if
condition|(
name|bitSet
operator|!=
literal|null
condition|)
block|{
name|count
operator|+=
name|bitSet
operator|.
name|cardinality
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
annotation|@
name|Test
DECL|method|testInvalidateEntries
specifier|public
name|void
name|testInvalidateEntries
parameter_list|()
throws|throws
name|Exception
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogByteSizeMergePolicy
argument_list|()
argument_list|)
argument_list|)
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
name|StringField
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|document
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|BitsetFilterCache
name|cache
init|=
operator|new
name|BitsetFilterCache
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|BitSetProducer
name|filter
init|=
name|cache
operator|.
name|getBitSetProducer
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|matchCount
argument_list|(
name|filter
argument_list|,
name|reader
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// now cached
name|assertThat
argument_list|(
name|matchCount
argument_list|(
name|filter
argument_list|,
name|reader
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// There are 3 segments
name|assertThat
argument_list|(
name|cache
operator|.
name|getLoadedFilters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3l
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|matchCount
argument_list|(
name|filter
argument_list|,
name|reader
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// now cached
name|assertThat
argument_list|(
name|matchCount
argument_list|(
name|filter
argument_list|,
name|reader
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// Only one segment now, so the size must be 1
name|assertThat
argument_list|(
name|cache
operator|.
name|getLoadedFilters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// There is no reference from readers and writer to any segment in the test index, so the size in the fbs cache must be 0
name|assertThat
argument_list|(
name|cache
operator|.
name|getLoadedFilters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testListener
specifier|public
name|void
name|testListener
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
operator|new
name|RAMDirectory
argument_list|()
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|StandardAnalyzer
argument_list|()
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
operator|new
name|LogByteSizeMergePolicy
argument_list|()
argument_list|)
argument_list|)
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
name|StringField
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
specifier|final
name|DirectoryReader
name|writerReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|IndexReader
name|reader
init|=
name|randomBoolean
argument_list|()
condition|?
name|writerReader
else|:
name|ElasticsearchDirectoryReader
operator|.
name|wrap
argument_list|(
name|writerReader
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|stats
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|onCacheCalls
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|onRemoveCalls
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|BitsetFilterCache
name|cache
init|=
operator|new
name|BitsetFilterCache
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|cache
operator|.
name|setListener
argument_list|(
operator|new
name|BitsetFilterCache
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{
name|onCacheCalls
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|stats
operator|.
name|addAndGet
argument_list|(
name|accountable
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|writerReader
operator|!=
name|reader
condition|)
block|{
name|assertNotNull
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{
name|onRemoveCalls
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|stats
operator|.
name|addAndGet
argument_list|(
operator|-
name|accountable
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|writerReader
operator|!=
name|reader
condition|)
block|{
name|assertNotNull
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|shardId
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|shardId
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertNull
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|BitSetProducer
name|filter
init|=
name|cache
operator|.
name|getBitSetProducer
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|matchCount
argument_list|(
name|filter
argument_list|,
name|reader
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|stats
operator|.
name|get
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|onCacheCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|onRemoveCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|reader
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|onRemoveCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|stats
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testSetListenerTwice
specifier|public
name|void
name|testSetListenerTwice
parameter_list|()
block|{
specifier|final
name|BitsetFilterCache
name|cache
init|=
operator|new
name|BitsetFilterCache
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|cache
operator|.
name|setListener
argument_list|(
operator|new
name|BitsetFilterCache
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{              }
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|setListener
argument_list|(
operator|new
name|BitsetFilterCache
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onCache
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{                  }
annotation|@
name|Override
specifier|public
name|void
name|onRemoval
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Accountable
name|accountable
parameter_list|)
block|{                  }
block|}
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"can't set it twice"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// all is well
block|}
block|}
block|}
end_class

end_unit

