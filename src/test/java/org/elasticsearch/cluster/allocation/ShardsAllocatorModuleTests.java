begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.cluster.allocation
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|allocation
package|;
end_package

begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

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
name|allocator
operator|.
name|BalancedShardsAllocator
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
name|allocator
operator|.
name|EvenShardsCountAllocator
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
name|allocator
operator|.
name|ShardsAllocator
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
name|allocator
operator|.
name|ShardsAllocatorModule
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
name|AbstractIntegrationTest
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
name|AbstractIntegrationTest
operator|.
name|ClusterScope
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
name|AbstractIntegrationTest
operator|.
name|Scope
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
name|settingsBuilder
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
name|instanceOf
import|;
end_import

begin_class
annotation|@
name|ClusterScope
argument_list|(
name|scope
operator|=
name|Scope
operator|.
name|TEST
argument_list|,
name|numNodes
operator|=
literal|0
argument_list|)
DECL|class|ShardsAllocatorModuleTests
specifier|public
class|class
name|ShardsAllocatorModuleTests
extends|extends
name|AbstractIntegrationTest
block|{
DECL|method|testLoadDefaultShardsAllocator
specifier|public
name|void
name|testLoadDefaultShardsAllocator
parameter_list|()
block|{
name|assertAllocatorInstance
argument_list|(
name|ImmutableSettings
operator|.
name|Builder
operator|.
name|EMPTY_SETTINGS
argument_list|,
name|BalancedShardsAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testLoadByShortKeyShardsAllocator
specifier|public
name|void
name|testLoadByShortKeyShardsAllocator
parameter_list|()
block|{
name|Settings
name|build
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|ShardsAllocatorModule
operator|.
name|TYPE_KEY
argument_list|,
name|ShardsAllocatorModule
operator|.
name|EVEN_SHARD_COUNT_ALLOCATOR_KEY
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAllocatorInstance
argument_list|(
name|build
argument_list|,
name|EvenShardsCountAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
name|build
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|ShardsAllocatorModule
operator|.
name|TYPE_KEY
argument_list|,
name|ShardsAllocatorModule
operator|.
name|BALANCED_ALLOCATOR_KEY
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertAllocatorInstance
argument_list|(
name|build
argument_list|,
name|BalancedShardsAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|testLoadByClassNameShardsAllocator
specifier|public
name|void
name|testLoadByClassNameShardsAllocator
parameter_list|()
block|{
name|Settings
name|build
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|ShardsAllocatorModule
operator|.
name|TYPE_KEY
argument_list|,
literal|"EvenShardsCount"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertAllocatorInstance
argument_list|(
name|build
argument_list|,
name|EvenShardsCountAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
name|build
operator|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|ShardsAllocatorModule
operator|.
name|TYPE_KEY
argument_list|,
literal|"org.elasticsearch.cluster.routing.allocation.allocator.EvenShardsCountAllocator"
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|assertAllocatorInstance
argument_list|(
name|build
argument_list|,
name|EvenShardsCountAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAllocatorInstance
specifier|private
name|void
name|assertAllocatorInstance
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|ShardsAllocator
argument_list|>
name|clazz
parameter_list|)
block|{
while|while
condition|(
name|cluster
argument_list|()
operator|.
name|numNodes
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|cluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|()
expr_stmt|;
block|}
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|ShardsAllocator
name|instance
init|=
name|cluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|ShardsAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|instance
argument_list|,
name|instanceOf
argument_list|(
name|clazz
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

