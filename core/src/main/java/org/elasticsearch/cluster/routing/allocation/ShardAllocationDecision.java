begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing.allocation
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
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Represents the decision taken for the allocation of a single shard.  If  * the shard is unassigned, {@link #getAllocateDecision()} will return an  * object containing the decision and its explanation, and {@link #getMoveDecision()}  * will return an object for which {@link MoveDecision#isDecisionTaken()} returns  * {@code false}.  If the shard is in the started state, then {@link #getMoveDecision()}  * will return an object containing the decision to move/rebalance the shard, and  * {@link #getAllocateDecision()} will return an object for which  * {@link AllocateUnassignedDecision#isDecisionTaken()} returns {@code false}.  If  * the shard is neither unassigned nor started (i.e. it is initializing or relocating),  * then both {@link #getAllocateDecision()} and {@link #getMoveDecision()} will return  * objects whose {@code isDecisionTaken()} method returns {@code false}.  */
end_comment

begin_class
DECL|class|ShardAllocationDecision
specifier|public
specifier|final
class|class
name|ShardAllocationDecision
implements|implements
name|ToXContent
implements|,
name|Writeable
block|{
DECL|field|NOT_TAKEN
specifier|public
specifier|static
specifier|final
name|ShardAllocationDecision
name|NOT_TAKEN
init|=
operator|new
name|ShardAllocationDecision
argument_list|(
name|AllocateUnassignedDecision
operator|.
name|NOT_TAKEN
argument_list|,
name|MoveDecision
operator|.
name|NOT_TAKEN
argument_list|)
decl_stmt|;
DECL|field|allocateDecision
specifier|private
specifier|final
name|AllocateUnassignedDecision
name|allocateDecision
decl_stmt|;
DECL|field|moveDecision
specifier|private
specifier|final
name|MoveDecision
name|moveDecision
decl_stmt|;
DECL|method|ShardAllocationDecision
specifier|public
name|ShardAllocationDecision
parameter_list|(
name|AllocateUnassignedDecision
name|allocateDecision
parameter_list|,
name|MoveDecision
name|moveDecision
parameter_list|)
block|{
name|this
operator|.
name|allocateDecision
operator|=
name|allocateDecision
expr_stmt|;
name|this
operator|.
name|moveDecision
operator|=
name|moveDecision
expr_stmt|;
block|}
DECL|method|ShardAllocationDecision
specifier|public
name|ShardAllocationDecision
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|allocateDecision
operator|=
operator|new
name|AllocateUnassignedDecision
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|moveDecision
operator|=
operator|new
name|MoveDecision
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
name|allocateDecision
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|moveDecision
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns {@code true} if either an allocation decision or a move decision was taken      * for the shard.  If no decision was taken, as in the case of initializing or relocating      * shards, then this method returns {@code false}.      */
DECL|method|isDecisionTaken
specifier|public
name|boolean
name|isDecisionTaken
parameter_list|()
block|{
return|return
name|allocateDecision
operator|.
name|isDecisionTaken
argument_list|()
operator|||
name|moveDecision
operator|.
name|isDecisionTaken
argument_list|()
return|;
block|}
comment|/**      * Gets the unassigned allocation decision for the shard.  If the shard was not in the unassigned state,      * the instance of {@link AllocateUnassignedDecision} that is returned will have {@link AllocateUnassignedDecision#isDecisionTaken()}      * return {@code false}.      */
DECL|method|getAllocateDecision
specifier|public
name|AllocateUnassignedDecision
name|getAllocateDecision
parameter_list|()
block|{
return|return
name|allocateDecision
return|;
block|}
comment|/**      * Gets the move decision for the shard.  If the shard was not in the started state,      * the instance of {@link MoveDecision} that is returned will have {@link MoveDecision#isDecisionTaken()}      * return {@code false}.      */
DECL|method|getMoveDecision
specifier|public
name|MoveDecision
name|getMoveDecision
parameter_list|()
block|{
return|return
name|moveDecision
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
if|if
condition|(
name|allocateDecision
operator|.
name|isDecisionTaken
argument_list|()
condition|)
block|{
name|allocateDecision
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|moveDecision
operator|.
name|isDecisionTaken
argument_list|()
condition|)
block|{
name|moveDecision
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
block|}
end_class

end_unit
