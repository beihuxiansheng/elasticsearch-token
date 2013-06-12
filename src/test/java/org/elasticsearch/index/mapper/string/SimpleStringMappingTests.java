begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.string
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|string
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
name|index
operator|.
name|FieldInfo
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
name|FieldInfo
operator|.
name|DocValuesType
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
name|IndexableField
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
name|XContentFactory
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
name|fielddata
operator|.
name|FieldDataType
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
name|*
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
name|Mapper
operator|.
name|BuilderContext
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
name|ElasticSearchTestCase
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
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|SimpleStringMappingTests
specifier|public
class|class
name|SimpleStringMappingTests
extends|extends
name|ElasticSearchTestCase
block|{
DECL|field|DOC_VALUES_SETTINGS
specifier|private
specifier|static
name|Settings
name|DOC_VALUES_SETTINGS
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|FieldDataType
operator|.
name|FORMAT_KEY
argument_list|,
name|FieldDataType
operator|.
name|DOC_VALUES_FORMAT_VALUE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testLimit
specifier|public
name|void
name|testLimit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ignore_above"
argument_list|,
literal|5
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"12345"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"123456"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultsForAnalyzed
specifier|public
name|void
name|testDefaultsForAnalyzed
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultsForNotAnalyzed
specifier|public
name|void
name|testDefaultsForNotAnalyzed
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_ONLY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// now test it explicitly set
name|mapping
operator|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
argument_list|)
operator|.
name|field
argument_list|(
literal|"omit_norms"
argument_list|,
literal|false
argument_list|)
operator|.
name|field
argument_list|(
literal|"index_options"
argument_list|,
literal|"freqs"
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
name|string
argument_list|()
expr_stmt|;
name|defaultMapper
operator|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
name|doc
operator|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|omitNorms
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|indexOptions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|FieldInfo
operator|.
name|IndexOptions
operator|.
name|DOCS_AND_FREQS
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTermVectors
specifier|public
name|void
name|testTermVectors
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
literal|"with_offsets"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field4"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
literal|"with_positions"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field5"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
literal|"with_positions_offsets"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"field6"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
literal|"with_positions_offsets_payloads"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"field1"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field3"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field4"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field5"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field6"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field1"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field2"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field3"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field4"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field4"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field4"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field4"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field5"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field5"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field5"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field5"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field6"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectors
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field6"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorOffsets
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field6"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPositions
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"field6"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|storeTermVectorPayloads
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDocValues
specifier|public
name|void
name|testDocValues
parameter_list|()
throws|throws
name|Exception
block|{
comment|// doc values only work on non-analyzed content
specifier|final
name|BuilderContext
name|ctx
init|=
operator|new
name|BuilderContext
argument_list|(
literal|null
argument_list|,
operator|new
name|ContentPath
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|StringFieldMapper
operator|.
name|Builder
argument_list|(
literal|"anything"
argument_list|)
operator|.
name|fieldDataSettings
argument_list|(
name|DOC_VALUES_SETTINGS
argument_list|)
operator|.
name|build
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|/* OK */
block|}
operator|new
name|StringFieldMapper
operator|.
name|Builder
argument_list|(
literal|"anything"
argument_list|)
operator|.
name|tokenized
argument_list|(
literal|false
argument_list|)
operator|.
name|fieldDataSettings
argument_list|(
name|DOC_VALUES_SETTINGS
argument_list|)
operator|.
name|build
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
operator|new
name|StringFieldMapper
operator|.
name|Builder
argument_list|(
literal|"anything"
argument_list|)
operator|.
name|index
argument_list|(
literal|false
argument_list|)
operator|.
name|fieldDataSettings
argument_list|(
name|DOC_VALUES_SETTINGS
argument_list|)
operator|.
name|build
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|String
name|mapping
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"str1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"fst"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"str2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"not_analyzed"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"int"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"integer"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"double"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"double"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fielddata"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"doc_values"
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
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|ParsedDocument
name|parsedDoc
init|=
name|defaultMapper
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
literal|"1"
argument_list|,
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"str1"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"str2"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"int"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|field
argument_list|(
literal|"double"
argument_list|,
literal|"1234"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|doc
init|=
name|parsedDoc
operator|.
name|rootDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|null
argument_list|,
name|docValuesType
argument_list|(
name|doc
argument_list|,
literal|"str1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|docValuesType
argument_list|(
name|doc
argument_list|,
literal|"str2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|docValuesType
argument_list|(
name|doc
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_SET
argument_list|,
name|docValuesType
argument_list|(
name|doc
argument_list|,
literal|"double"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|docValuesType
specifier|private
specifier|static
name|DocValuesType
name|docValuesType
parameter_list|(
name|Document
name|document
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
for|for
control|(
name|IndexableField
name|field
range|:
name|document
operator|.
name|getFields
argument_list|(
name|fieldName
argument_list|)
control|)
block|{
if|if
condition|(
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|docValueType
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|field
operator|.
name|fieldType
argument_list|()
operator|.
name|docValueType
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

