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
name|ExceptionsHelper
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
name|joda
operator|.
name|FormatDateTimeFormatter
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
name|joda
operator|.
name|Joda
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
name|settings
operator|.
name|Settings
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
name|unit
operator|.
name|TimeValue
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
comment|/**  * Holds additional information as to why the shard is in unassigned state.  */
end_comment

begin_class
DECL|class|UnassignedInfo
specifier|public
class|class
name|UnassignedInfo
implements|implements
name|ToXContent
implements|,
name|Writeable
argument_list|<
name|UnassignedInfo
argument_list|>
block|{
DECL|field|DATE_TIME_FORMATTER
specifier|public
specifier|static
specifier|final
name|FormatDateTimeFormatter
name|DATE_TIME_FORMATTER
init|=
name|Joda
operator|.
name|forPattern
argument_list|(
literal|"dateOptionalTime"
argument_list|)
decl_stmt|;
DECL|field|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
init|=
literal|"index.unassigned.node_left.delayed_timeout"
decl_stmt|;
DECL|field|DEFAULT_DELAYED_NODE_LEFT_TIMEOUT
specifier|private
specifier|static
specifier|final
name|TimeValue
name|DEFAULT_DELAYED_NODE_LEFT_TIMEOUT
init|=
name|TimeValue
operator|.
name|timeValueMinutes
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|/**      * Reason why the shard is in unassigned state.      *<p/>      * Note, ordering of the enum is important, make sure to add new values      * at the end and handle version serialization properly.      */
DECL|enum|Reason
specifier|public
enum|enum
name|Reason
block|{
comment|/**          * Unassigned as a result of an API creation of an index.          */
DECL|enum constant|INDEX_CREATED
name|INDEX_CREATED
block|,
comment|/**          * Unassigned as a result of a full cluster recovery.          */
DECL|enum constant|CLUSTER_RECOVERED
name|CLUSTER_RECOVERED
block|,
comment|/**          * Unassigned as a result of opening a closed index.          */
DECL|enum constant|INDEX_REOPENED
name|INDEX_REOPENED
block|,
comment|/**          * Unassigned as a result of importing a dangling index.          */
DECL|enum constant|DANGLING_INDEX_IMPORTED
name|DANGLING_INDEX_IMPORTED
block|,
comment|/**          * Unassigned as a result of restoring into a new index.          */
DECL|enum constant|NEW_INDEX_RESTORED
name|NEW_INDEX_RESTORED
block|,
comment|/**          * Unassigned as a result of restoring into a closed index.          */
DECL|enum constant|EXISTING_INDEX_RESTORED
name|EXISTING_INDEX_RESTORED
block|,
comment|/**          * Unassigned as a result of explicit addition of a replica.          */
DECL|enum constant|REPLICA_ADDED
name|REPLICA_ADDED
block|,
comment|/**          * Unassigned as a result of a failed allocation of the shard.          */
DECL|enum constant|ALLOCATION_FAILED
name|ALLOCATION_FAILED
block|,
comment|/**          * Unassigned as a result of the node hosting it leaving the cluster.          */
DECL|enum constant|NODE_LEFT
name|NODE_LEFT
block|,
comment|/**          * Unassigned as a result of explicit cancel reroute command.          */
DECL|enum constant|REROUTE_CANCELLED
name|REROUTE_CANCELLED
block|,
comment|/**          * When a shard moves from started back to initializing, for example, during shadow replica          */
DECL|enum constant|REINITIALIZED
name|REINITIALIZED
block|,
comment|/**          * A better replica location is identified and causes the existing replica allocation to be cancelled.          */
DECL|enum constant|REALLOCATED_REPLICA
name|REALLOCATED_REPLICA
block|;     }
DECL|field|reason
specifier|private
specifier|final
name|Reason
name|reason
decl_stmt|;
DECL|field|timestamp
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|message
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
DECL|field|failure
specifier|private
specifier|final
name|Throwable
name|failure
decl_stmt|;
DECL|method|UnassignedInfo
specifier|public
name|UnassignedInfo
parameter_list|(
name|Reason
name|reason
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|reason
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|message
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|UnassignedInfo
specifier|public
name|UnassignedInfo
parameter_list|(
name|Reason
name|reason
parameter_list|,
annotation|@
name|Nullable
name|String
name|message
parameter_list|,
annotation|@
name|Nullable
name|Throwable
name|failure
parameter_list|)
block|{
name|this
argument_list|(
name|reason
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|message
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|method|UnassignedInfo
specifier|private
name|UnassignedInfo
parameter_list|(
name|Reason
name|reason
parameter_list|,
name|long
name|timestamp
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|failure
parameter_list|)
block|{
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|failure
operator|=
name|failure
expr_stmt|;
assert|assert
operator|!
operator|(
name|message
operator|==
literal|null
operator|&&
name|failure
operator|!=
literal|null
operator|)
operator|:
literal|"provide a message if a failure exception is provided"
assert|;
block|}
DECL|method|UnassignedInfo
name|UnassignedInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|reason
operator|=
name|Reason
operator|.
name|values
argument_list|()
index|[
operator|(
name|int
operator|)
name|in
operator|.
name|readByte
argument_list|()
index|]
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|this
operator|.
name|failure
operator|=
name|in
operator|.
name|readThrowable
argument_list|()
expr_stmt|;
block|}
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
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|reason
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|timestamp
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeThrowable
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
DECL|method|readFrom
specifier|public
name|UnassignedInfo
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|UnassignedInfo
argument_list|(
name|in
argument_list|)
return|;
block|}
comment|/**      * The reason why the shard is unassigned.      */
DECL|method|getReason
specifier|public
name|Reason
name|getReason
parameter_list|()
block|{
return|return
name|this
operator|.
name|reason
return|;
block|}
comment|/**      * The timestamp in milliseconds since epoch. Note, we use timestamp here since      * we want to make sure its preserved across node serializations. Extra care need      * to be made if its used to calculate diff (handle negative values) in case of      * time drift.      */
DECL|method|getTimestampInMillis
specifier|public
name|long
name|getTimestampInMillis
parameter_list|()
block|{
return|return
name|this
operator|.
name|timestamp
return|;
block|}
comment|/**      * Returns optional details explaining the reasons.      */
annotation|@
name|Nullable
DECL|method|getMessage
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|this
operator|.
name|message
return|;
block|}
comment|/**      * Returns additional failure exception details if exists.      */
annotation|@
name|Nullable
DECL|method|getFailure
specifier|public
name|Throwable
name|getFailure
parameter_list|()
block|{
return|return
name|failure
return|;
block|}
comment|/**      * Builds a string representation of the message and the failure if exists.      */
annotation|@
name|Nullable
DECL|method|getDetails
specifier|public
name|String
name|getDetails
parameter_list|()
block|{
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|message
operator|+
operator|(
name|failure
operator|==
literal|null
condition|?
literal|""
else|:
literal|", failure "
operator|+
name|ExceptionsHelper
operator|.
name|detailedMessage
argument_list|(
name|failure
argument_list|)
operator|)
return|;
block|}
comment|/**      * The allocation delay value associated with the index (defaulting to node settings if not set).      */
DECL|method|getAllocationDelayTimeoutSetting
specifier|public
name|long
name|getAllocationDelayTimeoutSetting
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
if|if
condition|(
name|reason
operator|!=
name|Reason
operator|.
name|NODE_LEFT
condition|)
block|{
return|return
literal|0
return|;
block|}
name|TimeValue
name|delayTimeout
init|=
name|indexSettings
operator|.
name|getAsTime
argument_list|(
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|settings
operator|.
name|getAsTime
argument_list|(
name|INDEX_DELAYED_NODE_LEFT_TIMEOUT_SETTING
argument_list|,
name|DEFAULT_DELAYED_NODE_LEFT_TIMEOUT
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|Math
operator|.
name|max
argument_list|(
literal|0l
argument_list|,
name|delayTimeout
operator|.
name|millis
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * The time in millisecond until this unassigned shard can be reassigned.      */
DECL|method|getDelayAllocationExpirationIn
specifier|public
name|long
name|getDelayAllocationExpirationIn
parameter_list|(
name|long
name|unassignedShardsAllocatedTimestamp
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|Settings
name|indexSettings
parameter_list|)
block|{
name|long
name|delayTimeout
init|=
name|getAllocationDelayTimeoutSetting
argument_list|(
name|settings
argument_list|,
name|indexSettings
argument_list|)
decl_stmt|;
if|if
condition|(
name|delayTimeout
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|long
name|delta
init|=
name|unassignedShardsAllocatedTimestamp
operator|-
name|timestamp
decl_stmt|;
comment|// account for time drift, treat it as no timeout
if|if
condition|(
name|delta
operator|<
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|delayTimeout
operator|-
name|delta
return|;
block|}
comment|/**      * Returns the number of shards that are unassigned and currently being delayed.      */
DECL|method|getNumberOfDelayedUnassigned
specifier|public
specifier|static
name|int
name|getNumberOfDelayedUnassigned
parameter_list|(
name|long
name|unassignedShardsAllocatedTimestamp
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|)
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|==
literal|false
condition|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|shard
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|delay
init|=
name|shard
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getDelayAllocationExpirationIn
argument_list|(
name|unassignedShardsAllocatedTimestamp
argument_list|,
name|settings
argument_list|,
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|delay
operator|>
literal|0
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**      * Finds the smallest delay expiration setting of an unassigned shard. Returns 0 if there are none.      */
DECL|method|findSmallestDelayedAllocationSetting
specifier|public
specifier|static
name|long
name|findSmallestDelayedAllocationSetting
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|long
name|nextDelaySetting
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|)
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|==
literal|false
condition|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|shard
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|delayTimeoutSetting
init|=
name|shard
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getAllocationDelayTimeoutSetting
argument_list|(
name|settings
argument_list|,
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|delayTimeoutSetting
operator|>
literal|0
operator|&&
name|delayTimeoutSetting
operator|<
name|nextDelaySetting
condition|)
block|{
name|nextDelaySetting
operator|=
name|delayTimeoutSetting
expr_stmt|;
block|}
block|}
block|}
return|return
name|nextDelaySetting
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|?
literal|0l
else|:
name|nextDelaySetting
return|;
block|}
comment|/**      * Finds the next (closest) delay expiration of an unassigned shard. Returns 0 if there are none.      */
DECL|method|findNextDelayedAllocationIn
specifier|public
specifier|static
name|long
name|findNextDelayedAllocationIn
parameter_list|(
name|long
name|unassignedShardsAllocatedTimestamp
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
name|long
name|nextDelay
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|ShardRouting
name|shard
range|:
name|state
operator|.
name|routingTable
argument_list|()
operator|.
name|shardsWithState
argument_list|(
name|ShardRoutingState
operator|.
name|UNASSIGNED
argument_list|)
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|primary
argument_list|()
operator|==
literal|false
condition|)
block|{
name|IndexMetaData
name|indexMetaData
init|=
name|state
operator|.
name|metaData
argument_list|()
operator|.
name|index
argument_list|(
name|shard
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|nextShardDelay
init|=
name|shard
operator|.
name|unassignedInfo
argument_list|()
operator|.
name|getDelayAllocationExpirationIn
argument_list|(
name|unassignedShardsAllocatedTimestamp
argument_list|,
name|settings
argument_list|,
name|indexMetaData
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextShardDelay
operator|>
literal|0
operator|&&
name|nextShardDelay
operator|<
name|nextDelay
condition|)
block|{
name|nextDelay
operator|=
name|nextShardDelay
expr_stmt|;
block|}
block|}
block|}
return|return
name|nextDelay
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|?
literal|0l
else|:
name|nextDelay
return|;
block|}
DECL|method|shortSummary
specifier|public
name|String
name|shortSummary
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"[reason="
argument_list|)
operator|.
name|append
argument_list|(
name|reason
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", at["
argument_list|)
operator|.
name|append
argument_list|(
name|DATE_TIME_FORMATTER
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|timestamp
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|String
name|details
init|=
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|details
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", details["
argument_list|)
operator|.
name|append
argument_list|(
name|details
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"unassigned_info["
operator|+
name|shortSummary
argument_list|()
operator|+
literal|"]"
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
argument_list|(
literal|"unassigned_info"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"reason"
argument_list|,
name|reason
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"at"
argument_list|,
name|DATE_TIME_FORMATTER
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|timestamp
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|details
init|=
name|getDetails
argument_list|()
decl_stmt|;
if|if
condition|(
name|details
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"details"
argument_list|,
name|details
argument_list|)
expr_stmt|;
block|}
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

