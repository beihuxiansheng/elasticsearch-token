begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.ping.unicast
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|unicast
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
name|ElasticSearchIllegalArgumentException
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Sets
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
name|jsr166y
operator|.
name|LinkedTransferQueue
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
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|ZenPing
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
name|BaseTransportRequestHandler
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
name|Arrays
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
name|Queue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ConcurrentHashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CountDownLatch
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
name|AtomicInteger
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|common
operator|.
name|settings
operator|.
name|ImmutableSettings
operator|.
name|Builder
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
name|common
operator|.
name|unit
operator|.
name|TimeValue
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
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
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
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|ZenPing
operator|.
name|PingResponse
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|UnicastZenPing
specifier|public
class|class
name|UnicastZenPing
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|ZenPing
argument_list|>
implements|implements
name|ZenPing
block|{
DECL|field|LIMIT_PORTS_COUNT
specifier|public
specifier|static
specifier|final
name|int
name|LIMIT_PORTS_COUNT
init|=
literal|1
decl_stmt|;
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
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|DiscoveryNode
index|[]
name|nodes
decl_stmt|;
DECL|field|nodesProvider
specifier|private
specifier|volatile
name|DiscoveryNodesProvider
name|nodesProvider
decl_stmt|;
DECL|field|pingIdGenerator
specifier|private
specifier|final
name|AtomicInteger
name|pingIdGenerator
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|receivedResponses
specifier|private
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|ConcurrentMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|PingResponse
argument_list|>
argument_list|>
name|receivedResponses
init|=
name|newConcurrentMap
argument_list|()
decl_stmt|;
comment|// a list of temporal responses a node will return for a request (holds requests from other nodes)
DECL|field|temporalResponses
specifier|private
specifier|final
name|Queue
argument_list|<
name|PingResponse
argument_list|>
name|temporalResponses
init|=
operator|new
name|LinkedTransferQueue
argument_list|<
name|PingResponse
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|hostsProviders
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|UnicastHostsProvider
argument_list|>
name|hostsProviders
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|UnicastHostsProvider
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|UnicastZenPing
specifier|public
name|UnicastZenPing
parameter_list|(
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
name|this
argument_list|(
name|EMPTY_SETTINGS
argument_list|,
name|threadPool
argument_list|,
name|transportService
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
block|}
DECL|method|UnicastZenPing
specifier|public
name|UnicastZenPing
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
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|String
index|[]
name|hostArr
init|=
name|componentSettings
operator|.
name|getAsArray
argument_list|(
literal|"hosts"
argument_list|)
decl_stmt|;
comment|// trim the hosts
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hostArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hostArr
index|[
name|i
index|]
operator|=
name|hostArr
index|[
name|i
index|]
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|hosts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|hostArr
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using initial hosts {}"
argument_list|,
name|hosts
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|int
name|idCounter
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|host
range|:
name|hosts
control|)
block|{
try|try
block|{
name|TransportAddress
index|[]
name|addresses
init|=
name|transportService
operator|.
name|addressesFromString
argument_list|(
name|host
argument_list|)
decl_stmt|;
comment|// we only limit to 1 addresses, makes no sense to ping 100 ports
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
operator|(
name|i
operator|<
name|addresses
operator|.
name|length
operator|&&
name|i
operator|<
name|LIMIT_PORTS_COUNT
operator|)
condition|;
name|i
operator|++
control|)
block|{
name|nodes
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"#zen_unicast_"
operator|+
operator|(
operator|++
name|idCounter
operator|)
operator|+
literal|"#"
argument_list|,
name|addresses
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Failed to resolve address for ["
operator|+
name|host
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|nodes
operator|=
name|nodes
operator|.
name|toArray
argument_list|(
operator|new
name|DiscoveryNode
index|[
name|nodes
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|registerHandler
argument_list|(
name|UnicastPingRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|UnicastPingRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|doStart
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
DECL|method|doClose
annotation|@
name|Override
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|transportService
operator|.
name|removeHandler
argument_list|(
name|UnicastPingRequestHandler
operator|.
name|ACTION
argument_list|)
expr_stmt|;
block|}
DECL|method|addHostsProvider
specifier|public
name|void
name|addHostsProvider
parameter_list|(
name|UnicastHostsProvider
name|provider
parameter_list|)
block|{
name|hostsProviders
operator|.
name|add
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
DECL|method|removeHostsProvider
specifier|public
name|void
name|removeHostsProvider
parameter_list|(
name|UnicastHostsProvider
name|provider
parameter_list|)
block|{
name|hostsProviders
operator|.
name|remove
argument_list|(
name|provider
argument_list|)
expr_stmt|;
block|}
DECL|method|setNodesProvider
annotation|@
name|Override
specifier|public
name|void
name|setNodesProvider
parameter_list|(
name|DiscoveryNodesProvider
name|nodesProvider
parameter_list|)
block|{
name|this
operator|.
name|nodesProvider
operator|=
name|nodesProvider
expr_stmt|;
block|}
DECL|method|pingAndWait
specifier|public
name|PingResponse
index|[]
name|pingAndWait
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
specifier|final
name|AtomicReference
argument_list|<
name|PingResponse
index|[]
argument_list|>
name|response
init|=
operator|new
name|AtomicReference
argument_list|<
name|PingResponse
index|[]
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ping
argument_list|(
operator|new
name|PingListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onPing
parameter_list|(
name|PingResponse
index|[]
name|pings
parameter_list|)
block|{
name|response
operator|.
name|set
argument_list|(
name|pings
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|,
name|timeout
argument_list|)
expr_stmt|;
try|try
block|{
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
return|return
name|response
operator|.
name|get
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
DECL|method|ping
annotation|@
name|Override
specifier|public
name|void
name|ping
parameter_list|(
specifier|final
name|PingListener
name|listener
parameter_list|,
specifier|final
name|TimeValue
name|timeout
parameter_list|)
throws|throws
name|ElasticSearchException
block|{
specifier|final
name|int
name|id
init|=
name|pingIdGenerator
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
name|receivedResponses
operator|.
name|put
argument_list|(
name|id
argument_list|,
operator|new
name|ConcurrentHashMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|PingResponse
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToDisconnect1
init|=
name|sendPings
argument_list|(
name|id
argument_list|,
name|timeout
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|threadPool
operator|.
name|schedule
argument_list|(
name|timeout
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|CACHED
argument_list|,
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
specifier|final
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToDisconnect
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|nodesToDisconnect1
argument_list|)
decl_stmt|;
name|nodesToDisconnect
operator|.
name|addAll
argument_list|(
name|sendPings
argument_list|(
name|id
argument_list|,
name|timeout
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|DiscoveryNode
name|node
range|:
name|nodesToDisconnect
control|)
block|{
name|transportService
operator|.
name|disconnectFromNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|ConcurrentMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|PingResponse
argument_list|>
name|responses
init|=
name|receivedResponses
operator|.
name|remove
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|listener
operator|.
name|onPing
argument_list|(
name|responses
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|PingResponse
index|[
name|responses
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|sendPings
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|sendPings
parameter_list|(
specifier|final
name|int
name|id
parameter_list|,
specifier|final
name|TimeValue
name|timeout
parameter_list|,
name|boolean
name|wait
parameter_list|)
block|{
specifier|final
name|UnicastPingRequest
name|pingRequest
init|=
operator|new
name|UnicastPingRequest
argument_list|()
decl_stmt|;
name|pingRequest
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|pingRequest
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|DiscoveryNodes
name|discoNodes
init|=
name|nodesProvider
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|pingRequest
operator|.
name|pingResponse
operator|=
operator|new
name|PingResponse
argument_list|(
name|discoNodes
operator|.
name|localNode
argument_list|()
argument_list|,
name|discoNodes
operator|.
name|masterNode
argument_list|()
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToPing
init|=
name|newArrayList
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
for|for
control|(
name|UnicastHostsProvider
name|provider
range|:
name|hostsProviders
control|)
block|{
name|nodesToPing
operator|.
name|addAll
argument_list|(
name|provider
operator|.
name|buildDynamicNodes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|DiscoveryNode
argument_list|>
name|nodesToDisconnect
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|nodesToPing
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|DiscoveryNode
name|node
range|:
name|nodesToPing
control|)
block|{
comment|// make sure we are connected
name|boolean
name|nodeFoundByAddressX
decl_stmt|;
name|DiscoveryNode
name|nodeToSendX
init|=
name|discoNodes
operator|.
name|findByAddress
argument_list|(
name|node
operator|.
name|address
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeToSendX
operator|!=
literal|null
condition|)
block|{
name|nodeFoundByAddressX
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|nodeToSendX
operator|=
name|node
expr_stmt|;
name|nodeFoundByAddressX
operator|=
literal|true
expr_stmt|;
block|}
specifier|final
name|DiscoveryNode
name|nodeToSend
init|=
name|nodeToSendX
decl_stmt|;
specifier|final
name|boolean
name|nodeFoundByAddress
init|=
name|nodeFoundByAddressX
decl_stmt|;
if|if
condition|(
operator|!
name|transportService
operator|.
name|nodeConnected
argument_list|(
name|nodeToSend
argument_list|)
condition|)
block|{
name|nodesToDisconnect
operator|.
name|add
argument_list|(
name|nodeToSend
argument_list|)
expr_stmt|;
comment|// fork the connection to another thread
name|threadPool
operator|.
name|executor
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|MANAGEMENT
argument_list|)
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
comment|// connect to the node, see if we manage to do it, if not, bail
if|if
condition|(
operator|!
name|nodeFoundByAddress
condition|)
block|{
name|transportService
operator|.
name|connectToNodeLight
argument_list|(
name|nodeToSend
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|transportService
operator|.
name|connectToNode
argument_list|(
name|nodeToSend
argument_list|)
expr_stmt|;
block|}
comment|// we are connected, send the ping request
name|sendPingRequestToNode
argument_list|(
name|id
argument_list|,
name|timeout
argument_list|,
name|pingRequest
argument_list|,
name|latch
argument_list|,
name|node
argument_list|,
name|nodeToSend
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectTransportException
name|e
parameter_list|)
block|{
comment|// can't connect to the node
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] failed to connect to {}"
argument_list|,
name|e
argument_list|,
name|id
argument_list|,
name|nodeToSend
argument_list|)
expr_stmt|;
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sendPingRequestToNode
argument_list|(
name|id
argument_list|,
name|timeout
argument_list|,
name|pingRequest
argument_list|,
name|latch
argument_list|,
name|node
argument_list|,
name|nodeToSend
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|wait
condition|)
block|{
try|try
block|{
name|latch
operator|.
name|await
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|*
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
block|}
return|return
name|nodesToDisconnect
return|;
block|}
DECL|method|sendPingRequestToNode
specifier|private
name|void
name|sendPingRequestToNode
parameter_list|(
specifier|final
name|int
name|id
parameter_list|,
name|TimeValue
name|timeout
parameter_list|,
name|UnicastPingRequest
name|pingRequest
parameter_list|,
specifier|final
name|CountDownLatch
name|latch
parameter_list|,
specifier|final
name|DiscoveryNode
name|node
parameter_list|,
specifier|final
name|DiscoveryNode
name|nodeToSend
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] connecting to {}"
argument_list|,
name|id
argument_list|,
name|nodeToSend
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|nodeToSend
argument_list|,
name|UnicastPingRequestHandler
operator|.
name|ACTION
argument_list|,
name|pingRequest
argument_list|,
name|TransportRequestOptions
operator|.
name|options
argument_list|()
operator|.
name|withTimeout
argument_list|(
call|(
name|long
call|)
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|*
literal|1.25
argument_list|)
argument_list|)
argument_list|,
operator|new
name|BaseTransportResponseHandler
argument_list|<
name|UnicastPingResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|UnicastPingResponse
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|UnicastPingResponse
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
name|UnicastPingResponse
name|response
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"[{}] received response from {}: {}"
argument_list|,
name|id
argument_list|,
name|nodeToSend
argument_list|,
name|Arrays
operator|.
name|toString
argument_list|(
name|response
operator|.
name|pingResponses
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|DiscoveryNodes
name|discoveryNodes
init|=
name|nodesProvider
operator|.
name|nodes
argument_list|()
decl_stmt|;
for|for
control|(
name|PingResponse
name|pingResponse
range|:
name|response
operator|.
name|pingResponses
control|)
block|{
if|if
condition|(
name|pingResponse
operator|.
name|target
argument_list|()
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
name|discoveryNodes
operator|.
name|localNodeId
argument_list|()
argument_list|)
condition|)
block|{
comment|// that's us, ignore
continue|continue;
block|}
if|if
condition|(
operator|!
name|pingResponse
operator|.
name|clusterName
argument_list|()
operator|.
name|equals
argument_list|(
name|clusterName
argument_list|)
condition|)
block|{
comment|// not part of the cluster
name|logger
operator|.
name|debug
argument_list|(
literal|"[{}] filtering out response from {}, not same cluster_name [{}]"
argument_list|,
name|id
argument_list|,
name|pingResponse
operator|.
name|target
argument_list|()
argument_list|,
name|pingResponse
operator|.
name|clusterName
argument_list|()
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|ConcurrentMap
argument_list|<
name|DiscoveryNode
argument_list|,
name|PingResponse
argument_list|>
name|responses
init|=
name|receivedResponses
operator|.
name|get
argument_list|(
name|response
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|responses
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"received ping response {} with no matching id [{}]"
argument_list|,
name|pingResponse
argument_list|,
name|response
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|responses
operator|.
name|put
argument_list|(
name|pingResponse
operator|.
name|target
argument_list|()
argument_list|,
name|pingResponse
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|latch
operator|.
name|countDown
argument_list|()
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
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
if|if
condition|(
name|exp
operator|instanceof
name|ConnectTransportException
condition|)
block|{
comment|// ok, not connected...
name|logger
operator|.
name|trace
argument_list|(
literal|"failed to connect to {}"
argument_list|,
name|exp
argument_list|,
name|nodeToSend
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to send ping to [{}]"
argument_list|,
name|exp
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|handlePingRequest
specifier|private
name|UnicastPingResponse
name|handlePingRequest
parameter_list|(
specifier|final
name|UnicastPingRequest
name|request
parameter_list|)
block|{
name|temporalResponses
operator|.
name|add
argument_list|(
name|request
operator|.
name|pingResponse
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|schedule
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|request
operator|.
name|timeout
operator|.
name|millis
argument_list|()
operator|*
literal|2
argument_list|)
argument_list|,
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|,
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
name|temporalResponses
operator|.
name|remove
argument_list|(
name|request
operator|.
name|pingResponse
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|PingResponse
argument_list|>
name|pingResponses
init|=
name|newArrayList
argument_list|(
name|temporalResponses
argument_list|)
decl_stmt|;
name|DiscoveryNodes
name|discoNodes
init|=
name|nodesProvider
operator|.
name|nodes
argument_list|()
decl_stmt|;
name|pingResponses
operator|.
name|add
argument_list|(
operator|new
name|PingResponse
argument_list|(
name|discoNodes
operator|.
name|localNode
argument_list|()
argument_list|,
name|discoNodes
operator|.
name|masterNode
argument_list|()
argument_list|,
name|clusterName
argument_list|)
argument_list|)
expr_stmt|;
name|UnicastPingResponse
name|unicastPingResponse
init|=
operator|new
name|UnicastPingResponse
argument_list|()
decl_stmt|;
name|unicastPingResponse
operator|.
name|id
operator|=
name|request
operator|.
name|id
expr_stmt|;
name|unicastPingResponse
operator|.
name|pingResponses
operator|=
name|pingResponses
operator|.
name|toArray
argument_list|(
operator|new
name|PingResponse
index|[
name|pingResponses
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
return|return
name|unicastPingResponse
return|;
block|}
DECL|class|UnicastPingRequestHandler
class|class
name|UnicastPingRequestHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|UnicastPingRequest
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"discovery/zen/unicast"
decl_stmt|;
DECL|method|newInstance
annotation|@
name|Override
specifier|public
name|UnicastPingRequest
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|UnicastPingRequest
argument_list|()
return|;
block|}
DECL|method|executor
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
DECL|method|messageReceived
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|UnicastPingRequest
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|sendResponse
argument_list|(
name|handlePingRequest
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|UnicastPingRequest
specifier|static
class|class
name|UnicastPingRequest
implements|implements
name|Streamable
block|{
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|timeout
name|TimeValue
name|timeout
decl_stmt|;
DECL|field|pingResponse
name|PingResponse
name|pingResponse
decl_stmt|;
DECL|method|UnicastPingRequest
name|UnicastPingRequest
parameter_list|()
block|{         }
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
name|id
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|timeout
operator|=
name|readTimeValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|pingResponse
operator|=
name|readPingResponse
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
name|out
operator|.
name|writeInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|timeout
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|pingResponse
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|UnicastPingResponse
specifier|static
class|class
name|UnicastPingResponse
implements|implements
name|Streamable
block|{
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|pingResponses
name|PingResponse
index|[]
name|pingResponses
decl_stmt|;
DECL|method|UnicastPingResponse
name|UnicastPingResponse
parameter_list|()
block|{         }
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
name|id
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|pingResponses
operator|=
operator|new
name|PingResponse
index|[
name|in
operator|.
name|readVInt
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pingResponses
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pingResponses
index|[
name|i
index|]
operator|=
name|readPingResponse
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|writeInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|pingResponses
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|PingResponse
name|pingResponse
range|:
name|pingResponses
control|)
block|{
name|pingResponse
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

