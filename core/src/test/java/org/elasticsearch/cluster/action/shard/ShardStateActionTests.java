begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.action.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|action
operator|.
name|shard
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|CorruptIndexException
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
name|ClusterState
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
name|cluster
operator|.
name|routing
operator|.
name|IndexRoutingTable
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
name|routing
operator|.
name|ShardRouting
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
name|routing
operator|.
name|ShardsIterator
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
name|Settings
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
name|ESTestCase
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
name|cluster
operator|.
name|TestClusterService
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
name|transport
operator|.
name|CapturingTransport
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
name|ReceiveTimeoutTransportException
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
name|TransportException
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
name|AfterClass
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
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|AtomicBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
operator|.
name|ClusterStateCreationUtils
operator|.
name|stateWithStartedPrimary
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
DECL|class|ShardStateActionTests
specifier|public
class|class
name|ShardStateActionTests
extends|extends
name|ESTestCase
block|{
DECL|field|THREAD_POOL
specifier|private
specifier|static
name|ThreadPool
name|THREAD_POOL
decl_stmt|;
DECL|field|shardStateAction
specifier|private
name|ShardStateAction
name|shardStateAction
decl_stmt|;
DECL|field|transport
specifier|private
name|CapturingTransport
name|transport
decl_stmt|;
DECL|field|transportService
specifier|private
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
name|TestClusterService
name|clusterService
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|startThreadPool
specifier|public
specifier|static
name|void
name|startThreadPool
parameter_list|()
block|{
name|THREAD_POOL
operator|=
operator|new
name|ThreadPool
argument_list|(
literal|"ShardStateActionTest"
argument_list|)
expr_stmt|;
block|}
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
name|transport
operator|=
operator|new
name|CapturingTransport
argument_list|()
expr_stmt|;
name|clusterService
operator|=
operator|new
name|TestClusterService
argument_list|(
name|THREAD_POOL
argument_list|)
expr_stmt|;
name|transportService
operator|=
operator|new
name|TransportService
argument_list|(
name|transport
argument_list|,
name|THREAD_POOL
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|start
argument_list|()
expr_stmt|;
name|shardStateAction
operator|=
operator|new
name|ShardStateAction
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|transportService
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|After
DECL|method|tearDown
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|transportService
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|stopThreadPool
specifier|public
specifier|static
name|void
name|stopThreadPool
parameter_list|()
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|THREAD_POOL
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|THREAD_POOL
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|testNoMaster
specifier|public
name|void
name|testNoMaster
parameter_list|()
block|{
specifier|final
name|String
name|index
init|=
literal|"test"
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|stateWithStartedPrimary
argument_list|(
name|index
argument_list|,
literal|true
argument_list|,
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|DiscoveryNodes
operator|.
name|Builder
name|builder
init|=
name|DiscoveryNodes
operator|.
name|builder
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|masterNodeId
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|nodes
argument_list|(
name|builder
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|indexUUID
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndexUUID
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|noMaster
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|noMaster
operator|.
name|get
argument_list|()
assert|;
name|shardStateAction
operator|.
name|shardFailed
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|getRandomShardRouting
argument_list|(
name|index
argument_list|)
argument_list|,
name|indexUUID
argument_list|,
literal|"test"
argument_list|,
name|getSimulatedFailure
argument_list|()
argument_list|,
operator|new
name|ShardStateAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onShardFailedNoMaster
parameter_list|()
block|{
name|noMaster
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onShardFailedFailure
parameter_list|(
name|DiscoveryNode
name|master
parameter_list|,
name|TransportException
name|e
parameter_list|)
block|{              }
block|}
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|noMaster
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testFailure
specifier|public
name|void
name|testFailure
parameter_list|()
block|{
specifier|final
name|String
name|index
init|=
literal|"test"
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|stateWithStartedPrimary
argument_list|(
name|index
argument_list|,
literal|true
argument_list|,
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|indexUUID
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndexUUID
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|failure
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
assert|assert
operator|!
name|failure
operator|.
name|get
argument_list|()
assert|;
name|shardStateAction
operator|.
name|shardFailed
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|getRandomShardRouting
argument_list|(
name|index
argument_list|)
argument_list|,
name|indexUUID
argument_list|,
literal|"test"
argument_list|,
name|getSimulatedFailure
argument_list|()
argument_list|,
operator|new
name|ShardStateAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onShardFailedNoMaster
parameter_list|()
block|{              }
annotation|@
name|Override
specifier|public
name|void
name|onShardFailedFailure
parameter_list|(
name|DiscoveryNode
name|master
parameter_list|,
name|TransportException
name|e
parameter_list|)
block|{
name|failure
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|CapturingTransport
operator|.
name|CapturedRequest
index|[]
name|capturedRequests
init|=
name|transport
operator|.
name|getCapturedRequestsAndClear
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|capturedRequests
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|failure
operator|.
name|get
argument_list|()
assert|;
name|transport
operator|.
name|handleResponse
argument_list|(
name|capturedRequests
index|[
literal|0
index|]
operator|.
name|requestId
argument_list|,
operator|new
name|TransportException
argument_list|(
literal|"simulated"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|failure
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testTimeout
specifier|public
name|void
name|testTimeout
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|String
name|index
init|=
literal|"test"
decl_stmt|;
name|clusterService
operator|.
name|setState
argument_list|(
name|stateWithStartedPrimary
argument_list|(
name|index
argument_list|,
literal|true
argument_list|,
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|indexUUID
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
operator|.
name|getIndexUUID
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|progress
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|AtomicBoolean
name|timedOut
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
name|TimeValue
name|timeout
init|=
operator|new
name|TimeValue
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|shardStateAction
operator|.
name|shardFailed
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|getRandomShardRouting
argument_list|(
name|index
argument_list|)
argument_list|,
name|indexUUID
argument_list|,
literal|"test"
argument_list|,
name|getSimulatedFailure
argument_list|()
argument_list|,
name|timeout
argument_list|,
operator|new
name|ShardStateAction
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onShardFailedFailure
parameter_list|(
name|DiscoveryNode
name|master
parameter_list|,
name|TransportException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|ReceiveTimeoutTransportException
condition|)
block|{
name|assertFalse
argument_list|(
name|progress
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|timedOut
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|progress
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|timedOut
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|CapturingTransport
operator|.
name|CapturedRequest
index|[]
name|capturedRequests
init|=
name|transport
operator|.
name|getCapturedRequestsAndClear
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|capturedRequests
operator|.
name|length
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getRandomShardRouting
specifier|private
name|ShardRouting
name|getRandomShardRouting
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|routingTable
argument_list|()
operator|.
name|index
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|ShardsIterator
name|shardsIterator
init|=
name|indexRoutingTable
operator|.
name|randomAllActiveShardsIt
argument_list|()
decl_stmt|;
name|ShardRouting
name|shardRouting
init|=
name|shardsIterator
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
assert|assert
name|shardRouting
operator|!=
literal|null
assert|;
return|return
name|shardRouting
return|;
block|}
DECL|method|getSimulatedFailure
specifier|private
name|Throwable
name|getSimulatedFailure
parameter_list|()
block|{
return|return
operator|new
name|CorruptIndexException
argument_list|(
literal|"simulated"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

