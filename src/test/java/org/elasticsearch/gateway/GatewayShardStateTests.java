begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|MutableShardRouting
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
name|RoutingNode
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
name|ShardRoutingState
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
DECL|class|GatewayShardStateTests
specifier|public
class|class
name|GatewayShardStateTests
extends|extends
name|ElasticsearchTestCase
block|{
DECL|method|testWriteShardState
specifier|public
name|void
name|testWriteShardState
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|NodeEnvironment
name|env
init|=
name|newNodeEnvironment
argument_list|()
init|)
block|{
name|GatewayShardsState
name|state
init|=
operator|new
name|GatewayShardsState
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|,
name|env
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|ShardId
name|id
init|=
operator|new
name|ShardId
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|long
name|version
init|=
name|between
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|2
argument_list|)
decl_stmt|;
name|boolean
name|primary
init|=
name|randomBoolean
argument_list|()
decl_stmt|;
name|ShardStateInfo
name|state1
init|=
operator|new
name|ShardStateInfo
argument_list|(
name|version
argument_list|,
name|primary
argument_list|)
decl_stmt|;
name|state
operator|.
name|maybeWriteShardState
argument_list|(
name|id
argument_list|,
name|state1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|ShardStateInfo
name|shardStateInfo
init|=
name|state
operator|.
name|loadShardInfo
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shardStateInfo
argument_list|,
name|state1
argument_list|)
expr_stmt|;
name|ShardStateInfo
name|state2
init|=
operator|new
name|ShardStateInfo
argument_list|(
name|version
argument_list|,
name|primary
argument_list|)
decl_stmt|;
name|state
operator|.
name|maybeWriteShardState
argument_list|(
name|id
argument_list|,
name|state2
argument_list|,
name|state1
argument_list|)
expr_stmt|;
name|shardStateInfo
operator|=
name|state
operator|.
name|loadShardInfo
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardStateInfo
argument_list|,
name|state1
argument_list|)
expr_stmt|;
name|ShardStateInfo
name|state3
init|=
operator|new
name|ShardStateInfo
argument_list|(
name|version
operator|+
literal|1
argument_list|,
name|primary
argument_list|)
decl_stmt|;
name|state
operator|.
name|maybeWriteShardState
argument_list|(
name|id
argument_list|,
name|state3
argument_list|,
name|state1
argument_list|)
expr_stmt|;
name|shardStateInfo
operator|=
name|state
operator|.
name|loadShardInfo
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardStateInfo
argument_list|,
name|state3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getCurrentState
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testPersistRoutingNode
specifier|public
name|void
name|testPersistRoutingNode
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
name|NodeEnvironment
name|env
init|=
name|newNodeEnvironment
argument_list|()
init|)
block|{
name|GatewayShardsState
name|state
init|=
operator|new
name|GatewayShardsState
argument_list|(
name|ImmutableSettings
operator|.
name|EMPTY
argument_list|,
name|env
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|numShards
init|=
name|between
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|shards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|active
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
name|numShards
condition|;
name|i
operator|++
control|)
block|{
name|long
name|version
init|=
name|between
argument_list|(
literal|1
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
operator|/
literal|2
argument_list|)
decl_stmt|;
name|ShardRoutingState
name|shardRoutingState
init|=
name|randomFrom
argument_list|(
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
name|ShardRoutingState
operator|.
name|RELOCATING
argument_list|,
name|ShardRoutingState
operator|.
name|STARTED
argument_list|)
decl_stmt|;
name|MutableShardRouting
name|mutableShardRouting
init|=
operator|new
name|MutableShardRouting
argument_list|(
literal|"idx"
argument_list|,
name|i
argument_list|,
literal|"foo"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|shardRoutingState
argument_list|,
name|version
argument_list|)
decl_stmt|;
if|if
condition|(
name|mutableShardRouting
operator|.
name|active
argument_list|()
condition|)
block|{
name|active
operator|.
name|add
argument_list|(
name|mutableShardRouting
argument_list|)
expr_stmt|;
block|}
name|shards
operator|.
name|add
argument_list|(
name|mutableShardRouting
argument_list|)
expr_stmt|;
block|}
name|RoutingNode
name|node
init|=
operator|new
name|RoutingNode
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|DiscoveryNode
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
name|shards
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|shardIdShardStateInfoMap
init|=
name|state
operator|.
name|persistRoutingNodeState
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shardIdShardStateInfoMap
operator|.
name|size
argument_list|()
argument_list|,
name|active
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|written
range|:
name|shardIdShardStateInfoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ShardStateInfo
name|shardStateInfo
init|=
name|state
operator|.
name|loadShardInfo
argument_list|(
name|written
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shardStateInfo
argument_list|,
name|written
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|assertNull
argument_list|(
name|state
operator|.
name|loadShardInfo
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"no_such_index"
argument_list|,
name|written
operator|.
name|getKey
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|state
operator|.
name|getCurrentState
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|state
operator|.
name|getCurrentState
argument_list|()
operator|.
name|putAll
argument_list|(
name|shardIdShardStateInfoMap
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// sometimes write the same thing twice
name|shardIdShardStateInfoMap
operator|=
name|state
operator|.
name|persistRoutingNodeState
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|shardIdShardStateInfoMap
operator|.
name|size
argument_list|()
argument_list|,
name|active
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|written
range|:
name|shardIdShardStateInfoMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ShardStateInfo
name|shardStateInfo
init|=
name|state
operator|.
name|loadShardInfo
argument_list|(
name|written
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shardStateInfo
argument_list|,
name|written
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|assertNull
argument_list|(
name|state
operator|.
name|loadShardInfo
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"no_such_index"
argument_list|,
name|written
operator|.
name|getKey
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|List
argument_list|<
name|MutableShardRouting
argument_list|>
name|nextRoundOfShards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|MutableShardRouting
name|routing
range|:
name|shards
control|)
block|{
name|nextRoundOfShards
operator|.
name|add
argument_list|(
operator|new
name|MutableShardRouting
argument_list|(
name|routing
argument_list|,
name|routing
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|node
operator|=
operator|new
name|RoutingNode
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|DiscoveryNode
argument_list|(
literal|"foo"
argument_list|,
literal|null
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|,
name|nextRoundOfShards
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|shardIdShardStateInfoMapNew
init|=
name|state
operator|.
name|persistRoutingNodeState
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shardIdShardStateInfoMapNew
operator|.
name|size
argument_list|()
argument_list|,
name|active
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|ShardStateInfo
argument_list|>
name|written
range|:
name|shardIdShardStateInfoMapNew
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ShardStateInfo
name|shardStateInfo
init|=
name|state
operator|.
name|loadShardInfo
argument_list|(
name|written
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|shardStateInfo
argument_list|,
name|written
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|ShardStateInfo
name|oldStateInfo
init|=
name|shardIdShardStateInfoMap
operator|.
name|get
argument_list|(
name|written
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|oldStateInfo
operator|.
name|version
argument_list|,
name|written
operator|.
name|getValue
argument_list|()
operator|.
name|version
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
name|assertNull
argument_list|(
name|state
operator|.
name|loadShardInfo
argument_list|(
operator|new
name|ShardId
argument_list|(
literal|"no_such_index"
argument_list|,
name|written
operator|.
name|getKey
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

