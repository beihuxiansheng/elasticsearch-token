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

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * Represent information about snapshot  */
end_comment

begin_class
DECL|class|Snapshot
specifier|public
class|class
name|Snapshot
implements|implements
name|Comparable
argument_list|<
name|Snapshot
argument_list|>
implements|,
name|ToXContent
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|SnapshotState
name|state
decl_stmt|;
DECL|field|reason
specifier|private
specifier|final
name|String
name|reason
decl_stmt|;
DECL|field|indices
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
DECL|field|endTime
specifier|private
specifier|final
name|long
name|endTime
decl_stmt|;
DECL|field|totalShard
specifier|private
specifier|final
name|int
name|totalShard
decl_stmt|;
DECL|field|successfulShards
specifier|private
specifier|final
name|int
name|successfulShards
decl_stmt|;
DECL|field|shardFailures
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
decl_stmt|;
DECL|field|NO_FAILURES
specifier|private
specifier|final
specifier|static
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|NO_FAILURES
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|method|Snapshot
specifier|private
name|Snapshot
parameter_list|(
name|String
name|name
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|,
name|SnapshotState
name|state
parameter_list|,
name|String
name|reason
parameter_list|,
name|Version
name|version
parameter_list|,
name|long
name|startTime
parameter_list|,
name|long
name|endTime
parameter_list|,
name|int
name|totalShard
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|)
block|{
assert|assert
name|name
operator|!=
literal|null
assert|;
assert|assert
name|indices
operator|!=
literal|null
assert|;
assert|assert
name|state
operator|!=
literal|null
assert|;
assert|assert
name|shardFailures
operator|!=
literal|null
assert|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|indices
operator|=
name|indices
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|reason
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|startTime
expr_stmt|;
name|this
operator|.
name|endTime
operator|=
name|endTime
expr_stmt|;
name|this
operator|.
name|totalShard
operator|=
name|totalShard
expr_stmt|;
name|this
operator|.
name|successfulShards
operator|=
name|successfulShards
expr_stmt|;
name|this
operator|.
name|shardFailures
operator|=
name|shardFailures
expr_stmt|;
block|}
DECL|method|Snapshot
specifier|public
name|Snapshot
parameter_list|(
name|String
name|name
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|,
name|long
name|startTime
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|indices
argument_list|,
name|SnapshotState
operator|.
name|IN_PROGRESS
argument_list|,
literal|null
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|startTime
argument_list|,
literal|0L
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|NO_FAILURES
argument_list|)
expr_stmt|;
block|}
DECL|method|Snapshot
specifier|public
name|Snapshot
parameter_list|(
name|String
name|name
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
parameter_list|,
name|long
name|startTime
parameter_list|,
name|String
name|reason
parameter_list|,
name|long
name|endTime
parameter_list|,
name|int
name|totalShard
parameter_list|,
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|indices
argument_list|,
name|snapshotState
argument_list|(
name|reason
argument_list|,
name|shardFailures
argument_list|)
argument_list|,
name|reason
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|,
name|startTime
argument_list|,
name|endTime
argument_list|,
name|totalShard
argument_list|,
name|totalShard
operator|-
name|shardFailures
operator|.
name|size
argument_list|()
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
block|}
DECL|method|snapshotState
specifier|private
specifier|static
name|SnapshotState
name|snapshotState
parameter_list|(
name|String
name|reason
parameter_list|,
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
parameter_list|)
block|{
if|if
condition|(
name|reason
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|shardFailures
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|SnapshotState
operator|.
name|SUCCESS
return|;
block|}
else|else
block|{
return|return
name|SnapshotState
operator|.
name|PARTIAL
return|;
block|}
block|}
else|else
block|{
return|return
name|SnapshotState
operator|.
name|FAILED
return|;
block|}
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
comment|/**      * Returns current snapshot state      *      * @return snapshot state      */
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
comment|/**      * Returns reason for complete snapshot failure      *      * @return snapshot failure reason      */
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
comment|/**      * Returns version of Elasticsearch that was used to create this snapshot      *      * @return Elasticsearch version      */
DECL|method|version
specifier|public
name|Version
name|version
parameter_list|()
block|{
return|return
name|version
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
DECL|method|totalShard
specifier|public
name|int
name|totalShard
parameter_list|()
block|{
return|return
name|totalShard
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
comment|/**      * Returns shard failures      */
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
comment|/**      * Compares two snapshots by their start time      *      * @param o other snapshot      * @return the value {@code 0} if snapshots were created at the same time;      * a value less than {@code 0} if this snapshot was created before snapshot {@code o}; and      * a value greater than {@code 0} if this snapshot was created after snapshot {@code o};      */
annotation|@
name|Override
DECL|method|compareTo
specifier|public
name|int
name|compareTo
parameter_list|(
name|Snapshot
name|o
parameter_list|)
block|{
return|return
name|Long
operator|.
name|compare
argument_list|(
name|startTime
argument_list|,
name|o
operator|.
name|startTime
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
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Snapshot
name|that
init|=
operator|(
name|Snapshot
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|startTime
operator|!=
name|that
operator|.
name|startTime
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|that
operator|.
name|name
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
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
name|int
name|result
init|=
name|name
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
call|(
name|int
call|)
argument_list|(
name|startTime
operator|^
operator|(
name|startTime
operator|>>>
literal|32
operator|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|SNAPSHOT
specifier|static
specifier|final
name|XContentBuilderString
name|SNAPSHOT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"snapshot"
argument_list|)
decl_stmt|;
DECL|field|NAME
specifier|static
specifier|final
name|XContentBuilderString
name|NAME
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
DECL|field|VERSION_ID
specifier|static
specifier|final
name|XContentBuilderString
name|VERSION_ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"version_id"
argument_list|)
decl_stmt|;
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
DECL|field|TOTAL_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total_shards"
argument_list|)
decl_stmt|;
DECL|field|SUCCESSFUL_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|SUCCESSFUL_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"successful_shards"
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
name|ToXContent
operator|.
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
name|SNAPSHOT
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NAME
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|VERSION_ID
argument_list|,
name|version
operator|.
name|id
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
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|START_TIME
argument_list|,
name|startTime
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|END_TIME
argument_list|,
name|endTime
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL_SHARDS
argument_list|,
name|totalShard
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUCCESSFUL_SHARDS
argument_list|,
name|successfulShards
argument_list|)
expr_stmt|;
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
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|Snapshot
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|name
init|=
literal|null
decl_stmt|;
name|Version
name|version
init|=
name|Version
operator|.
name|CURRENT
decl_stmt|;
name|SnapshotState
name|state
init|=
name|SnapshotState
operator|.
name|IN_PROGRESS
decl_stmt|;
name|String
name|reason
init|=
literal|null
decl_stmt|;
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|long
name|startTime
init|=
literal|0
decl_stmt|;
name|long
name|endTime
init|=
literal|0
decl_stmt|;
name|int
name|totalShard
init|=
literal|0
decl_stmt|;
name|int
name|successfulShards
init|=
literal|0
decl_stmt|;
name|ImmutableList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailures
init|=
name|NO_FAILURES
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"snapshot"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
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
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
literal|"name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|name
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
literal|"state"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|state
operator|=
name|SnapshotState
operator|.
name|valueOf
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"reason"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|reason
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
literal|"start_time"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|startTime
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"end_time"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|endTime
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"total_shards"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|totalShard
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
literal|"successful_shards"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|successfulShards
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
literal|"version_id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|Version
operator|.
name|fromId
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_ARRAY
condition|)
block|{
if|if
condition|(
literal|"indices"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|indicesArray
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|indicesArray
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|indices
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|indicesArray
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"failures"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|ArrayList
argument_list|<
name|SnapshotShardFailure
argument_list|>
name|shardFailureArrayList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|shardFailureArrayList
operator|.
name|add
argument_list|(
name|SnapshotShardFailure
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shardFailures
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|shardFailureArrayList
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// It was probably created by newer version - ignoring
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
comment|// It was probably created by newer version - ignoring
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
operator|new
name|Snapshot
argument_list|(
name|name
argument_list|,
name|indices
argument_list|,
name|state
argument_list|,
name|reason
argument_list|,
name|version
argument_list|,
name|startTime
argument_list|,
name|endTime
argument_list|,
name|totalShard
argument_list|,
name|successfulShards
argument_list|,
name|shardFailures
argument_list|)
return|;
block|}
block|}
end_class

end_unit

