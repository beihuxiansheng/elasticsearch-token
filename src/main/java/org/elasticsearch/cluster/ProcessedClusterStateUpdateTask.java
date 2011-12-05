begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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

begin_comment
comment|/**  * An extension interface to {@link ClusterStateUpdateTask} that allows to be notified when  * the cluster state update has been processed.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|ProcessedClusterStateUpdateTask
specifier|public
interface|interface
name|ProcessedClusterStateUpdateTask
extends|extends
name|ClusterStateUpdateTask
block|{
comment|/**      * Called when the result of the {@link #execute(ClusterState)} have been processed      * properly by all listeners.      */
DECL|method|clusterStateProcessed
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

