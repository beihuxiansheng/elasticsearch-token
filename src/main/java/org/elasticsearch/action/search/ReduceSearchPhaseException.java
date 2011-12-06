begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  * A failure during a reduce phase (when receiving results from several shards, and reducing them  * into one or more results and possible actions).  *  *  */
end_comment

begin_class
DECL|class|ReduceSearchPhaseException
specifier|public
class|class
name|ReduceSearchPhaseException
extends|extends
name|SearchPhaseExecutionException
block|{
DECL|method|ReduceSearchPhaseException
specifier|public
name|ReduceSearchPhaseException
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
name|phaseName
argument_list|,
literal|"[reduce] "
operator|+
name|msg
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
block|}
DECL|method|ReduceSearchPhaseException
specifier|public
name|ReduceSearchPhaseException
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
name|phaseName
argument_list|,
literal|"[reduce] "
operator|+
name|msg
argument_list|,
name|cause
argument_list|,
name|shardFailures
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

