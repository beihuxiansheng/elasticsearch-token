begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
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
name|indices
operator|.
name|create
operator|.
name|CreateIndexClusterStateUpdateRequest
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
name|IndexTemplateFilter
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
name|routing
operator|.
name|ShardRouting
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
name|RoutingAllocation
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
name|decider
operator|.
name|AllocationDecider
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
name|decider
operator|.
name|AllocationDeciders
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
name|decider
operator|.
name|EnableAllocationDecider
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
name|service
operator|.
name|ClusterService
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
name|inject
operator|.
name|ModuleTestCase
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
name|ClusterSettings
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
name|IndexScopedSettings
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
name|Setting
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
name|Setting
operator|.
name|Property
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
name|common
operator|.
name|settings
operator|.
name|SettingsModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|ClusterPlugin
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_class
DECL|class|ClusterModuleTests
specifier|public
class|class
name|ClusterModuleTests
extends|extends
name|ModuleTestCase
block|{
DECL|field|clusterService
specifier|private
name|ClusterService
name|clusterService
init|=
operator|new
name|ClusterService
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
operator|new
name|ClusterSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
DECL|class|FakeAllocationDecider
specifier|static
class|class
name|FakeAllocationDecider
extends|extends
name|AllocationDecider
block|{
DECL|method|FakeAllocationDecider
specifier|protected
name|FakeAllocationDecider
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FakeShardsAllocator
specifier|static
class|class
name|FakeShardsAllocator
implements|implements
name|ShardsAllocator
block|{
annotation|@
name|Override
DECL|method|allocate
specifier|public
name|void
name|allocate
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|)
block|{
comment|// noop
block|}
annotation|@
name|Override
DECL|method|weighShard
specifier|public
name|Map
argument_list|<
name|DiscoveryNode
argument_list|,
name|Float
argument_list|>
name|weighShard
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|,
name|ShardRouting
name|shard
parameter_list|)
block|{
return|return
operator|new
name|HashMap
argument_list|<>
argument_list|()
return|;
block|}
block|}
DECL|class|FakeIndexTemplateFilter
specifier|static
class|class
name|FakeIndexTemplateFilter
implements|implements
name|IndexTemplateFilter
block|{
annotation|@
name|Override
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|CreateIndexClusterStateUpdateRequest
name|request
parameter_list|,
name|IndexTemplateMetaData
name|template
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|method|testRegisterClusterDynamicSettingDuplicate
specifier|public
name|void
name|testRegisterClusterDynamicSettingDuplicate
parameter_list|()
block|{
try|try
block|{
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE_SETTING
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Cannot register setting ["
operator|+
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE_SETTING
operator|.
name|getKey
argument_list|()
operator|+
literal|"] twice"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterClusterDynamicSetting
specifier|public
name|void
name|testRegisterClusterDynamicSetting
parameter_list|()
block|{
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"foo.bar"
argument_list|,
literal|false
argument_list|,
name|Property
operator|.
name|Dynamic
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|ClusterSettings
operator|.
name|class
argument_list|,
name|service
lambda|->
name|service
operator|.
name|hasDynamicSetting
argument_list|(
literal|"foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterIndexDynamicSettingDuplicate
specifier|public
name|void
name|testRegisterIndexDynamicSettingDuplicate
parameter_list|()
block|{
try|try
block|{
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_ALLOCATION_ENABLE_SETTING
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Cannot register setting ["
operator|+
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_ALLOCATION_ENABLE_SETTING
operator|.
name|getKey
argument_list|()
operator|+
literal|"] twice"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRegisterIndexDynamicSetting
specifier|public
name|void
name|testRegisterIndexDynamicSetting
parameter_list|()
block|{
name|SettingsModule
name|module
init|=
operator|new
name|SettingsModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|Setting
operator|.
name|boolSetting
argument_list|(
literal|"index.foo.bar"
argument_list|,
literal|false
argument_list|,
name|Property
operator|.
name|Dynamic
argument_list|,
name|Property
operator|.
name|IndexScope
argument_list|)
argument_list|)
decl_stmt|;
name|assertInstanceBinding
argument_list|(
name|module
argument_list|,
name|IndexScopedSettings
operator|.
name|class
argument_list|,
name|service
lambda|->
name|service
operator|.
name|hasDynamicSetting
argument_list|(
literal|"index.foo.bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testRegisterAllocationDeciderDuplicate
specifier|public
name|void
name|testRegisterAllocationDeciderDuplicate
parameter_list|()
block|{
name|IllegalArgumentException
name|e
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
operator|new
name|ClusterModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ClusterPlugin
argument_list|()
block|{
block|@Override                     public Collection<AllocationDecider> createAllocationDeciders(Settings settings
argument_list|,
name|ClusterSettings
name|clusterSettings
argument_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|EnableAllocationDecider
argument_list|(
name|settings
argument_list|,
name|clusterSettings
argument_list|)
argument_list|)
return|;
block|}
block|}
block|)
end_class

begin_empty_stmt
unit|))
empty_stmt|;
end_empty_stmt

begin_expr_stmt
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Cannot specify allocation decider ["
operator|+
name|EnableAllocationDecider
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"] twice"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}      public
DECL|method|testRegisterAllocationDecider
name|void
name|testRegisterAllocationDecider
parameter_list|()
block|{
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|ClusterPlugin
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|AllocationDecider
argument_list|>
name|createAllocationDeciders
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterSettings
name|clusterSettings
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
operator|new
name|FakeAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|module
operator|.
name|allocationDeciders
operator|.
name|stream
argument_list|()
operator|.
name|anyMatch
argument_list|(
name|d
lambda|->
name|d
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|FakeAllocationDecider
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testRegisterShardsAllocator
specifier|public
name|void
name|testRegisterShardsAllocator
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ClusterModule
operator|.
name|SHARDS_ALLOCATOR_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"custom"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerShardsAllocator
argument_list|(
literal|"custom"
argument_list|,
name|FakeShardsAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|ShardsAllocator
operator|.
name|class
argument_list|,
name|FakeShardsAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testRegisterShardsAllocatorAlreadyRegistered
specifier|public
name|void
name|testRegisterShardsAllocatorAlreadyRegistered
parameter_list|()
block|{
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerShardsAllocator
argument_list|(
name|ClusterModule
operator|.
name|BALANCED_ALLOCATOR
argument_list|,
name|FakeShardsAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [shards_allocator] more than once for [balanced]"
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|testUnknownShardsAllocator
specifier|public
name|void
name|testUnknownShardsAllocator
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ClusterModule
operator|.
name|SHARDS_ALLOCATOR_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"dne"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertBindingFailure
argument_list|(
name|module
argument_list|,
literal|"Unknown [shards_allocator]"
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testEvenShardsAllocatorBackcompat
specifier|public
name|void
name|testEvenShardsAllocatorBackcompat
parameter_list|()
block|{
name|Settings
name|settings
init|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ClusterModule
operator|.
name|SHARDS_ALLOCATOR_TYPE_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
name|ClusterModule
operator|.
name|EVEN_SHARD_COUNT_ALLOCATOR
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|assertBinding
argument_list|(
name|module
argument_list|,
name|ShardsAllocator
operator|.
name|class
argument_list|,
name|BalancedShardsAllocator
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
end_function

begin_function
DECL|method|testRegisterIndexTemplateFilterDuplicate
specifier|public
name|void
name|testRegisterIndexTemplateFilterDuplicate
parameter_list|()
block|{
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|module
operator|.
name|registerIndexTemplateFilter
argument_list|(
name|FakeIndexTemplateFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|module
operator|.
name|registerIndexTemplateFilter
argument_list|(
name|FakeIndexTemplateFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|"Can't register the same [index_template_filter] more than once for ["
operator|+
name|FakeIndexTemplateFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
end_function

begin_function
DECL|method|testRegisterIndexTemplateFilter
specifier|public
name|void
name|testRegisterIndexTemplateFilter
parameter_list|()
block|{
name|ClusterModule
name|module
init|=
operator|new
name|ClusterModule
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|clusterService
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|module
operator|.
name|registerIndexTemplateFilter
argument_list|(
name|FakeIndexTemplateFilter
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertSetMultiBinding
argument_list|(
name|module
argument_list|,
name|IndexTemplateFilter
operator|.
name|class
argument_list|,
name|FakeIndexTemplateFilter
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

