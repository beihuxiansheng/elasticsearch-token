begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.breaker
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|breaker
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
name|test
operator|.
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
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
name|atomic
operator|.
name|AtomicBoolean
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
name|hamcrest
operator|.
name|Matchers
operator|.
name|equalTo
import|;
end_import

begin_comment
comment|/**  * Tests for the Memory Aggregating Circuit Breaker  */
end_comment

begin_class
DECL|class|MemoryCircuitBreakerTests
specifier|public
class|class
name|MemoryCircuitBreakerTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testThreadedUpdatesToBreaker
specifier|public
name|void
name|testThreadedUpdatesToBreaker
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUM_THREADS
init|=
literal|5
decl_stmt|;
specifier|final
name|int
name|BYTES_PER_THREAD
init|=
literal|1000
decl_stmt|;
specifier|final
name|Thread
index|[]
name|threads
init|=
operator|new
name|Thread
index|[
name|NUM_THREADS
index|]
decl_stmt|;
specifier|final
name|AtomicBoolean
name|tripped
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|lastException
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|MemoryCircuitBreaker
name|breaker
init|=
operator|new
name|MemoryCircuitBreaker
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
operator|(
name|BYTES_PER_THREAD
operator|*
name|NUM_THREADS
operator|)
operator|-
literal|1
argument_list|)
argument_list|,
literal|1.0
argument_list|,
name|logger
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|threads
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
name|BYTES_PER_THREAD
condition|;
name|j
operator|++
control|)
block|{
try|try
block|{
name|breaker
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CircuitBreakingException
name|e
parameter_list|)
block|{
if|if
condition|(
name|tripped
operator|.
name|get
argument_list|()
condition|)
block|{
name|assertThat
argument_list|(
literal|"tripped too many times"
argument_list|,
literal|true
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|tripped
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
assert|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e2
parameter_list|)
block|{
name|lastException
operator|.
name|set
argument_list|(
name|e2
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|threads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|threads
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
literal|"no other exceptions were thrown"
argument_list|,
name|lastException
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"breaker was tripped exactly once"
argument_list|,
name|tripped
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testConstantFactor
specifier|public
name|void
name|testConstantFactor
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|MemoryCircuitBreaker
name|breaker
init|=
operator|new
name|MemoryCircuitBreaker
argument_list|(
operator|new
name|ByteSizeValue
argument_list|(
literal|15
argument_list|)
argument_list|,
literal|1.6
argument_list|,
name|logger
argument_list|)
decl_stmt|;
comment|// add only 7 bytes
name|breaker
operator|.
name|addWithoutBreaking
argument_list|(
literal|7
argument_list|)
expr_stmt|;
try|try
block|{
comment|// this won't actually add it because it trips the breaker
name|breaker
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should never reach this"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CircuitBreakingException
name|cbe
parameter_list|)
block|{
assert|assert
literal|true
assert|;
block|}
comment|// shouldn't throw an exception
name|breaker
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|breaker
operator|.
name|getUsed
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|9L
argument_list|)
argument_list|)
expr_stmt|;
comment|// adding 3 more bytes (now at 12)
name|breaker
operator|.
name|addWithoutBreaking
argument_list|(
literal|3
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Adding no bytes still breaks
name|breaker
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should never reach this"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CircuitBreakingException
name|cbe
parameter_list|)
block|{
assert|assert
literal|true
assert|;
block|}
block|}
block|}
end_class

end_unit

