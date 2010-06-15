begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
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
name|util
operator|.
name|concurrent
operator|.
name|DynamicExecutors
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
name|ThreadBarrier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|testng
operator|.
name|annotations
operator|.
name|Test
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
name|RejectedExecutionException
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
name|ThreadPoolExecutor
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|BlockingThreadPoolTest
specifier|public
class|class
name|BlockingThreadPoolTest
block|{
DECL|method|testBlocking
annotation|@
name|Test
specifier|public
name|void
name|testBlocking
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|min
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|max
init|=
literal|4
decl_stmt|;
specifier|final
name|long
name|waitTime
init|=
literal|1000
decl_stmt|;
comment|//1 second
specifier|final
name|ThreadBarrier
name|barrier
init|=
operator|new
name|ThreadBarrier
argument_list|(
name|max
operator|+
literal|1
argument_list|)
decl_stmt|;
name|ThreadPoolExecutor
name|pool
init|=
operator|(
name|ThreadPoolExecutor
operator|)
name|DynamicExecutors
operator|.
name|newBlockingThreadPool
argument_list|(
name|min
argument_list|,
name|max
argument_list|,
literal|60000
argument_list|,
literal|1
argument_list|,
name|waitTime
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
literal|"Min property"
argument_list|,
name|pool
operator|.
name|getCorePoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Max property"
argument_list|,
name|pool
operator|.
name|getMaximumPoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|max
argument_list|)
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
name|max
condition|;
operator|++
name|i
control|)
block|{
name|pool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|barrier
operator|.
name|reset
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|//wait until thread executes this task
comment|//otherwise, a task might be queued
name|Thread
operator|.
name|sleep
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
literal|"wrong pool size"
argument_list|,
name|pool
operator|.
name|getPoolSize
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"wrong active size"
argument_list|,
name|pool
operator|.
name|getActiveCount
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
comment|//Queue should be empty, lets occupy it's only free space
name|assertThat
argument_list|(
literal|"queue isn't empty"
argument_list|,
name|pool
operator|.
name|getQueue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|pool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|//dummy task
block|}
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"queue isn't full"
argument_list|,
name|pool
operator|.
name|getQueue
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|//request should block since queue is full
try|try
block|{
name|pool
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|//dummy task
block|}
block|}
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Should have thrown RejectedExecutionException"
argument_list|,
literal|false
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RejectedExecutionException
name|e
parameter_list|)
block|{
comment|//caught expected exception
block|}
name|barrier
operator|.
name|await
argument_list|()
expr_stmt|;
name|pool
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

