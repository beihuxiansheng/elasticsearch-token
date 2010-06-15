begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|strategy
operator|.
name|DefaultShardsRoutingStrategy
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
name|util
operator|.
name|io
operator|.
name|stream
operator|.
name|BytesStreamInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|cluster
operator|.
name|ClusterState
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexMetaData
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|RoutingBuilders
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|ClusterSerializationTests
specifier|public
class|class
name|ClusterSerializationTests
block|{
DECL|method|testClusterStateSerialization
annotation|@
name|Test
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
name|newMetaDataBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test"
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
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|routingTable
argument_list|()
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|(
literal|"test"
argument_list|)
operator|.
name|initializeEmpty
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
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
name|newNodesBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node3"
argument_list|)
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"node1"
argument_list|)
operator|.
name|masterNodeId
argument_list|(
literal|"node2"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|newClusterStateBuilder
argument_list|()
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
name|DefaultShardsRoutingStrategy
name|strategy
init|=
operator|new
name|DefaultShardsRoutingStrategy
argument_list|()
decl_stmt|;
name|clusterState
operator|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
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
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterState
name|serializedClusterState
init|=
name|ClusterState
operator|.
name|Builder
operator|.
name|fromBytes
argument_list|(
name|ClusterState
operator|.
name|Builder
operator|.
name|toBytes
argument_list|(
name|clusterState
argument_list|)
argument_list|,
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|,
name|newNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|serializedClusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRoutingTableSerialization
annotation|@
name|Test
specifier|public
name|void
name|testRoutingTableSerialization
parameter_list|()
throws|throws
name|Exception
block|{
name|MetaData
name|metaData
init|=
name|newMetaDataBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newIndexMetaDataBuilder
argument_list|(
literal|"test"
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
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|routingTable
argument_list|()
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|(
literal|"test"
argument_list|)
operator|.
name|initializeEmpty
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
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
name|newNodesBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node1"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node2"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
name|newNode
argument_list|(
literal|"node3"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|newClusterStateBuilder
argument_list|()
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
name|DefaultShardsRoutingStrategy
name|strategy
init|=
operator|new
name|DefaultShardsRoutingStrategy
argument_list|()
decl_stmt|;
name|RoutingTable
name|source
init|=
name|strategy
operator|.
name|reroute
argument_list|(
name|clusterState
argument_list|)
decl_stmt|;
name|BytesStreamOutput
name|outStream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
operator|.
name|writeTo
argument_list|(
name|source
argument_list|,
name|outStream
argument_list|)
expr_stmt|;
name|BytesStreamInput
name|inStream
init|=
operator|new
name|BytesStreamInput
argument_list|(
name|outStream
operator|.
name|copiedByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|RoutingTable
name|target
init|=
name|RoutingTable
operator|.
name|Builder
operator|.
name|readFrom
argument_list|(
name|inStream
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|target
operator|.
name|prettyPrint
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|source
operator|.
name|prettyPrint
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|newNode
specifier|private
name|DiscoveryNode
name|newNode
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
return|return
operator|new
name|DiscoveryNode
argument_list|(
name|nodeId
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
block|}
end_class

end_unit

