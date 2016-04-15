begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.node
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|node
package|;
end_package

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
name|Writeable
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
name|transport
operator|.
name|TransportAddressSerializers
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
name|xcontent
operator|.
name|ToXContent
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
name|xcontent
operator|.
name|XContentBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|node
operator|.
name|Node
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|function
operator|.
name|Predicate
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
name|transport
operator|.
name|TransportAddressSerializers
operator|.
name|addressToStream
import|;
end_import

begin_comment
comment|/**  * A discovery node represents a node that is part of the cluster.  */
end_comment

begin_class
DECL|class|DiscoveryNode
specifier|public
class|class
name|DiscoveryNode
implements|implements
name|Writeable
argument_list|<
name|DiscoveryNode
argument_list|>
implements|,
name|ToXContent
block|{
DECL|method|isLocalNode
specifier|public
specifier|static
name|boolean
name|isLocalNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
if|if
condition|(
name|Node
operator|.
name|NODE_LOCAL_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
return|return
name|Node
operator|.
name|NODE_LOCAL_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
if|if
condition|(
name|Node
operator|.
name|NODE_MODE_SETTING
operator|.
name|exists
argument_list|(
name|settings
argument_list|)
condition|)
block|{
name|String
name|nodeMode
init|=
name|Node
operator|.
name|NODE_MODE_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"local"
operator|.
name|equals
argument_list|(
name|nodeMode
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
literal|"network"
operator|.
name|equals
argument_list|(
name|nodeMode
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unsupported node.mode ["
operator|+
name|nodeMode
operator|+
literal|"]. Should be one of [local, network]."
argument_list|)
throw|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|nodeRequiresLocalStorage
specifier|public
specifier|static
name|boolean
name|nodeRequiresLocalStorage
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|Node
operator|.
name|NODE_DATA_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
operator|||
name|Node
operator|.
name|NODE_MASTER_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|method|isMasterNode
specifier|public
specifier|static
name|boolean
name|isMasterNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|Node
operator|.
name|NODE_MASTER_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|method|isDataNode
specifier|public
specifier|static
name|boolean
name|isDataNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|Node
operator|.
name|NODE_DATA_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|method|isIngestNode
specifier|public
specifier|static
name|boolean
name|isIngestNode
parameter_list|(
name|Settings
name|settings
parameter_list|)
block|{
return|return
name|Node
operator|.
name|NODE_INGEST_SETTING
operator|.
name|get
argument_list|(
name|settings
argument_list|)
return|;
block|}
DECL|field|nodeName
specifier|private
specifier|final
name|String
name|nodeName
decl_stmt|;
DECL|field|nodeId
specifier|private
specifier|final
name|String
name|nodeId
decl_stmt|;
DECL|field|hostName
specifier|private
specifier|final
name|String
name|hostName
decl_stmt|;
DECL|field|hostAddress
specifier|private
specifier|final
name|String
name|hostAddress
decl_stmt|;
DECL|field|address
specifier|private
specifier|final
name|TransportAddress
name|address
decl_stmt|;
DECL|field|attributes
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|field|version
specifier|private
specifier|final
name|Version
name|version
decl_stmt|;
DECL|field|roles
specifier|private
specifier|final
name|Set
argument_list|<
name|Role
argument_list|>
name|roles
decl_stmt|;
comment|/**      * Creates a new {@link DiscoveryNode} by reading from the stream provided as argument      * @param in the stream      * @throws IOException if there is an error while reading from the stream      */
DECL|method|DiscoveryNode
specifier|public
name|DiscoveryNode
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nodeName
operator|=
name|in
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|in
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|in
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|hostAddress
operator|=
name|in
operator|.
name|readString
argument_list|()
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|TransportAddressSerializers
operator|.
name|addressFromStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|this
operator|.
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|(
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|attributes
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
name|int
name|rolesSize
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|this
operator|.
name|roles
operator|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Role
operator|.
name|class
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
name|rolesSize
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ordinal
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ordinal
operator|<
literal|0
operator|||
name|ordinal
operator|>=
name|Role
operator|.
name|values
argument_list|()
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown Role ordinal ["
operator|+
name|ordinal
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|this
operator|.
name|roles
operator|.
name|add
argument_list|(
name|Role
operator|.
name|values
argument_list|()
index|[
name|ordinal
index|]
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|version
operator|=
name|Version
operator|.
name|readVersion
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new {@link DiscoveryNode}      *<p>      *<b>Note:</b> if the version of the node is unknown {@link Version#minimumCompatibilityVersion()} should be used for the current      * version. it corresponds to the minimum version this elasticsearch version can communicate with. If a higher version is used      * the node might not be able to communicate with the remove node. After initial handshakes node versions will be discovered      * and updated.      *</p>      *      * @param nodeId     the nodes unique id.      * @param address    the nodes transport address      * @param attributes node attributes      * @param roles      node roles      * @param version    the version of the node.      */
DECL|method|DiscoveryNode
specifier|public
name|DiscoveryNode
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|TransportAddress
name|address
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|,
name|Set
argument_list|<
name|Role
argument_list|>
name|roles
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|this
argument_list|(
literal|""
argument_list|,
name|nodeId
argument_list|,
name|address
operator|.
name|getHost
argument_list|()
argument_list|,
name|address
operator|.
name|getAddress
argument_list|()
argument_list|,
name|address
argument_list|,
name|attributes
argument_list|,
name|roles
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new {@link DiscoveryNode}      *<p>      *<b>Note:</b> if the version of the node is unknown {@link Version#minimumCompatibilityVersion()} should be used for the current      * version. it corresponds to the minimum version this elasticsearch version can communicate with. If a higher version is used      * the node might not be able to communicate with the remove node. After initial handshakes node versions will be discovered      * and updated.      *</p>      *      * @param nodeName   the nodes name      * @param nodeId     the nodes unique id.      * @param address    the nodes transport address      * @param attributes node attributes      * @param roles      node roles      * @param version    the version of the node.      */
DECL|method|DiscoveryNode
specifier|public
name|DiscoveryNode
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|TransportAddress
name|address
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|,
name|Set
argument_list|<
name|Role
argument_list|>
name|roles
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
name|this
argument_list|(
name|nodeName
argument_list|,
name|nodeId
argument_list|,
name|address
operator|.
name|getHost
argument_list|()
argument_list|,
name|address
operator|.
name|getAddress
argument_list|()
argument_list|,
name|address
argument_list|,
name|attributes
argument_list|,
name|roles
argument_list|,
name|version
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new {@link DiscoveryNode}.      *<p>      *<b>Note:</b> if the version of the node is unknown {@link Version#minimumCompatibilityVersion()} should be used for the current      * version. it corresponds to the minimum version this elasticsearch version can communicate with. If a higher version is used      * the node might not be able to communicate with the remove node. After initial handshakes node versions will be discovered      * and updated.      *</p>      *      * @param nodeName    the nodes name      * @param nodeId      the nodes unique id.      * @param hostName    the nodes hostname      * @param hostAddress the nodes host address      * @param address     the nodes transport address      * @param attributes  node attributes      * @param roles       node roles      * @param version     the version of the node.      */
DECL|method|DiscoveryNode
specifier|public
name|DiscoveryNode
parameter_list|(
name|String
name|nodeName
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|String
name|hostName
parameter_list|,
name|String
name|hostAddress
parameter_list|,
name|TransportAddress
name|address
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
parameter_list|,
name|Set
argument_list|<
name|Role
argument_list|>
name|roles
parameter_list|,
name|Version
name|version
parameter_list|)
block|{
if|if
condition|(
name|nodeName
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|nodeName
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|nodeName
operator|=
literal|""
expr_stmt|;
block|}
name|this
operator|.
name|nodeId
operator|=
name|nodeId
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|hostName
operator|=
name|hostName
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|hostAddress
operator|=
name|hostAddress
operator|.
name|intern
argument_list|()
expr_stmt|;
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
if|if
condition|(
name|version
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|version
operator|=
name|Version
operator|.
name|CURRENT
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
name|this
operator|.
name|attributes
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
comment|//verify that no node roles are being provided as attributes
name|Predicate
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|predicate
init|=
parameter_list|(
name|attrs
parameter_list|)
lambda|->
block|{
for|for
control|(
name|Role
name|role
range|:
name|Role
operator|.
name|values
argument_list|()
control|)
block|{
assert|assert
name|attrs
operator|.
name|containsKey
argument_list|(
name|role
operator|.
name|getRoleName
argument_list|()
argument_list|)
operator|==
literal|false
assert|;
block|}
return|return
literal|true
return|;
block|}
decl_stmt|;
assert|assert
name|predicate
operator|.
name|test
argument_list|(
name|attributes
argument_list|)
assert|;
name|Set
argument_list|<
name|Role
argument_list|>
name|rolesSet
init|=
name|EnumSet
operator|.
name|noneOf
argument_list|(
name|Role
operator|.
name|class
argument_list|)
decl_stmt|;
name|rolesSet
operator|.
name|addAll
argument_list|(
name|roles
argument_list|)
expr_stmt|;
name|this
operator|.
name|roles
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|rolesSet
argument_list|)
expr_stmt|;
block|}
comment|/**      * The address that the node can be communicated with.      */
DECL|method|getAddress
specifier|public
name|TransportAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
comment|/**      * The unique id of the node.      */
DECL|method|getId
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
comment|/**      * The name of the node.      */
DECL|method|getName
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeName
return|;
block|}
comment|/**      * The node attributes.      */
DECL|method|getAttributes
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|this
operator|.
name|attributes
return|;
block|}
comment|/**      * Should this node hold data (shards) or not.      */
DECL|method|isDataNode
specifier|public
name|boolean
name|isDataNode
parameter_list|()
block|{
return|return
name|roles
operator|.
name|contains
argument_list|(
name|Role
operator|.
name|DATA
argument_list|)
return|;
block|}
comment|/**      * Can this node become master or not.      */
DECL|method|isMasterNode
specifier|public
name|boolean
name|isMasterNode
parameter_list|()
block|{
return|return
name|roles
operator|.
name|contains
argument_list|(
name|Role
operator|.
name|MASTER
argument_list|)
return|;
block|}
comment|/**      * Returns a boolean that tells whether this an ingest node or not      */
DECL|method|isIngestNode
specifier|public
name|boolean
name|isIngestNode
parameter_list|()
block|{
return|return
name|roles
operator|.
name|contains
argument_list|(
name|Role
operator|.
name|INGEST
argument_list|)
return|;
block|}
comment|/**      * Returns a set of all the roles that the node fulfills.      * If the node doesn't have any specific role, the set is returned empty, which means that the node is a coordinating only node.      */
DECL|method|getRoles
specifier|public
name|Set
argument_list|<
name|Role
argument_list|>
name|getRoles
parameter_list|()
block|{
return|return
name|roles
return|;
block|}
DECL|method|getVersion
specifier|public
name|Version
name|getVersion
parameter_list|()
block|{
return|return
name|this
operator|.
name|version
return|;
block|}
DECL|method|getHostName
specifier|public
name|String
name|getHostName
parameter_list|()
block|{
return|return
name|this
operator|.
name|hostName
return|;
block|}
DECL|method|getHostAddress
specifier|public
name|String
name|getHostAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|hostAddress
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|DiscoveryNode
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|DiscoveryNode
argument_list|(
name|in
argument_list|)
return|;
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
name|writeString
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|hostName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|hostAddress
argument_list|)
expr_stmt|;
name|addressToStream
argument_list|(
name|out
argument_list|,
name|address
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|attributes
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
name|attributes
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
name|out
operator|.
name|writeVInt
argument_list|(
name|roles
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Role
name|role
range|:
name|roles
control|)
block|{
name|out
operator|.
name|writeVInt
argument_list|(
name|role
operator|.
name|ordinal
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Version
operator|.
name|writeVersion
argument_list|(
name|version
argument_list|,
name|out
argument_list|)
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
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|DiscoveryNode
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DiscoveryNode
name|other
init|=
operator|(
name|DiscoveryNode
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|nodeId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|nodeId
argument_list|)
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
return|return
name|nodeId
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
name|nodeName
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeId
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Strings
operator|.
name|hasLength
argument_list|(
name|hostName
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
name|hostName
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|address
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
name|address
argument_list|)
operator|.
name|append
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|attributes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
name|XContentBuilder
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"transport_address"
argument_list|,
name|getAddress
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"attributes"
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
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|field
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
comment|/**      * Enum that holds all the possible roles that that a node can fulfill in a cluster.      * Each role has its name and a corresponding abbreviation used by cat apis.      */
DECL|enum|Role
specifier|public
enum|enum
name|Role
block|{
DECL|enum constant|MASTER
name|MASTER
argument_list|(
literal|"master"
argument_list|,
literal|"m"
argument_list|)
block|,
DECL|enum constant|DATA
name|DATA
argument_list|(
literal|"data"
argument_list|,
literal|"d"
argument_list|)
block|,
DECL|enum constant|INGEST
name|INGEST
argument_list|(
literal|"ingest"
argument_list|,
literal|"i"
argument_list|)
block|;
DECL|field|roleName
specifier|private
specifier|final
name|String
name|roleName
decl_stmt|;
DECL|field|abbreviation
specifier|private
specifier|final
name|String
name|abbreviation
decl_stmt|;
DECL|method|Role
name|Role
parameter_list|(
name|String
name|roleName
parameter_list|,
name|String
name|abbreviation
parameter_list|)
block|{
name|this
operator|.
name|roleName
operator|=
name|roleName
expr_stmt|;
name|this
operator|.
name|abbreviation
operator|=
name|abbreviation
expr_stmt|;
block|}
DECL|method|getRoleName
specifier|public
name|String
name|getRoleName
parameter_list|()
block|{
return|return
name|roleName
return|;
block|}
DECL|method|getAbbreviation
specifier|public
name|String
name|getAbbreviation
parameter_list|()
block|{
return|return
name|abbreviation
return|;
block|}
block|}
block|}
end_class

end_unit

