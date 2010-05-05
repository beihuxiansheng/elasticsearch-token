begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.io.stream
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|ThreadLocals
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
name|Unicode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
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
DECL|field|cachedBytes
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|>
name|cachedBytes
init|=
operator|new
name|ThreadLocal
argument_list|<
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|byte
index|[]
argument_list|>
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|ThreadLocals
operator|.
name|CleanableValue
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|(
operator|new
name|byte
index|[
literal|256
index|]
argument_list|)
return|;
block|}
block|}
decl_stmt|;
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
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
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
name|shift
expr_stmt|;
block|}
return|return
name|i
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
literal|0x7F
decl_stmt|;
for|for
control|(
name|int
name|shift
init|=
literal|7
init|;
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|!=
literal|0
condition|;
name|shift
operator|+=
literal|7
control|)
block|{
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
name|shift
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
comment|/**      * Reads a string.      */
DECL|method|readUTF
specifier|public
name|String
name|readUTF
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|cachedBytes
operator|.
name|get
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|length
operator|>
name|bytes
operator|.
name|length
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
call|(
name|int
call|)
argument_list|(
name|length
operator|*
literal|1.25
argument_list|)
index|]
expr_stmt|;
name|cachedBytes
operator|.
name|get
argument_list|()
operator|.
name|set
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
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
name|Unicode
operator|.
name|fromBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
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
name|byte
name|ch
init|=
name|readByte
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|<
literal|0
condition|)
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
return|return
operator|(
name|ch
operator|!=
literal|0
operator|)
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
block|}
end_class

end_unit

