begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.timestamp
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|timestamp
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
name|xcontent
operator|.
name|ToXContent
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
name|common
operator|.
name|xcontent
operator|.
name|json
operator|.
name|JsonXContent
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
name|ParsedDocument
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
name|SourceToParse
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
name|TimestampFieldMapper
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
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
DECL|class|TimestampMappingTests
specifier|public
class|class
name|TimestampMappingTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testSimpleDisabled
specifier|public
name|void
name|testSimpleDisabled
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|BytesReference
name|source
init|=
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
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
name|source
argument_list|)
operator|.
name|type
argument_list|(
literal|"type"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
operator|.
name|timestamp
argument_list|(
literal|1
argument_list|)
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
literal|"_timestamp"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEnabled
specifier|public
name|void
name|testEnabled
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|BytesReference
name|source
init|=
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
literal|"value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|ParsedDocument
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
name|source
argument_list|)
operator|.
name|type
argument_list|(
literal|"type"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
operator|.
name|timestamp
argument_list|(
literal|1
argument_list|)
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
literal|"_timestamp"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
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
literal|"_timestamp"
argument_list|)
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
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
literal|"_timestamp"
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|docMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDefaultValues
specifier|public
name|void
name|testDefaultValues
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|enabled
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimestampFieldMapper
operator|.
name|Defaults
operator|.
name|ENABLED
operator|.
name|enabled
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimestampFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|stored
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimestampFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|indexed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|path
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|dateTimeFormatter
argument_list|()
operator|.
name|format
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|TimestampFieldMapper
operator|.
name|DEFAULT_DATE_TIME_FORMAT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSetValues
specifier|public
name|void
name|testSetValues
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|field
argument_list|(
literal|"path"
argument_list|,
literal|"timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"year"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|enabled
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
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
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
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|fieldType
argument_list|()
operator|.
name|indexed
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
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|path
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"timestamp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|docMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|dateTimeFormatter
argument_list|()
operator|.
name|format
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"year"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatDisablingDuringMergeIsWorking
specifier|public
name|void
name|testThatDisablingDuringMergeIsWorking
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|enabledMapping
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|enabledMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|enabledMapping
argument_list|)
decl_stmt|;
name|String
name|disabledMapping
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|disabledMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|disabledMapping
argument_list|)
decl_stmt|;
name|enabledMapper
operator|.
name|merge
argument_list|(
name|disabledMapper
argument_list|,
name|DocumentMapper
operator|.
name|MergeFlags
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|enabledMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|enabled
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testThatDisablingFieldMapperDoesNotReturnAnyUselessInfo
specifier|public
name|void
name|testThatDisablingFieldMapperDoesNotReturnAnyUselessInfo
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|inversedStoreSetting
init|=
operator|!
name|TimestampFieldMapper
operator|.
name|Defaults
operator|.
name|FIELD_TYPE
operator|.
name|stored
argument_list|()
decl_stmt|;
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|inversedStoreSetting
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
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
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|mapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|is
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"{\"%s\":{}}"
argument_list|,
name|TimestampFieldMapper
operator|.
name|NAME
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|// issue 3174
DECL|method|testThatSerializationWorksCorrectlyForIndexField
specifier|public
name|void
name|testThatSerializationWorksCorrectlyForIndexField
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|enabledMapping
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
literal|"_timestamp"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
literal|true
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|"yes"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
literal|"no"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|enabledMapper
init|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|enabledMapping
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|JsonXContent
operator|.
name|contentBuilder
argument_list|()
operator|.
name|startObject
argument_list|()
decl_stmt|;
name|enabledMapper
operator|.
name|timestampFieldMapper
argument_list|()
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|ToXContent
operator|.
name|EMPTY_PARAMS
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|close
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|serializedMap
init|=
name|JsonXContent
operator|.
name|jsonXContent
operator|.
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|mapAndClose
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|serializedMap
argument_list|,
name|hasKey
argument_list|(
literal|"_timestamp"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|serializedMap
operator|.
name|get
argument_list|(
literal|"_timestamp"
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|Map
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|timestampConfiguration
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|serializedMap
operator|.
name|get
argument_list|(
literal|"_timestamp"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|timestampConfiguration
argument_list|,
name|hasKey
argument_list|(
literal|"store"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|timestampConfiguration
operator|.
name|get
argument_list|(
literal|"store"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|timestampConfiguration
argument_list|,
name|hasKey
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|timestampConfiguration
operator|.
name|get
argument_list|(
literal|"index"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"no"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

