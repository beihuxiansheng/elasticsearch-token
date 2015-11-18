begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.processor.date
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|processor
operator|.
name|date
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|Data
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
name|hamcrest
operator|.
name|CoreMatchers
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
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|DateProcessorTests
specifier|public
class|class
name|DateProcessorTests
extends|extends
name|ESTestCase
block|{
DECL|method|testJodaPattern
specifier|public
name|void
name|testJodaPattern
parameter_list|()
block|{
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"Europe/Amsterdam"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"date_as_string"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"yyyy dd MM hh:mm:ss"
argument_list|)
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"2010 12 06 11:05:15"
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2010-06-12T11:05:15.000+02:00"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJodaPatternMultipleFormats
specifier|public
name|void
name|testJodaPatternMultipleFormats
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|matchFormats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|matchFormats
operator|.
name|add
argument_list|(
literal|"yyyy dd MM"
argument_list|)
expr_stmt|;
name|matchFormats
operator|.
name|add
argument_list|(
literal|"dd/MM/yyyy"
argument_list|)
expr_stmt|;
name|matchFormats
operator|.
name|add
argument_list|(
literal|"dd-MM-yyyy"
argument_list|)
expr_stmt|;
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"Europe/Amsterdam"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"date_as_string"
argument_list|,
name|matchFormats
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"2010 12 06"
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2010-06-12T00:00:00.000+02:00"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"12/06/2010"
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2010-06-12T00:00:00.000+02:00"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"12-06-2010"
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
expr_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2010-06-12T00:00:00.000+02:00"
argument_list|)
argument_list|)
expr_stmt|;
name|document
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"2010"
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
expr_stmt|;
try|try
block|{
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"processor should have failed due to not supported date format"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
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
literal|"unable to parse date [2010]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testJodaPatternLocale
specifier|public
name|void
name|testJodaPatternLocale
parameter_list|()
block|{
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"Europe/Amsterdam"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ITALIAN
argument_list|,
literal|"date_as_string"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"yyyy dd MMM"
argument_list|)
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"2010 12 giugno"
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2010-06-12T00:00:00.000+02:00"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testJodaPatternDefaultYear
specifier|public
name|void
name|testJodaPatternDefaultYear
parameter_list|()
block|{
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"Europe/Amsterdam"
argument_list|)
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|,
literal|"date_as_string"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"dd/MM"
argument_list|)
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"12/06"
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateTime
operator|.
name|now
argument_list|()
operator|.
name|getYear
argument_list|()
operator|+
literal|"-06-12T00:00:00.000+02:00"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTAI64N
specifier|public
name|void
name|testTAI64N
parameter_list|()
block|{
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
literal|2
argument_list|)
argument_list|,
name|randomLocale
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|"date_as_string"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|DateParserFactory
operator|.
name|TAI64N
argument_list|)
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|dateAsString
init|=
operator|(
name|randomBoolean
argument_list|()
condition|?
literal|"@"
else|:
literal|""
operator|)
operator|+
literal|"4000000050d506482dbdf024"
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
name|dateAsString
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"2012-12-22T03:00:46.767+02:00"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnixMs
specifier|public
name|void
name|testUnixMs
parameter_list|()
block|{
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|,
name|randomLocale
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|"date_as_string"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|DateParserFactory
operator|.
name|UNIX_MS
argument_list|)
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"1000500"
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1970-01-01T00:16:40.500Z"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testUnix
specifier|public
name|void
name|testUnix
parameter_list|()
block|{
name|DateProcessor
name|dateProcessor
init|=
operator|new
name|DateProcessor
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|,
name|randomLocale
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|,
literal|"date_as_string"
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|DateParserFactory
operator|.
name|UNIX
argument_list|)
argument_list|,
literal|"date_as_date"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|document
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|document
operator|.
name|put
argument_list|(
literal|"date_as_string"
argument_list|,
literal|"1000.5"
argument_list|)
expr_stmt|;
name|Data
name|data
init|=
operator|new
name|Data
argument_list|(
literal|"index"
argument_list|,
literal|"type"
argument_list|,
literal|"id"
argument_list|,
name|document
argument_list|)
decl_stmt|;
name|dateProcessor
operator|.
name|execute
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|data
operator|.
name|getPropertyValue
argument_list|(
literal|"date_as_date"
argument_list|,
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1970-01-01T00:16:40.500Z"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

