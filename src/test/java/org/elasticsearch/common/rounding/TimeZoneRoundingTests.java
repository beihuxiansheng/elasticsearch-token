begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.rounding
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|rounding
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
name|test
operator|.
name|ElasticsearchTestCase
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
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|ISODateTimeFormat
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
name|equalTo
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TimeZoneRoundingTests
specifier|public
class|class
name|TimeZoneRoundingTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testUTCMonthRounding
specifier|public
name|void
name|testUTCMonthRounding
parameter_list|()
block|{
name|Rounding
name|tzRounding
init|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|MONTH_OF_YEAR
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2009-02-01T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|utc
argument_list|(
literal|"2009-02-01T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2009-03-01T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|WEEK_OF_WEEKYEAR
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2012-01-10T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2012-01-09T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|utc
argument_list|(
literal|"2012-01-09T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2012-01-16T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|WEEK_OF_WEEKYEAR
argument_list|)
operator|.
name|postOffset
argument_list|(
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|24
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2012-01-10T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2012-01-08T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|utc
argument_list|(
literal|"2012-01-08T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2012-01-15T00:00:00.000Z"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testDayTimeZoneRounding
specifier|public
name|void
name|testDayTimeZoneRounding
parameter_list|()
block|{
name|Rounding
name|tzRounding
init|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|DAY_OF_MONTH
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|24
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|24
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|DAY_OF_MONTH
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|postZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|26
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|26
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|2
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|DAY_OF_MONTH
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2009-02-02T00:00:00"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|utc
argument_list|(
literal|"2009-02-02T00:00:00"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T00:00:00"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|DAY_OF_MONTH
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|postZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2009-02-02T00:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|+
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|time
argument_list|(
literal|"2009-02-02T00:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|+
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2009-02-03T00:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|+
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimeTimeZoneRounding
specifier|public
name|void
name|testTimeTimeZoneRounding
parameter_list|()
block|{
name|Rounding
name|tzRounding
init|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
literal|0l
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|1l
argument_list|)
operator|.
name|getMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|postZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|2
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|2
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|0l
operator|-
name|TimeValue
operator|.
name|timeValueHours
argument_list|(
literal|1
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:00:00"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:00:00"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T02:00:00"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|postZone
argument_list|(
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|-
literal|2
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|utc
argument_list|(
literal|"2009-02-03T01:01:01"
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2009-02-03T01:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|+
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|nextRoundingValue
argument_list|(
name|time
argument_list|(
literal|"2009-02-03T01:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|+
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2009-02-03T02:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forOffsetHours
argument_list|(
operator|+
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTimeTimeZoneRoundingDST
specifier|public
name|void
name|testTimeTimeZoneRoundingDST
parameter_list|()
block|{
name|Rounding
name|tzRounding
decl_stmt|;
comment|// testing savings to non savings switch
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-10-26T01:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-10-26T01:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-10-26T01:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-10-26T01:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// testing non savings to savings switch
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-03-30T01:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-03-30T01:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-03-30T01:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-03-30T01:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"CET"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// testing non savings to savings switch (America/Chicago)
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-03-09T03:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-03-09T03:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-03-09T03:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-03-09T03:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// testing savings to non savings switch 2013 (America/Chicago)
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2013-11-03T06:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2013-11-03T06:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2013-11-03T06:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2013-11-03T06:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// testing savings to non savings switch 2014 (America/Chicago)
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-11-02T06:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-11-02T06:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|DateTimeUnit
operator|.
name|HOUR_OF_DAY
argument_list|)
operator|.
name|preZone
argument_list|(
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|round
argument_list|(
name|time
argument_list|(
literal|"2014-11-02T06:01:01"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|equalTo
argument_list|(
name|time
argument_list|(
literal|"2014-11-02T06:00:00"
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
literal|"America/Chicago"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|utc
specifier|private
name|long
name|utc
parameter_list|(
name|String
name|time
parameter_list|)
block|{
return|return
name|time
argument_list|(
name|time
argument_list|,
name|DateTimeZone
operator|.
name|UTC
argument_list|)
return|;
block|}
DECL|method|time
specifier|private
name|long
name|time
parameter_list|(
name|String
name|time
parameter_list|,
name|DateTimeZone
name|zone
parameter_list|)
block|{
return|return
name|ISODateTimeFormat
operator|.
name|dateOptionalTimeParser
argument_list|()
operator|.
name|withZone
argument_list|(
name|zone
argument_list|)
operator|.
name|parseMillis
argument_list|(
name|time
argument_list|)
return|;
block|}
block|}
end_class

end_unit

