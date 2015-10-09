begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.builder
package|package
name|org
operator|.
name|elasticsearch
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
name|geo
operator|.
name|GeoPoint
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
name|io
operator|.
name|PathUtils
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
name|XContentBuilderString
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
name|XContentParser
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
name|test
operator|.
name|ESTestCase
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|XContentBuilderTests
specifier|public
class|class
name|XContentBuilderTests
extends|extends
name|ESTestCase
block|{
annotation|@
name|Test
DECL|method|testPrettyWithLfAtEnd
specifier|public
name|void
name|testPrettyWithLfAtEnd
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
name|usePrettyPrint
argument_list|()
expr_stmt|;
name|generator
operator|.
name|usePrintLineFeedAtEnd
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
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// double close, and check there is no error...
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|writer
operator|.
name|unsafeCharArray
argument_list|()
index|[
name|writer
operator|.
name|size
argument_list|()
operator|-
literal|1
index|]
argument_list|,
name|equalTo
argument_list|(
literal|'\n'
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
DECL|method|testRaw
specifier|public
name|void
name|testRaw
parameter_list|()
throws|throws
name|IOException
block|{
block|{
name|XContentBuilder
name|xContentBuilder
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
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"foo\":{\"test\":\"value\"}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|XContentBuilder
name|xContentBuilder
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
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo1"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"foo\":{\"test\":\"value\"},\"foo1\":{\"test\":\"value\"}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|XContentBuilder
name|xContentBuilder
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
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"test\":\"value\",\"foo\":{\"test\":\"value\"}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|XContentBuilder
name|xContentBuilder
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
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"test\":\"value\",\"foo\":{\"test\":\"value\"},\"test1\":\"value1\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|{
name|XContentBuilder
name|xContentBuilder
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
name|xContentBuilder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"test"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|rawField
argument_list|(
literal|"foo1"
argument_list|,
operator|new
name|BytesArray
argument_list|(
literal|"{\"test\":\"value\"}"
argument_list|)
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|field
argument_list|(
literal|"test1"
argument_list|,
literal|"value1"
argument_list|)
expr_stmt|;
name|xContentBuilder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|xContentBuilder
operator|.
name|bytes
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"test\":\"value\",\"foo\":{\"test\":\"value\"},\"foo1\":{\"test\":\"value\"},\"test1\":\"value1\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|Arrays
operator|.
name|asList
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
name|BytesStreamOutput
name|bos
init|=
operator|new
name|BytesStreamOutput
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
DECL|method|testByteConversion
specifier|public
name|void
name|testByteConversion
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
literal|"test_name"
argument_list|,
call|(
name|Byte
call|)
argument_list|(
name|byte
argument_list|)
literal|120
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|toUtf8
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"{\"test_name\":120}"
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
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"UTC"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ROOT
argument_list|)
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
argument_list|<>
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
argument_list|<>
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
annotation|@
name|Test
DECL|method|testCopyCurrentStructure
specifier|public
name|void
name|testCopyCurrentStructure
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
literal|"test field"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"filter"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"terms"
argument_list|)
expr_stmt|;
comment|// up to 20k random terms
name|int
name|numTerms
init|=
name|randomInt
argument_list|(
literal|20000
argument_list|)
operator|+
literal|1
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|terms
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numTerms
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numTerms
condition|;
name|i
operator|++
control|)
block|{
name|terms
operator|.
name|add
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"fakefield"
argument_list|,
name|terms
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
expr_stmt|;
name|XContentParser
name|parser
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
name|createParser
argument_list|(
name|builder
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|XContentBuilder
name|filterBuilder
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"test"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|assertThat
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test field"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"filter"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|filterBuilder
operator|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|parser
operator|.
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
name|filterBuilder
operator|.
name|copyCurrentStructure
argument_list|(
name|parser
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|assertNotNull
argument_list|(
name|filterBuilder
argument_list|)
expr_stmt|;
name|parser
operator|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|filterBuilder
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"terms"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"fakefield"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|parser
operator|.
name|nextToken
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|assertThat
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|terms
operator|.
name|get
argument_list|(
name|i
operator|++
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|i
argument_list|,
name|equalTo
argument_list|(
name|terms
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHandlingOfPath
specifier|public
name|void
name|testHandlingOfPath
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|PathUtils
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|checkPathSerialization
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHandlingOfPath_relative
specifier|public
name|void
name|testHandlingOfPath_relative
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|PathUtils
operator|.
name|get
argument_list|(
literal|".."
argument_list|,
literal|".."
argument_list|,
literal|"path"
argument_list|)
decl_stmt|;
name|checkPathSerialization
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHandlingOfPath_absolute
specifier|public
name|void
name|testHandlingOfPath_absolute
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|createTempDir
argument_list|()
operator|.
name|toAbsolutePath
argument_list|()
decl_stmt|;
name|checkPathSerialization
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPathSerialization
specifier|private
name|void
name|checkPathSerialization
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentBuilder
name|pathBuilder
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
name|pathBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"file"
argument_list|,
name|path
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentBuilder
name|stringBuilder
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
name|stringBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"file"
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|pathBuilder
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|stringBuilder
operator|.
name|string
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHandlingOfPath_XContentBuilderStringName
specifier|public
name|void
name|testHandlingOfPath_XContentBuilderStringName
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|PathUtils
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|XContentBuilderString
name|name
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|XContentBuilder
name|pathBuilder
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
name|pathBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|path
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentBuilder
name|stringBuilder
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
name|stringBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
name|name
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|pathBuilder
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|stringBuilder
operator|.
name|string
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHandlingOfCollectionOfPaths
specifier|public
name|void
name|testHandlingOfCollectionOfPaths
parameter_list|()
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|PathUtils
operator|.
name|get
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
name|XContentBuilder
name|pathBuilder
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
name|pathBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"file"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|path
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|XContentBuilder
name|stringBuilder
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
name|stringBuilder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"file"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|pathBuilder
operator|.
name|string
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|stringBuilder
operator|.
name|string
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIndentIsPlatformIndependent
specifier|public
name|void
name|testIndentIsPlatformIndependent
parameter_list|()
throws|throws
name|IOException
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
name|prettyPrint
argument_list|()
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
literal|"foo"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foobar"
argument_list|,
literal|"boom"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|String
name|string
init|=
name|builder
operator|.
name|string
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  \"test\" : \"foo\",\n"
operator|+
literal|"  \"foo\" : {\n"
operator|+
literal|"    \"foobar\" : \"boom\"\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
name|string
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
name|YAML
argument_list|)
operator|.
name|prettyPrint
argument_list|()
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
literal|"foo"
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|field
argument_list|(
literal|"foobar"
argument_list|,
literal|"boom"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|string
operator|=
name|builder
operator|.
name|string
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"---\n"
operator|+
literal|"test: \"foo\"\n"
operator|+
literal|"foo:\n"
operator|+
literal|"  foobar: \"boom\"\n"
argument_list|,
name|string
argument_list|)
expr_stmt|;
block|}
DECL|method|testRenderGeoPoint
specifier|public
name|void
name|testRenderGeoPoint
parameter_list|()
throws|throws
name|IOException
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
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
operator|.
name|field
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|value
argument_list|(
operator|new
name|GeoPoint
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|String
name|string
init|=
name|builder
operator|.
name|string
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  \"foo\" : {\n"
operator|+
literal|"    \"lat\" : 1.0,\n"
operator|+
literal|"    \"lon\" : 2.0\n"
operator|+
literal|"  }\n"
operator|+
literal|"}"
argument_list|,
name|string
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

