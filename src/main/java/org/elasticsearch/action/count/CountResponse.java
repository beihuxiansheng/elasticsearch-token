begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.count
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|count
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
name|BroadcastOperationResponse
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
comment|/**  * The response of the count action.  */
end_comment

begin_class
DECL|class|CountResponse
specifier|public
class|class
name|CountResponse
extends|extends
name|BroadcastOperationResponse
block|{
DECL|field|terminatedEarly
specifier|private
name|boolean
name|terminatedEarly
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|method|CountResponse
name|CountResponse
parameter_list|()
block|{      }
DECL|method|CountResponse
name|CountResponse
parameter_list|(
name|long
name|count
parameter_list|,
name|boolean
name|hasTerminatedEarly
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
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|terminatedEarly
operator|=
name|hasTerminatedEarly
expr_stmt|;
block|}
comment|/**      * The count of documents matching the query provided.      */
DECL|method|getCount
specifier|public
name|long
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**      * True if the request has been terminated early due to enough count      */
DECL|method|terminatedEarly
specifier|public
name|boolean
name|terminatedEarly
parameter_list|()
block|{
return|return
name|this
operator|.
name|terminatedEarly
return|;
block|}
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
if|if
condition|(
name|getFailedShards
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|getSuccessfulShards
argument_list|()
operator|==
literal|0
operator|&&
name|getTotalShards
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
return|;
block|}
return|return
name|RestStatus
operator|.
name|OK
return|;
block|}
comment|// if total failure, bubble up the status code to the response level
if|if
condition|(
name|getSuccessfulShards
argument_list|()
operator|==
literal|0
operator|&&
name|getTotalShards
argument_list|()
operator|>
literal|0
condition|)
block|{
name|RestStatus
name|status
init|=
name|RestStatus
operator|.
name|OK
decl_stmt|;
for|for
control|(
name|ShardOperationFailedException
name|shardFailure
range|:
name|getShardFailures
argument_list|()
control|)
block|{
name|RestStatus
name|shardStatus
init|=
name|shardFailure
operator|.
name|status
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardStatus
operator|.
name|getStatus
argument_list|()
operator|>=
name|status
operator|.
name|getStatus
argument_list|()
condition|)
block|{
name|status
operator|=
name|shardStatus
expr_stmt|;
block|}
block|}
return|return
name|status
return|;
block|}
return|return
name|RestStatus
operator|.
name|OK
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
name|count
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|terminatedEarly
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
name|out
operator|.
name|writeVLong
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|terminatedEarly
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

