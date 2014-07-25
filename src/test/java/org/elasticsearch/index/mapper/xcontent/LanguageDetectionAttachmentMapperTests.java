begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
package|;
end_package

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
name|xcontent
operator|.
name|XContentBuilder
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
name|mapper
operator|.
name|DocumentMapper
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
name|DocumentMapperParser
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
name|ParseContext
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
name|attachment
operator|.
name|AttachmentMapper
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
name|core
operator|.
name|StringFieldMapper
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
name|Before
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToBytesFromClasspath
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|Streams
operator|.
name|copyToStringFromClasspath
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
operator|.
name|jsonBuilder
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
name|instanceOf
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|LanguageDetectionAttachmentMapperTests
specifier|public
class|class
name|LanguageDetectionAttachmentMapperTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|field|docMapper
specifier|private
name|DocumentMapper
name|docMapper
decl_stmt|;
annotation|@
name|Before
DECL|method|setupMapperParser
specifier|public
name|void
name|setupMapperParser
parameter_list|()
throws|throws
name|IOException
block|{
name|setupMapperParser
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|setupMapperParser
specifier|public
name|void
name|setupMapperParser
parameter_list|(
name|boolean
name|langDetect
parameter_list|)
throws|throws
name|IOException
block|{
name|DocumentMapperParser
name|mapperParser
init|=
operator|new
name|DocumentMapperParser
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.detect_language"
argument_list|,
name|langDetect
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
operator|new
name|AnalysisService
argument_list|(
operator|new
name|Index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|mapperParser
operator|.
name|putTypeParser
argument_list|(
name|AttachmentMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|AttachmentMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/language/language-mapping.json"
argument_list|)
decl_stmt|;
name|docMapper
operator|=
name|mapperParser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|fullName
argument_list|(
literal|"file.language"
argument_list|)
operator|.
name|mapper
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|StringFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testLanguage
specifier|private
name|void
name|testLanguage
parameter_list|(
name|String
name|filename
parameter_list|,
name|String
name|expected
parameter_list|,
name|String
modifier|...
name|forcedLanguage
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|html
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/xcontent/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|XContentBuilder
name|xcb
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
literal|1
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"file"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
name|filename
argument_list|)
operator|.
name|field
argument_list|(
literal|"_content"
argument_list|,
name|html
argument_list|)
decl_stmt|;
if|if
condition|(
name|forcedLanguage
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|xcb
operator|.
name|field
argument_list|(
literal|"_language"
argument_list|,
name|forcedLanguage
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
name|xcb
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|ParseContext
operator|.
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|xcb
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
comment|// Our mapping should be kept as a String
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.language"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFrDetection
specifier|public
name|void
name|testFrDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|testLanguage
argument_list|(
literal|"text-in-french.txt"
argument_list|,
literal|"fr"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnDetection
specifier|public
name|void
name|testEnDetection
parameter_list|()
throws|throws
name|Exception
block|{
name|testLanguage
argument_list|(
literal|"text-in-english.txt"
argument_list|,
literal|"en"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFrForced
specifier|public
name|void
name|testFrForced
parameter_list|()
throws|throws
name|Exception
block|{
name|testLanguage
argument_list|(
literal|"text-in-english.txt"
argument_list|,
literal|"fr"
argument_list|,
literal|"fr"
argument_list|)
expr_stmt|;
block|}
comment|/**      * This test gives strange results! detection of ":-)" gives "lt" as a result      * @throws Exception      */
annotation|@
name|Test
DECL|method|testNoLanguage
specifier|public
name|void
name|testNoLanguage
parameter_list|()
throws|throws
name|Exception
block|{
name|testLanguage
argument_list|(
literal|"text-in-nolang.txt"
argument_list|,
literal|"lt"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLangDetectDisabled
specifier|public
name|void
name|testLangDetectDisabled
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We replace the mapper with another one which have index.mapping.attachment.detect_language = false
name|setupMapperParser
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|testLanguage
argument_list|(
literal|"text-in-english.txt"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLangDetectDocumentEnabled
specifier|public
name|void
name|testLangDetectDocumentEnabled
parameter_list|()
throws|throws
name|Exception
block|{
comment|// We replace the mapper with another one which have index.mapping.attachment.detect_language = false
name|setupMapperParser
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|byte
index|[]
name|html
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/xcontent/text-in-english.txt"
argument_list|)
decl_stmt|;
name|XContentBuilder
name|xcb
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"_id"
argument_list|,
literal|1
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"file"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_name"
argument_list|,
literal|"text-in-english.txt"
argument_list|)
operator|.
name|field
argument_list|(
literal|"_content"
argument_list|,
name|html
argument_list|)
operator|.
name|field
argument_list|(
literal|"_detect_language"
argument_list|,
literal|true
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
decl_stmt|;
name|ParseContext
operator|.
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|xcb
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
comment|// Our mapping should be kept as a String
name|assertThat
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartName
argument_list|(
literal|"file.language"
argument_list|)
operator|.
name|mapper
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"en"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

