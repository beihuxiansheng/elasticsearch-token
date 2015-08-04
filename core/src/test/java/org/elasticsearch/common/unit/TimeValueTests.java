begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|PeriodType
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
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|lessThan
import|;
end_import

begin_class
DECL|class|TimeValueTests
specifier|public
class|class
name|TimeValueTests
extends|extends
name|ESTestCase
block|{
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
name|assertThat
argument_list|(
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|toMillis
argument_list|(
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|millis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
operator|.
name|toMicros
argument_list|(
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
operator|.
name|micros
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|toSeconds
argument_list|(
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|seconds
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|TimeUnit
operator|.
name|MINUTES
operator|.
name|toMinutes
argument_list|(
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|minutes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|TimeUnit
operator|.
name|HOURS
operator|.
name|toHours
argument_list|(
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
operator|.
name|hours
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toDays
argument_list|(
literal|10
argument_list|)
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
operator|.
name|days
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
block|{
name|assertThat
argument_list|(
literal|"10ms"
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5s"
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1533
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5m"
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|90
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5h"
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|90
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1.5d"
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|36
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"1000d"
argument_list|,
name|equalTo
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFormat
specifier|public
name|void
name|testFormat
parameter_list|()
block|{
name|assertThat
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1025
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
operator|.
name|format
argument_list|(
name|PeriodType
operator|.
name|dayTime
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1 second and 25 milliseconds"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|format
argument_list|(
name|PeriodType
operator|.
name|dayTime
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1 minute"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|65
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|format
argument_list|(
name|PeriodType
operator|.
name|dayTime
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1 hour and 5 minutes"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|24
operator|*
literal|600
operator|+
literal|85
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|format
argument_list|(
name|PeriodType
operator|.
name|dayTime
argument_list|()
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"241 hours and 25 minutes"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testMinusOne
specifier|public
name|void
name|testMinusOne
parameter_list|()
block|{
name|assertThat
argument_list|(
operator|new
name|TimeValue
argument_list|(
operator|-
literal|1
argument_list|)
operator|.
name|nanos
argument_list|()
argument_list|,
name|lessThan
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testParseTimeValue
specifier|public
name|void
name|testParseTimeValue
parameter_list|()
block|{
comment|// Space is allowed before unit:
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 ms"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10ms"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 MS"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10MS"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 s"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10s"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 S"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10S"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 m"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10m"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 M"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10M"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 h"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10h"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 H"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10H"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 d"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10d"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 D"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10D"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 w"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10w"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10 W"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|70
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|,
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"10W"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertEqualityAfterSerialize
specifier|private
name|void
name|assertEqualityAfterSerialize
parameter_list|(
name|TimeValue
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|value
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|TimeValue
name|inValue
init|=
name|TimeValue
operator|.
name|readTimeValue
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|inValue
argument_list|,
name|equalTo
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testSerialize
specifier|public
name|void
name|testSerialize
parameter_list|()
throws|throws
name|Exception
block|{
name|assertEqualityAfterSerialize
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|DAYS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEqualityAfterSerialize
argument_list|(
operator|new
name|TimeValue
argument_list|(
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEqualityAfterSerialize
argument_list|(
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ElasticsearchParseException
operator|.
name|class
argument_list|)
DECL|method|testFailOnUnknownUnits
specifier|public
name|void
name|testFailOnUnknownUnits
parameter_list|()
block|{
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"23tw"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ElasticsearchParseException
operator|.
name|class
argument_list|)
DECL|method|testFailOnMissingUnits
specifier|public
name|void
name|testFailOnMissingUnits
parameter_list|()
block|{
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"42"
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|ElasticsearchParseException
operator|.
name|class
argument_list|)
DECL|method|testNoDotsAllowed
specifier|public
name|void
name|testNoDotsAllowed
parameter_list|()
block|{
name|TimeValue
operator|.
name|parseTimeValue
argument_list|(
literal|"42ms."
argument_list|,
literal|null
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

