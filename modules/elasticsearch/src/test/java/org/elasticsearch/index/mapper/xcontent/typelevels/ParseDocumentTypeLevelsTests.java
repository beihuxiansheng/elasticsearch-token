begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent.typelevels
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
operator|.
name|typelevels
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
name|xcontent
operator|.
name|XContentDocumentMapper
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
name|xcontent
operator|.
name|XContentMapperTests
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
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
name|*
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ParseDocumentTypeLevelsTests
specifier|public
class|class
name|ParseDocumentTypeLevelsTests
block|{
DECL|method|testNoLevel
annotation|@
name|Test
specifier|public
name|void
name|testNoLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeLevel
annotation|@
name|Test
specifier|public
name|void
name|testTypeLevel
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
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
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoLevelWithFieldTypeAsValue
annotation|@
name|Test
specifier|public
name|void
name|testNoLevelWithFieldTypeAsValue
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
literal|"type"
argument_list|,
literal|"value_type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value_type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeLevelWithFieldTypeAsValue
annotation|@
name|Test
specifier|public
name|void
name|testTypeLevelWithFieldTypeAsValue
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"value_type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
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
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value_type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoLevelWithFieldTypeAsObject
annotation|@
name|Test
specifier|public
name|void
name|testNoLevelWithFieldTypeAsObject
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type_field"
argument_list|,
literal|"type_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// in this case, we analyze the type object as the actual document, and ignore the other same level fields
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"type_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeLevelWithFieldTypeAsObject
annotation|@
name|Test
specifier|public
name|void
name|testTypeLevelWithFieldTypeAsObject
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type_field"
argument_list|,
literal|"type_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
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
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type.type_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"type_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoLevelWithFieldTypeAsValueNotFirst
annotation|@
name|Test
specifier|public
name|void
name|testNoLevelWithFieldTypeAsValueNotFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"value_type"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
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
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value_type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeLevelWithFieldTypeAsValueNotFirst
annotation|@
name|Test
specifier|public
name|void
name|testTypeLevelWithFieldTypeAsValueNotFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"value_type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
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
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value_type"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoLevelWithFieldTypeAsObjectNotFirst
annotation|@
name|Test
specifier|public
name|void
name|testNoLevelWithFieldTypeAsObjectNotFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type_field"
argument_list|,
literal|"type_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
operator|.
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// when the type is not the first one, we don't confuse it...
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type.type_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"type_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTypeLevelWithFieldTypeAsObjectNotFirst
annotation|@
name|Test
specifier|public
name|void
name|testTypeLevelWithFieldTypeAsObjectNotFirst
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|defaultMapping
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
name|XContentDocumentMapper
name|defaultMapper
init|=
name|XContentMapperTests
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|defaultMapping
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
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"type"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type_field"
argument_list|,
literal|"type_value"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test2"
argument_list|,
literal|"value2"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"inner"
argument_list|)
operator|.
name|field
argument_list|(
literal|"inner_field"
argument_list|,
literal|"inner_value"
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
name|copiedBytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"type.type_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"type_value"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"value2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|doc
operator|.
name|doc
argument_list|()
operator|.
name|get
argument_list|(
literal|"inner.inner_field"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"inner_value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

