begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport.local
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|local
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
name|Version
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
name|breaker
operator|.
name|CircuitBreaker
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
name|bytes
operator|.
name|BytesReference
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
name|AbstractLifecycleComponent
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
name|Lifecycle
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
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|NamedWriteableAwareStreamInput
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
name|NamedWriteableRegistry
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
name|transport
operator|.
name|BoundTransportAddress
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
name|transport
operator|.
name|LocalTransportAddress
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
name|transport
operator|.
name|TransportAddress
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
name|AbstractRunnable
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
name|EsExecutors
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
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|breaker
operator|.
name|CircuitBreakerService
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
name|ActionNotFoundTransportException
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
name|NodeNotConnectedException
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
name|RemoteTransportException
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
name|RequestHandlerRegistry
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
name|ResponseHandlerFailureTransportException
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
name|Transport
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
name|TransportSerializationException
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
name|TransportServiceAdapter
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
name|Transports
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
name|support
operator|.
name|TransportStatus
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ThreadFactory
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
name|ThreadPoolExecutor
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
name|TimeUnit
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
name|AtomicLong
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
comment|/**  *  */
end_comment

begin_class
DECL|class|LocalTransport
specifier|public
class|class
name|LocalTransport
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Transport
argument_list|>
implements|implements
name|Transport
block|{
DECL|field|LOCAL_TRANSPORT_THREAD_NAME_PREFIX
specifier|public
specifier|static
specifier|final
name|String
name|LOCAL_TRANSPORT_THREAD_NAME_PREFIX
init|=
literal|"local_transport"
decl_stmt|;
DECL|field|threadPool
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|workers
specifier|private
specifier|final
name|ThreadPoolExecutor
name|workers
decl_stmt|;
DECL|field|transportServiceAdapter
specifier|private
specifier|volatile
name|TransportServiceAdapter
name|transportServiceAdapter
decl_stmt|;
DECL|field|boundAddress
specifier|private
specifier|volatile
name|BoundTransportAddress
name|boundAddress
decl_stmt|;
DECL|field|localAddress
specifier|private
specifier|volatile
name|LocalTransportAddress
name|localAddress
decl_stmt|;
DECL|field|transports
specifier|private
specifier|final
specifier|static
name|ConcurrentMap
argument_list|<
name|LocalTransportAddress
argument_list|,
name|LocalTransport
argument_list|>
name|transports
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|transportAddressIdGenerator
specifier|private
specifier|static
specifier|final
name|AtomicLong
name|transportAddressIdGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|connectedNodes
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|LocalTransport
argument_list|>
name|connectedNodes
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|field|namedWriteableRegistry
specifier|protected
specifier|final
name|NamedWriteableRegistry
name|namedWriteableRegistry
decl_stmt|;
DECL|field|circuitBreakerService
specifier|private
specifier|final
name|CircuitBreakerService
name|circuitBreakerService
decl_stmt|;
DECL|field|TRANSPORT_LOCAL_ADDRESS
specifier|public
specifier|static
specifier|final
name|String
name|TRANSPORT_LOCAL_ADDRESS
init|=
literal|"transport.local.address"
decl_stmt|;
DECL|field|TRANSPORT_LOCAL_WORKERS
specifier|public
specifier|static
specifier|final
name|String
name|TRANSPORT_LOCAL_WORKERS
init|=
literal|"transport.local.workers"
decl_stmt|;
DECL|field|TRANSPORT_LOCAL_QUEUE
specifier|public
specifier|static
specifier|final
name|String
name|TRANSPORT_LOCAL_QUEUE
init|=
literal|"transport.local.queue"
decl_stmt|;
annotation|@
name|Inject
DECL|method|LocalTransport
specifier|public
name|LocalTransport
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|NamedWriteableRegistry
name|namedWriteableRegistry
parameter_list|,
name|CircuitBreakerService
name|circuitBreakerService
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
name|int
name|workerCount
init|=
name|this
operator|.
name|settings
operator|.
name|getAsInt
argument_list|(
name|TRANSPORT_LOCAL_WORKERS
argument_list|,
name|EsExecutors
operator|.
name|boundedNumberOfProcessors
argument_list|(
name|settings
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|queueSize
init|=
name|this
operator|.
name|settings
operator|.
name|getAsInt
argument_list|(
name|TRANSPORT_LOCAL_QUEUE
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"creating [{}] workers, queue_size [{}]"
argument_list|,
name|workerCount
argument_list|,
name|queueSize
argument_list|)
expr_stmt|;
specifier|final
name|ThreadFactory
name|threadFactory
init|=
name|EsExecutors
operator|.
name|daemonThreadFactory
argument_list|(
name|this
operator|.
name|settings
argument_list|,
name|LOCAL_TRANSPORT_THREAD_NAME_PREFIX
argument_list|)
decl_stmt|;
name|this
operator|.
name|workers
operator|=
name|EsExecutors
operator|.
name|newFixed
argument_list|(
name|LOCAL_TRANSPORT_THREAD_NAME_PREFIX
argument_list|,
name|workerCount
argument_list|,
name|queueSize
argument_list|,
name|threadFactory
argument_list|,
name|threadPool
operator|.
name|getThreadContext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|namedWriteableRegistry
operator|=
name|namedWriteableRegistry
expr_stmt|;
name|this
operator|.
name|circuitBreakerService
operator|=
name|circuitBreakerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addressesFromString
specifier|public
name|TransportAddress
index|[]
name|addressesFromString
parameter_list|(
name|String
name|address
parameter_list|,
name|int
name|perAddressLimit
parameter_list|)
block|{
return|return
operator|new
name|TransportAddress
index|[]
block|{
operator|new
name|LocalTransportAddress
argument_list|(
name|address
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|addressSupported
specifier|public
name|boolean
name|addressSupported
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|TransportAddress
argument_list|>
name|address
parameter_list|)
block|{
return|return
name|LocalTransportAddress
operator|.
name|class
operator|.
name|equals
argument_list|(
name|address
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
block|{
name|String
name|address
init|=
name|settings
operator|.
name|get
argument_list|(
name|TRANSPORT_LOCAL_ADDRESS
argument_list|)
decl_stmt|;
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
name|address
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|transportAddressIdGenerator
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|localAddress
operator|=
operator|new
name|LocalTransportAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|LocalTransport
name|previous
init|=
name|transports
operator|.
name|put
argument_list|(
name|localAddress
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"local address ["
operator|+
name|address
operator|+
literal|"] is already bound"
argument_list|)
throw|;
block|}
name|boundAddress
operator|=
operator|new
name|BoundTransportAddress
argument_list|(
operator|new
name|TransportAddress
index|[]
block|{
name|localAddress
block|}
argument_list|,
name|localAddress
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
block|{
name|transports
operator|.
name|remove
argument_list|(
name|localAddress
argument_list|)
expr_stmt|;
comment|// now, go over all the transports connected to me, and raise disconnected event
for|for
control|(
specifier|final
name|LocalTransport
name|targetTransport
range|:
name|transports
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|DiscoveryNode
argument_list|,
name|LocalTransport
argument_list|>
name|entry
range|:
name|targetTransport
operator|.
name|connectedNodes
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
name|this
condition|)
block|{
name|targetTransport
operator|.
name|disconnectFromNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
block|{
name|ThreadPool
operator|.
name|terminate
argument_list|(
name|workers
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|transportServiceAdapter
specifier|public
name|void
name|transportServiceAdapter
parameter_list|(
name|TransportServiceAdapter
name|transportServiceAdapter
parameter_list|)
block|{
name|this
operator|.
name|transportServiceAdapter
operator|=
name|transportServiceAdapter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|boundAddress
specifier|public
name|BoundTransportAddress
name|boundAddress
parameter_list|()
block|{
return|return
name|boundAddress
return|;
block|}
annotation|@
name|Override
DECL|method|profileBoundAddresses
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|BoundTransportAddress
argument_list|>
name|profileBoundAddresses
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyMap
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|nodeConnected
specifier|public
name|boolean
name|nodeConnected
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
return|return
name|connectedNodes
operator|.
name|containsKey
argument_list|(
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|connectToNodeLight
specifier|public
name|void
name|connectToNodeLight
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ConnectTransportException
block|{
name|connectToNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|connectToNode
specifier|public
name|void
name|connectToNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
throws|throws
name|ConnectTransportException
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|connectedNodes
operator|.
name|containsKey
argument_list|(
name|node
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|LocalTransport
name|targetTransport
init|=
name|transports
operator|.
name|get
argument_list|(
name|node
operator|.
name|getAddress
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetTransport
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ConnectTransportException
argument_list|(
name|node
argument_list|,
literal|"Failed to connect"
argument_list|)
throw|;
block|}
name|connectedNodes
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|targetTransport
argument_list|)
expr_stmt|;
name|transportServiceAdapter
operator|.
name|raiseNodeConnected
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|disconnectFromNode
specifier|public
name|void
name|disconnectFromNode
parameter_list|(
name|DiscoveryNode
name|node
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|LocalTransport
name|removed
init|=
name|connectedNodes
operator|.
name|remove
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|!=
literal|null
condition|)
block|{
name|transportServiceAdapter
operator|.
name|raiseNodeDisconnected
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|serverOpen
specifier|public
name|long
name|serverOpen
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|sendRequest
specifier|public
name|void
name|sendRequest
parameter_list|(
specifier|final
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|long
name|requestId
parameter_list|,
specifier|final
name|String
name|action
parameter_list|,
specifier|final
name|TransportRequest
name|request
parameter_list|,
name|TransportRequestOptions
name|options
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransportException
block|{
specifier|final
name|Version
name|version
init|=
name|Version
operator|.
name|smallest
argument_list|(
name|node
operator|.
name|getVersion
argument_list|()
argument_list|,
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
try|try
init|(
name|BytesStreamOutput
name|stream
init|=
operator|new
name|BytesStreamOutput
argument_list|()
init|)
block|{
name|stream
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeLong
argument_list|(
name|requestId
argument_list|)
expr_stmt|;
name|byte
name|status
init|=
literal|0
decl_stmt|;
name|status
operator|=
name|TransportStatus
operator|.
name|setRequest
argument_list|(
name|status
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeByte
argument_list|(
name|status
argument_list|)
expr_stmt|;
comment|// 0 for request, 1 for response.
name|threadPool
operator|.
name|getThreadContext
argument_list|()
operator|.
name|writeTo
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|.
name|writeString
argument_list|(
name|action
argument_list|)
expr_stmt|;
name|request
operator|.
name|writeTo
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|LocalTransport
name|targetTransport
init|=
name|connectedNodes
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|targetTransport
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NodeNotConnectedException
argument_list|(
name|node
argument_list|,
literal|"Node not connected"
argument_list|)
throw|;
block|}
specifier|final
name|byte
index|[]
name|data
init|=
name|BytesReference
operator|.
name|toBytes
argument_list|(
name|stream
operator|.
name|bytes
argument_list|()
argument_list|)
decl_stmt|;
name|transportServiceAdapter
operator|.
name|sent
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|transportServiceAdapter
operator|.
name|onRequestSent
argument_list|(
name|node
argument_list|,
name|requestId
argument_list|,
name|action
argument_list|,
name|request
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|targetTransport
operator|.
name|workers
argument_list|()
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
name|ThreadContext
name|threadContext
init|=
name|targetTransport
operator|.
name|threadPool
operator|.
name|getThreadContext
argument_list|()
decl_stmt|;
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|context
init|=
name|threadContext
operator|.
name|stashContext
argument_list|()
init|)
block|{
name|targetTransport
operator|.
name|messageReceived
argument_list|(
name|data
argument_list|,
name|action
argument_list|,
name|LocalTransport
operator|.
name|this
argument_list|,
name|version
argument_list|,
name|requestId
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|workers
name|ThreadPoolExecutor
name|workers
parameter_list|()
block|{
return|return
name|this
operator|.
name|workers
return|;
block|}
DECL|method|inFlightRequestsBreaker
name|CircuitBreaker
name|inFlightRequestsBreaker
parameter_list|()
block|{
comment|// We always obtain a fresh breaker to reflect changes to the breaker configuration.
return|return
name|circuitBreakerService
operator|.
name|getBreaker
argument_list|(
name|CircuitBreaker
operator|.
name|IN_FLIGHT_REQUESTS
argument_list|)
return|;
block|}
DECL|method|messageReceived
specifier|protected
name|void
name|messageReceived
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|String
name|action
parameter_list|,
name|LocalTransport
name|sourceTransport
parameter_list|,
name|Version
name|version
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|Long
name|sendRequestId
parameter_list|)
block|{
name|Transports
operator|.
name|assertTransportThread
argument_list|()
expr_stmt|;
try|try
block|{
name|transportServiceAdapter
operator|.
name|received
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|StreamInput
name|stream
init|=
name|StreamInput
operator|.
name|wrap
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|long
name|requestId
init|=
name|stream
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|byte
name|status
init|=
name|stream
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|boolean
name|isRequest
init|=
name|TransportStatus
operator|.
name|isRequest
argument_list|(
name|status
argument_list|)
decl_stmt|;
if|if
condition|(
name|isRequest
condition|)
block|{
name|ThreadContext
name|threadContext
init|=
name|threadPool
operator|.
name|getThreadContext
argument_list|()
decl_stmt|;
name|threadContext
operator|.
name|readHeaders
argument_list|(
name|stream
argument_list|)
expr_stmt|;
name|handleRequest
argument_list|(
name|stream
argument_list|,
name|requestId
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|sourceTransport
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|TransportResponseHandler
name|handler
init|=
name|transportServiceAdapter
operator|.
name|onResponseReceived
argument_list|(
name|requestId
argument_list|)
decl_stmt|;
comment|// ignore if its null, the adapter logs it
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|TransportStatus
operator|.
name|isError
argument_list|(
name|status
argument_list|)
condition|)
block|{
name|handleResponseError
argument_list|(
name|stream
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|handleResponse
argument_list|(
name|stream
argument_list|,
name|sourceTransport
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|sendRequestId
operator|!=
literal|null
condition|)
block|{
name|TransportResponseHandler
name|handler
init|=
name|sourceTransport
operator|.
name|transportServiceAdapter
operator|.
name|onResponseReceived
argument_list|(
name|sendRequestId
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
name|RemoteTransportException
name|error
init|=
operator|new
name|RemoteTransportException
argument_list|(
name|nodeName
argument_list|()
argument_list|,
name|localAddress
argument_list|,
name|action
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|sourceTransport
operator|.
name|workers
argument_list|()
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
name|ThreadContext
name|threadContext
init|=
name|sourceTransport
operator|.
name|threadPool
operator|.
name|getThreadContext
argument_list|()
decl_stmt|;
try|try
init|(
name|ThreadContext
operator|.
name|StoredContext
name|ignore
init|=
name|threadContext
operator|.
name|stashContext
argument_list|()
init|)
block|{
name|sourceTransport
operator|.
name|handleException
argument_list|(
name|handler
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to receive message for action [{}]"
argument_list|,
name|e
argument_list|,
name|action
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handleRequest
specifier|private
name|void
name|handleRequest
parameter_list|(
name|StreamInput
name|stream
parameter_list|,
name|long
name|requestId
parameter_list|,
name|int
name|messageLengthBytes
parameter_list|,
name|LocalTransport
name|sourceTransport
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|Exception
block|{
name|stream
operator|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|stream
argument_list|,
name|namedWriteableRegistry
argument_list|)
expr_stmt|;
specifier|final
name|String
name|action
init|=
name|stream
operator|.
name|readString
argument_list|()
decl_stmt|;
specifier|final
name|RequestHandlerRegistry
name|reg
init|=
name|transportServiceAdapter
operator|.
name|getRequestHandler
argument_list|(
name|action
argument_list|)
decl_stmt|;
name|transportServiceAdapter
operator|.
name|onRequestReceived
argument_list|(
name|requestId
argument_list|,
name|action
argument_list|)
expr_stmt|;
if|if
condition|(
name|reg
operator|!=
literal|null
operator|&&
name|reg
operator|.
name|canTripCircuitBreaker
argument_list|()
condition|)
block|{
name|inFlightRequestsBreaker
argument_list|()
operator|.
name|addEstimateBytesAndMaybeBreak
argument_list|(
name|messageLengthBytes
argument_list|,
literal|"<transport_request>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|inFlightRequestsBreaker
argument_list|()
operator|.
name|addWithoutBreaking
argument_list|(
name|messageLengthBytes
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LocalTransportChannel
name|transportChannel
init|=
operator|new
name|LocalTransportChannel
argument_list|(
name|this
argument_list|,
name|transportServiceAdapter
argument_list|,
name|sourceTransport
argument_list|,
name|action
argument_list|,
name|requestId
argument_list|,
name|version
argument_list|,
name|messageLengthBytes
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|reg
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ActionNotFoundTransportException
argument_list|(
literal|"Action ["
operator|+
name|action
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
specifier|final
name|TransportRequest
name|request
init|=
name|reg
operator|.
name|newRequest
argument_list|()
decl_stmt|;
name|request
operator|.
name|remoteAddress
argument_list|(
name|sourceTransport
operator|.
name|boundAddress
operator|.
name|publishAddress
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|readFrom
argument_list|(
name|stream
argument_list|)
expr_stmt|;
if|if
condition|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
operator|.
name|equals
argument_list|(
name|reg
operator|.
name|getExecutor
argument_list|()
argument_list|)
condition|)
block|{
comment|//noinspection unchecked
name|reg
operator|.
name|processMessageReceived
argument_list|(
name|request
argument_list|,
name|transportChannel
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|reg
operator|.
name|getExecutor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
operator|new
name|AbstractRunnable
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doRun
parameter_list|()
throws|throws
name|Exception
block|{
comment|//noinspection unchecked
name|reg
operator|.
name|processMessageReceived
argument_list|(
name|request
argument_list|,
name|transportChannel
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isForceExecution
parameter_list|()
block|{
return|return
name|reg
operator|.
name|isForceExecution
argument_list|()
return|;
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
if|if
condition|(
name|lifecycleState
argument_list|()
operator|==
name|Lifecycle
operator|.
name|State
operator|.
name|STARTED
condition|)
block|{
comment|// we can only send a response transport is started....
try|try
block|{
name|transportChannel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send error message back to client for action [{}]"
argument_list|,
name|e1
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Actual Exception"
argument_list|,
name|e
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
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
try|try
block|{
name|transportChannel
operator|.
name|sendResponse
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e1
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Failed to send error message back to client for action [{}]"
argument_list|,
name|e
argument_list|,
name|action
argument_list|)
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Actual Exception"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|handleResponse
specifier|protected
name|void
name|handleResponse
parameter_list|(
name|StreamInput
name|buffer
parameter_list|,
name|LocalTransport
name|sourceTransport
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
name|buffer
operator|=
operator|new
name|NamedWriteableAwareStreamInput
argument_list|(
name|buffer
argument_list|,
name|namedWriteableRegistry
argument_list|)
expr_stmt|;
specifier|final
name|TransportResponse
name|response
init|=
name|handler
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|response
operator|.
name|remoteAddress
argument_list|(
name|sourceTransport
operator|.
name|boundAddress
operator|.
name|publishAddress
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|response
operator|.
name|readFrom
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|handleException
argument_list|(
name|handler
argument_list|,
operator|new
name|TransportSerializationException
argument_list|(
literal|"Failed to deserialize response of type ["
operator|+
name|response
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|handleParsedResponse
argument_list|(
name|response
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
DECL|method|handleParsedResponse
specifier|protected
name|void
name|handleParsedResponse
parameter_list|(
specifier|final
name|TransportResponse
name|response
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
name|threadPool
operator|.
name|executor
argument_list|(
name|handler
operator|.
name|executor
argument_list|()
argument_list|)
operator|.
name|execute
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
name|handler
operator|.
name|handleResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|handleException
argument_list|(
name|handler
argument_list|,
operator|new
name|ResponseHandlerFailureTransportException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|handleResponseError
specifier|private
name|void
name|handleResponseError
parameter_list|(
name|StreamInput
name|buffer
parameter_list|,
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|)
block|{
name|Throwable
name|error
decl_stmt|;
try|try
block|{
name|error
operator|=
name|buffer
operator|.
name|readThrowable
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|error
operator|=
operator|new
name|TransportSerializationException
argument_list|(
literal|"Failed to deserialize exception response from stream"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|handleException
argument_list|(
name|handler
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
DECL|method|handleException
specifier|private
name|void
name|handleException
parameter_list|(
specifier|final
name|TransportResponseHandler
name|handler
parameter_list|,
name|Throwable
name|error
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|error
operator|instanceof
name|RemoteTransportException
operator|)
condition|)
block|{
name|error
operator|=
operator|new
name|RemoteTransportException
argument_list|(
literal|"None remote transport exception"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RemoteTransportException
name|rtx
init|=
operator|(
name|RemoteTransportException
operator|)
name|error
decl_stmt|;
try|try
block|{
name|handler
operator|.
name|handleException
argument_list|(
name|rtx
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"failed to handle exception response [{}]"
argument_list|,
name|t
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLocalAddresses
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLocalAddresses
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"0.0.0.0"
argument_list|)
return|;
block|}
DECL|method|getVersion
specifier|protected
name|Version
name|getVersion
parameter_list|()
block|{
comment|// for tests
return|return
name|Version
operator|.
name|CURRENT
return|;
block|}
block|}
end_class

end_unit

