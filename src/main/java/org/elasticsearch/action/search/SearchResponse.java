begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|StatusToXContent
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
name|common
operator|.
name|xcontent
operator|.
name|XContentFactory
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
name|aggregations
operator|.
name|Aggregations
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
name|search
operator|.
name|suggest
operator|.
name|Suggest
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
name|readShardSearchFailure
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
name|readInternalSearchResponse
import|;
end_import

begin_comment
comment|/**  * A response of a search request.  */
end_comment

begin_class
DECL|class|SearchResponse
specifier|public
class|class
name|SearchResponse
extends|extends
name|ActionResponse
implements|implements
name|StatusToXContent
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
DECL|field|tookInMillis
specifier|private
name|long
name|tookInMillis
decl_stmt|;
DECL|method|SearchResponse
specifier|public
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
name|long
name|tookInMillis
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
name|tookInMillis
operator|=
name|tookInMillis
expr_stmt|;
name|this
operator|.
name|shardFailures
operator|=
name|shardFailures
expr_stmt|;
block|}
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
if|if
condition|(
name|shardFailures
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|successfulShards
operator|==
literal|0
operator|&&
name|totalShards
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
name|successfulShards
operator|==
literal|0
operator|&&
name|totalShards
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
name|RestStatus
name|shardStatus
init|=
name|shardFailures
index|[
name|i
index|]
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
name|shardFailures
index|[
name|i
index|]
operator|.
name|status
argument_list|()
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
comment|/**      * The search hits.      */
DECL|method|getHits
specifier|public
name|SearchHits
name|getHits
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|hits
argument_list|()
return|;
block|}
DECL|method|getAggregations
specifier|public
name|Aggregations
name|getAggregations
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|aggregations
argument_list|()
return|;
block|}
DECL|method|getSuggest
specifier|public
name|Suggest
name|getSuggest
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|suggest
argument_list|()
return|;
block|}
comment|/**      * Has the search operation timed out.      */
DECL|method|isTimedOut
specifier|public
name|boolean
name|isTimedOut
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|timedOut
argument_list|()
return|;
block|}
comment|/**      * Has the search operation terminated early due to reaching      *<code>terminateAfter</code>      */
DECL|method|isTerminatedEarly
specifier|public
name|Boolean
name|isTerminatedEarly
parameter_list|()
block|{
return|return
name|internalResponse
operator|.
name|terminatedEarly
argument_list|()
return|;
block|}
comment|/**      * How long the search took.      */
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
comment|/**      * How long the search took in milliseconds.      */
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
DECL|method|getFailedShards
specifier|public
name|int
name|getFailedShards
parameter_list|()
block|{
comment|// we don't return totalShards - successfulShards, we don't count "no shards available" as a failed shard, just don't
comment|// count it in the successful counter
return|return
name|shardFailures
operator|.
name|length
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
name|this
operator|.
name|shardFailures
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
DECL|method|scrollId
specifier|public
name|void
name|scrollId
parameter_list|(
name|String
name|scrollId
parameter_list|)
block|{
name|this
operator|.
name|scrollId
operator|=
name|scrollId
expr_stmt|;
block|}
DECL|class|Fields
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|_SCROLL_ID
specifier|static
specifier|final
name|XContentBuilderString
name|_SCROLL_ID
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_scroll_id"
argument_list|)
decl_stmt|;
DECL|field|_SHARDS
specifier|static
specifier|final
name|XContentBuilderString
name|_SHARDS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"_shards"
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
DECL|field|SUCCESSFUL
specifier|static
specifier|final
name|XContentBuilderString
name|SUCCESSFUL
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"successful"
argument_list|)
decl_stmt|;
DECL|field|FAILED
specifier|static
specifier|final
name|XContentBuilderString
name|FAILED
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failed"
argument_list|)
decl_stmt|;
DECL|field|FAILURES
specifier|static
specifier|final
name|XContentBuilderString
name|FAILURES
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"failures"
argument_list|)
decl_stmt|;
DECL|field|STATUS
specifier|static
specifier|final
name|XContentBuilderString
name|STATUS
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
DECL|field|INDEX
specifier|static
specifier|final
name|XContentBuilderString
name|INDEX
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"index"
argument_list|)
decl_stmt|;
DECL|field|SHARD
specifier|static
specifier|final
name|XContentBuilderString
name|SHARD
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"shard"
argument_list|)
decl_stmt|;
DECL|field|REASON
specifier|static
specifier|final
name|XContentBuilderString
name|REASON
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"reason"
argument_list|)
decl_stmt|;
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
DECL|field|TIMED_OUT
specifier|static
specifier|final
name|XContentBuilderString
name|TIMED_OUT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"timed_out"
argument_list|)
decl_stmt|;
DECL|field|TERMINATED_EARLY
specifier|static
specifier|final
name|XContentBuilderString
name|TERMINATED_EARLY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"terminated_early"
argument_list|)
decl_stmt|;
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
name|Fields
operator|.
name|_SCROLL_ID
argument_list|,
name|scrollId
argument_list|)
expr_stmt|;
block|}
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
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TIMED_OUT
argument_list|,
name|isTimedOut
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isTerminatedEarly
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TERMINATED_EARLY
argument_list|,
name|isTerminatedEarly
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|Fields
operator|.
name|_SHARDS
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
name|getTotalShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUCCESSFUL
argument_list|,
name|getSuccessfulShards
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|FAILED
argument_list|,
name|getFailedShards
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
name|Fields
operator|.
name|FAILURES
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
name|Fields
operator|.
name|INDEX
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
name|Fields
operator|.
name|SHARD
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
name|Fields
operator|.
name|STATUS
argument_list|,
name|shardFailure
operator|.
name|status
argument_list|()
operator|.
name|getStatus
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|REASON
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
return|return
name|builder
return|;
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
name|scrollId
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|tookInMillis
operator|=
name|in
operator|.
name|readVLong
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
name|out
operator|.
name|writeOptionalString
argument_list|(
name|scrollId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|tookInMillis
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
try|try
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|jsonBuilder
argument_list|()
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|toXContent
argument_list|(
name|builder
argument_list|,
name|EMPTY_PARAMS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|string
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|"{ \"error\" : \""
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"\"}"
return|;
block|}
block|}
block|}
end_class

end_unit

