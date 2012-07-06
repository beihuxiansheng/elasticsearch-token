begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|bytes
operator|.
name|BytesArray
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
name|joda
operator|.
name|time
operator|.
name|DateTime
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
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|StreamInput
specifier|public
specifier|abstract
class|class
name|StreamInput
extends|extends
name|InputStream
block|{
comment|/**      * Reads and returns a single byte.      */
DECL|method|readByte
specifier|public
specifier|abstract
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Reads a specified number of bytes into an array at the specified offset.      *      * @param b      the array to read bytes into      * @param offset the offset in the array to start storing bytes      * @param len    the number of bytes to read      */
DECL|method|readBytes
specifier|public
specifier|abstract
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Reads a bytes reference from this stream, might hold an actual reference to the underlying      * bytes of the stream.      */
DECL|method|readBytesReference
specifier|public
name|BytesReference
name|readBytesReference
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readBytesReference
argument_list|(
name|readVInt
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Reads a bytes reference from this stream, might hold an actual reference to the underlying      * bytes of the stream.      */
DECL|method|readBytesReference
specifier|public
name|BytesReference
name|readBytesReference
parameter_list|(
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|BytesArray
operator|.
name|EMPTY
return|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|readBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesArray
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
return|;
block|}
DECL|method|readFully
specifier|public
name|void
name|readFully
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|readBytes
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
DECL|method|readShort
specifier|public
name|short
name|readShort
parameter_list|()
throws|throws
name|IOException
block|{
return|return
call|(
name|short
call|)
argument_list|(
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
argument_list|)
return|;
block|}
comment|/**      * Reads four bytes and returns an int.      */
DECL|method|readInt
specifier|public
name|int
name|readInt
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0xFF
operator|)
return|;
block|}
comment|/**      * Reads an int stored in variable-length format.  Reads between one and      * five bytes.  Smaller values take fewer bytes.  Negative numbers are not      * supported.      */
DECL|method|readVInt
specifier|public
name|int
name|readVInt
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|b
operator|&
literal|0x7F
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
assert|;
return|return
name|i
operator||
operator|(
operator|(
name|b
operator|&
literal|0x7F
operator|)
operator|<<
literal|28
operator|)
return|;
block|}
comment|/**      * Reads eight bytes and returns a long.      */
DECL|method|readLong
specifier|public
name|long
name|readLong
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
operator|(
operator|(
name|long
operator|)
name|readInt
argument_list|()
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
name|readInt
argument_list|()
operator|&
literal|0xFFFFFFFFL
operator|)
return|;
block|}
comment|/**      * Reads a long stored in variable-length format.  Reads between one and      * nine bytes.  Smaller values take fewer bytes.  Negative numbers are not      * supported.      */
DECL|method|readVLong
specifier|public
name|long
name|readVLong
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|b
init|=
name|readByte
argument_list|()
decl_stmt|;
name|long
name|i
init|=
name|b
operator|&
literal|0x7FL
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|7
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|14
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|21
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|28
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|35
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|42
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
name|i
operator||=
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|49
expr_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
return|return
name|i
return|;
name|b
operator|=
name|readByte
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
assert|;
return|return
name|i
operator||
operator|(
operator|(
name|b
operator|&
literal|0x7FL
operator|)
operator|<<
literal|56
operator|)
return|;
block|}
annotation|@
name|Nullable
DECL|method|readOptionalUTF
specifier|public
name|String
name|readOptionalUTF
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|readBoolean
argument_list|()
condition|)
block|{
return|return
name|readUTF
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|readUTF
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|charCount
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|char
index|[]
name|chars
init|=
name|CachedStreamInput
operator|.
name|getCharArray
argument_list|(
name|charCount
argument_list|)
decl_stmt|;
name|int
name|c
decl_stmt|,
name|charIndex
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|charIndex
operator|<
name|charCount
condition|)
block|{
name|c
operator|=
name|readByte
argument_list|()
operator|&
literal|0xff
expr_stmt|;
switch|switch
condition|(
name|c
operator|>>
literal|4
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
case|case
literal|6
case|:
case|case
literal|7
case|:
name|chars
index|[
name|charIndex
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
break|break;
case|case
literal|12
case|:
case|case
literal|13
case|:
name|chars
index|[
name|charIndex
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|c
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator||
name|readByte
argument_list|()
operator|&
literal|0x3F
argument_list|)
expr_stmt|;
break|break;
case|case
literal|14
case|:
name|chars
index|[
name|charIndex
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|c
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator||
operator|(
name|readByte
argument_list|()
operator|&
literal|0x3F
operator|)
operator|<<
literal|0
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|charCount
argument_list|)
return|;
block|}
DECL|method|readFloat
specifier|public
specifier|final
name|float
name|readFloat
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|readInt
argument_list|()
argument_list|)
return|;
block|}
DECL|method|readDouble
specifier|public
specifier|final
name|double
name|readDouble
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|readLong
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Reads a boolean.      */
DECL|method|readBoolean
specifier|public
specifier|final
name|boolean
name|readBoolean
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|readByte
argument_list|()
operator|!=
literal|0
return|;
block|}
comment|/**      * Resets the stream.      */
DECL|method|reset
specifier|public
specifier|abstract
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Closes the stream to further operations.      */
DECL|method|close
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|//    // IS
comment|//
comment|//    @Override public int read() throws IOException {
comment|//        return readByte();
comment|//    }
comment|//
comment|//    // Here, we assume that we always can read the full byte array
comment|//
comment|//    @Override public int read(byte[] b, int off, int len) throws IOException {
comment|//        readBytes(b, off, len);
comment|//        return len;
comment|//    }
DECL|method|readStringArray
specifier|public
name|String
index|[]
name|readStringArray
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|size
init|=
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
return|return
name|Strings
operator|.
name|EMPTY_ARRAY
return|;
block|}
name|String
index|[]
name|ret
init|=
operator|new
name|String
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
name|readUTF
argument_list|()
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Nullable
DECL|method|readMap
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readMap
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
operator|)
name|readGenericValue
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Nullable
DECL|method|readGenericValue
specifier|public
name|Object
name|readGenericValue
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
name|type
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|0
condition|)
block|{
return|return
name|readUTF
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|1
condition|)
block|{
return|return
name|readInt
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|2
condition|)
block|{
return|return
name|readLong
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|3
condition|)
block|{
return|return
name|readFloat
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|4
condition|)
block|{
return|return
name|readDouble
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|5
condition|)
block|{
return|return
name|readBoolean
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|6
condition|)
block|{
name|int
name|bytesSize
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|value
init|=
operator|new
name|byte
index|[
name|bytesSize
index|]
decl_stmt|;
name|readFully
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|7
condition|)
block|{
name|int
name|size
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|size
argument_list|)
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|readGenericValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|8
condition|)
block|{
name|int
name|size
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|Object
index|[]
name|list
init|=
operator|new
name|Object
index|[
name|size
index|]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|list
index|[
name|i
index|]
operator|=
name|readGenericValue
argument_list|()
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|9
operator|||
name|type
operator|==
literal|10
condition|)
block|{
name|int
name|size
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|Map
name|map
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|9
condition|)
block|{
name|map
operator|=
operator|new
name|LinkedHashMap
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|readUTF
argument_list|()
argument_list|,
name|readGenericValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|11
condition|)
block|{
return|return
name|readByte
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|12
condition|)
block|{
return|return
operator|new
name|Date
argument_list|(
name|readLong
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|13
condition|)
block|{
return|return
operator|new
name|DateTime
argument_list|(
name|readLong
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't read unknown type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

