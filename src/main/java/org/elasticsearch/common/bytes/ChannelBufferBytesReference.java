begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.bytes
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|bytes
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
name|transport
operator|.
name|netty
operator|.
name|ChannelBufferStreamInputFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ChannelBufferBytesReference
specifier|public
class|class
name|ChannelBufferBytesReference
implements|implements
name|BytesReference
block|{
DECL|field|buffer
specifier|private
specifier|final
name|ChannelBuffer
name|buffer
decl_stmt|;
DECL|method|ChannelBufferBytesReference
specifier|public
name|ChannelBufferBytesReference
parameter_list|(
name|ChannelBuffer
name|buffer
parameter_list|)
block|{
name|this
operator|.
name|buffer
operator|=
name|buffer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|byte
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|buffer
operator|.
name|getByte
argument_list|(
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|+
name|index
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|readableBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|slice
specifier|public
name|BytesReference
name|slice
parameter_list|(
name|int
name|from
parameter_list|,
name|int
name|length
parameter_list|)
block|{
return|return
operator|new
name|ChannelBufferBytesReference
argument_list|(
name|buffer
operator|.
name|slice
argument_list|(
name|from
argument_list|,
name|length
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|streamInput
specifier|public
name|StreamInput
name|streamInput
parameter_list|()
block|{
return|return
name|ChannelBufferStreamInputFactory
operator|.
name|create
argument_list|(
name|buffer
operator|.
name|duplicate
argument_list|()
argument_list|)
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
parameter_list|,
name|boolean
name|withLength
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|withLength
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|buffer
operator|.
name|readableBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|getBytes
argument_list|(
name|buffer
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|out
argument_list|,
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
operator|.
name|getBytes
argument_list|(
name|buffer
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|os
argument_list|,
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toBytes
specifier|public
name|byte
index|[]
name|toBytes
parameter_list|()
block|{
return|return
name|copyBytesArray
argument_list|()
operator|.
name|toBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toBytesArray
specifier|public
name|BytesArray
name|toBytesArray
parameter_list|()
block|{
if|if
condition|(
name|buffer
operator|.
name|hasArray
argument_list|()
condition|)
block|{
return|return
operator|new
name|BytesArray
argument_list|(
name|buffer
operator|.
name|array
argument_list|()
argument_list|,
name|buffer
operator|.
name|arrayOffset
argument_list|()
operator|+
name|buffer
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|buffer
operator|.
name|readableBytes
argument_list|()
argument_list|)
return|;
block|}
return|return
name|copyBytesArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|copyBytesArray
specifier|public
name|BytesArray
name|copyBytesArray
parameter_list|()
block|{
name|byte
index|[]
name|copy
init|=
operator|new
name|byte
index|[
name|buffer
operator|.
name|readableBytes
argument_list|()
index|]
decl_stmt|;
name|buffer
operator|.
name|getBytes
argument_list|(
name|buffer
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|copy
argument_list|)
expr_stmt|;
return|return
operator|new
name|BytesArray
argument_list|(
name|copy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasArray
specifier|public
name|boolean
name|hasArray
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|hasArray
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|array
specifier|public
name|byte
index|[]
name|array
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|array
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|arrayOffset
specifier|public
name|int
name|arrayOffset
parameter_list|()
block|{
return|return
name|buffer
operator|.
name|arrayOffset
argument_list|()
operator|+
name|buffer
operator|.
name|readerIndex
argument_list|()
return|;
block|}
block|}
end_class

end_unit

