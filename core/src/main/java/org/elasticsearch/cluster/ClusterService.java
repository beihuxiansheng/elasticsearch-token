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
name|block
operator|.
name|ClusterBlock
import|;
end_import

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
name|cluster
operator|.
name|routing
operator|.
name|OperationRouting
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|service
operator|.
name|PendingClusterTask
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
name|component
operator|.
name|LifecycleComponent
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
comment|/**  * The cluster service allowing to both register for cluster state events ({@link ClusterStateListener})  * and submit state update tasks ({@link ClusterStateUpdateTask}.  */
end_comment

begin_interface
DECL|interface|ClusterService
specifier|public
interface|interface
name|ClusterService
extends|extends
name|LifecycleComponent
argument_list|<
name|ClusterService
argument_list|>
block|{
comment|/**      * The local node.      */
DECL|method|localNode
name|DiscoveryNode
name|localNode
parameter_list|()
function_decl|;
comment|/**      * The current state.      */
DECL|method|state
name|ClusterState
name|state
parameter_list|()
function_decl|;
comment|/**      * Adds an initial block to be set on the first cluster state created.      */
DECL|method|addInitialStateBlock
name|void
name|addInitialStateBlock
parameter_list|(
name|ClusterBlock
name|block
parameter_list|)
throws|throws
name|IllegalStateException
function_decl|;
comment|/**      * Remove an initial block to be set on the first cluster state created.      */
DECL|method|removeInitialStateBlock
name|void
name|removeInitialStateBlock
parameter_list|(
name|ClusterBlock
name|block
parameter_list|)
throws|throws
name|IllegalStateException
function_decl|;
comment|/**      * The operation routing.      */
DECL|method|operationRouting
name|OperationRouting
name|operationRouting
parameter_list|()
function_decl|;
comment|/**      * Adds a priority listener for updated cluster states.      */
DECL|method|addFirst
name|void
name|addFirst
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Adds last listener.      */
DECL|method|addLast
name|void
name|addLast
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Adds a listener for updated cluster states.      */
DECL|method|add
name|void
name|add
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Removes a listener for updated cluster states.      */
DECL|method|remove
name|void
name|remove
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Add a listener for on/off local node master events      */
DECL|method|add
name|void
name|add
parameter_list|(
name|LocalNodeMasterListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Remove the given listener for on/off local master events      */
DECL|method|remove
name|void
name|remove
parameter_list|(
name|LocalNodeMasterListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Adds a cluster state listener that will timeout after the provided timeout,      * and is executed after the clusterstate has been successfully applied ie. is      * in state {@link org.elasticsearch.cluster.ClusterState.ClusterStateStatus#APPLIED}      * NOTE: a {@code null} timeout means that the listener will never be removed      * automatically      */
DECL|method|add
name|void
name|add
parameter_list|(
annotation|@
name|Nullable
name|TimeValue
name|timeout
parameter_list|,
name|TimeoutClusterStateListener
name|listener
parameter_list|)
function_decl|;
comment|/**      * Submits a task that will update the cluster state.      */
DECL|method|submitStateUpdateTask
name|void
name|submitStateUpdateTask
parameter_list|(
specifier|final
name|String
name|source
parameter_list|,
name|Priority
name|priority
parameter_list|,
specifier|final
name|ClusterStateUpdateTask
name|updateTask
parameter_list|)
function_decl|;
comment|/**      * Submits a task that will update the cluster state (the task has a default priority of {@link Priority#NORMAL}).      */
DECL|method|submitStateUpdateTask
name|void
name|submitStateUpdateTask
parameter_list|(
specifier|final
name|String
name|source
parameter_list|,
specifier|final
name|ClusterStateUpdateTask
name|updateTask
parameter_list|)
function_decl|;
comment|/**      * Returns the tasks that are pending.      */
DECL|method|pendingTasks
name|List
argument_list|<
name|PendingClusterTask
argument_list|>
name|pendingTasks
parameter_list|()
function_decl|;
comment|/**      * Returns the number of currently pending tasks.      */
DECL|method|numberOfPendingTasks
name|int
name|numberOfPendingTasks
parameter_list|()
function_decl|;
comment|/**      * Returns the maximum wait time for tasks in the queue      *      * @returns A zero time value if the queue is empty, otherwise the time value oldest task waiting in the queue      */
DECL|method|getMaxTaskWaitTime
name|TimeValue
name|getMaxTaskWaitTime
parameter_list|()
function_decl|;
block|}
end_interface

end_unit
