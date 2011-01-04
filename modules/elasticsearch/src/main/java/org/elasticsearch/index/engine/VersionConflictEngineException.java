begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|VersionConflictEngineException
specifier|public
class|class
name|VersionConflictEngineException
extends|extends
name|EngineException
block|{
DECL|method|VersionConflictEngineException
specifier|public
name|VersionConflictEngineException
parameter_list|(
name|ShardId
name|shardId
parameter_list|,
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|,
name|long
name|current
parameter_list|,
name|long
name|required
parameter_list|)
block|{
name|super
argument_list|(
name|shardId
argument_list|,
literal|"["
operator|+
name|type
operator|+
literal|"]["
operator|+
name|id
operator|+
literal|"]: version conflict, current ["
operator|+
name|current
operator|+
literal|"], required ["
operator|+
name|required
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

