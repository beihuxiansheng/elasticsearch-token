begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
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
name|support
operator|.
name|replication
operator|.
name|ShardReplicationOperationRequest
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
name|single
operator|.
name|instance
operator|.
name|InstanceShardOperationRequest
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
name|List
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|BulkShardRequest
specifier|public
class|class
name|BulkShardRequest
extends|extends
name|ShardReplicationOperationRequest
argument_list|<
name|BulkShardRequest
argument_list|>
block|{
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|items
specifier|private
name|BulkItemRequest
index|[]
name|items
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
decl_stmt|;
DECL|method|BulkShardRequest
name|BulkShardRequest
parameter_list|()
block|{     }
DECL|method|BulkShardRequest
name|BulkShardRequest
parameter_list|(
name|BulkRequest
name|bulkRequest
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|boolean
name|refresh
parameter_list|,
name|BulkItemRequest
index|[]
name|items
parameter_list|)
block|{
name|super
argument_list|(
name|bulkRequest
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|this
operator|.
name|items
operator|=
name|items
expr_stmt|;
name|this
operator|.
name|refresh
operator|=
name|refresh
expr_stmt|;
block|}
DECL|method|refresh
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|this
operator|.
name|refresh
return|;
block|}
DECL|method|shardId
name|int
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|items
name|BulkItemRequest
index|[]
name|items
parameter_list|()
block|{
return|return
name|items
return|;
block|}
annotation|@
name|Override
DECL|method|indices
specifier|public
name|String
index|[]
name|indices
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|indices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|BulkItemRequest
name|item
range|:
name|items
control|)
block|{
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|indices
operator|.
name|add
argument_list|(
name|item
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|indices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indices
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**      * Before we fork on a local thread, make sure we copy over the bytes if they are unsafe      */
annotation|@
name|Override
DECL|method|beforeLocalFork
specifier|public
name|void
name|beforeLocalFork
parameter_list|()
block|{
for|for
control|(
name|BulkItemRequest
name|item
range|:
name|items
control|)
block|{
if|if
condition|(
name|item
operator|.
name|request
argument_list|()
operator|instanceof
name|InstanceShardOperationRequest
condition|)
block|{
operator|(
operator|(
name|InstanceShardOperationRequest
operator|)
name|item
operator|.
name|request
argument_list|()
operator|)
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|ShardReplicationOperationRequest
operator|)
name|item
operator|.
name|request
argument_list|()
operator|)
operator|.
name|beforeLocalFork
argument_list|()
expr_stmt|;
block|}
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
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|items
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|BulkItemRequest
name|item
range|:
name|items
control|)
block|{
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|item
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
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
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|items
operator|=
operator|new
name|BulkItemRequest
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
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|items
index|[
name|i
index|]
operator|=
name|BulkItemRequest
operator|.
name|readBulkItem
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
name|refresh
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

