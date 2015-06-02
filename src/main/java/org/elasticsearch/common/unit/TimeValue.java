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
name|cluster
operator|.
name|metadata
operator|.
name|MetaDataIndexUpgradeService
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
name|Strings
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|Streamable
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
name|joda
operator|.
name|time
operator|.
name|Period
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
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|PeriodFormat
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
name|PeriodFormatter
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
name|io
operator|.
name|Serializable
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
name|Objects
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

begin_class
DECL|class|TimeValue
specifier|public
class|class
name|TimeValue
implements|implements
name|Serializable
implements|,
name|Streamable
block|{
comment|/** How many nano-seconds in one milli-second */
DECL|field|NSEC_PER_MSEC
specifier|public
specifier|static
specifier|final
name|long
name|NSEC_PER_MSEC
init|=
literal|1000000
decl_stmt|;
DECL|method|timeValueNanos
specifier|public
specifier|static
name|TimeValue
name|timeValueNanos
parameter_list|(
name|long
name|nanos
parameter_list|)
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|nanos
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
return|;
block|}
DECL|method|timeValueMillis
specifier|public
specifier|static
name|TimeValue
name|timeValueMillis
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|millis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
DECL|method|timeValueSeconds
specifier|public
specifier|static
name|TimeValue
name|timeValueSeconds
parameter_list|(
name|long
name|seconds
parameter_list|)
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|seconds
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
DECL|method|timeValueMinutes
specifier|public
specifier|static
name|TimeValue
name|timeValueMinutes
parameter_list|(
name|long
name|minutes
parameter_list|)
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|minutes
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
return|;
block|}
DECL|method|timeValueHours
specifier|public
specifier|static
name|TimeValue
name|timeValueHours
parameter_list|(
name|long
name|hours
parameter_list|)
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|hours
argument_list|,
name|TimeUnit
operator|.
name|HOURS
argument_list|)
return|;
block|}
DECL|field|duration
specifier|private
name|long
name|duration
decl_stmt|;
DECL|field|timeUnit
specifier|private
name|TimeUnit
name|timeUnit
decl_stmt|;
DECL|method|TimeValue
specifier|private
name|TimeValue
parameter_list|()
block|{      }
DECL|method|TimeValue
specifier|public
name|TimeValue
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
name|this
argument_list|(
name|millis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
DECL|method|TimeValue
specifier|public
name|TimeValue
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
name|this
operator|.
name|duration
operator|=
name|duration
expr_stmt|;
name|this
operator|.
name|timeUnit
operator|=
name|timeUnit
expr_stmt|;
block|}
DECL|method|nanos
specifier|public
name|long
name|nanos
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toNanos
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getNanos
specifier|public
name|long
name|getNanos
parameter_list|()
block|{
return|return
name|nanos
argument_list|()
return|;
block|}
DECL|method|micros
specifier|public
name|long
name|micros
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toMicros
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getMicros
specifier|public
name|long
name|getMicros
parameter_list|()
block|{
return|return
name|micros
argument_list|()
return|;
block|}
DECL|method|millis
specifier|public
name|long
name|millis
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toMillis
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getMillis
specifier|public
name|long
name|getMillis
parameter_list|()
block|{
return|return
name|millis
argument_list|()
return|;
block|}
DECL|method|seconds
specifier|public
name|long
name|seconds
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toSeconds
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getSeconds
specifier|public
name|long
name|getSeconds
parameter_list|()
block|{
return|return
name|seconds
argument_list|()
return|;
block|}
DECL|method|minutes
specifier|public
name|long
name|minutes
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toMinutes
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getMinutes
specifier|public
name|long
name|getMinutes
parameter_list|()
block|{
return|return
name|minutes
argument_list|()
return|;
block|}
DECL|method|hours
specifier|public
name|long
name|hours
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toHours
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getHours
specifier|public
name|long
name|getHours
parameter_list|()
block|{
return|return
name|hours
argument_list|()
return|;
block|}
DECL|method|days
specifier|public
name|long
name|days
parameter_list|()
block|{
return|return
name|timeUnit
operator|.
name|toDays
argument_list|(
name|duration
argument_list|)
return|;
block|}
DECL|method|getDays
specifier|public
name|long
name|getDays
parameter_list|()
block|{
return|return
name|days
argument_list|()
return|;
block|}
DECL|method|microsFrac
specifier|public
name|double
name|microsFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|nanos
argument_list|()
operator|)
operator|/
name|C1
return|;
block|}
DECL|method|getMicrosFrac
specifier|public
name|double
name|getMicrosFrac
parameter_list|()
block|{
return|return
name|microsFrac
argument_list|()
return|;
block|}
DECL|method|millisFrac
specifier|public
name|double
name|millisFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|nanos
argument_list|()
operator|)
operator|/
name|C2
return|;
block|}
DECL|method|getMillisFrac
specifier|public
name|double
name|getMillisFrac
parameter_list|()
block|{
return|return
name|millisFrac
argument_list|()
return|;
block|}
DECL|method|secondsFrac
specifier|public
name|double
name|secondsFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|nanos
argument_list|()
operator|)
operator|/
name|C3
return|;
block|}
DECL|method|getSecondsFrac
specifier|public
name|double
name|getSecondsFrac
parameter_list|()
block|{
return|return
name|secondsFrac
argument_list|()
return|;
block|}
DECL|method|minutesFrac
specifier|public
name|double
name|minutesFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|nanos
argument_list|()
operator|)
operator|/
name|C4
return|;
block|}
DECL|method|getMinutesFrac
specifier|public
name|double
name|getMinutesFrac
parameter_list|()
block|{
return|return
name|minutesFrac
argument_list|()
return|;
block|}
DECL|method|hoursFrac
specifier|public
name|double
name|hoursFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|nanos
argument_list|()
operator|)
operator|/
name|C5
return|;
block|}
DECL|method|getHoursFrac
specifier|public
name|double
name|getHoursFrac
parameter_list|()
block|{
return|return
name|hoursFrac
argument_list|()
return|;
block|}
DECL|method|daysFrac
specifier|public
name|double
name|daysFrac
parameter_list|()
block|{
return|return
operator|(
operator|(
name|double
operator|)
name|nanos
argument_list|()
operator|)
operator|/
name|C6
return|;
block|}
DECL|method|getDaysFrac
specifier|public
name|double
name|getDaysFrac
parameter_list|()
block|{
return|return
name|daysFrac
argument_list|()
return|;
block|}
DECL|field|defaultFormatter
specifier|private
specifier|final
name|PeriodFormatter
name|defaultFormatter
init|=
name|PeriodFormat
operator|.
name|getDefault
argument_list|()
operator|.
name|withParseType
argument_list|(
name|PeriodType
operator|.
name|standard
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|format
specifier|public
name|String
name|format
parameter_list|()
block|{
name|Period
name|period
init|=
operator|new
name|Period
argument_list|(
name|millis
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|defaultFormatter
operator|.
name|print
argument_list|(
name|period
argument_list|)
return|;
block|}
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|PeriodType
name|type
parameter_list|)
block|{
name|Period
name|period
init|=
operator|new
name|Period
argument_list|(
name|millis
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|PeriodFormat
operator|.
name|getDefault
argument_list|()
operator|.
name|withParseType
argument_list|(
name|type
argument_list|)
operator|.
name|print
argument_list|(
name|period
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|duration
operator|<
literal|0
condition|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|duration
argument_list|)
return|;
block|}
name|long
name|nanos
init|=
name|nanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|nanos
operator|==
literal|0
condition|)
block|{
return|return
literal|"0s"
return|;
block|}
name|double
name|value
init|=
name|nanos
decl_stmt|;
name|String
name|suffix
init|=
literal|"nanos"
decl_stmt|;
if|if
condition|(
name|nanos
operator|>=
name|C6
condition|)
block|{
name|value
operator|=
name|daysFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"d"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nanos
operator|>=
name|C5
condition|)
block|{
name|value
operator|=
name|hoursFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"h"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nanos
operator|>=
name|C4
condition|)
block|{
name|value
operator|=
name|minutesFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"m"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nanos
operator|>=
name|C3
condition|)
block|{
name|value
operator|=
name|secondsFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"s"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nanos
operator|>=
name|C2
condition|)
block|{
name|value
operator|=
name|millisFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"ms"
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|nanos
operator|>=
name|C1
condition|)
block|{
name|value
operator|=
name|microsFrac
argument_list|()
expr_stmt|;
name|suffix
operator|=
literal|"micros"
expr_stmt|;
block|}
return|return
name|Strings
operator|.
name|format1Decimals
argument_list|(
name|value
argument_list|,
name|suffix
argument_list|)
return|;
block|}
DECL|method|parseTimeValue
specifier|public
specifier|static
name|TimeValue
name|parseTimeValue
parameter_list|(
name|String
name|sValue
parameter_list|,
name|TimeValue
name|defaultValue
parameter_list|,
name|String
name|settingName
parameter_list|)
block|{
name|settingName
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|settingName
argument_list|)
expr_stmt|;
assert|assert
name|settingName
operator|.
name|startsWith
argument_list|(
literal|"index."
argument_list|)
operator|==
literal|false
operator|||
name|MetaDataIndexUpgradeService
operator|.
name|INDEX_TIME_SETTINGS
operator|.
name|contains
argument_list|(
name|settingName
argument_list|)
assert|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
name|long
name|millis
decl_stmt|;
comment|// TODO: we should be consistent about whether upper-case is allowed (it is always allowed for ByteSizeValue, but here only for
comment|// s/S and h/H):
name|String
name|lowerSValue
init|=
name|sValue
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"ms"
argument_list|)
condition|)
block|{
name|millis
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"s"
argument_list|)
condition|)
block|{
name|millis
operator|=
operator|(
name|long
operator|)
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|1000
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"m"
argument_list|)
condition|)
block|{
name|millis
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
name|millis
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"d"
argument_list|)
condition|)
block|{
name|millis
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|endsWith
argument_list|(
literal|"w"
argument_list|)
condition|)
block|{
name|millis
operator|=
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|lowerSValue
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lowerSValue
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|7
operator|*
literal|24
operator|*
literal|60
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|equals
argument_list|(
literal|"-1"
argument_list|)
condition|)
block|{
comment|// Allow this special value to be unit-less:
name|millis
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lowerSValue
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
condition|)
block|{
comment|// Allow this special value to be unit-less:
name|millis
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|Settings
operator|.
name|getSettingsRequireUnits
argument_list|()
condition|)
block|{
comment|// Missing units:
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse setting ["
operator|+
name|settingName
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"] as a time value: unit is missing or unrecognized"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Leniency default to msec for bwc:
name|millis
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|sValue
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|TimeValue
argument_list|(
name|millis
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"Failed to parse ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|field|C0
specifier|static
specifier|final
name|long
name|C0
init|=
literal|1L
decl_stmt|;
DECL|field|C1
specifier|static
specifier|final
name|long
name|C1
init|=
name|C0
operator|*
literal|1000L
decl_stmt|;
DECL|field|C2
specifier|static
specifier|final
name|long
name|C2
init|=
name|C1
operator|*
literal|1000L
decl_stmt|;
DECL|field|C3
specifier|static
specifier|final
name|long
name|C3
init|=
name|C2
operator|*
literal|1000L
decl_stmt|;
DECL|field|C4
specifier|static
specifier|final
name|long
name|C4
init|=
name|C3
operator|*
literal|60L
decl_stmt|;
DECL|field|C5
specifier|static
specifier|final
name|long
name|C5
init|=
name|C4
operator|*
literal|60L
decl_stmt|;
DECL|field|C6
specifier|static
specifier|final
name|long
name|C6
init|=
name|C5
operator|*
literal|24L
decl_stmt|;
DECL|method|readTimeValue
specifier|public
specifier|static
name|TimeValue
name|readTimeValue
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|TimeValue
name|timeValue
init|=
operator|new
name|TimeValue
argument_list|()
decl_stmt|;
name|timeValue
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|timeValue
return|;
block|}
comment|/**      * serialization converts TimeValue internally to NANOSECONDS      */
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|duration
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|NANOSECONDS
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|nanos
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|TimeValue
name|timeValue
init|=
operator|(
name|TimeValue
operator|)
name|o
decl_stmt|;
return|return
name|timeUnit
operator|.
name|toNanos
argument_list|(
name|duration
argument_list|)
operator|==
name|timeValue
operator|.
name|timeUnit
operator|.
name|toNanos
argument_list|(
name|timeValue
operator|.
name|duration
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|long
name|normalized
init|=
name|timeUnit
operator|.
name|toNanos
argument_list|(
name|duration
argument_list|)
decl_stmt|;
return|return
call|(
name|int
call|)
argument_list|(
name|normalized
operator|^
operator|(
name|normalized
operator|>>>
literal|32
operator|)
argument_list|)
return|;
block|}
DECL|method|nsecToMSec
specifier|public
specifier|static
name|long
name|nsecToMSec
parameter_list|(
name|long
name|ns
parameter_list|)
block|{
return|return
name|ns
operator|/
name|NSEC_PER_MSEC
return|;
block|}
block|}
end_class

end_unit

