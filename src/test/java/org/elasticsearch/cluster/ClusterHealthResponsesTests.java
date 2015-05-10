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
name|ElasticsearchException
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
operator|.
name|ClusterIndexHealth
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
name|ClusterShardHealth
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
name|IndicesOptions
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
name|cluster
operator|.
name|routing
operator|.
name|*
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
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
name|IOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|empty
import|;
end_import

begin_class
DECL|class|ClusterHealthResponsesTests
specifier|public
class|class
name|ClusterHealthResponsesTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|assertIndexHealth
specifier|private
name|void
name|assertIndexHealth
parameter_list|(
name|ClusterIndexHealth
name|indexHealth
parameter_list|,
name|ShardCounter
name|counter
parameter_list|,
name|IndexMetaData
name|indexMetaData
parameter_list|)
block|{
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|status
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getNumberOfShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|indexMetaData
operator|.
name|getNumberOfShards
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getNumberOfReplicas
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|indexMetaData
operator|.
name|getNumberOfReplicas
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getActiveShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|active
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getRelocatingShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|relocating
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getInitializingShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|initializing
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|unassigned
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getShards
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|indexMetaData
operator|.
name|getNumberOfShards
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|totalShards
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ClusterShardHealth
name|shardHealth
range|:
name|indexHealth
operator|.
name|getShards
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|totalShards
operator|+=
name|shardHealth
operator|.
name|getActiveShards
argument_list|()
operator|+
name|shardHealth
operator|.
name|getInitializingShards
argument_list|()
operator|+
name|shardHealth
operator|.
name|getUnassignedShards
argument_list|()
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|totalShards
argument_list|,
name|equalTo
argument_list|(
name|indexMetaData
operator|.
name|getNumberOfShards
argument_list|()
operator|*
operator|(
literal|1
operator|+
name|indexMetaData
operator|.
name|getNumberOfReplicas
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|ShardCounter
specifier|protected
class|class
name|ShardCounter
block|{
DECL|field|active
specifier|public
name|int
name|active
decl_stmt|;
DECL|field|relocating
specifier|public
name|int
name|relocating
decl_stmt|;
DECL|field|initializing
specifier|public
name|int
name|initializing
decl_stmt|;
DECL|field|unassigned
specifier|public
name|int
name|unassigned
decl_stmt|;
DECL|field|primaryActive
specifier|public
name|int
name|primaryActive
decl_stmt|;
DECL|field|primaryInactive
specifier|public
name|int
name|primaryInactive
decl_stmt|;
DECL|method|status
specifier|public
name|ClusterHealthStatus
name|status
parameter_list|()
block|{
if|if
condition|(
name|primaryInactive
operator|>
literal|0
condition|)
block|{
return|return
name|ClusterHealthStatus
operator|.
name|RED
return|;
block|}
if|if
condition|(
name|unassigned
operator|>
literal|0
operator|||
name|initializing
operator|>
literal|0
condition|)
block|{
return|return
name|ClusterHealthStatus
operator|.
name|YELLOW
return|;
block|}
return|return
name|ClusterHealthStatus
operator|.
name|GREEN
return|;
block|}
DECL|method|update
specifier|public
name|void
name|update
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|active
argument_list|()
condition|)
block|{
name|active
operator|++
expr_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
name|primaryActive
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|shardRouting
operator|.
name|relocating
argument_list|()
condition|)
block|{
name|relocating
operator|++
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
name|primaryInactive
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|shardRouting
operator|.
name|initializing
argument_list|()
condition|)
block|{
name|initializing
operator|++
expr_stmt|;
block|}
else|else
block|{
name|unassigned
operator|++
expr_stmt|;
block|}
block|}
block|}
DECL|field|node_id
specifier|static
name|int
name|node_id
init|=
literal|1
decl_stmt|;
DECL|method|genShardRouting
specifier|private
name|ImmutableShardRouting
name|genShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|boolean
name|primary
parameter_list|)
block|{
name|ShardRoutingState
name|state
decl_stmt|;
name|int
name|i
init|=
name|randomInt
argument_list|(
literal|40
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|5
condition|)
block|{
name|state
operator|=
name|ShardRoutingState
operator|.
name|STARTED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|>
literal|3
condition|)
block|{
name|state
operator|=
name|ShardRoutingState
operator|.
name|RELOCATING
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|>
literal|1
condition|)
block|{
name|state
operator|=
name|ShardRoutingState
operator|.
name|INITIALIZING
expr_stmt|;
block|}
else|else
block|{
name|state
operator|=
name|ShardRoutingState
operator|.
name|UNASSIGNED
expr_stmt|;
block|}
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|UNASSIGNED
case|:
return|return
operator|new
name|MutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|null
argument_list|,
name|primary
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
literal|1
argument_list|)
return|;
case|case
name|STARTED
case|:
return|return
operator|new
name|MutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|"node_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|node_id
operator|++
argument_list|)
argument_list|,
name|primary
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|,
literal|1
argument_list|)
return|;
case|case
name|INITIALIZING
case|:
return|return
operator|new
name|MutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|"node_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|node_id
operator|++
argument_list|)
argument_list|,
name|primary
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
literal|1
argument_list|)
return|;
case|case
name|RELOCATING
case|:
return|return
operator|new
name|MutableShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|"node_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|node_id
operator|++
argument_list|)
argument_list|,
literal|"node_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|node_id
operator|++
argument_list|)
argument_list|,
name|primary
argument_list|,
name|ShardRoutingState
operator|.
name|RELOCATING
argument_list|,
literal|1
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"Unknown state: "
operator|+
name|state
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|genShardRoutingTable
specifier|private
name|IndexShardRoutingTable
name|genShardRoutingTable
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|int
name|replicas
parameter_list|,
name|ShardCounter
name|counter
parameter_list|)
block|{
name|IndexShardRoutingTable
operator|.
name|Builder
name|builder
init|=
operator|new
name|IndexShardRoutingTable
operator|.
name|Builder
argument_list|(
operator|new
name|ShardId
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ImmutableShardRouting
name|shardRouting
init|=
name|genShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|counter
operator|.
name|update
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addShard
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|replicas
operator|>
literal|0
condition|;
name|replicas
operator|--
control|)
block|{
name|shardRouting
operator|=
name|genShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|counter
operator|.
name|update
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addShard
argument_list|(
name|shardRouting
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|genIndexRoutingTable
name|IndexRoutingTable
name|genIndexRoutingTable
parameter_list|(
name|IndexMetaData
name|indexMetaData
parameter_list|,
name|ShardCounter
name|counter
parameter_list|)
block|{
name|IndexRoutingTable
operator|.
name|Builder
name|builder
init|=
name|IndexRoutingTable
operator|.
name|builder
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|shard
init|=
literal|0
init|;
name|shard
operator|<
name|indexMetaData
operator|.
name|numberOfShards
argument_list|()
condition|;
name|shard
operator|++
control|)
block|{
name|builder
operator|.
name|addIndexShard
argument_list|(
name|genShardRoutingTable
argument_list|(
name|indexMetaData
operator|.
name|index
argument_list|()
argument_list|,
name|shard
argument_list|,
name|indexMetaData
operator|.
name|getNumberOfReplicas
argument_list|()
argument_list|,
name|counter
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Test
DECL|method|testClusterIndexHealth
specifier|public
name|void
name|testClusterIndexHealth
parameter_list|()
block|{
name|int
name|numberOfShards
init|=
name|randomInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|numberOfReplicas
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test1"
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
name|numberOfShards
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
name|numberOfReplicas
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ShardCounter
name|counter
init|=
operator|new
name|ShardCounter
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|genIndexRoutingTable
argument_list|(
name|indexMetaData
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|ClusterIndexHealth
name|indexHealth
init|=
operator|new
name|ClusterIndexHealth
argument_list|(
name|indexMetaData
argument_list|,
name|indexRoutingTable
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"index status: {}, expected {}"
argument_list|,
name|indexHealth
operator|.
name|getStatus
argument_list|()
argument_list|,
name|counter
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|assertIndexHealth
argument_list|(
name|indexHealth
argument_list|,
name|counter
argument_list|,
name|indexMetaData
argument_list|)
expr_stmt|;
block|}
DECL|method|assertClusterHealth
specifier|private
name|void
name|assertClusterHealth
parameter_list|(
name|ClusterHealthResponse
name|clusterHealth
parameter_list|,
name|ShardCounter
name|counter
parameter_list|)
block|{
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|status
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getActiveShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|active
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getActivePrimaryShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|primaryActive
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getInitializingShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|initializing
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getRelocatingShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|relocating
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getUnassignedShards
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|counter
operator|.
name|unassigned
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClusterHealth
specifier|public
name|void
name|testClusterHealth
parameter_list|()
throws|throws
name|IOException
block|{
name|ShardCounter
name|counter
init|=
operator|new
name|ShardCounter
argument_list|()
decl_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTable
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|int
name|numberOfShards
init|=
name|randomInt
argument_list|(
literal|3
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|numberOfReplicas
init|=
name|randomInt
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|IndexMetaData
name|indexMetaData
init|=
name|IndexMetaData
operator|.
name|builder
argument_list|(
literal|"test_"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
name|numberOfShards
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
name|numberOfReplicas
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|genIndexRoutingTable
argument_list|(
name|indexMetaData
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|metaData
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|routingTable
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
block|}
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|int
name|pendingTasks
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|int
name|inFlight
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
literal|200
argument_list|)
decl_stmt|;
name|ClusterHealthResponse
name|clusterHealth
init|=
operator|new
name|ClusterHealthResponse
argument_list|(
literal|"bla"
argument_list|,
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|IndicesOptions
operator|.
name|strictExpand
argument_list|()
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|,
name|clusterState
argument_list|,
name|pendingTasks
argument_list|,
name|inFlight
argument_list|)
decl_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"cluster status: {}, expected {}"
argument_list|,
name|clusterHealth
operator|.
name|getStatus
argument_list|()
argument_list|,
name|counter
operator|.
name|status
argument_list|()
argument_list|)
expr_stmt|;
name|clusterHealth
operator|=
name|maybeSerialize
argument_list|(
name|clusterHealth
argument_list|)
expr_stmt|;
name|assertClusterHealth
argument_list|(
name|clusterHealth
argument_list|,
name|counter
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getNumberOfPendingTasks
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|pendingTasks
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getNumberOfInFlightFetch
argument_list|()
argument_list|,
name|Matchers
operator|.
name|equalTo
argument_list|(
name|inFlight
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|maybeSerialize
name|ClusterHealthResponse
name|maybeSerialize
parameter_list|(
name|ClusterHealthResponse
name|clusterHealth
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|clusterHealth
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|StreamInput
name|in
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|clusterHealth
operator|=
name|ClusterHealthResponse
operator|.
name|readResponseFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|clusterHealth
return|;
block|}
annotation|@
name|Test
DECL|method|testValidations
specifier|public
name|void
name|testValidations
parameter_list|()
throws|throws
name|IOException
block|{
name|IndexMetaData
name|indexMetaData
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
literal|2
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|2
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ShardCounter
name|counter
init|=
operator|new
name|ShardCounter
argument_list|()
decl_stmt|;
name|IndexRoutingTable
name|indexRoutingTable
init|=
name|genIndexRoutingTable
argument_list|(
name|indexMetaData
argument_list|,
name|counter
argument_list|)
decl_stmt|;
name|indexMetaData
operator|=
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
literal|2
argument_list|)
operator|.
name|numberOfReplicas
argument_list|(
literal|3
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|ClusterIndexHealth
name|indexHealth
init|=
operator|new
name|ClusterIndexHealth
argument_list|(
name|indexMetaData
argument_list|,
name|indexRoutingTable
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|indexHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|Matchers
operator|.
name|hasSize
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|RoutingTable
operator|.
name|Builder
name|routingTable
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
decl_stmt|;
name|MetaData
operator|.
name|Builder
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
decl_stmt|;
name|metaData
operator|.
name|put
argument_list|(
name|indexMetaData
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|routingTable
operator|.
name|add
argument_list|(
name|indexRoutingTable
argument_list|)
expr_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|ClusterName
operator|.
name|DEFAULT
argument_list|)
operator|.
name|metaData
argument_list|(
name|metaData
argument_list|)
operator|.
name|routingTable
argument_list|(
name|routingTable
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterHealthResponse
name|clusterHealth
init|=
operator|new
name|ClusterHealthResponse
argument_list|(
literal|"bla"
argument_list|,
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndices
argument_list|(
name|IndicesOptions
operator|.
name|strictExpand
argument_list|()
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|,
name|clusterState
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|clusterHealth
operator|=
name|maybeSerialize
argument_list|(
name|clusterHealth
argument_list|)
expr_stmt|;
comment|// currently we have no cluster level validation failures as index validation issues are reported per index.
name|assertThat
argument_list|(
name|clusterHealth
operator|.
name|getValidationFailures
argument_list|()
argument_list|,
name|Matchers
operator|.
name|hasSize
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

