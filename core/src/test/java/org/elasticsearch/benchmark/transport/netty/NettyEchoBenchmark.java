begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.benchmark.transport.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|benchmark
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
name|jboss
operator|.
name|netty
operator|.
name|bootstrap
operator|.
name|ClientBootstrap
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
name|bootstrap
operator|.
name|ServerBootstrap
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
name|*
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
name|socket
operator|.
name|nio
operator|.
name|NioClientSocketChannelFactory
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
name|socket
operator|.
name|nio
operator|.
name|NioServerSocketChannelFactory
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
name|CountDownLatch
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

begin_class
DECL|class|NettyEchoBenchmark
specifier|public
class|class
name|NettyEchoBenchmark
block|{
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|int
name|payloadSize
init|=
literal|100
decl_stmt|;
name|int
name|CYCLE_SIZE
init|=
literal|50000
decl_stmt|;
specifier|final
name|long
name|NUMBER_OF_ITERATIONS
init|=
literal|500000
decl_stmt|;
name|ChannelBuffer
name|message
init|=
name|ChannelBuffers
operator|.
name|buffer
argument_list|(
literal|100
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
name|message
operator|.
name|capacity
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|message
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
comment|// Configure the server.
name|ServerBootstrap
name|serverBootstrap
init|=
operator|new
name|ServerBootstrap
argument_list|(
operator|new
name|NioServerSocketChannelFactory
argument_list|(
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// Set up the pipeline factory.
name|serverBootstrap
operator|.
name|setPipelineFactory
argument_list|(
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
return|return
name|Channels
operator|.
name|pipeline
argument_list|(
operator|new
name|EchoServerHandler
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Bind and start to accept incoming connections.
name|serverBootstrap
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|,
literal|9000
argument_list|)
argument_list|)
expr_stmt|;
name|ClientBootstrap
name|clientBootstrap
init|=
operator|new
name|ClientBootstrap
argument_list|(
operator|new
name|NioClientSocketChannelFactory
argument_list|(
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
argument_list|,
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|//        ClientBootstrap clientBootstrap = new ClientBootstrap(
comment|//                new OioClientSocketChannelFactory(Executors.newCachedThreadPool()));
comment|// Set up the pipeline factory.
specifier|final
name|EchoClientHandler
name|clientHandler
init|=
operator|new
name|EchoClientHandler
argument_list|()
decl_stmt|;
name|clientBootstrap
operator|.
name|setPipelineFactory
argument_list|(
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
return|return
name|Channels
operator|.
name|pipeline
argument_list|(
name|clientHandler
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// Start the connection attempt.
name|ChannelFuture
name|future
init|=
name|clientBootstrap
operator|.
name|connect
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|InetAddress
operator|.
name|getLoopbackAddress
argument_list|()
argument_list|,
literal|9000
argument_list|)
argument_list|)
decl_stmt|;
name|future
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
name|Channel
name|clientChannel
init|=
name|future
operator|.
name|getChannel
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Warming up..."
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|clientHandler
operator|.
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|clientChannel
operator|.
name|write
argument_list|(
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|clientHandler
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Warmed up"
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|cycleStart
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|1
init|;
name|i
operator|<
name|NUMBER_OF_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|clientHandler
operator|.
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|clientChannel
operator|.
name|write
argument_list|(
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|clientHandler
operator|.
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|i
operator|%
name|CYCLE_SIZE
operator|)
operator|==
literal|0
condition|)
block|{
name|long
name|cycleEnd
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ran 50000, TPS "
operator|+
operator|(
name|CYCLE_SIZE
operator|/
operator|(
call|(
name|double
call|)
argument_list|(
name|cycleEnd
operator|-
name|cycleStart
argument_list|)
operator|/
literal|1000
operator|)
operator|)
argument_list|)
expr_stmt|;
name|cycleStart
operator|=
name|cycleEnd
expr_stmt|;
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|seconds
init|=
operator|(
name|end
operator|-
name|start
operator|)
operator|/
literal|1000
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ran ["
operator|+
name|NUMBER_OF_ITERATIONS
operator|+
literal|"] iterations, payload ["
operator|+
name|payloadSize
operator|+
literal|"]: took ["
operator|+
name|seconds
operator|+
literal|"], TPS: "
operator|+
operator|(
operator|(
name|double
operator|)
name|NUMBER_OF_ITERATIONS
operator|)
operator|/
name|seconds
argument_list|)
expr_stmt|;
name|clientChannel
operator|.
name|close
argument_list|()
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
name|clientBootstrap
operator|.
name|releaseExternalResources
argument_list|()
expr_stmt|;
name|serverBootstrap
operator|.
name|releaseExternalResources
argument_list|()
expr_stmt|;
block|}
DECL|class|EchoClientHandler
specifier|public
specifier|static
class|class
name|EchoClientHandler
extends|extends
name|SimpleChannelUpstreamHandler
block|{
DECL|field|latch
specifier|public
specifier|volatile
name|CountDownLatch
name|latch
decl_stmt|;
DECL|method|EchoClientHandler
specifier|public
name|EchoClientHandler
parameter_list|()
block|{         }
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
block|{
name|latch
operator|.
name|countDown
argument_list|()
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
block|{
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|e
operator|.
name|getChannel
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|EchoServerHandler
specifier|public
specifier|static
class|class
name|EchoServerHandler
extends|extends
name|SimpleChannelUpstreamHandler
block|{
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
block|{
name|e
operator|.
name|getChannel
argument_list|()
operator|.
name|write
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
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
block|{
comment|// Close the connection when an exception is raised.
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|e
operator|.
name|getChannel
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

