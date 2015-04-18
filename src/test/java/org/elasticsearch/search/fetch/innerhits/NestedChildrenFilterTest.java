begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.innerhits
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|innerhits
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
name|IntField
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
name|RandomIndexWriter
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
name|search
operator|.
name|join
operator|.
name|BitDocIdSetCachingWrapperFilter
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
name|BitDocIdSetFilter
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
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|FetchSubPhase
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
name|fetch
operator|.
name|innerhits
operator|.
name|InnerHitsContext
operator|.
name|NestedInnerHits
operator|.
name|NestedChildrenFilter
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
name|ElasticsearchTestCase
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
name|List
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|NestedChildrenFilterTest
specifier|public
class|class
name|NestedChildrenFilterTest
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testNestedChildrenFilter
specifier|public
name|void
name|testNestedChildrenFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|numParentDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
literal|32
argument_list|)
decl_stmt|;
name|int
name|maxChildDocsPerParent
init|=
name|scaledRandomIntBetween
argument_list|(
literal|8
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
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
name|numParentDocs
condition|;
name|i
operator|++
control|)
block|{
name|int
name|numChildDocs
init|=
name|scaledRandomIntBetween
argument_list|(
literal|0
argument_list|,
name|maxChildDocsPerParent
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Document
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numChildDocs
operator|+
literal|1
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
name|numChildDocs
condition|;
name|j
operator|++
control|)
block|{
name|Document
name|childDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|childDoc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"type"
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
name|docs
operator|.
name|add
argument_list|(
name|childDoc
argument_list|)
expr_stmt|;
block|}
name|Document
name|parenDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|parenDoc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"type"
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
name|parenDoc
operator|.
name|add
argument_list|(
operator|new
name|IntField
argument_list|(
literal|"num_child_docs"
argument_list|,
name|numChildDocs
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|parenDoc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
name|IndexReader
name|reader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|FetchSubPhase
operator|.
name|HitContext
name|hitContext
init|=
operator|new
name|FetchSubPhase
operator|.
name|HitContext
argument_list|()
decl_stmt|;
name|BitDocIdSetFilter
name|parentFilter
init|=
operator|new
name|BitDocIdSetCachingWrapperFilter
argument_list|(
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"parent"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|Filter
name|childFilter
init|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"type"
argument_list|,
literal|"child"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|checkedParents
init|=
literal|0
decl_stmt|;
for|for
control|(
name|LeafReaderContext
name|leaf
range|:
name|reader
operator|.
name|leaves
argument_list|()
control|)
block|{
name|DocIdSetIterator
name|parents
init|=
name|parentFilter
operator|.
name|getDocIdSet
argument_list|(
name|leaf
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|parentDoc
init|=
name|parents
operator|.
name|nextDoc
argument_list|()
init|;
name|parentDoc
operator|!=
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|;
name|parentDoc
operator|=
name|parents
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|int
name|expectedChildDocs
init|=
name|leaf
operator|.
name|reader
argument_list|()
operator|.
name|document
argument_list|(
name|parentDoc
argument_list|)
operator|.
name|getField
argument_list|(
literal|"num_child_docs"
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|hitContext
operator|.
name|reset
argument_list|(
literal|null
argument_list|,
name|leaf
argument_list|,
name|parentDoc
argument_list|,
name|reader
argument_list|)
expr_stmt|;
name|NestedChildrenFilter
name|nestedChildrenFilter
init|=
operator|new
name|NestedChildrenFilter
argument_list|(
name|parentFilter
argument_list|,
name|childFilter
argument_list|,
name|hitContext
argument_list|)
decl_stmt|;
name|TotalHitCountCollector
name|totalHitCountCollector
init|=
operator|new
name|TotalHitCountCollector
argument_list|()
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|ConstantScoreQuery
argument_list|(
name|nestedChildrenFilter
argument_list|)
argument_list|,
name|totalHitCountCollector
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|totalHitCountCollector
operator|.
name|getTotalHits
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedChildDocs
argument_list|)
argument_list|)
expr_stmt|;
name|checkedParents
operator|++
expr_stmt|;
block|}
block|}
name|assertThat
argument_list|(
name|checkedParents
argument_list|,
name|equalTo
argument_list|(
name|numParentDocs
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

