begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.index.mapper.date
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|index
operator|.
name|mapper
operator|.
name|date
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|settingsBuilder
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|notNullValue
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
name|nullValue
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
name|ArrayList
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|NumericTokenStream
operator|.
name|NumericTermAttribute
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
name|analysis
operator|.
name|TokenStream
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
name|search
operator|.
name|Filter
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
name|search
operator|.
name|NumericRangeFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|unit
operator|.
name|TimeValue
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
name|FieldMapper
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
name|index
operator|.
name|mapper
operator|.
name|core
operator|.
name|DateFieldMapper
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
name|LongFieldMapper
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
name|unit
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
name|joda
operator|.
name|time
operator|.
name|DateTime
import|;
end_import

begin_import
import|import
name|org
operator|.
name|joda
operator|.
name|time
operator|.
name|DateTimeZone
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

begin_class
DECL|class|SimpleDateMappingTests
specifier|public
class|class
name|SimpleDateMappingTests
block|{
annotation|@
name|Test
DECL|method|testAutomaticDateParser
specifier|public
name|void
name|testAutomaticDateParser
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
name|mapper
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
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
literal|"date_field1"
argument_list|,
literal|"2011/01/22"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date_field2"
argument_list|,
literal|"2011/01/22 00:00:00"
argument_list|)
operator|.
name|field
argument_list|(
literal|"wrong_date1"
argument_list|,
literal|"-4"
argument_list|)
operator|.
name|field
argument_list|(
literal|"wrong_date2"
argument_list|,
literal|"2012/2"
argument_list|)
operator|.
name|field
argument_list|(
literal|"wrong_date3"
argument_list|,
literal|"2012/test"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|fieldMapper
init|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"date_field1"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
argument_list|,
name|instanceOf
argument_list|(
name|DateFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|fieldMapper
operator|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"date_field2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
argument_list|,
name|instanceOf
argument_list|(
name|DateFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|fieldMapper
operator|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"wrong_date1"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
argument_list|,
name|instanceOf
argument_list|(
name|StringFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|fieldMapper
operator|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"wrong_date2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
argument_list|,
name|instanceOf
argument_list|(
name|StringFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|fieldMapper
operator|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"wrong_date3"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldMapper
argument_list|,
name|instanceOf
argument_list|(
name|StringFieldMapper
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testParseLocal
specifier|public
name|void
name|testParseLocal
parameter_list|()
block|{
name|assertThat
argument_list|(
name|Locale
operator|.
name|GERMAN
argument_list|,
name|equalTo
argument_list|(
name|DateFieldMapper
operator|.
name|parseLocale
argument_list|(
literal|"de"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Locale
operator|.
name|GERMANY
argument_list|,
name|equalTo
argument_list|(
name|DateFieldMapper
operator|.
name|parseLocale
argument_list|(
literal|"de_DE"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|Locale
argument_list|(
literal|"de"
argument_list|,
literal|"DE"
argument_list|,
literal|"DE"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFieldMapper
operator|.
name|parseLocale
argument_list|(
literal|"de_DE_DE"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|DateFieldMapper
operator|.
name|parseLocale
argument_list|(
literal|"de_DE_DE_DE"
argument_list|)
expr_stmt|;
assert|assert
literal|false
assert|;
block|}
catch|catch
parameter_list|(
name|ElasticSearchIllegalArgumentException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|assertThat
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|equalTo
argument_list|(
name|DateFieldMapper
operator|.
name|parseLocale
argument_list|(
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
name|equalTo
argument_list|(
name|DateFieldMapper
operator|.
name|parseLocale
argument_list|(
literal|"ROOT"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLocale
specifier|public
name|void
name|testLocale
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
literal|"date_field_default"
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
literal|"E, d MMM yyyy HH:mm:ss Z"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field_en"
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
literal|"E, d MMM yyyy HH:mm:ss Z"
argument_list|)
operator|.
name|field
argument_list|(
literal|"locale"
argument_list|,
literal|"EN"
argument_list|)
operator|.
name|endObject
argument_list|()
operator|.
name|startObject
argument_list|(
literal|"date_field_de"
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
literal|"E, d MMM yyyy HH:mm:ss Z"
argument_list|)
operator|.
name|field
argument_list|(
literal|"locale"
argument_list|,
literal|"DE_de"
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
name|mapper
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
literal|"date_field_en"
argument_list|,
literal|"Wed, 06 Dec 2000 02:55:00 -0800"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date_field_de"
argument_list|,
literal|"Mi, 06 Dez 2000 02:55:00 -0800"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date_field_default"
argument_list|,
literal|"Wed, 06 Dec 2000 02:55:00 -0800"
argument_list|)
comment|// check default - no exception is a successs!
operator|.
name|endObject
argument_list|()
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|assertNumericTokensEqual
argument_list|(
name|doc
argument_list|,
name|defaultMapper
argument_list|,
literal|"date_field_en"
argument_list|,
literal|"date_field_de"
argument_list|)
expr_stmt|;
name|assertNumericTokensEqual
argument_list|(
name|doc
argument_list|,
name|defaultMapper
argument_list|,
literal|"date_field_en"
argument_list|,
literal|"date_field_default"
argument_list|)
expr_stmt|;
block|}
DECL|method|mapper
specifier|private
name|DocumentMapper
name|mapper
parameter_list|(
name|String
name|mapping
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we serialize and deserialize the mapping to make sure serialization works just fine
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
expr_stmt|;
name|defaultMapper
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
name|String
name|rebuildMapping
init|=
name|builder
operator|.
name|string
argument_list|()
decl_stmt|;
return|return
name|MapperTestUtils
operator|.
name|newParser
argument_list|()
operator|.
name|parse
argument_list|(
name|rebuildMapping
argument_list|)
return|;
block|}
DECL|method|assertNumericTokensEqual
specifier|private
name|void
name|assertNumericTokensEqual
parameter_list|(
name|ParsedDocument
name|doc
parameter_list|,
name|DocumentMapper
name|defaultMapper
parameter_list|,
name|String
name|fieldA
parameter_list|,
name|String
name|fieldB
parameter_list|)
throws|throws
name|IOException
block|{
name|assertThat
argument_list|(
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldA
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|defaultMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
argument_list|,
name|notNullValue
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
name|fieldB
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|defaultMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|TokenStream
name|tokenStream
init|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldA
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|defaultMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
decl_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|NumericTermAttribute
name|nta
init|=
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Long
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|values
operator|.
name|add
argument_list|(
name|nta
operator|.
name|getRawValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tokenStream
operator|=
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
name|fieldB
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|defaultMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|tokenStream
operator|.
name|reset
argument_list|()
expr_stmt|;
name|nta
operator|=
name|tokenStream
operator|.
name|addAttribute
argument_list|(
name|NumericTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tokenStream
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|nta
operator|.
name|getRawValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|pos
argument_list|,
name|equalTo
argument_list|(
name|values
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimestampAsDate
specifier|public
name|void
name|testTimestampAsDate
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
name|mapper
argument_list|(
name|mapping
argument_list|)
decl_stmt|;
name|long
name|value
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
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
literal|"date_field"
argument_list|,
name|value
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
literal|"date_field"
argument_list|)
operator|.
name|tokenStream
argument_list|(
name|defaultMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDateDetection
specifier|public
name|void
name|testDateDetection
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
name|field
argument_list|(
literal|"date_detection"
argument_list|,
literal|false
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
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
name|mapper
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
literal|"date_field"
argument_list|,
literal|"2010-01-01"
argument_list|)
operator|.
name|field
argument_list|(
literal|"date_field_x"
argument_list|,
literal|"2010-01-01"
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
name|get
argument_list|(
literal|"date_field"
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
name|rootDoc
argument_list|()
operator|.
name|get
argument_list|(
literal|"date_field_x"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2010-01-01"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHourFormat
specifier|public
name|void
name|testHourFormat
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
name|field
argument_list|(
literal|"date_detection"
argument_list|,
literal|false
argument_list|)
operator|.
name|startObject
argument_list|(
literal|"properties"
argument_list|)
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
name|field
argument_list|(
literal|"format"
argument_list|,
literal|"HH:mm:ss"
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
name|mapper
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
literal|"date_field"
argument_list|,
literal|"10:00:00"
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
operator|(
operator|(
name|LongFieldMapper
operator|.
name|CustomLongNumericField
operator|)
name|doc
operator|.
name|rootDoc
argument_list|()
operator|.
name|getField
argument_list|(
literal|"date_field"
argument_list|)
operator|)
operator|.
name|numericAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
operator|new
name|DateTime
argument_list|(
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|10
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|Filter
name|filter
init|=
name|defaultMapper
operator|.
name|mappers
argument_list|()
operator|.
name|smartNameFieldMapper
argument_list|(
literal|"date_field"
argument_list|)
operator|.
name|rangeFilter
argument_list|(
literal|"10:00:00"
argument_list|,
literal|"11:00:00"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|filter
argument_list|,
name|instanceOf
argument_list|(
name|NumericRangeFilter
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|NumericRangeFilter
argument_list|<
name|Long
argument_list|>
name|rangeFilter
init|=
operator|(
name|NumericRangeFilter
argument_list|<
name|Long
argument_list|>
operator|)
name|filter
decl_stmt|;
name|assertThat
argument_list|(
name|rangeFilter
operator|.
name|getMax
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|DateTime
argument_list|(
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|11
argument_list|)
operator|.
name|millis
argument_list|()
operator|+
literal|999
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// +999 to include the 00-01 minute
name|assertThat
argument_list|(
name|rangeFilter
operator|.
name|getMin
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|new
name|DateTime
argument_list|(
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|10
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIgnoreMalformedOption
specifier|public
name|void
name|testIgnoreMalformedOption
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
name|startObject
argument_list|(
literal|"field2"
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
literal|false
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
name|defaultMapper
init|=
name|mapper
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
literal|"a"
argument_list|)
operator|.
name|field
argument_list|(
literal|"field2"
argument_list|,
literal|"2010-01-01"
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
argument_list|,
name|nullValue
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
literal|"field2"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
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
literal|"field2"
argument_list|,
literal|"a"
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
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Verify that the default is false
try|try
block|{
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
literal|"field3"
argument_list|,
literal|"a"
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
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Unless the global ignore_malformed option is set to true
name|Settings
name|indexSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.mapping.ignore_malformed"
argument_list|,
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|defaultMapper
operator|=
name|MapperTestUtils
operator|.
name|newParser
argument_list|(
name|indexSettings
argument_list|)
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
literal|"field3"
argument_list|,
literal|"a"
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
literal|"field3"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// This should still throw an exception, since field2 is specifically set to ignore_malformed=false
try|try
block|{
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
literal|"field2"
argument_list|,
literal|"a"
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
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MapperParsingException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

