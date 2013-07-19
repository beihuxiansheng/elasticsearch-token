begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.integration.indices.store
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|integration
operator|.
name|indices
operator|.
name|store
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
name|node
operator|.
name|internal
operator|.
name|InternalNode
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
name|integration
operator|.
name|AbstractNodesTests
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
DECL|class|IndicesStoreTests
specifier|public
class|class
name|IndicesStoreTests
extends|extends
name|AbstractNodesTests
block|{
annotation|@
name|Override
DECL|method|getClassDefaultSettings
specifier|protected
name|Settings
name|getClassDefaultSettings
parameter_list|()
block|{
comment|// The default (none) gateway cleans the shards on closing
return|return
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
return|;
block|}
annotation|@
name|Override
DECL|method|beforeClass
specifier|protected
name|void
name|beforeClass
parameter_list|()
block|{
name|startNode
argument_list|(
literal|"server1"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|client
specifier|public
name|Client
name|client
parameter_list|()
block|{
return|return
name|client
argument_list|(
literal|"server1"
argument_list|)
return|;
block|}
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
name|prepareDelete
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
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// Ignore
block|}
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
literal|"--> making sure that shard and it's replica are allocated on server1 and server2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
literal|"server1"
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
literal|"server2"
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
name|startNode
argument_list|(
literal|"server3"
argument_list|)
expr_stmt|;
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
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"server3"
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
literal|"server2"
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
literal|"--> stopping node server2"
argument_list|)
expr_stmt|;
name|closeNode
argument_list|(
literal|"server2"
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
literal|"--> making sure that shard and it's replica exist on server1, server2 and server3"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
literal|"server1"
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
literal|"server3"
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
literal|"--> starting node server2"
argument_list|)
expr_stmt|;
name|startNode
argument_list|(
literal|"server2"
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
argument_list|(
literal|"server2"
argument_list|)
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
literal|"--> making sure that shard and it's replica are allocated on server1 and server3 but not on server2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|shardDirectory
argument_list|(
literal|"server1"
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
literal|"server3"
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
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|"server2"
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
name|InternalNode
name|node
init|=
operator|(
operator|(
name|InternalNode
operator|)
name|node
argument_list|(
name|server
argument_list|)
operator|)
decl_stmt|;
name|NodeEnvironment
name|env
init|=
name|node
operator|.
name|injector
argument_list|()
operator|.
name|getInstance
argument_list|(
name|NodeEnvironment
operator|.
name|class
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
name|TimeValue
name|timeout
parameter_list|,
name|String
name|server
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shard
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|boolean
name|shardExists
decl_stmt|;
do|do
block|{
name|shardExists
operator|=
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
expr_stmt|;
block|}
do|while
condition|(
name|shardExists
operator|&&
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|<
name|timeout
operator|.
name|millis
argument_list|()
condition|)
do|;
return|return
name|shardExists
return|;
block|}
block|}
end_class

end_unit

