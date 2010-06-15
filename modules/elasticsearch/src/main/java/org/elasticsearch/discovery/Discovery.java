begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ClusterState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|component
operator|.
name|LifecycleComponent
import|;
end_import

begin_comment
comment|/**  * A pluggable module allowing to implement discovery of other nodes, publishing of the cluster  * state to all nodes, electing a master of the cluster that raises cluster state change  * events.  *  * @author kimchy (shay.banon)  */
end_comment

begin_interface
DECL|interface|Discovery
specifier|public
interface|interface
name|Discovery
extends|extends
name|LifecycleComponent
argument_list|<
name|Discovery
argument_list|>
block|{
DECL|method|addListener
name|void
name|addListener
parameter_list|(
name|InitialStateDiscoveryListener
name|listener
parameter_list|)
function_decl|;
DECL|method|removeListener
name|void
name|removeListener
parameter_list|(
name|InitialStateDiscoveryListener
name|listener
parameter_list|)
function_decl|;
DECL|method|nodeDescription
name|String
name|nodeDescription
parameter_list|()
function_decl|;
comment|/**      * Publish all the changes to the cluster from the master (can be called just by the master). The publish      * process should not publish this state to the master as well! (the master is sending it...).      */
DECL|method|publish
name|void
name|publish
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

