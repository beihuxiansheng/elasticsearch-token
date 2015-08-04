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
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_comment
comment|/**  * A helper that allows to create shard routing instances within tests, while not requiring to expose  * different simplified constructors on the ShardRouting itself.  */
end_comment

begin_class
DECL|class|TestShardRouting
specifier|public
class|class
name|TestShardRouting
block|{
DECL|method|newShardRouting
specifier|public
specifier|static
name|ShardRouting
name|newShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|currentNodeId
parameter_list|,
name|boolean
name|primary
parameter_list|,
name|ShardRoutingState
name|state
parameter_list|,
name|long
name|version
parameter_list|)
block|{
return|return
operator|new
name|ShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|currentNodeId
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|primary
argument_list|,
name|state
argument_list|,
name|version
argument_list|,
name|buildUnassignedInfo
argument_list|(
name|state
argument_list|)
argument_list|,
name|buildAllocationId
argument_list|(
name|state
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|newShardRouting
specifier|public
specifier|static
name|ShardRouting
name|newShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|currentNodeId
parameter_list|,
name|String
name|relocatingNodeId
parameter_list|,
name|boolean
name|primary
parameter_list|,
name|ShardRoutingState
name|state
parameter_list|,
name|long
name|version
parameter_list|)
block|{
return|return
operator|new
name|ShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|currentNodeId
argument_list|,
name|relocatingNodeId
argument_list|,
literal|null
argument_list|,
name|primary
argument_list|,
name|state
argument_list|,
name|version
argument_list|,
name|buildUnassignedInfo
argument_list|(
name|state
argument_list|)
argument_list|,
name|buildAllocationId
argument_list|(
name|state
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|newShardRouting
specifier|public
specifier|static
name|ShardRouting
name|newShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|currentNodeId
parameter_list|,
name|String
name|relocatingNodeId
parameter_list|,
name|boolean
name|primary
parameter_list|,
name|ShardRoutingState
name|state
parameter_list|,
name|AllocationId
name|allocationId
parameter_list|,
name|long
name|version
parameter_list|)
block|{
return|return
operator|new
name|ShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|currentNodeId
argument_list|,
name|relocatingNodeId
argument_list|,
literal|null
argument_list|,
name|primary
argument_list|,
name|state
argument_list|,
name|version
argument_list|,
name|buildUnassignedInfo
argument_list|(
name|state
argument_list|)
argument_list|,
name|allocationId
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|newShardRouting
specifier|public
specifier|static
name|ShardRouting
name|newShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|currentNodeId
parameter_list|,
name|String
name|relocatingNodeId
parameter_list|,
name|RestoreSource
name|restoreSource
parameter_list|,
name|boolean
name|primary
parameter_list|,
name|ShardRoutingState
name|state
parameter_list|,
name|long
name|version
parameter_list|)
block|{
return|return
operator|new
name|ShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|currentNodeId
argument_list|,
name|relocatingNodeId
argument_list|,
name|restoreSource
argument_list|,
name|primary
argument_list|,
name|state
argument_list|,
name|version
argument_list|,
name|buildUnassignedInfo
argument_list|(
name|state
argument_list|)
argument_list|,
name|buildAllocationId
argument_list|(
name|state
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|newShardRouting
specifier|public
specifier|static
name|ShardRouting
name|newShardRouting
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|currentNodeId
parameter_list|,
name|String
name|relocatingNodeId
parameter_list|,
name|RestoreSource
name|restoreSource
parameter_list|,
name|boolean
name|primary
parameter_list|,
name|ShardRoutingState
name|state
parameter_list|,
name|long
name|version
parameter_list|,
name|UnassignedInfo
name|unassignedInfo
parameter_list|)
block|{
return|return
operator|new
name|ShardRouting
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|currentNodeId
argument_list|,
name|relocatingNodeId
argument_list|,
name|restoreSource
argument_list|,
name|primary
argument_list|,
name|state
argument_list|,
name|version
argument_list|,
name|unassignedInfo
argument_list|,
name|buildAllocationId
argument_list|(
name|state
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|buildAllocationId
specifier|private
specifier|static
name|AllocationId
name|buildAllocationId
parameter_list|(
name|ShardRoutingState
name|state
parameter_list|)
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|UNASSIGNED
case|:
return|return
literal|null
return|;
case|case
name|INITIALIZING
case|:
case|case
name|STARTED
case|:
return|return
name|AllocationId
operator|.
name|newInitializing
argument_list|()
return|;
case|case
name|RELOCATING
case|:
name|AllocationId
name|allocationId
init|=
name|AllocationId
operator|.
name|newInitializing
argument_list|()
decl_stmt|;
return|return
name|AllocationId
operator|.
name|newRelocation
argument_list|(
name|allocationId
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal state"
argument_list|)
throw|;
block|}
block|}
DECL|method|buildUnassignedInfo
specifier|private
specifier|static
name|UnassignedInfo
name|buildUnassignedInfo
parameter_list|(
name|ShardRoutingState
name|state
parameter_list|)
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|UNASSIGNED
case|:
case|case
name|INITIALIZING
case|:
return|return
operator|new
name|UnassignedInfo
argument_list|(
name|ESTestCase
operator|.
name|randomFrom
argument_list|(
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|values
argument_list|()
argument_list|)
argument_list|,
literal|"auto generated for test"
argument_list|)
return|;
case|case
name|STARTED
case|:
case|case
name|RELOCATING
case|:
return|return
literal|null
return|;
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"illegal state"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

