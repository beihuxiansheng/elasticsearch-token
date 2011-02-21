begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
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
name|ElasticSearchException
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
name|AbstractLifecycleComponent
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
name|inject
operator|.
name|Inject
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
name|OpenChannelsHandler
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
name|bootstrap
operator|.
name|ServerBootstrap
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
name|channel
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
name|common
operator|.
name|netty
operator|.
name|channel
operator|.
name|socket
operator|.
name|nio
operator|.
name|NioServerSocketChannelFactory
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
name|channel
operator|.
name|socket
operator|.
name|oio
operator|.
name|OioServerSocketChannelFactory
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpChunkAggregator
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpRequestDecoder
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseEncoder
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
name|handler
operator|.
name|timeout
operator|.
name|ReadTimeoutException
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
name|logging
operator|.
name|InternalLogger
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
name|logging
operator|.
name|InternalLoggerFactory
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
name|network
operator|.
name|NetworkService
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
name|network
operator|.
name|NetworkUtils
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
name|settings
operator|.
name|Settings
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
name|BoundTransportAddress
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
name|transport
operator|.
name|NetworkExceptionHelper
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
name|PortsRange
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
name|unit
operator|.
name|ByteSizeUnit
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
name|unit
operator|.
name|ByteSizeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
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
name|transport
operator|.
name|BindTransportException
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
name|NettyInternalESLoggerFactory
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
name|InetAddress
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
operator|.
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
name|EsExecutors
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NettyHttpServerTransport
specifier|public
class|class
name|NettyHttpServerTransport
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|HttpServerTransport
argument_list|>
implements|implements
name|HttpServerTransport
block|{
static|static
block|{
name|InternalLoggerFactory
operator|.
name|setDefaultFactory
argument_list|(
operator|new
name|NettyInternalESLoggerFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InternalLogger
name|newInstance
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|super
operator|.
name|newInstance
argument_list|(
name|name
operator|.
name|replace
argument_list|(
literal|"org.elasticsearch.common.netty."
argument_list|,
literal|"netty."
argument_list|)
operator|.
name|replace
argument_list|(
literal|"org.jboss.netty."
argument_list|,
literal|"netty."
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|field|networkService
specifier|private
specifier|final
name|NetworkService
name|networkService
decl_stmt|;
DECL|field|maxContentLength
specifier|private
specifier|final
name|ByteSizeValue
name|maxContentLength
decl_stmt|;
DECL|field|workerCount
specifier|private
specifier|final
name|int
name|workerCount
decl_stmt|;
DECL|field|blockingServer
specifier|private
specifier|final
name|boolean
name|blockingServer
decl_stmt|;
DECL|field|port
specifier|private
specifier|final
name|String
name|port
decl_stmt|;
DECL|field|bindHost
specifier|private
specifier|final
name|String
name|bindHost
decl_stmt|;
DECL|field|publishHost
specifier|private
specifier|final
name|String
name|publishHost
decl_stmt|;
DECL|field|tcpNoDelay
specifier|private
specifier|final
name|Boolean
name|tcpNoDelay
decl_stmt|;
DECL|field|tcpKeepAlive
specifier|private
specifier|final
name|Boolean
name|tcpKeepAlive
decl_stmt|;
DECL|field|reuseAddress
specifier|private
specifier|final
name|Boolean
name|reuseAddress
decl_stmt|;
DECL|field|tcpSendBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|tcpSendBufferSize
decl_stmt|;
DECL|field|tcpReceiveBufferSize
specifier|private
specifier|final
name|ByteSizeValue
name|tcpReceiveBufferSize
decl_stmt|;
DECL|field|serverBootstrap
specifier|private
specifier|volatile
name|ServerBootstrap
name|serverBootstrap
decl_stmt|;
DECL|field|boundAddress
specifier|private
specifier|volatile
name|BoundTransportAddress
name|boundAddress
decl_stmt|;
DECL|field|serverChannel
specifier|private
specifier|volatile
name|Channel
name|serverChannel
decl_stmt|;
DECL|field|serverOpenChannels
specifier|private
specifier|volatile
name|OpenChannelsHandler
name|serverOpenChannels
decl_stmt|;
DECL|field|httpServerAdapter
specifier|private
specifier|volatile
name|HttpServerAdapter
name|httpServerAdapter
decl_stmt|;
DECL|method|NettyHttpServerTransport
annotation|@
name|Inject
specifier|public
name|NettyHttpServerTransport
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|NetworkService
name|networkService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|networkService
operator|=
name|networkService
expr_stmt|;
name|ByteSizeValue
name|maxContentLength
init|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"max_content_length"
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
literal|"http.max_content_length"
argument_list|,
operator|new
name|ByteSizeValue
argument_list|(
literal|100
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|this
operator|.
name|workerCount
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"worker_count"
argument_list|,
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|*
literal|2
argument_list|)
expr_stmt|;
name|this
operator|.
name|blockingServer
operator|=
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"http.blocking_server"
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|TCP_BLOCKING_SERVER
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|TCP_BLOCKING
argument_list|,
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"port"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"http.port"
argument_list|,
literal|"9200-9300"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|bindHost
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"bind_host"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"http.bind_host"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"http.host"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|publishHost
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"publish_host"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"http.publish_host"
argument_list|,
name|settings
operator|.
name|get
argument_list|(
literal|"http.host"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tcpNoDelay
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"tcp_no_delay"
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|TCP_NO_DELAY
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tcpKeepAlive
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"tcp_keep_alive"
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|TCP_KEEP_ALIVE
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|reuseAddress
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"reuse_address"
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
name|TCP_REUSE_ADDRESS
argument_list|,
name|NetworkUtils
operator|.
name|defaultReuseAddress
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tcpSendBufferSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"tcp_send_buffer_size"
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|TCP_SEND_BUFFER_SIZE
argument_list|,
name|TCP_DEFAULT_SEND_BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|tcpReceiveBufferSize
operator|=
name|componentSettings
operator|.
name|getAsBytesSize
argument_list|(
literal|"tcp_receive_buffer_size"
argument_list|,
name|settings
operator|.
name|getAsBytesSize
argument_list|(
name|TCP_RECEIVE_BUFFER_SIZE
argument_list|,
name|TCP_DEFAULT_RECEIVE_BUFFER_SIZE
argument_list|)
argument_list|)
expr_stmt|;
comment|// validate max content length
if|if
condition|(
name|maxContentLength
operator|.
name|bytes
argument_list|()
operator|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"maxContentLength["
operator|+
name|maxContentLength
operator|+
literal|"] set to high value, resetting it to [100mb]"
argument_list|)
expr_stmt|;
name|maxContentLength
operator|=
operator|new
name|ByteSizeValue
argument_list|(
literal|100
argument_list|,
name|ByteSizeUnit
operator|.
name|MB
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|maxContentLength
operator|=
name|maxContentLength
expr_stmt|;
block|}
DECL|method|httpServerAdapter
specifier|public
name|void
name|httpServerAdapter
parameter_list|(
name|HttpServerAdapter
name|httpServerAdapter
parameter_list|)
block|{
name|this
operator|.
name|httpServerAdapter
operator|=
name|httpServerAdapter
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|this
operator|.
name|serverOpenChannels
operator|=
operator|new
name|OpenChannelsHandler
argument_list|()
expr_stmt|;
if|if
condition|(
name|blockingServer
condition|)
block|{
name|serverBootstrap
operator|=
operator|new
name|ServerBootstrap
argument_list|(
operator|new
name|OioServerSocketChannelFactory
argument_list|(
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"http_server_boss"
argument_list|)
argument_list|)
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"http_server_worker"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|serverBootstrap
operator|=
operator|new
name|ServerBootstrap
argument_list|(
operator|new
name|NioServerSocketChannelFactory
argument_list|(
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"http_server_boss"
argument_list|)
argument_list|)
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"http_server_worker"
argument_list|)
argument_list|)
argument_list|,
name|workerCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|HttpRequestHandler
name|requestHandler
init|=
operator|new
name|HttpRequestHandler
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|ChannelPipelineFactory
name|pipelineFactory
init|=
operator|new
name|ChannelPipelineFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ChannelPipeline
name|getPipeline
parameter_list|()
throws|throws
name|Exception
block|{
name|ChannelPipeline
name|pipeline
init|=
name|Channels
operator|.
name|pipeline
argument_list|()
decl_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
literal|"openChannels"
argument_list|,
name|serverOpenChannels
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
literal|"decoder"
argument_list|,
operator|new
name|HttpRequestDecoder
argument_list|()
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
literal|"aggregator"
argument_list|,
operator|new
name|HttpChunkAggregator
argument_list|(
operator|(
name|int
operator|)
name|maxContentLength
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
literal|"encoder"
argument_list|,
operator|new
name|HttpResponseEncoder
argument_list|()
argument_list|)
expr_stmt|;
name|pipeline
operator|.
name|addLast
argument_list|(
literal|"handler"
argument_list|,
name|requestHandler
argument_list|)
expr_stmt|;
return|return
name|pipeline
return|;
block|}
block|}
decl_stmt|;
name|serverBootstrap
operator|.
name|setPipelineFactory
argument_list|(
name|pipelineFactory
argument_list|)
expr_stmt|;
if|if
condition|(
name|tcpNoDelay
operator|!=
literal|null
condition|)
block|{
name|serverBootstrap
operator|.
name|setOption
argument_list|(
literal|"child.tcpNoDelay"
argument_list|,
name|tcpNoDelay
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tcpKeepAlive
operator|!=
literal|null
condition|)
block|{
name|serverBootstrap
operator|.
name|setOption
argument_list|(
literal|"child.keepAlive"
argument_list|,
name|tcpKeepAlive
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tcpSendBufferSize
operator|!=
literal|null
condition|)
block|{
name|serverBootstrap
operator|.
name|setOption
argument_list|(
literal|"child.sendBufferSize"
argument_list|,
name|tcpSendBufferSize
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tcpReceiveBufferSize
operator|!=
literal|null
condition|)
block|{
name|serverBootstrap
operator|.
name|setOption
argument_list|(
literal|"child.receiveBufferSize"
argument_list|,
name|tcpReceiveBufferSize
operator|.
name|bytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|reuseAddress
operator|!=
literal|null
condition|)
block|{
name|serverBootstrap
operator|.
name|setOption
argument_list|(
literal|"reuseAddress"
argument_list|,
name|reuseAddress
argument_list|)
expr_stmt|;
name|serverBootstrap
operator|.
name|setOption
argument_list|(
literal|"child.reuseAddress"
argument_list|,
name|reuseAddress
argument_list|)
expr_stmt|;
block|}
comment|// Bind and start to accept incoming connections.
name|InetAddress
name|hostAddressX
decl_stmt|;
try|try
block|{
name|hostAddressX
operator|=
name|networkService
operator|.
name|resolveBindHostAddress
argument_list|(
name|bindHost
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindHttpException
argument_list|(
literal|"Failed to resolve host ["
operator|+
name|bindHost
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|InetAddress
name|hostAddress
init|=
name|hostAddressX
decl_stmt|;
name|PortsRange
name|portsRange
init|=
operator|new
name|PortsRange
argument_list|(
name|port
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
name|lastException
init|=
operator|new
name|AtomicReference
argument_list|<
name|Exception
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
name|portsRange
operator|.
name|iterate
argument_list|(
operator|new
name|PortsRange
operator|.
name|PortCallback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|onPortNumber
parameter_list|(
name|int
name|portNumber
parameter_list|)
block|{
try|try
block|{
name|serverChannel
operator|=
name|serverBootstrap
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|hostAddress
argument_list|,
name|portNumber
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|lastException
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|success
condition|)
block|{
throw|throw
operator|new
name|BindHttpException
argument_list|(
literal|"Failed to bind to ["
operator|+
name|port
operator|+
literal|"]"
argument_list|,
name|lastException
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
name|InetSocketAddress
name|boundAddress
init|=
operator|(
name|InetSocketAddress
operator|)
name|serverChannel
operator|.
name|getLocalAddress
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|publishAddress
decl_stmt|;
try|try
block|{
name|publishAddress
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|networkService
operator|.
name|resolvePublishHostAddress
argument_list|(
name|publishHost
argument_list|)
argument_list|,
name|boundAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BindTransportException
argument_list|(
literal|"Failed to resolve publish address"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|this
operator|.
name|boundAddress
operator|=
operator|new
name|BoundTransportAddress
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
name|boundAddress
argument_list|)
argument_list|,
operator|new
name|InetSocketTransportAddress
argument_list|(
name|publishAddress
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|serverChannel
operator|!=
literal|null
condition|)
block|{
name|serverChannel
operator|.
name|close
argument_list|()
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
name|serverChannel
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|serverOpenChannels
operator|!=
literal|null
condition|)
block|{
name|serverOpenChannels
operator|.
name|close
argument_list|()
expr_stmt|;
name|serverOpenChannels
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|serverBootstrap
operator|!=
literal|null
condition|)
block|{
name|serverBootstrap
operator|.
name|releaseExternalResources
argument_list|()
expr_stmt|;
name|serverBootstrap
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|boundAddress
specifier|public
name|BoundTransportAddress
name|boundAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|boundAddress
return|;
block|}
DECL|method|dispatchRequest
name|void
name|dispatchRequest
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpChannel
name|channel
parameter_list|)
block|{
name|httpServerAdapter
operator|.
name|dispatchRequest
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
DECL|method|exceptionCaught
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
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ReadTimeoutException
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Connection timeout [{}]"
argument_list|,
name|ctx
operator|.
name|getChannel
argument_list|()
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ctx
operator|.
name|getChannel
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
comment|// ignore
return|return;
block|}
if|if
condition|(
operator|!
name|NetworkExceptionHelper
operator|.
name|isCloseConnectionException
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Caught exception while handling client http traffic, closing connection"
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|getChannel
argument_list|()
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

