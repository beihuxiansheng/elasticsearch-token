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
name|http
operator|.
name|netty
operator|.
name|pipelining
operator|.
name|OrderedUpstreamMessageEvent
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
name|*
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
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
annotation|@
name|ChannelHandler
operator|.
name|Sharable
DECL|class|HttpRequestHandler
specifier|public
class|class
name|HttpRequestHandler
extends|extends
name|SimpleChannelUpstreamHandler
block|{
DECL|field|serverTransport
specifier|private
specifier|final
name|NettyHttpServerTransport
name|serverTransport
decl_stmt|;
DECL|field|corsPattern
specifier|private
specifier|final
name|Pattern
name|corsPattern
decl_stmt|;
DECL|field|httpPipeliningEnabled
specifier|private
specifier|final
name|boolean
name|httpPipeliningEnabled
decl_stmt|;
DECL|field|detailedErrorsEnabled
specifier|private
specifier|final
name|boolean
name|detailedErrorsEnabled
decl_stmt|;
DECL|method|HttpRequestHandler
specifier|public
name|HttpRequestHandler
parameter_list|(
name|NettyHttpServerTransport
name|serverTransport
parameter_list|,
name|boolean
name|detailedErrorsEnabled
parameter_list|)
block|{
name|this
operator|.
name|serverTransport
operator|=
name|serverTransport
expr_stmt|;
name|this
operator|.
name|corsPattern
operator|=
name|RestUtils
operator|.
name|getCorsSettingRegex
argument_list|(
name|serverTransport
operator|.
name|settings
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|httpPipeliningEnabled
operator|=
name|serverTransport
operator|.
name|pipelining
expr_stmt|;
name|this
operator|.
name|detailedErrorsEnabled
operator|=
name|detailedErrorsEnabled
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|messageReceived
specifier|public
name|void
name|messageReceived
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|MessageEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
name|HttpRequest
name|request
decl_stmt|;
name|OrderedUpstreamMessageEvent
name|oue
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|httpPipeliningEnabled
operator|&&
name|e
operator|instanceof
name|OrderedUpstreamMessageEvent
condition|)
block|{
name|oue
operator|=
operator|(
name|OrderedUpstreamMessageEvent
operator|)
name|e
expr_stmt|;
name|request
operator|=
operator|(
name|HttpRequest
operator|)
name|oue
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|request
operator|=
operator|(
name|HttpRequest
operator|)
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
comment|// the netty HTTP handling always copy over the buffer to its own buffer, either in NioWorker internally
comment|// when reading, or using a cumalation buffer
name|NettyHttpRequest
name|httpRequest
init|=
operator|new
name|NettyHttpRequest
argument_list|(
name|request
argument_list|,
name|e
operator|.
name|getChannel
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|oue
operator|!=
literal|null
condition|)
block|{
name|serverTransport
operator|.
name|dispatchRequest
argument_list|(
name|httpRequest
argument_list|,
operator|new
name|NettyHttpChannel
argument_list|(
name|serverTransport
argument_list|,
name|httpRequest
argument_list|,
name|corsPattern
argument_list|,
name|oue
argument_list|,
name|detailedErrorsEnabled
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|serverTransport
operator|.
name|dispatchRequest
argument_list|(
name|httpRequest
argument_list|,
operator|new
name|NettyHttpChannel
argument_list|(
name|serverTransport
argument_list|,
name|httpRequest
argument_list|,
name|corsPattern
argument_list|,
name|detailedErrorsEnabled
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|messageReceived
argument_list|(
name|ctx
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|exceptionCaught
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|ExceptionEvent
name|e
parameter_list|)
throws|throws
name|Exception
block|{
name|serverTransport
operator|.
name|exceptionCaught
argument_list|(
name|ctx
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

