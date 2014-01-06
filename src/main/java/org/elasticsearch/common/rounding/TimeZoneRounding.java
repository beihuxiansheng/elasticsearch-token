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
name|unit
operator|.
name|TimeValue
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
name|DateTimeConstants
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TimeZoneRounding
specifier|public
specifier|abstract
class|class
name|TimeZoneRounding
implements|implements
name|Rounding
block|{
DECL|method|round
specifier|public
specifier|abstract
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
function_decl|;
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|DateTimeUnit
name|unit
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|unit
argument_list|)
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|(
name|TimeValue
name|interval
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|(
name|interval
argument_list|)
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|unit
specifier|private
name|DateTimeUnit
name|unit
decl_stmt|;
DECL|field|interval
specifier|private
name|long
name|interval
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|preTz
specifier|private
name|DateTimeZone
name|preTz
init|=
name|DateTimeZone
operator|.
name|UTC
decl_stmt|;
DECL|field|postTz
specifier|private
name|DateTimeZone
name|postTz
init|=
name|DateTimeZone
operator|.
name|UTC
decl_stmt|;
DECL|field|factor
specifier|private
name|float
name|factor
init|=
literal|1.0f
decl_stmt|;
DECL|field|preOffset
specifier|private
name|long
name|preOffset
decl_stmt|;
DECL|field|postOffset
specifier|private
name|long
name|postOffset
decl_stmt|;
DECL|field|preZoneAdjustLargeInterval
specifier|private
name|boolean
name|preZoneAdjustLargeInterval
init|=
literal|false
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|DateTimeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|interval
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|Builder
specifier|public
name|Builder
parameter_list|(
name|TimeValue
name|interval
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|interval
operator|=
name|interval
operator|.
name|millis
argument_list|()
expr_stmt|;
block|}
DECL|method|preZone
specifier|public
name|Builder
name|preZone
parameter_list|(
name|DateTimeZone
name|preTz
parameter_list|)
block|{
name|this
operator|.
name|preTz
operator|=
name|preTz
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|preZoneAdjustLargeInterval
specifier|public
name|Builder
name|preZoneAdjustLargeInterval
parameter_list|(
name|boolean
name|preZoneAdjustLargeInterval
parameter_list|)
block|{
name|this
operator|.
name|preZoneAdjustLargeInterval
operator|=
name|preZoneAdjustLargeInterval
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|postZone
specifier|public
name|Builder
name|postZone
parameter_list|(
name|DateTimeZone
name|postTz
parameter_list|)
block|{
name|this
operator|.
name|postTz
operator|=
name|postTz
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|preOffset
specifier|public
name|Builder
name|preOffset
parameter_list|(
name|long
name|preOffset
parameter_list|)
block|{
name|this
operator|.
name|preOffset
operator|=
name|preOffset
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|postOffset
specifier|public
name|Builder
name|postOffset
parameter_list|(
name|long
name|postOffset
parameter_list|)
block|{
name|this
operator|.
name|postOffset
operator|=
name|postOffset
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|factor
specifier|public
name|Builder
name|factor
parameter_list|(
name|float
name|factor
parameter_list|)
block|{
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|TimeZoneRounding
name|build
parameter_list|()
block|{
name|TimeZoneRounding
name|timeZoneRounding
decl_stmt|;
if|if
condition|(
name|unit
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|preTz
operator|.
name|equals
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
operator|&&
name|postTz
operator|.
name|equals
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
condition|)
block|{
name|timeZoneRounding
operator|=
operator|new
name|UTCTimeZoneRoundingFloor
argument_list|(
name|unit
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|preZoneAdjustLargeInterval
operator|||
name|unit
operator|.
name|field
argument_list|()
operator|.
name|getDurationField
argument_list|()
operator|.
name|getUnitMillis
argument_list|()
operator|<
name|DateTimeConstants
operator|.
name|MILLIS_PER_HOUR
operator|*
literal|12
condition|)
block|{
name|timeZoneRounding
operator|=
operator|new
name|TimeTimeZoneRoundingFloor
argument_list|(
name|unit
argument_list|,
name|preTz
argument_list|,
name|postTz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|timeZoneRounding
operator|=
operator|new
name|DayTimeZoneRoundingFloor
argument_list|(
name|unit
argument_list|,
name|preTz
argument_list|,
name|postTz
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|preTz
operator|.
name|equals
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
operator|&&
name|postTz
operator|.
name|equals
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
condition|)
block|{
name|timeZoneRounding
operator|=
operator|new
name|UTCIntervalTimeZoneRounding
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|preZoneAdjustLargeInterval
operator|||
name|interval
operator|<
name|DateTimeConstants
operator|.
name|MILLIS_PER_HOUR
operator|*
literal|12
condition|)
block|{
name|timeZoneRounding
operator|=
operator|new
name|TimeIntervalTimeZoneRounding
argument_list|(
name|interval
argument_list|,
name|preTz
argument_list|,
name|postTz
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|timeZoneRounding
operator|=
operator|new
name|DayIntervalTimeZoneRounding
argument_list|(
name|interval
argument_list|,
name|preTz
argument_list|,
name|postTz
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|preOffset
operator|!=
literal|0
operator|||
name|postOffset
operator|!=
literal|0
condition|)
block|{
name|timeZoneRounding
operator|=
operator|new
name|PrePostTimeZoneRounding
argument_list|(
name|timeZoneRounding
argument_list|,
name|preOffset
argument_list|,
name|postOffset
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|factor
operator|!=
literal|1.0f
condition|)
block|{
name|timeZoneRounding
operator|=
operator|new
name|FactorTimeZoneRounding
argument_list|(
name|timeZoneRounding
argument_list|,
name|factor
argument_list|)
expr_stmt|;
block|}
return|return
name|timeZoneRounding
return|;
block|}
block|}
DECL|class|TimeTimeZoneRoundingFloor
specifier|static
class|class
name|TimeTimeZoneRoundingFloor
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|static
specifier|final
name|byte
name|ID
init|=
literal|1
decl_stmt|;
DECL|field|unit
specifier|private
name|DateTimeUnit
name|unit
decl_stmt|;
DECL|field|preTz
specifier|private
name|DateTimeZone
name|preTz
decl_stmt|;
DECL|field|postTz
specifier|private
name|DateTimeZone
name|postTz
decl_stmt|;
DECL|method|TimeTimeZoneRoundingFloor
name|TimeTimeZoneRoundingFloor
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|TimeTimeZoneRoundingFloor
name|TimeTimeZoneRoundingFloor
parameter_list|(
name|DateTimeUnit
name|unit
parameter_list|,
name|DateTimeZone
name|preTz
parameter_list|,
name|DateTimeZone
name|postTz
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|preTz
operator|=
name|preTz
expr_stmt|;
name|this
operator|.
name|postTz
operator|=
name|postTz
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
name|long
name|time
init|=
name|utcMillis
operator|+
name|preTz
operator|.
name|getOffset
argument_list|(
name|utcMillis
argument_list|)
decl_stmt|;
name|time
operator|=
name|unit
operator|.
name|field
argument_list|()
operator|.
name|roundFloor
argument_list|(
name|time
argument_list|)
expr_stmt|;
comment|// now, time is still in local, move it to UTC (or the adjustLargeInterval flag is set)
name|time
operator|=
name|time
operator|-
name|preTz
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
expr_stmt|;
comment|// now apply post Tz
name|time
operator|=
name|time
operator|+
name|postTz
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|time
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
comment|//            return value + unit.field().getDurationField().getUnitMillis();
return|return
name|unit
operator|.
name|field
argument_list|()
operator|.
name|roundCeiling
argument_list|(
name|value
operator|+
literal|1
argument_list|)
return|;
block|}
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
name|unit
operator|=
name|DateTimeUnit
operator|.
name|resolve
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|preTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
expr_stmt|;
name|postTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
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
name|writeByte
argument_list|(
name|unit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|preTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|postTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|UTCTimeZoneRoundingFloor
specifier|static
class|class
name|UTCTimeZoneRoundingFloor
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|2
decl_stmt|;
DECL|field|unit
specifier|private
name|DateTimeUnit
name|unit
decl_stmt|;
DECL|method|UTCTimeZoneRoundingFloor
name|UTCTimeZoneRoundingFloor
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|UTCTimeZoneRoundingFloor
name|UTCTimeZoneRoundingFloor
parameter_list|(
name|DateTimeUnit
name|unit
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
return|return
name|unit
operator|.
name|field
argument_list|()
operator|.
name|roundFloor
argument_list|(
name|utcMillis
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|unit
operator|.
name|field
argument_list|()
operator|.
name|roundCeiling
argument_list|(
name|value
operator|+
literal|1
argument_list|)
return|;
block|}
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
name|unit
operator|=
name|DateTimeUnit
operator|.
name|resolve
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
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
name|writeByte
argument_list|(
name|unit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DayTimeZoneRoundingFloor
specifier|static
class|class
name|DayTimeZoneRoundingFloor
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|3
decl_stmt|;
DECL|field|unit
specifier|private
name|DateTimeUnit
name|unit
decl_stmt|;
DECL|field|preTz
specifier|private
name|DateTimeZone
name|preTz
decl_stmt|;
DECL|field|postTz
specifier|private
name|DateTimeZone
name|postTz
decl_stmt|;
DECL|method|DayTimeZoneRoundingFloor
name|DayTimeZoneRoundingFloor
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|DayTimeZoneRoundingFloor
name|DayTimeZoneRoundingFloor
parameter_list|(
name|DateTimeUnit
name|unit
parameter_list|,
name|DateTimeZone
name|preTz
parameter_list|,
name|DateTimeZone
name|postTz
parameter_list|)
block|{
name|this
operator|.
name|unit
operator|=
name|unit
expr_stmt|;
name|this
operator|.
name|preTz
operator|=
name|preTz
expr_stmt|;
name|this
operator|.
name|postTz
operator|=
name|postTz
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
name|long
name|time
init|=
name|utcMillis
operator|+
name|preTz
operator|.
name|getOffset
argument_list|(
name|utcMillis
argument_list|)
decl_stmt|;
name|time
operator|=
name|unit
operator|.
name|field
argument_list|()
operator|.
name|roundFloor
argument_list|(
name|time
argument_list|)
expr_stmt|;
comment|// after rounding, since its day level (and above), its actually UTC!
comment|// now apply post Tz
name|time
operator|=
name|time
operator|+
name|postTz
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|time
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|unit
operator|.
name|field
argument_list|()
operator|.
name|getDurationField
argument_list|()
operator|.
name|getUnitMillis
argument_list|()
operator|+
name|value
return|;
block|}
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
name|unit
operator|=
name|DateTimeUnit
operator|.
name|resolve
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|preTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
expr_stmt|;
name|postTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
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
name|writeByte
argument_list|(
name|unit
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|preTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|postTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|UTCIntervalTimeZoneRounding
specifier|static
class|class
name|UTCIntervalTimeZoneRounding
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|4
decl_stmt|;
DECL|field|interval
specifier|private
name|long
name|interval
decl_stmt|;
DECL|method|UTCIntervalTimeZoneRounding
name|UTCIntervalTimeZoneRounding
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|UTCIntervalTimeZoneRounding
name|UTCIntervalTimeZoneRounding
parameter_list|(
name|long
name|interval
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
return|return
name|Rounding
operator|.
name|Interval
operator|.
name|round
argument_list|(
name|utcMillis
argument_list|,
name|interval
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|+
name|interval
return|;
block|}
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
name|interval
operator|=
name|in
operator|.
name|readVLong
argument_list|()
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
name|writeVLong
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TimeIntervalTimeZoneRounding
specifier|static
class|class
name|TimeIntervalTimeZoneRounding
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|5
decl_stmt|;
DECL|field|interval
specifier|private
name|long
name|interval
decl_stmt|;
DECL|field|preTz
specifier|private
name|DateTimeZone
name|preTz
decl_stmt|;
DECL|field|postTz
specifier|private
name|DateTimeZone
name|postTz
decl_stmt|;
DECL|method|TimeIntervalTimeZoneRounding
name|TimeIntervalTimeZoneRounding
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|TimeIntervalTimeZoneRounding
name|TimeIntervalTimeZoneRounding
parameter_list|(
name|long
name|interval
parameter_list|,
name|DateTimeZone
name|preTz
parameter_list|,
name|DateTimeZone
name|postTz
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|preTz
operator|=
name|preTz
expr_stmt|;
name|this
operator|.
name|postTz
operator|=
name|postTz
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
name|long
name|time
init|=
name|utcMillis
operator|+
name|preTz
operator|.
name|getOffset
argument_list|(
name|utcMillis
argument_list|)
decl_stmt|;
name|time
operator|=
name|Rounding
operator|.
name|Interval
operator|.
name|round
argument_list|(
name|time
argument_list|,
name|interval
argument_list|)
expr_stmt|;
comment|// now, time is still in local, move it to UTC
name|time
operator|=
name|time
operator|-
name|preTz
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
expr_stmt|;
comment|// now apply post Tz
name|time
operator|=
name|time
operator|+
name|postTz
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|time
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|+
name|interval
return|;
block|}
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
name|interval
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|preTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
expr_stmt|;
name|postTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
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
name|writeVLong
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|preTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|postTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DayIntervalTimeZoneRounding
specifier|static
class|class
name|DayIntervalTimeZoneRounding
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|6
decl_stmt|;
DECL|field|interval
specifier|private
name|long
name|interval
decl_stmt|;
DECL|field|preTz
specifier|private
name|DateTimeZone
name|preTz
decl_stmt|;
DECL|field|postTz
specifier|private
name|DateTimeZone
name|postTz
decl_stmt|;
DECL|method|DayIntervalTimeZoneRounding
name|DayIntervalTimeZoneRounding
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|DayIntervalTimeZoneRounding
name|DayIntervalTimeZoneRounding
parameter_list|(
name|long
name|interval
parameter_list|,
name|DateTimeZone
name|preTz
parameter_list|,
name|DateTimeZone
name|postTz
parameter_list|)
block|{
name|this
operator|.
name|interval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|preTz
operator|=
name|preTz
expr_stmt|;
name|this
operator|.
name|postTz
operator|=
name|postTz
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
name|long
name|time
init|=
name|utcMillis
operator|+
name|preTz
operator|.
name|getOffset
argument_list|(
name|utcMillis
argument_list|)
decl_stmt|;
name|time
operator|=
name|Rounding
operator|.
name|Interval
operator|.
name|round
argument_list|(
name|time
argument_list|,
name|interval
argument_list|)
expr_stmt|;
comment|// after rounding, since its day level (and above), its actually UTC!
comment|// now apply post Tz
name|time
operator|=
name|time
operator|+
name|postTz
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
expr_stmt|;
return|return
name|time
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|+
name|interval
return|;
block|}
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
name|interval
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|preTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
expr_stmt|;
name|postTz
operator|=
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readSharedString
argument_list|()
argument_list|)
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
name|writeVLong
argument_list|(
name|interval
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|preTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeSharedString
argument_list|(
name|postTz
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FactorTimeZoneRounding
specifier|static
class|class
name|FactorTimeZoneRounding
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|7
decl_stmt|;
DECL|field|timeZoneRounding
specifier|private
name|TimeZoneRounding
name|timeZoneRounding
decl_stmt|;
DECL|field|factor
specifier|private
name|float
name|factor
decl_stmt|;
DECL|method|FactorTimeZoneRounding
name|FactorTimeZoneRounding
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|FactorTimeZoneRounding
name|FactorTimeZoneRounding
parameter_list|(
name|TimeZoneRounding
name|timeZoneRounding
parameter_list|,
name|float
name|factor
parameter_list|)
block|{
name|this
operator|.
name|timeZoneRounding
operator|=
name|timeZoneRounding
expr_stmt|;
name|this
operator|.
name|factor
operator|=
name|factor
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
return|return
name|timeZoneRounding
operator|.
name|round
argument_list|(
call|(
name|long
call|)
argument_list|(
name|factor
operator|*
name|utcMillis
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|timeZoneRounding
operator|.
name|nextRoundingValue
argument_list|(
name|value
argument_list|)
return|;
block|}
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
name|timeZoneRounding
operator|=
operator|(
name|TimeZoneRounding
operator|)
name|Rounding
operator|.
name|Streams
operator|.
name|read
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|factor
operator|=
name|in
operator|.
name|readFloat
argument_list|()
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
name|Rounding
operator|.
name|Streams
operator|.
name|write
argument_list|(
name|timeZoneRounding
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|factor
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PrePostTimeZoneRounding
specifier|static
class|class
name|PrePostTimeZoneRounding
extends|extends
name|TimeZoneRounding
block|{
DECL|field|ID
specifier|final
specifier|static
name|byte
name|ID
init|=
literal|8
decl_stmt|;
DECL|field|timeZoneRounding
specifier|private
name|TimeZoneRounding
name|timeZoneRounding
decl_stmt|;
DECL|field|preOffset
specifier|private
name|long
name|preOffset
decl_stmt|;
DECL|field|postOffset
specifier|private
name|long
name|postOffset
decl_stmt|;
DECL|method|PrePostTimeZoneRounding
name|PrePostTimeZoneRounding
parameter_list|()
block|{
comment|// for serialization
block|}
DECL|method|PrePostTimeZoneRounding
name|PrePostTimeZoneRounding
parameter_list|(
name|TimeZoneRounding
name|timeZoneRounding
parameter_list|,
name|long
name|preOffset
parameter_list|,
name|long
name|postOffset
parameter_list|)
block|{
name|this
operator|.
name|timeZoneRounding
operator|=
name|timeZoneRounding
expr_stmt|;
name|this
operator|.
name|preOffset
operator|=
name|preOffset
expr_stmt|;
name|this
operator|.
name|postOffset
operator|=
name|postOffset
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
annotation|@
name|Override
DECL|method|round
specifier|public
name|long
name|round
parameter_list|(
name|long
name|utcMillis
parameter_list|)
block|{
return|return
name|postOffset
operator|+
name|timeZoneRounding
operator|.
name|round
argument_list|(
name|utcMillis
operator|+
name|preOffset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|nextRoundingValue
specifier|public
name|long
name|nextRoundingValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|postOffset
operator|+
name|timeZoneRounding
operator|.
name|nextRoundingValue
argument_list|(
name|value
operator|-
name|postOffset
argument_list|)
return|;
block|}
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
name|timeZoneRounding
operator|=
operator|(
name|TimeZoneRounding
operator|)
name|Rounding
operator|.
name|Streams
operator|.
name|read
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|preOffset
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|postOffset
operator|=
name|in
operator|.
name|readVLong
argument_list|()
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
name|Rounding
operator|.
name|Streams
operator|.
name|write
argument_list|(
name|timeZoneRounding
argument_list|,
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|preOffset
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|postOffset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

