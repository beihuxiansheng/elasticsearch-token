begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.azure.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|azure
operator|.
name|test
package|;
end_package

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
DECL|class|AzureSimpleTest
specifier|public
class|class
name|AzureSimpleTest
extends|extends
name|AzureAbstractTest
block|{
DECL|method|AzureSimpleTest
specifier|public
name|AzureSimpleTest
parameter_list|()
block|{
name|super
argument_list|(
name|AzureComputeServiceSimpleMock
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|one_node_should_run
specifier|public
name|void
name|one_node_should_run
parameter_list|()
block|{
comment|// Then we start our node for tests
name|nodeBuilder
argument_list|()
expr_stmt|;
comment|// We expect having 2 nodes as part of the cluster, let's test that
name|checkNumberOfNodes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

