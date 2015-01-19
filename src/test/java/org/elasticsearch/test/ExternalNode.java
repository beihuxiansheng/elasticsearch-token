begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
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
name|base
operator|.
name|Predicate
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
name|Constants
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
name|action
operator|.
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodeInfo
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
name|admin
operator|.
name|cluster
operator|.
name|node
operator|.
name|info
operator|.
name|NodesInfoResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|Client
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|transport
operator|.
name|TransportClient
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
name|Loggers
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
name|discovery
operator|.
name|DiscoveryModule
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
name|TransportModule
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Random
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
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
name|settingsBuilder
import|;
end_import

begin_comment
comment|/**  * Simple helper class to start external nodes to be used within a test cluster  */
end_comment

begin_class
DECL|class|ExternalNode
specifier|final
class|class
name|ExternalNode
implements|implements
name|Closeable
block|{
DECL|field|REQUIRED_SETTINGS
specifier|public
specifier|static
specifier|final
name|Settings
name|REQUIRED_SETTINGS
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"config.ignore_system_properties"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_KEY
argument_list|,
literal|"zen"
argument_list|)
operator|.
name|put
argument_list|(
literal|"node.mode"
argument_list|,
literal|"network"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// we need network mode for this
DECL|field|path
specifier|private
specifier|final
name|Path
name|path
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|settingsSource
specifier|private
specifier|final
name|SettingsSource
name|settingsSource
decl_stmt|;
DECL|field|process
specifier|private
name|Process
name|process
decl_stmt|;
DECL|field|nodeInfo
specifier|private
name|NodeInfo
name|nodeInfo
decl_stmt|;
DECL|field|clusterName
specifier|private
specifier|final
name|String
name|clusterName
decl_stmt|;
DECL|field|client
specifier|private
name|TransportClient
name|client
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
init|=
name|Loggers
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|externalNodeSettings
specifier|private
name|Settings
name|externalNodeSettings
decl_stmt|;
DECL|method|ExternalNode
name|ExternalNode
parameter_list|(
name|Path
name|path
parameter_list|,
name|long
name|seed
parameter_list|,
name|SettingsSource
name|settingsSource
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
literal|null
argument_list|,
name|seed
argument_list|,
name|settingsSource
argument_list|)
expr_stmt|;
block|}
DECL|method|ExternalNode
name|ExternalNode
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|clusterName
parameter_list|,
name|long
name|seed
parameter_list|,
name|SettingsSource
name|settingsSource
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path must be a directory"
argument_list|)
throw|;
block|}
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|clusterName
operator|=
name|clusterName
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
name|this
operator|.
name|settingsSource
operator|=
name|settingsSource
expr_stmt|;
block|}
DECL|method|start
specifier|synchronized
name|ExternalNode
name|start
parameter_list|(
name|Client
name|localNode
parameter_list|,
name|Settings
name|defaultSettings
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|clusterName
parameter_list|,
name|int
name|nodeOrdinal
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|ExternalNode
name|externalNode
init|=
operator|new
name|ExternalNode
argument_list|(
name|path
argument_list|,
name|clusterName
argument_list|,
name|random
operator|.
name|nextLong
argument_list|()
argument_list|,
name|settingsSource
argument_list|)
decl_stmt|;
name|Settings
name|settings
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|defaultSettings
argument_list|)
operator|.
name|put
argument_list|(
name|settingsSource
operator|.
name|node
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|externalNode
operator|.
name|startInternal
argument_list|(
name|localNode
argument_list|,
name|settings
argument_list|,
name|nodeName
argument_list|,
name|clusterName
argument_list|)
expr_stmt|;
return|return
name|externalNode
return|;
block|}
DECL|method|startInternal
specifier|synchronized
name|void
name|startInternal
parameter_list|(
name|Client
name|client
parameter_list|,
name|Settings
name|settings
parameter_list|,
name|String
name|nodeName
parameter_list|,
name|String
name|clusterName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|process
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Already started"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Constants
operator|.
name|WINDOWS
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"bin/elasticsearch"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|add
argument_list|(
literal|"bin/elasticsearch.bat"
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
literal|"-Des.cluster.name="
operator|+
name|clusterName
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"-Des.node.name="
operator|+
name|nodeName
argument_list|)
expr_stmt|;
name|ImmutableSettings
operator|.
name|Builder
name|externaNodeSettingsBuilder
init|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
decl_stmt|;
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
switch|switch
condition|(
name|entry
operator|.
name|getKey
argument_list|()
condition|)
block|{
case|case
literal|"cluster.name"
case|:
case|case
literal|"node.name"
case|:
case|case
literal|"path.home"
case|:
case|case
literal|"node.mode"
case|:
case|case
literal|"node.local"
case|:
case|case
name|TransportModule
operator|.
name|TRANSPORT_TYPE_KEY
case|:
case|case
name|DiscoveryModule
operator|.
name|DISCOVERY_TYPE_KEY
case|:
case|case
name|TransportModule
operator|.
name|TRANSPORT_SERVICE_TYPE_KEY
case|:
case|case
literal|"config.ignore_system_properties"
case|:
continue|continue;
default|default:
name|externaNodeSettingsBuilder
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|externalNodeSettings
operator|=
name|externaNodeSettingsBuilder
operator|.
name|put
argument_list|(
name|REQUIRED_SETTINGS
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|externalNodeSettings
operator|.
name|getAsMap
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"-Des."
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
literal|"-Des.path.home="
operator|+
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
operator|.
name|toAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"-Des.path.conf="
operator|+
name|path
operator|+
literal|"/config"
argument_list|)
expr_stmt|;
name|ProcessBuilder
name|builder
init|=
operator|new
name|ProcessBuilder
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|builder
operator|.
name|directory
argument_list|(
name|path
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|inheritIO
argument_list|()
expr_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"starting external node [{}] with: {}"
argument_list|,
name|nodeName
argument_list|,
name|builder
operator|.
name|command
argument_list|()
argument_list|)
expr_stmt|;
name|process
operator|=
name|builder
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeInfo
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|waitForNode
argument_list|(
name|client
argument_list|,
name|nodeName
argument_list|)
condition|)
block|{
name|nodeInfo
operator|=
name|nodeInfo
argument_list|(
name|client
argument_list|,
name|nodeName
argument_list|)
expr_stmt|;
assert|assert
name|nodeInfo
operator|!=
literal|null
assert|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Node ["
operator|+
name|nodeName
operator|+
literal|"] didn't join the cluster"
argument_list|)
throw|;
block|}
name|success
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|waitForNode
specifier|static
name|boolean
name|waitForNode
parameter_list|(
specifier|final
name|Client
name|client
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|InterruptedException
block|{
return|return
name|ElasticsearchTestCase
operator|.
name|awaitBusy
argument_list|(
operator|new
name|Predicate
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Object
name|input
parameter_list|)
block|{
specifier|final
name|NodesInfoResponse
name|nodeInfos
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|NodeInfo
index|[]
name|nodes
init|=
name|nodeInfos
operator|.
name|getNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeInfo
name|info
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|,
literal|30
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
return|;
block|}
DECL|method|nodeInfo
specifier|static
name|NodeInfo
name|nodeInfo
parameter_list|(
specifier|final
name|Client
name|client
parameter_list|,
specifier|final
name|String
name|nodeName
parameter_list|)
block|{
specifier|final
name|NodesInfoResponse
name|nodeInfos
init|=
name|client
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesInfo
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|NodeInfo
index|[]
name|nodes
init|=
name|nodeInfos
operator|.
name|getNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeInfo
name|info
range|:
name|nodes
control|)
block|{
if|if
condition|(
name|nodeName
operator|.
name|equals
argument_list|(
name|info
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|info
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|getTransportAddress
specifier|synchronized
name|TransportAddress
name|getTransportAddress
parameter_list|()
block|{
if|if
condition|(
name|nodeInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Node has not started yet"
argument_list|)
throw|;
block|}
return|return
name|nodeInfo
operator|.
name|getTransport
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
return|;
block|}
DECL|method|getClient
specifier|synchronized
name|Client
name|getClient
parameter_list|()
block|{
if|if
condition|(
name|nodeInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Node has not started yet"
argument_list|)
throw|;
block|}
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|TransportAddress
name|addr
init|=
name|nodeInfo
operator|.
name|getTransport
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|publishAddress
argument_list|()
decl_stmt|;
comment|// verify that the end node setting will have network enabled.
name|Settings
name|clientSettings
init|=
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
name|externalNodeSettings
argument_list|)
operator|.
name|put
argument_list|(
literal|"client.transport.nodes_sampler_interval"
argument_list|,
literal|"1s"
argument_list|)
operator|.
name|put
argument_list|(
literal|"name"
argument_list|,
literal|"transport_client_"
operator|+
name|nodeInfo
operator|.
name|getNode
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
operator|.
name|put
argument_list|(
name|ClusterName
operator|.
name|SETTING
argument_list|,
name|clusterName
argument_list|)
operator|.
name|put
argument_list|(
literal|"client.transport.sniff"
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|TransportClient
name|client
init|=
operator|new
name|TransportClient
argument_list|(
name|clientSettings
argument_list|)
decl_stmt|;
name|client
operator|.
name|addTransportAddress
argument_list|(
name|addr
argument_list|)
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
block|}
return|return
name|client
return|;
block|}
DECL|method|reset
specifier|synchronized
name|void
name|reset
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|this
operator|.
name|random
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
DECL|method|stop
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|stop
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|stop
specifier|synchronized
name|void
name|stop
parameter_list|(
name|boolean
name|forceKill
parameter_list|)
block|{
if|if
condition|(
name|running
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
name|forceKill
operator|==
literal|false
operator|&&
name|nodeInfo
operator|!=
literal|null
operator|&&
name|random
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
comment|// sometimes shut down gracefully
name|getClient
argument_list|()
operator|.
name|admin
argument_list|()
operator|.
name|cluster
argument_list|()
operator|.
name|prepareNodesShutdown
argument_list|(
name|this
operator|.
name|nodeInfo
operator|.
name|getNode
argument_list|()
operator|.
name|id
argument_list|()
argument_list|)
operator|.
name|setExit
argument_list|(
name|random
operator|.
name|nextBoolean
argument_list|()
argument_list|)
operator|.
name|setDelay
argument_list|(
literal|"0s"
argument_list|)
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|process
operator|.
name|destroy
argument_list|()
expr_stmt|;
try|try
block|{
name|process
operator|.
name|waitFor
argument_list|()
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
name|interrupted
argument_list|()
expr_stmt|;
block|}
name|process
operator|=
literal|null
expr_stmt|;
name|nodeInfo
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
DECL|method|running
specifier|synchronized
name|boolean
name|running
parameter_list|()
block|{
return|return
name|process
operator|!=
literal|null
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
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|getName
specifier|synchronized
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|nodeInfo
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Node has not started yet"
argument_list|)
throw|;
block|}
return|return
name|nodeInfo
operator|.
name|getNode
argument_list|()
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

