begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.cluster.node.info
package|package
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
name|Build
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
name|support
operator|.
name|nodes
operator|.
name|BaseNodeResponse
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
name|http
operator|.
name|HttpInfo
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
name|jvm
operator|.
name|JvmInfo
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
name|network
operator|.
name|NetworkInfo
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
name|os
operator|.
name|OsInfo
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
name|process
operator|.
name|ProcessInfo
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
name|ThreadPoolInfo
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
name|TransportInfo
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Node information (static, does not change over time).  */
end_comment

begin_class
DECL|class|NodeInfo
specifier|public
class|class
name|NodeInfo
extends|extends
name|BaseNodeResponse
block|{
annotation|@
name|Nullable
DECL|field|serviceAttributes
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|serviceAttributes
decl_stmt|;
DECL|field|version
specifier|private
name|Version
name|version
decl_stmt|;
DECL|field|build
specifier|private
name|Build
name|build
decl_stmt|;
annotation|@
name|Nullable
DECL|field|settings
specifier|private
name|Settings
name|settings
decl_stmt|;
annotation|@
name|Nullable
DECL|field|os
specifier|private
name|OsInfo
name|os
decl_stmt|;
annotation|@
name|Nullable
DECL|field|process
specifier|private
name|ProcessInfo
name|process
decl_stmt|;
annotation|@
name|Nullable
DECL|field|jvm
specifier|private
name|JvmInfo
name|jvm
decl_stmt|;
annotation|@
name|Nullable
DECL|field|threadPool
specifier|private
name|ThreadPoolInfo
name|threadPool
decl_stmt|;
annotation|@
name|Nullable
DECL|field|network
specifier|private
name|NetworkInfo
name|network
decl_stmt|;
annotation|@
name|Nullable
DECL|field|transport
specifier|private
name|TransportInfo
name|transport
decl_stmt|;
annotation|@
name|Nullable
DECL|field|http
specifier|private
name|HttpInfo
name|http
decl_stmt|;
annotation|@
name|Nullable
DECL|field|plugins
specifier|private
name|PluginsInfo
name|plugins
decl_stmt|;
DECL|method|NodeInfo
name|NodeInfo
parameter_list|()
block|{     }
DECL|method|NodeInfo
specifier|public
name|NodeInfo
parameter_list|(
name|Version
name|version
parameter_list|,
name|Build
name|build
parameter_list|,
name|DiscoveryNode
name|node
parameter_list|,
annotation|@
name|Nullable
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|serviceAttributes
parameter_list|,
annotation|@
name|Nullable
name|Settings
name|settings
parameter_list|,
annotation|@
name|Nullable
name|OsInfo
name|os
parameter_list|,
annotation|@
name|Nullable
name|ProcessInfo
name|process
parameter_list|,
annotation|@
name|Nullable
name|JvmInfo
name|jvm
parameter_list|,
annotation|@
name|Nullable
name|ThreadPoolInfo
name|threadPool
parameter_list|,
annotation|@
name|Nullable
name|NetworkInfo
name|network
parameter_list|,
annotation|@
name|Nullable
name|TransportInfo
name|transport
parameter_list|,
annotation|@
name|Nullable
name|HttpInfo
name|http
parameter_list|,
annotation|@
name|Nullable
name|PluginsInfo
name|plugins
parameter_list|)
block|{
name|super
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|build
operator|=
name|build
expr_stmt|;
name|this
operator|.
name|serviceAttributes
operator|=
name|serviceAttributes
expr_stmt|;
name|this
operator|.
name|settings
operator|=
name|settings
expr_stmt|;
name|this
operator|.
name|os
operator|=
name|os
expr_stmt|;
name|this
operator|.
name|process
operator|=
name|process
expr_stmt|;
name|this
operator|.
name|jvm
operator|=
name|jvm
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|network
operator|=
name|network
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|http
operator|=
name|http
expr_stmt|;
name|this
operator|.
name|plugins
operator|=
name|plugins
expr_stmt|;
block|}
comment|/**      * System's hostname.<code>null</code> in case of UnknownHostException      */
annotation|@
name|Nullable
DECL|method|getHostname
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|getNode
argument_list|()
operator|.
name|getHostName
argument_list|()
return|;
block|}
comment|/**      * The current ES version      */
DECL|method|getVersion
specifier|public
name|Version
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
comment|/**      * The build version of the node.      */
DECL|method|getBuild
specifier|public
name|Build
name|getBuild
parameter_list|()
block|{
return|return
name|this
operator|.
name|build
return|;
block|}
comment|/**      * The service attributes of the node.      */
annotation|@
name|Nullable
DECL|method|getServiceAttributes
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getServiceAttributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|serviceAttributes
return|;
block|}
comment|/**      * The settings of the node.      */
annotation|@
name|Nullable
DECL|method|getSettings
specifier|public
name|Settings
name|getSettings
parameter_list|()
block|{
return|return
name|this
operator|.
name|settings
return|;
block|}
comment|/**      * Operating System level information.      */
annotation|@
name|Nullable
DECL|method|getOs
specifier|public
name|OsInfo
name|getOs
parameter_list|()
block|{
return|return
name|this
operator|.
name|os
return|;
block|}
comment|/**      * Process level information.      */
annotation|@
name|Nullable
DECL|method|getProcess
specifier|public
name|ProcessInfo
name|getProcess
parameter_list|()
block|{
return|return
name|process
return|;
block|}
comment|/**      * JVM level information.      */
annotation|@
name|Nullable
DECL|method|getJvm
specifier|public
name|JvmInfo
name|getJvm
parameter_list|()
block|{
return|return
name|jvm
return|;
block|}
annotation|@
name|Nullable
DECL|method|getThreadPool
specifier|public
name|ThreadPoolInfo
name|getThreadPool
parameter_list|()
block|{
return|return
name|this
operator|.
name|threadPool
return|;
block|}
comment|/**      * Network level information.      */
annotation|@
name|Nullable
DECL|method|getNetwork
specifier|public
name|NetworkInfo
name|getNetwork
parameter_list|()
block|{
return|return
name|network
return|;
block|}
annotation|@
name|Nullable
DECL|method|getTransport
specifier|public
name|TransportInfo
name|getTransport
parameter_list|()
block|{
return|return
name|transport
return|;
block|}
annotation|@
name|Nullable
DECL|method|getHttp
specifier|public
name|HttpInfo
name|getHttp
parameter_list|()
block|{
return|return
name|http
return|;
block|}
annotation|@
name|Nullable
DECL|method|getPlugins
specifier|public
name|PluginsInfo
name|getPlugins
parameter_list|()
block|{
return|return
name|this
operator|.
name|plugins
return|;
block|}
DECL|method|readNodeInfo
specifier|public
specifier|static
name|NodeInfo
name|readNodeInfo
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|NodeInfo
name|nodeInfo
init|=
operator|new
name|NodeInfo
argument_list|()
decl_stmt|;
name|nodeInfo
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|nodeInfo
return|;
block|}
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
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|version
operator|=
name|Version
operator|.
name|readVersion
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|build
operator|=
name|Build
operator|.
name|readBuild
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|,
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|serviceAttributes
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|settings
operator|=
name|Settings
operator|.
name|readSettingsFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|os
operator|=
name|OsInfo
operator|.
name|readOsInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|process
operator|=
name|ProcessInfo
operator|.
name|readProcessInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|jvm
operator|=
name|JvmInfo
operator|.
name|readJvmInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|threadPool
operator|=
name|ThreadPoolInfo
operator|.
name|readThreadPoolInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|network
operator|=
name|NetworkInfo
operator|.
name|readNetworkInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|transport
operator|=
name|TransportInfo
operator|.
name|readTransportInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|http
operator|=
name|HttpInfo
operator|.
name|readHttpInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|plugins
operator|=
name|PluginsInfo
operator|.
name|readPluginsInfo
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|version
operator|.
name|id
argument_list|)
expr_stmt|;
name|Build
operator|.
name|writeBuild
argument_list|(
name|build
argument_list|,
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|getServiceAttributes
argument_list|()
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|serviceAttributes
operator|.
name|size
argument_list|()
argument_list|)
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
name|serviceAttributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|settings
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Settings
operator|.
name|writeSettingsToStream
argument_list|(
name|settings
argument_list|,
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|os
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|process
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|process
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jvm
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|jvm
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|threadPool
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|threadPool
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|network
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|network
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transport
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|transport
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|http
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|http
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|plugins
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|plugins
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

