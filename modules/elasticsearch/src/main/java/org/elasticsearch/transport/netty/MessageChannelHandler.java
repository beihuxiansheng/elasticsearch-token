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
name|*
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
name|ThrowableObjectInputStream
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
name|ChannelBufferInputStream
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
annotation|@
name|ChannelPipelineCoverage
argument_list|(
literal|"one"
argument_list|)
DECL|class|MessageChannelHandler
specifier|public
class|class
name|MessageChannelHandler
extends|extends
name|SimpleChannelUpstreamHandler
block|{
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|transportServiceAdapter
specifier|private
specifier|final
name|TransportServiceAdapter
name|transportServiceAdapter
decl_stmt|;
DECL|field|transport
specifier|private
specifier|final
name|NettyTransport
name|transport
decl_stmt|;
DECL|method|MessageChannelHandler
specifier|public
name|MessageChannelHandler
parameter_list|(
name|NettyTransport
name|transport
parameter_list|,
name|Logger
name|logger
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
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|MessageEvent
name|event
parameter_list|)
throws|throws
name|Exception
block|{
name|ChannelBufferInputStream
name|buffer
init|=
operator|(
name|ChannelBufferInputStream
operator|)
name|event
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|long
name|requestId
init|=
name|buffer
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|byte
name|status
init|=
name|buffer
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isRequest
init|=
name|isRequest
argument_list|(
name|status
argument_list|)
decl_stmt|;
if|if
condition|(
name|isRequest
condition|)
block|{
name|handleRequest
argument_list|(
name|event
argument_list|,
name|buffer
argument_list|,
name|requestId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TransportResponseHandler
name|handler
init|=
name|transportServiceAdapter
operator|.
name|remove
argument_list|(
name|requestId
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResponseHandlerNotFoundTransportException
argument_list|(
name|requestId
argument_list|)
throw|;
block|}
if|if
condition|(
name|isError
argument_list|(
name|status
argument_list|)
condition|)
block|{
name|handlerResponseError
argument_list|(
name|buffer
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleResponse
argument_list|(
name|buffer
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handleResponse
specifier|private
name|void
name|handleResponse
parameter_list|(
name|ChannelBufferInputStream
name|buffer
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
specifier|final
name|Streamable
name|streamable
init|=
name|handler
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|streamable
operator|.
name|readFrom
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|streamable
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
if|if
condition|(
name|handler
operator|.
name|spawn
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
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
name|handleResponse
argument_list|(
name|streamable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Failed to handler response"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
comment|//noinspection unchecked
name|handler
operator|.
name|handleResponse
argument_list|(
name|streamable
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"Failed to handler response"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handlerResponseError
specifier|private
name|void
name|handlerResponseError
parameter_list|(
name|ChannelBufferInputStream
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
name|ThrowableObjectInputStream
name|ois
init|=
operator|new
name|ThrowableObjectInputStream
argument_list|(
name|buffer
argument_list|)
decl_stmt|;
name|error
operator|=
operator|(
name|Throwable
operator|)
name|ois
operator|.
name|readObject
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"None remote transport exception"
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
name|handler
operator|.
name|spawn
argument_list|()
condition|)
block|{
name|threadPool
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
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Failed to handle exception response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handler
operator|.
name|handleException
argument_list|(
name|rtx
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handleRequest
specifier|private
name|void
name|handleRequest
parameter_list|(
name|MessageEvent
name|event
parameter_list|,
name|ChannelBufferInputStream
name|buffer
parameter_list|,
name|long
name|requestId
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|action
init|=
name|buffer
operator|.
name|readUTF
argument_list|()
decl_stmt|;
specifier|final
name|NettyTransportChannel
name|transportChannel
init|=
operator|new
name|NettyTransportChannel
argument_list|(
name|transport
argument_list|,
name|action
argument_list|,
name|event
operator|.
name|getChannel
argument_list|()
argument_list|,
name|requestId
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|TransportRequestHandler
name|handler
init|=
name|transportServiceAdapter
operator|.
name|handler
argument_list|(
name|action
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ActionNotFoundTransportException
argument_list|(
literal|"Action ["
operator|+
name|action
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
specifier|final
name|Streamable
name|streamable
init|=
name|handler
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|streamable
operator|.
name|readFrom
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|handler
operator|.
name|spawn
argument_list|()
condition|)
block|{
name|threadPool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"unchecked"
block|}
argument_list|)
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
name|messageReceived
argument_list|(
name|streamable
argument_list|,
name|transportChannel
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
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
literal|"Failed to send error message back to client for action ["
operator|+
name|action
operator|+
literal|"]"
argument_list|,
name|e1
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
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//noinspection unchecked
name|handler
operator|.
name|messageReceived
argument_list|(
name|streamable
argument_list|,
name|transportChannel
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
literal|"Failed to send error message back to client for action ["
operator|+
name|action
operator|+
literal|"]"
argument_list|,
name|e
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
block|}
DECL|method|exceptionCaught
annotation|@
name|Override
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
block|}
end_class

end_unit

