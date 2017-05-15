begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.disruption
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|disruption
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|Plugin
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
name|ESIntegTestCase
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
name|InternalTestCluster
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
name|disruption
operator|.
name|NetworkDisruption
operator|.
name|TwoPartitions
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
name|MockTransportService
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|greaterThanOrEqualTo
import|;
end_import

begin_class
DECL|class|NetworkDisruptionIT
specifier|public
class|class
name|NetworkDisruptionIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodePlugins
specifier|protected
name|Collection
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|Plugin
argument_list|>
argument_list|>
name|nodePlugins
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|MockTransportService
operator|.
name|TestPlugin
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|testNetworkPartitionWithNodeShutdown
specifier|public
name|void
name|testNetworkPartitionWithNodeShutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|internalCluster
argument_list|()
operator|.
name|ensureAtLeastNumDataNodes
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|String
index|[]
name|nodeNames
init|=
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
decl_stmt|;
name|NetworkDisruption
name|networkDisruption
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|TwoPartitions
argument_list|(
name|nodeNames
index|[
literal|0
index|]
argument_list|,
name|nodeNames
index|[
literal|1
index|]
argument_list|)
argument_list|,
operator|new
name|NetworkDisruption
operator|.
name|NetworkUnresponsive
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|networkDisruption
argument_list|)
expr_stmt|;
name|networkDisruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|(
name|InternalTestCluster
operator|.
name|nameFilter
argument_list|(
name|nodeNames
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|clearDisruptionScheme
argument_list|()
expr_stmt|;
block|}
DECL|method|testNetworkPartitionRemovalRestoresConnections
specifier|public
name|void
name|testNetworkPartitionRemovalRestoresConnections
parameter_list|()
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|remove
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodes
operator|.
name|size
argument_list|()
operator|<=
literal|2
condition|)
block|{
name|internalCluster
argument_list|()
operator|.
name|ensureAtLeastNumDataNodes
argument_list|(
literal|3
operator|-
name|nodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getNodeNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|remove
argument_list|(
name|internalCluster
argument_list|()
operator|.
name|getMasterName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|side1
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|randomSubsetOf
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|nodes
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|nodes
argument_list|)
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|side2
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
name|side2
operator|.
name|removeAll
argument_list|(
name|side1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|side2
operator|.
name|size
argument_list|()
argument_list|,
name|greaterThanOrEqualTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|NetworkDisruption
name|networkDisruption
init|=
operator|new
name|NetworkDisruption
argument_list|(
operator|new
name|TwoPartitions
argument_list|(
name|side1
argument_list|,
name|side2
argument_list|)
argument_list|,
operator|new
name|NetworkDisruption
operator|.
name|NetworkDisconnect
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|networkDisruption
argument_list|)
expr_stmt|;
name|networkDisruption
operator|.
name|startDisrupting
argument_list|()
expr_stmt|;
comment|// sends some requests
name|client
argument_list|(
name|randomFrom
argument_list|(
name|side1
argument_list|)
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|(
name|randomFrom
argument_list|(
name|side2
argument_list|)
argument_list|)
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
name|internalCluster
argument_list|()
operator|.
name|clearDisruptionScheme
argument_list|()
expr_stmt|;
comment|// check all connections are restore
for|for
control|(
name|String
name|nodeA
range|:
name|side1
control|)
block|{
for|for
control|(
name|String
name|nodeB
range|:
name|side2
control|)
block|{
name|TransportService
name|serviceA
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|nodeA
argument_list|)
decl_stmt|;
name|TransportService
name|serviceB
init|=
name|internalCluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|,
name|nodeB
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nodeA
operator|+
literal|" is not connected to "
operator|+
name|nodeB
argument_list|,
name|serviceA
operator|.
name|nodeConnected
argument_list|(
name|serviceB
operator|.
name|getLocalNode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodeB
operator|+
literal|" is not connected to "
operator|+
name|nodeA
argument_list|,
name|serviceB
operator|.
name|nodeConnected
argument_list|(
name|serviceA
operator|.
name|getLocalNode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

