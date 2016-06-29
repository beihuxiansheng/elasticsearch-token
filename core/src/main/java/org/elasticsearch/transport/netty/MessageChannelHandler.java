begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|component
operator|.
name|Lifecycle
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
name|compress
operator|.
name|Compressor
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
name|compress
operator|.
name|CompressorFactory
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
name|compress
operator|.
name|NotCompressedException
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
name|NamedWriteableAwareStreamInput
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
name|logging
operator|.
name|ESLogger
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
name|NettyUtils
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
name|transport
operator|.
name|InetSocketTransportAddress
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
name|util
operator|.
name|concurrent
operator|.
name|AbstractRunnable
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|ActionNotFoundTransportException
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
name|RequestHandlerRegistry
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
name|ResponseHandlerFailureTransportException
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
name|TransportRequest
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
name|TransportResponse
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
name|TransportResponseHandler
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
name|TransportSerializationException
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
name|TransportServiceAdapter
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
name|Transports
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
name|TransportStatus
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
name|ChannelHandlerContext
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
name|ExceptionEvent
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
name|MessageEvent
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
name|SimpleChannelUpstreamHandler
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
name|WriteCompletionEvent
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_comment
comment|/**  * A handler (must be the last one!) that does size based frame decoding and forwards the actual message  * to the relevant action.  */
end_comment

