begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
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
name|cluster
operator|.
name|metadata
operator|.
name|SnapshotId
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
name|test
operator|.
name|ESTestCase
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

begin_class
DECL|class|ShardRoutingTests
specifier|public
class|class
name|ShardRoutingTests
extends|extends
name|ESTestCase
block|{
DECL|method|testFrozenAfterRead
specifier|public
name|void
name|testFrozenAfterRead
parameter_list|()
throws|throws
name|IOException
block|{
name|ShardRouting
name|routing
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"foo"
argument_list|,
literal|1
argument_list|,
literal|"node_1"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|routing
operator|.
name|moveToPrimary
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|routing
operator|.
name|primary
argument_list|()
argument_list|)
expr_stmt|;
name|routing
operator|.
name|moveFromPrimary
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|routing
operator|.
name|primary
argument_list|()
argument_list|)
expr_stmt|;
name|BytesStreamOutput
name|out
init|=
operator|new
name|BytesStreamOutput
argument_list|()
decl_stmt|;
name|routing
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|ShardRouting
name|newRouting
init|=
name|ShardRouting
operator|.
name|readShardRoutingEntry
argument_list|(
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|newRouting
operator|.
name|moveToPrimary
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
block|}
DECL|method|testIsSameAllocation
specifier|public
name|void
name|testIsSameAllocation
parameter_list|()
block|{
name|ShardRouting
name|unassignedShard0
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|unassignedShard1
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|initializingShard0
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|"1"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|initializingShard1
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|"1"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|startedShard0
init|=
operator|new
name|ShardRouting
argument_list|(
name|initializingShard0
argument_list|)
decl_stmt|;
name|startedShard0
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|ShardRouting
name|startedShard1
init|=
operator|new
name|ShardRouting
argument_list|(
name|initializingShard1
argument_list|)
decl_stmt|;
name|startedShard1
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
comment|// test identity
name|assertTrue
argument_list|(
name|initializingShard0
operator|.
name|isSameAllocation
argument_list|(
name|initializingShard0
argument_list|)
argument_list|)
expr_stmt|;
comment|// test same allocation different state
name|assertTrue
argument_list|(
name|initializingShard0
operator|.
name|isSameAllocation
argument_list|(
name|startedShard0
argument_list|)
argument_list|)
expr_stmt|;
comment|// test unassigned is false even to itself
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isSameAllocation
argument_list|(
name|unassignedShard0
argument_list|)
argument_list|)
expr_stmt|;
comment|// test different shards/nodes/state
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isSameAllocation
argument_list|(
name|unassignedShard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isSameAllocation
argument_list|(
name|initializingShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isSameAllocation
argument_list|(
name|initializingShard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isSameAllocation
argument_list|(
name|startedShard1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testIsSameShard
specifier|public
name|void
name|testIsSameShard
parameter_list|()
block|{
name|ShardRouting
name|index1Shard0a
init|=
name|randomShardRouting
argument_list|(
literal|"index1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ShardRouting
name|index1Shard0b
init|=
name|randomShardRouting
argument_list|(
literal|"index1"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ShardRouting
name|index1Shard1
init|=
name|randomShardRouting
argument_list|(
literal|"index1"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|index2Shard0
init|=
name|randomShardRouting
argument_list|(
literal|"index2"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ShardRouting
name|index2Shard1
init|=
name|randomShardRouting
argument_list|(
literal|"index2"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|index1Shard0a
operator|.
name|isSameShard
argument_list|(
name|index1Shard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|index1Shard0a
operator|.
name|isSameShard
argument_list|(
name|index1Shard0b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|index1Shard0a
operator|.
name|isSameShard
argument_list|(
name|index1Shard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|index1Shard0a
operator|.
name|isSameShard
argument_list|(
name|index2Shard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|index1Shard0a
operator|.
name|isSameShard
argument_list|(
name|index2Shard1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|randomShardRouting
specifier|private
name|ShardRouting
name|randomShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shard
parameter_list|)
block|{
name|ShardRoutingState
name|state
init|=
name|randomFrom
argument_list|(
name|ShardRoutingState
operator|.
name|values
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|index
argument_list|,
name|shard
argument_list|,
name|state
operator|==
name|ShardRoutingState
operator|.
name|UNASSIGNED
condition|?
literal|null
else|:
literal|"1"
argument_list|,
name|state
operator|!=
name|ShardRoutingState
operator|.
name|UNASSIGNED
operator|&&
name|randomBoolean
argument_list|()
argument_list|,
name|state
argument_list|,
name|randomInt
argument_list|(
literal|5
argument_list|)
argument_list|)
return|;
block|}
DECL|method|testIsSourceTargetRelocation
specifier|public
name|void
name|testIsSourceTargetRelocation
parameter_list|()
block|{
name|ShardRouting
name|unassignedShard0
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|initializingShard0
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|,
literal|"node1"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|initializingShard1
init|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|1
argument_list|,
literal|"node1"
argument_list|,
name|randomBoolean
argument_list|()
argument_list|,
name|ShardRoutingState
operator|.
name|INITIALIZING
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ShardRouting
name|startedShard0
init|=
operator|new
name|ShardRouting
argument_list|(
name|initializingShard0
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|startedShard0
operator|.
name|isRelocationTarget
argument_list|()
argument_list|)
expr_stmt|;
name|startedShard0
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|startedShard0
operator|.
name|isRelocationTarget
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|startedShard1
init|=
operator|new
name|ShardRouting
argument_list|(
name|initializingShard1
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|startedShard1
operator|.
name|isRelocationTarget
argument_list|()
argument_list|)
expr_stmt|;
name|startedShard1
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|startedShard1
operator|.
name|isRelocationTarget
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|sourceShard0a
init|=
operator|new
name|ShardRouting
argument_list|(
name|startedShard0
argument_list|)
decl_stmt|;
name|sourceShard0a
operator|.
name|relocate
argument_list|(
literal|"node2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceShard0a
operator|.
name|isRelocationTarget
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|targetShard0a
init|=
name|sourceShard0a
operator|.
name|buildTargetRelocatingShard
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTarget
argument_list|()
argument_list|)
expr_stmt|;
name|ShardRouting
name|sourceShard0b
init|=
operator|new
name|ShardRouting
argument_list|(
name|startedShard0
argument_list|)
decl_stmt|;
name|sourceShard0b
operator|.
name|relocate
argument_list|(
literal|"node2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|ShardRouting
name|sourceShard1
init|=
operator|new
name|ShardRouting
argument_list|(
name|startedShard1
argument_list|)
decl_stmt|;
name|sourceShard1
operator|.
name|relocate
argument_list|(
literal|"node2"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// test true scenarios
name|assertTrue
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|sourceShard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|sourceShard0a
operator|.
name|isRelocationSourceOf
argument_list|(
name|targetShard0a
argument_list|)
argument_list|)
expr_stmt|;
comment|// test two shards are not mixed
name|assertFalse
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|sourceShard1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceShard1
operator|.
name|isRelocationSourceOf
argument_list|(
name|targetShard0a
argument_list|)
argument_list|)
expr_stmt|;
comment|// test two allocations are not mixed
name|assertFalse
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|sourceShard0b
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceShard0b
operator|.
name|isRelocationSourceOf
argument_list|(
name|targetShard0a
argument_list|)
argument_list|)
expr_stmt|;
comment|// test different shard states
name|assertFalse
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|unassignedShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|unassignedShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isRelocationSourceOf
argument_list|(
name|targetShard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unassignedShard0
operator|.
name|isRelocationSourceOf
argument_list|(
name|sourceShard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|initializingShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|initializingShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|initializingShard0
operator|.
name|isRelocationSourceOf
argument_list|(
name|targetShard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|initializingShard0
operator|.
name|isRelocationSourceOf
argument_list|(
name|sourceShard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|targetShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|startedShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|sourceShard0a
operator|.
name|isRelocationTargetOf
argument_list|(
name|startedShard0
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|startedShard0
operator|.
name|isRelocationSourceOf
argument_list|(
name|targetShard0a
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|startedShard0
operator|.
name|isRelocationSourceOf
argument_list|(
name|sourceShard0a
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testEqualsIgnoringVersion
specifier|public
name|void
name|testEqualsIgnoringVersion
parameter_list|()
block|{
name|ShardRouting
name|routing
init|=
name|randomShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|ShardRouting
name|otherRouting
init|=
operator|new
name|ShardRouting
argument_list|(
name|routing
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"expected equality\nthis  "
operator|+
name|routing
operator|+
literal|",\nother "
operator|+
name|otherRouting
argument_list|,
name|routing
operator|.
name|equalsIgnoringMetaData
argument_list|(
name|otherRouting
argument_list|)
argument_list|)
expr_stmt|;
name|otherRouting
operator|=
operator|new
name|ShardRouting
argument_list|(
name|routing
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"expected equality\nthis  "
operator|+
name|routing
operator|+
literal|",\nother "
operator|+
name|otherRouting
argument_list|,
name|routing
operator|.
name|equalsIgnoringMetaData
argument_list|(
name|otherRouting
argument_list|)
argument_list|)
expr_stmt|;
name|otherRouting
operator|=
operator|new
name|ShardRouting
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|Integer
index|[]
name|changeIds
init|=
operator|new
name|Integer
index|[]
block|{
literal|0
block|,
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|}
decl_stmt|;
for|for
control|(
name|int
name|changeId
range|:
name|randomSubsetOf
argument_list|(
name|randomIntBetween
argument_list|(
literal|1
argument_list|,
name|changeIds
operator|.
name|length
argument_list|)
argument_list|,
name|changeIds
argument_list|)
control|)
block|{
switch|switch
condition|(
name|changeId
condition|)
block|{
case|case
literal|0
case|:
comment|// change index
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
operator|+
literal|"a"
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
comment|// change shard id
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
operator|+
literal|1
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// change current node
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
operator|==
literal|null
condition|?
literal|"1"
else|:
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
operator|+
literal|"_1"
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|3
case|:
comment|// change relocating node
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
operator|==
literal|null
condition|?
literal|"1"
else|:
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
operator|+
literal|"_1"
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|4
case|:
comment|// change restore source
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
operator|==
literal|null
condition|?
operator|new
name|RestoreSource
argument_list|(
operator|new
name|SnapshotId
argument_list|(
literal|"test"
argument_list|,
literal|"s1"
argument_list|)
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
literal|"test"
argument_list|)
else|:
operator|new
name|RestoreSource
argument_list|(
name|otherRouting
operator|.
name|restoreSource
argument_list|()
operator|.
name|snapshotId
argument_list|()
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|otherRouting
operator|.
name|index
argument_list|()
operator|+
literal|"_1"
argument_list|)
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|5
case|:
comment|// change primary flag
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
operator|==
literal|false
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|6
case|:
comment|// change state
name|ShardRoutingState
name|newState
decl_stmt|;
do|do
block|{
name|newState
operator|=
name|randomFrom
argument_list|(
name|ShardRoutingState
operator|.
name|values
argument_list|()
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|newState
operator|==
name|otherRouting
operator|.
name|state
argument_list|()
condition|)
do|;
name|UnassignedInfo
name|unassignedInfo
init|=
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|unassignedInfo
operator|==
literal|null
operator|&&
operator|(
name|newState
operator|==
name|ShardRoutingState
operator|.
name|UNASSIGNED
operator|||
name|newState
operator|==
name|ShardRoutingState
operator|.
name|INITIALIZING
operator|)
condition|)
block|{
name|unassignedInfo
operator|=
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|newState
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|unassignedInfo
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// change version
name|otherRouting
operator|=
operator|new
name|ShardRouting
argument_list|(
name|otherRouting
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|randomBoolean
argument_list|()
condition|)
block|{
comment|// change unassigned info
name|otherRouting
operator|=
name|TestShardRouting
operator|.
name|newShardRouting
argument_list|(
name|otherRouting
operator|.
name|getIndexName
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|id
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|restoreSource
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|primary
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|state
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|version
argument_list|()
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
operator|==
literal|null
condition|?
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
literal|"test"
argument_list|)
else|:
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
argument_list|,
name|otherRouting
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|+
literal|"_1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"comparing\nthis  {} to\nother {}"
argument_list|,
name|routing
argument_list|,
name|otherRouting
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"expected non-equality\nthis  "
operator|+
name|routing
operator|+
literal|",\nother "
operator|+
name|otherRouting
argument_list|,
name|routing
operator|.
name|equalsIgnoringMetaData
argument_list|(
name|otherRouting
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFrozenOnRoutingTable
specifier|public
name|void
name|testFrozenOnRoutingTable
parameter_list|()
block|{
name|MetaData
name|metaData
init|=
name|MetaData
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
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
literal|1
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RoutingTable
name|routingTable
init|=
name|RoutingTable
operator|.
name|builder
argument_list|()
operator|.
name|addAsNew
argument_list|(
name|metaData
operator|.
name|index
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
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
for|for
control|(
name|ShardRouting
name|routing
range|:
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|allShards
argument_list|()
control|)
block|{
name|long
name|version
init|=
name|routing
operator|.
name|version
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|routing
operator|.
name|isFrozen
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|routing
operator|.
name|moveToPrimary
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|moveFromPrimary
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|initialize
argument_list|(
literal|"boom"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|cancelRelocation
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|moveToUnassigned
argument_list|(
operator|new
name|UnassignedInfo
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|REPLICA_ADDED
argument_list|,
literal|"foobar"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|relocate
argument_list|(
literal|"foobar"
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
try|try
block|{
name|routing
operator|.
name|reinitializeShard
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"must be frozen"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ex
parameter_list|)
block|{
comment|// expected
block|}
name|assertEquals
argument_list|(
name|version
argument_list|,
name|routing
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testExpectedSize
specifier|public
name|void
name|testExpectedSize
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|iters
init|=
name|randomIntBetween
argument_list|(
literal|10
argument_list|,
literal|100
argument_list|)
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
name|iters
condition|;
name|i
operator|++
control|)
block|{
name|ShardRouting
name|routing
init|=
name|randomShardRouting
argument_list|(
literal|"test"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|long
name|byteSize
init|=
name|randomIntBetween
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
if|if
condition|(
name|routing
operator|.
name|unassigned
argument_list|()
condition|)
block|{
name|ShardRoutingHelper
operator|.
name|initialize
argument_list|(
name|routing
argument_list|,
literal|"foo"
argument_list|,
name|byteSize
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|routing
operator|.
name|started
argument_list|()
condition|)
block|{
name|ShardRoutingHelper
operator|.
name|relocate
argument_list|(
name|routing
argument_list|,
literal|"foo"
argument_list|,
name|byteSize
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|byteSize
operator|=
operator|-
literal|1
expr_stmt|;
block|}
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
name|routing
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|routing
operator|=
name|ShardRouting
operator|.
name|readShardRoutingEntry
argument_list|(
name|StreamInput
operator|.
name|wrap
argument_list|(
name|out
operator|.
name|bytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|routing
operator|.
name|initializing
argument_list|()
operator|||
name|routing
operator|.
name|relocating
argument_list|()
condition|)
block|{
name|assertEquals
argument_list|(
name|routing
operator|.
name|toString
argument_list|()
argument_list|,
name|byteSize
argument_list|,
name|routing
operator|.
name|getExpectedShardSize
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|byteSize
operator|>=
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|routing
operator|.
name|toString
argument_list|()
argument_list|,
name|routing
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"expected_shard_size["
operator|+
name|byteSize
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|routing
operator|.
name|initializing
argument_list|()
condition|)
block|{
name|routing
operator|=
operator|new
name|ShardRouting
argument_list|(
name|routing
argument_list|)
expr_stmt|;
name|routing
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|routing
operator|.
name|getExpectedShardSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|routing
operator|.
name|toString
argument_list|()
argument_list|,
name|routing
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"expected_shard_size["
operator|+
name|byteSize
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|assertFalse
argument_list|(
name|routing
operator|.
name|toString
argument_list|()
argument_list|,
name|routing
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"expected_shard_size ["
operator|+
name|byteSize
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|byteSize
argument_list|,
name|routing
operator|.
name|getExpectedShardSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

