begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.netty.benchmark
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
operator|.
name|benchmark
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|threadpool
operator|.
name|cached
operator|.
name|CachedThreadPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|timer
operator|.
name|TimerService
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
name|BaseTransportResponseHandler
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
name|TransportService
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
name|NettyTransport
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
name|SizeUnit
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
name|SizeValue
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
name|StopWatch
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
name|settings
operator|.
name|ImmutableSettings
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
name|util
operator|.
name|transport
operator|.
name|InetSocketTransportAddress
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|BenchmarkNettyClient
specifier|public
class|class
name|BenchmarkNettyClient
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
block|{
specifier|final
name|SizeValue
name|payloadSize
init|=
operator|new
name|SizeValue
argument_list|(
literal|100
argument_list|,
name|SizeUnit
operator|.
name|BYTES
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUMBER_OF_CLIENTS
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|NUMBER_OF_ITERATIONS
init|=
literal|500000
decl_stmt|;
specifier|final
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|payloadSize
operator|.
name|bytes
argument_list|()
index|]
decl_stmt|;
specifier|final
name|AtomicLong
name|idGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|waitForRequest
init|=
literal|false
decl_stmt|;
specifier|final
name|boolean
name|spawn
init|=
literal|true
decl_stmt|;
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"network.server"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"transport.netty.connectionsPerNode"
argument_list|,
literal|5
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|ThreadPool
name|threadPool
init|=
operator|new
name|CachedThreadPool
argument_list|(
name|settings
argument_list|)
decl_stmt|;
specifier|final
name|TimerService
name|timerService
init|=
operator|new
name|TimerService
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
decl_stmt|;
specifier|final
name|TransportService
name|transportService
init|=
operator|new
name|TransportService
argument_list|(
operator|new
name|NettyTransport
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|)
argument_list|,
name|threadPool
argument_list|,
name|timerService
argument_list|)
operator|.
name|start
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"server"
argument_list|,
operator|new
name|InetSocketTransportAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|9999
argument_list|)
argument_list|)
decl_stmt|;
name|transportService
operator|.
name|connectToNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|Thread
index|[]
name|clients
init|=
operator|new
name|Thread
index|[
name|NUMBER_OF_CLIENTS
index|]
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|NUMBER_OF_CLIENTS
operator|*
name|NUMBER_OF_ITERATIONS
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
name|NUMBER_OF_CLIENTS
condition|;
name|i
operator|++
control|)
block|{
name|clients
index|[
name|i
index|]
operator|=
operator|new
name|Thread
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|NUMBER_OF_ITERATIONS
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|long
name|id
init|=
name|idGenerator
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|BenchmarkMessage
name|message
init|=
operator|new
name|BenchmarkMessage
argument_list|(
name|id
argument_list|,
name|payload
argument_list|)
decl_stmt|;
name|BaseTransportResponseHandler
argument_list|<
name|BenchmarkMessage
argument_list|>
name|handler
init|=
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|BenchmarkMessage
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BenchmarkMessage
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|BenchmarkMessage
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|BenchmarkMessage
name|response
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|id
operator|!=
name|id
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"NO ID MATCH ["
operator|+
name|response
operator|.
name|id
operator|+
literal|"] and ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|exp
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
name|spawn
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|waitForRequest
condition|)
block|{
name|transportService
operator|.
name|submitRequest
argument_list|(
name|node
argument_list|,
literal|"benchmark"
argument_list|,
name|message
argument_list|,
name|handler
argument_list|)
operator|.
name|txGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
literal|"benchmark"
argument_list|,
name|message
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|StopWatch
name|stopWatch
init|=
operator|new
name|StopWatch
argument_list|()
operator|.
name|start
argument_list|()
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
name|NUMBER_OF_CLIENTS
condition|;
name|i
operator|++
control|)
block|{
name|clients
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
try|try
block|{
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
name|stopWatch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Ran ["
operator|+
name|NUMBER_OF_CLIENTS
operator|+
literal|"], each with ["
operator|+
name|NUMBER_OF_ITERATIONS
operator|+
literal|"] iterations, payload ["
operator|+
name|payloadSize
operator|+
literal|"]: took ["
operator|+
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|+
literal|"], TPS: "
operator|+
operator|(
name|NUMBER_OF_CLIENTS
operator|*
name|NUMBER_OF_ITERATIONS
operator|)
operator|/
name|stopWatch
operator|.
name|totalTime
argument_list|()
operator|.
name|secondsFrac
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|close
argument_list|()
expr_stmt|;
name|threadPool
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

