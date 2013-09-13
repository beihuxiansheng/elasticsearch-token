begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.flush
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
name|flush
package|;
end_package

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
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastShardOperationRequest
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|ShardFlushRequest
class|class
name|ShardFlushRequest
extends|extends
name|BroadcastShardOperationRequest
block|{
DECL|field|full
specifier|private
name|boolean
name|full
decl_stmt|;
DECL|field|force
specifier|private
name|boolean
name|force
decl_stmt|;
DECL|method|ShardFlushRequest
name|ShardFlushRequest
parameter_list|()
block|{     }
DECL|method|ShardFlushRequest
specifier|public
name|ShardFlushRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|FlushRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|full
operator|=
name|request
operator|.
name|full
argument_list|()
expr_stmt|;
name|this
operator|.
name|force
operator|=
name|request
operator|.
name|force
argument_list|()
expr_stmt|;
block|}
DECL|method|full
specifier|public
name|boolean
name|full
parameter_list|()
block|{
return|return
name|this
operator|.
name|full
return|;
block|}
DECL|method|force
specifier|public
name|boolean
name|force
parameter_list|()
block|{
return|return
name|this
operator|.
name|force
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
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_0_90_3
argument_list|)
condition|)
block|{
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
comment|// refresh flag
block|}
name|full
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|force
operator|=
name|in
operator|.
name|readBoolean
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrBefore
argument_list|(
name|Version
operator|.
name|V_0_90_3
argument_list|)
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// refresh flag
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|full
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|force
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

