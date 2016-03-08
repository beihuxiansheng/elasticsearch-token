begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.deps.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|deps
operator|.
name|lucene
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
name|FieldType
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
name|PrefixQuery
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
name|search
operator|.
name|vectorhighlight
operator|.
name|CustomFieldQuery
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
name|vectorhighlight
operator|.
name|FastVectorHighlighter
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
name|test
operator|.
name|ESTestCase
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
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
name|nullValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|VectorHighlighterTests
specifier|public
class|class
name|VectorHighlighterTests
extends|extends
name|ESTestCase
block|{
DECL|method|testVectorHighlighter
specifier|public
name|void
name|testVectorHighlighter
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|STANDARD_ANALYZER
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
name|TextField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|vectorsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"the big bad dog"
argument_list|,
name|vectorsType
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
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
name|TopDocs
name|topDocs
init|=
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
literal|"_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|String
name|fragment
init|=
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bad"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|reader
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"content"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|equalTo
argument_list|(
literal|"the big<b>bad</b> dog"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testVectorHighlighterPrefixQuery
specifier|public
name|void
name|testVectorHighlighterPrefixQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|STANDARD_ANALYZER
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
name|TextField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|vectorsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"the big bad dog"
argument_list|,
name|vectorsType
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
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
name|TopDocs
name|topDocs
init|=
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
literal|"_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|PrefixQuery
name|prefixQuery
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ba"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|prefixQuery
operator|.
name|getRewriteMethod
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|PrefixQuery
operator|.
name|CONSTANT_SCORE_REWRITE
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|fragment
init|=
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|prefixQuery
argument_list|)
argument_list|,
name|reader
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"content"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|prefixQuery
operator|.
name|setRewriteMethod
argument_list|(
name|PrefixQuery
operator|.
name|SCORING_BOOLEAN_REWRITE
argument_list|)
expr_stmt|;
name|Query
name|rewriteQuery
init|=
name|prefixQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|fragment
operator|=
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
name|rewriteQuery
argument_list|)
argument_list|,
name|reader
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"content"
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// now check with the custom field query
name|prefixQuery
operator|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"ba"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|prefixQuery
operator|.
name|getRewriteMethod
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|PrefixQuery
operator|.
name|CONSTANT_SCORE_REWRITE
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fragment
operator|=
name|highlighter
operator|.
name|getBestFragment
argument_list|(
operator|new
name|CustomFieldQuery
argument_list|(
name|prefixQuery
argument_list|,
name|reader
argument_list|,
name|highlighter
argument_list|)
argument_list|,
name|reader
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"content"
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testVectorHighlighterNoStore
specifier|public
name|void
name|testVectorHighlighterNoStore
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|STANDARD_ANALYZER
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
name|TextField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
name|FieldType
name|vectorsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_NOT_STORED
argument_list|)
decl_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectors
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorPositions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|vectorsType
operator|.
name|setStoreTermVectorOffsets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"the big bad dog"
argument_list|,
name|vectorsType
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
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
name|TopDocs
name|topDocs
init|=
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
literal|"_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|String
name|fragment
init|=
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bad"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|reader
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"content"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testVectorHighlighterNoTermVector
specifier|public
name|void
name|testVectorHighlighterNoTermVector
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|indexWriter
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|STANDARD_ANALYZER
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
name|TextField
argument_list|(
literal|"_id"
argument_list|,
literal|"1"
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
name|TextField
argument_list|(
literal|"content"
argument_list|,
literal|"the big bad dog"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|indexWriter
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
name|TopDocs
name|topDocs
init|=
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
literal|"_id"
argument_list|,
literal|"1"
argument_list|)
argument_list|)
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|topDocs
operator|.
name|totalHits
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|FastVectorHighlighter
name|highlighter
init|=
operator|new
name|FastVectorHighlighter
argument_list|()
decl_stmt|;
name|String
name|fragment
init|=
name|highlighter
operator|.
name|getBestFragment
argument_list|(
name|highlighter
operator|.
name|getFieldQuery
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"bad"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|reader
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|,
literal|"content"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fragment
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

