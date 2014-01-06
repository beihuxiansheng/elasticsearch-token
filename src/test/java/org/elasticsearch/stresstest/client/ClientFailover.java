begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.stresstest.client
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|stresstest
operator|.
name|client
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
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|transport
operator|.
name|TransportClient
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
name|node
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|NodeBuilder
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
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ClientFailover
specifier|public
class|class
name|ClientFailover
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
name|Node
index|[]
name|nodes
init|=
operator|new
name|Node
index|[
literal|3
index|]
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
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|node
argument_list|()
expr_stmt|;
block|}
specifier|final
name|TransportClient
name|client
init|=
operator|new
name|TransportClient
argument_list|()
operator|.
name|addTransportAddress
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|9300
argument_list|)
argument_list|)
operator|.
name|addTransportAddress
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|9301
argument_list|)
argument_list|)
operator|.
name|addTransportAddress
argument_list|(
operator|new
name|InetSocketTransportAddress
argument_list|(
literal|"localhost"
argument_list|,
literal|9302
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|done
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|final
name|AtomicLong
name|indexed
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
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
name|Thread
name|indexer
init|=
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
while|while
condition|(
operator|!
name|done
operator|.
name|get
argument_list|()
condition|)
block|{
try|try
block|{
name|client
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|indexed
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
name|indexer
operator|.
name|start
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|int
name|index
init|=
name|i
operator|%
name|nodes
operator|.
name|length
decl_stmt|;
name|nodes
index|[
name|index
index|]
operator|.
name|close
argument_list|()
expr_stmt|;
name|ClusterHealthResponse
name|health
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|health
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"timed out on health"
argument_list|)
expr_stmt|;
block|}
name|nodes
index|[
name|index
index|]
operator|=
name|NodeBuilder
operator|.
name|nodeBuilder
argument_list|()
operator|.
name|node
argument_list|()
expr_stmt|;
name|health
operator|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|health
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"timed out on health"
argument_list|)
expr_stmt|;
block|}
block|}
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
comment|// TODO add verification to the number of indexed docs
block|}
block|}
end_class

end_unit

