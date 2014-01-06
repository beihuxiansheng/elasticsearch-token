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

begin_comment
comment|/**  * A task that can update the cluster state.  */
end_comment

begin_interface
DECL|interface|ClusterStateUpdateTask
specifier|public
interface|interface
name|ClusterStateUpdateTask
block|{
comment|/**      * Update the cluster state based on the current state. Return the *same instance* if no state      * should be changed.      */
DECL|method|execute
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * A callback called when execute fails.      */
DECL|method|onFailure
name|void
name|onFailure
parameter_list|(
name|String
name|source
parameter_list|,
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

