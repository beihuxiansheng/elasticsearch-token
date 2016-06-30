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
name|org
operator|.
name|elasticsearch
operator|.
name|transport
operator|.
name|netty
operator|.
name|NettyUtils
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
name|buffer
operator|.
name|CompositeChannelBuffer
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
name|ChannelHandlerContext
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
name|HttpResponseEncoder
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

begin_comment
comment|/**  * Wraps a netty {@link HttpResponseEncoder} and makes sure that if the resulting  * channel buffer is composite, it will use the correct gathering flag. See more  * at {@link NettyUtils#DEFAULT_GATHERING}.  */
end_comment

begin_class
DECL|class|ESHttpResponseEncoder
specifier|public
class|class
name|ESHttpResponseEncoder
extends|extends
name|HttpResponseEncoder
block|{
annotation|@
name|Override
DECL|method|encode
specifier|protected
name|Object
name|encode
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Channel
name|channel
parameter_list|,
name|Object
name|msg
parameter_list|)
throws|throws
name|Exception
block|{
name|Object
name|retVal
init|=
name|super
operator|.
name|encode
argument_list|(
name|ctx
argument_list|,
name|channel
argument_list|,
name|msg
argument_list|)
decl_stmt|;
if|if
condition|(
name|retVal
operator|instanceof
name|CompositeChannelBuffer
condition|)
block|{
name|CompositeChannelBuffer
name|ccb
init|=
operator|(
name|CompositeChannelBuffer
operator|)
name|retVal
decl_stmt|;
if|if
condition|(
name|ccb
operator|.
name|useGathering
argument_list|()
operator|!=
name|NettyUtils
operator|.
name|DEFAULT_GATHERING
condition|)
block|{
name|List
argument_list|<
name|ChannelBuffer
argument_list|>
name|decompose
init|=
name|ccb
operator|.
name|decompose
argument_list|(
name|ccb
operator|.
name|readerIndex
argument_list|()
argument_list|,
name|ccb
operator|.
name|readableBytes
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ChannelBuffers
operator|.
name|wrappedBuffer
argument_list|(
name|NettyUtils
operator|.
name|DEFAULT_GATHERING
argument_list|,
name|decompose
operator|.
name|toArray
argument_list|(
operator|new
name|ChannelBuffer
index|[
name|decompose
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|retVal
return|;
block|}
block|}
end_class

end_unit

