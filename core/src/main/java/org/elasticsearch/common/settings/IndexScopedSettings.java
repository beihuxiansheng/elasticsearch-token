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
name|UnassignedInfo
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
name|ShardsLimitAllocationDecider
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
name|index
operator|.
name|IndexModule
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
name|IndexingSlowLog
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
name|MergePolicyConfig
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
name|MergeSchedulerConfig
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
name|SearchSlowLog
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
name|cache
operator|.
name|bitset
operator|.
name|BitsetFilterCache
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
name|EngineConfig
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
name|fielddata
operator|.
name|IndexFieldDataService
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
name|FieldMapper
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
name|percolator
operator|.
name|PercolatorQueriesRegistry
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
name|similarity
operator|.
name|SimilarityService
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
name|FsDirectoryService
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
name|IndexStore
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
name|Store
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
name|IndexWarmer
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
comment|/**  * Encapsulates all valid index level settings.  * @see org.elasticsearch.common.settings.Setting.Scope#INDEX  */
end_comment

begin_class
DECL|class|IndexScopedSettings
specifier|public
specifier|final
class|class
name|IndexScopedSettings
extends|extends
name|AbstractScopedSettings
block|{
DECL|field|INDEX_SETTINGS_KEY_PREDICATE
specifier|public
specifier|static
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|INDEX_SETTINGS_KEY_PREDICATE
init|=
parameter_list|(
name|s
parameter_list|)
lambda|->
name|s
operator|.
name|startsWith
argument_list|(
name|IndexMetaData
operator|.
name|INDEX_SETTING_PREFIX
argument_list|)
decl_stmt|;
DECL|field|BUILT_IN_INDEX_SETTINGS
specifier|public
specifier|static
name|Set
argument_list|<
name|Setting
argument_list|<
name|?
argument_list|>
argument_list|>
name|BUILT_IN_INDEX_SETTINGS
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
name|IndexSettings
operator|.
name|INDEX_TTL_DISABLE_PURGE_SETTING
argument_list|,
name|IndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE_SETTING
argument_list|,
name|IndexStore
operator|.
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC_SETTING
argument_list|,
name|MergeSchedulerConfig
operator|.
name|AUTO_THROTTLE_SETTING
argument_list|,
name|MergeSchedulerConfig
operator|.
name|MAX_MERGE_COUNT_SETTING
argument_list|,
name|MergeSchedulerConfig
operator|.
name|MAX_THREAD_COUNT_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_ROUTING_EXCLUDE_GROUP_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_ROUTING_INCLUDE_GROUP_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_ROUTING_REQUIRE_GROUP_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_AUTO_EXPAND_REPLICAS_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_NUMBER_OF_REPLICAS_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_NUMBER_OF_SHARDS_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_SHADOW_REPLICAS_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_SHARED_FILESYSTEM_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_READ_ONLY_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_READ_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_WRITE_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_BLOCKS_METADATA_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_SHARED_FS_ALLOW_RECOVERY_ON_ANY_NODE_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_PRIORITY_SETTING
argument_list|,
name|IndexMetaData
operator|.
name|INDEX_DATA_PATH_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_DEBUG_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_WARN_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_INFO_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_FETCH_TRACE_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_WARN_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_DEBUG_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_INFO_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_THRESHOLD_QUERY_TRACE_SETTING
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_LEVEL
argument_list|,
name|SearchSlowLog
operator|.
name|INDEX_SEARCH_SLOWLOG_REFORMAT
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_WARN_SETTING
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_DEBUG_SETTING
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_INFO_SETTING
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_THRESHOLD_INDEX_TRACE_SETTING
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_LEVEL_SETTING
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_REFORMAT_SETTING
argument_list|,
name|IndexingSlowLog
operator|.
name|INDEX_INDEXING_SLOWLOG_MAX_SOURCE_CHARS_TO_LOG_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_COMPOUND_FORMAT_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_EXPUNGE_DELETES_ALLOWED_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_FLOOR_SEGMENT_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_AT_ONCE_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_AT_ONCE_EXPLICIT_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGED_SEGMENT_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_SEGMENTS_PER_TIER_SETTING
argument_list|,
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_POLICY_RECLAIM_DELETES_WEIGHT_SETTING
argument_list|,
name|IndexSettings
operator|.
name|INDEX_TRANSLOG_DURABILITY_SETTING
argument_list|,
name|IndexSettings
operator|.
name|INDEX_WARMER_ENABLED_SETTING
argument_list|,
name|IndexSettings
operator|.
name|INDEX_REFRESH_INTERVAL_SETTING
argument_list|,
name|IndexSettings
operator|.
name|MAX_RESULT_WINDOW_SETTING
argument_list|,
name|IndexSettings
operator|.
name|INDEX_TRANSLOG_SYNC_INTERVAL_SETTING
argument_list|,
name|IndexSettings
operator|.
name|DEFAULT_FIELD_SETTING
argument_list|,
name|IndexSettings
operator|.
name|QUERY_STRING_LENIENT_SETTING
argument_list|,
name|IndexSettings
operator|.
name|ALLOW_UNMAPPED
argument_list|,
name|IndexSettings
operator|.
name|INDEX_CHECK_ON_STARTUP
argument_list|,
name|ShardsLimitAllocationDecider
operator|.
name|INDEX_TOTAL_SHARDS_PER_NODE_SETTING
argument_list|,
name|IndexSettings
operator|.
name|INDEX_GC_DELETES_SETTING
argument_list|,
name|IndicesRequestCache
operator|.
name|INDEX_CACHE_REQUEST_ENABLED_SETTING
argument_list|,
name|UnassignedInfo
operator|.
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_REBALANCE_ENABLE_SETTING
argument_list|,
name|EnableAllocationDecider
operator|.
name|INDEX_ROUTING_ALLOCATION_ENABLE_SETTING
argument_list|,
name|IndexSettings
operator|.
name|INDEX_TRANSLOG_FLUSH_THRESHOLD_SIZE_SETTING
argument_list|,
name|IndexFieldDataService
operator|.
name|INDEX_FIELDDATA_CACHE_KEY
argument_list|,
name|FieldMapper
operator|.
name|IGNORE_MALFORMED_SETTING
argument_list|,
name|FieldMapper
operator|.
name|COERCE_SETTING
argument_list|,
name|Store
operator|.
name|INDEX_STORE_STATS_REFRESH_INTERVAL_SETTING
argument_list|,
name|PercolatorQueriesRegistry
operator|.
name|INDEX_MAP_UNMAPPED_FIELDS_AS_STRING_SETTING
argument_list|,
name|MapperService
operator|.
name|INDEX_MAPPER_DYNAMIC_SETTING
argument_list|,
name|MapperService
operator|.
name|INDEX_MAPPING_NESTED_FIELDS_LIMIT_SETTING
argument_list|,
name|BitsetFilterCache
operator|.
name|INDEX_LOAD_RANDOM_ACCESS_FILTERS_EAGERLY_SETTING
argument_list|,
name|IndexModule
operator|.
name|INDEX_STORE_TYPE_SETTING
argument_list|,
name|IndexModule
operator|.
name|INDEX_QUERY_CACHE_TYPE_SETTING
argument_list|,
name|IndexModule
operator|.
name|INDEX_QUERY_CACHE_EVERYTHING_SETTING
argument_list|,
name|PrimaryShardAllocator
operator|.
name|INDEX_RECOVERY_INITIAL_SHARDS_SETTING
argument_list|,
name|FsDirectoryService
operator|.
name|INDEX_LOCK_FACTOR_SETTING
argument_list|,
name|EngineConfig
operator|.
name|INDEX_CODEC_SETTING
argument_list|,
name|IndexWarmer
operator|.
name|INDEX_NORMS_LOADING_SETTING
argument_list|,
comment|// validate that built-in similarities don't get redefined
name|Setting
operator|.
name|groupSetting
argument_list|(
literal|"index.similarity."
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|INDEX
argument_list|,
parameter_list|(
name|s
parameter_list|)
lambda|->
block|{
name|boolean
name|valid
init|=
literal|true
decl_stmt|;
assert|assert
operator|(
name|s
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
operator|>
literal|1
operator|)
assert|;
name|String
name|similarityName
init|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|indexOf
argument_list|(
literal|"."
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|SimilarityService
operator|.
name|BUILT_IN
operator|.
name|keySet
argument_list|()
operator|.
name|contains
argument_list|(
name|similarityName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Cannot redefine built-in Similarity ["
operator|+
name|similarityName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|valid
return|;
block|}
argument_list|)
argument_list|,
comment|// this allows similarity settings to be passed
name|Setting
operator|.
name|groupSetting
argument_list|(
literal|"index.analysis."
argument_list|,
literal|false
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|INDEX
argument_list|)
comment|// this allows analysis settings to be passed
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_SCOPED_SETTINGS
specifier|public
specifier|static
specifier|final
name|IndexScopedSettings
name|DEFAULT_SCOPED_SETTINGS
init|=
operator|new
name|IndexScopedSettings
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|,
name|IndexScopedSettings
operator|.
name|BUILT_IN_INDEX_SETTINGS
argument_list|)
decl_stmt|;
DECL|method|IndexScopedSettings
specifier|public
name|IndexScopedSettings
parameter_list|(
name|Settings
name|settings
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
name|settings
argument_list|,
name|settingsSet
argument_list|,
name|Setting
operator|.
name|Scope
operator|.
name|INDEX
argument_list|)
expr_stmt|;
block|}
DECL|method|IndexScopedSettings
specifier|private
name|IndexScopedSettings
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndexScopedSettings
name|other
parameter_list|,
name|IndexMetaData
name|metaData
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|metaData
operator|.
name|getSettings
argument_list|()
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
DECL|method|copy
specifier|public
name|IndexScopedSettings
name|copy
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|IndexMetaData
name|metaData
parameter_list|)
block|{
return|return
operator|new
name|IndexScopedSettings
argument_list|(
name|settings
argument_list|,
name|this
argument_list|,
name|metaData
argument_list|)
return|;
block|}
DECL|method|isPrivateSetting
specifier|public
name|boolean
name|isPrivateSetting
parameter_list|(
name|String
name|key
parameter_list|)
block|{
switch|switch
condition|(
name|key
condition|)
block|{
case|case
name|IndexMetaData
operator|.
name|SETTING_CREATION_DATE
case|:
case|case
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
case|:
case|case
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
case|:
case|case
name|IndexMetaData
operator|.
name|SETTING_VERSION_UPGRADED
case|:
case|case
name|MergePolicyConfig
operator|.
name|INDEX_MERGE_ENABLED
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

