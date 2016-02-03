begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
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
name|support
operator|.
name|AutoCreateIndex
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
name|master
operator|.
name|TransportMasterNodeReadAction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|recycler
operator|.
name|PageCacheRecycler
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
name|TransportClientNodesService
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
name|ClusterModule
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
name|ClusterName
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
name|InternalClusterInfoService
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
name|action
operator|.
name|index
operator|.
name|MappingUpdatedAction
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
name|AwarenessAllocationDecider
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
name|ClusterRebalanceAllocationDecider
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
name|ConcurrentRebalanceAllocationDecider
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
name|DiskThresholdDecider
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
name|routing
operator|.
name|allocation
operator|.
name|decider
operator|.
name|FilterAllocationDecider
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
name|ShardsLimitAllocationDecider
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
name|SnapshotInProgressAllocationDecider
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
name|ThrottlingAllocationDecider
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
name|InternalClusterService
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
name|logging
operator|.
name|ESLoggerFactory
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
name|network
operator|.
name|NetworkModule
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
name|network
operator|.
name|NetworkService
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
name|util
operator|.
name|concurrent
operator|.
name|EsExecutors
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
name|util
operator|.
name|concurrent
operator|.
name|ThreadContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|DiscoveryModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|DiscoveryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|DiscoverySettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|ZenDiscovery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|elect
operator|.
name|ElectMasterService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|fd
operator|.
name|FaultDetection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|unicast
operator|.
name|UnicastZenPing
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
name|env
operator|.
name|NodeEnvironment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|GatewayService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|PrimaryShardAllocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|HttpTransportSettings
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
name|IndexSettings
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
name|store
operator|.
name|IndexStoreConfig
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
name|analysis
operator|.
name|HunspellService
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
name|breaker
operator|.
name|HierarchyCircuitBreakerService
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
name|cache
operator|.
name|query
operator|.
name|IndicesQueryCache
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
name|cache
operator|.
name|request
operator|.
name|IndicesRequestCache
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
name|fielddata
operator|.
name|cache
operator|.
name|IndicesFieldDataCache
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
name|recovery
operator|.
name|RecoverySettings
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
name|store
operator|.
name|IndicesStore
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
name|ttl
operator|.
name|IndicesTTLService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|fs
operator|.
name|FsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmGcMonitorService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|jvm
operator|.
name|JvmService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|os
operator|.
name|OsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|process
operator|.
name|ProcessService
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
name|Node
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
name|InternalSettingsPreparer
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
name|PluginsService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|fs
operator|.
name|FsRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|repositories
operator|.
name|uri
operator|.
name|URLRepository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|BaseRestHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|ScriptService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|SearchService
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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportSettings
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
name|netty
operator|.
name|NettyTransport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tribe
operator|.
name|TribeService
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
name|Set
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
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Encapsulates all valid cluster level settings.  */
end_comment

