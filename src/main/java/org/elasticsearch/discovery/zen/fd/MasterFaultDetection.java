begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|ElasticSearchIllegalStateException
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
name|common
operator|.
name|component
operator|.
name|AbstractComponent
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
name|discovery
operator|.
name|zen
operator|.
name|DiscoveryNodesProvider
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
name|atomic
operator|.
name|AtomicBoolean
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
name|unit
operator|.
name|TimeValue
operator|.
name|timeValueSeconds
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|TransportRequestOptions
operator|.
name|options
import|;
end_import

begin_comment
comment|/**  * A fault detection that pings the master periodically to see if its alive.  *  *  */
end_comment

begin_class
DECL|class|MasterFaultDetection
specifier|public
class|class
name|MasterFaultDetection
extends|extends
name|AbstractComponent
block|{
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onMasterFailure
name|void
name|onMasterFailure
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|String
name|reason
parameter_list|)
function_decl|;
DECL|method|onDisconnectedFromMaster
name|void
name|onDisconnectedFromMaster
parameter_list|()
function_decl|;
block|}
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
DECL|field|nodesProvider
specifier|private
specifier|final
name|DiscoveryNodesProvider
name|nodesProvider
decl_stmt|;
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
argument_list|<
name|Listener
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|connectOnNetworkDisconnect
specifier|private
specifier|final
name|boolean
name|connectOnNetworkDisconnect
decl_stmt|;
DECL|field|pingInterval
specifier|private
specifier|final
name|TimeValue
name|pingInterval
decl_stmt|;
DECL|field|pingRetryTimeout
specifier|private
specifier|final
name|TimeValue
name|pingRetryTimeout
decl_stmt|;
DECL|field|pingRetryCount
specifier|private
specifier|final
name|int
name|pingRetryCount
decl_stmt|;
comment|// used mainly for testing, should always be true
DECL|field|registerConnectionListener
specifier|private
specifier|final
name|boolean
name|registerConnectionListener
decl_stmt|;
DECL|field|connectionListener
specifier|private
specifier|final
name|FDConnectionListener
name|connectionListener
decl_stmt|;
DECL|field|masterPinger
specifier|private
specifier|volatile
name|MasterPinger
name|masterPinger
decl_stmt|;
DECL|field|masterNodeMutex
specifier|private
specifier|final
name|Object
name|masterNodeMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|masterNode
specifier|private
specifier|volatile
name|DiscoveryNode
name|masterNode
decl_stmt|;
DECL|field|retryCount
specifier|private
specifier|volatile
name|int
name|retryCount
decl_stmt|;
DECL|field|notifiedMasterFailure
specifier|private
specifier|final
name|AtomicBoolean
name|notifiedMasterFailure
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|MasterFaultDetection
specifier|public
name|MasterFaultDetection
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
name|DiscoveryNodesProvider
name|nodesProvider
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
name|nodesProvider
operator|=
name|nodesProvider
expr_stmt|;
name|this
operator|.
name|connectOnNetworkDisconnect
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"connect_on_network_disconnect"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|pingInterval
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"ping_interval"
argument_list|,
name|timeValueSeconds
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|pingRetryTimeout
operator|=
name|componentSettings
operator|.
name|getAsTime
argument_list|(
literal|"ping_timeout"
argument_list|,
name|timeValueSeconds
argument_list|(
literal|30
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|pingRetryCount
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"ping_retries"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|this
operator|.
name|registerConnectionListener
operator|=
name|componentSettings
operator|.
name|getAsBoolean
argument_list|(
literal|"register_connection_listener"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"[master] uses ping_interval [{}], ping_timeout [{}], ping_retries [{}]"
argument_list|,
name|pingInterval
argument_list|,
name|pingRetryTimeout
argument_list|,
name|pingRetryCount
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectionListener
operator|=
operator|new
name|FDConnectionListener
argument_list|()
expr_stmt|;
if|if
condition|(
name|registerConnectionListener
condition|)
block|{
name|transportService
operator|.
name|addConnectionListener
argument_list|(
name|connectionListener
argument_list|)
expr_stmt|;
block|}
name|transportService
operator|.
name|registerHandler
argument_list|(
name|MasterPingRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|MasterPingRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|masterNode
specifier|public
name|DiscoveryNode
name|masterNode
parameter_list|()
block|{
return|return
name|this
operator|.
name|masterNode
return|;
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
DECL|method|restart
specifier|public
name|void
name|restart
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
synchronized|synchronized
init|(
name|masterNodeMutex
init|)
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
literal|"[master] restarting fault detection against master [{}], reason [{}]"
argument_list|,
name|masterNode
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
name|innerStop
argument_list|()
expr_stmt|;
name|innerStart
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
specifier|final
name|DiscoveryNode
name|masterNode
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
synchronized|synchronized
init|(
name|masterNodeMutex
init|)
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
literal|"[master] starting fault detection against master [{}], reason [{}]"
argument_list|,
name|masterNode
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
name|innerStart
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|innerStart
specifier|private
name|void
name|innerStart
parameter_list|(
specifier|final
name|DiscoveryNode
name|masterNode
parameter_list|)
block|{
name|this
operator|.
name|masterNode
operator|=
name|masterNode
expr_stmt|;
name|this
operator|.
name|retryCount
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|notifiedMasterFailure
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// try and connect to make sure we are connected
try|try
block|{
name|transportService
operator|.
name|connectToNode
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|// notify master failure (which stops also) and bail..
name|notifyMasterFailure
argument_list|(
name|masterNode
argument_list|,
literal|"failed to perform initial connect ["
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|masterPinger
operator|!=
literal|null
condition|)
block|{
name|masterPinger
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|masterPinger
operator|=
operator|new
name|MasterPinger
argument_list|()
expr_stmt|;
comment|// start the ping process
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
name|masterPinger
argument_list|)
expr_stmt|;
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|(
name|String
name|reason
parameter_list|)
block|{
synchronized|synchronized
init|(
name|masterNodeMutex
init|)
block|{
if|if
condition|(
name|masterNode
operator|!=
literal|null
condition|)
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
literal|"[master] stopping fault detection against master [{}], reason [{}]"
argument_list|,
name|masterNode
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
name|innerStop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|innerStop
specifier|private
name|void
name|innerStop
parameter_list|()
block|{
comment|// also will stop the next ping schedule
name|this
operator|.
name|retryCount
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|masterPinger
operator|!=
literal|null
condition|)
block|{
name|masterPinger
operator|.
name|stop
argument_list|()
expr_stmt|;
name|masterPinger
operator|=
literal|null
expr_stmt|;
block|}
name|this
operator|.
name|masterNode
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
name|stop
argument_list|(
literal|"closing"
argument_list|)
expr_stmt|;
name|this
operator|.
name|listeners
operator|.
name|clear
argument_list|()
expr_stmt|;
name|transportService
operator|.
name|removeConnectionListener
argument_list|(
name|connectionListener
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|removeHandler
argument_list|(
name|MasterPingRequestHandler
operator|.
name|ACTION
argument_list|)
expr_stmt|;
block|}
DECL|method|handleTransportDisconnect
specifier|private
name|void
name|handleTransportDisconnect
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
synchronized|synchronized
init|(
name|masterNodeMutex
init|)
block|{
if|if
condition|(
operator|!
name|node
operator|.
name|equals
argument_list|(
name|this
operator|.
name|masterNode
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|connectOnNetworkDisconnect
condition|)
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
comment|// if all is well, make sure we restart the pinger
if|if
condition|(
name|masterPinger
operator|!=
literal|null
condition|)
block|{
name|masterPinger
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|masterPinger
operator|=
operator|new
name|MasterPinger
argument_list|()
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
name|masterPinger
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
literal|"[master] [{}] transport disconnected (with verified connect)"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterNode
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
literal|"[master] [{}] transport disconnected"
argument_list|,
name|node
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|node
argument_list|,
literal|"transport disconnected"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|notifyDisconnectedFromMaster
specifier|private
name|void
name|notifyDisconnectedFromMaster
parameter_list|()
block|{
name|threadPool
operator|.
name|cached
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
name|onDisconnectedFromMaster
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|notifyMasterFailure
specifier|private
name|void
name|notifyMasterFailure
parameter_list|(
specifier|final
name|DiscoveryNode
name|masterNode
parameter_list|,
specifier|final
name|String
name|reason
parameter_list|)
block|{
if|if
condition|(
name|notifiedMasterFailure
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|threadPool
operator|.
name|cached
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
name|onMasterFailure
argument_list|(
name|masterNode
argument_list|,
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|stop
argument_list|(
literal|"master failure, "
operator|+
name|reason
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|FDConnectionListener
specifier|private
class|class
name|FDConnectionListener
implements|implements
name|TransportConnectionListener
block|{
annotation|@
name|Override
DECL|method|onNodeConnected
specifier|public
name|void
name|onNodeConnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{         }
annotation|@
name|Override
DECL|method|onNodeDisconnected
specifier|public
name|void
name|onNodeDisconnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
name|handleTransportDisconnect
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MasterPinger
specifier|private
class|class
name|MasterPinger
implements|implements
name|Runnable
block|{
DECL|field|running
specifier|private
specifier|volatile
name|boolean
name|running
init|=
literal|true
decl_stmt|;
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|running
operator|=
literal|false
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
operator|!
name|running
condition|)
block|{
comment|// return and don't spawn...
return|return;
block|}
specifier|final
name|DiscoveryNode
name|masterToPing
init|=
name|masterNode
decl_stmt|;
if|if
condition|(
name|masterToPing
operator|==
literal|null
condition|)
block|{
comment|// master is null, should not happen, but we are still running, so reschedule
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
name|MasterPinger
operator|.
name|this
argument_list|)
expr_stmt|;
return|return;
block|}
name|transportService
operator|.
name|sendRequest
argument_list|(
name|masterToPing
argument_list|,
name|MasterPingRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|MasterPingRequest
argument_list|(
name|nodesProvider
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|masterToPing
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|options
argument_list|()
operator|.
name|withHighType
argument_list|()
operator|.
name|withTimeout
argument_list|(
name|pingRetryTimeout
argument_list|)
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|MasterPingResponseResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MasterPingResponseResponse
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MasterPingResponseResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|handleResponse
parameter_list|(
name|MasterPingResponseResponse
name|response
parameter_list|)
block|{
if|if
condition|(
operator|!
name|running
condition|)
block|{
return|return;
block|}
comment|// reset the counter, we got a good result
name|MasterFaultDetection
operator|.
name|this
operator|.
name|retryCount
operator|=
literal|0
expr_stmt|;
comment|// check if the master node did not get switched on us..., if it did, we simply return with no reschedule
if|if
condition|(
name|masterToPing
operator|.
name|equals
argument_list|(
name|MasterFaultDetection
operator|.
name|this
operator|.
name|masterNode
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|response
operator|.
name|connectedToMaster
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[master] [{}] does not have us registered with it..."
argument_list|,
name|masterToPing
argument_list|)
expr_stmt|;
name|notifyDisconnectedFromMaster
argument_list|()
expr_stmt|;
block|}
comment|// we don't stop on disconnection from master, we keep pinging it
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
name|MasterPinger
operator|.
name|this
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
name|TransportException
name|exp
parameter_list|)
block|{
if|if
condition|(
operator|!
name|running
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|exp
operator|instanceof
name|ConnectTransportException
condition|)
block|{
comment|// ignore this one, we already handle it by registering a connection listener
return|return;
block|}
synchronized|synchronized
init|(
name|masterNodeMutex
init|)
block|{
comment|// check if the master node did not get switched on us...
if|if
condition|(
name|masterToPing
operator|.
name|equals
argument_list|(
name|MasterFaultDetection
operator|.
name|this
operator|.
name|masterNode
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|exp
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NoLongerMasterException
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[master] pinging a master {} that is no longer a master"
argument_list|,
name|masterNode
argument_list|,
name|pingRetryCount
argument_list|,
name|pingRetryTimeout
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterToPing
argument_list|,
literal|"no longer master"
argument_list|)
expr_stmt|;
block|}
name|int
name|retryCount
init|=
operator|++
name|MasterFaultDetection
operator|.
name|this
operator|.
name|retryCount
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"[master] failed to ping [{}], retry [{}] out of [{}]"
argument_list|,
name|exp
argument_list|,
name|masterNode
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
literal|"[master] failed to ping [{}], tried [{}] times, each with maximum [{}] timeout"
argument_list|,
name|masterNode
argument_list|,
name|pingRetryCount
argument_list|,
name|pingRetryTimeout
argument_list|)
expr_stmt|;
comment|// not good, failure
name|notifyMasterFailure
argument_list|(
name|masterToPing
argument_list|,
literal|"failed to ping, tried ["
operator|+
name|pingRetryCount
operator|+
literal|"] times, each with  maximum ["
operator|+
name|pingRetryTimeout
operator|+
literal|"] timeout"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// resend the request, not reschedule, rely on send timeout
name|transportService
operator|.
name|sendRequest
argument_list|(
name|masterToPing
argument_list|,
name|MasterPingRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|MasterPingRequest
argument_list|(
name|nodesProvider
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|,
name|masterToPing
operator|.
name|id
argument_list|()
argument_list|)
argument_list|,
name|options
argument_list|()
operator|.
name|withHighType
argument_list|()
operator|.
name|withTimeout
argument_list|(
name|pingRetryTimeout
argument_list|)
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
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
DECL|class|NoLongerMasterException
specifier|private
specifier|static
class|class
name|NoLongerMasterException
extends|extends
name|ElasticSearchIllegalStateException
block|{
annotation|@
name|Override
DECL|method|fillInStackTrace
specifier|public
name|Throwable
name|fillInStackTrace
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|class|MasterPingRequestHandler
specifier|private
class|class
name|MasterPingRequestHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|MasterPingRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|public
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"discovery/zen/fd/masterPing"
decl_stmt|;
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|MasterPingRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MasterPingRequest
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|MasterPingRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|DiscoveryNodes
name|nodes
init|=
name|nodesProvider
operator|.
name|nodes
argument_list|()
decl_stmt|;
comment|// check if we are really the same master as the one we seemed to be think we are
comment|// this can happen if the master got "kill -9" and then another node started using the same port
if|if
condition|(
operator|!
name|request
operator|.
name|masterNodeId
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
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Got ping as master with id ["
operator|+
name|request
operator|.
name|masterNodeId
operator|+
literal|"], but not master and no id"
argument_list|)
throw|;
block|}
comment|// if we are no longer master, fail...
if|if
condition|(
operator|!
name|nodes
operator|.
name|localNodeMaster
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoLongerMasterException
argument_list|()
throw|;
block|}
comment|// send a response, and note if we are connected to the master or not
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|MasterPingResponseResponse
argument_list|(
name|nodes
operator|.
name|nodeExists
argument_list|(
name|request
operator|.
name|nodeId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|executor
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
DECL|class|MasterPingRequest
specifier|private
specifier|static
class|class
name|MasterPingRequest
implements|implements
name|Streamable
block|{
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|masterNodeId
specifier|private
name|String
name|masterNodeId
decl_stmt|;
DECL|method|MasterPingRequest
specifier|private
name|MasterPingRequest
parameter_list|()
block|{         }
DECL|method|MasterPingRequest
specifier|private
name|MasterPingRequest
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|String
name|masterNodeId
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
name|masterNodeId
operator|=
name|masterNodeId
expr_stmt|;
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
name|nodeId
operator|=
name|in
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|masterNodeId
operator|=
name|in
operator|.
name|readUTF
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
name|out
operator|.
name|writeUTF
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|masterNodeId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MasterPingResponseResponse
specifier|private
specifier|static
class|class
name|MasterPingResponseResponse
implements|implements
name|Streamable
block|{
DECL|field|connectedToMaster
specifier|private
name|boolean
name|connectedToMaster
decl_stmt|;
DECL|method|MasterPingResponseResponse
specifier|private
name|MasterPingResponseResponse
parameter_list|()
block|{         }
DECL|method|MasterPingResponseResponse
specifier|private
name|MasterPingResponseResponse
parameter_list|(
name|boolean
name|connectedToMaster
parameter_list|)
block|{
name|this
operator|.
name|connectedToMaster
operator|=
name|connectedToMaster
expr_stmt|;
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
name|connectedToMaster
operator|=
name|in
operator|.
name|readBoolean
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
name|out
operator|.
name|writeBoolean
argument_list|(
name|connectedToMaster
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

