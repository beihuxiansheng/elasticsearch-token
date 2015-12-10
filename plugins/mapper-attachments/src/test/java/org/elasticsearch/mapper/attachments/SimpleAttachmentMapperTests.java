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
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
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
name|compress
operator|.
name|CompressedXContent
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
name|MapperTestUtils
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
name|MapperService
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
name|junit
operator|.
name|Test
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
comment|/**  *  */
end_comment

begin_class
DECL|class|SimpleAttachmentMapperTests
specifier|public
class|class
name|SimpleAttachmentMapperTests
extends|extends
name|AttachmentUnitTestCase
block|{
DECL|method|testSimpleMappings
specifier|public
name|void
name|testSimpleMappings
parameter_list|()
throws|throws
name|Exception
block|{
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
name|Settings
operator|.
name|EMPTY
argument_list|,
name|getIndicesModuleWithRegisteredAttachmentMapper
argument_list|()
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
literal|"/org/elasticsearch/index/mapper/attachment/test/unit/simple/test-mapping.json"
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
literal|"/org/elasticsearch/index/mapper/attachment/test/sample-files/testXHTML.html"
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
name|field
argument_list|(
literal|"file"
argument_list|,
name|html
argument_list|)
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
literal|"application/xhtml+xml"
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
literal|"XHTML test document"
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
literal|"This document tests the ability of Apache Tika to extract content"
argument_list|)
argument_list|)
expr_stmt|;
comment|// re-parse it
name|String
name|builtMapping
init|=
name|docMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|docMapper
operator|=
name|mapperParser
operator|.
name|parse
argument_list|(
name|builtMapping
argument_list|)
expr_stmt|;
name|json
operator|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"file"
argument_list|,
name|html
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|doc
operator|=
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
literal|"application/xhtml+xml"
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
literal|"XHTML test document"
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
literal|"This document tests the ability of Apache Tika to extract content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testContentBackcompat
specifier|public
name|void
name|testContentBackcompat
parameter_list|()
throws|throws
name|Exception
block|{
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
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|V_1_4_2
operator|.
name|id
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|getIndicesModuleWithRegisteredAttachmentMapper
argument_list|()
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
literal|"/org/elasticsearch/index/mapper/attachment/test/unit/simple/test-mapping.json"
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
literal|"/org/elasticsearch/index/mapper/attachment/test/sample-files/testXHTML.html"
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
name|field
argument_list|(
literal|"file"
argument_list|,
name|html
argument_list|)
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
literal|"file"
argument_list|)
argument_list|,
name|containsString
argument_list|(
literal|"This document tests the ability of Apache Tika to extract content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * test for https://github.com/elastic/elasticsearch-mapper-attachments/issues/179      */
DECL|method|testSimpleMappingsWithAllFields
specifier|public
name|void
name|testSimpleMappingsWithAllFields
parameter_list|()
throws|throws
name|Exception
block|{
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
name|Settings
operator|.
name|EMPTY
argument_list|,
name|getIndicesModuleWithRegisteredAttachmentMapper
argument_list|()
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
literal|"/org/elasticsearch/index/mapper/attachment/test/unit/simple/test-mapping-all-fields.json"
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
literal|"/org/elasticsearch/index/mapper/attachment/test/sample-files/testXHTML.html"
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
name|field
argument_list|(
literal|"file"
argument_list|,
name|html
argument_list|)
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
literal|"application/xhtml+xml"
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
literal|"XHTML test document"
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
literal|"This document tests the ability of Apache Tika to extract content"
argument_list|)
argument_list|)
expr_stmt|;
comment|// re-parse it
name|String
name|builtMapping
init|=
name|docMapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|docMapper
operator|=
name|mapperParser
operator|.
name|parse
argument_list|(
name|builtMapping
argument_list|)
expr_stmt|;
name|json
operator|=
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"file"
argument_list|,
name|html
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|doc
operator|=
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
literal|"application/xhtml+xml"
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
literal|"XHTML test document"
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
literal|"This document tests the ability of Apache Tika to extract content"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * See issue https://github.com/elastic/elasticsearch-mapper-attachments/issues/169      * Mapping should not contain field names with dot.      */
DECL|method|testMapperErrorWithDotTwoLevels169
specifier|public
name|void
name|testMapperErrorWithDotTwoLevels169
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|mappingBuilder
init|=
name|jsonBuilder
argument_list|()
decl_stmt|;
name|mappingBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"mail"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"attachments"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"innerfield"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"attachment"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|byte
index|[]
name|mapping
init|=
name|mappingBuilder
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
decl_stmt|;
name|MapperService
name|mapperService
init|=
name|MapperTestUtils
operator|.
name|newMapperService
argument_list|(
name|createTempDir
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|getIndicesModuleWithRegisteredAttachmentMapper
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentMapper
name|docMapper
init|=
name|mapperService
operator|.
name|parse
argument_list|(
literal|"mail"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// this should not throw an exception
name|mapperService
operator|.
name|parse
argument_list|(
literal|"mail"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|docMapper
operator|.
name|mapping
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// the mapping may not contain a field name with a dot
name|assertFalse
argument_list|(
name|docMapper
operator|.
name|mapping
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

