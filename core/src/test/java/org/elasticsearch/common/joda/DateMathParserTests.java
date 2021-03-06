begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.joda
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|joda
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|DateTimeZone
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|LongSupplier
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

begin_class
DECL|class|DateMathParserTests
specifier|public
class|class
name|DateMathParserTests
extends|extends
name|ESTestCase
block|{
DECL|field|formatter
name|FormatDateTimeFormatter
name|formatter
init|=
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"dateOptionalTime||epoch_millis"
argument_list|)
decl_stmt|;
DECL|field|parser
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|formatter
argument_list|)
decl_stmt|;
DECL|method|assertDateMathEquals
name|void
name|assertDateMathEquals
parameter_list|(
name|String
name|toTest
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertDateMathEquals
argument_list|(
name|toTest
argument_list|,
name|expected
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDateMathEquals
name|void
name|assertDateMathEquals
parameter_list|(
name|String
name|toTest
parameter_list|,
name|String
name|expected
parameter_list|,
specifier|final
name|long
name|now
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|DateTimeZone
name|timeZone
parameter_list|)
block|{
name|long
name|gotMillis
init|=
name|parser
operator|.
name|parse
argument_list|(
name|toTest
argument_list|,
parameter_list|()
lambda|->
name|now
argument_list|,
name|roundUp
argument_list|,
name|timeZone
argument_list|)
decl_stmt|;
name|assertDateEquals
argument_list|(
name|gotMillis
argument_list|,
name|toTest
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
DECL|method|assertDateEquals
name|void
name|assertDateEquals
parameter_list|(
name|long
name|gotMillis
parameter_list|,
name|String
name|original
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|long
name|expectedMillis
init|=
name|parser
operator|.
name|parse
argument_list|(
name|expected
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|gotMillis
operator|!=
name|expectedMillis
condition|)
block|{
name|fail
argument_list|(
literal|"Date math not equal\n"
operator|+
literal|"Original              : "
operator|+
name|original
operator|+
literal|"\n"
operator|+
literal|"Parsed                : "
operator|+
name|formatter
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|gotMillis
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"Expected              : "
operator|+
name|expected
operator|+
literal|"\n"
operator|+
literal|"Expected milliseconds : "
operator|+
name|expectedMillis
operator|+
literal|"\n"
operator|+
literal|"Actual milliseconds   : "
operator|+
name|gotMillis
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testBasicDates
specifier|public
name|void
name|testBasicDates
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014"
argument_list|,
literal|"2014-01-01T00:00:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05"
argument_list|,
literal|"2014-05-01T00:00:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30"
argument_list|,
literal|"2014-05-30T00:00:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20"
argument_list|,
literal|"2014-05-30T20:00:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21"
argument_list|,
literal|"2014-05-30T20:21:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21:35"
argument_list|,
literal|"2014-05-30T20:21:35.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21:35.123"
argument_list|,
literal|"2014-05-30T20:21:35.123"
argument_list|)
expr_stmt|;
block|}
DECL|method|testRoundingDoesNotAffectExactDate
specifier|public
name|void
name|testRoundingDoesNotAffectExactDate
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014-11-12T22:55:00.000Z"
argument_list|,
literal|"2014-11-12T22:55:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-12T22:55:00.000Z"
argument_list|,
literal|"2014-11-12T22:55:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-12T22:55:00.000"
argument_list|,
literal|"2014-11-12T21:55:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+01:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-12T22:55:00.000"
argument_list|,
literal|"2014-11-12T21:55:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+01:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-12T22:55:00.000+01:00"
argument_list|,
literal|"2014-11-12T21:55:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-12T22:55:00.000+01:00"
argument_list|,
literal|"2014-11-12T21:55:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testTimezone
specifier|public
name|void
name|testTimezone
parameter_list|()
block|{
comment|// timezone works within date format
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21+02:00"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
comment|// test alternative ways of writing zero offsets, according to ISO 8601 +00:00, +00, +0000 should work.
comment|// joda also seems to allow for -00:00, -00, -0000
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21+00:00"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21+00"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21+0000"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21-00:00"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21-00"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21-0000"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|)
expr_stmt|;
comment|// but also externally
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+02:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+00:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+00:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+0000"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"-00:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"-00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T18:21"
argument_list|,
literal|"2014-05-30T18:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"-0000"
argument_list|)
argument_list|)
expr_stmt|;
comment|// and timezone in the date has priority
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21+03:00"
argument_list|,
literal|"2014-05-30T17:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"-08:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21Z"
argument_list|,
literal|"2014-05-30T20:21:00.000"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"-08:00"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasicMath
specifier|public
name|void
name|testBasicMath
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+y"
argument_list|,
literal|"2015-11-18"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||-2y"
argument_list|,
literal|"2012-11-18"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+3M"
argument_list|,
literal|"2015-02-18"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||-M"
argument_list|,
literal|"2014-10-18"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+1w"
argument_list|,
literal|"2014-11-25"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||-3w"
argument_list|,
literal|"2014-10-28"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+22d"
argument_list|,
literal|"2014-12-10"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||-423d"
argument_list|,
literal|"2013-09-21"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||+13h"
argument_list|,
literal|"2014-11-19T03"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||-1h"
argument_list|,
literal|"2014-11-18T13"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||+13H"
argument_list|,
literal|"2014-11-19T03"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||-1H"
argument_list|,
literal|"2014-11-18T13"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||+10240m"
argument_list|,
literal|"2014-11-25T17:07"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||-10m"
argument_list|,
literal|"2014-11-18T14:17"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32||+60s"
argument_list|,
literal|"2014-11-18T14:28:32"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32||-3600s"
argument_list|,
literal|"2014-11-18T13:27:32"
argument_list|)
expr_stmt|;
block|}
DECL|method|testLenientEmptyMath
specifier|public
name|void
name|testLenientEmptyMath
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014-05-30T20:21||"
argument_list|,
literal|"2014-05-30T20:21:00.000"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultipleAdjustments
specifier|public
name|void
name|testMultipleAdjustments
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+1M-1M"
argument_list|,
literal|"2014-11-18"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+1M-1m"
argument_list|,
literal|"2014-12-17T23:59"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||-1m+1M"
argument_list|,
literal|"2014-12-17T23:59"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+1M/M"
argument_list|,
literal|"2014-12-01"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||+1M/M+1h"
argument_list|,
literal|"2014-12-01T01"
argument_list|)
expr_stmt|;
block|}
DECL|method|testNow
specifier|public
name|void
name|testNow
parameter_list|()
block|{
specifier|final
name|long
name|now
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"2014-11-18T14:27:32"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"now"
argument_list|,
literal|"2014-11-18T14:27:32"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"now+M"
argument_list|,
literal|"2014-12-18T14:27:32"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"now-2d"
argument_list|,
literal|"2014-11-16T14:27:32"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"now/m"
argument_list|,
literal|"2014-11-18T14:27"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// timezone does not affect now
name|assertDateMathEquals
argument_list|(
literal|"now/m"
argument_list|,
literal|"2014-11-18T14:27"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+02:00"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRoundingPreservesEpochAsBaseDate
specifier|public
name|void
name|testRoundingPreservesEpochAsBaseDate
parameter_list|()
block|{
comment|// If a user only specifies times, then the date needs to always be 1970-01-01 regardless of rounding
name|FormatDateTimeFormatter
name|formatter
init|=
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"HH:mm:ss"
argument_list|)
decl_stmt|;
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|formatter
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|formatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
argument_list|(
literal|"1970-01-01T04:52:20.000Z"
argument_list|)
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
literal|"04:52:20"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|formatter
operator|.
name|parser
argument_list|()
operator|.
name|parseMillis
argument_list|(
literal|"1970-01-01T04:52:20.999Z"
argument_list|)
argument_list|,
name|parser
operator|.
name|parse
argument_list|(
literal|"04:52:20"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Implicit rounding happening when parts of the date are not specified
DECL|method|testImplicitRounding
specifier|public
name|void
name|testImplicitRounding
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18"
argument_list|,
literal|"2014-11-18"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18"
argument_list|,
literal|"2014-11-18T23:59:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T09:20"
argument_list|,
literal|"2014-11-18T09:20"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T09:20"
argument_list|,
literal|"2014-11-18T09:20:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18"
argument_list|,
literal|"2014-11-17T23:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18"
argument_list|,
literal|"2014-11-18T22:59:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T09:20"
argument_list|,
literal|"2014-11-18T08:20:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T09:20"
argument_list|,
literal|"2014-11-18T08:20:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
comment|// implicit rounding with explicit timezone in the date format
name|FormatDateTimeFormatter
name|formatter
init|=
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"YYYY-MM-ddZ"
argument_list|)
decl_stmt|;
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|formatter
argument_list|)
decl_stmt|;
name|long
name|time
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"2011-10-09+01:00"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|parser
operator|.
name|parse
argument_list|(
literal|"2011-10-09T00:00:00.000+01:00"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|)
argument_list|,
name|time
argument_list|)
expr_stmt|;
name|time
operator|=
name|parser
operator|.
name|parse
argument_list|(
literal|"2011-10-09+01:00"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|this
operator|.
name|parser
operator|.
name|parse
argument_list|(
literal|"2011-10-09T23:59:59.999+01:00"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|)
argument_list|,
name|time
argument_list|)
expr_stmt|;
block|}
comment|// Explicit rounding using the || separator
DECL|method|testExplicitRounding
specifier|public
name|void
name|testExplicitRounding
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/y"
argument_list|,
literal|"2014-01-01"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/y"
argument_list|,
literal|"2014-12-31T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014||/y"
argument_list|,
literal|"2014-01-01"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-01-01T00:00:00.001||/y"
argument_list|,
literal|"2014-12-31T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// rounding should also take into account time zone
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/y"
argument_list|,
literal|"2013-12-31T23:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/y"
argument_list|,
literal|"2014-12-31T22:59:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/M"
argument_list|,
literal|"2014-11-01"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/M"
argument_list|,
literal|"2014-11-30T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11||/M"
argument_list|,
literal|"2014-11-01"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11||/M"
argument_list|,
literal|"2014-11-30T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/M"
argument_list|,
literal|"2014-10-31T23:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/M"
argument_list|,
literal|"2014-11-30T22:59:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/w"
argument_list|,
literal|"2014-11-17"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/w"
argument_list|,
literal|"2014-11-23T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/w"
argument_list|,
literal|"2014-11-17"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/w"
argument_list|,
literal|"2014-11-23T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/w"
argument_list|,
literal|"2014-11-16T23:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"+01:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/w"
argument_list|,
literal|"2014-11-17T01:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"-01:00"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/w"
argument_list|,
literal|"2014-11-16T23:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/w"
argument_list|,
literal|"2014-11-23T22:59:59.999Z"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-07-22||/w"
argument_list|,
literal|"2014-07-20T22:00:00.000Z"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
expr_stmt|;
comment|// with DST
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/d"
argument_list|,
literal|"2014-11-18"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/d"
argument_list|,
literal|"2014-11-18T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/d"
argument_list|,
literal|"2014-11-18"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18||/d"
argument_list|,
literal|"2014-11-18T23:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||/h"
argument_list|,
literal|"2014-11-18T14"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||/h"
argument_list|,
literal|"2014-11-18T14:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/H"
argument_list|,
literal|"2014-11-18T14"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/H"
argument_list|,
literal|"2014-11-18T14:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||/h"
argument_list|,
literal|"2014-11-18T14"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||/h"
argument_list|,
literal|"2014-11-18T14:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/H"
argument_list|,
literal|"2014-11-18T14"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14||/H"
argument_list|,
literal|"2014-11-18T14:59:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32||/m"
argument_list|,
literal|"2014-11-18T14:27"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32||/m"
argument_list|,
literal|"2014-11-18T14:27:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||/m"
argument_list|,
literal|"2014-11-18T14:27"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27||/m"
argument_list|,
literal|"2014-11-18T14:27:59.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32.123||/s"
argument_list|,
literal|"2014-11-18T14:27:32"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32.123||/s"
argument_list|,
literal|"2014-11-18T14:27:32.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32||/s"
argument_list|,
literal|"2014-11-18T14:27:32"
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"2014-11-18T14:27:32||/s"
argument_list|,
literal|"2014-11-18T14:27:32.999"
argument_list|,
literal|0
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|testTimestamps
specifier|public
name|void
name|testTimestamps
parameter_list|()
block|{
name|assertDateMathEquals
argument_list|(
literal|"1418248078000"
argument_list|,
literal|"2014-12-10T21:47:58.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"32484216259000"
argument_list|,
literal|"2999-05-20T17:24:19.000"
argument_list|)
expr_stmt|;
name|assertDateMathEquals
argument_list|(
literal|"253382837059000"
argument_list|,
literal|"9999-05-20T17:24:19.000"
argument_list|)
expr_stmt|;
comment|// datemath still works on timestamps
name|assertDateMathEquals
argument_list|(
literal|"1418248078000||/m"
argument_list|,
literal|"2014-12-10T21:47:00.000"
argument_list|)
expr_stmt|;
comment|// also check other time units
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"epoch_second||dateOptionalTime"
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|datetime
init|=
name|parser
operator|.
name|parse
argument_list|(
literal|"1418248078"
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|)
decl_stmt|;
name|assertDateEquals
argument_list|(
name|datetime
argument_list|,
literal|"1418248078"
argument_list|,
literal|"2014-12-10T21:47:58.000"
argument_list|)
expr_stmt|;
comment|// a timestamp before 10000 is a year
name|assertDateMathEquals
argument_list|(
literal|"9999"
argument_list|,
literal|"9999-01-01T00:00:00.000"
argument_list|)
expr_stmt|;
comment|// 10000 is also a year, breaking bwc, used to be a timestamp
name|assertDateMathEquals
argument_list|(
literal|"10000"
argument_list|,
literal|"10000-01-01T00:00:00.000"
argument_list|)
expr_stmt|;
comment|// but 10000 with T is still a date format
name|assertDateMathEquals
argument_list|(
literal|"10000T"
argument_list|,
literal|"10000-01-01T00:00:00.000"
argument_list|)
expr_stmt|;
block|}
DECL|method|assertParseException
name|void
name|assertParseException
parameter_list|(
name|String
name|msg
parameter_list|,
name|String
name|date
parameter_list|,
name|String
name|exc
parameter_list|)
block|{
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
name|date
argument_list|,
parameter_list|()
lambda|->
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Date: "
operator|+
name|date
operator|+
literal|"\n"
operator|+
name|msg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|e
argument_list|)
operator|.
name|contains
argument_list|(
name|exc
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testIllegalMathFormat
specifier|public
name|void
name|testIllegalMathFormat
parameter_list|()
block|{
name|assertParseException
argument_list|(
literal|"Expected date math unsupported operator exception"
argument_list|,
literal|"2014-11-18||*5"
argument_list|,
literal|"operator not supported"
argument_list|)
expr_stmt|;
name|assertParseException
argument_list|(
literal|"Expected date math incompatible rounding exception"
argument_list|,
literal|"2014-11-18||/2m"
argument_list|,
literal|"rounding"
argument_list|)
expr_stmt|;
name|assertParseException
argument_list|(
literal|"Expected date math illegal unit type exception"
argument_list|,
literal|"2014-11-18||+2a"
argument_list|,
literal|"unit [a] not supported"
argument_list|)
expr_stmt|;
name|assertParseException
argument_list|(
literal|"Expected date math truncation exception"
argument_list|,
literal|"2014-11-18||+12"
argument_list|,
literal|"truncated"
argument_list|)
expr_stmt|;
name|assertParseException
argument_list|(
literal|"Expected date math truncation exception"
argument_list|,
literal|"2014-11-18||-"
argument_list|,
literal|"truncated"
argument_list|)
expr_stmt|;
block|}
DECL|method|testIllegalDateFormat
specifier|public
name|void
name|testIllegalDateFormat
parameter_list|()
block|{
name|assertParseException
argument_list|(
literal|"Expected bad timestamp exception"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
operator|+
literal|"0"
argument_list|,
literal|"failed to parse date field"
argument_list|)
expr_stmt|;
name|assertParseException
argument_list|(
literal|"Expected bad date format exception"
argument_list|,
literal|"123bogus"
argument_list|,
literal|"with format"
argument_list|)
expr_stmt|;
block|}
DECL|method|testOnlyCallsNowIfNecessary
specifier|public
name|void
name|testOnlyCallsNowIfNecessary
parameter_list|()
block|{
specifier|final
name|AtomicBoolean
name|called
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|LongSupplier
name|now
init|=
parameter_list|()
lambda|->
block|{
name|called
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
literal|42L
return|;
block|}
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
literal|"2014-11-18T14:27:32"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|parse
argument_list|(
literal|"now/d"
argument_list|,
name|now
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|called
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testThatUnixTimestampMayNotHaveTimeZone
specifier|public
name|void
name|testThatUnixTimestampMayNotHaveTimeZone
parameter_list|()
block|{
name|DateMathParser
name|parser
init|=
operator|new
name|DateMathParser
argument_list|(
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"epoch_millis"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|parse
argument_list|(
literal|"1234567890123"
argument_list|,
parameter_list|()
lambda|->
literal|42
argument_list|,
literal|false
argument_list|,
name|DateTimeZone
operator|.
name|forTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected ElasticsearchParseException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchParseException
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
literal|"failed to parse date field"
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
literal|"with format [epoch_millis]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

