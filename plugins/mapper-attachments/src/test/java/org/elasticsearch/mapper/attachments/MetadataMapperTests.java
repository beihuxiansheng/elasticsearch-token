begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.mapper.attachments
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|mapper
operator|.
name|attachments
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
name|bytes
operator|.
name|BytesReference
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
name|MapperParsingException
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
name|mapper
operator|.
name|attachments
operator|.
name|AttachmentMapper
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
name|elasticsearch
operator|.
name|test
operator|.
name|StreamsUtils
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
name|test
operator|.
name|StreamsUtils
operator|.
name|copyToStringFromClasspath
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
comment|/**  * Test for https://github.com/elasticsearch/elasticsearch-mapper-attachments/issues/38  */
end_comment

begin_class
DECL|class|MetadataMapperTests
specifier|public
class|class
name|MetadataMapperTests
extends|extends
name|AttachmentUnitTestCase
block|{
DECL|method|checkMeta
specifier|protected
name|void
name|checkMeta
parameter_list|(
name|String
name|filename
parameter_list|,
name|Settings
name|otherSettings
parameter_list|,
name|Long
name|expectedDate
parameter_list|,
name|Long
name|expectedLength
parameter_list|)
throws|throws
name|IOException
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|this
operator|.
name|testSettings
argument_list|)
operator|.
name|put
argument_list|(
name|otherSettings
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|DocumentMapperParser
name|mapperParser
init|=
name|MapperTestUtils
operator|.
name|newMapperService
argument_list|(
name|createTempDir
argument_list|()
argument_list|,
name|settings
argument_list|)
operator|.
name|documentMapperParser
argument_list|()
decl_stmt|;
name|String
name|mapping
init|=
name|copyToStringFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/attachment/test/unit/metadata/test-mapping.json"
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|mapperParser
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|byte
index|[]
name|html
init|=
name|copyToBytesFromClasspath
argument_list|(
literal|"/org/elasticsearch/index/mapper/attachment/test/sample-files/"
operator|+
name|filename
argument_list|)
decl_stmt|;
name|BytesReference
name|json
init|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
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
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
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
literal|"person"
argument_list|,
literal|"person"
argument_list|,
literal|"1"
argument_list|,
name|json
argument_list|)
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
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
name|getMapper
argument_list|(
literal|"file.content"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"World"
argument_list|)
argument_list|)
expr_stmt|;
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
name|getMapper
argument_list|(
literal|"file.name"
argument_list|)
operator|.
name|fieldType
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
name|filename
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedDate
operator|==
literal|null
condition|)
block|{
name|assertThat
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"file.date"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"file.date"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedDate
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|getMapper
argument_list|(
literal|"file.title"
argument_list|)
operator|.
name|fieldType
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
literal|"Hello"
argument_list|)
argument_list|)
expr_stmt|;
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
name|getMapper
argument_list|(
literal|"file.author"
argument_list|)
operator|.
name|fieldType
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
literal|"kimchy"
argument_list|)
argument_list|)
expr_stmt|;
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
name|getMapper
argument_list|(
literal|"file.keywords"
argument_list|)
operator|.
name|fieldType
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
literal|"elasticsearch,cool,bonsai"
argument_list|)
argument_list|)
expr_stmt|;
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
name|getMapper
argument_list|(
literal|"file.content_type"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
argument_list|,
name|startsWith
argument_list|(
literal|"text/html;"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedLength
operator|==
literal|null
condition|)
block|{
name|assertNull
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"file.content_length"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|doc
operator|.
name|getField
argument_list|(
name|docMapper
operator|.
name|mappers
argument_list|()
operator|.
name|getMapper
argument_list|(
literal|"file.content_length"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|,
name|greaterThan
argument_list|(
literal|0L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIgnoreWithoutDate
specifier|public
name|void
name|testIgnoreWithoutDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMeta
argument_list|(
literal|"htmlWithoutDateMeta.html"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|300L
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreWithEmptyDate
specifier|public
name|void
name|testIgnoreWithEmptyDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMeta
argument_list|(
literal|"htmlWithEmptyDateMeta.html"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|334L
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreWithCorrectDate
specifier|public
name|void
name|testIgnoreWithCorrectDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMeta
argument_list|(
literal|"htmlWithValidDateMeta.html"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
literal|1354233600000L
argument_list|,
literal|344L
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithoutDate
specifier|public
name|void
name|testWithoutDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMeta
argument_list|(
literal|"htmlWithoutDateMeta.html"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.ignore_errors"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|300L
argument_list|)
expr_stmt|;
block|}
DECL|method|testWithEmptyDate
specifier|public
name|void
name|testWithEmptyDate
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|checkMeta
argument_list|(
literal|"htmlWithEmptyDateMeta.html"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.ignore_errors"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"failed to parse"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testWithCorrectDate
specifier|public
name|void
name|testWithCorrectDate
parameter_list|()
throws|throws
name|Exception
block|{
name|checkMeta
argument_list|(
literal|"htmlWithValidDateMeta.html"
argument_list|,
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.attachment.ignore_errors"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|1354233600000L
argument_list|,
literal|344L
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

