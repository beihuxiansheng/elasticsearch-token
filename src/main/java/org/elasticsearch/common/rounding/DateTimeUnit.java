begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticsearchException
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
name|joda
operator|.
name|Joda
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
name|DateTimeField
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

begin_comment
comment|/**  *  */
end_comment

begin_enum
DECL|enum|DateTimeUnit
specifier|public
enum|enum
name|DateTimeUnit
block|{
DECL|enum constant|WEEK_OF_WEEKYEAR
name|WEEK_OF_WEEKYEAR
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|weekOfWeekyear
argument_list|()
argument_list|)
block|,
DECL|enum constant|YEAR_OF_CENTURY
name|YEAR_OF_CENTURY
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|yearOfCentury
argument_list|()
argument_list|)
block|,
DECL|enum constant|QUARTER
name|QUARTER
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|,
name|Joda
operator|.
name|QuarterOfYear
operator|.
name|getField
argument_list|(
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
argument_list|)
argument_list|)
block|,
DECL|enum constant|MONTH_OF_YEAR
name|MONTH_OF_YEAR
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|monthOfYear
argument_list|()
argument_list|)
block|,
DECL|enum constant|DAY_OF_MONTH
name|DAY_OF_MONTH
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|dayOfMonth
argument_list|()
argument_list|)
block|,
DECL|enum constant|HOUR_OF_DAY
name|HOUR_OF_DAY
argument_list|(
operator|(
name|byte
operator|)
literal|6
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|hourOfDay
argument_list|()
argument_list|)
block|,
DECL|enum constant|MINUTES_OF_HOUR
name|MINUTES_OF_HOUR
argument_list|(
operator|(
name|byte
operator|)
literal|7
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|minuteOfHour
argument_list|()
argument_list|)
block|,
DECL|enum constant|SECOND_OF_MINUTE
name|SECOND_OF_MINUTE
argument_list|(
operator|(
name|byte
operator|)
literal|8
argument_list|,
name|ISOChronology
operator|.
name|getInstanceUTC
argument_list|()
operator|.
name|secondOfMinute
argument_list|()
argument_list|)
block|;
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|DateTimeField
name|field
decl_stmt|;
DECL|method|DateTimeUnit
specifier|private
name|DateTimeUnit
parameter_list|(
name|byte
name|id
parameter_list|,
name|DateTimeField
name|field
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|field
specifier|public
name|DateTimeField
name|field
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|resolve
specifier|public
specifier|static
name|DateTimeUnit
name|resolve
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
switch|switch
condition|(
name|id
condition|)
block|{
case|case
literal|1
case|:
return|return
name|WEEK_OF_WEEKYEAR
return|;
case|case
literal|2
case|:
return|return
name|YEAR_OF_CENTURY
return|;
case|case
literal|3
case|:
return|return
name|QUARTER
return|;
case|case
literal|4
case|:
return|return
name|MONTH_OF_YEAR
return|;
case|case
literal|5
case|:
return|return
name|DAY_OF_MONTH
return|;
case|case
literal|6
case|:
return|return
name|HOUR_OF_DAY
return|;
case|case
literal|7
case|:
return|return
name|MINUTES_OF_HOUR
return|;
case|case
literal|8
case|:
return|return
name|SECOND_OF_MINUTE
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unknown date time unit id ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

