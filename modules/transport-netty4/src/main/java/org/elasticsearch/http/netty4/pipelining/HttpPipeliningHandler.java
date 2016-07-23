begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.http.netty4.pipelining
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|http
operator|.
name|netty4
operator|.
name|pipelining
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
name|ChannelHandlerContext
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
name|ChannelPromise
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
name|LastHttpContent
import|;
end_import

begin_import
import|import
name|io
operator|.
name|netty
operator|.
name|util
operator|.
name|ReferenceCountUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|termvectors
operator|.
name|TermVectorsFilter
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
name|SuppressForbidden
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
name|netty4
operator|.
name|Netty4Utils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_comment
comment|/**  * Implements HTTP pipelining ordering, ensuring that responses are completely served in the same order as their  * corresponding requests. NOTE: A side effect of using this handler is that upstream HttpRequest objects will  * cause the original message event to be effectively transformed into an OrderedUpstreamMessageEvent. Conversely  * OrderedDownstreamChannelEvent objects are expected to be received for the correlating response objects.  */
end_comment

begin_class
DECL|class|HttpPipeliningHandler
specifier|public
class|class
name|HttpPipeliningHandler
extends|extends
name|ChannelDuplexHandler
block|{
DECL|field|INITIAL_EVENTS_HELD
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_EVENTS_HELD
init|=
literal|3
decl_stmt|;
DECL|field|maxEventsHeld
specifier|private
specifier|final
name|int
name|maxEventsHeld
decl_stmt|;
DECL|field|readSequence
specifier|private
name|int
name|readSequence
decl_stmt|;
DECL|field|writeSequence
specifier|private
name|int
name|writeSequence
decl_stmt|;
DECL|field|holdingQueue
specifier|private
specifier|final
name|Queue
argument_list|<
name|HttpPipelinedResponse
argument_list|>
name|holdingQueue
decl_stmt|;
comment|/**      * @param maxEventsHeld the maximum number of channel events that will be retained prior to aborting the channel      *                      connection. This is required as events cannot queue up indefinitely; we would run out of      *                      memory if this was the case.      */
DECL|method|HttpPipeliningHandler
specifier|public
name|HttpPipeliningHandler
parameter_list|(
specifier|final
name|int
name|maxEventsHeld
parameter_list|)
block|{
name|this
operator|.
name|maxEventsHeld
operator|=
name|maxEventsHeld
expr_stmt|;
name|this
operator|.
name|holdingQueue
operator|=
operator|new
name|PriorityQueue
argument_list|<>
argument_list|(
name|INITIAL_EVENTS_HELD
argument_list|)
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
name|msg
operator|instanceof
name|LastHttpContent
condition|)
block|{
name|ctx
operator|.
name|fireChannelRead
argument_list|(
operator|new
name|HttpPipelinedRequest
argument_list|(
operator|(
operator|(
name|LastHttpContent
operator|)
name|msg
operator|)
operator|.
name|retain
argument_list|()
argument_list|,
name|readSequence
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ctx
operator|.
name|fireChannelRead
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|ChannelHandlerContext
name|ctx
parameter_list|,
name|Object
name|msg
parameter_list|,
name|ChannelPromise
name|promise
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|msg
operator|instanceof
name|HttpPipelinedResponse
condition|)
block|{
name|boolean
name|channelShouldClose
init|=
literal|false
decl_stmt|;
synchronized|synchronized
init|(
name|holdingQueue
init|)
block|{
if|if
condition|(
name|holdingQueue
operator|.
name|size
argument_list|()
operator|<
name|maxEventsHeld
condition|)
block|{
name|holdingQueue
operator|.
name|add
argument_list|(
operator|(
name|HttpPipelinedResponse
operator|)
name|msg
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|holdingQueue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|HttpPipelinedResponse
name|response
init|=
name|holdingQueue
operator|.
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|sequence
argument_list|()
operator|!=
name|writeSequence
condition|)
block|{
break|break;
block|}
name|holdingQueue
operator|.
name|remove
argument_list|()
expr_stmt|;
name|ctx
operator|.
name|write
argument_list|(
name|response
operator|.
name|response
argument_list|()
argument_list|,
name|response
operator|.
name|promise
argument_list|()
argument_list|)
expr_stmt|;
name|writeSequence
operator|++
expr_stmt|;
block|}
block|}
else|else
block|{
name|channelShouldClose
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|channelShouldClose
condition|)
block|{
try|try
block|{
name|Netty4Utils
operator|.
name|closeChannels
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|ctx
operator|.
name|channel
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
operator|(
operator|(
name|HttpPipelinedResponse
operator|)
name|msg
operator|)
operator|.
name|release
argument_list|()
expr_stmt|;
name|promise
operator|.
name|setSuccess
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|ctx
operator|.
name|write
argument_list|(
name|msg
argument_list|,
name|promise
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

