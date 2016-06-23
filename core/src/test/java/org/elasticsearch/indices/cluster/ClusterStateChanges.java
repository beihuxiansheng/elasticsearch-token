begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
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
name|ActionResponse
import|;
end_import

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
name|cluster
operator|.
name|reroute
operator|.
name|ClusterRerouteRequest
import|;
end_import

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
name|cluster
operator|.
name|reroute
operator|.
name|TransportClusterRerouteAction
import|;
end_import

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
name|close
operator|.
name|CloseIndexRequest
import|;
end_import

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
name|close
operator|.
name|TransportCloseIndexAction
import|;
end_import

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
name|CreateIndexRequest
import|;
end_import

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
name|TransportCreateIndexAction
import|;
end_import

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
name|delete
operator|.
name|DeleteIndexRequest
import|;
end_import

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
name|delete
operator|.
name|TransportDeleteIndexAction
import|;
end_import

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
name|open
operator|.
name|OpenIndexRequest
import|;
end_import

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
name|open
operator|.
name|TransportOpenIndexAction
import|;
end_import

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
name|settings
operator|.
name|put
operator|.
name|TransportUpdateSettingsAction
import|;
end_import

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
name|settings
operator|.
name|put
operator|.
name|UpdateSettingsRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|ActionFilters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|DestructiveOperations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|PlainActionFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|MasterNodeRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeActionUtils
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
name|ClusterStateUpdateTask
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
name|EmptyClusterInfoService
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
name|AliasValidator
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
name|IndexNameExpressionResolver
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
name|MetaDataCreateIndexService
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
name|MetaDataDeleteIndexService
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
name|MetaDataIndexStateService
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
name|MetaDataIndexUpgradeService
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
name|MetaDataUpdateSettingsService
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
name|AllocationService
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
name|FailedRerouteAllocation
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
name|RandomAllocationDeciderTests
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
name|ReplicaAfterPrimaryActiveAllocationDecider
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
name|SameShardAllocationDecider
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
name|SuppressForbidden
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
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|NodeServicesProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|MapperService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|IndexEventListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|gateway
operator|.
name|NoopGatewayAllocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
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
name|Transport
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
name|Collections
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|getRandom
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
operator|.
name|PATH_HOME_SETTING
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
name|notNullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyBoolean
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anyString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doAnswer
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
DECL|class|ClusterStateChanges
class|class
name|ClusterStateChanges
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|allocationService
specifier|private
specifier|final
name|AllocationService
name|allocationService
decl_stmt|;
comment|// transport actions
DECL|field|transportCloseIndexAction
specifier|private
specifier|final
name|TransportCloseIndexAction
name|transportCloseIndexAction
decl_stmt|;
DECL|field|transportOpenIndexAction
specifier|private
specifier|final
name|TransportOpenIndexAction
name|transportOpenIndexAction
decl_stmt|;
DECL|field|transportDeleteIndexAction
specifier|private
specifier|final
name|TransportDeleteIndexAction
name|transportDeleteIndexAction
decl_stmt|;
DECL|field|transportUpdateSettingsAction
specifier|private
specifier|final
name|TransportUpdateSettingsAction
name|transportUpdateSettingsAction
decl_stmt|;
DECL|field|transportClusterRerouteAction
specifier|private
specifier|final
name|TransportClusterRerouteAction
name|transportClusterRerouteAction
decl_stmt|;
DECL|field|transportCreateIndexAction
specifier|private
specifier|final
name|TransportCreateIndexAction
name|transportCreateIndexAction
decl_stmt|;
DECL|method|ClusterStateChanges
specifier|public
name|ClusterStateChanges
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
name|PATH_HOME_SETTING
operator|.
name|getKey
argument_list|()
argument_list|,
literal|"dummy"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|allocationService
operator|=
operator|new
name|AllocationService
argument_list|(
name|settings
argument_list|,
operator|new
name|AllocationDeciders
argument_list|(
name|settings
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|SameShardAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|ReplicaAfterPrimaryActiveAllocationDecider
argument_list|(
name|settings
argument_list|)
argument_list|,
operator|new
name|RandomAllocationDeciderTests
operator|.
name|RandomAllocationDecider
argument_list|(
name|getRandom
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|NoopGatewayAllocator
operator|.
name|INSTANCE
argument_list|,
operator|new
name|BalancedShardsAllocator
argument_list|(
name|settings
argument_list|)
argument_list|,
name|EmptyClusterInfoService
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|ClusterSettings
name|clusterSettings
init|=
operator|new
name|ClusterSettings
argument_list|(
name|settings
argument_list|,
name|ClusterSettings
operator|.
name|BUILT_IN_CLUSTER_SETTINGS
argument_list|)
decl_stmt|;
name|ActionFilters
name|actionFilters
init|=
operator|new
name|ActionFilters
argument_list|(
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|)
decl_stmt|;
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
init|=
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|DestructiveOperations
name|destructiveOperations
init|=
operator|new
name|DestructiveOperations
argument_list|(
name|settings
argument_list|,
name|clusterSettings
argument_list|)
decl_stmt|;
name|Environment
name|environment
init|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
decl_stmt|;
name|ThreadPool
name|threadPool
init|=
literal|null
decl_stmt|;
comment|// it's not used
name|Transport
name|transport
init|=
literal|null
decl_stmt|;
comment|// it's not used
comment|// mocks
name|clusterService
operator|=
name|mock
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
expr_stmt|;
name|IndicesService
name|indicesService
init|=
name|mock
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// MetaDataCreateIndexService creates indices using its IndicesService instance to check mappings -> fake it here
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|IndexEventListener
argument_list|>
name|listeners
init|=
name|anyList
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Consumer
argument_list|<
name|ShardId
argument_list|>
name|globalCheckpointSyncer
init|=
name|any
argument_list|(
name|Consumer
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|indicesService
operator|.
name|createIndex
argument_list|(
name|any
argument_list|(
name|NodeServicesProvider
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|IndexMetaData
operator|.
name|class
argument_list|)
argument_list|,
name|listeners
argument_list|,
name|globalCheckpointSyncer
argument_list|)
argument_list|)
operator|.
name|then
argument_list|(
name|invocationOnMock
lambda|->
block|{
name|IndexService
name|indexService
init|=
name|mock
argument_list|(
name|IndexService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
operator|(
name|IndexMetaData
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|1
index|]
decl_stmt|;
name|when
argument_list|(
name|indexService
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|indexMetaData
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|MapperService
name|mapperService
init|=
name|mock
argument_list|(
name|MapperService
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|indexService
operator|.
name|mapperService
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|mapperService
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|mapperService
operator|.
name|docMappers
argument_list|(
name|anyBoolean
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|indexService
operator|.
name|getIndexEventListener
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
operator|new
name|IndexEventListener
argument_list|()
block|{}
argument_list|)
expr_stmt|;
return|return
name|indexService
return|;
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// services
name|TransportService
name|transportService
init|=
operator|new
name|TransportService
argument_list|(
name|settings
argument_list|,
name|transport
argument_list|,
name|threadPool
argument_list|)
decl_stmt|;
name|MetaDataIndexUpgradeService
name|metaDataIndexUpgradeService
init|=
operator|new
name|MetaDataIndexUpgradeService
argument_list|(
name|settings
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|{
comment|// metaData upgrader should do nothing
annotation|@
name|Override
specifier|public
name|IndexMetaData
name|upgradeIndexMetaData
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
return|return
name|indexMetaData
return|;
block|}
block|}
decl_stmt|;
name|NodeServicesProvider
name|nodeServicesProvider
init|=
operator|new
name|NodeServicesProvider
argument_list|(
name|threadPool
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|clusterService
argument_list|)
decl_stmt|;
name|MetaDataIndexStateService
name|indexStateService
init|=
operator|new
name|MetaDataIndexStateService
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|allocationService
argument_list|,
name|metaDataIndexUpgradeService
argument_list|,
name|nodeServicesProvider
argument_list|,
name|indicesService
argument_list|)
decl_stmt|;
name|MetaDataDeleteIndexService
name|deleteIndexService
init|=
operator|new
name|MetaDataDeleteIndexService
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|allocationService
argument_list|)
decl_stmt|;
name|MetaDataUpdateSettingsService
name|metaDataUpdateSettingsService
init|=
operator|new
name|MetaDataUpdateSettingsService
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|allocationService
argument_list|,
name|IndexScopedSettings
operator|.
name|DEFAULT_SCOPED_SETTINGS
argument_list|,
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|settings
argument_list|)
argument_list|)
decl_stmt|;
name|MetaDataCreateIndexService
name|createIndexService
init|=
operator|new
name|MetaDataCreateIndexService
argument_list|(
name|settings
argument_list|,
name|clusterService
argument_list|,
name|indicesService
argument_list|,
name|allocationService
argument_list|,
operator|new
name|AliasValidator
argument_list|(
name|settings
argument_list|)
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
name|environment
argument_list|,
name|nodeServicesProvider
argument_list|,
name|IndexScopedSettings
operator|.
name|DEFAULT_SCOPED_SETTINGS
argument_list|)
decl_stmt|;
name|transportCloseIndexAction
operator|=
operator|new
name|TransportCloseIndexAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|indexStateService
argument_list|,
name|clusterSettings
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|destructiveOperations
argument_list|)
expr_stmt|;
name|transportOpenIndexAction
operator|=
operator|new
name|TransportOpenIndexAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|indexStateService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|destructiveOperations
argument_list|)
expr_stmt|;
name|transportDeleteIndexAction
operator|=
operator|new
name|TransportDeleteIndexAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|deleteIndexService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|destructiveOperations
argument_list|)
expr_stmt|;
name|transportUpdateSettingsAction
operator|=
operator|new
name|TransportUpdateSettingsAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|metaDataUpdateSettingsService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|)
expr_stmt|;
name|transportClusterRerouteAction
operator|=
operator|new
name|TransportClusterRerouteAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|allocationService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|)
expr_stmt|;
name|transportCreateIndexAction
operator|=
operator|new
name|TransportCreateIndexAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|createIndexService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|)
expr_stmt|;
block|}
DECL|method|createIndex
specifier|public
name|ClusterState
name|createIndex
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|CreateIndexRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|transportCreateIndexAction
argument_list|,
name|request
argument_list|,
name|state
argument_list|)
return|;
block|}
DECL|method|closeIndices
specifier|public
name|ClusterState
name|closeIndices
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|CloseIndexRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|transportCloseIndexAction
argument_list|,
name|request
argument_list|,
name|state
argument_list|)
return|;
block|}
DECL|method|openIndices
specifier|public
name|ClusterState
name|openIndices
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|OpenIndexRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|transportOpenIndexAction
argument_list|,
name|request
argument_list|,
name|state
argument_list|)
return|;
block|}
DECL|method|deleteIndices
specifier|public
name|ClusterState
name|deleteIndices
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|DeleteIndexRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|transportDeleteIndexAction
argument_list|,
name|request
argument_list|,
name|state
argument_list|)
return|;
block|}
DECL|method|updateSettings
specifier|public
name|ClusterState
name|updateSettings
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|UpdateSettingsRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|transportUpdateSettingsAction
argument_list|,
name|request
argument_list|,
name|state
argument_list|)
return|;
block|}
DECL|method|reroute
specifier|public
name|ClusterState
name|reroute
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|ClusterRerouteRequest
name|request
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|transportClusterRerouteAction
argument_list|,
name|request
argument_list|,
name|state
argument_list|)
return|;
block|}
DECL|method|applyFailedShards
specifier|public
name|ClusterState
name|applyFailedShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|List
argument_list|<
name|FailedRerouteAllocation
operator|.
name|FailedShard
argument_list|>
name|failedShards
parameter_list|)
block|{
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
init|=
name|allocationService
operator|.
name|applyFailedShards
argument_list|(
name|clusterState
argument_list|,
name|failedShards
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|rerouteResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|applyStartedShards
specifier|public
name|ClusterState
name|applyStartedShards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|startedShards
parameter_list|)
block|{
name|RoutingAllocation
operator|.
name|Result
name|rerouteResult
init|=
name|allocationService
operator|.
name|applyStartedShards
argument_list|(
name|clusterState
argument_list|,
name|startedShards
argument_list|)
decl_stmt|;
return|return
name|ClusterState
operator|.
name|builder
argument_list|(
name|clusterState
argument_list|)
operator|.
name|routingResult
argument_list|(
name|rerouteResult
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|execute
specifier|private
parameter_list|<
name|Request
extends|extends
name|MasterNodeRequest
argument_list|<
name|Request
argument_list|>
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
name|ClusterState
name|execute
parameter_list|(
name|TransportMasterNodeAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|masterNodeAction
parameter_list|,
name|Request
name|request
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|)
block|{
return|return
name|executeClusterStateUpdateTask
argument_list|(
name|clusterState
argument_list|,
parameter_list|()
lambda|->
block|{
try|try
block|{
name|TransportMasterNodeActionUtils
operator|.
name|runMasterOperation
argument_list|(
name|masterNodeAction
argument_list|,
name|request
argument_list|,
name|clusterState
argument_list|,
operator|new
name|PlainActionFuture
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|executeClusterStateUpdateTask
specifier|private
name|ClusterState
name|executeClusterStateUpdateTask
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Runnable
name|runnable
parameter_list|)
block|{
name|ClusterState
index|[]
name|result
init|=
operator|new
name|ClusterState
index|[
literal|1
index|]
decl_stmt|;
name|doAnswer
argument_list|(
name|invocationOnMock
lambda|->
block|{
name|ClusterStateUpdateTask
name|task
init|=
operator|(
name|ClusterStateUpdateTask
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|1
index|]
decl_stmt|;
name|result
index|[
literal|0
index|]
operator|=
name|task
operator|.
name|execute
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
operator|.
name|when
argument_list|(
name|clusterService
argument_list|)
operator|.
name|submitStateUpdateTask
argument_list|(
name|anyString
argument_list|()
argument_list|,
name|any
argument_list|(
name|ClusterStateUpdateTask
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|runnable
operator|.
name|run
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|result
index|[
literal|0
index|]
argument_list|,
name|notNullValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
index|[
literal|0
index|]
return|;
block|}
block|}
end_class

end_unit

