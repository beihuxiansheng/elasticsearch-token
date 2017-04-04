begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.rest.action.cat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|action
operator|.
name|cat
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|indices
operator|.
name|stats
operator|.
name|CommonStats
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
name|stats
operator|.
name|IndicesStatsResponse
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
name|stats
operator|.
name|IndicesStatsTests
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
name|stats
operator|.
name|ShardStats
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
name|RecoverySource
operator|.
name|PeerRecoverySource
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
name|RecoverySource
operator|.
name|StoreRecoverySource
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
name|UnassignedInfo
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
name|Table
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
name|UUIDs
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
name|unit
operator|.
name|TimeValue
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
name|Index
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
name|query
operator|.
name|QueryCacheStats
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
name|request
operator|.
name|RequestCacheStats
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
name|SegmentsStats
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
name|FieldDataStats
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
name|flush
operator|.
name|FlushStats
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
name|get
operator|.
name|GetStats
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
name|MergeStats
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
name|refresh
operator|.
name|RefreshStats
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
name|stats
operator|.
name|SearchStats
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
name|DocsStats
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
name|IndexingStats
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
name|index
operator|.
name|shard
operator|.
name|ShardPath
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
name|StoreStats
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
name|warmer
operator|.
name|WarmerStats
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
name|RestController
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
name|suggest
operator|.
name|completion
operator|.
name|CompletionStats
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
name|ESTestCase
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
name|rest
operator|.
name|FakeRestRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|emptyList
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

begin_comment
comment|/**  * Tests for {@link RestIndicesAction}  */
end_comment

