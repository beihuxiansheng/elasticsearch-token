begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.zen.ping.multicast
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
name|multicast
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
name|*
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
name|network
operator|.
name|NetworkService
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
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|ZenPingException
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
name|net
operator|.
name|DatagramPacket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MulticastSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketTimeoutException
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
name|cluster
operator|.
name|node
operator|.
name|DiscoveryNode
operator|.
name|readNode
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
name|EMPTY_SETTINGS
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
name|EsExecutors
operator|.
name|daemonThreadFactory
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|MulticastZenPing
specifier|public
class|class
name|MulticastZenPing
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|ZenPing
argument_list|>
implements|implements
name|ZenPing
block|{
DECL|field|address
specifier|private
specifier|final
name|String
name|address
decl_stmt|;
DECL|field|port
specifier|private
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|group
specifier|private
specifier|final
name|String
name|group
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|field|ttl
specifier|private
specifier|final
name|int
name|ttl
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
DECL|field|networkService
specifier|private
specifier|final
name|NetworkService
name|networkService
decl_stmt|;
DECL|field|nodesProvider
specifier|private
specifier|volatile
name|DiscoveryNodesProvider
name|nodesProvider
decl_stmt|;
DECL|field|receiver
specifier|private
specifier|volatile
name|Receiver
name|receiver
decl_stmt|;
DECL|field|receiverThread
specifier|private
specifier|volatile
name|Thread
name|receiverThread
decl_stmt|;
DECL|field|multicastSocket
specifier|private
name|MulticastSocket
name|multicastSocket
decl_stmt|;
DECL|field|datagramPacketSend
specifier|private
name|DatagramPacket
name|datagramPacketSend
decl_stmt|;
DECL|field|datagramPacketReceive
specifier|private
name|DatagramPacket
name|datagramPacketReceive
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
DECL|field|sendMutex
specifier|private
specifier|final
name|Object
name|sendMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|receiveMutex
specifier|private
specifier|final
name|Object
name|receiveMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|MulticastZenPing
specifier|public
name|MulticastZenPing
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
argument_list|,
operator|new
name|NetworkService
argument_list|(
name|EMPTY_SETTINGS
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|MulticastZenPing
specifier|public
name|MulticastZenPing
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
name|NetworkService
name|networkService
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
name|this
operator|.
name|networkService
operator|=
name|networkService
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"address"
argument_list|)
expr_stmt|;
name|this
operator|.
name|port
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"port"
argument_list|,
literal|54328
argument_list|)
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"group"
argument_list|,
literal|"224.2.2.4"
argument_list|)
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"buffer_size"
argument_list|,
literal|2048
argument_list|)
expr_stmt|;
name|this
operator|.
name|ttl
operator|=
name|componentSettings
operator|.
name|getAsInt
argument_list|(
literal|"ttl"
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using group [{}], with port [{}], ttl [{}], and address [{}]"
argument_list|,
name|group
argument_list|,
name|port
argument_list|,
name|ttl
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|.
name|registerHandler
argument_list|(
name|MulticastPingResponseRequestHandler
operator|.
name|ACTION
argument_list|,
operator|new
name|MulticastPingResponseRequestHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setNodesProvider
specifier|public
name|void
name|setNodesProvider
parameter_list|(
name|DiscoveryNodesProvider
name|nodesProvider
parameter_list|)
block|{
if|if
condition|(
name|lifecycle
operator|.
name|started
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Can't set nodes provider when started"
argument_list|)
throw|;
block|}
name|this
operator|.
name|nodesProvider
operator|=
name|nodesProvider
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doStart
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
try|try
block|{
name|this
operator|.
name|datagramPacketReceive
operator|=
operator|new
name|DatagramPacket
argument_list|(
operator|new
name|byte
index|[
name|bufferSize
index|]
argument_list|,
name|bufferSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|datagramPacketSend
operator|=
operator|new
name|DatagramPacket
argument_list|(
operator|new
name|byte
index|[
name|bufferSize
index|]
argument_list|,
name|bufferSize
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
name|group
argument_list|)
argument_list|,
name|port
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
name|warn
argument_list|(
literal|"disabled, failed to setup multicast (datagram) discovery : {}"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"disabled, failed to setup multicast (datagram) discovery"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|InetAddress
name|multicastInterface
init|=
literal|null
decl_stmt|;
try|try
block|{
name|MulticastSocket
name|multicastSocket
decl_stmt|;
comment|//            if (NetworkUtils.canBindToMcastAddress()) {
comment|//                try {
comment|//                    multicastSocket = new MulticastSocket(new InetSocketAddress(group, port));
comment|//                } catch (Exception e) {
comment|//                    logger.debug("Failed to create multicast socket by binding to group address, binding to port", e);
comment|//                    multicastSocket = new MulticastSocket(port);
comment|//                }
comment|//            } else {
name|multicastSocket
operator|=
operator|new
name|MulticastSocket
argument_list|(
name|port
argument_list|)
expr_stmt|;
comment|//            }
name|multicastSocket
operator|.
name|setTimeToLive
argument_list|(
name|ttl
argument_list|)
expr_stmt|;
comment|// set the send interface
name|multicastInterface
operator|=
name|networkService
operator|.
name|resolvePublishHostAddress
argument_list|(
name|address
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|setInterface
argument_list|(
name|multicastInterface
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|joinGroup
argument_list|(
name|InetAddress
operator|.
name|getByName
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|setReceiveBufferSize
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|setSendBufferSize
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|setSoTimeout
argument_list|(
literal|60000
argument_list|)
expr_stmt|;
name|this
operator|.
name|multicastSocket
operator|=
name|multicastSocket
expr_stmt|;
name|this
operator|.
name|receiver
operator|=
operator|new
name|Receiver
argument_list|()
expr_stmt|;
name|this
operator|.
name|receiverThread
operator|=
name|daemonThreadFactory
argument_list|(
name|settings
argument_list|,
literal|"discovery#multicast#receiver"
argument_list|)
operator|.
name|newThread
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|this
operator|.
name|receiverThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|datagramPacketReceive
operator|=
literal|null
expr_stmt|;
name|datagramPacketSend
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|multicastSocket
operator|!=
literal|null
condition|)
block|{
name|multicastSocket
operator|.
name|close
argument_list|()
expr_stmt|;
name|multicastSocket
operator|=
literal|null
expr_stmt|;
block|}
name|logger
operator|.
name|warn
argument_list|(
literal|"disabled, failed to setup multicast discovery on {}: {}"
argument_list|,
name|multicastInterface
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"disabled, failed to setup multicast discovery on {}"
argument_list|,
name|e
argument_list|,
name|multicastInterface
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|doStop
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
if|if
condition|(
name|receiver
operator|!=
literal|null
condition|)
block|{
name|receiver
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|receiverThread
operator|!=
literal|null
condition|)
block|{
name|receiverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|multicastSocket
operator|!=
literal|null
condition|)
block|{
name|multicastSocket
operator|.
name|close
argument_list|()
expr_stmt|;
name|multicastSocket
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doClose
specifier|protected
name|void
name|doClose
parameter_list|()
throws|throws
name|ElasticSearchException
block|{     }
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
annotation|@
name|Override
DECL|method|ping
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
name|sendPingRequest
argument_list|(
name|id
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// try and send another ping request halfway through (just in case someone woke up during it...)
comment|// this can be a good trade-off to nailing the initial lookup or un-delivered messages
name|threadPool
operator|.
name|schedule
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|timeout
operator|.
name|millis
argument_list|()
operator|/
literal|2
argument_list|)
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
try|try
block|{
name|sendPingRequest
argument_list|(
name|id
argument_list|,
literal|false
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
name|warn
argument_list|(
literal|"[{}] failed to send second ping request"
argument_list|,
name|e
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
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
DECL|method|sendPingRequest
specifier|private
name|void
name|sendPingRequest
parameter_list|(
name|int
name|id
parameter_list|,
name|boolean
name|remove
parameter_list|)
block|{
if|if
condition|(
name|multicastSocket
operator|==
literal|null
condition|)
block|{
return|return;
block|}
synchronized|synchronized
init|(
name|sendMutex
init|)
block|{
name|CachedStreamOutput
operator|.
name|Entry
name|cachedEntry
init|=
name|CachedStreamOutput
operator|.
name|popEntry
argument_list|()
decl_stmt|;
try|try
block|{
name|HandlesStreamOutput
name|out
init|=
name|cachedEntry
operator|.
name|cachedHandlesBytes
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|clusterName
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|nodesProvider
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|datagramPacketSend
operator|.
name|setData
argument_list|(
name|cachedEntry
operator|.
name|bytes
argument_list|()
operator|.
name|copiedByteArray
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
if|if
condition|(
name|remove
condition|)
block|{
name|receivedResponses
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|ZenPingException
argument_list|(
literal|"Failed to serialize ping request"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|CachedStreamOutput
operator|.
name|pushEntry
argument_list|(
name|cachedEntry
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|multicastSocket
operator|.
name|send
argument_list|(
name|datagramPacketSend
argument_list|)
expr_stmt|;
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
literal|"[{}] sending ping request"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
if|if
condition|(
name|remove
condition|)
block|{
name|receivedResponses
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lifecycle
operator|.
name|stoppedOrClosed
argument_list|()
condition|)
block|{
return|return;
block|}
throw|throw
operator|new
name|ZenPingException
argument_list|(
literal|"Failed to send ping request over multicast on "
operator|+
name|multicastSocket
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|MulticastPingResponseRequestHandler
class|class
name|MulticastPingResponseRequestHandler
extends|extends
name|BaseTransportRequestHandler
argument_list|<
name|MulticastPingResponse
argument_list|>
block|{
DECL|field|ACTION
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"discovery/zen/multicast"
decl_stmt|;
annotation|@
name|Override
DECL|method|newInstance
specifier|public
name|MulticastPingResponse
name|newInstance
parameter_list|()
block|{
return|return
operator|new
name|MulticastPingResponse
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
name|MulticastPingResponse
name|request
parameter_list|,
name|TransportChannel
name|channel
parameter_list|)
throws|throws
name|Exception
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
literal|"[{}] received {}"
argument_list|,
name|request
operator|.
name|id
argument_list|,
name|request
operator|.
name|pingResponse
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
name|get
argument_list|(
name|request
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
name|request
operator|.
name|pingResponse
argument_list|,
name|request
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
name|request
operator|.
name|pingResponse
operator|.
name|target
argument_list|()
argument_list|,
name|request
operator|.
name|pingResponse
argument_list|)
expr_stmt|;
block|}
name|channel
operator|.
name|sendResponse
argument_list|(
name|VoidStreamable
operator|.
name|INSTANCE
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
DECL|class|MulticastPingResponse
specifier|static
class|class
name|MulticastPingResponse
implements|implements
name|Streamable
block|{
DECL|field|id
name|int
name|id
decl_stmt|;
DECL|field|pingResponse
name|PingResponse
name|pingResponse
decl_stmt|;
DECL|method|MulticastPingResponse
name|MulticastPingResponse
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
name|id
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|pingResponse
operator|=
name|PingResponse
operator|.
name|readPingResponse
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
name|out
operator|.
name|writeInt
argument_list|(
name|id
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
DECL|class|Receiver
specifier|private
class|class
name|Receiver
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
while|while
condition|(
name|running
condition|)
block|{
try|try
block|{
name|int
name|id
decl_stmt|;
name|DiscoveryNode
name|requestingNodeX
decl_stmt|;
name|ClusterName
name|clusterName
decl_stmt|;
synchronized|synchronized
init|(
name|receiveMutex
init|)
block|{
try|try
block|{
name|multicastSocket
operator|.
name|receive
argument_list|(
name|datagramPacketReceive
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SocketTimeoutException
name|ignore
parameter_list|)
block|{
continue|continue;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|running
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to receive packet"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
try|try
block|{
name|StreamInput
name|input
init|=
name|CachedStreamInput
operator|.
name|cachedHandles
argument_list|(
operator|new
name|BytesStreamInput
argument_list|(
name|datagramPacketReceive
operator|.
name|getData
argument_list|()
argument_list|,
name|datagramPacketReceive
operator|.
name|getOffset
argument_list|()
argument_list|,
name|datagramPacketReceive
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|id
operator|=
name|input
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|clusterName
operator|=
name|ClusterName
operator|.
name|readClusterName
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|requestingNodeX
operator|=
name|readNode
argument_list|(
name|input
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
name|warn
argument_list|(
literal|"failed to read requesting node from {}"
argument_list|,
name|e
argument_list|,
name|datagramPacketReceive
operator|.
name|getSocketAddress
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|DiscoveryNodes
name|discoveryNodes
init|=
name|nodesProvider
operator|.
name|nodes
argument_list|()
decl_stmt|;
specifier|final
name|DiscoveryNode
name|requestingNode
init|=
name|requestingNodeX
decl_stmt|;
if|if
condition|(
name|requestingNode
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
comment|// that's me, ignore
continue|continue;
block|}
if|if
condition|(
operator|!
name|clusterName
operator|.
name|equals
argument_list|(
name|MulticastZenPing
operator|.
name|this
operator|.
name|clusterName
argument_list|)
condition|)
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
literal|"[{}] received ping_request from [{}], but wrong cluster_name [{}], expected [{}], ignoring"
argument_list|,
name|id
argument_list|,
name|requestingNode
argument_list|,
name|clusterName
argument_list|,
name|MulticastZenPing
operator|.
name|this
operator|.
name|clusterName
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
comment|// don't connect between two client nodes, no need for that...
if|if
condition|(
operator|!
name|discoveryNodes
operator|.
name|localNode
argument_list|()
operator|.
name|shouldConnectTo
argument_list|(
name|requestingNode
argument_list|)
condition|)
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
literal|"[{}] received ping_request from [{}], both are client nodes, ignoring"
argument_list|,
name|id
argument_list|,
name|requestingNode
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
specifier|final
name|MulticastPingResponse
name|multicastPingResponse
init|=
operator|new
name|MulticastPingResponse
argument_list|()
decl_stmt|;
name|multicastPingResponse
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|multicastPingResponse
operator|.
name|pingResponse
operator|=
operator|new
name|PingResponse
argument_list|(
name|discoveryNodes
operator|.
name|localNode
argument_list|()
argument_list|,
name|discoveryNodes
operator|.
name|masterNode
argument_list|()
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
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
literal|"[{}] received ping_request from [{}], sending {}"
argument_list|,
name|id
argument_list|,
name|requestingNode
argument_list|,
name|multicastPingResponse
operator|.
name|pingResponse
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|transportService
operator|.
name|nodeConnected
argument_list|(
name|requestingNode
argument_list|)
condition|)
block|{
comment|// do the connect and send on a thread pool
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
comment|// connect to the node if possible
try|try
block|{
name|transportService
operator|.
name|connectToNode
argument_list|(
name|requestingNode
argument_list|)
expr_stmt|;
name|transportService
operator|.
name|sendRequest
argument_list|(
name|requestingNode
argument_list|,
name|MulticastPingResponseRequestHandler
operator|.
name|ACTION
argument_list|,
name|multicastPingResponse
argument_list|,
operator|new
name|VoidTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
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
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to receive confirmation on sent ping response to [{}]"
argument_list|,
name|exp
argument_list|,
name|requestingNode
argument_list|)
expr_stmt|;
block|}
block|}
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
name|warn
argument_list|(
literal|"failed to connect to requesting node {}"
argument_list|,
name|e
argument_list|,
name|requestingNode
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
name|transportService
operator|.
name|sendRequest
argument_list|(
name|requestingNode
argument_list|,
name|MulticastPingResponseRequestHandler
operator|.
name|ACTION
argument_list|,
name|multicastPingResponse
argument_list|,
operator|new
name|VoidTransportResponseHandler
argument_list|(
name|ThreadPool
operator|.
name|Names
operator|.
name|SAME
argument_list|)
block|{
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
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to receive confirmation on sent ping response to [{}]"
argument_list|,
name|exp
argument_list|,
name|requestingNode
argument_list|)
expr_stmt|;
block|}
block|}
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
name|logger
operator|.
name|warn
argument_list|(
literal|"unexpected exception in multicast receiver"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

