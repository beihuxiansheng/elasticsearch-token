begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|BytesArray
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
name|XContentType
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
name|MapperService
operator|.
name|MergeReason
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
name|ObjectMapper
operator|.
name|Dynamic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|ESSingleNodeTestCase
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
name|InternalSettingsPlugin
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
name|VersionUtils
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
name|Collection
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|getRandom
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
name|containsString
import|;
end_import

begin_class
DECL|class|ObjectMapperTests
specifier|public
class|class
name|ObjectMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|method|testDifferentInnerObjectTokenFailure
specifier|public
name|void
name|testDifferentInnerObjectTokenFailure
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|defaultMapper
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
decl_stmt|;
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|defaultMapper
operator|.
name|parse
argument_list|(
name|SourceToParse
operator|.
name|source
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|" {\n"
operator|+
literal|"      \"object\": {\n"
operator|+
literal|"        \"array\":[\n"
operator|+
literal|"        {\n"
operator|+
literal|"          \"object\": { \"value\": \"value\" }\n"
operator|+
literal|"        },\n"
operator|+
literal|"        {\n"
operator|+
literal|"          \"object\":\"value\"\n"
operator|+
literal|"        }\n"
operator|+
literal|"        ]\n"
operator|+
literal|"      },\n"
operator|+
literal|"      \"value\":\"value\"\n"
operator|+
literal|"    }"
argument_list|)
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"different type"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyArrayProperties
specifier|public
name|void
name|testEmptyArrayProperties
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
name|startArray
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|endArray
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
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyFieldsArrayMultiFields
specifier|public
name|void
name|testEmptyFieldsArrayMultiFields
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
literal|"tweet"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|endArray
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
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"tweet"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldsArrayMultiFieldsShouldThrowException
specifier|public
name|void
name|testFieldsArrayMultiFieldsShouldThrowException
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
literal|"tweet"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
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
try|try
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"tweet"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected MapperParsingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"expected map for property [fields]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"but got a class java.util.ArrayList"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyFieldsArray
specifier|public
name|void
name|testEmptyFieldsArray
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
literal|"tweet"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|endArray
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
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"tweet"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFieldsWithFilledArrayShouldThrowException
specifier|public
name|void
name|testFieldsWithFilledArrayShouldThrowException
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
literal|"tweet"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startArray
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"string"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endArray
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
try|try
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"tweet"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected MapperParsingException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Expected map for property [fields]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFieldPropertiesArray
specifier|public
name|void
name|testFieldPropertiesArray
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
literal|"tweet"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"fields"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"raw"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|"tweet"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMerge
specifier|public
name|void
name|testMerge
parameter_list|()
throws|throws
name|IOException
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
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"keyword"
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
name|MapperService
name|mapperService
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|mapper
operator|.
name|root
argument_list|()
operator|.
name|includeInAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|mapper
operator|.
name|root
argument_list|()
operator|.
name|dynamic
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|update
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
name|field
argument_list|(
literal|"include_in_all"
argument_list|,
literal|false
argument_list|)
operator|.
name|field
argument_list|(
literal|"dynamic"
argument_list|,
literal|"strict"
argument_list|)
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
name|mapper
operator|=
name|mapperService
operator|.
name|merge
argument_list|(
literal|"type"
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|update
argument_list|)
argument_list|,
name|MergeReason
operator|.
name|MAPPING_UPDATE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mapper
operator|.
name|root
argument_list|()
operator|.
name|includeInAll
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Dynamic
operator|.
name|STRICT
argument_list|,
name|mapper
operator|.
name|root
argument_list|()
operator|.
name|dynamic
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testEmptyName
specifier|public
name|void
name|testEmptyName
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
literal|""
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"name"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"text"
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
comment|// Empty name not allowed in index created after 5.0
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
operator|.
name|parse
argument_list|(
literal|""
argument_list|,
operator|new
name|CompressedXContent
argument_list|(
name|mapping
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"name cannot be empty string"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|getPlugins
parameter_list|()
block|{
return|return
name|pluginList
argument_list|(
name|InternalSettingsPlugin
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

