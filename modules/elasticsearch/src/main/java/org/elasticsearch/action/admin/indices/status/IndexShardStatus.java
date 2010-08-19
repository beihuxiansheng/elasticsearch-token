begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.status
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
name|status
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
name|Iterators
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
name|ByteSizeValue
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|IndexShardStatus
specifier|public
class|class
name|IndexShardStatus
implements|implements
name|Iterable
argument_list|<
name|ShardStatus
argument_list|>
block|{
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|shards
specifier|private
specifier|final
name|ShardStatus
index|[]
name|shards
decl_stmt|;
DECL|method|IndexShardStatus
name|IndexShardStatus
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|ShardStatus
index|[]
name|shards
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|shards
operator|=
name|shards
expr_stmt|;
block|}
DECL|method|shardId
specifier|public
name|ShardId
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
DECL|method|getShardId
specifier|public
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
argument_list|()
return|;
block|}
DECL|method|shards
specifier|public
name|ShardStatus
index|[]
name|shards
parameter_list|()
block|{
return|return
name|this
operator|.
name|shards
return|;
block|}
DECL|method|getShards
specifier|public
name|ShardStatus
index|[]
name|getShards
parameter_list|()
block|{
return|return
name|shards
argument_list|()
return|;
block|}
DECL|method|getAt
specifier|public
name|ShardStatus
name|getAt
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|shards
index|[
name|position
index|]
return|;
block|}
DECL|method|storeSize
specifier|public
name|ByteSizeValue
name|storeSize
parameter_list|()
block|{
name|long
name|bytes
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|ShardStatus
name|shard
range|:
name|shards
argument_list|()
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|storeSize
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|bytes
operator|==
operator|-
literal|1
condition|)
block|{
name|bytes
operator|=
literal|0
expr_stmt|;
block|}
name|bytes
operator|+=
name|shard
operator|.
name|storeSize
argument_list|()
operator|.
name|bytes
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bytes
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|ByteSizeValue
argument_list|(
name|bytes
argument_list|)
return|;
block|}
DECL|method|getStoreSize
specifier|public
name|ByteSizeValue
name|getStoreSize
parameter_list|()
block|{
return|return
name|storeSize
argument_list|()
return|;
block|}
DECL|method|translogOperations
specifier|public
name|long
name|translogOperations
parameter_list|()
block|{
name|long
name|translogOperations
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|ShardStatus
name|shard
range|:
name|shards
argument_list|()
control|)
block|{
if|if
condition|(
name|shard
operator|.
name|translogOperations
argument_list|()
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|translogOperations
operator|==
operator|-
literal|1
condition|)
block|{
name|translogOperations
operator|=
literal|0
expr_stmt|;
block|}
name|translogOperations
operator|+=
name|shard
operator|.
name|translogOperations
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|translogOperations
return|;
block|}
DECL|method|getTranslogOperations
specifier|public
name|long
name|getTranslogOperations
parameter_list|()
block|{
return|return
name|translogOperations
argument_list|()
return|;
block|}
DECL|field|docs
specifier|private
specifier|transient
name|DocsStatus
name|docs
decl_stmt|;
DECL|method|docs
specifier|public
name|DocsStatus
name|docs
parameter_list|()
block|{
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
return|return
name|docs
return|;
block|}
name|DocsStatus
name|docs
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ShardStatus
name|shard
range|:
name|shards
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|shard
operator|.
name|shardRouting
argument_list|()
operator|.
name|primary
argument_list|()
condition|)
block|{
comment|// only sum docs for the primaries
continue|continue;
block|}
if|if
condition|(
name|shard
operator|.
name|docs
argument_list|()
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
block|{
name|docs
operator|=
operator|new
name|DocsStatus
argument_list|()
expr_stmt|;
block|}
name|docs
operator|.
name|numDocs
operator|+=
name|shard
operator|.
name|docs
argument_list|()
operator|.
name|numDocs
argument_list|()
expr_stmt|;
name|docs
operator|.
name|maxDoc
operator|+=
name|shard
operator|.
name|docs
argument_list|()
operator|.
name|maxDoc
argument_list|()
expr_stmt|;
name|docs
operator|.
name|deletedDocs
operator|+=
name|shard
operator|.
name|docs
argument_list|()
operator|.
name|deletedDocs
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
return|return
name|this
operator|.
name|docs
return|;
block|}
DECL|method|getDocs
specifier|public
name|DocsStatus
name|getDocs
parameter_list|()
block|{
return|return
name|docs
argument_list|()
return|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|ShardStatus
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Iterators
operator|.
name|forArray
argument_list|(
name|shards
argument_list|)
return|;
block|}
block|}
end_class

end_unit

