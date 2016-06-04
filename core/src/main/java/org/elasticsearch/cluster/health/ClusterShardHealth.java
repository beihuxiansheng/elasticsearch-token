begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.health
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|health
package|;
end_package

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
name|IndexShardRoutingTable
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
DECL|class|ClusterShardHealth
specifier|public
specifier|final
class|class
name|ClusterShardHealth
implements|implements
name|Writeable
block|{
DECL|field|shardId
specifier|private
specifier|final
name|int
name|shardId
decl_stmt|;
DECL|field|status
specifier|private
specifier|final
name|ClusterHealthStatus
name|status
decl_stmt|;
DECL|field|activeShards
specifier|private
specifier|final
name|int
name|activeShards
decl_stmt|;
DECL|field|relocatingShards
specifier|private
specifier|final
name|int
name|relocatingShards
decl_stmt|;
DECL|field|initializingShards
specifier|private
specifier|final
name|int
name|initializingShards
decl_stmt|;
DECL|field|unassignedShards
specifier|private
specifier|final
name|int
name|unassignedShards
decl_stmt|;
DECL|field|primaryActive
specifier|private
specifier|final
name|boolean
name|primaryActive
decl_stmt|;
DECL|method|ClusterShardHealth
specifier|public
name|ClusterShardHealth
parameter_list|(
specifier|final
name|int
name|shardId
parameter_list|,
specifier|final
name|IndexShardRoutingTable
name|shardRoutingTable
parameter_list|,
specifier|final
name|boolean
name|noActiveAllocationIds
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|int
name|computeActiveShards
init|=
literal|0
decl_stmt|;
name|int
name|computeRelocatingShards
init|=
literal|0
decl_stmt|;
name|int
name|computeInitializingShards
init|=
literal|0
decl_stmt|;
name|int
name|computeUnassignedShards
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shardRouting
range|:
name|shardRoutingTable
control|)
block|{
if|if
condition|(
name|shardRouting
operator|.
name|active
argument_list|()
condition|)
block|{
name|computeActiveShards
operator|++
expr_stmt|;
if|if
condition|(
name|shardRouting
operator|.
name|relocating
argument_list|()
condition|)
block|{
comment|// the shard is relocating, the one it is relocating to will be in initializing state, so we don't count it
name|computeRelocatingShards
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|shardRouting
operator|.
name|initializing
argument_list|()
condition|)
block|{
name|computeInitializingShards
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|shardRouting
operator|.
name|unassigned
argument_list|()
condition|)
block|{
name|computeUnassignedShards
operator|++
expr_stmt|;
block|}
block|}
name|ClusterHealthStatus
name|computeStatus
decl_stmt|;
specifier|final
name|ShardRouting
name|primaryRouting
init|=
name|shardRoutingTable
operator|.
name|primaryShard
argument_list|()
decl_stmt|;
if|if
condition|(
name|primaryRouting
operator|.
name|active
argument_list|()
condition|)
block|{
if|if
condition|(
name|computeActiveShards
operator|==
name|shardRoutingTable
operator|.
name|size
argument_list|()
condition|)
block|{
name|computeStatus
operator|=
name|ClusterHealthStatus
operator|.
name|GREEN
expr_stmt|;
block|}
else|else
block|{
name|computeStatus
operator|=
name|ClusterHealthStatus
operator|.
name|YELLOW
expr_stmt|;
block|}
block|}
else|else
block|{
name|computeStatus
operator|=
name|UnassignedInfo
operator|.
name|unassignedPrimaryShardHealth
argument_list|(
name|primaryRouting
operator|.
name|unassignedInfo
argument_list|()
argument_list|,
name|noActiveAllocationIds
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|status
operator|=
name|computeStatus
expr_stmt|;
name|this
operator|.
name|activeShards
operator|=
name|computeActiveShards
expr_stmt|;
name|this
operator|.
name|relocatingShards
operator|=
name|computeRelocatingShards
expr_stmt|;
name|this
operator|.
name|initializingShards
operator|=
name|computeInitializingShards
expr_stmt|;
name|this
operator|.
name|unassignedShards
operator|=
name|computeUnassignedShards
expr_stmt|;
name|this
operator|.
name|primaryActive
operator|=
name|primaryRouting
operator|.
name|active
argument_list|()
expr_stmt|;
block|}
DECL|method|ClusterShardHealth
specifier|public
name|ClusterShardHealth
parameter_list|(
specifier|final
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|activeShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|relocatingShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|initializingShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|unassignedShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|primaryActive
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|getId
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|getStatus
specifier|public
name|ClusterHealthStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getRelocatingShards
specifier|public
name|int
name|getRelocatingShards
parameter_list|()
block|{
return|return
name|relocatingShards
return|;
block|}
DECL|method|getActiveShards
specifier|public
name|int
name|getActiveShards
parameter_list|()
block|{
return|return
name|activeShards
return|;
block|}
DECL|method|isPrimaryActive
specifier|public
name|boolean
name|isPrimaryActive
parameter_list|()
block|{
return|return
name|primaryActive
return|;
block|}
DECL|method|getInitializingShards
specifier|public
name|int
name|getInitializingShards
parameter_list|()
block|{
return|return
name|initializingShards
return|;
block|}
DECL|method|getUnassignedShards
specifier|public
name|int
name|getUnassignedShards
parameter_list|()
block|{
return|return
name|unassignedShards
return|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
specifier|final
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
name|status
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|activeShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|relocatingShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|initializingShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|unassignedShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|primaryActive
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

