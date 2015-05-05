begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.seal
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
name|seal
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
name|indices
operator|.
name|SyncedFlushService
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
name|*
import|;
end_import

begin_comment
comment|/**  * A response to a seal action on several indices.  */
end_comment

begin_class
DECL|class|SealIndicesResponse
specifier|public
class|class
name|SealIndicesResponse
extends|extends
name|ActionResponse
implements|implements
name|ToXContent
block|{
DECL|field|results
specifier|private
name|Set
argument_list|<
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|>
name|results
decl_stmt|;
DECL|method|SealIndicesResponse
name|SealIndicesResponse
parameter_list|()
block|{     }
DECL|method|SealIndicesResponse
name|SealIndicesResponse
parameter_list|(
name|Set
argument_list|<
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|>
name|results
parameter_list|)
block|{
name|this
operator|.
name|results
operator|=
name|results
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
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|results
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|syncedFlushResult
init|=
operator|new
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|()
decl_stmt|;
name|syncedFlushResult
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|syncedFlushResult
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
name|results
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|syncedFlushResult
range|:
name|results
control|)
block|{
name|syncedFlushResult
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|results
specifier|public
name|Set
argument_list|<
name|SyncedFlushService
operator|.
name|SyncedFlushResult
argument_list|>
name|results
parameter_list|()
block|{
return|return
name|results
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
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
argument_list|>
name|allResults
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// first, sort everything by index and shard id
for|for
control|(
name|SyncedFlushService
operator|.
name|SyncedFlushResult
name|result
range|:
name|results
control|)
block|{
name|String
name|indexName
init|=
name|result
operator|.
name|getShardId
argument_list|()
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
decl_stmt|;
name|int
name|shardId
init|=
name|result
operator|.
name|getShardId
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
if|if
condition|(
name|allResults
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// no results yet for this index
name|allResults
operator|.
name|put
argument_list|(
name|indexName
argument_list|,
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|.
name|shardResponses
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|shardResponses
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
name|shardResponse
range|:
name|result
operator|.
name|shardResponses
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|shardResponses
operator|.
name|put
argument_list|(
name|shardResponse
operator|.
name|getKey
argument_list|()
argument_list|,
name|shardResponse
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|allResults
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|shardResponses
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|allResults
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
operator|.
name|put
argument_list|(
name|shardId
argument_list|,
name|result
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
argument_list|>
name|result
range|:
name|allResults
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|result
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|Object
argument_list|>
name|shardResponse
range|:
name|result
operator|.
name|getValue
argument_list|()
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
literal|"shard_id"
argument_list|,
name|shardResponse
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardResponse
operator|.
name|getValue
argument_list|()
operator|instanceof
name|Map
condition|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
literal|"responses"
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
name|results
init|=
operator|(
name|Map
argument_list|<
name|ShardRouting
argument_list|,
name|SyncedFlushService
operator|.
name|SyncedFlushResponse
argument_list|>
operator|)
name|shardResponse
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|boolean
name|success
init|=
literal|true
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
name|shardCopy
range|:
name|results
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|shardCopy
operator|.
name|getKey
argument_list|()
operator|.
name|currentNodeId
argument_list|()
argument_list|,
name|shardCopy
operator|.
name|getValue
argument_list|()
operator|.
name|success
argument_list|()
condition|?
literal|"success"
else|:
name|shardCopy
operator|.
name|getValue
argument_list|()
operator|.
name|failureReason
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardCopy
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
name|success
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"message"
argument_list|,
name|success
condition|?
literal|"success"
else|:
literal|"failed on some copies"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"message"
argument_list|,
name|shardResponse
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// must be a string
block|}
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
return|return
name|builder
return|;
block|}
block|}
end_class

end_unit