begin_class
DECL|class|RestIndicesActionTests
specifier|public
class|class
name|RestIndicesActionTests
extends|extends
name|ESTestCase
block|{
DECL|method|testBuildTable
specifier|public
name|void
name|testBuildTable
parameter_list|()
block|{
specifier|final
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
decl_stmt|;
specifier|final
name|RestController
name|restController
init|=
operator|new
name|RestController
argument_list|(
name|settings
argument_list|,
name|Collections
operator|.
name|emptySet
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RestIndicesAction
name|action
init|=
operator|new
name|RestIndicesAction
argument_list|(
name|settings
argument_list|,
name|restController
argument_list|,
operator|new
name|IndexNameExpressionResolver
argument_list|(
name|settings
argument_list|)
argument_list|)
decl_stmt|;
comment|// build a (semi-)random table
specifier|final
name|int
name|numIndices
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|Index
index|[]
name|indices
init|=
operator|new
name|Index
index|[
name|numIndices
index|]
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
name|numIndices
condition|;
name|i
operator|++
control|)
block|{
name|indices
index|[
name|i
index|]
operator|=
operator|new
name|Index
argument_list|(
name|randomAlphaOfLength
argument_list|(
literal|5
argument_list|)
argument_list|,
name|UUIDs
operator|.
name|randomBase64UUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|MetaData
operator|.
name|Builder
name|metaDataBuilder
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Index
name|index
range|:
name|indices
control|)
block|{
name|metaDataBuilder
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|builder
argument_list|(
name|index
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|settings
argument_list|(
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_VERSION_CREATED
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
operator|.
name|put
argument_list|(
name|IndexMetaData
operator|.
name|SETTING_INDEX_UUID
argument_list|,
name|index
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|)
operator|.
name|creationDate
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
operator|.
name|numberOfShards
argument_list|(
literal|1
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|1
argument_list|)
operator|.
name|state
argument_list|(
name|IndexMetaData
operator|.
name|State
operator|.
name|OPEN
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|MetaData
name|metaData
init|=
name|metaDataBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|CLUSTER_NAME_SETTING
operator|.
name|getDefault
argument_list|(
name|Settings
operator|.
name|EMPTY
argument_list|)
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|String
index|[]
name|indicesStr
init|=
operator|new
name|String
index|[
name|indices
operator|.
name|length
index|]
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
name|indices
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indicesStr
index|[
name|i
index|]
operator|=
name|indices
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
specifier|final
name|ClusterHealthResponse
name|clusterHealth
init|=
operator|new
name|ClusterHealthResponse
argument_list|(
name|clusterState
operator|.
name|getClusterName
argument_list|()
operator|.
name|value
argument_list|()
argument_list|,
name|indicesStr
argument_list|,
name|clusterState
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|1000L
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|table
init|=
name|action
operator|.
name|buildTable
argument_list|(
operator|new
name|FakeRestRequest
argument_list|()
argument_list|,
name|indices
argument_list|,
name|clusterHealth
argument_list|,
name|randomIndicesStatsResponse
argument_list|(
name|indices
argument_list|)
argument_list|,
name|metaData
argument_list|)
decl_stmt|;
comment|// now, verify the table is correct
name|int
name|count
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|headers
init|=
name|table
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"health"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"status"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|headers
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"uuid"
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
argument_list|>
name|rows
init|=
name|table
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|rows
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|indices
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: more to verify (e.g. randomize cluster health, num primaries, num replicas, etc)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rows
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|count
operator|=
literal|0
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Table
operator|.
name|Cell
argument_list|>
name|row
init|=
name|rows
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"red*"
argument_list|)
argument_list|)
expr_stmt|;
comment|// all are red because cluster state doesn't have routing entries
name|assertThat
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
literal|"open"
argument_list|)
argument_list|)
expr_stmt|;
comment|// all are OPEN for now
name|assertThat
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|indices
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|row
operator|.
name|get
argument_list|(
name|count
operator|++
argument_list|)
operator|.
name|value
argument_list|,
name|equalTo
argument_list|(
name|indices
index|[
name|i
index|]
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|randomIndicesStatsResponse
specifier|private
name|IndicesStatsResponse
name|randomIndicesStatsResponse
parameter_list|(
specifier|final
name|Index
index|[]
name|indices
parameter_list|)
block|{
name|List
argument_list|<
name|ShardStats
argument_list|>
name|shardStats
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Index
name|index
range|:
name|indices
control|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|ShardId
name|shardId
init|=
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
name|createTempDir
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"indices"
argument_list|)
operator|.
name|resolve
argument_list|(
name|index
operator|.
name|getUUID
argument_list|()
argument_list|)
operator|.
name|resolve
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|ShardRouting
name|shardRouting
init|=
name|ShardRouting
operator|.
name|newUnassigned
argument_list|(
name|shardId
argument_list|,
name|i
operator|==
literal|0
argument_list|,
name|i
operator|==
literal|0
condition|?
name|StoreRecoverySource
operator|.
name|EMPTY_STORE_INSTANCE
else|:
name|PeerRecoverySource
operator|.
name|INSTANCE
argument_list|,
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|shardRouting
operator|=
name|shardRouting
operator|.
name|initialize
argument_list|(
literal|"node-0"
argument_list|,
literal|null
argument_list|,
name|ShardRouting
operator|.
name|UNAVAILABLE_EXPECTED_SHARD_SIZE
argument_list|)
expr_stmt|;
name|shardRouting
operator|=
name|shardRouting
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|CommonStats
name|stats
init|=
operator|new
name|CommonStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|fieldData
operator|=
operator|new
name|FieldDataStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|queryCache
operator|=
operator|new
name|QueryCacheStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|docs
operator|=
operator|new
name|DocsStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|store
operator|=
operator|new
name|StoreStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|indexing
operator|=
operator|new
name|IndexingStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|search
operator|=
operator|new
name|SearchStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|segments
operator|=
operator|new
name|SegmentsStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|merge
operator|=
operator|new
name|MergeStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|refresh
operator|=
operator|new
name|RefreshStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|completion
operator|=
operator|new
name|CompletionStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|requestCache
operator|=
operator|new
name|RequestCacheStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|get
operator|=
operator|new
name|GetStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|flush
operator|=
operator|new
name|FlushStats
argument_list|()
expr_stmt|;
name|stats
operator|.
name|warmer
operator|=
operator|new
name|WarmerStats
argument_list|()
expr_stmt|;
name|shardStats
operator|.
name|add
argument_list|(
operator|new
name|ShardStats
argument_list|(
name|shardRouting
argument_list|,
operator|new
name|ShardPath
argument_list|(
literal|false
argument_list|,
name|path
argument_list|,
name|path
argument_list|,
name|shardId
argument_list|)
argument_list|,
name|stats
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|IndicesStatsTests
operator|.
name|newIndicesStatsResponse
argument_list|(
name|shardStats
operator|.
name|toArray
argument_list|(
operator|new
name|ShardStats
index|[
name|shardStats
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|shardStats
operator|.
name|size
argument_list|()
argument_list|,
name|shardStats
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|,
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

