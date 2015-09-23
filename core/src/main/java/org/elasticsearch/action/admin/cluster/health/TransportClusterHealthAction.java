begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.health
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|health
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|ActionFilters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|IndicesOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|TransportMasterNodeReadAction
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
name|ClusterBlockException
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
name|metadata
operator|.
name|IndexNameExpressionResolver
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
name|UnassignedInfo
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
name|Strings
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
name|gateway
operator|.
name|GatewayAllocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|IndexNotFoundException
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportClusterHealthAction
specifier|public
class|class
name|TransportClusterHealthAction
extends|extends
name|TransportMasterNodeReadAction
argument_list|<
name|ClusterHealthRequest
argument_list|,
name|ClusterHealthResponse
argument_list|>
block|{
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|gatewayAllocator
specifier|private
specifier|final
name|GatewayAllocator
name|gatewayAllocator
decl_stmt|;
annotation|@
name|Inject
DECL|method|TransportClusterHealthAction
specifier|public
name|TransportClusterHealthAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|GatewayAllocator
name|gatewayAllocator
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|ClusterHealthAction
operator|.
name|NAME
argument_list|,
name|transportService
argument_list|,
name|clusterService
argument_list|,
name|threadPool
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|ClusterHealthRequest
operator|::
operator|new
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|gatewayAllocator
operator|=
name|gatewayAllocator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
specifier|protected
name|String
name|executor
parameter_list|()
block|{
comment|// this should be executing quickly no need to fork off
return|return
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
return|;
block|}
annotation|@
name|Override
DECL|method|checkBlock
specifier|protected
name|ClusterBlockException
name|checkBlock
parameter_list|(
name|ClusterHealthRequest
name|request
parameter_list|,
name|ClusterState
name|state
parameter_list|)
block|{
return|return
literal|null
return|;
comment|// we want users to be able to call this even when there are global blocks, just to check the health (are there blocks?)
block|}
annotation|@
name|Override
DECL|method|newResponse
specifier|protected
name|ClusterHealthResponse
name|newResponse
parameter_list|()
block|{
return|return
operator|new
name|ClusterHealthResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|masterOperation
specifier|protected
name|void
name|masterOperation
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
specifier|final
name|ClusterState
name|unusedState
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|listener
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|waitForEvents
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|long
name|endTimeMS
init|=
name|TimeValue
operator|.
name|nsecToMSec
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
operator|+
name|request
operator|.
name|timeout
argument_list|()
operator|.
name|millis
argument_list|()
decl_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"cluster_health (wait_for_events ["
operator|+
name|request
operator|.
name|waitForEvents
argument_list|()
operator|+
literal|"])"
argument_list|,
name|request
operator|.
name|waitForEvents
argument_list|()
argument_list|,
operator|new
name|ClusterStateUpdateTask
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ClusterState
name|execute
parameter_list|(
name|ClusterState
name|currentState
parameter_list|)
block|{
return|return
name|currentState
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|String
name|source
parameter_list|,
name|ClusterState
name|oldState
parameter_list|,
name|ClusterState
name|newState
parameter_list|)
block|{
specifier|final
name|long
name|timeoutInMillis
init|=
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|endTimeMS
operator|-
name|TimeValue
operator|.
name|nsecToMSec
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|TimeValue
name|newTimeout
init|=
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|timeoutInMillis
argument_list|)
decl_stmt|;
name|request
operator|.
name|timeout
argument_list|(
name|newTimeout
argument_list|)
expr_stmt|;
name|executeHealth
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onNoLongerMaster
parameter_list|(
name|String
name|source
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"stopped being master while waiting for events with priority [{}]. retrying."
argument_list|,
name|request
operator|.
name|waitForEvents
argument_list|()
argument_list|)
expr_stmt|;
name|doExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"unexpected failure during [{}]"
argument_list|,
name|t
argument_list|,
name|source
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|runOnlyOnMaster
parameter_list|()
block|{
return|return
operator|!
name|request
operator|.
name|local
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|executeHealth
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|executeHealth
specifier|private
name|void
name|executeHealth
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
specifier|final
name|ActionListener
argument_list|<
name|ClusterHealthResponse
argument_list|>
name|listener
parameter_list|)
block|{
name|int
name|waitFor
init|=
literal|5
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|waitForStatus
argument_list|()
operator|==
literal|null
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForRelocatingShards
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForActiveShards
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|waitFor
operator|--
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|==
literal|null
operator|||
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// check that they actually exists in the meta data
name|waitFor
operator|--
expr_stmt|;
block|}
assert|assert
name|waitFor
operator|>=
literal|0
assert|;
specifier|final
name|ClusterStateObserver
name|observer
init|=
operator|new
name|ClusterStateObserver
argument_list|(
name|clusterService
argument_list|,
name|logger
argument_list|)
decl_stmt|;
specifier|final
name|ClusterState
name|state
init|=
name|observer
operator|.
name|observedState
argument_list|()
decl_stmt|;
if|if
condition|(
name|waitFor
operator|==
literal|0
operator|||
name|request
operator|.
name|timeout
argument_list|()
operator|.
name|millis
argument_list|()
operator|==
literal|0
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|getResponse
argument_list|(
name|request
argument_list|,
name|state
argument_list|,
name|waitFor
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
operator|.
name|millis
argument_list|()
operator|==
literal|0
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|int
name|concreteWaitFor
init|=
name|waitFor
decl_stmt|;
specifier|final
name|ClusterStateObserver
operator|.
name|ChangePredicate
name|validationPredicate
init|=
operator|new
name|ClusterStateObserver
operator|.
name|ValidationPredicate
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|validate
parameter_list|(
name|ClusterState
name|newState
parameter_list|)
block|{
return|return
name|newState
operator|.
name|status
argument_list|()
operator|==
name|ClusterState
operator|.
name|ClusterStateStatus
operator|.
name|APPLIED
operator|&&
name|validateRequest
argument_list|(
name|request
argument_list|,
name|newState
argument_list|,
name|concreteWaitFor
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|ClusterStateObserver
operator|.
name|Listener
name|stateListener
init|=
operator|new
name|ClusterStateObserver
operator|.
name|Listener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onNewClusterState
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|getResponse
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|,
name|concreteWaitFor
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onClusterServiceClose
parameter_list|()
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"ClusterService was close during health call"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
specifier|final
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
specifier|final
name|ClusterHealthResponse
name|response
init|=
name|getResponse
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|,
name|concreteWaitFor
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|state
operator|.
name|status
argument_list|()
operator|==
name|ClusterState
operator|.
name|ClusterStateStatus
operator|.
name|APPLIED
operator|&&
name|validateRequest
argument_list|(
name|request
argument_list|,
name|state
argument_list|,
name|concreteWaitFor
argument_list|)
condition|)
block|{
name|stateListener
operator|.
name|onNewClusterState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|observer
operator|.
name|waitForNextChange
argument_list|(
name|stateListener
argument_list|,
name|validationPredicate
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|validateRequest
specifier|private
name|boolean
name|validateRequest
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|int
name|waitFor
parameter_list|)
block|{
name|ClusterHealthResponse
name|response
init|=
name|clusterHealth
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|,
name|clusterService
operator|.
name|numberOfPendingTasks
argument_list|()
argument_list|,
name|gatewayAllocator
operator|.
name|getNumberOfInFlightFetch
argument_list|()
argument_list|,
name|clusterService
operator|.
name|getMaxTaskWaitTime
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|prepareResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|clusterState
argument_list|,
name|waitFor
argument_list|)
return|;
block|}
DECL|method|getResponse
specifier|private
name|ClusterHealthResponse
name|getResponse
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|int
name|waitFor
parameter_list|,
name|boolean
name|timedOut
parameter_list|)
block|{
name|ClusterHealthResponse
name|response
init|=
name|clusterHealth
argument_list|(
name|request
argument_list|,
name|clusterState
argument_list|,
name|clusterService
operator|.
name|numberOfPendingTasks
argument_list|()
argument_list|,
name|gatewayAllocator
operator|.
name|getNumberOfInFlightFetch
argument_list|()
argument_list|,
name|clusterService
operator|.
name|getMaxTaskWaitTime
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|valid
init|=
name|prepareResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|clusterState
argument_list|,
name|waitFor
argument_list|)
decl_stmt|;
assert|assert
name|valid
operator|||
name|timedOut
assert|;
comment|// we check for a timeout here since this method might be called from the wait_for_events
comment|// response handler which might have timed out already.
comment|// if the state is sufficient for what we where waiting for we don't need to mark this as timedOut.
comment|// We spend too much time in waiting for events such that we might already reached a valid state.
comment|// this should not mark the request as timed out
name|response
operator|.
name|timedOut
operator|=
name|timedOut
operator|&&
name|valid
operator|==
literal|false
expr_stmt|;
return|return
name|response
return|;
block|}
DECL|method|prepareResponse
specifier|private
name|boolean
name|prepareResponse
parameter_list|(
specifier|final
name|ClusterHealthRequest
name|request
parameter_list|,
specifier|final
name|ClusterHealthResponse
name|response
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
specifier|final
name|int
name|waitFor
parameter_list|)
block|{
name|int
name|waitForCounter
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|waitForStatus
argument_list|()
operator|!=
literal|null
operator|&&
name|response
operator|.
name|getStatus
argument_list|()
operator|.
name|value
argument_list|()
operator|<=
name|request
operator|.
name|waitForStatus
argument_list|()
operator|.
name|value
argument_list|()
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForRelocatingShards
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|response
operator|.
name|getRelocatingShards
argument_list|()
operator|<=
name|request
operator|.
name|waitForRelocatingShards
argument_list|()
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|waitForActiveShards
argument_list|()
operator|!=
operator|-
literal|1
operator|&&
name|response
operator|.
name|getActiveShards
argument_list|()
operator|>=
name|request
operator|.
name|waitForActiveShards
argument_list|()
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|request
operator|.
name|indices
argument_list|()
operator|!=
literal|null
operator|&&
name|request
operator|.
name|indices
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|indexNameExpressionResolver
operator|.
name|concreteIndices
argument_list|(
name|clusterState
argument_list|,
name|IndicesOptions
operator|.
name|strictExpand
argument_list|()
argument_list|,
name|request
operator|.
name|indices
argument_list|()
argument_list|)
expr_stmt|;
name|waitForCounter
operator|++
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexNotFoundException
name|e
parameter_list|)
block|{
name|response
operator|.
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
comment|// no indices, make sure its RED
comment|// missing indices, wait a bit more...
block|}
block|}
if|if
condition|(
operator|!
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|">="
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"ge("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"<="
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"le("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<=
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|">"
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"gt("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|>
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"<"
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"lt("
argument_list|)
condition|)
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
name|request
operator|.
name|waitForNodes
argument_list|()
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|<
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|int
name|expected
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|request
operator|.
name|waitForNodes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getNumberOfNodes
argument_list|()
operator|==
name|expected
condition|)
block|{
name|waitForCounter
operator|++
expr_stmt|;
block|}
block|}
block|}
return|return
name|waitForCounter
operator|==
name|waitFor
return|;
block|}
DECL|method|clusterHealth
specifier|private
name|ClusterHealthResponse
name|clusterHealth
parameter_list|(
name|ClusterHealthRequest
name|request
parameter_list|,
name|ClusterState
name|clusterState
parameter_list|,
name|int
name|numberOfPendingTasks
parameter_list|,
name|int
name|numberOfInFlightFetch
parameter_list|,
name|TimeValue
name|pendingTaskTimeInQueue
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Calculating health based on state version [{}]"
argument_list|,
name|clusterState
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|concreteIndices
decl_stmt|;
try|try
block|{
name|concreteIndices
operator|=
name|indexNameExpressionResolver
operator|.
name|concreteIndices
argument_list|(
name|clusterState
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexNotFoundException
name|e
parameter_list|)
block|{
comment|// one of the specified indices is not there - treat it as RED.
name|ClusterHealthResponse
name|response
init|=
operator|new
name|ClusterHealthResponse
argument_list|(
name|clusterName
operator|.
name|value
argument_list|()
argument_list|,
name|Strings
operator|.
name|EMPTY_ARRAY
argument_list|,
name|clusterState
argument_list|,
name|numberOfPendingTasks
argument_list|,
name|numberOfInFlightFetch
argument_list|,
name|UnassignedInfo
operator|.
name|getNumberOfDelayedUnassigned
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|settings
argument_list|,
name|clusterState
argument_list|)
argument_list|,
name|pendingTaskTimeInQueue
argument_list|)
decl_stmt|;
name|response
operator|.
name|status
operator|=
name|ClusterHealthStatus
operator|.
name|RED
expr_stmt|;
return|return
name|response
return|;
block|}
return|return
operator|new
name|ClusterHealthResponse
argument_list|(
name|clusterName
operator|.
name|value
argument_list|()
argument_list|,
name|concreteIndices
argument_list|,
name|clusterState
argument_list|,
name|numberOfPendingTasks
argument_list|,
name|numberOfInFlightFetch
argument_list|,
name|UnassignedInfo
operator|.
name|getNumberOfDelayedUnassigned
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|settings
argument_list|,
name|clusterState
argument_list|)
argument_list|,
name|pendingTaskTimeInQueue
argument_list|)
return|;
block|}
block|}
end_class

end_unit

