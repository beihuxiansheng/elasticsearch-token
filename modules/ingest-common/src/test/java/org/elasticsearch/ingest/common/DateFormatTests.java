begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.ingest.common
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|common
package|;
end_package

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
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormatter
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
name|function
operator|.
name|Function
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsEqual
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|DateFormatTests
specifier|public
class|class
name|DateFormatTests
extends|extends
name|ESTestCase
block|{
DECL|method|testParseJoda
specifier|public
name|void
name|testParseJoda
parameter_list|()
block|{
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|jodaFunction
init|=
name|DateFormat
operator|.
name|Joda
operator|.
name|getFunction
argument_list|(
literal|"MMM dd HH:mm:ss Z"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|8
argument_list|)
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|Instant
operator|.
name|ofEpochMilli
argument_list|(
name|jodaFunction
operator|.
name|apply
argument_list|(
literal|"Nov 24 01:29:01 -0800"
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|)
operator|.
name|atZone
argument_list|(
name|ZoneId
operator|.
name|of
argument_list|(
literal|"GMT-8"
argument_list|)
argument_list|)
operator|.
name|format
argument_list|(
name|DateTimeFormatter
operator|.
name|ofPattern
argument_list|(
literal|"MM dd HH:mm:ss"
argument_list|,
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"11 24 01:29:01"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseUnixMs
specifier|public
name|void
name|testParseUnixMs
parameter_list|()
block|{
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|UnixMs
operator|.
name|getFunction
argument_list|(
literal|null
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|,
literal|null
argument_list|)
operator|.
name|apply
argument_list|(
literal|"1000500"
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1000500L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseUnix
specifier|public
name|void
name|testParseUnix
parameter_list|()
block|{
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|Unix
operator|.
name|getFunction
argument_list|(
literal|null
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|,
literal|null
argument_list|)
operator|.
name|apply
argument_list|(
literal|"1000.5"
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1000500L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseISO8601
specifier|public
name|void
name|testParseISO8601
parameter_list|()
block|{
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|Iso8601
operator|.
name|getFunction
argument_list|(
literal|null
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|,
literal|null
argument_list|)
operator|.
name|apply
argument_list|(
literal|"2001-01-01T00:00:00-0800"
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|978336000000L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseISO8601Failure
specifier|public
name|void
name|testParseISO8601Failure
parameter_list|()
block|{
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|function
init|=
name|DateFormat
operator|.
name|Iso8601
operator|.
name|getFunction
argument_list|(
literal|null
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|function
operator|.
name|apply
argument_list|(
literal|"2001-01-0:00-0800"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"parse should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|//all good
block|}
block|}
DECL|method|testTAI64NParse
specifier|public
name|void
name|testTAI64NParse
parameter_list|()
block|{
name|String
name|input
init|=
literal|"4000000050d506482dbdf024"
decl_stmt|;
name|String
name|expected
init|=
literal|"2012-12-22T03:00:46.767+02:00"
decl_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|Tai64n
operator|.
name|getFunction
argument_list|(
literal|null
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
literal|2
argument_list|)
argument_list|,
literal|null
argument_list|)
operator|.
name|apply
argument_list|(
operator|(
name|randomBoolean
argument_list|()
condition|?
literal|"@"
else|:
literal|""
operator|)
operator|+
name|input
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFromString
specifier|public
name|void
name|testFromString
parameter_list|()
block|{
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"UNIX_MS"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|UnixMs
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"unix_ms"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Joda
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"UNIX"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Unix
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"unix"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Joda
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"ISO8601"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Iso8601
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"iso8601"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Joda
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"TAI64N"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Tai64n
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"tai64n"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Joda
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|DateFormat
operator|.
name|fromString
argument_list|(
literal|"prefix-"
operator|+
name|randomAlphaOfLengthBetween
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|DateFormat
operator|.
name|Joda
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

