begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http.netty4.cors
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty4
operator|.
name|cors
package|;
end_package

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelDuplexHandler
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelFutureListener
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|channel
operator|.
name|ChannelHandlerContext
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|DefaultFullHttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaderNames
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|io
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
name|io
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
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpResponseStatus
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
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_comment
comment|/**  * Handles<a href="http://www.w3.org/TR/cors/">Cross Origin Resource Sharing</a> (CORS) requests.  *<p>  * This handler can be configured using a {@link Netty4CorsConfig}, please  * refer to this class for details about the configuration options available.  *  * This code was borrowed from Netty 4 and refactored to work for Elasticsearch's Netty 3 setup.  */
end_comment

begin_class
DECL|class|Netty4CorsHandler
specifier|public
class|class
name|Netty4CorsHandler
extends|extends
name|ChannelDuplexHandler
block|{
DECL|field|ANY_ORIGIN
specifier|public
specifier|static
specifier|final
name|String
name|ANY_ORIGIN
init|=
literal|"*"
decl_stmt|;
DECL|field|SCHEME_PATTERN
specifier|private
specifier|static
name|Pattern
name|SCHEME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^https?://"
argument_list|)
decl_stmt|;
DECL|field|config
specifier|private
specifier|final
name|Netty4CorsConfig
name|config
decl_stmt|;
DECL|field|request
specifier|private
name|HttpRequest
name|request
decl_stmt|;
comment|/**      * Creates a new instance with the specified {@link Netty4CorsConfig}.      */
DECL|method|Netty4CorsHandler
specifier|public
name|Netty4CorsHandler
parameter_list|(
specifier|final
name|Netty4CorsConfig
name|config
parameter_list|)
block|{
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|()
throw|;
block|}
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|channelRead
specifier|public
name|void
name|channelRead
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|config
operator|.
name|isCorsSupportEnabled
argument_list|()
operator|&&
name|msg
operator|instanceof
name|HttpRequest
condition|)
block|{
name|request
operator|=
operator|(
name|HttpRequest
operator|)
name|msg
expr_stmt|;
if|if
condition|(
name|isPreflightRequest
argument_list|(
name|request
argument_list|)
condition|)
block|{
name|handlePreflight
argument_list|(
name|ctx
argument_list|,
name|request
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|config
operator|.
name|isShortCircuit
argument_list|()
operator|&&
operator|!
name|validateOrigin
argument_list|()
condition|)
block|{
name|forbidden
argument_list|(
name|ctx
argument_list|,
name|request
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|ctx
operator|.
name|fireChannelRead
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
DECL|method|setCorsResponseHeaders
specifier|public
specifier|static
name|void
name|setCorsResponseHeaders
parameter_list|(
name|HttpRequest
name|request
parameter_list|,
name|HttpResponse
name|resp
parameter_list|,
name|Netty4CorsConfig
name|config
parameter_list|)
block|{
if|if
condition|(
operator|!
name|config
operator|.
name|isCorsSupportEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|String
name|originHeader
init|=
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|ORIGIN
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|originHeader
argument_list|)
condition|)
block|{
specifier|final
name|String
name|originHeaderVal
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|isAnyOriginSupported
argument_list|()
condition|)
block|{
name|originHeaderVal
operator|=
name|ANY_ORIGIN
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|config
operator|.
name|isOriginAllowed
argument_list|(
name|originHeader
argument_list|)
operator|||
name|isSameOrigin
argument_list|(
name|originHeader
argument_list|,
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|HOST
argument_list|)
argument_list|)
condition|)
block|{
name|originHeaderVal
operator|=
name|originHeader
expr_stmt|;
block|}
else|else
block|{
name|originHeaderVal
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|originHeaderVal
operator|!=
literal|null
condition|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
name|originHeaderVal
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|config
operator|.
name|isCredentialsAllowed
argument_list|()
condition|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|handlePreflight
specifier|private
name|void
name|handlePreflight
parameter_list|(
specifier|final
name|ChannelHandlerContext
name|ctx
parameter_list|,
specifier|final
name|HttpRequest
name|request
parameter_list|)
block|{
specifier|final
name|HttpResponse
name|response
init|=
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|request
operator|.
name|protocolVersion
argument_list|()
argument_list|,
name|HttpResponseStatus
operator|.
name|OK
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|setOrigin
argument_list|(
name|response
argument_list|)
condition|)
block|{
name|setAllowMethods
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|setAllowHeaders
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|setAllowCredentials
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|setMaxAge
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|setPreflightHeaders
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|writeAndFlush
argument_list|(
name|response
argument_list|)
operator|.
name|addListener
argument_list|(
name|ChannelFutureListener
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|forbidden
argument_list|(
name|ctx
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|forbidden
specifier|private
specifier|static
name|void
name|forbidden
parameter_list|(
specifier|final
name|ChannelHandlerContext
name|ctx
parameter_list|,
specifier|final
name|HttpRequest
name|request
parameter_list|)
block|{
name|ctx
operator|.
name|writeAndFlush
argument_list|(
operator|new
name|DefaultFullHttpResponse
argument_list|(
name|request
operator|.
name|protocolVersion
argument_list|()
argument_list|,
name|HttpResponseStatus
operator|.
name|FORBIDDEN
argument_list|)
argument_list|)
operator|.
name|addListener
argument_list|(
name|ChannelFutureListener
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
block|}
DECL|method|isSameOrigin
specifier|private
specifier|static
name|boolean
name|isSameOrigin
parameter_list|(
specifier|final
name|String
name|origin
parameter_list|,
specifier|final
name|String
name|host
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|host
argument_list|)
operator|==
literal|false
condition|)
block|{
comment|// strip protocol from origin
specifier|final
name|String
name|originDomain
init|=
name|SCHEME_PATTERN
operator|.
name|matcher
argument_list|(
name|origin
argument_list|)
operator|.
name|replaceFirst
argument_list|(
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|host
operator|.
name|equals
argument_list|(
name|originDomain
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
comment|/**      * This is a non CORS specification feature which enables the setting of preflight      * response headers that might be required by intermediaries.      *      * @param response the HttpResponse to which the preflight response headers should be added.      */
DECL|method|setPreflightHeaders
specifier|private
name|void
name|setPreflightHeaders
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|config
operator|.
name|preflightResponseHeaders
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setOrigin
specifier|private
name|boolean
name|setOrigin
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
specifier|final
name|String
name|origin
init|=
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|ORIGIN
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|origin
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|origin
argument_list|)
operator|&&
name|config
operator|.
name|isNullOriginAllowed
argument_list|()
condition|)
block|{
name|setAnyOrigin
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
if|if
condition|(
name|config
operator|.
name|isAnyOriginSupported
argument_list|()
condition|)
block|{
if|if
condition|(
name|config
operator|.
name|isCredentialsAllowed
argument_list|()
condition|)
block|{
name|echoRequestOrigin
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|setVaryHeader
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setAnyOrigin
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
if|if
condition|(
name|config
operator|.
name|isOriginAllowed
argument_list|(
name|origin
argument_list|)
condition|)
block|{
name|setOrigin
argument_list|(
name|response
argument_list|,
name|origin
argument_list|)
expr_stmt|;
name|setVaryHeader
argument_list|(
name|response
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|validateOrigin
specifier|private
name|boolean
name|validateOrigin
parameter_list|()
block|{
if|if
condition|(
name|config
operator|.
name|isAnyOriginSupported
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|String
name|origin
init|=
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|ORIGIN
argument_list|)
decl_stmt|;
if|if
condition|(
name|Strings
operator|.
name|isNullOrEmpty
argument_list|(
name|origin
argument_list|)
condition|)
block|{
comment|// Not a CORS request so we cannot validate it. It may be a non CORS request.
return|return
literal|true
return|;
block|}
if|if
condition|(
literal|"null"
operator|.
name|equals
argument_list|(
name|origin
argument_list|)
operator|&&
name|config
operator|.
name|isNullOriginAllowed
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// if the origin is the same as the host of the request, then allow
if|if
condition|(
name|isSameOrigin
argument_list|(
name|origin
argument_list|,
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|HOST
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|config
operator|.
name|isOriginAllowed
argument_list|(
name|origin
argument_list|)
return|;
block|}
DECL|method|echoRequestOrigin
specifier|private
name|void
name|echoRequestOrigin
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|setOrigin
argument_list|(
name|response
argument_list|,
name|request
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|ORIGIN
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setVaryHeader
specifier|private
specifier|static
name|void
name|setVaryHeader
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaderNames
operator|.
name|VARY
argument_list|,
name|HttpHeaderNames
operator|.
name|ORIGIN
argument_list|)
expr_stmt|;
block|}
DECL|method|setAnyOrigin
specifier|private
specifier|static
name|void
name|setAnyOrigin
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|setOrigin
argument_list|(
name|response
argument_list|,
name|ANY_ORIGIN
argument_list|)
expr_stmt|;
block|}
DECL|method|setOrigin
specifier|private
specifier|static
name|void
name|setOrigin
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|,
specifier|final
name|String
name|origin
parameter_list|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
name|origin
argument_list|)
expr_stmt|;
block|}
DECL|method|setAllowCredentials
specifier|private
name|void
name|setAllowCredentials
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|isCredentialsAllowed
argument_list|()
operator|&&
operator|!
name|response
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|)
operator|.
name|equals
argument_list|(
name|ANY_ORIGIN
argument_list|)
condition|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isPreflightRequest
specifier|private
specifier|static
name|boolean
name|isPreflightRequest
parameter_list|(
specifier|final
name|HttpRequest
name|request
parameter_list|)
block|{
specifier|final
name|HttpHeaders
name|headers
init|=
name|request
operator|.
name|headers
argument_list|()
decl_stmt|;
return|return
name|request
operator|.
name|method
argument_list|()
operator|.
name|equals
argument_list|(
name|HttpMethod
operator|.
name|OPTIONS
argument_list|)
operator|&&
name|headers
operator|.
name|contains
argument_list|(
name|HttpHeaderNames
operator|.
name|ORIGIN
argument_list|)
operator|&&
name|headers
operator|.
name|contains
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_REQUEST_METHOD
argument_list|)
return|;
block|}
DECL|method|setAllowMethods
specifier|private
name|void
name|setAllowMethods
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
name|config
operator|.
name|allowedRequestMethods
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|m
lambda|->
name|m
operator|.
name|name
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|setAllowHeaders
specifier|private
name|void
name|setAllowHeaders
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|,
name|config
operator|.
name|allowedRequestHeaders
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|setMaxAge
specifier|private
name|void
name|setMaxAge
parameter_list|(
specifier|final
name|HttpResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|headers
argument_list|()
operator|.
name|set
argument_list|(
name|HttpHeaderNames
operator|.
name|ACCESS_CONTROL_MAX_AGE
argument_list|,
name|config
operator|.
name|maxAge
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

