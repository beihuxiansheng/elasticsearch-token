begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|hotthreads
operator|.
name|NodeHotThreads
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|hotthreads
operator|.
name|NodesHotThreadsRequestBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|hotthreads
operator|.
name|NodesHotThreadsResponse
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
name|TimeValue
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
name|ElasticsearchIntegrationTest
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
name|Map
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|FilterBuilders
operator|.
name|andFilter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|FilterBuilders
operator|.
name|notFilter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|FilterBuilders
operator|.
name|queryFilter
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|matchAllQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryBuilders
operator|.
name|termQuery
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertHitCount
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|notNullValue
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|HotThreadsTest
specifier|public
class|class
name|HotThreadsTest
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testHotThreadsDontFail
specifier|public
name|void
name|testHotThreadsDontFail
parameter_list|()
throws|throws
name|ExecutionException
throws|,
name|InterruptedException
block|{
comment|/**          * This test just checks if nothing crashes or gets stuck etc.          */
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|iters
init|=
name|atLeast
argument_list|(
literal|2
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|type
decl_stmt|;
name|NodesHotThreadsRequestBuilder
name|nodesHotThreadsRequestBuilder
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesHotThreads
argument_list|()
decl_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|TimeValue
name|timeValue
init|=
operator|new
name|TimeValue
argument_list|(
name|rarely
argument_list|()
condition|?
name|randomIntBetween
argument_list|(
literal|500
argument_list|,
literal|5000
argument_list|)
else|:
name|randomIntBetween
argument_list|(
literal|20
argument_list|,
literal|500
argument_list|)
argument_list|)
decl_stmt|;
name|nodesHotThreadsRequestBuilder
operator|.
name|setInterval
argument_list|(
name|timeValue
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|nodesHotThreadsRequestBuilder
operator|.
name|setThreads
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
condition|)
block|{
case|case
literal|2
case|:
name|type
operator|=
literal|"cpu"
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|type
operator|=
literal|"wait"
expr_stmt|;
break|break;
default|default:
name|type
operator|=
literal|"block"
expr_stmt|;
break|break;
block|}
name|assertThat
argument_list|(
name|type
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|nodesHotThreadsRequestBuilder
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|type
operator|=
literal|null
expr_stmt|;
block|}
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
name|nodesHotThreadsRequestBuilder
operator|.
name|execute
argument_list|(
operator|new
name|ActionListener
argument_list|<
name|NodesHotThreadsResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|NodesHotThreadsResponse
name|nodeHotThreads
parameter_list|)
block|{
try|try
block|{
name|assertThat
argument_list|(
name|nodeHotThreads
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|NodeHotThreads
argument_list|>
name|nodesMap
init|=
name|nodeHotThreads
operator|.
name|getNodesMap
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|nodesMap
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|cluster
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|NodeHotThreads
name|ht
range|:
name|nodeHotThreads
control|)
block|{
name|assertNotNull
argument_list|(
name|ht
operator|.
name|getHotThreads
argument_list|()
argument_list|)
expr_stmt|;
comment|//logger.info(ht.getHotThreads());
block|}
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"FAILED"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|indexRandom
argument_list|(
literal|true
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"2"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|,
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type1"
argument_list|,
literal|"3"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field1"
argument_list|,
literal|"value3"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureSearchable
argument_list|()
expr_stmt|;
while|while
condition|(
name|latch
operator|.
name|getCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|()
operator|.
name|setQuery
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
operator|.
name|setPostFilter
argument_list|(
name|andFilter
argument_list|(
name|queryFilter
argument_list|(
name|matchAllQuery
argument_list|()
argument_list|)
argument_list|,
name|notFilter
argument_list|(
name|andFilter
argument_list|(
name|queryFilter
argument_list|(
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"value1"
argument_list|)
argument_list|)
argument_list|,
name|queryFilter
argument_list|(
name|termQuery
argument_list|(
literal|"field1"
argument_list|,
literal|"value2"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|3l
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

