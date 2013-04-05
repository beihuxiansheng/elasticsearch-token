begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.search.facet
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Callable
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
name|ExecutionException
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
name|ExecutorService
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
name|Future
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

begin_class
DECL|class|ConcurrentDuel
specifier|public
class|class
name|ConcurrentDuel
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|pool
specifier|private
specifier|final
name|ExecutorService
name|pool
decl_stmt|;
DECL|field|numExecutorThreads
specifier|private
specifier|final
name|int
name|numExecutorThreads
decl_stmt|;
DECL|method|ConcurrentDuel
specifier|public
name|ConcurrentDuel
parameter_list|(
name|int
name|numThreads
parameter_list|)
block|{
name|pool
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numThreads
argument_list|)
expr_stmt|;
name|this
operator|.
name|numExecutorThreads
operator|=
name|numThreads
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
DECL|method|runDuel
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|runDuel
parameter_list|(
specifier|final
name|DuelExecutor
argument_list|<
name|T
argument_list|>
name|executor
parameter_list|,
name|int
name|iterations
parameter_list|,
name|int
name|numTasks
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|List
argument_list|<
name|T
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|T
name|firstRun
init|=
name|executor
operator|.
name|run
argument_list|()
decl_stmt|;
name|results
operator|.
name|add
argument_list|(
name|firstRun
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{                  }
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicLong
name|count
init|=
operator|new
name|AtomicLong
argument_list|(
name|iterations
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Future
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|>
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
name|numTasks
condition|;
name|i
operator|++
control|)
block|{
name|futures
operator|.
name|add
argument_list|(
name|pool
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|T
argument_list|>
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|T
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
while|while
condition|(
name|count
operator|.
name|decrementAndGet
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|executor
operator|.
name|run
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
for|for
control|(
name|Future
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|results
operator|.
name|addAll
argument_list|(
name|future
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
DECL|method|duel
specifier|public
name|void
name|duel
parameter_list|(
name|DuelJudge
argument_list|<
name|T
argument_list|>
name|judge
parameter_list|,
specifier|final
name|DuelExecutor
argument_list|<
name|T
argument_list|>
name|executor
parameter_list|,
name|int
name|iterations
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|duel
argument_list|(
name|judge
argument_list|,
name|executor
argument_list|,
name|iterations
argument_list|,
name|numExecutorThreads
argument_list|)
expr_stmt|;
block|}
DECL|method|duel
specifier|public
name|void
name|duel
parameter_list|(
name|DuelJudge
argument_list|<
name|T
argument_list|>
name|judge
parameter_list|,
specifier|final
name|DuelExecutor
argument_list|<
name|T
argument_list|>
name|executor
parameter_list|,
name|int
name|iterations
parameter_list|,
name|int
name|threadCount
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
block|{
name|T
name|firstRun
init|=
name|executor
operator|.
name|run
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|runDuel
init|=
name|runDuel
argument_list|(
name|executor
argument_list|,
name|iterations
argument_list|,
name|threadCount
argument_list|)
decl_stmt|;
for|for
control|(
name|T
name|t
range|:
name|runDuel
control|)
block|{
name|judge
operator|.
name|judge
argument_list|(
name|firstRun
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|interface|DuelExecutor
specifier|public
specifier|static
interface|interface
name|DuelExecutor
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|run
specifier|public
name|T
name|run
parameter_list|()
function_decl|;
block|}
DECL|interface|DuelJudge
specifier|public
specifier|static
interface|interface
name|DuelJudge
parameter_list|<
name|T
parameter_list|>
block|{
DECL|method|judge
specifier|public
name|void
name|judge
parameter_list|(
name|T
name|firstRun
parameter_list|,
name|T
name|result
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

