begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.gateway.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|gateway
operator|.
name|local
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
name|common
operator|.
name|collect
operator|.
name|Maps
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
name|XContentParser
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
name|Map
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|LocalGatewayStartedShards
specifier|public
class|class
name|LocalGatewayStartedShards
block|{
DECL|class|StartedShard
specifier|public
specifier|static
class|class
name|StartedShard
block|{
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|method|StartedShard
specifier|public
name|StartedShard
parameter_list|(
name|long
name|version
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
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
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|shards
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|shards
decl_stmt|;
DECL|method|LocalGatewayStartedShards
specifier|public
name|LocalGatewayStartedShards
parameter_list|(
name|long
name|version
parameter_list|,
name|Map
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|shards
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|shards
argument_list|)
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|shards
specifier|public
name|ImmutableMap
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|shards
parameter_list|()
block|{
return|return
name|shards
return|;
block|}
DECL|method|builder
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|version
specifier|private
name|long
name|version
decl_stmt|;
DECL|field|shards
specifier|private
name|Map
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|shards
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|method|state
specifier|public
name|Builder
name|state
parameter_list|(
name|LocalGatewayStartedShards
name|state
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|state
operator|.
name|version
argument_list|()
expr_stmt|;
name|this
operator|.
name|shards
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|shards
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|version
specifier|public
name|Builder
name|version
parameter_list|(
name|long
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**          * Returns<tt>true</tt> if something really changed.          */
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
return|return
name|shards
operator|.
name|remove
argument_list|(
name|shardId
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**          * Returns<tt>true</tt> if something really changed.          */
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|long
name|version
parameter_list|)
block|{
name|Long
name|lVersion
init|=
name|shards
operator|.
name|get
argument_list|(
name|shardId
argument_list|)
decl_stmt|;
if|if
condition|(
name|lVersion
operator|!=
literal|null
operator|&&
name|lVersion
operator|==
name|version
condition|)
block|{
return|return
literal|false
return|;
block|}
name|this
operator|.
name|shards
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|version
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
DECL|method|build
specifier|public
name|LocalGatewayStartedShards
name|build
parameter_list|()
block|{
return|return
operator|new
name|LocalGatewayStartedShards
argument_list|(
name|version
argument_list|,
name|shards
argument_list|)
return|;
block|}
DECL|method|toXContent
specifier|public
specifier|static
name|void
name|toXContent
parameter_list|(
name|LocalGatewayStartedShards
name|state
parameter_list|,
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
literal|"state"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"version"
argument_list|,
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"shards"
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ShardId
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|state
operator|.
name|shards
operator|.
name|entrySet
argument_list|()
control|)
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
literal|"index"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"id"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"version"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|LocalGatewayStartedShards
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
block|{
comment|// no data...
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
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
literal|"shards"
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
name|END_ARRAY
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
name|START_OBJECT
condition|)
block|{
name|String
name|shardIndex
init|=
literal|null
decl_stmt|;
name|int
name|shardId
init|=
operator|-
literal|1
decl_stmt|;
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
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
block|}
elseif|else
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
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shardIndex
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
literal|"id"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shardId
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
literal|"version"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|builder
operator|.
name|shards
operator|.
name|put
argument_list|(
operator|new
name|ShardId
argument_list|(
name|shardIndex
argument_list|,
name|shardId
argument_list|)
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
elseif|else
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
literal|"version"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|builder
operator|.
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|readFrom
specifier|public
specifier|static
name|LocalGatewayStartedShards
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|LocalGatewayStartedShards
operator|.
name|Builder
name|builder
init|=
operator|new
name|Builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|version
operator|=
name|in
operator|.
name|readLong
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
name|shards
operator|.
name|put
argument_list|(
name|ShardId
operator|.
name|readShardId
argument_list|(
name|in
argument_list|)
argument_list|,
name|in
operator|.
name|readLong
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|writeTo
specifier|public
specifier|static
name|void
name|writeTo
parameter_list|(
name|LocalGatewayStartedShards
name|state
parameter_list|,
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|state
operator|.
name|shards
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
name|ShardId
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|state
operator|.
name|shards
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

