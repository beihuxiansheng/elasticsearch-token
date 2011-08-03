begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|CachedStreamOutput
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
name|buffer
operator|.
name|ChannelBuffer
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
name|buffer
operator|.
name|ChannelBuffers
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
name|channel
operator|.
name|Channel
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
name|channel
operator|.
name|ChannelFuture
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
name|channel
operator|.
name|ChannelFutureListener
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|Cookie
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|CookieDecoder
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|CookieEncoder
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
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|DefaultHttpResponse
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|elasticsearch
operator|.
name|common
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
name|netty
operator|.
name|handler
operator|.
name|codec
operator|.
name|http
operator|.
name|HttpVersion
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
name|http
operator|.
name|HttpException
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
name|XContentRestResponse
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
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
operator|.
name|NettyTransport
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
name|Set
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NettyHttpChannel
specifier|public
class|class
name|NettyHttpChannel
implements|implements
name|HttpChannel
block|{
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
DECL|field|request
specifier|private
specifier|final
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
name|org
operator|.
name|elasticsearch
operator|.
name|common
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
parameter_list|)
block|{
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
name|request
operator|=
name|request
expr_stmt|;
block|}
DECL|method|sendResponse
annotation|@
name|Override
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
name|request
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
name|request
operator|.
name|getHeader
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
name|request
operator|.
name|getHeader
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
name|elasticsearch
operator|.
name|common
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
name|addHeader
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
name|request
operator|.
name|getHeader
argument_list|(
name|HttpHeaders
operator|.
name|Names
operator|.
name|USER_AGENT
argument_list|)
argument_list|)
condition|)
block|{
comment|// add support for cross origin
name|resp
operator|.
name|addHeader
argument_list|(
literal|"Access-Control-Allow-Origin"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|==
name|HttpMethod
operator|.
name|OPTIONS
condition|)
block|{
comment|// also add more access control parameters
name|resp
operator|.
name|addHeader
argument_list|(
literal|"Access-Control-Max-Age"
argument_list|,
literal|1728000
argument_list|)
expr_stmt|;
name|resp
operator|.
name|addHeader
argument_list|(
literal|"Access-Control-Allow-Methods"
argument_list|,
literal|"PUT, DELETE"
argument_list|)
expr_stmt|;
name|resp
operator|.
name|addHeader
argument_list|(
literal|"Access-Control-Allow-Headers"
argument_list|,
literal|"X-Requested-With"
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|opaque
init|=
name|request
operator|.
name|getHeader
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
name|addHeader
argument_list|(
literal|"X-Opaque-Id"
argument_list|,
name|opaque
argument_list|)
expr_stmt|;
block|}
comment|// Convert the response content to a ChannelBuffer.
name|ChannelFutureListener
name|releaseContentListener
init|=
literal|null
decl_stmt|;
name|ChannelBuffer
name|buf
decl_stmt|;
try|try
block|{
if|if
condition|(
name|response
operator|instanceof
name|XContentRestResponse
condition|)
block|{
comment|// if its a builder based response, and it was created with a CachedStreamOutput, we can release it
comment|// after we write the response, and no need to do an extra copy because its not thread safe
name|XContentBuilder
name|builder
init|=
operator|(
operator|(
name|XContentRestResponse
operator|)
name|response
operator|)
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|builder
operator|.
name|payload
argument_list|()
operator|instanceof
name|CachedStreamOutput
operator|.
name|Entry
condition|)
block|{
name|releaseContentListener
operator|=
operator|new
name|NettyTransport
operator|.
name|CacheFutureListener
argument_list|(
operator|(
name|CachedStreamOutput
operator|.
name|Entry
operator|)
name|builder
operator|.
name|payload
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|builder
operator|.
name|unsafeBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|builder
operator|.
name|unsafeBytesLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|response
operator|.
name|contentThreadSafe
argument_list|()
condition|)
block|{
name|buf
operator|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|response
operator|.
name|content
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|contentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|=
name|ChannelBuffers
operator|.
name|copiedBuffer
argument_list|(
name|response
operator|.
name|content
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|contentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|response
operator|.
name|contentThreadSafe
argument_list|()
condition|)
block|{
name|buf
operator|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|response
operator|.
name|content
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|contentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|=
name|ChannelBuffers
operator|.
name|copiedBuffer
argument_list|(
name|response
operator|.
name|content
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|contentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|HttpException
argument_list|(
literal|"Failed to convert response to bytes"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|response
operator|.
name|prefixContent
argument_list|()
operator|!=
literal|null
operator|||
name|response
operator|.
name|suffixContent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|ChannelBuffer
name|prefixBuf
init|=
name|ChannelBuffers
operator|.
name|EMPTY_BUFFER
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|prefixContent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|prefixBuf
operator|=
name|ChannelBuffers
operator|.
name|copiedBuffer
argument_list|(
name|response
operator|.
name|prefixContent
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|prefixContentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ChannelBuffer
name|suffixBuf
init|=
name|ChannelBuffers
operator|.
name|EMPTY_BUFFER
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|suffixContent
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|suffixBuf
operator|=
name|ChannelBuffers
operator|.
name|copiedBuffer
argument_list|(
name|response
operator|.
name|suffixContent
argument_list|()
argument_list|,
literal|0
argument_list|,
name|response
operator|.
name|suffixContentLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|=
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|prefixBuf
argument_list|,
name|buf
argument_list|,
name|suffixBuf
argument_list|)
expr_stmt|;
block|}
name|resp
operator|.
name|setContent
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|resp
operator|.
name|setHeader
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
name|resp
operator|.
name|setHeader
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
name|buf
operator|.
name|readableBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
name|request
operator|.
name|getHeader
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
name|addHeader
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
comment|// Write the response.
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
name|releaseContentListener
operator|!=
literal|null
condition|)
block|{
name|future
operator|.
name|addListener
argument_list|(
name|releaseContentListener
argument_list|)
expr_stmt|;
block|}
comment|// Close the connection after the write operation is done if necessary.
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

