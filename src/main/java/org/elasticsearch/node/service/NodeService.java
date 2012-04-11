begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.node.service
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|service
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
name|ImmutableMap
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
name|stats
operator|.
name|NodeStats
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
name|collect
operator|.
name|MapBuilder
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
name|http
operator|.
name|HttpServer
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
name|IndicesService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|monitor
operator|.
name|MonitorService
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
name|TransportService
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

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|NodeService
specifier|public
class|class
name|NodeService
extends|extends
name|AbstractComponent
block|{
DECL|field|threadPool
specifier|private
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|monitorService
specifier|private
specifier|final
name|MonitorService
name|monitorService
decl_stmt|;
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
DECL|field|transportService
specifier|private
specifier|final
name|TransportService
name|transportService
decl_stmt|;
DECL|field|indicesService
specifier|private
specifier|final
name|IndicesService
name|indicesService
decl_stmt|;
annotation|@
name|Nullable
DECL|field|httpServer
specifier|private
name|HttpServer
name|httpServer
decl_stmt|;
DECL|field|serviceAttributes
specifier|private
specifier|volatile
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|serviceAttributes
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
annotation|@
name|Nullable
DECL|field|hostname
specifier|private
name|String
name|hostname
decl_stmt|;
annotation|@
name|Inject
DECL|method|NodeService
specifier|public
name|NodeService
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|MonitorService
name|monitorService
parameter_list|,
name|Discovery
name|discovery
parameter_list|,
name|ClusterService
name|clusterService
parameter_list|,
name|TransportService
name|transportService
parameter_list|,
name|IndicesService
name|indicesService
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
name|monitorService
operator|=
name|monitorService
expr_stmt|;
name|this
operator|.
name|clusterService
operator|=
name|clusterService
expr_stmt|;
name|this
operator|.
name|transportService
operator|=
name|transportService
expr_stmt|;
name|this
operator|.
name|indicesService
operator|=
name|indicesService
expr_stmt|;
name|discovery
operator|.
name|setNodeService
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|InetAddress
name|address
init|=
name|NetworkUtils
operator|.
name|getLocalAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|hostname
operator|=
name|address
operator|.
name|getHostName
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|setHttpServer
specifier|public
name|void
name|setHttpServer
parameter_list|(
annotation|@
name|Nullable
name|HttpServer
name|httpServer
parameter_list|)
block|{
name|this
operator|.
name|httpServer
operator|=
name|httpServer
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|putNodeAttribute
specifier|public
name|void
name|putNodeAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|putAttribute
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
DECL|method|removeNodeAttribute
specifier|public
name|void
name|removeNodeAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|removeAttribute
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
DECL|method|putAttribute
specifier|public
specifier|synchronized
name|void
name|putAttribute
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|serviceAttributes
operator|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
operator|.
name|putAll
argument_list|(
name|serviceAttributes
argument_list|)
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
DECL|method|removeAttribute
specifier|public
specifier|synchronized
name|void
name|removeAttribute
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|serviceAttributes
operator|=
operator|new
name|MapBuilder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
operator|.
name|putAll
argument_list|(
name|serviceAttributes
argument_list|)
operator|.
name|remove
argument_list|(
name|key
argument_list|)
operator|.
name|immutableMap
argument_list|()
expr_stmt|;
block|}
comment|/**      * Attributes different services in the node can add to be reported as part of the node info (for example).      */
DECL|method|attributes
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceAttributes
return|;
block|}
DECL|method|info
specifier|public
name|NodeInfo
name|info
parameter_list|()
block|{
return|return
operator|new
name|NodeInfo
argument_list|(
name|hostname
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
argument_list|,
name|serviceAttributes
argument_list|,
name|settings
argument_list|,
name|monitorService
operator|.
name|osService
argument_list|()
operator|.
name|info
argument_list|()
argument_list|,
name|monitorService
operator|.
name|processService
argument_list|()
operator|.
name|info
argument_list|()
argument_list|,
name|monitorService
operator|.
name|jvmService
argument_list|()
operator|.
name|info
argument_list|()
argument_list|,
name|threadPool
operator|.
name|info
argument_list|()
argument_list|,
name|monitorService
operator|.
name|networkService
argument_list|()
operator|.
name|info
argument_list|()
argument_list|,
name|transportService
operator|.
name|info
argument_list|()
argument_list|,
name|httpServer
operator|==
literal|null
condition|?
literal|null
else|:
name|httpServer
operator|.
name|info
argument_list|()
argument_list|)
return|;
block|}
DECL|method|info
specifier|public
name|NodeInfo
name|info
parameter_list|(
name|boolean
name|settings
parameter_list|,
name|boolean
name|os
parameter_list|,
name|boolean
name|process
parameter_list|,
name|boolean
name|jvm
parameter_list|,
name|boolean
name|threadPool
parameter_list|,
name|boolean
name|network
parameter_list|,
name|boolean
name|transport
parameter_list|,
name|boolean
name|http
parameter_list|)
block|{
return|return
operator|new
name|NodeInfo
argument_list|(
name|hostname
argument_list|,
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
argument_list|,
name|serviceAttributes
argument_list|,
name|settings
condition|?
name|this
operator|.
name|settings
else|:
literal|null
argument_list|,
name|os
condition|?
name|monitorService
operator|.
name|osService
argument_list|()
operator|.
name|info
argument_list|()
else|:
literal|null
argument_list|,
name|process
condition|?
name|monitorService
operator|.
name|processService
argument_list|()
operator|.
name|info
argument_list|()
else|:
literal|null
argument_list|,
name|jvm
condition|?
name|monitorService
operator|.
name|jvmService
argument_list|()
operator|.
name|info
argument_list|()
else|:
literal|null
argument_list|,
name|threadPool
condition|?
name|this
operator|.
name|threadPool
operator|.
name|info
argument_list|()
else|:
literal|null
argument_list|,
name|network
condition|?
name|monitorService
operator|.
name|networkService
argument_list|()
operator|.
name|info
argument_list|()
else|:
literal|null
argument_list|,
name|transport
condition|?
name|transportService
operator|.
name|info
argument_list|()
else|:
literal|null
argument_list|,
name|http
condition|?
operator|(
name|httpServer
operator|==
literal|null
condition|?
literal|null
else|:
name|httpServer
operator|.
name|info
argument_list|()
operator|)
else|:
literal|null
argument_list|)
return|;
block|}
DECL|method|stats
specifier|public
name|NodeStats
name|stats
parameter_list|()
block|{
comment|// for indices stats we want to include previous allocated shards stats as well (it will
comment|// only be applied to the sensible ones to use, like refresh/merge/flush/indexing stats)
return|return
operator|new
name|NodeStats
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|hostname
argument_list|,
name|indicesService
operator|.
name|stats
argument_list|(
literal|true
argument_list|)
argument_list|,
name|monitorService
operator|.
name|osService
argument_list|()
operator|.
name|stats
argument_list|()
argument_list|,
name|monitorService
operator|.
name|processService
argument_list|()
operator|.
name|stats
argument_list|()
argument_list|,
name|monitorService
operator|.
name|jvmService
argument_list|()
operator|.
name|stats
argument_list|()
argument_list|,
name|threadPool
operator|.
name|stats
argument_list|()
argument_list|,
name|monitorService
operator|.
name|networkService
argument_list|()
operator|.
name|stats
argument_list|()
argument_list|,
name|monitorService
operator|.
name|fsService
argument_list|()
operator|.
name|stats
argument_list|()
argument_list|,
name|transportService
operator|.
name|stats
argument_list|()
argument_list|,
name|httpServer
operator|==
literal|null
condition|?
literal|null
else|:
name|httpServer
operator|.
name|stats
argument_list|()
argument_list|)
return|;
block|}
DECL|method|stats
specifier|public
name|NodeStats
name|stats
parameter_list|(
name|boolean
name|indices
parameter_list|,
name|boolean
name|os
parameter_list|,
name|boolean
name|process
parameter_list|,
name|boolean
name|jvm
parameter_list|,
name|boolean
name|threadPool
parameter_list|,
name|boolean
name|network
parameter_list|,
name|boolean
name|fs
parameter_list|,
name|boolean
name|transport
parameter_list|,
name|boolean
name|http
parameter_list|)
block|{
comment|// for indices stats we want to include previous allocated shards stats as well (it will
comment|// only be applied to the sensible ones to use, like refresh/merge/flush/indexing stats)
return|return
operator|new
name|NodeStats
argument_list|(
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|nodes
argument_list|()
operator|.
name|localNode
argument_list|()
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|hostname
argument_list|,
name|indices
condition|?
name|indicesService
operator|.
name|stats
argument_list|(
literal|true
argument_list|)
else|:
literal|null
argument_list|,
name|os
condition|?
name|monitorService
operator|.
name|osService
argument_list|()
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|process
condition|?
name|monitorService
operator|.
name|processService
argument_list|()
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|jvm
condition|?
name|monitorService
operator|.
name|jvmService
argument_list|()
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|threadPool
condition|?
name|this
operator|.
name|threadPool
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|network
condition|?
name|monitorService
operator|.
name|networkService
argument_list|()
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|fs
condition|?
name|monitorService
operator|.
name|fsService
argument_list|()
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|transport
condition|?
name|transportService
operator|.
name|stats
argument_list|()
else|:
literal|null
argument_list|,
name|http
condition|?
operator|(
name|httpServer
operator|==
literal|null
condition|?
literal|null
else|:
name|httpServer
operator|.
name|stats
argument_list|()
operator|)
else|:
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

