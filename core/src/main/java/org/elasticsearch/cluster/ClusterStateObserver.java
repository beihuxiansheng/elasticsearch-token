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
name|Logger
import|;
end_import

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
name|cluster
operator|.
name|service
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
name|ThreadContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * A utility class which simplifies interacting with the cluster state in cases where  * one tries to take action based on the current state but may want to wait for a new state  * and retry upon failure.  */
end_comment

begin_class
DECL|class|ClusterStateObserver
specifier|public
class|class
name|ClusterStateObserver
block|{
DECL|field|logger
specifier|protected
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|field|MATCH_ALL_CHANGES_PREDICATE
specifier|private
specifier|final
name|Predicate
argument_list|<
name|ClusterState
argument_list|>
name|MATCH_ALL_CHANGES_PREDICATE
init|=
name|state
lambda|->
literal|true
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|contextHolder
specifier|private
specifier|final
name|ThreadContext
name|contextHolder
decl_stmt|;
DECL|field|timeOutValue
specifier|volatile
name|TimeValue
name|timeOutValue
decl_stmt|;
DECL|field|lastObservedState
specifier|final
name|AtomicReference
argument_list|<
name|StoredState
argument_list|>
name|lastObservedState
decl_stmt|;
DECL|field|clusterStateListener
specifier|final
name|TimeoutClusterStateListener
name|clusterStateListener
init|=
operator|new
name|ObserverClusterStateListener
argument_list|()
decl_stmt|;
comment|// observingContext is not null when waiting on cluster state changes
DECL|field|observingContext
specifier|final
name|AtomicReference
argument_list|<
name|ObservingContext
argument_list|>
name|observingContext
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|startTimeNS
specifier|volatile
name|Long
name|startTimeNS
decl_stmt|;
DECL|field|timedOut
specifier|volatile
name|boolean
name|timedOut
decl_stmt|;
DECL|method|ClusterStateObserver
specifier|public
name|ClusterStateObserver
parameter_list|(
name|ClusterService
name|clusterService
parameter_list|,
name|Logger
name|logger
parameter_list|,
name|ThreadContext
name|contextHolder
parameter_list|)
block|{
name|this
argument_list|(
name|clusterService
argument_list|,
operator|new
name|TimeValue
argument_list|(
literal|60000
argument_list|)
argument_list|,
name|logger
argument_list|,
name|contextHolder
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param timeout        a global timeout for this observer. After it has expired the observer      *                       will fail any existing or new #waitForNextChange calls. Set to null      *                       to wait indefinitely      */
DECL|method|ClusterStateObserver
specifier|public
name|ClusterStateObserver
parameter_list|(
name|ClusterService
name|clusterService
parameter_list|,
annotation|@
name|Nullable
name|TimeValue
name|timeout
parameter_list|,
name|Logger
name|logger
parameter_list|,
name|ThreadContext
name|contextHolder
parameter_list|)
block|{
name|this
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|,
name|clusterService
argument_list|,
name|timeout
argument_list|,
name|logger
argument_list|,
name|contextHolder
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param timeout        a global timeout for this observer. After it has expired the observer      *                       will fail any existing or new #waitForNextChange calls. Set to null      *                       to wait indefinitely      */
DECL|method|ClusterStateObserver
specifier|public
name|ClusterStateObserver
parameter_list|(
name|ClusterState
name|initialState
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
annotation|@
name|Nullable
name|TimeValue
name|timeout
parameter_list|,
name|Logger
name|logger
parameter_list|,
name|ThreadContext
name|contextHolder
parameter_list|)
block|{
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|lastObservedState
operator|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|(
operator|new
name|StoredState
argument_list|(
name|initialState
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|timeOutValue
operator|=
name|timeout
expr_stmt|;
if|if
condition|(
name|timeOutValue
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|startTimeNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|contextHolder
operator|=
name|contextHolder
expr_stmt|;
block|}
comment|/** sets the last observed state to the currently applied cluster state and returns it */
DECL|method|setAndGetObservedState
specifier|public
name|ClusterState
name|setAndGetObservedState
parameter_list|()
block|{
if|if
condition|(
name|observingContext
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"cannot set current cluster state while waiting for a cluster state change"
argument_list|)
throw|;
block|}
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|StoredState
argument_list|(
name|clusterState
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|clusterState
return|;
block|}
comment|/** indicates whether this observer has timed out */
DECL|method|isTimedOut
specifier|public
name|boolean
name|isTimedOut
parameter_list|()
block|{
return|return
name|timedOut
return|;
block|}
DECL|method|waitForNextChange
specifier|public
name|void
name|waitForNextChange
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|waitForNextChange
argument_list|(
name|listener
argument_list|,
name|MATCH_ALL_CHANGES_PREDICATE
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForNextChange
specifier|public
name|void
name|waitForNextChange
parameter_list|(
name|Listener
name|listener
parameter_list|,
annotation|@
name|Nullable
name|TimeValue
name|timeOutValue
parameter_list|)
block|{
name|waitForNextChange
argument_list|(
name|listener
argument_list|,
name|MATCH_ALL_CHANGES_PREDICATE
argument_list|,
name|timeOutValue
argument_list|)
expr_stmt|;
block|}
DECL|method|waitForNextChange
specifier|public
name|void
name|waitForNextChange
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|Predicate
argument_list|<
name|ClusterState
argument_list|>
name|statePredicate
parameter_list|)
block|{
name|waitForNextChange
argument_list|(
name|listener
argument_list|,
name|statePredicate
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wait for the next cluster state which satisfies statePredicate      *      * @param listener        callback listener      * @param statePredicate predicate to check whether cluster state changes are relevant and the callback should be called      * @param timeOutValue    a timeout for waiting. If null the global observer timeout will be used.      */
DECL|method|waitForNextChange
specifier|public
name|void
name|waitForNextChange
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|Predicate
argument_list|<
name|ClusterState
argument_list|>
name|statePredicate
parameter_list|,
annotation|@
name|Nullable
name|TimeValue
name|timeOutValue
parameter_list|)
block|{
name|listener
operator|=
operator|new
name|ContextPreservingListener
argument_list|(
name|listener
argument_list|,
name|contextHolder
operator|.
name|newRestorableContext
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|observingContext
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"already waiting for a cluster state change"
argument_list|)
throw|;
block|}
name|Long
name|timeoutTimeLeftMS
decl_stmt|;
if|if
condition|(
name|timeOutValue
operator|==
literal|null
condition|)
block|{
name|timeOutValue
operator|=
name|this
operator|.
name|timeOutValue
expr_stmt|;
if|if
condition|(
name|timeOutValue
operator|!=
literal|null
condition|)
block|{
name|long
name|timeSinceStartMS
init|=
name|TimeValue
operator|.
name|nsecToMSec
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTimeNS
argument_list|)
decl_stmt|;
name|timeoutTimeLeftMS
operator|=
name|timeOutValue
operator|.
name|millis
argument_list|()
operator|-
name|timeSinceStartMS
expr_stmt|;
if|if
condition|(
name|timeoutTimeLeftMS
operator|<=
literal|0L
condition|)
block|{
comment|// things have timeout while we were busy -> notify
name|logger
operator|.
name|trace
argument_list|(
literal|"observer timed out. notifying listener. timeout setting [{}], time since start [{}]"
argument_list|,
name|timeOutValue
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|timeSinceStartMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// update to latest, in case people want to retry
name|timedOut
operator|=
literal|true
expr_stmt|;
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|StoredState
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onTimeout
argument_list|(
name|timeOutValue
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
else|else
block|{
name|timeoutTimeLeftMS
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|startTimeNS
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|timeOutValue
operator|=
name|timeOutValue
expr_stmt|;
name|timeoutTimeLeftMS
operator|=
name|timeOutValue
operator|.
name|millis
argument_list|()
expr_stmt|;
name|timedOut
operator|=
literal|false
expr_stmt|;
block|}
comment|// sample a new state
name|ClusterState
name|newState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastObservedState
operator|.
name|get
argument_list|()
operator|.
name|sameState
argument_list|(
name|newState
argument_list|)
operator|==
literal|false
operator|&&
name|statePredicate
operator|.
name|test
argument_list|(
name|newState
argument_list|)
condition|)
block|{
comment|// good enough, let's go.
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: sampled state accepted by predicate ({})"
argument_list|,
name|newState
argument_list|)
expr_stmt|;
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|StoredState
argument_list|(
name|newState
argument_list|)
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: sampled state rejected by predicate ({}). adding listener to ClusterService"
argument_list|,
name|newState
argument_list|)
expr_stmt|;
specifier|final
name|ObservingContext
name|context
init|=
operator|new
name|ObservingContext
argument_list|(
name|listener
argument_list|,
name|statePredicate
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|observingContext
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|context
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"already waiting for a cluster state change"
argument_list|)
throw|;
block|}
name|clusterService
operator|.
name|addTimeoutListener
argument_list|(
name|timeoutTimeLeftMS
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|TimeValue
argument_list|(
name|timeoutTimeLeftMS
argument_list|)
argument_list|,
name|clusterStateListener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ObserverClusterStateListener
class|class
name|ObserverClusterStateListener
implements|implements
name|TimeoutClusterStateListener
block|{
annotation|@
name|Override
DECL|method|clusterChanged
specifier|public
name|void
name|clusterChanged
parameter_list|(
name|ClusterChangedEvent
name|event
parameter_list|)
block|{
name|ObservingContext
name|context
init|=
name|observingContext
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
comment|// No need to remove listener as it is the responsibility of the thread that set observingContext to null
return|return;
block|}
specifier|final
name|ClusterState
name|state
init|=
name|event
operator|.
name|state
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|statePredicate
operator|.
name|test
argument_list|(
name|state
argument_list|)
condition|)
block|{
if|if
condition|(
name|observingContext
operator|.
name|compareAndSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|clusterService
operator|.
name|removeTimeoutListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: accepting cluster state change ({})"
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|StoredState
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: predicate approved change but observing context has changed - ignoring (new cluster state version [{}])"
argument_list|,
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: predicate rejected change (new cluster state version [{}])"
argument_list|,
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|postAdded
specifier|public
name|void
name|postAdded
parameter_list|()
block|{
name|ObservingContext
name|context
init|=
name|observingContext
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
comment|// No need to remove listener as it is the responsibility of the thread that set observingContext to null
return|return;
block|}
name|ClusterState
name|newState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastObservedState
operator|.
name|get
argument_list|()
operator|.
name|sameState
argument_list|(
name|newState
argument_list|)
operator|==
literal|false
operator|&&
name|context
operator|.
name|statePredicate
operator|.
name|test
argument_list|(
name|newState
argument_list|)
condition|)
block|{
comment|// double check we're still listening
if|if
condition|(
name|observingContext
operator|.
name|compareAndSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: post adding listener: accepting current cluster state ({})"
argument_list|,
name|newState
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|removeTimeoutListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|StoredState
argument_list|(
name|newState
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|newState
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: postAdded - predicate approved state but observing context has changed - ignoring ({})"
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: postAdded - predicate rejected state ({})"
argument_list|,
name|newState
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onClose
specifier|public
name|void
name|onClose
parameter_list|()
block|{
name|ObservingContext
name|context
init|=
name|observingContext
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: cluster service closed. notifying listener."
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|removeTimeoutListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|listener
operator|.
name|onClusterServiceClose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onTimeout
specifier|public
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|ObservingContext
name|context
init|=
name|observingContext
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|clusterService
operator|.
name|removeTimeoutListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|long
name|timeSinceStartMS
init|=
name|TimeValue
operator|.
name|nsecToMSec
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTimeNS
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"observer: timeout notification from cluster service. timeout setting [{}], time since start [{}]"
argument_list|,
name|timeOutValue
argument_list|,
operator|new
name|TimeValue
argument_list|(
name|timeSinceStartMS
argument_list|)
argument_list|)
expr_stmt|;
comment|// update to latest, in case people want to retry
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|StoredState
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|timedOut
operator|=
literal|true
expr_stmt|;
name|context
operator|.
name|listener
operator|.
name|onTimeout
argument_list|(
name|timeOutValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * The observer considers two cluster states to be the same if they have the same version and master node id (i.e. null or set)      */
DECL|class|StoredState
specifier|private
specifier|static
class|class
name|StoredState
block|{
DECL|field|masterNodeId
specifier|private
specifier|final
name|String
name|masterNodeId
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|method|StoredState
name|StoredState
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|this
operator|.
name|masterNodeId
operator|=
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|getMasterNodeId
argument_list|()
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|clusterState
operator|.
name|version
argument_list|()
expr_stmt|;
block|}
DECL|method|sameState
specifier|public
name|boolean
name|sameState
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
return|return
name|version
operator|==
name|clusterState
operator|.
name|version
argument_list|()
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|masterNodeId
argument_list|,
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|getMasterNodeId
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|interface|Listener
specifier|public
interface|interface
name|Listener
block|{
comment|/** called when a new state is observed */
DECL|method|onNewClusterState
name|void
name|onNewClusterState
parameter_list|(
name|ClusterState
name|state
parameter_list|)
function_decl|;
comment|/** called when the cluster service is closed */
DECL|method|onClusterServiceClose
name|void
name|onClusterServiceClose
parameter_list|()
function_decl|;
DECL|method|onTimeout
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
function_decl|;
block|}
DECL|class|ObservingContext
specifier|static
class|class
name|ObservingContext
block|{
DECL|field|listener
specifier|public
specifier|final
name|Listener
name|listener
decl_stmt|;
DECL|field|statePredicate
specifier|public
specifier|final
name|Predicate
argument_list|<
name|ClusterState
argument_list|>
name|statePredicate
decl_stmt|;
DECL|method|ObservingContext
name|ObservingContext
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|Predicate
argument_list|<
name|ClusterState
argument_list|>
name|statePredicate
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
name|statePredicate
operator|=
name|statePredicate
expr_stmt|;
block|}
block|}
DECL|class|ContextPreservingListener
specifier|private
specifier|static
specifier|final
class|class
name|ContextPreservingListener
implements|implements
name|Listener
block|{
DECL|field|delegate
specifier|private
specifier|final
name|Listener
name|delegate
decl_stmt|;
DECL|field|contextSupplier
specifier|private
specifier|final
name|Supplier
argument_list|<
name|ThreadContext
operator|.
name|StoredContext
argument_list|>
name|contextSupplier
decl_stmt|;
DECL|method|ContextPreservingListener
specifier|private
name|ContextPreservingListener
parameter_list|(
name|Listener
name|delegate
parameter_list|,
name|Supplier
argument_list|<
name|ThreadContext
operator|.
name|StoredContext
argument_list|>
name|contextSupplier
parameter_list|)
block|{
name|this
operator|.
name|contextSupplier
operator|=
name|contextSupplier
expr_stmt|;
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onNewClusterState
specifier|public
name|void
name|onNewClusterState
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|context
init|=
name|contextSupplier
operator|.
name|get
argument_list|()
init|)
block|{
name|delegate
operator|.
name|onNewClusterState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onClusterServiceClose
specifier|public
name|void
name|onClusterServiceClose
parameter_list|()
block|{
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|context
init|=
name|contextSupplier
operator|.
name|get
argument_list|()
init|)
block|{
name|delegate
operator|.
name|onClusterServiceClose
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|onTimeout
specifier|public
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|context
init|=
name|contextSupplier
operator|.
name|get
argument_list|()
init|)
block|{
name|delegate
operator|.
name|onTimeout
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

