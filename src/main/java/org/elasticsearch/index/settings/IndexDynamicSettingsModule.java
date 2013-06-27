begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
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
name|routing
operator|.
name|allocation
operator|.
name|decider
operator|.
name|DisableAllocationDecider
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
name|settings
operator|.
name|DynamicSettings
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
name|settings
operator|.
name|Validator
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
name|AbstractModule
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
name|local
operator|.
name|LocalGatewayAllocator
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
name|engine
operator|.
name|robin
operator|.
name|RobinEngine
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
name|gateway
operator|.
name|IndexShardGatewayService
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
name|indexing
operator|.
name|slowlog
operator|.
name|ShardSlowLogIndexingService
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
name|merge
operator|.
name|policy
operator|.
name|LogByteSizeMergePolicyProvider
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
name|merge
operator|.
name|policy
operator|.
name|LogDocMergePolicyProvider
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
name|merge
operator|.
name|policy
operator|.
name|TieredMergePolicyProvider
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
name|search
operator|.
name|slowlog
operator|.
name|ShardSlowLogSearchService
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
name|service
operator|.
name|InternalIndexShard
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
name|support
operator|.
name|AbstractIndexStore
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
name|translog
operator|.
name|TranslogService
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
name|translog
operator|.
name|fs
operator|.
name|FsTranslog
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
name|indices
operator|.
name|warmer
operator|.
name|InternalIndicesWarmer
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndexDynamicSettingsModule
specifier|public
class|class
name|IndexDynamicSettingsModule
extends|extends
name|AbstractModule
block|{
DECL|field|indexDynamicSettings
specifier|private
specifier|final
name|DynamicSettings
name|indexDynamicSettings
decl_stmt|;
DECL|method|IndexDynamicSettingsModule
specifier|public
name|IndexDynamicSettingsModule
parameter_list|()
block|{
name|indexDynamicSettings
operator|=
operator|new
name|DynamicSettings
argument_list|()
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|FilterAllocationDecider
operator|.
name|INDEX_ROUTING_REQUIRE_GROUP
operator|+
literal|"*"
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|FilterAllocationDecider
operator|.
name|INDEX_ROUTING_INCLUDE_GROUP
operator|+
literal|"*"
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|FilterAllocationDecider
operator|.
name|INDEX_ROUTING_EXCLUDE_GROUP
operator|+
literal|"*"
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|DisableAllocationDecider
operator|.
name|INDEX_ROUTING_ALLOCATION_DISABLE_ALLOCATION
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|DisableAllocationDecider
operator|.
name|INDEX_ROUTING_ALLOCATION_DISABLE_NEW_ALLOCATION
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|DisableAllocationDecider
operator|.
name|INDEX_ROUTING_ALLOCATION_DISABLE_REPLICA_ALLOCATION
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|FsTranslog
operator|.
name|INDEX_TRANSLOG_FS_TYPE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|FsTranslog
operator|.
name|INDEX_TRANSLOG_FS_BUFFER_SIZE
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|FsTranslog
operator|.
name|INDEX_TRANSLOG_FS_TRANSIENT_BUFFER_SIZE
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
name|Validator
operator|.
name|NON_NEGATIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_AUTO_EXPAND_REPLICAS
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_READ_ONLY
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_READ
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_WRITE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_BLOCKS_METADATA
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndexShardGatewayService
operator|.
name|INDEX_GATEWAY_SNAPSHOT_INTERVAL
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|IndicesTTLService
operator|.
name|INDEX_TTL_DISABLE_PURGE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|InternalIndexShard
operator|.
name|INDEX_REFRESH_INTERVAL
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LocalGatewayAllocator
operator|.
name|INDEX_RECOVERY_INITIAL_SHARDS
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MIN_MERGE_SIZE
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_SIZE
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_DOCS
argument_list|,
name|Validator
operator|.
name|POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MERGE_FACTOR
argument_list|,
name|Validator
operator|.
name|INTEGER_GTE_2
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogByteSizeMergePolicyProvider
operator|.
name|INDEX_COMPOUND_FORMAT
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogDocMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MIN_MERGE_DOCS
argument_list|,
name|Validator
operator|.
name|POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogDocMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_DOCS
argument_list|,
name|Validator
operator|.
name|POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogDocMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MERGE_FACTOR
argument_list|,
name|Validator
operator|.
name|INTEGER_GTE_2
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|LogDocMergePolicyProvider
operator|.
name|INDEX_COMPOUND_FORMAT
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|RobinEngine
operator|.
name|INDEX_TERM_INDEX_INTERVAL
argument_list|,
name|Validator
operator|.
name|POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|RobinEngine
operator|.
name|INDEX_TERM_INDEX_DIVISOR
argument_list|,
name|Validator
operator|.
name|POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|RobinEngine
operator|.
name|INDEX_INDEX_CONCURRENCY
argument_list|,
name|Validator
operator|.
name|NON_NEGATIVE_INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|RobinEngine
operator|.
name|INDEX_GC_DELETES
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|RobinEngine
operator|.
name|INDEX_CODEC
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|RobinEngine
operator|.
name|INDEX_FAIL_ON_MERGE_FAILURE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|INDEX_INDEXING_SLOWLOG_REFORMAT
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogIndexingService
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_REFORMAT
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardSlowLogSearchService
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|ShardsLimitAllocationDecider
operator|.
name|INDEX_TOTAL_SHARDS_PER_NODE
argument_list|,
name|Validator
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_EXPUNGE_DELETES_ALLOWED
argument_list|,
name|Validator
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_FLOOR_SEGMENT
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_AT_ONCE
argument_list|,
name|Validator
operator|.
name|INTEGER_GTE_2
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_AT_ONCE_EXPLICIT
argument_list|,
name|Validator
operator|.
name|INTEGER_GTE_2
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGED_SEGMENT
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_SEGMENTS_PER_TIER
argument_list|,
name|Validator
operator|.
name|DOUBLE_GTE_2
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_RECLAIM_DELETES_WEIGHT
argument_list|,
name|Validator
operator|.
name|NON_NEGATIVE_DOUBLE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_COMPOUND_FORMAT
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TranslogService
operator|.
name|INDEX_TRANSLOG_FLUSH_THRESHOLD_OPS
argument_list|,
name|Validator
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TranslogService
operator|.
name|INDEX_TRANSLOG_FLUSH_THRESHOLD_SIZE
argument_list|,
name|Validator
operator|.
name|BYTES_SIZE
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TranslogService
operator|.
name|INDEX_TRANSLOG_FLUSH_THRESHOLD_PERIOD
argument_list|,
name|Validator
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|TranslogService
operator|.
name|INDEX_TRANSLOG_DISABLE_FLUSH
argument_list|)
expr_stmt|;
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|InternalIndicesWarmer
operator|.
name|INDEX_WARMER_ENABLED
argument_list|)
expr_stmt|;
block|}
DECL|method|addDynamicSettings
specifier|public
name|void
name|addDynamicSettings
parameter_list|(
name|String
modifier|...
name|settings
parameter_list|)
block|{
name|indexDynamicSettings
operator|.
name|addDynamicSettings
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|addDynamicSetting
specifier|public
name|void
name|addDynamicSetting
parameter_list|(
name|String
name|setting
parameter_list|,
name|Validator
name|validator
parameter_list|)
block|{
name|indexDynamicSettings
operator|.
name|addDynamicSetting
argument_list|(
name|setting
argument_list|,
name|validator
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|configure
specifier|protected
name|void
name|configure
parameter_list|()
block|{
name|bind
argument_list|(
name|DynamicSettings
operator|.
name|class
argument_list|)
operator|.
name|annotatedWith
argument_list|(
name|IndexDynamicSettings
operator|.
name|class
argument_list|)
operator|.
name|toInstance
argument_list|(
name|indexDynamicSettings
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

