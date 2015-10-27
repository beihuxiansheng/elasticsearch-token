begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.serialization
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|serialization
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
name|metadata
operator|.
name|IndexMetaData
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
name|metadata
operator|.
name|IndexTemplateMetaData
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
name|metadata
operator|.
name|MetaData
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
name|RoutingTable
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
name|allocation
operator|.
name|AllocationService
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
name|DummyTransportAddress
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
name|ESAllocationTestCase
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
name|containsString
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ClusterStateToStringTests
specifier|public
class|class
name|ClusterStateToStringTests
extends|extends
name|ESAllocationTestCase
block|{
DECL|method|testClusterStateSerialization
specifier|public
name|void
name|testClusterStateSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|MetaData
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test_idx"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|10
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|IndexTemplateMetaData
operator|.
name|builder
argument_list|(
literal|"test_template"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|addAsNew
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
literal|"test_idx"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
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
operator|new
name|DiscoveryNode
argument_list|(
literal|"node_foo"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"node_foo"
argument_list|)
operator|.
name|masterNodeId
argument_list|(
literal|"node_foo"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|nodes
argument_list|(
name|nodes
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|AllocationService
name|strategy
init|=
name|createAllocationService
argument_list|()
decl_stmt|;
name|clusterState
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|(
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingTable
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|String
name|clusterStateString
init|=
name|clusterState
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|clusterStateString
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateString
argument_list|,
name|containsString
argument_list|(
literal|"test_idx"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateString
argument_list|,
name|containsString
argument_list|(
literal|"test_template"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterStateString
argument_list|,
name|containsString
argument_list|(
literal|"node_foo"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

