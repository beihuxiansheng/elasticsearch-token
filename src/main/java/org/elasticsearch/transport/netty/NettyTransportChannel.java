begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ThrowableObjectOutputStream
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
name|BytesStreamOutput
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
name|CachedStreamOutput
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
name|Streamable
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
name|transport
operator|.
name|TransportResponseOptions
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
name|support
operator|.
name|TransportStreams
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
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFuture
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

begin_comment
comment|/**  *  */
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
annotation|@
name|Override
DECL|method|action
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
annotation|@
name|Override
DECL|method|sendResponse
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
name|sendResponse
argument_list|(
name|message
argument_list|,
name|TransportResponseOptions
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|Streamable
name|message
parameter_list|,
name|TransportResponseOptions
name|options
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|transport
operator|.
name|compress
condition|)
block|{
name|options
operator|.
name|withCompress
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
name|TransportStreams
operator|.
name|buildResponse
argument_list|(
name|cachedEntry
argument_list|,
name|requestId
argument_list|,
name|message
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|ChannelBuffer
name|buffer
init|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|underlyingBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|ChannelFuture
name|future
init|=
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|future
operator|.
name|addListener
argument_list|(
operator|new
name|NettyTransport
operator|.
name|CacheFutureListener
argument_list|(
name|cachedEntry
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
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
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
name|BytesStreamOutput
name|stream
decl_stmt|;
try|try
block|{
name|stream
operator|=
name|cachedEntry
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|writeResponseExceptionHeader
argument_list|(
name|stream
argument_list|)
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
name|error
argument_list|)
decl_stmt|;
name|ThrowableObjectOutputStream
name|too
init|=
operator|new
name|ThrowableObjectOutputStream
argument_list|(
name|stream
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
name|cachedEntry
operator|.
name|reset
argument_list|()
expr_stmt|;
name|stream
operator|=
name|cachedEntry
operator|.
name|bytes
argument_list|()
expr_stmt|;
name|writeResponseExceptionHeader
argument_list|(
name|stream
argument_list|)
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
name|stream
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
name|ChannelBuffer
name|buffer
init|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|stream
operator|.
name|underlyingBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|stream
operator|.
name|size
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
name|ChannelFuture
name|future
init|=
name|channel
operator|.
name|write
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|future
operator|.
name|addListener
argument_list|(
operator|new
name|NettyTransport
operator|.
name|CacheFutureListener
argument_list|(
name|cachedEntry
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|writeResponseExceptionHeader
specifier|private
name|void
name|writeResponseExceptionHeader
parameter_list|(
name|BytesStreamOutput
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
operator|.
name|writeBytes
argument_list|(
name|LENGTH_PLACEHOLDER
argument_list|)
expr_stmt|;
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
name|TransportStreams
operator|.
name|statusSetResponse
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|status
operator|=
name|TransportStreams
operator|.
name|statusSetError
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
block|}
block|}
end_class

end_unit

