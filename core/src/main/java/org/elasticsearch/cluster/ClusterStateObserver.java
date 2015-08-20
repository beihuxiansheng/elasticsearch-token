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
name|ElasticsearchException
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|ESLogger
name|logger
decl_stmt|;
DECL|field|MATCH_ALL_CHANGES_PREDICATE
specifier|public
specifier|final
name|ChangePredicate
name|MATCH_ALL_CHANGES_PREDICATE
init|=
operator|new
name|EventPredicate
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterChangedEvent
name|changedEvent
parameter_list|)
block|{
return|return
name|changedEvent
operator|.
name|previousState
argument_list|()
operator|.
name|version
argument_list|()
operator|!=
name|changedEvent
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
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
name|ObservedState
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
argument_list|<
name|ObservingContext
argument_list|>
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
name|ESLogger
name|logger
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
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param clusterService      * @param timeout        a global timeout for this observer. After it has expired the observer      *                       will fail any existing or new #waitForNextChange calls. Set to null      *                       to wait indefinitely      */
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
name|ESLogger
name|logger
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
name|ObservedState
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
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
block|}
comment|/** last cluster state observer by this observer. Note that this may not be the current one */
DECL|method|observedState
specifier|public
name|ClusterState
name|observedState
parameter_list|()
block|{
name|ObservedState
name|state
init|=
name|lastObservedState
operator|.
name|get
argument_list|()
decl_stmt|;
assert|assert
name|state
operator|!=
literal|null
assert|;
return|return
name|state
operator|.
name|clusterState
return|;
block|}
comment|/** indicates whether this observer has timedout */
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
name|ChangePredicate
name|changePredicate
parameter_list|)
block|{
name|waitForNextChange
argument_list|(
name|listener
argument_list|,
name|changePredicate
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Wait for the next cluster state which satisfies changePredicate      *      * @param listener        callback listener      * @param changePredicate predicate to check whether cluster state changes are relevant and the callback should be called      * @param timeOutValue    a timeout for waiting. If null the global observer timeout will be used.      */
DECL|method|waitForNextChange
specifier|public
name|void
name|waitForNextChange
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|ChangePredicate
name|changePredicate
parameter_list|,
annotation|@
name|Nullable
name|TimeValue
name|timeOutValue
parameter_list|)
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
literal|0l
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
name|ObservedState
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
name|ObservedState
name|newState
init|=
operator|new
name|ObservedState
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
decl_stmt|;
name|ObservedState
name|lastState
init|=
name|lastObservedState
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|changePredicate
operator|.
name|apply
argument_list|(
name|lastState
operator|.
name|clusterState
argument_list|,
name|lastState
operator|.
name|status
argument_list|,
name|newState
operator|.
name|clusterState
argument_list|,
name|newState
operator|.
name|status
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
name|newState
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|newState
operator|.
name|clusterState
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
name|ObservingContext
name|context
init|=
operator|new
name|ObservingContext
argument_list|(
name|listener
argument_list|,
name|changePredicate
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
name|add
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
comment|/**      * reset this observer to the give cluster state. Any pending waits will be canceled.      *      * @param toState      */
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|(
name|ClusterState
name|toState
parameter_list|)
block|{
if|if
condition|(
name|observingContext
operator|.
name|getAndSet
argument_list|(
literal|null
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|clusterService
operator|.
name|remove
argument_list|(
name|clusterStateListener
argument_list|)
expr_stmt|;
block|}
name|lastObservedState
operator|.
name|set
argument_list|(
operator|new
name|ObservedState
argument_list|(
name|toState
argument_list|)
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|context
operator|.
name|changePredicate
operator|.
name|apply
argument_list|(
name|event
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
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|ObservedState
name|state
init|=
operator|new
name|ObservedState
argument_list|(
name|event
operator|.
name|state
argument_list|()
argument_list|)
decl_stmt|;
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
name|state
argument_list|)
expr_stmt|;
name|context
operator|.
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|state
operator|.
name|clusterState
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
name|event
operator|.
name|state
argument_list|()
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
name|event
operator|.
name|state
argument_list|()
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
name|ObservedState
name|newState
init|=
operator|new
name|ObservedState
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
argument_list|)
decl_stmt|;
name|ObservedState
name|lastState
init|=
name|lastObservedState
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|changePredicate
operator|.
name|apply
argument_list|(
name|lastState
operator|.
name|clusterState
argument_list|,
name|lastState
operator|.
name|status
argument_list|,
name|newState
operator|.
name|clusterState
argument_list|,
name|newState
operator|.
name|status
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
name|remove
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|lastObservedState
operator|.
name|set
argument_list|(
name|newState
argument_list|)
expr_stmt|;
name|context
operator|.
name|listener
operator|.
name|onNewClusterState
argument_list|(
name|newState
operator|.
name|clusterState
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
name|remove
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
name|remove
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
name|ObservedState
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
DECL|interface|Listener
specifier|public
specifier|static
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
DECL|interface|ChangePredicate
specifier|public
interface|interface
name|ChangePredicate
block|{
comment|/**          * a rough check used when starting to monitor for a new change. Called infrequently can be less accurate.          *          * @return true if newState should be accepted          */
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterState
name|previousState
parameter_list|,
name|ClusterState
operator|.
name|ClusterStateStatus
name|previousStatus
parameter_list|,
name|ClusterState
name|newState
parameter_list|,
name|ClusterState
operator|.
name|ClusterStateStatus
name|newStatus
parameter_list|)
function_decl|;
comment|/**          * called to see whether a cluster change should be accepted          *          * @return true if changedEvent.state() should be accepted          */
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterChangedEvent
name|changedEvent
parameter_list|)
function_decl|;
block|}
DECL|class|ValidationPredicate
specifier|public
specifier|static
specifier|abstract
class|class
name|ValidationPredicate
implements|implements
name|ChangePredicate
block|{
annotation|@
name|Override
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterState
name|previousState
parameter_list|,
name|ClusterState
operator|.
name|ClusterStateStatus
name|previousStatus
parameter_list|,
name|ClusterState
name|newState
parameter_list|,
name|ClusterState
operator|.
name|ClusterStateStatus
name|newStatus
parameter_list|)
block|{
if|if
condition|(
name|previousState
operator|!=
name|newState
operator|||
name|previousStatus
operator|!=
name|newStatus
condition|)
block|{
return|return
name|validate
argument_list|(
name|newState
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|validate
specifier|protected
specifier|abstract
name|boolean
name|validate
parameter_list|(
name|ClusterState
name|newState
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterChangedEvent
name|changedEvent
parameter_list|)
block|{
if|if
condition|(
name|changedEvent
operator|.
name|previousState
argument_list|()
operator|.
name|version
argument_list|()
operator|!=
name|changedEvent
operator|.
name|state
argument_list|()
operator|.
name|version
argument_list|()
condition|)
block|{
return|return
name|validate
argument_list|(
name|changedEvent
operator|.
name|state
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
DECL|class|EventPredicate
specifier|public
specifier|static
specifier|abstract
class|class
name|EventPredicate
implements|implements
name|ChangePredicate
block|{
annotation|@
name|Override
DECL|method|apply
specifier|public
name|boolean
name|apply
parameter_list|(
name|ClusterState
name|previousState
parameter_list|,
name|ClusterState
operator|.
name|ClusterStateStatus
name|previousStatus
parameter_list|,
name|ClusterState
name|newState
parameter_list|,
name|ClusterState
operator|.
name|ClusterStateStatus
name|newStatus
parameter_list|)
block|{
return|return
name|previousState
operator|!=
name|newState
operator|||
name|previousStatus
operator|!=
name|newStatus
return|;
block|}
block|}
DECL|class|ObservingContext
specifier|static
class|class
name|ObservingContext
block|{
DECL|field|listener
specifier|final
specifier|public
name|Listener
name|listener
decl_stmt|;
DECL|field|changePredicate
specifier|final
specifier|public
name|ChangePredicate
name|changePredicate
decl_stmt|;
DECL|method|ObservingContext
specifier|public
name|ObservingContext
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|ChangePredicate
name|changePredicate
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
name|changePredicate
operator|=
name|changePredicate
expr_stmt|;
block|}
block|}
DECL|class|ObservedState
specifier|static
class|class
name|ObservedState
block|{
DECL|field|clusterState
specifier|final
specifier|public
name|ClusterState
name|clusterState
decl_stmt|;
DECL|field|status
specifier|final
specifier|public
name|ClusterState
operator|.
name|ClusterStateStatus
name|status
decl_stmt|;
DECL|method|ObservedState
specifier|public
name|ObservedState
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|this
operator|.
name|clusterState
operator|=
name|clusterState
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|clusterState
operator|.
name|status
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"version ["
operator|+
name|clusterState
operator|.
name|version
argument_list|()
operator|+
literal|"], status ["
operator|+
name|status
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit
