begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.bulk
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|bulk
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
name|mapper
operator|.
name|Mapping
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

begin_interface
DECL|interface|MappingUpdatePerformer
specifier|public
interface|interface
name|MappingUpdatePerformer
block|{
comment|/**      * Update the mappings on the master.      */
DECL|method|updateMappings
name|void
name|updateMappings
parameter_list|(
name|Mapping
name|update
parameter_list|,
name|ShardId
name|shardId
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      *  Throws a {@code ReplicationOperation.RetryOnPrimaryException} if the operation needs to be      * retried on the primary due to the mappings not being present yet, or a different exception if      * updating the mappings on the master failed.      */
DECL|method|verifyMappings
name|void
name|verifyMappings
parameter_list|(
name|Mapping
name|update
parameter_list|,
name|ShardId
name|shardId
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

