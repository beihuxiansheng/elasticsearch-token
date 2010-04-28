begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|search
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
name|search
operator|.
name|SearchHits
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facets
operator|.
name|Facets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|InternalSearchResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
operator|.
name|json
operator|.
name|JsonBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|ToJson
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
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
name|action
operator|.
name|search
operator|.
name|ShardSearchFailure
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|InternalSearchResponse
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A response of a search request.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SearchResponse
specifier|public
class|class
name|SearchResponse
implements|implements
name|ActionResponse
implements|,
name|ToXContent
block|{
DECL|field|internalResponse
specifier|private
name|InternalSearchResponse
name|internalResponse
decl_stmt|;
DECL|field|scrollId
specifier|private
name|String
name|scrollId
decl_stmt|;
DECL|field|totalShards
specifier|private
name|int
name|totalShards
decl_stmt|;
DECL|field|successfulShards
specifier|private
name|int
name|successfulShards
decl_stmt|;
DECL|field|shardFailures
specifier|private
name|ShardSearchFailure
index|[]
name|shardFailures
decl_stmt|;
DECL|method|SearchResponse
name|SearchResponse
parameter_list|()
block|{     }
DECL|method|SearchResponse
specifier|public
name|SearchResponse
parameter_list|(
name|InternalSearchResponse
name|internalResponse
parameter_list|,
name|String
name|scrollId
parameter_list|,
name|int
name|totalShards
parameter_list|,
name|int
name|successfulShards
parameter_list|,
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|)
block|{
name|this
operator|.
name|internalResponse
operator|=
name|internalResponse
expr_stmt|;
name|this
operator|.
name|scrollId
operator|=
name|scrollId
expr_stmt|;
name|this
operator|.
name|totalShards
operator|=
name|totalShards
expr_stmt|;
name|this
operator|.
name|successfulShards
operator|=
name|successfulShards
expr_stmt|;
name|this
operator|.
name|shardFailures
operator|=
name|shardFailures
expr_stmt|;
block|}
comment|/**      * The search hits.      */
DECL|method|hits
specifier|public
name|SearchHits
name|hits
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|hits
argument_list|()
return|;
block|}
comment|/**      * The search hits.      */
DECL|method|getHits
specifier|public
name|SearchHits
name|getHits
parameter_list|()
block|{
return|return
name|hits
argument_list|()
return|;
block|}
comment|/**      * The search facets.      */
DECL|method|facets
specifier|public
name|Facets
name|facets
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|facets
argument_list|()
return|;
block|}
comment|/**      * The search facets.      */
DECL|method|getFacets
specifier|public
name|Facets
name|getFacets
parameter_list|()
block|{
return|return
name|facets
argument_list|()
return|;
block|}
comment|/**      * The total number of shards the search was executed on.      */
DECL|method|totalShards
specifier|public
name|int
name|totalShards
parameter_list|()
block|{
return|return
name|totalShards
return|;
block|}
comment|/**      * The total number of shards the search was executed on.      */
DECL|method|getTotalShards
specifier|public
name|int
name|getTotalShards
parameter_list|()
block|{
return|return
name|totalShards
return|;
block|}
comment|/**      * The successful number of shards the search was executed on.      */
DECL|method|successfulShards
specifier|public
name|int
name|successfulShards
parameter_list|()
block|{
return|return
name|successfulShards
return|;
block|}
comment|/**      * The successful number of shards the search was executed on.      */
DECL|method|getSuccessfulShards
specifier|public
name|int
name|getSuccessfulShards
parameter_list|()
block|{
return|return
name|successfulShards
return|;
block|}
comment|/**      * The failed number of shards the search was executed on.      */
DECL|method|failedShards
specifier|public
name|int
name|failedShards
parameter_list|()
block|{
return|return
name|totalShards
operator|-
name|successfulShards
return|;
block|}
comment|/**      * The failed number of shards the search was executed on.      */
DECL|method|getFailedShards
specifier|public
name|int
name|getFailedShards
parameter_list|()
block|{
return|return
name|failedShards
argument_list|()
return|;
block|}
comment|/**      * The failures that occurred during the search.      */
DECL|method|shardFailures
specifier|public
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|()
block|{
return|return
name|this
operator|.
name|shardFailures
return|;
block|}
comment|/**      * The failures that occurred during the search.      */
DECL|method|getShardFailures
specifier|public
name|ShardSearchFailure
index|[]
name|getShardFailures
parameter_list|()
block|{
return|return
name|shardFailures
return|;
block|}
comment|/**      * If scrolling was enabled ({@link SearchRequest#scroll(org.elasticsearch.search.Scroll)}, the      * scroll id that can be used to continue scrolling.      */
DECL|method|scrollId
specifier|public
name|String
name|scrollId
parameter_list|()
block|{
return|return
name|scrollId
return|;
block|}
comment|/**      * If scrolling was enabled ({@link SearchRequest#scroll(org.elasticsearch.search.Scroll)}, the      * scroll id that can be used to continue scrolling.      */
DECL|method|getScrollId
specifier|public
name|String
name|getScrollId
parameter_list|()
block|{
return|return
name|scrollId
return|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
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
if|if
condition|(
name|scrollId
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"_scrollId"
argument_list|,
name|scrollId
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
literal|"_shards"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"total"
argument_list|,
name|totalShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"successful"
argument_list|,
name|successfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"failed"
argument_list|,
name|failedShards
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardFailures
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|startArray
argument_list|(
literal|"failures"
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardSearchFailure
name|shardFailure
range|:
name|shardFailures
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
if|if
condition|(
name|shardFailure
operator|.
name|shard
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index"
argument_list|,
name|shardFailure
operator|.
name|shard
argument_list|()
operator|.
name|index
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"shard"
argument_list|,
name|shardFailure
operator|.
name|shard
argument_list|()
operator|.
name|shardId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
literal|"reason"
argument_list|,
name|shardFailure
operator|.
name|reason
argument_list|()
argument_list|)
expr_stmt|;
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|internalResponse
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
DECL|method|readSearchResponse
specifier|public
specifier|static
name|SearchResponse
name|readSearchResponse
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SearchResponse
name|response
init|=
operator|new
name|SearchResponse
argument_list|()
decl_stmt|;
name|response
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|internalResponse
operator|=
name|readInternalSearchResponse
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|totalShards
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|successfulShards
operator|=
name|in
operator|.
name|readVInt
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
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|shardFailures
operator|=
name|ShardSearchFailure
operator|.
name|EMPTY_ARRAY
expr_stmt|;
block|}
else|else
block|{
name|shardFailures
operator|=
operator|new
name|ShardSearchFailure
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
name|shardFailures
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|shardFailures
index|[
name|i
index|]
operator|=
name|readShardSearchFailure
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|scrollId
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|internalResponse
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
name|totalShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|successfulShards
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardFailures
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardSearchFailure
name|shardSearchFailure
range|:
name|shardFailures
control|)
block|{
name|shardSearchFailure
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|scrollId
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
name|writeUTF
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

