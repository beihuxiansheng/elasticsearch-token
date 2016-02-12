begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.replication
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|replication
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
name|ActionRequest
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
comment|/**  * A replication request that has no more information than ReplicationRequest.  * Unfortunately ReplicationRequest can't be declared as a type parameter  * because it has a self referential type parameter of its own. So use this  * instead.  */
end_comment

begin_class
DECL|class|BasicReplicationRequest
specifier|public
class|class
name|BasicReplicationRequest
extends|extends
name|ReplicationRequest
argument_list|<
name|BasicReplicationRequest
argument_list|>
block|{
DECL|method|BasicReplicationRequest
specifier|public
name|BasicReplicationRequest
parameter_list|()
block|{     }
comment|/**      * Creates a new request with resolved shard id      */
DECL|method|BasicReplicationRequest
specifier|public
name|BasicReplicationRequest
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
comment|/**      * Copy constructor that creates a new request that is a copy of the one      * provided as an argument.      */
DECL|method|BasicReplicationRequest
specifier|protected
name|BasicReplicationRequest
parameter_list|(
name|BasicReplicationRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

