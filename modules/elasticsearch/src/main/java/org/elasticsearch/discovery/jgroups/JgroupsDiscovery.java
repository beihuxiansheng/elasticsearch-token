begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.jgroups
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|jgroups
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
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
name|*
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
name|discovery
operator|.
name|Discovery
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
name|DiscoveryException
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
name|InitialStateDiscoveryListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|env
operator|.
name|Environment
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
name|org
operator|.
name|elasticsearch
operator|.
name|util
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
name|util
operator|.
name|io
operator|.
name|HostResolver
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
name|BytesStreamInput
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
name|BytesStreamOutput
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
name|org
operator|.
name|jgroups
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
name|Inet4Address
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Inet6Address
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
name|URL
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|cluster
operator|.
name|ClusterState
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|JgroupsDiscovery
specifier|public
class|class
name|JgroupsDiscovery
extends|extends
name|AbstractLifecycleComponent
argument_list|<
name|Discovery
argument_list|>
implements|implements
name|Discovery
implements|,
name|Receiver
block|{
static|static
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"jgroups.logging.log_factory_class"
argument_list|,
name|JgroupsCustomLogFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|clusterName
specifier|private
specifier|final
name|ClusterName
name|clusterName
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|Channel
name|channel
decl_stmt|;
DECL|field|addressSet
specifier|private
specifier|volatile
name|boolean
name|addressSet
init|=
literal|false
decl_stmt|;
DECL|field|localNode
specifier|private
name|Node
name|localNode
decl_stmt|;
DECL|field|firstMaster
specifier|private
specifier|volatile
name|boolean
name|firstMaster
init|=
literal|false
decl_stmt|;
DECL|field|initialStateSent
specifier|private
specifier|final
name|AtomicBoolean
name|initialStateSent
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
DECL|field|initialStateListeners
specifier|private
specifier|final
name|CopyOnWriteArrayList
argument_list|<
name|InitialStateDiscoveryListener
argument_list|>
name|initialStateListeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|InitialStateDiscoveryListener
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|JgroupsDiscovery
annotation|@
name|Inject
specifier|public
name|JgroupsDiscovery
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|Environment
name|environment
parameter_list|,
name|ClusterName
name|clusterName
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|String
name|config
init|=
name|componentSettings
operator|.
name|get
argument_list|(
literal|"config"
argument_list|,
literal|"udp"
argument_list|)
decl_stmt|;
name|String
name|actualConfig
init|=
name|config
decl_stmt|;
if|if
condition|(
operator|!
name|config
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|)
block|{
name|actualConfig
operator|=
literal|"jgroups/"
operator|+
name|config
operator|+
literal|".xml"
expr_stmt|;
block|}
name|URL
name|configUrl
init|=
name|environment
operator|.
name|resolveConfig
argument_list|(
name|actualConfig
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Using configuration [{}]"
argument_list|,
name|configUrl
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|sysPropsSet
init|=
name|newHashMap
argument_list|()
decl_stmt|;
try|try
block|{
comment|// prepare system properties to configure jgroups based on the settings
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|settings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"discovery.jgroups"
argument_list|)
condition|)
block|{
name|String
name|jgroupsKey
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|substring
argument_list|(
literal|"discovery."
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
name|jgroupsKey
argument_list|)
operator|==
literal|null
condition|)
block|{
name|sysPropsSet
operator|.
name|put
argument_list|(
name|jgroupsKey
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|jgroupsKey
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"jgroups.bind_addr"
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// automatically set the bind address based on ElasticSearch default bindings...
try|try
block|{
name|InetAddress
name|bindAddress
init|=
name|HostResolver
operator|.
name|resultBindHostAddress
argument_list|(
literal|null
argument_list|,
name|settings
argument_list|,
name|HostResolver
operator|.
name|LOCAL_IP
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|bindAddress
operator|instanceof
name|Inet4Address
operator|&&
name|HostResolver
operator|.
name|isIPv4
argument_list|()
operator|)
operator|||
operator|(
name|bindAddress
operator|instanceof
name|Inet6Address
operator|&&
operator|!
name|HostResolver
operator|.
name|isIPv4
argument_list|()
operator|)
condition|)
block|{
name|sysPropsSet
operator|.
name|put
argument_list|(
literal|"jgroups.bind_addr"
argument_list|,
name|bindAddress
operator|.
name|getHostAddress
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"jgroups.bind_addr"
argument_list|,
name|bindAddress
operator|.
name|getHostAddress
argument_list|()
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
comment|// ignore this
block|}
block|}
name|channel
operator|=
operator|new
name|JChannel
argument_list|(
name|configUrl
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ChannelException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiscoveryException
argument_list|(
literal|"Failed to create jgroups channel with config ["
operator|+
name|configUrl
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
for|for
control|(
name|String
name|keyToRemove
range|:
name|sysPropsSet
operator|.
name|keySet
argument_list|()
control|)
block|{
name|System
operator|.
name|getProperties
argument_list|()
operator|.
name|remove
argument_list|(
name|keyToRemove
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|addListener
annotation|@
name|Override
specifier|public
name|void
name|addListener
parameter_list|(
name|InitialStateDiscoveryListener
name|listener
parameter_list|)
block|{
name|initialStateListeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|removeListener
annotation|@
name|Override
specifier|public
name|void
name|removeListener
parameter_list|(
name|InitialStateDiscoveryListener
name|listener
parameter_list|)
block|{
name|initialStateListeners
operator|.
name|remove
argument_list|(
name|listener
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
block|{
try|try
block|{
name|channel
operator|.
name|connect
argument_list|(
name|clusterName
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|channel
operator|.
name|setReceiver
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Connected to cluster [{}], address [{}]"
argument_list|,
name|channel
operator|.
name|getClusterName
argument_list|()
argument_list|,
name|channel
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|localNode
operator|=
operator|new
name|Node
argument_list|(
name|settings
operator|.
name|get
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"node.data"
argument_list|,
literal|true
argument_list|)
argument_list|,
name|channel
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|transportService
operator|.
name|boundAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isMaster
argument_list|()
condition|)
block|{
name|firstMaster
operator|=
literal|true
expr_stmt|;
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"jgroups-disco-initialconnect(master)"
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
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
block|{
name|Nodes
operator|.
name|Builder
name|builder
init|=
operator|new
name|Nodes
operator|.
name|Builder
argument_list|()
operator|.
name|localNodeId
argument_list|(
name|localNode
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|masterNodeId
argument_list|(
name|localNode
operator|.
name|id
argument_list|()
argument_list|)
comment|// put our local node
operator|.
name|put
argument_list|(
name|localNode
argument_list|)
decl_stmt|;
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|nodes
argument_list|(
name|builder
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|sendInitialStateEventIfNeeded
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|addressSet
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"jgroups-disco-initialconnect"
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
block|{
name|Nodes
operator|.
name|Builder
name|builder
init|=
operator|new
name|Nodes
operator|.
name|Builder
argument_list|()
operator|.
name|localNodeId
argument_list|(
name|localNode
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|localNode
argument_list|)
decl_stmt|;
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|nodes
argument_list|(
name|builder
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
try|try
block|{
name|channel
operator|.
name|send
argument_list|(
operator|new
name|Message
argument_list|(
name|channel
operator|.
name|getView
argument_list|()
operator|.
name|getCreator
argument_list|()
argument_list|,
name|channel
operator|.
name|getAddress
argument_list|()
argument_list|,
name|nodeMessagePayload
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addressSet
operator|=
literal|true
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Sent (initial) node information to master [{}], node [{}]"
argument_list|,
name|channel
operator|.
name|getView
argument_list|()
operator|.
name|getCreator
argument_list|()
argument_list|,
name|localNode
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
literal|"Can't send address to master ["
operator|+
name|channel
operator|.
name|getView
argument_list|()
operator|.
name|getCreator
argument_list|()
operator|+
literal|"] will try again later..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ChannelException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DiscoveryException
argument_list|(
literal|"Can't connect to group ["
operator|+
name|clusterName
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|doStop
annotation|@
name|Override
specifier|protected
name|void
name|doStop
parameter_list|()
throws|throws
name|ElasticSearchException
block|{
name|initialStateSent
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|channel
operator|.
name|isConnected
argument_list|()
condition|)
block|{
name|channel
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
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
if|if
condition|(
name|channel
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|nodeDescription
specifier|public
name|String
name|nodeDescription
parameter_list|()
block|{
return|return
name|channel
operator|.
name|getClusterName
argument_list|()
operator|+
literal|"/"
operator|+
name|channel
operator|.
name|getAddress
argument_list|()
return|;
block|}
DECL|method|firstMaster
annotation|@
name|Override
specifier|public
name|boolean
name|firstMaster
parameter_list|()
block|{
return|return
name|firstMaster
return|;
block|}
DECL|method|publish
annotation|@
name|Override
specifier|public
name|void
name|publish
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isMaster
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalStateException
argument_list|(
literal|"Shouldn't publish state when not master"
argument_list|)
throw|;
block|}
try|try
block|{
name|channel
operator|.
name|send
argument_list|(
operator|new
name|Message
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|ClusterState
operator|.
name|Builder
operator|.
name|toBytes
argument_list|(
name|clusterState
argument_list|)
argument_list|)
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
name|error
argument_list|(
literal|"Failed to send cluster state to nodes"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|receive
annotation|@
name|Override
specifier|public
name|void
name|receive
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
if|if
condition|(
name|msg
operator|.
name|getSrc
argument_list|()
operator|.
name|equals
argument_list|(
name|channel
operator|.
name|getAddress
argument_list|()
argument_list|)
condition|)
block|{
return|return;
comment|// my own message, ignore.
block|}
comment|// message from the master, the cluster state has changed.
if|if
condition|(
name|msg
operator|.
name|getSrc
argument_list|()
operator|.
name|equals
argument_list|(
name|channel
operator|.
name|getView
argument_list|()
operator|.
name|getCreator
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|byte
index|[]
name|buffer
init|=
name|msg
operator|.
name|getBuffer
argument_list|()
decl_stmt|;
specifier|final
name|ClusterState
name|clusterState
init|=
name|ClusterState
operator|.
name|Builder
operator|.
name|fromBytes
argument_list|(
name|buffer
argument_list|,
name|settings
argument_list|,
name|localNode
argument_list|)
decl_stmt|;
comment|// ignore cluster state messages that do not include "me", not in the game yet...
if|if
condition|(
name|clusterState
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"jgroups-disco-receive(from master)"
argument_list|,
operator|new
name|ProcessedClusterStateUpdateTask
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
block|{
return|return
name|clusterState
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clusterStateProcessed
parameter_list|(
name|ClusterState
name|clusterState
parameter_list|)
block|{
name|sendInitialStateEventIfNeeded
argument_list|()
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
name|error
argument_list|(
literal|"Received corrupted cluster state."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
comment|// direct message from a member indicating it has joined the jgroups cluster and provides us its node information
if|if
condition|(
name|isMaster
argument_list|()
condition|)
block|{
try|try
block|{
name|BytesStreamInput
name|is
init|=
operator|new
name|BytesStreamInput
argument_list|(
name|msg
operator|.
name|getBuffer
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Node
name|newNode
init|=
name|Node
operator|.
name|readNode
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
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
literal|"Received node information from [{}], node [{}]"
argument_list|,
name|msg
operator|.
name|getSrc
argument_list|()
argument_list|,
name|newNode
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|transportService
operator|.
name|addressSupported
argument_list|(
name|newNode
operator|.
name|address
argument_list|()
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO, what should we do now? Maybe inform that node that its crap?
name|logger
operator|.
name|warn
argument_list|(
literal|"Received a wrong address type from ["
operator|+
name|msg
operator|.
name|getSrc
argument_list|()
operator|+
literal|"], ignoring... (received_address["
operator|+
name|newNode
operator|.
name|address
argument_list|()
operator|+
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"jgroups-disco-receive(from node["
operator|+
name|newNode
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
block|{
if|if
condition|(
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|nodeExists
argument_list|(
name|newNode
operator|.
name|id
argument_list|()
argument_list|)
condition|)
block|{
comment|// no change, the node already exists in the cluster
name|logger
operator|.
name|warn
argument_list|(
literal|"Received an address [{}] for an existing node [{}]"
argument_list|,
name|newNode
operator|.
name|address
argument_list|()
argument_list|,
name|newNode
argument_list|)
expr_stmt|;
return|return
name|currentState
return|;
block|}
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|nodes
argument_list|(
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|newNode
argument_list|(
name|newNode
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
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
literal|"Can't read address from cluster member ["
operator|+
name|msg
operator|.
name|getSrc
argument_list|()
operator|+
literal|"] message ["
operator|+
name|msg
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|msg
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|logger
operator|.
name|error
argument_list|(
literal|"A message between two members that neither of them is the master is not allowed."
argument_list|)
expr_stmt|;
block|}
DECL|method|isMaster
specifier|private
name|boolean
name|isMaster
parameter_list|()
block|{
return|return
name|channel
operator|.
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|channel
operator|.
name|getView
argument_list|()
operator|.
name|getCreator
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getState
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getState
parameter_list|()
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
DECL|method|setState
annotation|@
name|Override
specifier|public
name|void
name|setState
parameter_list|(
name|byte
index|[]
name|state
parameter_list|)
block|{     }
DECL|method|viewAccepted
annotation|@
name|Override
specifier|public
name|void
name|viewAccepted
parameter_list|(
specifier|final
name|View
name|newView
parameter_list|)
block|{
if|if
condition|(
operator|!
name|addressSet
condition|)
block|{
try|try
block|{
name|channel
operator|.
name|send
argument_list|(
operator|new
name|Message
argument_list|(
name|newView
operator|.
name|getCreator
argument_list|()
argument_list|,
name|channel
operator|.
name|getAddress
argument_list|()
argument_list|,
name|nodeMessagePayload
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Sent (view) node information to master [{}], node [{}]"
argument_list|,
name|newView
operator|.
name|getCreator
argument_list|()
argument_list|,
name|localNode
argument_list|)
expr_stmt|;
name|addressSet
operator|=
literal|true
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
literal|"Can't send address to master ["
operator|+
name|newView
operator|.
name|getCreator
argument_list|()
operator|+
literal|"] will try again later..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|// I am the master
if|if
condition|(
name|channel
operator|.
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
name|newView
operator|.
name|getCreator
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|newMembers
init|=
name|newHashSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Address
name|address
range|:
name|newView
operator|.
name|getMembers
argument_list|()
control|)
block|{
name|newMembers
operator|.
name|add
argument_list|(
name|address
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|clusterService
operator|.
name|submitStateUpdateTask
argument_list|(
literal|"jgroups-disco-view"
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
block|{
name|Nodes
name|newNodes
init|=
name|currentState
operator|.
name|nodes
argument_list|()
operator|.
name|removeDeadMembers
argument_list|(
name|newMembers
argument_list|,
name|newView
operator|.
name|getCreator
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Nodes
operator|.
name|Delta
name|delta
init|=
name|newNodes
operator|.
name|delta
argument_list|(
name|currentState
operator|.
name|nodes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|delta
operator|.
name|added
argument_list|()
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"No new nodes should be created when a new discovery view is accepted"
argument_list|)
expr_stmt|;
block|}
comment|// we want to send a new cluster state any how on view change (that's why its commented)
comment|// for cases where we have client node joining (and it needs the cluster state)
comment|//                    if (!delta.removed()) {
comment|//                        // no nodes were removed, return the current state
comment|//                        return currentState;
comment|//                    }
return|return
name|newClusterStateBuilder
argument_list|()
operator|.
name|state
argument_list|(
name|currentState
argument_list|)
operator|.
name|nodes
argument_list|(
name|newNodes
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// check whether I have been removed due to temporary disconnect
specifier|final
name|String
name|me
init|=
name|channel
operator|.
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|boolean
name|foundMe
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|.
name|id
argument_list|()
operator|.
name|equals
argument_list|(
name|me
argument_list|)
condition|)
block|{
name|foundMe
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|foundMe
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Disconnected from cluster, resending to master [{}], node [{}]"
argument_list|,
name|newView
operator|.
name|getCreator
argument_list|()
argument_list|,
name|localNode
argument_list|)
expr_stmt|;
try|try
block|{
name|channel
operator|.
name|send
argument_list|(
operator|new
name|Message
argument_list|(
name|newView
operator|.
name|getCreator
argument_list|()
argument_list|,
name|channel
operator|.
name|getAddress
argument_list|()
argument_list|,
name|nodeMessagePayload
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addressSet
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|addressSet
operator|=
literal|false
expr_stmt|;
name|logger
operator|.
name|warn
argument_list|(
literal|"Can't send address to master ["
operator|+
name|newView
operator|.
name|getCreator
argument_list|()
operator|+
literal|"] will try again later..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|nodeMessagePayload
specifier|private
name|byte
index|[]
name|nodeMessagePayload
parameter_list|()
throws|throws
name|IOException
block|{
name|BytesStreamOutput
name|os
init|=
name|BytesStreamOutput
operator|.
name|Cached
operator|.
name|cached
argument_list|()
decl_stmt|;
name|localNode
operator|.
name|writeTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
return|return
name|os
operator|.
name|copiedByteArray
argument_list|()
return|;
block|}
DECL|method|sendInitialStateEventIfNeeded
specifier|private
name|void
name|sendInitialStateEventIfNeeded
parameter_list|()
block|{
if|if
condition|(
name|initialStateSent
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
name|InitialStateDiscoveryListener
name|listener
range|:
name|initialStateListeners
control|)
block|{
name|listener
operator|.
name|initialStateProcessed
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|suspect
annotation|@
name|Override
specifier|public
name|void
name|suspect
parameter_list|(
name|Address
name|suspectedMember
parameter_list|)
block|{     }
DECL|method|block
annotation|@
name|Override
specifier|public
name|void
name|block
parameter_list|()
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Blocked..."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

