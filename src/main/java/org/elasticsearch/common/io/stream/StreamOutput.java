begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|io
operator|.
name|stream
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|Nullable
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
name|bytes
operator|.
name|BytesReference
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
name|UTF8StreamWriter
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
name|text
operator|.
name|Text
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
name|ReadableInstant
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|SoftReference
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
name|LinkedHashMap
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
comment|/**  *  */
end_comment

begin_class
DECL|class|StreamOutput
specifier|public
specifier|abstract
class|class
name|StreamOutput
extends|extends
name|OutputStream
block|{
DECL|field|utf8StreamWriter
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|SoftReference
argument_list|<
name|UTF8StreamWriter
argument_list|>
argument_list|>
name|utf8StreamWriter
init|=
operator|new
name|ThreadLocal
argument_list|<
name|SoftReference
argument_list|<
name|UTF8StreamWriter
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|utf8StreamWriter
specifier|public
specifier|static
name|UTF8StreamWriter
name|utf8StreamWriter
parameter_list|()
block|{
name|SoftReference
argument_list|<
name|UTF8StreamWriter
argument_list|>
name|ref
init|=
name|utf8StreamWriter
operator|.
name|get
argument_list|()
decl_stmt|;
name|UTF8StreamWriter
name|writer
init|=
operator|(
name|ref
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|ref
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|writer
operator|==
literal|null
condition|)
block|{
name|writer
operator|=
operator|new
name|UTF8StreamWriter
argument_list|(
literal|1024
operator|*
literal|4
argument_list|)
expr_stmt|;
name|utf8StreamWriter
operator|.
name|set
argument_list|(
operator|new
name|SoftReference
argument_list|<
name|UTF8StreamWriter
argument_list|>
argument_list|(
name|writer
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|reset
argument_list|()
expr_stmt|;
return|return
name|writer
return|;
block|}
DECL|field|version
specifier|private
name|Version
name|version
init|=
name|Version
operator|.
name|CURRENT
decl_stmt|;
DECL|method|getVersion
specifier|public
name|Version
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|setVersion
specifier|public
name|StreamOutput
name|setVersion
parameter_list|(
name|Version
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|seekPositionSupported
specifier|public
name|boolean
name|seekPositionSupported
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|position
specifier|public
name|long
name|position
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|/**      * Writes a single byte.      */
DECL|method|writeByte
specifier|public
specifier|abstract
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes an array of bytes.      *      * @param b the bytes to write      */
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes an array of bytes.      *      * @param b      the bytes to write      * @param length the number of bytes to write      */
DECL|method|writeBytes
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes an array of bytes.      *      * @param b      the bytes to write      * @param offset the offset in the byte array      * @param length the number of bytes to write      */
DECL|method|writeBytes
specifier|public
specifier|abstract
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes the bytes reference, including a length header.      */
DECL|method|writeBytesReference
specifier|public
name|void
name|writeBytesReference
parameter_list|(
annotation|@
name|Nullable
name|BytesReference
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
name|writeVInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|writeTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|writeBytesRef
specifier|public
name|void
name|writeBytesRef
parameter_list|(
name|BytesRef
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
name|writeVInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|write
argument_list|(
name|bytes
operator|.
name|bytes
argument_list|,
name|bytes
operator|.
name|offset
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|writeShort
specifier|public
specifier|final
name|void
name|writeShort
parameter_list|(
name|short
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|v
operator|>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|v
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes an int as four bytes.      */
DECL|method|writeInt
specifier|public
name|void
name|writeInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|24
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
name|i
operator|>>
literal|8
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes an int in a variable-length format.  Writes between one and      * five bytes.  Smaller values take fewer bytes.  Negative numbers      * will always use all 5 bytes and are therefore better serialized      * using {@link #writeInt}      */
DECL|method|writeVInt
specifier|public
name|void
name|writeVInt
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7F
operator|)
operator|!=
literal|0
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7f
operator|)
operator||
literal|0x80
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes a long as eight bytes.      */
DECL|method|writeLong
specifier|public
name|void
name|writeLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|i
operator|>>
literal|32
argument_list|)
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
operator|(
name|int
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**      * Writes an long in a variable-length format.  Writes between one and nine      * bytes.  Smaller values take fewer bytes.  Negative numbers are not      * supported.      */
DECL|method|writeVLong
specifier|public
name|void
name|writeVLong
parameter_list|(
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
assert|assert
name|i
operator|>=
literal|0
assert|;
while|while
condition|(
operator|(
name|i
operator|&
operator|~
literal|0x7F
operator|)
operator|!=
literal|0
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
literal|0x7f
operator|)
operator||
literal|0x80
argument_list|)
argument_list|)
expr_stmt|;
name|i
operator|>>>=
literal|7
expr_stmt|;
block|}
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
DECL|method|writeOptionalString
specifier|public
name|void
name|writeOptionalString
parameter_list|(
annotation|@
name|Nullable
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writeString
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeOptionalSharedString
specifier|public
name|void
name|writeOptionalSharedString
parameter_list|(
annotation|@
name|Nullable
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|writeSharedString
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeOptionalText
specifier|public
name|void
name|writeOptionalText
parameter_list|(
annotation|@
name|Nullable
name|Text
name|text
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|text
operator|==
literal|null
condition|)
block|{
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeText
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeText
specifier|public
name|void
name|writeText
parameter_list|(
name|Text
name|text
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|text
operator|.
name|hasBytes
argument_list|()
operator|&&
name|seekPositionSupported
argument_list|()
condition|)
block|{
name|long
name|pos1
init|=
name|position
argument_list|()
decl_stmt|;
comment|// make room for the size
name|seek
argument_list|(
name|pos1
operator|+
literal|4
argument_list|)
expr_stmt|;
name|UTF8StreamWriter
name|utf8StreamWriter
init|=
name|utf8StreamWriter
argument_list|()
decl_stmt|;
name|utf8StreamWriter
operator|.
name|setOutput
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|utf8StreamWriter
operator|.
name|write
argument_list|(
name|text
operator|.
name|string
argument_list|()
argument_list|)
expr_stmt|;
name|utf8StreamWriter
operator|.
name|close
argument_list|()
expr_stmt|;
name|long
name|pos2
init|=
name|position
argument_list|()
decl_stmt|;
name|seek
argument_list|(
name|pos1
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
call|(
name|int
call|)
argument_list|(
name|pos2
operator|-
name|pos1
operator|-
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|seek
argument_list|(
name|pos2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|BytesReference
name|bytes
init|=
name|text
operator|.
name|bytes
argument_list|()
decl_stmt|;
name|writeInt
argument_list|(
name|bytes
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|bytes
operator|.
name|writeTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeTextArray
specifier|public
name|void
name|writeTextArray
parameter_list|(
name|Text
index|[]
name|array
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Text
name|t
range|:
name|array
control|)
block|{
name|writeText
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeSharedText
specifier|public
name|void
name|writeSharedText
parameter_list|(
name|Text
name|text
parameter_list|)
throws|throws
name|IOException
block|{
name|writeText
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
DECL|method|writeString
specifier|public
name|void
name|writeString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|charCount
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|writeVInt
argument_list|(
name|charCount
argument_list|)
expr_stmt|;
name|int
name|c
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|charCount
condition|;
name|i
operator|++
control|)
block|{
name|c
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|<=
literal|0x007F
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0x07FF
condition|)
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
name|c
operator|>>
literal|12
operator|&
literal|0x0F
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
name|c
operator|>>
literal|6
operator|&
literal|0x3F
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
name|c
operator|>>
literal|0
operator|&
literal|0x3F
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
name|c
operator|>>
literal|6
operator|&
literal|0x1F
argument_list|)
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
name|c
operator|>>
literal|0
operator|&
literal|0x3F
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeSharedString
specifier|public
name|void
name|writeSharedString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|IOException
block|{
name|writeString
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
DECL|method|writeFloat
specifier|public
name|void
name|writeFloat
parameter_list|(
name|float
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeInt
argument_list|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeDouble
specifier|public
name|void
name|writeDouble
parameter_list|(
name|double
name|v
parameter_list|)
throws|throws
name|IOException
block|{
name|writeLong
argument_list|(
name|Double
operator|.
name|doubleToLongBits
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|ZERO
specifier|private
specifier|static
name|byte
name|ZERO
init|=
literal|0
decl_stmt|;
DECL|field|ONE
specifier|private
specifier|static
name|byte
name|ONE
init|=
literal|1
decl_stmt|;
DECL|field|TWO
specifier|private
specifier|static
name|byte
name|TWO
init|=
literal|2
decl_stmt|;
comment|/**      * Writes a boolean.      */
DECL|method|writeBoolean
specifier|public
name|void
name|writeBoolean
parameter_list|(
name|boolean
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
name|b
condition|?
name|ONE
else|:
name|ZERO
argument_list|)
expr_stmt|;
block|}
DECL|method|writeOptionalBoolean
specifier|public
name|void
name|writeOptionalBoolean
parameter_list|(
annotation|@
name|Nullable
name|Boolean
name|b
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
name|writeByte
argument_list|(
name|TWO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeByte
argument_list|(
name|b
condition|?
name|ONE
else|:
name|ZERO
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Forces any buffered output to be written.      */
DECL|method|flush
specifier|public
specifier|abstract
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Closes this stream to further operations.      */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|reset
specifier|public
specifier|abstract
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
function_decl|;
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|writeBytes
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
DECL|method|writeStringArray
specifier|public
name|void
name|writeStringArray
parameter_list|(
name|String
index|[]
name|array
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|array
control|)
block|{
name|writeString
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Writes a string array, for nullable string, writes it as 0 (empty string).      */
DECL|method|writeStringArrayNullable
specifier|public
name|void
name|writeStringArrayNullable
parameter_list|(
annotation|@
name|Nullable
name|String
index|[]
name|array
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|array
operator|==
literal|null
condition|)
block|{
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeVInt
argument_list|(
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|s
range|:
name|array
control|)
block|{
name|writeString
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|writeMap
specifier|public
name|void
name|writeMap
parameter_list|(
annotation|@
name|Nullable
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
name|writeGenericValue
argument_list|(
name|map
argument_list|)
expr_stmt|;
block|}
DECL|method|writeGenericValue
specifier|public
name|void
name|writeGenericValue
parameter_list|(
annotation|@
name|Nullable
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return;
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
expr_stmt|;
name|writeString
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
name|Integer
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
expr_stmt|;
name|writeInt
argument_list|(
operator|(
name|Integer
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
name|Long
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
operator|(
name|Long
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|3
argument_list|)
expr_stmt|;
name|writeFloat
argument_list|(
operator|(
name|Float
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
name|Double
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|4
argument_list|)
expr_stmt|;
name|writeDouble
argument_list|(
operator|(
name|Double
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
name|Boolean
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|5
argument_list|)
expr_stmt|;
name|writeBoolean
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|6
argument_list|)
expr_stmt|;
name|writeVInt
argument_list|(
operator|(
operator|(
name|byte
index|[]
operator|)
name|value
operator|)
operator|.
name|length
argument_list|)
expr_stmt|;
name|writeBytes
argument_list|(
operator|(
operator|(
name|byte
index|[]
operator|)
name|value
operator|)
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|7
argument_list|)
expr_stmt|;
name|List
name|list
init|=
operator|(
name|List
operator|)
name|value
decl_stmt|;
name|writeVInt
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
name|writeGenericValue
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Object
index|[]
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|8
argument_list|)
expr_stmt|;
name|Object
index|[]
name|list
init|=
operator|(
name|Object
index|[]
operator|)
name|value
decl_stmt|;
name|writeVInt
argument_list|(
name|list
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
name|writeGenericValue
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|Map
condition|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|LinkedHashMap
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|9
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|10
argument_list|)
expr_stmt|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|value
decl_stmt|;
name|writeVInt
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writeSharedString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|writeGenericValue
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|Byte
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|11
argument_list|)
expr_stmt|;
name|writeByte
argument_list|(
operator|(
name|Byte
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|12
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
operator|(
operator|(
name|Date
operator|)
name|value
operator|)
operator|.
name|getTime
argument_list|()
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|13
argument_list|)
expr_stmt|;
name|writeLong
argument_list|(
operator|(
operator|(
name|ReadableInstant
operator|)
name|value
operator|)
operator|.
name|getMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|BytesReference
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|14
argument_list|)
expr_stmt|;
name|writeBytesReference
argument_list|(
operator|(
name|BytesReference
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
name|Text
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|15
argument_list|)
expr_stmt|;
name|writeText
argument_list|(
operator|(
name|Text
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
name|Short
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|16
argument_list|)
expr_stmt|;
name|writeShort
argument_list|(
operator|(
name|Short
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
name|int
index|[]
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|17
argument_list|)
expr_stmt|;
name|writePrimitiveIntArray
argument_list|(
operator|(
name|int
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
name|long
index|[]
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|18
argument_list|)
expr_stmt|;
name|writePrimitiveLongArray
argument_list|(
operator|(
name|long
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
name|float
index|[]
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|19
argument_list|)
expr_stmt|;
name|writePrimitiveFloatArray
argument_list|(
operator|(
name|float
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
name|double
index|[]
operator|.
name|class
condition|)
block|{
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
literal|20
argument_list|)
expr_stmt|;
name|writePrimitiveDoubleArray
argument_list|(
operator|(
name|double
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
literal|"Can't write type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|writePrimitiveIntArray
specifier|private
name|void
name|writePrimitiveIntArray
parameter_list|(
name|int
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeInt
argument_list|(
name|value
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writePrimitiveLongArray
specifier|private
name|void
name|writePrimitiveLongArray
parameter_list|(
name|long
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeLong
argument_list|(
name|value
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writePrimitiveFloatArray
specifier|private
name|void
name|writePrimitiveFloatArray
parameter_list|(
name|float
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeFloat
argument_list|(
name|value
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writePrimitiveDoubleArray
specifier|private
name|void
name|writePrimitiveDoubleArray
parameter_list|(
name|double
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVInt
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeDouble
argument_list|(
name|value
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Serializes a potential null value.      */
DECL|method|writeOptionalStreamable
specifier|public
name|void
name|writeOptionalStreamable
parameter_list|(
annotation|@
name|Nullable
name|Streamable
name|streamable
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|streamable
operator|!=
literal|null
condition|)
block|{
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|streamable
operator|.
name|writeTo
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

