begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|index
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
name|IndexService
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
name|ParsedDocument
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
name|junit
operator|.
name|Before
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsString
import|;
end_import

begin_class
DECL|class|DateFieldMapperTests
specifier|public
class|class
name|DateFieldMapperTests
extends|extends
name|ESSingleNodeTestCase
block|{
DECL|field|indexService
name|IndexService
name|indexService
decl_stmt|;
DECL|field|parser
name|DocumentMapperParser
name|parser
decl_stmt|;
annotation|@
name|Before
DECL|method|before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|indexService
operator|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|parser
operator|=
name|indexService
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapperParser
argument_list|()
expr_stmt|;
block|}
DECL|method|testDefaults
specifier|public
name|void
name|testDefaults
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
literal|"date"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-11"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|pointField
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|pointDimensionCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|pointNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1457654400000L
argument_list|,
name|pointField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|IndexableField
name|dvField
init|=
name|fields
index|[
literal|1
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|dvField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1457654400000L
argument_list|,
name|dvField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dvField
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNotIndexed
specifier|public
name|void
name|testNotIndexed
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"index"
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-11"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|dvField
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|dvField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoDocValues
specifier|public
name|void
name|testNoDocValues
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"doc_values"
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|mapper
init|=
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-11"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|pointField
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|pointDimensionCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testStore
specifier|public
name|void
name|testStore
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
literal|"date"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-11"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|pointField
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|pointDimensionCount
argument_list|()
argument_list|)
expr_stmt|;
name|IndexableField
name|dvField
init|=
name|fields
index|[
literal|1
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|dvField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|IndexableField
name|storedField
init|=
name|fields
index|[
literal|2
index|]
decl_stmt|;
name|assertTrue
argument_list|(
name|storedField
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1457654400000L
argument_list|,
name|storedField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testIgnoreMalformed
specifier|public
name|void
name|testIgnoreMalformed
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
literal|"date"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ThrowingRunnable
name|runnable
init|=
parameter_list|()
lambda|->
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-99"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|MapperParsingException
name|e
init|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
name|runnable
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"Cannot parse \"2016-03-99\""
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"ignore_malformed"
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
expr_stmt|;
name|DocumentMapper
name|mapper2
init|=
name|parser
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
name|ParsedDocument
name|doc
init|=
name|mapper2
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|":1"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testIncludeInAll
specifier|public
name|void
name|testIncludeInAll
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
literal|"date"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-11"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"_all"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2016-03-11"
argument_list|,
name|fields
index|[
literal|0
index|]
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"include_in_all"
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
name|endObject
argument_list|()
operator|.
name|string
argument_list|()
expr_stmt|;
name|mapper
operator|=
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|"2016-03-11"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|fields
operator|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"_all"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangeFormat
specifier|public
name|void
name|testChangeFormat
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"epoch_second"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|1457654400
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|pointField
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1457654400000L
argument_list|,
name|pointField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testChangeLocale
specifier|public
name|void
name|testChangeLocale
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"locale"
argument_list|,
literal|"fr"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
literal|1457654400
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullValue
specifier|public
name|void
name|testNullValue
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
literal|"date"
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
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|ParsedDocument
name|doc
init|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
name|nullField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|new
name|IndexableField
index|[
literal|0
index|]
argument_list|,
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"null_value"
argument_list|,
literal|"2016-03-11"
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
name|mapper
operator|=
name|parser
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
name|assertEquals
argument_list|(
name|mapping
argument_list|,
name|mapper
operator|.
name|mappingSource
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|=
name|mapper
operator|.
name|parse
argument_list|(
literal|"test"
argument_list|,
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
name|nullField
argument_list|(
literal|"field"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|IndexableField
index|[]
name|fields
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getFields
argument_list|(
literal|"field"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|fields
operator|.
name|length
argument_list|)
expr_stmt|;
name|IndexableField
name|pointField
init|=
name|fields
index|[
literal|0
index|]
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|pointDimensionCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|8
argument_list|,
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|pointNumBytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pointField
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1457654400000L
argument_list|,
name|pointField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|IndexableField
name|dvField
init|=
name|fields
index|[
literal|1
index|]
decl_stmt|;
name|assertEquals
argument_list|(
name|DocValuesType
operator|.
name|SORTED_NUMERIC
argument_list|,
name|dvField
operator|.
name|fieldType
argument_list|()
operator|.
name|docValuesType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1457654400000L
argument_list|,
name|dvField
operator|.
name|numericValue
argument_list|()
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|dvField
operator|.
name|fieldType
argument_list|()
operator|.
name|stored
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testNullConfigValuesFail
specifier|public
name|void
name|testNullConfigValuesFail
parameter_list|()
throws|throws
name|MapperParsingException
throws|,
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
literal|"date"
argument_list|)
operator|.
name|field
argument_list|(
literal|"format"
argument_list|,
operator|(
name|String
operator|)
literal|null
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
name|Exception
name|e
init|=
name|expectThrows
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
name|parser
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
name|assertEquals
argument_list|(
literal|"[format] must not have a [null] value"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

