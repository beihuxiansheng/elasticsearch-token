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
name|BytesHolder
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BytesStreamInput
specifier|public
class|class
name|BytesStreamInput
extends|extends
name|StreamInput
block|{
DECL|field|buf
specifier|protected
name|byte
name|buf
index|[]
decl_stmt|;
DECL|field|pos
specifier|protected
name|int
name|pos
decl_stmt|;
DECL|field|count
specifier|protected
name|int
name|count
decl_stmt|;
DECL|field|unsafe
specifier|private
specifier|final
name|boolean
name|unsafe
decl_stmt|;
DECL|method|BytesStreamInput
specifier|public
name|BytesStreamInput
parameter_list|(
name|byte
name|buf
index|[]
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|this
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|,
name|unsafe
argument_list|)
expr_stmt|;
block|}
DECL|method|BytesStreamInput
specifier|public
name|BytesStreamInput
parameter_list|(
name|byte
name|buf
index|[]
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|unsafe
parameter_list|)
block|{
name|this
operator|.
name|buf
operator|=
name|buf
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|Math
operator|.
name|min
argument_list|(
name|offset
operator|+
name|length
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|unsafe
operator|=
name|unsafe
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readBytesReference
specifier|public
name|BytesHolder
name|readBytesReference
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|unsafe
condition|)
block|{
return|return
name|readBytesHolder
argument_list|()
return|;
block|}
name|int
name|size
init|=
name|readVInt
argument_list|()
decl_stmt|;
name|BytesHolder
name|bytes
init|=
operator|new
name|BytesHolder
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|size
expr_stmt|;
return|return
name|bytes
return|;
block|}
annotation|@
name|Override
DECL|method|skip
specifier|public
name|long
name|skip
parameter_list|(
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|+
name|n
operator|>
name|count
condition|)
block|{
name|n
operator|=
name|count
operator|-
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|<
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|pos
operator|+=
name|n
expr_stmt|;
return|return
name|n
return|;
block|}
DECL|method|position
specifier|public
name|int
name|position
parameter_list|()
block|{
return|return
name|this
operator|.
name|pos
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|(
name|pos
operator|<
name|count
operator|)
condition|?
operator|(
name|buf
index|[
name|pos
operator|++
index|]
operator|&
literal|0xff
operator|)
else|:
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
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
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
elseif|else
if|if
condition|(
name|off
operator|<
literal|0
operator|||
name|len
argument_list|<
literal|0
operator|||
name|len
argument_list|>
name|b
operator|.
name|length
operator|-
name|off
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
if|if
condition|(
name|pos
operator|>=
name|count
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|pos
operator|+
name|len
operator|>
name|count
condition|)
block|{
name|len
operator|=
name|count
operator|-
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|<=
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
return|return
name|len
return|;
block|}
DECL|method|underlyingBuffer
specifier|public
name|byte
index|[]
name|underlyingBuffer
parameter_list|()
block|{
return|return
name|buf
return|;
block|}
annotation|@
name|Override
DECL|method|readByte
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|pos
operator|>=
name|count
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
return|return
name|buf
index|[
name|pos
operator|++
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|readBytes
specifier|public
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
block|{
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|pos
operator|>=
name|count
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
if|if
condition|(
name|pos
operator|+
name|len
operator|>
name|count
condition|)
block|{
name|len
operator|=
name|count
operator|-
name|pos
expr_stmt|;
block|}
if|if
condition|(
name|len
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|buf
argument_list|,
name|pos
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|len
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|pos
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do here...
block|}
block|}
end_class

end_unit

