begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|FloatArrayList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|IntOpenHashSet
import|;
end_import

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
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|generators
operator|.
name|RandomInts
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
name|analysis
operator|.
name|MockAnalyzer
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
name|elasticsearch
operator|.
name|common
operator|.
name|lease
operator|.
name|Releasables
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
name|NotFilter
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
name|XFilteredQuery
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
name|cache
operator|.
name|fixedbitset
operator|.
name|FixedBitSetFilter
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
name|fielddata
operator|.
name|plain
operator|.
name|ParentChildIndexFieldData
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
name|search
operator|.
name|nested
operator|.
name|NonNestedDocsFilter
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
name|TestSearchContext
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_class
DECL|class|ChildrenQueryTests
specifier|public
class|class
name|ChildrenQueryTests
extends|extends
name|AbstractChildTests
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
name|current
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
name|SearchContext
operator|.
name|removeCurrent
argument_list|()
expr_stmt|;
name|Releasables
operator|.
name|close
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBasicQuerySanities
specifier|public
name|void
name|testBasicQuerySanities
parameter_list|()
block|{
name|Query
name|childQuery
init|=
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
decl_stmt|;
name|ScoreType
name|scoreType
init|=
name|ScoreType
operator|.
name|values
argument_list|()
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|ScoreType
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"child"
argument_list|)
operator|.
name|parentFieldMapper
argument_list|()
decl_stmt|;
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
init|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
argument_list|)
decl_stmt|;
name|FixedBitSetFilter
name|parentFilter
init|=
name|wrap
argument_list|(
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
argument_list|)
decl_stmt|;
name|int
name|minChildren
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|int
name|maxChildren
init|=
name|scaledRandomIntBetween
argument_list|(
name|minChildren
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|ChildrenQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|,
name|parentFilter
argument_list|,
name|childQuery
argument_list|,
name|scoreType
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|,
literal|12
argument_list|,
name|wrap
argument_list|(
name|NonNestedDocsFilter
operator|.
name|INSTANCE
argument_list|)
argument_list|)
decl_stmt|;
name|QueryUtils
operator|.
name|check
argument_list|(
name|query
argument_list|)
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
specifier|final
name|Random
name|r
init|=
name|random
argument_list|()
decl_stmt|;
specifier|final
name|IndexWriterConfig
name|iwc
init|=
name|LuceneTestCase
operator|.
name|newIndexWriterConfig
argument_list|(
name|r
argument_list|,
name|LuceneTestCase
operator|.
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|r
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|IndexWriterConfig
operator|.
name|DISABLE_AUTO_FLUSH
argument_list|)
operator|.
name|setRAMBufferSizeMB
argument_list|(
name|scaledRandomIntBetween
argument_list|(
literal|16
argument_list|,
literal|64
argument_list|)
argument_list|)
decl_stmt|;
comment|// we might index a lot - don't go crazy here
name|RandomIndexWriter
name|indexWriter
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|r
argument_list|,
name|directory
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|int
name|numUniqueChildValues
init|=
name|scaledRandomIntBetween
argument_list|(
literal|100
argument_list|,
literal|2000
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
name|IntOpenHashSet
name|filteredOrDeletedDocs
init|=
operator|new
name|IntOpenHashSet
argument_list|()
decl_stmt|;
name|int
name|childDocId
init|=
literal|0
decl_stmt|;
name|int
name|numParentDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|1
argument_list|,
name|numUniqueChildValues
argument_list|)
decl_stmt|;
name|ObjectObjectOpenHashMap
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|FloatArrayList
argument_list|>
argument_list|>
name|childValueToParentIds
init|=
operator|new
name|ObjectObjectOpenHashMap
argument_list|<>
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
name|boolean
name|filterMe
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
name|filteredOrDeletedDocs
operator|.
name|add
argument_list|(
name|parentDocId
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|filterMe
condition|)
block|{
name|filteredOrDeletedDocs
operator|.
name|add
argument_list|(
name|parentDocId
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"filter"
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
init|=
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
literal|100
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
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|FloatArrayList
argument_list|>
name|parentIdToChildScores
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
name|parentIdToChildScores
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
name|parentIdToChildScores
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|markParentAsDeleted
operator|&&
operator|!
name|filterMe
condition|)
block|{
name|FloatArrayList
name|childScores
init|=
name|parentIdToChildScores
operator|.
name|get
argument_list|(
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
name|childScores
operator|==
literal|null
condition|)
block|{
name|parentIdToChildScores
operator|.
name|put
argument_list|(
name|parent
argument_list|,
name|childScores
operator|=
operator|new
name|FloatArrayList
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|childScores
operator|.
name|add
argument_list|(
literal|1f
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
name|commit
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
name|ChildrenQueryTests
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
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
literal|"child"
argument_list|)
operator|.
name|parentFieldMapper
argument_list|()
decl_stmt|;
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
init|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|fieldData
argument_list|()
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
argument_list|)
decl_stmt|;
name|FixedBitSetFilter
name|parentFilter
init|=
name|wrap
argument_list|(
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
argument_list|)
decl_stmt|;
name|Filter
name|rawFilterMe
init|=
operator|new
name|NotFilter
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"filter"
argument_list|,
literal|"me"
argument_list|)
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
comment|// Using this in FQ, will invoke / test the Scorer#advance(..) and also let the Weight#scorer not get live docs as acceptedDocs
name|Filter
name|filterMe
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|filterMe
operator|=
name|SearchContext
operator|.
name|current
argument_list|()
operator|.
name|filterCache
argument_list|()
operator|.
name|cache
argument_list|(
name|rawFilterMe
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|filterMe
operator|=
name|rawFilterMe
expr_stmt|;
block|}
comment|// Simulate a parent update
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
specifier|final
name|int
name|numberOfUpdatableParents
init|=
name|numParentDocs
operator|-
name|filteredOrDeletedDocs
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|numberOfUpdates
init|=
name|RandomInts
operator|.
name|randomIntBetween
argument_list|(
name|random
argument_list|()
argument_list|,
literal|0
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|numberOfUpdatableParents
argument_list|,
name|TEST_NIGHTLY
condition|?
literal|25
else|:
literal|5
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
name|numberOfUpdates
condition|;
name|j
operator|++
control|)
block|{
name|int
name|parentId
decl_stmt|;
do|do
block|{
name|parentId
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|numParentDocs
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|filteredOrDeletedDocs
operator|.
name|contains
argument_list|(
name|parentId
argument_list|)
condition|)
do|;
name|String
name|parentUid
init|=
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
name|parentId
argument_list|)
argument_list|)
decl_stmt|;
name|indexWriter
operator|.
name|deleteDocuments
argument_list|(
operator|new
name|Term
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|parentUid
argument_list|)
argument_list|)
expr_stmt|;
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
name|parentUid
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
name|indexWriter
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexReader
operator|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
operator|.
name|w
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|searcher
operator|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
expr_stmt|;
name|engineSearcher
operator|=
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
expr_stmt|;
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
block|}
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
name|Query
name|childQuery
init|=
operator|new
name|ConstantScoreQuery
argument_list|(
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
name|ScoreType
name|scoreType
init|=
name|ScoreType
operator|.
name|values
argument_list|()
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|ScoreType
operator|.
name|values
argument_list|()
operator|.
name|length
argument_list|)
index|]
decl_stmt|;
name|FixedBitSetFilter
name|nonNestedDocsFilter
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|wrap
argument_list|(
name|NonNestedDocsFilter
operator|.
name|INSTANCE
argument_list|)
else|:
literal|null
decl_stmt|;
comment|// leave min/max set to 0 half the time
name|int
name|minChildren
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|*
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
literal|110
argument_list|)
decl_stmt|;
name|int
name|maxChildren
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|2
argument_list|)
operator|*
name|scaledRandomIntBetween
argument_list|(
name|minChildren
argument_list|,
literal|110
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|ChildrenQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
literal|"parent"
argument_list|,
literal|"child"
argument_list|,
name|parentFilter
argument_list|,
name|childQuery
argument_list|,
name|scoreType
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|nonNestedDocsFilter
argument_list|)
decl_stmt|;
name|query
operator|=
operator|new
name|XFilteredQuery
argument_list|(
name|query
argument_list|,
name|filterMe
argument_list|)
expr_stmt|;
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
name|int
name|numHits
init|=
literal|1
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|25
argument_list|)
decl_stmt|;
name|TopScoreDocCollector
name|actualTopDocsCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numHits
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|MultiCollector
operator|.
name|wrap
argument_list|(
name|collector
argument_list|,
name|actualTopDocsCollector
argument_list|)
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
name|MockScorer
name|mockScorer
init|=
operator|new
name|MockScorer
argument_list|(
name|scoreType
argument_list|)
decl_stmt|;
name|TopScoreDocCollector
name|expectedTopDocsCollector
init|=
name|TopScoreDocCollector
operator|.
name|create
argument_list|(
name|numHits
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|expectedTopDocsCollector
operator|.
name|setScorer
argument_list|(
name|mockScorer
argument_list|)
expr_stmt|;
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
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|FloatArrayList
argument_list|>
name|parentIdToChildScores
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
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|FloatArrayList
argument_list|>
name|entry
range|:
name|parentIdToChildScores
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|count
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|elementsCount
decl_stmt|;
if|if
condition|(
name|count
operator|>=
name|minChildren
operator|&&
operator|(
name|maxChildren
operator|==
literal|0
operator|||
name|count
operator|<=
name|maxChildren
operator|)
condition|)
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
name|entry
operator|.
name|getKey
argument_list|()
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
name|mockScorer
operator|.
name|scores
operator|=
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|expectedTopDocsCollector
operator|.
name|collect
argument_list|(
name|docsEnum
operator|.
name|docID
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
name|assertTopDocs
argument_list|(
name|actualTopDocsCollector
operator|.
name|topDocs
argument_list|()
argument_list|,
name|expectedTopDocsCollector
operator|.
name|topDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
block|}
end_class

end_unit

