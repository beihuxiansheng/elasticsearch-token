begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.publish
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|publish
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
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**  * A queue that holds all "in-flight" incoming cluster states from the master. Once a master commits a cluster  * state, it is made available via {@link #getNextClusterStateToProcess()}. The class also takes care of batching  * cluster states for processing and failures.  *<p>  * The queue is bound by {@link #maxQueueSize}. When the queue is at capacity and a new cluster state is inserted  * the oldest cluster state will be dropped. This is safe because:  * 1) Under normal operations, master will publish&amp; commit a cluster state before processing another change (i.e., the queue length is 1)  * 2) If the master fails to commit a change, it will step down, causing a master election, which will flush the queue.  * 3) In general it's safe to process the incoming cluster state as a replacement to the cluster state that's dropped.  * a) If the dropped cluster is from the same master as the incoming one is, it is likely to be superseded by the incoming state (or another state in the queue).  * This is only not true in very extreme cases of out of order delivery.  * b) If the dropping cluster state is not from the same master, it means that:  * i) we are no longer following the master of the dropped cluster state but follow the incoming one  * ii) we are no longer following any master, in which case it doesn't matter which cluster state will be processed first.  *<p>  * The class is fully thread safe and can be used concurrently.  */
end_comment

begin_class
DECL|class|PendingClusterStatesQueue
specifier|public
class|class
name|PendingClusterStatesQueue
block|{
DECL|interface|StateProcessedListener
interface|interface
name|StateProcessedListener
block|{
DECL|method|onNewClusterStateProcessed
name|void
name|onNewClusterStateProcessed
parameter_list|()
function_decl|;
DECL|method|onNewClusterStateFailed
name|void
name|onNewClusterStateFailed
parameter_list|(
name|Throwable
name|t
parameter_list|)
function_decl|;
block|}
DECL|field|pendingStates
specifier|final
name|ArrayList
argument_list|<
name|ClusterStateContext
argument_list|>
name|pendingStates
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|logger
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|maxQueueSize
specifier|final
name|int
name|maxQueueSize
decl_stmt|;
DECL|method|PendingClusterStatesQueue
specifier|public
name|PendingClusterStatesQueue
parameter_list|(
name|ESLogger
name|logger
parameter_list|,
name|int
name|maxQueueSize
parameter_list|)
block|{
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
name|this
operator|.
name|maxQueueSize
operator|=
name|maxQueueSize
expr_stmt|;
block|}
comment|/** Add an incoming, not yet committed cluster state */
DECL|method|addPending
specifier|public
specifier|synchronized
name|void
name|addPending
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
name|pendingStates
operator|.
name|add
argument_list|(
operator|new
name|ClusterStateContext
argument_list|(
name|state
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|pendingStates
operator|.
name|size
argument_list|()
operator|>
name|maxQueueSize
condition|)
block|{
name|ClusterStateContext
name|context
init|=
name|pendingStates
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"dropping pending state [{}]. more than [{}] pending states."
argument_list|,
name|context
argument_list|,
name|maxQueueSize
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|committed
argument_list|()
condition|)
block|{
name|context
operator|.
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
operator|new
name|ElasticsearchException
argument_list|(
literal|"too many pending states ([{}] pending)"
argument_list|,
name|maxQueueSize
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Mark a previously added cluster state as committed. This will make it available via {@link #getNextClusterStateToProcess()}      * When the cluster state is processed (or failed), the supplied listener will be called      **/
DECL|method|markAsCommitted
specifier|public
specifier|synchronized
name|ClusterState
name|markAsCommitted
parameter_list|(
name|String
name|stateUUID
parameter_list|,
name|StateProcessedListener
name|listener
parameter_list|)
block|{
specifier|final
name|ClusterStateContext
name|context
init|=
name|findState
argument_list|(
name|stateUUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"can't resolve cluster state with uuid ["
operator|+
name|stateUUID
operator|+
literal|"] to commit"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|context
operator|.
name|committed
argument_list|()
condition|)
block|{
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"cluster state with uuid ["
operator|+
name|stateUUID
operator|+
literal|"] is already committed"
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|context
operator|.
name|markAsCommitted
argument_list|(
name|listener
argument_list|)
expr_stmt|;
return|return
name|context
operator|.
name|state
return|;
block|}
comment|/**      * mark that the processing of the given state has failed. All committed states that are {@link ClusterState#supersedes(ClusterState)}-ed      * by this failed state, will be failed as well      */
DECL|method|markAsFailed
specifier|public
specifier|synchronized
name|void
name|markAsFailed
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Throwable
name|reason
parameter_list|)
block|{
specifier|final
name|ClusterStateContext
name|failedContext
init|=
name|findState
argument_list|(
name|state
operator|.
name|stateUUID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|failedContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"can't resolve failed cluster state with uuid ["
operator|+
name|state
operator|.
name|stateUUID
argument_list|()
operator|+
literal|"], version ["
operator|+
name|state
operator|.
name|version
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|failedContext
operator|.
name|committed
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed cluster state is not committed "
operator|+
name|state
argument_list|)
throw|;
block|}
comment|// fail all committed states which are batch together with the failed state
name|ArrayList
argument_list|<
name|ClusterStateContext
argument_list|>
name|statesToRemove
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|pendingStates
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
specifier|final
name|ClusterStateContext
name|pendingContext
init|=
name|pendingStates
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|pendingContext
operator|.
name|committed
argument_list|()
operator|==
literal|false
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ClusterState
name|pendingState
init|=
name|pendingContext
operator|.
name|state
decl_stmt|;
if|if
condition|(
name|pendingContext
operator|.
name|equals
argument_list|(
name|failedContext
argument_list|)
condition|)
block|{
name|statesToRemove
operator|.
name|add
argument_list|(
name|pendingContext
argument_list|)
expr_stmt|;
name|pendingContext
operator|.
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|.
name|supersedes
argument_list|(
name|pendingState
argument_list|)
condition|)
block|{
name|statesToRemove
operator|.
name|add
argument_list|(
name|pendingContext
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"failing committed state {} together with state {}"
argument_list|,
name|pendingContext
argument_list|,
name|failedContext
argument_list|)
expr_stmt|;
name|pendingContext
operator|.
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
name|pendingStates
operator|.
name|removeAll
argument_list|(
name|statesToRemove
argument_list|)
expr_stmt|;
assert|assert
name|findState
argument_list|(
name|state
operator|.
name|stateUUID
argument_list|()
argument_list|)
operator|==
literal|null
operator|:
literal|"state was marked as processed but can still be found in pending list "
operator|+
name|state
assert|;
block|}
comment|/**      * indicates that a cluster state was successfully processed. Any committed state that is {@link ClusterState#supersedes(ClusterState)}-ed      * by the processed state will be marked as processed as well.      *<p>      * NOTE: successfully processing a state indicates we are following the master it came from. Any committed state from another master will      * be failed by this method      */
DECL|method|markAsProcessed
specifier|public
specifier|synchronized
name|void
name|markAsProcessed
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
if|if
condition|(
name|findState
argument_list|(
name|state
operator|.
name|stateUUID
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't resolve processed cluster state with uuid ["
operator|+
name|state
operator|.
name|stateUUID
argument_list|()
operator|+
literal|"], version ["
operator|+
name|state
operator|.
name|version
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
specifier|final
name|DiscoveryNode
name|currentMaster
init|=
name|state
operator|.
name|nodes
argument_list|()
operator|.
name|getMasterNode
argument_list|()
decl_stmt|;
assert|assert
name|currentMaster
operator|!=
literal|null
operator|:
literal|"processed cluster state mast have a master. "
operator|+
name|state
assert|;
comment|// fail or remove any incoming state from a different master
comment|// respond to any committed state from the same master with same or lower version (we processed a higher version)
name|ArrayList
argument_list|<
name|ClusterStateContext
argument_list|>
name|contextsToRemove
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|pendingStates
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
specifier|final
name|ClusterStateContext
name|pendingContext
init|=
name|pendingStates
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
specifier|final
name|ClusterState
name|pendingState
init|=
name|pendingContext
operator|.
name|state
decl_stmt|;
specifier|final
name|DiscoveryNode
name|pendingMasterNode
init|=
name|pendingState
operator|.
name|nodes
argument_list|()
operator|.
name|getMasterNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|currentMaster
argument_list|,
name|pendingMasterNode
argument_list|)
operator|==
literal|false
condition|)
block|{
name|contextsToRemove
operator|.
name|add
argument_list|(
name|pendingContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|pendingContext
operator|.
name|committed
argument_list|()
condition|)
block|{
comment|// this is a committed state , warn
name|logger
operator|.
name|warn
argument_list|(
literal|"received a cluster state (uuid[{}]/v[{}]) from a different master than the current one, rejecting (received {}, current {})"
argument_list|,
name|pendingState
operator|.
name|stateUUID
argument_list|()
argument_list|,
name|pendingState
operator|.
name|version
argument_list|()
argument_list|,
name|pendingMasterNode
argument_list|,
name|currentMaster
argument_list|)
expr_stmt|;
name|pendingContext
operator|.
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"cluster state from a different master than the current one, rejecting (received "
operator|+
name|pendingMasterNode
operator|+
literal|", current "
operator|+
name|currentMaster
operator|+
literal|")"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"removing non-committed state with uuid[{}]/v[{}] from [{}] - a state from [{}] was successfully processed"
argument_list|,
name|pendingState
operator|.
name|stateUUID
argument_list|()
argument_list|,
name|pendingState
operator|.
name|version
argument_list|()
argument_list|,
name|pendingMasterNode
argument_list|,
name|currentMaster
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|pendingState
operator|.
name|stateUUID
argument_list|()
operator|.
name|equals
argument_list|(
name|state
operator|.
name|stateUUID
argument_list|()
argument_list|)
condition|)
block|{
assert|assert
name|pendingContext
operator|.
name|committed
argument_list|()
operator|:
literal|"processed cluster state is not committed "
operator|+
name|state
assert|;
name|contextsToRemove
operator|.
name|add
argument_list|(
name|pendingContext
argument_list|)
expr_stmt|;
name|pendingContext
operator|.
name|listener
operator|.
name|onNewClusterStateProcessed
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|state
operator|.
name|version
argument_list|()
operator|>=
name|pendingState
operator|.
name|version
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"processing pending state uuid[{}]/v[{}] together with state uuid[{}]/v[{}]"
argument_list|,
name|pendingState
operator|.
name|stateUUID
argument_list|()
argument_list|,
name|pendingState
operator|.
name|version
argument_list|()
argument_list|,
name|state
operator|.
name|stateUUID
argument_list|()
argument_list|,
name|state
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|contextsToRemove
operator|.
name|add
argument_list|(
name|pendingContext
argument_list|)
expr_stmt|;
if|if
condition|(
name|pendingContext
operator|.
name|committed
argument_list|()
condition|)
block|{
name|pendingContext
operator|.
name|listener
operator|.
name|onNewClusterStateProcessed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// now ack the processed state
name|pendingStates
operator|.
name|removeAll
argument_list|(
name|contextsToRemove
argument_list|)
expr_stmt|;
assert|assert
name|findState
argument_list|(
name|state
operator|.
name|stateUUID
argument_list|()
argument_list|)
operator|==
literal|null
operator|:
literal|"state was marked as processed but can still be found in pending list "
operator|+
name|state
assert|;
block|}
DECL|method|findState
name|ClusterStateContext
name|findState
parameter_list|(
name|String
name|stateUUID
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pendingStates
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ClusterStateContext
name|context
init|=
name|pendingStates
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|stateUUID
argument_list|()
operator|.
name|equals
argument_list|(
name|stateUUID
argument_list|)
condition|)
block|{
return|return
name|context
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** clear the incoming queue. any committed state will be failed */
DECL|method|failAllStatesAndClear
specifier|public
specifier|synchronized
name|void
name|failAllStatesAndClear
parameter_list|(
name|Throwable
name|reason
parameter_list|)
block|{
for|for
control|(
name|ClusterStateContext
name|pendingState
range|:
name|pendingStates
control|)
block|{
if|if
condition|(
name|pendingState
operator|.
name|committed
argument_list|()
condition|)
block|{
name|pendingState
operator|.
name|listener
operator|.
name|onNewClusterStateFailed
argument_list|(
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
name|pendingStates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * Gets the next committed state to process.      *<p>      * The method tries to batch operation by getting the cluster state the highest possible committed states      * which succeeds the first committed state in queue (i.e., it comes from the same master).      */
DECL|method|getNextClusterStateToProcess
specifier|public
specifier|synchronized
name|ClusterState
name|getNextClusterStateToProcess
parameter_list|()
block|{
if|if
condition|(
name|pendingStates
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|ClusterStateContext
name|stateToProcess
init|=
literal|null
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|index
operator|<
name|pendingStates
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|ClusterStateContext
name|potentialState
init|=
name|pendingStates
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|potentialState
operator|.
name|committed
argument_list|()
condition|)
block|{
name|stateToProcess
operator|=
name|potentialState
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|stateToProcess
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// now try to find the highest committed state from the same master
for|for
control|(
init|;
name|index
operator|<
name|pendingStates
operator|.
name|size
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|ClusterStateContext
name|potentialState
init|=
name|pendingStates
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
name|potentialState
operator|.
name|state
operator|.
name|supersedes
argument_list|(
name|stateToProcess
operator|.
name|state
argument_list|)
operator|&&
name|potentialState
operator|.
name|committed
argument_list|()
condition|)
block|{
comment|// we found a new one
name|stateToProcess
operator|=
name|potentialState
expr_stmt|;
block|}
block|}
assert|assert
name|stateToProcess
operator|.
name|committed
argument_list|()
operator|:
literal|"should only return committed cluster state. found "
operator|+
name|stateToProcess
operator|.
name|state
assert|;
return|return
name|stateToProcess
operator|.
name|state
return|;
block|}
comment|/** returns all pending states, committed or not */
DECL|method|pendingClusterStates
specifier|public
specifier|synchronized
name|ClusterState
index|[]
name|pendingClusterStates
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|ClusterState
argument_list|>
name|states
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ClusterStateContext
name|context
range|:
name|pendingStates
control|)
block|{
name|states
operator|.
name|add
argument_list|(
name|context
operator|.
name|state
argument_list|)
expr_stmt|;
block|}
return|return
name|states
operator|.
name|toArray
argument_list|(
operator|new
name|ClusterState
index|[
name|states
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
DECL|class|ClusterStateContext
specifier|static
class|class
name|ClusterStateContext
block|{
DECL|field|state
specifier|final
name|ClusterState
name|state
decl_stmt|;
DECL|field|listener
name|StateProcessedListener
name|listener
decl_stmt|;
DECL|method|ClusterStateContext
name|ClusterStateContext
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|clusterState
expr_stmt|;
block|}
DECL|method|markAsCommitted
name|void
name|markAsCommitted
parameter_list|(
name|StateProcessedListener
name|listener
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|toString
argument_list|()
operator|+
literal|"is already committed"
argument_list|)
throw|;
block|}
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|committed
name|boolean
name|committed
parameter_list|()
block|{
return|return
name|listener
operator|!=
literal|null
return|;
block|}
DECL|method|stateUUID
specifier|public
name|String
name|stateUUID
parameter_list|()
block|{
return|return
name|state
operator|.
name|stateUUID
argument_list|()
return|;
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
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"[uuid[%s], v[%d], m[%s]]"
argument_list|,
name|stateUUID
argument_list|()
argument_list|,
name|state
operator|.
name|version
argument_list|()
argument_list|,
name|state
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
DECL|method|stats
specifier|public
specifier|synchronized
name|PendingClusterStateStats
name|stats
parameter_list|()
block|{
comment|// calculate committed cluster state
name|int
name|committed
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ClusterStateContext
name|clusterStatsContext
range|:
name|pendingStates
control|)
block|{
if|if
condition|(
name|clusterStatsContext
operator|.
name|committed
argument_list|()
condition|)
block|{
name|committed
operator|+=
literal|1
expr_stmt|;
block|}
block|}
return|return
operator|new
name|PendingClusterStateStats
argument_list|(
name|pendingStates
operator|.
name|size
argument_list|()
argument_list|,
name|pendingStates
operator|.
name|size
argument_list|()
operator|-
name|committed
argument_list|,
name|committed
argument_list|)
return|;
block|}
block|}
end_class

end_unit

