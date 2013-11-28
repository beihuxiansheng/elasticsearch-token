begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.search.child
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|search
operator|.
name|child
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|ObjectObjectOpenHashMap
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
name|queries
operator|.
name|TermFilter
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
name|DocIdSetIterator
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
name|Query
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
name|FixedBitSet
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
name|admin
operator|.
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
operator|.
name|CacheRecycler
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
name|compress
operator|.
name|CompressedString
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
name|search
operator|.
name|XConstantScoreQuery
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
name|ImmutableSettings
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
name|env
operator|.
name|Environment
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
name|analysis
operator|.
name|AnalysisService
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
name|cache
operator|.
name|filter
operator|.
name|weighted
operator|.
name|WeightedFilterCache
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
name|cache
operator|.
name|id
operator|.
name|IdCache
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
name|cache
operator|.
name|id
operator|.
name|SimpleIdCacheTests
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
name|cache
operator|.
name|id
operator|.
name|simple
operator|.
name|SimpleIdCache
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
name|mapper
operator|.
name|MapperService
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
name|Uid
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
name|internal
operator|.
name|ParentFieldMapper
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
name|internal
operator|.
name|TypeFieldMapper
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
name|internal
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
name|service
operator|.
name|IndexService
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
name|cache
operator|.
name|filter
operator|.
name|IndicesFilterCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|settings
operator|.
name|NodeSettingsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|ContextIndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
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
name|ElasticsearchLuceneTestCase
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
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|StringDescription
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|NavigableSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
DECL|class|ChildrenConstantScoreQueryTests
specifier|public
class|class
name|ChildrenConstantScoreQueryTests
extends|extends
name|ElasticsearchLuceneTestCase
block|{
annotation|@
name|BeforeClass
DECL|method|before
specifier|public
specifier|static
name|void
name|before
parameter_list|()
throws|throws
name|IOException
block|{
name|forceDefaultCodec
argument_list|()
expr_stmt|;
name|SearchContext
operator|.
name|setCurrent
argument_list|(
name|createSearchContext
argument_list|(
literal|"test"
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|after
specifier|public
specifier|static
name|void
name|after
parameter_list|()
throws|throws
name|IOException
block|{
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|indexWriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|parent
init|=
literal|1
init|;
name|parent
operator|<=
literal|5
condition|;
name|parent
operator|++
control|)
block|{
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
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
literal|"parent"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|parent
argument_list|)
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
literal|"parent"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|child
init|=
literal|1
init|;
name|child
operator|<=
literal|3
condition|;
name|child
operator|++
control|)
block|{
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
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
literal|"child"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|parent
operator|*
literal|3
operator|+
name|child
argument_list|)
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
literal|"child"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
literal|"parent"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|parent
argument_list|)
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
operator|+
name|child
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
block|}
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
operator|.
name|w
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
name|indexReader
argument_list|)
decl_stmt|;
name|TermQuery
name|childQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
literal|"value"
operator|+
operator|(
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
operator|)
argument_list|)
argument_list|)
decl_stmt|;
name|TermFilter
name|parentFilter
init|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
literal|"parent"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|shortCircuitParentDocSet
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|ChildrenConstantScoreQuery
name|query
init|=
operator|new
name|ChildrenConstantScoreQuery
argument_list|(
name|childQuery
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|,
name|parentFilter
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|BitSetCollector
name|collector
init|=
operator|new
name|BitSetCollector
argument_list|(
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|FixedBitSet
name|actualResult
init|=
name|collector
operator|.
name|getResult
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|actualResult
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|indexWriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|)
decl_stmt|;
name|int
name|numUniqueChildValues
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|10000
else|:
literal|1000
argument_list|)
decl_stmt|;
name|String
index|[]
name|childValues
init|=
operator|new
name|String
index|[
name|numUniqueChildValues
index|]
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
name|numUniqueChildValues
condition|;
name|i
operator|++
control|)
block|{
name|childValues
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|childDocId
init|=
literal|0
decl_stmt|;
name|int
name|numParentDocs
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|20000
else|:
literal|1000
argument_list|)
decl_stmt|;
name|ObjectObjectOpenHashMap
argument_list|<
name|String
argument_list|,
name|NavigableSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|childValueToParentIds
init|=
operator|new
name|ObjectObjectOpenHashMap
argument_list|<
name|String
argument_list|,
name|NavigableSet
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|parentDocId
init|=
literal|0
init|;
name|parentDocId
operator|<
name|numParentDocs
condition|;
name|parentDocId
operator|++
control|)
block|{
name|boolean
name|markParentAsDeleted
init|=
name|rarely
argument_list|()
decl_stmt|;
name|String
name|parent
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|parentDocId
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
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
literal|"parent"
argument_list|,
name|parent
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
literal|"parent"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|markParentAsDeleted
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"delete"
argument_list|,
literal|"me"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
name|int
name|numChildDocs
decl_stmt|;
if|if
condition|(
name|rarely
argument_list|()
condition|)
block|{
name|numChildDocs
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|100
else|:
literal|25
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|numChildDocs
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|TEST_NIGHTLY
condition|?
literal|40
else|:
literal|10
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numChildDocs
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|markChildAsDeleted
init|=
name|rarely
argument_list|()
decl_stmt|;
name|String
name|childValue
init|=
name|childValues
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|childValues
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
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
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
literal|"child"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|childDocId
argument_list|)
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
literal|"child"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|ParentFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUid
argument_list|(
literal|"parent"
argument_list|,
name|parent
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"field1"
argument_list|,
name|childValue
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|markChildAsDeleted
condition|)
block|{
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"delete"
argument_list|,
literal|"me"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|markChildAsDeleted
condition|)
block|{
name|NavigableSet
argument_list|<
name|String
argument_list|>
name|parentIds
decl_stmt|;
if|if
condition|(
name|childValueToParentIds
operator|.
name|containsKey
argument_list|(
name|childValue
argument_list|)
condition|)
block|{
name|parentIds
operator|=
name|childValueToParentIds
operator|.
name|lget
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|childValueToParentIds
operator|.
name|put
argument_list|(
name|childValue
argument_list|,
name|parentIds
operator|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|markParentAsDeleted
condition|)
block|{
name|parentIds
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// Delete docs that are marked to be deleted.
name|indexWriter
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
literal|"delete"
argument_list|,
literal|"me"
argument_list|)
argument_list|)
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexReader
name|indexReader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|Engine
operator|.
name|Searcher
name|engineSearcher
init|=
operator|new
name|Engine
operator|.
name|SimpleSearcher
argument_list|(
name|ChildrenConstantScoreQueryTests
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|,
name|searcher
argument_list|)
decl_stmt|;
operator|(
operator|(
name|TestSearchContext
operator|)
name|SearchContext
operator|.
name|current
argument_list|()
operator|)
operator|.
name|setSearcher
argument_list|(
operator|new
name|ContextIndexSearcher
argument_list|(
name|SearchContext
operator|.
name|current
argument_list|()
argument_list|,
name|engineSearcher
argument_list|)
argument_list|)
expr_stmt|;
name|TermFilter
name|parentFilter
init|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|TypeFieldMapper
operator|.
name|NAME
argument_list|,
literal|"parent"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|max
init|=
name|numUniqueChildValues
operator|/
literal|4
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
name|max
condition|;
name|i
operator|++
control|)
block|{
name|String
name|childValue
init|=
name|childValues
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numUniqueChildValues
argument_list|)
index|]
decl_stmt|;
name|TermQuery
name|childQuery
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field1"
argument_list|,
name|childValue
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|shortCircuitParentDocSet
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numParentDocs
argument_list|)
decl_stmt|;
name|Query
name|query
decl_stmt|;
name|boolean
name|applyAcceptedDocs
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|applyAcceptedDocs
condition|)
block|{
comment|// Usage in HasChildQueryParser
name|query
operator|=
operator|new
name|ChildrenConstantScoreQuery
argument_list|(
name|childQuery
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|,
name|parentFilter
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|applyAcceptedDocs
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Usage in HasChildFilterParser
name|query
operator|=
operator|new
name|XConstantScoreQuery
argument_list|(
operator|new
name|CustomQueryWrappingFilter
argument_list|(
operator|new
name|ChildrenConstantScoreQuery
argument_list|(
name|childQuery
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|,
name|parentFilter
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|applyAcceptedDocs
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|BitSetCollector
name|collector
init|=
operator|new
name|BitSetCollector
argument_list|(
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
name|FixedBitSet
name|actualResult
init|=
name|collector
operator|.
name|getResult
argument_list|()
decl_stmt|;
name|FixedBitSet
name|expectedResult
init|=
operator|new
name|FixedBitSet
argument_list|(
name|indexReader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|childValueToParentIds
operator|.
name|containsKey
argument_list|(
name|childValue
argument_list|)
condition|)
block|{
name|AtomicReader
name|slowAtomicReader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|Terms
name|terms
init|=
name|slowAtomicReader
operator|.
name|terms
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|NavigableSet
argument_list|<
name|String
argument_list|>
name|parentIds
init|=
name|childValueToParentIds
operator|.
name|lget
argument_list|()
decl_stmt|;
name|TermsEnum
name|termsEnum
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|parentIds
control|)
block|{
name|TermsEnum
operator|.
name|SeekStatus
name|seekStatus
init|=
name|termsEnum
operator|.
name|seekCeil
argument_list|(
name|Uid
operator|.
name|createUidAsBytes
argument_list|(
literal|"parent"
argument_list|,
name|id
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|seekStatus
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|FOUND
condition|)
block|{
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
name|slowAtomicReader
operator|.
name|getLiveDocs
argument_list|()
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
name|expectedResult
operator|.
name|set
argument_list|(
name|docsEnum
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|seekStatus
operator|==
name|TermsEnum
operator|.
name|SeekStatus
operator|.
name|END
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
name|assertBitSet
argument_list|(
name|actualResult
argument_list|,
name|expectedResult
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|assertBitSet
specifier|static
name|void
name|assertBitSet
parameter_list|(
name|FixedBitSet
name|actual
parameter_list|,
name|FixedBitSet
name|expected
parameter_list|,
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|actual
operator|.
name|equals
argument_list|(
name|expected
argument_list|)
condition|)
block|{
name|Description
name|description
init|=
operator|new
name|StringDescription
argument_list|()
decl_stmt|;
name|description
operator|.
name|appendText
argument_list|(
name|reason
argument_list|(
name|actual
argument_list|,
name|expected
argument_list|,
name|searcher
argument_list|)
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"\nExpected: "
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendValue
argument_list|(
name|expected
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"\n     got: "
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendValue
argument_list|(
name|actual
argument_list|)
expr_stmt|;
name|description
operator|.
name|appendText
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|java
operator|.
name|lang
operator|.
name|AssertionError
argument_list|(
name|description
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|reason
specifier|static
name|String
name|reason
parameter_list|(
name|FixedBitSet
name|actual
parameter_list|,
name|FixedBitSet
name|expected
parameter_list|,
name|IndexSearcher
name|indexSearcher
parameter_list|)
throws|throws
name|IOException
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"expected cardinality:"
argument_list|)
operator|.
name|append
argument_list|(
name|expected
operator|.
name|cardinality
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|DocIdSetIterator
name|iterator
init|=
name|expected
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"Expected doc["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with id value "
argument_list|)
operator|.
name|append
argument_list|(
name|indexSearcher
operator|.
name|doc
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|"actual cardinality: "
argument_list|)
operator|.
name|append
argument_list|(
name|actual
operator|.
name|cardinality
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|actual
operator|.
name|iterator
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|doc
init|=
name|iterator
operator|.
name|nextDoc
argument_list|()
init|;
name|doc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|doc
operator|=
name|iterator
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|"Actual doc["
argument_list|)
operator|.
name|append
argument_list|(
name|doc
argument_list|)
operator|.
name|append
argument_list|(
literal|"] with id value "
argument_list|)
operator|.
name|append
argument_list|(
name|indexSearcher
operator|.
name|doc
argument_list|(
name|doc
argument_list|)
operator|.
name|get
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|createSearchContext
specifier|static
name|SearchContext
name|createSearchContext
parameter_list|(
name|String
name|indexName
parameter_list|,
name|String
name|parentType
parameter_list|,
name|String
name|childType
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Index
name|index
init|=
operator|new
name|Index
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
specifier|final
name|IdCache
name|idCache
init|=
operator|new
name|SimpleIdCache
argument_list|(
name|index
argument_list|,
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
specifier|final
name|CacheRecycler
name|cacheRecycler
init|=
operator|new
name|CacheRecycler
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|EMPTY
decl_stmt|;
name|MapperService
name|mapperService
init|=
operator|new
name|MapperService
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
operator|new
name|Environment
argument_list|()
argument_list|,
operator|new
name|AnalysisService
argument_list|(
name|index
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|mapperService
operator|.
name|merge
argument_list|(
name|childType
argument_list|,
operator|new
name|CompressedString
argument_list|(
name|PutMappingRequest
operator|.
name|buildFromSimplifiedDef
argument_list|(
name|childType
argument_list|,
literal|"_parent"
argument_list|,
literal|"type="
operator|+
name|parentType
argument_list|)
operator|.
name|string
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|IndexService
name|indexService
init|=
operator|new
name|SimpleIdCacheTests
operator|.
name|StubIndexService
argument_list|(
name|mapperService
argument_list|)
decl_stmt|;
name|idCache
operator|.
name|setIndexService
argument_list|(
name|indexService
argument_list|)
expr_stmt|;
name|ThreadPool
name|threadPool
init|=
operator|new
name|ThreadPool
argument_list|()
decl_stmt|;
name|NodeSettingsService
name|nodeSettingsService
init|=
operator|new
name|NodeSettingsService
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|IndicesFilterCache
name|indicesFilterCache
init|=
operator|new
name|IndicesFilterCache
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|cacheRecycler
argument_list|,
name|nodeSettingsService
argument_list|)
decl_stmt|;
name|WeightedFilterCache
name|filterCache
init|=
operator|new
name|WeightedFilterCache
argument_list|(
name|index
argument_list|,
name|settings
argument_list|,
name|indicesFilterCache
argument_list|)
decl_stmt|;
return|return
operator|new
name|TestSearchContext
argument_list|(
name|cacheRecycler
argument_list|,
name|idCache
argument_list|,
name|indexService
argument_list|,
name|filterCache
argument_list|)
return|;
block|}
block|}
end_class

end_unit

