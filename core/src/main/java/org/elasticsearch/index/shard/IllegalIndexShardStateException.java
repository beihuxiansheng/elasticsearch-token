begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
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
name|ResourceNotFoundException
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|IllegalIndexShardStateException
specifier|public
class|class
name|IllegalIndexShardStateException
extends|extends
name|ElasticsearchException
block|{
DECL|field|currentState
specifier|private
specifier|final
name|IndexShardState
name|currentState
decl_stmt|;
DECL|method|IllegalIndexShardStateException
specifier|public
name|IllegalIndexShardStateException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|IndexShardState
name|currentState
parameter_list|,
name|String
name|msg
parameter_list|)
block|{
name|this
argument_list|(
name|shardId
argument_list|,
name|currentState
argument_list|,
name|msg
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|IllegalIndexShardStateException
specifier|public
name|IllegalIndexShardStateException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|IndexShardState
name|currentState
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|ex
parameter_list|)
block|{
name|super
argument_list|(
literal|"CurrentState["
operator|+
name|currentState
operator|+
literal|"] "
operator|+
name|msg
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|setShard
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|currentState
expr_stmt|;
block|}
DECL|method|currentState
specifier|public
name|IndexShardState
name|currentState
parameter_list|()
block|{
return|return
name|currentState
return|;
block|}
DECL|method|IllegalIndexShardStateException
specifier|public
name|IllegalIndexShardStateException
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|currentState
operator|=
name|IndexShardState
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
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
name|writeByte
argument_list|(
name|currentState
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
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
name|RestStatus
operator|.
name|NOT_FOUND
return|;
block|}
block|}
end_class

end_unit

