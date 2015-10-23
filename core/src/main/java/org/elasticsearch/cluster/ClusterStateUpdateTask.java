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
name|Priority
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
implements|implements
name|ClusterStateTaskConfig
implements|,
name|ClusterStateTaskExecutor
argument_list|<
name|Void
argument_list|>
implements|,
name|ClusterStateTaskListener
block|{
DECL|field|priority
specifier|final
specifier|private
name|Priority
name|priority
decl_stmt|;
DECL|method|ClusterStateUpdateTask
specifier|public
name|ClusterStateUpdateTask
parameter_list|()
block|{
name|this
argument_list|(
name|Priority
operator|.
name|NORMAL
argument_list|)
expr_stmt|;
block|}
DECL|method|ClusterStateUpdateTask
specifier|public
name|ClusterStateUpdateTask
parameter_list|(
name|Priority
name|priority
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|priority
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|execute
specifier|final
specifier|public
name|Result
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|,
name|List
argument_list|<
name|Void
argument_list|>
name|tasks
parameter_list|)
throws|throws
name|Exception
block|{
name|ClusterState
name|result
init|=
name|execute
argument_list|(
name|currentState
argument_list|)
decl_stmt|;
return|return
operator|new
name|Result
argument_list|(
name|result
argument_list|,
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
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
comment|/**      * If the cluster state update task wasn't processed by the provided timeout, call      * {@link #onFailure(String, Throwable)}. May return null to indicate no timeout is needed (default).      */
annotation|@
name|Nullable
DECL|method|timeout
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|priority
specifier|public
name|Priority
name|priority
parameter_list|()
block|{
return|return
name|priority
return|;
block|}
block|}
end_class

end_unit

