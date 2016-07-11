begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|FailedNodeException
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
name|support
operator|.
name|nodes
operator|.
name|BaseNodeResponse
import|;
end_import

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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNodes
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
name|logging
operator|.
name|Loggers
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
name|LocalTransportAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
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
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|ConcurrentHashMap
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
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptySet
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|sameInstance
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|AsyncShardFetchTests
specifier|public
class|class
name|AsyncShardFetchTests
extends|extends
name|ESTestCase
block|{
DECL|field|node1
specifier|private
specifier|final
name|DiscoveryNode
name|node1
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"node1"
argument_list|,
name|LocalTransportAddress
operator|.
name|buildUnique
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|DATA
argument_list|)
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
DECL|field|response1
specifier|private
specifier|final
name|Response
name|response1
init|=
operator|new
name|Response
argument_list|(
name|node1
argument_list|)
decl_stmt|;
DECL|field|failure1
specifier|private
specifier|final
name|Throwable
name|failure1
init|=
operator|new
name|Throwable
argument_list|(
literal|"simulated failure 1"
argument_list|)
decl_stmt|;
DECL|field|node2
specifier|private
specifier|final
name|DiscoveryNode
name|node2
init|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"node2"
argument_list|,
name|LocalTransportAddress
operator|.
name|buildUnique
argument_list|()
argument_list|,
name|Collections
operator|.
name|emptyMap
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
name|DiscoveryNode
operator|.
name|Role
operator|.
name|DATA
argument_list|)
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
decl_stmt|;
DECL|field|response2
specifier|private
specifier|final
name|Response
name|response2
init|=
operator|new
name|Response
argument_list|(
name|node2
argument_list|)
decl_stmt|;
DECL|field|failure2
specifier|private
specifier|final
name|Throwable
name|failure2
init|=
operator|new
name|Throwable
argument_list|(
literal|"simulate failure 2"
argument_list|)
decl_stmt|;
DECL|field|threadPool
specifier|private
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|test
specifier|private
name|TestFetch
name|test
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
operator|new
name|TestThreadPool
argument_list|(
name|getTestName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|test
operator|=
operator|new
name|TestFetch
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|terminate
specifier|public
name|void
name|terminate
parameter_list|()
throws|throws
name|Exception
block|{
name|terminate
argument_list|(
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|testClose
specifier|public
name|void
name|testClose
parameter_list|()
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|node1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|response1
argument_list|)
expr_stmt|;
comment|// first fetch, no data, still on going
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|Response
argument_list|>
name|fetchData
init|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire a response, wait on reroute incrementing
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify we get back the data node
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
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
name|test
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"fetch data should fail when closed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
block|}
DECL|method|testFullCircleSingleNodeSuccess
specifier|public
name|void
name|testFullCircleSingleNodeSuccess
parameter_list|()
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|node1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|response1
argument_list|)
expr_stmt|;
comment|// first fetch, no data, still on going
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|Response
argument_list|>
name|fetchData
init|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire a response, wait on reroute incrementing
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// verify we get back the data node
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
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
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
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
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node1
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testFullCircleSingleNodeFailure
specifier|public
name|void
name|testFullCircleSingleNodeFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|node1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// add a failed response for node1
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|failure1
argument_list|)
expr_stmt|;
comment|// first fetch, no data, still on going
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|Response
argument_list|>
name|fetchData
init|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire a response, wait on reroute incrementing
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// failure, fetched data exists, but has no data
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
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
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
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
comment|// on failure, we reset the failure on a successive call to fetchData, and try again afterwards
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|response1
argument_list|)
expr_stmt|;
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// 2 reroutes, cause we have a failure that we clear
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
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
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node1
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoNodesOnSetup
specifier|public
name|void
name|testTwoNodesOnSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|node1
argument_list|)
operator|.
name|put
argument_list|(
name|node2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|response1
argument_list|)
expr_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node2
operator|.
name|getId
argument_list|()
argument_list|,
name|response2
argument_list|)
expr_stmt|;
comment|// no fetched data, 2 requests still on going
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|Response
argument_list|>
name|fetchData
init|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire the first response, it should trigger a reroute
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// there is still another on going request, so no data
name|assertThat
argument_list|(
name|test
operator|.
name|getNumberOfInFlightFetches
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire the second simulation, this should allow us to get the data
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// no more ongoing requests, we should fetch the data
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node1
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node2
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoNodesOnSetupAndFailure
specifier|public
name|void
name|testTwoNodesOnSetupAndFailure
parameter_list|()
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|node1
argument_list|)
operator|.
name|put
argument_list|(
name|node2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|response1
argument_list|)
expr_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node2
operator|.
name|getId
argument_list|()
argument_list|,
name|failure2
argument_list|)
expr_stmt|;
comment|// no fetched data, 2 requests still on going
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|Response
argument_list|>
name|fetchData
init|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire the first response, it should trigger a reroute
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
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
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire the second simulation, this should allow us to get the data
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// since one of those failed, we should only have one entry
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
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
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node1
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTwoNodesAddedInBetween
specifier|public
name|void
name|testTwoNodesAddedInBetween
parameter_list|()
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|node1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|,
name|response1
argument_list|)
expr_stmt|;
comment|// no fetched data, 2 requests still on going
name|AsyncShardFetch
operator|.
name|FetchResult
argument_list|<
name|Response
argument_list|>
name|fetchData
init|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|test
operator|.
name|reroute
operator|.
name|get
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire the first response, it should trigger a reroute
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node1
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// now, add a second node to the nodes, it should add it to the ongoing requests
name|nodes
operator|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|nodes
argument_list|)
operator|.
name|put
argument_list|(
name|node2
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|test
operator|.
name|addSimulation
argument_list|(
name|node2
operator|.
name|getId
argument_list|()
argument_list|,
name|response2
argument_list|)
expr_stmt|;
comment|// no fetch data, has a new node introduced
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// fire the second simulation, this should allow us to get the data
name|test
operator|.
name|fireSimulationAndWait
argument_list|(
name|node2
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
comment|// since one of those failed, we should only have one entry
name|fetchData
operator|=
name|test
operator|.
name|fetchData
argument_list|(
name|nodes
argument_list|,
name|emptySet
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|hasData
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node1
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fetchData
operator|.
name|getData
argument_list|()
operator|.
name|get
argument_list|(
name|node2
argument_list|)
argument_list|,
name|sameInstance
argument_list|(
name|response2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|TestFetch
specifier|static
class|class
name|TestFetch
extends|extends
name|AsyncShardFetch
argument_list|<
name|Response
argument_list|>
block|{
DECL|class|Entry
specifier|static
class|class
name|Entry
block|{
DECL|field|response
specifier|public
specifier|final
name|Response
name|response
decl_stmt|;
DECL|field|failure
specifier|public
specifier|final
name|Throwable
name|failure
decl_stmt|;
DECL|field|executeLatch
specifier|private
specifier|final
name|CountDownLatch
name|executeLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|waitLatch
specifier|private
specifier|final
name|CountDownLatch
name|waitLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|method|Entry
specifier|public
name|Entry
parameter_list|(
name|Response
name|response
parameter_list|,
name|Throwable
name|failure
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
name|this
operator|.
name|failure
operator|=
name|failure
expr_stmt|;
block|}
block|}
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|simulations
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Entry
argument_list|>
name|simulations
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reroute
specifier|private
name|AtomicInteger
name|reroute
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|method|TestFetch
specifier|public
name|TestFetch
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|Loggers
operator|.
name|getLogger
argument_list|(
name|TestFetch
operator|.
name|class
argument_list|)
argument_list|,
literal|"test"
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|"_na_"
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
block|}
DECL|method|addSimulation
specifier|public
name|void
name|addSimulation
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
name|simulations
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
operator|new
name|Entry
argument_list|(
name|response
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|addSimulation
specifier|public
name|void
name|addSimulation
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|simulations
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
operator|new
name|Entry
argument_list|(
literal|null
argument_list|,
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|fireSimulationAndWait
specifier|public
name|void
name|fireSimulationAndWait
parameter_list|(
name|String
name|nodeId
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|simulations
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|executeLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|simulations
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
operator|.
name|waitLatch
operator|.
name|await
argument_list|()
expr_stmt|;
name|simulations
operator|.
name|remove
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reroute
specifier|protected
name|void
name|reroute
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
name|reroute
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|asyncFetch
specifier|protected
name|void
name|asyncFetch
parameter_list|(
specifier|final
name|ShardId
name|shardId
parameter_list|,
name|DiscoveryNode
index|[]
name|nodes
parameter_list|)
block|{
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|nodes
control|)
block|{
specifier|final
name|String
name|nodeId
init|=
name|node
operator|.
name|getId
argument_list|()
decl_stmt|;
name|threadPool
operator|.
name|generic
argument_list|()
operator|.
name|execute
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
name|Entry
name|entry
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entry
operator|=
name|simulations
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
comment|// we are simulating a master node switch, wait for it to not be null
name|awaitBusy
argument_list|(
parameter_list|()
lambda|->
name|simulations
operator|.
name|containsKey
argument_list|(
name|nodeId
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
name|entry
operator|!=
literal|null
assert|;
name|entry
operator|.
name|executeLatch
operator|.
name|await
argument_list|()
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|failure
operator|!=
literal|null
condition|)
block|{
name|processAsyncFetch
argument_list|(
name|shardId
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FailedNodeException
argument_list|(
name|nodeId
argument_list|,
literal|"unexpected"
argument_list|,
name|entry
operator|.
name|failure
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processAsyncFetch
argument_list|(
name|shardId
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|entry
operator|.
name|response
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|entry
operator|.
name|waitLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Response
specifier|static
class|class
name|Response
extends|extends
name|BaseNodeResponse
block|{
DECL|method|Response
specifier|public
name|Response
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

