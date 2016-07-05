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
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
import|;
end_import

begin_class
DECL|class|SearchPhaseExecutionException
specifier|public
class|class
name|SearchPhaseExecutionException
extends|extends
name|ElasticsearchException
block|{
DECL|field|phaseName
specifier|private
specifier|final
name|String
name|phaseName
decl_stmt|;
DECL|field|shardFailures
specifier|private
specifier|final
name|ShardSearchFailure
index|[]
name|shardFailures
decl_stmt|;
DECL|method|SearchPhaseExecutionException
specifier|public
name|SearchPhaseExecutionException
parameter_list|(
name|String
name|phaseName
parameter_list|,
name|String
name|msg
parameter_list|,
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|)
block|{
name|this
argument_list|(
name|phaseName
argument_list|,
name|msg
argument_list|,
literal|null
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
block|}
DECL|method|SearchPhaseExecutionException
specifier|public
name|SearchPhaseExecutionException
parameter_list|(
name|String
name|phaseName
parameter_list|,
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|deduplicateCause
argument_list|(
name|cause
argument_list|,
name|shardFailures
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|phaseName
operator|=
name|phaseName
expr_stmt|;
name|this
operator|.
name|shardFailures
operator|=
name|shardFailures
expr_stmt|;
block|}
DECL|method|SearchPhaseExecutionException
specifier|public
name|SearchPhaseExecutionException
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
name|phaseName
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
name|int
name|numFailures
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|shardFailures
operator|=
operator|new
name|ShardSearchFailure
index|[
name|numFailures
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
name|numFailures
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
name|ShardSearchFailure
operator|.
name|readShardSearchFailure
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|writeOptionalString
argument_list|(
name|phaseName
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
name|failure
range|:
name|shardFailures
control|)
block|{
name|failure
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|deduplicateCause
specifier|private
specifier|static
name|Throwable
name|deduplicateCause
parameter_list|(
name|Throwable
name|cause
parameter_list|,
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|)
block|{
if|if
condition|(
name|shardFailures
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"shardSearchFailures must not be null"
argument_list|)
throw|;
block|}
comment|// if the cause of this exception is also the cause of one of the shard failures we don't add it
comment|// to prevent duplication in stack traces rendered to the REST layer
if|if
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ShardSearchFailure
name|failure
range|:
name|shardFailures
control|)
block|{
if|if
condition|(
name|failure
operator|.
name|getCause
argument_list|()
operator|==
name|cause
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
return|return
name|cause
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
if|if
condition|(
name|shardFailures
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// if no successful shards, it means no active shards, so just return SERVICE_UNAVAILABLE
return|return
name|RestStatus
operator|.
name|SERVICE_UNAVAILABLE
return|;
block|}
name|RestStatus
name|status
init|=
name|shardFailures
index|[
literal|0
index|]
operator|.
name|status
argument_list|()
decl_stmt|;
if|if
condition|(
name|shardFailures
operator|.
name|length
operator|>
literal|1
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
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
if|if
condition|(
name|shardFailures
index|[
name|i
index|]
operator|.
name|status
argument_list|()
operator|.
name|getStatus
argument_list|()
operator|>=
literal|500
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
block|}
return|return
name|status
return|;
block|}
DECL|method|shardFailures
specifier|public
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|()
block|{
return|return
name|shardFailures
return|;
block|}
DECL|method|getCause
specifier|public
name|Throwable
name|getCause
parameter_list|()
block|{
name|Throwable
name|cause
init|=
name|super
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|==
literal|null
condition|)
block|{
comment|// fall back to guessed root cause
for|for
control|(
name|ElasticsearchException
name|rootCause
range|:
name|guessRootCauses
argument_list|()
control|)
block|{
return|return
name|rootCause
return|;
block|}
block|}
return|return
name|cause
return|;
block|}
DECL|method|buildMessage
specifier|private
specifier|static
name|String
name|buildMessage
parameter_list|(
name|String
name|phaseName
parameter_list|,
name|String
name|msg
parameter_list|,
name|ShardSearchFailure
index|[]
name|shardFailures
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Failed to execute phase ["
argument_list|)
operator|.
name|append
argument_list|(
name|phaseName
argument_list|)
operator|.
name|append
argument_list|(
literal|"], "
argument_list|)
operator|.
name|append
argument_list|(
name|msg
argument_list|)
expr_stmt|;
if|if
condition|(
name|shardFailures
operator|!=
literal|null
operator|&&
name|shardFailures
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"; shardFailures "
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
name|sb
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
name|shardFailure
operator|.
name|shard
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|shardFailure
operator|.
name|reason
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
operator|.
name|append
argument_list|(
name|shardFailure
operator|.
name|reason
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|innerToXContent
specifier|protected
name|void
name|innerToXContent
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
literal|"phase"
argument_list|,
name|phaseName
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|group
init|=
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"group_shard_failures"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// we group by default
name|builder
operator|.
name|field
argument_list|(
literal|"grouped"
argument_list|,
name|group
argument_list|)
expr_stmt|;
comment|// notify that it's grouped
name|builder
operator|.
name|field
argument_list|(
literal|"failed_shards"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
name|ShardOperationFailedException
index|[]
name|failures
init|=
name|params
operator|.
name|paramAsBoolean
argument_list|(
literal|"group_shard_failures"
argument_list|,
literal|true
argument_list|)
condition|?
name|ExceptionsHelper
operator|.
name|groupBy
argument_list|(
name|shardFailures
argument_list|)
else|:
name|shardFailures
decl_stmt|;
for|for
control|(
name|ShardOperationFailedException
name|failure
range|:
name|failures
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|failure
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
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
name|super
operator|.
name|innerToXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|causeToXContent
specifier|protected
name|void
name|causeToXContent
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
name|super
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// if the cause is null we inject a guessed root cause that will then be rendered twice so wi disable it manually
name|super
operator|.
name|causeToXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|guessRootCauses
specifier|public
name|ElasticsearchException
index|[]
name|guessRootCauses
parameter_list|()
block|{
name|ShardOperationFailedException
index|[]
name|failures
init|=
name|ExceptionsHelper
operator|.
name|groupBy
argument_list|(
name|shardFailures
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ElasticsearchException
argument_list|>
name|rootCauses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|failures
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|ShardOperationFailedException
name|failure
range|:
name|failures
control|)
block|{
name|ElasticsearchException
index|[]
name|guessRootCauses
init|=
name|ElasticsearchException
operator|.
name|guessRootCauses
argument_list|(
name|failure
operator|.
name|getCause
argument_list|()
argument_list|)
decl_stmt|;
name|rootCauses
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|guessRootCauses
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rootCauses
operator|.
name|toArray
argument_list|(
operator|new
name|ElasticsearchException
index|[
literal|0
index|]
argument_list|)
return|;
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
name|buildMessage
argument_list|(
name|phaseName
argument_list|,
name|getMessage
argument_list|()
argument_list|,
name|shardFailures
argument_list|)
return|;
block|}
DECL|method|getPhaseName
specifier|public
name|String
name|getPhaseName
parameter_list|()
block|{
return|return
name|phaseName
return|;
block|}
block|}
end_class

end_unit

