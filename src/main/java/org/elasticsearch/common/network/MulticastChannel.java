begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.network
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|network
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
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
name|BytesArray
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
name|logging
operator|.
name|ESLogger
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
name|logging
operator|.
name|ESLoggerFactory
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
name|ImmutableSettings
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
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
comment|/**  * A multicast channel that supports registering for receive events, and sending datagram packets. Allows  * to easily share the same multicast socket if it holds the same config.  */
end_comment

begin_class
DECL|class|MulticastChannel
specifier|public
specifier|abstract
class|class
name|MulticastChannel
implements|implements
name|Closeable
block|{
comment|/**      * Builds a channel based on the provided config, allowing to control if sharing a channel that uses      * the same config is allowed or not.      */
DECL|method|getChannel
specifier|public
specifier|static
name|MulticastChannel
name|getChannel
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|shared
parameter_list|,
name|Config
name|config
parameter_list|,
name|Listener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|shared
condition|)
block|{
return|return
operator|new
name|Plain
argument_list|(
name|listener
argument_list|,
name|name
argument_list|,
name|config
argument_list|)
return|;
block|}
return|return
name|Shared
operator|.
name|getSharedChannel
argument_list|(
name|listener
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**      * Config of multicast channel.      */
DECL|class|Config
specifier|public
specifier|static
specifier|final
class|class
name|Config
block|{
DECL|field|port
specifier|public
specifier|final
name|int
name|port
decl_stmt|;
DECL|field|group
specifier|public
specifier|final
name|String
name|group
decl_stmt|;
DECL|field|bufferSize
specifier|public
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|field|ttl
specifier|public
specifier|final
name|int
name|ttl
decl_stmt|;
DECL|field|multicastInterface
specifier|public
specifier|final
name|InetAddress
name|multicastInterface
decl_stmt|;
DECL|method|Config
specifier|public
name|Config
parameter_list|(
name|int
name|port
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|bufferSize
parameter_list|,
name|int
name|ttl
parameter_list|,
name|InetAddress
name|multicastInterface
parameter_list|)
block|{
name|this
operator|.
name|port
operator|=
name|port
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|this
operator|.
name|ttl
operator|=
name|ttl
expr_stmt|;
name|this
operator|.
name|multicastInterface
operator|=
name|multicastInterface
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|Config
name|config
init|=
operator|(
name|Config
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|bufferSize
operator|!=
name|config
operator|.
name|bufferSize
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|port
operator|!=
name|config
operator|.
name|port
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|ttl
operator|!=
name|config
operator|.
name|ttl
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|?
operator|!
name|group
operator|.
name|equals
argument_list|(
name|config
operator|.
name|group
argument_list|)
else|:
name|config
operator|.
name|group
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|multicastInterface
operator|!=
literal|null
condition|?
operator|!
name|multicastInterface
operator|.
name|equals
argument_list|(
name|config
operator|.
name|multicastInterface
argument_list|)
else|:
name|config
operator|.
name|multicastInterface
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|port
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|group
operator|!=
literal|null
condition|?
name|group
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|bufferSize
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|ttl
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|multicastInterface
operator|!=
literal|null
condition|?
name|multicastInterface
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|/**      * Listener that gets called when data is received on the multicast channel.      */
DECL|interface|Listener
specifier|public
specifier|static
interface|interface
name|Listener
block|{
DECL|method|onMessage
name|void
name|onMessage
parameter_list|(
name|BytesReference
name|data
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
function_decl|;
block|}
comment|/**      * Simple listener that wraps multiple listeners into one.      */
DECL|class|MultiListener
specifier|public
specifier|static
class|class
name|MultiListener
implements|implements
name|Listener
block|{
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
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|remove
specifier|public
name|boolean
name|remove
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
return|return
name|this
operator|.
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|onMessage
specifier|public
name|void
name|onMessage
parameter_list|(
name|BytesReference
name|data
parameter_list|,
name|SocketAddress
name|address
parameter_list|)
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
name|onMessage
argument_list|(
name|data
argument_list|,
name|address
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|field|listener
specifier|protected
specifier|final
name|Listener
name|listener
decl_stmt|;
DECL|field|closed
specifier|private
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|method|MulticastChannel
specifier|protected
name|MulticastChannel
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
block|}
comment|/**      * Send the data over the multicast channel.      */
DECL|method|send
specifier|public
specifier|abstract
name|void
name|send
parameter_list|(
name|BytesReference
name|data
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**      * Close the channel.      */
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|close
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|close
specifier|protected
specifier|abstract
name|void
name|close
parameter_list|(
name|Listener
name|listener
parameter_list|)
function_decl|;
DECL|field|SHARED_CHANNEL_NAME
specifier|public
specifier|static
specifier|final
name|String
name|SHARED_CHANNEL_NAME
init|=
literal|"#shared#"
decl_stmt|;
comment|/**      * A shared channel that keeps a static map of Config -> Shared channels, and closes shared      * channel once their reference count has reached 0. It also handles de-registering relevant      * listener from the shared list of listeners.      */
DECL|class|Shared
specifier|private
specifier|final
specifier|static
class|class
name|Shared
extends|extends
name|MulticastChannel
block|{
DECL|field|sharedChannels
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|Config
argument_list|,
name|Shared
argument_list|>
name|sharedChannels
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|mutex
specifier|private
specifier|static
specifier|final
name|Object
name|mutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
comment|// global mutex so we don't sync on static methods (.class)
DECL|method|getSharedChannel
specifier|static
name|MulticastChannel
name|getSharedChannel
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
name|Shared
name|shared
init|=
name|sharedChannels
operator|.
name|get
argument_list|(
name|config
argument_list|)
decl_stmt|;
if|if
condition|(
name|shared
operator|!=
literal|null
condition|)
block|{
name|shared
operator|.
name|incRef
argument_list|()
expr_stmt|;
operator|(
operator|(
name|MultiListener
operator|)
name|shared
operator|.
name|listener
operator|)
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|MultiListener
name|multiListener
init|=
operator|new
name|MultiListener
argument_list|()
decl_stmt|;
name|multiListener
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|shared
operator|=
operator|new
name|Shared
argument_list|(
name|multiListener
argument_list|,
operator|new
name|Plain
argument_list|(
name|multiListener
argument_list|,
name|SHARED_CHANNEL_NAME
argument_list|,
name|config
argument_list|)
argument_list|)
expr_stmt|;
name|sharedChannels
operator|.
name|put
argument_list|(
name|config
argument_list|,
name|shared
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Delegate
argument_list|(
name|listener
argument_list|,
name|shared
argument_list|)
return|;
block|}
block|}
DECL|method|close
specifier|static
name|void
name|close
parameter_list|(
name|Shared
name|shared
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
synchronized|synchronized
init|(
name|mutex
init|)
block|{
comment|// remove this
name|boolean
name|removed
init|=
operator|(
operator|(
name|MultiListener
operator|)
name|shared
operator|.
name|listener
operator|)
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
decl_stmt|;
assert|assert
name|removed
operator|:
literal|"a listener should be removed"
assert|;
if|if
condition|(
name|shared
operator|.
name|decRef
argument_list|()
operator|==
literal|0
condition|)
block|{
assert|assert
operator|(
operator|(
name|MultiListener
operator|)
name|shared
operator|.
name|listener
operator|)
operator|.
name|listeners
operator|.
name|isEmpty
argument_list|()
assert|;
name|sharedChannels
operator|.
name|remove
argument_list|(
name|shared
operator|.
name|channel
operator|.
name|getConfig
argument_list|()
argument_list|)
expr_stmt|;
name|shared
operator|.
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|channel
specifier|final
name|Plain
name|channel
decl_stmt|;
DECL|field|refCount
specifier|private
name|int
name|refCount
init|=
literal|1
decl_stmt|;
DECL|method|Shared
name|Shared
parameter_list|(
name|MultiListener
name|listener
parameter_list|,
name|Plain
name|channel
parameter_list|)
block|{
name|super
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
DECL|method|incRef
specifier|private
name|void
name|incRef
parameter_list|()
block|{
name|refCount
operator|++
expr_stmt|;
block|}
DECL|method|decRef
specifier|private
name|int
name|decRef
parameter_list|()
block|{
operator|--
name|refCount
expr_stmt|;
assert|assert
name|refCount
operator|>=
literal|0
operator|:
literal|"illegal ref counting, close called multiple times"
assert|;
return|return
name|refCount
return|;
block|}
annotation|@
name|Override
DECL|method|send
specifier|public
name|void
name|send
parameter_list|(
name|BytesReference
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|send
argument_list|(
name|data
argument_list|)
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
assert|assert
literal|false
operator|:
literal|"Shared references should never be closed directly, only via Delegate"
assert|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|close
argument_list|(
name|this
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * A light weight delegate that wraps another channel, mainly to support delegating      * the close method with the provided listener and not holding existing listener.      */
DECL|class|Delegate
specifier|private
specifier|final
specifier|static
class|class
name|Delegate
extends|extends
name|MulticastChannel
block|{
DECL|field|channel
specifier|private
specifier|final
name|MulticastChannel
name|channel
decl_stmt|;
DECL|method|Delegate
name|Delegate
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|MulticastChannel
name|channel
parameter_list|)
block|{
name|super
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|send
specifier|public
name|void
name|send
parameter_list|(
name|BytesReference
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|channel
operator|.
name|send
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|channel
operator|.
name|close
argument_list|(
name|listener
argument_list|)
expr_stmt|;
comment|// we delegate here to the close with our listener, not with the delegate listener
block|}
block|}
comment|/**      * Simple implementation of a channel.      */
DECL|class|Plain
specifier|private
specifier|static
class|class
name|Plain
extends|extends
name|MulticastChannel
block|{
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Config
name|config
decl_stmt|;
DECL|field|multicastSocket
specifier|private
specifier|volatile
name|MulticastSocket
name|multicastSocket
decl_stmt|;
DECL|field|datagramPacketSend
specifier|private
specifier|final
name|DatagramPacket
name|datagramPacketSend
decl_stmt|;
DECL|field|datagramPacketReceive
specifier|private
specifier|final
name|DatagramPacket
name|datagramPacketReceive
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
DECL|field|receiver
specifier|private
specifier|final
name|Receiver
name|receiver
decl_stmt|;
DECL|field|receiverThread
specifier|private
specifier|final
name|Thread
name|receiverThread
decl_stmt|;
DECL|method|Plain
name|Plain
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|String
name|name
parameter_list|,
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|super
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|ESLoggerFactory
operator|.
name|getLogger
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
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
name|config
operator|.
name|bufferSize
index|]
argument_list|,
name|config
operator|.
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
name|config
operator|.
name|bufferSize
index|]
argument_list|,
name|config
operator|.
name|bufferSize
argument_list|,
name|InetAddress
operator|.
name|getByName
argument_list|(
name|config
operator|.
name|group
argument_list|)
argument_list|,
name|config
operator|.
name|port
argument_list|)
expr_stmt|;
name|this
operator|.
name|multicastSocket
operator|=
name|buildMulticastSocket
argument_list|(
name|config
argument_list|)
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
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|build
argument_list|()
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
DECL|method|buildMulticastSocket
specifier|private
name|MulticastSocket
name|buildMulticastSocket
parameter_list|(
name|Config
name|config
parameter_list|)
throws|throws
name|Exception
block|{
name|MulticastSocket
name|multicastSocket
init|=
operator|new
name|MulticastSocket
argument_list|(
name|config
operator|.
name|port
argument_list|)
decl_stmt|;
try|try
block|{
name|multicastSocket
operator|.
name|setTimeToLive
argument_list|(
name|config
operator|.
name|ttl
argument_list|)
expr_stmt|;
comment|// set the send interface
name|multicastSocket
operator|.
name|setInterface
argument_list|(
name|config
operator|.
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
name|config
operator|.
name|group
argument_list|)
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|setReceiveBufferSize
argument_list|(
name|config
operator|.
name|bufferSize
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|setSendBufferSize
argument_list|(
name|config
operator|.
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
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|multicastSocket
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
return|return
name|multicastSocket
return|;
block|}
DECL|method|getConfig
specifier|public
name|Config
name|getConfig
parameter_list|()
block|{
return|return
name|this
operator|.
name|config
return|;
block|}
annotation|@
name|Override
DECL|method|send
specifier|public
name|void
name|send
parameter_list|(
name|BytesReference
name|data
parameter_list|)
throws|throws
name|Exception
block|{
synchronized|synchronized
init|(
name|sendMutex
init|)
block|{
name|datagramPacketSend
operator|.
name|setData
argument_list|(
name|data
operator|.
name|toBytes
argument_list|()
argument_list|)
expr_stmt|;
name|multicastSocket
operator|.
name|send
argument_list|(
name|datagramPacketSend
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|protected
name|void
name|close
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|receiver
operator|.
name|stop
argument_list|()
expr_stmt|;
name|receiverThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
if|if
condition|(
name|multicastSocket
operator|!=
literal|null
condition|)
block|{
name|IOUtils
operator|.
name|closeWhileHandlingException
argument_list|(
name|multicastSocket
argument_list|)
expr_stmt|;
name|multicastSocket
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
name|receiverThread
operator|.
name|join
argument_list|(
literal|10000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
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
if|if
condition|(
name|multicastSocket
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"multicast socket closed while running, restarting..."
argument_list|)
expr_stmt|;
name|multicastSocket
operator|=
name|buildMulticastSocket
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to receive packet, throttling..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
block|}
block|}
continue|continue;
block|}
block|}
if|if
condition|(
name|datagramPacketReceive
operator|.
name|getData
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|listener
operator|.
name|onMessage
argument_list|(
operator|new
name|BytesArray
argument_list|(
name|datagramPacketReceive
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|,
name|datagramPacketReceive
operator|.
name|getSocketAddress
argument_list|()
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
if|if
condition|(
name|running
condition|)
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
block|}
block|}
end_class

end_unit

