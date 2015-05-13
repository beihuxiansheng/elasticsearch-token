begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.boost
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|boost
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
operator|.
name|Document
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
name|ElasticsearchSingleNodeTest
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
name|closeTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FieldLevelBoostTests
specifier|public
class|class
name|FieldLevelBoostTests
extends|extends
name|ElasticsearchSingleNodeTest
block|{
annotation|@
name|Test
DECL|method|testFieldLevelBoost
specifier|public
name|void
name|testFieldLevelBoost
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
literal|"person"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"str_field"
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
name|startObject
argument_list|(
literal|"int_field"
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
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"byte_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"byte"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"double_field"
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
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"float_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"long_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"short_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"short"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|mapping
argument_list|)
decl_stmt|;
name|BytesReference
name|json
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
literal|"str_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|2.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|"some name"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"int_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|3.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|10
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"byte_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|4.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|20
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|5.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|"2012-01-10"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"double_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|6.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|30.0
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"float_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|7.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|40.0
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"long_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|8.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|50
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"short_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
literal|9.0
argument_list|)
operator|.
name|field
argument_list|(
literal|"value"
argument_list|,
literal|60
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|docMapper
operator|.
name|parse
argument_list|(
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
name|IndexableField
name|f
init|=
name|doc
operator|.
name|getField
argument_list|(
literal|"str_field"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|2.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"int_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|3.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"byte_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|4.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"date_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|5.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"double_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|6.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"float_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|7.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"long_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|8.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|=
name|doc
operator|.
name|getField
argument_list|(
literal|"short_field"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|double
operator|)
name|f
operator|.
name|boost
argument_list|()
argument_list|,
name|closeTo
argument_list|(
literal|9.0
argument_list|,
literal|0.001
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidFieldLevelBoost
specifier|public
name|void
name|testInvalidFieldLevelBoost
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
literal|"person"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"str_field"
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
name|startObject
argument_list|(
literal|"int_field"
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
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"byte_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"byte"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"date"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"double_field"
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
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"float_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"float"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"long_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"long"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|startObject
argument_list|(
literal|"short_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
literal|"short"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"norms"
argument_list|)
operator|.
name|field
argument_list|(
literal|"enabled"
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
name|string
argument_list|()
decl_stmt|;
name|DocumentMapper
name|docMapper
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
name|mapping
argument_list|)
decl_stmt|;
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"str_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"int_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"byte_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"date_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"double_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"float_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"long_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
try|try
block|{
name|docMapper
operator|.
name|parse
argument_list|(
literal|"person"
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
literal|"short_field"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
operator|.
name|rootDoc
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MapperParsingException
name|ex
parameter_list|)
block|{
comment|// Expected
block|}
block|}
block|}
end_class

end_unit

