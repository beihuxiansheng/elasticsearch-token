begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.service
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|service
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
name|discovery
operator|.
name|DiscoveryService
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
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
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
name|util
operator|.
name|guice
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
name|util
operator|.
name|settings
operator|.
name|Settings
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
name|ScheduledFuture
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
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterState
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|TimeValue
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|concurrent
operator|.
name|DynamicExecutors
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|InternalClusterService
specifier|public
class|class
name|InternalClusterService
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|ClusterService
argument_list|>
implements|implements
name|ClusterService
block|{
DECL|field|timeoutInterval
specifier|private
specifier|final
name|TimeValue
name|timeoutInterval
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|discoveryService
specifier|private
specifier|final
name|DiscoveryService
name|discoveryService
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|updateTasksExecutor
specifier|private
specifier|volatile
name|ExecutorService
name|updateTasksExecutor
decl_stmt|;
DECL|field|clusterStateListeners
specifier|private
specifier|final
name|List
argument_list|<
name|ClusterStateListener
argument_list|>
name|clusterStateListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ClusterStateListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|clusterStateTimeoutListeners
specifier|private
specifier|final
name|List
argument_list|<
name|TimeoutHolder
argument_list|>
name|clusterStateTimeoutListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|TimeoutHolder
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|scheduledFuture
specifier|private
specifier|volatile
name|ScheduledFuture
name|scheduledFuture
decl_stmt|;
DECL|field|clusterState
specifier|private
specifier|volatile
name|ClusterState
name|clusterState
init|=
name|newClusterStateBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|InternalClusterService
annotation|@
name|Inject
specifier|public
name|InternalClusterService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|DiscoveryService
name|discoveryService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|discoveryService
operator|=
name|discoveryService
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|timeoutInterval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"timeoutInterval"
argument_list|,
name|timeValueMillis
argument_list|(
literal|500
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
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
literal|"clusterService#updateTask"
argument_list|)
argument_list|)
expr_stmt|;
name|scheduledFuture
operator|=
name|threadPool
operator|.
name|scheduleWithFixedDelay
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
name|long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|TimeoutHolder
name|holder
range|:
name|clusterStateTimeoutListeners
control|)
block|{
if|if
condition|(
operator|(
name|timestamp
operator|-
name|holder
operator|.
name|timestamp
operator|)
operator|>
name|holder
operator|.
name|timeout
operator|.
name|millis
argument_list|()
condition|)
block|{
name|clusterStateTimeoutListeners
operator|.
name|remove
argument_list|(
name|holder
argument_list|)
expr_stmt|;
name|InternalClusterService
operator|.
name|this
operator|.
name|threadPool
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
name|holder
operator|.
name|listener
operator|.
name|onTimeout
argument_list|(
name|holder
operator|.
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|,
name|timeoutInterval
argument_list|)
expr_stmt|;
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|scheduledFuture
operator|.
name|cancel
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|TimeoutHolder
name|holder
range|:
name|clusterStateTimeoutListeners
control|)
block|{
name|holder
operator|.
name|listener
operator|.
name|onTimeout
argument_list|(
name|holder
operator|.
name|timeout
argument_list|)
expr_stmt|;
block|}
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
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|state
specifier|public
name|ClusterState
name|state
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterState
return|;
block|}
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|ClusterStateListener
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
name|ClusterStateListener
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
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|TimeValue
name|timeout
parameter_list|,
name|TimeoutClusterStateListener
name|listener
parameter_list|)
block|{
name|clusterStateTimeoutListeners
operator|.
name|add
argument_list|(
operator|new
name|TimeoutHolder
argument_list|(
name|listener
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|timeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|(
name|TimeoutClusterStateListener
name|listener
parameter_list|)
block|{
name|clusterStateTimeoutListeners
operator|.
name|remove
argument_list|(
operator|new
name|TimeoutHolder
argument_list|(
name|listener
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
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
name|ClusterStateUpdateTask
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
return|return;
block|}
name|ClusterState
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
literal|"Failed to execute cluster state update, state:\nVersion ["
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
name|sb
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|readOnlyRoutingNodes
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
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
name|clusterState
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
name|ClusterState
argument_list|(
name|clusterState
operator|.
name|version
argument_list|()
operator|+
literal|1
argument_list|,
name|clusterState
operator|.
name|metaData
argument_list|()
argument_list|,
name|clusterState
operator|.
name|routingTable
argument_list|()
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
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
name|info
argument_list|(
literal|"Got old cluster state ["
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
literal|"Cluster State updated:\nVersion ["
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
name|sb
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|routingTable
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|clusterState
operator|.
name|readOnlyRoutingNodes
argument_list|()
operator|.
name|prettyPrint
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Cluster state updated, version [{}], source [{}]"
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
name|ClusterChangedEvent
name|clusterChangedEvent
init|=
operator|new
name|ClusterChangedEvent
argument_list|(
name|source
argument_list|,
name|clusterState
argument_list|,
name|previousClusterState
argument_list|,
name|discoveryService
operator|.
name|firstMaster
argument_list|()
argument_list|)
decl_stmt|;
comment|// new cluster state, notify all listeners
specifier|final
name|DiscoveryNodes
operator|.
name|Delta
name|nodesDelta
init|=
name|clusterChangedEvent
operator|.
name|nodesDelta
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodesDelta
operator|.
name|hasChanges
argument_list|()
operator|&&
name|logger
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|String
name|summary
init|=
name|nodesDelta
operator|.
name|shortSummary
argument_list|()
decl_stmt|;
if|if
condition|(
name|summary
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|summary
argument_list|)
expr_stmt|;
block|}
block|}
comment|// TODO, do this in parallel (and wait)
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodesDelta
operator|.
name|addedNodes
argument_list|()
control|)
block|{
try|try
block|{
name|transportService
operator|.
name|connectToNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO, need to mark this node as failed...
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to connect to node ["
operator|+
name|node
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|TimeoutHolder
name|timeoutHolder
range|:
name|clusterStateTimeoutListeners
control|)
block|{
name|timeoutHolder
operator|.
name|listener
operator|.
name|clusterChanged
argument_list|(
name|clusterChangedEvent
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ClusterStateListener
name|listener
range|:
name|clusterStateListeners
control|)
block|{
name|listener
operator|.
name|clusterChanged
argument_list|(
name|clusterChangedEvent
argument_list|)
expr_stmt|;
block|}
name|threadPool
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
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodesDelta
operator|.
name|removedNodes
argument_list|()
control|)
block|{
name|transportService
operator|.
name|disconnectFromNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// if we are the master, publish the new state to all nodes
if|if
condition|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
name|discoveryService
operator|.
name|publish
argument_list|(
name|clusterState
argument_list|)
expr_stmt|;
block|}
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
name|clusterState
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|TimeoutHolder
specifier|private
specifier|static
class|class
name|TimeoutHolder
block|{
DECL|field|listener
specifier|final
name|TimeoutClusterStateListener
name|listener
decl_stmt|;
DECL|field|timestamp
specifier|final
name|long
name|timestamp
decl_stmt|;
DECL|field|timeout
specifier|final
name|TimeValue
name|timeout
decl_stmt|;
DECL|method|TimeoutHolder
specifier|private
name|TimeoutHolder
parameter_list|(
name|TimeoutClusterStateListener
name|listener
parameter_list|,
name|long
name|timestamp
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
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
DECL|method|hashCode
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|listener
operator|.
name|hashCode
argument_list|()
return|;
block|}
DECL|method|equals
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
operator|(
name|TimeoutHolder
operator|)
name|obj
operator|)
operator|.
name|listener
operator|==
name|listener
return|;
block|}
block|}
block|}
end_class

end_unit

