begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
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
name|test
operator|.
name|ESTestCase
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
name|TestThreadPool
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
name|Transports
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
name|TimeUnit
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

begin_class
DECL|class|ListenableActionFutureTests
specifier|public
class|class
name|ListenableActionFutureTests
extends|extends
name|ESTestCase
block|{
DECL|method|testListenerIsCallableFromNetworkThreads
specifier|public
name|void
name|testListenerIsCallableFromNetworkThreads
parameter_list|()
throws|throws
name|Throwable
block|{
name|ThreadPool
name|threadPool
init|=
operator|new
name|TestThreadPool
argument_list|(
literal|"testListenerIsCallableFromNetworkThreads"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|PlainListenableActionFuture
argument_list|<
name|Object
argument_list|>
name|future
init|=
operator|new
name|PlainListenableActionFuture
argument_list|<>
argument_list|(
name|threadPool
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|listenerCalled
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|error
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Object
name|response
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|future
operator|.
name|addListener
argument_list|(
operator|new
name|ActionListener
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|listenerCalled
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|listenerCalled
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|Thread
name|networkThread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|error
operator|.
name|set
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|listenerCalled
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
name|future
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|,
name|Transports
operator|.
name|TEST_MOCK_TRANSPORT_THREAD_PREFIX
operator|+
literal|"_testListenerIsCallableFromNetworkThread"
argument_list|)
decl_stmt|;
name|networkThread
operator|.
name|start
argument_list|()
expr_stmt|;
name|networkThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|listenerCalled
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|error
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
name|error
operator|.
name|get
argument_list|()
throw|;
block|}
block|}
finally|finally
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|threadPool
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