begin_class
DECL|class|ClusterSettings
specifier|public
specifier|final
class|class
name|ClusterSettings
extends|extends
name|AbstractScopedSettings
block|{
DECL|method|ClusterSettings
specifier|public
name|ClusterSettings
parameter_list|(
name|Settings
name|nodeSettings
parameter_list|,
name|Set
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|settingsSet
parameter_list|)
block|{
name|super
argument_list|(
name|nodeSettings
argument_list|,
name|settingsSet
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|CLUSTER
argument_list|)
expr_stmt|;
name|addSettingsUpdater
argument_list|(
operator|new
name|LoggingSettingUpdater
argument_list|(
name|nodeSettings
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|LoggingSettingUpdater
specifier|private
specifier|static
specifier|final
class|class
name|LoggingSettingUpdater
implements|implements
name|SettingUpdater
argument_list|<
name|Settings
argument_list|>
block|{
DECL|field|loggerPredicate
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|loggerPredicate
init|=
name|ESLoggerFactory
operator|.
name|LOG_LEVEL_SETTING
operator|::
name|match
decl_stmt|;
DECL|field|settings
specifier|private
specifier|final
name|Settings
name|settings
decl_stmt|;
DECL|method|LoggingSettingUpdater
name|LoggingSettingUpdater
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasChanged
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
block|{
return|return
name|current
operator|.
name|filter
argument_list|(
name|loggerPredicate
argument_list|)
operator|.
name|getAsMap
argument_list|()
operator|.
name|equals
argument_list|(
name|previous
operator|.
name|filter
argument_list|(
name|loggerPredicate
argument_list|)
operator|.
name|getAsMap
argument_list|()
argument_list|)
operator|==
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getValue
specifier|public
name|Settings
name|getValue
parameter_list|(
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|current
operator|.
name|filter
argument_list|(
name|loggerPredicate
argument_list|)
operator|.
name|getAsMap
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|key
range|:
name|previous
operator|.
name|getAsMap
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|loggerPredicate
operator|.
name|test
argument_list|(
name|key
argument_list|)
operator|&&
name|builder
operator|.
name|internalMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
operator|==
literal|false
condition|)
block|{
if|if
condition|(
name|ESLoggerFactory
operator|.
name|LOG_LEVEL_SETTING
operator|.
name|getConcreteSetting
argument_list|(
name|key
argument_list|)
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
operator|==
literal|false
condition|)
block|{
name|builder
operator|.
name|putNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|ESLoggerFactory
operator|.
name|LOG_LEVEL_SETTING
operator|.
name|getConcreteSetting
argument_list|(
name|key
argument_list|)
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|apply
specifier|public
name|void
name|apply
parameter_list|(
name|Settings
name|value
parameter_list|,
name|Settings
name|current
parameter_list|,
name|Settings
name|previous
parameter_list|)
block|{
for|for
control|(
name|String
name|key
range|:
name|value
operator|.
name|getAsMap
argument_list|()
operator|.
name|keySet
argument_list|()
control|)
block|{
assert|assert
name|loggerPredicate
operator|.
name|test
argument_list|(
name|key
argument_list|)
assert|;
name|String
name|component
init|=
name|key
operator|.
name|substring
argument_list|(
literal|"logger."
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"_root"
operator|.
name|equals
argument_list|(
name|component
argument_list|)
condition|)
block|{
specifier|final
name|String
name|rootLevel
init|=
name|value
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|ESLoggerFactory
operator|.
name|getRootLogger
argument_list|()
operator|.
name|setLevel
argument_list|(
name|rootLevel
operator|==
literal|null
condition|?
name|ESLoggerFactory
operator|.
name|LOG_DEFAULT_LEVEL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|.
name|name
argument_list|()
else|:
name|rootLevel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|component
argument_list|)
operator|.
name|setLevel
argument_list|(
name|value
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
empty_stmt|;
DECL|field|BUILT_IN_CLUSTER_SETTINGS
specifier|public
specifier|static
name|Set
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|BUILT_IN_CLUSTER_SETTINGS
init|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|AwarenessAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_AWARENESS_ATTRIBUTE_SETTING
argument_list|,
name|TransportClientNodesService
operator|.
name|CLIENT_TRANSPORT_NODES_SAMPLER_INTERVAL
argument_list|,
comment|// TODO these transport client settings are kind of odd here and should only be valid if we are a transport client
name|TransportClientNodesService
operator|.
name|CLIENT_TRANSPORT_PING_TIMEOUT
argument_list|,
name|TransportClientNodesService
operator|.
name|CLIENT_TRANSPORT_IGNORE_CLUSTER_NAME
argument_list|,
name|TransportClientNodesService
operator|.
name|CLIENT_TRANSPORT_SNIFF
argument_list|,
name|AwarenessAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_AWARENESS_FORCE_GROUP_SETTING
argument_list|,
name|BalancedShardsAllocator
operator|.
name|INDEX_BALANCE_FACTOR_SETTING
argument_list|,
name|BalancedShardsAllocator
operator|.
name|SHARD_BALANCE_FACTOR_SETTING
argument_list|,
name|BalancedShardsAllocator
operator|.
name|THRESHOLD_SETTING
argument_list|,
name|ClusterRebalanceAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ALLOW_REBALANCE_SETTING
argument_list|,
name|ConcurrentRebalanceAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_CLUSTER_CONCURRENT_REBALANCE_SETTING
argument_list|,
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_ENABLE_SETTING
argument_list|,
name|EnableAllocationDecider
operator|.
name|CLUSTER_ROUTING_REBALANCE_ENABLE_SETTING
argument_list|,
name|FilterAllocationDecider
operator|.
name|CLUSTER_ROUTING_INCLUDE_GROUP_SETTING
argument_list|,
name|FilterAllocationDecider
operator|.
name|CLUSTER_ROUTING_EXCLUDE_GROUP_SETTING
argument_list|,
name|FilterAllocationDecider
operator|.
name|CLUSTER_ROUTING_REQUIRE_GROUP_SETTING
argument_list|,
name|FsRepository
operator|.
name|REPOSITORIES_CHUNK_SIZE_SETTING
argument_list|,
name|FsRepository
operator|.
name|REPOSITORIES_COMPRESS_SETTING
argument_list|,
name|FsRepository
operator|.
name|REPOSITORIES_LOCATION_SETTING
argument_list|,
name|IndexStoreConfig
operator|.
name|INDICES_STORE_THROTTLE_TYPE_SETTING
argument_list|,
name|IndexStoreConfig
operator|.
name|INDICES_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
argument_list|,
name|IndicesQueryCache
operator|.
name|INDICES_CACHE_QUERY_SIZE_SETTING
argument_list|,
name|IndicesQueryCache
operator|.
name|INDICES_CACHE_QUERY_COUNT_SETTING
argument_list|,
name|IndicesTTLService
operator|.
name|INDICES_TTL_INTERVAL_SETTING
argument_list|,
name|MappingUpdatedAction
operator|.
name|INDICES_MAPPING_DYNAMIC_TIMEOUT_SETTING
argument_list|,
name|MetaData
operator|.
name|SETTING_READ_ONLY_SETTING
argument_list|,
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_MAX_BYTES_PER_SEC_SETTING
argument_list|,
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_RETRY_DELAY_STATE_SYNC_SETTING
argument_list|,
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_RETRY_DELAY_NETWORK_SETTING
argument_list|,
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_ACTIVITY_TIMEOUT_SETTING
argument_list|,
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_INTERNAL_ACTION_TIMEOUT_SETTING
argument_list|,
name|RecoverySettings
operator|.
name|INDICES_RECOVERY_INTERNAL_LONG_ACTION_TIMEOUT_SETTING
argument_list|,
name|ThreadPool
operator|.
name|THREADPOOL_GROUP_SETTING
argument_list|,
name|ThrottlingAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_NODE_INITIAL_PRIMARIES_RECOVERIES_SETTING
argument_list|,
name|ThrottlingAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_NODE_CONCURRENT_INCOMING_RECOVERIES_SETTING
argument_list|,
name|ThrottlingAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_NODE_CONCURRENT_OUTGOING_RECOVERIES_SETTING
argument_list|,
name|ThrottlingAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_NODE_CONCURRENT_RECOVERIES_SETTING
argument_list|,
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_LOW_DISK_WATERMARK_SETTING
argument_list|,
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_HIGH_DISK_WATERMARK_SETTING
argument_list|,
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_DISK_THRESHOLD_ENABLED_SETTING
argument_list|,
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_INCLUDE_RELOCATIONS_SETTING
argument_list|,
name|DiskThresholdDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_REROUTE_INTERVAL_SETTING
argument_list|,
name|InternalClusterInfoService
operator|.
name|INTERNAL_CLUSTER_INFO_UPDATE_INTERVAL_SETTING
argument_list|,
name|InternalClusterInfoService
operator|.
name|INTERNAL_CLUSTER_INFO_TIMEOUT_SETTING
argument_list|,
name|SnapshotInProgressAllocationDecider
operator|.
name|CLUSTER_ROUTING_ALLOCATION_SNAPSHOT_RELOCATION_ENABLED_SETTING
argument_list|,
name|DestructiveOperations
operator|.
name|REQUIRES_NAME_SETTING
argument_list|,
name|DiscoverySettings
operator|.
name|PUBLISH_TIMEOUT_SETTING
argument_list|,
name|DiscoverySettings
operator|.
name|PUBLISH_DIFF_ENABLE_SETTING
argument_list|,
name|DiscoverySettings
operator|.
name|COMMIT_TIMEOUT_SETTING
argument_list|,
name|DiscoverySettings
operator|.
name|NO_MASTER_BLOCK_SETTING
argument_list|,
name|GatewayService
operator|.
name|EXPECTED_DATA_NODES_SETTING
argument_list|,
name|GatewayService
operator|.
name|EXPECTED_MASTER_NODES_SETTING
argument_list|,
name|GatewayService
operator|.
name|EXPECTED_NODES_SETTING
argument_list|,
name|GatewayService
operator|.
name|RECOVER_AFTER_DATA_NODES_SETTING
argument_list|,
name|GatewayService
operator|.
name|RECOVER_AFTER_MASTER_NODES_SETTING
argument_list|,
name|GatewayService
operator|.
name|RECOVER_AFTER_NODES_SETTING
argument_list|,
name|GatewayService
operator|.
name|RECOVER_AFTER_TIME_SETTING
argument_list|,
name|NetworkModule
operator|.
name|HTTP_ENABLED
argument_list|,
name|NetworkModule
operator|.
name|HTTP_TYPE_SETTING
argument_list|,
name|NetworkModule
operator|.
name|TRANSPORT_SERVICE_TYPE_SETTING
argument_list|,
name|NetworkModule
operator|.
name|TRANSPORT_TYPE_SETTING
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_CORS_ALLOW_CREDENTIALS
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_CORS_ENABLED
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_CORS_MAX_AGE
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_DETAILED_ERRORS_ENABLED
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_PIPELINING
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_CORS_ALLOW_ORIGIN
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_PORT
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_PUBLISH_PORT
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_PIPELINING_MAX_EVENTS
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_COMPRESSION
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_COMPRESSION_LEVEL
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_CORS_ALLOW_METHODS
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_CORS_ALLOW_HEADERS
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_DETAILED_ERRORS_ENABLED
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_MAX_CONTENT_LENGTH
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_MAX_CHUNK_SIZE
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_MAX_HEADER_SIZE
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_MAX_INITIAL_LINE_LENGTH
argument_list|,
name|HttpTransportSettings
operator|.
name|SETTING_HTTP_RESET_COOKIES
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|TOTAL_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|REQUEST_CIRCUIT_BREAKER_LIMIT_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|REQUEST_CIRCUIT_BREAKER_OVERHEAD_SETTING
argument_list|,
name|InternalClusterService
operator|.
name|CLUSTER_SERVICE_SLOW_TASK_LOGGING_THRESHOLD_SETTING
argument_list|,
name|SearchService
operator|.
name|DEFAULT_SEARCH_TIMEOUT_SETTING
argument_list|,
name|ElectMasterService
operator|.
name|DISCOVERY_ZEN_MINIMUM_MASTER_NODES_SETTING
argument_list|,
name|TransportService
operator|.
name|TRACE_LOG_EXCLUDE_SETTING
argument_list|,
name|TransportService
operator|.
name|TRACE_LOG_INCLUDE_SETTING
argument_list|,
name|TransportCloseIndexAction
operator|.
name|CLUSTER_INDICES_CLOSE_ENABLE_SETTING
argument_list|,
name|ShardsLimitAllocationDecider
operator|.
name|CLUSTER_TOTAL_SHARDS_PER_NODE_SETTING
argument_list|,
name|InternalClusterService
operator|.
name|CLUSTER_SERVICE_RECONNECT_INTERVAL_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|FIELDDATA_CIRCUIT_BREAKER_TYPE_SETTING
argument_list|,
name|HierarchyCircuitBreakerService
operator|.
name|REQUEST_CIRCUIT_BREAKER_TYPE_SETTING
argument_list|,
name|Transport
operator|.
name|TRANSPORT_TCP_COMPRESS
argument_list|,
name|TransportSettings
operator|.
name|TRANSPORT_PROFILES_SETTING
argument_list|,
name|TransportSettings
operator|.
name|HOST
argument_list|,
name|TransportSettings
operator|.
name|PUBLISH_HOST
argument_list|,
name|TransportSettings
operator|.
name|BIND_HOST
argument_list|,
name|TransportSettings
operator|.
name|PUBLISH_PORT
argument_list|,
name|TransportSettings
operator|.
name|PORT
argument_list|,
name|NettyTransport
operator|.
name|WORKER_COUNT
argument_list|,
name|NettyTransport
operator|.
name|CONNECTIONS_PER_NODE_RECOVERY
argument_list|,
name|NettyTransport
operator|.
name|CONNECTIONS_PER_NODE_BULK
argument_list|,
name|NettyTransport
operator|.
name|CONNECTIONS_PER_NODE_REG
argument_list|,
name|NettyTransport
operator|.
name|CONNECTIONS_PER_NODE_STATE
argument_list|,
name|NettyTransport
operator|.
name|CONNECTIONS_PER_NODE_PING
argument_list|,
name|NettyTransport
operator|.
name|PING_SCHEDULE
argument_list|,
name|NettyTransport
operator|.
name|TCP_BLOCKING_CLIENT
argument_list|,
name|NettyTransport
operator|.
name|TCP_CONNECT_TIMEOUT
argument_list|,
name|NettyTransport
operator|.
name|NETTY_MAX_CUMULATION_BUFFER_CAPACITY
argument_list|,
name|NettyTransport
operator|.
name|NETTY_MAX_COMPOSITE_BUFFER_COMPONENTS
argument_list|,
name|NettyTransport
operator|.
name|NETTY_RECEIVE_PREDICTOR_SIZE
argument_list|,
name|NettyTransport
operator|.
name|NETTY_RECEIVE_PREDICTOR_MIN
argument_list|,
name|NettyTransport
operator|.
name|NETTY_RECEIVE_PREDICTOR_MAX
argument_list|,
name|NetworkService
operator|.
name|NETWORK_SERVER
argument_list|,
name|NettyTransport
operator|.
name|NETTY_BOSS_COUNT
argument_list|,
name|NettyTransport
operator|.
name|TCP_NO_DELAY
argument_list|,
name|NettyTransport
operator|.
name|TCP_KEEP_ALIVE
argument_list|,
name|NettyTransport
operator|.
name|TCP_REUSE_ADDRESS
argument_list|,
name|NettyTransport
operator|.
name|TCP_SEND_BUFFER_SIZE
argument_list|,
name|NettyTransport
operator|.
name|TCP_RECEIVE_BUFFER_SIZE
argument_list|,
name|NettyTransport
operator|.
name|TCP_BLOCKING_SERVER
argument_list|,
name|NetworkService
operator|.
name|GLOBAL_NETWORK_HOST_SETTING
argument_list|,
name|NetworkService
operator|.
name|GLOBAL_NETWORK_BINDHOST_SETTING
argument_list|,
name|NetworkService
operator|.
name|GLOBAL_NETWORK_PUBLISHHOST_SETTING
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_NO_DELAY
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_KEEP_ALIVE
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_REUSE_ADDRESS
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_SEND_BUFFER_SIZE
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_RECEIVE_BUFFER_SIZE
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_BLOCKING
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_BLOCKING_SERVER
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_BLOCKING_CLIENT
argument_list|,
name|NetworkService
operator|.
name|TcpSettings
operator|.
name|TCP_CONNECT_TIMEOUT
argument_list|,
name|IndexSettings
operator|.
name|QUERY_STRING_ANALYZE_WILDCARD
argument_list|,
name|IndexSettings
operator|.
name|QUERY_STRING_ALLOW_LEADING_WILDCARD
argument_list|,
name|PrimaryShardAllocator
operator|.
name|NODE_INITIAL_SHARDS_SETTING
argument_list|,
name|ScriptService
operator|.
name|SCRIPT_CACHE_SIZE_SETTING
argument_list|,
name|ScriptService
operator|.
name|SCRIPT_CACHE_EXPIRE_SETTING
argument_list|,
name|ScriptService
operator|.
name|SCRIPT_AUTO_RELOAD_ENABLED_SETTING
argument_list|,
name|IndicesFieldDataCache
operator|.
name|INDICES_FIELDDATA_CLEAN_INTERVAL_SETTING
argument_list|,
name|IndicesFieldDataCache
operator|.
name|INDICES_FIELDDATA_CACHE_SIZE_KEY
argument_list|,
name|IndicesRequestCache
operator|.
name|INDICES_CACHE_QUERY_SIZE
argument_list|,
name|IndicesRequestCache
operator|.
name|INDICES_CACHE_QUERY_EXPIRE
argument_list|,
name|IndicesRequestCache
operator|.
name|INDICES_CACHE_REQUEST_CLEAN_INTERVAL
argument_list|,
name|HunspellService
operator|.
name|HUNSPELL_LAZY_LOAD
argument_list|,
name|HunspellService
operator|.
name|HUNSPELL_IGNORE_CASE
argument_list|,
name|HunspellService
operator|.
name|HUNSPELL_DICTIONARY_OPTIONS
argument_list|,
name|IndicesStore
operator|.
name|INDICES_STORE_DELETE_SHARD_TIMEOUT
argument_list|,
name|Environment
operator|.
name|PATH_CONF_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_DATA_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_HOME_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_LOGS_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_PLUGINS_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_REPO_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_SCRIPTS_SETTING
argument_list|,
name|Environment
operator|.
name|PATH_SHARED_DATA_SETTING
argument_list|,
name|Environment
operator|.
name|PIDFILE_SETTING
argument_list|,
name|DiscoveryService
operator|.
name|DISCOVERY_SEED_SETTING
argument_list|,
name|DiscoveryService
operator|.
name|INITIAL_STATE_TIMEOUT_SETTING
argument_list|,
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_SETTING
argument_list|,
name|DiscoveryModule
operator|.
name|ZEN_MASTER_SERVICE_TYPE_SETTING
argument_list|,
name|FaultDetection
operator|.
name|PING_RETRIES_SETTING
argument_list|,
name|FaultDetection
operator|.
name|PING_TIMEOUT_SETTING
argument_list|,
name|FaultDetection
operator|.
name|REGISTER_CONNECTION_LISTENER_SETTING
argument_list|,
name|FaultDetection
operator|.
name|PING_INTERVAL_SETTING
argument_list|,
name|FaultDetection
operator|.
name|CONNECT_ON_NETWORK_DISCONNECT_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|PING_TIMEOUT_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|JOIN_TIMEOUT_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|JOIN_RETRY_ATTEMPTS_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|JOIN_RETRY_DELAY_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|MAX_PINGS_FROM_ANOTHER_MASTER_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|SEND_LEAVE_REQUEST_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|MASTER_ELECTION_FILTER_CLIENT_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|MASTER_ELECTION_WAIT_FOR_JOINS_TIMEOUT_SETTING
argument_list|,
name|ZenDiscovery
operator|.
name|MASTER_ELECTION_FILTER_DATA_SETTING
argument_list|,
name|UnicastZenPing
operator|.
name|DISCOVERY_ZEN_PING_UNICAST_HOSTS_SETTING
argument_list|,
name|UnicastZenPing
operator|.
name|DISCOVERY_ZEN_PING_UNICAST_CONCURRENT_CONNECTS_SETTING
argument_list|,
name|SearchService
operator|.
name|DEFAULT_KEEPALIVE_SETTING
argument_list|,
name|SearchService
operator|.
name|KEEPALIVE_INTERVAL_SETTING
argument_list|,
name|Node
operator|.
name|WRITE_PORTS_FIELD_SETTING
argument_list|,
name|Node
operator|.
name|NODE_NAME_SETTING
argument_list|,
name|Node
operator|.
name|NODE_CLIENT_SETTING
argument_list|,
name|Node
operator|.
name|NODE_DATA_SETTING
argument_list|,
name|Node
operator|.
name|NODE_MASTER_SETTING
argument_list|,
name|Node
operator|.
name|NODE_LOCAL_SETTING
argument_list|,
name|Node
operator|.
name|NODE_MODE_SETTING
argument_list|,
name|Node
operator|.
name|NODE_INGEST_SETTING
argument_list|,
name|Node
operator|.
name|NODE_ATTRIBUTES
argument_list|,
name|URLRepository
operator|.
name|ALLOWED_URLS_SETTING
argument_list|,
name|URLRepository
operator|.
name|REPOSITORIES_LIST_DIRECTORIES_SETTING
argument_list|,
name|URLRepository
operator|.
name|REPOSITORIES_URL_SETTING
argument_list|,
name|URLRepository
operator|.
name|SUPPORTED_PROTOCOLS_SETTING
argument_list|,
name|TransportMasterNodeReadAction
operator|.
name|FORCE_LOCAL_SETTING
argument_list|,
name|AutoCreateIndex
operator|.
name|AUTO_CREATE_INDEX_SETTING
argument_list|,
name|BaseRestHandler
operator|.
name|MULTI_ALLOW_EXPLICIT_INDEX
argument_list|,
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
argument_list|,
name|Client
operator|.
name|CLIENT_TYPE_SETTING_S
argument_list|,
name|InternalSettingsPreparer
operator|.
name|IGNORE_SYSTEM_PROPERTIES_SETTING
argument_list|,
name|ClusterModule
operator|.
name|SHARDS_ALLOCATOR_TYPE_SETTING
argument_list|,
name|EsExecutors
operator|.
name|PROCESSORS_SETTING
argument_list|,
name|ThreadContext
operator|.
name|DEFAULT_HEADERS_SETTING
argument_list|,
name|ESLoggerFactory
operator|.
name|LOG_DEFAULT_LEVEL_SETTING
argument_list|,
name|ESLoggerFactory
operator|.
name|LOG_LEVEL_SETTING
argument_list|,
name|TribeService
operator|.
name|BLOCKS_METADATA_SETTING
argument_list|,
name|TribeService
operator|.
name|BLOCKS_WRITE_SETTING
argument_list|,
name|TribeService
operator|.
name|BLOCKS_WRITE_INDICES_SETTING
argument_list|,
name|TribeService
operator|.
name|BLOCKS_READ_INDICES_SETTING
argument_list|,
name|TribeService
operator|.
name|BLOCKS_METADATA_INDICES_SETTING
argument_list|,
name|TribeService
operator|.
name|ON_CONFLICT_SETTING
argument_list|,
name|TribeService
operator|.
name|TRIBE_NAME_SETTING
argument_list|,
name|NodeEnvironment
operator|.
name|MAX_LOCAL_STORAGE_NODES_SETTING
argument_list|,
name|NodeEnvironment
operator|.
name|ENABLE_LUCENE_SEGMENT_INFOS_TRACE_SETTING
argument_list|,
name|NodeEnvironment
operator|.
name|ADD_NODE_ID_TO_CUSTOM_PATH
argument_list|,
name|OsService
operator|.
name|REFRESH_INTERVAL_SETTING
argument_list|,
name|ProcessService
operator|.
name|REFRESH_INTERVAL_SETTING
argument_list|,
name|JvmService
operator|.
name|REFRESH_INTERVAL_SETTING
argument_list|,
name|FsService
operator|.
name|REFRESH_INTERVAL_SETTING
argument_list|,
name|JvmGcMonitorService
operator|.
name|ENABLED_SETTING
argument_list|,
name|JvmGcMonitorService
operator|.
name|REFRESH_INTERVAL_SETTING
argument_list|,
name|JvmGcMonitorService
operator|.
name|GC_SETTING
argument_list|,
name|PageCacheRecycler
operator|.
name|LIMIT_HEAP_SETTING
argument_list|,
name|PageCacheRecycler
operator|.
name|WEIGHT_BYTES_SETTING
argument_list|,
name|PageCacheRecycler
operator|.
name|WEIGHT_INT_SETTING
argument_list|,
name|PageCacheRecycler
operator|.
name|WEIGHT_LONG_SETTING
argument_list|,
name|PageCacheRecycler
operator|.
name|WEIGHT_OBJECTS_SETTING
argument_list|,
name|PageCacheRecycler
operator|.
name|TYPE_SETTING
argument_list|,
name|PluginsService
operator|.
name|MANDATORY_SETTING
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
block|}
end_class

end_unit

