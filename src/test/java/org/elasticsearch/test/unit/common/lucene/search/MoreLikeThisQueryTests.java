begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.common.lucene.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
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
name|common
operator|.
name|lucene
operator|.
name|search
operator|.
name|MoreLikeThisQuery
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MoreLikeThisQueryTests
specifier|public
class|class
name|MoreLikeThisQueryTests
block|{
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
name|VERSION
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
argument_list|)
decl_stmt|;
name|indexWriter
operator|.
name|commit
argument_list|()
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
literal|"text"
argument_list|,
literal|"lucene"
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
name|TextField
argument_list|(
literal|"_id"
argument_list|,
literal|"2"
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
literal|"text"
argument_list|,
literal|"lucene release"
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
name|IndexReader
operator|.
name|open
argument_list|(
name|indexWriter
argument_list|,
literal|true
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
name|MoreLikeThisQuery
name|mltQuery
init|=
operator|new
name|MoreLikeThisQuery
argument_list|(
literal|"lucene"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"text"
block|}
argument_list|,
name|Lucene
operator|.
name|STANDARD_ANALYZER
argument_list|)
decl_stmt|;
name|mltQuery
operator|.
name|setLikeText
argument_list|(
literal|"lucene"
argument_list|)
expr_stmt|;
name|mltQuery
operator|.
name|setMinTermFrequency
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mltQuery
operator|.
name|setMinDocFreq
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|long
name|count
init|=
name|Lucene
operator|.
name|count
argument_list|(
name|searcher
argument_list|,
name|mltQuery
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|count
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|indexWriter
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

