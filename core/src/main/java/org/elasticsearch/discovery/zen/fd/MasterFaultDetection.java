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
name|ClusterStateUpdateTask
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
name|cluster
operator|.
name|NotMasterException
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

begin_comment
comment|/**  * A fault detection that pings the master periodically to see if its alive.  */
end_comment

begin_class
DECL|class|MasterFaultDetection
specifier|public
class|class
name|MasterFaultDetection
extends|extends
name|FaultDetection
block|{
DECL|field|MASTER_PING_ACTION_NAME
specifier|public
specifier|static
specifier|final
name|String
name|MASTER_PING_ACTION_NAME
init|=
literal|"internal:discovery/zen/fd/master_ping"
decl_stmt|;
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
comment|/** called when pinging the master failed, like a timeout, transport disconnects etc */
DECL|method|onMasterFailure
name|void
name|onMasterFailure
parameter_list|(
name|DiscoveryNode
name|masterNode
parameter_list|,
name|Throwable
name|cause
parameter_list|,
name|String
name|reason
parameter_list|)
function_decl|;
block|}
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
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
argument_list|<>
argument_list|()
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
name|ClusterName
name|clusterName
parameter_list|,
name|ClusterService
name|clusterService
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
name|this
operator|.
name|clusterService
operator|=
name|clusterService
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
name|transportService
operator|.
name|registerRequestHandler
argument_list|(
name|MASTER_PING_ACTION_NAME
argument_list|,
name|MasterPingRequest
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
name|e
argument_list|,
literal|"failed to perform initial connect "
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
comment|// we start pinging slightly later to allow the chosen master to complete it's own master election
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
name|removeHandler
argument_list|(
name|MASTER_PING_ACTION_NAME
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
literal|null
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
literal|null
argument_list|,
literal|"transport disconnected"
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Throwable
name|cause
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
name|onMasterFailure
argument_list|(
name|masterNode
argument_list|,
name|cause
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
specifier|final
name|MasterPingRequest
name|request
init|=
operator|new
name|MasterPingRequest
argument_list|(
name|clusterService
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
argument_list|,
name|clusterName
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
name|masterToPing
argument_list|,
name|MASTER_PING_ACTION_NAME
argument_list|,
name|request
argument_list|,
name|options
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
name|masterToPing
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|exp
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NotMasterException
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[master] pinging a master {} that is no longer a master"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterToPing
argument_list|,
name|exp
argument_list|,
literal|"no longer master"
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|exp
operator|.
name|getCause
argument_list|()
operator|instanceof
name|ThisIsNotTheMasterYouAreLookingForException
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[master] pinging a master {} that is not the master"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterToPing
argument_list|,
name|exp
argument_list|,
literal|"not master"
argument_list|)
expr_stmt|;
return|return;
block|}
elseif|else
if|if
condition|(
name|exp
operator|.
name|getCause
argument_list|()
operator|instanceof
name|NodeDoesNotExistOnMasterException
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"[master] pinging a master {} but we do not exists on it, act as if its master failure"
argument_list|,
name|masterNode
argument_list|)
expr_stmt|;
name|notifyMasterFailure
argument_list|(
name|masterToPing
argument_list|,
name|exp
argument_list|,
literal|"do not exists on master, act as master failure"
argument_list|)
expr_stmt|;
return|return;
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
literal|null
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
name|MASTER_PING_ACTION_NAME
argument_list|,
name|request
argument_list|,
name|options
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
comment|/** Thrown when a ping reaches the wrong node */
DECL|class|ThisIsNotTheMasterYouAreLookingForException
specifier|static
class|class
name|ThisIsNotTheMasterYouAreLookingForException
extends|extends
name|IllegalStateException
block|{
DECL|method|ThisIsNotTheMasterYouAreLookingForException
name|ThisIsNotTheMasterYouAreLookingForException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|ThisIsNotTheMasterYouAreLookingForException
name|ThisIsNotTheMasterYouAreLookingForException
parameter_list|()
block|{         }
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
DECL|class|NodeDoesNotExistOnMasterException
specifier|static
class|class
name|NodeDoesNotExistOnMasterException
extends|extends
name|IllegalStateException
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
implements|implements
name|TransportRequestHandler
argument_list|<
name|MasterPingRequest
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
name|MasterPingRequest
name|request
parameter_list|,
specifier|final
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|DiscoveryNodes
name|nodes
init|=
name|clusterService
operator|.
name|state
argument_list|()
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
name|ThisIsNotTheMasterYouAreLookingForException
argument_list|()
throw|;
block|}
comment|// ping from nodes of version< 1.4.0 will have the clustername set to null
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
name|logger
operator|.
name|trace
argument_list|(
literal|"master fault detection ping request is targeted for a different [{}] cluster then us [{}]"
argument_list|,
name|request
operator|.
name|clusterName
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ThisIsNotTheMasterYouAreLookingForException
argument_list|(
literal|"master fault detection ping request is targeted for a different ["
operator|+
name|request
operator|.
name|clusterName
operator|+
literal|"] cluster then us ["
operator|+
name|clusterName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
comment|// when we are elected as master or when a node joins, we use a cluster state update thread
comment|// to incorporate that information in the cluster state. That cluster state is published
comment|// before we make it available locally. This means that a master ping can come from a node
comment|// that has already processed the new CS but it is not known locally.
comment|// Therefore, if we fail we have to check again under a cluster state thread to make sure
comment|// all processing is finished.
comment|//
if|if
condition|(
operator|!
name|nodes
operator|.
name|localNodeMaster
argument_list|()
operator|||
operator|!
name|nodes
operator|.
name|nodeExists
argument_list|(
name|request
operator|.
name|nodeId
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"checking ping from [{}] under a cluster state thread"
argument_list|,
name|request
operator|.
name|nodeId
argument_list|)
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"master ping (from: ["
operator|+
name|request
operator|.
name|nodeId
operator|+
literal|"])"
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
throws|throws
name|Exception
block|{
comment|// if we are no longer master, fail...
name|DiscoveryNodes
name|nodes
init|=
name|currentState
operator|.
name|nodes
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nodes
operator|.
name|nodeExists
argument_list|(
name|request
operator|.
name|nodeId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NodeDoesNotExistOnMasterException
argument_list|()
throw|;
block|}
return|return
name|currentState
return|;
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
name|onFailure
argument_list|(
name|source
argument_list|,
operator|new
name|NotMasterException
argument_list|(
literal|"local node is not master"
argument_list|)
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
annotation|@
name|Nullable
name|Throwable
name|t
parameter_list|)
block|{
if|if
condition|(
name|t
operator|==
literal|null
condition|)
block|{
name|t
operator|=
operator|new
name|ElasticsearchException
argument_list|(
literal|"unknown error while processing ping"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"error while sending ping response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
try|try
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|MasterPingResponseResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"error while sending ping response"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// send a response, and note if we are connected to the master or not
name|channel
operator|.
name|sendResponse
argument_list|(
operator|new
name|MasterPingResponseResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|MasterPingRequest
specifier|public
specifier|static
class|class
name|MasterPingRequest
extends|extends
name|TransportRequest
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
DECL|field|clusterName
specifier|private
name|ClusterName
name|clusterName
decl_stmt|;
DECL|method|MasterPingRequest
specifier|public
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
parameter_list|,
name|ClusterName
name|clusterName
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
name|this
operator|.
name|clusterName
operator|=
name|clusterName
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
name|masterNodeId
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
name|out
operator|.
name|writeString
argument_list|(
name|masterNodeId
argument_list|)
expr_stmt|;
name|clusterName
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
extends|extends
name|TransportResponse
block|{
DECL|method|MasterPingResponseResponse
specifier|private
name|MasterPingResponseResponse
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

