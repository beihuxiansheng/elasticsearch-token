begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.indices.flush
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|flush
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * Result for all copies of a shard  */
end_comment

begin_class
DECL|class|ShardsSyncedFlushResult
specifier|public
class|class
name|ShardsSyncedFlushResult
block|{
DECL|field|failureReason
specifier|private
name|String
name|failureReason
decl_stmt|;
DECL|field|shardResponses
specifier|private
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|shardResponses
decl_stmt|;
DECL|field|syncId
specifier|private
name|String
name|syncId
decl_stmt|;
DECL|field|shardId
specifier|private
name|ShardId
name|shardId
decl_stmt|;
comment|// some shards may be unassigned, so we need this as state
DECL|field|totalShards
specifier|private
name|int
name|totalShards
decl_stmt|;
DECL|method|ShardsSyncedFlushResult
specifier|public
name|ShardsSyncedFlushResult
parameter_list|()
block|{     }
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
comment|/**      * failure constructor      */
DECL|method|ShardsSyncedFlushResult
specifier|public
name|ShardsSyncedFlushResult
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|String
name|failureReason
parameter_list|)
block|{
name|this
operator|.
name|syncId
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|failureReason
operator|=
name|failureReason
expr_stmt|;
name|this
operator|.
name|shardResponses
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|totalShards
operator|=
name|totalShards
expr_stmt|;
block|}
comment|/**      * success constructor      */
DECL|method|ShardsSyncedFlushResult
specifier|public
name|ShardsSyncedFlushResult
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|syncId
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|shardResponses
parameter_list|)
block|{
name|this
operator|.
name|failureReason
operator|=
literal|null
expr_stmt|;
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|this
operator|.
name|shardResponses
operator|=
name|builder
operator|.
name|putAll
argument_list|(
name|shardResponses
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|syncId
operator|=
name|syncId
expr_stmt|;
name|this
operator|.
name|totalShards
operator|=
name|totalShards
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
comment|/**      * @return true if the operation failed before reaching step three of synced flush. {@link #failureReason()} can be used for      * more details      */
DECL|method|failed
specifier|public
name|boolean
name|failed
parameter_list|()
block|{
return|return
name|failureReason
operator|!=
literal|null
return|;
block|}
comment|/**      * @return the reason for the failure if synced flush failed before step three of synced flush      */
DECL|method|failureReason
specifier|public
name|String
name|failureReason
parameter_list|()
block|{
return|return
name|failureReason
return|;
block|}
DECL|method|syncId
specifier|public
name|String
name|syncId
parameter_list|()
block|{
return|return
name|syncId
return|;
block|}
comment|/**      * @return total number of shards for which a sync attempt was made      */
DECL|method|totalShards
specifier|public
name|int
name|totalShards
parameter_list|()
block|{
return|return
name|totalShards
return|;
block|}
comment|/**      * @return total number of successful shards      */
DECL|method|successfulShards
specifier|public
name|int
name|successfulShards
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
name|result
range|:
name|shardResponses
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|result
operator|.
name|success
argument_list|()
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|i
return|;
block|}
comment|/**      * @return an array of shard failures      */
DECL|method|failedShards
specifier|public
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|failedShards
parameter_list|()
block|{
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|failures
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|result
range|:
name|shardResponses
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|result
operator|.
name|getValue
argument_list|()
operator|.
name|success
argument_list|()
operator|==
literal|false
condition|)
block|{
name|failures
operator|.
name|put
argument_list|(
name|result
operator|.
name|getKey
argument_list|()
argument_list|,
name|result
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|failures
return|;
block|}
comment|/**      * @return Individual responses for each shard copy with a detailed failure message if the copy failed to perform the synced flush.      * Empty if synced flush failed before step three.      */
DECL|method|shardResponses
specifier|public
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|shardResponses
parameter_list|()
block|{
return|return
name|shardResponses
return|;
block|}
comment|//    @Override
comment|//    public void writeTo(StreamOutput out) throws IOException {
comment|//        super.writeTo(out);
comment|//        out.writeOptionalString(failureReason);
comment|//        out.writeOptionalString(syncId);
comment|//        out.writeVInt(totalShards);
comment|//        out.writeVInt(shardResponses.size());
comment|//        for (Map.Entry<ShardRouting, SyncedFlushService.SyncedFlushResponse> result : shardResponses.entrySet()) {
comment|//            result.getKey().writeTo(out);
comment|//            result.getValue().writeTo(out);
comment|//        }
comment|//        shardId.writeTo(out);
comment|//    }
comment|//    @Override
comment|//    public void readFrom(StreamInput in) throws IOException {
comment|//        super.readFrom(in);
comment|//        failureReason = in.readOptionalString();
comment|//        syncId = in.readOptionalString();
comment|//        totalShards = in.readVInt();
comment|//        int size = in.readVInt();
comment|//        ImmutableMap.Builder<ShardRouting, SyncedFlushService.SyncedFlushResponse> builder = ImmutableMap.builder();
comment|//        for (int i = 0; i< size; i++) {
comment|//            ImmutableShardRouting shardRouting = ImmutableShardRouting.readShardRoutingEntry(in);
comment|//            SyncedFlushService.SyncedFlushResponse syncedFlushRsponse = new SyncedFlushService.SyncedFlushResponse();
comment|//            syncedFlushRsponse.readFrom(in);
comment|//            builder.put(shardRouting, syncedFlushRsponse);
comment|//        }
comment|//        shardResponses = builder.build();
comment|//        shardId = ShardId.readShardId(in);
comment|//    }
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
block|}
end_class

end_unit

