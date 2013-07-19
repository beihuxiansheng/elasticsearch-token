begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.unit.transport.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|unit
operator|.
name|transport
operator|.
name|local
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
name|test
operator|.
name|unit
operator|.
name|transport
operator|.
name|AbstractSimpleTransportTests
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
name|elasticsearch
operator|.
name|transport
operator|.
name|local
operator|.
name|LocalTransport
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

begin_class
DECL|class|SimpleLocalTransportTests
specifier|public
class|class
name|SimpleLocalTransportTests
extends|extends
name|AbstractSimpleTransportTests
block|{
annotation|@
name|Override
DECL|method|build
specifier|protected
name|void
name|build
parameter_list|()
block|{
name|serviceA
operator|=
operator|new
name|TransportService
argument_list|(
operator|new
name|LocalTransport
argument_list|(
name|threadPool
argument_list|)
argument_list|,
name|threadPool
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|serviceANode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"A"
argument_list|,
name|serviceA
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|)
expr_stmt|;
name|serviceB
operator|=
operator|new
name|TransportService
argument_list|(
operator|new
name|LocalTransport
argument_list|(
name|threadPool
argument_list|)
argument_list|,
name|threadPool
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
name|serviceBNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
literal|"B"
argument_list|,
name|serviceB
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

