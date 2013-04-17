begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.common.xcontent.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|common
operator|.
name|xcontent
operator|.
name|builder
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|FastByteArrayOutputStream
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
name|FastCharArrayWriter
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
name|XContentGenerator
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
name|testng
operator|.
name|annotations
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
name|*
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
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|CAMELCASE
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
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|UNDERSCORE
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
name|assertThat
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

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|Test
DECL|class|XContentBuilderTests
specifier|public
class|class
name|XContentBuilderTests
block|{
annotation|@
name|Test
DECL|method|verifyReuseJsonGenerator
specifier|public
name|void
name|verifyReuseJsonGenerator
parameter_list|()
throws|throws
name|Exception
block|{
name|FastCharArrayWriter
name|writer
init|=
operator|new
name|FastCharArrayWriter
argument_list|()
decl_stmt|;
name|XContentGenerator
name|generator
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createGenerator
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|flush
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|toStringTrim
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
comment|// try again...
name|writer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeStringField
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|flush
argument_list|()
expr_stmt|;
comment|// we get a space at the start here since it thinks we are not in the root object (fine, we will ignore it in the real code we use)
name|assertThat
argument_list|(
name|writer
operator|.
name|toStringTrim
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSimpleGenerator
specifier|public
name|void
name|testSimpleGenerator
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOverloadedList
specifier|public
name|void
name|testOverloadedList
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"1"
argument_list|,
literal|"2"
argument_list|)
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"test\":[\"1\",\"2\"]}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWritingBinaryToStream
specifier|public
name|void
name|testWritingBinaryToStream
parameter_list|()
throws|throws
name|Exception
block|{
name|FastByteArrayOutputStream
name|bos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
name|XContentGenerator
name|gen
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createGenerator
argument_list|(
name|bos
argument_list|)
decl_stmt|;
name|gen
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|writeStringField
argument_list|(
literal|"name"
argument_list|,
literal|"something"
argument_list|)
expr_stmt|;
name|gen
operator|.
name|flush
argument_list|()
expr_stmt|;
name|bos
operator|.
name|write
argument_list|(
literal|", source : { test : \"value\" }"
operator|.
name|getBytes
argument_list|(
literal|"UTF8"
argument_list|)
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeStringField
argument_list|(
literal|"name2"
argument_list|,
literal|"something2"
argument_list|)
expr_stmt|;
name|gen
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
name|gen
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|bos
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
decl_stmt|;
name|String
name|sData
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
literal|"UTF8"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DATA: "
operator|+
name|sData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFieldCaseConversion
specifier|public
name|void
name|testFieldCaseConversion
parameter_list|()
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|fieldCaseConversion
argument_list|(
name|CAMELCASE
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"test_name"
argument_list|,
literal|"value"
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"testName\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|fieldCaseConversion
argument_list|(
name|UNDERSCORE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"testName"
argument_list|,
literal|"value"
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"test_name\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDateTypesConversion
specifier|public
name|void
name|testDateTypesConversion
parameter_list|()
throws|throws
name|Exception
block|{
name|Date
name|date
init|=
operator|new
name|Date
argument_list|()
decl_stmt|;
name|String
name|expectedDate
init|=
name|XContentBuilder
operator|.
name|defaultDatePrinter
operator|.
name|print
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|Calendar
name|calendar
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|String
name|expectedCalendar
init|=
name|XContentBuilder
operator|.
name|defaultDatePrinter
operator|.
name|print
argument_list|(
name|calendar
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
decl_stmt|;
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"date\":\""
operator|+
name|expectedDate
operator|+
literal|"\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"calendar"
argument_list|,
name|calendar
argument_list|)
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
name|equalTo
argument_list|(
literal|"{\"calendar\":\""
operator|+
name|expectedCalendar
operator|+
literal|"\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"date\":\""
operator|+
name|expectedDate
operator|+
literal|"\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
expr_stmt|;
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"calendar"
argument_list|,
name|calendar
argument_list|)
expr_stmt|;
name|builder
operator|.
name|map
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"calendar\":\""
operator|+
name|expectedCalendar
operator|+
literal|"\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

