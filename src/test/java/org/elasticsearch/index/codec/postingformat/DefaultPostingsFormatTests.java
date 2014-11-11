begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.codec.postingformat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|postingformat
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
name|WhitespaceAnalyzer
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
name|codecs
operator|.
name|Codec
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|lucene50
operator|.
name|Lucene50Codec
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
operator|.
name|Store
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
name|index
operator|.
name|codec
operator|.
name|postingsformat
operator|.
name|BloomFilterPostingsFormat
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
name|codec
operator|.
name|postingsformat
operator|.
name|Elasticsearch090PostingsFormat
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|*
import|;
end_import

begin_comment
comment|/**  * Simple smoke test for {@link org.elasticsearch.index.codec.postingsformat.Elasticsearch090PostingsFormat}  */
end_comment

begin_class
DECL|class|DefaultPostingsFormatTests
specifier|public
class|class
name|DefaultPostingsFormatTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|class|TestCodec
specifier|private
specifier|final
class|class
name|TestCodec
extends|extends
name|Lucene50Codec
block|{
annotation|@
name|Override
DECL|method|getPostingsFormatForField
specifier|public
name|PostingsFormat
name|getPostingsFormatForField
parameter_list|(
name|String
name|field
parameter_list|)
block|{
return|return
operator|new
name|Elasticsearch090PostingsFormat
argument_list|()
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testUseDefault
specifier|public
name|void
name|testUseDefault
parameter_list|()
throws|throws
name|IOException
block|{
name|Codec
name|codec
init|=
operator|new
name|TestCodec
argument_list|()
decl_stmt|;
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|config
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|config
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
operator|new
name|TextField
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
literal|"1234"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
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
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|LeafReader
name|ar
init|=
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|ar
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|Terms
name|uidTerms
init|=
name|ar
operator|.
name|terms
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|terms
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
name|assertThat
argument_list|(
name|terms
argument_list|,
name|not
argument_list|(
name|instanceOf
argument_list|(
name|BloomFilterPostingsFormat
operator|.
name|BloomFilteredTerms
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|uidTerms
argument_list|,
name|not
argument_list|(
name|instanceOf
argument_list|(
name|BloomFilterPostingsFormat
operator|.
name|BloomFilteredTerms
operator|.
name|class
argument_list|)
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
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNoUIDField
specifier|public
name|void
name|testNoUIDField
parameter_list|()
throws|throws
name|IOException
block|{
name|Codec
name|codec
init|=
operator|new
name|TestCodec
argument_list|()
decl_stmt|;
name|Directory
name|d
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|IndexWriterConfig
name|config
init|=
operator|new
name|IndexWriterConfig
argument_list|(
operator|new
name|WhitespaceAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|config
operator|.
name|setCodec
argument_list|(
name|codec
argument_list|)
expr_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|d
argument_list|,
name|config
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
name|writer
operator|.
name|addDocument
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|TextField
argument_list|(
literal|"foo"
argument_list|,
literal|"foo bar foo bar"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|,
operator|new
name|TextField
argument_list|(
literal|"some_other_field"
argument_list|,
literal|"1234"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|DirectoryReader
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
name|List
argument_list|<
name|LeafReaderContext
argument_list|>
name|leaves
init|=
name|reader
operator|.
name|leaves
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|leaves
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|LeafReader
name|ar
init|=
name|leaves
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|ar
operator|.
name|terms
argument_list|(
literal|"foo"
argument_list|)
decl_stmt|;
name|Terms
name|some_other_field
init|=
name|ar
operator|.
name|terms
argument_list|(
literal|"some_other_field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|terms
argument_list|,
name|not
argument_list|(
name|instanceOf
argument_list|(
name|BloomFilterPostingsFormat
operator|.
name|BloomFilteredTerms
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|some_other_field
argument_list|,
name|not
argument_list|(
name|instanceOf
argument_list|(
name|BloomFilterPostingsFormat
operator|.
name|BloomFilteredTerms
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|TermsEnum
name|iterator
init|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
while|while
condition|(
name|iterator
operator|.
name|next
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|expected
operator|.
name|remove
argument_list|(
name|iterator
operator|.
name|term
argument_list|()
operator|.
name|utf8ToString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|expected
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
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
name|d
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

