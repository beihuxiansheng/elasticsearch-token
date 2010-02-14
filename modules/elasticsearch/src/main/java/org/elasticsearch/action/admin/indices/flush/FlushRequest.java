begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|action
operator|.
name|support
operator|.
name|broadcast
operator|.
name|BroadcastOperationRequest
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
name|BroadcastOperationThreading
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|FlushRequest
specifier|public
class|class
name|FlushRequest
extends|extends
name|BroadcastOperationRequest
block|{
DECL|method|FlushRequest
name|FlushRequest
parameter_list|()
block|{      }
DECL|method|FlushRequest
specifier|public
name|FlushRequest
parameter_list|(
name|String
name|index
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|String
index|[]
block|{
name|index
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|FlushRequest
specifier|public
name|FlushRequest
parameter_list|(
name|String
modifier|...
name|indices
parameter_list|)
block|{
name|super
argument_list|(
name|indices
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// we want to do the refresh in parallel on local shards...
name|operationThreading
argument_list|(
name|BroadcastOperationThreading
operator|.
name|THREAD_PER_SHARD
argument_list|)
expr_stmt|;
block|}
DECL|method|listenerThreaded
annotation|@
name|Override
specifier|public
name|FlushRequest
name|listenerThreaded
parameter_list|(
name|boolean
name|threadedListener
parameter_list|)
block|{
name|super
operator|.
name|listenerThreaded
argument_list|(
name|threadedListener
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|operationThreading
annotation|@
name|Override
specifier|public
name|FlushRequest
name|operationThreading
parameter_list|(
name|BroadcastOperationThreading
name|operationThreading
parameter_list|)
block|{
name|super
operator|.
name|operationThreading
argument_list|(
name|operationThreading
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

