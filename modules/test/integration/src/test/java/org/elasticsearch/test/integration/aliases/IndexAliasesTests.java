begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.aliases
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|aliases
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
name|index
operator|.
name|IndexResponse
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
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndexMissingException
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
annotation|@
name|Test
DECL|class|IndexAliasesTests
specifier|public
class|class
name|IndexAliasesTests
extends|extends
name|AbstractNodesTests
block|{
DECL|field|client1
specifier|protected
name|Client
name|client1
decl_stmt|;
DECL|field|client2
specifier|protected
name|Client
name|client2
decl_stmt|;
DECL|method|startNodes
annotation|@
name|BeforeMethod
specifier|public
name|void
name|startNodes
parameter_list|()
block|{
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server2"
argument_list|)
expr_stmt|;
name|client1
operator|=
name|getClient1
argument_list|()
expr_stmt|;
name|client2
operator|=
name|getClient2
argument_list|()
expr_stmt|;
block|}
DECL|method|closeNodes
annotation|@
name|AfterMethod
specifier|public
name|void
name|closeNodes
parameter_list|()
block|{
name|client1
operator|.
name|close
argument_list|()
expr_stmt|;
name|client2
operator|.
name|close
argument_list|()
expr_stmt|;
name|closeAllNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|getClient1
specifier|protected
name|Client
name|getClient1
parameter_list|()
block|{
return|return
name|client
argument_list|(
literal|"server1"
argument_list|)
return|;
block|}
DECL|method|getClient2
specifier|protected
name|Client
name|getClient2
parameter_list|()
block|{
return|return
name|client
argument_list|(
literal|"server2"
argument_list|)
return|;
block|}
DECL|method|testAliases
annotation|@
name|Test
specifier|public
name|void
name|testAliases
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Creating index [test]"
argument_list|)
expr_stmt|;
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|create
argument_list|(
name|createIndexRequest
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
literal|"Running Cluster Health"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|clusterHealth
init|=
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|health
argument_list|(
name|clusterHealth
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
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
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Indexing against [alias1], should fail"
argument_list|)
expr_stmt|;
name|client1
operator|.
name|index
argument_list|(
name|indexRequest
argument_list|(
literal|"alias1"
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
assert|assert
literal|false
operator|:
literal|"index [alias1] should not exists"
assert|;
block|}
catch|catch
parameter_list|(
name|IndexMissingException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"alias1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Aliasing index [test] with [alias1]"
argument_list|)
expr_stmt|;
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|aliases
argument_list|(
name|indexAliasesRequest
argument_list|()
operator|.
name|addAlias
argument_list|(
literal|"test"
argument_list|,
literal|"alias1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Indexing against [alias1], should work now"
argument_list|)
expr_stmt|;
name|IndexResponse
name|indexResponse
init|=
name|client1
operator|.
name|index
argument_list|(
name|indexRequest
argument_list|(
literal|"alias1"
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
decl_stmt|;
name|assertThat
argument_list|(
name|indexResponse
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Creating index [test]"
argument_list|)
expr_stmt|;
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|create
argument_list|(
name|createIndexRequest
argument_list|(
literal|"test_x"
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
literal|"Running Cluster Health"
argument_list|)
expr_stmt|;
name|clusterHealth
operator|=
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|health
argument_list|(
name|clusterHealth
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
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
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Remove [alias1], Aliasing index [test_x] with [alias1]"
argument_list|)
expr_stmt|;
name|client1
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|aliases
argument_list|(
name|indexAliasesRequest
argument_list|()
operator|.
name|removeAlias
argument_list|(
literal|"test"
argument_list|,
literal|"alias1"
argument_list|)
operator|.
name|addAlias
argument_list|(
literal|"test_x"
argument_list|,
literal|"alias1"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Indexing against [alias1], should work against [test_x]"
argument_list|)
expr_stmt|;
name|indexResponse
operator|=
name|client1
operator|.
name|index
argument_list|(
name|indexRequest
argument_list|(
literal|"alias1"
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
name|assertThat
argument_list|(
name|indexResponse
operator|.
name|index
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"test_x"
argument_list|)
argument_list|)
expr_stmt|;
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

