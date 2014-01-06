begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one  * or more contributor license agreements. See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elasticsearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_comment
comment|/**  * Handles responses obtained when publishing a new cluster state from master to all non master nodes.  * Allows to await a reply from all non master nodes, up to a timeout  */
end_comment

begin_interface
DECL|interface|ClusterStatePublishResponseHandler
specifier|public
interface|interface
name|ClusterStatePublishResponseHandler
block|{
comment|/**      * Called for each response obtained from non master nodes      * @param node the node that replied to the publish event      */
DECL|method|onResponse
name|void
name|onResponse
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
function_decl|;
comment|/**      * Called for each failure obtained from non master nodes      * @param node the node that replied to the publish event      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|Throwable
name|t
parameter_list|)
function_decl|;
comment|/**      * Allows to wait for all non master nodes to reply to the publish event up to a timeout      * @param timeout the timeout      * @return true if the timeout expired or not, false otherwise      * @throws InterruptedException      */
DECL|method|awaitAllNodes
name|boolean
name|awaitAllNodes
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

