begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support.single
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
name|NoShardAvailableActionException
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
name|BaseAction
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
name|cluster
operator|.
name|node
operator|.
name|Node
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
name|Nodes
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
name|routing
operator|.
name|ShardsIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|IndicesService
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
name|*
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|TransportSingleOperationAction
specifier|public
specifier|abstract
class|class
name|TransportSingleOperationAction
parameter_list|<
name|Request
extends|extends
name|SingleOperationRequest
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
extends|extends
name|BaseAction
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
DECL|field|indicesService
specifier|protected
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
DECL|field|threadPool
specifier|protected
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|method|TransportSingleOperationAction
specifier|protected
name|TransportSingleOperationAction
parameter_list|(
name|Settings
name|settings
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
name|IndicesService
name|indicesService
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
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|transportAction
argument_list|()
argument_list|,
operator|new
name|TransportHandler
argument_list|()
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|transportShardAction
argument_list|()
argument_list|,
operator|new
name|ShardTransportHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doExecute
annotation|@
name|Override
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
DECL|method|transportAction
specifier|protected
specifier|abstract
name|String
name|transportAction
parameter_list|()
function_decl|;
DECL|method|transportShardAction
specifier|protected
specifier|abstract
name|String
name|transportShardAction
parameter_list|()
function_decl|;
DECL|method|shardOperation
specifier|protected
specifier|abstract
name|Response
name|shardOperation
parameter_list|(
name|Request
name|request
parameter_list|,
name|int
name|shardId
parameter_list|)
throws|throws
name|ElasticSearchException
function_decl|;
DECL|method|newRequest
specifier|protected
specifier|abstract
name|Request
name|newRequest
parameter_list|()
function_decl|;
DECL|method|newResponse
specifier|protected
specifier|abstract
name|Response
name|newResponse
parameter_list|()
function_decl|;
DECL|class|AsyncSingleAction
specifier|private
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
DECL|field|shardsIt
specifier|private
specifier|final
name|ShardsIterator
name|shardsIt
decl_stmt|;
DECL|field|request
specifier|private
specifier|final
name|Request
name|request
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|Nodes
name|nodes
decl_stmt|;
DECL|method|AsyncSingleAction
specifier|private
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
name|ClusterState
name|clusterState
init|=
name|clusterService
operator|.
name|state
argument_list|()
decl_stmt|;
name|nodes
operator|=
name|clusterState
operator|.
name|nodes
argument_list|()
expr_stmt|;
comment|// update to the concrete shard to use
name|request
operator|.
name|index
argument_list|(
name|clusterState
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndex
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|shardsIt
operator|=
name|indicesService
operator|.
name|indexServiceSafe
argument_list|(
name|request
operator|.
name|index
argument_list|()
argument_list|)
operator|.
name|operationRouting
argument_list|()
operator|.
name|getShards
argument_list|(
name|clusterState
argument_list|,
name|request
operator|.
name|type
argument_list|()
argument_list|,
name|request
operator|.
name|id
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
block|{
name|performFirst
argument_list|()
expr_stmt|;
block|}
DECL|method|onFailure
specifier|private
name|void
name|onFailure
parameter_list|(
name|ShardRouting
name|shardRouting
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
operator|&&
name|e
operator|!=
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|shardRouting
operator|.
name|shortSummary
argument_list|()
operator|+
literal|": Failed to get ["
operator|+
name|request
operator|.
name|type
argument_list|()
operator|+
literal|"#"
operator|+
name|request
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|perform
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**          * First get should try and use a shard that exists on a local node for better performance          */
DECL|method|performFirst
specifier|private
name|void
name|performFirst
parameter_list|()
block|{
while|while
condition|(
name|shardsIt
operator|.
name|hasNextActive
argument_list|()
condition|)
block|{
specifier|final
name|ShardRouting
name|shard
init|=
name|shardsIt
operator|.
name|nextActive
argument_list|()
decl_stmt|;
if|if
condition|(
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodes
operator|.
name|localNodeId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|request
operator|.
name|operationThreaded
argument_list|()
condition|)
block|{
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
try|try
block|{
name|Response
name|response
init|=
name|shardOperation
argument_list|(
name|request
argument_list|,
name|shard
operator|.
name|id
argument_list|()
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|shard
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
else|else
block|{
try|try
block|{
specifier|final
name|Response
name|response
init|=
name|shardOperation
argument_list|(
name|request
argument_list|,
name|shard
operator|.
name|id
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|listenerThreaded
argument_list|()
condition|)
block|{
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
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|onFailure
argument_list|(
name|shard
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
operator|!
name|shardsIt
operator|.
name|hasNextActive
argument_list|()
condition|)
block|{
comment|// no local node get, go remote
name|shardsIt
operator|.
name|reset
argument_list|()
expr_stmt|;
name|perform
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|perform
specifier|private
name|void
name|perform
parameter_list|(
specifier|final
name|Exception
name|lastException
parameter_list|)
block|{
while|while
condition|(
name|shardsIt
operator|.
name|hasNextActive
argument_list|()
condition|)
block|{
specifier|final
name|ShardRouting
name|shard
init|=
name|shardsIt
operator|.
name|nextActive
argument_list|()
decl_stmt|;
comment|// no need to check for local nodes, we tried them already in performFirstGet
if|if
condition|(
operator|!
name|shard
operator|.
name|currentNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|nodes
operator|.
name|localNodeId
argument_list|()
argument_list|)
condition|)
block|{
name|Node
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
name|transportShardAction
argument_list|()
argument_list|,
operator|new
name|ShardSingleOperationRequest
argument_list|(
name|request
argument_list|,
name|shard
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
operator|new
name|BaseTransportResponseHandler
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
name|void
name|handleResponse
parameter_list|(
specifier|final
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|listenerThreaded
argument_list|()
condition|)
block|{
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
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleException
parameter_list|(
name|RemoteTransportException
name|exp
parameter_list|)
block|{
name|onFailure
argument_list|(
name|shard
argument_list|,
name|exp
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
comment|// no need to spawn, we will execute the listener on a different thread if needed in handleResponse
return|return
literal|false
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
operator|!
name|shardsIt
operator|.
name|hasNextActive
argument_list|()
condition|)
block|{
name|Exception
name|failure
init|=
name|lastException
decl_stmt|;
if|if
condition|(
name|failure
operator|==
literal|null
condition|)
block|{
name|failure
operator|=
operator|new
name|NoShardAvailableActionException
argument_list|(
name|shardsIt
operator|.
name|shardId
argument_list|()
argument_list|,
literal|"No shard available for ["
operator|+
name|request
operator|.
name|type
argument_list|()
operator|+
literal|"#"
operator|+
name|request
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
name|shardsIt
operator|.
name|shardId
argument_list|()
operator|+
literal|": Failed to get ["
operator|+
name|request
operator|.
name|type
argument_list|()
operator|+
literal|"#"
operator|+
name|request
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|,
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|request
operator|.
name|listenerThreaded
argument_list|()
condition|)
block|{
specifier|final
name|Exception
name|fFailure
init|=
name|failure
decl_stmt|;
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
name|listener
operator|.
name|onFailure
argument_list|(
name|fFailure
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|failure
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|TransportHandler
specifier|private
class|class
name|TransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|Request
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|Request
name|newInstance
parameter_list|()
block|{
return|return
name|newRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
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
comment|// no need to have a threaded listener since we just send back a response
name|request
operator|.
name|listenerThreaded
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// if we have a local operation, execute it on a thread since we don't spawn
name|request
operator|.
name|operationThreaded
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|execute
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
name|result
parameter_list|)
block|{
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|result
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
name|Throwable
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
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send response for get"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|spawn
annotation|@
name|Override
specifier|public
name|boolean
name|spawn
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
DECL|class|ShardTransportHandler
specifier|private
class|class
name|ShardTransportHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|ShardSingleOperationRequest
argument_list|>
block|{
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|ShardSingleOperationRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|ShardSingleOperationRequest
argument_list|()
return|;
block|}
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ShardSingleOperationRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|Response
name|response
init|=
name|shardOperation
argument_list|(
name|request
operator|.
name|request
argument_list|()
argument_list|,
name|request
operator|.
name|shardId
argument_list|()
argument_list|)
decl_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|ShardSingleOperationRequest
specifier|protected
class|class
name|ShardSingleOperationRequest
implements|implements
name|Streamable
block|{
DECL|field|request
specifier|private
name|Request
name|request
decl_stmt|;
DECL|field|shardId
specifier|private
name|int
name|shardId
decl_stmt|;
DECL|method|ShardSingleOperationRequest
name|ShardSingleOperationRequest
parameter_list|()
block|{         }
DECL|method|ShardSingleOperationRequest
specifier|public
name|ShardSingleOperationRequest
parameter_list|(
name|Request
name|request
parameter_list|,
name|int
name|shardId
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
name|shardId
operator|=
name|shardId
expr_stmt|;
block|}
DECL|method|request
specifier|public
name|Request
name|request
parameter_list|()
block|{
return|return
name|request
return|;
block|}
DECL|method|shardId
specifier|public
name|int
name|shardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|request
operator|=
name|newRequest
argument_list|()
expr_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|shardId
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|request
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|shardId
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

