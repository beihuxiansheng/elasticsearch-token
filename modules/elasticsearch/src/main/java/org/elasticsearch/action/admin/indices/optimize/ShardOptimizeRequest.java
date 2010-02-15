begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.optimize
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
name|optimize
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
name|BroadcastShardOperationRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|ShardOptimizeRequest
specifier|public
class|class
name|ShardOptimizeRequest
extends|extends
name|BroadcastShardOperationRequest
block|{
DECL|field|waitForMerge
specifier|private
name|boolean
name|waitForMerge
init|=
literal|true
decl_stmt|;
DECL|field|maxNumSegments
specifier|private
name|int
name|maxNumSegments
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|onlyExpungeDeletes
specifier|private
name|boolean
name|onlyExpungeDeletes
init|=
literal|false
decl_stmt|;
DECL|field|flush
specifier|private
name|boolean
name|flush
init|=
literal|false
decl_stmt|;
DECL|field|refresh
specifier|private
name|boolean
name|refresh
init|=
literal|false
decl_stmt|;
DECL|method|ShardOptimizeRequest
name|ShardOptimizeRequest
parameter_list|()
block|{     }
DECL|method|ShardOptimizeRequest
specifier|public
name|ShardOptimizeRequest
parameter_list|(
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|,
name|OptimizeRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|waitForMerge
operator|=
name|request
operator|.
name|waitForMerge
argument_list|()
expr_stmt|;
name|maxNumSegments
operator|=
name|request
operator|.
name|maxNumSegments
argument_list|()
expr_stmt|;
name|onlyExpungeDeletes
operator|=
name|request
operator|.
name|onlyExpungeDeletes
argument_list|()
expr_stmt|;
name|flush
operator|=
name|request
operator|.
name|flush
argument_list|()
expr_stmt|;
name|refresh
operator|=
name|request
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
DECL|method|waitForMerge
name|boolean
name|waitForMerge
parameter_list|()
block|{
return|return
name|waitForMerge
return|;
block|}
DECL|method|maxNumSegments
name|int
name|maxNumSegments
parameter_list|()
block|{
return|return
name|maxNumSegments
return|;
block|}
DECL|method|onlyExpungeDeletes
specifier|public
name|boolean
name|onlyExpungeDeletes
parameter_list|()
block|{
return|return
name|onlyExpungeDeletes
return|;
block|}
DECL|method|flush
specifier|public
name|boolean
name|flush
parameter_list|()
block|{
return|return
name|flush
return|;
block|}
DECL|method|refresh
specifier|public
name|boolean
name|refresh
parameter_list|()
block|{
return|return
name|refresh
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClassNotFoundException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|waitForMerge
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|maxNumSegments
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|onlyExpungeDeletes
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|flush
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|refresh
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|DataOutput
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
name|writeBoolean
argument_list|(
name|waitForMerge
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|maxNumSegments
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|onlyExpungeDeletes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|flush
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|refresh
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

