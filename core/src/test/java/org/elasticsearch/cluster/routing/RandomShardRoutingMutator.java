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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility class the makes random modifications to ShardRouting  */
end_comment

begin_class
DECL|class|RandomShardRoutingMutator
specifier|public
specifier|final
class|class
name|RandomShardRoutingMutator
block|{
DECL|method|RandomShardRoutingMutator
specifier|private
name|RandomShardRoutingMutator
parameter_list|()
block|{      }
DECL|method|randomChange
specifier|public
specifier|static
name|void
name|randomChange
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|String
index|[]
name|nodes
parameter_list|)
block|{
switch|switch
condition|(
name|randomInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
if|if
condition|(
name|shardRouting
operator|.
name|unassigned
argument_list|()
operator|==
literal|false
condition|)
block|{
name|shardRouting
operator|.
name|moveToUnassigned
argument_list|(
operator|new
name|UnassignedInfo
argument_list|(
name|randomReason
argument_list|()
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|shardRouting
operator|.
name|unassignedInfo
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|shardRouting
operator|.
name|updateUnassignedInfo
argument_list|(
operator|new
name|UnassignedInfo
argument_list|(
name|randomReason
argument_list|()
argument_list|,
name|randomAsciiOfLength
argument_list|(
literal|10
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|1
case|:
if|if
condition|(
name|shardRouting
operator|.
name|unassigned
argument_list|()
condition|)
block|{
name|shardRouting
operator|.
name|initialize
argument_list|(
name|randomFrom
argument_list|(
name|nodes
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|2
case|:
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
name|shardRouting
operator|.
name|moveFromPrimary
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|shardRouting
operator|.
name|moveToPrimary
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
literal|3
case|:
if|if
condition|(
name|shardRouting
operator|.
name|initializing
argument_list|()
condition|)
block|{
name|shardRouting
operator|.
name|moveToStarted
argument_list|()
expr_stmt|;
block|}
break|break;
block|}
block|}
DECL|method|randomReason
specifier|public
specifier|static
name|UnassignedInfo
operator|.
name|Reason
name|randomReason
parameter_list|()
block|{
switch|switch
condition|(
name|randomInt
argument_list|(
literal|9
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_CREATED
return|;
case|case
literal|1
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|CLUSTER_RECOVERED
return|;
case|case
literal|2
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|INDEX_REOPENED
return|;
case|case
literal|3
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|DANGLING_INDEX_IMPORTED
return|;
case|case
literal|4
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|NEW_INDEX_RESTORED
return|;
case|case
literal|5
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|EXISTING_INDEX_RESTORED
return|;
case|case
literal|6
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|REPLICA_ADDED
return|;
case|case
literal|7
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|ALLOCATION_FAILED
return|;
case|case
literal|8
case|:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|NODE_LEFT
return|;
default|default:
return|return
name|UnassignedInfo
operator|.
name|Reason
operator|.
name|REROUTE_CANCELLED
return|;
block|}
block|}
block|}
end_class

end_unit

