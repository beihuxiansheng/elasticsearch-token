begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
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
name|netty
operator|.
name|buffer
operator|.
name|ChannelBuffer
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
comment|/**  * A Netty {@link org.elasticsearch.common.netty.buffer.ChannelBuffer} based {@link org.elasticsearch.common.io.stream.StreamInput}.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|ChannelBufferStreamInput
specifier|public
class|class
name|ChannelBufferStreamInput
extends|extends
name|StreamInput
block|{
DECL|field|buffer
specifier|private
specifier|final
name|ChannelBuffer
name|buffer
decl_stmt|;
DECL|field|startIndex
specifier|private
specifier|final
name|int
name|startIndex
decl_stmt|;
DECL|field|endIndex
specifier|private
specifier|final
name|int
name|endIndex
decl_stmt|;
DECL|method|ChannelBufferStreamInput
specifier|public
name|ChannelBufferStreamInput
parameter_list|(
name|ChannelBuffer
name|buffer
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|>
name|buffer
operator|.
name|readableBytes
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
name|startIndex
operator|=
name|buffer
operator|.
name|readerIndex
argument_list|()
expr_stmt|;
name|endIndex
operator|=
name|startIndex
operator|+
name|length
expr_stmt|;
name|buffer
operator|.
name|markReaderIndex
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns the number of read bytes by this stream so far.      */
DECL|method|readBytes
specifier|public
name|int
name|readBytes
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|-
name|startIndex
return|;
block|}
DECL|method|available
annotation|@
name|Override
specifier|public
name|int
name|available
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|endIndex
operator|-
name|buffer
operator|.
name|readerIndex
argument_list|()
return|;
block|}
DECL|method|mark
annotation|@
name|Override
specifier|public
name|void
name|mark
parameter_list|(
name|int
name|readlimit
parameter_list|)
block|{
name|buffer
operator|.
name|markReaderIndex
argument_list|()
expr_stmt|;
block|}
DECL|method|markSupported
annotation|@
name|Override
specifier|public
name|boolean
name|markSupported
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|read
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|buffer
operator|.
name|readByte
argument_list|()
operator|&
literal|0xff
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
name|int
name|available
init|=
name|available
argument_list|()
decl_stmt|;
if|if
condition|(
name|available
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|len
operator|=
name|Math
operator|.
name|min
argument_list|(
name|available
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
DECL|method|reset
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|resetReaderIndex
argument_list|()
expr_stmt|;
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
name|n
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
name|skipBytes
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|skipBytes
argument_list|(
operator|(
name|int
operator|)
name|n
argument_list|)
return|;
block|}
block|}
DECL|method|skipBytes
specifier|public
name|int
name|skipBytes
parameter_list|(
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nBytes
init|=
name|Math
operator|.
name|min
argument_list|(
name|available
argument_list|()
argument_list|,
name|n
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|skipBytes
argument_list|(
name|nBytes
argument_list|)
expr_stmt|;
return|return
name|nBytes
return|;
block|}
DECL|method|readByte
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|available
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
return|return
name|buffer
operator|.
name|readByte
argument_list|()
return|;
block|}
DECL|method|readBytes
annotation|@
name|Override
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
name|int
name|read
init|=
name|read
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
decl_stmt|;
if|if
condition|(
name|read
operator|<
name|len
condition|)
block|{
throw|throw
operator|new
name|EOFException
argument_list|()
throw|;
block|}
block|}
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do here
block|}
block|}
end_class

end_unit

