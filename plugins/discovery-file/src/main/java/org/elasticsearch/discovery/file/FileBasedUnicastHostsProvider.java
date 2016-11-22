begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.file
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|file
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
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
name|UnicastHostsProvider
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|NoSuchFileException
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
name|concurrent
operator|.
name|ExecutorService
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
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|UnicastZenPing
operator|.
name|DISCOVERY_ZEN_PING_UNICAST_HOSTS_RESOLVE_TIMEOUT
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
name|UnicastZenPing
operator|.
name|resolveDiscoveryNodes
import|;
end_import

begin_comment
comment|/**  * An implementation of {@link UnicastHostsProvider} that reads hosts/ports  * from {@link #UNICAST_HOSTS_FILE}.  *  * Each unicast host/port that is part of the discovery process must be listed on  * a separate line.  If the port is left off an entry, a default port of 9300 is  * assumed.  An example unicast hosts file could read:  *  * 67.81.244.10  * 67.81.244.11:9305  * 67.81.244.15:9400  */
end_comment

begin_class
DECL|class|FileBasedUnicastHostsProvider
class|class
name|FileBasedUnicastHostsProvider
extends|extends
name|AbstractComponent
implements|implements
name|UnicastHostsProvider
block|{
DECL|field|UNICAST_HOSTS_FILE
specifier|static
specifier|final
name|String
name|UNICAST_HOSTS_FILE
init|=
literal|"unicast_hosts.txt"
decl_stmt|;
DECL|field|UNICAST_HOST_PREFIX
specifier|static
specifier|final
name|String
name|UNICAST_HOST_PREFIX
init|=
literal|"#zen_file_unicast_host_"
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|executorService
specifier|private
specifier|final
name|ExecutorService
name|executorService
decl_stmt|;
DECL|field|unicastHostsFilePath
specifier|private
specifier|final
name|Path
name|unicastHostsFilePath
decl_stmt|;
DECL|field|nodeIdGenerator
specifier|private
specifier|final
name|AtomicLong
name|nodeIdGenerator
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
comment|// generates unique ids for the node
DECL|field|resolveTimeout
specifier|private
specifier|final
name|TimeValue
name|resolveTimeout
decl_stmt|;
DECL|method|FileBasedUnicastHostsProvider
name|FileBasedUnicastHostsProvider
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|ExecutorService
name|executorService
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|executorService
operator|=
name|executorService
expr_stmt|;
name|this
operator|.
name|unicastHostsFilePath
operator|=
operator|new
name|Environment
argument_list|(
name|settings
argument_list|)
operator|.
name|configFile
argument_list|()
operator|.
name|resolve
argument_list|(
literal|"discovery-file"
argument_list|)
operator|.
name|resolve
argument_list|(
name|UNICAST_HOSTS_FILE
argument_list|)
expr_stmt|;
name|this
operator|.
name|resolveTimeout
operator|=
name|DISCOVERY_ZEN_PING_UNICAST_HOSTS_RESOLVE_TIMEOUT
operator|.
name|get
argument_list|(
name|settings
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|buildDynamicNodes
specifier|public
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|buildDynamicNodes
parameter_list|()
block|{
name|List
argument_list|<
name|String
argument_list|>
name|hostsList
decl_stmt|;
try|try
init|(
name|Stream
argument_list|<
name|String
argument_list|>
name|lines
init|=
name|Files
operator|.
name|lines
argument_list|(
name|unicastHostsFilePath
argument_list|)
init|)
block|{
name|hostsList
operator|=
name|lines
operator|.
name|filter
argument_list|(
name|line
lambda|->
name|line
operator|.
name|startsWith
argument_list|(
literal|"#"
argument_list|)
operator|==
literal|false
argument_list|)
comment|// lines starting with `#` are comments
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
decl||
name|NoSuchFileException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"[discovery-file] Failed to find unicast hosts file [{}]"
argument_list|,
name|unicastHostsFilePath
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|hostsList
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
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
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"[discovery-file] Error reading unicast hosts file [{}]"
argument_list|,
name|unicastHostsFilePath
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|hostsList
operator|=
name|Collections
operator|.
name|emptyList
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|discoNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|discoNodes
operator|.
name|addAll
argument_list|(
name|resolveDiscoveryNodes
argument_list|(
name|executorService
argument_list|,
name|logger
argument_list|,
name|hostsList
argument_list|,
literal|1
argument_list|,
name|transportService
argument_list|,
parameter_list|()
lambda|->
name|UNICAST_HOST_PREFIX
operator|+
name|nodeIdGenerator
operator|.
name|incrementAndGet
argument_list|()
operator|+
literal|"#"
argument_list|,
name|resolveTimeout
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"[discovery-file] Using dynamic discovery nodes {}"
argument_list|,
name|discoNodes
argument_list|)
expr_stmt|;
return|return
name|discoNodes
return|;
block|}
block|}
end_class

end_unit

