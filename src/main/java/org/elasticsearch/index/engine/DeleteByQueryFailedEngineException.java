begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.engine
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|engine
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DeleteByQueryFailedEngineException
specifier|public
class|class
name|DeleteByQueryFailedEngineException
extends|extends
name|EngineException
block|{
DECL|method|DeleteByQueryFailedEngineException
specifier|public
name|DeleteByQueryFailedEngineException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Engine
operator|.
name|DeleteByQuery
name|deleteByQuery
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
literal|"Delete by query failed for ["
operator|+
name|deleteByQuery
operator|.
name|query
argument_list|()
operator|+
literal|"]"
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

