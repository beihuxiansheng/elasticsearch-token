begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.single.instance
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|single
operator|.
name|instance
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
name|ActionResponse
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
name|UnavailableShardsException
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
name|HandledTransportAction
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
name|ClusterStateObserver
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
name|block
operator|.
name|ClusterBlockLevel
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
name|ShardIterator
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
name|ShardRouting
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
name|node
operator|.
name|NodeClosedException
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
name|TransportResponseHandler
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
name|ConnectTransportException
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
name|TransportChannel
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
name|TransportException
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
name|TransportRequestHandler
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
name|TransportRequestOptions
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportInstanceSingleOperationAction
specifier|public
specifier|abstract
class|class
name|TransportInstanceSingleOperationAction
parameter_list|<
name|Request
extends|extends
name|InstanceShardOperationRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
extends|extends
name|HandledTransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
block|{
DECL|field|clusterService
specifier|protected
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|transportService
specifier|protected
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|executor
specifier|final
name|String
name|executor
decl_stmt|;
DECL|field|shardActionName
specifier|final
name|String
name|shardActionName
decl_stmt|;
DECL|method|TransportInstanceSingleOperationAction
specifier|protected
name|TransportInstanceSingleOperationAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|actionName
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|Supplier
argument_list|<
name|Request
argument_list|>
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|actionName
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|actionFilters
argument_list|,
name|indexNameExpressionResolver
argument_list|,
name|request
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
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
argument_list|()
expr_stmt|;
name|this
operator|.
name|shardActionName
operator|=
name|actionName
operator|+
literal|"[s]"
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|shardActionName
argument_list|,
name|request
argument_list|,
name|executor
argument_list|,
operator|new
name|ShardTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
operator|new
name|AsyncSingleAction
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|executor
specifier|protected
specifier|abstract
name|String
name|executor
parameter_list|()
function_decl|;
DECL|method|shardOperation
specifier|protected
specifier|abstract
name|void
name|shardOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
function_decl|;
DECL|method|newResponse
specifier|protected
specifier|abstract
name|Response
name|newResponse
parameter_list|()
function_decl|;
DECL|method|checkGlobalBlock
specifier|protected
name|ClusterBlockException
name|checkGlobalBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|globalBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|)
return|;
block|}
DECL|method|checkRequestBlock
specifier|protected
name|ClusterBlockException
name|checkRequestBlock
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
return|return
name|state
operator|.
name|blocks
argument_list|()
operator|.
name|indexBlockedException
argument_list|(
name|ClusterBlockLevel
operator|.
name|WRITE
argument_list|,
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Resolves the request. Throws an exception if the request cannot be resolved.      */
DECL|method|resolveRequest
specifier|protected
specifier|abstract
name|void
name|resolveRequest
parameter_list|(
name|ClusterState
name|state
parameter_list|,
name|Request
name|request
parameter_list|)
function_decl|;
DECL|method|retryOnFailure
specifier|protected
name|boolean
name|retryOnFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
DECL|method|transportOptions
specifier|protected
name|TransportRequestOptions
name|transportOptions
parameter_list|()
block|{
return|return
name|TransportRequestOptions
operator|.
name|EMPTY
return|;
block|}
comment|/**      * Should return an iterator with a single shard!      */
DECL|method|shards
specifier|protected
specifier|abstract
name|ShardIterator
name|shards
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|,
name|Request
name|request
parameter_list|)
function_decl|;
DECL|class|AsyncSingleAction
class|class
name|AsyncSingleAction
block|{
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|observer
specifier|private
specifier|volatile
name|ClusterStateObserver
name|observer
decl_stmt|;
DECL|field|shardIt
specifier|private
name|ShardIterator
name|shardIt
decl_stmt|;
DECL|field|nodes
specifier|private
name|DiscoveryNodes
name|nodes
decl_stmt|;
DECL|method|AsyncSingleAction
name|AsyncSingleAction
parameter_list|(
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|this
operator|.
name|observer
operator|=
operator|new
name|ClusterStateObserver
argument_list|(
name|clusterService
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|,
name|logger
argument_list|,
name|threadPool
operator|.
name|getThreadContext
argument_list|()
argument_list|)
expr_stmt|;
name|doStart
argument_list|()
expr_stmt|;
block|}
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
name|nodes
operator|=
name|observer
operator|.
name|observedState
argument_list|()
operator|.
name|nodes
argument_list|()
expr_stmt|;
try|try
block|{
name|ClusterBlockException
name|blockException
init|=
name|checkGlobalBlock
argument_list|(
name|observer
operator|.
name|observedState
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|blockException
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|blockException
operator|.
name|retryable
argument_list|()
condition|)
block|{
name|retry
argument_list|(
name|blockException
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
throw|throw
name|blockException
throw|;
block|}
block|}
name|request
operator|.
name|concreteIndex
argument_list|(
name|indexNameExpressionResolver
operator|.
name|concreteSingleIndex
argument_list|(
name|observer
operator|.
name|observedState
argument_list|()
argument_list|,
name|request
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|resolveRequest
argument_list|(
name|observer
operator|.
name|observedState
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
name|blockException
operator|=
name|checkRequestBlock
argument_list|(
name|observer
operator|.
name|observedState
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
if|if
condition|(
name|blockException
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|blockException
operator|.
name|retryable
argument_list|()
condition|)
block|{
name|retry
argument_list|(
name|blockException
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
throw|throw
name|blockException
throw|;
block|}
block|}
name|shardIt
operator|=
name|shards
argument_list|(
name|observer
operator|.
name|observedState
argument_list|()
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// no shardIt, might be in the case between index gateway recovery and shardIt initialization
if|if
condition|(
name|shardIt
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|retry
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// this transport only make sense with an iterator that returns a single shard routing (like primary)
assert|assert
name|shardIt
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
name|ShardRouting
name|shard
init|=
name|shardIt
operator|.
name|nextOrNull
argument_list|()
decl_stmt|;
assert|assert
name|shard
operator|!=
literal|null
assert|;
if|if
condition|(
operator|!
name|shard
operator|.
name|active
argument_list|()
condition|)
block|{
name|retry
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
name|request
operator|.
name|shardId
operator|=
name|shardIt
operator|.
name|shardId
argument_list|()
expr_stmt|;
name|DiscoveryNode
name|node
init|=
name|nodes
operator|.
name|get
argument_list|(
name|shard
operator|.
name|currentNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|shardActionName
argument_list|,
name|request
argument_list|,
name|transportOptions
argument_list|()
argument_list|,
operator|new
name|TransportResponseHandler
argument_list|<
name|Response
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Response
name|newInstance
parameter_list|()
block|{
return|return
name|newResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|executor
parameter_list|()
block|{
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
specifier|public
name|void
name|handleResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|TransportException
name|exp
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|exp
operator|.
name|unwrapCause
argument_list|()
decl_stmt|;
comment|// if we got disconnected from the node, or the node / shard is not in the right state (being closed)
if|if
condition|(
name|cause
operator|instanceof
name|ConnectTransportException
operator|||
name|cause
operator|instanceof
name|NodeClosedException
operator|||
name|retryOnFailure
argument_list|(
name|exp
argument_list|)
condition|)
block|{
name|retry
argument_list|(
operator|(
name|Exception
operator|)
name|cause
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|exp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|retry
name|void
name|retry
parameter_list|(
annotation|@
name|Nullable
specifier|final
name|Exception
name|failure
parameter_list|)
block|{
if|if
condition|(
name|observer
operator|.
name|isTimedOut
argument_list|()
condition|)
block|{
comment|// we running as a last attempt after a timeout has happened. don't retry
name|Exception
name|listenFailure
init|=
name|failure
decl_stmt|;
if|if
condition|(
name|listenFailure
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|shardIt
operator|==
literal|null
condition|)
block|{
name|listenFailure
operator|=
operator|new
name|UnavailableShardsException
argument_list|(
name|request
operator|.
name|concreteIndex
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Timeout waiting for [{}], request: {}"
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|,
name|actionName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listenFailure
operator|=
operator|new
name|UnavailableShardsException
argument_list|(
name|shardIt
operator|.
name|shardId
argument_list|()
argument_list|,
literal|"[{}] shardIt, [{}] active : Timeout waiting for [{}], request: {}"
argument_list|,
name|shardIt
operator|.
name|size
argument_list|()
argument_list|,
name|shardIt
operator|.
name|sizeActive
argument_list|()
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|,
name|actionName
argument_list|)
expr_stmt|;
block|}
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|listenFailure
argument_list|)
expr_stmt|;
return|return;
block|}
name|observer
operator|.
name|waitForNextChange
argument_list|(
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
name|state
parameter_list|)
block|{
name|doStart
argument_list|()
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
name|NodeClosedException
argument_list|(
name|nodes
operator|.
name|getLocalNode
argument_list|()
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
comment|// just to be on the safe side, see if we can start it now?
name|doStart
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|request
operator|.
name|timeout
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ShardTransportHandler
specifier|private
class|class
name|ShardTransportHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|Request
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
specifier|final
name|Request
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|shardOperation
argument_list|(
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|Response
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|inner
parameter_list|)
block|{
name|inner
operator|.
name|addSuppressed
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to send response for get"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

