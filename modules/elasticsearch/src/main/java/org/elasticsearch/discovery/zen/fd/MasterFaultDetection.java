begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|AbstractComponent
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
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

begin_comment
comment|/**  * A fault detection that pings the master periodically to see if its alive.  *  * @author kimchy (shay.banon)  */
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
DECL|field|connectionListener
specifier|private
specifier|final
name|FDConnectionListener
name|connectionListener
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
literal|false
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
name|logger
operator|.
name|debug
argument_list|(
literal|"Master FD uses ping_interval [{}], ping_timeout [{}], ping_retries [{}]"
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
name|transportService
operator|.
name|addConnectionListener
argument_list|(
name|connectionListener
argument_list|)
expr_stmt|;
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
parameter_list|)
block|{
name|stop
argument_list|()
expr_stmt|;
name|start
argument_list|(
name|masterNode
argument_list|)
expr_stmt|;
block|}
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
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
name|Exception
name|e
parameter_list|)
block|{
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
block|}
comment|// start the ping process
name|threadPool
operator|.
name|schedule
argument_list|(
operator|new
name|SendPingRequest
argument_list|()
argument_list|,
name|pingInterval
argument_list|)
expr_stmt|;
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
block|{
comment|// also will stop the next ping schedule
name|this
operator|.
name|retryCount
operator|=
literal|0
expr_stmt|;
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
argument_list|()
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
literal|"Master [{}] failed on disconnect (with verified connect)"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterNode
argument_list|,
literal|"Failed on disconnect (with verified connect)"
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
literal|"Master [{}] failed on disconnect"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterNode
argument_list|,
literal|"Failed on disconnect"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|notifyDisconnectedFromMaster
specifier|private
name|void
name|notifyDisconnectedFromMaster
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
comment|// we don't stop on disconnection from master, we keep pinging it
block|}
DECL|method|notifyMasterFailure
specifier|private
name|void
name|notifyMasterFailure
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
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
name|stop
argument_list|()
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
DECL|method|onNodeConnected
annotation|@
name|Override
specifier|public
name|void
name|onNodeConnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{         }
DECL|method|onNodeDisconnected
annotation|@
name|Override
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
DECL|class|SendPingRequest
specifier|private
class|class
name|SendPingRequest
implements|implements
name|Runnable
block|{
DECL|method|run
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|masterNode
operator|!=
literal|null
condition|)
block|{
specifier|final
name|DiscoveryNode
name|sentToNode
init|=
name|masterNode
decl_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|masterNode
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
argument_list|)
argument_list|,
name|pingRetryTimeout
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
comment|// reset the counter, we got a good result
name|MasterFaultDetection
operator|.
name|this
operator|.
name|retryCount
operator|=
literal|0
expr_stmt|;
comment|// check if the master node did not get switched on us...
if|if
condition|(
name|sentToNode
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
literal|"Master [{}] does not have us registered with it..."
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyDisconnectedFromMaster
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|threadPool
operator|.
name|schedule
argument_list|(
name|SendPingRequest
operator|.
name|this
argument_list|,
name|pingInterval
argument_list|)
expr_stmt|;
block|}
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
comment|// check if the master node did not get switched on us...
if|if
condition|(
name|sentToNode
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
literal|"Master [{}] failed to ping, retry [{}] out of [{}]"
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
literal|"Master [{}] failed on ping, tried [{}] times, each with [{}] timeout"
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
name|sentToNode
argument_list|,
literal|"Failed on ping, tried ["
operator|+
name|pingRetryCount
operator|+
literal|"] times, each with ["
operator|+
name|pingRetryTimeout
operator|+
literal|"] timeout"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|sendRequest
argument_list|(
name|sentToNode
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
argument_list|)
argument_list|,
name|pingRetryTimeout
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
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
DECL|method|newInstance
annotation|@
name|Override
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
DECL|method|messageReceived
annotation|@
name|Override
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
name|node
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
DECL|field|node
specifier|private
name|DiscoveryNode
name|node
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
name|node
operator|=
name|readNode
argument_list|(
name|in
argument_list|)
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
name|node
operator|.
name|writeTo
argument_list|(
name|out
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
name|connectedToMaster
operator|=
name|in
operator|.
name|readBoolean
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

