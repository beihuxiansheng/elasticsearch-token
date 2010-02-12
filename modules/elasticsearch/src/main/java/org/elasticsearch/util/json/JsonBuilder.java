begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
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
name|util
operator|.
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|NotThreadSafe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|io
operator|.
name|FastCharArrayWriter
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
DECL|class|JsonBuilder
specifier|public
class|class
name|JsonBuilder
block|{
comment|/**      * A thread local based cache of {@link JsonBuilder}.      */
DECL|class|Cached
specifier|public
specifier|static
class|class
name|Cached
block|{
DECL|field|generator
specifier|private
name|JsonBuilder
name|generator
decl_stmt|;
DECL|method|Cached
specifier|public
name|Cached
parameter_list|(
name|JsonBuilder
name|generator
parameter_list|)
block|{
name|this
operator|.
name|generator
operator|=
name|generator
expr_stmt|;
block|}
DECL|field|cache
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Cached
argument_list|>
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Cached
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Cached
name|initialValue
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|Cached
argument_list|(
operator|new
name|JsonBuilder
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchException
argument_list|(
literal|"Failed to create json generator"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
decl_stmt|;
comment|/**          * Returns the cached thread local generator, with its internal {@link StringBuilder} cleared.          */
DECL|method|cached
specifier|public
specifier|static
name|JsonBuilder
name|cached
parameter_list|()
throws|throws
name|IOException
block|{
name|Cached
name|cached
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
name|cached
operator|.
name|generator
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|cached
operator|.
name|generator
return|;
block|}
DECL|method|cachedNoReset
specifier|public
specifier|static
name|JsonBuilder
name|cachedNoReset
parameter_list|()
block|{
name|Cached
name|cached
init|=
name|cache
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|cached
operator|.
name|generator
return|;
block|}
block|}
DECL|method|cached
specifier|public
specifier|static
name|JsonBuilder
name|cached
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Cached
operator|.
name|cached
argument_list|()
return|;
block|}
DECL|field|writer
specifier|private
specifier|final
name|FastCharArrayWriter
name|writer
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|JsonFactory
name|factory
decl_stmt|;
DECL|field|generator
specifier|private
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|JsonGenerator
name|generator
decl_stmt|;
DECL|field|utf8Result
specifier|final
name|UnicodeUtil
operator|.
name|UTF8Result
name|utf8Result
init|=
operator|new
name|UnicodeUtil
operator|.
name|UTF8Result
argument_list|()
decl_stmt|;
DECL|method|JsonBuilder
specifier|public
name|JsonBuilder
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|Jackson
operator|.
name|defaultJsonFactory
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonBuilder
specifier|public
name|JsonBuilder
parameter_list|(
name|JsonFactory
name|factory
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|writer
operator|=
operator|new
name|FastCharArrayWriter
argument_list|()
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|generator
operator|=
name|factory
operator|.
name|createJsonGenerator
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
DECL|method|prettyPrint
specifier|public
name|JsonBuilder
name|prettyPrint
parameter_list|()
block|{
name|generator
operator|.
name|useDefaultPrettyPrinter
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|startJsonp
specifier|public
name|JsonBuilder
name|startJsonp
parameter_list|(
name|String
name|callback
parameter_list|)
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
name|callback
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|endJsonp
specifier|public
name|JsonBuilder
name|endJsonp
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|");"
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|startObject
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|startObject
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|endObject
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|startArray
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|startArray
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|endArray
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
name|field
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|this
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|field
specifier|public
name|JsonBuilder
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
name|generator
operator|.
name|writeFieldName
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
name|this
return|;
block|}
DECL|method|nullField
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|binary
specifier|public
name|JsonBuilder
name|binary
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeBinary
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|raw
specifier|public
name|JsonBuilder
name|raw
parameter_list|(
name|String
name|json
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeRaw
argument_list|(
name|json
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|string
specifier|public
name|JsonBuilder
name|string
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
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
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
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
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
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
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
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
parameter_list|(
name|Integer
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
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
parameter_list|(
name|Long
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
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
parameter_list|(
name|Float
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
operator|.
name|floatValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|number
specifier|public
name|JsonBuilder
name|number
parameter_list|(
name|Double
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
operator|.
name|doubleValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|bool
specifier|public
name|JsonBuilder
name|bool
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
name|this
return|;
block|}
DECL|method|value
specifier|public
name|JsonBuilder
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
name|string
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
name|number
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
name|number
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
name|number
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
name|number
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
name|bool
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
name|binary
argument_list|(
operator|(
name|byte
index|[]
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
name|this
return|;
block|}
DECL|method|flush
specifier|public
name|JsonBuilder
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
name|this
return|;
block|}
DECL|method|reset
specifier|public
name|JsonBuilder
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|writer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|generator
operator|=
name|factory
operator|.
name|createJsonGenerator
argument_list|(
name|writer
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|string
specifier|public
name|String
name|string
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|toStringTrim
argument_list|()
return|;
block|}
comment|/**      * Returns the byte[] that represents the utf8 of the json written up until now.      * Note, the result is shared within this instance, so copy the byte array if needed      * or use {@link #utf8copied()}.      */
DECL|method|utf8
specifier|public
name|UnicodeUtil
operator|.
name|UTF8Result
name|utf8
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
comment|// ignore whitepsaces
name|int
name|st
init|=
literal|0
decl_stmt|;
name|int
name|len
init|=
name|writer
operator|.
name|size
argument_list|()
decl_stmt|;
name|char
index|[]
name|val
init|=
name|writer
operator|.
name|unsafeCharArray
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|st
operator|<
name|len
operator|)
operator|&&
operator|(
name|val
index|[
name|st
index|]
operator|<=
literal|' '
operator|)
condition|)
block|{
name|st
operator|++
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|st
operator|<
name|len
operator|)
operator|&&
operator|(
name|val
index|[
name|len
operator|-
literal|1
index|]
operator|<=
literal|' '
operator|)
condition|)
block|{
name|len
operator|--
expr_stmt|;
block|}
name|UnicodeUtil
operator|.
name|UTF16toUTF8
argument_list|(
name|val
argument_list|,
name|st
argument_list|,
name|len
argument_list|,
name|utf8Result
argument_list|)
expr_stmt|;
return|return
name|utf8Result
return|;
block|}
comment|/**      * Returns a copied byte[] that represnts the utf8 o fthe json written up until now.      */
DECL|method|utf8copied
specifier|public
name|byte
index|[]
name|utf8copied
parameter_list|()
throws|throws
name|IOException
block|{
name|utf8
argument_list|()
expr_stmt|;
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|utf8Result
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|utf8Result
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|utf8Result
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
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
block|}
end_class

end_unit

