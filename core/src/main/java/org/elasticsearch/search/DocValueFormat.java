begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|InetAddressPoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|geo
operator|.
name|GeoHashUtils
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
name|NamedWriteable
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
name|joda
operator|.
name|DateMathParser
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
name|FormatDateTimeFormatter
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
name|elasticsearch
operator|.
name|common
operator|.
name|network
operator|.
name|InetAddresses
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
name|network
operator|.
name|NetworkAddress
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|function
operator|.
name|LongSupplier
import|;
end_import

begin_comment
comment|/** A formatter for values as returned by the fielddata/doc-values APIs. */
end_comment

begin_interface
DECL|interface|DocValueFormat
specifier|public
interface|interface
name|DocValueFormat
extends|extends
name|NamedWriteable
block|{
comment|/** Format a long value. This is used by terms and histogram aggregations      *  to format keys for fields that use longs as a doc value representation      *  such as the {@code long} and {@code date} fields. */
DECL|method|format
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
function_decl|;
comment|/** Format a double value. This is used by terms and stats aggregations      *  to format keys for fields that use numbers as a doc value representation      *  such as the {@code long}, {@code double} or {@code date} fields. */
DECL|method|format
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
function_decl|;
comment|/** Format a double value. This is used by terms aggregations to format      *  keys for fields that use binary doc value representations such as the      *  {@code keyword} and {@code ip} fields. */
DECL|method|format
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
function_decl|;
comment|/** Parse a value that was formatted with {@link #format(long)} back to the      *  original long value. */
DECL|method|parseLong
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
function_decl|;
comment|/** Parse a value that was formatted with {@link #format(double)} back to      *  the original double value. */
DECL|method|parseDouble
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
function_decl|;
comment|/** Parse a value that was formatted with {@link #format(BytesRef)} back      *  to the original BytesRef. */
DECL|method|parseBytesRef
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
function_decl|;
DECL|field|RAW
name|DocValueFormat
name|RAW
init|=
operator|new
name|DocValueFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
literal|"raw"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|Double
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
return|return
name|value
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
name|double
name|d
init|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|roundUp
condition|)
block|{
name|d
operator|=
name|Math
operator|.
name|ceil
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|d
operator|=
name|Math
operator|.
name|floor
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|round
argument_list|(
name|d
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|DateTime
specifier|final
class|class
name|DateTime
implements|implements
name|DocValueFormat
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"date_time"
decl_stmt|;
DECL|field|formatter
specifier|final
name|FormatDateTimeFormatter
name|formatter
decl_stmt|;
DECL|field|timeZone
specifier|final
name|DateTimeZone
name|timeZone
decl_stmt|;
DECL|field|parser
specifier|private
specifier|final
name|DateMathParser
name|parser
decl_stmt|;
DECL|method|DateTime
specifier|public
name|DateTime
parameter_list|(
name|FormatDateTimeFormatter
name|formatter
parameter_list|,
name|DateTimeZone
name|timeZone
parameter_list|)
block|{
name|this
operator|.
name|formatter
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|formatter
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeZone
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|timeZone
argument_list|)
expr_stmt|;
name|this
operator|.
name|parser
operator|=
operator|new
name|DateMathParser
argument_list|(
name|formatter
argument_list|)
expr_stmt|;
block|}
DECL|method|DateTime
specifier|public
name|DateTime
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Joda
operator|.
name|forPattern
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
argument_list|,
name|DateTimeZone
operator|.
name|forID
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
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
name|writeString
argument_list|(
name|formatter
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|timeZone
operator|.
name|getID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|formatter
operator|.
name|printer
argument_list|()
operator|.
name|withZone
argument_list|(
name|timeZone
argument_list|)
operator|.
name|print
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|format
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|parseLong
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
return|return
name|parser
operator|.
name|parse
argument_list|(
name|value
argument_list|,
name|now
argument_list|,
name|roundUp
argument_list|,
name|timeZone
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseDouble
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
return|return
name|parseLong
argument_list|(
name|value
argument_list|,
name|roundUp
argument_list|,
name|now
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|parseBytesRef
specifier|public
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|field|GEOHASH
name|DocValueFormat
name|GEOHASH
init|=
operator|new
name|DocValueFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
literal|"geo_hash"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|GeoHashUtils
operator|.
name|stringEncode
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|format
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
DECL|field|BOOLEAN
name|DocValueFormat
name|BOOLEAN
init|=
operator|new
name|DocValueFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
literal|"bool"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|valueOf
argument_list|(
name|value
operator|!=
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|valueOf
argument_list|(
name|value
operator|!=
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|"false"
case|:
return|return
literal|0
return|;
case|case
literal|"true"
case|:
return|return
literal|1
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot parse boolean ["
operator|+
name|value
operator|+
literal|"], expected either [true] or [false]"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
return|return
name|parseLong
argument_list|(
name|value
argument_list|,
name|roundUp
argument_list|,
name|now
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
DECL|field|IP
name|DocValueFormat
name|IP
init|=
operator|new
name|DocValueFormat
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
literal|"ip"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{         }
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
name|byte
index|[]
name|bytes
init|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|value
operator|.
name|bytes
argument_list|,
name|value
operator|.
name|offset
argument_list|,
name|value
operator|.
name|offset
operator|+
name|value
operator|.
name|length
argument_list|)
decl_stmt|;
name|InetAddress
name|inet
init|=
name|InetAddressPoint
operator|.
name|decode
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
return|return
name|NetworkAddress
operator|.
name|format
argument_list|(
name|inet
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|BytesRef
argument_list|(
name|InetAddressPoint
operator|.
name|encode
argument_list|(
name|InetAddresses
operator|.
name|forString
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
decl_stmt|;
DECL|class|Decimal
specifier|final
class|class
name|Decimal
implements|implements
name|DocValueFormat
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"decimal"
decl_stmt|;
DECL|field|SYMBOLS
specifier|private
specifier|static
specifier|final
name|DecimalFormatSymbols
name|SYMBOLS
init|=
operator|new
name|DecimalFormatSymbols
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
decl_stmt|;
DECL|field|pattern
specifier|final
name|String
name|pattern
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|NumberFormat
name|format
decl_stmt|;
DECL|method|Decimal
specifier|public
name|Decimal
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|pattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|format
operator|=
operator|new
name|DecimalFormat
argument_list|(
name|pattern
argument_list|,
name|SYMBOLS
argument_list|)
expr_stmt|;
block|}
DECL|method|Decimal
specifier|public
name|Decimal
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
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
name|writeString
argument_list|(
name|pattern
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|format
operator|.
name|format
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|format
operator|.
name|format
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|BytesRef
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|parseLong
specifier|public
name|long
name|parseLong
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
name|Number
name|n
decl_stmt|;
try|try
block|{
name|n
operator|=
name|format
operator|.
name|parse
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|format
operator|.
name|isParseIntegerOnly
argument_list|()
condition|)
block|{
return|return
name|n
operator|.
name|longValue
argument_list|()
return|;
block|}
else|else
block|{
name|double
name|d
init|=
name|n
operator|.
name|doubleValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|roundUp
condition|)
block|{
name|d
operator|=
name|Math
operator|.
name|ceil
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|d
operator|=
name|Math
operator|.
name|floor
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|round
argument_list|(
name|d
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|parseDouble
specifier|public
name|double
name|parseDouble
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|roundUp
parameter_list|,
name|LongSupplier
name|now
parameter_list|)
block|{
name|Number
name|n
decl_stmt|;
try|try
block|{
name|n
operator|=
name|format
operator|.
name|parse
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|n
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|parseBytesRef
specifier|public
name|BytesRef
name|parseBytesRef
parameter_list|(
name|String
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|pattern
argument_list|)
return|;
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
block|{
return|return
literal|true
return|;
block|}
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
block|{
return|return
literal|false
return|;
block|}
name|Decimal
name|that
init|=
operator|(
name|Decimal
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|pattern
argument_list|,
name|that
operator|.
name|pattern
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

