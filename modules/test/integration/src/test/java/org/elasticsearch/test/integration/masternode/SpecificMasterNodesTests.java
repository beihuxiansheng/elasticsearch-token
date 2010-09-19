begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.masternode
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|masternode
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|MasterNotDiscoveredException
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
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
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
DECL|class|SpecificMasterNodesTests
specifier|public
class|class
name|SpecificMasterNodesTests
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
block|{
name|closeAllNodes
argument_list|()
expr_stmt|;
block|}
DECL|method|simpleOnlyMasterNodesElection
annotation|@
name|Test
specifier|public
name|void
name|simpleOnlyMasterNodesElection
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"--> start data node / non master node"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"data1"
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.master"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"discovery.initial_state_timeout"
argument_list|,
literal|"1s"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertThat
argument_list|(
name|client
argument_list|(
literal|"data1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|setMasterNodeTimeout
argument_list|(
literal|"100ms"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
literal|false
operator|:
literal|"should not be able to find master"
assert|;
block|}
catch|catch
parameter_list|(
name|MasterNotDiscoveredException
name|e
parameter_list|)
block|{
comment|// all is well, no master elected
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> start master node"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"master1"
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.master"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|(
literal|"data1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"master1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|(
literal|"master1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"master1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stop master node"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"master1"
argument_list|)
expr_stmt|;
try|try
block|{
name|assertThat
argument_list|(
name|client
argument_list|(
literal|"data1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|setMasterNodeTimeout
argument_list|(
literal|"100ms"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNodeId
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
literal|false
operator|:
literal|"should not be able to find master"
assert|;
block|}
catch|catch
parameter_list|(
name|MasterNotDiscoveredException
name|e
parameter_list|)
block|{
comment|// all is well, no master elected
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"--> start master node"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"master1"
argument_list|,
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"node.data"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.master"
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|(
literal|"data1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"master1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|client
argument_list|(
literal|"master1"
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"master1"
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stop all nodes"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"data1"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"master1"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

