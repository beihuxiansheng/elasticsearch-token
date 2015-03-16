begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.snapshots
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|snapshots
package|;
end_package

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
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ShardOperationFailedException
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
name|Streamable
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
name|rest
operator|.
name|RestStatus
import|;
end_import

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
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * Information about snapshot  */
end_comment

begin_class
DECL|class|SnapshotInfo
specifier|public
class|class
name|SnapshotInfo
implements|implements
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|DATE_TIME_FORMATTER
specifier|private
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
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|state
specifier|private
name|SnapshotState
name|state
decl_stmt|;
DECL|field|reason
specifier|private
name|String
name|reason
decl_stmt|;
DECL|field|indices
specifier|private
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
name|long
name|endTime
decl_stmt|;
DECL|field|totalShards
specifier|private
name|int
name|totalShards
decl_stmt|;
DECL|field|successfulShards
specifier|private
name|int
name|successfulShards
decl_stmt|;
DECL|field|shardFailures
specifier|private
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
decl_stmt|;
DECL|method|SnapshotInfo
name|SnapshotInfo
parameter_list|()
block|{      }
comment|/**      * Creates a new snapshot information from a {@link Snapshot}      *      * @param snapshot snapshot information returned by repository      */
DECL|method|SnapshotInfo
specifier|public
name|SnapshotInfo
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|name
operator|=
name|snapshot
operator|.
name|name
argument_list|()
expr_stmt|;
name|state
operator|=
name|snapshot
operator|.
name|state
argument_list|()
expr_stmt|;
name|reason
operator|=
name|snapshot
operator|.
name|reason
argument_list|()
expr_stmt|;
name|indices
operator|=
name|snapshot
operator|.
name|indices
argument_list|()
expr_stmt|;
name|startTime
operator|=
name|snapshot
operator|.
name|startTime
argument_list|()
expr_stmt|;
name|endTime
operator|=
name|snapshot
operator|.
name|endTime
argument_list|()
expr_stmt|;
name|totalShards
operator|=
name|snapshot
operator|.
name|totalShard
argument_list|()
expr_stmt|;
name|successfulShards
operator|=
name|snapshot
operator|.
name|successfulShards
argument_list|()
expr_stmt|;
name|shardFailures
operator|=
name|snapshot
operator|.
name|shardFailures
argument_list|()
expr_stmt|;
block|}
comment|/**      * Returns snapshot name      *      * @return snapshot name      */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Returns snapshot state      *      * @return snapshot state      */
DECL|method|state
specifier|public
name|SnapshotState
name|state
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**      * Returns snapshot failure reason      *      * @return snapshot failure reason      */
DECL|method|reason
specifier|public
name|String
name|reason
parameter_list|()
block|{
return|return
name|reason
return|;
block|}
comment|/**      * Returns indices that were included into this snapshot      *      * @return list of indices      */
DECL|method|indices
specifier|public
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|()
block|{
return|return
name|indices
return|;
block|}
comment|/**      * Returns time when snapshot started      *      * @return snapshot start time      */
DECL|method|startTime
specifier|public
name|long
name|startTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
comment|/**      * Returns time when snapshot ended      *<p/>      * Can be 0L if snapshot is still running      *      * @return snapshot end time      */
DECL|method|endTime
specifier|public
name|long
name|endTime
parameter_list|()
block|{
return|return
name|endTime
return|;
block|}
comment|/**      * Returns total number of shards that were snapshotted      *      * @return number of shards      */
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
comment|/**      * Number of failed shards      *      * @return number of failed shards      */
DECL|method|failedShards
specifier|public
name|int
name|failedShards
parameter_list|()
block|{
return|return
name|totalShards
operator|-
name|successfulShards
return|;
block|}
comment|/**      * Returns total number of shards that were successfully snapshotted      *      * @return number of successful shards      */
DECL|method|successfulShards
specifier|public
name|int
name|successfulShards
parameter_list|()
block|{
return|return
name|successfulShards
return|;
block|}
comment|/**      * Returns shard failures      *      * @return shard failures      */
DECL|method|shardFailures
specifier|public
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|()
block|{
return|return
name|shardFailures
return|;
block|}
comment|/**      * Returns snapshot REST status      */
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
if|if
condition|(
name|state
operator|==
name|SnapshotState
operator|.
name|FAILED
condition|)
block|{
return|return
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
return|;
block|}
if|if
condition|(
name|shardFailures
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|RestStatus
operator|.
name|OK
return|;
block|}
return|return
name|RestStatus
operator|.
name|status
argument_list|(
name|successfulShards
argument_list|,
name|totalShards
argument_list|,
name|shardFailures
operator|.
name|toArray
argument_list|(
operator|new
name|ShardOperationFailedException
index|[
name|shardFailures
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|INDICES
specifier|static
specifier|final
name|XContentBuilderString
name|INDICES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"indices"
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
DECL|field|REASON
specifier|static
specifier|final
name|XContentBuilderString
name|REASON
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"reason"
argument_list|)
decl_stmt|;
DECL|field|START_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|START_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"start_time"
argument_list|)
decl_stmt|;
DECL|field|START_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|START_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"start_time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|END_TIME
specifier|static
specifier|final
name|XContentBuilderString
name|END_TIME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"end_time"
argument_list|)
decl_stmt|;
DECL|field|END_TIME_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|END_TIME_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"end_time_in_millis"
argument_list|)
decl_stmt|;
DECL|field|DURATION
specifier|static
specifier|final
name|XContentBuilderString
name|DURATION
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"duration"
argument_list|)
decl_stmt|;
DECL|field|DURATION_IN_MILLIS
specifier|static
specifier|final
name|XContentBuilderString
name|DURATION_IN_MILLIS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"duration_in_millis"
argument_list|)
decl_stmt|;
DECL|field|FAILURES
specifier|static
specifier|final
name|XContentBuilderString
name|FAILURES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failures"
argument_list|)
decl_stmt|;
DECL|field|SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shards"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|FAILED
specifier|static
specifier|final
name|XContentBuilderString
name|FAILED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failed"
argument_list|)
decl_stmt|;
DECL|field|SUCCESSFUL
specifier|static
specifier|final
name|XContentBuilderString
name|SUCCESSFUL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"successful"
argument_list|)
decl_stmt|;
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
literal|"snapshot"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|INDICES
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|STATE
argument_list|,
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|reason
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REASON
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|startTime
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME
argument_list|,
name|DATE_TIME_FORMATTER
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|startTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME_IN_MILLIS
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|endTime
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|END_TIME
argument_list|,
name|DATE_TIME_FORMATTER
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|endTime
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|END_TIME_IN_MILLIS
argument_list|,
name|endTime
argument_list|)
expr_stmt|;
name|builder
operator|.
name|timeValueField
argument_list|(
name|Fields
operator|.
name|DURATION_IN_MILLIS
argument_list|,
name|Fields
operator|.
name|DURATION
argument_list|,
name|endTime
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|FAILURES
argument_list|)
expr_stmt|;
for|for
control|(
name|SnapshotShardFailure
name|shardFailure
range|:
name|shardFailures
control|)
block|{
name|SnapshotShardFailure
operator|.
name|toXContent
argument_list|(
name|shardFailure
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|totalShards
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FAILED
argument_list|,
name|failedShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUCCESSFUL
argument_list|,
name|successfulShards
argument_list|)
expr_stmt|;
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
name|name
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|indicesListBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|indicesListBuilder
operator|.
name|add
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indices
operator|=
name|indicesListBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
name|state
operator|=
name|SnapshotState
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
name|reason
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|startTime
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|endTime
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|totalShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|successfulShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|size
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|failureBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|failureBuilder
operator|.
name|add
argument_list|(
name|SnapshotShardFailure
operator|.
name|readSnapshotShardFailure
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shardFailures
operator|=
name|failureBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|shardFailures
operator|=
name|ImmutableList
operator|.
name|of
argument_list|()
expr_stmt|;
block|}
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
name|name
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|indices
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeByte
argument_list|(
name|state
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|startTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|endTime
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|totalShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|successfulShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardFailures
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SnapshotShardFailure
name|failure
range|:
name|shardFailures
control|)
block|{
name|failure
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reads snapshot information from stream input      *      * @param in stream input      * @return deserialized snapshot info      * @throws IOException      */
DECL|method|readSnapshotInfo
specifier|public
specifier|static
name|SnapshotInfo
name|readSnapshotInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotInfo
name|snapshotInfo
init|=
operator|new
name|SnapshotInfo
argument_list|()
decl_stmt|;
name|snapshotInfo
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|snapshotInfo
return|;
block|}
comment|/**      * Reads optional snapshot information from stream input      *      * @param in stream input      * @return deserialized snapshot info or null      * @throws IOException      */
DECL|method|readOptionalSnapshotInfo
specifier|public
specifier|static
name|SnapshotInfo
name|readOptionalSnapshotInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|in
operator|.
name|readOptionalStreamable
argument_list|(
operator|new
name|SnapshotInfo
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

