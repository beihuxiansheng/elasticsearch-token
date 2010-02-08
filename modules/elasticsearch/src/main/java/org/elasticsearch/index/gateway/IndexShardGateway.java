begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|deletionpolicy
operator|.
name|SnapshotIndexCommit
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
name|IndexShardComponent
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
name|translog
operator|.
name|Translog
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|IndexShardGateway
specifier|public
interface|interface
name|IndexShardGateway
extends|extends
name|IndexShardComponent
block|{
comment|/**      * Recovers the state of the shard from the gateway.      */
DECL|method|recover
name|RecoveryStatus
name|recover
parameter_list|()
throws|throws
name|IndexShardGatewayRecoveryException
function_decl|;
comment|/**      * Snapshots the given shard into the gateway.      */
DECL|method|snapshot
name|void
name|snapshot
parameter_list|(
name|SnapshotIndexCommit
name|snapshotIndexCommit
parameter_list|,
name|Translog
operator|.
name|Snapshot
name|translogSnapshot
parameter_list|)
function_decl|;
comment|/**      * Returns<tt>true</tt> if this gateway requires scheduling management for snapshot      * operations.      */
DECL|method|requiresSnapshotScheduling
name|boolean
name|requiresSnapshotScheduling
parameter_list|()
function_decl|;
DECL|method|close
name|void
name|close
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

