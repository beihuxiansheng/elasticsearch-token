begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthStatus
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
name|state
operator|.
name|ClusterStateResponse
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
name|indices
operator|.
name|mapping
operator|.
name|put
operator|.
name|PutMappingResponse
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
name|get
operator|.
name|GetResponse
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
name|Requests
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
name|FileSystemUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|Gateway
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
name|internal
operator|.
name|InternalNode
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
name|integration
operator|.
name|AbstractNodesTests
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
name|AfterMethod
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
name|BeforeMethod
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|AbstractSimpleIndexGatewayTests
specifier|public
specifier|abstract
class|class
name|AbstractSimpleIndexGatewayTests
extends|extends
name|AbstractNodesTests
block|{
DECL|method|closeNodes
annotation|@
name|AfterMethod
specifier|public
name|void
name|closeNodes
parameter_list|()
throws|throws
name|Exception
block|{
name|node
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// since we store (by default) the index snapshot under the gateway, resetting it will reset the index data as well
operator|(
operator|(
name|InternalNode
operator|)
name|node
argument_list|(
literal|"server1"
argument_list|)
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Gateway
operator|.
name|class
argument_list|)
operator|.
name|reset
argument_list|()
expr_stmt|;
name|closeAllNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|buildNode1
annotation|@
name|BeforeMethod
specifier|public
name|void
name|buildNode1
parameter_list|()
throws|throws
name|Exception
block|{
name|buildNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
comment|// since we store (by default) the index snapshot under the gateway, resetting it will reset the index data as well
operator|(
operator|(
name|InternalNode
operator|)
name|node
argument_list|(
literal|"server1"
argument_list|)
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Gateway
operator|.
name|class
argument_list|)
operator|.
name|reset
argument_list|()
expr_stmt|;
name|closeAllNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|testSnapshotOperations
annotation|@
name|Test
specifier|public
name|void
name|testSnapshotOperations
parameter_list|()
throws|throws
name|Exception
block|{
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
comment|// get the environment, so we can clear the work dir when needed
name|Environment
name|environment
init|=
operator|(
operator|(
name|InternalNode
operator|)
name|node
argument_list|(
literal|"server1"
argument_list|)
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|Environment
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Translog tests
name|logger
operator|.
name|info
argument_list|(
literal|"Creating index [{}]"
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|// create a mapping
name|PutMappingResponse
name|putMappingResponse
init|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|preparePutMapping
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setType
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|setSource
argument_list|(
name|mappingSource
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|putMappingResponse
operator|.
name|acknowledged
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that mapping is there
name|ClusterStateResponse
name|clusterState
init|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|state
argument_list|(
name|clusterStateRequest
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// create two and delete the first
name|logger
operator|.
name|info
argument_list|(
literal|"Indexing #1"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|index
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|(
literal|"1"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Indexing #2"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|index
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"2"
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|(
literal|"2"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|// perform snapshot to the index
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Deleting #1"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|delete
argument_list|(
name|deleteRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
comment|// perform snapshot to the index
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot (should be a no op)"
argument_list|)
expr_stmt|;
comment|// do it again, it should be a no op
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Closing the server"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Starting the server, should recover from the gateway (only translog should be populated)"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Running Cluster Health (wait for the shards to startup)"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|clusterHealth
init|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForYellowStatus
argument_list|()
operator|.
name|waitForActiveShards
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Done Cluster Health, status "
operator|+
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|timedOut
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
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify that mapping is there
name|clusterState
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|state
argument_list|(
name|clusterStateRequest
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|clusterState
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|mapping
argument_list|(
literal|"type1"
argument_list|)
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #1, should not exists"
argument_list|)
expr_stmt|;
name|GetResponse
name|getResponse
init|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #2"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"2"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"2"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now flush and add some data (so we have index recovery as well)
name|logger
operator|.
name|info
argument_list|(
literal|"Flushing, so we have actual content in the index files (#2 should be in the index)"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|flush
argument_list|(
name|flushRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Indexing #3, so we have something in the translog as well"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|index
argument_list|(
name|Requests
operator|.
name|indexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"3"
argument_list|)
operator|.
name|source
argument_list|(
name|source
argument_list|(
literal|"3"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot (should be a no op)"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Closing the server"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Starting the server, should recover from the gateway (both index and translog) and reuse work dir"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Running Cluster Health (wait for the shards to startup)"
argument_list|)
expr_stmt|;
name|clusterHealth
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForYellowStatus
argument_list|()
operator|.
name|waitForActiveShards
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Done Cluster Health, status "
operator|+
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|timedOut
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
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #1, should not exists"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #2 (not from the translog, but from the index)"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"2"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"2"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #3 (from the translog)"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"3"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"3"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Closing the server"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Clearing cluster work dir, so there will be a full recovery from the gateway"
argument_list|)
expr_stmt|;
name|FileSystemUtils
operator|.
name|deleteRecursively
argument_list|(
name|environment
operator|.
name|workWithClusterFile
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Starting the server, should recover from the gateway (both index and translog) without reusing work dir"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Running Cluster Health (wait for the shards to startup)"
argument_list|)
expr_stmt|;
name|clusterHealth
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForYellowStatus
argument_list|()
operator|.
name|waitForActiveShards
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Done Cluster Health, status "
operator|+
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|timedOut
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
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #1, should not exists"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #2 (not from the translog, but from the index)"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"2"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"2"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #3 (from the translog)"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"3"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"3"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Flushing, so we have actual content in the index files (#3 should be in the index now as well)"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|flush
argument_list|(
name|flushRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Gateway Snapshot (should be a no op)"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|gatewaySnapshot
argument_list|(
name|gatewaySnapshotRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Closing the server"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Starting the server, should recover from the gateway (just from the index, nothing in the translog)"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Running Cluster Health (wait for the shards to startup)"
argument_list|)
expr_stmt|;
name|clusterHealth
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForYellowStatus
argument_list|()
operator|.
name|waitForActiveShards
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Done Cluster Health, status "
operator|+
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|timedOut
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
name|clusterHealth
operator|.
name|status
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|ClusterHealthStatus
operator|.
name|YELLOW
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #1, should not exists"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #2 (not from the translog, but from the index)"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"2"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"2"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Getting #3 (not from the translog, but from the index)"
argument_list|)
expr_stmt|;
name|getResponse
operator|=
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|get
argument_list|(
name|getRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|type
argument_list|(
literal|"type1"
argument_list|)
operator|.
name|id
argument_list|(
literal|"3"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getResponse
operator|.
name|sourceAsString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
argument_list|(
literal|"3"
argument_list|,
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Deleting the index"
argument_list|)
expr_stmt|;
name|client
argument_list|(
literal|"server1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|delete
argument_list|(
name|deleteIndexRequest
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
block|}
DECL|method|mappingSource
specifier|private
name|String
name|mappingSource
parameter_list|()
block|{
return|return
literal|"{ type1 : { properties : { name : { type : \"string\" } } } }"
return|;
block|}
DECL|method|source
specifier|private
name|String
name|source
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|nameValue
parameter_list|)
block|{
return|return
literal|"{ type1 : { \"id\" : \""
operator|+
name|id
operator|+
literal|"\", \"name\" : \""
operator|+
name|nameValue
operator|+
literal|"\" } }"
return|;
block|}
block|}
end_class

end_unit

