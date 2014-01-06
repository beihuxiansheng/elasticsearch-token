begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|IndexShardClosedException
import|;
end_import

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
comment|/**  * An engine is already closed.  *<p/>  *<p>Note, the relationship between shard and engine indicates that engine closed is shard closed, and  * we might get something slipping through the the shard and into the engine while the shard is closing.  *  *  */
end_comment

begin_class
DECL|class|EngineClosedException
specifier|public
class|class
name|EngineClosedException
extends|extends
name|IndexShardClosedException
block|{
DECL|method|EngineClosedException
specifier|public
name|EngineClosedException
parameter_list|(
name|ShardId
name|shardId
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
DECL|method|EngineClosedException
specifier|public
name|EngineClosedException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|Throwable
name|t
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

