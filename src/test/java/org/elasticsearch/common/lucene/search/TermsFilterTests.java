begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|search
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
name|core
operator|.
name|KeywordAnalyzer
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
name|queries
operator|.
name|TermsFilter
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
name|FixedBitSet
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
name|lucene
operator|.
name|docset
operator|.
name|DocIdSets
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
name|MatcherAssert
operator|.
name|assertThat
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
name|nullValue
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TermsFilterTests
specifier|public
class|class
name|TermsFilterTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testTermFilter
specifier|public
name|void
name|testTermFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"field1"
decl_stmt|;
name|Directory
name|rd
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|term
init|=
name|i
operator|*
literal|10
decl_stmt|;
comment|//terms are units of 10;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldName
argument_list|,
literal|""
operator|+
name|term
argument_list|,
name|StringField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"all"
argument_list|,
literal|"xxx"
argument_list|,
name|StringField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|40
operator|)
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|AtomicReader
name|reader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|TermFilter
name|tf
init|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"19"
argument_list|)
argument_list|)
decl_stmt|;
name|FixedBitSet
name|bits
init|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bits
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|DocIdSet
name|result
init|=
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|bits
operator|=
name|DocIdSets
operator|.
name|toFixedBitSet
argument_list|(
name|result
operator|.
name|iterator
argument_list|()
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TermFilter
argument_list|(
operator|new
name|Term
argument_list|(
literal|"all"
argument_list|,
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|bits
operator|=
name|DocIdSets
operator|.
name|toFixedBitSet
argument_list|(
name|result
operator|.
name|iterator
argument_list|()
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTermsFilter
specifier|public
name|void
name|testTermsFilter
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|fieldName
init|=
literal|"field1"
decl_stmt|;
name|Directory
name|rd
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|w
init|=
operator|new
name|IndexWriter
argument_list|(
name|rd
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|Lucene
operator|.
name|VERSION
argument_list|,
operator|new
name|KeywordAnalyzer
argument_list|()
argument_list|)
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|int
name|term
init|=
name|i
operator|*
literal|10
decl_stmt|;
comment|//terms are units of 10;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|fieldName
argument_list|,
literal|""
operator|+
name|term
argument_list|,
name|StringField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"all"
argument_list|,
literal|"xxx"
argument_list|,
name|StringField
operator|.
name|TYPE_NOT_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|%
literal|40
operator|)
operator|==
literal|0
condition|)
block|{
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|AtomicReader
name|reader
init|=
name|SlowCompositeReaderWrapper
operator|.
name|wrap
argument_list|(
name|DirectoryReader
operator|.
name|open
argument_list|(
name|w
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|TermsFilter
name|tf
init|=
operator|new
name|TermsFilter
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"19"
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|FixedBitSet
name|bits
init|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|bits
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TermsFilter
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"19"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"20"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TermsFilter
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"19"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"20"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"10"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|tf
operator|=
operator|new
name|TermsFilter
argument_list|(
operator|new
name|Term
index|[]
block|{
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"19"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"20"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"10"
argument_list|)
block|,
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|"00"
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|(
name|FixedBitSet
operator|)
name|tf
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
name|reader
operator|.
name|getLiveDocs
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|bits
operator|.
name|cardinality
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|rd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

