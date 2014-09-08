begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http.netty
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty
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
name|base
operator|.
name|Strings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|common
operator|.
name|io
operator|.
name|stream
operator|.
name|BytesStreamOutput
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
name|ReleasableBytesStreamOutput
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
name|lease
operator|.
name|Releasable
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
name|netty
operator|.
name|ReleaseChannelFutureListener
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
name|HttpChannel
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
name|RestResponse
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
name|RestStatus
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
name|buffer
operator|.
name|ChannelBuffer
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
name|buffer
operator|.
name|ChannelBuffers
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
name|channel
operator|.
name|ChannelFuture
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
name|ChannelFutureListener
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
name|*
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
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty
operator|.
name|NettyHttpServerTransport
operator|.
name|*
import|;
end_import

begin_import
import|import static
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
name|HttpHeaders
operator|.
name|Names
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NettyHttpChannel
specifier|public
class|class
name|NettyHttpChannel
extends|extends
name|HttpChannel
block|{
DECL|field|END_JSONP
specifier|private
specifier|static
specifier|final
name|ChannelBuffer
name|END_JSONP
decl_stmt|;
static|static
block|{
name|BytesRef
name|U_END_JSONP
init|=
operator|new
name|BytesRef
argument_list|(
literal|");"
argument_list|)
decl_stmt|;
name|END_JSONP
operator|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|U_END_JSONP
operator|.
name|bytes
argument_list|,
name|U_END_JSONP
operator|.
name|offset
argument_list|,
name|U_END_JSONP
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
DECL|field|transport
specifier|private
specifier|final
name|NettyHttpServerTransport
name|transport
decl_stmt|;
DECL|field|channel
specifier|private
specifier|final
name|Channel
name|channel
decl_stmt|;
DECL|field|nettyRequest
specifier|private
specifier|final
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
name|nettyRequest
decl_stmt|;
DECL|field|corsPattern
specifier|private
name|Pattern
name|corsPattern
decl_stmt|;
DECL|method|NettyHttpChannel
specifier|public
name|NettyHttpChannel
parameter_list|(
name|NettyHttpServerTransport
name|transport
parameter_list|,
name|Channel
name|channel
parameter_list|,
name|NettyHttpRequest
name|request
parameter_list|,
name|Pattern
name|corsPattern
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|transport
operator|=
name|transport
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|nettyRequest
operator|=
name|request
operator|.
name|request
argument_list|()
expr_stmt|;
name|this
operator|.
name|corsPattern
operator|=
name|corsPattern
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newBytesOutput
specifier|public
name|BytesStreamOutput
name|newBytesOutput
parameter_list|()
block|{
return|return
operator|new
name|ReleasableBytesStreamOutput
argument_list|(
name|transport
operator|.
name|bigArrays
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|sendResponse
specifier|public
name|void
name|sendResponse
parameter_list|(
name|RestResponse
name|response
parameter_list|)
block|{
comment|// Decide whether to close the connection or not.
name|boolean
name|http10
init|=
name|nettyRequest
operator|.
name|getProtocolVersion
argument_list|()
operator|.
name|equals
argument_list|(
name|HttpVersion
operator|.
name|HTTP_1_0
argument_list|)
decl_stmt|;
name|boolean
name|close
init|=
name|HttpHeaders
operator|.
name|Values
operator|.
name|CLOSE
operator|.
name|equalsIgnoreCase
argument_list|(
name|nettyRequest
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONNECTION
argument_list|)
argument_list|)
operator|||
operator|(
name|http10
operator|&&
operator|!
name|HttpHeaders
operator|.
name|Values
operator|.
name|KEEP_ALIVE
operator|.
name|equalsIgnoreCase
argument_list|(
name|nettyRequest
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONNECTION
argument_list|)
argument_list|)
operator|)
decl_stmt|;
comment|// Build the response object.
name|HttpResponseStatus
name|status
init|=
name|getStatus
argument_list|(
name|response
operator|.
name|status
argument_list|()
argument_list|)
decl_stmt|;
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
name|HttpResponse
name|resp
decl_stmt|;
if|if
condition|(
name|http10
condition|)
block|{
name|resp
operator|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HttpVersion
operator|.
name|HTTP_1_0
argument_list|,
name|status
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|close
condition|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONNECTION
argument_list|,
literal|"Keep-Alive"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|resp
operator|=
operator|new
name|DefaultHttpResponse
argument_list|(
name|HttpVersion
operator|.
name|HTTP_1_1
argument_list|,
name|status
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|RestUtils
operator|.
name|isBrowser
argument_list|(
name|nettyRequest
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|USER_AGENT
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|transport
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
name|SETTING_CORS_ENABLED
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|String
name|originHeader
init|=
name|request
operator|.
name|header
argument_list|(
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
if|if
condition|(
name|corsPattern
operator|==
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
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
name|transport
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|SETTING_CORS_ALLOW_ORIGIN
argument_list|,
literal|"*"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|ACCESS_CONTROL_ALLOW_ORIGIN
argument_list|,
name|corsPattern
operator|.
name|matcher
argument_list|(
name|originHeader
argument_list|)
operator|.
name|matches
argument_list|()
condition|?
name|originHeader
else|:
literal|"null"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|nettyRequest
operator|.
name|getMethod
argument_list|()
operator|==
name|HttpMethod
operator|.
name|OPTIONS
condition|)
block|{
comment|// Allow Ajax requests based on the CORS "preflight" request
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|ACCESS_CONTROL_MAX_AGE
argument_list|,
name|transport
operator|.
name|settings
argument_list|()
operator|.
name|getAsInt
argument_list|(
name|SETTING_CORS_MAX_AGE
argument_list|,
literal|1728000
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|ACCESS_CONTROL_ALLOW_METHODS
argument_list|,
name|transport
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|SETTING_CORS_ALLOW_METHODS
argument_list|,
literal|"OPTIONS, HEAD, GET, POST, PUT, DELETE"
argument_list|)
argument_list|)
expr_stmt|;
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|ACCESS_CONTROL_ALLOW_HEADERS
argument_list|,
name|transport
operator|.
name|settings
argument_list|()
operator|.
name|get
argument_list|(
name|SETTING_CORS_ALLOW_HEADERS
argument_list|,
literal|"X-Requested-With, Content-Type, Content-Length"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transport
operator|.
name|settings
argument_list|()
operator|.
name|getAsBoolean
argument_list|(
name|SETTING_CORS_ALLOW_CREDENTIALS
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|ACCESS_CONTROL_ALLOW_CREDENTIALS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|opaque
init|=
name|nettyRequest
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
literal|"X-Opaque-Id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|opaque
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
literal|"X-Opaque-Id"
argument_list|,
name|opaque
argument_list|)
expr_stmt|;
block|}
comment|// Add all custom headers
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|customHeaders
init|=
name|response
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
if|if
condition|(
name|customHeaders
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|headerEntry
range|:
name|customHeaders
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|headerValue
range|:
name|headerEntry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|headerEntry
operator|.
name|getKey
argument_list|()
argument_list|,
name|headerValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|BytesReference
name|content
init|=
name|response
operator|.
name|content
argument_list|()
decl_stmt|;
name|ChannelBuffer
name|buffer
decl_stmt|;
name|boolean
name|addedReleaseListener
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|response
operator|.
name|contentThreadSafe
argument_list|()
condition|)
block|{
name|buffer
operator|=
name|content
operator|.
name|toChannelBuffer
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|=
name|content
operator|.
name|copyBytesArray
argument_list|()
operator|.
name|toChannelBuffer
argument_list|()
expr_stmt|;
block|}
comment|// handle JSONP
name|String
name|callback
init|=
name|request
operator|.
name|param
argument_list|(
literal|"callback"
argument_list|)
decl_stmt|;
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
specifier|final
name|BytesRef
name|callbackBytes
init|=
operator|new
name|BytesRef
argument_list|(
name|callback
argument_list|)
decl_stmt|;
name|callbackBytes
operator|.
name|bytes
index|[
name|callbackBytes
operator|.
name|length
index|]
operator|=
literal|'('
expr_stmt|;
name|callbackBytes
operator|.
name|length
operator|++
expr_stmt|;
name|buffer
operator|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|callbackBytes
operator|.
name|bytes
argument_list|,
name|callbackBytes
operator|.
name|offset
argument_list|,
name|callbackBytes
operator|.
name|length
argument_list|)
argument_list|,
name|buffer
argument_list|,
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|END_JSONP
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add content-type header of "application/javascript"
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_TYPE
argument_list|,
literal|"application/javascript"
argument_list|)
expr_stmt|;
block|}
name|resp
operator|.
name|setContent
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// If our response doesn't specify a content-type header, set one
if|if
condition|(
operator|!
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|contains
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_TYPE
argument_list|)
condition|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_TYPE
argument_list|,
name|response
operator|.
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// If our response has no content-length, calculate and set one
if|if
condition|(
operator|!
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|contains
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_LENGTH
argument_list|)
condition|)
block|{
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|CONTENT_LENGTH
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|buffer
operator|.
name|readableBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transport
operator|.
name|resetCookies
condition|)
block|{
name|String
name|cookieString
init|=
name|nettyRequest
operator|.
name|headers
argument_list|()
operator|.
name|get
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|COOKIE
argument_list|)
decl_stmt|;
if|if
condition|(
name|cookieString
operator|!=
literal|null
condition|)
block|{
name|CookieDecoder
name|cookieDecoder
init|=
operator|new
name|CookieDecoder
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Cookie
argument_list|>
name|cookies
init|=
name|cookieDecoder
operator|.
name|decode
argument_list|(
name|cookieString
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|cookies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Reset the cookies if necessary.
name|CookieEncoder
name|cookieEncoder
init|=
operator|new
name|CookieEncoder
argument_list|(
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Cookie
name|cookie
range|:
name|cookies
control|)
block|{
name|cookieEncoder
operator|.
name|addCookie
argument_list|(
name|cookie
argument_list|)
expr_stmt|;
block|}
name|resp
operator|.
name|headers
argument_list|()
operator|.
name|add
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|SET_COOKIE
argument_list|,
name|cookieEncoder
operator|.
name|encode
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|ChannelFuture
name|future
init|=
name|channel
operator|.
name|write
argument_list|(
name|resp
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|contentThreadSafe
argument_list|()
operator|&&
name|content
operator|instanceof
name|Releasable
condition|)
block|{
name|future
operator|.
name|addListener
argument_list|(
operator|new
name|ReleaseChannelFutureListener
argument_list|(
operator|(
name|Releasable
operator|)
name|content
argument_list|)
argument_list|)
expr_stmt|;
name|addedReleaseListener
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|close
condition|)
block|{
name|future
operator|.
name|addListener
argument_list|(
name|ChannelFutureListener
operator|.
name|CLOSE
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|addedReleaseListener
operator|&&
name|content
operator|instanceof
name|Releasable
condition|)
block|{
operator|(
operator|(
name|Releasable
operator|)
name|content
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|field|TOO_MANY_REQUESTS
specifier|private
specifier|static
specifier|final
name|HttpResponseStatus
name|TOO_MANY_REQUESTS
init|=
operator|new
name|HttpResponseStatus
argument_list|(
literal|429
argument_list|,
literal|"Too Many Requests"
argument_list|)
decl_stmt|;
DECL|method|getStatus
specifier|private
name|HttpResponseStatus
name|getStatus
parameter_list|(
name|RestStatus
name|status
parameter_list|)
block|{
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|CONTINUE
case|:
return|return
name|HttpResponseStatus
operator|.
name|CONTINUE
return|;
case|case
name|SWITCHING_PROTOCOLS
case|:
return|return
name|HttpResponseStatus
operator|.
name|SWITCHING_PROTOCOLS
return|;
case|case
name|OK
case|:
return|return
name|HttpResponseStatus
operator|.
name|OK
return|;
case|case
name|CREATED
case|:
return|return
name|HttpResponseStatus
operator|.
name|CREATED
return|;
case|case
name|ACCEPTED
case|:
return|return
name|HttpResponseStatus
operator|.
name|ACCEPTED
return|;
case|case
name|NON_AUTHORITATIVE_INFORMATION
case|:
return|return
name|HttpResponseStatus
operator|.
name|NON_AUTHORITATIVE_INFORMATION
return|;
case|case
name|NO_CONTENT
case|:
return|return
name|HttpResponseStatus
operator|.
name|NO_CONTENT
return|;
case|case
name|RESET_CONTENT
case|:
return|return
name|HttpResponseStatus
operator|.
name|RESET_CONTENT
return|;
case|case
name|PARTIAL_CONTENT
case|:
return|return
name|HttpResponseStatus
operator|.
name|PARTIAL_CONTENT
return|;
case|case
name|MULTI_STATUS
case|:
comment|// no status for this??
return|return
name|HttpResponseStatus
operator|.
name|INTERNAL_SERVER_ERROR
return|;
case|case
name|MULTIPLE_CHOICES
case|:
return|return
name|HttpResponseStatus
operator|.
name|MULTIPLE_CHOICES
return|;
case|case
name|MOVED_PERMANENTLY
case|:
return|return
name|HttpResponseStatus
operator|.
name|MOVED_PERMANENTLY
return|;
case|case
name|FOUND
case|:
return|return
name|HttpResponseStatus
operator|.
name|FOUND
return|;
case|case
name|SEE_OTHER
case|:
return|return
name|HttpResponseStatus
operator|.
name|SEE_OTHER
return|;
case|case
name|NOT_MODIFIED
case|:
return|return
name|HttpResponseStatus
operator|.
name|NOT_MODIFIED
return|;
case|case
name|USE_PROXY
case|:
return|return
name|HttpResponseStatus
operator|.
name|USE_PROXY
return|;
case|case
name|TEMPORARY_REDIRECT
case|:
return|return
name|HttpResponseStatus
operator|.
name|TEMPORARY_REDIRECT
return|;
case|case
name|BAD_REQUEST
case|:
return|return
name|HttpResponseStatus
operator|.
name|BAD_REQUEST
return|;
case|case
name|UNAUTHORIZED
case|:
return|return
name|HttpResponseStatus
operator|.
name|UNAUTHORIZED
return|;
case|case
name|PAYMENT_REQUIRED
case|:
return|return
name|HttpResponseStatus
operator|.
name|PAYMENT_REQUIRED
return|;
case|case
name|FORBIDDEN
case|:
return|return
name|HttpResponseStatus
operator|.
name|FORBIDDEN
return|;
case|case
name|NOT_FOUND
case|:
return|return
name|HttpResponseStatus
operator|.
name|NOT_FOUND
return|;
case|case
name|METHOD_NOT_ALLOWED
case|:
return|return
name|HttpResponseStatus
operator|.
name|METHOD_NOT_ALLOWED
return|;
case|case
name|NOT_ACCEPTABLE
case|:
return|return
name|HttpResponseStatus
operator|.
name|NOT_ACCEPTABLE
return|;
case|case
name|PROXY_AUTHENTICATION
case|:
return|return
name|HttpResponseStatus
operator|.
name|PROXY_AUTHENTICATION_REQUIRED
return|;
case|case
name|REQUEST_TIMEOUT
case|:
return|return
name|HttpResponseStatus
operator|.
name|REQUEST_TIMEOUT
return|;
case|case
name|CONFLICT
case|:
return|return
name|HttpResponseStatus
operator|.
name|CONFLICT
return|;
case|case
name|GONE
case|:
return|return
name|HttpResponseStatus
operator|.
name|GONE
return|;
case|case
name|LENGTH_REQUIRED
case|:
return|return
name|HttpResponseStatus
operator|.
name|LENGTH_REQUIRED
return|;
case|case
name|PRECONDITION_FAILED
case|:
return|return
name|HttpResponseStatus
operator|.
name|PRECONDITION_FAILED
return|;
case|case
name|REQUEST_ENTITY_TOO_LARGE
case|:
return|return
name|HttpResponseStatus
operator|.
name|REQUEST_ENTITY_TOO_LARGE
return|;
case|case
name|REQUEST_URI_TOO_LONG
case|:
return|return
name|HttpResponseStatus
operator|.
name|REQUEST_URI_TOO_LONG
return|;
case|case
name|UNSUPPORTED_MEDIA_TYPE
case|:
return|return
name|HttpResponseStatus
operator|.
name|UNSUPPORTED_MEDIA_TYPE
return|;
case|case
name|REQUESTED_RANGE_NOT_SATISFIED
case|:
return|return
name|HttpResponseStatus
operator|.
name|REQUESTED_RANGE_NOT_SATISFIABLE
return|;
case|case
name|EXPECTATION_FAILED
case|:
return|return
name|HttpResponseStatus
operator|.
name|EXPECTATION_FAILED
return|;
case|case
name|UNPROCESSABLE_ENTITY
case|:
return|return
name|HttpResponseStatus
operator|.
name|BAD_REQUEST
return|;
case|case
name|LOCKED
case|:
return|return
name|HttpResponseStatus
operator|.
name|BAD_REQUEST
return|;
case|case
name|FAILED_DEPENDENCY
case|:
return|return
name|HttpResponseStatus
operator|.
name|BAD_REQUEST
return|;
case|case
name|TOO_MANY_REQUESTS
case|:
return|return
name|TOO_MANY_REQUESTS
return|;
case|case
name|INTERNAL_SERVER_ERROR
case|:
return|return
name|HttpResponseStatus
operator|.
name|INTERNAL_SERVER_ERROR
return|;
case|case
name|NOT_IMPLEMENTED
case|:
return|return
name|HttpResponseStatus
operator|.
name|NOT_IMPLEMENTED
return|;
case|case
name|BAD_GATEWAY
case|:
return|return
name|HttpResponseStatus
operator|.
name|BAD_GATEWAY
return|;
case|case
name|SERVICE_UNAVAILABLE
case|:
return|return
name|HttpResponseStatus
operator|.
name|SERVICE_UNAVAILABLE
return|;
case|case
name|GATEWAY_TIMEOUT
case|:
return|return
name|HttpResponseStatus
operator|.
name|GATEWAY_TIMEOUT
return|;
case|case
name|HTTP_VERSION_NOT_SUPPORTED
case|:
return|return
name|HttpResponseStatus
operator|.
name|HTTP_VERSION_NOT_SUPPORTED
return|;
default|default:
return|return
name|HttpResponseStatus
operator|.
name|INTERNAL_SERVER_ERROR
return|;
block|}
block|}
block|}
end_class

end_unit

