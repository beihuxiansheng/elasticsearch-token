begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|ImmutableMap
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
name|Streamable
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
name|io
operator|.
name|Serializable
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
name|*
import|;
end_import

begin_comment
comment|/**  * A discovery node represents a node that is part of the cluster.  *  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|DiscoveryNode
specifier|public
class|class
name|DiscoveryNode
implements|implements
name|Streamable
implements|,
name|Serializable
block|{
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
operator|!
operator|(
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"node.client"
argument_list|,
literal|false
argument_list|)
operator|||
operator|(
operator|!
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"node.data"
argument_list|,
literal|true
argument_list|)
operator|&&
operator|!
name|settings
operator|.
name|getAsBoolean
argument_list|(
literal|"node.master"
argument_list|,
literal|true
argument_list|)
operator|)
operator|)
return|;
block|}
DECL|field|EMPTY_LIST
specifier|public
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|DiscoveryNode
argument_list|>
name|EMPTY_LIST
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
DECL|field|nodeName
specifier|private
name|String
name|nodeName
init|=
literal|""
operator|.
name|intern
argument_list|()
decl_stmt|;
DECL|field|nodeId
specifier|private
name|String
name|nodeId
decl_stmt|;
DECL|field|address
specifier|private
name|TransportAddress
name|address
decl_stmt|;
DECL|field|attributes
specifier|private
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
decl_stmt|;
DECL|method|DiscoveryNode
specifier|private
name|DiscoveryNode
parameter_list|()
block|{     }
DECL|method|DiscoveryNode
specifier|public
name|DiscoveryNode
parameter_list|(
name|String
name|nodeId
parameter_list|,
name|TransportAddress
name|address
parameter_list|)
block|{
name|this
argument_list|(
literal|""
argument_list|,
name|nodeId
argument_list|,
name|address
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
parameter_list|)
block|{
if|if
condition|(
name|nodeName
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|nodeName
operator|=
literal|""
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
name|nodeName
operator|.
name|intern
argument_list|()
expr_stmt|;
block|}
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
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|intern
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|intern
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|attributes
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
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
name|address
operator|=
name|address
expr_stmt|;
block|}
comment|/**      * Should this node form a connection to the provided node.      */
DECL|method|shouldConnectTo
specifier|public
name|boolean
name|shouldConnectTo
parameter_list|(
name|DiscoveryNode
name|otherNode
parameter_list|)
block|{
if|if
condition|(
name|clientNode
argument_list|()
operator|&&
name|otherNode
operator|.
name|clientNode
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * The address that the node can be communicated with.      */
DECL|method|address
specifier|public
name|TransportAddress
name|address
parameter_list|()
block|{
return|return
name|address
return|;
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
argument_list|()
return|;
block|}
comment|/**      * The unique id of the node.      */
DECL|method|id
specifier|public
name|String
name|id
parameter_list|()
block|{
return|return
name|nodeId
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
name|id
argument_list|()
return|;
block|}
comment|/**      * The name of the node.      */
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeName
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
name|name
argument_list|()
return|;
block|}
comment|/**      * The node attributes.      */
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
name|attributes
return|;
block|}
comment|/**      * The node attributes.      */
DECL|method|getAttributes
specifier|public
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
argument_list|()
return|;
block|}
comment|/**      * Should this node hold data (shards) or not.      */
DECL|method|dataNode
specifier|public
name|boolean
name|dataNode
parameter_list|()
block|{
name|String
name|data
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"data"
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
operator|!
name|clientNode
argument_list|()
return|;
block|}
return|return
name|data
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
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
name|dataNode
argument_list|()
return|;
block|}
comment|/**      * Is the node a client node or not.      */
DECL|method|clientNode
specifier|public
name|boolean
name|clientNode
parameter_list|()
block|{
name|String
name|client
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"client"
argument_list|)
decl_stmt|;
return|return
name|client
operator|!=
literal|null
operator|&&
name|client
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
return|;
block|}
DECL|method|isClientNode
specifier|public
name|boolean
name|isClientNode
parameter_list|()
block|{
return|return
name|clientNode
argument_list|()
return|;
block|}
comment|/**      * Can this node become master or not.      */
DECL|method|masterNode
specifier|public
name|boolean
name|masterNode
parameter_list|()
block|{
name|String
name|master
init|=
name|attributes
operator|.
name|get
argument_list|(
literal|"master"
argument_list|)
decl_stmt|;
if|if
condition|(
name|master
operator|==
literal|null
condition|)
block|{
return|return
operator|!
name|clientNode
argument_list|()
return|;
block|}
return|return
name|master
operator|.
name|equals
argument_list|(
literal|"true"
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
name|masterNode
argument_list|()
return|;
block|}
DECL|method|readNode
specifier|public
specifier|static
name|DiscoveryNode
name|readNode
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|DiscoveryNode
name|node
init|=
operator|new
name|DiscoveryNode
argument_list|()
decl_stmt|;
name|node
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
DECL|method|readFrom
annotation|@
name|Override
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
name|nodeName
operator|=
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|intern
argument_list|()
expr_stmt|;
name|nodeId
operator|=
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|intern
argument_list|()
expr_stmt|;
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
name|readUTF
argument_list|()
operator|.
name|intern
argument_list|()
argument_list|,
name|in
operator|.
name|readUTF
argument_list|()
operator|.
name|intern
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|attributes
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
DECL|method|writeTo
annotation|@
name|Override
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
name|writeUTF
argument_list|(
name|nodeName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|nodeId
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
name|writeUTF
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeUTF
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|equals
annotation|@
name|Override
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
return|return
literal|false
return|;
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
DECL|method|hashCode
annotation|@
name|Override
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
DECL|method|toString
annotation|@
name|Override
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
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|nodeName
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
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
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|nodeId
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
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
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|address
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
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
block|}
end_class

end_unit

