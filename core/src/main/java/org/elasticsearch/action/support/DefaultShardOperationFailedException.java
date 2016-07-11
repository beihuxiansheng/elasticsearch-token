begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
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
name|rest
operator|.
name|RestStatus
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|ExceptionsHelper
operator|.
name|detailedMessage
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DefaultShardOperationFailedException
specifier|public
class|class
name|DefaultShardOperationFailedException
implements|implements
name|ShardOperationFailedException
block|{
DECL|field|index
specifier|private
name|String
name|index
decl_stmt|;
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|field|reason
specifier|private
name|Throwable
name|reason
decl_stmt|;
DECL|field|status
specifier|private
name|RestStatus
name|status
decl_stmt|;
DECL|method|DefaultShardOperationFailedException
specifier|protected
name|DefaultShardOperationFailedException
parameter_list|()
block|{     }
DECL|method|DefaultShardOperationFailedException
specifier|public
name|DefaultShardOperationFailedException
parameter_list|(
name|ElasticsearchException
name|e
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|e
operator|.
name|getIndex
argument_list|()
operator|==
literal|null
condition|?
literal|null
else|:
name|e
operator|.
name|getIndex
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardId
operator|=
name|e
operator|.
name|getShardId
argument_list|()
operator|.
name|id
argument_list|()
expr_stmt|;
name|this
operator|.
name|reason
operator|=
name|e
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|e
operator|.
name|status
argument_list|()
expr_stmt|;
block|}
DECL|method|DefaultShardOperationFailedException
specifier|public
name|DefaultShardOperationFailedException
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|Throwable
name|reason
parameter_list|)
block|{
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
name|reason
operator|=
name|reason
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|ExceptionsHelper
operator|.
name|status
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|index
specifier|public
name|String
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
DECL|method|reason
specifier|public
name|String
name|reason
parameter_list|()
block|{
return|return
name|detailedMessage
argument_list|(
name|reason
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
return|return
name|status
return|;
block|}
annotation|@
name|Override
DECL|method|getCause
specifier|public
name|Throwable
name|getCause
parameter_list|()
block|{
return|return
name|reason
return|;
block|}
DECL|method|readShardOperationFailed
specifier|public
specifier|static
name|DefaultShardOperationFailedException
name|readShardOperationFailed
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DefaultShardOperationFailedException
name|exp
init|=
operator|new
name|DefaultShardOperationFailedException
argument_list|()
decl_stmt|;
name|exp
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|exp
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|index
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
block|}
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|reason
operator|=
name|in
operator|.
name|readException
argument_list|()
expr_stmt|;
name|status
operator|=
name|RestStatus
operator|.
name|readFrom
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
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeException
argument_list|(
name|reason
argument_list|)
expr_stmt|;
name|RestStatus
operator|.
name|writeTo
argument_list|(
name|out
argument_list|,
name|status
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
return|return
literal|"["
operator|+
name|index
operator|+
literal|"]["
operator|+
name|shardId
operator|+
literal|"] failed, reason ["
operator|+
name|reason
argument_list|()
operator|+
literal|"]"
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
name|field
argument_list|(
literal|"shard"
argument_list|,
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"status"
argument_list|,
name|status
operator|.
name|name
argument_list|()
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
literal|"reason"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|ElasticsearchException
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|,
name|reason
argument_list|)
expr_stmt|;
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
block|}
end_class

end_unit

