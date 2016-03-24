begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.fd
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|fd
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|ClusterName
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
name|common
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
name|EsRejectedExecutionException
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
name|BaseTransportResponseHandler
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
name|TransportRequest
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
name|TransportResponse
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
name|io
operator|.
name|IOException
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
name|CopyOnWriteArrayList
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
name|ConcurrentCollections
operator|.
name|newConcurrentMap
import|;
end_import

begin_comment
comment|/**  * A fault detection of multiple nodes.  */
end_comment

begin_class
DECL|class|NodesFaultDetection
specifier|public
class|class
name|NodesFaultDetection
extends|extends
name|FaultDetection
block|{
DECL|field|PING_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|PING_ACTION_NAME
init|=
literal|"internal:discovery/zen/fd/ping"
decl_stmt|;
DECL|class|Listener
specifier|public
specifier|abstract
specifier|static
class|class
name|Listener
block|{
DECL|method|onNodeFailure
specifier|public
name|void
name|onNodeFailure
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|,
name|String
name|reason
parameter_list|)
block|{}
DECL|method|onPingReceived
specifier|public
name|void
name|onPingReceived
parameter_list|(
name|PingRequest
name|pingRequest
parameter_list|)
block|{}
block|}
DECL|field|listeners
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|Listener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|nodesFD
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|NodeFD
argument_list|>
name|nodesFD
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|clusterStateVersion
specifier|private
specifier|volatile
name|long
name|clusterStateVersion
init|=
name|ClusterState
operator|.
name|UNKNOWN_VERSION
decl_stmt|;
DECL|field|localNode
specifier|private
specifier|volatile
name|DiscoveryNode
name|localNode
decl_stmt|;
DECL|method|NodesFaultDetection
specifier|public
name|NodesFaultDetection
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"[node  ] uses ping_interval [{}], ping_timeout [{}], ping_retries [{}]"
argument_list|,
name|pingInterval
argument_list|,
name|pingRetryTimeout
argument_list|,
name|pingRetryCount
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|PING_ACTION_NAME
argument_list|,
name|PingRequest
operator|::
operator|new
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
operator|new
name|PingRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setLocalNode
specifier|public
name|void
name|setLocalNode
parameter_list|(
name|DiscoveryNode
name|localNode
parameter_list|)
block|{
name|this
operator|.
name|localNode
operator|=
name|localNode
expr_stmt|;
block|}
DECL|method|addListener
specifier|public
name|void
name|addListener
parameter_list|(
name|Listener
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
DECL|method|removeListener
specifier|public
name|void
name|removeListener
parameter_list|(
name|Listener
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
block|}
comment|/**      * make sure that nodes in clusterState are pinged. Any pinging to nodes which are not      * part of the cluster will be stopped      */
DECL|method|updateNodesAndPing
specifier|public
name|void
name|updateNodesAndPing
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
comment|// remove any nodes we don't need, this will cause their FD to stop
for|for
control|(
name|DiscoveryNode
name|monitoredNode
range|:
name|nodesFD
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|!
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|nodeExists
argument_list|(
name|monitoredNode
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
name|nodesFD
operator|.
name|remove
argument_list|(
name|monitoredNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add any missing nodes
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|clusterState
operator|.
name|nodes
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|.
name|equals
argument_list|(
name|localNode
argument_list|)
condition|)
block|{
comment|// no need to monitor the local node
continue|continue;
block|}
if|if
condition|(
operator|!
name|nodesFD
operator|.
name|containsKey
argument_list|(
name|node
argument_list|)
condition|)
block|{
name|NodeFD
name|fd
init|=
operator|new
name|NodeFD
argument_list|(
name|node
argument_list|)
decl_stmt|;
comment|// it's OK to overwrite an existing nodeFD - it will just stop and the new one will pick things up.
name|nodesFD
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|fd
argument_list|)
expr_stmt|;
comment|// we use schedule with a 0 time value to run the pinger on the pool as it will run on later
name|threadPool
operator|.
name|schedule
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|fd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** stops all pinging **/
DECL|method|stop
specifier|public
name|NodesFaultDetection
name|stop
parameter_list|()
block|{
name|nodesFD
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|stop
argument_list|()
expr_stmt|;
name|transportService
operator|.
name|removeHandler
argument_list|(
name|PING_ACTION_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handleTransportDisconnect
specifier|protected
name|void
name|handleTransportDisconnect
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|NodeFD
name|nodeFD
init|=
name|nodesFD
operator|.
name|remove
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeFD
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|connectOnNetworkDisconnect
condition|)
block|{
name|NodeFD
name|fd
init|=
operator|new
name|NodeFD
argument_list|(
name|node
argument_list|)
decl_stmt|;
try|try
block|{
name|transportService
operator|.
name|connectToNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|nodesFD
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|fd
argument_list|)
expr_stmt|;
comment|// we use schedule with a 0 time value to run the pinger on the pool as it will run on later
name|threadPool
operator|.
name|schedule
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|fd
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
name|trace
argument_list|(
literal|"[node  ] [{}] transport disconnected (with verified connect)"
argument_list|,
name|node
argument_list|)
expr_stmt|;
comment|// clean up if needed, just to be safe..
name|nodesFD
operator|.
name|remove
argument_list|(
name|node
argument_list|,
name|fd
argument_list|)
expr_stmt|;
name|notifyNodeFailure
argument_list|(
name|node
argument_list|,
literal|"transport disconnected (with verified connect)"
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
literal|"[node  ] [{}] transport disconnected"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|notifyNodeFailure
argument_list|(
name|node
argument_list|,
literal|"transport disconnected"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|notifyNodeFailure
specifier|private
name|void
name|notifyNodeFailure
parameter_list|(
specifier|final
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
try|try
block|{
name|threadPool
operator|.
name|generic
argument_list|()
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
name|Listener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|onNodeFailure
argument_list|(
name|node
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EsRejectedExecutionException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[node  ] [{}] ignoring node failure (reason [{}]). Local node is shutting down"
argument_list|,
name|ex
argument_list|,
name|node
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|notifyPingReceived
specifier|private
name|void
name|notifyPingReceived
parameter_list|(
specifier|final
name|PingRequest
name|pingRequest
parameter_list|)
block|{
name|threadPool
operator|.
name|generic
argument_list|()
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
name|Listener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|onPingReceived
argument_list|(
name|pingRequest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|class|NodeFD
specifier|private
class|class
name|NodeFD
implements|implements
name|Runnable
block|{
DECL|field|retryCount
specifier|volatile
name|int
name|retryCount
decl_stmt|;
DECL|field|node
specifier|private
specifier|final
name|DiscoveryNode
name|node
decl_stmt|;
DECL|method|NodeFD
specifier|private
name|NodeFD
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
DECL|method|running
specifier|private
name|boolean
name|running
parameter_list|()
block|{
return|return
name|NodeFD
operator|.
name|this
operator|.
name|equals
argument_list|(
name|nodesFD
operator|.
name|get
argument_list|(
name|node
argument_list|)
argument_list|)
return|;
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
operator|!
name|running
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
name|PingRequest
name|pingRequest
init|=
operator|new
name|PingRequest
argument_list|(
name|node
operator|.
name|id
argument_list|()
argument_list|,
name|clusterName
argument_list|,
name|localNode
argument_list|,
name|clusterStateVersion
argument_list|)
decl_stmt|;
specifier|final
name|TransportRequestOptions
name|options
init|=
name|TransportRequestOptions
operator|.
name|builder
argument_list|()
operator|.
name|withType
argument_list|(
name|TransportRequestOptions
operator|.
name|Type
operator|.
name|PING
argument_list|)
operator|.
name|withTimeout
argument_list|(
name|pingRetryTimeout
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|PING_ACTION_NAME
argument_list|,
name|pingRequest
argument_list|,
name|options
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|PingResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|PingResponse
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|PingResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|PingResponse
name|response
parameter_list|)
block|{
if|if
condition|(
operator|!
name|running
argument_list|()
condition|)
block|{
return|return;
block|}
name|retryCount
operator|=
literal|0
expr_stmt|;
name|threadPool
operator|.
name|schedule
argument_list|(
name|pingInterval
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
name|NodeFD
operator|.
name|this
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
if|if
condition|(
operator|!
name|running
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|exp
operator|instanceof
name|ConnectTransportException
operator|||
name|exp
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ConnectTransportException
condition|)
block|{
name|handleTransportDisconnect
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return;
block|}
name|retryCount
operator|++
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"[node  ] failed to ping [{}], retry [{}] out of [{}]"
argument_list|,
name|exp
argument_list|,
name|node
argument_list|,
name|retryCount
argument_list|,
name|pingRetryCount
argument_list|)
expr_stmt|;
if|if
condition|(
name|retryCount
operator|>=
name|pingRetryCount
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[node  ] failed to ping [{}], tried [{}] times, each with  maximum [{}] timeout"
argument_list|,
name|node
argument_list|,
name|pingRetryCount
argument_list|,
name|pingRetryTimeout
argument_list|)
expr_stmt|;
comment|// not good, failure
if|if
condition|(
name|nodesFD
operator|.
name|remove
argument_list|(
name|node
argument_list|,
name|NodeFD
operator|.
name|this
argument_list|)
condition|)
block|{
name|notifyNodeFailure
argument_list|(
name|node
argument_list|,
literal|"failed to ping, tried ["
operator|+
name|pingRetryCount
operator|+
literal|"] times, each with maximum ["
operator|+
name|pingRetryTimeout
operator|+
literal|"] timeout"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// resend the request, not reschedule, rely on send timeout
name|transportService
operator|.
name|sendRequest
argument_list|(
name|node
argument_list|,
name|PING_ACTION_NAME
argument_list|,
name|pingRequest
argument_list|,
name|options
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
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
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PingRequestHandler
class|class
name|PingRequestHandler
implements|implements
name|TransportRequestHandler
argument_list|<
name|PingRequest
argument_list|>
block|{
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|PingRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
comment|// if we are not the node we are supposed to be pinged, send an exception
comment|// this can happen when a kill -9 is sent, and another node is started using the same port
if|if
condition|(
operator|!
name|localNode
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
name|request
operator|.
name|nodeId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Got pinged as node ["
operator|+
name|request
operator|.
name|nodeId
operator|+
literal|"], but I am node ["
operator|+
name|localNode
operator|.
name|id
argument_list|()
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// PingRequest will have clusterName set to null if it came from a node of version<1.4.0
if|if
condition|(
name|request
operator|.
name|clusterName
operator|!=
literal|null
operator|&&
operator|!
name|request
operator|.
name|clusterName
operator|.
name|equals
argument_list|(
name|clusterName
argument_list|)
condition|)
block|{
comment|// Don't introduce new exception for bwc reasons
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Got pinged with cluster name ["
operator|+
name|request
operator|.
name|clusterName
operator|+
literal|"], but I'm part of cluster ["
operator|+
name|clusterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|notifyPingReceived
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|PingResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PingRequest
specifier|public
specifier|static
class|class
name|PingRequest
extends|extends
name|TransportRequest
block|{
comment|// the (assumed) node id we are pinging
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|clusterName
specifier|private
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|masterNode
specifier|private
name|DiscoveryNode
name|masterNode
decl_stmt|;
DECL|field|clusterStateVersion
specifier|private
name|long
name|clusterStateVersion
init|=
name|ClusterState
operator|.
name|UNKNOWN_VERSION
decl_stmt|;
DECL|method|PingRequest
specifier|public
name|PingRequest
parameter_list|()
block|{         }
DECL|method|PingRequest
name|PingRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|DiscoveryNode
name|masterNode
parameter_list|,
name|long
name|clusterStateVersion
parameter_list|)
block|{
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|masterNode
operator|=
name|masterNode
expr_stmt|;
name|this
operator|.
name|clusterStateVersion
operator|=
name|clusterStateVersion
expr_stmt|;
block|}
DECL|method|nodeId
specifier|public
name|String
name|nodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
DECL|method|clusterName
specifier|public
name|ClusterName
name|clusterName
parameter_list|()
block|{
return|return
name|clusterName
return|;
block|}
DECL|method|masterNode
specifier|public
name|DiscoveryNode
name|masterNode
parameter_list|()
block|{
return|return
name|masterNode
return|;
block|}
DECL|method|clusterStateVersion
specifier|public
name|long
name|clusterStateVersion
parameter_list|()
block|{
return|return
name|clusterStateVersion
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|nodeId
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|clusterName
operator|=
name|ClusterName
operator|.
name|readClusterName
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|masterNode
operator|=
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|clusterStateVersion
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|clusterName
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|masterNode
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|clusterStateVersion
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|PingResponse
specifier|private
specifier|static
class|class
name|PingResponse
extends|extends
name|TransportResponse
block|{
DECL|method|PingResponse
specifier|private
name|PingResponse
parameter_list|()
block|{         }
annotation|@
name|Override
DECL|method|readFrom
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

