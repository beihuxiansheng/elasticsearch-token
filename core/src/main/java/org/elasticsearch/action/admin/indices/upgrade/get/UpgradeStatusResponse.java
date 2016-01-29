begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.upgrade.get
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
name|upgrade
operator|.
name|get
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
name|ShardOperationFailedException
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
name|support
operator|.
name|broadcast
operator|.
name|BroadcastResponse
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
name|HashSet
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

begin_class
DECL|class|UpgradeStatusResponse
specifier|public
class|class
name|UpgradeStatusResponse
extends|extends
name|BroadcastResponse
implements|implements
name|ToXContent
block|{
DECL|field|shards
specifier|private
name|ShardUpgradeStatus
index|[]
name|shards
decl_stmt|;
DECL|field|indicesUpgradeStatus
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|IndexUpgradeStatus
argument_list|>
name|indicesUpgradeStatus
decl_stmt|;
DECL|method|UpgradeStatusResponse
name|UpgradeStatusResponse
parameter_list|()
block|{     }
DECL|method|UpgradeStatusResponse
name|UpgradeStatusResponse
parameter_list|(
name|ShardUpgradeStatus
index|[]
name|shards
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|int
name|failedShards
parameter_list|,
name|List
argument_list|<
name|ShardOperationFailedException
argument_list|>
name|shardFailures
parameter_list|)
block|{
name|super
argument_list|(
name|totalShards
argument_list|,
name|successfulShards
argument_list|,
name|failedShards
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|shards
expr_stmt|;
block|}
DECL|method|getIndices
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|IndexUpgradeStatus
argument_list|>
name|getIndices
parameter_list|()
block|{
if|if
condition|(
name|indicesUpgradeStatus
operator|!=
literal|null
condition|)
block|{
return|return
name|indicesUpgradeStatus
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|IndexUpgradeStatus
argument_list|>
name|indicesUpgradeStats
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|indices
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardUpgradeStatus
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
name|indexName
range|:
name|indices
control|)
block|{
name|List
argument_list|<
name|ShardUpgradeStatus
argument_list|>
name|shards
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ShardUpgradeStatus
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
name|getShardRouting
argument_list|()
operator|.
name|getIndexName
argument_list|()
operator|.
name|equals
argument_list|(
name|indexName
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
name|indicesUpgradeStats
operator|.
name|put
argument_list|(
name|indexName
argument_list|,
operator|new
name|IndexUpgradeStatus
argument_list|(
name|indexName
argument_list|,
name|shards
operator|.
name|toArray
argument_list|(
operator|new
name|ShardUpgradeStatus
index|[
name|shards
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indicesUpgradeStatus
operator|=
name|indicesUpgradeStats
expr_stmt|;
return|return
name|indicesUpgradeStats
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
name|shards
operator|=
operator|new
name|ShardUpgradeStatus
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shards
index|[
name|i
index|]
operator|=
name|ShardUpgradeStatus
operator|.
name|readShardUpgradeStatus
argument_list|(
name|in
argument_list|)
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shards
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardUpgradeStatus
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
DECL|method|getTotalBytes
specifier|public
name|long
name|getTotalBytes
parameter_list|()
block|{
name|long
name|totalBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexUpgradeStatus
name|indexShardUpgradeStatus
range|:
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|totalBytes
operator|+=
name|indexShardUpgradeStatus
operator|.
name|getTotalBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|totalBytes
return|;
block|}
DECL|method|getToUpgradeBytes
specifier|public
name|long
name|getToUpgradeBytes
parameter_list|()
block|{
name|long
name|upgradeBytes
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexUpgradeStatus
name|indexShardUpgradeStatus
range|:
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|upgradeBytes
operator|+=
name|indexShardUpgradeStatus
operator|.
name|getToUpgradeBytes
argument_list|()
expr_stmt|;
block|}
return|return
name|upgradeBytes
return|;
block|}
DECL|method|getToUpgradeBytesAncient
specifier|public
name|long
name|getToUpgradeBytesAncient
parameter_list|()
block|{
name|long
name|upgradeBytesAncient
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IndexUpgradeStatus
name|indexShardUpgradeStatus
range|:
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|upgradeBytesAncient
operator|+=
name|indexShardUpgradeStatus
operator|.
name|getToUpgradeBytesAncient
argument_list|()
expr_stmt|;
block|}
return|return
name|upgradeBytesAncient
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
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE
argument_list|,
name|getTotalBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_TO_UPGRADE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE_TO_UPGRADE
argument_list|,
name|getToUpgradeBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_TO_UPGRADE_ANCIENT_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE_TO_UPGRADE_ANCIENT
argument_list|,
name|getToUpgradeBytesAncient
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|level
init|=
name|params
operator|.
name|param
argument_list|(
literal|"level"
argument_list|,
literal|"indices"
argument_list|)
decl_stmt|;
name|boolean
name|outputShards
init|=
literal|"shards"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
decl_stmt|;
name|boolean
name|outputIndices
init|=
literal|"indices"
operator|.
name|equals
argument_list|(
name|level
argument_list|)
operator|||
name|outputShards
decl_stmt|;
if|if
condition|(
name|outputIndices
condition|)
block|{
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
name|IndexUpgradeStatus
name|indexUpgradeStatus
range|:
name|getIndices
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|indexUpgradeStatus
operator|.
name|getIndex
argument_list|()
argument_list|,
name|XContentBuilder
operator|.
name|FieldCaseConversion
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE
argument_list|,
name|indexUpgradeStatus
operator|.
name|getTotalBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_TO_UPGRADE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE_TO_UPGRADE
argument_list|,
name|indexUpgradeStatus
operator|.
name|getToUpgradeBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_TO_UPGRADE_ANCIENT_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE_TO_UPGRADE_ANCIENT
argument_list|,
name|indexUpgradeStatus
operator|.
name|getToUpgradeBytesAncient
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|outputShards
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|SHARDS
argument_list|)
expr_stmt|;
for|for
control|(
name|IndexShardUpgradeStatus
name|indexShardUpgradeStatus
range|:
name|indexUpgradeStatus
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|indexShardUpgradeStatus
operator|.
name|getShardId
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardUpgradeStatus
name|shardUpgradeStatus
range|:
name|indexShardUpgradeStatus
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE
argument_list|,
name|getTotalBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_TO_UPGRADE_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE_TO_UPGRADE
argument_list|,
name|getToUpgradeBytes
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|byteSizeField
argument_list|(
name|Fields
operator|.
name|SIZE_TO_UPGRADE_ANCIENT_IN_BYTES
argument_list|,
name|Fields
operator|.
name|SIZE_TO_UPGRADE_ANCIENT
argument_list|,
name|getToUpgradeBytesAncient
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|ROUTING
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
name|shardUpgradeStatus
operator|.
name|getShardRouting
argument_list|()
operator|.
name|state
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|PRIMARY
argument_list|,
name|shardUpgradeStatus
operator|.
name|getShardRouting
argument_list|()
operator|.
name|primary
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|NODE
argument_list|,
name|shardUpgradeStatus
operator|.
name|getShardRouting
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardUpgradeStatus
operator|.
name|getShardRouting
argument_list|()
operator|.
name|relocatingNodeId
argument_list|()
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
name|RELOCATING_NODE
argument_list|,
name|shardUpgradeStatus
operator|.
name|getShardRouting
argument_list|()
operator|.
name|relocatingNodeId
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
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
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
DECL|field|SIZE
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size"
argument_list|)
decl_stmt|;
DECL|field|SIZE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|SIZE_TO_UPGRADE
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_TO_UPGRADE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_to_upgrade"
argument_list|)
decl_stmt|;
DECL|field|SIZE_TO_UPGRADE_ANCIENT
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_TO_UPGRADE_ANCIENT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_to_upgrade_ancient"
argument_list|)
decl_stmt|;
DECL|field|SIZE_TO_UPGRADE_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_TO_UPGRADE_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_to_upgrade_in_bytes"
argument_list|)
decl_stmt|;
DECL|field|SIZE_TO_UPGRADE_ANCIENT_IN_BYTES
specifier|static
specifier|final
name|XContentBuilderString
name|SIZE_TO_UPGRADE_ANCIENT_IN_BYTES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"size_to_upgrade_ancient_in_bytes"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

