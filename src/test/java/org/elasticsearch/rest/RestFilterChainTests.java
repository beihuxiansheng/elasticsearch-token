begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|Nullable
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
name|bytes
operator|.
name|BytesReference
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilder
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
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|FakeRestRequest
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_class
DECL|class|RestFilterChainTests
specifier|public
class|class
name|RestFilterChainTests
extends|extends
name|ElasticsearchTestCase
block|{
annotation|@
name|Test
DECL|method|testRestFilters
specifier|public
name|void
name|testRestFilters
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|RestController
name|restController
init|=
operator|new
name|RestController
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|int
name|numFilters
init|=
name|randomInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Integer
argument_list|>
name|orders
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|numFilters
argument_list|)
decl_stmt|;
while|while
condition|(
name|orders
operator|.
name|size
argument_list|()
operator|<
name|numFilters
condition|)
block|{
name|orders
operator|.
name|add
argument_list|(
name|randomInt
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|RestFilter
argument_list|>
name|filters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Integer
name|order
range|:
name|orders
control|)
block|{
name|TestFilter
name|testFilter
init|=
operator|new
name|TestFilter
argument_list|(
name|order
argument_list|,
name|randomFrom
argument_list|(
name|Operation
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|filters
operator|.
name|add
argument_list|(
name|testFilter
argument_list|)
expr_stmt|;
name|restController
operator|.
name|registerFilter
argument_list|(
name|testFilter
argument_list|)
expr_stmt|;
block|}
name|ArrayList
argument_list|<
name|RestFilter
argument_list|>
name|restFiltersByOrder
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|filters
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|restFiltersByOrder
argument_list|,
operator|new
name|Comparator
argument_list|<
name|RestFilter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|RestFilter
name|o1
parameter_list|,
name|RestFilter
name|o2
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|order
argument_list|()
argument_list|,
name|o2
operator|.
name|order
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RestFilter
argument_list|>
name|expectedRestFilters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RestFilter
name|filter
range|:
name|restFiltersByOrder
control|)
block|{
name|TestFilter
name|testFilter
init|=
operator|(
name|TestFilter
operator|)
name|filter
decl_stmt|;
name|expectedRestFilters
operator|.
name|add
argument_list|(
name|testFilter
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|testFilter
operator|.
name|callback
operator|==
name|Operation
operator|.
name|CONTINUE_PROCESSING
operator|)
condition|)
block|{
break|break;
block|}
block|}
name|restController
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/"
argument_list|,
operator|new
name|RestHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|TestResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|FakeRestRequest
name|fakeRestRequest
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|FakeRestChannel
name|fakeRestChannel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|fakeRestRequest
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|restController
operator|.
name|dispatchRequest
argument_list|(
name|fakeRestRequest
argument_list|,
name|fakeRestChannel
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fakeRestChannel
operator|.
name|await
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|TestFilter
argument_list|>
name|testFiltersByLastExecution
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RestFilter
name|restFilter
range|:
name|filters
control|)
block|{
name|testFiltersByLastExecution
operator|.
name|add
argument_list|(
operator|(
name|TestFilter
operator|)
name|restFilter
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|testFiltersByLastExecution
argument_list|,
operator|new
name|Comparator
argument_list|<
name|TestFilter
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|TestFilter
name|o1
parameter_list|,
name|TestFilter
name|o2
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|executionToken
argument_list|,
name|o2
operator|.
name|executionToken
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|TestFilter
argument_list|>
name|finalTestFilters
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|RestFilter
name|filter
range|:
name|testFiltersByLastExecution
control|)
block|{
name|TestFilter
name|testFilter
init|=
operator|(
name|TestFilter
operator|)
name|filter
decl_stmt|;
name|finalTestFilters
operator|.
name|add
argument_list|(
name|testFilter
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|testFilter
operator|.
name|callback
operator|==
name|Operation
operator|.
name|CONTINUE_PROCESSING
operator|)
condition|)
block|{
break|break;
block|}
block|}
name|assertThat
argument_list|(
name|finalTestFilters
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|expectedRestFilters
operator|.
name|size
argument_list|()
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
name|finalTestFilters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TestFilter
name|testFilter
init|=
name|finalTestFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|testFilter
argument_list|,
name|equalTo
argument_list|(
name|expectedRestFilters
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|testFilter
operator|.
name|runs
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testTooManyContinueProcessing
specifier|public
name|void
name|testTooManyContinueProcessing
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|additionalContinueCount
init|=
name|randomInt
argument_list|(
literal|10
argument_list|)
decl_stmt|;
name|TestFilter
name|testFilter
init|=
operator|new
name|TestFilter
argument_list|(
name|randomInt
argument_list|()
argument_list|,
operator|new
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|RestRequest
name|request
parameter_list|,
specifier|final
name|RestChannel
name|channel
parameter_list|,
specifier|final
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|additionalContinueCount
condition|;
name|i
operator|++
control|)
block|{
name|filterChain
operator|.
name|continueProcessing
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|RestController
name|restController
init|=
operator|new
name|RestController
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
name|restController
operator|.
name|registerFilter
argument_list|(
name|testFilter
argument_list|)
expr_stmt|;
name|restController
operator|.
name|registerHandler
argument_list|(
name|RestRequest
operator|.
name|Method
operator|.
name|GET
argument_list|,
literal|"/"
argument_list|,
operator|new
name|RestHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|handleRequest
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|TestResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|FakeRestRequest
name|fakeRestRequest
init|=
operator|new
name|FakeRestRequest
argument_list|()
decl_stmt|;
name|FakeRestChannel
name|fakeRestChannel
init|=
operator|new
name|FakeRestChannel
argument_list|(
name|fakeRestRequest
argument_list|,
name|additionalContinueCount
operator|+
literal|1
argument_list|)
decl_stmt|;
name|restController
operator|.
name|dispatchRequest
argument_list|(
name|fakeRestRequest
argument_list|,
name|fakeRestChannel
argument_list|)
expr_stmt|;
name|fakeRestChannel
operator|.
name|await
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|testFilter
operator|.
name|runs
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fakeRestChannel
operator|.
name|responses
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fakeRestChannel
operator|.
name|errors
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|additionalContinueCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|FakeRestChannel
specifier|private
specifier|static
class|class
name|FakeRestChannel
extends|extends
name|RestChannel
block|{
DECL|field|latch
specifier|private
specifier|final
name|CountDownLatch
name|latch
decl_stmt|;
DECL|field|responses
name|AtomicInteger
name|responses
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|errors
name|AtomicInteger
name|errors
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|FakeRestChannel
specifier|protected
name|FakeRestChannel
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|int
name|responseCount
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|,
name|randomBoolean
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|latch
operator|=
operator|new
name|CountDownLatch
argument_list|(
name|responseCount
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newBuilder
specifier|public
name|XContentBuilder
name|newBuilder
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|newBuilder
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|newBuilder
specifier|public
name|XContentBuilder
name|newBuilder
parameter_list|(
annotation|@
name|Nullable
name|BytesReference
name|autoDetectSource
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|newBuilder
argument_list|(
name|autoDetectSource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|newBytesOutput
specifier|protected
name|BytesStreamOutput
name|newBytesOutput
parameter_list|()
block|{
return|return
name|super
operator|.
name|newBytesOutput
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|request
specifier|public
name|RestRequest
name|request
parameter_list|()
block|{
return|return
name|super
operator|.
name|request
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|RestResponse
name|response
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|status
argument_list|()
operator|==
name|RestStatus
operator|.
name|OK
condition|)
block|{
name|responses
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|errors
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
DECL|method|await
specifier|public
name|boolean
name|await
parameter_list|()
throws|throws
name|InterruptedException
block|{
return|return
name|latch
operator|.
name|await
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
block|}
DECL|enum|Operation
specifier|private
specifier|static
enum|enum
name|Operation
implements|implements
name|Callback
block|{
DECL|enum constant|CONTINUE_PROCESSING
name|CONTINUE_PROCESSING
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
name|filterChain
operator|.
name|continueProcessing
argument_list|(
name|request
argument_list|,
name|channel
argument_list|)
expr_stmt|;
block|}
block|}
block|,
DECL|enum constant|CHANNEL_RESPONSE
name|CHANNEL_RESPONSE
block|{
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|TestResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|interface|Callback
specifier|private
specifier|static
interface|interface
name|Callback
block|{
DECL|method|execute
name|void
name|execute
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
DECL|field|counter
specifier|private
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|class|TestFilter
specifier|private
class|class
name|TestFilter
extends|extends
name|RestFilter
block|{
DECL|field|order
specifier|private
specifier|final
name|int
name|order
decl_stmt|;
DECL|field|callback
specifier|private
specifier|final
name|Callback
name|callback
decl_stmt|;
DECL|field|runs
name|AtomicInteger
name|runs
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|executionToken
specifier|volatile
name|int
name|executionToken
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
comment|//the filters that don't run will go last in the sorted list
DECL|method|TestFilter
name|TestFilter
parameter_list|(
name|int
name|order
parameter_list|,
name|Callback
name|callback
parameter_list|)
block|{
name|this
operator|.
name|order
operator|=
name|order
expr_stmt|;
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|void
name|process
parameter_list|(
name|RestRequest
name|request
parameter_list|,
name|RestChannel
name|channel
parameter_list|,
name|RestFilterChain
name|filterChain
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|runs
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|executionToken
operator|=
name|counter
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|this
operator|.
name|callback
operator|.
name|execute
argument_list|(
name|request
argument_list|,
name|channel
argument_list|,
name|filterChain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|order
specifier|public
name|int
name|order
parameter_list|()
block|{
return|return
name|order
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"[order:"
operator|+
name|order
operator|+
literal|", executionToken:"
operator|+
name|executionToken
operator|+
literal|"]"
return|;
block|}
block|}
DECL|class|TestResponse
specifier|private
specifier|static
class|class
name|TestResponse
extends|extends
name|RestResponse
block|{
annotation|@
name|Override
DECL|method|contentType
specifier|public
name|String
name|contentType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|content
specifier|public
name|BytesReference
name|content
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|RestStatus
operator|.
name|OK
return|;
block|}
block|}
block|}
end_class

end_unit

