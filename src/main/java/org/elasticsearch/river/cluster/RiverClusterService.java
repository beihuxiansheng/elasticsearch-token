begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.river.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|river
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
name|ElasticSearchException
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
name|ClusterService
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
name|ClusterState
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
name|AbstractLifecycleComponent
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
name|inject
operator|.
name|Inject
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
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportService
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
operator|.
name|newSingleThreadExecutor
import|;
end_import

begin_import
import|import static
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
name|EsExecutors
operator|.
name|daemonThreadFactory
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|RiverClusterService
specifier|public
class|class
name|RiverClusterService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|RiverClusterService
argument_list|>
block|{
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|publishAction
specifier|private
specifier|final
name|PublishRiverClusterStateAction
name|publishAction
decl_stmt|;
DECL|field|clusterStateListeners
specifier|private
specifier|final
name|List
argument_list|<
name|RiverClusterStateListener
argument_list|>
name|clusterStateListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|RiverClusterStateListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|updateTasksExecutor
specifier|private
specifier|volatile
name|ExecutorService
name|updateTasksExecutor
decl_stmt|;
DECL|field|clusterState
specifier|private
specifier|volatile
name|RiverClusterState
name|clusterState
init|=
name|RiverClusterState
operator|.
name|builder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|Inject
DECL|method|RiverClusterService
specifier|public
name|RiverClusterService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|publishAction
operator|=
operator|new
name|PublishRiverClusterStateAction
argument_list|(
name|settings
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
operator|new
name|UpdateClusterStateListener
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|this
operator|.
name|updateTasksExecutor
operator|=
name|newSingleThreadExecutor
argument_list|(
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"riverClusterService#updateTask"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|updateTasksExecutor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
try|try
block|{
name|updateTasksExecutor
operator|.
name|awaitTermination
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|RiverClusterStateListener
name|listener
parameter_list|)
block|{
name|clusterStateListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|RiverClusterStateListener
name|listener
parameter_list|)
block|{
name|clusterStateListeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * The current state.      */
DECL|method|state
specifier|public
name|ClusterState
name|state
parameter_list|()
block|{
return|return
name|clusterService
operator|.
name|state
argument_list|()
return|;
block|}
DECL|method|submitStateUpdateTask
specifier|public
name|void
name|submitStateUpdateTask
parameter_list|(
specifier|final
name|String
name|source
parameter_list|,
specifier|final
name|RiverClusterStateUpdateTask
name|updateTask
parameter_list|)
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
return|return;
block|}
name|updateTasksExecutor
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
operator|!
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"processing [{}]: ignoring, cluster_service not started"
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"processing [{}]: execute"
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|RiverClusterState
name|previousClusterState
init|=
name|clusterState
decl_stmt|;
try|try
block|{
name|clusterState
operator|=
name|updateTask
operator|.
name|execute
argument_list|(
name|previousClusterState
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"failed to execute cluster state update, state:\nversion ["
argument_list|)
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"], source ["
argument_list|)
operator|.
name|append
argument_list|(
name|source
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|warn
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|previousClusterState
operator|!=
name|clusterState
condition|)
block|{
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
comment|// only the master controls the version numbers
name|clusterState
operator|=
operator|new
name|RiverClusterState
argument_list|(
name|clusterState
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|,
name|clusterState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// we got this cluster state from the master, filter out based on versions (don't call listeners)
if|if
condition|(
name|clusterState
operator|.
name|version
argument_list|()
operator|<
name|previousClusterState
operator|.
name|version
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"got old cluster state ["
operator|+
name|clusterState
operator|.
name|version
argument_list|()
operator|+
literal|"<"
operator|+
name|previousClusterState
operator|.
name|version
argument_list|()
operator|+
literal|"] from source ["
operator|+
name|source
operator|+
literal|"], ignoring"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"cluster state updated:\nversion ["
argument_list|)
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|version
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"], source ["
argument_list|)
operator|.
name|append
argument_list|(
name|source
argument_list|)
operator|.
name|append
argument_list|(
literal|"]\n"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"cluster state updated, version [{}], source [{}]"
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
name|RiverClusterChangedEvent
name|clusterChangedEvent
init|=
operator|new
name|RiverClusterChangedEvent
argument_list|(
name|source
argument_list|,
name|clusterState
argument_list|,
name|previousClusterState
argument_list|)
decl_stmt|;
for|for
control|(
name|RiverClusterStateListener
name|listener
range|:
name|clusterStateListeners
control|)
block|{
name|listener
operator|.
name|riverClusterChanged
argument_list|(
name|clusterChangedEvent
argument_list|)
expr_stmt|;
block|}
comment|// if we are the master, publish the new state to all nodes
if|if
condition|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|publishAction
operator|.
name|publish
argument_list|(
name|clusterState
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"processing [{}]: done applying updated cluster_state"
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"processing [{}]: no change in cluster_state"
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|UpdateClusterStateListener
specifier|private
class|class
name|UpdateClusterStateListener
implements|implements
name|PublishRiverClusterStateAction
operator|.
name|NewClusterStateListener
block|{
annotation|@
name|Override
DECL|method|onNewClusterState
specifier|public
name|void
name|onNewClusterState
parameter_list|(
specifier|final
name|RiverClusterState
name|clusterState
parameter_list|)
block|{
name|ClusterState
name|state
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"master should not receive new cluster state from [{}]"
argument_list|,
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|masterNode
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|submitStateUpdateTask
argument_list|(
literal|"received_state"
argument_list|,
operator|new
name|RiverClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RiverClusterState
name|execute
parameter_list|(
name|RiverClusterState
name|currentState
parameter_list|)
block|{
return|return
name|clusterState
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

