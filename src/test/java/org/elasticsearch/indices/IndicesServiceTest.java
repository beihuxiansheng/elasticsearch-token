begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|LockObtainFailedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchIllegalStateException
import|;
end_import

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
name|cluster
operator|.
name|health
operator|.
name|ClusterHealthStatus
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
name|Requests
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
name|ClusterService
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
name|MetaData
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
name|GatewayMetaState
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
name|test
operator|.
name|ElasticsearchSingleNodeTest
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|concurrent
operator|.
name|TimeUnit
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
name|assertHitCount
import|;
end_import

begin_class
DECL|class|IndicesServiceTest
specifier|public
class|class
name|IndicesServiceTest
extends|extends
name|ElasticsearchSingleNodeTest
block|{
DECL|method|getIndicesService
specifier|public
name|IndicesService
name|getIndicesService
parameter_list|()
block|{
return|return
name|getInstanceFromNode
argument_list|(
name|IndicesService
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|resetNodeAfterTest
specifier|protected
name|boolean
name|resetNodeAfterTest
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|testCanDeleteShardContent
specifier|public
name|void
name|testCanDeleteShardContent
parameter_list|()
block|{
name|IndicesService
name|indicesService
init|=
name|getIndicesService
argument_list|()
decl_stmt|;
name|IndexMetaData
name|meta
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settings
argument_list|(
name|Version
operator|.
name|CURRENT
argument_list|)
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
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
literal|"no shard location"
argument_list|,
name|indicesService
operator|.
name|canDeleteShardContent
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|IndexService
name|test
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasShard
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"shard is allocated"
argument_list|,
name|indicesService
operator|.
name|canDeleteShardContent
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|meta
argument_list|)
argument_list|)
expr_stmt|;
name|test
operator|.
name|removeShard
argument_list|(
literal|0
argument_list|,
literal|"boom"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"shard is removed"
argument_list|,
name|indicesService
operator|.
name|canDeleteShardContent
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|meta
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testDeleteIndexStore
specifier|public
name|void
name|testDeleteIndexStore
parameter_list|()
throws|throws
name|Exception
block|{
name|IndicesService
name|indicesService
init|=
name|getIndicesService
argument_list|()
decl_stmt|;
name|IndexService
name|test
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|ClusterService
name|clusterService
init|=
name|getInstanceFromNode
argument_list|(
name|ClusterService
operator|.
name|class
argument_list|)
decl_stmt|;
name|IndexMetaData
name|firstMetaData
init|=
name|clusterService
operator|.
name|state
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
name|assertTrue
argument_list|(
name|test
operator|.
name|hasShard
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|indicesService
operator|.
name|deleteIndexStore
argument_list|(
literal|"boom"
argument_list|,
name|firstMetaData
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalStateException
name|ex
parameter_list|)
block|{
comment|// all good
block|}
name|GatewayMetaState
name|gwMetaState
init|=
name|getInstanceFromNode
argument_list|(
name|GatewayMetaState
operator|.
name|class
argument_list|)
decl_stmt|;
name|MetaData
name|meta
init|=
name|gwMetaState
operator|.
name|loadMetaState
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareDelete
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|meta
operator|=
name|gwMetaState
operator|.
name|loadMetaState
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|meta
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"field"
argument_list|,
literal|"value"
argument_list|)
operator|.
name|setRefresh
argument_list|(
literal|true
argument_list|)
operator|.
name|get
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
name|prepareFlush
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
name|assertHitCount
argument_list|(
name|client
argument_list|()
operator|.
name|prepareSearch
argument_list|(
literal|"test"
argument_list|)
operator|.
name|get
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|IndexMetaData
name|secondMetaData
init|=
name|clusterService
operator|.
name|state
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
name|assertAcked
argument_list|(
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
argument_list|)
expr_stmt|;
name|NodeEnvironment
name|nodeEnv
init|=
name|getInstanceFromNode
argument_list|(
name|NodeEnvironment
operator|.
name|class
argument_list|)
decl_stmt|;
name|Path
index|[]
name|paths
init|=
name|nodeEnv
operator|.
name|shardDataPaths
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|getMetaData
argument_list|()
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|indicesService
operator|.
name|deleteIndexStore
argument_list|(
literal|"boom"
argument_list|,
name|secondMetaData
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalStateException
name|ex
parameter_list|)
block|{
comment|// all good
block|}
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// now delete the old one and make sure we resolve against the name
try|try
block|{
name|indicesService
operator|.
name|deleteIndexStore
argument_list|(
literal|"boom"
argument_list|,
name|firstMetaData
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ElasticsearchIllegalStateException
name|ex
parameter_list|)
block|{
comment|// all good
block|}
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
name|ensureGreen
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
DECL|method|testPendingTasks
specifier|public
name|void
name|testPendingTasks
parameter_list|()
throws|throws
name|IOException
block|{
name|IndicesService
name|indicesService
init|=
name|getIndicesService
argument_list|()
decl_stmt|;
name|IndexService
name|test
init|=
name|createIndex
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|NodeEnvironment
name|nodeEnc
init|=
name|getInstanceFromNode
argument_list|(
name|NodeEnvironment
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|test
operator|.
name|hasShard
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|Path
index|[]
name|paths
init|=
name|nodeEnc
operator|.
name|shardDataPaths
argument_list|(
operator|new
name|ShardId
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|test
operator|.
name|getIndexSettings
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|indicesService
operator|.
name|processPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
operator|new
name|TimeValue
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"can't get lock"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockObtainFailedException
name|ex
parameter_list|)
block|{          }
for|for
control|(
name|Path
name|p
range|:
name|paths
control|)
block|{
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|indicesService
operator|.
name|addPendingDelete
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
operator|new
name|ShardId
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|test
operator|.
name|getIndexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|assertAcked
argument_list|(
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
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|paths
control|)
block|{
name|assertTrue
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|indicesService
operator|.
name|numPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// shard lock released... we can now delete
name|indicesService
operator|.
name|processPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
operator|new
name|TimeValue
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indicesService
operator|.
name|numPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|paths
control|)
block|{
name|assertFalse
argument_list|(
name|Files
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|indicesService
operator|.
name|addPendingDelete
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
operator|new
name|ShardId
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
literal|0
argument_list|)
argument_list|,
name|test
operator|.
name|getIndexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|indicesService
operator|.
name|addPendingDelete
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
operator|new
name|ShardId
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
literal|1
argument_list|)
argument_list|,
name|test
operator|.
name|getIndexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|indicesService
operator|.
name|addPendingDelete
argument_list|(
operator|new
name|Index
argument_list|(
literal|"bogus"
argument_list|)
argument_list|,
operator|new
name|ShardId
argument_list|(
literal|"bogus"
argument_list|,
literal|1
argument_list|)
argument_list|,
name|test
operator|.
name|getIndexSettings
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indicesService
operator|.
name|numPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// shard lock released... we can now delete
name|indicesService
operator|.
name|processPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|,
operator|new
name|TimeValue
argument_list|(
literal|0
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|indicesService
operator|.
name|numPendingDeletes
argument_list|(
name|test
operator|.
name|index
argument_list|()
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|assertAcked
argument_list|(
name|client
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|indices
argument_list|()
operator|.
name|prepareOpen
argument_list|(
literal|"test"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

