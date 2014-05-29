begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.snapshots.status
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|snapshots
operator|.
name|status
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
name|metadata
operator|.
name|SnapshotId
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
name|SnapshotMetaData
operator|.
name|State
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
name|XContentFactory
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
name|List
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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_comment
comment|/**  * Status of a snapshot  */
end_comment

begin_class
DECL|class|SnapshotStatus
specifier|public
class|class
name|SnapshotStatus
implements|implements
name|ToXContent
implements|,
name|Streamable
block|{
DECL|field|snapshotId
specifier|private
name|SnapshotId
name|snapshotId
decl_stmt|;
DECL|field|state
specifier|private
name|State
name|state
decl_stmt|;
DECL|field|shards
specifier|private
name|ImmutableList
argument_list|<
name|SnapshotIndexShardStatus
argument_list|>
name|shards
decl_stmt|;
DECL|field|indicesStatus
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|SnapshotIndexStatus
argument_list|>
name|indicesStatus
decl_stmt|;
DECL|field|shardsStats
specifier|private
name|SnapshotShardsStats
name|shardsStats
decl_stmt|;
DECL|field|stats
specifier|private
name|SnapshotStats
name|stats
decl_stmt|;
DECL|method|SnapshotStatus
name|SnapshotStatus
parameter_list|(
name|SnapshotId
name|snapshotId
parameter_list|,
name|State
name|state
parameter_list|,
name|ImmutableList
argument_list|<
name|SnapshotIndexShardStatus
argument_list|>
name|shards
parameter_list|)
block|{
name|this
operator|.
name|snapshotId
operator|=
name|snapshotId
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|shards
expr_stmt|;
name|shardsStats
operator|=
operator|new
name|SnapshotShardsStats
argument_list|(
name|shards
argument_list|)
expr_stmt|;
name|updateShardStats
argument_list|()
expr_stmt|;
block|}
DECL|method|SnapshotStatus
name|SnapshotStatus
parameter_list|()
block|{     }
comment|/**      * Returns snapshot id      */
DECL|method|getSnapshotId
specifier|public
name|SnapshotId
name|getSnapshotId
parameter_list|()
block|{
return|return
name|snapshotId
return|;
block|}
comment|/**      * Returns snapshot state      */
DECL|method|getState
specifier|public
name|State
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
comment|/**      * Returns list of snapshot shards      */
DECL|method|getShards
specifier|public
name|List
argument_list|<
name|SnapshotIndexShardStatus
argument_list|>
name|getShards
parameter_list|()
block|{
return|return
name|shards
return|;
block|}
DECL|method|getShardsStats
specifier|public
name|SnapshotShardsStats
name|getShardsStats
parameter_list|()
block|{
return|return
name|shardsStats
return|;
block|}
comment|/**      * Returns list of snapshot indices      */
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|SnapshotIndexStatus
argument_list|>
name|getIndices
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|indicesStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|this
operator|.
name|indicesStatus
return|;
block|}
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SnapshotIndexStatus
argument_list|>
name|indicesStatus
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|SnapshotIndexShardStatus
name|shard
range|:
name|shards
control|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|shard
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|index
range|:
name|indices
control|)
block|{
name|List
argument_list|<
name|SnapshotIndexShardStatus
argument_list|>
name|shards
init|=
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|SnapshotIndexShardStatus
name|shard
range|:
name|this
operator|.
name|shards
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|getIndex
argument_list|()
operator|.
name|equals
argument_list|(
name|index
argument_list|)
condition|)
block|{
name|shards
operator|.
name|add
argument_list|(
name|shard
argument_list|)
expr_stmt|;
block|}
block|}
name|indicesStatus
operator|.
name|put
argument_list|(
name|index
argument_list|,
operator|new
name|SnapshotIndexStatus
argument_list|(
name|index
argument_list|,
name|shards
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indicesStatus
operator|=
name|indicesStatus
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|indicesStatus
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
name|snapshotId
operator|=
name|SnapshotId
operator|.
name|readSnapshotId
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
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
name|SnapshotIndexShardStatus
argument_list|>
name|builder
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
name|builder
operator|.
name|add
argument_list|(
name|SnapshotIndexShardStatus
operator|.
name|readShardSnapshotStatus
argument_list|(
name|in
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|shards
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|updateShardStats
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
name|snapshotId
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
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
name|writeVInt
argument_list|(
name|shards
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SnapshotIndexShardStatus
name|shard
range|:
name|shards
control|)
block|{
name|shard
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Reads snapshot status from stream input      *      * @param in stream input      * @return deserialized snapshot status      * @throws IOException      */
DECL|method|readSnapshotStatus
specifier|public
specifier|static
name|SnapshotStatus
name|readSnapshotStatus
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotStatus
name|snapshotInfo
init|=
operator|new
name|SnapshotStatus
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
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|"{ \"error\" : \""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\"}"
return|;
block|}
block|}
comment|/**      * Returns number of files in the snapshot      */
DECL|method|getStats
specifier|public
name|SnapshotStats
name|getStats
parameter_list|()
block|{
return|return
name|stats
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
DECL|field|REPOSITORY
specifier|static
specifier|final
name|XContentBuilderString
name|REPOSITORY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"repository"
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
name|Fields
operator|.
name|SNAPSHOT
argument_list|,
name|snapshotId
operator|.
name|getSnapshot
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REPOSITORY
argument_list|,
name|snapshotId
operator|.
name|getRepository
argument_list|()
argument_list|)
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
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|shardsStats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|stats
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|INDICES
argument_list|)
expr_stmt|;
for|for
control|(
name|SnapshotIndexStatus
name|indexStatus
range|:
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|indexStatus
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
DECL|method|updateShardStats
specifier|private
name|void
name|updateShardStats
parameter_list|()
block|{
name|stats
operator|=
operator|new
name|SnapshotStats
argument_list|()
expr_stmt|;
name|shardsStats
operator|=
operator|new
name|SnapshotShardsStats
argument_list|(
name|shards
argument_list|)
expr_stmt|;
for|for
control|(
name|SnapshotIndexShardStatus
name|shard
range|:
name|shards
control|)
block|{
name|stats
operator|.
name|add
argument_list|(
name|shard
operator|.
name|getStats
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

