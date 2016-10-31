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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|lease
operator|.
name|Releasable
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
name|Setting
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
name|AbstractRunnable
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|KeyedLock
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
name|zen
operator|.
name|MasterFaultDetection
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
name|zen
operator|.
name|NodesFaultDetection
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
name|ConcurrentMap
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Setting
operator|.
name|Property
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
name|settings
operator|.
name|Setting
operator|.
name|positiveTimeSetting
import|;
end_import

begin_comment
comment|/**  * This component is responsible for connecting to nodes once they are added to the cluster state, and disconnect when they are  * removed. Also, it periodically checks that all connections are still open and if needed restores them.  * Note that this component is *not* responsible for removing nodes from the cluster if they disconnect / do not respond  * to pings. This is done by {@link NodesFaultDetection}. Master fault detection  * is done by {@link MasterFaultDetection}.  */
end_comment

begin_class
DECL|class|NodeConnectionsService
specifier|public
class|class
name|NodeConnectionsService
extends|extends
name|AbstractLifecycleComponent
block|{
DECL|field|CLUSTER_NODE_RECONNECT_INTERVAL_SETTING
specifier|public
specifier|static
specifier|final
name|Setting
argument_list|<
name|TimeValue
argument_list|>
name|CLUSTER_NODE_RECONNECT_INTERVAL_SETTING
init|=
name|positiveTimeSetting
argument_list|(
literal|"cluster.nodes.reconnect_interval"
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|10
argument_list|)
argument_list|,
name|Property
operator|.
name|NodeScope
argument_list|)
decl_stmt|;
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
comment|// map between current node and the number of failed connection attempts. 0 means successfully connected.
comment|// if a node doesn't appear in this list it shouldn't be monitored
DECL|field|nodes
specifier|private
name|ConcurrentMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|Integer
argument_list|>
name|nodes
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|nodeLocks
specifier|private
specifier|final
name|KeyedLock
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodeLocks
init|=
operator|new
name|KeyedLock
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|reconnectInterval
specifier|private
specifier|final
name|TimeValue
name|reconnectInterval
decl_stmt|;
DECL|field|backgroundFuture
specifier|private
specifier|volatile
name|ScheduledFuture
argument_list|<
name|?
argument_list|>
name|backgroundFuture
init|=
literal|null
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeConnectionsService
specifier|public
name|NodeConnectionsService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportService
name|transportService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|reconnectInterval
operator|=
name|NodeConnectionsService
operator|.
name|CLUSTER_NODE_RECONNECT_INTERVAL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
DECL|method|connectToNodes
specifier|public
name|void
name|connectToNodes
parameter_list|(
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|addedNodes
parameter_list|)
block|{
comment|// TODO: do this in parallel (and wait)
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|addedNodes
control|)
block|{
try|try
init|(
name|Releasable
name|ignored
init|=
name|nodeLocks
operator|.
name|acquire
argument_list|(
name|node
argument_list|)
init|)
block|{
name|Integer
name|current
init|=
name|nodes
operator|.
name|put
argument_list|(
name|node
argument_list|,
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|current
operator|==
literal|null
operator|:
literal|"node "
operator|+
name|node
operator|+
literal|" was added in event but already in internal nodes"
assert|;
name|validateNodeConnected
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|disconnectFromNodes
specifier|public
name|void
name|disconnectFromNodes
parameter_list|(
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|removedNodes
parameter_list|)
block|{
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|removedNodes
control|)
block|{
try|try
init|(
name|Releasable
name|ignored
init|=
name|nodeLocks
operator|.
name|acquire
argument_list|(
name|node
argument_list|)
init|)
block|{
name|Integer
name|current
init|=
name|nodes
operator|.
name|remove
argument_list|(
name|node
argument_list|)
decl_stmt|;
assert|assert
name|current
operator|!=
literal|null
operator|:
literal|"node "
operator|+
name|node
operator|+
literal|" was removed in event but not in internal nodes"
assert|;
try|try
block|{
name|transportService
operator|.
name|disconnectFromNode
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
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"failed to disconnect to node [{}]"
argument_list|,
name|node
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|validateNodeConnected
name|void
name|validateNodeConnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
assert|assert
name|nodeLocks
operator|.
name|isHeldByCurrentThread
argument_list|(
name|node
argument_list|)
operator|:
literal|"validateNodeConnected must be called under lock"
assert|;
if|if
condition|(
name|lifecycle
operator|.
name|stoppedOrClosed
argument_list|()
operator|||
name|nodes
operator|.
name|containsKey
argument_list|(
name|node
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// we double check existence of node since connectToNode might take time...
comment|// nothing to do
block|}
else|else
block|{
try|try
block|{
comment|// connecting to an already connected node is a noop
name|transportService
operator|.
name|connectToNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|put
argument_list|(
name|node
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Integer
name|nodeFailureCount
init|=
name|nodes
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
assert|assert
name|nodeFailureCount
operator|!=
literal|null
operator|:
name|node
operator|+
literal|" didn't have a counter in nodes map"
assert|;
name|nodeFailureCount
operator|=
name|nodeFailureCount
operator|+
literal|1
expr_stmt|;
comment|// log every 6th failure
if|if
condition|(
operator|(
name|nodeFailureCount
operator|%
literal|6
operator|)
operator|==
literal|1
condition|)
block|{
specifier|final
name|int
name|finalNodeFailureCount
init|=
name|nodeFailureCount
decl_stmt|;
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"failed to connect to node {} (tried [{}] times)"
argument_list|,
name|node
argument_list|,
name|finalNodeFailureCount
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|nodeFailureCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|ConnectionChecker
class|class
name|ConnectionChecker
extends|extends
name|AbstractRunnable
block|{
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"unexpected error while checking for node reconnects"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
DECL|method|doRun
specifier|protected
name|void
name|doRun
parameter_list|()
block|{
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodes
operator|.
name|keySet
argument_list|()
control|)
block|{
try|try
init|(
name|Releasable
name|ignored
init|=
name|nodeLocks
operator|.
name|acquire
argument_list|(
name|node
argument_list|)
init|)
block|{
name|validateNodeConnected
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onAfter
specifier|public
name|void
name|onAfter
parameter_list|()
block|{
if|if
condition|(
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
name|backgroundFuture
operator|=
name|threadPool
operator|.
name|schedule
argument_list|(
name|reconnectInterval
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
name|backgroundFuture
operator|=
name|threadPool
operator|.
name|schedule
argument_list|(
name|reconnectInterval
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|GENERIC
argument_list|,
operator|new
name|ConnectionChecker
argument_list|()
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
block|{
name|FutureUtils
operator|.
name|cancel
argument_list|(
name|backgroundFuture
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{      }
block|}
end_class

end_unit

