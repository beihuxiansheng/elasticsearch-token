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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|UnicodeUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticSearchIllegalArgumentException
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
name|HttpRequest
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
name|QueryStringDecoder
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

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|NettyHttpRequest
specifier|public
class|class
name|NettyHttpRequest
implements|implements
name|HttpRequest
block|{
DECL|field|request
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
name|request
decl_stmt|;
DECL|field|queryStringDecoder
specifier|private
name|QueryStringDecoder
name|queryStringDecoder
decl_stmt|;
DECL|field|utf16Result
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|>
name|utf16Result
init|=
operator|new
name|ThreadLocal
argument_list|<
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|UnicodeUtil
operator|.
name|UTF16Result
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|UnicodeUtil
operator|.
name|UTF16Result
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|method|NettyHttpRequest
specifier|public
name|NettyHttpRequest
parameter_list|(
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
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|queryStringDecoder
operator|=
operator|new
name|QueryStringDecoder
argument_list|(
name|request
operator|.
name|getUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|method
annotation|@
name|Override
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
return|return
name|Method
operator|.
name|GET
return|;
block|}
DECL|method|uri
annotation|@
name|Override
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
DECL|method|hasContent
annotation|@
name|Override
specifier|public
name|boolean
name|hasContent
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|readableBytes
argument_list|()
operator|>
literal|0
return|;
block|}
DECL|method|contentAsString
annotation|@
name|Override
specifier|public
name|String
name|contentAsString
parameter_list|()
block|{
name|UnicodeUtil
operator|.
name|UTF16Result
name|result
init|=
name|utf16Result
operator|.
name|get
argument_list|()
decl_stmt|;
name|ChannelBuffer
name|content
init|=
name|request
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|UTF8toUTF16
argument_list|(
name|content
argument_list|,
name|content
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|content
operator|.
name|readableBytes
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|result
operator|.
name|result
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|headerNames
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|headerNames
parameter_list|()
block|{
return|return
name|request
operator|.
name|getHeaderNames
argument_list|()
return|;
block|}
DECL|method|header
annotation|@
name|Override
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
name|getHeader
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|headers
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|headers
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|request
operator|.
name|getHeaders
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|cookie
annotation|@
name|Override
specifier|public
name|String
name|cookie
parameter_list|()
block|{
return|return
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
return|;
block|}
DECL|method|paramAsFloat
annotation|@
name|Override
specifier|public
name|float
name|paramAsFloat
parameter_list|(
name|String
name|key
parameter_list|,
name|float
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
return|return
name|Float
operator|.
name|parseFloat
argument_list|(
name|sValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Failed to parse float parameter ["
operator|+
name|key
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|paramAsInt
annotation|@
name|Override
specifier|public
name|int
name|paramAsInt
parameter_list|(
name|String
name|key
parameter_list|,
name|int
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|sValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Failed to parse int parameter ["
operator|+
name|key
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|paramAsBoolean
annotation|@
name|Override
specifier|public
name|boolean
name|paramAsBoolean
parameter_list|(
name|String
name|key
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
name|String
name|sValue
init|=
name|param
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|sValue
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
try|try
block|{
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|sValue
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ElasticSearchIllegalArgumentException
argument_list|(
literal|"Failed to parse boolean parameter ["
operator|+
name|key
operator|+
literal|"] with value ["
operator|+
name|sValue
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|param
annotation|@
name|Override
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|keyParams
init|=
name|params
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyParams
operator|==
literal|null
operator|||
name|keyParams
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|keyParams
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
DECL|method|params
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|params
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|queryStringDecoder
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|params
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|queryStringDecoder
operator|.
name|getParameters
argument_list|()
return|;
block|}
comment|// LUCENE TRACK
comment|// The idea here is not to allocate all these byte arrays / char arrays again, just use the channel buffer to convert
comment|// directly into UTF16 from bytes that represent UTF8 ChannelBuffer
DECL|method|UTF8toUTF16
specifier|public
specifier|static
name|void
name|UTF8toUTF16
parameter_list|(
name|ChannelBuffer
name|cb
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|,
specifier|final
name|UnicodeUtil
operator|.
name|UTF16Result
name|result
parameter_list|)
block|{
specifier|final
name|int
name|end
init|=
name|offset
operator|+
name|length
decl_stmt|;
name|char
index|[]
name|out
init|=
name|result
operator|.
name|result
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|offsets
operator|.
name|length
operator|<=
name|end
condition|)
block|{
name|int
index|[]
name|newOffsets
init|=
operator|new
name|int
index|[
literal|2
operator|*
name|end
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|result
operator|.
name|offsets
argument_list|,
literal|0
argument_list|,
name|newOffsets
argument_list|,
literal|0
argument_list|,
name|result
operator|.
name|offsets
operator|.
name|length
argument_list|)
expr_stmt|;
name|result
operator|.
name|offsets
operator|=
name|newOffsets
expr_stmt|;
block|}
specifier|final
name|int
index|[]
name|offsets
init|=
name|result
operator|.
name|offsets
decl_stmt|;
comment|// If incremental decoding fell in the middle of a
comment|// single unicode character, rollback to its start:
name|int
name|upto
init|=
name|offset
decl_stmt|;
while|while
condition|(
name|offsets
index|[
name|upto
index|]
operator|==
operator|-
literal|1
condition|)
name|upto
operator|--
expr_stmt|;
name|int
name|outUpto
init|=
name|offsets
index|[
name|upto
index|]
decl_stmt|;
comment|// Pre-allocate for worst case 1-for-1
if|if
condition|(
name|outUpto
operator|+
name|length
operator|>=
name|out
operator|.
name|length
condition|)
block|{
name|char
index|[]
name|newOut
init|=
operator|new
name|char
index|[
literal|2
operator|*
operator|(
name|outUpto
operator|+
name|length
operator|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|newOut
argument_list|,
literal|0
argument_list|,
name|outUpto
argument_list|)
expr_stmt|;
name|result
operator|.
name|result
operator|=
name|out
operator|=
name|newOut
expr_stmt|;
block|}
while|while
condition|(
name|upto
operator|<
name|end
condition|)
block|{
specifier|final
name|int
name|b
init|=
name|cb
operator|.
name|getByte
argument_list|(
name|upto
argument_list|)
operator|&
literal|0xff
decl_stmt|;
specifier|final
name|int
name|ch
decl_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
name|outUpto
expr_stmt|;
if|if
condition|(
name|b
operator|<
literal|0xc0
condition|)
block|{
assert|assert
name|b
operator|<
literal|0x80
assert|;
name|ch
operator|=
name|b
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|<
literal|0xe0
condition|)
block|{
name|ch
operator|=
operator|(
operator|(
name|b
operator|&
literal|0x1f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|cb
operator|.
name|getByte
argument_list|(
name|upto
argument_list|)
operator|&
literal|0x3f
operator|)
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|<
literal|0xf0
condition|)
block|{
name|ch
operator|=
operator|(
operator|(
name|b
operator|&
literal|0xf
operator|)
operator|<<
literal|12
operator|)
operator|+
operator|(
operator|(
name|cb
operator|.
name|getByte
argument_list|(
name|upto
argument_list|)
operator|&
literal|0x3f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|cb
operator|.
name|getByte
argument_list|(
name|upto
operator|+
literal|1
argument_list|)
operator|&
literal|0x3f
operator|)
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|b
operator|<
literal|0xf8
assert|;
name|ch
operator|=
operator|(
operator|(
name|b
operator|&
literal|0x7
operator|)
operator|<<
literal|18
operator|)
operator|+
operator|(
operator|(
name|cb
operator|.
name|getByte
argument_list|(
name|upto
argument_list|)
operator|&
literal|0x3f
operator|)
operator|<<
literal|12
operator|)
operator|+
operator|(
operator|(
name|cb
operator|.
name|getByte
argument_list|(
name|upto
operator|+
literal|1
argument_list|)
operator|&
literal|0x3f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|cb
operator|.
name|getByte
argument_list|(
name|upto
operator|+
literal|2
argument_list|)
operator|&
literal|0x3f
operator|)
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|offsets
index|[
name|upto
operator|++
index|]
operator|=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|ch
operator|<=
name|UNI_MAX_BMP
condition|)
block|{
comment|// target is a character<= 0xFFFF
name|out
index|[
name|outUpto
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|ch
expr_stmt|;
block|}
else|else
block|{
comment|// target is a character in range 0xFFFF - 0x10FFFF
specifier|final
name|int
name|chHalf
init|=
name|ch
operator|-
name|HALF_BASE
decl_stmt|;
name|out
index|[
name|outUpto
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|chHalf
operator|>>
name|HALF_SHIFT
operator|)
operator|+
name|UnicodeUtil
operator|.
name|UNI_SUR_HIGH_START
argument_list|)
expr_stmt|;
name|out
index|[
name|outUpto
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|chHalf
operator|&
name|HALF_MASK
operator|)
operator|+
name|UnicodeUtil
operator|.
name|UNI_SUR_LOW_START
argument_list|)
expr_stmt|;
block|}
block|}
name|offsets
index|[
name|upto
index|]
operator|=
name|outUpto
expr_stmt|;
name|result
operator|.
name|length
operator|=
name|outUpto
expr_stmt|;
block|}
DECL|field|UNI_MAX_BMP
specifier|private
specifier|static
specifier|final
name|long
name|UNI_MAX_BMP
init|=
literal|0x0000FFFF
decl_stmt|;
DECL|field|HALF_BASE
specifier|private
specifier|static
specifier|final
name|int
name|HALF_BASE
init|=
literal|0x0010000
decl_stmt|;
DECL|field|HALF_SHIFT
specifier|private
specifier|static
specifier|final
name|long
name|HALF_SHIFT
init|=
literal|10
decl_stmt|;
DECL|field|HALF_MASK
specifier|private
specifier|static
specifier|final
name|long
name|HALF_MASK
init|=
literal|0x3FFL
decl_stmt|;
block|}
end_class

end_unit

