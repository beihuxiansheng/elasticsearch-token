begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.stats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|stats
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
name|support
operator|.
name|broadcast
operator|.
name|BroadcastShardOperationResponse
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
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
operator|.
name|ImmutableShardRouting
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ShardStats
specifier|public
class|class
name|ShardStats
extends|extends
name|BroadcastShardOperationResponse
block|{
DECL|field|shardRouting
specifier|private
name|ShardRouting
name|shardRouting
decl_stmt|;
DECL|field|stats
name|CommonStats
name|stats
decl_stmt|;
DECL|method|ShardStats
name|ShardStats
parameter_list|()
block|{     }
DECL|method|ShardStats
name|ShardStats
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|)
block|{
name|super
argument_list|(
name|shardRouting
operator|.
name|index
argument_list|()
argument_list|,
name|shardRouting
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardRouting
operator|=
name|shardRouting
expr_stmt|;
name|this
operator|.
name|stats
operator|=
operator|new
name|CommonStats
argument_list|()
expr_stmt|;
block|}
comment|/**      * The shard routing information (cluster wide shard state).      */
DECL|method|shardRouting
specifier|public
name|ShardRouting
name|shardRouting
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardRouting
return|;
block|}
comment|/**      * The shard routing information (cluster wide shard state).      */
DECL|method|getShardRouting
specifier|public
name|ShardRouting
name|getShardRouting
parameter_list|()
block|{
return|return
name|shardRouting
argument_list|()
return|;
block|}
DECL|method|stats
specifier|public
name|CommonStats
name|stats
parameter_list|()
block|{
return|return
name|this
operator|.
name|stats
return|;
block|}
DECL|method|getStats
specifier|public
name|CommonStats
name|getStats
parameter_list|()
block|{
return|return
name|stats
argument_list|()
return|;
block|}
DECL|method|readShardStats
specifier|public
specifier|static
name|ShardStats
name|readShardStats
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ShardStats
name|stats
init|=
operator|new
name|ShardStats
argument_list|()
decl_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|shardRouting
operator|=
name|readShardRoutingEntry
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|stats
operator|=
name|CommonStats
operator|.
name|readCommonStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|shardRouting
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|stats
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

