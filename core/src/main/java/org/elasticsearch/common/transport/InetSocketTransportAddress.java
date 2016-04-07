begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|transport
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
name|network
operator|.
name|NetworkAddress
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_comment
comment|/**  * A transport address used for IP socket address (wraps {@link java.net.InetSocketAddress}).  */
end_comment

begin_class
DECL|class|InetSocketTransportAddress
specifier|public
specifier|final
class|class
name|InetSocketTransportAddress
implements|implements
name|TransportAddress
block|{
DECL|field|PROTO
specifier|public
specifier|static
specifier|final
name|InetSocketTransportAddress
name|PROTO
init|=
operator|new
name|InetSocketTransportAddress
argument_list|()
decl_stmt|;
DECL|field|address
specifier|private
specifier|final
name|InetSocketAddress
name|address
decl_stmt|;
DECL|method|InetSocketTransportAddress
specifier|public
name|InetSocketTransportAddress
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|len
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|a
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
comment|// 4 bytes (IPv4) or 16 bytes (IPv6)
name|in
operator|.
name|readFully
argument_list|(
name|a
argument_list|)
expr_stmt|;
name|InetAddress
name|inetAddress
init|=
name|InetAddress
operator|.
name|getByAddress
argument_list|(
name|a
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|this
operator|.
name|address
operator|=
operator|new
name|InetSocketAddress
argument_list|(
name|inetAddress
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
DECL|method|InetSocketTransportAddress
specifier|private
name|InetSocketTransportAddress
parameter_list|()
block|{
name|address
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|InetSocketTransportAddress
specifier|public
name|InetSocketTransportAddress
parameter_list|(
name|InetAddress
name|address
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|address
argument_list|,
name|port
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|InetSocketTransportAddress
specifier|public
name|InetSocketTransportAddress
parameter_list|(
name|InetSocketAddress
name|address
parameter_list|)
block|{
if|if
condition|(
name|address
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"InetSocketAddress must not be null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|address
operator|.
name|getAddress
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Address must be resolved but wasn't - InetSocketAddress#getAddress() returned null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|address
operator|=
name|address
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|uniqueAddressTypeId
specifier|public
name|short
name|uniqueAddressTypeId
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|sameHost
specifier|public
name|boolean
name|sameHost
parameter_list|(
name|TransportAddress
name|other
parameter_list|)
block|{
return|return
name|other
operator|instanceof
name|InetSocketTransportAddress
operator|&&
name|address
operator|.
name|getAddress
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|InetSocketTransportAddress
operator|)
name|other
operator|)
operator|.
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHost
specifier|public
name|String
name|getHost
parameter_list|()
block|{
return|return
name|getAddress
argument_list|()
return|;
comment|// just delegate no resolving
block|}
annotation|@
name|Override
DECL|method|getAddress
specifier|public
name|String
name|getAddress
parameter_list|()
block|{
return|return
name|NetworkAddress
operator|.
name|format
argument_list|(
name|address
operator|.
name|getAddress
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getPort
specifier|public
name|int
name|getPort
parameter_list|()
block|{
return|return
name|address
operator|.
name|getPort
argument_list|()
return|;
block|}
DECL|method|address
specifier|public
name|InetSocketAddress
name|address
parameter_list|()
block|{
return|return
name|this
operator|.
name|address
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|TransportAddress
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
name|InetSocketTransportAddress
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
name|byte
index|[]
name|bytes
init|=
name|address
argument_list|()
operator|.
name|getAddress
argument_list|()
operator|.
name|getAddress
argument_list|()
decl_stmt|;
comment|// 4 bytes (IPv4) or 16 bytes (IPv6)
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// 1 byte
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// don't serialize scope ids over the network!!!!
comment|// these only make sense with respect to the local machine, and will only formulate
comment|// the address incorrectly remotely.
name|out
operator|.
name|writeInt
argument_list|(
name|address
operator|.
name|getPort
argument_list|()
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
name|InetSocketTransportAddress
name|address1
init|=
operator|(
name|InetSocketTransportAddress
operator|)
name|o
decl_stmt|;
return|return
name|address
operator|.
name|equals
argument_list|(
name|address1
operator|.
name|address
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
name|address
operator|!=
literal|null
condition|?
name|address
operator|.
name|hashCode
argument_list|()
else|:
literal|0
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
return|return
name|NetworkAddress
operator|.
name|format
argument_list|(
name|address
argument_list|)
return|;
block|}
block|}
end_class

end_unit

