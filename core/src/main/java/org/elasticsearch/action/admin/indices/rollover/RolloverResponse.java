begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.rollover
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
name|rollover
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
name|ActionResponse
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
name|ToXContentObject
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_class
DECL|class|RolloverResponse
specifier|public
specifier|final
class|class
name|RolloverResponse
extends|extends
name|ActionResponse
implements|implements
name|ToXContentObject
block|{
DECL|field|NEW_INDEX
specifier|private
specifier|static
specifier|final
name|String
name|NEW_INDEX
init|=
literal|"new_index"
decl_stmt|;
DECL|field|OLD_INDEX
specifier|private
specifier|static
specifier|final
name|String
name|OLD_INDEX
init|=
literal|"old_index"
decl_stmt|;
DECL|field|DRY_RUN
specifier|private
specifier|static
specifier|final
name|String
name|DRY_RUN
init|=
literal|"dry_run"
decl_stmt|;
DECL|field|ROLLED_OVER
specifier|private
specifier|static
specifier|final
name|String
name|ROLLED_OVER
init|=
literal|"rolled_over"
decl_stmt|;
DECL|field|CONDITIONS
specifier|private
specifier|static
specifier|final
name|String
name|CONDITIONS
init|=
literal|"conditions"
decl_stmt|;
DECL|field|ACKNOWLEDGED
specifier|private
specifier|static
specifier|final
name|String
name|ACKNOWLEDGED
init|=
literal|"acknowledged"
decl_stmt|;
DECL|field|SHARDS_ACKED
specifier|private
specifier|static
specifier|final
name|String
name|SHARDS_ACKED
init|=
literal|"shards_acknowledged"
decl_stmt|;
DECL|field|oldIndex
specifier|private
name|String
name|oldIndex
decl_stmt|;
DECL|field|newIndex
specifier|private
name|String
name|newIndex
decl_stmt|;
DECL|field|conditionStatus
specifier|private
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|conditionStatus
decl_stmt|;
DECL|field|dryRun
specifier|private
name|boolean
name|dryRun
decl_stmt|;
DECL|field|rolledOver
specifier|private
name|boolean
name|rolledOver
decl_stmt|;
DECL|field|acknowledged
specifier|private
name|boolean
name|acknowledged
decl_stmt|;
DECL|field|shardsAcked
specifier|private
name|boolean
name|shardsAcked
decl_stmt|;
DECL|method|RolloverResponse
name|RolloverResponse
parameter_list|()
block|{     }
DECL|method|RolloverResponse
name|RolloverResponse
parameter_list|(
name|String
name|oldIndex
parameter_list|,
name|String
name|newIndex
parameter_list|,
name|Set
argument_list|<
name|Condition
operator|.
name|Result
argument_list|>
name|conditionResults
parameter_list|,
name|boolean
name|dryRun
parameter_list|,
name|boolean
name|rolledOver
parameter_list|,
name|boolean
name|acknowledged
parameter_list|,
name|boolean
name|shardsAcked
parameter_list|)
block|{
name|this
operator|.
name|oldIndex
operator|=
name|oldIndex
expr_stmt|;
name|this
operator|.
name|newIndex
operator|=
name|newIndex
expr_stmt|;
name|this
operator|.
name|dryRun
operator|=
name|dryRun
expr_stmt|;
name|this
operator|.
name|rolledOver
operator|=
name|rolledOver
expr_stmt|;
name|this
operator|.
name|acknowledged
operator|=
name|acknowledged
expr_stmt|;
name|this
operator|.
name|shardsAcked
operator|=
name|shardsAcked
expr_stmt|;
name|this
operator|.
name|conditionStatus
operator|=
name|conditionResults
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|result
lambda|->
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<>
argument_list|(
name|result
operator|.
name|condition
operator|.
name|toString
argument_list|()
argument_list|,
name|result
operator|.
name|matched
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the name of the index that the request alias was pointing to      */
DECL|method|getOldIndex
specifier|public
name|String
name|getOldIndex
parameter_list|()
block|{
return|return
name|oldIndex
return|;
block|}
comment|/**      * Returns the name of the index that the request alias currently points to      */
DECL|method|getNewIndex
specifier|public
name|String
name|getNewIndex
parameter_list|()
block|{
return|return
name|newIndex
return|;
block|}
comment|/**      * Returns the statuses of all the request conditions      */
DECL|method|getConditionStatus
specifier|public
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|getConditionStatus
parameter_list|()
block|{
return|return
name|conditionStatus
return|;
block|}
comment|/**      * Returns if the rollover execution was skipped even when conditions were met      */
DECL|method|isDryRun
specifier|public
name|boolean
name|isDryRun
parameter_list|()
block|{
return|return
name|dryRun
return|;
block|}
comment|/**      * Returns true if the rollover was not simulated and the conditions were met      */
DECL|method|isRolledOver
specifier|public
name|boolean
name|isRolledOver
parameter_list|()
block|{
return|return
name|rolledOver
return|;
block|}
comment|/**      * Returns true if the creation of the new rollover index and switching of the      * alias to the newly created index was successful, and returns false otherwise.      * If {@link #isDryRun()} is true, then this will also return false. If this      * returns false, then {@link #isShardsAcked()} will also return false.      */
DECL|method|isAcknowledged
specifier|public
name|boolean
name|isAcknowledged
parameter_list|()
block|{
return|return
name|acknowledged
return|;
block|}
comment|/**      * Returns true if the requisite number of shards were started in the newly      * created rollover index before returning.  If {@link #isAcknowledged()} is      * false, then this will also return false.      */
DECL|method|isShardsAcked
specifier|public
name|boolean
name|isShardsAcked
parameter_list|()
block|{
return|return
name|shardsAcked
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
name|oldIndex
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|newIndex
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|int
name|conditionSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|>
name|conditions
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|conditionSize
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
name|conditionSize
condition|;
name|i
operator|++
control|)
block|{
name|String
name|condition
init|=
name|in
operator|.
name|readString
argument_list|()
decl_stmt|;
name|boolean
name|satisfied
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
name|conditions
operator|.
name|add
argument_list|(
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<>
argument_list|(
name|condition
argument_list|,
name|satisfied
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|conditionStatus
operator|=
name|conditions
expr_stmt|;
name|dryRun
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|rolledOver
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|acknowledged
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|shardsAcked
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|oldIndex
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|newIndex
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|conditionStatus
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
name|String
argument_list|,
name|Boolean
argument_list|>
name|entry
range|:
name|conditionStatus
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|dryRun
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|rolledOver
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|acknowledged
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|shardsAcked
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
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|OLD_INDEX
argument_list|,
name|oldIndex
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|NEW_INDEX
argument_list|,
name|newIndex
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|ROLLED_OVER
argument_list|,
name|rolledOver
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|DRY_RUN
argument_list|,
name|dryRun
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|ACKNOWLEDGED
argument_list|,
name|acknowledged
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SHARDS_ACKED
argument_list|,
name|shardsAcked
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|CONDITIONS
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|entry
range|:
name|conditionStatus
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

