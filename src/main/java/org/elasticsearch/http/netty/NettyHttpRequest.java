begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|Charsets
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
name|elasticsearch
operator|.
name|rest
operator|.
name|support
operator|.
name|AbstractRestRequest
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

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|NettyHttpRequest
specifier|public
class|class
name|NettyHttpRequest
extends|extends
name|AbstractRestRequest
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
DECL|field|params
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
decl_stmt|;
DECL|field|rawPath
specifier|private
specifier|final
name|String
name|rawPath
decl_stmt|;
DECL|field|cachedData
specifier|private
name|byte
index|[]
name|cachedData
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
name|params
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|String
name|uri
init|=
name|request
operator|.
name|getUri
argument_list|()
decl_stmt|;
name|int
name|pathEndPos
init|=
name|uri
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathEndPos
operator|<
literal|0
condition|)
block|{
name|this
operator|.
name|rawPath
operator|=
name|uri
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|rawPath
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pathEndPos
argument_list|)
expr_stmt|;
name|RestUtils
operator|.
name|decodeQueryString
argument_list|(
name|uri
argument_list|,
name|pathEndPos
operator|+
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
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
DECL|method|rawPath
specifier|public
name|String
name|rawPath
parameter_list|()
block|{
return|return
name|rawPath
return|;
block|}
annotation|@
name|Override
DECL|method|params
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|params
parameter_list|()
block|{
return|return
name|params
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
annotation|@
name|Override
DECL|method|contentLength
specifier|public
name|int
name|contentLength
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
return|;
block|}
annotation|@
name|Override
DECL|method|contentUnsafe
specifier|public
name|boolean
name|contentUnsafe
parameter_list|()
block|{
comment|// the netty HTTP handling always copy over the buffer to its own buffer, either in NioWorker internally
comment|// when reading, or using a cumalation buffer
return|return
literal|false
return|;
comment|//return request.getContent().hasArray();
block|}
annotation|@
name|Override
DECL|method|contentByteArray
specifier|public
name|byte
index|[]
name|contentByteArray
parameter_list|()
block|{
if|if
condition|(
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|hasArray
argument_list|()
condition|)
block|{
return|return
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|array
argument_list|()
return|;
block|}
if|if
condition|(
name|cachedData
operator|!=
literal|null
condition|)
block|{
return|return
name|cachedData
return|;
block|}
name|cachedData
operator|=
operator|new
name|byte
index|[
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|readableBytes
argument_list|()
index|]
expr_stmt|;
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|getBytes
argument_list|(
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|cachedData
argument_list|)
expr_stmt|;
return|return
name|cachedData
return|;
block|}
annotation|@
name|Override
DECL|method|contentByteArrayOffset
specifier|public
name|int
name|contentByteArrayOffset
parameter_list|()
block|{
if|if
condition|(
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|hasArray
argument_list|()
condition|)
block|{
comment|// get the array offset, and the reader index offset within it
return|return
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|arrayOffset
argument_list|()
operator|+
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|readerIndex
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|contentAsString
specifier|public
name|String
name|contentAsString
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|(
name|Charsets
operator|.
name|UTF_8
argument_list|)
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
name|getHeader
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hasParam
specifier|public
name|boolean
name|hasParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|param
specifier|public
name|String
name|param
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|params
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|defaultValue
return|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

