begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.settings
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|settings
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|AppenderSkeleton
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|spi
operator|.
name|LoggingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalArgumentException
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
name|health
operator|.
name|ClusterHealthResponse
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
name|node
operator|.
name|stats
operator|.
name|NodeStats
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
name|node
operator|.
name|stats
operator|.
name|NodesStatsResponse
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
name|get
operator|.
name|GetSettingsResponse
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
name|common
operator|.
name|Priority
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
name|index
operator|.
name|engine
operator|.
name|VersionConflictEngineException
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
name|merge
operator|.
name|scheduler
operator|.
name|ConcurrentMergeSchedulerProvider
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
name|test
operator|.
name|ElasticsearchIntegrationTest
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertAcked
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|hamcrest
operator|.
name|ElasticsearchAssertions
operator|.
name|assertThrows
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
name|equalTo
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
name|nullValue
import|;
end_import

begin_class
DECL|class|UpdateSettingsTests
specifier|public
class|class
name|UpdateSettingsTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
annotation|@
name|Test
DECL|method|testOpenCloseUpdateSettings
specifier|public
name|void
name|testOpenCloseUpdateSettings
parameter_list|()
throws|throws
name|Exception
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
try|try
block|{
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
comment|// this one can change
operator|.
name|put
argument_list|(
literal|"index.cache.filter.type"
argument_list|,
literal|"none"
argument_list|)
comment|// this one can't
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalArgumentException
name|e
parameter_list|)
block|{
comment|// all is well
block|}
name|IndexMetaData
name|indexMetaData
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index.cache.filter.type"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// Now verify via dedicated get settings api:
name|GetSettingsResponse
name|getSettingsResponse
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getSettingsResponse
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getSettingsResponse
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.cache.filter.type"
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
operator|-
literal|1
argument_list|)
comment|// this one can change
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|indexMetaData
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now verify via dedicated get settings api:
name|getSettingsResponse
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getSettingsResponse
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"-1"
argument_list|)
argument_list|)
expr_stmt|;
comment|// now close the index, change the non dynamic setting, and see that it applies
comment|// Wait for the index to turn green before attempting to close it
name|ClusterHealthResponse
name|health
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareHealth
argument_list|()
operator|.
name|setTimeout
argument_list|(
literal|"30s"
argument_list|)
operator|.
name|setWaitForEvents
argument_list|(
name|Priority
operator|.
name|LANGUID
argument_list|)
operator|.
name|setWaitForGreenStatus
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|health
operator|.
name|isTimedOut
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareClose
argument_list|(
literal|"test"
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.refresh_interval"
argument_list|,
literal|"1s"
argument_list|)
comment|// this one can change
operator|.
name|put
argument_list|(
literal|"index.cache.filter.type"
argument_list|,
literal|"none"
argument_list|)
comment|// this one can't
argument_list|)
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|indexMetaData
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareState
argument_list|()
operator|.
name|execute
argument_list|()
operator|.
name|actionGet
argument_list|()
operator|.
name|getState
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexMetaData
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
literal|"index.cache.filter.type"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"none"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now verify via dedicated get settings api:
name|getSettingsResponse
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|getSettingsResponse
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.refresh_interval"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"1s"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|getSettingsResponse
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
literal|"index.cache.filter.type"
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"none"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEngineGCDeletesSetting
specifier|public
name|void
name|testEngineGCDeletesSetting
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|createIndex
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"f"
argument_list|,
literal|1
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// set version to 1
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// sets version to 2
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"f"
argument_list|,
literal|2
argument_list|)
operator|.
name|setVersion
argument_list|(
literal|2
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// delete is still in cache this should work& set version to 3
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.gc_deletes"
argument_list|,
literal|0
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|client
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// sets version to 4
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
comment|// wait for cache time to change TODO: this needs to be solved better. To be discussed.
name|assertThrows
argument_list|(
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|"1"
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"f"
argument_list|,
literal|3
argument_list|)
operator|.
name|setVersion
argument_list|(
literal|4
argument_list|)
argument_list|,
name|VersionConflictEngineException
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// delete is should not be in cache
block|}
comment|// #6626: make sure we can update throttle settings and the changes take effect
annotation|@
name|Test
annotation|@
name|Slow
DECL|method|testUpdateThrottleSettings
specifier|public
name|void
name|testUpdateThrottleSettings
parameter_list|()
block|{
comment|// No throttling at first, only 1 non-replicated shard, force lots of merging:
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE
argument_list|,
literal|"none"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|"0"
argument_list|)
operator|.
name|put
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_AT_ONCE
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_SEGMENTS_PER_TIER
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
name|ConcurrentMergeSchedulerProvider
operator|.
name|MAX_THREAD_COUNT
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
name|ConcurrentMergeSchedulerProvider
operator|.
name|MAX_MERGE_COUNT
argument_list|,
literal|"2"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|long
name|termUpto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// Provoke slowish merging by making many unique terms:
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|termUpto
operator|++
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|termUpto
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|%
literal|10
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
comment|// No merge IO throttling should have happened:
name|NodesStatsResponse
name|nodesStats
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeStats
name|stats
range|:
name|nodesStats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|assertThat
argument_list|(
name|stats
operator|.
name|getIndices
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|getThrottleTime
argument_list|()
operator|.
name|getMillis
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|0l
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Now updates settings to turn on merge throttling lowish rate
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE
argument_list|,
literal|"merge"
argument_list|)
operator|.
name|put
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
argument_list|,
literal|"1mb"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Make sure setting says it is in fact changed:
name|GetSettingsResponse
name|getSettingsResponse
init|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareGetSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|getSettingsResponse
operator|.
name|getSetting
argument_list|(
literal|"test"
argument_list|,
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|"merge"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Also make sure we see throttling kicking in:
name|boolean
name|done
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|done
operator|==
literal|false
condition|)
block|{
comment|// Provoke slowish merging by making many unique terms:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|termUpto
operator|++
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" some random text that keeps repeating over and over again hambone"
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|termUpto
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|%
literal|10
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|refresh
argument_list|()
expr_stmt|;
name|nodesStats
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeStats
name|stats
range|:
name|nodesStats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|long
name|throttleMillis
init|=
name|stats
operator|.
name|getIndices
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|getThrottleTime
argument_list|()
operator|.
name|getMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|throttleMillis
operator|>
literal|0
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// Now updates settings to disable merge throttling
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE
argument_list|,
literal|"none"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Optimize does a waitForMerges, which we must do to make sure all in-flight (throttled) merges finish:
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOptimize
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Record current throttling so far
name|long
name|sumThrottleTime
init|=
literal|0
decl_stmt|;
name|nodesStats
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeStats
name|stats
range|:
name|nodesStats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|sumThrottleTime
operator|+=
name|stats
operator|.
name|getIndices
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|getThrottleTime
argument_list|()
operator|.
name|getMillis
argument_list|()
expr_stmt|;
block|}
comment|// Make sure no further throttling happens:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// Provoke slowish merging by making many unique terms:
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|termUpto
operator|++
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|termUpto
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|%
literal|10
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
name|long
name|newSumThrottleTime
init|=
literal|0
decl_stmt|;
name|nodesStats
operator|=
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesStats
argument_list|()
operator|.
name|setIndices
argument_list|(
literal|true
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeStats
name|stats
range|:
name|nodesStats
operator|.
name|getNodes
argument_list|()
control|)
block|{
name|newSumThrottleTime
operator|+=
name|stats
operator|.
name|getIndices
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|getThrottleTime
argument_list|()
operator|.
name|getMillis
argument_list|()
expr_stmt|;
block|}
comment|// No additional merge IO throttling should have happened:
name|assertEquals
argument_list|(
name|sumThrottleTime
argument_list|,
name|newSumThrottleTime
argument_list|)
expr_stmt|;
block|}
DECL|class|MockAppender
specifier|private
specifier|static
class|class
name|MockAppender
extends|extends
name|AppenderSkeleton
block|{
DECL|field|sawIndexWriterMessage
specifier|public
name|boolean
name|sawIndexWriterMessage
decl_stmt|;
DECL|field|sawFlushDeletes
specifier|public
name|boolean
name|sawFlushDeletes
decl_stmt|;
DECL|field|sawMergeThreadPaused
specifier|public
name|boolean
name|sawMergeThreadPaused
decl_stmt|;
DECL|field|sawUpdateSetting
specifier|public
name|boolean
name|sawUpdateSetting
decl_stmt|;
annotation|@
name|Override
DECL|method|append
specifier|protected
name|void
name|append
parameter_list|(
name|LoggingEvent
name|event
parameter_list|)
block|{
name|String
name|message
init|=
name|event
operator|.
name|getMessage
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|==
name|Level
operator|.
name|TRACE
operator|&&
name|event
operator|.
name|getLoggerName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"lucene.iw"
argument_list|)
condition|)
block|{
name|sawFlushDeletes
operator||=
name|message
operator|.
name|contains
argument_list|(
literal|"IW: apply all deletes during flush"
argument_list|)
expr_stmt|;
name|sawMergeThreadPaused
operator||=
name|message
operator|.
name|contains
argument_list|(
literal|"CMS: pause thread"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|event
operator|.
name|getLevel
argument_list|()
operator|==
name|Level
operator|.
name|INFO
operator|&&
name|message
operator|.
name|contains
argument_list|(
literal|"updating [max_thread_count] from [10000] to [1]"
argument_list|)
condition|)
block|{
name|sawUpdateSetting
operator|=
literal|true
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|requiresLayout
specifier|public
name|boolean
name|requiresLayout
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{         }
block|}
comment|// #6882: make sure we can change index.merge.scheduler.max_thread_count live
annotation|@
name|Test
annotation|@
name|Slow
DECL|method|testUpdateMergeMaxThreadCount
specifier|public
name|void
name|testUpdateMergeMaxThreadCount
parameter_list|()
block|{
name|MockAppender
name|mockAppender
init|=
operator|new
name|MockAppender
argument_list|()
decl_stmt|;
name|Logger
name|rootLogger
init|=
name|Logger
operator|.
name|getRootLogger
argument_list|()
decl_stmt|;
name|Level
name|savedLevel
init|=
name|rootLogger
operator|.
name|getLevel
argument_list|()
decl_stmt|;
name|rootLogger
operator|.
name|addAppender
argument_list|(
name|mockAppender
argument_list|)
expr_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|Level
operator|.
name|TRACE
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Tons of merge threads allowed, only 1 non-replicated shard, force lots of merging, throttle so they fall behind:
name|assertAcked
argument_list|(
name|prepareCreate
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_TYPE
argument_list|,
literal|"merge"
argument_list|)
operator|.
name|put
argument_list|(
name|AbstractIndexStore
operator|.
name|INDEX_STORE_THROTTLE_MAX_BYTES_PER_SEC
argument_list|,
literal|"1mb"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_SHARDS
argument_list|,
literal|"1"
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_NUMBER_OF_REPLICAS
argument_list|,
literal|"0"
argument_list|)
operator|.
name|put
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_MAX_MERGE_AT_ONCE
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
name|TieredMergePolicyProvider
operator|.
name|INDEX_MERGE_POLICY_SEGMENTS_PER_TIER
argument_list|,
literal|"2"
argument_list|)
operator|.
name|put
argument_list|(
name|ConcurrentMergeSchedulerProvider
operator|.
name|MAX_THREAD_COUNT
argument_list|,
literal|"10000"
argument_list|)
operator|.
name|put
argument_list|(
name|ConcurrentMergeSchedulerProvider
operator|.
name|MAX_MERGE_COUNT
argument_list|,
literal|"10000"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|()
expr_stmt|;
name|long
name|termUpto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
comment|// Provoke slowish merging by making many unique terms:
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|termUpto
operator|++
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|termUpto
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|%
literal|10
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|mockAppender
operator|.
name|sawFlushDeletes
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|mockAppender
operator|.
name|sawMergeThreadPaused
argument_list|)
expr_stmt|;
name|mockAppender
operator|.
name|sawFlushDeletes
operator|=
literal|false
expr_stmt|;
name|mockAppender
operator|.
name|sawMergeThreadPaused
operator|=
literal|false
expr_stmt|;
name|assertFalse
argument_list|(
name|mockAppender
operator|.
name|sawUpdateSetting
argument_list|)
expr_stmt|;
comment|// Now make a live change to reduce allowed merge threads:
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareUpdateSettings
argument_list|(
literal|"test"
argument_list|)
operator|.
name|setSettings
argument_list|(
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|ConcurrentMergeSchedulerProvider
operator|.
name|MAX_THREAD_COUNT
argument_list|,
literal|"1"
argument_list|)
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
comment|// Make sure we log the change:
name|assertTrue
argument_list|(
name|mockAppender
operator|.
name|sawUpdateSetting
argument_list|)
expr_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|// Provoke slowish merging by making many unique terms:
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|termUpto
operator|++
argument_list|)
expr_stmt|;
block|}
name|client
argument_list|()
operator|.
name|prepareIndex
argument_list|(
literal|"test"
argument_list|,
literal|"type"
argument_list|,
literal|""
operator|+
name|termUpto
argument_list|)
operator|.
name|setSource
argument_list|(
literal|"field"
operator|+
operator|(
name|i
operator|%
literal|10
operator|)
argument_list|,
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|i
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|// This time we should see some merges were in fact paused:
if|if
condition|(
name|mockAppender
operator|.
name|sawMergeThreadPaused
condition|)
block|{
break|break;
block|}
name|i
operator|++
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|rootLogger
operator|.
name|removeAppender
argument_list|(
name|mockAppender
argument_list|)
expr_stmt|;
name|rootLogger
operator|.
name|setLevel
argument_list|(
name|savedLevel
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

