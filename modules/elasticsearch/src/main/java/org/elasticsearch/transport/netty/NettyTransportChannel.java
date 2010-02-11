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
name|transport
operator|.
name|NotSerializableTransportException
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
name|RemoteTransportException
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
name|TransportChannel
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
name|ByteArrayDataOutputStream
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
name|Streamable
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
name|ThrowableObjectOutputStream
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
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|buffer
operator|.
name|ChannelBufferOutputStream
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
name|ChannelBuffers
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
name|channel
operator|.
name|Channel
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
name|NotSerializableException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|Transport
operator|.
name|Helper
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|NettyTransportChannel
specifier|public
class|class
name|NettyTransportChannel
implements|implements
name|TransportChannel
block|{
DECL|field|LENGTH_PLACEHOLDER
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|LENGTH_PLACEHOLDER
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
DECL|field|transport
specifier|private
specifier|final
name|NettyTransport
name|transport
decl_stmt|;
DECL|field|action
specifier|private
specifier|final
name|String
name|action
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|Channel
name|channel
decl_stmt|;
DECL|field|requestId
specifier|private
specifier|final
name|long
name|requestId
decl_stmt|;
DECL|method|NettyTransportChannel
specifier|public
name|NettyTransportChannel
parameter_list|(
name|NettyTransport
name|transport
parameter_list|,
name|String
name|action
parameter_list|,
name|Channel
name|channel
parameter_list|,
name|long
name|requestId
parameter_list|)
block|{
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|requestId
operator|=
name|requestId
expr_stmt|;
block|}
DECL|method|action
annotation|@
name|Override
specifier|public
name|String
name|action
parameter_list|()
block|{
return|return
name|this
operator|.
name|action
return|;
block|}
DECL|method|sendResponse
annotation|@
name|Override
specifier|public
name|void
name|sendResponse
parameter_list|(
name|Streamable
name|message
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayDataOutputStream
name|stream
init|=
name|ByteArrayDataOutputStream
operator|.
name|Cached
operator|.
name|cached
argument_list|()
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|LENGTH_PLACEHOLDER
argument_list|)
expr_stmt|;
comment|// fake size
name|stream
operator|.
name|writeLong
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
name|byte
name|status
init|=
literal|0
decl_stmt|;
name|status
operator|=
name|setResponse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeByte
argument_list|(
name|status
argument_list|)
expr_stmt|;
comment|// 0 for request, 1 for response.
name|message
operator|.
name|writeTo
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|ChannelBuffer
name|buffer
init|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|stream
operator|.
name|copiedByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|setInt
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|writerIndex
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
comment|// update real size.
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
DECL|method|sendResponse
annotation|@
name|Override
specifier|public
name|void
name|sendResponse
parameter_list|(
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
block|{
name|ChannelBuffer
name|buffer
init|=
name|ChannelBuffers
operator|.
name|dynamicBuffer
argument_list|()
decl_stmt|;
name|ChannelBufferOutputStream
name|os
init|=
operator|new
name|ChannelBufferOutputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|LENGTH_PLACEHOLDER
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
name|byte
name|status
init|=
literal|0
decl_stmt|;
name|status
operator|=
name|setResponse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|status
operator|=
name|setError
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeByte
argument_list|(
name|status
argument_list|)
expr_stmt|;
comment|// mark the buffer, so we can reset it when the exception is not serializable
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
name|buffer
operator|.
name|markWriterIndex
argument_list|()
expr_stmt|;
try|try
block|{
name|RemoteTransportException
name|tx
init|=
operator|new
name|RemoteTransportException
argument_list|(
name|transport
operator|.
name|nodeName
argument_list|()
argument_list|,
name|transport
operator|.
name|wrapAddress
argument_list|(
name|channel
operator|.
name|getLocalAddress
argument_list|()
argument_list|)
argument_list|,
name|action
argument_list|,
name|error
argument_list|)
decl_stmt|;
name|ThrowableObjectOutputStream
name|too
init|=
operator|new
name|ThrowableObjectOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|too
operator|.
name|writeObject
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|too
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotSerializableException
name|e
parameter_list|)
block|{
name|buffer
operator|.
name|resetWriterIndex
argument_list|()
expr_stmt|;
name|RemoteTransportException
name|tx
init|=
operator|new
name|RemoteTransportException
argument_list|(
name|transport
operator|.
name|nodeName
argument_list|()
argument_list|,
name|transport
operator|.
name|wrapAddress
argument_list|(
name|channel
operator|.
name|getLocalAddress
argument_list|()
argument_list|)
argument_list|,
name|action
argument_list|,
operator|new
name|NotSerializableTransportException
argument_list|(
name|error
argument_list|)
argument_list|)
decl_stmt|;
name|ThrowableObjectOutputStream
name|too
init|=
operator|new
name|ThrowableObjectOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
name|too
operator|.
name|writeObject
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|too
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|buffer
operator|.
name|setInt
argument_list|(
literal|0
argument_list|,
name|buffer
operator|.
name|writerIndex
argument_list|()
operator|-
literal|4
argument_list|)
expr_stmt|;
comment|// update real size.
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

