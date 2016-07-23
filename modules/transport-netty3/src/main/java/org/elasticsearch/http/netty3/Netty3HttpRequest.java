begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http.netty3
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty3
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
name|bytes
operator|.
name|BytesArray
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
name|bytes
operator|.
name|BytesReference
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
name|netty3
operator|.
name|Netty3Utils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|support
operator|.
name|RestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|channel
operator|.
name|Channel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|SocketAddress
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

begin_class
DECL|class|Netty3HttpRequest
specifier|public
class|class
name|Netty3HttpRequest
extends|extends
name|RestRequest
block|{
DECL|field|request
specifier|private
specifier|final
name|HttpRequest
name|request
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|Channel
name|channel
decl_stmt|;
DECL|field|content
specifier|private
specifier|final
name|BytesReference
name|content
decl_stmt|;
DECL|method|Netty3HttpRequest
specifier|public
name|Netty3HttpRequest
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|Channel
name|channel
parameter_list|)
block|{
name|super
argument_list|(
name|request
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|readable
argument_list|()
condition|)
block|{
name|this
operator|.
name|content
operator|=
name|Netty3Utils
operator|.
name|toBytesReference
argument_list|(
name|request
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|content
operator|=
name|BytesArray
operator|.
name|EMPTY
expr_stmt|;
block|}
block|}
DECL|method|request
specifier|public
name|org
operator|.
name|jboss
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpRequest
name|request
parameter_list|()
block|{
return|return
name|this
operator|.
name|request
return|;
block|}
annotation|@
name|Override
DECL|method|method
specifier|public
name|Method
name|method
parameter_list|()
block|{
name|HttpMethod
name|httpMethod
init|=
name|request
operator|.
name|getMethod
argument_list|()
decl_stmt|;
if|if
condition|(
name|httpMethod
operator|==
name|HttpMethod
operator|.
name|GET
condition|)
return|return
name|Method
operator|.
name|GET
return|;
if|if
condition|(
name|httpMethod
operator|==
name|HttpMethod
operator|.
name|POST
condition|)
return|return
name|Method
operator|.
name|POST
return|;
if|if
condition|(
name|httpMethod
operator|==
name|HttpMethod
operator|.
name|PUT
condition|)
return|return
name|Method
operator|.
name|PUT
return|;
if|if
condition|(
name|httpMethod
operator|==
name|HttpMethod
operator|.
name|DELETE
condition|)
return|return
name|Method
operator|.
name|DELETE
return|;
if|if
condition|(
name|httpMethod
operator|==
name|HttpMethod
operator|.
name|HEAD
condition|)
block|{
return|return
name|Method
operator|.
name|HEAD
return|;
block|}
if|if
condition|(
name|httpMethod
operator|==
name|HttpMethod
operator|.
name|OPTIONS
condition|)
block|{
return|return
name|Method
operator|.
name|OPTIONS
return|;
block|}
return|return
name|Method
operator|.
name|GET
return|;
block|}
annotation|@
name|Override
DECL|method|uri
specifier|public
name|String
name|uri
parameter_list|()
block|{
return|return
name|request
operator|.
name|getUri
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|hasContent
specifier|public
name|boolean
name|hasContent
parameter_list|()
block|{
return|return
name|content
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|content
specifier|public
name|BytesReference
name|content
parameter_list|()
block|{
return|return
name|content
return|;
block|}
comment|/**      * Returns the remote address where this rest request channel is "connected to".  The      * returned {@link SocketAddress} is supposed to be down-cast into more      * concrete type such as {@link java.net.InetSocketAddress} to retrieve      * the detailed information.      */
annotation|@
name|Override
DECL|method|getRemoteAddress
specifier|public
name|SocketAddress
name|getRemoteAddress
parameter_list|()
block|{
return|return
name|channel
operator|.
name|getRemoteAddress
argument_list|()
return|;
block|}
comment|/**      * Returns the local address where this request channel is bound to.  The returned      * {@link SocketAddress} is supposed to be down-cast into more concrete      * type such as {@link java.net.InetSocketAddress} to retrieve the detailed      * information.      */
annotation|@
name|Override
DECL|method|getLocalAddress
specifier|public
name|SocketAddress
name|getLocalAddress
parameter_list|()
block|{
return|return
name|channel
operator|.
name|getLocalAddress
argument_list|()
return|;
block|}
DECL|method|getChannel
specifier|public
name|Channel
name|getChannel
parameter_list|()
block|{
return|return
name|channel
return|;
block|}
annotation|@
name|Override
DECL|method|header
specifier|public
name|String
name|header
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|headers
specifier|public
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|headers
parameter_list|()
block|{
return|return
name|request
operator|.
name|headers
argument_list|()
operator|.
name|entries
argument_list|()
return|;
block|}
block|}
end_class

end_unit

