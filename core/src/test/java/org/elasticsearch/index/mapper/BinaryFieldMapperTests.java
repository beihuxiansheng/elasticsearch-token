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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

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
name|compress
operator|.
name|CompressorFactory
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|Arrays
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

begin_class
DECL|class|BinaryFieldMapperTests
specifier|public
class|class
name|BinaryFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
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
DECL|method|testDefaultMapping
specifier|public
name|void
name|testDefaultMapping
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
literal|"binary"
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
name|mapper
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
name|FieldMapper
name|fieldMapper
init|=
name|mapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
argument_list|,
name|instanceOf
argument_list|(
name|BinaryFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testStoredValue
specifier|public
name|void
name|testStoredValue
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
literal|"field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"binary"
argument_list|)
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
literal|true
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
name|mapper
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
comment|// case 1: a simple binary value
specifier|final
name|byte
index|[]
name|binaryValue1
init|=
operator|new
name|byte
index|[
literal|100
index|]
decl_stmt|;
name|binaryValue1
index|[
literal|56
index|]
operator|=
literal|1
expr_stmt|;
comment|// case 2: a value that looks compressed: this used to fail in 1.x
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
try|try
init|(
name|StreamOutput
name|compressed
init|=
name|CompressorFactory
operator|.
name|COMPRESSOR
operator|.
name|streamOutput
argument_list|(
name|out
argument_list|)
init|)
block|{
operator|new
name|BytesArray
argument_list|(
name|binaryValue1
argument_list|)
operator|.
name|writeTo
argument_list|(
name|compressed
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|binaryValue2
init|=
name|BytesReference
operator|.
name|toBytes
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|CompressorFactory
operator|.
name|isCompressed
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|binaryValue2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|byte
index|[]
name|value
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|binaryValue1
argument_list|,
name|binaryValue2
argument_list|)
control|)
block|{
name|ParsedDocument
name|doc
init|=
name|mapper
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
literal|"id"
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
name|value
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|,
name|XContentType
operator|.
name|JSON
argument_list|)
argument_list|)
decl_stmt|;
name|BytesRef
name|indexedValue
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getBinaryValue
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|,
name|indexedValue
argument_list|)
expr_stmt|;
name|FieldMapper
name|fieldMapper
init|=
name|mapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|Object
name|originalValue
init|=
name|fieldMapper
operator|.
name|fieldType
argument_list|()
operator|.
name|valueForDisplay
argument_list|(
name|indexedValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|value
argument_list|)
argument_list|,
name|originalValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testEmptyName
specifier|public
name|void
name|testEmptyName
parameter_list|()
throws|throws
name|IOException
block|{
comment|// after 5.x
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
literal|""
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"binary"
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
block|}
end_class

end_unit

