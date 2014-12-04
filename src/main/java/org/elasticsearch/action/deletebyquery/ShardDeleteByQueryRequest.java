begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.deletebyquery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|deletebyquery
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
name|ActionRequestValidationException
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
name|OriginalIndices
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
name|IndicesOptions
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
name|Strings
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
name|bytes
operator|.
name|BytesReference
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
name|XContentHelper
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
name|Arrays
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
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ValidateActions
operator|.
name|addValidationError
import|;
end_import

begin_comment
comment|/**  * Delete by query request to execute on a specific shard.  */
end_comment

begin_class
DECL|class|ShardDeleteByQueryRequest
specifier|public
class|class
name|ShardDeleteByQueryRequest
extends|extends
name|ShardReplicationOperationRequest
argument_list|<
name|ShardDeleteByQueryRequest
argument_list|>
block|{
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|source
specifier|private
name|BytesReference
name|source
decl_stmt|;
DECL|field|types
specifier|private
name|String
index|[]
name|types
init|=
name|Strings
operator|.
name|EMPTY_ARRAY
decl_stmt|;
annotation|@
name|Nullable
DECL|field|routing
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|routing
decl_stmt|;
annotation|@
name|Nullable
DECL|field|filteringAliases
specifier|private
name|String
index|[]
name|filteringAliases
decl_stmt|;
DECL|field|nowInMillis
specifier|private
name|long
name|nowInMillis
decl_stmt|;
DECL|field|originalIndices
specifier|private
name|OriginalIndices
name|originalIndices
decl_stmt|;
DECL|method|ShardDeleteByQueryRequest
name|ShardDeleteByQueryRequest
parameter_list|(
name|IndexDeleteByQueryRequest
name|request
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|request
operator|.
name|index
argument_list|()
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|request
operator|.
name|source
argument_list|()
expr_stmt|;
name|this
operator|.
name|types
operator|=
name|request
operator|.
name|types
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|shardId
expr_stmt|;
name|replicationType
argument_list|(
name|request
operator|.
name|replicationType
argument_list|()
argument_list|)
expr_stmt|;
name|consistencyLevel
argument_list|(
name|request
operator|.
name|consistencyLevel
argument_list|()
argument_list|)
expr_stmt|;
name|timeout
operator|=
name|request
operator|.
name|timeout
argument_list|()
expr_stmt|;
name|this
operator|.
name|routing
operator|=
name|request
operator|.
name|routing
argument_list|()
expr_stmt|;
name|filteringAliases
operator|=
name|request
operator|.
name|filteringAliases
argument_list|()
expr_stmt|;
name|nowInMillis
operator|=
name|request
operator|.
name|nowInMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|originalIndices
operator|=
operator|new
name|OriginalIndices
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
DECL|method|ShardDeleteByQueryRequest
name|ShardDeleteByQueryRequest
parameter_list|()
block|{     }
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|super
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
name|addValidationError
argument_list|(
literal|"source is missing"
argument_list|,
name|validationException
argument_list|)
expr_stmt|;
block|}
return|return
name|validationException
return|;
block|}
DECL|method|shardId
specifier|public
name|int
name|shardId
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardId
return|;
block|}
DECL|method|source
name|BytesReference
name|source
parameter_list|()
block|{
return|return
name|source
return|;
block|}
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|this
operator|.
name|types
return|;
block|}
DECL|method|routing
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|routing
parameter_list|()
block|{
return|return
name|this
operator|.
name|routing
return|;
block|}
DECL|method|filteringAliases
specifier|public
name|String
index|[]
name|filteringAliases
parameter_list|()
block|{
return|return
name|filteringAliases
return|;
block|}
DECL|method|nowInMillis
name|long
name|nowInMillis
parameter_list|()
block|{
return|return
name|nowInMillis
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
return|return
name|originalIndices
operator|.
name|indices
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|indicesOptions
specifier|public
name|IndicesOptions
name|indicesOptions
parameter_list|()
block|{
return|return
name|originalIndices
operator|.
name|indicesOptions
argument_list|()
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
name|source
operator|=
name|in
operator|.
name|readBytesReference
argument_list|()
expr_stmt|;
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|types
operator|=
name|in
operator|.
name|readStringArray
argument_list|()
expr_stmt|;
name|int
name|routingSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|routingSize
operator|>
literal|0
condition|)
block|{
name|routing
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|routingSize
argument_list|)
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
name|routingSize
condition|;
name|i
operator|++
control|)
block|{
name|routing
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
block|}
name|int
name|aliasesSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|aliasesSize
operator|>
literal|0
condition|)
block|{
name|filteringAliases
operator|=
operator|new
name|String
index|[
name|aliasesSize
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
name|aliasesSize
condition|;
name|i
operator|++
control|)
block|{
name|filteringAliases
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
block|}
name|nowInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|originalIndices
operator|=
name|OriginalIndices
operator|.
name|readOriginalIndices
argument_list|(
name|in
argument_list|)
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesReference
argument_list|(
name|source
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
name|writeStringArray
argument_list|(
name|types
argument_list|)
expr_stmt|;
if|if
condition|(
name|routing
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|routing
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|r
range|:
name|routing
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filteringAliases
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|filteringAliases
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|alias
range|:
name|filteringAliases
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|alias
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|out
operator|.
name|writeVInt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeVLong
argument_list|(
name|nowInMillis
argument_list|)
expr_stmt|;
name|OriginalIndices
operator|.
name|writeOriginalIndices
argument_list|(
name|originalIndices
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|sSource
init|=
literal|"_na_"
decl_stmt|;
try|try
block|{
name|sSource
operator|=
name|XContentHelper
operator|.
name|convertToJson
argument_list|(
name|source
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore
block|}
return|return
literal|"delete_by_query {["
operator|+
name|index
operator|+
literal|"]"
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|types
argument_list|)
operator|+
literal|", query ["
operator|+
name|sSource
operator|+
literal|"]}"
return|;
block|}
block|}
end_class

end_unit

