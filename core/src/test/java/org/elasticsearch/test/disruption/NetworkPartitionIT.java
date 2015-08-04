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
name|TransportModule
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
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|NetworkPartitionIT
specifier|public
class|class
name|NetworkPartitionIT
extends|extends
name|ESIntegTestCase
block|{
annotation|@
name|Override
DECL|method|nodeSettings
specifier|protected
name|Settings
name|nodeSettings
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|TransportModule
operator|.
name|TRANSPORT_SERVICE_TYPE_KEY
argument_list|,
name|MockTransportService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
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
name|NetworkPartition
name|networkPartition
init|=
operator|new
name|NetworkUnresponsivePartition
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
argument_list|,
name|getRandom
argument_list|()
argument_list|)
decl_stmt|;
name|internalCluster
argument_list|()
operator|.
name|setDisruptionScheme
argument_list|(
name|networkPartition
argument_list|)
expr_stmt|;
name|networkPartition
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
block|}
end_class

end_unit

