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
name|joda
operator|.
name|time
operator|.
name|format
operator|.
name|DateTimeFormat
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
name|DateTimeFormatter
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

begin_enum
DECL|enum|DateFormat
enum|enum
name|DateFormat
block|{
DECL|enum constant|Iso8601
name|Iso8601
block|{
annotation|@
name|Override
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|getFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
return|return
name|ISODateTimeFormat
operator|.
name|dateTimeParser
argument_list|()
operator|.
name|withZone
argument_list|(
name|timezone
argument_list|)
operator|::
name|parseDateTime
return|;
block|}
block|}
block|,
DECL|enum constant|Unix
name|Unix
block|{
annotation|@
name|Override
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|getFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
return|return
parameter_list|(
name|date
parameter_list|)
lambda|->
operator|new
name|DateTime
argument_list|(
call|(
name|long
call|)
argument_list|(
name|Double
operator|.
name|parseDouble
argument_list|(
name|date
argument_list|)
operator|*
literal|1000
argument_list|)
argument_list|,
name|timezone
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|UnixMs
name|UnixMs
block|{
annotation|@
name|Override
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|getFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
return|return
parameter_list|(
name|date
parameter_list|)
lambda|->
operator|new
name|DateTime
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|date
argument_list|)
argument_list|,
name|timezone
argument_list|)
return|;
block|}
block|}
block|,
DECL|enum constant|Tai64n
name|Tai64n
block|{
annotation|@
name|Override
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|getFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
return|return
parameter_list|(
name|date
parameter_list|)
lambda|->
operator|new
name|DateTime
argument_list|(
name|parseMillis
argument_list|(
name|date
argument_list|)
argument_list|,
name|timezone
argument_list|)
return|;
block|}
specifier|private
name|long
name|parseMillis
parameter_list|(
name|String
name|date
parameter_list|)
block|{
if|if
condition|(
name|date
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|date
operator|=
name|date
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|long
name|base
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|date
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|16
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
comment|// 1356138046000
name|long
name|rest
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|date
operator|.
name|substring
argument_list|(
literal|16
argument_list|,
literal|24
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|base
operator|*
literal|1000
operator|)
operator|-
literal|10000
operator|)
operator|+
operator|(
name|rest
operator|/
literal|1000000
operator|)
return|;
block|}
block|}
block|,
DECL|enum constant|Joda
name|Joda
block|{
annotation|@
name|Override
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|getFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|DateTimeFormatter
name|parser
init|=
name|DateTimeFormat
operator|.
name|forPattern
argument_list|(
name|format
argument_list|)
operator|.
name|withZone
argument_list|(
name|timezone
argument_list|)
operator|.
name|withLocale
argument_list|(
name|locale
argument_list|)
decl_stmt|;
return|return
name|text
lambda|->
name|parser
operator|.
name|withDefaultYear
argument_list|(
operator|(
operator|new
name|DateTime
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
operator|)
operator|.
name|getYear
argument_list|()
argument_list|)
operator|.
name|parseDateTime
argument_list|(
name|text
argument_list|)
return|;
block|}
block|}
block|;
DECL|method|getFunction
specifier|abstract
name|Function
argument_list|<
name|String
argument_list|,
name|DateTime
argument_list|>
name|getFunction
parameter_list|(
name|String
name|format
parameter_list|,
name|DateTimeZone
name|timezone
parameter_list|,
name|Locale
name|locale
parameter_list|)
function_decl|;
DECL|method|fromString
specifier|static
name|DateFormat
name|fromString
parameter_list|(
name|String
name|format
parameter_list|)
block|{
switch|switch
condition|(
name|format
condition|)
block|{
case|case
literal|"ISO8601"
case|:
return|return
name|Iso8601
return|;
case|case
literal|"UNIX"
case|:
return|return
name|Unix
return|;
case|case
literal|"UNIX_MS"
case|:
return|return
name|UnixMs
return|;
case|case
literal|"TAI64N"
case|:
return|return
name|Tai64n
return|;
default|default:
return|return
name|Joda
return|;
block|}
block|}
block|}
end_enum

end_unit

