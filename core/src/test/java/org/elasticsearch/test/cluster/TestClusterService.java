begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.cluster
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|ElasticsearchException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|Version
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
name|*
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
name|node
operator|.
name|DiscoveryNodes
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
name|Lifecycle
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
name|LifecycleListener
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|ESLoggerFactory
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
name|logging
operator|.
name|Loggers
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
name|common
operator|.
name|transport
operator|.
name|DummyTransportAddress
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
name|ConcurrentCollections
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
name|util
operator|.
name|concurrent
operator|.
name|FutureUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Queue
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
name|ScheduledFuture
import|;
end_import

begin_comment
comment|/** a class that simulate simple cluster service features, like state storage and listeners */
end_comment

begin_class
DECL|class|TestClusterService
specifier|public
class|class
name|TestClusterService
implements|implements
name|ClusterService
block|{
DECL|field|state
specifier|volatile
name|ClusterState
name|state
decl_stmt|;
DECL|field|listeners
specifier|private
specifier|final
name|Collection
argument_list|<
name|ClusterStateListener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|onGoingTimeouts
specifier|private
specifier|final
name|Queue
argument_list|<
name|NotifyTimeout
argument_list|>
name|onGoingTimeouts
init|=
name|ConcurrentCollections
operator|.
name|newQueue
argument_list|()
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|)
decl_stmt|;
DECL|method|TestClusterService
specifier|public
name|TestClusterService
parameter_list|()
block|{
name|this
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TestClusterService
specifier|public
name|TestClusterService
parameter_list|(
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|this
argument_list|(
name|ClusterState
operator|.
name|builder
argument_list|(
operator|new
name|ClusterName
argument_list|(
literal|"test"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|threadPool
argument_list|)
expr_stmt|;
block|}
DECL|method|TestClusterService
specifier|public
name|TestClusterService
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
name|this
argument_list|(
name|state
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|TestClusterService
specifier|public
name|TestClusterService
parameter_list|(
name|ClusterState
name|state
parameter_list|,
annotation|@
name|Nullable
name|ThreadPool
name|threadPool
parameter_list|)
block|{
if|if
condition|(
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|state
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|state
argument_list|)
operator|.
name|nodes
argument_list|(
name|DiscoveryNodes
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"test_node"
argument_list|,
name|DummyTransportAddress
operator|.
name|INSTANCE
argument_list|,
name|Version
operator|.
name|CURRENT
argument_list|)
argument_list|)
operator|.
name|localNodeId
argument_list|(
literal|"test_node"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
assert|assert
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|!=
literal|null
assert|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
block|}
comment|/** set the current state and trigger any registered listeners about the change, mimicking an update task */
DECL|method|setState
specifier|synchronized
specifier|public
name|ClusterState
name|setState
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
assert|assert
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|!=
literal|null
assert|;
comment|// make sure we have a version increment
name|state
operator|=
name|ClusterState
operator|.
name|builder
argument_list|(
name|state
argument_list|)
operator|.
name|version
argument_list|(
name|this
operator|.
name|state
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
return|return
name|setStateAndNotifyListeners
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|method|setStateAndNotifyListeners
specifier|private
name|ClusterState
name|setStateAndNotifyListeners
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
name|ClusterChangedEvent
name|event
init|=
operator|new
name|ClusterChangedEvent
argument_list|(
literal|"test"
argument_list|,
name|state
argument_list|,
name|this
operator|.
name|state
argument_list|)
decl_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
for|for
control|(
name|ClusterStateListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|clusterChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
return|return
name|state
return|;
block|}
comment|/** set the current state and trigger any registered listeners about the change */
DECL|method|setState
specifier|public
name|ClusterState
name|setState
parameter_list|(
name|ClusterState
operator|.
name|Builder
name|state
parameter_list|)
block|{
return|return
name|setState
argument_list|(
name|state
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|localNode
specifier|public
name|DiscoveryNode
name|localNode
parameter_list|()
block|{
return|return
name|state
operator|.
name|getNodes
argument_list|()
operator|.
name|localNode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|state
specifier|public
name|ClusterState
name|state
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
DECL|method|addInitialStateBlock
specifier|public
name|void
name|addInitialStateBlock
parameter_list|(
name|ClusterBlock
name|block
parameter_list|)
throws|throws
name|IllegalStateException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|removeInitialStateBlock
specifier|public
name|void
name|removeInitialStateBlock
parameter_list|(
name|ClusterBlock
name|block
parameter_list|)
throws|throws
name|IllegalStateException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|operationRouting
specifier|public
name|OperationRouting
name|operationRouting
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|addFirst
specifier|public
name|void
name|addFirst
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addLast
specifier|public
name|void
name|addLast
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|ClusterStateListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|NotifyTimeout
argument_list|>
name|it
init|=
name|onGoingTimeouts
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NotifyTimeout
name|timeout
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeout
operator|.
name|listener
operator|.
name|equals
argument_list|(
name|listener
argument_list|)
condition|)
block|{
name|timeout
operator|.
name|cancel
argument_list|()
expr_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|LocalNodeMasterListener
name|listener
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|LocalNodeMasterListener
name|listener
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|TimeValue
name|timeout
parameter_list|,
specifier|final
name|TimeoutClusterStateListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|threadPool
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TestClusterService wasn't initialized with a thread pool"
argument_list|)
throw|;
block|}
name|NotifyTimeout
name|notifyTimeout
init|=
operator|new
name|NotifyTimeout
argument_list|(
name|listener
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
name|notifyTimeout
operator|.
name|future
operator|=
name|threadPool
operator|.
name|schedule
argument_list|(
name|timeout
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
name|notifyTimeout
argument_list|)
expr_stmt|;
name|onGoingTimeouts
operator|.
name|add
argument_list|(
name|notifyTimeout
argument_list|)
expr_stmt|;
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|listener
operator|.
name|postAdded
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|submitStateUpdateTask
specifier|synchronized
specifier|public
name|void
name|submitStateUpdateTask
parameter_list|(
name|String
name|source
parameter_list|,
name|Priority
name|priority
parameter_list|,
name|ClusterStateUpdateTask
name|updateTask
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"processing [{}]"
argument_list|,
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
operator|==
literal|false
operator|&&
name|updateTask
operator|.
name|runOnlyOnMaster
argument_list|()
condition|)
block|{
name|updateTask
operator|.
name|onNoLongerMaster
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"failed [{}], no longer master"
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return;
block|}
name|ClusterState
name|newState
decl_stmt|;
name|ClusterState
name|previousClusterState
init|=
name|state
decl_stmt|;
try|try
block|{
name|newState
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
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"failed to process cluster state update task ["
operator|+
name|source
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|setStateAndNotifyListeners
argument_list|(
name|newState
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateTask
operator|instanceof
name|ProcessedClusterStateUpdateTask
condition|)
block|{
operator|(
operator|(
name|ProcessedClusterStateUpdateTask
operator|)
name|updateTask
operator|)
operator|.
name|clusterStateProcessed
argument_list|(
name|source
argument_list|,
name|previousClusterState
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"finished [{}]"
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|submitStateUpdateTask
specifier|public
name|void
name|submitStateUpdateTask
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterStateUpdateTask
name|updateTask
parameter_list|)
block|{
name|submitStateUpdateTask
argument_list|(
name|source
argument_list|,
name|Priority
operator|.
name|NORMAL
argument_list|,
name|updateTask
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMaxTaskWaitTime
specifier|public
name|TimeValue
name|getMaxTaskWaitTime
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|pendingTasks
specifier|public
name|List
argument_list|<
name|PendingClusterTask
argument_list|>
name|pendingTasks
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|numberOfPendingTasks
specifier|public
name|int
name|numberOfPendingTasks
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|lifecycleState
specifier|public
name|Lifecycle
operator|.
name|State
name|lifecycleState
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|addLifecycleListener
specifier|public
name|void
name|addLifecycleListener
parameter_list|(
name|LifecycleListener
name|listener
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|removeLifecycleListener
specifier|public
name|void
name|removeLifecycleListener
parameter_list|(
name|LifecycleListener
name|listener
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|start
specifier|public
name|ClusterService
name|start
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|stop
specifier|public
name|ClusterService
name|stop
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|ElasticsearchException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|class|NotifyTimeout
class|class
name|NotifyTimeout
implements|implements
name|Runnable
block|{
DECL|field|listener
specifier|final
name|TimeoutClusterStateListener
name|listener
decl_stmt|;
DECL|field|timeout
specifier|final
name|TimeValue
name|timeout
decl_stmt|;
DECL|field|future
specifier|volatile
name|ScheduledFuture
name|future
decl_stmt|;
DECL|method|NotifyTimeout
name|NotifyTimeout
parameter_list|(
name|TimeoutClusterStateListener
name|listener
parameter_list|,
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
DECL|method|cancel
specifier|public
name|void
name|cancel
parameter_list|()
block|{
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|future
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|future
operator|!=
literal|null
operator|&&
name|future
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
return|return;
block|}
name|listener
operator|.
name|onTimeout
argument_list|(
name|this
operator|.
name|timeout
argument_list|)
expr_stmt|;
comment|// note, we rely on the listener to remove itself in case of timeout if needed
block|}
block|}
block|}
end_class

end_unit

