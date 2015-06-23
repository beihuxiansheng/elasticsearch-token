begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|BroadcastShardResponse
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
name|Nullable
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
name|ToXContent
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
name|XContentBuilderString
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
name|engine
operator|.
name|CommitStats
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
name|IndexShard
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
name|ShardRouting
operator|.
name|readShardRoutingEntry
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
name|BroadcastShardResponse
implements|implements
name|ToXContent
block|{
DECL|field|shardRouting
specifier|private
name|ShardRouting
name|shardRouting
decl_stmt|;
DECL|field|commonStats
name|CommonStats
name|commonStats
decl_stmt|;
annotation|@
name|Nullable
DECL|field|commitStats
name|CommitStats
name|commitStats
decl_stmt|;
DECL|method|ShardStats
name|ShardStats
parameter_list|()
block|{     }
DECL|method|ShardStats
specifier|public
name|ShardStats
parameter_list|(
name|IndexShard
name|indexShard
parameter_list|,
name|ShardRouting
name|shardRouting
parameter_list|,
name|CommonStatsFlags
name|flags
parameter_list|)
block|{
name|super
argument_list|(
name|indexShard
operator|.
name|shardId
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
name|commonStats
operator|=
operator|new
name|CommonStats
argument_list|(
name|indexShard
argument_list|,
name|flags
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitStats
operator|=
name|indexShard
operator|.
name|commitStats
argument_list|()
expr_stmt|;
block|}
comment|/**      * The shard routing information (cluster wide shard state).      */
DECL|method|getShardRouting
specifier|public
name|ShardRouting
name|getShardRouting
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardRouting
return|;
block|}
DECL|method|getStats
specifier|public
name|CommonStats
name|getStats
parameter_list|()
block|{
return|return
name|this
operator|.
name|commonStats
return|;
block|}
DECL|method|getCommitStats
specifier|public
name|CommitStats
name|getCommitStats
parameter_list|()
block|{
return|return
name|this
operator|.
name|commitStats
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
annotation|@
name|Override
DECL|method|readFrom
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
name|commonStats
operator|=
name|CommonStats
operator|.
name|readCommonStats
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|commitStats
operator|=
name|CommitStats
operator|.
name|readOptionalCommitStatsFrom
argument_list|(
name|in
argument_list|)
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
name|commonStats
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalStreamable
argument_list|(
name|commitStats
argument_list|)
expr_stmt|;
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
argument_list|(
name|Fields
operator|.
name|ROUTING
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATE
argument_list|,
name|shardRouting
operator|.
name|state
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARY
argument_list|,
name|shardRouting
operator|.
name|primary
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NODE
argument_list|,
name|shardRouting
operator|.
name|currentNodeId
argument_list|()
argument_list|)
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|RELOCATING_NODE
argument_list|,
name|shardRouting
operator|.
name|relocatingNodeId
argument_list|()
argument_list|)
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|commonStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|commitStats
operator|!=
literal|null
condition|)
block|{
name|commitStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|ROUTING
specifier|static
specifier|final
name|XContentBuilderString
name|ROUTING
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"routing"
argument_list|)
decl_stmt|;
DECL|field|STATE
specifier|static
specifier|final
name|XContentBuilderString
name|STATE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"state"
argument_list|)
decl_stmt|;
DECL|field|PRIMARY
specifier|static
specifier|final
name|XContentBuilderString
name|PRIMARY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"primary"
argument_list|)
decl_stmt|;
DECL|field|NODE
specifier|static
specifier|final
name|XContentBuilderString
name|NODE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"node"
argument_list|)
decl_stmt|;
DECL|field|RELOCATING_NODE
specifier|static
specifier|final
name|XContentBuilderString
name|RELOCATING_NODE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"relocating_node"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

