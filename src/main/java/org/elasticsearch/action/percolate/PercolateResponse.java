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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|common
operator|.
name|xcontent
operator|.
name|XContentBuilderString
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
name|action
operator|.
name|support
operator|.
name|RestActions
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
name|Iterator
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
DECL|class|PercolateResponse
specifier|public
class|class
name|PercolateResponse
extends|extends
name|BroadcastOperationResponse
implements|implements
name|Iterable
argument_list|<
name|PercolateResponse
operator|.
name|Match
argument_list|>
implements|,
name|ToXContent
block|{
DECL|field|EMPTY
specifier|private
specifier|static
specifier|final
name|Match
index|[]
name|EMPTY
init|=
operator|new
name|Match
index|[
literal|0
index|]
decl_stmt|;
DECL|field|tookInMillis
specifier|private
name|long
name|tookInMillis
decl_stmt|;
DECL|field|matches
specifier|private
name|Match
index|[]
name|matches
decl_stmt|;
DECL|field|count
specifier|private
name|long
name|count
decl_stmt|;
DECL|field|hasScores
specifier|private
name|boolean
name|hasScores
decl_stmt|;
DECL|method|PercolateResponse
specifier|public
name|PercolateResponse
parameter_list|(
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
parameter_list|,
name|Match
index|[]
name|matches
parameter_list|,
name|long
name|count
parameter_list|,
name|long
name|tookInMillis
parameter_list|,
name|boolean
name|hasScores
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
name|tookInMillis
operator|=
name|tookInMillis
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
name|hasScores
operator|=
name|hasScores
expr_stmt|;
block|}
DECL|method|PercolateResponse
specifier|public
name|PercolateResponse
parameter_list|(
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
parameter_list|,
name|long
name|count
parameter_list|,
name|long
name|tookInMillis
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
name|tookInMillis
operator|=
name|tookInMillis
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|EMPTY
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|count
expr_stmt|;
name|this
operator|.
name|hasScores
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|PercolateResponse
specifier|public
name|PercolateResponse
parameter_list|(
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
parameter_list|,
name|long
name|tookInMillis
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
name|tookInMillis
operator|=
name|tookInMillis
expr_stmt|;
name|this
operator|.
name|matches
operator|=
name|EMPTY
expr_stmt|;
name|this
operator|.
name|hasScores
operator|=
literal|false
expr_stmt|;
block|}
DECL|method|PercolateResponse
name|PercolateResponse
parameter_list|()
block|{     }
DECL|method|PercolateResponse
specifier|public
name|PercolateResponse
parameter_list|(
name|Match
index|[]
name|matches
parameter_list|)
block|{
name|this
operator|.
name|matches
operator|=
name|matches
expr_stmt|;
block|}
comment|/**      * How long the percolate took.      */
DECL|method|getTook
specifier|public
name|TimeValue
name|getTook
parameter_list|()
block|{
return|return
operator|new
name|TimeValue
argument_list|(
name|tookInMillis
argument_list|)
return|;
block|}
comment|/**      * How long the percolate took in milliseconds.      */
DECL|method|getTookInMillis
specifier|public
name|long
name|getTookInMillis
parameter_list|()
block|{
return|return
name|tookInMillis
return|;
block|}
DECL|method|getMatches
specifier|public
name|Match
index|[]
name|getMatches
parameter_list|()
block|{
return|return
name|this
operator|.
name|matches
return|;
block|}
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
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|Match
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|matches
argument_list|)
operator|.
name|iterator
argument_list|()
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
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOOK
argument_list|,
name|tookInMillis
argument_list|)
expr_stmt|;
name|RestActions
operator|.
name|buildBroadcastShardsHeader
argument_list|(
name|builder
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|count
argument_list|)
expr_stmt|;
if|if
condition|(
name|matches
operator|.
name|length
operator|!=
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
name|Fields
operator|.
name|MATCHES
argument_list|)
expr_stmt|;
name|boolean
name|justIds
init|=
literal|"ids"
operator|.
name|equals
argument_list|(
name|params
operator|.
name|param
argument_list|(
literal|"percolate_format"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|justIds
condition|)
block|{
for|for
control|(
name|PercolateResponse
operator|.
name|Match
name|match
range|:
name|matches
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|match
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|PercolateResponse
operator|.
name|Match
name|match
range|:
name|matches
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
name|Fields
operator|.
name|_INDEX
argument_list|,
name|match
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_ID
argument_list|,
name|match
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasScores
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|_SCORE
argument_list|,
name|match
operator|.
name|getScore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
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
name|tookInMillis
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|count
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|matches
operator|=
operator|new
name|Match
index|[
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|matches
index|[
name|i
index|]
operator|=
operator|new
name|Match
argument_list|()
expr_stmt|;
name|matches
index|[
name|i
index|]
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
name|hasScores
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
name|tookInMillis
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
name|writeVInt
argument_list|(
name|matches
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Match
name|match
range|:
name|matches
control|)
block|{
name|match
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasScores
argument_list|)
expr_stmt|;
block|}
DECL|class|Match
specifier|public
specifier|static
class|class
name|Match
implements|implements
name|Streamable
block|{
DECL|field|index
specifier|private
name|Text
name|index
decl_stmt|;
DECL|field|id
specifier|private
name|Text
name|id
decl_stmt|;
DECL|field|score
specifier|private
name|float
name|score
decl_stmt|;
DECL|method|Match
specifier|public
name|Match
parameter_list|(
name|Text
name|index
parameter_list|,
name|Text
name|id
parameter_list|,
name|float
name|score
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
block|}
DECL|method|Match
name|Match
parameter_list|()
block|{         }
DECL|method|index
specifier|public
name|Text
name|index
parameter_list|()
block|{
return|return
name|index
return|;
block|}
DECL|method|id
specifier|public
name|Text
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
block|{
return|return
name|score
return|;
block|}
DECL|method|getIndex
specifier|public
name|Text
name|getIndex
parameter_list|()
block|{
return|return
name|index
argument_list|()
return|;
block|}
DECL|method|getId
specifier|public
name|Text
name|getId
parameter_list|()
block|{
return|return
name|id
argument_list|()
return|;
block|}
DECL|method|getScore
specifier|public
name|float
name|getScore
parameter_list|()
block|{
return|return
name|score
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
name|id
operator|=
name|in
operator|.
name|readText
argument_list|()
expr_stmt|;
name|index
operator|=
name|in
operator|.
name|readText
argument_list|()
expr_stmt|;
name|score
operator|=
name|in
operator|.
name|readFloat
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
name|out
operator|.
name|writeText
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeText
argument_list|(
name|index
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|score
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|TOOK
specifier|static
specifier|final
name|XContentBuilderString
name|TOOK
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"took"
argument_list|)
decl_stmt|;
DECL|field|TOTAL
specifier|static
specifier|final
name|XContentBuilderString
name|TOTAL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"total"
argument_list|)
decl_stmt|;
DECL|field|MATCHES
specifier|static
specifier|final
name|XContentBuilderString
name|MATCHES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"matches"
argument_list|)
decl_stmt|;
DECL|field|_INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|_INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_index"
argument_list|)
decl_stmt|;
DECL|field|_ID
specifier|static
specifier|final
name|XContentBuilderString
name|_ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_id"
argument_list|)
decl_stmt|;
DECL|field|_SCORE
specifier|static
specifier|final
name|XContentBuilderString
name|_SCORE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_score"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

