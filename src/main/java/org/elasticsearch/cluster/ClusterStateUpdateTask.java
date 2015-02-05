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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|EsRejectedExecutionException
import|;
end_import

begin_comment
comment|/**  * A task that can update the cluster state.  */
end_comment

begin_class
DECL|class|ClusterStateUpdateTask
specifier|abstract
specifier|public
class|class
name|ClusterStateUpdateTask
block|{
comment|/**      * Update the cluster state based on the current state. Return the *same instance* if no state      * should be changed.      */
DECL|method|execute
specifier|abstract
specifier|public
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
specifier|abstract
specifier|public
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
comment|/**      * indicates whether this task should only run if current node is master      */
DECL|method|runOnlyOnMaster
specifier|public
name|boolean
name|runOnlyOnMaster
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**      * called when the task was rejected because the local node is no longer master      */
DECL|method|onNoLongerMaster
specifier|public
name|void
name|onNoLongerMaster
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|onFailure
argument_list|(
name|source
argument_list|,
operator|new
name|EsRejectedExecutionException
argument_list|(
literal|"no longer master. source: ["
operator|+
name|source
operator|+
literal|"]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

