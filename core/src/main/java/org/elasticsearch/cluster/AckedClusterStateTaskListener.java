begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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
name|Nullable
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_interface
DECL|interface|AckedClusterStateTaskListener
specifier|public
interface|interface
name|AckedClusterStateTaskListener
extends|extends
name|ClusterStateTaskListener
block|{
comment|/**      * Called to determine which nodes the acknowledgement is expected from      *      * @param discoveryNode a node      * @return true if the node is expected to send ack back, false otherwise      */
DECL|method|mustAck
name|boolean
name|mustAck
parameter_list|(
name|DiscoveryNode
name|discoveryNode
parameter_list|)
function_decl|;
comment|/**      * Called once all the nodes have acknowledged the cluster state update request. Must be      * very lightweight execution, since it gets executed on the cluster service thread.      *      * @param e optional error that might have been thrown      */
DECL|method|onAllNodesAcked
name|void
name|onAllNodesAcked
parameter_list|(
annotation|@
name|Nullable
name|Exception
name|e
parameter_list|)
function_decl|;
comment|/**      * Called once the acknowledgement timeout defined by      * {@link AckedClusterStateUpdateTask#ackTimeout()} has expired      */
DECL|method|onAckTimeout
name|void
name|onAckTimeout
parameter_list|()
function_decl|;
comment|/**      * Acknowledgement timeout, maximum time interval to wait for acknowledgements      */
DECL|method|ackTimeout
name|TimeValue
name|ackTimeout
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

