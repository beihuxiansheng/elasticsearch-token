begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_class
DECL|class|MultiPhrasePrefixQueryTests
specifier|public
class|class
name|MultiPhrasePrefixQueryTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|simpleTests
specifier|public
name|void
name|simpleTests
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
name|Field
argument_list|(
literal|"field"
argument_list|,
literal|"aaa bbb ccc ddd"
argument_list|,
name|TextField
operator|.
name|TYPE_NOT_STORED
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
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
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
name|MultiPhrasePrefixQuery
name|query
init|=
operator|new
name|MultiPhrasePrefixQuery
argument_list|()
decl_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aa"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Lucene
operator|.
name|count
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|MultiPhrasePrefixQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"bb"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Lucene
operator|.
name|count
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|MultiPhrasePrefixQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"aaa"
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"cc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Lucene
operator|.
name|count
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|1l
argument_list|)
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|MultiPhrasePrefixQuery
argument_list|()
expr_stmt|;
name|query
operator|.
name|setSlop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
operator|new
name|Term
argument_list|(
literal|"field"
argument_list|,
literal|"xxx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Lucene
operator|.
name|count
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

