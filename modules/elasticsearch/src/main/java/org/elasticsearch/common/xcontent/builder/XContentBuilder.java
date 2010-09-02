begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.builder
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|builder
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
name|elasticsearch
operator|.
name|common
operator|.
name|joda
operator|.
name|time
operator|.
name|ReadableInstant
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
name|elasticsearch
operator|.
name|common
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
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentGenerator
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
name|xcontent
operator|.
name|XContentType
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
name|xcontent
operator|.
name|support
operator|.
name|XContentMapConverter
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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentBuilder
specifier|public
specifier|abstract
class|class
name|XContentBuilder
parameter_list|<
name|T
extends|extends
name|XContentBuilder
parameter_list|>
block|{
DECL|enum|FieldCaseConversion
specifier|public
specifier|static
enum|enum
name|FieldCaseConversion
block|{
comment|/**          * No came conversion will occur.          */
DECL|enum constant|NONE
name|NONE
block|,
comment|/**          * Camel Case will be converted to Underscore casing.          */
DECL|enum constant|UNDERSCORE
name|UNDERSCORE
block|,
comment|/**          * Underscore will be converted to Camel case conversion.          */
DECL|enum constant|CAMELCASE
name|CAMELCASE
block|}
DECL|field|defaultDatePrinter
specifier|public
specifier|final
specifier|static
name|DateTimeFormatter
name|defaultDatePrinter
init|=
name|ISODateTimeFormat
operator|.
name|dateTime
argument_list|()
operator|.
name|withZone
argument_list|(
name|DateTimeZone
operator|.
name|UTC
argument_list|)
decl_stmt|;
DECL|field|globalFieldCaseConversion
specifier|protected
specifier|static
name|FieldCaseConversion
name|globalFieldCaseConversion
init|=
name|FieldCaseConversion
operator|.
name|NONE
decl_stmt|;
DECL|method|globalFieldCaseConversion
specifier|public
specifier|static
name|void
name|globalFieldCaseConversion
parameter_list|(
name|FieldCaseConversion
name|globalFieldCaseConversion
parameter_list|)
block|{
name|XContentBuilder
operator|.
name|globalFieldCaseConversion
operator|=
name|globalFieldCaseConversion
expr_stmt|;
block|}
DECL|field|generator
specifier|protected
name|XContentGenerator
name|generator
decl_stmt|;
DECL|field|builder
specifier|protected
name|T
name|builder
decl_stmt|;
DECL|field|fieldCaseConversion
specifier|protected
name|FieldCaseConversion
name|fieldCaseConversion
init|=
name|globalFieldCaseConversion
decl_stmt|;
DECL|field|cachedStringBuilder
specifier|protected
name|StringBuilder
name|cachedStringBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|method|fieldCaseConversion
specifier|public
name|T
name|fieldCaseConversion
parameter_list|(
name|FieldCaseConversion
name|fieldCaseConversion
parameter_list|)
block|{
name|this
operator|.
name|fieldCaseConversion
operator|=
name|fieldCaseConversion
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|contentType
specifier|public
name|XContentType
name|contentType
parameter_list|()
block|{
return|return
name|generator
operator|.
name|contentType
argument_list|()
return|;
block|}
DECL|method|prettyPrint
specifier|public
name|T
name|prettyPrint
parameter_list|()
block|{
name|generator
operator|.
name|usePrettyPrint
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|startObject
specifier|public
name|T
name|startObject
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|startObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|startObject
specifier|public
name|T
name|startObject
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|endObject
specifier|public
name|T
name|endObject
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|array
specifier|public
name|T
name|array
parameter_list|(
name|String
name|name
parameter_list|,
name|String
modifier|...
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|startArray
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|array
specifier|public
name|T
name|array
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
modifier|...
name|values
parameter_list|)
throws|throws
name|IOException
block|{
name|startArray
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|value
range|:
name|values
control|)
block|{
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|startArray
specifier|public
name|T
name|startArray
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|startArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|startArray
specifier|public
name|T
name|startArray
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|endArray
specifier|public
name|T
name|endArray
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fieldCaseConversion
operator|==
name|FieldCaseConversion
operator|.
name|UNDERSCORE
condition|)
block|{
name|name
operator|=
name|Strings
operator|.
name|toUnderscoreCase
argument_list|(
name|name
argument_list|,
name|cachedStringBuilder
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fieldCaseConversion
operator|==
name|FieldCaseConversion
operator|.
name|CAMELCASE
condition|)
block|{
name|name
operator|=
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|name
argument_list|,
name|cachedStringBuilder
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeFieldName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|char
index|[]
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Integer
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|field
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|field
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|field
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|field
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|startArray
argument_list|(
name|name
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|value
control|)
block|{
name|value
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|nullField
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
name|Class
name|type
init|=
name|value
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Float
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Double
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Long
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
operator|(
name|Boolean
operator|)
name|value
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Date
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
name|Date
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|ReadableInstant
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
name|ReadableInstant
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|List
condition|)
block|{
name|field
argument_list|(
name|name
argument_list|,
operator|(
name|List
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|field
argument_list|(
name|name
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeBinary
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|ReadableInstant
name|date
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|value
argument_list|(
name|date
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|ReadableInstant
name|date
parameter_list|,
name|DateTimeFormatter
name|formatter
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|value
argument_list|(
name|date
argument_list|,
name|formatter
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|value
argument_list|(
name|date
argument_list|)
return|;
block|}
DECL|method|field
specifier|public
name|T
name|field
parameter_list|(
name|String
name|name
parameter_list|,
name|Date
name|date
parameter_list|,
name|DateTimeFormatter
name|formatter
parameter_list|)
throws|throws
name|IOException
block|{
name|field
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|value
argument_list|(
name|date
argument_list|,
name|formatter
argument_list|)
return|;
block|}
DECL|method|nullField
specifier|public
name|T
name|nullField
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNullField
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|nullValue
specifier|public
name|T
name|nullValue
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|rawField
specifier|public
name|T
name|rawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRawFieldStart
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|raw
argument_list|(
name|content
argument_list|)
return|;
block|}
DECL|method|rawField
specifier|public
name|T
name|rawField
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|InputStream
name|content
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRawFieldStart
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
return|return
name|raw
argument_list|(
name|content
argument_list|)
return|;
block|}
DECL|method|raw
specifier|public
specifier|abstract
name|T
name|raw
parameter_list|(
name|byte
index|[]
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|raw
specifier|public
specifier|abstract
name|T
name|raw
parameter_list|(
name|InputStream
name|content
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|value
operator|.
name|booleanValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|boolean
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|ReadableInstant
name|date
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|date
argument_list|,
name|defaultDatePrinter
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|ReadableInstant
name|date
parameter_list|,
name|DateTimeFormatter
name|dateTimeFormatter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|dateTimeFormatter
operator|.
name|print
argument_list|(
name|date
argument_list|)
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Date
name|date
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|date
argument_list|,
name|defaultDatePrinter
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Date
name|date
parameter_list|,
name|DateTimeFormatter
name|dateTimeFormatter
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|dateTimeFormatter
operator|.
name|print
argument_list|(
name|date
operator|.
name|getTime
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Integer
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|int
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|long
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|value
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|float
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|value
argument_list|(
name|value
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|double
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeBinary
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|map
specifier|public
name|T
name|map
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentMapConverter
operator|.
name|writeMap
argument_list|(
name|generator
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentMapConverter
operator|.
name|writeMap
argument_list|(
name|generator
argument_list|,
name|map
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|value
specifier|public
name|T
name|value
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
name|type
init|=
name|value
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|String
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
operator|(
name|Float
operator|)
name|value
operator|)
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
operator|(
name|Double
operator|)
name|value
operator|)
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
operator|(
name|Integer
operator|)
name|value
operator|)
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
operator|(
name|Long
operator|)
name|value
operator|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
name|Boolean
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Date
operator|.
name|class
condition|)
block|{
name|value
argument_list|(
operator|(
name|Date
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|ReadableInstant
condition|)
block|{
name|value
argument_list|(
operator|(
name|ReadableInstant
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
name|value
argument_list|(
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Type not allowed ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|flush
specifier|public
name|T
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|generator
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
DECL|method|reset
specifier|public
specifier|abstract
name|T
name|reset
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|unsafeBytes
specifier|public
specifier|abstract
name|byte
index|[]
name|unsafeBytes
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|unsafeBytesLength
specifier|public
specifier|abstract
name|int
name|unsafeBytesLength
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|copiedBytes
specifier|public
specifier|abstract
name|byte
index|[]
name|copiedBytes
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|string
specifier|public
specifier|abstract
name|String
name|string
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

