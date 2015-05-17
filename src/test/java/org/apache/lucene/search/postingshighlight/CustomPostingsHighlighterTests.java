begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elasticsearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.search.postingshighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|postingshighlight
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
name|highlight
operator|.
name|DefaultEncoder
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
name|highlight
operator|.
name|HighlightUtils
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|CustomPostingsHighlighterTests
specifier|public
class|class
name|CustomPostingsHighlighterTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testCustomPostingsHighlighter
specifier|public
name|void
name|testCustomPostingsHighlighter
parameter_list|()
throws|throws
name|Exception
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
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|FieldType
name|offsetsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|offsetsType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
comment|//good position but only one match
specifier|final
name|String
name|firstValue
init|=
literal|"This is a test. Just a test1 highlighting from postings highlighter."
decl_stmt|;
name|Field
name|body
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
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
name|body
argument_list|)
expr_stmt|;
name|body
operator|.
name|setStringValue
argument_list|(
name|firstValue
argument_list|)
expr_stmt|;
comment|//two matches, not the best snippet due to its length though
specifier|final
name|String
name|secondValue
init|=
literal|"This is the second highlighting value to perform highlighting on a longer text that gets scored lower."
decl_stmt|;
name|Field
name|body2
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|body2
argument_list|)
expr_stmt|;
name|body2
operator|.
name|setStringValue
argument_list|(
name|secondValue
argument_list|)
expr_stmt|;
comment|//two matches and short, will be scored highest
specifier|final
name|String
name|thirdValue
init|=
literal|"This is highlighting the third short highlighting value."
decl_stmt|;
name|Field
name|body3
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|body3
argument_list|)
expr_stmt|;
name|body3
operator|.
name|setStringValue
argument_list|(
name|thirdValue
argument_list|)
expr_stmt|;
comment|//one match, same as first but at the end, will be scored lower due to its position
specifier|final
name|String
name|fourthValue
init|=
literal|"Just a test4 highlighting from postings highlighter."
decl_stmt|;
name|Field
name|body4
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|body4
argument_list|)
expr_stmt|;
name|body4
operator|.
name|setStringValue
argument_list|(
name|fourthValue
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|firstHlValue
init|=
literal|"Just a test1<b>highlighting</b> from postings highlighter."
decl_stmt|;
name|String
name|secondHlValue
init|=
literal|"This is the second<b>highlighting</b> value to perform<b>highlighting</b> on a longer text that gets scored lower."
decl_stmt|;
name|String
name|thirdHlValue
init|=
literal|"This is<b>highlighting</b> the third short<b>highlighting</b> value."
decl_stmt|;
name|String
name|fourthHlValue
init|=
literal|"Just a test4<b>highlighting</b> from postings highlighter."
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"body"
argument_list|,
literal|"highlighting"
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
name|Sort
operator|.
name|INDEXORDER
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
name|int
name|docId
init|=
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
decl_stmt|;
name|String
name|fieldValue
init|=
name|firstValue
operator|+
name|HighlightUtils
operator|.
name|PARAGRAPH_SEPARATOR
operator|+
name|secondValue
operator|+
name|HighlightUtils
operator|.
name|PARAGRAPH_SEPARATOR
operator|+
name|thirdValue
operator|+
name|HighlightUtils
operator|.
name|PARAGRAPH_SEPARATOR
operator|+
name|fourthValue
decl_stmt|;
name|CustomPostingsHighlighter
name|highlighter
init|=
operator|new
name|CustomPostingsHighlighter
argument_list|(
literal|null
argument_list|,
operator|new
name|CustomPassageFormatter
argument_list|(
literal|"<b>"
argument_list|,
literal|"</b>"
argument_list|,
operator|new
name|DefaultEncoder
argument_list|()
argument_list|)
argument_list|,
name|fieldValue
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Snippet
index|[]
name|snippets
init|=
name|highlighter
operator|.
name|highlightField
argument_list|(
literal|"body"
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|,
name|docId
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|snippets
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|snippets
index|[
literal|0
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|firstHlValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|snippets
index|[
literal|1
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|secondHlValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|snippets
index|[
literal|2
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|thirdHlValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|snippets
index|[
literal|3
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|fourthHlValue
argument_list|)
argument_list|)
expr_stmt|;
name|ir
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
annotation|@
name|Test
DECL|method|testNoMatchSize
specifier|public
name|void
name|testNoMatchSize
parameter_list|()
throws|throws
name|Exception
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
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|iwc
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
name|RandomIndexWriter
name|iw
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
name|iwc
argument_list|)
decl_stmt|;
name|FieldType
name|offsetsType
init|=
operator|new
name|FieldType
argument_list|(
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
decl_stmt|;
name|offsetsType
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS
argument_list|)
expr_stmt|;
name|Field
name|body
init|=
operator|new
name|Field
argument_list|(
literal|"body"
argument_list|,
literal|""
argument_list|,
name|offsetsType
argument_list|)
decl_stmt|;
name|Field
name|none
init|=
operator|new
name|Field
argument_list|(
literal|"none"
argument_list|,
literal|""
argument_list|,
name|offsetsType
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
name|body
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|none
argument_list|)
expr_stmt|;
name|String
name|firstValue
init|=
literal|"This is a test. Just a test highlighting from postings. Feel free to ignore."
decl_stmt|;
name|body
operator|.
name|setStringValue
argument_list|(
name|firstValue
argument_list|)
expr_stmt|;
name|none
operator|.
name|setStringValue
argument_list|(
name|firstValue
argument_list|)
expr_stmt|;
name|iw
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexReader
name|ir
init|=
name|iw
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|Query
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"none"
argument_list|,
literal|"highlighting"
argument_list|)
argument_list|)
decl_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|ir
argument_list|)
decl_stmt|;
name|TopDocs
name|topDocs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
literal|10
argument_list|,
name|Sort
operator|.
name|INDEXORDER
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
name|int
name|docId
init|=
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
decl_stmt|;
name|CustomPassageFormatter
name|passageFormatter
init|=
operator|new
name|CustomPassageFormatter
argument_list|(
literal|"<b>"
argument_list|,
literal|"</b>"
argument_list|,
operator|new
name|DefaultEncoder
argument_list|()
argument_list|)
decl_stmt|;
name|CustomPostingsHighlighter
name|highlighter
init|=
operator|new
name|CustomPostingsHighlighter
argument_list|(
literal|null
argument_list|,
name|passageFormatter
argument_list|,
name|firstValue
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Snippet
index|[]
name|snippets
init|=
name|highlighter
operator|.
name|highlightField
argument_list|(
literal|"body"
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|,
name|docId
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|snippets
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|highlighter
operator|=
operator|new
name|CustomPostingsHighlighter
argument_list|(
literal|null
argument_list|,
name|passageFormatter
argument_list|,
name|firstValue
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|snippets
operator|=
name|highlighter
operator|.
name|highlightField
argument_list|(
literal|"body"
argument_list|,
name|query
argument_list|,
name|searcher
argument_list|,
name|docId
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|snippets
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|snippets
index|[
literal|0
index|]
operator|.
name|getText
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"This is a test."
argument_list|)
argument_list|)
expr_stmt|;
name|ir
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

