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
name|ElasticSearchException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|SearchPhaseExecutionException
specifier|public
class|class
name|SearchPhaseExecutionException
extends|extends
name|ElasticSearchException
block|{
DECL|field|phaseName
specifier|private
specifier|final
name|String
name|phaseName
decl_stmt|;
DECL|field|shardFailures
specifier|private
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
name|super
argument_list|(
name|buildMessage
argument_list|(
name|phaseName
argument_list|,
name|msg
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
name|buildMessage
argument_list|(
name|phaseName
argument_list|,
name|msg
argument_list|,
name|shardFailures
argument_list|)
argument_list|,
name|cause
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
DECL|method|phaseName
specifier|public
name|String
name|phaseName
parameter_list|()
block|{
return|return
name|phaseName
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
DECL|method|buildMessage
specifier|private
specifier|static
specifier|final
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
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

