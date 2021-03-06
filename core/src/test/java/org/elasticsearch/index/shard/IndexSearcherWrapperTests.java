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
name|FieldFilterLeafReader
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
name|FilterDirectoryReader
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
name|LeafReader
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
name|TopDocs
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
name|EngineException
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
name|util
operator|.
name|Collections
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
name|ConcurrentHashMap
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

begin_class
DECL|class|IndexSearcherWrapperTests
specifier|public
class|class
name|IndexSearcherWrapperTests
extends|extends
name|ESTestCase
block|{
DECL|method|testReaderCloseListenerIsCalled
specifier|public
name|void
name|testReaderCloseListenerIsCalled
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
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
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
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|ElasticsearchDirectoryReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|"_na_"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|open
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|closeCalls
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|IndexSearcherWrapper
name|wrapper
init|=
operator|new
name|IndexSearcherWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DirectoryReader
name|wrap
parameter_list|(
name|DirectoryReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldMaskingReader
argument_list|(
literal|"field"
argument_list|,
name|reader
argument_list|,
name|closeCalls
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexSearcher
name|wrap
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|EngineException
block|{
return|return
name|searcher
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|int
name|sourceRefCount
init|=
name|open
operator|.
name|getRefCount
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|outerCount
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
try|try
init|(
name|Engine
operator|.
name|Searcher
name|engineSearcher
init|=
operator|new
name|Engine
operator|.
name|Searcher
argument_list|(
literal|"foo"
argument_list|,
name|searcher
argument_list|)
init|)
block|{
specifier|final
name|Engine
operator|.
name|Searcher
name|wrap
init|=
name|wrapper
operator|.
name|wrap
argument_list|(
name|engineSearcher
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|wrap
operator|.
name|reader
argument_list|()
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|ElasticsearchDirectoryReader
operator|.
name|addReaderCloseListener
argument_list|(
name|wrap
operator|.
name|getDirectoryReader
argument_list|()
argument_list|,
name|key
lambda|->
block|{
if|if
condition|(
name|key
operator|==
name|open
operator|.
name|getReaderCacheHelper
argument_list|()
operator|.
name|getKey
argument_list|()
condition|)
block|{
name|count
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|outerCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|wrap
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|wrap
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
literal|"wrapped reader is closed"
argument_list|,
name|wrap
operator|.
name|reader
argument_list|()
operator|.
name|tryIncRef
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|sourceRefCount
argument_list|,
name|open
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|open
argument_list|,
name|writer
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|outerCount
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|count
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|open
operator|.
name|getRefCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsCacheable
specifier|public
name|void
name|testIsCacheable
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
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
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
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|ElasticsearchDirectoryReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|"_na_"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|open
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|iwc
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AtomicInteger
name|closeCalls
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|IndexSearcherWrapper
name|wrapper
init|=
operator|new
name|IndexSearcherWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DirectoryReader
name|wrap
parameter_list|(
name|DirectoryReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldMaskingReader
argument_list|(
literal|"field"
argument_list|,
name|reader
argument_list|,
name|closeCalls
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexSearcher
name|wrap
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|EngineException
block|{
return|return
name|searcher
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|ConcurrentHashMap
argument_list|<
name|Object
argument_list|,
name|TopDocs
argument_list|>
name|cache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
try|try
init|(
name|Engine
operator|.
name|Searcher
name|engineSearcher
init|=
operator|new
name|Engine
operator|.
name|Searcher
argument_list|(
literal|"foo"
argument_list|,
name|searcher
argument_list|)
init|)
block|{
try|try
init|(
name|Engine
operator|.
name|Searcher
name|wrap
init|=
name|wrapper
operator|.
name|wrap
argument_list|(
name|engineSearcher
argument_list|)
init|)
block|{
name|ElasticsearchDirectoryReader
operator|.
name|addReaderCloseListener
argument_list|(
name|wrap
operator|.
name|getDirectoryReader
argument_list|()
argument_list|,
name|key
lambda|->
block|{
name|cache
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|TopDocs
name|search
init|=
name|wrap
operator|.
name|searcher
argument_list|()
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|wrap
operator|.
name|reader
argument_list|()
operator|.
name|getReaderCacheHelper
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|,
name|search
argument_list|)
expr_stmt|;
block|}
block|}
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|open
argument_list|,
name|writer
argument_list|,
name|dir
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|cache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|closeCalls
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoWrap
specifier|public
name|void
name|testNoWrap
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
name|IndexWriterConfig
name|iwc
init|=
name|newIndexWriterConfig
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|Field
operator|.
name|Store
operator|.
name|YES
else|:
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
name|doc
argument_list|)
expr_stmt|;
name|DirectoryReader
name|open
init|=
name|ElasticsearchDirectoryReader
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|"_na_"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|open
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"doc"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
name|iwc
operator|.
name|getSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|IndexSearcherWrapper
name|wrapper
init|=
operator|new
name|IndexSearcherWrapper
argument_list|()
decl_stmt|;
try|try
init|(
name|Engine
operator|.
name|Searcher
name|engineSearcher
init|=
operator|new
name|Engine
operator|.
name|Searcher
argument_list|(
literal|"foo"
argument_list|,
name|searcher
argument_list|)
init|)
block|{
specifier|final
name|Engine
operator|.
name|Searcher
name|wrap
init|=
name|wrapper
operator|.
name|wrap
argument_list|(
name|engineSearcher
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|wrap
argument_list|,
name|engineSearcher
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|close
argument_list|(
name|open
argument_list|,
name|writer
argument_list|,
name|dir
argument_list|)
expr_stmt|;
block|}
DECL|class|FieldMaskingReader
specifier|private
specifier|static
class|class
name|FieldMaskingReader
extends|extends
name|FilterDirectoryReader
block|{
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|closeCalls
specifier|private
specifier|final
name|AtomicInteger
name|closeCalls
decl_stmt|;
DECL|method|FieldMaskingReader
name|FieldMaskingReader
parameter_list|(
name|String
name|field
parameter_list|,
name|DirectoryReader
name|in
parameter_list|,
name|AtomicInteger
name|closeCalls
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|,
operator|new
name|SubReaderWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|LeafReader
name|wrap
parameter_list|(
name|LeafReader
name|reader
parameter_list|)
block|{
return|return
operator|new
name|FieldFilterLeafReader
argument_list|(
name|reader
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|field
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|closeCalls
operator|=
name|closeCalls
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWrapDirectoryReader
specifier|protected
name|DirectoryReader
name|doWrapDirectoryReader
parameter_list|(
name|DirectoryReader
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldMaskingReader
argument_list|(
name|field
argument_list|,
name|in
argument_list|,
name|closeCalls
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getReaderCacheHelper
specifier|public
name|CacheHelper
name|getReaderCacheHelper
parameter_list|()
block|{
return|return
name|in
operator|.
name|getReaderCacheHelper
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|doClose
argument_list|()
expr_stmt|;
name|closeCalls
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

