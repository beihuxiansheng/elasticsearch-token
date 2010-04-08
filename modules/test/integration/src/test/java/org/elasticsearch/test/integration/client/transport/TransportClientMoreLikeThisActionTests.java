begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.client.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|client
operator|.
name|transport
package|;
end_package

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
name|client
operator|.
name|transport
operator|.
name|TransportClient
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
name|document
operator|.
name|MoreLikeThisActionTests
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
name|util
operator|.
name|transport
operator|.
name|TransportAddress
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportClientMoreLikeThisActionTests
specifier|public
class|class
name|TransportClientMoreLikeThisActionTests
extends|extends
name|MoreLikeThisActionTests
block|{
DECL|method|getClient1
annotation|@
name|Override
specifier|protected
name|Client
name|getClient1
parameter_list|()
block|{
name|TransportAddress
name|server1Address
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
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|TransportClient
name|client
init|=
operator|new
name|TransportClient
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|addTransportAddress
argument_list|(
name|server1Address
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
DECL|method|getClient2
annotation|@
name|Override
specifier|protected
name|Client
name|getClient2
parameter_list|()
block|{
name|TransportAddress
name|server1Address
init|=
operator|(
operator|(
name|InternalNode
operator|)
name|node
argument_list|(
literal|"server2"
argument_list|)
operator|)
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|TransportService
operator|.
name|class
argument_list|)
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
name|TransportClient
name|client
init|=
operator|new
name|TransportClient
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.enabled"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
name|client
operator|.
name|addTransportAddress
argument_list|(
name|server1Address
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
block|}
end_class

end_unit

