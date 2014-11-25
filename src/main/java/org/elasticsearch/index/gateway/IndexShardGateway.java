begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
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
name|IndexShardComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|recovery
operator|.
name|RecoveryState
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_interface
DECL|interface|IndexShardGateway
specifier|public
interface|interface
name|IndexShardGateway
extends|extends
name|IndexShardComponent
extends|,
name|Closeable
block|{
DECL|method|type
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * The last / on going recovery status.      */
DECL|method|recoveryState
name|RecoveryState
name|recoveryState
parameter_list|()
function_decl|;
comment|/**      * Recovers the state of the shard from the gateway.      */
DECL|method|recover
name|void
name|recover
parameter_list|(
name|boolean
name|indexShouldExists
parameter_list|,
name|RecoveryState
name|recoveryState
parameter_list|)
throws|throws
name|IndexShardGatewayRecoveryException
function_decl|;
block|}
end_interface

end_unit

