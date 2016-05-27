begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation.command
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|RoutingNodes
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
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|RerouteExplanation
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
name|allocation
operator|.
name|RoutingAllocation
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
name|allocation
operator|.
name|decider
operator|.
name|Decision
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
name|ParseField
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|xcontent
operator|.
name|XContentBuilder
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
name|xcontent
operator|.
name|XContentParser
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
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ShardRoutingState
operator|.
name|RELOCATING
import|;
end_import

begin_comment
comment|/**  * A command that cancels relocation, or recovery of a given shard on a node.  */
end_comment

begin_class
DECL|class|CancelAllocationCommand
specifier|public
class|class
name|CancelAllocationCommand
implements|implements
name|AllocationCommand
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"cancel"
decl_stmt|;
DECL|field|COMMAND_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|COMMAND_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|String
name|index
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|int
name|shardId
decl_stmt|;
DECL|field|node
specifier|private
specifier|final
name|String
name|node
decl_stmt|;
DECL|field|allowPrimary
specifier|private
specifier|final
name|boolean
name|allowPrimary
decl_stmt|;
comment|/**      * Creates a new {@link CancelAllocationCommand}      *      * @param index index of the shard which allocation should be canceled      * @param shardId id of the shard which allocation should be canceled      * @param node id of the node that manages the shard which allocation should be canceled      */
DECL|method|CancelAllocationCommand
specifier|public
name|CancelAllocationCommand
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|String
name|node
parameter_list|,
name|boolean
name|allowPrimary
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|allowPrimary
operator|=
name|allowPrimary
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|CancelAllocationCommand
specifier|public
name|CancelAllocationCommand
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|index
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|node
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|allowPrimary
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|allowPrimary
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
comment|/**      * Get the index of the shard which allocation should be canceled      * @return index of the shard which allocation should be canceled      */
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
comment|/**       * Get the id of the shard which allocation should be canceled      * @return id of the shard which allocation should be canceled      */
DECL|method|shardId
specifier|public
name|int
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
comment|/**      * Get the id of the node that manages the shard which allocation should be canceled      * @return id of the node that manages the shard which allocation should be canceled      */
DECL|method|node
specifier|public
name|String
name|node
parameter_list|()
block|{
return|return
name|this
operator|.
name|node
return|;
block|}
DECL|method|allowPrimary
specifier|public
name|boolean
name|allowPrimary
parameter_list|()
block|{
return|return
name|this
operator|.
name|allowPrimary
return|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|public
name|RerouteExplanation
name|execute
parameter_list|(
name|RoutingAllocation
name|allocation
parameter_list|,
name|boolean
name|explain
parameter_list|)
block|{
name|DiscoveryNode
name|discoNode
init|=
name|allocation
operator|.
name|nodes
argument_list|()
operator|.
name|resolveNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RoutingNodes
operator|.
name|RoutingNodeIterator
name|it
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|routingNodeIter
argument_list|(
name|discoNode
operator|.
name|getId
argument_list|()
argument_list|)
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ShardRouting
name|shardRouting
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|shardRouting
operator|.
name|shardId
argument_list|()
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|shardRouting
operator|.
name|shardId
argument_list|()
operator|.
name|id
argument_list|()
operator|!=
name|shardId
condition|)
block|{
continue|continue;
block|}
name|found
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|relocatingNodeId
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|initializing
argument_list|()
condition|)
block|{
comment|// the shard is initializing and recovering from another node, simply cancel the recovery
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
comment|// and cancel the relocating state from the shard its being relocated from
name|RoutingNode
name|relocatingFromNode
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|node
argument_list|(
name|shardRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|relocatingFromNode
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ShardRouting
name|fromShardRouting
range|:
name|relocatingFromNode
control|)
block|{
if|if
condition|(
name|fromShardRouting
operator|.
name|isSameShard
argument_list|(
name|shardRouting
argument_list|)
operator|&&
name|fromShardRouting
operator|.
name|state
argument_list|()
operator|==
name|RELOCATING
condition|)
block|{
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|cancelRelocation
argument_list|(
name|fromShardRouting
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|shardRouting
operator|.
name|relocating
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|allowPrimary
operator|&&
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
comment|// can't cancel a primary shard being initialized
if|if
condition|(
name|explain
condition|)
block|{
return|return
operator|new
name|RerouteExplanation
argument_list|(
name|this
argument_list|,
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
literal|"cancel_allocation_command"
argument_list|,
literal|"can't cancel "
operator|+
name|shardId
operator|+
literal|" on node "
operator|+
name|discoNode
operator|+
literal|", shard is primary and initializing its state"
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[cancel_allocation] can't cancel "
operator|+
name|shardId
operator|+
literal|" on node "
operator|+
name|discoNode
operator|+
literal|", shard is primary and initializing its state"
argument_list|)
throw|;
block|}
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|boolean
name|addAsUnassigned
init|=
literal|true
decl_stmt|;
comment|// now, find the shard that is initializing on the target node
name|RoutingNodes
operator|.
name|RoutingNodeIterator
name|initializingNode
init|=
name|allocation
operator|.
name|routingNodes
argument_list|()
operator|.
name|routingNodeIter
argument_list|(
name|shardRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|initializingNode
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|initializingNode
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|ShardRouting
name|initializingShardRouting
init|=
name|initializingNode
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|initializingShardRouting
operator|.
name|isRelocationTargetOf
argument_list|(
name|shardRouting
argument_list|)
condition|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
comment|// cancel and remove target shard
name|initializingNode
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// promote to initializing shard without relocation source and ensure that removed relocation source
comment|// is not added back as unassigned shard
name|initializingNode
operator|.
name|removeRelocationSource
argument_list|()
expr_stmt|;
name|addAsUnassigned
operator|=
literal|false
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
if|if
condition|(
name|addAsUnassigned
condition|)
block|{
name|it
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
name|REROUTE_CANCELLED
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|// the shard is not relocating, its either started, or initializing, just cancel it and move on...
if|if
condition|(
operator|!
name|allowPrimary
operator|&&
name|shardRouting
operator|.
name|primary
argument_list|()
condition|)
block|{
comment|// can't cancel a primary shard being initialized
if|if
condition|(
name|explain
condition|)
block|{
return|return
operator|new
name|RerouteExplanation
argument_list|(
name|this
argument_list|,
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
literal|"cancel_allocation_command"
argument_list|,
literal|"can't cancel "
operator|+
name|shardId
operator|+
literal|" on node "
operator|+
name|discoNode
operator|+
literal|", shard is primary and started"
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[cancel_allocation] can't cancel "
operator|+
name|shardId
operator|+
literal|" on node "
operator|+
name|discoNode
operator|+
literal|", shard is primary and started"
argument_list|)
throw|;
block|}
name|it
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
name|REROUTE_CANCELLED
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
if|if
condition|(
name|explain
condition|)
block|{
return|return
operator|new
name|RerouteExplanation
argument_list|(
name|this
argument_list|,
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
literal|"cancel_allocation_command"
argument_list|,
literal|"can't cancel "
operator|+
name|shardId
operator|+
literal|", failed to find it on node "
operator|+
name|discoNode
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[cancel_allocation] can't cancel "
operator|+
name|shardId
operator|+
literal|", failed to find it on node "
operator|+
name|discoNode
argument_list|)
throw|;
block|}
return|return
operator|new
name|RerouteExplanation
argument_list|(
name|this
argument_list|,
name|allocation
operator|.
name|decision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
literal|"cancel_allocation_command"
argument_list|,
literal|"shard "
operator|+
name|shardId
operator|+
literal|" on node "
operator|+
name|discoNode
operator|+
literal|" can be cancelled"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"shard"
argument_list|,
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"node"
argument_list|,
name|node
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"allow_primary"
argument_list|,
name|allowPrimary
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|endObject
argument_list|()
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|CancelAllocationCommand
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|index
init|=
literal|null
decl_stmt|;
name|int
name|shardId
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|nodeId
init|=
literal|null
decl_stmt|;
name|boolean
name|allowPrimary
init|=
literal|false
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|index
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"shard"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shardId
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"node"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|nodeId
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"allow_primary"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"allowPrimary"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|allowPrimary
operator|=
name|parser
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"[{}] command does not support field [{}]"
argument_list|,
name|NAME
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"[{}] command does not support complex json tokens [{}]"
argument_list|,
name|NAME
argument_list|,
name|token
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"[{}] command missing the index parameter"
argument_list|,
name|NAME
argument_list|)
throw|;
block|}
if|if
condition|(
name|shardId
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"[{}] command missing the shard parameter"
argument_list|,
name|NAME
argument_list|)
throw|;
block|}
if|if
condition|(
name|nodeId
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"[{}] command missing the node parameter"
argument_list|,
name|NAME
argument_list|)
throw|;
block|}
return|return
operator|new
name|CancelAllocationCommand
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|nodeId
argument_list|,
name|allowPrimary
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CancelAllocationCommand
name|other
init|=
operator|(
name|CancelAllocationCommand
operator|)
name|obj
decl_stmt|;
comment|// Override equals and hashCode for testing
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|index
argument_list|,
name|other
operator|.
name|index
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|shardId
argument_list|,
name|other
operator|.
name|shardId
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|node
argument_list|,
name|other
operator|.
name|node
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|allowPrimary
argument_list|,
name|other
operator|.
name|allowPrimary
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
comment|// Override equals and hashCode for testing
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|node
argument_list|,
name|allowPrimary
argument_list|)
return|;
block|}
block|}
end_class

end_unit

