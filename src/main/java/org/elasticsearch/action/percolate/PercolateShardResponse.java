begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.percolate
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|percolate
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
name|BroadcastShardOperationResponse
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
name|text
operator|.
name|StringText
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
name|text
operator|.
name|Text
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|percolator
operator|.
name|PercolatorService
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
comment|/**  */
end_comment

begin_class
DECL|class|PercolateShardResponse
specifier|public
class|class
name|PercolateShardResponse
extends|extends
name|BroadcastShardOperationResponse
block|{
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|field|scores
specifier|private
name|float
index|[]
name|scores
decl_stmt|;
DECL|field|matches
specifier|private
name|Text
index|[]
name|matches
decl_stmt|;
comment|// Request fields:
DECL|field|limit
specifier|private
name|boolean
name|limit
decl_stmt|;
DECL|field|requestedSize
specifier|private
name|int
name|requestedSize
decl_stmt|;
DECL|field|sort
specifier|private
name|boolean
name|sort
decl_stmt|;
DECL|field|score
specifier|private
name|boolean
name|score
decl_stmt|;
DECL|method|PercolateShardResponse
specifier|public
name|PercolateShardResponse
parameter_list|()
block|{     }
DECL|method|PercolateShardResponse
specifier|public
name|PercolateShardResponse
parameter_list|(
name|Text
index|[]
name|matches
parameter_list|,
name|long
name|count
parameter_list|,
name|float
index|[]
name|scores
parameter_list|,
name|PercolatorService
operator|.
name|PercolateContext
name|context
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|scores
operator|=
name|scores
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|context
operator|.
name|limit
expr_stmt|;
name|this
operator|.
name|requestedSize
operator|=
name|context
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|context
operator|.
name|sort
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|context
operator|.
name|score
expr_stmt|;
block|}
DECL|method|PercolateShardResponse
specifier|public
name|PercolateShardResponse
parameter_list|(
name|Text
index|[]
name|matches
parameter_list|,
name|long
name|count
parameter_list|,
name|PercolatorService
operator|.
name|PercolateContext
name|context
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
name|this
operator|.
name|scores
operator|=
operator|new
name|float
index|[
literal|0
index|]
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|context
operator|.
name|limit
expr_stmt|;
name|this
operator|.
name|requestedSize
operator|=
name|context
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|context
operator|.
name|sort
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|context
operator|.
name|score
expr_stmt|;
block|}
DECL|method|PercolateShardResponse
specifier|public
name|PercolateShardResponse
parameter_list|(
name|long
name|count
parameter_list|,
name|PercolatorService
operator|.
name|PercolateContext
name|context
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
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
name|matches
operator|=
name|StringText
operator|.
name|EMPTY_ARRAY
expr_stmt|;
name|this
operator|.
name|scores
operator|=
operator|new
name|float
index|[
literal|0
index|]
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|context
operator|.
name|limit
expr_stmt|;
name|this
operator|.
name|requestedSize
operator|=
name|context
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|context
operator|.
name|sort
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|context
operator|.
name|score
expr_stmt|;
block|}
DECL|method|PercolateShardResponse
specifier|public
name|PercolateShardResponse
parameter_list|(
name|PercolatorService
operator|.
name|PercolateContext
name|context
parameter_list|,
name|String
name|index
parameter_list|,
name|int
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|shardId
argument_list|)
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|StringText
operator|.
name|EMPTY_ARRAY
expr_stmt|;
name|this
operator|.
name|scores
operator|=
operator|new
name|float
index|[
literal|0
index|]
expr_stmt|;
name|this
operator|.
name|limit
operator|=
name|context
operator|.
name|limit
expr_stmt|;
name|this
operator|.
name|requestedSize
operator|=
name|context
operator|.
name|size
expr_stmt|;
name|this
operator|.
name|sort
operator|=
name|context
operator|.
name|sort
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|context
operator|.
name|score
expr_stmt|;
block|}
DECL|method|matches
specifier|public
name|Text
index|[]
name|matches
parameter_list|()
block|{
return|return
name|matches
return|;
block|}
DECL|method|scores
specifier|public
name|float
index|[]
name|scores
parameter_list|()
block|{
return|return
name|scores
return|;
block|}
DECL|method|count
specifier|public
name|long
name|count
parameter_list|()
block|{
return|return
name|count
return|;
block|}
DECL|method|limit
specifier|public
name|boolean
name|limit
parameter_list|()
block|{
return|return
name|limit
return|;
block|}
DECL|method|requestedSize
specifier|public
name|int
name|requestedSize
parameter_list|()
block|{
return|return
name|requestedSize
return|;
block|}
DECL|method|sort
specifier|public
name|boolean
name|sort
parameter_list|()
block|{
return|return
name|sort
return|;
block|}
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|()
block|{
return|return
name|score
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
name|matches
operator|=
name|in
operator|.
name|readTextArray
argument_list|()
expr_stmt|;
name|scores
operator|=
operator|new
name|float
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
name|scores
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|scores
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
block|}
name|limit
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|requestedSize
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|sort
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
name|writeTextArray
argument_list|(
name|matches
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|scores
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|float
name|score
range|:
name|scores
control|)
block|{
name|out
operator|.
name|writeFloat
argument_list|(
name|score
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|limit
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|requestedSize
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|sort
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

