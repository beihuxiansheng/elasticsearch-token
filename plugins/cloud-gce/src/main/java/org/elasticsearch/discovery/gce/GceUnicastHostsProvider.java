begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.gce
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|gce
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|services
operator|.
name|compute
operator|.
name|model
operator|.
name|AccessConfig
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|services
operator|.
name|compute
operator|.
name|model
operator|.
name|Instance
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|api
operator|.
name|services
operator|.
name|compute
operator|.
name|model
operator|.
name|NetworkInterface
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
name|cloud
operator|.
name|gce
operator|.
name|GceComputeService
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
name|Strings
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
name|discovery
operator|.
name|zen
operator|.
name|ping
operator|.
name|unicast
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
name|net
operator|.
name|InetAddress
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|gce
operator|.
name|GceComputeService
operator|.
name|Fields
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|GceUnicastHostsProvider
specifier|public
class|class
name|GceUnicastHostsProvider
extends|extends
name|AbstractComponent
implements|implements
name|UnicastHostsProvider
block|{
DECL|class|Status
specifier|static
specifier|final
class|class
name|Status
block|{
DECL|field|TERMINATED
specifier|private
specifier|static
specifier|final
name|String
name|TERMINATED
init|=
literal|"TERMINATED"
decl_stmt|;
block|}
DECL|field|gceComputeService
specifier|private
specifier|final
name|GceComputeService
name|gceComputeService
decl_stmt|;
DECL|field|transportService
specifier|private
name|TransportService
name|transportService
decl_stmt|;
DECL|field|networkService
specifier|private
name|NetworkService
name|networkService
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|project
specifier|private
specifier|final
name|String
name|project
decl_stmt|;
DECL|field|zones
specifier|private
specifier|final
name|String
index|[]
name|zones
decl_stmt|;
DECL|field|tags
specifier|private
specifier|final
name|String
index|[]
name|tags
decl_stmt|;
DECL|field|refreshInterval
specifier|private
specifier|final
name|TimeValue
name|refreshInterval
decl_stmt|;
DECL|field|lastRefresh
specifier|private
name|long
name|lastRefresh
decl_stmt|;
DECL|field|cachedDiscoNodes
specifier|private
name|List
argument_list|<
name|DiscoveryNode
argument_list|>
name|cachedDiscoNodes
decl_stmt|;
annotation|@
name|Inject
DECL|method|GceUnicastHostsProvider
specifier|public
name|GceUnicastHostsProvider
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|GceComputeService
name|gceComputeService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|NetworkService
name|networkService
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|gceComputeService
operator|=
name|gceComputeService
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|networkService
operator|=
name|networkService
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|refreshInterval
operator|=
name|settings
operator|.
name|getAsTime
argument_list|(
name|Fields
operator|.
name|REFRESH
argument_list|,
name|TimeValue
operator|.
name|timeValueSeconds
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|settings
operator|.
name|get
argument_list|(
name|Fields
operator|.
name|PROJECT
argument_list|)
expr_stmt|;
name|this
operator|.
name|zones
operator|=
name|settings
operator|.
name|getAsArray
argument_list|(
name|Fields
operator|.
name|ZONE
argument_list|)
expr_stmt|;
comment|// Check that we have all needed properties
name|checkProperty
argument_list|(
name|Fields
operator|.
name|PROJECT
argument_list|,
name|project
argument_list|)
expr_stmt|;
name|checkProperty
argument_list|(
name|Fields
operator|.
name|ZONE
argument_list|,
name|zones
argument_list|)
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|settings
operator|.
name|getAsArray
argument_list|(
name|Fields
operator|.
name|TAGS
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
literal|"using tags {}"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|this
operator|.
name|tags
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * We build the list of Nodes from GCE Management API      * Information can be cached using `plugins.refresh_interval` property if needed.      * Setting `plugins.refresh_interval` to `-1` will cause infinite caching.      * Setting `plugins.refresh_interval` to `0` will disable caching (default).      */
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
if|if
condition|(
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|cachedDiscoNodes
operator|!=
literal|null
operator|&&
operator|(
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|<
literal|0
operator|||
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|lastRefresh
operator|)
operator|<
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|)
condition|)
block|{
if|if
condition|(
name|logger
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|logger
operator|.
name|trace
argument_list|(
literal|"using cache to retrieve node list"
argument_list|)
expr_stmt|;
return|return
name|cachedDiscoNodes
return|;
block|}
name|lastRefresh
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"start building nodes list using GCE API"
argument_list|)
expr_stmt|;
name|cachedDiscoNodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|String
name|ipAddress
init|=
literal|null
decl_stmt|;
try|try
block|{
name|InetAddress
name|inetAddress
init|=
name|networkService
operator|.
name|resolvePublishHostAddress
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|inetAddress
operator|!=
literal|null
condition|)
block|{
name|ipAddress
operator|=
name|inetAddress
operator|.
name|getHostAddress
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// We can't find the publish host address... Hmmm. Too bad :-(
comment|// We won't simply filter it
block|}
try|try
block|{
name|Collection
argument_list|<
name|Instance
argument_list|>
name|instances
init|=
name|gceComputeService
operator|.
name|instances
argument_list|()
decl_stmt|;
if|if
condition|(
name|instances
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"no instance found for project [{}], zones [{}]."
argument_list|,
name|this
operator|.
name|project
argument_list|,
name|this
operator|.
name|zones
argument_list|)
expr_stmt|;
return|return
name|cachedDiscoNodes
return|;
block|}
for|for
control|(
name|Instance
name|instance
range|:
name|instances
control|)
block|{
name|String
name|name
init|=
name|instance
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|type
init|=
name|instance
operator|.
name|getMachineType
argument_list|()
decl_stmt|;
name|String
name|status
init|=
name|instance
operator|.
name|getStatus
argument_list|()
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"gce instance {} with status {} found."
argument_list|,
name|name
argument_list|,
name|status
argument_list|)
expr_stmt|;
comment|// We don't want to connect to TERMINATED status instances
comment|// See https://github.com/elasticsearch/elasticsearch-cloud-gce/issues/3
if|if
condition|(
name|Status
operator|.
name|TERMINATED
operator|.
name|equals
argument_list|(
name|status
argument_list|)
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"node {} is TERMINATED. Ignoring"
argument_list|,
name|name
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// see if we need to filter by tag
name|boolean
name|filterByTag
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|tags
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"start filtering instance {} with tags {}."
argument_list|,
name|name
argument_list|,
name|tags
argument_list|)
expr_stmt|;
if|if
condition|(
name|instance
operator|.
name|getTags
argument_list|()
operator|==
literal|null
operator|||
name|instance
operator|.
name|getTags
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|||
name|instance
operator|.
name|getTags
argument_list|()
operator|.
name|getItems
argument_list|()
operator|==
literal|null
operator|||
name|instance
operator|.
name|getTags
argument_list|()
operator|.
name|getItems
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// If this instance have no tag, we filter it
name|logger
operator|.
name|trace
argument_list|(
literal|"no tags for this instance but we asked for tags. {} won't be part of the cluster."
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|filterByTag
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|// check that all tags listed are there on the instance
name|logger
operator|.
name|trace
argument_list|(
literal|"comparing instance tags {} with tags filter {}."
argument_list|,
name|instance
operator|.
name|getTags
argument_list|()
operator|.
name|getItems
argument_list|()
argument_list|,
name|tags
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|instancetag
range|:
name|instance
operator|.
name|getTags
argument_list|()
operator|.
name|getItems
argument_list|()
control|)
block|{
if|if
condition|(
name|instancetag
operator|.
name|equals
argument_list|(
name|tag
argument_list|)
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
name|filterByTag
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
name|filterByTag
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"filtering out instance {} based tags {}, not part of {}"
argument_list|,
name|name
argument_list|,
name|tags
argument_list|,
name|instance
operator|.
name|getTags
argument_list|()
operator|==
literal|null
operator|||
name|instance
operator|.
name|getTags
argument_list|()
operator|.
name|getItems
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|instance
operator|.
name|getTags
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
else|else
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"instance {} with tags {} is added to discovery"
argument_list|,
name|name
argument_list|,
name|tags
argument_list|)
expr_stmt|;
block|}
name|String
name|ip_public
init|=
literal|null
decl_stmt|;
name|String
name|ip_private
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|NetworkInterface
argument_list|>
name|interfaces
init|=
name|instance
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
for|for
control|(
name|NetworkInterface
name|networkInterface
range|:
name|interfaces
control|)
block|{
if|if
condition|(
name|ip_public
operator|==
literal|null
condition|)
block|{
comment|// Trying to get Public IP Address (For future use)
if|if
condition|(
name|networkInterface
operator|.
name|getAccessConfigs
argument_list|()
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|AccessConfig
name|accessConfig
range|:
name|networkInterface
operator|.
name|getAccessConfigs
argument_list|()
control|)
block|{
if|if
condition|(
name|Strings
operator|.
name|hasText
argument_list|(
name|accessConfig
operator|.
name|getNatIP
argument_list|()
argument_list|)
condition|)
block|{
name|ip_public
operator|=
name|accessConfig
operator|.
name|getNatIP
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
if|if
condition|(
name|ip_private
operator|==
literal|null
condition|)
block|{
name|ip_private
operator|=
name|networkInterface
operator|.
name|getNetworkIP
argument_list|()
expr_stmt|;
block|}
comment|// If we have both public and private, we can stop here
if|if
condition|(
name|ip_private
operator|!=
literal|null
operator|&&
name|ip_public
operator|!=
literal|null
condition|)
break|break;
block|}
try|try
block|{
if|if
condition|(
name|ip_private
operator|.
name|equals
argument_list|(
name|ipAddress
argument_list|)
condition|)
block|{
comment|// We found the current node.
comment|// We can ignore it in the list of DiscoveryNode
name|logger
operator|.
name|trace
argument_list|(
literal|"current node found. Ignoring {} - {}"
argument_list|,
name|name
argument_list|,
name|ip_private
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|address
init|=
name|ip_private
decl_stmt|;
comment|// Test if we have es_port metadata defined here
if|if
condition|(
name|instance
operator|.
name|getMetadata
argument_list|()
operator|!=
literal|null
operator|&&
name|instance
operator|.
name|getMetadata
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"es_port"
argument_list|)
condition|)
block|{
name|Object
name|es_port
init|=
name|instance
operator|.
name|getMetadata
argument_list|()
operator|.
name|get
argument_list|(
literal|"es_port"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"es_port is defined with {}"
argument_list|,
name|es_port
argument_list|)
expr_stmt|;
if|if
condition|(
name|es_port
operator|instanceof
name|String
condition|)
block|{
name|address
operator|=
name|address
operator|.
name|concat
argument_list|(
literal|":"
argument_list|)
operator|.
name|concat
argument_list|(
operator|(
name|String
operator|)
name|es_port
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Ignoring other values
name|logger
operator|.
name|trace
argument_list|(
literal|"es_port is instance of {}. Ignoring..."
argument_list|,
name|es_port
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ip_private is a single IP Address. We need to build a TransportAddress from it
name|TransportAddress
index|[]
name|addresses
init|=
name|transportService
operator|.
name|addressesFromString
argument_list|(
name|address
argument_list|)
decl_stmt|;
comment|// If user has set `es_port` metadata, we don't need to ping all ports
comment|// we only limit to 1 addresses, makes no sense to ping 100 ports
name|logger
operator|.
name|trace
argument_list|(
literal|"adding {}, type {}, address {}, transport_address {}, status {}"
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
name|ip_private
argument_list|,
name|addresses
index|[
literal|0
index|]
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|cachedDiscoNodes
operator|.
name|add
argument_list|(
operator|new
name|DiscoveryNode
argument_list|(
literal|"#cloud-"
operator|+
name|name
operator|+
literal|"-"
operator|+
literal|0
argument_list|,
name|addresses
index|[
literal|0
index|]
argument_list|,
name|version
operator|.
name|minimumCompatibilityVersion
argument_list|()
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
name|logger
operator|.
name|warn
argument_list|(
literal|"failed to add {}, address {}"
argument_list|,
name|e
argument_list|,
name|name
argument_list|,
name|ip_private
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Exception caught during discovery {} : {}"
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|trace
argument_list|(
literal|"Exception caught during discovery"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"{} node(s) added"
argument_list|,
name|cachedDiscoNodes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"using dynamic discovery nodes {}"
argument_list|,
name|cachedDiscoNodes
argument_list|)
expr_stmt|;
return|return
name|cachedDiscoNodes
return|;
block|}
DECL|method|checkProperty
specifier|private
name|void
name|checkProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Strings
operator|.
name|hasText
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{} is not set."
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkProperty
specifier|private
name|void
name|checkProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
index|[]
name|values
parameter_list|)
block|{
if|if
condition|(
name|values
operator|==
literal|null
operator|||
name|values
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"{} is not set."
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