begin_class
DECL|class|MessageChannelHandler
specifier|public
class|class
name|MessageChannelHandler
extends|extends
name|SimpleChannelUpstreamHandler
block|{
DECL|field|logger
specifier|protected
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|threadPool
specifier|protected
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|transportServiceAdapter
specifier|protected
specifier|final
name|TransportServiceAdapter
name|transportServiceAdapter
decl_stmt|;
DECL|field|transport
specifier|protected
specifier|final
name|NettyTransport
name|transport
decl_stmt|;
DECL|field|profileName
specifier|protected
specifier|final
name|String
name|profileName
decl_stmt|;
DECL|field|threadContext
specifier|private
specifier|final
name|ThreadContext
name|threadContext
decl_stmt|;
DECL|method|MessageChannelHandler
specifier|public
name|MessageChannelHandler
parameter_list|(
name|NettyTransport
name|transport
parameter_list|,
name|ESLogger
name|logger
parameter_list|,
name|String
name|profileName
parameter_list|)
block|{
name|this
operator|.
name|threadPool
operator|=
name|transport
operator|.
name|threadPool
argument_list|()
expr_stmt|;
name|this
operator|.
name|threadContext
operator|=
name|threadPool
operator|.
name|getThreadContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|transportServiceAdapter
operator|=
name|transport
operator|.
name|transportServiceAdapter
argument_list|()
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|profileName
operator|=
name|profileName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeComplete
specifier|public
name|void
name|writeComplete
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|WriteCompletionEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
name|transportServiceAdapter
operator|.
name|sent
argument_list|(
name|e
operator|.
name|getWrittenAmount
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|writeComplete
argument_list|(
name|ctx
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|MessageEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
name|Transports
operator|.
name|assertTransportThread
argument_list|()
expr_stmt|;
name|Object
name|m
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|m
operator|instanceof
name|ChannelBuffer
operator|)
condition|)
block|{
name|ctx
operator|.
name|sendUpstream
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
name|ChannelBuffer
name|buffer
init|=
operator|(
name|ChannelBuffer
operator|)
name|m
decl_stmt|;
name|Marker
name|marker
init|=
operator|new
name|Marker
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|int
name|size
init|=
name|marker
operator|.
name|messageSizeWithRemainingHeaders
argument_list|()
decl_stmt|;
name|transportServiceAdapter
operator|.
name|received
argument_list|(
name|marker
operator|.
name|messageSizeWithAllHeaders
argument_list|()
argument_list|)
expr_stmt|;
comment|// we have additional bytes to read, outside of the header
name|boolean
name|hasMessageBytesToRead
init|=
name|marker
operator|.
name|messageSize
argument_list|()
operator|!=
literal|0
decl_stmt|;
comment|// netty always copies a buffer, either in NioWorker in its read handler, where it copies to a fresh
comment|// buffer, or in the cumulation buffer, which is cleaned each time
name|StreamInput
name|streamIn
init|=
name|ChannelBufferStreamInputFactory
operator|.
name|create
argument_list|(
name|buffer
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|tCtx
init|=
name|threadContext
operator|.
name|stashContext
argument_list|()
init|)
block|{
name|long
name|requestId
init|=
name|streamIn
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|byte
name|status
init|=
name|streamIn
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|Version
name|version
init|=
name|Version
operator|.
name|fromId
argument_list|(
name|streamIn
operator|.
name|readInt
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|TransportStatus
operator|.
name|isCompress
argument_list|(
name|status
argument_list|)
operator|&&
name|hasMessageBytesToRead
operator|&&
name|buffer
operator|.
name|readable
argument_list|()
condition|)
block|{
name|Compressor
name|compressor
decl_stmt|;
try|try
block|{
name|compressor
operator|=
name|CompressorFactory
operator|.
name|compressor
argument_list|(
name|NettyUtils
operator|.
name|toBytesReference
argument_list|(
name|buffer
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotCompressedException
name|ex
parameter_list|)
block|{
name|int
name|maxToRead
init|=
name|Math
operator|.
name|min
argument_list|(
name|buffer
operator|.
name|readableBytes
argument_list|()
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|buffer
operator|.
name|readerIndex
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"stream marked as compressed, but no compressor found, first ["
argument_list|)
operator|.
name|append
argument_list|(
name|maxToRead
argument_list|)
operator|.
name|append
argument_list|(
literal|"] content bytes out of ["
argument_list|)
operator|.
name|append
argument_list|(
name|buffer
operator|.
name|readableBytes
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"] readable bytes with message size ["
argument_list|)
operator|.
name|append
argument_list|(
name|size
argument_list|)
operator|.
name|append
argument_list|(
literal|"] "
argument_list|)
operator|.
name|append
argument_list|(
literal|"] are ["
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
name|maxToRead
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|buffer
operator|.
name|getByte
argument_list|(
name|offset
operator|+
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|streamIn
operator|=
name|compressor
operator|.
name|streamInput
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
argument_list|)
operator|==
literal|false
operator|||
name|version
operator|.
name|major
operator|!=
name|Version
operator|.
name|CURRENT
operator|.
name|major
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Received message from unsupported version: ["
operator|+
name|version
operator|+
literal|"] minimal compatible version is: ["
operator|+
name|Version
operator|.
name|CURRENT
operator|.
name|minimumCompatibilityVersion
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|streamIn
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
if|if
condition|(
name|TransportStatus
operator|.
name|isRequest
argument_list|(
name|status
argument_list|)
condition|)
block|{
name|threadContext
operator|.
name|readHeaders
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
name|handleRequest
argument_list|(
name|ctx
operator|.
name|getChannel
argument_list|()
argument_list|,
name|marker
argument_list|,
name|streamIn
argument_list|,
name|requestId
argument_list|,
name|size
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|TransportResponseHandler
argument_list|<
name|?
argument_list|>
name|handler
init|=
name|transportServiceAdapter
operator|.
name|onResponseReceived
argument_list|(
name|requestId
argument_list|)
decl_stmt|;
comment|// ignore if its null, the adapter logs it
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|TransportStatus
operator|.
name|isError
argument_list|(
name|status
argument_list|)
condition|)
block|{
name|handlerResponseError
argument_list|(
name|streamIn
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleResponse
argument_list|(
name|ctx
operator|.
name|getChannel
argument_list|()
argument_list|,
name|streamIn
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
name|marker
operator|.
name|validateResponse
argument_list|(
name|streamIn
argument_list|,
name|requestId
argument_list|,
name|handler
argument_list|,
name|TransportStatus
operator|.
name|isError
argument_list|(
name|status
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
try|try
block|{
if|if
condition|(
name|success
condition|)
block|{
name|IOUtils
operator|.
name|close
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|streamIn
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
comment|// Set the expected position of the buffer, no matter what happened
name|buffer
operator|.
name|readerIndex
argument_list|(
name|marker
operator|.
name|expectedReaderIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handleResponse
specifier|protected
name|void
name|handleResponse
parameter_list|(
name|Channel
name|channel
parameter_list|,
name|StreamInput
name|buffer
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
name|buffer
operator|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|buffer
argument_list|,
name|transport
operator|.
name|namedWriteableRegistry
argument_list|)
expr_stmt|;
specifier|final
name|TransportResponse
name|response
init|=
name|handler
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|response
operator|.
name|remoteAddress
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
operator|(
name|InetSocketAddress
operator|)
name|channel
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|remoteAddress
argument_list|()
expr_stmt|;
try|try
block|{
name|response
operator|.
name|readFrom
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|handleException
argument_list|(
name|handler
argument_list|,
operator|new
name|TransportSerializationException
argument_list|(
literal|"Failed to deserialize response of type ["
operator|+
name|response
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
if|if
condition|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
operator|.
name|equals
argument_list|(
name|handler
operator|.
name|executor
argument_list|()
argument_list|)
condition|)
block|{
comment|//noinspection unchecked
name|handler
operator|.
name|handleResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|handler
operator|.
name|executor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|ResponseHandler
argument_list|(
name|handler
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|handleException
argument_list|(
name|handler
argument_list|,
operator|new
name|ResponseHandlerFailureTransportException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handlerResponseError
specifier|private
name|void
name|handlerResponseError
parameter_list|(
name|StreamInput
name|buffer
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
name|Throwable
name|error
decl_stmt|;
try|try
block|{
name|error
operator|=
name|buffer
operator|.
name|readThrowable
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|error
operator|=
operator|new
name|TransportSerializationException
argument_list|(
literal|"Failed to deserialize exception response from stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|handleException
argument_list|(
name|handler
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
DECL|method|handleException
specifier|private
name|void
name|handleException
parameter_list|(
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|error
operator|instanceof
name|RemoteTransportException
operator|)
condition|)
block|{
name|error
operator|=
operator|new
name|RemoteTransportException
argument_list|(
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RemoteTransportException
name|rtx
init|=
operator|(
name|RemoteTransportException
operator|)
name|error
decl_stmt|;
if|if
condition|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
operator|.
name|equals
argument_list|(
name|handler
operator|.
name|executor
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|handler
operator|.
name|handleException
argument_list|(
name|rtx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to handle exception response [{}]"
argument_list|,
name|e
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|handler
operator|.
name|executor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|handler
operator|.
name|handleException
argument_list|(
name|rtx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to handle exception response [{}]"
argument_list|,
name|e
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleRequest
specifier|protected
name|String
name|handleRequest
parameter_list|(
name|Channel
name|channel
parameter_list|,
name|Marker
name|marker
parameter_list|,
name|StreamInput
name|buffer
parameter_list|,
name|long
name|requestId
parameter_list|,
name|int
name|messageLengthBytes
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|IOException
block|{
name|buffer
operator|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|buffer
argument_list|,
name|transport
operator|.
name|namedWriteableRegistry
argument_list|)
expr_stmt|;
specifier|final
name|String
name|action
init|=
name|buffer
operator|.
name|readString
argument_list|()
decl_stmt|;
name|transportServiceAdapter
operator|.
name|onRequestReceived
argument_list|(
name|requestId
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|NettyTransportChannel
name|transportChannel
init|=
literal|null
decl_stmt|;
try|try
block|{
specifier|final
name|RequestHandlerRegistry
name|reg
init|=
name|transportServiceAdapter
operator|.
name|getRequestHandler
argument_list|(
name|action
argument_list|)
decl_stmt|;
if|if
condition|(
name|reg
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ActionNotFoundTransportException
argument_list|(
name|action
argument_list|)
throw|;
block|}
if|if
condition|(
name|reg
operator|.
name|canTripCircuitBreaker
argument_list|()
condition|)
block|{
name|transport
operator|.
name|inFlightRequestsBreaker
argument_list|()
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
name|messageLengthBytes
argument_list|,
literal|"<transport_request>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transport
operator|.
name|inFlightRequestsBreaker
argument_list|()
operator|.
name|addWithoutBreaking
argument_list|(
name|messageLengthBytes
argument_list|)
expr_stmt|;
block|}
name|transportChannel
operator|=
operator|new
name|NettyTransportChannel
argument_list|(
name|transport
argument_list|,
name|transportServiceAdapter
argument_list|,
name|action
argument_list|,
name|channel
argument_list|,
name|requestId
argument_list|,
name|version
argument_list|,
name|profileName
argument_list|,
name|messageLengthBytes
argument_list|)
expr_stmt|;
specifier|final
name|TransportRequest
name|request
init|=
name|reg
operator|.
name|newRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|remoteAddress
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
operator|(
name|InetSocketAddress
operator|)
name|channel
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// in case we throw an exception, i.e. when the limit is hit, we don't want to verify
name|validateRequest
argument_list|(
name|marker
argument_list|,
name|buffer
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|)
expr_stmt|;
if|if
condition|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
operator|.
name|equals
argument_list|(
name|reg
operator|.
name|getExecutor
argument_list|()
argument_list|)
condition|)
block|{
comment|//noinspection unchecked
name|reg
operator|.
name|processMessageReceived
argument_list|(
name|request
argument_list|,
name|transportChannel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|reg
operator|.
name|getExecutor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|RequestHandler
argument_list|(
name|reg
argument_list|,
name|request
argument_list|,
name|transportChannel
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// the circuit breaker tripped
if|if
condition|(
name|transportChannel
operator|==
literal|null
condition|)
block|{
name|transportChannel
operator|=
operator|new
name|NettyTransportChannel
argument_list|(
name|transport
argument_list|,
name|transportServiceAdapter
argument_list|,
name|action
argument_list|,
name|channel
argument_list|,
name|requestId
argument_list|,
name|version
argument_list|,
name|profileName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|transportChannel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send error message back to client for action [{}]"
argument_list|,
name|e
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Actual Exception"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|action
return|;
block|}
comment|// This template method is needed to inject custom error checking logic in tests.
DECL|method|validateRequest
specifier|protected
name|void
name|validateRequest
parameter_list|(
name|Marker
name|marker
parameter_list|,
name|StreamInput
name|buffer
parameter_list|,
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|)
throws|throws
name|IOException
block|{
name|marker
operator|.
name|validateRequest
argument_list|(
name|buffer
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exceptionCaught
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ExceptionEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
name|transport
operator|.
name|exceptionCaught
argument_list|(
name|ctx
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|class|ResponseHandler
class|class
name|ResponseHandler
implements|implements
name|Runnable
block|{
DECL|field|handler
specifier|private
specifier|final
name|TransportResponseHandler
name|handler
decl_stmt|;
DECL|field|response
specifier|private
specifier|final
name|TransportResponse
name|response
decl_stmt|;
DECL|method|ResponseHandler
specifier|public
name|ResponseHandler
parameter_list|(
name|TransportResponseHandler
name|handler
parameter_list|,
name|TransportResponse
name|response
parameter_list|)
block|{
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|handler
operator|.
name|handleResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|handleException
argument_list|(
name|handler
argument_list|,
operator|new
name|ResponseHandlerFailureTransportException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|RequestHandler
class|class
name|RequestHandler
extends|extends
name|AbstractRunnable
block|{
DECL|field|reg
specifier|private
specifier|final
name|RequestHandlerRegistry
name|reg
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|TransportRequest
name|request
decl_stmt|;
DECL|field|transportChannel
specifier|private
specifier|final
name|NettyTransportChannel
name|transportChannel
decl_stmt|;
DECL|method|RequestHandler
specifier|public
name|RequestHandler
parameter_list|(
name|RequestHandlerRegistry
name|reg
parameter_list|,
name|TransportRequest
name|request
parameter_list|,
name|NettyTransportChannel
name|transportChannel
parameter_list|)
block|{
name|this
operator|.
name|reg
operator|=
name|reg
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|transportChannel
operator|=
name|transportChannel
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
annotation|@
name|Override
DECL|method|doRun
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|reg
operator|.
name|processMessageReceived
argument_list|(
name|request
argument_list|,
name|transportChannel
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isForceExecution
specifier|public
name|boolean
name|isForceExecution
parameter_list|()
block|{
return|return
name|reg
operator|.
name|isForceExecution
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|transport
operator|.
name|lifecycleState
argument_list|()
operator|==
name|Lifecycle
operator|.
name|State
operator|.
name|STARTED
condition|)
block|{
comment|// we can only send a response transport is started....
try|try
block|{
name|transportChannel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send error message back to client for action [{}]"
argument_list|,
name|e1
argument_list|,
name|reg
operator|.
name|getAction
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Actual Exception"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Internal helper class to store characteristic offsets of a buffer during processing      */
DECL|class|Marker
specifier|protected
specifier|static
specifier|final
class|class
name|Marker
block|{
DECL|field|buffer
specifier|private
specifier|final
name|ChannelBuffer
name|buffer
decl_stmt|;
DECL|field|remainingMessageSize
specifier|private
specifier|final
name|int
name|remainingMessageSize
decl_stmt|;
DECL|field|expectedReaderIndex
specifier|private
specifier|final
name|int
name|expectedReaderIndex
decl_stmt|;
DECL|method|Marker
specifier|public
name|Marker
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
comment|// when this constructor is called, we have read already two parts of the message header: the marker bytes and the message
comment|// message length (see SizeHeaderFrameDecoder). Hence we have to rewind the index for MESSAGE_LENGTH_SIZE bytes to read the
comment|// remaining message length again.
name|this
operator|.
name|remainingMessageSize
operator|=
name|buffer
operator|.
name|getInt
argument_list|(
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|-
name|NettyHeader
operator|.
name|MESSAGE_LENGTH_SIZE
argument_list|)
expr_stmt|;
name|this
operator|.
name|expectedReaderIndex
operator|=
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|+
name|remainingMessageSize
expr_stmt|;
block|}
comment|/**          * @return the number of bytes that have yet to be read from the buffer          */
DECL|method|messageSizeWithRemainingHeaders
specifier|public
name|int
name|messageSizeWithRemainingHeaders
parameter_list|()
block|{
return|return
name|remainingMessageSize
return|;
block|}
comment|/**          * @return the number in bytes for the message including all headers (even the ones that have been read from the buffer already)          */
DECL|method|messageSizeWithAllHeaders
specifier|public
name|int
name|messageSizeWithAllHeaders
parameter_list|()
block|{
return|return
name|remainingMessageSize
operator|+
name|NettyHeader
operator|.
name|MARKER_BYTES_SIZE
operator|+
name|NettyHeader
operator|.
name|MESSAGE_LENGTH_SIZE
return|;
block|}
comment|/**          * @return the number of bytes for the message itself (excluding all headers).          */
DECL|method|messageSize
specifier|public
name|int
name|messageSize
parameter_list|()
block|{
return|return
name|messageSizeWithAllHeaders
argument_list|()
operator|-
name|NettyHeader
operator|.
name|HEADER_SIZE
return|;
block|}
comment|/**          * @return the expected index of the buffer's reader after the message has been consumed entirely.          */
DECL|method|expectedReaderIndex
specifier|public
name|int
name|expectedReaderIndex
parameter_list|()
block|{
return|return
name|expectedReaderIndex
return|;
block|}
comment|/**          * Validates that a request has been fully read (not too few bytes but also not too many bytes).          *          * @param stream    A stream that is associated with the buffer that is tracked by this marker.          * @param requestId The current request id.          * @param action    The currently executed action.          * @throws IOException           Iff the stream could not be read.          * @throws IllegalStateException Iff the request has not been fully read.          */
DECL|method|validateRequest
specifier|public
name|void
name|validateRequest
parameter_list|(
name|StreamInput
name|stream
parameter_list|,
name|long
name|requestId
parameter_list|,
name|String
name|action
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|nextByte
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
comment|// calling read() is useful to make sure the message is fully read, even if there some kind of EOS marker
if|if
condition|(
name|nextByte
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message not fully read (request) for requestId ["
operator|+
name|requestId
operator|+
literal|"], action ["
operator|+
name|action
operator|+
literal|"], readerIndex ["
operator|+
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|+
literal|"] vs expected ["
operator|+
name|expectedReaderIndex
operator|+
literal|"]; resetting"
argument_list|)
throw|;
block|}
if|if
condition|(
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|<
name|expectedReaderIndex
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message is fully read (request), yet there are "
operator|+
operator|(
name|expectedReaderIndex
operator|-
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|)
operator|+
literal|" remaining bytes; resetting"
argument_list|)
throw|;
block|}
if|if
condition|(
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|>
name|expectedReaderIndex
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message read past expected size (request) for requestId ["
operator|+
name|requestId
operator|+
literal|"], action ["
operator|+
name|action
operator|+
literal|"], readerIndex ["
operator|+
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|+
literal|"] vs expected ["
operator|+
name|expectedReaderIndex
operator|+
literal|"]; resetting"
argument_list|)
throw|;
block|}
block|}
comment|/**          * Validates that a response has been fully read (not too few bytes but also not too many bytes).          *          * @param stream    A stream that is associated with the buffer that is tracked by this marker.          * @param requestId The corresponding request id for this response.          * @param handler   The current response handler.          * @param error     Whether validate an error response.          * @throws IOException           Iff the stream could not be read.          * @throws IllegalStateException Iff the request has not been fully read.          */
DECL|method|validateResponse
specifier|public
name|void
name|validateResponse
parameter_list|(
name|StreamInput
name|stream
parameter_list|,
name|long
name|requestId
parameter_list|,
name|TransportResponseHandler
argument_list|<
name|?
argument_list|>
name|handler
parameter_list|,
name|boolean
name|error
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Check the entire message has been read
specifier|final
name|int
name|nextByte
init|=
name|stream
operator|.
name|read
argument_list|()
decl_stmt|;
comment|// calling read() is useful to make sure the message is fully read, even if there is an EOS marker
if|if
condition|(
name|nextByte
operator|!=
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message not fully read (response) for requestId ["
operator|+
name|requestId
operator|+
literal|"], handler ["
operator|+
name|handler
operator|+
literal|"], error ["
operator|+
name|error
operator|+
literal|"]; resetting"
argument_list|)
throw|;
block|}
if|if
condition|(
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|<
name|expectedReaderIndex
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message is fully read (response), yet there are "
operator|+
operator|(
name|expectedReaderIndex
operator|-
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|)
operator|+
literal|" remaining bytes; resetting"
argument_list|)
throw|;
block|}
if|if
condition|(
name|buffer
operator|.
name|readerIndex
argument_list|()
operator|>
name|expectedReaderIndex
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Message read past expected size (response) for requestId ["
operator|+
name|requestId
operator|+
literal|"], handler ["
operator|+
name|handler
operator|+
literal|"], error ["
operator|+
name|error
operator|+
literal|"]; resetting"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
end_class

end_unit

