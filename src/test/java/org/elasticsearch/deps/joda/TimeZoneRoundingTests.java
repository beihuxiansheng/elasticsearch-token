begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.deps.joda
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|deps
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
name|common
operator|.
name|joda
operator|.
name|TimeZoneRounding
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
name|Chronology
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
name|chrono
operator|.
name|ISOChronology
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
name|TimeZoneRounding
name|tzRounding
init|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|monthOfYear
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|tzRounding
operator|.
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|weekOfWeekyear
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|weekOfWeekyear
argument_list|()
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
name|calc
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
block|}
annotation|@
name|Test
DECL|method|testDayTimeZoneRounding
specifier|public
name|void
name|testDayTimeZoneRounding
parameter_list|()
block|{
name|TimeZoneRounding
name|tzRounding
init|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|dayOfMonth
argument_list|()
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|dayOfMonth
argument_list|()
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|dayOfMonth
argument_list|()
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|dayOfMonth
argument_list|()
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
name|calc
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
block|}
annotation|@
name|Test
DECL|method|testTimeTimeZoneRounding
specifier|public
name|void
name|testTimeTimeZoneRounding
parameter_list|()
block|{
name|TimeZoneRounding
name|tzRounding
init|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|hourOfDay
argument_list|()
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|hourOfDay
argument_list|()
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|hourOfDay
argument_list|()
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
name|calc
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
name|tzRounding
operator|=
name|TimeZoneRounding
operator|.
name|builder
argument_list|(
name|chronology
argument_list|()
operator|.
name|hourOfDay
argument_list|()
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
name|calc
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
block|}
DECL|method|chronology
specifier|private
specifier|static
name|Chronology
name|chronology
parameter_list|()
block|{
return|return
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
return|;
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

