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
name|cluster
operator|.
name|routing
operator|.
name|UnassignedInfo
operator|.
name|AllocationStatus
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
name|cluster
operator|.
name|routing
operator|.
name|allocation
operator|.
name|decider
operator|.
name|Decision
operator|.
name|Type
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * Represents the allocation decision by an allocator for an unassigned shard.  */
end_comment

begin_class
DECL|class|UnassignedShardDecision
specifier|public
class|class
name|UnassignedShardDecision
block|{
comment|/** a constant representing a shard decision where no decision was taken */
DECL|field|DECISION_NOT_TAKEN
specifier|public
specifier|static
specifier|final
name|UnassignedShardDecision
name|DECISION_NOT_TAKEN
init|=
operator|new
name|UnassignedShardDecision
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
annotation|@
name|Nullable
DECL|field|finalDecision
specifier|private
specifier|final
name|Decision
name|finalDecision
decl_stmt|;
annotation|@
name|Nullable
DECL|field|allocationStatus
specifier|private
specifier|final
name|AllocationStatus
name|allocationStatus
decl_stmt|;
annotation|@
name|Nullable
DECL|field|finalExplanation
specifier|private
specifier|final
name|String
name|finalExplanation
decl_stmt|;
annotation|@
name|Nullable
DECL|field|assignedNodeId
specifier|private
specifier|final
name|String
name|assignedNodeId
decl_stmt|;
annotation|@
name|Nullable
DECL|field|allocationId
specifier|private
specifier|final
name|String
name|allocationId
decl_stmt|;
annotation|@
name|Nullable
DECL|field|nodeDecisions
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Decision
argument_list|>
name|nodeDecisions
decl_stmt|;
DECL|method|UnassignedShardDecision
specifier|private
name|UnassignedShardDecision
parameter_list|(
name|Decision
name|finalDecision
parameter_list|,
name|AllocationStatus
name|allocationStatus
parameter_list|,
name|String
name|finalExplanation
parameter_list|,
name|String
name|assignedNodeId
parameter_list|,
name|String
name|allocationId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Decision
argument_list|>
name|nodeDecisions
parameter_list|)
block|{
assert|assert
name|finalExplanation
operator|!=
literal|null
operator|||
name|finalDecision
operator|==
literal|null
operator|:
literal|"if a decision was taken, there must be an explanation for it"
assert|;
assert|assert
name|assignedNodeId
operator|!=
literal|null
operator|||
name|finalDecision
operator|==
literal|null
operator|||
name|finalDecision
operator|.
name|type
argument_list|()
operator|!=
name|Type
operator|.
name|YES
operator|:
literal|"a yes decision must have a node to assign the shard to"
assert|;
assert|assert
name|allocationStatus
operator|!=
literal|null
operator|||
name|finalDecision
operator|==
literal|null
operator|||
name|finalDecision
operator|.
name|type
argument_list|()
operator|==
name|Type
operator|.
name|YES
operator|:
literal|"only a yes decision should not have an allocation status"
assert|;
assert|assert
name|allocationId
operator|==
literal|null
operator|||
name|assignedNodeId
operator|!=
literal|null
operator|:
literal|"allocation id can only be null if the assigned node is null"
assert|;
name|this
operator|.
name|finalDecision
operator|=
name|finalDecision
expr_stmt|;
name|this
operator|.
name|allocationStatus
operator|=
name|allocationStatus
expr_stmt|;
name|this
operator|.
name|finalExplanation
operator|=
name|finalExplanation
expr_stmt|;
name|this
operator|.
name|assignedNodeId
operator|=
name|assignedNodeId
expr_stmt|;
name|this
operator|.
name|allocationId
operator|=
name|allocationId
expr_stmt|;
name|this
operator|.
name|nodeDecisions
operator|=
name|nodeDecisions
operator|!=
literal|null
condition|?
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|nodeDecisions
argument_list|)
else|:
literal|null
expr_stmt|;
block|}
comment|/**      * Creates a NO decision with the given {@link AllocationStatus} and explanation for the NO decision.      */
DECL|method|noDecision
specifier|public
specifier|static
name|UnassignedShardDecision
name|noDecision
parameter_list|(
name|AllocationStatus
name|allocationStatus
parameter_list|,
name|String
name|explanation
parameter_list|)
block|{
return|return
name|noDecision
argument_list|(
name|allocationStatus
argument_list|,
name|explanation
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Creates a NO decision with the given {@link AllocationStatus} and explanation for the NO decision,      * as well as the individual node-level decisions that comprised the final NO decision.      */
DECL|method|noDecision
specifier|public
specifier|static
name|UnassignedShardDecision
name|noDecision
parameter_list|(
name|AllocationStatus
name|allocationStatus
parameter_list|,
name|String
name|explanation
parameter_list|,
annotation|@
name|Nullable
name|Map
argument_list|<
name|String
argument_list|,
name|Decision
argument_list|>
name|nodeDecisions
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|explanation
argument_list|,
literal|"explanation must not be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|allocationStatus
argument_list|,
literal|"allocationStatus must not be null"
argument_list|)
expr_stmt|;
return|return
operator|new
name|UnassignedShardDecision
argument_list|(
name|Decision
operator|.
name|NO
argument_list|,
name|allocationStatus
argument_list|,
name|explanation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|nodeDecisions
argument_list|)
return|;
block|}
comment|/**      * Creates a THROTTLE decision with the given explanation and individual node-level decisions that      * comprised the final THROTTLE decision.      */
DECL|method|throttleDecision
specifier|public
specifier|static
name|UnassignedShardDecision
name|throttleDecision
parameter_list|(
name|String
name|explanation
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Decision
argument_list|>
name|nodeDecisions
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|explanation
argument_list|,
literal|"explanation must not be null"
argument_list|)
expr_stmt|;
return|return
operator|new
name|UnassignedShardDecision
argument_list|(
name|Decision
operator|.
name|THROTTLE
argument_list|,
name|AllocationStatus
operator|.
name|DECIDERS_THROTTLED
argument_list|,
name|explanation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|nodeDecisions
argument_list|)
return|;
block|}
comment|/**      * Creates a YES decision with the given explanation and individual node-level decisions that      * comprised the final YES decision, along with the node id to which the shard is assigned and      * the allocation id for the shard, if available.      */
DECL|method|yesDecision
specifier|public
specifier|static
name|UnassignedShardDecision
name|yesDecision
parameter_list|(
name|String
name|explanation
parameter_list|,
name|String
name|assignedNodeId
parameter_list|,
annotation|@
name|Nullable
name|String
name|allocationId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Decision
argument_list|>
name|nodeDecisions
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|explanation
argument_list|,
literal|"explanation must not be null"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|assignedNodeId
argument_list|,
literal|"assignedNodeId must not be null"
argument_list|)
expr_stmt|;
return|return
operator|new
name|UnassignedShardDecision
argument_list|(
name|Decision
operator|.
name|YES
argument_list|,
literal|null
argument_list|,
name|explanation
argument_list|,
name|assignedNodeId
argument_list|,
name|allocationId
argument_list|,
name|nodeDecisions
argument_list|)
return|;
block|}
comment|/**      * Returns<code>true</code> if a decision was taken by the allocator, {@code false} otherwise.      * If no decision was taken, then the rest of the fields in this object are meaningless and return {@code null}.      */
DECL|method|isDecisionTaken
specifier|public
name|boolean
name|isDecisionTaken
parameter_list|()
block|{
return|return
name|finalDecision
operator|!=
literal|null
return|;
block|}
comment|/**      * Returns the final decision made by the allocator on whether to assign the unassigned shard.      * This value can only be {@code null} if {@link #isDecisionTaken()} returns {@code false}.      */
annotation|@
name|Nullable
DECL|method|getFinalDecision
specifier|public
name|Decision
name|getFinalDecision
parameter_list|()
block|{
return|return
name|finalDecision
return|;
block|}
comment|/**      * Returns the final decision made by the allocator on whether to assign the unassigned shard.      * Only call this method if {@link #isDecisionTaken()} returns {@code true}, otherwise it will      * throw an {@code IllegalArgumentException}.      */
DECL|method|getFinalDecisionSafe
specifier|public
name|Decision
name|getFinalDecisionSafe
parameter_list|()
block|{
if|if
condition|(
name|isDecisionTaken
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"decision must have been taken in order to return the final decision"
argument_list|)
throw|;
block|}
return|return
name|finalDecision
return|;
block|}
comment|/**      * Returns the status of an unsuccessful allocation attempt.  This value will be {@code null} if      * no decision was taken or if the decision was {@link Decision.Type#YES}.      */
annotation|@
name|Nullable
DECL|method|getAllocationStatus
specifier|public
name|AllocationStatus
name|getAllocationStatus
parameter_list|()
block|{
return|return
name|allocationStatus
return|;
block|}
comment|/**      * Returns the free-text explanation for the reason behind the decision taken in {@link #getFinalDecision()}.      */
annotation|@
name|Nullable
DECL|method|getFinalExplanation
specifier|public
name|String
name|getFinalExplanation
parameter_list|()
block|{
return|return
name|finalExplanation
return|;
block|}
comment|/**      * Returns the free-text explanation for the reason behind the decision taken in {@link #getFinalDecision()}.      * Only call this method if {@link #isDecisionTaken()} returns {@code true}, otherwise it will      * throw an {@code IllegalArgumentException}.      */
DECL|method|getFinalExplanationSafe
specifier|public
name|String
name|getFinalExplanationSafe
parameter_list|()
block|{
if|if
condition|(
name|isDecisionTaken
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"decision must have been taken in order to return the final explanation"
argument_list|)
throw|;
block|}
return|return
name|finalExplanation
return|;
block|}
comment|/**      * Get the node id that the allocator will assign the shard to, unless {@link #getFinalDecision()} returns      * a value other than {@link Decision.Type#YES}, in which case this returns {@code null}.      */
annotation|@
name|Nullable
DECL|method|getAssignedNodeId
specifier|public
name|String
name|getAssignedNodeId
parameter_list|()
block|{
return|return
name|assignedNodeId
return|;
block|}
comment|/**      * Gets the allocation id for the existing shard copy that the allocator is assigning the shard to.      * This method returns a non-null value iff {@link #getAssignedNodeId()} returns a non-null value      * and the node on which the shard is assigned already has a shard copy with an in-sync allocation id      * that we can re-use.      */
annotation|@
name|Nullable
DECL|method|getAllocationId
specifier|public
name|String
name|getAllocationId
parameter_list|()
block|{
return|return
name|allocationId
return|;
block|}
comment|/**      * Gets the individual node-level decisions that went into making the final decision as represented by      * {@link #getFinalDecision()}.  The map that is returned has the node id as the key and a {@link Decision}      * as the decision for the given node.      */
annotation|@
name|Nullable
DECL|method|getNodeDecisions
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Decision
argument_list|>
name|getNodeDecisions
parameter_list|()
block|{
return|return
name|nodeDecisions
return|;
block|}
block|}
end_class

end_unit
