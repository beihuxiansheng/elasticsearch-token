begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.discovery
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|discovery
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
import|;
end_import

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
name|common
operator|.
name|SuppressForbidden
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
name|NetworkUtils
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
name|test
operator|.
name|InternalTestCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|SettingsSource
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
name|local
operator|.
name|LocalTransport
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
name|InetAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|ServerSocket
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_class
DECL|class|ClusterDiscoveryConfiguration
specifier|public
class|class
name|ClusterDiscoveryConfiguration
extends|extends
name|SettingsSource
block|{
DECL|field|DEFAULT_NODE_SETTINGS
specifier|static
name|Settings
name|DEFAULT_NODE_SETTINGS
init|=
name|Settings
operator|.
name|settingsBuilder
argument_list|()
operator|.
name|put
argument_list|(
literal|"discovery.type"
argument_list|,
literal|"zen"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|field|IP_ADDR
specifier|private
specifier|static
specifier|final
name|String
name|IP_ADDR
init|=
literal|"127.0.0.1"
decl_stmt|;
DECL|field|numOfNodes
specifier|final
name|int
name|numOfNodes
decl_stmt|;
DECL|field|nodeSettings
specifier|final
name|Settings
name|nodeSettings
decl_stmt|;
DECL|field|transportClientSettings
specifier|final
name|Settings
name|transportClientSettings
decl_stmt|;
DECL|method|ClusterDiscoveryConfiguration
specifier|public
name|ClusterDiscoveryConfiguration
parameter_list|(
name|int
name|numOfNodes
parameter_list|,
name|Settings
name|extraSettings
parameter_list|)
block|{
name|this
operator|.
name|numOfNodes
operator|=
name|numOfNodes
expr_stmt|;
name|this
operator|.
name|nodeSettings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|DEFAULT_NODE_SETTINGS
argument_list|)
operator|.
name|put
argument_list|(
name|extraSettings
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|transportClientSettings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|extraSettings
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|node
specifier|public
name|Settings
name|node
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
return|return
name|nodeSettings
return|;
block|}
annotation|@
name|Override
DECL|method|transportClient
specifier|public
name|Settings
name|transportClient
parameter_list|()
block|{
return|return
name|transportClientSettings
return|;
block|}
DECL|class|UnicastZen
specifier|public
specifier|static
class|class
name|UnicastZen
extends|extends
name|ClusterDiscoveryConfiguration
block|{
comment|// this variable is incremented on each bind attempt and will maintain the next port that should be tried
DECL|field|nextPort
specifier|private
specifier|static
name|int
name|nextPort
init|=
name|calcBasePort
argument_list|()
decl_stmt|;
DECL|field|unicastHostOrdinals
specifier|private
specifier|final
name|int
index|[]
name|unicastHostOrdinals
decl_stmt|;
DECL|field|unicastHostPorts
specifier|private
specifier|final
name|int
index|[]
name|unicastHostPorts
decl_stmt|;
DECL|method|UnicastZen
specifier|public
name|UnicastZen
parameter_list|(
name|int
name|numOfNodes
parameter_list|,
name|Settings
name|extraSettings
parameter_list|)
block|{
name|this
argument_list|(
name|numOfNodes
argument_list|,
name|numOfNodes
argument_list|,
name|extraSettings
argument_list|)
expr_stmt|;
block|}
DECL|method|UnicastZen
specifier|public
name|UnicastZen
parameter_list|(
name|int
name|numOfNodes
parameter_list|,
name|int
name|numOfUnicastHosts
parameter_list|,
name|Settings
name|extraSettings
parameter_list|)
block|{
name|super
argument_list|(
name|numOfNodes
argument_list|,
name|extraSettings
argument_list|)
expr_stmt|;
if|if
condition|(
name|numOfUnicastHosts
operator|==
name|numOfNodes
condition|)
block|{
name|unicastHostOrdinals
operator|=
operator|new
name|int
index|[
name|numOfNodes
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
name|numOfNodes
condition|;
name|i
operator|++
control|)
block|{
name|unicastHostOrdinals
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
block|}
else|else
block|{
name|Set
argument_list|<
name|Integer
argument_list|>
name|ordinals
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|numOfUnicastHosts
argument_list|)
decl_stmt|;
while|while
condition|(
name|ordinals
operator|.
name|size
argument_list|()
operator|!=
name|numOfUnicastHosts
condition|)
block|{
name|ordinals
operator|.
name|add
argument_list|(
name|RandomizedTest
operator|.
name|randomInt
argument_list|(
name|numOfNodes
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|unicastHostOrdinals
operator|=
name|Ints
operator|.
name|toArray
argument_list|(
name|ordinals
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|unicastHostPorts
operator|=
name|unicastHostPorts
argument_list|(
name|numOfNodes
argument_list|)
expr_stmt|;
assert|assert
name|unicastHostOrdinals
operator|.
name|length
operator|<=
name|unicastHostPorts
operator|.
name|length
assert|;
block|}
DECL|method|UnicastZen
specifier|public
name|UnicastZen
parameter_list|(
name|int
name|numOfNodes
parameter_list|,
name|int
index|[]
name|unicastHostOrdinals
parameter_list|)
block|{
name|this
argument_list|(
name|numOfNodes
argument_list|,
name|Settings
operator|.
name|EMPTY
argument_list|,
name|unicastHostOrdinals
argument_list|)
expr_stmt|;
block|}
DECL|method|UnicastZen
specifier|public
name|UnicastZen
parameter_list|(
name|int
name|numOfNodes
parameter_list|,
name|Settings
name|extraSettings
parameter_list|,
name|int
index|[]
name|unicastHostOrdinals
parameter_list|)
block|{
name|super
argument_list|(
name|numOfNodes
argument_list|,
name|extraSettings
argument_list|)
expr_stmt|;
name|this
operator|.
name|unicastHostOrdinals
operator|=
name|unicastHostOrdinals
expr_stmt|;
name|this
operator|.
name|unicastHostPorts
operator|=
name|unicastHostPorts
argument_list|(
name|numOfNodes
argument_list|)
expr_stmt|;
assert|assert
name|unicastHostOrdinals
operator|.
name|length
operator|<=
name|unicastHostPorts
operator|.
name|length
assert|;
block|}
DECL|method|calcBasePort
specifier|private
specifier|static
name|int
name|calcBasePort
parameter_list|()
block|{
return|return
literal|30000
operator|+
name|InternalTestCluster
operator|.
name|BASE_PORT
return|;
block|}
annotation|@
name|Override
DECL|method|node
specifier|public
name|Settings
name|node
parameter_list|(
name|int
name|nodeOrdinal
parameter_list|)
block|{
name|Settings
operator|.
name|Builder
name|builder
init|=
name|Settings
operator|.
name|builder
argument_list|()
decl_stmt|;
name|String
index|[]
name|unicastHosts
init|=
operator|new
name|String
index|[
name|unicastHostOrdinals
operator|.
name|length
index|]
decl_stmt|;
if|if
condition|(
name|nodeOrdinal
operator|>=
name|unicastHostPorts
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"nodeOrdinal ["
operator|+
name|nodeOrdinal
operator|+
literal|"] is greater than the number unicast ports ["
operator|+
name|unicastHostPorts
operator|.
name|length
operator|+
literal|"]"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// we need to pin the node port& host so we'd know where to point things
name|builder
operator|.
name|put
argument_list|(
literal|"transport.tcp.port"
argument_list|,
name|unicastHostPorts
index|[
name|nodeOrdinal
index|]
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"transport.host"
argument_list|,
name|IP_ADDR
argument_list|)
expr_stmt|;
comment|// only bind on one IF we use v4 here by default
name|builder
operator|.
name|put
argument_list|(
literal|"transport.bind_host"
argument_list|,
name|IP_ADDR
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"transport.publish_host"
argument_list|,
name|IP_ADDR
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
literal|"http.enabled"
argument_list|,
literal|false
argument_list|)
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
name|unicastHostOrdinals
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|unicastHosts
index|[
name|i
index|]
operator|=
name|IP_ADDR
operator|+
literal|":"
operator|+
operator|(
name|unicastHostPorts
index|[
name|unicastHostOrdinals
index|[
name|i
index|]
index|]
operator|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|putArray
argument_list|(
literal|"discovery.zen.ping.unicast.hosts"
argument_list|,
name|unicastHosts
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|put
argument_list|(
name|super
operator|.
name|node
argument_list|(
name|nodeOrdinal
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|SuppressForbidden
argument_list|(
name|reason
operator|=
literal|"we know we pass a IP address"
argument_list|)
DECL|method|unicastHostPorts
specifier|protected
specifier|synchronized
specifier|static
name|int
index|[]
name|unicastHostPorts
parameter_list|(
name|int
name|numHosts
parameter_list|)
block|{
name|int
index|[]
name|unicastHostPorts
init|=
operator|new
name|int
index|[
name|numHosts
index|]
decl_stmt|;
specifier|final
name|int
name|basePort
init|=
name|calcBasePort
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxPort
init|=
name|basePort
operator|+
name|InternalTestCluster
operator|.
name|PORTS_PER_JVM
decl_stmt|;
name|int
name|tries
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|unicastHostPorts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boolean
name|foundPortInRange
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|tries
operator|<
name|InternalTestCluster
operator|.
name|PORTS_PER_JVM
operator|&&
operator|!
name|foundPortInRange
condition|)
block|{
try|try
init|(
name|ServerSocket
name|serverSocket
init|=
operator|new
name|ServerSocket
argument_list|()
init|)
block|{
comment|// Set SO_REUSEADDR as we may bind here and not be able to reuse the address immediately without it.
name|serverSocket
operator|.
name|setReuseAddress
argument_list|(
name|NetworkUtils
operator|.
name|defaultReuseAddress
argument_list|()
argument_list|)
expr_stmt|;
name|serverSocket
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|IP_ADDR
argument_list|,
name|nextPort
argument_list|)
argument_list|)
expr_stmt|;
comment|// bind was a success
name|foundPortInRange
operator|=
literal|true
expr_stmt|;
name|unicastHostPorts
index|[
name|i
index|]
operator|=
name|nextPort
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Do nothing
block|}
name|nextPort
operator|++
expr_stmt|;
if|if
condition|(
name|nextPort
operator|>=
name|maxPort
condition|)
block|{
comment|// Roll back to the beginning of the range and do not go into another JVM's port range
name|nextPort
operator|=
name|basePort
expr_stmt|;
block|}
name|tries
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundPortInRange
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"could not find enough open ports in range ["
operator|+
name|basePort
operator|+
literal|"-"
operator|+
name|maxPort
operator|+
literal|"]. required ["
operator|+
name|unicastHostPorts
operator|.
name|length
operator|+
literal|"] ports"
argument_list|)
throw|;
block|}
block|}
return|return
name|unicastHostPorts
return|;
block|}
block|}
block|}
end_class

end_unit

