begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|store
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Predicate
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
name|NodeEnvironment
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
name|ElasticsearchIntegrationTest
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
name|ElasticsearchIntegrationTest
operator|.
name|Scope
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
name|TestCluster
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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|clusterHealthRequest
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Requests
operator|.
name|createIndexRequest
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
name|equalTo
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

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
DECL|class|IndicesStoreTests
specifier|public
class|class
name|IndicesStoreTests
extends|extends
name|ElasticsearchIntegrationTest
block|{
DECL|field|SETTINGS
specifier|private
specifier|static
specifier|final
name|Settings
name|SETTINGS
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"gateway.type"
argument_list|,
literal|"local"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|shardsCleanup
specifier|public
name|void
name|shardsCleanup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|node_1
init|=
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|SETTINGS
argument_list|)
decl_stmt|;
specifier|final
name|String
name|node_2
init|=
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|SETTINGS
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> creating index [test] with one shard and on replica"
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
name|create
argument_list|(
name|createIndexRequest
argument_list|(
literal|"test"
argument_list|)
operator|.
name|settings
argument_list|(
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"index.numberOfReplicas"
argument_list|,
literal|1
argument_list|)
operator|.
name|put
argument_list|(
literal|"index.numberOfShards"
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> running cluster_health"
argument_list|)
expr_stmt|;
name|ClusterHealthResponse
name|clusterHealth
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
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
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
name|logger
operator|.
name|info
argument_list|(
literal|"--> done cluster_health, status "
operator|+
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> making sure that shard and its replica are allocated on node_1 and node_2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
name|node_1
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
name|node_2
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting node server3"
argument_list|)
expr_stmt|;
name|String
name|node_3
init|=
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|SETTINGS
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> making sure that shard is not allocated on server3"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|waitForShardDeletion
argument_list|(
name|node_3
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|File
name|server2Shard
init|=
name|shardDirectory
argument_list|(
name|node_2
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> stopping node node_2"
argument_list|)
expr_stmt|;
name|cluster
argument_list|()
operator|.
name|stopRandomNode
argument_list|(
name|TestCluster
operator|.
name|nameFilter
argument_list|(
name|node_2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|server2Shard
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> running cluster_health"
argument_list|)
expr_stmt|;
name|clusterHealth
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
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
operator|.
name|waitForNodes
argument_list|(
literal|"2"
argument_list|)
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
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
name|logger
operator|.
name|info
argument_list|(
literal|"--> done cluster_health, status "
operator|+
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> making sure that shard and its replica exist on server1, server2 and server3"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
name|node_1
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|server2Shard
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
name|node_3
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> starting node node_4"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|node_4
init|=
name|cluster
argument_list|()
operator|.
name|startNode
argument_list|(
name|SETTINGS
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> running cluster_health"
argument_list|)
expr_stmt|;
name|clusterHealth
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
name|health
argument_list|(
name|clusterHealthRequest
argument_list|()
operator|.
name|waitForGreenStatus
argument_list|()
argument_list|)
operator|.
name|actionGet
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
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
name|logger
operator|.
name|info
argument_list|(
literal|"--> done cluster_health, status "
operator|+
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"--> making sure that shard and its replica are allocated on server1 and server3 but not on server2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
name|node_1
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
name|node_3
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|waitForShardDeletion
argument_list|(
name|node_4
argument_list|,
literal|"test"
argument_list|,
literal|0
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|shardDirectory
specifier|private
name|File
name|shardDirectory
parameter_list|(
name|String
name|server
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shard
parameter_list|)
block|{
name|NodeEnvironment
name|env
init|=
name|cluster
argument_list|()
operator|.
name|getInstance
argument_list|(
name|NodeEnvironment
operator|.
name|class
argument_list|,
name|server
argument_list|)
decl_stmt|;
return|return
name|env
operator|.
name|shardLocations
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shard
argument_list|)
argument_list|)
index|[
literal|0
index|]
return|;
block|}
DECL|method|waitForShardDeletion
specifier|private
name|boolean
name|waitForShardDeletion
parameter_list|(
specifier|final
name|String
name|server
parameter_list|,
specifier|final
name|String
name|index
parameter_list|,
specifier|final
name|int
name|shard
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|boolean
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|!
name|shardDirectory
argument_list|(
name|server
argument_list|,
name|index
argument_list|,
name|shard
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|shardDirectory
argument_list|(
name|server
argument_list|,
name|index
argument_list|,
name|shard
argument_list|)
operator|.
name|exists
argument_list|()
return|;
block|}
block|}
end_class

end_unit

